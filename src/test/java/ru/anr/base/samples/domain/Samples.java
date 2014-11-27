/**
 * 
 */
package ru.anr.base.samples.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import ru.anr.base.domain.BaseEntity;

/**
 * A sample entity.
 * 
 * 
 * @author Alexey Romanchuk
 * @created Oct 30, 2014
 * 
 */
@Entity
@Table(name = "samples")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "samples")
public class Samples extends BaseEntity {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = -8838120036727868758L;

    /**
     * Name
     */
    private String name;

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the name
     */
    @Column(name = "name")
    public String getName() {

        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {

        this.name = name;
    }
}
