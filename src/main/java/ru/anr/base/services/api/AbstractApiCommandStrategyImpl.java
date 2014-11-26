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
package ru.anr.base.services.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

import ru.anr.base.ApplicationException;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.MethodTypes;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.services.BaseServiceImpl;

/**
 * Implementation of base Api Command strategy.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class AbstractApiCommandStrategyImpl extends BaseServiceImpl implements ApiCommandStrategy {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractApiCommandStrategyImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand process(APICommand cmd) {

        cmd.setResponse(doInvoke(cmd.getType(), cmd));
        return cmd;
    }

    /**
     * Preloaded handler methods
     */
    private final Map<MethodTypes, Method> handlers = new HashMap<>();

    /**
     * Initialization of strategy
     */
    @PostConstruct
    public void initMethods() {

        Class<?> clazz = target(this).getClass();
        ReflectionUtils.doWithMethods(clazz, new MethodCallback() {

            @Override
            public void doWith(Method method) {

                // This part of code found in some Spring Mvc annotation
                // processors
                Method specificMethod = ClassUtils.getMostSpecificMethod(method, clazz);
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

                if (isHandlerMethod(specificMethod)
                        && (bridgedMethod == specificMethod || !isHandlerMethod(bridgedMethod))) {

                    ApiMethod a = AnnotationUtils.getAnnotation(specificMethod, ApiMethod.class);
                    if (checkHandlerMethod(specificMethod)) {
                        handlers.put(a.value(), specificMethod);
                    }
                }
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);
    }

    /**
     * Performs checking method return type and argument with logging
     * information if these methods are not valid.
     * 
     * @param method
     *            Annotated method
     * @return true, if method is valid
     */
    private boolean checkHandlerMethod(Method method) {

        boolean rs = true;

        if (!Modifier.isPublic(method.getModifiers())) {
            logger.error("Method '{}' of class '{}' should be public", method.getName(), this.getClass());
            rs = false;
        }

        if (Modifier.isAbstract(method.getModifiers())) {
            logger.error("Method '{}' of class '{}' cannot be abstract", method.getName(), this.getClass());
            rs = false;
        }

        if (method.getReturnType() != ResponseModel.class) {

            logger.error("Method '{}' of class '{}' must have ResponseModel as return type", method.getName(),
                    this.getClass());
            rs = false;
        }

        Class<?>[] types = method.getParameterTypes();
        if (ArrayUtils.isEmpty(types) || types.length > 1 || types[0] != APICommand.class) {

            logger.error("Method '{}' of class '{}' must have ApiCommand as a single agrument", method.getName(),
                    this.getClass());
            rs = false;
        }
        return rs;
    }

    /**
     * Checke the the method is api method handler (annotation {@link ApiMethod}
     * is presented).
     * 
     * @param method
     *            Method to check
     * @return true, if method has {@link ApiMethod} annotation
     */
    private boolean isHandlerMethod(Method method) {

        return AnnotationUtils.getAnnotation(method, ApiMethod.class) != null;
    }

    /**
     * Handler method invocation
     * 
     * @param apiMethod
     *            Api method type
     * @param cmd
     *            Api command
     * @return Response
     */
    private ResponseModel doInvoke(MethodTypes apiMethod, APICommand cmd) {

        Method method = this.handlers.get(apiMethod);
        if (method == null) {
            throw new UnsupportedOperationException("Method '" + apiMethod.name() + "' is unsupported");
        }
        try {
            return (ResponseModel) method.invoke(target(this), cmd);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * @return the handlers
     */
    public Map<MethodTypes, Method> getHandlers() {

        return handlers;
    }
}
