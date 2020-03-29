package com.i2c.service.billpaymentschedularservice.AdjestmentService;

import java.io.*;
import java.sql.*;
import java.util.logging.*;

import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.service.*;
import com.i2c.billpayment.jobs.*;
import com.i2c.service.billpaymentschedularservice.FTPService.*;
import com.i2c.service.billpaymentschedularservice.monitor.*;
import com.i2c.service.util.*;
import java.util.ArrayList;

public class AdjustmentServiceThread {
  private String instanceName = null;
  private PaymentServiceMonitor monitorThread = null;
  private Logger lgr = null;

  public AdjustmentServiceThread(String instanceName,
                                 PaymentServiceMonitor monitorThread,
      Logger adjustmentLgr) {
    this.instanceName = instanceName;
    this.monitorThread = monitorThread;
    this.lgr = adjustmentLgr;
  }

  public void run() {
    process();
  }

  public boolean process(){

    AdjustmentServiceDAO adjustmentServiceDAO = null;
    Connection dbConn = null;
    boolean batchTime = false;
    StringBuffer adjustmentStatus = null;
    AdjustmentServiceMailer adjustmentMailer = null;
    AdjustmentInfoVO nextSchedule = null;
    try {
      lgr.log(LogLevel.getLevel(
          Constants.LOG_FINEST),
                          "AdjustmentServiceThread -- Method Name: run --- AdjustmentService Thread is started");

      lgr.log(LogLevel.getLevel(
          Constants.LOG_FINEST),
                          "AdjustmentServiceThread -- Method Name: run --- Getting Database Connection for instance --->" +
                          instanceName);
      dbConn = DatabaseHandler.getMasterConnection("AdjustmentServiceThread",
          instanceName,
          Constants.CARD_INSTANCE_DB_CONN);
      adjustmentServiceDAO = new AdjustmentServiceDAO(instanceName, dbConn, lgr);

      while (true) {

        try {
          try {
            lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "AdjustmentServiceThread -- Method Name: run --- Calling method for testing database connection");
            testConnection(dbConn);
          }
          catch (SQLException tstConEx) {
            lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "AdjustmentServiceThread -- Method Name: run --- Exception in testing database connection -- Creating new Connection");
            try {
              dbConn = DatabaseHandler.getMasterConnection(
                  "AdjustmentServiceThread",
                  instanceName,
                  Constants.CARD_INSTANCE_DB_CONN);

              adjustmentServiceDAO = new AdjustmentServiceDAO(instanceName,
                  dbConn,
                  lgr);
            }
            catch (Exception ex) {
              lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                  "ACHLoadBatchReturnServiceThread  -- Method Name: run --- Exception in creating new Connection");
            }
          }
          catch (Exception tstConEx) {
            lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "AdjustmentServiceThread -- Method Name: run --- Exception in testing database connection -- Creating new Connection");
            try {
              dbConn = DatabaseHandler.getMasterConnection(
                  "AdjustmentServiceThread",
                  instanceName,
                  Constants.CARD_INSTANCE_DB_CONN);

              adjustmentServiceDAO = new AdjustmentServiceDAO(instanceName,
                  dbConn,
                  lgr);
            }
            catch (Exception ex) {
              lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG), "AdjustmentServiceThread  -- Method Name: run --- Exception in creating new Connection");
            }
          }

          lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                              " \n\nGetting current date time for updating ADJUSTMENT_SRV monitoring Date Time ");
          java.util.Date currDate = getCurrentDate();
          lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                              " \n\nUpdating ADJUSTMENT_SRV Monitoring Date Time --- Current Date Time Got--->" +
                              currDate);
          monitorThread.setMonitorDateTime(currDate);

          // get AdjsutmentSleepTime from DB
          boolean isTrue = adjustmentServiceDAO.adjustmentSleepTime(Constants.PROCESSOR_ID);
          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  " get adjustmentSleepTime from DB and set ADJUSTMENT_SLEEP_TIME -- > " + isTrue);

          // Check Service allowed for Instance
          if (adjustmentServiceDAO.processScheduler(dbConn,
              Constants.USE_BILL_ADJUSTMENT_PROCESSING)) {
            batchTime = adjustmentServiceDAO.checkScheduleTime();
            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "AdjustmentServiceThread --- run --- Adjustment received from checkScheduleTime ---> " +
                    batchTime);
            if (batchTime) {
              lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "AdjustmentServiceThread --- run --- Invoking Bill Adjustment File processing");
              try {
                /*************************************************/
                lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        "Get DB Connection for <card_inst> for Processing respose ");
