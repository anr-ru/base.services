/**
 * 
 */
package ru.anr.base.samples.services.api;

import org.springframework.stereotype.Component;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.ErrorModel;
import ru.anr.base.domain.api.models.PingModel;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.services.api.ApiStrategy;
import ru.anr.base.services.api.PingV1ApiCommand;

/**
 * A second version of 'ping' api command.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */
@ApiStrategy(version = "v2", id = "Ping", model = PingModel.class)
@Component("PingV2ApiCommand")
public class PingV2ApiCommand extends PingV1ApiCommand {

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseModel get(APICommand cmd) {

        ErrorModel r = new ErrorModel();
        r.setCode(-1);
        r.setMessage(((PingModel) cmd.getRequest()).getValue() + " " + cmd.getType());

        return r;
    }
}
