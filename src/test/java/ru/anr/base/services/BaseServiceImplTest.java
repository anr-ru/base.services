package ru.anr.base.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.anr.base.dao.SecuredPageImpl;
import ru.anr.base.domain.api.models.BaseObjectModel;
import ru.anr.base.domain.api.models.SampleModel;
import ru.anr.base.samples.dao.MyDao;
import ru.anr.base.samples.domain.SampleState;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.samples.domain.TestStates;
import ru.anr.base.samples.services.ACLSecured;
import ru.anr.base.samples.services.TestDataService;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Test for checking Spring {@link org.springframework.core.env.Environment} to
 * work.
 *
 * @author Alexey Romanchuk
 * @created Nov, 3 2014
 */
public class BaseServiceImplTest extends BaseLocalServiceTestCase {

    /**
     * The bean under test
     */
    @Autowired
    @Qualifier("base")
    protected BaseService base;

    /**
     * Test method for {@link BaseService#text(String, Object...)}
     */
    @Test
    public void tesTextResources() {

        LocaleContextHolder.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));

        LocaleContextHolder.setLocale(new Locale("ru", "RU")); // Russia

        //BaseServiceImpl impl = target(base);
        Assertions.assertEquals("Привет, мир!", base.text("hello.world"));

        Assertions.assertEquals("Привет, добрый мир!", base.text("hello.world.param", "добрый"));

        LocaleContextHolder.setLocale(new Locale("en", "US")); // USA

        Assertions.assertEquals("Hello, world!", base.text("hello.world"));
        Assertions.assertEquals("[xxxhello.world.noxxx]", base.text("hello.world.no", "xxx"));
    }

    /**
     * Tests for {@link BaseService#textLocalized(String, Locale, Object...)}.
     */
    @Test
    public void tesTextResourcesLocalized() {

        LocaleContextHolder.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));

        Assertions.assertEquals("Привет, мир!", base.textLocalized("hello.world", new Locale("ru", "RU")));
        Assertions.assertEquals("Hello, world!", base.textLocalized("hello.world", new Locale("en", "EN")));
    }


    /**
     * Test method for
     * {@link BaseService#changeState(ru.anr.base.domain.BaseEntity, Enum)}
     */
    @Test
    public void testChangeState() {

        authenticate(new TestingAuthenticationToken("test", "password", "ROLE_USER"));

        SampleState e = dao.save(new SampleState());
        e.setState(TestStates.A.name());

        TestStates old = base.changeState(e, TestStates.B);
        Assertions.assertSame(TestStates.A, old);

        try {
            base.changeState(e, TestStates.A);
            Assertions.fail();
        } catch (AccessDeniedException ex) {
            logger.info("Denied: {}", ex.getMessage());
            Assertions.assertEquals("Access is denied", ex.getMessage());
        }
    }

    @Test
    public void testRunAs() {

        Authentication t1 = authenticate(new TestingAuthenticationToken("test", "password", "ROLE_ROOT"));
        Authentication t2 = authenticate(new TestingAuthenticationToken("test", "password", "ROLE_USER"));

        Assertions.assertSame(t2, SecurityContextHolder.getContext().getAuthentication());

        BaseServiceImpl s = new BaseServiceImpl();

        // 1. With the result
        Assertions.assertEquals("XY", s.runAs(t1, x -> {
            Assertions.assertSame(t1, SecurityContextHolder.getContext().getAuthentication());
            return nullSafe(x[0]) + nullSafe(x[1]);
        }, "X", "Y"));
        Assertions.assertSame(t2, SecurityContextHolder.getContext().getAuthentication());

        // 2. Without a result
        s.runAs(t1, x -> {
            Assertions.assertSame(t1, SecurityContextHolder.getContext().getAuthentication());
            Assertions.assertEquals("X", x[0]);
            Assertions.assertEquals("Y", x[1]);
        }, "X", "Y");
        Assertions.assertSame(t2, SecurityContextHolder.getContext().getAuthentication());

    }

    @Autowired
    @Qualifier("TestDataService")
    protected TestDataService service;


    @Test
    public void testExtensions() {

        Assertions.assertEquals("[1, 2]", nullSafe(service.doExtension(null)));
        Assertions.assertEquals("[2]", nullSafe(service.doExtension("test")));

    }

    @Autowired
    protected MyDao mydao;

    /**
     * Use case: check the filtration of object when using the secured pages
     */
    @Test
    public void testSecuredPages() {

        authenticate(new TestingAuthenticationToken("test", "password", "ROLE_USER"));

        Samples s1 = mydao.save(new Samples());
        s1.setName("read");
        Samples s2 = mydao.save(new Samples());
        s2.setName("write");

        // Update it
        PageRequest pager = PageRequest.of(0, 100);
        Page<Samples> pages = mydao.pages(pager);

        Assertions.assertEquals(2, pages.getContent().size());
        Assertions.assertEquals(1, new SecuredPageImpl<>(mydao, pager, pages).getContent().size());
    }

    @Autowired
    protected ACLSecured secured;

    @Test
    public void testGetBean() {
        ACLSecured acls = base.getBean(ACLSecured.class);
        Assertions.assertSame(secured, acls);
    }

    /**
     * Tests for {@link BaseServiceImpl#toModel(Page, Class, Class)}.
     */
    @Test
    public void testToModel() {

        Samples s = dao.save(new Samples());
        s.setName("KKK");
        Page<Samples> pages = mydao.pages(PageRequest.of(0, 10));

        List<SampleModel> list = BaseServiceImpl.toModel(pages, SampleModel.class, Samples.class);
        Assertions.assertEquals(s.getId(), first(list).id);

        list = BaseServiceImpl.toModel(pages, SampleModel.class, Samples.class, (m, o) -> m.name = s.getName() + "XXX");
        Assertions.assertEquals("KKKXXX", first(list).name);

        list = BaseServiceImpl.toModel(pages, SampleModel::new);
        Assertions.assertEquals("KKK", first(list).name);
    }

    /**
     * Tests for {@link BaseServiceImpl#isSupported(SupportableService)}
     */
    @Test
    public void testIsSupported() {
        Assertions.assertFalse(BaseServiceImpl.isSupported(null));
        Assertions.assertTrue(BaseServiceImpl.isSupported(new SupportableService() {
            @Override
            public boolean isSupported() {
                return SupportableService.super.isSupported();
            }
        }));
        Assertions.assertFalse(BaseServiceImpl.isSupported(new SupportableService() {
            @Override
            public boolean isSupported() {
                return false;
            }
        }));
    }

    /**
     * Tests for {@link BaseServiceImpl#isDefined(BaseObjectModel)}
     */
    @Test
    public void testIsDefined() {

        Assertions.assertFalse(BaseServiceImpl.isDefined(null));
        Assertions.assertFalse(BaseServiceImpl.isDefined(new SampleModel(null)));

        Samples s = dao.save(new Samples());
        Assertions.assertTrue(BaseServiceImpl.isDefined(new SampleModel(s)));
    }
}
