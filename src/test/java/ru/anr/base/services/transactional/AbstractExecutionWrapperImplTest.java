package ru.anr.base.services.transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.anr.base.ApplicationException;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.samples.services.TestDataService;
import ru.anr.base.services.BaseLocalServiceTestCase;

import java.util.List;

public class AbstractExecutionWrapperImplTest extends BaseLocalServiceTestCase {

    @Autowired
    @Qualifier("TestDataService")
    private TestDataService service;


    private static class MainExecution extends AbstractExecutionWrapperImpl<Samples, List<Samples>> {
        @Override
        protected List<Samples> onProcess(Samples object, Object... params) {
            if ("ERROR".equals(object.getName())) throw new ApplicationException("ERROR RAISED");
            object.setName("DONE");
            return list(object);
        }

        @Override
        protected List<Samples> onError(Samples object, Throwable exception, Object... params) {
            object.setName(exception.getMessage());
            return list();
        }
    }


    @Test
    public void process() {

        MainExecution main = new MainExecution();

        // 1. Normal execution
        Samples s = dao.save(new Samples());

        List<Samples> rs = service.execute(main, s);
        Assertions.assertEquals("DONE", s.getName());
        Assertions.assertEquals(1, rs.size());
        Assertions.assertEquals(s, rs.get(0));

        // 2. Error execution
        s = dao.save(new Samples());
        s.setName("ERROR");

        rs = service.execute(main, s);
        Assertions.assertEquals("ERROR RAISED", s.getName());
        Assertions.assertEquals(0, rs.size());
    }
}
