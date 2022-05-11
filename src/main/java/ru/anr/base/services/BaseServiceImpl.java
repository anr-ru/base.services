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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.MultiValueMap;
import ru.anr.base.ApplicationException;
import ru.anr.base.BaseSpringParent;
import ru.anr.base.dao.EntityUtils;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.APIException;
import ru.anr.base.domain.api.MethodTypes;
import ru.anr.base.domain.api.models.BaseObjectModel;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.services.api.APICommandFactory;
import ru.anr.base.services.api.ApiCommandStrategy;
import ru.anr.base.services.api.ApiStrategy;
import ru.anr.base.services.api.ApiUtils;
import ru.anr.base.services.pattern.Strategy;
import ru.anr.base.services.pattern.StrategyFactory;
import ru.anr.base.services.pattern.StrategyFactoryImpl;
import ru.anr.base.services.pattern.StrategyStatistic;
import ru.anr.base.services.validation.ValidationFactory;
import ru.anr.base.services.validation.ValidationUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Implementation of Base Service.
 *
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 */
public class BaseServiceImpl extends BaseSpringParent implements BaseService {

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> S getBean(Class<S> clazz) {

        return super.bean(clazz);
    }

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
     * Factories that include some extensions.
     */
    private final Map<String, StrategyFactory> extensionFactories = toMap();


    protected void registerExtensions(String extId, List<Strategy<Object>> extensions) {
        extensionFactories.put(extId, new StrategyFactoryImpl(extensions));
    }

    /**
     * A variant of the processExtensions(...) function for the 'default' extension.
     * This function is for the compatibility.
     *
     * @param object The object to process
     * @param params Additional parameters
     * @return A list including resulted objects if they were during the
     * processing.
     */
    protected List<Object> processExtensions(Object object, Object... params) {
        return processExtensions("default", object, params);
    }

