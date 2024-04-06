/*
 * Copyright 2014-2024 the original author or authors.
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.anr.base.BaseParent;
import ru.anr.base.dao.EntityUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * A parent for all entities. It includes a definition of the ID column, an optimistic version column
 * {@link #equals(Object)} and {@link #hashCode()} operations and some useful fields that can
 * be applicable for almost all entities.
 *
 * @author Alexey Romanchuk
 * @created Nov 5, 2014
 */
@MappedSuperclass
@DynamicUpdate
public class BaseEntity extends BaseParent implements Serializable, Accessible {

    private static final long serialVersionUID = 6325902826050618344L;

    /**
     * A GUID linked with each object. Before saving, it works as the identifier
     */
    private final String guid = UUID.randomUUID().toString();

    /**
     * The primary key
     */
    private Long id;

    /**
     * Version column for optimistic locks
     */
    private Integer version;

    /**
     * Time of creation
     */
    private ZonedDateTime created;

    /**
     * Time of modification
     */
    private ZonedDateTime modified;

    /**
     * The state
     */
    private String state;

    /**
     * The time when the state has been changed
     */
    private ZonedDateTime stateChanged;

    /**
     * The information on the last transition to the state or object updates errors, warning, etc.
     */
    private String stateInfo;

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
     * Returns true if the specified state is equals to the current
     *
     * @param s State to compare
     * @return true if states are equals
     */
    public boolean hasState(String s) {
        return safeEquals(s, getState());
    }

    /**
     * Changes the state of this object. If the object already has this state,
     * the state is not changed more.
     *
     * @param newState The new state to set
     * @return The previous state
     */
    public String changeState(String newState) {

        String oldState = getState();

        if (!this.hasState(newState)) {
            setState(newState);
            setStateChanged(now());
        }
        return oldState;
    }

    /**
     * The state diagram of the object. It defined the transition directions between different
     * states allowing to control the state flow.
     */
    private final MultiValueMap<String, String> transitions = new LinkedMultiValueMap<>();

    /**
     * Puts a state transition to the map. Just a convenient short-cut method.
     *
     * @param key    The 'from' state code
     * @param states The list of possible target states.
     * @param <S>    Type of the states enumeration
     */
    @SafeVarargs
    protected final <S extends Enum<S>> void putStates(S key, S... states) {
        transitions.put(key.name(), toStr(list(states)));
    }

    /**
     * Changes the state of the object. A enumeration-based version
     *
     * @param newState A new state
     * @param <S>      A type of state enumeration
     * @return The previous state (can be null)
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
     * @param states State to compare
     * @param <S>    Type of the state enumeration
     * @return true, if one of the states is the current state
     */
    @SafeVarargs
    public final <S extends Enum<S>> boolean hasState(S... states) {
        return list(states).stream().anyMatch(s -> safeEquals(getState(), s.name()));
    }

    /**
     * Checks the object's state to be in the states array (string version)
     *
     * @param states State to compare
     * @return true, if one of the states is the current state
     */
    public boolean hasState(String... states) {
        return list(states).stream().anyMatch(s -> safeEquals(getState(), s));
    }

    /**
     * Returns the map with all available transitions
     *
     * @return The map
     */
    @Transient
    public MultiValueMap<String, String> getTransitionsMap() {
        return transitions; // Empty by default
    }

    ///////////////////////////////////////////////////////////////////////////
    ///// getters/setters
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", initialValue = 1000, allocationSize = 50)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
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
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return the created
     */
    @Column(name = "i_created")
    public ZonedDateTime getCreated() {
        return cloneObject(created);
    }

    /**
     * @param created the created to set
     */
    public void setCreated(ZonedDateTime created) {
        this.created = cloneObject(created);
    }

    /**
     * @return the modified
     */
    @Column(name = "i_modified")
    public ZonedDateTime getModified() {
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
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the stateChanged
     */
    @Column(name = "i_state_changed")
    public ZonedDateTime getStateChanged() {
        return cloneObject(stateChanged);
    }

    /**
     * @param stateChanged the stateChanged to set
     */
    public void setStateChanged(ZonedDateTime stateChanged) {
        this.stateChanged = cloneObject(stateChanged);
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(ZonedDateTime modified) {
        this.modified = cloneObject(modified);
    }

    @Column(name = "state_info", length = 512)
    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    ///////////////////////////////////////////////////////////////////////////
    ///// some functions
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the effective ID. We use some random GUID before the object stored. This allows to distinct
     * new unsaved objects.
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
        if (getClass() != (obj instanceof BaseEntity ? EntityUtils.entityClass((BaseEntity) obj)
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

        ToStringBuilder b = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", internalId());

        if (getVersion() != null) {
            b.append("v", getVersion());
        }
        if (getState() != null) {
            b.append("s", getState());
        }
        return b.toString();
    }
}
