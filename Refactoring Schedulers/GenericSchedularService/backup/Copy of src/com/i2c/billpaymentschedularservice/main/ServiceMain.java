package com.i2c.billpaymentschedularservice.main;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import com.i2c.billpaymentschedularservice.handler.*;
import com.i2c.billpaymentschedularservice.monitor.*;
import com.i2c.billpaymentschedularservice.util.*;
import com.i2c.billpaymentschedularservice.model.DbConnectionInfoVO;
import com.i2c.utils.logging.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ServiceMain {

  private static long lastUpdateTime = -1;
  private static ServerSocket server = null;
  public static Logger lgr = null;

  public static void main(String[] args) {
//    DbConnectionInfoObj connObj = null;
    try {
      //Load Application setting from the Config file
      loadInitSetting();
      lgr.log(LogLevel.getLevel(Constants.LOG_INFO),"\n\nStarting the Main Service Thread Insatance Size --> "+LoadProperties.instanceConnectionsTable.size());
      //Getting the Instance Name List
       Iterator instanceConnPoolObj =	LoadProperties.instanceConnectionsTable.values().iterator();

       if(instanceConnPoolObj.hasNext()){
         server = new ServerSocket(Constants.PORT_NUMBER);
         lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," Invoking -------- Scheduler Socket Service ---- PORT NUMBER---> " + Constants.PORT_NUMBER);
         SchedulerSocketService socketService = new SchedulerSocketService(server);
         socketService.start();
       }

     //Getting the Instance Information
     try {
       //Getting the Connection Instance Information
//               connObj = (DbConnectionInfoObj)instanceConnPoolObj.next();

       lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," Inst Name -->"+
               Constants.CARD_INSTANCE_DB_CONN.getConnectionName()+
               "<--Conn String-->"+Constants.CARD_INSTANCE_DB_CONN.getConnectionString()+
               "<--User-->"+Constants.CARD_INSTANCE_DB_CONN.getUserID()+
               "<--Password-->"+Constants.CARD_INSTANCE_DB_CONN.getPasswod());
       /************************* Payment Service ***********************/
       Logger transLogger = I2cLogger.getInstance(Constants.LOG_FILE_PATH +
                                                  Constants.PAYMENT_SERVICE +
                                                  File.separator +
                                                  Constants.PAYMENT_SERVICE + "-log-%g.log",
                                                  Constants.LOG_FILE_SIZE,
                                                  Constants.LOG_FILE_NO,
                                                  Constants.PAYMENT_SERVICE);

       BPTranscationScheduler bpTransSch = new BPTranscationScheduler(Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),transLogger);
       bpTransSch.start();
       //////////////////////////////////////////////////////////////////
//               lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," Before Creating Bill Payment File Processing Monitor Service Thread ");
//               PaymentServiceMonitor paymentServiceMonitor = new PaymentServiceMonitor(Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),"Bill Payment Scheduler Service", paymentLogger);
//               paymentServiceMonitor.start();
//
//               lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," Before Creating Bill Payment File Processing Service Thread ");
//               PaymentServiceThread paymentService = new PaymentServiceThread(Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),paymentServiceMonitor, paymentLogger);
//               boolean paymentDone = paymentService.process();
//
//               if(paymentDone) {
//                 processRespAdj(paymentLogger, paymentServiceMonitor);
//             } else {
//               boolean searchRespAdj = searchRespAdj();
//               if(searchRespAdj) {//manual placemnet of payment file on FTP
//                 processRespAdj(paymentLogger, paymentServiceMonitor);
//               }
//             }
///////////////////////////////////////////////////////////////////
//       /************************* Response Service **********************/
//               Logger responseLogger = I2cLogger.getInstance(Constants.LOG_FILE_PATH +
//                                                             Constants.RESPONSE_SERVICE +
//                                                             File.separator +
//                                                             Constants.RESPONSE_SERVICE + "-log-%g.log",
//                                                             Constants.LOG_FILE_SIZE,
//                                                             Constants.LOG_FILE_NO,
//                                                             Constants.RESPONSE_SERVICE);
//
//               lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," Before Creating Bill Response File Processing Monitor Service Thread ");
//               ResponseServiceMonitor responseServiceMonitor = new ResponseServiceMonitor(Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),"Bill Response Scheduler Service", responseLogger);
//               responseServiceMonitor.start();
//
//               lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," Before Creating Bill Response File Processing Service Thread ");
//               ResponseServiceThread responseService = new ResponseServiceThread(Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),responseServiceMonitor, responseLogger);
//               responseService.start();
//
//       /************************ Adjustment Service *********************/
//               Logger adjustmentLogger = I2cLogger.getInstance(Constants.LOG_FILE_PATH +
//                                                               Constants.ADJUSTMENT_SERVICE +
//                                                               File.separator +
//                                                               Constants.ADJUSTMENT_SERVICE + "-log-%g.log",
//                                                               Constants.LOG_FILE_SIZE,
//                                                               Constants.LOG_FILE_NO,
//                                                               Constants.ADJUSTMENT_SERVICE);
//
//               lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," Before Creating Bill Adjustment File Processing Monitor Service Thread ");
//               AdjustmentServiceMonitor adjustmentServiceMonitor = new AdjustmentServiceMonitor(Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),"Bill Response Scheduler Service", adjustmentLogger);
//               adjustmentServiceMonitor.start();
//
//               lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," Before Creating Bill Adjustment File Processing Service Thread ");
//               AdjustmentServiceThread adjustmentService = new AdjustmentServiceThread(Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),adjustmentServiceMonitor, adjustmentLogger);
//               adjustmentService.start();

       /************************* Payee API Logger ***********************/
       // Payee API log Path
       String payeeLogPath = Constants.LOG_FILE_PATH + Constants.PAYEE_SERVICE_API;

       Logger payeeApiLogger = I2cLogger.getInstance(payeeLogPath +
                                                     File.separator +
                                                     Constants.PAYEE_SERVICE_API + "log-%g.log",
                                                     Constants.LOG_FILE_SIZE,
                                                     Constants.LOG_FILE_NO,
                                                     "PayeeServiceThread");

       /************************ Payee Service ***************************/
       Logger payeeLogger = I2cLogger.getInstance(Constants.LOG_FILE_PATH +
                                                  Constants.PAYEE_SERVICE +
                                                  File.separator +
                                                  Constants.PAYEE_SERVICE + "-log-%g.log",
                                                  Constants.LOG_FILE_SIZE,
                                                  Constants.LOG_FILE_NO,
                                                  Constants.PAYEE_SERVICE);

       lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," Before Creating Payee File Processing Monitor Service Thread ");
       PayeeServiceMonitor payeeServiceMonitor = new PayeeServiceMonitor(Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),"Bill Payment Scheduler Service", payeeLogger);
       payeeServiceMonitor.start();

       lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," Before Creating Payee File Processing Service Thread ");
       PayeeServiceThread payeeService = new PayeeServiceThread(Constants.CARD_INSTANCE_DB_CONN.getConnectionName(),payeeServiceMonitor, payeeLogger, payeeApiLogger);
       payeeService.start();

     }
     catch (Throwable e){
       lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Creating new Threads loop -->"+e);
     }
       } catch (Exception ex) {
         lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Main Service Thread -->"+ex);
       }//end catch
     }//end main

