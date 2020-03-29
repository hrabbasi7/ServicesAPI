package com.i2c.services.home;

import com.i2c.services.util.*;
import com.i2c.services.base.*;
import com.i2c.services.*;
import java.sql.*;
import java.util.*;

/**
 * <p>Title: FinancialServiceHome: A class which provides the financial services</p>
 * <p>Description: This class provides the DB methods for Financial Services. For example if we
 * want to get the list of the transaction against a given card. </p>
 * <p>Copyright: Copyright (c) 2006 Innvoative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class FinancialServiceHome
    extends ServicesBaseHome {

  /**
   * Constructor
   */
  private FinancialServiceHome() {
  } //end CardsServiceHome

  /**
   * Factory method which creates the instance of the FinancialServiceHome class and returns it to the
   * calling client.
   * @param _con Connection
   * @return CardsServiceHome
   */

   public static FinancialServiceHome getInstance(Connection _con) {
    FinancialServiceHome home = new FinancialServiceHome();

    home.con = _con;
    return home;
  } //end getInstance

  /**
   * This method inserts the transfer information in the "transfer_reqs" database table.
   * The method first gets the user id of both card no (i.e. card-no to and card-no from)
   * and inserts the transfer information into the database.
   * @param requestObj ServicesRequestObj -- Information populated object
   * @throws Exception
   * @return long -- Inserted transfer Id
   */
  public long insertTransfer(ServicesRequestObj requestObj) throws Exception {
    StringBuffer query = null;
    String fromOwner = null, toOwner = null;
    boolean isSameOwner = false;
    long retValue = -1;
    try {
      fromOwner = getValue("select user_id from cards where card_no='" +
                           requestObj.getCardNo() + "'");
      toOwner = getValue("select user_id from cards where card_no='" +
                         requestObj.getToCardNo() + "'");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "From Owner -- > " + fromOwner);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "To Owner -- > " + toOwner);

      isSameOwner = (fromOwner != null && toOwner != null &&
                     fromOwner.equals(toOwner));

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "Is Same Owner -- > " + isSameOwner);

      query = new StringBuffer();

      query.append("insert into transfer_reqs (");
      query.append("user_id, device_type, card_no_from, card_no_to, ");
      query.append("is_same_owner, transfer_time, ");
      query.append("amount, status_id, is_recurring, ");
      query.append("continue_on_fail, nretries_done, max_retries) values ( ");
      query.append("'" + requestObj.getCardNo() + "','W','" +
                   requestObj.getCardNo() + "','" + requestObj.getToCardNo() +
                   "',");
      query.append( ( (isSameOwner) ? "'Y'" : "'N'") + "," +
                   (requestObj.getTransDate() != null &&
                    !requestObj.getTransDate().trim().equals("") ?
                    "'" + requestObj.getTransDate() + "'" : "Today") + ", ");
      query.append(requestObj.getAmount() + ", 'S', 'N','" +
                   (requestObj.getRetryOnFail() != null &&
                    !requestObj.getRetryOnFail().trim().equals("") ?
                    requestObj.getRetryOnFail().trim() : "N"));
      query.append("', '0', '" +
                   (requestObj.getMaxTries() != null &&
                    !requestObj.getMaxTries().trim().equals("") ?
                    requestObj.getMaxTries().trim() : "2") + "')");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "Inserting the transfer in the DB.....");

      retValue = insertValues(query.toString());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "Got the Transfer ID -- > " + retValue);

      return retValue;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
  } //end insertTransfer

  /**
   * This method inserts the ACH Transfer in the "transfer_achs" database table. It first checks whether
   * transfer date is valid or not and inserts transfer infromation into the database. At the end of
   * the processing "serial no" is returned which indicates the transfer request no. In case of any error
   * an exception is thrown which describes the cause of the error.
   * @param requestObj ServicesRequestObj -- All the transfer information is contained in this object
   * @throws Exception
   * @return long
   */
  public long insertAchTransfer(ServicesRequestObj requestObj) throws Exception {
    StringBuffer query = null;
    long retValue = -1;
    try {
      String transferDate = DateUtil.getCurrentDate();

      if (requestObj.getTransferDate() != null &&
          !requestObj.getTransferDate().trim().equals("")) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Converting supplied transfer date format to native one.... Date supplied -- > " +
                                        requestObj.getTransferDate());
        //convert the transfer date format
        transferDate = CommonUtilities.convertDateFormat(Constants.DATE_FORMAT,
            Constants.WEB_DATE_FORMAT, requestObj.getTransferDate());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Date after conversion -- > " +
                                        transferDate);
      } //end if
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Inserting ACH Transfer for Card No -- > " +
                                      requestObj.getCardNo());

      query = new StringBuffer();
      //make the query to insert the transfer
      query.append(
          "insert into transfer_achs (user_id, device_type, ach_type, ");
      query.append("card_no, account_title, account_no, account_type, ");
      query.append("bank_name, amount, routing_no, status_id, is_recurring, ");
      query.append(
          "continue_on_fail, nretries_done, max_retries, transfer_date) values ( ");
      query.append("'" + requestObj.getCardNo() + "', '" +
                   requestObj.getDeviceType() + "', " + requestObj.getAchType() +
                   ",'" +
                   requestObj.getCardNo() + "',");
      query.append("'" + requestObj.getBankAcctTitle() + "', '" +
                   requestObj.getBankAcctNo() + "', '" +
                   requestObj.getBankAcctType() + "', '" +
                   requestObj.getBankName() + "', ");
      query.append("'" + requestObj.getAmount() + "'," +
                   requestObj.getBankRoutingNo() + ", 'S', 'N', '" +
                   (requestObj.getRetryOnFail() != null &&
                    !requestObj.getRetryOnFail().trim().equals("") ?
                    requestObj.getRetryOnFail().trim() : "N") + "', " +
                   " 0, " +
                   (requestObj.getMaxTries() != null &&
                    !requestObj.getMaxTries().trim().equals("") ?
                    requestObj.getMaxTries().trim() : "2") +
                   ",'" + transferDate + "' )");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for inserting transfer -- > " +
                                      query.toString());
      //insert the transfer
      retValue = insertValues(query.toString());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Transfer inserted successfully with Id -- > " +
                                      retValue);

      return retValue;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Exception while inserting ACH Transfer -- > " +
                                      ex.getMessage());
      //throw the exception
      throw ex;
    } //end catch
  } //end insertAchTransfer

  /**
   * This method returns the transaction list of the specified card no. The method builds the query
   * based on the from-date, to-date and type of transaction such as successful or unsucessful
   * and query the database to get the list of the transactions. The transaction information object
   * is created for each row of the processed query, added in the vector and returend to the client.
   * If an erorr occurs during the processing an exception is thrown which describes the error in
   * detail.
   * @param cardNo String -- Card No
   * @param noOfTrans String -- Number of Transactions to include in the returning list
   * @param typeOfTrans String -- Type of transactions to include in return list
   * @param dateFrom String -- From Date
   * @param dateTo String -- To Date
   * @param chkAmount boolean -- Flag to indicate whether to return the transactions with non zero amount
   * @throws Exception
   * @return Vector -- List of Transactions
   */
  public Vector getCardTransactionList(String cardNo, String noOfTrans,
                                       String typeOfTrans, String dateFrom,
                                       String dateTo, boolean chkAmount) throws
      Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Card No -- > " + cardNo +
                                    " && No Of Trans -- > " + noOfTrans);
    String dFrom = null;
    String dTo = null;

    if (dateFrom != null) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Converting from Date format....");
      //convert date from format
      dFrom = CommonUtilities.convertDateFormat(Constants.DATE_FORMAT,
                                                Constants.WEB_DATE_FORMAT,
                                                dateFrom);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Date from after conversion -- > " +
                                      dFrom);
    } //end if

    if (dateTo != null) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Converting to date format....");
      dTo = CommonUtilities.convertDateFormat(Constants.DATE_FORMAT,
                                              Constants.WEB_DATE_FORMAT, dateTo);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "To Date after conversion -- > " + dTo);
    } //end if
    //build the query
    String query = "select ";

    if (noOfTrans != null)
      query += "first " + noOfTrans;

    query += " 'F'||i.iso_serial_no as i_serial_no,i.trans_date,i.trans_time,i.trans_type,i.business_date,i.account_no_from,r.resp_desc,i.amount_processed,i.iso_serial_no" +
        " from trans_requests i,iso_resp_codes r where i.response_code=r.resp_code and i.card_no=?";

    if (typeOfTrans.equalsIgnoreCase(Constants.SUCCESSFUL_TRANS_ONLY))
      query += " and i.response_code='00'";

    else if (typeOfTrans.equalsIgnoreCase(Constants.UNSUCCESSFUL_TRANS_ONLY))
      query += " and i.response_code <> '00'";

    if (chkAmount)
      query +=
          " and i.amount_processed <> 0 and i.amount_processed is not null";

    if (dateFrom != null && dateTo != null)
      query += " and i.trans_date between '" + dFrom + "' and '" + dTo + "'";

      //append the where clause
    query += " order by i.iso_serial_no desc";

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      //prepare the statement
      pstmt = con.prepareStatement(query);
      //set the host variables
      pstmt.setString(1, cardNo);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Executing the Query -- > " + query);
      //execute the query
      rs = pstmt.executeQuery();
      //create the list of transactions
      Vector transList = new Vector();
      while (rs.next()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "------------------->Got the Transaction<---------------------");
        TransactionObj transObj = new TransactionObj();
        //get the transaction id
        transObj.setTransId(rs.getString("i_serial_no"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trace Audit No -- > " +
                                        transObj.getTransId());

        //get the account no
        transObj.setAccountNo(rs.getString("account_no_from"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Account No From -- > " +
                                        transObj.getAccountNo());

        //get the trans type id
        transObj.setTransTypeId(rs.getString("trans_type"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Type -- > " +
                                        transObj.getTransTypeId());
        //get the transaction date
        transObj.setTransDate(rs.getString("trans_date") + " " +
                              rs.getString("trans_time"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Date -- > " +
                                        transObj.getTransDate());

        String businessDate = rs.getString("business_date");
        if (businessDate != null) {
          //convert in correct format
          String bDate = CommonUtilities.convertDateFormat(Constants.
              WEB_DATE_FORMAT, Constants.DATE_FORMAT, businessDate);
          //get the business date
          transObj.setBusinessDate(bDate);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Business Date -- > " + transObj.getBusinessDate());
        } //end if
        //get the amount
        transObj.setAmount(rs.getString("amount_processed"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Processed Amount -- > " +
                                        transObj.getAmount());

        //get the description
        transObj.setDescription(rs.getString("resp_desc"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Description -- > " +
                                        transObj.getDescription());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "--------------------->End Transaction Info<--------------------------------");
        //add the transaction in the list
        transList.add(transObj);
      } //end while

      return transList;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
      } //end try
      catch (Exception ex) {} //end catch
    } //end finally
  } //end getCardTransactionList



  /**
   * The method is used to get the ACH transaction for the given card no.
   * The method fetches the given no of transaction based on the transaction type and type of the
   * transaction such as successful transaction or unsuccessful, cretes the transcaction object for
   * every row of the fetched row and returns the list of the transaction to the client. In case of
   * any error an exceptin is thrown to the client which descrbies the error in detail with appropiate
   * response code and the response description.
   * @param cardNo String
   * @param noOfTrans String
   * @param typeOfTrans String
   * @param dateFrom String
   * @param dateTo String
   * @param chkAmount boolean
   * @throws Exception
   * @return Vector
   */


  public Vector getACHCardTransactionList(String cardNo, String noOfTrans,
                                          String typeOfTrans, String dateFrom,
                                          String dateTo, boolean chkAmount) throws
      Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Card No -- > " + cardNo +
                                    " && No Of Trans -- > " + noOfTrans);
    String dFrom = null;
    String dTo = null;

    if (dateFrom != null) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Converting from Date format....");
      //convert date from format
      dFrom = CommonUtilities.convertDateFormat(Constants.DATE_FORMAT,
                                                Constants.WEB_DATE_FORMAT,
                                                dateFrom);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Date from after conversion -- > " +
                                      dFrom);
    } //end if

    if (dateTo != null) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Converting to date format....");
      dTo = CommonUtilities.convertDateFormat(Constants.DATE_FORMAT,
                                              Constants.WEB_DATE_FORMAT, dateTo);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "To Date after conversion -- > " + dTo);
    } //end if
    //build the query
    String query = "select ";

    if (noOfTrans != null)
      query += "first " + noOfTrans;

    query += " 'F'||i.iso_serial_no as i_serial_no,i.trans_date,i.trans_time,i.trans_type,i.business_date,i.account_no_from,r.resp_desc,i.amount_processed,i.iso_serial_no" +
        " from trans_requests i,iso_resp_codes r where i.response_code=r.resp_code and i.card_no=?";

    if (typeOfTrans.equalsIgnoreCase(Constants.SUCCESSFUL_TRANS_ONLY))
      query += " and i.response_code='00'";

    else if (typeOfTrans.equalsIgnoreCase(Constants.UNSUCCESSFUL_TRANS_ONLY))
      query += " and i.response_code <> '00'";

    if (chkAmount)
      query +=
          " and i.amount_processed <> 0 and i.amount_processed is not null";

    if (dateFrom != null && dateTo != null)
      query += " and i.trans_date between '" + dFrom + "' and '" + dTo + "'";

    query += " and i.service_id like '%ACH%'";

    //append the where clause
    query += " order by i.iso_serial_no desc";

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      //prepare the statement
      pstmt = con.prepareStatement(query);
      //set the host variables
      pstmt.setString(1, cardNo);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Executing the Query -- > " + query);
      //execute the query
      rs = pstmt.executeQuery();
      //create the list of transactions
      Vector transList = new Vector();
      while (rs.next()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "------------------->Got the Transaction<---------------------");
        TransactionObj transObj = new TransactionObj();
        //get the transaction id
        transObj.setTransId(rs.getString("i_serial_no"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trace Audit No -- > " +
                                        transObj.getTransId());

        //get the account no
        transObj.setAccountNo(rs.getString("account_no_from"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Account No From -- > " +
                                        transObj.getAccountNo());

        //get the trans type id
        transObj.setTransTypeId(rs.getString("trans_type"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Type -- > " +
                                        transObj.getTransTypeId());
        //get the transaction date
        transObj.setTransDate(rs.getString("trans_date") + " " +
                              rs.getString("trans_time"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Date -- > " +
                                        transObj.getTransDate());

        String businessDate = rs.getString("business_date");
        if (businessDate != null) {
          //convert in correct format
          String bDate = CommonUtilities.convertDateFormat(Constants.
              WEB_DATE_FORMAT, Constants.DATE_FORMAT, businessDate);
          //get the business date
          transObj.setBusinessDate(bDate);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Business Date -- > " + transObj.getBusinessDate());
        } //end if
        //get the amount
        transObj.setAmount(rs.getString("amount_processed"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Processed Amount -- > " +
                                        transObj.getAmount());

        //get the description
        transObj.setDescription(rs.getString("resp_desc"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Description -- > " +
                                        transObj.getDescription());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "--------------------->End Transaction Info<--------------------------------");
        //add the transaction in the list
        transList.add(transObj);
      } //end while

      return transList;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
      } //end try
      catch (Exception ex) {} //end catch
    } //end finally
  } //end getCardTransactionList



  /**
   * The method is used to get the VAS transactions for the given card no in sepecifed time interval.
   * The method fetches the given no of transaction based on the transaction type and type of the
   * transaction such as successful transaction or unsuccessful, creates the transcaction object for
   * every row of the fetched row and returns the list of the transaction to the client. In case of
   * any error an exception is thrown to the client which describes the error in detail with appropiate
   * response code and the response description.

   * @param cardNo String
   * @param noOfTrans String
   * @param typeOfTrans String
   * @param dateFrom String
   * @param dateTo String
   * @param chkAmount boolean
   * @throws Exception
   * @return Vector
   */

  public Vector getVASCardTransactionList(String cardNo,String vasTypeId, String noOfTrans,
                                          String typeOfTrans, String dateFrom,
                                          String dateTo, boolean chkAmount) throws
      Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Method for getting VAS transactions --- Card No -- > " + cardNo +
                                    "<---No Of Trans -- > " + noOfTrans +
                                    "<---VAS Type ID -- > " + vasTypeId
                                    +"<---Type Of Trans -- > " + typeOfTrans
                                    +"<---Date From -- > " + dateFrom
                                    +"<---Date To -- > " + dateTo
                                    +"<---Check Amount -- > " + chkAmount);

    String dFrom = null;
    String dTo = null;

    if (dateFrom != null) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Converting from Date format....");
      //convert date from format
      dFrom = CommonUtilities.convertDateFormat(Constants.DATE_FORMAT,
                                                Constants.WEB_DATE_FORMAT,
                                                dateFrom);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Date from after conversion -- > " +
                                      dFrom);
    } //end if

    if (dateTo != null) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Converting to date format....");
      dTo = CommonUtilities.convertDateFormat(Constants.DATE_FORMAT,
                                              Constants.WEB_DATE_FORMAT, dateTo);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "To Date after conversion -- > " + dTo);
    } //end if
    //build the query
    String query = "select ";

    if (noOfTrans != null)
      query += "first " + noOfTrans;

    query += " 'V'||v.vas_trace_audit as vas_trace,v.vas_trans_date,v.vas_trans_time,s.trans_type,v.vas_trans_date,v.card_no,r.resp_desc,v.vas_trans_amt,v.vas_trace_audit" +
        " from vas_trans_requests v,iso_resp_codes r, services s where v.vas_resp_code=r.resp_code and v.vas_service_id=s.service_id and v.card_no=? and v.vas_type_id=?";

    if (typeOfTrans.equalsIgnoreCase(Constants.SUCCESSFUL_TRANS_ONLY))
      query += " and v.vas_resp_code='00'";

    else if (typeOfTrans.equalsIgnoreCase(Constants.UNSUCCESSFUL_TRANS_ONLY))
      query += " and v.vas_resp_code <> '00'";

    if (chkAmount)
      query +=
          " and v.vas_trans_amt <> 0 and v.vas_trans_amt is not null";

    if (dateFrom != null && dateTo != null)
      query += " and v.vas_trans_date between '" + dFrom + "' and '" + dTo +
          "'";

    query += " and v.vas_service_id like '%VAS%'";

    //append the where clause
    query += " order by v.vas_trace_audit desc";

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      //prepare the statement
      pstmt = con.prepareStatement(query);
      //set the host variables
      pstmt.setString(1, cardNo);
      pstmt.setString(2, vasTypeId);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Executing the Query -- > " + query);
      //execute the query
      rs = pstmt.executeQuery();
      //create the list of transactions
      Vector transList = new Vector();
      while (rs.next()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "------------------->Got the Transaction<---------------------");
        TransactionObj transObj = new TransactionObj();
        //get the transaction id
        transObj.setTransId(rs.getString("vas_trace"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trace Audit No -- > " +
                                        transObj.getTransId());

        //get the account no
        transObj.setAccountNo(rs.getString("card_no"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Account No From -- > " +
                                        transObj.getAccountNo());

        //get the trans type id
        transObj.setTransTypeId(rs.getString("trans_type"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Type -- > " +
                                        transObj.getTransTypeId());
        //get the transaction date
        transObj.setTransDate(rs.getString("vas_trans_date") + " " +
                              rs.getString("vas_trans_time"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Date -- > " +
                                        transObj.getTransDate());

        String businessDate = rs.getString("vas_trans_date");
        if (businessDate != null) {
          //convert in correct format
          String bDate = CommonUtilities.convertDateFormat(Constants.
              WEB_DATE_FORMAT, Constants.DATE_FORMAT, businessDate);
          //get the business date
          transObj.setBusinessDate(bDate);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Business Date -- > " + transObj.getBusinessDate());
        } //end if
        //get the amount
        transObj.setAmount(rs.getString("vas_trans_amt"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Processed Amount -- > " +
                                        transObj.getAmount());

        //get the description
        transObj.setDescription(rs.getString("resp_desc"));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Description -- > " +
                                        transObj.getDescription());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "--------------------->End Transaction Info<--------------------------------");
        //add the transaction in the list
        transList.add(transObj);
      } //end while

      return transList;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
      } //end try
      catch (Exception ex) {} //end catch
    } //end finally
  } //end getCardTransactionList


  /**
   * This method is used to execute the given serial no. transaction.
   * It calls the database stored procedure "PROCESS_TRANS" to process the transaction and returns the
   * response object which completely describes the processing status of the transaction. In case of
   * any error response with response code 96 is returned to the client. If an exception is thrown
   * during processing it is returned to the client which describes the error with appropiate
   * response code and its description.
   * @param iserialno String
   * @param applyFee String
   * @throws Exception
   * @return ServicesResponseObj
   */


  public ServicesResponseObj processTransaction(String iserialno,
                                                String applyFee) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Received iserial no -- > " + iserialno +
                                      " && applyFee -- > " + applyFee);
      //create query to process trans
      String query = "Execute procedure process_trans(" + iserialno + ")";

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Executing Query -- > " + query);
      //get the value of the procedure
      String results[] = getProcedureVal(query);
      if (results != null) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Procedure executed :: Resp Code -- > " +
                                        results[0] + " && Resp Desc -- > " +
                                        results[1]);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Commission Amount -- > " + results[2] +
                                        " && Card Balance -- > " + results[3]);

        respObj.setRespCode(results[0]);
        respObj.setRespDesc(results[1]);
        if (respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
          respObj.setFeeAmount(results[2]);
          respObj.setCardBalance(results[3]);
        } //end if
        return respObj;
      } //end if

      respObj.setRespCode("96");
      respObj.setRespDesc("System Error");
      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
  } //end processTransaction


  /**
   * This method returns the response object which is populated with the transaction information.
   * The method fetches the transaction infromation from the database with given transaction id and
   * the card no and populate the response object with this infromation. IN case of any invalid
   * transaction id response code 12 is returned to the client. If an exception occurs during the
   * processing it is returned to the client.
   * @param transId String
   * @param cardNo String
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj getTransactionInfo(String transId, String cardNo) throws
      Exception {
    //make response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Trans Id Received -- > " + transId);
    //make the query
    String query = "select 'F'||t.iso_serial_no as i_serial_no,t.account_no_from,t.trans_type,t.device_id,t.trans_date||' '||t.trans_time as trans_date ,t.business_date,t.card_aceptor_name,t.amount_sp_fee,t.amount_processed,r.resp_desc " +
        "from trans_requests t, iso_resp_codes r where t.response_code=r.resp_code and t.iso_serial_no=? and t.card_no=?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      pstmt = con.prepareStatement(query);
      //set the host variables
      pstmt.setString(1, transId);
      pstmt.setString(2, cardNo);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Executing Query....");
      rs = pstmt.executeQuery();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query executed....");
      if (rs.next()) {
        Vector list = new Vector();
        TransactionObj transObj = new TransactionObj();

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Found the Record....");
        //get the transaction id
        String transactionId = rs.getString("i_serial_no");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Id -- > " + transactionId);
        //set it in response
        transObj.setTransId(transactionId.trim());
        //get the account no
        String acctNo = rs.getString("account_no_from");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Account No -- > " + acctNo);
        //set it in response
        transObj.setAccountNo(acctNo);
        //get the trans type id
        String transTypeId = rs.getString("trans_type");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Type Id -- > " + transTypeId);
        //set it in response
        transObj.setTransTypeId(transTypeId);
        //get the device id
        String deviceId = rs.getString("device_id");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Device Id -- > " + deviceId);
        //set it in response
        transObj.setDeviceId(deviceId);
        //get the trans date
        String transDate = rs.getString("trans_date");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Date -- > " + transDate);
        //set it in response
        transObj.setTransDate(transDate != null ? transDate.trim() : null);
        //get the business date
        String businessDate = rs.getString("business_date");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Business Date-- > " + businessDate);
        if (businessDate != null) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Converting the business date format....");
          String newBusDate = CommonUtilities.convertDateFormat(Constants.
              WEB_DATE_FORMAT, Constants.DATE_FORMAT, businessDate);
          //set it in the response
          transObj.setBusinessDate(newBusDate);
        } //end if
        //get the acceptor name and loc
        String acceptorNameAndLoc = rs.getString("card_aceptor_name");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Acceptor name and loc --> " +
                                        acceptorNameAndLoc);
        //set it in response
        transObj.setAccpNameAndLoc(acceptorNameAndLoc);
        //get the Fee amount
        String feeAmount = rs.getString("amount_sp_fee");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Fee Amount -- > " + feeAmount);
        //set it in response
        transObj.setFeeAmount(feeAmount);
        //get the trans amount
        String amount = rs.getString("amount_processed");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Amount -- > " + amount);
        //set it in response
        transObj.setAmount(amount);
        //get the description
        String desc = rs.getString("resp_desc");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Description -- > " + desc);
        //set it in response
        transObj.setDescription(desc);
        //add it in list
        list.add(transObj);
        //add the list in response
        respObj.setTransactionList(list);
        respObj.setRespCode(Constants.SUCCESS_CODE);
        respObj.setRespDesc(Constants.SUCCESS_MSG);
        return respObj;
      } //end if
      else {
        respObj.setRespCode("12");
        respObj.setRespDesc("Invalid Trans Id supplied");
        return respObj;
      } //end else
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
      } //end try
      catch (Exception ex) {} //end catch
    } //end finally
  } //end getTransactionInfo

  /**
   * This method is used to insert the reversal entry in the database table "iso_finance_msgs".
   * The method first checks whether the partial amount is zero. If it is then it insert blank
   * at the left of the partial amount figure. The method then inserts the recrods into the
   * iso-finance-msgs database table. At the end of the processing serial number of the inserted
   * transaction is returned to the client.
   * @param cardNo String -- Card No
   * @param switchId String -- Switch ID
   * @param deviceType String -- Device Type
   * @param transAmount double -- Transaction Amount
   * @param partialAmount double -- Partial Reversal Amount
   * @param reverseTransFee String -- Reverse Transaction Fee or Not
   * @param applyFee String -- Apply reversal Fee or not
   * @param desc String -- Description
   * @throws Exception
   * @return String -- iso serial no
   */
  public String insertReversalEntry(String cardNo, String switchId,
                                    String deviceType, double transAmount,
                                    double partialAmount,
                                    String reverseTransFee, String applyFee,
                                    String desc) throws Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Arguments Received :: Amount -- > " +
                                    transAmount +
                                    " && Partial Amount -- > " + partialAmount +
                                    " && Reverse Trans Fee -- > " +
                                    reverseTransFee + " && Apply Fee -- > " +
                                    applyFee);

    String respCode = "00";
    if (reverseTransFee.equalsIgnoreCase("Y") && applyFee.equalsIgnoreCase("Y"))
      respCode = "01";

    String partialStr = null;
    if (partialAmount != 0.0) {
      //make the string for partial reversal of transaction
      partialStr = CommonUtilities.addPadding(CommonUtilities.Padding.LEFT, 12,
                                              transAmount + "", "0");
      partialStr +=
          CommonUtilities.addPadding(CommonUtilities.Padding.LEFT, 12,
                                     partialAmount + "", "0");
      partialStr += "000000000000000000";
    } //end if

    //build the query to insert the transaction
    String query = "insert into iso_finance_msgs (i_log_datetime,is_rev_linkd,is_tran_fee_linkd,is_c2c_linkd,is_pin_based,is_international,switch_id,device_type,i_002pan" +
        ",i_004amttran,i_013datelocltran,i_012timelocltran,i_018merchant_ccd,i_039respcode,i_043cdacptnamelo" +
        ",description,i_message_type,i_095replacamts) values('" +
        DateUtil.getCurrentDate() + " " + DateUtil.getCurrentTime()
        + "'N','N','N','N','N','" + switchId + "','" + deviceType + "','" +
        cardNo + "'," + (partialAmount != 0.0 ? partialAmount : transAmount) +
        ",'" + DateUtil.getCurrentDate() + "','" + DateUtil.getCurrentTime() +
        "',0,'" + respCode + "','" + desc + "','" + desc + "','0420','" +
        partialStr + "'";

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Going to log the transaction.....");
    //execute the query and get the serial
    long id = insertValues(query);
    return id + "";
  } //end insertReversalEntry

  /**
   * This method checks whether debit is allowed on the given card or not. The method calls the
   * database stored procedure "CHECK_DEBIT" to determine whether debit is allowed on the given
   * card number. Response object is returned to the client at the end of the processing which
   * describes whether debit is allowed on the given card no.
   * @param cardNo String -- Card No
   * @param amount String -- Amount to debit
   * @param serviceId String -- Service to Apply
   * @param applyFee String -- Apply Fee or not
   * @param batchMode boolean -- Is batch Mode or Online Mode
   * @throws Exception
   * @return ServicesResponseObj -- Object containing response information
   */
  public ServicesResponseObj checkDebit(String cardNo, String amount,
                                        String serviceId,
                                        String applyFee, boolean batchMode
                                        ,String arn
                                        ,String cardAccptCode
                                        ,String mti
                                        ,String acquirerId) throws
      Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Arguments received ::" +
                                    "<--Card No--> " + cardNo + "<--Amount--> " + amount
                                    + "<--Service Id--> " + serviceId
                                    + "<--Apply Fee--> " + applyFee +
                                    "<---batchMode--->" + batchMode +
                                    "<---ARN--->" + arn +
                                    "<---cardAccptCode--->" + cardAccptCode +
                                    "<---MTI--->" + mti +
                                    "<---Acquirer ID--->" + acquirerId);
    boolean appFee = (applyFee != null && applyFee.trim().equalsIgnoreCase("N") ? false : true);

    CallableStatement cstmt = null;
    ResultSet rs = null;
    StringBuffer query = new StringBuffer();
    try {
      ServicesResponseObj respObj = new ServicesResponseObj();

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Going to parse the amount....");
      //parse the amount in double
      double amt = Double.parseDouble(amount);
//      amt = amt * -1;
      //prepare the query
      query.append("Execute procedure check_debit(cardno = ?,init_str = ?,amount = ?,checkbalance = ?,pservice_id = ?,papply_fee = ?,prrn = ?,pcard_aceptor_code = ?,pmti = ?,pacq_inst_code = ?)");
//          = "Execute procedure CHECK_DEBIT(" +
//          "cardno='" + cardNo + "'," +
//          "init_str=null" + "," +
//          "amount=" + amt + "," +
//          "checkbalance=" + (batchMode ? 0 : 1) + "," +
//          "pservice_id='" + serviceId + "'," +
//          "papply_fee=" + (appFee ? 1 : 0) + ")";

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query -- > " + query);
      //prepare the statement
      cstmt = con.prepareCall(query.toString());
      cstmt.setString(1,cardNo);
      cstmt.setString(2,null);
      cstmt.setString(3,amount);
      cstmt.setInt(4,(batchMode ? 0 : 1));
      cstmt.setString(5,serviceId);
      cstmt.setInt(6,(appFee ? 1 : 0));
      cstmt.setString(7,arn);
      cstmt.setString(8,cardAccptCode);
      cstmt.setString(9,mti);
      cstmt.setString(10,acquirerId);
      //execute the statement
      rs = cstmt.executeQuery();

      if (rs.next()) {
        respObj.setRespCode(rs.getString(1));
        respObj.setRespDesc(rs.getString(2));
        respObj.setFeeAmount(rs.getString(3));
      } //end if

      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null)
          rs.close();
        if (cstmt != null)
          cstmt.close();
      } //end try
      catch (Exception ex) {} //end catch
    } //end finally
  } //end checkDebit

  /**
   * This method is used to check whether the credits are allowed on the supplied card or not.
   * The method call the database stored procedure "CHECK_CREDIT" to determine whether credit is
   * allowed on the given card number or not. Response object is created from the response returned
   * form the database procedure's retunened parameters and returned to the client at the end of the
   * processing which describes whether credit was allowed on the given card no.
   * @param cardNo String -- Card No
   * @param amount String -- Amount to credit
   * @param serviceId String -- Service to apply
   * @param applyFee String -- Apply fee or not
   * @param batchMode boolean -- Is Batch Mode or Online Mode
   * @throws Exception
   * @return ServicesResponseObj -- Object containing response information
   */
  public ServicesResponseObj checkCredit(String cardNo, String amount,
                                         String serviceId,
                                         String applyFee, boolean batchMode
                                         ,String arn
                                         ,String cardAccptCode
                                         ,String mti
                                         ,String acquirerId) throws
      Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Arguments received ::" +
                                    "Card No -- > " + cardNo + "\n" +
                                    "Amount -- > " + amount + "\n" +
                                    "Apply Fee -- > " + applyFee
                                    + "<--batchMode-->" + batchMode
                                    + "<--serviceId-->" + serviceId
                                    + "<--ARN-->" + arn
                                    + "<--cardAccptCode-->" + cardAccptCode
                                    + "<--MTI-->" + mti
                                    + "<--AcquirerId-->" + acquirerId);
    boolean appFee = (applyFee != null && applyFee.trim().equalsIgnoreCase("N") ? false : true);

    CallableStatement cstmt = null;
    ResultSet rs = null;
    try {
      ServicesResponseObj respObj = new ServicesResponseObj();
      //prepare the query
      StringBuffer query = new StringBuffer();
      query.append("Execute procedure check_credit(pcard_no = ?,init_str = ?,pamount = ?,checkbalance = ?,papplyfee = ?,pservice_id = ?,prrn = ?,pcard_aceptor_code = ?,pmti = ?,pacq_inst_code = ?)");

//      query.append("Execute procedure CHECK_CREDIT(");
//      query.append("pcard_no= '" + cardNo + "',");
//      query.append(" init_str= null,");
//      query.append(" pamount= " + amount + ",");
//      query.append(" checkbalance= " + (batchMode ? 0 : 1) + ",");
//      query.append(" papplyfee=" + (appFee ? 1 : 0) + ",");
//      if (serviceId != null) {
//        query.append(" pservice_id= '" + serviceId + "')");
//      }
//      else {
//        query.append(" pservice_id= " + null +")");
//      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query -- > " + query);
      //prepare the statement
      cstmt = con.prepareCall(query.toString());
      cstmt.setString(1,cardNo);
      cstmt.setString(2,null);
      cstmt.setString(3,amount);
      cstmt.setInt(4,(batchMode ? 0 : 1));
      cstmt.setInt(5,(appFee ? 1 : 0));
      cstmt.setString(6,serviceId);
      cstmt.setString(7,arn);
      cstmt.setString(8,cardAccptCode);
      cstmt.setString(9,mti);
      cstmt.setString(10,acquirerId);

      //execute the statement
      rs = cstmt.executeQuery();

      if (rs.next()) {
        respObj.setRespCode(rs.getString(1));
        respObj.setRespDesc(rs.getString(2));
        respObj.setFeeAmount(rs.getString(3));
      } //end if

      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null)
          rs.close();
        if (cstmt != null)
          cstmt.close();
      } //end try
      catch (Exception ex) {} //end catch
    } //end finally
  } //end checkCredit



  /**
   * The method is used to load funds on the OLTP. The method uses the database stored procedure
   * "LOAD_FUNDS" to load the given amount into the given card no. The method creates the response
   * object and populates it with the parameter returned from the database stored procedure execution.
   * In case of any exception the method throws exception to the calling method which describes
   * the cause of the error in detail.
   * @param cardNo String
   * @param amount String
   * @param deviceType String
   * @param switchBal String
   * @param retRefNo String
   * @param respCode String
   * @param respDesc String
   * @param applyFee String
   * @param serviceId String
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj loadFundsAtOltp(ServicesRequestObj reqObj,
                                             String switchBal,
                                             String retRefNo,
                                             String respCode,
                                             String respDesc
                                             ) throws Exception {
    ServicesResponseObj respObj = new ServicesResponseObj();
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_CONFIG), "Going to load funds --- Card--->"+ reqObj.getCardNo()
                                    + "<---Amount--->" + reqObj.getAmount()
                                    + "<---getDeviceType--->" + reqObj.getDeviceType()
                                    + "<---switchBal--->" + switchBal
                                    + "<---retRefNo--->" + retRefNo
                                    + "<---respCode--->" + respCode
                                    + "<---respDesc--->" + respDesc
                                    + "<---reqObj.getApplyFee(--->" + reqObj.getApplyFee()
                                    + "<---getServiceId--->" + reqObj.getServiceId()
                                    + "<---getAcquirerId--->" + reqObj.getAcquirerId()
                                    + "<---getAcqData1--->" + reqObj.getAcqData1()
                                    + "<---getAcqData2--->" + reqObj.getAcqData2()
                                    + "<---getAcqData3--->" + reqObj.getAcqData3()
                                    + "<---getAcqUsrId--->" + reqObj.getAcqUsrId()
                                    + "<---reqObj.getCardAcceptorId--->" + reqObj.getCardAcceptorId()
                                    + "<---getCardAcceptNameAndLoc--->" + reqObj.getCardAcceptNameAndLoc()
                                    + "<---getMcc--->" + reqObj.getMcc()
                                    + "<---getDeviceId--->" + reqObj.getDeviceId()
                                    + "<---getVirtualAccount--->" + reqObj.getVirtualAccount()
                                    + "<---getReceiverName--->" + reqObj.getReceiverName()
                                    + "<---Request Description--->" + reqObj.getDescription());


    query.append("execute procedure LOAD_FUNDS(pcard_no = ?, pamount = ?,pdevice_type = ?,pswitch_bal = ?,pret_ref = ?,presp_code = ?,pdesc = ?,papply_fee = ?,pservice_id = ?, pacq_id = ?, psub_srv = ?, pdata_2 = ?, pdata_3 = ?, pacq_userid = ?, pcard_acptr_code = ?, pcard_acptr_name = ?, pmcc = ?, pdev_id = ?, pvirt_account = ?, precver_name = ?, plocal_dtime = ?)");

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_CONFIG), "Executing Query -- > " + query);

    try {
      //prepare the call
      cstmt = con.prepareCall(query.toString());

      cstmt.setString(1,reqObj.getCardNo());
      cstmt.setString(2,reqObj.getAmount());
      cstmt.setString(3,reqObj.getDeviceType());
      cstmt.setString(4,switchBal);
      cstmt.setString(5,retRefNo);
      cstmt.setString(6,respCode);
      cstmt.setString(7,reqObj.getDescription());
      cstmt.setString(8,(reqObj.getApplyFee().trim().equals("Y") ? "1" : "0"));
      cstmt.setString(9,reqObj.getServiceId());
      cstmt.setString(10,reqObj.getAcquirerId());
      cstmt.setString(11,reqObj.getAcqData1());
      cstmt.setString(12,reqObj.getAcqData2());
      cstmt.setString(13,reqObj.getAcqData3());
      cstmt.setString(14,reqObj.getAcqUsrId());
      cstmt.setString(15,reqObj.getCardAcceptorId());
      cstmt.setString(16,reqObj.getCardAcceptNameAndLoc());
      cstmt.setString(17,reqObj.getMcc());
      cstmt.setString(18,reqObj.getDeviceId());
      cstmt.setString(19,reqObj.getVirtualAccount());
      cstmt.setString(20,reqObj.getReceiverName());
      cstmt.setString(21,reqObj.getLocalDateTime());


      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "Executing Query -- > " + query);


      //execute the procedure
      rs = cstmt.executeQuery();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "Query Executed....Looking for values...");
      if (rs.next()) {
        String response = rs.getString(1);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Response Code -- > " + response);
        //set in response object
        respObj.setRespCode(response);

        String desc = rs.getString(2);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Response Desc -- > " + desc);
        //set in response obj
        respObj.setRespDesc(desc);

        String cardBalance = rs.getString(3);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Card Balance -- > " + cardBalance);
        //set it in response
        respObj.setCardBalance(cardBalance);

        String feeAmount = rs.getString(4);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Fee Amount -- > " + feeAmount);
        //set it in response
        respObj.setFeeAmount(feeAmount);

        String isoSerialNo = rs.getString(5);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "ISO Serial No -- > " + isoSerialNo);
        //set it in response
        respObj.setTransId(isoSerialNo);
      } //end if

      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null)
          rs.close();
        if (cstmt != null)
          cstmt.close();
      } //end try
      catch (Exception ex) {} //end
    } //end finally
  } //end if


  /**
   * The method is used to check whether reversal is allowed or not. It calls the database stored
   * procedure "CHK_TRANS_REVERSAL" to decide whether the reversal of the transaction is allowed
   * or not. The method populates the response object with the parameters returned from the database
   * procedure and returns it to the client. In case of any exception the method returns the exception
   * to the calling method which describes the cause of the error in detail.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */


  public ServicesResponseObj checkReversal(ServicesRequestObj requestObj) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Inside Check Reversal... TransID--->" + requestObj.getTransId()
                                    + "<--getTransDate--->" + requestObj.getTransDate()
                                    + "<--getAmount--->" + requestObj.getAmount()
                                    + "<--getApplyFee--->" + requestObj.getApplyFee()
                                    + "<--getReverseTransFee--->" + requestObj.getReverseTransFee()
                                    + "<--getCardNo--->" + requestObj.getCardNo()
                                    + "<--getRetreivalRefNum--->" + requestObj.getRetreivalRefNum()
                                    + "<--getCardAcceptorId--->" + requestObj.getCardAcceptorId());
    StringBuffer query = new StringBuffer();
    StringBuffer updQuery = new StringBuffer();

    CallableStatement cstmt = null;
    ResultSet rs = null;

    PreparedStatement pstmt = null;

    try {
      if(requestObj.getCardNo() != null){
        updQuery.append("UPDATE cards SET card_no = ? WHERE card_no = ?");
        pstmt = con.prepareStatement(updQuery.toString());
        pstmt.setString(1,requestObj.getCardNo());
        pstmt.setString(2,requestObj.getCardNo());
        pstmt.executeUpdate();
        pstmt.close();
        pstmt = null;
      }

      query.append("Execute procedure chk_trans_reversal(pserial_no = ?, pdate = ?, pamount = ?, papply_fee = ?, preverse_fee = ?, pcard_no = ? , pret_ref_no = ?, pcard_acptr_code = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Executing Query--> " + query);
      //prepare the statement
      cstmt = con.prepareCall(query.toString());
      cstmt.setString(1,requestObj.getTransId());
      cstmt.setString(2,requestObj.getTransDate());
      cstmt.setString(3,requestObj.getAmount());
      cstmt.setString(4,requestObj.getApplyFee());
      cstmt.setString(5,requestObj.getReverseTransFee());
      cstmt.setString(6,requestObj.getCardNo());
      cstmt.setString(7,requestObj.getRetreivalRefNum());
      cstmt.setString(8,requestObj.getCardAcceptorId());
      //execute the query
      rs = cstmt.executeQuery();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query executed..");

      if (rs.next()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Found the return from procedure...");
        //get the response code
        String respCode = rs.getString(1);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Code--> " + respCode);
        //get the response description
        String respDesc = rs.getString(2);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Desc--> " + respDesc);
        //get the amount
        double amount = rs.getDouble(3);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Reversal Amount--> " + amount);
        //get the fee amount
        double fee = rs.getDouble(4);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Fee Amount--> " + fee);

        //add both amounts
        amount += fee;
        //set these values in response
        respObj.setRespCode(respCode);
        respObj.setRespDesc(respDesc);
        respObj.setFeeAmount(amount + "");
      } //end if

      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception in check Reversal--> " +
                                      ex.getMessage());
      throw ex;
    } //end catch
    finally {
      if (rs != null) rs.close();
      if (cstmt != null) cstmt.close();
    } //end finally
  } //end checkReversal

  /**
   * The method is used to do the reversal for the given transaction. The method calls the
   * database stored procedure "CONFIRM_REVERSAL" to cofirm the reversal of the given transaction.
   * The method populates the response object with the parameters returned from the database
   * procedure and returns it to the client. In case of any exception the method returns the
   * exception to the calling method which describes the cause of the error in detail.
   * @param requestObj ServicesRequestObj
   * @param respCode String
   * @param respDesc String
   * @param retRefNo String
   * @param switchBal String
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj confirmReveral(ServicesRequestObj requestObj,
                                            String respCode, String respDesc,
                                            String retRefNo, String switchBal) throws
      Exception {
    //make the response object
    ServicesResponseObj respObj = new ServicesResponseObj();
    StringBuffer query = new StringBuffer();

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Inside Check Reversal... Attributes --- getTransId()" + requestObj.getTransId()
                                    + "<---getApplyFee--->" + requestObj.getApplyFee()
                                    + "<---getReverseTransFee--->" + requestObj.getReverseTransFee()
                                    + "<---getAmount--->" + requestObj.getAmount()
                                    + "<---respCode--->" + respCode
                                    + "<---respDesc--->" + respDesc
                                    + "<---retRefNo--->" + retRefNo
                                    + "<---switchBal--->" + switchBal
                                    + "<---getDeviceType--->" + requestObj.getDeviceType()
                                    + "<---getDeviceId--->" + requestObj.getDeviceId()
                                    + "<---getCardAcceptorId--->" + requestObj.getCardAcceptorId()
                                    + "<---getCardAcceptNameAndLoc--->" + requestObj.getCardAcceptNameAndLoc()
                                    + "<---getLocalDateTime--->" + requestObj.getLocalDateTime()
                                    + "<---getMcc--->" + requestObj.getMcc()
                                    + "<---getAcquirerId--->" + requestObj.getAcquirerId()
                                    + "<---getAcqData1--->" + requestObj.getAcqData1()
                                    + "<---getAcqData2--->" + requestObj.getAcqData2()
                                    + "<---getAcqData3--->" + requestObj.getAcqData3()
                                    + "<---getAcqUsrId--->" + requestObj.getAcqUsrId());
    query.append("execute procedure confirm_reversal(pserial_no = ?,papply_fee = ?,preverse_fee = ?,pamount = ?,presp_code = ?,presp_desc = ?,pret_ref_no = ?,pswitch_bal = ?,pdevice_type = ?,pdevice_id = ?,pcrd_acpt_id = ?,pcrd_acpt_name_loc = ?,ptime_locl_tran = ?,pmerchant_ccd = ?,pacq_id = ?,psub_srv = ?,pdata_2 = ?,pdata_3 = ?,pacq_userid = ?)");

    CallableStatement cstmt = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Creating Statement...");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Executing Query--> " + query);
      //prepare the statement
      cstmt = con.prepareCall(query.toString());
      cstmt.setString(1,requestObj.getTransId());
      cstmt.setString(2,requestObj.getApplyFee());
      cstmt.setString(3,requestObj.getReverseTransFee());
      cstmt.setString(4,requestObj.getAmount());
      cstmt.setString(5,respCode);
      cstmt.setString(6,respDesc);
      cstmt.setString(7,retRefNo);
      cstmt.setString(8,switchBal);
      cstmt.setString(9,requestObj.getDeviceType());
      cstmt.setString(10,requestObj.getDeviceId());
      cstmt.setString(11,requestObj.getCardAcceptorId());
      cstmt.setString(12,requestObj.getCardAcceptNameAndLoc());
      cstmt.setString(13,requestObj.getLocalDateTime());
      cstmt.setString(14,requestObj.getMcc());
      cstmt.setString(15,requestObj.getAcquirerId());
      cstmt.setString(16,requestObj.getAcqData1());
      cstmt.setString(17,requestObj.getAcqData2());
      cstmt.setString(18,requestObj.getAcqData3());
      cstmt.setString(19,requestObj.getAcqUsrId());

      //execute the query
      rs = cstmt.executeQuery();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query executed..");

      if (rs.next()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Found the return from procedure...");
        //get the transaction id
        String transId = rs.getString(1);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Id--> " + transId);
        //get the response code
        String code = rs.getString(2);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Code--> " + code);
        //get the response description
        String desc = rs.getString(3);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Desc--> " + desc);
        //get the amount
        String balance = rs.getString(4);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Card Balance--> " + balance);
        //get the fee amount
        String feeAmount = rs.getString(5);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Fee Amount--> " + feeAmount);
        //get the business date
        String businessDate = rs.getString(6);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Business date--> " + businessDate);

        //set these values in response
        respObj.setRespCode(code);
        respObj.setRespDesc(desc);
        respObj.setCardBalance(balance);
        respObj.setFeeAmount(feeAmount);
        respObj.setTransId(transId);
        if (businessDate != null && !businessDate.trim().equals(""))
          respObj.setBusinessDate(CommonUtilities.convertDateFormat(Constants.
              WEB_DATE_FORMAT, Constants.INFORMIX_DATE_FORMAT, businessDate));
      } //end if

      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                      "Exception in confirm Reversal--> " +
                                      ex.getMessage());
      throw ex;
    } //end catch
    finally {
      if (rs != null) rs.close();
      if (cstmt != null) cstmt.close();
    } //end finally
  } //end confirmReversal


  /**
   * This method validates whether ACH Load Account is valid or not. The method gets the ach type and
   * verify status field values from the database against the given information to validate the ACH
   * account. If no information were found against the given parameter then service logs the
   * transaction and returns response with response code 06 which indicate that the banking
   * information is not valid for the given parameters. The service then checks whether supplied
   * information is verfied and for ACH Load purpose, if it is not then it returns response with
   * appropiate error response code and description. If every thing is OK then service response
   * with response code 00 which meean that ACH Load Account is a valid account is returned to the
   * calling method.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */

  public ServicesResponseObj validateACHLoadAccount(ServicesRequestObj
      requestObj) throws Exception {
    //make services response
    ServicesResponseObj respObj = new ServicesResponseObj();

    String query =
        "select ach_type,verify_status from ach_accounts where account_no='"
        + requestObj.getBankAcctNo() + "' and account_type='" + requestObj.
        getBankAcctType() + "' and routing_no='" + requestObj.getBankRoutingNo() +
        "'";

    if (requestObj.getBankName() != null &&
        !requestObj.getBankName().trim().equals(""))
      query += " and bank_name='" + requestObj.getBankName().trim() + "'";

    if (requestObj.getBankAcctTitle() != null &&
        !requestObj.getBankAcctTitle().trim().equals(""))
      query += " and account_title='" + requestObj.getBankAcctTitle().trim() +
          "'";

    query += " and user_id=(select user_id from cards where card_no='" +
        requestObj.getCardNo() + "')";

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Query --> " + query);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Executing query...");
    //get the value
    String[] val = getValues(query);
    if (val == null) {
      respObj.setRespCode("06");
      respObj.setRespDesc("Supplied Banking information not found");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Going to log the error transaction::Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //log the transaction
      String[] transIds = FinancialServiceHome.getInstance(con).logTransaction(
          requestObj.getCardNo(), Constants.LOAD_FUNDS_SERVICE,
          requestObj.getDeviceType(), null, "0200", requestObj.getAmount(),
          respObj.getRespDesc(), "0", respObj.getRespCode(),
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
    else if (val[0] != null && val[0].trim().equalsIgnoreCase("2")) {
      respObj.setRespCode("06");
      respObj.setRespDesc(
          "Supplied Banking information is not for load transactions");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Going to log the error transaction::Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //log the transaction
      String[] transIds = FinancialServiceHome.getInstance(con).logTransaction(
          requestObj.getCardNo(), Constants.LOAD_FUNDS_SERVICE,
          requestObj.getDeviceType(), null, "0200", requestObj.getAmount(),
          respObj.getRespDesc(), "0", respObj.getRespCode(),
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
    } //end else if
    else if (val[0] != null && val[0].trim().equalsIgnoreCase("1") && val[1] != null &&
             !val[1].trim().equalsIgnoreCase("V")) {
      respObj.setRespCode("06");
      respObj.setRespDesc("Supplied Banking Information is not verified");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Going to log the error transaction::Code -- > " +
                                      respObj.getRespCode() + " && Desc -- > " +
                                      respObj.getRespDesc());
      //log the transaction
      String[] transIds = FinancialServiceHome.getInstance(con).logTransaction(
          requestObj.getCardNo(), Constants.LOAD_FUNDS_SERVICE,
          requestObj.getDeviceType(), null, "0200", requestObj.getAmount(),
          respObj.getRespDesc(), "0", respObj.getRespCode(),
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
    } //end else if

    respObj.setRespCode(Constants.SUCCESS_CODE);
    respObj.setRespDesc(Constants.SUCCESS_MSG);
    return respObj;
  } //end validateACHLoadAccount



  /**
   * This method is used to perform the pre-authorization. The method uses the database stored
   * procedure "LOG_PREAUTH_TRANS" to perform the pre-authorization. The service then builds the
   * response object and populates it with the response paramters returned from the database procedure.
   * In case of any error the service throws exception which describes the cause of the error in
   * detail.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   * @return ServicesResponseObj
   */


  public ServicesResponseObj preAuthorization(ServicesRequestObj requestObj) throws
      Exception {
    ServicesResponseObj respObj = new ServicesResponseObj();
    StringBuffer query = new StringBuffer();

    //define query
//    String query = "Execute procedure log_preauth_trans(" +
//        "pi_002pan='" + requestObj.getCardNo() + "'," +
//        "pi_004amttran= " + requestObj.getAmount() + "," +
//        "pi_042cdacptid=" +
//        (requestObj.getCardAcceptorId() != null ?
//         "'" + requestObj.getCardAcceptorId() + "'" : null) + "," +
//        "pi_043cdacptnamelo=" +
//        (requestObj.getCardAcceptNameAndLoc() != null ?
//         "'" + requestObj.getCardAcceptNameAndLoc() + "'" : null) + "," +
//        "pi_018merchant_ccd=" +
//        (requestObj.getMcc() != null ? "'" + requestObj.getMcc() + "'" : null) +
//        "," +
//        "pi_041cdacpttmlid=" +
//        (requestObj.getDeviceId() != null ?
//         "'" + requestObj.getDeviceId() + "'" : null) + "," +
//        "pdevice_type=" +
//        (requestObj.getDeviceType() != null ?
//         "'" + requestObj.getDeviceType() + "'" : null) + "," +
//        "pi_102account_id1=" +
//        (requestObj.getAccountNo() != null ?
//         "'" + requestObj.getAccountNo() + "'" : null) + "," +
//        "pdescription=" +
//        (requestObj.getDescription() != null ?
//         "'" + requestObj.getDescription() + "'" : null) + "," +
//        "papply_fee=" +
//        (requestObj.getApplyFee() != null ?
//         "'" + requestObj.getApplyFee() + "'" : null) + ")";


    CallableStatement cstmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "<---- Pre Auth --- getCardNo--->" + requestObj.getCardNo()
                                      + "<---getAmount--->" + requestObj.getAmount()
                                      + "<---getCardAcceptorId--->" + requestObj.getCardAcceptorId()
                                      + "<---getCardAcceptNameAndLoc--->" + requestObj.getCardAcceptNameAndLoc()
                                      + "<---getMcc--->" + requestObj.getMcc()
                                      + "<---getDeviceId--->" + requestObj.getDeviceId()
                                      + "<---getDeviceType--->" + requestObj.getDeviceType()
                                      + "<---getAccountNo--->" + requestObj.getAccountNo()
                                      + "<---getDescription--->" + requestObj.getDescription()
                                      + "<---getApplyFee--->" + requestObj.getApplyFee()
                                      + "<---getAcquirerId--->" + requestObj.getAcquirerId()
                                      + "<---getAcqData1--->" + requestObj.getAcqData1()
                                      + "<---getAcqData2--->" + requestObj.getAcqData2()
                                      + "<---getAcqData3--->" + requestObj.getAcqData3()
                                      + "<---getAcqUsrId--->" + requestObj.getAcqUsrId()
                                      + "<---getRetreivalRefNum--->" + requestObj.getRetreivalRefNum()
                                      + "<---IS International--->" + requestObj.getIsInternational());

      query.append("Execute procedure log_preauth_trans(pi_002pan = ?,pi_004amttran = ?,pi_042cdacptid = ?,pi_043cdacptnamelo = ?,pi_018merchant_ccd = ?,pi_041cdacpttmlid = ?,pdevice_type = ?,pi_102account_id1 = ?,pdescription = ?,papply_fee = ?,pacq_id = ?,psub_srv = ?,pdata_2 = ?,pdata_3 = ?,pacq_userid = ?,pretrieval_ref = ?,p_intl = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query --> " + query);

      //create the statement
      cstmt = con.prepareCall(query.toString());

      cstmt.setString(1,requestObj.getCardNo());
      cstmt.setString(2,requestObj.getAmount());
      cstmt.setString(3,requestObj.getCardAcceptorId());
      cstmt.setString(4,requestObj.getCardAcceptNameAndLoc());
      cstmt.setString(5,requestObj.getMcc());
      cstmt.setString(6,requestObj.getDeviceId());
      cstmt.setString(7,requestObj.getDeviceType());
      cstmt.setString(8,requestObj.getAccountNo());
      cstmt.setString(9,requestObj.getDescription());
      cstmt.setString(10,requestObj.getApplyFee());
      cstmt.setString(11,requestObj.getAcquirerId());
      cstmt.setString(12,requestObj.getAcqData1());
      cstmt.setString(13,requestObj.getAcqData2());
      cstmt.setString(14,requestObj.getAcqData3());
      cstmt.setString(15,requestObj.getAcqUsrId());
      cstmt.setString(16,requestObj.getRetreivalRefNum());
      cstmt.setString(17,requestObj.getIsInternational());

      //execute the query
      rs = cstmt.executeQuery();

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query Executed..");

      if (rs.next()) {
        //get response code
        String respCode = rs.getString(1);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Code --> " + respCode);
        //set it in resp obj
        respObj.setRespCode(respCode);
        //get response desc
        String respDesc = rs.getString(2);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response desc --> " + respDesc);
        //set it in resp obj
        respObj.setRespDesc(respDesc);
        //get pre auth trans id
        String preAuthTransId = rs.getString(3);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Pre Auth Trans Id --> " +
                                        preAuthTransId);
        //set it in resp obj
        respObj.setTransId(preAuthTransId);
        //get the card balance
        String balance = rs.getString(4);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Card Balance --> " + balance);
        //set in resp obj
        respObj.setCardBalance(balance);
        //get the fee amount
        String feeAmount = rs.getString(5);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Fee Amount --> " + feeAmount);
        //set it in resp obj
        respObj.setFeeAmount(feeAmount);
      } //end if
      //return the response
      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception --> " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      if (rs != null) rs.close();
      if (cstmt != null) cstmt.close();
    } //end finally
  } //end preAuthorization

  /**
   * This method is used to settle down the pre-authorized transaction. The method uses the database stored
   * procedure "PROCESS_FORCED_POST" to force the pre-authorization. The service then builds the
   * response object and populates it with the response paramters returned from the database procedure.
   * In case of any exception the service throws exception which describes the cause of the error in
   * detail.
   * @param requestObj ServicesRequestObj -- Request information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */
  public ServicesResponseObj focePostTransaction(ServicesRequestObj requestObj) throws
      Exception {
    //create services response obj
    ServicesResponseObj respObj = new ServicesResponseObj();
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "<--Force Post Transaction --- getCardNo-->" + requestObj.getCardNo()
                                + "<--getAmount--->" + requestObj.getAmount()
                                + "<--getPreAuthTransId--->" + requestObj.getPreAuthTransId()
                                + "<--getCardAcceptorId--->" + requestObj.getCardAcceptorId()
                                + "<--getCardAcceptNameAndLoc--->" + requestObj.getCardAcceptNameAndLoc()
                                + "<--getMcc--->" + requestObj.getMcc()
                                + "<--getDeviceId--->" + requestObj.getDeviceId()
                                + "<--getDeviceType--->" + requestObj.getDeviceType()
                                + "<--getAccountNo--->" + requestObj.getAccountNo()
                                + "<--getDescription--->" + requestObj.getDescription()
                                + "<--getApplyFee--->" + requestObj.getApplyFee()
                                + "<--getAcquirerId--->" + requestObj.getAcquirerId()
                                + "<--getAcqData1--->" + requestObj.getAcqData1()
                                + "<--getAcqData2--->" + requestObj.getAcqData2()
                                + "<--getAcqData3--->" + requestObj.getAcqData3()
                                + "<--getAcqUsrId--->" + requestObj.getAcqUsrId()
                                + "<--getRetreivalRefNum--->" + requestObj.getRetreivalRefNum());

      query.append("execute procedure process_forcd_post(pi_002pan = ?, pi_004amttran = ?, ppreauth_serial = ?, pi_042cdacptid = ?, pi_043cdacptnamelo = ?, pi_018merchant_ccd = ?, pi_041cdacpttmlid = ?,pdevice_type = ?,pi_102account_id1 = ?,pdescription = ?,papply_fee = ?,pacq_id = ?,psub_srv = ?,pdata_2 = ?,pdata_3 = ?, pacq_userid = ?, pretrieval_ref = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Executing Query --> " + query);

      //create the statement
      cstmt = con.prepareCall(query.toString());
      cstmt.setString(1,requestObj.getCardNo());
      cstmt.setString(2,requestObj.getAmount());
      cstmt.setString(3,requestObj.getPreAuthTransId());
      cstmt.setString(4,requestObj.getCardAcceptorId());
      cstmt.setString(5,requestObj.getCardAcceptNameAndLoc());
      cstmt.setString(6,requestObj.getMcc());
      cstmt.setString(7,requestObj.getDeviceId());
      cstmt.setString(8,requestObj.getDeviceType());
      cstmt.setString(9,requestObj.getAccountNo());
      cstmt.setString(10,requestObj.getDescription());
      cstmt.setString(11,requestObj.getApplyFee());
      cstmt.setString(12,requestObj.getAcquirerId());
      cstmt.setString(13,requestObj.getAcqData1());
      cstmt.setString(14,requestObj.getAcqData2());
      cstmt.setString(15,requestObj.getAcqData3());
      cstmt.setString(16,requestObj.getAcqUsrId());
      cstmt.setString(17,requestObj.getRetreivalRefNum());

      //execute the query
      rs = cstmt.executeQuery();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query Executed..");
      if (rs.next()) {
        //get response code
        String respCode = rs.getString(1);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Code --> " + respCode);
        //set it in response obj
        respObj.setRespCode(respCode);
        //get the response desc
        String respDesc = rs.getString(2);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Desc --> " + respDesc);
        //set it in response obj
        respObj.setRespDesc(respDesc);
        //get the trans id
        String transId = rs.getString(3);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Id --> " + transId);
        //set it in response obj
        respObj.setTransId(transId);
        //get the card balance
        String cardBalance = rs.getString(4);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Card Balance --> " + cardBalance);
        //set it in response obj
        respObj.setCardBalance(cardBalance);
        //get the fee amount
        String feeAmount = rs.getString(5);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Fee Amount --> " + feeAmount);
        //set it in response obj
        respObj.setFeeAmount(feeAmount);
      } //end if

      //return the response
      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception --> " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      if (rs != null) rs.close();
      if (cstmt != null) cstmt.close();
    } //end finally
  } //end focePostTransaction

  /**
   * This method is used to reverse the acquirer charged fee. The method uses the database stored
   * procedure "REVERSE_ACQ_FEE" to perform the reveral of the Acquire fee. The service then builds the
   * response object and populates it with the response paramters returned from the database proceudre.
   * In case of any Exception the service throws exception which describes the cause of the error in
   * detail.
   * @param requestObj ServicesRequestObj -- Request Information
   * @throws Exception
   * @return ServicesResponseObj -- Response Information
   */
  public ServicesResponseObj reverseAcquirerFee(ServicesRequestObj requestObj) throws
      Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "<---Reverse Acquirer Fee --->");
    //make the services response obj
    ServicesResponseObj respObj = new ServicesResponseObj();
    //make the query
    String query = "Execute procedure reverse_acq_fee(" +
        "pserial=" + requestObj.getTransId() + "," +
        "pcard_no=" +
        (requestObj.getCardNo() != null ? "'" + requestObj.getCardNo() + "'" : null) +
        "," +
        "paccount_no=" +
        (requestObj.getAccountNo() != null ?
         "'" + requestObj.getAccountNo() + "'" : null) + "," +
        "pdesc=" +
        (requestObj.getDescription() != null ?
         "'" + requestObj.getDescription() + "'" : null) + ")";

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Query --> " + query);
    //create the statements
    CallableStatement cstmt = null;
    ResultSet rs = null;

    try {
      //create the statement
      cstmt = con.prepareCall(query);
      //execute the query
      rs = cstmt.executeQuery();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query Executed..");

      if (rs.next()) {
        //get the response code
        String respCode = rs.getString(1);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Code --> " + respCode);
        //put it in resp obj
        respObj.setRespCode(respCode);
        //get the response desc
        String respDesc = rs.getString(2);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response desc --> " + respDesc);
        //put it in response obj
        respObj.setRespDesc(respDesc);
        //get the card balance
        String balance = rs.getString(3);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Card Balance --> " + balance);
        //put it in response obj
        respObj.setCardBalance(balance);
        //get the trans id
        String transId = rs.getString(4);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Trans Id --> " + transId);
        //put it in response obj
        respObj.setTransId(transId);
        //get the amount reversed
        String amountRev = rs.getString(5);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Amount Reversed --> " + amountRev);
        //get business date
        String businessDate = rs.getString(6);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Business Date --> " + businessDate);
        if (businessDate != null) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Formatting date ...");
          //convert the date format
          String bdate = CommonUtilities.convertDateFormat(Constants.
              WEB_DATE_FORMAT, Constants.INFORMIX_DATE_FORMAT, businessDate);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG), "Date formatted --> " + bdate);
          //put it in response obj
          respObj.setBusinessDate(bdate);
        } //end if
      } //end if
      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception --> " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      if (rs != null) rs.close();
      if (cstmt != null) cstmt.close();
    } //end finally

  } //end reverseAcquirerFee

