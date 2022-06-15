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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.SortModel;
import ru.anr.base.services.BaseDataAwareServiceImpl;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

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

    @Override
    public Object patch(APICommand cmd) {
        throw new NotImplementedException("Method 'Patch' not implemented");
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
     * Extracts request parameters from the command. Throws an exception if some keys
     * were not found.
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
    protected <S> S findObjectByID(APICommand cmd, Function<Long, S> callback) {
        return findObjectByID(cmd, "id", map -> {
            Long id = parse(map.get("id").toString(), Long.class);
            checkParamWrong(id != null, "id", map.get("id").toString());
            return callback.apply(id);
        });
    }

    protected <S> S findObjectByID(APICommand cmd, String idParam, Function<Map<String, ?>, S> callback) {
        return callback.apply(extract(cmd, idParam));
    }

    protected Pageable buildPager(APICommand cmd, String field, SortModel.SortDirection sortOrder) {

        RequestModel rq = cmd.getRequest();
        if (isEmpty(rq.sorted)) {
            rq.sorted = list(new SortModel(field, sortOrder));
        }

        Map<String, SortModel.SortDirection> map = cmd.findSorting(SortModel.SortDirection.DESC, field);
        Sort.Direction direction = map.get(field) == SortModel.SortDirection.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;

        return safePageable(cmd, direction, field);
    }
}
