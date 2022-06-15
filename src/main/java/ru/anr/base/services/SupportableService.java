package ru.anr.base.services;

/**
 * A prototype for all services that are run based on switchers. The use of some services depends
 * on the configuration where the platform is launched.
 *
 * @author Alexey Romanchuk
 * @created Jun 15, 2022
 */
public interface SupportableService {
    /**
     * @return true, if the service is supported
     */
    default boolean isSupported() {
        return true;
    }
}
