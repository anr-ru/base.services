/*
 * Copyright 2014-2024 the original author or authors.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.AuditableAccessControlEntry;
import org.springframework.util.Assert;

/**
 * An audit logger with the standard SLF4j logging. The implementation is just copied
 * from {@link org.springframework.security.acls.domain.ConsoleAuditLogger}.
 *
 * @author Alexey Romanchuk
 * @created Feb 14, 2015
 */
public class Slf4jAuditLogger implements AuditLogger {

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
