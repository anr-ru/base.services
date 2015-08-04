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
package ru.anr.base.dao.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Repository;

import ru.anr.base.domain.BaseEntity;

/**
 * A base repository interface.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 * @param <T>
 *            The main entity type
 */
@Repository("BaseRepository")
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    /**
     * Refresh object from data base
     * 
     * @param object
     *            Object
     */
    void refresh(T object);

    /**
     * Executes a 'raw' (not predefine named) query. You can provide just a JPA
     * query string. It can be useful in tests.
     * 
     * @param queryStr
     *            A JPQL query string (for instance, "from Samples") with
     *            positioned (aka ?1) or named (like :name) parameters.
     * @param params
     *            Parameters of the query
     * @return A result list
     * 
     * @param <S>
     *            A result set object types
     */
    <S> List<S> query(String queryStr, Object... params);

    /**
     * Searches an entity by class and id.
     * 
     * @param entityClass
     *            Entity class
     * @param id
     *            Identified of entity
     * @return Found object or null if nothing found
     * 
     * @param <S>
     *            Object type
     */
    <S extends BaseEntity> S find(Class<?> entityClass, Long id);

    /**
     * Performs security-involved filtering of the page object
     * 
     * @param page
     *            The page which results need to be filtered
     * @return A resulted page list with applied security {@link PostFilter}.
     * 
     * @param <S>
     *            Type of item in the list
     */
    @PostFilter("hasPermission(filterObject,'read') or hasPermission(filterObject,'access_read')")
    <S extends BaseEntity> List<S> filter(Page<S> page);
}
