package com.i2c.service.billpaymentschedularservice.ResponseService;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.*;

import com.i2c.service.util.CommonUtilities;
import com.i2c.service.util.Constants;
import com.i2c.service.util.LogLevel;
import com.i2c.service.util.DatabaseHandler;
import com.i2c.service.billpaymentschedularservice.ServiceMain;

import com.i2c.service.base.BaseHome;
import com.i2c.service.billpaymentschedularservice.FTPService.FTPInfoVO;
import com.i2c.service.util.LabelValueBean;
import com.i2c.service.excep.LoadInfoExcep;
import java.util.Vector;
import java.util.logging.Logger;
import java.sql.Date;
import java.util.ArrayList;

public class ResponseServiceDAO
    extends BaseHome {

  private String instanceID = null;
  private Connection dbConn = null;
  private Logger lgr = null;

  public ResponseServiceDAO(String instanceID, Connection dbConn, Logger lgr) {
    this.instanceID = instanceID;
    this.dbConn = dbConn;
    this.lgr = lgr;
  }

  boolean checkScheduleTime() {

    boolean schTime = false;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST), "ResponseServiceDAO --- checkScheduleTime ---  Method for checking Schedule Date Time ");
      query.append(
          "select rsp_last_up_on||' '||cutover_time res_time from bp_processors");
      query.append(" where (rsp_last_up_on < ");
      CommonUtilities.buildQueryInfo(query,
                                     CommonUtilities.
                                     getCurrentFormatDate(Constants.DATE_FORMAT), true);
      query.append(" or (rsp_last_up_on = ");
      CommonUtilities.buildQueryInfo(query,
                                     CommonUtilities.
                                     getCurrentFormatDate(Constants.DATE_FORMAT), true);
      query.append(" and cutover_time <= ");
      CommonUtilities.buildQueryInfo(query,
                                     CommonUtilities.
                                     getCurrentFormatTime(Constants.
          EOD_TIME_FORMAT_MIN), true);
      query.append(" )) and is_active = 'Y'");
      query.append(" order by res_time ");
      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "ResponseServiceDAO --- checkScheduleTime ---  Method for checking Schedule Date Time --- Query--->\n" + query);

      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "ResponseServiceDAO --- checkScheduleTime ---  Method for checking Schedule Date Time --- Query Executed Result Set GOT--->" + rs);

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          lgr.
              log(
                  LogLevel.getLevel(Constants.LOG_FINEST),
                  "ResponseServiceDAO --- Response File Schedule Time Arrived --- Found Response File Processing Schedule Date Time--->" +
                  rs.getString(1));
          schTime = true;
        }
      }
    }
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
              "ResponseServiceDAO --- checkScheduleTime ---  Exception in checking Schedule Date Time -->" +
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
    return schTime;
  }

  void updateNextSchedule() {
    StringBuffer query = new StringBuffer();
    String newDate = null;
    Statement stmt = null;

    try {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "ResponseServiceDAO --- Method for updating batch schedule date --- ");
      newDate = CommonUtilities.addDaysInDate(Constants.DATE_FORMAT,
                                              CommonUtilities.
                                              getCurrentFormatDate(Constants.
          DATE_FORMAT), "1", Constants.DATE_FORMAT);
      query.append("update bp_processors set rsp_last_up_on = ");
      CommonUtilities.buildQueryInfo(query, newDate, true);

      stmt = dbConn.createStatement();
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "ResponseServiceDAO --- Going to execute the Query for updating response schedule date --->" +
              query);
      stmt.executeUpdate(query.toString());
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "ResponseServiceDAO --- Query for updating batch schedule date executed successfully--->");
    }
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
              "ResponseServiceDAO --- Excpetion in updating batch schedule date-->" +
              ex);
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

  /**
   * Get FTP Connection, and path information
   */
  public FTPInfoVO getResponseFtpInfo() {
    boolean schTime = false;
    FTPInfoVO ftpVo = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "ResponseServiceDAO --- getResponseFtpInfo ---  Method for checking Schedule Date Time ");

      query.append(
          "select td_sftp_address, td_sftp_port, td_sftp_userid, td_sftp_password,");
      query.append(" td_sftp_path, td_sftp_locpath, td_sftp_is_pgp, td_sftp_is_zip, td_sftp_allow, respfile_prefix,");
      query.append(
          " respfile_postfix, scheme_id, gen_null_file, pgp_pass_phrase, payfile_prefix");
      query.append(" from bp_processors");
      query.append(" where is_active = 'Y'");
