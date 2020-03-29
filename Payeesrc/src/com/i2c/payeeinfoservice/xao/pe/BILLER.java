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
 * Class BILLER.
 * 
 * @version $Revision$ $Date$
 */
public class BILLER implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _BILRID
     */
    private com.i2c.payeeinfoservice.xao.pe.BILRID _BILRID;

    /**
     * Field _NAME
     */
    private com.i2c.payeeinfoservice.xao.pe.NAME _NAME;

    /**
     * Field _STATUS
     */
    private com.i2c.payeeinfoservice.xao.pe.STATUS _STATUS;

    /**
     * Field _REGION
     */
    private com.i2c.payeeinfoservice.xao.pe.REGION _REGION;

    /**
     * Field _STATE
     */
    private com.i2c.payeeinfoservice.xao.pe.STATE _STATE;

    /**
     * Field _CLASSList
     */
    private java.util.Vector _CLASSList;

    /**
     * Field _CODEList
     */
    private java.util.Vector _CODEList;

    /**
     * Field _PRFXList
     */
    private java.util.Vector _PRFXList;

    /**
     * Field _ADDRFLAG
     */
    private com.i2c.payeeinfoservice.xao.pe.ADDRFLAG _ADDRFLAG;

    /**
     * Field _ADDRList
     */
    private java.util.Vector _ADDRList;


      //----------------/
     //- Constructors -/
    //----------------/

    public BILLER() {
        super();
        _CLASSList = new Vector();
        _CODEList = new Vector();
        _PRFXList = new Vector();
        _ADDRList = new Vector();
    } //-- com.i2c.payeeinfoservice.xao.pe.BILLER()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addADDR
     * 
     * 
     * 
     * @param vADDR
     */
    public void addADDR(com.i2c.payeeinfoservice.xao.pe.ADDR vADDR)
        throws java.lang.IndexOutOfBoundsException
    {
        _ADDRList.addElement(vADDR);
    } //-- void addADDR(com.i2c.payeeinfoservice.xao.pe.ADDR) 

    /**
     * Method addADDR
     * 
     * 
     * 
     * @param index
     * @param vADDR
     */
    public void addADDR(int index, com.i2c.payeeinfoservice.xao.pe.ADDR vADDR)
        throws java.lang.IndexOutOfBoundsException
    {
        _ADDRList.insertElementAt(vADDR, index);
    } //-- void addADDR(int, com.i2c.payeeinfoservice.xao.pe.ADDR) 

    /**
     * Method addCLASS
     * 
     * 
     * 
     * @param vCLASS
     */
    public void addCLASS(com.i2c.payeeinfoservice.xao.pe.CLASS vCLASS)
        throws java.lang.IndexOutOfBoundsException
    {
        _CLASSList.addElement(vCLASS);
    } //-- void addCLASS(com.i2c.payeeinfoservice.xao.pe.CLASS) 

    /**
     * Method addCLASS
     * 
     * 
     * 
     * @param index
     * @param vCLASS
     */
    public void addCLASS(int index, com.i2c.payeeinfoservice.xao.pe.CLASS vCLASS)
        throws java.lang.IndexOutOfBoundsException
    {
        _CLASSList.insertElementAt(vCLASS, index);
    } //-- void addCLASS(int, com.i2c.payeeinfoservice.xao.pe.CLASS) 

    /**
     * Method addCODE
     * 
     * 
     * 
     * @param vCODE
     */
    public void addCODE(com.i2c.payeeinfoservice.xao.pe.CODE vCODE)
        throws java.lang.IndexOutOfBoundsException
    {
        _CODEList.addElement(vCODE);
    } //-- void addCODE(com.i2c.payeeinfoservice.xao.pe.CODE) 

    /**
     * Method addCODE
     * 
     * 
     * 
     * @param index
     * @param vCODE
     */
    public void addCODE(int index, com.i2c.payeeinfoservice.xao.pe.CODE vCODE)
        throws java.lang.IndexOutOfBoundsException
    {
        _CODEList.insertElementAt(vCODE, index);
    } //-- void addCODE(int, com.i2c.payeeinfoservice.xao.pe.CODE) 

    /**
     * Method addPRFX
     * 
     * 
     * 
     * @param vPRFX
     */
    public void addPRFX(com.i2c.payeeinfoservice.xao.pe.PRFX vPRFX)
        throws java.lang.IndexOutOfBoundsException
    {
        _PRFXList.addElement(vPRFX);
    } //-- void addPRFX(com.i2c.payeeinfoservice.xao.pe.PRFX) 

    /**
     * Method addPRFX
     * 
     * 
     * 
     * @param index
     * @param vPRFX
     */
    public void addPRFX(int index, com.i2c.payeeinfoservice.xao.pe.PRFX vPRFX)
        throws java.lang.IndexOutOfBoundsException
    {
        _PRFXList.insertElementAt(vPRFX, index);
    } //-- void addPRFX(int, com.i2c.payeeinfoservice.xao.pe.PRFX) 

    /**
     * Method enumerateADDR
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateADDR()
    {
        return _ADDRList.elements();
    } //-- java.util.Enumeration enumerateADDR() 

    /**
     * Method enumerateCLASS
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateCLASS()
    {
        return _CLASSList.elements();
    } //-- java.util.Enumeration enumerateCLASS() 

    /**
     * Method enumerateCODE
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateCODE()
    {
        return _CODEList.elements();
    } //-- java.util.Enumeration enumerateCODE() 

    /**
     * Method enumeratePRFX
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumeratePRFX()
    {
        return _PRFXList.elements();
    } //-- java.util.Enumeration enumeratePRFX() 

    /**
     * Method getADDR
     * 
     * 
     * 
     * @param index
     * @return ADDR
     */
    public com.i2c.payeeinfoservice.xao.pe.ADDR getADDR(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _ADDRList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.i2c.payeeinfoservice.xao.pe.ADDR) _ADDRList.elementAt(index);
    } //-- com.i2c.payeeinfoservice.xao.pe.ADDR getADDR(int) 

    /**
     * Method getADDR
     * 
     * 
     * 
     * @return ADDR
     */
    public com.i2c.payeeinfoservice.xao.pe.ADDR[] getADDR()
    {
        int size = _ADDRList.size();
        com.i2c.payeeinfoservice.xao.pe.ADDR[] mArray = new com.i2c.payeeinfoservice.xao.pe.ADDR[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.i2c.payeeinfoservice.xao.pe.ADDR) _ADDRList.elementAt(index);
        }
        return mArray;
    } //-- com.i2c.payeeinfoservice.xao.pe.ADDR[] getADDR() 

    /**
     * Method getADDRCount
     * 
     * 
     * 
     * @return int
     */
    public int getADDRCount()
    {
        return _ADDRList.size();
    } //-- int getADDRCount() 

    /**
     * Returns the value of field 'ADDRFLAG'.
     * 
     * @return ADDRFLAG
     * @return the value of field 'ADDRFLAG'.
     */
    public com.i2c.payeeinfoservice.xao.pe.ADDRFLAG getADDRFLAG()
    {
        return this._ADDRFLAG;
    } //-- com.i2c.payeeinfoservice.xao.pe.ADDRFLAG getADDRFLAG() 

    /**
     * Returns the value of field 'BILRID'.
     * 
     * @return BILRID
     * @return the value of field 'BILRID'.
     */
    public com.i2c.payeeinfoservice.xao.pe.BILRID getBILRID()
    {
        return this._BILRID;
    } //-- com.i2c.payeeinfoservice.xao.pe.BILRID getBILRID() 

    /**
     * Method getCLASS
     * 
     * 
     * 
     * @param index
     * @return CLASS
     */
    public com.i2c.payeeinfoservice.xao.pe.CLASS getCLASS(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _CLASSList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.i2c.payeeinfoservice.xao.pe.CLASS) _CLASSList.elementAt(index);
    } //-- com.i2c.payeeinfoservice.xao.pe.CLASS getCLASS(int) 

    /**
     * Method getCLASS
     * 
     * 
     * 
     * @return CLASS
     */
    public com.i2c.payeeinfoservice.xao.pe.CLASS[] getCLASS()
    {
        int size = _CLASSList.size();
        com.i2c.payeeinfoservice.xao.pe.CLASS[] mArray = new com.i2c.payeeinfoservice.xao.pe.CLASS[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.i2c.payeeinfoservice.xao.pe.CLASS) _CLASSList.elementAt(index);
        }
        return mArray;
    } //-- com.i2c.payeeinfoservice.xao.pe.CLASS[] getCLASS() 

    /**
     * Method getCLASSCount
     * 
     * 
     * 
     * @return int
     */
    public int getCLASSCount()
    {
        return _CLASSList.size();
    } //-- int getCLASSCount() 

    /**
     * Method getCODE
     * 
     * 
     * 
     * @param index
     * @return CODE
     */
    public com.i2c.payeeinfoservice.xao.pe.CODE getCODE(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _CODEList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.i2c.payeeinfoservice.xao.pe.CODE) _CODEList.elementAt(index);
    } //-- com.i2c.payeeinfoservice.xao.pe.CODE getCODE(int) 

    /**
     * Method getCODE
     * 
     * 
     * 
     * @return CODE
     */
    public com.i2c.payeeinfoservice.xao.pe.CODE[] getCODE()
    {
        int size = _CODEList.size();
        com.i2c.payeeinfoservice.xao.pe.CODE[] mArray = new com.i2c.payeeinfoservice.xao.pe.CODE[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.i2c.payeeinfoservice.xao.pe.CODE) _CODEList.elementAt(index);
        }
        return mArray;
    } //-- com.i2c.payeeinfoservice.xao.pe.CODE[] getCODE() 

    /**
     * Method getCODECount
     * 
     * 
     * 
     * @return int
     */
    public int getCODECount()
    {
        return _CODEList.size();
    } //-- int getCODECount() 

    /**
     * Returns the value of field 'NAME'.
     * 
     * @return NAME
     * @return the value of field 'NAME'.
     */
    public com.i2c.payeeinfoservice.xao.pe.NAME getNAME()
    {
        return this._NAME;
    } //-- com.i2c.payeeinfoservice.xao.pe.NAME getNAME() 

    /**
     * Method getPRFX
     * 
     * 
     * 
     * @param index
     * @return PRFX
     */
    public com.i2c.payeeinfoservice.xao.pe.PRFX getPRFX(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _PRFXList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.i2c.payeeinfoservice.xao.pe.PRFX) _PRFXList.elementAt(index);
    } //-- com.i2c.payeeinfoservice.xao.pe.PRFX getPRFX(int) 

    /**
     * Method getPRFX
     * 
     * 
     * 
     * @return PRFX
     */
    public com.i2c.payeeinfoservice.xao.pe.PRFX[] getPRFX()
    {
        int size = _PRFXList.size();
        com.i2c.payeeinfoservice.xao.pe.PRFX[] mArray = new com.i2c.payeeinfoservice.xao.pe.PRFX[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.i2c.payeeinfoservice.xao.pe.PRFX) _PRFXList.elementAt(index);
        }
        return mArray;
    } //-- com.i2c.payeeinfoservice.xao.pe.PRFX[] getPRFX() 

    /**
     * Method getPRFXCount
     * 
     * 
     * 
     * @return int
     */
    public int getPRFXCount()
    {
        return _PRFXList.size();
    } //-- int getPRFXCount() 

    /**
     * Returns the value of field 'REGION'.
     * 
     * @return REGION
     * @return the value of field 'REGION'.
     */
    public com.i2c.payeeinfoservice.xao.pe.REGION getREGION()
    {
        return this._REGION;
    } //-- com.i2c.payeeinfoservice.xao.pe.REGION getREGION() 

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
     * Returns the value of field 'STATUS'.
     * 
     * @return STATUS
     * @return the value of field 'STATUS'.
     */
    public com.i2c.payeeinfoservice.xao.pe.STATUS getSTATUS()
    {
        return this._STATUS;
    } //-- com.i2c.payeeinfoservice.xao.pe.STATUS getSTATUS() 

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
     * Method removeADDR
     * 
     * 
     * 
     * @param index
     * @return ADDR
     */
    public com.i2c.payeeinfoservice.xao.pe.ADDR removeADDR(int index)
    {
        java.lang.Object obj = _ADDRList.elementAt(index);
        _ADDRList.removeElementAt(index);
        return (com.i2c.payeeinfoservice.xao.pe.ADDR) obj;
    } //-- com.i2c.payeeinfoservice.xao.pe.ADDR removeADDR(int) 

    /**
     * Method removeAllADDR
     * 
     */
    public void removeAllADDR()
    {
        _ADDRList.removeAllElements();
    } //-- void removeAllADDR() 

    /**
     * Method removeAllCLASS
     * 
     */
    public void removeAllCLASS()
    {
        _CLASSList.removeAllElements();
    } //-- void removeAllCLASS() 

    /**
     * Method removeAllCODE
     * 
     */
    public void removeAllCODE()
    {
        _CODEList.removeAllElements();
    } //-- void removeAllCODE() 

    /**
     * Method removeAllPRFX
     * 
     */
    public void removeAllPRFX()
    {
        _PRFXList.removeAllElements();
    } //-- void removeAllPRFX() 

    /**
     * Method removeCLASS
     * 
     * 
     * 
     * @param index
     * @return CLASS
     */
    public com.i2c.payeeinfoservice.xao.pe.CLASS removeCLASS(int index)
    {
        java.lang.Object obj = _CLASSList.elementAt(index);
        _CLASSList.removeElementAt(index);
        return (com.i2c.payeeinfoservice.xao.pe.CLASS) obj;
    } //-- com.i2c.payeeinfoservice.xao.pe.CLASS removeCLASS(int) 

    /**
     * Method removeCODE
     * 
     * 
     * 
     * @param index
     * @return CODE
     */
    public com.i2c.payeeinfoservice.xao.pe.CODE removeCODE(int index)
    {
        java.lang.Object obj = _CODEList.elementAt(index);
        _CODEList.removeElementAt(index);
        return (com.i2c.payeeinfoservice.xao.pe.CODE) obj;
    } //-- com.i2c.payeeinfoservice.xao.pe.CODE removeCODE(int) 

    /**
     * Method removePRFX
     * 
     * 
     * 
     * @param index
     * @return PRFX
     */
    public com.i2c.payeeinfoservice.xao.pe.PRFX removePRFX(int index)
    {
        java.lang.Object obj = _PRFXList.elementAt(index);
        _PRFXList.removeElementAt(index);
        return (com.i2c.payeeinfoservice.xao.pe.PRFX) obj;
    } //-- com.i2c.payeeinfoservice.xao.pe.PRFX removePRFX(int) 

    /**
     * Method setADDR
     * 
     * 
     * 
     * @param index
     * @param vADDR
     */
    public void setADDR(int index, com.i2c.payeeinfoservice.xao.pe.ADDR vADDR)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _ADDRList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _ADDRList.setElementAt(vADDR, index);
    } //-- void setADDR(int, com.i2c.payeeinfoservice.xao.pe.ADDR) 

    /**
     * Method setADDR
     * 
     * 
     * 
     * @param ADDRArray
     */
    public void setADDR(com.i2c.payeeinfoservice.xao.pe.ADDR[] ADDRArray)
    {
        //-- copy array
        _ADDRList.removeAllElements();
        for (int i = 0; i < ADDRArray.length; i++) {
            _ADDRList.addElement(ADDRArray[i]);
        }
    } //-- void setADDR(com.i2c.payeeinfoservice.xao.pe.ADDR) 

    /**
     * Sets the value of field 'ADDRFLAG'.
     * 
     * @param ADDRFLAG the value of field 'ADDRFLAG'.
     */
    public void setADDRFLAG(com.i2c.payeeinfoservice.xao.pe.ADDRFLAG ADDRFLAG)
    {
        this._ADDRFLAG = ADDRFLAG;
    } //-- void setADDRFLAG(com.i2c.payeeinfoservice.xao.pe.ADDRFLAG) 

    /**
     * Sets the value of field 'BILRID'.
     * 
     * @param BILRID the value of field 'BILRID'.
     */
    public void setBILRID(com.i2c.payeeinfoservice.xao.pe.BILRID BILRID)
    {
        this._BILRID = BILRID;
    } //-- void setBILRID(com.i2c.payeeinfoservice.xao.pe.BILRID) 

    /**
     * Method setCLASS
     * 
     * 
     * 
     * @param index
     * @param vCLASS
     */
    public void setCLASS(int index, com.i2c.payeeinfoservice.xao.pe.CLASS vCLASS)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _CLASSList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _CLASSList.setElementAt(vCLASS, index);
    } //-- void setCLASS(int, com.i2c.payeeinfoservice.xao.pe.CLASS) 

    /**
     * Method setCLASS
     * 
     * 
     * 
     * @param CLASSArray
     */
    public void setCLASS(com.i2c.payeeinfoservice.xao.pe.CLASS[] CLASSArray)
    {
        //-- copy array
        _CLASSList.removeAllElements();
        for (int i = 0; i < CLASSArray.length; i++) {
            _CLASSList.addElement(CLASSArray[i]);
        }
    } //-- void setCLASS(com.i2c.payeeinfoservice.xao.pe.CLASS) 

    /**
     * Method setCODE
     * 
     * 
     * 
     * @param index
     * @param vCODE
     */
    public void setCODE(int index, com.i2c.payeeinfoservice.xao.pe.CODE vCODE)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _CODEList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _CODEList.setElementAt(vCODE, index);
    } //-- void setCODE(int, com.i2c.payeeinfoservice.xao.pe.CODE) 

    /**
     * Method setCODE
     * 
     * 
     * 
     * @param CODEArray
     */
    public void setCODE(com.i2c.payeeinfoservice.xao.pe.CODE[] CODEArray)
    {
        //-- copy array
        _CODEList.removeAllElements();
        for (int i = 0; i < CODEArray.length; i++) {
            _CODEList.addElement(CODEArray[i]);
        }
    } //-- void setCODE(com.i2c.payeeinfoservice.xao.pe.CODE) 

    /**
     * Sets the value of field 'NAME'.
     * 
     * @param NAME the value of field 'NAME'.
     */
    public void setNAME(com.i2c.payeeinfoservice.xao.pe.NAME NAME)
    {
        this._NAME = NAME;
    } //-- void setNAME(com.i2c.payeeinfoservice.xao.pe.NAME) 

    /**
     * Method setPRFX
     * 
     * 
     * 
     * @param index
     * @param vPRFX
     */
    public void setPRFX(int index, com.i2c.payeeinfoservice.xao.pe.PRFX vPRFX)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _PRFXList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _PRFXList.setElementAt(vPRFX, index);
    } //-- void setPRFX(int, com.i2c.payeeinfoservice.xao.pe.PRFX) 

    /**
     * Method setPRFX
     * 
     * 
     * 
     * @param PRFXArray
     */
    public void setPRFX(com.i2c.payeeinfoservice.xao.pe.PRFX[] PRFXArray)
    {
        //-- copy array
        _PRFXList.removeAllElements();
        for (int i = 0; i < PRFXArray.length; i++) {
            _PRFXList.addElement(PRFXArray[i]);
        }
    } //-- void setPRFX(com.i2c.payeeinfoservice.xao.pe.PRFX) 

    /**
     * Sets the value of field 'REGION'.
     * 
     * @param REGION the value of field 'REGION'.
     */
    public void setREGION(com.i2c.payeeinfoservice.xao.pe.REGION REGION)
    {
        this._REGION = REGION;
    } //-- void setREGION(com.i2c.payeeinfoservice.xao.pe.REGION) 

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
     * Sets the value of field 'STATUS'.
     * 
     * @param STATUS the value of field 'STATUS'.
     */
    public void setSTATUS(com.i2c.payeeinfoservice.xao.pe.STATUS STATUS)
    {
        this._STATUS = STATUS;
    } //-- void setSTATUS(com.i2c.payeeinfoservice.xao.pe.STATUS) 

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
        return (com.i2c.payeeinfoservice.xao.pe.BILLER) Unmarshaller.unmarshal(com.i2c.payeeinfoservice.xao.pe.BILLER.class, reader);
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
