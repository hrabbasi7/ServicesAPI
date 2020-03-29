package com.i2c.service.solsparkservice;
import java.io.*;
import java.net.*;
import java.util.*;
import com.i2c.service.util.*;
import java.sql.*;
import com.i2c.service.excep.*;
import com.i2c.services.*;
import com.informix.jdbc.IfxDriver;
import com.i2c.solspark.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ServiceThread extends Thread {
  private Socket socket = null;
  private StringBuffer mailMsg = null;
  private StringBuffer expMsg = null;
  private String instanceName = null;
  private StringBuffer serviceResp = null;
  //Constructor
  public ServiceThread(Socket _socket) {
    socket = _socket;
    mailMsg = new StringBuffer();
    expMsg = new StringBuffer();
    serviceResp = new StringBuffer();
    if(Constants.getMachineIP() != null){
      mailMsg.append(Constants.MAIL_REPORT_ADMIN_MESSAGE + "\n\nMachine IP: " + Constants.getMachineIP());
    }
    else {
      mailMsg.append(Constants.MAIL_REPORT_ADMIN_MESSAGE + "\n\nMachine IP: N/A" );
    }
  }//end constructor

  /**
   * Run Method
   */
  public void run() {
    RequestInfoObj requestInfo = null;
    ResponseInfoObj responseObj = null;
    String clientRequest = null;
    String responseMessage = null;

    boolean sendMessageFlag = false;
    try {
      //Client IP Value
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"The Requesting Client IP is -->"+socket.getInetAddress().getHostAddress());
      //Reading the Request Message
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Service Thread ---> Calling readClientRequest");
      clientRequest = readClientRequest();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," The Client Request -->"+clientRequest);

      //Validating the Client Request
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Service Thread ---> Calling validateRequestMessage");
      requestInfo = validateRequestMessage(clientRequest);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," The Request Object is -->"+requestInfo);

      //Processing the request message
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Service Thread ---> Calling processRequestMessage");
      responseObj = processRequestMessage(requestInfo);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," The Response Object is -->"+responseObj);

      //Preparing the response message
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Service Thread ---> Calling prepareClientResponse");
      responseMessage = prepareClientResponse(responseObj, requestInfo);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," The Prepared Reponse Message is -->"+responseMessage);

      //Sending the Response message
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Service Thread ---> Calling sendClientResponse");
      sendMessageFlag = sendClientResponse(clientRequest,responseMessage, requestInfo);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," sendMessageFlag -->"+sendMessageFlag);

    } catch(Exception e){
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in Run Thread -->"+e);
        expMsg.append("\n");
        expMsg.append("\nException Message: " + e.toString());
        expMsg.append("\n\n\nStack Trace: " + CommonUtilities.getStackTrace(e));
        expMsg.append("\n");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Mail Message Build so far---->" + mailMsg);
    } finally {
      //If unable to send the success message then send the error message
      if (!sendMessageFlag)
        try {
          sendClientResponse(clientRequest,Constants.ERROR_MALFUNCTION_ID +
                             Constants.MESSAGE_DELIMETER_VALUE +
                             Constants.ERROR_MALFUNCTION_MESSAGE, requestInfo);
        }
        catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST), " Exception in sending response message -->" + ex);
        }
      //Send Error Email To Admin
      sendAdminErrorEmail(responseObj);
    }//end finally
   }//end run method

   public void sendAdminErrorEmail(ResponseInfoObj responseObj){
     boolean notifyFlag = false;
     try {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," sendAdminErrorEmail --- Notify_Resp_Codes_Values -->" + Constants.NOTIFY_RESP_CODES_VALUES);
       // checking if Resposne code value matches any of the Response codes for which mail should be genrerated
       String[] respList = CommonUtilities.convertStringArray(Constants.NOTIFY_RESP_CODES_VALUES,Constants.DEFAULT_DELIMETER_STRING);
       if (respList != null && responseObj != null && responseObj.getRespCode() != null) {
         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Notify_Resp_Codes_Values array size-->" + respList.length + "<-- Response Code got---> " + responseObj.getRespCode());
         notifyFlag = isEmailResponseCode(respList,responseObj.getRespCode());
         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Result of isEmailResponseCode---->" + notifyFlag);
       }//end if

       // if notifyFlag then send mail
       if (notifyFlag || (expMsg != null && expMsg.length() > 0 )) {
         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Calling function to send email --- Notify Flag value--->" + notifyFlag+"<----expMsg -->"+expMsg+",--- mailMsg--->"+mailMsg );

         //Append the exception message
         if (expMsg != null && expMsg.length() > 0 )
           mailMsg.append(expMsg);

         mailMsg.append("\n\n\n" + Constants.MAIL_REPORT_ADMIN_FOOTER);

         //Send the email
         if(!new ServiceHome().sendAdminEmail(mailMsg.toString(),Constants.MAIL_REPORT_ADMIN_SUBJECT)) {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," System is unable to send email, check the logs for more information");
         }//end if
      }//end if notify

     } catch (Exception ex) {
       System.out.println("Exception in sending Admin Error Email --->"+ex);
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
           LOG_FINEST), " Exception in sending Admin Error Email -->" + ex);
     }//end catach

   }//end method

  /**
   *
   * Method in processing the message and send the response
   * @param clientRequest: the client request
   */
  private ResponseInfoObj processRequestMessage(RequestInfoObj requestInfo) throws ProcessValuesExcep
  {
    Connection dbConn = null;

    ResponseInfoObj responseObj = null;

    ServicesHandler handler=null;
    ServicesRequestObj servicesRequestObj=null;
    ServicesResponseObj servicesResponseObj=null;
    ServiceHome serviceHome = new ServiceHome();

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Process Request Message -->"+requestInfo+"<--- Function -->"+requestInfo.getFunction()+"<-- Instance -->"+requestInfo.getInstance()+"<-- Switch -->"+requestInfo.getSwitchInfo()+"<-- Card Num -->"+requestInfo.getCardNum());
    try
    {
      boolean validFlag = true;

      dbConn = DatabaseHandler.getConnection("ServiceThread",requestInfo.getInstance());
      handler=ServicesHandler.getInstance(dbConn,Constants.SERVICES_LOG_FILE_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," calling validateCards -->");

      //-------------Check if the Card Numbers provided are valid or not---------

      try
      {
        validFlag = serviceHome.validateCards(requestInfo, dbConn, this);
      }
      catch (InvalidFieldValueExcep ifvex)
      {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in verfiying card-->" + ifvex);
        responseObj = new ResponseInfoObj();
        responseObj.setRespCode(Constants.INVALID_CARD_NUMBER_RESPONSE);
        responseObj.setSwitchResponseCode(Constants.INVALID_CARD_NUMBER_RESPONSE);
        throw new InvalidFieldValueExcep(-1,ifvex.getMessage());
      }
      catch (ProcessValuesExcep ex1)
      {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in validateCards-->"+ex1);
        throw new ProcessValuesExcep(-1,"Exception in validateCards-->"+ex1);
      }

      if(!validFlag)
      {
        //Setting the response object
         responseObj = new ResponseInfoObj();
         responseObj.setRespCode(Constants.INVALID_CARD_NUMBER_RESPONSE);
         responseObj.setSwitchResponseCode(Constants.INVALID_CARD_NUMBER_RESPONSE);
         throw new InvalidFieldValueExcep(-1,"Unable to verify the card");
      }
      //***********************Get Balance Function***************************
      else if (requestInfo.getFunction().equalsIgnoreCase(Constants.GET_BALANCE_FUNCTION))
      {
        try
        {
          servicesRequestObj = mapServicesRequestInformation(requestInfo);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API getCardBalance---| ");
          servicesResponseObj = handler.getCardBalance(servicesRequestObj);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API getCardBalance---| ");
          responseObj = mapServicesResponseInformation(servicesResponseObj);
        }
        catch(Exception e)
        {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Services API getCardBalance-->"+e);
           throw new ProcessValuesExcep(-1,"Exception in Services API getCardBalance-->"+e);
        }
      }
      //********************Get Service Fee Function****************************
       else if (requestInfo.getFunction().equalsIgnoreCase(Constants.GET_SERVICE_FEE_FUNCTION))
       {
         String serviceID = null;
         try
         {
           if(requestInfo.getServiceID().equalsIgnoreCase(Constants.CARD_TO_CARD_ID)){
             if (requestInfo.getCardNum() != null &&
                 requestInfo.getCardNum().trim().length() > 0 &&
                 requestInfo.getCardNumTo() != null &&
                 requestInfo.getCardNumTo().trim().length() > 0) {
               serviceID = serviceHome.checkCardUserID(dbConn, requestInfo);
               requestInfo.setServiceID(serviceID);
               CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                   LOG_FINEST),
                                               " |---Service ID set for Card TO Card Transfer---| " +
                                               requestInfo.getServiceID());
             }
           }
           servicesRequestObj = mapServicesRequestInformation(requestInfo);
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API getServiceFee---| ");
           servicesResponseObj = handler.getServiceFee(servicesRequestObj);
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API getServiceFee---| ");
           responseObj = mapServicesResponseInformation(servicesResponseObj);
         }
         catch(Exception e)
         {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Services API getCardBalance-->"+e);
            throw new ProcessValuesExcep(-1,"Exception in Services API getCardBalance-->"+e);
         }
       }else if(requestInfo.getFunction().equalsIgnoreCase(Constants.GET_C2C_PARAMETER_VALUES)){
        try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling getC2CAmountParamterValue --- Card Number---> " +requestInfo.getCardNum());
          responseObj = serviceHome.getC2CAmountParamterValue(dbConn,requestInfo.getCardNum());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Response returned from getC2CAmountParamterValue --- Response Code---> " + responseObj.getRespCode() + "<---Min Value---> " + responseObj.getAuditNo() + "<---Max Value---> " + responseObj.getFeeAmount());
        }
        catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in getting C2C parameter values-->"+ex);
          throw new ProcessValuesExcep(-1,"Exception in getting C2C parameter values-->"+ex);
        }
       }
      //*************************Reload Function******************************
      else if (requestInfo.getFunction().equalsIgnoreCase(Constants.RELOAD_CARD_FUNCTION))
      {
        try
        {
          servicesRequestObj = mapServicesRequestInformation(requestInfo);
          if(servicesRequestObj.getAmount() != null && servicesRequestObj.getAmount().trim().length() > 0 && Double.parseDouble(servicesRequestObj.getAmount())<0)
          {
            double tempAmount=Double.parseDouble(servicesRequestObj.getAmount());
            tempAmount = -tempAmount;
            servicesRequestObj.setAmount(Double.toString(tempAmount));
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API debitFunds---| ");
            servicesResponseObj = handler.debitFunds(servicesRequestObj);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API debitFunds---| ");
          }
          else if(servicesRequestObj.getAmount() != null && servicesRequestObj.getAmount().trim().length() > 0 && Double.parseDouble(servicesRequestObj.getAmount())>=0)
          {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API creditFunds---| ");
            servicesResponseObj = handler.creditFunds(servicesRequestObj);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API creditFunds---| ");
          }
          responseObj = mapServicesResponseInformation(servicesResponseObj);
        }
        catch(Exception e)
        {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Services API Reload Functions-->"+e);
          throw new ProcessValuesExcep(-1,"Exception in Services API Reload Functions-->"+e);
        }
      }
      //**************************Activate Card Function****************************
      else if (requestInfo.getFunction().equalsIgnoreCase(Constants.ACTIVATE_CARD_FUNCTION))
      {
        try
        {
          servicesRequestObj = mapServicesRequestInformation(requestInfo);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API activateCard---| ");
          servicesResponseObj = handler.activateCard(servicesRequestObj);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API activateCard---| ");
          responseObj = mapServicesResponseInformation(servicesResponseObj);
        }
        catch(Exception e)
        {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Services API activateCard -->"+e);
           throw new ProcessValuesExcep(-1,"Exception in Services API activateCard -->"+e);
        }
      }
     //***************************Validate the User Information from the database****
      else if (requestInfo.getFunction().equalsIgnoreCase(Constants.VALIDATED_USER_FUNCTION))
      {
        try
        {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants. LOG_FINEST), " |---Calling validateCardInformation---| ");
          responseObj = new ServiceHome().validateCardInformation(requestInfo,dbConn);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), " |---Returned validateCardInformation---| ");
        }
        catch (Exception e)
        {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in validate Card/user function -->"+e);
           throw new ProcessValuesExcep(-1,"Exception in validate Card/User function -->"+e);
        }
      }

      //***************************Update Person Information Function****************
      else if (requestInfo.getFunction().equalsIgnoreCase(Constants.UPDATE_PERSON_FUNCTION))
      {
        try
        {
          servicesRequestObj = mapServicesRequestInformation(requestInfo);

             if (servicesRequestObj.getNewAAC() != null && servicesRequestObj.getNewAAC().trim().length() > 0)
             {
                System.out.println("ACC**********************************");
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API setCardAccessCode---| ");
                servicesResponseObj = handler.setCardAccessCode(servicesRequestObj);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API setCardAccessCode---| ");
                if(servicesResponseObj.getRespCode().equalsIgnoreCase("00") && servicesRequestObj.getNewPin() != null && servicesRequestObj.getNewPin().trim().length() > 0)
                {
                  System.out.println("PIN**********************************");
                  servicesRequestObj.setAAC(servicesResponseObj.getAAC());
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API setCardPIN---| ");
                  servicesResponseObj = ServicesHandler.getInstance(dbConn,Constants.SERVICES_LOG_FILE_PATH).setCardPIN(servicesRequestObj);
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API setCardPIN---| ");
                  if (servicesResponseObj.getRespCode().equalsIgnoreCase("00"))
                  {
                    System.out.println("PROFILE**********************************");
                    servicesRequestObj.setPin(servicesResponseObj.getPin());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API updateCardHolderProfile---| ");
                    servicesResponseObj = ServicesHandler.getInstance(dbConn,Constants.SERVICES_LOG_FILE_PATH).updateCardHolderProfile(servicesRequestObj);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API updateCardHolderProfile---| ");
                  }
                }
                else if(servicesResponseObj.getRespCode().equalsIgnoreCase("00") && (servicesRequestObj.getNewPin() == null || servicesRequestObj.getNewPin().trim().length() == 0))
                {
                   System.out.println("PROFILE**********************************");
                   System.out.println("Updated AAC: " + servicesResponseObj.getAAC());
                   System.out.println("Old AAC: " + servicesRequestObj.getAAC());
                   servicesRequestObj.setAAC(servicesResponseObj.getAAC());
                   CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API updateCardHolderProfile---| ");
                   servicesResponseObj = ServicesHandler.getInstance(dbConn,Constants.SERVICES_LOG_FILE_PATH).updateCardHolderProfile(servicesRequestObj);
                   CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API updateCardHolderProfile---| ");
                }
             }
             else if(servicesRequestObj.getNewAAC() == null && servicesRequestObj.getNewPin() != null)
             {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API setCardPIN---| ");
                  servicesResponseObj = ServicesHandler.getInstance(dbConn,Constants.SERVICES_LOG_FILE_PATH).setCardPIN(servicesRequestObj);
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API setCardPIN---| ");
                  if (servicesResponseObj.getRespCode().equalsIgnoreCase("00"))
                  {
                    servicesRequestObj.setPin(servicesResponseObj.getPin());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API updateCardHolderProfile---| ");
                    servicesResponseObj = ServicesHandler.getInstance(dbConn,Constants.SERVICES_LOG_FILE_PATH).updateCardHolderProfile(servicesRequestObj);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API updateCardHolderProfile---| ");
                  }
             }
             else if(servicesRequestObj.getNewAAC() == null && servicesRequestObj.getNewPin() == null)
             {
               CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API updateCardHolderProfile---| ");
               servicesResponseObj = handler.updateCardHolderProfile(servicesRequestObj);
               CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API updateCardHolderProfile---| ");
             }
          responseObj = mapServicesResponseInformation(servicesResponseObj);
          //CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," responseObj.getSwitchResponseCode() "+responseObj.getSwitchResponseCode());
          /*
          if (responseObj != null && responseObj.getSwitchResponseCode() != null && responseObj.getSwitchResponseCode().trim().equalsIgnoreCase(Constants.SUCCESS_FUNCTION_ID))
          {
            new ServiceHome().updatePersonInformation(requestInfo, dbConn);
          }
          */
        }
        catch(Exception e)
        {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Services API Update card holder Profile-->"+e);
           throw new ProcessValuesExcep(-1,"Exception in Services API Update card holder Profile-->"+e);
        }
      }
