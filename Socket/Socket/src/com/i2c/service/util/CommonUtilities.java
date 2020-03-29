package com.i2c.service.util;

import com.i2c.service.base.*;
import com.i2c.service.util.*;
import com.i2c.service.solsparkservice.*;
import com.i2c.services.*;
import java.util.*;
import java.sql.*;
import java.text.*;
import com.i2c.service.excep.*;
import java.util.logging.*;
import java.util.*;
import java.sql.*;
import java.io.*;





/**
 * @author barshad
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CommonUtilities extends BaseHome {


          public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd";

          /**
           * Method for converting the invalid characters and Add a single quote
           * to the string and in case of NULL return NULL as a string
           * @param info: information to be checked and converted
           * @param addComma: flag for wheather to add , at the end of query
           */
          public static String buildQueryInfo(String info, boolean addComma) {
                  try {
                          System.out.println(" In the method for inserting query information ---->" + info);
                          if (info != null)
                                  info = CommonUtilities.convertValidValue(info);
                          else
                                  info = "null";

                          if (addComma)
                                  info += ",";

                  } catch (Exception e) {
                          System.out.println(
                                  "Exception in Build Query Information ----> " + e);
                  } //end catch
                  return info;
          } //end build query information method

          /**
           * Build information into the query
           * @param query: query to be build
           * @param info: information to be added in the query
           * @param lastParam: wheather to add , at the end of query
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

                  } catch (Exception e) {
                          System.out.println("Exception in Build Query Information ----> " + e);
                  } //end catch
          } //end build query information method

  /**
   * Method for adding the days in the date
   * @param sourceformat: source format of the data
   * @param date: the source date
   * @param days: days to be added
   * @param fianlFormat: final format of the date
   * @return: the finally added date in the require format
   */
  public static String calulateDateInfo(String sourceformat,String date,String days,String fianlFormat,int timeField){
          String newDate = date;
          SimpleDateFormat sdf = null;
          GregorianCalendar calcDate = null;
          try {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Original Date -->"+date+"<-- Days -->"+days);
                  sdf = new SimpleDateFormat(sourceformat);
                  calcDate = new GregorianCalendar();
                  calcDate.setTime(sdf.parse(newDate));
                  calcDate.add(timeField,Integer.parseInt(days));
                  sdf = new SimpleDateFormat(fianlFormat);
                  newDate = sdf.format(calcDate.getTime());
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Final Date -->"+newDate);
          } catch(Exception e){
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in adding Days method--->"+e);
          }//end catch
          return newDate;
  }//end method



	/**
	 * Method for converting the number into two zero's at the end
	 * @param number: number to be converted into two zero's
	 * @return: final string after conversation
	 */
	public static String convertTwoZero(String number){
		if (number != null) {
			String sub = number.substring(number.indexOf('.')+1);
			if (sub.length() == 1)
				number += "0";
			else if (sub.length()== 0)
				number += "00";
			else if (sub.length() > 1) {
				number = number.substring(0,number.indexOf('.')) + number.substring(number.indexOf('.'),number.indexOf('.')+3);
			}//end else if
		}//end null
		return number;
	}//end add two zero method

	/**
	 * Method for getting the current system date in DB formatted form
	 * @return : formatted date
	 */
	public static String getCurrentFormatDate(String format) {
		String date = "";
		try {
		     SimpleDateFormat sdf = new SimpleDateFormat(format);
		     date = sdf.format(new java.util.Date());
			 getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"<- Current date is ---->" +date);
		} catch (Exception e){
			getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Getting DB Format date --->"+e);
		}//end catch
		return date;
	}//end method

	/**
	 * Method for getting the current system time in DB formatted form
	 * @return: formatted system current time
	 */
	public static String getCurrentFormatTime(String format) {
		String date = "";
		try {
		     SimpleDateFormat sdf = new SimpleDateFormat(format);
		     date = sdf.format(new java.util.Date());
			 getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," <- Current Time is ---->" +date);
		} catch (Exception e){
			getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Getting DB Format Time --->"+e);
		}//end catch
		return date;
	}//end method

	/**
	 * Method for getting the current system date and time in DB formatted fom
	 * @return: formatted date and time
	 */
	public static String getCurrentFormatDateTime(String format) {
		String date = "";
		try {
		     SimpleDateFormat sdf = new SimpleDateFormat(format);
		     date = sdf.format(new java.util.Date());
			 getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," <- Current date time is ---->" +date);
		} catch (Exception e){
			getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Getting DB Format date time--->"+e);
		}//end catch
		return date;
	}//end method

	/**
	 * Method for converting the date string in the required date format
	 * @param newformat: format in which the date to be converted
	 * @param formatDate: the original format of the date
	 * @param dateValue: the date value
	 * @return: new formated date string
	 */
	public static String convertDateFormat(String newformat,String formatDate,String dateValue)
		throws ConvertDateFormatExcep{
	  String formattedDate = dateValue;

	  try {
			getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Convert Date Format Method  ");
			getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"The Original Date is  --->" + dateValue);
			java.util.Date orgdate = new SimpleDateFormat(formatDate).parse(dateValue);
			formattedDate = new SimpleDateFormat(newformat).format(orgdate);
			getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"The Formatted Date is  --->" + formattedDate);
	  }
	  catch (Exception e) {
		getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in converting date format --->" + e);
		throw new ConvertDateFormatExcep(e);
	  }//end catch
	  return formattedDate;
	}//end method


	/**
	 * Method for converting and handling the special characters in the string value
	 * @param value : string to be handle for special characters
	 * @return: final string value after conversion
	 */
	public static String convertValidValue(String value) {
		try {
			 getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"<- String to be converted for Special Characters ->"+value);
			 if (value == null || value.trim().length() < 1)
                           return null;
                         if (value.indexOf("'") > -1 )
			 	 value = value.replaceAll("'","''");
			 if (value.indexOf("\"") > -1 )
				 value = value.replaceAll("\"","\"\"");
		} catch(Exception e) {
			getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"<---- Exception in converting into Valid values ------>"+e);
		}//end catch
		getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"<-- Final String After conversion for Special Characters -->"+value);
		return "'"+value.trim()+"'";
	}//end method


	/**
	 * Method for logging the information in the log file
	 * @param level: The level for the information passed to the log files
	 * @param message: The message to be logged
	 */
	public static Logger getLogger() {
		Logger l = null;
		try {
			Log log = Log.getLogObj();
			l = log.getLogger();
		} catch (Exception e) {
			System.out.println("Exception in Logging information --->"+e);
		}//end catch
		return l;
	}//end log info method


	/**
	 * Method for adding the character padding to the string
	 * @param info: buffer to be padded
	 * @param character: string charcter to be padded in the buffer
	 * @param size: size of the total buffer after padding
	 * @param startPadding: padding at the start of the string or at the end
	 */
	public static StringBuffer addCharPadding(StringBuffer info,String character,int size,boolean frontPadding) {
		int position = -1;
		int limit = size - info.length();
		try {
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Orginal String for padding is --->"+info+"<--- String Size is -->"+info.length());
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Character string to be padded is --->"+character);
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Size to be padded is --->"+size);
			for (int i =0 ; i < limit ; ++i) {
				if (frontPadding)
					position = 0;
				else
					position = info.length();

				info.insert(position,character);
			}//end for
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Final String After padding is --->"+info+"<--- String Size is -->"+info.length());
		} catch (Exception e){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"  Exception in Adding Character Padding to the String method --->"+e);
		}//end catch
		return info;
	}//end method


	/**
	 * Method for adding the character padding to the string
	 * @param info: buffer to be padded
	 * @param character: string charcter to be padded in the buffer
	 * @param size: size of the total buffer after padding
	 * @param startPadding: padding at the start of the string or at the end
	 */
	public static StringBuffer addCharPadding(String value,String character,int size,boolean startPadding) {
		int position = -1;
		int limit = size - value.length();
		StringBuffer info = new StringBuffer(value);
		try {
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Orginal String for padding is --->"+info+"<--- String Size is -->"+info.length());
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Character string to be padded is --->"+character);
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Size to be padded is --->"+size);
			for (int i =0 ; i < limit ; ++i) {
				if (startPadding)
					position = 0;
				else
					position = info.length();

				info.insert(position,character);
			}//end for
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Final String After padding is --->"+info+"<--- String Size is -->"+info.length());
		} catch (Exception e){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"  Exception in Adding Character Padding to the String method --->"+e);
		}//end catch
		return info;
	}//end method


	/**
	 * Method for adding the character padding to the string
	 * @param info: buffer to be padded
	 * @param character: string charcter to be padded in the buffer
	 * @param size: size of the total buffer after padding
	 * @param startPadding: padding at the start of the string or at the end
	 */
	public static StringBuffer addCharPadding(String value,String character,int size,boolean startPadding,String fieldDesc)
			throws InvalidFieldValueExcep {
		int position = -1;
		int limit = size - value.trim().length();
		StringBuffer info = new StringBuffer(value.trim());

		CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Orginal String for padding is --->"+info+"<--- String Size is -->"+info.length());
		CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Character string to be padded is --->"+character);
		CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Size to be padded is --->"+size);
		CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Field Desc --->"+fieldDesc);

		//Checking the field length
		if (info.length() > size)
			throw new InvalidFieldValueExcep(0,"Invalid Field Length -->"+fieldDesc);

		try {
			for (int i =0 ; i < limit ; ++i) {
				if (startPadding)
					position = 0;
				else
					position = info.length();

				info.insert(position,character);
			}//end for
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Final String After padding is --->"+info+"<--- String Size is -->"+info.length());
		} catch (Exception e){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"  Exception in Adding Character Padding to the String method --->"+e);
		}//end catch
		return info;
	}//end method


	/**
	 * Method for calculating the Check Digit for a string
	 * @param value: the value whose check digit is to be calculated
	 * @return: calculated check digit
	 */
	public static String calcCheckDigit(String value) {
		String checkDigit = "";
	  	long digitSum = 0;
	  	try {
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"The Value for Calculating Check Digit is --->"+value);

			//Traversing through the Card Numbers
			for (int i = 0; i < value.length() ; ++i) {
				digitSum +=  (int)((Integer.parseInt(value.charAt(i)+"") * Integer.parseInt(Constants.CHECK_DIGIT_STRING.charAt(i)+"")) / 10)
							+ ((Integer.parseInt(value.charAt(i)+"") * Integer.parseInt(Constants.CHECK_DIGIT_STRING.charAt(i)+"")) % 10);
			}//end for

			checkDigit = ((10 - (digitSum % 10)) % 10) + "";
	  } catch (Exception ex) {
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Calculating Check Digit Method --->"+ex);
	  }//end catch
	  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"The Final Check Digit is -->"+checkDigit);
	  return checkDigit;
	}//end calc digit method

	/**
	 * @author abakar
	 * used to get a collection for generating a combo
	 * @param con
	 * @param query
	 * @param defaultOption
	 * @return
	 * @throws Exception
	 */
	public static ArrayList getCollection(Connection con, String query, String defaultOption) throws Exception {
		ArrayList list = new ArrayList();
		String[] data = new String[2];
		ResultSet rs = null;
		Statement stmt = null;

		try {
			//add data for default option
			data[0] = "";
			data[1] = defaultOption;
			list.add(data);

			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				data = new String[2];
				data[0] = rs.getString(1);
				data[1] = rs.getString(2);
				list.add(data);
			}//end while
		}//end try
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}//end catch
		finally {
			if(null != rs)
				rs.close();
			if(null != stmt)
				stmt.close();
		}//end finally

		return list;
	}//end method

	/**
	 * @author abakar
	 * concatenates ' at the start and end of a string and return it.
	 */
	public static String getSingleQuotedString(String string) {
		return "'" + string + "'";
	}//end method

	/**
	* @author abakar
	* this method is used to check whether a string is null or empty
	* @param string
	* @return
	*/
	public static boolean isNullOrEmptyString(String string) {
		if(null == string || "".equals(string))
			return true;
		else
		return false;
	}//end method

	/**
	 * Method for adding the days in the date
	 * @param sourceformat: source format of the data
	 * @param date: the source date
	 * @param days: days to be added
	 * @param fianlFormat: final format of the date
	 * @return: the finally added date in the require format
	 */
	public static String addDaysInDate(String sourceformat,String date,String days,String fianlFormat){
		String newDate = date;
		SimpleDateFormat sdf = null;
		GregorianCalendar calcDate = null;
		try {
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Original Date -->"+date+"<-- Days -->"+days);
			sdf = new SimpleDateFormat(sourceformat);
			calcDate = new GregorianCalendar();
			calcDate.setTime(sdf.parse(newDate));
			calcDate.add(GregorianCalendar.MONTH,Integer.parseInt(days));
			sdf = new SimpleDateFormat(fianlFormat);
			newDate = sdf.format(calcDate.getTime());
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Final Date -->"+newDate);
		} catch(Exception e){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in adding Days method--->"+e);
		}//end catch
		return newDate;
	}//end method


        /**
         * Method for calculating the Time Difference between the two dates
         * @param currentDate: the current/smaller date value
         * @param nextDate: the future/next data value
         * @param format: the formats of the date
         * @return: the Time Difference in millisecond
         */
        public static long calculateTimeDifference(String currentDate,String nextDate,String format){
          long timeDiff = -1;
          try {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Calculating Time Difference Method Current Time -->"+currentDate+"<--- Next --->"+nextDate);
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            java.util.Date currentDateObj = sdf.parse(currentDate);
            java.util.Date nextDateObj = sdf.parse(nextDate);
            timeDiff = nextDateObj.getTime() - currentDateObj.getTime();
          } catch (Exception ex) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in Calculating Time Difference -->"+ex);
          }//end catch
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Final Time Difference -->"+timeDiff);
          return timeDiff;
        }//end method


	/**
	 * @Author: Hassan Arif
	 * @Date: 23/02/2004
	 * Date Validator (CCYYMMDD)
	 * @param strDateFormat
	 * @return boolean
	 */
	public static boolean dateValidator(String strFormat,String strDate) {
		try{
			if(strDate==null){
				return false;
			}
		 java.util.Date d;
		 CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"strFormat: "+strFormat + " strDate: "+strDate);

		 SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
		 sdf.setLenient(false);
		 d = sdf.parse(strDate);
		 return true;
		}catch(Exception ex){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING), "Invalid Date Format Exception");
			return false;
		}
	}
	/**
	 * @Author: Hassan Arif
	 * @Date: 23/02/2004
	 * Validate numeric value
	 * @param String strNumber
	 * @return boolean
	 */
	public static boolean isNumeric(String strNumber){
		try{
		Integer.parseInt(strNumber);
		return true;
		}catch(NumberFormatException ex){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING), "Number Format Exception");
			return false;
		}
	}

	/**
	 * used to get the single option for a collection.
	 * @author abakar
	 * @param value
	 * @param text
	 * @return
	 */
	public static String[] getCollectionOption(String value, String text) {
		String[] data = new String[2];
		data[0] = value;
		data[1] = text;
		return data;
	}

	/**
	 * used to check if a string is empty
	 * @author abakar
	 * @param string
	 * @return
	 */
	public static boolean isEmptyString(String string) {
		if("".equals(string)) {
			return true;
		}

		return false;
	}//end method

	/**
	 * used to check if an object is null
	 * @author abakar
	 * @param object
	 * @return
	 */
	public static boolean isNull(Object object) {
		if(null == object) {
			return true;
		}

		return false;
	}//end method


        /**
         * Method for converting the delimeted String into Array
         * @param value: the string containing the delimeter
         * @param delimeter: the delimeter value
         * @return: the String array
         */
        public static String[] convertStringArray(String value,String delimeter){
          String[] valueList = null;
          try {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Convert String Array Method Value is -->"+value+"<--- Delimeter -->"+delimeter);
            Vector valueVec = new Vector();
            //Tokenizing the string to create list
            StringTokenizer stk = new StringTokenizer(value,delimeter);
            while(stk.hasMoreTokens())
              valueVec.addElement(stk.nextToken());

            //Add the values in Array
            if (valueVec.size() > 0) {
              valueList = new String[valueVec.size()];
              valueVec.toArray(valueList);
            }//end if

          } catch (Exception ex) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Exception in convertStringArray method -->"+ex);
          }//end catch
          return valueList;
        }//end method

        public static String getStackTrace(Throwable th)
        {
          final Writer trace = new StringWriter();
          final PrintWriter pw = new PrintWriter(trace);
          th.printStackTrace(pw);
          return trace.toString();
        }

        public static String maskInformation(String info, String cardPrgName, String cardPrgID)
        {
          StringBuffer sb = new StringBuffer(info);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," CommonUtilities --- maskInformation --- Info received -->"+info + "<--- Card Program Name--->" + cardPrgName + "<--- Card Program ID--->" + cardPrgID);
          if(info.length() > 0 && info.length() <= 4){
            sb.replace(0,info.length(),"XXXX");
          }
          else if(info.length() > 4){
            if(cardPrgName != null && cardPrgName.trim().length() > 0 && cardPrgID != null && cardPrgID.trim().length() > 0){
              sb.replace(0,info.length()-4,cardPrgName+ "[" + cardPrgID + "]" + "--");
            }
            else if(cardPrgName == null && cardPrgID != null && cardPrgID.trim().length() > 0){
              sb.replace(0,info.length()-4,"[" + cardPrgID + "]" + "--");
            }
            else
            {
              sb.replace(0,info.length()-4,"*");
            }
          }
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," CommonUtilities --- maskInformation --- Info sent -->"+sb.toString());
          return sb.toString();
        }

}//end common utility class
