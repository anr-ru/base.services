package ru.anr.base.dao;

import org.hibernate.proxy.HibernateProxy;
import ru.anr.base.domain.BaseEntity;

/**
 * Entity Utils
 *
 * @author Alexey Romanchuk
 * @created Aug 29, 2021
 */
public final class EntityUtils {

    private EntityUtils() {

    }

    /**
     * Extracts the entity's class for the specified entity which can be a
     * proxied hibernate entity. The implementation is Hibernate specific.
     *
     * @param entity Entity
     * @return Entity's class
     */
    public static Class<?> entityClass(BaseEntity entity) {

        Object r = entity(entity);
        return r.getClass();
    }

    /**
     * Extracts a pure entity for the given hibernate entity which can be a
     * proxied. The implementation is Hibernate specific.
     *
     * @param entity The entity
     * @param <S>    Object type
     * @return Pure entity
     */
    @SuppressWarnings("unchecked")
    public static <S extends BaseEntity> S entity(S entity) {

        S r = entity;

        if (entity instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) entity;
            r = (S) proxy.getHibernateLazyInitializer().getImplementation();
        }
        return r;
    }
}