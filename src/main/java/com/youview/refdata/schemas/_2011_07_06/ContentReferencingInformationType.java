//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.25 at 10:09:26 AM GMT 
//


package com.youview.refdata.schemas._2011_07_06;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContentReferencingInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContentReferencingInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Locator" type="{http://refdata.youview.com/schemas/2011-07-06}LocatorType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="acquire" use="required" type="{http://refdata.youview.com/schemas/2011-07-06}AcquisitionDirectiveType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentReferencingInformationType", propOrder = {
    "locator"
})
public class ContentReferencingInformationType {

    @XmlElement(name = "Locator", required = true)
    protected List<LocatorType> locator;
    @XmlAttribute(required = true)
    protected AcquisitionDirectiveType acquire;

    /**
     * Gets the value of the locator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the locator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocator().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocatorType }
     * 
     * 
     */
    public List<LocatorType> getLocator() {
        if (locator == null) {
            locator = new ArrayList<LocatorType>();
        }
        return this.locator;
    }

    /**
     * Gets the value of the acquire property.
     * 
     * @return
     *     possible object is
     *     {@link AcquisitionDirectiveType }
     *     
     */
    public AcquisitionDirectiveType getAcquire() {
        return acquire;
    }

    /**
     * Sets the value of the acquire property.
     * 
     * @param value
     *     allowed object is
     *     {@link AcquisitionDirectiveType }
     *     
     */
    public void setAcquire(AcquisitionDirectiveType value) {
        this.acquire = value;
    }

}