/**
 * 
 */
package ru.anr.base.services.security;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import ru.anr.base.domain.BaseEntity;

/**
 * A wrapper for {@link org.springframework.security.acls.model.AclService} for
 * more simple ACL managerment.
 *
 *
 * @author Alexey Romanchuk
 * @created Feb 14, 2015
 *
 */

public interface ACLManager {

    /**
     * Grants a given permission to an entity for a specific user
     * 
     * @param e
     *            Entity
     * @param sid
     *            A sid to set (whom the permission is set)
     * @param permission
     *            A permission
     */
    void grant(BaseEntity e, Sid sid, Permission permission);
}
