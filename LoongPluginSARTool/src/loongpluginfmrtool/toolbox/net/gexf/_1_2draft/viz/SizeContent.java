//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.09 at 10:24:49 AM PDT 
//


package loongpluginfmrtool.toolbox.net.gexf._1_2draft.viz;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import loongpluginfmrtool.toolbox.net.gexf._1.SpellsContent;


/**
 * <p>Java class for size-content complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="size-content">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.gexf.net/1.2draft/viz}spells" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="value" use="required" type="{http://www.gexf.net/1.2draft/viz}size-type" />
 *       &lt;attribute name="start" type="{http://www.gexf.net/1.2draft}time-type" />
 *       &lt;attribute name="startopen" type="{http://www.gexf.net/1.2draft}time-type" />
 *       &lt;attribute name="end" type="{http://www.gexf.net/1.2draft}time-type" />
 *       &lt;attribute name="endopen" type="{http://www.gexf.net/1.2draft}time-type" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "size-content", propOrder = {
    "spells"
})
public class SizeContent {

    protected SpellsContent spells;
    @XmlAttribute(required = true)
    protected float value;
    @XmlAttribute
    protected String start;
    @XmlAttribute
    protected String startopen;
    @XmlAttribute
    protected String end;
    @XmlAttribute
    protected String endopen;

    /**
     * Gets the value of the spells property.
     * 
     * @return
     *     possible object is
     *     {@link SpellsContent }
     *     
     */
    public SpellsContent getSpells() {
        return spells;
    }

    /**
     * Sets the value of the spells property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpellsContent }
     *     
     */
    public void setSpells(SpellsContent value) {
        this.spells = value;
    }

    /**
     * Gets the value of the value property.
     * 
     */
    public float getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Gets the value of the start property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStart() {
        return start;
    }

    /**
     * Sets the value of the start property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStart(String value) {
        this.start = value;
    }

    /**
     * Gets the value of the startopen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartopen() {
        return startopen;
    }

    /**
     * Sets the value of the startopen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartopen(String value) {
        this.startopen = value;
    }

    /**
     * Gets the value of the end property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnd() {
        return end;
    }

    /**
     * Sets the value of the end property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnd(String value) {
        this.end = value;
    }

    /**
     * Gets the value of the endopen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndopen() {
        return endopen;
    }

    /**
     * Sets the value of the endopen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndopen(String value) {
        this.endopen = value;
    }

}
