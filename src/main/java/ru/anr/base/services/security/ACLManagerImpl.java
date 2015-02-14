/**
 * 
 */
package ru.anr.base.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import ru.anr.base.domain.BaseEntity;
import ru.anr.base.services.BaseDataAwareServiceImpl;

/**
 * Implementation for {@link ACLManager}.
 *
 *
 * @author Alexey Romanchuk
 * @created Feb 14, 2015
 *
 */

public class ACLManagerImpl extends BaseDataAwareServiceImpl implements ACLManager {

    /**
     * Standard {@link MutableAclService} implementation
     */
    @Autowired
    private MutableAclService acls;

    /**
     * {@inheritDoc}
     */
    @Override
    public void grant(BaseEntity e, Sid sid, Permission permission) {

        ObjectIdentity oi = new ObjectIdentityImpl(entityClass(e), e.getId());
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
