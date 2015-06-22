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

import ru.anr.base.domain.api.APICommand;

/**
 * Interface for command factory. Provides general function to handle API
 * command and their errors.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public interface APICommandFactory {

    /**
     * Processing an API Command. If an exception occurs, the below
     * {@link #error(APICommand, Throwable)} procedure required to get a valid
     * error response.
     * 
     * @param cmd
     *            API Command
     * @return Resulted API Command
     */
    APICommand process(APICommand cmd);

    /**
     * A special entry point for handling errors. It generates a pretty error
     * response.
     * 
     * @param cmd
     *            Original API Command
     * @param ex
     *            An exception
     * @return API Command with built error response
     */
    APICommand error(APICommand cmd, Throwable ex);

    /**
     * A special entry point for handling global errors, not for specific
     * command.
     * 
     * @param ex
     *            AN exception
     * @return API Command with built error response
     */
    APICommand error(Throwable ex);
}
