/*
 * Copyright 2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
