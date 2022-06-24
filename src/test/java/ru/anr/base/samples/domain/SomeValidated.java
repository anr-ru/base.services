package ru.anr.base.samples.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Description ...
 *
 * @author Alexey Romanchuk
 * @created Jan 30, 2015
 */

public class SomeValidated {

    /**
     * Some value
     */
    @NotNull(message = "{not.null.value}")
    private String value;

    /**
     * Some digit
     */
    @Min(value = 5)
    private int digit;

    /**
     * @return the value
     */
    public String getValue() {

        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {

        this.value = value;
    }

    /**
     * @return the digit
     */
    public int getDigit() {

        return digit;
    }

    /**
     * @param digit the digit to set
     */
    public void setDigit(int digit) {

        this.digit = digit;
    }
}
