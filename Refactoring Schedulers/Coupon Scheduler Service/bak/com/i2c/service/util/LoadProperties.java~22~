package com.i2c.service.util;

import java.io.*;
import java.util.*;
import com.i2c.schedulerframework.util.ConfigrationHandler;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2007
 * Company:      i2c Inc.
 * @author habbasi
 * @version 1.0
 */

/**
 * This class is a utility class used to read and load the property file have
 * keys and values with a typical format i.e., key=value
 */

public class LoadProperties extends ConfigrationHandler {

  private static java.util.Properties prop;

  /**
   * This constructor method loads the file properties into the Properties object.
   * @param String The name of the file including the complete path.
   * @throws java.io.IOException if the file is not accessible.
   */
  public static void loadInfo(String fileName) throws java.io.IOException {
    // load common configration
    loadBaseConfigFile(fileName);

    System.out.println("[LoadProperties].[loadInfo] File Path is --->" + fileName);
    //Checking the file exist
    if (!new File(fileName).exists())
      throw new IOException("[LoadProperties].[loadInfo] Configuration file not exist");

    //Read the file information
    java.io.FileInputStream fis = new java.io.FileInputStream(fileName);
    prop = new java.util.Properties();
    try{
    prop.load(fis);
    //Close the file
    if (fis != null){
        fis.close();
        fis = null;
    }
    loadConfigurationFile();
//    LogAttributes.execute();
    }catch (Exception ex) {
        ex.printStackTrace();
    }finally{
      try{
        if (fis != null) {
          fis.close();
          fis = null;
        }
      }catch(Exception ex){
          ex.printStackTrace();
      }
    }
  } //end method

  /**
   * Method for validating the mandatory/required values
   * @throws IOException
   */
  private static void loadConfigurationFile() throws IOException {

//----Varibles for the Log File
//    if (getValue("CommonConfigurationFilePath") != null) {
//        Constants.COMMON_CONFIGURATION_FILE = getValue("CommonConfigurationFilePath");
//    }else{
//        throw new IOException("CommonConfigurationFilePath value is missing ");
//    }
//    if (getValue("LogPath") != null) {
//      Constants.LOG_FILE_PATH = getValue("LogPath");
//      if (!new File(Constants.LOG_FILE_PATH).exists())
//        new File(Constants.LOG_FILE_PATH).mkdirs();
//    }else
//      throw new IOException("LogPath value is missing ");
//
//    if (getValue("LogFile") != null)
//      Constants.LOG_FILE_NAME = getValue("LogFile");
//    else
//      throw new IOException("LogFile value is missing ");
//
//    if (getValue("LogSize") != null)
//      Constants.LOG_FILE_SIZE = Integer.parseInt(getValue("LogSize"));
//    else
//      throw new IOException("LogSize value is missing ");
//
//    if (getValue("LogLevel") != null)
//      Constants.LOG_DEBUG_LEVEL = Integer.parseInt(getValue("LogLevel"));
//    else
//      throw new IOException("LogLevel value is missing ");
//
//    if (getValue("NoOfLogFiles") != null)
//      Constants.LOG_FILE_NO = Integer.parseInt(getValue("NoOfLogFiles"));
//    else
//      throw new IOException("NoOfLogFiles value is missing ");
//
//    if (getValue("Monitor_Service_Sleep_Time") != null) {
//          Constants.MONITOR_SERVICE_SLEEP_TIME = Long.parseLong(getValue("Monitor_Service_Sleep_Time").trim());
//    } else {
//      throw new IOException("[LoadProperties].[loadConfigurationFile] DefaultSleepTime value is missing ");
//    }
//
//    if (getValue("Port_Number") != null) {
//      try {
//        Constants.PORT_NUMBER = Integer.parseInt(getValue("Port_Number"));
//      }catch (NumberFormatException ex) {
//        throw new IOException("Invalid Port number--->" + ex);
//      }
//    }else {
//      throw new IOException("PORT Number value is missing");
//    }
//    loadCommonConfigurationInfo(Constants.COMMON_CONFIGURATION_FILE);


    if (getValue("DefaultSleepTime") != null){
      Constants.DEFAULT_SLEEP_TIME = Long.parseLong(getValue("DefaultSleepTime"));
    } else {
      throw new IOException("[LoadProperties].[loadConfigurationFile] DefaultSleepTime value is missing ");
    }

    if(getValue("COUPON_GEN_SLEEP_TIME") != null){
      Constants.COUPON_GEN_SLEEP_TIME = Long.parseLong(getValue("COUPON_GEN_SLEEP_TIME").trim());
    } else {
      throw new IOException("LoadProperties].[loadConfigurationFile] COUPON_GEN_SLEEP_TIME value is missing ");
    }


  } //end validate method


}
