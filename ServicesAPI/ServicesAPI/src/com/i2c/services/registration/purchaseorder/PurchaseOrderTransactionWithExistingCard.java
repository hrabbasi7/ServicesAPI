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

public class PurchaseOrderTransactionWithExistingCard
    extends Transaction {

  private TransactionRequestInfoObj requestObj = null;
  private Connection dbConn = null;

  public PurchaseOrderTransactionWithExistingCard(Connection dbConn,
                                                  TransactionRequestInfoObj
                                                  requestObj) {
    super(dbConn, requestObj);
    this.dbConn = dbConn;
    this.requestObj = requestObj;
  }

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
    boolean applyFee = false;

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
              String cardPrgId = poDBHndlr.getCardProgramIDByCardStartNos(
                      requestObj.
                      getCardStartNos());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Card Program ID got--->" +
                                              cardPrgId);
              requestObj.setCardPrgId(cardPrgId);
          }

          if (validator.validateMandatory()) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Mandatory fields present");
          }
      }
      catch (Exception validateExcep) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Exception in Validating Mandatory Attibutes--->" +
                                        validateExcep);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Logging Error transaction, Returning resposne...");
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER, "06",
                                               validateExcep.getMessage(), null,
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

      if (!poDBHndlr.isCardProgramValid(requestObj.getCardPrgId())) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Provided Card Program Not Valid -- Logging transaction -- Returning Error response");
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER, "06",
                                               "Provided Card Program Not Valid --->" +
                                               requestObj.getCardPrgId(), null,
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
                                               "Purchase Order service is not supported in batch mode", null,
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

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Active Status for switch --->" +
                                      switchID);

      if (switchID == null || !poDBHndlr.isSwitchActive(switchID)) {
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER, "91",
                                               "Switch is Inactive", null,
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
                                               "Unable to check card program fee", null,
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
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER,
                                               dbResponse.getResponseCode(),
                                               dbResponse.getResponseDesc(),
                                               null, requestObj.getAcquirerId()
                                               , requestObj.getCrdAceptorCode()
                                               , requestObj.getCrdAceptorName()
                                               , requestObj.getMerchantCatCd()
                                               , requestObj.getRetRefNumber()
                                               , requestObj.getDeviceId());
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

      int status = checkExistingOFAC_AVS();
      if (status == Constants.OFAC_FAILED) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "OFAC for Existing Card Number Failed -- Logging transaction -- Returning Error response");
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.PURCHASE_ORDER, "OF",
                                               "OFAC for Existing Card Number is Failed in database", null,
                                               requestObj.getAcquirerId()
                                               , requestObj.getCrdAceptorCode()
                                               , requestObj.getCrdAceptorName()
                                               , requestObj.getMerchantCatCd()
                                               , requestObj.getRetRefNumber()
                                               , requestObj.getDeviceId()
                                               );
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      } else if (status == Constants.AVS_FAILED) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "AVS for Existing Card Number Failed -- Logging transaction -- Returning Error response");
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.PURCHASE_ORDER, "AV",
                                               "AVS for Existing Card Number is Failed in database", null,
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


      //Generate New Card
      try {
        cardNumber = performCardRegistrationProcessing();
      }
      catch (Exception cardRegEx) {
        clientResposne = prepareClientResponse(null,
                                               Constants.PURCHASE_ORDER, "06",
                                               cardRegEx.getMessage(), null,
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

      dbDAO = new DbRequestInfoObject();
      dbDAO.setCardNo(cardNumber);
      dbDAO.setAmount(requestObj.getInitialAmount());
      dbDAO.setDeviceType(requestObj.getDeviceType());
      dbDAO.setRetRefNumber(requestObj.getRetRefNumber());
      dbDAO.setResponseCode("00");
      if (requestObj.getInitialAmount() != null) {
        dbDAO.setDescription("New Card Purchase Order,Activate Card With Load Funds Request");
      }
      else {
        dbDAO.setDescription(
            "New Card Purchase Order,Activate Card Request");
      }
      dbDAO.setApplyFee("N");
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

      //Apply Fee
      if (applyFee) {
        dbDAO = new DbRequestInfoObject();
        dbDAO.setCardNo(cardNumber); //applying fee on new card
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

        dbFeeResponse = debitFee(dbDAO,
                                 poDBHndlr);
        if (dbFeeResponse != null &&
            !dbFeeResponse.getResponseCode().
            equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          clientResposne = prepareClientResponse(cardNumber,
                                                 Constants.PURCHASE_ORDER,
                                                 dbFeeResponse.getResponseCode(),
                                                 "Unable to debit service fee-->-->" +
                                                 dbFeeResponse.getResponseDesc(), null,
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
      }
      //Prepare success response

      if (dbResponse != null && dbResponse.getResponseCode() != null &&
          dbResponse.getResponseCode().trim().length() > 0) {
        clientResposne.setResposneCode(dbResponse.getResponseCode());
        clientResposne.setResposneDescription(dbResponse.getResponseDesc());
        clientResposne.setIsoSerialNumber(dbResponse.getIsoSerialNo());
        clientResposne.setTraceAuditNumber( (dbResponse.getTraceAuditNo() != null ?
                                             dbResponse.getTraceAuditNo() :
                                             poDBHndlr.getTraceAuditNo(
            dbResponse.getIsoSerialNo())));
      }else {
        clientResposne = prepareClientResponse(cardNumber,
                                               Constants.PURCHASE_ORDER, "00",
                                               "New Card Purchase Order Completed Successfully--->" +
                                               poDBHndlr.maskCardNo(cardNumber), null,
                                               requestObj.getAcquirerId()
                                               , requestObj.getCrdAceptorCode()
                                               , requestObj.getCrdAceptorName()
                                               , requestObj.getMerchantCatCd()
                                               , requestObj.getRetRefNumber()
                                               , requestObj.getDeviceId()
                                               );
      }
      clientResposne.setRefernceId(poDBHndlr.buildRefernceId(clientResposne.getIsoSerialNumber()));
      clientResposne.setFeeAmount(serviceFee);
      clientResposne.setCardBalance(poDBHndlr.getCardBalance(cardNumber));
      clientResposne.setNewCardNumber(cardNumber);
    }
    catch (Exception ex) {
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
    }
    return clientResposne;
  }

  /**
   * This method is used to register the card or to generate a new card. After registration or regeneration
   * of the card it updates the card holder profile and returns the new card no.
   * @throws Exception
   * @return String
   */
  private String performCardRegistrationProcessing() throws
      Exception {
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
                                    "Updating profile for card using existing card profile --- New Card Number--->" +
                                    cardNumber);
    updateExistingCardHolderProfile(cardNumber);
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
                                        PurchaseOrderDataBaseHandler poDBHndlr) {
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
                                      dbDAO.getDeviceId());

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
    }
    return dbResponse;
  }

}
