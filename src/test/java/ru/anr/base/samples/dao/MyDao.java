package ru.anr.base.samples.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.samples.domain.Samples;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * JpaRepository for working with database.
 *
 * @author Alexey Romanchuk
 * @created Oct 30, 2014
 */
@Repository("mydao")
public interface MyDao<T extends Samples> extends BaseRepository<T> {

    /**
     * Test query for Cache
     *
     * @return Some data
     */
    @Query(value = "From Samples")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheRegion", value = "samplequery"),
            @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<T> findCachedSamples();

    /**
     * Test query for Query without cache
     *
     * @param name Some param
     * @return Some data
     */
    @Query(name = "SampleOne", value = "From Samples where name like :x")
    List<T> findSamples(@Param("x") String name);

    /**
     * An example of update query
     *
     * @param id Some ID
     * @return Number of updated rows
     */
    @Modifying
    @Query("update Samples set name = 'xxx' where id = :id")
    @Transactional
    Integer updateSamples(@Param("id") Long id);

    /**
     * Tests for paging
     *
     * @param p Page params
     * @return Next page
     */
    @Query(countQuery = "select count(*) from Samples x where x.id >= 1", value = "From Samples x where x.id >= 1")
    Page<T> pages(Pageable p);
}
