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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.*;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.services.BaseDataAwareServiceImpl;

/**
 * An implementation of {@link ACLManager}.
 *
 * @author Alexey Romanchuk
 * @created Feb 14, 2015
 */
public class ACLManagerImpl extends BaseDataAwareServiceImpl implements ACLManager {

    private static final Logger logger = LoggerFactory.getLogger(ACLManagerImpl.class);

    /**
     * The standard {@link MutableAclService} implementation
     */
    @Autowired
    private MutableAclService acls;

    /**
     * {@inheritDoc}
     */
    @Override
    public void grant(BaseEntity e, Sid sid, Permission permission) {

        ObjectIdentity oi = new ObjectIdentityImpl(entityClass(e), e.getId());
        logger.debug("Granting for ObjectIdentity: {} - {}", oi.getType(), oi.getIdentifier());

        MutableAcl acl = null;
        try {
            acl = (MutableAcl) acls.readAclById(oi);
        } catch (NotFoundException ex) {
            acl = acls.createAcl(oi);
        }

        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        acls.updateAcl(acl);
    }
}
