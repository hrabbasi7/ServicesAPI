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
import com.i2c.billpayment.vo.InterfaceConfigObj;

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
    if (fis != null)
      fis.close();
      //Validate the load values
    validateLoadValues();
    //Loading the log parameters
//    LogAttributes.execute();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    finally{
      try{
        if (fis != null) {
          fis.close();
          fis = null;
        }
      }catch(Exception ex){}
    }
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

    if (getValue("DbDriver") != null)
      Constants.DB_DRIVER_NAME = getValue("DbDriver");
    else
      throw new IOException("DbDriver value is missing ");

    if (getValue("ActiveThreadLimit") != null)
      Constants.MAX_ACTIVE_THREADS = Long.parseLong(getValue(
          "ActiveThreadLimit"));
    else
      throw new IOException("ActiveThreadLimit value is missing ");

    if (getValue("Mail_SMTP") != null)
      Constants.MAIL_SMTP = getValue("Mail_SMTP");
    else
      throw new IOException("Mail_SMTP value is missing ");

    if (getValue("Mail_Report_From") != null)
      Constants.MAIL_REPORT_FROM = getValue("Mail_Report_From");
    else
      throw new IOException("Mail_Report_From value is missing ");

    if (getValue("Email_Retry_Max_Counter") != null)
      Constants.MAX_EMAIL_RETRY = Integer.parseInt(getValue(
          "Email_Retry_Max_Counter"));
    else
      throw new IOException("Email_Retry_Max_Counter value is missing ");

    if (getValue("Mail_Report_Admin") != null)
      Constants.MAIL_REPORT_ADMIN = getValue("Mail_Report_Admin");
    else
      throw new IOException("Mail_Report_Admin value is missing ");

    if (getValue("Port_Number") != null) {
      try {
        Constants.PORT_NUMBER = Integer.parseInt(getValue("Port_Number"));
      }
      catch (NumberFormatException ex) {
        throw new IOException("Invalid Port number--->" + ex);
      }
    }
    else {
      throw new IOException("PORT Number value is missing");
    }

    if (getValue("Verfiy__Db_Counter") != null) {
      Constants.VERFIY_DB_COUNTER = Integer.parseInt(getValue(
          "Verfiy__Db_Counter"));
    }

    if (getValue("Monitor_Service_Sleep_Time") != null) {
      Constants.MONITOR_SERVICE_SLEEP_TIME = Long.parseLong(getValue(
          "Monitor_Service_Sleep_Time").trim());
    }else
      throw new IOException("Monitor_Service_Sleep_Time value is missing ");

    if (getValue("Default_Alert_Time") != null) {
      Constants.DEFAULT_ALERT_TIME = Long.parseLong(getValue(
          "Default_Alert_Time").trim());
    }else
      throw new IOException("Default_Alert_Time value is missing ");

//    if (getValue("DefaultSleepTime") != null)
//      Constants.DEFAULT_SLEEP_TIME = Long.parseLong(getValue("DefaultSleepTime"));
//    else
//      throw new IOException("DefaultSleepTime value is missing ");
    /***************Payment Sleep Time *********************/
//    if (getValue("PaymentSleepTime") != null) {
//      Constants.PAYMENT_SLEEP_TIME = Long.parseLong(getValue(
//          "PaymentSleepTime").trim());
//      System.out.println("Paymentsleeptime " + Constants.PAYMENT_SLEEP_TIME);
//    }else
//      throw new IOException("PaymentSleepTime value is missing ");
    /***************Response Sleep Time *********************/
//    if (getValue("ResponseSleepTime") != null) {
//      Constants.RESPONSE_SLEEP_TIME = Long.parseLong(getValue(
//          "ResponseSleepTime").trim());
//      System.out.println("Responsesleeptime "+ Constants.RESPONSE_SLEEP_TIME);
//    }else
//      throw new IOException("ResponseSleepTime value is missing ");
    /***************Adjustment Sleep Time *********************/
//    if (getValue("AdjustmentSleepTime") != null) {
//      Constants.ADJUSTMENT_SLEEP_TIME = Long.parseLong(getValue(
//          "AdjustmentSleepTime").trim());
//      System.out.println("AdjustmentSleepTime "+ Constants.ADJUSTMENT_SLEEP_TIME);
//    }else
//      throw new IOException("AdjustmentSleepTime value is missing ");
    /***************Payee Sleep Time *********************/
    if (getValue("PayeeSleepTime") != null) {
          Constants.PAYEE_SLEEP_TIME = Long.parseLong(getValue(
              "PayeeSleepTime").trim());
          System.out.println("PayeeSleeptime " + Constants.PAYEE_SLEEP_TIME);
        }else
      throw new IOException("PayeeSleepTime value is missing ");
    /***************Default Sleep Time *********************/
    if (getValue("DefaultWaitTime") != null) {
      Constants.DEFAULT_WAIT_TIME = Long.parseLong(getValue(
          "DefaultWaitTime").trim()) * 60 * 1000;
      System.out.println("DefaultWaitTime in Minutes: " + Constants.DEFAULT_WAIT_TIME);
    }else
      throw new IOException("DefaultWaitTime value is missing in configration");
    /******* No of Days to process old Response and Adjustment files *******/
