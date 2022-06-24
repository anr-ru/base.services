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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        authenticate("user");

        Samples s = create(Samples.class, "name", "read");

        try {
            service.getObject(s.getId());
            Assertions.fail();
        } catch (AccessDeniedException ex) {
            Assertions.assertEquals("Access is denied", ex.getMessage());
        }

        acls.grant(s, new PrincipalSid("user"), BasePermission.READ);

        Assertions.assertEquals(s, service.getObject(s.getId()));
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
            Assertions.assertEquals("Access is denied", ex.getMessage());
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
            Assertions.assertEquals("Access is denied", ex.getMessage());
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

        List<Samples> rs = myDao.filter(p);
        Assertions.assertEquals(1, rs.size());
        Assertions.assertTrue(rs.contains(s1));
        Assertions.assertFalse(rs.contains(s2));

        // Access granted via ACL
        acls.grant(s2, new PrincipalSid("user"), BasePermission.READ);

        rs = myDao.filter(p);
        Assertions.assertEquals(2, rs.size());
        Assertions.assertTrue(rs.containsAll(list(s1, s2)));
    }

    @Test
    public void testAccessToEntity() {

        authenticate("user");

        Samples s = create(Samples.class, "name", "none");

        dao.find(Samples.class, s.getId()); // No security required

        try {
            dao.findSecured(Samples.class, s.getId());
            Assertions.fail();
        } catch (AccessDeniedException ex) {
            Assertions.assertEquals("Access is denied", ex.getMessage());
        }

        s.setName("read");

        Samples sx = dao.findSecured(Samples.class, s.getId());
        Assertions.assertEquals(s, sx);

        Samples sxx = dao.find(Samples.class, s.getId());
        Assertions.assertEquals(sx, sxx);
    }
}
