/**
 * 
 */
package ru.anr.base.domain.api;

import javax.xml.bind.annotation.XmlElement;

import ru.anr.base.domain.api.models.ResponseModel;

/**
 * Description ...
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