//****************************Card To Card Transfer Function************************
      else if (requestInfo.getFunction().equalsIgnoreCase(Constants.CARD_TO_CARD_TRANSFER))
      {
        try
        {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Card To Card Transfer Function From Card -->"+requestInfo.getCardNum()+"<--- To Card -->"+requestInfo.getCardNumTo() +"<--- Amount -->"+requestInfo.getAmount());
          servicesRequestObj = mapServicesRequestInformation(requestInfo);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API selfTransfer---| ");
          servicesResponseObj = handler.selfTransfer(servicesRequestObj);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API selfTransfer---| ");
          responseObj = mapServicesResponseInformation(servicesResponseObj);
        }
        catch(Exception e)
        {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Services API selfTransfer -->"+e);
           throw new ProcessValuesExcep(-1,"Exception in Services API selfTransfer -->"+e);
        }
      }
//*****************************Replace Lost Stolen Card Function***********************
      else if (requestInfo.getFunction().equalsIgnoreCase(Constants.REPLACE_LOST_STOLEN_CARD_FUNCTION))
      {
        try
        {
          servicesRequestObj = mapServicesRequestInformation(requestInfo);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API replaceStolenCard---| ");
          servicesResponseObj = handler.replaceStolenCard(servicesRequestObj);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API replaceStolenCard---| ");
          responseObj = mapServicesResponseInformation(servicesResponseObj);
        }
        catch (Exception e)
        {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING), "Exception in Services API replaceStolenCard -->" + e);
           throw new ProcessValuesExcep( -1,"Exception in Services API replaceStolenCard -->" +e);
        }
      }
//******************************Retrieve Transactions Function****************************
      else if (requestInfo.getFunction().equalsIgnoreCase(Constants.RETERIEVE_TRANSACTION_FUNCTION))
      {
        try
        {
          servicesRequestObj = mapServicesRequestInformation(requestInfo);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API getCardMiniStatement---| ");
          servicesResponseObj = handler.getCardMiniStatement(servicesRequestObj);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API getCardMiniStatement---| ");
          responseObj = mapServicesResponseInformation(servicesResponseObj);
        }
        catch (Exception e)
        {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING), "Exception in Services API getCardMiniStatement -->" + e);
           throw new ProcessValuesExcep( -1,"Exception in Services API getCardMiniStatement -->" + e);
        }
      }
//*******************************Get ACH Information Function*******************************

      else if (requestInfo.getFunction().equalsIgnoreCase(Constants.GET_ACH_INFO_FUNCTION))
      {
        try
        {
          servicesRequestObj = mapServicesRequestInformation(requestInfo);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API getACHAccountInfo---| ");
          servicesResponseObj = handler.getACHAccountInfo(servicesRequestObj);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API getACHAccountInfo---| ");
          responseObj = mapServicesResponseInformation(servicesResponseObj);
        }
        catch (Exception e)
        {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING), "Exception in Services API getACHAccountInfo -->" + e);
          throw new ProcessValuesExcep( -1,"Exception in Services API getACHAccountInfo -->" + e);
        }
      }

