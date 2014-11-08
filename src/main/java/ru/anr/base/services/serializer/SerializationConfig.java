/**
 * 
 */
package ru.anr.base.services.serializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Definition of xml/json serialization beans.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 8, 2014
 *
 */
@Configuration
public class SerializationConfig {

    /**
     * Defining an XML Serializer
     * 
     * @return Serializer instance
     */
    @Bean(name = "xmlSerializer")
    public Serializer xmlSerializer() {

        return new XMLSerializerImpl();
    }

    /**
     * Defining a JSON Serializer
     * 
     * @return Serializer instance
     */
    @Bean(name = "jsonSerializer")
    public Serializer jsonSerializer() {

        return new JSONSerializerImpl();
    }
}
