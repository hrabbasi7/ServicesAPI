package org.bouncycastle.openpgp.logging;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

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

  private static Properties prop;

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
    private static void validateLoadValues()  throws IOException
    {
     //Varibles for the Log File
     if (getValue("LogPath") != null && getValue("LogPath").trim().length() > 0 )
     {
       Constants.LOG_FILE_PATH = getValue("LogPath");
       if (!new File(Constants.LOG_FILE_PATH).exists())
       {
         new File(Constants.LOG_FILE_PATH).mkdir();
       }
     }
     else
     {
           throw new IOException("LogPath value is missing ");
     }


     if (getValue("LogFile") != null && getValue("LogFile").trim().length() > 0)
       Constants.LOG_FILE_NAME = getValue("LogFile");
     else
       throw new IOException("LogFile value is missing ");

     if (getValue("LogSize") != null && getValue("LogSize").trim().length() > 0)
       Constants.LOG_FILE_SIZE = Integer.parseInt(getValue("LogSize"));
     else
       throw new IOException("LogSize value is missing ");

     if (getValue("LogLevel") != null && getValue("LogLevel").trim().length() > 0)
       Constants.LOG_DEBUG_LEVEL = Integer.parseInt(getValue("LogLevel"));
     else
       throw new IOException("LogLevel value is missing ");

     if (getValue("NoOfLogFiles") != null && getValue("NoOfLogFiles").trim().length() > 0)
       Constants.LOG_FILE_NO = Integer.parseInt(getValue("NoOfLogFiles"));
     else
       throw new IOException("NoOfLogFiles value is missing ");

    }//end validate method

    /**
     * This method returns the value against the supplied key.
     * @param String Key name
     * @return String Key vlaue
     */
     public static String getValue( String keyName ) {
       if (prop == null || prop.getProperty(keyName) == null)
         return null;

       return prop.getProperty(keyName).trim();
     }//end method
}
