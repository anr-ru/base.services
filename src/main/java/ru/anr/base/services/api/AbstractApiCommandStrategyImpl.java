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

import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import ru.anr.base.domain.BaseEntity;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.services.BaseDataAwareServiceImpl;

/**
 * Implementation of base API Command strategy.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class AbstractApiCommandStrategyImpl extends BaseDataAwareServiceImpl implements ApiCommandStrategy {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractApiCommandStrategyImpl.class);

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

    /**
     * Prepares a {@link Pageable} object based on provided data. Handles the
     * situation when the paging parameters are nulls.
     * 
     * @param cmd
     *            An original API Command
     * @param direction
     *            A direction of sorting
     * @param properties
     *            Names of properties used for sorting
     * @return A new created {@link Pageable}
     */
    protected Pageable safePageable(APICommand cmd, Direction direction, String... properties) {

        logger.trace("Page parameters: {}:{}", cmd.getRequest().getPage(), cmd.getRequest().getPerPage());

        Integer page = Optional.ofNullable(cmd.getRequest().getPage()).orElse(0);
        Integer perPage = Optional.ofNullable(cmd.getRequest().getPerPage()).orElse(10);
        if (page < 0) {
            page = 0;
        }
        if (perPage <= 0) {
            perPage = 10;
        }
        return new PageRequest(page, perPage, direction, properties);
    }

    /**
     * A simple callback for customization of searching an object by its
     * identifier.
     */
    @FunctionalInterface
    protected interface ObjectFindCallback<S> {

        /**
         * Finds an object by the given identifier
         * 
         * @param id
         *            The identifier
         * @return A found object or null if nothing found.
         */
        S find(Long id);
    }

    /**
     * Performs a search of an object by its identifier given as a string value.
     * 
     * @param strId
     *            An string value which is expected to be a long value
     * @param callback
     *            A callback function used to delegate the search procedure to a
     *            service
     * @return A found object (cab be null).
     * 
     * @param <S>
     *            A class of the object
     */
    protected <S extends BaseEntity> S findObjectByID(String strId, ObjectFindCallback<S> callback) {

        Long id = parse(strId, Long.class);
        if (id == null) {
            rejectAPI("api.param.is.wrong", "id", strId);
        }
        return callback.find(id);
    }
}
