package com.i2c.schedulerframework.util;

import java.io.*;
import java.util.*;
import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;
import java.io.*;
import com.i2c.schedulerframework.model.InstanceDbConfigBean;
import com.i2c.schedulerframework.model.DbConfigBean;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2006
 * Company:      i2c Inc.
 * @author habbasi
 * @version 1.0
 */

/**
 * This class is a utility class used to read and load the property file have
 * keys and values with a typical format i.e., key=value
 */

public abstract class ConfigrationHandler {

  protected static java.util.Properties prop;
  public static Hashtable instanceConnectionsTable = new Hashtable();

  /**
   * This constructor method loads the file properties into the Properties object.
   * @param String The name of the file including the complete path.
   * @throws java.io.IOException if the file is not accessible.
   */
  public static void loadBaseConfigFile(String fileName) throws java.io.IOException {

    System.out.println("[ConfigrationHandler].[loadBaseConfigFile] Config File Path is --->" + fileName);
    //Checking the file exist
    if (!new File(fileName).exists())
      throw new IOException("[ConfigrationHandler].[loadBaseConfigFile] Configuration file not Exist on the given Path");

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
      }catch(Exception ex){
      ex.printStackTrace();
    }
    }
  } //end method

  /**
   * Method for validating the mandatory/required values
   * @throws IOException
   */
  private static void validateLoadValues() throws IOException {

    //Varibles for the Log File
    if (getValue("LogPath") != null) {
      BaseConstants.LOG_FILE_PATH = getValue("LogPath");
      if (!new File(BaseConstants.LOG_FILE_PATH).exists())
        new File(BaseConstants.LOG_FILE_PATH).mkdirs();
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] LogPath value is missing ");
    }
    if (getValue("LogFile") != null) {
      BaseConstants.LOG_FILE_NAME = getValue("LogFile");
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] LogFile value is missing ");
    }
    if (getValue("LogSize") != null) {
      BaseConstants.LOG_FILE_SIZE = Integer.parseInt(getValue("LogSize"));
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] LogSize value is missing ");
    }
    if (getValue("LogLevel") != null) {
      BaseConstants.LOG_DEBUG_LEVEL = Integer.parseInt(getValue("LogLevel"));
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] LogLevel value is missing ");
    }
    if (getValue("NoOfLogFiles") != null) {
      BaseConstants.LOG_FILE_NO = Integer.parseInt(getValue("NoOfLogFiles"));
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] NoOfLogFiles value is missing ");
    }

    // DB Driver
    if (getValue("DbDriver") != null) {
      BaseConstants.DB_DRIVER_NAME = getValue("DbDriver");
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] DbDriver value is missing ");
    }

    // Email informtion
    if (getValue("Mail_SMTP") != null) {
      BaseConstants.MAIL_SMTP = getValue("Mail_SMTP");
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] Mail_SMTP value is missing ");
    }

    if (getValue("Mail_Report_From") != null) {
      BaseConstants.MAIL_REPORT_FROM = getValue("Mail_Report_From");
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] Mail_Report_From value is missing ");
    }

    if (getValue("Mail_Report_Admin") != null) {
      BaseConstants.MAIL_REPORT_ADMIN = getValue("Mail_Report_Admin");
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] Mail_Report_Admin value is missing ");
    }

    if (getValue("Email_Retry_Max_Counter") != null) {
      BaseConstants.MAX_EMAIL_RETRY = Integer.parseInt(getValue(
          "Email_Retry_Max_Counter"));
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] Email_Retry_Max_Counter value is missing ");
    }

    // Socket Service Port
    if (getValue("Port_Number") != null) {
      try {
        BaseConstants.PORT_NUMBER = Integer.parseInt(getValue("Port_Number"));
      }
      catch (NumberFormatException ex) {
        throw new IOException(
            "[ConfigrationHandler].[validateLoadValues] Invalid Port number--->" + ex);
      }
    }
    else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] PORT Number value is missing");
    }


    // Monitor variables
    if (getValue("Monitor_Service_Sleep_Time") != null) {
      BaseConstants.MONITOR_SERVICE_SLEEP_TIME = Long.parseLong(getValue(
          "Monitor_Service_Sleep_Time").trim());
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] Monitor_Service_Sleep_Time value is missing ");
    }

    if (getValue("Default_Alert_Time") != null) {
      BaseConstants.DEFAULT_ALERT_TIME = Long.parseLong(getValue(
          "Default_Alert_Time").trim());
    } else {
      throw new IOException(
          "[ConfigrationHandler].[validateLoadValues] Default_Alert_Time value is missing ");
    }

    if (getValue("Scheduler_Type") != null) {
      BaseConstants.SCHEDULER_TYPE = getValue("Scheduler_Type");
    }
    else {
      throw new IOException("Scheduler_Type value is missing");
    }

    if (getValue("Scheduler_IP") != null) {
      BaseConstants.SCHEDULER_IP = getValue("Scheduler_IP");
    }
    else if (BaseConstants.SCHEDULER_TYPE.equalsIgnoreCase("S")) {
      throw new IOException("Scheduler_IP value is missing");
    }
    else {
      System.err.println("Scheduler_IP value is missing");
    }

    if (getValue("Scheduler_Port") != null) {
      try {
        BaseConstants.SCHEDULER_PORT = Integer.parseInt(getValue("Scheduler_Port"));
      }
      catch (NumberFormatException ex) {
        throw new IOException("Invalid Scheduler_Port--->" + ex);
      }
    }
    else if (BaseConstants.SCHEDULER_TYPE.equalsIgnoreCase("S")) {
      throw new IOException("Scheduler_Port value is missing");
    }
    else {
      System.err.println("Scheduler_Port value is missing");
    }




