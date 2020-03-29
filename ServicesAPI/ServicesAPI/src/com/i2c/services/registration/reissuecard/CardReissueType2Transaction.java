package com.i2c.services.registration.reissuecard;

import com.i2c.services.registration.base.Transaction;
import com.i2c.services.registration.base.TransactionRequestInfoObj;
import com.i2c.services.registration.base.InformationValidator;
import com.i2c.services.registration.base.DbRequestInfoObject;
import com.i2c.services.registration.base.DbResponseInfoObject;
import com.i2c.services.registration.base.TransactionResponseInfoObj;
import com.i2c.services.util.*;
import java.sql.*;

/**
 * <p>Title: This class is used to update the card attributes. </p>
 * <p>Description: This class perfroms the operations for updating the card attributes</p>
 * <p>Copyright: Copyright (c) 2006 Innvative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class CardReissueType2Transaction extends Transaction {

  private TransactionRequestInfoObj requestObj = null;
  private Connection dbConn = null;

  /**
   * Constructor which initlizes the database connection and request bean attributes. Request bean contain
   * the attributes contained in the request.
   * @param dbConn Connection
   * @param requestObj TransactionRequestInfoObj
   */
  public CardReissueType2Transaction(Connection dbConn,
                                     TransactionRequestInfoObj requestObj) {
    super(dbConn, requestObj);
    this.dbConn = dbConn;
    this.requestObj = requestObj;
  }



  /**
    * This method is used to update the card attributes. It first validates the mandatory attributes
    * and returns response with appropiate error response code in the following cases <br>
    * 1. Mandatory attributes for the re-issue card (reponse code 06 is reutned if invalidate)<br>
    * 2. Validate card no (response code 14 is returned if invalid card no)
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

    String expiryMonths = null;
    String currExpiryDate = null;
    String expiryDate = null;

    String cardProgram = null;
    CardReissueInformationValidator validator = null;
    CardReissueDataBaseHandler crDBHndlr = null;
    DbRequestInfoObject dbDAO = null;
    DbResponseInfoObject dbResponse = new DbResponseInfoObject();
    TransactionResponseInfoObj clientResposne = new TransactionResponseInfoObj();

    boolean applyFee = false;
    String serviceFee = null;

    String switchID = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Processing Card Reissue Transaction --- Reissue Type--->" +
                                      requestObj.getReissueType());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating Mandatory Attibutes");
      validator = new CardReissueInformationValidator(requestObj);
      try {
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

      //Checking Exisiting Card valid
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating exisiting card--->" +
                                      requestObj.getExistingCard());
      crDBHndlr = new CardReissueDataBaseHandler(this.dbConn);

      if (!crDBHndlr.isCardNumberValid(requestObj.getExistingCard())) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "14",
                                               "Provided Existing Card status is not valid--->" + crDBHndlr.maskCardNo(requestObj.getExistingCard()), null);
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

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting switch against the given Card Program --- Card Program ID--->" +
                                      cardProgram);

      switchID = crDBHndlr.getSwitchID(cardProgram);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Batch Status for switch --->" +
                                      switchID);

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

      if (switchID == null || !crDBHndlr.isSwitchActive(switchID)) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "91",
                                               "Card Reissue not supported for inactive switch", null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;

      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking Service Allowed --- Service ID--->" +
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
                                      "Getting Current Expiry Date for card--->" +
                                      requestObj.getExistingCard());

      currExpiryDate = crDBHndlr.getCurrentExpiruDate(requestObj.getExistingCard());

      if (currExpiryDate == null || currExpiryDate.trim().length() == 0) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "06",
                                               "Unable to get current expiry date for provided card", null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Found Current Expiry Date for card--->" +
                                      currExpiryDate);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Expiration months defined for card program id--->" +
                                      cardProgram);

      expiryMonths = crDBHndlr.getCardExpirationMonths(cardProgram);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Found Expiration months defined for card program id--->" +
                                      expiryMonths);

      if (expiryMonths == null || expiryMonths.trim().length() == 0) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "06",
                                               "Unable to get expiration months against card program", null);
        clientResposne.setFeeAmount("0.00");
        return clientResposne;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Calculating new Expiry date for card--->" +
                                      requestObj.getExistingCard());

      expiryDate = CommonUtilities.addDaysInDate(Constants.DATE_FORMAT,
                                                 currExpiryDate, expiryMonths, Constants.DATE_FORMAT,java.util.GregorianCalendar.MONTH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Calculated new Expiry date for card--->" +
                                      expiryDate);
      if (expiryDate == null || expiryDate.trim().length() == 0) {
        clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                               Constants.CARD_REISSUE, "06",
                                               "Unable to calculate new expiration date", null);
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
                                                 "Invalid Service Fee", null);
          clientResposne.setFeeAmount("0.00");
          return clientResposne;
        }
        if (serviceFeeList[1] != null &&
            !serviceFeeList[1].equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                                 Constants.CARD_REISSUE,
                                                 serviceFeeList[1],
                                                 "Card Reissue-- " +
                                                 serviceFeeList[2], null);
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

        if (applyFee) {

          dbDAO = new DbRequestInfoObject();

          dbDAO.setCardNo(requestObj.getExistingCard());
          dbDAO.setServiceId(Constants.CARD_REISSUE);
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
                                          dbDAO.getSkipStatus() + "<---getIsOKNegBal--->" +
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

          dbDAO = new DbRequestInfoObject();

          dbDAO.setCardNo(requestObj.getExistingCard());
          dbDAO.setServiceId(Constants.CARD_REISSUE);
          dbDAO.setDeviceType(requestObj.getDeviceType());
          dbDAO.setFeeAmount(serviceFee);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Applying Fee on Existing Card --- getCardNo--->" +
                                          dbDAO.getCardNo()
                                          + "<---getServiceId--->" +
                                          dbDAO.getServiceId()
                                          + "<---getDeviceType--->" +
                                          dbDAO.getDeviceType()
                                          + "<---getFeeAmount--->" +
                                          dbDAO.getFeeAmount()
                                          );

          dbResponse = crDBHndlr.debitServiceFee(dbDAO);
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
        } //end applyFee > 0
      } //end if Apply Fee = Y
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Updating Expiry Date for card--->" +
                                      requestObj.getExistingCard() +
                                      "<---New Expiry Date--->" + expiryDate +
                                      "<----Card status to 'A'---->");
      crDBHndlr.updateCardAttrForReissueType2(requestObj.getExistingCard(),
                                              expiryDate);

      clientResposne = prepareClientResponse(requestObj.getExistingCard(),
                                             Constants.CARD_REISSUE, "00",
                                             "Card Reissue for Same card with updated expiry completed successfully", null);
      clientResposne.setFeeAmount(serviceFee);
      clientResposne.setCardBalance(crDBHndlr.getCardBalance(requestObj.
          getExistingCard()));
      clientResposne.setNewExpiry(expiryDate);
      return clientResposne;
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Processing trasnaction for Card Reissue Type 2--->" +
                                      ex);
      try {
        if (dbConn != null) {
          dbConn.rollback();
        }
      }
      catch (SQLException sqlex) {
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
}
