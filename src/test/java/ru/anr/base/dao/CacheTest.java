package ru.anr.base.dao;

import org.hibernate.Session;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.anr.base.samples.dao.MyDao;
import ru.anr.base.samples.domain.Samples;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Some simple tests
 *
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 */
@Disabled
public class CacheTest extends AbstractDaoTestCase {

    private static final Logger logger = LoggerFactory.getLogger(CacheTest.class);

    @Autowired
    protected MyDao mydao;

    /**
     * Cached queries tests
     */
    @Test
    public void testCachedQuery() {

        newSample("yyy");

        List<Samples> rs = mydao.findCachedSamples();
        Assertions.assertNotNull(rs);

        rs = mydao.findCachedSamples();
        Assertions.assertNotNull(rs);
        rs = mydao.findCachedSamples();

        Map<String, QueryStatistics> map = getQueryStats();
        logger.debug("Query statistic: {}", map);
        logger.debug("RS: {}", rs);

        QueryStatistics s1 = map.get("From Samples");
        Assertions.assertTrue(s1.getExecutionCount() > 0); // It's executed
        Assertions.assertTrue(s1.getCacheHitCount() > 0); // at least one hit
    }

    /**
     * No cache
     */
    @Test
    public void testUnCachedQuery() {

        Samples s = new Samples();
        s.setName("yyy");
        mydao.saveAndFlush(s);

        List<Samples> rs = mydao.findSamples("xxx");
        Assertions.assertNotNull(rs);

        rs = mydao.findSamples("xxx");
        Assertions.assertNotNull(rs);

        Map<String, QueryStatistics> map = getQueryStats();
        logger.debug("Query statistic: {}", map);

        QueryStatistics s2 = map.get("From Samples where name like :x");
        Assertions.assertTrue(s2.getExecutionCount() > 0); // It's executed
        Assertions.assertEquals(0, s2.getCacheHitCount()); // no one in cache
        Assertions.assertEquals(0, s2.getCachePutCount());
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
