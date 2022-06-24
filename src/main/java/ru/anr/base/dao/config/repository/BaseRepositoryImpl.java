/*
 * Copyright 2014-2022 the original author or authors.
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
package ru.anr.base.dao.config.repository;

import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import ru.anr.base.BaseParent;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * A custom implementation for DAO Repositories. Added some useful functions
 * from {@link EntityManager}.
 *
 * @param <T> Entity Type
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 */
public class BaseRepositoryImpl<T extends BaseEntity> extends SimpleJpaRepository<T, Long>
        implements BaseRepository<T> {

    private final EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(BaseRepositoryImpl.class);

    /**
     * The constructor of repository instances
     *
     * @param domainClass   The entity class
     * @param entityManager {@link EntityManager}
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
    public <S> List<S> query(String queryStr, Object... params) {

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

    @Override
    @SuppressWarnings("unchecked")
    public <S extends BaseEntity> S findSecured(Class<?> entityClass, Long id) {
        return (S) entityManager.find(entityClass, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends BaseEntity> List<S> filter(Page<S> page) {
        // Disclaimer: the original list does not allow any modifications
        return BaseParent.list(page.getContent());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> executeSQLQuery(String sql, Pageable page, Map<String, Object> params) {

        if (logger.isDebugEnabled()) {
            logger.debug("SQL: {}, params: {}", sql, params);
        }

        Query query = entityManager.createNativeQuery(sql);

        if (params != null) {
            for (Parameter<?> qp : query.getParameters()) {
                if (params.containsKey(qp.getName())) {
                    query.setParameter(qp.getName(), params.get(qp.getName()));
                }
            }
        }

        if (page != null) {
            query.setFirstResult((int) page.getOffset());
            query.setMaxResults(page.getPageSize());
        }
        /*
         * Convert the results to a map.
         */
        org.hibernate.query.Query<?> q = query.unwrap(org.hibernate.query.Query.class);
        q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        q.setReadOnly(true);

        return (List<Map<String, Object>>) query.getResultList();
    }

    @Override
    public int execute(String queryStr, Object... params) {

        Query q = entityManager.createQuery(queryStr);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                q.setParameter(i + 1, params[i]);
            }
        }
        return q.executeUpdate();
    }
}
