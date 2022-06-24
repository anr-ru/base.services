/*
 * Copyright 2014-2022 the original author or authors.
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

import ru.anr.base.domain.BaseEntity;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Calendar;

/**
 * A base model for all objects (contains id, state, created and other basic
 * properties).
 *
 * @author Alexey Romanchuk
 * @created Jun 8, 2015
 */
public class BaseObjectModel extends RequestModel {

    private static final long serialVersionUID = -752471039342963751L;

    /**
     * The identifier of the object
     */
    @XmlAttribute(name = "id")
    public Long id;

    /**
     * The state attribute
     */
    @XmlAttribute(name = "state")
    public String state;

    /**
     * Time of creation
     */
    @XmlAttribute(name = "created")
    public Calendar created;

    /**
     * Time of the latest modification
     */
    @XmlAttribute(name = "modified")
    public Calendar modified;

    /**
     * Time of latest state transitions
     */
    @XmlAttribute(name = "state_changed")
    public Calendar stateChanged;

    /**
     * The default constructor.
     */
    public BaseObjectModel() {
        super();
    }


    /**
     * Construction by the given {@link BaseEntity}
     *
     * @param object The object
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
     * @param model The original model
     */
    public BaseObjectModel(BaseObjectModel model) {

        this();

        BaseObjectModel m = nullSafe(model, BaseObjectModel.class);

        this.id = m.id;
        this.created = m.created;
        this.modified = m.modified;
        this.state = m.state;
        this.stateChanged = m.stateChanged;
    }
}
