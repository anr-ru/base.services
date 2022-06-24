package ru.anr.base.samples.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;

/**
 * Description ...
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 */
@XmlRootElement(name = "model")
public class Model {
    /**
     * Some field
     */
    @XmlAttribute(name = "field")
    public String field;

    /**
     * Time field
     */
    @XmlAttribute(name = "time")
    public ZonedDateTime time;

    @XmlAttribute(name = "calendar")
    public Calendar calendar;

    /**
     * Submodels
     */
    @XmlElementWrapper(name = "subs")
    @XmlElement(name = "sub")
    public List<SubModel> subs;

    /**
     * Decimal
     */
    @XmlAttribute(name = "sum")
    public BigDecimal sum;

    ///////////////////////////////////////////////////////////////////////////
    ///// getters/setters
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
