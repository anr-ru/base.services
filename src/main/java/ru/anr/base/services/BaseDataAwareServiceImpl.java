/**
 * 
 */
package ru.anr.base.services;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;

/**
 * A parent class for all business logic services, which need a database access.
 * We suppose the necessery DAO repository was injected.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */
@Transactional(propagation = Propagation.REQUIRED)
public class BaseDataAwareServiceImpl extends BaseServiceImpl {

    /**
     * Base repository
     */
    private BaseRepository<BaseEntity> repository;

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param dao
     *            the dao to set
     */
    public void setDao(BaseRepository<BaseEntity> dao) {

        this.repository = dao;
    }

    /**
     * Trying to make it of 'R' type
     * 
     * @return the dao param
     * 
     * @param <T>
     *            Entity class
     * @param <S>
     *            Dao class
     */
    @SuppressWarnings("unchecked")
    protected <T extends BaseEntity, S extends BaseRepository<T>> S dao() {

        return (S) repository;
    }
}
