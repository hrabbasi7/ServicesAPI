package com.i2c.services.registration.base;
import com.i2c.services.util.*;
import java.sql.Connection;

import com.i2c.chauth.mgr.ChAuthManager;
import com.i2c.chauth.vo.RecordAuthInfoObj;
import com.i2c.auth.MessageInfoObj;
import com.i2c.chauth.jobs.CardGenerator;
import com.i2c.chauth.vo.CardGenerateInfoObj;
import java.io.File;

/**
 * <p>Title: Provides service for transaction processing. </p>
 * <p>Description: The API provides service for processing of the transaction. For example
 * to generate a new card or update the card holder' information.</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public abstract class Transaction {

  private Connection dbConn = null;
  private TransactionDataBaseHandler dbHandler = null;
  private TransactionRequestInfoObj requestObj = null;

  /**
   * Constructor for creation of the instance of the class Transaction.
   * @param dbConn Connection
   * @param requestObj TransactionRequestInfoObj
   */
  public Transaction(Connection dbConn, TransactionRequestInfoObj requestObj) {
    this.dbConn = dbConn;
    this.requestObj = requestObj;
    this.dbHandler = new TransactionDataBaseHandler(dbConn);
  }
  /**
   * Method which the sub-classes must implement for processing the transaction.
   * @return TransactionResponseInfoObj
   */

  public abstract TransactionResponseInfoObj processTransaction() throws Exception;

  /**
   * This method checks the OFAC & AVS status and return true if the OFAC & AVS status is "Ok" else returns false.
   * @return boolean
   */

  protected int checkExistingOFAC_AVS()throws Exception {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for checking Existing OFAC_AVS for card Number--->" +
                                      requestObj.getExistingCard());
      int status = dbHandler.isExistingOFAC_AVSValid(requestObj.
              getExistingCard());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Is Existing OFAC_AVS Valid --->" +
                                      status);
      return status;
  }


  /**
   * This method checks the new OfAC & AVS status, build the RecordAuthInfoObj which contains the authorized information
   * of the OFAC & AVS and returns it. The method first builds the log file path, then translates the request
   * information object into OFAC AVS Request object and check the staus of the OFAC AVS.
   * @return RecordAuthInfoObj
   */

  protected RecordAuthInfoObj checkNewOFAC_AVS() {

    ChAuthManager ofacAvsMgr = null;
    MessageInfoObj ofacAvsRequestInfo = null;
    String logFilePath = null;
    RecordAuthInfoObj ofacAvsResponseInfo = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for checking OFAC_AVS with new Information");
      logFilePath = Constants.LOG_FILE_PATH + File.separator + "services_ofac_avs";
      File f = new File(logFilePath);
      if (!f.exists()) {
        f.mkdirs();
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Log File Path --->" + logFilePath);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Message Info Object ");
      ofacAvsRequestInfo = mapRequestObject();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Calling ChAuthMgr for validating OFAC/AVS");
      ofacAvsMgr = new ChAuthManager();
      ofacAvsResponseInfo = ofacAvsMgr.processChInfo(ofacAvsRequestInfo, dbConn,
          logFilePath);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Resposne returned from ChAuthMgr ---- IsAVSTrue--->" +
                                      ofacAvsResponseInfo.isAvsTrue()
                                      + "<---isOfacTrue--->" +
                                      ofacAvsResponseInfo.isOfacTrue()
                                      + "<---getAvsDescription--->" +
                                      ofacAvsResponseInfo.getAvsDescription()
                                      + "<---getOfacDescription--->" +
                                      ofacAvsResponseInfo.getOfacDescription());

    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in verifying OFAC/AVS using API--->" +
                                      ex);
    }
    return ofacAvsResponseInfo;
  }

  /**
   * This method is used to generate a new card for the given card program id specified in the request.
   * This method first builds the log file path then translates the request object into
   * the CardInfoObject and uses CardGenerator class to generate the new card. At the end of the processing
   * it returns card no of the newly generated card.
   * @return String
   */


  protected String generateNewCard() throws Exception{

    String effectiveDate = null;
    String newCardNumber = null;
    CardGenerator genCardMgr = null;
    CardGenerateInfoObj genInfoObj = null;
    String logFilePath = null;

    try {

      requestObj.setEffectiveDate(CommonUtilities.getCurrentFormatDate(
          Constants.EFFECTIVE_DATE_FORMAT));
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for generating card Number --- Card Program ID--->" +
                                      requestObj.getCardPrgId()
                                      + "<---Effective Date--->" +
                                      requestObj.getEffectiveDate()
                                      + "<---Card Type--->" +
                                      requestObj.getCardType()
                                      + "<----Card Generate Type---->"
                                      + requestObj.getCardGenType()
                                      + "<----Card Source---->"
                                      + requestObj.getExistingCard());
      logFilePath = Constants.LOG_FILE_PATH + File.separator + "services_card_gen";
      File f = new File(logFilePath);
      if (!f.exists()) {
        f.mkdirs();
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Log File Path --->" + logFilePath);
      genInfoObj = new CardGenerateInfoObj();
      genInfoObj.setCardProgram(requestObj.getCardPrgId());
      genInfoObj.setCardEffectiveDate(requestObj.getEffectiveDate());
      genInfoObj.setTypeOfCard(requestObj.getCardType());
      genInfoObj.setCardGenMode(requestObj.getCardGenType());
      genInfoObj.setCardGenSrc(requestObj.getExistingCard());

      genCardMgr = new CardGenerator(logFilePath);
      newCardNumber = genCardMgr.generateCard(dbConn, genInfoObj);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "New Card Number generated --->" +
                                      newCardNumber);
    }
    catch (java.sql.SQLException sqlex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "SQL Exception in Generating Card --- SQL State--->" +
                                      sqlex.getSQLState() + "<--Error Code--->" + sqlex.getErrorCode());
      throw sqlex;
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Generating Card --->" +
                                      ex);
      throw ex;
    }
    return newCardNumber;
  }


  /**
   * This method is used to update the profile of the card holder such as his name, address, zip etc.
   * The method first set the OFAC & AVS status to "OK" in request object and then passes this to the
   * Database Handler class which updates the card holder profile.
   * @param cardNumber String
   */

  protected void updateCardHolderProfile(String cardNumber) throws Exception{

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Updating Card Holder Profile for card Number--->" +
                                      cardNumber);
      requestObj.setOfacStatus(Constants.OFAC_AVS_OK);
      requestObj.setAvsStatus(Constants.OFAC_AVS_OK);
      dbHandler.updateCardHolderProfile(requestObj,cardNumber);
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Updating Card Holder Profile for card Number--->" +
                                      ex);
      throw ex;
    }
  }

  /**
   * This method basically copy an older profile of a card holder to new card.
   * The method first gets the old profile of the card holder with the existing card no and then add it
   * to the new card holder account. The method uses the DatabaseHandler class to get and update the card
   * holder profile.
   * @param cardNumber String
   */

  protected void updateExistingCardHolderProfile(String cardNumber) throws Exception{

    TransactionRequestInfoObj existingProfile = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Updating Card Holder Profile for Exisiting card Number--->" +
                                      cardNumber);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Profile for existing card Number--->" +
                                      requestObj.getExistingCard());
      existingProfile = dbHandler.getCardHolderProfile(requestObj.
          getExistingCard());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Updating Existing Profile for New card Number--->" +
                                      cardNumber);
      dbHandler.updateCardHolderProfile(existingProfile, cardNumber);

    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Updating Card Holder Profile for Existing card Number--->" +
                                      ex);
      throw ex;
    }
  }


  /**
   * This method is used to check whether the card balance is greater or equal to the service fee charged for
   * a transaction. The method basically compare the service fee with card's available fund. If the card funds
   * are lesser then the service fee then it return false else true.
   * @param cardBalance String
   * @param serviceFee String
   * @return boolean
   */


  protected boolean verifyCardFunds(String cardBalance, String serviceFee) {
    boolean isEnoughBalance = false;
    double balance = 0.0;
    double fee = 0.0;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for checking enough balance in card for debiting fee --- Card Balance--->" +
                                      cardBalance + "<---Fee--->" + serviceFee);
      balance = Double.parseDouble(cardBalance);
      fee = Double.parseDouble(serviceFee);

      if (fee <= balance) {
        return true;
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in  checking enough balance in card for debiting fee--->" +
                                      ex);
    }
    return isEnoughBalance;
  }


  /**
   * This method is used to check whether the application of the service fee should be yes or no.
   * The method check the service fee if it is greater then zero then it mean that application of the service
   * fee is allowed and return true else it returns false.
   * @param serviceFee String
   * @return boolean
   */

  protected boolean applyServiceFee(String serviceFee) {
    boolean doApply = false;
    double fee = 0.0;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for checking Whether Service Fee is > 0 --- Service Fee--->" +
                                      serviceFee);
      fee = Double.parseDouble(serviceFee);
      if (fee > 0) {
        return true;
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in checking Whether Service Fee is > 0 --->" +
                                      ex);
    }
    return doApply;
  }


  /**
   * This method simply copy one bean attribute to another bean.
   * @return MessageInfoObj
   */

  private MessageInfoObj mapRequestObject() {
    MessageInfoObj reqObj = new MessageInfoObj();
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Method for mapping ofac/avs request object");
    reqObj.setInstProgID(requestObj.getCardPrgId());
    reqObj.setShippingMethod(requestObj.getShippingMethod());
    reqObj.setFirstName(requestObj.getFirstName());
    reqObj.setLastName(requestObj.getLastName());
    reqObj.setSsn(requestObj.getSsn());
    reqObj.setEmail(requestObj.getEmailAddress());
    reqObj.setCurrentAddress1(requestObj.getAddress1());
    reqObj.setCurrentAddress2(requestObj.getAddress2());
    reqObj.setCurrentCity(requestObj.getCity());
    reqObj.setCurrentState(requestObj.getState());
    reqObj.setCurrentZip(requestObj.getZip());
    reqObj.setApplyDate(CommonUtilities.getCurrentFormatDate(Constants.WEB_DATE_FORMAT)); //Must be in MM/dd/yyyy
    reqObj.setDriverLicenseNo(requestObj.getDrivingLisenseNumber());
    reqObj.setDriverLicenseState(requestObj.getDrivingLisenseState());
    reqObj.setActionType(requestObj.getActionType());

    return reqObj;
  }


  /**
   * The method is used to prepare the response for the client from the given parameters. It
   * simply creates a response bean which holds the attributes of the response.
   * @param cardNo String
   * @param serviceID String
   * @param responseCode String
   * @param Description String
   * @param amount String
   * @return TransactionResponseInfoObj
   */
  protected TransactionResponseInfoObj prepareClientResponse(String cardNo,
      String serviceID, String responseCode, String Description, String amount) {
    DbRequestInfoObject logInfo = new DbRequestInfoObject();
    DbResponseInfoObject transInfo = new DbResponseInfoObject();
    TransactionResponseInfoObj clientResponse = new TransactionResponseInfoObj();

    String cardBalance = null;
    String accoutNumber = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for preparing client response");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Logging transaction in Database with Info");
      if(cardNo != null){
        cardBalance = dbHandler.getCardLedgerBalance(cardNo);
        accoutNumber = dbHandler.getCardAccountNumber(cardNo);
      }

      logInfo.setCardNo(cardNo);
      logInfo.setServiceId(serviceID);
      logInfo.setRetRefNumber(null);
      logInfo.setMsgType("0200");
      logInfo.setRemainingBalance(cardBalance);
      logInfo.setAmount(amount);
      logInfo.setDescription(Description);
      logInfo.setAccountNumber(accoutNumber);
      logInfo.setInsertMode("0");
      logInfo.setDeviceType(requestObj.getDeviceType());
      logInfo.setResponseCode(responseCode);
      logInfo.setDeviceId(null);
      logInfo.setCrdAceptorCode(null);
      logInfo.setCrdAceptorName(null);
      logInfo.setMerchantCatCd(null);
      logInfo.setAcquirerId(null);

      transInfo = dbHandler.logTransaction(logInfo);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ISO Serial No --->" +
                                      transInfo.getIsoSerialNo()
                                      + "<---Trace Audit Number--->" +
                                      transInfo.getTraceAuditNo());
      clientResponse.setResposneCode(responseCode);
      clientResponse.setResposneDescription(Description);
      clientResponse.setIsoSerialNumber(transInfo.getIsoSerialNo());
      clientResponse.setTraceAuditNumber(transInfo.getTraceAuditNo());
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in preparing client response --->" +
                                      ex);
    }


    return clientResponse;
  }

  protected TransactionResponseInfoObj prepareClientResponse(String cardNo,
      String serviceID, String responseCode, String Description, String amount,
      String acquirerID,
      String cardAccptId,
      String cardAccptName,
      String mcc,
      String retRefNum,
      String deviceId) throws Exception{
    DbRequestInfoObject logInfo = new DbRequestInfoObject();
    DbResponseInfoObject transInfo = new DbResponseInfoObject();
    TransactionResponseInfoObj clientResponse = new TransactionResponseInfoObj();

    String cardBalance = null;
    String accoutNumber = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for preparing client response");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Logging transaction in Database with Info");
      if(cardNo != null){
        cardBalance = dbHandler.getCardLedgerBalance(cardNo);
        accoutNumber = dbHandler.getCardAccountNumber(cardNo);
      }

      logInfo.setCardNo(cardNo);
      logInfo.setServiceId(serviceID);
      logInfo.setRetRefNumber(retRefNum);
      logInfo.setMsgType("0200");
      logInfo.setRemainingBalance(cardBalance);
      logInfo.setAmount(amount);
      logInfo.setDescription(Description);
      logInfo.setAccountNumber(accoutNumber);
      logInfo.setInsertMode("0");
      logInfo.setDeviceType(requestObj.getDeviceType());
      logInfo.setResponseCode(responseCode);
      logInfo.setDeviceId(deviceId);
      logInfo.setCrdAceptorCode(cardAccptId);
      logInfo.setCrdAceptorName(cardAccptName);
      logInfo.setMerchantCatCd(mcc);
      logInfo.setAcquirerId(acquirerID);

      transInfo = dbHandler.logTransaction(logInfo);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ISO Serial No --->" +
                                      transInfo.getIsoSerialNo()
                                      + "<---Trace Audit Number--->" +
                                      transInfo.getTraceAuditNo());
      clientResponse.setResposneCode(responseCode);
      clientResponse.setResposneDescription(Description);
      clientResponse.setIsoSerialNumber(transInfo.getIsoSerialNo());
      clientResponse.setTraceAuditNumber(transInfo.getTraceAuditNo());
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in preparing client response --->" +
                                      ex);
      throw ex;
    }


    return clientResponse;
  }


}
