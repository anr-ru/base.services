package ru.anr.base.services.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.APIException;
import ru.anr.base.domain.api.RawFormatTypes;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.samples.services.api.EmptyV1ApiCommand;
import ru.anr.base.services.BaseLocalServiceTestCase;

/**
 * Tests for API commands
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */
public class ApiCommandFactoryTest extends BaseLocalServiceTestCase {

    private static final String PING = "Ping";

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

        Assertions.assertEquals(Integer.valueOf(0), m.code);
        Assertions.assertEquals("hello 1 GET", m.message);

        // Ping version 2
        ping = new APICommand(PING, "v2");
        ping = ping.addRaw("{\"value\": \"hello 2\"}").method(GET);

        rs = factory.process(ping);
        m = rs.getResponse();

        Assertions.assertEquals(-1, m.code); // Yes!
        Assertions.assertEquals("hello 2 Get", m.message);

        // Ping version 3 (does not exists)
        ping = new APICommand(PING, "v3");
        ping = ping.addRaw("{\"value\": \"hello\"}").method(GET);

        try {

            factory.process(ping);
            Assertions.fail();

        } catch (IllegalArgumentException ex) {

            Assertions.assertEquals("Version 'v3' for a command 'ping' does not exist", ex.getMessage());
            rs = factory.error(ping, ex);

            Assertions.assertEquals("{\"code\":1,\"message\":\"Version 'v3' for a command 'ping' does not exist\"}",
                    rs.getRawModel());
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

        Assertions.assertEquals(0, m.code);
        Assertions.assertEquals("hello x GET", m.message);
    }

    /**
     * Use case : Ping command
     */
    @Test
    public void testError() {

        APICommand e = new APICommand("Error", "v1");
        e = e.method(GET);

        APICommand rs;
        try {
            factory.process(e);
            Assertions.fail();
        } catch (APIException ex) {

            Assertions.assertEquals("Exception", ex.getMessage());
            rs = factory.error(e, ex);

            Assertions.assertEquals("{\"code\":5,\"message\":\"Exception\"}", rs.getRawModel());
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

        Assertions.assertEquals(0, m.code);
    }

    /**
     * Use case : A command with a deliberately set code value to prevent its generation.
     */
    @Test
    public void testEmptyContentCommandWithResettingCode() {

        APICommand ping = new APICommand("Empty", "v1");
        ping.setRequestFormat(null);
        ping.setResponseFormat(null);

        ping = ping.method(GET);
        ping.setRequest(new RequestModel());
        ping.getRequest().code = Integer.MAX_VALUE;

        APICommand rs = factory.process(ping);
        ResponseModel m = rs.getResponse();

        Assertions.assertNull(m.code);
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
        Assertions.assertEquals("hello GET", m.message);

        // PUT
        ping = new APICommand(PING, "v1");
        ping = ping.addRaw("{\"value\": \"hello\"}").method("PUT");

        rs = factory.process(ping);
        m = rs.getResponse();
        Assertions.assertEquals("hello PUT", m.message);

        // POST
        ping = new APICommand(PING, "v1");
        ping = ping.addRaw("{\"value\": \"hello\"}").method("POST");

        rs = factory.process(ping);
        m = rs.getResponse();
        Assertions.assertEquals("hello POST", m.message);

        // DELETE
        ping = new APICommand(PING, "v1");
        ping = ping.addRaw("{\"value\": \"hello\"}").method("DELETE");

        rs = factory.process(ping);
        m = rs.getResponse();
        Assertions.assertEquals("hello DELETE", m.message);
    }

    /**
     * Use case : command with constraint violation inside
     */
    @Test
    public void testConstraintViolationException() {

        APICommand ping = new APICommand("Constraint.Violation", "v1");
        ping = ping.addRaw("{\"value\": \"hello\"}").method(GET).context("x", 25);

        try {
            factory.process(ping);
            Assertions.fail();
        } catch (Exception ex) {

            APICommand rs = factory.error(ex);
            Assertions.assertEquals("{\"code\":-1,\"message\":\"The value is expected to be NOT NULL\"}",
                    rs.getRawModel());
        }
    }

    @Test
    public void testAPIInfoExtraction() {
        ApiStrategy api = ApiUtils.extract(EmptyV1ApiCommand.class);
        Assertions.assertEquals("Empty", api.id());
        Assertions.assertEquals("v1", api.version());
    }
}
