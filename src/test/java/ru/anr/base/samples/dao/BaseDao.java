/**
 * 
 */
package ru.anr.base.samples.dao;

import org.springframework.stereotype.Repository;

import ru.anr.base.dao.BaseRepository;
import ru.anr.base.samples.domain.Samples;

/**
 * Default sample dao bean.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 17, 2014
 *
 */
@Repository("sampledao")
public interface BaseDao extends BaseRepository<Samples> {

}
