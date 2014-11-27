/**
 * 
 */
package ru.anr.base.services;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import ru.anr.base.tests.BaseTestCase;

/**
 * Testing for {@link org.springframework.core.env.Environment} access to get
 * profile info in runtime.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2014
 *
 */
@ActiveProfiles("production")
@ContextConfiguration(classes = { BaseServiceImplProductionTest.class, MessagePropertiesConfig.class })
public class BaseServiceImplProductionTest extends BaseTestCase {

    /**
     * Configuring a bean
     * 
     * @return Bean instance
     */
    @Bean(name = "beanProd")
    public BaseServiceImpl instance() {

        return new BaseServiceImpl();
    }

    /**
     * Test method for {@link ru.anr.base.services.BaseServiceImpl#isProdMode()}
     */
    @Test
    public void testIsProdMode() {

        BaseServiceImpl s1 = bean("beanProd", BaseServiceImpl.class);
        Assert.assertTrue(s1.isProdMode());
    }
}
