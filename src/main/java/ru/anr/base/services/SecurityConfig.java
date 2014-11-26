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

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * Spring security configuration. This context config should be imported to add
 * base authentication/authorization settings.
 * 
 * 
 * See
 * {@link org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration}
 * implementation to understand a default
 * {@link org.springframework.security.access.AccessDecisionManager}.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 *
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(MessagePropertiesConfig.class)
public class SecurityConfig {

    /**
     * List of all security providers
     */
    private List<AuthenticationProvider> providers;

    /**
     * Defining a {@link AuthenticationManager} bean
     * 
     * @param messageSource
     *            Message source instance
     * @return A bean instance
     * 
     * @throws Exception
     *             In case of ProviderManager initialization error
     */
    @Bean(name = "authenticationManager")
    @DependsOn("messageSource")
    public AuthenticationManager authenticationManager(MessageSource messageSource) throws Exception {

        ProviderManager providerManager = new ProviderManager(providers);
        providerManager.setMessageSource(messageSource);
        providerManager.setAuthenticationEventPublisher(new DefaultAuthenticationEventPublisher());

        providerManager.afterPropertiesSet();
        return providerManager;
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param providers
     *            the providers to set
     */
    public void setProviders(List<AuthenticationProvider> providers) {

        this.providers = providers;
    }
}
