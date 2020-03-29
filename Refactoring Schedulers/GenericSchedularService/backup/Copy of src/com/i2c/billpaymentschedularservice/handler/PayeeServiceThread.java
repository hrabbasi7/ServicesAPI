package com.i2c.billpaymentschedularservice.handler;

import java.io.*;
import java.sql.*;

import com.i2c.billpayment.jobs.*;
import com.i2c.billpayment.vo.*;
import com.i2c.billpaymentschedularservice.monitor.*;
import com.i2c.billpaymentschedularservice.util.*;
import com.i2c.billpaymentschedularservice.FTPService.FTPInfoVO;
import com.i2c.payeeinfoservice.mapper.PayeeMapper;
import java.util.Iterator;
import com.i2c.utils.logging.I2cLogger;
import java.util.logging.Logger;
import com.i2c.billpaymentschedularservice.FTPService.I2cFTP;
import com.i2c.billpaymentschedularservice.dao.PayeeServiceDAO;
import com.i2c.billpaymentschedularservice.mailer.PayeeServiceMailer;
import com.i2c.billpaymentschedularservice.model.PayeeInfoVO;
import com.i2c.billpaymentschedularservice.model.DbConnectionInfoVO;

public class PayeeServiceThread
    extends Thread {
  private String instanceName = null;
  private PayeeServiceMonitor monitorThread = null;
  private Logger logger = null;
  private Logger payeeApiLogger = null;

  public PayeeServiceThread(String instanceName,
                            PayeeServiceMonitor monitorThread,
                            Logger payeeLgr,
                            Logger payeeApiLogger) {
    this.instanceName = instanceName;
    this.monitorThread = monitorThread;
    this.logger = payeeLgr;
    this.payeeApiLogger = payeeApiLogger;
  }

  public void run() {

    PayeeServiceDAO payeeServiceDAO = null;
    Connection dbConn = null;
    boolean batchTime = false;
    StringBuffer payeeTransStatus = null;
    PayeeServiceMailer payeeMailer = null;
    PayeeInfoVO nextSchedule = null;
    try {
      logger.log(LogLevel.getLevel(Constants.LOG_INFO),
              "PayeeServiceThread -- Method Name: run --- payeeService Thread is started");

      logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "PayeeServiceThread -- Method Name: run --- Getting Database Connection for instance --->" +
              instanceName);
      dbConn = DatabaseHandler.getMasterConnection("PayeeServiceThread",
          instanceName,
          Constants.CARD_INSTANCE_DB_CONN);

      payeeServiceDAO = new PayeeServiceDAO(instanceName, dbConn, logger);

      while (true) {

        try {
          try {
            logger.log(LogLevel.getLevel(Constants.LOG_CONFIG), "PayeeServiceThread -- Method Name: run --- Calling method for testing database connection");
            testConnection(dbConn);
          }
          catch (SQLException tstConEx) {
            logger.log(LogLevel.getLevel(Constants.LOG_CONFIG), "PayeeServiceThread -- Method Name: run --- Exception in testing database connection -- Creating new Connection");
            try {
              dbConn = DatabaseHandler.getMasterConnection("PayeeServiceThread",
                  instanceName,
                  Constants.CARD_INSTANCE_DB_CONN);
              payeeServiceDAO = new PayeeServiceDAO(instanceName, dbConn, logger);
            }
            catch (Exception ex) {
              logger.log(LogLevel.getLevel(Constants.LOG_CONFIG), "ACHLoadBatchReturnServiceThread  -- Method Name: run --- Exception in creating new Connection");
            }
          }
          catch (Exception tstConEx) {
            logger.log(LogLevel.getLevel(Constants.LOG_CONFIG), "PayeeServiceThread -- Method Name: run --- Exception in testing database connection -- Creating new Connection");
            try {
              dbConn = DatabaseHandler.getMasterConnection("PayeeServiceThread",
                  instanceName,
                  Constants.CARD_INSTANCE_DB_CONN);

              payeeServiceDAO = new PayeeServiceDAO(instanceName, dbConn, logger);
            }
            catch (Exception ex) {
              logger.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                      "PayeeServiceThread  -- Method Name: run --- Exception in creating new Connection");
            }
          }

          logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
                  " \n\nGetting current date time for updating PAYEE_SRV monitoring Date Time ");
          java.util.Date currDate = getCurrentDate();
          logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
                  " \n\nUpdating PAYEE_SRV Monitoring Date Time --- Current Date Time Got--->" +
                  currDate);
          monitorThread.setMonitorDateTime(currDate);
          // Check Service allowed for Instance
          if (payeeServiceDAO.processScheduler(dbConn,
                                               Constants.
                                               USE_BILL_PAYMENT_PAYEE_PROCESSING)) {
            batchTime = payeeServiceDAO.checkScheduleTime();
            logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "PayeeServiceThread --- run --- Payee received from <PayeeScheduleTime> ---> " +
                    batchTime);
            if (batchTime) {
              logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "PayeeServiceThread --- run --- Invoking Bill Payee File processing");
              try {
                /*************<<<*************>>>>>***********/
                logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                        ">>>> START <invokePayeeProcessing> to populate and update Payee Info");

                payeeTransStatus = invokePayeeProcessing(dbConn,
                    payeeServiceDAO);

                logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                        ">>>> END <invokePayeeProcessing> to populate and update Payee Info");

                /**************\****************\*****************/
                logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        "PayeeServiceThread --- run --- Sending success email");

                payeeMailer = new PayeeServiceMailer(Constants.PAYEE_SRV_SUCC,
                    instanceName,
                    null,
                    payeeTransStatus.toString(),
                    dbConn,
                    logger);
                payeeMailer.sendEmailNotification();
              }
              catch (Exception procEx) {
                logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        "PayeeServiceThread --- run --- Exception in processing of Bill Payee File Processing --- Emailing to Admin-->" +
                        procEx);
                payeeMailer = new PayeeServiceMailer(Constants.PAYEE_SRV_FAIL,
                    instanceName,
                    Constants.getMachineIP(),
                    procEx.toString(),
                    CommonUtilities.getStackTrace(procEx),
                    dbConn,
                    logger);
                payeeMailer.sendEmailNotification();

                logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                        "PayeeServiceThread --- run --- Stop Payee File processing after Exception Email  -- Sleeping for default time");
                sleep(Constants.PAYEE_SLEEP_TIME);
              }
            } ///////////Check for the Time difference////////////////
            else {
              nextSchedule = payeeServiceDAO.fetchNextSchedule();
              if (nextSchedule != null) {
                logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                        "PayeeServiceThread -- Method Name: run --- (GOT Next Payee Schedule Info) --->" +
                        "ScheduledDateTime--: " +
                        nextSchedule.getScheduledDateTime() +
                        " --CurrentDateTime--: " +
                        nextSchedule.getCurrentDateTime() +
                        " --TimeDifference--: " +
                        nextSchedule.getTimeDifference());
                if (nextSchedule.getTimeDifference() != -1 &&
                    nextSchedule.getTimeDifference() < 0) {
                  logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
                          "PayeeServiceThread -- Next Schedule Time is Negative ---  sleeping for PayeeSleepTime ---> " +
                          nextSchedule.getTimeDifference());
                  sleep(Constants.PAYEE_SLEEP_TIME);
//                  continue;
                }
                else if (nextSchedule.getTimeDifference() != -1 &&
                         nextSchedule.getTimeDifference() <
                         Constants.PAYEE_SLEEP_TIME) {
                  logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                          "PayeeServiceThread -- No PayeeService Tasks to schedule ---  Sleeping for ---> " +
                          nextSchedule.getTimeDifference());
                  sleep(nextSchedule.getTimeDifference());
                } ///////////////////////////////////
                else { //end if check schTime arrived
              logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "PayeeServiceThread --- run --- Payee File processing Time not arrived -- Sleeping for default time");
              sleep(Constants.PAYEE_SLEEP_TIME);
            }
          }
        }
      }
      else { //end if check isAllowed
        logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
                "PayeeServiceThread --- run --- Payee File processing is not allowed for this instance");
        sleep(Constants.PAYEE_SLEEP_TIME);
          }
        }
        catch (Exception ex) { //end inner try
          logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
                  "PayeeServiceThread --- Excpetion in Bill Payee File processing--->",
                  ex);
          ex.printStackTrace();
          sleep(Constants.PAYEE_SLEEP_TIME);
        } //end inner catch
      } //end while
    }
    catch (Exception ex) { //end outer try
      logger.log(LogLevel.getLevel(Constants.LOG_SEVERE),
              "PayeeServiceThread --- Outer Catch -- Excpetion in Payee Service Thread--->" +
              ex);
    }
  } //end run

  private StringBuffer invokePayeeProcessing(Connection dbConn,
                                             PayeeServiceDAO payeeDao) throws
      Exception {

    boolean isDownload = false;
    FTPInfoVO ftpVo = null;
    I2cFTP ftpServer = null;
    StringBuffer logPath = new StringBuffer();
    StringBuffer returnMsg = null;
    StringBuffer ouputPath = new StringBuffer();
    String movePath = new String();
    boolean sucessDel = false;
//    boolean exists = false;
    try {
      logPath.append(Constants.LOG_FILE_PATH + //File.separator +
                     instanceName + File.separator + Constants.PAYEE_SERVICE);
      logger.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "PayeeServiceThread --- invokePayeeProcessing --- Log File Path to be passed--->" +
              logPath);

      if (!new File(logPath.toString()).exists()) {
        logger.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                "PayeeServiceThread --- invokePayeeProcessing --- Creating Log File Folder(s)--->" +
                new File(logPath.toString()).mkdirs());
      }
      /**
       * getting ftp information for Payee from DB
       */
      logger.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "invokePayeeProcessing --- Call <getPayeeFtpInfo> to get FTP related Payee Info");
      ftpVo = payeeDao.getPayeeFtpInfo();

      if (((Constants.PAYEE_OUTPUT_PATH != null) &&
           (Constants.PAYEE_OUTPUT_PATH.trim().length() > 0)) &&
          ((!ftpVo.getFtpLocalPath().equals("")) &&
           (ftpVo.getFtpLocalPath() != null))) {

        ouputPath.append(ftpVo.getFtpLocalPath() + File.separator +
                         Constants.PAYEE_OUTPUT_PATH + File.separator +
                         instanceName + File.separator +
                         Constants.DOWNLOAD_FOLDER);

        logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                "PayeeServiceThread --- invokePayeeProcessing --- Output Path to be passed--->" +
                ouputPath.toString());

