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
package ru.anr.base.dao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * An annotation-based dao config for non-JTA environments. It includes an
 * abstract datasource definition, JPA based entity factory and JPA transaction
 * manager.
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 */
public abstract class AbstractJPADaoConfig extends AbstractDaoConfig {

    /**
     * Connection userName
     */
    private String userName;

    /**
     * Connection password
     */
    private String password;

    /**
     * JDBC url
     */
    private String jdbcUrl;

    /**
     * The driver class name for JDBC
     */
    private String jdbcDriverClassName;

    /**
     * Data source (if provided)
     */
    private DataSource jndiDataSource;

    /**
     * {@link DataSource} definition to override in descendants
     *
     * @return A pooled datasource
     */
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        return jndiDataSource;
    }

    /**
     * Persistence file location
     */
    private String persistenceFileLocation = "classpath:META-INF/persistence-local.xml";

    /**
     * Definition of {@link EntityManagerFactory}
     *
     * @return Bean instance
     */
    @Bean(name = "entityManagerFactory")
    @DependsOn({"dataSource"})
    public EntityManagerFactory entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        // See comments in AbstractDaoConfig#annotationDrivenTransactionManager
        factory.setDataSource(dataSource());
        factory.setPersistenceXmlLocation(persistenceFileLocation);
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Bean(name = "transactionManager")
    @DependsOn({"dataSource", "entityManagerFactory"})
    public PlatformTransactionManager transactionManager() {

        JpaTransactionManager txManager = new JpaTransactionManager();
        // See comments in AbstractDaoConfig#annotationDrivenTransactionManager
        txManager.setEntityManagerFactory(entityManagerFactory());
        txManager.setDataSource(dataSource());
        return txManager;
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {

        this.userName = userName;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {

        this.password = password;
    }

    /**
     * @param jdbcUrl the jdbcUrl to set
     */
    public void setJdbcUrl(String jdbcUrl) {

        this.jdbcUrl = jdbcUrl;
    }

    /**
     * @return the userName
     */
    public String getUserName() {

        return userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {

        return password;
    }

    /**
     * @return the jdbcUrl
     */
    public String getJdbcUrl() {

        return jdbcUrl;
    }

    /**
     * @return the jdbcDriverClassName
     */
    public String getJdbcDriverClassName() {

        return jdbcDriverClassName;
    }

    /**
     * @param jdbcDriverClassName the jdbcDriverClassName to set
     */
    public void setJdbcDriverClassName(String jdbcDriverClassName) {

        this.jdbcDriverClassName = jdbcDriverClassName;
    }

    /**
     * @param persistenceFileLocation the persistenceFileLocation to set
     */
    public void setPersistenceFileLocation(String persistenceFileLocation) {

        this.persistenceFileLocation = persistenceFileLocation;
    }

    /**
     * @param jndiDataSource the jndiDataSource to set
     */
    public void setJndiDataSource(DataSource jndiDataSource) {

        this.jndiDataSource = jndiDataSource;
    }

}