//    if (getValue("ModuleConfigurationFilePath") != null) {
//      BaseConstants.MODULE_CONFIGURATION_FILE = getValue("ModuleConfigurationFilePath");
//    } else {
//      throw new IOException(
//          "[ConfigrationHandler].[validateLoadValues] ModuleConfigurationFilePath value is missing ");
//    }
//
//    if (getValue("CommonConfigurationFilePath") != null) {
//      BaseConstants.COMMON_CONFIGURATION_FILE = getValue("CommonConfigurationFilePath");
//    } else {
//      throw new IOException(
//          "[ConfigrationHandler].[validateLoadValues] CommonConfigurationFilePath value is missing ");
//    }
//
//
//    if (getValue("BLClassName") != null) {
//      BaseConstants.BL_DERIVED_CLASS_NAME = getValue("BLClassName");
//    } else {
//      throw new IOException(
//          "[ConfigrationHandler].[validateLoadValues] BLClassName value is missing ");
//    }

    //Load <Cards instance> DB Connection information from config.ini file
    loadCardInstancesConnectionInfo();
    //Load the Database instance level Connectin information from Table instances_table
    loadInstanceConnectionInfo(BaseConstants.CARD_INSTANCE_DB_CONN);
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






  /**
   * Method for loading the instance db connection information
   * from DataBase Table ext_interfaces and load the information in list
   * @param conn DbConfigBean
   */
  private static void loadInstanceConnectionInfo(DbConfigBean cardInstanceDBConn) {
    int startCounter = 1;
    DbConfigBean connObj = null;
    try {
      System.out.println(
          "[ConfigrationHandler].[loadInstanceConnectionInfo] Method for loading instance connection information -->");

      ArrayList connAList = DatabaseConnectionUtil.getExtInterfaceConfig(cardInstanceDBConn);
      InstanceDbConfigBean  obj = null;
      for (int i = 0; i < connAList.size(); i++){
        obj = (InstanceDbConfigBean) connAList.get(i);
        if (obj.getIsActive() != null && obj.getIsActive().trim().equalsIgnoreCase("Y")){
          connObj = new DbConfigBean();
          //Setting the DB Instance Connection Name
          connObj.setConnectionName(obj.getInterfaceId());
          //Setting the DB Instance Connection String
          connObj.setConnectionString(obj.getConnString());
          //Setting the DB Instance Connection User
          connObj.setUserID(obj.getUserId());
          //Setting the DB Instance Connection Password
          connObj.setPasswod(obj.getPassword());

          System.out.println("[ConfigrationHandler].[loadInstanceConnectionInfo] Instance Information Loaded Name -->" +
                             connObj.getConnectionName() + "<--String-->" +
                             connObj.getConnectionString() + "<--user-->" +
                             connObj.getUserID() + "<----Password--->" +
                             connObj.getPasswod());

          //Add the information in the list
          instanceConnectionsTable.put(connObj.getConnectionName().trim(), connObj);
        }
      }

      System.out.println("[ConfigrationHandler].[loadInstanceConnectionInfo] Instance Connection Vector size is --->" +
                         instanceConnectionsTable.size());
      //Checking the DB Instance information
      if (instanceConnectionsTable.size() < 1){
        throw new IOException("[ConfigrationHandler].[loadInstanceConnectionInfo] Instance DataBase Information is missing ");
      }
    }
    catch (Exception e) {
      System.out.println(
          "[ConfigrationHandler].[loadInstanceConnectionInfo] Exception in loading Instances Connection Information -->" +
          e.getMessage());
      e.printStackTrace();
    } //end catch
  } //end method




  /**
   * Load Card Instances DB Connection Information
   */
  private static void loadCardInstancesConnectionInfo() {

    DbConfigBean connObj = null;
    try {
      System.out.println(
          "[ConfigrationHandler].[loadCardInstancesConnectionInfo] Method for loading Card Instance connection information");
        //Getting the Isntance Db Connection Information
        if (getValue("DbCardInstanceName") != null) {
          connObj = new DbConfigBean();
          //Setting the DB Instance Connection Name
          connObj.setConnectionName(getValue("DbCardInstanceName"));
          //Setting the DB Instance Connection String
          connObj.setConnectionString(getValue("DbConnectionCardInstance"));
          //Setting the DB Instance Connection User
          connObj.setUserID(getValue("DbCardUserName"));
          //Setting the DB Instance Connection Password
          connObj.setPasswod(getValue("DbCardUserPassword"));

          System.out.println("[ConfigrationHandler].[loadCardInstancesConnectionInfo]" +
                             "<<----  Loaded Card Instances DB Information ---->>" +
                             "<--Connection Name-->" + connObj.getConnectionName() +
                             "<--Conn String-->" + connObj.getConnectionString() +
                             "<--user-->" + connObj.getUserID() +
                             "<----Password--->" + connObj.getPasswod());

          BaseConstants.CARD_INSTANCE_DB_CONN = connObj;

        }
    }
    catch (Exception ex) {
      System.out.println(
          "[ConfigrationHandler].[loadCardInstancesConnectionInfo] Exception in loading <Card Instance> Connection information -->" +
          ex.getMessage());
      ex.printStackTrace();
    } //end catch
  } //end method





  /**
     * Load Properties from xxxxxx_config.ini
     * @param fileName String
     */
