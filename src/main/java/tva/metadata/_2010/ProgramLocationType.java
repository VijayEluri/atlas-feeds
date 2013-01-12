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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProgramLocationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProgramLocationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Program" type="{urn:tva:metadata:2010}CRIDRefType"/>
 *         &lt;element name="ProgramURL" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="InstanceMetadataId" type="{urn:tva:metadata:2010}InstanceMetadataIdType"/>
 *         &lt;element name="InstanceDescription" type="{urn:tva:metadata:2010}InstanceDescriptionType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProgramLocationType", propOrder = {
    "program",
    "programURL",
    "instanceMetadataId",
    "instanceDescription"
})
@XmlSeeAlso({
    PushDownloadType.class,
    ScheduleEventType.class,
    OnDemandProgramType.class
})
public abstract class ProgramLocationType {

    @XmlElement(name = "Program", required = true)
    protected CRIDRefType program;
    @XmlElement(name = "ProgramURL")
    @XmlSchemaType(name = "anyURI")
    protected String programURL;
    @XmlElement(name = "InstanceMetadataId", required = true)
    protected String instanceMetadataId;
    @XmlElement(name = "InstanceDescription", required = true)
    protected InstanceDescriptionType instanceDescription;

    /**
     * Gets the value of the program property.
     * 
     * @return
     *     possible object is
     *     {@link CRIDRefType }
     *     
     */
    public CRIDRefType getProgram() {
        return program;
    }

    /**
     * Sets the value of the program property.
     * 
     * @param value
     *     allowed object is
     *     {@link CRIDRefType }
     *     
     */
    public void setProgram(CRIDRefType value) {
        this.program = value;
    }

    /**
     * Gets the value of the programURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramURL() {
        return programURL;
    }

    /**
     * Sets the value of the programURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramURL(String value) {
        this.programURL = value;
    }

    /**
     * Gets the value of the instanceMetadataId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceMetadataId() {
        return instanceMetadataId;
    }

    /**
     * Sets the value of the instanceMetadataId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceMetadataId(String value) {
        this.instanceMetadataId = value;
    }

    /**
     * Gets the value of the instanceDescription property.
     * 
     * @return
     *     possible object is
     *     {@link InstanceDescriptionType }
     *     
     */
    public InstanceDescriptionType getInstanceDescription() {
        return instanceDescription;
    }

    /**
     * Sets the value of the instanceDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstanceDescriptionType }
     *     
     */
    public void setInstanceDescription(InstanceDescriptionType value) {
        this.instanceDescription = value;
    }

}