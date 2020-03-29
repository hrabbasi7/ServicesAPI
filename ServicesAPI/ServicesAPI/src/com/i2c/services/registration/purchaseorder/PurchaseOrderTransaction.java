package com.i2c.services.registration.purchaseorder;

import com.i2c.services.registration.base.Transaction;
import com.i2c.services.registration.base.DbRequestInfoObject;
import com.i2c.services.registration.base.DbResponseInfoObject;
import com.i2c.services.registration.base.TransactionRequestInfoObj;
import com.i2c.services.registration.base.InformationValidator;
import com.i2c.services.registration.base.TransactionResponseInfoObj;
import com.i2c.services.util.*;
import com.i2c.chauth.vo.RecordAuthInfoObj;
import java.sql.*;

/**
 * <p>Title: PurchaseOrderTransaction: A class which is  used to perfrom the purchase transaction.</p>
 * <p>Description: This class is used to execute the purchase transaction.</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class PurchaseOrderTransaction
    extends Transaction {

  private TransactionRequestInfoObj requestObj = null;
  private Connection dbConn = null;

  public PurchaseOrderTransaction(Connection dbConn,
                                  TransactionRequestInfoObj requestObj) {
    super(dbConn, requestObj);
    this.dbConn = dbConn;
    this.requestObj = requestObj;
  }

  /**
   * This method is used to upgrade the card. It first validates the following attributs, if
   * found invlalid then it pauses the execution and returns response to client with appropiate
   * response code.<br>
   * 1. All mandatory attributes of the card such as card no (If invalid response code 06 is returned) <br>
   * 2. Card Program of the given card no. (If invalid response code 06 is returned)<br>
   * 3. Validates existing card no. (If invalid response code 14 is returned) <br>
   * 4. Card status at ATM & POS (If found inactive resposne code SA is returned) <br>
   * 5. Validates switch (If invalid response code 06 is returned) <br>
   * 6. Validates switch activation (If invalid response code 91 is returned) <br>
   * 7. Validates switch batch mode (If invalid response code 40 is returned) <br>
   * 9. Validates service Id (If invalid response code 57 is returned) <br>
   * 10.Validates card parameters (If invalid response code 51 is returned)<br>
   * After validation the method calculates the service fee for the given service. If the application of the
   * service fee is allowed then it creates the DB Information object and checks whether debit is allowed and
   * the status of the OFAC & AVS. If the debit is not allowed or OFAC OR AVS status was found falsy then it
   * pauses the execution and returns the response to the client.
   * After this the method generates new card, apply the fee and returns the response.
   * @return TransactionResponseInfoObj
   */

  public TransactionResponseInfoObj processTransaction() throws Exception{
    PurchaseOrderInformationValidator validator = null;
    PurchaseOrderDataBaseHandler poDBHndlr = null;
    RecordAuthInfoObj ofacAvsResponse = null;
    DbRequestInfoObject dbDAO = null;
    DbResponseInfoObject dbResponse = new DbResponseInfoObject();
    DbResponseInfoObject dbFeeResponse = new DbResponseInfoObject();
    String switchID = null;
    String cardNumber = null;

    String serviceFee = "0.00";
    String orgTrcAudit = null;
    boolean applyFee = false;
    boolean performOFACAVS = false;

    TransactionResponseInfoObj clientResposne = new TransactionResponseInfoObj();

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Processing PurchaseOrder Transaction");

      poDBHndlr = new PurchaseOrderDataBaseHandler(dbConn);
      validator = new PurchaseOrderInformationValidator(requestObj);
      try {
          if ((requestObj.getCardPrgId() == null ||
               requestObj.getCardPrgId().trim().length() == 0) &&
              (requestObj.getCardStartNos() != null &&
               requestObj.getCardStartNos().trim().length() > 0)) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Card Program ID not given, getting card program id from provided card start nos--->" +
                                              requestObj.getCardStartNos());
              String cardPrgId = poDBHndlr.getCardProgramIDByCardStartNos(requestObj.
                      getCardStartNos());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Card Program ID got--->" +
                                              cardPrgId);
              requestObj.setCardPrgId(cardPrgId);
          }
          if (validator.validateMandatory()) {
              if (validator.checkOFACAVSMandatoryFields()) {
                  performOFACAVS = true;
              }
          }
      }catch (Exception validateExcep) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Exception in Validating Mandatory Attibutes--->" + validateExcep);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Logging Error transaction, Returning resposne...");
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER, "06",
                                               validateExcep.getMessage(), null,requestObj.getAcquirerId()
                                               ,requestObj.getCrdAceptorCode()
                                               ,requestObj.getCrdAceptorName()
                                               ,requestObj.getMerchantCatCd()
                                               ,requestObj.getRetRefNumber()
                                               ,requestObj.getDeviceId()
                                               );
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      if (!poDBHndlr.isCardProgramValid(requestObj.getCardPrgId())) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Provided Card Program Not Valid -- Logging transaction -- Returning Error response");
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER, "06",
                                               "Provided Card Program Not Valid --->" +
                                               requestObj.getCardPrgId(), null,requestObj.getAcquirerId()
                                               ,requestObj.getCrdAceptorCode()
                                               ,requestObj.getCrdAceptorName()
                                               ,requestObj.getMerchantCatCd()
                                               ,requestObj.getRetRefNumber()
                                               ,requestObj.getDeviceId()
                                               );
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting switch against the given Card Program --- Card Program ID--->" +
                                      requestObj.getCardPrgId());

      switchID = poDBHndlr.getSwitchID(requestObj.getCardPrgId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Batch Status for switch --->" +
                                      switchID);

      if (switchID == null || poDBHndlr.isBatchAllowed(switchID)) {
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER, "40",
                                               "Purchase Order service is not supported in batch mode", null,requestObj.getAcquirerId()
                                               ,requestObj.getCrdAceptorCode()
                                               ,requestObj.getCrdAceptorName()
                                               ,requestObj.getMerchantCatCd()
                                               ,requestObj.getRetRefNumber()
                                               ,requestObj.getDeviceId()
                                               );
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Active Status for switch --->" +
                                      switchID);

      if (switchID == null || !poDBHndlr.isSwitchActive(switchID)) {
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER, "91",
                                               "Switch is Inactive", null,requestObj.getAcquirerId()
                                               ,requestObj.getCrdAceptorCode()
                                               ,requestObj.getCrdAceptorName()
                                               ,requestObj.getMerchantCatCd()
                                               ,requestObj.getRetRefNumber()
                                               ,requestObj.getDeviceId()
                                               );
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Service Allowed --- Service ID--->" +
                                      Constants.PURCHASE_ORDER);
      dbDAO = new DbRequestInfoObject();

      dbDAO.setCardProgramId(requestObj.getCardPrgId());
      dbDAO.setServiceId(Constants.PURCHASE_ORDER);
      dbDAO.setAmount(requestObj.getInitialAmount());
      dbDAO.setApplyFee(requestObj.getApplyFee());

      dbResponse = poDBHndlr.checkCardProgramFee(dbDAO);

      if (dbResponse == null) {
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER, "06",
                                               "Unable to check card program fee", null,requestObj.getAcquirerId()
                                               ,requestObj.getCrdAceptorCode()
                                               ,requestObj.getCrdAceptorName()
                                               ,requestObj.getMerchantCatCd()
                                               ,requestObj.getRetRefNumber()
                                               ,requestObj.getDeviceId()
                                               );
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }
      if (!dbResponse.getResponseCode().equalsIgnoreCase(Constants.
          SUCCESS_CODE)) {
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER,
                                               dbResponse.getResponseCode(),
                                               dbResponse.getResponseDesc(),
                                               null,requestObj.getAcquirerId()
                                               ,requestObj.getCrdAceptorCode()
                                               ,requestObj.getCrdAceptorName()
                                               ,requestObj.getMerchantCatCd()
                                               ,requestObj.getRetRefNumber()
                                               ,requestObj.getDeviceId());
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      //Get Service Fee on the basis of Apply Fee flag
      if (requestObj.getApplyFee() != null &&
          requestObj.getApplyFee().equalsIgnoreCase(Constants.YES_OPTION)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Getting Service Fee --- Apply Fee--->" +
                                        requestObj.getApplyFee());
          serviceFee = dbResponse.getFeeAmount();
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Checking if Service Fee greater than 0 --- Fee --->" +
                                        serviceFee);
        applyFee = applyServiceFee(serviceFee);
      } //apply Fee == Y

      if (!poDBHndlr.isParameterAllowed(Constants.MIN_INIT_FUNDS_PARAM,
                                        requestObj.getCardPrgId(),
                                        requestObj.getInitialAmount())) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.PURCHASE_ORDER, "57",
                                               "Transfer amount does not match Initial Funds paramter value--->" + Constants.MIN_INIT_FUNDS_PARAM, null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }


      if(performOFACAVS) {

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Checking Conidtional fields for OFAC/AVS --- getSsn--->" + requestObj.getSsn()
                                        + "<---getDrivingLisenseNumber--->" + requestObj.getDrivingLisenseNumber()
                                        + "<---getDrivingLisenseState--->" + requestObj.getDrivingLisenseState()
                                        + "<---getForeignId--->" + requestObj.getForeignId()
                                        + "<---getForeignIdType--->" + requestObj.getForeignIdType()
                                        + "<---getCountryCode--->" + requestObj.getCountryCode());

        int condID = validator.checkConditionalFields();

        if(condID == 1){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Checking Conidtional fields for OFAC/AVS --- SSN is provided ---- getSsn--->" + requestObj.getSsn());

        }else if(condID == 2){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Checking Conidtional fields for OFAC/AVS --- Driving Licesnce Number & Driving Lisence State are given ---- getDrivingLisenseNumber--->" + requestObj.getDrivingLisenseNumber()
                                          + "<---getDrivingLisenseState--->" + requestObj.getDrivingLisenseState());
        }else if(condID == 3){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Checking Conidtional fields for OFAC/AVS --- USA Driving Licesnce Number is given as Foreign ID---- getForeignId--->" + requestObj.getForeignId()
                                          + "<---getForeignIdType--->" + requestObj.getForeignIdType());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Checking Conidtional fields for OFAC/AVS --- Using Foreign ID as Driving Lisence Number & State as Driving Lisence State for OFAC & AVS---- getForeignId--->" + requestObj.getForeignId()
                                          + "<---getState--->" + requestObj.getState());
          requestObj.setDrivingLisenseNumber(requestObj.getForeignId());
          requestObj.setDrivingLisenseState(requestObj.getState());
        }else{
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Checking Conidtional fields for OFAC/AVS --- Conditional fields either not provided or they are not according to above rules so service will try to perform OFAC/AVS without these...");
        }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Checking OFAC/AVS using new information");
        ofacAvsResponse = checkNewOFAC_AVS();
        if (ofacAvsResponse == null) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Verifying OFAC & AVS using new information --- No response received from API --- returning 06...");
          clientResposne = prepareClientResponse(null,
                                                 Constants.PURCHASE_ORDER, "06",
                                                 "Unable to verify OFAC/AVS status", null,requestObj.getAcquirerId()
                                               ,requestObj.getCrdAceptorCode()
                                               ,requestObj.getCrdAceptorName()
                                               ,requestObj.getMerchantCatCd()
                                               ,requestObj.getRetRefNumber()
                                               ,requestObj.getDeviceId()
                                               );
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }else if (ofacAvsResponse != null && (!ofacAvsResponse.isAvsTrue() || !ofacAvsResponse.isOfacTrue())) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "OFAC/AVS failed --- returning error response...");
          String respCode = null;
          String respDesc = null;
          if(!ofacAvsResponse.isOfacTrue()){
              respCode = "OF";
              respDesc = "OFAC Failed, <--OFAC Response Code--->" +
                                                 ofacAvsResponse.
                                                 getOfacRespCode() + "--><--OFAC Response Description--->" +
                                                 ofacAvsResponse.
                                                 getOfacDescription() + "-->";
          }else if(!ofacAvsResponse.isAvsTrue()){
              respCode = "AV";
              respDesc = "AVS Failed, <--AVS Response Code--->" +
                                   ofacAvsResponse.
                                   getAvsRespCode() + "--><--AVS Response Description--->" +
                                   ofacAvsResponse.
                                   getAvsDescription() + "-->";
          }
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "OFAC/AVS failed --- isAvsTrue ----> " +
                                          ofacAvsResponse.isAvsTrue()
                                          + "<---isOfacTrue--->" +
                                          ofacAvsResponse.isOfacTrue()
                                          + "<---getAvsDescription--->" +
                                          ofacAvsResponse.getAvsDescription()
                                          + "<---getAvsCode--->" +
                                          ofacAvsResponse.getAvsRespCode()
                                          + "<---getOfacDescription--->" +
                                          ofacAvsResponse.getOfacDescription()
                                          + "<---getOfacCode--->" +
                                          ofacAvsResponse.getOfacRespCode()
                                          +
                                          "<-----Returning Error Response------>");
          clientResposne = prepareClientResponse(null,
                                                 Constants.PURCHASE_ORDER, respCode,
                                                 respDesc, null,requestObj.getAcquirerId()
                                               ,requestObj.getCrdAceptorCode()
                                               ,requestObj.getCrdAceptorName()
                                               ,requestObj.getMerchantCatCd()
                                               ,requestObj.getRetRefNumber()
                                               ,requestObj.getDeviceId()
                                               );
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }
      }
      //Generate New Card
      try {
        cardNumber = performCardRegistrationProcessing(performOFACAVS);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Assigning card to stakeholder...");
        poDBHndlr.processCardAssignment(requestObj,cardNumber,Constants.ASSIGN_PO_DESC);
      }catch (Exception cardRegEx) {
        try{
          if (dbConn != null) {
            dbConn.rollback();
          }
        }catch(SQLException sqlex){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "SQLException in rolling back transaction --->" +
                                          sqlex);
        }
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER, "96",
                                               cardRegEx.getMessage(), null,requestObj.getAcquirerId()
                                               ,requestObj.getCrdAceptorCode()
                                               ,requestObj.getCrdAceptorName()
                                               ,requestObj.getMerchantCatCd()
                                               ,requestObj.getRetRefNumber()
                                               ,requestObj.getDeviceId()
                                               );
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }
      if(performOFACAVS){
        //Activate card here
        //if initial amount given then load also

        dbDAO = new DbRequestInfoObject();
        dbDAO.setCardNo(cardNumber);
        dbDAO.setAmount(requestObj.getInitialAmount());
        dbDAO.setDeviceType(requestObj.getDeviceType());
        dbDAO.setRetRefNumber(requestObj.getRetRefNumber());
        dbDAO.setResponseCode("00");
        if(requestObj.getInitialAmount() != null){
          dbDAO.setDescription("New Card Purchase Order,Activate Card With Load Funds Request");
        }else{
          dbDAO.setDescription("New Card Purchase Order,Activate Card Request");
        }
        dbDAO.setApplyFee(requestObj.getApplyFee());
        dbDAO.setServiceId(Constants.PURCHASE_ORDER);
        dbDAO.setAcquirerId(requestObj.getAcquirerId());
        dbDAO.setAcquirerData1(requestObj.getAcquirerData1());
        dbDAO.setAcquirerData2(requestObj.getAcquirerData2());
        dbDAO.setAcquirerData3(requestObj.getAcquirerData3());
        dbDAO.setAcquirerUserId(requestObj.getAcquirerUserId());
        dbDAO.setCrdAceptorCode(requestObj.getCrdAceptorCode());
        dbDAO.setCrdAceptorName(requestObj.getCrdAceptorName());
        dbDAO.setMerchantCatCd(requestObj.getMerchantCatCd());
        dbDAO.setDeviceId(requestObj.getDeviceId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Activating new card --- getCardNo--->" +
                                        dbDAO.getCardNo()
                                        + "<---getAmount--->" +
                                        dbDAO.getAmount()
                                        + "<---getDeviceType--->" +
                                        dbDAO.getDeviceType()
                                        + "<---getRetRefNumber--->" +
                                        dbDAO.getRetRefNumber()
                                        + "<---getResponseCode--->" +
                                        dbDAO.getResponseCode()
                                        + "<---getDescription--->" +
                                        dbDAO.getDescription()
                                        + "<---getApplyFee--->" +
                                        dbDAO.getApplyFee()
                                        + "<---getServiceId--->" +
                                        dbDAO.getServiceId()
                                        + "<---getAcquirerId--->" +
                                        dbDAO.getAcquirerId()
                                        + "<---getAcquirerData1--->" +
                                        dbDAO.getAcquirerData1()
                                        + "<---getAcquirerData2--->" +
                                        dbDAO.getAcquirerData2()
                                        + "<---getAcquirerData3--->" +
                                        dbDAO.getAcquirerData3()
                                        + "<---getAcquirerUserId--->" +
                                        dbDAO.getAcquirerUserId()
                                        + "<---getCrdAceptorCode--->" +
                                        dbDAO.getCrdAceptorCode()
                                        + "<---getCrdAceptorName--->" +
                                        dbDAO.getCrdAceptorName()
                                        + "<---getMerchantCatCd--->" +
                                        dbDAO.getMerchantCatCd()
                                        + "<---getDeviceId--->" +
                                        dbDAO.getDeviceId());

        dbResponse = poDBHndlr.activateCardInOltp(dbDAO);
        if (dbResponse == null) {
          clientResposne = prepareClientResponse(cardNumber,
                                                 Constants.PURCHASE_ORDER, "06",
                                                 "Unable to activate new card", null,
                                                 requestObj.getAcquirerId()
                                                 , requestObj.getCrdAceptorCode()
                                                 , requestObj.getCrdAceptorName()
                                                 , requestObj.getMerchantCatCd()
                                                 , requestObj.getRetRefNumber()
                                                 , requestObj.getDeviceId()
                                                 );
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }
        if (!dbResponse.getResponseCode().equalsIgnoreCase(Constants.
            SUCCESS_CODE)) {
          clientResposne = prepareClientResponse(cardNumber,
                                                 Constants.PURCHASE_ORDER,
                                                 dbResponse.getResponseCode(),
                                                 dbResponse.getResponseDesc(), null,
                                                 requestObj.getAcquirerId()
                                                 , requestObj.getCrdAceptorCode()
                                                 , requestObj.getCrdAceptorName()
                                                 , requestObj.getMerchantCatCd()
                                                 , requestObj.getRetRefNumber()
                                                 , requestObj.getDeviceId()
                                                 );
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }
        clientResposne.setResposneCode(dbResponse.getResponseCode());
        clientResposne.setResposneDescription(dbResponse.getResponseDesc());
        clientResposne.setIsoSerialNumber(dbResponse.getIsoSerialNo());
        clientResposne.setTraceAuditNumber((dbResponse.getTraceAuditNo() != null ? dbResponse.getTraceAuditNo() : poDBHndlr.getTraceAuditNo(dbResponse.getIsoSerialNo())));

        if(applyFee){
          applyFee = false;
        }
      }else{
        //Load Amount
        if (requestObj.getInitialAmount() != null &&
            requestObj.getInitialAmount().trim().length() > 0) {
          //Checking Credit Allowed on new card number
          dbDAO = new DbRequestInfoObject();

          dbDAO.setCardNo(cardNumber);
          //        dbDAO.setServiceId(Constants.CARD_UPGRADE);
          dbDAO.setAmount(requestObj.getInitialAmount());
          dbDAO.setSkipStatus("BA");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Checking Credit Allowed --- getCardNo--->" +
                                          dbDAO.getCardNo()
                                          + "<---getAmount--->" +
                                          dbDAO.getAmount()
                                          + "<---getSkipStatus--->" +
                                          dbDAO.getSkipStatus());
          dbResponse = poDBHndlr.checkCard(dbDAO);
          if (dbResponse == null) {
            clientResposne = prepareClientResponse(cardNumber,
                Constants.PURCHASE_ORDER, "06",
                "Unable to verify credit allowed", null,
                requestObj.getAcquirerId()
                , requestObj.getCrdAceptorCode()
                , requestObj.getCrdAceptorName()
                , requestObj.getMerchantCatCd()
                , requestObj.getRetRefNumber()
                , requestObj.getDeviceId()
                );
            clientResposne.setFeeAmount("0.00");
            return clientResposne;
          }
          if (!dbResponse.getResponseCode().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            clientResposne = prepareClientResponse(cardNumber,
                Constants.PURCHASE_ORDER,
                dbResponse.getResponseCode(),
                dbResponse.getResponseDesc(), null, requestObj.getAcquirerId()
                , requestObj.getCrdAceptorCode()
                , requestObj.getCrdAceptorName()
                , requestObj.getMerchantCatCd()
                , requestObj.getRetRefNumber()
                , requestObj.getDeviceId()
                );
            clientResposne.setFeeAmount("0.00");
            return clientResposne;
          }

          dbDAO = new DbRequestInfoObject();
          dbDAO.setCardNo(cardNumber);
          dbDAO.setAmount(requestObj.getInitialAmount());
          dbDAO.setDeviceType(requestObj.getDeviceType());
          dbDAO.setRetRefNumber(requestObj.getRetRefNumber());
          dbDAO.setResponseCode("00");
          dbDAO.setDescription("New Card Purchase Order,Loading initial amount");
          dbDAO.setApplyFee(requestObj.getApplyFee());
          dbDAO.setServiceId(Constants.PURCHASE_ORDER);
          dbDAO.setAcquirerId(requestObj.getAcquirerId());
          dbDAO.setAcquirerData1(requestObj.getAcquirerData1());
          dbDAO.setAcquirerData2(requestObj.getAcquirerData2());
          dbDAO.setAcquirerData3(requestObj.getAcquirerData3());
          dbDAO.setAcquirerUserId(requestObj.getAcquirerUserId());
          dbDAO.setCrdAceptorCode(requestObj.getCrdAceptorCode());
          dbDAO.setCrdAceptorName(requestObj.getCrdAceptorName());
          dbDAO.setMerchantCatCd(requestObj.getMerchantCatCd());
          dbDAO.setDeviceId(requestObj.getDeviceId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Crediting funds on new card --- getCardNo--->" +
                                          dbDAO.getCardNo()
                                          + "<---getAmount--->" +
                                          dbDAO.getAmount()
                                          + "<---getDeviceType--->" +
                                          dbDAO.getDeviceType()
                                          + "<---getRetRefNumber--->" +
                                          dbDAO.getRetRefNumber()
                                          + "<---getResponseCode--->" +
                                          dbDAO.getResponseCode()
                                          + "<---getDescription--->" +
                                          dbDAO.getDescription()
                                          + "<---getApplyFee--->" +
                                          dbDAO.getApplyFee()
                                          + "<---getServiceId--->" +
                                          dbDAO.getServiceId()
                                          + "<---getAcquirerId--->" +
                                          dbDAO.getAcquirerId()
                                          + "<---getAcquirerData1--->" +
                                          dbDAO.getAcquirerData1()
                                          + "<---getAcquirerData2--->" +
                                          dbDAO.getAcquirerData2()
                                          + "<---getAcquirerData3--->" +
                                          dbDAO.getAcquirerData3()
                                          + "<---getAcquirerUserId--->" +
                                          dbDAO.getAcquirerUserId()
                                          + "<---getCrdAceptorCode--->" +
                                          dbDAO.getCrdAceptorCode()
                                          + "<---getCrdAceptorName--->" +
                                          dbDAO.getCrdAceptorName()
                                          + "<---getMerchantCatCd--->" +
                                          dbDAO.getMerchantCatCd()
                                          + "<---getDeviceId--->" +
                                          dbDAO.getDeviceId());

          dbResponse = poDBHndlr.creditFundsAtOltp(dbDAO);

          if (dbResponse == null) {
            clientResposne = prepareClientResponse(cardNumber,
                Constants.PURCHASE_ORDER, "06",
                "Unable to credit funds", null, requestObj.getAcquirerId()
                , requestObj.getCrdAceptorCode()
                , requestObj.getCrdAceptorName()
                , requestObj.getMerchantCatCd()
                , requestObj.getRetRefNumber()
                , requestObj.getDeviceId()
                );
            clientResposne.setFeeAmount("0.00");
            return clientResposne;
          }
          if (!dbResponse.getResponseCode().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            clientResposne = prepareClientResponse(cardNumber,
                Constants.PURCHASE_ORDER,
                dbResponse.getResponseCode(),
                dbResponse.getResponseDesc(), null, requestObj.getAcquirerId()
                , requestObj.getCrdAceptorCode()
                , requestObj.getCrdAceptorName()
                , requestObj.getMerchantCatCd()
                , requestObj.getRetRefNumber()
                , requestObj.getDeviceId()
                );
            clientResposne.setFeeAmount("0.00");
            return clientResposne;
          }
          clientResposne.setResposneCode(dbResponse.getResponseCode());
          clientResposne.setResposneDescription(dbResponse.getResponseDesc());
          clientResposne.setIsoSerialNumber(dbResponse.getIsoSerialNo());
          clientResposne.setTraceAuditNumber((dbResponse.getTraceAuditNo() != null ? dbResponse.getTraceAuditNo() : poDBHndlr.getTraceAuditNo(dbResponse.getIsoSerialNo())));
          if(applyFee){
            applyFee = false;
          }
        }else{
          clientResposne = prepareClientResponse(cardNumber,
                                                 Constants.PURCHASE_ORDER, "00",
                                                 "New Card Purchase Order Completed Successfully--->" +
                                                 poDBHndlr.maskCardNo(cardNumber), null,requestObj.getAcquirerId()
                                                 ,requestObj.getCrdAceptorCode()
                                                 ,requestObj.getCrdAceptorName()
                                                 ,requestObj.getMerchantCatCd()
                                                 ,requestObj.getRetRefNumber()
                                                 ,requestObj.getDeviceId()
                                                 );
          orgTrcAudit = clientResposne.getTraceAuditNumber();
        }
      }

      //Apply Fee
      if (applyFee) {
        dbDAO = new DbRequestInfoObject();
        dbDAO.setCardNo(cardNumber);//applying fee on new card
        dbDAO.setServiceId(Constants.PURCHASE_ORDER);
        dbDAO.setDeviceType(requestObj.getDeviceType());
        dbDAO.setFeeAmount(serviceFee);
        dbDAO.setRetRefNumber(requestObj.getRetRefNumber());
        dbDAO.setResponseCode("00");
        dbDAO.setDescription("New Card Purchase Order Fee");
        dbDAO.setApplyFee(requestObj.getApplyFee());
        dbDAO.setAcquirerId(requestObj.getAcquirerId());
        dbDAO.setAcquirerData1(requestObj.getAcquirerData1());
        dbDAO.setAcquirerData2(requestObj.getAcquirerData2());
        dbDAO.setAcquirerData3(requestObj.getAcquirerData3());
        dbDAO.setAcquirerUserId(requestObj.getAcquirerUserId());
        dbDAO.setCrdAceptorCode(requestObj.getCrdAceptorCode());
        dbDAO.setCrdAceptorName(requestObj.getCrdAceptorName());
        dbDAO.setMerchantCatCd(requestObj.getMerchantCatCd());
        dbDAO.setDeviceId(requestObj.getDeviceId());
        dbDAO.setOrgTraceAudit(orgTrcAudit);

        dbFeeResponse = debitFee(dbDAO,
                              poDBHndlr);
        if (dbFeeResponse != null &&
            !dbFeeResponse.getResponseCode().
            equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          clientResposne = prepareClientResponse(cardNumber,
                                                 Constants.PURCHASE_ORDER,
                                                 dbFeeResponse.getResponseCode(),
                                                 "Unable to debit service fee-->-->" + dbFeeResponse.getResponseDesc(), null,requestObj.getAcquirerId()
                                               ,requestObj.getCrdAceptorCode()
                                               ,requestObj.getCrdAceptorName()
                                               ,requestObj.getMerchantCatCd()
                                               ,requestObj.getRetRefNumber()
                                               ,requestObj.getDeviceId()
                                               );
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }
      }
      //Prepare success response

