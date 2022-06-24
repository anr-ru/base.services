package ru.anr.base.samples.services;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.anr.base.services.BaseServiceImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of {@link ValidatedMethods}.
 *
 * @author Alexey Romanchuk
 * @created Jan 30, 2015
 */
@Component("validatedMethods")
@Validated
public class ValidatedMethodsImpl extends BaseServiceImpl implements ValidatedMethods {

    /**
     * {@inheritDoc}
     */
    @Override
    public String method1(String code) {
        return super.text(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object method2(String x) {
        return x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Validator method3() {
        return validator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void method4(String code) {
        reject(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S> void method5(Set<ConstraintViolation<S>> constraints) {
        rejectIfNeed(new HashSet<>(constraints));
    }
}
