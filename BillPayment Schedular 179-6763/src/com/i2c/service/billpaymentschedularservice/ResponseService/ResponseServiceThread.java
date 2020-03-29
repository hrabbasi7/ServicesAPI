package com.i2c.service.billpaymentschedularservice.ResponseService;

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

public class ResponseServiceThread {
  private Logger lgr = null;
  private String instanceName = null;
  private PaymentServiceMonitor monitorThread = null;

  public ResponseServiceThread(String instanceName,
                               PaymentServiceMonitor monitorThread,
                               Logger responseLgr) {
    this.instanceName = instanceName;
    this.monitorThread = monitorThread;
    this.lgr = responseLgr;
  }

  public void run() {
    process();
  }

  public boolean process(){
    ResponseServiceDAO responseServiceDAO = null;
    Connection dbConn = null;
    boolean batchTime = false;
    StringBuffer responseTransStatus = null;
    ResponseServiceMailer responseMailer = null;
//    ResponseInfoVO nextSchedule = null;
    try {

      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "ResponseServiceThread -- Method Name: run --- ResponseService Thread is started");

      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "ResponseServiceThread -- Method Name: run --- Getting Database Connection for instance --->" +
              instanceName);
      dbConn = DatabaseHandler.getMasterConnection("ResponseServiceThread",
          instanceName,
          Constants.CARD_INSTANCE_DB_CONN);
      responseServiceDAO = new ResponseServiceDAO(instanceName, dbConn, lgr);

      while (true) {

        try {
          try {
            lgr.log(LogLevel.
                    getLevel(Constants.LOG_CONFIG), "ResponseServiceThread -- Method Name: run --- Calling method for testing database connection");
            testConnection(dbConn);
          }
          catch (SQLException tstConEx) {
            lgr.log(LogLevel.
                    getLevel(Constants.LOG_CONFIG), "ResponseServiceThread -- Method Name: run --- Exception in testing database connection -- Creating new Connection");
            try {
              dbConn = DatabaseHandler.getMasterConnection(
                  "ResponseServiceThread",
                  instanceName, Constants.CARD_INSTANCE_DB_CONN);
              responseServiceDAO = new ResponseServiceDAO(instanceName, dbConn,
                  lgr);
            }
            catch (Exception ex) {
              lgr.log(LogLevel.getLevel(Constants.
                                        LOG_CONFIG), "ACHLoadBatchReturnServiceThread  -- Method Name: run --- Exception in creating new Connection");
            }
          }
          catch (Exception tstConEx) {
            lgr.log(LogLevel.
                    getLevel(Constants.LOG_CONFIG), "ResponseServiceThread -- Method Name: run --- Exception in testing database connection -- Creating new Connection");
            try {
              dbConn = DatabaseHandler.getMasterConnection(
                  "ResponseServiceThread",
                  instanceName, Constants.CARD_INSTANCE_DB_CONN);
              responseServiceDAO = new ResponseServiceDAO(instanceName, dbConn,
                  lgr);
            }
            catch (Exception ex) {
              lgr.log(LogLevel.getLevel(Constants.
                                        LOG_CONFIG), "ResponseServiceThread  -- Method Name: run --- Exception in creating new Connection");
            }
          }
          lgr.log(LogLevel.
                  getLevel(Constants.LOG_FINEST),
                  " \n\nGetting current date time for updating RESPONSE_SRV monitoring Date Time ");
          java.util.Date currDate = getCurrentDate();
          lgr.log(LogLevel.
                  getLevel(Constants.LOG_FINEST),
                  " \n\nUpdating RESPONSE_SRV Monitoring Date Time --- Current Date Time Got--->" +
                  currDate);
          monitorThread.setMonitorDateTime(currDate);

          /***** Get the response Sleep Time from Db and populate the Constant*****/
          boolean isTrue = responseServiceDAO.responseSleepTime(Constants.PROCESSOR_ID);

          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "get responseSleepTime and populate the RESPONSESLEEPTIME ---> " + isTrue);
          /******************************************************************/

          // Check Service allowed for Instance
          if ((responseServiceDAO.processScheduler(dbConn, Constants.USE_BILL_RESPONSE_PROCESSING))
              && CommonUtilities.checkPrimaryScheduler()) {
            batchTime = responseServiceDAO.checkScheduleTime();
            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "ResponseServiceThread --- run --- Response received from checkScheduleTime ---> " +
                    batchTime);
            if (batchTime) {
              lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "ResponseServiceThread --- run --- Invoking Bill Response File processing");
              try {
                /*************************************************/
                lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        ">>>START calling <invokeResponseProcessing> to Process response file... ");

                responseTransStatus = invokeResponseProcessing(dbConn,
                    responseServiceDAO);

                lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        ">>>END calling <invokeResponseProcessing> after Processing response file... ");

