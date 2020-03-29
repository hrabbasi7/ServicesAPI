package com.i2c.services.registration.reissuecard;

import com.i2c.services.registration.base.Transaction;
import com.i2c.services.registration.base.TransactionRequestInfoObj;
import com.i2c.services.registration.base.DbRequestInfoObject;
import com.i2c.services.registration.base.DbResponseInfoObject;
import com.i2c.services.registration.base.TransactionResponseInfoObj;

import com.i2c.services.util.*;
import java.sql.*;

/**
* <p>Title:CardReissueType3Transaction: This class is used to process the reissue card transaction </p>
 * <p>Description: This class provides the funcationality for updating the card attributes</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class CardReissueType3Transaction extends Transaction {

  private TransactionRequestInfoObj requestObj = null;
  private Connection dbConn = null;

  /**
   * Constructor which initlizes the database connection and request object.
   * @param dbConn Connection
   * @param requestObj TransactionRequestInfoObj
   */
  public CardReissueType3Transaction(Connection dbConn,
                                     TransactionRequestInfoObj requestObj) {
    super(dbConn, requestObj);
    this.dbConn = dbConn;
    this.requestObj = requestObj;
  }


  /**
    * This method is used to update the card attributes. It first validates the mandatory attributes
    * and returns response with appropiate error response code in the following cases <br>
    * 1. Mandatory attributes for the re-issue card (reponse code 06 is reutned if invalidate)<br>
    * 2. Validate card no. (response code 14 is returned if invalid card no)
    * 3. Card Activation & Pre-activation (response code SA returned if card is inactive)
    * 4. Swtich Activation and Online verification (response code 40,95 reutned if card is Inactive)
    * 5. Service allowed (response code 57 returned if service is not allowed)
    * 6. Calculation of the Service Fee
    * If the application of the service fee is allowed then it checks whether the debit is allowed
    * for the given card if it is then the method debits the funds from the given card funds,
    * updates the card holder profile and returns response to the client.
    * @return TransactionResponseInfoObj
   */
  public TransactionResponseInfoObj processTransaction() {
    String newCardNumber = null;
    CardReissueInformationValidator validator = null;
    CardReissueDataBaseHandler crDBHndlr = null;
    DbRequestInfoObject dbDAO = new DbRequestInfoObject();
    DbResponseInfoObject dbResponse = new DbResponseInfoObject();
    TransactionResponseInfoObj clientResposne = new TransactionResponseInfoObj();

    String cardProgram = null;
    String switchID = null;

    String serviceFee = null;
    String oldCardBalance = null;

    boolean applyFee = false;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Processing Card Reissue Transaction --- Reissue Type--->" +
                                      requestObj.getReissueType());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating Mandatory Attibutes");
      validator = new CardReissueInformationValidator(requestObj);
      try {
        //validating mandatory fields for card upgrade
        validator.validateMandatory();
      }
      catch (Exception validateExcep) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Exception in Validating Mandatory Attibutes--->" +
                                        validateExcep);
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "06",
                                               validateExcep.getMessage(), null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;

      }

      //----------------Performing Database related Validation of Request Attributes----------//
      crDBHndlr = new CardReissueDataBaseHandler(dbConn);

      //Checking Exisiting Card valid
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating exisiting card--->" +
                                      requestObj.getExistingCard());
      if (!crDBHndlr.isCardNumberValid(requestObj.getExistingCard())) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                       Constants.CARD_REISSUE, "14",
                                                       "Existing Card Number Not Valid--->" + crDBHndlr.maskCardNo(requestObj.getExistingCard()), null);
                clientResposne.setFeeAmount("0.00");
                return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting ATM/POS status for exisiting card--->" +
                                      requestObj.getExistingCard());

      crDBHndlr.getCardStatuses(requestObj);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Got ATM/POS status for exisiting card --- ATM Status--->" +
                                      requestObj.getAtmStatus() + "<---POS Status--->" + requestObj.getPosStatus());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking exisiting card Active--->" +
                                      requestObj.getExistingCard());

      if (!crDBHndlr.isExistingCardNumberActive(requestObj.getAtmStatus(),requestObj.getPosStatus())) {

        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "SA",
                                               "Provided Existing Card status is not Active--->" + crDBHndlr.maskCardNo(requestObj.getExistingCard()), null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Card program of exisiting card--->" +
                                      requestObj.getExistingCard());
      cardProgram = crDBHndlr.getCardProgramID(requestObj.getExistingCard());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Card program of exisiting card--->" +
                                      cardProgram);
      requestObj.setCardPrgId(cardProgram);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting switch against the Card Program --- Card Program ID--->" +
                                      cardProgram);
      switchID = crDBHndlr.getSwitchID(cardProgram);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Batch Status for switch --->" +
                                      switchID);
      //Checking if Switch is Batch Mode
      if (switchID == null || crDBHndlr.isBatchAllowed(switchID)) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "40",
                                               "Card Reissue not supported in batch mode", null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Active Status for switch --->" +
                                      switchID);
      //Checking if Switch Inactive
      if (switchID == null || !crDBHndlr.isSwitchActive(switchID)) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "91",
                                               "Card Reissue not supported for inactive switch", null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Service Allowed --- Card Program ID--->" +
                                      cardProgram + "<---Service ID--->" +
                                      Constants.CARD_REISSUE);
      if (!crDBHndlr.isServiceAllowed(Constants.CARD_REISSUE,
                                      cardProgram)) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "57",
                                               "Service is not defined/Inactive--->" + Constants.CARD_REISSUE, null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting existing card balance--->" +
                                      requestObj.getExistingCard());
      oldCardBalance = crDBHndlr.getCardBalance(requestObj.getExistingCard());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Parameter Allowed --- Card Program ID--->" +
                                      cardProgram +
                                      "<---Parameter Code--->" +
                                      Constants.MIN_INIT_FUNDS_PARAM +
                                      "<---Parameter Value--->" +
                                      oldCardBalance);
      //Checking if MIN_INT_FUNDS parameter defined and Trasnfer Amount
      //is not violating parameter values, Return Error because Card Upgrade without transfering Amount
      //specified amount in this parameter is not allowed
      if (!crDBHndlr.isParameterAllowed(Constants.MIN_INIT_FUNDS_PARAM,
                                        cardProgram,
                                        oldCardBalance)) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "57",
                                               "Transfer amount does not match Initial Funds paramter value--->" + Constants.MIN_INIT_FUNDS_PARAM, null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      //Get Service Fee on the basis of Apply Fee flag
      if (requestObj.getApplyFee() != null &&
          requestObj.getApplyFee().equalsIgnoreCase(Constants.YES_OPTION)) {

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Getting Service Fee --- Apply Fee--->" +
                                        requestObj.getApplyFee());

        String[] serviceFeeList = crDBHndlr.getServiceFee(Constants.
            CARD_REISSUE, cardProgram, null);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Service Fee List Contents ---- Service Fee--->" +
                                        serviceFeeList[0] +
                                        "<---Return Code--->" +
                                        serviceFeeList[1] +
                                        "<---Description--->" +
                                        serviceFeeList[2]);
        if (serviceFeeList[1] == null) { //No response code recived from API
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_REISSUE, "06",
                                                 "Invalid Service fee", null);
          clientResposne.setFeeAmount("0.00");
          return clientResposne;

        }
        if (serviceFeeList[1] != null &&
            !serviceFeeList[1].equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_REISSUE,
                                                 serviceFeeList[1],
                                                 serviceFeeList[2], null);
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }
        else {
          serviceFee = serviceFeeList[0];
        }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Checking if Apply Service Fee greater than 0 --- Fee --->" +
                                        serviceFee);
        applyFee = applyServiceFee(serviceFee);
        if (applyFee) { //Only Check balance if we have to apply Fee
//
//          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//              LOG_CONFIG),
//                                          "Verifying Funds --- Existing Card Balance--->" +
//                                          oldCardBalance +
//                                          "<---Service Fee--->" +
//                                          serviceFee);
//          if (!verifyCardFunds(oldCardBalance, serviceFee)) {
//            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//                LOG_CONFIG),
//                                            "Card has not enough Funds for Fee Debit ---- Checking Is_Ok_NEG_BAL for service--->" +
//                                            Constants.CARD_REISSUE);
//            if (!crDBHndlr.checkIsOkOnNegativeBalance(oldCardProgram,
//                Constants.CARD_REISSUE)) {
//              //Log Transaction
//              //Return Error resposne -- 51
//            }
//          }
          dbDAO = new DbRequestInfoObject();

          dbDAO.setCardNo(requestObj.getExistingCard());
          dbDAO.setServiceId(Constants.CARD_REISSUE);
          dbDAO.setSkipStatus("B");
          dbDAO.setIsOKNegBal("N");
          dbDAO.setAmount(null);
          dbDAO.setChkTransAmt("N");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Checking Debit Allowed --- getCardNo--->" +
                                          dbDAO.getCardNo()
                                          + "<---getServiceId--->" +
                                          dbDAO.getServiceId()
                                          + "<---getSkipStatus--->" +
                                          dbDAO.getSkipStatus()
                                          + "<---getIsOKNegBal--->" +
                                          dbDAO.getIsOKNegBal()
                                          + "<---getAmount--->" +
                                          dbDAO.getAmount()
                                          + "<---getChkTransAmt--->" +
                                          dbDAO.getChkTransAmt());
          dbResponse = crDBHndlr.checkDebitAllowed(dbDAO);
          if (dbResponse == null) {
            clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                   Constants.CARD_REISSUE, "06",
                                                   "Unable to verify debit allowed", null);
            clientResposne.setFeeAmount("0.00");
            return clientResposne;
          }
          if (!dbResponse.getResponseCode().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                   Constants.CARD_REISSUE, dbResponse.getResponseCode(),
                                                   dbResponse.getResponseDesc(), null);
            clientResposne.setFeeAmount("0.00");
            return clientResposne;
          }
        }
      } //end if Apply Fee = Y

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking OFAC/AVS for Existing Card --- Card Number--->" +
                                      requestObj.getExistingCard());
      int status = checkExistingOFAC_AVS();
      if (status == Constants.OFAC_FAILED) {
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_REISSUE, "OF",
                                                 "OFAC for exisiting card failed--->" +
                                                 crDBHndlr.maskCardNo(requestObj.
                  getExistingCard()), null);
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
      } else if (status == Constants.AVS_FAILED) {
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_REISSUE, "AV",
                                                 "AVS for exisiting card failed--->" +
                                                 crDBHndlr.maskCardNo(requestObj.
                  getExistingCard()), null);
          clientResposne.setFeeAmount("0.00");
          return clientResposne;

      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Performing Card Registration Process");
      try{
        newCardNumber = performCardRegistrationProcessing();
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Assigning card to stakeholder...");
        crDBHndlr.processCardAssignment(requestObj,newCardNumber,Constants.ASSIGN_CR_DESC);
      }
      catch (Exception cardRegEx) {
        try{
          if (dbConn != null) {
            dbConn.rollback();
          }
        }catch(SQLException sqlex){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "SQLException in rolling back transaction --->" +
                                          sqlex);
        }
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "96",
                                               cardRegEx.getMessage(), null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "New Card Number--->" + newCardNumber);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transferring Alerts associated with old card to new card...");
      crDBHndlr.transferCardAlerts(requestObj.getExistingCard(),newCardNumber);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transferring Coupons associated with old card to new card...");
      crDBHndlr.transferCardCoupons(requestObj.getExistingCard(),newCardNumber);

      if (newCardNumber != null && newCardNumber.trim().length() > 0) {

        //Checking Credit Allowed on new card number
        dbDAO = new DbRequestInfoObject();

        dbDAO.setCardNo(newCardNumber);
        dbDAO.setServiceId(Constants.CARD_REISSUE);
        dbDAO.setSkipStatus("BA");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Checking Credit Allowed --- getCardNo--->" +
                                        dbDAO.getCardNo()
                                        + "<---getServiceId--->" +
                                        dbDAO.getServiceId()
                                        + "<---getApplyFee--->" +
                                        dbDAO.getApplyFee());
        dbResponse = crDBHndlr.checkCard(dbDAO);
        if (dbResponse == null) {
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_REISSUE, "06",
                                                 "Unable to verify credit allowed", null);
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }
        if (!dbResponse.getResponseCode().equalsIgnoreCase(Constants.
            SUCCESS_CODE)) {
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_REISSUE, dbResponse.getResponseCode(),
                                                 dbResponse.getResponseDesc(), null);
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }

        //Setting fields for transferring amount
        dbDAO = new DbRequestInfoObject();

        dbDAO.setCardNo(requestObj.getExistingCard());
        dbDAO.setToCardNumber(newCardNumber);
        dbDAO.setServiceId(Constants.CARD_REISSUE);
        dbDAO.setDeviceType(requestObj.getDeviceType());
        dbDAO.setAmount(null);
        dbDAO.setFeeAmount(serviceFee);
        dbDAO.setApplyFee(requestObj.getApplyFee());
        dbDAO.setIsAllCashOut(true);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Attempting to Transfer requested Amount --- From Card--->" +
                                        dbDAO.getCardNo()
                                        + "<---To Card Number--->" +
                                        dbDAO.getToCardNumber()
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

        dbResponse = crDBHndlr.transferAmount(dbDAO);
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
                                        dbResponse.getNewCardBalance()
                                        );
        clientResposne.setResposneCode(dbResponse.getResponseCode());
        clientResposne.setResposneDescription(dbResponse.getResponseDesc());
        clientResposne.setTraceAuditNumber(dbResponse.getTraceAuditNo());
        clientResposne.setTraceAuditNumberToCard(dbResponse.getTraceAuditNoToCard());
        clientResposne.setFeeAmount(dbResponse.getFeeAmount());
        clientResposne.setCardBalance(dbResponse.getCardBalance());
        clientResposne.setCurrentBalance(dbResponse.getNewCardBalance());

        String cardPrgName = crDBHndlr.getCardProgramName(requestObj.getCardPrgId());
        clientResposne.setCardPrgName(cardPrgName);

        crDBHndlr.getSuccessResponseAttributes(newCardNumber,clientResposne);
        clientResposne.setNewCardNumber(newCardNumber);

        clientResposne.setInstitutionName(crDBHndlr.getFinanacialInstitutionName(requestObj.getCardPrgId()));

        return clientResposne;
      }else{
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "06",
                                               "Invalid New Card generated", null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }


