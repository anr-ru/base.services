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

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

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
import org.springframework.util.MultiValueMap;

import ru.anr.base.ApplicationException;
import ru.anr.base.BaseSpringParent;
import ru.anr.base.dao.BaseRepositoryImpl;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.domain.api.APIException;
import ru.anr.base.services.pattern.Strategy;
import ru.anr.base.services.pattern.StrategyFactory;
import ru.anr.base.services.pattern.StrategyFactoryImpl;
import ru.anr.base.services.pattern.StrategyStatistic;
import ru.anr.base.services.validation.ValidationFactory;
import ru.anr.base.services.validation.ValidationUtils;

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
     * {@inheritDoc}
     */
    @Override
    public String textLocalized(String code, Locale l, Object... args) {

        String txt = null;

        try {
            txt = messages.getMessage(code, args, l);

        } catch (NoSuchMessageException ex) {

            logger.error("Message resource error: {}", ex.getMessage());
            txt = String.format(MSG_ERROR_DECORATION, code);
        }
        return txt;
    }

    /**
     * List of additional extensions of the service
     */
    private List<Strategy<Object>> extensions = list();

    /**
     * A factory which manages by extensions execution
     */
    private StrategyFactory extensionFactory;

    /**
     * Initialization
     */
    @PostConstruct
    public void init() {

        extensionFactory = new StrategyFactoryImpl(extensions);
    }

    /**
     * A point of extension of the current service with sort of plug-ins.
     * Delegates an additional processing to some strategies, defined for the
     * service.
     * 
     * @param object
     *            Original object to process
     * @param params
     *            Additional parameters
     * @return A list including resulted objects if they were during the
     *         processing.
     */
    protected List<Object> processExtentions(Object object, Object... params) {

        StrategyStatistic stat = extensionFactory.process(object, params);

        if (logger.isDebugEnabled()) {
            logger.debug("List of applied strategies: {}", stat.getAppliedStrategies());
        }
        return stat.getResults();
    }

    /**
     * Getting validator instance if configured (requires
     * {@link ru.anr.base.services.validation.ValidationConfig} to be loaded)
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
     * 
     * @param <S>
     *            Type of the object to validate
     */
    protected <S> void rejectIfNeed(Set<ConstraintViolation<? extends S>> violations) {

        if (notEmpty(violations)) {
            throw APIException.validation("validation", getAllErrorsAsString(violations));
        }
    }

    /**
     * Generates {@link APIException}
     * 
     * @param id
     *            The identifier to use
     * @param params
     *            The message parameters
     */
    protected void rejectAPI(String id, Object... params) {

        throw APIException.validation(id, text(id, params));
    }

    /**
     * Checks the given value is not null
     * 
     * @param value
     *            A value to check
     * @param errorId
     *            A message for an exception to throw
     * @param params
     *            Parameters of the error message
     */
    protected void checkNotNull(Object value, String errorId, Object... params) {

        if (value == null) {
            rejectAPI(errorId, params);
        }
    }

    /**
     * Checks the given value is not null
     * 
     * @param value
     *            A value to check
     * @param pattern
     *            pattern to check
     * @param errorId
     *            A message for an exception to throw
     * @param params
     *            Parameters of the error message
     */
    protected void checkPattern(String value, String pattern, String errorId, Object... params) {

        if (!Pattern.matches(pattern, value)) {
            rejectAPI(errorId, params);
        }
    }

    /**
     * Checks the given condition is true and throws an exception if it is
     * false.
     * 
     * @param condition
     *            A condition to check
     * @param errorId
     *            A message identifier for an exception to throw
     * @param params
     *            Parameters of the error message
     */
    protected void checkIsTrue(boolean condition, String errorId, Object... params) {

        if (!condition) {
            rejectAPI(errorId, params);
        }
    }

    /**
     * Cached validators
     */
    private Map<Class<?>, StrategyFactory> validators = toMap();

    /**
     * Validates an object
     * 
     * @param o
     *            The object to be validated
     * @param <S>
     *            The type of the object
     */
    protected <S extends Object> void validate(S o) {

        rejectIfNeed(new HashSet<>(validator().validate(o)));

        Class<?> clazz = (o instanceof BaseEntity) ? BaseRepositoryImpl.entityClass((BaseEntity) o) : o.getClass();

        if (!validators.containsKey(clazz)) {

            ValidationFactory factory = bean("ValidationFactory", ValidationFactory.class);

            StrategyFactory sf = new StrategyFactoryImpl(factory.getValidators(clazz));
            validators.put(clazz, sf);
        }
        validators.get(clazz).process(o);
    }

    /**
     * Extract all error message from {@link ConstraintViolation} as a single
     * string
     * 
     * @param violations
     *            collection of violations
     * @return All errors as a comma-separated string
     * @param <S>
     *            The class of the object
     */
    protected <S> String getAllErrorsAsString(Set<ConstraintViolation<? extends S>> violations) {

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
            logger.error("Transition {} to {} is invalid", object.getState(), newState.name());
            throw new AccessDeniedException("Unable to change the state");
        }
        return oldState;
    }

    /**
     * Loads extensions marked with the given annotation type
     * 
     * @param marker
     *            The marker annotation
     * @return A list of extensions
     */
    protected List<Strategy<Object>> loadExtentions(Class<? extends Annotation> marker) {

        Map<String, Object> beans = ctx.getBeansWithAnnotation(marker);

        @SuppressWarnings("unchecked")
        List<Strategy<Object>> extentions = list(beans.values().stream().map(a -> {
            return (Strategy<Object>) a;
        }));

        logger.info("Loaded: {} '{}' extensions for the: {}", extentions.size(), marker.getSimpleName(), target(this));
        return extentions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TargetEnvironments getTargetEnv() {

        return TargetEnvironments.search(getProfiles());
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param extensions
     *            the extensions to set
     */
    public void setExtensions(List<Strategy<Object>> extensions) {

        this.extensions = extensions;
    }

}
