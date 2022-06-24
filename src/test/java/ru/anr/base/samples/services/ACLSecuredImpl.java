package ru.anr.base.samples.services;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import ru.anr.base.samples.domain.Samples;

/**
 * An implementation of {@link ACLSecured}.
 *
 * @author Alexey Romanchuk
 * @created Dec 1, 2014
 */
@Component("AclSecured")
public class ACLSecuredImpl extends TestDataServiceImpl implements ACLSecured {

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
