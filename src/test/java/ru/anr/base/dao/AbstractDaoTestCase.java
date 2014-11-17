/**
 * 
 */
package ru.anr.base.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import ru.anr.base.samples.domain.Samples;
import ru.anr.base.tests.BaseTestCase;

/**
 * Abstract parent for different tests.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */
@ActiveProfiles(value = "test")
@ContextConfiguration(locations = "classpath:/tests-dao-context.xml")
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional
public abstract class AbstractDaoTestCase extends BaseTestCase {

    /**
     * Dao ref
     */
    @Autowired
    @Qualifier("sampledao")
    protected BaseRepository<Samples> dao;

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

        return dao.saveAndFlush(s);
    }

    /**
     * Injection of EntityManager directly to perform some specific internal
     * ops.
     */
    @PersistenceContext(unitName = "TestUnitLocal")
    protected EntityManager em;
}