                /***************************************************/
                if (responseTransStatus == null ||
                    responseTransStatus.toString().equalsIgnoreCase("")) {
                  lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                          "AdjustmentServiceThread --- run --- Sending Response Download Fail email");
                  // Stop This Emailing
//                  responseMailer = new ResponseServiceMailer(Constants.
//                      RESPONSE_DOWNLOAD_FAIL,
//                      instanceName, null,
//                      "Response File Not available on Processor FTP site OR Unable to Download from FTP",
//                      dbConn,
//                      lgr);
//                  responseMailer.sendEmailNotification();

                  lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                          "ResponseServiceThread --- run --- Response File not Download -- Return FALSE");
//                          "ResponseServiceThread --- run --- Response File not Download -- Sleeping for Response Sleep time" +
//                          Constants.RESPONSE_SLEEP_TIME); // Close <dbConn>
                  dbConn.close();
                  return false;
                  //sleep(Constants.RESPONSE_SLEEP_TIME);

                }
                else {

                  lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                          "ResponseServiceThread --- run --- Sending success email");

                  responseMailer = new ResponseServiceMailer(Constants.
                      RESPONSE_SRV_SUCC, instanceName, null,
                      responseTransStatus.toString(),
                      dbConn,
                      lgr);
                  responseMailer.sendEmailNotification();
                  dbConn.close();
                  return true;
                }
              }
              catch (Exception procEx) {
                lgr.log(LogLevel.getLevel(Constants.LOG_SEVERE),
                        "ResponseServiceThread --- run --- Exception in processing of Bill Response File Processing --- Emailing to Admin-->" +
                        procEx.getMessage());
                responseMailer = new ResponseServiceMailer(Constants.
                    RESPONSE_SRV_FAIL,
                    instanceName,
                    Constants.getMachineIP(),
                    procEx.toString(),
                    CommonUtilities.getStackTrace(procEx),
                    dbConn,
                    lgr);
                responseMailer.sendEmailNotification();

                lgr.log(LogLevel.getLevel(Constants.LOG_SEVERE),
                        "Return FALSE after Email to Admin");
//                        "Going to Sleep after Exception in ResponseServiceThread : " +
//                        Constants.RESPONSE_SLEEP_TIME);
//                sleep(Constants.RESPONSE_SLEEP_TIME);
                dbConn.close();
                return false;
              }
            } //end if check schTime arrived ////////
            else {
              dbConn.close();
              return false;
//              nextSchedule = responseServiceDAO.fetchNextSchedule();
//              if (nextSchedule != null) {
//                lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
//                        "ResponseServiceThread -- Method Name: run --- (GOT Next Schedule Info) --->" +
//                        "ScheduledDateTime--: " +
//                        nextSchedule.getScheduledDateTime() +
//                        " --CurrentDateTime--: " +
//                        nextSchedule.getCurrentDateTime() +
//                        " --TimeDifference--: " +
//                        nextSchedule.getTimeDifference());
//                if (nextSchedule.getTimeDifference() != -1 &&
//                    nextSchedule.getTimeDifference() < 0) {
//                  lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
//                          "ResponseServiceThread -- Next Schedule Time is Negative ---  Sleep for ResponseS ---> " +
//                          nextSchedule.getTimeDifference());
//                  sleep(Constants.RESPONSE_SLEEP_TIME);
////                  continue;
//                }
//                else if (nextSchedule.getTimeDifference() != -1 &&
//                         nextSchedule.getTimeDifference() <
//                         Constants.RESPONSE_SLEEP_TIME) {
//                  lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
//                          "ResponseServiceThread -- No Response Service Tasks to schedule ---  Sleeping for ---> " +
//                          nextSchedule.getTimeDifference());
//                  sleep(nextSchedule.getTimeDifference());
//                } ///////////////////////////////////
//                else {
//                  lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
//                          "ResponseServiceThread --- run --- Response File processing Time not arrived -- Sleeping for default time");
//                  sleep(Constants.RESPONSE_SLEEP_TIME);
//                }
//              }
            }
          }
          //end if check isAllowed
          else {
            dbConn.close();
            return false;
//            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
//                    "ResponseServiceThread --- run --- Response File processing is not allowed for this instance");
//            sleep(Constants.RESPONSE_SLEEP_TIME);
          }
        } //end inner try
        catch (Exception ex) {
          lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                  "ResponseServiceThread --- Excpetion in Bill Response File processing--->" +
                  ex);
          dbConn.close();
          return false;
