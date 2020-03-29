package com.i2c.service.base;

/**
 * @author barshad
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
import java.sql.*;
import java.util.*;
import com.i2c.service.util.*;
import com.i2c.service.util.*;
import com.i2c.service.excep.*;
import com.i2c.service.*;

public abstract class BaseHome {


	/**
	 * Method for inserting the information into the database (Save Changes)
	 * @param savequery: query for execution insert or update or delete
	 * @param dbConn:connection object for executing query	 *
	 * @return the last serial number inserted
	 */
	public long insertValues(String insertquery,Connection dbConn)
		throws InsertValuesExcep {
		long serialNo = -1 ;
		Statement smt = null;
		String serialQuery = "";
		boolean flag = true;
		CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG)," Query for inseting values -->\n"+insertquery);
		try {
			smt = dbConn.createStatement();
			smt.executeUpdate(insertquery);
			smt.close();

			//Getting the serial number condition
			if (insertquery.indexOf(Constants.INSERT_QUERY_INTO_VALUE) > -1) {
				serialQuery = Constants.SERIAL_QUERY + " "+ insertquery.substring(insertquery.indexOf(Constants.INSERT_QUERY_INTO_VALUE) + Constants.INSERT_QUERY_INTO_VALUE.length() ,insertquery.indexOf(Constants.INSERT_QUERY_COLUMN_START_VALUE));
				CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINE)," Serail Number Query -->\n"+serialQuery);
				serialNo = getMaxValue(serialQuery ,dbConn) - 1 ;
			}//end serial number if

			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINE)," Serail Number for insert record -->\n"+serialNo);
		} catch(SQLException e) {
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," SQLException in storing values in database ---> "+e);
			serialNo = -1;
			//Throw the Exception
			throw new InsertValuesExcep(e.getErrorCode(),e);
		} catch(Exception e) {
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in storing values in database ---> "+e);
			flag = false;
			//Throw the Exception
			throw new InsertValuesExcep(e);
		} finally {
			try {
				if (smt != null)
					smt.close();
			} catch(Exception ex) {}
		}//end finally
		return serialNo;
	}//End store Values method



	/**
	 * Method for updating the database (Save Changes)
	 * @param savequery: query for execution insert or update or delete
	 * @param dbConn:connection object for executing query	 *
	 */
	public boolean storeValues(String savequery,Connection dbConn)
		throws StoreValuesExcep {

		Statement smt = null;
		boolean flag = true;
		CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG)," Query for storing values -->\n"+savequery);
		try {
			smt = dbConn.createStatement();
      		smt.executeUpdate(savequery);
	    } catch(SQLException e) {
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," SQLException in storing values in database ---> "+e);
			flag = false;
			//Throw the Exception
    	    throw new StoreValuesExcep(e.getErrorCode(),e);
		} catch(Exception e) {
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in storing values in database ---> "+e);
			flag = false;
			//Throw the Exception
			throw new StoreValuesExcep(e);
	    } finally {
			try {
				if (smt != null)
					smt.close();
			} catch(Exception ex) {}
	    }//end finally
	    return flag;
	}//End store Values method


	/*
  	 * Getting the Reference Values from the database
	 * @param queryString: query for getting the reference value in the key value pair
	 * @param dbConn:connection object for executing query
  	 */
    public Vector getKeyValuePairs(String queryString,Connection dbConn)
    	throws LoadKeyValuePairsExcep {

		CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG)," Query for getting Key Value Pair ->\n"+queryString);
		Vector refvalues = new Vector();
		LabelValueBean labelValueBean = null;
		Statement smt = null;
		ResultSet rs = null;
		try {
		     smt = dbConn.createStatement();
		     rs = smt.executeQuery(queryString);
			 //Populating the Key Pair value
		     while (rs.next()) {
		          labelValueBean = new LabelValueBean(rs.getString(2), rs.getString(1).trim());
		          refvalues.addElement(labelValueBean);
		     }//end while
		} catch(SQLException e){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," SQLException in Getting Key Value Pair ----> "+e);
			throw new LoadKeyValuePairsExcep(e.getErrorCode(),e);
		} catch(Exception e){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in Getting Key Value Pair ----> "+e);
		    throw new LoadKeyValuePairsExcep(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (smt != null)
					smt.close();
			} catch(Exception ex) {}
		}//end finally
		CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINER),"<-- Vector Size for getting Key Value Pair -->"+refvalues.size());
		return refvalues;
    }//End Getting Key Pair Values from database Method


	/*
  	 * Getting the Single Reference Values from the database
	 * @param queryString: query for getting the reference value in the key
	 * @param dbConn:connection object for executing query
  	 */
    public Vector getKeyValues(String queryString,Connection dbConn)
    	throws LoadKeyValuesExcep {

	  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG) ,"<--- Query for getting Key values -->\n"+queryString);
      Vector refvalues = new Vector();
	  Statement smt = null;
	  ResultSet rs = null;

      try {
          smt = dbConn.createStatement();
          rs = smt.executeQuery(queryString);

          while(rs.next()) {
              if (rs.getString(1) != null)
	              refvalues.addElement(rs.getString(1).trim());
	          else
		          refvalues.addElement("");
          }//end while
		} catch(SQLException e){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"SQLException in Getting Key Values ----> "+e);
		  	throw new LoadKeyValuesExcep(e.getErrorCode(),e);
        } catch(Exception e){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Getting Key Values ----> "+e);
			throw new LoadKeyValuesExcep(e);
		} finally {
		  try {
			  if (rs != null)
				  rs.close();
			  if (smt != null)
				  smt.close();
		  } catch(Exception ex) {}
	  	}//end finally
	    return refvalues;
    }//End Getting single reference Value from database Method


	/*
  	 * Checking the value already exist in the database or not
	 * @param queryString: query for checking the duplication
	 * @param dbConn:connection object for executing query
  	 */
    public boolean checkValueExist(Connection dbConn,String queryString)
    	throws CheckValueExistExcep {

		CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"<-- Query for Checking Value Exist -->\n"+queryString);
        boolean recordexist = false;
		Statement smt = null;
		ResultSet rs = null;
        try{
           smt = dbConn.createStatement();
           rs = smt.executeQuery(queryString);

           while(rs.next()) {
             if (rs.getInt(1) > 0)
               recordexist = true;
           }//end while
		} catch(SQLException e){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"SQLException in Checking Value Exist ----> "+e);
			throw new CheckValueExistExcep(e.getErrorCode(),e);
        } catch(Exception e){
			CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Checking Value Exist ----> "+e);
			throw new CheckValueExistExcep(e);
		} finally {
		  try {
			  if (rs != null)
				  rs.close();
			  if (smt != null)
				  smt.close();
		  } catch(Exception ex) {}
		}//end finally
        return recordexist;
    }//End check Exist Method


	/*
  	 * Checking the getting the maximum sequence counter value
	 * @param queryString: query for getting the maximum value
	 * @param dbConn:connection object for executing query
  	 */
   public long getMaxValue(String queryString,Connection dbConn)
   		throws GetMaxValueExcep {
	  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"<-- Query for getting the Max value -->\n"+queryString);
	  Statement smt = null;
	  ResultSet rs = null;
      long seqcount = 1 ;
      try {
          smt = dbConn.createStatement();
          rs = smt.executeQuery(queryString);

          if (rs.next()) {
             seqcount = rs.getLong(1) + 1;
          }//end if
	  } catch(SQLException e){
		  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"SQLException in Getting Maximum Sequence Counter ----> "+e);
		  throw new GetMaxValueExcep(e.getErrorCode(),e);
      } catch(Exception e){
		  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Getting Maximum Sequence Counter ----> "+e);
          throw new GetMaxValueExcep(e);
      } finally {
			try {
				if (rs != null)
					rs.close();
				if (smt != null)
					smt.close();
			} catch(Exception ex) {}
	  }//end finally
	  return seqcount;
    }//End Getting Sequence Counter Value from database Method


}//end class
