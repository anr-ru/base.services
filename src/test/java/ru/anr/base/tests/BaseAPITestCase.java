/**
 * 
 */
package ru.anr.base.tests;

import org.junit.Assert;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.MethodTypes;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.services.BaseLocalServiceTestCase;
import ru.anr.base.services.api.APICommandFactory;

/**
 * Base API Test Tool for local tests.
 *
 *
 * @author Alexey Romanchuk
 * @created Dec 18, 2014
 *
 */
@Ignore
public class BaseAPITestCase extends BaseLocalServiceTestCase {

    /**
     * API Factory
     */
    @Autowired
    private APICommandFactory factory;

    /**
     * Executes API command and parses the result
     * 
     * @param cmdId
     *            Identified of Command
     * @param version
     *            Version
     * @param method
     *            Http method
     * @param request
     *            Request Modek
     * @param contexts
     *            Context arrays ( key/value pairs, usually are parts of url in
     *            REST paradigm)
     * @return String result
     * 
     * @param <S>
     *            Type of response model
     */
    protected <S extends ResponseModel> S doAPI(String cmdId, String version, MethodTypes method,
            RequestModel request, Object... contexts) {

        APICommand cmd = new APICommand(cmdId, version).context(contexts);

        cmd.setType(method);
        cmd.setRequest(request);

        APICommand r = factory.process(cmd);
        Assert.assertNotNull(r.getRawModel()); // json by default

        return r.getResponse();
    }

}