//*******************************Generate ACH Function***************************************

      else if (requestInfo.getFunction().equalsIgnoreCase(Constants.ACH_GENERATE__FUNCTION))
      {
        try
        {
          servicesRequestObj = mapServicesRequestInformation(requestInfo);
          if(servicesRequestObj.getAchType() != null && servicesRequestObj.getAchType().trim().length() > 0 && servicesRequestObj.getAchType().equalsIgnoreCase("1"))
          {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API loadFunds---| ");
            servicesResponseObj = handler.loadFunds(servicesRequestObj);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API loadFunds---| ");
          }
          else if (servicesRequestObj.getAchType() != null && servicesRequestObj.getAchType().trim().length() > 0 && servicesRequestObj.getAchType().equalsIgnoreCase("2"))
          {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling Services API withdrawFunds---| ");
            servicesResponseObj = handler.withdrawFunds(servicesRequestObj);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Returned Services API withdrawFunds---| ");
          }
          responseObj = mapServicesResponseInformation(servicesResponseObj);
        }
        catch (Exception e)
        {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING), "Exception in Services API ACH_GENERATE__FUNCTION -->" + e);
          throw new ProcessValuesExcep( -1,"Exception in Services API ACH_GENERATE__FUNCTION -->" + e);
        }
      }//end else if
  if(dbConn != null)
      {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Saving the Data, Auto commiting ---| ");
        dbConn.setAutoCommit(true);
      }
    }// main try block
    catch(InvalidFieldValueExcep ifvex)
    {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in validating cards -->"+ifvex);
    }
    catch (Exception e)
    {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in sending Solspark Host the Request Message -->"+e);
      expMsg.append("\n");
      expMsg.append("\nException Message: " + e.toString());
      expMsg.append("\n\nStack Trace: " + CommonUtilities.getStackTrace(e));
      expMsg.append("\n");
    }// main catch block
    finally{
      //Get the Description of the Response Code
      if (responseObj == null || responseObj.getSwitchResponseCode() == null){
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---generating error response ---| ");
        responseObj = new ResponseInfoObj();
        responseObj.setSwitchResponseCode(Constants.ERROR_MALFUNCTION_ID);
        responseObj.setRespCode(Constants.ERROR_MALFUNCTION_ID);
        responseObj.setRespDesc(Constants.ERROR_MALFUNCTION_MESSAGE);
      }//end else

      //Getting the Information for Response Code Description
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Calling getResponseCodeDesc ---| ");
      responseObj = new ServiceHome().getResponseCodeDesc(responseObj,dbConn);
      try{
        //Close the connection
        if (dbConn != null){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---Connection Auto Commit Flag ---| " + dbConn.getAutoCommit());
          if (!dbConn.getAutoCommit()) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), " |---Some Exception Occured rolling back changes");
            dbConn.rollback();
            dbConn.setAutoCommit(true);
          }// end if check getAutoCommit
          DatabaseHandler.returnConnection(dbConn, "ServiceThread");
        }// end if dbConn not null
      }
      catch (Exception ex){}
    }//end finally
    return responseObj;
  }//end method

  /**
   * Method for mapping the information
   * @param achInfoObj: the ach API object
   * @return: response information object
   */
  /*
  public ResponseInfoObj mapInformation(AchObject achInfoObj)
      throws ProcessValuesExcep {
    ResponseInfoObj responseObj = new ResponseInfoObj();

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," MapInformationachInfoObj -->"+achInfoObj
                                      +"<--- getStatus -->"+achInfoObj.getStatus()+"<--- getAccountTitle -->"+achInfoObj.getAccountTitle()
                                      +"<--- getAccountType -->"+achInfoObj.getAccountType()+"<--- getAchType -->"+achInfoObj.getAchType()
                                      +"<--- getBankName -->"+achInfoObj.getBankName()+"<--- getRoutingNo -->"+achInfoObj.getRoutingNo());
      //Setting the Response Object Information
      responseObj.setSwitchResponseCode(achInfoObj.getStatus());
      responseObj.setAccountTitle(achInfoObj.getAccountTitle());
      responseObj.setAccountType(achInfoObj.getAccountType());
      responseObj.setAchType(achInfoObj.getAchType());
      responseObj.setBankName(achInfoObj.getBankName());
      responseObj.setRountingNum(achInfoObj.getRoutingNo());
    } catch (Exception e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Map Information -->"+e);
      throw new ProcessValuesExcep(-1,"Exception in Map Information -->"+e);
    }//end catch
    return responseObj;
  }//end method
  */

 /**
  * Method for mapping Request Object to Solspark Request Object
  * @param requestInfo RequestInfoObj
  * @throws ProcessValuesExcep
  * @return SolsparkRequestObj
  */

 public SolsparkRequestObj mapSolsparkRequestObject(RequestInfoObj requestInfo) throws ProcessValuesExcep
 {
   SolsparkRequestObj requestObj = new SolsparkRequestObj();

  try
  {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), " |---In mapSolsparkRequestObject---| ");
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), " Request Info Object -->" + requestInfo
                                    + "<--- getCardNum -->" +
                                    requestInfo.getCardNum()
                                    + "<--- getSwitchInfo -->" +
                                    requestInfo.getSwitchInfo()
                                    + "<--- Function ID -->" +
                                    Constants.UPDATE_PERSON_FUNCTION
                                    );

    requestObj.setSwitchInfo(requestInfo.getSwitchInfo());
    requestObj.setCardNum(requestInfo.getCardNum());
    requestObj.setFunctionID(Constants.UPDATE_PERSON_FUNCTION); // is it for balance
  }
  catch (Exception ex)
  {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"ServiceThread --- mapSolsparkRequestObject --- Exception in Solspark Request Map Information -->"+ex);
    throw new ProcessValuesExcep(-1,"ServiceThread --- mapSolsparkRequestObject --- Exception in Solspark Request Map Information -->"+ex);
  }

   return requestObj;
 }
 /**
    * Method for mapping the response information
    * @param achInfoObj: the ach API object
    * @return: response information object
 */

   public ResponseInfoObj mapSolsparkResponseObject(SolsparkResponseObj solsparkObj)
       throws ProcessValuesExcep {
     ResponseInfoObj responseObj = new ResponseInfoObj();

     try {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceThread --- mapSolsparkRequestObject --- In mapSolsparkResponseObject -->"+solsparkObj
                                       +"<--- getBalance -->"+solsparkObj.getBalance()+"<--- getAccountNum() -->"+solsparkObj.getAccountNum()
                                       +"<--- getRespCode -->"+solsparkObj.getRespCode()+"<--- getRespDesc -->"+solsparkObj.getRespDesc()
                                       +"<--- getRountingNum -->"+solsparkObj.getRountingNum()+"<--- getAuditNo -->"+solsparkObj.getAuditNo());

       //Setting the Response Object Information
       responseObj.setAuditNo(solsparkObj.getAuditNo());
       responseObj.setBalance(solsparkObj.getBalance());
       responseObj.setAccountNum(solsparkObj.getAccountNum());
       responseObj.setSwitchResponseCode(solsparkObj.getRespCode());
       responseObj.setRespCode(solsparkObj.getRespCode());
       responseObj.setRespDesc(solsparkObj.getRespDesc());
       responseObj.setRountingNum(solsparkObj.getRountingNum());
     } catch (Exception e) {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"ServiceThread --- mapSolsparkRequestObject --- Exception in Solspark Map Response Information -->"+e);
       throw new ProcessValuesExcep(-1,"ServiceThread --- mapSolsparkRequestObject --- Exception in Map Response Information -->"+e);
     }//end catch
     return responseObj;
   }//end method

/*

    * Method for mapping the RequestInfo Object to ServicesRequest Object
    * @param requestInfo: the RequestInfoObj object
    * @return: ServicesRequestObj object

*/

 public ServicesRequestObj mapServicesRequestInformation(RequestInfoObj requestInfo) throws ProcessValuesExcep
{
  ServicesRequestObj servicesRequestObj = new ServicesRequestObj();

  try
  {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---In mapServicesRequestInformation---| ");
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Request Info Object -->"+requestInfo
                                    + "<--- getCardNum -->" +
                                    requestInfo.getCardNum()
                                    + "<--- getAmount -->" +
                                    requestInfo.getAmount()
                                    + "<--- getCardNumTo -->" +
                                    requestInfo.getCardNumTo()
                                    + "<--- getNewCardNum -->" +
                                    requestInfo.getNewCardNum()
                                    + "<--- getMaxRecords -->" +
                                    requestInfo.getMaxRecords()
                                    + "<--- getBankAccountNum -->" +
                                    requestInfo.getBankAccountNum()
                                    + "<--- getDeviceType -->" +
                                    requestInfo.getDeviceType()
                                    + "<--- getNewPIN() -->" +
                                    requestInfo.getNewPIN()
                                    + "<--- getAac() -->" +
                                    requestInfo.getAac()
                                    + "<--- getSsn() -->" +
                                    requestInfo.getSsn()
                                    + "<--- getAccountNum() -->" +
                                    requestInfo.getAccountNum()
                                    + "<--- getExpDate() -->" +
                                    requestInfo.getExpDate()
                                    + "<--- getStreet1() -->" +
                                    requestInfo.getStreet1()
                                    + "<--- getDeviceType -->" +
                                    requestInfo.getDeviceType()
                                    + "<--- getStreet2() -->" +
                                    requestInfo.getStreet2()
                                    + "<--- getCity -->" +
                                    requestInfo.getCity()
                                    + "<--- getState -->" +
                                    requestInfo.getState()
                                    + "<--- getPostalCode -->" +
                                    requestInfo.getPostalCode()
                                    + "<--- getFirstName -->" +
                                    requestInfo.getFirstName()
                                    + "<--- getLastName -->" +
                                    requestInfo.getLastName()
                                    + "<--- getDob -->" +
                                    requestInfo.getDob()
                                    + "<--- getWorkPhone -->" +
                                    requestInfo.getWorkPhone()
                                    + "<--- getHomePhone -->" +
                                    requestInfo.getHomePhone()
                                    + "<--- getEmail -->" +
                                    requestInfo.getEmail()
                                    + "<--- getSex -->" +
                                    requestInfo.getSex()
                                    + "<--- getNewACC -->" +
                                    requestInfo.getNewACC()
                                    + "<--- getAchType -->" +
                                    requestInfo.getAchType()
                                    + "<--- getBankName -->" +
                                    requestInfo.getBankName()
                                    + "<--- getAccountTitle -->" +
                                    requestInfo.getAccountTitle()
                                    + "<--- getAccountType -->" +
                                    requestInfo.getAccountType()
                                    + "<--- getRoutingNum -->" +
                                    requestInfo.getRoutingNum()
                                    + "<--- getBankAccountNum -->" +
                                    requestInfo.getBankAccountNum()
                                    + "<--- getPin -->" +
                                    requestInfo.getPin()
                                    + "<--- getApplyFee -->" +
                                    requestInfo.getApplyFee()
                                    + "<--- getDescription -->" +
                                    requestInfo.getDescription()
                                    + "<--- getMiddleName -->" +
                                    requestInfo.getMiddleName()
                                    + "<--- getCountry -->" +
                                    requestInfo.getCountry()
                                    + "<--- getMotherMaidenName -->" +
                                    requestInfo.getMotherMaidenName()
                                    + "<--- getNickName -->" +
                                    requestInfo.getNickName()
                                    + "<--- getBankAddress -->" +
                                    requestInfo.getBankAddress()
                                    + "<--- getTransferDate -->" +
                                    requestInfo.getTransferDate()
                                    + "<--- getRetryOnFail -->" +
                                    requestInfo.getRetryOnFail()
                                    + "<--- getMaxTries -->" +
                                    requestInfo.getMaxTries()+
                                    "<--- getServiceID -->" +
                                    requestInfo.getServiceID());

//----------------------------------------------------------------------------//
    servicesRequestObj.setCardNo(requestInfo.getCardNum());
    servicesRequestObj.setAmount(requestInfo.getAmount());
    servicesRequestObj.setToCardNo(requestInfo.getCardNumTo());
    servicesRequestObj.setNewCardNo(requestInfo.getNewCardNum());
    servicesRequestObj.setNewPin(requestInfo.getNewPIN());
    servicesRequestObj.setNoOfTrans(requestInfo.getMaxRecords());
    servicesRequestObj.setAAC(requestInfo.getAac());
    servicesRequestObj.setSsn(requestInfo.getSsn());
    servicesRequestObj.setAccountNo(requestInfo.getAccountNum());
    servicesRequestObj.setExpiryDate(requestInfo.getExpDate());
    servicesRequestObj.setAddressStreet1(requestInfo.getStreet1());
    servicesRequestObj.setAddressStreet1(requestInfo.getStreet2());
    servicesRequestObj.setCity(requestInfo.getCity());
    servicesRequestObj.setStateCode(requestInfo.getState());
    servicesRequestObj.setZipCode(requestInfo.getPostalCode());
    servicesRequestObj.setFirstName(requestInfo.getFirstName());
    servicesRequestObj.setLastName(requestInfo.getLastName());
    servicesRequestObj.setDob(requestInfo.getDob());
    servicesRequestObj.setWorkPhone(requestInfo.getWorkPhone());
    servicesRequestObj.setHomePhone(requestInfo.getHomePhone());
    servicesRequestObj.setEmail(requestInfo.getEmail());
    servicesRequestObj.setGender(requestInfo.getSex());
    servicesRequestObj.setNewAAC(requestInfo.getNewACC());
    servicesRequestObj.setAchType(requestInfo.getAchType());
    servicesRequestObj.setBankName(requestInfo.getBankName());
    servicesRequestObj.setBankAcctTitle(requestInfo.getAccountTitle());
    servicesRequestObj.setBankAcctType(requestInfo.getAccountType());
    servicesRequestObj.setBankRoutingNo(requestInfo.getRoutingNum());
    servicesRequestObj.setBankAcctNo(requestInfo.getBankAccountNum());
//********************************New Attrributes Mapping*************************//
    servicesRequestObj.setPin(requestInfo.getPin());
    servicesRequestObj.setApplyFee(requestInfo.getApplyFee());
    servicesRequestObj.setDescription(requestInfo.getDescription());
    servicesRequestObj.setMiddleName(requestInfo.getMiddleName());
    servicesRequestObj.setCountry(requestInfo.getCountry());
    servicesRequestObj.setMotherMaidenName(requestInfo.getMotherMaidenName());
    servicesRequestObj.setNickName(requestInfo.getNickName());
    servicesRequestObj.setBankAddress(requestInfo.getBankAddress());
    servicesRequestObj.setTransferDate(requestInfo.getTransferDate());
    servicesRequestObj.setRetryOnFail(requestInfo.getRetryOnFail());
    servicesRequestObj.setMaxTries(requestInfo.getMaxTries());
    servicesRequestObj.setChkAmount(true);
    servicesRequestObj.setDeviceType(requestInfo.getDeviceType());
    servicesRequestObj.setServiceId(requestInfo.getServiceID());
  }
  catch(Exception e)
  {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Request Services Map Information -->"+e);
    throw new ProcessValuesExcep(-1,"Exception in Map Information -->"+e);
  }
  return servicesRequestObj;
}
/*

    * Method for mapping the ServicesResponse Object to ResponseInfo Object
    * @param servicesResponseObj: the ServicesResponseObj object
    * @return: ResponseInfoObj object

*/

