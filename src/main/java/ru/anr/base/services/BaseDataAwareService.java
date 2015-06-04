/**
 * 
 */
package ru.anr.base.services;

import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import ru.anr.base.domain.BaseEntity;

/**
 * The certain kind of interface with database specific functions.
 *
 *
 * @author Alexey Romanchuk
 * @created Jun 3, 2015
 *
 */

public interface BaseDataAwareService extends BaseService {

    /**
     * Saves a new entity to the database. If the entity is already stored DAO
     * operation is not invoked.
     * 
     * @param object
     *            The object to save
     * @return Saved entity
     * 
     * @param <S>
     *            Type of the entity
     */
    @PreAuthorize("hasPermission(#o,'write') or hasPermission(#o,'access_write')")
    <S extends BaseEntity> S save(@P("o") S object);
}
