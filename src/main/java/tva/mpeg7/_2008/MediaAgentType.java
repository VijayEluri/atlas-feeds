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
 * <p>Java class for MediaAgentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MediaAgentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Role" type="{urn:tva:mpeg7:2008}ControlledTermUseType"/>
 *         &lt;choice>
 *           &lt;element name="Agent" type="{urn:tva:mpeg7:2008}AgentType"/>
 *           &lt;element name="AgentRef" type="{urn:tva:mpeg7:2008}ReferenceType"/>
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
@XmlType(name = "MediaAgentType", propOrder = {
    "role",
    "agent",
    "agentRef"
})
@XmlSeeAlso({
    tva.mpeg7._2008.SourcePreferencesType.Disseminator.class,
    CreatorType.class
})
public class MediaAgentType {

    @XmlElement(name = "Role", required = true)
    protected ControlledTermUseType role;
    @XmlElement(name = "Agent")
    protected AgentType agent;
    @XmlElement(name = "AgentRef")
    protected ReferenceType agentRef;

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link ControlledTermUseType }
     *     
     */
    public ControlledTermUseType getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControlledTermUseType }
     *     
     */
    public void setRole(ControlledTermUseType value) {
        this.role = value;
    }

    /**
     * Gets the value of the agent property.
     * 
     * @return
     *     possible object is
     *     {@link AgentType }
     *     
     */
    public AgentType getAgent() {
        return agent;
    }

    /**
     * Sets the value of the agent property.
     * 
     * @param value
     *     allowed object is
     *     {@link AgentType }
     *     
     */
    public void setAgent(AgentType value) {
        this.agent = value;
    }

    /**
     * Gets the value of the agentRef property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *     
     */
    public ReferenceType getAgentRef() {
        return agentRef;
    }

    /**
     * Sets the value of the agentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *     
     */
    public void setAgentRef(ReferenceType value) {
        this.agentRef = value;
    }

}