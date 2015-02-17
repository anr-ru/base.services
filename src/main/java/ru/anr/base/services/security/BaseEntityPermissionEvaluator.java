/**
 * 
 */
package ru.anr.base.services.security;

import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;

import ru.anr.base.domain.BaseEntity;

/**
 * This {@link org.springframework.security.access.PermissionEvaluator} uses
 * {@link BaseEntity#accessible(Authentication, Object)} function to perform
 * some additional permission checks on domain object level.
 *
 *
 * @author Alexey Romanchuk
 * @created Feb 17, 2015
 *
 */
public class BaseEntityPermissionEvaluator extends AclPermissionEvaluator {

    /**
     * Constructor for evaluator
     * 
     * @param aclService
     *            {@link AclService}
     */
    public BaseEntityPermissionEvaluator(AclService aclService) {

        super(aclService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object domainObject, Object permission) {

        boolean accessible = true;

        if (permission != null && permission.toString().startsWith("access") && domainObject instanceof BaseEntity) {

            // We suppose such patterns: access_read, access_write, ...
            String[] splitted = permission.toString().split("_");
            accessible = ((BaseEntity) domainObject).accessible(authentication, splitted[1]);

        } else { // A standard ACL
            accessible = super.hasPermission(authentication, domainObject, permission);
        }
        return accessible;
    }
}
