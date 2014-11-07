/**
 * 
 */
package ru.anr.base.dao;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import org.hsqldb.jdbcDriver;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import ru.anr.base.ApplicationException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Test JPA configuration, which uses HSQL database datasource.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */
@Configuration
@EnableJpaRepositories(basePackages = { "ru.anr.base.samples.dao" }, repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public class DaoConfig extends AbstractJPADaoConfig {

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
     * {@inheritDoc}
     */
    @Override
    public DataSource dataSource() {

        ComboPooledDataSource ds = new ComboPooledDataSource();

        try {

            ds.setDriverClass(jdbcDriver.class.getName());
            ds.setJdbcUrl(jdbcUrl);
            ds.setUser(userName);
            ds.setPassword(password);

            ds.setInitialPoolSize(1);
            ds.setMinPoolSize(1);
            ds.setMaxPoolSize(3);

        } catch (PropertyVetoException ex) {
            throw new ApplicationException(ex);
        }

        return ds;
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
}
