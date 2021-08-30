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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import ru.anr.base.BaseParent;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * A base content model of the response's body (parsed from JSON or XML). It
 * also contains fields for representation of error response (with code and
 * error message).
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */
@XmlRootElement(name = "response")
public class ResponseModel extends BaseParent implements Serializable {

    /**
     * The serial code
     */
    private static final long serialVersionUID = 6293179038331851434L;

    ///// Codes

    /**
     * The response code. 0 mean 'success'
     */
    @XmlAttribute(name = "code")
    public Integer code;

    /**
     * The unique identifier of error (if specified)
     */
    @XmlAttribute(name = "error_id")
    public String errorId;

    /**
     * The user-friendly message for error
     */
    @XmlElement(name = "message")
    public String message;

    /**
     * Description gives some details about error
     */
    @XmlElement(name = "description")
    public String description;


    ///// Paging

    /**
     * The current page number
     */
    @XmlAttribute(name = "page")
    public Integer page;

    /**
     * The number of items on the page
     */
    @XmlAttribute(name = "per_page")
    public Integer perPage;

    /**
     * The total number of results (if known)
     */
    @XmlAttribute(name = "total")
    public Long total;

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
