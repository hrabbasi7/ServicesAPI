package com.i2c.services.handlers;

import com.i2c.services.*;
import com.i2c.services.home.*;
import com.i2c.services.util.*;
import com.i2c.transferapi.*;
import com.i2c.solspark.*;
import java.sql.*;
import java.util.*;

/**
 * <p>Title: FinancialServiceHandler: This class provides the services for financial transaction processing</p>
 * <p>Description: This class provides method for financial transaction processing. For example if we
 * want to transfer the funds from one card account to another card account </p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd.</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class FinancialServiceHandler {

  private Connection con = null;

  /**
   * Constructor
   */
  private FinancialServiceHandler() {
  } //end constructor

  /**
   * This method creates the instance of FinancialServiceHandler class and returns the reference to the
   * calling method.
   * @param _con Connection
   * @return FinancialServiceHandler -- Required object
   */

  public static FinancialServiceHandler getInstance(Connection _con) {
    FinancialServiceHandler handler = new FinancialServiceHandler();

    handler.con = _con;
    return handler;
  } //end getInstance()

  /**
   * This method performs the card to card transfer. It transfer funds from one card account into
   * other card account. It first creates the FinancialServiceHome class object. It first checks whether
   * the from 'card no' and the 'to card' are valid. If given cards are invalid then transaction is logged
   * and appropiate response is returned to the client. It then checks the transfer date if the transfer
   * date is too late (more then 7 days past ) or the transfer date is after the current date, then
   * transaction is logged and response is returned to the client with appropiate response code and the
   * description of the response. After the transfer date is validated Transfer API is used to transfer
   * the funds from one card to the other card. After successful tranfer appropiate response is returned
   * to the client which describes the response with response code and the response description.
   * @param requestObj ServicesRequestObj -- Request information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */

  public ServicesResponseObj cardToCardTransfer(ServicesRequestObj requestObj) throws
      Exception {
    //make the services response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    java.util.Date transDate = null;
    java.util.Date currentDate = null;
    java.util.Date prevAllowedDate = null;

    String currDateStrVal = null;

    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {

      respObj = financialServiceHome.isCardInfoValid(requestObj.getCardNo(),
          requestObj.getAAC(), requestObj.getExpiryDate(),
          requestObj.getAccountNo(), requestObj.getPin(), "C2C_FUNDS_TRSFR",
          requestObj.getDeviceType(), requestObj.getDeviceId(),
          requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
          requestObj.getMcc(), requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
        return respObj;
      }

      if (requestObj.getToCardNo() != null &&
          !requestObj.getToCardNo().trim().equals("")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Checking provided To Card Number is valid --- To Card Number--->" +
                                        requestObj.getToCardNo());
        //make query
        String qry = "select card_no from cards where card_no='" +
            requestObj.getToCardNo() + "'";

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Executing Query --> " + qry);
        //execute the query
        String val = financialServiceHome.getValue(qry);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Found Result --> " + val);

        if (val == null) {
          respObj.setRespCode("14");
          respObj.setRespDesc("Provided To Card Number is not valid -- (" +
                              financialServiceHome.maskCardNo(requestObj.
              getToCardNo()) + ")");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Going to log the error transaction::Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
              getCardNo(), "C2C_FUNDS_TRSFR", requestObj.getDeviceType(), null,
              "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
              requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
              requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
              requestObj.getAccountNo(), requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with iso serial no -- > " +
                                          transIds[0] +
                                          " && trace audit no -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);
          return respObj;

        } //end if
      } //end if

      if (requestObj.getToCardNo() != null &&
          !requestObj.getToCardNo().trim().equals("")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Checking the first name and last name... First Name--> " +
                                        requestObj.getFirstName() +
                                        " && last Name-->" +
                                        requestObj.getLastName());
        //make query
        String qry = "select card_no from cards where card_no='" +
            requestObj.getToCardNo() + "'";

        if (requestObj.getFirstName() != null &&
            !requestObj.getFirstName().trim().equalsIgnoreCase(""))
          qry += " and first_name1='" + requestObj.getFirstName().trim() + "'";

        if (requestObj.getLastName() != null &&
            !requestObj.getLastName().trim().equalsIgnoreCase(""))
          qry += " and last_name1='" + requestObj.getLastName().trim() + "'";

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Executing Query --> " + qry);
        //execute the query
        String val = financialServiceHome.getValue(qry);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Found Result --> " + val);

        if (val == null) {
          respObj.setRespCode("06");
          respObj.setRespDesc("Supplied first name or last name does not match with information against card (" +
                              financialServiceHome.maskCardNo(requestObj.
              getToCardNo()) + ")");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Going to log the error transaction::Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
              getCardNo(), "C2C_FUNDS_TRSFR", requestObj.getDeviceType(), null,
              "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
              requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
              requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
              requestObj.getAccountNo(), requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with iso serial no -- > " +
                                          transIds[0] +
                                          " && trace audit no -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);
          return respObj;

        } //end if
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Calling transfer API for Card to Card Transfer....");

      if (requestObj.getTransferDate() != null &&
          !requestObj.getTransferDate().trim().equals("")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Converting transfer Date....");
        //convert the date format
        transDate = CommonUtilities.getFormatDate(Constants.WEB_DATE_FORMAT,
                                                  requestObj.getTransferDate());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Getting current date...");
        //get current date
        currentDate = DateUtil.getCurrentDateValue(Constants.WEB_DATE_FORMAT);

        currDateStrVal = CommonUtilities.getFormatedDate(currentDate,
            Constants.WEB_DATE_FORMAT);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Getting 7-day previous date...");

        prevAllowedDate = CommonUtilities.getParsedDate(CommonUtilities.
            addDaysInDate(Constants.WEB_DATE_FORMAT, currDateStrVal, "-7",
                          Constants.WEB_DATE_FORMAT,
                          java.util.GregorianCalendar.DATE),
            Constants.WEB_DATE_FORMAT);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "comparing dates...");

        if (transDate.before(prevAllowedDate)) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Provided date is before 7-Day Previous Allowed date...");

          respObj.setRespCode("06");
          respObj.setRespDesc("Invalid transfer date provided in request");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Getting the Service Id..");
          //get from owner
          String fromOwner = financialServiceHome.getValue(
              "select user_id from cards where card_no='" +
              requestObj.getCardNo() + "'");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "From Owner--> " + fromOwner);
          //get to owner
          String toOwner = financialServiceHome.getValue(
              "select user_id from cards where card_no='" +
              requestObj.getToCardNo() + "'");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "To Card Owner--> " + toOwner);

          String serviceId = null;

          if (fromOwner != null && fromOwner.equalsIgnoreCase(toOwner))
            serviceId = "C2C_SELF_TRSFR";
          else
            serviceId = "C2C_FUNDS_TRSFR";

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Going to log the error transaction::Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
              getCardNo(), serviceId, requestObj.getDeviceType(), null, "0200",
              "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
              requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
              requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
              requestObj.getAccountNo(), requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with iso serial no -- > " +
                                          transIds[0] +
                                          " && trace audit no -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);
          return respObj;
        } //end if

        if (transDate.after(prevAllowedDate) && transDate.before(currentDate)) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Provided date is after 7-Day Previous Allowed date but before Current Date... Setting provided Transfer Date to Current Date");
          requestObj.setTransferDate(currDateStrVal);
        } //end if

        //convert the date format to DB
        String transferDate = CommonUtilities.convertDateFormat(Constants.
            DATE_FORMAT, Constants.WEB_DATE_FORMAT, requestObj.getTransferDate());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Date after conversion -- > " +
                                        transferDate);
        //set the converted date in request object
        requestObj.setTransferDate(transferDate);
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Calling Transfer API For card to card transfer....");
      if (requestObj.getTransId() != null &&
          !requestObj.getTransId().trim().equals("")) {
        CardToCard cardToCard = new TransferAPI(Constants.TRANSFERAPI_LOG_PATH).
            cardToCardTransfer(con, requestObj.getTransId(),
                               (requestObj.getApplyFee() != null &&
                                requestObj.getApplyFee().equalsIgnoreCase("Y") ? true : false));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response from Transfer API :: Status --  > " +
                                        cardToCard.getStatus() +
                                        " && Desc -- > " +
                                        cardToCard.getComments());

        if (!cardToCard.getStatus().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          respObj.setRespCode(cardToCard.getStatus());
          respObj.setRespDesc(cardToCard.getComments());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Returning Response back.....");
          return respObj;
        } //end if

        respObj.setCardBalance(cardToCard.getFromCardBalance());
        respObj.setToCardBalance(cardToCard.getToCardbalance());
        respObj.setFeeAmount(cardToCard.getFeeAmount());
        respObj.setTransId(cardToCard.getTransId());
        respObj.setTransferId(cardToCard.getTransferId());
      } //end if
      else {

        if (requestObj.getAmount() == null ||
            requestObj.getAmount().equalsIgnoreCase("0")) {
          respObj.setRespCode(Constants.SUCCESS_CODE);
          respObj.setRespDesc("Transfer Amount was 0");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Transfer Amount was 0.. Returning Response....");
          return respObj;
        } //end if
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Parsing the amount in double.....");
        //parse the amount
        double amount = Double.parseDouble(requestObj.getAmount());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Amount parsed -- > " + amount);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Before calling transfer API to insert transfer.....");
        //make transfer api obj
        TransferAPI transApi = new TransferAPI(Constants.TRANSFERAPI_LOG_PATH);
        //insert the transfer
        long transferId = transApi.insertTransfer(con, requestObj.getCardNo(),
                                                  requestObj.getToCardNo(),
                                                  amount,
                                                  requestObj.getDeviceType(),
                                                  requestObj.getTransferDate(),
                                                  requestObj.getRetryOnFail(),
                                                  requestObj.getMaxTries(),
                                                  requestObj.getDescription());

        if (requestObj.getTransferDate() != null &&
            !requestObj.getTransferDate().trim().equals("")) {
          if (transDate.after(currentDate)) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG), "Transfer Date is of future...Just returning the id without processing transaction...");
            respObj.setTransId(transferId + "");
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG), "Getting card balance...");
            //get the card balance
            String balance = financialServiceHome.getValue(
                "select card_balance from card_funds where card_no='" +
                requestObj.getCardNo() + "'");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG), "Got Card Balance --> " + balance);
            //set balance in response
            respObj.setCardBalance(balance);
            return respObj;
          } //end if
        } //end if

        //process the transfer with the transfer API
        CardToCard cardToCard = transApi.cardToCardTransfer(con,
            transferId + "",
            (requestObj.getApplyFee() != null &&
             requestObj.getApplyFee().equalsIgnoreCase("Y") ? true : false));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response from Transfer API :: Status --  > " +
                                        cardToCard.getStatus() +
                                        " && Desc -- > " +
                                        cardToCard.getComments());

        if (!cardToCard.getStatus().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          respObj.setRespCode(cardToCard.getStatus());
          respObj.setRespDesc(cardToCard.getComments());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Returning Response back.....");
          return respObj;
        } //end if

        respObj.setCardBalance(cardToCard.getFromCardBalance());
        respObj.setToCardBalance(cardToCard.getToCardbalance());
        respObj.setFeeAmount(cardToCard.getFeeAmount());
        respObj.setTransId(cardToCard.getTransId());
        respObj.setTransferId(cardToCard.getTransferId());
      } //end else

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transaction processed successfully...Returning response...");
      respObj.setRespCode(Constants.SUCCESS_CODE);
      respObj.setRespDesc(Constants.SUCCESS_MSG);
      return respObj;
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
      respObj.setStkTrace(trc);
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Going to log the error transaction::Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //log the transaction
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end cardToCardTransfer

  /**
   * This methos transfers funds between two cards. It first determines the service id
   * based on the ACH type in the request object. If the ACH type is 1 then it validates the ACH Load
   * account. It then determines the tranfer date and checks whether the tranfer date is before the
   * current date, after the current date or too many days has been passed since the today's date.
   * If the transfer time is too late i.e. if transfer date is before 7 days of today's date then
   * transaction is logged and response is returned to the user. If the transfer date is after the
   * current date then transaction is inserted into the database for the future transfer purpose. If the
   * transfer time is before the current time and not too late then card validation is done. If the
   * validation is perfromed successfuly, transfer API is used to load the fund into the specified
   * card account. At the end of the processing transaction fee is charged if it is allowed and
   * the response is returned to the user.
   * @param requestObj ServicesRequestObj -- Request information
   * @throws Exception
   * @return ServicesResponseObj -- Response information
   */

  public ServicesResponseObj transferFunds(ServicesRequestObj requestObj) throws
      Exception {

    java.util.Date transDate = null;
    java.util.Date currentDate = null;
    java.util.Date prevAllowedDate = null;

    String currDateStrVal = null;

    //get the right service
    String serviceId = (requestObj.getAchType().equalsIgnoreCase("1") ?
                        Constants.LOAD_FUNDS_SERVICE :
                        Constants.WITHD_FUNDS_SERVICE);
    //make the services response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);

    if (requestObj.getAchType().trim().equalsIgnoreCase("1")) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transaction is of Load and validating the banking information...");
      //validate banking information
      respObj = financialServiceHome.validateACHLoadAccount(requestObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response after validation::Code--> " +
                                      respObj.getRespCode() + " && Desc--> " +
                                      respObj.getRespDesc());

      if (respObj.getRespCode() != null &&
          !respObj.getRespCode().trim().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;
    } //end if

    if (requestObj.getTransferDate() != null &&
        !requestObj.getTransferDate().trim().equals("")) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to format the transfer date....");
      //format the date
      transDate = CommonUtilities.getFormatDate(Constants.
                                                WEB_DATE_FORMAT,
                                                requestObj.getTransferDate());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting the current date...");
      //get the current date
      currentDate = DateUtil.getCurrentDateValue(Constants.
                                                 WEB_DATE_FORMAT);

      currDateStrVal = CommonUtilities.getFormatedDate(currentDate,
          Constants.WEB_DATE_FORMAT);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting 7-day previous date...");

      prevAllowedDate = CommonUtilities.getParsedDate(CommonUtilities.
          addDaysInDate(Constants.WEB_DATE_FORMAT, currDateStrVal, "-7",
                        Constants.WEB_DATE_FORMAT,
                        java.util.GregorianCalendar.DATE),
          Constants.WEB_DATE_FORMAT);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Comparing dates...");

      if (transDate.before(prevAllowedDate)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Provided date is before 7-Day Previous Allowed date...");

        respObj.setRespCode("06");
        respObj.setRespDesc("Invalid transfer date provided in request");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), serviceId, requestObj.getDeviceType(), null, "0200",
            "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;

      } //end if

      if (transDate.after(prevAllowedDate) && transDate.before(currentDate)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Provided date is after 7-Day Previous Allowed date but before Current Date... Setting provided Transfer Date to Current Date");
        requestObj.setTransferDate(currDateStrVal);
      } //end if

      //format the transfer date
      String transferDate = CommonUtilities.convertDateFormat(Constants.
          DATE_FORMAT, Constants.WEB_DATE_FORMAT, requestObj.getTransferDate());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Date after formatting -- > " +
                                      transferDate);
      //set it in request
      requestObj.setTransferDate(transferDate);
    } //end if
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG), "------------------------------------------------------------------------------------");
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Calling transfer API to log transaction in transfer_achs....");

    double amount = Double.parseDouble(requestObj.getAmount());
    //call the transfer api for process the ach transfer
    long transferId = new TransferAPI(Constants.TRANSFERAPI_LOG_PATH).
        insertAchTransfer(requestObj.getAchType(), requestObj.getCardNo(),
                          requestObj.getBankAcctTitle(),
                          requestObj.getBankAcctNo(),
                          requestObj.getBankAcctType(), requestObj.getBankName(),
                          requestObj.getBankRoutingNo(), amount,
                          (requestObj.getTransferDate() != null ?
                           requestObj.getTransferDate() : "today"),
                          requestObj.getDeviceType(), requestObj.getRetryOnFail(),
                          requestObj.getMaxTries(),
                          requestObj.getDescription(), con);

//     SolsparkResponseObj solsRespObj =  new TransferAPI(Constants.TRANSFERAPI_LOG_PATH).processAchTransfer(requestObj.getAchType(),requestObj.getCardNo(),requestObj.getBankAcctTitle(),
//         requestObj.getBankAcctNo(),requestObj.getBankAcctType(),requestObj.getBankName(),requestObj.getBankRoutingNo(),amount,requestObj.getTransferDate(),
//         requestObj.getDeviceType(),requestObj.getRetryOnFail(),requestObj.getMaxTries(),requestObj.getDescription(), (requestObj.getApplyFee() != null && requestObj.getApplyFee().equalsIgnoreCase("Y") ? true : false), con);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Transfer API Call ended..Transfer ID -- > " +
                                    transferId);
    //get the trans id
