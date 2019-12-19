/**
 * 
 */
package ru.anr.base.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ru.anr.base.dao.repository.BaseRepository;
import ru.anr.base.domain.BaseEntity;

/**
 * A secured implementation of the page with a filtration on the loaded content
 * according to the Spring Security rules defined for the object type.
 *
 *
 * @author Alexey Romanchuk
 * @created Dec 18, 2019
 *
 * @param <T>
 *            The type of items
 */

public class SecuredPageImpl<T extends BaseEntity> extends PageImpl<T> {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 6120568128949835771L;

    /**
     * Default constructor with filtration of the data
     * 
     * @param repository
     *            Some repository bean
     * @param pager
     *            The original pager
     * @param page
     *            The page object of the result set to secure
     */
    public SecuredPageImpl(BaseRepository<?> repository, Pageable pager, Page<T> page) {

        super(repository.filter(page), pager, page.getTotalElements());
    }
}