//                cardInstDBConn = DatabaseHandler.getConnection("AdjustmentServiceThread","card_inst");

                lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                        ">>>>START calling <invokeAdjustmentProcessing> to Process Adjustment file... ");
                adjustmentStatus = invokeAdjustmentProcessing(dbConn,
                    adjustmentServiceDAO);
                lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                        ">>>>END calling <invokeAdjustmentProcessing> to Process Adjustment file... ");

                /*************************************************/
                if (adjustmentStatus == null){
                  lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        "AdjustmentServiceThread --- run --- Sending Adjustment Download Fail email");

//                adjustmentMailer = new AdjustmentServiceMailer(Constants.
//                                                               ADJUSTMENT_DOWNLOAD_FAIL,
//                                                               instanceName, null,
//                                                               "Adjustment File Not available on Processor FTP site OR Unable to Download from FTP",
//                                                               dbConn,
//                                                               lgr);
//                adjustmentMailer.sendEmailNotification();

                lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        "AdjustmentServiceThread --- run --- Adjustment File not Download --- Return FALSE.... ");
//                        "AdjustmentServiceThread --- run --- Adjustment File not Download -- Sleeping for Adjustment Sleep time" +
//                    Constants.ADJUSTMENT_SLEEP_TIME);
//                sleep(Constants.ADJUSTMENT_SLEEP_TIME); //Close <dbConn>
                dbConn.close();
                return false;
                } else {
                  lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                          "AdjustmentServiceThread --- run --- Sending success email");

                  adjustmentMailer = new AdjustmentServiceMailer(Constants.
                                                                 ADJUSTMENT_SRV_SUCC,
                                                                 instanceName, null,
                                                                 adjustmentStatus.toString(),
                                                                 dbConn,
                                                                 lgr);
                  adjustmentMailer.sendEmailNotification();
                  dbConn.close();
                  return true;
                }
              }
              catch (Exception procEx) {
                lgr.log(LogLevel.getLevel(Constants.LOG_SEVERE),
                        "AdjustmentServiceThread --- run --- Exception in processing of Bill Adjustment File Processing --- Emailing to Admin-->" +
                        procEx);
                adjustmentMailer = new AdjustmentServiceMailer(Constants.ADJUSTMENT_SRV_FAIL,
                    instanceName,
                    Constants.getMachineIP(),
                    procEx.toString(),
                    CommonUtilities.getStackTrace(procEx),
                    dbConn,
                    lgr);
                adjustmentMailer.sendEmailNotification();

                lgr.log(LogLevel.getLevel(Constants.LOG_SEVERE),
                        "........Return FALSE after Emailing to Admin........");
//                        "Going to Sleep after Exception for : " +
//                        Constants.ADJUSTMENT_SLEEP_TIME);
//                sleep(Constants.ADJUSTMENT_SLEEP_TIME);
                dbConn.close();
                return false;
              }
            } //////////////////////////
            else {
              dbConn.close();
              return false;
          //              nextSchedule = adjustmentServiceDAO.fetchNextSchedule();
//              if (nextSchedule != null) {
//                lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
//                        "AdjsutmentServiceThread -- Method Name: run --- (GOT Next Schedule Info) --->\n" +
//                        "ScheduledDateTime--: " +
//                        nextSchedule.getScheduledDateTime() +
//                        " --CurrentDateTime--: " +
//                        nextSchedule.getCurrentDateTime() +
//                        " --TimeDifference--: " +
//                        nextSchedule.getTimeDifference());
//                if (nextSchedule.getTimeDifference() != -1 &&
//                    nextSchedule.getTimeDifference() < 0) {
//                  lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
//                          "AdjsutmentServiceThread -- Next Schedule Time is Negative ---  Sleep for AdjustmentSleepTime ---> " +
//                          nextSchedule.getTimeDifference());
////                  sleep(Constants.ADJUSTMENT_SLEEP_TIME);
////                  continue;
//                }
//                else if (nextSchedule.getTimeDifference() != -1 &&
//                         nextSchedule.getTimeDifference() <
//                         Constants.ADJUSTMENT_SLEEP_TIME) {
//                  lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
//                          "AdjsutmentServiceThread -- No Adjustment Service Tasks to schedule ---  Sleeping for ---> " +
//                          nextSchedule.getTimeDifference());
////                  sleep(nextSchedule.getTimeDifference());
//                } ///////////////////////////////////
//                else { //end if check schTime arrived
//              lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
//                      "AdjsutmentServiceThread --- run --- Adjustment File processing Time not arrived -- Sleeping for default time");
////              sleep(Constants.ADJUSTMENT_SLEEP_TIME);
//            }
//          }
        }
      }
      else { //end if check isAllowed
        lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                "AdjustmentServiceThread --- run --- Adjustment File processing is not allowed for this instance");
