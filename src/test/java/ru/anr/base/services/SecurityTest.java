package ru.anr.base.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tests of security settings.
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 */

public class SecurityTest extends BaseLocalServiceTestCase {

    /**
     * This bean has no security settings
     */
    @Autowired
    @Qualifier("base")
    private BaseService base;

    /**
     * This one has some security settings with IS_AUTHENTICATED_FULLY
     */
    @Autowired
    @Qualifier("superSecured")
    private BaseService fully;

    /**
     * This one has some security settings with ROLE_USER
     */
    @Autowired
    @Qualifier("roleSecured")
    private BaseService roled;

    /**
     * Our test authentication manager
     */
    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    /**
     * Cheching how authorization works
     */
    @Test
    public void testAuthorization() {

        /*
         * Invocation of unsecured method - nothing happens
         */
        base.text("hello.world");

        /*
         * Now, let's invoke a secured method - first of all we need
         * authentication to be set in a security context
         */
        try {
            fully.text("hello.world");
            Assertions.fail();
        } catch (AuthenticationCredentialsNotFoundException ex) {
            Assertions.assertEquals("An Authentication object was not found in the SecurityContext", ex.getMessage());
        }

        /*
         * Setting a security token, but it's not authenticated
         */
        Authentication token = authenticationManager.authenticate(new TestingAuthenticationToken("xxx", "yyy"));
        SecurityContextHolder.getContext().setAuthentication(token);

        fully.text("hello.world"); // this is already OK
        try {
            roled.text("hello.world"); // but this is not
            Assertions.fail();
        } catch (AccessDeniedException ex) {
            Assertions.assertEquals("Access Denied", ex.getMessage());
        }

        /*
         * Now the token is authenticated and has some role.
         */
        GrantedAuthority role = new SimpleGrantedAuthority("ROLE_XXX");

        token = authenticationManager.authenticate(new TestingAuthenticationToken("xxx", "yyy", list(role)));
        SecurityContextHolder.getContext().setAuthentication(token);

        fully.text("hello.world"); // this is OK
        try {
            roled.text("hello.world"); // But this is not
            Assertions.fail();
        } catch (AccessDeniedException ex) {
            Assertions.assertEquals("Access Denied", ex.getMessage());
        }

        /*
         * Finally, the role is exactly the same
         */
        role = new SimpleGrantedAuthority("ROLE_USER");

        token = authenticationManager.authenticate(new TestingAuthenticationToken("xxx", "yyy", list(role)));
        SecurityContextHolder.getContext().setAuthentication(token);

        Assertions.assertNotNull(roled.text("hello.world")); // Both are OK
        Assertions.assertNotNull(fully.text("hello.world"));
    }
}
