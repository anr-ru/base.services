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

import org.apache.commons.lang3.ArrayUtils;
import ru.anr.base.BaseParent;

import java.util.List;

/**
 * A structure for storing the invocation context in strategies.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */
public class StrategyConfig {

    /**
     * True, if a strategy can be applied for the specified object
     */
    private final boolean applicable;

    /**
     * The original object for strategy application
     */
    private final Object object;

    /**
     * See {@link StrategyModes}
     */
    private final StrategyModes mode;

    /**
     * An array of additional parameters
     */
    private final Object[] params;

    /**
     * A collection of objects gathered during processing through all strategies
     */
    private final List<Object> collection = BaseParent.list();

    /**
     * Constructor
     *
     * @param applicable true/false for strategy to be applied
     * @param object     An object
     * @param mode       {@link StrategyModes}
     * @param params     Stored additional params
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
     * Adds an object to the resulted collection
     *
     * @param o Some object
     */
    public void add(Object o) {

        collection.add(o);
    }

    /**
     * @return the collection
     */
    public List<Object> getCollection() {

        return collection;
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
         * Normal execution of the strategies' chain
         */
        Normal,
        /**
         * After this strategy further execution must be stopped (current
         * strategy is the last)
         */
        TerminateAfter
    }
}
