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
package ru.anr.base.domain.api.models;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Base content model of reponse body (parsed from JSON or XML). It also
 * contains fields for representation of error response (with code and error
 * message).
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */
@XmlRootElement(name = "response")
public class ResponseModel implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6293179038331851434L;

    /**
     * Reponse code. 0 mean 'success'
     */
    private Integer code;

    /**
     * A unique identifier of en error (if specified)
     */
    private String errorId;

    /**
     * Current page number
     */
    private Integer page;

    /**
     * Number of items on the page
     */
    private Integer perPage;

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
     * @return the code
     */
    @XmlAttribute(name = "code")
    public Integer getCode() {

        return code;
    }

    /**
     * @return the page
     */
    @XmlAttribute(name = "page")
    public Integer getPage() {

        return page;
    }

    /**
     * @param page
     *            the page to set
     */
    public void setPage(Integer page) {

        this.page = page;
    }

    /**
     * @return the perPage
     */
    @XmlAttribute(name = "per_page")
    public Integer getPerPage() {

        return perPage;
    }

    /**
     * @param perPage
     *            the perPage to set
     */
    public void setPerPage(Integer perPage) {

        this.perPage = perPage;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(Integer code) {

        this.code = code;
    }

    /**
     * @return the message
     */
    @XmlElement(name = "message")
    public String getMessage() {

        return message;
    }

    /**
     * @return the errorId
     */
    @XmlAttribute(name = "error_id")
    public String getErrorId() {

        return errorId;
    }

    /**
     * @param errorId
     *            the errorId to set
     */
    public void setErrorId(String errorId) {

        this.errorId = errorId;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {

        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this);
    }

}
