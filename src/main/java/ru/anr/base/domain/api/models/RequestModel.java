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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

/**
 * A base model for an API request.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */
@XmlRootElement(name = "request")
public class RequestModel extends ResponseModel implements Serializable {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 7797203596478650118L;

    /**
     * The parsed list of fields used for sorting
     */
    @XmlTransient
    public List<SortModel> sorted;

    /**
     * A full-text search query string
     */
    @XmlTransient
    public String search;

    /**
     * A list of fields required in result set
     */
    @XmlTransient
    public List<String> fields;

    /**
     * The identifier of a request
     */
    @XmlAttribute(name = "query_id")
    public String queryId;

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Excluded fields
     */
    private static final String[] EXCLUDED_FIELDS = new String[]{"sorted", "page", "perPage", "search", "fields"};

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, EXCLUDED_FIELDS);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, EXCLUDED_FIELDS);
    }

}
