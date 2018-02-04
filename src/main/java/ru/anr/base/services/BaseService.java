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

import java.util.Locale;

import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import ru.anr.base.domain.BaseEntity;

/**
 * A base service interface.
 * 
 * 
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 * 
 */
public interface BaseService {

    /**
     * Retrieves a text message from resource with provided code. If no message
     * found with specified code, it returns a spring in format [xxxCODExxx]. If
     * exists {@link org.springframework.context.i18n.LocaleContextHolder} the
     * current locale is used, otherwise default jvm settings.
     * 
     * @param code
     *            A Message code
     * @param args
     *            Message arguments
     * @return A text with parameters replaced with the arguments
     */
    String text(String code, Object... args);

    /**
     * Changes a state of the {@link BaseEntity} object and returns the old
     * state.
     * 
     * @param object
     *            The object
     * @param newState
     *            New state
     * @return The old state
     * 
     * @param <S>
     *            Type of state enumeration
     */
    @PreAuthorize("hasPermission(#o,'write') or hasPermission(#o,'access_state') or hasRole('ROLE_ROOT')")
    <S extends Enum<S>> S changeState(@P("o") BaseEntity object, S newState);

    /**
     * Determines the current environment if defined. By usage of spring
     * profiles like DEV, QA and PROD it is possible to organize a different
     * behavior in each situation.
     * 
     * @return A name of the environment or null, if no one from
     *         {@link TargetEnvironments} is defined in profiles.
     */
    TargetEnvironments getTargetEnv();

    /**
     * Retrieves a text message from resource with provided code and locale. If
     * no message found with specified code, it returns a spring in format
     * [xxxCODExxx].
     * 
     * @param code
     *            A Message code
     * @param locale
     *            A Locale of message
     * @param args
     *            Message arguments
     * @return A text with parameters replaced with the arguments
     */
    String textLocalized(String code, Locale locale, Object... args);
}
