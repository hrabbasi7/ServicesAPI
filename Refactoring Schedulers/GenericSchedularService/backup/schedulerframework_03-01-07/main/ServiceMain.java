package com.i2c.schedulerframework.main;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import com.i2c.schedulerframework.handler.*;
import com.i2c.schedulerframework.monitor.*;
import com.i2c.schedulerframework.util.ConfigrationHandler;
import com.i2c.schedulerframework.util.Constant;
import com.i2c.schedulerframework.model.DbConfigBean;
import com.i2c.schedulerframework.util.CommonUtilities;
import com.i2c.utils.logging.*;

/**
 * <p>Title: Generic Scheduler Frame work</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: i2cinc</p>
 * @author hrabbasi
 * @version 1.0
 */

public class ServiceMain {

  private static long lastUpdateTime = -1;
  private static ServerSocket server = null;
  public static Logger mainLogger = null;

  static String className = "com.i2c.GSFW.bl.BLDerived";

  public static void main(String[] args) {

    /*******************************************************************/
    BaseHandler baseHandler = null;
                try {
                        baseHandler = (BaseHandler) (Class.forName(className).newInstance());
                } catch (InstantiationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                Thread baseHanderlThread = new Thread(baseHandler);
                baseHanderlThread.start();

    /*******************************************************************/

//    DbConnectionInfoObj connObj = null;
    try {
      //Load Application setting from the Config file
      loadInitSetting();
      mainLogger.log(I2cLogger.INFO,"\n\nStarting the Main Service Thread Insatance Size --> "+ConfigrationHandler.instanceConnectionsTable.size());
      //Getting the Instance Name List
       Iterator instanceConnPoolObj =	ConfigrationHandler.instanceConnectionsTable.values().iterator();

       if(instanceConnPoolObj.hasNext()){
         int port = Constant.PORT_NUMBER;
         server = new ServerSocket(port);
         mainLogger.log(I2cLogger.FINEST," Invoking -------- Scheduler Socket Service ---- PORT NUMBER---> " + port);
         StatusInformerSocket socketService = new StatusInformerSocket(server, mainLogger);
         socketService.start();
       }

     //Getting the Instance Information
     try {
       //Getting the Connection Instance Information
//               connObj = (DbConnectionInfoObj)instanceConnPoolObj.next();

       mainLogger.log(I2cLogger.INFO," Instance Name -->"+
               Constant.CARD_INSTANCE_DB_CONN.getConnectionName()+
               "<--Conn String-->"+Constant.CARD_INSTANCE_DB_CONN.getConnectionString()+
               "<--User-->"+Constant.CARD_INSTANCE_DB_CONN.getUserID()+
               "<--Password-->"+Constant.CARD_INSTANCE_DB_CONN.getPasswod());
       /************************* Payment Service ***********************/
//       Logger transLogger = I2cLogger.getInstance(Constant.LOG_FILE_PATH +
//                                                  Constant.PAYMENT_SERVICE +
//                                                  File.separator +
//                                                  Constant.PAYMENT_SERVICE + "-log-%g.log",
//                                                  Constant.LOG_FILE_SIZE,
//                                                  Constant.LOG_FILE_NO,
//                                                  Constant.PAYMENT_SERVICE);
//       BPTranscationScheduler bpTransSch = new BPTranscationScheduler(Constant.CARD_INSTANCE_DB_CONN.getConnectionName(),transLogger);
//       bpTransSch.start();
// ////////////////////////////////////////////////////////////////
//               lgr.log(LogLevel.getLevel(Constant.FINEST," Before Creating Bill Payment File Processing Monitor Service Thread ");
//               PaymentServiceMonitor paymentServiceMonitor = new PaymentServiceMonitor(Constant.CARD_INSTANCE_DB_CONN.getConnectionName(),"Bill Payment Scheduler Service", paymentLogger);
//               paymentServiceMonitor.start();
//
//               lgr.log(LogLevel.getLevel(Constant.FINEST," Before Creating Bill Payment File Processing Service Thread ");
//               PaymentServiceThread paymentService = new PaymentServiceThread(Constant.CARD_INSTANCE_DB_CONN.getConnectionName(),paymentServiceMonitor, paymentLogger);
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
//               Logger responseLogger = I2cLogger.getInstance(Constant.LOG_FILE_PATH +
//                                                             Constant.RESPONSE_SERVICE +
//                                                             File.separator +
//                                                             Constant.RESPONSE_SERVICE + "-log-%g.log",
//                                                             Constant.LOG_FILE_SIZE,
//                                                             Constant.LOG_FILE_NO,
//                                                             Constant.RESPONSE_SERVICE);
//
//               lgr.log(LogLevel.getLevel(Constant.FINEST," Before Creating Bill Response File Processing Monitor Service Thread ");
//               ResponseServiceMonitor responseServiceMonitor = new ResponseServiceMonitor(Constant.CARD_INSTANCE_DB_CONN.getConnectionName(),"Bill Response Scheduler Service", responseLogger);
//               responseServiceMonitor.start();
//
//               lgr.log(LogLevel.getLevel(Constant.FINEST," Before Creating Bill Response File Processing Service Thread ");
//               ResponseServiceThread responseService = new ResponseServiceThread(Constant.CARD_INSTANCE_DB_CONN.getConnectionName(),responseServiceMonitor, responseLogger);
//               responseService.start();
//
//       /************************ Adjustment Service *********************/
//               Logger adjustmentLogger = I2cLogger.getInstance(Constant.LOG_FILE_PATH +
//                                                               Constant.ADJUSTMENT_SERVICE +
//                                                               File.separator +
//                                                               Constant.ADJUSTMENT_SERVICE + "-log-%g.log",
//                                                               Constant.LOG_FILE_SIZE,
//                                                               Constant.LOG_FILE_NO,
//                                                               Constant.ADJUSTMENT_SERVICE);
//
//               lgr.log(LogLevel.getLevel(Constant.FINEST," Before Creating Bill Adjustment File Processing Monitor Service Thread ");
//               AdjustmentServiceMonitor adjustmentServiceMonitor = new AdjustmentServiceMonitor(Constant.CARD_INSTANCE_DB_CONN.getConnectionName(),"Bill Response Scheduler Service", adjustmentLogger);
//               adjustmentServiceMonitor.start();
//
//               lgr.log(LogLevel.getLevel(Constant.FINEST," Before Creating Bill Adjustment File Processing Service Thread ");
//               AdjustmentServiceThread adjustmentService = new AdjustmentServiceThread(Constant.CARD_INSTANCE_DB_CONN.getConnectionName(),adjustmentServiceMonitor, adjustmentLogger);
//               adjustmentService.start();

       /************************* Payee API Logger ***********************/
       // Payee API log Path
       String payeeLogPath = Constant.LOG_FILE_PATH + Constant.PAYEE_SERVICE_API;

       Logger payeeApiLogger = I2cLogger.getInstance(payeeLogPath +
                                                     File.separator +
                                                     Constant.PAYEE_SERVICE_API + "log-%g.log",
                                                     Constant.LOG_FILE_SIZE,
                                                     Constant.LOG_FILE_NO,
                                                     "PayeeServiceThread");

       /************************ Payee Service ***************************/
       Logger payeeLogger = I2cLogger.getInstance(Constant.LOG_FILE_PATH +
                                                  Constant.PAYEE_SERVICE +
                                                  File.separator +
                                                  Constant.PAYEE_SERVICE + "-log-%g.log",
                                                  Constant.LOG_FILE_SIZE,
                                                  Constant.LOG_FILE_NO,
                                                  Constant.PAYEE_SERVICE);

       mainLogger.log(I2cLogger.FINEST," Before Creating Payee File Processing Monitor Service Thread ");
       SchedulerHangNotifier payeeServiceMonitor = new SchedulerHangNotifier(Constant.CARD_INSTANCE_DB_CONN.getConnectionName(),"Bill Payment Scheduler Service", payeeLogger);
       payeeServiceMonitor.start();

       mainLogger.log(I2cLogger.FINEST," Before Creating Payee File Processing Service Thread ");
//       PayeeServiceThread payeeService = new PayeeServiceThread(Constant.CARD_INSTANCE_DB_CONN.getConnectionName(),payeeServiceMonitor, payeeLogger, payeeApiLogger);
//       payeeService.start();

     }
     catch (Throwable e){
       mainLogger.log(I2cLogger.WARNING,"Exception in Creating new Threads loop -->"+e);
     }
       } catch (Exception ex) {
         mainLogger.log(I2cLogger.WARNING,"Exception in Main Service Thread -->"+ex);
       }//end catch
     }//end main

  /**
   * Method for loading the initial settings
   * @return: true or false
   * @throws IOException
   */
  public static boolean loadInitSetting()
      throws IOException {
    boolean firstTime = false;

    try {
      System.out.println("<< Load Generic Scheduler Configration Settings >>");
      String filePath = System.getProperty("user.dir") + File.separator + Constant.CONFIGURATION_FILE;
      System.out.println("The Config File Path is ---> " + filePath);

      if (isUpdated(filePath)) {
            System.out.println("Enter in Loading Values from Config File ");
            ConfigrationHandler.loadConfigFile(filePath);

            mainLogger = I2cLogger.getInstance(Constant.LOG_FILE_PATH +
                                            File.separator +
                                            Constant.LOG_FILE_NAME + "-%g.log",
                                            Constant.LOG_FILE_SIZE,
                                            Constant.LOG_FILE_NO,
                                            Constant.LOG_CONTEXT_NAME);

            System.out.println(" Constant.DB_CONNECTION_STRING-"+Constant.DB_CONNECTION_STRING+"_");
            System.out.println(" Constant.DB_USER_NAME-"+Constant.DB_USER_NAME+"_");
            System.out.println(" Constant.DB_USER_PASSWORD-"+Constant.DB_USER_PASSWORD+"_");
            System.out.println(" Constant.DB_DRIVER-"+Constant.DB_DRIVER_NAME+"_");
            System.out.println(" Constant.LOG_FILE_SIZE-"+Constant.LOG_FILE_SIZE+"_");
            System.out.println(" Constant.LOG_FILE_NO-"+Constant.LOG_FILE_NO+"_");
            System.out.println(" Constant.LOG_FILE_PATH-"+Constant.LOG_FILE_PATH+"_");
            System.out.println(" Constant.LOG_FILE_NAME-"+Constant.LOG_FILE_NAME+"_");
            System.out.println(" Constant.OUTPUT_PATH-"+Constant.OUTPUT_PATH+"_");
            System.out.println(" Constant.MAIL_REPORT_MESSAGE-"+Constant.MAIL_REPORT_MESSAGE+"_");
            System.out.println(" Constant.MAIL_REPORT_FROM-"+Constant.MAIL_REPORT_FROM+"_");
            System.out.println(" Constant.MAIL_REPORT_SUBJECT-"+Constant.MAIL_REPORT_SUBJECT+"_");
            System.out.println(" Constant.MAIL_REPORT_TO-"+Constant.MAIL_REPORT_TO+"_");
            System.out.println(" Constant.MAIL_SMTP-"+Constant.MAIL_SMTP+"_");
            System.out.println(" Constant.DEFAULT_SLEEP_TIME-"+Constant.DEFAULT_SLEEP_TIME+"_");

        } else {
            System.out.println("Config File is not Updated. No Need for Loading again ");
        }//end else

        /* Set the MACHINE IP here so it is avaiable throughout the application */
        if(CommonUtilities.getMachineIP() == null )
        {
          CommonUtilities.setMachineIP();
        }

    } catch (IOException ioe) {
        System.out.println("Unable to Load the Settings. Scheduler Server is unable to start/continue --> " + ioe);
        throw ioe;
    }//end catch
    return true;
  }//end load properties method

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
