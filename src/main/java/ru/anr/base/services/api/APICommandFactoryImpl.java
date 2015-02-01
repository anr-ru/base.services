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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import ru.anr.base.ApplicationException;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.APIException;
import ru.anr.base.domain.api.RawFormatTypes;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.services.BaseServiceImpl;
import ru.anr.base.services.serializer.SerializationConfig;
import ru.anr.base.services.serializer.Serializer;

/**
 * API command factory - performs marchalling and unmarchalling of any api
 * commands.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */
@Import(SerializationConfig.class)
public class APICommandFactoryImpl extends BaseServiceImpl implements APICommandFactory {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(APICommandFactoryImpl.class);

    /**
     * API Commands. Map's key is a command identifier, and the second key is
     * 'version'
     */
    private final Map<String, Map<String, ApiCommandStrategy>> commands = new HashMap<>();

    /**
     * JSON Serialization
     */
    @Autowired
    @Qualifier("jsonSerializer")
    private Serializer json;

    /**
     * XML Serialization
     */
    @Autowired
    @Qualifier("xmlSerializer")
    private Serializer xml;

    /**
     * Error prefix for specific error code description, which is stored in text
     * resources
     */
    private String errorCodePrefix = "api.errorcode.";

    /**
     * Registration of found in spring context API commands
     * 
     * @param beans
     *            Beans
     */
    public void registerApi(Map<String, ApiCommandStrategy> beans) {

        logger.info("Registering '{}' api command beans", beans.size());

        for (Entry<String, ApiCommandStrategy> e : beans.entrySet()) {

            ApiStrategy a = getAnnotation(target(e.getValue()));
            if (a != null) {
                register(a, e.getValue());
            }
        }
        logger.debug("API Command registry: {}", commands);
    }

    /**
     * Find annotaion of API Strategy command class
     * 
     * @param s
     *            API Strategy command
     * @return Annotation instance
     */
    private ApiStrategy getAnnotation(ApiCommandStrategy s) {

        return AnnotationUtils.findAnnotation(s.getClass(), ApiStrategy.class);
    }

