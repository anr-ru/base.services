/**
 * 
 */
package ru.anr.base.services.validation;

import java.util.List;

import ru.anr.base.services.pattern.Strategy;

/**
 * This factory has information about all registered validators.
 *
 *
 * @author Alexey Romanchuk
 * @created Jan 21, 2016
 *
 */

public interface ValidationFactory {

    /**
     * Returns a list with all validators available to the specified class of
     * object
     * 
     * @param clazz
     *            The class
     * @return A list of validators (they're strategies, actually)
     */
    List<Strategy<Object>> getValidators(Class<?> clazz);
}
