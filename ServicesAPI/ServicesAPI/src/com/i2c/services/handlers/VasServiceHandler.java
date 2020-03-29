package com.i2c.services.handlers;

import java.sql.*;
import com.i2c.services.*;
import com.i2c.services.home.VasServiceHome;
import com.i2c.services.util.*;

/**
 * <p>Title: VasServiceHandler: This class provides value added services (VAS) </p>
 * <p>Description: This class provides value added services (VAS). For example
 * if we want to pre-authorize some amount from card holder account in advance and later
 * pre-authorization would be completed as the card holder require it.</p>
 * <p>Copyright: Copyright (c) 2005 Innvoative Pvt. Ltd.</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class VasServiceHandler {

  ////////////////////////////////private variables//////////////////////
  private Connection con = null;

  /**
   * Default Constructor. Simply used to create new instance of VasServiceHandler class.
   */
  public VasServiceHandler() {
  } //end constructor

  /**
   * Factory method which creates the VasServiceHandler object and return it. It takes the database
   * connection as input and set the member variable against it.
   * @param _con Connection
   * @return VasServiceHandler
   */

  public static VasServiceHandler getInstance(Connection _con) {
    VasServiceHandler handler = new VasServiceHandler();
    handler.con = _con;
    return handler;
  } //end getInstance()

  /**
   * The method is used to perform the Pre-Authorization against the given card no.
   * The method first gets the switch infromation. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then checks the VAS Acccount type, if it is not null or empty then service performs
   * the pre-authorization against the given card no and returns the response to the client.
   * @param requestObj ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj VASPreAuth(ServicesRequestObj requestObj) throws
      Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();
    VasServiceHome serviceHome = VasServiceHome.getInstance(con);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "VAS Pre-Auth Method -- First Checking Switch ID");
    try {
      //create the swithchInfo object
      SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
          getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS preAuth Method -- Switch ID found--->" +
                                      switchInfo.getSwitchId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS preAuth Method -- Checking switch activation");
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            serviceHome.maskCardNo(requestObj.getCardNo()) +
                            ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS preAuth Method -- Going to check Switch Batch Flag");
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
        //set the appropriate response codes
        respObj.setRespCode("40");
        respObj.setRespDesc("VAS Pre-Auth service is not supported at Switch(" +
                            switchInfo.getSwitchId() + ")");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        //return the response object
        return respObj;
      } //end if

      if (requestObj.getVasAccountType() != null &&
          requestObj.getVasAccountType().trim().length() > 0) {

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "VAS preAuth Method -- Now performing VAS Pre-Auth");
        respObj = serviceHome.doVASPreAuth(requestObj);
      }
      else {
        respObj.setRespCode("30");
        respObj.setRespDesc(
            "VAS Account Type Value Missing in the request message");
      }


    } //end try
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {}
      //set the response code to system error
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error-->" + trc);
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
      //log the transaction
      String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    } //end catch

    return respObj;
  }


  /**
   * The method is used to perform the Pre-Authorization Completion against the given card no.
   * The method first gets the switch infromation. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then checks the VAS Acccount type, if it is not null or empty then service performs
   * the pre-authorization completion against the given card no and return the response to the client.
   * In case of any error during the processing service returns the response to the cleint with response
   * code and response description.
   * @param requestObj ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj VASPreAuthCompletion(ServicesRequestObj requestObj) throws
      Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();
    VasServiceHome serviceHome = VasServiceHome.getInstance(con);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "VAS Pre-Auth Completion Method -- First Checking Switch ID");
    try {
      //create the swithchInfo object
      SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
          getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Pre Auth Completion Method [VASPreAuthCompletion] -- Switch ID found--->" +
                                      switchInfo.getSwitchId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Pre Auth Completion Method [VASPreAuthCompletion] -- Checking switch activation");
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            serviceHome.maskCardNo(requestObj.getCardNo()) +
                            ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Pre Auth Completion Method [VASPreAuthCompletion] -- Going to check Switch Batch Flag");
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
        //set the appropriate response codes
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "VAS Pre-Auth Completion service is not supported at Switch(" +
            switchInfo.getSwitchId() + ")");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        //return the response object
        return respObj;
      } //end if

      if (requestObj.getVasAccountType() != null &&
          requestObj.getVasAccountType().trim().length() > 0) {


        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "VAS Pre Auth Completion Method [VASPreAuthCompletion] -- Now performing VAS Pre-Auth Completion");
        respObj = serviceHome.doVASPreAuthCompletion(requestObj);
      }
      else {
        respObj.setRespCode("30");
        respObj.setRespDesc("VAS Account Type Value Missing in the request message");
      }


    } //end try
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {}
      //set the response code to system error
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error-->" + trc);
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
      //log the transaction
      String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    } //end catch

    return respObj;
  }

  /**
   * This method returns the balance of the given VAS account.
   * The method first gets the switch infromation. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then validates the card information if it is found falsy then exuection is stopped
   * and response is returned to the client.
   * The service then checks the VAS Acccount type, if it is not null or empty then service gets
   * the VAS balance against the given card no and returns the response to the client. In case
   * of any error during the processing service returns the response with 96 repsonde code and
   * appropiate response description.
   * @param requestObj ServicesRequestObj -- Request Information
   * @return ServicesResponseObj -- Response Information
   */

  public ServicesResponseObj vasBalanceInquiry(ServicesRequestObj requestObj) throws Exception{

    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the reference of Cards Service Home
    VasServiceHome serviceHome = VasServiceHome.getInstance(con);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "VAS BAlance Inquiry Function -- Going to check Switch ID");

    try {
      //get the switch info
      SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
          getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS BAlance Inquiry Function -- Switch ID found--->" +
                                      switchInfo.getSwitchId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS BAlance Inquiry Function -- Checking switch active");
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            serviceHome.maskCardNo(requestObj.getCardNo()) +
                            ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.VAS_BALANCE_INQUIRY,
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS BAlance Inquiry Function -- Going to check Switch Batch Flag");
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
        //set the appropriate response codes
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "VAS Balance Inquiry Service is not supported at Switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.VAS_BALANCE_INQUIRY, requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        //return the response object
        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS BAlance Inquiry Function -- Validating card related attributes ");
      respObj = serviceHome.isCardInfoValid(requestObj.getCardNo(),
                                            requestObj.getAAC(),
                                            requestObj.getExpiryDate(),
                                            requestObj.getAccountNo(),
                                            requestObj.getPin(),
                                            Constants.VAS_BALANCE_INQUIRY,
                                            requestObj.getDeviceType(),
                                            requestObj.getDeviceId(),
                                            requestObj.getCardAcceptorId(),
                                            requestObj.getCardAcceptNameAndLoc(),
                                            requestObj.getMcc(),
                                            requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      if (requestObj.getVasAccountType() != null &&
          requestObj.getVasAccountType().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "VAS BAlance Inquiry Function -- Calling API to get VAS Balance");
        respObj = serviceHome.getVASBalance(requestObj);
      }
      else {
        respObj.setRespCode("30");
        respObj.setRespDesc(
            "VAS Account Type Value Missing in the request message");
      }

    } //end try
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {}
      //set the response code to system error
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error-->" + trc);
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
      //log the transaction
      String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    } //end catch

    return respObj;
  } //end vasBalanceInquiry


  /**
   * The method is used to credit the funds from the given card's VAS account.
   * The method first gets the switch infromation. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then validates the card information if it was found falsy then service
   * stops the execution and returns the response to the client.
   * The service then checks the VAS Acccount type, if it is not null or empty then service credits
   * the funds against the given card no and returns the response to the client. In case
   * of any error during the processing service returns 96 response code which indicate the system
   * malfunction.
   * @param requestObj ServicesRequestObj
   * @return ServicesResponseObj
   */


  public ServicesResponseObj vasCreditFunds(ServicesRequestObj requestObj) throws Exception{

    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the reference of Cards Service Home
    VasServiceHome serviceHome = VasServiceHome.getInstance(con);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "VAS Credit Funds Function -- Going to check Switch ID");

    try {
      //get the switch info
      SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
          getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Credit Funds  Function -- Switch ID found--->" +
                                      switchInfo.getSwitchId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Credit Funds Function -- Checking switch active");
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            serviceHome.maskCardNo(requestObj.getCardNo()) +
                            ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.VAS_CREDIT,
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Credit Funds  Function -- Going to check Switch Batch Flag");
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
        //set the appropriate response codes
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "VAS Credit Funds Service is not supported at Switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.VAS_CREDIT, requestObj.getDeviceType(), null, "0200", "0",
            respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        //return the response object
        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Credit Funds Function -- Validating card related attributes ");
      respObj = serviceHome.isCardInfoValid(requestObj.getCardNo(),
                                            requestObj.getAAC(),
                                            requestObj.getExpiryDate(),
                                            requestObj.getAccountNo(),
                                            requestObj.getPin(),
                                            Constants.VAS_CREDIT,
                                            requestObj.getDeviceType(),
                                            requestObj.getDeviceId(),
                                            requestObj.getCardAcceptorId(),
                                            requestObj.getCardAcceptNameAndLoc(),
                                            requestObj.getMcc(),
                                            requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      if (requestObj.getVasAccountType() != null &&
          requestObj.getVasAccountType().trim().length() > 0) {

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "VAS Credit Funds Function -- Calling creditVASAccount");
        respObj = serviceHome.creditVASAccount(requestObj);
      }
      else {
        respObj.setRespCode("30");
        respObj.setRespDesc(
            "VAS Account Type Value Missing in the request message");
      }

    } //end try
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {}
      //set the response code to system error
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error-->" + trc);
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
      //log the transaction
      String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);
      return respObj;
    } //end catch

    return respObj;
  } //end vasCredit



 /**
   * The method is used to debit the funds from the VAS account associated with given card no.
   * The method first gets the switch infromation. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then validates the card information if it was found falsy the the service
   * stop the exectuiton and returns the response to the client.
   * The service then checks the VAS Acccount type, if it is not null or empty then service debits the
   * funds against the given card no and returns the response to the client. In case
   * of any error during the processing service returns 96 response code which indicates the system
   * malfunction.

    * @param requestObj ServicesRequestObj
    * @return ServicesResponseObj
    */



  public ServicesResponseObj vasDebitFunds(ServicesRequestObj requestObj) throws Exception{

    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the reference of Cards Service Home
    VasServiceHome serviceHome = VasServiceHome.getInstance(con);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "VAS Debit Funds Function -- Going to check Switch ID");

    try {
      //get the switch info
      SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
          getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Debit Funds  Function -- Switch ID found--->" +
                                      switchInfo.getSwitchId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Debit Funds Function -- Checking switch active");
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            serviceHome.maskCardNo(requestObj.getCardNo()) +
                            ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.VAS_DEBIT,
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Debit Funds  Function -- Going to check Switch Batch Flag");
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
        //set the appropriate response codes
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "VAS Debit Funds Service is not supported at Switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.VAS_DEBIT, requestObj.getDeviceType(), null, "0200", "0",
            respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        //return the response object
        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Debit Funds Function -- Validating card related attributes ");
      respObj = serviceHome.isCardInfoValid(requestObj.getCardNo(),
                                            requestObj.getAAC(),
                                            requestObj.getExpiryDate(),
                                            requestObj.getAccountNo(),
                                            requestObj.getPin(),
                                            Constants.VAS_DEBIT,
                                            requestObj.getDeviceType(),
                                            requestObj.getDeviceId(),
                                            requestObj.getCardAcceptorId(),
                                            requestObj.getCardAcceptNameAndLoc(),
                                            requestObj.getMcc(),
                                            requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      if (requestObj.getVasAccountType() != null &&
          requestObj.getVasAccountType().trim().length() > 0) {

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "VAS Debit Funds Function -- Calling API debiting VAS Account");
        respObj = serviceHome.debitVASAccount(requestObj);
      }
      else {
        respObj.setRespCode("30");
        respObj.setRespDesc(
            "VAS Account Type Value Missing in the request message");
      }
    } //end try
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {}
      //set the response code to system error
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error-->" + trc);
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
      //log the transaction
      String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);
      return respObj;
    } //end catch

    return respObj;
  } //end debitVas


  /**
   * The method is used to transfer the funds from one card to another cards's VAS account.
   * The method first gets the switch infromation. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then validates the card information if it was found falsy the the service
   * stops the exectuiton and returns the response to the client.
   * The service then checks the VAS Acccount type, if it is not null or empty then service transfers
   * the funds from from-card-no to to-card-no and returns the response to the client. In case
   * of any error during the processing service returns 96 response code which indicates the system
   * malfunction.
   * @param requestObj ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj vasTransferFundsFrom(ServicesRequestObj requestObj) throws Exception{

    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the reference of Cards Service Home
    VasServiceHome serviceHome = VasServiceHome.getInstance(con);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "VAS Transfer Funds From -- Going to check Switch ID");

    try {
      //get the switch info
      SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
          getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Transfer Funds From -- Switch ID found--->" +
                                      switchInfo.getSwitchId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Transfer Funds From -- Checking switch active");
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            serviceHome.maskCardNo(requestObj.getCardNo()) +
                            ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.VAS_FUNDS_TRANSFER_FROM,
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS  Transfer Funds From -- Going to check Switch Batch Flag");
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
        //set the appropriate response codes
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "VAS Transfer Funds From Service is not supported at Switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.VAS_FUNDS_TRANSFER_FROM, requestObj.getDeviceType(), null,
            "0200", "0",
            respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        //return the response object
        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Transfer Funds From Function -- Validating card related attributes ");
      respObj = serviceHome.isCardInfoValid(requestObj.getCardNo(),
                                            requestObj.getAAC(),
                                            requestObj.getExpiryDate(),
                                            requestObj.getAccountNo(),
                                            requestObj.getPin(),
                                            Constants.VAS_FUNDS_TRANSFER_FROM,
                                            requestObj.getDeviceType(),
                                            requestObj.getDeviceId(),
                                            requestObj.getCardAcceptorId(),
                                            requestObj.getCardAcceptNameAndLoc(),
                                            requestObj.getMcc(),
                                            requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;
      if (requestObj.getVasAccountType() != null &&
          requestObj.getVasAccountType().trim().length() > 0) {

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "VAS Transfer Funds From Function -- Calling API for transer funds from VAS Account");
        respObj = serviceHome.vasTransferFrom(requestObj);
      }
      else {
        respObj.setRespCode("30");
        respObj.setRespDesc(
            "VAS Account Type Value Missing in the request message");
      }
    } //end try
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {}
      //set the response code to system error
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error-->" + trc);
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
      //log the transaction
      String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    } //end catch

    return respObj;
  } //end VAS_TRANSFER_FROM


  /**
   * The method is used to transfer fund to the given card no's associated VAS account.
   * The method first gets the switch infromation. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then validates the card information if it was found falsy then service
   * stop the exectuiton and returns the response to the client.
   * The service then checks the VAS Acccount type, if it is not null or empty then service transfers
   * the funds to the given card no and returns the response to the client. In case
   * of any error during the processing service return 96 response code which indicates the system
   * malfunction.
   * @param requestObj ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj vasTransferFundsTo(ServicesRequestObj requestObj)  throws Exception{

    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the reference of Cards Service Home
    VasServiceHome serviceHome = VasServiceHome.getInstance(con);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "VAS Transfer Funds To -- Going to check Switch ID");

    try {
      //get the switch info
      SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
          getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Transfer Funds To -- Switch ID found--->" +
                                      switchInfo.getSwitchId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Transfer Funds To -- Checking switch active");
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            serviceHome.maskCardNo(requestObj.getCardNo()) +
                            ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.VAS_FUNDS_TRANSFER_TO,
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Transfer Funds To -- Going to check Switch Batch Flag");
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
        //set the appropriate response codes
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "VAS Transfer Funds To Service is not supported at Switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.VAS_FUNDS_TRANSFER_TO, requestObj.getDeviceType(), null,
            "0200", "0",
            respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        //return the response object
        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS Transfer Funds To Function -- Validating card related attributes ");
      respObj = serviceHome.isCardInfoValid(requestObj.getCardNo(),
                                            requestObj.getAAC(),
                                            requestObj.getExpiryDate(),
                                            requestObj.getAccountNo(),
                                            requestObj.getPin(),
                                            Constants.VAS_FUNDS_TRANSFER_TO,
                                            requestObj.getDeviceType(),
                                            requestObj.getDeviceId(),
                                            requestObj.getCardAcceptorId(),
                                            requestObj.getCardAcceptNameAndLoc(),
                                            requestObj.getMcc(),
                                            requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      if (requestObj.getVasAccountType() != null &&
          requestObj.getVasAccountType().trim().length() > 0) {

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "VAS Transfer Funds To Function -- Calling API for transer funds to VAS Account");
        respObj = serviceHome.vasTransferTo(requestObj);
      }
      else {
        respObj.setRespCode("30");
        respObj.setRespDesc(
            "VAS Account Type Value Missing in the request message");
      }
    } //end try
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {}
      //set the response code to system error
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error-->" + trc);
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
      //log the transaction
      String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);
      return respObj;
    } //end catch

    return respObj;
  } //end VAS_TRANSFER_TO


 /**
   * The method is used to get the list of the card which are linked with the given card no.
   * The method first gets the switch information. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then validates the card information if it was found falsy then  service  stops the
   * exectuiton and returns the response to the client.
   * The service then checks the VAS Acccount type, if it is not null or empty then service gets the
   * linked card list against the given card no and returns the response to the client. In case
   * of any error during the processing service returns 96 response code which indicates the system
   * malfunction.
   * @param requestObj ServicesRequestObj
   * @return ServicesResponseObj
  */

 public ServicesResponseObj getLinkedCards(ServicesRequestObj requestObj)  throws Exception {

    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the reference of Cards Service Home
    VasServiceHome serviceHome = VasServiceHome.getInstance(con);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "Get Linked Cards -- Going to check Switch ID");

    try {
      //get the switch info
      SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
          getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Get Linked Cards  -- Switch ID found--->" +
                                      switchInfo.getSwitchId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Get Linked Cards  -- Checking switch active");
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            serviceHome.maskCardNo(requestObj.getCardNo()) +
                            ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.GET_LINKED_CARDS,
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Get Linked Cards -- Going to check Switch Batch Flag");
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
        //set the appropriate response codes
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "Get Linked Cards Service is not supported at Switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.GET_LINKED_CARDS, requestObj.getDeviceType(), null,
            "0200", "0",
            respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        //return the response object
        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Get Linked Cards Function -- Validating card related attributes ");
      respObj = serviceHome.isCardInfoValid(requestObj.getCardNo(),
                                            requestObj.getAAC(),
                                            requestObj.getExpiryDate(),
                                            requestObj.getAccountNo(),
                                            requestObj.getPin(),
                                            Constants.GET_LINKED_CARDS,
                                            requestObj.getDeviceType(),
                                            requestObj.getDeviceId(),
                                            requestObj.getCardAcceptorId(),
                                            requestObj.getCardAcceptNameAndLoc(),
                                            requestObj.getMcc(),
                                            requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Get Linked Cards Service Function -- Calling API for transer funds to VAS Account");
      respObj = serviceHome.getLinkedCards(requestObj);

    } //end try
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);

      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {}
      //set the response code to system error
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error-->" + trc);
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
      //log the transaction
      String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    } //end catch

    return respObj;
  } //end Get Linked Card


  public ServicesResponseObj getVASAccountsInfo(ServicesRequestObj requestObj) throws
      Exception {

    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the reference of Cards Service Home
    VasServiceHome serviceHome = VasServiceHome.getInstance(con);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "Method for getting VAS Accounts Info -- Going to check Switch ID");
    try {
      //get the switch info
      SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
          getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting VAS Accounts Info -- Switch ID found--->" +
                                      switchInfo.getSwitchId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting VAS Accounts Info -- Checking switch active");
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            serviceHome.maskCardNo(requestObj.getCardNo()) +
                            ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.GET_VAS_ACCOUNTS,
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting VAS Accounts Info -- Going to check Switch Batch Flag");
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
        //set the appropriate response codes
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "Get VAS Accounts Service is not supported at Switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.GET_VAS_ACCOUNTS, requestObj.getDeviceType(), null,
            "0200", "0",
            respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        //return the response object
        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting VAS Accounts Info-- Validating card related attributes ");
      respObj = serviceHome.isCardInfoValid(requestObj.getCardNo(),
                                            requestObj.getAAC(),
                                            requestObj.getExpiryDate(),
                                            requestObj.getAccountNo(),
                                            requestObj.getPin(),
                                            Constants.GET_VAS_ACCOUNTS,
                                            requestObj.getDeviceType(),
                                            requestObj.getDeviceId(),
                                            requestObj.getCardAcceptorId(),
                                            requestObj.getCardAcceptNameAndLoc(),
                                            requestObj.getMcc(),
                                            requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting VAS Accounts Info -- Calling API for getting VAS Accounts");
      respObj = serviceHome.getVASAccounts(requestObj);

    } //end try
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage() +
                                      "Stack Trace -- > " + trc);

      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {}
      //set the response code to system error
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error-->" + trc);
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
          "Going to log the error transaction::Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //log the transaction
      String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
          requestObj.getServiceId(), requestObj.getDeviceType(), null, "0200",
          "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
          requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
          requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
          requestObj.getAccountNo(), requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),
          "Transaction logged with iso serial no -- > " + transIds[0] +
                         " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    } //end catch

    return respObj;
  } //end Get Linked Card



  /**
   * The method is used to set the linked cards against the given card no.
   * The method first gets the switch information. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then validates the card information if it was found falsy then the service
   * stops the exectuiton and returns the response to the client.
   * The service then checks the secondary card if it null then it pauses execution and returns
   * the response to the client. The service then sets the
   * link card(s) against the given card no and returns the response to the client. In case
   * of any error during the processing service returns 96 response code which indicates the system
   * malfunction.
   * @param requestObj ServicesRequestObj
   * @return ServicesResponseObj
   */


  public ServicesResponseObj setLinkedCards(ServicesRequestObj requestObj)  throws Exception {

     //make the response object
     ServicesResponseObj respObj = new ServicesResponseObj();
     //get the reference of Cards Service Home
     VasServiceHome serviceHome = VasServiceHome.getInstance(con);

     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                     "Set Linked Cards -- Going to check Switch ID");

     try {
       //get the switch info
       SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
           getCardNo());
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                       "Set Linked Cards  -- Switch ID found--->" +
                                       switchInfo.getSwitchId());
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                       "Set Linked Cards  -- Checking switch active");
       if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
         respObj.setRespCode("91");
         respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                             ") is not active for Card (" +
                             serviceHome.maskCardNo(requestObj.getCardNo()) +
                             ")");

         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                         "Going to log the error transaction::Code -- > " +
                                         respObj.getRespCode() +
                                         " && Desc -- > " + respObj.getRespDesc());
         //log the transaction
         String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
             Constants.SET_LINKED_CARDS,
             requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
             "0", respObj.getRespCode(), requestObj.getDeviceId(),
             requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
             requestObj.getMcc(), requestObj.getAccountNo(),
             requestObj.getAcquirerId());

         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                         "Transaction logged with iso serial no -- > " +
                                         transIds[0] +
                                         " && trace audit no -- > " + transIds[1]);
         //set the trans id in response
         respObj.setTransId(transIds[0]);
         return respObj;
       } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                       "Set Linked Cards -- Going to check Switch Batch Flag");
       if (switchInfo.getSwitchId() != null &&
           !switchInfo.getSwitchId().trim().equals("") &&
           switchInfo.isBatchTransAllowed()) {
         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
         //set the appropriate response codes
         respObj.setRespCode("40");
         respObj.setRespDesc(
             "Set Linked Cards Service is not supported at Switch(" +
             switchInfo.getSwitchId() + ")");

         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
             LOG_FINEST),
                                         "Going to log the error transaction::Code -- > " +
                                         respObj.getRespCode() +
                                         " && Desc -- > " +
                                         respObj.getRespDesc());
         //log the transaction
         String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
             Constants.SET_LINKED_CARDS, requestObj.getDeviceType(), null,
             "0200", "0",
             respObj.getRespDesc(), "0", respObj.getRespCode(),
             requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
             requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
             requestObj.getAccountNo(), requestObj.getAcquirerId());

         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
             LOG_FINEST),
                                         "Transaction logged with iso serial no -- > " +
                                         transIds[0] +
                                         " && trace audit no -- > " + transIds[1]);
         //set the trans id in response
         respObj.setTransId(transIds[0]);
         //return the response object
         return respObj;
       } //end if

       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                       "Set Linked Cards Function -- Validating card related attributes ");
       respObj = serviceHome.isCardInfoValid(requestObj.getCardNo(),
                                             requestObj.getAAC(),
                                             requestObj.getExpiryDate(),
                                             requestObj.getAccountNo(),
                                             requestObj.getPin(),
                                             Constants.SET_LINKED_CARDS,
                                             requestObj.getDeviceType(),
                                             requestObj.getDeviceId(),
                                             requestObj.getCardAcceptorId(),
                                             requestObj.getCardAcceptNameAndLoc(),
                                             requestObj.getMcc(),
                                             requestObj.getAcquirerId());
       if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
         return respObj;

       if(requestObj.getSecCardNo() == null || requestObj.getSecCardNo().trim().length() == 0){
         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Invalid Secondary card number received");
         //set the appropriate response codes
         respObj.setRespCode("14");
         respObj.setRespDesc("Invalid Secondary Card number received in request");
         return respObj;
       }

       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                       "Set Linked Cards Service Function -- Calling API for setting linked card");
       respObj = serviceHome.setLinkedCards(requestObj);

     } //end try
     catch (Exception exp) {
       String trc = CommonUtilities.getStackTrace(exp);
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                       "Exception -- > " + exp.getMessage() + "Stack Trace -- > " + trc);
       try {
         if (con != null)
           con.rollback();
       } //end try
       catch (Exception ex) {}
       //set the response code to system error
       respObj.setRespCode("96");
       respObj.setRespDesc("System Error-->" + trc);
       //respObj.setSysDesc("System Error, Check logs for more information...");
       respObj.setExcepMsg(exp.getMessage());
       respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
       respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
       //log the transaction
       String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
           LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
       //set the trans id in response
       respObj.setTransId(transIds[0]);

       return respObj;
     } //end catch

     return respObj;
   } //end Get Linked Card



   /**
   * The method is used to remove the linked cards against the given card no.
   * The method first gets the switch information. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then validates the card information if it was found falsy then the service
   * stops the exectuiton and returns the response to the client.
   * The service then checks the secondary card if it null then it pauses execution and returns
   * the response to the client. The service then removes the
   * link card(s) against the given card no and returns the response to the client. In case
   * of any error during the processing service returns 96 response code which indicates the system
   * malfunction.
   * @param requestObj ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj unlinkCards(ServicesRequestObj requestObj)  throws Exception {

        //make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        //get the reference of Cards Service Home
        VasServiceHome serviceHome = VasServiceHome.getInstance(con);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Un Link Cards -- Going to check Switch ID");

        try {
          //get the switch info
          SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
              getCardNo());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                          "Un Link Cards  -- Switch ID found--->" +
                                          switchInfo.getSwitchId());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                          "Un Link Cards  -- Checking switch active");
          if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
            respObj.setRespCode("91");
            respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                                ") is not active for Card (" +
                                serviceHome.maskCardNo(requestObj.getCardNo()) +
                                ")");

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                            "Going to log the error transaction::Code -- > " +
                                            respObj.getRespCode() +
                                            " && Desc -- > " + respObj.getRespDesc());
            //log the transaction
            String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
                Constants.UNLINK_CARDS,
                requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                "0", respObj.getRespCode(), requestObj.getDeviceId(),
                requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                requestObj.getMcc(), requestObj.getAccountNo(),
                requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                            "Transaction logged with iso serial no -- > " +
                                            transIds[0] +
                                            " && trace audit no -- > " + transIds[1]);
            //set the trans id in response
            respObj.setTransId(transIds[0]);
            return respObj;
          } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                          "Un Link -- Going to check Switch Batch Flag");
          if (switchInfo.getSwitchId() != null &&
              !switchInfo.getSwitchId().trim().equals("") &&
              switchInfo.isBatchTransAllowed()) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
            //set the appropriate response codes
            respObj.setRespCode("40");
            respObj.setRespDesc(
                "UnLink Cards Service is not supported at Switch(" +
                switchInfo.getSwitchId() + ")");

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "Going to log the error transaction::Code -- > " +
                                            respObj.getRespCode() +
                                            " && Desc -- > " +
                                            respObj.getRespDesc());
            //log the transaction
            String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
                Constants.UNLINK_CARDS, requestObj.getDeviceType(), null,
                "0200", "0",
                respObj.getRespDesc(), "0", respObj.getRespCode(),
                requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "Transaction logged with iso serial no -- > " +
                                            transIds[0] +
                                            " && trace audit no -- > " + transIds[1]);
            //set the trans id in response
            respObj.setTransId(transIds[0]);
            //return the response object
            return respObj;
          } //end if

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                          "Un Link Cards Function -- Validating card related attributes ");
          respObj = serviceHome.isCardInfoValid(requestObj.getCardNo(),
                                                requestObj.getAAC(),
                                                requestObj.getExpiryDate(),
                                                requestObj.getAccountNo(),
                                                requestObj.getPin(),
                                                Constants.UNLINK_CARDS,
                                                requestObj.getDeviceType(),
                                                requestObj.getDeviceId(),
                                                requestObj.getCardAcceptorId(),
                                                requestObj.getCardAcceptNameAndLoc(),
                                                requestObj.getMcc(),
                                                requestObj.getAcquirerId());
          if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
            return respObj;

          if(requestObj.getSecCardNo() == null || requestObj.getSecCardNo().trim().length() == 0){
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Invalid Secondary card number received");
            //set the appropriate response codes
            respObj.setRespCode("14");
            respObj.setRespDesc("Invalid Secondary Card number received in request");
            return respObj;
          }

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                          "Un Link Cards Service Function -- Calling API for transer funds to VAS Account");
          respObj = serviceHome.unLinkeCards(requestObj);

        } //end try
        catch (Exception exp) {
          String trc = CommonUtilities.getStackTrace(exp);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                          "Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
          try {
            if (con != null)
              con.rollback();
          } //end try
          catch (Exception ex) {}
          //set the response code to system error
          respObj.setRespCode("96");
          respObj.setRespDesc("System Error-->" + trc);
          //respObj.setSysDesc("System Error, Check logs for more information...");
          respObj.setExcepMsg(exp.getMessage());
          respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
          respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
          //log the transaction
          String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);
          return respObj;
        } //end catch

        return respObj;
      } //end Get Linked Card



  /**
   * The method is used to transfer the funds into linked card defined against the given card no.
   * The method first gets the switch information. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then validates the card information if it was found falsy then the service
   * stops the exectution and returns the response to the client. The service then transfers the funds
   * to the link card defined against the given card no and returns the response to the client. In case
   * of any error during the processing service returns 96 response code which indicates the system
   * malfunction.
   * @param requestObj ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj linkedCardTransfer(ServicesRequestObj requestObj)  throws Exception {

    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the reference of Cards Service Home
    VasServiceHome serviceHome = VasServiceHome.getInstance(con);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "Linked Card Tranfer -- Going to check Switch ID");

    try {
      //get the switch info
      SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
          getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Linked Card Tranfer  -- Switch ID found--->" +
                                      switchInfo.getSwitchId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Linked Card Tranfer  -- Checking switch active");
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            serviceHome.maskCardNo(requestObj.getCardNo()) +
                            ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.LINKD_CARD_TRANSFER,
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Linked Card Transfer -- Going to check Switch Batch Flag");
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
        //set the appropriate response codes
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "Linked Card Transfer Service is not supported at Switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            Constants.LINKD_CARD_TRANSFER, requestObj.getDeviceType(), null,
            "0200", "0",
            respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        //return the response object
        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Linked Card Transfer Function -- Validating card related attributes ");
      respObj = serviceHome.isCardInfoValid(requestObj.getCardNo(),
                                            requestObj.getAAC(),
                                            requestObj.getExpiryDate(),
                                            requestObj.getAccountNo(),
                                            requestObj.getPin(),
                                            Constants.LINKD_CARD_TRANSFER,
                                            requestObj.getDeviceType(),
                                            requestObj.getDeviceId(),
                                            requestObj.getCardAcceptorId(),
                                            requestObj.getCardAcceptNameAndLoc(),
                                            requestObj.getMcc(),
                                            requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Get Linked Cards Transfer Function -- Calling API for transer funds to VAS Account");
      respObj = serviceHome.linkedCardTransfer(requestObj);

    } //end try
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {}
      //set the response code to system error
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error-->" + trc);
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
      //log the transaction
      String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    } //end catch

    return respObj;
  } //end Linked Card Transfer

  /**
   *
   * The method is used to perform the Pre-Authorization Reversal against the given card no and
   * the transaction no.
   * The method first gets the switch infromation. If the switch is inactive or in the batch
   * mode then execution is stopped, transaction is logged and response is returned to the client.
   * The service then checks the VAS Acccount type, if it is not null and empty then service performs
   * the pre-authorization reversal against the given card no and returns the response to the client.
   * @param requestObj ServicesRequestObj
   * @return ServicesResponseObj
   */


  public ServicesResponseObj VASPreAuthReversal(ServicesRequestObj requestObj)  throws Exception {
    ServicesResponseObj respObj = new ServicesResponseObj();
    VasServiceHome serviceHome = VasServiceHome.getInstance(con);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "VAS Pre-Auth Reversal Method -- First Checking Switch ID");
    try {
      //create the swithchInfo object
      SwitchInfoObj switchInfo = serviceHome.getCardSwitchInfo(requestObj.
          getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS preAuth Reversal Method -- Switch ID found--->" +
                                      switchInfo.getSwitchId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS preAuth Reversal Method -- Checking switch activation");
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            serviceHome.maskCardNo(requestObj.getCardNo()) +
                            ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "VAS preAuth Method -- Going to check Switch Batch Flag");
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
        //set the appropriate response codes
        respObj.setRespCode("40");
        respObj.setRespDesc("VAS Pre-Auth service is not supported at Switch(" +
                            switchInfo.getSwitchId() + ")");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),
            requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        //return the response object
        return respObj;
      } //end if
      if (requestObj.getVasAccountType() != null &&
          requestObj.getVasAccountType().trim().length() > 0) {

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "VAS preAuth Reversal Method -- Now performing VAS Pre-Auth Reversal");
        respObj = serviceHome.doVASPreAuthReversal(requestObj);
      }
      else {
        respObj.setRespCode("30");
        respObj.setRespDesc("VAS Account Type Value Missing in the request message");
      }


    } //end try
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if (con != null)
          con.rollback();
      } //end try
      catch (Exception ex) {}
      //set the response code to system error
      respObj.setRespCode("96");
      respObj.setRespDesc("System Error-->" + trc);
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setExcepMsg(exp.getMessage());
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
      //log the transaction
      String[] transIds = serviceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    } //end catch

    return respObj;
  }

}

