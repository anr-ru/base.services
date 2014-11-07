/**
 * 
 */
package ru.anr.base.samples.domain;

import javax.xml.bind.annotation.XmlValue;

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

public class SubModel {

    /**
     * Some element
     */
    private Integer value;

    /**
     * Constructor
     * 
     * @param value
     *            Some value
     */
    public SubModel(int value) {

        this.value = value;
    }

    /**
     * Default constructor
     */
    public SubModel() {

        /*
         * Empty
         */

    }

    /**
     * @return the value
     */
    @XmlValue
    public int getValue() {

        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(int value) {

        this.value = value;
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
