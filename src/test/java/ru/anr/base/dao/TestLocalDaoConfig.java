/**
 * 
 */
package ru.anr.base.dao;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

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
public class TestLocalDaoConfig extends AbstractJPADaoConfig {

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource dataSource() {

        ComboPooledDataSource ds = new ComboPooledDataSource();

        try {

            ds.setDriverClass(getJdbcDriverClassName());
            ds.setJdbcUrl(getJdbcUrl());
            ds.setUser(getUserName());
            ds.setPassword(getPassword());

            ds.setInitialPoolSize(1);
            ds.setMinPoolSize(1);
            ds.setMaxPoolSize(3);

        } catch (PropertyVetoException ex) {
            throw new ApplicationException(ex);
        }

        return ds;
    }
}
