/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.6</a>, using an XML
 * Schema.
 * $Id$
 */

package com.i2c.payeeinfoservice.xao.pe;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class ADDR.
 * 
 * @version $Revision$ $Date$
 */
public class ADDR implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _CHILDID
     */
    private com.i2c.payeeinfoservice.xao.pe.CHILDID _CHILDID;

    /**
     * Field _ADDR1
     */
    private com.i2c.payeeinfoservice.xao.pe.ADDR1 _ADDR1;

    /**
     * Field _ADDR2
     */
    private com.i2c.payeeinfoservice.xao.pe.ADDR2 _ADDR2;

    /**
     * Field _CITY
     */
    private com.i2c.payeeinfoservice.xao.pe.CITY _CITY;

    /**
     * Field _STATE
     */
    private com.i2c.payeeinfoservice.xao.pe.STATE _STATE;

    /**
     * Field _ZIP
     */
    private com.i2c.payeeinfoservice.xao.pe.ZIP _ZIP;


      //----------------/
     //- Constructors -/
    //----------------/

    public ADDR() {
        super();
    } //-- com.i2c.payeeinfoservice.xao.pe.ADDR()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'ADDR1'.
     * 
     * @return ADDR1
     * @return the value of field 'ADDR1'.
     */
    public com.i2c.payeeinfoservice.xao.pe.ADDR1 getADDR1()
    {
        return this._ADDR1;
    } //-- com.i2c.payeeinfoservice.xao.pe.ADDR1 getADDR1() 

    /**
     * Returns the value of field 'ADDR2'.
     * 
     * @return ADDR2
     * @return the value of field 'ADDR2'.
     */
    public com.i2c.payeeinfoservice.xao.pe.ADDR2 getADDR2()
    {
        return this._ADDR2;
    } //-- com.i2c.payeeinfoservice.xao.pe.ADDR2 getADDR2() 

    /**
     * Returns the value of field 'CHILDID'.
     * 
     * @return CHILDID
     * @return the value of field 'CHILDID'.
     */
    public com.i2c.payeeinfoservice.xao.pe.CHILDID getCHILDID()
    {
        return this._CHILDID;
    } //-- com.i2c.payeeinfoservice.xao.pe.CHILDID getCHILDID() 

    /**
     * Returns the value of field 'CITY'.
     * 
     * @return CITY
     * @return the value of field 'CITY'.
     */
    public com.i2c.payeeinfoservice.xao.pe.CITY getCITY()
    {
        return this._CITY;
    } //-- com.i2c.payeeinfoservice.xao.pe.CITY getCITY() 

    /**
     * Returns the value of field 'STATE'.
     * 
     * @return STATE
     * @return the value of field 'STATE'.
     */
    public com.i2c.payeeinfoservice.xao.pe.STATE getSTATE()
    {
        return this._STATE;
    } //-- com.i2c.payeeinfoservice.xao.pe.STATE getSTATE() 

    /**
     * Returns the value of field 'ZIP'.
     * 
     * @return ZIP
     * @return the value of field 'ZIP'.
     */
    public com.i2c.payeeinfoservice.xao.pe.ZIP getZIP()
    {
        return this._ZIP;
    } //-- com.i2c.payeeinfoservice.xao.pe.ZIP getZIP() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'ADDR1'.
     * 
     * @param ADDR1 the value of field 'ADDR1'.
     */
    public void setADDR1(com.i2c.payeeinfoservice.xao.pe.ADDR1 ADDR1)
    {
        this._ADDR1 = ADDR1;
    } //-- void setADDR1(com.i2c.payeeinfoservice.xao.pe.ADDR1) 

    /**
     * Sets the value of field 'ADDR2'.
     * 
     * @param ADDR2 the value of field 'ADDR2'.
     */
    public void setADDR2(com.i2c.payeeinfoservice.xao.pe.ADDR2 ADDR2)
    {
        this._ADDR2 = ADDR2;
    } //-- void setADDR2(com.i2c.payeeinfoservice.xao.pe.ADDR2) 

    /**
     * Sets the value of field 'CHILDID'.
     * 
     * @param CHILDID the value of field 'CHILDID'.
     */
    public void setCHILDID(com.i2c.payeeinfoservice.xao.pe.CHILDID CHILDID)
    {
        this._CHILDID = CHILDID;
    } //-- void setCHILDID(com.i2c.payeeinfoservice.xao.pe.CHILDID) 

    /**
     * Sets the value of field 'CITY'.
     * 
     * @param CITY the value of field 'CITY'.
     */
    public void setCITY(com.i2c.payeeinfoservice.xao.pe.CITY CITY)
    {
        this._CITY = CITY;
    } //-- void setCITY(com.i2c.payeeinfoservice.xao.pe.CITY) 

    /**
     * Sets the value of field 'STATE'.
     * 
     * @param STATE the value of field 'STATE'.
     */
    public void setSTATE(com.i2c.payeeinfoservice.xao.pe.STATE STATE)
    {
        this._STATE = STATE;
    } //-- void setSTATE(com.i2c.payeeinfoservice.xao.pe.STATE) 

    /**
     * Sets the value of field 'ZIP'.
     * 
     * @param ZIP the value of field 'ZIP'.
     */
    public void setZIP(com.i2c.payeeinfoservice.xao.pe.ZIP ZIP)
    {
        this._ZIP = ZIP;
    } //-- void setZIP(com.i2c.payeeinfoservice.xao.pe.ZIP) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Object
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.i2c.payeeinfoservice.xao.pe.ADDR) Unmarshaller.unmarshal(com.i2c.payeeinfoservice.xao.pe.ADDR.class, reader);
    } //-- java.lang.Object unmarshal(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
