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
package ru.anr.base.services.api;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * Utils for API.
 *
 * @author Alexey Romanchuk
 * @created Apr 10, 2021
 */
public class ApiUtils {

    private ApiUtils() {
    }

    /**
     * Extract API Strategy information from the given API strategy class.
     *
     * @param clazz The class of API strategy
     * @return the resulted annotation
     */
    public static ApiStrategy extract(Class<?> clazz) {
        ApiStrategy a = AnnotationUtils.findAnnotation(clazz, ApiStrategy.class);
        Assert.notNull(a, "Not an API Strategy");
        return a;
    }
}
