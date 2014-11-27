/**
 * 
 */
package ru.anr.base.samples.services;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import ru.anr.base.services.BaseDataAwareServiceImpl;

/**
 * A secured service implementation.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 *
 */
@Component("roleSecured")
public class RoleSecuredImpl extends BaseDataAwareServiceImpl {

    /**
     * {@inheritDoc}
     */
    @Override
    @Secured("ROLE_USER")
    public String text(String code, Object... args) {

        return super.text(code, args);
    }
}
