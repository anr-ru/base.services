package ru.anr.base.services.serializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.anr.base.BaseParent;
import ru.anr.base.samples.domain.Model;
import ru.anr.base.samples.domain.SubModel;
import ru.anr.base.services.BaseLocalServiceTestCase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

/**
 * Tests for serialization services.
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 */
public class SerializationTest extends BaseLocalServiceTestCase {

    /**
     * XML etalon
     */
    private static final String TEST_XML = "<?xml version='1.0' encoding='UTF-8'?>"
            + "<model field=\"xxx\" time=\"2014-09-11T10:30Z\" sum=\"322.032329300\">"
            + "<subs><sub>1</sub><sub>2</sub></subs></model>";

    /**
     * @return A model instance
     */
    private Model newModel() {

        Model m = new Model();
        m.setField("xxx");
        m.setSum(new BigDecimal("322.032329300"));
        m.setTime(ZonedDateTime.of(2014, 9, 11, 10, 30, 0, 0, DEFAULT_TIMEZONE));
        m.setSubs(BaseParent.list(new SubModel(1), new SubModel(2)));

        return m;
    }

    /**
     * JSON etalon
     */
    private static final String TEST_JSON = "{\"field\":\"xxx\",\"time\":\"2014-09-11T10:30Z\","
            + "\"sub\":[{\"value\":1},{\"value\":2}],\"sum\":322.032329300}";

    /**
     * A ref to JSON Serializer bean
     */
    @Autowired
    @Qualifier("jsonSerializer")
    private Serializer json;

    /**
     * JSON Tests
     */
    @Test
    public void testJSON() {

        Model m = newModel();

        String value = json.toStr(m);
        logger.info("JSON: {}", value);

        Assertions.assertEquals(TEST_JSON, value);

        Model mx = json.fromStr(value, Model.class);

        Assertions.assertEquals(m.getTime(), mx.getTime());
        Assertions.assertEquals(m, mx);

        Assertions.assertEquals(d("600").toPlainString(), d("600.00").stripTrailingZeros().toPlainString());
    }

    /**
     * JSON stripped etalon
     */
    private static final String TEST_JSON_STRIPPED = "{\"field\":\"xxx\",\"time\":\"2014-09-11T10:30Z\","
            + "\"sub\":[{\"value\":1},{\"value\":2}],\"sum\":322.0323293}";


    @Test
    public void testJSONStripZeros() {

        Model m = newModel();

        JSONSerializerImpl impl = new JSONSerializerImpl();
        impl.useStripTrainlingZeroSerializer();

        String value = impl.toStr(m);
        logger.info("JSON: {}", value);

        Assertions.assertEquals(TEST_JSON_STRIPPED, value);

        Model mx = impl.fromStr(value, Model.class);

        Assertions.assertEquals(m.getTime(), mx.getTime());
        mx.setSum(mx.getSum().setScale(9, RoundingMode.HALF_UP));
        Assertions.assertEquals(m, mx);

        Assertions.assertEquals(d("600").toPlainString(), d("600.00").stripTrailingZeros().toPlainString());
    }

    /**
     * A ref to XML Serializer bean
     */
    @Autowired
    @Qualifier("xmlSerializer")
    private Serializer xml;

    /**
     * XML via Jackson
     */
    @Test
    public void testJacksonXML() {

        Model m = newModel();

        String value = xml.toStr(m);
        logger.info("XML: {}", value);

        Assertions.assertEquals(TEST_XML, value);

        Model mx = xml.fromStr(value, Model.class);

        Assertions.assertEquals(m.getTime(), mx.getTime());
        Assertions.assertEquals(m, mx);
    }
}
