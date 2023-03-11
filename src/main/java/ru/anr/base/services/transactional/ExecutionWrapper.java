package ru.anr.base.services.transactional;

/**
 * This wrapper executes the functions under new transaction.
 * <p>
 * This service is required when you need to use a new transaction but
 * for unit tests it is not so convenient as usually all data are rolled back
 * which leads to improper work of the service under testing.
 * <p>
 * This wrapper splits execution flow into several streams (normal processing
 * in the same transaction/in a new transaction amd error catching in the same
 * transaction/in a new transaction).
 *
 * @param <T> The type of the object to process
 * @param <R> The type of the resulted value
 * @created Mar 11, 2023
 */
public interface ExecutionWrapper<T, R> {

    /**
     * Processes the object in a new transaction.
     *
     * @param object The object
     * @param params The parameters
     */
    R process(T object, Object... params);

    /**
     * Processes the object in the test mode (when we need to have the same transaction).
     *
     * @param object The object
     * @param params The parameters
     */
    R processForTests(T object, Object... params);

    /**
     * Covers the error processing in a new transaction
     *
     * @param object    The object
     * @param exception The exception
     * @param params    The parameters
     */
    R error(T object, Throwable exception, Object... params);

    /**
     * Covers the error processing in the same transaction
     *
     * @param object    The document
     * @param exception The exception
     * @param params    The parameters
     */
    R errorForTests(T object, Throwable exception, Object... params);
}
