package com.i2c.service.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Constants {
  public static String REQUIRED_PARAMETER_MISSING =
      "Required parameter(s) are missing for further processing";
  public static String MISSING_FUNCTION_PARAMETERS =
      "Function attributes are missing";
  public static String NOTIFY_RESP_CODES_VALUES=null;
  public static String MESSAGE_EMPTY = "Client Message is Empty";
  public static String INVALID_MESSAGE = "Invalid client message received!";
  public static String INVALID_RESPONSE = "Invalid response received!";
  public static String PORT_NO = "SERVER_PORT";
  public static int PORT_NO_VALUE = -1;
  public static String DATE_TIME_FORMAT = "MM-dd-yyyy HH:mm:ss";
  public static String SOLSPARK_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static String SOLSPARK_DEFAULT_ERR_RESP_CODE = "28";
  public static String SOLSPARK_GET_BALANCE_FUNC = "0";
  public static String SOLSPARK_ADD_FUNDS_FUNC = "5";
  public static String SOLSPARK_ACTIVATE_CARD_FUNC = "7";
  public static String HSM_CONFIG_FILE_PATH = "";
//Card ATM and POS status flags

  public static String ATM_STATUS_FLAG = "B";
  public static String POS_STATUS_FLAG = "B";


  //Generate PIN and Access Code Method
  public static String GENERETAE_METHOD_NATURAL = "N";
  public static String GENERETAE_METHOD_LAST_4_CARD_NUMBER = "C";
  public static String GENERETAE_METHOD_LAST_4_PHONE_NUMBER = "P";
  public static String GENERETAE_METHOD_LAST_4_SSN = "S";
  public static String GENERETAE_METHOD_OTHER = "O";
  public static String GENERETAE_METHOD_EXPIRT_DATE = "E";
  public static String GENERETAE_METHOD_LAST_4_ZIP_POSTAL = "Z";
  public static String GENERETAE_METHOD_ZERO = "0";
  public static String GENERETAE_METHOD_SAME_AS_ACCESS_CODE = "A";


  public static String CLIENT_IP_NAME = "CLIENT_IP";
  public static String CLIENT_IP_VLAUE = "";
  public static String CONFIGURATION_FILE = "config.ini";
  public static String DEC_ACTIVE_STRING = "DEC_ACTIVATE";
  public static String ENC_DEC_ACTIVATE_STRING = "ENC_DEC_ACTIVATE";
  public static String ALGO_ENC_CODE = "ALGO_END_CODE";
  public static String ALGO_CODE = "ALGO_CODE";
  public static String MESSAGE_DELIMETER = "MESSAGE_DELIMETER";
  public static String MESSAGE_DELIMETER_VALUE = "|";
  public static int DEFAULT_TIME_OUT = 20000;
  public static String FUNCTION_ID = "01";
  public static String SUCCESS_FUNCTION_ID = "00";
  public static String ERROR_MALFUNCTION_ID = "96";
  public static String ERROR_AUTHORIZATION_ID = "11";
  public static String ERROR_AUTHORIZATION_MESSAGE = "Authorization Failed";
  public static String ERROR_MALFUNCTION_MESSAGE = "System Malfunction";
  public static String SCK_VALUE1 = "SCK_VALUE1";
  public static String SCK_VALUE2 = "SCK_VALUE2";
  public static String SCK_VALUE3 = "SCK_VALUE3";
  public static int SECURITY_CODE_LENGTH = 5;

  public static String ALLOWED_INSTANCES = "";
  public static String CONNECTION_FILE_PATH = "";
  public static String HSM_IP = "";
  public static int HSM_PORT = -1;
  public static int HSM_INDEX = -1;

  public static String INVALID_CARD_NUMBER_RESPONSE = "14";
  public static String GET_BALANCE_FUNCTION = "0";
  public static String GET_SERVICE_FEE_FUNCTION = "1";
  public static String ACTIVATE_CARD_FUNCTION = "7";
  public static String RELOAD_CARD_FUNCTION = "5";
  public static String UPDATE_PERSON_FUNCTION = "8";
  public static String REPLACE_LOST_STOLEN_CARD_FUNCTION = "9";
  public static String RETERIEVE_TRANSACTION_FUNCTION = "10";
  public static String CARD_TO_CARD_TRANSFER = "25";
  public static String VALIDATED_USER_FUNCTION = "26";
  public static String GET_ACH_INFO_FUNCTION = "27";
  public static String ACH_GENERATE__FUNCTION = "28";
  public static String GET_C2C_PARAMETER_VALUES = "2";

  public static String C2C_AMOUNT_PARAM = "AMTTRANSFER";

  public static String CARDS_PATH = "D:\\mcp";
  //Date Formats to be used in the application
  public static String DATE_FORMAT = "yyyy-MM-dd";
  public static String DATE_FORMAT_DISPLAY = "yyyy-mm-dd";

  public static String TIME_FORMAT = "HH:mm:ss";
  public static String TIME_FORMAT_DISPLAY = "hh:mm:ss";

//  public static String DATE_TIME_FORMAT = "MM-dd-yyyy hh:mm:ss";
  public static String FILE_DATE_TIME_FORMAT = "MMddyyyy_hhmmss";
  public static String TRUE_OPTION = "Y";
  public static int CARD_START_SEQUENCE_NUMBER = 1;
  public static int DEFAULT_CARD_NUMBER_STRING = 0;
  public static int DEFAULT_CARD_START_NUMBER_PADDING = 0;
  public static int DEFAULT_CARD_END_NUMBER_PADDING = 9;

  public static String GENERATE_CARDS_FLAG = "G";
  public static String CHECK_DIGIT_STRING = "2121212121212121212";
  public static String LOAD_CARDS_FLAG = "L";

  public static String SERIAL_QUERY = " select dbinfo('sqlca.sqlerrd1') from ";
  public static String INSERT_QUERY_INTO_VALUE = "into";
  public static String INSERT_QUERY_COLUMN_START_VALUE = "(";

  //File Download Constants
  public static String STREAM_CONTENT_TYPE = "application/octet-stream";

  //Cards Production Batch Constants

  public static String STARTNOSARRAY= "";


  //CACM FILE VARIABLES
  public static String UNBATCHED_CACM_CARDS_STATUS = "T";
  public static String SENT_BATCHED_CACM_CARDS_STATUS = "S";
  public static String CONFIRMED_BATCHED_CACM_CARDS_STATUS = "C";
  public static String CACM_STATUS_IN_PROGRESS = "I";
  public static String CACM_STATUS_FAILED = "F";
  public static String CACM_STATUS_COMPLETED = "C";
  public static String CACM_FILE_DEFAULT_COUNTS = "0";
  public static String CACM_FILE_EXCEPTION_MESSAGE_DELIMETER = ":";

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

  //Detail
  public static String DETAIL_CACM_DATE_REISSUE_EXP_DATE = "";

  public static final String SUBMIT = "Submit";
  public static final String USER_OBJECT = "userobj";
  public static final String USERID = "userid";
  public static final String REQUEST_PARAM_NAME = "request";
  public static final String REQUEST_LOGOUT = "LOGOUT";
  public static final String REQUEST_SOURCE_NAME = "source";

  //Transaction CardsConstants
  public static final String REQUEST_SEARCH = "SRHPROD"; // "req_search";
  public static final String REQUEST_PROD_INFO = "PRDINFO";
  public static final String MANAGER_ROLE = "MNG";
  public static final String CLERK_ROLE = "CLK";

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

//Varibles for Report solsparkservice
  public static String SERVICE_CONFIG_PATH = "";
  public static String SERVICE_CONFIG_FILE = "config.ini";
  public static long SERVICE_CONFIG_FILE_LAST_UPDATE;
  public static String SERVICE_INTERVAL = "60000";
  public static String SERVICE_DELAY = "60000";

//Mail Settings
  public static String REPORT_SAVE_PATH = "";
  public static String REPORT_SERVER_URL = "";
  public static String REPORT_FILE_TITLE = "Report ";
  public static String SOURCE_INSTANCE_NAME = "DbInstanceName";
  public static String SOURCE_INSTANCE_VALUE = "";
  public static String MAIL_REPORT_MESSAGE = "";
  public static String MAIL_REPORT_ATTACH_MESSAGE = "";
  public static String MAIL_REPORT_FOOTER_MESSAGE = "";
  public static String MAIL_REPORT_SUBJECT = "";
  public static String MAIL_REPORT_ADMIN_MESSAGE = "";
  public static String MAIL_REPORT_ADMIN_SUBJECT = "";
  public static String MAIL_REPORT_ADMIN_FOOTER = "";

  public static String MAIL_ADMIN_NOTIFY_SUBJECT = "Notification for new response code";
  public static String MAIL_ADMIN_NOTIFY_MESSAGE = "Dear Administrator\n\nThe service is unable to find out the information for the following response code in the database.";

  public static String MAIL_SMTP = null;
  public static String MAIL_REPORT_FROM = null;
  public static String MAIL_REPORT_ADMIN = null;
  public static String MAIL_REPORT_TO = null;
  public static String MAIL_REPORT_CC = null;
  public static String MAIL_REPORT_BCC = null;
  public static String DEFAULT_DELIMETER_STRING = ",";

  public static String DEVICE_TYPE="I";

  public static  long MAX_ACTIVE_THREADS = 0;
  public static  long MAX_REPORT_RETRY = 5;
  public static  long START_REPORT_FAILURE_EMAIL = 5;
  public static  long START_FAILURE_EMAIL = 0;
  public static  long MAX_EMAIL_RETRY = 5;

  private static long CURRENT_ACTIVE_THREADS = 0;

  public static synchronized long getActiveThreads(){
    return CURRENT_ACTIVE_THREADS;
  }

  public static synchronized void setActiveThreads(long value){
    CURRENT_ACTIVE_THREADS = value;
  }



  public static  long INCREAMENT_EMAIL_COUNTER = 10;
  public static  long MAX_LIMIT_SEND_EMAIL_COUNTER = 3;
  public static  long CURRENT_SEND_EMAIL_COUNTER = 0;


  public static long DEFAULT_WAIT_TIME = 1000;
  public static long DEFAULT_SLEEP_TIME = 0;

  public static String DB_CONNECTION_STRING = "";
  public static String DB_USER_NAME = "";
  public static String DB_USER_PASSWORD = "";
  public static String DB_DRIVER_NAME = "";


  //Varibles for the Log File
  public static String SERVICES_LOG_FILE_PATH="";
  public static String LOG_FILE_PATH = "";
  public static String LOG_FILE_NAME = "";
  public static String LOG_CONTEXT_NAME = "com.i2c.service";

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

  public static String CARD_TO_CARD_ID = "C2C";
  public static String CARD_TO_CARD_SELF_TRNSF = "C2C_SELF_TRSFR";
  public static String CARD_TO_CARD_SHARE_FUNDS = "C2C_FUNDS_TRSFR";

  public static String PRE_ACTIVE_CARD_STATUS = "A";
  public static String ACTIVE_CARD_STATUS = "B";
  public static String LOST_CARD_STATUS = "C";
  public static String STOLEN_CARD_STATUS = "D";
  public static String RESTRICTED_CARD_STATUS = "E";
  public static String CLOSED_CARD_STATUS = "F";
  public static String LOST_NOT_CAPT_CARD_STATUS = "G";
  public static String STOLEN_NOT_CAPT_CARD_STATUS = "H";
  public static String INACTIVE_CARD_STATUS = "I";
  public static String REISSUE_CARD_STATUS = "R";



  public static int START_POWER_FACTOR = 0;

  private static int CURRENT_FAIL_COUNTER = 0;
  private static StringBuffer FAILED_REPORTS = new StringBuffer();

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



//  public static synchronized void clearFailedReport(){
//    FAILED_REPORTS.delete(0,FAILED_REPORTS.length());
//  }//end methid

  public static synchronized void deleteFailedReport(String reportInfo){
    System.out.println("Before Delete Report Info-->"+reportInfo+"<-- Existing Info -->"+FAILED_REPORTS);
    if (reportInfo != null && reportInfo.trim().length() > 0)
      FAILED_REPORTS.replace(FAILED_REPORTS.indexOf(reportInfo),FAILED_REPORTS.indexOf(reportInfo) + reportInfo.length(),"");
    System.out.println("After Delete Report Info-->"+reportInfo+"<-- Existing Info -->"+FAILED_REPORTS);
  }//end method

  private static String MACHINE_IP = null;
  public static synchronized String getMachineIP() {
      return MACHINE_IP;
  }
  public static synchronized void setMachineIP() {
    try {
//      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_INFO), " Method Name: setMachineIP -->");
      java.net.InetAddress machine_ip = java.net.InetAddress.getLocalHost();
      Constants.MACHINE_IP = machine_ip.getHostAddress();
    }
    catch (java.net.UnknownHostException uhe) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      " Exception in getting machine ip -->" +
                                      uhe);
    }
  }


}
