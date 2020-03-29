package com.i2c.services.util;

import com.i2c.services.util.*;
import com.i2c.services.*;
import com.i2c.solspark.*;
import java.util.logging.*;
import java.text.*;
import java.util.*;
import java.io.*;

/**
 * <p>Title:CommonUtilities: This method provide general functions which are used by other classes </p>
 * <p>Description: It provides the functions which are common across the classes </p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */


public class CommonUtilities {

  public interface Padding {
    public static int RIGHT = 1;
    public static int LEFT = 2;
  }

  /**
   *This method is used to add the pad string at the end of the given data string. This method first checks
   * the length of the string if it lesser then the required length then it adds the pad string to the
   * given data string
   * @param direction int
   * @param totallength int
   * @param text String
   * @param pad String
   * @return String
   */

  public static String addPadding(int direction,int totallength,String text,String pad) {
    getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "addPadding -- > Direction : " + direction +
                    "Total Length : " + totallength + "Text : " + text +
                    "Pad : " + pad);

    if (text.length() > totallength) {
      text = text.substring(0, totallength);
      return text;
    }
    StringBuffer buffer = new StringBuffer(text);
    switch (direction) {
      case Padding.LEFT:
        while (buffer.length() != totallength) {
          buffer.insert(0, pad);
        }
      case Padding.RIGHT:
        while (buffer.length() != totallength) {
          buffer.append(pad);
        }
      default:
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Returning String --> " +
                                        buffer.toString());
        return buffer.toString();
    }
  } //end addPadding

  /**
   * Method for logging the information in the log file. It returns the logger object which is used by
   * the client to log the information  into the log file
   * @param level: The level for the information passed to the log files
   * @param message: The message to be logged
   */
  public static Logger getLogger() {
    Logger l = null;
    try {
      Log log = Log.getLogObj();
      l = log.getLogger();
    }
    catch (Exception e) {
      System.out.println("Exception in Logging information --->" + e);
    } //end catch
    return l;
  } //end log info method

  /**
   * This Method converts the date string in the required date format and returns the formatted date string.
   * @param newformat: format in which the date to be converted
   * @param formatDate: the original format of the date
   * @param dateValue: the date value
   * @return: new formated date string
   */
  public static String convertDateFormat(
      String newformat,
      String formatDate,
      String dateValue) throws Exception {
    String formattedDate = dateValue;

    try {
      getLogger().log(
          LogLevel.getLevel(Constants.LOG_FINEST),
          " Convert Date Format Method  ");
      getLogger().log(
          LogLevel.getLevel(Constants.LOG_FINEST),
          "The Original Date is  --->" + dateValue);
      java.util.Date orgdate =
          new SimpleDateFormat(formatDate).parse(dateValue);
      formattedDate = new SimpleDateFormat(newformat).format(orgdate);
      getLogger().log(
          LogLevel.getLevel(Constants.LOG_FINEST),
          "The Formatted Date is  --->" + formattedDate);
    }
    catch (Exception e) {
      getLogger().log(
          LogLevel.getLevel(Constants.LOG_WARNING),
          "Exception in converting date format --->" + e);
      throw new Exception(e);
    } //end catch
    return formattedDate;
  } //end method

  /**
   * Method for converting the date string in the required date format. In other words this method
   * converts the string into date.
   * @param newformat: format in which the date to be converted
   * @param dateValue: the date value
   * @return: new formated date string
   */
  public static Date getFormatDate(
      String newformat,
      String dateValue) throws Exception {
    try {
      getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                      " Convert Date Format Method  ");
      getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "The Original Date is  --->" + dateValue);

      java.util.Date newdate = new SimpleDateFormat(newformat).parse(dateValue);
      getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "The Formatted Date is  --->" + newdate.toString());

      return newdate;
    }
    catch (Exception e) {
      getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                      "Exception in converting date format --->" + e);
      throw new Exception(e);
    } //end catch
  } //end method

  /**
   * This method concatenates all the string in provided list and return one String which is a comma
   * seperated values string.
   * @param list ArrayList -- List of Strings
   * @return String -- Comman seperated values of list
   */
  public static String getCommaSeperatedList(ArrayList list) {
    String commaSepList = "";
    for (int i = 0; i < list.size(); i++) {
      //get the String item from the list
      String item = (String) list.get(i);
      //add the item in the commaseperated list
      commaSepList += item;

      //check is there any more record in the list after this one
      if ( (i + 1) < list.size()) {
        commaSepList += ", ";
      } //end if
    } //end for

    //return the made list
    return commaSepList;
  } //end getCommaSeperatedList

  /**
   * This method moves the given file to the given folder.
   * @param filePath String
   * @param folderPath String
   * @return boolean
   */

  public static boolean moveFileToThisFolder(String filePath, String folderPath) {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "***************************************Start-->moveFileToThisFolder************************");
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "ACHUtil:moveFileToThisFolder------->File Path : " +
                                    filePath);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "ACHUtil:moveFileToThisFolder----------->Folder Path to move file : " +
                                    folderPath);

    File origFile = new File(filePath);
    File dir = new File(folderPath);

    if (!dir.exists())
      dir.mkdirs();

    boolean result = origFile.renameTo(new File(dir, origFile.getName()));
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "****************************************End-->moveFileToThisFolder***********************");
    return result;
  }


  /**
   * This method copy the ServiceRequestObj bean attributes to the SolsparkRequestObj bean attributes.
   * @param solsReqObj SolsparkRequestObj
   * @param reqObj ServicesRequestObj
   */
  public static void fillTheCHInfoInSolsparkObj(SolsparkRequestObj solsReqObj,
                                                ServicesRequestObj reqObj) {
    if (reqObj.getFirstName() != null &&
        !reqObj.getFirstName().trim().equals(""))
      solsReqObj.setFirstName(reqObj.getFirstName());

    if (reqObj.getLastName() != null && !reqObj.getLastName().trim().equals(""))
      solsReqObj.setLastName(reqObj.getLastName());

    if (reqObj.getDob() != null && !reqObj.getDob().trim().equals("")) {
      solsReqObj.setDob(reqObj.getDob());
    } //end if

    if (reqObj.getAddressStreet1() != null &&
        !reqObj.getAddressStreet1().trim().equals(""))
      solsReqObj.setStreet1(reqObj.getAddressStreet1());

    if (reqObj.getAddressStreet2() != null &&
        !reqObj.getAddressStreet2().trim().equals(""))
      solsReqObj.setStreet2(reqObj.getAddressStreet2());

    if (reqObj.getCity() != null && !reqObj.getCity().trim().equals(""))
      solsReqObj.setCity(reqObj.getCity());

    if (reqObj.getStateCode() != null &&
        !reqObj.getStateCode().trim().equals(""))
      solsReqObj.setState(reqObj.getStateCode());

    if (reqObj.getZipCode() != null && !reqObj.getZipCode().trim().equals(""))
      solsReqObj.setPostalCode(reqObj.getZipCode());

    if (reqObj.getHomePhone() != null &&
        !reqObj.getHomePhone().trim().equals(""))
      solsReqObj.setHomePhone(reqObj.getHomePhone());

    if (reqObj.getWorkPhone() != null &&
        !reqObj.getWorkPhone().trim().equals(""))
      solsReqObj.setWorkPhone(reqObj.getWorkPhone());

    if (reqObj.getEmail() != null && !reqObj.getEmail().trim().equals(""))
      solsReqObj.setEmail(reqObj.getEmail());

    if (reqObj.getGender() != null && !reqObj.getGender().trim().equals(""))
      solsReqObj.setSex(reqObj.getGender());

    if (reqObj.getSsn() != null && !reqObj.getSsn().trim().equals(""))
      solsReqObj.setSsn(reqObj.getSsn());

    solsReqObj.setExpDate(reqObj.getExpiryDate());
    solsReqObj.setAac(reqObj.getAAC());
    solsReqObj.setAccountNum(reqObj.getAccountNo());
  } //end fillTheCHInfoInSolsparkObj

  public static String buildUpdateCHProfileQry(ServicesRequestObj reqObj) {
    Vector profQuery = new Vector();

    if (reqObj.getFirstName() != null &&
        !reqObj.getFirstName().trim().equals(""))
        profQuery.add("first_name1='" + convertValidValue(reqObj.getFirstName()) + "'");

    if (reqObj.getMiddleName() != null &&
        !reqObj.getMiddleName().trim().equals(""))
        profQuery.add("middle_name1='" + convertValidValue(reqObj.getMiddleName()) + "'");

    if (reqObj.getLastName() != null &&
        !reqObj.getLastName().trim().equals(""))
        profQuery.add("last_name1='" + convertValidValue(reqObj.getLastName()) + "'");

    if (reqObj.getDob() != null && !reqObj.getDob().trim().equals("")) {
        try {
            String dob = CommonUtilities.convertDateFormat(Constants.
                    DATE_FORMAT,
                    Constants.WEB_DATE_FORMAT, reqObj.getDob());
            profQuery.add("date_of_birth='" + convertValidValue(dob) + "'");
        } catch (Exception exp) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_FINEST),
                                            "Exception in converting date format -- > " +
                                            exp.getMessage());
        }
    } //end if

    if (reqObj.getAddressStreet1() != null &&
        !reqObj.getAddressStreet1().trim().equals(""))
        profQuery.add("address1='" + convertValidValue(reqObj.getAddressStreet1()) + "'");

    if (reqObj.getAddressStreet2() != null &&
        !reqObj.getAddressStreet2().trim().equals(""))
        profQuery.add("address2='" + convertValidValue(reqObj.getAddressStreet2()) + "'");

    if (reqObj.getCity() != null && !reqObj.getCity().trim().equals(""))
        profQuery.add("city='" + convertValidValue(reqObj.getCity()) + "'");

    if (reqObj.getStateCode() != null &&
        !reqObj.getStateCode().trim().equals(""))
        profQuery.add("state_code='" + convertValidValue(reqObj.getStateCode()) + "'");

    if (reqObj.getZipCode() != null && !reqObj.getZipCode().trim().equals(""))
        profQuery.add("zip_postal_code='" + convertValidValue(reqObj.getZipCode()) + "'");

    if (reqObj.getHomePhone() != null &&
        !reqObj.getHomePhone().trim().equals(""))
        profQuery.add("home_phone_no='" + convertValidValue(reqObj.getHomePhone()) + "'");

    if (reqObj.getWorkPhone() != null &&
        !reqObj.getWorkPhone().trim().equals(""))
        profQuery.add("work_phone_no='" + convertValidValue(reqObj.getWorkPhone()) + "'");

    if (reqObj.getEmail() != null && !reqObj.getEmail().trim().equals(""))
        profQuery.add("email='" + convertValidValue(reqObj.getEmail()) + "'");

    if (reqObj.getGender() != null && !reqObj.getGender().trim().equals(""))
        profQuery.add("gender='" + convertValidValue(reqObj.getGender()) + "'");

    if (reqObj.getMotherMaidenName() != null &&
        !reqObj.getMotherMaidenName().trim().equals(""))
        profQuery.add("mother_maiden_nam='" + convertValidValue(reqObj.getMotherMaidenName()) +
                      "'");

    if (reqObj.getSsn() != null && !reqObj.getSsn().trim().equals(""))
        profQuery.add("ssn_nid_no='" + convertValidValue(reqObj.getSsn()) + "'");

    if (reqObj.getCountry() != null && !reqObj.getCountry().trim().equals(""))
        profQuery.add("country_code='" + convertValidValue(reqObj.getCountry()) + "'");

    if (reqObj.getBillingAddress1() != null
        && !reqObj.getBillingAddress1().trim().equals(""))
        profQuery
                .add("bill_address1='" + convertValidValue(reqObj.getBillingAddress1()) + "'");

    if (reqObj.getBillingAddress2() != null
        && !reqObj.getBillingAddress2().trim().equals(""))
        profQuery
                .add("bill_address2='" + convertValidValue(reqObj.getBillingAddress2()) + "'");

    if (reqObj.getBillingCity() != null
        && !reqObj.getBillingCity().trim().equals(""))
        profQuery.add("bill_city='" + convertValidValue(reqObj.getBillingCity()) + "'");

    if (reqObj.getBillingCountrycode() != null
        && !reqObj.getBillingCountrycode().trim().equals(""))
        profQuery.add("bill_country_code='"
                      + convertValidValue(reqObj.getBillingCountrycode()) + "'");

    if (reqObj.getBillingState() != null
        && !reqObj.getBillingState().trim().equals(""))
        profQuery.add("bill_state_code='" + convertValidValue(reqObj.getBillingState()) + "'");

    if (reqObj.getBillingZipCode() != null
        && !reqObj.getBillingZipCode().trim().equals(""))
        profQuery.add("bill_zip_code='" + convertValidValue(reqObj.getBillingZipCode()) + "'");

    if (reqObj.getForeignId() != null
        && !reqObj.getForeignId().trim().equals(""))
        profQuery.add("foreign_id='" + convertValidValue(reqObj.getForeignId()) + "'");

    if (reqObj.getForeignIdType() != null
        && !reqObj.getForeignIdType().trim().equals(""))
        profQuery
                .add("foreign_id_type='" + convertValidValue(reqObj.getForeignIdType()) + "'");

    if (reqObj.getForeignCountryCode() != null
        && !reqObj.getForeignCountryCode().trim().equals(""))
        profQuery
                .add("f_country_code='" + convertValidValue(reqObj.getForeignCountryCode()) +
                     "'");

    if (reqObj.getDrivingLicesneNo() != null
        && !reqObj.getDrivingLicesneNo().trim().equals(""))
        profQuery.add("driving_license_no='" + convertValidValue(reqObj.getDrivingLicesneNo()) +
                      "'");

    if (reqObj.getDrivingLicesneState() != null
        && !reqObj.getDrivingLicesneState().trim().equals(""))
        profQuery
                .add("driving_license_st='" + convertValidValue(reqObj.getDrivingLicesneState()) +
                     "'");

    if (profQuery.size() > 0) {

      StringBuffer StrBuf = new StringBuffer();
      StrBuf.append(" update cards set ");
      int size = profQuery.size();

      for (int i = 0; i < size - 1; i++) {
        StrBuf.append(profQuery.get(i));
        StrBuf.append(" , ");
      }

      StrBuf.append(profQuery.get(size - 1));
      StrBuf.append(" where card_no='" + reqObj.getCardNo() + "'");

      return StrBuf.toString();
    } //end if
    return null;
  } //end buildQuery


  /**
   * This method copis the SolsparkResponseObj bean to the TransactionInfoObj
   * @param solsRespObj SolsparkResponseObj
   * @param respObj ServicesResponseObj
   * @param noOfTrans int
   */

  public static void fillTransFromSolsParkToServicesReqObj(SolsparkResponseObj
      solsRespObj, ServicesResponseObj respObj, int noOfTrans) {
    if (solsRespObj.getTransactionList().size() == 0)
      return;

    for (int i = 0; i < noOfTrans && i < solsRespObj.getTransactionList().size();
         i++) {
      //retreive the transaction object out of the transaction list
      TransInfoObj transInfo = (TransInfoObj) solsRespObj.getTransactionList().
          get(i);
      //make the transaction object
      TransactionObj transObj = new TransactionObj();
      transObj.setTransId(transInfo.getTransID());
      transObj.setTransDate(transInfo.getTransDate());
      transObj.setAccountNo(transInfo.getAccountID());
      transObj.setAmount(transInfo.getAmount());
      transObj.setBusinessDate(transInfo.getPostDate());
      transObj.setDescription(transInfo.getDescription());
      transObj.setTransTypeId(transInfo.getTransTypeID());
      //set the transaction object in the response trans list
      respObj.getTransactionList().add(transObj);
    } //end for
  } //end fillTransFromSolsParkToServicesReqObj

  /**
   * This method builds the query for ACH account update
   * @param requestObj ServicesRequestObj
   * @return String
   */

  public static String makeAchAccountUpdateQry(ServicesRequestObj requestObj) {
    Vector updQuery = new Vector();

    if (requestObj.getNickName() != null &&
        !requestObj.getNickName().trim().equals(""))
      updQuery.add("ach_acct_nick='" + requestObj.getNickName() + "'");

    if (requestObj.getBankName() != null &&
        !requestObj.getBankName().trim().equals(""))
      updQuery.add("bank_name='" + requestObj.getBankName() + "'");

    if (requestObj.getBankAddress() != null &&
        !requestObj.getBankAddress().trim().equals(""))
      updQuery.add("bank_address='" + requestObj.getBankAddress() + "'");

    if (requestObj.getBankAcctNo() != null &&
        !requestObj.getBankAcctNo().trim().equals(""))
      updQuery.add("account_no='" + requestObj.getBankAcctNo() + "'");

    if (requestObj.getBankAcctTitle() != null &&
        !requestObj.getBankAcctTitle().trim().equals(""))
      updQuery.add("account_title='" + requestObj.getBankAcctTitle() + "'");

    if (requestObj.getBankAcctType() != null &&
        !requestObj.getBankAcctType().trim().equals(""))
      updQuery.add("account_type='" + requestObj.getBankAcctType() + "'");

    if (requestObj.getBankRoutingNo() != null &&
        !requestObj.getBankRoutingNo().trim().equals(""))
      updQuery.add("routing_no='" + requestObj.getBankRoutingNo() + "'");

    if (updQuery.size() > 0) {

      StringBuffer StrBuf = new StringBuffer();
      StrBuf.append(" update ach_accounts set ");
      int size = updQuery.size();

      for (int i = 0; i < size - 1; i++) {
        StrBuf.append(updQuery.get(i));
        StrBuf.append(" , ");
      }

      StrBuf.append(updQuery.get(size - 1));
      StrBuf.append(" where ach_acct_sr_no=" + requestObj.getAchAccountNo() +
                    "");

      return StrBuf.toString();
    } //end if
    return "";
  } //end makeAchAccountUpdateQry

  /**
   * This method calucates the micro payments amount
   * @return String[]
   */

  public static String[] calculateMicroPayments() {
    // Generating Random floats / Formatting

    Random rand = new Random();

    float testAmmount1 = rand.nextFloat();
    if (testAmmount1 == 0.00) {
      testAmmount1 += 0.01;
    } //end if

    NumberFormat formatter = new DecimalFormat("0.00");
    String ammount1 = formatter.format(testAmmount1);

    float testAmmount2 = rand.nextFloat();
    if (testAmmount1 == 0.00) {
      testAmmount1 += 0.01;
    } //end if
    String ammount2 = formatter.format(testAmmount2);

    String[] amounts = new String[2];
    amounts[0] = ammount1;
    amounts[1] = ammount2;
    return amounts;
  } //end calculateMicroPayments


  /**
   * This method builds the query. It append comman (,) if the query has more argument.
   * @param query StringBuffer
   * @param info String
   * @param lastParam boolean
   */
  public static void buildQueryInfo(
      StringBuffer query,
      String info,
      boolean lastParam) {
    try {
      System.out.println(
          " In the method for inserting query information ---->" + info);
      if (info != null)
        query.append(CommonUtilities.convertValidValue(info));
      else
        query.append("null");

      if (!lastParam)
        query.append(",");

    }
    catch (Exception e) {
      System.out.println("Exception in Build Query Information ----> " + e);
    } //end catch
  } //end build query information method


  /**
   * This method is used to convert the string into valid character
   * @param value String
   * @return String
   */
  public static String convertValidValue(String value) {
    try {
      getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "<- String to be converted for Special Characters ->" +
                      value);
      if (value == null || value.trim().length() < 1)
        return null;
      if (value.indexOf("'") > -1)
        value = value.replaceAll("'", "''");
      if (value.indexOf("\"") > -1)
        value = value.replaceAll("\"", "\"\"");
    }
    catch (Exception e) {
      getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                      "<---- Exception in converting into Valid values ------>" +
                      e);
    } //end catch
    getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                    "<-- Final String After conversion for Special Characters -->" +
                    value);
    return "'" + value.trim() + "'";
  } //end method

  /**
   * This method gets the system date in given format and returns it.
   * @param format String
   * @return String
   */

  public static String getCurrentFormatDate(String format) {
    String date = "";
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      date = sdf.format(new java.util.Date());
      getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                      "<- Current date is ---->" + date);
    }
    catch (Exception e) {
      getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                      "Exception in Getting DB Format date --->" + e);
    } //end catch
    return date;
  } //end method

  /**
   * This method adds given number of days in the given date and returns the resulting date.
   * @param sourceformat String
   * @param date String
   * @param days String
   * @param fianlFormat String
   * @param field int
   * @return String
   */

  public static String addDaysInDate(
      String sourceformat,
      String date,
      String days,
      String fianlFormat, int field) {
    String newDate = date;
    SimpleDateFormat sdf = null;
    GregorianCalendar calcDate = null;
    try {

      if (days == null || days.trim().equals(""))
        days = "0";

      CommonUtilities.getLogger().log(
          LogLevel.getLevel(Constants.LOG_FINEST),
          "Original Date -->" + date + "<-- Days -->" + days);
      sdf = new SimpleDateFormat(sourceformat);
      calcDate = new GregorianCalendar();
      calcDate.setTime(sdf.parse(newDate));
      calcDate.add(field, Integer.parseInt(days));
      sdf = new SimpleDateFormat(fianlFormat);
      newDate = sdf.format(calcDate.getTime());
      CommonUtilities.getLogger().log(
          LogLevel.getLevel(Constants.LOG_FINEST),
          "Final Date -->" + newDate);
    }
    catch (Exception e) {
      CommonUtilities.getLogger().log(
          LogLevel.getLevel(Constants.LOG_WARNING),
          "Exception in adding Days method--->" + e);
    } //end catch
    return newDate;
  } //end method

  /**
   * This method converts the date string into given format date.
   * @param date String
   * @param format String
   * @return Date
   */

   public static Date getParsedDate(String date, String format) {
    Date prsedValue = null;
    SimpleDateFormat sdf = null;
    try {

      CommonUtilities.getLogger().log(
          LogLevel.getLevel(Constants.LOG_CONFIG),
          "Methid for parsing String date value to get Date object-->" + date +
          "<---Format--->" + format);

      sdf = new SimpleDateFormat(format);
      prsedValue = sdf.parse(date);

      CommonUtilities.getLogger().log(
          LogLevel.getLevel(Constants.LOG_CONFIG),
          "String value successfully parsed to --->" + prsedValue);
    }
    catch (ParseException ex) {
      CommonUtilities.getLogger().log(
          LogLevel.getLevel(Constants.LOG_WARNING),
          "Execption in parsing String value to get Date object --->" +
          prsedValue);
    }
    return prsedValue;
  }


  /**
   * This method coverts the given date into given format string and returns it.
   * @param date Date
   * @param format String
   * @return String
   */
  public static String getFormatedDate(Date date, String format) {
    String frmtedValue = null;
    SimpleDateFormat sdf = null;
    try {

      CommonUtilities.getLogger().log(
          LogLevel.getLevel(Constants.LOG_CONFIG),
          "Methid for formating Date value to get String object-->" + date +
          "<---Format--->" + format);

      sdf = new SimpleDateFormat(format);
      frmtedValue = sdf.format(date);

      CommonUtilities.getLogger().log(
          LogLevel.getLevel(Constants.LOG_CONFIG),
          "Date value successfully formated to --->" + frmtedValue);
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(
          LogLevel.getLevel(Constants.LOG_WARNING),
          "Execption in formating Date value to get String object --->" +
          frmtedValue);
    }
    return frmtedValue;
  }

  public static long getDiffInDates(String fromDate, String toDate,
                                    String format, char diffIn) {
      long diff = 0;
      DateFormat df = null;

      Date from = null;
      Date to = null;

      try {
          CommonUtilities.getLogger().log(
                  LogLevel.getLevel(Constants.LOG_CONFIG),
                  "Method for getting difference in dates, From Date--->" +
                  fromDate + "<---To Date--->" + toDate +
                  "<---Dates Format--->" + format +
                  "<---Calulate Diff in--->" + diffIn);

          df = new SimpleDateFormat(format);
          from = df.parse(fromDate);
          to = df.parse(toDate);

          double calcDiff = to.getTime() - from.getTime();
          CommonUtilities.getLogger().log(
                  LogLevel.getLevel(Constants.LOG_CONFIG),
                  "Method for getting difference in dates, Diff got--->" + diff);
          double div = -1;
          switch (diffIn) {
          case 'L':
              diff = (long) calcDiff;
              break;
          case 'S':
              div = 1000d;
              calcDiff = calcDiff / div;
              diff = (long) calcDiff;
              break;
          case 'I':
              div = 60000d;
              calcDiff = calcDiff / div;
              diff = (long) calcDiff;
              break;
          case 'H':
              div = 3600000d;
              calcDiff = calcDiff / div;
              diff = (long) calcDiff;
              break;
          case 'D':
              div = 86400000d;
              calcDiff = calcDiff / div;
              diff = (long) calcDiff;
              break;
          case 'M':
              div = 2592000000d;
              calcDiff = calcDiff / div;
              diff = (long) calcDiff;
              break;
          default:
              break;
          }
      } catch (ParseException ex) {
          CommonUtilities.getLogger().log(
                  LogLevel.getLevel(Constants.LOG_WARNING),
                  "Exception in getting difference in dates--->" + ex);
      }
      return diff;
  }

  /**
   * This method converts the stack trace of exception into string and returns the string.
   * @param th Throwable
   * @return String
   */

  public static String getStackTrace(Throwable th)
  {
    final Writer trace = new StringWriter();
    final PrintWriter pw = new PrintWriter(trace);
    th.printStackTrace(pw);
    return trace.toString();
  }


