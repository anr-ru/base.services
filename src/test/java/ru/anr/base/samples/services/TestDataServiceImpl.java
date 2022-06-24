/**
 *
 */
package ru.anr.base.samples.services;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.services.BaseDataAwareServiceImpl;

import javax.annotation.PostConstruct;

/**
 * Description ...
 *
 * @author Alexey Romanchuk
 * @created Nov 21, 2014
 */
@Component("TestDataService")
public class TestDataServiceImpl extends BaseDataAwareServiceImpl implements TestDataService {

    @PostConstruct
    public void init() {
        registerExtensions("default", loadExtensions(ServiceExt1.class)); // 2 extensions
        registerExtensions("test", loadExtensions(ServiceExt2.class)); // 1 extensions
    }

    @Override
    public Object doExtension(String ext) {
        return (ext == null) ? processExtensions(null) : processExtensions(ext, null, (Object[]) null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Samples doInTransaction(Samples old, boolean reload) {
        Samples o = reload ? reload(old) : old;
        o.setName("XXX");
        return dao().saveAndFlush(o);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteInTransaction(Samples s) {
        Samples o = reload(s);
        dao().delete(o);
    }
}