//        sleep(Constants.ADJUSTMENT_SLEEP_TIME);
        dbConn.close();
        return false;
      }
    }
    catch (Exception ex) { //end inner try
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                  "AdjustmentServiceThread --- Excpetion in Bill Adjustment File processing--->" +
                  ex);
//          sleep(Constants.ADJUSTMENT_SLEEP_TIME);
      dbConn.close();
      return false;
    } //end inner catch
      } //end while
    }
    catch (Exception ex) { //end outer try
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "AdjustmentServiceThread --- Outer Catch -- Excpetion in Adjustment Service Thread--->" +
              ex);
      return false;
    }
  } //end run

  private StringBuffer invokeAdjustmentProcessing(Connection dbConn,
                                                  AdjustmentServiceDAO
                                                  adjustmentDao) throws
      Exception {

    boolean isDownload = false;
    FTPInfoVO ftpVo = null;
    AdjustmentFileHandler adjustmentHandler = null;
    I2cFTP ftpServer = null;
    StringBuffer logPath = new StringBuffer();
    StringBuffer returnMsg = null;
    StringBuffer outputPath = new StringBuffer();
    String movePath = new String();
    String adjustmentFileName = null;
    String decryptedPath = null;
    String inAdjFileName = null;
    int fileId = -1;
        //    AdjustmentServiceMailer adjustmentMailer = null;

    try {
      logPath.append(Constants.LOG_FILE_PATH + //File.separator +
                     instanceName + File.separator +
                     Constants.ADJUSTMENT_SERVICE);
      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                          "AdjustmentServiceThread --- invokeAdjustmentProcessing --- Log File Path to be passed--->" +
                          logPath);

      if (!new File(logPath.toString()).exists()) {
        lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                            "AdjustmentServiceThread --- invokeAdjustmentProcessing --- Creating Log File Folder(s)--->" +
                            new File(logPath.toString()).mkdirs());
      }
      /**
       *  Get the Adjustment Download related info from DB
       */
      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                          "getting ftp information from DB");
      ftpVo = adjustmentDao.getAdjustmentFtpInfo();

      if (((Constants.ADJUSTMENT_OUTPUT_PATH != null) &&
          (Constants.ADJUSTMENT_OUTPUT_PATH.trim().length() > 0)) &&
          ((!ftpVo.getFtpLocalPath().equals("")) &&
           (ftpVo.getFtpLocalPath() != null))) {
        outputPath.append(ftpVo.getFtpLocalPath() + File.separator +
                          Constants.ADJUSTMENT_OUTPUT_PATH + File.separator +
                          instanceName + File.separator +
                          Constants.DOWNLOAD_FOLDER);

        lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                            "AdjustmentServiceThread --- invokeAdjustmentProcessing --- Download Output Path to be passed--->" +
                            outputPath.toString());



        if (!new File(outputPath.toString()).exists()) {
          lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                              "AdjustmentServiceThread --- invokeAdjustmentProcessing --- Creating OutPut Path Folder(s)--->" +
                              new File(outputPath.toString()).mkdirs());
        }
      }
      else {
        lgr.log
            (LogLevel.getLevel(Constants.LOG_CONFIG),
             "invokeAdjustmentProcessing --- AdjustmentServiceThread Output Path value does not exist in configuration file-->" +
             Constants.ADJUSTMENT_OUTPUT_PATH);

        throw new Exception(
            "AdjustmentServiceThread Path value does not exist in configuration file--->" +
            Constants.ADJUSTMENT_OUTPUT_PATH);
      }
      /**************************************************
       * Adjustmenet File Downloading
       **************************************************/

      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "Initilizing FTP with ftp info from DB");
      ftpServer = new I2cFTP(ftpVo);

      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                          "Start Connecting FTP Server --> " +
                          ftpVo.getFtpAddress() +
                          "\nCheck isftpAllowed -- " + ftpVo.getIsFtpAllowed());

      if (ftpVo.getIsFtpAllowed().equals("Y")) {
        /* FTP Connected Sucessfully then download the file */
        if (ftpServer.connect()) {
          /********* Check for Seven Day old Adjustment Files **********/
          returnMsg = checkForPreviousFiles(adjustmentDao, ftpServer, ftpVo, dbConn,
                                            logPath, outputPath, returnMsg);

          /************ Check for bad Payment file on FTP ***********/
          boolean badExist = checkForBadPayment(ftpServer, ftpVo);
          /*********************************************************/
          if (badExist){
            throw new Exception ("Today Payment File marked as Bad. Adjustment Downloading Cancel");
          }

          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                              "FTP Server connected sucessfully....");
          /* Create Adjustment File Name for Today */
          adjustmentFileName = new String(ftpVo.getFilePreFix() + "." +
                                          CommonUtilities.getCurrentFormatDate(
                                              "MMddyyyy"));
          lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                              "Adjustment File Name to be downloaded ---> " +
                              adjustmentFileName);
          /* Put File name in the Down load Path */
          outputPath.append(File.separator + adjustmentFileName);
          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                              "---> START Downloading the Adjustment File" +
                              "\nAdjustment File download output Path with File Name ----> " +
                              outputPath.toString());

          try{
            isDownload = ftpServer.download(outputPath.toString(),
                                            adjustmentFileName);
          }catch(Exception e){
            lgr.log(
                LogLevel.getLevel(Constants.LOG_WARNING),
                "Exception During the Download of the Adjustment File " + e.toString());
            return returnMsg; //Close <dbConn>
          }
          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                              "---> END Downloading----isFile Downloaded --------> " +
                              isDownload);
          /* FTP Disconnected */
          ftpServer.disconnect();
          /*************************************/
          if (isDownload) {
            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                                "Adjustment File Download Sucessfully....");

            if (ftpVo.getIsPgp().equals("Y")){
              lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
                      "PGP Allowed for Adjustment file");

              decryptedPath = ftpVo.getFtpLocalPath() + File.separator +
                  Constants.ADJUSTMENT_OUTPUT_PATH + File.separator +
                  instanceName + File.separator +
                  Constants.DECRYPT_FOLDER;

              lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                                  "Decryption Path ---> " + decryptedPath);
              /* if decryption path does not exist then create it */
              if (!new File(decryptedPath).exists()) {
                lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "AdjustmentServiceThread --- invokeAdjustmentProcessing --- Creating Decrypted OutPut Path Folder(s)--->" +
                                    new File(decryptedPath).mkdirs());
              }
              lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                                  "Start Decrypting File at Path --> " +
                                  decryptedPath);
              if (decryptFileFromPgp(outputPath.toString(), decryptedPath, ftpVo,
                                     logPath.toString() + File.separator + "PgpApi")
                  && (ftpVo.getIsPgp().equals("Y"))) {

                lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                                    "Adjustment File Decrypted Sucessfully.....");

