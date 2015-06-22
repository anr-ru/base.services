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

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import ru.anr.base.ApplicationException;
import ru.anr.base.BaseSpringParent;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.models.ResponseModel;
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

        ResponseModel m = new ResponseModel();
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
    public APICommand error(APICommand cmd, Throwable ex) {

        return generateError(64, "System error", ex.getMessage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public APICommand error(Throwable ex) {

        return generateError(64, "System error", ex.getMessage());
    }
}
