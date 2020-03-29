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

import org.exolab.castor.mapping.AccessMode;
import org.exolab.castor.xml.TypeValidator;
import org.exolab.castor.xml.XMLFieldDescriptor;
import org.exolab.castor.xml.validators.*;

/**
 * Class ADDRDescriptor.
 * 
 * @version $Revision$ $Date$
 */
public class ADDRDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field nsPrefix
     */
    private java.lang.String nsPrefix;

    /**
     * Field nsURI
     */
    private java.lang.String nsURI;

    /**
     * Field xmlName
     */
    private java.lang.String xmlName;

    /**
     * Field identity
     */
    private org.exolab.castor.xml.XMLFieldDescriptor identity;


      //----------------/
     //- Constructors -/
    //----------------/

    public ADDRDescriptor() {
        super();
        nsURI = "http://www.w3.org/2001/XMLSchema";
        xmlName = "ADDR";
        
        //-- set grouping compositor
        setCompositorAsSequence();
        org.exolab.castor.xml.util.XMLFieldDescriptorImpl  desc           = null;
        org.exolab.castor.xml.XMLFieldHandler              handler        = null;
        org.exolab.castor.xml.FieldValidator               fieldValidator = null;
        //-- initialize attribute descriptors
        
        //-- initialize element descriptors
        
        //-- _CHILDID
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.CHILDID.class, "_CHILDID", "CHILDID", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                ADDR target = (ADDR) object;
                return target.getCHILDID();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ADDR target = (ADDR) object;
                    target.setCHILDID( (com.i2c.payeeinfoservice.xao.pe.CHILDID) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.CHILDID();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _CHILDID
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _ADDR1
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.ADDR1.class, "_ADDR1", "ADDR1", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                ADDR target = (ADDR) object;
                return target.getADDR1();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ADDR target = (ADDR) object;
                    target.setADDR1( (com.i2c.payeeinfoservice.xao.pe.ADDR1) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.ADDR1();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _ADDR1
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _ADDR2
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.ADDR2.class, "_ADDR2", "ADDR2", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                ADDR target = (ADDR) object;
                return target.getADDR2();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ADDR target = (ADDR) object;
                    target.setADDR2( (com.i2c.payeeinfoservice.xao.pe.ADDR2) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.ADDR2();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _ADDR2
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _CITY
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.CITY.class, "_CITY", "CITY", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                ADDR target = (ADDR) object;
                return target.getCITY();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ADDR target = (ADDR) object;
                    target.setCITY( (com.i2c.payeeinfoservice.xao.pe.CITY) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.CITY();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _CITY
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _STATE
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.STATE.class, "_STATE", "STATE", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                ADDR target = (ADDR) object;
                return target.getSTATE();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ADDR target = (ADDR) object;
                    target.setSTATE( (com.i2c.payeeinfoservice.xao.pe.STATE) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.STATE();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _STATE
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _ZIP
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.ZIP.class, "_ZIP", "ZIP", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                ADDR target = (ADDR) object;
                return target.getZIP();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    ADDR target = (ADDR) object;
                    target.setZIP( (com.i2c.payeeinfoservice.xao.pe.ZIP) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.ZIP();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _ZIP
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
    } //-- com.i2c.payeeinfoservice.xao.pe.ADDRDescriptor()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getAccessMode
     * 
     * 
     * 
     * @return AccessMode
     */
    public org.exolab.castor.mapping.AccessMode getAccessMode()
    {
        return null;
    } //-- org.exolab.castor.mapping.AccessMode getAccessMode() 

    /**
     * Method getExtends
     * 
     * 
     * 
     * @return ClassDescriptor
     */
    public org.exolab.castor.mapping.ClassDescriptor getExtends()
    {
        return null;
    } //-- org.exolab.castor.mapping.ClassDescriptor getExtends() 

    /**
     * Method getIdentity
     * 
     * 
     * 
     * @return FieldDescriptor
     */
    public org.exolab.castor.mapping.FieldDescriptor getIdentity()
    {
        return identity;
    } //-- org.exolab.castor.mapping.FieldDescriptor getIdentity() 

    /**
     * Method getJavaClass
     * 
     * 
     * 
     * @return Class
     */
    public java.lang.Class getJavaClass()
    {
        return com.i2c.payeeinfoservice.xao.pe.ADDR.class;
    } //-- java.lang.Class getJavaClass() 

    /**
     * Method getNameSpacePrefix
     * 
     * 
     * 
     * @return String
     */
    public java.lang.String getNameSpacePrefix()
    {
        return nsPrefix;
    } //-- java.lang.String getNameSpacePrefix() 

    /**
     * Method getNameSpaceURI
     * 
     * 
     * 
     * @return String
     */
    public java.lang.String getNameSpaceURI()
    {
        return nsURI;
    } //-- java.lang.String getNameSpaceURI() 

    /**
     * Method getValidator
     * 
     * 
     * 
     * @return TypeValidator
     */
    public org.exolab.castor.xml.TypeValidator getValidator()
    {
        return this;
    } //-- org.exolab.castor.xml.TypeValidator getValidator() 

    /**
     * Method getXMLName
     * 
     * 
     * 
     * @return String
     */
    public java.lang.String getXMLName()
    {
        return xmlName;
    } //-- java.lang.String getXMLName() 

}
