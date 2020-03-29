package com.i2c.campaigngenservice.util;
///*
// * Created on Nov 30, 2003
// *
// * To change the template for this generated file go to
// * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
// */
////package com.i2c.util;
//
//
//import java.util.*;
//import java.util.regex.*;
//
//
//
///**
//	* @author iahmad
//	*
//	* QBE - Operators
//	* Less Than (<) means 20 Displays all records having values less than 20
//	* Greater Than (>) means > 20 Displays all records having values Greater than 20
//	* Not Equal To (<>) means <>20 Displays all the records having value not equal to 20
//	* Range (..)  means 1..10  Displays all the records having value between 1 & 10
//	* Multiple Range (||)  1..5|10|15..20 records within range either 1 to 5 or 10 or range either 15 to 20.
//	* ABC* = Display records with the specified field values starting with ABC.
//	* A*B  = Display records with the specified field values starting with A and ending with B.
//	* A*C* = Display records with the specified field values starting with A and having a C some where in their value.
//	* A?C  = Display records with the specified field value starting with A and ending with C, having only one character in the center.
//
//	For example this criterion on a single field is allowed. This functionality is not available in SMS developed in new ERA.
//
//	A|ABCDZ*|B|C|D??|1..3|>=90|!ABC*|!=
//	Explanation
//	1. value matches  ABCDZ%
//	2. OR value is starts with “D” and there are any other two characters of total length 3.
//	3. OR value between 1 to 3
//	4. or value greater equal to 90
//	5. and value not matches ABC%
//	6. and value is not null
//	7. or value in (A,B,C)
//*/
//
//public class WildCardQuery {
//	/**
//	 *
//	 */
//	public WildCardQuery() {
//		// TODO Auto-generated constructor stub
//	}
//
//	public static String getSQL(Object col1, Object data1) {
//
//		boolean isChar = false;
//		String data = (String) data1;
//		String col = (String) col1;
//		String tempData = null;
//		if (data == null) {
//			return data;
//		}
//		//System.out.println(" Data : "+data);
//		//data = data.replaceAll(" ", "");
//		if (data.trim().equals("")) {
//			return col + "=" + data;
//		}
//		/*
//				if (data.indexOf("'") != -1) {
//					StringTokenizer tok1 = new StringTokenizer(data, "'");
//					if(tok1.countTokens()==2){
//						data = data.replaceAll("'", "");
//						isChar = true;
//					}else{
//						System.out.println("INVALID QUERY : TOKENS = "+(tok1.countTokens()) +" "+ (col + "=" + data));
//						return  col + "=" + data;
//					}
//				}else if(data.indexOf("\"") != -1) {
//					return  col + "=" + data;
//				}
//
//*/
//		tempData = data.toLowerCase();
//		if (data.indexOf("'") != -1) {
//			data = data.replaceAll("'", "");
//			isChar = true;
//		} else
//		 if (data.indexOf("\"") != -1) {
//			return col + "=" + data;
//		}
//		StringTokenizer token = new StringTokenizer(data, "|");
//		//System.out.println("TOKENS :" + token.countTokens());
//		if (token.countTokens() > 1) {
//			System.out.println(data);
//			data = multipleCRT(col, data, isChar);
//			return data;
//		}
//		if (data.indexOf("*") != -1) {
//			System.out.println(data);
//			data = " LIKE \'" + (replaceStr(data, "*", "%")) + "\'";
//			//ESCAPE \\\"";
//
//		} else if (data.indexOf("?") != -1) {
//			data = " LIKE \'" + (replaceStr(data, "?", "_")) + "\'";
//			// ESCAPE \\\"";
//
//		} else if (data.length() == 1 && data.startsWith("=")) {
//			data = " IS NULL ";
//
//		} else if (data.length() > 1 && data.startsWith("=")) {
//			data = data.replaceAll("=", "");
//			if (isChar) {
//				data = " = '" + data + "' ";
//			} else {
//				data = " = \"" + data+"\"";
//			}
//			return col + data;
//		} else if (data.length() == 2 && data.startsWith("!=")) {
//			data = " IS NOT NULL ";
//
//		} else if (data.length() > 2 && data.startsWith("!=")) {
//			data = data.replaceAll("!=", "");
//			if (isChar) {
//				data = " <> '" + data + "' ";
//			} else {
//				data = " <> \"" + data+"\"";
//			}
//
//		} else if (data.length() == 2 && data.startsWith("<>")) {
//			data = " IS NOT NULL ";
//
//		} else if (data.length() > 2 && data.startsWith("<>")) {
//			data = data.replaceAll("<>", "");
//			if (isChar) {
//				data = " <> '" + data + "' ";
//			} else {
//				data = " <> \"" + data + "\"";
//			}
//
//		} else if (data.indexOf("|") != -1) {
//			if (isChar) {
//				data = " IN ('" + (replaceStr(data, "|", "','")) + "')";
//			} else {
//				data = " IN ( " + (replaceStr(data, "|", ",")) + " )";
//			}
//
//		} else if (data.startsWith(">=")) {
//			data = data.replaceAll(">=", "");
//			if (isChar) {
//				data = " >= '" + data + "' ";
//			} else {
//				data = " >= " + data;
//			}
//
//		} else if (data.startsWith("<=")) {
//			data = data.replaceAll("<=", "");
//			if (isChar) {
//				data = " <= '" + data + "' ";
//			} else {
//				data = " <= " + data;
//			}
//
//		} else if (data.startsWith(">")) {
//			data = data.replaceAll(">", "");
//			if (isChar) {
//				data = " >'" + data + "' ";
//			} else {
//				data = " > " + data;
//			}
//
//		} else if (data.startsWith("<")) {
//			data = data.replaceAll("<", "");
//			if (isChar) {
//				data = " <'" + data + "' ";
//			} else {
//				data = " < " + data;
//			}
//		} else if (data.indexOf("..") != -1) {
//			if (isChar) {
//				data = " BETWEEN '" + (replaceStr(data, "..", "' AND '")) + "' ";
//			} else {
//				data = " BETWEEN \"" + (replaceStr(data, "..", "\" AND \"")+"\"");
//			}
//
//		} else if (tempData.indexOf("today") != -1) {
//			System.out.println("Today Working start");
//			tempData = trimData(tempData);
//			if(tempData.equals("\"today\"")||tempData.equals("'today'")){
//				data = "=" + tempData;
//			}else{
//				boolean isMatch = isTodayCheck(tempData);
//				if(isMatch){
//					tempData = tempData.replaceAll("\"","");
//					tempData = tempData.replaceAll("\'","");
//				}
//				data = "=" + tempData;
//			}
//			System.out.println("Today Working End");
//		}else {
//			if (isChar) {
//				return col + " = '" + data + "'";
//			} else {
//				return col + " = \"" + data+"\"";
//			}
//		}
//		if (data.indexOf("!") != -1) {
//			data = replaceStr(data, "!", "");
//			return col + " NOT " + data;
//		} else {
//			return col + data;
//		}
//	}
//	public static String getSQLInet(Object col1, Object data1) {
//
//		boolean isChar = false;
//		String data = (String) data1;
//		String col = (String) col1;
//		if (data == null) {
//			return data;
//		}
//		//System.out.println(" Data : "+data);
//		//data = data.replaceAll(" ", "");
//		if (data.trim().equals("")) {
//			return col + "=" + data;
//		}
//		/*
//				if (data.indexOf("'") != -1) {
//					StringTokenizer tok1 = new StringTokenizer(data, "'");
//					if(tok1.countTokens()==2){
//						data = data.replaceAll("'", "");
//						isChar = true;
//					}else{
//						System.out.println("INVALID QUERY : TOKENS = "+(tok1.countTokens()) +" "+ (col + "=" + data));
//						return  col + "=" + data;
//					}
//				}else if(data.indexOf("\"") != -1) {
//					return  col + "=" + data;
//				}
//		*/
//
//		if (data.indexOf("'") != -1) {
//			data = data.replaceAll("'", "");
//			isChar = true;
//		} else if (data.indexOf("\"") != -1) {
//			return col + "=" + data;
//		}
//		StringTokenizer token = new StringTokenizer(data, "|");
//		//System.out.println("TOKENS :" + token.countTokens());
//		if (token.countTokens() > 1) {
//			System.out.println(data);
//			data = multipleCRTInet(col, data, isChar);
//			return data;
//		}
//		if (data.indexOf("*") != -1) {
//			System.out.println("Replacing * with %"+data);
//			data = (replaceStr(data, "*", "*"));
//			data = (replaceStr(data, "?", "_"));
//			data = (replaceStr(data, "=", ""));
//			data = " LIKE \"" + data + "\"";
//		}/*else if (data.indexOf("?") != -1) {
//			data = " LIKE \'" + data + "\'";
//			//data = " LIKE \"" + (replaceStr(data, "?", "_")) + "\"";
//		}*/ else if (data.length() == 1 && data.startsWith("=")) {
//			 return " isnull ("+col+")"; //data = " IS NULL ";
//
//		} else if (data.length() > 1 && data.startsWith("=")) {
//			data = data.replaceAll("=", "");
//			if (isChar) {
//				data = " = '" + data + "' ";
//			} else {
//				data = " = " + data;
//			}
//			return col + data;
//		} else if (data.length() == 2 && data.startsWith("!=")) {
//			return " not isnull ("+col+")"; //data = " IS NOT NULL ";
//
//		} else if (data.length() > 2 && data.startsWith("!=")) {
//			data = data.replaceAll("!=", "");
//			if (isChar) {
//				data = " <> '" + data + "' ";
//			} else {
//				data = " <> " + data;
//			}
//
//		} else if (data.length() == 2 && data.startsWith("<>")) {
//			return " not isnull ("+col+")"; //data = " IS NOT NULL ";
//
//		} else if (data.length() > 2 && data.startsWith("<>")) {
//			data = data.replaceAll("<>", "");
//			if (isChar) {
//				data = " <> '" + data + "' ";
//			} else {
//				data = " <> " + data;
//			}
//
//		} else if (data.indexOf("|") != -1) {
//			if (isChar) {
//				data = " IN ('" + (replaceStr(data, "|", "','")) + "')";
//			} else {
//				data = " IN ( " + (replaceStr(data, "|", ",")) + " )";
//			}
//
//		} else if (data.startsWith(">=")) {
//			data = data.replaceAll(">=", "");
//			if (isChar) {
//				data = " >= '" + data + "' ";
//			} else {
//				data = " >= " + data;
//			}
//
//		} else if (data.startsWith("<=")) {
//			data = data.replaceAll("<=", "");
//			if (isChar) {
//				data = " <= '" + data + "' ";
//			} else {
//				data = " <= " + data;
//			}
//
//		} else if (data.startsWith(">")) {
//			data = data.replaceAll(">", "");
//			if (isChar) {
//				data = " >'" + data + "' ";
//			} else {
//				data = " > " + data;
//			}
//
//		} else if (data.startsWith("<")) {
//			data = data.replaceAll("<", "");
//			if (isChar) {
//				data = " <'" + data + "' ";
//			} else {
//				data = " < " + data;
//			}
//		} else if (data.indexOf("..") != -1) {
//			if (isChar) {
//				data =
//					" IN '" + (replaceStr(data, "..", "' TO '")) + "' ";
//			} else {
//				data = " IN " + (replaceStr(data, "..", " TO "));
//			}
//
//		} else {
//			if (isChar) {
//				return col + " = '" + data + "'";
//			} else {
//				return col + " = " + data;
//			}
//		}
//		if (data.indexOf("!") != -1) {
//			data = replaceStr(data, "!", "");
//			return " NOT ( " + col + data + " )";
//		} else {
//			return col + data;
//		}
//	}
//
//	//shrink a string so only one space in between each word
//	private static String shrinkStr(String orig) {
//		char[] string;
//		char c;
//		int cursor;
//		boolean isWhiteSpace;
//
//		cursor = 0;
//		isWhiteSpace = false;
//		string = orig.toCharArray();
//		for (int i = 0; i < string.length; i++) {
//			c = string[i];
//			if (isWhiteSpace) {
//				if (c > ' ' && c <= '}') {
//					isWhiteSpace = false;
//					string[cursor++] = c;
//				}
//			} else if (string[i] <= ' ') {
//				isWhiteSpace = true;
//				string[cursor++] = ' ';
//			} else if (string[i] <= '}') {
//				string[cursor++] = c;
//			}
//		}
//
//		return new String(string, 0, cursor).trim();
//	} //shrinkStr
//	public static String replaceStr(String src, String oldStr, String newStr) {
//		int last, pos, len;
//		StringBuffer buffer;
//		buffer = new StringBuffer();
//		last = 0;
//		len = oldStr.length();
//		pos = src.indexOf(oldStr);
//		while (pos >= 0) {
//			buffer.append(src.substring(last, pos));
//			buffer.append(newStr);
//			last = pos + len;
//			pos = src.indexOf(oldStr, last);
//		}
//		buffer.append(src.substring(last));
//		return buffer.toString();
//	} //replaceStr
//	private static String multipleCRT(
//		String col,
//		String data,
//		boolean isChar) {
//		StringBuffer sql = new StringBuffer(" ");
//		StringBuffer inSql = new StringBuffer(" ");
//		String notINClause1 = " ";
//		String INClause2 = " ";
//		boolean inClause = false;
//		boolean notIN = false;
//		boolean btw = false;
//		boolean wchr = false, wchr2 = false;
//
//		int totTokens = 0;
//		if (data.indexOf("|") != -1) {
//			StringTokenizer token = new StringTokenizer(data, "|");
//			String value = null;
//			int counter = 0;
//			int countIN = 0;
//			totTokens = token.countTokens();
//			while (token.hasMoreTokens()) {
//				value = token.nextToken();
//				System.out.println("Counter: " + counter);
//				if (value.indexOf("..") != -1) {
//					btw = true;
//					if (counter > 0) {
//						if (value.indexOf("!") != -1) {
//							sql.append(" AND " + col);
//							sql.append(" NOT ");
//							value = replaceStr(value, "!", "");
//						} else {
//							sql.append(" OR " + col);
//						}
//					} else {
//						sql.append(" " + col);
//					}
//					value = "'"+value+"'";
//					value = replaceStr(value, "..", "' AND '");
//					if (value.indexOf("!") != -1) {
//						sql.append(" NOT ");
//						value = replaceStr(value, "!", "");
//					}
//					sql.append(" BETWEEN ");
//					if (isChar) {
//						sql.append(" '" + value + "' ");
//					} else {
//						sql.append(value);
//					}
//				} else if (
//					value.indexOf("<=") != -1
//						|| value.indexOf(">=") != -1
//						|| value.indexOf(">") != -1
//						|| value.indexOf("<") != -1
//						|| value.indexOf("?") != -1
//						|| value.indexOf("*") != -1
//						|| value.indexOf("=") != -1) {
//					wchr = true;
//					if (counter > 0 && value.startsWith("!"))
//						sql.append(" AND ");
//					else if (counter > 0)
//						sql.append(" OR ");
//					sql.append(getSQL2(col, value, isChar));
//
//				} else if (
//					value.indexOf("<>") != -1 || value.indexOf("!=") != -1) {
//					wchr2 = true;
//					if (counter > 0)
//						sql.append(" AND ");
//					sql.append(getSQL2(col, value, isChar));
//				} else {
//					counter--;
//					countIN++;
//					if (value.startsWith("!")) {
//						notIN = true;
//						value = replaceStr(value, "!", "");
//						if (isChar) {
//							notINClause1 = " '" + value + "' ";
//						} else {
//							notINClause1 = value;
//						}
//						continue;
//
//					}
//					if (!inClause) {
//						INClause2 = col;
//						inSql.append(" IN ( ");
//						inClause = true;
//					}
//					if (isChar) {
//						inSql.append(" '" + value + "' ");
//					} else {
//						inSql.append(" \"" + value + "\" ");
//					}
//					inSql.append(",");
//				}
//				counter++;
//			}
//
//			if (inClause) {
//				if (wchr || btw || wchr2) {
//					INClause2 = " OR " + INClause2;
//				}
//				if (countIN == totTokens && notIN) {
//					inSql.append(notINClause1);
//					INClause2 = INClause2 + " NOT " + inSql.toString();
//					inSql = new StringBuffer(INClause2);
//				} else {
//					inSql.setLength(inSql.length() - 1);
//					INClause2 = INClause2 + inSql.toString();
//					inSql = new StringBuffer(INClause2);
//				}
//				inSql.append(" ) ");
//			}
//		}
//		return sql.toString() + " " + inSql.toString();
//	}
//	// prepares clause having multiple values
//	private static String multipleCRTInet(
//		String col,
//		String data,
//		boolean isChar) {
//		StringBuffer sql = new StringBuffer(" ");
//		StringBuffer inSql = new StringBuffer(" ");
//		String notINClause1 = " ";
//		String INClause2 = " ";
//		boolean inClause = false;
//		boolean notIN = false;
//		boolean btw = false;
//		boolean wchr = false, wchr2 = false;
//
//		int totTokens = 0;
//		if (data.indexOf("|") != -1) {
//			StringTokenizer token = new StringTokenizer(data, "|");
//			String value = null;
//			int counter = 0;
//			int countIN = 0;
//			totTokens = token.countTokens();
//			while (token.hasMoreTokens()) {
//				value = token.nextToken();
//				System.out.println("Counter: " + counter);
//				if (value.indexOf("..") != -1) {
//					btw = true;
//					if (counter > 0) {
//						if (value.indexOf("!") != -1) {
//							sql.append(" AND " + col);
//							sql.append(" NOT ");
//							value = replaceStr(value, "!", "");
//						} else {
//							sql.append(" OR " + col);
//						}
//					} else {
//						sql.append(" " + col);
//					}
//
//					value = replaceStr(value, "..", "' AND '");
//					if (value.indexOf("!") != -1) {
//						sql.append(" NOT ");
//						value = replaceStr(value, "!", "");
//					}
//					sql.append(" BETWEEN ");
//					if (isChar) {
//						sql.append(" '" + value + "' ");
//					} else {
//						sql.append(value);
//					}
//				} else if (
//					value.indexOf("<=") != -1
//						|| value.indexOf(">=") != -1
//						|| value.indexOf(">") != -1
//						|| value.indexOf("<") != -1
//						|| value.indexOf("?") != -1
//						|| value.indexOf("*") != -1
//						|| value.indexOf("=") != -1) {
//					wchr = true;
//					if (counter > 0 && value.startsWith("!"))
//						sql.append(" AND ");
//					else if (counter > 0)
//						sql.append(" OR ");
//					sql.append(getSQL2(col, value, isChar));
//
//				} else if (
//					value.indexOf("<>") != -1 || value.indexOf("!=") != -1) {
//					wchr2 = true;
//					if (counter > 0)
//						sql.append(" AND ");
//					sql.append(getSQL2(col, value, isChar));
//				} else {
//					counter--;
//					countIN++;
//					if (value.startsWith("!")) {
//						notIN = true;
//						value = replaceStr(value, "!", "");
//						if (isChar) {
//							notINClause1 = " '" + value + "' ";
//						} else {
//							notINClause1 = value;
//						}
//						continue;
//
//					}
//					if (!inClause) {
//						INClause2 = col;
//						inSql.append(" = [ ");
//						inClause = true;
//					}
//					if (isChar) {
//						inSql.append(" '" + value + "' ");
//					} else {
//						inSql.append(value);
//					}
//					inSql.append(",");
//				}
//				counter++;
//			}
//
//			if (inClause) {
//				if (wchr || btw || wchr2) {
//					INClause2 = " OR " + INClause2;
//				}
//				if (countIN == totTokens && notIN) {
//					inSql.append(notINClause1);
//					INClause2 = INClause2 + " NOT " + inSql.toString();
//					inSql = new StringBuffer(INClause2);
//				} else {
//					inSql.setLength(inSql.length() - 1);
//					INClause2 = INClause2 + inSql.toString();
//					inSql = new StringBuffer(INClause2);
//				}
//				inSql.append(" ] ");
//			}
//		}
//		return sql.toString() + " " + inSql.toString();
//	}
//
//	public static String getSQL2(Object col1, Object data1, boolean isChar) {
//
//		String data = (String) data1;
//		String col = (String) col1;
//		if (data == null) {
//			return data;
//		}
//		if (data.indexOf("*") != -1) {
//			data = " LIKE \'" + (replaceStr(data, "*", "%")) + "\'";
//			//ESCAPE \\\"";
//		} else if (data.indexOf("?") != -1) {
//			data = " LIKE \'" + (replaceStr(data, "?", "_")) + "\'";
//			// ESCAPE \\\"";
//		} else if (data.length() == 1 && data.startsWith("=")) {
//			data = " IS NULL ";
//		} else if (data.length() > 1 && data.startsWith("=")) {
//			data = data.replaceAll("=", "");
//			if (isChar) {
//				data = " = '" + data + "' ";
//			} else {
//				data = " = \"" + data+"\"";
//			}
//			return col + data;
//		} else if (data.length() == 2 && data.startsWith("!=")) {
//			data = " IS NOT NULL ";
//
//		} else if (data.length() > 2 && data.startsWith("!=")) {
//			data = data.replaceAll("!=", "");
//			if (isChar) {
//				data = " <> '" + data + "' ";
//			} else {
//				data = " <> \"" + data+"\"";
//			}
//
//		} else if (data.length() == 2 && data.startsWith("<>")) {
//			data = " IS NOT NULL ";
//
//		} else if (data.length() > 2 && data.startsWith("<>")) {
//			data = data.replaceAll("<>", "");
//			if (isChar) {
//				data = " <> '" + data + "' ";
//			} else {
//				data = " <> \"" + data +"\"";
//			}
//
//		} else if (data.startsWith(">=")) {
//			data = data.replaceAll(">=", "");
//			if (isChar) {
//				data = " >= '" + data + "' ";
//			} else {
//				data = " >= " + data;
//			}
//
//		} else if (data.startsWith("<=")) {
//			data = data.replaceAll("<=", "");
//			if (isChar) {
//				data = " <= '" + data + "' ";
//			} else {
//				data = " <= " + data;
//			}
//
//		} else if (data.startsWith(">")) {
//			data = data.replaceAll(">", "");
//			if (isChar) {
//				data = " >'" + data + "' ";
//			} else {
//				data = " > " + data;
//			}
//
//		} else if (data.startsWith("<")) {
//			data = data.replaceAll("<", "");
//			if (isChar) {
//				data = " <'" + data + "' ";
//			} else {
//				data = " < " + data;
//			}
//		} else {
//			if (isChar) {
//				return col + " = '" + data + "'";
//			} else {
//				return col + " = \"" + data+"\"";
//			}
//		}
//		if (data.indexOf("!") != -1) {
//			data = replaceStr(data, "!", "");
//			return col + " NOT " + data;
//		} else {
//			return col + data;
//		}
//	}
//	/*
//		Saqib Bukhari -- tuesday, 28 th Feb. 2006
//	*/
//	public static String trimData(String data){
//		data = data.trim();
//		data = data.replaceAll(" ","");
//		return data;
//	}
//
//	public static boolean isTodayCheck(String data){
//		boolean isMatch = false;
//		data = data.replaceAll("\"","");
//		data = data.replaceAll("'","");
//		Pattern p = Pattern.compile("(?i)today(?-i)[+|-]?[0-9]*?");
//		Matcher m = p.matcher(data);
//		isMatch = m.matches();
//		System.out.println(isMatch);
//		return isMatch;
//	}
//	/*
//		Saqib Bukhari -- tuesday, 28 th Feb. 2006
//	*/
//}
  /*
   * Created on Nov 30, 2003
   *
   * To change the template for this generated file go to
   * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
   */

