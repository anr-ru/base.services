/**
 * 
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
 * Configuration for Api commands and factory. Exports
 * {@link APICommandFactoryImpl} bean.
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
     * Finding all api commands and storing them in a factory.
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
