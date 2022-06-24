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
package ru.anr.base.domain;

import ru.anr.base.BaseParent;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

/**
 * A converter for {@link ZonedDateTime} objects and standard SQL timestamps.
 *
 * @author Alexey Romanchuk
 * @created Nov 5, 2014
 */

@Converter(autoApply = true)
public class LocalDateTimePersistenceConverter extends BaseParent
        implements AttributeConverter<ZonedDateTime, Timestamp> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime entity) {
        return nullSafe(entity, e -> Timestamp.valueOf(e.toLocalDateTime())).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp database) {
        return nullSafe(database, d -> ZonedDateTime.of(d.toLocalDateTime(), DEFAULT_TIMEZONE)).orElse(null);
    }
}