//          sleep(Constants.RESPONSE_SLEEP_TIME);
        } //end inner catch
      } //end while
    } //end outer try
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "ResponseServiceThread --- Outer Catch -- Excpetion in Response Service Thread--->" +
              ex);
      return false;
    }
  } //end process

  private StringBuffer invokeResponseProcessing(Connection dbConn,
                                                ResponseServiceDAO responseDao) throws
      Exception {

    boolean isDownload = false;
    FTPInfoVO ftpVo = null;
    ResponseFileHandler responseHandler = null;
    I2cFTP ftpServer = null;
    StringBuffer logPath = new StringBuffer();
    StringBuffer returnMsg = new StringBuffer();
    StringBuffer outputPath = new StringBuffer();
    String movePath = new String();
    String responseFileName = null;
    String decryptedPath = null;
    int fileId = -1;
    String inRespFileName = null;

        try {
      /* Create Log Path for Response Service */
      logPath.append(Constants.LOG_FILE_PATH + //File.separator +
                     instanceName + File.separator +
                     Constants.RESPONSE_SERVICE);
      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "ResponseServiceThread --- invokeResponseProcessing --- Log File Path to be passed--->" +
              logPath);
      /* If log path does not exist then Create it */
      if (!new File(logPath.toString()).exists()) {
        lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                "ResponseServiceThread --- invokeResponseProcessing --- Creating Log File Folder(s)--->" +
                new File(logPath.toString()).mkdirs());
      }
      /**
       * get Response Related information from bp_processors Table
       */
      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "getting ftp information from DB");

      ftpVo = responseDao.getResponseFtpInfo();

      /* If Respose Output Path exists */
      if (((Constants.RESPONSE_OUTPUT_PATH != null) &&
           (Constants.RESPONSE_OUTPUT_PATH.trim().length() > 0)) &&
          ((!ftpVo.getFtpLocalPath().equals("")) &&
           (ftpVo.getFtpLocalPath() != null))) {
        /* d:\...\mcp\Download */
        outputPath.append(ftpVo.getFtpLocalPath() + File.separator +
                          Constants.RESPONSE_OUTPUT_PATH + File.separator +
                          instanceName + File.separator +
                          Constants.DOWNLOAD_FOLDER);

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "ResponseServiceThread --- invokeResponseProcessing --- Download Output Path to be passed--->" +
                outputPath.toString());

//        Constants.RESPONSE_FTP_DOWNLOAD_FOLDER = outputPath.toString();

        /* if output path does not exist then create it */
        if (!new File(outputPath.toString()).exists()) {
          lgr.
              log(LogLevel.getLevel(Constants.LOG_CONFIG),
                  "ResponseServiceThread --- invokeResponseProcessing --- Creating Download OutPut Path Folder(s)--->" +
                  new File(outputPath.toString()).mkdirs());
        }
      }
      /* if Response output Path does not exist */
      else {
        lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                "ResponseServiceThread --- invokeResponseProcessing --- ResponseServiceThread Output Path value does not exist in configuration file-->" +
                Constants.RESPONSE_OUTPUT_PATH);

        throw new Exception(
            "ResponseServiceThread Path value does not exist in configuration file--->" +
            Constants.RESPONSE_OUTPUT_PATH);
      }
      /**************************************************
       * Response File Downloading
       **************************************************/
