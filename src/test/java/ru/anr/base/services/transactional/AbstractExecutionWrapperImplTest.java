package ru.anr.base.services.transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.anr.base.ApplicationException;
import ru.anr.base.samples.domain.Samples;
import ru.anr.base.samples.services.TestDataService;
import ru.anr.base.services.BaseLocalServiceTestCase;

public class AbstractExecutionWrapperImplTest extends BaseLocalServiceTestCase {

    @Autowired
    @Qualifier("TestDataService")
    private TestDataService service;


    private static class MainExecution extends AbstractExecutionWrapperImpl<Samples> {
        @Override
        protected void onProcess(Samples object, Object... params) {
            if ("ERROR".equals(object.getName())) throw new ApplicationException("ERROR RAISED");
            object.setName("DONE");
        }

        @Override
        protected void onError(Samples object, Throwable exception, Object... params) {
            object.setName(exception.getMessage());
        }
    }


    @Test
    public void process() {

        MainExecution main = new MainExecution();

        // 1. Normal execution
        Samples s = dao.save(new Samples());

        service.execute(main, s);
        Assertions.assertEquals("DONE", s.getName());

        // 2. Error execution
        s = dao.save(new Samples());
        s.setName("ERROR");

        service.execute(main, s);
        Assertions.assertEquals("ERROR RAISED", s.getName());
    }
}
