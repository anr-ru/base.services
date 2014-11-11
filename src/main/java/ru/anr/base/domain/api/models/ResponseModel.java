/**
 * 
 */
package ru.anr.base.domain.api.models;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Base content model of reponse body (parsed from JSON or XML)
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
    private int code = 0;

    /**
     * Current page number
     */
    private Integer page;

    /**
     * Number of items on the page
     */
    private Integer perPage;

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the code
     */
    @XmlAttribute(name = "code")
    public int getCode() {

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
    public void setCode(int code) {

        this.code = code;
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