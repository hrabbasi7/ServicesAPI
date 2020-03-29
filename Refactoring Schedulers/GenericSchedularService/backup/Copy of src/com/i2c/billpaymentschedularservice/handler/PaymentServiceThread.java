package com.i2c.billpaymentschedularservice.handler;

import java.io.*;
import java.sql.*;

import org.bouncycastle.openpgp.service.*;
import com.i2c.billpayment.jobs.*;
import com.i2c.billpaymentschedularservice.FTPService.*;
import com.i2c.billpaymentschedularservice.monitor.*;
import com.i2c.billpaymentschedularservice.util.*;
import com.i2c.billpaymentschedularservice.main.ServiceMain;
import java.util.ArrayList;
import com.i2c.billpayment.vo.BPBatchStatistics;
import java.util.logging.Logger;
import com.i2c.utils.logging.I2cLogger;
import com.i2c.billpaymentschedularservice.dao.PaymentServiceDAO;
import com.i2c.billpaymentschedularservice.mailer.PaymentServiceMailer;
import com.i2c.billpaymentschedularservice.model.PaymentInfoVO;

public class PaymentServiceThread {
//    extends Thread{
  private String instanceName = null;
  private PaymentServiceMonitor monitorThread = null;
  private Logger lgr = null;

  public PaymentServiceThread(String instanceName,
                              PaymentServiceMonitor monitorThread,
                              Logger paymentLgr) {
    this.instanceName = instanceName;
    this.monitorThread = monitorThread;
    this.lgr = paymentLgr;
  }

  public void run() {
    process();
  } // run

  public boolean process() {
    PaymentServiceDAO paymentServiceDAO = null;
    Connection dbConn = null;
    boolean batchTime = false;
    StringBuffer paymentTransStatus = null;
    PaymentServiceMailer paymentMailer = null;
    PaymentInfoVO nextSchedule = null;
    try {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "PaymentServiceThread -- Method Name: run --- PaymentServiceThread is started");

      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "PaymentServiceThread -- Method Name: run --- Getting Database Connection for instance --->" +
              instanceName);
      dbConn = DatabaseHandler.getMasterConnection("PaymentServiceThread",
          instanceName,
          Constants.CARD_INSTANCE_DB_CONN);
      paymentServiceDAO = new PaymentServiceDAO(instanceName, dbConn, lgr);

      while (true) {

        try {
          try {
            lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                    "PaymentServiceThread -- Method Name: run --- Calling method for testing database connection");
            testConnection(dbConn);
          }
          catch (SQLException tstConEx) {
            lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                    "PaymentServiceThread -- Method Name: run --- Exception in testing database connection -- Creating new Connection");
            try {
              dbConn = DatabaseHandler.getMasterConnection(
                  "PaymentServiceThread",
                  instanceName,
                  Constants.CARD_INSTANCE_DB_CONN);

              paymentServiceDAO = new PaymentServiceDAO(instanceName, dbConn,
                  lgr);
            }
            catch (Exception ex) {
              lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                      "PaymentServiceThread  -- Method Name: run --- Exception in creating new Connection");
            }
          }
          catch (Exception tstConEx) {
            lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                    "PaymentServiceThread -- Method Name: run --- Exception in testing database connection -- Creating new Connection");
            try {
              dbConn = DatabaseHandler.getMasterConnection(
                  "PaymentServiceThread",
                  instanceName,
                  Constants.CARD_INSTANCE_DB_CONN);

              paymentServiceDAO = new PaymentServiceDAO(instanceName, dbConn,
                  lgr);
            }
            catch (Exception ex) {
              lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                      "PaymentServiceThread  -- Method Name: run --- Exception in creating new Connection");
            }
          }

          lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                  " \n\nGetting current date time for updating PAYMENT_SRV monitoring Date Time ");
          java.util.Date currDate = getCurrentDate();
          lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                  " \n\nUpdating PAYMENT_SRV Monitoring Date Time --- Current Date Time Got--->" +
                  currDate);
          monitorThread.setMonitorDateTime(currDate);

          /****************** Get PaymentSleepTime from DB ****************/
          boolean isTrue = paymentServiceDAO.paymentSleepTime(Constants.PROCESSOR_ID);
          /****************************************************************/
          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "paymentSleepTime update the PAYMENTSLEEPTIME ----> " + isTrue);

          // Check Service allowed for Instance
          if (paymentServiceDAO.processScheduler(dbConn,
                                                 Constants.
                                                 USE_BILL_PAYMENT_PROCESSING)) {
            batchTime = paymentServiceDAO.checkScheduleTime();
            lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "\nPaymentServiceThread --- run --- Response received from checkScheduleTime ---> " +
                    batchTime);
            if (batchTime) {
              lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "PaymentServiceThread --- run --- Invoking Bill Payment File processing");
              //             +  "PaymentServiceThread --- run --- Getting Database Connection for instance ---> card_inst");
              try {
                /**********************invokePaymentProcessing*************************/
                lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        "\nPaymentServiceThread --- run --- Invoke Payment File processing <invokePaymentProcessing> called..");

                paymentTransStatus = invokePaymentProcessing(dbConn,
                    paymentServiceDAO);

                lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        "\nPaymentServiceThread --- run --- Exit Bill Payment File processing <invokePaymentProcessing> called..");

                /**********************************************************************/
                lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        "PaymentServiceThread --- run --- Sending success email");

                paymentMailer = new PaymentServiceMailer(
                    Constants.PAYMENT_SRV_SUCC, instanceName, null,
                    paymentTransStatus.toString(), dbConn, lgr);
                paymentMailer.sendEmailNotification(); // Close <dbConn>

                dbConn.close();
                return true;
              }
              catch (Exception procEx) {
                lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        "PaymentServiceThread --- run --- Exception in processing of Bill Payment File Processing --- Emailing to Admin-->" +
                        procEx);
                paymentMailer = new PaymentServiceMailer(
                    Constants.PAYMENT_SRV_FAIL, instanceName,
                    Constants.getMachineIP(), procEx.toString(),
                    CommonUtilities.getStackTrace(procEx), dbConn, lgr);
                paymentMailer.sendEmailNotification();

                lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                        "Going to Sleep after Exception In PaymentServiceThread : " +
                        Constants.PAYMENT_SLEEP_TIME);
