/**
 * 
 */
package ru.anr.base.services.pattern;

import java.util.List;

/**
 * A holder for strategies processing result.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
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
     * Constructor
     * 
     * @param object
     *            An object
     * @param appliedStrategies
     *            List of classes of all applied strategies
     */
    public StrategyStatistic(Object object, List<Class<?>> appliedStrategies) {

        this.object = object;
        this.appliedStrategies = appliedStrategies;
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

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
}
