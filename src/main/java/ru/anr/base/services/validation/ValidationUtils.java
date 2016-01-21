package ru.anr.base.services.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;

import ru.anr.base.BaseParent;

/**
 * Validation utilities.
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
     * 
     * @param <S>
     *            The class of the object
     */
    public static <S> String getAllErrorsAsString(Set<ConstraintViolation<? extends S>> violations) {

        return list(violations.stream().map(v -> v.getMessage()).distinct()).toString();
    }
}
