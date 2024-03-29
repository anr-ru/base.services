package ru.anr.base.samples.services.api;

import org.springframework.stereotype.Component;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.APIException;
import ru.anr.base.domain.api.models.PingModel;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.services.api.AbstractApiCommandStrategyImpl;
import ru.anr.base.services.api.ApiStrategy;

/**
 * A special command which throws an {@link APIException} for test reason.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */
@ApiStrategy(version = "v1", id = "Error", model = PingModel.class)
@Component("ErrorV1ApiCommand")
public class ErrorV1ApiCommand extends AbstractApiCommandStrategyImpl {

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseModel get(APICommand cmd) {
        throw APIException.withCode("Exception", 5);
    }
}
