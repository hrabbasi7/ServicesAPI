package com.i2c.services.test.helper;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author agohar
 * @version 1.0
 */

public class DBConnectionManager {

    /**
     * @throws Exception
     * @return Connection
     */
    public static Connection getConnection(String[] conProps) throws Exception {

        Connection con = null;

        Class.forName(conProps[0]);
        con = DriverManager
                .getConnection(conProps[1], conProps[2], conProps[3]);

        return con;

    }// end




    /**
     * @param con
     *        Connection
     * @throws Exception
     */
    public static void closeConnection(Connection con) throws Exception {

        if(con != null)
            con.close();

    }// end
}// end class
