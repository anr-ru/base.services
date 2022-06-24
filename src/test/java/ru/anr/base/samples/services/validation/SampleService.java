package ru.anr.base.samples.services.validation;

import ru.anr.base.samples.domain.Samples;

/**
 * Description ...
 *
 * @author Alexey Romanchuk
 * @created Jan 21, 2016
 */

public interface SampleService {
    /**
     * Just a service
     *
     * @param o An argument
     */
    void doValidate(Samples o);
}
