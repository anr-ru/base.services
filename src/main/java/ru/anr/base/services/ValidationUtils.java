package ru.anr.base.services;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import ru.anr.base.BaseParent;

/**
 * Validation utils.
 *
 *
 * @author Alexey Romanchuk
 * @created Feb 22, 2015
 *
 */
public final class ValidationUtils extends BaseParent {

    /**
     * Private constructor
     */
    private ValidationUtils() {

        super();
    }

    /**
     * Extract all error message from {@link ConstraintViolation} as a single
     * string
     * 
     * @param violations
     *            collection of violations
     * @return All errors as a comma-separated string
     */
    public static String getAllErrorsAsString(Set<ConstraintViolation<?>> violations) {

        return violations.stream().map(v -> v.getMessage()).collect(Collectors.toList()).toString();
    }
}
