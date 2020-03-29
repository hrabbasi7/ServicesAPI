package org.bouncycastle.openpgp.logging;

import org.bouncycastle.openpgp.logging.Constants;

import java.util.logging.Logger;
import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;



/**
 * @author barshad
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CommonUtilities
{
         /**
           * Method for logging the information in the log file
           * @param level: The level for the information passed to the log files
           * @param message: The message to be logged
           */
          public static Logger getLogger()
          {
                  Logger l = null;
                  try
                  {
                          Log log = Log.getLogObj();
                          l = log.getLogger();
                  }
                  catch (Exception e)
                  {
                          System.out.println("Exception in Logging information --->"+e);
                  }//end catch
                  return l;
          }//end log info method



          public static String getStackTrace(Throwable th)
          {
            final Writer trace = new StringWriter();
            final PrintWriter pw = new PrintWriter(trace);
            th.printStackTrace(pw);
            return trace.toString();
          }

}//end common utility class {
