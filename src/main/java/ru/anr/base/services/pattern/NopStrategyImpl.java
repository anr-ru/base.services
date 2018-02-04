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
package ru.anr.base.services.pattern;

import ru.anr.base.services.BaseServiceImpl;
import ru.anr.base.services.pattern.StrategyConfig.StrategyModes;

/**
 * No operation strategy. Just does nothing and can be used for test purposes.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class NopStrategyImpl extends BaseServiceImpl implements Strategy<Object> {

    /**
     * {@inheritDoc}
     */
    @Override
    public StrategyConfig check(Object o, Object... params) {

        return new StrategyConfig(true, o, StrategyModes.Normal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object process(Object o, StrategyConfig cfg) {

        return o;
    }
}