//      if (applyFee) {
//        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
//                                        "Performing Card Registration Process");
//        dbDAO.setCardNo(requestObj.getExistingCard());
//        dbDAO.setServiceId(Constants.CARD_REISSUE);
//        dbDAO.setDeviceType(requestObj.getDeviceType());
//        dbDAO.setFeeAmount(serviceFee);
//
//        dbResponse = crDBHndlr.debitServiceFee(dbDAO);
//        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
//                                        "Response Received ---- Response Code--->" +
//                                        dbResponse.getResponseCode()
//                                        + "<---Response Description--->" +
//                                        dbResponse.getResponseCode()
//                                        + "<---Trace Audtit No--->" +
//                                        dbResponse.getTraceAuditNo()
//                                        + "<---ISO Serial No--->" +
//                                        dbResponse.getIsoSerialNo()
//                                        + "<---Remaining Balance--->" +
//                                        dbResponse.getRemainingBal());
//      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Processing trasnaction for Card Reissue Type 3--->" +
                                      ex);
      try{
        if (dbConn != null) {
          dbConn.rollback();
        }
      }catch(SQLException sqlex){
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "SQLException in rolling back transaction --->" +
                                        sqlex);
      }
      clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                             Constants.CARD_REISSUE, "96",
                                             ex.getMessage(), null);
      clientResposne.setFeeAmount("0.00");
    }
    return clientResposne;
  }

  /**
   * This method is used to register the card or to generate a new card. After generation or
   * registration of  card it updates the card holder profile and return the new card no.
   * @throws Exception
   * @return String
   */

  private String performCardRegistrationProcessing() throws Exception{
    String cardNumber = null;
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Calling method for card generation");
    requestObj.setCardGenType(Constants.NEW_CARDGEN_REISSUE);
    cardNumber = generateNewCard();

    if (cardNumber == null || cardNumber.trim().length() == 0) {
      throw new Exception("Invalid new card number received");
    }

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Card Number generated--->" + cardNumber);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Calling method for updating profile for card--->" +
                                    cardNumber + "<---Using Existing Profile of card--->" + requestObj.getExistingCard());
    updateExistingCardHolderProfile(cardNumber);
    return cardNumber;
  }

  protected void updateExistingCardHolderProfile(String cardNumber) throws
     Exception {
   TransactionRequestInfoObj existingProfile = null;
   CardReissueDataBaseHandler crDbHandler = null;
   try {
     crDbHandler = new CardReissueDataBaseHandler(this.dbConn);
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Method for Updating Card Holder Profile for Exisiting card Number--->" +
                                     cardNumber);
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Getting Profile for existing card Number--->" +
                                     requestObj.getExistingCard());
     existingProfile = crDbHandler.getCardHolderProfile(requestObj.
         getExistingCard());
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Updating Existing Profile for New card Number--->" +
                                     cardNumber);
     crDbHandler.updateCardHolderProfile(existingProfile, cardNumber);
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Updating Existing Card limits for New card Number--->" +
                                     cardNumber);
     crDbHandler.updateCardLimits(existingProfile, cardNumber);
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Updating Default card associated with User--->" +
                                     requestObj.getUserId());
     if(existingProfile.getUserId() == null || existingProfile.getUserId().trim().length() == 0){
         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "No user exist for the old card...");
     }else{
         crDbHandler.updateDefaultCard(existingProfile.getUserId(),requestObj.getExistingCard(),cardNumber);
     }
   }catch (Exception ex) {
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                     "Exception in Updating Card Holder Profile for Existing card Number--->" +
                                     ex);
     throw ex;
   }
 }

}