import java.util.*;
import java.util.regex.*;



  /**
          * @author iahmad
          *
          * QBE - Operators
          * Less Than (<) means 20 Displays all records having values less than 20
          * Greater Than (>) means > 20 Displays all records having values Greater than 20
          * Not Equal To (<>) means <>20 Displays all the records having value not equal to 20
          * Range (..)  means 1..10  Displays all the records having value between 1 & 10
          * Multiple Range (||)  1..5|10|15..20 records within range either 1 to 5 or 10 or range either 15 to 20.
          * ABC* = Display records with the specified field values starting with ABC.
          * A*B  = Display records with the specified field values starting with A and ending with B.
          * A*C* = Display records with the specified field values starting with A and having a C some where in their value.
          * A?C  = Display records with the specified field value starting with A and ending with C, having only one character in the center.

          For example this criterion on a single field is allowed. This functionality is not available in SMS developed in new ERA.

          A|ABCDZ*|B|C|D??|1..3|>=90|!ABC*|!=
          Explanation
          1. value matches  ABCDZ%
          2. OR value is starts with “D” and there are any other two characters of total length 3.
          3. OR value between 1 to 3
          4. or value greater equal to 90
          5. and value not matches ABC%
          6. and value is not null
          7. or value in (A,B,C)
  */

  public class WildCardQuery {
          /**
           *
           */
          public WildCardQuery() {
                  // TODO Auto-generated constructor stub
          }

          public static String getSQL(Object col1, Object data1) {

                  boolean isChar = false;
                  String data = (String) data1;
                  String col = (String) col1;
                  String tempData = null;
                  if (data == null) {
                          return data;
                  }
                  //System.out.println(" Data : "+data);
                  //data = data.replaceAll(" ", "");
                  if (data.trim().equals("")) {
                          return col + "=" + data;
                  }
                  /*
                                  if (data.indexOf("'") != -1) {
                                          StringTokenizer tok1 = new StringTokenizer(data, "'");
                                          if(tok1.countTokens()==2){
                                                  data = data.replaceAll("'", "");
                                                  isChar = true;
                                          }else{
                                                  System.out.println("INVALID QUERY : TOKENS = "+(tok1.countTokens()) +" "+ (col + "=" + data));
                                                  return  col + "=" + data;
                                          }
                                  }else if(data.indexOf("\"") != -1) {
                                          return  col + "=" + data;
                                  }

  */
                  tempData = data.toLowerCase();
                  if (data.indexOf("'") != -1) {
                          data = data.replaceAll("'", "");
                          isChar = true;
                  } else
                   if (data.indexOf("\"") != -1) {
                          return col + "=" + data;
                  }
                  StringTokenizer token = new StringTokenizer(data, "|");
                  //System.out.println("TOKENS :" + token.countTokens());
                  if (token.countTokens() > 1) {
                          System.out.println(data);
                          data = multipleCRT(col, data, isChar);
                          return data;
                  }
                  if (data.indexOf("*") != -1) {
                          System.out.println(data);
                          data = " LIKE \'" + (replaceStr(data, "*", "%")) + "\'";
                          //ESCAPE \\\"";

                  } else if (data.indexOf("?") != -1) {
                          data = " LIKE \'" + (replaceStr(data, "?", "_")) + "\'";
                          // ESCAPE \\\"";

                  } else if (data.length() == 1 && data.startsWith("=")) {
                          data = " IS NULL ";

                  } else if (data.length() > 1 && data.startsWith("=")) {
                          data = data.replaceAll("=", "");
                          if (isChar) {
                                  data = " = '" + data + "' ";
                          } else {
                                  data = " = '" + data+"' ";
                          }
                          return col + data;
                  } else if (data.length() == 2 && data.startsWith("!=")) {
                          data = " IS NOT NULL ";

                  } else if (data.length() > 2 && data.startsWith("!=")) {
                          data = data.replaceAll("!=", "");
                          if (isChar) {
                                  data = " <> '" + data + "' ";
                          } else {
                                  data = " <> '" + data+"' ";
                          }

                  } else if (data.length() == 2 && data.startsWith("<>")) {
                          data = " IS NOT NULL ";

                  } else if (data.length() > 2 && data.startsWith("<>")) {
                          data = data.replaceAll("<>", "");
                          if (isChar) {
                                  data = " <> '" + data + "' ";
                          } else {
                                  data = " <> '" + data + "' ";
                          }

                  } else if (data.indexOf("|") != -1) {
                          if (isChar) {
                                  data = " IN ('" + (replaceStr(data, "|", "','")) + "')";
                          } else {
                                  data = " IN ( " + (replaceStr(data, "|", ",")) + " )";
                          }

                  } else if (data.startsWith(">=")) {
                          data = data.replaceAll(">=", "");
                          if (isChar) {
                                  data = " >= '" + data + "' ";
                          } else {
                                  data = " >= " + data;
                          }

                  } else if (data.startsWith("<=")) {
                          data = data.replaceAll("<=", "");
                          if (isChar) {
                                  data = " <= '" + data + "' ";
                          } else {
                                  data = " <= " + data;
                          }

                  } else if (data.startsWith(">")) {
                          data = data.replaceAll(">", "");
                          if (isChar) {
                                  data = " >'" + data + "' ";
                          } else {
                                  data = " > " + data;
                          }

                  } else if (data.startsWith("<")) {
                          data = data.replaceAll("<", "");
                          if (isChar) {
                                  data = " <'" + data + "' ";
                          } else {
                                  data = " < " + data;
                          }
                  } else if (data.indexOf("..") != -1) {
                          if (isChar) {
                                  data = " BETWEEN '" + (replaceStr(data, "..", "' AND '")) + "' ";
                          } else {
                                  data = " BETWEEN '" + (replaceStr(data, "..", "' AND '")+"' ");
                          }

                  } else if (tempData.indexOf("today") != -1) {
                          System.out.println("Today Working start");
                          tempData = trimData(tempData);
                          if(tempData.equals("\"today\"")||tempData.equals("'today'")){
                                  data = "=" + tempData;
                          }else{
                                  boolean isMatch = isTodayCheck(tempData);
                                  if(isMatch){
                                          tempData = tempData.replaceAll("\"","");
                                          tempData = tempData.replaceAll("\'","");
                                  }
                                  data = "=" + tempData;
                          }
                          System.out.println("Today Working End");
                  }else {
                          if (isChar) {
                                  return col + " = '" + data + "'";
                          } else {
                                  return col + " = '" + data+"'";
                          }
                  }
                  if (data.indexOf("!") != -1) {
                          data = replaceStr(data, "!", "");
                          return col + " NOT " + data;
                  } else {
                          return col + data;
                  }
          }
          public static String getSQLInet(Object col1, Object data1) {

                  boolean isChar = false;
                  String data = (String) data1;
                  String col = (String) col1;
                  if (data == null) {
                          return data;
                  }
                  //System.out.println(" Data : "+data);
                  //data = data.replaceAll(" ", "");
                  if (data.trim().equals("")) {
                          return col + "=" + data;
                  }
                  /*
                                  if (data.indexOf("'") != -1) {
                                          StringTokenizer tok1 = new StringTokenizer(data, "'");
                                          if(tok1.countTokens()==2){
                                                  data = data.replaceAll("'", "");
                                                  isChar = true;
                                          }else{
                                                  System.out.println("INVALID QUERY : TOKENS = "+(tok1.countTokens()) +" "+ (col + "=" + data));
                                                  return  col + "=" + data;
                                          }
                                  }else if(data.indexOf("\"") != -1) {
                                          return  col + "=" + data;
                                  }
                  */

                  if (data.indexOf("'") != -1) {
                          data = data.replaceAll("'", "");
                          isChar = true;
                  } else if (data.indexOf("\"") != -1) {
                          return col + "=" + data;
                  }
                  StringTokenizer token = new StringTokenizer(data, "|");
                  //System.out.println("TOKENS :" + token.countTokens());
                  if (token.countTokens() > 1) {
                          System.out.println(data);
                          data = multipleCRTInet(col, data, isChar);
                          return data;
                  }
                  if (data.indexOf("*") != -1) {
                          System.out.println("Replacing * with %"+data);
                          data = (replaceStr(data, "*", "*"));
                          data = (replaceStr(data, "?", "_"));
                          data = (replaceStr(data, "=", ""));
                          data = " LIKE \"" + data + "\"";
                  }/*else if (data.indexOf("?") != -1) {
                          data = " LIKE \'" + data + "\'";
                          //data = " LIKE \"" + (replaceStr(data, "?", "_")) + "\"";
                  }*/ else if (data.length() == 1 && data.startsWith("=")) {
                           return " isnull ("+col+")"; //data = " IS NULL ";

                  } else if (data.length() > 1 && data.startsWith("=")) {
                          data = data.replaceAll("=", "");
                          if (isChar) {
                                  data = " = '" + data + "' ";
                          } else {
                                  data = " = " + data;
                          }
                          return col + data;
                  } else if (data.length() == 2 && data.startsWith("!=")) {
                          return " not isnull ("+col+")"; //data = " IS NOT NULL ";

                  } else if (data.length() > 2 && data.startsWith("!=")) {
                          data = data.replaceAll("!=", "");
                          if (isChar) {
                                  data = " <> '" + data + "' ";
                          } else {
                                  data = " <> " + data;
                          }

                  } else if (data.length() == 2 && data.startsWith("<>")) {
                          return " not isnull ("+col+")"; //data = " IS NOT NULL ";

                  } else if (data.length() > 2 && data.startsWith("<>")) {
                          data = data.replaceAll("<>", "");
                          if (isChar) {
                                  data = " <> '" + data + "' ";
                          } else {
                                  data = " <> " + data;
                          }

                  } else if (data.indexOf("|") != -1) {
                          if (isChar) {
                                  data = " IN ('" + (replaceStr(data, "|", "','")) + "')";
                          } else {
                                  data = " IN ( " + (replaceStr(data, "|", ",")) + " )";
                          }

                  } else if (data.startsWith(">=")) {
                          data = data.replaceAll(">=", "");
                          if (isChar) {
                                  data = " >= '" + data + "' ";
                          } else {
                                  data = " >= " + data;
                          }

                  } else if (data.startsWith("<=")) {
                          data = data.replaceAll("<=", "");
                          if (isChar) {
                                  data = " <= '" + data + "' ";
                          } else {
                                  data = " <= " + data;
                          }

                  } else if (data.startsWith(">")) {
                          data = data.replaceAll(">", "");
                          if (isChar) {
                                  data = " >'" + data + "' ";
                          } else {
                                  data = " > " + data;
                          }

                  } else if (data.startsWith("<")) {
                          data = data.replaceAll("<", "");
                          if (isChar) {
                                  data = " <'" + data + "' ";
                          } else {
                                  data = " < " + data;
                          }
                  } else if (data.indexOf("..") != -1) {
                          if (isChar) {
                                  data =
                                          " IN '" + (replaceStr(data, "..", "' TO '")) + "' ";
                          } else {
                                  data = " IN " + (replaceStr(data, "..", " TO "));
                          }

                  } else {
                          if (isChar) {
                                  return col + " = '" + data + "'";
                          } else {
                                  return col + " = " + data;
                          }
                  }
                  if (data.indexOf("!") != -1) {
                          data = replaceStr(data, "!", "");
                          return " NOT ( " + col + data + " )";
                  } else {
                          return col + data;
                  }
          }

          //shrink a string so only one space in between each word
          private static String shrinkStr(String orig) {
                  char[] string;
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
                          } else if (string[i] <= ' ') {
                                  isWhiteSpace = true;
                                  string[cursor++] = ' ';
                          } else if (string[i] <= '}') {
                                  string[cursor++] = c;
                          }
                  }

                  return new String(string, 0, cursor).trim();
          } //shrinkStr
          public static String replaceStr(String src, String oldStr, String newStr) {
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
          private static String multipleCRT(
                  String col,
                  String data,
                  boolean isChar) {
                  StringBuffer sql = new StringBuffer(" ");
                  StringBuffer inSql = new StringBuffer(" ");
                  String notINClause1 = " ";
                  String INClause2 = " ";
                  boolean inClause = false;
                  boolean notIN = false;
                  boolean btw = false;
                  boolean wchr = false, wchr2 = false;

                  int totTokens = 0;
                  if (data.indexOf("|") != -1) {
                          StringTokenizer token = new StringTokenizer(data, "|");
                          String value = null;
                          int counter = 0;
                          int countIN = 0;
                          totTokens = token.countTokens();
                          while (token.hasMoreTokens()) {
                                  value = token.nextToken();
                                  System.out.println("Counter: " + counter);
                                  if (value.indexOf("..") != -1) {
                                          btw = true;
                                          if (counter > 0) {
                                                  if (value.indexOf("!") != -1) {
                                                          sql.append(" AND " + col);
                                                          sql.append(" NOT ");
                                                          value = replaceStr(value, "!", "");
                                                  } else {
                                                          sql.append(" OR " + col);
                                                  }
                                          } else {
                                                  sql.append(" " + col);
                                          }
                                          value = "'"+value+"'";
                                          value = replaceStr(value, "..", "' AND '");
                                          if (value.indexOf("!") != -1) {
                                                  sql.append(" NOT ");
                                                  value = replaceStr(value, "!", "");
                                          }
                                          sql.append(" BETWEEN ");
                                          if (isChar) {
                                                  sql.append(" '" + value + "' ");
                                          } else {
                                                  sql.append(value);
                                          }
                                  } else if (
                                          value.indexOf("<=") != -1
                                                  || value.indexOf(">=") != -1
                                                  || value.indexOf(">") != -1
                                                  || value.indexOf("<") != -1
                                                  || value.indexOf("?") != -1
                                                  || value.indexOf("*") != -1
                                                  || value.indexOf("=") != -1) {
                                          wchr = true;
                                          if (counter > 0 && value.startsWith("!"))
                                                  sql.append(" AND ");
                                          else if (counter > 0)
                                                  sql.append(" OR ");
                                          sql.append(getSQL2(col, value, isChar));

                                  } else if (
                                          value.indexOf("<>") != -1 || value.indexOf("!=") != -1) {
                                          wchr2 = true;
                                          if (counter > 0)
                                                  sql.append(" AND ");
                                          sql.append(getSQL2(col, value, isChar));
                                  } else {
                                          counter--;
                                          countIN++;
                                          if (value.startsWith("!")) {
                                                  notIN = true;
                                                  value = replaceStr(value, "!", "");
                                                  if (isChar) {
                                                          notINClause1 = " '" + value + "' ";
                                                  } else {
                                                          notINClause1 = value;
                                                  }
                                                  continue;

                                          }
                                          if (!inClause) {
                                                  INClause2 = col;
                                                  inSql.append(" IN ( ");
                                                  inClause = true;
                                          }
                                          if (isChar) {
                                                  inSql.append(" '" + value + "' ");
                                          } else {
                                                  inSql.append(" '" + value + "' ");
                                          }
                                          inSql.append(",");
                                  }
                                  counter++;
                          }

                          if (inClause) {
                                  if (wchr || btw || wchr2) {
                                          INClause2 = " OR " + INClause2;
                                  }
                                  if (countIN == totTokens && notIN) {
                                          inSql.append(notINClause1);
                                          INClause2 = INClause2 + " NOT " + inSql.toString();
                                          inSql = new StringBuffer(INClause2);
                                  } else {
                                          inSql.setLength(inSql.length() - 1);
                                          INClause2 = INClause2 + inSql.toString();
                                          inSql = new StringBuffer(INClause2);
                                  }
                                  inSql.append(" ) ");
                          }
                  }
                  return sql.toString() + " " + inSql.toString();
          }
          // prepares clause having multiple values
          private static String multipleCRTInet(
                  String col,
                  String data,
                  boolean isChar) {
                  StringBuffer sql = new StringBuffer(" ");
                  StringBuffer inSql = new StringBuffer(" ");
                  String notINClause1 = " ";
                  String INClause2 = " ";
                  boolean inClause = false;
                  boolean notIN = false;
                  boolean btw = false;
                  boolean wchr = false, wchr2 = false;

                  int totTokens = 0;
                  if (data.indexOf("|") != -1) {
                          StringTokenizer token = new StringTokenizer(data, "|");
                          String value = null;
                          int counter = 0;
                          int countIN = 0;
                          totTokens = token.countTokens();
                          while (token.hasMoreTokens()) {
                                  value = token.nextToken();
                                  System.out.println("Counter: " + counter);
                                  if (value.indexOf("..") != -1) {
                                          btw = true;
                                          if (counter > 0) {
                                                  if (value.indexOf("!") != -1) {
                                                          sql.append(" AND " + col);
                                                          sql.append(" NOT ");
                                                          value = replaceStr(value, "!", "");
                                                  } else {
                                                          sql.append(" OR " + col);
                                                  }
                                          } else {
                                                  sql.append(" " + col);
                                          }

                                          value = replaceStr(value, "..", "' AND '");
                                          if (value.indexOf("!") != -1) {
                                                  sql.append(" NOT ");
                                                  value = replaceStr(value, "!", "");
                                          }
                                          sql.append(" BETWEEN ");
                                          if (isChar) {
                                                  sql.append(" '" + value + "' ");
                                          } else {
                                                  sql.append(value);
                                          }
                                  } else if (
                                          value.indexOf("<=") != -1
                                                  || value.indexOf(">=") != -1
                                                  || value.indexOf(">") != -1
                                                  || value.indexOf("<") != -1
                                                  || value.indexOf("?") != -1
                                                  || value.indexOf("*") != -1
                                                  || value.indexOf("=") != -1) {
                                          wchr = true;
                                          if (counter > 0 && value.startsWith("!"))
                                                  sql.append(" AND ");
                                          else if (counter > 0)
                                                  sql.append(" OR ");
                                          sql.append(getSQL2(col, value, isChar));

                                  } else if (
                                          value.indexOf("<>") != -1 || value.indexOf("!=") != -1) {
                                          wchr2 = true;
                                          if (counter > 0)
                                                  sql.append(" AND ");
                                          sql.append(getSQL2(col, value, isChar));
                                  } else {
                                          counter--;
                                          countIN++;
                                          if (value.startsWith("!")) {
                                                  notIN = true;
                                                  value = replaceStr(value, "!", "");
                                                  if (isChar) {
                                                          notINClause1 = " '" + value + "' ";
                                                  } else {
                                                          notINClause1 = value;
                                                  }
                                                  continue;

                                          }
                                          if (!inClause) {
                                                  INClause2 = col;
                                                  inSql.append(" = [ ");
                                                  inClause = true;
                                          }
                                          if (isChar) {
                                                  inSql.append(" '" + value + "' ");
                                          } else {
                                                  inSql.append(value);
                                          }
                                          inSql.append(",");
                                  }
                                  counter++;
                          }

                          if (inClause) {
                                  if (wchr || btw || wchr2) {
                                          INClause2 = " OR " + INClause2;
                                  }
                                  if (countIN == totTokens && notIN) {
                                          inSql.append(notINClause1);
                                          INClause2 = INClause2 + " NOT " + inSql.toString();
                                          inSql = new StringBuffer(INClause2);
                                  } else {
                                          inSql.setLength(inSql.length() - 1);
                                          INClause2 = INClause2 + inSql.toString();
                                          inSql = new StringBuffer(INClause2);
                                  }
                                  inSql.append(" ] ");
                          }
                  }
                  return sql.toString() + " " + inSql.toString();
          }

          public static String getSQL2(Object col1, Object data1, boolean isChar) {

                  String data = (String) data1;
                  String col = (String) col1;
                  if (data == null) {
                          return data;
                  }
                  if (data.indexOf("*") != -1) {
                          data = " LIKE \'" + (replaceStr(data, "*", "%")) + "\'";
                          //ESCAPE \\\"";
                  } else if (data.indexOf("?") != -1) {
                          data = " LIKE \'" + (replaceStr(data, "?", "_")) + "\'";
                          // ESCAPE \\\"";
                  } else if (data.length() == 1 && data.startsWith("=")) {
                          data = " IS NULL ";
                  } else if (data.length() > 1 && data.startsWith("=")) {
                          data = data.replaceAll("=", "");
                          if (isChar) {
                                  data = " = '" + data + "' ";
                          } else {
                                  data = " = '" + data+"' ";
                          }
                          return col + data;
                  } else if (data.length() == 2 && data.startsWith("!=")) {
                          data = " IS NOT NULL ";

                  } else if (data.length() > 2 && data.startsWith("!=")) {
                          data = data.replaceAll("!=", "");
                          if (isChar) {
                                  data = " <> '" + data + "' ";
                          } else {
                                  data = " <> '" + data+"' ";
                          }

                  } else if (data.length() == 2 && data.startsWith("<>")) {
                          data = " IS NOT NULL ";

                  } else if (data.length() > 2 && data.startsWith("<>")) {
                          data = data.replaceAll("<>", "");
                          if (isChar) {
                                  data = " <> '" + data + "' ";
                          } else {
                                  data = " <> '" + data +"' ";
                          }

                  } else if (data.startsWith(">=")) {
                          data = data.replaceAll(">=", "");
                          if (isChar) {
                                  data = " >= '" + data + "' ";
                          } else {
                                  data = " >= " + data;
                          }

                  } else if (data.startsWith("<=")) {
                          data = data.replaceAll("<=", "");
                          if (isChar) {
                                  data = " <= '" + data + "' ";
                          } else {
                                  data = " <= " + data;
                          }

                  } else if (data.startsWith(">")) {
                          data = data.replaceAll(">", "");
                          if (isChar) {
                                  data = " >'" + data + "' ";
                          } else {
                                  data = " > " + data;
                          }

                  } else if (data.startsWith("<")) {
                          data = data.replaceAll("<", "");
                          if (isChar) {
                                  data = " <'" + data + "' ";
                          } else {
                                  data = " < " + data;
                          }
                  } else {
                          if (isChar) {
                                  return col + " = '" + data + "'";
                          } else {
                                  return col + " = '" + data+"'";
                          }
                  }
                  if (data.indexOf("!") != -1) {
                          data = replaceStr(data, "!", "");
                          return col + " NOT " + data;
                  } else {
                          return col + data;
                  }
          }
          /*
                  Saqib Bukhari -- tuesday, 28 th Feb. 2006
          */
          public static String trimData(String data){
                  data = data.trim();
                  data = data.replaceAll(" ","");
                  return data;
          }

          public static boolean isTodayCheck(String data){
                  boolean isMatch = false;
                  data = data.replaceAll("\"","");
                  data = data.replaceAll("'","");
                  Pattern p = Pattern.compile("(?i)today(?-i)[+|-]?[0-9]*?");
                  Matcher m = p.matcher(data);
                  isMatch = m.matches();
                  System.out.println(isMatch);
                  return isMatch;
          }
          /*
                  Saqib Bukhari -- tuesday, 28 th Feb. 2006
          */
  }
