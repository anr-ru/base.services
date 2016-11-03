/**
 * 
 */
package ru.anr.base.services.properties;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

import ru.anr.base.services.BaseService;
import ru.anr.base.tests.BaseTestCase;

/**
 * QA Profiles
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2016
 *
 */
@ContextConfiguration(locations = "classpath:tests-properties-context.xml", inheritLocations = false)
public class DefaultProfiledPropertiesTest extends BaseTestCase {

    /**
     * The value to check
     */
    @Value("${value.x}")
    private String value;

    /**
     * {@link BaseService}
     */
    @Autowired
    private BaseService service;

    /**
     * Use case: checking the values are equals
     */
    @Test
    public void testCheckValue() {

        Assert.assertEquals("default-value", value);

        Assert.assertNull(service.getTargetEnv());
    }
}
