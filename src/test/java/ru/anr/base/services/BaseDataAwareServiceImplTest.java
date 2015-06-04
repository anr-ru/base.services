/**
 * 
 */
package ru.anr.base.services;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;

import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.samples.domain.Samples;

/**
 * Tests for {@link BaseDataAwareServiceImpl}.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 *
 */
public class BaseDataAwareServiceImplTest extends BaseLocalServiceTestCase {

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

        BaseDataAwareServiceImpl impl = target(service);

        Samples s = new Samples();
        s.setName("xxx");
        s.changeState("New");

        s = impl.dao().save(s);
        Assert.assertNotNull(s.getId());

        BaseRepository<Samples> d = impl.dao();

        Samples e = d.findOne(s.getId());
        Assert.assertEquals(s, e);
    }

    /**
     * Testing injection for not existing bean
     */
    @Autowired(required = false)
    @Qualifier("SomeUnknownRepository")
    private BaseRepository<BaseEntity> bean;

    /**
     * 
     */
    @Autowired
    @Qualifier("TestDataService")
    private BaseDataAwareService testService;

    /**
     * Check dao access methods
     */
    @Test
    public void testAutowired() {

        BaseDataAwareServiceImpl impl = target(testService);
        Assert.assertNotNull(impl.dao());

        Assert.assertNull(bean);
    }

    /**
     * Test method for {@link BaseDataAwareService#save(BaseEntity)}
     */
    @Test
    public void testSaveEntity() {

        authenticate(new TestingAuthenticationToken("test", "password", "ROLE_USER"));

        Samples e = new Samples();
        e.setName("write");

        Samples ex = testService.save(e);

        Assert.assertEquals(e, ex);
        Assert.assertNotNull(ex.getId());

        ex.setName("read");

        try {
            testService.save(ex);
            Assert.fail();
        } catch (AccessDeniedException exp) {
            Assert.assertEquals("Access is denied", exp.getMessage());
        }

        e = new Samples();
        e.setName("read");

        try {
            logger.debug("Started");
            testService.save(e);
            Assert.fail();
        } catch (AccessDeniedException exp) {
            Assert.assertEquals("Access is denied", exp.getMessage());
        }

    }

}
