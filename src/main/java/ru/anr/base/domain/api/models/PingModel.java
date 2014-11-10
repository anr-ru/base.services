/**
 * 
 */
package ru.anr.base.domain.api.models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Ping model. Has one attribute
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */
@XmlRootElement(name = "ping")
public class PingModel extends RequestModel {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 6040666167936904587L;

    /**
     * Some value
     */
    private String value;

    /**
     * @return the value
     */
    @XmlAttribute(name = "value")
    public String getValue() {

        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {

        this.value = value;
    }

}
