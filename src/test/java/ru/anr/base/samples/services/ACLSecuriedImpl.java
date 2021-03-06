/**
 * 
 */
package ru.anr.base.samples.services;

import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import ru.anr.base.samples.domain.Samples;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Dec 1, 2014
 *
 */
@Component("AclSecured")
public class ACLSecuriedImpl extends TestDataServiceImpl implements ACLSecured {

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#xx,'write') or hasPermission(#xx,'access_write')")
    public void apply(@P("xx") Samples o) {

        if (o != null) {
            dao().save(o);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PostAuthorize("hasPermission(returnObject,'read') and hasPermission(returnObject,'access_read')")
    public Samples getObject(Long id) {

        return dao().find(Samples.class, id);
    }
}
