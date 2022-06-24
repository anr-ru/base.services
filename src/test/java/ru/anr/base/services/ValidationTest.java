package ru.anr.base.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.anr.base.ApplicationException;
import ru.anr.base.samples.domain.SomeValidated;
import ru.anr.base.samples.services.ValidatedMethods;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

/**
 * Description ...
 *
 * @author Alexey Romanchuk
 * @created Jan 30, 2015
 */

public class ValidationTest extends BaseLocalServiceTestCase {

    /**
     * Default validator injection
     */
    @Autowired
    private Validator validator;

    /**
     * Validation
     */
    @Test
    public void testValidation() {

        SomeValidated v = new SomeValidated();
        v.setDigit(4);

        Set<ConstraintViolation<SomeValidated>> errors = validator.validate(v);
        Assertions.assertEquals(2, errors.size());

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
                Assertions.assertEquals("The value 4 must be greater then 5", c.getMessage());
            } else {
                Assertions.assertEquals("The value is expected to be NOT NULL", c.getMessage());
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
            Assertions.fail();

        } catch (ConstraintViolationException ex) {

            Assertions.assertEquals(1, ex.getConstraintViolations().size());
            Assertions.assertEquals("The value is expected to be NOT NULL", ex.getConstraintViolations().iterator().next()
                    .getMessage());

            Assertions.assertEquals("[xxxYxxx]", service.method1("Y"));
        }

        // 2. Constraint on a returned value

        try {
            service.method2(null);
            Assertions.fail();
        } catch (ConstraintViolationException ex) {

            Assertions.assertEquals(1, ex.getConstraintViolations().size());
            Assertions.assertEquals("must not be null", ex.getConstraintViolations().iterator().next().getMessage());

            Assertions.assertEquals("x", service.method2("x"));
        }
    }

    /**
     * Checking validator()
     */
    @Test
    public void baseValidator() {

        Assertions.assertNotNull(service.method3());
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
            Assertions.fail();
        } catch (ApplicationException ex) {
            Assertions.assertEquals("The value is expected to be NOT NULL", ex.getMessage());
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
            service.method5(errors);

            Assertions.fail();
        } catch (ApplicationException ex) {
            String s = ex.getMessage();
            Assertions.assertTrue(s.contains("The value is expected to be NOT NULL"));
            Assertions.assertTrue(s.contains("The value 4 must be greater then 5"));
        }
    }
}
