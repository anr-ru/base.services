/**
 * 
 */
package ru.anr.base.samples.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.security.core.Authentication;

import ru.anr.base.domain.BaseEntity;

/**
 * Test entity with states
 *
 *
 * @author Alexey Romanchuk
 * @created Jun 3, 2015
 *
 */
@Entity
@Table(name = "sample_state")
public class SampleState extends BaseEntity {

    /**
     * The serial ID
     */
    private static final long serialVersionUID = -3408781434856281628L;

    /**
     * Default
     */
    public SampleState() {

        putStates(TestStates.A, TestStates.B); // this transition only
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accessible(Authentication token, Object permission) {

        boolean access = true;
        if (safeEquals(permission, "state")) {
            access = !safeEquals(getState(), TestStates.B.name());
        }
        return access;
    }
}
