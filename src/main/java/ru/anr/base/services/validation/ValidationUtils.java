/*
 * Copyright 2014-2022 the original author or authors.
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

import ru.anr.base.BaseParent;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validation utilities.
 *
 * @author Alexey Romanchuk
 * @created Feb 22, 2015
 */
public final class ValidationUtils extends BaseParent {

    private ValidationUtils() {

    }

    /**
     * Extracts all error messages from {@link ConstraintViolation} as a single
     * string.
     *
     * @param violations The collection of violations
     * @param <S>        The class of the object
     * @return All errors as a comma-separated string
     */
    public static <S> String getAllErrorsAsString(Set<ConstraintViolation<? extends S>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .distinct()
                .collect(Collectors.joining(","));
    }
}
