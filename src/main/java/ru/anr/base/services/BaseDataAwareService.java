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
package ru.anr.base.services;

import ru.anr.base.domain.BaseEntity;
import ru.anr.base.services.transactional.ExecutionWrapper;

/**
 * An interface with database specific functions.
 *
 * @author Alexey Romanchuk
 * @created Jun 3, 2015
 */

public interface BaseDataAwareService extends BaseService {
    /**
     * Saves a new entity to the database. If the entity has been already
     * stored, the DAO operation is not invoked.
     *
     * @param object An object to save
     * @param <S>    Type of the entity
     * @return The saved entity
     */
    <S extends BaseEntity> S save(S object);

    /**
     * This method reloads the given entity to guarantee that we are under the current transaction. We need to use this method
     * when we work under a new transaction (REQUIRES_NEW) with objects that might be obtained under
     * some another transaction.
     *
     * @param entity The entity to reload
     * @param <T>    The type of the entity
     * @return The reloaded object
     */
    <T> T reload(T entity);

    /**
     * A way to roll back the current transaction if we are launching something in the 'dry-run' mode.
     */
    void markAsRollback();

    /**
     * An implementation of a service that allows to run a new transaction or the same transaction
     * based on the current mode - development or production. This may be useful when you need
     * to run something in a new transaction but in the development mode you cannot use it as
     * all unit tests are rolled back usually.
     *
     * @param executor The executor service
     * @param o        The main object to be processed
     * @param params   The additional parameters
     * @param <T>      The type of the object
     * @param <R>      The type of the resulted value
     * @return The resulted value
     */
    <T, R> R execute(ExecutionWrapper<T, R> executor, T o, Object... params);
}
