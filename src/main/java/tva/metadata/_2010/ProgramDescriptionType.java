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
 * <p>Java class for ProgramDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProgramDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProgramInformationTable" type="{urn:tva:metadata:2010}ProgramInformationTableType" minOccurs="0"/>
 *         &lt;element name="GroupInformationTable" type="{urn:tva:metadata:2010}GroupInformationTableType" minOccurs="0"/>
 *         &lt;element name="ProgramLocationTable" type="{urn:tva:metadata:2010}ProgramLocationTableType" minOccurs="0"/>
 *         &lt;element name="ServiceInformationTable" type="{urn:tva:metadata:2010}ServiceInformationTableType" minOccurs="0"/>
 *         &lt;element name="CreditsInformationTable" type="{urn:tva:metadata:2010}CreditsInformationTableType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProgramDescriptionType", propOrder = {
    "programInformationTable",
    "groupInformationTable",
    "programLocationTable",
    "serviceInformationTable",
    "creditsInformationTable"
})
public class ProgramDescriptionType {

    @XmlElement(name = "ProgramInformationTable")
    protected ProgramInformationTableType programInformationTable;
    @XmlElement(name = "GroupInformationTable")
    protected GroupInformationTableType groupInformationTable;
    @XmlElement(name = "ProgramLocationTable")
    protected ProgramLocationTableType programLocationTable;
    @XmlElement(name = "ServiceInformationTable")
    protected ServiceInformationTableType serviceInformationTable;
    @XmlElement(name = "CreditsInformationTable")
    protected CreditsInformationTableType creditsInformationTable;

    /**
     * Gets the value of the programInformationTable property.
     * 
     * @return
     *     possible object is
     *     {@link ProgramInformationTableType }
     *     
     */
    public ProgramInformationTableType getProgramInformationTable() {
        return programInformationTable;
    }

    /**
     * Sets the value of the programInformationTable property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProgramInformationTableType }
     *     
     */
    public void setProgramInformationTable(ProgramInformationTableType value) {
        this.programInformationTable = value;
    }

    /**
     * Gets the value of the groupInformationTable property.
     * 
     * @return
     *     possible object is
     *     {@link GroupInformationTableType }
     *     
     */
    public GroupInformationTableType getGroupInformationTable() {
        return groupInformationTable;
    }

    /**
     * Sets the value of the groupInformationTable property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupInformationTableType }
     *     
     */
    public void setGroupInformationTable(GroupInformationTableType value) {
        this.groupInformationTable = value;
    }

    /**
     * Gets the value of the programLocationTable property.
     * 
     * @return
     *     possible object is
     *     {@link ProgramLocationTableType }
     *     
     */
    public ProgramLocationTableType getProgramLocationTable() {
        return programLocationTable;
    }

    /**
     * Sets the value of the programLocationTable property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProgramLocationTableType }
     *     
     */
    public void setProgramLocationTable(ProgramLocationTableType value) {
        this.programLocationTable = value;
    }

    /**
     * Gets the value of the serviceInformationTable property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceInformationTableType }
     *     
     */
    public ServiceInformationTableType getServiceInformationTable() {
        return serviceInformationTable;
    }

    /**
     * Sets the value of the serviceInformationTable property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceInformationTableType }
     *     
     */
    public void setServiceInformationTable(ServiceInformationTableType value) {
        this.serviceInformationTable = value;
    }

    /**
     * Gets the value of the creditsInformationTable property.
     * 
     * @return
     *     possible object is
     *     {@link CreditsInformationTableType }
     *     
     */
    public CreditsInformationTableType getCreditsInformationTable() {
        return creditsInformationTable;
    }

    /**
     * Sets the value of the creditsInformationTable property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditsInformationTableType }
     *     
     */
    public void setCreditsInformationTable(CreditsInformationTableType value) {
        this.creditsInformationTable = value;
    }

}