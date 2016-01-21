/**
 * 
 */
package ru.anr.base.services.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.anr.base.dao.BaseRepositoryImpl;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.services.BaseServiceImpl;
import ru.anr.base.services.pattern.StrategyConfig;
import ru.anr.base.services.pattern.StrategyConfig.StrategyModes;

/**
 * Implementation of Base Validator.
 *
 *
 * @author Aleksey Melkov
 * @created 09 нояб. 2015 г.
 *
 * @param <T>
 *            The type of object
 */

public class BaseValidatorImpl<T extends BaseEntity> extends BaseServiceImpl implements BaseValidator<T> {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseValidatorImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public final StrategyConfig check(T o, Object... params) {

        return new StrategyConfig(supports(BaseRepositoryImpl.entityClass(o)) && supports(o), o, StrategyModes.Normal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(T object) {

        return true;
    }

    /**
     * The validation
     * 
     * @param o
     *            The object to validate
     */
    protected void validate(T o) {

        // Empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final T process(T o, StrategyConfig cfg) {

        logger.debug("Validating an object: {}", o);

        validate(o);
        return o;
    }
}
