package ru.anr.base.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import ru.anr.base.dao.repository.SecuredRepository;
import ru.anr.base.samples.dao.MyDao;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.samples.services.ACLSecured;
import ru.anr.base.services.security.ACLManager;

import java.util.List;

/**
 * Tests for ACL Security.
 *
 * @author Alexey Romanchuk
 * @created Dec 1, 2014
 */
public class ACLSecurityTest extends BaseLocalServiceTestCase {

    /**
     * This bean has no security settings
     */
    @Autowired
    @Qualifier("AclSecured")
    private ACLSecured service;

    /**
     * Default acl service
     */
    @Autowired
    private ACLManager acls;

    /**
     * Our test authentication manager
     */
    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    /**
     * Authentication
     *
     * @param user A user
     * @return Authenticated token
     */
    private Authentication authenticate(String user) {

        Authentication token = authenticationManager.authenticate(new TestingAuthenticationToken(user,
                "password", "ROLE_USER"));

        SecurityContextHolder.getContext().setAuthentication(token);
        return token;
    }

    /**
     * Pattern: hasPermission(.., 'read') AND hasPermission(..,'access_read').
     * The second one requires domain-level check which depends on value of the
     * name attribute.
     */
    @Test
    public void testACLAndAccess() {

        Sid user = new PrincipalSid("user");
        Sid test = new PrincipalSid("test");

        authenticate("user");
        Samples s = create(Samples.class, "name", "read");

        assertException(args -> service.getObject(s.getId()), "Access Denied");

        acls.grant(s, user, BasePermission.READ);
        Assertions.assertTrue(acls.isGranted(s, user, list(BasePermission.READ)));
        Assertions.assertEquals(s, service.getObject(s.getId()));

        acls.grant(s, test, BasePermission.READ);
        Assertions.assertTrue(acls.isGranted(s, test, list(BasePermission.READ)));
        authenticate("test");

        Assertions.assertEquals(s, service.getObject(s.getId()));
        assertException(args -> acls.revoke(s, test), "No permissions to delete ACLs");

        MultiValueMap<Sid, Permission> map = acls.getPermissions(s);
        Assertions.assertEquals(2, map.size());
        Assertions.assertEquals(list(BasePermission.READ), map.get(user));
        Assertions.assertEquals(list(BasePermission.READ), map.get(test));

        authenticate("user");
        acls.revoke(s, test);
        Assertions.assertFalse(acls.isGranted(s, test, list(BasePermission.READ)));

        authenticate("test");
        assertException(args -> service.getObject(s.getId()), "Access Denied");

        authenticate("user");
        Assertions.assertEquals(s, service.getObject(s.getId()));

        map = acls.getPermissions(s);
        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals(list(BasePermission.READ), map.get(user));

        acls.revoke(s, user);
        assertException(args -> service.getObject(s.getId()), "Access Denied");

        Assertions.assertFalse(acls.isGranted(s, test, list(BasePermission.READ)));

        map = acls.getPermissions(s);
        Assertions.assertEquals(0, map.size());
    }

    /**
     * Pattern: hasPermission(.., 'write') OR hasPermission(..,'access_write').
     * The second one requires domain-level check which depends on value of the
     * name attribute.
     */
    @Test
    public void testACLOrScenario() {

        authenticate("user");
        Samples s = create(Samples.class, "name", "read"); // access_read

        try {
            service.apply(s);
            Assertions.fail();
        } catch (AccessDeniedException ex) {
            Assertions.assertEquals("Access Denied", ex.getMessage());
        }

        // Now adding just an ACL access
        acls.grant(s, new PrincipalSid("user"), BasePermission.WRITE);
        service.apply(s); // good !

        // And set per object type access
        s = create(Samples.class, "name", "write"); // access_write
        service.apply(s); // Ok

        s.setName("read");
        try {
            service.apply(s);
            Assertions.fail();

        } catch (AccessDeniedException ex) {
            Assertions.assertEquals("Access Denied", ex.getMessage());
        }
    }

