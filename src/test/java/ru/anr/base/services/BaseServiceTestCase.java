package ru.anr.base.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ContextConfiguration;
import ru.anr.base.dao.AbstractDaoTestCase;

import java.util.Locale;

/**
 * A parent class for all service layer tests.
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 */
@ContextConfiguration(locations = "classpath:/tests-service-context.xml", inheritLocations = false)
@Disabled
public class BaseServiceTestCase extends AbstractDaoTestCase {

    /**
     * The bean under test
     */
    @Autowired
    @Qualifier("base")
    protected BaseService base;

    /**
     * Default test settings
     */
    @BeforeEach
    public void setUp() {

        LocaleContextHolder.setLocale(Locale.US);
    }
}
