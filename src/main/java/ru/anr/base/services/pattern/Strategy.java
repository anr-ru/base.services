/**
 * 
 */
package ru.anr.base.services.pattern;

/**
 * A strategy pattern.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public interface Strategy<T> {

    /**
     * Checking how this strategy can be or cannot be applied to an object.
     * 
     * @param o
     *            Incoming object
     * @param params
     *            Additional params
     * @return Configuration for strategy execution (true or false), including a
     *         {@link ru.anr.base.services.pattern.StrategyConfig.StrategyModes}
     */
    StrategyConfig check(T o, Object... params);

    /**
     * Performing this strategy to an object
     * 
     * @param o
     *            Incoming object
     * @param cfg
     *            Original config
     * @return Resulted (maybe updated) object
     */
    T process(T o, StrategyConfig cfg);
}
