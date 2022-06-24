package ru.anr.base.services.pattern;

import ru.anr.base.services.pattern.StrategyConfig.StrategyModes;

/**
 * Description ...
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */
public class TerminateStrategyImpl implements Strategy<Object> {
    /**
     * {@inheritDoc}
     */
    @Override
    public StrategyConfig check(Object o, Object... params) {
        return new StrategyConfig(true, o, StrategyModes.TerminateAfter, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object process(Object o, StrategyConfig cfg) {
        return o;
    }
}
