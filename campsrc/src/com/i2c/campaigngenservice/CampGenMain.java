package com.i2c.campaigngenservice;

/**
 * <p>Title: Payee Information Service</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Innovative Pvt. Ltd.</p>
 *
 * @author Haroon ur Rashid Abbasi
 * @version 1.0
 */
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Logger;


import com.i2c.campaigngenservice.vo.*;
import com.i2c.campaigngenservice.util.CommonUtilities;
import com.i2c.campaigngenservice.util.Constants;
import com.i2c.campaigngenservice.util.LoadProperties;
//import com.i2c.services.execution.task.campaigns.datecalc.CampaignDateCalculationResponse;
//import com.i2c.services.execution.task.campaigns.datecalc.CampaignDateCalculator;
//import com.i2c.services.execution.task.campaigns.datecalc.CampaignDateInfoObj;
import com.i2c.utils.logging.I2cLogger;
import java.sql.Connection;
import com.i2c.campaigngenservice.util.DatabaseHandler;
import java.sql.*;
import com.i2c.campaigngenservice.bl.CampaignGenerationBL;

public class CampGenMain {

    public static Logger lgr;
    private static long lastUpdateTime = -1;
    private Connection dbConn = null;
    private String instanceName = "mcp";

    public CampGenMain() {

    }


    public void populateInsts() {
        try {
            Connection mainConn = null;
            Class.forName("com.informix.jdbc.IfxDriver");
            mainConn = DriverManager.getConnection(
                    "jdbc:informix-sqli://192.168.0.16:9001/cards:informixserver=ids_mcp",
                    "mcpdev", "i2c/mcp");
//            mainConn.setAutoCommit(false);
            System.out.println("\n\n\n\n\n-----------CAMP GEN QUERY STARTED------->\n\n\n");
            CampaignGenerationBL campGenBl = new CampaignGenerationBL(instanceName, mainConn);
//            campGenBl.copyCampaignToInsts(137);
            campGenBl.populateCampInstDetails(137);
            System.out.println("\n\n\n\n\n-----------CAMP GEN QUERY-------->\n" +
                               campGenBl.recipientCardQueryBuilder(137));

        } catch (Exception e) {
            System.out.println("Exception in populateInsts-----> " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Method for loading the initial settings
     * @return: true or false
     * @throws IOException
     */
    public static boolean loadInitSetting() throws IOException {
        boolean firstTime = false;

        try {
            System.out.println("Load Application Settings ");
            String filePath = System.getProperty("user.dir") + File.separator +
                              Constants.CONFIGURATION_FILE;
            System.out.println("The Config File Path is -->" + filePath);

            if (isUpdated(filePath)) {
                System.out.println(" Enter in Loading Values from Config File ");
                LoadProperties.loadInfo(filePath);
                System.err.println("Logger configuration is: \n\t" +
                		Constants.LOG_FILE_PATH+File.separator+Constants.LOG_FILE_NAME+"-%g.log\n\t"+
                    Constants.LOG_FILE_SIZE+"\n\t"+
                    Constants.LOG_FILE_NO+"\n\t"+
                    Constants.LOG_CONTEXT_NAME);
                lgr = I2cLogger.getInstance(Constants.LOG_FILE_PATH +
                                            File.separator +
                                            Constants.LOG_FILE_NAME + "-%g.log",
                                            Constants.LOG_FILE_SIZE,
                                            Constants.LOG_FILE_NO,
                                            Constants.LOG_CONTEXT_NAME);
//            System.out.println(Constants.LOG_FILE_PATH+File.separator+Constants.LOG_FILE_NAME+"-%g.log");
//                                        "----"+Constants.LOG_FILE_SIZE, Constants.LOG_FILE_NO +"---"+ Constants.LOG_CONTEXT_NAME);

                if(lgr==null){javax.swing.JOptionPane.showMessageDialog(null, "mer gaye oae!");System.exit(0);}

                lgr.log(I2cLogger.CONFIG,
                        " Constants.DB_CONNECTION_STRING-" +
                        Constants.DB_CONNECTION_STRING + "_");
                lgr.log(I2cLogger.CONFIG,
                        " Constants.DB_USER_NAME-" + Constants.DB_USER_NAME +
                        "_");
                lgr.log(I2cLogger.CONFIG,
                        " Constants.DB_USER_PASSWORD-" +
                        Constants.DB_USER_PASSWORD + "_");
                lgr.log(I2cLogger.CONFIG,
                        " Constants.DB_DRIVER-" + Constants.DB_DRIVER_NAME +
                        "_");
                lgr.log(I2cLogger.CONFIG,
                        " Constants.MAX_ACTIVE_THREADS-" +
                        Constants.MAX_ACTIVE_THREADS + "_");
                lgr.log(I2cLogger.CONFIG,
                        " Constants.DEFAULT_SLEEP_TIME-" +
                        Constants.DEFAULT_SLEEP_TIME + "_");
                lgr.log(I2cLogger.CONFIG,
                        " Constants.LOG_FILE_SIZE-" + Constants.LOG_FILE_SIZE +
                        "_");
                lgr.log(I2cLogger.CONFIG,
                        " Constants.LOG_FILE_NO-" + Constants.LOG_FILE_NO + "_");
                lgr.log(I2cLogger.CONFIG,
                        " Constants.LOG_FILE_PATH-" + Constants.LOG_FILE_PATH +
                        "_");
                lgr.log(I2cLogger.CONFIG,
                        " Constants.LOG_FILE_NAME-" + Constants.LOG_FILE_NAME +
                        "_");


            } else {
                System.out.println(
                        "Config File is not Updated. No Need for Loading again ");
            } //end else

//--------------- Set the MACHINE IP here so it is avaiable throughout the application
            if (Constants.getMachineIP() == null) {
                Constants.setMachineIP();
            }

        } catch (IOException e) {
            System.out.println(
                    "Unable to Load the Settings. Server is unable to start/continue -->" +
                    e);
            throw e;
        } //end catch
        return true;
    }

    /**
     * Checking the configuration file is update or not
     * @param fileName: configuration file
     * @return: changed or not
     */
    public static boolean isUpdated(String fileName) {
        try {
            File configFile = new File(fileName);
            System.out.println(
                    "Checking Config File Updation lastUpdateTime -->" +
                    lastUpdateTime + " New Time -->" +
                    configFile.lastModified());

            if (!configFile.exists()) {
                return true;
            }

            if (lastUpdateTime != configFile.lastModified()) {
                lastUpdateTime = configFile.lastModified();
                return true;
            } //end if
        } catch (Exception ex) {
            System.out.println(
                    "Exception in checking updation of Config file -->" +
                    ex);
            return false;
        }
        return false;
    }

    public static void main(String[] args) {
//        String str = com.i2c.campaigngenservice.util.WildCardQuery.getSQL("cards.card_prg_id", "SSCPA|!=001");
//        System.out.println("QBE: " + str);
        CampGenMain campGen = new CampGenMain();
        try {
            loadInitSetting();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // to execute the camp gen api
        campGen.populateInsts();

    }

}
