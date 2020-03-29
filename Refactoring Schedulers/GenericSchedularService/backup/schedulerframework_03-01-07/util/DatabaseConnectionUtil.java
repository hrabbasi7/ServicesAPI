package com.i2c.schedulerframework.util;


import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.Vector;
import com.i2c.schedulerframework.util.*;
import com.i2c.billpayment.vo.InterfaceConfigObj;
import com.i2c.schedulerframework.main.ServiceMain;
import com.i2c.schedulerframework.model.DbConfigBean;
import com.i2c.utils.logging.I2cLogger;

public class DatabaseConnectionUtil {

  /**
     * Method for getting the database connection
     * @param servletName: the class which is getting a connection
     * @return: the connection
     * @throws SQLException
     */
    public static Connection getMasterConnection(String className,
                                                 String instanceName,
                                                 DbConfigBean connObj) throws SQLException {
//      DbConnectionInfoObj connObj = null;
      System.out.println("Getting DB connection Instance Name -->"+instanceName+" Connection Instance Size -->"+ConfigrationHandler.instanceConnectionsTable.size());
      try {
        Class.forName(BaseConstants.DB_DRIVER_NAME);
      } catch (Exception e){
      e.printStackTrace();
    }

      //Checking the Instance Connection Poool
//      if (instanceName == null || !LoadProperties.instanceConnectionsTable.containsKey(instanceName))
//        throw new SQLException("Invalid Instance Id");
//      //Getting the Connection Information object
//      connObj = (DbConnectionInfoObj)LoadProperties.instanceConnectionsTable.get(instanceName);

      if (connObj == null)
        throw new SQLException("Invalid Instance Id");
      System.out.println(" Got Connection Object Connection String is -->"+connObj.getConnectionString()+"<---Connection Name -->"+connObj.getConnectionName()+"<-- User -->"+connObj.getUserID()+"<--- Password -->"+connObj.getPasswod()+"<--- Connection Pool Size -->"+connObj.getConnectionPool().size());
      //Creating Instance connection
      Connection con = DriverManager.getConnection(connObj.getConnectionString(), connObj.getUserID(), connObj.getPasswod());

      try {
        System.out.println("DataBaseHandler --- getConnection --- Calling checkConnection method --- Response received-->" + checkConnection(con));
      }
      catch (SQLException ex) {
        System.out.println("DataBaseHandler --- getConnection --- Exception in the method for checking connection ---> " + ex);
        throw new SQLException(ex.getMessage() );
      }
      return con;
  }//end get connection

