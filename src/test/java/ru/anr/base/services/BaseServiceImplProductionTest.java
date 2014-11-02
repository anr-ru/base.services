/**
 * 
 */
package ru.anr.base.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Testing for {@link org.springframework.core.env.Environment} access to get
 * profile info in runtime.
 *
 *
 * @author Alexey Romanchuk
 * @created 03 нояб. 2014 г.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("production")
@ContextConfiguration(classes = BaseServiceImplProductionTest.class)
public class BaseServiceImplProductionTest {

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
     * A ref to context
     */
    @Autowired
    private ApplicationContext ctx;

    /**
     * Test method for {@link ru.anr.base.services.BaseServiceImpl#isProdMode()}
     */
    @Test
    public void testIsProdMode() {

        BaseServiceImpl s1 = ctx.getBean("beanProd", BaseServiceImpl.class);
        Assert.assertTrue(s1.isProdMode());
    }
}
