//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.25 at 10:09:26 AM GMT 
//


package tva.metadata._2010;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for serviceInformationNameLengthType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="serviceInformationNameLengthType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="short"/>
 *     &lt;enumeration value="medium"/>
 *     &lt;enumeration value="long"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "serviceInformationNameLengthType")
@XmlEnum
public enum ServiceInformationNameLengthType {

    @XmlEnumValue("short")
    SHORT("short"),
    @XmlEnumValue("medium")
    MEDIUM("medium"),
    @XmlEnumValue("long")
    LONG("long");
    private final String value;

    ServiceInformationNameLengthType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ServiceInformationNameLengthType fromValue(String v) {
        for (ServiceInformationNameLengthType c: ServiceInformationNameLengthType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}