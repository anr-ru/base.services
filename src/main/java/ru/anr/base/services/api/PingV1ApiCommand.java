/**
 * 
 */
package ru.anr.base.services.api;

import java.io.Serializable;
import java.util.Map;

import ru.anr.base.domain.api.ErrorModel;
import ru.anr.base.domain.api.models.PingModel;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.ResponseModel;

/**
 * A 'ping' api command. It's always successful.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */
@ApiStrategy(version = "v1", id = "Ping", model = PingModel.class)
public class PingV1ApiCommand extends AbstractApiCommandStrategyImpl {

    /**
     * Building a response
     * 
     * @param rq
     *            Request model
     * @param method
     *            A method name (to check)
     * @return Response model (we use {@link ErrorModel})
     */
    private ResponseModel build(PingModel rq, String method) {

        ErrorModel m = new ErrorModel();
        m.setCode(0);

        if (rq != null) {
            m.setMessage(rq.getValue() + " " + method);
        }
        return m;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResponseModel doCreate(RequestModel rq, Map<String, ? extends Serializable> ids) {

        return build((PingModel) rq, "POST");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResponseModel doModify(RequestModel rq, Map<String, ? extends Serializable> ids) {

        return build((PingModel) rq, "PUT");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResponseModel doGet(RequestModel rq, Map<String, ? extends Serializable> ids) {

        return build((PingModel) rq, "GET");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResponseModel doDelete(RequestModel rq, Map<String, ? extends Serializable> ids) {

        return build((PingModel) rq, "DELETE");
    }
}
