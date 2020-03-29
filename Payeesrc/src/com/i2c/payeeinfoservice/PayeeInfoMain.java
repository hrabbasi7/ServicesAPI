package com.i2c.payeeinfoservice;

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
import org.exolab.castor.builder.SourceGenerator;

import com.i2c.payeeinfoservice.vo.*;
import com.i2c.payeeinfoservice.util.Constants;
import com.i2c.payeeinfoservice.util.LoadProperties;
import com.i2c.utils.logging.I2cLogger;
import com.i2c.payeeinfoservice.xao.pe.BILRMASK;
import com.i2c.payeeinfoservice.mapper.PayeeMapper;
import java.sql.Connection;
import com.i2c.payeeinfoservice.util.DatabaseHandler;
import java.sql.*;
import com.i2c.payeeinfoservice.mapper.PayeeInfoInterface;
import com.i2c.payeeinfoservice.mapper.SyncPayeeInfo;



// instantiate the SourceGenerator
//            SourceGenerator srcGen = new SourceGenerator();
// call the generateSource method to generate the Object-Model from the
// the schema and put it in a package
//            srcGen.generateSource("Person.xsd", "bindtest");
// read the xml document
//            FileReader reader = new FileReader(fileName);
//UNMARSHAL the xml document
//            BILRMASK billerMain = (BILRMASK) BILRMASK.unmarshal(reader);
// do some processing on the java object
//           System.out.println("Biller Count -- > " + billerMain.getBILLERCount());
// instantiate a writer with the output xml document
//            FileWriter writer = new FileWriter("genXML.xml");
//MARSHAL the java object to the xml document
//            person.marshal(writer);


public class PayeeInfoMain {

    public static Logger lgr;
    private static long lastUpdateTime = -1;
    private Connection dbConn = null;
    private String instanceName = "mcp";
//    private String fileName =
//    "D:/Haroon/BillPayment/Payee_Files/card_instances/Download/epiclist.ra.mask.childid.xml";

    public PayeeInfoMain() {
//        try {
//            dbConn = DatabaseHandler.getConnection("PayeeInfoMain",
//                    instanceName);
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
    }


    public void parsePayee() {
        try {
            dbConn = DatabaseHandler.getConnection("PayeeInfoMain", instanceName);
            Constants.BILLER_XML_FILE_PATH = Constants.PAYEE_INFO_FILE_PATH +
                                             File.separator +
                                             Constants.PAYEE_INFO_FILE_NAME;
            System.out.println("Payee Info File Path------> " + Constants.BILLER_XML_FILE_PATH);
            PayeeMapper mapper = new PayeeMapper(instanceName, dbConn ,
                                                 Constants.LOG_FILE_PATH,
                                                 Constants.BILLER_XML_FILE_PATH,
                                                 lgr);
            mapper.payeePopulater();
//            PayeeVO payeeVo = null;
//
//            payeeVo = new PayeeVO();
//            payeeVo.setPayeeId("501888");
//            payeeVo.setPayeeName("1401 CONDOMINIUM ASSOCIATION");
//            payeeVo.setPayeeRegion("R");
//            payeeVo.setPayeeStatus("Y");
//            payeeVo.setPayeeAddressFlag("N");
//
//            SyncPayeeInfo abc = new SyncPayeeInfo(instanceName,
//                                                  dbConn, "D:\\", lgr);
//
//            abc.updatePayeeData(payeeVo);

        } catch (Exception e) {
            System.out.println("Exception in nparspayyeee-----> " + e.toString());
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
                lgr = I2cLogger.getInstance(Constants.LOG_FILE_PATH +
                                            File.separator +
                                            Constants.LOG_FILE_NAME + "-%g.log",
                                            Constants.LOG_FILE_SIZE,
                                            Constants.LOG_FILE_NO,
                                            Constants.LOG_CONTEXT_NAME);
//            System.out.println(Constants.LOG_FILE_PATH+File.separator+Constants.LOG_FILE_NAME+"-%g.log");
//                                        "----"+Constants.LOG_FILE_SIZE, Constants.LOG_FILE_NO +"---"+ Constants.LOG_CONTEXT_NAME);
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
        PayeeInfoMain mainPayee = new PayeeInfoMain();
        try {
            loadInitSetting();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        mainPayee.parsePayee();
        ////////////////////////////////
    }
}
