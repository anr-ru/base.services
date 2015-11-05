/**
 * 
 */
package ru.anr.base.services;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

import ru.anr.base.tests.BaseTestCase;

/**
 * This test shows that environment variables are preferable by default.
 *
 *
 * @author Alexey Romanchuk
 * @created Jan 30, 2015
 *
 */
@ContextConfiguration(locations = "classpath:/tests-properties-context.xml", inheritLocations = false)
public class EnvironmentPropertiesTest extends BaseTestCase {

    /**
     * The value
     */
    @Value("${USER}")
    private String value;

    /**
     * The property is read from the environment
     */
    @Test
    public void testPropertiesOverriding() {

        Assert.assertEquals(System.getenv("USER"), value);
    }
}
