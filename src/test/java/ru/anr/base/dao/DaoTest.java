package ru.anr.base.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import ru.anr.base.samples.dao.MyDao;
import ru.anr.base.samples.domain.Samples;

import java.util.List;

/**
 * Some simple tests
 *
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 */
public class DaoTest extends AbstractDaoTestCase {

    /**
     * Dao ref
     */
    @Autowired
    @Qualifier("mydao")
    protected MyDao<Samples> mydao;

    /**
     * Query tests
     */
    @Test
    public void testDao() {

        Samples s = newSample("yyy");

        List<Samples> rs = mydao.findCachedSamples();
        Assertions.assertFalse(rs.isEmpty());

        mydao.updateSamples(s.getId());

        mydao.refresh(s);
        Assertions.assertEquals("xxx", s.getName());
    }

    /**
     * Tests for
     * {@link ru.anr.base.dao.repository.BaseRepository#find(Class, Long)}
     */
    @Test
    public void testFindMethod() {

        Samples s = newSample("xx");
        Samples x = mydao.find(Samples.class, s.getId());

        Assertions.assertEquals(s, x);
    }

    /**
     * Understanding of paging
     */
    @Test
    public void testPages() {

        Samples s1 = newSample("01");
        Samples s2 = newSample("02");
        Samples s3 = newSample("03");
        Samples s4 = newSample("04");
        Samples s5 = newSample("05");

        Pageable p = PageRequest.of(0, 2);
        Page<Samples> page = mydao.pages(p);

        // Page 0
        Assertions.assertEquals(3, page.getTotalPages());
        Assertions.assertEquals(5, page.getTotalElements());

        Assertions.assertEquals(0, page.getNumber());
        Assertions.assertEquals(2, page.getNumberOfElements());

        List<Samples> rs = page.getContent();
        Assertions.assertTrue(rs.contains(s1));
        Assertions.assertTrue(rs.contains(s2));

        page = mydao.pages(page.nextPageable());

        // Page 1
        Assertions.assertEquals(3, page.getTotalPages());
        Assertions.assertEquals(5, page.getTotalElements());

        Assertions.assertEquals(1, page.getNumber());
        Assertions.assertEquals(2, page.getNumberOfElements());

        rs = page.getContent();
        Assertions.assertTrue(rs.contains(s3));
        Assertions.assertTrue(rs.contains(s4));

        page = mydao.pages(page.nextPageable());

        // Page 2
        Assertions.assertEquals(3, page.getTotalPages());
        Assertions.assertEquals(5, page.getTotalElements());

        Assertions.assertEquals(2, page.getNumber());
        Assertions.assertEquals(1, page.getNumberOfElements());

        rs = page.getContent();
        Assertions.assertTrue(rs.contains(s5));

        // Once more
        page = mydao.pages(page.nextPageable());
        Assertions.assertTrue(page.hasContent());
        Assertions.assertFalse(page.hasNext());

        rs = page.getContent();
        Assertions.assertTrue(rs.contains(s5)); // the same page
    }

    /**
     * Understanding of sorting
     */
    @Test
    public void testPagesSortOrders() {

        Samples s1 = newSample("01");
        Samples s2 = newSample("02");
        Samples s3 = newSample("03");
        Samples s4 = newSample("04");
        Samples s5 = newSample("05");

        Pageable p = PageRequest.of(0, 3, Direction.DESC, "name");
        Page<Samples> page = mydao.pages(p);

        // Page 0
        Assertions.assertEquals(2, page.getTotalPages());
        Assertions.assertEquals(5, page.getTotalElements());

        Assertions.assertEquals(0, page.getNumber());
        Assertions.assertEquals(3, page.getNumberOfElements());

        List<Samples> rs = page.getContent();
        Assertions.assertEquals(s5, rs.get(0));
        Assertions.assertEquals(s4, rs.get(1));
        Assertions.assertEquals(s3, rs.get(2));

        page = mydao.pages(page.nextPageable());

        // Page 1
        Assertions.assertEquals(2, page.getTotalPages());
        Assertions.assertEquals(5, page.getTotalElements());

        Assertions.assertEquals(1, page.getNumber());
        Assertions.assertEquals(2, page.getNumberOfElements());

        rs = page.getContent();
        Assertions.assertEquals(s2, rs.get(0));
        Assertions.assertEquals(s1, rs.get(1));

        // Changing order
        page = mydao.pages(PageRequest.of(1, 3, Direction.ASC, "name"));

        Assertions.assertEquals(2, page.getTotalPages());
        Assertions.assertEquals(5, page.getTotalElements());

        Assertions.assertEquals(1, page.getNumber());
        Assertions.assertEquals(2, page.getNumberOfElements());

        rs = page.getContent();
        Assertions.assertEquals(s4, rs.get(0));
        Assertions.assertEquals(s5, rs.get(1));
    }

    /**
     * Query tests
     */
    @Test
    public void testRawQueries() {

        Samples s = newSample("yyy");

        List<Samples> rs = mydao.query("from Samples where id = ?1", s.getId());
        Assertions.assertEquals(1, rs.size());

        Assertions.assertEquals(s, rs.get(0));

        rs = mydao.query("from Samples where id = 0");
        Assertions.assertEquals(0, rs.size());
    }

    /**
     * Entity class
     */
    @Test
    public void testLazyClass() {

        Samples p = newSample("yyy");
        Samples s = newSample("yyy");
        s.setParent(p);
        s = mydao.save(s);

        Samples x = mydao.getById(s.getId());
        // TODO: can't create a HibernateProxy
        Assertions.assertEquals(Samples.class, entityClass(x.getParent()));
    }

    /**
     * Execute Query
     */
    @Test
    public void testExecuteQuery() {

        Samples s = newSample("yyy");
        s = mydao.save(s);

        // Update it
        Assertions.assertEquals(1, mydao.execute("update Samples set name = ?1", "zzz"));

        Samples x = mydao.getById(s.getId());
        mydao.refresh(x);

        Assertions.assertEquals("zzz", x.getName());
    }
}
