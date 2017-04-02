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
     * Some action has been successfully applied to the object
     */
    Processed,
    /**
     * Enabled object
     */
    Active,
    /**
     * The movement from the 'New' to 'Active' state
     */
    Pending,
    /**
     * Disabled object
     */
    Inactive;
}
