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

/**
 * A 'Service' interface
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
}
