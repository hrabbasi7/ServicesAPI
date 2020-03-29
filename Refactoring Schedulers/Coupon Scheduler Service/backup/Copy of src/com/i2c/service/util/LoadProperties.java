package com.i2c.service.util;

import java.io.*;
import java.util.*;
import javax.crypto.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.*;
import java.security.*;
import java.io.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      i2c Inc.
 * @author Burhan
 * @version 1.0
 */

/**
 * This class is a utility class used to read and load the property file have
 * keys and values with a typical format i.e., key=value
 */

public class LoadProperties {

  private static java.util.Properties prop;
  public static Hashtable instanceConnectionsTable = new Hashtable();

  /**
   * This constructor method loads the file properties into the Properties object.
   * @param String The name of the file including the complete path.
   * @throws java.io.IOException if the file is not accessible.
   */
  public static void loadInfo(String fileName) throws java.io.IOException {

    System.out.println("File Path is --->" + fileName);
    //Checking the file exist
    if (!new File(fileName).exists())
      throw new IOException("Configuration file not exist");

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
    LogAttributes.execute();
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

    //Varibles for the Log File
    if (getValue("CommonConfigurationFilePath") != null) {
        Constants.COMMON_CONFIGURATION_FILE = getValue("CommonConfigurationFilePath");
    }else{
        throw new IOException("CommonConfigurationFilePath value is missing ");
    }
    if (getValue("LogPath") != null) {
      Constants.LOG_FILE_PATH = getValue("LogPath");
      if (!new File(Constants.LOG_FILE_PATH).exists())
        new File(Constants.LOG_FILE_PATH).mkdirs();
    }else
      throw new IOException("LogPath value is missing ");

    if (getValue("LogFile") != null)
      Constants.LOG_FILE_NAME = getValue("LogFile");
    else
      throw new IOException("LogFile value is missing ");

    if (getValue("LogSize") != null)
      Constants.LOG_FILE_SIZE = Integer.parseInt(getValue("LogSize"));
    else
      throw new IOException("LogSize value is missing ");

    if (getValue("LogLevel") != null)
      Constants.LOG_DEBUG_LEVEL = Integer.parseInt(getValue("LogLevel"));
    else
      throw new IOException("LogLevel value is missing ");

    if (getValue("NoOfLogFiles") != null)
      Constants.LOG_FILE_NO = Integer.parseInt(getValue("NoOfLogFiles"));
    else
      throw new IOException("NoOfLogFiles value is missing ");

    if (getValue("DefaultSleepTime") != null)
      Constants.DEFAULT_SLEEP_TIME = Long.parseLong(getValue("DefaultSleepTime"));
    else
      throw new IOException("DefaultSleepTime value is missing ");

    if (getValue("Monitor_Service_Sleep_Time") != null) {
          Constants.MONITOR_SERVICE_SLEEP_TIME = Long.parseLong(getValue(
              "Monitor_Service_Sleep_Time").trim());
    }

    if(getValue("COUPON_GEN_SLEEP_TIME") != null){
      Constants.COUPON_GEN_SLEEP_TIME = Long.parseLong(getValue("COUPON_GEN_SLEEP_TIME").trim());
    }
    else
      throw new IOException("COUPON_GEN_SLEEP_TIME value is missing ");

    if (getValue("Port_Number") != null) {
      try {
        Constants.PORT_NUMBER = Integer.parseInt(getValue("Port_Number"));
      }catch (NumberFormatException ex) {
        throw new IOException("Invalid Port number--->" + ex);
      }
    }else {
      throw new IOException("PORT Number value is missing");
    }

    loadCommonConfigurationInfo(Constants.COMMON_CONFIGURATION_FILE);

  } //end validate method

