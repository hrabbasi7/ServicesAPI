package com.i2c.services.registration.cardupgrade;

import com.i2c.services.registration.base.Transaction;
import com.i2c.services.registration.base.TransactionRequestInfoObj;
import com.i2c.services.registration.base.DbRequestInfoObject;
import com.i2c.services.registration.base.DbResponseInfoObject;
import com.i2c.services.registration.base.TransactionResponseInfoObj;

import com.i2c.services.util.*;
import java.sql.*;
/**
 * <p>Title: CardUpgradeWithAllCashOut: Class for upgrading the card and setting the card funds to zero </p>
 * <p>Description: This class is used to upgrade the card and sets the funds of the card to zero</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class CardUpgradeWithAllCashOut
    extends Transaction {
  private TransactionRequestInfoObj requestObj = null;
  private Connection dbConn = null;

  /**
   * Constructor for the class
   * @param dbConn Connection
   * @param requestObj TransactionRequestInfoObj
   */

  public CardUpgradeWithAllCashOut(Connection dbConn,
                                   TransactionRequestInfoObj
                                   requestObj) {
    super(dbConn, requestObj);
    this.dbConn = dbConn;
    this.requestObj = requestObj;
  }


  /** /**
   * This method is used to upgrade the card and sets the new card balance to zero. It first validates the
   * following attributs, if found invlalid then it pauses the execution and returns response to client
   * with appropiate response code.<br>
   * 1. All Mandatory Attributes of the card such as card no (If invalid response code 06 is returned) <br>
   * 2. Card Program of the given card no. (If invalid response code 06 is returned)<br>
   * 3. Vlidates existing card no. (If invalid response code 14 is returned) <br>
   * 4. Card status at ATM & POS (If found inactive resposne code SA is returned) <br>
   * 5. Validates switch (If invalid response code 06 is returned) <br>
   * 6. Validates switch activation (If invalid response code 91 is returned) <br>
   * 7. Validates switch batch mode (If invalid response code 40 is returned) <br>
   * 9. Validates service Id (If invalid response code 57 is returned) <br>
   * 10. Validate card parameter (If invalid response code 51 is returned)<br>
   * After validation the method calculates the service fee for the given service. If the application of the
   * service fee is allowed then it creates the DB Information object and checks whether debit is allowed and
   * the status of the OFAC & AVS. If the debit was not allowed or OFAC OR AVS status was found falsy then it
   * pauses the execution and returns the response to the client.
   * After this the method generate new card, apply the fee return the response.
   * @return TransactionResponseInfoObj
   */
  public TransactionResponseInfoObj processTransaction() {
    String newCardNumber = null;
    CardUpgradeInformationValidator validator = null;
    CardUpgradeDataBaseHandler cuDBHndlr = null;
    DbRequestInfoObject dbDAO = new DbRequestInfoObject();
    DbResponseInfoObject dbResponse = new DbResponseInfoObject();
    TransactionResponseInfoObj clientResposne = new TransactionResponseInfoObj();

    String oldCardProgram = null;

    String oldSwitchID = null;
    String newSwitchID = null;

    String serviceFee = null;
    String oldCardBalance = null;

    boolean applyFee = false;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Processing Card Uprade With All cashout Transaction");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating Mandatory Attibutes");
      validator = new CardUpgradeInformationValidator(requestObj);
      try {
        //validating mandatory fields for card upgrade
        validator.validateMandatory();
      }
      catch (Exception validateExcep) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Exception in Validating Mandatory Attibutes--->" +
                                        validateExcep);
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "06",
                                               validateExcep.getMessage(), null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Authenticating Upgrade/NEW Card Program--->" +
                                      requestObj.getCardPrgId());

      //----------------Performing Database related Validation of Request Attributes----------//
      cuDBHndlr = new CardUpgradeDataBaseHandler(dbConn);

      //Check (Upgrade) Card Program valid
      if (!cuDBHndlr.isCardProgramValid(requestObj.getCardPrgId())) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Provided Card Program Not Valid -- Logging transaction -- Returning Error response");
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "06",
                                               "Provided Card Program Not Valid --->" + requestObj.getCardPrgId(), null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      //Checking Exisiting Card valid
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating exisiting card--->" +
                                      requestObj.getExistingCard());
      if (!cuDBHndlr.isCardNumberValid(requestObj.getExistingCard())) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "14",
                                               "Existing Card Number Not Valid--->" + cuDBHndlr.maskCardNo(requestObj.getExistingCard()), null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting ATM/POS status for exisiting card--->" +
                                      requestObj.getExistingCard());

      cuDBHndlr.getCardStatuses(requestObj);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Got ATM/POS status for exisiting card --- ATM Status--->" +
                                      requestObj.getAtmStatus() + "<---POS Status--->" + requestObj.getPosStatus());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking exisiting card Active--->" +
                                      requestObj.getExistingCard());
      if (!cuDBHndlr.isExistingCardNumberActive(requestObj.getAtmStatus(),requestObj.getPosStatus())) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "SA",
                                               "Provided Existing Card Number is not Active--->" + cuDBHndlr.maskCardNo(requestObj.getExistingCard()), null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Card program of exisiting card--->" +
                                      requestObj.getExistingCard());
      oldCardProgram = cuDBHndlr.getCardProgramID(requestObj.getExistingCard());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Card program of exisiting card--->" +
                                      oldCardProgram);

      //Checking if old and new card programs are different

      if (!validator.validateCardPrograms(oldCardProgram,
                                          requestObj.getCardPrgId())) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "06",
                                               "Existing and Upgraded Card Programs cannot be same for Card Upgrade", null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting switch against the Upgrade Card Program --- Card Program ID--->" +
                                      requestObj.getCardPrgId());

      newSwitchID = cuDBHndlr.getSwitchID(requestObj.getCardPrgId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting switch against the Old Card Program --- Card Program ID--->" +
                                      oldCardProgram);
      oldSwitchID = cuDBHndlr.getSwitchID(oldCardProgram);

      //Validating if Both switches are same
      if (!validator.validateSwitches(oldSwitchID, newSwitchID)) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "06",
                                               "Card Upgrade across different switches not supported", null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Batch Status for switch --->" +
                                      newSwitchID);
      //Checking if Switch is Batch Mode
      if (newSwitchID == null || cuDBHndlr.isBatchAllowed(newSwitchID)) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "40",
                                               "Card Upgrade service is not supported in batch mode", null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Active Status for switch --->" +
                                      newSwitchID);
      //Checking if Switch Inactive
      if (newSwitchID == null || !cuDBHndlr.isSwitchActive(newSwitchID)) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "91",
                                               "Card Upgrade not supported for inactive switch", null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Service Allowed --- Card Program ID--->" +
                                      oldCardProgram + "<---Service ID--->" +
                                      Constants.CARD_UPGRADE);
      if (!cuDBHndlr.isServiceAllowed(Constants.CARD_UPGRADE,
                                      oldCardProgram)) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "57",
                                               "Service is not defined/Active--->" + Constants.CARD_UPGRADE + " for card program--->" + oldCardProgram, null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting existing card balance--->" +
                                      requestObj.getExistingCard());
      oldCardBalance = cuDBHndlr.getCardBalance(requestObj.getExistingCard());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Parameter Allowed --- Card Program ID--->" +
                                      requestObj.getCardPrgId() +
                                      "<---Parameter Code--->" +
                                      Constants.MIN_INIT_FUNDS_PARAM +
                                      "<---Parameter Value--->" +
                                      oldCardBalance);

      //Checking if MIN_INT_FUNDS parameter defined and Trasnfer Amount
      //is not violating parameter values, Return Error because Card Upgrade without transfering Amount
      //specified amount in this parameter is not allowed
      if (!cuDBHndlr.isParameterAllowed(Constants.MIN_INIT_FUNDS_PARAM,
                                        requestObj.getCardPrgId(),
                                        oldCardBalance)) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "57",
                                               "Transfer amount does not match Initial Funds paramter value--->" + Constants.MIN_INIT_FUNDS_PARAM + " for card program--->" + requestObj.getCardPrgId() , null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      //Get Service Fee on the basis of Apply Fee flag
      if (requestObj.getApplyFee() != null &&
          requestObj.getApplyFee().equalsIgnoreCase(Constants.YES_OPTION)) {

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Getting Service Fee --- Apply Fee--->" +
                                        requestObj.getApplyFee());

        String[] serviceFeeList = cuDBHndlr.getServiceFee(Constants.
            CARD_UPGRADE, oldCardProgram, null);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Service Fee List Contents ---- Service Fee--->" +
                                        serviceFeeList[0] +
                                        "<---Return Code--->" +
                                        serviceFeeList[1] +
                                        "<---Description--->" +
                                        serviceFeeList[2]);
        if (serviceFeeList[1] == null) { //No response code recived from API
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_UPGRADE, "06",
                                                 "Invalid Service Fee", null);
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }
        if (serviceFeeList[1] != null &&
            !serviceFeeList[1].equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_UPGRADE, serviceFeeList[1],
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
//                                            Constants.CARD_UPGRADE);
//            if (!cuDBHndlr.checkIsOkOnNegativeBalance(oldCardProgram,
//                Constants.CARD_UPGRADE)) {
//              //Log Transaction
//              //Return Error resposne -- 51
//            }
//          }

          dbDAO = new DbRequestInfoObject();

          dbDAO.setCardNo(requestObj.getExistingCard());
          dbDAO.setServiceId(Constants.CARD_UPGRADE);
          dbDAO.setSkipStatus("B");
          dbDAO.setIsOKNegBal("N");
          dbDAO.setAmount(null);
          dbDAO.setChkTransAmt("N");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Checking Debit Allowed --- getCardNo--->" +
                                          dbDAO.getCardNo()
                                          + "<---getServiceId--->" +
                                          dbDAO.getServiceId() +
                                          "<---getAmount[ServiceFee]--->" +
                                          dbDAO.getAmount()
                                          + "<---getIsOKNegBal--->" +
                                          dbDAO.getIsOKNegBal()+ "<---getAmount--->" +
                                          dbDAO.getAmount()
                                          + "<---getChkTransAmt--->" +
                                          dbDAO.getChkTransAmt());
          dbResponse = cuDBHndlr.checkDebitAllowed(dbDAO);
          if (dbResponse == null) {
            clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                     Constants.CARD_UPGRADE, "06",
                                                     "Unable to verify debit allowed", null);
              clientResposne.setFeeAmount("0.00");
              return clientResposne;
          }
          if (!dbResponse.getResponseCode().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                   Constants.CARD_UPGRADE, dbResponse.getResponseCode(),
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
                                                 Constants.CARD_UPGRADE, "OF",
                                                 "OFAC for exisiting card failed--->" +
                                                 cuDBHndlr.maskCardNo(requestObj.
                  getExistingCard()), null);
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
      } else if (status == Constants.AVS_FAILED) {
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_UPGRADE, "AV",
                                                 "AVS for exisiting card failed--->" +
                                                 cuDBHndlr.maskCardNo(requestObj.
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
        cuDBHndlr.processCardAssignment(requestObj, newCardNumber,Constants.ASSIGN_CU_DESC);
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
                                               Constants.CARD_UPGRADE, "96",
                                               cardRegEx.getMessage(), null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }


      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "New Card Number--->" + newCardNumber);

