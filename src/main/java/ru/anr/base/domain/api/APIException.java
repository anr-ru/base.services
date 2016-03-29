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

public final class APIException extends ApplicationException {

    /**
     * Error code for system error
     */
    public static final int ERROR_SYSTEM = 1;

    /**
     * Error code for validation errors
     */
    public static final int ERROR_VALIDATION = -1;

    /**
     * Serial ID
     */
    private static final long serialVersionUID = -1705171832150544996L;

    /**
     * Error code is useful for automatic parsing
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
    private APIException(String msg, int errorCode) {

        super(msg);
        this.errorCode = errorCode;
    }

    /**
     * Generates a validation exception
     * 
     * @param errorId
     *            A string identifier of the error
     * @param msg
     *            Full message linked with the error
     * 
     * @return An exception object
     */
    public static APIException validation(String errorId, String msg) {

        APIException ex = new APIException(msg, ERROR_VALIDATION);
        ex.setErrorId(errorId);

        return ex;
    }

    /**
     * Generates a API exception
     * 
     * @param msg
     *            Error message
     * @param errorCode
     *            Error code
     * 
     * @return An exception object
     */
    public static APIException withCode(String msg, int errorCode) {

        return new APIException(msg, errorCode);
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
