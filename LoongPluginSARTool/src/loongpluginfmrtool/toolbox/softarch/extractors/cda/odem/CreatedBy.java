//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.28 at 11:33:10 PM PDT 
//


package loongpluginfmrtool.toolbox.softarch.extractors.cda.odem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "exporter",
    "provider"
})
@XmlRootElement(name = "created-by")
public class CreatedBy {

    @XmlElement(required = true)
    protected Exporter exporter;
    @XmlElement(required = true)
    protected String provider;

    /**
     * Gets the value of the exporter property.
     * 
     * @return
     *     possible object is
     *     {@link Exporter }
     *     
     */
    public Exporter getExporter() {
        return exporter;
    }

    /**
     * Sets the value of the exporter property.
     * 
     * @param value
     *     allowed object is
     *     {@link Exporter }
     *     
     */
    public void setExporter(Exporter value) {
        this.exporter = value;
    }

    /**
     * Gets the value of the provider property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Sets the value of the provider property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvider(String value) {
        this.provider = value;
    }

}
