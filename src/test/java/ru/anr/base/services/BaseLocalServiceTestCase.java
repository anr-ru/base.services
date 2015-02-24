/**
 * 
 */
package ru.anr.base.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
@ContextConfiguration(locations = "classpath:/tests-service-context.xml", inheritLocations = false)
@Ignore
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

        Assert.assertTrue(r.isAuthenticated());
        SecurityContextHolder.getContext().setAuthentication(r);

        return r;
    }

    /**
     * {@inheritDoc}
     */
    @Before
    @Override
    public void setUp() {

        super.setUp();

        SecurityContextHolder.clearContext();
    }
}