  /**
   * Method for getting the database connection
   * @param servletName: the class which is getting a connection
   * @return: the connection
   * @throws SQLException
   */
  public static Connection getConnection(String className,String instanceName) throws SQLException {
    DbConfigBean connInfoObj = null;
    ServiceMain.mainLogger.log(I2cLogger.FINEST,"Getting DB connection Instance Name -->"+instanceName+" Connection Instance Size -->"+ConfigrationHandler.instanceConnectionsTable.size());
    try {
      Class.forName(BaseConstants.DB_DRIVER_NAME);
    } catch (Exception e){}

    //Checking the Instance Connection Poool
    if (instanceName == null || !ConfigrationHandler.instanceConnectionsTable.containsKey(instanceName))
      throw new SQLException("Invalid Instance Id");
    //Getting the Connection Information object
    connInfoObj = (DbConfigBean)ConfigrationHandler.instanceConnectionsTable.get(instanceName);

    if (connInfoObj == null)
      throw new SQLException("Invalid Instance Id");
    ServiceMain.mainLogger.log(I2cLogger.FINEST," Got Connection Object Connection String is -->"+connInfoObj.getConnectionString()+"<---Connection Name -->"+connInfoObj.getConnectionName()+"<-- User -->"+connInfoObj.getUserID()+"<--- Password -->"+connInfoObj.getPasswod()+"<--- Connection Pool Size -->"+connInfoObj.getConnectionPool().size());
    //Creating Instance connection
    Connection con = DriverManager.getConnection(connInfoObj.getConnectionString(), connInfoObj.getUserID(), connInfoObj.getPasswod());
    try {
      ServiceMain.mainLogger.log(I2cLogger.FINEST,"DataBaseHandler --- getConnection --- Calling checkConnection method --- Response received-->" + checkConnection(con));
    }
    catch (SQLException ex) {
      ServiceMain.mainLogger.log(I2cLogger.WARNING,"DataBaseHandler --- getConnection --- Exception in the method for checking connection ---> " + ex);
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
  DbConfigBean connObj = null;
  ServiceMain.mainLogger.log(I2cLogger.FINEST,"Getting FC DB connection Instance Name -->"+instanceName+" Connection Instance Size -->"+ConfigrationHandler.instanceConnectionsTable.size());
  try {
    Class.forName(BaseConstants.DB_DRIVER_NAME);
  } catch (Exception e){}

  //Checking the Instance Connection Poool
  if (instanceName == null || !ConfigrationHandler.instanceConnectionsTable.containsKey(instanceName))
    throw new SQLException("Invalid Instance Id");
  //Getting the Connection Information object
  connObj = (DbConfigBean)ConfigrationHandler.instanceConnectionsTable.get(instanceName);

  if (connObj == null)
    throw new SQLException("No Connection info found for provided instance");
  ServiceMain.mainLogger.log(I2cLogger.FINEST," Got FC Connection Object Connection String is -->"+connObj.getFcConnectionString()+"<---Connection Name -->"+connObj.getConnectionName()+"<-- User -->"+connObj.getFcUserID()+"<--- Password -->"+connObj.getFcPasswod());
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
    ServiceMain.mainLogger.log(I2cLogger.FINEST,"DataBaseHandler --- checkConnection --- In the method for checking connection --- Coneection received--->" + con);
    CallableStatement stmt = null ;
    try {
      stmt = con.prepareCall("execute procedure set_isolation_proc()") ;
      stmt.executeUpdate() ;
      stmt.close() ;
      ret = true ;
    } catch(SQLException e) {
      throw new SQLException("Connection inactive: " + e.getMessage() );
    } catch (Exception ex ) {
      ServiceMain.mainLogger.log(I2cLogger.WARNING,"DataBaseHandler --- checkConnection --- Exception in checking connection ---> " + ex);
      throw new SQLException("Error: " + ex.getMessage() ) ;
    }
    return ret;
  }//end check Conenction Method





/******************************************************************************/
  public static ArrayList getExtInterfaceConfig(DbConfigBean conn) throws Exception
   {
     StringBuffer query = new StringBuffer();
     Class.forName(BaseConstants.DB_DRIVER_NAME);
     Connection extCon = DriverManager.getConnection(
        conn.getConnectionString(),
        conn.getUserID(),
        conn.getPasswod());

     ArrayList configAL = new ArrayList();

     //build query to get the external interfaces
////     String query = "select * from ext_interfaces";
//     query.append("select eface.interface_id, eface.interface_name, eface.interface_abrv, ");
//     query.append("eface.conn_driver_name, eface.conn_string, eface.user_id, eface.password, ebatch.is_active ");
//     query.append("from ext_interfaces eface, bp_ext_batches ebatch ");
//     query.append("where eface.interface_id = ebatch.interface_id ");
//     query.append("and ebatch.is_active = 'Y'");

     query.append("select distinct e.interface_id,e.interface_name,e.interface_abrv,b.is_active, ");
     query.append("case when e.instance_id is null ");
     query.append("then e.conn_string ");
     query.append("else  (select db_conn_string from instances where instance_id=e.instance_id) ");
     query.append("end conn_string, ");
     query.append("case when e.instance_id is null ");
     query.append("then e.user_id ");
     query.append("else (select db_user_id from instances where instance_id=e.instance_id) ");
     query.append("end user_id, ");
     query.append("case when e.instance_id is null ");
     query.append("then e.password ");
     query.append("else (select db_user_passwd from instances where instance_id=e.instance_id) ");
     query.append("end password, ");
     query.append("case when e.instance_id is null ");
     query.append("then e.conn_driver_name ");
     query.append("else (select conn_driver_name from instances where instance_id=e.instance_id) ");
     query.append("end conn_driver_name ");
     query.append("from ext_interfaces e, bp_ext_batches b ");
     query.append("where e.interface_id = b.interface_id ");
     query.append("and b.is_active ='Y' ");

     Statement pstmt = null;
     ResultSet rs = null;

     try {
      //prepare the statement
      pstmt = extCon.createStatement();

      //System.out.println("Query to Get Connections---:>" + query.toString());
      //execute the statement
      rs = pstmt.executeQuery(query.toString());

      System.out.println("<-------> Statement Executed for 'ext_interfaces'<------->");

      //flag to hold the invalid configuration status
      boolean isInvalidConfig = false;



      while(rs.next()) {

        InterfaceConfigObj configObj = new InterfaceConfigObj();
        //set the interface id
        String id = rs.getString("interface_id").trim();
        configObj.setInterfaceId(id);

        //get the interface name
        String value = rs.getString("interface_name").trim();

        System.out.println("Interface Name -- > " + value);

        configObj.setInterfaceName((value != null ? value : ""));

        //get the Connection driver name
        value = rs.getString("conn_driver_name").trim();

        System.out.println("Driver Name -- > " + value);

        if(value == null || value.trim().equalsIgnoreCase("")) {

          ServiceMain.mainLogger.log(I2cLogger.
              FINEST,"Connection Driver Name not found against interface id -- > "
                                          + configObj.getInterfaceId());

          isInvalidConfig = true;
        }//end if

        //set the driver name
        configObj.setDriverName(value);

        //get the connection string
        value = rs.getString("conn_string").trim();

        System.out.println("Connection String -- > " + value);

        if(value == null || value.trim().equalsIgnoreCase("")){

          System.out.println("Connection String not found against interface id -- > "
                              + configObj.getInterfaceId());

          isInvalidConfig = true;
        }//end if

        //set the connection string
        configObj.setConnString(value);

        //get the user id
        value = rs.getString("user_id").trim();

        System.out.println("User ID -- > " + value);


        if(value == null || value.trim().equalsIgnoreCase("")) {

          System.out.println("User ID not found against interface id -- > "
                              + configObj.getInterfaceId());

          isInvalidConfig = true;
        }//end if

        //set the user id
        configObj.setUserId(value);

        //get the password
        value = rs.getString("password").trim();

        System.out.println("Password -- > " + value);

        if(value == null || value.trim().equalsIgnoreCase("")) {

          System.out.println("Password not found against interface id -- > "
                              + configObj.getInterfaceId());

          isInvalidConfig = true;

        }else{
          //set the password
          configObj.setPassword(value);
        }
        //get the isactive
        value = rs.getString("is_active").trim();

        System.out.println("bp_ext_batches -- Is Active -- > " + value);

       //set the is active
       configObj.setIsActive((value != null ? value.trim() : "N"));

       if(!isInvalidConfig)
           configAL.add(configObj);

      }//end if(rs.next())

      return configAL;

    }//end try

     catch (Exception ex) {

       System.out.println("Exception -- > " + ex);
       ex.printStackTrace();

       throw new Exception(ex);

     }//end catch

     finally {

       try {
         if(rs != null) rs.close();
         if(pstmt != null) pstmt.close();

       }//end try
       catch (Exception ex) {}//end

     }//end finally

   } //end getExtInterfaceInterface()

}//end class




