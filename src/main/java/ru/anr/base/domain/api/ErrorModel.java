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

import javax.xml.bind.annotation.XmlElement;

import ru.anr.base.domain.api.models.ResponseModel;

/**
 * API Error model. Contains fields for serialization a message, code and
 * description.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class ErrorModel extends ResponseModel {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 1741010058203291704L;

    /**
     * A user-friedly message for error
     */
    private String message;

    /**
     * Description gives some details about error
     */
    private String description;

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the message
     */
    @XmlElement(name = "message")
    public String getMessage() {

        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {

        this.message = message;
    }

    /**
     * @return the description
     */
    @XmlElement(name = "description")
    public String getDescription() {

        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {

        this.description = description;
    }

}
