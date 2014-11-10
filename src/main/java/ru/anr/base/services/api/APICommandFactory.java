/**
 * 
 */
package ru.anr.base.services.api;

import ru.anr.base.domain.api.APICommand;

/**
 * Interface for command factory. Provides general function to handle API
 * command and their errors.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public interface APICommandFactory {

    /**
     * Processing an API Command. If an exception occurs, the below
     * {@link #error(APICommand, Exception)} procedure required to get a valid
     * error response.
     * 
     * @param cmd
     *            API Command
     * @return Resulted API Command
     */
    APICommand process(APICommand cmd);

    /**
     * A special entry point for handling errors. It generates a pretty error
     * response.
     * 
     * @param cmd
     *            Original API Command
     * @param ex
     *            AN exception
     * @return API Command with built error response
     */
    APICommand error(APICommand cmd, Exception ex);
}
