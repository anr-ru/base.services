/**
 * 
 */
package ru.anr.base.samples.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 *
 */

@XmlRootElement(name = "model")
public class Model {

    /**
     * Some field
     */
    private String field;

    /**
     * Submodels
     */
    private List<SubModel> subs;

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the field
     */
    @XmlAttribute(name = "field")
    public String getField() {

        return field;
    }

    /**
     * @return the subs
     */
    @XmlElementWrapper(name = "subs")
    @XmlElement(name = "sub")
    public List<SubModel> getSubs() {

        return subs;
    }

    /**
     * @param subs
     *            the subs to set
     */
    public void setSubs(List<SubModel> subs) {

        this.subs = subs;
    }

    /**
     * @param field
     *            the field to set
     */
    public void setField(String field) {

        this.field = field;
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
