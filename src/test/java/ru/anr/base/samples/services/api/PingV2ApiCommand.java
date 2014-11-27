/**
 * 
 */
package ru.anr.base.samples.services.api;

import org.springframework.stereotype.Component;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.models.PingModel;
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
    public APICommand process(APICommand cmd) {

        APICommand c = super.process(cmd);
        c.getResponse().setCode(-1);

        return cmd;
    }
}
