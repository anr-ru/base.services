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
package ru.anr.base.services;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import ru.anr.base.ApplicationException;
import ru.anr.base.BaseSpringParent;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.services.pattern.Strategy;
import ru.anr.base.services.pattern.StrategyFactory;
import ru.anr.base.services.pattern.StrategyFactoryImpl;
import ru.anr.base.services.pattern.StrategyStatistic;

/**
 * Implementation of Base Service.
 * 
 * 
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 * 
 */
public class BaseServiceImpl extends BaseSpringParent implements BaseService {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);

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

    /**
     * A ref to a text resource service
     */
    @Autowired
    @Qualifier("messageSourceAccessor")
    private MessageSourceAccessor messages;

    /**
     * A template of string is returned in case when message with code not found
     */
    private static final String MSG_ERROR_DECORATION = "[xxx%sxxx]";

    /**
     * {@inheritDoc}
     */
    @Override
    public String text(String code, Object... args) {

        String txt = null;

        try {
            txt = messages.getMessage(code, args);

        } catch (NoSuchMessageException ex) {

            logger.error("Message resource error: {}", ex.getMessage());
            txt = String.format(MSG_ERROR_DECORATION, code);
        }
        return txt;
    }

    /**
     * List of additional extentions of the service
     */
    private List<Strategy<Object>> extentions = list();

    /**
     * A factory which manages by extentions execution
     */
    private StrategyFactory extentionFactory;

    /**
     * Initialization
     */
    @PostConstruct
    public void init() {

        extentionFactory = new StrategyFactoryImpl(extentions);
    }

    /**
     * A point of extention of the current service with sort of plugins.
     * Delegates an additional processing to some strategies, defined for the
     * service.
     * 
     * @param object
     *            Original object to process
     * @param params
     *            Additional parameters
     * @return Possible updated original object
     */
    protected Object processExtentions(Object object, Object... params) {

        StrategyStatistic stat = extentionFactory.process(object, params);

        if (logger.isDebugEnabled()) {
            logger.debug("List of applied strategies: {}", stat.getAppliedStrategies());
        }
        return stat.getObject();
    }

    /**
     * Getting validator instance if configured (requires
     * {@link ValidationConfig} to be loaded)
     * 
     * @return {@link Validator}
     */
    protected Validator validator() {

        Validator bean = null;

        if (hasBean("validator")) {
            bean = bean("validator", Validator.class);
        }
        return bean;
    }

    /**
     * Throws an exception with specified message taken from resources
     * 
     * @param msgCode
     *            A code
     * @param args
     *            Message arguments
     */
    protected void reject(String msgCode, Object... args) {

        throw new ApplicationException(text(msgCode, args));

    }

    /**
     * Throws an exception if specified validator contains some violations,
     * joining all error messages
     * 
     * @param violations
     *            A collection with violations
     */
    protected void rejectIfNeed(Set<ConstraintViolation<?>> violations) {

        if (!CollectionUtils.isEmpty(violations)) {

            throw new ApplicationException(getAllErrorsAsString(violations));
        }
    }

    /**
     * Extract all error message from {@link ConstraintViolation} as a single
     * string
     * 
     * @param violations
     *            collection of violations
     * @return All errors as a comma-separated string
     */
    protected String getAllErrorsAsString(Set<ConstraintViolation<?>> violations) {

        return ValidationUtils.getAllErrorsAsString(violations);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <S extends Enum<S>> S changeState(BaseEntity object, S newState) {

        S oldState = null;
        boolean transitionDone = false;

        if (safeEquals(newState.name(), object.getState())) {

            logger.debug("Trying to change the same state (trivial case)");

            transitionDone = true;
            oldState = newState;

        } else {
            MultiValueMap<String, String> map = object.getTransitionsMap();
            if (map.containsKey(object.getState())) {

                List<String> targets = map.get(object.getState());

                if (targets.contains(newState.name())) {

                    String oldStateStr = object.changeState(newState.name());
                    oldState = (S) Enum.valueOf(newState.getClass(), oldStateStr);
                    transitionDone = true;
                }
            }
        }
        if (!transitionDone) {
            throw new AccessDeniedException("Unable to change the state");
        }
        return oldState;
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param extentions
     *            the extensions to set
     */
    public void setExtentions(List<Strategy<Object>> extentions) {

        this.extentions = extentions;
    }
}
