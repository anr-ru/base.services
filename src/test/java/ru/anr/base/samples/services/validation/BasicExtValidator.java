/**
 * 
 */
package ru.anr.base.samples.services.validation;

import org.springframework.stereotype.Component;

import ru.anr.base.ApplicationException;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.services.validation.BaseValidatorImpl;
import ru.anr.base.services.validation.Validator;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Jan 21, 2016
 *
 */
@Component
@Validator(type = Samples.class, order = 2)
public class BasicExtValidator extends BaseValidatorImpl<Samples> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate(Samples o) {

        if (o.getName() != null && o.getName().contains("Error")) {
            throw new ApplicationException("Exception");
        }
    }
}
