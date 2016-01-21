/**
 * 
 */
package ru.anr.base.services.validation;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.anr.base.ApplicationException;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.samples.services.validation.SampleService;
import ru.anr.base.services.BaseLocalServiceTestCase;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Jan 21, 2016
 *
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
            Assert.fail();
        } catch (ApplicationException ex) {
            Assert.assertEquals("Error", ex.getMessage());
        }
    }
}
