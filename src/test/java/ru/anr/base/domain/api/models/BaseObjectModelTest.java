package ru.anr.base.domain.api.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.tests.BaseTestCase;

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
        o.setCreated(now().plusMinutes(1));
        o.setModified(now().plusMinutes(2));
        o.setState("Basic");
        o.setStateChanged(now().plusMinutes(3));

        BaseObjectModel m = new BaseObjectModel(o);

        Assertions.assertEquals(o.getId(), m.id);
        Assertions.assertEquals(o.getState(), m.state);
        Assertions.assertEquals(calendar(o.getCreated()), m.created);
        Assertions.assertEquals(calendar(o.getModified()), m.modified);
        Assertions.assertEquals(calendar(o.getStateChanged()), m.stateChanged);

        BaseObjectModel mx = new BaseObjectModel(m);

        Assertions.assertEquals(m.id, mx.id);
        Assertions.assertEquals(m.state, mx.state);
        Assertions.assertEquals(m.created, mx.created);
        Assertions.assertEquals(m.modified, mx.modified);
        Assertions.assertEquals(m.stateChanged, mx.stateChanged);
    }

    /**
     * Testing {@link BaseObjectModel} construction
     */
    @Test
    public void testConstructionFromNulls() {

        BaseObjectModel m = new BaseObjectModel((BaseEntity) null);

        Assertions.assertNull(m.id);
        Assertions.assertNull(m.state);
        Assertions.assertNull(m.created);
        Assertions.assertNull(m.modified);
        Assertions.assertNull(m.stateChanged);

        m = new BaseObjectModel((BaseObjectModel) null);

        Assertions.assertNull(m.id);
        Assertions.assertNull(m.state);
        Assertions.assertNull(m.created);
        Assertions.assertNull(m.modified);
        Assertions.assertNull(m.stateChanged);
    }
}
