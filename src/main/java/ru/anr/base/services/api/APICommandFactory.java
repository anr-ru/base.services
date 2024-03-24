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

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.models.ResponseModel;

/**
 * An interface for the command factory. Provides general functions to handle
 * API commands and their errors.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */

public interface APICommandFactory {

    /**
     * Processes the given API Command. If an exception occurs, the below
     * {@link #error(APICommand, Throwable)} procedure required to get a valid
     * error response.
     *
     * @param cmd The API Command
     * @return The resulted API Command
     */
    APICommand process(APICommand cmd);

    /**
     * A special entry point for handling errors. It generates a proper API error
     * response.
     *
     * @param cmd The original API Command
     * @param ex  The thrown exception
     * @return The resulted API Command with a built error response
     */
    APICommand error(APICommand cmd, Throwable ex);

    /**
     * A special entry point for handling errors. It generates an API error
     * response using the specified response model.
     *
     * @param cmd   Original API Command
     * @param ex    An exception
     * @param model The model to use
     * @return API Command with a built error response
     */
    APICommand error(APICommand cmd, Throwable ex, ResponseModel model);

    /**
     * A special entry point for handling global errors if the original command
     * is unknown in the current context.
     *
     * @param ex The exception
     * @return The API Command with a built error response
     */
    APICommand error(Throwable ex);
}
