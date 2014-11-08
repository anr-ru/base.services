/**
 * 
 */
package ru.anr.base.services.serializer;

import java.io.IOException;

import ru.anr.base.ApplicationException;
import ru.anr.base.BaseParent;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
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
     * Mapper
     */
    private ObjectMapper objectMapper;

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
            throw new ApplicationException(ex);
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
            throw new ApplicationException(ex);
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