//                Thread.sleep(Constants.PAYMENT_SLEEP_TIME);

                dbConn.close();
                return false;
              }
            } ///////////Check for the Time difference///////////////////////
            else {
              nextSchedule = paymentServiceDAO.fetchNextSchedule();
              if (nextSchedule != null) {
                lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                        "PaymentServiceThread -- Method Name: run --- (GOT Next Schedule Info) --->\n" +
                        "Payment ScheduledDateTime--: " +
                        nextSchedule.getScheduledDateTime() +
                        " --CurrentDateTime--: " +
                        nextSchedule.getCurrentDateTime() +
                        " --TimeDifference--: " +
                        nextSchedule.getTimeDifference());
                if (nextSchedule.getTimeDifference() != -1 &&
                    nextSchedule.getTimeDifference() < 0) {
                  lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                          "PaymentServiceThread -- Next Schedule Time is Negative ---  Sleeping for Payment Sleep Time ---> " +
                          nextSchedule.getTimeDifference());
                  Thread.sleep(Constants.PAYMENT_SLEEP_TIME);
//                continue;
                }
                else if (nextSchedule.getTimeDifference() != -1 &&
                         nextSchedule.getTimeDifference() <
                         Constants.PAYMENT_SLEEP_TIME) {
                  lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                          "PaymentServiceThread -- No Payment Service Tasks to schedule ---  Sleeping for Difference Time---> " +
                          nextSchedule.getTimeDifference());
                  Thread.sleep(nextSchedule.getTimeDifference());
                } ///////////////////////////////////
                else { //end if check schTime arrived
                  lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                          "PaymentServiceThread --- Payment File processing Time not arrived -- Sleeping for Payment Sleep Time");
                  Thread.sleep(Constants.PAYMENT_SLEEP_TIME);
                }
              }
            }
          }
          else { //end if check isAllowed
            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "PaymentServiceThread --- run --- Payment File processing is not allowed for this Instance --- Else of processScheduler");
            Thread.sleep(Constants.PAYMENT_SLEEP_TIME);
          }// trure is not right plz check
