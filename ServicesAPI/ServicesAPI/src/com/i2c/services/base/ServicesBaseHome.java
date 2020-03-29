package com.i2c.services.base;

import java.sql.*;
import java.util.*;
import com.i2c.services.util.*;
import com.i2c.services.*;
import com.i2c.transferapi.*;
import com.i2c.solspark.*;

/**
 * <p>Title: ServiceBaseHome: This class does the database interaction for fetching the desired records </p>
 * <p>Description: This class is a base class for all the home classes. It mainly interacts with the
 * database to fetch the data and to return the result to outside world. For example if we want to find the
 * card record then we will use this class which returns the detailed information about the given card.</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc </p>
 * @author MCP Backend Team
 * @version 1.0
 */

public abstract class ServicesBaseHome {

  //Connection object will be inherited in all the child classes
  protected Connection con = null;

  /**
   * Constructor
   */

  public ServicesBaseHome() {

  } //end Construtor ServicesBaseHome

  /**
   * This method executes the provided query. Normally it is used to tests the database connectivity i.e.
   * whether database connection is properly established or not.
   * @param query String -- Query to execute
   * @throws Exception
   */

  public void executeQuery(String query) throws Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_FINEST), "Query Received -- > " + query);

    Statement stmt = null;

    try {
      //create the statement
      stmt = con.createStatement();

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST), "Going to execute the query......");

      //execute the query
      stmt.executeUpdate(query);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST), "Query has been executed successfully");
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + ex.getMessage());
      //throw the exception back
      throw ex;
    } //end catch
    finally {
      try {
        if (stmt != null) stmt.close();
      } //end try
      catch (Exception ex) {} //end
    } //end finally
  } //end insertValues

  /**
   * This method takes the query as parameter, executes the query and returns the first column
   * value of the queried table. Normally it is used to check whether a specific value exists in the
   * database table.
   * @param query String
   * @throws Exception
   * @return String
   */

  public String getValue(String query) throws Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Query -- > " + query);
    Statement stmt = null;
    ResultSet rs = null;
    try {
      //create the statement
      stmt = con.createStatement();

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST), "Going to execute the query......");
      //execute the statement
      rs = stmt.executeQuery(query);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST), "Query Executed......");

      if (rs.next()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Found value -- > " + rs.getString(1));
        //return the value
        return (rs.getString(1) != null ? rs.getString(1).trim() :
                rs.getString(1));
      } //end if

      return null;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
      } //end try
      catch (Exception ex) {} //end
    } //end finally
  } //end getValue

  /**
   * This method takes the query as parameter, executes the query and returns the string array which
   * contains the result of the query. In case if the query is not executed due to any reason this method
   * throws the exception which describes the cause of the error.
   * @param query String
   * @throws Exception -- SqlException which describe the cause of the error.
   * @return String[]
   */

  public String[] getValues(String query) throws Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "Query -- > " + query);
    Statement stmt = null;
    ResultSet rs = null;
    try {
      //create the statement
      stmt = con.createStatement();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Going to execute the query......");
      //execute the statement
      rs = stmt.executeQuery(query);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Query Executed......");
      if (rs.next()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Found values...");
        ResultSetMetaData rsmtdt = rs.getMetaData();
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Total Columns Retrieved -- > " +
                                        rsmtdt.getColumnCount());
        String[] str = new String[rsmtdt.getColumnCount()];
        for (int i = 0; i < rsmtdt.getColumnCount(); i++) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST), "Value " + (i + 1) + " -- > " + rs.getString(i + 1));
          //get the value;
          str[i] = rs.getString(i + 1);
        } //end for
        //return the value
        return str;
      } //end if

      return null;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
      } //end try
      catch (Exception ex) {} //end
    } //end finally
  } //end getValues

  /**
   * This method is used to execute the procedure and returns the result of the executed procedure
   * in the form of the string array. In case of any error during the query execution this method
   * throws the exception which describes the cause of the error in detail.
   * @param query String
   * @throws Exception
   * @return String[]
   */

  public String[] getProcedureVal(String query) throws Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "Query Received -- > " + query);
    CallableStatement cstmt = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Going to execute the query");
      cstmt = con.prepareCall(query);
      rs = cstmt.executeQuery();
      if (rs.next()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Got the value -- > " + rs.getString(1));
        //get the resultset meta data
        ResultSetMetaData rsmtdt = rs.getMetaData();
        String[] str = new String[rsmtdt.getColumnCount()];
        for (int i = 0; i < str.length; i++) {
          str[i] = rs.getString(i + 1);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST), "Got the Value -- > " + str[i]);
        } //end for
        return str;
      } //end if
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Value not found...Returning null..");
      return null;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null) rs.close();
        if (cstmt != null) cstmt.close();
      } //end try
      catch (Exception ex) {} //end
    } //end finally
  } //end getProcedureVal

  /**
   * This method is used to insert values into database table. It returns the id (serial no) for the
   * inserted value. In other words it returns serial no. of the executed query.
   * @param insertquery String, query to be executed
   * @throws Exception
   * @return long, id for insertion item
   */

  public long insertValues(String insertquery) throws Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "Insert Values :: Query -- > " +
                                    insertquery);
    long serialNo = -1;
    Statement smt = null;
    String serialQuery = "";
    try {
      smt = con.createStatement();
      smt.executeUpdate(insertquery);
      smt.close();
      //Getting the serial number condition
      if (insertquery.toLowerCase().indexOf(Constants.INSERT_QUERY_INTO_VALUE.
                                            toLowerCase()) >
          -1) {
        serialQuery = Constants.SERIAL_QUERY + " " + insertquery.substring(
            insertquery.toLowerCase().indexOf(Constants.INSERT_QUERY_INTO_VALUE.
                                              toLowerCase()) +
            Constants.INSERT_QUERY_INTO_VALUE.length(),
            insertquery.
            toLowerCase().indexOf(Constants.INSERT_QUERY_COLUMN_START_VALUE.
                                  toLowerCase()));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST), "Serial No Query -- > " + serialQuery);

        //get the max value
        serialNo = getMaxValue(serialQuery) - 1;
      } //end serial number if
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST), "Serial No for inserted record -- > " + serialNo);

      return serialNo;
    }
    catch (SQLException e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + e.getMessage());
      throw e;
    }
    catch (Exception e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + e.getMessage());
      throw e;
    }
    finally {
      try {
        if (smt != null)
          smt.close();
      } //end try
      catch (Exception ex) {}
    } //end finally
  } //end insertValues

  /**
   * This method is used to get the maximum value in the specific table against the specific column.
   * It takes the query as the parameter, executes the query and returns the maximum value of the
   * specified table's column.
   * @param queryString Query
   * @throws Exception
   * @return long maximumj value found in this query
   */

  public long getMaxValue(String queryString) throws Exception {

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_FINEST), "getMaxVlaue:: Query -- > " + queryString);

    Statement smt = null;
    ResultSet rs = null;
    long seqcount = 1;
    try {
      smt = con.createStatement();
      rs = smt.executeQuery(queryString);

      if (rs.next()) {
        seqcount = rs.getLong(1) + 1;
      } //end if
      return seqcount;
    }
    catch (SQLException e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + e.getMessage());
      throw e;
    }
    catch (Exception e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + e.getMessage());
      throw e;
    }
    finally {
      try {
        if (rs != null)
          rs.close();
        if (smt != null)
          smt.close();
      } //end try
      catch (Exception ex) {}
    } //end finally
  } //end getMaxVlaue

  /**
   * This method is used to log the transaction in the database. Basically this method is used to call
   * the "Log_Transaction" procedure which inserts the transaction record in the database and returns the
   * response to the calling routines which contains the description of the logged transaction.
   * @param cardNo String
   * @param serviceId String
   * @param deviceType String
   * @param description String
   * @param retRefNo String
   * @param deviceId String
   * @param cardAcceptorId String
   * @param cardAcceptorNameAndLoc String
   * @param mcc String
   * @param accountNo String
   * @throws Exception
   * @return String[]
   */

  public String[] logTransaction(String cardNo,
                                 String serviceId,
                                 String deviceType,
                                 String description,
                                 String retRefNo,
                                 String deviceId,
                                 String cardAcceptorId,
                                 String cardAcceptorNameAndLoc,
                                 String mcc,
                                 String accountNo, String acquirerId) throws
      Exception {
    return logTransaction(cardNo, serviceId, deviceType, retRefNo, "0200",
                          "0.0", description, "0", "00", deviceId,
                          cardAcceptorId, cardAcceptorNameAndLoc, mcc,
                          accountNo, acquirerId);
  } //end log transaction

  /**
   * This method is used to log the transaction in mcp system. Its basic purpose is keep the record
   * of the operation which are being performed.
   * @param cardNo String
   * @param serviceId String
   * @param deviceType String
   * @param retRefNo String
   * @param msgType String
   * @param amount String
   * @param description String
   * @param entryType String
   * @throws Exception
   * @return String
   */
