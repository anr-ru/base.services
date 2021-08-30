/**
 *
 */
package ru.anr.base.dao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.anr.base.dao.config.repository.BaseRepositoryFactoryBean;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.samples.dao.BaseDao;
import ru.anr.base.tests.AbstractLocalDaoConfig;

/**
 * 'Just-config' version of DAO for local tests.
 *
 *
 * @author Alexey Romanchuk
 * @created Dec 18, 2014
 *
 */
@Configuration
@EnableJpaRepositories(
        basePackageClasses = {BaseRepository.class, BaseDao.class},
        repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public class LocalDaoConfig extends AbstractLocalDaoConfig {

    /**
     * Empty
     */
}