//          return false; //true
        }
        catch (Exception ex) { //end inner try
          lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                  "PaymentServiceThread --- Excpetion in Bill Payment File processing--->" +
                  ex.getMessage());
          Thread.sleep(Constants.PAYMENT_SLEEP_TIME);
        } //end inner catch
      } //end while
    }
    catch (Exception ex) { //end outer try
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "PaymentServiceThread --- Outer Catch -- Excpetion in Payment Service Thread--->" +
              ex);
      return false;
    }
  } //end process()

  private StringBuffer invokePaymentProcessing(Connection dbConn,
                                               PaymentServiceDAO paymentDao) throws
      Exception {

    FTPInfoVO ftpVo = null;
    BillPaymentProcessor bp = null;
    I2cFTP ftpServer = null;
    StringBuffer logPath = new StringBuffer();
    StringBuffer returnMsg = null;
    StringBuffer uploadPath = new StringBuffer();
    String generatedPaymentFilePath = null;
    String encryptPath = null;
    String encryptPathwithFile = null;
    String processedPath = null;
    String movePaymentFileFolder = null;
    String paymentFileSerialNo = null;

    try {
      logPath.append(Constants.LOG_FILE_PATH + //File.separator +
                     instanceName + File.separator + Constants.PAYMENT_SERVICE);
      lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "PaymentServiceThread --- invokePaymentProcessing --- Log File Path to be passed--->" +
              logPath.toString());

      if (!new File(logPath.toString()).exists()) {
        lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                "PaymentServiceThread --- invokePaymentProcessing --- Creating Log File Folder(s)--->" +
                new File(logPath.toString()).mkdirs());
      }
      /**
       * Get Payment File related Ftp info from DB
       */
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "getting Payment File ftp information from DB");
      ftpVo = new FTPInfoVO();
      ftpVo = paymentDao.getPaymentFtpInfo();

      if (((Constants.PAYMENT_OUTPUT_PATH != null) &&
           (Constants.PAYMENT_OUTPUT_PATH.trim().length() > 0)) &&
          ((!ftpVo.getFtpLocalPath().equals("")) &&
           (ftpVo.getFtpLocalPath() != null))) {

        uploadPath.append(ftpVo.getFtpLocalPath() + File.separator +
                          Constants.PAYMENT_OUTPUT_PATH + File.separator +
                          instanceName + File.separator +
                          Constants.UPLOAD_FOLDER);
        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "PaymentServiceThread --- invokePaymentProcessing --- Output Path for uploading to be passed--->" +
                uploadPath.toString());

        if (!new File(uploadPath.toString()).exists()) {
          lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                  "PaymentServiceThread --- invokePaymentProcessing --- Creating Upload OutPut Path Folder(s)--->" +
                  new File(uploadPath.toString()).mkdirs());
        }
      }
      else {
        lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                "PaymentServiceThread --- invokePaymentProcessing --- " +
                "PaymentServiceThread Upload Output Path value does not exist in configuration file-->" +
                ftpVo.getFtpLocalPath() + File.separator +
                Constants.PAYMENT_OUTPUT_PATH);

        throw new Exception(
            "PaymentServiceThread Path value does not exist in configuration file--->" +
            ftpVo.getFtpLocalPath() + File.separator +
            Constants.PAYMENT_OUTPUT_PATH);
      }
      /*************************************/
      bp = new BillPaymentProcessor(logPath.toString() +
                                    File.separator +
                                    "BPProcesser",
                                    dbConn);
      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "PaymentServiceThread --- invokePaymentProcessing --->> START PAYMENT Processing and Payment file generation");
      bp.processBillPayments(uploadPath.toString(), Constants.PROCESSOR_ID);
      /**************************************/
      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "PaymentServiceThread --- invokePaymentProcessing --->> END PAYMENT Processing and Payment file generation");

      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "PaymentServiceThread --- run --- Updating the NEXT Schedule Date");
      paymentDao.updateNextSchedule();

