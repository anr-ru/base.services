package ru.anr.base.services.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.anr.base.BaseParent;
import ru.anr.base.samples.domain.Model;
import ru.anr.base.samples.domain.SubModel;
import ru.anr.base.services.BaseLocalServiceTestCase;

import java.io.IOException;
import java.math.BigDecimal;
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
            + "<model field=\"xxx\" time=\"2014-09-11T10:30:00Z\" calendar=\"2014-09-11T10:30:00.000+00:00\" sum=\"322.0323293\">"
            + "<subs><sub>1</sub><sub>2</sub></subs></model>";

    /**
     * @return A model instance
     */
    private Model newModel() {

        Model m = new Model();
        m.field = "xxx";
        m.sum = new BigDecimal("322.0323293");
        m.time = ZonedDateTime.of(2014, 9, 11, 10, 30, 0, 0, DEFAULT_TIMEZONE);
        m.subs = BaseParent.list(new SubModel(1), new SubModel(2));
        m.calendar = calendar(m.time);

        return m;
    }

    /**
     * JSON etalon
     */
    private static final String TEST_JSON = "{\"field\":\"xxx\",\"time\":\"2014-09-11T10:30:00Z\",\"calendar\":\"2014-09-11T10:30:00.000+00:00\","
            + "\"sub\":[{\"value\":1},{\"value\":2}],\"sum\":322.0323293}";

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

        Assertions.assertEquals(date(m.calendar), date(mx.calendar));
        Assertions.assertEquals(m.time, mx.time);

        // TODO: Two calendars differ by 'firstDayOfWeek' and 'minimalDaysInFirstWeek', WTF?
        m.calendar = null;
        mx.calendar = null;
        Assertions.assertEquals(m, mx);

        Assertions.assertEquals(d("600").toPlainString(), d("600.00").stripTrailingZeros().toPlainString());
    }

    /**
     * JSON stripped etalon
     */
    private static final String TEST_JSON_STRIPPED = "{\"field\":\"xxx\",\"time\":\"2014-09-11T10:30:00Z\",\"calendar\":\"2014-09-11T10:30:00.000+00:00\","
            + "\"sub\":[{\"value\":1},{\"value\":2}],\"sum\":322.0323293}";


    @Test
    public void testJSONStripZeros() {

        Model m = newModel();

        JSONSerializerImpl impl = new JSONSerializerImpl();

        String value = impl.toStr(m);
        logger.info("JSON: {}", value);

        Assertions.assertEquals(TEST_JSON_STRIPPED, value);

        Model mx = impl.fromStr(value, Model.class);

        Assertions.assertEquals(m.time, mx.time);
        //mx.setSum(mx.getSum().setScale(9, BigDecimal.ROUND_HALF_UP));

        Assertions.assertEquals(date(m.calendar), date(mx.calendar));
        // TODO: Two calendars differ by 'firstDayOfWeek' and 'minimalDaysInFirstWeek', WTF?
        m.calendar = null;
        mx.calendar = null;

        Assertions.assertEquals(m.sum, mx.sum);
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

        Assertions.assertEquals(date(m.calendar), date(mx.calendar));
        // TODO: Two calendars differ by 'firstDayOfWeek' and 'minimalDaysInFirstWeek', WTF?
        m.calendar = null;
        mx.calendar = null;
        Assertions.assertEquals(m, mx);
    }

    @Test
    public void testJason() throws java.io.IOException {

        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        //m.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"));
        m.setDateFormat(new StdDateFormat());

        ZonedDateTime z = ZonedDateTime.of(2021, 4, 1, 10, 25, 27, 0, DEFAULT_TIMEZONE);

        Assertions.assertEquals("\"2021-04-01T10:25:27Z\"", m.writeValueAsString(z));
    }

    @Test
    public void testExpNumbers() throws IOException {

        Model m = new Model();
        m.sum = new BigDecimal("10");


        JSONSerializerImpl impl = new JSONSerializerImpl();

        String value = impl.toStr(m);
        Assertions.assertEquals("{\"sum\":10}", value);
    }
}
