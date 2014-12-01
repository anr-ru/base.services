/**
 * 
 */
package ru.anr.base.services.api;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ru.anr.base.domain.api.ErrorModel;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.domain.api.models.SortModel;
import ru.anr.base.domain.api.models.SortModel.SortDirection;
import ru.anr.base.services.BaseServiceTestCase;
import ru.anr.base.services.serializer.Serializer;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Tests for serialization of API command models
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 *
 */

public class ApiSerializationTest extends BaseServiceTestCase {

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

        Assert.assertEquals("{\"code\":0}", value);

        RequestModel mx = json.fromStr(value, RequestModel.class);
        Assert.assertEquals(m, mx);

        // These fields are transient
        Assert.assertNull(mx.getPage());
        Assert.assertNull(mx.getFields());
        Assert.assertNull(mx.getPerPage());
        Assert.assertNull(mx.getSorted());
        Assert.assertNull(mx.getSearch());

        value = xml.toStr(m);
        logger.info("XML: {}", value);

        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8'?><request code=\"0\"/>", value);

        mx = xml.fromStr(value, RequestModel.class);
        Assert.assertEquals(m, mx);
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

        Assert.assertEquals("{\"code\":5,\"page\":2,\"per_page\":1}", value);

        ResponseModel mx = json.fromStr(value, ResponseModel.class);
        Assert.assertEquals(m, mx);

        /*
         * XML
         */
        value = xml.toStr(m);
        logger.info("XML: {}", value);

        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8'?><response code=\"5\" page=\"2\" per_page=\"1\"/>",
                value);

        mx = xml.fromStr(value, ResponseModel.class);
        Assert.assertEquals(m, mx);
    }

    /**
     * Examples for serialization/deserializarion of complex types like String
     * of models
     */
    @Test
    public void testListSerialization() {

        ErrorModel e1 = new ErrorModel();
        e1.setCode(1);

        ErrorModel e2 = new ErrorModel();
        e2.setCode(2);

        List<ErrorModel> errors = list(e1, e2);
        Assert.assertEquals("[{\"code\":1},{\"code\":2}]", json.toStr(errors));

        TypeReference<List<ErrorModel>> ref = new TypeReference<List<ErrorModel>>() {
        };

        String js = "[{\"code\":1},{\"code\":2}]";

        errors = json.fromStr(js, ref);
        Assert.assertEquals(1, errors.get(0).getCode());
        Assert.assertEquals(2, errors.get(1).getCode());
    }

    /**
     * {@link ErrorModel}
     */
    @Test
    public void testErrorModel() {

        ErrorModel m = new ErrorModel();
        m.setCode(64);

        m.setMessage("Ошибка");
        m.setDescription("Description of Ошибка");

        String value = json.toStr(m);
        logger.info("JSON: {}", value);

        Assert.assertEquals("{\"code\":64,\"message\":\"Ошибка\",\"description\":\"Description of Ошибка\"}", value);

        ErrorModel mx = json.fromStr(value, ErrorModel.class);
        Assert.assertEquals(m, mx);

        /*
         * XML
         */
        value = xml.toStr(m);
        logger.info("XML: {}", value);

        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8'?><response code=\"64\">"
                + "<message>Ошибка</message><description>Description of Ошибка</description></response>", value);

        mx = xml.fromStr(value, ErrorModel.class);
        Assert.assertEquals(m, mx);
    }
}