    /**
     * Checking how authorization works for Role ACL
     */
    @Test
    public void testACLforRole() {

        /*
         * Default user
         */
        Authentication token = authenticate("user1");
        Assertions.assertTrue(token.isAuthenticated());

        Samples s = create(Samples.class, "name", null);

        /*
         * Permissions are granter for ROLE, not for a concrete user
         */
        acls.grant(s, new GrantedAuthoritySid("ROLE_USER"), BasePermission.READ);
        acls.grant(s, new GrantedAuthoritySid("ROLE_USER"), BasePermission.WRITE);

        service.getObject(s.getId());
        service.apply(s);

        token = authenticate("user2");
        Assertions.assertTrue(token.isAuthenticated());

        service.getObject(s.getId());
        service.apply(s);

        MultiValueMap<Sid, Permission> map = acls.getPermissions(s);
        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals(list(BasePermission.READ, BasePermission.WRITE),
                map.get(new GrantedAuthoritySid("ROLE_USER")));
    }

    /**
     * Passing 'null' value
     */
    @Test
    public void testNullObjects() {

        authenticate("user");

        service.getObject(900L);
        service.apply(null);

        Assertions.assertTrue(true);
    }

    /**
     * The test DAO
     */
    @Autowired
    private MyDao myDao;

    @Autowired
    private SecuredRepository securedDao;

    /**
     * Filtering the pages
     */
    @Test
    public void testPageFilteringByAccessible() {

        authenticate("user");

        Samples s1 = create(Samples.class, "name", "read");
        Samples s2 = create(Samples.class, "name", "write");

        Page<Samples> p = myDao.pages(PageRequest.of(0, 10));
        Assertions.assertEquals(2, p.getContent().size());
        Assertions.assertTrue(p.getContent().containsAll(list(s1, s2)));

        List<Samples> rs = securedDao.filterSecured(p);
        Assertions.assertEquals(1, rs.size());
        Assertions.assertTrue(rs.contains(s1));
        Assertions.assertFalse(rs.contains(s2));

        // Access granted via ACL
        acls.grant(s2, new PrincipalSid("user"), BasePermission.READ);

        rs = securedDao.filterSecured(p);
        Assertions.assertEquals(2, rs.size());
        Assertions.assertTrue(rs.containsAll(list(s1, s2)));
    }

    /**
     * A use case: filtering lists
     */
    @Test
    public void testListFilteredByAccessible() {

        authenticate("user");

        Samples s1 = create(Samples.class, "name", "read");
        Samples s2 = create(Samples.class, "name", "write");

        List<Samples> rs = securedDao.filterSecured(list(s1, s2));
        Assertions.assertEquals(1, rs.size());
        Assertions.assertTrue(rs.contains(s1));
        Assertions.assertFalse(rs.contains(s2));

        // Access granted via ACL
        acls.grant(s2, new PrincipalSid("user"), BasePermission.READ);

        rs = securedDao.filterSecured(list(s1, s2));
        Assertions.assertEquals(2, rs.size());
        Assertions.assertTrue(rs.containsAll(list(s1, s2)));
    }


    @Test
    public void testAccessToEntity() {

        authenticate("user");

        Samples s = create(Samples.class, "name", "none");

        dao.find(Samples.class, s.getId()); // No security required

        try {
            securedDao.findSecured(Samples.class, s.getId());
            Assertions.fail();
        } catch (AccessDeniedException ex) {
            Assertions.assertEquals("Access Denied", ex.getMessage());
        }

        s.setName("read");

        Samples sx = securedDao.findSecured(Samples.class, s.getId());
        Assertions.assertEquals(s, sx);

        Samples sxx = dao.find(Samples.class, s.getId());
        Assertions.assertEquals(sx, sxx);
    }
}
