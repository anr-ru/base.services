/**
 * 
 */
package ru.anr.base.services.validation;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import ru.anr.base.services.pattern.Strategy;

/**
 * Base Validator Interface
 *
 *
 * @author Aleksey Melkov
 * @created 09 нояб. 2015 г.
 *
 * @param <T>
 *            Type of object
 */

public interface BaseValidator<T> extends Strategy<T> {

    /**
     * Determines whether the validator supports or not the specified class
     * 
     * @param clazz
     *            The class to be checked
     * @return true, if supports
     */
    default boolean supports(Class<?> clazz) {

        return getSupported().isAssignableFrom(clazz);
    }

    /**
     * Returns the class which is linked to this validator (by {@link Validator}
     * annotation)
     * 
     * @return the class
     */
    default Class<?> getSupported() {

        Validator a = AnnotationUtils.findAnnotation(getClass(), Validator.class);
        Assert.notNull(a, "Wrong validator: not annotation @Validator for " + getClass());

        return a.type();
    }

    /**
     * Checks additional conditions for using validation for the specific object
     * 
     * @param object
     *            The object to check
     * @return true, if the object is allowed to be validated
     */
    boolean supports(T object);
}
