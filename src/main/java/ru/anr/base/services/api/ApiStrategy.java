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
     * 
     * @return The version
     */
    String version();

    /**
     * Unique identifier of an API command
     * 
     * 
     * @return The identifier
     */
    String id();

    /**
     * Defines a class for request command body representation
     * 
     * @return The model
     */
    Class<? extends RequestModel> model() default RequestModel.class;
}
