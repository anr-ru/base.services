/**
 * 
 */
package ru.anr.base.domain.api;

/**
 * API General type of methods.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public enum MethodTypes {

    /**
     * Creates new data (aka http POST)
     */
    Create,
    /**
     * Retrieves some data (http GET)
     */
    Get,
    /**
     * Modifies existing data (PUT)
     */
    Modify,
    /**
     * Deletes some data (DELETE)
     */
    Delete;
}
