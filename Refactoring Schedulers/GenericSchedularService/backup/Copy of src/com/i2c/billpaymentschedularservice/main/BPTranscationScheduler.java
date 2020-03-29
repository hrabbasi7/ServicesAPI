package com.i2c.billpaymentschedularservice.main;

import java.util.logging.*;

import com.i2c.billpaymentschedularservice.dao.ResponseServiceDAO;
import com.i2c.billpaymentschedularservice.monitor.*;
import com.i2c.billpaymentschedularservice.util.*;
import java.util.Date;
import com.i2c.billpaymentschedularservice.FTPService.I2cFTP;
import com.i2c.billpaymentschedularservice.FTPService.FTPInfoVO;
import java.sql.Connection;
import java.sql.*;
import com.i2c.billpaymentschedularservice.FTPService.FTPUtil;
import com.i2c.billpaymentschedularservice.handler.PaymentServiceThread;
import com.i2c.billpaymentschedularservice.handler.ResponseServiceThread;
import com.i2c.billpaymentschedularservice.handler.AdjustmentServiceThread;

/**
 * <p>Title: BillPayment Scheduler Service </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright innovative pvt ltd (c) 2005</p>
 *
 * <p>Company: i2cinc</p>
 *
 * @author Haroon ur Rashid Abbasi
 * @version 1.0
 */
public class BPTranscationScheduler extends Thread {

  private Logger lgr = null;
  private String instanceName = null;

  public BPTranscationScheduler(String instanceName, Logger logger) {
    this.instanceName = instanceName;
    this.lgr = logger;
  }

