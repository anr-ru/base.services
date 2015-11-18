/**
 * 
 */
package ru.anr.base.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.anr.base.domain.BaseEntity;

/**
 * Implementation of Base Validator.
 *
 *
 * @author Aleksey Melkov
 * @created 09 нояб. 2015 г.
 *
 */

public class BaseValidatorImpl extends BaseServiceImpl implements BaseValidator {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseValidatorImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(BaseEntity object) {

        if (object != null) {

            logger.info("Validate a object: {}", object);
            processExtentions(object);
        }
    }

}
