/**
 * 
 */
package ru.anr.base.services;

import java.util.Locale;

import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ContextConfiguration;

import ru.anr.base.dao.AbstractDaoTestCase;

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
    @Before
    public void setUp() {

        LocaleContextHolder.setLocale(Locale.US);
    }
}
