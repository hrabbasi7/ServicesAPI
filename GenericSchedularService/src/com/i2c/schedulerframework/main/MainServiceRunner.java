package com.i2c.schedulerframework.main;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import com.i2c.schedulerframework.handler.*;
import com.i2c.schedulerframework.monitor.*;
import com.i2c.schedulerframework.util.ConfigrationHandler;
import com.i2c.schedulerframework.util.BaseConstants;
import com.i2c.schedulerframework.model.DbConfigBean;
import com.i2c.schedulerframework.util.CommonUtilities;
import com.i2c.utils.logging.*;
import com.i2c.schedulerframework.util.BaseConstants;
import java.lang.reflect.Constructor;

/**
 * <p>Title: Generic Scheduler Frame work</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: i2cinc</p>
 * @author hrabbasi
 * @version 1.0
 */

public class MainServiceRunner {

  private static long lastUpdateTime = -1;
  private static ServerSocket server = null;
  public static Logger mainLogger = null;

  public MainServiceRunner(){
    //Load Application setting from the Config file
      try {
        loadInitSetting();
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
  }

  public void serviceRunner() {
    DbConfigBean connObj = null;
    try {

      mainLogger.log(I2cLogger.FINE,
                     "[MainServiceRunner].[serviceRunner] Starting the Main Service -- Insatance Connections Size --> " +
                     ConfigrationHandler.instanceConnectionsTable.size());
      //Getting the Instance Name List
      Iterator instanceConnPoolObj = ConfigrationHandler.instanceConnectionsTable.values().iterator();

      if (instanceConnPoolObj.hasNext()) {
        int port = BaseConstants.PORT_NUMBER;
        server = new ServerSocket(port);
        mainLogger.log(I2cLogger.INFO,
                       "[MainServiceRunner].[serviceRunner] Invoking --- Scheduler Socket Service --- PORT NUMBER---> " +
                       port);
        StatusInformerSocket socketService = new StatusInformerSocket(server, mainLogger);
        socketService.start();
      }
      mainLogger.log(I2cLogger.INFO,
                     "[MainServiceRunner].[serviceRunner] Card Instance DB Connection Name -->" + BaseConstants.CARD_INSTANCE_DB_CONN.getConnectionName() +
                     "<--Conn String-->" + BaseConstants.CARD_INSTANCE_DB_CONN.getConnectionString() +
                     "<--User-->" + BaseConstants.CARD_INSTANCE_DB_CONN.getUserID() +
                     "<--Password-->" + BaseConstants.CARD_INSTANCE_DB_CONN.getPasswod());
      //Getting the Instance Information
      try {
        while(instanceConnPoolObj.hasNext()) {

          //Getting the Connection Instance Information
             connObj = (DbConfigBean)instanceConnPoolObj.next();

             SchedulerHangNotifier genericSchedulerMonitor = new SchedulerHangNotifier(connObj.getConnectionName(),
                 "Generic Scheduler Service Monitor", mainLogger);
             genericSchedulerMonitor.start();

        /*******************************************************************/
            BaseHandler baseHandler = null;
            try {
//              baseHandler = (BaseHandler) (Class.forName(BaseConstants.BL_DERIVED_CLASS_NAME).newInstance());
              //create the handler object
              Class[] argClass = new Class[] {String.class, com.i2c.schedulerframework.monitor.SchedulerHangNotifier.class};
              Object[] args = new Object[] {connObj.getConnectionName(), genericSchedulerMonitor};
              //create the handler object
              Class baseHandlerClass = Class.forName(BaseConstants.BL_DERIVED_CLASS_NAME);
              Constructor handlerClassConstructor = baseHandlerClass.getConstructor(argClass);
              baseHandler = (BaseHandler) handlerClassConstructor.newInstance(args);

            }
            catch (InstantiationException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            catch (IllegalAccessException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            mainLogger.log(I2cLogger.INFO,
                           "[MainServiceRunner].[serviceRunner] Starting the base Handler Thread");
            Thread baseHanderlThread = new Thread(baseHandler);
            baseHanderlThread.start();
          } // end while

//        /************************* Payee API Logger ***********************/
//        // Payee API log Path
//        String payeeLogPath = BaseConstants.LOG_FILE_PATH +
//            Constant.PAYEE_SERVICE_API;
//
//        Logger payeeApiLogger = I2cLogger.getInstance(payeeLogPath +
//            File.separator +
//            Constant.PAYEE_SERVICE_API + "log-%g.log",
//            BaseConstants.LOG_FILE_SIZE,
//            BaseConstants.LOG_FILE_NO,
//            "PayeeServiceThread");
//
//        /************************ Payee Service ***************************/
//        Logger payeeLogger = I2cLogger.getInstance(BaseConstants.LOG_FILE_PATH +
//            Constant.PAYEE_SERVICE + File.separator +
//            Constant.PAYEE_SERVICE + "-log-%g.log",
//            BaseConstants.LOG_FILE_SIZE,
//            BaseConstants.LOG_FILE_NO,
//            Constant.PAYEE_SERVICE);
//
//        mainLogger.log(I2cLogger.FINEST,
//            " Before Creating Payee File Processing Monitor Service Thread ");
//        SchedulerHangNotifier payeeServiceMonitor = new SchedulerHangNotifier(
//            BaseConstants.CARD_INSTANCE_DB_CONN.getConnectionName(),
//            "Bill Payment Scheduler Service", payeeLogger);
//        payeeServiceMonitor.start();
//
//        mainLogger.log(I2cLogger.FINEST,
//                       " Before Creating Payee File Processing Service Thread ");
//        PayeeServiceThread payeeService = new PayeeServiceThread(BaseConstants.
//            CARD_INSTANCE_DB_CONN.getConnectionName(), payeeServiceMonitor,
//            payeeLogger, payeeApiLogger);
//        payeeService.start();

      }
      catch (Throwable e) {
        mainLogger.log(I2cLogger.WARNING,
                       "[MainServiceRunner].[serviceRunner] Exception in Creating new Threads loop -->" + e);
      }
    }
    catch (Exception ex) {
      mainLogger.log(I2cLogger.WARNING,
                     "[MainServiceRunner].[serviceRunner] Exception in Main Service Thread -->" + ex);
    } //end catch
  } //end serviceRunner

  /**
   * Method for loading the initial settings
   * @return: true or false
   * @throws IOException
   */
  public static boolean loadInitSetting() throws IOException {
    boolean firstTime = false;

    try {
      System.out.println("[MainServiceRunner].[loadInitSetting]<<----Load Generic Scheduler Configration Settings---->>");
      String filePath = System.getProperty("user.dir") + File.separator + BaseConstants.CONFIGURATION_FILE;
      System.out.println("[MainServiceRunner].[loadInitSetting] Generic Scheduler Config File Path is ---> " + filePath);

      if (isUpdated(filePath)) {
        System.out.println("[MainServiceRunner].[loadInitSetting] Start Loading Values from Config File");

        ConfigrationHandler.loadBaseConfigFile(filePath);

        mainLogger = I2cLogger.getInstance(BaseConstants.LOG_FILE_PATH + File.separator +
                                           BaseConstants.LOG_FILE_NAME + "-%g.log",
                                           BaseConstants.LOG_FILE_SIZE,
                                           BaseConstants.LOG_FILE_NO,
                                           BaseConstants.LOG_CONTEXT_NAME);

        System.out.println(" BaseConstants.DB_CONNECTION_STRING-" + BaseConstants.DB_CONNECTION_STRING + "_");
        System.out.println(" BaseConstants.DB_USER_NAME-" + BaseConstants.DB_USER_NAME + "_");
        System.out.println(" BaseConstants.DB_USER_PASSWORD-" + BaseConstants.DB_USER_PASSWORD + "_");
        System.out.println(" BaseConstants.DB_DRIVER-" + BaseConstants.DB_DRIVER_NAME + "_");
        System.out.println(" BaseConstants.LOG_FILE_SIZE-" + BaseConstants.LOG_FILE_SIZE + "_");
        System.out.println(" BaseConstants.LOG_FILE_NO-" + BaseConstants.LOG_FILE_NO + "_");
        System.out.println(" BaseConstants.LOG_FILE_PATH-" + BaseConstants.LOG_FILE_PATH + "_");
        System.out.println(" BaseConstants.LOG_FILE_NAME-" + BaseConstants.LOG_FILE_NAME + "_");
        System.out.println(" BaseConstants.OUTPUT_PATH-" + BaseConstants.OUTPUT_PATH + "_");
        System.out.println(" BaseConstants.MAIL_REPORT_MESSAGE-" + BaseConstants.MAIL_REPORT_MESSAGE + "_");
        System.out.println(" BaseConstants.MAIL_REPORT_FROM-" + BaseConstants.MAIL_REPORT_FROM + "_");
        System.out.println(" BaseConstants.MAIL_REPORT_SUBJECT-" + BaseConstants.MAIL_REPORT_SUBJECT + "_");
        System.out.println(" BaseConstants.MAIL_REPORT_TO-" + BaseConstants.MAIL_REPORT_TO + "_");
        System.out.println(" BaseConstants.MAIL_SMTP-" + BaseConstants.MAIL_SMTP + "_");
        System.out.println(" BaseConstants.DEFAULT_SLEEP_TIME-" + BaseConstants.DEFAULT_SLEEP_TIME + "_");

      }
      else {
        System.out.println("[MainServiceRunner].[loadInitSetting] Config File is not Updated. No Need for Loading again ");
      } //end else

      /* Set the MACHINE IP here so it is avaiable throughout the application */
      if (CommonUtilities.getMachineIP() == null) {
        CommonUtilities.setMachineIP();
      }

    }
    catch (IOException ioe) {
      System.out.println("[MainServiceRunner].[loadInitSetting] Unable to Load the Settings. Generic Scheduler Server is unable to start/continue " +
                         ioe);
      throw ioe;
    } //end catch
    return true;
  } //end load properties method





  /**
   * Checking the configuration file is update or not
   * @param fileName: configuration file
   * @return: changed or not
   */
  public static boolean isUpdated(String fileName) {
    try {
      File configFile = new File(fileName);
      System.out.println(
          "[MainServiceRunner].[isUpdated] Checking Config File Updation lastUpdateTime -->" +
          lastUpdateTime + " New Time -->" +
          configFile.lastModified());

      if (!configFile.exists()) {
        return true;
      }

      if (lastUpdateTime != configFile.lastModified()) {
        lastUpdateTime = configFile.lastModified();
        return true;
      } //end if
    }
    catch (Exception ex) {
      System.out.println(
          "[MainServiceRunner].[isUpdated] Exception in checking updation of Config file -->" +
          ex);
      ex.printStackTrace();
      return false;
    }
    return false;
  } //end method
}
