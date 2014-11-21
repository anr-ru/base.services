/**
 * 
 */
package ru.anr.base.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.anr.base.domain.BaseEntity;

/**
 * Custom repository interface for extensions.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */
@Repository("BaseRepository")
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    /**
     * Refresh object from data base
     * 
     * @param object
     *            Object
     */
    void refresh(T object);

    /**
     * Executes a 'raw' (not predefine named) query. You can provide just a JPA
     * query string. It can be useful in tests.
     * 
     * @param queryStr
     *            A JPQL query string (for instance, "from Samples") with
     *            positioned (aka ?1) or named (like :name) parameters.
     * @param params
     *            Paratemers of the query
     * @return A result list
     * 
     * @param <S>
     *            A result set object types
     */
    <S extends BaseEntity> List<S> query(String queryStr, Object... params);
}
