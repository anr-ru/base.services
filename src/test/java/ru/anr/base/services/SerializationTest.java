/**
 * 
 */
package ru.anr.base.services;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import ru.anr.base.samples.domain.Model;
import ru.anr.base.samples.domain.SubModel;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * Tests for serialization services.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 *
 */

public class SerializationTest extends BaseServiceTestCase {

    /**
     * XML etalon
     */
    private static final String TEST_XML = "<?xml version=\'1.0\' encoding=\'UTF-8\'?>"
            + "<model field=\"xxx\"><subs><sub>1</sub><sub>2</sub></subs></model>";

    /**
     * @return A model instance
     */
    private Model newModel() {

        Model m = new Model();
        m.setField("xxx");
        m.setSubs(list(new SubModel(1), new SubModel(2)));

        return m;
    }

    /**
     * JSON etalon
     */
    private static final String TEST_JSON = "{\"model\":{\"field\":\"xxx\",\"sub\":[{\"value\":1},{\"value\":2}]}}";

    /**
     * JSON Tests
     * 
     * @throws IOException
     *             If error occurs
     */
    @Test
    public void testJSON() throws IOException {

        Model m = newModel();

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(mapper.getTypeFactory());
        // if ONLY using JAXB annotations:
        mapper.setAnnotationIntrospector(introspector);
        // if using BOTH JAXB annotations AND Jackson annotations:

        AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();
        mapper.setAnnotationIntrospector(new AnnotationIntrospectorPair(introspector, secondary));

        String value = mapper.writeValueAsString(m);
        logger.info("JSON: {}", value);

        Assert.assertEquals(TEST_JSON, value);

        Model mx = mapper.readValue(value, Model.class);
        Assert.assertEquals(m, mx);
    }

    /**
     * XML via Jackson
     * 
     * @throws IOException
     *             If error occurs
     */
    @Test
    public void testJacksonXML() throws IOException {

        Model m = newModel();

        JacksonXmlModule module = new JacksonXmlModule();
        // and then configure, for example:
        module.setDefaultUseWrapper(true);

        XmlMapper mapper = new XmlMapper(module);
        // and you can also configure AnnotationIntrospectors etc here:

        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(mapper.getTypeFactory());
        // if ONLY using JAXB annotations:
        mapper.setAnnotationIntrospector(introspector);

        mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);

        String value = mapper.writeValueAsString(m);
        logger.info("XML: {}", value);

        Assert.assertEquals(TEST_XML, value);

        Model mx = mapper.readValue(value, Model.class);
        Assert.assertEquals(m, mx);
    }
}
