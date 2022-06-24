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
package ru.anr.base.services.security;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import ru.anr.base.domain.BaseEntity;

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
}
