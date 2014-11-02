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
 * Test for checking Spring {@link org.springframework.core.env.Environment} to
 * work.
 *
 *
 * @author Alexey Romanchuk
 * @created 03 нояб. 2014 г.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("tests")
@ContextConfiguration(classes = BaseServiceImplTest.class)
public class BaseServiceImplTest {

    /**
     * Getting a bean instance
     * 
     * @return Bean instance
     */
    @Bean(name = "beanDev")
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

        BaseServiceImpl s1 = ctx.getBean("beanDev", BaseServiceImpl.class);
        Assert.assertFalse(s1.isProdMode());
    }
}