//      lgr.
//          log(LogLevel.getLevel(Constants.LOG_CONFIG),
//              "getting ftp information from DB");
//      ftpVo = responseDao.getResponseFtpInfo();

      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "Initilizing FTP with ftpInfoVO from DB");
      ftpServer = new I2cFTP(ftpVo);

      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "Start Connecting FTP Server --> " + ftpVo.getFtpAddress() +
              "\nIsFtpAllowed -- " + ftpVo.getIsFtpAllowed());

      if (ftpVo.getIsFtpAllowed().equals("Y")) {
        /* FTP Connected Sucessfully then download the file */
        if (ftpServer.connect()) {

          /********* Check for N Day old Response Files **********/
          returnMsg = checkForPreviousFiles(responseDao, ftpServer, ftpVo, dbConn,
                                            logPath, outputPath, returnMsg);

          /**************** check for bad payment File ***************/
          boolean badExist = checkForBadPayment(ftpServer, ftpVo);
          if (badExist){
            throw new Exception ("Today Payment File marked as Bad. Response Downloading Cancel");
          }
          /***********************************************************/
          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "FTP Server connected sucessfully....");
          /* Create Response File Name for Today */
          responseFileName = new String(ftpVo.getFilePreFix() + "." +
                                        CommonUtilities.getCurrentFormatDate(
                                            "MMddyyyy"));

          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "Response File Name to be downloaded ---> " +
                  responseFileName);
          /* Put File name in the Down load Path */
          outputPath.append(File.separator + responseFileName);
          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "Response File Download output Path with File Name ----> " +
                  outputPath.toString());

          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "---> START Download the Response File");
          try {
            isDownload = ftpServer.download(outputPath.toString(),
                                            responseFileName);
          }
          catch (Exception e) {
            lgr.log(
                LogLevel.getLevel(Constants.LOG_WARNING),
                "Exception During the Download of the Response File --- Unable to Download file " +
                e.getMessage());
            return returnMsg;
          }

          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "---> END Download----isFile Downloaded --------> " +
                  isDownload);
          /* FTP Disconnected */
          ftpServer.disconnect();
          /*************************************/
          if (isDownload) {

              lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "Response File Download Sucessfully....");
            if (ftpVo.getIsPgp().equals("Y")) {
              lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
                      "PGP Allowed for response file");

              decryptedPath = ftpVo.getFtpLocalPath() + File.separator +
                  Constants.RESPONSE_OUTPUT_PATH + File.separator +
                  instanceName + File.separator +
                  Constants.DECRYPT_FOLDER;

              lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "Decryption Path ---> " + decryptedPath);
              /* if decryption path does not exist then create it */
              if (!new File(decryptedPath).exists()) {
                lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                        "ResponseServiceThread --- invokeResponseProcessing --- Creating Decrypted OutPut Path Folder(s)--->" +
                        new File(decryptedPath).mkdirs());
              }

              lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                      "Start Decrypting File at Path --> " + decryptedPath);

              if (decryptFileFromPgp(outputPath.toString(),
                                     decryptedPath,
                                     ftpVo,
                                     logPath.toString() + File.separator + "PgpApi")) {

                lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                        "Response File Decrypted Sucessfully.....");

