package ru.anr.base.dao.config.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.anr.base.dao.AbstractDaoTestCase;
import ru.anr.base.samples.domain.Samples;

import java.util.List;
import java.util.Map;

/**
 * Tests for BaseRepository functions.
 */
public class BaseRepositoryImplTest extends AbstractDaoTestCase {

    @Test
    public void testNativeSQL() {

        create(Samples.class, "name", "x1");
        create(Samples.class, "name", "x2");

        // No params
        List<Map<String, Object>> rs = sampleDao.executeSQLQuery("select id,name from samples",
                PageRequest.of(0, 100, Sort.by("name").ascending()), toMap());
        Assertions.assertEquals(2, rs.size());
        Assertions.assertEquals(2, rs.get(0).size());
        Assertions.assertEquals("x1", rs.get(0).get("NAME"));
        Assertions.assertEquals(2, rs.get(1).size());
        Assertions.assertEquals("x2", rs.get(1).get("NAME"));

        // With params
        rs = sampleDao.executeSQLQuery("select id,name from samples where name like :x",
                PageRequest.of(0, 100, Sort.by("name").ascending()), toMap("x", "x1"));
        Assertions.assertEquals(1, rs.size());
        Assertions.assertEquals(2, rs.get(0).size());
        Assertions.assertEquals("x1", rs.get(0).get("NAME"));
    }
}