  /**
   * Method for loading the instance db connection information
   * from the web.xml file and load the information in list
   */
  private static void loadInstanceConnectionInfo() {
      int startCounter = 1;
      DbConnectionInfoObj connObj = null;
      try {
          System.out.println(
                  "Method for loading instance connection information from -->");

          while (true) {
              //Getting the Isntance Db Connection Information
              if (getValue("DbInstanceName" + startCounter) != null) {
                  connObj = new DbConnectionInfoObj();
                  //Setting the DB Instance Connection Name
                  connObj.setConnectionName(getValue("DbInstanceName" +
                          startCounter));
                  //Setting the DB Instance Connection String
                  connObj.setConnectionString(getValue("DbConnectionString" +
                          startCounter));
                  //Setting the DB Instance Connection User
                  connObj.setUserID(getValue("DbUserName" + startCounter));
                  //Setting the DB Instance Connection Password
                  connObj.setPasswod(getValue("DbUserPassword" + startCounter));
                  //Setting the DB Instance Service User
                  connObj.setServiceUserID(getValue("DbServiceUser" +
                          startCounter));
                  //Setting the ACH Output Path
                  connObj.setAchOutputPath(getValue("DbACHPath" +
                          startCounter));

                  //Setting the Fastcash database settings
                  connObj.setFcConnectionString(getValue(
                          "DbFCConnectionString" +
                          startCounter));
                  //Setting the Fastcash database user
                  connObj.setFcUserID(getValue("DbFCUserName" + startCounter));
                  //Setting the Fastcash database user password
                  connObj.setFcPasswod(getValue("DbFCUserPassword" +
                                                startCounter));

                  System.out.println(" Instance Information Loaded Name -->" +
                                     connObj.getConnectionName() +
                                     "<--String-->" +
                                     connObj.getConnectionString() +
                                     "<--user-->" +
                                     connObj.getUserID() +
                                     "<----Password--->" +
                                     connObj.getPasswod() +
                                     "<---- Service User ID --->" +
                                     connObj.getServiceUserID() +
                                     ///FastCash database
                                     "<--FC Connection String-->" +
                                     connObj.getFcConnectionString()
                                     + "<--Fc DB user-->" +
                                     connObj.getFcUserID()
                                     + "<----Fc DB Password--->" +
                                     connObj.getFcPasswod()

                                     + "<---- ACH Output Path --->" +
                                     connObj.getAchOutputPath());

                  //Add the information in the list
                  instanceConnectionsTable.put(connObj.getConnectionName(),
                                               connObj);
              } else { //If no remianing connection instance
                  break;
              } //end else
              startCounter++;
          } //end while
          System.out.println(" Instance Connection Vector size is --->" +
                             instanceConnectionsTable.size() +
                             "<--- Start Counter -->" + startCounter);
          //Checking the DB Instance information
          if (instanceConnectionsTable.size() < 1)
              throw new IOException(
                      "DB Instance information value is missing ");
      } catch (Exception e) {
          System.out.println(
                  "Exception in loading instance connection information -->" +
                  e);
      } //end catch
  } //end method

  private static void loadCommonConfigurationInfo(String fileName){
      java.io.FileInputStream fis = null;
      try {
          fis = new java.io.FileInputStream(fileName);
          prop = new java.util.Properties();
          prop.load(fis);
          //Close the file
          if (fis != null) {
              fis.close();
              fis = null;
          }

          if (getValue("DbDriver") != null)
              Constants.DB_DRIVER_NAME = getValue("DbDriver");
          else
              throw new IOException("DbDriver value is missing ");

          if (getValue("Mail_SMTP") != null)
              Constants.MAIL_SMTP = getValue("Mail_SMTP");
          else
              throw new IOException("Mail_SMTP value is missing ");

          if (getValue("Mail_Report_From") != null)
              Constants.MAIL_REPORT_FROM = getValue("Mail_Report_From");
          else
              throw new IOException("Mail_Report_From value is missing ");

          if (getValue("Mail_Report_Admin") != null)
              Constants.MAIL_REPORT_ADMIN = getValue("Mail_Report_Admin");
          else
              throw new IOException("Mail_Report_Admin value is missing ");

          loadInstanceConnectionInfo();
      } catch (Exception ex) {
          ex.printStackTrace();
      } finally {
          try {
              if (fis != null) {
                  fis.close();
                  fis = null;
              }
          } catch (Exception ex) {
              ex.printStackTrace();
          }
      }

  }

  /**
   * This method returns the value against the supplied key.
   * @param String Key name
   * @return String Key vlaue
   */
  public static String getValue(String keyName) {
      if (prop == null || prop.getProperty(keyName) == null)
          return null;

      return prop.getProperty(keyName).trim();
  } //end method
}
