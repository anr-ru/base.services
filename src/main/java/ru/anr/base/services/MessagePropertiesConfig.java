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

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */
@Configuration
public class MessagePropertiesConfig {

    /**
     * Paths to each property file
     */
    private String[] paths = new String[]{};

    /**
     * Defining a {@link MessageSourceAccessor} to get messages for specific
     * locale.
     * 
     * @param messageSource
     *            Injection of {@link MessageSource} bean defined below
     * 
     * @return A bean instance
     */
    @Bean(name = "messageSourceAccessor")
    @DependsOn("messageSource")
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {

        return new MessageSourceAccessor(messageSource);
    }

    /**
     * Defining a message source bean
     * 
     * @return A bean instance
     */
    @Bean(name = "messageSource")
    public MessageSource messageSource() {

        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasenames(paths);
        source.setCacheSeconds(60); // every one minute to be reloaded

        return source;
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param paths
     *            the paths to set
     */
    public void setPaths(String... paths) {

        this.paths = ArrayUtils.clone(paths);
    }
}
