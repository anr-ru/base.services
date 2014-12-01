/**
 * 
 */
package ru.anr.base.samples.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.services.BaseDataAwareServiceImpl;

/**
 * Description ...
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 21, 2014
 *
 */
@Component("TestDataService")
public class TestDataServiceImpl extends BaseDataAwareServiceImpl {

    /**
     * {@inheritDoc}
     */
    @Override
    @Autowired
    @Qualifier("BaseRepository")
    public void setDao(BaseRepository<BaseEntity> dao) {

        super.setDao(dao);
    }
}
