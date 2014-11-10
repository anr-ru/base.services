/**
 * 
 */
package ru.anr.base.services.api;

import ru.anr.base.domain.api.APICommand;

/**
 * Interface for Api command strategy.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public interface ApiCommandStrategy {

    /**
     * Process an API Command. Parsed and prepared
     * 
     * @param cmd
     *            API Command
     * @return Resulted command (with embedded response model)
     */
    APICommand process(APICommand cmd);
}
