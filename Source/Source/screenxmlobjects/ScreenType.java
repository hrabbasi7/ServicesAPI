//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.2-b15-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2005.10.06 at 12:29:32 GMT+05:00 
//


package com.i2c.component.screenxmlobjects;


/**
 * Java content class for ScreenType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/C:/ScreensSchema.xsd line 22)
 * <p>
 * <pre>
 * &lt;complexType name="ScreenType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="db-queries" type="{http://www.ibm.com}DBQueriesType"/>
 *         &lt;element name="control-mappings" type="{http://www.ibm.com}ControlMappingsType"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface ScreenType {


    /**
     * 
     * @return
     *     possible object is
     *     {@link com.i2c.component.screenxmlobjects.ControlMappingsType}
     */
    com.i2c.component.screenxmlobjects.ControlMappingsType getControlMappings();

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link com.i2c.component.screenxmlobjects.ControlMappingsType}
     */
    void setControlMappings(com.i2c.component.screenxmlobjects.ControlMappingsType value);

    /**
     * 
     * @return
     *     possible object is
     *     {@link com.i2c.component.screenxmlobjects.DBQueriesType}
     */
    com.i2c.component.screenxmlobjects.DBQueriesType getDbQueries();

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link com.i2c.component.screenxmlobjects.DBQueriesType}
     */
    void setDbQueries(com.i2c.component.screenxmlobjects.DBQueriesType value);

}
