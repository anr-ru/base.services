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

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.anr.base.ApplicationException;
import ru.anr.base.BaseParent;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * Abstract jackson-based serialer implementation.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 8, 2014
 *
 */

public abstract class AbstractSerializerImpl extends BaseParent implements Serializer {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractSerializerImpl.class);

    /**
     * Mapper
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructor. Here is performed general initialization for JSON and XML
     * configuration.
     * 
     * @param origin
     *            Original mapper
     */
    public AbstractSerializerImpl(ObjectMapper origin) {

        super();
        this.objectMapper = origin;
        objectMapper.registerModule(new JSR310Module());

        // ISO for date/time
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.setSerializationInclusion(Include.NON_NULL);

        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(objectMapper.getTypeFactory());
        objectMapper.setAnnotationIntrospector(introspector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> S fromStr(String s, Class<S> clazz) {

        try {
            return objectMapper.readValue(s, clazz);
        } catch (IOException ex) {
            logger.error("Serializer exception", ex);
            throw new ApplicationException(ex.getMessage().replaceAll("ru.anr.([a-zA-Z\\d]*\\.)*([a-zA-Z\\d]*)", ""));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> S fromStr(String s, TypeReference<S> ref) {

        try {
            return objectMapper.readValue(s, ref);
        } catch (IOException ex) {
            logger.error("Serializer exception", ex);
            throw new ApplicationException(ex.getMessage().replaceAll("ru.anr.([a-zA-Z\\d]*\\.)*([a-zA-Z\\d]*)", ""));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toStr(Object o) {

        try {
            return objectMapper.writeValueAsString(o);
        } catch (IOException ex) {
            logger.error("Serializer exception", ex);
            throw new ApplicationException(ex.getMessage().replaceAll("ru.anr.([a-zA-Z\\d]*\\.)*([a-zA-Z\\d]*)", ""));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectMapper mapper() {

        return objectMapper;
    }
}
