package com.i2c.payeeinfoservice.mapper;

import java.io.*;
import java.sql.*;
import java.util.*;

import com.i2c.payeeinfoservice.dao.*;
import com.i2c.payeeinfoservice.util.*;
import com.i2c.payeeinfoservice.vo.*;
import com.i2c.payeeinfoservice.xao.pe.*;
import com.i2c.payeeinfoservice.util.LogLevel;
import com.i2c.payeeinfoservice.util.Constants;
import com.i2c.utils.logging.I2cLogger;
import java.util.logging.Logger;

/**
 * <p>Title: Payee Information Service</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Innovative Pvt. Ltd.</p>
 *
 * @author Haroon ur Rashid Abbasi
 * @version 1.0
 */
public class PayeeMapper {
    private BILRMASK payeeMain = null;
    private Connection dbConn = null;
    private String instanceID = null;
    private String payeeFileName = null;
    private Hashtable payeeHashTable = new Hashtable();

    public static Logger lgr;

    public PayeeMapper(String instanceID, Connection dbConn, String logPath,
                       String payeeFileName, Logger logger) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
//        Constants.LOG_FILE_PATH = logPath;
//        Constants.LOG_FILE_NAME = "PayeeAPI-";
        this.payeeFileName = payeeFileName;
        this.lgr = logger;
    }

    public PayeeMapper(String instanceID, Connection dbConn,
                       String payeeFileName, Logger logger) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
        this.payeeFileName = payeeFileName;
        this.lgr = logger;
    }


    public void payeePopulater() throws Exception {
        try{
            lgr.log(I2cLogger.INFO,
                    "********************Start PayeePopulater******************");

            lgr.log(I2cLogger.INFO,
                        "Payee File Path ---> " + payeeFileName);
            // read the xml document
            FileReader reader = new FileReader(payeeFileName);
            //UNMARSHAL the xml document
            lgr.log(I2cLogger.INFO,
                    "Reading File End ---- Start Unmarshalling");
            payeeMain = (BILRMASK) BILRMASK.unmarshal(reader);
            lgr.log(I2cLogger.FINE, "Populate Payee Vo");
            int payeeCount = payeeMain.getBILLERCount();
            lgr.log(I2cLogger.INFO, "Payee Counter -----> " + payeeCount);
            if (payeeCount > 0) {
                lgr.log(I2cLogger.FINE,"Payee Count is greater then Zero");
                PayeeVO payeeVo = null;
                PayeesDAO payeeDao = new PayeesDAO(instanceID,dbConn, lgr);
                lgr.log(I2cLogger.INFO,
                            "Getting all payees from the DB for Comparsion with File");
                Hashtable allPayeesTable = payeeDao.selectPayees();
                /* If Payee Tables are populated first time then no record in Hash */
                lgr.log(I2cLogger.INFO,
                            ">>>>>START Extracting Payees from XML File...");
                for (int i = 0; i < payeeCount; i++) {
                    BILLER payeeXml = payeeMain.getBILLER(i);
                    payeeVo = new PayeeVO();
                    payeeVo.setPayeeId(payeeXml.getBILRID().getContent());
                    payeeVo.setPayeeName(payeeXml.getNAME().getContent());
                    payeeVo.setPayeeStatus((payeeXml.getSTATUS().getContent().
                                            equals(
                            "ACTIVE")) ? "Y" : "N");
                    if (payeeXml.getREGION() != null) {
                        if (payeeXml.getREGION().getContent().equals("NATIONAL")) {
                            payeeVo.setPayeeRegion("N");
                        } else if (payeeXml.getREGION().getContent().equals(
                                "LOCAL")) {
                            payeeVo.setPayeeRegion("L");
                        } else if (payeeXml.getREGION().getContent().equals(
                                "REGION")) {
                            payeeVo.setPayeeRegion("R");
                        }
                    }
                    if (payeeXml.getSTATE() != null) {
                        payeeVo.setPayeeState(payeeXml.getSTATE().getContent());
                    }
                    if (payeeXml.getADDRFLAG() != null) {
                        payeeVo.setPayeeAddressFlag(payeeXml.getADDRFLAG().
                                getFLAG().
                                getContent().trim().equals(
                                        "TRUE") ? "Y" : "N");
                    }
                    if (payeeVo.getPayeeAddressFlag().equals("Y")) {
                        payeeVo.setPayeeAddress(getPayeeAddressList(payeeXml));
                    }
                    if (payeeXml.getCLASSCount() > 0) {
                        payeeVo.setPayeeCatagory(getPayeeCatagoryList(payeeXml));
                    }
                    if (payeeXml.getCODECount() > 0) {
                        payeeVo.setPayeeCode(getPayeeCodeList(payeeXml));
                    }
                    if (payeeXml.getPRFXCount() > 0) {
                        payeeVo.setPayeePrefix(getPayeePrefixList(payeeXml));
                    }
                    lgr.log(I2cLogger.FINE,
                            "Calling ----- getPayeeStateList ");
                    // getPayee StateList
                    payeeVo = getPayeeStateList(payeeVo);
                    // compare DB payees with XML payees

                    lgr.log(I2cLogger.FINE,
                            "Payee exist in DB then Start Compare it with payees in File");

                    if (allPayeesTable.size() > 0) {

                        lgr.log(I2cLogger.FINE,
                                "Payee Exist in DataBase -- <comparePayee()> -- Compare Payee Id --> " +
                                payeeVo.getPayeeId() + " With its DB record...");

                        PayeeVO returnPayee = comparePayee(allPayeesTable, payeeVo);

                        if (returnPayee != null){
                            lgr.log(I2cLogger.FINE,
                                    "returnPayee from comparePayee --- Payee need to be updated. Payee Name --> " +
                                    returnPayee.getPayeeName());
                            updatePayeeInDB(returnPayee, payeeDao, Long.parseLong(returnPayee.getPayeeSNo().trim()));

                        } else if (allPayeesTable.get(payeeVo.getPayeeId()) == null){
                            lgr.log(I2cLogger.INFO,
                                    "Payee Need to be Inserted --- Payee Name --> " +
                                    payeeVo.getPayeeName());

                            if (insertPayeeInDb(payeeVo, payeeDao)) {
                                lgr.log(I2cLogger.INFO,
                                        "Sucessfully inserted Payee Name -->" +
                                        payeeVo.getPayeeName());
                            }
                        }
                    } else if (payeeDao.isPayeeAlreadyExist(payeeVo)){ // Insert Payees
                        long payeeSno = payeeDao.getPayeeSerialNo(payeeVo);
                        lgr.log(I2cLogger.FINE,
                                "Repeated Payee Name: " + payeeVo.getPayeeName() +
                                "PayeeID: " + payeeVo.getPayeeId());
                        if (updatePayeeInDB(payeeVo, payeeDao, payeeSno)){
                            lgr.log(I2cLogger.INFO,
                                    "First Time Inserted Payee is UPDATED Sucessfully " +
                                    "due to repeation -->" + payeeVo.getPayeeName());
                        }
                    } else {
                        if (insertPayeeInDb(payeeVo, payeeDao)) {
                            lgr.log(I2cLogger.INFO,
                                    "ELSE Sucessfully inserted Payee Name -->" +
                                    payeeVo.getPayeeName());
                        }
                    }
                }
            } else {
                lgr.log(I2cLogger.SEVERE,
                        "Payee XML File Contains NO Record of Payee...");
            }
        }catch(Exception ex){
          ex.printStackTrace();
          lgr.log(I2cLogger.SEVERE,
                  "PayeeMapper -- PayeePopulater--- " + ex.toString());
          throw new Exception("PayeeMapper -- PayeePopulater --- Exception during Payee Population --- " +
                  ex.getMessage());
        }
    }

    private PayeeVO comparePayee(Hashtable allPayeesTableFromDB, PayeeVO payeeFromXml) {
        lgr.log(I2cLogger.FINE,
                            "Entered in the <comparePayee>");
        PayeeVO oldPayeeVo =  (PayeeVO) allPayeesTableFromDB.get(payeeFromXml.getPayeeId());
        PayeeVO updatedPayeeVo = new PayeeVO();
        if (oldPayeeVo != null){
            if (!(oldPayeeVo.getPayeeId().equals(payeeFromXml.getPayeeId()))) {
                updatedPayeeVo.setPayeeId(payeeFromXml.getPayeeId());
                updatedPayeeVo.setUpdateFlag(true);
            }
            if (!(oldPayeeVo.getPayeeName().equals(payeeFromXml.getPayeeName()))) {
                updatedPayeeVo.setPayeeName(payeeFromXml.getPayeeName());
                updatedPayeeVo.setUpdateFlag(true);
            }
            if (!(oldPayeeVo.getPayeeRegion().equals(payeeFromXml.
                    getPayeeRegion()))) {
                updatedPayeeVo.setPayeeRegion(payeeFromXml.getPayeeRegion());
                updatedPayeeVo.setUpdateFlag(true);
            }
            if (!(oldPayeeVo.getPayeeState().equals(payeeFromXml.getPayeeState()))) {
                updatedPayeeVo.setPayeeState(payeeFromXml.getPayeeState());
                updatedPayeeVo.setUpdateFlag(true);
            }
            if (!(oldPayeeVo.getPayeeStatus().equals(payeeFromXml.
                    getPayeeStatus()))) {
                updatedPayeeVo.setPayeeStatus(payeeFromXml.getPayeeStatus());
                updatedPayeeVo.setUpdateFlag(true);
            }
            if (!(oldPayeeVo.getPayeeAddressFlag().equals(payeeFromXml.
                    getPayeeAddressFlag()))) {
                updatedPayeeVo.setPayeeAddressFlag(payeeFromXml.
                        getPayeeAddressFlag());
                updatedPayeeVo.setUpdateFlag(true);
            }
            if (oldPayeeVo.getPayeeAddress() != null &&
                payeeFromXml.getPayeeAddress() != null) {
                if (oldPayeeVo.getPayeeAddress().size() !=
                    payeeFromXml.getPayeeAddress().size()) {
                    updatedPayeeVo.setPayeeAddress(payeeFromXml.getPayeeAddress());
                    updatedPayeeVo.setUpdateFlag(true);
                } else if (compareAddresses(oldPayeeVo.getPayeeAddress(),
                                            payeeFromXml.getPayeeAddress())) {
                    updatedPayeeVo.setUpdateFlag(true);
                }
            }
            if (oldPayeeVo.getPayeePrefix() != null &&
                payeeFromXml.getPayeePrefix() != null) {
                if (oldPayeeVo.getPayeePrefix().size() !=
                    payeeFromXml.getPayeePrefix().size()) {
                    updatedPayeeVo.setPayeePrefix(payeeFromXml.getPayeePrefix());
                    updatedPayeeVo.setUpdateFlag(true);
                } else if (comparePrefixes(oldPayeeVo.getPayeePrefix(),
                                           payeeFromXml.getPayeePrefix())) {
                    updatedPayeeVo.setUpdateFlag(true);
                }
            }
            if (oldPayeeVo.getPayeeCode() != null && payeeFromXml.getPayeeCode() != null) {
                if (oldPayeeVo.getPayeeCode().size() !=
                    payeeFromXml.getPayeeCode().size()) {
                    updatedPayeeVo.setPayeeCode(payeeFromXml.getPayeeCode());
                    updatedPayeeVo.setUpdateFlag(true);
                } else if (compareCodes(oldPayeeVo.getPayeeCode(),
                                        payeeFromXml.getPayeeCode())) {
                    updatedPayeeVo.setUpdateFlag(true);
                }
            }
            if (oldPayeeVo.getPayeeCatagory() != null &&
                payeeFromXml.getPayeeCatagory() != null) {
                if (oldPayeeVo.getPayeeCatagory().size() !=
                    payeeFromXml.getPayeeCatagory().size()) {
                    updatedPayeeVo.setPayeeCatagory(payeeFromXml.
                            getPayeeCatagory());
                    updatedPayeeVo.setUpdateFlag(true);
                } else if (compareCatagory(oldPayeeVo.getPayeeCatagory(),
                                           payeeFromXml.getPayeeCatagory())) {
                    updatedPayeeVo.setUpdateFlag(true);
                }
            }
            if (updatedPayeeVo.isUpdateFlag()) {
                updatedPayeeVo.setPayeeSNo(oldPayeeVo.getPayeeSNo());
                updatedPayeeVo.setPayeeId(payeeFromXml.getPayeeId());
                updatedPayeeVo.setPayeeName(payeeFromXml.getPayeeName());
                updatedPayeeVo.setPayeeRegion(payeeFromXml.getPayeeRegion());
                updatedPayeeVo.setPayeeState(payeeFromXml.getPayeeState());
                updatedPayeeVo.setPayeeStatus(payeeFromXml.getPayeeStatus());
                updatedPayeeVo.setPayeeAddressFlag(payeeFromXml.
                        getPayeeAddressFlag());
//            updatedPayeeVo.setPayeeAddress(payeeFromXml.getPayeeAddress());
//            updatedPayeeVo.setPayeeCatagory(payeeFromXml.getPayeeCatagory());
//            updatedPayeeVo.setPayeePrefix(payeeFromXml.getPayeePrefix());
//            updatedPayeeVo.setPayeeCode(payeeFromXml.getPayeeCode());
                lgr.log(I2cLogger.INFO,
                        "comparePayee --- Payee need to be updated. Payee Name --> " +
                        updatedPayeeVo.getPayeeName());
                return updatedPayeeVo;
            }
        }
        lgr.log(I2cLogger.FINE,
                "comparePayee --- No Payee updation is required.....");
        return null;
    }

    private boolean compareAddresses(ArrayList oldAddressList, ArrayList xmlAddressList) {
        boolean changeFlag = false;
        PayeeAddressVO oldAddressVo = new PayeeAddressVO();
        PayeeAddressVO xmlAddressVo = new PayeeAddressVO();
        int counter = -1;
        if (oldAddressList.size() > xmlAddressList.size()){
            counter = oldAddressList.size();
        }else{
            counter = xmlAddressList.size();
        }
        for (int i = 0; i < counter; i++){
            oldAddressVo = (PayeeAddressVO) oldAddressList.get(i);
            xmlAddressVo = (PayeeAddressVO) xmlAddressList.get(i);
            if (!(oldAddressVo.getAddress1().equals(xmlAddressVo.getAddress1()))){
                changeFlag = true;
            }
            if (!(oldAddressVo.getAddress2().equals(xmlAddressVo.getAddress2()))){
                changeFlag = true;
            }
            if (!(oldAddressVo.getAddressCity().equals(xmlAddressVo.getAddressCity()))){
                changeFlag = true;
            }
            if (!(oldAddressVo.getAddressState().equals(xmlAddressVo.getAddressState()))){
                changeFlag = true;
            }
            if (!(oldAddressVo.getAddressZIP().equals(xmlAddressVo.getAddressZIP()))){
                changeFlag = true;
            }
            if (!(oldAddressVo.getAddressChildId().equals(xmlAddressVo.getAddressChildId()))){
                changeFlag = true;
            }
        }
        return changeFlag;
    }

    private boolean comparePrefixes(ArrayList oldPrefixList, ArrayList xmlPrefixList) {
        boolean changeFlag = false;
        PayeePrefixVO oldPrefixVo = new PayeePrefixVO();
        PayeePrefixVO xmlPrefixVo = new PayeePrefixVO();
        int counter = -1;
        if (oldPrefixList.size() > xmlPrefixList.size()){
            counter = oldPrefixList.size();
        }else{
            counter = xmlPrefixList.size();
        }
        for (int i = 0; i < counter; i++){
            oldPrefixVo = (PayeePrefixVO) oldPrefixList.get(i);
            xmlPrefixVo = (PayeePrefixVO) xmlPrefixList.get(i);
            if (!(oldPrefixVo.getPrefixMaskFrom().equals(xmlPrefixVo.getPrefixMaskFrom()))){
                changeFlag = true;
            }
            if (!(oldPrefixVo.getPrefixMaskTo().equals(xmlPrefixVo.getPrefixMaskTo()))){
                changeFlag = true;
            }
            if (!(oldPrefixVo.getPrefixLength().equals(xmlPrefixVo.getPrefixLength()))){
                changeFlag = true;
            }
            if (!(oldPrefixVo.getPrefixChildId().equals(xmlPrefixVo.getPrefixChildId()))){
                changeFlag = true;
            }
        }
        return changeFlag;
    }

    private boolean compareCodes(ArrayList oldCodeList, ArrayList xmlCodeList) {
        boolean changeFlag = false;
        PayeeCodeVO oldCodeVo = new PayeeCodeVO();
        PayeeCodeVO xmlCodeVo = new PayeeCodeVO();
        int counter = -1;
        if (oldCodeList.size() > xmlCodeList.size()){
            counter = oldCodeList.size();
        }else{
            counter = xmlCodeList.size();
        }
        for (int i = 0; i < counter; i++){
            oldCodeVo = (PayeeCodeVO) oldCodeList.get(i);
            xmlCodeVo = (PayeeCodeVO) xmlCodeList.get(i);
            if (!(oldCodeVo.getCodeMask().equals(xmlCodeVo.getCodeMask()))){
                changeFlag = true;
            }
            if (!(oldCodeVo.getCodeFieldLength().equals(xmlCodeVo.getCodeFieldLength()))){
                changeFlag = true;
            }
            if (!(oldCodeVo.getCodeChildId().equals(xmlCodeVo.getCodeChildId()))){
                changeFlag = true;
            }
        }
        return changeFlag;
    }

    private boolean compareCatagory(ArrayList oldCatagoryList, ArrayList xmlCatagoryList) {
        boolean changeFlag = false;
        PayeeCatagoryVO oldCatagoryVo = new PayeeCatagoryVO();
        PayeeCatagoryVO xmlCatagoryVo = new PayeeCatagoryVO();
        int counter = -1;
        if (oldCatagoryList.size() > xmlCatagoryList.size()){
            counter = oldCatagoryList.size();
        }else{
            counter = xmlCatagoryList.size();
        }
        for (int i = 0; i < counter; i++){
            oldCatagoryVo = (PayeeCatagoryVO) oldCatagoryList.get(i);
            xmlCatagoryVo = (PayeeCatagoryVO) xmlCatagoryList.get(i);
            if (!(oldCatagoryVo.getCatagoryName().equals(xmlCatagoryVo.getCatagoryName()))){
                changeFlag = true;
            }
            if (!(oldCatagoryVo.getCatagoryDescription().equals(xmlCatagoryVo.getCatagoryDescription()))){
                changeFlag = true;
            }
        }
        return changeFlag;
    }

    private boolean insertPayeeInDb(PayeeVO payeeVo, PayeesDAO payeeInsert){
        long payeeSerialNo = -1;
        boolean sucess = false;
        try{
            payeeSerialNo = payeeInsert.insertPayee(payeeVo);
            lgr.log(I2cLogger.INFO,
                    "Inserted Payee Serial No ---> " + payeeSerialNo);

            if (payeeVo.getPayeeAddress() != null &&
                payeeVo.getPayeeAddressFlag().equals("Y") && payeeSerialNo != -1) {
                payeeAddressInsert(payeeVo.getPayeeAddress(),
                                   payeeSerialNo);
            }

            if (payeeVo.getPayeeCatagory() != null  && payeeSerialNo != -1) {
                payeeCatagoryInsert(payeeVo.getPayeeCatagory(),
                                    payeeSerialNo);
            }

            if (payeeVo.getPayeeCode() != null && payeeSerialNo != -1) {
                payeeCodeInsert(payeeVo.getPayeeCode(), payeeSerialNo);
            }

            if (payeeVo.getPayeePrefix() != null && payeeSerialNo != -1) {
                payeePrefixInsert(payeeVo.getPayeePrefix(),
                                  payeeSerialNo);
            }
            sucess = true;
        }catch(Exception ex){
            lgr.log(I2cLogger.SEVERE,
                    "PayeeMapper -- insertPayeeInDb --- " + ex.toString());
        }
        return sucess;
    }

    private boolean updatePayeeInDB(PayeeVO payeeVo, PayeesDAO payeeInsertDao,
                                    long payeeSerialNo) {
        boolean sucess = false;
        try{
            if (payeeVo.getPayeeSNo().equals(null) || payeeVo.getPayeeSNo() == null
                    || payeeVo.getPayeeSNo().equals("")){
                payeeVo.setPayeeSNo(String.valueOf(payeeSerialNo));
            }
            payeeInsertDao.updatePayee(payeeVo);
            lgr.log(I2cLogger.FINE,
                    "PayeeMapper ---  updatePayeeInDB payee updated sucessfully..");
            if (payeeVo.getPayeeAddress() != null &&
                payeeVo.getPayeeAddressFlag().equals("Y")) {
                payeeAddressUpdate(payeeVo.getPayeeAddress(), payeeSerialNo);
            }
            if (payeeVo.getPayeeCatagory() != null) {
                payeeCatagoryUpdate(payeeVo.getPayeeCatagory(), payeeSerialNo);
            }
            if (payeeVo.getPayeeCode() != null) {
                payeeCodeUpdate(payeeVo.getPayeeCode(), payeeSerialNo);
            }
            if (payeeVo.getPayeePrefix() != null) {
                payeePrefixUpdate(payeeVo.getPayeePrefix(), payeeSerialNo);
            }
            sucess = true;
        }catch(Exception ex){
                        lgr.log(I2cLogger.SEVERE,
                    "PayeeMapper -- updatePayeeInDb --- " + ex.toString());

        }
        return sucess;
    }

    //populate AddressVO list against Payee
    public ArrayList getPayeeAddressList(BILLER payee) {
        ArrayList payeeAddressList = new ArrayList();
        PayeeAddressVO addressVo = null;
        for (int i = 0; i < payee.getADDRCount(); i++) {
            addressVo = new PayeeAddressVO();
            ADDR payeeAddress = payee.getADDR(i);
            addressVo.setAddressChildId(payeeAddress.getCHILDID().getContent());
            addressVo.setAddress1(payeeAddress.getADDR1().getContent());
            if (payeeAddress.getADDR2() != null) {
                addressVo.setAddress2(payeeAddress.getADDR2().getContent());
            }
            addressVo.setAddressCity(payeeAddress.getCITY().getContent());
            addressVo.setAddressState(payeeAddress.getSTATE().getContent());
            addressVo.setAddressZIP(payeeAddress.getZIP().getContent());
            payeeAddressList.add(addressVo);
        }
        return payeeAddressList;
    }
    // Populate Payee CatagoryVO list against payee
    public ArrayList getPayeeCatagoryList(BILLER payee) {
        ArrayList payeeCatagoryList = new ArrayList();
        PayeeCatagoryVO catagoryVo = null;
        for (int i = 0; i < payee.getCLASSCount(); i++) {
            catagoryVo = new PayeeCatagoryVO();
            CLASS payeeCatagory = payee.getCLASS(i);
            catagoryVo.setCatagoryName(payeeCatagory.getCLASSNAME().getContent());
            catagoryVo.setCatagoryDescription(payeeCatagory.getCLASSDESC().
                                              getContent());
            payeeCatagoryList.add(catagoryVo);
        }
        return payeeCatagoryList;
    }
    //Populate CodeVO list against payee
    public ArrayList getPayeeCodeList(BILLER payee) {
        ArrayList payeeMaskList = new ArrayList();
        PayeeCodeVO codeVo = null;
        for (int i = 0; i < payee.getCODECount(); i++) {
            codeVo = new PayeeCodeVO();
            CODE payeeCode = payee.getCODE(i);
            codeVo.setCodeChildId(payeeCode.getCHILDID().getContent());
            codeVo.setCodeMask(payeeCode.getCODEMASK().getContent());
            codeVo.setCodeFieldLength(payeeCode.getFLDLENGTH().getContent());
            payeeMaskList.add(codeVo);
        }
        return payeeMaskList;
    }
    // Populate PrefixVo list against payees
    public ArrayList getPayeePrefixList(BILLER payee) {
        ArrayList payeePrefixList = new ArrayList();
        PayeePrefixVO prefixVo = null;
        for (int i = 0; i < payee.getPRFXCount(); i++) {
            prefixVo = new PayeePrefixVO();
            PRFX payeePrefix = payee.getPRFX(i);
            prefixVo.setPrefixChildId(payeePrefix.getCHILDID().getContent());
            StringTokenizer prefixTokenizer = new StringTokenizer(
                    payeePrefix.getPRFXMASK().getContent(), "-");
            if (prefixTokenizer.countTokens() > 1) {
                prefixVo.setPrefixMaskFrom(prefixTokenizer.nextToken());
                prefixVo.setPrefixMaskTo(prefixTokenizer.nextToken());
            } else {
                prefixVo.setPrefixMaskFrom(prefixTokenizer.nextToken());
                prefixVo.setPrefixMaskTo(prefixVo.getPrefixMaskFrom());
            }
            prefixVo.setPrefixLength(payeePrefix.getPRFXLENGTH().getContent());
            payeePrefixList.add(prefixVo);
        }
        return payeePrefixList;
    }
    // DB insertion of address
    private void payeeAddressInsert(ArrayList payeeAddresses, long payeeSNo) {
        AddressDAO payeeAddressDao = null;
        for (int i = 0; i < payeeAddresses.size(); i++) {
            payeeAddressDao = new AddressDAO(instanceID,dbConn);
            PayeeAddressVO addressVo = (PayeeAddressVO) payeeAddresses.get(i);
            payeeAddressDao.insertAddress(addressVo, payeeSNo);
        }
    }
    //DB update of Addresses
    private void payeeAddressUpdate(ArrayList payeeAddresses, long payeeSNo) {
        AddressDAO payeeAddressDao = null;
        int updateFlag = -1;
        for (int i = 0; i < payeeAddresses.size(); i++) {
            payeeAddressDao = new AddressDAO(instanceID,dbConn);
            PayeeAddressVO addressVo = (PayeeAddressVO) payeeAddresses.get(i);
            updateFlag = payeeAddressDao.updateAddress(addressVo, payeeSNo);
            if (updateFlag == 0){
                            lgr.log(I2cLogger.SEVERE,
                    "PayeeMapper -- payeeAddressUpdate --- insertAddress for payee Sno --> "+
                payeeSNo);
                payeeAddressDao.insertAddress(addressVo, payeeSNo);
            }
        }
    }
    //DB insertion of Catagory
    private void payeeCatagoryInsert(ArrayList payeeCatagory, long payeeSNo) {
        CatagoryDAO payeeCatagoryDao = new CatagoryDAO(instanceID, dbConn);
        payeeCatagoryDao.insertCatagories(payeeSNo, payeeCatagory);
    }
    //DB updation of Catagory
    private void payeeCatagoryUpdate(ArrayList payeeCatagory, long payeeSNo) {
        CatagoryDAO payeeCatagoryDao = new CatagoryDAO(instanceID, dbConn);
        payeeCatagoryDao.updateCatagories(payeeSNo, payeeCatagory);
    }
    //DB insertion of Code
    private void payeeCodeInsert(ArrayList payeeCode, long payeeSNo) {
        CodeDAO payeeCodeDao = new CodeDAO(instanceID, dbConn);
        payeeCodeDao.insertCodes(payeeSNo, payeeCode);
    }
    //DB updation of Code
    private void payeeCodeUpdate(ArrayList payeeCode, long payeeSNo) {
        CodeDAO payeeCodeDao = new CodeDAO(instanceID, dbConn);
        payeeCodeDao.updateCodes(payeeSNo, payeeCode);
    }
    //DB insertion of Prefix
    private void payeePrefixInsert(ArrayList payeePrefix, long payeeSNo) {
        PrefixDAO payeePrefixDao = new PrefixDAO(instanceID, dbConn);
        payeePrefixDao.insertPrefixes(payeeSNo, payeePrefix);
    }
    //DB updation of Prefix
    private void payeePrefixUpdate(ArrayList payeePrefix, long payeeSNo) {
        PrefixDAO payeePrefixDao = new PrefixDAO(instanceID, dbConn);
        payeePrefixDao.updatePrefixes(payeeSNo, payeePrefix);
    }

    private PayeeVO getPayeeStateList(PayeeVO payeeVo) {
        PayeeVO tempPayeeVo = null;
        StringBuffer stateBuffer = new StringBuffer();
        /* if a single payee exist in hash */
        if (!payeeHashTable.isEmpty()){
            /* Get Payee from Hash from Payee ID */
            tempPayeeVo = (PayeeVO) payeeHashTable.get(payeeVo.getPayeeId());
            /* If payee already exist in the Hash means it is repeated for State*/
            if (tempPayeeVo != null) {
                /* Remove payee from Hash to append State List */
                payeeHashTable.remove(tempPayeeVo.getPayeeId());
                /* if Hash Payee state List is not equal to Payee from xml */
                if (!(tempPayeeVo.getPayeeState().equals(payeeVo.getPayeeState()))){
                    /* Append Payee State List */
                    stateBuffer.append(tempPayeeVo.getPayeeState() + ","
                                       + payeeVo.getPayeeState());
                    payeeVo.setPayeeState(stateBuffer.toString());
                    /* Add again in the Hash */
                    payeeHashTable.put(payeeVo.getPayeeId(), payeeVo);
                    return payeeVo;
                }
            }else{
                payeeHashTable.put(payeeVo.getPayeeId(), payeeVo);
            }
        }
        else{
            payeeHashTable.put(payeeVo.getPayeeId(), payeeVo);
        }
        return payeeVo;
    }

    /********************************************************************/
    public boolean updatePayeeInDbFromPAR(PayeeVO payeeVo,
                                   PayeesDAO payeeUpdateDao,
                                   long payeeSerialNo) throws Exception {
        boolean sucess = false;
        try{
            if (payeeVo.getPayeeSNo().equals(null) || payeeVo.getPayeeSNo() == null
                    || payeeVo.getPayeeSNo().equals("")){
                payeeVo.setPayeeSNo(String.valueOf(payeeSerialNo));
            }
            payeeUpdateDao.updatePayee(payeeVo);
            lgr.log(I2cLogger.FINE,
                    "updatePayeeInDbFromPAR payee updated sucessfully....");
            if (payeeVo.getPayeeAddress() != null &&
                payeeVo.getPayeeAddressFlag().equals("Y")) {
                payeeAddressUpdate(payeeVo.getPayeeAddress(), payeeSerialNo);
            }
            if (payeeVo.getPayeeCatagory() != null) {
                payeeCatagoryUpdate(payeeVo.getPayeeCatagory(), payeeSerialNo);
            }
            if (payeeVo.getPayeeCode() != null) {
                payeeCodeUpdate(payeeVo.getPayeeCode(), payeeSerialNo);
            }
            if (payeeVo.getPayeePrefix() != null) {
                payeePrefixUpdate(payeeVo.getPayeePrefix(), payeeSerialNo);
            }
            sucess = true;
        }catch(Exception ex){
            lgr.log(I2cLogger.SEVERE,
                    "updatePayeeInDbFromPAR --->> " + ex.toString());
            ex.printStackTrace();
            throw new Exception("updatePayeeInDbFromPAR --- Exception during UPDATION of Payee Info from PAR");
        }
        return sucess;
    }
   /********************************************************************/
