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
        m.setPage(5);
        m.setFields(list("id,name"));
        m.setPerPage(3);
        m.setSearch("query");
        m.setSorted(list(new SortModel("name", SortDirection.ASC)));

        String value = json.toStr(m);
        logger.info("JSON: {}", value);

        Assertions.assertEquals("{\"page\":5,\"per_page\":3}", value);

        RequestModel mx = json.fromStr(value, RequestModel.class);
        Assertions.assertEquals(m, mx);

        // These fields are transient
        Assertions.assertNull(mx.getFields());
        Assertions.assertNull(mx.getSorted());
        Assertions.assertNull(mx.getSearch());

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
        m.setCode(5);
        m.setPage(2);
        m.setPerPage(1);

        String value = json.toStr(m);
        logger.info("JSON: {}", value);

        Assertions.assertEquals("{\"code\":5,\"page\":2,\"per_page\":1}", value);

        ResponseModel mx = json.fromStr(value, ResponseModel.class);
        Assertions.assertEquals(m, mx);

        /*
         * XML
         */
        value = xml.toStr(m);
        logger.info("XML: {}", value);

        Assertions.assertEquals("<?xml version='1.0' encoding='UTF-8'?><response code=\"5\" page=\"2\" per_page=\"1\"/>",
                value);

        mx = xml.fromStr(value, ResponseModel.class);
        Assertions.assertEquals(m, mx);
    }

    /**
     * Examples for serialization/deserializarion of complex types like String
     * of models
     */
    @Test
    public void testListSerialization() {

        ResponseModel e1 = new ResponseModel();
        e1.setCode(1);

        ResponseModel e2 = new ResponseModel();
        e2.setCode(2);

        List<ResponseModel> errors = list(e1, e2);
        Assertions.assertEquals("[{\"code\":1},{\"code\":2}]", json.toStr(errors));

        TypeReference<List<ResponseModel>> ref = new TypeReference<List<ResponseModel>>() {
        };

        String js = "[{\"code\":1},{\"code\":2}]";

        errors = json.fromStr(js, ref);
        Assertions.assertEquals(Integer.valueOf(1), errors.get(0).getCode());
        Assertions.assertEquals(Integer.valueOf(2), errors.get(1).getCode());
    }

    /**
     * Testing errors
     */
    @Test
    public void testErrorModel() {

        ResponseModel m = new ResponseModel();
        m.setCode(64);

        m.setMessage("Ошибка");
        m.setDescription("Description of Ошибка");

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
