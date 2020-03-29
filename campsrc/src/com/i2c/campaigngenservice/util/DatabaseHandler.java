package com.i2c.campaigngenservice.util;


import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.Vector;
import com.i2c.campaigngenservice.util.*;
import com.i2c.utils.logging.*;
import com.i2c.utils.logging.*;

public class DatabaseHandler {

  /**
   * Method for getting the database connection
   * @param servletName: the class which is getting a connection
   * @return: the connection
   * @throws SQLException
   */
  public static Connection getConnection(String className,String instanceName) throws SQLException {
    DbConnectionInfoObj connObj = null;
    System.out.println("Getting DB connection Instance Name -->"+instanceName+
                          "Connection Instance Size -->"+LoadProperties.instanceConnectionsTable.size());
    System.out.println("Getting DB connection Instance Name -->"+instanceName+
                          "Connection Instance Size -->"+LoadProperties.instanceConnectionsTable.size());
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
    System.out.println(" Got Connection Object Connection String is -->"+connObj.getConnectionString()+"<---Connection Name -->"+connObj.getConnectionName()+"<-- User -->"+connObj.getUserID()+"<--- Password -->"+connObj.getPasswod()+"<--- Connection Pool Size -->"+connObj.getConnectionPool().size());
    //Creating Instance connection
    Connection con = DriverManager.getConnection(connObj.getConnectionString(), connObj.getUserID(), connObj.getPasswod());
    try {
      System.out.println("DataBaseHandler --- getConnection --- Calling checkConnection method --- Response received-->" + checkConnection(con));
    }
    catch (SQLException ex) {
      System.err.println("DataBaseHandler --- getConnection --- Exception in the method for checking connection ---> " + ex);
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
/**
 * Method for getting the FC database connection
 * @param servletName: the class which is getting a connection
 * @return: the connection
 * @throws SQLException
 */
public static Connection getFCConnection(String className,String instanceName) throws SQLException {
  DbConnectionInfoObj connObj = null;
  System.out.println("Getting FC DB connection Instance Name -->"+instanceName+" Connection Instance Size -->"+LoadProperties.instanceConnectionsTable.size());
  try {
    Class.forName(Constants.DB_DRIVER_NAME);
  } catch (Exception e){}

  //Checking the Instance Connection Poool
  if (instanceName == null || !LoadProperties.instanceConnectionsTable.containsKey(instanceName))
    throw new SQLException("Invalid Instance Id");
  //Getting the Connection Information object
  connObj = (DbConnectionInfoObj)LoadProperties.instanceConnectionsTable.get(instanceName);

  if (connObj == null)
    throw new SQLException("No Connection info found for provided instance");
  System.out.println(" Got FC Connection Object Connection String is -->"+connObj.getFcConnectionString()+"<---Connection Name -->"+connObj.getConnectionName()+"<-- User -->"+connObj.getFcUserID()+"<--- Password -->"+connObj.getFcPasswod());
  //Creating Instance connection
  Connection con = DriverManager.getConnection(connObj.getFcConnectionString(), connObj.getFcUserID(), connObj.getFcPasswod());
  return con;
}//end get connection

/******************************************************************************/
  /**
   * This method is used to check connections
   * @param con Connection
   * @return boolean
   * @throws SQLException Exception
   */
  public static boolean checkConnection(Connection con) throws SQLException {
    boolean ret = false ;
    System.out.println("DataBaseHandler --- checkConnection --- In the method for checking connection --- Coneection received--->" + con);
    CallableStatement stmt = null ;
    try {
      stmt = con.prepareCall("execute procedure set_isolation_proc()") ;
      stmt.executeUpdate() ;
      stmt.close() ;
      ret = true ;
    } catch(SQLException e) {
      throw new SQLException("Connection inactive: " + e.getMessage() );
    } catch (Exception ex ) {
      System.out.println("DataBaseHandler --- checkConnection --- Exception in checking connection ---> " + ex);
      throw new SQLException("Error: " + ex.getMessage() ) ;
    }
    return ret;
  }//end check Conenction Method
/******************************************************************************/


}//end class




