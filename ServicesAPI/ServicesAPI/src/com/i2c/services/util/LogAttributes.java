package com.i2c.services.util;

import java.io.*;

/**
 * <p>Title:LogAttributes: This class holds the log attributes </p>
 * <p>Description: This class holds the log attributes such as log level and log file path </p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class LogAttributes {

  private static String log_level="5";
  private static String root_path=null;
  private static String log_files_path=null;


  /**
   * This method sets the log file path for logging the information
   * @throws IOException
   */
  public static void execute() throws IOException{
    log_level= String.valueOf(Constants.LOG_DEBUG_LEVEL);
    log_files_path= Constants.LOG_FILE_PATH +File.separator;
    Constants.EXACT_LOG_PATH = log_files_path + Constants.LOG_FILE_NAME + "-0.log";
  }

  /**
   * Private constructor
   */
  private LogAttributes() {
  }


  /**
   * This method returns the root path for log file
   * @return String
   */
  public static String getRoot(){
    return root_path;
  }


  /**
   * This method returns the log file path
   * @return String
   */
  public static String getLogFilePath(){
    return log_files_path;
  }

  /**
   * This method returns the debug level setted for the logger
   * @return int
   */
  public static int getDebugLevel() {
    return new Integer(log_level).intValue();
  }
}
