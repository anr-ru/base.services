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

/**
 * An interface for general API strategy.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */

public interface ApiCommandStrategy {

    /**
     * GET method
     *
     * @param cmd Command
     * @return Model of response
     */
    Object get(APICommand cmd);

    /**
     * POST method
     *
     * @param cmd Command
     * @return Model of response
     */
    Object post(APICommand cmd);

    /**
     * PUT method
     *
     * @param cmd Command
     * @return Model of response
     */
    Object put(APICommand cmd);

    /**
     * PATCH method
     *
     * @param cmd Command
     * @return Model of response
     */
    Object patch(APICommand cmd);

    /**
     * DELETE method
     *
     * @param cmd Command
     * @return Model of response
     */
    Object delete(APICommand cmd);

    /**
     * Retrieves the stored config for the strategy
     *
     * @return The annotation (it's cached)
     */
    ApiStrategy config();

    /**
     * The initialization procedure before the command execution. This allows to
     * make some preliminary actions, i.e. to set up the thread context.
     *
     * @param cmd The API command
     */
    default void init(APICommand cmd) {
    }
}
