package com.i2c.payeeinfoservice.util;

import com.i2c.utils.logging.*;
import com.i2c.payeeinfoservice.PayeeInfoMain;
import com.i2c.payeeinfoservice.mapper.PayeeMapper;
import com.i2c.utils.logging.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Constants {

  public static String PAYEE_INFO_FILE_PATH = "";
  public static String PAYEE_INFO_FILE_NAME = "epiclist.ra.mask.childid.xml";

  public static String COMMON_LOG_INFO = "common";
  public static String ACH_SERVICE = "ach";
  public static String PAYEE_INFO_SERVICE = "payee_info_service";
  public static String ACH_MONITOR_SERVICE="ach_monitor";
  public static String ACH_LOAD_BATCH_MONITOR_SERVICE="ach_load_monitor";
//  public static String ACH_BATCH_CONFIG_SERVICE = "ach_batch_config";
  public static String ACH_BATCH_CONFIG_SERVICE = "HRA";
  public static String ACH_LOAD_BATCH_RETURN_MONITOR_SERVICE="ach_ld_ret_monitor";
  public static String ACH_LOAD_BATCH_RETURN_SERVICE="ach_load_return";
  public static String BILLER_XML_FILE_PATH="";

  public static String[] APPLICATIONS_NAME_ARRAY = {PAYEE_INFO_SERVICE};
  public static String INCOMING_FOLDER = "incoming";
  //Date Formats to be used in the application
  public static String DATE_FORMAT = "yyyy-MM-dd";
  public static String DATE_FORMAT_DISPLAY = "yyyy-mm-dd";
  public static String ACH_DATE_FORMAT = "MM/dd/yyyy";
  public static String MAIL_DATE_FORMAT = "MMMM dd, yyyy";
  public static String TIME_FORMAT = "HH:mm:ss";
  public static String TIME_FORMAT_DISPLAY = "hh:mm:ss";
//---------------------- Email IDs ------------------------//

  public static String REPORT_FAILURE = "REPORT_FAILURE";
  public static String REPORT_SUCCESS = "REPORT_SUCCESS";
  public static String ACH_FTPS_SUCCESS = "ACH_FTPS_SUCCESS";
  public static String ACH_FTPS_PF_ERROR = "ACH_FTPS_PF_ERROR";
  public static String ACH_FTPS_FF_ERROR = "ACH_FTPS_FF_ERROR";
  public static String ACH_WARNING = "ACH_WARNING";
  public static String ACH_RETURN_SUCCESS = "ACH_RETURN_SUCCESS";
  public static String ACH_ERROR = "ACH_ERROR";
  public static String CH_AUTH_ERROR = "CH_AUTH_ERROR";
  public static String CARD_BATCH_SUCCESS = "CARD_BATCH_SUCCESS";
  public static String CARD_BATCH_FAILURE = "CARD_BATCH_FAILURE";
  public static String TRANS_BATCH_SUCCESS = "TRANS_BATCH_SUCCESS";
  public static String TRANS_BATCH_FAILURE  = "TRANS_BATCH_FAILURE";
  public static String EOD_TRANSFER_ERROR = "EOD_TRANSFER_ERROR";
  public static String EOD_SPECIAL_ERROR = "EOD_SPECIAL_ERROR";
  public static String EOD_SRP_ERROR = "EOD_SRP_ERROR";
  public static String EOD_CARD_SUMM_ERROR = "EOD_CARD_SUMM_ERROR";
  public static String EOD_ERROR = "EOD_ERROR";
  public static String SRP_SUCESS = "SRP_SUCESS";
  public static String ACH_LD_BTCH_SUCC = "ACH_LD_BTCH_SUCC";
  public static String ACH_LD_BTCH_FAIL = "ACH_LD_BTCH_FAIL";
  public static String ACH_LD_BCH_RET_SUM = "ACH_LD_BCH_RET_SUM";
  public static String INTL_FIS_SUCC_MAIL="INTL_FIS_SUCC_MAIL";
  public static String ACH_LD_BCH_RET_FAIL = "ACH_LD_BCH_RET_FAIL";
  public static String INTL_FIS_FAIL_MAIL = "INTL_FIS_FAIL_MAIL";


  public static String ACH_DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
  public static String US_LOCALE = "US";
  public static String US_LANGUAGE = "EN";
  public static String CST_TIME_ZONE = "CST";

  public static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static String ALERT_DATE_TIME_FORMAT = "dd/MM/yy HH:mm";
  public static String EOD_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
  public static String EOD_TIME_FORMAT = "HH:mm:ss";
  public static String EOD_TIME_FORMAT_MIN = "HH:mm";
//  public static String DATE_TIME_FORMAT = "MM-dd-yyyy hh:mm:ss";
  public static String FILE_DATE_TIME_FORMAT = "MMddyyyy_hhmmss";
  public static String FILE_DATE_FORMAT = "MMddyyyy";
  public static String TRUE_OPTION = "Y";
  public static int CARD_START_SEQUENCE_NUMBER = 1;
  public static int DEFAULT_CARD_NUMBER_STRING = 0;
  public static int DEFAULT_CARD_START_NUMBER_PADDING = 0;
  public static int DEFAULT_CARD_END_NUMBER_PADDING = 9;

  public static String GENERATE_CARDS_FLAG = "G";
  public static String CHECK_DIGIT_STRING = "2121212121212121212";
  public static String LOAD_CARDS_FLAG = "L";

  public static String SERIAL_QUERY = " select dbinfo('sqlca.sqlerrd1') from ";
  public static String INSERT_QUERY_INTO_VALUE = "INTO";
  public static String INSERT_QUERY_COLUMN_START_VALUE = "(";

  //File Download Constants
  public static String STREAM_CONTENT_TYPE = "application/octet-stream";

  //Cards Production Batch Constants

  public static String STARTNOSARRAY= "";

  public static String ASTERISK_VALUE = "*";
  public static String ADD_RECORD_TYPE = "A";
  public static String DELETE_RECORD_TYPE = "D";
  public static String CHANGE_RECORD_TYPE = "C";
  public static String FULL_REFRESH_RECORD_TYPE = "F";

  public static String FILE_HEADER_PARTIAL_REFRESH = "1";
  public static String FILE_HEADER_FULL_REFRESH = "0";

  public static String FILE_HEADER_RECORD_TYPE = "FH";
  public static String BATCH_HEADER_RECORD_TYPE = "BH";
  public static String BATCH_TRAILOR_RECORD_TYPE = "BT";
  public static String FILE_TRAILOR_RECORD_TYPE = "FT";

  public static final String SESSION_MONITOR = "timeout";
  public static final int DEFAULT_SECURITY = -1;
  public static final int VALID_USER = 0;
  public static final int INVALID_USER = 1;
  public static final int ACCESS_DENIED = 3;
  public static final int INVALID_SESSION = 4;
  public static final int INVALID_LOGIN = 5;
  public static final int ACCESS_GRANTED = 6;

  //Variables for task operations
  public static final int TASK_ALL_ALLOW = 10;
  public static final int TASK_ACCESS_ALLOW = 11;
  public static final int TASK_INSERT_ALLOW = 12;
  public static final int TASK_DELETE_ALLOW = 13;
  public static final int TASK_EDIT_ALLOW = 14;
  public static final int TASK_VIEW_ALLOW = 15;

  //Transaction Parameter
  public static final String REQUEST_VALUE_DELIMETER = "|";
  public static final String RESPONSE_VALUE_DELIMETER = "|";


  //Varibles for the users status
  public static final String DELETE_USER = "2";
  public static final String DISABLE_USER = "1";
  public static final String ENABLE_USER = "0";
  public static final int MAX_TOPTEN = 10;

  public static final String ALLOW_ACCESS = "1";
  public static final String DIS_ALLOW_ACCESS = "0";

  public static final String YES_OPTION = "Y";
  public static final String NO_OPTION = "N";

  //CACM FILE VARIABLES
  public static String STATUS_TO_BE_SEND = "T";
  public static String STATUS_SEND = "S";
  public static String STATUS_IN_PROGRESS = "I";
  public static String STATUS_FAILED = "F";
  public static String STATUS_DONE = "D";

  public static String OUTPUT_FILE_HTML = "H";
  public static String OUTPUT_FILE_PDF = "P";
  public static String OUTPUT_FILE_XML = "X";
  public static String OUTPUT_FILE_CSV = "C";
  public static String OUTPUT_FILE_XL = "E";
  public static String OUTPUT_FILE_WORD = "W";

  public static String NOTIFY_EMAIL = "E";
  public static String NOTIFY_FAX = "F";
  public static String NOTIFY_SMS = "S";


  public static String REOCCURING_NONE_FLAG = "N";
  public static String REOCCURING_DAILY_FLAG = "D";
  public static String REOCCURING_WEEKLY_FLAG = "W";
  public static String REOCCURING_MONTHLY_FLAG = "M";
  public static String REOCCURING_QUATERLY_FLAG = "Q";
  public static String REOCCURING_BI_YEARLY_FLAG = "B";
  public static String REOCCURING_YEARLY_FLAG = "Y";

  public static String EXTENSION_FILE_HTML = "html";
  public static String EXTENSION_FILE_PDF = "pdf";
  public static String EXTENSION_FILE_XML = "xml";
  public static String EXTENSION_FILE_XL = "xls";
  public static String EXTENSION_FILE_CSV = "csv";
  public static String EXTENSION_FILE_WORD = "doc";

//Varibles for Report SchedularService
  public static String SERVICE_CONFIG_PATH = "";
  public static String SERVICE_CONFIG_FILE = "config.ini";
  public static long SERVICE_CONFIG_FILE_LAST_UPDATE;
  public static String SERVICE_INTERVAL = "60000";
  public static String SERVICE_DELAY = "60000";
  public static long REPORT_TERMINATION_TIME = 10000;

//Mail Settings
  public static String REPORT_SAVE_PATH = "";
  public static String REPORT_SERVER_URL = "";
  public static String REPORT_FILE_TITLE = "Report ";
  public static String SOURCE_INSTANCE_NAME = "instName";
  public static String MAIL_REPORT_MESSAGE = "";
  public static String MAIL_REPORT_ATTACH_MESSAGE = "";
  public static String MAIL_REPORT_FOOTER_MESSAGE = "";
  public static String MAIL_REPORT_SUBJECT = "";
  //Report Admin message
  public static String MAIL_REPORT_ADMIN_MESSAGE = "";
  public static String MAIL_REPORT_ADMIN_SUBJECT = "";
  //ACH Admin message
  public static String MAIL_ACH_ADMIN_MESSAGE = "";
  public static String MAIL_ACH_ADMIN_SUBJECT = "";
  public static String MAIL_ACH_ADMIN_SUBJECT_WARN = "";
  //EOD Admin message
  public static String MAIL_EOD_ADMIN_MESSAGE = "";
  public static String MAIL_EOD_ADMIN_SUBJECT = "";

//---------------------------FTPS Mail Info---------------------------------//

//---------------------------New---------------------------------//

  public static String ACH_FTPS_MAIL_SUBJECT = "Outbound NACHA";
  public static String ACH_FTPS_FAILURE_SUBJECT = "";
  public static String ACH_FTPS_MAIL_MESSAGE_HEADER = "";
  public static String ACH_FTPS_MAIL_MESSAGE_FOOTER = "";
  public static String ACH_FTPS_MAIL_MESSAGE_BODY_PART1 = "";
  public static String ACH_FTPS_MAIL_MESSAGE_BODY_PART2 = "";
  public static String ACH_FTPS_MAIL_MESSAGE_BODY_PART3 = "";
  public static String ACH_FTPS_DOWNLOAD_MESSAGE = "";
  public static String ACH_FTPS_UPLOAD_MESSAGE = "";
  public static String ACH_RETURN_PROCESS_TO = "";
  public static String ACH_RETURN_PROCESS_SUBJECT = "";



  public static String ACH_FTPS_MAIL_ADMIN_MESSAGE_1 = "";
  public static String ACH_FTPS_MAIL_ADMIN_MESSAGE_2 = "";
  public static String ACH_FTPS_MAIL_ADMIN_MESSAGE_3 = "";

//---------------------------Excisting---------------------------------//

  public static String ACH_FILE_NAME = "File Name = ";
  public static String ACH_TOTAL_BATACH = "Total Batch = ";
  public static String ACH_TOTAL_CREDIT_AMOUNT = "Total Credit Amount = $ ";
  public static String ACH_TOTAL_DEBIT_AMOUNT = "Total Debit Amount = $ ";
  public static String ACH_MOVE_SENT_DIR = "sent";

  public static String MAIL_SMTP = null;
  public static String MAIL_REPORT_FROM = null;
  public static String MAIL_REPORT_ADMIN = null;
  public static String MAIL_REPORT_TO = null;
  public static String MAIL_REPORT_CC = null;
  public static String MAIL_REPORT_BCC = null;
  public static String DEFAULT_DELIMETER_STRING = ",";
  public static String MAIL_DELIMETER_STRING = "|";
  public static String MAIL_MSG_DELIMETER_STRING = "#";
  public static String ATTCH_MSG_DELIMETER_STRING = "@";
  public static String MAIL_ATTCH_MSG_1 = " and is being sent as an attachment with this email";
  public static String MAIL_ATTCH_MSG_2 = "also ";
  public static  long MAX_ACTIVE_THREADS = 0;
  public static  long MAX_REPORT_RETRY = 5;

  public static  long START_REPORT_FAILURE_EMAIL = 5;
  public static  long START_FAILURE_EMAIL = 0;
  public static  long MAX_EMAIL_RETRY = 5;

  public static  long ACH_START_REPORT_FAILURE_EMAIL = 5;
  public static  long ACH_START_FAILURE_EMAIL = 0;
  public static  long ACH_MAX_EMAIL_RETRY = 5;

  public static String IS_TRUE="Y";
  public static String IS_FALSE="N";

// -------------Scheduler Attributes-------------

  public static String USE_ACH="USE_ACH";
  public static String USE_ACH_BATCH_CONFIG="USE_ACH_BATCH_CONFIG";
  public static String USE_ACH_LOAD_BATCH_RETURN="USE_ACH_LOAD_BATCH_RETURN";

//--------------------Transfer Service Verfiy Counter-------------------------//

  public static int VERFIY_DB_COUNTER = 0;
  public static String ALLOWED_INSTANCES = "";
  public static String CONNECTION_FILE_PATH = "";

  public static String ACH_BATCH_CONFIG_LOG_FOLDER="services_ach_batch_config";
  public static String ACH_BATCH_CONFIG_OUTPUT_PATH = null;
  public static String ACH_LOAD_BATCH_RETURN_LOG_FOLDER="services_ach_lod_ret";

  public static String ACH_FTPS_CERT_FILE = "";

  private static long CURRENT_ACTIVE_THREADS = 0;

  public static synchronized long getActiveThreads(){
    return CURRENT_ACTIVE_THREADS;
  }
  public static synchronized void setActiveThreads(long value){
  CURRENT_ACTIVE_THREADS = value;
  }

  private static long CURRENT_ACTIVE_ACH_THREADS = 0;

  public static synchronized long getActiveACHThreads(){
    return CURRENT_ACTIVE_ACH_THREADS;
  }
  public static synchronized void setActiveACHThreads(long value){
    CURRENT_ACTIVE_ACH_THREADS = value;
  }

  public static  long INCREAMENT_EMAIL_COUNTER = 10;
  public static  long ACH_INCREAMENT_EMAIL_COUNTER = 10;

  public static  long MAX_LIMIT_SEND_EMAIL_COUNTER = 3;
  public static  long ACH_MAX_LIMIT_SEND_EMAIL_COUNTER = 3;

  public static  long CURRENT_SEND_EMAIL_COUNTER = 0;
  public static  long ACH_CURRENT_SEND_EMAIL_COUNTER = 0;

  public static long DEFAULT_WAIT_TIME = 1000;
  public static long DEFAULT_SLEEP_TIME = 0;

  public static String SMS_CHANNEL = "SMS";
  public static String EMAIL_CHANNEL = "Email";

  public static String BT_ALERT = "BT";
  public static String CP_ALERT = "CP";
  public static String DP_ALERT = "DP";

  public static String EMAIL_BT_ALERT_VM = "balancethrshold_email.vm";
  public static String EMAIL_CP_ALERT_VM = "creditpost_email.vm";
  public static String EMAIL_DP_ALERT_VM = "debitpost_email.vm";

  public static String SMS_BT_ALERT_VM = "balancethrshold_sms.vm";
  public static String SMS_CP_ALERT_VM = "creditpost_sms.vm";
  public static String SMS_DP_ALERT_VM = "debitpost_sms.vm";

  public static String CARD_DIRCTV = "cardNumber";
  public static String OPERATOR_DIRCTV = "operator";
  public static String LIMIT_DIRCTV = "limit";
  public static String CARD_BAL_DIRCTV = "cardBalance";
  public static String TAN_DIRCTV = "tan";
  public static String DATE_TIME_DIRCTV = "dateTime";
  public static String AMOUNT_DIRCTV = "amount";
  public static String DESC_DIRCTV = "desc";
  public static String FTR_DIRCTV = "footer";

  public static String ALERT_OPRTR_GRTR = "G";
  public static String ALERT_OPRTR_LESS = "L";


  public static String DB_CONNECTION_STRING = "";
  public static String DB_USER_NAME = "";
  public static String DB_USER_PASSWORD = "";
  public static String DB_DRIVER_NAME = "";

  public static String CONFIGURATION_FILE = "config.ini";
  //Varibles for the Log File
  public static String LOG_FILE_PATH = "";
  public static String LOG_FILE_NAME = "";
  public static String LOG_CONTEXT_NAME = "com.i2c.payeeinfoservice";

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

  public static int START_POWER_FACTOR = 0;
  public static int ACH_START_POWER_FACTOR = 0;

  private static int CURRENT_FAIL_COUNTER = 0;
  private static StringBuffer FAILED_REPORTS = new StringBuffer();


  //***************************************************************//
  public static String ALERT_EMAIL_HDR = "Dear Administrator,";
  public static String ALERT_EMAIL_MSG = "# Monitor Thread has detected some problem in the execution of # at <#> instance. The # is not responding since # minutes.";
  public static String ALERT_EMAIL_SUBJECT = "MCP Scheduler Service Monitoring Alert";
  public static long DEFAULT_ALERT_TIME = 60;
  public static long MONITOR_SERVICE_SLEEP_TIME = 30;
  //***************************************************************//
  public static synchronized void setReportFailedCounter(int counter){
    CURRENT_FAIL_COUNTER = counter;
  }//end methid

  public static synchronized void clearReportFailedCounter(){
    CURRENT_FAIL_COUNTER = 0;
}//end methid

  public static synchronized int getReportFailedCounter(){
    return CURRENT_FAIL_COUNTER ;
  }//end methid

  public static synchronized void addFailedReport(String reportID){
    FAILED_REPORTS.append(reportID+ " \n");
  }//end methid

  public static synchronized String getFailedReport(){
    return FAILED_REPORTS.toString();
  }//end methid


  private static int CURRENT_ACH_FAIL_COUNTER = 0;
  public static synchronized void setACHFailedCounter(int counter){
    CURRENT_ACH_FAIL_COUNTER = counter;
  }//end methid

  public static synchronized void clearACHFailedCounter(){
    CURRENT_ACH_FAIL_COUNTER = 0;
}//end methid

  public static synchronized int getACHFailedCounter(){
    return CURRENT_ACH_FAIL_COUNTER ;
  }//end methid


  private static int CURRENT_EOD_FAIL_COUNTER = 0;
  public static synchronized void setEODFailedCounter(int counter){
    CURRENT_EOD_FAIL_COUNTER = counter;
  }//end methid

  public static synchronized void clearEODFailedCounter(){
    CURRENT_EOD_FAIL_COUNTER = 0;
}//end methid

  public static synchronized int getEODFailedCounter(){
    return CURRENT_EOD_FAIL_COUNTER ;
  }//end methid

//--------------------CHAUTHService-------------------//
  private static int CURRENT_CHAUTH_FAIL_COUNTER = 0;
  public static synchronized void setCHAUTHFailedCounter(int counter) {
    CURRENT_CHAUTH_FAIL_COUNTER = counter;
  } //end methid

  public static synchronized void clearCHAUTHFailedCounter() {
    CURRENT_CHAUTH_FAIL_COUNTER = 0;
  } //end methid

  public static synchronized int getCHAUTHFailedCounter() {
    return CURRENT_CHAUTH_FAIL_COUNTER;
  } //end methid

//  public static synchronized void clearFailedReport(){
//    FAILED_REPORTS.delete(0,FAILED_REPORTS.length());
//  }//end methid

  public static synchronized void deleteFailedReport(String reportInfo){
//    PayeeMapper.lgr.log(I2cLogger.FINE, "Before Delete Report Info-->"+reportInfo+"<-- Existing Info -->"+FAILED_REPORTS);
    if (reportInfo != null && reportInfo.trim().length() > 0)
      FAILED_REPORTS.replace(FAILED_REPORTS.indexOf(reportInfo),FAILED_REPORTS.indexOf(reportInfo) + reportInfo.length(),"");
//    PayeeMapper.lgr.log(I2cLogger.FINE, "After Delete Report Info-->"+reportInfo+"<-- Existing Info -->"+FAILED_REPORTS);
  }//end method

  public static int PORT_NUMBER = -1;
  private static String MACHINE_IP = null;
  public static synchronized String getMachineIP() {
    return MACHINE_IP;
  }
  public static synchronized void setMachineIP() {
    try {
      PayeeInfoMain.lgr.log(I2cLogger.INFO,
                                      " Method Name: setMachineIP -->");
      java.net.InetAddress machine_ip = java.net.InetAddress.getLocalHost();
      Constants.MACHINE_IP = machine_ip.getHostAddress();
    }
    catch (java.net.UnknownHostException uhe) {
      PayeeInfoMain.lgr.log(I2cLogger.WARNING,
                                      " Exception in getting machine ip -->" +
                                      uhe);
    }
  }
}
