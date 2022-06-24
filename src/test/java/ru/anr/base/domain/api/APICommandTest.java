package ru.anr.base.domain.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.anr.base.BaseParent;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.SortModel;
import ru.anr.base.domain.api.models.SortModel.SortDirection;

import java.time.LocalDateTime;
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
        Assertions.assertEquals(64, cmd.getRequest().page);
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

        cmd = new APICommand(USERS, VER).method("PATCH");
        Assertions.assertEquals(MethodTypes.Patch, cmd.getType());

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
            Assertions.assertEquals("API Method is null", ex.getMessage());
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

        Assertions.assertEquals(100, m.page.intValue());
        Assertions.assertEquals(75, m.perPage.intValue());
        Assertions.assertEquals("xxx", m.search);
        Assertions.assertEquals(list("id", "version"), m.fields);

        List<SortModel> sorts = m.sorted;
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

        Assertions.assertNull(m.page);
        Assertions.assertNull(m.perPage);
        Assertions.assertNull(m.fields);
        Assertions.assertNull(m.sorted);
        Assertions.assertNull(m.search);

        /*
         * Missing cases
         */
        map = toMap("page", "xwlejkdr4", "per_page", "dd", "fields", ",,,,,,", "sort", "!");
        m = cmd.params(map).getRequest();

        Assertions.assertNull(m.page);
        Assertions.assertNull(m.perPage);
        Assertions.assertTrue(isEmpty(m.fields));
        Assertions.assertEquals(list(), m.sorted);

        /*
         * One field
         */
        m = cmd.params(toMap("fields", "xx")).getRequest();
        Assertions.assertEquals(list("xx"), m.fields);

        // comma in the end
        m = cmd.params(toMap("fields", "yy,")).getRequest();
        Assertions.assertEquals(list("yy"), m.fields);

        m = cmd.params(toMap("fields", "yy,,,zz")).getRequest();
        Assertions.assertEquals(list("yy", "zz"), m.fields);

        /*
         * Sorting details
         */
        m = cmd.params(toMap("sort", "x,z")).getRequest();
        Assertions.assertEquals(list(new SortModel("x", SortDirection.ASC), new SortModel("z", SortDirection.ASC)), m.sorted);

        m = cmd.params(toMap("sort", "+x,-z")).getRequest();
        Assertions.assertEquals(list(new SortModel("x", SortDirection.ASC), new SortModel("z", SortDirection.DESC)),
                m.sorted);

        Map<String, SortDirection> rs = cmd.findSorting(SortDirection.DESC, "x", "z", "y");
        Assertions.assertEquals(SortDirection.ASC, rs.get("x"));
        Assertions.assertEquals(SortDirection.DESC, rs.get("z"));
        Assertions.assertEquals(SortDirection.DESC, rs.get("y"));

        // Dates
        map = toMap("created", "2020-05-14", "modified", "2020-22-11");
        cmd.params(map).getRequest();

        LocalDateTime z = LocalDateTime.of(2020, 5, 16, 0, 0);

        Assertions.assertEquals(LocalDateTime.of(2020, 5, 14, 0, 0),
                cmd.parseDate("created", now().toLocalDate()));
        Assertions.assertEquals(z, cmd.parseDate("modified", z.toLocalDate()));

        // If '+' was lost
        m = cmd.params(toMap("sort", " x,-z")).getRequest();
        Assertions.assertEquals(list(new SortModel("x", SortDirection.ASC), new SortModel("z", SortDirection.DESC)),
                m.sorted);
    }

    /**
     * Use case: retrieve context parameters
     */
    @Test
    public void testCommandParameters() {

        APICommand cmd = new APICommand("main", "v5")
                .context("id", 100L, "value", "xxx")
                .params(toMap("value", "yyy", "field", "name"));

        Assertions.assertTrue(cmd.hasParam("id"));
        Assertions.assertTrue(cmd.hasParam("value"));
        Assertions.assertTrue(cmd.hasParam("field"));

        Assertions.assertEquals(100L, (Long) cmd.get("id"));
        Assertions.assertEquals("yyy", cmd.get("value")); // overridden
        Assertions.assertEquals("name", cmd.get("field"));
    }
}
