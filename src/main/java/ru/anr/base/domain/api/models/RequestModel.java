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
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A base model for an API request.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */
@XmlRootElement(name = "request")
public class RequestModel extends ResponseModel implements Serializable {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 7797203596478650118L;

    /**
     * A list of fields used for sorting
     */
    private List<SortModel> sorted;

    /**
     * A full-text search query string
     */
    private String search;

    /**
     * A list of fields required in result set
     */
    private List<String> fields;

    /**
     * The identifier of a request
     */
    private String queryId;

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the page
     */
    @Override
    @XmlTransient
    public Integer getPage() {

        return super.getPage();
    }

    /**
     * @return the sorted
     */
    @XmlTransient
    public List<SortModel> getSorted() {

        return sorted;
    }

    /**
     * @param sorted
     *            the sorted to set
     */
    public void setSorted(List<SortModel> sorted) {

        this.sorted = sorted;
    }

    /**
     * @return the perPage
     */
    @Override
    @XmlTransient
    public Integer getPerPage() {

        return super.getPerPage();
    }

    /**
     * @return the search
     */
    @XmlTransient
    public String getSearch() {

        return search;
    }

    /**
     * @param search
     *            the search to set
     */
    public void setSearch(String search) {

        this.search = search;
    }

    /**
     * @return the fields
     */
    @XmlTransient
    public List<String> getFields() {

        return fields;
    }

    /**
     * @return the queryId
     */
    @XmlAttribute(name = "query_id")
    public String getQueryId() {

        return queryId;
    }

    /**
     * @param queryId
     *            the queryId to set
     */
    public void setQueryId(String queryId) {

        this.queryId = queryId;
    }

    /**
     * @param fields
     *            the fields to set
     */
    public void setFields(List<String> fields) {

        this.fields = fields;
    }

    /**
     * Excluded fields
     */
    private static final String[] EXCLUDED_FIELDS = new String[]{ "sorted", "page", "perPage", "search", "fields" };

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {

        return EqualsBuilder.reflectionEquals(this, obj, EXCLUDED_FIELDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this, EXCLUDED_FIELDS);
    }

}
