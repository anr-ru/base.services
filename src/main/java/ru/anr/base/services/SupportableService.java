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
package ru.anr.base.services;

/**
 * A prototype for all services that are run based on switchers. The use of some services depends
 * on the configuration where the platform is launched.
 *
 * @author Alexey Romanchuk
 * @created Jun 15, 2022
 */
public interface SupportableService {
    /**
     * @return true, if the service is supported
     */
    default boolean isSupported() {
        return true;
    }
}
