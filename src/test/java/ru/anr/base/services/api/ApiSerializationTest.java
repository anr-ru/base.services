package ru.anr.base.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.domain.api.models.SortModel;
import ru.anr.base.domain.api.models.SortModel.SortDirection;
import ru.anr.base.services.BaseLocalServiceTestCase;
import ru.anr.base.services.serializer.Serializer;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Tests for serialization of API command models
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 */
public class ApiSerializationTest extends BaseLocalServiceTestCase {

    /**
     * A ref to JSON Serializer bean
     */
    @Autowired
    @Qualifier("jsonSerializer")
    private Serializer json;

    /**
     * A ref to XML Serializer bean
     */
    @Autowired
    @Qualifier("xmlSerializer")
    private Serializer xml;

    /**
     * {@link RequestModel}
     */
    @Test
    public void testRequest() {

        RequestModel m = new RequestModel();
        m.page = 5;
        m.fields = list("id,name");
        m.perPage = 3;
        m.search = "query";
        m.sorted = list(new SortModel("name", SortDirection.ASC));

        String value = json.toStr(m);
        logger.info("JSON: {}", value);

        Assertions.assertEquals("{\"page\":5,\"per_page\":3}", value);

        RequestModel mx = json.fromStr(value, RequestModel.class);
        Assertions.assertEquals(m, mx);

        // These fields are transient
        Assertions.assertNull(mx.fields);
        Assertions.assertNull(mx.sorted);
        Assertions.assertNull(mx.search);

        value = xml.toStr(m);
        logger.info("XML: {}", value);

        Assertions.assertEquals("<?xml version='1.0' encoding='UTF-8'?><request page=\"5\" per_page=\"3\"/>", value);

        mx = xml.fromStr(value, RequestModel.class);
        Assertions.assertEquals(m, mx);
    }

    /**
     * {@link ResponseModel}
     */
    @Test
    public void testResponse() {

        ResponseModel m = new ResponseModel();
        m.code = 5;
        m.page = 2;
        m.perPage = 1;
        m.attrs = new LinkedHashMap<>();
        m.attrs.put("a1", list(1, 2, 3));
        m.attrs.put("a2", list("1", "2", "3"));
        m.attrs.put("a3", list(d("1.01"), d("2.02"), d("3.03")));

        String value = json.toStr(m);
        logger.info("JSON: {}", value);

        Assertions.assertEquals("{\"code\":5,\"page\":2,\"per_page\":1,\"attrs\":{\"a1\":[1,2,3],\"a2\":[\"1\",\"2\",\"3\"],\"a3\":[1.01,2.02,3.03]}}", value);

        ResponseModel mx = json.fromStr(value, ResponseModel.class);
        Assertions.assertEquals(m.code, mx.code);
        Assertions.assertEquals(m.page, mx.page);
        Assertions.assertEquals(m.perPage, mx.perPage);
        Assertions.assertEquals(m.attrs.get("a1"), mx.attrs.get("a1"));
        Assertions.assertEquals(m.attrs.get("a2"), mx.attrs.get("a2"));
        Assertions.assertEquals(m.attrs.get("a3").get(0), d(String.valueOf(mx.attrs.get("a3").get(0))));
        Assertions.assertEquals(m.attrs.get("a3").get(1), d(String.valueOf(mx.attrs.get("a3").get(1))));
        Assertions.assertEquals(m.attrs.get("a3").get(2), d(String.valueOf(mx.attrs.get("a3").get(2))));

        /*
         * XML
         */
        value = xml.toStr(m);
        logger.info("XML: {}", value);

        Assertions.assertEquals("<?xml version='1.0' encoding='UTF-8'?>" +
                        "<response code=\"5\" page=\"2\" per_page=\"1\">" +
                        "<attrs>" +
                        "<a1>1</a1>" +
                        "<a1>2</a1>" +
                        "<a1>3</a1>" +
                        "<a2>1</a2>" +
                        "<a2>2</a2>" +
                        "<a2>3</a2>" +
                        "<a3>1.01</a3>" +
                        "<a3>2.02</a3>" +
                        "<a3>3.03</a3>" +
                        "</attrs></response>",
                value);

        mx = xml.fromStr(value, ResponseModel.class);
        Assertions.assertEquals(m, mx);
    }

    /**
     * Examples for serialization/deserialization of complex types like String
     * of models
     */
    @Test
    public void testListSerialization() {

        ResponseModel e1 = new ResponseModel();
        e1.code = 1;

        ResponseModel e2 = new ResponseModel();
        e2.code = 2;

        List<ResponseModel> errors = list(e1, e2);
        Assertions.assertEquals("[{\"code\":1},{\"code\":2}]", json.toStr(errors));

        TypeReference<List<ResponseModel>> ref = new TypeReference<>() {
        };

        String js = "[{\"code\":1},{\"code\":2}]";

        errors = json.fromStr(js, ref);
        Assertions.assertEquals(1, errors.get(0).code);
        Assertions.assertEquals(2, errors.get(1).code);
    }

    /**
     * Testing errors
     */
    @Test
    public void testErrorModel() {

        ResponseModel m = new ResponseModel();
        m.code = 64;

        m.message = "Ошибка";
        m.description = "Description of Ошибка";

        String value = json.toStr(m);
        logger.info("JSON: {}", value);

        Assertions.assertEquals("{\"code\":64,\"message\":\"Ошибка\",\"description\":\"Description of Ошибка\"}", value);

        ResponseModel mx = json.fromStr(value, ResponseModel.class);
        Assertions.assertEquals(m, mx);

        /*
         * XML
         */
        value = xml.toStr(m);
        logger.info("XML: {}", value);

        Assertions.assertEquals("<?xml version='1.0' encoding='UTF-8'?><response code=\"64\">"
                + "<message>Ошибка</message><description>Description of Ошибка</description></response>", value);

        mx = xml.fromStr(value, ResponseModel.class);
        Assertions.assertEquals(m, mx);
    }
}