//   public boolean insertPayeeInDbFromPAR(PayeeVO payeeVo, PayeesDAO payeeInsert){
//       long payeeSerialNo = -1;
//       boolean sucess = false;
//       try{
//           payeeSerialNo = payeeInsert.insertPayee(payeeVo);
//           lgr.log(I2cLogger.INFO,
//                   "Inserted Payee Serial No ---> " + payeeSerialNo);
//
//           if (payeeVo.getPayeeAddress() != null &&
//               payeeVo.getPayeeAddressFlag().equals("Y") && payeeSerialNo != -1) {
//               payeeAddressInsert(payeeVo.getPayeeAddress(),
//                                  payeeSerialNo);
//           }
//
//           if (payeeVo.getPayeeCatagory() != null  && payeeSerialNo != -1) {
//               payeeCatagoryInsert(payeeVo.getPayeeCatagory(),
//                                   payeeSerialNo);
//           }
//
//           if (payeeVo.getPayeeCode() != null && payeeSerialNo != -1) {
//               payeeCodeInsert(payeeVo.getPayeeCode(), payeeSerialNo);
//           }
//
//           if (payeeVo.getPayeePrefix() != null && payeeSerialNo != -1) {
//               payeePrefixInsert(payeeVo.getPayeePrefix(),
//                                 payeeSerialNo);
//           }
//           sucess = true;
//       }catch(Exception ex){
//           lgr.log(I2cLogger.SEVERE,
//                   "PayeeMapper -- insertPayeeInDb --- " + ex.toString());
//       }
//       return sucess;
//   }
   /********************************************************************/


}

