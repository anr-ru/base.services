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
        factory.setPersistenceXmlLocation("classpath:META-INF/persistence-local.xml");
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
}
