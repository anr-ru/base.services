package ru.anr.base.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import ru.anr.base.BaseParent;

import javax.sql.DataSource;

/**
 * A common Authorization / ACL configurator.
 *
 * @author Alexey Romanchuk
 * @created Feb 14, 2015
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class ACLConfig extends GlobalMethodSecurityConfiguration {

    /**
     * An authority to manage acl (take ownership, modify, ...)
     */
    private final String aclManageRole;

    /**
     * The name for cache. This name must be define in cache managent system
     */
    private final String aclCacheName;

    /**
     * Class identity query - a way to get next identifier. By default used a
     * HSQL "call identity()". Must be overriden in other databases.
     * <p>
     * For example, "SELECT HIBERNATE_SEQUENCE.CURRVAL FROM DUAL"
     */
    private final String classIdentityQuery;

    /**
     * Sid identity query - a way to get next identifier. By default used a HSQL
     * "call identity()". Must be overriden in other databases.
     * <p>
     * For example, "SELECT HIBERNATE_SEQUENCE.CURRVAL FROM DUAL"
     */
    private final String sidIdentityQuery;

    /**
     * To use or not use the acls
     */
    private final boolean useAcls;

    private final RunAsManager runAs;

    private final AuthenticationManager authenticationManagerRef;


    /**
     * Construction of the configuration
     */
    public ACLConfig(String aclManageRole, String aclCacheName,
                     String classIdentityQuery, String sidIdentityQuery,
                     boolean useAcls, RunAsManager runAs,
                     AuthenticationManager authenticationManagerRef) {
        this.aclManageRole = aclManageRole;
        this.aclCacheName = aclCacheName;
        this.classIdentityQuery = classIdentityQuery;
        this.sidIdentityQuery = sidIdentityQuery;
        this.useAcls = useAcls;
        this.runAs = runAs;
        this.authenticationManagerRef = authenticationManagerRef;
    }


    /**
     * An 'AclService' bean. Uses a preconfigured spring {@link CacheManager}.
     *
     * @param cacheManager A ref to Spring {@link CacheManager}
     * @param datasource   The datasource
     * @return The bean instance.
     */
    @Bean(name = "aclService")
    @DependsOn({"cacheManager", "datasource"})
    public MutableAclService aclServiceBuild(CacheManager cacheManager, DataSource datasource) {

        PermissionGrantingStrategy grantingStrategy = new DefaultPermissionGrantingStrategy(new Slf4jAuditLogger());
        GrantedAuthority role = new SimpleGrantedAuthority(aclManageRole);

        AclAuthorizationStrategy authorizationStrategy = new AclAuthorizationStrategyImpl(role);

        Assert.notNull(aclCacheName, "The name for cache must be specified");
        Cache cache = cacheManager.getCache(aclCacheName);

        Assert.notNull(cache, "The name '" + aclCacheName + "' not found");
        AclCache aclCache = new SpringCacheBasedAclCache(cache, grantingStrategy, authorizationStrategy);

        LookupStrategy lookupStrategy =
                new BasicLookupStrategy(datasource, aclCache, authorizationStrategy, grantingStrategy);

        JdbcMutableAclService s = new JdbcMutableAclService(datasource, lookupStrategy, aclCache);

        if (BaseParent.notEmpty(classIdentityQuery)) {
            s.setClassIdentityQuery(classIdentityQuery);
        }
        if (BaseParent.notEmpty(sidIdentityQuery)) {
            s.setSidIdentityQuery(sidIdentityQuery);
        }

        /*
         * Specifying a permission evaluator with our acl service
         */
        //setPermissionEvaluator(BaseParent.list(new BaseEntityPermissionEvaluator(s, useAcls)));
        return s;
    }

    @Autowired
    private MutableAclService aclService;


    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new BaseEntityPermissionEvaluator(aclService, useAcls));
        return expressionHandler;
    }

    @Override
    protected AuthenticationManager authenticationManager() {
        return this.authenticationManagerRef;
    }

    @Override
    protected RunAsManager runAsManager() {
        return runAs;
    }
}
