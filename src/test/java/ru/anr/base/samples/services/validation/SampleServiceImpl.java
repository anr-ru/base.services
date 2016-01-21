/**
 * 
 */
package ru.anr.base.samples.services.validation;

import org.springframework.stereotype.Component;

import ru.anr.base.samples.domain.Samples;
import ru.anr.base.services.BaseServiceImpl;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Jan 21, 2016
 *
 */
@Component
public class SampleServiceImpl extends BaseServiceImpl implements SampleService {

    /**
     * {@inheritDoc}
     */
    @Override
    public void doValidate(Samples o) {

        validate(o);
    }
}
