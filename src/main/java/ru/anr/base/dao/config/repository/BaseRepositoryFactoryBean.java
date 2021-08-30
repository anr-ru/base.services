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

import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;

import javax.persistence.EntityManager;

/**
 * A factory bean for creation of a custom repository instance. This code is
 * taken from Spring Data Reference Docs.
 *
 * @param <T> Type of Entity
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 */

public class BaseRepositoryFactoryBean<T extends BaseEntity>
        extends JpaRepositoryFactoryBean<BaseRepository<T>, T, Long> {

    /**
     * Creates a new {@link BaseRepositoryFactoryBean} for the given repository
     * interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public BaseRepositoryFactoryBean(Class<? extends BaseRepository<T>> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new InternalRepositoryFactory<T>(entityManager);
    }
}
