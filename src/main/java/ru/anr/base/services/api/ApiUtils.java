package ru.anr.base.services.api;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * Utils for API.
 *
 * @author Alexey Romanchuk
 * @created Apr 10, 2021
 */
public class ApiUtils {

    /**
     * Extract API Strategy information from the given API strategy class.
     *
     * @param clazz The class of API strategy
     * @return the resulted annotation
     */
    public static ApiStrategy extract(Class<?> clazz) {
        ApiStrategy a = AnnotationUtils.findAnnotation(clazz, ApiStrategy.class);
        Assert.notNull(a, "Not an API Strategy");
        return a;
    }
}
