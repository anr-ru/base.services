/**
 * 
 */
package ru.anr.base.samples.services;

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
    public void apply(Samples o) {

        dao().save(o);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Samples getObject(Long id) {

        return (Samples) dao().findOne(id);
    }
}
