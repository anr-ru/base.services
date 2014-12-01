/**
 * 
 */
package ru.anr.base.samples.services.api;

import org.springframework.stereotype.Component;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.services.api.AbstractApiCommandStrategyImpl;
import ru.anr.base.services.api.ApiStrategy;

/**
 * A special command which reset a response.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */
@ApiStrategy(version = "v1", id = "Empty")
@Component("EmptyV1ApiCommand")
public class EmptyV1ApiCommand extends AbstractApiCommandStrategyImpl {

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseModel get(APICommand cmd) {

        return null;
    }
}
