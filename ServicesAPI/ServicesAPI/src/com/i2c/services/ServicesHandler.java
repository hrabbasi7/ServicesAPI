package com.i2c.services;

import java.sql.*;

import com.i2c.services.handlers.*;
import com.i2c.services.home.*;
import com.i2c.services.registration.base.*;
import com.i2c.services.util.*;

import com.i2c.services.registration.purchaseorder.PurchaseOrderTransaction;
import com.i2c.services.registration.purchaseorder.NewCardPOFundsFromOldCard;
import com.i2c.services.registration.purchaseorder.PurchaseOrderTransactionWithExistingCard;
import com.i2c.services.registration.cardupgrade.CardUpgradeTransaction;
import com.i2c.services.registration.cardupgrade.CardUpgradeWithAllCashOut;
import com.i2c.services.registration.cardupgrade.
    CardUpgradeWithAmountTransferTransaction;

import com.i2c.services.registration.reissuecard.CardReissueType1Transaction;
import com.i2c.services.registration.reissuecard.CardReissueType2Transaction;
import com.i2c.services.registration.reissuecard.CardReissueType3Transaction;

import com.i2c.services.acqinstauthneticator.*;
import com.i2c.service.hsm.HSMService;


/**
 * <p>Title: ServicesHandler: This class provides interface to invoke different services</p>
 * <p>Description: This class is used to invoke the different services such as card service
 * for handling cards</p>
 * <p><b>Note1 :</b> The methods of this class should not be called with an open ResultSet
 * <p><b>Note2 :</b> It is strictly assumed that parameters passed to each method will be validated at Caller's End
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class ServicesHandler {

  private Connection con = null;
  private boolean conAutoCommit;

  private ServicesHandler(String servicesLogPath) {
    Constants.LOG_FILE_PATH = servicesLogPath;
    Constants.HSM_LOG_FILE_PATH = servicesLogPath;
    Constants.FUR_LOG_FILE_PATH = servicesLogPath;
    Constants.TRANSFERAPI_LOG_PATH = servicesLogPath + java.io.File.separator +
        "transferapi";
//    new HSMService(Constants.HSMSERVICE_LOG_PATH, Constants.HSMSERVICE_CONF_PATH, con);
  } //end Constructor ServicesHandler

  /**
   * This method gets the instance of ServicesHandler Class
   * @param _con Connection -- Connection Object
   * @param servicesLogPath String -- Path to form the log file
   * @return ServicesHandler
   */
  public static ServicesHandler getInstance(Connection _con,
                                            String servicesLogPath) {
    ServicesHandler handler = new ServicesHandler(servicesLogPath);

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Connection -- > " + _con +
                                      " && My Conn -- > " + handler.con);
      handler.con = _con;
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Connection -- > " + _con +
                                      " && My Conn -- > " + handler.con);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Auto Commit -- > " +
                                      handler.con.getAutoCommit());
      //preserve the connection's autocommit condition
      handler.conAutoCommit = handler.con.getAutoCommit();
      if (handler.con.getAutoCommit())
        handler.con.setAutoCommit(false);
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception while changing the connection's autocommit...." +
                                      exp.getMessage());
    } //end catch

    return handler;
  } //end getInstance

  /**
   * This method is to echo the MCP System whether it is up or not
   * @return ServicesResponseObj <br>
   * <br>1. Response Code ---- 00= success
   * <br>2. Response Description
   */
  public ServicesResponseObj echo() {
    ServicesResponseObj respObj = new ServicesResponseObj();
    respObj.setRespCode(Constants.SUCCESS_CODE);
    respObj.setRespDesc(Constants.SUCCESS_MSG);
    try {
      con.setAutoCommit(this.conAutoCommit);
    } //end try
    catch (Exception ex) {} //end
    return respObj;
  } //end echo

  /**
   * This method is used to get the latest status of the supplied card such as whether the card is active
   * or not.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * <br>12. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>13. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>14.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>15. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Card Status Code String, Status Code of the card if response is successful else null
   * <br>4. Card Status Description String, Description of Card Status if response is successful else null
   * <br>5. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>6. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj getCardStatus(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //get the service ID
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Card Program ID for Card--->" +
                                      requestObj.getCardNo());
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //   String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(requestObj.getDeviceType(), "S1");

      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
          requestObj.getDeviceType(), "S1", "00", cardPrgId);

      if (serviceId != null)

        //Constants.GET_CARD_STATUS_SERVICE = serviceId;
        requestObj.setServiceId(serviceId); // above line replaced with this by imtiaz
      else requestObj.setServiceId(Constants.GET_CARD_STATUS_SERVICE); // added by imtiaz
      //call the get card status
      respObj = CardsServiceHandler.getInstance(con).getCardStatus(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getCardStatus

  /**
   * This method is used to activate the specified card and also to load the amount after activating if amount is supplied
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
  public ServicesResponseObj activateCard(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Activate Card --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Activate Card --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Activate Card --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      respObj = CardsServiceHandler.getInstance(con).activateCard(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end activateCard

  /**
   * This method is used to add the supplied amount in the supplied card
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Amount String, Amount to add in the balance of the supplied card -- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>9. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>10. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>11.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>12. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
//  public ServicesResponseObj addFunds(ServicesRequestObj requestObj) throws
//      Exception {
//    //make the response object
//    ServicesResponseObj respObj = new ServicesResponseObj();
//
//    try {
//      respObj = FinancialServiceHandler.getInstance(con).addFunds(requestObj);
//      //commit all the work
//      con.commit();
//      //return the result
//      return respObj;
//    } //end try
//    catch (Exception exp) {
//      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
//                                      "Exception  -- > " + exp.getMessage());
//      try {
//        if (con != null)
//          con.rollback();
//      } //end try
//      catch (Exception ex) {} //end
//
//      respObj.setRespCode("96");
//      respObj.setRespDesc("System Error");
//      return respObj;
//    } //end catch
//    finally {
//      //reset the connection's autocommit condition
//      con.setAutoCommit(this.conAutoCommit);
//    } //end finally
//  } //end addFunds

  /**
   * This method is used to release all the cash from the supplied card. First it finds the service id
   * for the given request and then uses FinancialServiceHandler class to set the card blance to zero.
   * If an error occur during processing then this method throws exception
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Cashout amount String, The Amount which has been cashed out from the supplied card if applyFee = Y else NULL
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

  //////////////////////// Changed////////////////////////////////////////////////

  public ServicesResponseObj allCashOut(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con); // line uncommented by imtiaz

    try {
      //get the service ID
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG), // line uncommented by imtiaz
                                      "Getting Card Program ID for Card--->" +
                                      requestObj.getCardNo()); // line uncommented by imtiaz
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo()); //line uncommented by imtiaz

      //String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(requestObj.getDeviceType(), "C1");
      /////////////////////// Above Line Was Replaced With Below Line By Imtiaz ///////////////////////
      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
          requestObj.getDeviceType(), "C1", "00", cardPrgId);

//      if (serviceId != null)
//        Constants.ALL_CASH_OUT_SERVICE = serviceId;
/////////////////////// Above Line Was Replaced With Below Line By Imtiaz ///////////////////////
      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0) {
        requestObj.setServiceId(serviceId);
      }
      else {
        requestObj.setServiceId(Constants.ALL_CASH_OUT_SERVICE);
      }
//////////////////////////////////////////////////////////////////////////////////////////////////

      //call the All Cash Out
      respObj = FinancialServiceHandler.getInstance(con).allCashOut(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  /**
   * This method is used to change the ACH Account Information (for ACH Account for Withdraw only).
   * It first finds the service id for the given request and then uses the ACHServiceHandler to
   * perform the requested operation.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Nick Name String, ACH Account Nick Name --- Optional
   * <br>7. Bank Name String, Name of the Bank --- Optional
   * <br>8. Bank Address String, Address of the Bank --- Optional
   * <br>9. Account No String, Bank Account No --- Optional
   * <br>10. Account Title String, Bank Account Title --- Optional
   * <br>11. Account Type String , Bank Account Type - 11 = Checking and 01 = Savings --- Optional
   * <br>12. Routing No String, Bank Routing No --- Optional
   * <br>13. ACH Account No String, ACH Account Identifier against which the Information will be changed --- Mandatory
   * <br>14. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>15. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>16. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>17. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>18.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>19. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */


  //////////////////////////     Changed          ///////////////////////////////////////

  public ServicesResponseObj changeACHAccountInfo(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);
    try {
      //get the service ID
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo()); // This line added by Imtiaz

      //String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(requestObj.getDeviceType(), "AC");
      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
          requestObj.getDeviceType(), "AC", "00", cardPrgId);
      //if (serviceId != null) Constants.CHANGE_ACH_ACCT_INFO_SERVICE = serviceId;
      ///////////// Above Line Were replaced with these lines
      if (serviceId != null && serviceId.trim().length() > 0 &&
          !serviceId.equals("")) {
        requestObj.setServiceId(serviceId);
      }
      else {
        requestObj.setServiceId(Constants.CHANGE_ACH_ACCT_INFO_SERVICE);
      }

      respObj = ACHServiceHandler.getInstance(con).changeACHAccountInfo(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  /**
   * This method is used to change the Account Nick of the verified ACH Account (for Load and withdraw both).
   * It first finds the service id for the given request and then uses the ACH service handler to chnage the
   * nick of the account
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Nick Name String, ACH Account Nick Name to Change --- Mandatory
   * <br>7. ACH Account No String, ACH Account No to change the nick name --- Mandatory
   * <br>8. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>9. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>10. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>11. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>12. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>13. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

  //////////////////////////////////// Changed /////////////////////////////////////////////////////

  public ServicesResponseObj changeACHAccountNick(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //get the service ID
      //////////// Line added By Imtiaz ////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //////////////////////////////////////////////////////////////

      //String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
      //    requestObj.getDeviceType(), "AB");
      //if (serviceId != null)
      //Constants.CHANGE_ACH_ACCOUNT_SERVICE = serviceId;
      // Above Commented Line Were Replaced With These Line By Imtiaz///////
      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
          requestObj.getDeviceType(), "AB", "00", cardPrgId);
      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0) {
        requestObj.setServiceId(serviceId);
      }
      else {
        requestObj.setServiceId(Constants.CHANGE_ACH_ACCOUNT_SERVICE);
      }

      respObj = ACHServiceHandler.getInstance(con).changeAchAccountNick(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  /**
   * This method is used to create the ACH Account for ACH Load Transactions only.
   * It first finds the service id for the given request and then uses the ACH service handler to create
   * the ACH account
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Nick Name String, ACH Account Nick Name --- Mandatory
   * <br>7. Bank Name String, Name of the Bank --- Optional
   * <br>8. Bank Address String, Address of the Bank --- Optional
   * <br>9. Account No String, Bank Account No --- Mandatory
   * <br>10. Account Title String, Bank Account Title --- Optional
   * <br>11. Account Type String , Bank Account Type - 11 = Checking and 01 = Savings --- Mandatory
   * <br>12. Routing No String, Bank Routing No --- Mandatory
   * <br>13. ACH Account No String, ACH Account Identifier against which the Information will be changed --- Mandatory
   * <br>14. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>15. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>16. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>17. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>18. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>19. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. ACH Account ID String, Unique identification of the ACH Account created in case of success else null
   * <br>4. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */


  //////////////////////////  Changed /////////////////////////////////////////////

  public ServicesResponseObj createACHAccountForLoad(ServicesRequestObj
      requestObj) throws Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //set the ach type to 1
      requestObj.setAchType("1");
      //////////////////// Follwing Lines were Added By Imtiaz ///////////////////////////
      //requestObj.setTypeOfTrans("A6"); // This line added by Imtiaz
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      ////////////////////////////////////////////////////////////////////////////////////

      //get the service ID
      //String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
      //  requestObj.getDeviceType(), "A6");
      //if (serviceId != null)
      //Constants.ACH_ACCOUNT_FEE = serviceId;
      //else
      // Constants.ACH_ACCOUNT_FEE = "ADD_ACH_AC_LOAD";

      ////////// Above Commented Line were Replaced With These Lines By Imtiaz ///////////////
      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
          requestObj.getDeviceType(), "43", "00", cardPrgId);
      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId("ACH_REG");

      respObj = ACHServiceHandler.getInstance(con).createACHAccount(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  /**
   * This method is used to create the ACH Account for ACH Load and Withdraw only.
   * It first finds the service id for the given request and then uses the ACH service handler to create
   * the ACH account for Load & Withdraw purpose
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Nick Name String, ACH Account Nick Name --- Mandatory
   * <br>7. Bank Name String, Name of the Bank --- Optional
   * <br>8. Bank Address String, Address of the Bank --- Optional
   * <br>9. Account No String, Bank Account No --- Mandatory
   * <br>10. Account Title String, Bank Account Title --- Optional
   * <br>11. Account Type String , Bank Account Type - 11 = Checking and 01 = Savings --- Mandatory
   * <br>12. Routing No String, Bank Routing No --- Mandatory
   * <br>13. ACH Account No String, ACH Account Identifier against which the Information will be changed --- Mandatory
   * <br>14. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>15. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>16. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>17. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>18. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>19. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. ACH Account ID String, Unique identification of the ACH Account created in case of success else null
   * <br>4. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj createACHAccountForLoadAndWithdraw(
      ServicesRequestObj requestObj) throws Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);
    try {
      //set the ach type to 1
      requestObj.setAchType("1");
      //////////////////////// Following Lines Were Added By Imtiaz ///////////////////////////
      //requestObj.setTypeOfTrans("A7");
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      ////////////////////////////////////////////////////////////////////////////////////////

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "A7");
//      if (serviceId != null)
//        Constants.ACH_ACCOUNT_FEE = serviceId;
//      else
//        Constants.ACH_ACCOUNT_FEE = "ADD_ACH_AC_WL";

      ///////////////////////// Above Commented Lines Were Replaced With Following Line //////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "43", "00", cardPrgId);
      if (serviceId != null && !serviceId.trim().equals("") &&
          serviceId.length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId("ACH_REG");
        ////////////////////////////////////////////////////////////////////////////////////////////////////////

      respObj = ACHServiceHandler.getInstance(con).createACHAccount(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  /**
   * This method is used to create the ACH Account for ACH Withdraw only.
   * It first finds the service id for the given request and then uses the ACH service handler
   * to create the ACH account for Withdraw purpose
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Nick Name String, ACH Account Nick Name --- Mandatory
   * <br>7. Bank Name String, Name of the Bank --- Optional
   * <br>8. Bank Address String, Address of the Bank --- Optional
   * <br>9. Account No String, Bank Account No --- Mandatory
   * <br>10. Account Title String, Bank Account Title --- Optional
   * <br>11. Account Type String , Bank Account Type - 11 = Checking and 01 = Savings --- Mandatory
   * <br>12. Routing No String, Bank Routing No --- Mandatory
   * <br>13. ACH Account No String, ACH Account Identifier against which the Information will be changed --- Mandatory
   * <br>14. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>15. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>16. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>17. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>18. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>19. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. ACH Account ID String, Unique identification of the ACH Account created in case of success else null
   * <br>4. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj createACHAccountForWithdraw(ServicesRequestObj
      requestObj) throws Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //set the ach type to 2
      requestObj.setAchType("2");

      ////////////////////////// Following Lines Were Added By Imtiaz //////////////////////////
      //requestObj.setTypeOfTrans("A5");
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      /////////////////////////////////////////////////////////////////////////////////////////

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "A5");
//      if (serviceId != null)
//        Constants.ACH_ACCOUNT_FEE = serviceId;
//      else
//        Constants.ACH_ACCOUNT_FEE = "ADD_ACH_AC_WITHD";

      /////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "43", "00", cardPrgId);
      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId("ACH_REG");
        ///////////////////////////////////////////////////////////////////////////////////////////////////

      respObj = ACHServiceHandler.getInstance(con).createACHAccount(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  /**
   * This method is used to credit the amount in the supplied Card. It first validates the amount which
   * is being credited then it finds the service id for the given request and uses the
   * FinancialServiceHandler class to credit the amount from the given card no
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Amount String, Amount to add in the balance of the supplied card -- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>9. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>10. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>11. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>12. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

  //////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj creditFunds(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //////////////// Follwing Lines Were Added By Imtiaz /////////////////////////////////////////

      //requestObj.setTypeOfTrans("21");
      ///////////////////////////////////////////////////////////////////////////////////////////////

//     //get the service ID
//     if (requestObj.getServiceId() != null &&
//          requestObj.getServiceId().trim().length() > 0) {
//        Constants.ADD_FUNDS = requestObj.getServiceId();
//      }
//      else {
//        String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//            requestObj.getDeviceType(), "21");
//        if (serviceId != null)
//          Constants.ADD_FUNDS = serviceId;
//        else
//          Constants.ADD_FUNDS = "SW_DEPOSIT";
//      }

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Credit Funds --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Credit Funds --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Credit Funds--- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      }else{
        respObj.setRespCode("13");
        respObj.setRespDesc("Invalid Amount");
        return respObj;
      }

      ////////////// Above Commented Lines Were Replaced With These Lines By Imtiaz ///////////////
      if (requestObj.getServiceId() == null ||
          requestObj.getServiceId().trim().length() == 0) {
        String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());

        String serviceId = serviceHome.huntForServiceId(requestObj.
            getDeviceType(), "21", "00", cardPrgId);

        if (serviceId != null && serviceId.trim().length() > 0 &&
            !serviceId.equals(""))
          requestObj.setServiceId(serviceId);
        else
          requestObj.setServiceId("SW_DEPOSIT");
      }

      respObj = FinancialServiceHandler.getInstance(con).addFunds(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end creditFunds

  /**
   * This method is used to deactivate the supplied card. It uses the CardServiceHandler class to
   * deactivate the card
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
  public ServicesResponseObj deactivateCard(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      respObj = CardsServiceHandler.getInstance(con).deActivateCard(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  /**
   * This method is used to debit the supplied amount from the supplied card account. It first validates
   * the amount which is being debited from the given card then it fetches the service from the database
   * for the given card and uses the FinancialServiceHandler class to debit the fund from the given
   * card no
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Amount String, Amount to debit in the balance of the supplied card -- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>9. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>10. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>11.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>12. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

  //////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj debitFunds(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);
    try {

      /////////////////////////////// Follwoing Lines Were Added By Imtiaz //////////////////////////

      //requestObj.setTypeOfTrans("01");
      //////////////////////////////////////////////////////////////////////////////////////////////

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Debit Funds --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Debit Funds --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Debit Funds--- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      }else{
        respObj.setRespCode("13");
        respObj.setRespDesc("Invalid Amount");
        return respObj;
      }

      //make the amount to negative
      double nBal = Double.parseDouble(requestObj.getAmount());

      if (nBal > 0)
        nBal *= -1;
        //set it against in request
      requestObj.setAmount(nBal + "");
      //get the service ID

//      if (requestObj.getServiceId() != null &&
//          requestObj.getServiceId().trim().length() > 0) {
//        Constants.ADD_FUNDS = requestObj.getServiceId();
//      }
//      else {
//        String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//            requestObj.getDeviceType(), "01");
//
//        if (serviceId != null)
//          Constants.ADD_FUNDS = serviceId;
//        else
//          Constants.ADD_FUNDS = "WS_WITHD";
//      }

      ////////////////////////// Above Commented Lines Were Replaced With These Lines By Imtiaz ///////////
      if (requestObj.getServiceId() == null ||
          requestObj.getServiceId().trim().length() == 0) {

        String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());

        String serviceId = serviceHome.huntForServiceId(requestObj.
            getDeviceType(), "01", "00", cardPrgId);

        if (serviceId != null && !serviceId.equals("") &&
            serviceId.trim().length() > 0)
          requestObj.setServiceId(serviceId);
        else
          requestObj.setServiceId("WS_WITHD");

      }
      ////////////////////////////////////////////////////////////////////////////////////////////////

      //pass the call to the appropriate handler
      respObj = FinancialServiceHandler.getInstance(con).addFunds(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //debitFunds

  /**
   * This method is used to settle the pre authorized transaction. It first validates the amount, if
   * it is invalid then it pauses the execution and returns response to the client. It then gets the
   * card program id and the service id for the given request and uses the FinancialServiceHanlder class
   * to settle the pre-authorized transaction. At the end of the processing it returns response to the
   * client.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Pre Auth Trans Id String, Pre Authorization Transaction ID -- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Description  String, Description or comments -- Optional
   * <br>9. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>10. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>11. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>12. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>13. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */


//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj forcePostTransaction(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Force Post Transaction --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Force Post Transaction --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Force Post Transaction --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      /////////////////// Followiong Lines Were Added By Imtiaz /////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTypeOfTrans("T3");
      ///////////////////////////////////////////////////////////////////////////////
//           //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "T3");
//      if (serviceId != null)
//        Constants.FORCE_POST_AUTHORIZATION = serviceId;
//
      /////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz ////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "00", "00", cardPrgId);
      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.FORCE_POST_AUTHORIZATION);
        ////////////////////////////////////////////////////////////////////////////////////////////////

      if (requestObj.getAmount() != null &&
          !requestObj.getAmount().trim().equals("")) {
        //invert the amount
        requestObj.setAmount("-" + requestObj.getAmount());
      } //end if
