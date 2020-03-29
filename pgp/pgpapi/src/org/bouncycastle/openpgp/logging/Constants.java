package org.bouncycastle.openpgp.logging;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Constants
{

  public static String CONFIGURATION_FILE = "config.ini";

  //-----------------------Log File Related Attributes-------------------------//
  public static String LOG_FILE_PATH = "";
  public static String LOG_FILE_NAME = "";
  public static String LOG_CONTEXT_NAME = "org.bouncycastle.openpgp";

  public static int LOG_FILE_SIZE = 0;
  public static int LOG_FILE_NO = 0;
  public static int LOG_DEBUG_LEVEL = 2;
  public static int LOG_SEVERE = 1;
  public static int LOG_WARNING = 2;
  public static int LOG_INFO = 3;
  public static int LOG_CONFIG = 4;
  public static int LOG_FINE = 5;
  public static int LOG_FINER = 6;
  public static int LOG_FINEST = 7;
}