//           query.append(" and td_sftp_allow = 'Y' ");

      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "ResponseServiceDAO --- getResponseFtpInfo ---  Method for checking Schedule Date Time --- Query--->" +
              query.toString());

//            System.out.print("getResponseFtpInfo---->" + query.toString());
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      ftpVo = new FTPInfoVO();
      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "ResponseServiceDAO --- getResponseFtpInfo ---  Method for checking Schedule Date Time --- Query Executed Result Set GOT--->" +
              rs);

      while (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setFtpAddress(rs.getString(1));
        }
        if (rs.getString(2) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setFtpPort(rs.getString(2));
        }
        if (rs.getString(3) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setFtpUserId(rs.getString(3));
        }
        if (rs.getString(4) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setFtpPassword(rs.getString(4));
        }
        if (rs.getString(5) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setFtpRemotePath(rs.getString(5));
        }
        if (rs.getString(6) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setFtpLocalPath(rs.getString(6));
        }
        if (rs.getString(7) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setIsPgp(rs.getString(7));
        }
        if (rs.getString(8) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setIsZip(rs.getString(8));
        }
        if (rs.getString(9) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setIsFtpAllowed(rs.getString(9));
        }
        if (rs.getString(10) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setFilePreFix(rs.getString(10));
        }
        if (rs.getString(11) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setFilePostFix(rs.getString(11));
        }
        if (rs.getString(12) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setSchemeId(rs.getString(12));
        }
        if (rs.getString(13) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setIsNullFile(rs.getString(13));
        }
        if (rs.getString(14) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setPgpPassPhrase(rs.getString(14));
        }
        if (rs.getString(15) != null && rs.getString(1).trim().length() > 0) {
          ftpVo.setPaymentFilePrefix(rs.getString(15));
        }

      }
    }
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
              "ResponseServiceDAO --- getResponseFtpInfo ---  Exception in checking Schedule Date Time -->" + ex);
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
    return ftpVo;
  }

  /**
   * Method Name: fetchNextSchedule
   * Description: This method fetches the next schedule info (bussiness date,
   *              cutovertime) from the bp_processors table.
   * Parameters:  NONE
   * @return ResponseInfoVO
   * @throws LoadInfoExcep
   */
  public ResponseInfoVO fetchNextSchedule() throws LoadInfoExcep {
    ResponseInfoVO nextInfo = null;
    StringBuffer query = new StringBuffer();
    Vector records = null;
    LabelValueBean lbv = null;

    lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
            "ResponseServiceDAO --- Method Name: fetchNextSchedule ");
    try {
      query.append("select rsp_last_up_on||' '||cutover_time resp_cut_time, processor_abrv  from bp_processors order by resp_cut_time");
      lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
              "ResponseServiceDAO --- Method Name: fetchNextSchedule --- query for fetching next schedule time---> " +
              query);

      records = getKeyValuePairs(query.toString(), dbConn);
      lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
              "ResponseServiceDAO --- Method Name: fetchNextSchedule --- query result size---> " +
              records.size());
      if (records.size() > 0) {
        lbv = (LabelValueBean) records.elementAt(0);
        nextInfo = new ResponseInfoVO();
        nextInfo.setCurrentDateTime(CommonUtilities.getCurrentFormatDateTime(
            Constants.EOD_DATE_TIME_FORMAT));
        nextInfo.setScheduledDateTime(lbv.getValue().trim());
        nextInfo.setTimeDifference(CommonUtilities.calculateTimeDifference(
            nextInfo.getCurrentDateTime(), nextInfo.getScheduledDateTime(),
            Constants.EOD_DATE_TIME_FORMAT));
        lgr.
            log(LogLevel.getLevel(Constants.LOG_FINE),
                "ResponseServiceDAO --- Method Name: fetchNextSchedule --- ResponseInfoVO attribute values---> --ScheduledDateTime--: " +
                nextInfo.getScheduledDateTime() + " --CurrentDateTime--: " +
                nextInfo.getCurrentDateTime() + " --TimeDifference--: " +
                nextInfo.getTimeDifference());
      }
    }
    catch (Exception e) {
      lgr.log(LogLevel.getLevel(Constants.LOG_SEVERE),
              "ResponseServiceDAO:---  Exception in fetching next schedule-->: " +
              e);
      throw new LoadInfoExcep( -1,
          "Exception in fetching next Schedule Info for Response Scheduler---> " +
                              e);
    }
    return nextInfo;
  }





  /**
   * get the sleep time for respnse
   * @param processor_id String
   * @return boolean
   */
  boolean responseSleepTime(String processor_id) {
      boolean schTime = false;

      StringBuffer query = new StringBuffer();
      Statement stmt = null;
      ResultSet rs = null;

      try {
        lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                "ResponseServiceDAO --- responseSleepTime ---  Method for checking PaymentSleepTime ");
        query.append(
            "select rsp_adj_rtry_mins responseSleepTime from bp_processors");
        query.append(" where processor_id = ");
        CommonUtilities.buildQueryInfo(query, processor_id, true);
        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "ResponseServiceDAO --- responseSleepTime --- Query--->" +
                query);

        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(query.toString());
        lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                "ResponseServiceDAO --- responseSleepTime ---  Method for checking Schedule Date Time --- Query Executed Result Set GOT--->" +
                rs);

        if (rs.next()) {
          if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "ResponseServiceDAO --- PaymentSleepTime in the DB is ---> " +
                    rs.getString(1));
            Constants.RESPONSE_SLEEP_TIME = Long.parseLong(rs.getString(1));
            Constants.RESPONSE_SLEEP_TIME = Constants.RESPONSE_SLEEP_TIME * 60 * 1000;
            schTime = true;
          }
        }
      }
      catch (Exception ex) {
        lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
                "ResponseServiceDAO --- responseSleepTime ---  Exception in checking Schedule Date Time -->" +
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
          ex1.printStackTrace();
        }
      }
      return schTime;
  }





  /**
   * get List of Date for Unprocessed Response files
   * @return ArrayList
   */
  public ArrayList getOldResponseFileDateFtp() {
    ArrayList respDateList = null;
    String respDate = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "ResponseServiceDAO --- getOldResponseFileDateFtp ---  Method for checking Response File Date which are not processed ");

      query.append("select f.bp_file_date");
      query.append(" from bp_batch_files f, bp_batches b, bp_batch_details d");
      query.append(" where f.bp_file_sr_no = b.bp_file_sr_no");
      query.append(" and b.bp_batch_sr_no = d.bp_batch_sr_no");
      query.append(" and d.bp_resp_file_no IS NULL");
      query.append(" and bp_file_date >= Today - (select process_files_days from bp_processors where processor_id ='"+ Constants.PROCESSOR_ID +"')");

      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "ResponseServiceDAO --- getOldResponseFileDateFtp --- Query--->" +
              query.toString());

      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      respDateList = new ArrayList();
      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "ResponseServiceDAO --- getOldResponseFileDateFtp ---  Query Executed Result Set GOT--->" +
              rs);

      while (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          respDate = CommonUtilities.convertDateFormat(Constants.ACH_DATE_FORMAT,
              Constants.DATE_FORMAT,
              rs.getString(1));
//          respDate = rs.getString(1);
          respDate = respDate.replaceAll(Pattern.quote("/"),"");
          respDateList.add(respDate);
        }
      }
    }
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
              "ResponseServiceDAO --- getResponseFtpInfo ---  Exception in checking Schedule Date Time -->" + ex);
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
    return respDateList;
  }






  /**
   * get the sleep time for Next Retry for Respnse/Adjustment
   * @param processor_id String
   * @return boolean
   */
  public long[] respAdjTimes(String processor_id) {
      long[] resAdjRetryTime = new long[2];

      StringBuffer query = new StringBuffer();
      Statement stmt = null;
      ResultSet rs = null;

      try {
        lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                "ResponseServiceDAO --- respAdjTimes ---  Method for checking PaymentSleepTime ");
        query.append("select rsp_adj_rtry_mins responseSleepTime, rsp_adj_ntfy_mins notifyTime");
        query.append(" from bp_processors");
        query.append(" where processor_id = ");
        CommonUtilities.buildQueryInfo(query, processor_id, true);
        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "ResponseServiceDAO --- respAdjTimes --- Query--->" +
                query);

        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(query.toString());
        lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                "ResponseServiceDAO --- respAdjTimes ---  Method for checking Schedule Date Time --- Query Executed Result Set GOT--->" +
                rs);

        while (rs.next()) {
          if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "ResponseServiceDAO --- ResponseAdjustment Retry Time in the DB is ---> " +
                    rs.getString(1));
            resAdjRetryTime[0] = Long.parseLong(rs.getString(1));
          }
          if (rs.getString(2) != null && rs.getString(1).trim().length() > 0) {
            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "ResponseServiceDAO --- ResponseAdjustment Failure Notification Time in the DB is ---> " +
                    rs.getString(2));
            resAdjRetryTime[1] = Long.parseLong(rs.getString(2));
          }
        }
      }
      catch (Exception ex) {
        lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
                "ResponseServiceDAO --- respAdjTimes ---  Exception in checking Schedule Date Time -->" +
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
          ex1.printStackTrace();
        }
      }
      return resAdjRetryTime;
  }





  /**
   * get the time to email when fail to get Respnse/Adjustment File from FTP
   * @param processor_id String
   * @return boolean
   */
