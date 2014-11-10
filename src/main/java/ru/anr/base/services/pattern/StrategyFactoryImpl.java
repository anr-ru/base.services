/**
 * 
 */
package ru.anr.base.services.pattern;

import java.util.List;

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
     * Chain of strategies. We use a list here to make sure a valild sequence is
     * applied.
     */
    private final List<Strategy> strategies;

    /**
     * Constructor
     * 
     * @param strategies
     *            List of strategies
     */
    public StrategyFactoryImpl(List<Strategy> strategies) {

        super();
        this.strategies = list(strategies); // null safe
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StrategyStatistic process(Object object, Object... params) {

        Object o = object;
        List<Class<?>> list = list();

        for (Strategy s : strategies) {

            StrategyConfig cfg = s.check(object, params);

            if (cfg.isApplicable()) {

                o = s.process(o, cfg);
                list.add(target(s).getClass());

                if (cfg.getMode() == StrategyModes.TerminateAfter) {
                    break;
                }
            }
        }
        return new StrategyStatistic(o, list);
    }
}
