package ru.anr.base.samples.services;

import ru.anr.base.samples.domain.Samples;

/**
 * A service for testing ACL security.
 *
 * @author Alexey Romanchuk
 * @created Dec 1, 2014
 */
public interface ACLSecured {
    /**
     * Testing ACL access
     *
     * @param object Sample object
     */
    void apply(Samples object);

    /**
     * Testing ACL access
     *
     * @param id The identifier
     * @return The resulted object
     */
    Samples getObject(Long id);
}
