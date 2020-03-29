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
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class BILRMASK.
 * 
 * @version $Revision$ $Date$
 */
public class BILRMASK implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _BILLERList
     */
    private java.util.Vector _BILLERList;


      //----------------/
     //- Constructors -/
    //----------------/

    public BILRMASK() {
        super();
        _BILLERList = new Vector();
    } //-- com.i2c.payeeinfoservice.xao.pe.BILRMASK()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addBILLER
     * 
     * 
     * 
     * @param vBILLER
     */
    public void addBILLER(com.i2c.payeeinfoservice.xao.pe.BILLER vBILLER)
        throws java.lang.IndexOutOfBoundsException
    {
        _BILLERList.addElement(vBILLER);
    } //-- void addBILLER(com.i2c.payeeinfoservice.xao.pe.BILLER) 

    /**
     * Method addBILLER
     * 
     * 
     * 
     * @param index
     * @param vBILLER
     */
    public void addBILLER(int index, com.i2c.payeeinfoservice.xao.pe.BILLER vBILLER)
        throws java.lang.IndexOutOfBoundsException
    {
        _BILLERList.insertElementAt(vBILLER, index);
    } //-- void addBILLER(int, com.i2c.payeeinfoservice.xao.pe.BILLER) 

    /**
     * Method enumerateBILLER
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateBILLER()
    {
        return _BILLERList.elements();
    } //-- java.util.Enumeration enumerateBILLER() 

    /**
     * Method getBILLER
     * 
     * 
     * 
     * @param index
     * @return BILLER
     */
    public com.i2c.payeeinfoservice.xao.pe.BILLER getBILLER(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _BILLERList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.i2c.payeeinfoservice.xao.pe.BILLER) _BILLERList.elementAt(index);
    } //-- com.i2c.payeeinfoservice.xao.pe.BILLER getBILLER(int) 

    /**
     * Method getBILLER
     * 
     * 
     * 
     * @return BILLER
     */
    public com.i2c.payeeinfoservice.xao.pe.BILLER[] getBILLER()
    {
        int size = _BILLERList.size();
        com.i2c.payeeinfoservice.xao.pe.BILLER[] mArray = new com.i2c.payeeinfoservice.xao.pe.BILLER[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.i2c.payeeinfoservice.xao.pe.BILLER) _BILLERList.elementAt(index);
        }
        return mArray;
    } //-- com.i2c.payeeinfoservice.xao.pe.BILLER[] getBILLER() 

    /**
     * Method getBILLERCount
     * 
     * 
     * 
     * @return int
     */
    public int getBILLERCount()
    {
        return _BILLERList.size();
    } //-- int getBILLERCount() 

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
     * Method removeAllBILLER
     * 
     */
    public void removeAllBILLER()
    {
        _BILLERList.removeAllElements();
    } //-- void removeAllBILLER() 

    /**
     * Method removeBILLER
     * 
     * 
     * 
     * @param index
     * @return BILLER
     */
    public com.i2c.payeeinfoservice.xao.pe.BILLER removeBILLER(int index)
    {
        java.lang.Object obj = _BILLERList.elementAt(index);
        _BILLERList.removeElementAt(index);
        return (com.i2c.payeeinfoservice.xao.pe.BILLER) obj;
    } //-- com.i2c.payeeinfoservice.xao.pe.BILLER removeBILLER(int) 

    /**
     * Method setBILLER
     * 
     * 
     * 
     * @param index
     * @param vBILLER
     */
    public void setBILLER(int index, com.i2c.payeeinfoservice.xao.pe.BILLER vBILLER)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _BILLERList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _BILLERList.setElementAt(vBILLER, index);
    } //-- void setBILLER(int, com.i2c.payeeinfoservice.xao.pe.BILLER) 

    /**
     * Method setBILLER
     * 
     * 
     * 
     * @param BILLERArray
     */
    public void setBILLER(com.i2c.payeeinfoservice.xao.pe.BILLER[] BILLERArray)
    {
        //-- copy array
        _BILLERList.removeAllElements();
        for (int i = 0; i < BILLERArray.length; i++) {
            _BILLERList.addElement(BILLERArray[i]);
        }
    } //-- void setBILLER(com.i2c.payeeinfoservice.xao.pe.BILLER) 

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
        return (com.i2c.payeeinfoservice.xao.pe.BILRMASK) Unmarshaller.unmarshal(com.i2c.payeeinfoservice.xao.pe.BILRMASK.class, reader);
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
