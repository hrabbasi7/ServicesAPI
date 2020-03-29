package com.i2c.services.handlers;

import com.i2c.services.util.*;
import com.i2c.services.*;
import com.i2c.services.home.*;
import com.i2c.transferapi.*;
import java.sql.*;

/**
 * <p>Title: ACHServiceHandler: This class provides ACH related services</p>
 * <p>Description: This class provides Services For ACH. For example if we wants to check the
 * status of the ACH account whether it is activated or not or create a new account.</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd.</p>
 * <p>Company: I2c Inc</p>
 * @author MCP BackEnd Team
 * @version 1.0
 */


public class ACHServiceHandler {

  private Connection con = null;

  /**
   * Default constructor for creating the instance of the ACHServiceHandler class
   */

  private ACHServiceHandler()
  {

  }//end ACHServiceHandler


  /**
   * This method takes the database connection as the input parameter, creates the instance of the
   * ACHServiceHandler class and returns it.
   * @param _con Connection
   * @return ACHServiceHandler
   */

  public static ACHServiceHandler getInstance(Connection _con)
  {
    ACHServiceHandler handler = new ACHServiceHandler();
    handler.con = _con;
    return handler;
  }//end getInstance


  /**
   * This method is used to create the ACH Account. It takes the "RequestObject" as parameter and creates
   * the ACH account accordingly. It first checks whether given switch is valid, active and the online,
   * then it validiates the card-no and finds the card-program against this card. After this it checks
   * the user information and expiry of the given card, if the card has been expired then it sets the
   * response accordinglgy and return the response to the calling method.
   * If the requested infromation is valid then it creates the ACH account accordingly. Before returning
   * the response it logs the transaction and sets the response according to the processing status.
   * @param requestObj ServicesRequestObj -- Request Information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */


  /////////////////////// Changed////////////////////////////////////////////////////////////////////
  // All Constants.CHANGE_ACCOUNT_FEE Replaced with requestObj.getServiceId()
  ////////////////////////////////////////////////////////////////////////////////////////////////

