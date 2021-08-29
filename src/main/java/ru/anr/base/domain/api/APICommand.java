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
package ru.anr.base.domain.api;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anr.base.BaseParent;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.SortModel;
import ru.anr.base.domain.api.models.SortModel.SortDirection;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A representation of a general API command (based on the RESTful API
 * approach).
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */

public class APICommand extends BaseParent implements Serializable {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(APICommand.class);

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 2901448044671838025L;

    /**
     * Method type
     */
    private MethodTypes type = MethodTypes.Get;

    /**
     * API version marker ('v1' for example)
     */
    private String version;

    /**
     * Unique identifier for API Command
     */
    private String commandId;

    /**
     * Command parameters in the key/value format
     */
    private Map<String, Object> contexts = toMap();

    /**
     * Raw model data (JSON, XML) of request or response
     */
    private String rawModel;

    /**
     * Format type (JSON, XML)
     */
    private RawFormatTypes requestFormat = RawFormatTypes.JSON;

    /**
     * Format type (JSON, XML)
     */
    private RawFormatTypes responseFormat = RawFormatTypes.JSON;

    /**
     * Parsed request model
     */
    private RequestModel request = new RequestModel();
    ;

    /**
     * Prepared response model
     */
    private Object response;

    /**
     * Constructor of new command
     *
     * @param commandId Identifier of a command
     * @param version   Command version id
     */
    public APICommand(String commandId, String version) {

        super();
        setCommandId(commandId);
        setVersion(version);
    }

    // ////////////////////// Builder methods //////////////////////////////

    /**
     * Adds the response format
     *
     * @param t A format of raw data for response
     * @return This object
     */
    public APICommand responseFormat(RawFormatTypes t) {

        setResponseFormat(t);
        return this;
    }

    /**
     * Adds the request format
     *
     * @param t A format of raw data request
     * @return This object
     */
    public APICommand requestFormat(RawFormatTypes t) {

        setRequestFormat(t);
        return this;
    }

    /**
     * Adds raw request data (not parsed)
     *
     * @param raw A raw string with the request model
     * @return This object
     */
    public APICommand addRaw(String raw) {

        setRawModel(raw);
        return this;
    }

    /**
     * Sets the type of operation according to the specified http method
     *
     * @param method Method
     * @return This
     */
    public APICommand method(String method) {

        Objects.requireNonNull(method, "Method is null");
        MethodTypes t = MethodTypes.Get;

        switch (method) {
            case "POST":
                t = MethodTypes.Post;
                break;
            case "PUT":
                t = MethodTypes.Put;
                break;
            case "GET":
                t = MethodTypes.Get;
                break;
            case "DELETE":
                t = MethodTypes.Delete;
                break;
            default:
                throw new UnsupportedOperationException(method);
        }

        setType(t);
        return this;
    }

    /**
     * Adds pairs name/value to the request parameters.
     *
     * @param array An array of pairs 'key/value'
     * @return This object
     */
    public APICommand context(Object... array) {

        setContexts(toMap(array));
        return this;
    }

    /**
     * Parses predefined query parameters to set additional properties of the
     * command.
     *
     * @param params A map of request parameters.
     * @return This object
     */
    public APICommand params(Map<String, ?> params) {

        logger.debug("Original parameters: {}", params);

        request = new RequestModel();
        Map<String, ?> copy = new HashMap<>(params);

        // The number of the current page in multi-paged results
        Object v = copy.get("page");
        if (v != null) {
            request.setPage(parse(v.toString(), Integer.class));
            copy.remove("page");
        }

        // The number of items per page
        v = copy.get("per_page");
        if (v != null) {
            request.setPerPage(parse(v.toString(), Integer.class));
            copy.remove("per_page");
        }

        // A list of queried fields
        v = copy.get("fields");
        if (v != null) {
            request.setFields(parseList(v.toString()));
            copy.remove("fields");
        }

        // the query field
        v = copy.get("q");
        if (v != null) {
            request.setSearch(v.toString());
            copy.remove("q");
        }

        // the sort field
        v = copy.get("sort");
        if (v != null) {
            request.setSorted(parseSort(parseList(v.toString())));
            copy.remove("sort");
            logger.trace("Parsed sorting: {}", request.getSorted());
        }
        /*
         * The rest of parameters is used depending on the context
         */
        this.contexts.putAll(copy);
        return this;
    }

    /**
     * Parsing string fields with +/- markers to {@link SortModel}.
     *
     * @param values A list of fields with expected prefixed
     * @return A list of {@link SortModel} objects
     */
    private List<SortModel> parseSort(List<String> values) {

        List<SortModel> list = list();
        for (String v : values) {
            // '+' is ignored
            if (v.charAt(0) == '+' || v.charAt(0) == ' ') {
                list.add(new SortModel(v.substring(1), SortDirection.ASC));
            } else if (v.charAt(0) == '-') {
                list.add(new SortModel(v.substring(1), SortDirection.DESC));
            }
        }
        return list;
    }

