//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.2-b15-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2005.10.06 at 12:29:32 GMT+05:00 
//


package com.i2c.component.screenxmlobjects;


/**
 * Java content class for ControlMappingsType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/C:/ScreensSchema.xsd line 55)
 * <p>
 * <pre>
 * &lt;complexType name="ControlMappingsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="control" type="{http://www.ibm.com}ControlType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface ControlMappingsType {


    /**
     * Gets the value of the Control property.
     * 
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the Control property.
     * 
     * For example, to add a new item, do as follows:
     * <pre>
     *    getControl().add(newItem);
     * </pre>
     * 
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link com.i2c.component.screenxmlobjects.ControlType}
     * 
     */
    java.util.List getControl();

}