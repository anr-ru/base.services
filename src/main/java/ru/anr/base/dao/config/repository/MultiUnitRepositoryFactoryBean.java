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
package ru.anr.base.dao.config.repository;

import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.util.Assert;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;

import javax.persistence.EntityManager;

/**
 * A multiUnit version of the repository factory.
 *
 * @param <T> The type of an entity
 * @author Alexey Romanchuk
 * @created Aug 11, 2015
 */

public class MultiUnitRepositoryFactoryBean<T extends BaseEntity>
        extends TransactionalRepositoryFactoryBeanSupport<BaseRepository<T>, T, Long> {

    /**
     * Creates a new {@link MultiUnitRepositoryFactoryBean} for the given
     * repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public MultiUnitRepositoryFactoryBean(Class<? extends BaseRepository<T>> repositoryInterface) {

        super(repositoryInterface);
    }

    /**
     * A reference to the current {@link EntityManager}
     */
    private EntityManager entityManager;

    /**
     * The {@link EntityManager} to be used.
     *
     * @param entityManager the entityManager to set
     */
    public void setEntityManager(EntityManager entityManager) {

        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMappingContext(MappingContext<?, ?> mappingContext) {

        super.setMappingContext(mappingContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {

        return new InternalRepositoryFactory<T>(entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {

        Assert.notNull(entityManager, "EntityManager must not be null!");
        super.afterPropertiesSet();
    }
}
