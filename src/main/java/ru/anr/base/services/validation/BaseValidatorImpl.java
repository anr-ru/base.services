/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.anr.base.services.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anr.base.dao.EntityUtils;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.services.BaseServiceImpl;
import ru.anr.base.services.pattern.StrategyConfig;
import ru.anr.base.services.pattern.StrategyConfig.StrategyModes;

/**
 * Implementation of Base Validator.
 *
 * @param <T> The type of object
 * @author Aleksey Melkov
 * @created 09 нояб. 2015 г.
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
        return new StrategyConfig(supports(EntityUtils.entityClass(o)) && supports(o), o, StrategyModes.Normal);
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
     * @param o The object to validate
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