//      if (BillPaymentProcessor.filsStatsObj.isFileCreated()) {
      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "Payment file Created Sucessfully");

      returnMsg = new StringBuffer();
      returnMsg.append("File Name : " +
                       BillPaymentProcessor.filsStatsObj.getFileName());
      returnMsg.append("\nTotal Amount : " +
                       BillPaymentProcessor.filsStatsObj.getTotalAmount());
      returnMsg.append("\nTotal Batches : " +
                       BillPaymentProcessor.filsStatsObj.getTotalBatches());
      returnMsg.append("\nTotal Failed Transaction : " +
                       BillPaymentProcessor.filsStatsObj.getTotalFailedTrans());
      returnMsg.append("\nTotal Successful Transaction : " +
                       BillPaymentProcessor.filsStatsObj.getTotalSuccessTrans());
      returnMsg.append("\nTotal Transaction : " +
                       BillPaymentProcessor.filsStatsObj.getTotalTrans());
      returnMsg.append("\n\nBatch Details :\n");
      returnMsg.append("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");

      ArrayList list = bp.filsStatsObj.getBatchStatList();
      returnMsg.append("Batch Size : " + list.size());
      returnMsg.append("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");

      for (int i = 0; i < list.size(); i++) {
        BPBatchStatistics batchStat = (BPBatchStatistics) list.get(i);
        returnMsg.append("\nBatch ID : ................. : " +
                         batchStat.getBatchName());
        returnMsg.append("\nTotal Transaction : ........ : " +
                         batchStat.getTotalTrans());
        returnMsg.append("\nTotal Successful Transaction : " +
                         batchStat.getTotalSuccessTrans());
        returnMsg.append("\nTotal Failed Transaction : . : " +
                         batchStat.getTotalFailedTrans());
        returnMsg.append("\nTotal Amount : ............. : " +
                         batchStat.getTotalAmount());
        returnMsg.append("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n\n");
      }
      // get the serial no of the Payment file Generated for updation of Status
      paymentFileSerialNo = BillPaymentProcessor.filsStatsObj.getFileSrNo();

      generatedPaymentFilePath = uploadPath.toString() + File.separator +
          BillPaymentProcessor.filsStatsObj.getFileName() + File.separator +
          BillPaymentProcessor.filsStatsObj.getFileName();

      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "invokePaymentProcessing --- Generated Payment file Path --> " +
              generatedPaymentFilePath);

      if (ftpVo.getIsPgp().equals("Y")) {
        // Creating Encrypt File Path
        encryptPath = ftpVo.getFtpLocalPath() + File.separator +
            Constants.PAYMENT_OUTPUT_PATH + File.separator +
            instanceName + File.separator +
            Constants.ENCRYPT_FOLDER;

        if (!new File(encryptPath).exists()) {
          lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                  "invokePaymentProcessing --- Creating Encryption OutPut Path Folder(s)--->" +
                  new File(encryptPath).mkdirs());
        }

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "invokePaymentProcessing --- Encrypt Payment File and Place to Path ---> " +
                encryptPath);

        encryptPathwithFile = encryptPath + File.separator +
            BillPaymentProcessor.filsStatsObj.getFileName();

        if (encryptFileFromPgp(generatedPaymentFilePath,
                               encryptPathwithFile,
                               ftpVo,
                               logPath.toString() + File.separator + "PgpApi")) {

          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "File Encrypted Sucessfully.....");
        }
        else {
          throw new Exception(
              "Exception --- Encryption Fail on Payment File --->  " +
              generatedPaymentFilePath);
        }
        // update encrypted file path to upload file path;
        Constants.FTP_UPLOAD_FOLDER = ftpVo.getFtpLocalPath() + File.separator +
            Constants.PAYMENT_OUTPUT_PATH + File.separator +
            instanceName + File.separator +
            Constants.ENCRYPT_FOLDER + File.separator +
            BillPaymentProcessor.filsStatsObj.getFileName();

      } // if PGP is N
      else {
        // update encrypted file path to upload file path;
        Constants.FTP_UPLOAD_FOLDER = uploadPath.toString() + File.separator +
            BillPaymentProcessor.filsStatsObj.getFileName() + File.separator +
            BillPaymentProcessor.filsStatsObj.getFileName();
        // path for payment move file folder to proceed folder
        movePaymentFileFolder = uploadPath.toString() + File.separator +
            BillPaymentProcessor.filsStatsObj.getFileName();

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "No PGP option selected and File is uploaded from : " +
                Constants.FTP_UPLOAD_FOLDER);
      }

      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              "Check the status of isFtpAllowed ---> " + ftpVo.getIsFtpAllowed());
      if (ftpVo.getIsFtpAllowed().equals("Y")) {

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "FTPisAllowed -- sInitilize Ftp with ftpVo information from DB" +
                "Payment file Placed at following Path for Uploading ---> " +
                encryptPathwithFile);

        ftpServer = new I2cFTP(ftpVo);