    /**
     * Parsing values separated by comma (',').
     *
     * @param s A string with values
     * @return Parsed values
     */
    private List<String> parseList(String s) {

        List<String> list = null;
        String[] array = StringUtils.split(s, ",");

        if (!ArrayUtils.isEmpty(array)) {
            list = list(array);
        }
        return list;
    }

    /**
     * Parses the given parameter as the local date value
     *
     * @param name The name of the parameyet
     * @param def  The default date value if nothing parsed
     * @return The date
     */
    public LocalDateTime parseDate(String name, LocalDate def) {

        Map<String, ?> map = this.getContexts();
        String strDate = nullSafe(map.get(name));

        return parseLocal(strDate, "yyyy-MM-dd").orElse(def.atStartOfDay());
    }

    /**
     * Searches all sorting parameters or set the default value if the sorting field not defined.
     *
     * @param def    The default sort direction if the sort order was not parsed/defined
     * @param fields The fields which are expected to be used for sorting
     * @return The found sort order with the same
     */
    public Map<String, SortModel.SortDirection> findSorting(SortModel.SortDirection def, String... fields) {

        RequestModel rq = this.getRequest();
        Map<String, SortModel.SortDirection> results = toMap();

        if (rq.getSorted() != null) {
            list(fields).forEach(field -> {

                SortModel sm = first(filter(rq.getSorted(), f -> field.equals(f.getField())));
                SortDirection direction = def;

                if (sm != null) {
                    direction = (sm.getDirection() == SortModel.SortDirection.ASC) ? SortDirection.ASC : SortDirection.DESC;
                }
                results.put(field, direction);
            });
        }
        return results;
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the type
     */
    public MethodTypes getType() {

        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return new ToStringBuilder(this).append("id", commandId).append("v", version).append("t", type).toString();
    }

    /**
     * @param type the type to set
     */
    public void setType(MethodTypes type) {

        this.type = type;
    }

    /**
     * @return the requestFormat
     */
    public RawFormatTypes getRequestFormat() {

        return requestFormat;
    }

    /**
     * @param requestFormat the requestFormat to set
     */
    public void setRequestFormat(RawFormatTypes requestFormat) {

        this.requestFormat = requestFormat;
    }

    /**
     * @return the responseFormat
     */
    public RawFormatTypes getResponseFormat() {

        return responseFormat;
    }

    /**
     * @param responseFormat the responseFormat to set
     */
    public void setResponseFormat(RawFormatTypes responseFormat) {

        this.responseFormat = responseFormat;
    }

    /**
     * @return the version
     */
    public String getVersion() {

        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {

        this.version = version;
    }

    /**
     * @return the commandId
     */
    public String getCommandId() {

        return commandId;
    }

    /**
     * @param commandId the commandId to set
     */
    public void setCommandId(String commandId) {

        this.commandId = commandId;
    }

    /**
     * @return the contexts
     */
    public Map<String, Object> getContexts() {

        return contexts;
    }

    /**
     * Gets a parameter value of the command by its name. Uses the given default
     * value if the parameter's value is null.
     *
     * @param name         The name of a parameter
     * @param defaultValue The value to be set if the value of the parameter is null
     * @param <S>          The class of the parameter's value
     * @return The value of the parameter
     */
    public <S> S get(String name, S defaultValue) {

        @SuppressWarnings("unchecked")
        S v = (S) getContexts().get(name);

        if (v == null) {
            v = defaultValue;
        }
        return v;
    }

    /**
     * Gets a parameter value of the command by its name without a default
     * value.
     *
     * @param name The name of a parameter
     * @param <S>  The class of the parameter's value
     * @return The value of the parameter
     */
    public <S> S get(String name) {

        return get(name, null);
    }

    /**
     * Determines whether the parameter list or context has the given parameter
     * name
     *
     * @param name The name
     * @return true, if it has
     */
    public boolean hasParam(String name) {

        return getContexts().containsKey(name);
    }

    /**
     * @param contexts the contexts to set
     */
    public void setContexts(Map<String, Object> contexts) {

        this.contexts = contexts;
    }

    /**
     * @return the rawModel
     */
    public String getRawModel() {

        return rawModel;
    }

    /**
     * @param rawModel the rawModel to set
     */
    public void setRawModel(String rawModel) {

        this.rawModel = rawModel;
    }

    /**
     * @param <S> Expected class
     * @return the request
     */
    @SuppressWarnings("unchecked")
    public <S extends RequestModel> S getRequest() {

        return (S) request;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(RequestModel request) {

        this.request = request;
    }

    /**
     * @param <S> Expected class
     * @return the response
     */
    @SuppressWarnings("unchecked")
    public <S> S getResponse() {

        return (S) response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(Object response) {

        this.response = response;
    }
}
