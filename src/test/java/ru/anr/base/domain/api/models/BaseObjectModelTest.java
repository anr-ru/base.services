package ru.anr.base.domain.api.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.tests.BaseTestCase;

import java.util.GregorianCalendar;

/**
 * Description ...
 *
 * @author Alexey Romanchuk
 * @created Jun 8, 2015
 */
@ContextConfiguration(classes = BaseObjectModelTest.class)
public class BaseObjectModelTest extends BaseTestCase {

    /**
     * Testing {@link BaseObjectModel} construction
     */
    @Test
    public void testConstructorGood() {

        BaseEntity o = new BaseEntity();
        o.setId(25L);
        o.setCreated(GregorianCalendar.from(now().plusMinutes(1)));
        o.setModified(GregorianCalendar.from(now().plusMinutes(2)));
        o.setState("Basic");
        o.setStateChanged(GregorianCalendar.from(now().plusMinutes(3)));

        BaseObjectModel m = new BaseObjectModel(o);

        Assertions.assertEquals(o.getId(), m.getId());
        Assertions.assertEquals(o.getState(), m.getState());
        Assertions.assertEquals(o.getCreated(), m.getCreated());
        Assertions.assertEquals(o.getModified(), m.getModified());
        Assertions.assertEquals(o.getStateChanged(), m.getStateChanged());

        BaseObjectModel mx = new BaseObjectModel(m);

        Assertions.assertEquals(m.getId(), mx.getId());
        Assertions.assertEquals(m.getState(), mx.getState());
        Assertions.assertEquals(m.getCreated(), mx.getCreated());
        Assertions.assertEquals(m.getModified(), mx.getModified());
        Assertions.assertEquals(m.getStateChanged(), mx.getStateChanged());

    }

    /**
     * Testing {@link BaseObjectModel} construction
     */
    @Test
    public void testConstructionFromNulls() {

        BaseObjectModel m = new BaseObjectModel((BaseEntity) null);

        Assertions.assertNull(m.getId());
        Assertions.assertNull(m.getState());
        Assertions.assertNull(m.getCreated());
        Assertions.assertNull(m.getModified());
        Assertions.assertNull(m.getStateChanged());

        m = new BaseObjectModel((BaseObjectModel) null);

        Assertions.assertNull(m.getId());
        Assertions.assertNull(m.getState());
        Assertions.assertNull(m.getCreated());
        Assertions.assertNull(m.getModified());
        Assertions.assertNull(m.getStateChanged());

    }

}