//        Constants.PAYEE_FTP_DOWNLOAD_FOLDER = ouputPath.toString();
        if (!new File(ouputPath.toString()).exists()) {
          logger.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                  "PayeeServiceThread --- invokePayeeProcessing --- Creating OutPut Path Folder(s)--->" +
                  new File(ouputPath.toString()).mkdirs());
        }
      }
      else {
        logger.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                "PayeeServiceThread --- invokePayeeProcessing --- PayeeServiceThread Output Path value does not exist in DB LocalPath and Configuration file-->" +
                Constants.PAYEE_OUTPUT_PATH);

        throw new Exception(
            "PayeeServiceThread Path value does not exist in configuration file--->" +
            Constants.PAYEE_OUTPUT_PATH);
      }
      /**************************************************
       * Payee File Downloading
       **************************************************/

      ftpServer = new I2cFTP(ftpVo);

      logger.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "Check isftpAllowed --: " + ftpVo.getIsFtpAllowed());

      if (ftpVo.getIsFtpAllowed().equals("Y")) {
        logger.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                "invokePayeeProcessing --- Start Connection with FTP...");

        if (ftpServer.connect()) {

          String payeeFileName = new String(ftpVo.getFilePreFix() +
                                            ftpVo.getFilePostFix());
          ouputPath.append(File.separator + payeeFileName);

          logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                  " Start Download Payee File from FTP.." +
                  "\nLocal output Path with File Name for Downloading ----> " +
                  ouputPath.toString());

          isDownload = ftpServer.download(ouputPath.toString(),
                                          payeeFileName);

          logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "<isPayeeFile> Downloaded ------> " + isDownload);

          ftpServer.disconnect();

          if (isDownload) {
            movePath = ftpVo.getFtpLocalPath() + File.separator +
                Constants.PAYEE_OUTPUT_PATH + File.separator +
                instanceName + File.separator +
                Constants.PROCESSED_FOLDER;
            // if Payee File exist in Download and Processed
            boolean fileInDownload = new File(ouputPath.toString()).exists();
            boolean fileInProcessed = new File(movePath + File.separator + payeeFileName).exists();

            if (fileInDownload && fileInProcessed) {

              returnMsg = new StringBuffer();
              // File Comparison between Download folder and Process Folder
              boolean isDifferent = com.i2c.utils.FileUtil.compareFiles(
                  ouputPath.toString(),
                  movePath + File.separator +
                  payeeFileName);
              // TRUE if files are same
              if (isDifferent) {
                returnMsg.append(
                    "\nNew and Old Payee information files is Same....." +
                    "\nDownloaded file contains NO change, So No update in existing Payee Data in DataBase.....\n");

                logger.log(LogLevel.getLevel(Constants.LOG_FINE),
                        "PayeePopulator--- Moving payee File to ---" + movePath);
                // check for Processed folder existance
                boolean processedExist = new File(movePath).exists();

                if (processedExist == false) {
                  logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                             "payeeInstancePopulation --- Creating Payee Processed File Folder--->" +
                             new File(movePath + File.separator + payeeFileName).mkdirs());
                }

                boolean fileMoved = CommonUtilities.moveFileToThisFolder(
                    ouputPath.toString(),
                    movePath,
                    payeeFileName);

                if (fileMoved){
                  logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                             "PayeePopulator--- After Same File Comparison -- Payee file moved Sucessfully..");
                }

                /**
                 * delete the file in download folder after moving it to Processed
                 */
                sucessDel = (new File(ouputPath.toString())).delete();
                if (sucessDel) {
                  logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                          "PayeePopulator--- newly downloaded file deleted Sucessfully..");
                }
                logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                        "PayeeServiceThread --- run --- Updating the NEXT Schedule Date");
                payeeDao.updateNextSchedule();
              } // Payee old and new Files are not Same
              else {
                logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                        "--->>New Payee Data Exist in newly Downloaded File against OLD file\n" +
                        "<---> Updation is required according to New Payee File<<--");

                returnMsg = payeeInstancePopulation(dbConn,
                    payeeDao,
                    logPath,
                    ouputPath,
                    movePath,
                    payeeFileName);
              }
            } // Payee old File not found in Processed folder
            else {
              logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                         "----> Payee Info File NOT Found in <Processed> <---- Insert Payee Info data...");
              returnMsg = payeeInstancePopulation(dbConn,
                                                  payeeDao,
                                                  logPath,
                                                  ouputPath,
                                                  movePath,
                                                  payeeFileName);
            }
          }
          else {
            throw new Exception(
                "Exception --- Payee File Download FAIL from FTP server...");
          }
        }
        else {
          throw new IOException(
              "IOException --- FTP server Connection FAIL...");
        }
      }
    }
    catch (Exception ex) {
      logger.log(LogLevel.getLevel(Constants.LOG_WARNING),
              "PayeeServiceThread --- invokePayeeProcessing ---" +
              " Exception in processing of Payee File Processing--->" +
              ex.toString());
      ex.printStackTrace();
      throw new Exception(
          "Exception in processing of Payee Service Thread--->" + ex.toString());

    }
    return returnMsg;
  } //end method

  private StringBuffer payeeInstancePopulation(Connection dbConn,
                                               PayeeServiceDAO payeeDao,
                                               StringBuffer logPath,
                                               StringBuffer ouputPath,
                                               String movePath,
                                               String payeeFileName) throws
      Exception {

    boolean exists = false;
    StringBuffer returnMsg = new StringBuffer();
    /*********************** For master instance **********************************/
    logger.log(LogLevel.getLevel(Constants.LOG_INFO),
            ">>>>START Populating Payee for <Master Instance>");

    PayeePopulator(dbConn, payeeDao, logPath, ouputPath, movePath, ouputPath.toString());

    logger.log(LogLevel.getLevel(Constants.LOG_INFO),
            ">>>>>>END Populating Payee for <Master Instance>");
    /********************* Change the date for next day *************************/
    logger.log(LogLevel.getLevel(Constants.LOG_INFO),
            "PayeeServiceThread --- payeeInstancePopulation() --- Updating Payee Scheduler Date to Next Day");
    payeeDao.updateNextSchedule();

    returnMsg.append(
        "Payee Information sucessfully Populated/Updated in : master_instance\n");

    /********* for all client's instances --- Getting the Instance Name List ******/
    Iterator instanceConnPoolObj = LoadProperties.instanceConnectionsTable.
        values().iterator();

    DbConnectionInfoVO connObj = null;

    logger.log(LogLevel.getLevel(Constants.LOG_INFO),
            "------> Start Population Payee Info for Clients Instances <------");

    while (instanceConnPoolObj.hasNext()) {
      connObj = (DbConnectionInfoVO) instanceConnPoolObj.next();
      Class.forName(Constants.DB_DRIVER_NAME);

      logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                 "<--- Client Connection Information --->" +
                 "<--- Connection Name --->" + connObj.getConnectionName() +
                 "<--- DB Driver Name --->" + Constants.DB_DRIVER_NAME +
                 "<--- Connection String --->" + connObj.getConnectionString() +
                 "<--- User ID --->" + connObj.getUserID() + "<--------->");

      Connection connClientInst = DriverManager.getConnection(
          connObj.getConnectionString(), connObj.getUserID(),
          connObj.getPasswod());

      logger.log(LogLevel.getLevel(Constants.LOG_INFO),
              "<---DATA BASE Connection created Sucessfully for Client Instance --->" +
              connObj.getConnectionName() +"<----------\n\n"+
              ">****>>START Populating Payee for <<" +
              connObj.getConnectionName() +
              ">> Instance <****>");

      PayeePopulator(connClientInst, payeeDao, logPath, ouputPath, movePath, payeeFileName);

      logger.log(LogLevel.getLevel(Constants.LOG_INFO),
              ">****>>END Populating Payee for <<" +
              connObj.getConnectionName() +
              ">> Instance <****>");
      returnMsg.append(
          "\nPayee Information sucessfully Populated/Updated in : " +
          connObj.getConnectionName());
    } //end while

    logger.log(LogLevel.getLevel(Constants.LOG_INFO),
            "payeeInstancePopulation--- Moving Payee File to ---> " + movePath);

    String payeeFileInProcessed = movePath + File.separator + payeeFileName;

    exists = (new File(payeeFileInProcessed)).exists();

    if (exists == true) {
      // delete the Old Payee file from "Processed" folder
      boolean success = (new File(payeeFileInProcessed)).delete();
      // IF old file deleted sucessfully then move Payee file from <Download> to
      if (success) {
        logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                   "payeeInstancePopulation--- Payee File Deleted from <Processed> folder Sucessfully....");

        // File moved to processed
        boolean fileMovedToProcessed = CommonUtilities.moveFileToThisFolder(
            ouputPath.toString(), movePath, payeeFileName);
        if (fileMovedToProcessed){
          logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                     "payeeInstancePopulation--- After File Deletion from <Processed> --- Payee File Moved from <Download> to <Processed> folder Sucessfully..");
        }
      } else {
        logger.log(LogLevel.getLevel(Constants.LOG_SEVERE),
                   "------>Payee File in Processed Folder not Deleted<------\n" +
                   "New Payee file not moved to Processed folder");
      }
    } else {
      boolean processedExist = new File(movePath).exists();

      if (processedExist == false) {
        logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                   "payeeInstancePopulation --- Creating Payee Processed File Folder--->" +
                   new File(movePath).mkdirs());
      }
      // File or directory does not exist
      boolean fileMoved = CommonUtilities.moveFileToThisFolder(
            ouputPath.toString(), movePath, payeeFileName);
      if (fileMoved){
        logger.log(LogLevel.getLevel(Constants.LOG_INFO),
                   "payeeInstancePopulation--- File Not Exist in <Processed> folder \n" +
                   "---->Payee File Moved from <Download> to <Processed> folder Sucessfully<---");
      }
    }

  return returnMsg;
}

  private void PayeePopulator(Connection dbConn,
                              PayeeServiceDAO payeeDao,
                              StringBuffer logPath,
                              StringBuffer ouputPath,
                              String movePath,
                              String payeeFileName) throws Exception {

    logger.log(LogLevel.getLevel(Constants.LOG_INFO),
            "PayeePopulator --- Payee File Name --->> " + payeeFileName +
            "PayeePopulator--- Calling Payee API :------->");
    /* Logger getInstance Moved to main function - HRA*/
//    // Payee API log Path
//    String payeeLogPath = Constants.LOG_FILE_PATH + Constants.PAYEE_SERVICE_API;
//
//    Logger payeeApiLogger = I2cLogger.getInstance(payeeLogPath +
//                                                  File.separator +
//                                                  Constants.PAYEE_SERVICE_API + "-%g.log",
//                                                  Constants.LOG_FILE_SIZE,
//                                                  Constants.LOG_FILE_NO,
//                                                  this.getName());
//
//    boolean logPathExist = new File(payeeLogPath).exists();
//
//    if (logPathExist == false) {
//      logger.log(LogLevel.getLevel(Constants.LOG_CONFIG),
//                 "PayeePopulator --- Creating Log File Folder for PayeeServiceAPI--->" +
//                 new File(logPath.toString()).mkdirs());
//    }

    logger.log(LogLevel.getLevel(Constants.LOG_INFO),
               "i2cLogger Status--->" + payeeApiLogger +
               "\nPayeePopulator---> initilizing <PayeeMapper()> :-->");

    PayeeMapper mapper = new PayeeMapper(instanceName,
                                         dbConn,
                                         logPath.toString() + File.separator +
                                         "PayeeAPI",
                                         ouputPath.toString(),
                                         payeeApiLogger);

    logger.log(LogLevel.getLevel(Constants.LOG_INFO),
               "PayeePopulator--- Calling <payeePopulater()> :-->");

    mapper.payeePopulater();

    logger.log(LogLevel.getLevel(Constants.LOG_INFO),
               "PayeePopulator--- Scucesfully Executed........");

  }

  private java.util.Date getCurrentDate() {
    java.util.Date currDate = null;
    try {
      logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
              " Getting current Date ");
      currDate = new java.util.Date();
    }
    catch (Exception ex) {
      logger.log(LogLevel.getLevel(Constants.LOG_WARNING),
          " Exception in Getting current Date --->" + ex);
    }
    return currDate;
  }

  private void testConnection(Connection dbConn) throws SQLException {
    StringBuffer query = new StringBuffer();
    Statement stmt = null;

    try {
      logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
              " Method for testing database conenction ");

      query.append("select business_date from system_variables");
      stmt = dbConn.createStatement();
      stmt.executeQuery(query.toString());
      stmt.close();
    }
    catch (SQLException ex) {
      logger.log(LogLevel.getLevel(Constants.LOG_FINEST),
              " Exception in testing database conenction --->" + ex +
              "<---Error Code--->" + ex.getErrorCode());
      if (dbConn != null) {
        dbConn.close();
      }
      throw new SQLException("Exception in testing database connection...");
    }
    finally {
      if (stmt != null) {
        stmt.close();
        stmt = null;
      }
    }
  }

}