//    if (getValue("NoOfDaysToProcess") != null) {
//      Constants.RESADJ_NO_OF_DAYS = getValue("NoOfDaysToProcess").trim();
//      System.out.println("NoOfDaysToProcess in Days: " + Constants.RESADJ_NO_OF_DAYS);
//    }else
//      throw new IOException("NoOfDaysToProcess value is missing in configration");

    if (getValue("Payee_File_Output") != null) {
          Constants.PAYEE_OUTPUT_PATH = getValue(
              "Payee_File_Output").trim();
    }

    if (getValue("Payment_File_Output") != null) {
      Constants.PAYMENT_OUTPUT_PATH = getValue(
          "Payment_File_Output").trim();
    }

    if (getValue("Response_File_Output") != null) {
      Constants.RESPONSE_OUTPUT_PATH = getValue(
          "Response_File_Output").trim();
    }

    if (getValue("Adjustment_File_Output") != null) {
      Constants.ADJUSTMENT_OUTPUT_PATH = getValue(
          "Adjustment_File_Output").trim();
    }

    if (getValue("VerifyPublicKeyPath") != null) {
      Constants.VERIFY_PUBLIC_KEY_PATH = getValue(
          "VerifyPublicKeyPath").trim();
    }

    if (getValue("EncryptionPublicKeyPath") != null) {
      Constants.ENCRYPT_PUBLIC_KEY_PATH = getValue(
      "EncryptionPublicKeyPath").trim();
    }

    if (getValue("ResponseInFilePrefix") != null) {
     Constants.RESPONSE_INFILE_PREFIX = getValue(
     "ResponseInFilePrefix").trim();
   }

   if (getValue("AdjustmentInFilePrefix") != null) {
     Constants.ADJUSTMENT_INFILE_PREFIX = getValue(
     "AdjustmentInFilePrefix").trim();
   }

   if (getValue("SecretKeyPath") != null) {
     Constants.SECRET_KEY_PATH = getValue(
      "SecretKeyPath").trim();
   }

//    if (getValue("PassPhrase") != null) {
//      Constants.PASS_PHRASE = getValue(
//          "PassPhrase").trim();
//    }

   if (getValue("Scheduler_Type") != null) {
       Constants.SCHEDULER_TYPE = getValue("Scheduler_Type");
   } else {
       throw new IOException("Scheduler_Type value is missing");
   }

   if (getValue("Scheduler_IP") != null) {
       Constants.SCHEDULER_IP = getValue("Scheduler_IP");
   } else if (Constants.SCHEDULER_TYPE.equalsIgnoreCase("S")) {
       throw new IOException("Scheduler_IP value is missing");
   } else {
       System.err.println("Scheduler_IP value is missing");
   }

   if (getValue("Scheduler_Port") != null) {
       try {
           Constants.SCHEDULER_PORT = Integer.parseInt(getValue("Scheduler_Port"));
       } catch (NumberFormatException ex) {
           throw new IOException("Invalid Scheduler_Port--->" + ex);
       }
   } else if (Constants.SCHEDULER_TYPE.equalsIgnoreCase("S")) {
       throw new IOException("Scheduler_Port value is missing");
   } else {
       System.err.println("Scheduler_Port value is missing");
   }


    //Cards instance DB Connection
    loadCardInstancesConnectionInfo();
    //Load the Database instance information
    loadInstanceConnectionInfo(Constants.CARD_INSTANCE_DB_CONN);
  } //end validate method

  /**
   * Method for loading the instance db connection information
   * from the web.xml file and load the information in list
   */
