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

package ru.anr.base.dao;

import org.hibernate.proxy.HibernateProxy;
import ru.anr.base.domain.BaseEntity;

/**
 * Entity Utils
 *
 * @author Alexey Romanchuk
 * @created Aug 29, 2021
 */
public final class EntityUtils {

    private EntityUtils() {
        // prevent instantiating
    }

    /**
     * Extracts the entity's class for the specified entity which can be a
     * proxied hibernate entity. The implementation is Hibernate-specific.
     *
     * @param entity The entity
     * @return The entity's class
     */
    public static Class<?> entityClass(BaseEntity entity) {
        Object r = entity(entity);
        return r.getClass();
    }

    /**
     * Extracts a pure entity for the given hibernate entity which can be proxied.
     * The implementation is Hibernate-specific.
     *
     * @param entity The entity
     * @param <S>    The type
     * @return The resulted pure entity
     */
    @SuppressWarnings("unchecked")
    public static <S extends BaseEntity> S entity(S entity) {

        S r = entity;

        if (entity instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) entity;
            r = (S) proxy.getHibernateLazyInitializer().getImplementation();
        }
        return r;
    }
}