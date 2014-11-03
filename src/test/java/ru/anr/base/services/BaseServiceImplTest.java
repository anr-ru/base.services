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
 * Test for checking Spring {@link org.springframework.core.env.Environment} to
 * work.
 *
 *
 * @author Alexey Romanchuk
 * @created 03 нояб. 2014 г.
 *
 */
@ActiveProfiles("tests")
@ContextConfiguration(classes = BaseServiceImplTest.class)
public class BaseServiceImplTest extends BaseTestCase {

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
     * Test method for {@link ru.anr.base.services.BaseServiceImpl#isProdMode()}
     */
    @Test
    public void testIsProdMode() {

        BaseServiceImpl s1 = bean("beanDev", BaseServiceImpl.class);
        Assert.assertFalse(s1.isProdMode());
    }
}
