/**
 * 
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
