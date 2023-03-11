package ru.anr.base.services.transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.anr.base.services.BaseDataAwareServiceImpl;

/**
 * An abstract implementation of the {@link ExecutionWrapper}.
 *
 * @created Mar 11, 2023
 */
public abstract class AbstractExecutionWrapperImpl<T, R>
        extends BaseDataAwareServiceImpl implements ExecutionWrapper<T, R> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractExecutionWrapperImpl.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R process(T document, Object... params) {
        // We have to reload the object because it is in a new transaction
        return onProcess(reload(document), params);
    }

    // For overriding in particular executors
    abstract protected R onProcess(T object, Object... params);

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public R processForTests(T object, Object... params) {
        return onProcess(object, params);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R error(T object, Throwable exception, Object... params) {
        return this.errorForTests(object, exception, params);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public R errorForTests(T object, Throwable exception, Object... params) {
        return onError(object, exception, params);
    }

    // For overriding in particular executors
    abstract protected R onError(T object, Throwable exception, Object... params);
}