  public ServicesResponseObj createACHAccount(ServicesRequestObj requestObj) throws Exception
{
  //make the response object
  ServicesResponseObj respObj = new ServicesResponseObj();
  ACHServiceHome achServiceHome = ACHServiceHome.getInstance(con);
  //validate the card information
  respObj = CardsServiceHome.getInstance(con).isCardInfoValid(requestObj.getCardNo(),requestObj.getAAC(),requestObj.getExpiryDate(),requestObj.getAccountNo(),requestObj.getPin(), requestObj.getServiceId(), requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAcquirerId());
  if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
    return respObj;

  try{
    //get the card switch info
    SwitchInfoObj switchInfo = achServiceHome.getCardSwitchInfo(requestObj.getCardNo());

    if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
      respObj.setRespCode("91");
      respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() + ") is not active for Card (" + achServiceHome.maskCardNo(requestObj.getCardNo()) + ")");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
      //log the transaction
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);
      return respObj;
    } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

    if (switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed()) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans is Allowed.....");
      respObj.setRespCode("40");
      respObj.setRespDesc("Create ACH Account Service is not avaiable for switch(" + switchInfo.getSwitchId() + ")");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
      //log the transaction
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    } //end if(switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Validating the Card....");
    //validate the card
    respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.getCardNo(), requestObj.getServiceId(),requestObj.getApplyFee(),null,requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Reponse after validation :: Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
    //validate the response
    if (!respObj.getRespCode().trim().equals(Constants.SUCCESS_CODE))
      return respObj;


    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Validating Bank Routing Number --->" + requestObj.getBankRoutingNo());
    if(requestObj.getBankRoutingNo() != null && requestObj.getBankRoutingNo().trim().length() > 0){
      try {
        String routingNo = requestObj.getBankRoutingNo().substring(0,
            requestObj.getBankRoutingNo().length() - 1);
        String checkDegit = requestObj.getBankRoutingNo().substring(requestObj.getBankRoutingNo().length() - 1,requestObj.getBankRoutingNo().length());
        Integer retChkDegit = computeCheckDigit(routingNo);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Validating Bank Routing Number --- Check Degit --->" + checkDegit + "<--Computed Check degit-->" + retChkDegit);
        if(checkDegit != null && checkDegit.equals(retChkDegit.toString())){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Validating Bank Routing Number --- routing number valid");
        }else{
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Validating Bank Routing Number --- routing number invalid");
          respObj.setRespCode("RI");
          respObj.setRespDesc("Invalid Bank routing Number -- Check Digit mismatch");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
          //log the transaction
          String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);
          return respObj;
        }
      }catch (Exception ex1) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Exception in validating Bank Routing Number --->" + ex1);
        respObj.setRespCode("RI");
        respObj.setRespDesc("Invalid Bank routing Number -- Exception in validating routing number--->" + ex1);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      }
    }else{
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Validating Bank Routing Number --- routing number invalid");
      respObj.setRespCode("RI");
      respObj.setRespDesc("Invalid Bank routing Number received");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
      //log the transaction
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);
      return respObj;
    }

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Retreiving Card Program Id...");
    //get the card program id of card
    String cardPrgId = achServiceHome.getValue("select card_prg_id from cards where card_no='" + requestObj.getCardNo() + "'" );

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Card Program Id Got -- > " + cardPrgId);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Retreiving ACH ACCOUNT EXP DAYS LIMIT...");
    //get the value of expiray day service
    String expDayLimit = achServiceHome.getValue("select param_val from card_prog_params where card_prg_id='" + cardPrgId + "' and param_code='" + Constants.ACH_REG_EXPIRY_DAYS_SERVICE + "'");

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"EXP DAYS LIMIT FOUND -- > " + expDayLimit);

    if(expDayLimit == null)
      expDayLimit = Constants.DEFAULT_EXP_DAYS;

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"FINAL EXP DAY LIMIT -- > " + expDayLimit);
    //calculate the expiry date
    String expDate = DateUtil.getDateAfterNDays(expDayLimit);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Exp Date Calculated -- > " + expDate);
    //get the current date
    String createdAt = DateUtil.getCurrentDate() + " " + DateUtil.getCurrentTime();

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Calculating the Micro Payments....");
    //calculate the micropayments
    String[] amounts = CommonUtilities.calculateMicroPayments();

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Getting the user ID against card-->" + requestObj.getCardNo());
    //get the user id
    String userId = achServiceHome.getValue("select user_id from cards where card_no='" + requestObj.getCardNo() + "'");
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Got userId --> " + userId);

    if(userId == null || userId.trim().length() == 0){
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG), "Invalid User ID got--->" + userId);
      respObj.setRespCode("UN");
      respObj.setRespDesc("Invalid User ID got while creating ACH Account");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
      //log the transaction
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
    //set the trans id in response
    respObj.setTransId(transIds[0]);
    return respObj;
  }

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Creating the ACH Account in DB....");

    String acctQuery="insert into ach_accounts(user_id,ach_acct_nick,ach_type,created_at,account_no,expired_at,account_type,account_title,bank_name," +
        "bank_address,routing_no,verify_status,nfailed_tries,test_amount1,test_amount2, ivr_ach_act_nick ) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement stm=con.prepareStatement(acctQuery);
    stm.setString(1,userId);
    stm.setString(2,requestObj.getNickName());
    stm.setString(3,requestObj.getAchType());
    stm.setString(4,createdAt);
    stm.setString(5,requestObj.getBankAcctNo());
    stm.setString(6,expDate+" 00:00:00");
    stm.setString(7,requestObj.getBankAcctType());
    if(requestObj.getBankAcctTitle()!=null)
      stm.setString(8,requestObj.getBankAcctTitle());
    else
      stm.setString(8,null);
    if(requestObj.getBankName()!=null)
      stm.setString(9,requestObj.getBankName());
    else
      stm.setString(9,null);
    if(requestObj.getBankAddress()!=null)
      stm.setString(10,requestObj.getBankAddress());
    else
      stm.setString(10,null);
    stm.setString(11,requestObj.getBankRoutingNo());
    stm.setString(12,"L");
    stm.setInt(13,0);
    stm.setString(14,amounts[0]);
    stm.setString(15,amounts[1]);
    stm.setBytes(16,requestObj.getAudioFile());
    stm.executeUpdate();
    stm.close();

   //build the query to create the ACH Account
    /*String acctQuery = "insert into ach_accounts(user_id,ach_acct_nick,ach_type,created_at,account_no,expired_at,account_type,account_title,bank_name," +
        "bank_address,routing_no,verify_status,nfailed_tries,test_amount1,test_amount2, ivr_ach_act_nick ) values('" +
        userId + "','" + requestObj.getNickName() + "'," +
        "'" + requestObj.getAchType() + "','" + createdAt + "','" +
        requestObj.getBankAcctNo() + "','" + expDate + " 00:00:00" + "','" +
        requestObj.getBankAcctType() + "'," + (requestObj.getBankAcctTitle() != null ? "'" + requestObj.getBankAcctTitle() + "'" : null) +
        "," + (requestObj.getBankName() != null ? "'" + requestObj.getBankName() + "'" : null) + "," +
        (requestObj.getBankAddress() != null ? "'" + requestObj.getBankAddress() + "'" : null) +
        ",'" + requestObj.getBankRoutingNo() + "','L',0," + amounts[0] + "," + amounts[1] + ","+requestObj.getAudioFile()+")";
     */
    //execute the query

    //long acctSrNo = achServiceHome.insertValues(acctQuery);
    long acctSrNo=-1;
    String serialQry=Constants.SERIAL_QUERY+" ach_accounts";
    stm=con.prepareStatement(serialQry);
    ResultSet rs=stm.executeQuery();
    if(rs.next()) acctSrNo=rs.getLong(1);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ACH Account Information saved against account serial no -- > " + acctSrNo);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the transaction...");
    //log the transaction
    String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(), requestObj.getDeviceType(),Constants.ACH_ACCOUNT_MSG,null,requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Transaction logged with ISO Serial No -- > " + transIds[0] );

    if(requestObj.getApplyFee().trim().equalsIgnoreCase("Y"))
    {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Applying fee at OLTP...");
      respObj = achServiceHome.applyServiceFeeAtOltp(requestObj.getCardNo(),requestObj.getServiceId(),null,requestObj.getRetreivalRefNum(),transIds[1], requestObj.getDeviceType(), requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),
                                    requestObj.getAcquirerId(),
                                    requestObj.getAcqUsrId(),
                                    requestObj.getAcqData1(),
                                    requestObj.getAcqData2(),
                                    requestObj.getAcqData3()
);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Response after applying fee :: Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
      if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
      {
        //rollback all the work
        con.rollback();
        return respObj;
      }//end if
    }//end if
    else
    {
      //get the latest balance
      String balance = achServiceHome.getValue("select card_balance from card_funds where card_no='" + requestObj.getCardNo() + "'");
      //set the balance in response
      respObj.setCardBalance(balance);
    }//end else
    //set the trans id
    respObj.setTransId(transIds[0]);
    respObj.setRespCode(Constants.SUCCESS_CODE);
    respObj.setRespDesc(Constants.SUCCESS_MSG);
    respObj.setAchAccountNo(String.valueOf(acctSrNo));
    return respObj;
  }//end try
  catch(Exception exp)
  {
    String trc = CommonUtilities.getStackTrace(exp);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),"Exception -- > " + exp.getMessage() + "Stack Trace -- > " + trc);
    try {
      if(con != null)
        con.rollback();
    }//end try
    catch (Exception ex) {}
    //set the response code to system error
    respObj.setRespCode("96");
    respObj.setRespDesc("System Error-->" + trc);
    //respObj.setSysDesc("System Error, Check logs for more information...");
    respObj.setExcepMsg(exp.getMessage());
    respObj.setStkTrace(trc);
    respObj.setLogFilePath("More information can be found at--->" + Constants.EXACT_LOG_PATH);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " 	+ respObj.getRespDesc());
    //log the transaction
    String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
    //set the trans id in response
    respObj.setTransId(transIds[0]);

    return respObj;
  }//end catch
}//end createACHAccount



