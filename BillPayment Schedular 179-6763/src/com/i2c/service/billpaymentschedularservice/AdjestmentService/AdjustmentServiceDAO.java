package com.i2c.service.billpaymentschedularservice.AdjestmentService;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

import com.i2c.service.base.*;
import com.i2c.service.billpaymentschedularservice.FTPService.*;
import com.i2c.service.excep.*;
import com.i2c.service.util.*;
import java.util.regex.Pattern;

class AdjustmentServiceDAO extends BaseHome{

  private String instanceID = null;
  private Connection dbConn = null;
  private Logger lgr = null;

  AdjustmentServiceDAO(String instanceID, Connection dbConn, Logger lgr) {
    this.instanceID = instanceID;
    this.dbConn = dbConn;
    this.lgr = lgr;
  }

  boolean checkScheduleTime() {
    boolean schTime = false;

    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {//com.i2c.utils.logging.I2cLogger.FINEST;
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST), "AdjustmentServiceDAO --- checkScheduleTime ---  Method for checking Schedule Date Time ");
      query.append(
          "select adj_last_up_on||' '||cutover_time adj_time from bp_processors");
      query.append(" where (adj_last_up_on < ");
      CommonUtilities.buildQueryInfo(query,
                                     CommonUtilities.
                                     getCurrentFormatDate(Constants.DATE_FORMAT), true);
      query.append(" or (adj_last_up_on = ");
      CommonUtilities.buildQueryInfo(query,
                                     CommonUtilities.
                                     getCurrentFormatDate(Constants.DATE_FORMAT), true);
      query.append(" and cutover_time <= ");
      CommonUtilities.buildQueryInfo(query,
                                     CommonUtilities.
                                     getCurrentFormatTime(Constants.EOD_TIME_FORMAT_MIN),
                                     true);
      query.append(" )) and is_active = 'Y'");
      query.append(" order by adj_time ");
      lgr.log(LogLevel.getLevel(Constants.LOG_INFO), "AdjustmentServiceDAO --- checkScheduleTime ---  Method for checking Schedule Date Time --- Query--->" + query);

      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG), "AdjustmentServiceDAO --- checkScheduleTime ---  Method for checking Schedule Date Time --- Query Executed Result Set GOT--->" + rs);

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          lgr.log(
              LogLevel.getLevel(Constants.LOG_FINEST),
              "AdjustmentServiceDAO --- Response File Schedule Time Arrived --- Found Adjustment File Processing Schedule Date Time--->" +
              rs.getString(1));
          schTime = true;
        }
      }
    }
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING), "AdjustmentServiceDAO --- checkScheduleTime ---  Exception in checking Schedule Date Time -->" + ex);
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
      lgr.log(
          LogLevel.getLevel(Constants.LOG_FINEST),
          "AdjustmentServiceDAO --- Method for updating batch schedule date --- ");
      newDate = CommonUtilities.addDaysInDate(Constants.DATE_FORMAT,
                                              CommonUtilities.
                                              getCurrentFormatDate(Constants.
          DATE_FORMAT), "1", Constants.DATE_FORMAT);
      query.append("update bp_processors set adj_last_up_on = ");
      CommonUtilities.buildQueryInfo(query, newDate, true);

      stmt = dbConn.createStatement();
      lgr.log(
          LogLevel.getLevel(Constants.LOG_FINEST),
          "AdjustmentServiceDAO --- Going to execute the Query for updating adjustment schedule date --->" + query);
      stmt.executeUpdate(query.toString());
      lgr.log(
          LogLevel.getLevel(Constants.LOG_FINEST),
          "AdjustmentServiceDAO --- Query for updating batch schedule date executed successfully--->");
    }
    catch (Exception ex) {
      lgr.log(
          LogLevel.getLevel(Constants.LOG_WARNING),
          "AdjustmentServiceDAO --- Excpetion in updating batch schedule date-->" +
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
   *
   */
  FTPInfoVO getAdjustmentFtpInfo() {
    boolean schTime = false;
    FTPInfoVO ftpVo = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "AdjustmentServiceDAO --- getResponseFtpInfo ---  Method for checking Schedule Date Time ");

      query.append("select td_sftp_address, td_sftp_port, td_sftp_userid, td_sftp_password,");
      query.append(" td_sftp_path, td_sftp_locpath, td_sftp_is_pgp, td_sftp_is_zip, td_sftp_allow, adjfile_prefix,");
      query.append(" adjfile_postfix, scheme_id, gen_null_file, pgp_pass_phrase, payfile_prefix");
      query.append(" from bp_processors");
      query.append(" where is_active = 'Y'");
//      query.append(" and td_sftp_allow = 'Y' ");

      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "AdjustmentServiceDAO --- getAdjustmenteFtpInfo ---  Method for checking Schedule Date Time\n --- Query--->" +
              query.toString());

//      System.out.print("getResponseFtpInfo---->" + query.toString());

      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      ftpVo = new FTPInfoVO();
      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG), "AdjustmentServiceDAO --- getResponseFtpInfo ---  Method for checking Schedule Date Time --- Query Executed Result Set GOT--->" + rs);

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
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING), "AdjustmentServiceDAO --- getAdjustmentFtpInfo ---  Exception in checking Schedule Date Time -->" + ex);
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
   * @return AdjustmentInfoVO
   * @throws LoadInfoExcep
   */
  public AdjustmentInfoVO fetchNextSchedule() throws LoadInfoExcep {
    AdjustmentInfoVO nextInfo = null;
    StringBuffer query = new StringBuffer();
    Vector records = null;
    LabelValueBean lbv = null;

    lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
            "AdjustmentServiceDAO --- Method Name: fetchNextSchedule ");
    try {
      query.append("select adj_last_up_on||' '||cutover_time adj_cut_time, processor_abrv  from bp_processors order by adj_cut_time");
      lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
              "AdjustmentServiceDAO --- Method Name: fetchNextSchedule --- query for fetching next schedule time---> " +
              query);

      records = getKeyValuePairs(query.toString(), dbConn);
      lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
              "AdjustmentServiceDAO --- Method Name: fetchNextSchedule --- query result size---> " +
              records.size());
      if (records.size() > 0) {
        lbv = (LabelValueBean) records.elementAt(0);
        nextInfo = new AdjustmentInfoVO();
        nextInfo.setCurrentDateTime(CommonUtilities.getCurrentFormatDateTime(
            Constants.EOD_DATE_TIME_FORMAT));
        nextInfo.setScheduledDateTime(lbv.getValue().trim());
        nextInfo.setTimeDifference(CommonUtilities.calculateTimeDifference(
            nextInfo.getCurrentDateTime(), nextInfo.getScheduledDateTime(),
            Constants.EOD_DATE_TIME_FORMAT));
        lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
                "AdjustmentServiceDAO --- Method Name: fetchNextSchedule --- AdjustmentInfoVO attribute values---> --ScheduledDateTime--: " +
                nextInfo.getScheduledDateTime() + " --CurrentDateTime--: " +
                nextInfo.getCurrentDateTime() + " --TimeDifference--: " +
                nextInfo.getTimeDifference());
      }
    }
    catch (Exception e) {
      lgr.log(LogLevel.getLevel(Constants.LOG_SEVERE),
              "AdjustmentServiceDAO:---  Exception in fetching next schedule-->: " +
              e);
      throw new LoadInfoExcep( -1,
          "Exception in fetching next Schedule Info for Response Scheduler---> " +
                              e);
    }
    return nextInfo;
  }

  /**
   * get the sleep time for adjustment
   * @param processor_id String
   * @return boolean
   */
  boolean adjustmentSleepTime(String processor_id) {
      boolean schTime = false;

      StringBuffer query = new StringBuffer();
      Statement stmt = null;
      ResultSet rs = null;

      try {
        lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                "AdjustmentServiceDAO --- adjustmentSleepTime ---  Method for checking PaymentSleepTime ");
        query.append(
            "select rsp_adj_rtry_mins adjustmentSleepTime from bp_processors");
        query.append(" where processor_id = ");
        CommonUtilities.buildQueryInfo(query, processor_id, true);
        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "AdjustmentServiceDAO --- adjustmentSleepTime --- Query--->" +
                query);

        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(query.toString());
        lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                "AdjustmentServiceDAO --- adjustmentSleepTime ---  Method for checking Schedule Date Time --- Query Executed Result Set GOT--->" +
                rs);

        if (rs.next()) {
          if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "AdjustmentServiceDAO --- PaymentSleepTime in the DB is ---> " +
                    rs.getString(1));
            Constants.ADJUSTMENT_SLEEP_TIME = Long.parseLong(rs.getString(1));
            Constants.ADJUSTMENT_SLEEP_TIME = Constants.ADJUSTMENT_SLEEP_TIME * 60 * 1000;
            schTime = true;
          }
        }
      }
      catch (Exception ex) {
        lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
                "AdjustmentServiceDAO --- adjustmentSleepTime ---  Exception in checking Schedule Date Time -->" +
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
   * get list of dates of the unprocessed Adjustment Files
   * @return ArrayList
   */

  public ArrayList getOldAdjustmentFileDateFtp() {
    ArrayList adjDateList = null;
    String adjDate = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "AdjustmentServiceDAO --- getOldAdjustmentFileDateFtp ---  Method for checking Adjustmetn File Date which are not processed ");

      query.append("select f.bp_file_date");
      query.append(" from bp_batch_files f, bp_batches b, bp_batch_details d");
      query.append(" where f.bp_file_sr_no = b.bp_file_sr_no");
      query.append(" and b.bp_batch_sr_no = d.bp_batch_sr_no");
      query.append(" and d.bp_adj_file_no IS NULL");
      query.append(" and bp_file_date >= Today - (select process_files_days from bp_processors where processor_id ='"+ Constants.PROCESSOR_ID +"')");

      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "AdjustmentServiceDAO --- getOldAdjustmentFileDateFtp --- Query--->" +
              query.toString());

      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      adjDateList = new ArrayList();
      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "AdjustmentServiceDAO --- getOldAdjustmentFileDateFtp ---  Query Executed Result Set GOT--->" +
              rs);

      while (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          adjDate = CommonUtilities.convertDateFormat(Constants.ACH_DATE_FORMAT,
              Constants.DATE_FORMAT,
              rs.getString(1));

          adjDate = adjDate.replaceAll(Pattern.quote("/"),"");
          adjDateList.add(adjDate);
        }
      }
    }
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
              "AdjustmentServiceDAO --- getResponseFtpInfo ---  Exception in checking Schedule Date Time -->" +
              ex.getMessage());
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
    return adjDateList;
  }

}
