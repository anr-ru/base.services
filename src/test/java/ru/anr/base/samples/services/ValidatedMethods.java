/**
 * 
 */
package ru.anr.base.samples.services;

import java.util.Collection;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

/**
 * Method validation
 *
 *
 * @author Alexey Romanchuk
 * @created Jan 30, 2015
 *
 */

public interface ValidatedMethods {

    /**
     * Method 1 (not null argument required)
     * 
     * @param code
     *            argument
     * @return value
     */
    String method1(@NotNull(message = "{not.null.value}") String code);

    /**
     * Method 2 (returns not null)
     * 
     * @param x
     *            Argument
     * @return Some not null value
     */
    @NotNull
    Object method2(String x);

    /**
     * Method 3 (return validator)
     * 
     * @return Validator instance or null
     */
    Validator method3();

    /**
     * Method 4 (throws an exception)
     * 
     * @param code
     *            A code
     */
    void method4(String code);

    /**
     * Method 5 (throws an exception according to validator)
     * 
     * @param constraints
     *            Validation errors
     * @param <S>
     *            Type of object
     */
    <S> void method5(Collection<ConstraintViolation<Object>> constraints);
}
