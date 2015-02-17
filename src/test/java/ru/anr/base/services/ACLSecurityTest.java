/**
 * 
 */
package ru.anr.base.services;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ru.anr.base.samples.domain.Samples;
import ru.anr.base.samples.services.ACLSecured;
import ru.anr.base.services.security.ACLManager;

/**
 * Tests for ACL Security.
 *
 *
 * @author Alexey Romanchuk
 * @created Dec 1, 2014
 *
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
     * @param user
     *            A user
     * @return Authenticated token
     */
    private Authentication authenticate(String user) {

        Authentication token =
                authenticationManager.authenticate(new TestingAuthenticationToken(user, "password", "ROLE_USER"));
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
            Assert.fail();
        } catch (AccessDeniedException ex) {
            Assert.assertEquals("Access is denied", ex.getMessage());
        }

        acls.grant(s, new PrincipalSid("user"), BasePermission.READ);

        service.getObject(s.getId());
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

            Assert.fail();

        } catch (AccessDeniedException ex) {

            Assert.assertEquals("Access is denied", ex.getMessage());
        }

        // Now adding just an ACL access

        acls.grant(s, new PrincipalSid("user"), BasePermission.WRITE);

        service.apply(s); // good !

        s = create(Samples.class, "name", "write"); // access_write

        service.apply(s); // Ok

        s.setName("read");

        try {
            service.apply(s);
            Assert.fail();

        } catch (AccessDeniedException ex) {
            Assert.assertEquals("Access is denied", ex.getMessage());
        }
    }

    /**
     * Cheching how authorization works for Role ACL
     */
    @Test
    public void testACLforRole() {

        /*
         * Default user
         */
        Authentication token = authenticate("user1");
        Assert.assertTrue(token.isAuthenticated());

        Samples s = create(Samples.class, "name", null);

        /*
         * Permissions are granter for ROLE, not for a concrete user
         */
        acls.grant(s, new GrantedAuthoritySid("ROLE_USER"), BasePermission.READ);
        acls.grant(s, new GrantedAuthoritySid("ROLE_USER"), BasePermission.WRITE);

        service.getObject(s.getId());
        service.apply(s);

        token = authenticate("user2");
        Assert.assertTrue(token.isAuthenticated());

        service.getObject(s.getId());
        service.apply(s);
    }
}
