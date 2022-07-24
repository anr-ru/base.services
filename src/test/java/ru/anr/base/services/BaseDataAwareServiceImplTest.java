package ru.anr.base.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.samples.services.TestDataService;

/**
 * Tests for {@link BaseDataAwareServiceImpl}.
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 */
public class BaseDataAwareServiceImplTest extends BaseLocalServiceTestCase {

    @Autowired
    @Qualifier("baseData")
    private BaseService service;

    /**
     * Check dao access methods
     */
    @Test
    public void testDaoAccess() {

        authenticate(new TestingAuthenticationToken("test", "password", "ROLE_USER"));

        BaseDataAwareServiceImpl impl = target(service);

        Samples s = new Samples();
        s.setName("read");
        s.changeState("New");

        s = impl.dao().save(s);
        Assertions.assertNotNull(s.getId());

        BaseRepository<Samples> d = impl.dao();

        Samples e = d.find(Samples.class, s.getId());
        Assertions.assertEquals(s, e);
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
        Assertions.assertNotNull(impl.dao());

        Assertions.assertNull(bean);
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

        Assertions.assertEquals(e, ex);
        Assertions.assertNotNull(ex.getId());

        ex.setName("read");

        try {
            testService.save(ex);
            Assertions.fail();
        } catch (AccessDeniedException exp) {
            Assertions.assertEquals("Access Denied", exp.getMessage());
        }

        e = new Samples();
        e.setName("read");

        try {
            logger.debug("Started");
            testService.save(e);
            Assertions.fail();
        } catch (AccessDeniedException exp) {
            Assertions.assertEquals("Access Denied", exp.getMessage());
        }

    }

    @Autowired
    @Qualifier("TestDataService")
    private TestDataService testData;

    /**
     * Tests for reload
     */
    @Test
    //@Rollback(false)
    public void testReload() {

        authenticate(new TestingAuthenticationToken("test", "password", "ROLE_USER"));

        Samples e = dao.save(new Samples());
        Assertions.assertEquals(e, testService.reload(e));

        Samples e1 = testData.doInTransaction(e, false);
        Assertions.assertNotEquals(e, e1);
        Samples e2 = testData.doInTransaction(e, true);
        Assertions.assertNotEquals(e, e2);

        dao.delete(e);
        // Have to do it in a new transaction as the objects are already in the database
        testData.deleteInTransaction(e1);
        testData.deleteInTransaction(e2);
    }
}
