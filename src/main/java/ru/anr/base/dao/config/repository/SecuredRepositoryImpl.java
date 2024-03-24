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
package ru.anr.base.dao.config.repository;

import org.springframework.data.domain.Page;
import ru.anr.base.BaseSpringParent;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.dao.repository.SecuredRepository;
import ru.anr.base.domain.BaseEntity;

import java.util.List;

/**
 * An implementation of secured DAO operations.
 *
 * @author Alexey Romanchuk
 * @created Jul 24, 2022
 */
public class SecuredRepositoryImpl extends BaseSpringParent implements SecuredRepository {

    // The DAO repository for save/delete/read operations
    private BaseRepository<BaseEntity> dao;

    @Override
    public <S extends BaseEntity> S findSecured(Class<S> entityClass, Long id) {
        return dao.find(entityClass, id);
    }

    @Override
    public <S extends BaseEntity> S saveSecured(S entity) {
        return dao.save(entity);
    }

    @Override
    public void deleteSecured(BaseEntity entity) {
        dao.delete(entity);
    }

    @Override
    public <S extends BaseEntity> List<S> filterSecured(Page<S> page) {
        return list(page.getContent());
    }

    public void setDao(BaseRepository<BaseEntity> dao) {
        this.dao = dao;
    }
}
