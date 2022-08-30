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

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.anr.base.domain.BaseEntity;

import java.util.List;
import java.util.Map;

/**
 * A base repository interface.
 *
 * @param <T> The main entity type
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 */
@Repository("BaseRepository")
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    /**
     * Refreshes (loads) object from the database
     *
     * @param object Object
     */
    <S extends T> void refresh(S object);

    /**
     * Executes a 'raw' (not predefine named) query. You can provide just a JPA
     * query string. It can be useful in tests.
     *
     * @param queryStr A JPQL query string (for instance, "from Samples") with
     *                 positioned (aka ?1) or named (like :name) parameters.
     * @param params   Parameters of the query
     * @param <S>      A result set object types
     * @return A result list
     */
    <S> List<S> query(String queryStr, Object... params);

    /**
     * Executes a query (UPDATE or DELETE).
     *
     * @param queryStr The query
     * @param params   The parameters
     * @return The number of processed object
     */
    int execute(String queryStr, Object... params);

    /**
     * Executes a native SQL UPDATE or DELETE query.
     *
     * @param sql    The query
     * @param params The parameters
     * @return The number of processed object
     */
    int executeSQL(String sql, Map<String, Object> params);

    /**
     * Searches an entity by class and id.
     *
     * @param entityClass Entity class
     * @param id          Identified of entity
     * @param <S>         Object type
     * @return Found object or null if nothing found
     */
    <S extends T> S find(Class<S> entityClass, Long id);

    /**
     * Executes a native SQL query. This function can be useful for doing some reports directly based on SQL tables.
     *
     * @param sql    The SQL query to execute
     * @param page   The pager
     * @param params Query parameters
     * @return The resulted list
     */
    List<Map<String, Object>> executeSQLQuery(String sql, Pageable page, Map<String, Object> params);
}
