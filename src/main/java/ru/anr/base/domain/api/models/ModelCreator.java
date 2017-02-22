/**
 * 
 */
package ru.anr.base.domain.api.models;

/**
 * An auxiliary interface for creation of models
 *
 *
 * @author Alexey Romanchuk
 * @created Feb 22, 2017
 *
 */
@FunctionalInterface
public interface ModelCreator<S, V> {

    /**
     * Creates a new model of the S type using the given value
     * 
     * @param v
     *            The value to use
     * @return A new model
     */
    S newValue(V v);
}
