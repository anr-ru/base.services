package ru.anr.base.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.anr.base.tests.BaseTestCase;

import java.util.Set;

/**
 * Tests for base entity.
 *
 * @author Alexey Romanchuk
 * @created Nov 5, 2014
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

        Assertions.assertNotEquals(a, b);

        logger.debug("a = {}, b = {}", a, b);

        b = a;
        Assertions.assertEquals(a, b);

        a = new BaseEntity();
        b = new BaseEntity();
        Assertions.assertNotEquals(a, b);

        a.setId(1L);
        Assertions.assertNotEquals(a, b);

        b.setId(2L);
        logger.debug("a = {}, b = {}", a, b);
        Assertions.assertNotEquals(a, b);

        b.setId(1L);
        Assertions.assertEquals(a, b);
    }

    /**
     * Tests for collection usage
     */
    @Test
    public void testCollection() {

        BaseEntity a = new BaseEntity();
        BaseEntity b = new BaseEntity();

        Set<BaseEntity> s = set(a, b);
        Assertions.assertEquals(2, s.size());
        Assertions.assertTrue(s.contains(a));
        Assertions.assertTrue(s.contains(b));

        a.setId(1L);
        b.setId(2L);

        Assertions.assertEquals(2, s.size());
        Assertions.assertFalse(s.contains(a)); // Another key
        Assertions.assertFalse(s.contains(b));

        a.setId(1L);
        b.setId(1L);

        Assertions.assertEquals(2, s.size());
        Assertions.assertFalse(s.contains(a));
        Assertions.assertFalse(s.contains(b));

        /*
         * Now it's the same
         */
        s = set(a, b);
        Assertions.assertEquals(1, s.size());
        Assertions.assertTrue(s.contains(a));
        Assertions.assertTrue(s.contains(b));
    }

    /**
     * State transition
     */
    @Test
    public void testEnumStates() {

        BaseEntity e = new BaseEntity();
        Assertions.assertNull(e.changeState(BasicStates.Active));

        Assertions.assertSame(BasicStates.Active, e.changeState(BasicStates.Inactive));
        Assertions.assertSame(BasicStates.Inactive, e.changeState(BasicStates.New));
    }

    /**
     * hasState() checks
     */
    @Test
    public void testHasStates() {

        BaseEntity e = new BaseEntity();
        e.changeState(BasicStates.Active);

        Assertions.assertTrue(e.hasState(BasicStates.Active));
        Assertions.assertTrue(e.hasState(BasicStates.Active, BasicStates.Inactive, BasicStates.New));
        Assertions.assertTrue(e.hasState(BasicStates.Active, BasicStates.New));

        Assertions.assertFalse(e.hasState(BasicStates.Inactive, BasicStates.New));
        Assertions.assertFalse(e.hasState(BasicStates.New));
    }
}