//                String adjInFileName = Constants.ADJUSTMENT_INFILE_PREFIX + "." +
//                    CommonUtilities.getJulienDate();
//
//                decryptedPath = decryptedPath + File.separator + adjInFileName;

                // check for the inner decrypt file path
                inAdjFileName = decryptInFilePathName(decryptedPath, dbConn);

              }
              else {
                throw new Exception("Enable to Decrypt the Adjustment File..");
              }

              lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
                      "----->Save File in DB With File ---> " + decryptedPath);

              /************Save in DB*********************/
              fileId = CommonUtilities.saveFileInBD(dbConn,
                                                    decryptedPath,
                                                    inAdjFileName,
                                                    Constants.ADJ_FILE_ID);
              /******************************************/

              lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                                  ">>>>START <AdjustmentFileHandler> with logpath---->" +
                                  logPath.toString() + File.separator +
                                  "BRProcessor");
              adjustmentHandler = new AdjustmentFileHandler(dbConn,
                                                            logPath.toString() +
                                                            File.separator + "BAProcessor",
                                                            decryptedPath);
            }// is_PGP_allowed else
            else {

        lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
                "------->Save File in DB from ---->" + outputPath.toString());


        /************Save in DB*********************/
        fileId = CommonUtilities.saveFileInBD(dbConn,
                outputPath.toString(),
                adjustmentFileName,
                Constants.ADJ_FILE_ID);
        /******************************************/


              lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                      ">>>>START <AdjustmentFileHandler> with logpath---->" +
                      logPath.toString() + File.separator +
                      "BRProcessor");

              adjustmentHandler = new AdjustmentFileHandler(dbConn,
                  logPath.toString() +
                  File.separator + "BAProcessor",
                  outputPath.toString());
        }
        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                            "Calling <processAdjustmentFile()> to process the Adjustment File ");
        adjustmentHandler.processAdjustmentFile();

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                            ">>>>END Processing <AdjustmentFileHandler> with logpath---->");
        if (returnMsg == null){
          returnMsg = new StringBuffer();
        }
        returnMsg.append("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        returnMsg.append("\nFile Name : ............... : " +
                         AdjustmentFileHandler.filsStatsObj.getFileName());
        returnMsg.append("\nTotal Amount : ............ : " +
                         AdjustmentFileHandler.filsStatsObj.getTotalAmount());
        returnMsg.append("\nTotal Batches : ........... : " +
                         AdjustmentFileHandler.filsStatsObj.getTotalBatches());
        returnMsg.append("\nTotal Failed Transaction :..: " +
                         AdjustmentFileHandler.filsStatsObj.
                         getTotalFailedTrans());
        returnMsg.append("\nTotal SucessFul Transaction : " +
                         AdjustmentFileHandler.filsStatsObj.
                         getTotalSuccessTrans());
        returnMsg.append("\nTotal Transaction : ....... : " +
                         AdjustmentFileHandler.filsStatsObj.getTotalTrans());
        returnMsg.append("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        /*****************************************/
            movePath = ftpVo.getFtpLocalPath() + File.separator +
                Constants.ADJUSTMENT_OUTPUT_PATH + File.separator +
                instanceName + File.separator +
                Constants.PROCESSED_FOLDER;

            lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Adjustment Processed File <moveFileToThisFolder> from encrypted folder " +
                                outputPath.toString() + " to Processed Folder " + movePath);

            if (CommonUtilities.moveFileToThisFolder(outputPath.toString(),
                movePath,
                adjustmentFileName)) {
              lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "Adjustment File moved to Processed Folder Sucessfully...");
            }

            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "AdjustmentServiceThread --- invokeAdjustmentProcessing --- Updating the NEXT Schedule Date");
            adjustmentDao.updateNextSchedule();
          } /////////////////////////////
          else {
            lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
                "Exception --- Adjustment File Download FAIL from FTP server...");
            return returnMsg;
          }
        }
        else {
          throw new IOException(
              "IOException --- FTP server Connection FAIL...");
        }
      }
    }
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
              "AdjustmentServiceThread --- invokeAdjustmentProcessing --- Exception in processing of Adjustment File Processing--->" +
              ex.getMessage() +
              "\nAdjustmentServiceThread Stack Trace: " + CommonUtilities.getStackTrace(ex));
      throw new Exception(
          "Exception in processing of Adjustment Service Thread--->" +
          ex);
    }
    return returnMsg;
  } //end method





  /**
   * Method Process Previous Adjustment Files
   * @param AdjustmentDao AdjustmentServiceDAO
   * @param ftpServer I2cFTP
   * @param FTPInfoVo ftpVo
   * @param dbConn Connection
   * @param logPath StringBuffer
   * @param outputPath StringBuffer
   * @return boolean
   * @throws Exception
   */
  private StringBuffer checkForPreviousFiles(AdjustmentServiceDAO adjustmentDao,
                                             I2cFTP ftpServer, FTPInfoVO ftpVo,
                                             Connection dbConn,
                                             StringBuffer logPath,
                                             StringBuffer outputPath,
                                             StringBuffer returnMsg) throws
      Exception {

    AdjustmentFileHandler adjustmentHandler = null;
    ArrayList dateList = new ArrayList();
    String oldAdjustmentFileName = null;
    boolean isOldFileDownload = false;
    boolean isFileExist = false;

    // Get old Adjustment Files Date Array List
    dateList = adjustmentDao.getOldAdjustmentFileDateFtp();
    returnMsg = null;
    for (int i = 0; i < dateList.size(); i++) {
      String oldAdjustmentFileDate = (String) dateList.get(i);
      ///////*****************************************////////
      // Make old Adjustment File name
      oldAdjustmentFileName = new String(ftpVo.getFilePreFix() + "." +
                                       oldAdjustmentFileDate);
      // check file on FTP dir list
      isFileExist = ftpServer.listFiles(oldAdjustmentFileName);

      // if file exist on FTP
      if (isFileExist) {
        lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
                "Old Adjustment File Name to be downloaded ---> " +
                oldAdjustmentFileName);

        /* Put old File name in the Download Path */
        outputPath.append(File.separator + oldAdjustmentFileName);

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "Adjustment File Download output Path with File Name ----> " +
                outputPath.toString());

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "---> START Downloading the Old Adjustment File");
        try {
          isOldFileDownload = ftpServer.download(outputPath.toString(),
                                                 oldAdjustmentFileName);
        }
        catch (Exception e) {
          e.printStackTrace();
          lgr.log(
              LogLevel.getLevel(Constants.LOG_WARNING),
              "Exception During the Download of the OLD Adjustment Files " +
              e.getMessage());
          returnMsg.append(
              "<----->Exception During the Download of the OLD Adjustment Files " +
              e.getMessage());
          return returnMsg;
        }

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "---> END Download----isOldResposeFile -- Downloaded --------> " +
                isOldFileDownload);
        /****************IS oldAdjustmentFileDownloaded *******************/
        if (isOldFileDownload) {
          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "Old Adjustment File Download Sucessfully....");
          // if PGP decryption of file isAllowed for the
          if (ftpVo.getIsPgp().equals("Y")) {
            lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
                    "PGP Allowed for Old Adjustment file");

            String decryptedPath = ftpVo.getFtpLocalPath() + File.separator +
                Constants.ADJUSTMENT_OUTPUT_PATH + File.separator +
                instanceName + File.separator +
                Constants.DECRYPT_FOLDER;

            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "Decryption Path for old Adjustment File---> " +
                    decryptedPath);
            /* if decryption path does not exist then create it */
            if (!new File(decryptedPath).exists()) {
              lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                      "checkForPreviousFiles()--- Creating Decrypted OutPut Path Folder(s)--->" +
                      new File(decryptedPath).mkdirs());
            }

            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "Start Decrypting Old File at Path --> " + decryptedPath);

            if (decryptFileFromPgp(outputPath.toString(),
                                   decryptedPath,
                                   ftpVo,
                                   logPath.toString() + File.separator +
                                   "PgpApi")) {

              lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                      "Adjustment File Decrypted Sucessfully.....");

              // Get the PGP inner file name from PGP Api and test it against
              // standrad if ok then return it with path else email
              decryptedPath = decryptInFilePathName(decryptedPath, dbConn);

              lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                      "File Decrypted Sucessfully...on Path -->" +
                      decryptedPath);
            } // respose File decrypted else
            else {
              throw new Exception(
                  "WARNING: Enable to Decrypt the OLD Adjustment File. OR PGP is not allowed for Old File");
            }
            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "Initilization <AdjustmentFileHandler> <With PGPAllowed> -- logpath---->" +
                    logPath.toString() + File.separator + "BAPror4oldFiles"
                    + "\nWith File ---> " + decryptedPath);

            /******************** Adjustment Handler API *******************/
            adjustmentHandler = new AdjustmentFileHandler(dbConn,
                logPath.toString() + File.separator + "BAPror4oldFiles",
                decryptedPath);

            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "<--->START <processOldAdjustmentFile()> to process the " +
                    decryptedPath + " ....Adjustment File ");

          } // if isPGP_allwod else
          else {

            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "Initilization <AdjustmentFileHandler> <WithOut PGPAllowed> -- logpath---->" +
                    logPath.toString() + File.separator + "BRPror4oldFiles"
                    + "\nWith File ---> " + outputPath.toString());
            /******************** Adjustment Handler API *******************/
            adjustmentHandler = new AdjustmentFileHandler(dbConn,
                logPath.toString() + File.separator + "BAPror4oldFiles",
                outputPath.toString());

            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "<--->START <processAdjustmentFile()> to process the " +
                    outputPath.toString() + " ....Adjustment File ");
          }

          /************* Method to call <processAdjustmentFile()> ************/

          adjustmentHandler.processAdjustmentFile();

          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  ">>>>END Processing <AdjustmentFileHandler> with logpath---->");

          returnMsg = new StringBuffer();
          returnMsg.append("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
          returnMsg.append("\nFile Name : ............... : " +
                           AdjustmentFileHandler.filsStatsObj.getFileName());
          returnMsg.append("\nTotal Amount : ............ : " +
                           AdjustmentFileHandler.filsStatsObj.getTotalAmount());
          returnMsg.append("\nTotal Batches : ........... : " +
                           AdjustmentFileHandler.filsStatsObj.getTotalBatches());
          returnMsg.append("\nTotal Failed Transaction :..: " +
                           AdjustmentFileHandler.filsStatsObj.
                           getTotalFailedTrans());
          returnMsg.append("\nTotal SucessFul Transaction : " +
                           AdjustmentFileHandler.filsStatsObj.
                           getTotalSuccessTrans());
          returnMsg.append("\nTotal Transaction : ....... : " +
                           AdjustmentFileHandler.filsStatsObj.getTotalTrans());
          returnMsg.append("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

          /*****************************************/
          String movePath = ftpVo.getFtpLocalPath() + File.separator +
              Constants.ADJUSTMENT_OUTPUT_PATH + File.separator +
              instanceName + File.separator +
              Constants.PROCESSED_FOLDER;

          lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                  "Adjustment Processed File <moveFileToThisFolder> from encrypted folder " +
                  outputPath.toString() + " to Processed Folder " + movePath);

          if (CommonUtilities.moveFileToThisFolder(outputPath.toString(),
              movePath,
              oldAdjustmentFileName)) {
            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "Adjustment File moved to Processed Folder Sucessfully...");
          }
        }
      }
    }
    return returnMsg;
  }





  /**
   * Check for PGP inner file name for standrad
   * @param decryptedPath String
   * @return String
   */
  private String decryptInFilePathName(String decryptedPath,
                                       Connection dbConn) throws
      Exception {

    String inFileName = null;

    String fileNameFromConfig = Constants.ADJUSTMENT_INFILE_PREFIX + "." +
        CommonUtilities.getJulienDate();

    if (Constants.ADJ_DECRYPT_INFILE_NAME.equalsIgnoreCase(fileNameFromConfig)) {

//      inFileName = decryptedPath + File.separator + fileNameFromConfig;
        inFileName = fileNameFromConfig;
        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "decryptInFilePathName --- Adjustment inner File Name matched at path" +
              inFileName);

    }
    else {

      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "decryptInFilePathName ---> Adjustment inner File Name NOT matched");

