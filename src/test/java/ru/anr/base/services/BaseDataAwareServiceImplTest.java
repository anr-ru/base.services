/**
 * 
 */
package ru.anr.base.services;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ru.anr.base.samples.dao.MyDao;
import ru.anr.base.samples.domain.Samples;

/**
 * Tests for {@link BaseDataAwareServiceImpl}.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 *
 */
public class BaseDataAwareServiceImplTest extends BaseServiceTestCase {

    /**
     * 
     */
    @Autowired
    @Qualifier("baseData")
    private BaseService service;

    /**
     * Check dao access methods
     */
    @Test
    public void testDaoAccess() {

        BaseDataAwareServiceImpl impl = (BaseDataAwareServiceImpl) target(service);

        Samples s = new Samples();
        s.setName("xxx");
        s.changeState("New");

        s = impl.dao().save(s);
        Assert.assertNotNull(s.getId());

        MyDao<Samples> d = impl.dao();
        Assert.assertEquals(s, d.findOne(s.getId()));
    }
}
