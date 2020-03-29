package com.i2c.service.couponschedularservice.main;

import com.i2c.service.util.*;
import com.i2c.service.couponschedularservice.CouponGenerationServiceThread;

import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import com.i2c.service.couponschedularservice.monitor.CouponGenerationMonitorThread;

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

  public static void main(String[] args) {
    DbConnectionInfoObj connObj = null;
    try {
      //Load Application setting from the Config file
      loadInitSetting();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"\n\nStarting the Main Service Thread Insatance Size -->"+LoadProperties.instanceConnectionsTable.size());
      //Getting the Instance Name List
       Iterator instanceConnPoolObj =	LoadProperties.instanceConnectionsTable.values().iterator();

       if(instanceConnPoolObj.hasNext()){
         server = new ServerSocket(Constants.PORT_NUMBER);
         CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Invoking --------Scheduler Socket Service ---- PORT NUMBER--->" + Constants.PORT_NUMBER);
         SchedulerSocketService socketService = new SchedulerSocketService(server);
         socketService.start();
       }

     //Getting the Instance Information
       while(instanceConnPoolObj.hasNext()) {
         try {
           //Getting the Connection Instance Information
           connObj = (DbConnectionInfoObj)instanceConnPoolObj.next();
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Inst Name -->"+connObj.getConnectionName()+"<--Conn String-->"+connObj.getConnectionString()+"<--User-->"+connObj.getUserID()+"<--Password-->"+connObj.getPasswod());

           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Before Creating Alert Execution Monitor Service Thread ");
           CouponGenerationMonitorThread couponGenMonitor = new CouponGenerationMonitorThread(connObj.getConnectionName(),"Coupon Generation Scheduler Service");
           couponGenMonitor.start();

           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Before Creating Coupon generation Service Thread ");
           CouponGenerationServiceThread coupGenService = new CouponGenerationServiceThread(connObj.getConnectionName(), couponGenMonitor);
           coupGenService.start();

         }
         catch (Throwable e){
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Creating new Threads loop -->"+e);
         }//end catch
       }//end while
     } catch (Exception ex) {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Main Service Thread -->"+ex);
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
      System.out.println("Load Application Settings ");
      String filePath = System.getProperty("user.dir") + File.separator + Constants.CONFIGURATION_FILE;
      System.out.println("The Config File Path is -->"+filePath);

      if (isUpdated(filePath)) {
            System.out.println(" Enter in Loading Values from Config File ");
            LoadProperties.loadInfo(filePath);

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