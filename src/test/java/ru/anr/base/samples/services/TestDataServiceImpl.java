/**
 *
 */
package ru.anr.base.samples.services;

import org.springframework.stereotype.Component;
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
}
