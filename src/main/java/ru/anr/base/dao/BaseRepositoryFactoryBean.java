/**
 * 
 */
package ru.anr.base.dao;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;

/**
 * A factory bean for creation of our custom repository instance. This code is
 * taken from Spring Data Reference Docs.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 6, 2014
 *
 */

public class BaseRepositoryFactoryBean<T extends BaseEntity> extends
        JpaRepositoryFactoryBean<BaseRepository<T>, T, Long> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {

        return new InternalRepositoryFactory<T>(entityManager);
    }

    /**
     * Repository factory
     */
    private static class InternalRepositoryFactory<T extends BaseEntity> extends JpaRepositoryFactory {

        /**
         * Entity manager ref
         */
        private final EntityManager entityManager;

        /**
         * Constructor
         * 
         * @param entityManager
         *            {@link EntityManager} from config
         */
        InternalRepositoryFactory(EntityManager entityManager) {

            super(entityManager);

            this.entityManager = entityManager;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Object getTargetRepository(RepositoryMetadata metadata) {

            @SuppressWarnings("unchecked")
            Class<T> clazz = (Class<T>) metadata.getDomainType();

            return new BaseRepositoryImpl<T>(clazz, entityManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {

            // The RepositoryMetadata can be safely ignored, it is used by the
            // JpaRepositoryFactory
            // to check for QueryDslJpaRepository's which is out of scope.
            return BaseRepository.class;
        }
    }
}
