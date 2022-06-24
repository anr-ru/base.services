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
package ru.anr.base.services;

import java.util.Set;

/**
 * A enumeration of environments.
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2016
 */
public enum TargetEnvironments {
    /**
     * CI Environment
     */
    CI,
    /**
     * Developers' Stage Environment
     */
    DEV,
    /**
     * QA Stage Environment
     */
    QA,
    /**
     * Production Environment
     */
    PROD;

    /**
     * Searches a value of the enumeration by its name in the given set of
     * strings.
     *
     * @param s The set of strings
     * @return A first found value
     */
    public static TargetEnvironments search(Set<String> s) {
        TargetEnvironments rs = null;
        for (TargetEnvironments t : values()) {
            if (s.contains(t.name())) {
                rs = t;
                break;
            }
        }
        return rs;
    }
}
