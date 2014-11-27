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
package ru.anr.base.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.config.JtaTransactionManagerFactoryBean;

/**
 * Annotation-based dao config for JPA mode. It includes abstract datasource
 * definition via c3p0 pool, JPA based entity factory and JPA transaction
 * manager.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */
@Configuration
public class BaseJTADaoConfig extends AbstractDaoConfig {

    /**
     * {@inheritDoc}
     */
    @Override
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {

        JtaTransactionManagerFactoryBean factory = new JtaTransactionManagerFactoryBean();
        return factory.getObject();
    }
}
