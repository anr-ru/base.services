/**
 * 
 */
package ru.anr.base.dao;

import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import ru.anr.base.BaseSpringParent;

/**
 * Abstract configuration for data access - contains a query for
 * {@link PlatformTransactionManager} and Caching definition
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */
@EnableTransactionManagement
public abstract class AbstractDaoConfig extends BaseSpringParent implements TransactionManagementConfigurer {

    /**
     * A factory for transaction manager
     * 
     * @return An instance of {@link PlatformTransactionManager}
     */
    @Bean(name = "transactionManager")
    public abstract PlatformTransactionManager transactionManager();

    /**
     * {@inheritDoc}
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {

        return transactionManager();
    }

    /**
     * Definition of CacheManager. We use EhCache
     * 
     * @return An instance of {@link CacheManager} interface
     */
    @Bean(name = "cacheManager")
    public CacheManager cacheManager() {

        EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
        factory.setAcceptExisting(true);
        factory.setShared(false);
        factory.setConfigLocation(new ClassPathResource("ehcache.xml"));

        factory.afterPropertiesSet();

        EhCacheCacheManager mgr = new EhCacheCacheManager();
        mgr.setCacheManager(factory.getObject());
        mgr.setTransactionAware(true);
        mgr.afterPropertiesSet();

        return mgr;
    }

}