//      inFileName = decryptedPath + File.separator + Constants.ADJ_DECRYPT_INFILE_NAME;
      inFileName = Constants.ADJ_DECRYPT_INFILE_NAME;

      // Email
      String emailMsg = "Adjustment Pgp inFile Name not match" +
          " with standrad --- inFile Name ---->" +
          Constants.ADJ_DECRYPT_INFILE_NAME;

      AdjustmentServiceMailer adjustmentMailer = new AdjustmentServiceMailer(
          Constants.
          ADJUST_INFILE_FAIL,
          instanceName, null,
          emailMsg,
          dbConn,
          lgr);
      adjustmentMailer.sendEmailNotification();
    }

    return inFileName;
  }

  private boolean checkForBadPayment(I2cFTP ftpServer, FTPInfoVO ftpVo) throws
      Exception {
    boolean isExist = false;
    String paymentFileName = null;
    paymentFileName = ftpVo.getPaymentFilePrefix() +
        CommonUtilities.getCurrentFormatDate("MMddyyyy") + ".bad";

    isExist = ftpServer.listFiles(paymentFileName);

    return isExist;
  }

  private boolean decryptFileFromPgp(String downloadFilePath,
                                     String decryptedPath,
                                     FTPInfoVO ftpVo,
                                     String logPath) {

    PGPCryptographicService cs = new PGPCryptographicService(logPath);

    try {
      Constants.ADJ_DECRYPT_INFILE_NAME =
          cs.verifyAndDecryptPGPFile(downloadFilePath,
                                     Constants.SECRET_KEY_PATH,
                                     ftpVo.getPgpPassPhrase(),
                                     Constants.VERIFY_PUBLIC_KEY_PATH,
                                     decryptedPath);
    }
    catch (PGPException ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
              " PGP Exception in decryptFileFromPgp --->" + ex);
      return false;
    }
    catch (Exception ex1) {
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
              "Exception in decryptFileFromPgp --->" + ex1);
      return false;
    }
    return true;
  }

  private java.util.Date getCurrentDate() {
    java.util.Date currDate = null;
    try {
      lgr.log(
          LogLevel.
          getLevel(Constants.LOG_FINEST),
          " Getting current Date ");
      currDate = new java.util.Date();
    }
    catch (Exception ex) {
      lgr.
          log(
              LogLevel.getLevel(Constants.LOG_WARNING),
              " Exception in Getting current Date --->" + ex);
    }
    return currDate;
  }

  private void testConnection(Connection dbConn) throws SQLException {
    StringBuffer query = new StringBuffer();
    Statement stmt = null;

    try {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              " Method for testing database conenction ");

      query.append("select business_date from system_variables");
      stmt = dbConn.createStatement();
      stmt.executeQuery(query.toString());
      stmt.close();
    }
    catch (SQLException ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
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
