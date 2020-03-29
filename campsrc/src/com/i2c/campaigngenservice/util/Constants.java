package com.i2c.campaigngenservice.util;

import com.i2c.utils.logging.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Constants {

    public static int HOUR_OF_DAY = 1;
    public static int DAY_OF_MONTH = 2;

    public static String COMMON_LOG_INFO = "common";

    public static String CAMP_GEN_API = "camp_gen_api";
    public static String[] APPLICATIONS_NAME_ARRAY = {CAMP_GEN_API};
    public static String ACH_DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
    public static String CHECK_DIGIT_STRING = "2121212121212121212";
    public static String SERIAL_QUERY = " select dbinfo('sqlca.sqlerrd1') from ";
    public static String INSERT_QUERY_INTO_VALUE = "INTO";
    public static String INSERT_QUERY_COLUMN_START_VALUE = "(";
    public static long MAX_ACTIVE_THREADS = 0;
    public static String IS_TRUE = "Y";
    public static String IS_FALSE = "N";
    public static long DEFAULT_WAIT_TIME = 1000;
    public static long DEFAULT_SLEEP_TIME = 0;
    public static String DB_CONNECTION_STRING = "";
    public static String DB_USER_NAME = "";
    public static String DB_USER_PASSWORD = "";
    public static String DB_DRIVER_NAME = "";
    public static String CONFIGURATION_FILE = "config.ini";
//  //Varibles for the Log File
    public static String LOG_FILE_PATH = "";
    public static String LOG_FILE_NAME = "";
    public static String LOG_CONTEXT_NAME = "com.i2c.campgen";

    public static int LOG_FILE_SIZE = 0;
    public static int LOG_FILE_NO = 0;
    public static int LOG_DEBUG_LEVEL = 2;
    public static int LOG_SEVERE = 1;
    public static int LOG_WARNING = 2;
    public static int LOG_INFO = 3;
    public static int LOG_CONFIG = 4;
    public static int LOG_FINE = 5;
    public static int LOG_FINER = 6;
    public static int LOG_FINEST = 7;
    public static int PORT_NUMBER = -1;
    private static String MACHINE_IP = null;
    public static synchronized String getMachineIP() {
        return MACHINE_IP;
    }

    public static synchronized void setMachineIP() {
        try {
            System.out.print(" Method Name: setMachineIP -->");
            java.net.InetAddress machine_ip = java.net.InetAddress.getLocalHost();
            Constants.MACHINE_IP = machine_ip.getHostAddress();
        } catch (java.net.UnknownHostException uhe) {
            System.err.print("Exception in setMachineIP--->" + uhe.getMessage());
        }
    }


}
