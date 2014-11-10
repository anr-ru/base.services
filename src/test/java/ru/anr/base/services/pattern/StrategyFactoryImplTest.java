/**
 * 
 */
package ru.anr.base.services.pattern;

import org.junit.Assert;
import org.junit.Test;

import ru.anr.base.samples.domain.Samples;
import ru.anr.base.services.BaseServiceTestCase;

/**
 * Tests for strategy factory
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */
public class StrategyFactoryImplTest extends BaseServiceTestCase {

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

        return new StrategyFactoryImpl(list(new NopStrategyImpl(), new FalseStrategyImpl(), new NopStrategyImpl()));
    }

    /**
     * The second sequence of strategies - with one in terminated mode in the
     * middle
     * 
     * @return {@link StrategyFactory}
     */
    public StrategyFactory strategyFactory2() {

        return new StrategyFactoryImpl(list(new NopStrategyImpl(), new TerminateStrategyImpl(), new NopStrategyImpl()));
    }

    /**
     * Checking our expectation about concrete strategies
     */
    @Test
    public void testApplicable() {

        Assert.assertTrue(new NopStrategyImpl().check(etalon).isApplicable());
        Assert.assertFalse(new FalseStrategyImpl().check(etalon).isApplicable());
        Assert.assertTrue(new TerminateStrategyImpl().check(etalon).isApplicable());
    }

    /**
     * Processing the first chain : not applicable strategy is excluded.
     */
    @Test
    public void testSelection() {

        Samples o = new Samples();

        StrategyFactory sf = strategyFactory1();
        StrategyStatistic st = sf.process(o);

        Assert.assertSame(o, st.getObject());

        logger.debug("Result: {}", st.getAppliedStrategies());

        Assert.assertEquals(2, st.getAppliedStrategies().size());
        Assert.assertTrue(st.getAppliedStrategies().contains(NopStrategyImpl.class));
        Assert.assertFalse(st.getAppliedStrategies().contains(FalseStrategyImpl.class));
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

        Assert.assertSame(o, st.getObject());
        logger.debug("Result: {}", st.getAppliedStrategies());

        // terminated after the second
        Assert.assertEquals(2, st.getAppliedStrategies().size());
        Assert.assertTrue(st.getAppliedStrategies().containsAll(
                list(NopStrategyImpl.class, TerminateStrategyImpl.class)));
    }
}
