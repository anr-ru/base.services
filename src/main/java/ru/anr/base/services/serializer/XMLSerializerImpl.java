/**
 * 
 */
package ru.anr.base.services.serializer;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

/**
 * XML configuration.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 8, 2014
 *
 */

public class XMLSerializerImpl extends AbstractSerializerImpl {

    /**
     * Constructor
     */
    public XMLSerializerImpl() {

        super(new XmlMapper());

        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(true);

        mapper.registerModule(module);

        ((XmlMapper) mapper).configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    }
}
