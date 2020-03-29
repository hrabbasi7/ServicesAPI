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

      System.out.println("File Path is --->"+fileName);
      //Checking the file exist
      if (!new File(fileName).exists())
        throw new IOException("Configuration file not exist");

      //Read the file information

      java.io.FileInputStream fis = new java.io.FileInputStream (fileName);
      prop = new java.util.Properties();
      prop.load(fis);

    //Close the file
      if ( fis != null)
        fis.close();
      //Validate the load values
    /*
    String outFilePath=System.getProperty("user.dir")+File.separator+"config-2.ini";
    java.io.FileOutputStream fos=new java.io.FileOutputStream(outFilePath);
    prop.store(fos,"Comments");
    if ( fos != null)
        fos.close();
    */

      validateLoadValues();
      //Loading the log parameters
      try {
        LogAttributes.execute();
      } catch(Exception e){}
    }//end method

    /**
     * Method for validating the mandatory/required values
     * @throws IOException
     */
    private static void validateLoadValues()  throws IOException {


     //Varibles for the Log File
     if (getValue("ServicesLogPath") != null ) {
       Constants.SERVICES_LOG_FILE_PATH = getValue("ServicesLogPath");
       if (!new File(Constants.SERVICES_LOG_FILE_PATH).exists())
         new File(Constants.SERVICES_LOG_FILE_PATH).mkdir();
     } else
       throw new IOException("Services LogPath value is missing ");

     if (getValue("LogPath") != null ) {
       Constants.LOG_FILE_PATH = getValue("LogPath");
       if (!new File(Constants.LOG_FILE_PATH).exists())
         new File(Constants.LOG_FILE_PATH).mkdir();
     } else
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

      if (getValue("Mail_Report_Admin_Message") != null)
        Constants.MAIL_REPORT_ADMIN_MESSAGE = getValue("Mail_Report_Admin_Message");
      else
        throw new IOException("Mail_Report_Admin_Message value is missing ");

      if (getValue("Mail_Report_Admin_Subject") != null)
        Constants.MAIL_REPORT_ADMIN_SUBJECT = getValue("Mail_Report_Admin_Subject");
      else
        throw new IOException("Mail_Report_Admin_Subject value is missing ");

      if (getValue(Constants.PORT_NO) != null)
        Constants.PORT_NO_VALUE = Integer.parseInt(getValue(Constants.PORT_NO));
      else
        throw new IOException("Port Number value is missing ");

      if (getValue(Constants.CLIENT_IP_NAME) != null)
        Constants.CLIENT_IP_VLAUE = getValue(Constants.CLIENT_IP_NAME);
      else
        throw new IOException("Client IP Number value is missing ");

      if (getValue(Constants.MESSAGE_DELIMETER) != null)
        Constants.MESSAGE_DELIMETER_VALUE = getValue(Constants.MESSAGE_DELIMETER);
      else
        throw new IOException("MESSAGE_DELIMETER value is missing ");
      if(getValue("Notify_Response_Code_Values") != null)
      {
        Constants.NOTIFY_RESP_CODES_VALUES = getValue("Notify_Response_Code_Values");
      }
      if(getValue("Mail_Report_Footer") != null)
      {
        Constants.MAIL_REPORT_ADMIN_FOOTER = getValue("Mail_Report_Footer");
      }
      if(getValue("HSM_CONFIG_FILE_PATH") != null){
        Constants.HSM_CONFIG_FILE_PATH = getValue("HSM_CONFIG_FILE_PATH");
      }

      //Load the database instance information
      loadInstanceConnectionInfo();
    }//end validate method



 /**
   * Method for loading the instance db connection information
   * from the web.xml file and load the information in list
   */
   private static void loadInstanceConnectionInfo(){
           int startCounter = 1;
           DbConnectionInfoObj connObj = null;
           try {
                  System.out.println("Method for loading instance connection information -->");
                  while(true) {
                    //Getting the Isntance Db Connection Information
                    if (getValue(Constants.SOURCE_INSTANCE_NAME + startCounter) != null) {
                      connObj = new DbConnectionInfoObj();
                      //Setting the DB Instance Connection Name
                      connObj.setConnectionName(getValue(Constants.SOURCE_INSTANCE_NAME +
                                                         startCounter));
                      //Setting the DB Instance Connection String
                      connObj.setConnectionString(getValue("DbConnectionString" +
                                                           startCounter));
                      //Setting the DB Instance Connection User
                      connObj.setUserID(getValue("DbUserName" + startCounter));
                      //Setting the DB Instance Connection Password
                      connObj.setPasswod(getValue("DbUserPassword" + startCounter));

                      System.out.println(" Instance Information Loaded Name -->" +
                                         connObj.getConnectionName() + "<--String-->" +
                                         connObj.getConnectionString() + "<--user-->" +
                                         connObj.getUserID() + "<----Password--->" +
                                         connObj.getPasswod());
                      //Add the information in the list
                      instanceConnectionsTable.put(connObj.getConnectionName(), connObj);
                    }
                    else { //If no remianing connection instance
                      break;
                    } //end else
                    startCounter++;
                  }//end while
                  System.out.println(" Instance Connection Vector size is --->"+instanceConnectionsTable.size()+"<--- Start Counter -->"+startCounter);
             //Checking the DB Instance information
             if (instanceConnectionsTable.size() < 1)
              throw new IOException("DB Instance information value is missing ");
           } catch (Exception e) {
                  System.out.println("Exception in loading instance connection information -->"+e);
           }//end catch
   }//end method




    /**
     * This method returns the value against the supplied key.
     * @param String Key name
     * @return String Key vlaue
     */
     private static String getValue( String keyName ) {
       if (prop == null || prop.getProperty(keyName) == null)
         return null;

       return prop.getProperty(keyName).trim();
     }//end method
}
