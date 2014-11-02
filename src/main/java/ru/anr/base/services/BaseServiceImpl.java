/**
 * 
 */
package ru.anr.base.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import ru.anr.base.BaseParent;

/**
 * Implementation of Base Service.
 * 
 * 
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 * 
 */
public class BaseServiceImpl extends BaseParent implements BaseService {

    /**
     * Default name for production profile
     */
    public static final String PRODUCTION_PROFILE = "production";

    /**
     * Environment injection
     */
    @Autowired
    private Environment env;

    /**
     * Checking for 'Production' mode
     * 
     * @return true, if 'production' profile found
     */
    public boolean isProdMode() {

        Set<String> profiles = set(env.getActiveProfiles());
        return profiles.contains(PRODUCTION_PROFILE);
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the env
     */
    public Environment getEnv() {

        return env;
    }
}
