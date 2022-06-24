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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import ru.anr.base.domain.Accessible;

/**
 * This {@link org.springframework.security.access.PermissionEvaluator} uses
 * {@link Accessible#accessible(Authentication, Object)} function to perform
 * some additional permission checks on domain object level.
 *
 * @author Alexey Romanchuk
 * @created Feb 17, 2015
 */
public class BaseEntityPermissionEvaluator extends AclPermissionEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(BaseEntityPermissionEvaluator.class);

    /**
     * A flag which indicates the ACL checking is required
     */
    private final boolean useAcls;

    /**
     * Constructor for evaluator
     *
     * @param aclService {@link AclService}
     * @param useAcls    true, if ACL permission checking should be switched on
     */
    public BaseEntityPermissionEvaluator(AclService aclService, boolean useAcls) {

        super(aclService);
        this.useAcls = useAcls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object domainObject, Object p) {

        boolean accessible = true;

        Authentication auth = authentication instanceof OAuth2Authentication
                ? ((OAuth2Authentication) authentication).getUserAuthentication() : authentication;


        if (domainObject != null) {
            if (p != null && p.toString().startsWith("access") && (domainObject instanceof Accessible)) {

                // We suppose such patterns: 'access_read', 'access_write', ...
                String[] splitted = p.toString().split("_");
                accessible = ((Accessible) domainObject).accessible(auth, splitted[1]);
            } else if (useAcls) { // The standard ACL checking
                accessible = checkNotNullId(domainObject) && super.hasPermission(auth, domainObject, p);
            } else {
                accessible = false; // not used acls and our checks
            }
        }
        if (logger.isTraceEnabled()) {
            logger.trace("hasPermission for {} and {} = {}", domainObject, p, accessible);
        }
        return accessible;
    }

    /**
     * Checking the identifier is not null. Because a standard ACL check has no
     * sense otherwise.
     *
     * @param domainObject The object to check
     * @return true, if the object has not null ID
     */
    private boolean checkNotNullId(Object domainObject) {

        boolean rs;
        try {
            new ObjectIdentityImpl(domainObject);
            rs = true;
        } catch (IllegalArgumentException ex) {
            rs = false;
        }
        return rs;
    }
}
