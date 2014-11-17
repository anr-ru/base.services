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
     * Creates new data (http POST)
     */
    Post,
    /**
     * Retrieves some data (http GET)
     */
    Get,
    /**
     * Modifies existing data (PUT)
     */
    Put,
    /**
     * Deletes some data (DELETE)
     */
    Delete;
}
