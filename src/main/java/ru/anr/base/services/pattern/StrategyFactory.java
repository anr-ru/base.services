/**
 * 
 */
package ru.anr.base.services.pattern;

/**
 * An interface - the main entry point for procecessing an object via specific
 * chain of strategies.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public interface StrategyFactory {

    /**
     * Starts execution of strategy chain for specific object
     * 
     * @param object
     *            An object
     * @param params
     *            An array of additional params
     * @return Result of processing (contains a result object and information
     *         about applied strategies)
     */
    StrategyStatistic process(Object object, Object... params);
}
