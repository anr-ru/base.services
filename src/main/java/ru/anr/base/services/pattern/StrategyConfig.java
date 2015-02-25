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
package ru.anr.base.services.pattern;

import org.apache.commons.lang3.ArrayUtils;

/**
 * A storage for the way how a stategy should be handled. It's used by
 * {@link StrategyFactory} algorithm.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class StrategyConfig {

    /**
     * True, if a strategy can be applied for specified object
     */
    private final boolean applicable;

    /**
     * Original object for strategy application
     */
    private final Object object;

    /**
     * See {@link StrategyModes}
     */
    private final StrategyModes mode;

    /**
     * An array of additinal params
     */
    private final Object[] params;

    /**
     * Constructor
     * 
     * @param applicable
     *            true/false for strategy to be applied
     * @param object
     *            An object
     * @param mode
     *            {@link StrategyModes}
     * @param params
     *            Stored additinal params
     */
    public StrategyConfig(boolean applicable, Object object, StrategyModes mode, Object... params) {

        this.applicable = applicable;
        this.object = object;
        this.mode = mode;
        this.params = ArrayUtils.clone(params);
    }

    /**
     * @return the applicable
     */
    public boolean isApplicable() {

        return applicable;
    }

    /**
     * @return the object
     */
    public Object getObject() {

        return object;
    }

    /**
     * @return the mode
     */
    public StrategyModes getMode() {

        return mode;
    }

    /**
     * @return the params
     */
    public Object[] getParams() {

        return ArrayUtils.clone(params);
    }

    /**
     * The mode is used for chain of strategies, when it's necessary to stop
     * execution of sequence after specific strategy.
     */
    public enum StrategyModes {
        /**
         * Normal execution of strategies chain
         */
        Normal,
        /**
         * After this strategy, further execution must be stopped (current
         * strategy is the last)
         */
        TerminateAfter;
    }
}
