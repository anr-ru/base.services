/**
 * 
 */
package ru.anr.base.services.api;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.ErrorModel;
import ru.anr.base.domain.api.MethodTypes;
import ru.anr.base.domain.api.models.PingModel;
import ru.anr.base.domain.api.models.ResponseModel;

/**
 * A 'ping' api command. It's always successful, inspite of using
 * {@link ErrorModel} :-)
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
     * Supporting POST method
     * 
     * @param cmd
     *            Command
     * @return Response
     */
    @ApiMethod(MethodTypes.Post)
    public ResponseModel doCreate(APICommand cmd) {

        return build((PingModel) cmd.getRequest(), "POST");
    }

    /**
     * Supporting PUT method
     * 
     * @param cmd
     *            Command
     * @return Response
     */
    @ApiMethod(MethodTypes.Put)
    protected ResponseModel doModify(APICommand cmd) {

        return build((PingModel) cmd.getRequest(), "PUT");
    }

    /**
     * Supporting GET method
     * 
     * @param cmd
     *            Command
     * @return Response
     */
    @ApiMethod(MethodTypes.Get)
    protected ResponseModel doGet(APICommand cmd) {

        return build((PingModel) cmd.getRequest(), "GET");
    }

    /**
     * Supporing DELETE method
     * 
     * @param cmd
     *            Command
     * @return Response
     */
    @ApiMethod(MethodTypes.Delete)
    protected ResponseModel doDelete(APICommand cmd) {

        return build((PingModel) cmd.getRequest(), "DELETE");
    }
}
