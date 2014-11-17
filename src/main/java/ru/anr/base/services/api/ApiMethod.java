/**
 * 
 */
package ru.anr.base.services.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.anr.base.domain.api.MethodTypes;

/**
 * Marker for class method to be a handler for some API command.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 14, 2014
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiMethod {

    /**
     * Api Method to be used
     */
    MethodTypes value();
}
