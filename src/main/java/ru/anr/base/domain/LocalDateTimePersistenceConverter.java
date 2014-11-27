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

import java.sql.Timestamp;
import java.time.ZonedDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import ru.anr.base.BaseParent;

/**
 * A converter for {@link ZonedDateTime} object and standard SQL Timestamp.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 5, 2014
 *
 */

@Converter(autoApply = true)
public class LocalDateTimePersistenceConverter extends BaseParent implements
        AttributeConverter<ZonedDateTime, Timestamp> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime entity) {

        return (entity == null) ? null : Timestamp.valueOf(entity.toLocalDateTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp database) {

        return (database == null) ? null : ZonedDateTime.of(database.toLocalDateTime(), DEFAULT_TIMEZONE);
    }
}
