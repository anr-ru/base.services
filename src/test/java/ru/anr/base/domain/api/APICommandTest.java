/**
 * 
 */
package ru.anr.base.domain.api;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ru.anr.base.BaseParent;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.SortModel;
import ru.anr.base.domain.api.models.SortModel.SortDirection;

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

        APICommand cmd =
                new APICommand(USERS, VER).responseFormat(RawFormatTypes.XML).method("POST").context("id", 100L)
                        .params(toMap("page", 64)).addRaw("{}");

        Assert.assertEquals("{}", cmd.getRawModel());
        Assert.assertEquals(RawFormatTypes.JSON, cmd.getRequestFormat());
        Assert.assertEquals(RawFormatTypes.XML, cmd.getResponseFormat());

        Assert.assertEquals(VER, cmd.getVersion());
        Assert.assertEquals(MethodTypes.Post, cmd.getType());

        Assert.assertEquals(USERS, cmd.getCommandId());
        Assert.assertEquals(100L, cmd.getContexts().get("id"));
        Assert.assertNotNull(cmd.getRequest());
        Assert.assertEquals(64, cmd.getRequest().getPage().intValue());
    }

    /**
     * Tests for builder for method type
     */
    @Test
    public void testCommandBuilderForType() {

        APICommand cmd = new APICommand(USERS, VER).method("GET");
        Assert.assertEquals(MethodTypes.Get, cmd.getType());

        cmd = new APICommand(USERS, VER).method("PUT");
        Assert.assertEquals(MethodTypes.Put, cmd.getType());

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

    /**
     * Tests for {@link APICommand#params(Map)}
     */
    @Test
    public void testParamsParsing() {

        Map<String, String> map = toMap("id", "64", "page", 100, "per_page", 75, "fields", //
                "id,version", "sort", "+id,-value", "q", "xxx");

        APICommand cmd = new APICommand("1", "1");

        RequestModel m = cmd.params(map).getRequest();

        Assert.assertEquals(100, m.getPage().intValue());
        Assert.assertEquals(75, m.getPerPage().intValue());
        Assert.assertEquals("xxx", m.getSearch());
        Assert.assertEquals(list("id", "version"), m.getFields());

        List<SortModel> sorts = m.getSorted();
        Assert.assertEquals(2, sorts.size());
        Assert.assertEquals("id", sorts.get(0).getField());
        Assert.assertEquals(SortModel.SortDirection.ASC, sorts.get(0).getDirection());

        Assert.assertEquals("value", sorts.get(1).getField());
        Assert.assertEquals(SortModel.SortDirection.DESC, sorts.get(1).getDirection());

        /*
         * Missing cases
         */
        map = toMap();
        m = cmd.params(map).getRequest();

        Assert.assertNull(m.getPage());
        Assert.assertNull(m.getPerPage());
        Assert.assertNull(m.getFields());
        Assert.assertNull(m.getSorted());
        Assert.assertNull(m.getSearch());

        /*
         * Missing cases
         */
        map = toMap("page", "xwlejkdr4", "per_page", "dd", "fields", ",,,,,,", "sort", "!");
        m = cmd.params(map).getRequest();

        Assert.assertNull(m.getPage());
        Assert.assertNull(m.getPerPage());
        Assert.assertNull(m.getFields());
        Assert.assertEquals(list(), m.getSorted());

        /*
         * One field
         */
        m = cmd.params(toMap("fields", "xx")).getRequest();
        Assert.assertEquals(list("xx"), m.getFields());

        // comma in the end
        m = cmd.params(toMap("fields", "yy,")).getRequest();
        Assert.assertEquals(list("yy"), m.getFields());

        m = cmd.params(toMap("fields", "yy,,,zz")).getRequest();
        Assert.assertEquals(list("yy", "zz"), m.getFields());

        /*
         * Sorting details
         */
        m = cmd.params(toMap("sort", "x,z")).getRequest();
        Assert.assertEquals(list(), m.getSorted());

        m = cmd.params(toMap("sort", "+x,-z")).getRequest();
        Assert.assertEquals(list(new SortModel("x", SortDirection.ASC), new SortModel("z", SortDirection.DESC)),
                m.getSorted());

        Map<String, SortDirection> rs = cmd.findSorting(SortDirection.DESC, "x", "z", "y");
        Assert.assertEquals(SortDirection.ASC, rs.get("x"));
        Assert.assertEquals(SortDirection.DESC, rs.get("z"));
        Assert.assertEquals(SortDirection.DESC, rs.get("y"));

        // Dates
        map = toMap("created", "2020-05-14", "modified", "2020-22-11");
        m = cmd.params(map).getRequest();

        ZonedDateTime z = ZonedDateTime.of(2020, 5, 16, 0,0,0,0,DEFAULT_TIMEZONE);

        Assert.assertEquals(ZonedDateTime.of(2020, 5, 14, 0,0,0,0,DEFAULT_TIMEZONE),
                cmd.parseDate("created", now()));
        Assert.assertEquals(z, cmd.parseDate("modified", z));

    }
}