//  public static String addDaysInDate(
//      String sourceformat,
//      String date,
//      String days,
//      String fianlFormat) {
//    String newDate = date;
//    SimpleDateFormat sdf = null;
//    GregorianCalendar calcDate = null;
//    try {
//
//      if (days == null || days.trim().equals(""))
//        days = "0";
//
//      CommonUtilities.getLogger().log(
//          LogLevel.getLevel(Constants.LOG_FINEST),
//          "Original Date -->" + date + "<-- Days -->" + days);
//      sdf = new SimpleDateFormat(sourceformat);
//      calcDate = new GregorianCalendar();
//      calcDate.setTime(sdf.parse(newDate));
//      calcDate.add(GregorianCalendar.MONTH, Integer.parseInt(days));
//      sdf = new SimpleDateFormat(fianlFormat);
//      newDate = sdf.format(calcDate.getTime());
//      CommonUtilities.getLogger().log(
//          LogLevel.getLevel(Constants.LOG_FINEST),
//          "Final Date -->" + newDate);
//    }
//    catch (Exception e) {
//      CommonUtilities.getLogger().log(
//          LogLevel.getLevel(Constants.LOG_WARNING),
//          "Exception in adding Days method--->" + e);
//    } //end catch
//    return newDate;
//  } //end method

} //end common utility class {
