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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.services.BaseDataAwareServiceImpl;

import java.util.List;

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

        MutableAcl acl = getAcl(e);
        if (acl == null) acl = acls.createAcl(oi);

        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        acls.updateAcl(acl);
    }


    @Override
    public void revokeAll(BaseEntity e) {

        ObjectIdentity oi = new ObjectIdentityImpl(entityClass(e), e.getId());
        logger.debug("Revoking permissions for {} - {}", oi.getType(), oi.getIdentifier());

        acls.deleteAcl(oi, true);
    }

    @Override
    public void revoke(BaseEntity entity, Sid sid) {

        MutableAcl acl = getAcl(entity);
        if (acl == null) return; // nothing to revoke

        try {

            int size = acl.getEntries().size();
            for (int idx = 0; idx < size; idx++) {
                if (acl.getEntries().get(idx).getSid().equals(sid)) {
                    acl.deleteAce(idx);
                }
            }
        } catch (NotFoundException ex) {
            throw new AccessDeniedException("No permissions to delete ACLs");
        }
        acls.updateAcl(acl);
    }

    private MutableAcl getAcl(BaseEntity entity) {

        ObjectIdentity oi = new ObjectIdentityImpl(entityClass(entity), entity.getId());
        logger.debug("Granting for ObjectIdentity: {} - {}", oi.getType(), oi.getIdentifier());

        MutableAcl acl;
        try {
            acl = (MutableAcl) acls.readAclById(oi);
        } catch (NotFoundException ex) {
            acl = null;
        }
        return acl;
    }

    @Override
    public boolean isGranted(BaseEntity entity, Sid sid, List<Permission> permissions) {
        MutableAcl acl = getAcl(entity);
        if (acl == null) return false;
        try {
            return acl.isGranted(permissions, list(sid), false);
        } catch (NotFoundException ex) {
            return false;
        }
    }

    @Override
    public MultiValueMap<Sid, Permission> getPermissions(BaseEntity entity) {

        MutableAcl acl = getAcl(entity);
        MultiValueMap<Sid, Permission> rs = new LinkedMultiValueMap<>();

        if (acl != null) {
            for (int idx = 0; idx < acl.getEntries().size(); idx++) {
                AccessControlEntry ace = acl.getEntries().get(idx);
                rs.add(ace.getSid(), ace.getPermission());
            }
        }
        return rs;
    }

}