//  public String[] logTransaction(String cardNo,
//                                 String serviceId,
//                                 String deviceType ,
//                                 String retRefNo,
//                                 String msgType,
//                                 String amount,
//                                 String description,
//                                 String entryType,
//                                 String respCode) throws Exception
//  {
//    return logTransaction(cardNo,serviceId,deviceType,retRefNo,msgType,amount,description,entryType,respCode,null,null,null,"0");
//  }//end log transaction

  /**
   * This method is used to log the transaction in mcp system. It calls the database stored procedure
   * "Log_Transaction" which insert the information of the transaction into the database.
   * @param cardNo String
   * @param serviceId String
   * @param deviceType String
   * @param retRefNo String
   * @param msgType String
   * @param amount String
   * @param description String
   * @param entryType String
   * @param respCode String
   * @param deviceId String
   * @param cardAcceptorCode String
   * @param cardAcceptorNameAndLoc String
   * @param merchantCatCode String
   * @param accountNo String
   * @throws Exception
   * @return String
   */

  public String[] logTransaction(String cardNo,
                                 String serviceId,
                                 String deviceType,
                                 String retRefNo,
                                 String msgType,
                                 String amount,
                                 String description,
                                 String entryType,
                                 String respCode,
                                 String deviceId,
                                 String cardAcceptorCode,
                                 String cardAcceptorNameAndLoc,
                                 String merchantCatCode,
                                 String accountNo, String acquirerId) throws
      Exception {

    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "Args :: Card No -- > " + cardNo + "\n" +
                                    "Service Id -- > " + serviceId + "\n" +
                                    "Retreival Reference No -- > " + retRefNo +
                                    "\n" +
                                    "Msg Type --> " + msgType + "\n" +
                                    "Amount --> " + amount + "\n" +
                                    "Description --> " + description + "\n" +
                                    "Entry Type --> " + entryType + "\n" +
                                    "Response Code --> " + respCode + "\n" +
                                    "device Id --> " + deviceId + "\n" +
                                    "Card Acceptor Code --> " +
                                    cardAcceptorCode + "\n" +
                                    "Card Acceptor Name And Loc --> " + "\n" +
                                    "Merchant Cat Code --> " + merchantCatCode +
                                    "\n" +
                                    "Account Number--> " + accountNo + "\n" +
                                    "Acquirer ID--> " + acquirerId);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "Getting the card Balance....");

    String[] results = new String[2];

    try {
      //get the card balance
      String cardBal = null;
      if (cardNo != null)
        cardBal = getValue(
            "select ledger_balance from card_funds where card_no='" + cardNo +
            "'");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Balance Got -- > " + cardBal);
      //make the query to log the transaction
      query.append("Execute procedure LOG_TRANSACTION(pcard_no = ?, pservice_id = ?, pret_ref = ?, pmsg_type = ?, premaining_bal = ?, pamt_processed = ?, pdescription = ?, paccount_id= ?, pinsert_mode= ?, pdevice_type= ?, presp_code= ?, pdevice_id= ?, pcard_aceptor_code= ?, pcard_aceptor_name= ?, pmerchant_cat_code= ?, p_acq_id= ? )");

      cstmt = con.prepareCall(query.toString());

      cstmt.setString(1, cardNo);
      cstmt.setString(2, serviceId);
      cstmt.setString(3, retRefNo);
      cstmt.setString(4, msgType);
      cstmt.setString(5, cardBal);
      cstmt.setString(6, amount);
      cstmt.setString(7, description);
      cstmt.setString(8, accountNo);
      cstmt.setString(9, entryType);
      cstmt.setString(10, deviceType);
      cstmt.setString(11, respCode);
      cstmt.setString(12, deviceId);
      cstmt.setString(13, cardAcceptorCode);
      cstmt.setString(14, cardAcceptorNameAndLoc);
      cstmt.setString(15, merchantCatCode);
      cstmt.setString(16, acquirerId);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Going to log the transaction--->" +
                                      query.toString());

      rs = cstmt.executeQuery();

      if (rs.next()) {
        results[0] = rs.getString(1);
        results[1] = rs.getString(2);
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
          "Got the Results from LOG_TRANSACTION :: " +
                                      "iso serial no -- > " + results[0] +
                                      " && trace audit no -- > " + results[1]);
      //return the iso serial no
      return results;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage());
      throw exp;
    }
    finally {
      try {
        if (rs != null) rs.close();
        if (cstmt != null) cstmt.close();
      } //end try
      catch (Exception ex) {} //end
    } //end finally
  } //end logTransaction


  public ServicesResponseObj logVASTransaction(ServicesRequestObj reqObj) throws Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Query for logging VAS Transaction --- getCardNo-- > " + reqObj.getCardNo()
                                + "<--getVasAccountType-->" + reqObj.getVasAccountType()
                                + "<--getServiceId-->" + reqObj.getServiceId()
                                + "<--getAcquirerId-->" + reqObj.getAcquirerId()
                                + "<--getCardAcceptorId-->" + reqObj.getCardAcceptorId()
                                + "<--getCardAcceptNameAndLoc-->" + reqObj.getCardAcceptNameAndLoc()
                                + "<--getMcc-->" + reqObj.getMcc()
                                + "<--getDeviceId-->" + reqObj.getDeviceId()
                                + "<--getDeviceType-->" + reqObj.getDeviceType()
                                + "<--getAcqData1-->" + reqObj.getAcqData1()
                                + "<--getAcqData2-->" + reqObj.getAcqData2()
                                + "<--getAcqData3-->" + reqObj.getAcqData3()
                                + "<--getAcqUsrId-->" + reqObj.getAcqUsrId()
                                + "<--getRetreivalRefNum-->" + reqObj.getRetreivalRefNum()
                                + "<--getDescription-->" + reqObj.getDescription()
                                + "<--getApplyFee-->" + reqObj.getApplyFee());

      query.append("Execute procedure vas_fee_log(pcard_no = ?, pvas_type_id = ?, pservice_id = ?, pacq_id = ?, pcard_acpt_id = ?, pcard_acpt_nameloc = ?, pmcc = ?, pdevice_id = ?, pdevice_type = ?, psub_srv = ?, pdata_2 = ?,pdata_3 = ?,pacq_userid = ?,pretrieval_ref = ?, p_vas_trans_desc = ?, papply_fee = ?)");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for logging VAS Transaction --- Query-- > " + query);

      cstmt = con.prepareCall(query.toString());

      cstmt.setString(1,reqObj.getCardNo());
      cstmt.setString(2,reqObj.getVasAccountType());
      cstmt.setString(3,reqObj.getServiceId());
      cstmt.setString(4,reqObj.getAcquirerId());
      cstmt.setString(5,reqObj.getCardAcceptorId());
      cstmt.setString(6,reqObj.getCardAcceptNameAndLoc());
      cstmt.setString(7,reqObj.getMcc());
      cstmt.setString(8,reqObj.getDeviceId());
      cstmt.setString(9,reqObj.getDeviceType());
      cstmt.setString(10,reqObj.getAcqData1());
      cstmt.setString(11,reqObj.getAcqData2());
      cstmt.setString(12,reqObj.getAcqData3());
      cstmt.setString(13,reqObj.getAcqUsrId());
      cstmt.setString(14,reqObj.getRetreivalRefNum());
      cstmt.setString(15,reqObj.getDescription());
      cstmt.setString(16,reqObj.getApplyFee());

      rs = cstmt.executeQuery();

      if(rs.next()){
        String respCode = rs.getString(1);
        if(respCode != null && respCode.trim().length() > 0){
          respObj.setRespCode(respCode);
        }
        String respDesc = rs.getString(2);
        if(respDesc != null && respDesc.trim().length() > 0){
          respObj.setRespDesc(respDesc);
        }
        String vasBal = rs.getString(3);
        if(vasBal != null && vasBal.trim().length() > 0){
          respObj.setCardBalance(vasBal);
        }
        String isoSerial = rs.getString(4);
        if(isoSerial != null && isoSerial.trim().length() > 0){
          respObj.setTransId(isoSerial);
        }
        String feeISOSerial = rs.getString(5);
        if(feeISOSerial != null && feeISOSerial.trim().length() > 0){
          respObj.setFeeSerialNo(feeISOSerial);
        }
        String fee = rs.getString(6);
        if(fee != null && fee.trim().length() > 0){
          respObj.setFeeAmount(fee);
        }
      }else{
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Query for logging VAS Transaction --- Query-- > " + query);
        throw new Exception("no response recevied from vas_fee_log API, throwing exception...");
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in logging VAS Transaction --- > " + ex);
      throw ex;

    }finally{
      try {
        if (rs != null) rs.close();
        if (cstmt != null) cstmt.close();
      } //end try
      catch (Exception ex) {} //end
    }
    return respObj;
  }

  /**
   * This method is used to apply the fee against supplied service at OLTP. It takes card no, service Id
   * and apply the service fee on the card against the transaction. This method provides the interface
   * to apply the service fee against the particular service.
   * @param cardNo String -- Card No
   * @param serviceId String -- Serivce Id
   * @param switchBalance String -- Switch Balance
   * @param retRefNo String -- Retrievel Reference No
   * @param traceAuditNo String -- Trace Audit No
   * @param deviceType String -- Device Type
   * @param deviceId String
   * @param cardAcceptorCode String
   * @param cardAcceptorNameAndLoc String
   * @param mcc String
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */

  public ServicesResponseObj applyServiceFeeAtOltp(String cardNo,
      String serviceId,
      String switchBalance,
      String retRefNo,
      String traceAuditNo,
      String deviceType,
      String deviceId,
      String cardAcceptorCode,
      String cardAcceptorNameAndLoc,
      String mcc,
      String acqId,
      String acqUsrId,
      String acqData1,
      String acqData2,
      String acqData3
      ) throws Exception {
    //call the appropriate method
    return applyServiceFeeAtOltp(cardNo, serviceId, null, null, switchBalance,
                                 retRefNo,
                                 traceAuditNo, deviceType, deviceId,
                                 cardAcceptorCode, cardAcceptorNameAndLoc, mcc,
                                 acqId, acqUsrId, acqData1, acqData2, acqData3);
  } //end applyServiceFeeAtOltp

  /**
   * This method is used to apply the fee against supplied service at OLTP. The method first calls the
   * database procedure "CAL_SERVICE_FEE" to calculate the service fee then it calls the "CONFIRM_DEBIT"
   * database procedure to apply the service fee against the given card. In case of any error it reutrns
   * the response object which descibes the processing progress in detail.
   * @param cardNo String -- Card No
   * @param serviceId String -- Serivce Id
   * @param feeAmount String -- Fee to apply
   * @param feeDesc String -- Fee Description
   * @param switchBalance String -- Switch Balance
   * @param retRefNo String -- Retrievel Reference No
   * @param traceAuditNo String -- Trace Audit No
   * @param deviceType String -- Device Type of transaction
   * @param deviceId String -- Device Id of transaction
   * @param cardAcceptorCode String -- Card Acceptor ID
   * @param cardAcceptorNameAndLoc String -- Card Acceptor Name and location
   * @param mcc String -- Merchant Category Code
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */

  public ServicesResponseObj applyServiceFeeAtOltp(String cardNo,
      String serviceId,
      String feeAmount,
      String feeDesc,
      String switchBalance,
      String retRefNo,
      String traceAuditNo,
      String deviceType,
      String deviceId,
      String cardAcceptorCode,
      String cardAcceptorNameAndLoc,
      String mcc,
      String acqId,
      String acqUsrId,
      String acqData1,
      String acqData2,
      String acqData3
      ) throws Exception {
    String feeAmt = null;
    //make the services response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    StringBuffer confQry = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

//      String feeSerial = null;
//      String feeTrace = null;
    String remBal = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Arguments :: Card No -- > " + cardNo
                                      + " <-- Service Id -- > " + serviceId
                                      + " <-- feeAmount -- > " + feeAmount
                                      + " <-- feeDesc -- > " + feeDesc
                                      + " <-- switchBalance-- > " +
                                      switchBalance
                                      + " <-- retRefNo-- > " + retRefNo
                                      + " <-- traceAuditNo-- > " + traceAuditNo
                                      + " <-- deviceType -- > " + deviceType
                                      + " <-- deviceId-- > " + deviceId
                                      + " <-- cardAcceptorCode-- > " +
                                      cardAcceptorCode
                                      + " <-- cardAcceptorNameAndLoc-- > " +
                                      cardAcceptorNameAndLoc
                                      + " <-- mcc -- > " + mcc
                                      + " <-- acqId -- > " + acqId
                                      + " <-- acqUsrId -- > " + acqUsrId
                                      + " <-- acqData1 -- > " + acqData1
                                      + " <-- acqData2 -- > " + acqData2
                                      + " <-- acqData3 -- > " + acqData3);
