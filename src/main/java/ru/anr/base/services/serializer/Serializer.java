/**
 * 
 */
package ru.anr.base.services.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple serialization interface.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 8, 2014
 *
 */

public interface Serializer {

    /**
     * Generates an object of specified class from String
     * 
     * @param s
     *            String (can be XML/JSON)
     * @param clazz
     *            Expected class
     * @return Parsed object instance
     * 
     * @param <S>
     *            Object class
     */
    <S> S fromStr(String s, Class<S> clazz);

    /**
     * Generates a string of given format (XML/JSON)
     * 
     * @param o
     *            Object
     * @return String
     */
    String toStr(Object o);

    /**
     * Returns an internal {@link ObjectMapper}.
     * 
     * @return Instance of {@link ObjectMapper} associated with serializer
     */
    ObjectMapper mapper();
}
