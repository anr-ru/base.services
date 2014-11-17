/**
 * 
 */
package ru.anr.base.services.api;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.MethodTypes;
import ru.anr.base.domain.api.models.ResponseModel;

/**
 * A sample method strategy.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 14, 2014
 *
 */

public class SampeStrategy extends AbstractApiCommandStrategyImpl {

    /**
     * @param cmd
     *            {@link APICommand}
     * @return Response
     */
    @ApiMethod(MethodTypes.Get)
    private ResponseModel get(APICommand cmd) {

        ResponseModel m = new ResponseModel();
        m.setCode(1);

        return m;
    }

    /**
     * @param cmd
     *            {@link APICommand}
     * @return Response
     */
    @ApiMethod(MethodTypes.Post)
    protected ResponseModel post(APICommand cmd) {

        ResponseModel m = new ResponseModel();
        m.setCode(2);

        return m;
    }

    /**
     * @param cmd
     *            {@link APICommand}
     * @param value
     *            Value
     * @return Response
     */
    @ApiMethod(MethodTypes.Put)
    public ResponseModel put(APICommand cmd, String value) {

        ResponseModel m = new ResponseModel();
        m.setCode(22);

        return m;
    }

    /**
     * @param cmd
     *            {@link APICommand}
     * @return Response
     */
    @ApiMethod(MethodTypes.Delete)
    public String delete(APICommand cmd) {

        return "xx";
    }

    /**
     * @param cmd
     *            {@link APICommand}
     * @return Response
     */
    public ResponseModel put(APICommand cmd) {

        ResponseModel m = new ResponseModel();
        m.setCode(3);

        return m;
    }

}