/**
 * This method is used to verify the supplied ACH Account. First it gets the switch infromation
 * against the card-no, validates the switch (i.e. check the switch activation and online mode).
 * If the switch is not active or in the batch mode then it sets the response and pauses further
 * execution by returning the response with appropiate response code and description.
 * If switch validation were perfomred successfully it validates the card no and gets the serial no.
 * of the ach account. If verification is done then it checks the status of the ACH account.
 * At the end it returns the response which describes the status of the processing with appropiate
 * response code and response description.
 * @param requestObj ServicesRequestObj -- Request information
 * @throws Exception
 * @return ServicesResponseObj -- Response information
 */

  //////////////////////////////////// Changed /////////////////////////////////////////////////////
  //   Constants.ACH_VERIFY_SERVICE were replaced with requestObj.getServiceId()                  //
  //////////////////////////////////////////////////////////////////////////////////////////////////

  public ServicesResponseObj verifyAchAccount(ServicesRequestObj requestObj) throws Exception
  {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    ACHServiceHome achServiceHome = ACHServiceHome.getInstance(con);
    //validate the card information
    respObj = CardsServiceHome.getInstance(con).isCardInfoValid(requestObj.getCardNo(),requestObj.getAAC(),requestObj.getExpiryDate(),requestObj.getAccountNo(),requestObj.getPin(), requestObj.getServiceId(), requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAcquirerId());
    if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
      return respObj;

    try{
      //get the card switch info
      SwitchInfoObj switchInfo = achServiceHome.getCardSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() + ") is not active for Card (" + achServiceHome.maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans is Allowed.....");
        respObj.setRespCode("40");
        respObj.setRespDesc("Verify ACH Account Service is not avaiable for switch(" + switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Before Validating Card.......");
      //check the card
      respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(), null,requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Response after card validation :: Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
      //validate response
      if (!respObj.getRespCode().trim().equals(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Getting the ACH Account info against ACH Account No -- > " + requestObj.getAchAccountNo());
      //get the ach account info
      String checkQry = "select ach_acct_sr_no from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "";
      //execute the query and get the value
      String acctNo = achServiceHome.getValue(checkQry);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Account Serial No Found against supplied account no -- > " + acctNo);

      //validate account no
      if (acctNo == null) {
        respObj.setRespCode("06");
        respObj.setRespDesc("Supplied ACH Account No(" + requestObj.getAchAccountNo() + ") does not exist");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //endif

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Verifying account for ACH Type....");
      //verify whether the supplied ach account no is for load or withdraw?
      String acctTypeQry = "select ach_type from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo();
      //execute the query and get the achtype
      String achType = achServiceHome.getValue(acctTypeQry);

      if (achType != null && !achType.trim().equals("1")) {
        respObj.setRespCode("06");
        respObj.setRespDesc("Supplied ACH Account No(" + requestObj.getAchAccountNo() + ") is not a load account");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ACH Type of Account --- > " + achType);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Checking ACH Account Status ....");
      //get the ach account status
      String accountStatus = achServiceHome.getValue( "select verify_status from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ACH Account Status Got -- > " + accountStatus);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Getting the max reg tries ...");
      int maxTries = 3;
      //get the max retries limit for the ACH Account
      String maxRegTries = achServiceHome.getValue("select param_val from card_prog_params where card_prg_id=(select card_prg_id from cards where card_no='" + requestObj.getCardNo() + "') and param_code='ACHREGTRIESMAX'");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Max ACH Account Reg tries Got -- > " + maxRegTries);
      //check the results
      if (maxRegTries != null && !maxRegTries.trim().equals(""))
        maxTries = (int)Double.parseDouble(maxRegTries);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Getting the nfailed tries so far done....");
      //get the nfailed tries
      String nFailedTries = achServiceHome.getValue( "select nfailed_tries from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "NFailed Tries Got -- > " + nFailedTries);

      int failedTries = 0;
      if (nFailedTries != null && !nFailedTries.trim().equals(""))
        failedTries = Integer.parseInt(nFailedTries);

      if (accountStatus != null && accountStatus.trim().equalsIgnoreCase("F") && failedTries >=maxTries) {
        respObj.setRespCode("06");
        respObj.setRespDesc("Supplied ACH Account No(" + requestObj.getAchAccountNo() + ") is already failed");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if
      else if(accountStatus != null && accountStatus.trim().equalsIgnoreCase("V"))
      {
        respObj.setRespCode("06");
        respObj.setRespDesc("ACH Account (" + requestObj.getAchAccountNo() + ") is already verified");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      }//end else if
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Going to verify the amounts provided......");
      //make the query to verify the supplied amounts
      String verifyQry = "select ach_acct_sr_no from ach_accounts where ach_acct_sr_no= " + requestObj.getAchAccountNo() + " and ((test_amount1=" + (requestObj.getVerifyAmount1() != null ? requestObj.getVerifyAmount1() : "0.0") + " and test_amount2=" + (requestObj.getVerifyAmount2() != null ? requestObj.getVerifyAmount2() : "0.0") + " ) or (test_amount1=" + (requestObj.getVerifyAmount2() != null ? requestObj.getVerifyAmount2() : "0.0") + " and test_amount2=" + (requestObj.getVerifyAmount1() != null ? requestObj.getVerifyAmount1() : "0.0") + " )) ";
      //execute the query and get the value
      acctNo = achServiceHome.getValue(verifyQry);
      //validate acctNo
      if (acctNo == null) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Amounts do not matched with the supplied one......");
        //increment the failed tries as one more attempt has been failed
        failedTries++;

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Updating the nfailed tries and status of the ACH Account.....");
        String updQry = "update ach_accounts set nfailed_tries=" + failedTries;
        if (failedTries >= maxTries)
          updQry += ",verify_status='F'";

          //append where clause
        updQry += " where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "";

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Query Built -- > " + updQry);
        //execute the query and update the record
        achServiceHome.executeQuery(updQry);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ACH Account updated successfully");

        respObj.setRespCode("13");
        respObj.setRespDesc("Provided micro amounts to verify ACH Account do not match with the actual amounts");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if

      verifyQry = "update ach_accounts set verify_status='V',verification_dtime = current where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "";
      //execute the query to verify the amounts
      achServiceHome.executeQuery(verifyQry);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ACH Account Verified successfully....");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Going to log the transaction....");
      //log the transaction
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(), requestObj.getDeviceType(), Constants.ACH_ACCOUNT_MSG, null,requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Transaction logged with ISO Serial No -- > " + transIds[0]);

      if (requestObj.getApplyFee().equalsIgnoreCase("Y")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Applying the Fee....");
        //apply the fee at OLTP
        respObj = achServiceHome.applyServiceFeeAtOltp(requestObj.getCardNo(), requestObj.getServiceId(), null, requestObj.getRetreivalRefNum(), transIds[1], requestObj.getDeviceType(), requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),
                                    requestObj.getAcquirerId(),
                                    requestObj.getAcqUsrId(),
                                    requestObj.getAcqData1(),
                                    requestObj.getAcqData2(),
                                    requestObj.getAcqData3());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),            "Response after Applying Fee :: Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        {
          //rollback all the work
          con.rollback();

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
          //log the transaction
          transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);

          return respObj;
        }//end if
      } //end if
      else
      {
        //get the latest balance
        String balance = achServiceHome.getValue("select card_balance from card_funds where card_no='" + requestObj.getCardNo() + "'");
        //set the balance in response
        respObj.setCardBalance(balance);
      }//end else
      //set the iso serial no in response
      respObj.setTransId(transIds[0]);
      //populate the response object to success message
      respObj.setRespCode(Constants.SUCCESS_CODE);
      respObj.setRespDesc(Constants.SUCCESS_MSG);
      return respObj;
    }//end try
    catch(Exception exp)
    {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),"Exception --- > " + exp.getMessage() + "Stack Trace -- > " + trc);
      try {
        if(con != null)
          con.rollback();
      }//end try
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
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    }//end catch
  }//end verifyAchAccount

  /**
    * This method gets the ACH Account Status. This method first gets the switch infromation,
    * checks the switch activation and switch mode. If the switch against the card-no is inactive or in
    * the batch mode then it returns the appropiate response with proper description. It then validates
    * the card and finds the status of the ACH account against given card no and returns the response
    * which describes the status of the processing. In case of successful processing the response contains the
    * ACH account status. In case of any error response contains the response code with appropiate
    * description of the processing. At the end of the processing the transaction is logged into the
    * database.
    * @param requestObj ServicesRequestObj -- Request information
    * @throws Exception
    * @return ServicesResponseObj -- Response information
    */


  /////////////////////////////////// Changed ////////////////////////////////////////////////////////
  //     Constants.ACH_ACCT_STATUS_SERVICE were replaced with requestObj.getServiceId()            //
  ////////////////////////////////////////////////////////////////////////////////////////////////////

  public ServicesResponseObj getAchAccountStatus(ServicesRequestObj requestObj) throws Exception
  {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //validate the card information
    respObj = CardsServiceHome.getInstance(con).isCardInfoValid(requestObj.getCardNo(),requestObj.getAAC(),requestObj.getExpiryDate(),requestObj.getAccountNo(),requestObj.getPin(), requestObj.getServiceId(), requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAcquirerId());
    if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
      return respObj;
    //get the reference of ACH Service Home
    ACHServiceHome achServiceHome = ACHServiceHome.getInstance(con);
    try{
      //get the card switch info
      SwitchInfoObj switchInfo = achServiceHome.getCardSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() + ") is not active for Card (" + achServiceHome.maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans is Allowed.....");
        respObj.setRespCode("40");
        respObj.setRespDesc( "Create ACH Account Service is not avaiable for switch(" + switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Validating the Card....");
      //validate the card
      respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(), null,requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Response after validating card :: Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());

      //validate the response
      if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Checking the ACH Account Existance.....");
      //make the query to check the account existance in db
      String acctQry =
          "select ach_acct_sr_no from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "";
      //execute the query and get the result
      String acctNo = achServiceHome.getValue(acctQry);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Got the Account No --> " + acctNo);
      //validate the acctNo
      if (acctNo == null) {
        respObj.setRespCode("06");
        respObj.setRespDesc("ACH Account No(" + requestObj.getAchAccountNo() + ") does not exist");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);

        return respObj;
      } //end if

      //verify whether the supplied ach account no is for load or withdraw?
      String acctTypeQry = "select ach_type from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo();
      //execute the query and get the achtype
      String achType = achServiceHome.getValue(acctTypeQry);

      if (achType != null && !achType.trim().equals("1")) {
        respObj.setRespCode("06");
        respObj.setRespDesc("Supplied ACH Account No(" + requestObj.getAchAccountNo() + ") is not a load account");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);

        return respObj;
      } //end if

      //make the query to get the account status
      acctQry = "select verify_status from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo();
      //execute the query and get the status
      String acctStatus = achServiceHome.getValue(acctQry);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Going to log the transaction....");
      //log the transaction
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(),Constants.ACH_ACCOUNT_STATUS_MSG, null,requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Transaction logged with ISO Serial No -- > " + transIds[0]);

      if (requestObj.getApplyFee().equalsIgnoreCase("Y")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Applying fee at OLTP...");
        //apply the fee at OLTP
        respObj = achServiceHome.applyServiceFeeAtOltp(requestObj.getCardNo(), requestObj.getServiceId(), null, requestObj.getRetreivalRefNum(),transIds[1], requestObj.getDeviceType(), requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),
                                    requestObj.getAcquirerId(),
                                    requestObj.getAcqUsrId(),
                                    requestObj.getAcqData1(),
                                    requestObj.getAcqData2(),
                                    requestObj.getAcqData3()
);
        if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
          return respObj;
      } //end if
      else
      {
        //get the latest balance
        String balance = achServiceHome.getValue("select card_balance from card_funds where card_no='" + requestObj.getCardNo() + "'");
        //set the balance in response
        respObj.setCardBalance(balance);
      }//end else
      //set the iso serial no in response
      respObj.setTransId(transIds[0]);
      //return the account status with success code
      respObj.setRespCode(Constants.SUCCESS_CODE);
      respObj.setRespDesc(Constants.SUCCESS_MSG);

      if(acctStatus != null && acctStatus.trim().equalsIgnoreCase("L"))
        respObj.setAchAccountStatusDesc("Logged");
      else if(acctStatus != null && acctStatus.trim().equalsIgnoreCase("I"))
        respObj.setAchAccountStatusDesc("In Process");
      else if(acctStatus != null && acctStatus.trim().equalsIgnoreCase("V"))
        respObj.setAchAccountStatusDesc("Verified");
      else if(acctStatus != null && acctStatus.trim().equalsIgnoreCase("F"))
        respObj.setAchAccountStatusDesc("Failed");

      respObj.setAchAccountStatus(acctStatus);
      return respObj;
    }//end try
    catch(Exception exp)
    {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),"Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if(con != null)
          con.rollback();
      }//end try
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
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    }//end  catch
  }//end getAchAccountStatus

  /**
   * This method is used to change the "ACH Account Nick Name". This method first fetches the switch information
   * from the database, validates the switch activation and switch mode. If the switch is inactive or in the
   * batch mode then it pauses the execution and returns the response with appropiate error response code and
   * the description of the response. If switch validation was successful then it fetches the serial no.
   * against the card-no and changes the existing ACH account nick with the new ACH account nick.
   * @param requestObj ServicesRequestObj -- Request Information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */


  ////////////////////////// Changed ///////////////////////////////////////
  /////////// Constants.CHANGE_ACH_ACCOUNT_SERVICES were replaced with requestObj.getServiceId()
  ///////////////////////////////////////////////////////////////////////////////////////////////

  public ServicesResponseObj changeAchAccountNick(ServicesRequestObj requestObj) throws Exception
    {
      //make the response object
      ServicesResponseObj respObj = new ServicesResponseObj();
      //validate the card information
      respObj = CardsServiceHome.getInstance(con).isCardInfoValid(requestObj.getCardNo(),requestObj.getAAC(),requestObj.getExpiryDate(),requestObj.getAccountNo(),requestObj.getPin(), requestObj.getServiceId(), requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAcquirerId());
      if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;
      //get the reference of ACH Service Home
      ACHServiceHome achServiceHome = ACHServiceHome.getInstance(con);
      try{
        //get the card switch info
        SwitchInfoObj switchInfo = achServiceHome.getCardSwitchInfo(requestObj.getCardNo());

        if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
          respObj.setRespCode("91");
          respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() + ") is not active for Card (" + achServiceHome.maskCardNo(requestObj.getCardNo()) + ")");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
          //log the transaction
          String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);
          return respObj;
        } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

        if (switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed()) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans is Allowed.....");
          respObj.setRespCode("40");
          respObj.setRespDesc("Create ACH Account Service is not avaiable for switch(" + switchInfo.getSwitchId() + ")");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
          //log the transaction
          String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);

          return respObj;
        } //end if(switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Before validating the Card....");
        //validate the card
        respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.getCardNo(),requestObj.getServiceId(), requestObj.getApplyFee(), null,requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Response after card validation :: Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //validate the response
        if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
          return respObj;

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Checking the ACH Account Existance.....");
        //make the query to check the account existance in db
        String acctQry ="select ach_acct_sr_no from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "";
        //execute the query and get the result
        String acctNo = achServiceHome.getValue(acctQry);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Got the Account No --> " + acctNo);
        //validate the acctNo
        if (acctNo == null) {
          respObj.setRespCode("12");
          respObj.setRespDesc("ACH Account No(" + requestObj.getAchAccountNo() + ") does not exist");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
          //log the transaction
          String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);

          return respObj;
        } //end if

