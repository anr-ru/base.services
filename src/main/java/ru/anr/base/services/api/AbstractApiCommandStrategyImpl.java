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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.services.BaseDataAwareServiceImpl;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * An implementation of the base API strategy.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */

public class AbstractApiCommandStrategyImpl extends BaseDataAwareServiceImpl implements ApiCommandStrategy {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractApiCommandStrategyImpl.class);

    /**
     * GET method
     *
     * @param cmd API Command
     * @return Response model
     */
    @Override
    public Object get(APICommand cmd) {

        throw new NotImplementedException("Method 'GET' not implemented");
    }

    /**
     * POST method
     *
     * @param cmd API Command
     * @return Response model
     */
    @Override
    public Object post(APICommand cmd) {

        throw new NotImplementedException("Method 'Post' not implemented");
    }

    /**
     * PUT method
     *
     * @param cmd API Command
     * @return Response model
     */

    @Override
    public Object put(APICommand cmd) {

        throw new NotImplementedException("Method 'Put' not implemented");
    }

    /**
     * DELETE method
     *
     * @param cmd API Command
     * @return Response model
     */

    @Override
    public Object delete(APICommand cmd) {

        throw new NotImplementedException("Method 'Delete' not implemented");
    }

    /**
     * Prepares a {@link Pageable} object based on provided data. Handles the
     * case when paging parameters are null.
     *
     * @param cmd        An API Command
     * @param direction  A direction of sorting
     * @param properties Names of properties used for sorting
     * @return A new created {@link Pageable}
     */
    protected Pageable safePageable(APICommand cmd, Direction direction, String... properties) {

        logger.trace("Page parameters: {}:{}", cmd.getRequest().page, cmd.getRequest().perPage);

        int page = Optional.ofNullable(cmd.getRequest().page).orElse(0);
        int perPage = Optional.ofNullable(cmd.getRequest().perPage).orElse(10);
        if (page < 0) {
            page = 0;
        }
        if (perPage <= 0) {
            perPage = 10;
        }
        return ArrayUtils.isEmpty(properties) ? PageRequest.of(page, perPage) : PageRequest.of(page, perPage, direction, properties);
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
         * @param id The identifier
         * @return A found object or null if nothing found.
         */
        S find(Long id);
    }

    /**
     * Searches and returns id parameters from the specified context of the api
     * command
     *
     * @param cmd the API Command
     * @return Long value of identifier or raises an exception if it is absent
     * or unparsible
     */
    protected Long extractId(APICommand cmd) {

        Map<String, ?> map = extract(cmd, "id");
        return parse(map.get("id").toString(), Long.class);
    }

    /**
     * Returns an identifier from the request parameters and check whether it
     * has been correctly parsed or not.
     *
     * @param cmd An original API command
     * @return Parsed not null identifier
     */
    protected Long extractIdStrict(APICommand cmd) {

        Long id = extractId(cmd);
        if (id == null) {
            Map<String, ?> map = extract(cmd, "id");
            rejectAPI("api.param.is.wrong", "id", map.get("id").toString());
        }
        return id;
    }

    /**
     * Extracts request parameters from the command
     *
     * @param cmd  The API command
     * @param keys A set of key to find in the request parameters
     * @return A resulted map which definitely contains not-empty keys
     */
    protected Map<String, Object> extract(APICommand cmd, String... keys) {

        Map<String, Object> contexts = cmd.getContexts();
        Set<String> emptyParam = getEmptyKeys(contexts, list(keys));

        if (!emptyParam.isEmpty()) {
            rejectAPI("api.param.is.null", first(emptyParam));
        }
        return contexts;
    }

    /**
     * Performs a search of an object by its identifier located inside of a
     * {@link APICommand}.
     *
     * @param cmd      A command to parse
     * @param callback A callback function used to delegate the search procedure to a
     *                 service
     * @param <S>      A class of the object
     * @return A found object (cab be null).
     */
    protected <S> S findObjectByID(APICommand cmd, ObjectFindCallback<S> callback) {

        return callback.find(extractIdStrict(cmd));
    }
}
