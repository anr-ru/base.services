/**
 * 
 */
package ru.anr.base.samples.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.Authentication;

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

    /**
     * Ref to parent
     */
    private Samples parent;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accessible(Authentication token, Object permission) {

        return this.getName() == null || safeEquals(permission, this.getName());
    }

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
     * @return the parent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parent")
    public Samples getParent() {

        return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(Samples parent) {

        this.parent = parent;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {

        this.name = name;
    }
}
