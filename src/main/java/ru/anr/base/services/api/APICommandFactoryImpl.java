/**
 * 
 */
package ru.anr.base.services.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.APIException;
import ru.anr.base.domain.api.ErrorModel;
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

            ApiStrategy a = getAnnotation(e.getValue());
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

        Map<String, ApiCommandStrategy> versions = commands.get(a.id());

        if (versions == null) {
            versions = new HashMap<>();
            commands.put(a.id(), versions);
        }

        Assert.isTrue(!versions.containsKey(a.version()), "Duplicate version " + a.version() + " for " + a.id());
        versions.put(a.version(), s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand process(APICommand cmd) {

        ApiCommandStrategy s = findStrategy(cmd);
        processRequestModel(cmd, getAnnotation(s));

        APICommand c = s.process(cmd);
        processResponseModel(c);

        return c;
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

        Assert.isTrue(commands.containsKey(cmd.getCommandId()), "Command '" + cmd.getCommandId() + "' not found");
        Map<String, ApiCommandStrategy> versions = commands.get(cmd.getCommandId());

        Assert.isTrue(versions.containsKey(cmd.getVersion()), "Version '" + cmd.getVersion() + "' for a command '"
                + cmd.getCommandId() + "' does not exist");

        return versions.get(cmd.getVersion());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand error(APICommand cmd, Exception ex) {

        ErrorModel m = new ErrorModel();

        int code = (ex instanceof APIException) ? ((APIException) ex).getErrorCode() : APIException.ERROR_SYSTEM;
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
            }
            cmd.setRequest(m);
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

        ResponseModel m = cmd.getResponse();
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
