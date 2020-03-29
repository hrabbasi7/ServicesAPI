package com.i2c.payeeinfoservice.util;

import java.io.*;
import java.util.*;
import javax.crypto.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.*;
import java.security.*;
import java.io.*;
import com.i2c.payeeinfoservice.mapper.PayeeMapper;
import com.i2c.utils.logging.*;
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
    prop.load(fis);
    //Close the file
    if (fis != null)
      fis.close();
      //Validate the load values
    validateLoadValues();
    //Loading the log parameters
    try {
      LogAttributes.execute();
    }
    catch (Exception e) {}
  } //end method

  /**
   * Method for validating the mandatory/required values
   * @throws IOException
   */
  private static void validateLoadValues() throws IOException {

    //Varibles for the Log File
    if (getValue("LogPath") != null) {
      Constants.LOG_FILE_PATH = getValue("LogPath");
      if (!new File(Constants.LOG_FILE_PATH).exists())
        new File(Constants.LOG_FILE_PATH).mkdirs();
    }
    else
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

//    if (getValue("ACHFTPSCertFile") != null) {
//        Constants.ACH_FTPS_CERT_FILE = getValue("ACHFTPSCertFile");
//    }
//    else
//        throw new IOException("ACH FTPS Certificate file path value is missing ");

    if (getValue("DbDriver") != null)
      Constants.DB_DRIVER_NAME = getValue("DbDriver");
    else
      throw new IOException("DbDriver value is missing ");

  if (getValue("PayeeInfoFilePath") != null)
      Constants.PAYEE_INFO_FILE_PATH = getValue("PayeeInfoFilePath");
    else
      throw new IOException("PayeeInfoFilePath value is missing ");

  if (getValue("PayeeInfoFileName") != null)
     Constants.PAYEE_INFO_FILE_NAME = getValue("PayeeInfoFileName");
   else
     throw new IOException("PayeeInfoFileName value is missing ");


    //Load the Database instance information
    loadInstanceConnectionInfo();
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
          "Method for loading instance connection information -->");
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
          connObj.setServiceUserID(getValue("DbServiceUser" + startCounter));
          //Setting the ACH Output Path
          connObj.setAchOutputPath(getValue("DbACHPath" + startCounter));

          //Setting the Fastcash database settings
          connObj.setFcConnectionString(getValue("DbFCConnectionString" +
                                                 startCounter));
          //Setting the Fastcash database user
          connObj.setFcUserID(getValue("DbFCUserName" + startCounter));
          //Setting the Fastcash database user password
          connObj.setFcPasswod(getValue("DbFCUserPassword" + startCounter));

          System.out.println(" Instance Information Loaded Name -->" +
                             connObj.getConnectionName() + "<--String-->" +
                             connObj.getConnectionString() + "<--user-->" +
                             connObj.getUserID() + "<----Password--->" +
                             connObj.getPasswod() +
                             "<---- Service User ID --->" +
                             connObj.getServiceUserID() +
                             ///FastCash database
                             "<--FC Connection String-->" +
                             connObj.getFcConnectionString()
                             + "<--Fc DB user-->" + connObj.getFcUserID()
                             + "<----Fc DB Password--->" + connObj.getFcPasswod()

                             + "<---- ACH Output Path --->" +
                             connObj.getAchOutputPath());

          //Add the information in the list
          instanceConnectionsTable.put(connObj.getConnectionName(), connObj);
        }
        else { //If no remianing connection instance
          break;
        } //end else
        startCounter++;
      } //end while
      System.out.println(" Instance Connection Vector size is --->" +
                         instanceConnectionsTable.size() +
                         "<--- Start Counter -->" + startCounter);
      //Checking the DB Instance information
      if (instanceConnectionsTable.size() < 1)
        throw new IOException("DB Instance information value is missing ");
    }
    catch (Exception e) {
      System.out.println(
          "Exception in loading instance connection information -->" + e);
    } //end catch
  } //end method

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
