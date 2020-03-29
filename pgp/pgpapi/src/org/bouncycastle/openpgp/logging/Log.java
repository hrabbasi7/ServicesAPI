/*
 * Created on Sep 8, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.bouncycastle.openpgp.logging;

import java.util.logging.*;
import java.io.*;
import java.util.*;

import org.bouncycastle.openpgp.logging.LogAttributes;
import org.bouncycastle.openpgp.logging.LogLevel;

/**
 * @author srashid
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Log {

        private static Log log;
        private static Logger logger ;
        private static FileHandler fh ;

        private Log() throws IOException{

                String logFile = LogAttributes.getLogFilePath() + File.separator + Constants.LOG_FILE_NAME +"-%g.log" ;
                System.out.println("Using log file = " + logFile);
                logger = Logger.getLogger(Constants.LOG_CONTEXT_NAME);
                fh = new FileHandler(logFile,Constants.LOG_FILE_SIZE,Constants.LOG_FILE_NO);
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                //simpleFormatter.formatMessage(new LogRecord(LogLevel.getLevel(Constants.LOG_FINE),""));
                fh.setFormatter(simpleFormatter);
                logger.addHandler(fh);
                // Request that every detail gets logged.
                logger.setLevel(LogLevel.getLevel(LogAttributes.getDebugLevel()));
          }

          public static Log getLogObj(){

                if(log!=null)
                        return log;
                try
                {
                  log = new Log();
                }
                catch(Exception e)
                {
                  System.out.println("Exception in Creating Log Object --->" + e);
                }
                        return log;
          }

          public void close(){
                  fh.close();
                  logger = null ;
                  log = null ;
          }

          public Logger getLogger(){
                  return logger;
          }
}