//      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Calculating Fee...");
//      //make the query to execute the cal service fee api
//      calServiceQry.append("execute procedure cal_service_fee(cardNo = ?,serviceId = ?)");
//
//      cstmt = con.prepareCall(calServiceQry.toString());
//      cstmt.setString(1,cardNo);
//      cstmt.setString(2,serviceId);
//
//      rs = cstmt.executeQuery();
//
//      if(rs.next()){
//        feeAmt = rs.getString(0);
//        respObj.setRespCode(rs.getString(1));
//        respObj.setRespDesc(rs.getString(2));
//      }
//
//      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Response Got from API :: Resp Code -- > " + respObj.getRespCode() + " && Resp Desc -- > " + respObj.getRespDesc());
//
//      if (respObj.getRespCode() != null && !respObj.getRespCode().trim().equals(Constants.SUCCESS_CODE)) {
//        return respObj;
//      } //end if
//
//      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "Fee Calculated -- > " + feeAmt);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Calling confirm debit api....");
      //call the confirm debit api to confirm the debit fee

      confQry.append("execute procedure confirm_debit(p_str_cardno = ?, p_str_tranid = ?,p_str_serviceid = ?,p_d_amount = ?,p_d_switch_balance = ?,p_org_trace_audit = ?,p_device_type = ?,p_device_id = ?,p_crd_aceptor_code = ?,p_crd_aceptor_name = ?,p_merchant_cat_cd = ?,p_fee_amount = ?,p_fee_desc = ?,pacq_id = ?,psub_srv = ?,pdata_2 = ?,pdata_3 = ?,pacq_userid = ?)");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "confirm debit query--->" + confQry);

      cstmt = con.prepareCall(confQry.toString());

      cstmt.setString(1, cardNo);
      cstmt.setString(2, retRefNo);
      cstmt.setString(3, serviceId);
      cstmt.setString(4, "0");
      cstmt.setString(5, switchBalance);
      cstmt.setString(6, traceAuditNo);
      cstmt.setString(7, deviceType);
      cstmt.setString(8, deviceId);
      cstmt.setString(9, cardAcceptorCode);
      cstmt.setString(10, cardAcceptorNameAndLoc);
      cstmt.setString(11, mcc);
      cstmt.setString(12, feeAmount);
      cstmt.setString(13, feeDesc);
      cstmt.setString(14, acqId);
      cstmt.setString(15, acqData1);
      cstmt.setString(16, acqData2);
      cstmt.setString(17, acqData3);
      cstmt.setString(18, acqUsrId);

      //execute the confirm debit api and get the results
      rs = cstmt.executeQuery();

      if (rs.next()) {
        respObj.setRespCode(rs.getString(1));
        respObj.setFeeSerialNo(rs.getString(2));
        respObj.setFeeTraceAuditNo(rs.getString(3));
        respObj.setCardBalance(rs.getString(4));
        respObj.setRespDesc(rs.getString(5));
        respObj.setFeeAmount(rs.getString(6));
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Response Recevied from API:: Response Code -- > " +
                                      respObj.getRespCode()
                                      + "\n" + "Response Desc -- > " +
                                      respObj.getRespDesc()
                                      + "\n" + "Fee ISO serial -- > " +
                                      respObj.getFeeSerialNo()
                                      + "\n" + "Fee Trace Audit No -- > " +
                                      respObj.getFeeTraceAuditNo()
                                      + "\n" + "Remaining Balance -- > " +
                                      respObj.getCardBalance()
                                      + "\n" + "Fee Amount -- > " +
                                      respObj.getFeeAmount());

