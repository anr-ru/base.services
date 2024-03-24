/*
 * Copyright 2014-2024 the original author or authors.
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

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;

import javax.persistence.EntityManager;

/**
 * An implementation of our own repository factory.
 *
 * @author Alexey Romanchuk
 * @created Aug 30, 2021
 */
public class InternalRepositoryFactory<T extends BaseEntity> extends JpaRepositoryFactory {

    /**
     * A package-related constructor
     *
     * @param entityManager {@link EntityManager} from config
     */
    InternalRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                                                                    EntityManager entityManager) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) information.getDomainType();
        return new BaseRepositoryImpl<>(clazz, entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {

        // The RepositoryMetadata can be safely ignored, it is used by the
        // JpaRepositoryFactory
        // to check for QueryDslJpaRepository's which is out of scope.
        return BaseRepository.class;
    }
}
