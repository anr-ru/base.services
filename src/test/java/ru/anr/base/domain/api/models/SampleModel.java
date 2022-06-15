package ru.anr.base.domain.api.models;

import ru.anr.base.samples.domain.Samples;

/**
 * @author Alexey Romanchuk
 * @created Jun 08, 2022
 */
public class SampleModel extends BaseObjectModel {

    public String name;

    public SampleModel(Samples s) {
        super(s);

        Samples sx = nullSafe(s, Samples.class); // prevent null objects
        this.name = sx.getName();
    }

    public SampleModel() {
        this(null);
    }
}