//      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
//                                      "Transferring Alerts associated with old card to new card...");
//      cuDBHndlr.transferCardAlerts(requestObj.getExistingCard(), newCardNumber);
//
//      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
//                                      "Transferring Coupons associated with old card to new card...");
//      cuDBHndlr.transferCardCoupons(requestObj.getExistingCard(), newCardNumber);


      if (newCardNumber != null && newCardNumber.trim().length() > 0) {

        //Checking Credit Allowed on new card number
        dbDAO = new DbRequestInfoObject();

        dbDAO.setCardNo(newCardNumber);
//        dbDAO.setServiceId(Constants.CARD_UPGRADE);
        dbDAO.setSkipStatus("BA");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Checking Credit Allowed --- getCardNo--->" +
                                        dbDAO.getCardNo()
                                        + "<---getServiceId--->" +
                                        dbDAO.getServiceId()
                                        + "<---getSkipStatus--->" +
                                        dbDAO.getSkipStatus());
        dbResponse = cuDBHndlr.checkCard(dbDAO);
        if (dbResponse == null) {
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_UPGRADE, "06",
                                                 "Unable to verify credit allowed", null);
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }
        if (!dbResponse.getResponseCode().equalsIgnoreCase(Constants.
            SUCCESS_CODE)) {
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_UPGRADE, dbResponse.getResponseCode(),
                                                 dbResponse.getResponseDesc(), null);
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }

        //Setting fields for transferring amount
        dbDAO = new DbRequestInfoObject();

        dbDAO.setCardNo(requestObj.getExistingCard());
        dbDAO.setToCardNumber(newCardNumber);
        dbDAO.setServiceId(Constants.CARD_UPGRADE);
        dbDAO.setDeviceType(requestObj.getDeviceType());
        dbDAO.setFeeAmount(serviceFee);
        dbDAO.setApplyFee(requestObj.getApplyFee());
        dbDAO.setIsAllCashOut(true);
        dbDAO.setActivateToCard((requestObj.isActivateToCard() ? "Y" : "N"));
        dbDAO.setAcquirerId(requestObj.getAcquirerId());
        dbDAO.setAcquirerUserId(requestObj.getAcquirerUserId());
        dbDAO.setAcquirerData1(requestObj.getAcquirerData1());
        dbDAO.setAcquirerData2(requestObj.getAcquirerData2());
        dbDAO.setAcquirerData3(requestObj.getAcquirerData3());
        dbDAO.setCrdAceptorCode(requestObj.getCrdAceptorCode());
        dbDAO.setCrdAceptorName(requestObj.getCrdAceptorName());
        dbDAO.setMerchantCatCd(requestObj.getMerchantCatCd());
        dbDAO.setDeviceId(requestObj.getDeviceId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Attempting to Transfer requested Amount --- From Card--->" +
                                        dbDAO.getCardNo()
                                        + "<---To Card Number--->" +
                                        dbDAO.getToCardNumber()
                                        + "<---Transfer Amount--->" +
                                        dbDAO.getAmount()
                                        + "<---Service ID--->" +
                                        dbDAO.getServiceId()
                                        + "<---Service Fee--->" +
                                        dbDAO.getFeeAmount()
                                        + "<---Apply Fee--->" +
                                        dbDAO.getApplyFee()
                                        + "<---getDeviceType--->" +
                                        dbDAO.getDeviceType()
                                        + "<---isIsAllCashOut--->" +
                                        dbDAO.isIsAllCashOut()
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
                                        + "<---getRetRefNumber--->" +
                                        dbDAO.getRetRefNumber()
                                        + "<---getActivateToCard--->" +
                                        dbDAO.getActivateToCard());

        dbResponse = cuDBHndlr.transferAmount(dbDAO);
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

        String cardPrgName = cuDBHndlr.getCardProgramName(requestObj.getCardPrgId());
        clientResposne.setCardPrgName(cardPrgName);
        clientResposne.setIsoSerialNumber(cuDBHndlr.getISOSerialNo(clientResposne.getTraceAuditNumberToCard()));

        cuDBHndlr.getSuccessResponseAttributes(newCardNumber,clientResposne);
        clientResposne.setNewCardNumber(newCardNumber);
        clientResposne.setRefernceId(cuDBHndlr.buildRefernceId(clientResposne.getIsoSerialNumber()));
        clientResposne.setInstitutionName(cuDBHndlr.getFinanacialInstitutionName(requestObj.getCardPrgId()));

        return clientResposne;
      }else{
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "96",
                                               "Invalid new card generated", null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }


