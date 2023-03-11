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
public abstract class AbstractExecutionWrapperImpl<T>
        extends BaseDataAwareServiceImpl implements ExecutionWrapper<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractExecutionWrapperImpl.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(T document, Object... params) {
        // We have to reload the object because it is in a new transaction
        onProcess(reload(document), params);
    }

    // For overriding in particular executors
    abstract protected void onProcess(T object, Object... params);

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processForTests(T object, Object... params) {
        onProcess(object, params);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void error(T object, Throwable exception, Object... params) {
        this.errorForTests(object, exception, params);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void errorForTests(T object, Throwable exception, Object... params) {
        onError(object, exception, params);
    }

    // For overriding in particular executors
    abstract protected void onError(T object, Throwable exception, Object... params);
}
