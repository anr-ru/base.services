package ru.anr.base.services.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.anr.base.ApplicationException;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.samples.services.validation.SampleService;
import ru.anr.base.services.BaseLocalServiceTestCase;
import ru.anr.base.tests.multithreading.ThreadExecutor;
import ru.anr.base.tests.multithreading.ThreadJob;

import java.util.LinkedHashMap;

/**
 * Basic validation tests.
 *
 * @author Alexey Romanchuk
 * @created Jan 21, 2016
 */

public class ValidationTest extends BaseLocalServiceTestCase {
    /**
     * A service
     */
    @Autowired
    private SampleService service;

    /**
     * Checking the validation works and the first by order validator is applied
     */
    @Test
    public void test() {

        Samples o = new Samples();
        service.doValidate(o); // all ok

        o.setName("Here is Error");
        try {
            service.doValidate(o);
            Assertions.fail();
        } catch (ApplicationException ex) {
            Assertions.assertEquals("Error", ex.getMessage());
        }
    }

    @Autowired
    private ValidationFactory factory;

    /**
     * Use case: check multi-threading access to validators
     */
    @Test
    public void testMultiThreadAccessToValidators() {

        ThreadExecutor exec = new ThreadExecutor();
        for (int i = 1; i <= 20; i++) {
            exec.add(new ThreadJob(x -> {
                logger.debug("Found {} validators", factory.getValidators(Samples.class).size());
            }));
        }

        exec.start();
        Assertions.assertTrue(exec.waitNotError());
    }
}
