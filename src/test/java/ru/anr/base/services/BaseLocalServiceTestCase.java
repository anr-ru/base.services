/**
 *
 */
package ru.anr.base.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import ru.anr.base.tests.BaseLocalDaoTestCase;

/**
 * A parent class for all service layer tests.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */
@ContextConfiguration(value = "classpath:/tests-service-context.xml", inheritLocations = false)
@Disabled
public class BaseLocalServiceTestCase extends BaseLocalDaoTestCase {

    /**
     * Ref to authentication manager
     */
    @Autowired
    protected AuthenticationManager authenticationManager;

    /**
     * Performs authentication and when it's successfull - sets security context
     *
     * @param token
     *            A token for authentication
     * @return Authrorised user
     */
    protected Authentication authenticate(Authentication token) {

        Authentication r = authenticationManager.authenticate(token);

        Assertions.assertTrue(r.isAuthenticated());
        SecurityContextHolder.getContext().setAuthentication(r);

        return r;
    }

    /**
     * {@inheritDoc}
     */
    @BeforeEach
    @Override
    public void setUp() {

        super.setUp();
        SecurityContextHolder.clearContext();
    }
}
