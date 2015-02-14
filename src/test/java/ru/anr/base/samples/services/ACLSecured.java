/**
 * 
 */
package ru.anr.base.samples.services;

import ru.anr.base.samples.domain.Samples;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Dec 1, 2014
 *
 */

public interface ACLSecured {

    /**
     * Testing ACL access
     * 
     * @param object
     *            Sample object
     */
    void apply(Samples object);

    /**
     * Testing ACL access
     * 
     * @param id
     *            Identier
     * @return Object
     */
    Samples getObject(Long id);
}
