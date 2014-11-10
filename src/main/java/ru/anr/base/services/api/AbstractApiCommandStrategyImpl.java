/**
 * 
 */
package ru.anr.base.services.api;

import java.io.Serializable;
import java.util.Map;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.services.BaseServiceImpl;

/**
 * Implementation of base Api Command strategy.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class AbstractApiCommandStrategyImpl extends BaseServiceImpl implements ApiCommandStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand process(APICommand cmd) {

        ResponseModel rs = null;

        switch (cmd.getType()) {
            case Create:
                rs = doCreate(cmd.getRequest(), cmd.getContexts());
                break;
            case Get:
                rs = doGet(cmd.getRequest(), cmd.getContexts());
                break;
            case Modify:
                rs = doModify(cmd.getRequest(), cmd.getContexts());
                break;
            case Delete:
                rs = doDelete(cmd.getRequest(), cmd.getContexts());
                break;
            default:
                throw new UnsupportedOperationException("Method unsupported");
        }

        cmd.setResponse(rs);
        return cmd;
    }

    /**
     * 'Create' method (aka POST)
     * 
     * @param rq
     *            Original request model
     * @param ids
     *            A map of request identifiers
     * @return A response model
     */
    protected ResponseModel doCreate(RequestModel rq, Map<String, ? extends Serializable> ids) {

        throw new UnsupportedOperationException("Method unsupported");
    }

    /**
     * 'Modify' method (aka PUT)
     * 
     * @param rq
     *            Original request model
     * @param ids
     *            A map of request identifiers
     * @return A response model
     */
    protected ResponseModel doModify(RequestModel rq, Map<String, ? extends Serializable> ids) {

        throw new UnsupportedOperationException("Method unsupported");
    }

    /**
     * 'Get' method (aka GET)
     * 
     * @param rq
     *            Original request model
     * @param ids
     *            A map of request identifiers
     * @return A response model
     */
    protected ResponseModel doGet(RequestModel rq, Map<String, ? extends Serializable> ids) {

        throw new UnsupportedOperationException("Method unsupported");
    }

    /**
     * 'Delete' method (aka Delete)
     * 
     * @param rq
     *            Original request model
     * @param ids
     *            A map of request identifiers
     * @return A response model
     */
    protected ResponseModel doDelete(RequestModel rq, Map<String, ? extends Serializable> ids) {

        throw new UnsupportedOperationException("Method unsupported");
    }

}
