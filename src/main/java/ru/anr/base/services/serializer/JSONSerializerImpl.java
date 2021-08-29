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

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * JSON Serializer implementation.
 *
 * @author Alexey Romanchuk
 * @created Nov 8, 2014
 */

public class JSONSerializerImpl extends AbstractSerializerImpl {

    /**
     * Constructor. We can use both JAXB and Jackson annotations for mapping.
     */
    public JSONSerializerImpl() {

        super(new ObjectMapper());

        // mapper().configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        // mapper().configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(mapper().getTypeFactory());
        AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();

        mapper().setAnnotationIntrospector(new AnnotationIntrospectorPair(introspector, secondary));
    }
    
    /**
     * A constructor with the flag of formatted output which is convenient for
     * debugging.
     *
     * @param prettyOutput true, if the pretty formatted output is required
     */
    public JSONSerializerImpl(boolean prettyOutput) {

        this();
        mapper().configure(SerializationFeature.INDENT_OUTPUT, prettyOutput);
    }
}