    /**
     * A point of extension of the current service with sort of plug-ins.
     * Delegates additional processing to some strategies defined for the
     * service. We support multiple of extensions.
     *
     * @param extId  The extension ID
     * @param object The object to process
     * @param params Additional parameters
     * @return The list including resulted objects if they were during the
     * processing.
     */
    protected List<Object> processExtensions(String extId, Object object, Object... params) {
        StrategyStatistic stat = extensionFactories.containsKey(extId) ? extensionFactories.get(extId).process(object, params) : null;
        if (stat == null) {
            logger.warn("No extensions defined for '{}'" + extId);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("List of applied strategies: {}", stat.getAppliedStrategies());
            }
        }
        return stat == null ? null : stat.getResults();
    }


    /**
     * Getting the validator instance if configured (requires
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
     * @param msgCode A code
     * @param args    Message arguments
     */
    protected void reject(String msgCode, Object... args) {

        throw new ApplicationException(text(msgCode, args));

    }

    /**
     * Throws an exception if specified validator contains some violations,
     * joining all error messages
     *
     * @param violations A collection with violations
     * @param <S>        Type of the object to validate
     */
    protected <S> void rejectIfNeed(Set<ConstraintViolation<? extends S>> violations) {

        if (notEmpty(violations)) {
            throw APIException.validation("validation", getAllErrorsAsString(violations));
        }
    }

    /**
     * Generates {@link APIException}
     *
     * @param id     The identifier to use
     * @param params The message parameters
     */
    protected void rejectAPI(String id, Object... params) {

        throw APIException.validation(id, text(id, params));
    }

    /**
     * Checks the given value is not null
     *
     * @param value   A value to check
     * @param errorId A message for an exception to throw
     * @param params  Parameters of the error message
     */
    protected void checkNotNull(Object value, String errorId, Object... params) {

        if (value == null) {
            rejectAPI(errorId, params);
        }
    }

    /**
     * Checks the given value is not null and throws an exception with the given
     * code.
     *
     * @param value   The value to check
     * @param paramId The name of the parameter that represents the value
     */
    protected void checkParamNotNull(Object value, String paramId) {

        checkNotNull(value, "api.param.is.null", paramId);
    }

    /**
     * Checks whether the parameter is wrong. It supposed to use some standard
     * message ID to avoid to much parameters message writing.
     *
     * @param condition The condition to check
     * @param paramId   The parameter ID
     * @param value     The value of the parameter which is wrong
     */
    protected void checkParamWrong(boolean condition, String paramId, Object value) {

        checkIsTrue(condition, "api.param.is.wrong", paramId, value);
    }

    /**
     * Checks the given value is not null
     *
     * @param value   A value to check
     * @param pattern pattern to check
     * @param errorId A message for an exception to throw
     * @param params  Parameters of the error message
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
     * @param condition A condition to check
     * @param errorId   A message identifier for an exception to throw
     * @param params    Parameters of the error message
     */
    protected void checkIsTrue(boolean condition, String errorId, Object... params) {

        if (!condition) {
            rejectAPI(errorId, params);
        }
    }

    /**
     * Cached validators
     */
    private final Map<Class<?>, StrategyFactory> validators = toMap();

    /**
     * Validates an object
     *
     * @param o   The object to be validated
     * @param <S> The type of the object
     */
    protected <S extends Object> void validate(S o) {

        rejectIfNeed(new HashSet<>(validator().validate(o)));

        Class<?> clazz = (o instanceof BaseEntity) ? EntityUtils.entityClass((BaseEntity) o) : o.getClass();

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
     * @param violations collection of violations
     * @param <S>        The class of the object
     * @return All errors as a comma-separated string
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
            throw new AccessDeniedException(
                    "Unable to change the state from '" + object.getState() + "' to '" + newState.name() + "'");
        }
        return oldState;
    }

    /**
     * Loads extensions marked with the given annotation type
     *
     * @param marker The marker annotation
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

    /**
     * Executes the given callback with the use of the given temporal authentication token and restores the
     * previous token after the completion of the execution.
     *
     * @param token    The token to apply
     * @param callback The callback to use
     * @param args     The arguments
     * @param <S>      The type of the result
     * @return The callback's result
     */
    protected <S> S runAs(Authentication token, Function<Object[], S> callback, Object... args) {
        S result;
        Authentication previousToken = SecurityContextHolder.getContext().getAuthentication();
        try {
            SecurityContextHolder.getContext().setAuthentication(token);
            result = callback.apply(args);
        } finally {
            SecurityContextHolder.getContext().setAuthentication(previousToken);
        }
        return result;
    }

    /**
     * The same as {@link #runAs(Authentication, Function, Object...)} but for the void return type.
     *
     * @param token    The token to apply
     * @param callback The non-return callback
     * @param args     The arguments to pass
     */
    protected void runAs(Authentication token, Consumer<Object[]> callback, Object... args) {
        runAs(token, (Function<Object[], Void>) arguments -> {
            callback.accept(arguments);
            return null;
        }, args);
    }

    protected APICommand api(Class<? extends ApiCommandStrategy> clazz, MethodTypes method, RequestModel request,
                             Object... contexts) {

        ApiStrategy a = ApiUtils.extract(clazz);
        APICommandFactory factory = bean(APICommandFactory.class);

        APICommand cmd = new APICommand(a.id(), a.version()).context(contexts);
        RequestModel rq = nullSafeOp(request).orElse(new RequestModel());

        cmd.setType(method);
        cmd.setRequest(rq);

        return factory.process(cmd);
    }

    protected void checkPositiveNumber(BigDecimal value) {
        checkIsTrue(value.signum() > 0, "number.positive", value.toString());
    }

    protected void checkPositiveNumberOrZero(BigDecimal value) {
        checkIsTrue(value.signum() >= 0, "number.positive", value.toString());
    }

    protected void checkPercentNumber(BigDecimal value) {
        checkIsTrue(value.compareTo(d("1")) <= 0, "number.percent", value.toString());
    }

    public void checkLessThanScale(int scale, BigDecimal value) {
        checkIsTrue(verifyLessThanScale(scale, value), "number.scale", value.toString(), scale);
    }

    /**
     * 1. Checks whether the given value has precision less than is allowed by the
     * given scale. For example, if a currency allows the minimum value to be
     * 0.00001, then this function return true for all values which are less
     * than 0.00001 (like 0.000005, 0.00000001, etc.).
     *
     * 2. Also, we check that after rounding the value is not changed, i.e. to avoid $0.023 or so.
     */
    public boolean verifyLessThanScale(int scale, BigDecimal value) {
        if (value.signum() == 0) return true; // zero also works here

        BigDecimal min = scale(BigDecimal.ONE.movePointLeft(scale), scale);
        return min.compareTo(value) <= 0 && value.compareTo(scale(value, scale)) == 0;
    }

    /**
     * A short-cut for a frequently used verification pattern.
     *
     * @param object The object to check
     * @return true, if the object is not null and its ID is defined
     */
    protected boolean isDefined(BaseObjectModel object) {
        return object != null && object.id != null;
    }


    @Override
    public void markAsRollback() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionStatus status = TransactionAspectSupport.currentTransactionStatus();
            status.setRollbackOnly();
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Set the default extensions
     *
     * @param extensions the extensions to set
     */
    public void setExtensions(List<Strategy<Object>> extensions) {
        registerExtensions("default", extensions);
    }

}
