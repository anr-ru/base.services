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
package ru.anr.base.services.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import ru.anr.base.BaseSpringParent;

import javax.validation.Validator;

/**
 * The validation configuration (depends on {@link ru.anr.base.services.MessagePropertiesConfig}).
 *
 * @author Alexey Romanchuk
 * @created Jan 30, 2015
 */
@Configuration
public class ValidationConfig extends BaseSpringParent {

    /**
     * The message source with text message configs
     */
    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;

    /**
     * Exports the validation bean, which can be used as default validation
     *
     * @return The resulted {@link Validator} instance
     */
    @Bean(name = "validator")
    @DependsOn("messageSource")
    public Validator validator() {

        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);

        return bean;
    }

    /**
     * Exports an interceptor for validation method postprocessing.
     *
     * @param validator The validator instance
     * @return The bean instance
     */
    @Bean(name = "methodValidator")
    @DependsOn("validator")
    public MethodValidationPostProcessor methodValidator(Validator validator) {

        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);

        return processor;
    }

    /**
     * Creates the {@link ValidationFactory} bean
     *
     * @return The bean instance
     */
    @Bean(name = "ValidationFactory")
    public ValidationFactory factory() {
        return new ValidationFactoryImpl();
    }
}
