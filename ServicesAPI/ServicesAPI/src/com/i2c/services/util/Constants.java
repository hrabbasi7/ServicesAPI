package com.i2c.services.util;

import java.io.File;

/**
 * <p>Title: Constants : This class contains Common variables which are used by different classes </p>
 * <p>Description: This class holds the common variablea which are being used by different
 * classes for different services such as whether any service is allowed or the
 * type of tansaction (online or batch mode) </p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class Constants {
  //Date Formats to be used in the application
  public static String PO_DATE_FORMAT_DOB = "MMddyyyy";
  public static String DATE_FORMAT = "yyyy-MM-dd";
  public static String WEB_DATE_FORMAT = "MM/dd/yyyy";
  public static String IVR_DATE_FORMAT = "MM-dd-yyyy";
  public static String DATE_FORMAT_DISPLAY = "yyyy-mm-dd";
  public static String SOLSPARK_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static String EXP_DATE_FORMAT = "MMyy";
  public static String INFORMIX_DATE_FORMAT = "yyyy-MM-dd";

  public static String HSMSERVICE_LOG_PATH = "hsm-logs";
  public static String HSMSERVICE_CONF_PATH = "wrapper.ini";

  public static String TIME_FORMAT = "HH:mm:ss";
  public static String TIME_FORMAT_DISPLAY = "hh:mm:ss";

  public static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static String EOD_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
  public static String EOD_TIME_FORMAT = "HH:mm";
//  public static String DATE_TIME_FORMAT = "MM-dd-yyyy hh:mm:ss";
  public static String FILE_DATE_TIME_FORMAT = "MMddyyyy_hhmmss";

  public static char DIFF_IN_MILLIS = 'L';
  public static char DIFF_IN_SECS = 'S';
  public static char DIFF_IN_MINS = 'I';
  public static char DIFF_IN_HRS = 'H';
  public static char DIFF_IN_DAYS = 'D';
  public static char DIFF_IN_MONTHS = 'M';

  public static String TRANSFERAPI_LOG_PATH = "";
  public static String LOG_FILE_PATH = "";
  public static String HSM_LOG_FILE_PATH="";
  public static String HSM_WRPR_FILE_PATH=setHSMWrapperFilePath();
  public static String FUR_LOG_FILE_PATH="";
  public static String LOG_FILE_NAME = "services-log";
  public static String LOG_CONTEXT_NAME = "com.i2c.services";

  public static int LOG_FILE_SIZE = 1000000;
  public static int LOG_FILE_NO = 10;
  public static int LOG_DEBUG_LEVEL = 7;
  public static int LOG_SEVERE = 1;
  public static int LOG_WARNING = 2;
  public static int LOG_INFO = 3;
  public static int LOG_CONFIG = 4;
  public static int LOG_FINE = 5;
  public static int LOG_FINER = 6;
  public static int LOG_FINEST = 7;

  //Flags for Transaction Types
  public static String SUCCESSFUL_TRANS_ONLY="S";
  public static String UNSUCCESSFUL_TRANS_ONLY="U";
  public static String ALL_TRANS="B";

  public static String SERIAL_QUERY = " select dbinfo('sqlca.sqlerrd1') from ";
  /**
   * variable used for getting start string of the table name
   */
  public static String INSERT_QUERY_INTO_VALUE = "into";
  /**
   * variable used for getting end string of table name
   */
  public static String INSERT_QUERY_COLUMN_START_VALUE = "(";


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

  public static String SUCCESS_CODE="00";
  public static String SUCCESS_MSG= "OK";
  public static String SKIP_CODE="51";

  public static String DEFAULT_EXP_DAYS="15";
  public static int NO_OF_TRANS=5;

  public static String DEVICE_TYPE_CS="C";
  public static String DEVICE_TYPE_WS="H";

  //Device types for Different Services
  public static String ACTIVE_CARD_SERVICE = "CARD_ACTIVATE";
  public static String ACTIVE_AND_LOAD_CARD_SERVICE = "ACTIVATEANDLOAD";
  public static String DEACTIVATE_CARD_SERVICE= "CARD_DEACTIVATE";
  public static String GET_CARD_STATUS_SERVICE = "GET_CARD_STATUS";
  public static String SET_CARD_STATUS_SERVICE = "SET_CARD_STATUS";
  public static String GET_PIN_SERVICE="GET_CARD_PIN";
  public static String GET_AAC_SERVICE="GET_AAC";
  public static String GET_CH_PAYEES_SERVICE="GET_CH_PAYEES";
  public static String GET_BP_STATUS_SERVICE="GET_BP_STATUS";
  public static String RESET_AAC="RESET_AAC";
  public static String CH_PROFILE_UPD_SERVICE="UPDATE_PROFILE";
  public static String BALANCE_INQUIRY_SERVICE="ATM_BI";
  public static String CARD_INSTANT_ISSUE_SERVICE="CARD_INST_ISSUE";

  public static String CHECK_BILL_PAY_SERVICE="CHECK_BILL_PAY";
  public static String ONLINE_BILL_SERVICE="ONLINE_BILL_PAY";
  public static String BILL_PAY_TRANS_STMT_SERVICE="BP_TRANS_STMT";

  public static String TRANS_SERVICE = "MINI_STMT_FEE";

  public static String MINI_STMT_SERVICE="MINI_STMT_FEE";
  public static String TRANS_HISTORY_SERVICE="STMT_FEE";

  public static String ACH_TRANS_HISTORY_SERVICE="ACH_STMT_FEE";
  public static String ACH_MINI_STMT_SERVICE="ACH_MINI_STMT_FEE";
  public static String VAS_TRANS_HISTORY_SERVICE="VAS_STMT_FEE";
  public static String VAS_MINI_STMT_SERVICE="VAS_MINI_STMT_FEE";

  public static String ACH_LOAD_SERVICE="ACH_USE";
  public static String ADD_FUNDS="SW_DEPOSIT";
  public static String ALL_CASH_OUT_SERVICE="CLOS_ACT";
  public static String REP_STOLEN_SERVICE="CARD_REPL";
  public static String ACH_ACCOUNT_FEE = "ADD_ACH_AC_WITHD";
  public static String ACH_VERIFY_SERVICE = "VERIFY_ACH";
  public static String CHANGE_ACH_ACCOUNT_SERVICE="CHNG_ACH_NICK";
  public static String ACH_ACCT_STATUS_SERVICE="GET_VERIFY_STATUS";
  public static String ACH_REG_EXPIRY_DAYS_SERVICE="ACHREGEXPIRYDAYS";
  public static String VALIDATE_PIN_SERVICE="VALIDATE_PIN";
  public static String VALIDATE_AAC_SERVICE="VALIDATE_AAC";
  public static String SET_PIN_SERVICE="SET_CARD_PIN";
  public static String PURCHASE_SERVICE="POS_PUR_PIN";
  public static String TRANS_INFO_SERVICE="GET_TRANS_INFO";
  public static String LOAD_FUNDS_SERVICE="ACH_USE";
  public static String WITHD_FUNDS_SERVICE="ACH_WITHD";
  public static String ACH_ACCOUNT_INFO_SERVICE="GET_ACH_INFO";
  public static String ACQ_CHARGE_FEE="ACQ_CHARGE_FEE";
  public static String REV_ACQ_FEE="REV_ACQ_CHARGE_FEE";
  public static String PRE_AUTHORIZATION="POS_PUR_PIN";
  public static String REVERSAL_SERVICE="REVERSAL";
  public static String BILL_PAY_REVERSAL_SERVICE="BILL_PAY_REVERSAL";
  public static String FORCE_POST_AUTHORIZATION="POS_PUR_PIN";
  public static String GET_CH_ACCOUNTS_SERVICE="GET_CH_ACCOUNTS";
  public static String SET_AAC_SERVICE="SET_AAC";
  public static String RESET_PIN="RESET_PIN";

  public static String WEB_WITHDRAWAL = "WS_WITHD" ;
  public static String CHANGE_ACH_ACCT_INFO_SERVICE="CHNG_ACH_INFO_UV";
  public static String VAS_BALANCE_INQUIRY = "VAS_BI";
  public static String VAS_PREAUTH= "VAS_PREAUTH";
  public static String VAS_PREAUTH_REVERSAL= "VAS_PREAUTH";
  public static String VAS_DEBIT = "VAS_DR";
  public static String VAS_CREDIT="VAS_CR";
  public static String VAS_FUNDS_TRANSFER_FROM="VAS_FUNDS_TSFR_FROM";
  public static String VAS_FUNDS_TRANSFER_TO="VAS_FUNDS_TSFR_TO";
  public static String GET_LINKED_CARDS="GET_LINKD_CARDS";
  public static String GET_VAS_ACCOUNTS="GET_VAS_ACCTS";
  public static String SET_LINKED_CARDS="SET_LINKD_CARDS";
  public static String UNLINK_CARDS="SET_UNLINKD_CARDS";
  public static String LINKD_CARD_TRANSFER="LINKD_CARD_TRANSFER";
  public static String PURCHASE_ORDER="NEW_CARD_PO";
  public static String CARD_UPGRADE="CARD_UPGRADE";
  public static String CARD_REISSUE="CARD_REISSUE";
  public static String DIRECT_DEPOSIT="DIRECT_DEPOSIT";

  public static String NEW_CARDGEN_UPGRADE="U";
  public static String NEW_CARDGEN_UPGRADE_SPECIAL_OPTION="S";
  public static String NEW_CARDGEN_REISSUE="R";
  public static String NEW_CARDGEN_PUR_ORDER="P";

  public static String ACT_INIT_LOAD = "A";

  public static String MIN_INIT_FUNDS_PARAM="INIT_FUNDS";
  public static String BAD_PIN_TRIES_PARAM="BAD_PIN_TRIES";
  public static String BILL_PAY_LIMIT_PARAM="BILLPAYLIMIT";
  public static String BILL_PAY_LEAD_DAYS_PARAM="BILLPAY_LEADDAYS";
  public static String REM_EXP_MONTHS_BFR_ACT_PARAM = "MINMONTHB4EXPONACT";


  public static String ONLINE_BP_PROCCESSING_DAYS = "3";
  public static String CHECK_BP_PROCCESSING_DAYS = "7";

  public static int OFAC_AVS_GOOD = 0;
  public static int OFAC_FAILED = 1;
  public static int AVS_FAILED = 2;

  //descriptions for different services
  public static String CARD_ACTIVATE_MSG = "Card Activate Request";
  public static String CARD_DEACT_MSG = "Card Deactivate Request";
  public static String GET_CARD_STATUS_MSG = "Card Status Inquiry";
  public static String SET_CARD_STATUS_MSG = "Request to Change Card Status";
  public static String SET_CARD_PIN_MSG = "Request to Change Card PIN";
  public static String CARD_INSTANT_ISSUE_MSG = "Request for Instant Card Issue";
  public static String GET_AAC_MSG = "Card Access Code Inquiry";
  public static String GET_CH_PAYEE_MSG = "Get Cardholder Payees";
  public static String GET_BP_STATUS_MSG = "Get Bill Payment Status";
  public static String SET_AAC_MSG = "Request to Change Card Access Code";
  public static String RESET_AAC_MSG = "Request to Reset Card Access Code";
  public static String RESET_PIN_MSG = "Request to Reset Card PIN";
  public static String UPDATE_PROFILE_MSG = "Request to Update Card Holder Profile";
  public static String REPL_STOLEN_MSG = "Request to Replace Stolen Card";
  public static String VALIDATE_AAC_MSG  = "Request to validate Card Access Code";
  public static String VALIDATE_PIN_MSG  = "Request to validate Card PIN";
  public static String GET_CH_ACCOUNTS_MSG = "Request to Get Card Holder Accounts";
  public static String ADD_FUNDS_MSG = "Request to ADD / Withdraw Funds";
  public static String BALANCE_INQUIRY_MSG = "Card Balance Inquiry";
  public static String TRANS_INQUIRY_MSG = "Transaction Inquiry";
  public static String VAS_TRANS_INQUIRY_MSG="VAS Transaction Inquiry";
  public static String ALL_CASH_OUT_MSG = "Request for All Cash Out";
  public static String TRANS_INFO_MSG = "Request for transaction information";
  public static String BP_TRANS_INFO_MSG = "Request for Bill Payment Transaction Statement";
  public static String ACH_ACCOUNT_MSG = "Request for ACH Account Creation";
  public static String ACH_ACCOUNT_STATUS_MSG = "Request to Get ACH Account Status";
  public static String CHANGE_ACH_ACCT_MSG = "Request to Change ACH Account Information";
  public static String ACH_ACCOUNT_INFO_MSG = "Request to Get ACH Account Information";
  public static String OFAC_AVS_OK = "G";
  public static String ACTIVE_CARD = "B";
  public static String CLOSED_CARD = "F";
  public static String PRE_ACTIVE_CARD = "A";
  public static String YES_OPTION = "Y";
  public static String NO_OPTION = "N";
  public static String REISSUE_SAME_CARD_SAME_INFO = "1";
  public static String REISSUE_SAME_CARD_UPDATE_EXPIRY = "2";
  public static String REISSUE_NEW_CARD_SAME_INFO = "3";
  public static String EFFECTIVE_DATE_FORMAT = "MM-dd-yyyy";
  public static String EXACT_LOG_PATH = null;
  public static String ASSIGN_PO_DESC = "Assignment during New Card Purchase Order";
  public static String ASSIGN_CU_DESC = "Assignment during Card Upgrade";
  public static String ASSIGN_CR_DESC = "Assignment during Card Reissue (New Card Same Info)";

  public static String CHG_BACK_APPROVE = "A";
  public static String CHG_BACK_REJECT = "R";

  public static String BILL_TYPE_CHK = "C";
  public static String BILL_TYPE_ONLINE = "O";

  public static String BILL_PAY_STATUS_SCHD = "S";
  public static String BILL_PAY_STATUS_INPROGRS = "I";
  public static String BILL_PAY_STATUS_FAILED = "F";

  public static String COUPONS_STATUS_AVAIL = "A";

  public static String ADDRESS_MTCH_ZIP_NOT_MTCH = "A";
  public static String NOTHING_MTCH = "N";
  public static String ADDRESS_NOT_MTCH_ZIP_9DGT_MTCH = "W";
  public static String BOTH_MTCH_ZIP9DGT = "X";
  public static String ALL_MTCH = "Y";
  public static String ADDRESS_NOT_MTCH_ZIP_5DGT_MTCH = "Z";


  public static String ALERT_TYPE_BILL_PAY = "BP";
  public static String ALERT_OPERATOR_GRTR = "G";
  public static String ALERT_OPERATOR_LESS = "L";
  public static String ALERT_CH_ADD_EMAIL = "email";

  public static String ALERT_CH_EMAIL = "Email";
  public static String ALERT_CH_SMS = "SMS";

  private static String setHSMWrapperFilePath(){
    String path = System.getProperty("user.dir");
    String finalPath = null;
//    String token = "WEB-INF";
    String name = "wrapper.ini";
    System.out.println("<---Building HSM Wrapper File path--->" + path);
    File file = new File(path);
    finalPath = file.getParent() + File.separator + name;
//    finalPath = path.substring(0, path.indexOf(token) + token.length()) + File.separator + name;
    System.out.println("<---Final File path--->" + finalPath);
    return finalPath;
  }
}
