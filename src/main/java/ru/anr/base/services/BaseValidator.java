/**
 * 
 */
package ru.anr.base.services;

import ru.anr.base.domain.BaseEntity;

/**
 * Base Validator Interface
 *
 *
 * @author Aleksey Melkov
 * @created 09 нояб. 2015 г.
 *
 */

public interface BaseValidator {

    /**
     * Validate object
     * 
     * @param object
     *            Object
     */
    void validate(BaseEntity object);

}
