/**
 * 
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
