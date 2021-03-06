package com.i2c.campaigngenservice.util;

import java.util.Vector;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;
import com.i2c.utils.logging.*;
import java.util.logging.Logger;

public class MailMsgDAO {

  private String emailID = null;
  private String emailFrom = null;
  private String emailTo = null;
  private String emailSubject = null;
  private String emailMsgHdr = null;
  private String emailMsgBody = null;
  private String emailMsgFtr = null;
  private Logger lgr = null;

  public MailMsgDAO(String emailID) {
    this.emailID = emailID;
  }




  public MailMsgDAO(String emailID, Logger lgr) {
    this.emailID = emailID;
    this.lgr = lgr;
  }




  public Vector getMailInfo(String instanceName) throws Exception{
    Vector mailInfo = new Vector();
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;

    StringBuffer query = new StringBuffer();

    try {
      lgr.log(I2cLogger.FINEST,"Method for getting Email info for ID-->" + emailID + "<--InstanceName-->" + instanceName);
      query.append("select * from emails where email_id = ");
      CommonUtilities.buildQueryInfo(query,emailID,true);
      lgr.log(I2cLogger.FINEST,"Query for getting Email info--->" + query);
      dbConn = DatabaseHandler.getConnection("MailMsgDAO", instanceName);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if(rs.next()){
        if(rs.getString("email_add_from") != null && rs.getString("email_add_from").trim().length() > 0){
          emailFrom = rs.getString("email_add_from").trim();
        }
        if(rs.getString("email_address") != null && rs.getString("email_address").trim().length() > 0){
          emailTo = rs.getString("email_address").trim();
        }
        if(rs.getString("email_subject") != null && rs.getString("email_subject").trim().length() > 0){
          emailSubject = rs.getString("email_subject").trim();
        }
        if(rs.getString("email_msg_hd1") != null && rs.getString("email_msg_hd1").trim().length() > 0){
          emailMsgHdr = rs.getString("email_msg_hd1").trim();
        }
        if(rs.getString("email_msg_hd2") != null && rs.getString("email_msg_hd2").trim().length() > 0){
          emailMsgBody = rs.getString("email_msg_hd2").trim();
        }
        if(rs.getString("email_msg_hd3") != null && rs.getString("email_msg_hd3").trim().length() > 0){
          emailMsgFtr = rs.getString("email_msg_hd3").trim();
        }
        if(emailFrom != null && emailTo != null){
          mailInfo.add(emailFrom);
          mailInfo.add(emailTo);
          mailInfo.add(emailSubject);
          mailInfo.add(emailMsgHdr);
          mailInfo.add(emailMsgBody);
          mailInfo.add(emailMsgFtr);
        }
      }else{
        lgr.log(I2cLogger.FINEST,"No Email info found for given email ID --->" + emailID);
        throw new Exception("Unable to find email information from 'emails' table for provided emailID--->" + emailID);
      }
    }
    catch (Exception ex) {
      lgr.log(I2cLogger.WARNING,"Exception in getting mail info --->" + ex);
      throw new Exception("Exception in getting mail info --->" + ex);
    }finally{
      try {
        if (rs != null) {
          rs.close();
        }
        if (stmt != null) {
          stmt.close();
        }
        if (dbConn != null) {
          dbConn.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return mailInfo;
  }

  public Vector getMailInfo(Connection dbConn) throws Exception{
    Vector mailInfo = new Vector();
    Statement stmt = null;
    ResultSet rs = null;

    StringBuffer query = new StringBuffer();

    try {
      lgr.log(I2cLogger.FINEST,"Method for getting Email info for ID-->" + emailID);
      query.append("select * from emails where email_id = ");
      CommonUtilities.buildQueryInfo(query,emailID,true);
      lgr.log(I2cLogger.FINEST,"Query for getting Email info--->" + query);

      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if(rs.next()){
        if(rs.getString("email_add_from") != null && rs.getString("email_add_from").trim().length() > 0){
          emailFrom = rs.getString("email_add_from").trim();
        }
        if(rs.getString("email_address") != null && rs.getString("email_address").trim().length() > 0){
          emailTo = rs.getString("email_address").trim();
        }
        if(rs.getString("email_subject") != null && rs.getString("email_subject").trim().length() > 0){
          emailSubject = rs.getString("email_subject").trim();
        }
        if(rs.getString("email_msg_hd1") != null && rs.getString("email_msg_hd1").trim().length() > 0){
          emailMsgHdr = rs.getString("email_msg_hd1").trim();
        }
        if(rs.getString("email_msg_hd2") != null && rs.getString("email_msg_hd2").trim().length() > 0){
          emailMsgBody = rs.getString("email_msg_hd2").trim();
        }
        if(rs.getString("email_msg_hd3") != null && rs.getString("email_msg_hd3").trim().length() > 0){
          emailMsgFtr = rs.getString("email_msg_hd3").trim();
        }
        if(emailFrom != null && emailTo != null){
          mailInfo.add(emailFrom);
          mailInfo.add(emailTo);
          mailInfo.add(emailSubject);
          mailInfo.add(emailMsgHdr);
          mailInfo.add(emailMsgBody);
          mailInfo.add(emailMsgFtr);
        }
      }else{
        lgr.log(I2cLogger.FINEST,"No Email info found for given email ID --->" + emailID);
        throw new Exception("Unable to find email information from 'emails' table for provided emailID--->" + emailID);
      }
    }
    catch (Exception ex) {
      lgr.log(I2cLogger.WARNING,"Exception in getting mail info --->" + ex);
      throw new Exception("Exception in getting mail info --->" + ex);
    }finally{
      try {
        if (rs != null) {
          rs.close();
        }
        if (stmt != null) {
          stmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return mailInfo;
  }
}
