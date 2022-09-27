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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import ru.anr.base.dao.EntityUtils;
import ru.anr.base.dao.SecuredPageImpl;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.dao.repository.SecuredRepository;
import ru.anr.base.domain.BaseEntity;

/**
 * A parent class for all business logic services, which need a database access.
 * We suppose the necessary DAO repository was injected.
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 */
@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = {NotFoundException.class})
public class BaseDataAwareServiceImpl extends BaseServiceImpl implements BaseDataAwareService {

    private static final Logger logger = LoggerFactory.getLogger(BaseDataAwareServiceImpl.class);

    /**
     * Base repository
     */
    private BaseRepository<BaseEntity> repository;
    private SecuredRepository securedRepository;

    /**
     * Returns an entity (maybe proxied) class
     *
     * @param e Entity
     * @return Class
     */
    protected Class<?> entityClass(BaseEntity e) {
        return EntityUtils.entityClass(e);
    }

    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasPermission(#o,'write') or hasPermission(#o,'access_write') or hasRole('ROLE_ROOT')")
    @Override
    public <S extends BaseEntity> S save(@P("o") S object) {

        S e = object;
        BaseRepository<BaseEntity> dao = dao();
        Assert.notNull(dao, "BaseRepository DAO is not configured");

        if (object.getId() == null) {
            e = dao.save(object);
        }
        return e;
    }

    /**
     * This method reload the given entity to guarantee that we are under the current transaction.
     * We need to use this method when we work under a new transaction (REQUIRES_NEW).
     *
     * @param entity The entity to reload
     * @param <T>    The type of the entity
     * @return The reloaded object
     */
    @Override
    public <T> T reload(T entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity o = dao().find(EntityUtils.entityClass((BaseEntity)entity), ((BaseEntity)entity).getId());
            if (o == null) {
                logger.error("The object {} not found, probably, it was deleted", entity);
                //throw new ApplicationException("Deleted object");
                o = (BaseEntity) entity; // try to use the same object
            }
            return (T)o;
        }
         else {
             return entity;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void markAsRollback() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionStatus status = TransactionAspectSupport.currentTransactionStatus();
            status.setRollbackOnly();
        }
    }

    /**
     * Quick filtration of DAO results according to security settings.
     *
     * @param pages The queried pages
     * @param <S>   The type of the entity
     * @return The resulted pages
     */
    protected <S extends BaseEntity> Page<S> filterSecured(Page<S> pages) {
        return new SecuredPageImpl<>(securedDao(), pages);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///// getters/setters
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Injecting 'BaseRepository' bean if only it exists.
     *
     * @param dao the dao to set
     */
    @Autowired
    @Qualifier("BaseRepository")
    @Lazy
    public void setDao(BaseRepository<BaseEntity> dao) {
        this.repository = dao;
    }

    @Autowired
    @Qualifier("SecuredRepository")
    @Lazy
    public void setSecuredDao(SecuredRepository dao) {
        this.securedRepository = dao;
    }

    /**
     * Trying to make it of 'S' type
     *
     * @param <T> Entity class
     * @param <S> Dao class
     * @return the dao param
     */
    @SuppressWarnings("unchecked")
    protected <T extends BaseEntity, S extends BaseRepository<T>> S dao() {
        return (S) repository;
    }

    /**
     * Returns the secured DAO instance
     *
     * @return The secured DAO repository
     */
    protected SecuredRepository securedDao() {
        return securedRepository;
    }
}
