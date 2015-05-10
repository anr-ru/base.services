/**
 * 
 */
package ru.anr.base.services.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import ru.anr.base.domain.Accessible;

/**
 * This {@link org.springframework.security.access.PermissionEvaluator} uses
 * {@link Accessible#accessible(Authentication, Object)} function to perform
 * some additional permission checks on domain object level.
 *
 *
 * @author Alexey Romanchuk
 * @created Feb 17, 2015
 *
 */
public class BaseEntityPermissionEvaluator extends AclPermissionEvaluator {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseEntityPermissionEvaluator.class);

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
    public boolean hasPermission(Authentication authentication, Object domainObject, Object p) {

        boolean accessible = true;

        if (authentication instanceof OAuth2Authentication) {
            authentication = ((OAuth2Authentication) authentication).getUserAuthentication();
        }

        logger.trace("hasPermission for {} and {}", domainObject, p);

        if (domainObject != null) {
            if (p != null && p.toString().startsWith("access") && (domainObject instanceof Accessible)) {

                // We suppose such patterns: access_read, access_write, ...
                String[] splitted = p.toString().split("_");
                accessible = ((Accessible) domainObject).accessible(authentication, splitted[1]);

            } else { // A standard ACL
                accessible = super.hasPermission(authentication, domainObject, p);
            }
        }
        return accessible;
    }
}
