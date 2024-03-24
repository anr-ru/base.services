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
package ru.anr.base.services.security;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.util.MultiValueMap;
import ru.anr.base.domain.BaseEntity;

import java.util.List;

/**
 * A wrapper for {@link org.springframework.security.acls.model.AclService} for
 * more simple ACL management.
 *
 * @author Alexey Romanchuk
 * @created Feb 14, 2015
 */
public interface ACLManager {
    /**
     * Grants a given permission to an entity for a specific user
     *
     * @param e          The entity
     * @param sid        The sid to set (whom the permission is set)
     * @param permission The permission
     */
    void grant(BaseEntity e, Sid sid, Permission permission);

    /**
     * Revokes all ACL permissions for the given object
     *
     * @param e The object
     */
    void revokeAll(BaseEntity e);

    /**
     * Revokes all ACL permissions for the given object
     *
     * @param e   The object
     * @param sid The sid
     */
    void revoke(BaseEntity e, Sid sid);

    /**
     * Verifies the given permissions are granted to the given Sid for the given
     * entity.
     *
     * @param entity      The entity
     * @param sid         The sid
     * @param permissions The permissions list
     * @return true, if permissions are granted
     */
    boolean isGranted(BaseEntity entity, Sid sid, List<Permission> permissions);

    /**
     * Returns the map of ACLs.
     *
     * @param entity The entity
     * @return The map with Sid's as keys and permissions as values
     */
    MultiValueMap<Sid, Permission> getPermissions(BaseEntity entity);
}
