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
package ru.anr.base.domain.api.models;

import java.util.Calendar;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAttribute;

import ru.anr.base.BaseParent;
import ru.anr.base.domain.BaseEntity;
import ru.anr.base.services.BaseService;

/**
 * A base model for all objects (contains id, state, created and other basic
 * properties).
 *
 *
 * @author Alexey Romanchuk
 * @created Jun 8, 2015
 *
 */

public class BaseObjectModel extends RequestModel {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = -752471039342963751L;

    /**
     * The identifier of the object
     */
    private Long id;

    /**
     * The state attribute
     */
    private String state;

    /**
     * Time of creation
     */
    private Calendar created;

    /**
     * Time of latest modification
     */
    private Calendar modified;

    /**
     * Time of latest state transitions
     */
    private Calendar stateChanged;

    /**
     * Default
     */
    public BaseObjectModel() {

        super();
    }

    /**
     * Null-safe initialization
     * 
     * @param o
     *            An object
     * @param clazz
     *            The class of the object
     * @return The old object or a new created
     * @param <S>
     *            Object type
     */
    public static <S> S nullSafe(S o, Class<S> clazz) {

        return Optional.ofNullable(o).orElse(BaseParent.inst(clazz, new Class<?>[]{}));
    }

    /**
     * Construction by the given {@link BaseEntity}
     * 
     * @param object
     *            The object
     */
    public BaseObjectModel(BaseEntity object) {

        this();

        BaseEntity o = nullSafe(object, BaseEntity.class);

        this.id = o.getId();
        this.created = o.getCreated();
        this.modified = o.getModified();
        this.state = o.getState();
        this.stateChanged = o.getStateChanged();
    }

    /**
     * Copy constructor
     * 
     * @param model
     *            The original model
     */
    public BaseObjectModel(BaseObjectModel model) {

        this();

        BaseObjectModel mo = nullSafe(model, BaseObjectModel.class);

        this.id = mo.getId();
        this.created = mo.getCreated();
        this.modified = mo.getModified();
        this.state = mo.getState();
        this.stateChanged = mo.getStateChanged();
    }

    /**
     * The common entry point for fill data from the model to the given object.
     * 
     * @param object
     *            The object to apply
     * @param service
     *            The base service to extracts some additional data or other
     *            services
     */
    public void fill(BaseEntity object, BaseService service) {

    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the id
     */
    @XmlAttribute(name = "id")
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
     * @return the state
     */
    @XmlAttribute(name = "state")
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
     * @return the created
     */
    @XmlAttribute(name = "created")
    public Calendar getCreated() {

        return created;
    }

    /**
     * @param created
     *            the created to set
     */
    public void setCreated(Calendar created) {

        this.created = created;
    }

    /**
     * @return the stateChanged
     */
    @XmlAttribute(name = "state_changed")
    public Calendar getStateChanged() {

        return stateChanged;
    }

    /**
     * @param stateChanged
     *            the stateChanged to set
     */
    public void setStateChanged(Calendar stateChanged) {

        this.stateChanged = stateChanged;
    }

    /**
     * @return the modified
     */
    @XmlAttribute(name = "modified")
    public Calendar getModified() {

        return modified;
    }

    /**
     * @param modified
     *            the modified to set
     */
    public void setModified(Calendar modified) {

        this.modified = modified;
    }

}
