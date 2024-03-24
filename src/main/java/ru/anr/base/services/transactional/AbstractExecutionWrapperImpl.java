/*
 * Copyright 2014-2024 the original author or authors.
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