//    public abstract void loadModuleConfigurationInfo(String fileName);

//    private static void loadModuleConfigurationInfo(String fileName){
//        java.io.FileInputStream fis = null;
//        try {
//            fis = new java.io.FileInputStream(fileName);
//            prop = new java.util.Properties();
//            prop.load(fis);
//            //Close the file
//            if (fis != null) {
//                fis.close();
//                fis = null;
//            }
//
//            if (getValue("DbDriver") != null)
//                BaseConstants.DB_DRIVER_NAME = getValue("DbDriver");
//            else
//                throw new IOException("DbDriver value is missing ");
//
//            if (getValue("Mail_SMTP") != null)
//                BaseConstants.MAIL_SMTP = getValue("Mail_SMTP");
//            else
//                throw new IOException("Mail_SMTP value is missing ");
//
//            if (getValue("Mail_Report_From") != null)
//                BaseConstants.MAIL_REPORT_FROM = getValue("Mail_Report_From");
//            else
//                throw new IOException("Mail_Report_From value is missing ");
//
//            if (getValue("Mail_Report_Admin") != null)
//                BaseConstants.MAIL_REPORT_ADMIN = getValue("Mail_Report_Admin");
//            else
//                throw new IOException("Mail_Report_Admin value is missing ");
//
////            loadInstanceConnectionInfo();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                if (fis != null) {
//                    fis.close();
//                    fis = null;
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//
//  }





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