//  public long respAdjNotifyTime(String processor_id) {
//      long resAdjNotifyTime = 0;
//
//      StringBuffer query = new StringBuffer();
//      Statement stmt = null;
//      ResultSet rs = null;
//
//      try {
//        lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
//                "ResponseServiceDAO --- respAdjNotifyTime ---  Method for checking PaymentSleepTime ");
//        query.append(
//            "select rsp_adj_ntfy_mins notifyTime from bp_processors");
//        query.append(" where processor_id = ");
//        CommonUtilities.buildQueryInfo(query, processor_id, true);
//        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
//                "ResponseServiceDAO --- respAdjNotifyTime --- Query--->" +
//                query);
//
//        stmt = dbConn.createStatement();
//        rs = stmt.executeQuery(query.toString());
//        lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
//                "ResponseServiceDAO --- respAdjNotifyTime ---  Method for checking Schedule Date Time --- Query Executed Result Set GOT--->" +
//                rs);
//
//        if (rs.next()) {
//          if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
//            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
//                    "ResponseServiceDAO --- PaymentSleepTime in the DB is ---> " +
//                    rs.getString(1));
//            resAdjNotifyTime = Long.parseLong(rs.getString(1));
//            resAdjNotifyTime = resAdjNotifyTime * 60 * 1000;
//          }
//        }
//      }
//      catch (Exception ex) {
//        lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
//                "ResponseServiceDAO --- respAdjNotifyTime ---  Exception in checking Schedule Date Time -->" +
//                ex);
//      }
//      finally {
//        try {
//          if (rs != null) {
//            rs.close();
//            rs = null;
//          }
//          if (stmt != null) {
//            stmt.close();
//            stmt = null;
//          }
//        }
//        catch (SQLException ex1) {
//          ex1.printStackTrace();
//        }
//      }
//      return resAdjNotifyTime;
//  }



}
