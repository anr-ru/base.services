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
package ru.anr.base.services.pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anr.base.BaseSpringParent;
import ru.anr.base.services.pattern.StrategyConfig.StrategyModes;

import java.util.List;

/**
 * An implementation of {@link StrategyFactory}
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */

public class StrategyFactoryImpl extends BaseSpringParent implements StrategyFactory {

    private static final Logger logger = LoggerFactory.getLogger(StrategyFactoryImpl.class);

    /**
     * Chain of strategies. We use a list here to make sure the valid sequence
     * is applied.
     */
    private final List<Strategy<Object>> strategies;

    private boolean debug = false;

    /**
     * Constructor
     *
     * @param strategies List of strategies
     */
    public StrategyFactoryImpl(List<Strategy<Object>> strategies) {
        super();
        this.strategies = list(strategies); // null safe
    }

    public StrategyFactoryImpl(List<Strategy<Object>> strategies, boolean debug) {
        super();
        this.strategies = list(strategies); // null safe
        this.debug = debug;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StrategyStatistic process(Object object, Object... params) {

        Object o = object;
        List<Class<?>> list = list();
        List<Object> results = list();

        for (Strategy<Object> s : strategies) {

            StrategyConfig cfg = s.check(object, params);

            if (cfg.isApplicable()) {

                o = s.process(o, cfg);

                if (debug) {
                    // As it can be expensive
                    list.add(target(s).getClass());
                }
                results.addAll(cfg.getCollection());

                if (cfg.getMode() == StrategyModes.TerminateAfter) {
                    logger.debug("A chain terminated at {} execution", s);
                    break;
                }
            }
        }

        if (strategies.isEmpty()) {
            logger.debug("No one strategy executed due to an empty strategy list");
        }
        return new StrategyStatistic(o, list, results);
    }
}
