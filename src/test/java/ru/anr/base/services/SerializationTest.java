/**
 * 
 */
package ru.anr.base.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Assert;
import org.junit.Test;

import ru.anr.base.samples.domain.Model;
import ru.anr.base.samples.domain.SubModel;
import ru.anr.base.services.serializer.JSONSerializerImpl;
import ru.anr.base.services.serializer.Serializer;
import ru.anr.base.services.serializer.XMLSerializerImpl;

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
            + "<model field=\"xxx\" time=\"2014-09-11T10:30Z[GMT]\" sum=\"322.032329300\">"
            + "<subs><sub>1</sub><sub>2</sub></subs></model>";

    /**
     * @return A model instance
     */
    private Model newModel() {

        Model m = new Model();
        m.setField("xxx");
        m.setSum(new BigDecimal("322.032329300"));
        m.setTime(ZonedDateTime.of(2014, 9, 11, 10, 30, 0, 0, ZoneId.of("GMT")));
        m.setSubs(list(new SubModel(1), new SubModel(2)));

        return m;
    }

    /**
     * JSON etalon
     */
    private static final String TEST_JSON = "{\"model\":{\"field\":\"xxx\",\"time\":\"2014-09-11T10:30Z[GMT]\","
            + "\"sub\":[{\"value\":1},{\"value\":2}],\"sum\":322.032329300}}";

    /**
     * JSON Tests
     * 
     * @throws IOException
     *             If error occurs
     */
    @Test
    public void testJSON() throws IOException {

        Model m = newModel();

        Serializer s = new JSONSerializerImpl();

        String value = s.to(m);
        logger.info("JSON: {}", value);

        Assert.assertEquals(TEST_JSON, value);

        Model mx = s.from(value, Model.class);

        Assert.assertEquals(m.getTime(), mx.getTime());
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

        Serializer s = new XMLSerializerImpl();

        String value = s.to(m);
        logger.info("XML: {}", value);

        Assert.assertEquals(TEST_XML, value);

        Model mx = s.from(value, Model.class);
        Assert.assertEquals(m, mx);
    }
}
