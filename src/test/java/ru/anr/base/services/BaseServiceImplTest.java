package ru.anr.base.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import ru.anr.base.samples.domain.SampleState;
import ru.anr.base.samples.domain.TestStates;

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

        BaseServiceImpl impl = target(base);
        Assertions.assertEquals("Привет, мир!", impl.text("hello.world"));

        Assertions.assertEquals("Привет, добрый мир!", impl.text("hello.world.param", "добрый"));

        LocaleContextHolder.setLocale(new Locale("en", "US")); // USA

        Assertions.assertEquals("Hello, world!", impl.text("hello.world"));
        Assertions.assertEquals("[xxxhello.world.noxxx]", impl.text("hello.world.no", "xxx"));
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
}
