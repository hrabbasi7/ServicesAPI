package com.i2c.services.home;

import com.i2c.services.base.*;
import java.sql.*;

/**
 *
 * <p>Title: ACHServiceHome: A class which provides ACH services</p>
 * <p>Description: This class provides methods for ACH related DB Operations such as getting ACH account
 * status from the database.</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class ACHServiceHome extends ServicesBaseHome{

  /**
  * Constructor
  * @param _con Connection
  */
 private ACHServiceHome() {
 } //end Constructor ServicesHome


 /**
  * Factory Method which creates the instance of the ACHServiceHome and returns it.
  * @param _con Connection
  * @return ACHServiceHome
  */
 public static ACHServiceHome getInstance(Connection _con)
 {
   ACHServiceHome home = new ACHServiceHome();

     home.con = _con;
     return home;
 }//end getInstance

}//end ACHServiceHome

