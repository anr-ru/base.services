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

/**
 * Basic states which can be used in many cases.
 *
 * @author Alexey Romanchuk
 * @created Jun 5, 2015
 */

public enum BasicStates {

    /**
     * A new object
     */
    New,
    /**
     * Some process was applied to an object and completed.
     */
    Processed,
    /**
     * Enabled object
     */
    Active,
    /**
     * Some process was applied to an object but not yet completed.
     */
    Pending,
    /**
     * Disabled object
     */
    Inactive,
    /**
     * Some process was applied to an object and was cancelled.
     */
    Cancelled,
    /**
     * Some error state
     */
    Failed,
    /**
     * The operations with the object have been suspended
     */
    Suspended,
    /**
     * The 'deleted' usually means the object should not be considered anymore
     */
    Deleted
}
