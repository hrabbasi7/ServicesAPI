package com.i2c.services.util;

import java.util.logging.*;
import com.i2c.services.util.*;
import java.io.*;


/**
 * <p>Title: Log: This class provide the functionality for logging the messaging into the log file </p>
 * <p>Description: This class provide the logging functionality which is used by numerous classes</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class Log {

  private static Log log;
  private static Logger logger;
  private static FileHandler fh;


  /**
   * This method creates the logger object for logging the information.
   * @throws IOException
   */
  private Log() throws IOException {
    if (LogAttributes.getLogFilePath() == null) {
      LogAttributes.execute();
    }
    if (!new File(LogAttributes.getLogFilePath()).exists()) {
      new File(LogAttributes.getLogFilePath()).mkdirs();
    }

    String logFile = LogAttributes.getLogFilePath() + Constants.LOG_FILE_NAME +
        "-%g.log";
    System.out.println("Using log file = " + logFile);
    logger = Logger.getLogger(Constants.LOG_CONTEXT_NAME);
    fh = new FileHandler(logFile, Constants.LOG_FILE_SIZE,
                         Constants.LOG_FILE_NO);
    SimpleFormatter simpleFormatter = new SimpleFormatter();
    //simpleFormatter.formatMessage(new LogRecord(LogLevel.getLevel(Constants.LOG_FINE),""));
    fh.setFormatter(simpleFormatter);
    logger.addHandler(fh);
    // Request that every detail gets logged.
    logger.setLevel(LogLevel.getLevel(LogAttributes.getDebugLevel()));
  }

  /**
   * This method returns the log object
   * @return Log
   */

  public static Log getLogObj() {

    if (log != null) {
      return log;
    }
    try {
      log = new Log();
    }
    catch (Exception e) {
    }
    return log;
  }

  /**
   * This method destory the log object and logger
   */

  public void close() {
    fh.close();
    logger = null;
    log = null;
  }


  /**
   * This method returns the logger object which is used for logging the messages into the log file.
   * @return Logger
   */
  public Logger getLogger() {
    return logger;
  }

}
