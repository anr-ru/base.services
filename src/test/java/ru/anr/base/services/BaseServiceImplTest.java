package ru.anr.base.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.anr.base.dao.SecuredPageImpl;
import ru.anr.base.dao.repository.SecuredRepository;
import ru.anr.base.domain.api.models.BaseObjectModel;
import ru.anr.base.domain.api.models.SampleModel;
import ru.anr.base.samples.dao.MyDao;
import ru.anr.base.samples.domain.SampleState;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.samples.domain.TestStates;
import ru.anr.base.samples.services.ACLSecured;
import ru.anr.base.samples.services.TestDataService;
import ru.anr.base.services.security.ACLManager;

import java.math.BigDecimal;
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
            Assertions.assertEquals("Access Denied", ex.getMessage());
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
        Assertions.assertEquals("[3, 1]", nullSafe(service.doExtension(null)));
        Assertions.assertEquals("[2]", nullSafe(service.doExtension("test")));
        Assertions.assertEquals("[3, 1]", nullSafe(service.doExtension("default")));
    }

    @Autowired
    protected MyDao mydao;

    @Autowired
    protected SecuredRepository securedDao;

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
        Assertions.assertEquals(1, new SecuredPageImpl<>(securedDao, pages).getContent().size());
    }

    @Autowired
    private ACLManager acls;

    /**
     * Use case: check the filtration of object when using the secured pages with list
     */
    @Test
    public void testSecuredPagesFromList() {

        authenticate(new TestingAuthenticationToken("test", "password", "ROLE_USER"));

        Samples s1 = mydao.save(new Samples());
        s1.setName("read");
        Samples s2 = mydao.save(new Samples());
        s2.setName("write");

        // Update it
        PageRequest pager = PageRequest.of(0, 100);

        Page<Samples> page = new SecuredPageImpl<>(securedDao, list(s1, s2), pager, 2);
        Assertions.assertEquals(1, page.getContent().size());
        Assertions.assertEquals(list(s1), page.getContent());
        Assertions.assertEquals(1, page.getTotalElements()); // corrected to 1

        acls.grant(s2, new PrincipalSid("test"), BasePermission.READ);

        page = new SecuredPageImpl<>(securedDao, list(s1, s2), pager, 2);
        Assertions.assertEquals(2, page.getContent().size());
        Assertions.assertEquals(list(s1, s2), page.getContent());
        Assertions.assertEquals(2, page.getTotalElements());
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

    /**
     * Tests for {@link BaseServiceImpl#parseAndCheckParam(String, String, Class)}
     */
    @Test
    public void testCheckAndParseParam() {

        BaseServiceImpl service = target(bean(BaseServiceImpl.class));


        Assertions.assertEquals(d("3.14"),
                service.parseAndCheckParam("3.14", "num", BigDecimal.class));
        Assertions.assertEquals(10L,
                service.parseAndCheckParam("10", "num", Long.class));

        assertException(x -> service.parseAndCheckParam("x2", "num", Long.class),
                "Wrong parameter `num` = x2");
        assertException(x -> service.parseAndCheckParam("x2", "num", BigDecimal.class),
                "Wrong parameter `num` = x2");
        assertException(x -> service.parseAndCheckParam(null, "num", BigDecimal.class),
                "Parameter `num` is null");
    }
}
