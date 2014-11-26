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
package ru.anr.base.services;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;

/**
 * A parent class for all business logic services, which need a database access.
 * We suppose the necessery DAO repository was injected.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */
@Transactional(propagation = Propagation.REQUIRED)
public class BaseDataAwareServiceImpl extends BaseServiceImpl {

    /**
     * Base repository
     */
    private BaseRepository<BaseEntity> repository;

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param dao
     *            the dao to set
     */
    public void setDao(BaseRepository<BaseEntity> dao) {

        this.repository = dao;
    }

    /**
     * Trying to make it of 'R' type
     * 
     * @return the dao param
     * 
     * @param <T>
     *            Entity class
     * @param <S>
     *            Dao class
     */
    @SuppressWarnings("unchecked")
    protected <T extends BaseEntity, S extends BaseRepository<T>> S dao() {

        return (S) repository;
    }
}
