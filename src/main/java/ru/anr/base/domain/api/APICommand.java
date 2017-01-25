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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ru.anr.base.BaseParent;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.SortModel;
import ru.anr.base.domain.api.models.SortModel.SortDirection;

/**
 * Representation of general API command (based on RESTful API model).
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class APICommand extends BaseParent implements Serializable {

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
    private RequestModel request = new RequestModel();;

    /**
     * Prepared response model
     */
    private Object response;

    /**
     * Constructor of new command
     * 
     * @param commandId
     *            Identifier of a command
     * @param version
     *            Command version id
     */
    public APICommand(String commandId, String version) {

        super();
        setCommandId(commandId);
        setVersion(version);
    }

    // ////////////////////// Builder methods //////////////////////////////

    /**
     * Add response format
     * 
     * @param t
     *            Format of raw data for response
     * @return This
     */
    public APICommand responseFormat(RawFormatTypes t) {

        setResponseFormat(t);
        return this;
    }

    /**
     * Add request format
     * 
     * @param t
     *            Format of raw data request
     * @return This
     */
    public APICommand requestFormat(RawFormatTypes t) {

        setRequestFormat(t);
        return this;
    }

    /**
     * Adding a raw request data (not parsed)
     * 
     * @param raw
     *            A raw string with content
     * @return This
     */
    public APICommand addRaw(String raw) {

        setRawModel(raw);
        return this;
    }

    /**
     * Sets a type of operation according specified http method
     * 
     * @param method
     *            Method
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
     * @param array
     *            An array of pairs 'key/value'
     * @return This
     */
    public APICommand context(Object... array) {

        setContexts(toMap(array));
        return this;
    }

    /**
     * Parsing specified query params to set some additional properties of the
     * command
     * 
     * @param params
     *            A map of request params
     * @return This
     */
    public APICommand params(Map<String, ?> params) {

        request = new RequestModel();
        Map<String, ?> copy = new HashMap<>(params);

        Object v = copy.get("page");
        if (v != null) {
            request.setPage(parse(v.toString(), Integer.class));
            copy.remove("page");
        }

        v = copy.get("per_page");
        if (v != null) {
            request.setPerPage(parse(v.toString(), Integer.class));
            copy.remove("per_page");
        }

        v = copy.get("fields");
        if (v != null) {
            request.setFields(parseList(v.toString()));
            copy.remove("fields");
        }

        v = copy.get("q");
        if (v != null) {
            request.setSearch(v.toString());
            copy.remove("q");
        }

        v = copy.get("sort");
        if (v != null) {
            request.setSorted(parseSort(parseList(v.toString())));
            copy.remove("sort");
        }
        /*
         * The rest of parameters is used depending on the context
         */
        this.contexts.putAll(copy);
        return this;
    }

    /**
     * Parsing of string fields with +/- markers to {@link SortModel}.
     * 
     * @param values
     *            List of fields with expected prefixed
     * @return List of {@link SortModel} objects
     */
    private List<SortModel> parseSort(List<String> values) {

        List<SortModel> list = list();
        for (String v : values) {
            if (v.charAt(0) == '+') {
                list.add(new SortModel(v.substring(1), SortDirection.ASC));
            } else if (v.charAt(0) == '-') {
                list.add(new SortModel(v.substring(1), SortDirection.DESC));
            }
        }
        return list;
    }

    /**
     * Parsing of values separated by ','
     * 
     * @param s
     *            String with values
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
     * @param type
     *            the type to set
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
     * @param requestFormat
     *            the requestFormat to set
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
     * @param responseFormat
     *            the responseFormat to set
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
     * @param version
     *            the version to set
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
     * @param commandId
     *            the commandId to set
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
     * @param contexts
     *            the contexts to set
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
     * @param rawModel
     *            the rawModel to set
     */
    public void setRawModel(String rawModel) {

        this.rawModel = rawModel;
    }

    /**
     * @return the request
     * 
     * @param <S>
     *            Expected class
     */
    @SuppressWarnings("unchecked")
    public <S extends RequestModel> S getRequest() {

        return (S) request;
    }

    /**
     * @param request
     *            the request to set
     */
    public void setRequest(RequestModel request) {

        this.request = request;
    }

    /**
     * @return the response
     * 
     * @param <S>
     *            Expected class
     */
    @SuppressWarnings("unchecked")
    public <S> S getResponse() {

        return (S) response;
    }

    /**
     * @param response
     *            the response to set
     */
    public void setResponse(Object response) {

        this.response = response;
    }
}
