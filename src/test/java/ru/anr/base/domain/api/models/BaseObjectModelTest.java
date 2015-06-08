/**
 * 
 */
package ru.anr.base.domain.api.models;

import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import ru.anr.base.domain.BaseEntity;
import ru.anr.base.tests.BaseTestCase;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Jun 8, 2015
 *
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

        Assert.assertEquals(o.getId(), m.getId());
        Assert.assertEquals(o.getState(), m.getState());
        Assert.assertEquals(o.getCreated(), m.getCreated());
        Assert.assertEquals(o.getModified(), m.getModified());
        Assert.assertEquals(o.getStateChanged(), m.getStateChanged());

        BaseObjectModel mx = new BaseObjectModel(m);

        Assert.assertEquals(m.getId(), mx.getId());
        Assert.assertEquals(m.getState(), mx.getState());
        Assert.assertEquals(m.getCreated(), mx.getCreated());
        Assert.assertEquals(m.getModified(), mx.getModified());
        Assert.assertEquals(m.getStateChanged(), mx.getStateChanged());

    }

    /**
     * Testing {@link BaseObjectModel} construction
     */
    @Test
    public void testConstructionFromNulls() {

        BaseObjectModel m = new BaseObjectModel((BaseEntity) null);

        Assert.assertNull(m.getId());
        Assert.assertNull(m.getState());
        Assert.assertNull(m.getCreated());
        Assert.assertNull(m.getModified());
        Assert.assertNull(m.getStateChanged());

        m = new BaseObjectModel((BaseObjectModel) null);

        Assert.assertNull(m.getId());
        Assert.assertNull(m.getState());
        Assert.assertNull(m.getCreated());
        Assert.assertNull(m.getModified());
        Assert.assertNull(m.getStateChanged());

    }

}
