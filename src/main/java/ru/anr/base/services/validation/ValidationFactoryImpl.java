/*
 * Copyright 2014 the original author or authors.
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
package ru.anr.base.services.validation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ru.anr.base.BaseSpringParent;
import ru.anr.base.services.pattern.Strategy;

/**
 * The implementation of the {@link ValidationFactory}.
 *
 *
 * @author Alexey Romanchuk
 * @created Jan 21, 2016
 *
 */
public class ValidationFactoryImpl extends BaseSpringParent implements ValidationFactory {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ValidationFactoryImpl.class);

    /**
     * A map with all defined validators
     */
    private MultiValueMap<Class<?>, Strategy<Object>> validators = new LinkedMultiValueMap<>();

    /**
     * The method to sort validators
     */
    private static final Comparator<Strategy<Object>> ORDER_SORTING = (left, right) -> {

        /**
         * Annotation 1
         */
        Validator a1 = AnnotationUtils.findAnnotation(target(left).getClass(), Validator.class);
        /**
         * Annotation 2
         */
        Validator a2 = AnnotationUtils.findAnnotation(target(right).getClass(), Validator.class);

        return a1.order() - a2.order();
    };

    /**
     * Initialization
     * 
     * @param beans
     *            All beans which are the validators
     */
    @SuppressWarnings("unchecked")
    public void init(Map<String, Object> beans) {

        beans.forEach((name, bean) -> {
            validators.add(((BaseValidator<?>) bean).getSupported(), (Strategy<Object>) bean);
        });

        /*
         * Sorting the lists of validators according to their order
         */
        validators.forEach((name, list) -> {
            list.sort(ORDER_SORTING);
        });
        logger.info("Loaded validators for {} object types", validators.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Strategy<Object>> getValidators(Class<?> clazz) {

        return validators.get(clazz);
    }
}