//      if(dbResponse.getResponseCode() != null && dbResponse.getResponseCode().trim().length() > 0){
//        clientResposne.setResposneCode(dbResponse.getResponseCode());
//        clientResposne.setResposneDescription(dbResponse.getResponseDesc());
//        clientResposne.setIsoSerialNumber(dbResponse.getIsoSerialNo());
//        clientResposne.setTraceAuditNumber((dbResponse.getTraceAuditNo() != null ? dbResponse.getTraceAuditNo() : poDBHndlr.getTraceAuditNo(dbResponse.getIsoSerialNo())));
//        //         clientResposne
//      }else{
//        clientResposne = prepareClientResponse(cardNumber,
//                                               Constants.PURCHASE_ORDER, "00",
//                                               "New Card Purchase Order Completed Successfully--->" +
//                                               poDBHndlr.maskCardNo(cardNumber), null,requestObj.getAcquirerId()
//                                               ,requestObj.getCrdAceptorCode()
//                                               ,requestObj.getCrdAceptorName()
//                                               ,requestObj.getMerchantCatCd()
//                                               ,requestObj.getRetRefNumber()
//                                               ,requestObj.getDeviceId()
//                                               );
//      }
      clientResposne.setRefernceId(poDBHndlr.buildRefernceId(clientResposne.getIsoSerialNumber()));
      clientResposne.setFeeAmount(serviceFee);
      clientResposne.setCardBalance(poDBHndlr.getCardBalance(cardNumber));
      clientResposne.setNewCardNumber(cardNumber);
    }catch (Exception ex) {
      try{
        if (dbConn != null) {
          dbConn.rollback();
        }
      }catch(SQLException sqlex){
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "SQLException in rolling back transaction --->" +
                                        sqlex);
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in processing of Purcahse Order transaction--->" +
                                      ex);
      clientResposne = prepareClientResponse(cardNumber,
                                             Constants.PURCHASE_ORDER, "96",
                                             "System Malfunction--->" + ex + "<---Stack Trace--->" + CommonUtilities.getStackTrace(ex), null,requestObj.getAcquirerId()
                                             ,requestObj.getCrdAceptorCode()
                                             ,requestObj.getCrdAceptorName()
                                             ,requestObj.getMerchantCatCd()
                                             ,requestObj.getRetRefNumber()
                                             ,requestObj.getDeviceId());
    }finally{
      if(performOFACAVS && clientResposne.getTraceAuditNumber() != null && ofacAvsResponse != null && ofacAvsResponse.getTransId() != null){
        poDBHndlr.updateOfac_AvsTraceAuditNo(clientResposne.
                                             getTraceAuditNumber(),
                                             ofacAvsResponse.getTransId());
      }
    }
    return clientResposne;
  }

  /**
   * This method is used to register the card or to generate a new card. After registration or regeneration
   * of the card it updates the card holder profile and returns the new card no.
   * @throws Exception
   * @return String
   */
  private String performCardRegistrationProcessing(boolean updProfile) throws Exception {
    String cardNumber = null;
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Calling method for card generation");
    requestObj.setCardGenType(Constants.NEW_CARDGEN_PUR_ORDER);
    cardNumber = generateNewCard();
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Card Number generated--->" + cardNumber);
    if (cardNumber == null || cardNumber.trim().length() == 0) {
      throw new Exception("Invalid New Card Number generated");
    }
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Calling method for updating profile for card--->" +
                                    cardNumber);
    if(updProfile){
      updateCardHolderProfile(cardNumber);
    }else{
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "No Profile information given for new card Number--->" +
                                      cardNumber + "<---Update Profile Flag-->" + updProfile);
    }
    return cardNumber;
  }

  /**
   * A private method which is used to debit the fee from the given card account funds. It deducts the
   * service fee from  the given card no.
   * @param serviceFee String
   * @param poDBHndlr PurchaseOrderDataBaseHandler
   * @return DbResponseInfoObject
   */

  private DbResponseInfoObject debitFee(DbRequestInfoObject dbDAO,
                                        PurchaseOrderDataBaseHandler poDBHndlr) throws Exception{
    DbResponseInfoObject dbResponse = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Applying Fee on Card --- getCardNo--->" +
                                      dbDAO.getCardNo()
                                      + "<---getServiceId--->" +
                                      dbDAO.getServiceId()
                                      + "<---getDeviceType--->" +
                                      dbDAO.getDeviceType()
                                      + "<---getFeeAmount--->" +
                                      dbDAO.getFeeAmount()
                                      + "<---getRetRefNumber--->" +
                                      dbDAO.getRetRefNumber()
                                      + "<---getResponseCode--->" +
                                      dbDAO.getResponseCode()
                                      + "<---getDescription--->" +
                                      dbDAO.getDescription()
                                      + "<---getApplyFee--->" +
                                      dbDAO.getApplyFee()
                                      + "<---getAcquirerId--->" +
                                      dbDAO.getAcquirerId()
                                      + "<---getAcquirerData1--->" +
                                      dbDAO.getAcquirerData1()
                                      + "<---getAcquirerData2--->" +
                                      dbDAO.getAcquirerData2()
                                      + "<---getAcquirerData3--->" +
                                      dbDAO.getAcquirerData3()
                                      + "<---getAcquirerUserId--->" +
                                      dbDAO.getAcquirerUserId()
                                      + "<---getCrdAceptorCode--->" +
                                      dbDAO.getCrdAceptorCode()
                                      + "<---getCrdAceptorName--->" +
                                      dbDAO.getCrdAceptorName()
                                      + "<---getMerchantCatCd--->" +
                                      dbDAO.getMerchantCatCd()
                                      + "<---getDeviceId--->" +
                                      dbDAO.getDeviceId()
                                      + "<---getOrgTraceAudit--->" +
                                      dbDAO.getOrgTraceAudit());

      dbResponse = poDBHndlr.debitServiceFee(dbDAO);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Response Received ---- Response Code--->" +
                                      dbResponse.getResponseCode()
                                      + "<---Response Description--->" +
                                      dbResponse.getResponseDesc()
                                      + "<---Trace Audtit No--->" +
                                      dbResponse.getTraceAuditNo()
                                      + "<---ISO Serial No--->" +
                                      dbResponse.getIsoSerialNo()
                                      + "<---Remaining Balance--->" +
                                      dbResponse.getRemainingBal());
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception while debitting service fee--->" +
                                      ex);
      throw ex;
    }
    return dbResponse;
  }

  protected void updateCardHolderProfile(String cardNumber) throws Exception{

    try {
      PurchaseOrderDataBaseHandler poDBHndlr = new PurchaseOrderDataBaseHandler(dbConn);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Updating Card Holder Profile for card Number--->" +
                                      cardNumber);
      requestObj.setOfacStatus(Constants.OFAC_AVS_OK);
      requestObj.setAvsStatus(Constants.OFAC_AVS_OK);
      poDBHndlr.updateCardHolderProfile(requestObj,cardNumber);
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Updating Card Holder Profile for card Number--->" +
                                      ex);
      throw ex;
    }
  }


}