//                decryptedPath = decryptedPath + File.separator +
//                    Constants.RESPONSE_INFILE_PREFIX + "." +
//                    CommonUtilities.getJulienDate();

                // Get the PGP inner file name from PGP Api and test it against
                // standrad if ok then return it with path else email
                inRespFileName = decryptInFilePathName(decryptedPath, dbConn);

                lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
                        "File Decrypted Sucessfully...on Path -->" +
                        decryptedPath + File.separator + inRespFileName);
              } // respose File decrypted else
              else {
                throw new Exception(
                    "WARNING: Enable to Decrypt the Response File. OR PGP is not allowed");
              }
              lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                      "Save File in DB from ---->" + decryptedPath);

              /************Save in DB*********************/
              fileId = CommonUtilities.saveFileInBD(dbConn,
                      decryptedPath,
                      inRespFileName,
                      Constants.RSP_FILE_ID);
             /******************************************/

             lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                      "Initilization <ResponseFileHandler> with logpath---->" +
                      logPath.toString() + File.separator + "BRProcessor");


              responseHandler = new ResponseFileHandler(dbConn,
                  logPath.toString() + File.separator + "BRProcessor",
                  decryptedPath + File.separator + inRespFileName); //add fileId here
            } // if isPGP_allwod else
            else {
//          outputPath.append(File.separator +
//                            Constants.RESPONSE_INFILE_PREFIX + "." +
//                            CommonUtilities.getJulienDate());

             /************Save in DB*********************/
              fileId = CommonUtilities.saveFileInBD(dbConn,
                                           outputPath.toString(),
                                           responseFileName,
                                           Constants.RSP_FILE_ID);
             /******************************************/


              lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                      "Initilization <ResponseFileHandler> with logpath---->" +
                      logPath.toString() + File.separator + "BRProcessor"
                      + "\nWith File ---> " + outputPath.toString());
              /******************** Response Handler API *******************/
              responseHandler = new ResponseFileHandler(dbConn,
                  logPath.toString() + File.separator + "BRProcessor",
                  outputPath.toString()); // add fileid here
            }

            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "--->>>START <processResponseFile()> to process the Response File ");

            responseHandler.processResponseFile();

            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "--->>>END <processResponseFile()> to process the Response File ");
            /************ Print Status get from Response Handling API *********/

            if (returnMsg == null){
              returnMsg = new StringBuffer();
            }
            returnMsg.append(
                "\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            returnMsg.append("\nFile Name : ................ : " +
                             ResponseFileHandler.filsStatsObj.getFileName());
            returnMsg.append("\nTotal Amount : ............. : " +
                             ResponseFileHandler.filsStatsObj.getTotalAmount());
            returnMsg.append("\nTotal Batches : ............ : " +
                             ResponseFileHandler.filsStatsObj.getTotalBatches());
            returnMsg.append("\nTotal Transaction : ........ : " +
                             ResponseFileHandler.filsStatsObj.getTotalTrans());
            returnMsg.append("\nTotal Failed Transaction : . : " +
                             ResponseFileHandler.filsStatsObj.
                             getTotalFailedTrans());
            returnMsg.append("\nTotal Successful Transaction : " +
                             ResponseFileHandler.filsStatsObj.
                             getTotalSuccessTrans());
            returnMsg.append("\nTotal Cheque Transaction : . : " +
                             ResponseFileHandler.filsStatsObj.getToBeConfirmed());
            returnMsg.append(
                "\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            /*****************************************/

            movePath = ftpVo.getFtpLocalPath() + File.separator +
                Constants.RESPONSE_OUTPUT_PATH + File.separator +
                instanceName + File.separator +
                Constants.PROCESSED_FOLDER;

            lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Start move <moveFileToThisFolder> Processed File from Encrypt folder " +
                    outputPath.toString() + " to Processed Folder " + movePath);

            if (CommonUtilities.moveFileToThisFolder(outputPath.toString(),
                movePath,
                responseFileName)) {
              lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "Response File moved to Processed Folder Sucessfully...");

            }
            lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                    "ResponseServiceThread --- invokeResponseProcessing --- Resposne Received from API --->" +
                    returnMsg);

            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "ResponseServiceThread --- invokeResponseProcessing --- Updating the NEXT Schedule Date");
            responseDao.updateNextSchedule();
          }
          else {
            throw new Exception(
                "Exception --- Response File Download Fail from FTP server...");
          }
        }
        else {
          throw new IOException(
              "IOException --- FTP server Connection FAIL...");
        }
      }
    }
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_SEVERE),
              "ResponseServiceThread --- invokeResponseProcessing --- Exception in processing of Response File Processing--->" +
              ex.getMessage() +
              "\nResponseServiceThread Stack Trace: " + CommonUtilities.getStackTrace(ex));
      throw new Exception(
          "Exception in processing of Response Service Thread--->" +
          ex.getMessage() +
          "\nStack Trace: " + CommonUtilities.getStackTrace(ex));

    }
    return returnMsg;
  } //end InvokeResponseProcessing Method





  /**
   * Method Process Previous Response Files
   * @param responseDao ResponseServiceDAO
   * @param ftpServer I2cFTP
   * @param FTPInfoVo ftpVo
   * @param dbConn Connection
   * @param logPath StringBuffer
   * @param outputPath StringBuffer
   * @return boolean
   * @throws Exception
   */
  private StringBuffer checkForPreviousFiles(ResponseServiceDAO responseDao,
                                        I2cFTP ftpServer, FTPInfoVO ftpVo,
                                        Connection dbConn,
                                        StringBuffer logPath,
                                        StringBuffer outputPath,
                                        StringBuffer returnMsg) throws
      Exception {

    ResponseFileHandler responseHandler = null;
    ArrayList dateList = new ArrayList();
    String oldResponseFileName = null;
    boolean isOldFileDownload = false;
    boolean isFileExist = false;

    // Get old Response Files Date Array List
    dateList = responseDao.getOldResponseFileDateFtp();
    returnMsg = null;
    for (int i = 0; i < dateList.size(); i++) {
      String oldResponseFileDate = (String) dateList.get(i);
      ///////*****************************************////////
      // Make old Response File name
      oldResponseFileName = new String(ftpVo.getFilePreFix() + "." +
                                       oldResponseFileDate);
      // check file on FTP dir list
      isFileExist = ftpServer.listFiles(oldResponseFileName);

      // if file exist on FTP
      if (isFileExist) {
        lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
                "Old Response File Name to be downloaded ---> " +
                oldResponseFileName);

        /* Put old File name in the Download Path */
        outputPath.append(File.separator + oldResponseFileName);

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "Response File Download output Path with File Name ----> " +
                outputPath.toString());

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "---> START Downloading the Old Response File");
        try {
          isOldFileDownload = ftpServer.download(outputPath.toString(),
                                                 oldResponseFileName);
        }
        catch (Exception e) {
          e.printStackTrace();
          lgr.log(
              LogLevel.getLevel(Constants.LOG_WARNING),
              "Exception During the Download of the OLD Response Files " +
              e.getMessage());
          returnMsg.append(
              "<----->Exception During the Download of the OLD Response Files " +
              e.getMessage());
          return returnMsg;
        }

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "---> END Download----isOldResposeFile -- Downloaded --------> " +
                isOldFileDownload);
        /****************IS oldResponseFileDownloaded *******************/
        if (isOldFileDownload) {
          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "Old Response File Download Sucessfully....");
          // if PGP decryption of file isAllowed for the
          if (ftpVo.getIsPgp().equals("Y")) {
            lgr.log(LogLevel.getLevel(Constants.LOG_FINE),
                    "PGP Allowed for Old response file");

            String decryptedPath = ftpVo.getFtpLocalPath() + File.separator +
                Constants.RESPONSE_OUTPUT_PATH + File.separator +
                instanceName + File.separator +
                Constants.DECRYPT_FOLDER;

            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "Decryption Path for old Response File---> " +
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
                      "Response File Decrypted Sucessfully.....");

              // Get the PGP inner file name from PGP Api and test it against
              // standrad if ok then return it with path else email
              decryptedPath = decryptInFilePathName(decryptedPath, dbConn);

              lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                      "File Decrypted Sucessfully...on Path -->" +
                      decryptedPath);
            } // respose File decrypted else
            else {
              throw new Exception(
                  "WARNING: Enable to Decrypt the OLD Response File. OR PGP is not allowed for Old File");
            }
            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "Initilization <ResponseFileHandler> <With PGPAllowed> -- logpath---->" +
                    logPath.toString() + File.separator + "BRPror4oldFiles"
                    + "\nWith File ---> " + decryptedPath);

            responseHandler = new ResponseFileHandler(dbConn,
                logPath.toString() + File.separator + "BRPror4oldFiles",
                decryptedPath);

            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "<--->START <processResponseFile()> to process the " +
                    decryptedPath + " ....Response File ");

          } // if isPGP_allwod else
          else {

            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "Initilization <ResponseFileHandler> <WithOut PGPAllowed> -- logpath---->" +
                    logPath.toString() + File.separator + "BRPror4oldFiles"
                    + "\nWith File ---> " + outputPath.toString());
            /******************** Response Handler API *******************/
            responseHandler = new ResponseFileHandler(dbConn,
                logPath.toString() + File.separator + "BRPror4oldFiles",
                outputPath.toString());

            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "<--->START <processResponseFile()> to process the " +
                    outputPath.toString() + " ....Response File ");
          }

          /************* Method to call <processResponseFile()> ************/

          responseHandler.processResponseFile();

          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "<--->END <processResponseFile()> to process the Old Response File ");

          /****************************************************************/
          returnMsg.append(
                "\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            returnMsg.append("\nFile Name : ................ : " +
                             ResponseFileHandler.filsStatsObj.getFileName());
            returnMsg.append("\nTotal Amount : ............. : " +
                             ResponseFileHandler.filsStatsObj.getTotalAmount());
            returnMsg.append("\nTotal Batches : ............ : " +
                             ResponseFileHandler.filsStatsObj.getTotalBatches());
            returnMsg.append("\nTotal Transaction : ........ : " +
                             ResponseFileHandler.filsStatsObj.getTotalTrans());
            returnMsg.append("\nTotal Failed Transaction : . : " +
                             ResponseFileHandler.filsStatsObj.
                             getTotalFailedTrans());
            returnMsg.append("\nTotal Successful Transaction : " +
                             ResponseFileHandler.filsStatsObj.
                             getTotalSuccessTrans());
            returnMsg.append("\nTotal Cheque Transaction : . : " +
                             ResponseFileHandler.filsStatsObj.getToBeConfirmed());
            returnMsg.append(
                "\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            /*****************************************/

            String movePath = ftpVo.getFtpLocalPath() + File.separator +
                Constants.RESPONSE_OUTPUT_PATH + File.separator +
                instanceName + File.separator +
                Constants.PROCESSED_FOLDER;

            lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Start move <moveFileToThisFolder> Processed File from Encrypt folder " +
                    outputPath.toString() + " to Processed Folder " + movePath);

            if (CommonUtilities.moveFileToThisFolder(outputPath.toString(),
                movePath,
                oldResponseFileName)) {
              lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "Response File moved to Processed Folder Sucessfully...");

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

    String inRespFileName = null;

    String fileNameFromConfig = Constants.RESPONSE_INFILE_PREFIX + "." +
        CommonUtilities.getJulienDate();

    lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "decryptInFilePathName --- Response inner File Name should be --> " +
                fileNameFromConfig);

    if (Constants.RES_DECRYPT_INFILE_NAME.equalsIgnoreCase(fileNameFromConfig)){

//      inFilePathName = decryptedPath + File.separator + fileNameFromConfig;
        inRespFileName = fileNameFromConfig;

      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "decryptInFilePathName --- Response inner File Name matched at path" +
                inRespFileName);

    } else {

      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "decryptInFilePathName ---> Response inner File Name NOT matched");

