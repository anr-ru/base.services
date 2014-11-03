/**
 * 
 */
package ru.anr.base.services;

import java.util.Set;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.anr.base.BaseSpringParent;

/**
 * Implementation of Base Service.
 * 
 * 
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 * 
 */
@Transactional(propagation = Propagation.REQUIRED)
public class BaseServiceImpl extends BaseSpringParent implements BaseService {

    /**
     * Default name for production profile
     */
    public static final String PRODUCTION_PROFILE = "production";

    /**
     * Checking for 'Production' mode
     * 
     * @return true, if 'production' profile found
     */
    protected boolean isProdMode() {

        Set<String> profiles = getProfiles();
        return profiles.contains(PRODUCTION_PROFILE);
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

}
