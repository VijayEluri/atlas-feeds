//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.25 at 10:09:26 AM GMT 
//


package tva.mpeg7._2008;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TimeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="TimePoint" type="{urn:tva:mpeg7:2008}timePointType"/>
 *           &lt;element name="RelTimePoint" type="{urn:tva:mpeg7:2008}RelTimePointType"/>
 *           &lt;element name="RelIncrTimePoint" type="{urn:tva:mpeg7:2008}RelIncrTimePointType"/>
 *         &lt;/choice>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="Duration" type="{urn:tva:mpeg7:2008}durationType"/>
 *           &lt;element name="IncrDuration" type="{urn:tva:mpeg7:2008}IncrDurationType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeType", propOrder = {
    "timePoint",
    "relTimePoint",
    "relIncrTimePoint",
    "duration",
    "incrDuration"
})
@XmlSeeAlso({
    tva.mpeg7._2008.CreationPreferencesType.DatePeriod.class,
    tva.mpeg7._2008.PreferenceConditionType.Time.class,
    tva.mpeg7._2008.SourcePreferencesType.DisseminationDate.class,
    tva.mpeg7._2008.ClassificationPreferencesType.DatePeriod.class
})
public class TimeType {

    @XmlElement(name = "TimePoint")
    protected String timePoint;
    @XmlElement(name = "RelTimePoint")
    protected RelTimePointType relTimePoint;
    @XmlElement(name = "RelIncrTimePoint")
    protected RelIncrTimePointType relIncrTimePoint;
    @XmlElement(name = "Duration")
    protected String duration;
    @XmlElement(name = "IncrDuration")
    protected IncrDurationType incrDuration;

    /**
     * Gets the value of the timePoint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimePoint() {
        return timePoint;
    }

    /**
     * Sets the value of the timePoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimePoint(String value) {
        this.timePoint = value;
    }

    /**
     * Gets the value of the relTimePoint property.
     * 
     * @return
     *     possible object is
     *     {@link RelTimePointType }
     *     
     */
    public RelTimePointType getRelTimePoint() {
        return relTimePoint;
    }

    /**
     * Sets the value of the relTimePoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelTimePointType }
     *     
     */
    public void setRelTimePoint(RelTimePointType value) {
        this.relTimePoint = value;
    }

    /**
     * Gets the value of the relIncrTimePoint property.
     * 
     * @return
     *     possible object is
     *     {@link RelIncrTimePointType }
     *     
     */
    public RelIncrTimePointType getRelIncrTimePoint() {
        return relIncrTimePoint;
    }

    /**
     * Sets the value of the relIncrTimePoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelIncrTimePointType }
     *     
     */
    public void setRelIncrTimePoint(RelIncrTimePointType value) {
        this.relIncrTimePoint = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDuration(String value) {
        this.duration = value;
    }

    /**
     * Gets the value of the incrDuration property.
     * 
     * @return
     *     possible object is
     *     {@link IncrDurationType }
     *     
     */
    public IncrDurationType getIncrDuration() {
        return incrDuration;
    }

    /**
     * Sets the value of the incrDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link IncrDurationType }
     *     
     */
    public void setIncrDuration(IncrDurationType value) {
        this.incrDuration = value;
    }

}