//      inFilePathName = decryptedPath + File.separator + Constants.RES_DECRYPT_INFILE_NAME;
      inRespFileName = Constants.RES_DECRYPT_INFILE_NAME;
      // Email
      String emailMsg = "Response Pgp inFile Name not match" +
          " with standrad --- inFile Name ---->" +
          Constants.RES_DECRYPT_INFILE_NAME;

      ResponseServiceMailer responseMailer = new ResponseServiceMailer(Constants.
                                                 RESPONSE_INFILE_FAIL,
                                                 instanceName, null,
                                                 emailMsg,
                                                 dbConn,
                                                 lgr);
      responseMailer.sendEmailNotification();


    }

    return inRespFileName;
  }

  private boolean checkForBadPayment(I2cFTP ftpServer, FTPInfoVO ftpVo) throws Exception{
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
      Constants.RES_DECRYPT_INFILE_NAME =
          cs.verifyAndDecryptPGPFile(downloadFilePath,
                                     Constants.SECRET_KEY_PATH,
                                     ftpVo.getPgpPassPhrase(),
                                     Constants.VERIFY_PUBLIC_KEY_PATH,
                                     decryptedPath);
    }
    catch (PGPException ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_SEVERE),
              "ResponseServiceThread --- decryptFileFromPgp --- PGPException in Verifying and Decryption--->" +
              ex);
      ex.printStackTrace();
      return false;
    }
    catch (Exception ex1) {
      lgr.log(LogLevel.getLevel(Constants.LOG_SEVERE),
              "ResponseServiceThread --- decryptFileFromPgp --- Exception in Verifying and Decryption--->" +
              ex1);
      ex1.printStackTrace();
      return false;
    }
    return true;
  }

  private java.util.Date getCurrentDate() {
    java.util.Date currDate = null;
    try {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              " Getting current Date ");
      currDate = new java.util.Date();
    }
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
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
