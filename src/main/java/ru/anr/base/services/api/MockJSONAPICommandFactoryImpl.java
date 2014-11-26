/**
 * 
 */
package ru.anr.base.services.api;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import ru.anr.base.ApplicationException;
import ru.anr.base.BaseSpringParent;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.ErrorModel;
import ru.anr.base.services.serializer.SerializationConfig;
import ru.anr.base.services.serializer.Serializer;

/**
 * A mock implementation for {@link APICommandFactory}. It can be used for
 * prototyping JSON responses with given API command.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 26, 2014
 *
 */
@Import(SerializationConfig.class)
public class MockJSONAPICommandFactoryImpl extends BaseSpringParent implements APICommandFactory {

    /**
     * JSON Serialization
     */
    @Autowired
    @Qualifier("jsonSerializer")
    private Serializer json;

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand process(APICommand cmd) {

        /*
         * Default implementation return an empty response for all commands
         */
        return generateResponse("{}", cmd);
    }

    /**
     * Loading a file from classpath
     * 
     * @param filePath
     *            (relative from classpath)
     * @return File content
     */
    protected String loadFromFile(String filePath) {

        try {

            ClassPathResource r = new ClassPathResource(filePath);
            return IOUtils.toString(r.getInputStream(), "utf-8");

        } catch (IOException ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * Generates JSON Error model for given parameters. <code>jsonStr</code> can
     * be loaded from file with {@link #loadFromFile(String)}.
     * 
     * @param jsonStr
     *            JSON string with response
     * @param originalCommand
     *            Original command
     * @return API Command
     */
    protected APICommand generateResponse(String jsonStr, APICommand originalCommand) {

        originalCommand.setRawModel(jsonStr);
        return originalCommand;
    }

    /**
     * Generates JSON Error model for given parameters
     * 
     * @param code
     *            Error code
     * @param msg
     *            Error message
     * @param description
     *            Error description
     * @return API Command
     */
    protected APICommand generateError(int code, String msg, String description) {

        APICommand cmd = new APICommand(null, null);

        ErrorModel m = new ErrorModel();
        m.setCode(64);
        m.setDescription("A system error description");
        m.setMessage("System error");

        cmd.setRawModel(json.toStr(m));
        return cmd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand error(APICommand cmd, Exception ex) {

        return generateError(64, "System error", ex.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand error(Exception ex) {

        return generateError(64, "System error", ex.getMessage());
    }
}
