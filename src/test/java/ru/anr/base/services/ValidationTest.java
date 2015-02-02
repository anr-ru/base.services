/**
 * 
 */
package ru.anr.base.services;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ru.anr.base.ApplicationException;
import ru.anr.base.samples.domain.SomeValidated;
import ru.anr.base.samples.services.ValidatedMethods;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Jan 30, 2015
 *
 */

public class ValidationTest extends BaseLocalServiceTestCase {

    /**
     * Default validator injection
     */
    @Autowired
    private Validator validator;

    /**
     * Validatino
     */
    @Test
    public void testValidation() {

        SomeValidated v = new SomeValidated();
        v.setDigit(4);

        Set<ConstraintViolation<SomeValidated>> errors = validator.validate(v);
        Assert.assertEquals(2, errors.size());

        /*
         * Check values are read from message source properly
         */
        errors.forEach((c) -> { // to see what's going
            logger.debug("Message          : {}", c.getMessage());
        });

        Iterator<ConstraintViolation<SomeValidated>> i = errors.iterator();
        ConstraintViolation<SomeValidated> c = i.next();

        do {
            logger.debug("Checking : {}", c.getPropertyPath());

            if (safeEquals("digit", c.getPropertyPath().toString())) {
                Assert.assertEquals("The value 4 must be greater then 5", c.getMessage());
            } else {
                Assert.assertEquals("The value is expected to be NOT NULL", c.getMessage());
            }
            c = i.next();

        } while (i.hasNext());
    }

    /**
     * Service for testing
     */
    @Autowired
    @Qualifier("validatedMethods")
    private ValidatedMethods service;

    /**
     * Method validation
     */
    @Test
    public void methodValidation() {

        // 1. Constraint on argument

        try {

            service.method1(null);
            Assert.fail();

        } catch (ConstraintViolationException ex) {

            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Assert.assertEquals("The value is expected to be NOT NULL", ex.getConstraintViolations().iterator().next()
                    .getMessage());

            service.method1("x");
        }

        // 2. Constraint on a returned value

        try {
            service.method2(null);
            Assert.fail();
        } catch (ConstraintViolationException ex) {

            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Assert.assertEquals("may not be null", ex.getConstraintViolations().iterator().next().getMessage());

            service.method2("x");
        }
    }

    /**
     * Checking validator()
     */
    @Test
    public void baseValidator() {

        Assert.assertNotNull(service.method3());
    }

    /**
     * Checking rejection. The method reject(...) can be used to throws an
     * exception with specific message code and can be used for instant
     * validation
     */
    @Test
    public void baseRejection() {

        try {
            service.method4("not.null.value");
            Assert.fail();
        } catch (ApplicationException ex) {
            Assert.assertEquals("The value is expected to be NOT NULL", ex.getMessage());
        }
    }

    /**
     * Checking throwing validation errors
     */
    @Test
    public void testValidationRejection() {

        SomeValidated v = new SomeValidated();
        v.setDigit(4);

        try {

            Set<ConstraintViolation<SomeValidated>> errors = validator.validate(v);

            /*
             * I didn't cope with 'generic' in this case
             */
            Set<ConstraintViolation<?>> errors2 = new HashSet<ConstraintViolation<?>>();
            errors2.addAll(errors);

            service.method5(errors2);

            Assert.fail();

        } catch (ApplicationException ex) {
            Assert.assertEquals("[The value is expected to be NOT NULL, The value 4 must be greater then 5]",
                    ex.getMessage());
        }
    }
}
