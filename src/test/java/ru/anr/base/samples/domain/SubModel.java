package ru.anr.base.samples.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlValue;

/**
 * Description ...
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 */

public class SubModel {

    /**
     * Some element
     */
    @XmlValue
    public Integer value;

    /**
     * Constructor
     *
     * @param value Some value
     */
    public SubModel(int value) {

        this.value = value;
    }

    /**
     * Default constructor
     */
    public SubModel() {
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
