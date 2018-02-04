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

import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import ru.anr.base.domain.BaseEntity;

/**
 * An interface with database specific functions.
 *
 *
 * @author Alexey Romanchuk
 * @created Jun 3, 2015
 *
 */

public interface BaseDataAwareService extends BaseService {

    /**
     * Saves a new entity to the database. If the entity has been already
     * stored, the DAO operation is not invoked.
     * 
     * @param object
     *            An object to save
     * @return The saved entity
     * 
     * @param <S>
     *            Type of the entity
     */
    @PreAuthorize("hasPermission(#o,'write') or hasPermission(#o,'access_write')")
    <S extends BaseEntity> S save(@P("o") S object);
}
