/**
 * 
 */
package ru.anr.base.samples.services;

import org.springframework.security.access.annotation.Secured;

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
     * @param o
     *            Sample object
     */
    @Secured({ "ACL_OBJECT_WRITE" })
    void apply(Samples o);

    /**
     * Testing ACL access
     * 
     * @param id
     *            Identier
     * @return Object
     */
    @Secured({ "ACL_READ" })
    Samples getObject(Long id);
}