//     String transId = financialServiceHome.getValue("select i.i_serial_no from iso_finance_msgs i,transfer_achs a,trans_requests t where" +
//                            " a.dr_trace_audit_no = t.trace_audit_no and " +
//                            "t.iso_serial_no = i.i_serial_no and " +
//                            "a.trans_id =" + transferId + "");

    if (transferId == -1) {
      respObj.setRespCode("96");
      respObj.setRespDesc("System Malfunction");
      return respObj;
    } //end if

    respObj.setRespCode(Constants.SUCCESS_CODE);
    respObj.setRespDesc(Constants.SUCCESS_MSG);
    respObj.setFeeAmount(null);
    respObj.setCardBalance(null);
    respObj.setTransId(transferId + "");
    return respObj;
  } //end transferFunds

  /**
   * This method debits or credits funds from specified card account. If amount is +ve then funds
   * will be credited otherwise debited from the card account.
   * The method first determines the amount which is debited or credited. If amount is a valid amount then
   * it checks the switch for activation and the online mode. If the switch is inactive then the
   * transaction is logged and response is returned to the user. The service then checks whether the
   * amount is negative or positive. If it is egative then card is checked for debit purpose else it
   * is checked for the credit purpose. Then transfer API is used to debit or credit the card funds.
   * The service then checks the the debit fee, subtract it from the card balance and call the Transfer
   * API to add the funds into the card account. At the end of the processing response is returned to
   * the client describing the response code with description of the response.
   * @param requestObj ServicesRequestObj -- Request Information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */



  ///////////////////////// Changed //////////////////////////////////////////////////////////
  ///////////////// Constans.ADD_FUNDS replaced with requestObj.getServiceId() ////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  public ServicesResponseObj addFunds(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to parse the requested amount in " +
                                      "double. Requested Amt -- > " +
                                      requestObj.getAmount());

      //parse the requested amount in double.
      double requestedAmt = Double.parseDouble(requestObj.getAmount());
      String switchBalance = null;
      String switchTransId = null;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "Getting the switch information.....");
      //get the switch information
      SwitchInfoObj switchInfo = financialServiceHome.getCardSwitchInfo(
          requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      //get the batch mode
      boolean batchMode = switchInfo.isBatchTransAllowed();

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "Switch Batch Mode -- > " + batchMode);

      if (requestObj.getAmount().indexOf("-") != -1) {
        //it debit amount
        requestObj.setAmount( (requestedAmt * -1) + "");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Going to check whether debits are allowed or not...");
        //call check debit
        respObj = financialServiceHome.checkDebit(requestObj.getCardNo(),
                                                  requestObj.getAmount(),
                                                  requestObj.getServiceId(),
                                                  requestObj.getApplyFee(), batchMode,requestObj.getRetreivalRefNum(),requestObj.getCardAcceptorId(),"0200",requestObj.getAcquirerId());

        requestObj.setAmount(requestedAmt + "");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Response received from Check debit -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc() +
                                        "&& Fee -- > " + respObj.getFeeAmount());

        if (respObj.getRespCode() != null &&
            !respObj.getRespCode().trim().equals(Constants.SUCCESS_CODE)) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Going to log the error transaction::Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          String desc = (respObj.getRespDesc() != null ? respObj.getRespDesc().trim() : "");
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
              getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
              "0200", "0", desc + (requestObj.getDescription() != null ? " (" + requestObj.getDescription() + ")" : ""), "0", respObj.getRespCode(),
              requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
              requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
              requestObj.getAccountNo(), requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with iso serial no -- > " +
                                          transIds[0] +
                                          " && trace audit no -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);

          return respObj;
        } //end if
      } //end if
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Going to check wether credits are allowed or not....");
        //call check credit
        respObj = financialServiceHome.checkCredit(requestObj.getCardNo(),
            requestObj.getAmount(), requestObj.getServiceId(),
            requestObj.getApplyFee(), batchMode,requestObj.getRetreivalRefNum(),requestObj.getCardAcceptorId(),"0200",requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Response from Check Credit :: Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc() +
                                        " && Fee -- > " + respObj.getFeeAmount());

        if (respObj.getRespCode() != null &&
            !respObj.getRespCode().trim().equals(Constants.SUCCESS_CODE)) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Going to log the error transaction::Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          String desc = (respObj.getRespDesc() != null ? respObj.getRespDesc().trim() : "");
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
              getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
              "0200", "0", desc + (requestObj.getDescription() != null ? " (" + requestObj.getDescription() + ")" : ""), "0", respObj.getRespCode(),
              requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
              requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
              requestObj.getAccountNo(), requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with iso serial no -- > " +
                                          transIds[0] +
                                          " && trace audit no -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);

          return respObj;
        } //end if
      } //end else

      if (batchMode) {
        TransferAPI transferAPI = new TransferAPI(Constants.LOG_FILE_PATH +
                                                  java.io.File.
                                                  separator + "transferapi");
        //parse the fee amount
        double feeAmt = Double.parseDouble(respObj.getFeeAmount());
        double amountToCreditSolspark = requestedAmt - feeAmt;

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Amount to debit / Credit SOLSPARK -- > " +
                                        amountToCreditSolspark);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Getting card program against card -- > " +
                                        requestObj.getCardNo());
        //get the card program id
        String cardPrgId = financialServiceHome.
            getValue("select card_prg_id from cards where card_no='" +
                     requestObj.getCardNo() + "'");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Calculating the Debit Fee .... Card Prg Id -- > " +
                                        cardPrgId);
        //get the debit fee
        double debitFee = transferAPI.getFee(con, cardPrgId,
                                             Constants.WEB_WITHDRAWAL);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Calling Transfer API to debit solspark with net amount -- > " +
                                        amountToCreditSolspark);
        //debit the amount at SOLSPARK
        SolsparkResponseObj solsResp = transferAPI.solsparkDebitOnly(con,
            requestObj.getCardNo(), amountToCreditSolspark * -1, debitFee);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Response from SOLSPARK:: Code -- > " +
                                        solsResp.getRespCode() +
                                        " && Desc -- > " + solsResp.getRespDesc());
        respObj.setRespCode(financialServiceHome.getSwitchResponseCode(
            switchInfo.getSwitchId(), solsResp.getRespCode()));
        respObj.setRespDesc(financialServiceHome.getSwitchResponseDesc(
            switchInfo.getSwitchId(), solsResp.getRespCode()));

        if (respObj.getRespCode() != null &&
            !respObj.getRespCode().trim().
            equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Going to log the error transaction::Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
              getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
              "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
              requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
              requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
              requestObj.getAccountNo(), requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with iso serial no -- > " +
                                          transIds[0] +
                                          " && trace audit no -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);

          return respObj;
        } //end if
        //set the switch information
        switchTransId = solsResp.getTransID();
        switchBalance = solsResp.getBalance();

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Calling load funds....");
        //call the load funds
        respObj = financialServiceHome.loadFundsAtOltp(requestObj,
            switchBalance,
            switchTransId, respObj.getRespCode(), respObj.getRespDesc());

        //set the trans id in response object
        respObj.setSwitchTransId(switchTransId);
        respObj.setCardBalance(switchBalance);
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Validating the card information...");
        //validate card information
        ServicesResponseObj infoRespObj = CardsServiceHome.getInstance(con).
            isCardInfoValid(requestObj.getCardNo(), requestObj.getAAC(),
                            requestObj.getExpiryDate(), requestObj.getAccountNo(),
                            requestObj.getPin(), requestObj.getServiceId(),
                            requestObj.getDeviceType(), requestObj.getDeviceId(),
                            requestObj.getCardAcceptorId(),
                            requestObj.getCardAcceptNameAndLoc(),
                            requestObj.getMcc(), requestObj.getAcquirerId());
        if (infoRespObj.getRespCode() != null &&
            !infoRespObj.getRespCode().
            trim().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          return infoRespObj;
        }
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Calling load funds....");
        //call the load funds
        respObj = financialServiceHome.loadFundsAtOltp(requestObj,
            switchBalance,
            requestObj.getRetreivalRefNum(), respObj.getRespCode(),
            respObj.getRespDesc());
      } //end else

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Response from Load Funds :: Code -- > " +
                                      respObj.getRespCode()
                                      + " && Desc -- > " + respObj.getRespDesc());
      return respObj;
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
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end debitFunds

  public ServicesResponseObj makeBillPayment(ServicesRequestObj requestObj) throws
          Exception {
      ServicesResponseObj respObj = new ServicesResponseObj();
      FinancialServiceHome financialServiceHome = null;
      boolean debitToday = false;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for making bill payment request,Request data got....");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "<---getCardNo---> " +
                                          requestObj.getCardNo()
                                          + "<---getAcquirerId---> " +
                                          requestObj.getAcquirerId()
                                          + "<---getAcqUsrId---> " +
                                          requestObj.getAcqUsrId()
                                          +
                  "<---getBillPaymentPayeeSerialNo---> " +
                                          requestObj.
                                          getBillPaymentPayeeSerialNo()
                                          + "<---getBillPaymentPayeeID---> " +
                                          requestObj.getBillPaymentPayeeID()
                                          +
                  "<---getBillPaymentConsumerAccountNo---> " +
                                          requestObj.
                                          getBillPaymentConsumerAccountNo()
                                          + "<---getBillPaymentAmount---> " +
                                          requestObj.getBillPaymentAmount()
                                          + "<---getBillPaymentDate---> " +
                                          requestObj.getBillPaymentDate()
                                          + "<---getRetreivalRefNum---> " +
                                          requestObj.getRetreivalRefNum()
                                          +
                                          "<---getBillPaymentPayeeName---> " +
                                          requestObj.getBillPaymentPayeeName()
                                          + "<---getBillPaymentPayeeCID---> " +
                                          requestObj.getBillPaymentPayeeCID()
                                          +
                  "<---getBillPaymentPayeeStreet1---> " +
                                          requestObj.
                                          getBillPaymentPayeeStreet1()
                                          +
                  "<---getBillPaymentPayeeStreet2---> " +
                                          requestObj.
                                          getBillPaymentPayeeStreet2()
                                          +
                  "<---getBillPaymentPayeeStreet3---> " +
                                          requestObj.
                                          getBillPaymentPayeeStreet3()
                                          +
                  "<---getBillPaymentPayeeStreet4---> " +
                                          requestObj.
                                          getBillPaymentPayeeStreet4()
                                          +
                                          "<---getBillPaymentPayeeCity---> " +
                                          requestObj.getBillPaymentPayeeCity()
                                          +
                                          "<---getBillPaymentPayeeState---> " +
                                          requestObj.getBillPaymentPayeeState()
                                          + "<---getBillPaymentPayeeZIP---> " +
                                          requestObj.getBillPaymentPayeeZIP()
                                          +
                  "<---getBillPaymentPayeeCountry---> " +
                                          requestObj.
                                          getBillPaymentPayeeCountry()
                                          + "<---getBillPaymentPayerNo---> " +
                                          requestObj.getBillPaymentPayerNo()
                                          +
                                          "<---getBillPaymentPayerName---> " +
                                          requestObj.getBillPaymentPayerName()
                                          +
                  "<---getBillPaymentPayerAddress1---> " +
                                          requestObj.
                                          getBillPaymentPayerAddress1()
                                          +
                  "<---getBillPaymentPayerAddress2---> " +
                                          requestObj.
                                          getBillPaymentPayerAddress2()
                                          +
                                          "<---getBillPaymentPayerCity---> " +
                                          requestObj.getBillPaymentPayerCity()
                                          +
                                          "<---getBillPaymentPayerState---> " +
                                          requestObj.getBillPaymentPayerState()
                                          + "<---getBillPaymentPayerZIP---> " +
                                          requestObj.getBillPaymentPayerZIP()
                                          +
                  "<---getBillPaymentPayerCountry---> " +
                                          requestObj.
                                          getBillPaymentPayerCountry()
                                          +
                  "<---getBillPaymentPayeeUserData1---> " +
                                          requestObj.
                                          getBillPaymentPayeeUserData1()
                                          +
                  "<---getBillPaymentPayeeUserData2---> " +
                                          requestObj.
                                          getBillPaymentPayeeUserData2()
                                          +
                  "<---getBillPaymentPayeeUserData3---> " +
                                          requestObj.
                                          getBillPaymentPayeeUserData3()
                                          +
                  "<---getBillPaymentPayeeUserData4---> " +
                                          requestObj.
                                          getBillPaymentPayeeUserData4()
                                          +
                  "<---getBillPaymentPayeeUserData5---> " +
                                          requestObj.
                                          getBillPaymentPayeeUserData5()
                                          +
                  "<---getBillPaymentPayeeUserData6---> " +
                                          requestObj.
                                          getBillPaymentPayeeUserData6()
                                          + "<---getDescription---> " +
                                          requestObj.getDescription()
                                          + "<---getDeviceType---> " +
                                          requestObj.getDeviceType()
                                          + "<---getAcqData2---> " +
                                          requestObj.getAcqData2()
                                          + "<---getAcqData3---> " +
                                          requestObj.getAcqData3()
                                          + "<---getCardAcceptorId---> " +
                                          requestObj.getCardAcceptorId()
                                          +
                                          "<---getCardAcceptNameAndLoc---> " +
                                          requestObj.getCardAcceptNameAndLoc()
                                          + "<---getMcc---> " +
                                          requestObj.getMcc()
                                          + "<---getDeviceId---> " +
                                          requestObj.getDeviceId()
                                          + "<---getExpiryDate---> " +
                                          requestObj.getExpiryDate()
                                          + "<---getAAC---> " +
                                          requestObj.getAAC()
                                          + "<---getPin---> " +
                                          requestObj.getPin()
                                          + "<---getAccountNo---> " +
                                          requestObj.getAccountNo()
                                          + "<---getBillPaymentAlertType---> " +
                                          requestObj.getBillPaymentAlertType()
                                          + "<---getBillPaymentAlertUserNo---> " +
                                          requestObj.getBillPaymentAlertUserNo()
                                          + "<---getApplyFee---> " +
                                          requestObj.getApplyFee());

          financialServiceHome = FinancialServiceHome.getInstance(this.con);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Checking mandatory fields...");
          if (requestObj.getBillPaymentAmount() == null ||
              requestObj.getBillPaymentAmount().trim().length() == 0
              || requestObj.getBillPaymentDate() == null ||
              requestObj.getBillPaymentDate().trim().length() == 0
              || requestObj.getBillPaymentConsumerAccountNo() == null ||
              requestObj.getBillPaymentConsumerAccountNo().trim().length() ==
              0) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Missing mandatory fields required for making bill payment request...");
              respObj.setRespCode("12");
              respObj.setRespDesc(
                      "Missing mandatory fields required for making bill payment request");
              String[] transIds = financialServiceHome.logTransaction(
                      requestObj.
                      getCardNo(), requestObj.getServiceId(),
                      requestObj.getDeviceType(), null, "0200", "0",
                      respObj.getRespDesc(),
                      "0", respObj.getRespCode(), requestObj.getDeviceId(),
                      requestObj.getCardAcceptorId(),
                      requestObj.getCardAcceptNameAndLoc(),
                      requestObj.getMcc(),
                      requestObj.getAccountNo(),
                      requestObj.getAcquirerId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Transaction logged with iso serial no -- > " +
                                              transIds[0] +
                                              " && trace audit no -- > " +
                                              transIds[1]);
              //set the trans id in response
              respObj.setTransId(transIds[0]);
              return respObj;
          }

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Checking conditional fields...");
          if ((requestObj.getBillPaymentPayeeSerialNo() == null ||
               requestObj.getBillPaymentPayeeSerialNo().trim().length() == 0)
              &&
              (requestObj.getBillPaymentPayeeID() == null ||
               requestObj.getBillPaymentPayeeID().trim().length() == 0)
              &&
              (requestObj.getBillPaymentPayeeName() == null ||
               requestObj.getBillPaymentPayeeName().trim().length() == 0
               || requestObj.getBillPaymentPayeeStreet1() == null ||
               requestObj.getBillPaymentPayeeStreet1().trim().length() == 0
               || requestObj.getBillPaymentPayeeState() == null ||
               requestObj.getBillPaymentPayeeState().trim().length() == 0
               || requestObj.getBillPaymentPayeeCity() == null ||
               requestObj.getBillPaymentPayeeCity().trim().length() == 0
               || requestObj.getBillPaymentPayeeZIP() == null ||
               requestObj.getBillPaymentPayeeZIP().trim().length() == 0)) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Missing conditional fields required for making bill payment request...");
              respObj.setRespCode("12");
              respObj.setRespDesc(
                      "Missing conditional fields required for making bill payment request");

              String[] transIds = financialServiceHome.logTransaction(
                      requestObj.
                      getCardNo(), requestObj.getServiceId(),
                      requestObj.getDeviceType(), null, "0200", "0",
                      respObj.getRespDesc(),
                      "0", respObj.getRespCode(), requestObj.getDeviceId(),
                      requestObj.getCardAcceptorId(),
                      requestObj.getCardAcceptNameAndLoc(),
                      requestObj.getMcc(),
                      requestObj.getAccountNo(),
                      requestObj.getAcquirerId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Transaction logged with iso serial no -- > " +
                                              transIds[0] +
                                              " && trace audit no -- > " +
                                              transIds[1]);
              //set the trans id in response
              respObj.setTransId(transIds[0]);
              return respObj;
          }

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Identifying bill type...");
          if (financialServiceHome.identifyBillType(requestObj)) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Bill type identified as --->" +
                                              Constants.BILL_TYPE_ONLINE);
              requestObj.setBillPaymentBillType(Constants.BILL_TYPE_ONLINE);
              requestObj.setServiceId(Constants.ONLINE_BILL_SERVICE);
          } else {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Bill type identified as --->" +
                                              Constants.BILL_TYPE_CHK);
              requestObj.setBillPaymentBillType(Constants.BILL_TYPE_CHK);
              requestObj.setServiceId(Constants.CHECK_BILL_PAY_SERVICE);
          }

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Validating Card Information like Old Pin, Old ACC, Exp Date.....");
          //validate the card information
          respObj = financialServiceHome.isCardInfoValid(requestObj.getCardNo(),
                  requestObj.getAAC(),
                  requestObj.getExpiryDate(),
                  requestObj.getAccountNo(),
                  requestObj.getPin(),
                  requestObj.getServiceId(),
                  requestObj.getDeviceType(),
                  requestObj.getDeviceId(),
                  requestObj.getCardAcceptorId(),
                  requestObj.
                  getCardAcceptNameAndLoc(),
                  requestObj.getMcc(),
                  requestObj.getAcquirerId());
          if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
              return respObj;
          }
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Getting the switch information....");
          //get the switch information
          SwitchInfoObj switchInfo = financialServiceHome.getCardSwitchInfo(
                  requestObj.
                  getCardNo());

          if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
              respObj.setRespCode("91");
              respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                                  ") is not active for Card (" +
                                  financialServiceHome.maskCardNo(requestObj.
                      getCardNo()) +
                                  ")");

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Going to log the error transaction::Code -- > " +
                                              respObj.getRespCode() +
                                              " && Desc -- > " +
                                              respObj.getRespDesc());
              //log the transaction
              String[] transIds = financialServiceHome.logTransaction(
                      requestObj.
                      getCardNo(), requestObj.getServiceId(),
                      requestObj.getDeviceType(), null, "0200", "0",
                      respObj.getRespDesc(),
                      "0", respObj.getRespCode(), requestObj.getDeviceId(),
                      requestObj.getCardAcceptorId(),
                      requestObj.getCardAcceptNameAndLoc(),
                      requestObj.getMcc(),
                      requestObj.getAccountNo(),
                      requestObj.getAcquirerId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Transaction logged with iso serial no -- > " +
                                              transIds[0] +
                                              " && trace audit no -- > " +
                                              transIds[1]);
              //set the trans id in response
              respObj.setTransId(transIds[0]);
              return respObj;
          } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

          if (switchInfo.getSwitchId() != null &&
              !switchInfo.getSwitchId().trim().equals("") &&
              switchInfo.isBatchTransAllowed()) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Switch is active and Batch Mode is on....");
              respObj.setRespCode("40");
              respObj.setRespDesc(
                      "Make Bill Payment service is not supported at switch(" +
                      switchInfo.getSwitchId() + ")");

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Going to log the error transaction::Code -- > " +
                                              respObj.getRespCode() +
                                              " && Desc -- > " +
                                              respObj.getRespDesc());
              //log the transaction
              String[] transIds = financialServiceHome.logTransaction(
                      requestObj.
                      getCardNo(), requestObj.getServiceId(),
                      requestObj.getDeviceType(), null, "0200", "0",
                      respObj.getRespDesc(),
                      "0", respObj.getRespCode(), requestObj.getDeviceId(),
                      requestObj.getCardAcceptorId(),
                      requestObj.getCardAcceptNameAndLoc(),
                      requestObj.getMcc(),
                      requestObj.getAccountNo(),
                      requestObj.getAcquirerId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Transaction logged with iso serial no -- > " +
                                              transIds[0] +
                                              " && trace audit no -- > " +
                                              transIds[1]);
              //set the trans id in response
              respObj.setTransId(transIds[0]);
              return respObj;
          } //end if

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Checking provided amount against parameter--->" +
                                          requestObj.getBillPaymentAmount());
          String cardPrgId = financialServiceHome.getCardProgramID(requestObj.getCardNo());
          if(!financialServiceHome.isParameterAllowed(Constants.BILL_PAY_LIMIT_PARAM,cardPrgId,requestObj.getBillPaymentAmount())){
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Provided Amount violates parameter--->" + Constants.BILL_PAY_LIMIT_PARAM);
              respObj.setRespCode("57");
              respObj.setRespDesc("Provided bill payment amount(" + requestObj.getBillPaymentAmount() + ") violates parameter (" + Constants.BILL_PAY_LIMIT_PARAM + ")");

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Going to log the error transaction::Code -- > " +
                                              respObj.getRespCode() +
                                              " && Desc -- > " +
                                              respObj.getRespDesc());
              //log the transaction
              String[] transIds = financialServiceHome.logTransaction(
                      requestObj.
                      getCardNo(), requestObj.getServiceId(),
                      requestObj.getDeviceType(), null, "0200", "0",
                      respObj.getRespDesc(),
                      "0", respObj.getRespCode(), requestObj.getDeviceId(),
                      requestObj.getCardAcceptorId(),
                      requestObj.getCardAcceptNameAndLoc(),
                      requestObj.getMcc(),
                      requestObj.getAccountNo(),
                      requestObj.getAcquirerId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Transaction logged with iso serial no -- > " +
                                              transIds[0] +
                                              " && trace audit no -- > " +
                                              transIds[1]);
              //set the trans id in response
              respObj.setTransId(transIds[0]);
              return respObj;

          }

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "validating provided payment date--->" +
                                          requestObj.getBillPaymentDate());

          int chkDate = checkTransferInvocationDate(requestObj.
                  getBillPaymentDate());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "validation value got for provided payment date--->" +
                                          chkDate);
          if (chkDate == -1) {
              requestObj.setBillPaymentDate(CommonUtilities.
                                            getCurrentFormatDate(Constants.WEB_DATE_FORMAT));
              debitToday = true;
          } else if (chkDate == 1) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Payment date is less than [current date - 7days], returning error response...]");
              respObj.setRespCode("06");
              respObj.setRespDesc("Invalid Bill Payment Date provided in request,Payment date is less than [CurrentDate - 7days]");
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Going to log the error transaction::Code -- > " +
                                              respObj.getRespCode() +
                                              " && Desc -- > " +
                                              respObj.getRespDesc());
              //log the transaction
              String[] transIds = financialServiceHome.logTransaction(
                      requestObj.
                      getCardNo(), requestObj.getServiceId(),
                      requestObj.getDeviceType(), null,
                      "0200",
                      "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
                      requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                      requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                      requestObj.getAccountNo(), requestObj.getAcquirerId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Transaction logged with iso serial no -- > " +
                                              transIds[0] +
                                              " && trace audit no -- > " +
                                              transIds[1]);
              //set the trans id in response
              respObj.setTransId(transIds[0]);
              return respObj;
          } else if (chkDate == 2) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Payment date is between [current date - 7days AND current date, logging transaction with current date...]");
              requestObj.setBillPaymentDate(CommonUtilities.
                                            getCurrentFormatDate(Constants.WEB_DATE_FORMAT));
              debitToday = true;
          } else if (chkDate == 3) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Payment date is of future,just scheduling transaction...");
          }else if (chkDate == 4) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Payment date is equal to current date...]");
              debitToday = true;
          }

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Cheking serial based payee data provided/exist... ");
          if (!financialServiceHome.checkSerialBasedPayeeData(requestObj)) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Unable to log bill payment request as no payee data found based on provided payee serial...");
              respObj.setRespCode("12");
              respObj.setRespDesc(
                      "Unable to log bill payment request as no payee data found based on provided payee serial");

              String[] transIds = financialServiceHome.logTransaction(
                      requestObj.
                      getCardNo(), requestObj.getServiceId(),
                      requestObj.getDeviceType(), null, "0200", "0",
                      respObj.getRespDesc(),
                      "0", respObj.getRespCode(), requestObj.getDeviceId(),
                      requestObj.getCardAcceptorId(),
                      requestObj.getCardAcceptNameAndLoc(),
                      requestObj.getMcc(),
                      requestObj.getAccountNo(),
                      requestObj.getAcquirerId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Transaction logged with iso serial no -- > " +
                                              transIds[0] +
                                              " && trace audit no -- > " +
                                              transIds[1]);
              //set the trans id in response
              respObj.setTransId(transIds[0]);
              return respObj;
          }

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Checking Payee is Active, Payee ID--->" + requestObj.getBillPaymentPayeeID() + "<---Payee Serial No-->" + requestObj.getBillPaymentPayeeSerialNo());
          if (requestObj.getBillPaymentPayeeID() != null &&
              requestObj.getBillPaymentPayeeID().trim().length() > 0) {
              if (!financialServiceHome.checkPayeeActiveById(requestObj.
                      getBillPaymentPayeeID())) {

                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                                                  "Payee is not active(" + requestObj.getBillPaymentPayeeID() + ")");
                  respObj.setRespCode("IP");
                  respObj.setRespDesc("Payee is not active(" + requestObj.getBillPaymentPayeeID() + ")");
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                                                  "Going to log the error transaction::Code -- > " +
                                                  respObj.getRespCode() +
                                                  " && Desc -- > " +
                                                  respObj.getRespDesc());
                  //log the transaction
                  String[] transIds = financialServiceHome.logTransaction(
                          requestObj.
                          getCardNo(), requestObj.getServiceId(),
                          requestObj.getDeviceType(), null, "0200", "0",
                          respObj.getRespDesc(),
                          "0", respObj.getRespCode(), requestObj.getDeviceId(),
                          requestObj.getCardAcceptorId(),
                          requestObj.getCardAcceptNameAndLoc(),
                          requestObj.getMcc(),
                          requestObj.getAccountNo(),
                          requestObj.getAcquirerId());

                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                                                  "Transaction logged with iso serial no -- > " +
                                                  transIds[0] +
                                                  " && trace audit no -- > " +
                                                  transIds[1]);
                  //set the trans id in response
                  respObj.setTransId(transIds[0]);
                  return respObj;


              }
          } else if (requestObj.getBillPaymentPayeeSerialNo() != null &&
                     requestObj.getBillPaymentPayeeSerialNo().trim().length() >
                     0) {
              if (!financialServiceHome.checkPayeeActiveBySerialNo(requestObj.
                      getBillPaymentPayeeSerialNo())) {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                                                  "Payee is not active(" +
                                                  requestObj.
                                                  getBillPaymentPayeeSerialNo() + ")");
                  respObj.setRespCode("IP");
                  respObj.setRespDesc("Payee is not active(" +
                                      requestObj.getBillPaymentPayeeSerialNo() + ")");
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                                                  "Going to log the error transaction::Code -- > " +
                                                  respObj.getRespCode() +
                                                  " && Desc -- > " +
                                                  respObj.getRespDesc());
                  //log the transaction
                  String[] transIds = financialServiceHome.logTransaction(
                          requestObj.
                          getCardNo(), requestObj.getServiceId(),
                          requestObj.getDeviceType(), null, "0200", "0",
                          respObj.getRespDesc(),
                          "0", respObj.getRespCode(), requestObj.getDeviceId(),
                          requestObj.getCardAcceptorId(),
                          requestObj.getCardAcceptNameAndLoc(),
                          requestObj.getMcc(),
                          requestObj.getAccountNo(),
                          requestObj.getAcquirerId());

                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                                                  "Transaction logged with iso serial no -- > " +
                                                  transIds[0] +
                                                  " && trace audit no -- > " +
                                                  transIds[1]);
                  //set the trans id in response
                  respObj.setTransId(transIds[0]);
                  return respObj;
              }
          }

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Logging bill payment request...");
          long serial = financialServiceHome.logBillPaymentRequest(requestObj);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Serial got--->" + serial);

          if (serial < 0) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Missing mandatory fields required for making bill payment request...");
              respObj.setRespCode("96");
              respObj.setRespDesc(
                      "Failed to log Bill Payment request, Invalid Serial got--->" +
                      serial);
              String[] transIds = financialServiceHome.logTransaction(
                      requestObj.
                      getCardNo(), requestObj.getServiceId(),
                      requestObj.getDeviceType(), null, "0200", "0",
                      respObj.getRespDesc(),
                      "0", respObj.getRespCode(), requestObj.getDeviceId(),
                      requestObj.getCardAcceptorId(),
                      requestObj.getCardAcceptNameAndLoc(),
                      requestObj.getMcc(),
                      requestObj.getAccountNo(),
                      requestObj.getAcquirerId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Transaction logged with iso serial no -- > " +
                                              transIds[0] +
                                              " && trace audit no -- > " +
                                              transIds[1]);
              //set the trans id in response
              respObj.setTransId(transIds[0]);
              return respObj;
          }

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                          "Calculating processing days for bill payment request...");

          int processingDays = 0;
          int leadDays = 0;
          try {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Calculating bill payment lead days --- Parameter Code--->" + Constants.BILL_PAY_LEAD_DAYS_PARAM);
              leadDays = Integer.parseInt(financialServiceHome.
                                          getParameterValue(
                                                  cardPrgId,
                                                  Constants.BILL_PAY_LEAD_DAYS_PARAM));
          } catch (NumberFormatException nfEx) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "NumberFormatException in converting lead days to integer value--->" + nfEx + "<----Proceeding with default value--->" + leadDays);
          } catch (Exception genEx) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Exception in converting lead days to integer value--->" + genEx + "<----Proceeding with default value--->" + leadDays);
          }

          if(requestObj.getBillPaymentBillType().equals(Constants.BILL_TYPE_ONLINE)){
              processingDays = Integer.parseInt(Constants.ONLINE_BP_PROCCESSING_DAYS) + leadDays;

          }else if(requestObj.getBillPaymentBillType().equals(Constants.BILL_TYPE_CHK)){
              processingDays = Integer.parseInt(Constants.CHECK_BP_PROCCESSING_DAYS) + leadDays;
          }
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Processing days calculated--->" + processingDays);
          if (debitToday) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Deducting specified amount from provided card...");

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Calling OLTP API for check debit...");
              respObj = financialServiceHome.checkDebit(requestObj.getCardNo(),
                                              requestObj.getBillPaymentAmount(),
                                              requestObj.getServiceId(),
                                              requestObj.getApplyFee(), false,
                                              requestObj.getRetreivalRefNum(),
                                              requestObj.getCardAcceptorId(),
                                              "0200",
                                              requestObj.getAcquirerId());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Response received from Check debit -- > " +
                                              respObj.getRespCode() +
                                              " && Desc -- > " +
                                              respObj.getRespDesc() +
                                              "&& Fee -- > " +
                                              respObj.getFeeAmount());

              if (respObj.getRespCode() != null &&
                  !respObj.getRespCode().trim().equals(Constants.SUCCESS_CODE)) {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                          "Going to log the error transaction::Code -- > " +
                          respObj.getRespCode() +
                          " && Desc -- > " +
                          respObj.getRespDesc());
                  String desc = (respObj.getRespDesc() != null ?
                                 respObj.getRespDesc().trim() : "");
                  //log the transaction
                  String[] transIds = financialServiceHome.logTransaction(
                          requestObj.
                          getCardNo(), requestObj.getServiceId(),
                          requestObj.getDeviceType(), null,
                          "0200", "0",
                          desc +
                          (requestObj.getDescription() != null ?
                           " (" + requestObj.getDescription() + ")" : ""),
                          "0",
                          respObj.getRespCode(),
                          requestObj.getDeviceId(),
                          requestObj.getCardAcceptorId(),
                          requestObj.getCardAcceptNameAndLoc(),
                          requestObj.getMcc(),
                          requestObj.getAccountNo(), requestObj.getAcquirerId());

                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                          "Transaction logged with iso serial no -- > " +
                          transIds[0] +
                          " && trace audit no -- > " +
                          transIds[1]);
                  //set the trans id in response
                  respObj.setTransId(transIds[0]);
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                          "Updating Status (to Failed) & ISO Serial in BP request...");

                  String failureDesc = (requestObj.getDescription() != null ? requestObj.getDescription() : "");
                  failureDesc = failureDesc + " [" + desc + "] ";

                  financialServiceHome.updateBPRequestStatus(serial,
                          Constants.BILL_PAY_STATUS_FAILED,
                          respObj.getTransId(), failureDesc);
                  return respObj;
              } //end if

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG), "Calling OLTP API for deducting funds....");
              double amount = Double.parseDouble(requestObj.
                                                 getBillPaymentAmount());
              String debitAmt = (amount * -1) + "";
              requestObj.setAmount(debitAmt);
              //call the load funds
              respObj = financialServiceHome.loadFundsAtOltp(requestObj,
                      null, requestObj.getRetreivalRefNum(),
                        respObj.getRespCode(),
                        respObj.getRespDesc());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Response from Load Funds :: Code -- > " +
                                              respObj.getRespCode()
                                              + " && Desc -- > " +
                                              respObj.getRespDesc()
                                              + " && ISO Serial -- > " +
                                              respObj.getTransId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Updating Status & ISO Serial in BP request...");

              if (respObj.getRespCode() != null &&
                  !respObj.getRespCode().trim().equals(Constants.SUCCESS_CODE)) {
                  financialServiceHome.updateBPRequestStatus(serial,
                          Constants.BILL_PAY_STATUS_FAILED,
                          respObj.getTransId(), respObj.getDescription());
              } else {
                  financialServiceHome.updateBPRequestStatus(serial,
                          Constants.BILL_PAY_STATUS_INPROGRS,
                          respObj.getTransId());
              }
              respObj.setBillPaymentTransId(Long.toString(serial));
              respObj.setBillPaymentProcessingDays(String.valueOf(processingDays));
              return respObj;
          } else {
              respObj.setRespCode("00");
              respObj.setRespDesc("OK");
              respObj.setBillPaymentTransId(Long.toString(serial));
              respObj.setBillPaymentProcessingDays(String.valueOf(processingDays));
              return respObj;
          }
      } catch (Exception exp) {
          String trc = CommonUtilities.getStackTrace(exp);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_SEVERE),
                                          "Exception -- > " + exp.getMessage() +
                                          "Stack Trace -- > " + trc);
          try {
              if (con != null)
                  con.rollback();
          } catch (Exception ex) {} //end try
          //set the response code to system error
          respObj.setRespCode("96");
          respObj.setRespDesc("System Error-->" + trc);
          //respObj.setSysDesc("System Error, Check logs for more information...");
          respObj.setExcepMsg(exp.getMessage());
          respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
          respObj.setLogFilePath("More information can be found at--->" +
                                 Constants.EXACT_LOG_PATH);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                          "Going to log the error transaction::Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
                  getCardNo(), requestObj.getServiceId(),
                              requestObj.getDeviceType(), null,
                                      "0200", "0", respObj.getRespDesc(), "0",
                                      respObj.getRespCode(),
                                      requestObj.getDeviceId(),
                                      requestObj.getCardAcceptorId(),
                                      requestObj.getCardAcceptNameAndLoc(),
                                      requestObj.getMcc(),
                                      requestObj.getAccountNo(),
                                      requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                          "Transaction logged with iso serial no -- > " +
                                          transIds[0] +
                                          " && trace audit no -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);
          return respObj;
      }
  }


  public ServicesResponseObj performBillPaymentReversal(ServicesRequestObj requestObj) throws Exception{
      ServicesResponseObj respObj = new ServicesResponseObj();
      FinancialServiceHome financialServiceHome = null;

    try {
        financialServiceHome = FinancialServiceHome.getInstance(this.con);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Validating Card Information like Old Pin, Old ACC, Exp Date.....");
        //validate the card information
        respObj = financialServiceHome.isCardInfoValid(requestObj.getCardNo(),
                                              requestObj.getAAC(),
                                              requestObj.getExpiryDate(),
                                              requestObj.getAccountNo(),
                                              requestObj.getPin(),
                                              requestObj.getServiceId(),
                                              requestObj.getDeviceType(),
                                              requestObj.getDeviceId(),
                                              requestObj.getCardAcceptorId(),
                                              requestObj.
                                              getCardAcceptNameAndLoc(),
                                              requestObj.getMcc(),
                                              requestObj.getAcquirerId());
        if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
            return respObj;
        }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Getting the switch information....");

        //get the switch information
        SwitchInfoObj switchInfo = financialServiceHome.getCardSwitchInfo(requestObj.
                getCardNo());

        if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
            respObj.setRespCode("91");
            respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                                ") is not active for Card (" +
                                financialServiceHome.maskCardNo(requestObj.getCardNo()) +
                                ")");

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Going to log the error transaction::Code -- > " +
                                            respObj.getRespCode() +
                                            " && Desc -- > " +
                                            respObj.getRespDesc());
            //log the transaction
            String[] transIds = financialServiceHome.logTransaction(requestObj.
                    getCardNo(), requestObj.getServiceId(),
                    requestObj.getDeviceType(), null, "0200", "0",
                    respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(),
                    requestObj.getAccountNo(),
                    requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Transaction logged with iso serial no -- > " +
                                            transIds[0] +
                                            " && trace audit no -- > " +
                                            transIds[1]);
            //set the trans id in response
            respObj.setTransId(transIds[0]);
            return respObj;
        } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

        if (switchInfo.getSwitchId() != null &&
            !switchInfo.getSwitchId().trim().equals("") &&
            switchInfo.isBatchTransAllowed()) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Switch is active and Batch Mode is on....");
            respObj.setRespCode("40");
            respObj.setRespDesc(
                    "Bill Payment reversal service is not supported at switch(" +
                    switchInfo.getSwitchId() + ")");

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Going to log the error transaction::Code -- > " +
                                            respObj.getRespCode() +
                                            " && Desc -- > " +
                                            respObj.getRespDesc());
            //log the transaction
            String[] transIds = financialServiceHome.logTransaction(requestObj.
                    getCardNo(), requestObj.getServiceId(),
                    requestObj.getDeviceType(), null, "0200", "0",
                    respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(),
                    requestObj.getAccountNo(),
                    requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Transaction logged with iso serial no -- > " +
                                            transIds[0] +
                                            " && trace audit no -- > " +
                                            transIds[1]);
            //set the trans id in response
            respObj.setTransId(transIds[0]);
            return respObj;
        } //end if

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Performing reversal ....");
        respObj = financialServiceHome.processBillPaymentReveral(requestObj);
        if(respObj == null || respObj.getRespCode() == null || respObj.getRespCode().trim().length() == 0){
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Invalid response received from OLTP API for processing bill payment reversal...");
            throw new Exception("Invalid response received from OLTP API for processing bill payment reversal");
        }
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne got... RespCode--->" + respObj.getRespCode()
                + "<---RespDesc--->" + respObj.getRespDesc()
                + "<---TransId--->" + respObj.getTransId()
                + "<---CardBalance--->" + respObj.getCardBalance()
                + "<---FeeAmount--->" + respObj.getFeeAmount()
                + "<---BusinessDate--->" + respObj.getBusinessDate());
        return respObj;
    }catch (Exception exp) {
        String trc = CommonUtilities.getStackTrace(exp);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                        "Exception -- > " + exp.getMessage() +
                                        "Stack Trace -- > " + trc);
        try {
            if (con != null)
                con.rollback();
        } catch (Exception ex) { //end try
            ex.printStackTrace();
        }
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
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
                getCardNo(), requestObj.getServiceId(),
                            requestObj.getDeviceType(), null,
                                    "0200", "0", respObj.getRespDesc(), "0",
                                    respObj.getRespCode(),
                                    requestObj.getDeviceId(),
                                    requestObj.getCardAcceptorId(),
                                    requestObj.getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(),
                                    requestObj.getAccountNo(),
                                    requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
    } //end catch
  }

  /**
   * This method reverses the already made transaction. It first checks the trasaction
   * category if the category of the transaction is 'F' then user is informed about invalid transaction
   * with appropiate response code and the response description. The service then gets the switch
   * information and validate the switch. If the switch is inactive then transaction is logged and
   * response is returned to the user. It then checks the transaction date if the transaction date is
   * valid date then reversal of the transaction is performed.
   * @param requestObj ServicesRequestObj -- Request information
   * @throws Exception
   * @return ServicesResponseObj -- Response information
   */

  ///////////////////////////////// Changed ////////////////////////////////////////////////////
  //   Constants.REVERSAL_SERVICE were replaced with requestObj.getServiceId()                //
  //////////////////////////////////////////////////////////////////////////////////////////////

  public ServicesResponseObj performReversal(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //delare switch balance and ret ref no
    String switchBal = null;
    String retRefNo = null;

    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Trans Category received--> " +
                                      requestObj.getTransCat());
      if (requestObj.getTransCat() != null &&
          !requestObj.getTransCat().trim().equalsIgnoreCase("F")) {
        respObj.setRespCode("12");
        respObj.setRespDesc("Invalid Transaction Id supplied");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Switch Information...");
      //get the switch info
      SwitchInfoObj switchInfo = CardsServiceHome.getInstance(con).
          getCardSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (requestObj.getTransDate() != null &&
          !requestObj.getTransDate().trim().equals("")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Converting the transaction date format from MM/DD/YYYY to yyyy-mm-dd..");
        //convert the date format
        String date = CommonUtilities.convertDateFormat(Constants.
            INFORMIX_DATE_FORMAT, Constants.WEB_DATE_FORMAT,
            requestObj.getTransDate());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Converted Date --> " + date);
        //set the converted date in request obj
        requestObj.setTransDate(date);
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking whether reversal is allowed or not...");
      //check to perform the reversal
      respObj = financialServiceHome.checkReversal(requestObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response from Check Reveral:: Code-->" +
                                      respObj.getRespCode() + " && Desc--> " +
                                      respObj.getRespDesc());

      //validate switch info
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        double amount = Double.parseDouble(respObj.getFeeAmount());
        //validate the response
        if (respObj.getRespCode() != null &&
            respObj.getRespCode().trim().equals(Constants.SUCCESS_CODE) &&
            (amount > 0 || amount < 0)) {
          //make the solspark request object
          SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
          //set the appropriate parameters
          solsReqObj.setCardNum(requestObj.getCardNo());
          solsReqObj.setAac(requestObj.getAAC());
          solsReqObj.setExpDate(requestObj.getExpiryDate());
          solsReqObj.setAccountNum(requestObj.getAccountNo());
          solsReqObj.setSwitchInfo(switchInfo.getSwitchId());
          solsReqObj.setAmount("-" + respObj.getFeeAmount());
          //make the solspark handler
          SolsparkHandler handler = new SolsparkHandler(con);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Before Calling SOLSPARK API...........");
          //add the funds
          SolsparkResponseObj solsRespObj = handler.addCardFunds(solsReqObj);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Response Object Got from SOLSPARK -- > Resp code -- > " +
                                          solsRespObj.getRespCode() +
                                          " Resp Desc -- > " +
                                          solsRespObj.getRespDesc());
          //set the response
          respObj.setRespCode(financialServiceHome.getSwitchResponseCode(
              switchInfo.getSwitchId(), solsRespObj.getRespCode()));
          respObj.setRespDesc(financialServiceHome.getSwitchResponseDesc(
              switchInfo.getSwitchId(), solsRespObj.getRespCode()));
          retRefNo = solsRespObj.getTransID();
          switchBal = solsRespObj.getBalance();

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Calling confirm reversal...");
          //confirm the reversal
          respObj = financialServiceHome.confirmReveral(requestObj,
              respObj.getRespCode(), respObj.getRespDesc(), retRefNo, switchBal);
          //set the solspark response information in response object
          if (switchBal != null && retRefNo != null) {
            respObj.setCardBalance(switchBal);
            respObj.setSwitchTransId(retRefNo);
          } //end if
        } //end if
      } //end if(switchInfo.getSwitchId() != null &&!switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Calling confirm reversal...");
        //confirm the reversal
        respObj = financialServiceHome.confirmReveral(requestObj,
            respObj.getRespCode(), respObj.getRespDesc(),
            requestObj.getRetreivalRefNum(), switchBal);
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response after processing :: Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      return respObj;
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
      respObj.setExcepMsg(exp.getMessage());
      //respObj.setSysDesc("System Error, Check logs for more information...");
      respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Going to log the error transaction::Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //log the transaction
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end performReversal

  public ServicesResponseObj performPurchaseOrderReversal(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Switch Information for provided card start nos--->" + requestObj.getCardNo());
      //get the switch info
      SwitchInfoObj switchInfo = CardsServiceHome.getInstance(con).getCardProgramSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card Start Nos(" + requestObj.getCardNo() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(null, requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (requestObj.getTransDate() != null &&
          !requestObj.getTransDate().trim().equals("")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Converting the transaction date format from MM/DD/YYYY to yyyy-mm-dd..");
        //convert the date format
        String date = CommonUtilities.convertDateFormat(Constants.
            INFORMIX_DATE_FORMAT, Constants.WEB_DATE_FORMAT,
            requestObj.getTransDate());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Converted Date --> " + date);
        //set the converted date in request obj
        requestObj.setTransDate(date);
      } //end if

      //check to perform the reversal
      String cardNumber = null;
      if(requestObj.getTransId() != null){
        cardNumber = financialServiceHome.getCardNumber(requestObj.getTransId(),true);
      }else if(requestObj.getRetreivalRefNum() != null){
        cardNumber = financialServiceHome.getCardNumber(requestObj.getRetreivalRefNum(),false);
      }

      requestObj.setCardNo(cardNumber);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking whether card track already generated...");
      if(requestObj.getCardNo() != null && requestObj.getCardNo().trim().length() > 0){
        if(financialServiceHome.checkCardTrackGenerated(requestObj.getCardNo())){
          respObj.setRespCode("CT");
          respObj.setRespDesc("Unable to reverse, Card Track Already generated");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                          "Going to log the transaction::Code -- > " +
                                          respObj.getRespCode() + " && Desc -- > " +
                                          respObj.getRespDesc());
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
              getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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
          return respObj;
        }
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Checking whether reversal is allowed or not...");

      respObj = financialServiceHome.checkReversal(requestObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response from Check Reveral:: Code-->" +
                                      respObj.getRespCode() + " && Desc--> " +
                                      respObj.getRespDesc());

      //validate switch info
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Switch is active and is in Batch Mode.....");
        respObj.setRespCode("40");
        respObj.setRespDesc("Purchase Order reversal is not available for switch(" +
                            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(null, requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      }else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Calling confirm reversal...");
        //confirm the reversal
        respObj = financialServiceHome.confirmReveral(requestObj,
            respObj.getRespCode(), respObj.getRespDesc(),
            requestObj.getRetreivalRefNum(), null);
        if (respObj != null && respObj.getRespCode() != null &&
            respObj.getRespCode().equals(Constants.SUCCESS_CODE)) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Checking whether the transaction to reverse is 'NC' ");
          String cardNo = null;
          if (requestObj.getTransId() != null &&
              requestObj.getTransId().trim().length() > 0) {
            cardNo = financialServiceHome.checkPurchaseOrderTransaction(requestObj.getTransId(), true);
          }else {
            cardNo = financialServiceHome.checkPurchaseOrderTransaction(requestObj.getRetreivalRefNum(), false);
          }
          if (cardNo != null) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Going to set the status of the card in MCP System......");
            financialServiceHome.changeCardStatus(cardNo,
                                                  Constants.CLOSED_CARD,false);
          }
        }
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response after processing :: Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      return respObj;
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
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end performReversal

  /**
   * This method is used to charge the fee on a specified card no. It first gets the switch information
   * and validates the switch for activation and the online mode. If the switch is inactive then the
   * transaction is logged and response is returned to the client with appropiate response code and
   * the response description. The service then checks whether the debit is allowed or not. If it is
   * not then transaction is logged and response is returned to the client. In case of success service
   * fee is applied at OLTP and the response is returned to the client.
   * @param requestObj ServicesRequestObj -- Request Information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */

  ///////////////////////////// Changed ////////////////////////////////////////////////////////////
  //   Constants.ACQ_CHARGE_FEE were replaced with requestObj.getServiceId()                      //
  //////////////////////////////////////////////////////////////////////////////////////////////////

  public ServicesResponseObj acquirerChargeFee(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    String retRefNo = null;
    String switchBalance = null;

    try {
      //parse the amount and get it in double
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Switch Information...");
      //get the switch info
      SwitchInfoObj switchInfo = CardsServiceHome.getInstance(con).
          getCardSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Calling the check debit...");
      //call the check debit
      respObj = financialServiceHome.checkDebit(requestObj.getCardNo(),
                                                requestObj.getAcquirerFeeAmt(),
                                                requestObj.getServiceId(),
                                                requestObj.getApplyFee(),
                                                switchInfo.isBatchTransAllowed(),requestObj.getRetreivalRefNum(),requestObj.getCardAcceptorId(),"0722",requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response after check debit :: Code--> " +
                                      respObj.getRespCode() + " && Desc--> " +
                                      respObj.getRespDesc());

      if (respObj.getRespCode() != null &&
          !respObj.getRespCode().trim().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if
      //validate switch info
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Switch is active and is in Batch Mode.....");
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "Pre-Authorization Service is not available for switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if(switchInfo.getSwitchId() != null &&!switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Applying the service fee...");
      //applying the fee
      respObj = financialServiceHome.applyServiceFeeAtOltp(requestObj.getCardNo(),
          requestObj.getServiceId(), requestObj.getAcquirerFeeAmt(),
          requestObj.getAcquirerFeeDesc(), switchBalance, requestObj.getRetreivalRefNum(),
          null, requestObj.getDeviceType(), requestObj.getDeviceId(),
          requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
          requestObj.getMcc(), requestObj.getAcquirerId(),
          requestObj.getAcqUsrId(), requestObj.getAcqData1(),
          requestObj.getAcqData2(), requestObj.getAcqData3());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response After applying fee:: Code--> " +
                                      respObj.getRespCode() + " && Desc--> " +
                                      respObj.getRespDesc());

      if (respObj.getRespCode() != null &&
          !respObj.getRespCode().trim().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if
      if (switchBalance != null && !switchBalance.trim().equals(""))
        respObj.setCardBalance(switchBalance);
        //set the Business date in response
      respObj.setBusinessDate(DateUtil.getCurrentDate(Constants.WEB_DATE_FORMAT));
      return respObj;
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
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end acquirerChargeFee

  /**
   * This method reverses the acquirer charged fee.  It first gets the switch information
   * and validates the switch for activation and the online mode. If the switch is inactive or in the
   * batch mode then the transaction is logged and response is returned to the client with appropiate
   * response code and the response description. The service then calls the FinancialServiceHandler class
   * to reverse the acquirer fee. At the end of the processing appropiate response is returned to the user.
   * @param requestObj ServicesRequestObj -- Request Information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */

  public ServicesResponseObj reverseAcquirerFee(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Switch Information...");
      //get the switch info
      SwitchInfoObj switchInfo = CardsServiceHome.getInstance(con).
          getCardSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), Constants.REV_ACQ_FEE, requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      //validate switch info
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Switch is active and is in Batch Mode.....");
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "Pre-Authorization Service is not available for switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), Constants.REV_ACQ_FEE, requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if(switchInfo.getSwitchId() != null &&!switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Calling Reverse Acquirer Fee API...");
      //pass the call
      respObj = financialServiceHome.reverseAcquirerFee(requestObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Returning Response Received...");
      //return the response
      return respObj;
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
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end reverseAcquirerFee

  /**
   * This method gets the balance of supplied card. This method first gets the
   * switch information from the database if the switch is not active then transaction is logged
   * and response is retuned to the user. If the switch is batch then the balance of the card is
   * determined at the the SOLSPARK switch. Service then checks whether the application of the
   * service fee is allowed or not. If it is allowed then the service fee is applied and the
   * card balance is debited. At the end of the processing a response is returned to the client
   * which contains the response code with description.
   * @param requestObj ServicesRequestObj -- Request Information
   * @throws Exception
   * @return ServicesResponseObj -- Response information
   */



  /////////////////// Changed  /////////////////////////////////////////////////////////////////
  //////// Constants.BALACE_INQUIRY_SERVICE replaced with requestObj.getServiceId() ///////////
  /////////////////////////////////////////////////////////////////////////////////////////////
  public ServicesResponseObj getCardBalance(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      //get the switch info
      SwitchInfoObj switchInfo = financialServiceHome.getCardSwitchInfo(
          requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Batch Trans Allowed is 'Y' and Getting the balance from SOLSPARK....");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Validating Card....");
        //validate the information
        respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.
            getCardNo(), requestObj.getServiceId(), "N", "BE",
            requestObj.getDeviceType(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response After validation :: Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //validate response code
        if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
          return respObj;

        //create the solspark handler object
        SolsparkHandler handler = new SolsparkHandler(con);
        SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
        //set the card no from services request object to the solspark request object
        solsReqObj.setCardNum(requestObj.getCardNo());
        solsReqObj.setAac(requestObj.getAAC());
        solsReqObj.setExpDate(requestObj.getExpiryDate());
        solsReqObj.setAccountNum(requestObj.getAccountNo());
        //set the switch
        solsReqObj.setSwitchInfo(switchInfo.getSwitchId());
        //get the balance from solspark
        SolsparkResponseObj solsRespObj = handler.getCardBalance(solsReqObj);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Received From SOLSPARK :: Resp Code -- > " +
                                        solsRespObj.getRespCode() + "\n" +
                                        "Resp Desc -- > " +
                                        solsRespObj.getRespDesc() + "\n" +
                                        "Balance -- > " +
                                        solsRespObj.getBalance() + "\n" +
                                        "Trans Id -- > " +
                                        solsRespObj.getTransID());

        if (!solsRespObj.getRespCode().equals(Constants.SUCCESS_CODE)) {
          respObj.setRespCode(financialServiceHome.getSwitchResponseCode(
              switchInfo.getSwitchId(), solsRespObj.getRespCode()));
          //set the response description
          respObj.setRespDesc(financialServiceHome.getSwitchResponseDesc(
              switchInfo.getSwitchId(), solsRespObj.getRespCode()));
          return respObj;
        } //end if
        //set the solspark response information in response object
        respObj.setSwitchTransId(solsRespObj.getTransID());
        respObj.setCardBalance(solsRespObj.getBalance());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Going to log the transaction in MCP System...");
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(),
            requestObj.getDeviceType(), Constants.BALANCE_INQUIRY_MSG,
            solsRespObj.getTransID(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Transaction logged with ISO Serial No -- > " +
                                        transIds[0]);

        if (requestObj.getApplyFee().equalsIgnoreCase("Y")) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Applying the Fee at SOLSPARK...Fee(Got from SOLSPARK) -- > " +
                                          solsRespObj.getFeeAmount());
          //parse the fee to double
          double debitFee = Double.parseDouble( (solsRespObj.getFeeAmount() != null &&
                                                 !solsRespObj.getFeeAmount().
                                                 trim().equals("") ?
                                                 solsRespObj.getFeeAmount() :
                                                 "0.0"));
          //apply the fee at SOLSPARK
          respObj = financialServiceHome.applyServiceFeeAtSolspark(requestObj.
              getCardNo(), "0.0", requestObj.getServiceId(), debitFee,
              respObj.getCardBalance());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Response Got after applying Fee :: Resp Code -- > " +
                                          respObj.getFeeAmount() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());

          if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE)) {
            //rollback the work
            con.rollback();
            return respObj;
          } //end if

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Applying the Fee at OLTP.....");
          //apply the fee at OLTP
          ServicesResponseObj feeResp = financialServiceHome.
              applyServiceFeeAtOltp(requestObj.getCardNo(),
                                    requestObj.getServiceId(),
                                    respObj.getCardBalance(),
                                    respObj.getTransId(), transIds[1],
                                    requestObj.getDeviceType(),
                                    requestObj.getDeviceId(),
                                    requestObj.getCardAcceptorId(),
                                    requestObj.getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(),
                                    requestObj.getAcquirerId(),
                                    requestObj.getAcqUsrId(),
                                    requestObj.getAcqData1(),
                                    requestObj.getAcqData2(),
                                    requestObj.getAcqData3());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Response After Applying Fee :: Code -- > " +
                                          feeResp.getRespCode() +
                                          " && Desc -- > " +
                                          feeResp.getRespDesc());
          if (!feeResp.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
            return feeResp;
        } //end if
        respObj.setSwitchAuditNo(solsRespObj.getAuditNo());
        //set the ret ref no in response
        respObj.setTransId(transIds[0]);
        respObj.setRespCode(Constants.SUCCESS_CODE);
        respObj.setRespDesc(Constants.SUCCESS_MSG);
        //return the response object
        return respObj;
      } //end if(switchInfo.getSwitchId() != null &&!switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

      //validate the card information
      respObj = CardsServiceHome.getInstance(con).isCardInfoValid(requestObj.
          getCardNo(), requestObj.getAAC(), requestObj.getExpiryDate(),
          requestObj.getAccountNo(), requestObj.getPin(),
          requestObj.getServiceId(), requestObj.getDeviceType(),
          requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
          requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
          requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating Card....");
      //validate the information
      respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.
          getCardNo(), requestObj.getServiceId(),
          requestObj.getApplyFee(), "BE", requestObj.getDeviceType(),
          requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
          requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
          requestObj.getAccountNo(), requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response After validation :: Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //validate response code
      if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting the balance of Card in MCP DB......");
      //build the query to get the Balance
      String balQry = "select card_balance from card_funds where card_no='" +
          requestObj.getCardNo() + "'";
      //get the balance
      String balance = financialServiceHome.getValue(balQry);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Balance Got -- > " + balance);

      if (balance == null) {
        respObj.setRespCode("06");
        respObj.setRespDesc("Balance is not available for Card(" +
                            requestObj.getCardNo() + ")");
        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to log the transction....");
      //log the transaction
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(),
          requestObj.getDeviceType(), Constants.BALANCE_INQUIRY_MSG, null,
          requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
          requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
          requestObj.getAccountNo(), requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transaction logged with ISO Serial No -- > " +
                                      transIds[0]);

      if (requestObj.getApplyFee().trim().equalsIgnoreCase("Y")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Calculating the Fee Amount...");
        //calculate fee amount
        ServicesResponseObj feeResp = CardsServiceHome.getInstance(con).
            getServiceFee(requestObj.getCardNo(), "0.0",
                          requestObj.getServiceId(),
                          requestObj.getDeviceType(), requestObj.getDeviceId(),
                          requestObj.getCardAcceptorId(),
                          requestObj.getCardAcceptNameAndLoc(),
                          requestObj.getMcc(), requestObj.getAccountNo(),
                          requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response after fee calculation :: Code--> " +
                                        feeResp.getRespCode() +
                                        " && desc -- > " + feeResp.getRespDesc() +
                                        " && Fee --> " + feeResp.getFeeAmount());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Parsing the fee in double...");
        //parse the fee amount
        double feeAmt = Double.parseDouble(feeResp.getFeeAmount());

        if (feeAmt > 0) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Checking the card status...");
          //get the card status
          String[] cardStatus = CardsServiceHome.getInstance(con).getValues(
              "select card_status_atm,card_status_pos from cards where card_no='" +
              requestObj.getCardNo() + "'");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Card Status ATM --> " + cardStatus[0] +
                                          " && Card Status POS--> " +
                                          cardStatus[1]);

          if (cardStatus[0].trim().equalsIgnoreCase("E") ||
              cardStatus[1].trim().equalsIgnoreCase("E")) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG), "Going to roll back the work...");
            //rollback work
            con.rollback();

            //set the response
            respObj.setRespCode("57");
            respObj.setRespDesc("Cannot apply fee on Card(" +
                                CardsServiceHome.
                                getInstance(con).maskCardNo(requestObj.
                getCardNo()) + ") having Status E (Restricted Withdrawls)");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG), "Going to log the error transaction...");
            //log the transaction
            transIds = CardsServiceHome.getInstance(con).logTransaction(requestObj.getCardNo(), requestObj.getServiceId(),requestObj.getDeviceType(), null, "0200", "0",respObj.getRespDesc(), "0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Transaction logged with ISO Serial No - > " +
                                            transIds[0] +
                                            " && Trace Audit No -- > " +
                                            transIds[1]);

            return respObj;
          } //end if
        } //end if

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Applying the Fee at OLTP....");
        //apply the fee at OLTP
        respObj = financialServiceHome.applyServiceFeeAtOltp(requestObj.
            getCardNo(), requestObj.getServiceId(), null, null,
            transIds[1], requestObj.getDeviceType(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(),
            requestObj.getAcquirerId(),
            requestObj.getAcqUsrId(),
            requestObj.getAcqData1(),
            requestObj.getAcqData2(),
            requestObj.getAcqData3()
            );
        if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
          return respObj;
      } //end if(requestObj.getApplyFee().trim().equalsIgnoreCase("Y"))
      else {
        respObj.setCardBalance(balance);
      } //end else

      //set the iso serial no in response
      respObj.setTransId(transIds[0]);
      respObj.setRespCode(Constants.SUCCESS_CODE);
      respObj.setRespDesc(Constants.SUCCESS_MSG);
      //return the response received
      return respObj;
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
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end getCardBalance

  /**
   * This method retrieves the specified number of transaction against the supplied card no. This method
   * basically checks how many transaction are done on the given card. The service first gets the
   * switch information and validates the switch for activeness. If the switch is inactive then the
   * transaction is logged and the response is returned to the client. If the switch is in batch mode
   * then validation of the card is performed. Service then translates the request object into the
   * SOLSPARK request object. If the application of the service fee is allowed then the service fee
   * is deducted from the card balance. At the end of the processing response is returned to the client
   * which describes the response code with full description of the processing. In case of successful
   * processing response code is "00" and in case of any error appropiate code with error description
   * is given to the client.
   * @param requestObj ServicesRequestObj -- Request information
   * @throws Exception
   * @return ServicesResponseObj -- Response information
   */


  ////////////////////////////////// Changed /////////////////////////////////////////////////////
  ////  Constants.TRANS_SERVICE replaced with requestObj.getServiceId()                         //
  ////////////////////////////////////////////////////////////////////////////////////////////////
  public ServicesResponseObj getCardTransactions(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      //get the switch info
      SwitchInfoObj switchInfo = financialServiceHome.getCardSwitchInfo(
          requestObj.getCardNo());
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Batch Trans Allowed is 'Y' and Getting the mini statement from SOLSPARK....");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Validating Card....");
        //validate the information
        respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.
            getCardNo(), requestObj.getServiceId(), "N", null,
            requestObj.getDeviceType(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response after validation :: Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        //validate response
        if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
          return respObj;

        //create the solspark handler object
        SolsparkHandler handler = new SolsparkHandler(con);
        SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
        //set the card no from services request object to the solspark request object
        solsReqObj.setCardNum(requestObj.getCardNo());
        solsReqObj.setAac(requestObj.getAAC());
        solsReqObj.setExpDate(requestObj.getExpiryDate());
        solsReqObj.setAccountNum(requestObj.getAccountNo());
        solsReqObj.setMaxRecords("25");
        solsReqObj.setSwitchInfo(switchInfo.getSwitchId());
        solsReqObj.setPageNum("1");
        //get the balance from solspark
        SolsparkResponseObj solsRespObj = handler.retrieveTransactions(
            solsReqObj);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Received From SOLSPARK :: Resp Code -- > " +
                                        solsRespObj.getRespCode() + "\n" +
                                        "Resp Desc -- > " +
                                        solsRespObj.getRespDesc() + "\n" +
                                        "No of Transactions -- > " +
                                        solsRespObj.getTransactionList().size() +
                                        "\n" +
                                        "Trans Id -- > " +
                                        solsRespObj.getTransID());

        if (!solsRespObj.getRespCode().equals(Constants.SUCCESS_CODE)) {
          respObj.setRespCode(financialServiceHome.getSwitchResponseCode(
              switchInfo.getSwitchId(), solsRespObj.getRespCode()));
          //get the response description
          respObj.setRespDesc(financialServiceHome.getSwitchResponseDesc(
              switchInfo.getSwitchId(), solsRespObj.getRespCode()));
          return respObj;
        }
        //set the solspark response information in response
        respObj.setCardBalance(solsRespObj.getBalance());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Going to log the transaction in MCP System....");
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(),
            Constants.TRANS_INQUIRY_MSG, solsRespObj.getTransID(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Transaction logged with ISO Serial No -- > " +
                                        transIds[0]);
        //check whether to apply the fee?
        if (requestObj.getApplyFee().equalsIgnoreCase("Y")) {
          //parse the fee in double
          double debitFee = Double.parseDouble( (solsRespObj.getFeeAmount() != null &&
                                                 !solsRespObj.getFeeAmount().
                                                 trim().equals("") ?
                                                 solsRespObj.getFeeAmount() :
                                                 "0.0"));

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Going to apply the fee at SOLSPARK...(FEE from SOLSPARK) -- > " +
                                          solsRespObj.getFeeAmount());
          //apply the fee at SOLSPARK
          respObj = financialServiceHome.applyServiceFeeAtSolspark(requestObj.
              getCardNo(), "0.0", requestObj.getServiceId(), debitFee,
              respObj.getCardBalance());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Response After Applying Fee :: Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
            //rollback the work
            con.rollback();
            return respObj;
          } //end if

          if (respObj.getCardBalance() == null) {
            respObj.setCardBalance(solsRespObj.getBalance());
          } //end if
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Applying Fee at OLTP....");
          //apply fee at OLTP
          ServicesResponseObj feeResp = financialServiceHome.
              applyServiceFeeAtOltp(requestObj.getCardNo(),
                                    requestObj.getServiceId(),
                                    respObj.getCardBalance(),
                                    respObj.getTransId(), transIds[1],
                                    requestObj.getDeviceType(),
                                    requestObj.getDeviceId(),
                                    requestObj.getCardAcceptorId(),
                                    requestObj.getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(),
                                    requestObj.getAcquirerId(),
                                    requestObj.getAcqUsrId(),
                                    requestObj.getAcqData1(),
                                    requestObj.getAcqData2(),
                                    requestObj.getAcqData3()
                                    );
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Response after Applying Fee :: Code -- > " +
                                          feeResp.getRespCode() +
                                          " && Desc -- > " +
                                          feeResp.getRespDesc());

          if (!feeResp.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
            return feeResp;
        } //end if
        else {
          //get the card balance
          String balance = financialServiceHome.getValue(
              "select card_balance from card_funds where card_no='" +
              requestObj.getCardNo() + "'");
          //set the balance in the response
          respObj.setCardBalance(balance);
        } //end else
        //fill the trans info
        CommonUtilities.fillTransFromSolsParkToServicesReqObj(solsRespObj,
            respObj, Integer.parseInt(solsReqObj.getMaxRecords()));
        //set the ret ref no in response
        respObj.setTransId(transIds[0]);
        respObj.setSwitchTransId(solsRespObj.getTransID());
        respObj.setSwitchAuditNo(solsRespObj.getAuditNo());
        //return the response object
        respObj.setRespCode(Constants.SUCCESS_CODE);
        respObj.setRespDesc(Constants.SUCCESS_MSG);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

      //validate the card information
      respObj = CardsServiceHome.getInstance(con).isCardInfoValid(requestObj.
          getCardNo(), requestObj.getAAC(), requestObj.getExpiryDate(),
          requestObj.getAccountNo(), requestObj.getPin(),
          requestObj.getServiceId(), requestObj.getDeviceType(),
          requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
          requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
          requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating Card....");
      //validate the information
      respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(), null,
          requestObj.getDeviceType(), requestObj.getDeviceId(),
          requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
          requestObj.getMcc(), requestObj.getAccountNo(),
          requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response after validation :: Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //validate response
      if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting the Mini Statement of Card from MCP DB......");
      //get the transaction list from the DB
      Vector transList = null;
      if (requestObj.getTypeOfTrans().equalsIgnoreCase(Constants.
          SUCCESSFUL_TRANS_ONLY)) {
        //Its for Mini Statement get the successful trans only
        transList = financialServiceHome.getCardTransactionList(requestObj.
                getCardNo(), requestObj.getNoOfTrans(), requestObj.getTypeOfTrans(), requestObj.getDateFrom(), requestObj.getDateTo(),
            requestObj.isChkAmount());
      } //end if
      else {
        //Its for transaction history get both type of transactions
        transList = financialServiceHome.getCardTransactionList(requestObj.
            getCardNo(), requestObj.getNoOfTrans(), requestObj.getTypeOfTrans(),
            requestObj.getDateFrom(), requestObj.getDateTo(),
            requestObj.isChkAmount());
      } //end else

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transactions Got -- > " + transList.size());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to log the Transaction....");
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(),
          Constants.TRANS_INQUIRY_MSG, null, requestObj.getDeviceId(),
          requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
          requestObj.getMcc(), requestObj.getAccountNo(),
          requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transaction logged with ISO Serial No -- > " +
                                      transIds[0]);
      if (requestObj.getApplyFee().equalsIgnoreCase("Y")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Applying Fee at OLTP...");
        //apply fee at OLTP
        respObj = financialServiceHome.applyServiceFeeAtOltp(requestObj.
            getCardNo(), requestObj.getServiceId(), null, null, transIds[1],
            requestObj.getDeviceType(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(),
            requestObj.getAcquirerId(),
            requestObj.getAcqUsrId(),
            requestObj.getAcqData1(),
            requestObj.getAcqData2(),
            requestObj.getAcqData3()
            );
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response After Applying Fee :: Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
          return respObj;
      } //end if
      else {
        //get the latest balance
        String balance = CardsServiceHome.getInstance(con).getValue(
            "select card_balance from card_funds where card_no='" +
            requestObj.getCardNo() + "'");
        //set the balance in response
        respObj.setCardBalance(balance);
      } //end else
      //set the iso serial no in response
      respObj.setTransId(transIds[0]);
      //set the response to ok
      respObj.setRespCode(Constants.SUCCESS_CODE);
      respObj.setRespDesc(Constants.SUCCESS_MSG);
      respObj.setTransactionList(transList);
      return respObj;
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
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end getCardMiniStatement

//////////////////////// Changed /////////////////////////////////////////////

  /**
   * This method gets the switch information from the datbase table 'iso-switches'. If the switch
   * is not active or it is offline switch then transaction is logged and the response is returned
   * to the client. The service then validates the card information, if it is found falsy then the
   * transcation is logged and the execution is paused by returning the response with appropiate
   * response code and description. If only successful transaction list is required then the service
   * gets the list of the successful transactions. Otherwise it gets the list of all the transactions.
   * The service then checks whether the application of the service fee is allowed or not. It it is
   * allowed then the service fee is applied to the transaction. At the end of the processing response
   * is returned to the client which contains the response code with the description of the response.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj getACHCardTransactions(ServicesRequestObj
      requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      //get the switch info
      SwitchInfoObj switchInfo = financialServiceHome.getCardSwitchInfo(
          requestObj.getCardNo());
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Switch is active and is in Batch Mode.....");
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "Get ACH Transaction Service is not available for switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      }

      //validate the card information
      respObj = CardsServiceHome.getInstance(con).isCardInfoValid(requestObj.
          getCardNo(), requestObj.getAAC(), requestObj.getExpiryDate(),
          requestObj.getAccountNo(), requestObj.getPin(),
          requestObj.getServiceId(), requestObj.getDeviceType(),
          requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
          requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
          requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating Card....");
      //validate the information
      respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(), null,
          requestObj.getDeviceType(), requestObj.getDeviceId(),
          requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
          requestObj.getMcc(), requestObj.getAccountNo(),
          requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response after validation :: Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //validate response
      if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting the ACH Transaction History/Mini Statement of Card from MCP DB......");
      //get the transaction list from the DB
      Vector transList = null;
      if (requestObj.getTypeOfTrans().equalsIgnoreCase(Constants.
          SUCCESSFUL_TRANS_ONLY)) {
        //Its for Mini Statement get the successful trans only
        transList = financialServiceHome.getACHCardTransactionList(requestObj.
            getCardNo(), requestObj.getNoOfTrans(), requestObj.getTypeOfTrans(), requestObj.getDateFrom(), requestObj.getDateTo(),
            requestObj.isChkAmount());
      } //end if
      else {
        //Its for transaction history get both type of transactions
        transList = financialServiceHome.getACHCardTransactionList(requestObj.
            getCardNo(), requestObj.getNoOfTrans(), requestObj.getTypeOfTrans(),
            requestObj.getDateFrom(), requestObj.getDateTo(),
            requestObj.isChkAmount());
      } //end else

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transactions Got -- > " + transList.size());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to log the Transaction....");
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(),
          Constants.TRANS_INQUIRY_MSG, null, requestObj.getDeviceId(),
          requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
          requestObj.getMcc(), requestObj.getAccountNo(),
          requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transaction logged with ISO Serial No -- > " +
                                      transIds[0]);
      if (requestObj.getApplyFee().equalsIgnoreCase("Y")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Applying Fee at OLTP...");
        //apply fee at OLTP
        respObj = financialServiceHome.applyServiceFeeAtOltp(requestObj.
            getCardNo(), requestObj.getServiceId(), null, null, transIds[1],
            requestObj.getDeviceType(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(),
            requestObj.getAcquirerId(),
            requestObj.getAcqUsrId(),
            requestObj.getAcqData1(),
            requestObj.getAcqData2(),
            requestObj.getAcqData3()
            );
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response After Applying Fee :: Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
          return respObj;
      } //end if
      else {
        //get the latest balance
        String balance = CardsServiceHome.getInstance(con).getValue(
            "select card_balance from card_funds where card_no='" +
            requestObj.getCardNo() + "'");
        //set the balance in response
        respObj.setCardBalance(balance);
      } //end else
      //set the iso serial no in response
      respObj.setTransId(transIds[0]);
      //set the response to ok
      respObj.setRespCode(Constants.SUCCESS_CODE);
      respObj.setRespDesc(Constants.SUCCESS_MSG);
      respObj.setTransactionList(transList);
      return respObj;
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
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end getCardMiniStatement

//////////////////////// Changed /////////////////////////////////////////////////////////

  /**
   * This method gets the VAS transaction list against the supplied card. The service first
   * gets the switch information if the switch is inactive or  batch mode switch then the transaction
   * is logged and the response is returned to the client. The service then validates the card attributes.
   * If there was an error then the transaction is logged and response is returned to the client.
   * The service then checks the type of transaction list which is required if only successful transactions
   * are required then the service fetches the VAS transaction whose transaction status is successful.
   * Otherwise service gets all the transaction regardless of the transaction status.
   * At the end of the processing transaction is logged and the response is returned to the client.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */
//Execute procedure log_vas_trans(pacq_id  = ?, pcard_acpt_id   = ? , pcard_acpt_nameloc = ? , pcard_no = ? , pcard_prg_id = ? , pdevice_id = ?, pdevice_type = ?, pfee_trace_audit = ? , pmcc = ? , porg_trace_audit = ? , prev_trace_audit = ?, ptrace_audit_no = ? , pvas_msg_type = ?, pvas_resp_code = ?, pvas_running_bal  = ?, pvas_service_id = ?, vas_trace_audit = ?, pvas_trasn_amt = ?, pvas_trans_date  = ?, pvas_trans_desc = ?, pvas_trans_time  = ? , pvas_type_id = ?, pfee_amount= ?)
  public ServicesResponseObj getVASCardTransactions(ServicesRequestObj
      requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      //get the switch info
      SwitchInfoObj switchInfo = financialServiceHome.getCardSwitchInfo(
          requestObj.getCardNo());
      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Switch is active and is in Batch Mode.....");
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "Get VAS Transaction History Service is not available for switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      }

      //validate the card information
      respObj = CardsServiceHome.getInstance(con).isCardInfoValid(requestObj.
          getCardNo(), requestObj.getAAC(), requestObj.getExpiryDate(),
          requestObj.getAccountNo(), requestObj.getPin(),
          requestObj.getServiceId(), requestObj.getDeviceType(),
          requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
          requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
          requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating Card....");
      //validate the information
      respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(), null,
          requestObj.getDeviceType(), requestObj.getDeviceId(),
          requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
          requestObj.getMcc(), requestObj.getAccountNo(),
          requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response after validation :: Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //validate response
      if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting the VAS Transaction History/Mini Statement of Card from MCP DB......");
      //get the transaction list from the DB
      Vector transList = null;
      if (requestObj.getTypeOfTrans().equalsIgnoreCase(Constants.
          SUCCESSFUL_TRANS_ONLY)) {
        //Its for Mini Statement get the successful trans only
        transList = financialServiceHome.getVASCardTransactionList(requestObj.getCardNo(),requestObj.getVasAccountType(), requestObj.getNoOfTrans(), requestObj.getTypeOfTrans(), requestObj.getDateFrom(), requestObj.getDateTo(),
                requestObj.isChkAmount());
      } //end if
      else {
        //Its for transaction history get both type of transactions
        transList = financialServiceHome.getVASCardTransactionList(requestObj.getCardNo(),requestObj.getVasAccountType(), requestObj.getNoOfTrans(), requestObj.getTypeOfTrans(),requestObj.getDateFrom(), requestObj.getDateTo(),requestObj.isChkAmount());
      } //end else

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transactions Got -- > " + transList.size());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to log the Transaction....");
      respObj = financialServiceHome.logVASTransaction(requestObj);
      respObj.setTransactionList(transList);
      return respObj;
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
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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
      return respObj;
    } //end catch
  } //end getCardMiniStatement

  /**
   * This method sets the balance of supplied card to zero. The service first gets the switch
   * information if the switch is active and online then the service proceed with further processing.
   * If the switch is inactive then this method logs the transaction and returns the response to the client.
   * If the swtich is in batch mode then the request object is translated into the SOLSPARK request
   * object. Before setting all the cash out the card information is validated. If the card information
   * was found falsy then the service logs the transaction and returns the response to the client.
   * In case of successfully validation of the card the card balance is set to zero. After setting
   * the card balance to zero service checks the application of the service fee. If the application
   * of the service fee is allowed then service fee is applied at OLTP. At the end of the processing
   * service returns the response to the client which describes the processing status with appropiate
   * response code and the description.
   * @param requestObj ServicesRequestObj -- Request Information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */



  ////////////////////////////////////// Changed////////////////////////////////////
  // Constants.ALL_CASH_OUT were replaced with requestObj.getServiceId()
  ///////////////////////////////////////////////////////////////////////////////////
  public ServicesResponseObj allCashOut(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      //get the card switch info
      SwitchInfoObj switchInfo = CardsServiceHome.getInstance(con).
          getCardSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Batch Trans is allowed...Making the card balance 0 at SOLSPARK....");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Validating card....");
        //check for the card balance
        respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.
            getCardNo(), requestObj.getServiceId(), "N", "GHIRBACDE",
            requestObj.getDeviceType(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "After Validation Response Got :: Resp Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());

        if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
          return respObj;

        //make the solspark request object
        SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
        //populate the required fields
        solsReqObj.setCardNum(requestObj.getCardNo());
        solsReqObj.setAac(requestObj.getAAC());
        solsReqObj.setAccountNum(requestObj.getAccountNo());
        solsReqObj.setSwitchInfo(switchInfo.getSwitchId());
        //make the handler
        SolsparkHandler handler = new SolsparkHandler(con);
        //get the balance of card at solspark
        SolsparkResponseObj solsRespObj = null;

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Getting the balance of card at SOLSPARK...");
        //get card balance
        solsRespObj = handler.getCardBalance(solsReqObj);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Got :: Resp ID -- > " +
                                        solsRespObj.getRespCode() + "\n" +
                                        " Desc -- > " + solsRespObj.getRespDesc() +
                                        "\n" + " Balance -- > " +
                                        solsRespObj.getBalance());
        //check the response
        if (!solsRespObj.getRespCode().trim().equals(Constants.SUCCESS_CODE)) {
          respObj.setRespCode(financialServiceHome.getSwitchResponseCode(
              switchInfo.getSwitchId(), solsRespObj.getRespCode()));
          respObj.setRespDesc(financialServiceHome.getSwitchResponseDesc(
              switchInfo.getSwitchId(), solsRespObj.getRespCode()));

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Going to log the error transaction::Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          //log the transaction
          ///////////////// Constansts. All-Cash Repplaced with requestObj.getServiceId()//////////
          String[] transIds = financialServiceHome.logTransaction(requestObj.
              getCardNo(), requestObj.getServiceId(),
              requestObj.getDeviceType(), null, "0200", "0",
              respObj.getRespDesc(), "0", respObj.getRespCode(),
              requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
              requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
              requestObj.getAccountNo(), requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with iso serial no -- > " +
                                          transIds[0] +
                                          " && trace audit no -- > " +
                                          transIds[1]);

          return respObj;
        } //end if

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Calculating the fee amount for Service--> " +
                                        requestObj.getServiceId());
        //calculate the service fee
        ServicesResponseObj feeResp = financialServiceHome.getServiceFee(
            requestObj.getCardNo(), "0.0", requestObj.getServiceId(),
            requestObj.getDeviceType(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(), requestObj.getAccountNo(),
            requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response after Fee Calculation::Code-->" +
                                        feeResp.getRespCode() +
                                        " && Desc-->" + feeResp.getRespDesc() +
                                        "&& Fee-->" + feeResp.getFeeAmount());

        if (feeResp.getRespCode() != null &&
            !feeResp.getRespCode().trim().
            equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Going to log the error transaction::Code -- > " +
                                          feeResp.getRespCode() +
                                          " && Desc -- > " +
                                          feeResp.getRespDesc());
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
              getCardNo(), requestObj.getServiceId(),
              requestObj.getDeviceType(), null, "0200", "0",
              feeResp.getRespDesc(), "0", feeResp.getRespCode(),
              requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
              requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
              requestObj.getAccountNo(), requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with iso serial no -- > " +
                                          transIds[0] +
                                          " && trace audit no -- > " +
                                          transIds[1]);

          return respObj;
        } //end if
        //parse the balance in double
        double requestedAmt = Double.parseDouble(solsRespObj.getBalance());
        //parse the fee amount
        double feeAmount = Double.parseDouble(feeResp.getFeeAmount());

        //get the cash out amount
        String cashOutAmt = (requestedAmt - feeAmount) + "";
        //make the balance to -ive
        String nbal = "-" + requestedAmt;
        //set the amount in solspark request object
        solsReqObj.setAmount(nbal); ;

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Going to add the funds at SOLSPARK-- > " +
                                        nbal);
        //add the -ive balance to cash out
        solsRespObj = handler.addCardFunds(solsReqObj);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response from SOLSPARK:: Trans Id -- > " +
                                        solsRespObj.getTransID() +
                                        " && Code -- > " +
                                        solsRespObj.getRespCode() +
                                        " && Desc -- > " +
                                        solsRespObj.getRespDesc());

        if (!solsRespObj.getRespCode().trim().equals(Constants.SUCCESS_CODE)) {
          respObj.setRespCode(solsRespObj.getRespCode());
          respObj.setRespDesc(solsRespObj.getRespDesc());
          return respObj;
        } //end if(!solsRespObj.getBalance().equals("0"))
        //set the solspark response information in response object
        respObj.setCardBalance(solsRespObj.getBalance());
        respObj.setSwitchTransId(solsRespObj.getTransID());
        respObj.setSwitchAuditNo(solsRespObj.getAuditNo());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Going to log the transaction in MCP System...");
        //log the transaction
        String[] transIds = CardsServiceHome.getInstance(con).logTransaction(
            requestObj.getCardNo(), requestObj.getServiceId(),
            requestObj.getDeviceType(), solsRespObj.getTransID(), "0200",
            (requestedAmt * -1) + "", Constants.ALL_CASH_OUT_MSG, "0", "00",
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Transaction logged with ISO Serial No -- > " +
                                        transIds[0]);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Going to update the  balance at OLTP..");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Getting the card balance..");
        //get the card balance
        String cardBalance = financialServiceHome.getValue(
            "select card_balance from card_funds where card_no='" +
            requestObj.getCardNo() + "'");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Got Card Balance--> " + cardBalance);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Going to update the balance at OLTP using update_funds API...");
        StringBuffer updateFundsQry = new StringBuffer();
        updateFundsQry.append("Execute procedure update_funds (");
        updateFundsQry.append("pcard_no = '" + requestObj.getCardNo() + "',");
        updateFundsQry.append("ptrans_amount = '-" + cardBalance + "',");
        updateFundsQry.append("pservice_id = '" +
                              requestObj.getServiceId() + "',"); // Constats Replaced with request
        updateFundsQry.append("pmti = '0200',");
        updateFundsQry.append("presp_code = '00',");
        updateFundsQry.append("pis_batch = 'N',");
        updateFundsQry.append("do_change_status = 'N')");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Updating Funds at OLTP using update_funds API --- Query--->" +
                                        updateFundsQry);
        String[] results = financialServiceHome.getProcedureVal(updateFundsQry.
            toString());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Got the Results from update_funds :: " +
                                        "response code -- > " + results[0] +
                                        " && response description -- > " +
                                        results[1]);

        //build the query
        String query = "update card_funds set switch_balance=0 where card_no='" +
            requestObj.getCardNo() + "'";
        //execute the query
        CardsServiceHome.getInstance(con).executeQuery(query);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Balance set to 0 in OLTP....");

        //set the ret ref no in response
        respObj.setTransId(transIds[0]);
        //set the cash out amount
        respObj.setCashOutAmount(cashOutAmt);
        //set the card balance
        respObj.setCardBalance("0.00");
        //populate the response with success msg
        respObj.setRespCode(Constants.SUCCESS_CODE);
        respObj.setRespDesc(Constants.SUCCESS_MSG);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

      //validate the card information
      respObj = CardsServiceHome.getInstance(con).isCardInfoValid(requestObj.
          getCardNo(), requestObj.getAAC(), requestObj.getExpiryDate(),
          requestObj.getAccountNo(), requestObj.getPin(),
          requestObj.getServiceId(), requestObj.getDeviceType(),
          requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
          requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
          requestObj.getAcquirerId());
      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Validating Card...");
      //check for the card balance
      respObj = CardsServiceHandler.getInstance(con).validateCard(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(),
          "GHIRBACDE", requestObj.getDeviceType(), requestObj.getDeviceId(),
          requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
          requestObj.getMcc(), requestObj.getAccountNo(),
          requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "After Validation Response Got :: Resp Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());

      if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
        return respObj;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting the card balance..");
      //get the card balance
      String cardBalance = financialServiceHome.getValue(
          "select card_balance from card_funds where card_no='" +
          requestObj.getCardNo() + "'");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Got Card Balance--> " + cardBalance);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Calculating the fee amount for Service--> " +
                                      requestObj.getServiceId());
      //calculate the service fee
      ServicesResponseObj feeResp = financialServiceHome.getServiceFee(
          requestObj.getCardNo(), "0.0", requestObj.getServiceId(),
          requestObj.getDeviceType(), requestObj.getDeviceId(),
          requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
          requestObj.getMcc(), requestObj.getAccountNo(),
          requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response after Fee Calculation::Code-->" +
                                      feeResp.getRespCode() + " && Desc-->" +
                                      feeResp.getRespDesc() + "&& Fee-->" +
                                      feeResp.getFeeAmount());

      if (feeResp.getRespCode() != null &&
          !feeResp.getRespCode().trim().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        feeResp.getRespCode() +
                                        " && Desc -- > " +
                                        feeResp.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", feeResp.getRespDesc(),
            "0", feeResp.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);

        return respObj;
      } //end if

      double requestedAmount = Double.parseDouble(cardBalance);
      double feeAmount = Double.parseDouble(feeResp.getFeeAmount());
      //get the cashout amount
      String cashOutAmount = (requestedAmount - feeAmount) + "";

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to update the  balance at OLTP..");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to update the balance at OLTP using update_funds API...");
      StringBuffer updateFundsQry = new StringBuffer();
      updateFundsQry.append("Execute procedure update_funds (");
      updateFundsQry.append("pcard_no = '" + requestObj.getCardNo() + "',");
      updateFundsQry.append("ptrans_amount = '-" + cashOutAmount + "',");
      updateFundsQry.append("pservice_id = '" + requestObj.getServiceId() +
                            "',");
      updateFundsQry.append("pmti = '0200',");
      updateFundsQry.append("presp_code = '00',");
      updateFundsQry.append("pis_batch = 'N',");
      updateFundsQry.append("do_change_status = 'N')");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Updating Funds at OLTP using update_funds API --- Query--->" +
                                      updateFundsQry);
      String[] results = financialServiceHome.getProcedureVal(updateFundsQry.
          toString());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Got the Results from update_funds :: " +
                                      "response code -- > " + results[0] +
                                      " && response description -- > " +
                                      results[1]);

      //build the query
      String query = null;
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to Update switch balance at OLTP using update_funds API ");
      if (requestObj.getApplyFee() != null &&
          requestObj.getApplyFee().trim().equalsIgnoreCase("Y")) {
        query = "update card_funds set switch_balance=switch_balance-" +
            cashOutAmount + " where card_no='" + requestObj.getCardNo() + "'";
      }
      else {
        query =
            "update card_funds set switch_balance=0 where card_no='" +
            requestObj.getCardNo() + "'";

        cashOutAmount = cardBalance;
      } //end else
      //execute the query
      financialServiceHome.executeQuery(query);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to log the transaction....");
      //log the transaction
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
          "0200", "-" + cashOutAmount, requestObj.getServiceId(), "0", "00",
          requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
          requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
          requestObj.getAccountNo(), requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transaction logged with ISO Serial No -- > " +
                                      transIds[0]);

      if (requestObj.getApplyFee().equalsIgnoreCase("Y")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Applying fee at OLTP...");
        //apply the fee
        respObj = financialServiceHome.applyServiceFeeAtOltp(requestObj.
            getCardNo(), requestObj.getServiceId(), null, null, transIds[1],
            requestObj.getDeviceType(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
            requestObj.getMcc(),
            requestObj.getAcquirerId(),
            requestObj.getAcqUsrId(),
            requestObj.getAcqData1(),
            requestObj.getAcqData2(),
            requestObj.getAcqData3()
            );
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response after applying fee :: Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());

        if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          //rollback all the work
          con.rollback();
          return respObj;
        } //end if
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Balance set to 0 in OLTP....");
      //set the iso serial no in response
      respObj.setTransId(transIds[0]);
      //set the cash out amount
      respObj.setCashOutAmount(cashOutAmount);
      respObj.setCardBalance("0.00");
      //populate the response with success msg
      respObj.setRespCode(Constants.SUCCESS_CODE);
      respObj.setRespDesc(Constants.SUCCESS_MSG);
      return respObj;
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
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end allCashOut

