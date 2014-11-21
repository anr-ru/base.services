/**
 * 
 */
package ru.anr.base.services.pattern;

import ru.anr.base.services.BaseServiceImpl;
import ru.anr.base.services.pattern.StrategyConfig.StrategyModes;

/**
 * No operation strategy. Just do nothing and can be used for test purposes.
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
