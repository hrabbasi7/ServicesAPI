package com.i2c.service.couponschedularservice;

import com.i2c.service.util.*;
import com.i2c.schedulerframework.monitor.SchedulerHangNotifier;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import com.i2c.utils.logging.I2cLogger;
import com.i2c.schedulerframework.monitor.*;
import com.i2c.schedulerframework.util.CommonUtilities;
import com.i2c.schedulerframework.handler.BaseHandler;
import com.i2c.service.couponschedularservice.bl.CouponGenerationServiceThread;
import com.i2c.schedulerframework.model.DbConfigBean;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Haroon ur Rashid Abbasi
 * @version 1.0
 */

public class ServiceMain {

  private static long lastUpdateTime = -1;
  private static ServerSocket server = null;
  private static Logger couponLogger = null;
//  private static MainServiceRunner runner = null;

  public static void main(String[] args) {

//    runner = new MainServiceRunner();
//    runner.serviceRunner();


    DbConfigBean connObj = null;
    try {
      //Load Application setting from the Config file
      loadInitSetting();
      couponLogger.log(I2cLogger.INFO,"[ServiceMain].[main] ==>Starting the Main Service Thread Insatance Size -->"+LoadProperties.instanceConnectionsTable.size());
      //Getting the Instance Name List
       Iterator instanceConnPoolObj =	LoadProperties.instanceConnectionsTable.values().iterator();

       if(instanceConnPoolObj.hasNext()){
         server = new ServerSocket(Constants.PORT_NUMBER);
         couponLogger.log(I2cLogger.FINEST,"[ServiceMain].[main] Invoking ----Scheduler Socket Service ---- PORT NUMBER--->" + Constants.PORT_NUMBER);
         StatusInformerSocket socketService = new StatusInformerSocket(server, couponLogger);
         socketService.start();
       }

     //Getting the Instance Information
       while(instanceConnPoolObj.hasNext()) {
         try {
           //Getting the Connection Instance Information
           connObj = (DbConfigBean)instanceConnPoolObj.next();
           couponLogger.log(I2cLogger.INFO,"[ServiceMain].[main] Inst Name -->"+connObj.getConnectionName()+"<--Conn String-->"+connObj.getConnectionString()+"<--User-->"+connObj.getUserID()+"<--Password-->"+connObj.getPasswod());

           couponLogger.log(I2cLogger.INFO,"[ServiceMain].[main] Before Creating Alert Execution Monitor Service Thread ");

           SchedulerHangNotifier couponGenMonitor = new SchedulerHangNotifier(connObj.getConnectionName(),"Coupon Generation Scheduler Service", couponLogger);
           couponGenMonitor.start();

           couponLogger.log(I2cLogger.INFO,"[ServiceMain].[main] Before Creating Coupon generation Service Thread ");

//           CouponGenerationServiceThread coupGenService = new CouponGenerationServiceThread(connObj.getConnectionName(), couponGenMonitor);
//           coupGenService.start();

           BaseHandler coupGenService = null;
           coupGenService = (BaseHandler) new CouponGenerationServiceThread(connObj.getConnectionName(), couponGenMonitor);
           Thread businessHandlerThread = new Thread(coupGenService);
           businessHandlerThread.start();

         }
         catch (Throwable e){
           couponLogger.log(I2cLogger.WARNING,"[ServiceMain].[main] Exception in Creating new Threads loop -->"+e);
           e.printStackTrace();
         }//end catch
       }//end while
     } catch (Exception ex) {
       couponLogger.log(I2cLogger.WARNING,"[ServiceMain].[main] Exception in Main Service Thread -->"+ex);
       ex.printStackTrace();
     }//end catch
   }//end main

  /**
   * Method for loading the initial settings
   * @return: true or false
   * @throws IOException
   */
  public static boolean loadInitSetting()
      throws IOException {

    try {
      System.out.println("[ServiceMain].[loadInitSetting] Set Coupon Scheduler as Main Scheduler" +
                         " to get instances DBConns from Generic Scheduler ");
      Constants.SCHEDULER_ID = Constants.USE_COUPON_GEN;

      System.out.println("[ServiceMain].[loadInitSetting] Load Initial Coupon Settings ");
      String filePath = System.getProperty("user.dir") + File.separator + Constants.CONFIGURATION_FILE;
      System.out.println("[ServiceMain].[loadInitSetting] The Config File Path is -->"+filePath);

      if (isUpdated(filePath)) {
            System.out.println("[ServiceMain].[loadInitSetting] Enter in Loading Values from Config File ");
            LoadProperties.loadInfo(filePath);

            couponLogger = I2cLogger.getInstance(Constants.LOG_FILE_PATH + File.separator +
                                                 Constants.LOG_FILE_NAME + "-%g.log",
                                                 Constants.LOG_FILE_SIZE,
                                                 Constants.LOG_FILE_NO,
                                                 Constants.LOCAL_CONTEXT_NAME);

            System.out.println(" Constants.SCHEDULER_ID-"+Constants.SCHEDULER_ID+"_");
            System.out.println(" Constants.DB_CONNECTION_STRING-"+Constants.DB_CONNECTION_STRING+"_");
            System.out.println(" Constants.DB_USER_NAME-"+Constants.DB_USER_NAME+"_");
            System.out.println(" Constants.DB_USER_PASSWORD-"+Constants.DB_USER_PASSWORD+"_");
            System.out.println(" Constants.DB_DRIVER-"+Constants.DB_DRIVER_NAME+"_");
            System.out.println(" Constants.DEFAULT_SLEEP_TIME-"+Constants.DEFAULT_SLEEP_TIME+"_");
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


        } else {
            System.out.println("[ServiceMain].[loadInitSetting] Config File is not Updated. No Need for Loading again ");
        }//end else

//--------------- Set the MACHINE IP here so it is avaiable throughout the application
        if(CommonUtilities.getMachineIP() == null )
        {
          CommonUtilities.getMachineIP();
        }


    } catch (IOException e) {
        System.out.println("[ServiceMain].[loadInitSetting] Unable to Load the Settings. Server is unable to start/continue -->"+e);
        e.printStackTrace();
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
    System.out.println("[ServiceMain].[isUpdated] Checking Config File Updation lastUpdateTime -->" +
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
    System.out.println("[ServiceMain].[isUpdated] Exception in checking updation of Config file -->" +
                       ex);
    return false;
  }
  return false;
} //end method
}