//    lgr.
//        log(LogLevel.getLevel(Constants.LOG_INFO),
//            "Payment file Placed at following Path for Uploading ---> " +
//            encryptPathwithFile);

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "Start connecting FTP server ---> " + ftpVo.getFtpAddress());

        if (ftpServer.connect()) {

          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "<--- FTP server Connected Sucessfully --->" +
                  "\n\n Start Uploading Payment File from " +
                  Constants.FTP_UPLOAD_FOLDER);

          if (ftpServer.upload(Constants.FTP_UPLOAD_FOLDER,
                               BillPaymentProcessor.filsStatsObj.getFileName())) {
            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "File Upload Sucessfully...");

            ftpServer.disconnect();

            // Creating Processed path for payment file
            processedPath = ftpVo.getFtpLocalPath() + File.separator +
                Constants.PAYMENT_OUTPUT_PATH + File.separator +
                instanceName + File.separator +
                Constants.SENT_FOLDER;

            if (!new File(processedPath).exists()) {
              lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                      "invokePaymentProcessing --- Creating Encryption OutPut Path Folder(s)--->" +
                      new File(processedPath).mkdirs());
            }

            if (ftpVo.getIsPgp().equals("Y")) {
              lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                      "invokePaymentProcessing -- moveFileToThisFolder -- move Encrypted uploaded files to Processed Folder.. ");

              CommonUtilities.moveFileToThisFolder(encryptPath,
                  processedPath,
                  BillPaymentProcessor.
                  filsStatsObj.getFileName());
            }
            else {
              lgr.log(LogLevel.getLevel(Constants.LOG_CONFIG),
                      "invokePaymentProcessing -- moveFileToThisFolder -- move NON-Encrypted uploaded files to Processed Folder.. ");

              CommonUtilities.moveFileToThisFolder(movePaymentFileFolder,
                  processedPath,
                  BillPaymentProcessor.
                  filsStatsObj.getFileName());
            }
            /*************** Update the Payment File Status To 'S' *********/
            paymentDao.updatePaymentStatus(paymentFileSerialNo);
            /***************************************************************/
          }
          else {
            throw new Exception(
                "Exception --- Uploading Fail for File --->  " + encryptPath);
          }
        }
        else {
          throw new IOException(
              "IOException --- Can not connect to FTP server...");
        }
      }
      else {
        lgr.
            log(LogLevel.getLevel(Constants.LOG_INFO),
                "FTP is not allowed so no file uploaded....");
      }
//    }/////////////////////////////////////////////
//    else {
//      lgr.
//            log(LogLevel.getLevel(Constants.LOG_INFO),
//                "No Payment file generated and no uploading is performed...");
//
//        returnMsg = new StringBuffer();
//        returnMsg.append("No Payment File is created....");
//        returnMsg.append("\nFile Name : " +
//                         BillPaymentProcessor.filsStatsObj.getFileName());
//        returnMsg.append("\nTotal Amount : " +
//                         BillPaymentProcessor.filsStatsObj.getTotalAmount());
//        returnMsg.append("\nTotal Batches : " +
//                         BillPaymentProcessor.filsStatsObj.getTotalBatches());
//        returnMsg.append("\nTotal Failed Transactions : " +
//                         BillPaymentProcessor.filsStatsObj.getTotalFailedTrans());
//        returnMsg.append("\nTotal SucessFul Transactions : " +
//                         BillPaymentProcessor.filsStatsObj.getTotalSuccessTrans());
//        returnMsg.append("\nTotal Transactions : " +
//                         BillPaymentProcessor.filsStatsObj.getTotalTrans());
//      }
      /*****************************************/
      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "PaymentServiceThread --- invokePaymentProcessing --- Resposne Received from API --->" +
              returnMsg);
    }
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),
              "PaymentServiceThread --- invokePaymentProcessing --- Exception in processing of Payment File Processing--->" +
              ex.toString());
      ex.printStackTrace();
      throw new Exception(
          "Exception in processing of Payment Service Thread--->" +
          ex.toString() + "Stack Trace ---> " + CommonUtilities.getStackTrace(ex));
    }
    return returnMsg;
  } //end method

  boolean encryptFileFromPgp(String fileName,
                             String encryptedPath,
                             FTPInfoVO ftpVo,
                             String logPath) {
    PGPCryptographicService cs = new PGPCryptographicService(logPath);

    try {
      cs.encryptAndSignPGPFile(fileName,
                               Constants.ENCRYPT_PUBLIC_KEY_PATH,
                               Constants.SECRET_KEY_PATH,
                               ftpVo.getPgpPassPhrase(),
                               encryptedPath);
    }
    catch (Exception ex) {
      lgr.log(LogLevel.getLevel(Constants.LOG_SEVERE),
              "PaymentServiceThread --- encryptFilePgp --- Exception in Generating of PGP based Payment File --->" +
              ex);
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
      lgr.log(
          LogLevel.getLevel(Constants.LOG_WARNING),
          " Exception in Getting current Date --->" +
          ex);
    }
    return currDate;
  }

  private void testConnection(Connection dbConn) throws SQLException {
    StringBuffer query = new StringBuffer();
    Statement stmt = null;

    try {
      lgr.log(LogLevel.
              getLevel(Constants.LOG_FINEST),
              " Method for testing database conenction ");

      query.append("select business_date from system_variables");
      stmt = dbConn.createStatement();
      stmt.executeQuery(query.toString());
      stmt.close();
    }
    catch (SQLException ex) {
      lgr.log(LogLevel.
              getLevel(Constants.LOG_FINEST),
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
