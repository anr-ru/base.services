/**
 * 
 */
package ru.anr.base.domain;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import ru.anr.base.tests.BaseTestCase;

/**
 * Tests for base entity.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 5, 2014
 *
 */
@ContextConfiguration(classes = BaseEntityTest.class)
public class BaseEntityTest extends BaseTestCase {

    /**
     * Equals tests: equals uses guid until id is not set
     */
    @Test
    public void testEquals() {

        BaseEntity a = new BaseEntity();
        BaseEntity b = new BaseEntity();

        Assert.assertNotEquals(a, b);

        logger.debug("a = {}, b = {}", a, b);

        b = a;
        Assert.assertEquals(a, b);

        a = new BaseEntity();
        b = new BaseEntity();
        Assert.assertNotEquals(a, b);

        a.setId(1L);
        Assert.assertNotEquals(a, b);

        b.setId(2L);
        logger.debug("a = {}, b = {}", a, b);
        Assert.assertNotEquals(a, b);

        b.setId(1L);
        Assert.assertEquals(a, b);
    }

    /**
     * Tests for collection usage
     */
    @Test
    public void testCollection() {

        BaseEntity a = new BaseEntity();
        BaseEntity b = new BaseEntity();

        Set<BaseEntity> s = set(a, b);
        Assert.assertEquals(2, s.size());
        Assert.assertTrue(s.contains(a));
        Assert.assertTrue(s.contains(b));

        a.setId(1L);
        b.setId(2L);

        Assert.assertEquals(2, s.size());
        Assert.assertFalse(s.contains(a)); // Another key
        Assert.assertFalse(s.contains(b));

        a.setId(1L);
        b.setId(1L);

        Assert.assertEquals(2, s.size());
        Assert.assertFalse(s.contains(a));
        Assert.assertFalse(s.contains(b));

        /*
         * Now it's the same
         */
        s = set(a, b);
        Assert.assertEquals(1, s.size());
        Assert.assertTrue(s.contains(a));
        Assert.assertTrue(s.contains(b));
    }

    /**
     * State transition
     */
    @Test
    public void testEnumStates() {

        BaseEntity e = new BaseEntity();
        Assert.assertNull(e.changeState(BasicStates.Active));

        Assert.assertSame(BasicStates.Active, e.changeState(BasicStates.Inactive));
        Assert.assertSame(BasicStates.Inactive, e.changeState(BasicStates.New));
    }

    /**
     * hasState() checks
     */
    @Test
    public void testHasStates() {

        BaseEntity e = new BaseEntity();
        e.changeState(BasicStates.Active);

        Assert.assertTrue(e.hasState(BasicStates.Active));
        Assert.assertTrue(e.hasState(BasicStates.Active, BasicStates.Inactive, BasicStates.New));
        Assert.assertTrue(e.hasState(BasicStates.Active, BasicStates.New));

        Assert.assertFalse(e.hasState(BasicStates.Inactive, BasicStates.New));
        Assert.assertFalse(e.hasState(BasicStates.New));
    }
}
