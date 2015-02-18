/**
 * 
 */
package ru.anr.base.domain;

import org.springframework.security.core.Authentication;

/**
 * Accessible interface - a simple way to perform access check on domain object
 * level.
 *
 *
 * @author Alexey Romanchuk
 * @created Feb 18, 2015
 *
 */

public interface Accessible {

    /**
     * Customizable function for domain - specific access checks.
     * 
     * @param token
     *            Authentication
     * @param permission
     *            The permission to check. (See
     *            PermissionEvaluator#hasPermission() for argument details)
     * @return true, if the object can be accessed
     */
    default boolean accessible(Authentication token, Object permission) {

        return true; // by default it's always accessible
    }
}
