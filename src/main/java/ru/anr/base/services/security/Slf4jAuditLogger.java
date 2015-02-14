/**
 * 
 */
package ru.anr.base.services.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.AuditableAccessControlEntry;
import org.springframework.util.Assert;

/**
 * Audit logger with a standard slf4j logging. The implementation is just copied
 * from {@link org.springframework.security.acls.domain.ConsoleAuditLogger}.
 *
 *
 * @author Alexey Romanchuk
 * @created Feb 14, 2015
 *
 */

public class Slf4jAuditLogger implements AuditLogger {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Slf4jAuditLogger.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void logIfNeeded(boolean granted, AccessControlEntry ace) {

        Assert.notNull(ace, "AccessControlEntry required");

        if (ace instanceof AuditableAccessControlEntry) {
            AuditableAccessControlEntry auditableAce = (AuditableAccessControlEntry) ace;

            if (granted && auditableAce.isAuditSuccess()) {
                logger.debug("GRANTED due to ACE: {}", ace);
            } else if (!granted && auditableAce.isAuditFailure()) {
                logger.debug("DENIED due to ACE: {}", ace);
            }
        }
    }
}
