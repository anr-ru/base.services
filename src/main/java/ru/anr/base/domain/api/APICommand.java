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
package ru.anr.base.domain.api;

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
 * A general API command (based on the RESTful API approach).
 * It supports several types of action: GET, PUT, PATCH, DELETE, POST and can parse
 * arguments given as list, sorted fields and may recognize specific fields like page/per_page,
 * query (q) or a fixed list of fields to be returned.
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
     * the API version marker ('v1' for example)
     */
    private String version;

    /**
     * The unique identifier for API Command
     */
    private String commandId;

    /**
     * Command parameters (including internally created) in the key/value format.
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
     * @param t The format of raw data for response
     * @return This object
     */
    public APICommand responseFormat(RawFormatTypes t) {
        setResponseFormat(t);
        return this;
    }

    /**
     * Adds the request format
     *
     * @param t The format of raw data request
     * @return This object
     */
    public APICommand requestFormat(RawFormatTypes t) {
        setRequestFormat(t);
        return this;
    }

    /**
     * Adds raw request data (not parsed yet)
     *
     * @param raw The raw string with the request model
     * @return This object
     */
    public APICommand addRaw(String raw) {
        setRawModel(raw);
        return this;
    }

    /**
     * Recognizes the type of operation according to the specified http method
     *
     * @param method The http method as a string
     * @return This object
     */
    public APICommand method(String method) {

        Objects.requireNonNull(method, "API Method is null");
        MethodTypes t;

        switch (method) {
            case "POST":
                t = MethodTypes.Post;
                break;
            case "PUT":
                t = MethodTypes.Put;
                break;
            case "PATCH":
                t = MethodTypes.Patch;
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
     * @param array The array of pairs 'key/value'
     * @return This object
     */
    public APICommand context(Object... array) {

        contexts.putAll(toMap(array));
        return this;
    }

    /**
     * Parses HTTP url query parameters to set additional properties of the
     * command.
     *
     * @param params The map of request parameters.
     * @return This object
     */
    public APICommand params(Map<String, ?> params) {

        logger.trace("Original HTTP parameters: {}", params);

        request = new RequestModel();
        Map<String, ?> copy = new HashMap<>(params);

        // The number of the current page in multi-paged results
        Object v = copy.get("page");
        if (v != null) {
            request.page = parse(v.toString(), Integer.class);
            copy.remove("page");
        }

        // The number of items per page
        v = copy.get("per_page");
        if (v != null) {
            request.perPage = parse(v.toString(), Integer.class);
            copy.remove("per_page");
        }

        // A list of queried fields
        v = copy.get("fields");
        if (v != null) {
            request.fields = parseList(v.toString());
            copy.remove("fields");
        }

        // the query parameter
        v = copy.get("q");
        if (v != null) {
            request.search = v.toString();
            copy.remove("q");
        }

        // the sort field
        v = copy.get("sort");
        if (v != null) {
            request.sorted = parseSort(parseList(v.toString()));
            copy.remove("sort");
            logger.trace("Parsed sorting: {}", request.sorted);
        }
        /*
         * The rest of parameters is used depending on the command's context
         */
        this.contexts.putAll(copy);
        return this;
    }

    /**
     * Parses the sort fields with +/- markers to corresponding {@link SortModel}.
     *
     * @param values The list of fields with expected prefixed +/-
     * @return The resulted list of {@link SortModel} objects
     */
    private List<SortModel> parseSort(List<String> values) {

        List<SortModel> list = list();
        for (String v : values) {
            // '+' is ignored or can be absent
            if (v.charAt(0) == '+' || v.charAt(0) == ' ') {
                list.add(new SortModel(v.substring(1), SortDirection.ASC));
            } else if (Character.isLetter(v.charAt(0))) { // sort=created means sorting ASC (like omitted '+')
                list.add(new SortModel(v, SortDirection.ASC));
            } else if (v.charAt(0) == '-') {
                list.add(new SortModel(v.substring(1), SortDirection.DESC));
            }
        }
        return list;
    }

    /**
     * Parses values separated by comma (','). We recognize list fields by the ',' command
     * inside. If just only one value of list is provided it must always have the comma in the end.
     *
     * <pre>
     *     Examples:
     *     states=New,Pending,Processed
     *
     *     // One value of list
     *     states=New,
     * </pre>
     *
     * @param s A string with values
     * @return Parsed values
     */
    private List<String> parseList(String s) {
        String[] array = StringUtils.split(s, ",");
        return nullSafe(array, BaseParent::list).orElse(list());
    }

    /**
     * Parses the given parameter as the local date value
     *
     * @param name The name of the parameter.
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

        if (rq.sorted != null) {
            list(fields).forEach(field -> {

                SortModel sm = first(filter(rq.sorted, f -> field.equals(f.getField())));
                SortDirection direction = def;

                if (sm != null) {
                    direction = (sm.getDirection() == SortModel.SortDirection.ASC) ? SortDirection.ASC : SortDirection.DESC;
                }
                results.put(field, direction);
            });
        }
        return results;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///// getters/setters
    ///////////////////////////////////////////////////////////////////////////

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
        return new ToStringBuilder(this)
                .append("id", commandId)
                .append("v", version)
                .append("t", type).toString();
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
