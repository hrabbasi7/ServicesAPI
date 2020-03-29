package com.i2c.services.registration.cardupgrade;

import com.i2c.services.registration.base.Transaction;
import com.i2c.services.registration.base.TransactionRequestInfoObj;
import com.i2c.services.registration.base.DbRequestInfoObject;
import com.i2c.services.registration.base.DbResponseInfoObject;
import com.i2c.services.registration.base.TransactionResponseInfoObj;

import com.i2c.services.util.*;
import java.sql.*;

/**
 * <p>Title: CardUpgradeTransaction: A class which is used for card upgradation </p>
 * <p>Description: This class is used to perfrom the card upgradition such as generating
 * new card from the older card information.</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class CardUpgradeTransaction extends Transaction {

  private TransactionRequestInfoObj requestObj = null;
  private Connection dbConn = null;

  /**
   * Constructor of the class which initlizes the database connection and request object
   * @param dbConn Connection
   * @param requestObj TransactionRequestInfoObj
   */
  public CardUpgradeTransaction(Connection dbConn,
                                TransactionRequestInfoObj requestObj) {
    super(dbConn, requestObj);
    this.dbConn = dbConn;
    this.requestObj = requestObj;
  }

  /**
   * This method is used to upgrade the card. It first validates the following attributs, if
   * found invlalid then it pause the execution and returns response to client with appropiate
   * response code.<br>
   * 1. All mandatory attributes of the card such as card no (If invalid response code 06 is returned) <br>
   * 2. Card Program of the given card no. (If invalid response code 06 is returned)<br>
   * 3. Vlidates existing card no. (If invalid response code 14 is returned) <br>
   * 4. Card status at ATM & POS (If found inactive resposne code SA is returned) <br>
   * 5. Validates switch (If invalid response code 06 is returned) <br>
   * 6. Validates switch activation (If invalid response code 91 is returned) <br>
   * 7. Validates switch batch mode (If invalid response code 40 is returned) <br>
   * 9. Validates service Id (If invalid response code 57 is returned) <br>
   * 10. Validates card parameter (If invalid response code 51 is returned)<br>
   * After validation the method calculates the service fee for the given service. If the application of the
   * service fee is allowed then it creates the DB Information object and checks whether debit is allowed and
   * the status of the OFAC & AVS. If the debit is not allowed or OFAC OR AVS status was found falsy then it
   * pauses the execution and returns the response to the client.
   * After processing the above steps this the method generate new card, apply the fee and  returns the
   * response to the calling method.
   * @return TransactionResponseInfoObj
   */
  public TransactionResponseInfoObj processTransaction() {
    CardUpgradeInformationValidator validator = null;
    CardUpgradeDataBaseHandler cuDBHndlr = null;
    DbRequestInfoObject dbDAO = null;
    DbResponseInfoObject dbResponse = new DbResponseInfoObject();

    TransactionResponseInfoObj clientResponse = new TransactionResponseInfoObj();

    String oldCardProgram = null;

    String oldSwitchID = null;
    String newSwitchID = null;

    String serviceFee = null;
//    String cardBalance = null;

    boolean applyFee = false;
    String newCardNumber = null;
    String cardPrgName = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Processing Card Upgrade Transaction");

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
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Logging Error transaction, Returning resposne...");

        clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "06",
                                               validateExcep.getMessage(), null);
        clientResponse.setFeeAmount("0.00");
        return clientResponse;
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Authenticating Card Program--->" +
                                      requestObj.getCardPrgId());

      //----------------Performing Database related Validation of Request Attributes----------//
      cuDBHndlr = new CardUpgradeDataBaseHandler(dbConn);

      //Check (Upgrade) Card Program valid
      if (!cuDBHndlr.isCardProgramValid(requestObj.getCardPrgId())) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Provided Card Program Not Valid -- Logging transaction -- Returning Error response");
        clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "06",
                                               "Provided Card Program Not Valid --->" + requestObj.getCardPrgId(), null);
        clientResponse.setFeeAmount("0.00");
        return clientResponse;
      }

      //Checking Exisiting Card valid
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating exisiting card--->" +
                                      requestObj.getExistingCard());
      if (!cuDBHndlr.isCardNumberValid(requestObj.getExistingCard())) {
        clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "14",
                                               "Existing Card Number Not Valid--->" + cuDBHndlr.maskCardNo(requestObj.getExistingCard()), null);
        clientResponse.setFeeAmount("0.00");
        return clientResponse;
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
        clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "SA",
                                               "Provided Existing Card Number is not Active--->" + cuDBHndlr.maskCardNo(requestObj.getExistingCard()), null);
        clientResponse.setFeeAmount("0.00");
        return clientResponse;
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
        clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "06",
                                               "Existing and Upgraded Card Programs cannot be same for Card Upgrade", null);
        clientResponse.setFeeAmount("0.00");
        return clientResponse;
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
        clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "06",
                                               "Card Upgrade across different switches not supported", null);
        clientResponse.setFeeAmount("0.00");
        return clientResponse;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Batch Status for switch --->" +
                                      newSwitchID);
      //Checking if Switch is Batch Mode
      if (newSwitchID == null || cuDBHndlr.isBatchAllowed(newSwitchID)) {
        clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "40",
                                               "Card Upgrade service is not supported in batch mode", null);
        clientResponse.setFeeAmount("0.00");
        return clientResponse;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Active Status for switch --->" +
                                      newSwitchID);
      //Checking if Switch Inactive
      if (newSwitchID == null || !cuDBHndlr.isSwitchActive(newSwitchID)) {
        clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "91",
                                               "Card Upgrade not supported for inactive switch", null);
        clientResponse.setFeeAmount("0.00");
        return clientResponse;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Service Allowed --- Card Program ID--->" +
                                      oldCardProgram + "<---Service ID--->" +
                                      Constants.CARD_UPGRADE);
      if (!cuDBHndlr.isServiceAllowed(Constants.CARD_UPGRADE,
                                      oldCardProgram)) {
        clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "57",
                                               "Service is not defined/Active--->" + Constants.CARD_UPGRADE + " for card program--->" + oldCardProgram, null);
        clientResponse.setFeeAmount("0.00");
        return clientResponse;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Parameter Allowed --- Card Program ID--->" +
                                      requestObj.getCardPrgId()
                                      + "<---Parameter Code--->" +
                                      Constants.MIN_INIT_FUNDS_PARAM +
                                      "<---Parameter Value--->" + null
                                      );

      //Checking if MIN_INT_FUNDS parameter defined
      //if it is then, Return Error because Card Upgrade without Amount
      //transfer is not allowed because of this parameter
      if (!cuDBHndlr.isParameterAllowed(Constants.MIN_INIT_FUNDS_PARAM,
                                       requestObj.getCardPrgId(), "0")) {
        clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "57",
                                               "Transfer amount does not match Initial Funds paramter value--->" + Constants.MIN_INIT_FUNDS_PARAM + " for card program--->" + requestObj.getCardPrgId(), null);
        clientResponse.setFeeAmount("0.00");
        return clientResponse;
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
          clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_UPGRADE, "06",
                                                 "Invalid Service Fee", null);
          clientResponse.setFeeAmount("0.00");
          return clientResponse;
        }
        if (serviceFeeList[1] != null &&
            !serviceFeeList[1].equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_UPGRADE,
                                                 serviceFeeList[1],
                                                 serviceFeeList[2], null);
          clientResponse.setFeeAmount("0.00");
          return clientResponse;
        }
        else {
          serviceFee = serviceFeeList[0];
        }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Checking if Service Fee greater than 0 --- Fee --->" +
                                        serviceFee);
        applyFee = applyServiceFee(serviceFee);

        if (applyFee) {
          //Only Check balance for (service fee + transfer amount)if we have to apply Fee
//          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//              LOG_CONFIG),
//                                          "Getting Card Balance for card--->" +
//                                          requestObj.getExistingCard());
//
//          cardBalance = cuDBHndlr.getCardBalance(requestObj.getExistingCard());
//
//          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//              LOG_CONFIG),
//                                          "Calculating Net Amount --- Service Fee + " + serviceFee + "<---Transfer Amount--->" +
//                                          requestObj.getTransferAmount());
//          netAmount = Double.parseDouble(serviceFee) + Double.parseDouble(requestObj.getTransferAmount());
//
//          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//              LOG_CONFIG),
//                                          "Got Net Amount --->" + netAmount);
//
//          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//              LOG_CONFIG),
//                                          "Verifying Funds --- Card Balance--->" +
//                                          cardBalance + "<---Service Fee + Transfer Amount--->" +
//                                          netAmount);
//          if (!verifyCardFunds(cardBalance, Double.toString(netAmount))) {
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
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Checking Debit Allowed--->" +
                                          requestObj.getExistingCard());
          dbDAO = new DbRequestInfoObject();

          dbDAO.setCardNo(requestObj.getExistingCard());
          dbDAO.setServiceId(Constants.CARD_UPGRADE);
          dbDAO.setSkipStatus("B");
          dbDAO.setIsOKNegBal("Y");
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
          dbResponse = cuDBHndlr.checkDebitAllowed(dbDAO);
          if (dbResponse == null) {
            clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                Constants.CARD_UPGRADE, "06",
                "Unable to verify debit allowed", null);
            clientResponse.setFeeAmount("0.00");
            return clientResponse;
          }
          if (!dbResponse.getResponseCode().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                Constants.CARD_UPGRADE, dbResponse.getResponseCode(),
                dbResponse.getResponseDesc(), null);
            clientResponse.setFeeAmount("0.00");
            return clientResponse;
          }
        }
      } //end if Apply Fee = Y

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking OFAC/AVS for Existing Card --- Card Number--->" +
                                      requestObj.getExistingCard());
      int status = checkExistingOFAC_AVS();
      if (status == Constants.OFAC_FAILED) {
          clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_UPGRADE, "OF",
                                                 "OFAC for exisiting card failed--->" +
                                                 cuDBHndlr.maskCardNo(requestObj.
                  getExistingCard()), null);
          clientResponse.setFeeAmount("0.00");
          return clientResponse;
      } else if (status == Constants.AVS_FAILED) {
          clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_UPGRADE, "AV",
                                                 "AVS for exisiting card failed--->" +
                                                 cuDBHndlr.maskCardNo(requestObj.
                  getExistingCard()), null);
          clientResponse.setFeeAmount("0.00");
          return clientResponse;
      }


      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Performing Card Registration Process");
      try {
       newCardNumber = performCardRegistrationProcessing();
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
           LOG_CONFIG), "Assigning card to stakeholder...");
       cuDBHndlr.processCardAssignment(requestObj,newCardNumber,Constants.ASSIGN_CU_DESC);
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
        clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_UPGRADE, "96",
                                               cardRegEx.getMessage(), null);
        clientResponse.setFeeAmount("0.00");
        return clientResponse;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Flag value to activate the Upgraded Card--->" + requestObj.isActivateToCard());
      if(requestObj.isActivateToCard()){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Activating the Upgraded Card...");
          //Activate card here

          dbDAO = new DbRequestInfoObject();
          dbDAO.setCardNo(newCardNumber);
          dbDAO.setDeviceType(requestObj.getDeviceType());
          dbDAO.setRetRefNumber(requestObj.getRetRefNumber());
          dbDAO.setResponseCode("00");
          dbDAO.setDescription("Card Upgrade,To Card activation");
          dbDAO.setApplyFee("N");
          dbDAO.setServiceId(Constants.CARD_UPGRADE);
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

          dbResponse = cuDBHndlr.activateCardInOltp(dbDAO);
          if (dbResponse == null) {
              clientResponse = prepareClientResponse(newCardNumber,
                      Constants.CARD_UPGRADE, "06",
                      "Unable to activate new card", null,
                      requestObj.getAcquirerId()
                      , requestObj.getCrdAceptorCode()
                      , requestObj.getCrdAceptorName()
                      , requestObj.getMerchantCatCd()
                      , requestObj.getRetRefNumber()
                      , requestObj.getDeviceId()
                               );
              clientResponse.setFeeAmount("0.00");
              return clientResponse;
          }
          if (!dbResponse.getResponseCode().equalsIgnoreCase(Constants.
                  SUCCESS_CODE)) {
              clientResponse = prepareClientResponse(newCardNumber,
                      Constants.CARD_UPGRADE,
                      dbResponse.getResponseCode(),
                      dbResponse.getResponseDesc(), null,
                      requestObj.getAcquirerId()
                      , requestObj.getCrdAceptorCode()
                      , requestObj.getCrdAceptorName()
                      , requestObj.getMerchantCatCd()
                      , requestObj.getRetRefNumber()
                      , requestObj.getDeviceId()
                               );
              clientResponse.setFeeAmount("0.00");
              return clientResponse;
          }
          clientResponse.setResposneCode(dbResponse.getResponseCode());
          clientResponse.setResposneDescription(dbResponse.getResponseDesc());
          clientResponse.setIsoSerialNumber(dbResponse.getIsoSerialNo());
          clientResponse.setTraceAuditNumber((dbResponse.getTraceAuditNo() != null ?
                                              dbResponse.getTraceAuditNo() :
                                              cuDBHndlr.getTraceAuditNo(
                  dbResponse.getIsoSerialNo())));
          clientResponse.setRefernceId(cuDBHndlr.buildRefernceId(clientResponse.getIsoSerialNumber()));
      }else{
          clientResponse = prepareClientResponse(newCardNumber,
                                                 Constants.CARD_UPGRADE, "00",
                                                 "Card ---> " +  cuDBHndlr.maskCardNo(newCardNumber)+ " is successfully Upgraded to --->" + (cardPrgName != null && cardPrgName.trim().length() > 0 ? cardPrgName : requestObj.getCardPrgId()), null);
          clientResponse.setRefernceId(cuDBHndlr.buildRefernceId(clientResponse.getIsoSerialNumber()));
      }

      if (applyFee) {

        dbDAO = new DbRequestInfoObject();

        dbDAO.setCardNo(requestObj.getExistingCard());
        dbDAO.setServiceId(Constants.CARD_UPGRADE);
        dbDAO.setDeviceType(requestObj.getDeviceType());
        dbDAO.setOrgTraceAudit(clientResponse.getTraceAuditNumber());
        dbDAO.setFeeAmount(serviceFee);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Applying Fee on Existing Card --- getCardNo--->" +
                                        dbDAO.getCardNo()
                                        + "<---getServiceId--->" +
                                        dbDAO.getServiceId()
                                        + "<---getDeviceType--->" +
                                        dbDAO.getDeviceType()
                                        + "<---getFeeAmount--->" +
                                        dbDAO.getFeeAmount()
                                        + "<---getOrgTraceAudit--->" +
                                        dbDAO.getOrgTraceAudit()
                                        );

        dbResponse = cuDBHndlr.debitServiceFee(dbDAO);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
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

        if (dbResponse != null &&
            !dbResponse.getResponseCode().
            equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_UPGRADE,
                                                 dbResponse.getResponseCode(),
                                                 "Unable to debit service fee-->" +
                                                 dbResponse.getResponseDesc(), null);
          clientResponse.setFeeAmount("0.00");
          return clientResponse;
        }
      } //applyFee = true && serviceFee > 0

      cardPrgName = cuDBHndlr.getCardProgramName(requestObj.getCardPrgId());
      cuDBHndlr.getSuccessResponseAttributes(newCardNumber,clientResponse,true);

      if(dbDAO != null){
        clientResponse.setFeeAmount(dbDAO.getFeeAmount());
      }else{
        clientResponse.setFeeAmount("0.00");
      }
      clientResponse.setNewCardNumber(newCardNumber);
      clientResponse.setCardPrgName(cardPrgName);
      clientResponse.setInstitutionName(cuDBHndlr.getFinanacialInstitutionName(requestObj.getCardPrgId()));

      return clientResponse;
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Processing transaction for Card Upgrade--->" +
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
      clientResponse = prepareClientResponse(requestObj.getExistingCard(),
                                             Constants.CARD_UPGRADE, "96",
                                             ex.getMessage(), null);
      clientResponse.setFeeAmount("0.00");
    }
    return clientResponse;
  }


  /**
   * This method is used to register the card or generate a new card. After registration or generation of
   * the  new card it updates the card holder profile and returns the new generated card no. to the calling
   * method.
   * @throws Exception
   * @return String
   */

  private String performCardRegistrationProcessing() throws Exception {
    String cardNumber = null;
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Calling method for card generation");
    requestObj.setCardGenType(Constants.NEW_CARDGEN_UPGRADE);
    cardNumber = generateNewCard();

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Card Number generated--->" + cardNumber);

    if (cardNumber == null || cardNumber.trim().length() == 0) {
      throw new Exception("Invalid new card number received");
    }

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Calling method for updating profile for card--->" +
                                    cardNumber +
                                    "<---Using Existing Profile of card--->" +
                                    requestObj.getExistingCard());
    updateExistingCardHolderProfile(cardNumber);

    return cardNumber;
  }

  protected void updateExistingCardHolderProfile(String cardNumber) throws Exception {
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
      existingProfile = cuDbHandler.getCardHolderProfile(requestObj.getExistingCard());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Updating Existing Profile for New card Number--->" +
                                      cardNumber);
      cuDbHandler.updateCardHolderProfile(existingProfile, cardNumber);
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Updating Card Holder Profile for Existing card Number--->" +
                                      ex);
      throw ex;
    }
  }

}
