package ru.anr.base.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import ru.anr.base.tests.BaseTestCase;

/**
 * This test shows how to avoid usage of environment variables.
 *
 * @author Alexey Romanchuk
 * @created Jan 30, 2015
 */
@ContextConfiguration(locations = "classpath:/tests-localproperties-context.xml", inheritLocations = false)
public class LocalPropertiesTest extends BaseTestCase {

    /**
     * The value
     */
    @Value("${USER}")
    private String value;

    /**
     * The property is read from the file
     */
    @Test
    public void testPropertiesOverriding() {

        Assertions.assertEquals("nemo", value);
    }

    /**
     * The value 1
     */
    @Value("${value1}")
    private String value1;

    /**
     * The value 2
     */
    @Value("${value2}")
    private String value2;

    /**
     * The property is substituted from another
     */
    @Test
    public void testPropertySubstitution() {

        Assertions.assertEquals("12345", value1);
        Assertions.assertEquals("12345", value2);
    }

}
