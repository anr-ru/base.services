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

import java.util.Set;

import javax.validation.ConstraintViolation;

import ru.anr.base.BaseParent;

/**
 * Validation utilities.
 *
 *
 * @author Alexey Romanchuk
 * @created Feb 22, 2015
 *
 */
public final class ValidationUtils extends BaseParent {

    /**
     * Private constructor
     */
    private ValidationUtils() {

        super();
    }

    /**
     * Extract all error message from {@link ConstraintViolation} as a single
     * string
     * 
     * @param violations
     *            collection of violations
     * @return All errors as a comma-separated string
     * 
     * @param <S>
     *            The class of the object
     */
    public static <S> String getAllErrorsAsString(Set<ConstraintViolation<? extends S>> violations) {

        return list(violations.stream().map(v -> v.getMessage()).distinct()).toString();
    }
}
