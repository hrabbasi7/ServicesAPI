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
 * Class CODE.
 * 
 * @version $Revision$ $Date$
 */
public class CODE implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _CHILDID
     */
    private com.i2c.payeeinfoservice.xao.pe.CHILDID _CHILDID;

    /**
     * Field _CODEMASK
     */
    private com.i2c.payeeinfoservice.xao.pe.CODEMASK _CODEMASK;

    /**
     * Field _FLDLENGTH
     */
    private com.i2c.payeeinfoservice.xao.pe.FLDLENGTH _FLDLENGTH;


      //----------------/
     //- Constructors -/
    //----------------/

    public CODE() {
        super();
    } //-- com.i2c.payeeinfoservice.xao.pe.CODE()


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
     * Returns the value of field 'CODEMASK'.
     * 
     * @return CODEMASK
     * @return the value of field 'CODEMASK'.
     */
    public com.i2c.payeeinfoservice.xao.pe.CODEMASK getCODEMASK()
    {
        return this._CODEMASK;
    } //-- com.i2c.payeeinfoservice.xao.pe.CODEMASK getCODEMASK() 

    /**
     * Returns the value of field 'FLDLENGTH'.
     * 
     * @return FLDLENGTH
     * @return the value of field 'FLDLENGTH'.
     */
    public com.i2c.payeeinfoservice.xao.pe.FLDLENGTH getFLDLENGTH()
    {
        return this._FLDLENGTH;
    } //-- com.i2c.payeeinfoservice.xao.pe.FLDLENGTH getFLDLENGTH() 

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
     * Sets the value of field 'CODEMASK'.
     * 
     * @param CODEMASK the value of field 'CODEMASK'.
     */
    public void setCODEMASK(com.i2c.payeeinfoservice.xao.pe.CODEMASK CODEMASK)
    {
        this._CODEMASK = CODEMASK;
    } //-- void setCODEMASK(com.i2c.payeeinfoservice.xao.pe.CODEMASK) 

    /**
     * Sets the value of field 'FLDLENGTH'.
     * 
     * @param FLDLENGTH the value of field 'FLDLENGTH'.
     */
    public void setFLDLENGTH(com.i2c.payeeinfoservice.xao.pe.FLDLENGTH FLDLENGTH)
    {
        this._FLDLENGTH = FLDLENGTH;
    } //-- void setFLDLENGTH(com.i2c.payeeinfoservice.xao.pe.FLDLENGTH) 

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
        return (com.i2c.payeeinfoservice.xao.pe.CODE) Unmarshaller.unmarshal(com.i2c.payeeinfoservice.xao.pe.CODE.class, reader);
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
