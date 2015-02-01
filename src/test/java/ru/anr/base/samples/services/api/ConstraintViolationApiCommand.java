/**
 * 
 */
package ru.anr.base.samples.services.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.models.PingModel;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.samples.services.ValidatedMethods;
import ru.anr.base.services.api.AbstractApiCommandStrategyImpl;
import ru.anr.base.services.api.ApiStrategy;

/**
 * A special command which throws an {@link ru.anr.base.domain.api.APIException}
 * for test reason.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */
@ApiStrategy(version = "v1", id = "Constraint.Violation", model = PingModel.class)
@Component("ConstraintViolationApiCommand")
public class ConstraintViolationApiCommand extends AbstractApiCommandStrategyImpl {

    /**
     * Service for testing
     */
    @Autowired
    @Qualifier("validatedMethods")
    private ValidatedMethods service;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseModel get(APICommand cmd) {

        service.method1((String) cmd.getContexts().get("xx"));
        return cmd.getResponse();
    }
}
