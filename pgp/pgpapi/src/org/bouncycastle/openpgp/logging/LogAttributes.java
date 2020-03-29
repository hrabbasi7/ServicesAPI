package org.bouncycastle.openpgp.logging;

import java.io.IOException;
import org.bouncycastle.openpgp.logging.Constants;

/**
 * Title:        adapter.ebank
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      i2cinc
 * @author shahid
 * @version 1.0
 */

public class LogAttributes {

  private static String log_level="5";
  private static String root_path=null;
  private static String log_files_path=null;

  public static void execute() throws IOException{
	log_level= String.valueOf(Constants.LOG_DEBUG_LEVEL);
        log_files_path= Constants.LOG_FILE_PATH;
  }
  private LogAttributes() {
  }

  public static String getRoot(){
    return root_path;
  }


  public static String getLogFilePath(){
    return log_files_path;
  }


  public static int getDebugLevel() {
    return new Integer(log_level).intValue();
  }
}
