
package com.i2c.component.util;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.http.*;

/**
 * Title:        Translation Gateway
 * Description:  This is a generic utility class common to all brokers
 * Copyright:    Copyright (c) 2002
 * Company:      I2c Inc.
 * @author
 * @version 1.0
 */

public class ComponentsUtil
{
    public static String NEWLINE_STR = "\n";

    static {
        try {
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            bw.newLine();
            bw.flush();
            NEWLINE_STR = sw.toString();
            bw.close();
            sw.close();
        }
        catch (Exception e) {
        }
    }

    public static String replaceStr(String src, String oldStr, String newStr)
    {
        int last, pos, len;
        StringBuffer buffer;
        buffer = new StringBuffer();
        last = 0;
        len = oldStr.length();
        pos = src.indexOf(oldStr);
        while (pos >= 0) {
            buffer.append(src.substring(last, pos));
            buffer.append(newStr);
            last = pos + len;
            pos = src.indexOf(oldStr, last);
        }
        buffer.append(src.substring(last));
        return buffer.toString();
    } //replaceStr

    public static String removeTags(String src)
    {
        StringBuffer buffer;
        int last, pos, len;
        char c;
        boolean isTag;

        isTag = false;
        last = 0;
        len = src.length();
        buffer = new StringBuffer();
        for (int i = 0; i < len; i++) {
            c = src.charAt(i);
            if (c == '>') {
                isTag = false;
                last = i + 1;
            }
            else if (c == '<' && !isTag) {
                isTag = true;
                buffer.append(src.substring(last, i));
            }
        }
        if (!isTag) {
            buffer.append(src.substring(last));
        }
        return buffer.toString();
    } //removeTags

    //shrink a string so only one space in between each word
    public static String shrinkStr(String orig)
    {
        char [] string;
        char c;
        int cursor;
        boolean isWhiteSpace;

        cursor = 0;
        isWhiteSpace = false;
        string = orig.toCharArray();
        for (int i = 0; i < string.length; i++) {
            c = string[i];
            if (isWhiteSpace) {
                if (c > ' ' && c <= '}') {
                    isWhiteSpace = false;
                    string[cursor++] = c;
                }
            }
            else if (string[i] <= ' ') {
                isWhiteSpace = true;
                string[cursor++] = ' ';
            }
            else if (string[i] <= '}') {
                string[cursor++] = c;
            }
        }
        return new String(string, 0, cursor).trim();
    } //shrinkStr

    //precision - number of decimal places to round up (can be negative number)
    //return -1 if parsing error occurs
    public static String round(String numStr, int precision)
    {
        double number, round;
        try {
            number = Double.parseDouble(numStr);
            round = Math.pow(10.0, (double)precision);
            number = Math.round(number * round) / round;
            return Double.toString(number);
        }
        catch (Exception e) {
            return "-1";
        }
    } //round

    //precision - number of decimal places to round up (can be negative number)
    public static String round(double number, int precision)
    {
        double round;
        round = Math.pow(10.0, (double)precision);
        number = Math.round(number * round) / round;
        return Double.toString(number);
    } //round

    public static void write(String fileName, String data)
    {
        File file = null;
        FileWriter fw = null;
        PrintWriter pw = null;

        try {
            file = new File(fileName);
            if (file.exists() && file.isDirectory()) {
                System.out.println("Unable to write to file <" + fileName
                                   + ">");
                return;
            }
            fw = new FileWriter(file);
            pw = new PrintWriter(fw);
            pw.print(data);
        }
        catch (IOException ioE) {
            ioE.printStackTrace();
        }
        finally {
            try {
                pw.close();
            }
            catch (Exception e) { }
            try {
                fw.close();
            }
            catch (Exception e) { }
        }
    } //write
	/**
		 * used to get the hashtable of request parameters
		 * @author abakar
		 * @param request
		 * @return
		 */
		public static Hashtable getRequestParametersTable(HttpServletRequest request) {

			Enumeration enumeration = request.getParameterNames();
			String parameter = null;
			String parameterValue = null;
			Hashtable table = new Hashtable();

			while(enumeration.hasMoreElements()) {
				parameter = (String) enumeration.nextElement();
				parameterValue = request.getParameter(parameter);
				table.put(parameter,parameterValue);
			}

			return table;
		}//end method


