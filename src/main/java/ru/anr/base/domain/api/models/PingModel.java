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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Ping model. Has one attribute. Used for tests.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 10, 2014
 *
 */
@XmlRootElement(name = "ping")
public class PingModel extends RequestModel {

    /**
     * Serial ID
     */
    private static final long serialVersionUID = 6040666167936904587L;

    /**
     * Some value
     */
    private String value;

    /**
     * @return the value
     */
    @XmlAttribute(name = "value")
    public String getValue() {

        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {

        this.value = value;
    }

}
