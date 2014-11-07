/**
 * 
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
