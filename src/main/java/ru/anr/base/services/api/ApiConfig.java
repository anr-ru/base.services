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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ru.anr.base.BaseSpringParent;
import ru.anr.base.services.serializer.SerializationConfig;

/**
 * A configuration for API commands and the factory. Exports
 * {@link APICommandFactoryImpl} the factory bean.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

@Configuration
@Import(SerializationConfig.class)
public class ApiConfig extends BaseSpringParent {

    /**
     * Reference for context
     */
    @Autowired
    private ApplicationContext ctx;

    /**
     * Finds all API commands and stores them in the factory.
     * 
     * @return A factory bean instance
     */
    @Bean(name = "apiCommandFactory")
    public APICommandFactory factory() {

        APICommandFactoryImpl impl = new APICommandFactoryImpl();

        Map<String, ApiCommandStrategy> beans = ctx.getBeansOfType(ApiCommandStrategy.class);
        impl.registerApi(beans);

        return impl;
    }
}
