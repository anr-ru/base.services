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

import org.apache.commons.lang3.NotImplementedException;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.services.BaseServiceImpl;

/**
 * Implementation of base Api Command strategy withot using {@link ApiMethod}
 * annotation.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class AbstractApiCommandStrategyImpl extends BaseServiceImpl implements ApiCommandStrategy {

    /**
     * GET method
     * 
     * @param cmd
     *            API Command
     * @return Response model
     */
    @Override
    public Object get(APICommand cmd) {

        throw new NotImplementedException("Method 'GET' not implemented");
    }

    /**
     * POST method
     * 
     * @param cmd
     *            API Command
     * @return Response model
     */
    @Override
    public Object post(APICommand cmd) {

        throw new NotImplementedException("Method 'Post' not implemented");
    }

    /**
     * PUT method
     * 
     * @param cmd
     *            API Command
     * @return Response model
     */

    @Override
    public Object put(APICommand cmd) {

        throw new NotImplementedException("Method 'Put' not implemented");
    }

    /**
     * DELETE method
     * 
     * @param cmd
     *            API Command
     * @return Response model
     */

    @Override
    public Object delete(APICommand cmd) {

        throw new NotImplementedException("Method 'Delete' not implemented");
    }
}
