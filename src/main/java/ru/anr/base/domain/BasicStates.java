/**
 * 
 */
package ru.anr.base.domain;

/**
 * Basic states which can be used in many cases.
 *
 *
 * @author Alexey Romanchuk
 * @created Jun 5, 2015
 *
 */

public enum BasicStates {

    /**
     * A new object
     */
    New,
    /**
     * Some process was applied to an object
     */
    Processed,
    /**
     * Enabled object
     */
    Active,
    /**
     * The movement between states
     */
    Pending,
    /**
     * Disabled object
     */
    Inactive,
    /**
     * Cancellation of a process
     */
    Cancelled;
}
