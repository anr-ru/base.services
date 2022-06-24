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

import java.util.List;

/**
 * A holder for strategies processing result.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */

public class StrategyStatistic {

    /**
     * An object
     */
    private final Object object;

    /**
     * List of classes for all applied strategies
     */
    private final List<Class<?>> appliedStrategies;

    /**
     * The resulted list
     */
    private final List<Object> results;

    /**
     * Constructor
     *
     * @param object            An object
     * @param appliedStrategies List of classes of all applied strategies
     * @param results           The results of processing
     */
    public StrategyStatistic(Object object, List<Class<?>> appliedStrategies, List<Object> results) {
        this.object = object;
        this.appliedStrategies = appliedStrategies;
        this.results = results;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///// getters/setters
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return the object
     */
    public Object getObject() {
        return object;
    }

    /**
     * @return the appliedStrategies
     */
    public List<Class<?>> getAppliedStrategies() {
        return appliedStrategies;
    }

    /**
     * @return the results
     */
    public List<Object> getResults() {
        return results;
    }
}
