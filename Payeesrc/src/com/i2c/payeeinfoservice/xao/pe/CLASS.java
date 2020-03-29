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
 * Class CLASS.
 * 
 * @version $Revision$ $Date$
 */
public class CLASS implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _CLASSNAME
     */
    private com.i2c.payeeinfoservice.xao.pe.CLASSNAME _CLASSNAME;

    /**
     * Field _CLASSDESC
     */
    private com.i2c.payeeinfoservice.xao.pe.CLASSDESC _CLASSDESC;


      //----------------/
     //- Constructors -/
    //----------------/

    public CLASS() {
        super();
    } //-- com.i2c.payeeinfoservice.xao.pe.CLASS()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'CLASSDESC'.
     * 
     * @return CLASSDESC
     * @return the value of field 'CLASSDESC'.
     */
    public com.i2c.payeeinfoservice.xao.pe.CLASSDESC getCLASSDESC()
    {
        return this._CLASSDESC;
    } //-- com.i2c.payeeinfoservice.xao.pe.CLASSDESC getCLASSDESC() 

    /**
     * Returns the value of field 'CLASSNAME'.
     * 
     * @return CLASSNAME
     * @return the value of field 'CLASSNAME'.
     */
    public com.i2c.payeeinfoservice.xao.pe.CLASSNAME getCLASSNAME()
    {
        return this._CLASSNAME;
    } //-- com.i2c.payeeinfoservice.xao.pe.CLASSNAME getCLASSNAME() 

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
     * Sets the value of field 'CLASSDESC'.
     * 
     * @param CLASSDESC the value of field 'CLASSDESC'.
     */
    public void setCLASSDESC(com.i2c.payeeinfoservice.xao.pe.CLASSDESC CLASSDESC)
    {
        this._CLASSDESC = CLASSDESC;
    } //-- void setCLASSDESC(com.i2c.payeeinfoservice.xao.pe.CLASSDESC) 

    /**
     * Sets the value of field 'CLASSNAME'.
     * 
     * @param CLASSNAME the value of field 'CLASSNAME'.
     */
    public void setCLASSNAME(com.i2c.payeeinfoservice.xao.pe.CLASSNAME CLASSNAME)
    {
        this._CLASSNAME = CLASSNAME;
    } //-- void setCLASSNAME(com.i2c.payeeinfoservice.xao.pe.CLASSNAME) 

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
        return (com.i2c.payeeinfoservice.xao.pe.CLASS) Unmarshaller.unmarshal(com.i2c.payeeinfoservice.xao.pe.CLASS.class, reader);
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
