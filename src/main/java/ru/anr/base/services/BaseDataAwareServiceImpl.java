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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ru.anr.base.dao.BaseRepositoryImpl;
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
@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = { NotFoundException.class })
public class BaseDataAwareServiceImpl extends BaseServiceImpl implements BaseDataAwareService {

    /**
     * Base repository
     */
    private BaseRepository<BaseEntity> repository;

    /**
     * Returns an entity (may be proxied) class
     * 
     * @param e
     *            Entity
     * @return Class
     */
    protected Class<?> entityClass(BaseEntity e) {

        return BaseRepositoryImpl.entityClass(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends BaseEntity> S save(S object) {

        S e = object;
        BaseRepository<BaseEntity> dao = dao();
        Assert.notNull(dao, "BaseRepository DAO is not configured");

        if (object.getId() == null) {
            e = dao.save(object);
        }
        return e;
    }

    /**
     * Applying security filter to the page
     * 
     * @param page
     *            The page to be filtered
     * @return Filtered list
     * @param <S>
     *            Type of an item of the list
     * 
     */
    protected <S extends BaseEntity> List<S> securedFilter(Page<S> page) {

        BaseRepository<BaseEntity> dao = dao();
        Assert.notNull(dao, "BaseRepository DAO is not configured");

        return dao.filter(page);
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Injecting 'BaseRepository' bean if only it exists.
     * 
     * @param dao
     *            the dao to set
     */
    @Autowired(required = false)
    @Qualifier("BaseRepository")
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
