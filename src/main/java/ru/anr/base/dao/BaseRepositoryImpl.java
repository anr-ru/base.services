/*
 * Copyright 2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.anr.base.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.proxy.HibernateProxy;
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

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <S extends BaseEntity> S find(Class<?> entityClass, Long id) {

        return (S) entityManager.find(entityClass, id);
    }

    /**
     * Extracts entity class for specified entity (which can be a proxied
     * hibernate entity). The implementation is hibernate specific.
     * 
     * @param entity
     *            Entity
     * @return Entity's class
     */
    public static Class<?> entityClass(BaseEntity entity) {

        Object r = entity(entity);
        return r.getClass();
    }

    /**
     * Extracts a pure entity for the specified hibernate entity (which can be a
     * proxied). The implementation is hibernate specific.
     * 
     * @param entity
     *            The entity
     * @return Pure entity
     * 
     * @param <S>
     *            Object type
     */
    @SuppressWarnings("unchecked")
    public static <S extends BaseEntity> S entity(S entity) {

        S r = entity;

        if (entity instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) entity;
            r = (S) proxy.getHibernateLazyInitializer().getImplementation();
        }
        return r;
    }
}
