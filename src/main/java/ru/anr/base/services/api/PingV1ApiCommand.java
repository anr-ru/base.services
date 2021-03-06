/*
 * Copyright 2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.anr.base.services.api;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.models.PingModel;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.ResponseModel;

/**
 * A 'ping' api command. It's always successful, in spite of using
 * {@link ResponseModel} :-)
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
     * @return Response model (we use {@link ResponseModel})
     */
    private ResponseModel build(RequestModel rq, String method) {

        ResponseModel m = new ResponseModel();
        m.setCode(0);

        if (rq != null && rq instanceof PingModel) {
            m.setMessage(((PingModel) rq).getValue() + " " + method);
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
    @Override
    public ResponseModel post(APICommand cmd) {

        return build(cmd.getRequest(), "POST");
    }

    /**
     * Supporting PUT method
     * 
     * @param cmd
     *            Command
     * @return Response
     */
    @Override
    public ResponseModel put(APICommand cmd) {

        return build(cmd.getRequest(), "PUT");
    }

    /**
     * Supporting GET method
     * 
     * @param cmd
     *            Command
     * @return Response
     */
    @Override
    public ResponseModel get(APICommand cmd) {

        return build(cmd.getRequest(), "GET");
    }

    /**
     * Supporing DELETE method
     * 
     * @param cmd
     *            Command
     * @return Response
     */
    @Override
    public ResponseModel delete(APICommand cmd) {

        return build(cmd.getRequest(), "DELETE");
    }
}
