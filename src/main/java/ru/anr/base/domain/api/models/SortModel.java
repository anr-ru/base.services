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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Basic Sort operation model.
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 */

public class SortModel implements Serializable {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 394767569835000862L;

    /**
     * Name of field
     */
    private String field;

    /**
     * {@link SortDirection}
     */
    private SortDirection direction;

    /**
     * Constructor
     *
     * @param field     Name of field
     * @param direction Sorting direction
     */
    public SortModel(String field, SortDirection direction) {
        this.field = field;
        this.direction = direction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * ASC or DESC directions
     */
    public enum SortDirection {
        /**
         * ASC
         */
        ASC,
        /**
         * DESC
         */
        DESC
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the direction
     */
    public SortDirection getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }
}
