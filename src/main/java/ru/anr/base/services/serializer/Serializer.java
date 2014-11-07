/**
 * 
 */
package ru.anr.base.services.serializer;

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
    <S> S from(String s, Class<S> clazz);

    /**
     * Generates a string of given format (XML/JSON)
     * 
     * @param o
     *            Object
     * @return String
     */
    String to(Object o);
}
