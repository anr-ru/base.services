/*
 * Copyright 2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