//      if (applyFee) {
//        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
//                                        "Performing Card Registration Process");
//        dbDAO.setCardNo(requestObj.getExistingCard());
//        dbDAO.setServiceId(Constants.CARD_UPGRADE);
//        dbDAO.setDeviceType(requestObj.getDeviceType());
//        dbDAO.setFeeAmount(serviceFee);
//
//        dbResponse = cuDBHndlr.debitServiceFee(dbDAO);
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
                                      "Exception in Processing trasnaction for Card Upgrade--->" +
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
                                             Constants.CARD_UPGRADE, "96",
                                             ex.getMessage(), null);
      clientResposne.setFeeAmount("0.00");
    }
    return clientResposne;
  }


  /**
   *  /**
   * This method is used to register the card or generate a new card. After registration or generation
   * of the new card it updates the card holder profile and return the new generated card no to the calling
   * method.
   * @throws Exception
   * @return String
   */


  private String performCardRegistrationProcessing() throws Exception{
    String cardNumber = null;
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Calling method for card generation");
    requestObj.setCardGenType(Constants.NEW_CARDGEN_UPGRADE);
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
   CardUpgradeDataBaseHandler cuDbHandler = null;
   try {
     cuDbHandler = new CardUpgradeDataBaseHandler(this.dbConn);
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Method for Updating Card Holder Profile for Exisiting card Number--->" +
                                     cardNumber);
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Getting Profile for existing card Number--->" +
                                     requestObj.getExistingCard());
     existingProfile = cuDbHandler.getCardHolderProfile(requestObj.
         getExistingCard());
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Updating Existing Profile for New card Number--->" +
                                     cardNumber);
     cuDbHandler.updateCardHolderProfile(existingProfile, cardNumber);
//     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
//                                     "Updating Existing Card limits for New card Number--->" +
//                                     cardNumber);
//     cuDbHandler.updateCardLimits(existingProfile, cardNumber);
   }catch (Exception ex) {
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                     "Exception in Updating Card Holder Profile for Existing card Number--->" +
                                     ex);
     throw ex;
   }
 }
}
