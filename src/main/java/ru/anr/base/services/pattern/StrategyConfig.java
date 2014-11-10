/**
 * 
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
    private StrategyModes mode = StrategyModes.Normal;

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
     * @param params
     *            Stored additinal params
     * @param mode
     *            {@link StrategyModes}
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

        return params;
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
         * After this strategy further strategies execution must be stopped (the
         * current strategy is the last)
         */
        TerminateAfter;
    }
}
