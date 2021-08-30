/**
 *
 */
package ru.anr.base.tests;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.anr.base.ApplicationException;
import ru.anr.base.dao.EntityUtils;
import ru.anr.base.dao.config.repository.BaseRepositoryImpl;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Base parent for all local tests.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 27, 2014
 *
 */
@ContextConfiguration(value = "classpath:/tests-dao-context.xml", inheritLocations = false)
@Disabled
@Transactional(transactionManager = "transactionManager")
public class BaseLocalDaoTestCase extends BaseTestCase {

    /**
     * Base dao ref
     */
    @Autowired
    @Qualifier("BaseRepository")
    protected BaseRepository<BaseEntity> dao;

    /**
     * Creation of sample entity with specified list of property name/value
     * pairs
     *
     * @param entityClass
     *            Entity class to create
     * @param propsAndValues
     *            List of property name/value paits, like: "login",
     *            "alex","weight", 100
     * @return Entity instance, stored in database
     *
     * @param <S>
     *            Entity class
     */
    protected <S extends BaseEntity> S create(Class<S> entityClass, Object... propsAndValues) {

        Map<String, Object> map = toMap(propsAndValues);

        S entity = inst(entityClass, new Class<?>[]{}, new Object[]{});

        for (Entry<String, Object> e : map.entrySet()) {
            try {

                PropertyUtils.setProperty(entity, e.getKey(), e.getValue());

            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new ApplicationException(ex);
            }
        }
        return dao.save(entity);
    }

    /**
     * A short-cut method for {@link BaseRepositoryImpl#entityClass(BaseEntity)}
     *
     *
     * @param e
     *            Some entity
     * @return Entity class
     */
    public static Class<?> entityClass(BaseEntity e) {

        return EntityUtils.entityClass(e);
    }

    /**
     * A short-cut method for {@link EntityUtils#entity(BaseEntity)}
     *
     * @param e
     *            Some entity
     * @return The entity without proxies
     *
     * @param <S>
     *            Entity type to cast to
     */
    public static <S extends BaseEntity> S entity(S e) {
        return EntityUtils.entity(e);
    }

}
