/**
 *
 */
package ru.anr.base.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.tests.BaseLocalDaoTestCase;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Abstract parent for different tests.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */
@ActiveProfiles(value = "test")
public abstract class AbstractDaoTestCase extends BaseLocalDaoTestCase {

    /**
     * Dao ref
     */
    @Autowired
    @Qualifier("sampledao")
    protected BaseRepository<Samples> sampleDao;

    /**
     * Creation of new sample object
     *
     * @param name
     *            Name param
     * @return Object instance stored in database
     */
    protected Samples newSample(String name) {

        Samples s = new Samples();
        s.setName(name);

        return sampleDao.saveAndFlush(s);
    }

    /**
     * Injection of EntityManager directly to perform some specific internal
     * ops.
     */
    @PersistenceContext(unitName = "TestUnitLocal")
    protected EntityManager em;
}
