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
 * Class PRFX.
 * 
 * @version $Revision$ $Date$
 */
public class PRFX implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _CHILDID
     */
    private com.i2c.payeeinfoservice.xao.pe.CHILDID _CHILDID;

    /**
     * Field _PRFXMASK
     */
    private com.i2c.payeeinfoservice.xao.pe.PRFXMASK _PRFXMASK;

    /**
     * Field _PRFXLENGTH
     */
    private com.i2c.payeeinfoservice.xao.pe.PRFXLENGTH _PRFXLENGTH;


      //----------------/
     //- Constructors -/
    //----------------/

    public PRFX() {
        super();
    } //-- com.i2c.payeeinfoservice.xao.pe.PRFX()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Returns the value of field 'PRFXLENGTH'.
     * 
     * @return PRFXLENGTH
     * @return the value of field 'PRFXLENGTH'.
     */
    public com.i2c.payeeinfoservice.xao.pe.PRFXLENGTH getPRFXLENGTH()
    {
        return this._PRFXLENGTH;
    } //-- com.i2c.payeeinfoservice.xao.pe.PRFXLENGTH getPRFXLENGTH() 

    /**
     * Returns the value of field 'PRFXMASK'.
     * 
     * @return PRFXMASK
     * @return the value of field 'PRFXMASK'.
     */
    public com.i2c.payeeinfoservice.xao.pe.PRFXMASK getPRFXMASK()
    {
        return this._PRFXMASK;
    } //-- com.i2c.payeeinfoservice.xao.pe.PRFXMASK getPRFXMASK() 

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
     * Sets the value of field 'CHILDID'.
     * 
     * @param CHILDID the value of field 'CHILDID'.
     */
    public void setCHILDID(com.i2c.payeeinfoservice.xao.pe.CHILDID CHILDID)
    {
        this._CHILDID = CHILDID;
    } //-- void setCHILDID(com.i2c.payeeinfoservice.xao.pe.CHILDID) 

    /**
     * Sets the value of field 'PRFXLENGTH'.
     * 
     * @param PRFXLENGTH the value of field 'PRFXLENGTH'.
     */
    public void setPRFXLENGTH(com.i2c.payeeinfoservice.xao.pe.PRFXLENGTH PRFXLENGTH)
    {
        this._PRFXLENGTH = PRFXLENGTH;
    } //-- void setPRFXLENGTH(com.i2c.payeeinfoservice.xao.pe.PRFXLENGTH) 

    /**
     * Sets the value of field 'PRFXMASK'.
     * 
     * @param PRFXMASK the value of field 'PRFXMASK'.
     */
    public void setPRFXMASK(com.i2c.payeeinfoservice.xao.pe.PRFXMASK PRFXMASK)
    {
        this._PRFXMASK = PRFXMASK;
    } //-- void setPRFXMASK(com.i2c.payeeinfoservice.xao.pe.PRFXMASK) 

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
        return (com.i2c.payeeinfoservice.xao.pe.PRFX) Unmarshaller.unmarshal(com.i2c.payeeinfoservice.xao.pe.PRFX.class, reader);
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
