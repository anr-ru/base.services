package ru.anr.base.services.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.models.SortModel;
import ru.anr.base.services.BaseLocalServiceTestCase;

/**
 * @author Alexey Romanchuk
 * @created Jun 23, 2022
 */
public class AbstractApiCommandStrategyImplTest extends BaseLocalServiceTestCase {

    @Test
    public void safePageable() {

        APICommand cmd = new APICommand("id", "v1");

        AbstractApiCommandStrategyImpl service = new AbstractApiCommandStrategyImpl();

        Pageable pager = service.safePageable(cmd, Sort.Direction.DESC);
        Assertions.assertEquals(0, pager.getPageNumber());
        Assertions.assertEquals(10, pager.getPageSize());
        Assertions.assertEquals(Sort.unsorted(), pager.getSort());

        cmd = new APICommand("id", "v1");

        pager = service.safePageable(cmd, Sort.Direction.DESC, "created");
        Assertions.assertEquals(0, pager.getPageNumber());
        Assertions.assertEquals(10, pager.getPageSize());
        Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "created"), pager.getSort());

        cmd = new APICommand("id", "v1");
        cmd.params(toMap("page", 5, "per_page", 50));

        pager = service.safePageable(cmd, Sort.Direction.ASC, "created");
        Assertions.assertEquals(5, pager.getPageNumber());
        Assertions.assertEquals(50, pager.getPageSize());
        Assertions.assertEquals(Sort.by(Sort.Direction.ASC, "created"), pager.getSort());
    }

    @Test
    public void buildPager() {
        APICommand cmd = new APICommand("id", "v1");

        AbstractApiCommandStrategyImpl service = new AbstractApiCommandStrategyImpl();

        Pageable pager = service.buildPager(cmd, "created", SortModel.SortDirection.DESC);
        Assertions.assertEquals(0, pager.getPageNumber());
        Assertions.assertEquals(10, pager.getPageSize());
        Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "created"), pager.getSort());

        cmd = new APICommand("id", "v1");
        cmd.params(toMap("sort", "-created"));

        pager = service.buildPager(cmd, "created", SortModel.SortDirection.ASC);
        Assertions.assertEquals(0, pager.getPageNumber());
        Assertions.assertEquals(10, pager.getPageSize());
        Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "created"), pager.getSort());

        cmd = new APICommand("id", "v1");
        cmd.params(toMap("sort", "created", "page", 5, "per_page", 50));

        pager = service.buildPager(cmd, "created", SortModel.SortDirection.DESC);
        Assertions.assertEquals(5, pager.getPageNumber());
        Assertions.assertEquals(50, pager.getPageSize());
        Assertions.assertEquals(Sort.by(Sort.Direction.ASC, "created"), pager.getSort());

        // A special case - we use only allowed sorting field
        cmd = new APICommand("id", "v1");
        cmd.params(toMap("sort", "modified", "page", 5, "per_page", 50));

        pager = service.buildPager(cmd, "created", SortModel.SortDirection.DESC);
        Assertions.assertEquals(5, pager.getPageNumber());
        Assertions.assertEquals(50, pager.getPageSize());
        Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "created"), pager.getSort());
    }
}
