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

import ru.anr.base.dao.EntityUtils;

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

    /**
     * A special function to determine the class of certain parameters.
     *
     * @param clazz  The class to determine
     * @param index  The parameter's index
     * @param params The array of parameters
     * @return true, if the given parameter has the given class
     */
    default boolean hasParamOf(Class<?> clazz, int index, Object... params) {
        return (params != null &&
                params.length > index &&
                params[index] != null &&
                EntityUtils.ofClass(params[index], clazz));
    }

    /**
     * Returns the value of the given parameter casting it to the given class.
     *
     * @param clazz  The class to cast
     * @param index  The parameter's index
     * @param params The array of parameters
     * @param <S>    The type
     * @return The parameter's value, or null, if the given parameter does not have
     * the given class
     */
    @SuppressWarnings("unchecked")
    default <S> S getParam(Class<S> clazz, int index, Object... params) {
        return hasParamOf(clazz, index, params) ? (S) params[index] : null;
    }
}
