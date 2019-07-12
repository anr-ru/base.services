/**
 * 
 */
package ru.anr.base.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import ru.anr.base.samples.dao.MyDao;
import ru.anr.base.samples.domain.Samples;

/**
 * Some simple tests
 * 
 * 
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 * 
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
        Assert.assertFalse(rs.isEmpty());

        mydao.updateSamples(s.getId());

        mydao.refresh(s);
        Assert.assertEquals("xxx", s.getName());
    }

    /**
     * Tests for
     * {@link ru.anr.base.dao.repository.BaseRepository#find(Class, Long)}
     */
    @Test
    public void testFindMethod() {

        Samples s = newSample("xx");
        Samples x = mydao.find(Samples.class, s.getId());

        Assert.assertEquals(s, x);
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

        Pageable p = new PageRequest(0, 2);
        Page<Samples> page = mydao.pages(p);

        // Page 0
        Assert.assertEquals(3, page.getTotalPages());
        Assert.assertEquals(5, page.getTotalElements());

        Assert.assertEquals(0, page.getNumber());
        Assert.assertEquals(2, page.getNumberOfElements());

        List<Samples> rs = page.getContent();
        Assert.assertTrue(rs.contains(s1));
        Assert.assertTrue(rs.contains(s2));

        page = mydao.pages(page.nextPageable());

        // Page 1
        Assert.assertEquals(3, page.getTotalPages());
        Assert.assertEquals(5, page.getTotalElements());

        Assert.assertEquals(1, page.getNumber());
        Assert.assertEquals(2, page.getNumberOfElements());

        rs = page.getContent();
        Assert.assertTrue(rs.contains(s3));
        Assert.assertTrue(rs.contains(s4));

        page = mydao.pages(page.nextPageable());

        // Page 2
        Assert.assertEquals(3, page.getTotalPages());
        Assert.assertEquals(5, page.getTotalElements());

        Assert.assertEquals(2, page.getNumber());
        Assert.assertEquals(1, page.getNumberOfElements());

        rs = page.getContent();
        Assert.assertTrue(rs.contains(s5));

        // Once more
        page = mydao.pages(page.nextPageable());
        Assert.assertTrue(page.hasContent());
        Assert.assertFalse(page.hasNext());

        rs = page.getContent();
        Assert.assertTrue(rs.contains(s5)); // the same page
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

        Pageable p = new PageRequest(0, 3, Direction.DESC, "name");
        Page<Samples> page = mydao.pages(p);

        // Page 0
        Assert.assertEquals(2, page.getTotalPages());
        Assert.assertEquals(5, page.getTotalElements());

        Assert.assertEquals(0, page.getNumber());
        Assert.assertEquals(3, page.getNumberOfElements());

        List<Samples> rs = page.getContent();
        Assert.assertEquals(s5, rs.get(0));
        Assert.assertEquals(s4, rs.get(1));
        Assert.assertEquals(s3, rs.get(2));

        page = mydao.pages(page.nextPageable());

        // Page 1
        Assert.assertEquals(2, page.getTotalPages());
        Assert.assertEquals(5, page.getTotalElements());

        Assert.assertEquals(1, page.getNumber());
        Assert.assertEquals(2, page.getNumberOfElements());

        rs = page.getContent();
        Assert.assertEquals(s2, rs.get(0));
        Assert.assertEquals(s1, rs.get(1));

        // Changing order
        page = mydao.pages(new PageRequest(1, 3, Direction.ASC, "name"));

        Assert.assertEquals(2, page.getTotalPages());
        Assert.assertEquals(5, page.getTotalElements());

        Assert.assertEquals(1, page.getNumber());
        Assert.assertEquals(2, page.getNumberOfElements());

        rs = page.getContent();
        Assert.assertEquals(s4, rs.get(0));
        Assert.assertEquals(s5, rs.get(1));
    }

    /**
     * Query tests
     */
    @Test
    public void testRawQueries() {

        Samples s = newSample("yyy");

        List<Samples> rs = mydao.query("from Samples where id = ?1", s.getId());
        Assert.assertEquals(1, rs.size());

        Assert.assertEquals(s, rs.get(0));

        rs = mydao.query("from Samples where id = 0");
        Assert.assertEquals(0, rs.size());
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

        Samples x = mydao.getOne(s.getId());
        // TODO: can't create a HibernateProxy
        Assert.assertEquals(Samples.class, entityClass(x.getParent()));
    }

    /**
     * Execute Query
     */
    @Test
    public void testExecuteQuery() {

        Samples s = newSample("yyy");
        s = mydao.save(s);

        // Update it
        Assert.assertEquals(1, mydao.execute("update Samples set name = ?", "zzz"));

        Samples x = mydao.getOne(s.getId());
        mydao.refresh(x);

        Assert.assertEquals("zzz", x.getName());
    }

}
