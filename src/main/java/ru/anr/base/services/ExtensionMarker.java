package ru.anr.base.services;

import java.lang.annotation.*;

/**
 * A marker for loaded extensions of services. Each marker should have
 * the value to be set for selecting the required extension category.
 * <p></p>
 * If you need to set the order for loading groups of extensions.
 *
 * @author Alexey Romanchuk
 * @created Sep 25, 2022
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExtensionMarker {
    /**
     * The extension category marker.
     * @return The resulted value
     */
    String value();
}
