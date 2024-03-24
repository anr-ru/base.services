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
package ru.anr.base.services;

import java.lang.annotation.*;

/**
 * A marker for loaded extensions of services. Each marker should have
 * the value to be set for selecting the required extension category.
 * <p>
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
     *
     * @return The resulted value
     */
    String value();
}
