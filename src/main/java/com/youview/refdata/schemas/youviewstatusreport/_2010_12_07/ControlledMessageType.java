//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.10.30 at 10:17:38 AM GMT 
//


package com.youview.refdata.schemas.youviewstatusreport._2010_12_07;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import tva.mpeg7._2008.TextualType;


/**
 * A message.
 * 
 * <p>Java class for ControlledMessageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ControlledMessageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Comment" type="{urn:tva:mpeg7:2008}TextualType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="severity" use="required" type="{http://refdata.youview.com/schemas/YouViewStatusReport/2010-12-07}SeverityType" />
 *       &lt;attribute name="location" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="reasonCode" use="required" type="{urn:tva:mpeg7:2008}termReferenceType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ControlledMessageType", propOrder = {
    "comment"
})
public class ControlledMessageType {

    @XmlElement(name = "Comment", required = true)
    protected TextualType comment;
    @XmlAttribute(name = "severity", required = true)
    protected SeverityType severity;
    @XmlAttribute(name = "location", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String location;
    @XmlAttribute(name = "reasonCode", required = true)
    protected String reasonCode;

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link TextualType }
     *     
     */
    public TextualType getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextualType }
     *     
     */
    public void setComment(TextualType value) {
        this.comment = value;
    }

    /**
     * Gets the value of the severity property.
     * 
     * @return
     *     possible object is
     *     {@link SeverityType }
     *     
     */
    public SeverityType getSeverity() {
        return severity;
    }

    /**
     * Sets the value of the severity property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeverityType }
     *     
     */
    public void setSeverity(SeverityType value) {
        this.severity = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Gets the value of the reasonCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReasonCode() {
        return reasonCode;
    }

    /**
     * Sets the value of the reasonCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonCode(String value) {
        this.reasonCode = value;
    }

}