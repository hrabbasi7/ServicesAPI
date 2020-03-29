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

public class NewCardPOFundsFromOldCard
    extends Transaction {

  private TransactionRequestInfoObj requestObj = null;
  private Connection dbConn = null;

  public NewCardPOFundsFromOldCard(Connection dbConn,
                                                  TransactionRequestInfoObj
                                                  requestObj) {
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
    PurchaseOrderDataBaseHandler poDBHndlr = null;
    PurchaseOrderInformationValidator validator = null;
    DbRequestInfoObject dbDAO = null;
    DbResponseInfoObject dbResponse = new DbResponseInfoObject();
    DbResponseInfoObject dbFeeResponse = new DbResponseInfoObject();
    String switchID = null;
    String cardNumber = null;
    String oldCardProgram = null;

    String serviceFee = "0.00";
    boolean applyFee = false;

    TransactionResponseInfoObj clientResposne = new TransactionResponseInfoObj();

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Processing PurchaseOrder Transaction with Existing Card given --->" +
                                      requestObj.getExistingCard() + "<--Funds From Old Card Flag-->" + requestObj.isFundsFromOldCard());

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
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Mandatory fields present");
          }
      } catch (Exception validateExcep) {
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

      dbDAO = new DbRequestInfoObject();
      dbDAO.setCardNo(requestObj.getExistingCard());
      dbDAO.setSkipStatus("B");
      dbDAO.setServiceId(Constants.PURCHASE_ORDER);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Checking Existing card --- getCardNo--->" +
                                      dbDAO.getCardNo()
                                      + "<---getSkipStatus--->" +
                                      dbDAO.getSkipStatus());
      dbResponse = poDBHndlr.checkCard(dbDAO);
      if (dbResponse == null) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.PURCHASE_ORDER, "06",
                                               "Unable to verify existing card", null,
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
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
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

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Card program of exisiting card--->" +
                                      requestObj.getExistingCard());
      oldCardProgram = poDBHndlr.getCardProgramID(requestObj.getExistingCard());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Card program of exisiting card--->" +
                                      oldCardProgram);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting switch against the given Card Program --- Card Program ID--->" +
                                      oldCardProgram);

      switchID = poDBHndlr.getSwitchID(oldCardProgram);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Batch Status for switch --->" +
                                      switchID);

      if (switchID == null || poDBHndlr.isBatchAllowed(switchID)) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
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
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
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

      if (requestObj.getApplyFee() != null &&
          requestObj.getApplyFee().equalsIgnoreCase(Constants.YES_OPTION)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Getting Service Fee --- Apply Fee--->" +
                                        requestObj.getApplyFee());

        String[] serviceFeeList = poDBHndlr.getServiceFee(Constants.
            PURCHASE_ORDER, oldCardProgram, null);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Service Fee List Contents ---- Service Fee--->" +
                                        serviceFeeList[0] +
                                        "<---Return Code--->" +
                                        serviceFeeList[1] +
                                        "<---Description--->" +
                                        serviceFeeList[2]);
        if (serviceFeeList[1] == null) { //No response code recived from API
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.PURCHASE_ORDER, "06",
                                                 "Invalid Service Fee", null,
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
        else if (serviceFeeList[1] != null &&
                 !serviceFeeList[1].equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.PURCHASE_ORDER,
                                                 serviceFeeList[1],
                                                 serviceFeeList[2], null,
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
        else {
          serviceFee = serviceFeeList[0];
        }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Checking if Service Fee greater than 0 --- Fee --->" +
                                        serviceFee);
        applyFee = applyServiceFee(serviceFee);
      } //apply Fee == Y

      dbDAO = new DbRequestInfoObject();

      dbDAO.setCardNo(requestObj.getExistingCard());
      dbDAO.setServiceId(Constants.PURCHASE_ORDER);
      dbDAO.setAmount(requestObj.getInitialAmount());
      dbDAO.setSkipStatus("B");
      dbDAO.setIsOKNegBal("Y");
      dbDAO.setChkTransAmt("Y");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Checking Debit Allowed --- getCardNo--->" +
                                      dbDAO.getCardNo()
                                      + "<---getServiceId--->" +
                                      dbDAO.getServiceId()
                                      + "<---getAmount--->" +
                                      dbDAO.getAmount()
                                      + "<---getSkipStatus--->" +
                                      dbDAO.getSkipStatus()
                                      + "<---getIsOKNegBal--->" +
                                      dbDAO.getIsOKNegBal()
                                      + "<---getChkTransAmt--->" +
                                      dbDAO.getChkTransAmt());

      dbResponse = poDBHndlr.checkDebitAllowed(dbDAO);
      if (dbResponse == null) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.PURCHASE_ORDER, "06",
                                               "Unable to verify debit allowed", null,
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
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
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


      //If Existing Card is valid, then check its OFAC_AVS
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
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
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
      if(requestObj.getInitialAmount() != null && requestObj.getInitialAmount().trim().length() > 0){
        //Load Amount
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG), "Loading Amount on new Card --- CardNo--->" + cardNumber + "<---Amount--->" + requestObj.getInitialAmount());

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
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
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
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
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

        dbDAO = new DbRequestInfoObject();

        dbDAO.setCardNo(requestObj.getExistingCard());
        dbDAO.setToCardNumber(cardNumber);
        dbDAO.setAmount(requestObj.getInitialAmount());
        dbDAO.setServiceId(Constants.PURCHASE_ORDER);
        dbDAO.setDeviceType(requestObj.getDeviceType());
        dbDAO.setFeeAmount(serviceFee);
        dbDAO.setApplyFee(requestObj.getApplyFee());
        dbDAO.setIsAllCashOut(false);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Attempting to Transfer requested Amount --- From Card--->" +
                                        dbDAO.getCardNo()
                                        + "<---To Card Number--->" +
                                        dbDAO.getToCardNumber()
                                        + "<---Amount--->" + dbDAO.getAmount()
                                        + "<---ServiceId--->" +
                                        dbDAO.getServiceId()
                                        + "<---FeeAmount--->" +
                                        dbDAO.getFeeAmount()
                                        + "<---ApplyFee--->" +
                                        dbDAO.getApplyFee()
                                        + "<---DeviceType--->" +
                                        dbDAO.getDeviceType()
                                        + "<---isALL Cash out--->" +
                                        dbDAO.isIsAllCashOut());

        dbResponse = poDBHndlr.transferAmount(dbDAO);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Received ---- Response Code--->" +
                                        dbResponse.getResponseCode()
                                        + "<---Response Description--->" +
                                        dbResponse.getResponseDesc()
                                        + "<---Trace Audit No--->" +
                                        dbResponse.getTraceAuditNo()
                                        + "<---Trace Audit No To Card--->" +
                                        dbResponse.getTraceAuditNoToCard()
                                        + "<---Fee Amount--->" +
                                        dbResponse.getFeeAmount()
                                        + "<---Card Balance--->" +
                                        dbResponse.getCardBalance()
                                        + "<---New Card Balance--->" +
                                        dbResponse.getNewCardBalance());
        clientResposne.setResposneCode(dbResponse.getResponseCode());
        clientResposne.setResposneDescription(dbResponse.getResponseDesc());
        clientResposne.setTraceAuditNumber(dbResponse.getTraceAuditNo());
        clientResposne.setTraceAuditNumberToCard(dbResponse.getTraceAuditNoToCard());
        clientResposne.setFeeAmount(dbResponse.getFeeAmount());
        clientResposne.setCardBalance(dbResponse.getCardBalance());
        clientResposne.setCurrentBalance(dbResponse.getNewCardBalance());
      }else if (applyFee) {
        dbDAO = new DbRequestInfoObject();
        dbDAO.setCardNo(requestObj.getExistingCard()); //applying fee on old card
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
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
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
      if(requestObj.getInitialAmount() != null && requestObj.getInitialAmount().trim().length() > 0){
        clientResposne.setResposneCode(dbResponse.getResponseCode());
        clientResposne.setResposneDescription(dbResponse.getResponseDesc());
        clientResposne.setTraceAuditNumber(dbResponse.getTraceAuditNo());
        clientResposne.setIsoSerialNumber(dbResponse.getIsoSerialNo());
      }else{
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
      clientResposne.setFeeAmount(serviceFee);
      clientResposne.setCardBalance(poDBHndlr.getCardBalance(cardNumber));
      clientResposne.setNewCardNumber(cardNumber);

    }catch (Exception ex) {
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
