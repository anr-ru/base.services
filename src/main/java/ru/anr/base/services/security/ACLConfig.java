/**
 * 
 */
package ru.anr.base.services.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.AclPermissionEvaluator;
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

/**
 * Common Authorization / ACL configurator.
 *
 *
 * @author Alexey Romanchuk
 * @created Feb 14, 2015
 *
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class ACLConfig extends GlobalMethodSecurityConfiguration {

    /**
     * Ref to datasource to store acl data
     */
    private DataSource dataSource;

    /**
     * An authority to manage acl (take ownership, modify, ...)
     */
    private String aclManageRole = "ROLE_ACL_ADMIN";

    /**
     * The name for cache. This name must be define in cache managent system
     */
    private String aclCacheName;

    /**
     * Class identity query - a way to get next identifier. By default used a
     * HSQL "call identity()". Must be overriden in other databases.
     * 
     * For example, "SELECT HIBERNATE_SEQUENCE.CURRVAL FROM DUAL"
     */
    private String classIdentityQuery;

    /**
     * Sid identity query - a way to get next identifier. By default used a HSQL
     * "call identity()". Must be overriden in other databases.
     * 
     * For example, "SELECT HIBERNATE_SEQUENCE.CURRVAL FROM DUAL"
     */
    private String sidIdentityQuery;

    /**
     * An 'AclService' bean. Uses a preconfigured spring {@link CacheManager}.
     * 
     * @param cacheManager
     *            A ref to Spring {@link CacheManager}
     * @return The bean instance.
     */
    @Bean(name = "aclService")
    public MutableAclService aclServiceBuild(CacheManager cacheManager) {

        PermissionGrantingStrategy grantingStrategy = new DefaultPermissionGrantingStrategy(new Slf4jAuditLogger());
        GrantedAuthority role = new SimpleGrantedAuthority(aclManageRole);

        AclAuthorizationStrategy authorizationStrategy = new AclAuthorizationStrategyImpl(role);

        Assert.notNull(aclCacheName, "The name for cache must be specified");
        Cache cache = cacheManager.getCache(aclCacheName);

        Assert.notNull(cache, "The name '" + aclCacheName + "' not found");
        AclCache aclCache = new SpringCacheBasedAclCache(cache, grantingStrategy, authorizationStrategy);

        LookupStrategy lookupStrategy =
                new BasicLookupStrategy(dataSource, aclCache, authorizationStrategy, grantingStrategy);

        MutableAclService s = new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);

        if (classIdentityQuery != null) {
            ((JdbcMutableAclService) s).setClassIdentityQuery(classIdentityQuery);
        }
        if (sidIdentityQuery != null) {
            ((JdbcMutableAclService) s).setSidIdentityQuery(sidIdentityQuery);
        }

        /*
         * Specifing permission evaluator with our acl service
         */
        setPermissionEvaluator(BaseParent.list(new AclPermissionEvaluator(s)));
        return s;
    }

    // /////////////// Overrides //////////////////////////////////////////////\

    /**
     * Ref to {@link AuthenticationManager}
     */
    @Autowired
    private AuthenticationManager authenticationManagerRef;

    /**
     * {@inheritDoc}
     */
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {

        return authenticationManagerRef;
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param dataSource
     *            the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    /**
     * @param aclManageRole
     *            the aclManageRole to set
     */
    public void setAclManageRole(String aclManageRole) {

        this.aclManageRole = aclManageRole;
    }

    /**
     * @param aclCacheName
     *            the aclCacheName to set
     */
    public void setAclCacheName(String aclCacheName) {

        this.aclCacheName = aclCacheName;
    }

    /**
     * @param classIdentityQuery
     *            the classIdentityQuery to set
     */
    public void setClassIdentityQuery(String classIdentityQuery) {

        this.classIdentityQuery = classIdentityQuery;
    }

    /**
     * @param sidIdentityQuery
     *            the sidIdentityQuery to set
     */
    public void setSidIdentityQuery(String sidIdentityQuery) {

        this.sidIdentityQuery = sidIdentityQuery;
    }

    /**
     * @param authenticationManager
     *            the authenticationManager to set
     */
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {

        this.authenticationManagerRef = authenticationManager;
    }
}