//  private static void processRespAdj(Logger paymentLogger,
//                                     PaymentServiceMonitor
//                                     paymentServiceMonitor) throws
//      InterruptedException {
//      Thread.sleep(100000);
//      boolean adjustmentDone = false;
//      boolean responseDone = false;
//      while (responseDone == false || adjustmentDone == false) {
//        lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
//              " Before Creating Bill Response File Processing Service Thread ");
//      if (responseDone == false){
//        ResponseServiceThread responseService = new
//            ResponseServiceThread(Constants.
//                                  CARD_INSTANCE_DB_CONN.
//                                  getConnectionName(),
//                                  paymentServiceMonitor,
//                                  paymentLogger);
//        responseDone = responseService.process();
//      }
//      if (adjustmentDone == false){
//        lgr.log(LogLevel.getLevel(Constants.LOG_FINEST),
//            " Before Creating Bill Adjustment File Processing Service Thread ");
//        AdjustmentServiceThread adjustmentService = new
//            AdjustmentServiceThread(Constants.CARD_INSTANCE_DB_CONN.
//                                    getConnectionName(),
//                                    paymentServiceMonitor,
//                                    paymentLogger);
//        adjustmentDone = adjustmentService.process();
//      }
//      Thread.sleep(100000);
//    }
//  }



  /**
   * Method for loading the initial settings
   * @return: true or false
   * @throws IOException
   */
  public static boolean loadInitSetting()
      throws IOException {
    boolean firstTime = false;

    try {
      System.out.println("Load Application Settings ");
      String filePath = System.getProperty("user.dir") + File.separator + Constants.CONFIGURATION_FILE;
      System.out.println("The Config File Path is -->"+filePath);

      if (isUpdated(filePath)) {
            System.out.println(" Enter in Loading Values from Config File ");
            LoadProperties.loadInfo(filePath);

            lgr = I2cLogger.getInstance(Constants.LOG_FILE_PATH +
                                            File.separator +
                                            Constants.LOG_FILE_NAME + "-%g.log",
                                            Constants.LOG_FILE_SIZE,
                                            Constants.LOG_FILE_NO,
                                            Constants.LOG_CONTEXT_NAME);

            System.out.println(" Constants.DB_CONNECTION_STRING-"+Constants.DB_CONNECTION_STRING+"_");
            System.out.println(" Constants.DB_USER_NAME-"+Constants.DB_USER_NAME+"_");
            System.out.println(" Constants.DB_USER_PASSWORD-"+Constants.DB_USER_PASSWORD+"_");
            System.out.println(" Constants.DB_DRIVER-"+Constants.DB_DRIVER_NAME+"_");
            System.out.println(" Constants.LOG_FILE_SIZE-"+Constants.LOG_FILE_SIZE+"_");
            System.out.println(" Constants.LOG_FILE_NO-"+Constants.LOG_FILE_NO+"_");
            System.out.println(" Constants.LOG_FILE_PATH-"+Constants.LOG_FILE_PATH+"_");
            System.out.println(" Constants.LOG_FILE_NAME-"+Constants.LOG_FILE_NAME+"_");
            System.out.println(" Constants.OUTPUT_PATH-"+Constants.OUTPUT_PATH+"_");
            System.out.println(" Constants.MAIL_REPORT_MESSAGE-"+Constants.MAIL_REPORT_MESSAGE+"_");
            System.out.println(" Constants.MAIL_REPORT_FROM-"+Constants.MAIL_REPORT_FROM+"_");
            System.out.println(" Constants.MAIL_REPORT_SUBJECT-"+Constants.MAIL_REPORT_SUBJECT+"_");
            System.out.println(" Constants.MAIL_REPORT_TO-"+Constants.MAIL_REPORT_TO+"_");
            System.out.println(" Constants.MAIL_SMTP-"+Constants.MAIL_SMTP+"_");
            System.out.println(" Constants.INCREAMENT_EMAIL_COUNTER-"+Constants.INCREAMENT_EMAIL_COUNTER+"_");
            System.out.println(" Constants.MAX_LIMIT_SEND_EMAIL_COUNTER-"+Constants.MAX_LIMIT_SEND_EMAIL_COUNTER+"_");
            System.out.println(" Constants.START_REPORT_FAILURE_EMAIL-"+Constants.START_REPORT_FAILURE_EMAIL+"_");
            System.out.println(" Constants.PAYMENT_SLEEP_TIME-"+Constants.PAYMENT_SLEEP_TIME+"_");
            System.out.println(" Constants.RESPONSE_SLEEP_TIME-"+Constants.RESPONSE_SLEEP_TIME+"_");
            System.out.println(" Constants.ADJUSTMENT_SLEEP_TIME-"+Constants.ADJUSTMENT_SLEEP_TIME+"_");
            System.out.println(" Constants.PAYEE_SLEEP_TIME-"+Constants.PAYEE_SLEEP_TIME+"_");
            System.out.println(" Constants.DEFAULT_SLEEP_TIME-"+Constants.DEFAULT_SLEEP_TIME+"_");

        } else {
            System.out.println("Config File is not Updated. No Need for Loading again ");
        }//end else

//--------------- Set the MACHINE IP here so it is avaiable throughout the application
        if(Constants.getMachineIP() == null )
        {
          Constants.setMachineIP();
        }


    } catch (IOException e) {
        System.out.println("Unable to Load the Settings. Server is unable to start/continue -->"+e);
        throw e;
    }//end catch
    return true;
  }//end load method

  /**
 * Checking the configuration file is update or not
 * @param fileName: configuration file
 * @return: changed or not
 */
public static boolean isUpdated(String fileName) {
  try {
    File configFile = new File(fileName);
    System.out.println("Checking Config File Updation lastUpdateTime -->" +
                       lastUpdateTime + " New Time -->" +
                       configFile.lastModified());

    if (!configFile.exists())
      return true;

    if (lastUpdateTime != configFile.lastModified()) {
      lastUpdateTime = configFile.lastModified();
      return true;
    } //end if
  }
  catch (Exception ex) {
    System.out.println("Exception in checking updation of Config file -->" +
                       ex);
    return false;
  }
  return false;
} //end method
}
