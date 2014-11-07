/**
 * 
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
    public Timestamp convertToDatabaseColumn(ZonedDateTime entityValue) {

        return Timestamp.valueOf(entityValue.toLocalDateTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp databaseValue) {

        return ZonedDateTime.of(databaseValue.toLocalDateTime(), DEFAULT_TIMEZONE);
    }
}
