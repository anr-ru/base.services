package ru.anr.base.domain.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.anr.base.BaseParent;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.SortModel;
import ru.anr.base.domain.api.models.SortModel.SortDirection;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Testing a command builder
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
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

        Assertions.assertEquals("{}", cmd.getRawModel());
        Assertions.assertEquals(RawFormatTypes.JSON, cmd.getRequestFormat());
        Assertions.assertEquals(RawFormatTypes.XML, cmd.getResponseFormat());

        Assertions.assertEquals(VER, cmd.getVersion());
        Assertions.assertEquals(MethodTypes.Post, cmd.getType());

        Assertions.assertEquals(USERS, cmd.getCommandId());
        Assertions.assertEquals(100L, cmd.getContexts().get("id"));
        Assertions.assertNotNull(cmd.getRequest());
        Assertions.assertEquals(64, cmd.getRequest().getPage());
    }

    /**
     * Tests for builder for method type
     */
    @Test
    public void testCommandBuilderForType() {

        APICommand cmd = new APICommand(USERS, VER).method("GET");
        Assertions.assertEquals(MethodTypes.Get, cmd.getType());

        cmd = new APICommand(USERS, VER).method("PUT");
        Assertions.assertEquals(MethodTypes.Put, cmd.getType());

        cmd = new APICommand(USERS, VER).method("DELETE");
        Assertions.assertEquals(MethodTypes.Delete, cmd.getType());

        try {
            new APICommand(USERS, VER).method("XXX");
            Assertions.fail();
        } catch (UnsupportedOperationException ex) {
            Assertions.assertEquals("XXX", ex.getMessage());
        }

        try {
            new APICommand(USERS, VER).method(null);
            Assertions.fail();
        } catch (Exception ex) {
            Assertions.assertEquals("Method is null", ex.getMessage());
        }

        try {
            new APICommand(USERS, VER).method("");
            Assertions.fail();
        } catch (UnsupportedOperationException ex) {
            Assertions.assertEquals("", ex.getMessage());
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

        Assertions.assertEquals(100, m.getPage().intValue());
        Assertions.assertEquals(75, m.getPerPage().intValue());
        Assertions.assertEquals("xxx", m.getSearch());
        Assertions.assertEquals(list("id", "version"), m.getFields());

        List<SortModel> sorts = m.getSorted();
        Assertions.assertEquals(2, sorts.size());
        Assertions.assertEquals("id", sorts.get(0).getField());
        Assertions.assertEquals(SortModel.SortDirection.ASC, sorts.get(0).getDirection());

        Assertions.assertEquals("value", sorts.get(1).getField());
        Assertions.assertEquals(SortModel.SortDirection.DESC, sorts.get(1).getDirection());

        /*
         * Missing cases
         */
        map = toMap();
        m = cmd.params(map).getRequest();

        Assertions.assertNull(m.getPage());
        Assertions.assertNull(m.getPerPage());
        Assertions.assertNull(m.getFields());
        Assertions.assertNull(m.getSorted());
        Assertions.assertNull(m.getSearch());

        /*
         * Missing cases
         */
        map = toMap("page", "xwlejkdr4", "per_page", "dd", "fields", ",,,,,,", "sort", "!");
        m = cmd.params(map).getRequest();

        Assertions.assertNull(m.getPage());
        Assertions.assertNull(m.getPerPage());
        Assertions.assertNull(m.getFields());
        Assertions.assertEquals(list(), m.getSorted());

        /*
         * One field
         */
        m = cmd.params(toMap("fields", "xx")).getRequest();
        Assertions.assertEquals(list("xx"), m.getFields());

        // comma in the end
        m = cmd.params(toMap("fields", "yy,")).getRequest();
        Assertions.assertEquals(list("yy"), m.getFields());

        m = cmd.params(toMap("fields", "yy,,,zz")).getRequest();
        Assertions.assertEquals(list("yy", "zz"), m.getFields());

        /*
         * Sorting details
         */
        m = cmd.params(toMap("sort", "x,z")).getRequest();
        Assertions.assertEquals(list(), m.getSorted());

        m = cmd.params(toMap("sort", "+x,-z")).getRequest();
        Assertions.assertEquals(list(new SortModel("x", SortDirection.ASC), new SortModel("z", SortDirection.DESC)),
                m.getSorted());

        Map<String, SortDirection> rs = cmd.findSorting(SortDirection.DESC, "x", "z", "y");
        Assertions.assertEquals(SortDirection.ASC, rs.get("x"));
        Assertions.assertEquals(SortDirection.DESC, rs.get("z"));
        Assertions.assertEquals(SortDirection.DESC, rs.get("y"));

        // Dates
        map = toMap("created", "2020-05-14", "modified", "2020-22-11");
        m = cmd.params(map).getRequest();

        ZonedDateTime z = ZonedDateTime.of(2020, 5, 16, 0, 0, 0, 0, DEFAULT_TIMEZONE);

        Assertions.assertEquals(ZonedDateTime.of(2020, 5, 14, 0, 0, 0, 0, DEFAULT_TIMEZONE),
                cmd.parseDate("created", now()));
        Assertions.assertEquals(z, cmd.parseDate("modified", z));

        // If '+' was lost
        m = cmd.params(toMap("sort", " x,-z")).getRequest();
        Assertions.assertEquals(list(new SortModel("x", SortDirection.ASC), new SortModel("z", SortDirection.DESC)),
                m.getSorted());
    }
}
