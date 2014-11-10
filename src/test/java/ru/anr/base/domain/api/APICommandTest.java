/**
 * 
 */
package ru.anr.base.domain.api;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ru.anr.base.BaseParent;

/**
 * Testing a command builder
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class APICommandTest extends BaseParent {

    /**
     * Users contant
     */
    private static final String USERS = "users";

    /**
     * Version constant
     */
    private static final String VER = "v1";

    /**
     * Tests for builder
     */
    @Test
    public void testCommandBuilder() {

        Map<String, Long> ids = new HashMap<>();
        ids.put("id", 100L);

        APICommand cmd =
                new APICommand(USERS, VER).responseFormat(RawFormatTypes.XML).method("POST").context(ids)
                        .params(new HashMap<>()).addRaw("{}");

        Assert.assertEquals("{}", cmd.getRawModel());
        Assert.assertEquals(RawFormatTypes.JSON, cmd.getRequestFormat());
        Assert.assertEquals(RawFormatTypes.XML, cmd.getResponseFormat());

        Assert.assertEquals(VER, cmd.getVersion());
        Assert.assertEquals(MethodTypes.Create, cmd.getType());

        Assert.assertEquals(USERS, cmd.getCommandId());
        Assert.assertEquals(100L, cmd.getContexts().get("id"));
        Assert.assertNotNull(cmd.getRequest());
    }

    /**
     * Tests for builder for method type
     */
    @Test
    public void testCommandBuilderForType() {

        APICommand cmd = new APICommand(USERS, VER).method("GET");
        Assert.assertEquals(MethodTypes.Get, cmd.getType());

        cmd = new APICommand(USERS, VER).method("PUT");
        Assert.assertEquals(MethodTypes.Modify, cmd.getType());

        cmd = new APICommand(USERS, VER).method("DELETE");
        Assert.assertEquals(MethodTypes.Delete, cmd.getType());

        try {
            new APICommand(USERS, VER).method("XXX");
            Assert.fail();
        } catch (UnsupportedOperationException ex) {
            Assert.assertEquals("XXX", ex.getMessage());
        }

        try {
            new APICommand(USERS, VER).method(null);
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertEquals("Method is null", ex.getMessage());
        }

        try {
            new APICommand(USERS, VER).method("");
            Assert.fail();
        } catch (UnsupportedOperationException ex) {
            Assert.assertEquals("", ex.getMessage());
        }

    }

}