//    //verify whether the supplied ach account no is for load or withdraw?
//    String acctTypeQry = "select ach_type from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo();
//    //execute the query and get the achtype
//    String achType = achServiceHome.getValue(acctTypeQry);
//
//    if(achType != null && !achType.trim().equals("1"))
//    {
//      respObj.setRespCode("06");
//      respObj.setRespDesc("Supplied ACH Account No(" + requestObj.getAchAccountNo() + ") is not a load account");
//      return respObj;
//    }//end if

        //make the query to check the status of account

      //acctQry = "update ach_accounts set ach_acct_nick='" + requestObj.getNickName() + "' where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "";

        //////////////////////////////////////Added By Imtiaz (Replaced above Commented Line////////
        if(requestObj.getNickName()!=null){
          acctQry ="update ach_accounts set ach_acct_nick= ? where ach_acct_sr_no=?";
          PreparedStatement stm = con.prepareStatement(acctQry);
          stm.setString(1, requestObj.getNickName());
          stm.setString(2, requestObj.getAchAccountNo());
          stm.executeUpdate();
          }
        else{
          acctQry ="update ach_accounts set ivr_ach_act_nick= ? where ach_acct_sr_no=?";
          PreparedStatement stm = con.prepareStatement(acctQry);
          stm.setBytes(1,requestObj.getAudioFile());
          stm.setString(2, requestObj.getAchAccountNo());
          stm.executeUpdate();
        }

      /////////////////////////////////////////////////////////////////////////////////////

      //execute the query to change the nick
        //achServiceHome.executeQuery(acctQry);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Nick has been changed successfully....");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Going to log the transaction....");
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(),Constants.CHANGE_ACH_ACCT_MSG, null,requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Transaction logged with ISO Serial No -- > " + transIds[0]);

        if (requestObj.getApplyFee().equalsIgnoreCase("Y")) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Applying fee at OLTP....");
          //apply fee at OLTP
          respObj = achServiceHome.applyServiceFeeAtOltp( requestObj.getCardNo(), requestObj.getServiceId(), null, requestObj.getRetreivalRefNum(), transIds[1], requestObj.getDeviceType(), requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),
                                    requestObj.getAcquirerId(),
                                    requestObj.getAcqUsrId(),
                                    requestObj.getAcqData1(),
                                    requestObj.getAcqData2(),
                                    requestObj.getAcqData3()
);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Response after applying fee :: Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
          if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
          {
            //rollback all the work
            con.rollback();

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
            //log the transaction
            transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
            //set the trans id in response
            respObj.setTransId(transIds[0]);
            return respObj;
          }//end if
        } //end if
        else
        {
          //get the latest balance
          String balance = achServiceHome.getValue("select card_balance from card_funds where card_no='" + requestObj.getCardNo() + "'");
          //set the balance in response
          respObj.setCardBalance(balance);
        }//end else
        //set the iso serial no in response
        respObj.setTransId(transIds[0]);
        respObj.setRespCode(Constants.SUCCESS_CODE);
        respObj.setRespDesc(Constants.SUCCESS_MSG);
        return respObj;
      }//end try
      catch(Exception exp)
      {
        String trc = CommonUtilities.getStackTrace(exp);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),"Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
        try {
          if(con != null)
            con.rollback();
        }//end try
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
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      }//end catch
    }//end changeAchAccountNick


    /**
     * This method changes the ACH Account information for "Withdraw Account" only. This method
     * first gets the switch infromation from the database, validates the switch for activenes and online
     * mode. If the switch is inactive or having batch mode then it returns the response with the
     * proper response code and the response description which describes the cause of the error.
     * It then gets the "ach account serial no" for the requested ach account no and updates the ACH
     * account information contained in the request object.
     * @param requestObj ServicesRequestObj -- Request information
     * @throws Exception
     * @return ServicesResponseObj -- Response information
     */


  ////////////////////// Changed //////////////////////////////////////////////////////////
  //////////// Constants.CHANGE_ACH_ACCOUNT_INFO were replaced with requestObj.getServiceId()

  public ServicesResponseObj changeACHAccountInfo(ServicesRequestObj requestObj) throws Exception
  {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //validate the card information
    respObj = CardsServiceHome.getInstance(con).isCardInfoValid(requestObj.getCardNo(),requestObj.getAAC(),requestObj.getExpiryDate(),requestObj.getAccountNo(),requestObj.getPin(), requestObj.getServiceId(), requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAcquirerId());
    if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
      return respObj;
    //get the ACH Service Home reference
    ACHServiceHome achServiceHome = ACHServiceHome.getInstance(con);

    try{
      //get the card switch info
      SwitchInfoObj switchInfo = achServiceHome.getCardSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() + ") is not active for Card (" + achServiceHome.maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans is Allowed.....");
        respObj.setRespCode("40");
        respObj.setRespDesc("Create ACH Account Service is not avaiable for switch(" + switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Validating the Card....");
      //validate the card
      respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(), null,requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Response after card validation :: Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
      //validate the response
      if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Checking the ACH Account Existance.....");
      //make the query to check the account existance in db
      String acctQry ="select ach_acct_sr_no from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "";
      //execute the query and get the result
      String acctNo = achServiceHome.getValue(acctQry);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Got the Account No --> " + acctNo);
      //validate the acctNo
      if (acctNo == null) {
        respObj.setRespCode("06");
        respObj.setRespDesc("ACH Account No(" + requestObj.getAchAccountNo() + ") does not exist");
        return respObj;
      } //end if

      //verify whether the supplied ach account no is for load or withdraw?
      String acctTypeQry = "select ach_type from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo();
      //execute the query and get the achtype
      String achType = achServiceHome.getValue(acctTypeQry);

      if (achType != null && !achType.trim().equals("2")) {
        respObj.setRespCode("06");
        respObj.setRespDesc("Supplied ACH Account No(" + requestObj.getAchAccountNo() + ") is not a withdraw only account");
        return respObj;
      } //end if

      //make the query to check the status of account
      acctQry = CommonUtilities.makeAchAccountUpdateQry(requestObj);
      //execute the query to change the nick
      achServiceHome.executeQuery(acctQry);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Account Information has been changed successfully....");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Going to log the transaction....");
      //log the transaction
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(),Constants.CHANGE_ACH_ACCT_MSG, null,requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Transaction logged with ISO Serial No -- > " + transIds[0]);

      if (requestObj.getApplyFee().equalsIgnoreCase("Y")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Applying Fee at OLTP...");
        //apply the fee at OLTP
        respObj = achServiceHome.applyServiceFeeAtOltp(requestObj.getCardNo(), requestObj.getServiceId(), null, requestObj.getRetreivalRefNum(), transIds[1], requestObj.getDeviceType(), requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),
                                    requestObj.getAcquirerId(),
                                    requestObj.getAcqUsrId(),
                                    requestObj.getAcqData1(),
                                    requestObj.getAcqData2(),
                                    requestObj.getAcqData3()
);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Response after applying fee :: Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        {
          //rollback all the work
          con.rollback();
          return respObj;
        }//end if
      } //end if
      else
      {
        //get the latest balance
        String balance = achServiceHome.getValue("select card_balance from card_funds where card_no='" + requestObj.getCardNo() + "'");
        //set the balance in response
        respObj.setCardBalance(balance);
      }//end else
      //set the iso serial no in response
      respObj.setTransId(transIds[0]);
      respObj.setRespCode(Constants.SUCCESS_CODE);
      respObj.setRespDesc(Constants.SUCCESS_MSG);
      return respObj;
    }//end try
    catch(Exception exp)
    {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),"Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if(con != null)
          con.rollback();
      }//end try
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
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    }
  }//end changeACHAccountInfo

  /**
    * This method gets the ACH Account information. It first gets the switch infromation from
    * the database and validates whether switch is active and online. If the switch is inactive or in the
    * batch mode then it returns the "ServiceReponseObj" which describes the error code (Response code) and
    * error description (Response Description). It then validates the card-no contained in the
    * request and uses the Transfer API to get the ACH Account information. During processing if an
    * error occcured it returns the response with the appropiate response code and description.
    * @param requestObj ServicesRequestObj -- Request info
    * @throws Exception
    * @return ServicesResponseObj -- Response info
    */