	public static boolean isCardNumber(String colName, String value, String defaultValueContainerName){
	boolean ret = false;

	if(value !=null){
		value = value.trim();
		value = removeSpaces(value);
	}

	if(
	colName != null && !defaultValueContainerName.trim().equalsIgnoreCase("cardsassignment")
	&& value.trim().length() >= 16 && value.length() <= 19 &&
	(
	colName.trim().indexOf("cardNo") != -1 ||
	colName.trim().equalsIgnoreCase("i002pan")
	)
	){
	 ret = true;
	}

	return ret;
	}

	public static String removeSpaces(String str){
	    //str = str+ " ";
		StringTokenizer tz = new StringTokenizer(str, " ");
		String temp = "";
			while(tz.hasMoreTokens()){
			temp+=tz.nextToken();
			}
	return temp;
	}

	public static String maskCardNumber(String cardNumber){

		String temp = cardNumber;
		temp =  temp.trim();
		temp = removeSpaces(temp);
		String firstSix = temp.substring(0,6);
		String lastFour = temp.substring(temp.length()-4,temp.length());
		int charsMissed = temp.length()-10;
		temp = firstSix;
		for(int count=0; count<charsMissed;count++){
			temp = temp+"*";
		}
		temp = temp+ lastFour;
		cardNumber = temp;
		return formatCardNumber(cardNumber);
		//return cardNumber;
	}

	public static String formatCardNumber(String str){
		StringBuffer sbuf = new StringBuffer();
		int index = 0;
		for(int i=4;i<str.length();i+=4){
			sbuf.append(str.substring(index,i));
			sbuf.append(" ");
			index = i;
		}
		if(index <= str.length())
			sbuf.append(str.substring(index));
		return sbuf.toString();
	}

	/**
	 * rightTrim
	 * @param value
	 * @return
	 */
	public static String rightTrim(String value) {
		StringBuffer val = null;
		String temp = null;
		try{

		if(value != null){
		temp = value;
		temp = temp.trim();
		}

		if(temp!= null && temp.length()>1 ){
			val = new StringBuffer();
			int i = 0;
			while(value.charAt(i++) == ' '){
				val.append(" ");
			}
			val.append(value.trim());
		}

		// Setting Value After Logic
		if(val != null){
			value = val.toString();
		}else{
			value = temp;
		}

		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * Returns Table Name from Query
	 * @param qry
	 * @return
	 */
	public static String getTableNameFromQuery(String qry) {

		StringTokenizer tz = new StringTokenizer(qry, " ");
		String tableName = null;
		boolean dec = false;
		while(tz.hasMoreTokens()){
			tableName = tz.nextToken();
			if(tableName.trim().length() < 1)
			continue;
			if(dec)
			break;
			if(tableName.trim().equalsIgnoreCase("FROM")){
				dec =true;
			}
		}
		return tableName;
	}

	public static void releaseResources(ResultSet rs, Statement stmt) {
		try {
			if (rs!=null){
				rs.close();
			}
		}catch (Exception e) {}	
		try {
			if (stmt != null){
				stmt.close();
			}
		}catch (Exception e) {}
	}
	
	public static void releaseResources(Statement stmt) {
		try {
			if (stmt != null){
				stmt.close();
			}
		} catch(Exception e) {}	
	}
	
	public static boolean applyDoublePassword(Connection con) throws Exception{
		boolean applyDoublepassword = false;
		Statement stm = null;
		ResultSet rs = null;
		String query = null;
		try {
			query =
				"select count(locking_password) as lockingPassword from locking_passwords";
			stm = con.createStatement();
			rs = stm.executeQuery(query);
			if (rs.next()) {
				if(Integer.parseInt(rs.getString("lockingPassword"))>0)
						applyDoublepassword = true;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			releaseResources(rs,stm);				
		}
		return applyDoublepassword;
	}//End Method
	/**
	* @author sbukhari
	* this method is used to check whether a string is null or empty
	* @param string
	* @return
	*/
	public static boolean isNullOrEmptyString(String string)
	{
		if (null == string || "".equals(string))
			return true;
		else
			return false;
	} //end method
	/**
	 * @author sbukhari
	 * @param string
	 * @return
	 */
	public static String fixNull(String string)
	{
		return null == string ? "" : string;
	} //end method
} //
