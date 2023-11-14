/*
 * Copyright 2014-2022 the original author or authors.
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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
import ru.anr.base.domain.api.models.ResponseModel;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * An implementation of {@link BaseService}.
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
     * The logger
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

        String txt;

        try {
            txt = messages.getMessage(code, args);
        } catch (NoSuchMessageException ex) {
            logger.debug("Message resource error: {}", ex.getMessage());
            txt = String.format(MSG_ERROR_DECORATION, code);
        }
        return txt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String textLocalized(String code, Locale l, Object... args) {

        String txt;
        try {
            txt = messages.getMessage(code, args, l);
        } catch (NoSuchMessageException ex) {
            logger.debug("Message resource error: {}", ex.getMessage());
            txt = String.format(MSG_ERROR_DECORATION, code);
        }
        return txt;
    }

    /**
     * Factories that include some extensions.
     */
    protected final Map<Object, StrategyFactory> extensionFactories = toMap();


    public void registerExtensions(Object extId, List<Strategy<Object>> extensions) {
        extensionFactories.put(extId, new StrategyFactoryImpl(extensions));
        if (notEmpty(extensions)) {
            logger.info("Initializing '{}' {} extensions for {}", extId, extensions.size(), target(this));
        }
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
        return processParametrizedExtensions("default", object, params);
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
    protected List<Object> processParametrizedExtensions(Object extId, Object object, Object... params) {

        // We use lazy loading for extensions
        if ((extId instanceof String) && !extensionFactories.containsKey(extId)) {
            registerExtensions(extId, loadExtensions(ExtensionMarker.class, (String) extId));
        }

        StrategyStatistic stat = extensionFactories.containsKey(extId) ?
                extensionFactories.get(extId).process(object, params) :
                null;

        if (stat == null) {
            logger.warn("No extensions defined for '{}'", nullSafe(extId));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("List of applied strategies: {}", stat.getAppliedStrategies());
            }
        }
        return nullSafe(stat, StrategyStatistic::getResults).orElse(null);
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
     * message ID.
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
     * Validates an object
     *
     * @param o   The object to be validated
     * @param <S> The type of the object
     */
    protected <S> void validate(S o, Object... params) {

        // Field-based validators
        rejectIfNeed(new HashSet<>(validator().validate(o)));
        Class<?> clazz = (o instanceof BaseEntity) ? EntityUtils.entityClass((BaseEntity) o) : o.getClass();

        // Complex validators
        if (!extensionFactories.containsKey(clazz)) {
            ValidationFactory factory = bean("ValidationFactory", ValidationFactory.class);
            registerExtensions(clazz, factory.getValidators(clazz));
        }
        processParametrizedExtensions(clazz, o, params);
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
     * Loads extensions marked with the given annotation type. Also, the resulted list of strategies is
     * sorted in accordance with the {@link Ordered} annotation if it is defined for each strategy.
     *
     * @param marker The marker annotation
     * @return The list of found extensions
     */
    protected List<Strategy<Object>> loadExtensions(Class<? extends Annotation> marker) {
        return loadExtensions(s -> AnnotationUtils.findAnnotation(target(s).getClass(), marker) != null, "default");
    }

    /**
     * Loads extensions marked with the given annotation type. Also, the resulted list of strategies is
     * sorted in accordance with the {@link Ordered} annotation if it is defined for each strategy.
     * This function filters out the strategies that has the given extensionId and the fixed annotation.
     *
     * @param marker      The marker annotation
     * @param extensionId The extension ID that needs to be used for selecting extensions.
     * @return The list of found extensions
     */
    protected List<Strategy<Object>> loadExtensions(Class<ExtensionMarker> marker, String extensionId) {
        return loadExtensions(s -> {
            ExtensionMarker a = AnnotationUtils.findAnnotation(target(s).getClass(), marker);
            return a != null && safeEquals(extensionId, a.value());
        }, extensionId);
    }

    /**
     * Loads extensions selecting them based on the given callback's result
     *
     * @param callback The callback to determine the condition of selection
     * @return The list of found extensions
     */
    protected List<Strategy<Object>> loadExtensions(Function<Strategy<Object>, Boolean> callback, String extensionId) {

        Map<String, Strategy<Object>> beans = ctx.getBeansOfType(getClazz()); // Loading all 'Strategies'
        if (logger.isTraceEnabled()) {
            logger.trace("All 'strategy' beans: {}", beans);
        }
        List<Strategy<Object>> extensions = beans.values()
                .stream()
                .filter(callback::apply)
                .sorted(new AnnotationAwareOrderComparator()) // if an extension has the 'Ordered' annotation.
                .collect(Collectors.toList());

        logger.info("Loaded: {} extensions for the: {}/{}", extensions.size(), extensionId, target(this));
        logger.trace("Loaded '{}' strategies: {}", extensionId, extensions);
        return extensions;
    }

    @SuppressWarnings("unchecked")
    private static Class<Strategy<Object>> getClazz() {
        try {
            // Yes, it's ugly. How to solve it better?
            return (Class<Strategy<Object>>) Class.forName("ru.anr.base.services.pattern.Strategy");
        } catch (ClassNotFoundException ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TargetEnvironments getTargetEnv() {
        return TargetEnvironments.search(getProfiles());
    }

    /////// Security Functions

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
    @SafeVarargs
    protected final <S, O> S runAs(Authentication token, Function<O[], S> callback, O... args) {
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
    @SafeVarargs
    protected final <O> void runAs(Authentication token, Consumer<O[]> callback, O... args) {
        runAs(token, (Function<O[], Void>) arguments -> {
            callback.accept(arguments);
            return null;
        }, args);
    }

    /**
     * Retrieves the current authorization details if there is an authorization
     *
     * @param <S> The type of authorization details
     * @return The resulted authorization details, or null if there is no authorization.
     */
    @SuppressWarnings("unchecked")
    protected <S extends User> S authorization() {
        return nullSafe(token(), a -> (S) a.getDetails()).orElse(null);
    }

    /**
     * @return true, if the security token is authenticated
     */
    protected boolean authorized() {
        return nullSafe(token(), Authentication::isAuthenticated).orElse(false);
    }

    /**
     * Verifies whether the current security roles contains the ROLE_ROOT
     *
     * @return true, if the authorization has the root
     */
    protected boolean hasRoot() {
        return hasRole("ROLE_ROOT");
    }

    /**
     * Checks whether the current authorized roles list includes the given role or not.
     *
     * @param role The role to check
     * @return true, if the given role is presented in the security context
     */
    protected boolean hasRole(String role) {
        return roles().contains(role);
    }

    /**
     * Returns the set of current roles
     *
     * @return The roles as a set
     */
    protected Set<String> roles() {
        return set(authorization().getAuthorities().stream().map(GrantedAuthority::getAuthority));
    }

    protected Authentication token() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Executes an API strategy.
     *
     * @param clazz    The strategy command
     * @param method   The method
     * @param request  The request model
     * @param contexts The command context and parameters
     * @return The resulted command
     */
    protected APICommand apiCmd(Class<? extends ApiCommandStrategy> clazz, MethodTypes method, RequestModel request,
                                Object... contexts) {

        ApiStrategy a = ApiUtils.extract(clazz);
        APICommandFactory factory = bean(APICommandFactory.class);

        APICommand cmd = new APICommand(a.id(), a.version()).context(contexts);
        RequestModel rq = nullSafeOp(request).orElse(new RequestModel());

        cmd.setType(method);
        cmd.setRequest(rq);

        return factory.process(cmd);
    }

    /**
     * Executes an API strategy with returning the response value only.
     *
     * @param clazz    The strategy command
     * @param method   The method
     * @param request  The request model
     * @param contexts The command context and parameters
     * @return The resulted command
     */
    protected <S> S api(Class<? extends ApiCommandStrategy> clazz, MethodTypes method, RequestModel request,
                        Object... contexts) {
        return apiCmd(clazz, method, request, contexts).getResponse();
    }

    /**
     * Checks the given value is a positive number.
     *
     * @param value The value
     */
    protected void checkPositiveNumber(BigDecimal value) {
        checkIsTrue(value.signum() > 0, "number.positive", value.toString());
    }

    /**
     * Checks the given value is a positive number or zero.
     *
     * @param value The value
     */
    protected void checkPositiveNumberOrZero(BigDecimal value) {
        checkIsTrue(value.signum() >= 0, "number.positive", value.toString());
    }

    /**
     * Checks the given value is a percent value
     *
     * @param value The value
     */
    protected void checkPercentNumber(BigDecimal value) {
        checkIsTrue(value.compareTo(d("1")) <= 0, "number.percent", value.toString());
    }

    /**
     * Checks the given value satisfies the given scale value
     *
     * @param value The value
     */
    public void checkLessThanScale(int scale, BigDecimal value) {
        checkIsTrue(verifyLessThanScale(scale, value), "number.scale", value.toString(), scale);
    }

    /**
     * Checks and parses the given string parameter value as the given class. This is widely used
     * scenario when you need to get the parse number with applied all necessary checks.
     *
     * @param str       The original string value
     * @param paramName The parameter's name
     * @param clazz     The number class to parse
     * @param <S>       The type of the value
     * @return The resulted parsed number value
     */
    public <S extends Number> S parseAndCheckParam(String str, String paramName, Class<S> clazz) {
        checkParamNotNull(str, paramName);
        S num = parse(str, clazz);
        checkParamWrong(num != null, paramName, str);
        return num;
    }

    /**
     * 1. Checks whether the given value has precision less than is allowed by the
     * given scale. For example, if a currency allows the minimum value to be
     * 0.00001, then this function return true for all values which are less
     * than 0.00001 (like 0.000005, 0.00000001, etc.).
     * <p>
     * 2. Also, we check that after rounding the value is not changed, i.e. to avoid $0.023 or so.
     *
     * @param scale The scale of decimal
     * @param value The value to check
     * @return true, if the scale of the value is less or equals to the min for the given scale
     */
    public static boolean verifyLessThanScale(int scale, BigDecimal value) {
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
    protected static boolean isDefined(BaseObjectModel object) {
        return object != null && object.id != null;
    }


    /**
     * Verifies the given service is available. This function maybe used for conditional injection and allows to minimize
     * the code for doing this kind of checks.
     *
     * @param service The service to check.
     */
    protected static boolean isSupported(SupportableService service) {
        return service != null && service.isSupported();
    }

    protected static <S extends ResponseModel, T> List<S> toModel(Page<T> pages, Function<T, S> callback) {
        return pages.getContent()
                .stream()
                .map(o -> {
                    S model = callback.apply(o);
                    model.total = pages.getTotalElements();
                    model.page = pages.getNumber();
                    model.perPage = pages.getSize();
                    return model;
                })
                .collect(Collectors.toList());
    }

    protected static <S extends ResponseModel, T> List<S> toModel(Page<T> pages, Class<S> modelClass
            , Class<T> objectClass, BiConsumer<S, T> callback) {
        return toModel(pages, o -> {
            S model = inst(modelClass, new Class<?>[]{objectClass}, o);
            if (callback != null) {
                callback.accept(model, o);
            }
            return model;
        });
    }

    protected static <S extends ResponseModel, T> List<S> toModel(Page<T> pages, Class<S> modelClass
            , Class<T> objectClass) {
        return toModel(pages, modelClass, objectClass, null);
    }

    ///////////////////////////////////////////////////////////////////////////
    ///// getters/setters
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Set the default extensions
     *
     * @param extensions the extensions to set
     */
    public void setExtensions(List<Strategy<Object>> extensions) {
        registerExtensions("default", extensions);
    }

}