/////////////////////////////////// Changed /////////////////////////////////////////////////////////
// Constants.ACH_ACCOUNT_INFO_SERVICE were replaced with requestObj.getServiceId()                 //
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public ServicesResponseObj getACHAccountInfo(ServicesRequestObj requestObj) throws Exception
  {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //validate the card information
    respObj = CardsServiceHome.getInstance(con).isCardInfoValid(requestObj.getCardNo(),requestObj.getAAC(),requestObj.getExpiryDate(),requestObj.getAccountNo(),requestObj.getPin(), requestObj.getServiceId(), requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAcquirerId());
    if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
      return respObj;
    //get the reference of ACH Service Home
    ACHServiceHome achServiceHome = ACHServiceHome.getInstance(con);
    try{
      //get the card switch info
      SwitchInfoObj switchInfo = achServiceHome.getCardSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() + ") is not active for Card (" + achServiceHome.maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Batch Trans is Allowed.....");
        respObj.setRespCode("40");
        respObj.setRespDesc("ACH Account Info Service is not avaiable for switch(" + switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Going to log the error transaction::Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
        //log the transaction
        String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Validating the Card....");
      //validate the card
      respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(), null,requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Response after card validation :: Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());
      //validate the response
      if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Calling the Transfer API to get the info.....");
      //pass the call to transfer api
      AchObject achObj = new TransferAPI(Constants.TRANSFERAPI_LOG_PATH).getAchInfo(con,requestObj.getCardNo(),requestObj.getBankAcctNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"After calling transfer api....");
      if(achObj == null)
      {
        respObj.setRespCode("96");
        respObj.setRespDesc("System Error");
        return respObj;
      }//end if
      //set the account status
      respObj.setAchAccountStatus(achObj.getStatus());
      //set the ACH Type
      respObj.setAchType(achObj.getAchType());
      //set the account type
      respObj.setBankAccountType(achObj.getAccountType());
      //set the account title
      respObj.setBankAccountTitle(achObj.getAccountTitle());
      //set the bank routing no
      respObj.setBankRoutingNo(achObj.getRoutingNo());
      //set bank name
      respObj.setBankName(achObj.getBankName());
      respObj.setRespCode(Constants.SUCCESS_CODE);
      respObj.setRespDesc(Constants.SUCCESS_MSG);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Going to log the transaction....");
      //log the transaction
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(), requestObj.getDeviceType(),Constants.ACH_ACCOUNT_INFO_MSG,null,requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Transaction logged with ISO Serial No -- > " + transIds[0]);

      if(requestObj.getApplyFee().equalsIgnoreCase("Y"))
      {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Applying Fee....");
        //apply the fee
        respObj = achServiceHome.applyServiceFeeAtOltp(requestObj.getCardNo(),requestObj.getServiceId(),null,requestObj.getRetreivalRefNum(),transIds[1], requestObj.getDeviceType(), requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),
                                    requestObj.getAcquirerId(),
                                    requestObj.getAcqUsrId(),
                                    requestObj.getAcqData1(),
                                    requestObj.getAcqData2(),
                                    requestObj.getAcqData3()
);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Response after applying Fee:: Code -- > " + respObj.getRespCode() + " && Desc -- > " + respObj.getRespDesc());

        if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
          return respObj;
      }//end if
      else
      {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Getting the card balance...");
          //get the latest balance
          String balance = achServiceHome.getValue("select card_balance from card_funds where card_no='" + requestObj.getCardNo() + "'");
          //set the balance in response
          respObj.setCardBalance(balance);
      }//end else

      //set the transaction id
      respObj.setTransId(transIds[0]);
      respObj.setRespCode(Constants.SUCCESS_CODE);
      respObj.setRespDesc(Constants.SUCCESS_MSG);
      return respObj;
    }//end try
    catch(Exception exp)
    {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),"Exception -- > " + exp.getMessage()+ "Stack Trace -- > " + trc);
      try {
        if(con != null)
          con.rollback();
      }//end try
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
      String[] transIds = achServiceHome.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),"Transaction logged with iso serial no -- > " + transIds[0] + " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);

      return respObj;
    }//end catch
  }//end getACHAccountInfo

  private Integer computeCheckDigit(String routingno) {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Method for validating bank routing number--->" + routingno);

    if (routingno.trim().length() != 8){
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Method for validating bank routing number --- routing number is not of required length--->" + routingno);
      throw new IllegalArgumentException(
          "Routing No length should be equal to 8 digits");
    }

    char[] array = routingno.toCharArray();
    int sum = 0;
    int weights[] = {3, 7, 1, 3, 7, 1, 3, 7};
    int checkDigit = 0;
    for (int i = 0; i < array.length; i++) {
      int digit = Character.getNumericValue(array[i]);
      sum += digit * weights[i];
    }

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Method for validating bank routing number --- SUM calculated--->" + sum);
    for (int j = 10;;) {
      if (j > sum || j == sum) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"j --->" + j);
        checkDigit = j - sum;
        break;
      }else{
        j += 10;
      }
    }
    return new Integer(checkDigit);
  }
}//end ACHServiceHandler


