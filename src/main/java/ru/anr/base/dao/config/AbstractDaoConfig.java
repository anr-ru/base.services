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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import ru.anr.base.BaseSpringParent;
import ru.anr.base.dao.config.repository.SecuredRepositoryImpl;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.dao.repository.SecuredRepository;
import ru.anr.base.domain.BaseEntity;

/**
 * An abstract configuration for data accessing. It contains a builder for
 * {@link PlatformTransactionManager} and caching.
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 */
@EnableTransactionManagement
@EnableCaching
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
    @DependsOn({"transactionManager"})
    public PlatformTransactionManager annotationDrivenTransactionManager() {

        // Can't use the injection here as we are unable to break the original interface.
        // Also, @Bean(...) by default, returns a bean proxy, not the real instance.
        return transactionManager();
    }

    /**
     * Definition of CacheManager. We use EhCache usually.
     *
     * @return An instance of {@link CacheManager} interface.
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

    /**
     * Creates a special bean that contains several operations that need to be
     * executed with security settings, like read/write/delete with permissions checking.
     *
     * @param dao The dao repository
     * @return The bean instance
     */
    @Bean(name = "SecuredRepository")
    @DependsOn({"BaseRepository"})
    public SecuredRepository securedDao(@Qualifier("BaseRepository") BaseRepository<BaseEntity> dao) {
        SecuredRepositoryImpl bean = new SecuredRepositoryImpl();
        bean.setDao(dao);
        return bean;
    }
}
