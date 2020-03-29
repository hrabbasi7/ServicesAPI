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
 * Class BILLERDescriptor.
 * 
 * @version $Revision$ $Date$
 */
public class BILLERDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public BILLERDescriptor() {
        super();
        nsURI = "http://www.w3.org/2001/XMLSchema";
        xmlName = "BILLER";
        
        //-- set grouping compositor
        setCompositorAsSequence();
        org.exolab.castor.xml.util.XMLFieldDescriptorImpl  desc           = null;
        org.exolab.castor.xml.XMLFieldHandler              handler        = null;
        org.exolab.castor.xml.FieldValidator               fieldValidator = null;
        //-- initialize attribute descriptors
        
        //-- initialize element descriptors
        
        //-- _BILRID
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.BILRID.class, "_BILRID", "BILRID", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                BILLER target = (BILLER) object;
                return target.getBILRID();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BILLER target = (BILLER) object;
                    target.setBILRID( (com.i2c.payeeinfoservice.xao.pe.BILRID) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.BILRID();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _BILRID
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _NAME
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.NAME.class, "_NAME", "NAME", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                BILLER target = (BILLER) object;
                return target.getNAME();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BILLER target = (BILLER) object;
                    target.setNAME( (com.i2c.payeeinfoservice.xao.pe.NAME) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.NAME();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _NAME
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _STATUS
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.STATUS.class, "_STATUS", "STATUS", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                BILLER target = (BILLER) object;
                return target.getSTATUS();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BILLER target = (BILLER) object;
                    target.setSTATUS( (com.i2c.payeeinfoservice.xao.pe.STATUS) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.STATUS();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _STATUS
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _REGION
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.REGION.class, "_REGION", "REGION", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                BILLER target = (BILLER) object;
                return target.getREGION();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BILLER target = (BILLER) object;
                    target.setREGION( (com.i2c.payeeinfoservice.xao.pe.REGION) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.REGION();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _REGION
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _STATE
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.STATE.class, "_STATE", "STATE", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                BILLER target = (BILLER) object;
                return target.getSTATE();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BILLER target = (BILLER) object;
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
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _STATE
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _CLASSList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.CLASS.class, "_CLASSList", "CLASS", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                BILLER target = (BILLER) object;
                return target.getCLASS();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BILLER target = (BILLER) object;
                    target.addCLASS( (com.i2c.payeeinfoservice.xao.pe.CLASS) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.CLASS();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _CLASSList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _CODEList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.CODE.class, "_CODEList", "CODE", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                BILLER target = (BILLER) object;
                return target.getCODE();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BILLER target = (BILLER) object;
                    target.addCODE( (com.i2c.payeeinfoservice.xao.pe.CODE) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.CODE();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _CODEList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _PRFXList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.PRFX.class, "_PRFXList", "PRFX", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                BILLER target = (BILLER) object;
                return target.getPRFX();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BILLER target = (BILLER) object;
                    target.addPRFX( (com.i2c.payeeinfoservice.xao.pe.PRFX) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.PRFX();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _PRFXList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _ADDRFLAG
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.ADDRFLAG.class, "_ADDRFLAG", "ADDRFLAG", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                BILLER target = (BILLER) object;
                return target.getADDRFLAG();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BILLER target = (BILLER) object;
                    target.setADDRFLAG( (com.i2c.payeeinfoservice.xao.pe.ADDRFLAG) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.ADDRFLAG();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        
        //-- validation code for: _ADDRFLAG
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _ADDRList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(com.i2c.payeeinfoservice.xao.pe.ADDR.class, "_ADDRList", "ADDR", org.exolab.castor.xml.NodeType.Element);
        handler = (new org.exolab.castor.xml.XMLFieldHandler() {
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                BILLER target = (BILLER) object;
                return target.getADDR();
            }
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    BILLER target = (BILLER) object;
                    target.addADDR( (com.i2c.payeeinfoservice.xao.pe.ADDR) value);
                }
                catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public java.lang.Object newInstance( java.lang.Object parent ) {
                return new com.i2c.payeeinfoservice.xao.pe.ADDR();
            }
        } );
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.w3.org/2001/XMLSchema");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        
        //-- validation code for: _ADDRList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
    } //-- com.i2c.payeeinfoservice.xao.pe.BILLERDescriptor()


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
        return com.i2c.payeeinfoservice.xao.pe.BILLER.class;
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