  public void run() {
    lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
            " BPTranscationScheduler --- Before Creating Bill Payment File Processing Monitor Service Thread ");
    PaymentServiceMonitor paymentServiceMonitor = new PaymentServiceMonitor(
        Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),
        "Bill Payment Scheduler Service", lgr);
    paymentServiceMonitor.start();

    while(true){
      try {
        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "BPTranscationScheduler --- Before Creating Bill Payment File Processing Service Thread ");
        PaymentServiceThread paymentService = new PaymentServiceThread(
            Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),
            paymentServiceMonitor,
            lgr);
        // Generate Payment File
        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "BPTranscationScheduler --- Before going for PaymentService Process()");
        boolean paymentDone = paymentService.process();

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "BPTranscationScheduler --- PaymentService Process() run sucessfully --->" +
                paymentDone);

        if (paymentDone) {
          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "BPTranscationScheduler --- Payment Generation Sucessful ---" +
                " Before going for Process Response and Adjustment");

          processRespAdj(instanceName, lgr, paymentServiceMonitor);

          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "BPTranscationScheduler --- Sucessfully Process Response and Adjustment");
        }
        else {
          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "BPTranscationScheduler --- Payment Generation Fail ---" +
                "Check for Manual placement of Payment File on FTP...");

          // Serach today Payment File on FTP Placed Maunally
          boolean searchRespAdj = searchPaymentFile();

          lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "BPTranscationScheduler --- Payment File placed on FTP Manually ---> " +
                searchRespAdj);

          if (searchRespAdj) { // after manual placemnet of payment file on FTP

            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                  "BPTranscationScheduler --- Payment File placed on FTP Manually --- " +
                  "Before going for Process Response and Adjustment...");

            processRespAdj(instanceName, lgr, paymentServiceMonitor);

            lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                    "BPTranscationScheduler --- After coming from Response Adjustment Processing");
          }
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
        lgr.log(LogLevel.getLevel(Constants.LOG_SEVERE),
                "Exception in BPTranscationScheduler ---> " + ex.getMessage() +
                "Stack Trace is ---> " + CommonUtilities.getStackTrace(ex));
      }
    }
  }

  private boolean searchPaymentFile() {
    Date today = new Date();
    I2cFTP ftpServer = null;
    FTPInfoVO ftpVo = null;
    Connection dbConn = null;
    boolean isPresent = false;
    boolean isSameDay = false;
    int counter = 0;

    try {
      dbConn = DatabaseHandler.getMasterConnection("PaymentServiceThread",
          instanceName,
          Constants.CARD_INSTANCE_DB_CONN);

      ResponseServiceDAO responseDao =
          new ResponseServiceDAO(instanceName, dbConn, lgr);

      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "Before going to check for Manual Placement of payment file on FTP in case of Auto Generation......");

      while (true) {

        lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
                "Check for Manual file placment of Payment File --- Try :" + counter++);

        ftpVo = responseDao.getResponseFtpInfo();
        ftpServer = new I2cFTP(ftpVo);

        ftpServer.connect();

        isPresent = FTPUtil.isFilePresent(ftpServer, ftpVo);

        isSameDay = (today.getDay() == new Date().getDay());

        if (isPresent == false && isSameDay) {
          Thread.sleep(Constants.DEFAULT_WAIT_TIME);
        }
        else {
          return isPresent;
        }
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),
              "Exception in searchPaymentFile ---> " + ex +
              "Stack Trace is ---> " + CommonUtilities.getStackTrace(ex));

      try {
        Thread.sleep(Constants.DEFAULT_WAIT_TIME);
      }
      catch (InterruptedException ex1) {
        ex1.printStackTrace();
      }
    }

    return false;
  }

  private void processRespAdj(String instanceName,
                              Logger paymentLogger,
                              PaymentServiceMonitor paymentServiceMonitor) throws
      Exception {

    boolean adjustmentDone = false;
    boolean responseDone = false;
    ResponseServiceDAO responseServiceDAO = null;
    Connection dbConn = null;
    long[] respAdjTimings;
    String emailMsg = null;

    dbConn = DatabaseHandler.getMasterConnection("ResponseServiceThread",
                                                 instanceName,
                                                 Constants.CARD_INSTANCE_DB_CONN);

    boolean notifyStatus = false;
    int retryTimeSpent = 0;
    responseServiceDAO = new ResponseServiceDAO(instanceName , dbConn, paymentLogger);
    // Sleep for the Difference Time between Payment and Response Adjustment
    Thread.sleep(Constants.DEFAULT_WAIT_TIME);

    while (responseDone == false || adjustmentDone == false) {
      lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
              " Before Creating Bill Response File Processing Service Thread ");
      if (responseDone == false) {
        ResponseServiceThread responseService = new
            ResponseServiceThread(Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),
                                  paymentServiceMonitor,
                                  paymentLogger);
        responseDone = responseService.process();
      }
      if (adjustmentDone == false) {
        lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
                " Before Creating Bill Adjustment File Processing Service Thread ");
        AdjustmentServiceThread adjustmentService = new
            AdjustmentServiceThread(Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),
                                    paymentServiceMonitor,
                                    paymentLogger);
        adjustmentDone = adjustmentService.process();
      }

      // Sleep for the next Retry Time
      respAdjTimings = responseServiceDAO.respAdjTimes(Constants.PROCESSOR_ID);

      if (notifyStatus == false && respAdjTimings[1] <= retryTimeSpent){

        if (responseDone == false && adjustmentDone == false){
          emailMsg = "Response and Adjustment files not found for today : " +
              new java.util.Date();
        }
        else if (responseDone == true){
          emailMsg = "Adjustment file not found for today : " + new java.util.Date();
        }
        else if (adjustmentDone == true){
          emailMsg = "Response file not found for today : " + new java.util.Date();
        }

        this.sendEmailAlert(emailMsg);
        notifyStatus = true;
        break;
      }

      retryTimeSpent += respAdjTimings[0];

      // go to sleep for next retry
      long retryTime = respAdjTimings[0] * 60 * 1000;
      Thread.sleep(retryTime);
    }
  }

  private void sendEmailAlert(String message) {
      AdminMailService mailThrd = new AdminMailService(message,
          Constants.RESADJ_FAIL_EMAIL_SUBJECT,
          Constants.MAIL_REPORT_ADMIN,
          Constants.MAIL_REPORT_FROM);
      mailThrd.start();
  }

}