//  public ServicesResponseObj directDepositAtOltp(String cardNo, String amount, String serviceId, String ApplyFee)
//        throws Exception
//    {
//      ServicesResponseObj respObj = new ServicesResponseObj();
//
//      int applyFeeFlag = 0;
//      if(ApplyFee != null && ApplyFee.equals("Y")){
//        applyFeeFlag = 1;
//      }else{
//        applyFeeFlag = 0;
//      }
//      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//          LOG_CONFIG),"Going to direct_deposit...");
//      String query = "Execute procedure direct_deposit(" +
//          "pcard_no  = '" + cardNo + "'," +
//          "pservice_id  = '" + serviceId + "'," +
//          "pamount  = " + (amount != null ? "'" + amount + "'" : null) + "," +
//          "papply_fee  = " + applyFeeFlag + ")";
//      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//          LOG_CONFIG),"Executing Query -- > " + query);
//
//      CallableStatement cstmt = null;
//      ResultSet rs = null;
//      try {
//        //prepare the call
//        cstmt = con.prepareCall(query);
//        //execute the procedure
//        rs = cstmt.executeQuery();
//        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//            LOG_CONFIG),"Query Executed....Looking for values...");
//        if(rs.next())
//        {
//          String response = rs.getString(1);
//          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//              LOG_CONFIG),"Response Code -- > " + response);
//          //set in response object
//          respObj.setRespCode(response);
//          String desc = rs.getString(2);
//          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//              LOG_CONFIG),"Response Desc -- > " + desc);
//          //set in response obj
//          respObj.setRespDesc(desc);
//          String cardBalance = rs.getString(3);
//          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//              LOG_CONFIG),"Card Balance -- > " + cardBalance);
//          //set it in response
//          respObj.setCardBalance(cardBalance);
//
//          String isoSerial = rs.getString(4);
//          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//              LOG_CONFIG),"ISO Serial -- > " + isoSerial);
//          //set it in response
//          respObj.setTransId(isoSerial);
//        }//end if
//        else{
//          respObj.setRespCode("06");
//          respObj.setRespDesc("No Response Received from Direct Deposit DB API");
//        }
//        return respObj;
//      }//end try
//      catch (Exception ex) {
//        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//            LOG_SEVERE),"Exception -- > " + ex.getMessage());
//        throw ex;
//      }//end catch
//      finally {
//        try {
//          if(rs != null)
//            rs.close();
//            if(cstmt != null)
//              cstmt.close();
//        }//end try
//        catch (Exception ex) {}//end
//      }//end finally
//    }//end if

  ////////////////////// Follwoing Mehtod Is Added By Imtiaz ///////////////////////////////////

  /**
   * The method populates the Service Request Object with the account infromation retreived from the
   * database. The method uses the database table "ach-account" to get the account information and
   * populates the Service Request Object with information fetched from the database. In case of any
   * exception the method throws exception which describes the cause of the error in detail.
   * @param requestObj ServicesRequestObj
   * @throws Exception
   */

  public void loadAccountInfo(ServicesRequestObj requestObj) throws Exception {
    PreparedStatement stm = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "<--Excuting Method withdrawFundsWithAccountNo( ) of FinancialServiceHome class-->");

      String query = "select account_no, account_type, account_title, bank_name, routing_no from ach_accounts where ach_acct_sr_no= ?";

      stm = con.prepareStatement(query);
      stm.setString(1, requestObj.getAchAccountNo());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query For Getting The Account Information Is >> " +
                                      query);
      rs = stm.executeQuery();

      if (rs.next()) {
        String accountNo = rs.getString("account_no");
        if (accountNo != null && !accountNo.equals("") &&
            accountNo.trim().length() > 0)
          requestObj.setBankAcctNo(accountNo.trim());
        String accountType = rs.getString("account_type");
        if (accountType != null && !accountType.equals("") &&
            accountType.trim().length() > 0)
          requestObj.setBankAcctType(accountType.trim());
        String accountTitle = rs.getString("account_title");
        if (accountTitle != null && !accountTitle.equals("") &&
            accountTitle.trim().length() > 0)
          requestObj.setBankAcctTitle(accountTitle.trim());
        String bankName = rs.getString("bank_name");
        if (bankName != null && !bankName.equals("") &&
            bankName.trim().length() > 0)
          requestObj.setBankName(bankName.trim());
        String routingNo = rs.getString("routing_no");
        if (routingNo != null && !routingNo.equals("") &&
            routingNo.trim().length() > 0)
          requestObj.setBankRoutingNo(routingNo.trim());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Account Information >>>> " +
                                        "Account No --> " +
                                        requestObj.getBankAcctNo() +
                                        "<-- Account Type --> " +
                                        requestObj.getBankAcctType() +
                                        "<-- Account Title --> " +
                                        requestObj.getBankAcctTitle() +
                                        "<-- Bank Name --> " +
                                        requestObj.getBankName() +
                                        "<-- Routing No -->" +
                                        requestObj.getBankRoutingNo());
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "No Account Information Exist against the query >> " +
                                        query);
      }
    }
    catch (Exception exc) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception While Getting The Account Information >> " +
                                      exc);
      throw exc;
    }
    finally {
      if (rs != null)
        rs.close();
      if (stm != null)
        stm.close();
    }
  }



  /**
   * The method is used to validate the card information. It calls the method isCardInfoValid to
   * validates the card information and returns response object which describes the status of the
   * validation.
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
   * The method checks whether the record of the given card no exists in the database. It uses the
   * database table "cards" to find the record of the given card no. If the given card no. record
   * was not found in the database it logs the transaction and reutrns the response object with
   * response code "14" which indicate that the given card does not exists in the database.
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
            "Card No (" + cardNo + ") does not exist in the database::logging error transaction:: Code -- > " +
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
            LOG_CONFIG),
            "Transaction logged with ISO Serial No - > " + transIds[0] +
                           " && Trace Audit No -- > " + transIds[1]);
        //set the trans id in response
        respObj.setTransId(transIds[0]);
        return respObj;
      } //end if
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

  public ServicesResponseObj checkDebitAllowed(ServicesRequestObj requestObj, String skipStatus, String isOkOnNegBal, String chkTransAmt) throws Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();
    StringBuffer query = new StringBuffer();
    CallableStatement cs = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for checking Service fee Debit Allowed --- getCardNo--->" + requestObj.getCardNo()
                                      + "<---getServiceId--->" + requestObj.getServiceId()
                                      + "<---getAmount--->" + requestObj.getAmount()
                                      + "<---skipStatus--->" + skipStatus
                                      + "<---isOkOnNegBal--->" + isOkOnNegBal
                                      + "<---chkTransAmt--->" + chkTransAmt);

      query.append("Execute procedure check_service_fee(pcard_no = ?, pservice_id = ?, ptrans_amount = ?, pskip_status = ?, pchk_ok_on_neg_bal = ?, pchk_trans_amt = ?)");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Query for checking Debit Allowed--->" +
                                query);

      cs = con.prepareCall(query.toString());

      cs.setString(1,requestObj.getCardNo());
      cs.setString(2,requestObj.getServiceId());
      cs.setString(3,null);
      cs.setString(4,skipStatus);
      cs.setString(5,isOkOnNegBal);
      cs.setString(6,chkTransAmt);

      rs = cs.executeQuery();

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          respObj.setRespCode(rs.getString(1).trim());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          respObj.setRespDesc(rs.getString(2).trim());
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          respObj.setFeeAmount(rs.getString(3).trim());
        }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Received ---- Response Code--->" +
                                        respObj.getRespCode()
                                        + "<---Response Description--->" +
                                        respObj.getRespDesc()
                                        + "<---Debit Fee--->" +
                                        respObj.getFeeAmount()
                                        );
      }else{
        respObj.setRespCode("06");
        respObj.setRespDesc("No response recevied from DB API for cheking debit allowed");
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in checking Debit Allowed--->" +
                                      ex);
      throw ex;
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
      catch (SQLException ex1) {
      }
    }
    return respObj;
  }

  public String checkPurchaseOrderTransaction(String transID, boolean isISOSerial) throws Exception{
    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String cardNo = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Service for checking whether the transaction ID is of NEW_CARD_PO --->" +
                                      transID + "<--IS ISO Serial Flag-->" +
                                      isISOSerial);
      if(isISOSerial){
        query.append("select card_no from trans_requests where iso_serial_no = ? and trans_type = ? and response_code = '00' and iso_message_type = '0200'");
      }else{
        query.append("select card_no from trans_requests where retrieval_ref_no = ? and trans_type = ? and response_code = '00' and iso_message_type = '0200'");
      }
      pstmt = con.prepareStatement(query.toString());
      pstmt.setString(1,transID);
      pstmt.setString(2,"NC");

      rs = pstmt.executeQuery();
      if(rs.next()){
        cardNo = rs.getString(1);
        if(cardNo != null && cardNo.trim().length() > 0){
          return cardNo.trim();
        }
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Card No found--->" + cardNo);
      }else{
        return null;
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in checking whether the transaction ID is of NEW_CARD_PO-->" + ex);
      throw ex;
    }finally{
      if (rs != null) {
        rs.close();
        rs = null;
      }
      if (pstmt != null) {
        pstmt.close();
        pstmt = null;
      }
    }
    return cardNo;
  }

  public String getCardNumber(String transID, boolean isISOSerial) throws Exception{
    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String cardNo = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for getting card number for given the transaction ID --->" +
                                      transID + "<--IS ISO Serial Flag-->" +
                                      isISOSerial);
      if(isISOSerial){
        query.append("select card_no from trans_requests where iso_serial_no = ? ");
      }else{
        query.append("select card_no from trans_requests where retrieval_ref_no = ? ");
      }
      pstmt = con.prepareStatement(query.toString());
      pstmt.setString(1,transID);

      rs = pstmt.executeQuery();
      if(rs.next()){
        cardNo = rs.getString(1);
        if(cardNo != null && cardNo.trim().length() > 0){
          return cardNo.trim();
        }
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Card No found--->" + cardNo);
      }else{
        return null;
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting card number for the given transaction ID -->" + ex);
      throw ex;
    }finally{
      if (rs != null) {
        rs.close();
        rs = null;
      }
      if (pstmt != null) {
        pstmt.close();
        pstmt = null;
      }
    }
    return cardNo;
  }

  public ServicesResponseObj processChargeBackTransaction(ServicesRequestObj requestObj) throws Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;
   try {
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "<---- Method for proessing charge back --- getChargeBackCaseId--->" + requestObj.getChargeBackCaseId());

     query.append("execute procedure process_chargeback(pchargeback_case_id = ? )");
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Query --> " + query);

     //create the statement
     cstmt = con.prepareCall(query.toString());
     cstmt.setString(1, requestObj.getChargeBackCaseId());
     rs = cstmt.executeQuery();

     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Query Executed..");

     if (rs.next()) {
       String respCode = rs.getString(1);
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                       "Response Code --> " + respCode);
       respObj.setRespCode(respCode);

       String respDesc = rs.getString(2);
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                       "Response desc --> " + respDesc);
       respObj.setRespDesc(respDesc);
     }else{
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                       "No response received from DB API, returning 01(Refer to card issuer)...");
       respObj.setRespCode("01");
       respObj.setRespDesc("Refer to Card Issuer");
     }
     //return the response
     return respObj;
   }catch (Exception ex) {
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                     "Exception --> " + ex.getMessage());
     throw ex;
   }finally {
     if (rs != null){
       rs.close();
       rs = null;
     }
     if (cstmt != null){
       cstmt.close();
       cstmt = null;
     }
   } //end finally
  }

  public ServicesResponseObj confirmChargeBackTransaction(ServicesRequestObj requestObj) throws Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;
   try {
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "<---- Method for confirming charge back --- getChargeBackCaseId--->" + requestObj.getChargeBackCaseId()
                                     + "<--getChargeBackApprovedAmount-->" + requestObj.getChargeBackApprovedAmount()
                                     + "<--getChargeBackRemarks-->" + requestObj.getChargeBackRemarks()
                                     + "<--getChargeBackStatus-->" + requestObj.getChargeBackStatus());

     query.append("execute procedure confirm_chargeback(pchargeback_case_id = ?, pamount_accepted = ?, pchargeback_status = ?, pchargeback_remarks = ?)");
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Query --> " + query);

     //create the statement
     cstmt = con.prepareCall(query.toString());

     cstmt.setString(1, requestObj.getChargeBackCaseId());
     cstmt.setString(2, requestObj.getChargeBackApprovedAmount());
     cstmt.setString(3, requestObj.getChargeBackStatus());
     cstmt.setString(4, requestObj.getChargeBackRemarks());

     rs = cstmt.executeQuery();

     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "Query Executed..");

     if (rs.next()) {
       String respCode = rs.getString(1);
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                       "Response Code --> " + respCode);
       respObj.setRespCode(respCode);

       String respDesc = rs.getString(2);
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                       "Response desc --> " + respDesc);
       respObj.setRespDesc(respDesc);
     } //end if
     //return the response
     return respObj;
   }catch (Exception ex) {
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                     "Exception --> " + ex.getMessage());
     throw ex;
   }finally {
     if (rs != null){
       rs.close();
       rs = null;
     }
     if (cstmt != null){
       cstmt.close();
       cstmt = null;
     }
   } //end finally
  }


  public ServicesResponseObj processBillPaymentReveral(ServicesRequestObj requestObj) throws
        Exception {
      //make the response object
      ServicesResponseObj respObj = new ServicesResponseObj();
      StringBuffer query = new StringBuffer();

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for processing bill payment reversal... Attributes --- getTransId()" + requestObj.getTransId()
                                      + "<---getBillPaymentReversalType--->" + requestObj.getBillPaymentReversalType()
                                      + "<---getApplyFee--->" + requestObj.getApplyFee()
                                      + "<---getReverseTransFee--->" + requestObj.getReverseTransFee()
                                      + "<---getAmount--->" + requestObj.getAmount()
                                      + "<---getServiceId--->" + requestObj.getServiceId()
                                      + "<---getLocalDateTime--->" + requestObj.getLocalDateTime()
                                      + "<---retRefNo--->" + requestObj.getRetreivalRefNum()
                                      + "<---getDeviceType--->" + requestObj.getDeviceType()
                                      + "<---getDeviceId--->" + requestObj.getDeviceId()
                                      + "<---getCardAcceptorId--->" + requestObj.getCardAcceptorId()
                                      + "<---getCardAcceptNameAndLoc--->" + requestObj.getCardAcceptNameAndLoc()
                                      + "<---getMcc--->" + requestObj.getMcc()
                                      + "<---getAcquirerId--->" + requestObj.getAcquirerId()
                                      + "<---getAcqData1--->" + requestObj.getAcqData1()
                                      + "<---getAcqData2--->" + requestObj.getAcqData2()
                                      + "<---getAcqData3--->" + requestObj.getAcqData3()
                                      + "<---getAcqUsrId--->" + requestObj.getAcqUsrId()
                                      + "<---getBillPaymentTransactionSerial--->" + requestObj.getBillPaymentTransactionSerial());
      query.append("execute procedure decline_bp_request(ptype = ?,pserial_no = ?,papply_fee = ?,preverse_fee = ?,pamount = ?,pservice_id = ?,pdate = ?,pret_ref_no = ?,pdevice_type = ?,pdevice_id = ?,pcrd_acpt_id = ?,pcrd_acpt_name_loc = ?,pmerchant_ccd = ?,pacq_id = ?,psub_srv = ?,pdata_2 = ?,pdata_3 = ?,pacq_userid = ?,ptrans_id = ?)");
      CallableStatement cstmt = null;
      ResultSet rs = null;
      try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Creating Statement...");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Executing Query--> " + query);
        //prepare the statement
        cstmt = con.prepareCall(query.toString());
        cstmt.setString(1,requestObj.getBillPaymentReversalType());
        cstmt.setString(2,requestObj.getTransId());
        cstmt.setString(3,requestObj.getApplyFee());
        cstmt.setString(4,requestObj.getReverseTransFee());
        cstmt.setString(5,requestObj.getAmount());
        cstmt.setString(6,requestObj.getServiceId());
        cstmt.setString(7,requestObj.getLocalDateTime());
        cstmt.setString(8,requestObj.getRetreivalRefNum());
        cstmt.setString(9,requestObj.getDeviceType());
        cstmt.setString(10,requestObj.getDeviceId());
        cstmt.setString(11,requestObj.getCardAcceptorId());
        cstmt.setString(12,requestObj.getCardAcceptNameAndLoc());
        cstmt.setString(13,requestObj.getMcc());
        cstmt.setString(14,requestObj.getAcquirerId());
        cstmt.setString(15,requestObj.getAcqData1());
        cstmt.setString(16,requestObj.getAcqData2());
        cstmt.setString(17,requestObj.getAcqData3());
        cstmt.setString(18,requestObj.getAcqUsrId());
        cstmt.setString(19,requestObj.getBillPaymentTransactionSerial());
        //execute the query
        rs = cstmt.executeQuery();
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Query executed..");

        if (rs.next()) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Gettng result from procedure...");
          //get the transaction id
          String transId = rs.getString(1);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Trans Id--> " + transId);
          //get the response code
          String code = rs.getString(2);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Response Code--> " + code);
          //get the response description
          String desc = rs.getString(3);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Response Desc--> " + desc);
          //get the amount
          String balance = rs.getString(4);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Card Balance--> " + balance);
          //get the fee amount
          String feeAmount = rs.getString(5);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Fee Amount--> " + feeAmount);
          //get the business date
          String businessDate = rs.getString(6);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Business date--> " + businessDate);

          //set these values in response
          respObj.setRespCode(code);
          respObj.setRespDesc(desc);
          respObj.setCardBalance(balance);
          respObj.setFeeAmount(feeAmount);
          respObj.setTransId(transId);
          if (businessDate != null && !businessDate.trim().equals("")){
              respObj.setBusinessDate(CommonUtilities.convertDateFormat(
                      Constants.
                      WEB_DATE_FORMAT, Constants.INFORMIX_DATE_FORMAT,
                      businessDate));
          }
        }else{
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "No return value got from OLTP API for processing Bill Payment reversal...");
            throw new Exception("Empty result returned by OLTP API for processing bill payment reversal");
        }
        return respObj;
      }catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                        "Exception in processing bill payment reversal--> " +
                                        ex);
        throw ex;
      }finally {
        if (rs != null) rs.close();
        if (cstmt != null) cstmt.close();
      } //end finally
  } //end confirmReversal

  public long logBillPaymentRequest(ServicesRequestObj requestObj) throws Exception{
      StringBuffer query = new StringBuffer();
      StringBuffer insertColummsQuery = new StringBuffer();
      StringBuffer insertValuesQuery = new StringBuffer();
      Properties data = new Properties();
      PreparedStatement pstmt = null;
      long serial = -1;
      String currDate = null;
    try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                              " Method for logging bill payment request, Request information received...");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "<---getCardNo---> " + requestObj.getCardNo()
                                        + "<---getBillPaymentPayeeSerialNo---> " +
                                        requestObj.getBillPaymentPayeeSerialNo()
                                        + "<---getBillPaymentPayeeID---> " +
                                        requestObj.getBillPaymentPayeeID()
                                        + "<---getBillPaymentConsumerAccountNo---> " +
                                        requestObj.getBillPaymentConsumerAccountNo()
                                        + "<---getBillPaymentAmount---> " +
                                        requestObj.getBillPaymentAmount()
                                        + "<---getBillPaymentDate---> " +
                                        requestObj.getBillPaymentDate()
                                        + "<---getRetreivalRefNum---> " +
                                        requestObj.getRetreivalRefNum()
                                        + "<---getBillPaymentPayeeName---> " +
                                        requestObj.getBillPaymentPayeeName()
                                        + "<---getBillPaymentPayeeCID---> " +
                                        requestObj.getBillPaymentPayeeCID()
                                        + "<---getBillPaymentPayeeStreet1---> " +
                                        requestObj.getBillPaymentPayeeStreet1()
                                        + "<---getBillPaymentPayeeStreet2---> " +
                                        requestObj.getBillPaymentPayeeStreet2()
                                        + "<---getBillPaymentPayeeStreet3---> " +
                                        requestObj.getBillPaymentPayeeStreet3()
                                        + "<---getBillPaymentPayeeStreet4---> " +
                                        requestObj.getBillPaymentPayeeStreet4()
                                        + "<---getBillPaymentPayeeCity---> " +
                                        requestObj.getBillPaymentPayeeCity()
                                        + "<---getBillPaymentPayeeState---> " +
                                        requestObj.getBillPaymentPayeeState()
                                        + "<---getBillPaymentPayeeZIP---> " +
                                        requestObj.getBillPaymentPayeeZIP()
                                        + "<---getBillPaymentPayeeCountry---> " +
                                        requestObj.getBillPaymentPayeeCountry()
                                        + "<---getBillPaymentPayerNo---> " +
                                        requestObj.getBillPaymentPayerNo()
                                        + "<---getBillPaymentPayerName---> " +
                                        requestObj.getBillPaymentPayerName()
                                        + "<---getBillPaymentPayerAddress1---> " +
                                        requestObj.getBillPaymentPayerAddress1()
                                        + "<---getBillPaymentPayerAddress2---> " +
                                        requestObj.getBillPaymentPayerAddress2()
                                        + "<---getBillPaymentPayerCity---> " +
                                        requestObj.getBillPaymentPayerCity()
                                        + "<---getBillPaymentPayerState---> " +
                                        requestObj.getBillPaymentPayerState()
                                        + "<---getBillPaymentPayerZIP---> " +
                                        requestObj.getBillPaymentPayerZIP()
                                        + "<---getBillPaymentPayerCountry---> " +
                                        requestObj.getBillPaymentPayerCountry()
                                        + "<---getBillPaymentPayeeUserData1---> " +
                                        requestObj.getBillPaymentPayeeUserData1()
                                        + "<---getBillPaymentPayeeUserData2---> " +
                                        requestObj.getBillPaymentPayeeUserData2()
                                        + "<---getBillPaymentPayeeUserData3---> " +
                                        requestObj.getBillPaymentPayeeUserData3()
                                        + "<---getBillPaymentPayeeUserData4---> " +
                                        requestObj.getBillPaymentPayeeUserData4()
                                        + "<---getBillPaymentPayeeUserData5---> " +
                                        requestObj.getBillPaymentPayeeUserData5()
                                        + "<---getBillPaymentPayeeUserData6---> " +
                                        requestObj.getBillPaymentPayeeUserData6()
                                        + "<---getDescription---> " +
                                        requestObj.getDescription()
                                        + "<---getDeviceType---> " +
                                        requestObj.getDeviceType()
                                        + "<--- BILL_PAY_STATUS_SCHD---> " +
                                         Constants.BILL_PAY_STATUS_SCHD
                                        + "<---getBillPaymentBillType---> " +
                                        requestObj.getBillPaymentBillType()
                                        + "<---getCardHolderId---> " +
                                        requestObj.getCardHolderId()
                                        + "<---getBillPaymentProcessorId---> " +
                                        requestObj.getBillPaymentProcessorId()
                                        + "<---getAcquirerId---> " +
                                        requestObj.getAcquirerId()
                                        + "<---getCardAcceptorId---> " +
                                        requestObj.getCardAcceptorId()
                                        + "<--- getCardAcceptNameAndLoc---> " +
                                        requestObj.getCardAcceptNameAndLoc()
                                        + "<---getMcc---> " +
                                        requestObj.getMcc()
                                        + "<---getDeviceId---> " +
                                        requestObj.getDeviceId()
                                        + "<---getApplyFee---> " +
                                        requestObj.getApplyFee()
                                        + "<---getCardHolderId---> " +
                                        requestObj.getCardHolderId()
                                        + "<---getBillPaymentAlertType---> " +
                                        requestObj.getBillPaymentAlertType()
                                        + "<---getBillPaymentAlertUserNo---> " +
                                        requestObj.getBillPaymentAlertUserNo());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        " Method for logging bill payment request, Getting Payer Data for card--->" + requestObj.getCardNo());
        getPayerData(requestObj);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      " Method for logging bill payment request, Making insert query...");
        if (requestObj.getCardNo() != null &&
            requestObj.getCardNo().trim().length() > 0) {
            data.setProperty("card_no", requestObj.getCardNo().trim());
        }
        if (requestObj.getBillPaymentPayeeSerialNo() != null &&
            requestObj.getBillPaymentPayeeSerialNo().trim().length() > 0) {
            data.setProperty("payee_sno",
                             requestObj.getBillPaymentPayeeSerialNo().trim());
        }
        if (requestObj.getBillPaymentPayeeID() != null &&
            requestObj.getBillPaymentPayeeID().trim().length() > 0) {
            data.setProperty("payee_id",
                             requestObj.getBillPaymentPayeeID().trim());
        }
        if (requestObj.getBillPaymentConsumerAccountNo() != null &&
            requestObj.getBillPaymentConsumerAccountNo().trim().length() > 0) {
            data.setProperty("consumer_acct_no",
                             requestObj.getBillPaymentConsumerAccountNo().trim());
        }
        if (requestObj.getBillPaymentAmount() != null &&
            requestObj.getBillPaymentAmount().trim().length() > 0) {
            data.setProperty("amount", requestObj.getBillPaymentAmount().trim());
        }
        if (requestObj.getBillPaymentDate() != null &&
            requestObj.getBillPaymentDate().trim().length() > 0) {
            String date = CommonUtilities.convertDateFormat(Constants.
                    DATE_FORMAT, Constants.WEB_DATE_FORMAT,
                          requestObj.getBillPaymentDate());
            data.setProperty("payment_schd_date", date);
        }
        if (requestObj.getRetreivalRefNum() != null &&
            requestObj.getRetreivalRefNum().trim().length() > 0) {
            data.setProperty("retr_ref_no",
                             requestObj.getRetreivalRefNum().trim());
        }
        if (requestObj.getBillPaymentPayeeName() != null &&
            requestObj.getBillPaymentPayeeName().trim().length() > 0) {
            data.setProperty("payee_name",
                             requestObj.getBillPaymentPayeeName().trim());
        }
        if (requestObj.getBillPaymentPayeeCID() != null &&
            requestObj.getBillPaymentPayeeCID().trim().length() > 0) {
            data.setProperty("payee_cid",
                             requestObj.getBillPaymentPayeeCID().trim());
        }
        if (requestObj.getBillPaymentPayeeStreet1() != null &&
            requestObj.getBillPaymentPayeeStreet1().trim().length() > 0) {
            data.setProperty("street1",
                             requestObj.getBillPaymentPayeeStreet1().trim());
        }
        if (requestObj.getBillPaymentPayeeStreet2() != null &&
            requestObj.getBillPaymentPayeeStreet2().trim().length() > 0) {
            data.setProperty("street2",
                             requestObj.getBillPaymentPayeeStreet2().trim());
        }

        if (requestObj.getBillPaymentPayeeStreet3() != null &&
            requestObj.getBillPaymentPayeeStreet3().trim().length() > 0) {
            data.setProperty("street3",
                             requestObj.getBillPaymentPayeeStreet3().trim());
        }

        if (requestObj.getBillPaymentPayeeStreet4() != null &&
            requestObj.getBillPaymentPayeeStreet4().trim().length() > 0) {
            data.setProperty("street4",
                             requestObj.getBillPaymentPayeeStreet4().trim());
        }

        if (requestObj.getBillPaymentPayeeCity() != null &&
            requestObj.getBillPaymentPayeeCity().trim().length() > 0) {
            data.setProperty("city", requestObj.getBillPaymentPayeeCity().trim());
        }
        if (requestObj.getBillPaymentPayeeState() != null &&
            requestObj.getBillPaymentPayeeState().trim().length() > 0) {
            data.setProperty("state",
                             requestObj.getBillPaymentPayeeState().trim());
        }
        if (requestObj.getBillPaymentPayeeZIP() != null &&
            requestObj.getBillPaymentPayeeZIP().trim().length() > 0) {
            data.setProperty("zip_postal_code",
                             requestObj.getBillPaymentPayeeZIP().trim());
        }
        if (requestObj.getBillPaymentPayeeCountry() != null &&
            requestObj.getBillPaymentPayeeCountry().trim().length() > 0) {
            data.setProperty("payee_country",
                             requestObj.getBillPaymentPayeeCountry().trim());
        }
        if (requestObj.getBillPaymentPayerNo() != null &&
            requestObj.getBillPaymentPayerNo().trim().length() > 0) {
            data.setProperty("payer_no",
                             requestObj.getBillPaymentPayerNo().trim());
        }
        if (requestObj.getBillPaymentPayerName() != null &&
            requestObj.getBillPaymentPayerName().trim().length() > 0) {
            data.setProperty("payer_name",
                             requestObj.getBillPaymentPayerName().trim());
        }
        if (requestObj.getBillPaymentPayerAddress1() != null &&
            requestObj.getBillPaymentPayerAddress1().trim().length() > 0) {
            data.setProperty("payer_address1",
                             requestObj.getBillPaymentPayerAddress1().trim());
        }
        if (requestObj.getBillPaymentPayerAddress2() != null &&
            requestObj.getBillPaymentPayerAddress2().trim().length() > 0) {
            data.setProperty("payer_address2",
                             requestObj.getBillPaymentPayerAddress2().trim());
        }
        if (requestObj.getBillPaymentPayerCity() != null &&
            requestObj.getBillPaymentPayerCity().trim().length() > 0) {
            data.setProperty("payer_city",
                             requestObj.getBillPaymentPayerCity().trim());
        }
        if (requestObj.getBillPaymentPayerState() != null &&
            requestObj.getBillPaymentPayerState().trim().length() > 0) {
            data.setProperty("payer_state",
                             requestObj.getBillPaymentPayerState().trim());
        }
        if (requestObj.getBillPaymentPayerZIP() != null &&
            requestObj.getBillPaymentPayerZIP().trim().length() > 0) {
            data.setProperty("payer_postal_code",
                             requestObj.getBillPaymentPayerZIP().trim());
        }
        if (requestObj.getBillPaymentPayerCountry() != null &&
            requestObj.getBillPaymentPayerCountry().trim().length() > 0) {
            data.setProperty("payer_country",
                             requestObj.getBillPaymentPayerCountry().trim());
        }
        if (requestObj.getBillPaymentPayeeUserData1() != null &&
            requestObj.getBillPaymentPayeeUserData1().trim().length() > 0) {
            data.setProperty("user_defined_1",
                             requestObj.getBillPaymentPayeeUserData1().trim());
        }
        if (requestObj.getBillPaymentPayeeUserData2() != null &&
            requestObj.getBillPaymentPayeeUserData2().trim().length() > 0) {
            data.setProperty("user_defined_2",
                             requestObj.getBillPaymentPayeeUserData2().trim());
        }
        if (requestObj.getBillPaymentPayeeUserData3() != null &&
            requestObj.getBillPaymentPayeeUserData3().trim().length() > 0) {
            data.setProperty("user_defined_3",
                             requestObj.getBillPaymentPayeeUserData3().trim());
        }
        if (requestObj.getBillPaymentPayeeUserData4() != null &&
            requestObj.getBillPaymentPayeeUserData4().trim().length() > 0) {
            data.setProperty("user_defined_4",
                             requestObj.getBillPaymentPayeeUserData4().trim());
        }
        if (requestObj.getBillPaymentPayeeUserData5() != null &&
            requestObj.getBillPaymentPayeeUserData5().trim().length() > 0) {
            data.setProperty("user_defined_5",
                             requestObj.getBillPaymentPayeeUserData5().trim());
        }
        if (requestObj.getBillPaymentPayeeUserData6() != null &&
            requestObj.getBillPaymentPayeeUserData6().trim().length() > 0) {
            data.setProperty("user_defined_6",
                             requestObj.getBillPaymentPayeeUserData6().trim());
        }
        if (requestObj.getDescription() != null &&
            requestObj.getDescription().trim().length() > 0) {
            data.setProperty("comments", requestObj.getDescription().trim());
        }
        if (requestObj.getDeviceType() != null &&
            requestObj.getDeviceType().trim().length() > 0) {
            data.setProperty("device_type", requestObj.getDeviceType().trim());
        }
        if (requestObj.getAcquirerId() != null &&
            requestObj.getAcquirerId().trim().length() > 0) {
            data.setProperty("acquirer_id",
                             requestObj.getAcquirerId().trim());
        }
        if (requestObj.getCardAcceptorId() != null &&
            requestObj.getCardAcceptorId().trim().length() > 0) {
            data.setProperty("card_acceptor_id",
                             requestObj.getCardAcceptorId().trim());
        }
        if (requestObj.getCardAcceptNameAndLoc() != null &&
            requestObj.getCardAcceptNameAndLoc().trim().length() > 0) {
            data.setProperty("card_aceptor_name",
                             requestObj.getCardAcceptNameAndLoc().trim());
        }
        if (requestObj.getDeviceId() != null &&
            requestObj.getDeviceId().trim().length() > 0) {
            data.setProperty("device_id",
                             requestObj.getDeviceId().trim());
        }
        if (requestObj.getMcc() != null &&
            requestObj.getMcc().trim().length() > 0) {
            data.setProperty("mcc", requestObj.getMcc().trim());
        }
        if (requestObj.getApplyFee() != null &&
            requestObj.getApplyFee().trim().length() > 0) {
            data.setProperty("apply_fee", requestObj.getApplyFee().trim());
        }
        if (requestObj.getBillPaymentAlertType() != null &&
            requestObj.getBillPaymentAlertType().trim().length() > 0) {
            data.setProperty("alert_flag", requestObj.getBillPaymentAlertType().trim());
        }
        if (requestObj.getBillPaymentAlertUserNo() != null &&
           requestObj.getBillPaymentAlertUserNo().trim().length() > 0) {
           data.setProperty("user_alert_no", requestObj.getBillPaymentAlertUserNo().trim());
       }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      " Method for logging bill payment request, Hastable size--->" + data.size());
        if (data != null && data.size() > 0) {
            if(requestObj.getCardHolderId() == null || requestObj.getCardHolderId().trim().length() == 0){
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                                " Method for logging bill payment request, Getting Cardholder Id ...");
                String chId = getCardHolderId(requestObj.getCardNo());
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                                " Method for logging bill payment request, Got Cardholder Id--->" +
                                                chId);
                if (chId != null) {
                    requestObj.setCardHolderId(chId);
                } else {
                    requestObj.setCardHolderId(requestObj.getCardNo());
                }
            }
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                          " Method for logging bill payment request, Getting processor ID for provided payee data ...");
            String processorId = getProcessorIdByPayee(requestObj.getBillPaymentPayeeSerialNo(),requestObj.getBillPaymentPayeeID(),requestObj.getBillPaymentPayeeName());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                            " Method for logging bill payment request, Got processor ID--->" + processorId);
            if(processorId != null){
                requestObj.setBillPaymentProcessorId(processorId);
            }else{
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                                " Method for logging bill payment request, No processor got for provided payee,Getting default processor ID ...");
                processorId = getDefaultProcessorId();
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                                " Method for logging bill payment request, No processor got for provided payee,Got default processor ID--->" + processorId);
                requestObj.setBillPaymentProcessorId(processorId);
            }
            data.setProperty("status_id", Constants.BILL_PAY_STATUS_SCHD);
            data.setProperty("bill_type", requestObj.getBillPaymentBillType());
            data.setProperty("ch_id", requestObj.getCardHolderId());
            data.setProperty("processor_id", requestObj.getBillPaymentProcessorId());

            currDate = CommonUtilities.getCurrentFormatDate(Constants.DATE_TIME_FORMAT);

            data.setProperty("log_dtime", currDate);

            insertColummsQuery.append("insert into bp_requests (");
            insertValuesQuery.append(" values (");
            Enumeration columns = data.propertyNames();

            while (columns.hasMoreElements()) {
                insertColummsQuery.append(columns.nextElement() + ",");
                insertValuesQuery.append(" ?,");
            }
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      " Method for logging bill payment request, insert query, columns part--->" + insertColummsQuery);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                          " Method for logging bill payment request, insert query, values part--->" + insertValuesQuery);
            insertColummsQuery.deleteCharAt(insertColummsQuery.length() - 1);
            insertValuesQuery.deleteCharAt(insertValuesQuery.length() - 1);
            insertColummsQuery.append(")");
            insertValuesQuery.append(")");
            query.append(insertColummsQuery);
            query.append(insertValuesQuery);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
              " Method for logging bill payment request, complete insert query--->" + query);
            pstmt = con.prepareStatement(query.toString());
            int index = 1;
            columns = data.propertyNames();
            while (columns.hasMoreElements()) {
                String column = columns.nextElement().toString();
                String value = data.getProperty(column);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Column--->" + column + "<--Value-->" + value);
                pstmt.setString(index++, value);
            }
            pstmt.executeUpdate();
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                            " Method for logging bill payment request, insert query executed, getting serial...");
            serial = getSerial(query);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                            " Method for logging bill payment request, insert query executed, got serial--->" + serial);
        }
    } catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                              " Exception in logging bill payment request--->" + ex);
        throw ex;
    } finally{
        if(pstmt != null){
            pstmt.close();
            pstmt = null;
        }
    }
    return serial;
  }



  private String getProcessorIdByPayee(String payeeSr,String payeeId,String payeeName) throws Exception{
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      Vector searchList = new Vector();
    try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        " Method for getting processor Id by Payee data, Payee Serial No---> " +
                                        payeeSr
                                        + "<---Payee Id--->" + payeeId
                                        + "<---Payee Name--->" + payeeName);
        query.append("select processor_id from bp_payees ");
        if (payeeSr != null || payeeId != null || payeeName != null) {
            query.append("where");
            if (payeeSr != null) {
                query.append(" payee_sno = ? and ");
                searchList.add(payeeSr);
            }
            if (payeeId != null) {
                query.append(" payee_id = ? and ");
                searchList.add(payeeId);
            }
            if (payeeName != null) {
                query.append(" payee_name = ? and ");
                searchList.add(payeeName);
            }
            query.delete(query.length() - 4, query.length());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            " Query for getting processor Id by Payee data--->" +
                                            query);
            pstmt = con.prepareStatement(query.toString());

            for(int i=0; i<searchList.size();){
                String value = searchList.elementAt(i).toString();
                pstmt.setString(++i,value);
            }
            rs = pstmt.executeQuery();
            if(rs.next()){
                String processorId = rs.getString(1);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                " Result got--->" +
                                                processorId);
                if(processorId != null && processorId.trim().length() > 0){
                    return processorId.trim();
                }
            }
        }
    } catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            " Exception in getting processor Id by Payee data--->" +
                                            ex);
        throw ex;
    }finally{
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

  private String getDefaultProcessorId() throws Exception {
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for getting default processor Id ");
          query.append(
                  "select processor_id from bp_processors order by processor_id");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Query for getting default processor Id--->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          rs = pstmt.executeQuery();
          if (rs.next()) {
              String processorId = rs.getString(1);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Result got--->" +
                                              processorId);
              if (processorId != null && processorId.trim().length() > 0) {
                  return processorId.trim();
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in getting default processor Id--->" +
                                          ex);
          throw ex;
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

  public void updateBPRequestStatus(long serialNo,String status,String isoSerialNo) throws Exception{
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for updating bill payment request with attributes, Status--->" + status
                  + "<---ISO Serial No--->" + isoSerialNo
                  + "<---BP Request Serial--->" + serialNo);
          query.append("update bp_requests set status_id = ?, trace_audit_no = ? where trans_id = ?");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Query for updating bill payment request with attributes--->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());

          pstmt.setString(1,status);
          pstmt.setString(2,isoSerialNo);
          pstmt.setLong(3,serialNo);

          pstmt.executeUpdate();
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in updating bill payment request with attributes--->" +
                                          ex);
          throw ex;
      } finally {
          try {
              if (pstmt != null) {
                  pstmt.close();
                  pstmt = null;
              }
          } catch (SQLException ex1) {
          }
      }
  }

  public void updateBPRequestStatus(long serialNo, String status,
                                     String isoSerialNo,String desc) throws Exception {
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for updating bill payment request with attributes, Status--->" +
                                          status
                                          + "<---ISO Serial No--->" +
                                          isoSerialNo
                                          + "<---BP Request Serial--->" +
                                          serialNo
                                          + "<---Description--->" + desc);
          query.append(
                  "update bp_requests set status_id = ?, trace_audit_no = ?, comments = ? where trans_id = ?");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Query for updating bill payment request with attributes--->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());

          pstmt.setString(1, status);
          pstmt.setString(2, isoSerialNo);
          pstmt.setString(3, desc);
          pstmt.setLong(4, serialNo);
          pstmt.executeUpdate();
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in updating bill payment request with attributes--->" +
                                          ex);
          throw ex;
      } finally {
          try {
              if (pstmt != null) {
                  pstmt.close();
                  pstmt = null;
              }
          } catch (SQLException ex1) {
          }
      }
  }

  private boolean findPayeeBySrNo(String payeeSr) throws Exception{
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for getting payee data for Payee Serial No---> " +
                                          payeeSr);
          query.append("select payee_id from bp_payees where payee_sno = ? ");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Query for getting Payee data--->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1, payeeSr);
          rs = pstmt.executeQuery();
          if (rs.next()) {
              String id = rs.getString(1);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Result got--->" +
                                              id);
              if (id != null && id.trim().length() > 0) {
                  return true;
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in getting Payee data--->" +
                                          ex);
          throw ex;
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
      return false;
  }

  private boolean findPayeeById(String payeeId)throws Exception {
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for getting payee data for Payee Id---> " +
                                          payeeId);
          query.append("select payee_id from bp_payees where payee_id = ? ");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Query for getting Payee data--->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1, payeeId);
          rs = pstmt.executeQuery();
          if (rs.next()) {
              String id = rs.getString(1);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Result got--->" +
                                              id);
              if (id != null && id.trim().length() > 0) {
                  return true;
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in getting Payee data--->" +
                                          ex);
          throw ex;
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
      return false;
  }

  private Vector findPayeeByName(String payeeName) throws Exception{
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      Vector payees = new Vector();
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for getting payee by Name---> " +
                                          payeeName);
          query.append("select payee_sno from bp_payees where payee_name = ? and payee_id is not null");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Query for getting payee by Name--->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1,payeeName);
          rs = pstmt.executeQuery();
          while (rs.next()) {
              String payeeSrNo = rs.getString(1);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " payeeSrNo--->" + payeeSrNo);
              if (payeeSrNo != null && payeeSrNo.trim().length() > 0) {
                  payees.add(payeeSrNo.trim());
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in getting payee by name--->" +
                                          ex);
          throw ex;
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
      return payees;
  }

  private BPChildPayeeObj getPayeeData(String payeeSrNo) throws Exception {
      StringBuffer query = new StringBuffer();
      BPChildPayeeObj payeeData = null;
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for getting payee Data for Payee Sr No---> " +
                                          payeeSrNo);
          query.append("select street1,city,state,zip_postal_code from bp_payee_addrs where payee_sno = ?");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Query for getting Payee Data--->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1, payeeSrNo);
          rs = pstmt.executeQuery();
          if (rs.next()) {
              payeeData = new BPChildPayeeObj();
              String address = rs.getString(1);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Payee Address--->" +
                                              address);
              if (address != null && address.trim().length() > 0) {
                  payeeData.setStreet1(address);
              }
              String city = rs.getString(2);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Payee City--->" +
                                              city);
              if (city != null && city.trim().length() > 0) {
                  payeeData.setCity(city);
              }
              String state = rs.getString(3);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Payee State--->" +
                                              state);
              if (state != null && state.trim().length() > 0) {
                  payeeData.setState(state);
              }
              String zip = rs.getString(4);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Payee Zip--->" +
                                              zip);
              if (zip != null && zip.trim().length() > 0) {
                  payeeData.setZipCode(zip);
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in getting Payee Data--->" +
                                          ex);
          throw ex;
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
      return payeeData;
  }

  private String getPayeeId(String payeeSrNo) throws Exception {
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for getting payee Id for Payee Sr No---> " +
                                          payeeSrNo);
          query.append("select payee_id from bp_payees where payee_sno = ? ");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Query for getting Payee Id--->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1, payeeSrNo);
          rs = pstmt.executeQuery();
          if (rs.next()) {
              String Id = rs.getString(1);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Result got--->" +
                                              Id);
              if (Id != null && Id.trim().length() > 0) {
                  return Id;
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in getting Payee Id--->" +
                                          ex);
          throw ex;
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

  public boolean checkPayeeActiveBySerialNo(String payeeSrNo) throws Exception {
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for checking payee is active---> " +
                                          payeeSrNo);
          query.append("select is_active from bp_payees where payee_sno = ? ");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Query for checking payee is active--->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1, payeeSrNo);
          rs = pstmt.executeQuery();
          if (rs.next()) {
              String isActive = rs.getString(1);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Result got--->" +
                                              isActive);
              if (isActive != null && isActive.trim().equalsIgnoreCase(Constants.NO_OPTION)) {
                   return false;
               }else{
                   return true;
               }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in checking payee is active--->" +
                                          ex);
          throw ex;
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
      return false;
  }

  public boolean checkPayeeActiveById(String payeeId) throws Exception {
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for checking payee is active---> " +
                                          payeeId);
          query.append("select is_active from bp_payees where payee_id = ? ");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Query for checking payee is active--->" +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1, payeeId);
          rs = pstmt.executeQuery();
          if (rs.next()) {
              String isActive = rs.getString(1);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Result got--->" +
                                              isActive);
              if (isActive != null && isActive.trim().equalsIgnoreCase(Constants.NO_OPTION)) {
                  return false;
              }else{
                  return true;
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in checking payee is active--->" +
                                          ex);
          throw ex;
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
      return false;
  }

  private Vector getChildPayees(Vector payeeSrNos,String city,String state) throws Exception{
      StringBuffer payeeSrWhereClause = new StringBuffer();
      StringBuffer query = new StringBuffer();
      BPChildPayeeObj childRecord = null;
      Vector childPayees = new Vector();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      int totalPayess = 0;
    try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        " Method for getting child payees --- city---> " +
                                        city
                                        + "<---state--->" + state
                                        + "<---payee sr size--->" +
                                        payeeSrNos.size());
        totalPayess = payeeSrNos.size();
        if (totalPayess > 0) {
            payeeSrWhereClause.append("(");
            for (int i = 0; i < totalPayess; i++) {
                payeeSrWhereClause.append("?,");
            }
            payeeSrWhereClause.deleteCharAt(payeeSrWhereClause.length() - 1);
            payeeSrWhereClause.append(")");
            query.append("select street1,zip_postal_code from bp_payee_addrs where city = ? and state = ? and payee_sno in ");
            query.append(payeeSrWhereClause);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        " query for getting child payees --- city---> " + query);
            pstmt = con.prepareStatement(query.toString());

            int index = 1;

            pstmt.setString(index++,city);
            pstmt.setString(index++,state);

            for (int i = 0; i < totalPayess; i++) {
                String value = payeeSrNos.elementAt(i).toString();
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"<--Payee Sr No-->" + value);
                pstmt.setString(index++,value);
            }

            rs = pstmt.executeQuery();

            while(rs.next()){
                childRecord = new BPChildPayeeObj();
                String street1 = rs.getString(1);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                                                " Street 1--->" + street1);
                if(street1 != null && street1.trim().length() > 0){
                    childRecord.setStreet1(street1);
                }
                String zip = rs.getString(2);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                " zip--->" + zip);
                if(zip != null && zip.trim().length() > 0){
                    childRecord.setZipCode(zip);
                }
                childPayees.add(childRecord);
            }
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            " Total child payees got--->" + childPayees.size());
        }
    } catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        " Exception in getting child payees --->" + ex);
        throw ex;
    }finally {
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
      return childPayees;
  }

  private boolean matchChildPayee(ServicesRequestObj requestObj,BPChildPayeeObj dbChildPayeeData) throws Exception{
      String respCode = null;
      boolean type = false;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for matching child payee data provided in request with data fetched from database...");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Request Child Payee Data --- Street1--->" +
                                          requestObj.getBillPaymentPayeeStreet1()
                                          + "<---ZIP--->" +
                                          requestObj.getBillPaymentPayeeZIP());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Database Child Payee Data --- Street1--->" +
                                          dbChildPayeeData.getStreet1()
                                          + "<---ZIP--->" +
                                          dbChildPayeeData.getZipCode());
          if (requestObj.getBillPaymentPayeeStreet1() != null
              && requestObj.getBillPaymentPayeeStreet1().trim().length() > 0
              && dbChildPayeeData.getStreet1() != null
              && dbChildPayeeData.getStreet1().trim().length() > 0
              && requestObj.getBillPaymentPayeeZIP() != null
              && requestObj.getBillPaymentPayeeZIP().trim().length() > 0
              && dbChildPayeeData.getZipCode() != null
              && dbChildPayeeData.getZipCode().trim().length() > 0
                  ) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                              " Caling API for matching address...");
              respCode = matchAddress(requestObj.getBillPaymentPayeeStreet1(),requestObj.getBillPaymentPayeeZIP(),dbChildPayeeData.getStreet1(),dbChildPayeeData.getZipCode());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Response got--->" + respCode);
              if(respCode != null){
                  if(respCode.equalsIgnoreCase(Constants.ALL_MTCH) || respCode.equalsIgnoreCase(Constants.BOTH_MTCH_ZIP9DGT)){
                      return true;
                  }else{
                      return type;
                  }
              }else{
                  return type;
              }
          } else {
              return type;
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_WARNING),
                                          " Exception in matching child payee date--->" + ex);
          throw ex;
      }
  }

  private String matchAddress(String requestAddress,String requestZip,String dbAddress,String dbZip) throws Exception{
      StringBuffer query = new StringBuffer();
      CallableStatement cstmt = null;
      ResultSet rs = null;

    try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        " Method for matching address & ZIP using DB API...");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        " Request Data --- Street1--->" +
                                        requestAddress
                                        + "<---ZIP--->" +
                                        requestZip);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        " Database Data --- Street1--->" +
                                        dbAddress
                                        + "<---ZIP--->" +
                                        dbZip);
        query.append("execute procedure match_address (p_msg_address = ?, p_msg_zip_code = ?,p_card_address = ?,p_cards_zip_code = ? )");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        " Query for matching address--->" +
                                        query);

        cstmt = con.prepareCall(query.toString());

        cstmt.setString(1,requestAddress);
        cstmt.setString(2,requestZip);
        cstmt.setString(3,dbAddress);
        cstmt.setString(4,dbZip);

        rs= cstmt.executeQuery();

        if(rs.next()){
            String respCode = rs.getString(1);
            String respDesc = rs.getString(2);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            " Result got from API for matching address, RespCode--->" +
                                            respCode + "<---RespDesc--->" + respDesc);
            return respCode;
        }
    } catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        " Exception in matching address--->" + ex);
        throw ex;
    }
    return null;
  }

  public boolean identifyBillType(ServicesRequestObj requestObj) throws
          Exception {
      boolean billType = false;
      Vector payeeList = null;
      Vector childPayeeList = null;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                      " Method for identifying bill type, Payee Sr No--->"
                                      +
                                      requestObj.getBillPaymentPayeeSerialNo()
                                      + "<---Payee Id--->" +
                                      requestObj.getBillPaymentPayeeSerialNo()
                                      + "<---Payee Name--->" +
                                      requestObj.getBillPaymentPayeeName()
                                      + "<---Payee Street1--->" +
                                      requestObj.getBillPaymentPayeeStreet1()
                                      + "<---Payee Zip--->" +
                                      requestObj.getBillPaymentPayeeZIP()
                                      + "<---Payee City--->" +
                                      requestObj.getBillPaymentPayeeCity()
                                      + "<---Payee State--->" +
                                      requestObj.getBillPaymentPayeeState());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                      " Method for identifying bill type, checking using Payee Sr No...");
      if (requestObj.getBillPaymentPayeeSerialNo() != null
          && requestObj.getBillPaymentPayeeSerialNo().trim().length() > 0
          && findPayeeBySrNo(requestObj.getBillPaymentPayeeSerialNo())) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for identifying bill type, Payee found returning Online...");
          return true;
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                      " Method for identifying bill type, checking using Payee Id...");
      if (requestObj.getBillPaymentPayeeID() != null
          && requestObj.getBillPaymentPayeeID().trim().length() > 0
          && findPayeeById(requestObj.getBillPaymentPayeeID())) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for identifying bill type, Payee found returning Online...");
          return true;
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                      " Method for identifying bill type, checking using Payee Name & Address...");
      if (requestObj.getBillPaymentPayeeName() != null
          && requestObj.getBillPaymentPayeeName().trim().length() > 0
          && requestObj.getBillPaymentPayeeStreet1() != null
          && requestObj.getBillPaymentPayeeStreet1().trim().length() > 0
          && requestObj.getBillPaymentPayeeZIP() != null
          && requestObj.getBillPaymentPayeeZIP().trim().length() > 0
          && requestObj.getBillPaymentPayeeCity() != null
          && requestObj.getBillPaymentPayeeCity().trim().length() > 0
          && requestObj.getBillPaymentPayeeState() != null
          && requestObj.getBillPaymentPayeeState().trim().length() > 0
              ) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for identifying bill type, getting all payees exist for provided Payee Name...");
          payeeList = findPayeeByName(requestObj.getBillPaymentPayeeName());
          if (payeeList != null && payeeList.size() > 0) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Method for identifying bill type, total payees got--->" + payeeList.size());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Method for identifying bill type, Getting child payees against fetched payees & provided city,state...");
              childPayeeList = getChildPayees(payeeList,
                                              requestObj.getBillPaymentPayeeCity(),
                                              requestObj.getBillPaymentPayeeState());
              if(childPayeeList != null && childPayeeList.size() > 0){
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                                                  " Method for identifying bill type, total child payees got--->" + childPayeeList.size());
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                                                  " Method for identifying bill type, matching provided address with address fetched from database...");
                  int totAddresses = childPayeeList.size();
                  for(int i=0; i<totAddresses; i++){
                      BPChildPayeeObj addressRecord = (BPChildPayeeObj) childPayeeList.elementAt(i);
                      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                              LOG_CONFIG),
                              " Method for identifying bill type, Database Data, Address1--->" + addressRecord.getStreet1()
                              + "<---ZIP Code--->" + addressRecord.getZipCode());
                      if(matchChildPayee(requestObj,addressRecord)){
                          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                              LOG_CONFIG),
                              " Method for identifying bill type, Provided address matched with Database Address... returning online");
                          billType = true;
                          break;
                      }
                  }
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                              LOG_CONFIG),
                                                  " Method for identifying bill type, is Bill Type Online after matching with all collection--->" + billType);
                  return billType;
              }else{
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                                                  " Method for identifying bill type, No Child Payee exist for Payees found against provided Name, returning check...");
                  return billType;
              }
          } else {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " Method for identifying bill type, No Payee exist for provided Name, returning check...");
              return billType;
          }
      }else{
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          " Method for identifying bill type, failed to check using Payee Name & Address, returning check...");
          return billType;
      }

  }

  public Vector getBillPaymentTransactions(ServicesRequestObj requestObj) throws
          Exception {

      StringBuffer query = new StringBuffer();
      Vector bpTransactions = new Vector();
      BPTransactionObj transRecord = null;
      PreparedStatement pstmt = null;
      boolean dateBased = false;
      ResultSet rs = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for getting bill payment transactions --- Card No--->" +
                                          requestObj.getCardNo()
                                          + "<---Status--->" + requestObj.getBillPaymentStatementType()
                                          + "<---Date From--->" + requestObj.getDateFrom()
                                          + "<---Date To--->" + requestObj.getDateTo());
          query.append("select trans_id,amount,payment_schd_date,comments,payment_sent_date,payment_resp_date,card_no,payee_sno,payee_name,state,city,payee_id,street2,street1,consumer_acct_no,zip_postal_code from bp_requests where card_no  = ? and status_id = ?");
          if(requestObj.getDateFrom() != null && requestObj.getDateTo() != null){
              query.append(" and payment_schd_date > ? and payment_schd_date <= ?");
              dateBased = true;
          }
          query.append(" order by trans_id desc");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " query for getting bill payment transactions--> " +
                                          query);
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1, requestObj.getCardNo());
          pstmt.setString(2, requestObj.getBillPaymentStatementType());
          if(dateBased){
              pstmt.setString(3,requestObj.getDateFrom());
              pstmt.setString(4,requestObj.getDateTo());
          }
          rs = pstmt.executeQuery();
          while (rs.next()) {
              transRecord = new BPTransactionObj();

              String trans_id = rs.getString(1);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " trans_id--->" + trans_id);
              if (trans_id != null && trans_id.trim().length() > 0) {
                  transRecord.setTransactionId(trans_id);
              }
              String amount = rs.getString(2);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " amount--->" + amount);
              if (amount != null && amount.trim().length() > 0) {
                  transRecord.setAmount(amount);
              }
              String payment_schd_date = rs.getString(3);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " payment_schd_date--->" + payment_schd_date);
              if (payment_schd_date != null && payment_schd_date.trim().length() > 0) {
                  transRecord.setScheduleDate(payment_schd_date);
              }
              String comments = rs.getString(4);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " comments--->" + comments);
              if (comments != null && comments.trim().length() > 0) {
                  transRecord.setComments(comments);
              }
              String payment_sent_date = rs.getString(5);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " payment_sent_date--->" + payment_sent_date);
              if (payment_sent_date != null && payment_sent_date.trim().length() > 0) {
                  transRecord.setSentDate(payment_sent_date);
              }
              String payment_resp_date = rs.getString(6);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " payment_resp_date--->" + payment_resp_date);
              if (payment_resp_date != null && payment_resp_date.trim().length() > 0) {
                  transRecord.setResponseDate(payment_resp_date);
              }
              String card_no = rs.getString(7);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " card_no--->" + card_no);
              if (card_no != null && card_no.trim().length() > 0) {
                  transRecord.setCardNo(card_no);
              }
              String payee_sno = rs.getString(8);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " payee_sno--->" + payee_sno);
              if (payee_sno != null && payee_sno.trim().length() > 0) {
                  transRecord.setPayeeSrNo(payee_sno);
              }
              String payee_name = rs.getString(9);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " payee_name--->" + payee_name);
              if (payee_name != null && payee_name.trim().length() > 0) {
                  transRecord.setPayeeName(payee_name);
              }
              String state = rs.getString(10);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " state--->" + state);
              if (state != null && state.trim().length() > 0) {
                  transRecord.setState(state);
              }
              String city = rs.getString(11);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " city--->" + city);
              if (city != null && city.trim().length() > 0) {
                  transRecord.setCity(city);
              }
              String payee_id = rs.getString(12);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " payee_id--->" + payee_id);
              if (payee_id != null && payee_id.trim().length() > 0) {
                  transRecord.setPayeeId(payee_id);
              }
              String street2 = rs.getString(13);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " street2--->" + street2);
              if (street2 != null && street2.trim().length() > 0) {
                  transRecord.setStreet2(street2);
              }
              String street1 = rs.getString(14);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " street1--->" + street1);
              if (street1 != null && street1.trim().length() > 0) {
                  transRecord.setStreet1(street1);
              }
              String consumer_acct_no = rs.getString(15);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " consumer_acct_no--->" + consumer_acct_no);
              if (consumer_acct_no != null && consumer_acct_no.trim().length() > 0) {
                  transRecord.setConsumerAccountNo(consumer_acct_no);
              }
              String zip_postal_code = rs.getString(16);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              " zip_postal_code--->" + zip_postal_code);
              if (zip_postal_code != null && zip_postal_code.trim().length() > 0) {
                  transRecord.setZipCode(zip_postal_code);
              }

              bpTransactions.add(transRecord);
          }
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Total bill payment transaction got--->" +
                                          bpTransactions.size());
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Exception in getting bill payment transactions--->" +
                                          ex);
          throw ex;
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
      return bpTransactions;
  }

  private void getPayerData(ServicesRequestObj requestObj) throws Exception{
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          " Method for getting Payer Data for card--->" + requestObj.getCardNo());

          query.append("select first_name1 || ' ' ||  last_name1 name,address1,address2,city,state_code,zip_postal_code,country_code from cards where card_no = ? ");
          pstmt = con.prepareStatement(query.toString());
          pstmt.setString(1, requestObj.getCardNo());
          rs = pstmt.executeQuery();
          if(rs.next()){
              String payer_name = rs.getString(1);
              if(payer_name != null && payer_name.trim().length() > 0){
                  requestObj.setBillPaymentPayerName(payer_name);
              }
              String payer_address1 = rs.getString(2);
              if(payer_address1 != null && payer_address1.trim().length() > 0){
                  requestObj.setBillPaymentPayerAddress1(payer_address1);
              }
              String payer_address2 = rs.getString(3);
              if(payer_address2 != null && payer_address2.trim().length() > 0){
                  requestObj.setBillPaymentPayerAddress2(payer_address2);
              }
              String payer_city = rs.getString(4);
              if(payer_city != null && payer_city.trim().length() > 0){
                  requestObj.setBillPaymentPayerCity(payer_city);
              }
              String payer_state = rs.getString(5);
              if(payer_state != null && payer_state.trim().length() > 0){
                  requestObj.setBillPaymentPayerState(payer_state);
              }
              String payer_postal_code = rs.getString(6);
              if(payer_postal_code != null && payer_postal_code.trim().length() > 0){
                  requestObj.setBillPaymentPayerZIP(payer_postal_code);
              }
              String payer_country = rs.getString(7);
              if(payer_country != null && payer_country.trim().length() > 0){
                  requestObj.setBillPaymentPayerCountry(payer_country);
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_WARNING),
                                          "Exception in getting Payer Data for card--->" + ex);
          throw new Exception("Unable to fetch Payer Data for provided card--->" + ex);
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
  }

  public boolean checkSerialBasedPayeeData(ServicesRequestObj requestObj) throws Exception{

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                      " Method for checking payee data provided/exist based on provided payee serial--->" +
                                      requestObj.getBillPaymentPayeeSerialNo());

    try {
        if (requestObj.getBillPaymentPayeeSerialNo() != null) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            " Method for checking payee data provided/exist based on provided payee serial, Checking whether Payee Data given for provided Payee Sr No--->" +
                                            requestObj.
                                            getBillPaymentPayeeSerialNo());
            if (requestObj.getBillPaymentPayeeID() != null &&
                requestObj.getBillPaymentPayeeID().trim().length() > 0) {
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                " Method for checking payee data provided/exist based on provided payee serial, Payee ID exist...");
                return true;
            } else if (requestObj.getBillPaymentPayeeName() != null &&
                       requestObj.getBillPaymentPayeeName().trim().length() > 0
                       && requestObj.getBillPaymentPayeeStreet1() != null &&
                       requestObj.getBillPaymentPayeeStreet1().trim().length() >
                       0
                       && requestObj.getBillPaymentPayeeState() != null &&
                       requestObj.getBillPaymentPayeeState().trim().length() >
                       0
                       && requestObj.getBillPaymentPayeeCity() != null &&
                       requestObj.getBillPaymentPayeeCity().trim().length() > 0
                       && requestObj.getBillPaymentPayeeZIP() != null &&
                       requestObj.getBillPaymentPayeeZIP().trim().length() > 0
                    ) {
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                " Method for checking payee data provided/exist based on provided payee serial, Payee Data exist...");
                return true;
            } else {
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                " Method for checking payee data provided/exist based on provided payee serial, No Payee Data exist,fetching from database...");
                String payeeId = getPayeeId(requestObj.
                                            getBillPaymentPayeeSerialNo());
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                " Method for checking payee data provided/exist based on provided payee serial,Payee Id got-->" +
                                                payeeId);

                if (payeeId == null || payeeId.trim().length() == 0) {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            " Method for checking payee data provided/exist based on provided payee serial,Payee Id not found fetching payee data...");
                    BPChildPayeeObj payeeData = getPayeeData(requestObj.
                            getBillPaymentPayeeSerialNo());
                    if (payeeData != null && payeeData.getStreet1() != null &&
                        payeeData.getStreet1().trim().length() > 0
                        && payeeData.getCity() != null &&
                        payeeData.getCity().trim().length() > 0
                        && payeeData.getState() != null &&
                        payeeData.getState().trim().length() > 0
                        && payeeData.getZipCode() != null &&
                        payeeData.getZipCode().trim().length() > 0) {

                        requestObj.setBillPaymentPayeeName(payeeData.
                                getStreet1().trim());
                        requestObj.setBillPaymentPayeeCity(payeeData.
                                getCity().trim());
                        requestObj.setBillPaymentPayeeState(payeeData.
                                getState().trim());
                        requestObj.setBillPaymentPayeeZIP(payeeData.
                                getZipCode().trim());
                        return true;
                    } else {
                        CommonUtilities.getLogger().log(LogLevel.getLevel(
                                Constants.
                                LOG_CONFIG),
                                " Method for checking payee data provided/exist based on provided payee serial,Cannot log payment request as mandatory data missing...");
                        return false;
                    }
                } else {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            " Method for checking payee data provided/exist based on provided payee serial,Payee Id found proceeding to log request...");
                    requestObj.setBillPaymentPayeeID(payeeId);
                    return true;
                }
            }
        } else {
            return true;
        }
    } catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        " Exception in checking payee data provided/exist based on provided payee serial,Payee Id found proceeding to log request-->" + ex);
        throw ex;
    }
  }

  public String generatePreAuthId(String isoSerialNo){
      int length = 0;
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                      " Method for generating Pre Auth Id for iso serial number--->" + isoSerialNo);
      length = isoSerialNo.length();
      if(length < 6){
          isoSerialNo = padValue(isoSerialNo,true,'0',6);
      }
      length = isoSerialNo.length();
      isoSerialNo = isoSerialNo.substring(length - 5,length);
      isoSerialNo = getSum(isoSerialNo) + isoSerialNo;
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                      " Pre Auth generated--->" + isoSerialNo);
      return isoSerialNo;
  }

  private String padValue(String source, boolean leftPad, char padChar, int maxLength) {
      if (source == null) {
          return "";
      }
      int strLen = source.length();
      String retString = source;
      if (strLen < maxLength) {
          for (int i = 0; i < (maxLength - strLen); i++) {
              retString = ((leftPad) ? (padChar + retString)
                           : (retString + padChar));
          }
      } else if (strLen > maxLength) {
          retString = (leftPad) ? (retString.substring(strLen - maxLength))
                      : (retString.substring(0, maxLength));
      }
      return retString;
  }

  public int getSum(String serial) {
    int length = serial.length();
      int sum = 0;
      for(int i=0;i<length;i++) {
        sum+=(int)(serial.charAt(i)-'0');
      }
      return (sum%10);
   }

   public void updatePreauthId(String isoSerialNo, String preAuthId) {
       StringBuffer query = new StringBuffer();
       PreparedStatement pstmt = null;
       try {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                   LOG_CONFIG),
                                           " Method for updating Pre Auth Id in iso_finance_msgs & trans_requests --- iso serial number--->" +
                                           isoSerialNo + "<---Pre Auth Id--->" +
                                           preAuthId);
           query.append(
                   "update iso_finance_msgs set i_038authidresp = ? where i_serial_no = ?");
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                   LOG_CONFIG),
                                           " Query for updating Pre Auth Id in iso_finance_msgs--->" + query);

           pstmt = con.prepareStatement(query.toString());
           pstmt.setString(1, preAuthId);
           pstmt.setString(2, isoSerialNo);

           pstmt.executeUpdate();

           query.delete(0,query.length());
           query.append("update trans_requests set auth_id_resp = ? where iso_serial_no = ?");
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                   LOG_CONFIG),
                                           " Query for updating Pre Auth Id in trans_requests--->" + query);
           pstmt = con.prepareStatement(query.toString());
           pstmt.setString(1, preAuthId);
           pstmt.setString(2, isoSerialNo);

           pstmt.executeUpdate();

           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                   LOG_CONFIG),
                                           " Pre Auth Id has been updated successfully in iso_finance_msgs & trans_requests...");
       } catch (Exception ex) {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                   LOG_CONFIG),
                                           "Exception iin updating Pre Auth Id in iso_finance_msgs & trans_requests--->" + ex);
       } finally {
           try {
               if (pstmt != null) {
                   pstmt.close();
                   pstmt = null;
               }
           } catch (SQLException ex1) {
           }
       }
   }
} //end FinancialServiceHome

