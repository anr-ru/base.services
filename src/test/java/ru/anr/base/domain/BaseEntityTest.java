package ru.anr.base.domain;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.anr.base.dao.AbstractDaoTestCase;
import ru.anr.base.samples.domain.Samples;

import java.util.Set;

/**
 * Tests for base entity.
 *
 * @author Alexey Romanchuk
 * @created Nov 5, 2014
 */
public class BaseEntityTest extends AbstractDaoTestCase {

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

    @Test
    public void saveEntities() {
        Samples e = dao.save(new Samples());
        Assertions.assertNotNull(e.getId());
        Assertions.assertNotNull(e.getCreated());
        Assertions.assertNull(e.getModified());

        e.setName("XXX");
        e = dao.saveAndFlush(e);
        Assertions.assertNotNull(e.getModified());
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
        // String version
        Assertions.assertTrue(e.hasState(BasicStates.Active.name(), BasicStates.New.name()));

        Assertions.assertFalse(e.hasState(BasicStates.Inactive, BasicStates.New));
        Assertions.assertFalse(e.hasState(BasicStates.New));

        e.setStateInfo(guid());
        Assertions.assertNotNull(e.getStateInfo());
    }


    /**
     * A use case: we wish to print a short class name only
     */
    @Test
    public void testToStringStyle() {

        Samples a = new Samples();
        Assertions.assertEquals("Samples[id=" + a.internalId() + "]", a.toString());

        Samples b = dao.save(new Samples());
        Assertions.assertEquals("Samples[id=" + b.getId() + ",v=0]", b.toString());
    }

    /**
     * A use case: a long state info string
     */
    @Test
    public void testLongStateInfo() {

        Samples a = new Samples();
        a.setStateInfo(StringUtils.repeat("x", 520));
        a = dao.saveAndFlush(a);
        Assertions.assertEquals(StringUtils.repeat("x", 512), a.getStateInfo());

        a.setStateInfo(StringUtils.repeat("x", 220));
        a = dao.saveAndFlush(a);
        Assertions.assertEquals(StringUtils.repeat("x", 220), a.getStateInfo());

        a.setStateInfo("");
        a = dao.saveAndFlush(a);
        Assertions.assertEquals("", a.getStateInfo());

        a.setStateInfo(null);
        a = dao.saveAndFlush(a);
        Assertions.assertNull(a.getStateInfo());
    }
}
