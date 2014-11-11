/**
 * 
 */
package ru.anr.base.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.DynamicUpdate;

import ru.anr.base.BaseParent;

/**
 * A parent for all entities. It include a definition of ID column,
 * {@link #equals(Object)} and {@link #hashCode()} operations.
 *
 * 
 * Added some basic database columns to avoid choosing and changing valid parent
 * entity.
 *
 * @author Alexey Romanchuk
 * @created Nov 5, 2014
 *
 */
@MappedSuperclass
@DynamicUpdate
public class BaseEntity extends BaseParent implements Serializable {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 6325902826050618344L;

    /**
     * A GUID linked with each object. Before saving it works as a identifier
     */
    private final String guid = UUID.randomUUID().toString();

    /**
     * Primary key
     */
    private Long id;

    /**
     * Version column for optimistic locks
     */
    private Integer version = 0;

    /**
     * Time of creation
     */
    private ZonedDateTime created;

    /**
     * Time of modification
     */
    private ZonedDateTime modified;

    /**
     * Name of object state
     */
    private String state;

    /**
     * The time when a state changed
     */
    private ZonedDateTime stateChanged;

    /**
     * Performing pre-create actions
     */
    @PrePersist
    public void prePersist() {

        setCreated(now());

    }

    /**
     * Performing pre-update actions
     */
    @PreUpdate
    public void preUpdate() {

        setModified(now());
    }

    /**
     * Changing a state of this object
     * 
     * @param newState
     *            New state name
     * @return Old state (being changed)
     */
    public String changeState(String newState) {

        String oldState = getState();

        setState(newState);
        setStateChanged(now());

        return oldState;
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hseq")
    @SequenceGenerator(name = "hseq", allocationSize = 50)
    @Column(name = "id")
    public Long getId() {

        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {

        this.id = id;
    }

    /**
     * @return the version
     */
    @Version
    @Column(name = "i_version")
    public Integer getVersion() {

        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(Integer version) {

        this.version = version;
    }

    /**
     * @return the created
     */
    @Column(name = "i_created")
    public ZonedDateTime getCreated() {

        return created;
    }

    /**
     * @param created
     *            the created to set
     */
    public void setCreated(ZonedDateTime created) {

        this.created = created;
    }

    /**
     * @return the modified
     */
    @Column(name = "i_modified")
    public ZonedDateTime getModified() {

        return modified;
    }

    /**
     * @return the state
     */
    @Column(name = "state", length = 32)
    public String getState() {

        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(String state) {

        this.state = state;
    }

    /**
     * @return the stateChanged
     */
    @Column(name = "i_state_changed")
    public ZonedDateTime getStateChanged() {

        return stateChanged;
    }

    /**
     * @param stateChanged
     *            the stateChanged to set
     */
    public void setStateChanged(ZonedDateTime stateChanged) {

        this.stateChanged = stateChanged;
    }

    /**
     * @param modified
     *            the modified to set
     */
    public void setModified(ZonedDateTime modified) {

        this.modified = modified;
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// some functions
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Getting an effective identifier
     * 
     * @return ID or GUID if ID is null
     */
    protected final Object internalId() {

        return (getId() == null) ? guid : getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return new HashCodeBuilder().append(internalId()).hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.OnlyOneReturn")
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        BaseEntity rhs = (BaseEntity) obj;
        return new EqualsBuilder().append(internalId(), rhs.internalId()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        ToStringBuilder b = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).append("id", internalId());

        if (getVersion() != null) {
            b.append("v", getVersion());
        }
        if (getState() != null) {
            b.append("s", getState());
        }
        return b.toString();
    }
}