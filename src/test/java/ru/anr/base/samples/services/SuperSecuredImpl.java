/**
 *
 */
package ru.anr.base.samples.services;

import org.springframework.security.access.prepost.PreAuthorize;
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
@Component("superSecured")
public class SuperSecuredImpl extends BaseDataAwareServiceImpl {

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("isFullyAuthenticated()")
    public String text(String code, Object... args) {
        return super.text(code, args);
    }
}
