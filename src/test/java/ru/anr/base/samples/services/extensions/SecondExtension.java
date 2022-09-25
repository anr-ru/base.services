package ru.anr.base.samples.services.extensions;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.anr.base.samples.services.ServiceExt1;
import ru.anr.base.samples.services.ServiceExt2;
import ru.anr.base.services.ExtensionMarker;
import ru.anr.base.services.pattern.Strategy;
import ru.anr.base.services.pattern.StrategyConfig;

/**
 * @author Alexey Romanchuk
 * @created May 11, 2022
 */
@Component
@ExtensionMarker("test")
@Order(10)
public class SecondExtension implements Strategy<Object> {

    @Override
    public StrategyConfig check(Object o, Object... params) {
        return new StrategyConfig(true, o, StrategyConfig.StrategyModes.Normal, params);
    }

    @Override
    public Object process(Object o, StrategyConfig cfg) {
        cfg.add("2");
        return o;
    }
}
