package ru.anr.base.services.properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.anr.base.services.BaseService;
import ru.anr.base.services.TargetEnvironments;
import ru.anr.base.tests.BaseTestCase;

/**
 * CI Profiles
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2016
 */
@ContextConfiguration(locations = "classpath:tests-properties-context.xml", inheritLocations = false)
@ActiveProfiles(profiles = {"CI", "production"})
public class CIProfiledPropertiesTest extends BaseTestCase {

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

        Assertions.assertEquals("ci-value", value);
        Assertions.assertSame(TargetEnvironments.CI, service.getTargetEnv());
    }
}
