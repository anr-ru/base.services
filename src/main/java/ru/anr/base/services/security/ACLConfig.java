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
package ru.anr.base.services.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import ru.anr.base.BaseParent;

import javax.sql.DataSource;

/**
 * A common Authorization and ACL configurator.
 *
 * @author Alexey Romanchuk
 * @created Feb 14, 2015
 */
@EnableMethodSecurity
@Configuration
public class ACLConfig {
    /**
     * An authority to manage acl (take ownership, modify, ...)
     */
    private final String aclManageRole;

    /**
     * The name for cache. This name must be defined in the cache management system
     */
    private final String aclCacheName;

    /**
     * Class Identity Query - a way to get next identifier. By default, we use the
     * HSQL "call identity()". Must be overridden in other databases.
     * <p>
     * For example, "SELECT HIBERNATE_SEQUENCE.CURRVAL FROM DUAL"
     */
    private final String classIdentityQuery;

    /**
     * Sid Identity Query - a way to get next identifier. By default, we use the HSQL
     * "call identity()". Must be overriden in other databases.
     * <p>
     * For example, "SELECT HIBERNATE_SEQUENCE.CURRVAL FROM DUAL"
     */
    private final String sidIdentityQuery;

    /**
     * Construction of the configuration
     *
     * @param aclCacheName       The ACL cache name
     * @param aclManageRole      The ACL role
     * @param classIdentityQuery The identity query
     * @param sidIdentityQuery   The sid identity query
     */
    public ACLConfig(String aclManageRole, String aclCacheName,
                     String classIdentityQuery, String sidIdentityQuery) {
        this.aclManageRole = aclManageRole;
        this.aclCacheName = aclCacheName;
        this.classIdentityQuery = classIdentityQuery;
        this.sidIdentityQuery = sidIdentityQuery;
    }


    /**
     * An 'AclService' bean. Uses a preconfigured spring {@link CacheManager}.
     *
     * @param cacheManager A ref to Spring {@link CacheManager}
     * @param datasource   The datasource
     * @return The bean instance.
     */
    @Bean(name = "aclService")
    @DependsOn({"cacheManager", "dataSource"})
    public MutableAclService aclServiceBuild(CacheManager cacheManager, @Qualifier("dataSource") DataSource datasource) {

        PermissionGrantingStrategy grantingStrategy = new DefaultPermissionGrantingStrategy(new Slf4jAuditLogger());
        GrantedAuthority role = new SimpleGrantedAuthority(aclManageRole);

        AclAuthorizationStrategy authorizationStrategy = new AclAuthorizationStrategyImpl(role);

        Assert.notNull(aclCacheName, "The name for cache must be specified");
        Cache cache = cacheManager.getCache(aclCacheName);

        Assert.notNull(cache, "The name '" + aclCacheName + "' not found");
        AclCache aclCache = new SpringCacheBasedAclCache(cache, grantingStrategy, authorizationStrategy);

        LookupStrategy lookupStrategy = new BasicLookupStrategy(datasource, aclCache, authorizationStrategy, grantingStrategy);
        JdbcMutableAclService s = new JdbcMutableAclService(datasource, lookupStrategy, aclCache);

        if (BaseParent.notEmpty(classIdentityQuery)) {
            s.setClassIdentityQuery(classIdentityQuery);
        }
        if (BaseParent.notEmpty(sidIdentityQuery)) {
            s.setSidIdentityQuery(sidIdentityQuery);
        }
        return s;
    }


    /**
     * The main method security bean - defined our own rules checking on methods.
     *
     * @param aclService The ACL service to use
     * @param useACL     true, if we need to use ACL on objects
     * @return The bean instance
     */
    @Bean
    @DependsOn({"aclService"})
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(AclService aclService,
                                                                           @Value("${security.use.acl:false}") boolean useACL) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(new BaseEntityPermissionEvaluator(aclService, useACL));
        return handler;
    }
}
