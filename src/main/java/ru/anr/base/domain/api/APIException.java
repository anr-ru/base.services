/**
 * 
 */
package ru.anr.base.domain.api;

import ru.anr.base.ApplicationException;

/**
 * Special kind of exception with an integer error code.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */

public class APIException extends ApplicationException {

    /**
     * Error code for system erro
     */
    public static final int ERROR_SYSTEM = 1;

    /**
     * Serial ID
     */
    private static final long serialVersionUID = -1705171832150544996L;

    /**
     * Error code - usefull for automatic pasring
     */
    private final int errorCode;

    /**
     * Constructor
     * 
     * @param msg
     *            Error message
     * @param errorCode
     *            Error code
     */
    public APIException(String msg, int errorCode) {

        super(msg);
        this.errorCode = errorCode;
    }

    /**
     * Constructor
     * 
     * @param msg
     *            An error message
     * @param errorCode
     *            Error code
     * @param cause
     *            Root exception
     */
    public APIException(String msg, Throwable cause, int errorCode) {

        super(msg, cause);
        this.errorCode = errorCode;
    }

    /**
     * Constructor
     * 
     * @param cause
     *            Root exception
     * @param errorCode
     *            Error code
     */
    public APIException(Throwable cause, int errorCode) {

        this("", cause, errorCode);
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the errorCode
     */
    public int getErrorCode() {

        return errorCode;
    }
}
