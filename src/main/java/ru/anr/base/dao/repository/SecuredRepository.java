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
package ru.anr.base.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.anr.base.domain.BaseEntity;

import java.util.List;

/**
 * A base repository interface.
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 */
public interface SecuredRepository {
    /**
     * Searches an entity by its class and the ID and verifies read access to it.
     *
     * @param entityClass Entity class
     * @param id          Identified of entity
     * @param <S>         Object type
     * @return Found object or null if nothing found
     */
    @PostAuthorize("hasPermission(returnObject,'read') or hasPermission(returnObject,'access_read') or hasRole('ROLE_ROOT')")
    <S extends BaseEntity> S findSecured(Class<S> entityClass, Long id);

    /**
     * Saves the entity with permissions checking.
     *
     * @param entity The entity
     * @param <S>    The type of entity
     * @return The resulted entity
     */
    @PreAuthorize("hasPermission(#o,'write') or hasPermission(#o,'access_write') or hasRole('ROLE_ROOT')")
    <S extends BaseEntity> S saveSecured(@Param("o") S entity);

    /**
     * Deletes the entity with permissions checking.
     *
     * @param entity The entity
     * @param <S>    The type of entity
     * @return The resulted entity
     */
    @PreAuthorize("hasPermission(#o,'delete') or hasPermission(#o,'access_delete') or hasRole('ROLE_ROOT')")
    <S extends BaseEntity> void deleteSecured(@Param("o") S entity);

    /**
     * Performs a permission-based filtration of the page object
     *
     * @param page The page which results need to be filtered
     * @param <S>  The type of item in the list
     * @return The resulted list extracted from the pages with applied security {@link PostFilter}.
     */
    @PostFilter("hasPermission(filterObject,'read') or hasPermission(filterObject,'access_read') or hasRole('ROLE_ROOT')")
    <S extends BaseEntity> List<S> filterSecured(Page<S> page);
}
