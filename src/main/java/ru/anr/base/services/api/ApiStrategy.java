/**
 * 
 */
package ru.anr.base.services.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.anr.base.domain.api.models.RequestModel;

/**
 * This annotation marks a specific class as a strategy which works with
 * specific API Command. The obligatory parameters are version and commandId.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiStrategy {

    /**
     * Version of an API command
     */
    String version();

    /**
     * Unique identifier of an API command
     */
    String id();

    /**
     * Defines a class for request command body representation
     */
    Class<? extends RequestModel> model() default RequestModel.class;
}
