/**
 * 
 */
package ru.anr.base.services.pattern;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.anr.base.BaseSpringParent;
import ru.anr.base.services.pattern.StrategyConfig.StrategyModes;

/**
 * Implementation of {@link StrategyFactory}
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class StrategyFactoryImpl extends BaseSpringParent implements StrategyFactory {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(StrategyFactoryImpl.class);

    /**
     * Chain of strategies. We use a list here to make sure a valild sequence is
     * applied.
     */
    private final List<Strategy<Object>> strategies;

    /**
     * Constructor
     * 
     * @param strategies
     *            List of strategies
     */
    public StrategyFactoryImpl(List<Strategy<Object>> strategies) {

        super();
        this.strategies = list(strategies); // null safe

        logger.info("Loaded {} strategies", strategies.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StrategyStatistic process(Object object, Object... params) {

        Object o = object;
        List<Class<?>> list = list();

        for (Strategy<Object> s : strategies) {

            StrategyConfig cfg = s.check(object, params);

            if (cfg.isApplicable()) {

                o = s.process(o, cfg);
                list.add(target(s).getClass());

                if (cfg.getMode() == StrategyModes.TerminateAfter) {
                    logger.debug("A chain terminated at {} execution", s);
                    break;
                }
            }
        }

        if (strategies.isEmpty()) {
            logger.warn("No one strategy executed due to an empty strategy list");
        }
        return new StrategyStatistic(o, list);
    }
}
