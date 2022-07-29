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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;
import org.springframework.web.util.UriUtils;
import ru.anr.base.ApplicationException;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.APIException;
import ru.anr.base.domain.api.RawFormatTypes;
import ru.anr.base.domain.api.models.RequestModel;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.services.BaseServiceImpl;
import ru.anr.base.services.serializer.SerializationConfig;
import ru.anr.base.services.serializer.Serializer;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * API command factory - performs marshalling and unmarshalling of any api
 * commands.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */
@Import(SerializationConfig.class)
public class APICommandFactoryImpl extends BaseServiceImpl implements APICommandFactory {

    private static final Logger logger = LoggerFactory.getLogger(APICommandFactoryImpl.class);

    /**
     * The registry of API Commands. The map's keys are command identifiers, and the second key is
     * a version of the command.
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
     * Registration of API commands found in the spring context
     *
     * @param beans API Strategies beans
     */
    public void registerApi(Map<String, ApiCommandStrategy> beans) {

        logger.info("Registering '{}' api command beans", beans.size());

        for (Entry<String, ApiCommandStrategy> e : beans.entrySet()) {
            ApiStrategy a = e.getValue().config();
            if (a != null) {
                register(a, e.getValue());
            }
        }
        logger.debug("API Command registry: {}", commands);
    }

    /**
     * Registers a found api command strategy
     *
     * @param a The strategy configuration
     * @param s The API strategy bean
     */
    private void register(ApiStrategy a, ApiCommandStrategy s) {

        String aId = a.id().toLowerCase();
        String aV = a.version().toLowerCase();

        Map<String, ApiCommandStrategy> versions = commands.computeIfAbsent(aId, k -> new HashMap<>());

        Assert.isTrue(!versions.containsKey(aV), "Duplicate version " + aV + " for " + aId);
        versions.put(aV, s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand process(APICommand cmd) {

        ApiCommandStrategy s = findStrategy(cmd);
        processRequestModel(cmd, s.config());

        cmd.setResponse(doInvoke(s, cmd));
        processResponseModel(cmd);

        return cmd;
    }

    /**
     * Invokes specific API Strategy method
     *
     * @param s   Found strategy
     * @param cmd A command
     * @return A model of response
     */
    private Object doInvoke(ApiCommandStrategy s, APICommand cmd) {

        Object rs;
        logger.trace("Invoking {} method for {}/{}", cmd.getType(), cmd.getCommandId(), cmd.getVersion());

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
            case Patch:
                rs = s.patch(cmd);
                break;
            default:
                throw new UnsupportedOperationException(cmd.getType().name());
        }
        return rs;
    }

    /**
     * Finds a strategy for specified command by its identifier and version
     * marker.
     *
     * @param cmd Original command
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
    public APICommand error(Throwable ex) {
        return error(new APICommand("", ""), ex);
    }

    /**
     * Getting an error code. If an exception is {@link APIException},
     * extracting the code from it, otherwise using a system exception code.
     *
     * @param reason An exception
     * @return integer code
     */
    private int resolveErrorCode(Throwable reason) {

        int code = APIException.ERROR_SYSTEM;
        if (reason instanceof APIException) {
            code = ((APIException) reason).getErrorCode();
        } else if (reason instanceof ConstraintViolationException) {
            code = APIException.ERROR_CLIENT;
        }
        return code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand error(APICommand cmd, Throwable ex, ResponseModel m) {

        Throwable reason = new ApplicationException(ex).getMostSpecificCause();

        int code = resolveErrorCode(reason);
        m.code = code;

        if (reason instanceof ApplicationException) {

            ApplicationException e = (ApplicationException) reason;
            if (e.getErrorId() != null) {
                m.errorId = e.getErrorId();
            }
        }

        if (reason instanceof APIException) {

            // Maybe the integer code is defined
            String msg = (code != APIException.ERROR_CLIENT) ? text(errorCodePrefix + code) : null;
            if (msg == null || msg.startsWith("[xxx")) {
                String reasonMsg = reason.getMessage();
                if (notEmpty(m.errorId) && m.errorId.startsWith("files")) {
                    reasonMsg = UriUtils.encodePath(reasonMsg, UTF_8.toString());
                }
                m.message = reasonMsg;
            } else {
                m.message = msg;
            }
        } else if (reason instanceof ConstraintViolationException) {
            m.message = getExceptionMessage(reason);
        } else {
            m.message = reason.getMessage();
        }

        cmd.setResponse(m);
        processResponseModel(cmd);

        return cmd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand error(APICommand cmd, Throwable ex) {
        return error(cmd, ex, new ResponseModel());
    }

    /**
     * A special processing for {@link org.hibernate.exception.ConstraintViolationException},
     * which occurs in validations.
     *
     * @param reason The reason exception
     * @return Exception message
     */
    private String getExceptionMessage(Throwable reason) {

        String rs;
        logger.trace("Processing an exception: {}", reason.getClass());

        if (reason instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) reason).getConstraintViolations();
            rs = getAllErrorsAsString(violations);
        } else {
            rs = reason.getMessage();
        }
        return rs;
    }

    /**
     * Deserializing the raw content of a request into the required model. If
     * raw content is null, the function does nothing.
     *
     * @param cmd Command
     * @param a   Annotation of strategy
     */
    private void processRequestModel(APICommand cmd, ApiStrategy a) {

        if (cmd.getRawModel() != null) {

            Assert.notNull(cmd.getRequestFormat(), "Request content format is empty");
            Serializer s = getSerializer(cmd.getRequestFormat());

            try {
                logger.trace("Raw request data: {}", cmd.getRawModel());
                if (s != null) {
                    RequestModel m = s.fromStr(cmd.getRawModel(), a.model());
                    if (cmd.getRequest() != null && (m != null)) {
                        /*
                         * This means - we have parsed previously some query params
                         */
                        m.fields = cmd.getRequest().fields;
                        m.page = cmd.getRequest().page;
                        m.perPage = cmd.getRequest().perPage;
                        m.search = cmd.getRequest().search;
                        m.sorted = cmd.getRequest().sorted;
                    } else {
                        logger.debug("No RequestModel was detected for {}", cmd);
                    }
                    cmd.setRequest(m);
                }
            } catch (Exception ex) {
                logger.error("Unable to parse: {}", cmd.getRawModel());
                throw ex;
            }
        } else {
            logger.trace("Raw model is null for {}", cmd);
        }
    }

    /**
     * Serializing the built response model to a raw string. If model is null, the
     * function does nothing.
     *
     * @param cmd Command
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

        if (m instanceof ResponseModel && ((ResponseModel) m).code == null) {
            ((ResponseModel) m).code = 0;
        } else if (m instanceof ResponseModel && ((ResponseModel) m).code == Integer.MAX_VALUE) {
            // A way to reset the code value if code=0 is not required
            ((ResponseModel) m).code = null;
        }

        Serializer s = getSerializer(cmd.getResponseFormat());

        if (s == null) {
            cmd.setRawModel(nullSafe(m)); // RAW
        } else {
            cmd.setRawModel(s.toStr(m));
        }
        logger.debug("raw api response: {}", cmd.getRawModel());
    }

    /**
     * Returns a serializer instance depending on the specified format
     *
     * @param format Format code
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
            case RAW:
                break;
            default:
                throw new UnsupportedOperationException("Unsupported format : " + format);
        }
        return s;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///// getters/setters
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param errorCodePrefix the errorCodePrefix to set
     */
    public void setErrorCodePrefix(String errorCodePrefix) {
        this.errorCodePrefix = errorCodePrefix;
    }
}
