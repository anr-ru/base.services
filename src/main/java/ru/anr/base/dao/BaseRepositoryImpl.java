/**
 * 
 */
package ru.anr.base.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;

/**
 * Custom implementation for Dao Repository. Added some useful functions from
 * {@link EntityManager}.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 * @param <T>
 *            Entity type
 */

public class BaseRepositoryImpl<T extends BaseEntity> extends SimpleJpaRepository<T, Long> implements
        BaseRepository<T> {

    /**
     * Ref to {@link EntityManager}
     */
    private final EntityManager entityManager;

    /**
     * Constructor of repository instance
     * 
     * @param domainClass
     *            Entity class
     * @param entityManager
     *            {@link EntityManager}
     */
    public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {

        super(domainClass, entityManager);

        // This is the recommended method for accessing inherited class
        // dependencies.
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh(T object) {

        entityManager.refresh(object);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <S extends BaseEntity> List<S> query(String queryStr, Object... params) {

        Query q = entityManager.createQuery(queryStr);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                q.setParameter(i + 1, params[i]);
            }
        }
        return q.getResultList();
    }
}
