package ru.anr.base.tests;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.anr.base.ApplicationException;
import ru.anr.base.dao.config.AbstractJPADaoConfig;
import ru.anr.base.dao.config.repository.BaseRepositoryFactoryBean;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * Test JPA configuration, which uses pooled data source.
 * <p></p>
 * Unfortunately, repositoryFactoryBeanClass should be repeated in all
 * descendants of this class.
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 */
@EnableJpaRepositories(repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public abstract class AbstractLocalDaoConfig extends AbstractJPADaoConfig {

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
            ds.setMaxPoolSize(15);

        } catch (PropertyVetoException ex) {
            throw new ApplicationException(ex);
        }
        return ds;
    }
}