//////////////////////////////// Changed ///////////////////////////////////////////////////////
// Constants.TRANS_INFO_SERVICE were replaced with requestObj.getServiceId()
////////////////////////////////////////////////////////////////////////////////////////////////


  /**
   * This method gets the information of a specific transaction against the given card.
   * The service first checks the transaction category if the category of the transaction is failed
   * i.e. "F" then further execution is stopped and response is returned to the client.
   * The service then finds the card-no against the given transaction serial no if card no
   * was not found or the card-no does not match with the given card no. then further execution of the
   * service is stopped and the response is returned to the client describing the complete response
   * code and it description. The service then fetches the switch information and validates them if the
   * switch is found inactive then the transaction is logged and response is returned to the client.
   * The service then gets the transaction information from the database and populates the resonse object.
   * If an error occurs while getting transaction information then appropiate response is returned to the
   * client. The service then checks the application of the service fee if it is allowed then the fee
   * is charged against the card account. At the end of the processing appropiate resoponse is returned
   * to the client.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj getTransactionInfo(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Trans Cat Received -->" +
                                      requestObj.getTransCat());

      if (requestObj.getTransCat() != null &&
          !requestObj.getTransCat().trim().equalsIgnoreCase("F")) {
        respObj.setRespCode("12");
        respObj.setRespDesc("Invalid Transaction Id prefix");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting card no from transaction....");
      //get the card no
      String cardNo = financialServiceHome.getValue(
          "select card_no from trans_requests where iso_serial_no=" +
          requestObj.getTransId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Got Card No --> " + cardNo);

      if (cardNo == null) {
        respObj.setRespCode("12");
        respObj.setRespDesc("Invalid Transaction Id supplied");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Invalid transaction id supplied..");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if

      if (requestObj.getCardNo() != null &&
          !requestObj.getCardNo().trim().equals("")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Checking for the Card No match..Card No from Req --> " +
                                        requestObj.getCardNo() +
                                        " && Card No From Trans --> " + cardNo);
        //match card no
        if (!cardNo.trim().equalsIgnoreCase(requestObj.getCardNo().trim())) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Card Nos don't match...");

          respObj.setRespCode("12");
          respObj.setRespDesc("Invalid Transaction Id supplied. Supplied Card No does not match with the Card No involved in transaction.");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Invalid transaction id supplied..");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Going to log the error transaction::Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
              getCardNo(), requestObj.getServiceId(),
              requestObj.getDeviceType(), null, "0200", "0",
              respObj.getRespDesc(), "0", respObj.getRespCode(),
              requestObj.getAccountNo(), requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with iso serial no -- > " +
                                          transIds[0] +
                                          " && trace audit no -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);

          return respObj;
        } //end if
      } //end if
      else
        requestObj.setCardNo(cardNo);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting card Switch info....");
      //get the switch information
      SwitchInfoObj switchInfo = financialServiceHome.getCardSwitchInfo(
          requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Switch is active and in batch mode....");
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "Get Transaction Information service is not supported at switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if (switchInfo.getSwitchId() != null &&!switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting the transaction information....");
      //get the transaction information
      respObj = financialServiceHome.getTransactionInfo(requestObj.getTransId(),
          requestObj.getCardNo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response after Get Transaction Info :: Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());

      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to log the transaction");
      //log the transaction
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(),
          Constants.TRANS_INFO_MSG, null, requestObj.getDeviceId(),
          requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
          requestObj.getMcc(), requestObj.getAccountNo(),
          requestObj.getAcquirerId());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transaction logged with i_serial_no -- > " +
                                      transIds[0] +
                                      " && trace_audit_no-- > " + transIds[0]);

      if (requestObj.getApplyFee().trim().equalsIgnoreCase("Y")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Validating Card....");
        //validate the card
        ServicesResponseObj validRespObj = CardsServiceHandler.getInstance(con).
            validateCard(requestObj.getCardNo(), requestObj.getServiceId(),
                         requestObj.getApplyFee(), null,
                         requestObj.getDeviceType(), requestObj.getDeviceId(),
                         requestObj.getCardAcceptorId(),
                         requestObj.getCardAcceptNameAndLoc(),
                         requestObj.getMcc(), requestObj.getAccountNo(),
                         requestObj.getAcquirerId());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response after validation :: Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " + respObj.getRespDesc());
        if (!validRespObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
          return validRespObj;

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Applying Fee at OLTP...");
        //apply the service fee
        ServicesResponseObj feeResp = financialServiceHome.
            applyServiceFeeAtOltp(requestObj.getCardNo(),
                                  requestObj.getServiceId(), null, null,
                                  transIds[1], requestObj.getDeviceType(),
                                  requestObj.getDeviceId(),
                                  requestObj.getCardAcceptorId(),
                                  requestObj.getCardAcceptNameAndLoc(),
                                  requestObj.getMcc(),
                                  requestObj.getAcquirerId(),
                                  requestObj.getAcqUsrId(),
                                  requestObj.getAcqData1(),
                                  requestObj.getAcqData2(),
                                  requestObj.getAcqData3()
                                  );
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response after applying fee :: Code -- > " +
                                        feeResp.getRespCode() +
                                        " && Desc -- > " + feeResp.getRespDesc());
        if (!feeResp.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
          return feeResp;

        respObj.setFeeAmount(feeResp.getFeeAmount());
        respObj.setCardBalance(feeResp.getCardBalance());
      } //end if
      else {
        //get the latest balance
        String balance = CardsServiceHome.getInstance(con).getValue(
            "select card_balance from card_funds where card_no='" +
            requestObj.getCardNo() + "'");
        //set the balance in response
        respObj.setCardBalance(balance);
      } //end else
      //set the trans id
      respObj.setTransId(transIds[0]);
      return respObj;
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
      respObj.setStkTrace(trc);
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Going to log the error transaction::Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //log the transaction
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end getTransactionInfo

  public ServicesResponseObj billPaymentTransactionStatement(
          ServicesRequestObj requestObj) throws
          Exception {

      ServicesResponseObj respObj = new ServicesResponseObj();
      FinancialServiceHome financialServiceHome = FinancialServiceHome.
              getInstance(con);
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Mehtod for getting bill payment transaction statement...");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Validating Card....");
          respObj = financialServiceHome.isCardInfoValid(requestObj.getCardNo(),
                  requestObj.getAAC(),
                  requestObj.getExpiryDate(),
                  requestObj.getAccountNo(),
                  requestObj.getPin(),
                  requestObj.getServiceId(),
                  requestObj.getDeviceType(),
                  requestObj.getDeviceId(),
                  requestObj.getCardAcceptorId(),
                  requestObj.
                  getCardAcceptNameAndLoc(),
                  requestObj.getMcc(),
                  requestObj.getAcquirerId());
          if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
              return respObj;
          }

          //validate the card
          ServicesResponseObj validRespObj = CardsServiceHandler.getInstance(con).validateCard(
                  requestObj.getCardNo(),
                  requestObj.getServiceId(),
                  requestObj.getApplyFee(),
                  null,
                  requestObj.getDeviceType(),
                  requestObj.getDeviceId(),
                  requestObj.getCardAcceptorId(),
                  requestObj.getCardAcceptNameAndLoc(),
                  requestObj.getMcc(),
                  requestObj.getAccountNo(),
                  requestObj.getAcquirerId());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Response after validation :: Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          if (!validRespObj.getRespCode().equalsIgnoreCase(Constants.
                  SUCCESS_CODE))
              return validRespObj;

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Getting card Switch info....");
          //get the switch information
          SwitchInfoObj switchInfo = financialServiceHome.getCardSwitchInfo(
                  requestObj.getCardNo());

          if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
              respObj.setRespCode("91");
              respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                                  ") is not active for Card (" +
                                  financialServiceHome.
                                  maskCardNo(requestObj.getCardNo()) + ")");

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Going to log the error transaction::Code -- > " +
                                              respObj.getRespCode() +
                                              " && Desc -- > " +
                                              respObj.getRespDesc());
              //log the transaction
              String[] transIds = financialServiceHome.logTransaction(
                      requestObj.
                      getCardNo(), requestObj.getServiceId(),
                      requestObj.getDeviceType(), null,
                      "0200", "0", respObj.getRespDesc(), "0",
                      respObj.getRespCode(),
                      requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                      requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                      requestObj.getAccountNo(), requestObj.getAcquirerId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Transaction logged with iso serial no -- > " +
                                              transIds[0] +
                                              " && trace audit no -- > " +
                                              transIds[1]);
              //set the trans id in response
              respObj.setTransId(transIds[0]);
              return respObj;
          } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

          if (switchInfo.getSwitchId() != null &&
              !switchInfo.getSwitchId().trim().equals("") &&
              switchInfo.isBatchTransAllowed()) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Switch is active and in batch mode....");
              respObj.setRespCode("40");
              respObj.setRespDesc(
                      "Bill Payment Transaction Statement service is not supported at switch(" +
                      switchInfo.getSwitchId() + ")");

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Going to log the error transaction::Code -- > " +
                                              respObj.getRespCode() +
                                              " && Desc -- > " +
                                              respObj.getRespDesc());
              //log the transaction
              String[] transIds = financialServiceHome.logTransaction(
                      requestObj.
                      getCardNo(), requestObj.getServiceId(),
                      requestObj.getDeviceType(), null,
                      "0200", "0", respObj.getRespDesc(), "0",
                      respObj.getRespCode(),
                      requestObj.getAccountNo(), requestObj.getAcquirerId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Transaction logged with iso serial no -- > " +
                                              transIds[0] +
                                              " && trace audit no -- > " +
                                              transIds[1]);
              //set the trans id in response
              respObj.setTransId(transIds[0]);

              return respObj;
          } //end if (switchInfo.getSwitchId() != null &&!switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Date From--->" +
                                          requestObj.getDateFrom() +
                                          "<---Date To--->" +
                                          requestObj.getDateTo());
          if(requestObj.getDateFrom() != null){
            String dateFrom = null;
            try {
                dateFrom = CommonUtilities.convertDateFormat(Constants.
                        DATE_FORMAT, Constants.WEB_DATE_FORMAT,
                        requestObj.getDateFrom());
            } catch (Exception fromEx) {
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "Exception in converting From Date to required format--->" + fromEx);
                dateFrom = null;
            }
            requestObj.setDateFrom(dateFrom);
          }

          if(requestObj.getDateTo() != null){
            String dateTo = null;
            try {
                dateTo = CommonUtilities.convertDateFormat(Constants.
                        DATE_FORMAT, Constants.WEB_DATE_FORMAT,
                        requestObj.getDateTo());
            } catch (Exception toEx) {
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "Exception in converting To Date to required format--->" + toEx);
                dateTo = null;
            }
            requestObj.setDateTo(dateTo);
        }

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "After conversion, Date From--->" +
                                          requestObj.getDateFrom() +
                                          "<---Date To--->" +
                                          requestObj.getDateTo());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Getting the transaction information....");
          //get the transaction information
          Vector bpTrans = financialServiceHome.getBillPaymentTransactions(
                  requestObj);

          respObj.setRespCode("00");
          respObj.setRespDesc("OK");
          respObj.setBpTransactionList(bpTrans);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Going to log the transaction");
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
                  getCardNo(), requestObj.getServiceId(),
                  requestObj.getDeviceType(),
                  Constants.BP_TRANS_INFO_MSG, null,
                  requestObj.getDeviceId(),
                  requestObj.getCardAcceptorId(),
                  requestObj.getCardAcceptNameAndLoc(),
                  requestObj.getMcc(),
                  requestObj.getAccountNo(),
                  requestObj.getAcquirerId());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Transaction logged with i_serial_no -- > " +
                                          transIds[0] +
                                          " && trace_audit_no-- > " +
                                          transIds[0]);

          if (requestObj.getApplyFee().trim().equalsIgnoreCase("Y")) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Applying Fee at OLTP...");
              //apply the service fee
              ServicesResponseObj feeResp = financialServiceHome.
                                            applyServiceFeeAtOltp(requestObj.
                      getCardNo(),
                      requestObj.getServiceId(), null, null,
                      transIds[1], requestObj.getDeviceType(),
                      requestObj.getDeviceId(),
                      requestObj.getCardAcceptorId(),
                      requestObj.getCardAcceptNameAndLoc(),
                      requestObj.getMcc(),
                      requestObj.getAcquirerId(),
                      requestObj.getAcqUsrId(),
                      requestObj.getAcqData1(),
                      requestObj.getAcqData2(),
                      requestObj.getAcqData3()
                                            );
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Response after applying fee :: Code -- > " +
                                              feeResp.getRespCode() +
                                              " && Desc -- > " +
                                              feeResp.getRespDesc());
              if (!feeResp.getRespCode().equalsIgnoreCase(Constants.
                      SUCCESS_CODE))
                  return feeResp;

              respObj.setFeeAmount(feeResp.getFeeAmount());
              respObj.setCardBalance(feeResp.getCardBalance());
          } else { //end if
              //get the latest balance
              String balance = CardsServiceHome.getInstance(con).getValue(
                      "select card_balance from card_funds where card_no='" +
                      requestObj.getCardNo() + "'");
              //set the balance in response
              respObj.setCardBalance(balance);
          } //end else
          //set the trans id
          respObj.setTransId(transIds[0]);
          return respObj;
      } catch (Exception exp) { //end try
          String trc = CommonUtilities.getStackTrace(exp);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_SEVERE),
                                          "Exception -- > " + exp.getMessage() +
                                          "Stack Trace -- > " + trc);
          try {
              if (con != null)
                  con.rollback();
          } catch (Exception ex) {} //end try
          //set the response code to system error
          respObj.setRespCode("96");
          respObj.setRespDesc("System Error-->" + trc);
          //respObj.setSysDesc("System Error, Check logs for more information...");
          respObj.setExcepMsg(exp.getMessage());
          respObj.setStkTrace(trc);
          respObj.setLogFilePath("More information can be found at--->" +
                                 Constants.EXACT_LOG_PATH);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                          "Going to log the error transaction::Code -- > " +
                                          respObj.getRespCode() +
                                          " && Desc -- > " +
                                          respObj.getRespDesc());
          //log the transaction
          String[] transIds = financialServiceHome.logTransaction(requestObj.
                  getCardNo(), requestObj.getServiceId(),
                  requestObj.getDeviceType(), null,
                  "0200", "0", respObj.getRespDesc(), "0",
                  respObj.getRespCode(),
                  requestObj.getDeviceId(),
                  requestObj.getCardAcceptorId(),
                  requestObj.getCardAcceptNameAndLoc(),
                  requestObj.getMcc(),
                  requestObj.getAccountNo(),
                  requestObj.getAcquirerId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                          "Transaction logged with iso serial no -- > " +
                                          transIds[0] +
                                          " && trace audit no -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);

          return respObj;
      } //end catch
  } //end


  /**
   * This method is used to make the pre authorization of a transaction.
   * The service first fetches the switch information and validates them if the switch is found inactive
   * or the switch is a batch mode switch then the transaction is logged and response is returned to
   * the client. The service then do the pre-authorization of the request and returns the response to
   * the client.
   * @param requestObj ServicesRequestObj -- Request Information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */

  /////////////////////////// Changed ////////////////////////////////////////////////////////////
  // Constants.PRE_AUTHORIZATION were replaced with requestObj.getServiceId()                  //
  ////////////////////////////////////////////////////////////////////////////////////////////////
  public ServicesResponseObj preAuthorization(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Switch Information...");
      //get the switch info
      SwitchInfoObj switchInfo = CardsServiceHome.getInstance(con).
          getCardSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      //validate switch info
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Switch is active and is in Batch Mode.....");
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "Pre-Authorization Service is not available for switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if(switchInfo.getSwitchId() != null &&!switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Calling Pre Auth API...");
      //pass the call
      respObj = financialServiceHome.preAuthorization(requestObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Response Received --- Response Code--->" + respObj.getRespCode() + "<---ISO Serial No--->" + respObj.getTransId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Generating Pre Auth ID for ISO Serial No--->" + respObj.getTransId());

      String preAuthId = financialServiceHome.generatePreAuthId(respObj.getTransId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Pre Auth ID got--->" + preAuthId);
      if(preAuthId == null || preAuthId.trim().length() == 0){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Invalid Pre Auth ID generated--->" + preAuthId);
      }else{
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Updating Pre Auth ID in trans_requests & iso_finance_msgs...");
          respObj.setPreAuthId(preAuthId);
          financialServiceHome.updatePreauthId(respObj.getTransId(),respObj.getPreAuthId());
      }
      //return response
      return respObj;
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
      respObj.setStkTrace(trc);
      respObj.setLogFilePath("More information can be found at--->" +
                             Constants.EXACT_LOG_PATH);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Going to log the error transaction::Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //log the transaction
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end preAuthorization

  /**
   * This method is used to settle down the pre authroization transaction.
   * The service first fetches the switch information and validates them if the switch is found inactive
   * or the batch mode switch then the transaction is logged and response is returned to the client.
   * The service then perofrms the force post-authorization to the given card and return the compiled
   * response to the client.
   * @param requestObj ServicesRequestObj -- Request Information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */

  /////////////// Changed /////////////////////////////////////////////////////////////////
  /// Constants.FORCE_POST_AUTHORIZATION were replaced with requestObj.getServiceId()    //
  /////////////////////////////////////////////////////////////////////////////////////////

  public ServicesResponseObj forcePostTransaction(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    //get the instance of financial service home
    FinancialServiceHome financialServiceHome = FinancialServiceHome.
        getInstance(con);
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Switch Information...");
      //get the switch info
      SwitchInfoObj switchInfo = CardsServiceHome.getInstance(con).
          getCardSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      //validate switch info
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Switch is active and is in Batch Mode.....");
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "Force Post Transaction Service is not available for switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if(switchInfo.getSwitchId() != null &&!switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Calling Force Post Transaction API...");
      //pass the call
      respObj = financialServiceHome.focePostTransaction(requestObj);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Returning Response Received....");
      //return the response
      return respObj;
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
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    } //end catch
  } //end forcePostAuthorization

  /**
   * This method is used to apply a specific service fee to a card.  The service first creates the
   * object of the Transfer API and debits the service fee at the the SOLSPARK switch. SOLSPARK API
   * returns a response which is checked whehter the processing was OK. At the end of the processing
   * compiled response is returned to the client.
   * @param requestObj ServicesRequestObj -- Request Information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */

  public ServicesResponseObj applyServiceFee(ServicesRequestObj requestObj) throws
      Exception {

    ServicesResponseObj respObj = new ServicesResponseObj();
    FinancialServiceHome financialServiceHome = null;
    try {
      financialServiceHome = FinancialServiceHome.getInstance(this.con);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for applying fee for service--->" +
                                      requestObj.getServiceId() +
                                      "<---Card--->" +
                                      requestObj.getCardNo());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Getting Switch Information...");
      //get the switch info
      SwitchInfoObj switchInfo = CardsServiceHome.getInstance(con).
          getCardSwitchInfo(requestObj.getCardNo());

      if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
        respObj.setRespCode("91");
        respObj.setRespDesc("Switch(" + switchInfo.getSwitchId() +
                            ") is not active for Card (" +
                            financialServiceHome.
                            maskCardNo(requestObj.getCardNo()) + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())

      //validate switch info
      if (switchInfo.getSwitchId() != null &&
          !switchInfo.getSwitchId().trim().equals("") &&
          switchInfo.isBatchTransAllowed()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Switch is active and is in Batch Mode.....");
        respObj.setRespCode("40");
        respObj.setRespDesc(
            "Apply Service Fee is not available for switch(" +
            switchInfo.getSwitchId() + ")");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(),
            requestObj.getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
            "0", respObj.getRespCode(), requestObj.getDeviceId(),
            requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if(switchInfo.getSwitchId() != null &&!switchInfo.getSwitchId().trim().equals("") && switchInfo.isBatchTransAllowed())

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to check whether debits are allowed or not...");
      //call check debit
      respObj = financialServiceHome.checkDebitAllowed(requestObj, "B", "Y",
          "N");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Response received from Check debit -- > " +
                                      respObj.getRespCode() +
                                      " && Desc -- > " + respObj.getRespDesc() +
                                      "&& Fee -- > " + respObj.getFeeAmount());

      if (respObj.getRespCode() != null &&
          !respObj.getRespCode().trim().equals(Constants.SUCCESS_CODE)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Going to log the error transaction::Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = financialServiceHome.logTransaction(requestObj.
            getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
            "0200", "0", respObj.getRespDesc(), "0", respObj.getRespCode(),
            requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
            requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
            requestObj.getAccountNo(), requestObj.getAcquirerId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with iso serial no -- > " +
                                        transIds[0] +
                                        " && trace audit no -- > " +
                                        transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);

        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to apply the fee at OLTP...");

      respObj = financialServiceHome.applyServiceFeeAtOltp(requestObj.getCardNo(),
          requestObj.getServiceId(), null, null, null, requestObj.getDeviceType(),
          requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
          requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
          requestObj.getAcquirerId(), requestObj.getAcqUsrId(),
          requestObj.getAcqData1(), requestObj.getAcqData2(),
          requestObj.getAcqData3());

      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
        //rollback the work
        con.rollback();
        //return response object
        return respObj;
      }
      else {
        respObj.setTransId(respObj.getFeeSerialNo());
      }
    }
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
      String[] transIds = financialServiceHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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

      return respObj;
    }
    return respObj;
  } //end applyServiceFee

  public ServicesResponseObj logChargeBackCase(ServicesRequestObj requestObj) throws Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();
    FinancialServiceHome servHome = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for logging charge back case--->" + requestObj.getChargeBackCaseId());
      servHome = FinancialServiceHome.getInstance(this.con);
      respObj = servHome.processChargeBackTransaction(requestObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for logging charge back case, Response received from OLTP --->" + respObj.getRespCode()
                                      + "<--Desctiption--->" + respObj.getRespDesc());
      return respObj;
    }catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage() +
                                      "Stack Trace -- > " + trc);
      try {
        if (con != null)
          con.rollback();
      }catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception in rolling back transaction...");
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_WARNING),
                                      "Exception in logging charge back case--->" + exp);
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
      String[] transIds = servHome.logTransaction(requestObj.
          getCardNo(), requestObj.getServiceId(), requestObj.getDeviceType(), null,
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
      return respObj;
    }
  }

  public ServicesResponseObj processChargeBackCase(ServicesRequestObj requestObj) throws Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();
    FinancialServiceHome servHome = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for confirming charge back case--->" +
                                      requestObj.getChargeBackCaseId());
      servHome = FinancialServiceHome.getInstance(this.con);
      respObj = servHome.confirmChargeBackTransaction(requestObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for confirming charge back case, Response received from OLTP --->" +
                                      respObj.getRespCode()
                                      + "<--Desctiption--->" +
                                      respObj.getRespDesc());
      return respObj;
    }
    catch (Exception exp) {
      String trc = CommonUtilities.getStackTrace(exp);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage() +
                                      "Stack Trace -- > " + trc);
      try {
        if (con != null)
          con.rollback();
      }
      catch (Exception ex) {}

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_WARNING),
                                      "Exception in confirming charge back case--->" +
                                      exp);
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
      String[] transIds = servHome.logTransaction(requestObj.
                                                  getCardNo(),
                                                  requestObj.getServiceId(),
                                                  requestObj.getDeviceType(), null,
                                                  "0200", "0",
                                                  respObj.getRespDesc(), "0",
                                                  respObj.getRespCode(),
                                                  requestObj.getDeviceId(),
                                                  requestObj.getCardAcceptorId(),
                                                  requestObj.
                                                  getCardAcceptNameAndLoc(),
                                                  requestObj.getMcc(),
                                                  requestObj.getAccountNo(),
                                                  requestObj.getAcquirerId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),
                                      "Transaction logged with iso serial no -- > " +
                                      transIds[0] +
                                      " && trace audit no -- > " + transIds[1]);
      //set the trans id in response
      respObj.setTransId(transIds[0]);
      return respObj;
    }
  }

  private int checkTransferInvocationDate(String providedDate) {
      /**
       * 1 -- Provided Date < Current Date - 7
       * 2 -- Provided Date > Current Date - 7 && Provided Date < Current Date
       * 3 -- Provided Date > Current Date
       * 4 -- Provided Date == Current Date
       */

      java.util.Date transDate = null;
      java.util.Date currentDate = null;
      java.util.Date prevAllowedDate = null;

      String currDateStrVal = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for checking transfer invocation date--->" + providedDate);
          if (providedDate != null &&
              !providedDate.trim().equals("")) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Converting transfer Date....");
              transDate = CommonUtilities.getFormatDate(Constants.
                      WEB_DATE_FORMAT,
                      providedDate);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Getting current date...");

              currentDate = DateUtil.getCurrentDateValue(Constants.
                      WEB_DATE_FORMAT);
              currDateStrVal = CommonUtilities.getFormatedDate(currentDate,
                      Constants.WEB_DATE_FORMAT);

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "Getting 7-day previous date...");

              prevAllowedDate = CommonUtilities.getParsedDate(CommonUtilities.
                      addDaysInDate(Constants.WEB_DATE_FORMAT, currDateStrVal,
                                    "-7",
                                    Constants.WEB_DATE_FORMAT,
                                    java.util.GregorianCalendar.DATE),
                      Constants.WEB_DATE_FORMAT);

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "comparing dates...");

              if (transDate.before(prevAllowedDate)) {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                          "Provided date is before 7-Day Previous Allowed date...");
                  return 1;
              } else if (transDate.after(prevAllowedDate) &&
                         transDate.before(currentDate)) {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG), "Provided date is after 7-Day Previous Allowed date but before Current Date...");
                  return 2;
              } else if (transDate.after(currentDate)) {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG), "Provided date is after Current Date...");
                  return 3;
              } else {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG), "Provided date is equal to Current Date...");
                  return 4;
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_WARNING), "Exception in chekcing transfer invocation date--->" + ex);
      }
      return -1;
  }


} //end FinancialServiceHandler
