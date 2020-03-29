package com.i2c.services.util;

import java.util.*;
import java.text.*;

/**
 * <p>Title: DateUtil: This class performs the date manipulation operation </p>
 * <p>Description: This class manipulate the dates such as getting system's current date </p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class DateUtil {
  public static final String dateFormat = "yyyy-MM-dd";
  public static final String timeFormat = "HH:mm:ss";
  public static final String nachaDateFormat = "yyMMdd";
  public static final String nachaTimeFormat = "HHmm";


  /**
   * This method returns the current system date in the default format
   * @return String
   */
  public static String getCurrentDate() {
    //returns current date
    DateFormat formatter = new SimpleDateFormat(dateFormat);
    String curdate = formatter.format(new Date());
    return curdate;
  }

  /**
   * This method return the current system time in the default format
   * @return String
   */
  public static String getCurrentTime() {
    // returns current time
    DateFormat formatter = new SimpleDateFormat(timeFormat);
    String curtime = formatter.format(new Date());
    return curtime;
  }

  /**
   * This method returns the current system date in the given format.
   * @param format String
   * @return String
   */

  public static String getCurrentDate(String format) {
    DateFormat formatter = new SimpleDateFormat(format);
    String curdate = formatter.format(new Date());
    return curdate;
  }

  /**
   * This method converts the string into given format date. In case of any error it throws exception.
   * @param format String
   * @throws Exception
   * @return Date
   */
  public static Date getCurrentDateValue(String format) throws Exception{
    DateFormat formatter = new SimpleDateFormat(format);
    String currentDate = getCurrentDate(format);
    Date curDate = formatter.parse(currentDate);
    return curDate;
  }

  /**
   * This method returns the current system time in the given format.
   * @param format String
   * @return String
   */

  public static String getCurrentTime(String format) {
    DateFormat formatter = new SimpleDateFormat(format);
    String curtime = formatter.format(new Date());
    return curtime;
  }

  /**
   * This method add two days in the given date and returns the date as string. In case of any error
   * it throws exception.
   * @param date String
   * @throws ParseException
   * @return String
   */

  public static String getDateAfter2Days(String date) throws ParseException {
    //return date after 2 days   from the date passed
    DateFormat formatter = new SimpleDateFormat(dateFormat);
    Date pdate = formatter.parse(date);
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime(pdate);
    gc.add(Calendar.DAY_OF_MONTH, 2);
    return formatter.format(gc.getTime());
  }

  /**
   * This method adds the given number of days into the current system date and returns the resulting date
   * string. In case of any error it throws exception which describes the cause of the error.
   * @param noOfDays String
   * @throws ParseException
   * @return String
   */
  public static String getDateAfterNDays(String noOfDays) throws ParseException {
    String date = getCurrentDate();
    //return date after n days   from the date passed
    DateFormat formatter = new SimpleDateFormat(dateFormat);
    Date pdate = formatter.parse(date);
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime(pdate);
    double days = Double.parseDouble(noOfDays);
    int dayLimit = (int) days;
    gc.add(Calendar.DAY_OF_MONTH, dayLimit);
    return formatter.format(gc.getTime());
  }

  /**
   * This method convert the given date string into date. In case of any error it throws exception which
   * describes the cause of the error.
   * @param date String
   * @throws ParseException
   * @return String
   */

  public static String formatDate(String date) throws ParseException {
    DateFormat formatter = new SimpleDateFormat(dateFormat);
    Date condate = formatter.parse(date);
    formatter = new SimpleDateFormat(nachaDateFormat);
    String convertedDate = formatter.format(condate);
    return convertedDate;
  }

  /**
   * This method is used to format the time. It convert the given time string into the formatted time string.
   * @param time String
   * @throws ParseException
   * @return String
   */

  public static String formatTime(String time) throws ParseException {
    DateFormat formatter = new SimpleDateFormat(timeFormat);
    Date contime = (Date) formatter.parse(time);
    formatter = new SimpleDateFormat(nachaTimeFormat);
    String convertedTime = formatter.format(contime);
    return convertedTime;
  }

  /**
   * This method formats the date. It first convert the date string into the old format and
   * then convert the date into new format and return the formatted date string.
   * @param date String
   * @param newFormat String
   * @param oldFormat String
   * @throws ParseException
   * @return String
   */

  public static String formatDate(String date, String newFormat, String oldFormat) throws ParseException {
    DateFormat formatter = new SimpleDateFormat(oldFormat);
    Date orgDate = formatter.parse(date);
    String formattedDate = new SimpleDateFormat(newFormat).format(orgDate);
    return formattedDate;
  }


  /**
   * This method formats the time. It first converts the time string into the old format time
   * and then converts the time into new format time and returns it as string.
   * @param time String
   * @param newFormat String
   * @param oldFormat String
   * @throws ParseException
   * @return String
   */
  public static String formatTime(String time, String newFormat,
                                  String oldFormat) throws ParseException {
    Date orgDate = new SimpleDateFormat(oldFormat).parse(time);
    String formattedTime = new SimpleDateFormat(newFormat).format(orgDate);
    return formattedTime;
  }

  /**
   * This method returns the date time after adding the given number of days into the current system
   * date/time.
   * @param processDays int
   * @throws ParseException
   * @return String
   */

  public static String getLimitDate(int processDays) throws ParseException {
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "DateUtil:getLimitDate----->Received process days : " + processDays);
      DateFormat formatter = new SimpleDateFormat(dateFormat);

      java.util.Date currentDate = formatter.parse(DateUtil.getCurrentDate()); //current system date
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "DateUtil:getLimitDate------>Current Date : " +
                                      currentDate);
      GregorianCalendar gc = new GregorianCalendar();
      gc.setTime(currentDate);

      switch (gc.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.SATURDAY: {
          processDays += 1;
          break;
        }
        case Calendar.FRIDAY: {
          break;
        }
        default:
          processDays += 2;
      }
      gc.add(Calendar.DAY_OF_MONTH, processDays *-1);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "*****************************End-->DateUtil:getLimitDate********************************");

      return formatter.format(gc.getTime());
    }

    catch (ParseException pex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "TransDAO:getLimitDate----------->Parse Exception -- > " +
                                      pex.getMessage());
      throw pex;
    }
  }
}
