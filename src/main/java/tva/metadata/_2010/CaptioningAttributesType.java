//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.25 at 10:09:26 AM GMT 
//


package tva.metadata._2010;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CaptioningAttributesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CaptioningAttributesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Coding" type="{urn:tva:metadata:2010}ControlledTermType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CaptioningAttributesType", propOrder = {
    "coding"
})
public class CaptioningAttributesType {

    @XmlElement(name = "Coding")
    protected ControlledTermType coding;

    /**
     * Gets the value of the coding property.
     * 
     * @return
     *     possible object is
     *     {@link ControlledTermType }
     *     
     */
    public ControlledTermType getCoding() {
        return coding;
    }

    /**
     * Sets the value of the coding property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControlledTermType }
     *     
     */
    public void setCoding(ControlledTermType value) {
        this.coding = value;
    }

}
