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
