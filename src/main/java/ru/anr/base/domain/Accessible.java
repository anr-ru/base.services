/*
 * Copyright 2014-2024 the original author or authors.
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
package ru.anr.base.domain;

import org.springframework.security.core.Authentication;

/**
 * Accessible Interface - a simple way to perform access check on the domain
 * object level.
 *
 * @author Alexey Romanchuk
 * @created Feb 18, 2015
 */
public interface Accessible {
    /**
     * A customizable function for domain objects. Must be overridden on
     * concrete objects.
     *
     * @param token      The authentication token
     * @param permission The permission to check. (See
     *                   PermissionEvaluator#hasPermission() for argument details)
     * @return true, if the object can be accessed
     */
    default boolean accessible(Authentication token, Object permission) {
        return true; // by default, it's always accessible
    }
}
