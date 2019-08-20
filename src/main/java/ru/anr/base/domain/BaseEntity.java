/*
 * Copyright 2014 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package ru.anr.base.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ru.anr.base.BaseParent;
import ru.anr.base.dao.BaseRepositoryImpl;

/**
 * A parent for all entities. It include a definition of the ID column,
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
public class BaseEntity extends BaseParent implements Serializable, Accessible {

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
    private Integer version;

    /**
     * Time of creation
     */
    private Calendar created;

    /**
     * Time of modification
     */
    private Calendar modified;

    /**
     * The state
     */
    private String state;

    /**
     * The time when the state has been changed
     */
    private Calendar stateChanged;

    /**
     * Performing pre-create actions
     */
    @PrePersist
    public void prePersist() {

        setCreated(GregorianCalendar.from(now()));

    }

    /**
     * Performing pre-update actions
     */
    @PreUpdate
    public void preUpdate() {

        setModified(GregorianCalendar.from(now()));
    }

    /**
     * Returns true if the specified state is equals to the current
     * 
     * @param s
     *            State to compare
     * @return true if states are equals
     */
    public boolean hasState(String s) {

        return safeEquals(s, getState());
    }

    /**
     * Changing the state of this object
     * 
     * @param newState
     *            A new state
     * @return Old state (being changed)
     */
    public String changeState(String newState) {

        String oldState = getState();

        if (!this.hasState(newState)) {
            setState(newState);
            setStateChanged(GregorianCalendar.from(now()));
        }
        return oldState;
    }

    /**
     * A state diagram of the object
     */
    private final MultiValueMap<String, String> transitions = new LinkedMultiValueMap<>();

    /**
     * Puts a state transition to the map. Just a convenient short-cut method.
     * 
     * @param key
     *            A key
     * @param states
     *            A list of target states
     * @param <S>
     *            Type of the states enumeration
     */
    protected <S extends Enum<S>> void putStates(S key, @SuppressWarnings("unchecked") S... states) {

        transitions.put(key.name(), list(list(states).stream().map(s -> s.name())));
    }

    /**
     * Changing the state of the object. Enumeration-based version
     * 
     * @param newState
     *            A new state
     * @return The previous state (can be null)
     * 
     * @param <S>
     *            A type of state enumeration
     */
    @SuppressWarnings("unchecked")
    public <S extends Enum<S>> S changeState(S newState) {

        S rs = null;
        if (newState != null) {
            String s = changeState(newState.name());
            rs = (s == null) ? null : (S) Enum.valueOf(newState.getClass(), s);
        }
        return rs;
    }

    /**
     * Checks the object's state to be in the states array
     * 
     * @param states
     *            State to compare
     * @return true, if one of the states is the current state
     * @param <S>
     *            Type of the state enumeration
     */
    public <S extends Enum<S>> boolean hasState(@SuppressWarnings("unchecked") S... states) {

        return list(states).stream().anyMatch(s -> safeEquals(getState(), s.name()));
    }

    /**
     * Checks the object's state to be in the states array (string version)
     * 
     * @param states
     *            State to compare
     * @return true, if one of the states is the current state
     */
    public boolean hasState(String... states) {

        return list(states).stream().anyMatch(s -> safeEquals(getState(), s));
    }

    /**
     * Returns a map with available transitions
     * 
     * @return The map
     */
    @Transient
    public MultiValueMap<String, String> getTransitionsMap() {

        return transitions; // Empty by default
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
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getCreated() {

        return cloneObject(created);
    }

    /**
     * @param created
     *            the created to set
     */
    public void setCreated(Calendar created) {

        this.created = cloneObject(created);
    }

    /**
     * @return the modified
     */
    @Column(name = "i_modified")
    public Calendar getModified() {

        return cloneObject(modified);
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
    public Calendar getStateChanged() {

        return cloneObject(stateChanged);
    }

    /**
     * @param stateChanged
     *            the stateChanged to set
     */
    public void setStateChanged(Calendar stateChanged) {

        this.stateChanged = cloneObject(stateChanged);
    }

    /**
     * @param modified
     *            the modified to set
     */
    public void setModified(Calendar modified) {

        this.modified = cloneObject(modified);
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
        if (getClass() != (obj instanceof BaseEntity ? BaseRepositoryImpl.entityClass((BaseEntity) obj)
                : obj.getClass())) {
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
