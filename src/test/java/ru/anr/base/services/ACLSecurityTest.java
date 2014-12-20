/**
 * 
 */
package ru.anr.base.services;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ru.anr.base.samples.domain.Samples;
import ru.anr.base.samples.services.ACLSecured;

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
    @Qualifier("aclService")
    private MutableAclService acls;

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
     * Cheching how authorization works
     */
    @Test
    @Ignore("does not work yet")
    public void testACL() {

        /*
         * Setting a security token, but it's not authenticated
         */
        Authentication token = authenticate("user");

        Samples s = create(Samples.class, "name", "xxx");
        MutableAcl acl = acls.createAcl(new ObjectIdentityImpl(Samples.class, s.getId()));
        acl.insertAce(0, BasePermission.READ, new PrincipalSid(token), true);

        acls.updateAcl(acl);

        try {

            service.apply(s);

            Assert.fail();

        } catch (AccessDeniedException ex) {

            Assert.assertEquals("Access is denied", ex.getMessage());
        }

        acl.insertAce(1, BasePermission.WRITE, new PrincipalSid(token), true);

        acls.updateAcl(acl);
        service.apply(s);

    }
}
