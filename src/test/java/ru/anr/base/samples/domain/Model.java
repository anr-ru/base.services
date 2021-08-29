/**
 *
 */
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
 *
 * @author Alexey Romanchuk
 * @created Nov 7, 2014
 *
 */

@XmlRootElement(name = "model")
public class Model {

    /**
     * Some field
     */
    private String field;

    /**
     * Time field
     */
    private ZonedDateTime time;

    private Calendar calendar;

    /**
     * Submodels
     */
    private List<SubModel> subs;

    /**
     * Decimal
     */
    private BigDecimal sum;

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the time field
     */
    @XmlAttribute(name = "time")
    public ZonedDateTime getTime() {

        return time;
    }

    @XmlAttribute(name = "calendar")
    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * @return the sum
     */
    @XmlAttribute(name = "sum")
    public BigDecimal getSum() {

        return sum;
    }

    /**
     * @param sum
     *            the sum to set
     */
    public void setSum(BigDecimal sum) {

        this.sum = sum;
    }

    /**
     * @param time
     *            the time field to set
     */
    public void setTime(ZonedDateTime time) {

        this.time = time;
    }

    /**
     * @return the field
     */
    @XmlAttribute(name = "field")
    public String getField() {

        return field;
    }

    /**
     * @return the subs
     */
    @XmlElementWrapper(name = "subs")
    @XmlElement(name = "sub")
    public List<SubModel> getSubs() {

        return subs;
    }

    /**
     * @param subs
     *            the subs to set
     */
    public void setSubs(List<SubModel> subs) {

        this.subs = subs;
    }

    /**
     * @param field
     *            the field to set
     */
    public void setField(String field) {

        this.field = field;
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
    public int hashCode() {

        return HashCodeBuilder.reflectionHashCode(this);
    }

}
