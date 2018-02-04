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

import java.util.List;

import ru.anr.base.services.pattern.Strategy;

/**
 * This factory has information about all registered validators.
 *
 *
 * @author Alexey Romanchuk
 * @created Jan 21, 2016
 *
 */

public interface ValidationFactory {

    /**
     * Returns a list with all validators available to the specified class of
     * object
     * 
     * @param clazz
     *            The class
     * @return A list of validators (they're strategies, actually)
     */
    List<Strategy<Object>> getValidators(Class<?> clazz);
}
