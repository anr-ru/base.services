/**
 * 
 */
package ru.anr.base.services;

import java.util.Set;

/**
 * A enumeration of environments.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 3, 2016
 *
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
     * @param s
     *            The set of strings
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
