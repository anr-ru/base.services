/**
 * 
 */
package ru.anr.base.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.anr.base.samples.domain.Samples;

/**
 * Some simple tests
 * 
 * 
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 * 
 */
@Ignore
public class CacheTest extends AbstractDaoTestCase {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(CacheTest.class);

    /**
     * Cached queries tests
     */
    @Test
    public void testCachedQuery() {

        newSample("yyy");

        List<Samples> rs = dao.findCachedSamples();
        Assert.assertNotNull(rs);

        rs = dao.findCachedSamples();
        Assert.assertNotNull(rs);
        rs = dao.findCachedSamples();

        Map<String, QueryStatistics> map = getQueryStats();
        logger.debug("Query statistic: {}", map);

        logger.info("RS: {}", rs);

        QueryStatistics s1 = map.get("From Samples");
        Assert.assertTrue(s1.getExecutionCount() > 0); // It's executed
        Assert.assertTrue(s1.getCacheHitCount() > 0); // at least one hit
    }

    /**
     * No cache
     */
    @Test
    public void testUnCachedQuery() {

        Samples s = new Samples();
        s.setName("yyy");
        s = dao.saveAndFlush(s);

        List<Samples> rs = dao.findSamples("xxx");
        Assert.assertNotNull(rs);

        rs = dao.findSamples("xxx");
        Assert.assertNotNull(rs);

        Map<String, QueryStatistics> map = getQueryStats();
        logger.debug("Query statistic: {}", map);

        QueryStatistics s2 = map.get("From Samples where name like :x");
        Assert.assertTrue(s2.getExecutionCount() > 0); // It's executed
        Assert.assertTrue(s2.getCacheHitCount() == 0); // no one in cache
        Assert.assertTrue(s2.getCachePutCount() == 0);
    }

    /**
     * @return Hibernate Query Cache statistics
     */
    private Map<String, QueryStatistics> getQueryStats() {

        Session ss = (Session) em.getDelegate();
        ss.getSessionFactory().getStatistics().logSummary();

        Statistics stats = ss.getSessionFactory().getStatistics();
        logger.debug("Cached queries: {}", list(stats.getQueries()));

        Map<String, QueryStatistics> map = new HashMap<>();

        for (String q : stats.getQueries()) {
            map.put(q, stats.getQueryStatistics(q));
        }
        return map;
    }

}
