/*
 * Copyright 2014-2022 the original author or authors.
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
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */
@ApiStrategy(version = "v1", id = "Ping", model = PingModel.class)
public class PingV1ApiCommand extends AbstractApiCommandStrategyImpl {

    /**
     * Building a response
     *
     * @param rq     Request model
     * @param method A method name (to check)
     * @return Response model (we use {@link ResponseModel})
     */
    private ResponseModel build(RequestModel rq, String method) {

        ResponseModel m = new ResponseModel();
        m.code = 0;

        if (rq instanceof PingModel) {
            m.message = (((PingModel) rq).value + " " + method);
        }
        return m;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseModel post(APICommand cmd) {
        return build(cmd.getRequest(), "POST");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseModel put(APICommand cmd) {
        return build(cmd.getRequest(), "PUT");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseModel get(APICommand cmd) {
        return build(cmd.getRequest(), "GET");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseModel delete(APICommand cmd) {
        return build(cmd.getRequest(), "DELETE");
    }
}
