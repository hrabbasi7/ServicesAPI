package com.i2c.services.home;

import java.sql.*;

import com.i2c.api.hsm.HSMResponse;
import com.i2c.service.hsm.HSMService;
import com.i2c.services.*;
import com.i2c.services.base.*;
import com.i2c.services.util.*;
import java.util.Vector;
import java.util.Properties;

/**
 * <p>Title: VasServiceHome: A class which provides the Value Added Services</p>
 * <p>Description: <p> This class provides the value added services (VAS)0 to it clients such as reserving
 * some amount of funds from the given card account for special usage or getting the list of the cards
*  which have been linked with a card</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class VasServiceHome extends ServicesBaseHome {

  /**
   * Private constructor
   */
  private VasServiceHome() {
  }

  /**
   * This static method acts as the Factory method. It creates the object of the VasServiceHome class
   * and returns it reference to the calling method.
   * @param _con Connection
   * @return VasServiceHome
   */

  public static VasServiceHome getInstance(Connection _con) {
    VasServiceHome home = new VasServiceHome();

    home.con = _con;
    return home;
  } //end getInstance


  /**
   * This method is used to check whether the given card attributes are valid for the given card no.
   * At the end of the processig it returns response object which describes the status of the processing
   * with appropiate response code and description. It calls the method "isCardInfoValid" for checking
   * the card attributes for their validation.

   * @param cardNo String
   * @param AAC String
   * @param expDate String
   * @param accountNo String
   * @param PIN String
   * @param serviceId String
   * @param deviceType String
   * @param deviceId String
   * @param cardAcceptorId String
   * @param cardAcceptorNameAndLoc String
   * @param mcc String
   * @param acquirerId String
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj isCardInfoValid(String cardNo, String AAC,
                                             String expDate, String accountNo,
                                             String PIN, String serviceId,
                                             String deviceType, String deviceId,
                                             String cardAcceptorId,
                                             String cardAcceptorNameAndLoc,
                                             String mcc, String acquirerId) throws
      Exception {
    return isCardInfoValid(cardNo, AAC, expDate, accountNo, PIN, true,
                           serviceId, deviceType, deviceId, cardAcceptorId,
                           cardAcceptorNameAndLoc, mcc, acquirerId);
  } //end isCardInfoValid


  /**
   * This method is used to check whether the given card attributes are valid for the given card no.
   * At the end of the processig it returns response object which describes the status of the processing
   * with appropiate response code and description.
   * The mehtod first checks the card no whether is is null or empty. If it is then it returns the
   * response code 14 with proper description of the response code. It then checks whether the given
   * card no exist in the database. If it is not then it respond with response code 14 which indicates
   * that given card no does not exists in the database. The method then compares the AAC with the
   * card access code fetched from the database. If AAC code mismatch with the card access code then
   * execution is paused and response is returned to the client. Similary expiry date and PIN offset
   * of the given card are validated. After inital validation the method calls the HSM Manager for the
   * validiation of the PIN of the given card no. If any error occurs during the processing exception
   * is returned to the client which describes the error in full detail.
   * @param cardNo String
   * @param AAC String
   * @param expDate String
   * @param accountNo String
   * @param PIN String
   * @param validatePIN boolean
   * @param serviceId String
   * @param deviceType String
   * @param deviceId String
   * @param cardAcceptorId String
   * @param cardAcceptorNameAndLoc String
   * @param mcc String
   * @param acquirerId String
   * @throws Exception
   * @return ServicesResponseObj
   */
  public ServicesResponseObj isCardInfoValid(String cardNo, String AAC,
                                             String expDate, String accountNo,
                                             String PIN, boolean validatePIN,
                                             String serviceId,
                                             String deviceType, String deviceId,
                                             String cardAcceptorId,
                                             String cardAcceptorNameAndLoc,
                                             String mcc, String acquirerId) throws
      Exception {
    //make response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Args Received :: Card No -- > " + cardNo +
                                    "\n" +
                                    "AAC -- > " + AAC + "\n" + "Exp Date -- > " +
                                    expDate + "\n" + "Account No -- > " +
                                    accountNo + " && PIN --> " + PIN +
                                    " && validatePIN-->" + validatePIN +
                                    " && Service Id--> " + serviceId +
                                    " && Device Type--> " + deviceType);
    if (cardNo == null || cardNo.trim().length() == 0){
      respObj.setRespCode("14");
      respObj.setRespDesc("Invalid Card Number provided");
      return respObj;
    }

    try {
      String card = getValue("select card_no from cards where card_no='" +
                             cardNo + "'");
      if (card == null) {
        respObj.setRespCode("14");
        respObj.setRespDesc("Card No(" + maskCardNo(cardNo) +
                            ") does not exist");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Card No (" + cardNo +
                                        ") does not exist in the database::logging error transaction:: Code -- > " +
                                        respObj.getRespCode() +
                                        " && Desc -- > " +
                                        respObj.getRespDesc());
        //log the transaction
        String[] transIds = logTransaction(cardNo, serviceId, deviceType, null,
                                           "0200", "0", respObj.getRespDesc(),
                                           "0", respObj.getRespCode(), deviceId,
                                           cardAcceptorId,
                                           cardAcceptorNameAndLoc, mcc,
                                           accountNo, acquirerId);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Transaction logged with ISO Serial No - > " +
                                        transIds[0] +
                                        " && Trace Audit No -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if
      if (AAC != null && !AAC.equalsIgnoreCase("")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Validating AAC...");
        //validate the aac
        String result = getValue(
            "select card_access_code from cards where card_no='" + cardNo +
            "' and card_access_code='" + AAC + "'");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Got AAC -- > " + result);
        if (result == null) {
          respObj.setRespCode("EB");
          respObj.setRespDesc("Invalid AAC supplied for Card(" +
                              maskCardNo(cardNo) + ")");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Invalid AAC supplied for Card(" + cardNo + ")");
          //log the transaction
          String[] transIds = logTransaction(cardNo, serviceId, deviceType, null,
                                             "0200", "0", respObj.getRespDesc(),
                                             "0", respObj.getRespCode(),
                                             deviceId, cardAcceptorId,
                                             cardAcceptorNameAndLoc, mcc,
                                             accountNo, acquirerId);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with ISO Serial No - > " +
                                          transIds[0] +
                                          " && Trace Audit No -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);
          return respObj;
        } //end if
      } //end if

      if (expDate != null && !expDate.equalsIgnoreCase("")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Validating Expiry Date...");
        //get the expiry date
        String result = getValue("select expiry_on from cards where card_no='" +
                                 cardNo + "'");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Got Expiry Date -- > " + result);
        if (result != null) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Formatting expiry date to -- > " +
                                          Constants.EXP_DATE_FORMAT);
          //format the expiry date
          String expiryDate = CommonUtilities.convertDateFormat(Constants.
              EXP_DATE_FORMAT, Constants.DATE_FORMAT, result);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Expiry Date After Conversion -- > " + expiryDate);

          if (!expiryDate.equalsIgnoreCase(expDate)) {
            respObj.setRespCode("SB");
            respObj.setRespDesc("Invalid Card Expiry Date supplied for Card(" +
                                maskCardNo(cardNo) + ")");

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Invalid Card Expiry Date supplied for Card(" +
                                            cardNo + ")");
            //log the transaction
            String[] transIds = logTransaction(cardNo, serviceId, deviceType, null,
                                               "0200", "0", respObj.getRespDesc(),
                                               "0", respObj.getRespCode(),
                                               deviceId, cardAcceptorId,
                                               cardAcceptorNameAndLoc, mcc,
                                               accountNo, acquirerId);

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Transaction logged with ISO Serial No - > " +
                                            transIds[0] +
                                            " && Trace Audit No -- > " +
                                            transIds[1]);
            //set the trans id in response
            respObj.setTransId(transIds[0]);
            return respObj;
          } //end if
        } //end if
      } //end if

      if (accountNo != null && !accountNo.trim().equalsIgnoreCase("")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Validating Account No...");
        //validate the account no
        String result = getValue("select a.account_number from cards c, card_accounts a where c.card_no = a.card_no and c.card_no='" +
                                 cardNo + "' and a.account_number='" +
                                 accountNo + "'");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Account No Got -- > " + result);
        if (result == null) {
          respObj.setRespCode("EC");
          respObj.setRespDesc("Invalid Account No Supplied for Card(" +
                              maskCardNo(cardNo) + ")");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Invalid Account No Supplied for Card(" +
                                          cardNo + ")");
          //log the transaction
          String[] transIds = logTransaction(cardNo, serviceId, deviceType, null,
                                             "0200", "0", respObj.getRespDesc(),
                                             "0", respObj.getRespCode(),
                                             deviceId, cardAcceptorId,
                                             cardAcceptorNameAndLoc, mcc,
                                             accountNo, acquirerId);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with ISO Serial No - > " +
                                          transIds[0] +
                                          " && Trace Audit No -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);
          return respObj;
        } //end if
      } //end if
      if (PIN != null && !PIN.trim().equalsIgnoreCase("") && validatePIN) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Validating PIN....Getting PIN Offset...");
        //get the pin offset of card
        String pinOffset = getValue(
            "select pin_offset from cards where card_no='" + cardNo + "'");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Got PIN Offset -- > " + pinOffset);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Validating the PIN at HSM....");

        //validate the PIN at HSM
        HSMService mgr = new HSMService(Constants.HSM_LOG_FILE_PATH,Constants.HSM_WRPR_FILE_PATH,con);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Method for validating the pin from HSM, validating PIN ...");

        HSMResponse hsmResp = mgr.validatePin(cardNo,PIN,PIN.length(),pinOffset,PIN.length());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response After PIN validation :: Code -- > " +
                                        hsmResp.getResponseCode());

        if (!hsmResp.getResponseCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          respObj.setRespCode("55");
          respObj.setRespDesc("Invalid PIN Supplied for Card(" +
                              maskCardNo(cardNo) + ")");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Invalid PIN Supplied for Card(" + cardNo + ")");
          //log the transaction
          String[] transIds = logTransaction(cardNo, serviceId, deviceType, null,
                                             "0200", "0", respObj.getRespDesc(),
                                             "0", respObj.getRespCode(),
                                             deviceId, cardAcceptorId,
                                             cardAcceptorNameAndLoc, mcc,
                                             accountNo, acquirerId);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Transaction logged with ISO Serial No - > " +
                                          transIds[0] +
                                          " && Trace Audit No -- > " +
                                          transIds[1]);
          //set the trans id in response
          respObj.setTransId(transIds[0]);
          return respObj;
        } //end if
      } //end esle if
      //Now the response is successsful
      respObj.setRespCode(Constants.SUCCESS_CODE);
      respObj.setRespDesc(Constants.SUCCESS_MSG);
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage());
      throw exp;
    } //end catch
  } //end isCardInfoValid


  /**
   * This method is used to complete the already pre-authorized transaction. It calls the database
   * stored procedure "VAS_PREAUTH_COMP" to complete the pre-authorization. A response object is created
   * and its attributes are initialized with the parameters returned from the database procedure. At the
   * end of the processing it returns the response object which describes the status of the processing
   * to the client. In case of any exception the service returns the respons object with response code
   * "96" which describes the error in detail.
   * @param request ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj doVASPreAuthCompletion(ServicesRequestObj request) throws Exception{
    ServicesResponseObj response = null;
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;
    try {
      //Double amount=null;
      //if(request.getAmount()==null) amount=Double.valueOf(request.getAmount());
      //else amount=Double.valueOf("0.0");

//      query.append("Execute procedure vas_preauth_comp( " +
//                   "pvas_tan= "+ request.getPreAuthTransId()+","+
//                   "pcard_no= " +
//                   (request.getCardNo() != null ?
//                    "'" + request.getCardNo() + "'" : null) + "," +
//                   " pvas_type_id= " +
//                   (request.getVasAccountType() != null ?
//                    "'" + request.getVasAccountType() + "'" : null) + "," +
//                   "pamount= "+ request.getAmount() +","+
//                   " pacq_id= " +
//                   (request.getAcquirerId() != null ?
//                    "'" + request.getAcquirerId() + "'" : null) + "," +
//                   " pcard_acpt_id= " +
//                   (request.getCardAcceptorId() != null ?
//                    "'" + request.getCardAcceptorId() + "'" : null) + "," +
//                   " pcard_acpt_nameloc= " +
//                   (request.getCardAcceptNameAndLoc() != null ?
//                    "'" + request.getCardAcceptNameAndLoc() + "'" : null) + "," +
//                   " pmcc= " +
//                   (request.getMcc() != null ? "'" + request.getMcc() + "'" : null) +
//                   "," +
//                   " pdevice_id= " +
//                   (request.getDeviceId() != null ?
//                    "'" + request.getDeviceId() + "'" : null) + "," +
//                   " pdevice_type= " +
//                   (request.getDeviceType() != null ?
//                    "'" + request.getDeviceType() + "'" : null) + "," +
//                   " pdesc= " +
//                   (request.getDescription() != null ?
//                    "'" + request.getDescription() + "'" : null) + "," +
//                   " apply_fee= " +
//                   (request.getApplyFee() != null ?
//                    "'" + request.getApplyFee() + "'" : null) + ")"
//                   );
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS PreAuth Completion --- request.getCardNo()---> " + request.getCardNo()
                                      + "<---request.getPreAuthTransId()-->" + request.getPreAuthTransId()
                                      + "<---request.getVasAccountType()-->" + request.getVasAccountType()
                                      + "<---request.getAmount()-->" + request.getAmount()
                                      + "<---request.getAcquirerId()-->" + request.getAcquirerId()
                                      + "<---request.getCardAcceptorId()-->" + request.getCardAcceptorId()
                                      + "<---request.getCardAcceptNameAndLoc()-->" + request.getCardAcceptNameAndLoc()
                                      + "<---request.getMcc()-->" + request.getMcc()
                                      + "<---request.getDeviceId()-->" + request.getDeviceId()
                                      + "<---request.getDeviceType()-->" + request.getDeviceType()
                                      + "<---request.getDescription()-->" + request.getDescription()
                                      + "<---request.getApplyFee()-->" + request.getApplyFee()
                                      + "<---request.getAcqData1()-->" + request.getAcqData1()
                                      + "<---request.getAcqData2()-->" + request.getAcqData2()
                                      + "<---request.getAcqData3()-->" + request.getAcqData3()
                                      + "<---request.getAcqUsrId()-->" + request.getAcqUsrId()
                                      + "<---request.getRetreivalRefNum()-->" + request.getRetreivalRefNum()
                                      + "<---request.getVasVendorId()-->" + request.getVasVendorId());

      query.append("execute procedure vas_preauth_comp( pvas_tan = ?, pcard_no = ?, pvas_type_id = ?, pamount = ?, pacq_id = ?, pcard_acpt_id = ?, pcard_acpt_nameloc = ?, pmcc = ?, pdevice_id = ? , pdevice_type = ?, pdesc = ?, apply_fee = ?, psub_srv = ?, pdata_2 = ?	, pdata_3 = ?, pacq_userid = ?, pretrieval_ref = ?, pvendor_id = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Query prepared for calling VAS PreAuth Completion--->" +
                                      query);

      cstmt = con.prepareCall(query.toString());

      cstmt.setString(1, request.getPreAuthTransId());
      cstmt.setString(2, request.getCardNo());
      cstmt.setString(3, request.getVasAccountType());
      cstmt.setString(4, request.getAmount());
      cstmt.setString(5, request.getAcquirerId());
      cstmt.setString(6, request.getCardAcceptorId());
      cstmt.setString(7, request.getCardAcceptNameAndLoc());
      cstmt.setString(8, request.getMcc());
      cstmt.setString(9, request.getDeviceId());
      cstmt.setString(10, request.getDeviceType());
      cstmt.setString(11, request.getDescription());
      cstmt.setString(12, request.getApplyFee());
      cstmt.setString(13, request.getAcqData1());
      cstmt.setString(14, request.getAcqData2());
      cstmt.setString(15, request.getAcqData3());
      cstmt.setString(16, request.getAcqUsrId());
      cstmt.setString(17, request.getRetreivalRefNum());
      cstmt.setString(18, request.getVasVendorId());

      rs = cstmt.executeQuery();
      response = new ServicesResponseObj();
      if (rs.next()) {
        response.setRespCode(rs.getString(1));
        response.setRespDesc(rs.getString(2));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- Response Code--->" +
                                        response.getRespCode() +
                                        "<---Response desc--->" +
                                        response.getRespDesc());
        if (response.getRespCode() != null &&
            response.getRespCode().trim().length() > 0) {
          if (response.getRespCode().trim().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            if (rs.getString(3) != null && rs.getString(3).trim().length() > 0)
              response.setCardBalance(rs.getString(3));
            if (rs.getString(4) != null && rs.getString(4).trim().length() > 0)
              response.setTransId(rs.getString(4));
            if (rs.getString(5) != null && rs.getString(5).trim().length() > 0)
              response.setFeeAmount(rs.getString(5));

            //response.setTransCat("V");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Resposne Received from API --- Transaction ID--->" +
                                            response.getTransId() +
                                            "<---VAS Balance--->" +
                                            response.getCardBalance() +
                                            "<---Fee Amount-->" +
                                            response.getFeeAmount() +
                                            "<---Trans Cat-->" +
                                            response.getTransCat());

          } //only provide other attributes in case of 00 response code
        }
        else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Resposne Received from API --- No response received returning 06");
          response.setRespCode("06");
          response.setRespDesc("No Response Received");
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- No response received --- returning 06");
        response.setRespCode("06");
        response.setRespDesc("No Response Received");
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception while doing VAS Pre-Atuth using API--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  }



  /**
   * This method is used to do the VAS pre-authorization. It calls the database stored
   * procedure "VAS_PREAUTH" to do the pre-authorization. A response object is created
   * and its attributes are initialized with the parameter returned from the database procedure. At the
   * end of the processing it returns the response object which describes the status of the processing
   * to the client. In case of any exception the service returns the response object with response code
   * "96" which describes the error in detail.

   * @param request ServicesRequestObj
   * @return ServicesResponseObj
   */
  public ServicesResponseObj doVASPreAuth(ServicesRequestObj request) throws Exception{
    ServicesResponseObj response = null;
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;
    try {
//      query.append("Execute procedure vas_preauth( " +
//                   "pcard_no= " +
//                   (request.getCardNo() != null ?
//                    "'" + request.getCardNo() + "'" : null) + "," +
//                   " pvas_type_id= " +
//                   (request.getVasAccountType() != null ?
//                    "'" + request.getVasAccountType() + "'" : null) + "," +
//                   "pamount= "+request.getAmount()+ ","+
//                   " pacq_id= " +
//                   (request.getAcquirerId() != null ?
//                    "'" + request.getAcquirerId() + "'" : null) + "," +
//                   " pcard_acpt_id= " +
//                   (request.getCardAcceptorId() != null ?
//                    "'" + request.getCardAcceptorId() + "'" : null) + "," +
//                   " pcard_acpt_nameloc= " +
//                   (request.getCardAcceptNameAndLoc() != null ?
//                    "'" + request.getCardAcceptNameAndLoc() + "'" : null) + "," +
//                   " pmcc= " +
//                   (request.getMcc() != null ? "'" + request.getMcc() + "'" : null) +
//                   "," +
//                   " pdevice_id= " +
//                   (request.getDeviceId() != null ?
//                    "'" + request.getDeviceId() + "'" : null) + "," +
//                   " pdevice_type= " +
//                   (request.getDeviceType() != null ?
//                    "'" + request.getDeviceType() + "'" : null) + "," +
//                   " pdesc= " +
//                   (request.getDescription() != null ?
//                    "'" + request.getDescription() + "'" : null) + "," +
//                   " apply_fee= " +
//                   (request.getApplyFee() != null ?
//                    "'" + request.getApplyFee() + "'" : null) + ")"
//                   );
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS PreAuth --- request.getCardNo()---> " + request.getCardNo()
                                      + "<---request.getVasAccountType()-->" + request.getVasAccountType()
                                      + "<---request.getAmount()-->" + request.getAmount()
                                      + "<---request.getAcquirerId()-->" + request.getAcquirerId()
                                      + "<---request.getCardAcceptorId()-->" + request.getCardAcceptorId()
                                      + "<---request.getCardAcceptNameAndLoc()-->" + request.getCardAcceptNameAndLoc()
                                      + "<---request.getMcc()-->" + request.getMcc()
                                      + "<---request.getDeviceId()-->" + request.getDeviceId()
                                      + "<---request.getDeviceType()-->" + request.getDeviceType()
                                      + "<---request.getDescription()-->" + request.getDescription()
                                      + "<---request.getApplyFee()-->" + request.getApplyFee()
                                      + "<---request.getAcqData1()-->" + request.getAcqData1()
                                      + "<---request.getAcqData2()-->" + request.getAcqData2()
                                      + "<---request.getAcqData3()-->" + request.getAcqData3()
                                      + "<---request.getAcqUsrId()-->" + request.getAcqUsrId()
                                      + "<---request.getRetreivalRefNum()-->" + request.getRetreivalRefNum()
                                      + "<---request.getVasVendorId()-->" + request.getVasVendorId());

      query.append("execute procedure vas_preauth(pcard_no = ? , pvas_type_id = ?, pamount = ? , pacq_id = ? , pcard_acpt_id = ? , pcard_acpt_nameloc = ? , pmcc = ? , pdevice_id = ? , pdevice_type = ? , pdesc = ? , apply_fee = ? , psub_srv = ? , pdata_2 = ? , pdata_3 = ? , pacq_userid = ? , pretrieval_ref = ?, pvendor_id = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS PreAuth--->" +
                                      query);

      cstmt = con.prepareCall(query.toString());
      cstmt.setString(1, request.getCardNo());
      cstmt.setString(2, request.getVasAccountType());
      cstmt.setString(3, request.getAmount());
      cstmt.setString(4, request.getAcquirerId());
      cstmt.setString(5, request.getCardAcceptorId());
      cstmt.setString(6, request.getCardAcceptNameAndLoc());
      cstmt.setString(7, request.getMcc());
      cstmt.setString(8, request.getDeviceId());
      cstmt.setString(9, request.getDeviceType());
      cstmt.setString(10, request.getDescription());
      cstmt.setString(11, request.getApplyFee());
      cstmt.setString(12, request.getAcqData1());
      cstmt.setString(13, request.getAcqData2());
      cstmt.setString(14, request.getAcqData3());
      cstmt.setString(15, request.getAcqUsrId());
      cstmt.setString(16, request.getRetreivalRefNum());
      cstmt.setString(17, request.getVasVendorId());

      rs = cstmt.executeQuery();
      response = new ServicesResponseObj();
      if (rs.next()) {
        response.setRespCode(rs.getString(1));
        response.setRespDesc(rs.getString(2));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- Response Code--->" +
                                        response.getRespCode() +
                                        "<---Response desc--->" +
                                        response.getRespDesc());
        if (response.getRespCode() != null &&
            response.getRespCode().trim().length() > 0) {
          if (response.getRespCode().trim().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            if (rs.getString(3) != null && rs.getString(3).trim().length() > 0)
              response.setCardBalance(rs.getString(3));
            if (rs.getString(4) != null && rs.getString(4).trim().length() > 0)
              response.setTransId(rs.getString(4));
            if (rs.getString(5) != null && rs.getString(5).trim().length() > 0)
              response.setFeeAmount(rs.getString(5));

            //response.setTransCat("V");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Resposne Received from API --- Transaction ID--->" +
                                            response.getTransId() +
                                            "<---VAS Balance--->" +
                                            response.getCardBalance() +
                                            "<---Fee Amount-->" +
                                            response.getFeeAmount() +
                                            "<---Trans Cat-->" +
                                            response.getTransCat());

          } //only provide other attributes in case of 00 response code
        }
        else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Resposne Received from API --- No response received returning 06");
          response.setRespCode("06");
          response.setRespDesc("No Response Received");
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- No response received --- returning 06");
        response.setRespCode("06");
        response.setRespDesc("No Response Received");
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception while doing VAS Pre-Atuth using API--->" +
                                      ex);
      throw ex;

    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  }



  /**
   * This method is used to get the VAS  balance. It calls the database stored  procedure
   * "VAS_BAL_INQ" to get the balance of the given card. A response object is created
   * and its attributes are initlized with the parameter returned from the database procedure. At the
   * end of the processing it returns the response object which describes the status of the processing
   * to the client. In case of any exception the service returns the response object with response code
   * "96" which describes the error in detail.
   * @param request ServicesRequestObj
   * @return ServicesResponseObj
   */


  public ServicesResponseObj getVASBalance(ServicesRequestObj request) throws Exception{

    ServicesResponseObj response = null;
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    try {
//      query.append("Execute procedure VAS_BAL_INQ( " +
//                   "pcard_no= " +
//                   (request.getCardNo() != null ?
//                    "'" + request.getCardNo() + "'" : null) + "," +
//                   " pvas_type_id= " +
//                   (request.getVasAccountType() != null ?
//                    "'" + request.getVasAccountType() + "'" : null) + "," +
//                   " pacq_id= " +
//                   (request.getAcquirerId() != null ?
//                    "'" + request.getAcquirerId() + "'" : null) + "," +
//                   " pcard_acpt_id= " +
//                   (request.getCardAcceptorId() != null ?
//                    "'" + request.getCardAcceptorId() + "'" : null) + "," +
//                   " pcard_acpt_nameloc= " +
//                   (request.getCardAcceptNameAndLoc() != null ?
//                    "'" + request.getCardAcceptNameAndLoc() + "'" : null) + "," +
//                   " pmcc= " +
//                   (request.getMcc() != null ? "'" + request.getMcc() + "'" : null) +
//                   "," +
//                   " pdevice_id= " +
//                   (request.getDeviceId() != null ?
//                    "'" + request.getDeviceId() + "'" : null) + "," +
//                   " pdevice_type= " +
//                   (request.getDeviceType() != null ?
//                    "'" + request.getDeviceType() + "'" : null) + "," +
//                   " pdesc= " +
//                   (request.getDescription() != null ?
//                    "'" + request.getDescription() + "'" : null) + "," +
//                   " apply_fee= " +
//                   (request.getApplyFee() != null ?
//                    "'" + request.getApplyFee() + "'" : null) + ")"
//                   );
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS BI --- request.getCardNo()---> "
                                      + request.getCardNo()
                                      + "<---request.getVasAccountType()-->" + request.getVasAccountType()
                                      + "<---request.getAcquirerId()-->" + request.getAcquirerId()
                                      + "<---request.getCardAcceptorId()-->" + request.getCardAcceptorId()
                                      + "<---request.getCardAcceptNameAndLoc()-->" + request.getCardAcceptNameAndLoc()
                                      + "<---request.getMcc()-->" + request.getMcc()
                                      + "<---request.getDeviceId()-->" + request.getDeviceId()
                                      + "<---request.getDeviceType()-->" + request.getDeviceType()
                                      + "<---request.getDescription()-->" + request.getDescription()
                                      + "<---request.getApplyFee()-->" + request.getApplyFee()
                                      + "<---request.getAcqData1()-->" + request.getAcqData1()
                                      + "<---request.getAcqData2()-->" + request.getAcqData2()
                                      + "<---request.getAcqData3()-->" + request.getAcqData3()
                                      + "<---request.getAcqUsrId()-->" + request.getAcqUsrId()
                                      + "<---request.getRetreivalRefNum()-->" + request.getRetreivalRefNum()
                                      + "<---request.getVasVendorId()-->" + request.getVasVendorId());

      query.append("execute procedure vas_bal_inq(pcard_no = ? , pvas_type_id = ? , pacq_id = ? , pcard_acpt_id = ? , pcard_acpt_nameloc = ? , pmcc = ? , pdevice_id = ? , pdevice_type = ? , pdesc = ? , apply_fee = ? , psub_srv = ? , pdata_2 = ? , pdata_3 = ? , pacq_userid = ? , pretrieval_ref = ?, pvendor_id = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS BI--->" +
                                      query);

      cstmt = con.prepareCall(query.toString());
      cstmt.setString(1, request.getCardNo());
      cstmt.setString(2, request.getVasAccountType());
      cstmt.setString(3, request.getAcquirerId());
      cstmt.setString(4, request.getCardAcceptorId());
      cstmt.setString(5, request.getCardAcceptNameAndLoc());
      cstmt.setString(6, request.getMcc());
      cstmt.setString(7, request.getDeviceId());
      cstmt.setString(8, request.getDeviceType());
      cstmt.setString(9, request.getDescription());
      cstmt.setString(10, request.getApplyFee());
      cstmt.setString(11, request.getAcqData1());
      cstmt.setString(12, request.getAcqData2());
      cstmt.setString(13, request.getAcqData3());
      cstmt.setString(14, request.getAcqUsrId());
      cstmt.setString(15, request.getRetreivalRefNum());
      cstmt.setString(16, request.getVasVendorId());

      rs = cstmt.executeQuery();

      response = new ServicesResponseObj();
      if (rs.next()) {
        response.setRespCode(rs.getString(1));
        response.setRespDesc(rs.getString(2));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- Response Code--->" +
                                        response.getRespCode() +
                                        "<---Response desc--->" +
                                        response.getRespDesc());
        if (response.getRespCode() != null &&
            response.getRespCode().trim().length() > 0) {
          if (response.getRespCode().trim().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            if (rs.getString(3) != null && rs.getString(3).trim().length() > 0)
              response.setCardBalance(rs.getString(3));
            if (rs.getString(4) != null && rs.getString(4).trim().length() > 0)
              response.setTransId(rs.getString(4));
            if (rs.getString(5) != null && rs.getString(5).trim().length() > 0)
              response.setFeeAmount(rs.getString(5));

            response.setTransCat("V");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Resposne Received from API --- Transaction ID--->" +
                                            response.getTransId() +
                                            "<---VAS Balance--->" +
                                            response.getCardBalance() +
                                            "<---Fee Amount-->" +
                                            response.getFeeAmount() +
                                            "<---Trans Cat-->" +
                                            response.getTransCat());

          } //only provide other attributes in case of 00 response code
        }
        else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Resposne Received from API --- No response received returning 06");
          response.setRespCode("06");
          response.setRespDesc("No Response Received");
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- No response received --- returning 06");
        response.setRespCode("06");
        response.setRespDesc("No Response Received");
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting VAS Balance using API--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  }


  /**
   *  This method is used to credit the funds from VAS Account. It calls the database stored
   *  procedure "CR_VAS_ACC" to credit the funds from the VAS account. A response object is created
   * and its attributes are initilized with the parameters returned from the database procedure. At the
   * end of the processing it returns the response object which describes the status of the processing
   * to the client. In case of any exception the service returns the response object with response code
   * "96" which describes the error in detail.
   * @param request ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj creditVASAccount(ServicesRequestObj request) throws Exception{

    ServicesResponseObj response = null;
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS Credit API --- request.getCardNo()---> "
                                      + request.getCardNo()
                                      + "<---request.getVasAccountType()-->" + request.getVasAccountType()
                                      + "<---request.getAmount()-->" + request.getAmount()
                                      + "<---request.getAcquirerId()-->" + request.getAcquirerId()
                                      + "<---request.getCardAcceptorId()-->" + request.getCardAcceptorId()
                                      + "<---request.getCardAcceptNameAndLoc()-->" + request.getCardAcceptNameAndLoc()
                                      + "<---request.getMcc()-->" + request.getMcc()
                                      + "<---request.getDeviceId()-->" + request.getDeviceId()
                                      + "<---request.getDeviceType()-->" + request.getDeviceType()
                                      + "<---request.getDescription()-->" + request.getDescription()
                                      + "<---request.getApplyFee()-->" + request.getApplyFee()
                                      + "<---request.getAcqData1()-->" + request.getAcqData1()
                                      + "<---request.getAcqData2()-->" + request.getAcqData2()
                                      + "<---request.getAcqData3()-->" + request.getAcqData3()
                                      + "<---request.getAcqUsrId()-->" + request.getAcqUsrId()
                                      + "<---request.getRetreivalRefNum()-->" + request.getRetreivalRefNum()
                                      + "<---request.getVasVendorId()-->" + request.getVasVendorId());

      query.append("execute procedure cr_vas_acc(pcard_no = ? , pvas_type_id = ? , pamount = ? , pacq_id = ? , pcard_acpt_id = ? , pcard_acpt_nameloc = ? , pmcc = ? , pdevice_id = ? , pdevice_type = ? , pdesc = ? , apply_fee = ? , psub_srv = ? , pdata_2 = ? , pdata_3 = ? , pacq_userid = ? , pretrieval_ref = ?, pvendor_id = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS Credit API--->" +
                                      query);

      cstmt = con.prepareCall(query.toString());
      cstmt.setString(1, request.getCardNo());
      cstmt.setString(2, request.getVasAccountType());
      cstmt.setString(3, request.getAmount());
      cstmt.setString(4, request.getAcquirerId());
      cstmt.setString(5, request.getCardAcceptorId());
      cstmt.setString(6, request.getCardAcceptNameAndLoc());
      cstmt.setString(7, request.getMcc());
      cstmt.setString(8, request.getDeviceId());
      cstmt.setString(9, request.getDeviceType());
      cstmt.setString(10, request.getDescription());
      cstmt.setString(11, request.getApplyFee());
      cstmt.setString(12, request.getAcqData1());
      cstmt.setString(13, request.getAcqData2());
      cstmt.setString(14, request.getAcqData3());
      cstmt.setString(15, request.getAcqUsrId());
      cstmt.setString(16, request.getRetreivalRefNum());
      cstmt.setString(17, request.getVasVendorId());

      rs = cstmt.executeQuery();

      response = new ServicesResponseObj();
      if (rs.next()) {
        response.setRespCode(rs.getString(1));
        response.setRespDesc(rs.getString(2));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- Response Code--->" +
                                        response.getRespCode() +
                                        "<---Response desc--->" +
                                        response.getRespDesc());
        if (response.getRespCode() != null &&
            response.getRespCode().trim().length() > 0) {
          if (response.getRespCode().trim().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            if (rs.getString(3) != null && rs.getString(3).trim().length() > 0)
              response.setCardBalance(rs.getString(3));
            if (rs.getString(4) != null && rs.getString(4).trim().length() > 0)
              response.setTransId(rs.getString(4));
            if (rs.getString(5) != null && rs.getString(5).trim().length() > 0)
              response.setFeeAmount(rs.getString(5));

            response.setTransCat("V");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Resposne Received from API --- Transaction ID--->" +
                                            response.getTransId() +
                                            "<---VAS Balance--->" +
                                            response.getCardBalance() +
                                            "<---Fee Amount-->" +
                                            response.getFeeAmount() +
                                            "<---Trans Cat-->" +
                                            response.getTransCat()
                                            );

          } //only provide other attributes in case of 00 response code
        }
        else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Resposne Received from API --- No response received returning 06");
          response.setRespCode("06");
          response.setRespDesc("No Response Received");
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- No response received --- returning 06");
        response.setRespCode("06");
        response.setRespDesc("No Response Received");
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting VAS Credit using API--->" +
                                      ex);
     throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  } //end creditVasAccount


  /**
   * This method is used to debit the funds from the given VAS Account. It calls the database stored
   * procedure "DR_VAS_ACC" to debit the funds from the given VAS account. A response object is created
   * and its attributes are initlized with the parameters returned from the database procedure. At the
   * end of the processing it returns the response object which describes the status of the processing
   * to the client. In case of any exception the service returns the response object with response code
   * "96" which describes the error in detail.
   * @param request ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj debitVASAccount(ServicesRequestObj request) throws Exception{

    ServicesResponseObj response = null;
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS Debit API --- request.getCardNo()---> " +
                                      request.getCardNo()
                                      + "<---request.getVasAccountType()-->" +
                                      request.getVasAccountType()
                                      + "<---request.getAmount()-->" +
                                      request.getAmount()
                                      + "<---request.getAcquirerId()-->" +
                                      request.getAcquirerId()
                                      + "<---request.getCardAcceptorId()-->" +
                                      request.getCardAcceptorId()
                                      + "<---request.getCardAcceptNameAndLoc()-->" +
                                      request.getCardAcceptNameAndLoc()
                                      + "<---request.getMcc()-->" +
                                      request.getMcc()
                                      + "<---request.getDeviceId()-->" +
                                      request.getDeviceId()
                                      + "<---request.getDeviceType()-->" +
                                      request.getDeviceType()
                                      + "<---request.getDescription()-->" +
                                      request.getDescription()
                                      + "<---request.getApplyFee()-->" +
                                      request.getApplyFee()
                                      + "<---request.getAcqData1()-->" +
                                      request.getAcqData1()
                                      + "<---request.getAcqData2()-->" +
                                      request.getAcqData2()
                                      + "<---request.getAcqData3()-->" +
                                      request.getAcqData3()
                                      + "<---request.getAcqUsrId()-->" +
                                      request.getAcqUsrId()
                                      + "<---request.getRetreivalRefNum()-->" +
                                      request.getRetreivalRefNum()
                                      + "<---request.getVasVendorId()-->" + request.getVasVendorId());

      query.append("execute procedure dr_vas_acc(pcard_no = ? , pvas_type_id = ? , pamount = ? , pacq_id = ? , pcard_acpt_id = ? , pcard_acpt_nameloc = ? , pmcc = ? , pdevice_id = ? , pdevice_type = ? , pdesc = ? , apply_fee = ? , psub_srv = ? , pdata_2 = ? , pdata_3 = ? , pacq_userid = ? , pretrieval_ref = ?, pvendor_id = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS Debit API--->" +
                                      query);

      cstmt = con.prepareCall(query.toString());
      cstmt.setString(1, request.getCardNo());
      cstmt.setString(2, request.getVasAccountType());
      cstmt.setString(3, request.getAmount());
      cstmt.setString(4, request.getAcquirerId());
      cstmt.setString(5, request.getCardAcceptorId());
      cstmt.setString(6, request.getCardAcceptNameAndLoc());
      cstmt.setString(7, request.getMcc());
      cstmt.setString(8, request.getDeviceId());
      cstmt.setString(9, request.getDeviceType());
      cstmt.setString(10, request.getDescription());
      cstmt.setString(11, request.getApplyFee());
      cstmt.setString(12, request.getAcqData1());
      cstmt.setString(13, request.getAcqData2());
      cstmt.setString(14, request.getAcqData3());
      cstmt.setString(15, request.getAcqUsrId());
      cstmt.setString(16, request.getRetreivalRefNum());
      cstmt.setString(17, request.getVasVendorId());

      rs = cstmt.executeQuery();
      response = new ServicesResponseObj();
      if (rs.next()) {
        response.setRespCode(rs.getString(1));
        response.setRespDesc(rs.getString(2));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- Response Code--->" +
                                        response.getRespCode() +
                                        "<---Response desc--->" +
                                        response.getRespDesc());
        if (response.getRespCode() != null &&
            response.getRespCode().trim().length() > 0) {
          if (response.getRespCode().trim().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            if (rs.getString(3) != null && rs.getString(3).trim().length() > 0)
              response.setCardBalance(rs.getString(3));
            if (rs.getString(4) != null && rs.getString(4).trim().length() > 0)
              response.setTransId(rs.getString(4));
            if (rs.getString(5) != null && rs.getString(5).trim().length() > 0)
              response.setFeeAmount(rs.getString(5));

            response.setTransCat("V");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Resposne Received from API --- Transaction ID--->" +
                                            response.getTransId() +
                                            "<---VAS Balance--->" +
                                            response.getCardBalance() +
                                            "<---Fee Amount-->" +
                                            response.getFeeAmount() +
                                            "<---Trans Cat-->" +
                                            response.getTransCat()
                                            );

          } //only provide other attributes in case of 00 response code
        }
        else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Resposne Received from API --- No response received returning 06");
          response.setRespCode("06");
          response.setRespDesc("No Response Received");
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- No response received --- returning 06");
        response.setRespCode("06");
        response.setRespDesc("No Response Received");
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting VAS Debit using API--->" +
                                      ex);
     throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  } //end debitVasAccount


  /**
   * This method is used to tranfer the fund in the given card no. It calls the database stored
   * procedure "VAS_TRANSFER_FROM" to transfer the funds into given card-no. A response object is created
   * and its attributes are initlized with the parameters returned from the database procedure. At the
   * end of the processing it returns the response object to the calling method which describes the status
   * of the processing.  In case of any exception the service returns the response object with response
   * code "96" which describes the error in detail.
   * @param request ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj vasTransferFrom(ServicesRequestObj request) throws Exception{

    ServicesResponseObj response = null;
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS Transfer From --- request.getCardNo()---> " + request.getCardNo()
                                      + "<---request.getVasAccountType()-->" + request.getVasAccountType()
                                      + "<---request.getAmount()-->" + request.getAmount()
                                      + "<---request.getAcquirerId()-->" + request.getAcquirerId()
                                      + "<---request.getCardAcceptorId()-->" + request.getCardAcceptorId()
                                      + "<---request.getCardAcceptNameAndLoc()-->" + request.getCardAcceptNameAndLoc()
                                      + "<---request.getMcc()-->" + request.getMcc()
                                      + "<---request.getDeviceId()-->" + request.getDeviceId()
                                      + "<---request.getDeviceType()-->" + request.getDeviceType()
                                      + "<---request.getDescription()-->" + request.getDescription()
                                      + "<---request.getApplyFee()-->" + request.getApplyFee()
                                      + "<---request.getAcqData1()-->" + request.getAcqData1()
                                      + "<---request.getAcqData2()-->" + request.getAcqData2()
                                      + "<---request.getAcqData3()-->" + request.getAcqData3()
                                      + "<---request.getAcqUsrId()-->" + request.getAcqUsrId()
                                      + "<---request.getRetreivalRefNum()-->" + request.getRetreivalRefNum()
                                      + "<---request.getVasVendorId()-->" + request.getVasVendorId());

      query.append("execute procedure vas_transfer_from(pcard_no = ? , pvas_type_id = ? , pamount = ? , pacq_id = ? , pcard_acpt_id = ? , pcard_acpt_nameloc = ? , pmcc = ? , pdevice_id = ? , pdevice_type = ? , pdesc = ? , apply_fee = ? , psub_srv = ? , pdata_2 = ? , pdata_3 = ? , pacq_userid = ? , pretrieval_ref = ?, pvendor_id = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS Transfer From API--->" +
                                      query);

      cstmt = con.prepareCall(query.toString());
      cstmt.setString(1, request.getCardNo());
      cstmt.setString(2, request.getVasAccountType());
      cstmt.setString(3, request.getAmount());
      cstmt.setString(4, request.getAcquirerId());
      cstmt.setString(5, request.getCardAcceptorId());
      cstmt.setString(6, request.getCardAcceptNameAndLoc());
      cstmt.setString(7, request.getMcc());
      cstmt.setString(8, request.getDeviceId());
      cstmt.setString(9, request.getDeviceType());
      cstmt.setString(10, request.getDescription());
      cstmt.setString(11, request.getApplyFee());
      cstmt.setString(12, request.getAcqData1());
      cstmt.setString(13, request.getAcqData2());
      cstmt.setString(14, request.getAcqData3());
      cstmt.setString(15, request.getAcqUsrId());
      cstmt.setString(16, request.getRetreivalRefNum());
      cstmt.setString(17, request.getVasVendorId());

      rs = cstmt.executeQuery();

      response = new ServicesResponseObj();
      if (rs.next()) {
        response.setRespCode(rs.getString(1));
        response.setRespDesc(rs.getString(2));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- Response Code--->" +
                                        response.getRespCode() +
                                        "<---Response desc--->" +
                                        response.getRespDesc());
        if (response.getRespCode() != null &&
            response.getRespCode().trim().length() > 0) {
          if (response.getRespCode().trim().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            if (rs.getString(3) != null && rs.getString(3).trim().length() > 0)
              response.setToCardBalance(rs.getString(3)); //Card Balance, Main account Balance
            if (rs.getString(4) != null && rs.getString(4).trim().length() > 0)
              response.setCardBalance(rs.getString(4)); //Vas Account Balance
            if (rs.getString(5) != null && rs.getString(5).trim().length() > 0)
              response.setTransId(rs.getString(5));
            if (rs.getString(6) != null && rs.getString(6).trim().length() > 0)
              response.setFeeAmount(rs.getString(6));

            response.setTransCat("V");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Resposne Received from API --- Transaction ID--->" +
                                            response.getTransId() +
                                            "<---VAS Balance--->" +
                                            response.getCardBalance() +
                                            "<---Main Account Balance--->" +
                                            response.getToCardBalance() +
                                            "<---Fee Amount-->" +
                                            response.getFeeAmount() +
                                            "<---Trans Cat-->" +
                                            response.getTransCat()
                                            );

          }//only provide other attributes in case of 00 response code
        }
        else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Resposne Received from API --- No response received returning 06");
          response.setRespCode("06");
          response.setRespDesc("No Response Received");
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- No response received --- returning 06");
        response.setRespCode("06");
        response.setRespDesc("No Response Received");
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in VAS_TRANSFER_FROM using API--->" +
                                      ex);
     throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  } //end VAS_TRANSFER_FROM


  /**
   * This method is used transfer funds from the given VAS account into given card no. It calls the
   * database stored procedure "VAS_TRANSFER_TO" to transfer the funds into given card-no account.
   * A response object is created and its attributes are initlized with the parameter returned from the
   * database procedure. At the end of the processing it returns the response object which describes the
   * status of the processing to the client. In case of any exception the service returns the respons
   * object with response code "96" which describes the error in detail.
   * @param request ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj vasTransferTo(ServicesRequestObj request) throws Exception{

    ServicesResponseObj response = null;
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS Transfer To --- request.getCardNo()---> " + request.getCardNo()
                                      + "<---request.getVasAccountType()-->" + request.getVasAccountType()
                                      + "<---request.getAmount()-->" + request.getAmount()
                                      + "<---request.getAcquirerId()-->" + request.getAcquirerId()
                                      + "<---request.getCardAcceptorId()-->" + request.getCardAcceptorId()
                                      + "<---request.getCardAcceptNameAndLoc()-->" + request.getCardAcceptNameAndLoc()
                                      + "<---request.getMcc()-->" + request.getMcc()
                                      + "<---request.getDeviceId()-->" + request.getDeviceId()
                                      + "<---request.getDeviceType()-->" + request.getDeviceType()
                                      + "<---request.getDescription()-->" + request.getDescription()
                                      + "<---request.getApplyFee()-->" + request.getApplyFee()
                                      + "<---request.getAcqData1()-->" + request.getAcqData1()
                                      + "<---request.getAcqData2()-->" + request.getAcqData2()
                                      + "<---request.getAcqData3()-->" + request.getAcqData3()
                                      + "<---request.getAcqUsrId()-->" + request.getAcqUsrId()
                                      + "<---request.getRetreivalRefNum()-->" + request.getRetreivalRefNum()
                                      + "<---request.getVasVendorId()-->" + request.getVasVendorId());

      query.append("execute procedure vas_transfer_to(pcard_no = ? , pvas_type_id = ? , pamount = ? , pacq_id = ? , pcard_acpt_id = ? , pcard_acpt_nameloc = ? , pmcc = ? , pdevice_id = ? , pdevice_type = ? , pdesc = ? , apply_fee = ? , psub_srv = ? , pdata_2 = ? , pdata_3 = ? , pacq_userid = ? , pretrieval_ref = ?, pvendor_id = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS Transfer To API--->" +
                                      query);

      cstmt = con.prepareCall(query.toString());
      cstmt.setString(1, request.getCardNo());
      cstmt.setString(2, request.getVasAccountType());
      cstmt.setString(3, request.getAmount());
      cstmt.setString(4, request.getAcquirerId());
      cstmt.setString(5, request.getCardAcceptorId());
      cstmt.setString(6, request.getCardAcceptNameAndLoc());
      cstmt.setString(7, request.getMcc());
      cstmt.setString(8, request.getDeviceId());
      cstmt.setString(9, request.getDeviceType());
      cstmt.setString(10, request.getDescription());
      cstmt.setString(11, request.getApplyFee());
      cstmt.setString(12, request.getAcqData1());
      cstmt.setString(13, request.getAcqData2());
      cstmt.setString(14, request.getAcqData3());
      cstmt.setString(15, request.getAcqUsrId());
      cstmt.setString(16, request.getRetreivalRefNum());
      cstmt.setString(17, request.getVasVendorId());

      rs = cstmt.executeQuery();

      response = new ServicesResponseObj();
      if (rs.next()) {
        response.setRespCode(rs.getString(1));
        response.setRespDesc(rs.getString(2));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- Response Code--->" +
                                        response.getRespCode() +
                                        "<---Response desc--->" +
                                        response.getRespDesc());
        if (response.getRespCode() != null &&
            response.getRespCode().trim().length() > 0) {

          if (response.getRespCode().trim().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            if (rs.getString(3) != null && rs.getString(3).trim().length() > 0)
              response.setCardBalance(rs.getString(3));
            if (rs.getString(4) != null && rs.getString(4).trim().length() > 0)
              response.setToCardBalance(rs.getString(4));
            if (rs.getString(5) != null && rs.getString(5).trim().length() > 0)
              response.setTransId(rs.getString(5));
            if (rs.getString(6) != null && rs.getString(6).trim().length() > 0)
              response.setFeeAmount(rs.getString(6));

            response.setTransCat("V");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Resposne Received from API --- Transaction ID--->" +
                                            response.getTransId() +
                                            "<---VAS Balance--->" +
                                            response.getToCardBalance() +
                                            "<---Main Account Balance--->" +
                                            response.getCardBalance() +
                                            "<---Fee Amount-->" +
                                            response.getFeeAmount() +
                                            "<---Trans Cat-->" +
                                            response.getTransCat()
                                            );
          }
        }
        else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Resposne Received from API --- No response received returning 06");
          response.setRespCode("06");
          response.setRespDesc("No Response Received");
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- No response received --- returning 06");
        response.setRespCode("06");
        response.setRespDesc("No Response Received");
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in VAS_TRANSFER_TO using API--->" +
                                      ex);
     throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  } //end VAS_TRANSFER_To


  /**
   * This method is used to get the list of linked cards which are linked with the given card no.
   * It calls the database stored procedure "GET_LINKED_CARDS" to get the linked cards. A response
   * object is created and its attributes are initlized with the parameters returned from the database
   * procedure. At the end of the processing it returns the response object which describes the status
   * of the processing to the client. In case of any exception the service returns the respons object
   * with response code "96" which describes the error in detail.
   * @param request ServicesRequestObj
   * @return ServicesResponseObj
   */

  public ServicesResponseObj getLinkedCards(ServicesRequestObj request) throws Exception{

    ServicesResponseObj response = null;
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;
    Vector linkedCardList = new Vector();

    try {
//      query.append("Execute procedure GET_LINKED_CARDS( " +
//                   "pcard_no= " +
//                   (request.getCardNo() != null ?
//                    "'" + request.getCardNo() + "'" : null) + "," +
//                   " pacq_id= " +
//                   (request.getAcquirerId() != null ?
//                    "'" + request.getAcquirerId() + "'" : null) + "," +
//                   " pcard_acpt_id= " +
//                   (request.getCardAcceptorId() != null ?
//                    "'" + request.getCardAcceptorId() + "'" : null) + "," +
//                   " pcard_acpt_nameloc= " +
//                   (request.getCardAcceptNameAndLoc() != null ?
//                    "'" + request.getCardAcceptNameAndLoc() + "'" : null) + "," +
//                   " pmcc= " +
//                   (request.getMcc() != null ? "'" + request.getMcc() + "'" : null) +
//                   "," +
//                   " pdevice_id= " +
//                   (request.getDeviceId() != null ?
//                    "'" + request.getDeviceId() + "'" : null) + "," +
//                   " pdevice_type= " +
//                   (request.getDeviceType() != null ?
//                    "'" + request.getDeviceType() + "'" : null) + "," +
//                   " pdesc= " +
//                   (request.getDescription() != null ?
//                    "'" + request.getDescription() + "'" : null) + "," +
//                   " papply_fee= " +
//                   (request.getApplyFee() != null ?
//                    "'" + request.getApplyFee() + "'" : null) + ")"
//                   );
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling Get Linked Cards --- request.getCardNo()---> " +
                                      request.getCardNo()
                                      + "<---request.getAcquirerId()-->" +
                                      request.getAcquirerId()
                                      + "<---request.getCardAcceptorId()-->" +
                                      request.getCardAcceptorId()
                                      + "<---request.getCardAcceptNameAndLoc()-->" +
                                      request.getCardAcceptNameAndLoc()
                                      + "<---request.getMcc()-->" +
                                      request.getMcc()
                                      + "<---request.getDeviceId()-->" +
                                      request.getDeviceId()
                                      + "<---request.getDeviceType()-->" +
                                      request.getDeviceType()
                                      + "<---request.getDescription()-->" +
                                      request.getDescription()
                                      + "<---request.getApplyFee()-->" +
                                      request.getApplyFee()
                                      + "<---request.getAcqData1()-->" +
                                      request.getAcqData1()
                                      + "<---request.getAcqData2()-->" +
                                      request.getAcqData2()
                                      + "<---request.getAcqData3()-->" +
                                      request.getAcqData3()
                                      + "<---request.getAcqUsrId()-->" +
                                      request.getAcqUsrId()
                                      + "<---request.getRetreivalRefNum()-->" +
                                      request.getRetreivalRefNum());

      query.append("execute procedure get_linked_cards(pcard_no = ?, pacq_id = ?, pcard_acpt_id = ?, pcard_acpt_nameloc = ?, pmcc = ?, pdevice_id = ?, pdevice_type = ?, pdesc = ?, papply_fee = ?, psub_srv = ?, pdata_2 = ?, pdata_3 = ?, pacq_userid = ?, pretrieval_ref = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling Get Linked Cards API--->" +
                                      query);

      cstmt = con.prepareCall(query.toString());

      cstmt.setString(1, request.getCardNo());
      cstmt.setString(2, request.getAcquirerId());
      cstmt.setString(3, request.getCardAcceptorId());
      cstmt.setString(4, request.getCardAcceptNameAndLoc());
      cstmt.setString(5, request.getMcc());
      cstmt.setString(6, request.getDeviceId());
      cstmt.setString(7, request.getDeviceType());
      cstmt.setString(8, request.getDescription());
      cstmt.setString(9, request.getApplyFee());
      cstmt.setString(10, request.getAcqData1());
      cstmt.setString(11, request.getAcqData2());
      cstmt.setString(12, request.getAcqData3());
      cstmt.setString(13, request.getAcqUsrId());
      cstmt.setString(14, request.getRetreivalRefNum());

      rs = cstmt.executeQuery();

      int counter = 0;
      String respCode = null;
      String respDesc = null;
      String transId = null;
      String fee = null;
      while (rs.next()) {
        linkedCardList.add(rs.getString(1));
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0)
          respCode = rs.getString(2);
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0)
          respDesc = rs.getString(3);
        if (rs.getString(4) != null && rs.getString(4).trim().length() > 0)
          transId = rs.getString(4);
        if (rs.getString(5) != null && rs.getString(5).trim().length() > 0)
          fee = rs.getString(5);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API ---Card Number--->" +
                                        linkedCardList.get(counter) +
                                        "<---Response Code--->" + respCode +
                                        "<---Response desc--->" + respDesc +
                                        "<---Transaction ID--->" + transId +
                                        "<---Fee --->" + fee);
        counter++;
      }
      response = new ServicesResponseObj();
      if (respCode != null && respCode.trim().length() >= 0) {
        response.setRespCode(respCode);
        response.setRespDesc(respDesc);
        response.setTransId(transId);
        response.setFeeAmount(fee);
        response.setLinkedCards(linkedCardList);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        " Linked Card List Size--->" +
                                        linkedCardList.size());
      }
      else {
        response.setRespCode("06");
        response.setRespDesc("No Response received from API");
      }

    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in GET_LINKED_CARDS using API--->" +
                                      ex);
     throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  } //end get linked cards


  /**
   * This method is used to set the link of a card with the given card no (Primary card to Secondary Card)
   * It calls the database stored procedure "SET_LINKED_CARDS" to set the link between the cards.
   * A response object is created and its attributes are initlized with the parameters returned from
   * the database procedure. At the end of the processing it returns the response object which describes
   * the status of the processing to the client. In case of any exception the service returns the response
   * object with response code "96" which describes the error in detail.
   * @param request ServicesRequestObj
   * @return ServicesResponseObj
   */


  public ServicesResponseObj setLinkedCards(ServicesRequestObj request) throws Exception{

   ServicesResponseObj response = null;
   StringBuffer query = new StringBuffer();
   CallableStatement cstmt = null;
   ResultSet rs = null;
   try {
//     query.append("Execute procedure set_linked_cards( " +
//                  "pcard_no= " +
//                  (request.getCardNo() != null ?
//                   "'" + request.getCardNo() + "'" : null) + "," +
//                  " p_lnk_card= "+
//                  (request.getSecCardNo() != null ?
//                   "'" + request.getSecCardNo() + "'" : null) + "," +
//                  " p_lnk_type="+
//                  (request.getLinkType() != null ?
//                   "'" + request.getLinkType() + "'" : "'L'") + "," +
//                  " pacq_id= " +
//                  (request.getAcquirerId() != null ?
//                   "'" + request.getAcquirerId() + "'" : null) + "," +
//                  " pcard_acpt_id= " +
//                  (request.getCardAcceptorId() != null ?
//                   "'" + request.getCardAcceptorId() + "'" : null) + "," +
//                  " pcard_acpt_nameloc= " +
//                  (request.getCardAcceptNameAndLoc() != null ?
//                   "'" + request.getCardAcceptNameAndLoc() + "'" : null) + "," +
//                  " pmcc= " +
//                  (request.getMcc() != null ? "'" + request.getMcc() + "'" : null) +
//                  "," +
//                  " pdevice_id= " +
//                  (request.getDeviceId() != null ?
//                   "'" + request.getDeviceId() + "'" : null) + "," +
//                  " pdevice_type= " +
//                  (request.getDeviceType() != null ?
//                   "'" + request.getDeviceType() + "'" : null) + "," +
//                  " pdesc= " +
//                  (request.getDescription() != null ?
//                   "'" + request.getDescription() + "'" : null) + "," +
//                  " papply_fee= " +
//                  (request.getApplyFee() != null ?
//                   "'" + request.getApplyFee() + "'" : null) +","+
//                  " pexecute_internal='N'"+")"
//                  );

     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Query prepared for calling Set Linked Cards --- request.getCardNo()---> " + request.getCardNo()
                                + "<---request.getSecCardNo()-->" + request.getSecCardNo()
                                + "<---request.getLinkType()-->" + request.getLinkType()
                                + "<---request.getAcquirerId()-->" + request.getAcquirerId()
                                + "<---request.getCardAcceptorId()-->" + request.getCardAcceptorId()
                                + "<---request.getCardAcceptNameAndLoc()-->" + request.getCardAcceptNameAndLoc()
                                + "<---request.getMcc()-->" + request.getMcc()
                                + "<---request.getDeviceId()-->" + request.getDeviceId()
                                + "<---request.getDeviceType()-->" + request.getDeviceType()
                                + "<---request.getDescription()-->" + request.getDescription()
                                + "<---request.getApplyFee()-->" + request.getApplyFee()
                                + "<---request.getAcqData1()-->" + request.getAcqData1()
                                + "<---request.getAcqData2()-->" + request.getAcqData2()
                                + "<---request.getAcqData3()-->" + request.getAcqData3()
                                + "<---request.getAcqUsrId()-->" + request.getAcqUsrId()
                                + "<---request.getRetreivalRefNum()-->" + request.getRetreivalRefNum());


     query.append("execute procedure set_linked_cards(pcard_no = ?,p_lnk_card = ?,p_lnk_type = ?,pacq_id = ?,pcard_acpt_id = ?,pcard_acpt_nameloc = ?,pmcc = ?,pdevice_id = ?,pdevice_type = ?,pdesc = ?,papply_fee = ?, psub_srv = ?, pdata_2 = ?,pdata_3 = ?,pacq_userid = ?, pretrieval_ref = ?)");

     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Query prepared for calling SET_LINKED_CARDS--->" +
                                     query);

     cstmt = con.prepareCall(query.toString());

     cstmt.setString(1,request.getCardNo());
     cstmt.setString(2, request.getSecCardNo());
     cstmt.setString(3, request.getLinkType());
     cstmt.setString(4, request.getAcquirerId());
     cstmt.setString(5, request.getCardAcceptorId());
     cstmt.setString(6, request.getCardAcceptNameAndLoc());
     cstmt.setString(7, request.getMcc());
     cstmt.setString(8, request.getDeviceId());
     cstmt.setString(9, request.getDeviceType());
     cstmt.setString(10, request.getDescription());
     cstmt.setString(11, request.getApplyFee());
     cstmt.setString(12, request.getAcqData1());
     cstmt.setString(13, request.getAcqData2());
     cstmt.setString(14, request.getAcqData3());
     cstmt.setString(15, request.getAcqUsrId());
     cstmt.setString(16, request.getRetreivalRefNum());

     rs = cstmt.executeQuery();

     String respCode = null;
     String respDesc = null;
     String transId = null;
     String fee = null;
     String cardNo=null;
     if(rs.next()){
       if (rs.getString(1) != null && rs.getString(1).trim().length() > 0)
         cardNo = rs.getString(1);
       if (rs.getString(2) != null && rs.getString(2).trim().length() > 0)
         respCode = rs.getString(2);
       if (rs.getString(3) != null && rs.getString(3).trim().length() > 0)
         respDesc = rs.getString(3);
       if (rs.getString(4) != null && rs.getString(4).trim().length() > 0)
         transId = rs.getString(4);
       if (rs.getString(5) != null && rs.getString(5).trim().length() > 0)
         fee = rs.getString(5);
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                       "Resposne Received from API ---Card Number--->" +
                                       cardNo +
                                       "<---Response Code--->" + respCode +
                                       "<---Response desc--->" + respDesc +
                                       "<---Transaction ID--->" + transId +
                                       "<---Fee --->" + fee);
     }

     response = new ServicesResponseObj();
     if (respCode != null && respCode.trim().length() >= 0) {
       response.setRespCode(respCode);
       response.setRespDesc(respDesc);
       response.setTransId(transId);
       response.setFeeAmount(fee);
       //response.setLinkedCards();
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                       " Linked Card Attributes has been setted--->" );
     }
     else {
       response.setRespCode("06");
       response.setRespDesc("No Response received from API");
     }

   }
   catch (Exception ex) {
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                     "Exception in SET_LINKED_CARDS using API--->" +
                                     ex);
    throw ex;
   }
   finally {
     try {
       if (rs != null) {
         rs.close();
       }
       if (cstmt != null) {
         cstmt.close();
       }
     }
     catch (SQLException ex1) {
     }
   }
   return response;
 } //end get linked cards


 /**
  * This method is used to remove the link between the cards (Primary card(s) to Secondary Card)
  * It calls the database stored procedure "UNLINK_CARDS" to remove the links between the cards.
  * A response object is created and its attributes are initlized with the parameter returned from
  * the database procedure. At the end of the processing it returns the response object which describes
  * the status of the processing to the client. In case of any exception the service returns the response
  * object with response code "96" which describes the error in detail.
  * @param request ServicesRequestObj
  * @return ServicesResponseObj
  */
 public ServicesResponseObj unLinkeCards(ServicesRequestObj request) throws Exception{

   ServicesResponseObj response = null;
   StringBuffer query = new StringBuffer();
   CallableStatement cstmt = null;
   ResultSet rs = null;
   try {
//     query.append("Execute procedure unlink_cards( " +
//                  "p_primary_card_no= " +
//                  (request.getCardNo() != null ?
//                   "'" + request.getCardNo() + "'" : null) + "," +
//                  " p_secondary_card_no = "+
//                  (request.getSecCardNo()!= null ?
//                   "'" + request.getSecCardNo()  + "'" : null) + "," +
//                  " p_acq_id = "+
//                  (request.getAcquirerId()!= null ?
//                   "'" + request.getAcquirerId()  + "'" : null) + "," +
//                  " p_card_acpt_id = "+
//                  (request.getCardAcceptorId()!= null ?
//                   "'" + request.getCardAcceptorId()  + "'" : null) + "," +
//                  " p_card_acpt_nameloc = "+
//                  (request.getCardAcceptNameAndLoc()!= null ?
//                   "'" + request.getCardAcceptNameAndLoc()  + "'" : null) + "," +
//                  " p_mcc = "+
//                  (request.getMcc()!= null ?
//                   "'" + request.getMcc()  + "'" : null) + "," +
//                  " p_device_id = "+
//                  (request.getDeviceId()!= null ?
//                   "'" + request.getDeviceId()  + "'" : null) + "," +
//                  " p_device_type = "+
//                  (request.getDeviceType()!= null ?
//                   "'" + request.getDeviceType()  + "'" : null) + "," +
//                  " p_desc = "+
//                  (request.getDescription()!= null ?
//                   "'" + request.getDescription()  + "'" : null) + "," +
//                  " p_apply_fee = "+
//                  (request.getApplyFee()!= null ?
//                   "'" + request.getApplyFee()  + "'" : null) + " )" );

     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Query prepared for calling UnLink Cards --- request.getCardNo()---> " + request.getCardNo()
                                + "<---request.getSecCardNo()-->" + request.getSecCardNo()
                                + "<---request.getAcquirerId()-->" + request.getAcquirerId()
                                + "<---request.getCardAcceptorId()-->" + request.getCardAcceptorId()
                                + "<---request.getCardAcceptNameAndLoc()-->" + request.getCardAcceptNameAndLoc()
                                + "<---request.getMcc()-->" + request.getMcc()
                                + "<---request.getDeviceId()-->" + request.getDeviceId()
                                + "<---request.getDeviceType()-->" + request.getDeviceType()
                                + "<---request.getDescription()-->" + request.getDescription()
                                + "<---request.getApplyFee()-->" + request.getApplyFee()
                                + "<---request.getAcqData1()-->" + request.getAcqData1()
                                + "<---request.getAcqData2()-->" + request.getAcqData2()
                                + "<---request.getAcqData3()-->" + request.getAcqData3()
                                + "<---request.getAcqUsrId()-->" + request.getAcqUsrId()
                                + "<---request.getRetreivalRefNum()-->" + request.getRetreivalRefNum());


     query.append("execute procedure unlink_cards(p_primary_card_no = ? , p_secondary_card_no = ? , p_acq_id = ? , p_card_acpt_id = ? , p_card_acpt_nameloc = ? , p_mcc = ? , p_device_id = ? , p_device_type = ? , p_desc = ? , p_apply_fee = ? , psub_srv = ? , pdata_2 = ? , pdata_3 = ?, pacq_userid = ?, pretrieval_ref = ?)");

     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Query prepared for calling UNINKED_CARDS--->" +
                                     query);

     cstmt = con.prepareCall(query.toString());

     cstmt.setString(1,request.getCardNo());
     cstmt.setString(2, request.getSecCardNo());
     cstmt.setString(3, request.getAcquirerId());
     cstmt.setString(4, request.getCardAcceptorId());
     cstmt.setString(5, request.getCardAcceptNameAndLoc());
     cstmt.setString(6, request.getMcc());
     cstmt.setString(7, request.getDeviceId());
     cstmt.setString(8, request.getDeviceType());
     cstmt.setString(9, request.getDescription());
     cstmt.setString(10, request.getApplyFee());
     cstmt.setString(11, request.getAcqData1());
     cstmt.setString(12, request.getAcqData2());
     cstmt.setString(13, request.getAcqData3());
     cstmt.setString(14, request.getAcqUsrId());
     cstmt.setString(15, request.getRetreivalRefNum());

     rs = cstmt.executeQuery();

     String respCode = null;
     String respDesc = null;
     String transId = null;
     String feeAmt = null;
     if(rs.next()){
       respCode = rs.getString(1);
       respDesc = rs.getString(2);
       transId = rs.getString(3);
       feeAmt = rs.getString(4);

       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                       "<----Resposne Received from API---->" +
                                       "<---Response Code--->" + respCode +
                                       "<---Response desc--->" + respDesc+
                                       "<---Trannsaction Id--->" + transId+
                                       "<---Fee Amount--->" + feeAmt);
     }

     response = new ServicesResponseObj();
     if (respCode != null && respCode.trim().length() >= 0) {
       response.setRespCode(respCode);
       response.setRespDesc(respDesc);
       if(response.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)){
         response.setTransId(transId);
         response.setFeeAmount(feeAmt);
       }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                       " Cards Un-Linking has been done successfully--->" );
     }
     else {
       response.setRespCode("06");
       response.setRespDesc("No Response received from API");
     }

   }
   catch (Exception ex) {
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                     "Exception in UNLINK_CARDS using API--->" +
                                     ex);
    throw ex;

   }
   finally {
     try {
       if (rs != null) {
         rs.close();
       }
       if (cstmt != null) {
         cstmt.close();
       }
     }
     catch (SQLException ex1) {
     }
   }
   return response;
 } //end get linked cards


 /**
  * This method is used to transfer the funds from the given card no to the linked card(s).
  * It calls the database stored procedure "LINKED_CRD_TRANSFER" to transfer the funds to the linked cards.
  * A response object is created and its attributes are initlized with the parameters returned from
  * the database procedure. At the end of the processing it returns the response object which describes
  * the status of the processing to the client. In case of any exception the service returns the response
  * object with response code "96" which describes the error in detail.
  * @param request ServicesRequestObj
  * @return ServicesResponseObj
  */

  public ServicesResponseObj linkedCardTransfer(ServicesRequestObj request) throws Exception{

    ServicesResponseObj response = null;
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;
    java.util.Vector linkedCardList = new java.util.Vector();

    try {
//      query.append("Execute procedure LNKD_CRD_TRANSFER( " +
//                   "pcard_no_from= " +
//                   (request.getCardNo() != null ?
//                    "'" + request.getCardNo() + "'" : null) + "," +
//                   " pamount= " +
//                   (request.getAmount() != null ?
//                    "'" + request.getAmount() + "'" : null) + "," +
//                   " pcard_no_to= " + (request.getToCardNo() != null ? "'" + request.getToCardNo() + "'"  : null) +
//                   "," +
//                   " pacq_id= " +
//                   (request.getAcquirerId() != null ?
//                    "'" + request.getAcquirerId() + "'" : null) + "," +
//                   " pcard_acpt_id= " +
//                   (request.getCardAcceptorId() != null ?
//                    "'" + request.getCardAcceptorId() + "'" : null) + "," +
//                   " pcard_acpt_nameloc= " +
//                   (request.getCardAcceptNameAndLoc() != null ?
//                    "'" + request.getCardAcceptNameAndLoc() + "'" : null) + "," +
//                   " pmcc= " +
//                   (request.getMcc() != null ? "'" + request.getMcc() + "'" : null) +
//                   "," +
//                   " pdevice_id= " +
//                   (request.getDeviceId() != null ?
//                    "'" + request.getDeviceId() + "'" : null) + "," +
//                   " pdevice_type= " +
//                   (request.getDeviceType() != null ?
//                    "'" + request.getDeviceType() + "'" : null) + "," +
//                   " pdesc= " +
//                   (request.getDescription() != null ?
//                    "'" + request.getDescription() + "'" : null) + "," +
//                   " papply_fee= " +
//                   (request.getApplyFee() != null ?
//                    "'" + request.getApplyFee() + "'" : null) + ")"
//                   );

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling Linked Card Transfer --- request.getCardNo()---> " + request.getCardNo()
                                      + "<---request.getAmount()-->" + request.getAmount()
                                      + "<---request.getToCardNo()-->" + request.getToCardNo()
                                      + "<---request.getAcquirerId()-->" + request.getAcquirerId()
                                      + "<---request.getCardAcceptorId()-->" + request.getCardAcceptorId()
                                      + "<---request.getCardAcceptNameAndLoc()-->" + request.getCardAcceptNameAndLoc()
                                      + "<---request.getMcc()-->" + request.getMcc()
                                      + "<---request.getDeviceId()-->" + request.getDeviceId()
                                      + "<---request.getDeviceType()-->" + request.getDeviceType()
                                      + "<---request.getDescription()-->" + request.getDescription()
                                      + "<---request.getApplyFee()-->" + request.getApplyFee()
                                      + "<---request.getAcqData1()-->" + request.getAcqData1()
                                      + "<---request.getAcqData2()-->" + request.getAcqData2()
                                      + "<---request.getAcqData3()-->" + request.getAcqData3()
                                      + "<---request.getAcqUsrId()-->" + request.getAcqUsrId()
                                      + "<---request.getRetreivalRefNum()-->" + request.getRetreivalRefNum());


      query.append("execute procedure lnkd_crd_transfer( pcard_no_from  = ?,pamount = ? ,pcard_no_to = ?,pacq_id = ? ,pcard_acpt_id  = ? ,pcard_acpt_nameloc  = ? ,pmcc = ?,pdevice_id = ? ,pdevice_type = ? ,pdesc  = ? ,papply_fee  = ? ,psub_srv  = ? ,pdata_2  = ? ,pdata_3  = ? ,pacq_userid  = ? ,pretrieval_ref  = ?)");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling LNKD_CRD_TRANSFER--->" +
                                      query);

      cstmt = con.prepareCall(query.toString());

      cstmt.setString(1,request.getCardNo());
      cstmt.setString(2, request.getAmount());
      cstmt.setString(3, request.getToCardNo());
      cstmt.setString(4, request.getAcquirerId());
      cstmt.setString(5, request.getCardAcceptorId());
      cstmt.setString(6, request.getCardAcceptNameAndLoc());
      cstmt.setString(7, request.getMcc());
      cstmt.setString(8, request.getDeviceId());
      cstmt.setString(9, request.getDeviceType());
      cstmt.setString(10, request.getDescription());
      cstmt.setString(11, request.getApplyFee());
      cstmt.setString(12, request.getAcqData1());
      cstmt.setString(13, request.getAcqData2());
      cstmt.setString(14, request.getAcqData3());
      cstmt.setString(15, request.getAcqUsrId());
      cstmt.setString(16, request.getRetreivalRefNum());


      rs = cstmt.executeQuery();

      response = new ServicesResponseObj();
      if (rs.next()) {
        response.setRespCode(rs.getString(1));
        response.setRespDesc(rs.getString(2));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- Response Code--->" +
                                        response.getRespCode() +
                                        "<---Response desc--->" +
                                        response.getRespDesc());
        if (response.getRespCode() != null &&
            response.getRespCode().trim().length() > 0) {
          if (rs.getString(3) != null && rs.getString(3).trim().length() > 0)
            response.setCardBalance(rs.getString(3));
          if (rs.getString(4) != null && rs.getString(4).trim().length() > 0)
            response.setToCardBalance(rs.getString(4));
          if (rs.getString(5) != null && rs.getString(5).trim().length() > 0)
            response.setTransId(rs.getString(5));
          if (rs.getString(6) != null && rs.getString(6).trim().length() > 0)
            response.setFeeAmount(rs.getString(6));
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Resposne Received from API --- Transaction ID--->" +
                                          response.getTransId() +
                                          "<---FROM Balance--->" +
                                          response.getCardBalance() +
                                          "<---TO Account Balance--->" +
                                          response.getToCardBalance() +
                                          "<---Fee Amount-->" +
                                          response.getFeeAmount());
        }
        else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Resposne Received from API --- No response received returning 06");
          response.setRespCode("06");
          response.setRespDesc("No Response Received");
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- No response received --- returning 06");
        response.setRespCode("06");
        response.setRespDesc("No Response Received");
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in LINKED_CARD_Transfer using API--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  } //end linked cards transfer

  /**
 * This methods is used to do the reversal of the already made VAS Pre-Authorzied transaction.
 * It calls the database stored procedure "VAS_PREAUTH_REVERSAL" to reverse the pre-authorized
 * transaction. A response object is created and its attributes are initlized with the parameters returned
 * from the database procedure. At the end of the processing it returns the response object which
 * describes the status of the processing to the client. In case of any exception the service returns the
 * response object with response code "96" which describes the error in detail.
 * @param request ServicesRequestObj
 * Method which takes the request as parameter and perform the reversal of the transaction.
 * @return ServicesResponseObj
 */

  public ServicesResponseObj doVASPreAuthReversal(ServicesRequestObj request) throws
      Exception {
    ServicesResponseObj response = null;
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;
    try {

//      query.append("Execute procedure vas_preauth_reversal( " +
//                   "pvas_tan= " + request.getTransId() + "," +
//                   "pcard_no= " + (request.getCardNo() != null ? "'" + request.getCardNo() + "'" : null) + "," +
//                   "pvas_type_id= " + (request.getVasAccountType() != null ? "'" + request.getVasAccountType() + "'" : null) + "," +
//                   "pamount= " + request.getAmount() + "," +
//                   "pacq_id= " + (request.getAcquirerId() != null ? "'" + request.getAcquirerId() + "'" : null) + "," +
//                   "pcard_acpt_id= " + (request.getCardAcceptorId() != null ? "'" + request.getCardAcceptorId() + "'" : null) + "," +
//                   "pcard_acpt_nameloc= " + (request.getCardAcceptNameAndLoc() != null ? "'" + request.getCardAcceptNameAndLoc() + "'" : null) + "," +
//                   "pmcc= " + (request.getMcc() != null ? "'" + request.getMcc() + "'" : null) +  "," +
//                   "pdevice_id= " + (request.getDeviceId() != null ? "'" + request.getDeviceId() + "'" : null) + "," +
//                   "pdevice_type= " + (request.getDeviceType() != null ? "'" + request.getDeviceType() + "'" : null) + "," +
//                   "pdesc= " + (request.getDescription() != null ? "'" + request.getDescription() + "'" : null) + "," +
//                   "apply_fee= " + (request.getApplyFee() != null ? "'" + request.getApplyFee() + "'" : null) + ")"
//                   );

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS PreAuth Completion --- request.getCardNo()---> " + request.getCardNo()
                                      + "<---request.getTransId()-->" + request.getTransId()
                                      + "<---request.getVasAccountType()-->" + request.getVasAccountType()
                                      + "<---request.getAmount()-->" + request.getAmount()
                                      + "<---request.getAcquirerId()-->" + request.getAcquirerId()
                                      + "<---request.getCardAcceptorId()-->" + request.getCardAcceptorId()
                                      + "<---request.getCardAcceptNameAndLoc()-->" + request.getCardAcceptNameAndLoc()
                                      + "<---request.getMcc()-->" + request.getMcc()
                                      + "<---request.getDeviceId()-->" + request.getDeviceId()
                                      + "<---request.getDeviceType()-->" + request.getDeviceType()
                                      + "<---request.getDescription()-->" + request.getDescription()
                                      + "<---request.getApplyFee()-->" + request.getApplyFee()
                                      + "<---request.getAcqData1()-->" + request.getAcqData1()
                                      + "<---request.getAcqData2()-->" + request.getAcqData2()
                                      + "<---request.getAcqData3()-->" + request.getAcqData3()
                                      + "<---request.getAcqUsrId()-->" + request.getAcqUsrId()
                                      + "<---request.getRetreivalRefNum()-->" + request.getRetreivalRefNum());

      query.append("execute procedure vas_preauth_reversal( pvas_tan = ?, pcard_no = ?, pvas_type_id = ?, pamount = ?, pacq_id = ?, pcard_acpt_id = ?, pcard_acpt_nameloc = ?, pmcc = ?, pdevice_id = ?	, pdevice_type = ?, pdesc = ?, apply_fee = ?, psub_srv = ?, pdata_2 = ?	, pdata_3 = ?, pacq_userid = ?, pretrieval_ref = ?, pvendor_id = ?)");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query prepared for calling VAS PreAuth Completion--->" +
                                      query);

      cstmt = con.prepareCall(query.toString());

      cstmt.setString(1, request.getTransId());
      cstmt.setString(2, request.getCardNo());
      cstmt.setString(3, request.getVasAccountType());
      cstmt.setString(4, request.getAmount());
      cstmt.setString(5, request.getAcquirerId());
      cstmt.setString(6, request.getCardAcceptorId());
      cstmt.setString(7, request.getCardAcceptNameAndLoc());
      cstmt.setString(8, request.getMcc());
      cstmt.setString(9, request.getDeviceId());
      cstmt.setString(10, request.getDeviceType());
      cstmt.setString(11, request.getDescription());
      cstmt.setString(12, request.getApplyFee());
      cstmt.setString(13, request.getAcqData1());
      cstmt.setString(14, request.getAcqData2());
      cstmt.setString(15, request.getAcqData3());
      cstmt.setString(16, request.getAcqUsrId());
      cstmt.setString(17, request.getRetreivalRefNum());
      cstmt.setString(18, request.getVasVendorId());

      rs = cstmt.executeQuery();
      response = new ServicesResponseObj();
      if (rs.next()) {
        response.setRespCode(rs.getString(1));
        response.setRespDesc(rs.getString(2));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- Response Code--->" +
                                        response.getRespCode() +
                                        "<---Response desc--->" +
                                        response.getRespDesc());
        if (response.getRespCode() != null &&
            response.getRespCode().trim().length() > 0) {
          if (response.getRespCode().trim().equalsIgnoreCase(Constants.
              SUCCESS_CODE)) {
            if (rs.getString(3) != null && rs.getString(3).trim().length() > 0)
              response.setCardBalance(rs.getString(3));
            if (rs.getString(4) != null && rs.getString(4).trim().length() > 0)
              response.setTransId(rs.getString(4));
            if (rs.getString(5) != null && rs.getString(5).trim().length() > 0)
              response.setFeeAmount(rs.getString(5));

              //response.setTransCat("V");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Resposne Received from API --- Transaction ID--->" +
                                            response.getTransId() +
                                            "<---VAS Balance--->" +
                                            response.getCardBalance() +
                                            "<---Fee Amount-->" +
                                            response.getFeeAmount() +
                                            "<---Trans Cat-->" +
                                            response.getTransCat());

          } //only provide other attributes in case of 00 response code
        }
        else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Resposne Received from API --- No response received returning 06");
          response.setRespCode("06");
          response.setRespDesc("No Response Received");
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- No response received --- returning 06");
        response.setRespCode("06");
        response.setRespDesc("No Response Received");
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception while doing VAS Pre-Atuth Reversal using API--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  }

  public ServicesResponseObj getVASAccounts(ServicesRequestObj request) throws
      Exception {
    ServicesResponseObj response = null;
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;
    Vector vasAccountsInfo = new Vector();
    VASAccountsInfoObj vasAccount = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for getting VAS accounts --- getCardNo()--->" + request.getCardNo()
                                      + "<--getVasAccountType--->" + request.getVasAccountType()
                                      + "<---getVasVendorId-->" + request.getVasVendorId()
                                      + "<--getAcquirerId--->" + request.getAcquirerId()
                                      + "<---getCardAcceptorId-->" + request.getCardAcceptorId()
                                      + "<--getCardAcceptNameAndLoc--->" + request.getCardAcceptNameAndLoc()
                                      + "<--getMcc--->" + request.getMcc()
                                      + "<---getDeviceId-->" + request.getDeviceId()
                                      + "<--getDeviceType--->" + request.getDeviceType()
                                      + "<---getAcqUsrId-->" + request.getAcqUsrId()
                                      + "<---getAcqData1-->" + request.getAcqData1()
                                      + "<---getAcqData2-->" + request.getAcqData2()
                                      + "<---getAcqData3-->" + request.getAcqData3()
                                      + "<---getDescription-->" + request.getDescription()
                                      + "<--getApplyFee--->" + request.getApplyFee()
                                      + "<---getRetreivalRefNum-->" + request.getRetreivalRefNum()
                                      + "<---getIsAdminTrans()-->" + request.getIsAdminTrans());

      query.append("execute procedure get_vas_acct(pcard_no = ? ,pvendor_id = ? ,pvas_type_id = ? ,pacq_id = ? ,pcard_acpt_id = ? ,pcard_acpt_nameloc = ? ,pmcc = ? ,pdevice_id = ? ,pdevice_type = ? ,psub_srv = ? ,pdata_2 = ? ,pdata_3 = ? ,pacq_userid = ? ,pretrieval_ref = ? ,p_vas_trans_desc = ? ,papply_fee = ?, p_is_admin_trans = ?)");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting VAS account number--->" + query);

      cstmt = con.prepareCall(query.toString());

      cstmt.setString(1,request.getCardNo());
      cstmt.setString(2,request.getVasVendorId());
      cstmt.setString(3,request.getVasAccountType());
      cstmt.setString(4,request.getAcquirerId());
      cstmt.setString(5,request.getCardAcceptorId());
      cstmt.setString(6,request.getCardAcceptNameAndLoc());
      cstmt.setString(7,request.getMcc());
      cstmt.setString(8,request.getDeviceId());
      cstmt.setString(9,request.getDeviceType());
      cstmt.setString(10,request.getAcqData1());
      cstmt.setString(11,request.getAcqData2());
      cstmt.setString(12,request.getAcqData3());
      cstmt.setString(13,request.getAcqUsrId());
      cstmt.setString(14,request.getRetreivalRefNum());
      cstmt.setString(15,request.getDescription());
      cstmt.setString(16,request.getApplyFee());
      cstmt.setString(17,request.getIsAdminTrans());

      rs = cstmt.executeQuery();
      response = new ServicesResponseObj();

      String respCode = null;
      String respDesc = null;
      String transId = null;
      String fee = null;
      String vasAcctNum = null;
      String vasAcctType = null;
      String vasVendor = null;

      while (rs.next()) {
        vasAcctNum = rs.getString(1);
        vasVendor = rs.getString(2);
        vasAcctType = rs.getString(3);
        respCode = rs.getString(4);
        respDesc = rs.getString(5);
        transId = rs.getString(6);
        fee = rs.getString(7);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Resposne Received from API --- VasAccountNumber--->" + vasAcctNum
                                        + "<---VasVendor--->" + vasVendor
                                        + "<---VasAccountType--->" + vasAcctType
                                        + "<---RespCode--->" + respCode
                                        + "<---RespDesc--->" + respDesc
                                        + "<---TransId--->" + transId
                                        + "<---Fee--->" + fee);
        if(vasAcctNum != null && vasAcctNum.trim().length() > 0
           && vasAcctType != null && vasAcctType.trim().length() > 0
           && vasVendor != null && vasVendor.trim().length() > 0){
          vasAccount = new VASAccountsInfoObj(vasAcctNum, vasAcctType,
                                              vasVendor);
          vasAccountsInfo.add(vasAccount);
        }
      }//end while
      response = new ServicesResponseObj();
      if (respCode != null && respCode.trim().length() >= 0) {
        response.setRespCode(respCode.trim());
        response.setRespDesc(respDesc);
        if(response.getRespCode().equals(Constants.SUCCESS_CODE)){
          if (transId != null) {
            response.setTransId(transId);
          }
          response.setFeeAmount(fee);
          response.setVasAccountList(vasAccountsInfo);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          " Vas Account List size--->" +
                                          vasAccountsInfo.size());
        }
      }else {
        response.setRespCode("06");
        response.setRespDesc("No Response received from API");
      }
    }catch (Exception ex) {
      ex.printStackTrace();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting VAS Account Number--->" +
                                      ex);
      throw ex;
    }finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return response;
  }
}