//      if (respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
//        respObj.setCardBalance(results[3]);
//        respObj.setFeeAmount(feeAmt);
//      } //end if
//
//      if (feeAmount != null && !feeAmount.trim().equals("")) {
//        respObj.setFeeAmount(feeAmount);
//        respObj.setTransId(results[1]);
//      } //end if

      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage());
      throw exp;
    } //end catch
  } //end applyServiceFeeAtOltp

  /**
   * This method applies the fee against supplied service at SOLSPARK switch. Batch transactions
   * use this method to apply the service fee against the transaction and service id. The method first
   * calls the "CAL_SERVICE_FEE" to caluculte the service fee which is being applied then it uses the
   * SolsparkHandler class to debit the funds from the given card no account. In case of any error it
   * returns response to the client which describes the processing status in detail.
   * @param cardNo String -- Card No
   * @param amount String -- Amount
   * @param serviceId String -- Service Id
   * @param debitFee double -- Fee to Debit
   * @param oldBalance String -- Old Balance
   * @throws Exception
   * @return ServicesResponseObj -- Response information
   */

  public ServicesResponseObj applyServiceFeeAtSolspark(String cardNo,
      String amount, String serviceId, double debitFee, String oldBalance) throws
      Exception {
    try {
      //make the services response code
      ServicesResponseObj respObj = new ServicesResponseObj();

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Args :: card No -- > " + cardNo + "\n" +
                                      "Service Id -- > " + serviceId + "\n" +
                                      "Debit Fee --> " + debitFee);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Calculating Fee...");
      //make the query to execute the cal service fee api
      String calServiceQry = "EXECUTE PROCEDURE CAL_SERVICE_FEE('" + cardNo +
          "'," + amount + ",'" + serviceId + "')";
      //execute the api and get the results
      String[] results = getProcedureVal(calServiceQry);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
          "Response Got from API :: Resp Code -- > " + results[1] +
                                      " && Resp Desc -- > " + results[2]);

      if (results[1] != null &&
          !results[1].trim().equals(Constants.SUCCESS_CODE)) {
        respObj.setRespCode(results[1]);
        respObj.setRespDesc(results[2]);
        return respObj;
      } //end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Fee Calculated -- > " + results[0]);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Parsing Fee to double...");
      //parse the fee and make it double
      double serviceFee = Double.parseDouble(results[0]);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Parsed Fee -- > " + serviceFee);

      //calculate the fee amount to apply on solspark
      double feeAmount = (serviceFee - debitFee) * -1;
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Applying Fee at SolsPark -- > " +
                                      feeAmount);
      //make the solspark handler
      SolsparkHandler handler = new SolsparkHandler(con);
      if (feeAmount != 0) {
        //get the card switch information
        SwitchInfoObj switchInfo = getCardSwitchInfo(cardNo);
        //add the -ive funds at solspark
        SolsparkResponseObj solsRespObj = handler.addCardFunds(switchInfo.
            getSwitchId(), feeAmount + "", cardNo, null);

        respObj.setRespCode(getSwitchResponseCode("SOLSPARK",
                                                  solsRespObj.getRespCode()));
        respObj.setRespDesc(getSwitchResponseDesc(switchInfo.getSwitchId(),
                                                  solsRespObj.getRespCode()));
        if (respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          respObj.setCardBalance(solsRespObj.getBalance());
          //set the fee amount in the response object
          respObj.setFeeAmount( (serviceFee - debitFee) + "");
        } //end if
        return respObj;
      } //end if
      else {
        respObj.setRespCode("00");
        respObj.setRespDesc("Zero Amount");
        //set the fee amount in the response object
        respObj.setFeeAmount("0.0");
        respObj.setCardBalance(oldBalance);
        return respObj;
      } //end else
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage());
      throw exp;
    } //end catch
  } //end applyServiceFeeAtSolspark

  /**
   * This method returns the response code description  against provided switch id and the response code.
   * It will throw exception if there was an error while executing the query.
   * @param switchId String -- Switch ID
   * @param respCode String -- Response Code to get the description
   * @throws Exception
   * @return String -- Respons Code Description
   */

  public String getSwitchResponseDesc(String switchId, String respCode) throws
      Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_FINEST), "Switch Id -- > " + switchId);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_FINEST), "Response Code -- > " + respCode);

    try {
      //build the query to get
      String query =
          "select switch_resp_desc from switch_resp_codes where switch_id='" +
          switchId + "' and switch_resp_code='" + respCode + "'";
      //get the description
      return getValue(query);
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + exp.getMessage());
      //throw the exception
      throw exp;
    } //end catch
  } //end getSwitchResponseDesc

  /**
   * This method returns the translated ISO response code against the response code of switch. It queries
   * the database table "switch_resp_codes" to find the ISO response code againt the given switch and the
   * switch response code. In case of any error while finding the iso response code it throws exception
   * which describes the cause of the error in detail.
   * @param switchId String -- Switch ID
   * @param respCode String -- Response Code to get the description
   * @throws Exception
   * @return String -- ISO Respons Code
   */

  public String getSwitchResponseCode(String switchId, String respCode) throws
      Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_FINEST), "Switch Id -- > " + switchId);

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_FINEST), "Response Code -- > " + respCode);

    try {
      //build the query to get
      String query =
          "select resp_code from switch_resp_codes where switch_id='" +
          switchId + "' and switch_resp_code='" + respCode + "'";
      //get the description
      return getValue(query);
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + exp.getMessage());
      //throw the exception
      throw exp;
    } //end catch
  } //end getSwitchResponseDesc

  /**
   * This method is used to get the switch information against the supplied card no. It uses the card id to
   * find the switch information. It returns SwitchInfoObj bean which holds the information about the
   * switch such as switch-id, switch-active, bat-trans-allowed, local-conn, local-conn-port.
   * @param cardNo String
   * @throws Exception
   * @return SwitchInfoObj
   */

  public SwitchInfoObj getCardSwitchInfo(String cardNo) throws Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_FINEST), "Card No Received -- > " + cardNo);
    //prepare the query
    String query = "select s.switch_id,s.switch_active,s.bat_trans_allowed,s.local_conn_ip,s.local_conn_port from  cards c,iso_switches s , card_programs p where" +

        " c.card_prg_id = p.card_prg_id and p.switch_id = s.switch_id and c.card_no=?";

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, cardNo);
      rs = pstmt.executeQuery();

      SwitchInfoObj switchInfo = new SwitchInfoObj();
      if (rs.next()) {
        String switchId = rs.getString("switch_id");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST), "Switch ID -- > " + switchId);

        switchInfo.setSwitchId(switchId);

        String switchActive = rs.getString("switch_active");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST), "Switch Active -- > " + switchActive);

        switchInfo.setSwitchActive( (switchActive != null &&
                                     switchActive.trim().equalsIgnoreCase("Y") ? true : false));

        String batchTransAllowed = rs.getString("bat_trans_allowed");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Batch Trans Allowed -- > " +
                                        batchTransAllowed);

        switchInfo.setBatchTransAllowed( (batchTransAllowed != null &&
                                          batchTransAllowed.trim().
                                          equalsIgnoreCase("Y") ? true : false));

        String localConnIP = rs.getString("local_conn_ip");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Local Connection IP -- > " +
                                        localConnIP);
        if (localConnIP != null)
          switchInfo.setLocalconnIP(localConnIP);

        int localConnPort = rs.getInt("local_conn_port");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Local Connection Port -- > " +
                                        localConnPort);
        switchInfo.setLocalConnPort(localConnPort);
      } //end if
      return switchInfo;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
      } //end try
      catch (Exception ex) {} //end
    } //end finally
  } //end getCardSwitchInfo

  public SwitchInfoObj getCardProgramSwitchInfo(String cardStartNos) throws Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_FINEST), "Card Start Nos Received -- > " + cardStartNos);
    //prepare the query
    String query = "select s.switch_id,s.switch_active,s.bat_trans_allowed,s.local_conn_ip,s.local_conn_port from  iso_switches s ,card_programs p where p.switch_id = s.switch_id and p.card_start_nos=?";

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      pstmt = con.prepareStatement(query);
      pstmt.setString(1, cardStartNos);
      rs = pstmt.executeQuery();

      SwitchInfoObj switchInfo = new SwitchInfoObj();
      if (rs.next()) {
        String switchId = rs.getString("switch_id");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST), "Switch ID -- > " + switchId);

        switchInfo.setSwitchId(switchId);

        String switchActive = rs.getString("switch_active");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST), "Switch Active -- > " + switchActive);

        switchInfo.setSwitchActive( (switchActive != null &&
                                     switchActive.trim().equalsIgnoreCase("Y") ? true : false));

        String batchTransAllowed = rs.getString("bat_trans_allowed");

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Batch Trans Allowed -- > " +
                                        batchTransAllowed);

        switchInfo.setBatchTransAllowed( (batchTransAllowed != null &&
                                          batchTransAllowed.trim().
                                          equalsIgnoreCase("Y") ? true : false));

        String localConnIP = rs.getString("local_conn_ip");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Local Connection IP -- > " +
                                        localConnIP);
        if (localConnIP != null)
          switchInfo.setLocalconnIP(localConnIP);

        int localConnPort = rs.getInt("local_conn_port");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Local Connection Port -- > " +
                                        localConnPort);
        switchInfo.setLocalConnPort(localConnPort);
      } //end if
      return switchInfo;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
      } //end try
      catch (Exception ex) {} //end
    } //end finally
  } //end getCardSwitchInfo


  /**
   * This method is used to get the service fee of the specified service against supplied card no. It calls
   * the database procedure "CAL_SERVICE_FEE" to calculate the service fee aginst the given service. At the
   * end of the processing it returns response object which describes the response in detail.
   * @param cardNo String -- Card No
   * @param amount String -- Amount to calculate the service fee in case of %age only
   * @param serviceId String -- Service ID for which to get the fee amount
   * @param deviceType String -- Device Type of transaction
   * @param deviceId String -- Device Id of transaction
   * @param cardAcceptorId String -- Card Acceptor ID
   * @param cardAcceptorNameAndLoc String -- Card Acceptor Name and location
   * @param mcc String -- Merchant Category Code
   * @param accountNo String -- Account No
   * @throws Exception
   * @return String -- Required service fee
   */

  public ServicesResponseObj getServiceFee(String cardNo,
                                           String amount,
                                           String serviceId,
                                           String deviceType,
                                           String deviceId,
                                           String cardAcceptorId,
                                           String cardAcceptorNameAndLoc,
                                           String mcc,
                                           String accountNo,
                                           String acquirerId) throws Exception {
    ServicesResponseObj respObj = new ServicesResponseObj();

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "Arguments Received :: Card No -- > " +
                                    cardNo + " && Amount -- > " + amount +
                                    " && Service ID -- > " + serviceId);
    //make the query to execute procedure
    String query = "Execute procedure cal_service_fee('" + cardNo + "'," +
        amount + ",'" + serviceId + "')";
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
          "Executing procedure to get the service fee.....");
      //execute the procedure
      String results[] = getProcedureVal(query);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Procedure executed....");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Results:: Fee -- > " + results[0] +
                                      " && Code -- > " + results[1] +
                                      " && Desc -- > " + results[2]);

      respObj.setRespCode(results[1]);
      respObj.setRespDesc(results[2]);
      if (respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        respObj.setFeeAmount(results[0]);

      if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
            "Going to log the error transaction::Code -- > " +
                           respObj.getRespCode() + " && Desc -- > " +
                           respObj.getRespDesc());
        //log the transaction
        String[] transIds = logTransaction(cardNo, serviceId, deviceType, null,
                                           "0200", "0", respObj.getRespDesc(),
                                           "0", respObj.getRespCode(), deviceId,
                                           cardAcceptorId,
                                           cardAcceptorNameAndLoc, mcc,
                                           accountNo, acquirerId);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
            "Transaction logged with iso serial no -- > " + transIds[0] +
                           " && trace audit no -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
      } //end if
      //return the response object
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage());
      throw exp;
    } //end catch
  } //end getServiceFee

  /**
   * This method gets the service fee of the specified service against supplied card no, amount and the
   * service id. This method returns the ServiceResponseObj as the return value which describe the response
   * which have received after executing the database stored procedure "cal-service-fee". The method uses
   * the database procedure "CAL_SERVICE_FEE" to calculate the service fee against the given service and
   * amount.
   * @param cardNo String -- Card No
   * @param amount String -- Amount to calculate the service fee in case of %age only
   * @param serviceId String -- Service ID for which to get the fee amount
   * @throws Exception
   * @return String -- Required service fee
   */

  public ServicesResponseObj getServiceFee(String cardNo,
                                           String amount,
                                           String serviceId) throws Exception {
    ServicesResponseObj respObj = new ServicesResponseObj();

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "Arguments Received :: Card No -- > " +
                                    cardNo + " && Amount -- > " + amount +
                                    " && Service ID -- > " + serviceId);
    //make the query to execute procedure
    String query = "Execute procedure cal_service_fee('" + cardNo + "'," +
        amount + ",'" + serviceId + "')";
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
          "Executing procedure to get the service fee.....");
      //execute the procedure
      String results[] = getProcedureVal(query);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Procedure executed....");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Results:: Fee -- > " + results[0] +
                                      " && Code -- > " + results[1] +
                                      " && Desc -- > " + results[2]);

      respObj.setRespCode(results[1]);
      respObj.setRespDesc(results[2]);
      if (respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        respObj.setFeeAmount(results[0]);

        //return the response object
      return respObj;
    } //end try
    catch (Exception exp) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + exp.getMessage());
      throw exp;
    } //end catch
  } //end getServiceFee

  /**
   * This method calculates the service id to apply. It returns the service id against the specificed card
   * program id and the device type. The method calls the database procedure "GET_SERVICE_FEE" to find the
   * service id.
   * @param deviceType String -- Device Type i.e. I = IVR , H = WEB SERVICES etc
   * @param transType String -- Transaction Type
   * @throws Exception
   * @return String -- Required service id else null
   */

  public String huntForServiceId(String deviceType, String transType,
                                 String responseCode, String cardPrgId) throws
      Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Device Type -- > " + deviceType +
                                    " && transType -- > " + transType +
                                    "<---Response Code--->" + responseCode +
                                    "<---Card Prg ID--->" + cardPrgId);
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;
    String serviceId = null;

    query.append("Execute procedure get_service_id(f_device_type = ");
    if (deviceType != null && deviceType.trim().length() > 0) {
      query.append("'" + deviceType + "',");
    }
    else {
      query.append(null +",");
    }
    query.append(" process_code = ");
    if (transType != null && transType.trim().length() > 0) {
      query.append("'" + transType + "',");
    }
    else {
      query.append(null +",");
    }
    query.append("response_flag = ");
    if (responseCode != null && responseCode.trim().length() > 0) {
      query.append("'" + responseCode + "',");
    }
    else {
      query.append(null +",");
    }
    query.append("pcard_prg_id = ");
    if (cardPrgId != null && cardPrgId.trim().length() > 0) {
      query.append("'" + cardPrgId + "')");
    }
    else {
      query.append(null +")");
    }

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Query for Getting serviced Id--->" + query);
    //get the service id
    cstmt = con.prepareCall(query.toString());
    rs = cstmt.executeQuery();
    if (rs.next()) {
      if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
        serviceId = rs.getString(1).trim();
      }
    }
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Service ID Found -- > " + serviceId);

    return serviceId;
  } //end huntForServiceId

  public String huntForServiceId(String deviceType, String transType,
                                 String responseCode, String cardPrgId,String isInternational) throws
      Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Device Type -- > " + deviceType +
                                    " && transType -- > " + transType +
                                    "<---Response Code--->" + responseCode +
                                    "<---Card Prg ID--->" + cardPrgId +
                                    "<---Is International--->" + isInternational);
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;
    String serviceId = null;

    query.append("Execute procedure get_service_id(f_device_type = ?, process_code = ?, response_flag = ?, pcard_prg_id = ?, pintl = ?)");

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Query for Getting serviced Id--->" + query);
    //get the service id
    cstmt = con.prepareCall(query.toString());
    cstmt.setString(1,deviceType);
    cstmt.setString(2,transType);
    cstmt.setString(3,responseCode);
    cstmt.setString(4,cardPrgId);
    cstmt.setString(5,isInternational);

    rs = cstmt.executeQuery();
    if (rs.next()) {
      if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
        serviceId = rs.getString(1).trim();
      }
    }
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Service ID Found -- > " + serviceId);

    return serviceId;
  } //end huntForServiceId


