/**
 * 
 */
package ru.anr.base.domain.api.models;

import java.io.Serializable;

/**
 * Basic Sort operation model.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class SortModel implements Serializable {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 394767569835000862L;

    /**
     * Name of field
     */
    private String field;

    /**
     * {@link SortDirection}
     */
    private SortDirection direction;

    /**
     * Constructor
     * 
     * @param field
     *            Name of field
     * @param direction
     *            Sorting direction
     */
    public SortModel(String field, SortDirection direction) {

        this.field = field;
        this.direction = direction;
    }

    /**
     * ASC or DESC directions
     */
    public enum SortDirection {
        /**
         * ASC
         */
        ASC,
        /**
         * DESC
         */
        DESC;
    }

    /**
     * @return the field
     */
    public String getField() {

        return field;
    }

    /**
     * @param field
     *            the field to set
     */
    public void setField(String field) {

        this.field = field;
    }

    /**
     * @return the direction
     */
    public SortDirection getDirection() {

        return direction;
    }

    /**
     * @param direction
     *            the direction to set
     */
    public void setDirection(SortDirection direction) {

        this.direction = direction;
    }

}
