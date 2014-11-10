/**
 * 
 */
package ru.anr.base.services.pattern;

import ru.anr.base.services.pattern.StrategyConfig.StrategyModes;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class FalseStrategyImpl implements Strategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public StrategyConfig check(Object o, Object... params) {

        return new StrategyConfig(false, o, StrategyModes.Normal, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> S process(S o, StrategyConfig cfg) {

        return null;
    }
}
