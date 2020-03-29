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

//  private Connection dbConn = null;
//
//  public BaseHome(Connection dbConn){
//    this.dbConn = dbConn;
//  }

  /**
   * Method for inserting the information into the database (Save Changes)
   * @param savequery: query for execution insert or update or delete
   * @param dbConn:connection object for executing query	 *
   * @return the last serial number inserted
   */
  public long insertValues(String insertquery, Connection dbConn) throws
      InsertValuesExcep {
    long serialNo = -1;
    Statement smt = null;
    String serialQuery = "";
    boolean flag = true;
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    " Query for inseting values -->\n" +
                                    insertquery);
    try {
      smt = dbConn.createStatement();
      smt.executeUpdate(insertquery);
      smt.close();

      //Getting the serial number condition
      if (insertquery.indexOf(Constants.INSERT_QUERY_INTO_VALUE) > -1) {
        serialQuery = Constants.SERIAL_QUERY + " " +
            insertquery.substring(
            insertquery.indexOf(Constants.INSERT_QUERY_INTO_VALUE) +
            Constants.INSERT_QUERY_INTO_VALUE.length(),
            insertquery.indexOf(Constants.INSERT_QUERY_COLUMN_START_VALUE));
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINE),
                                        " Serail Number Query -->\n" +
                                        serialQuery);
        serialNo = getMaxValue(serialQuery, dbConn) - 1;
      } //end serial number if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINE),
                                      " Serail Number for insert record -->\n" +
                                      serialNo);
    }
    catch (SQLException e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          " SQLException in storing values in database ---> " + e);
      serialNo = -1;
      //Throw the Exception
      throw new InsertValuesExcep(e.getErrorCode(), e);
    }
    catch (Exception e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          " Exception in storing values in database ---> " + e);
      flag = false;
      //Throw the Exception
      throw new InsertValuesExcep(e);
    }
    finally {
      try {
        if (smt != null)
          smt.close();
      }
      catch (Exception ex) {}
    } //end finally
    return serialNo;
  } //End store Values method

  /**
   * Method for updating the database (Save Changes)
   * @param savequery: query for execution insert or update or delete
   * @param dbConn:connection object for executing query	 *
   */
  public boolean storeValues(String savequery, Connection dbConn) throws
      StoreValuesExcep {

    Statement smt = null;
    boolean flag = true;
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    " Query for storing values -->\n" +
                                    savequery);
    try {
      smt = dbConn.createStatement();
      smt.executeUpdate(savequery);
    }
    catch (SQLException e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          " SQLException in storing values in database ---> " + e);
      flag = false;
      //Throw the Exception
      throw new StoreValuesExcep(e.getErrorCode(), e);
    }
    catch (Exception e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          " Exception in storing values in database ---> " + e);
      flag = false;
      //Throw the Exception
      throw new StoreValuesExcep(e);
    }
    finally {
      try {
        if (smt != null)
          smt.close();
      }
      catch (Exception ex) {}
    } //end finally
    return flag;
  } //End store Values method

  /*
   * Getting the Reference Values from the database
   * @param queryString: query for getting the reference value in the key value pair
   * @param dbConn:connection object for executing query
   */
  public Vector getKeyValuePairs(String queryString, Connection dbConn) throws
      LoadKeyValuePairsExcep {

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    " Query for getting Key Value Pair ->\n" +
                                    queryString);
    Vector refvalues = new Vector();
    LabelValueBean labelValueBean = null;
    Statement smt = null;
    ResultSet rs = null;
    try {
      smt = dbConn.createStatement();
      rs = smt.executeQuery(queryString);
      //Populating the Key Pair value
      while (rs.next()) {
        if (rs.getString(2) != null && rs.getString(1) != null) {
          labelValueBean = new LabelValueBean(rs.getString(2).trim(),
                                              rs.getString(1).trim());
          refvalues.addElement(labelValueBean);
        }
      } //end while
    }
    catch (SQLException e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          " SQLException in Getting Key Value Pair ----> " + e);
      throw new LoadKeyValuePairsExcep(e.getErrorCode(), e);
    }
    catch (Exception e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          " Exception in Getting Key Value Pair ----> " + e);
      throw new LoadKeyValuePairsExcep(e);
    }
    finally {
      try {
        if (rs != null)
          rs.close();
        if (smt != null)
          smt.close();
      }
      catch (Exception ex) {}
    } //end finally
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINER),
        "<-- Vector Size for getting Key Value Pair -->" + refvalues.size());
    return refvalues;
  } //End Getting Key Pair Values from database Method

  /*
   * Getting the Single Reference Values from the database
   * @param queryString: query for getting the reference value in the key
   * @param dbConn:connection object for executing query
   */
  public Vector getKeyValues(String queryString, Connection dbConn) throws
      LoadKeyValuesExcep {

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "<--- Query for getting Key values -->\n" +
                                    queryString);
    Vector refvalues = new Vector();
    Statement smt = null;
    ResultSet rs = null;

    try {
      smt = dbConn.createStatement();
      rs = smt.executeQuery(queryString);

      while (rs.next()) {
        if (rs.getString(1) != null)
          refvalues.addElement(rs.getString(1).trim());
        else
          refvalues.addElement("");
      } //end while
    }
    catch (SQLException e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          "SQLException in Getting Key Values ----> " + e);
      throw new LoadKeyValuesExcep(e.getErrorCode(), e);
    }
    catch (Exception e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Getting Key Values ----> " +
                                      e);
      throw new LoadKeyValuesExcep(e);
    }
    finally {
      try {
        if (rs != null)
          rs.close();
        if (smt != null)
          smt.close();
      }
      catch (Exception ex) {}
    } //end finally
    return refvalues;
  } //End Getting single reference Value from database Method

  /*
   * Checking the value already exist in the database or not
   * @param queryString: query for checking the duplication
   * @param dbConn:connection object for executing query
   */
  public boolean checkValueExist(Connection dbConn, String queryString) throws
      CheckValueExistExcep {

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "<-- Query for Checking Value Exist -->\n" +
                                    queryString);
    boolean recordexist = false;
    Statement smt = null;
    ResultSet rs = null;
    try {
      smt = dbConn.createStatement();
      rs = smt.executeQuery(queryString);

      while (rs.next()) {
        if (rs.getInt(1) > 0)
          recordexist = true;
      } //end while
    }
    catch (SQLException e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          "SQLException in Checking Value Exist ----> " + e);
      throw new CheckValueExistExcep(e.getErrorCode(), e);
    }
    catch (Exception e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          "Exception in Checking Value Exist ----> " + e);
      throw new CheckValueExistExcep(e);
    }
    finally {
      try {
        if (rs != null)
          rs.close();
        if (smt != null)
          smt.close();
      }
      catch (Exception ex) {}
    } //end finally
    return recordexist;
  } //End check Exist Method

  /*
   * Checking the getting the maximum sequence counter value
   * @param queryString: query for getting the maximum value
   * @param dbConn:connection object for executing query
   */
  public long getMaxValue(String queryString, Connection dbConn) throws
      GetMaxValueExcep {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "<-- Query for getting the Max value -->\n" +
                                    queryString);
    Statement smt = null;
    ResultSet rs = null;
    long seqcount = 1;
    try {
      smt = dbConn.createStatement();
      rs = smt.executeQuery(queryString);

      if (rs.next()) {
        seqcount = rs.getLong(1) + 1;
      } //end if
    }
    catch (SQLException e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          "SQLException in Getting Maximum Sequence Counter ----> " + e);
      throw new GetMaxValueExcep(e.getErrorCode(), e);
    }
    catch (Exception e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          "Exception in Getting Maximum Sequence Counter ----> " + e);
      throw new GetMaxValueExcep(e);
    }
    finally {
      try {
        if (rs != null)
          rs.close();
        if (smt != null)
          smt.close();
      }
      catch (Exception ex) {}
    } //end finally
    return seqcount;
  } //End Getting Sequence Counter Value from database Method

  /*
   --------------------------------------------------------------------------------
        Name: processScheduler
   Description: This method tests whether the processing of Schedule Services
                     is allowed or not.
        Parameters: columnName (name of the db column to examine), instanceName
        Return Value: boolean (indicating service allowed or not)
        Exceptions: NONE
   --------------------------------------------------------------------------------
   */

  public boolean processScheduler(Connection dbConn, String serviceName) throws
      SQLException {
    boolean useServcieFlag = true;
    StringBuffer query = new StringBuffer();

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    " Process Scheduler Method ");
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    " Scheduler Service Name " + serviceName);
    try {
      query.append(
          "select is_allowed from scheduler_services where service_name = ");
      CommonUtilities.buildQueryInfo(query, serviceName, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      " Query for Process Scheduler Service-->" +
                                      query);
      Vector retVal = this.getKeyValues(query.toString(), dbConn);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      " ResultSet Size  -->" + retVal.size());
      if (retVal.size() > 0) {
        String strVal = retVal.elementAt(0).toString();
        if (strVal.equalsIgnoreCase(Constants.IS_TRUE)) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              " Value Found for specified service -->" + Constants.IS_TRUE);
          return useServcieFlag;
        }
        else if (strVal.equalsIgnoreCase(Constants.IS_FALSE)) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              " Use_Scheduler_Service Value --> " + Constants.IS_FALSE);
          useServcieFlag = false;
        }
        else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST), " Use_Scheduler_Service Value --> NULL");
          return useServcieFlag;
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            " Unable to find the is_allowed value for specified service--->" +
                                        serviceName);
        return false;
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          " Exception in Process Scheduler Method-- > " + ex);
      useServcieFlag = false;
    }
    return useServcieFlag;
  } //end processScheduler method

  public boolean processScheduler( String serviceName, String instanceName) throws
    SQLException {
  boolean useServcieFlag = true;
  StringBuffer query = new StringBuffer();
  Connection dbConn = null;
  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                  " Process Scheduler Method ");
  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                  " Scheduler Service Name " + serviceName);
  try {
    query.append(
        "select is_allowed from scheduler_services where service_name = ");
    CommonUtilities.buildQueryInfo(query, serviceName, true);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    " Query for Process Scheduler Service-->" +
                                    query);
    dbConn = DatabaseHandler.getConnection("EODServiceThread",instanceName);
    Vector retVal = this.getKeyValues(query.toString(), dbConn);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    " ResultSet Size  -->" + retVal.size());
    if (retVal.size() > 0) {
      String strVal = retVal.elementAt(0).toString();
      if (strVal.equalsIgnoreCase(Constants.IS_TRUE)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
            " Value Found for specified service -->" + Constants.IS_TRUE);
        return useServcieFlag;
      }
      else if (strVal.equalsIgnoreCase(Constants.IS_FALSE)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
            " Use_Scheduler_Service Value --> " + Constants.IS_FALSE);
        useServcieFlag = false;
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST), " Use_Scheduler_Service Value --> NULL");
        return useServcieFlag;
      }
    }
    else {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
          " Unable to find the is_allowed value for specified service--->" +
                                      serviceName);
      return false;
    }
  }
  catch (Exception ex) {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
        " Exception in Process Scheduler Method-- > " + ex);
    useServcieFlag = false;
  }finally{
    if(dbConn != null){
      dbConn.close();
    }
  }
  return useServcieFlag;
} //end processScheduler method


  /**
   * Method for validating the passed connection
   * @param dbConn Connection
   * @param counter int
   * @param instanceName String
   * @throws ProcessValuesExcep
   * @return Connection
   */

  public Connection verfiyConnection(Connection dbConn, int counter,
                                     String instanceName) throws
      ProcessValuesExcep {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
          "<-- Verfiy Connection Method --- Arguments Received --- Connection ---> " +
                                      dbConn + " Counter ---> " + counter +
                                      " InstanceName -->" + instanceName);
      String query = "select * from system_variables";
      //Checking teh connection validatiy loop
      for (int i = 0; i < counter; i++) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            " Loop Checking Connection counter --->" + counter + "<--- i ---->" +
                                        i);
        try {

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              " BaseHome --- verfiyConnection --- Creating Statment Object---");
          stmt = dbConn.createStatement();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              " BaseHome --- verfiyConnection --- Executing Query---");
          rs = stmt.executeQuery(query);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              " BaseHome --- Verfiy Connection --- Connection is valid -- Closing ResultSet");
          //Closing the valid statment
          rs.close();
          stmt.close();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              " BaseHome --- Verfiy Connection --- Connection is valid -- Breaking Loop");
          break;

        }
        catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_WARNING),
              " BaseHome --Cuurent Count -->" + i + "<--- Counter -1 -->" +
                            (counter - 1) +
              "--- verfiyConnection --- Exception in Validating/Verifying Connection ---->" +
                            ex);

          //Terminate the method the retry connection counter value has exceeded
          if (i == counter - 1)
            throw new ProcessValuesExcep( -1,
                                         "Invalid Database Connection--->" + ex);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              " BaseHome --- Creating new Connection --- Argumnets passed to DatabaseHandler.getConnection --- ClassName---> BaseHome -- InstanceName--->" + instanceName);

          //Close exisiting dump connection
          try {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST), " Closing existing dump connetion --->");
            if (dbConn != null)
              DatabaseHandler.returnConnection(dbConn, "BaseHome");
            dbConn = null;
          }
          catch (Exception e) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_WARNING),
                " Exception in closing existing dump connection -->" + e);
          } //end catch

          //Creat new databaase connection if exsiting connection have problems
          try {
            dbConn = DatabaseHandler.getConnection("BaseHome", instanceName);
          }
          catch (Exception ex1) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_WARNING),
                " BaseHome --- verfiyConnection --- Exception in Creating New Connection --->" +
                              ex1);
          } //end catch
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_WARNING),
              " BaseHome --- verfiyConnection --- Got New DBConn--->" + dbConn);
        } //end catch

      } // end for
    }
    catch (Exception mainExcep) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          "Exception in Verify Connetion method -->" + mainExcep);
      throw new ProcessValuesExcep( -1,
                                   "Exception in Verify DB connection -->" +
                                   mainExcep.getMessage());

    }
    finally {
      try {
        if (rs != null)
          rs.close();
        if (stmt != null)
          stmt.close();
      }
      catch (Exception ex) {}
    } // end finally
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
        " BaseHome --- verfiyConnection --- Returning valid DB Conneciton--->" +
                                    dbConn.toString());
    return dbConn;
  } // end method

  /**
   * Method for validating the datbase connection object
   * @param con: connection object
   * @throws SQLException
   */

//public static void checkConnection(Connection con)
//          throws SQLException {
//        Statement smt = null;
//        ResultSet rs = null;
//         try {
//                CommonUtilities.getLogger().log(LogLevel.getLevel(CardsConstants.LOG_FINEST),"<------- Before Creating Ctatment from Connection  --->");
//                smt = con.createStatement();
//                CommonUtilities.getLogger().log(LogLevel.getLevel(CardsConstants.LOG_FINEST),"<------- Before Creating Resultset from Connection  --->");
//            rs = smt.executeQuery(" select currency_code  from system_variables ");
//                CommonUtilities.getLogger().log(LogLevel.getLevel(CardsConstants.LOG_FINEST),"<------- After Closing Stmt and ResultSet  --->");
//             rs.close();
//                smt.close();
//         } catch(Exception e) {
//                throw new SQLException("Invalid Connection");
//         } finally {
//                 try {
//                         if (rs != null )
//                                 rs.close();
//                         if (smt != null)
//                             smt.close();
//                 } catch (Exception e){}//end catch
//         }//end finally
//  }//end check Conenction Method

} //end class
