/**
 * 
 */
package ru.anr.base.domain.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import ru.anr.base.BaseParent;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.ResponseModel;

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
     * Idenfiers of objects in the api command context (keys/values pairs)
     */
    private Map<String, ? extends Serializable> contexts;

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
    private RequestModel request;

    /**
     * Prepared response model
     */
    private ResponseModel response;

    /**
     * Constructor of new command
     * 
     * @param id
     *            Identifier of command
     * @param version
     *            Command version id
     */
    public APICommand(String id, String version) {

        super();
        setCommandId(id);
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
                t = MethodTypes.Create;
                break;
            case "PUT":
                t = MethodTypes.Modify;
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
     * Sets context object identifiers
     * 
     * @param map
     *            Context identifiers to be used
     * @return This
     */
    public APICommand context(Map<String, ? extends Serializable> map) {

        setContexts(map);
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
    public APICommand params(Map<String, String> params) {

        request = new RequestModel();
        // TODO: parse all known query params (sort, fields, page, per_page, q)
        // and sets for request

        return this;
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
    public Map<String, ? extends Serializable> getContexts() {

        return contexts;
    }

    /**
     * @param contexts
     *            the contexts to set
     */
    public void setContexts(Map<String, ? extends Serializable> contexts) {

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
     */
    public RequestModel getRequest() {

        return request;
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
     */
    public ResponseModel getResponse() {

        return response;
    }

    /**
     * @param response
     *            the response to set
     */
    public void setResponse(ResponseModel response) {

        this.response = response;
    }
}