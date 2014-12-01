/**
 * 
 */
package ru.anr.base.services.api;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.APIException;
import ru.anr.base.domain.api.ErrorModel;
import ru.anr.base.domain.api.RawFormatTypes;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.services.BaseServiceTestCase;

/**
 * Tests for api commmand
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class ApiCommandFactoryTest extends BaseServiceTestCase {

    /**
     * the PING costant
     */
    private static final String PING = "Ping";

    /**
     * the GET constant
     */
    private static final String GET = "GET";

    /**
     * The bean under tests
     */
    @Autowired
    @Qualifier("apiCommandFactory")
    private APICommandFactory factory;

    /**
     * Use case : Ping command
     */
    @Test
    public void testOfPingCommand() {

        APICommand ping = new APICommand(PING, "v1");
        ping = ping.addRaw("{\"value\": \"hello 1\"}").method(GET);

        APICommand rs = factory.process(ping);
        ResponseModel m = rs.getResponse();

        Assert.assertEquals(0, m.getCode());
        Assert.assertEquals("hello 1 GET", ((ErrorModel) m).getMessage());

        // Ping version 2
        ping = new APICommand(PING, "v2");
        ping = ping.addRaw("{\"value\": \"hello 2\"}").method(GET);

        rs = factory.process(ping);
        m = rs.getResponse();

        Assert.assertEquals(-1, m.getCode()); // Yes!
        Assert.assertEquals("hello 2 Get", ((ErrorModel) m).getMessage());

        // Ping version 3 (does not exists)
        ping = new APICommand(PING, "v3");
        ping = ping.addRaw("{\"value\": \"hello\"}").method(GET);

        try {

            rs = factory.process(ping);
            Assert.fail();

        } catch (IllegalArgumentException ex) {

            Assert.assertEquals("Version 'v3' for a command 'ping' does not exist", ex.getMessage());
            rs = factory.error(ping, ex);

            Assert.assertEquals("{\"code\":1,\"message\":\"A system error\","
                    + "\"description\":\"Version 'v3' for a command 'ping' does not exist\"}", rs.getRawModel());
        }
    }

    /**
     * Use case : Ping command, XML case
     */
    @Test
    public void testOfPingCommandXMLCase() {

        APICommand ping = new APICommand(PING, "v1");
        ping.setRequestFormat(RawFormatTypes.XML);
        ping.setResponseFormat(RawFormatTypes.XML);

        ping = ping.addRaw("<?xml version='1.0' encoding='UTF-8' ?><ping value = \"hello x\" />").method(GET);

        APICommand rs = factory.process(ping);
        ResponseModel m = rs.getResponse();

        Assert.assertEquals(0, m.getCode());
        Assert.assertEquals("hello x GET", ((ErrorModel) m).getMessage());
    }

    /**
     * Use case : Ping command
     */
    @Test
    public void testError() {

        APICommand e = new APICommand("Error", "v1");
        e = e.method(GET);

        APICommand rs = null;
        try {

            rs = factory.process(e);
            Assert.fail();

        } catch (APIException ex) {

            Assert.assertEquals("Exception", ex.getMessage());
            rs = factory.error(e, ex);

            Assert.assertEquals("{\"code\":5,\"message\":\"Exception\"," + "\"description\":\"Exception\"}",
                    rs.getRawModel());
        }
    }

    /**
     * Use case : A command with empty content
     */
    @Test
    public void testEmptyContentCommand() {

        APICommand ping = new APICommand("Empty", "v1");
        ping.setRequestFormat(null);
        ping.setResponseFormat(null);

        ping = ping.method(GET);

        APICommand rs = factory.process(ping);
        ResponseModel m = rs.getResponse();

        Assert.assertEquals(0, m.getCode());
    }

    /**
     * Use case : Ping command
     */
    @Test
    public void testMethodsTest() {

        // GET
        APICommand ping = new APICommand(PING, "v1");
        ping = ping.addRaw("{\"value\": \"hello\"}").method(GET);

        APICommand rs = factory.process(ping);
        ResponseModel m = rs.getResponse();
        Assert.assertEquals("hello GET", ((ErrorModel) m).getMessage());

        // PUT
        ping = new APICommand(PING, "v1");
        ping = ping.addRaw("{\"value\": \"hello\"}").method("PUT");

        rs = factory.process(ping);
        m = rs.getResponse();
        Assert.assertEquals("hello PUT", ((ErrorModel) m).getMessage());

        // POST
        ping = new APICommand(PING, "v1");
        ping = ping.addRaw("{\"value\": \"hello\"}").method("POST");

        rs = factory.process(ping);
        m = rs.getResponse();
        Assert.assertEquals("hello POST", ((ErrorModel) m).getMessage());

        // DELETE
        ping = new APICommand(PING, "v1");
        ping = ping.addRaw("{\"value\": \"hello\"}").method("DELETE");

        rs = factory.process(ping);
        m = rs.getResponse();
        Assert.assertEquals("hello DELETE", ((ErrorModel) m).getMessage());
    }
}
