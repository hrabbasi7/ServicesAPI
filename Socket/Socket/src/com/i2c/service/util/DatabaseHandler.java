package com.i2c.service.util;


import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.Vector;
import com.i2c.service.util.*;


public class DatabaseHandler {

  /**
   * Method for getting the database connection
   * @param servletName: the class which is getting a connection
   * @return: the connection
   * @throws SQLException
   */
  public static Connection getConnection(String className,String instanceName) throws SQLException {
    DbConnectionInfoObj connObj = null;
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Getting DB connection Instance Name -->"+instanceName+" Connection Instance Size -->"+LoadProperties.instanceConnectionsTable.size());
    try {
      Class.forName(Constants.DB_DRIVER_NAME);
    } catch (Exception e){}

    //Checking the Instance Connection Poool
    if (instanceName == null || !LoadProperties.instanceConnectionsTable.containsKey(instanceName))
      throw new SQLException("Invalid Instance Id");
    //Getting the Connection Information object
    connObj = (DbConnectionInfoObj)LoadProperties.instanceConnectionsTable.get(instanceName);

    if (connObj == null)
      throw new SQLException("Invalid Instance Id");
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Got Connection Object Connection String is -->"+connObj.getConnectionString()+"<---Connection Name -->"+connObj.getConnectionName()+"<-- User -->"+connObj.getUserID()+"<--- Password -->"+connObj.getPasswod()+"<--- Connection Pool Size -->"+connObj.getConnectionPool().size());
    //Creating Instance connection
    Connection con = DriverManager.getConnection(connObj.getConnectionString(), connObj.getUserID(), connObj.getPasswod());
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"DataBaseHandler --- getConnection --- Calling checkConnection method --- Response received-->" + checkConnection(con));
    }
    catch (SQLException ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"DataBaseHandler --- getConnection --- Exception in the method for checking connection ---> " + ex);
      throw new SQLException(ex.getMessage() );
    }
    return con;
  }//end get connection

  /**
   * Method for closing the connection
   * @param returned:
   * @param servletName: the class which is returning the connection
   */
  public static void returnConnection(Connection returned, String className) {
    try {
      returned.close();
    } catch(Exception e){}
  }//end return connection

  /******************************************************************************/
  /**
   * This method is used to check connections
   * @param con Connection
   * @return boolean
   * @throws SQLException Exception
   */
  public static boolean checkConnection(Connection con) throws SQLException {
    boolean ret = false ;
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"DataBaseHandler --- checkConnection --- In the method for checking connection --- Coneection received--->" + con);
    CallableStatement stmt = null ;
    try {
      stmt = con.prepareCall("execute procedure set_isolation_proc()") ;
      stmt.executeUpdate() ;
      stmt.close() ;
      ret = true ;
    } catch(SQLException e) {
      throw new SQLException("Connection inactive: " + e.getMessage() );
    } catch (Exception ex ) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"DataBaseHandler --- checkConnection --- Exception in checking connection ---> " + ex);
      throw new SQLException("Error: " + ex.getMessage() ) ;
    }
    return ret;
  }//end check Conenction Method
/******************************************************************************/

}//end class