public ResponseInfoObj mapServicesResponseInformation(ServicesResponseObj servicesResponseObj) throws ProcessValuesExcep
{
  ResponseInfoObj responseObj=new ResponseInfoObj();

  try
  {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," |---In mapServicesResponseInformation---| ");
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Response MapInformations SolsparkObj -->"+servicesResponseObj
                                    + "<--- getRespCode -->" +
                                    servicesResponseObj.getRespCode()
                                    + "<--- getRespDesc -->" +
                                    servicesResponseObj.getRespDesc()
                                    + "<--- getAccountNum -->" +
                                    servicesResponseObj.getAccountNo() +
                                    "<--- getBalance -->" +
                                    servicesResponseObj.getCardBalance()
                                    + "<--- getFeeAmount -->" +
                                    servicesResponseObj.getFeeAmount() +
                                    "<--- getToCardBalance -->" +
                                    servicesResponseObj.getToCardBalance()
                                    + "<--- getTransId -->" +
                                    servicesResponseObj.getTransId()
                                    + "<--- getTransactionList -->" +
                                    servicesResponseObj.getTransactionList()
                                    + "<--- getTransID -->" +
                                    servicesResponseObj.getTransId()
                                    + "<--- getSwitchTransID -->" +
                                    servicesResponseObj.getSwitchTransId()
                                    + "<--- getTransactionList() -->" +
                                    servicesResponseObj.getTransactionList()
                                    + "<--- getAchType() -->" +
                                    servicesResponseObj.getAchType()
                                    + "<--- getBankName() -->" +
                                    servicesResponseObj.getBankName()
                                    + "<--- getBankAccountTitle() -->" +
                                    servicesResponseObj.getBankAccountTitle()
                                    + "<--- getToCardBalance() -->" +
                                    servicesResponseObj.getBankAccountType()
                                    + "<--- getBankRoutingNo() -->" +
                                    servicesResponseObj.getBankRoutingNo()
                                    + "<--- getBusinessDate() -->" +
                                    servicesResponseObj.getBusinessDate()
                                    + "<--- getDescription() -->" +
                                    servicesResponseObj.getDescription()
                                    + "<--- getAchAccountNo() -->" +
                                    servicesResponseObj.getAchAccountNo()
                                    + "<--- getToCardNo() -->" +
                                    servicesResponseObj.getToCardNo()
                                    + "<--- getToCardExpDate() -->" +
                                    servicesResponseObj.getToCardExpDate()
                                    + "<--- getSwitchTransId() -->" +
                                    servicesResponseObj.getSwitchTransId()
                                    + "<--- getSwitchRoutingNo() -->" +
                                    servicesResponseObj.getSwitchRoutingNo()
                                    + "<--- getSwitchAcctNo() -->" +
                                    servicesResponseObj.getSwitchAcctNo()
                                    );
