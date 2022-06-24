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
package ru.anr.base.services.pattern;

/**
 * A simple strategy pattern.
 *
 * @param <T> Type of the object
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */

public interface Strategy<T> {
    /**
     * Checking how this strategy can be or can not be applied to an object.
     *
     * @param o      The object
     * @param params Additional parameters
     * @return Configuration for strategy execution (true or false), including a
     * {@link ru.anr.base.services.pattern.StrategyConfig.StrategyModes}
     */
    StrategyConfig check(T o, Object... params);

    /**
     * Performing this strategy to an object
     *
     * @param o   Incoming object
     * @param cfg Original configuration
     * @return Resulted (possibly, updated) object
     */
    T process(T o, StrategyConfig cfg);
}
