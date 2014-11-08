/**
 * 
 */
package ru.anr.base.services.serializer;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * JSON Serializer implementation.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 8, 2014
 *
 */

public class JSONSerializerImpl extends AbstractSerializerImpl {

    /**
     * Constructor. We can use both JAXB and Jackson annotations for mapping.
     */
    public JSONSerializerImpl() {

        super(new ObjectMapper());

        mapper().configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        mapper().configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(mapper().getTypeFactory());
        AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();

        mapper().setAnnotationIntrospector(new AnnotationIntrospectorPair(introspector, secondary));
    }
}
