package com.i2c.services.test.helper;

import com.i2c.services.ServicesRequestObj;
import com.i2c.services.exceptions.InvalidConfigException;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

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
public class InputReader {

    private String inputFilePath;
    private Properties prop;
    private FileInputStream inputStream = null;




    public InputReader(String filePath) throws Exception {

        inputFilePath = filePath;
        prop = new Properties();

        try {

            inputStream = new FileInputStream(inputFilePath);
            // load properties
            prop.load(inputStream);

        } catch(Exception exp) {
            exp.printStackTrace();
            throw exp;
        }
    }




    public void close() throws Exception {
        if(inputStream != null) {
            inputStream.close();
        }
    }




    /**
     * @throws InvalidConfigException
     * @return String[]
     */
    public String[] getDbInformation() throws InvalidConfigException {

        String[] dbInfo = new String[4];

        // get driver information
        String property = "connection.info.driver";
        checkProperty(property);
        dbInfo[0] = prop.getProperty(property);

        // get connection String
        property = "connection.info.string";
        checkProperty(property);
        dbInfo[1] = prop.getProperty(property);

        // get db user name
        property = "connection.info.user";
        checkProperty(property);
        dbInfo[2] = prop.getProperty(property);

        // get db user password
        property = "connection.info.password";
        checkProperty(property);
        dbInfo[3] = prop.getProperty(property);

        return dbInfo;

    }// end method




    /**
     * @return
     * @throws InvalidConfigException
     */
    public String getLogPath() throws InvalidConfigException {

        String property = "test.logpath";
        // check property
        checkProperty(property);

        return prop.getProperty(property);

    }




    /**
     * @return
     * @throws InvalidConfigException
     */
    public ArrayList<ServicesRequestObj> getServicesRequestObjList() {

        ArrayList<ServicesRequestObj> list = new ArrayList<ServicesRequestObj>();

        for(int i = 1;; i++) {

            String key1 = "test" + i + ".";

            String property = key1 + "services.cardNo";

            if(prop.getProperty(property) == null) {
                break;
            }

            ServicesRequestObj obj = new ServicesRequestObj();

            // set card no
            obj.setCardNo(prop.getProperty(property));

            property = key1 + "services.expiryDate";
            // set expiry date
            obj.setExpiryDate(prop.getProperty(property));

            property = key1 + "services.aac";
            // set AAC
            obj.setAAC(prop.getProperty(property));

            property = key1 + "services.pin";
            obj.setPin(prop.getProperty(property));

            property = key1 + "services.accountNo";
            obj.setAccountNo(prop.getProperty(property));

            property = key1 + "services.bankAccNo";
            obj.setBankAcctNo(prop.getProperty(property));

            property = key1 + "services.bankAccTitle";
            obj.setBankAcctTitle(prop.getProperty(property));

            property = key1 + "services.bankAccType";
            obj.setBankAcctType(prop.getProperty(property));

            property = key1 + "services.routingNo";
            obj.setBankRoutingNo(prop.getProperty(property));

            property = key1 + "services.achAccountNo";
            obj.setAchAccountNo(prop.getProperty(property));

            property = key1 + "services.amount";
            obj.setAmount(prop.getProperty(property));

            property = key1 + "services.applyFee";
            obj.setApplyFee(prop.getProperty(property));

            property = key1 + "services.deviceType";
            obj.setDeviceType(prop.getProperty(property));

            property = key1 + "services.deviceId";
            obj.setDeviceType(prop.getProperty(property));

            property = key1 + "services.cardAcceptorCode";
            obj.setCardAcceptorId(prop.getProperty(property));

            property = key1 + "services.cardAcceptorNameAndLoc";
            obj.setCardAcceptNameAndLoc(prop.getProperty(property));

            property = key1 + "services.mcc";
            obj.setMcc(prop.getProperty(property));

            property = key1 + "services.nickName";
            obj.setNickName(prop.getProperty(property));

            property = key1 + "services.bankName";
            obj.setBankName(prop.getProperty(property));

            property = key1 + "services.bankAddress";
            obj.setBankAddress(prop.getProperty(property));

            list.add(obj);

        }// end for

        return list;
    }




    /**
     * @param property
     *        String
     * @throws InvalidConfigException
     */
    public void checkProperty(String property) throws InvalidConfigException {
        if(prop.getProperty(property) == null
                || prop.getProperty(property).trim().equals("")) {
            throw new InvalidConfigException("Property " + property
                    + ") not defined");
        }
    }
}// end class
