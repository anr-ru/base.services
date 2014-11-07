/**
 * 
 */
package ru.anr.base.dao;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

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
public abstract class AbstractJPADaoConfig extends AbstractDaoConfig {

    /**
     * {@link DataSource} definition to override in descendants
     * 
     * @return A pooled datasource
     */
    @Bean(name = "datasource")
    public abstract DataSource dataSource();

    /**
     * Definition of {@link EntityManagerFactory}
     * 
     * @return Bean instance
     */
    @Bean(name = "entityManagerFactory")
    public EntityManagerFactory entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setDataSource(dataSource());
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {

        JpaTransactionManager txManager = new JpaTransactionManager();

        txManager.setEntityManagerFactory(entityManagerFactory());
        txManager.setDataSource(dataSource());
        return txManager;
    }
}
