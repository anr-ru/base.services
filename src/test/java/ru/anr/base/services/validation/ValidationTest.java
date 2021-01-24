package ru.anr.base.services.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.anr.base.ApplicationException;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.samples.services.validation.SampleService;
import ru.anr.base.services.BaseLocalServiceTestCase;

/**
 * Description ...
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
}