//  public String huntForServiceId(String deviceType, String transType) throws Exception
//  {
//    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Device Type -- > " + deviceType  + " && transType -- > " + transType);
//    String qry = "select service_id from services where device_type='" + deviceType + "' and trans_type='" + transType + "' and resp_code=00";
//
//    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Getting serviced Id...");
//    //get the service id
//    String serviceId = getValue(qry);
//
//    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Service ID Found -- > " + serviceId);
//
//    return serviceId;
//  }//end huntForServiceId


  /**
   * This method is used to mask the given card no  with the * before the last 4 digits of card
   * @param cardNo String -- Card No
   * @throws Exception
   * @return String -- Masked Card No
   */

  public String maskCardNo(String cardNo) throws Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "Card No -- > " + cardNo);
    CallableStatement cstmt = null;
    ResultSet rs = null;
    try {
      String qry = "Execute procedure get_cardno(pcard_no='" + cardNo + "')";
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Query -- > " + qry);

      cstmt = con.prepareCall(qry);
      rs = cstmt.executeQuery();
      if (rs.next()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST), "Masked Card No Got -- > " + rs.getString(1));
        return rs.getString(1);
      } //end if
      return null;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
  } //end maskCardNo

  /**
   * This method searches the database table "CARDS" and returns the card-program-id against the given
   * card no. In case if no card no. exists or any error occurs while searching the table, it reutrns
   * null.
   * @param cardNumber String
   * @return String
   */

  public String getCardProgramID(String cardNumber) {
    String cardPrgID = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting Card Program ID for provided Card Number--->" +
                                      cardNumber);
      query.append(
          "select card_prg_id from cards where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting Card Program ID for provided Card Number--->" +
                                      query);
      stmt = con.createStatement();
      rs = stmt.executeQuery(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ResultSet for getting Card Program ID for provided Card Number--->" +
                                      rs);
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          cardPrgID = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Card Program ID Got--->" +
                                          cardPrgID);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting Card Program ID for provided Card Number--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return cardPrgID;
  }

  public String getCardEmployerID(String cardNumber) {
    String empID = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting Card Employer ID for provided Card Number--->" +
                                      cardNumber);
      query.append(
          "select employer_id from cards where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting Card Employer ID for provided Card Number--->" +
                                      query);
      stmt = con.createStatement();
      rs = stmt.executeQuery(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ResultSet for getting Card Employer ID for provided Card Number--->" +
                                      rs);
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          empID = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Card Employer ID Got--->" +
                                          empID);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting Card Employer ID for provided Card Number--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return empID;
  }


  /**
   * This method checks whether isParamterAllowed is allowed or not. If the parameter is allowed it returns
   * true otherwise it returns false. This method basically calls "IS_PARAM_ALLOWED" database stored
   * procedure which determines whether the parameter is allowed against the param code,
   * card program id and the parameter value.
   * @param paramCode String
   * @param cardPrgID String
   * @param paramValue String
   * @return boolean
   */

  public boolean isParameterAllowed(String paramCode, String cardPrgID,
                                    String paramValue) {
    boolean isOk = true;
    CallableStatement cs = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
          "Method for Checking Service is Allowed for Card program ID--->" +
                                      cardPrgID + "<---Parameter Code--->" +
                                      paramCode + "<---Parameter Value--->" +
                                      paramValue);
      cs = con.prepareCall("{call is_param_allowed(?,?,?)}");
      cs.setString(1, cardPrgID);
      cs.setString(2, paramCode);
      cs.setString(3, paramValue);
      rs = cs.executeQuery();
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          String paramAllowed = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Paramter is Allowed for Card program ID--->" +
                                          cardPrgID + "--->" + paramAllowed);
          if (paramAllowed.equalsIgnoreCase(Constants.NO_OPTION)) {
            return false;
          }
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          "Exception in Checking Parameter is Allowed for Card program ID--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (cs != null) {
          cs.close();
          cs = null;
        }
      }
      catch (Exception ex1) {}
    }
    return isOk;
  }

  public void changeCardStatus(String cardNumber, String cardStatus,boolean trackGenerated) throws
      Exception {
    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for changing card status for card number--->" +
                                      cardNumber + "<--Card Status-->" + cardStatus + "<---trackGenerated--->" + trackGenerated);
      query.append("update cards set card_status_atm = ?, card_status_pos = ?, last_sts_chg_on = today");
      if (cardStatus != null && cardStatus.equals(Constants.CLOSED_CARD) && !trackGenerated) {
        query.append(", card_status = 'D', track_batch_no = 0");
      }
      query.append(" where card_no = ?");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for changing card status--->" +
                                      query);
      pstmt = con.prepareStatement(query.toString());
      pstmt.setString(1, cardStatus);
      pstmt.setString(2, cardStatus);
      pstmt.setString(3, cardNumber);
      pstmt.executeUpdate();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query executed successfully...");
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in changing status of card--->" +
                                      ex);
      throw ex;
    }
    finally {
      if (pstmt != null) {
        pstmt.close();
        pstmt = null;
      }
    }
  }

  public boolean checkCardTrackGenerated(String cardNo) throws Exception{
    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String cardTrackNo = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for checking whether track of the provided card already generated --->" +
                                      cardNo);
      query.append("select track_batch_no from cards where card_no = ? ");

      pstmt = con.prepareStatement(query.toString());
      pstmt.setString(1, cardNo);

      rs = pstmt.executeQuery();
      if (rs.next()) {
        cardTrackNo = rs.getString(1);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Card Track No --->" + cardTrackNo);
        if (cardTrackNo != null && cardTrackNo.trim().length() > 0) {
          if(Integer.parseInt(cardTrackNo) > 0){
            return true;//card track already generated
          }else{
            return false;//card already reversed
          }
        }else{
          return false;//card track not yet generated
        }
      }else {
        return false;//card no invalid
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in checking card track already generated -->" +
                                      ex);
      throw ex;
    }finally {
      if (rs != null) {
        rs.close();
        rs = null;
      }
      if (pstmt != null) {
        pstmt.close();
        pstmt = null;
      }
    }
  }

  public String getCardHolderId(String cardNo) {
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for getting cardholder Id for provided card--->" + cardNo);
          query.append(
                  "select ch_id from cards where card_no = ?");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Query for getting cardholder Id for provided card--->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1,cardNo);
          rs = pstmt.executeQuery();

          if (rs.next()) {
              String chId = rs.getString(1);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Result got--->" +
                                              chId);
              if (chId != null && chId.trim().length() > 0) {
                  return chId.trim();
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in getting cardholder Id--->" +
                                          ex);
      } finally {
          try {
              if (rs != null) {
                  rs.close();
                  rs = null;
              }
              if (pstmt != null) {
                  pstmt.close();
                  pstmt = null;
              }
          } catch (SQLException ex1) {
          }
      }
      return null;
  }

  public void insertAlertChannelRecord(ServicesRequestObj requestObj)throws Exception{
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for inserting alert channel record --- ChannelID--->" + requestObj.getAlertChannelId()
                  + "<---ProviderID-->" + requestObj.getAlertProviderId()
                  + "<---AlertChannelAddress-->" + requestObj.getAlertChannelAddress()
                  + "<---AlertUserNo-->" + requestObj.getBillPaymentAlertUserNo());
          query.append("insert into alert_user_channels (channel_id,user_alert_no,channel_addr,provider_id) values(?,?,?,?)");
          pstmt = con.prepareStatement(query.toString());

          pstmt.setString(1, requestObj.getAlertChannelId());
          pstmt.setString(2, requestObj.getBillPaymentAlertUserNo());
          pstmt.setString(3, requestObj.getAlertChannelAddress());
          pstmt.setString(4, requestObj.getAlertProviderId());
          pstmt.executeUpdate();

      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Exception in inserting alert channel record--->" + ex);
          throw new Exception("Unable to insert alert channel record in database-->" + ex);
      }finally{
          if (pstmt != null) {
              pstmt.close();
              pstmt = null;
          }
      }
  }

  public long insertAlertUserRecord(ServicesRequestObj requestObj) throws Exception {
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      long serial = -1;
      try {
          String dateTime = CommonUtilities.getCurrentFormatDate(Constants.DATE_TIME_FORMAT);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for inserting alert channel record --- Operator--->" + Constants.ALERT_OPERATOR_GRTR
                                          + "<---amount--->" + requestObj.getBillPaymentAmount()
                                          + "<---comments--->" + requestObj.getDescription()
                                          + "<---Alert Date Time--->" + dateTime
                                          + "<---Is Active--->" + Constants.YES_OPTION
                                          + "<---Is Balance Condition Met--->" + Constants.NO_OPTION
                                          + "<---Alert Type--->" + Constants.ALERT_TYPE_BILL_PAY
                                          + "<---Card No--->" + requestObj.getCardNo()
                                          + "<---User Id--->" + requestObj.getCardUserId());

          query.append("insert into alert_user_params (operator,amount,comments,alert_dtime,is_active,is_bal_cond_met,alert_type,card_no,user_id) values(?,?,?,?,?,?,?,?,?)");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for inserting alert channel record --- Query--->" + query);

          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1, Constants.ALERT_OPERATOR_GRTR);
          pstmt.setString(2, requestObj.getBillPaymentAmount());
          pstmt.setString(3, requestObj.getDescription());
          pstmt.setString(4, dateTime);
          pstmt.setString(5, Constants.YES_OPTION);
          pstmt.setString(6, Constants.NO_OPTION);
          pstmt.setString(7, Constants.ALERT_TYPE_BILL_PAY);
          pstmt.setString(8, requestObj.getCardNo());
          pstmt.setString(9, requestObj.getCardUserId());
          pstmt.executeUpdate();
          serial = getSerial(query);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for inserting alert channel record -- Serial GOT--->" +
                                          serial);
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_WARNING),
                                          "Exception in inserting alert channel record--->" + ex);
          throw new Exception("Unable to insert alert user record in database-->" + ex);
      }finally{
          if (pstmt != null) {
              pstmt.close();
              pstmt = null;
          }
      }
      return serial;
  }

  public long getSerial(StringBuffer query) throws Exception {
      StringBuffer serialQuery = new StringBuffer();
      PreparedStatement stmt = null;
      ResultSet rs = null;
      long serial = -1;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for getting serial against insert query--->" +
                                          query);
          if (query.toString().toLowerCase().indexOf(Constants.
                  INSERT_QUERY_INTO_VALUE.toLowerCase()) > -1) {
              serialQuery.append(Constants.SERIAL_QUERY + " " +
                                 query.
                                 toString().substring(query.toString().toLowerCase().
                      indexOf(Constants.
                              INSERT_QUERY_INTO_VALUE.toLowerCase()) +
                      Constants.INSERT_QUERY_INTO_VALUE.
                      length(),
                      query.toString().toLowerCase().indexOf(
                              Constants.
                              INSERT_QUERY_COLUMN_START_VALUE.toLowerCase())));
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Query for getting serial no--> " +
                                              serialQuery);
              stmt = con.prepareStatement(serialQuery.toString());
              rs = stmt.executeQuery();
              if (rs.next()) {
                  if (rs.getString(1) != null &&
                      rs.getString(1).trim().length() > 0) {
                      serial = Integer.parseInt(rs.getString(1).trim());
                      CommonUtilities.getLogger().log(LogLevel.getLevel(
                              Constants.LOG_CONFIG),
                              " Serial found --> " + serial);
                  }
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_WARNING),
                                          "Exception in getting serial number--->" +
                                          ex);
          throw ex;
      } finally {
          try {
              if (rs != null) {
                  rs.close();
                  rs = null;
              }
              if (stmt != null) {
                  stmt.close();
                  stmt = null;
              }
          } catch (SQLException ex1) {
          }
      }
      return serial;
  }

  public String getCardUserID(String cardNumber) throws
          Exception {
      String userId = null;
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      StringBuffer query = new StringBuffer();

      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for getting user ID for card-->" +
                                          cardNumber);
          query.append("select user_id from cards where card_no = ?");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Query for getting user ID for card-->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1,cardNumber);

          rs = pstmt.executeQuery();

          if(rs.next()){
              if(rs.getString(1) != null && rs.getString(1).trim().length() > 0){
                  userId = rs.getString(1).trim();
              }
          }
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "User ID got--->" +
                                          userId);
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_WARNING),
                                          "Exception in getting user ID for card--->" + ex);
          throw new Exception("Unable to get user id for provided card--->" + ex);
      }finally{
          if(rs != null){
              rs.close();
              rs = null;
          }
          if(pstmt != null){
              pstmt.close();
              pstmt = null;
          }
      }
      return userId;
  }

  public String getParameterValue(String cardPrgid, String paramCode) throws
          Exception {
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      StringBuffer query = new StringBuffer();
      String paramVal = null;

      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for getting Parameter Value for card program-->" +
                                          cardPrgid + "<---Parameter Code--->" + paramCode);
          query.append("select param_val from card_prog_params where card_prg_id = ? and param_code = ?");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Query for getting Parameter Value for card program-->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1,cardPrgid);
          pstmt.setString(2,paramCode);

          rs = pstmt.executeQuery();

          if(rs.next()){
              if(rs.getString(1) != null && rs.getString(1).trim().length() > 0){
                  paramVal = rs.getString(1).trim();
              }
          }
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Param Value got--->" +
                                          paramVal);
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_WARNING),
                                          "Exception in getting Parameter Value for card program--->" + ex);
          throw new Exception("Unable to get Parameter Value for card program--->" + ex);
      }finally{
          if(rs != null){
              rs.close();
              rs = null;
          }
          if(pstmt != null){
              pstmt.close();
              pstmt = null;
          }
      }
      return paramVal;
  }


  public String getUserProfileInfo(String cardNumber,String info) throws
          Exception {
      String infoVal = null;
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      StringBuffer query = new StringBuffer();

      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for getting Profile info for card-->" +
                                          cardNumber + "<---Info required--->" + info);
          query.append("select ");
          query.append(info);
          query.append(" from cards where card_no = ?");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Query for getting Profile info for card-->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1,cardNumber);

          rs = pstmt.executeQuery();

          if(rs.next()){
              if(rs.getString(1) != null && rs.getString(1).trim().length() > 0){
                  infoVal = rs.getString(1).trim();
              }
          }
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Info got--->" +
                                          infoVal);
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_WARNING),
                                          "Exception in getting Profile info for card--->" + ex);
          throw new Exception("Unable to get profile info for provided card--->" + ex);
      }finally{
          if(rs != null){
              rs.close();
              rs = null;
          }
          if(pstmt != null){
              pstmt.close();
              pstmt = null;
          }
      }
      return infoVal;
  }

  public void updateBPRequestAlertInfo(ServicesRequestObj requestObj) throws Exception {
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for updating alert info for bill payment request --- "
                                          + "<---BillPaymentAlertType--->" + requestObj.getBillPaymentAlertType()
                                          + "<---BillPaymentAlertUserNo--->" + requestObj.getBillPaymentAlertUserNo()
                                          + "<---BillPaymentTransactionSerial--->" + requestObj.getBillPaymentTransactionSerial()
                  );

          query.append("update bp_requests set alert_flag = ?,user_alert_no = ? where trans_id = ?");
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1, requestObj.getBillPaymentAlertType());
          pstmt.setString(2, requestObj.getBillPaymentAlertUserNo());
          pstmt.setString(3, requestObj.getBillPaymentTransactionSerial());
          pstmt.executeUpdate();
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Exception in updating alert info for bill payment request--->" + ex);
          throw new Exception("Unable to update alert info for bill payment request-->" + ex);
      }finally{
          if (pstmt != null) {
              pstmt.close();
              pstmt = null;
          }
      }
  }
} //end ServicesBaseHome
