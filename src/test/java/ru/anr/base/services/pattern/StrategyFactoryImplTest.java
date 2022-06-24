package ru.anr.base.services.pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.services.BaseLocalServiceTestCase;

/**
 * Tests for strategy factory
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */
public class StrategyFactoryImplTest extends BaseLocalServiceTestCase {

    /**
     * An etalon object
     */
    private final Samples etalon = new Samples();

    /**
     * The first sequence of strategies - with one not applicable in the middle
     *
     * @return {@link StrategyFactory}
     */
    public StrategyFactory strategyFactory1() {
        return new StrategyFactoryImpl(list(
                new NopStrategyImpl(),
                new FalseStrategyImpl(),
                new NopStrategyImpl()), true);
    }

    /**
     * The second sequence of strategies - with one in terminated mode in the
     * middle
     *
     * @return {@link StrategyFactory}
     */
    public StrategyFactory strategyFactory2() {
        return new StrategyFactoryImpl(list(
                new NopStrategyImpl(),
                new TerminateStrategyImpl(),
                new NopStrategyImpl()), true);
    }

    /**
     * Checking our expectation about concrete strategies
     */
    @Test
    public void testApplicable() {

        Assertions.assertTrue(new NopStrategyImpl().check(etalon).isApplicable());
        Assertions.assertFalse(new FalseStrategyImpl().check(etalon).isApplicable());
        Assertions.assertTrue(new TerminateStrategyImpl().check(etalon).isApplicable());
    }

    /**
     * Processing the first chain : not applicable strategy is excluded.
     */
    @Test
    public void testSelection() {

        Samples o = new Samples();

        StrategyFactory sf = strategyFactory1();
        StrategyStatistic st = sf.process(o);

        Assertions.assertSame(o, st.getObject());

        logger.debug("Result: {}", st.getAppliedStrategies());

        Assertions.assertEquals(2, st.getAppliedStrategies().size());
        Assertions.assertTrue(st.getAppliedStrategies().contains(NopStrategyImpl.class));
        Assertions.assertFalse(st.getAppliedStrategies().contains(FalseStrategyImpl.class));
    }

    /**
     * Processing the second chain : a strategy in
     * {@link ru.anr.base.services.pattern.StrategyConfig.StrategyModes#TerminateAfter}
     * mode stops all chain processing
     */
    @Test
    public void testTermination() {

        Samples o = new Samples();

        StrategyFactory sf = strategyFactory2();
        StrategyStatistic st = sf.process(o);

        Assertions.assertSame(o, st.getObject());
        logger.debug("Result: {}", st.getAppliedStrategies());

        // terminated after the second
        Assertions.assertEquals(2, st.getAppliedStrategies().size());
        Assertions.assertTrue(st.getAppliedStrategies().containsAll(list(NopStrategyImpl.class, TerminateStrategyImpl.class)));
    }
}
