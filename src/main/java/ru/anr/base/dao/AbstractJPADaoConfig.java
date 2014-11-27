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
     * Connection userName
     */
    private String userName;

    /**
     * Password
     */
    private String password;

    /**
     * JDBC url
     */
    private String jdbcUrl;

    /**
     * Driver class for JDBC
     */
    private String jdbcDriverClassName;

    /**
     * {@link DataSource} definition to override in descendants
     * 
     * @return A pooled datasource
     */
    @Bean(name = "datasource")
    public abstract DataSource dataSource();

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
    public EntityManagerFactory entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
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
    public PlatformTransactionManager transactionManager() {

        JpaTransactionManager txManager = new JpaTransactionManager();

        txManager.setEntityManagerFactory(entityManagerFactory());
        txManager.setDataSource(dataSource());
        return txManager;
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {

        this.userName = userName;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {

        this.password = password;
    }

    /**
     * @param jdbcUrl
     *            the jdbcUrl to set
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
     * @param jdbcDriverClassName
     *            the jdbcDriverClassName to set
     */
    public void setJdbcDriverClassName(String jdbcDriverClassName) {

        this.jdbcDriverClassName = jdbcDriverClassName;
    }

    /**
     * @param persistenceFileLocation
     *            the persistenceFileLocation to set
     */
    public void setPersistenceFileLocation(String persistenceFileLocation) {

        this.persistenceFileLocation = persistenceFileLocation;
    }

}
