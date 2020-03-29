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
 * Class ADDRFLAG.
 * 
 * @version $Revision$ $Date$
 */
public class ADDRFLAG implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _FLAG
     */
    private com.i2c.payeeinfoservice.xao.pe.FLAG _FLAG;


      //----------------/
     //- Constructors -/
    //----------------/

    public ADDRFLAG() {
        super();
    } //-- com.i2c.payeeinfoservice.xao.pe.ADDRFLAG()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'FLAG'.
     * 
     * @return FLAG
     * @return the value of field 'FLAG'.
     */
    public com.i2c.payeeinfoservice.xao.pe.FLAG getFLAG()
    {
        return this._FLAG;
    } //-- com.i2c.payeeinfoservice.xao.pe.FLAG getFLAG() 

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
     * Sets the value of field 'FLAG'.
     * 
     * @param FLAG the value of field 'FLAG'.
     */
    public void setFLAG(com.i2c.payeeinfoservice.xao.pe.FLAG FLAG)
    {
        this._FLAG = FLAG;
    } //-- void setFLAG(com.i2c.payeeinfoservice.xao.pe.FLAG) 

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
        return (com.i2c.payeeinfoservice.xao.pe.ADDRFLAG) Unmarshaller.unmarshal(com.i2c.payeeinfoservice.xao.pe.ADDRFLAG.class, reader);
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
