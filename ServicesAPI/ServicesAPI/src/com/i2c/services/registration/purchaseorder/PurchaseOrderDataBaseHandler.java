package com.i2c.services.registration.purchaseorder;

import com.i2c.services.registration.base.TransactionDataBaseHandler;
import com.i2c.services.registration.base.TransactionRequestInfoObj;
import java.sql.*;
import com.i2c.services.util.*;

/**
 * <p>Title: PurchaseOrderDataBaseHandler: A class which provides the database related services. </p>
 * <p>Description: This class is used to manipulate the database such as update the OFAC AVS status
 * in the database</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

class PurchaseOrderDataBaseHandler  extends TransactionDataBaseHandler {
  private Connection dbConn = null;

  /**
   * Constructor for the class.
   * @param dbConn Connection
   */
  PurchaseOrderDataBaseHandler(Connection dbConn) {
    super(dbConn);
    this.dbConn = dbConn;
  }

  /**
   * This method is used to update the OFAC & AVS trace audit no. It sets the new trace audit number against
   * the given record id.
   * @param traceAuditNumber String
   * @param recordID String
   */
  void updateOfac_AvsTraceAuditNo(String traceAuditNumber, String recordID) throws Exception{
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Updating Trace Audit number in ch_auth_batches1 for Record ID--->" +
                                      recordID + "<---Trace Audit No--->" + traceAuditNumber);
      query.append("update ch_auth_batches1 set trace_no = ");
      CommonUtilities.buildQueryInfo(query, traceAuditNumber, true);
      query.append(" where record_id = ");
      CommonUtilities.buildQueryInfo(query, recordID , true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for Updating Trace Audit number in ch_auth_batches1 for Record ID--->" +
                                      recordID);
      stmt = dbConn.createStatement();
      stmt.executeUpdate(query.toString());

    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Updating Card Holder Profile for card Number--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
  }

  public void updateCardHolderProfile(TransactionRequestInfoObj requestObj,
                               String cardNumber) throws Exception {

    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;

    try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Method for Updating Card Holder Profile for card Number--->" +
                                        cardNumber);
        query.append(
                "update cards set shipping_method = ?, first_name1 = ?, middle_name1 = ?, ");
        query.append(
                " last_name1 = ?,date_of_birth = ?,email = ?,ssn_nid_no = ?,address1 = ?, ");
        query.append(
                "address2 = ?,state_code = ?,city = ?,zip_postal_code = ?,user_id = ?, ");
        query.append(
                "assignment_no = ?,mother_maiden_nam = ?,country_code = ?, ");
        query.append(
                "home_phone_no = ?,work_phone_no = ?,gender = ?,employer_id = ?, ");
        query.append(
                "sales_node_no = ?,ofac_status = ?,avs_status = ?,foreign_id = ?,");
        query.append("foreign_id_type = ?,bill_address1 = ?,bill_address2 = ?, ");
        query.append("bill_city = ?,bill_country_code = ?,bill_state_code = ?,");
        query.append("bill_zip_code = ? where card_no = ?");
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Query for Updating Card Holder Profile for card Number--->" +
                                        query);

        pstmt = dbConn.prepareStatement(query.toString());
        pstmt.setString(1, requestObj.getShippingMethod());
        pstmt.setString(2, requestObj.getFirstName());
        pstmt.setString(3, requestObj.getMiddleName());
        pstmt.setString(4, requestObj.getLastName());
        pstmt.setString(5, requestObj.getDob());
        pstmt.setString(6, requestObj.getEmailAddress());
        pstmt.setString(7, requestObj.getSsn());
        pstmt.setString(8, requestObj.getAddress1());
        pstmt.setString(9, requestObj.getAddress2());
        pstmt.setString(10, requestObj.getState());
        pstmt.setString(11, requestObj.getCity());
        pstmt.setString(12, requestObj.getZip());
        pstmt.setString(13, requestObj.getUserId());
        pstmt.setString(14, requestObj.getAssignmentNo());
        pstmt.setString(15, requestObj.getMotherMaidenName());
        pstmt.setString(16, requestObj.getCountryCode());
        pstmt.setString(17, requestObj.getHomePhone());
        pstmt.setString(18, requestObj.getWorkPhone());
        pstmt.setString(19, requestObj.getGender());
        pstmt.setString(20, requestObj.getEmployerId());
        pstmt.setString(21, requestObj.getSalesNodeNo());
        pstmt.setString(22, requestObj.getOfacStatus());
        pstmt.setString(23, requestObj.getAvsStatus());
        pstmt.setString(24, requestObj.getForeignId());
        pstmt.setString(25, requestObj.getForeignIdType());
        pstmt.setString(26, requestObj.getBillingAddress1());
        pstmt.setString(27, requestObj.getBillingAddress2());
        pstmt.setString(28, requestObj.getBillingCity());
        pstmt.setString(29, requestObj.getBillingCountrycode());
        pstmt.setString(30, requestObj.getBillingState());
        pstmt.setString(31, requestObj.getBillingZipCode());
        pstmt.setString(32, cardNumber);

        pstmt.executeUpdate();
    } catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                        "Exception in Updating Card Holder Profile for card Number--->" +
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


}
