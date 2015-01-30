/**
 * 
 */
package ru.anr.base.services;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * Validation configuration (depends on {@link MessagePropertiesConfig}).
 *
 *
 * @author Alexey Romanchuk
 * @created Jan 30, 2015
 *
 */
@Configuration
@Import(MessagePropertiesConfig.class)
public class ValidationConfig {

    /**
     * Message source with text message configs
     */
    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;

    /**
     * Exporting a validation bean, which can be used as default validation and
     * injected {@link org.springframework.beans.factory.annotation.Autowired}
     * annocation
     * 
     * @return {@link Validator} instance
     */
    @Bean(name = "validator")
    @DependsOn("messageSource")
    public Validator validator() {

        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);

        return bean;
    }

    /**
     * Exporting an interceptor for validation method execution
     * 
     * @param validator
     *            Validator to use
     * @return A bean instance
     */
    @Bean(name = "methodValidator")
    @DependsOn("validator")
    public MethodValidationPostProcessor methodValidator(Validator validator) {

        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);

        return processor;
    }
}