//
      //call the Force Post Trans
      respObj = FinancialServiceHandler.getInstance(con).forcePostTransaction(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end forcePostTransaction

  /**
   * This method is used to get the status of the supplied ACH Account. It first finds the service id
   * and uses the ACH service handler class to get the status of the given ACH Account. At the end
   * of the processing it returns response to the client which describes the status of the processing.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. ACH Account No String, ACH Account No to get the status -- Mandatory
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. ACH Account Status Code String, Status Code
   * <br>5. ACH Account Status Description String, Status Description
   * <br>6. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////

  public ServicesResponseObj getACHAccountStatus(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //get the service ID
      /////////////// Following Lines were Added By Imtiaz ///////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTypeOfTrans("AA");
      ////////////////////////////////////////////////////////////////////////////////////
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "AA");
//      if (serviceId != null)
//        Constants.ACH_ACCT_STATUS_SERVICE = serviceId;

      /////////////// Above Commented Lines Were Replaced With These Lines By Imtiaz ///////////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "AA", "00", cardPrgId);
      if (serviceId != null && serviceId.trim().length() > 0 &&
          !serviceId.equals(""))
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.ACH_ACCT_STATUS_SERVICE);
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        //call the Verfiy status
      respObj = ACHServiceHandler.getInstance(con).getAchAccountStatus(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  /**
   * This method is used to get the Access Code of the supplied Card. It first finds the service id
   * for the given request and uses the CardServiceHanlder class to gets the Access Code. It returns the
   * response code to the client at the end of the processing.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. AAC String, Required Access Code in case of success only else null
   * <br>5. Balance String, The current balance of the supplied card in case of success else null
   * <br>6. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj getCardAccessCode(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      /////////////////// Following Lines were Added By Imtiaz       ///////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTypeOfTrans("A1");
      //////////////////////////////////////////////////////////////////////////////////////////////
      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "A1");
//      if (serviceId != null)
//        Constants.GET_AAC_SERVICE = serviceId;
      ////////////// Above Commented Lines were replaced with threse Lines By Imtiaz //////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "A1", "00", cardPrgId);

      if (serviceId != null && serviceId.trim().length() > 0 &&
          !serviceId.equals(""))
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.GET_AAC_SERVICE);
        ////////////////////////////////////////////////////////////////////////////////////////////
        //call the get card AAC
      respObj = CardsServiceHandler.getInstance(con).getCardAccessCode(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getCardAccessCode

  /**
   * This method is used to get the current balance of the supplied card. It first finds the card program
   * and the service id for the given request and uses the FinancialServiceHandler class to find the
   * balance of the requested card. At the end of the processing it returns response to the client
   * which describes the processing status in detail with proper response code and the response description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj getCardBalance(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);
//    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);
    try {
      //get the service ID
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Card Program ID for Card--->" +
                                      requestObj.getCardNo());

      /////////////////// Following Lines were Added By Imtiaz       ///////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTypeOfTrans("31");
      //////////////////////////////////////////////////////////////////////////////////////////////


//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(requestObj.getDeviceType(), "31");
//      if (serviceId != null)
//        requestObj.setServiceId(serviceId);
//      else
//        requestObj.setServiceId(Constants.BALANCE_INQUIRY_SERVICE);

      ////////////// Above Commented Lines were replaced with threse Lines By Imtiaz //////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "31", "00", cardPrgId);
      if (serviceId != null && serviceId.trim().length() > 0 &&
          !serviceId.equals(""))
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.BALANCE_INQUIRY_SERVICE);
        ////////////////////////////////////////////////////////////////////////////////////////////

        //call the perform reversal
      respObj = FinancialServiceHandler.getInstance(con).getCardBalance(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //getCardBalance

  /**
   * This method is used to get the Accounts associated with the supplied card. It firsts finds the service
   * id for the given request and then uses the CardServiceHandler class to the the account associated
   * with the given card. At the end of the processing it returns the response to the client which
   * describes the processing status in detail with proper response code and the response description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Card Accounts List Vector, List of Card Accounts associated with supplied card in case of success else null
   * <br>5. Balance String, The current balance of the supplied card in case of success else null
   * <br>6. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj getCardHolderAccounts(ServicesRequestObj
      requestObj) throws Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //////////////// Following Lines Were Added By Imtiaz /////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTypeOfTrans("A4");
      //////////////////////////////////////////////////////////////////////////////

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "A4");
//      if (serviceId != null)
//        Constants.GET_CH_ACCOUNTS_SERVICE = serviceId;
      ////////////// Above Commented Lines were replaced with threse Lines By Imtiaz //////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "A4", "00", cardPrgId);
      if (serviceId != null && serviceId.trim().length() > 0 &&
          !serviceId.equals(""))
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.GET_CH_ACCOUNTS_SERVICE);
        ////////////////////////////////////////////////////////////////////////////////////////////
      respObj = CardsServiceHandler.getInstance(con).getCardHolderAccounts(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getCardHolderAccounts

  /**
   * This method is used to get the mini statement of the supplied card. It firsts sets the type of the
   * transaction to successful only and find the service id for the given request and then uses the
   * CardServiceHandler class to find the account associated with the given card. At the end of the
   * processing it returns the response to the client which describes the processing status in detail with
   * proper response code and the response description.

   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. No of Trans String, No of transactions to include in mini statement --- Optional Default = 5
   * <br>7. Description String, Description / Comments --- Optional
   * <br>8. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>9. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. List of Transactions Vecotr, List of transactions in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj getCardMiniStatement(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //get the successful transactions only
      requestObj.setTypeOfTrans(Constants.SUCCESSFUL_TRANS_ONLY);

      //////////////// Following Lines Were Added By Imtiaz /////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTypeOfTrans("M1");
      //////////////////////////////////////////////////////////////////////////////

      if (requestObj.getNoOfTrans() == null ||
          requestObj.getNoOfTrans().trim().equals(""))
        requestObj.setNoOfTrans(String.valueOf(Constants.NO_OF_TRANS));
        //get the transactions with amount > 0
      requestObj.setChkAmount(true);

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "M1");
//      if (serviceId != null)
//        Constants.MINI_STMT_SERVICE = serviceId;

      ////////////////// Above Lines Were Replaced By Following Lines By Imtiaz //////////////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "M1", "00", cardPrgId);
      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.MINI_STMT_SERVICE);
        ///////////////////////////////////////////////////////////////////////////////////////////////

        ///Constants.TRANS_SERVICE = Constants.MINI_STMT_SERVICE; ////// Commented By Imtiaz
        //pass the call to the appropriate handler
      respObj = FinancialServiceHandler.getInstance(con).getCardTransactions(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getCardMiniStatement

  /**
   * This method is used to get the PIN of the supplied Card. It uses the CardServiceHandler class to
   * get the PIN of the given card no. At the end of the processing it returns response to the client
   * which describes the status of the processing in detail with appropiate response code and the
   * response description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. PIN String, Required PIN in case of success only else null
   * <br>5. Balance String, The current balance of the supplied card in case of success else null
   * <br>6. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
  public ServicesResponseObj getCardPIN(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      respObj = CardsServiceHandler.getInstance(con).getCardPin(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getCardPIN

  /**
   * This method is used to get the transaction history of the supplied card. It firsts checks the dates
   * between which the trnasaction record or history is required then it finds the service id for the
   * given request and then uses the FinancialServiceHandler class to find the account associated
   * with the given card. At the end of the processing it returns the response to the client which
   * describes the processing status in detail with proper response code and the response description.

   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Date From String, Date from which to pick the transactions --- Optional Default = Today
   * <br>7. Date To String, Date to which to pick the transactions --- Optional Default = Today
   * <br>8. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>9. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>10. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>11. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>12. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>13. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. List of Transactions Vecotr, List of transactions in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj getTransactionHistory(ServicesRequestObj
      requestObj) throws Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //////////////////// Following Lines Were Added By Imtiaz///////////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());

      requestObj.setTypeOfTrans(Constants.SUCCESSFUL_TRANS_ONLY);
      //requestObj.setTypeOfTrans("53");
      ///////////////////////////////////////////////////////////////////////////////////////////
      if (requestObj.getDateFrom() == null)
        requestObj.setDateFrom(DateUtil.getCurrentDate(Constants.
            WEB_DATE_FORMAT));

      if (requestObj.getDateTo() == null)
        requestObj.setDateTo(DateUtil.getCurrentDate(Constants.WEB_DATE_FORMAT));

        //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "53");
//      if (serviceId != null)
//        Constants.TRANS_HISTORY_SERVICE = serviceId;
        /////////////// Above Lines Were Replaced With Following Lines By Imtiaz /////////////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "53", "00", cardPrgId);
      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.TRANS_HISTORY_SERVICE);
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        //set the service to transaction history
        // Constants.TRANS_SERVICE = Constants.TRANS_HISTORY_SERVICE; // Line Commented By Imtiaz
        //pass the call to the appropriate handler
      respObj = FinancialServiceHandler.getInstance(con).getCardTransactions(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getTransactionHistory

//////////////////////////////////// Changed /////////////////////////////////////////////////////

  /**
   * This method returns the mini statement of the ACH transaction. It first sets the type of transction
   * to successful only transaction then it finds the card program id and service id for the
   *  given request and then uses the FinancialServiceHandler class to find the account associated
   * with the given card. At the end of the processing it returns the response to the client which
   * describes the processing status in detail with proper response code and the response description.

   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj getACHMiniStatement(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //get the successful transactions only
      requestObj.setTypeOfTrans(Constants.SUCCESSFUL_TRANS_ONLY);

      /////////////////// Following Lines Were Added By Imtiaz /////////////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTransCat("AM");
      /////////////////////////////////////////////////////////////////////////////////////////////

      if (requestObj.getNoOfTrans() == null ||
          requestObj.getNoOfTrans().trim().equals(""))
        requestObj.setNoOfTrans(String.valueOf(Constants.NO_OF_TRANS));
        //get the transactions with amount > 0
      requestObj.setChkAmount(true);

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "AM");
//      if (serviceId != null)
//        requestObj.setServiceId(serviceId);
//      else{
//        requestObj.setServiceId(Constants.ACH_MINI_STMT_SERVICE);
//      }
      ////////////// Above Commented Lines Were Replaced By Follwing Lines By Imtiaz ///////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "AM", "00", cardPrgId);
      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.ACH_MINI_STMT_SERVICE);
        //////////////////////////////////////////////////////////////////////////////////////////////////
        //pass the call to the appropriate handler
      respObj = FinancialServiceHandler.getInstance(con).getACHCardTransactions(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getCardMiniStatement

//////////////////////////////////// Changed /////////////////////////////////////////////////////

  /**
   * This method returns the ACH transaction history for the given card no. It first sets the type of
   * transction to all transaction then it finds the card program id and validates the date between which
   * the transaction history is required. It then finds the service id for the
   * given request and then uses the FinancialServiceHandler class to find the account associated
   * with the given card. At the end of the processing it returns the response to the client which
   * describes the processing status in detail with proper response code and the response description.

   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj getACHTransactionHistory(ServicesRequestObj
      requestObj) throws Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      /////////////////// Following Lines Were Added By Imtiaz /////////////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      requestObj.setTypeOfTrans(Constants.SUCCESSFUL_TRANS_ONLY);
      //requestObj.setTransCat("AH");
      /////////////////////////////////////////////////////////////////////////////////////////////

      if (requestObj.getDateFrom() == null)
        requestObj.setDateFrom(DateUtil.getCurrentDate(Constants.
            WEB_DATE_FORMAT));

      if (requestObj.getDateTo() == null)
        requestObj.setDateTo(DateUtil.getCurrentDate(Constants.WEB_DATE_FORMAT));

        //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "AH");
//      if (serviceId != null)
//        Constants.ACH_TRANS_HISTORY_SERVICE = serviceId;
//        //set the service to transaction history
//      Constants.TRANS_SERVICE = Constants.ACH_TRANS_HISTORY_SERVICE;

        ////////////// Above Commented Lines Were Replaced By Follwing Lines By Imtiaz ///////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "AH", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.ACH_TRANS_HISTORY_SERVICE);
        //////////////////////////////////////////////////////////////////////////////////////////////////

        //pass the call to the appropriate handler
      respObj = FinancialServiceHandler.getInstance(con).getACHCardTransactions(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getTransactionHistory

  //////////////////////////////////// Changed /////////////////////////////////////////////////////
  /**
   * This method returns the mini statement for the VAS transaction. It first sets the type of transction
   * to successful only transaction then it finds the card program id and service id for the
   * given request and then uses the FinancialServiceHandler class to find the account associated
   * with the given card. At the end of the processing it returns the response to the client which
   * describes the processing status in detail with proper response code and the response description.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj getVASMiniStatement(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //get the successful transactions only

      /////////////////// Following Lines Were Added By Imtiaz /////////////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTransCat("VM");
      /////////////////////////////////////////////////////////////////////////////////////////////

      requestObj.setTypeOfTrans(Constants.SUCCESSFUL_TRANS_ONLY);

      if (requestObj.getNoOfTrans() == null ||
          requestObj.getNoOfTrans().trim().equals(""))
        requestObj.setNoOfTrans(String.valueOf(Constants.NO_OF_TRANS));
        //get the transactions with amount > 0
      requestObj.setChkAmount(true);

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "VM");
//      if (serviceId != null)
//        requestObj.setServiceId(serviceId);
//      else
//        requestObj.setServiceId(Constants.VAS_MINI_STMT_SERVICE);

      ////////////// Above Commented Lines Were Replaced By Follwing Lines By Imtiaz ///////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "VM", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.VAS_MINI_STMT_SERVICE);
        //////////////////////////////////////////////////////////////////////////////////////////////////


        //pass the call to the appropriate handler
      respObj = FinancialServiceHandler.getInstance(con).getVASCardTransactions(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getCardMiniStatement

//////////////////////////////////// Changed /////////////////////////////////////////////////////

  /**
   * This method performs the pre-authorization of a transaction againt given card no. It first validates
   * the amount, if amount is negative then it pauses execution and returns response to client with response
   * code 13. If amount is non-negative it uses the VASServiceHandler class to the perform the
   * pre-authorization of the given transaction against the given card. At the end of the processing
   * it returns the response to the client which describes the processing status in detail with proper
   * response code and the response description.

   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */
  public ServicesResponseObj vasPreAuth(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    try {
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(requestObj.getDeviceType(), "VH");
//      if (serviceId != null)    requestObj.setServiceId(serviceId);
//      else {
//        //set the service to transaction history
//        requestObj.setServiceId(Constants.VAS_TRANS_HISTORY_SERVICE);
//      } //end else
//      //pass the call to the appropriate handler

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "VAS Pre Auth --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "VAS Pre Auth --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "VAS Pre Auth --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      requestObj.setServiceId(Constants.VAS_DEBIT);
      respObj = VasServiceHandler.getInstance(con).VASPreAuth(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getTransactionHistory

//////////////////////////////////// Changed /////////////////////////////////////////////////////

  /**
   * This method completes the pre-authorization of already made transaction againt given card no. It
   * first validates the amount, if amount is negative then it pauses execution and returns response to
   * client with response code 13. If amount is non-negative it uses the VASServiceHandler class to
   * perform the pre-authorization of the given transaction against the given card. At the end of
   * the processing it returns the response to the client which describes the processing status in
   * detail with proper response code and the response description.

   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */
  public ServicesResponseObj vasPreAuthCompletion(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    try {
//       String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "VH");
//      if (serviceId != null)
//        requestObj.setServiceId(serviceId);
//      else {
//        //set the service to transaction history
//        requestObj.setServiceId(Constants.VAS_TRANS_HISTORY_SERVICE);
//      } //end else

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "VAS Pre Auth Completion--- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "VAS Pre Auth Completion --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "VAS Pre Auth Completion --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      requestObj.setServiceId(Constants.VAS_DEBIT);

      //pass the call to the appropriate handler
      respObj = VasServiceHandler.getInstance(con).VASPreAuthCompletion(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getTransactionHistory

//////////////////////////////////// Changed /////////////////////////////////////////////////////

  /**
   * This method perpares VAS transaction history againt given card no. It first gets the card program id
   * against given card no. It then validates the dates between those the history of the VAS transaction
   * is required. It then gets the service id for the given request and  prepare the VAS pre-authorization
   * transaction list against the given card using VasServiceHandler class. At the end of the processing
   * it returns the response to the client which describes the processing status in detail with proper
   *  response code and the response description.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */
  public ServicesResponseObj getVASTransactionHistory(ServicesRequestObj
      requestObj) throws Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      ///////////////// Following Lines Were Added By Imtiaz ////////////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      requestObj.setTypeOfTrans(Constants.SUCCESSFUL_TRANS_ONLY);
      //requestObj.setTransCat("VH");
      ////////////////////////////////////////////////////////////////////////////////////////////

      if (requestObj.getDateFrom() == null)
        requestObj.setDateFrom(DateUtil.getCurrentDate(Constants.
            WEB_DATE_FORMAT));

      if (requestObj.getDateTo() == null)
        requestObj.setDateTo(DateUtil.getCurrentDate(Constants.WEB_DATE_FORMAT));

//        //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "VH");
//      if (serviceId != null)
//        requestObj.setServiceId(serviceId);
//      else {
//        //set the service to transaction history
//        requestObj.setServiceId(Constants.VAS_TRANS_HISTORY_SERVICE);
//      } //end else

        //////////////// Above Commented Lines Were Replaced With Following Lines ////////////////////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "VH", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.VAS_TRANS_HISTORY_SERVICE);
        //////////////////////////////////////////////////////////////////////////////////////////////////////

        //pass the call to the appropriate handler
      respObj = FinancialServiceHandler.getInstance(con).getVASCardTransactions(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getTransactionHistory

  /**
   * This method is used to get the Transaction Information of the supplied Card against supplied transaction ID
   * It first gets the service id for the given request and uses the FinancialServiceHanlder to fetch the
   * detail of the given transaction. At the end of the processing it returns response to the client which
   * describes the status of the processing in detail with appropiate response code and the response description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Transaction ID String, Transaction ID to look up the details --- Mandatory
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Additional Info String list, Transaction Additional Information in case of success else null
   * <br>5. Account Number String, Account Number in case of success else null
   * <br>6. Trans Type Id String, Transaction Type in case of success else null
   * <br>7. DeviceId String, Device ID of Transaction in case of success else null
   * <br>8. TransDate String, Transaction Date in case of success else null
   * <br>9. Business Date String, Busniess Date of the transaction in case of success else null
   * <br>10. AcceptNameAndLoc String, Acceptor Name and Location in case of success else null
   * <br>11. Amount String, Transaction Amount in case of success else null
   * <br>12. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj getTransactionInfo(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      ///////////////// Following Lines Were Added By Imtiaz ////////////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTransCat("T1");
      ////////////////////////////////////////////////////////////////////////////////////////////

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "T1");
//      if (serviceId != null)
//        Constants.TRANS_INFO_SERVICE = serviceId;
      //call the get card status

      //////////////// Above Commented Lines Were Replaced With Following Lines ////////////////////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "T1", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.TRANS_INFO_SERVICE);
        //////////////////////////////////////////////////////////////////////////////////////////////////////

      respObj = FinancialServiceHandler.getInstance(con).getTransactionInfo(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getTransactionInfo

  /**
   * This method is used to load the supplied amount from the supplied bank account information in the supplied card.
   * The method first validates the amount which is being loaded into the given card and then uses the
   * FinancialServiceHandler class to load the funds into the given card no.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Nick Name String, ACH Account Nick Name --- Mandatory
   * <br>7. Bank Name String, Name of the Bank --- Optional
   * <br>8. Bank Address String, Address of the Bank --- Optional
   * <br>9. Account No String, Bank Account No --- Mandatory
   * <br>10. Account Title String, Bank Account Title --- Optional
   * <br>11. Account Type String , Bank Account Type - 11 = Checking and 01 = Savings --- Mandatory
   * <br>12. Routing No String, Bank Routing No --- Mandatory
   * <br>13. Amount String, Amount to load --- Mandatory
   * <br>14. RetryOnFailure String, Whether to retry on failure of transaction or not --- Optional
   * <br>15. MaxTries String, Max No of tries to be done in case of failure and retryonfail = true --- Optional
   * <br>16. Comments String, Comments / Description --- Optional
   * <br>17. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>18. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>19. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>20. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>21. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>22. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. ACH Account ID String, Unique identification of the ACH Account created in case of success else null
   * <br>4. Balance String, Current Balance of card in case of success  else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
  public ServicesResponseObj loadFunds(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Load Funds --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Load Funds --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Load Funds--- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      }

      //set the ach type to 1
      requestObj.setAchType("1");
      //pass the call to the appropriate handler
      respObj = FinancialServiceHandler.getInstance(con).transferFunds(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end loadFunds

  /**
   * This method makes the pre-authorized transaction. It first validates the amount
   * which is being authorized, then gets the card program id and the service id and uses the
   * FinancialServiceHandler class to pre-authorize the transaction. At the end of the processing
   * it returns the response to the client which describes the processing status in detail with
   * appropiate response code and the response description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Amount String, Amount to add in the balance of the supplied card -- Mandatory
   * <br>7. Description String, Description / Comments --- Optional
   * <br>8. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>9. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>10. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>11. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>12. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>13. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. PreAuthTransId String, Pre-Authorization Transaction ID in case of success else null
   * <br>6. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj preAuthorization(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Pre Auth --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Pre Auth --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Pre Auth --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      //get the service ID
      ///////////////////////////////// Following Lines Were Added By Imtiaz ////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTransCat("T2");
      ////////////////////////////////////////////////////////////////////////////////////////////////

//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "T2");
//      if (serviceId != null)
//        Constants.PRE_AUTHORIZATION = serviceId;
      // Above Commented Lines Were Replaced By Following Lines By Imtiaz ///////////////////////////
      String serviceId = null;
      if (requestObj.getDeviceType().equalsIgnoreCase(Constants.DEVICE_TYPE_CS)) {
          serviceId = serviceHome.huntForServiceId(requestObj.
                                                   getDeviceType(),
                                                   "01", "00", cardPrgId,
                                                   requestObj.getIsInternational());
      } else {
          serviceId = serviceHome.huntForServiceId(requestObj.
                                                   getDeviceType(),
                                                   "00", "00", cardPrgId,
                                                   requestObj.getIsInternational());
      }

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0) {
          requestObj.setServiceId(serviceId);
      } else {
          requestObj.setServiceId(Constants.PRE_AUTHORIZATION);
      }
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        //invert the amount
      requestObj.setAmount("-" + requestObj.getAmount());
      //call the Pre Auth
      respObj = FinancialServiceHandler.getInstance(con).preAuthorization(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end preAuthorization

  /**
   * This method is used to replace the supplied stolen card with new one. It uses the
   * CardServiceHandler class to replace the stolen card.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. NewCardNo String, New Card No to transfer the stolen card balance to it -- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>9. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>10. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>11. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>12. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. NewCardNo String, New Card No in case of success else null
   * <br>6. NewCardAccountNo String, New Card Account No in case of success else null
   * <br>7. NewCardBalance String, New Card Balance in case of success else null
   * <br>8. NewCardExipryDate String, New Card Expiry Date in case of success else null
   * <br>9. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj replaceStolenCard(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      respObj = CardsServiceHandler.getInstance(con).replaceStolenCard(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end replaceStolenCard

  /**
   * This method tells the client that the PIN mailer request is not supported for the card holder.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj requestPINMailer(ServicesRequestObj requestObj) throws
      Exception { //pass the call to the appropriate handler
    ServicesResponseObj respObj = new ServicesResponseObj();
    respObj.setRespCode("57");
    respObj.setRespDesc("Transaction Not Permitted to Card Holder");
//    respObj.setTransId("45464");
//    respObj.setFeeAmount("1.5");
//    respObj.setCardBalance("181.25");
    return respObj;
  }

  /**
   * This method resets the Card Acccess code of the supplied card. It first finds the
   * service id for the given request and uses the CardServiceHandler class to get the card access
   * code. At the end of the processing it returns response to the client which describes the
   * processing status in detail with appropiate response code and the response description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. NewAAC String, New AAC of reset against supplied card no in case of success else null
   * <br>5. Balance String, The current balance of the supplied card in case of success else null
   * <br>6. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj resetCardAccessCode(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      /////////////////////////// Follwoing Lines Wre Added By Imtiaz /////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTransCat("A3");
      /////////////////////////////////////////////////////////////////////////////////////////////

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "A3");
//      if (serviceId != null)
//        Constants.RESET_AAC = serviceId;

      ////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "A3", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.RESET_AAC);

        //////////////////////////////////////////////////////////////////////////////////////////////////
        //call the set card AAC
      respObj = CardsServiceHandler.getInstance(con).resetCardAccessCode(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end resetCardAccessCode

  /**
   * This method resets the PIN of the supplied card. It first finds the card program id
   * and the service id for the given request and then uses the CardServiceHandler class to reset
   * the PIN. At the end of the processing it returns response to the client which describes the
   * processing status in detail with appropiate response code and response description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. batchPIN String, Flag to indicate whether the pin will be changed through batch file or not, Y=Yes and N=No - Default= Y --- Mandatory
   * <br>9. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>10. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>11.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>12. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. NewPIN String, New PIN of reset against supplied card no in case of success else null
   * <br>5. Balance String, The current balance of the supplied card in case of success else null
   * <br>6. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj resetPIN(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      /////////////////////// Following Lines Were Added By Imtiaz ///////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTransCat("P4");
      //////////////////////////////////////////////////////////////////////////////////////////

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "P4");
//      if (serviceId != null)
//        Constants.RESET_PIN = serviceId;
      ////////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz ////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "P4", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.RESET_PIN);
        ///////////////////////////////////////////////////////////////////////////////////////////////

        //call the set card pin
      respObj = CardsServiceHandler.getInstance(con).resetPIN(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end resetPin

  /**
   * This method transfers the funds between different cards of same card holder. It first
   * validate the amount which is being transfered. If amount was invalid then it returns the response
   * with response code 13 to the client and stops execution. If amount was found valid then
   * it uses the CardServiceHandler class to transfer the funds between the cards.
   * At the end of the processing it retruns response to the client which describes the processing
   * status in detail with appropiate response code and the response description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- IF trans Id is provided then Optional Else Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. ToCardNo String, To Card No --- IF trans Id is provided then Optional Else Mandatory
   * <br>7. ToCardAccountNo String, To Card Account Number --- Optional
   * <br>8. Amount String, Amount to transfer --- IF trans Id is provided then Optional Else Mandatory
   * <br>9. TransferDate String, Transfer Date --- IF trans Id is provided then Optional Else Mandatory
   * <br>10. RetryOnFailure String, Whether to retry on failure or not --- Optional Default = N
   * <br>11. MaxTries String, Maximum reties to be done --- Optional Default = 0
   * <br>12. Comments String, Comments / Description --- Optional
   * <br>13. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>14. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>15. Trans Id String, Transfer Id of trans_reqs table. If this field is supplied all other transfer related fields will be ignored -- Optional
   * <br>16. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>17. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>18.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>19. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. FromCardBalance String, The current balance of the from card in case of success else null
   * <br>5. ToCardBalance String, The current balance of the to card in case of success else null
   * <br>6. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
  public ServicesResponseObj selfTransfer(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Self Transfer --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt <= 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Self Transfer --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Self Transfer--- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      }

      respObj = FinancialServiceHandler.getInstance(con).cardToCardTransfer(
          requestObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Response Received...Committing all the work done...");
      //commit all the work
      con.commit();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "After con.commit()...Returning Response...");
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  in self transfer-- > " +
                                      exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end selfTransfer

  /**
   * This method is used to set the Card Acccess code of the supplied card. It first finds the service
   * id for the given request and then uses the CardServiceHandler class to reset the card access code.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. NewAAC String, New Access Code to set --- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>9. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>10. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>11. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>12. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj setCardAccessCode(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //get the service ID
      ///////////////// Following Lines Were Added By Imtiaz ////////////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTransCat("A2");
      //////////////////////////////////////////////////////////////////////////////////////////
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "A2");
//      if (serviceId != null)
//        Constants.SET_AAC_SERVICE = serviceId;

      /////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz ///////////////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "A2", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.SET_AAC_SERVICE);
        /////////////////////////////////////////////////////////////////////////////////////////////////

        //call the set card AAC
      respObj = CardsServiceHandler.getInstance(con).setCardAccessCode(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end setCardAccessCode

  /**
   * This method is used to set the PIN of the supplied card. It first finds card program id and
   * the service id for the given request and then uses the CardServiceHandler class to set the PIN
   * of the given card.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.

   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. NewPIN String, New PIN to set --- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>9. batchPIN String, Flag to indicate whether the pin will be changed through batch file or not, Y=Yes and N=No - Default= Y --- Mandatory
   * <br>10. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>11. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>12.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>13. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj setCardPIN(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      ////////////////////////// Following Lines were Added By Imtiaz //////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTransCat("P3");
      //////////////////////////////////////////////////////////////////////////////////////////////

//    //get the service ID
//    String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "P3");
//      if (serviceId != null)
//        Constants.SET_PIN_SERVICE = serviceId;
      /////////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "P3", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.SET_PIN_SERVICE);
        /////////////////////////////////////////////////////////////////////////////////////////////////////
        //call the set card pin
      respObj = CardsServiceHandler.getInstance(con).setCardPin(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end setCardPin

  /**
   * This method is used to set the Card status of the supplied card. It first finds card program id and
   * the service id for the given request and then uses the CardServiceHandler class to set the status
   * of the given card.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. NewStatus String, New status to set --- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>9. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>10. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>11. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>12. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj setCardStatus(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      ////////////////////////// Following Lines were Added By Imtiaz //////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTransCat("S2");
      //////////////////////////////////////////////////////////////////////////////////////////////

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "S2");
//      if (serviceId != null)
//        Constants.SET_CARD_STATUS_SERVICE = serviceId;

      /////////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "S2", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.SET_CARD_STATUS_SERVICE);
        /////////////////////////////////////////////////////////////////////////////////////////////////////

      respObj = CardsServiceHandler.getInstance(con).setCardStatus(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end setCardStatus

  /**
   * This method is used to Share the funds among the between cards.
   * It first validates the amount which is being shared, if it was found invalid then it returns
   * response with response code 13 to the client. It then uses the FinancialServiceHandler class to share
   * the funds between the cards.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.

   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- IF trans Id is provided then Optional Else Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. ToCardNo String, To Card No --- IF trans Id is provided then Optional Else Mandatory
   * <br>7. ToCardAccountNo String, To Card Account Number --- Optional
   * <br>8. Amount String, Amount to transfer --- IF trans Id is provided then Optional Else Mandatory
   * <br>9. TransferDate String, Transfer Date --- IF trans Id is provided then Optional Else Mandatory
   * <br>10. RetryOnFailure String, Whether to retry on failure or not --- Optional Default = N
   * <br>11. MaxTries String, Maximum reties to be done --- Optional Default = 0
   * <br>12. Comments String, Comments / Description --- Optional
   * <br>13. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>14. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>15. Trans Id String, Transfer Id of trans_reqs table. If this field is supplied all other transfer related fields will be ignored -- Optional
   * <br>16. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>17. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>18. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>19. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. FromCardBalance String, The current balance of the from card in case of success else null
   * <br>5. ToCardBalance String, The current balance of the to card in case of success else null
   * <br>6. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
  public ServicesResponseObj shareFunds(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Share Funds --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt <= 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Share Funds --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Share Funds--- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      }

      respObj = FinancialServiceHandler.getInstance(con).cardToCardTransfer(
          requestObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Response received..Transaction processed..");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Committing all the work..Commit -->" +
                                      con.getAutoCommit() +
                                      "<---- Is closed -->" + con.isClosed());
      //commit all the work
      con.commit();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "After con.commit()..Returning response back...");
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception in share funds -- > " +
                                      exp.getMessage());
      try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to rollback the work");

        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Exception in rollbacking..");
      } //end

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Returning the response of 96...");
      //set response
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      return respObj;
    } //end catch
    finally {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Setting the connection autcommit to -- > " +
                                      this.conAutoCommit);
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "After con.setAutoCommit()....");
    } //end finally
  } //end shareFunds

  /**
   * This method is used to update the card holder profile. It first finds card program id and
   * the service id for the given request and then uses the CardServiceHandler class to update
   * the card holder profile.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.

   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. FirstName String, First Name to update --- Optional
   * <br>7. MiddleName String, Middle Name to update --- Optional
   * <br>8. LastName String, Last Name to update --- Optional
   * <br>9. DOB String, DOB to update --- Optional
   * <br>10. Address String, Address to update --- Optional
   * <br>11. City String, City to update --- Optional
   * <br>12. StateCode String, State Code to update --- Optional
   * <br>13. Country String, Country to update --- Optional
   * <br>14. HomePhone String, Home Phone No to update --- Optional
   * <br>15. WorkPhone String, Work Phone No to update --- Optional
   * <br>16. Email String, Email to update --- Optional
   * <br>17. Gender String, Gender to update --- Optional
   * <br>18. MotherMaindenName String, Mother Mainden Name to update --- Optional
   * <br>19. SSN String, SSN to update --- Optional
   * <br>20. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>21. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>22. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>23. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>24. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>25. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj updateCardHolderProfile(ServicesRequestObj
      requestObj) throws Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      ////////////////////////// Following Lines were Added By Imtiaz //////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      requestObj.setCardPrgId(cardPrgId);
      //requestObj.setTransCat("P1");
      //////////////////////////////////////////////////////////////////////////////////////////////

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "P1");
//      if (serviceId != null)
//        Constants.CH_PROFILE_UPD_SERVICE = serviceId;

      /////////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "P1", "00", cardPrgId);
      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0) requestObj.setServiceId(serviceId);
      else requestObj.setServiceId(Constants.CH_PROFILE_UPD_SERVICE);
      /////////////////////////////////////////////////////////////////////////////////////////////////////

      respObj = CardsServiceHandler.getInstance(con).updateCardHolderProfile(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end updateCardHolderProfile

  /**
   * This method is used to validate the supplied Card Acccess code of the supplied card.
   * It first finds card program id and the service id for the given request and then uses the CardServiceHandler class to validate the
   * supplied card access code.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.

   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>4. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>5. AAC String, New Access Code to validate --- Mandatory
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj validateCardAccessCode(ServicesRequestObj
      requestObj) throws Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      ////////////////////////// Following Lines were Added By Imtiaz //////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //requestObj.setTransCat("A8");
      //////////////////////////////////////////////////////////////////////////////////////////////
//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "A8");
//      if (serviceId != null)
//        Constants.VALIDATE_AAC_SERVICE = serviceId;

      /////////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "A8", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.VALIDATE_AAC_SERVICE);
        /////////////////////////////////////////////////////////////////////////////////////////////////////

      respObj = CardsServiceHandler.getInstance(con).validateAccessCode(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end validateCardAccessCode

  /**
   * This method is used to validate the supplied PIN of the supplied card. It first finds card
   *  program id and the service id for the given request and then uses the CardServiceHandler
   * class to validate the PIN of the given card.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.

   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>4. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>5. PIN String, PIN to validate --- Mandatory
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj validateCardPIN(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      ////////////////////////// Following Lines were Added By Imtiaz //////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
//      requestObj.setTransCat("P5");
      //////////////////////////////////////////////////////////////////////////////////////////////
//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "P5");
//      if (serviceId != null)
//        Constants.VALIDATE_PIN_SERVICE = serviceId;
      /////////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "P5", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.VALIDATE_PIN_SERVICE);
        /////////////////////////////////////////////////////////////////////////////////////////////////////

      respObj = CardsServiceHandler.getInstance(con).validatePIN(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end validateCardPin

  /**
   * This method is used to verify the given ACH account. It first finds card program id and
   * the service id for the given request and then uses the ACHServiceHandler class to verify
   * the ACH account.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.

   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. ACH Account No String, ACH Account No to verify --- Mandatory
   * <br>7. TestAmount1 String, Test Amount No 1 to verify --- Mandatory
   * <br>8. TestAmount2 String, Test Amount No 2 to verify --- Mandatory
   * <br>9. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>10. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>11. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>12. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>13.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>14. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

  //////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj verifyACHAccount(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      ////////////////////////// Following Lines were Added By Imtiaz //////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
//      requestObj.setTransCat("A0");
      //////////////////////////////////////////////////////////////////////////////////////////////

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "A0");
//      if (serviceId != null)
//        Constants.ACH_VERIFY_SERVICE = serviceId;

      /////////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "A0", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.ACH_VERIFY_SERVICE);
        /////////////////////////////////////////////////////////////////////////////////////////////////////

      respObj = ACHServiceHandler.getInstance(con).verifyAchAccount(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end verifyAchAccount

  /**
   * This method is used to withdraw the supplied amount from the supplied Card to the supplied Bank Account Information.
   * The method first validates the given amount which is being transfered and then uses the
   * FinancialServiceHandler class tot withdraw the funds from the given card account to bank account.
   * At the end of the processing it returns response to the client which describes the processing
   * status in detail with appropiate response code and the description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Nick Name String, ACH Account Nick Name --- Mandatory
   * <br>7. Bank Name String, Name of the Bank --- Optional
   * <br>8. Bank Address String, Address of the Bank --- Optional
   * <br>9. Account No String, Bank Account No --- Mandatory
   * <br>10. Account Title String, Bank Account Title --- Optional
   * <br>11. Account Type String , Bank Account Type - 11 = Checking and 01 = Savings --- Mandatory
   * <br>12. Routing No String, Bank Routing No --- Mandatory
   * <br>13. Amount String, Amount to withdraw --- Mandatory
   * <br>14. RetryOnFailure String, Whether to retry on failure of transaction or not --- Optional
   * <br>15. MaxTries String, Max No of tries to be done in case of failure and retryonfail = true --- Optional
   * <br>16. Comments String, Comments / Description --- Optional
   * <br>17. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>18. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>19. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>20. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>21. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>22. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. ACH Account ID String, Unique identification of the ACH Account created in case of success else null
   * <br>4. Balance String, Current Balance of card in case of success  else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
  public ServicesResponseObj withdrawFunds(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Withdraw Funds --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Withdraw Funds --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Withdraw Funds--- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      }

      //set the ach type to 1
      requestObj.setAchType("2");
      //pass the call to the appropriate handler
      respObj = FinancialServiceHandler.getInstance(con).transferFunds(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end withdrawFunds

  /**
   * This method is used to make the purchase transaction of the supplied amount. It first finds
   * card program id, validate the amount which is deducted for purchase transaction, find the
   * service id for the given request and uses the FinancialServiceHandler to make the purchase
   * transaction.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.

   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Amount String, Amount to make the purchase transaction -- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>9. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>10. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>11. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>12. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj makePurchaseTransaction(ServicesRequestObj
      requestObj) throws Exception {
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      ////////////////////////// Following Lines were Added By Imtiaz //////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
//      requestObj.setTransCat("00");
      //////////////////////////////////////////////////////////////////////////////////////////////

      //make the amount to negative
      double nBal = Double.parseDouble(requestObj.getAmount());

      if (nBal > 0)
        nBal *= -1;
        //set it against in request
      requestObj.setAmount(nBal + "");
      //hunt for the right service
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "00");
//      if (serviceId != null)
//        Constants.PURCHASE_SERVICE = serviceId;

      /////////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "00", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.PURCHASE_SERVICE);

        /////////////////////////////////////////////////////////////////////////////////////////////////////

        //make the service to appropriate
        //   Constants.ADD_FUNDS = Constants.PURCHASE_SERVICE;  //// Line Commented By Imtiaz
        //pass the call to the appropriate handler
      respObj = FinancialServiceHandler.getInstance(con).addFunds(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end makePurchaseTransaction

  /**
   * This method is used to reverse the supplied transaction id. It first validates the amount of
   * the transaction if it is negative then it pauses the execution and returns reponse to the
   * user with response code 13.
   * If the amount was valid amount then this method finds the card program id and the service id for
   * the given request. It then calls the FinancialServiceHandler class to perform the reversal of the
   * given transaction.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. TransactionID String, Transaction ID to reverse the transaction -- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>9. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>10. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>11. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>12. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the specified operation in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj performReversal(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);
    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Pre Auth --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Pre Auth --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Pre Auth --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      ////////////////////////// Following Lines were Added By Imtiaz //////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      // requestObj.setTransCat(null);
      //////////////////////////////////////////////////////////////////////////////////////////////
//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), null);
//      if (serviceId != null)
//        Constants.REVERSAL_SERVICE = serviceId;

      /////////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(), null,
          "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.REVERSAL_SERVICE);
        /////////////////////////////////////////////////////////////////////////////////////////////////////

      if (requestObj.getReverseTransFee() == null ||
          requestObj.getReverseTransFee().trim().equals(""))
        requestObj.setReverseTransFee("Y");
        //call the perform reversal
      respObj = FinancialServiceHandler.getInstance(con).performReversal(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end performReversal

  public ServicesResponseObj performPurchaseOrderReversal(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Pre Auth --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Pre Auth --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Pre Auth --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      requestObj.setServiceId(Constants.REVERSAL_SERVICE);
      /////////////////////////////////////////////////////////////////////////////////////////////////////

      if (requestObj.getReverseTransFee() == null ||
          requestObj.getReverseTransFee().trim().equals(""))
        requestObj.setReverseTransFee("Y");
        //call the perform reversal
      respObj = FinancialServiceHandler.getInstance(con).performPurchaseOrderReversal(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end performReversal

  public ServicesResponseObj makeBillPayment(ServicesRequestObj
          requestObj) throws
          Exception {
      //make the response object
      ServicesResponseObj respObj = new ServicesResponseObj();
      try {
          if (requestObj == null) {
              respObj.setRespCode("12");
              respObj.setRespDesc("Invalid client request received");
              return respObj;
          }
          if (requestObj.getBillPaymentAmount() != null &&
              requestObj.getBillPaymentAmount().trim().length() > 0) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Make Bill Payment --- Validating provided Amount --->" +
                                              requestObj.getBillPaymentAmount());
              try {
                  double amt = Double.parseDouble(requestObj.getBillPaymentAmount().trim());
                  if (amt < 0) {
                      CommonUtilities.getLogger().log(LogLevel.getLevel(
                              Constants.
                              LOG_CONFIG),
                              "Make Bill Payment --- Invalid Amount Received--->" +
                              requestObj.getBillPaymentAmount());
                      respObj.setRespCode("13");
                      respObj.setRespDesc("Invalid Amount");
                      return respObj;
                  }
              } catch (NumberFormatException ex1) {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                          "Make Bill Payment --- Invalid Amount Received--->" +
                          requestObj.getBillPaymentAmount());
                  respObj.setRespCode("13");
                  respObj.setRespDesc("Invalid Amount");
                  return respObj;
              }
          } //Validating Amount

          //call the perform reversal
          respObj = FinancialServiceHandler.getInstance(con).makeBillPayment(requestObj);
          //commit all the work
          con.commit();
          //return the result
          return respObj;
      } catch (Exception exp) { //end try
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_SEVERE),
                                          "Exception  -- > " + exp.getMessage());
          try {
              if (con != null)
                  con.rollback();
          } catch (Exception ex) {} //end try, end

          respObj.setRespCode("96");
          respObj.setRespDesc("System Error");
          respObj.setExcepMsg(exp.getMessage());
          respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
          respObj.setLogFilePath("More information can be found at--->" +
                                 Constants.EXACT_LOG_PATH);

          return respObj;
      } finally { //end catch
          //reset the connection's autocommit condition
          con.setAutoCommit(this.conAutoCommit);
      } //end finally
  }


  public ServicesResponseObj billPaymentReversal(ServicesRequestObj
          requestObj) throws
          Exception {
      //make the response object
      ServicesResponseObj respObj = new ServicesResponseObj();
      try {
          if (requestObj == null) {
              respObj.setRespCode("12");
              respObj.setRespDesc("Invalid client request received");
              return respObj;
          }

          if (requestObj.getAmount() != null &&
              requestObj.getAmount().trim().length() > 0) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Bill Payment Reversal --- Validating provided Amount --->" +
                                              requestObj.getAmount());
              try {
                  double amt = Double.parseDouble(requestObj.getAmount().trim());
                  if (amt < 0) {
                      CommonUtilities.getLogger().log(LogLevel.getLevel(
                              Constants.
                              LOG_CONFIG),
                              "Bill Payment Reversal --- Invalid Amount Received--->" +
                              requestObj.getAmount());
                      respObj.setRespCode("13");
                      respObj.setRespDesc("Invalid Amount");
                      return respObj;
                  }
              } catch (NumberFormatException ex1) {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                          "Bill Payment Reversal --- Invalid Amount Received--->" +
                          requestObj.getAmount());
                  respObj.setRespCode("13");
                  respObj.setRespDesc("Invalid Amount");
                  return respObj;
              }
          } //Validating Amount

          if (requestObj.getReverseTransFee() == null ||
              requestObj.getReverseTransFee().trim().equals("")){
              requestObj.setReverseTransFee("Y");
          }

          if(requestObj.getServiceId() == null || requestObj.getServiceId().trim().length() == 0){
              requestObj.setServiceId(Constants.BILL_PAY_REVERSAL_SERVICE);
          }

          //call the perform reversal
          respObj = FinancialServiceHandler.getInstance(con).
                    performBillPaymentReversal(requestObj);
          //commit all the work
          con.commit();
          //return the result
          return respObj;
      } catch (Exception exp) { //end try
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_SEVERE),
                                          "Exception  -- > " + exp.getMessage());
          try {
              if (con != null)
                  con.rollback();
          } catch (Exception ex) {} //end try, end

          respObj.setRespCode("96");
          respObj.setRespDesc("System Error");
          respObj.setExcepMsg(exp.getMessage());
          respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
          respObj.setLogFilePath("More information can be found at--->" +
                                 Constants.EXACT_LOG_PATH);

          return respObj;
      } finally { //end catch
          //reset the connection's autocommit condition
          con.setAutoCommit(this.conAutoCommit);
      } //end finally
  }



  /**
   * This method is used to apply the acquirer charge fee with the supplied Card No.
   * It first finds  the service id for the given request and then uses the FinancialServiceHandler
   * class to set apply the acquirer charge fee against the the given card.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.

   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Amount String, Amount charge the fee --- Mandatory
   * <br>7. Description String, Description for the Fee --- Mandatory
   * <br>6. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>7. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>8. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>9. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>10.Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>11. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the get Card Status Transaction of MCP System in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj acquirerChargeFee(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {


      if (requestObj.getAcquirerFeeAmt() != null &&
          requestObj.getAcquirerFeeAmt().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "acquirerChargeFee --- Validating provided Amount --->" +
                                        requestObj.getAcquirerFeeAmt());
        try {
          double amt = Double.parseDouble(requestObj.getAcquirerFeeAmt().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Acquirer Charge Fee--- Invalid Amount Received--->" +
                                            requestObj.getAcquirerFeeAmt());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "acquirerChargeFee--- Invalid Amount Received--->" +
                                          requestObj.getAcquirerFeeAmt());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      }else{
        respObj.setRespCode("13");
        respObj.setRespDesc("Invalid Amount--->" + requestObj.getAcquirerFeeAmt());
        return respObj;
      }
      ////////////////////////// Following Lines were Added By Imtiaz //////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
//      requestObj.setTransCat("F1");
      //////////////////////////////////////////////////////////////////////////////////////////////

//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "F1");
//      if (serviceId != null)
//        Constants.ACQ_CHARGE_FEE = serviceId;
      /////////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "F1", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.ACQ_CHARGE_FEE);
        /////////////////////////////////////////////////////////////////////////////////////////////////////

        //call the Acquirer charge fee
      respObj = FinancialServiceHandler.getInstance(con).acquirerChargeFee(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end acquirerChargeFee

  /**
   * This method is used to reverse the Acquirer Charge Fee. It uses the FinancialServiceHandler class
   * to reverse the Acquirer Charge Fee.
   * At the end of the transaction processing it returns response to the client which describes the
   * processing status in detail with appropiate response code and the response description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. TransactionID String, Transaction ID to reverse the acquirer charge fee -- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>9. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>10. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>11. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>12. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the specified operation in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
  public ServicesResponseObj reverseAcquirerFee(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      respObj = FinancialServiceHandler.getInstance(con).reverseAcquirerFee(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end reverseAcquirerFee

  /**
   * This method is used to get the ACH Account Information. It first finds the card program id
   * and the the service id for the given request and then uses the ACHServiceHandler class to
   * get the ACH account information against the the given card.
   * AT the end of the transaction response is returned to the client which describes the processing
   * status in detail with appropiate response code and the description of the response.

   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. TransactionID String, Transaction ID to reverse the acquirer charge fee -- Mandatory
   * <br>7. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>8. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>9. Bank Account No String, Bank Account No
   * <br>10. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>11. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>12. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>13. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. Transaction Id String, ID of the specified operation in case of success else null
   * <br>4. Balance String, The current balance of the supplied card in case of success else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   * <br>6. ACH Account Status String, Status of ACH Account
   * <br>7. ACH Type String, For Load = 1, For Withdraw = 2
   * <br>8. Account Type String, Bank Account Type
   * <br>9. Account Title String, Bank Account Title
   * <br>10. Routing No String, Bank Routing No
   * <br>11. Bank Name String, Bank Name
   */

//////////////////////////////////// Changed /////////////////////////////////////////////////////
  public ServicesResponseObj getACHAccountInfo(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      ////////////////////////// Following Lines were Added By Imtiaz //////////////////////////////
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
//      requestObj.setTransCat("AD");
      //////////////////////////////////////////////////////////////////////////////////////////////
//      //get the service ID
//      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
//          requestObj.getDeviceType(), "AD");
//      if (serviceId != null)
//        Constants.ACH_ACCOUNT_INFO_SERVICE = serviceId;

      /////////////////////// Above Commented Lines Were Replaced By Following Lines By Imtiaz //////
      String serviceId = serviceHome.huntForServiceId(requestObj.getDeviceType(),
          "AD", "00", cardPrgId);

      if (serviceId != null && !serviceId.equals("") &&
          serviceId.trim().length() > 0)
        requestObj.setServiceId(serviceId);
      else
        requestObj.setServiceId(Constants.ACH_ACCOUNT_INFO_SERVICE);
        /////////////////////////////////////////////////////////////////////////////////////////////////////

      respObj = ACHServiceHandler.getInstance(con).getACHAccountInfo(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getACHAccountInfo

  /**
   * This method is used apply a specific fee on supplied card. It uses the FinancialServiceHandler
   *  class to apply the fee against to the given card no.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Amount String, The amount if service basis is %age only -- Optional
   * <br>3. Trans Id String, ISO Serial No -- Optional
   * <br>4. Service Id String, Service ID to implement -- Mandatory
   * <br>5. Description String, Description -- Optional
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   */
  public ServicesResponseObj applyServiceFee(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      respObj = FinancialServiceHandler.getInstance(con).applyServiceFee(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end applyServiceFee

  /**
   * This method is used to find the service fee of given service. It uses the CardServiceHome
   * class to calculate the service fee for the given service.
   * At the end of the processing it returns response to the client which describes the processing
   * status in detail with response code and the response description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Service Id String, Service ID to implement -- Mandatory
   */

  public ServicesResponseObj getServiceFee(ServicesRequestObj requestObj) throws
      Exception {
    //make response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      //pass on the request
      respObj = CardsServiceHome.getInstance(con).getServiceFee(requestObj.
          getCardNo(), "0.00", requestObj.getServiceId());
      //return the response
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getServiceFee

  /**
   * This method is used to find the Vas Account balance for the given card.
   * It uses the VasServiceHandler class to find the Vas balance against the given card.
   * At the end of the processing it returns response to the client with appropiate response
   * code and the description.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */
  public ServicesResponseObj vasBalanceInquiry(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      respObj = VasServiceHandler.getInstance(con).vasBalanceInquiry(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end vasBalanceInquiry


  public ServicesResponseObj getVASAccountsInfo(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      respObj = VasServiceHandler.getInstance(con).getVASAccountsInfo(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end vasBalanceInquiry


  /**
   * This method is used to credit the funds from the given Vas account. First this method validates
   * the amount which is being credited. If amount was found invalid then it pauses the execution
   * and returns the response to the client with response code 13.
   * If amount is a valid amount then it uses the VasServiceHandler class to credit the funds
   * from the given Vas account. At the end of the processing it returns the response to the client
   * with appropiate response code and description.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj vasCreditFunds(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "VAS Credit Funds --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "VAS Credit Funds --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "VAS Credit Funds --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      respObj = VasServiceHandler.getInstance(con).vasCreditFunds(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end vasBalanceInquiry

  /**
   * This method is used to debit the funds from the given Vas account. First this method validates
   * the amount which is being credited. If amount was found invalid then it pauses the execution
   * and returns the response to the client with response code 13.
   * If amount is a valid amount then it uses the VasServiceHandler class to debit the funds
   * from the given Vas account. At the end of the processing it returns the response to the client
   * which contains the response code and description.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */
  public ServicesResponseObj vasDebitFunds(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "VAS Debit Funds --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "VAS Debit Funds --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "VAS Debit Funds --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      respObj = VasServiceHandler.getInstance(con).vasDebitFunds(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end vasBalanceInquiry

  /**
   * This method is used to tranfer the fund from one Vas account to another account.
   * First this method validates the amount which is being transferd. If amount was found invalid then it
   * pauses the execution and returns the response to the client with response code 13.
   * If amount is a valid amount then it uses the VasServiceHandler class to transfer the funds
   * from the given Vas account. At the end of the processing it returns the response to the client
   * with appropiate response code and description.

   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */
  public ServicesResponseObj vasTransferFundsFrom(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "VAS Transfer From --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "VAS Transfer From --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "VAS Transfer From --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      respObj = VasServiceHandler.getInstance(con).vasTransferFundsFrom(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end vasBalanceInquiry

  /**
   * This method is used to transfer the funds to the given Vas account. First this method validates
   * the amount which is being transfered. If amount was found invalid then it pauses the execution
   * and returnt the response to the client with response code 13.
   * If amount is a valid amount then it uses the VasServiceHandler class to credit the funds
   * from the given Vas account. At the end of the processing it returns the response to the client
   * with appropiate response code and description.

   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */
  public ServicesResponseObj vasTransferFundsTo(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "VAS Transfer To --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "VAS Transfer To --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "VAS Transfer To --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      respObj = VasServiceHandler.getInstance(con).vasTransferFundsTo(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end vasBalanceInquiry

  /**
   * This method is used to get the list of the linked cards with the given card.
   * It uses the VasServiceHandler class to get the linked card list. At the end of the processing
   * it returns response to the client which contains the detail of the processing including the
   * response code and description of the response.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj getLinkedCards(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      respObj = VasServiceHandler.getInstance(con).getLinkedCards(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end vasBalanceInquiry

  /**
   * This method is used to set the link between the cards.
   * It uses the VasServiceHandler class to set the link between the cards. At the end of the processing
   * it returns response to the client which contains the detail of the processing including the
   * response code and description of the response.

   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj setLinkedCards(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      respObj = VasServiceHandler.getInstance(con).setLinkedCards(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end vasBalanceInquiry

  /**
   * This method is used to remove the link between the cards.
   * It uses the VasServiceHandler class to remove the link between the cards. At the end of the
   * processing it returns response to the client which contains the detail of the processing including
   * the response code and description of the response.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */
  public ServicesResponseObj unlinkCards(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      respObj = VasServiceHandler.getInstance(con).unlinkCards(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end vasBalanceInquiry

  /**
   * This method is used to transfer the funds between the linked cards. First it validates the
   * amount which is being transfered, if the amount was found invalid then it pauses the execution
   * and returns the response to the client with response code 13.
   * It uses the VasServiceHandler class to transfer the funds between the linked cards.
   * At the end of the processing it returns response to the client which contains the detail of the
   * processing including the response code and description of the response.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj linkedCardTranfer(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Linked Card Transfer--- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Linked Card Transfer --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Linked Card Transfer --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      respObj = VasServiceHandler.getInstance(con).linkedCardTransfer(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end vasBalanceInquiry

  /**
   * This method is used make the purchase order. First it determines whether the application of the
   * service fee is allowed. If it is allowed then it sets the application of the service fee to "Y".
   * It then uses the PurchaseOrderTansaction to process the transaction. At the end of the processing
   * it returns response to the client which contains the detail of the processing including the
   * response code and description of the response.

   * @param requestObject TransactionRequestInfoObj
   * @return TransactionResponseInfoObj
   */
  public TransactionResponseInfoObj purchaseOrder(TransactionRequestInfoObj
                                                  requestObject) {
    PurchaseOrderTransaction newPO = null;
    NewCardPOFundsFromOldCard newPOFundsFromOld = null;
    PurchaseOrderTransactionWithExistingCard newPOWithExist = null;
    TransactionResponseInfoObj response = new TransactionResponseInfoObj();

    try {

      if (requestObject.getApplyFee() == null ||
          !requestObject.getApplyFee().trim().equals("N")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "<---Apply Fee Value is not 'N' so proceeding with 'Y' --->" + requestObject.getApplyFee());
        requestObject.setApplyFee("Y");
      }

      if(requestObject.getInitialAmount() != null){
        try {
          double d = Double.parseDouble(requestObject.getInitialAmount());
          if(d < 0){
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                            "Invalid Amount--->" + requestObject.getInitialAmount());
            response.setResposneCode("13");
            response.setResposneDescription("Invalid Amount--->" + requestObject.getInitialAmount());
            return response;
          }
        }
        catch (NumberFormatException ex2) {
          response.setResposneCode("13");
          response.setResposneDescription("Invalid Amount--->" + requestObject.getInitialAmount());
          return response;
        }
      }

      if(requestObject.isFundsFromOldCard()){
        if(requestObject.getExistingCard() != null && requestObject.getExistingCard().trim().length() > 0){
          newPOFundsFromOld = new NewCardPOFundsFromOldCard(con,requestObject);
          response = newPOFundsFromOld.processTransaction();
        }else{
          response.setResposneCode("12");
          response.setResposneDescription("New Card Purchase Order with Funds from old card option -- Missing Existing Card");
        }
      }else{
        if(requestObject.getExistingCard() != null && requestObject.getExistingCard().trim().length() > 0){
          newPOWithExist = new PurchaseOrderTransactionWithExistingCard(con,requestObject);
          response = newPOWithExist.processTransaction();
        }else{
          newPO = new PurchaseOrderTransaction(this.con, requestObject);
          response = newPO.processTransaction();
        }
      }
      con.commit();
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + ex.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex1) {} //end

      response.setResposneCode("96");
      response.setResposneDescription("System Error");
      response.setExcepMsg(ex.getMessage());
      response.setStkTrace(CommonUtilities.getStackTrace(ex));
      response.setLogFilePath("More information can be found at--->" +
                              Constants.EXACT_LOG_PATH);

      return response;
    }
    finally {
      try {
        if (con != null)
          con.setAutoCommit(this.conAutoCommit);
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  }

  /**
   * This method is used to upgrade the card from the existing card holder profile. First it finds the
   * nature of the card upgrade and then upgrade the card according to the given card upgrade option.
   * Following are different type of card upgrade option.
   * 1. Card Upgrade With amount transfer.
   * 2. Card Upgrade with all cash out.
   * 3. Card Upgrade in simple way.
   * This method take the decision according to the given card upgrade nature and upgrade the card.
   * At the end of the processing it returns the response to the client with appropiate response code
   * and description of the response.
   * @param requestObject TransactionRequestInfoObj
   * @return TransactionResponseInfoObj
   */
  public TransactionResponseInfoObj cardUpgrade(TransactionRequestInfoObj
                                                requestObject) {
    CardUpgradeTransaction cuProcessor = null;
    CardUpgradeWithAllCashOut cuAllCashProcessor = null;
    CardUpgradeWithAmountTransferTransaction cuAmtProcessor = null;
    TransactionResponseInfoObj response = new TransactionResponseInfoObj();

    try {
      //By Default Apply Fee flag should be Y, if not set by client
      if (requestObject.getApplyFee() == null ||
          requestObject.getApplyFee().trim().length() == 0) {
        requestObject.setApplyFee("Y");
      }
      if (requestObject.getExistingCard() != null &&
          (requestObject.getTransferAmount() == null ||
           requestObject.getTransferAmount().trim().length() == 0) &&
          !requestObject.closeExistingCard()) {
        cuProcessor = new CardUpgradeTransaction(this.con, requestObject);
        response = cuProcessor.processTransaction();
      }
      else if (requestObject.getExistingCard() != null &&
               requestObject.getTransferAmount() != null &&
               !requestObject.closeExistingCard()) {
        cuAmtProcessor = new CardUpgradeWithAmountTransferTransaction(this.con,
            requestObject);
        response = cuAmtProcessor.processTransaction();
      }
      else if (requestObject.getExistingCard() != null &&
               requestObject.closeExistingCard()) {
        cuAllCashProcessor = new CardUpgradeWithAllCashOut(this.con,
            requestObject);

        response = cuAllCashProcessor.processTransaction();
      }
      con.commit();
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + ex.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex1) {} //end

      response.setResposneCode("96");
      response.setResposneDescription("System Error");
      response.setExcepMsg(ex.getMessage());
      response.setStkTrace(CommonUtilities.getStackTrace(ex));
      response.setLogFilePath("More information can be found at--->" +
                              Constants.EXACT_LOG_PATH);

      return response;
    }
    finally {
      try {
        if (con != null)
          con.setAutoCommit(this.conAutoCommit);
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  }

  /**
   * This method is used to re-issue stolen or lost cards. The method determine the re-issue type
   * and calls the correct class according the type of the re-issue. <br>
   * 1. Type 1 Reissue (Same card is re-issued with same card attributes) <br>
   * 2. Type 2 Reissue (Same card is re-issued but with different expiry date from old one<br>
   * 3. Type 3 Reissue (New card is re-issed with the same information)<br>
   * In case of re-issue type other then the above give three types, execution is stopped and
   * response is returned to the client with response code 13 which indicates that the re-issue
   * type is an invalid re-issue type.
   * @param requestObject TransactionRequestInfoObj
   * @return TransactionResponseInfoObj
   */

  public TransactionResponseInfoObj reissueCard(TransactionRequestInfoObj
                                                requestObject) {
    CardReissueType1Transaction crType1Processor = null;
    CardReissueType2Transaction crType2Processor = null;
    CardReissueType3Transaction crType3Processor = null;
    TransactionResponseInfoObj response = new TransactionResponseInfoObj();

    try {
      if (requestObject.getExistingCard() != null &&
          requestObject.getReissueType() != null &&
          requestObject.
          getReissueType().equalsIgnoreCase(Constants.
                                            REISSUE_SAME_CARD_SAME_INFO)) {
        //By Default Apply Fee flag should be N for this scenario
//        if (requestObject.getApplyFee() == null ||
//            requestObject.getApplyFee().trim().length() == 0) {
//          requestObject.setApplyFee("N");
//        }
        crType1Processor = new CardReissueType1Transaction(this.con,
            requestObject);
        response = crType1Processor.processTransaction();
      }
      else if (requestObject.getExistingCard() != null &&
               requestObject.getReissueType() != null &&
               requestObject.
               getReissueType().equalsIgnoreCase(Constants.
                                                 REISSUE_SAME_CARD_UPDATE_EXPIRY)) {
        if (requestObject.getApplyFee() == null ||
            requestObject.getApplyFee().trim().length() == 0) {
          requestObject.setApplyFee("Y");
        }
        crType2Processor = new CardReissueType2Transaction(this.con,
            requestObject);
        response = crType2Processor.processTransaction();
      }
      else if (requestObject.getExistingCard() != null &&
               requestObject.getReissueType() != null &&
               requestObject.
               getReissueType().equalsIgnoreCase(Constants.
                                                 REISSUE_NEW_CARD_SAME_INFO)) {
        if (requestObject.getApplyFee() == null ||
            requestObject.getApplyFee().trim().length() == 0) {
          requestObject.setApplyFee("Y");
        }
        crType3Processor = new CardReissueType3Transaction(this.con,
            requestObject);
        response = crType3Processor.processTransaction();
      }
      else {
        response.setResposneCode("06");
        response.setResposneDescription("Invalid Reissue Type Received");
      }
      con.commit();
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + ex.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex1) {} //end

      response.setResposneCode("96");
      response.setResposneDescription("System Error");
      response.setExcepMsg(ex.getMessage());
      response.setStkTrace(CommonUtilities.getStackTrace(ex));
      response.setLogFilePath("More information can be found at--->" +
                              Constants.EXACT_LOG_PATH);
      return response;
    }
    finally {
      try {
        if (con != null)
          con.setAutoCommit(this.conAutoCommit);
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  }

//  public ServicesResponseObj directDeposit(ServicesRequestObj requestObj) throws
//      Exception {
//    //make the response object
//    ServicesResponseObj respObj = new ServicesResponseObj();
//
//    try {
//      respObj = FinancialServiceHandler.getInstance(con).directDepositAdvice(
//          requestObj);
//      //commit all the work
//      con.commit();
//      //return the result
//      return respObj;
//    } //end try
//    catch (Exception exp) {
//      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
//                                      "Exception  -- > " + exp.getMessage());
//      try {
//        if (con != null)
//          con.rollback();
//      } //end try
//      catch (Exception ex) {} //end
//
//      respObj.setRespCode("96");
//      respObj.setRespDesc("System Error");
//      return respObj;
//    } //end catch
//    finally {
//      //reset the connection's autocommit condition
//      con.setAutoCommit(this.conAutoCommit);
//    } //end finally
//  } //end vasBalanceInquiry



  /**
   * This method is used to withdraw the supplied amount from the supplied Card to the supplied Bank Account Information.
   * The method first validates the amount which is being withdrawn, it then validates the account no. to
   * which the amount is loaded and then uses the FinancialServiceHandler class to withdraw the funds
   * from the given card no and load the amount in the given account no.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Nick Name String, ACH Account Nick Name --- Mandatory
   * <br>7. Bank Name String, Name of the Bank --- Optional
   * <br>8. Bank Address String, Address of the Bank --- Optional
   * <br>9. Account No String, Bank Account No --- Mandatory
   * <br>10. Account Title String, Bank Account Title --- Optional
   * <br>11. Account Type String , Bank Account Type - 11 = Checking and 01 = Savings --- Mandatory
   * <br>12. Routing No String, Bank Routing No --- Mandatory
   * <br>13. Amount String, Amount to withdraw --- Mandatory
   * <br>14. RetryOnFailure String, Whether to retry on failure of transaction or not --- Optional
   * <br>15. MaxTries String, Max No of tries to be done in case of failure and retryonfail = true --- Optional
   * <br>16. Comments String, Comments / Description --- Optional
   * <br>17. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>18. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>19. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>20. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>21. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>22. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. ACH Account ID String, Unique identification of the ACH Account created in case of success else null
   * <br>4. Balance String, Current Balance of card in case of success  else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
  public ServicesResponseObj withdrawFundsWithAccountNo(ServicesRequestObj
      requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    FinancialServiceHome serviceHome = FinancialServiceHome.getInstance(con);

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Withdraw Funds --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Withdraw Funds --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Withdraw Funds--- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      }

      //set the ach type to 1
      requestObj.setAchType("2");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Calling loadAccountInfo()method of FinancialServiceHome class");

      serviceHome.loadAccountInfo(requestObj); //loads the Account Information

      if (requestObj.getBankAcctNo() == null || requestObj.getBankAcctType() == null ||
          requestObj.getBankRoutingNo() == null) {
        respObj.setRespCode("06");
        respObj.setRespDesc("Mandatory Fields Missing for Transfering Funds from Card To Bank using ACH Account Number");
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Now Calling transferFunds()method of FinancialServiceHandler class");

      respObj = FinancialServiceHandler.getInstance(con).transferFunds(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end withdrawFunds

  /**
   * This method is used to load the supplied amount from the supplied bank account information in the supplied card.
   * The method first checks the amount which is being loaded into the card no. If amount is invalid then
   * execution is paused and response code is returned to the client with response code 13.
   * It then checks the account information from which the funds are loaded into the card. At last it
   * uses the FinancialServiceHandler class to load the funds into the given card no.
   * At the end of the processing a response is returned to the client which describes the processing
   * status in detail with appropiate response code and description.
   * @param requestObj ServicesRequestObj -- Request Information as following:
   * <br>1. Card No String, The number of card for which to get the status -- Mandatory
   * <br>2. Expiry Date String, Expiry Date of the supplied Card to verify --- Optional
   * <br>3. AAC String , Account Access Code of the supplied card to verify --- Optional
   * <br>4. PIN String, Personal Identification No of the supplied card to verify --- Optional
   * <br>5. Account No String, Account Number of the supplied card to verify --- Optional
   * <br>6. Nick Name String, ACH Account Nick Name --- Mandatory
   * <br>7. Bank Name String, Name of the Bank --- Optional
   * <br>8. Bank Address String, Address of the Bank --- Optional
   * <br>9. Account No String, Bank Account No --- Mandatory
   * <br>10. Account Title String, Bank Account Title --- Optional
   * <br>11. Account Type String , Bank Account Type - 11 = Checking and 01 = Savings --- Mandatory
   * <br>12. Routing No String, Bank Routing No --- Mandatory
   * <br>13. Amount String, Amount to load --- Mandatory
   * <br>14. RetryOnFailure String, Whether to retry on failure of transaction or not --- Optional
   * <br>15. MaxTries String, Max No of tries to be done in case of failure and retryonfail = true --- Optional
   * <br>16. Comments String, Comments / Description --- Optional
   * <br>17. Apply Fee String, Flag to indicate whether to apply the fee on supplied card for this service or not , Y=Yes and N=No - Default = Y --- Optional
   * <br>18. Device Type String, Device Type to log the transactions e.g. I = IVR, W = WEB, H = WEB SERVICES etc --- Mandatory
   * <br>19. Device ID String, Device Id from where the transaction is initiated --- Optional
   * <br>20. Card Acceptor Code String, Card Acceptor Code --- Opational
   * <br>21. Card Acceptor Name And Location String, Card Acceptor Name And Location --- Optional
   * <br>22. MCC String, Merchant Category Code -- Optional Default=0
   * @throws Exception
   * @return ServicesResponseObj -- Response Information as following:
   * <br>1. Response Code String, Response Code 00=Success else Error
   * <br>2. Response Description String, Response Description
   * <br>3. ACH Account ID String, Unique identification of the ACH Account created in case of success else null
   * <br>4. Balance String, Current Balance of card in case of success  else null
   * <br>5. Fee Amount String, Amount of the Fee applied if applyFee=Y else null
   */
  public ServicesResponseObj loadFundsWithAccountNo(ServicesRequestObj
      requestObj) throws Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    FinancialServiceHome serviceHome = FinancialServiceHome.getInstance(con);

    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Load Funds --- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Load Funds --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Load Funds--- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      }

      //set the ach type to 1
      requestObj.setAchType("1");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Calling loadAccountInfo()method of FinancialServiceHome classs");

      serviceHome.loadAccountInfo(requestObj);

      if (requestObj.getBankAcctNo() == null || requestObj.getBankAcctType() == null ||
          requestObj.getBankRoutingNo() == null) {
        respObj.setRespCode("06");
        respObj.setRespDesc("Mandatory Fields Missing for Transfering Funds from Bank To Card using ACH Account Number");
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Now Calling transferFunds()method of FinancialServiceHandler classs");
      //pass the call to the appropriate handler
      respObj = FinancialServiceHandler.getInstance(con).transferFunds(
          requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally

  } //end loadFunds

  /**
   * This method is used to perform the reversal of already pre-authorized transaction.
   * It first validates the amount whose reversal is being carried out if amount was found invalid then
   * execution is stopped and response is returned to the client with response code 13.
   * In case of successfull amount validation the method finds the service id for the given request
   * and uses the VasServiceHandler class to perfrom the reversal of the given transaction.
   * At the end of the processing response is returned to the client which describes the processing
   * status with appropiate response code and description.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */
  public ServicesResponseObj vasPreAuthReversal(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    try {

      if (requestObj.getAmount() != null &&
          requestObj.getAmount().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "VAS Pre Auth Completion--- Validating provided Amount --->" +
                                        requestObj.getAmount());
        try {
          double amt = Double.parseDouble(requestObj.getAmount().trim());
          if (amt < 0) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "VAS Pre Auth Completion --- Invalid Amount Received--->" +
                                            requestObj.getAmount());
            respObj.setRespCode("13");
            respObj.setRespDesc("Invalid Amount");
            return respObj;
          }
        }
        catch (NumberFormatException ex1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "VAS Pre Auth Completion --- Invalid Amount Received--->" +
                                          requestObj.getAmount());
          respObj.setRespCode("13");
          respObj.setRespDesc("Invalid Amount");
          return respObj;
        }
      } //Validating Amount

      requestObj.setServiceId(Constants.VAS_CREDIT); // set the service-id of the request
      respObj = VasServiceHandler.getInstance(con).VASPreAuthReversal(
          requestObj); // perform VAS Pre-Auth Reversal
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getTransactionHistory


  /**
   * This method perfroms the authentication of the acquirer. It uses the AcquirerAuthenticator class to
   * authenticate the acquirer.
   * @param reqstObj AuthenticatorRequestObj
   * @return AuthenticatorResponseObj
   */
  public AuthenticatorResponseObj authenticateAcquirer(AuthenticatorRequestObj
      reqstObj) {
    AuthenticatorResponseObj response = null;
    AcquirerAuthenticator authAcq = null;
    try {

      authAcq = new AcquirerAuthenticator(reqstObj, this.con);
      response = authAcq.authenticate();
      con.commit();
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + ex.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex1) {} //end

      response.setResponseCode("96");
      response.setResponseDesc("System Error");
//      response.setExcepMsg(exp.getMessage());
//      response.setStkTrace(CommonUtilities.getStackTrace(exp));
//      response.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

      return response;
    }
    finally {
      try {
        if (con != null)
          con.setAutoCommit(this.conAutoCommit);
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  }

  /**
   * This method is used to get the acquirer information. It uses the AcquirerAuthenticator class to find
   * the information about the acquirer.
   * @param reqstObj AuthenticatorRequestObj
   * @return AuthenticatorResponseObj
   */

  public AuthenticatorResponseObj getAcquirerInformation(
      AuthenticatorRequestObj reqstObj) {
    AuthenticatorResponseObj response = null;
    AcquirerAuthenticator authAcq = null;
    try {

      authAcq = new AcquirerAuthenticator(reqstObj, this.con);
      response = authAcq.getAcquirerInfo();
      con.commit();
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + ex.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex1) {} //end

      response.setResponseCode("96");
      response.setResponseDesc("System Error");
      return response;
    }
    finally {
      try {
        if (con != null)
          con.setAutoCommit(this.conAutoCommit);
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  }


  /**
   * This method is used to verify the card. It first find the card program and service id for the given
   * card and then uses the CardServiceHandler class to verfiy the card.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj verifyCard(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

    try {
      //get the service ID
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Card Program ID for Card--->" +
                                      requestObj.getCardNo());
      String cardPrgId = serviceHome.getCardProgramID(requestObj.getCardNo());
      //   String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(requestObj.getDeviceType(), "S1");

      String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(
          requestObj.getDeviceType(), "S1", "00", cardPrgId);

      if (serviceId != null)

        //Constants.GET_CARD_STATUS_SERVICE = serviceId;
        requestObj.setServiceId(serviceId); // above line replaced with this by imtiaz
      else requestObj.setServiceId(Constants.GET_CARD_STATUS_SERVICE); // added by imtiaz
      //call the get card status
      respObj = CardsServiceHandler.getInstance(con).verifyCard(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  } //end getCardStatus

  public ServicesResponseObj instantCardIssue(ServicesRequestObj requestObj) throws Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      //call the get card status
      respObj = CardsServiceHandler.getInstance(con).processInstantCardIssue(requestObj);
      //commit all the work
      con.commit();
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  public ServicesResponseObj logChargeBackCase(ServicesRequestObj requestObj) throws Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      //call the get card status
      respObj = FinancialServiceHandler.getInstance(con).logChargeBackCase(requestObj);
//      //commit all the work
      if(con != null && !con.getAutoCommit()){
        con.commit();
      }
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  public ServicesResponseObj acceptChargeBackCase(ServicesRequestObj requestObj) throws Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      //call the get card status
      requestObj.setChargeBackStatus(Constants.CHG_BACK_APPROVE);
      respObj = FinancialServiceHandler.getInstance(con).processChargeBackCase(requestObj);
//      //commit all the work
      if(con != null && !con.getAutoCommit()){
        con.commit();
      }
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  public ServicesResponseObj rejectChargeBackCase(ServicesRequestObj requestObj) throws
      Exception {
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      //call the get card status
      requestObj.setChargeBackStatus(Constants.CHG_BACK_REJECT);
      respObj = FinancialServiceHandler.getInstance(con).processChargeBackCase(
          requestObj);
//      //commit all the work
      if(con != null && !con.getAutoCommit()){
        con.commit();
      }
      //return the result
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  public static ServicesResponseObj decryptData(ServicesRequestObj requestInfo) throws Exception{
    ServicesResponseObj response = new ServicesResponseObj();
    HSMService serv = null;

    try {
      if(requestInfo.getEncryptedData() != null && requestInfo.getEncryptedData().trim().length() > 0){
        serv = new HSMService(Constants.HSM_WRPR_FILE_PATH);
        String decData = serv.doDecrypt(requestInfo.getEncryptedData().trim());
        response.setRespCode("00");
        response.setRespCode("OK");
        response.setDecryptedData(decData);
        return response;
      }else{
        response.setRespCode("12");
        response.setRespCode("No Data to decrypt");
        return response;
      }
    }catch (Exception ex) {
      response.setRespCode("96");
      response.setRespCode("Exception in decrypting data using HSM--->" + ex);
      return response;
    }
  }

  public static ServicesResponseObj encryptData(ServicesRequestObj requestInfo) throws Exception{
    ServicesResponseObj response = new ServicesResponseObj();
    HSMService serv = null;

    try {
      if(requestInfo.getDecryptedData() != null && requestInfo.getDecryptedData().trim().length() > 0){
        serv = new HSMService(Constants.HSM_WRPR_FILE_PATH);
        String encData = serv.doEncrypt(requestInfo.getDecryptedData().trim());
        response.setRespCode("00");
        response.setRespCode("OK");
        response.setEncryptedData(encData);
        return response;
      }else{
        response.setRespCode("12");
        response.setRespCode("No Data to encrypt");
        return response;
      }
    }catch (Exception ex) {
      response.setRespCode("96");
      response.setRespCode("Exception in encrypting data using HSM--->" + ex);
      return response;
    }
  }

  public ServicesResponseObj assignCard(ServicesRequestObj requestObj) throws Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for assigning card...");
      respObj = CardsServiceHandler.getInstance(con).assignCard(requestObj);
      con.commit();
      return respObj;
    }catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp);
      try {
        if (con != null)
          con.rollback();
      }catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception in rolling back -- > " + ex);
      }
      respObj.setRespCode("00");
      respObj.setRespDesc("OK");
      respObj.setIsCardAssigned(false);
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      return respObj;
    }finally {
      con.setAutoCommit(this.conAutoCommit);
    }
  }

  public ServicesResponseObj getCardHolderPayees(ServicesRequestObj
          requestObj) throws
          Exception {
      ServicesResponseObj respObj = new ServicesResponseObj();
      CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

      try {
          if (requestObj == null) {
              respObj.setRespCode("12");
              respObj.setRespDesc("Invalid client request received");
              return respObj;
          }
          String cardPrgId = serviceHome.getCardProgramID(requestObj.
                  getCardNo());
          String serviceId = serviceHome.huntForServiceId(requestObj.
                  getDeviceType(),
                  "GP", "00", cardPrgId);

          if (serviceId != null && serviceId.trim().length() > 0 &&
              !serviceId.equals(""))
              requestObj.setServiceId(serviceId);
          else
              requestObj.setServiceId(Constants.GET_CH_PAYEES_SERVICE);
          ////////////////////////////////////////////////////////////////////////////////////////////
          //call the get card AAC
          respObj = CardsServiceHandler.getInstance(con).
                    getCardHolderPayeesList(
                            requestObj);
          //commit all the work
          con.commit();
          //return the result
          return respObj;
      } catch (Exception exp) { //end try
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_SEVERE),
                                          "Exception  -- > " + exp.getMessage());
          try {
              if (con != null)
                  con.rollback();
          } catch (Exception ex) {} //end try, end

          respObj.setRespCode("96");
          respObj.setRespDesc("System Error");
          respObj.setExcepMsg(exp.getMessage());
          respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
          respObj.setLogFilePath("More information can be found at--->" +
                                 Constants.EXACT_LOG_PATH);

          return respObj;
      } finally { //end catch
          //reset the connection's autocommit condition
          con.setAutoCommit(this.conAutoCommit);
      } //end finally
  }

  public ServicesResponseObj getBillPaymentStatus(ServicesRequestObj
          requestObj) throws
          Exception {
      ServicesResponseObj respObj = new ServicesResponseObj();
      CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);

      try {
          if (requestObj == null) {
              respObj.setRespCode("12");
              respObj.setRespDesc("Invalid client request received");
              return respObj;
          }
          String cardPrgId = serviceHome.getCardProgramID(requestObj.
                  getCardNo());
          String serviceId = serviceHome.huntForServiceId(requestObj.
                  getDeviceType(),
                  "BS", "00", cardPrgId);

          if (serviceId != null && serviceId.trim().length() > 0 &&
              !serviceId.equals(""))
              requestObj.setServiceId(serviceId);
          else
              requestObj.setServiceId(Constants.GET_BP_STATUS_SERVICE);
          ////////////////////////////////////////////////////////////////////////////////////////////
          //call the get card AAC
          respObj = CardsServiceHandler.getInstance(con).getBillPaymentStatus(
                            requestObj);
          //commit all the work
          con.commit();
          //return the result
          return respObj;
      } catch (Exception exp) { //end try
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_SEVERE),
                                          "Exception  -- > " + exp.getMessage());
          try {
              if (con != null)
                  con.rollback();
          } catch (Exception ex) {} //end try, end

          respObj.setRespCode("96");
          respObj.setRespDesc("System Error");
          respObj.setExcepMsg(exp.getMessage());
          respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
          respObj.setLogFilePath("More information can be found at--->" +
                                 Constants.EXACT_LOG_PATH);

          return respObj;
      } finally { //end catch
          //reset the connection's autocommit condition
          con.setAutoCommit(this.conAutoCommit);
      } //end finally
  }

  public ServicesResponseObj billPaymentTransactionStatement(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    try {
        requestObj.setServiceId(Constants.BILL_PAY_TRANS_STMT_SERVICE);
        respObj = FinancialServiceHandler.getInstance(con).billPaymentTransactionStatement(
            requestObj);
        //commit all the work
        con.commit();
        //return the result
        return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  public ServicesResponseObj defineBillPaymentAlert(ServicesRequestObj requestObj)throws Exception{
      ServicesResponseObj respObj = new ServicesResponseObj();
    try {
        respObj = CardsServiceHandler.getInstance(con).defineBillPaymentAlert(
            requestObj);
        //commit all the work
        con.commit();
        //return the result
        return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception  -- > " + exp.getMessage());
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {} //end

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      return respObj;
    } //end catch
    finally {
      //reset the connection's autocommit condition
      con.setAutoCommit(this.conAutoCommit);
    } //end finally
  }

  public ServicesResponseObj loadEntitlement(ServicesRequestObj requestObj) throws
          Exception {
      ServicesResponseObj respObj = new ServicesResponseObj();
      try {
          respObj = CardsServiceHandler.getInstance(con).
                    processEntitlementGeneration(requestObj);
          //commit all the work
          con.commit();
          //return the result
          return respObj;
      } catch (Exception exp) { //end try
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_SEVERE),
                                          "Exception  -- > " + exp.getMessage());
          try {
              if (con != null)
                  con.rollback();
          } catch (Exception ex) {} //end try, end

          respObj.setRespCode("96");
          respObj.setRespDesc("System Error");
          respObj.setExcepMsg(exp.getMessage());
          respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
          respObj.setLogFilePath("More information can be found at--->" +
                                 Constants.EXACT_LOG_PATH);

          return respObj;
      } finally { //end catch
          //reset the connection's autocommit condition
          con.setAutoCommit(this.conAutoCommit);
      } //end finally
  }

  public ServicesResponseObj redeemEntitlement(ServicesRequestObj requestObj) throws
          Exception {
      ServicesResponseObj respObj = new ServicesResponseObj();
      try {
          respObj = CardsServiceHandler.getInstance(con).
                    processEntitlementRedemption(requestObj);
          //commit all the work
          con.commit();
          //return the result
          return respObj;
      } catch (Exception exp) { //end try
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_SEVERE),
                                          "Exception  -- > " + exp.getMessage());
          try {
              if (con != null)
                  con.rollback();
          } catch (Exception ex) {} //end try, end

          respObj.setRespCode("96");
          respObj.setRespDesc("System Error");
          respObj.setExcepMsg(exp.getMessage());
          respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
          respObj.setLogFilePath("More information can be found at--->" +
                                 Constants.EXACT_LOG_PATH);

          return respObj;
      } finally { //end catch
          //reset the connection's autocommit condition
          con.setAutoCommit(this.conAutoCommit);
      } //end finally
  }


} //end ServicesHandler
