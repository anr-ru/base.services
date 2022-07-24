/*
 * Copyright 2014-2022 the original author or authors.
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

package ru.anr.base.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.anr.base.dao.repository.SecuredRepository;
import ru.anr.base.domain.BaseEntity;

/**
 * A secured implementation of the page with a filtration on the loaded content
 * according to the Spring Security rules defined for the object type.
 *
 * @param <T> The type of items
 * @author Alexey Romanchuk
 * @created Dec 18, 2019
 */

public class SecuredPageImpl<T extends BaseEntity> extends PageImpl<T> {

    private static final long serialVersionUID = 6120568128949835771L;

    /**
     * The default constructor with a filtration of the data
     *
     * @param repository Some repository bean
     * @param pages      The page object of the result set to secure
     */
    public SecuredPageImpl(SecuredRepository repository, Page<T> pages) {
        // We cannot determine the total number of elements here
        super(repository.filterSecured(pages), pages.getPageable(), pages.getTotalElements());
    }
}
