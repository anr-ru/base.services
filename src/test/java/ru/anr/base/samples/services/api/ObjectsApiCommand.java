package ru.anr.base.samples.services.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.models.PingModel;
import ru.anr.base.domain.api.models.ResponseModel;
import ru.anr.base.domain.api.models.SampleModel;
import ru.anr.base.samples.dao.MyDao;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.services.api.AbstractApiCommandStrategyImpl;
import ru.anr.base.services.api.ApiStrategy;

/**
 * A special command which throws an {@link ru.anr.base.domain.api.APIException}
 * for test reason.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */
@ApiStrategy(version = "v1", id = "Objects", model = PingModel.class)
@Component
public class ObjectsApiCommand extends AbstractApiCommandStrategyImpl {
    @Autowired
    private MyDao dao;

    @Override
    public ResponseModel get(APICommand cmd) {
        Samples s = findObjectByID(cmd, id -> dao.find(Samples.class, id));
        return new SampleModel(s);
    }

    //@Override
    public Object patch(APICommand cmd) {
        Samples s = findObjectByID(cmd, "name", map -> first(dao.findSamples((String) map.get("name"))));
        return new SampleModel(s);
    }
}