//  private static void loadInstanceConnectionInfo() {
//    int startCounter = 1;
//    DbConnectionInfoObj connObj = null;
//    try {
//      System.out.println(
//          "Method for loading instance connection information -->");
//      while (true) {
//        //Getting the Isntance Db Connection Information
//        if (getValue("DbInstanceName" + startCounter) != null) {
//          connObj = new DbConnectionInfoObj();
//          //Setting the DB Instance Connection Name
//          connObj.setConnectionName(getValue("DbInstanceName" +
//                                             startCounter));
//          //Setting the DB Instance Connection String
//          connObj.setConnectionString(getValue("DbConnectionString" +
//                                               startCounter));
//          //Setting the DB Instance Connection User
//          connObj.setUserID(getValue("DbUserName" + startCounter));
//          //Setting the DB Instance Connection Password
//          connObj.setPasswod(getValue("DbUserPassword" + startCounter));
//          //Setting the DB Instance Service User
//          connObj.setServiceUserID(getValue("DbServiceUser" + startCounter));
//          //Setting the ACH Output Path
//          connObj.setAchOutputPath(getValue("DbACHPath" + startCounter));
//
//          //Setting the Fastcash database settings
//          connObj.setFcConnectionString(getValue("DbFCConnectionString" +
//                                                 startCounter));
//          //Setting the Fastcash database user
//          connObj.setFcUserID(getValue("DbFCUserName" + startCounter));
//          //Setting the Fastcash database user password
//          connObj.setFcPasswod(getValue("DbFCUserPassword" + startCounter));
//
//          System.out.println(" Instance Information Loaded Name -->" +
//                             connObj.getConnectionName() + "<--String-->" +
//                             connObj.getConnectionString() + "<--user-->" +
//                             connObj.getUserID() + "<----Password--->" +
//                             connObj.getPasswod() +
//                             "<---- Service User ID --->" +
//                             connObj.getServiceUserID() +
//                             ///FastCash database
//                             "<--FC Connection String-->" +
//                             connObj.getFcConnectionString()
//                             + "<--Fc DB user-->" + connObj.getFcUserID()
//                             + "<----Fc DB Password--->" + connObj.getFcPasswod()
//
//                             + "<---- ACH Output Path --->" +
//                             connObj.getAchOutputPath());
//
//          //Add the information in the list
//          instanceConnectionsTable.put(connObj.getConnectionName(), connObj);
//        }
//        else { //If no remianing connection instance
//          break;
//        } //end else
//        startCounter++;
//      } //end while
//      System.out.println(" Instance Connection Vector size is --->" +
//                         instanceConnectionsTable.size() +
//                         "<--- Start Counter -->" + startCounter);
//      //Checking the DB Instance information
//      if (instanceConnectionsTable.size() < 1)
//        throw new IOException("DB Instance information value is missing ");
//    }
//    catch (Exception e) {
//      System.out.println(
//          "Exception in loading instance connection information -->" + e);
//    } //end catch
//  } //end method
  private static void loadInstanceConnectionInfo(DbConnectionInfoObj conn) {
    int startCounter = 1;
    DbConnectionInfoObj connObj = null;
    try {
      System.out.println(
          "Method for loading instance connection information -->");
//      while (true) {
//        //Getting the Isntance Db Connection Information
//        if (getValue("DbInstanceName" + startCounter) != null) {
//          connObj = new DbConnectionInfoObj();
//          //Setting the DB Instance Connection Name
//          connObj.setConnectionName(getValue("DbInstanceName" +
//                                             startCounter));
//          //Setting the DB Instance Connection String
//          connObj.setConnectionString(getValue("DbConnectionString" +
//                                               startCounter));
//          //Setting the DB Instance Connection User
//          connObj.setUserID(getValue("DbUserName" + startCounter));
//          //Setting the DB Instance Connection Password
//          connObj.setPasswod(getValue("DbUserPassword" + startCounter));
//          //Setting the DB Instance Service User
//          connObj.setServiceUserID(getValue("DbServiceUser" + startCounter));
//          //Setting the ACH Output Path
//          connObj.setAchOutputPath(getValue("DbACHPath" + startCounter));
//
//          //Setting the Fastcash database settings
//          connObj.setFcConnectionString(getValue("DbFCConnectionString" +
//                                                 startCounter));
//          //Setting the Fastcash database user
//          connObj.setFcUserID(getValue("DbFCUserName" + startCounter));
//          //Setting the Fastcash database user password
//          connObj.setFcPasswod(getValue("DbFCUserPassword" + startCounter));
//
//          System.out.println(" Instance Information Loaded Name -->" +
//                             connObj.getConnectionName() + "<--String-->" +
//                             connObj.getConnectionString() + "<--user-->" +
//                             connObj.getUserID() + "<----Password--->" +
//                             connObj.getPasswod() +
//                             "<---- Service User ID --->" +
//                             connObj.getServiceUserID() +
//                             ///FastCash database
//                             "<--FC Connection String-->" +
//                             connObj.getFcConnectionString()
//                             + "<--Fc DB user-->" + connObj.getFcUserID()
//                             + "<----Fc DB Password--->" + connObj.getFcPasswod()
//
//                             + "<---- ACH Output Path --->" +
//                             connObj.getAchOutputPath());
//
//          //Add the information in the list
//          instanceConnectionsTable.put(connObj.getConnectionName(), connObj);
//        }
//        else { //If no remianing connection instance
//          break;
//        } //end else
//        startCounter++;
//      } //end while
      ArrayList connAL = DatabaseHandler.getExtInterfaceConfig(conn);
      InterfaceConfigObj  obj = null;
      for (int i = 0; i < connAL.size(); i++){
        obj = (InterfaceConfigObj) connAL.get(i);
        if (obj.getIsActive() != null && obj.getIsActive().trim().equalsIgnoreCase("Y")){
          connObj = new DbConnectionInfoObj();
          //Setting the DB Instance Connection Name
          connObj.setConnectionName(obj.getInterfaceId());
          //Setting the DB Instance Connection String
          connObj.setConnectionString(obj.getConnString());
          //Setting the DB Instance Connection User
          connObj.setUserID(obj.getUserId());
          //Setting the DB Instance Connection Password
          connObj.setPasswod(obj.getPassword());

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
          instanceConnectionsTable.put(connObj.getConnectionName().trim(), connObj);
        }
      }

          System.out.println(" Instance Connection Vector size is --->" +
                         instanceConnectionsTable.size() +
                         "<--- Start Counter -->" + startCounter);
      //Checking the DB Instance information
      if (instanceConnectionsTable.size() < 1)
        throw new IOException("DB Instance information value is missing ");
    }
    catch (Exception e) {
      System.out.println(
          "Exception in loading instance connection information -->" + e.toString());
    } //end catch
  } //end method