//----------------------------------------------------------------------------//
    responseObj.setRespCode(servicesResponseObj.getRespCode());
    responseObj.setSwitchResponseCode(servicesResponseObj.getRespCode());
    responseObj.setRespDesc(servicesResponseObj.getRespDesc());
    responseObj.setFeeAmount(servicesResponseObj.getFeeAmount());
    responseObj.setAccountNum(servicesResponseObj.getAccountNo());
    responseObj.setBalance(servicesResponseObj.getCardBalance());
    responseObj.setBalanceTo(servicesResponseObj.getToCardBalance());
    responseObj.setTransID(servicesResponseObj.getTransId());
    responseObj.setAuditNo(servicesResponseObj.getTransId());
    responseObj.setTransactionList(servicesResponseObj.getTransactionList());
    responseObj.setAchType(servicesResponseObj.getAchType());
    responseObj.setBankName(servicesResponseObj.getBankName());
    responseObj.setAccountTitle(servicesResponseObj.getBankAccountTitle());
    responseObj.setAccountType(servicesResponseObj.getBankAccountType());
    responseObj.setRountingNum(servicesResponseObj.getBankRoutingNo());

    responseObj.setBusinessDate(servicesResponseObj.getBusinessDate());
    responseObj.setDescription(servicesResponseObj.getDescription());
    responseObj.setAchAccountNo(servicesResponseObj.getAchAccountNo());
    responseObj.setCardNo(servicesResponseObj.getToCardNo());
    responseObj.setCardExpDate(servicesResponseObj.getToCardExpDate());
    responseObj.setSwitchTransID(servicesResponseObj.getSwitchTransId());
    responseObj.setSwitchRoutingNum(servicesResponseObj.getSwitchRoutingNo());
    responseObj.setSwitchAccountNum(servicesResponseObj.getSwitchAcctNo());
    responseObj.setServAPIExcepMsg(servicesResponseObj.getExcepMsg());
    responseObj.setServAPIStkTrace(servicesResponseObj.getStkTrace());
    responseObj.setServAPILogFilePath(servicesResponseObj.getLogFilePath());


    // For Preparing Services API response String to be used in Mail message
    try
    {
      String[] respList = CommonUtilities.convertStringArray(Constants.
          NOTIFY_RESP_CODES_VALUES, Constants.DEFAULT_DELIMETER_STRING);
      if (respList != null && responseObj != null && responseObj.getRespCode() != null) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            " Notify_Resp_Codes_Values array size-->" + respList.length +
                                        "<-- Response Code got---> " +
                                        responseObj.getRespCode());
        // checking if the reposnse code of services API is in email response code list
        if (isEmailResponseCode(respList, responseObj.getRespCode())) {
          // call method to prepare services API response
          serviceResp.append(prepareServicesAPIResponseMessage(responseObj));
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              " Services API Response String--->" + serviceResp.toString());
        }
      } //end if
    }
    catch (Exception ex)
    {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in getting Services API Response String--->" + ex);
    }
  }
  catch(Exception e)
  {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Response Services Map Information -->"+e);
    throw new ProcessValuesExcep(-1,"Exception in Map Information -->"+e);
  }
  return responseObj;
}


  /**
   * Reading the Request message from the client
   * @return: the requested String
   */
  private String readClientRequest() throws ProcessValuesExcep
  {
       char [] buf = new char[250];
       String strRequest = null;
       BufferedReader inputBuffer = null;
       try {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Before Reading Request Message");
           //Getting the Input buffer
           inputBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           //Getting the input contents
           int len = inputBuffer.read(buf);
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Input Bytes Read -->"+len);
            //Casting the request message
           strRequest = new String(buf).trim();
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Received from client:" + socket.getInetAddress() + ", data: " + strRequest);
       } catch (Exception e) {
         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceThread --- readClientRequest ---- Exception in reading request -->"+e.toString());
         throw new ProcessValuesExcep(-1,"ServiceThread --- readClientRequest ---- Exception in reading request -->" + e.toString());
       }//end catch
       return strRequest;
   }//end read client request method



   /**
    * Method for validating the request message and populate the information in the object
    * @param clientRequest: client request message
    * @return: object containing the request information
    * @throws MissingValueExcep
    * @throws WrongValueExcep
    */
   private RequestInfoObj validateRequestMessage(String clientRequest)
       throws MissingValueExcep,WrongValueExcep {
     RequestInfoObj requestInfo = null;

     //Activate Card Functiom -- 7
     //Function|Instance|SwitchInfo|CardNum|LocalDateTime|SecurityCode|ExpiryDate|AAC|Pin|AccountNumber|Amount|ApplyFee|Description

     //Replace Lost stolen Card Functiom -- 9
     //function|instance|switchInfo|cardNum|localDateTime|securityCode|newcardnumber

     //Retrieve Current Transaction -- 10
     //function|instance|switchInfo|cardNum|localDateTime|securityCode|maxrecord|pageno

     //Get Balance Function -- 0
     //function|instance|switchInfo|cardNum|localDateTime|securityCode|aacountNum

     //Get Service Fee -- 1
     //function|instance|switchInfo|cardNum|localDateTime|securityCode|serviceID

     //Reload Card Function -- 5
     //function|instance|switchInfo|cardNum|localDateTime|securityCode|aacountNum|amount

     //Update Person ---  8
     //function|instance|switchInfo|cardNum|localDateTime|securityCode|accountNum|NewAccessCode|NewPIN|FirstName|LastName|Sex|DOB|Email|SSN|Street1|Street2|city|state|postal|homephone|workphone|middleName|country|motherMaidenName

     //Card To Card Transfer ---  25
     //function|instance|switchInfo|cardNumFrom|localDateTime|securityCode|accountNumFrom|amount|cardNumTo|transDate|retryOnFail|maxTries|AccountNumTo

      //Validate Card Information -- 26
      //function|instance|switchInfo|cardNum|localDateTime|securityCode|accesscode
      //00|FirstName|MiddleName|LastName|DOB|Sex|SSN

     //Get ACH Information -- 27
     //Request Message: function|instance|switchInfo|cardNum|localDateTime|securityCode|accountNum|BankAccountNum
     //Response Message: responsecode|AchType|AccountType|RoutingNumber|AccountTitle|BankName

     //ACH Generate -- 28
     //function|instance|switchInfo|cardNum|localDateTime|securityCode|accountNum|amount|AchType|AccountType|RoutingNumber|AccountTitle|BankName|BankAccountNum|nickname|bankAddress

     // 5 digits security code
     //Empty reques message
     if ( clientRequest == null || clientRequest.trim().length() < 1 )
       throw new MissingValueExcep(-1,Constants.MESSAGE_EMPTY);

     //Tokenize the string and extract the value
     StringTokenizer st = new StringTokenizer(clientRequest,Constants.MESSAGE_DELIMETER_VALUE,true);

     int tokenNumber = 1;

     //Invaid Message
     if (st.countTokens() < 1 )
       throw new MissingValueExcep(-1,Constants.INVALID_MESSAGE + clientRequest);

     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Total Tokens: " + st.countTokens());

     //Building the Request Message
     requestInfo = new RequestInfoObj();
     boolean emptyValue = false;
     while (st.hasMoreTokens())
     {

       //Getting the token
       String value = st.nextToken();
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Token # " + tokenNumber + " : " + value);
       System.out.println("Token # " + tokenNumber + " : " + value);
       /////////////////// Function Details /////////////////////////
      //function|instance|switchInfo|cardNum|localDateTime|securityCode|accountNum|amount
       if (value != null && value.trim().length() > 0 && !value.trim().equalsIgnoreCase(Constants.MESSAGE_DELIMETER_VALUE))
       {
         if (tokenNumber == 1)
         {
           requestInfo.setFunction(value.trim());
         }
         else if (tokenNumber == 2)
         {
           requestInfo.setInstance(value.trim());
           instanceName = value.trim();
         }
         else if (tokenNumber == 3)
         {
           requestInfo.setSwitchInfo(value.trim());
         }
         else if (tokenNumber == 4)
         {
           requestInfo.setCardNum(value.trim());
         }
         else if (tokenNumber == 5)
         {
           requestInfo.setDateTime(value.trim());
         }
         else if (tokenNumber == 6)
         {
           requestInfo.setSecurityCode(value.trim());
         } //end else if

       //Validated Information
         if (requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.VALIDATED_USER_FUNCTION))
         {
           if (tokenNumber == 7)
           {
             requestInfo.setAac(value.trim());
           }//end if
         }
         // Activate Card Function
         else if(requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.ACTIVATE_CARD_FUNCTION))
         {
           if(tokenNumber == 7)
           {
             requestInfo.setExpDate(value.trim());
           }
           else if(tokenNumber == 8)
           {
             requestInfo.setAac(value.trim());
           }
           else if(tokenNumber == 9)
           {
             requestInfo.setPin(value.trim());
           }
           if(tokenNumber == 10)
           {
             requestInfo.setAccountNum(value.trim());
           }
           else if(tokenNumber == 11)
           {
             requestInfo.setAmount(value.trim());
           }
           else if(tokenNumber == 12)
           {
             requestInfo.setApplyFee(value.trim());
           }
           else if(tokenNumber == 13)
           {
             requestInfo.setDescription(value.trim());
           }
           else if(tokenNumber == 14)
           {
             requestInfo.setDeviceType(value.trim());
           }

         }
         // Card to card transfer
         else  if (requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.CARD_TO_CARD_TRANSFER))
         {
           if (tokenNumber == 7)
           {
             requestInfo.setAccountNum(value.trim());
           }

           else if (tokenNumber == 8)
           {
             requestInfo.setAmount(value.trim());
           }
           else if (tokenNumber == 9)
           {
            requestInfo.setCardNumTo(value.trim());
           }
           else if(tokenNumber == 10)
           {
             requestInfo.setAccountNumTo(value.trim());
           }
           else if(tokenNumber == 11)
           {
             requestInfo.setTransferDate(value.trim());
           }
           else if(tokenNumber == 12)
           {
             requestInfo.setRetryOnFail(value.trim());
           }
           else if(tokenNumber == 13)
           {
             requestInfo.setMaxTries(value.trim());
           }
           else if(tokenNumber == 14)
           {
             requestInfo.setExpDate(value.trim());
           }
           else if(tokenNumber == 15)
           {
             requestInfo.setAac(value.trim());
           }
           else if(tokenNumber == 16)
           {
             requestInfo.setPin(value.trim());
           }
           else if(tokenNumber == 17)
           {
             requestInfo.setApplyFee(value.trim());
           }
           else if(tokenNumber == 18)
           {
             requestInfo.setDescription(value.trim());
           }
           else if(tokenNumber == 19)
           {
             requestInfo.setDeviceType(value.trim());
           }
         }
         else if (requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.REPLACE_LOST_STOLEN_CARD_FUNCTION))
         { //Replace stolen card Function

             if (tokenNumber == 7)
             {
               requestInfo.setNewCardNum(value.trim());
             }
             else if(tokenNumber == 8)
             {
               requestInfo.setExpDate(value.trim());
             }
             else if (tokenNumber == 9) {
               requestInfo.setAac(value.trim());
             }
             else if (tokenNumber == 10) {
               requestInfo.setPin(value.trim());
             }
             if (tokenNumber == 11) {
               requestInfo.setAccountNum(value.trim());
             }
             else if (tokenNumber == 12) {
               requestInfo.setApplyFee(value.trim());
             }
             else if(tokenNumber == 13)
             {
               requestInfo.setDeviceType(value.trim());
             }
         }
         else if (requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.RETERIEVE_TRANSACTION_FUNCTION))
         { //Retrieve Transaction Function

           if (tokenNumber == 7)
           {
             requestInfo.setMaxRecords(value.trim());
           }
           else if (tokenNumber == 8) {
             requestInfo.setPageNum(value.trim());
           }
           else if(tokenNumber == 9)
           {
             requestInfo.setExpDate(value.trim());
           }
           else if (tokenNumber == 10) {
             requestInfo.setAac(value.trim());
           }
           else if (tokenNumber == 11) {
             requestInfo.setPin(value.trim());
           }
           if (tokenNumber == 12) {
             requestInfo.setAccountNum(value.trim());
           }
           else if (tokenNumber == 13) {
             requestInfo.setApplyFee(value.trim());
           }
           else if (tokenNumber == 14) {
             requestInfo.setDescription(value.trim());
           }
           else if (tokenNumber == 15) {
             requestInfo.setDeviceType(value.trim());
           }
         }
         //get balance function
         else if (requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.GET_BALANCE_FUNCTION))
         {
           if (tokenNumber == 7) {
             requestInfo.setAccountNum(value.trim());
           }

           else if(tokenNumber == 8)
           {
             requestInfo.setExpDate(value.trim());
           }
           else if (tokenNumber == 9) {
             requestInfo.setAac(value.trim());
           }
           else if (tokenNumber == 10) {
             requestInfo.setPin(value.trim());
           }
           else if (tokenNumber == 11) {
             requestInfo.setApplyFee(value.trim());
           }
           else if (tokenNumber == 12) {
             requestInfo.setDescription(value.trim());
           }
           else if (tokenNumber == 13) {
             requestInfo.setDeviceType(value.trim());
           }
         }
         // get service fee function
         else if(requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.GET_SERVICE_FEE_FUNCTION))
         {
           if (tokenNumber == 7)
           {
             requestInfo.setServiceID(value.trim());
           }else if(tokenNumber == 8){
             requestInfo.setCardNumTo(value.trim());
           }else if(tokenNumber == 9){
             requestInfo.setDeviceType(value.trim());
           }
         }
         else if(requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.GET_C2C_PARAMETER_VALUES))
         {
           if(tokenNumber == 7){
             requestInfo.setDeviceType(value.trim());
           }
         }

         // reload card function
         else if (requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.RELOAD_CARD_FUNCTION))
         {
           if (tokenNumber == 7) {
             requestInfo.setAccountNum(value.trim());
           }
           else if (tokenNumber == 8) {
             requestInfo.setAmount(value.trim());
           }
           else if(tokenNumber == 9)
           {
             requestInfo.setExpDate(value.trim());
           }
           else if (tokenNumber == 10) {
             requestInfo.setAac(value.trim());
           }
           else if (tokenNumber == 11) {
             requestInfo.setPin(value.trim());
           }
           else if (tokenNumber == 12) {
             requestInfo.setApplyFee(value.trim());
           }
           else if (tokenNumber == 13) {
             requestInfo.setDescription(value.trim());
           }
           else if (tokenNumber == 14) {
             requestInfo.setDeviceType(value.trim());
           }
         }
         //Update Person Profile
        else if (requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.UPDATE_PERSON_FUNCTION)) { //Update Person Information Function

          if (tokenNumber == 7) {
            requestInfo.setAccountNum(value.trim());
          }
          else if (tokenNumber == 8)
          {
            requestInfo.setNewACC(value.trim());
          }
          else if (tokenNumber == 9) {
            requestInfo.setNewPIN(value.trim());
          }
          else if (tokenNumber == 10)
          {
            requestInfo.setFirstName(value.trim());
          }
          else if (tokenNumber == 11)
          {
            requestInfo.setLastName(value.trim());
          }
          else if (tokenNumber == 12) {
            requestInfo.setSex(value.trim());
          }
          else if (tokenNumber == 13)
          {
            requestInfo.setDob(value.trim());
          }

          else if (tokenNumber == 14) {
            requestInfo.setEmail(value.trim());
          }
          else if (tokenNumber == 15)
          {
            requestInfo.setSsn(value.trim());
          }
          else if (tokenNumber == 16)
           {
             requestInfo.setStreet1(value.trim());
           }
           else if (tokenNumber == 17)
           {
             requestInfo.setStreet2(value.trim());
           }
           else if (tokenNumber == 18)
           {
             requestInfo.setCity(value.trim());
           }
           else if (tokenNumber == 19)
           {
             requestInfo.setState(value.trim());
           }
           else if (tokenNumber == 20)
           {
             requestInfo.setPostalCode(value.trim());
           }
           else if (tokenNumber == 21)
           {
             requestInfo.setHomePhone(value.trim());
           }
           else if (tokenNumber == 22) {
             requestInfo.setWorkPhone(value.trim());
           }
           else if (tokenNumber == 23)
           {
             requestInfo.setCountry(value.trim());
           }
           else if (tokenNumber == 24)
           {
             requestInfo.setMotherMaidenName(value.trim());
           }

           else if (tokenNumber == 25)
           {
             requestInfo.setMiddleName(value.trim());
           }
           else if(tokenNumber == 26)
           {
             requestInfo.setExpDate(value.trim());
           }
           else if (tokenNumber == 27) {
             requestInfo.setAac(value.trim());
           }
           else if (tokenNumber == 28) {
             requestInfo.setPin(value.trim());
           }
           else if (tokenNumber == 29) {
             requestInfo.setApplyFee(value.trim());
           }
           else if (tokenNumber == 30) {
             requestInfo.setDeviceType(value.trim());
           }
         }
         // get ACH Info
         else if(requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.GET_ACH_INFO_FUNCTION))
         {
           if (tokenNumber == 7) {
              requestInfo.setAccountNum(value.trim());
           } else if(tokenNumber == 8){
            requestInfo.setBankAccountNum(value.trim());
           }
           else if (tokenNumber == 9) {
             requestInfo.setDeviceType(value.trim());
           }
         }
         //Generate ACH Function
         else  if (requestInfo.getFunction() != null && requestInfo.getFunction().equalsIgnoreCase(Constants.ACH_GENERATE__FUNCTION)) { //Generate ACH Function
           if (tokenNumber == 7)
           {
              requestInfo.setAccountNum(value.trim());
           }
           else if (tokenNumber == 8)
           {
               requestInfo.setAmount(value.trim());
           }
           else if (tokenNumber == 9)
           {
               requestInfo.setAchType(value.trim());
           }
           else if (tokenNumber == 10)
           {
               requestInfo.setAccountType(value.trim());
           }
           else if (tokenNumber == 11)
           {
               requestInfo.setRoutingNum(value.trim());
           }
           else if (tokenNumber == 12)
           {
               requestInfo.setAccountTitle(value.trim());
           }
           else if (tokenNumber == 13)
           {
               requestInfo.setBankName(value.trim());
           }

           else if (tokenNumber == 14)
           {
                requestInfo.setBankAccountNum(value.trim());
           }

           else if (tokenNumber == 15)
           {
              requestInfo.setNickName(value.trim());
           }
           else if (tokenNumber == 16)
           {
             requestInfo.setBankAddress(value.trim());
           }
           else if(tokenNumber == 17)
           {
             requestInfo.setExpDate(value.trim());
           }
           else if (tokenNumber == 18)
           {
             requestInfo.setAac(value.trim());
           }
           else if (tokenNumber == 19)
           {
             requestInfo.setPin(value.trim());
           }
           else if (tokenNumber == 20)
           {
             requestInfo.setApplyFee(value.trim());
           }
           else if (tokenNumber == 21)
           {
             requestInfo.setDescription(value.trim());
           }
           else if (tokenNumber == 22)
           {
             requestInfo.setDeviceType(value.trim());
           }
         }
           tokenNumber++;
           emptyValue = false;
        } else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Else Condition emptyValue -->"+emptyValue+"<--tokenNumber->"+tokenNumber+"<--value -->"+value);
          if (emptyValue)
            tokenNumber++;
          if (value != null && value.trim().length() > 0 && value.trim().equalsIgnoreCase(Constants.MESSAGE_DELIMETER_VALUE))
            emptyValue  = true;
        }
      }//end while
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Populated Request Information Object -->"+requestInfo);
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," requestInfo.getFunction() -->"+requestInfo.getFunction()+"<-->"+" requestInfo.getSwitch() -->"+requestInfo.getSwitchInfo()+"<-->"+
     " requestInfo.getInstance() -->"+requestInfo.getInstance()+"<-->"+" requestInfo.getCardNum() -->"+requestInfo.getCardNum()+"<-->"+
     " requestInfo.getSecurityCode() -->"+requestInfo.getSecurityCode()+"<-->"+" requestInfo.getDateTime() -->"+requestInfo.getDateTime()+"<-->"+
     " requestInfo.getAccountNum() -->"+requestInfo.getAccountNum()+"<-->"+" requestInfo.getAmount() -->"+requestInfo.getAmount()+"<-->"+
     " requestInfo.getNewAccessCode() -->"+requestInfo.getNewACC()+"<-->"+" requestInfo.getNewPIN() -->"+requestInfo.getNewPIN()+"<-->"+
     " requestInfo.getFirstName() -->"+requestInfo.getFirstName()+"<-->"+" requestInfo.getLastName() -->"+requestInfo.getLastName()+"<-->"+
     " requestInfo.getDob() -->"+requestInfo.getDob()+"<-->"+" requestInfo.getEmail() -->"+requestInfo.getEmail()+"<-->"+
     " requestInfo.getHomePhone() -->"+requestInfo.getHomePhone()+"<-->"+" requestInfo.getWorkphone() -->"+requestInfo.getWorkPhone()+"<-->"+
     " requestInfo.getPostal() -->"+requestInfo.getPostalCode()+"<-->"+" requestInfo.getSsn() -->"+requestInfo.getSsn()+"<-->"+
     " requestInfo.getSex() -->"+requestInfo.getSex()+"<-->"+" requestInfo.getState() -->"+requestInfo.getState()+"<-->"+
     " requestInfo.getStreet1() -->"+requestInfo.getStreet1()+"<-->"+" requestInfo.getStreet2() -->"+requestInfo.getStreet2()+"<-->"+
     " requestInfo.getSsn() -->"+requestInfo.getSsn()+"<-->"+" requestInfo.getCardNumTo() -->"+requestInfo.getCardNumTo()+"<-->"+
     " requestInfo.getNewCardNum() -->"+requestInfo.getNewCardNum()+"<-->"+" requestInfo.getMaxRecords() -->"+requestInfo.getMaxRecords()+"<-->"+
     " requestInfo.getPageNum() -->"+requestInfo.getPageNum()+"<-->"+" requestInfo.getMaxRecords() -->"+requestInfo.getMaxRecords()+"<-->"+
     " requestInfo.getAccountNumTo() -->"+requestInfo.getAccountNumTo()+"<-->"+" requestInfo.getCity() -->"+requestInfo.getCity()+" requestInfo.getAccessCode() -->"+requestInfo.getAac()+
     "<-- requestInfo.getAchType() -->"+requestInfo.getAchType()+"<-->"+" requestInfo.getAccountType() -->"+requestInfo.getAccountType()+"<-->"+
     "<-- requestInfo.getRoutingNum() -->"+requestInfo.getRoutingNum()+"<-->"+" requestInfo.getAccountTitle() -->"+requestInfo.getAccountTitle()+"<-->"+
     "<-- requestInfo.getBankName() -->"+requestInfo.getBankName()+"<-- requestInfo.getBankAccountNum() -->"+requestInfo.getBankAccountNum()+
     "<-- requestInfo.getDeviceType() -->"+requestInfo.getDeviceType()+"<-- requestInfo.getServiceID() -->"+requestInfo.getServiceID());


      //function|instance|switchInfo|cardNum|localDateTime|securityCode|accountNum|amount
      //If required fields are missing
      if (requestInfo.getFunction() == null || requestInfo.getInstance() == null || requestInfo.getCardNum() == null
          || requestInfo.getSecurityCode() == null || requestInfo.getDateTime() == null)
        throw new MissingValueExcep(-1,Constants.REQUIRED_PARAMETER_MISSING);
      else {
        //Checking the function to be valid
        if (!requestInfo.getFunction().equalsIgnoreCase(Constants.GET_BALANCE_FUNCTION)
            && !requestInfo.getFunction().equalsIgnoreCase(Constants.GET_SERVICE_FEE_FUNCTION)
            && !requestInfo.getFunction().equalsIgnoreCase(Constants.GET_C2C_PARAMETER_VALUES)
            && !requestInfo.getFunction().equalsIgnoreCase(Constants.ACTIVATE_CARD_FUNCTION)
            && !requestInfo.getFunction().equalsIgnoreCase(Constants.RELOAD_CARD_FUNCTION)
            && !requestInfo.getFunction().equalsIgnoreCase(Constants.CARD_TO_CARD_TRANSFER)
            && !requestInfo.getFunction().equalsIgnoreCase(Constants.VALIDATED_USER_FUNCTION)
            && !requestInfo.getFunction().equalsIgnoreCase(Constants.REPLACE_LOST_STOLEN_CARD_FUNCTION)
            && !requestInfo.getFunction().equalsIgnoreCase(Constants.RETERIEVE_TRANSACTION_FUNCTION)
            && !requestInfo.getFunction().equalsIgnoreCase(Constants.GET_ACH_INFO_FUNCTION)
            && !requestInfo.getFunction().equalsIgnoreCase(Constants.ACH_GENERATE__FUNCTION)
            && !requestInfo.getFunction().equalsIgnoreCase(Constants.UPDATE_PERSON_FUNCTION)
            )

          throw new WrongValueExcep(-1,"Invalid Function -->"+requestInfo.getFunction());

        //Checking the function to be valid and its required parameters are present
        if (requestInfo.getFunction().equalsIgnoreCase(Constants.RELOAD_CARD_FUNCTION)
            && requestInfo.getAmount() == null)
          throw new MissingValueExcep(-1,Constants.REQUIRED_PARAMETER_MISSING);

        //Checking the function to be valid and its required parameters are present
        if (requestInfo.getFunction().equalsIgnoreCase(Constants.GET_SERVICE_FEE_FUNCTION)
            && requestInfo.getServiceID() == null)
          throw new MissingValueExcep(-1,Constants.REQUIRED_PARAMETER_MISSING);


        //Checking the function to be valid and its required parameters are present
        if (requestInfo.getFunction().equalsIgnoreCase(Constants.RETERIEVE_TRANSACTION_FUNCTION)
            && (requestInfo.getMaxRecords() == null))
          throw new MissingValueExcep(-1,Constants.REQUIRED_PARAMETER_MISSING);

        //Checking the function to be valid and its required parameters are present
        if (requestInfo.getFunction().equalsIgnoreCase(Constants.GET_ACH_INFO_FUNCTION)
            && (requestInfo.getAccountNum() == null || requestInfo.getBankAccountNum() == null))
          throw new MissingValueExcep(-1,Constants.REQUIRED_PARAMETER_MISSING);

          //Checking the function to be valid and its required parameters are present
        if (requestInfo.getFunction().equalsIgnoreCase(Constants.ACH_GENERATE__FUNCTION)
            && (requestInfo.getAccountNum() == null || requestInfo.getAmount() == null
                || requestInfo.getAchType() == null || requestInfo.getAccountType() == null
                || requestInfo.getRoutingNum() == null || requestInfo.getBankAccountNum() == null))
          throw new MissingValueExcep(-1,Constants.REQUIRED_PARAMETER_MISSING);

        //Checking the function to be valid and its required parameters are present
        if (requestInfo.getFunction().equalsIgnoreCase(Constants.CARD_TO_CARD_TRANSFER)
            && (requestInfo.getAmount() == null || requestInfo.getCardNumTo() == null))
          throw new MissingValueExcep(-1,Constants.REQUIRED_PARAMETER_MISSING);

        //Checking the function to be valid and its required parameters are present
        if (requestInfo.getFunction().equalsIgnoreCase(Constants.GET_SERVICE_FEE_FUNCTION) && requestInfo.getServiceID().equalsIgnoreCase(Constants.CARD_TO_CARD_ID)
             && requestInfo.getCardNumTo() == null)
          throw new MissingValueExcep(-1,Constants.REQUIRED_PARAMETER_MISSING);

        //Checking the Valid Security Code
        try
        {
          if (!checkValidSecurityCode(requestInfo.getDateTime(),requestInfo.getSecurityCode(), 5)) {
            throw new WrongValueExcep( -1, "Unable to verfiy secutiry code--->" + Constants.INVALID_MESSAGE);
          }
        }
        catch (Exception ex)
        {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in validating the Security code -->"+ex);
          throw new WrongValueExcep( -1, Constants.INVALID_MESSAGE);
        }
      }//end else if
      return requestInfo;
   }//end compare method

   /**
    * Method for calculating the Security Code for the Date Time stamp
    * @param datestamp: string whose security code to be calculated
    * @return: the calculated Securty Code
    */
   private long calcSecurityCode(String datestamp) {
     long securityCode = 0;
     try {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Method for calculating security code -->"+datestamp);
       for ( int i=0; i < datestamp.length() ;++i) {
         securityCode += Long.parseLong(datestamp.charAt(i)+"");
       }//end for
     } catch (Exception e){
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Exception in calulating the Security code -->"+e);
     }//end method
     return securityCode;
   }//end method

   /**
    * Method for checking the Security Code for the Date Time stamp
    * @param datestamp: string whose security code to be calculated
    * @return: the calculated Securty Code
    */
   private boolean checkValidSecurityCode(String datestamp,String securityCode,int securityCodelength) throws ProcessValuesExcep{
     boolean securityCodeValid = false;
     try {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Method for validating the Security Code -->"+securityCode+"<--- Length -->"+securityCode.length());
       //Comparing the Security Codes
       if (securityCode.length() ==  securityCodelength
           && calcSecurityCode(datestamp) == Long.parseLong(securityCode))
         securityCodeValid =  true;
     } catch (Exception e){
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Exception in validating the Security code -->"+e);
       throw new ProcessValuesExcep(-1," Exception in validating the Security code -->"+e);
     }//end method
     return securityCodeValid;
   }//end method

   /**
    * Method for sending the reponse message to the client socket
    * @param response: response message to be send
    * @return: able to successfully send the message or not
    */
   protected boolean sendClientResponse(String request, String response, RequestInfoObj requestObj) throws ProcessValuesExcep{
     boolean sendMessageFlag = true;
     try {
       //Preparing email message body
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceThread ---- sendClientResponse ---- Client Request String ---> "+ request + " <--- Response--->" + response + " <--- Instance Name--->" + instanceName);

       if(instanceName != null && instanceName.trim().length() > 0 && LoadProperties.instanceConnectionsTable.containsKey(instanceName))
         mailMsg.append("\n\nInstance Name: " + instanceName);
       else
         mailMsg.append("\n\nInstance Name: N/A");

       if(request != null && request.trim().length() > 0){
         StringTokenizer st = new StringTokenizer(request,Constants.MESSAGE_DELIMETER_VALUE,true);
         StringBuffer tempRequest = new StringBuffer();
        try {
          Vector cardProgRs = null;
          Connection dbConn = null;
          String cardPrgName = null;
          String cardPrgId = null;
          try{
            dbConn = DatabaseHandler.getConnection("ServiceThread",instanceName);
            cardProgRs = new ServiceHome().getCardPrgInfo(requestObj.getCardNum(),dbConn);

            if(cardProgRs.size() > 0){
              LabelValueBean lvb = (LabelValueBean) cardProgRs.elementAt(0);
              if(lvb.getLabel() != null && lvb.getLabel().trim().length() > 0)
                cardPrgName = lvb.getLabel().trim();
              if(lvb.getValue() != null && lvb.getValue().trim().length() > 0)
                cardPrgId = lvb.getValue().trim();
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceThread ---- sendClientResponse ---- Card Program Information --- Card Prg Name ---> "+ cardPrgName + " <--- Card Prg ID--->" + cardPrgId);
            }
          }
          catch (Exception ex1){
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceThread ---- sendClientResponse ---- Exception in getting card program information--->" + ex1);
          }
          finally{
              try{
                if (dbConn != null){
                  DatabaseHandler.returnConnection(dbConn, "ServiceThread");
                }
              }
              catch (Exception ex2){}
          }// end finally
          while (st.hasMoreElements()) {
            String value = st.nextToken();
            if(value != null && !value.trim().equalsIgnoreCase(Constants.MESSAGE_DELIMETER_VALUE)
                               &&(value.trim().equalsIgnoreCase(requestObj.getCardNum())
                                 ||value.trim().equalsIgnoreCase(requestObj.getAccountNum())
                                 ||value.trim().equalsIgnoreCase(requestObj.getAac())
                                 ||value.trim().equalsIgnoreCase(requestObj.getPin())
                                 ||value.trim().equalsIgnoreCase(requestObj.getNewACC())
                                 ||value.trim().equalsIgnoreCase(requestObj.getNewPIN())
                                 ||value.trim().equalsIgnoreCase(requestObj.getNewCardNum())
                                 ||value.trim().equalsIgnoreCase(requestObj.getAccountNumTo())
                                 ||value.trim().equalsIgnoreCase(requestObj.getCardNumTo())
                                 ||value.trim().equalsIgnoreCase(requestObj.getBankAccountNum())
                                ))
              {
                //call function to mask information
                value = CommonUtilities.maskInformation(value.trim(),cardPrgName,cardPrgId);
              }
              tempRequest.append(value);
          } //end while
          if((tempRequest.charAt(tempRequest.length()-1)+"").equalsIgnoreCase(Constants.MESSAGE_DELIMETER_VALUE)){
            tempRequest.deleteCharAt(tempRequest.length()-1);
          }
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Temp Request--->" + tempRequest.toString());
          mailMsg.append("\n\nRequest Message: " + tempRequest.toString());
        }//end try
        catch (Exception ex){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING), "Exception in preparing request message for email notifiction--->" + ex);
          mailMsg.append("\n\nRequest Message: N/A");
        }
       }// end if request
       else
         mailMsg.append("\n\nRequest Message: N/A");

       if(serviceResp != null && serviceResp.length() > 0)
         mailMsg.append("\n\nServices API Response Message: " + serviceResp.toString());
       else
         mailMsg.append("\n\nServices API Response Message: N/A");

       if(response != null && response.trim().length() > 0)
         mailMsg.append("\n\nResponse Message: " + response);
       else
         mailMsg.append("\n\nResponse Message: N/A");

       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Mail Message Build so far---->" + mailMsg);
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Method for send Client Response -->"+response);
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Response Length -->"+response.length());
       if (socket != null) {
         socket.setSoTimeout(Constants.DEFAULT_TIME_OUT);
         Writer pw = new java.io.OutputStreamWriter(socket.getOutputStream());
         pw.write(response);
         pw.flush();
         socket.close();
       }//end if
     } catch (Exception e) {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Exception in sending response message -->"+e);
       sendMessageFlag = false;
       throw new ProcessValuesExcep(-1," Exception in sending response message -->"+e);
     }//end catch
     return sendMessageFlag;
   }//end send Client Resonse method

   // This method deals with sending client response in case of invalid client ip
   protected boolean sendClientResponse(String response) throws ProcessValuesExcep{
     boolean sendMessageFlag = true;
     try {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Method for send Client Response -->"+response);
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Response Length -->"+response.length());
       if (socket != null) {
         socket.setSoTimeout(Constants.DEFAULT_TIME_OUT);
         Writer pw = new java.io.OutputStreamWriter(socket.getOutputStream());
         pw.write(response);
         pw.flush();
         socket.close();
       }//end if
     } catch (Exception e) {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Exception in sending response message -->"+e);
       sendMessageFlag = false;
       throw new ProcessValuesExcep(-1," Exception in sending response message -->"+e);
     }//end catch
     return sendMessageFlag;
   }//end send Client Resonse method


   /**
    * Method for preparing the response string message to the client
    * @param responseObj: object containing the response information
    * @return: the response message to be send
    * @throws ProcessValuesExcep
    */
   private String prepareClientResponse(ResponseInfoObj responseObj, RequestInfoObj requestObj)
       throws ProcessValuesExcep {
     StringBuffer response = new StringBuffer();
     try {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Preparing Client Response responseObj -->"+responseObj+"<--- Response Code -->"+responseObj.getRespCode()
                                         +"<--- Response Description--->"+responseObj.getRespDesc()+"<-- Fee -->"+responseObj.getFeeAmount()+"<--- Balance -->"+responseObj.getBalance()
                                         +"<--- getAuditNo --->"+responseObj.getAuditNo()+"<-- getTransID -->"+responseObj.getTransID()+"<--- getRountingNum -->"+responseObj.getRountingNum()
                                         +"<--- isError --->"+responseObj.isError());

         //Getting the Response Code
         if (responseObj.getRespCode() != null)
         {
           response.append(responseObj.getRespCode());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
         else
         {
           throw new ProcessValuesExcep(-1,"No Response Code information found ");
         }//end else

         //Getting the Response Descritpion
         if (responseObj.getRespDesc() != null)
         {
           response.append(responseObj.getRespDesc());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }//end if

         //Getting the Audit Number
         if (responseObj.getAuditNo() != null)
         {
           response.append(responseObj.getAuditNo());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }

         //Getting the Routing Number
         if (responseObj.getSwitchRoutingNum() != null)
         {
          response.append(responseObj.getSwitchRoutingNum());
          response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }

        //Getting the Account Number
        if (responseObj.getSwitchAccountNum() != null) {
          response.append(responseObj.getSwitchAccountNum());
          response.append(Constants.MESSAGE_DELIMETER_VALUE);
        }
        // Card To Card Transfer
       if(responseObj.getBalanceTo() != null && responseObj.getCardNo() == null)
       {
         // Getting Balance From
         if (responseObj.getBalance() != null)
         {
           response.append(responseObj.getBalance());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
         // Getting the Balance To
         if (responseObj.getBalanceTo() != null)
         {
           response.append(responseObj.getBalanceTo());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
         // Getting Fee Charges
         if (responseObj.getFeeAmount() != null)
         {
           response.append(responseObj.getFeeAmount());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
       }
       // Replace Lost Stolen Card
       else if(responseObj.getBalanceTo() != null && responseObj.getCardNo() != null)
       {
         //Getting the Fee Charges
         if (responseObj.getFeeAmount() != null) {
           response.append(responseObj.getFeeAmount());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
         // Getting the Balance To
         if (responseObj.getBalanceTo() != null) {
           response.append(responseObj.getBalanceTo());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
         //Getting New Card Number
         if (responseObj.getCardNo() != null) {
           response.append(responseObj.getCardNo());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }

         // Getting Card Account Number
         if (responseObj.getAccountNum() != null) {
           response.append(responseObj.getAccountNum());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }

         //Getting Expiry Date
         if (responseObj.getCardExpDate() != null) {
           response.append(responseObj.getCardExpDate());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
       }
       // Generate ACH Function
       else if(responseObj.getAchAccountNo() != null)
       {
         //Getting the Balance
         if (responseObj.getBalance() != null) {
           response.append(responseObj.getBalance());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
         //Getting the Fee Charges
         if (responseObj.getFeeAmount() != null) {
           response.append(responseObj.getFeeAmount());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
         //Getting ACH Account Number
         if (responseObj.getAchAccountNo() != null) {
           response.append(responseObj.getAchAccountNo());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
       }
         // All Other Functions
       else
       {
         //Getting the Fee Charges
         if (responseObj.getFeeAmount() != null) {
           response.append(responseObj.getFeeAmount());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
         //System.out.println("Yes");
         //Getting the Balance
         if (responseObj.getBalance() != null) {
           response.append(responseObj.getBalance());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }
         else if((responseObj.getBalance() == null || responseObj.getBalance().trim().length() == 0) && requestObj.getFunction().equalsIgnoreCase(Constants.RETERIEVE_TRANSACTION_FUNCTION))
         {
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
         }// especial check for null balance in case of Mini statement
       }

       //Getting the Switch TransID To
       if (responseObj.getSwitchTransID() != null)
       {
         response.append(responseObj.getSwitchTransID());
         response.append(Constants.MESSAGE_DELIMETER_VALUE);
       }

         //Getting the Transaction Information
//         System.out.println("SIZE:   --->" + responseObj.getTransactionList().size());
         if (responseObj.getTransactionList() != null && responseObj.getTransactionList().size() > 0)
         {
           response.append(responseObj.getTransactionList().size());
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
           for (int i = 0; i < responseObj.getTransactionList().size(); i++)
           {
             com.i2c.services.TransactionObj infoObj = (com.i2c.services.TransactionObj)responseObj.getTransactionList().elementAt(i);

             if (infoObj.getTransDate() != null)
              response.append(infoObj.getTransDate());

             response.append(Constants.MESSAGE_DELIMETER_VALUE);
//             response.append(Constants.MESSAGE_DELIMETER_VALUE+"{4}");

             if (infoObj.getAmount() != null)
               response.append(infoObj.getAmount());

             response.append(Constants.MESSAGE_DELIMETER_VALUE);
//            response.append(Constants.MESSAGE_DELIMETER_VALUE+"{7}");


             if (infoObj.getTransId() != null)
               response.append(infoObj.getTransId());

             response.append(Constants.MESSAGE_DELIMETER_VALUE);
//             response.append(Constants.MESSAGE_DELIMETER_VALUE+"{1}");

             if (infoObj.getAccountNo() != null)
              response.append(infoObj.getAccountNo());

             response.append(Constants.MESSAGE_DELIMETER_VALUE);
//             response.append(Constants.MESSAGE_DELIMETER_VALUE+"{2}");

             if(infoObj.getTransTypeId()!= null)
               response.append(infoObj.getTransTypeId());

             response.append(Constants.MESSAGE_DELIMETER_VALUE);
//             response.append(Constants.MESSAGE_DELIMETER_VALUE+"{3}");


            if(infoObj.getBusinessDate()!= null)
               response.append(infoObj.getBusinessDate());

            response.append(Constants.MESSAGE_DELIMETER_VALUE);
//            response.append(Constants.MESSAGE_DELIMETER_VALUE+"{5}");

            if(infoObj.getAccpNameAndLoc()!= null)
               response.append(infoObj.getAccpNameAndLoc());

            response.append(Constants.MESSAGE_DELIMETER_VALUE);
//            response.append(Constants.MESSAGE_DELIMETER_VALUE+"{6}");


            if(infoObj.getDescription()!= null)
               response.append(infoObj.getDescription().trim());
            response.append(Constants.MESSAGE_DELIMETER_VALUE);
//          response.append(Constants.MESSAGE_DELIMETER_VALUE+"{8}");
//            response.append("#$#");
           }//end for
         }//end if

         //Remove the Last pipe sign character
         if ((response.charAt(response.length() - 1 ) + "").equalsIgnoreCase(Constants.MESSAGE_DELIMETER_VALUE))
           response.deleteCharAt(response.length() - 1);

       } catch (Exception e) {
         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in preparing response message -->"+e);
         throw new ProcessValuesExcep(-1,"Unable to prepare Client Response -->"+e);
       }//end catch
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Final Response Message build -->"+response);

       //Checking the Response length
       if (response.length() < 1)
         throw new ProcessValuesExcep(-1,"Unable to prepare Client Response ");
       return response.toString();
   }//end send Client Resonse method

   private String prepareServicesAPIResponseMessage(ResponseInfoObj responseObj)
   {
       StringBuffer response = new StringBuffer();
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareServicesAPIResponseMessage ---");
       if (responseObj.getRespCode() != null)
       {
         response.append(responseObj.getRespCode().trim());
         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareServicesAPIResponseMessage --- responseObj.getRespCode()" + responseObj.getRespCode());
         //Getting the Response Descritpion
         if (responseObj.getRespDesc() != null)
         {
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
           response.append(responseObj.getRespDesc().trim());
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareServicesAPIResponseMessage --- responseObj.getRespDesc()" + responseObj.getRespDesc());
         }//end if
         if (responseObj.getAuditNo() != null)
         {
           response.append(Constants.MESSAGE_DELIMETER_VALUE);
           response.append(responseObj.getAuditNo().trim());
         }
         if (responseObj.getServAPIExcepMsg() != null) {
           response.append("\n\nServices API Exception Message: ");
           response.append(responseObj.getServAPIExcepMsg().trim());
         }
         if (responseObj.getServAPIStkTrace() != null) {
           response.append("\n\nServices API Stack Trace: ");
           response.append(responseObj.getServAPIStkTrace().trim());
         }
         if (responseObj.getServAPILogFilePath() != null) {
           response.append("\n\nServices API Log File Path: ");
           response.append(responseObj.getServAPILogFilePath().trim());
         }

       }
       return response.toString();
   }

   private boolean isEmailResponseCode(String[] emailRespCodeList, String respCode)
   {
     boolean flag = false;
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," isEmailResponseCode  ----  emailRespCodeList Length-->" + emailRespCodeList.length + " <--- Response Code--->" + respCode);
     for (int i = 0; i < emailRespCodeList.length ; i++) {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Notify_Resp_Codes_Value at -->" + i + " INDEX-->" + emailRespCodeList[i] + "<-- Response Code got---> " + respCode);
           if(respCode.trim().equalsIgnoreCase(emailRespCodeList[i].trim())) {
             CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Rsponse Code value is within the Notify_Resp_Codes_Values [Mail should be sent for this response code] ---->" + emailRespCodeList[i] + "<-- Response Code got---> " + respCode);
             flag = true;
             break;
           }//end  if
         }//end for
     return flag;
   }

//   private String prepareClientResponseActivateCardFunction(ResponseInfoObj responseObj) throws ProcessValuesExcep
//   {
//     StringBuffer response = new StringBuffer();
//     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction ---");
//     // ----------  RespCode|RespDesc|AuditNo|Fee|Balance ------------ //
//     try
//     {
//       if (responseObj.getRespCode() != null)
//       {
//         response.append(responseObj.getRespCode());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction --- responseObj.getRespCode()" + responseObj.getRespCode());
//       }
//       else
//       {
//           throw new ProcessValuesExcep(-1,"No Response Code information found ");
//       }//end else
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Response Descritpion
//       if (responseObj.getRespDesc() != null)
//       {
//         response.append(responseObj.getRespDesc());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction --- responseObj.getRespDesc()" + responseObj.getRespDesc());
//       }//end if
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Audit Number
//       if (responseObj.getAuditNo() != null)
//       {
//         response.append(responseObj.getAuditNo());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction --- responseObj.getAuditNo()" + responseObj.getAuditNo());
//       }
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Fee Charges
//       if (responseObj.getFeeAmount() != null)
//       {
//         response.append(responseObj.getFeeAmount());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction --- responseObj.getFeeAmount()" + responseObj.getFeeAmount());
//       }
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Balance
//       if (responseObj.getBalance() != null)
//       {
//         response.append(responseObj.getBalance());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction --- responseObj.getBalance()" + responseObj.getBalance());
//       }
//     }
//     catch (Exception e)
//     {
//       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in preparing response message for Activate Card Function -->"+e);
//       throw new ProcessValuesExcep(-1,"Unable to prepare Client Response for Activate Card Function -->"+e);
//     }//end catch
//     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Final Response Message build for Activate Card Function -->"+response);
//
//     //Checking the Response length
//     if (response.length() < 1)
//       throw new ProcessValuesExcep(-1,"Unable to prepare Client Response ");
//     return response.toString();
//   }
//   private String prepareClientResponseGetBalanceFunction(ResponseInfoObj responseObj) throws ProcessValuesExcep
//   {
//     StringBuffer response = new StringBuffer();
//     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseGetBalanceFunction ---");
//     // ----------  RespCode|RespDesc|AuditNo|Fee|Balance ------------ //
//     try
//     {
//       if (responseObj.getRespCode() != null)
//       {
//         response.append(responseObj.getRespCode());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseGetBalanceFunction --- responseObj.getRespCode()" + responseObj.getRespCode());
//       }
//       else
//       {
//           throw new ProcessValuesExcep(-1,"No Response Code information found ");
//       }//end else
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Response Descritpion
//       if (responseObj.getRespDesc() != null)
//       {
//         response.append(responseObj.getRespDesc());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseGetBalanceFunction --- responseObj.getRespDesc()" + responseObj.getRespDesc());
//       }//end if
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Audit Number
//       if (responseObj.getAuditNo() != null)
//       {
//         response.append(responseObj.getAuditNo());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseGetBalanceFunction --- responseObj.getAuditNo()" + responseObj.getAuditNo());
//       }
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Fee Charges
//       if (responseObj.getFeeAmount() != null)
//       {
//         response.append(responseObj.getFeeAmount());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseGetBalanceFunction --- responseObj.getFeeAmount()" + responseObj.getFeeAmount());
//       }
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Balance
//       if (responseObj.getBalance() != null)
//       {
//         response.append(responseObj.getBalance());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseGetBalanceFunction --- responseObj.getBalance()" + responseObj.getBalance());
//       }
//     }
//     catch (Exception e)
//     {
//       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in preparing response message for Activate Card Function -->"+e);
//       throw new ProcessValuesExcep(-1,"Unable to prepare Client Response for Activate Card Function -->"+e);
//     }//end catch
//     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Final Response Message build for Activate Card Function -->"+response);
//
//     //Checking the Response length
//     if (response.length() < 1)
//       throw new ProcessValuesExcep(-1,"Unable to prepare Client Response ");
//     return response.toString();
//   }
//
//   private String prepareClientResponseReloadFunction(ResponseInfoObj responseObj) throws ProcessValuesExcep
//   {
//     StringBuffer response = new StringBuffer();
//     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction ---");
//     // ----------  RespCode|RespDesc|AuditNo|Fee|Balance ------------ //
//     try
//     {
//       if (responseObj.getRespCode() != null)
//       {
//         response.append(responseObj.getRespCode());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction --- responseObj.getRespCode()" + responseObj.getRespCode());
//       }
//       else
//       {
//           throw new ProcessValuesExcep(-1,"No Response Code information found ");
//       }//end else
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Response Descritpion
//       if (responseObj.getRespDesc() != null)
//       {
//         response.append(responseObj.getRespDesc());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction --- responseObj.getRespDesc()" + responseObj.getRespDesc());
//       }//end if
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Audit Number
//       if (responseObj.getAuditNo() != null)
//       {
//         response.append(responseObj.getAuditNo());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction --- responseObj.getAuditNo()" + responseObj.getAuditNo());
//       }
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Fee Charges
//       if (responseObj.getFeeAmount() != null)
//       {
//         response.append(responseObj.getFeeAmount());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction --- responseObj.getFeeAmount()" + responseObj.getFeeAmount());
//       }
//       response.append(Constants.MESSAGE_DELIMETER_VALUE);
//       //Getting the Balance
//       if (responseObj.getBalance() != null)
//       {
//         response.append(responseObj.getBalance());
//         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," ServiceThread --- prepareClientResponseActivateCardFunction --- responseObj.getBalance()" + responseObj.getBalance());
//       }
//     }
//     catch (Exception e)
//     {
//       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in preparing response message for Activate Card Function -->"+e);
//       throw new ProcessValuesExcep(-1,"Unable to prepare Client Response for Activate Card Function -->"+e);
//     }//end catch
//     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Final Response Message build for Activate Card Function -->"+response);
//
//     //Checking the Response length
//     if (response.length() < 1)
//       throw new ProcessValuesExcep(-1,"Unable to prepare Client Response ");
//     return response.toString();
//   }
//

}//end thread