    /**
     * Registration of found api command strategy
     * 
     * @param a
     *            Strategy configuration
     * @param s
     *            API strategy bean
     */
    private void register(ApiStrategy a, ApiCommandStrategy s) {

        String aId = a.id().toLowerCase();
        String aV = a.version().toLowerCase();

        Map<String, ApiCommandStrategy> versions = commands.get(aId);

        if (versions == null) {
            versions = new HashMap<>();
            commands.put(aId, versions);
        }

        Assert.isTrue(!versions.containsKey(aV), "Duplicate version " + aV + " for " + aId);
        versions.put(aV, s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand process(APICommand cmd) {

        ApiCommandStrategy s = findStrategy(cmd);
        processRequestModel(cmd, getAnnotation(target(s)));

        Object rs = doInvoke(s, cmd);
        cmd.setResponse(rs);

        processResponseModel(cmd);

        return cmd;
    }

    /**
     * Invokes specific API Strategy method
     * 
     * @param s
     *            Found strategy
     * @param cmd
     *            A command
     * @return A model of response
     */
    private Object doInvoke(ApiCommandStrategy s, APICommand cmd) {

        Object rs = null;
        logger.debug("Invoking {} method for {} / {}", cmd.getType(), cmd.getCommandId(), cmd.getVersion());

        switch (cmd.getType()) {
            case Get:
                rs = s.get(cmd);
                break;
            case Delete:
                rs = s.delete(cmd);
                break;
            case Post:
                rs = s.post(cmd);
                break;
            case Put:
                rs = s.put(cmd);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return rs;

    }

    /**
     * Finds a strategy for specified command by its identifier and version
     * marker.
     * 
     * @param cmd
     *            Original command
     * @return A processing strategy
     */
    private ApiCommandStrategy findStrategy(APICommand cmd) {

        Objects.requireNonNull(cmd.getCommandId(), "Command ID is null");
        Objects.requireNonNull(cmd.getVersion(), "Version ID is null");

        String id = cmd.getCommandId().toLowerCase();
        String v = cmd.getVersion().toLowerCase();

        Assert.isTrue(commands.containsKey(id), "Command '" + id + "' not found");
        Map<String, ApiCommandStrategy> versions = commands.get(id);

        Assert.isTrue(versions.containsKey(v), "Version '" + v + "' for a command '" + id + "' does not exist");
        return versions.get(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand error(Exception ex) {

        return error(new APICommand("", ""), ex);
    }

    /**
     * Getting an error code. If an exception is {@link APIException},
     * extracting the code from it, otherwise using a system exception code.
     * 
     * @param ex
     *            An exception
     * @return integer code
     */
    private int resolveErrorCode(Exception ex) {

        Throwable reason = new ApplicationException(ex).getRootCause();
        return (reason instanceof APIException) ? ((APIException) reason).getErrorCode() : APIException.ERROR_SYSTEM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand error(APICommand cmd, Exception ex) {

        ResponseModel m = new ResponseModel();

        int code = resolveErrorCode(ex);
        m.setCode(code);

        String msg = text(errorCodePrefix + code);
        if (msg.startsWith("[xxx")) {
            msg = ex.getMessage();
        }

        m.setMessage(msg);
        m.setDescription(ex.getMessage());

        cmd.setResponse(m);
        processResponseModel(cmd);

        return cmd;
    }

    private String getExceptionMessage(Exception ex) {

        String rs = null;

        Throwable e = new ApplicationException(ex).getMostSpecificCause();
        if (e instanceof ConstraintViolationException) {

            Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) e).getConstraintViolations();
            rs = getAllErrorsAsString(violations);
        } else {
            rs = ex.getMessage();
        }
        return rs;
    }

    /**
     * Derializing raw content of request into required model. If raw content is
     * null, the function does nothing.
     * 
     * @param cmd
     *            Command
     * @param a
     *            Annotation of strategy
     */
    private void processRequestModel(APICommand cmd, ApiStrategy a) {

        if (cmd.getRawModel() != null) {

            Assert.notNull(cmd.getRequestFormat(), "Request content format is empty");
            Serializer s = getSerializer(cmd.getRequestFormat());

            RequestModel m = s.fromStr(cmd.getRawModel(), a.model());
            if (cmd.getRequest() != null) {
                /*
                 * This means - we have parsed previously some query params
                 */
                m.setFields(cmd.getRequest().getFields());
                m.setPage(cmd.getRequest().getPage());
                m.setPerPage(cmd.getRequest().getPerPage());
                m.setSearch(cmd.getRequest().getSearch());
                m.setSorted(cmd.getRequest().getSorted());
            } else {
                logger.info("Unable to parse request model for {}", cmd);
            }
            cmd.setRequest(m);
        } else {
            logger.debug("Raw model is null for {}", cmd);
        }
    }

    /**
     * Serializing a buit response model to raw string. If model is null, the
     * function does nothing.
     * 
     * @param cmd
     *            Command
     */
    private void processResponseModel(APICommand cmd) {

        Object m = cmd.getResponse();
        if (m == null) {
            /*
             * This is specific case. If no response was generated, we suppose
             * to use a default response model with code of successful
             * operation.
             */
            m = new ResponseModel();
            cmd.setResponse(m);
        }

        if (cmd.getResponseFormat() == null) {
            cmd.setResponseFormat(RawFormatTypes.JSON); // default
        }

        Serializer s = getSerializer(cmd.getResponseFormat());

        cmd.setRawModel(s.toStr(m));
        logger.debug("raw api response: {}", cmd.getRawModel());
    }

    /**
     * Returns a serializer instance depending on specified format
     * 
     * @param format
     *            Format code
     * @return Serializer instance
     */
    private Serializer getSerializer(RawFormatTypes format) {

        Serializer s = null;

        switch (format) {
            case JSON:
                s = json;
                break;
            case XML:
                s = xml;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported format : " + format);
        }
        return s;
    }

    // /////////////////////////////////////////////////////////////////////////
    // ///
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param errorCodePrefix
     *            the errorCodePrefix to set
     */
    public void setErrorCodePrefix(String errorCodePrefix) {

        this.errorCodePrefix = errorCodePrefix;
    }
}