//  private static void loadCardInstancesConnectionInfo() {
//    int startCounter = 1;
//    DbConnectionInfoObj connObj = null;
//    try {
//      System.out.println(
//          "Method for loading Card Instance connection information -->");
//        //Getting the Isntance Db Connection Information
//        if (getValue("DbCardInstanceName") != null) {
//          connObj = new DbConnectionInfoObj();
//          //Setting the DB Instance Connection Name
//          connObj.setConnectionName(getValue("DbCardInstanceName"));
//          //Setting the DB Instance Connection String
//          connObj.setConnectionString(getValue("DbConnectionCardInstance" +
//                                               startCounter));
//          //Setting the DB Instance Connection User
//          connObj.setUserID(getValue("DbCardUserName"));
//          //Setting the DB Instance Connection Password
//          connObj.setPasswod(getValue("DbCardUserPassword"));
//
//          System.out.println("<********** Card Instances Information Loaded Name ********>" +
//                             connObj.getConnectionName() + "<--String-->" +
//                             connObj.getConnectionString() + "<--user-->" +
//                             connObj.getUserID() + "<----Password--->" +
//                             connObj.getPasswod() +
//                             "<---- Service User ID --->" +
//                             connObj.getServiceUserID()
//                             );
//          Constants.CARD_INSTANCE_DB_CONN = connObj;
//
//          //Add the information in the list
////          instanceConnectionsTable.put(connObj.getConnectionName(), connObj);
//        }
//    }
//    catch (Exception e) {
//      System.out.println(
//          "Exception in loading instance connection information -->" + e);
//    } //end catch
//  } //end method

  private static void loadCardInstancesConnectionInfo() {
//    int startCounter = 1;
    DbConnectionInfoObj connObj = null;
    try {
      System.out.println(
          "Method for loading Card Instance connection information -->");
        //Getting the Isntance Db Connection Information
        if (getValue("DbCardInstanceName") != null) {
          connObj = new DbConnectionInfoObj();
          //Setting the DB Instance Connection Name
          connObj.setConnectionName(getValue("DbCardInstanceName"));
          //Setting the DB Instance Connection String
          connObj.setConnectionString(getValue("DbConnectionCardInstance"));
          //Setting the DB Instance Connection User
          connObj.setUserID(getValue("DbCardUserName"));
          //Setting the DB Instance Connection Password
          connObj.setPasswod(getValue("DbCardUserPassword"));

          System.out.println("<********** Card Instances Information Loaded Name ********>" +
                             connObj.getConnectionName() + "<--String-->" +
                             connObj.getConnectionString() + "<--user-->" +
                             connObj.getUserID() + "<----Password--->" +
                             connObj.getPasswod() +
                             "<---- Service User ID --->" +
                             connObj.getServiceUserID()
                             );
          Constants.CARD_INSTANCE_DB_CONN = connObj;

          //Add the information in the list
//          instanceConnectionsTable.put(connObj.getConnectionName(), connObj);
        }
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
