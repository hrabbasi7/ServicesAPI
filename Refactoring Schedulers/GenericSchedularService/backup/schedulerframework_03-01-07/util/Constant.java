package com.i2c.schedulerframework.util;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Constant
    extends BaseConstants {
  /********************* BPayment  ********************************************/
  public static String PROCESSOR_ID = "1"; //-- Processor ID

  public static String PAYEE_SERVICE_MONITOR_SERVICE = "payee_service_monitor";
  public static String PAYEE_SERVICE = "payee_service";
  public static String PAYEE_SERVICE_API = "payee_service_api";

//---------------------- Email IDs ------------------------//

  public static String PAYEE_SRV = "PAYEE_SRV";
  public static String PAYEE_SRV_FAIL = "PAYEE_SRV_FAIL";
  public static String PAYEE_SRV_SUCC = "PAYEE_SRV_SUCC";

// -------------BPScheduler Attributes-------------

  public static String USE_BILL_PAYMENT_PAYEE_PROCESSING =
      "USE_BILL_PAYMENT_PAYEE_PROCESSING";

//***** Output path for BP Service ********************
   public static String UPLOAD_FOLDER = "Upload";
  public static String DOWNLOAD_FOLDER = "Download";
  public static String PROCESSED_FOLDER = "Processed";
  public static String SENT_FOLDER = "Sent";

  public static String PAYEE_OUTPUT_PATH = null;

  public static long PAYEE_SLEEP_TIME = 60000;

  /***************************************************************************/

}
