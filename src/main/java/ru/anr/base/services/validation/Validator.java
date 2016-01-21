/**
 * 
 */
package ru.anr.base.services.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Validation Annotation.
 *
 *
 * @author Alexey Romanchuk
 * @created Jan 21, 2016
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validator {

    /**
     * @return The type to be validated
     */
    Class<?> type();

    /**
     * The order if it is required (the more this number, the later the
     * validator is applied)
     * 
     * @return Some integer value
     */
    int order() default 0;
}
