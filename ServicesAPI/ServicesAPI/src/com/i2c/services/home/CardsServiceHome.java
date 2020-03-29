package com.i2c.services.home;

import com.i2c.services.util.*;
import com.i2c.services.base.*;
import com.i2c.services.*;
import com.i2c.api.hsm.HSMResponse;
import com.i2c.auth.MessageInfoObj;
import com.i2c.chauth.mgr.ChAuthManager;
import com.i2c.chauth.vo.RecordAuthInfoObj;
import com.i2c.utils.emailing.I2cEmailer;
import com.i2c.service.hsm.HSMService;
import com.sun.mail.iap.Response;

import java.sql.*;
import java.util.*;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 * Title:CardsServiceHome: A class which provides card related services
 * </p>
 * <p>
 * Description: This class provides the database related operations for Cards
 * such as uploading funds into the given card account
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore
 * </p>
 * <p>
 * Company: I2c Inc
 * </p>
 *
 * @author MCP Backend Team
 * @version 1.0
 */

public class CardsServiceHome extends ServicesBaseHome {

    /**
     * Private Constructor which simply creates CardServiceHome class Object.
     */
    private CardsServiceHome() {
    }




    /**
     * Factory method which creates the instance of the CardsServiceHome object
     * and returns it.
     *
     * @param _con
     *        Connection
     * @return CardsServiceHome
     */

    public static CardsServiceHome getInstance(Connection _con) {
        CardsServiceHome home = new CardsServiceHome();

        home.con = _con;
        return home;
    }




    /**
     * This method is used to check the validity of given card. It calls the
     * "CHECK_CARD" procedure for performing the validation of the card. After
     * calling the procedure this method builds the response object according to
     * the response received from procedure and returns it to the calling
     * method. In case of any error during processing it throws exception which
     * describes the processing status in detail with appropiate response code
     * and description.
     *
     * @param cardNo
     *        String -- Card NO
     * @param applyFee
     *        boolean -- Whether to apply the fee or not
     * @param serviceId
     *        String -- Service Id against which fee will be applied
     * @param skipStatuses
     *        String -- Statuses of card to skip the validation
     * @throws Exception
     * @return ServicesResponseObj -- Response information
     */
    public ServicesResponseObj checkCardPreReqs(String cardNo,
            boolean applyFee, String serviceId, String skipStatuses)
            throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Card No -- > " + cardNo);
        CallableStatement cstmt = null;
        ResultSet rs = null;

        try {

            // build the query to check the existance of the card in the MCP DB
            String cardQuery = "Execute procedure CHECK_CARD('" + cardNo + "'";

            if(applyFee)
                cardQuery += ",null,0.0,null,'" + serviceId + "',1,'"
                        + (skipStatuses != null ? skipStatuses : "B") + "')";

            else
                cardQuery += ",null,0.0,null,'" + serviceId + "',0,'"
                        + (skipStatuses != null ? skipStatuses : "B") + "')";

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query to check card  -- > " + cardQuery);

            // prepare the callable statement
            cstmt = con.prepareCall(cardQuery);
            // execute the query
            rs = cstmt.executeQuery();
            if(rs.next()) {
                // get the response code
                String respCode = rs.getString(1);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response Code -- > " + rs.getString(1));

                // get the response description
                String respDesc = rs.getString(2);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response Description -- > " + rs.getString(2));

                String debitFee = rs.getString(3);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Debit Fee -- > " + debitFee);

                respObj.setRespCode(respCode);
                respObj.setRespDesc(respDesc);
                // if(respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                // respObj.setFeeAmount(debitFee);
            } // end if
            // return the responseObject
            return respObj;
        } // end try
        catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    " Exception -- > " + ex.getMessage());
            // throw back the exception
            throw ex;
        } // end catch
        finally {
            if(rs != null)
                rs.close();
            if(cstmt != null)
                cstmt.close();
        } // end finally
    } // end checkCardPreReqs




    /**
     * This method activates the card. It first checks whether the application
     * of the service fee is allowed or not. If the application of the service
     * fee is allowed then it sets the service fee amount to one else zero.
     * After this it calls the database stored procedure ACTIVATE_CARD for
     * changing the status of the card to active. Response object is built using
     * the response received from the procedure and returned to the user of the
     * service. If an error occurs during the processing this method throws
     * excpetion which describes the reason of the error with appropiate
     * resposne code and response description.
     *
     * @param cardNo
     *        String -- Card No
     * @param applyFee
     *        boolean -- Apply Fee or Not
     * @param serviceId
     *        String -- Service Id to apply the Fee
     * @throws Exception
     * @return ServicesResponseObj -- Response Info
     */

    public ServicesResponseObj activateCardInOltp(ServicesRequestObj reqObj)
            throws Exception {
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "activateCardInOltp --- Arguments Received :: Card No -- > "
                        + reqObj.getCardNo() + "\n" + "<--ApplyFee -- > "
                        + reqObj.getApplyFee() + "\n" + "<--ServiceId -- > "
                        + reqObj.getServiceId() + "<--DeviceType -- > "
                        + reqObj.getDeviceType() + "<--Amount -- > "
                        + reqObj.getAmount() + "<--AcquirerId -- > "
                        + reqObj.getAcquirerId() + "<--AcqData1 -- > "
                        + reqObj.getAcqData1() + "<--AcqData1 -- > "
                        + reqObj.getAcqData2() + "<--AcqData1 -- > "
                        + reqObj.getAcqData3() + "<--AcqUsrId -- > "
                        + reqObj.getAcqUsrId() + "<--CardAcceptorId -- > "
                        + reqObj.getCardAcceptorId()
                        + "<--CardAcceptNameAndLoc -- > "
                        + reqObj.getCardAcceptNameAndLoc() + "<--Mcc -- > "
                        + reqObj.getMcc() + "<--DeviceId -- > "
                        + reqObj.getDeviceId() + "<--RetreivalRefNum -- > "
                        + reqObj.getRetreivalRefNum());

        // prepare the query
        StringBuffer query = new StringBuffer();

        CallableStatement cstmt = null;
        ResultSet rs = null;

        try {

            String tempApplyFee = reqObj.getApplyFee();
            String applyFee = null;
            if(reqObj.getApplyFee() != null
                    && reqObj.getApplyFee().trim().equalsIgnoreCase("Y")) {
                applyFee = "1";
            } else if(reqObj.getApplyFee() != null
                    && reqObj.getApplyFee().trim().equalsIgnoreCase("N")) {
                applyFee = "0";
            } else {
                applyFee = "1"; // By default
            }

            query
                    .append("execute procedure activate_card(pcardno= ?,pservice_id= ?,papply_fee= ?,pdevice_type= ?,pamount= ?, pacq_id= ?,psub_srv= ?, pdata_2= ? , pdata_3= ? , pacq_userid= ? , pcard_acptr_code= ? , pcard_acptr_name= ? , pmcc= ? , pdev_id= ? , pretrieval_ref= ? )");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query -- > " + query);

            cstmt = con.prepareCall(query.toString());

            cstmt.setString(1, reqObj.getCardNo());
            cstmt.setString(2, reqObj.getServiceId());
            cstmt.setString(3, applyFee);
            cstmt.setString(4, reqObj.getDeviceType());
            cstmt.setString(5, reqObj.getAmount());
            cstmt.setString(6, reqObj.getAcquirerId());
            cstmt.setString(7, reqObj.getAcqData1());
            cstmt.setString(8, reqObj.getAcqData2());
            cstmt.setString(9, reqObj.getAcqData3());
            cstmt.setString(10, reqObj.getAcqUsrId());
            cstmt.setString(11, reqObj.getCardAcceptorId());
            cstmt.setString(12, reqObj.getCardAcceptNameAndLoc());
            cstmt.setString(13, reqObj.getMcc());
            cstmt.setString(14, reqObj.getDeviceId());
            cstmt.setString(15, reqObj.getRetreivalRefNum());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to execute the query...");

            rs = cstmt.executeQuery();
            ServicesResponseObj respObj = new ServicesResponseObj();
            if(rs.next()) {
                // get the response code and description
                String respCode = rs.getString(1);
                String transId = rs.getString(2);
                String cardBal = rs.getString(3);
                String feeAmount = rs.getString(4);
                String respDesc = rs.getString(5);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response Code -- > " + respCode
                                + " && Resp Desc -- > " + respDesc);

                respObj.setRespCode(respCode);
                respObj.setRespDesc(respDesc);
                if(respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    respObj.setTransId(transId);
                    respObj.setCardBalance(cardBal);
                    if(tempApplyFee.equalsIgnoreCase("Y")) {
                        respObj.setFeeAmount(feeAmount);
                    }
                } // end if
            } // end if
            return respObj;
        } // end try
        catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage());
            throw ex;
        } // end catch
        finally {
            try {
                if(rs != null)
                    rs.close();
                if(cstmt != null)
                    cstmt.close();
            } // end try
            catch(Exception ex) {
            } // end catch
        } // end finally
    } // end activateCardInOltp




    /**
     * This method is used to add the funds in given card no. It first calls the
     * database stored procedure "UPDATE_FUNDS" for adding the funds to the
     * given card no with given amount. Response object is built using the
     * response received from the procedure and returned to the user of the
     * service. If an error occurs during the processing this method throws
     * excpetion which describes the reason of the error with appropiate
     * response code and response description.
     *
     * @param cardNo
     *        String
     * @param amount
     *        String
     * @throws Exception
     */
    public ServicesResponseObj addFundsInCard(String cardNo, String amount)
            throws Exception {
        ServicesResponseObj respObj = new ServicesResponseObj();

        String query = "Execute procedure update_funds(" + "pcard_no='"
                + cardNo + "'," + "ptrans_amount=" + amount + ","
                + "pservice_id='SW_DEPOSIT'" + "," + "pmti='0200'" + ","
                + "presp_code='00')";

        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG), "Query -->" + query);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareCall(query);
            rs = pstmt.executeQuery();

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query Executed...");

            if(rs.next()) {
                String respCode = rs.getString(1);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response Code --> " + respCode);

                String respDesc = rs.getString(2);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response Desc --> " + respDesc);

                respObj.setRespCode(respCode);
                respObj.setRespDesc(respDesc);
            } // end if
            return respObj;
        } // end try
        catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception --> " + ex.getMessage());
            throw ex;
        } // end catch
        finally {
            if(rs != null)
                rs.close();
            if(pstmt != null)
                pstmt.close();
        } // end finally
    } // end addFundsInCard




    /**
     * This method is used to deactivate the card at OLTP. It first checks
     * whether given card exists in the database. If given card no does not
     * exist then it returns response with response code 56 which indicates that
     * card no does not exist in the database. It then checks the existing
     * status of the card, if the status of the card is "A" or "I" then it mean
     * that card is alreay inactive. In case of alrdeady inactive card it return
     * reponse code 06. It then checks the no of Non_Fee transactions if the
     * amount of Non_Fee transaction is zero then it sets the card status to "A"
     * else "I". At the end of the processing Response Object is returned to the
     * client which describes the status of the processing with response code
     * and response description. If an error occurs during the processing this
     * method throws excpetion which describe the cause of error.
     *
     * @param cardNo
     *        String -- Card No to deactivate
     * @throws Exception
     * @return ServicesResponseObj -- Response information
     */
    public ServicesResponseObj deactivateCard(String cardNo) throws Exception {
        try {
            ServicesResponseObj respObj = new ServicesResponseObj();
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Card No Received -- > " + cardNo);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Checking card existance in db");
            // check for card existance
            String result = getValue("select card_no from cards where card_no='"
                    + cardNo + "'");
            if(result == null) {
                respObj.setRespCode("56");
                respObj.setRespDesc("Card No(" + cardNo.trim()
                        + ") does not exist");
                return respObj;
            }
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting the card status....");
            String status = getValue("select card_status_pos from cards where card_no='"
                    + cardNo + "'");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Card Status Found -- > " + status);
            if(status != null
                    && (status.trim().equalsIgnoreCase("A") || status.trim()
                            .equalsIgnoreCase("I"))) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Card is already inactive...");
                respObj.setRespCode("06");
                respObj.setRespDesc("Card No(" + cardNo.trim()
                        + ") is already inactive");
                return respObj;
            } // end if

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Checking for any non-fee transaction on card....");
            // get the non-fee transaction count
            String count = getValue("select count(*) from trans_requests where card_no='"
                    + cardNo.trim() + "' and iso_message_type <> '0722'");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Total Non-Fee transactions on card -- > " + count);

            String deActQry = "update cards set ";
            // check whether any trans found?
            if(count == null || count.equalsIgnoreCase("0")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "No Non-Fee Transaction found for card....");
                deActQry += "card_status_atm = 'A',card_status_pos='A' ";
            } // end if
            else {
                deActQry += "card_status_atm = 'I',card_status_pos='I' ";
            } // end if

            // append where clause
            deActQry += " where card_no='" + cardNo + "'";

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Deactivating card...");
            // execute the query to deactive the card
            executeQuery(deActQry);
            // populate the response object with success message
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            return respObj;
        } // end try
        catch(Exception exp) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage());
            throw exp;
        } // end catch
    } // deactivateCard




    /**
     * This methos is used to reset the card access code. It first generates new
     * access code for the given card no. It then updates the access code of the
     * given card no and returns the access code to the calling client. If an
     * error occurs during processing then this method throws exception which
     * describes the cause of the error.
     *
     * @param cardNo
     *        String -- Card No to resset the access code
     * @throws Exception
     * @return String -- The new AAC generated and reset
     */
    public String resetAccessCode(String cardNo) throws Exception {
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Received Card No -- > " + cardNo);
        try {
            String accessCode = generateAccessCode(cardNo);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Access Code generated -- > " + accessCode);
            // set the generated access code in card no
            executeQuery("update cards set card_access_code='" + accessCode
                    + "' where card_no='" + cardNo + "'");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Access code reset successfully....");
            // return the new access code
            return accessCode;
        } // end try
        catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage());
            throw ex;
        } // end catch
    } // end resetAccessCode




    /**
     * This method generates the access code according to the card program
     * definition for the given card no. The method first gets the access code
     * generation method based on the card program id of the given card no. The
     * card access code may be one of the following. <br>
     * 1. Expiry date of the given card no. <br>
     * 2. Last four digit of the card no.<br>
     * 3. Social Security Number <br>
     * 4. Can be four zeros (0000) <br>
     * 5. Last 4 digits of Postal Code <br>
     * According to the Access Code generation method, access code is generated
     * or fetched from the database and returned to the user.
     *
     * @param cardNo
     *        String -- Card No
     * @return String -- New AAC
     */
    public String generateAccessCode(String cardNo) {
        // make the query to get the method to generate access code
        String query = "select p.gen_access_code from cards c, card_programs p where p.card_prg_id=c.card_prg_id and c.card_no='"
                + cardNo + "'";
        String generateMethod = null;
        String information = null;
        try {
            CommonUtilities
                    .getLogger()
                    .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Getting the access code generation method from card program....");
            // get the access method
            generateMethod = getValue(query);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Access Method -- > " + generateMethod);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Generating Access Code.......");

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Generating the Access Code.. Method -- > "
                            + generateMethod);
            // Filter Condition
            if(generateMethod != null) {
                // Calculating Expiry Date
                if(generateMethod
                        .equalsIgnoreCase(Constants.GENERETAE_METHOD_EXPIRT_DATE)) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Getting the Expiry Date....");
                    // get the expiry date
                    String expDate = getValue("select expiry_on from cards where card_no='"
                            + cardNo + "'");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Expiry Date -- > " + expDate);
                    if(expDate != null)
                        information = CommonUtilities.convertDateFormat("MMyy",
                                Constants.SOLSPARK_DATE_TIME_FORMAT, expDate);
                } // end if

                // Calculating Last 4 Card Number
                else if(generateMethod
                        .equalsIgnoreCase(Constants.GENERETAE_METHOD_LAST_4_CARD_NUMBER)
                        && cardNo != null) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Calculating last 4 digits of Card No....");
                    // calculate last 4 digits of card no
                    information = cardNo.substring(cardNo.length() - 4, cardNo
                            .length());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Last 4 Digits Calculated -- > " + information);
                } // end else if

                // Calculating Last 4 SSN
                else if(generateMethod
                        .equalsIgnoreCase(Constants.GENERETAE_METHOD_LAST_4_SSN)) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Getting the SSN of Card Holder....");
                    // get the SSN
                    String ssn = getValue("select ssn_nid_no from cards where card_no='"
                            + cardNo + "'");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "SSN Got -- > " + ssn);
                    if(ssn != null) {
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Calculating last 4 digits of SSN....");
                        // get the last 4 digits of SSN
                        information = ssn.substring(ssn.length() - 4, ssn
                                .length());
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Last 4 digits of SSN Calculated -- > "
                                        + information);
                    } // end if
                } // end else if

                // Calculating Last 4 Zip/Postal Code
                else if(generateMethod
                        .equalsIgnoreCase(Constants.GENERETAE_METHOD_LAST_4_ZIP_POSTAL)) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Getting the billing zip code of card...");
                    // get the bill zip
                    String billZip = getValue("select bill_zip_code from cards where card_no='"
                            + cardNo + "'");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Got the billing zip -- > " + billZip);

                    if(billZip != null) {
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Calculating the last 4 digits of Bill Zip...");
                        // get the last 4 digits of billing address
                        information = billZip.substring(billZip.length() - 4,
                                billZip.length());
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Calculated last 4 digits of Bill Zip -- > "
                                        + information);
                    } // end if
                } // end else if

                // Calculating Last 4 0
                else if(generateMethod
                        .equalsIgnoreCase(Constants.GENERETAE_METHOD_ZERO)) {
                    information = "0000";
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Access Code Generated -- > " + information);
                } // end else if
            } // end if
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Final Access Code generated -- > " + information);
        } catch(Exception e) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception in generating access code -- > "
                            + e.getMessage());
        } // end catch
        return information;
    } // end method




    /**
     * This method generates the PIN according to the PIN generation method and
     * card program id of the given card no. The method first checks the PIN
     * geneartion method, generates the PIN and returns the PIN to the client.
     * The PIN generation method may be one of the following. <br>
     * 1. Expiry date of the given card no. <br>
     * 2. Last four digit of the card no.<br>
     * 3. Social Security Number <br>
     * 4. Can be four zeros (0000) <br>
     * 5. Last 4 digits of Postal Code <br>
     * According to the PIN generation method, PIN is generated or fetched from
     * the database and returned to the user.
     *
     * @param cardNo
     *        String -- Card No
     * @param generateMethod --
     *        PIN Generation Method
     * @return String -- New AAC
     */
    public String generatePIN(String cardNo, String generateMethod) {

        String information = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Args:: Card No -- > " + cardNo
                            + " && PIN Generation Method -- > "
                            + generateMethod);
            // Filter Condition
            if(generateMethod != null) {
                // Calculating Expiry Date
                if(generateMethod
                        .equalsIgnoreCase(Constants.GENERETAE_METHOD_EXPIRT_DATE)) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Getting the Expiry Date....");
                    // get the expiry date
                    String expDate = getValue("select expiry_on from cards where card_no='"
                            + cardNo + "'");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Expiry Date -- > " + expDate);
                    if(expDate != null)
                        information = CommonUtilities.convertDateFormat("MMyy",
                                Constants.DATE_FORMAT, expDate);
                } // end if

                // Calculating Last 4 Card Number
                else if(generateMethod
                        .equalsIgnoreCase(Constants.GENERETAE_METHOD_LAST_4_CARD_NUMBER)
                        && cardNo != null) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Calculating last 4 digits of Card No....");
                    // calculate last 4 digits of card no
                    information = cardNo.substring(cardNo.length() - 4, cardNo
                            .length());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Last 4 Digits Calculated -- > " + information);
                } // end else if

                // Calculating Last 4 SSN
                else if(generateMethod
                        .equalsIgnoreCase(Constants.GENERETAE_METHOD_LAST_4_SSN)) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Getting the SSN of Card Holder....");
                    // get the SSN
                    String ssn = getValue("select ssn_nid_no from cards where card_no='"
                            + cardNo + "'");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "SSN Got -- > " + ssn);
                    if(ssn != null) {
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Calculating last 4 digits of SSN....");
                        // get the last 4 digits of SSN
                        information = ssn.substring(ssn.length() - 4, ssn
                                .length());
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Last 4 digits of SSN Calculated -- > "
                                        + information);
                    } // end if
                } // end else if

                // Calculating Last 4 Zip/Postal Code
                else if(generateMethod
                        .equalsIgnoreCase(Constants.GENERETAE_METHOD_LAST_4_ZIP_POSTAL)) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Getting the billing zip code of card...");
                    // get the bill zip
                    String billZip = getValue("select bill_zip_code from cards where card_no='"
                            + cardNo + "'");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Got the billing zip -- > " + billZip);

                    if(billZip != null) {
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Calculating the last 4 digits of Bill Zip...");
                        // get the last 4 digits of billing address
                        information = billZip.substring(billZip.length() - 4,
                                billZip.length());
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Calculated last 4 digits of Bill Zip -- > "
                                        + information);
                    } // end if
                } // end else if

                // Calculating Last 4 0
                else if(generateMethod
                        .equalsIgnoreCase(Constants.GENERETAE_METHOD_ZERO)) {
                    information = "0000";
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Calculated PIN -- > " + information);
                } // end else if
                // Same as Access Code
                else if(generateMethod
                        .equalsIgnoreCase(Constants.GENERETAE_METHOD_SAME_AS_ACCESS_CODE)) {
                    CommonUtilities
                            .getLogger()
                            .log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Calculating PIN... Method --> Same as Access Code... Calculating Access Code..");
                    // generate Access Code
                    information = generateAccessCode(cardNo);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Access Code (so as PIN) calculated -- > "
                                    + information);
                } // end else if
            } // end if
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Final PIN generated -- > " + information);
        } catch(Exception e) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception in generating PIN -- > " + e.getMessage());
        } // end catch
        return information;
    } // end method




    /**
     * This method returns the PIN Generation method for the given card no. PIN
     * generation method is fetched based on the card program of the given card
     * no. It queries the database table "card_programs" to get the PIN
     * generation method. In case of any error during processing it throws
     * exception to the client which describes the cause of the error.
     *
     * @param cardNo
     *        String -- Card No
     * @throws Exception
     * @return String -- Required PIN
     */
    public String getPINGenerationMethod(String cardNo) throws Exception {
        // make the query to get the method to generate access code
        String query = "select p.gen_pin from cards c, card_programs p where p.card_prg_id=c.card_prg_id and c.card_no='"
                + cardNo + "'";

        try {
            CommonUtilities
                    .getLogger()
                    .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Getting the PIN code generation method from card program....");
            // get the access method
            String generateMethod = getValue(query);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "PIN Generation Method Got -- > " + generateMethod);
            // retrun the generate method
            return generateMethod;
        } // end try
        catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage());
            throw ex;
        } // end catch
    } // end getPINGenerationMethod




    /**
     * This method is used to check whether the given card attributes are valid
     * for the given card no. At the end of the processig it returns response
     * object which describes the status of the processing with appropiate
     * response code and description. It call the method "isCardInfoValid" for
     * checking the card attributes for their validation.
     *
     * @param cardNo
     *        String
     * @param AAC
     *        String
     * @param expDate
     *        String
     * @param accountNo
     *        String
     * @param PIN
     *        String
     * @param serviceId
     *        String
     * @param deviceType
     *        String
     * @param deviceId
     *        String
     * @param cardAcceptorId
     *        String
     * @param cardAcceptorNameAndLoc
     *        String
     * @param mcc
     *        String
     * @param acquirerId
     *        String
     * @throws Exception
     * @return ServicesResponseObj
     */

    public ServicesResponseObj isCardInfoValid(String cardNo, String AAC,
            String expDate, String accountNo, String PIN, String serviceId,
            String deviceType, String deviceId, String cardAcceptorId,
            String cardAcceptorNameAndLoc, String mcc, String acquirerId)
            throws Exception {
        return isCardInfoValid(cardNo, AAC, expDate, accountNo, PIN, true,
                serviceId, deviceType, deviceId, cardAcceptorId,
                cardAcceptorNameAndLoc, mcc, acquirerId);
    } // end isCardInfoValid




    /**
     * This method is used to check whether the given card attributes are valid
     * for the given card no. At the end of the processig it returns response
     * object which describes the status of the processing with appropiate
     * response code and description. The mehtod first checks the card no
     * whether is is null or empty. If it is then it returns the response code
     * 14 with proper description of the response code. It then checks whether
     * the given card no exist in the database. If it is not then it respond
     * with response code 14 which indicates that given card no does not exists
     * in the database. The method then compares the AAC with the card access
     * code fetched from the database. If AAC code mismatch with the card access
     * code then execution is paused and response is returned to the client.
     * Similarly expiry date and PIN offset of the given card are validated.
     * After inital validation the method calls the HSM Manager for the
     * validiation of the PIN of the given card no. If any error occurs during
     * the processing exception is returned to the client which describes the
     * error in full detail.
     *
     * @param cardNo
     *        String
     * @param AAC
     *        String
     * @param expDate
     *        String
     * @param accountNo
     *        String
     * @param PIN
     *        String
     * @param validatePIN
     *        boolean
     * @param serviceId
     *        String
     * @param deviceType
     *        String
     * @param deviceId
     *        String
     * @param cardAcceptorId
     *        String
     * @param cardAcceptorNameAndLoc
     *        String
     * @param mcc
     *        String
     * @param acquirerId
     *        String
     * @throws Exception
     * @return ServicesResponseObj
     */
    public ServicesResponseObj isCardInfoValid(String cardNo, String AAC,
            String expDate, String accountNo, String PIN, boolean validatePIN,
            String serviceId, String deviceType, String deviceId,
            String cardAcceptorId, String cardAcceptorNameAndLoc, String mcc,
            String acquirerId) throws Exception {
        // make response object
        ServicesResponseObj respObj = new ServicesResponseObj();

        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Args Received :: Card No -- > " + cardNo + "\n" + "AAC -- > "
                        + AAC + "\n" + "Exp Date -- > " + expDate + "\n"
                        + "Account No -- > " + accountNo + " && PIN --> " + PIN
                        + " && validatePIN-->" + validatePIN
                        + " && Service Id--> " + serviceId
                        + " && Device Type--> " + deviceType);
        if(cardNo == null || cardNo.trim().length() == 0) {
            respObj.setRespCode("14");
            respObj.setRespDesc("Invalid Card Number provided");
            return respObj;
        }

        try {
            String card = getValue("select card_no from cards where card_no='"
                    + cardNo + "'");
            if(card == null) {
                respObj.setRespCode("14");
                respObj.setRespDesc("Card No(" + maskCardNo(cardNo)
                        + ") does not exist");

                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Card No ("
                                        + cardNo
                                        + ") does not exist in the database::logging error transaction:: Code -- > "
                                        + respObj.getRespCode()
                                        + " && Desc -- > "
                                        + respObj.getRespDesc());
                // log the transaction
                String[] transIds = logTransaction(cardNo, serviceId,
                        deviceType, null, "0200", "0", respObj.getRespDesc(),
                        "0", respObj.getRespCode(), deviceId, cardAcceptorId,
                        cardAcceptorNameAndLoc, mcc, accountNo, acquirerId);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with ISO Serial No - > "
                                + transIds[0] + " && Trace Audit No -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if
            else if(AAC != null && !AAC.equalsIgnoreCase("")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating AAC...");
                // validate the aac
                String result = getValue("select card_access_code from cards where card_no='"
                        + cardNo + "' and card_access_code='" + AAC + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got AAC -- > " + result);
                if(result == null) {
                    respObj.setRespCode("EB");
                    respObj.setRespDesc("Invalid AAC supplied for Card("
                            + maskCardNo(cardNo) + ")");

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid AAC supplied for Card(" + cardNo + ")");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                } // end if
            } // end if

            else if(expDate != null && !expDate.equalsIgnoreCase("")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating Expiry Date...");
                // get the expiry date
                String result = getValue("select expiry_on from cards where card_no='"
                        + cardNo + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got Expiry Date -- > " + result);
                if(result != null) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Formatting expiry date to -- > "
                                    + Constants.EXP_DATE_FORMAT);
                    // format the expiry date
                    String expiryDate = CommonUtilities.convertDateFormat(
                            Constants.EXP_DATE_FORMAT, Constants.DATE_FORMAT,
                            result);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Expiry Date After Conversion -- > " + expiryDate);

                    if(!expiryDate.equalsIgnoreCase(expDate)) {
                        respObj.setRespCode("SB");
                        respObj
                                .setRespDesc("Invalid Card Expiry Date supplied for Card("
                                        + maskCardNo(cardNo) + ")");

                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Invalid Card Expiry Date supplied for Card("
                                        + cardNo + ")");
                        // log the transaction
                        String[] transIds = logTransaction(cardNo, serviceId,
                                deviceType, null, "0200", "0", respObj
                                        .getRespDesc(), "0", respObj
                                        .getRespCode(), deviceId,
                                cardAcceptorId, cardAcceptorNameAndLoc, mcc,
                                accountNo, acquirerId);

                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Transaction logged with ISO Serial No - > "
                                        + transIds[0]
                                        + " && Trace Audit No -- > "
                                        + transIds[1]);
                        // set the trans id in response
                        respObj.setTransId(transIds[0]);
                        return respObj;
                    } // end if
                } // end if
            } // end if

            else if(accountNo != null && !accountNo.trim().equalsIgnoreCase("")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating Account No...");
                // validate the account no
                String result = getValue("select a.account_number from cards c, card_accounts a where c.card_no = a.card_no and c.card_no='"
                        + cardNo + "' and a.account_number='" + accountNo + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Account No Got -- > " + result);
                if(result == null) {
                    respObj.setRespCode("EC");
                    respObj.setRespDesc("Invalid Account No Supplied for Card("
                            + maskCardNo(cardNo) + ")");

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid Account No Supplied for Card(" + cardNo
                                    + ")");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                } // end if
            } // end if
            else if(PIN != null && !PIN.trim().equalsIgnoreCase("")
                    && validatePIN) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating PIN....Getting PIN Offset...");
                // get the pin offset of card
                String pinOffset = getValue("select pin_offset from cards where card_no='"
                        + cardNo + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got PIN Offset -- > " + pinOffset);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating the PIN at HSM....");
                // validate the PIN at HSM
                HSMService manager = new HSMService(
                        Constants.HSM_LOG_FILE_PATH,
                        Constants.HSM_WRPR_FILE_PATH, con);
                HSMResponse hsmResp = manager.validatePin(cardNo, PIN, PIN
                        .length(), pinOffset, PIN.length());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response After PIN validation :: Code -- > "
                                + hsmResp.getResponseCode());

                if(!hsmResp.getResponseCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    respObj.setRespCode("55");
                    respObj.setRespDesc("Invalid PIN Supplied for Card("
                            + maskCardNo(cardNo) + ")");

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid PIN Supplied for Card(" + cardNo + ")");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                } // end if
            } // end esle if
            // Now the response is successsful
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            return respObj;
        } // end try
        catch(Exception exp) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage());
            throw exp;
        } // end catch
    } // end isCardInfoValid




    public ServicesResponseObj isCardInfoValid(String cardNo, String AAC,
            String expDate, String accountNo, String PIN, boolean validatePIN,
            String serviceId, String deviceType, String deviceId,
            String cardAcceptorId, String cardAcceptorNameAndLoc, String mcc,
            String acquirerId, String ssn, String phone, String dob,
            String zip, String securityCode, String dlNo, String dlState)
            throws Exception {
        // make response object
        ServicesResponseObj respObj = new ServicesResponseObj();

        CommonUtilities
                .getLogger()
                .log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Method for validating cardholder information----Args Received :: Card No -- > "
                                + cardNo
                                + "<---AAC ---> "
                                + AAC
                                + "<---Exp Date -- > "
                                + expDate
                                + "<---Account No -- > "
                                + accountNo
                                + "<---PIN --> "
                                + PIN
                                + "<---validatePIN--->"
                                + validatePIN
                                + "<---Service Id---> "
                                + serviceId
                                + "<---Device Type--> "
                                + deviceType
                                + "<---SSN---> "
                                + ssn
                                + "<---phone---> "
                                + phone
                                + "<---dob---> "
                                + dob
                                + "<---zip---> "
                                + zip
                                + "<---Security Code---> "
                                + securityCode
                                + "<---Driving Lisence No---> "
                                + dlNo
                                + "<---Driving Lisence State---> " + dlState);
        if(cardNo == null || cardNo.trim().length() == 0) {
            respObj.setRespCode("14");
            respObj.setRespDesc("Invalid Card Number provided");
            return respObj;
        }

        try {
            String card = getValue("select card_no from cards where card_no='"
                    + cardNo + "'");
            if(card == null) {
                respObj.setRespCode("14");
                respObj.setRespDesc("Card No(" + maskCardNo(cardNo)
                        + ") does not exist");

                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Card No ("
                                        + cardNo
                                        + ") does not exist in the database::logging error transaction:: Code -- > "
                                        + respObj.getRespCode()
                                        + " && Desc -- > "
                                        + respObj.getRespDesc());
                // log the transaction
                String[] transIds = logTransaction(cardNo, serviceId,
                        deviceType, null, "0200", "0", respObj.getRespDesc(),
                        "0", respObj.getRespCode(), deviceId, cardAcceptorId,
                        cardAcceptorNameAndLoc, mcc, accountNo, acquirerId);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with ISO Serial No - > "
                                + transIds[0] + " && Trace Audit No -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            }
            if(AAC != null && !AAC.equalsIgnoreCase("")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating AAC...");
                // validate the aac
                String result = getValue("select card_access_code from cards where card_no='"
                        + cardNo + "' and card_access_code='" + AAC + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got AAC -- > " + result);
                if(result == null) {
                    respObj.setRespCode("EB");
                    respObj.setRespDesc("Invalid AAC supplied for Card("
                            + maskCardNo(cardNo) + ")");

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid AAC supplied for Card(" + cardNo + ")");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                } // end if
            }
            if(expDate != null && !expDate.equalsIgnoreCase("")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating Expiry Date...");
                // get the expiry date
                String result = getValue("select expiry_on from cards where card_no='"
                        + cardNo + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got Expiry Date -- > " + result);
                if(result != null) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Formatting expiry date to -- > "
                                    + Constants.EXP_DATE_FORMAT);
                    // format the expiry date
                    String expiryDate = CommonUtilities.convertDateFormat(
                            Constants.EXP_DATE_FORMAT, Constants.DATE_FORMAT,
                            result);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Expiry Date After Conversion -- > " + expiryDate);

                    if(!expiryDate.equalsIgnoreCase(expDate)) {
                        respObj.setRespCode("SB");
                        respObj
                                .setRespDesc("Invalid Card Expiry Date supplied for Card("
                                        + maskCardNo(cardNo) + ")");

                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Invalid Card Expiry Date supplied for Card("
                                        + cardNo + ")");
                        // log the transaction
                        String[] transIds = logTransaction(cardNo, serviceId,
                                deviceType, null, "0200", "0", respObj
                                        .getRespDesc(), "0", respObj
                                        .getRespCode(), deviceId,
                                cardAcceptorId, cardAcceptorNameAndLoc, mcc,
                                accountNo, acquirerId);

                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Transaction logged with ISO Serial No - > "
                                        + transIds[0]
                                        + " && Trace Audit No -- > "
                                        + transIds[1]);
                        // set the trans id in response
                        respObj.setTransId(transIds[0]);
                        return respObj;
                    } // end if
                } // end if
            }
            if(accountNo != null && !accountNo.trim().equalsIgnoreCase("")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating Account No...");
                // validate the account no
                String result = getValue("select a.account_number from cards c, card_accounts a where c.card_no = a.card_no and c.card_no='"
                        + cardNo + "' and a.account_number='" + accountNo + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Account No Got -- > " + result);
                if(result == null) {
                    respObj.setRespCode("EC");
                    respObj.setRespDesc("Invalid Account No Supplied for Card("
                            + maskCardNo(cardNo) + ")");

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid Account No Supplied for Card(" + cardNo
                                    + ")");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                } // end if
            }
            if(PIN != null && !PIN.trim().equalsIgnoreCase("") && validatePIN) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating PIN....Getting PIN Offset...");
                // get the pin offset of card
                String pinOffset = getValue("select pin_offset from cards where card_no='"
                        + cardNo + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got PIN Offset -- > " + pinOffset);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating the PIN at HSM....");
                // validate the PIN at HSM
                HSMService manager = new HSMService(
                        Constants.HSM_LOG_FILE_PATH,
                        Constants.HSM_WRPR_FILE_PATH, con);
                HSMResponse hsmResp = manager.validatePin(cardNo, PIN, PIN
                        .length(), pinOffset, PIN.length());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response After PIN validation :: Code -- > "
                                + hsmResp.getResponseCode());

                if(!hsmResp.getResponseCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    respObj.setRespCode("55");
                    respObj.setRespDesc("Invalid PIN Supplied for Card("
                            + maskCardNo(cardNo) + ")");

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid PIN Supplied for Card(" + cardNo + ")");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                } // end if
            }
            if(zip != null && zip.trim().length() > 0) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating ZIP....");
                // get the pin offset of card
                String zipCode = getValue("select zip_postal_code from cards where card_no='"
                        + cardNo + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got zipCode -- > " + zipCode);

                if(zipCode == null || !zip.equalsIgnoreCase(zipCode)) {
                    respObj.setRespCode("SP");
                    respObj.setRespDesc("Invalid ZIP/Postal Code provided--->"
                            + zip);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid ZIP/Postal Code provided");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                }
            }
            if(dob != null && dob.trim().length() > 0) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating DOB...");
                // get the pin offset of card
                String dateofBirth = getValue("select date_of_birth from cards where card_no='"
                        + cardNo + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got DateOfBirth -- > " + dateofBirth);
                if(dateofBirth != null) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Formatting date of birth to -- > "
                                    + Constants.DATE_FORMAT);
                    dob = CommonUtilities.convertDateFormat(
                            Constants.DATE_FORMAT, Constants.WEB_DATE_FORMAT,
                            dob);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Expiry Date After Conversion -- > " + dob);
                    if(!dateofBirth.equalsIgnoreCase(dob)) {
                        respObj.setRespCode("SP");
                        respObj
                                .setRespDesc("Invalid Date of Birth supplied for Card("
                                        + maskCardNo(cardNo) + ")");

                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Invalid Date of Birth supplied for Card("
                                        + cardNo + ")");
                        // log the transaction
                        String[] transIds = logTransaction(cardNo, serviceId,
                                deviceType, null, "0200", "0", respObj
                                        .getRespDesc(), "0", respObj
                                        .getRespCode(), deviceId,
                                cardAcceptorId, cardAcceptorNameAndLoc, mcc,
                                accountNo, acquirerId);

                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Transaction logged with ISO Serial No - > "
                                        + transIds[0]
                                        + " && Trace Audit No -- > "
                                        + transIds[1]);
                        // set the trans id in response
                        respObj.setTransId(transIds[0]);
                        return respObj;
                    }
                } else {
                    respObj.setRespCode("SP");
                    respObj
                            .setRespDesc("Invalid Date of Birth supplied for Card("
                                    + maskCardNo(cardNo) + ")");

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid Date of Birth supplied for Card(" + cardNo
                                    + ")");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                }
            }
            if(ssn != null && ssn.trim().length() > 0) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating Last 4 digits of SSN...");
                // get the pin offset of card
                String ssNo = getValue("select ssn_nid_no from cards where card_no='"
                        + cardNo + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got SSN -- > " + ssNo);
                if(ssNo == null
                        || ssNo.length() < 4
                        || !ssNo.substring(ssNo.length() - 4, ssNo.length())
                                .equalsIgnoreCase(ssn)) {
                    respObj.setRespCode("SP");
                    respObj
                            .setRespDesc("Invalid Last 4 SSN digits provided for Card("
                                    + maskCardNo(cardNo) + ")");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid Last 4 SSN digits provided for Card("
                                    + cardNo + ")");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                }
            }
            if(phone != null && phone.trim().length() > 0) {

                int length = phone.trim().length();
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating Last 10 digits of Home Phone...");
                // get the pin offset of card
                String homePhNo = getValue("select home_phone_no from cards where card_no='"
                        + cardNo + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got homePhNo -- > " + homePhNo);
                if(homePhNo == null
                        || homePhNo.length() < length
                        || !homePhNo.substring(homePhNo.length() - length,
                                homePhNo.length()).equalsIgnoreCase(phone)) {
                    respObj.setRespCode("SP");
                    respObj
                            .setRespDesc("Invalid Last N HomePhone digits provided for Card("
                                    + maskCardNo(cardNo) + ")");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid Last N HomePhone digits provided for Card("
                                    + cardNo + ")");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                }
            }
            if(securityCode != null && securityCode.trim().length() > 0) {
                HSMResponse resp = null;
                try {
                    resp = validateCVV2FromHSM(cardNo, securityCode);
                } catch(IOException hsmIOEx) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "IO Exception received from HSM --->" + resp);
                    respObj.setRespCode("96");
                    respObj.setRespDesc("Unable to verify CVV2 of Card No("
                            + maskCardNo(cardNo)
                            + "), IO Exception in HSM API--->" + hsmIOEx);
                    respObj.setExcepMsg(hsmIOEx.getMessage());
                    respObj.setStkTrace(CommonUtilities.getStackTrace(hsmIOEx));
                    respObj
                            .setLogFilePath("More information can be found at--->"
                                    + Constants.EXACT_LOG_PATH);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to log the error transaction::Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with iso serial no -- > "
                                    + transIds[0] + " && trace audit no -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                } catch(Exception hsmEx) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Exception received from HSM --->" + resp);
                    respObj.setRespCode("96");
                    respObj.setRespDesc("Unable to verify CVV2 of Card No("
                            + maskCardNo(cardNo)
                            + "), Exception in HSM API--->" + hsmEx);
                    respObj.setExcepMsg(hsmEx.getMessage());
                    respObj.setStkTrace(CommonUtilities.getStackTrace(hsmEx));
                    respObj
                            .setLogFilePath("More information can be found at--->"
                                    + Constants.EXACT_LOG_PATH);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to log the error transaction::Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with iso serial no -- > "
                                    + transIds[0] + " && trace audit no -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                } finally {
                    // if (mgr != null) {
                    // mgr.closeHSMConnection();
                    // }
                }

                if(resp == null) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "No response received from HSM --->" + resp);
                    respObj.setRespCode("96");
                    respObj.setRespDesc("Unable to verify CVV2 of Card No("
                            + maskCardNo(cardNo)
                            + "), Invalid response received from HSM--->"
                            + resp);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to log the error transaction::Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with iso serial no -- > "
                                    + transIds[0] + " && trace audit no -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                }
                if(!resp.getResponseCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Unsuccessful response received from HSM --- HSM response code--->"
                                    + resp.getResponseCode());
                    respObj.setRespCode("SP");
                    respObj.setRespDesc("Unable to verify CVV2 of Card No("
                            + maskCardNo(cardNo) + "), HSM response code--->"
                            + resp.getResponseCode());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to log the error transaction::Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with iso serial no -- > "
                                    + transIds[0] + " && trace audit no -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                } // end if
                // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                // "Got CVV2 using HSM :: Code -- > " +
                // resp.getResponseCode() +
                // " && CVV2 -- > " + resp.getCVV2());
                // if (resp.getCVV2() == null ||
                // !securityCode.equalsIgnoreCase(resp.getCVV2())) {
                // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                // "Got CCV2 using HSM :: Invalid security code--->" +
                // securityCode);
                // respObj.setRespCode("SP");
                // respObj.setRespDesc("Invalid Security Code provided for
                // Card(" +
                // maskCardNo(cardNo) + ")");
                // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                // LOG_CONFIG),
                // "Invalid Security Code provided for Card(" +
                // cardNo + ")");
                // //log the transaction
                // String[] transIds = logTransaction(cardNo, serviceId,
                // deviceType, null,
                // "0200", "0", respObj.getRespDesc(),
                // "0", respObj.getRespCode(),
                // deviceId, cardAcceptorId,
                // cardAcceptorNameAndLoc, mcc,
                // accountNo, acquirerId);
                //
                // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                // LOG_CONFIG),
                // "Transaction logged with ISO Serial No - > " +
                // transIds[0] +
                // " && Trace Audit No -- > " +
                // transIds[1]);
                // //set the trans id in response
                // respObj.setTransId(transIds[0]);
                // return respObj;
                // }
            }

            if(dlNo != null && dlNo.trim().length() > 0) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating Driving Lisence No...");

                String drvLisNo = getValue("select driving_license_no from cards where card_no='"
                        + cardNo + "'");
                if(drvLisNo == null || !drvLisNo.trim().equalsIgnoreCase(dlNo)) {
                    respObj.setRespCode("SP");
                    respObj
                            .setRespDesc("Invalid Driving Lisence No. provided for Card("
                                    + maskCardNo(cardNo) + ")");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid Driving Lisence No. provided for Card("
                                    + cardNo + ")");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                }
            }

            if(dlState != null && dlState.trim().length() > 0) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating Driving Lisence State...");

                String drvLisSt = getValue("select driving_license_st from cards where card_no='"
                        + cardNo + "'");
                if(drvLisSt == null
                        || !drvLisSt.trim().equalsIgnoreCase(dlState)) {
                    respObj.setRespCode("SP");
                    respObj
                            .setRespDesc("Invalid Driving Lisence State provided for Card("
                                    + maskCardNo(cardNo) + ")");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Invalid Driving Lisence State provided for Card("
                                    + cardNo + ")");
                    // log the transaction
                    String[] transIds = logTransaction(cardNo, serviceId,
                            deviceType, null, "0200", "0", respObj
                                    .getRespDesc(), "0", respObj.getRespCode(),
                            deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                            mcc, accountNo, acquirerId);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No - > "
                                    + transIds[0] + " && Trace Audit No -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                }
            }

            // Now the response is successsful
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            return respObj;
        } // end try
        catch(Exception exp) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp);
            throw exp;
        } // end catch
    } // end isCardInfoValid




    /**
     * This method is used to populate the basic card information from the
     * database in response object. The response object contains the information
     * about the given card no. The method gets the account no, card-balance,
     * expiry date of the given card respectively and sets the attributes of the
     * response object accordingly.
     *
     * @param cardNo
     *        String -- Card No
     * @param respObj
     *        ServicesResponseObj -- Response object in which the info will be
     *        populated
     * @throws Exception
     */
    public void populateCardInfo(String cardNo, ServicesResponseObj respObj)
            throws Exception {
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Getting the info for card --- > " + cardNo);
        // set the card no
        respObj.setToCardNo(cardNo);

        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Getting account no...");
        // get the account no
        String accountNo = getValue("select account_number from card_accounts where card_no='"
                + cardNo + "'");
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Account No -- > " + accountNo);
        // set account no in response
        respObj.setAccountNo(accountNo);
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG), "Getting Balance....");
        // get balance
        String balance = getValue("select card_balance from card_funds where card_no='"
                + cardNo + "'");
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Got Balance -- > " + balance);
        // set hte balance in response
        respObj.setToCardBalance(balance);
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Getting expiry date...");
        // get the expiry date
        String expDate = getValue("select expiry_on from cards where card_no='"
                + cardNo + "'");
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Expiry Date -- > " + expDate);

        if(expDate != null && !expDate.trim().equals("")) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Converting expiry date format...");
            String eDate = CommonUtilities.convertDateFormat(
                    Constants.EXP_DATE_FORMAT, Constants.DATE_FORMAT, expDate);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Date after converting in new format -- > " + eDate);
            // set the expiry date in response
            respObj.setToCardExpDate(eDate);
        } // end if

        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Response object populated...");
    } // end populateCardInfo




    /**
     * This method is used to get the accounts associated with the given card
     * no. The method returns the vector which contains information such as
     * account-number, account-type, pos-account-status of the given card number
     * respectively.
     *
     * @param cardNo
     *        String -- Card No
     * @throws Exception
     * @return Vector -- List of Card Holder Accounts
     */
    public Vector getCardHolderAccounts(String cardNo) throws Exception {
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Card No Received -- > " + cardNo);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // make the query to get the accounts
        String query = "select account_number,account_type,pos_acc_status from card_accounts where card_no=?";

        try {
            // prepare the statement
            pstmt = con.prepareStatement(query);
            // set the host variable
            pstmt.setString(1, cardNo);
            // execute the query
            rs = pstmt.executeQuery();

            Vector accountList = new Vector();

            while(rs.next()) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got the Record....");

                CardAccountObj obj = new CardAccountObj();
                // get the account no
                String accountNo = rs.getString("account_number");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Account No -- > " + accountNo);
                if(accountNo != null && !accountNo.trim().equals("")) {
                    obj.setCardAccountNo(accountNo);
                } // end if
                // get the account type
                String accountType = rs.getString("account_type");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Account Type -- > " + accountType);

                if(accountType != null) {
                    obj.setCardAccountType(accountType);
                } // end if
                // get the account status
                String accountStatus = rs.getString("pos_acc_status");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Account Status -- > " + accountStatus);
                if(accountStatus != null) {
                    obj.setCardAccountStatus(accountStatus);
                } // end if

                // add the account object in the list
                accountList.add(obj);
            } // end while

            return accountList;
        } // end try
        catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage());
            // throw the exception
            throw ex;
        } // end catch
        finally {
            try {
                if(rs != null)
                    rs.close();
                if(pstmt != null)
                    pstmt.close();
            } // end try
            catch(Exception ex) {
            } // end
        } // end finally
    } // end getCardHolderAccounts




    /**
     * This method is used to check whether a stolen card can be replaced with
     * the given card no. It calls the database stored procedure
     * CHECK_CARD_REPLACE to verify whether the stolen card can be replaced with
     * the given card no. The method returns the response which fully describes
     * the status of the processing. In case of any error during the processing
     * the method throws exception which describes the cause of the error.
     *
     * @param stolenCard
     *        String
     * @param toCard
     *        String
     * @param serviceId
     *        String
     * @param applyFee
     *        String
     * @throws Exception
     * @return ServicesResponseObj
     */

    public ServicesResponseObj checkCardReplace(String stolenCard,
            String toCard, String serviceId, String applyFee) throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();

        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Arguments:: Stolen Card -- > " + stolenCard + "\n"
                        + "To Card -- > " + toCard + "\n" + "ServiceId -- > "
                        + serviceId + "\n" + "Apply Fee -- > " + applyFee);
        // make the query string
        String query = "Execute procedure CHECK_CARD_REPLACE("
                + "pfrom_card='"
                + stolenCard
                + "',"
                + "pto_card='"
                + toCard
                + "',"
                + "pservice_id='"
                + serviceId
                + "',"
                + "papply_fee="
                + (applyFee != null && applyFee.trim().equalsIgnoreCase("N") ? "0"
                        : "1") + ")";

        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Executing Query -- > " + query);

        CallableStatement cstmt = null;
        ResultSet rs = null;
        try {
            // prepare the statment
            cstmt = con.prepareCall(query);
            // execute the query
            rs = cstmt.executeQuery();
            if(rs.next()) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response Code -- > " + rs.getString(1));
                // set the response code in response object
                respObj.setRespCode(rs.getString(1));
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response Desc -- > " + rs.getString(2));
                // set the response desc in response object
                respObj.setRespDesc(rs.getString(2));
            } // end if
            return respObj;
        } // end try
        catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage());
            throw ex;
        } // end catch
        finally {
            try {
                if(cstmt != null)
                    cstmt.close();
                if(rs != null)
                    rs.close();
            } // end try
            catch(Exception ex) {
            } // end
        } // end finally
    } // end checkCardReplace




    /**
     * The method simply calls the database stored procedure
     * "APPLY_CARD_REPLACE" to check whether the application of the card replace
     * can be performed or not. It returns response object which describes the
     * processing status of the method. In case of any error during processing
     * an exception is thrown which describes the error in detail.
     *
     * @param stolenCard
     *        String
     * @param toCard
     *        String
     * @param serviceId
     *        String
     * @param applyFee
     *        String
     * @param deviceType
     *        String
     * @param switchBal
     *        String
     * @param retRefNo
     *        String
     * @throws Exception
     * @return ServicesResponseObj
     */

    public ServicesResponseObj applyReplaceCard(String stolenCard,
            String toCard, String serviceId, String applyFee,
            String deviceType, String switchBal, String retRefNo)
            throws Exception {
        ServicesResponseObj respObj = new ServicesResponseObj();

        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Arguments :: Stolen Card -- > " + stolenCard + "\n"
                        + "To Card -- > " + toCard + "\n" + "Service ID -- > "
                        + serviceId + "\n" + "Apply Fee -- > " + applyFee
                        + "\n" + "Device Type -- > " + deviceType + "\n"
                        + "Switch Balance -- > " + switchBal + "\n"
                        + "Ret Ref No -- > " + retRefNo);
        // make the query
        String query = "Execute procedure APPLY_CARD_REPLACE("
                + "pfrom_card='"
                + stolenCard
                + "',"
                + "pto_card='"
                + toCard
                + "',"
                + "pservice_id='"
                + serviceId
                + "',"
                + "papply_fee="
                + (applyFee != null && applyFee.trim().equalsIgnoreCase("N") ? "0"
                        : "1") + "," + "pdevice_type="
                + (deviceType != null ? "'" + deviceType + "'" : "null") + ","
                + "pswitch_bal=" + switchBal + "," + "pret_ref_no=" + retRefNo
                + ")";

        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Executing Query -- > " + query);

        CallableStatement cstmt = null;
        ResultSet rs = null;

        try {
            // prepare the statment
            cstmt = con.prepareCall(query);
            // execute the query
            rs = cstmt.executeQuery();
            if(rs.next()) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response Code -- > " + rs.getString(1));
                // set the response code in response object
                respObj.setRespCode(rs.getString(1));

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Respnose Desc -- > " + rs.getString(2));
                // set the response desc in response obj
                respObj.setRespDesc(rs.getString(2));

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "ISO Serial No -- > " + rs.getString(3));
                // set the iso serial no in response
                respObj.setTransId(rs.getString(3));

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Fee Amount -- > " + rs.getString(4));
                // set the fee amount in response
                respObj.setFeeAmount(rs.getString(4));

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Card Balance -- > " + rs.getString(5));
                // set the card balance in the response
                respObj.setCardBalance(rs.getString(5));
            } // end if

            return respObj;
        } // end try
        catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage());
            throw ex;
        } // end catch
        finally {
            try {
                if(rs != null)
                    rs.close();
                if(cstmt != null)
                    cstmt.close();
            } // end try
            catch(Exception ex) {
            } // end
        } // end finally
    } // end applyReplaceCard




    public ServicesResponseObj verifyCard(ServicesRequestObj reqObj)
            throws Exception {

        ServicesResponseObj respObj = null;
        StringBuffer query = new StringBuffer();
        CallableStatement cstmt = null;
        ResultSet rs = null;
        try {
            respObj = new ServicesResponseObj();

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for verifying card --- Card Number--->"
                            + reqObj.getCardNo() + "<---Acquirer Id--->"
                            + reqObj.getAcquirerId() + "<---Acquirer Id--->"
                            + reqObj.getAcquirerId()
                            + "<---Retreival Ref Num--->"
                            + reqObj.getRetreivalRefNum()
                            + "<---Local Date Time--->"
                            + reqObj.getLocalDateTime()
                            + "<---Card Acceptor Id--->"
                            + reqObj.getCardAcceptorId());

            query
                    .append("execute procedure verify_card ( pproviderid = ?, prequestid = ?, ptimestamp = ?, plocation  = ?, pcardno = ? )");
            cstmt = con.prepareCall(query.toString());
            cstmt.setString(1, reqObj.getAcquirerId());
            cstmt.setString(2, reqObj.getRetreivalRefNum());
            cstmt.setString(3, reqObj.getLocalDateTime());
            cstmt.setString(4, reqObj.getCardAcceptorId());
            cstmt.setString(5, reqObj.getCardNo());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query for verifying card --->" + query);

            rs = cstmt.executeQuery();
            if(rs.next()) {
                String transId = rs.getString(1);
                if(transId != null && transId.trim().length() > 0) {
                    respObj.setTransId(transId);
                }
                String retRefNo = rs.getString(2);
                if(retRefNo != null && retRefNo.trim().length() > 0) {
                    respObj.setRetRefNum(retRefNo);
                }
                String transDateTime = rs.getString(3);
                if(transDateTime != null && transDateTime.trim().length() > 0) {
                    respObj.setTimeStamp(transDateTime);
                }
                String respCode = rs.getString(4);
                if(respCode != null && respCode.trim().length() > 0) {
                    respObj.setRespCode(respCode);
                }
                String respDesc = rs.getString(5);
                if(respDesc != null && respDesc.trim().length() > 0) {
                    respObj.setRespDesc(respDesc);
                }
                String zip = rs.getString(6);
                if(zip != null && zip.trim().length() > 0) {
                    respObj.setZipCode(zip);
                }
                String cardRef = rs.getString(7);
                if(cardRef != null && cardRef.trim().length() > 0) {
                    respObj.setCardRef(cardRef);
                }
                String fName = rs.getString(8);
                if(fName != null && fName.trim().length() > 0) {
                    respObj.setFirstName(fName);
                }
                String lName = rs.getString(9);
                if(lName != null && lName.trim().length() > 0) {
                    respObj.setLastName(lName);
                }
                String prtFolio = rs.getString(10);
                if(prtFolio != null && prtFolio.trim().length() > 0) {
                    respObj.setPortFolio(prtFolio);
                }
            } else {
                respObj.setRespCode("14");
                respObj.setRespDesc("Invalid Account Number provided");
            }

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response Object formed --- Response Code---> "
                            + respObj.getRespCode() + "<---Resp Desc--->"
                            + respObj.getRespDesc() + "<---Trans Id--->"
                            + respObj.getTransId() + "<---Ret Ref Num--->"
                            + respObj.getRetRefNum() + "<---First Name--->"
                            + respObj.getFirstName() + "<---Last Name--->"
                            + respObj.getLastName() + "<---Card Ref--->"
                            + respObj.getCardRef() + "<---Portfolio--->"
                            + respObj.getPortFolio() + "<---Zip Code--->"
                            + respObj.getZipCode() + "<---Time Stamp--->"
                            + respObj.getTimeStamp());

        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage());
            throw ex;
        } finally {
            try {
                if(rs != null)
                    rs.close();
                if(cstmt != null)
                    cstmt.close();
            } // end try
            catch(Exception ex) {
            } // end

        }
        return respObj;
    }




    public String validateInstantIssueAttributes(String isoSerial)
            throws Exception {
        String card_no = null;
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for validating attributes for Instant Card Issue --- ISO Serial No--->"
                            + isoSerial);
            query
                    .append("select card_no from trans_requests where iso_serial_no = ? and service_id in ('NEW_CARD_PO','CARD_UPGRADE') and response_code = '00' and iso_message_type = '0200'");
            pstmt = con.prepareStatement(query.toString());

            pstmt.setString(1, isoSerial);

            rs = pstmt.executeQuery();

            if(rs.next()) {
                if(rs.getString(1) != null
                        && rs.getString(1).trim().length() > 0) {
                    card_no = rs.getString(1);
                    CommonUtilities
                            .getLogger()
                            .log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Method for validating attributes for Instant Card Issue --- Card No found for Successful NEW_CARD_PO--->"
                                            + card_no);
                } else {
                    CommonUtilities
                            .getLogger()
                            .log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Method for validating attributes for Instant Card Issue --- No Card No found for Successful NEW_CARD_PO, Invalid ISO Serial given-->"
                                            + card_no);

                    return null;
                }
            } else {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for validating attributes for Instant Card Issue --- No Card No found for Successful NEW_CARD_PO, Invalid ISO Serial given");
                return null;
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in validating attributes for Instant Card Issue --->"
                            + ex);
            throw ex;
        } finally {
            if(rs != null) {
                rs.close();
            }
            if(pstmt != null) {
                pstmt.close();
            }
        }
        return card_no;
    }




    public String getCardInfo(String cardNo, String cardQry) throws Exception {
        String info = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting card Info --- Card No--->" + cardNo
                            + "<---Card Query--->" + cardQry);
            pstmt = con.prepareStatement(cardQry);

            pstmt.setString(1, cardNo);

            rs = pstmt.executeQuery();

            if(rs.next()) {
                if(rs.getString(1) != null
                        && rs.getString(1).trim().length() > 0) {
                    info = rs.getString(1);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for getting card Info --- Info found--->"
                                    + info);
                } else {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for getting card Info --- No Info found--->"
                                    + info);
                    return null;
                }
            } else {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Method for getting card Info --- No Info found...");
                return null;
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in getting card Info --->" + ex);
            throw ex;
        } finally {
            if(rs != null) {
                rs.close();
            }
            if(pstmt != null) {
                pstmt.close();
            }
        }
        return info;
    }




    public int isExistingOFAC_AVSValid(String cardNumber) throws Exception {
        StringBuffer query = new StringBuffer();
        Statement stmt = null;
        ResultSet rs = null;
        String ofacStatus = null;
        String avsStatus = null;

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for checking Existing OFAC_AVS for card Number--->"
                            + cardNumber);
            query
                    .append("select ofac_status , avs_status from cards where card_no = ");
            CommonUtilities.buildQueryInfo(query, cardNumber, true);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query for checking Existing OFAC_AVS for card Number--->"
                            + query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query.toString());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "ResultSet for checking Existing OFAC_AVS for card Number--->"
                            + rs);
            if(rs.next()) {
                if(rs.getString(1) != null
                        && rs.getString(1).trim().length() > 0) {
                    ofacStatus = rs.getString(1).trim();
                }
                if(rs.getString(2) != null
                        && rs.getString(2).trim().length() > 0) {
                    avsStatus = rs.getString(2).trim();
                }
            }
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Found Values  ofac_status--->" + ofacStatus
                            + "<---avs_status--->" + avsStatus);
            if (ofacStatus != null &&
                ofacStatus.equalsIgnoreCase(Constants.OFAC_AVS_OK)) {
                if (avsStatus != null &&
                    avsStatus.equalsIgnoreCase(Constants.OFAC_AVS_OK)) {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                                                    "OFAC AVS for exisiting Card OK returning TRUE");
                    return Constants.OFAC_AVS_GOOD;
                } else {
                    return Constants.AVS_FAILED;
                }
            } else {
                return Constants.OFAC_FAILED;
            }

        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in checking OFAC/AVS of exisitng card--->" + ex);
             throw new Exception("Unable to check exisiting OFAC/AVS status--->" + ex);
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                    rs = null;
                }
                if(stmt != null) {
                    stmt.close();
                    stmt = null;
                }
            } catch(SQLException ex1) {
            }
        }
    }

    public int isExistingOFAC_AVSValid(String ofacStatus,String avsStatus) throws Exception {
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for checking  OFAC/AVS valid --- ofac_status--->" + ofacStatus
                            + "<---avs_status--->" + avsStatus);
            if (ofacStatus != null &&
                ofacStatus.equalsIgnoreCase(Constants.OFAC_AVS_OK)) {
                if (avsStatus != null &&
                    avsStatus.equalsIgnoreCase(Constants.OFAC_AVS_OK)) {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                                                    "OFAC AVS for exisiting Card OK returning TRUE");
                    return Constants.OFAC_AVS_GOOD;
                } else {
                    return Constants.AVS_FAILED;
                }
            } else {
                return Constants.OFAC_FAILED;
            }

        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in checking OFAC/AVS of exisitng card--->" + ex);
             throw new Exception("Unable to check exisiting OFAC/AVS status--->" + ex);
        }
    }


    public ServicesResponseObj assignCard(ServicesRequestObj requestObj) {
        ServicesResponseObj respObj = null;
        try {
            String employerId = null;
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for prcoessing card assignment --- card Number--->"
                            + requestObj.getCardNo()
                            + "<---Stake Holder ID--->"
                            + requestObj.getCardAcceptorId());
            if(requestObj.getCardAcceptorId() != null
                    && requestObj.getCardAcceptorId().trim().length() > 0) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for prcoessing card assignment --- Attempting assignment using card acceptor...");
                respObj = processCardAssignment(requestObj.getCardNo(), requestObj.getCardAcceptorId(), requestObj.getDescription());
                if(respObj.isIsCardAssigned() == false) {
                    employerId = getCardEmployerID(requestObj.getCardNo());
                    CommonUtilities
                            .getLogger()
                            .log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Method for prcoessing card assignment --- Assignment using card acceptor failed, attempting assignment using employer --->"
                                            + employerId);
                    return processCardAssignment(requestObj.getCardNo(),
                            employerId, requestObj.getDescription());
                } else {
                    return respObj;
                }
            } else {
                employerId = getCardEmployerID(requestObj.getCardNo());
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for prcoessing card assignment --- Attempting assignment using employer --->"
                                        + employerId);
                return processCardAssignment(requestObj.getCardNo(),
                        employerId, requestObj.getDescription());
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in assigning card--->" + ex);
            respObj = new ServicesResponseObj();
            respObj.setRespCode("00");
            respObj.setRespDesc("Unable to assign Card");
            respObj.setIsCardAssigned(false);
            return respObj;
        }
    }




    public ServicesResponseObj processCardAssignment(String cardNo, String merchantId,
            String description) {
        ServicesResponseObj respObj = new ServicesResponseObj();
        String salesNodeNum = null;
        String cardPrgId = null;
        String assignNo = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for prcoessing card assignment --- Card Number--->"
                            + cardNo + "<---Merchant Id--->" + merchantId
                            + "<---Description-->" + description);
            if(cardNo != null && cardNo.trim().length() > 0
                    && merchantId != null && merchantId.trim().length() > 0) {
                if(checkCardAlreadyAssigned(cardNo)) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for prcoessing card assignment --- card Number--->"
                                    + cardNo
                                    + " is already assigned, returning....");
                    respObj.setRespCode("00");
                    respObj.setRespDesc("Card is already assigned");
                    respObj.setIsCardAssigned(true);
                    return respObj;
                }
                cardPrgId = getCardProgramID(cardNo);
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for prcoessing card assignment --- Getting sales node number against the given stake holder id and card program--->"
                                        + cardPrgId);
                salesNodeNum = getSalesNodeNumber(merchantId, cardPrgId);
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for prcoessing card assignment --- Got sales node number against the given stake holder id and card program--->"
                                        + salesNodeNum);
                if(salesNodeNum != null && salesNodeNum.trim().length() > 0) {
                    CommonUtilities
                            .getLogger()
                            .log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Method for prcoessing card assignment --- Getting assignment no by inserting new card assignment record...");
                    assignNo = insertSalesRecord(merchantId, cardNo,
                            salesNodeNum, cardPrgId, description);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for prcoessing card assignment --- Got Assignment No.--->"
                                    + assignNo);
                    if(assignNo != null && assignNo.trim().length() > 0) {
                        CommonUtilities
                                .getLogger()
                                .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Method for prcoessing card assignment --- inserting detail record...");
                        insertDetailRecord(assignNo, cardNo);
                        CommonUtilities
                                .getLogger()
                                .log(
                                        LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Method for prcoessing card assignment --- Assigning the card to stakeholder...");
                        assignCard(cardNo, assignNo, merchantId, salesNodeNum);
                        respObj.setRespCode("00");
                        respObj.setRespDesc("OK");
                        respObj.setIsCardAssigned(true);
                        return respObj;
                    } else {
                        CommonUtilities
                                .getLogger()
                                .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Method for prcoessing card assignment --- No Assignment number got");
                        respObj.setRespCode("00");
                        respObj.setRespDesc("No Assignment number got");
                        respObj.setIsCardAssigned(false);
                        return respObj;
                    }
                } else {
                    CommonUtilities
                            .getLogger()
                            .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Method for prcoessing card assignment --- No Sales Node number found for provided merchant--->" + merchantId);
                    respObj.setRespCode("00");
                    respObj.setRespDesc("No Sales Node number found");
                    respObj.setIsCardAssigned(false);
                    return respObj;
                }
            } else {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for prcoessing card assignment --- Cannot assign as mandatory fields missing, card no or merchant id...");
                respObj.setRespCode("00");
                respObj.setRespDesc("Mandatory fields missing, card no or merchant id");
                respObj.setIsCardAssigned(false);
                return respObj;
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in prcoessing card assignment --->" + ex);
            respObj.setRespCode("00");
            respObj.setRespDesc("Unable to assign Card");
            respObj.setIsCardAssigned(false);
            return respObj;
        }
    }




    String getSalesNodeNumber(String stakeHolderId, String cardProgId) {

        String salesNodeNum = null;
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting sales node number --- StakeHolderID--->"
                            + stakeHolderId + "<---Card Program ID--->"
                            + cardProgId);
            if(stakeHolderId != null && stakeHolderId.trim().length() > 0) {
                query
                        .append("select sales_node_no from sale_chanels where card_prg_id = ? and stake_holder_id = ? order by sales_node_no");
                pstmt = con.prepareStatement(query.toString());
                pstmt.setString(1, cardProgId);
                pstmt.setString(2, stakeHolderId);

                rs = pstmt.executeQuery();

                if(rs.next()) {
                    salesNodeNum = rs.getString(1);
                    if(salesNodeNum != null && salesNodeNum.trim().length() > 0) {
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for getting sales node number --- SalesNodeNumber got--->"
                                        + salesNodeNum);
                        return salesNodeNum.trim();
                    } else {
                        CommonUtilities
                                .getLogger()
                                .log(
                                        LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Method for getting sales node number --- No SalesNodeNumber found for provided card program and Stake holder");
                        return null;
                    }
                } else {
                    CommonUtilities
                            .getLogger()
                            .log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Method for getting sales node number --- No SalesNodeNumber found for provided card program and Stake holder");
                    return null;
                }
            } else {
                CommonUtilities
                        .getLogger()
                        .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for getting sales node number --- No StakeHolder provided");
                return null;
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in getting sales node number --->" + ex);
            return null;
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                    rs = null;
                }
                if(pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch(Exception ex1) {
            }
        }
    }




    boolean checkCardAlreadyAssigned(String cardNo) {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String salesNodeNo = null;
        String stkHolderId = null;

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for checking card already assigned --- Card No--->"
                            + cardNo);
            query
                    .append("select sales_node_no,stake_holder_id from cards where card_no = ? ");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, cardNo);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                salesNodeNo = rs.getString(1);
                stkHolderId = rs.getString(2);
                if(salesNodeNo != null && salesNodeNo.trim().length() > 0
                        && stkHolderId != null
                        && stkHolderId.trim().length() > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for checking card already assigned --- SalesNodeNumber got--->"
                                    + salesNodeNo
                                    + "<---StakeHolder ID got--->"
                                    + stkHolderId);
                    return true;
                } else {
                    return false;
                }
            } else {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for checking card already assigned --- No SalesNodeNumber found for provided card program and Stake holder");
                return false;
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in checking card already assigned  --->" + ex);
            return false;
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                    rs = null;
                }
                if(pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch(Exception ex1) {
            }
        }
    }




    String insertSalesRecord(String stakeHolderId, String cardNumber,
            String salesNodeNumber, String cardProgId, String description) {

        String assignNo = null;
        StringBuffer query = new StringBuffer();
        StringBuffer serialQuery = new StringBuffer();
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        String assignDate = null;
        String assignTime = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting sales node number --- StakeHolderID--->"
                            + stakeHolderId + "<---Card Program ID--->"
                            + cardProgId + "<---Description--->" + description);
            assignDate = CommonUtilities
                    .getCurrentFormatDate(Constants.INFORMIX_DATE_FORMAT);
            assignTime = CommonUtilities
                    .getCurrentFormatDate(Constants.TIME_FORMAT);

            query
                    .append("insert into card_assignments(stake_holder_id,assign_date,assign_time,remarks,ncards,card_no_start,card_no_to,sales_node_no,card_prg_id,current_ncards) values(?,?,?,?,?,?,?,?,?,?)");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, stakeHolderId);
            pstmt.setString(2, assignDate);
            pstmt.setString(3, assignTime);
            pstmt.setString(4, description);
            pstmt.setString(5, "1");
            pstmt.setString(6, cardNumber);
            pstmt.setString(7, cardNumber);
            pstmt.setString(8, salesNodeNumber);
            pstmt.setString(9, cardProgId);
            pstmt.setString(10, "1");

            pstmt.executeUpdate();

            if(query.toString().toLowerCase().indexOf(
                    Constants.INSERT_QUERY_INTO_VALUE.toLowerCase()) > -1) {
                serialQuery
                        .append(Constants.SERIAL_QUERY
                                + " "
                                + query
                                        .toString()
                                        .substring(
                                                query
                                                        .toString()
                                                        .toLowerCase()
                                                        .indexOf(
                                                                Constants.INSERT_QUERY_INTO_VALUE
                                                                        .toLowerCase())
                                                        + Constants.INSERT_QUERY_INTO_VALUE
                                                                .length(),
                                                query
                                                        .toString()
                                                        .toLowerCase()
                                                        .indexOf(
                                                                Constants.INSERT_QUERY_COLUMN_START_VALUE
                                                                        .toLowerCase())));
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        " Serail Number Query --> " + serialQuery);
                stmt = con.createStatement();
                rs = stmt.executeQuery(serialQuery.toString());

                if(rs.next()) {
                    if(rs.getString(1) != null
                            && rs.getString(1).trim().length() > 0) {
                        assignNo = rs.getString(1).trim();
                    }
                }
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in getting sales node number --->" + ex);
            return null;
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                    rs = null;
                }
                if(stmt != null) {
                    stmt.close();
                    stmt = null;
                }
                if(pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch(Exception ex1) {
            }
        }
        return assignNo;
    }




    void insertDetailRecord(String assigNo, String cardNumber) {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting detail sales node number --- AssigNo--->"
                            + assigNo + "<---Card No--->" + cardNumber);
            query
                    .append("insert into card_assignment1s(assignment_no,card_no_from,card_no_to,ncards) values(?,?,?,?)");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, assigNo);
            pstmt.setString(2, cardNumber);
            pstmt.setString(3, cardNumber);
            pstmt.setString(4, "1");
            pstmt.executeUpdate();
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in getting detail sales node number --->" + ex);
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                    rs = null;
                }
                if(stmt != null) {
                    stmt.close();
                    stmt = null;
                }
                if(pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch(Exception ex1) {
            }
        }
    }




    void assignCard(String cardNo, String assignNo, String stkHolderID,
            String salesNodeNo) {

        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for assinging Card to stakeholder --- Card No--->"
                            + cardNo + "<---Assign No--->" + assignNo
                            + "<---Stake Holder ID--->" + stkHolderID
                            + "<---Sales Node No--->" + salesNodeNo);
            query
                    .append("update cards set assignment_no = ?,stake_holder_id = ?,sales_node_no = ? where card_no = ?");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query for assinging Card to stakeholder --->" + query);

            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, assignNo);
            pstmt.setString(2, stkHolderID);
            pstmt.setString(3, salesNodeNo);
            pstmt.setString(4, cardNo);

            pstmt.executeUpdate();
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Card assigned to stakeholder successfully....");
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in assigning card to stakeholder--->" + ex);
        } finally {
            try {
                if(pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch(SQLException ex1) {
            }
        }
    }




    public HSMResponse getCVV2FromHSM(String cardNo) throws Exception {
        HSMResponse hsmResp = null;
        HSMService mgr = null;
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Method for getting cvv2_cvc2 from HSM for card--->" + cardNo);
        mgr = new HSMService(Constants.HSM_LOG_FILE_PATH,
                Constants.HSM_WRPR_FILE_PATH, con);
        hsmResp = mgr.calculateCVC2(cardNo);
        return hsmResp;
    }




    public HSMResponse validateCVV2FromHSM(String cardNo, String cvv2_cvc2)
            throws Exception {
        HSMResponse hsmResp = null;
        HSMService mgr = null;
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Method for validating cvv2_cvc2 from HSM for card--->"
                        + cardNo + "<---CVV2_CVC2--->" + cvv2_cvc2);
        mgr = new HSMService(Constants.HSM_LOG_FILE_PATH,
                Constants.HSM_WRPR_FILE_PATH, con);
        hsmResp = mgr.verifyCVV2(cardNo, cvv2_cvc2);
        return hsmResp;
    }




    public int checkOfacAvsFieldUpdates(ServicesRequestObj request) {

        try {
            if(checkAllFieldsPresent(request)) {
                return 1;// all present
            } else if(checkAllFieldsNotPresent(request)) {
                return 3;// all not present
            } else {
                return 2;// partial present
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in Validating Mandatory Attrubtes--->" + ex);
            return -1;
        }
    }




    public Vector getOfacAvsExistingFields(String cardNo) {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector ofacAvsProfleInfo = null;

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting existing OFAC/AVS fields for card no--->"
                            + cardNo);
            query
                    .append("select first_name1,last_name1,address1,city,state_code,zip_postal_code,ssn_nid_no,driving_license_no,driving_license_st from cards where card_no = ?");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, cardNo);

            rs = pstmt.executeQuery();
            if(rs.next()) {
                ofacAvsProfleInfo = new Vector();
                String firstName = rs.getString(1);
                if(firstName != null && firstName.trim().length() > 0) {
                    ofacAvsProfleInfo.add(firstName.trim());
                } else {
                    ofacAvsProfleInfo.add("");
                }
                String lastName = rs.getString(2);
                if(lastName != null && lastName.trim().length() > 0) {
                    ofacAvsProfleInfo.add(lastName.trim());
                } else {
                    ofacAvsProfleInfo.add("");
                }
                String address1 = rs.getString(3);
                if(address1 != null && address1.trim().length() > 0) {
                    ofacAvsProfleInfo.add(address1.trim());
                } else {
                    ofacAvsProfleInfo.add("");
                }
                String city = rs.getString(4);
                if(city != null && city.trim().length() > 0) {
                    ofacAvsProfleInfo.add(city.trim());
                } else {
                    ofacAvsProfleInfo.add("");
                }
                String state = rs.getString(5);
                if(state != null && state.trim().length() > 0) {
                    ofacAvsProfleInfo.add(state.trim());
                } else {
                    ofacAvsProfleInfo.add("");
                }
                String zip = rs.getString(6);
                if(zip != null && zip.trim().length() > 0) {
                    ofacAvsProfleInfo.add(zip.trim());
                } else {
                    ofacAvsProfleInfo.add("");
                }
                String ssn = rs.getString(7);
                if(ssn != null && ssn.trim().length() > 0) {
                    ofacAvsProfleInfo.add(ssn.trim());
                } else {
                    ofacAvsProfleInfo.add("");
                }
                String dlNo = rs.getString(8);
                if(dlNo != null && dlNo.trim().length() > 0) {
                    ofacAvsProfleInfo.add(dlNo.trim());
                } else {
                    ofacAvsProfleInfo.add("");
                }
                String dlState = rs.getString(9);
                if(dlState != null && dlState.trim().length() > 0) {
                    ofacAvsProfleInfo.add(dlState.trim());
                } else {
                    ofacAvsProfleInfo.add("");
                }
            }
        } catch(Exception e) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in getting existing OFAC/AVS fields for card no--->"
                            + e);
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                    rs = null;
                }
                if(pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return ofacAvsProfleInfo;
    }




    private boolean checkAllFieldsNotPresent(ServicesRequestObj requestInfoObj) {
        boolean allNotPresent = false;
        try {
            if((requestInfoObj.getFirstName() == null || requestInfoObj
                    .getFirstName().trim().length() == 0)
                    && (requestInfoObj.getLastName() == null || requestInfoObj
                            .getLastName().trim().length() == 0)
                    && (requestInfoObj.getAddressStreet1() == null || requestInfoObj
                            .getAddressStreet1().trim().length() == 0)
                    && (requestInfoObj.getCity() == null || requestInfoObj
                            .getCity().trim().length() == 0)
                    && (requestInfoObj.getStateCode() == null || requestInfoObj
                            .getStateCode().trim().length() == 0)
                    && (requestInfoObj.getZipCode() == null || requestInfoObj
                            .getZipCode().trim().length() == 0)
                    && (requestInfoObj.getSsn() == null || requestInfoObj
                            .getSsn().trim().length() == 0)
                    && (requestInfoObj.getDrivingLicesneNo() == null || requestInfoObj
                            .getDrivingLicesneNo().trim().length() == 0)
                    && (requestInfoObj.getDrivingLicesneState() == null || requestInfoObj
                            .getDrivingLicesneState().trim().length() == 0)) {
                allNotPresent = true;
            }
        } catch(Exception ex) {
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_WARNING),
                            "Exception in checking if all mandatory fields required for OFAC&AVS are not present--->"
                                    + ex);
        }
        return allNotPresent;
    }




    private boolean checkAllFieldsPresent(ServicesRequestObj requestInfoObj) {
        boolean allPresent = false;
        try {
            if(requestInfoObj.getFirstName() != null
                    && requestInfoObj.getFirstName().trim().length() > 0
                    && requestInfoObj.getLastName() != null
                    && requestInfoObj.getLastName().trim().length() > 0
                    && requestInfoObj.getAddressStreet1() != null
                    && requestInfoObj.getAddressStreet1().trim().length() > 0
                    && requestInfoObj.getCity() != null
                    && requestInfoObj.getCity().trim().length() > 0
                    && requestInfoObj.getStateCode() != null
                    && requestInfoObj.getStateCode().trim().length() > 0
                    && requestInfoObj.getZipCode() != null
                    && requestInfoObj.getZipCode().trim().length() > 0
                    && requestInfoObj.getSsn() != null
                    && requestInfoObj.getSsn().trim().length() > 0
                    && requestInfoObj.getDrivingLicesneNo() != null
                    && requestInfoObj.getDrivingLicesneNo().trim().length() > 0
                    && requestInfoObj.getDrivingLicesneState() != null
                    && requestInfoObj.getDrivingLicesneState().trim().length() > 0) {
                allPresent = true;
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in checking if fields required for OFAC&AVS are present--->"
                            + ex);
        }
        return allPresent;
    }




    public ServicesResponseObj checkNewOFAC_AVS(ServicesRequestObj requestObj) {
        ServicesResponseObj ofacResp = new ServicesResponseObj();
        ChAuthManager ofacAvsMgr = null;
        MessageInfoObj ofacAvsRequestInfo = null;
        String logFilePath = null;
        RecordAuthInfoObj ofacAvsResponseInfo = null;

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for checking OFAC_AVS with new Information");
            logFilePath = Constants.LOG_FILE_PATH + File.separator
                    + "services_ofac_avs";
            File f = new File(logFilePath);
            if(!f.exists()) {
                f.mkdirs();
            }
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Log File Path --->" + logFilePath);
            try {
                CommonUtilities
                        .getLogger()
                        .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Mapping profile information to MessageInfo Object for performing ofac/avs...");
                ofacAvsRequestInfo = mapRequestObject(requestObj);
            } catch(Exception missingEx) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Exception in mapping profile information to MessageInfo Object--->"
                                + missingEx);
                ofacResp.setRespCode("N5");
                ofacResp
                        .setRespDesc("Unable to map profile information to MessageInfo Object for performing ofac/avs--->"
                                + missingEx);
                return ofacResp;
            }
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Calling ChAuthMgr for validating OFAC/AVS");
            ofacAvsMgr = new ChAuthManager();
            ofacAvsResponseInfo = ofacAvsMgr.processChInfo(ofacAvsRequestInfo,
                    con, logFilePath);
            if(ofacAvsResponseInfo != null) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Resposne returned from ChAuthMgr ---- IsAVSTrue--->"
                                + ofacAvsResponseInfo.isAvsTrue()
                                + "<---isOfacTrue--->"
                                + ofacAvsResponseInfo.isOfacTrue()
                                + "<---ofacRespCode--->"
                                + ofacAvsResponseInfo.getOfacRespCode()
                                + "<---avsRespCode--->"
                                + ofacAvsResponseInfo.getAvsRespCode()
                                + "<---isOfacTrue--->"
                                + ofacAvsResponseInfo.isOfacTrue()
                                + "<---getAvsDescription--->"
                                + ofacAvsResponseInfo.getAvsDescription()
                                + "<---getOfacDescription--->"
                                + ofacAvsResponseInfo.getOfacDescription());
                if(!ofacAvsResponseInfo.isOfacTrue()
                        || !ofacAvsResponseInfo.isAvsTrue()) {
                    CommonUtilities
                            .getLogger()
                            .log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Resposne returned from ChAuthMgr ---- ofac/avs failed, returning error response...");
                    if (!ofacAvsResponseInfo.isOfacTrue()) {
                        ofacResp.setRespCode("OF");
                        ofacResp
                            .setRespDesc("OFAC failed for provided profile information, OFAC RespCode-->"
                                    + ofacAvsResponseInfo.getOfacRespCode());

                    } else if (!ofacAvsResponseInfo.isAvsTrue()) {
                        ofacResp.setRespCode("AV");
                        ofacResp
                                .setRespDesc("AVS failed for provided profile information, AVS RespCode-->"
                                             + ofacAvsResponseInfo.getAvsRespCode());
                    }
                    ofacResp.setOfacRespCode(ofacAvsResponseInfo
                            .getOfacRespCode());
                    ofacResp.setAvsRespCode(ofacAvsResponseInfo
                            .getAvsRespCode());
                    ofacResp.setOfacRespDesc(ofacAvsResponseInfo
                            .getOfacDescription());
                    ofacResp.setAvsRespDesc(ofacAvsResponseInfo
                            .getAvsDescription());
                    return ofacResp;
                } else {
                    CommonUtilities
                            .getLogger()
                            .log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Resposne returned from ChAuthMgr ---- ofac/avs passed, returning success response...");
                    ofacResp.setRespCode("00");
                    ofacResp.setRespDesc("OK");
                    return ofacResp;
                }
            } else {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Null response received from OFAC/AVS API--->"
                                + ofacAvsResponseInfo);
                ofacResp.setRespCode("96");
                ofacResp
                        .setRespDesc("Null response received from OFAC/AVS API--->"
                                + ofacAvsResponseInfo);
                return ofacResp;
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in verifying OFAC/AVS using API--->" + ex);
            ofacResp.setRespCode("96");
            ofacResp.setRespDesc("Unable to perform OFAC/AVS through API--->"
                    + ex);
            return ofacResp;
        }
    }




    private MessageInfoObj mapRequestObject(ServicesRequestObj requestObj)
            throws Exception {
        MessageInfoObj reqObj = new MessageInfoObj();
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Method for mapping ofac/avs request object --- Arguments --- getCardPrgId()"
                        + requestObj.getCardPrgId() + "<--getCardNo--->"
                        + requestObj.getCardNo() + "<--getFirstName--->"
                        + requestObj.getFirstName() + "<--getLastName-->"
                        + requestObj.getLastName() + "<---getSsn-->"
                        + requestObj.getSsn() + "<--getEmail--->"
                        + requestObj.getEmail() + "<--getAddressStreet1--->"
                        + requestObj.getAddressStreet1()
                        + "<---getAddressStreet2-->"
                        + requestObj.getAddressStreet2() + "<--getCity--->"
                        + requestObj.getCity() + "<--getStateCode--->"
                        + requestObj.getStateCode() + "<---getZipCode-->"
                        + requestObj.getZipCode()
                        + "<---getDrivingLicesneNo-->"
                        + requestObj.getDrivingLicesneNo()
                        + "<---getDrivingLicesneState-->"
                        + requestObj.getDrivingLicesneState()
                        + "<--getActionType--->" + requestObj.getActionType());
        if(requestObj.getFirstName() == null
                || requestObj.getFirstName().trim().length() == 0
                || requestObj.getLastName() == null
                || requestObj.getLastName().trim().length() == 0
                || requestObj.getZipCode() == null
                || requestObj.getZipCode().trim().length() == 0
                || requestObj.getAddressStreet1() == null
                || requestObj.getAddressStreet1().trim().length() == 0
                || requestObj.getCity() == null
                || requestObj.getCity().trim().length() == 0
                || requestObj.getStateCode() == null
                || requestObj.getStateCode().trim().length() == 0) {
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for mapping ofac/avs request object --- Missing mandatory fields required for performing ofac/avs(mandatory fields are first name,last name,address 1,zip,city,state)...");
            throw new Exception(
                    "Missing mandatory fields required for performing ofac/avs(mandatory fields are first name,last name,address 1,zip,city,state)");
        }
        reqObj.setDrCardNo(requestObj.getCardNo());
        reqObj.setInstProgID(requestObj.getCardPrgId());
        reqObj.setFirstName(requestObj.getFirstName());
        reqObj.setLastName(requestObj.getLastName());
        reqObj.setSsn(requestObj.getSsn());
        reqObj.setEmail(requestObj.getEmail());
        reqObj.setCurrentAddress1(requestObj.getAddressStreet1());
        reqObj.setCurrentAddress2(requestObj.getAddressStreet2());
        reqObj.setCurrentCity(requestObj.getCity());
        reqObj.setCurrentState(requestObj.getStateCode());
        reqObj.setCurrentZip(requestObj.getZipCode());
        reqObj.setApplyDate(CommonUtilities
                .getCurrentFormatDate(Constants.WEB_DATE_FORMAT)); // Must be
                                                                    // in
                                                                    // MM/dd/yyyy
        reqObj.setDriverLicenseNo(requestObj.getDrivingLicesneNo());
        reqObj.setDriverLicenseState(requestObj.getDrivingLicesneState());
        reqObj.setActionType(requestObj.getActionType());
        return reqObj;
    }




    public boolean[] checkIsBrandedCardProgram(String cardPrgId) {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean[] results = new boolean[2];

        String brandedValue = null;
        String activateUnRegValue = null;
        try {
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for getting values for provided card program's is_branded & can_act_card_wo_reg values , Card ProgramId--->"
                                    + cardPrgId);
            query
                    .append("select is_branded,can_act_card_wo_reg from card_programs where card_prg_id = ?");
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Query for getting values for provided card program's is_branded & can_act_card_wo_reg values--->"
                                    + query);

            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, cardPrgId);

            rs = pstmt.executeQuery();
            if(rs.next()) {
                brandedValue = rs.getString(1);
                activateUnRegValue = rs.getString(2);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Result got -->" + "<---Is Branded value--->"
                                + brandedValue
                                + "<---Activate Unregistered value--->"
                                + activateUnRegValue);
                if(brandedValue != null
                        && brandedValue.trim().equalsIgnoreCase(
                                Constants.YES_OPTION)) {
                    results[0] = true;
                }
                if(activateUnRegValue != null
                        && activateUnRegValue.trim().equalsIgnoreCase(
                                Constants.YES_OPTION)) {
                    results[1] = true;
                }
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in checking whether provided card program is branded--->"
                            + ex);
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                    rs = null;
                }
                if(pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch(SQLException ex1) {
            }
        }
        return results;
    }

    public String getCardGenerationType(String cardNo) {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String cardGenType = null;

        try {
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for getting Card Generation Type for card number--->"
                                    + cardNo);
            query
                    .append("select card_gen_mode from cards where card_no = ?");
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Query for getting Card Generation Type for card number--->"
                                    + query);

            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, cardNo);

            rs = pstmt.executeQuery();
            if(rs.next()) {
                cardGenType = rs.getString(1);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Result got -->" + cardGenType);
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in getting Card Generation Type for card number--->"
                            + ex);
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                    rs = null;
                }
                if(pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch(SQLException ex1) {
            }
        }
        return cardGenType;
    }

    public boolean checkCardStatus(String cardNo,String checkStatus) {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isPreActive = false;
        try {
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for checking whether Card status matches provided status, Card number--->"
                            + cardNo + "<---Status--->" + checkStatus);
            query
                    .append("select card_status_pos,card_status_pos from cards where card_no = ?");
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Query for checking whether Card status matches provided status--->"
                            + query);

            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, cardNo);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                String cardStatusATM = rs.getString(1);
                String cardStatusPOS = rs.getString(2);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Result got, ATM -->" + cardStatusATM
                        + "<---POS--->" + cardStatusPOS);
                if(cardStatusATM != null && cardStatusATM.trim().equals(checkStatus)
                   && cardStatusPOS != null && cardStatusPOS.trim().equals(checkStatus)){
                    return true;
                }
            }
        } catch (Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in checking whether Card status matches provided status--->"
                    + ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch (SQLException ex1) {
            }
        }

        return isPreActive;
    }

    public String getExisingCardNo(String cardNo) {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String existCardNo = null;

        try {
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for getting Existing Card Number--->"
                            + cardNo);
            query
                    .append("select src_card_no from cards where card_no = ?");
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Query for checking Card status Pre Active Card number--->"
                            + query);

            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, cardNo);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                existCardNo = rs.getString(1);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Result got -->" + existCardNo);
            }
        } catch (Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in checking Card status Pre Active Card number--->"
                    + ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch (SQLException ex1) {
            }
        }

        return existCardNo;
    }



    public boolean checkCardStatusChangeAllowed(String cardNo, String newStatus) {
        StringBuffer query = new StringBuffer();
        CallableStatement cstmt = null;
        ResultSet rs = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for checking allowed card status changes--- Card Number--->"
                            + cardNo + "<---New card status--->" + newStatus);

            query
                    .append("execute procedure chk_allowed_status ( pcard_no = ?, pto_status = ? )");
            cstmt = con.prepareCall(query.toString());
            cstmt.setString(1, cardNo);
            cstmt.setString(2, newStatus);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query for checking allowed card status changes --->"
                            + query);

            rs = cstmt.executeQuery();
            if(rs.next()) {
                String result = rs.getString(1);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Result Got---> " + result);

                if(result != null && result.trim().equals(Constants.YES_OPTION)) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage());
            ;
            return false;
        } finally {
            try {
                if(rs != null)
                    rs.close();
                if(cstmt != null)
                    cstmt.close();
            } catch(Exception ex) {
            } // end try, end

        }
        return true;
    }




    public Vector getCardholderPayees(String cardHolderId, boolean ivrOnly)
            throws Exception {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BPPayeesObj chPayeeInfo = null;
        BPChildPayeeObj addressInfo = null;
        Vector payeeList = new Vector();

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting Payees defined for cardholder--->"
                            + cardHolderId + "<---Fetch IVR Only Payees--->"
                            + ivrOnly);
            query
                    .append("select c.payee_sno,c.consumer_acct_no,c.order_no,c.payee_nick,c.payee_voice_nick,p.payee_id,p.payee_name,a.street1,a.street2,a.street3,a.city,a.street4,a.state,a.zip_postal_code,a.country_code,a.payee_cid,c.addr_sno from bp_ch_payees c, bp_payees p, outer bp_payee_addrs a where c.ch_id = ? and c.payee_sno = p.payee_sno and c.addr_sno = a.addr_sno");
            if(ivrOnly) {
                query.append(" and c.payee_voice_nick is not null");
            }
            pstmt = con.prepareCall(query.toString());
            pstmt.setString(1, cardHolderId);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query for for getting Payees defined for cardholder--->"
                            + query);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                chPayeeInfo = new BPPayeesObj();
                addressInfo = new BPChildPayeeObj();
                chPayeeInfo.setAddressInfo(addressInfo);

                String payee_sno = rs.getString(1);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, payee_sno--->" + payee_sno);
                if(payee_sno != null && payee_sno.trim().length() > 0) {
                    chPayeeInfo.setPayeeSrNo(payee_sno);
                }

                String consumer_acct_no = rs.getString(2);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, consumer_acct_no--->" + consumer_acct_no);
                if(consumer_acct_no != null
                        && consumer_acct_no.trim().length() > 0) {
                    chPayeeInfo.setConsumerAccountNumber(consumer_acct_no);
                }

                String order_no = rs.getString(3);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, order_no--->" + order_no);
                if(order_no != null && order_no.trim().length() > 0) {
                    chPayeeInfo.setOrderNumber(order_no);
                }

                String payee_nick = rs.getString(4);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, payee_nick--->" + payee_nick);
                if(payee_nick != null && payee_nick.trim().length() > 0) {
                    chPayeeInfo.setPayeeNick(payee_nick);
                }

                byte[] payee_voice_nick = rs.getBytes(5);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, payee_voice_nick--->" + payee_voice_nick);
                if(payee_voice_nick != null) {
                    chPayeeInfo.setPayeeVoiceNick(payee_voice_nick);
                }

                String payee_id = rs.getString(6);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, payee_id--->" + payee_id);
                if(payee_id != null && payee_id.trim().length() > 0) {
                    chPayeeInfo.setPayeeId(payee_id);
                }

                String payee_name = rs.getString(7);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, payee_name--->" + payee_name);
                if(payee_name != null && payee_name.trim().length() > 0) {
                    chPayeeInfo.setPayeeName(payee_name);
                }

                String street1 = rs.getString(8);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, street1--->" + street1);
                if(street1 != null && street1.trim().length() > 0) {
                    addressInfo.setStreet1(street1);
                }

                String street2 = rs.getString(9);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, street2--->" + street2);

                if(street2 != null && street2.trim().length() > 0) {
                    addressInfo.setStreet2(street2);
                }

                String street3 = rs.getString(10);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, street3--->" + street3);
                if(street3 != null && street3.trim().length() > 0) {
                    addressInfo.setStreet3(street3);
                }

                String city = rs.getString(11);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, city--->" + city);
                if(city != null && city.trim().length() > 0) {
                    addressInfo.setCity(city);
                }

                String street4 = rs.getString(12);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, street4--->" + street4);
                if(street4 != null && street4.trim().length() > 0) {
                    addressInfo.setStreet4(street4);
                }

                String state = rs.getString(13);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, state--->" + state);
                if(state != null && state.trim().length() > 0) {
                    addressInfo.setState(state);
                }

                String zip_postal_code = rs.getString(14);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, zip_postal_code--->" + zip_postal_code);
                if(zip_postal_code != null
                        && zip_postal_code.trim().length() > 0) {
                    addressInfo.setZipCode(zip_postal_code);
                }

                String country_code = rs.getString(15);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, country_code--->" + country_code);
                if(country_code != null && country_code.trim().length() > 0) {
                    addressInfo.setCountryCode(country_code);
                }

                String payee_cid = rs.getString(16);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, payee_cid--->" + payee_cid);
                if(payee_cid != null && payee_cid.trim().length() > 0) {
                    addressInfo.setPayeeCId(payee_cid);
                }

                String addressSrNo = rs.getString(17);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Payee Data, addressSrNo--->" + addressSrNo);
                if(addressSrNo != null && addressSrNo.trim().length() > 0) {
                    addressInfo.setAddressSrNo(addressSrNo);
                }

                payeeList.add(chPayeeInfo);
            }
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Total Payees defined for cardholder--->"
                            + payeeList.size());
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage());
            ;
            throw ex;
        } finally {
            try {
                if(rs != null)
                    rs.close();
                if(pstmt != null)
                    pstmt.close();
            } catch(Exception ex) {
            } // end try, end
        }
        return payeeList;
    }




    public ServicesResponseObj getBillPaymentStatus(String cardNo,
            String isoSerial, String arn) throws Exception {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ServicesResponseObj respObj = new ServicesResponseObj();
        boolean byARN = false;

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting bill payment status, Card No--->"
                            + cardNo + "<---isoSerial--->" + isoSerial
                            + "<---arn--->" + arn);

            query
                    .append("select bp.payment_resp_date,bp.resp_payment_id,bp.resp_validity_code,bp.payee_id_actual,bp.payee_id_dir_sug,bp.resp_code,bp.resp_detail_id,bp.adj_payment_id,bp.adj_return_code,bp.sl_payee_id,bp.sl_payee_id_dir,bp.sl_payee_name,bp.processor_id,bs.batch_status_desc from bp_requests bp,batch_statuses bs where bp.card_no = ? and bp.status_id = bs.batch_status ");
            if(isoSerial != null && isoSerial.trim().length() > 0) {
                query.append("and trace_audit_no = ?");
            } else if(arn != null && arn.trim().length() > 0) {
                query.append("and retr_ref_no = ?");
                byARN = true;
            }
            pstmt = con.prepareCall(query.toString());
            pstmt.setString(1, cardNo);
            if(!byARN) {
                pstmt.setString(2, isoSerial);
            } else {
                pstmt.setString(2, arn);
            }
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query for getting bill payment status--->" + query);
            rs = pstmt.executeQuery();

            if(rs.next()) {
                respObj.setRespCode("00");
                respObj.setRespCode("OK");

                String payment_resp_date = rs.getString(1);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "payment_resp_date--->" + payment_resp_date);
                if(payment_resp_date != null
                        && payment_resp_date.trim().length() > 0) {
                    respObj.setBillPaymentRespDate(payment_resp_date);
                }

                String resp_payment_id = rs.getString(2);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "resp_payment_id--->" + resp_payment_id);
                if(resp_payment_id != null
                        && resp_payment_id.trim().length() > 0) {
                    respObj.setBillPaymentRespId(resp_payment_id);
                }

                String resp_validity_code = rs.getString(3);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "resp_validity_code--->" + resp_validity_code);
                if(resp_validity_code != null
                        && resp_validity_code.trim().length() > 0) {
                    respObj.setBillPaymentRespValidityCode(resp_validity_code);
                }

                String payee_id_actual = rs.getString(4);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "payee_id_actual--->" + payee_id_actual);
                if(payee_id_actual != null
                        && payee_id_actual.trim().length() > 0) {
                    respObj.setBillPaymentPayeeIdActual(payee_id_actual);
                }

                String payee_id_dir_sug = rs.getString(5);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "payee_id_dir_sug--->" + payee_id_dir_sug);
                if(payee_id_dir_sug != null
                        && payee_id_dir_sug.trim().length() > 0) {
                    respObj.setBillPaymentPayeeIdDirSugg(payee_id_dir_sug);
                }

                String resp_code = rs.getString(6);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "resp_code--->" + resp_code);
                if(resp_code != null && resp_code.trim().length() > 0) {
                    respObj.setBillPaymentRespCode(resp_code);
                }

                String resp_detail_id = rs.getString(7);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "resp_detail_id--->" + resp_detail_id);
                if(resp_detail_id != null && resp_detail_id.trim().length() > 0) {
                    respObj.setBillPaymentRespDetailId(resp_detail_id);
                }

                String adj_payment_id = rs.getString(8);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "adj_payment_id--->" + adj_payment_id);

                if(adj_payment_id != null && adj_payment_id.trim().length() > 0) {
                    respObj.setBillPaymentAdjustId(adj_payment_id);
                }

                String adj_return_code = rs.getString(9);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "adj_return_code--->" + adj_return_code);
                if(adj_return_code != null
                        && adj_return_code.trim().length() > 0) {
                    respObj.setBillPaymentAdjustRetCode(adj_return_code);
                }

                String sl_payee_id = rs.getString(10);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "sl_payee_id--->" + sl_payee_id);
                if(sl_payee_id != null && sl_payee_id.trim().length() > 0) {
                    respObj.setBillPaymentScanLinePayeeId(sl_payee_id);
                }

                String sl_payee_id_dir = rs.getString(11);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "sl_payee_id_dir--->" + sl_payee_id_dir);
                if(sl_payee_id_dir != null
                        && sl_payee_id_dir.trim().length() > 0) {
                    respObj.setBillPaymentScanLinePayeeIdDir(sl_payee_id_dir);
                }

                String sl_payee_name = rs.getString(12);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "sl_payee_name--->" + sl_payee_name);
                if(sl_payee_name != null && sl_payee_name.trim().length() > 0) {
                    respObj.setBillPaymentScanLinePayeeName(sl_payee_name);
                }

                String processor_id = rs.getString(13);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "processor_id--->" + processor_id);
                if(processor_id != null && processor_id.trim().length() > 0) {
                    respObj.setBillPaymentProcessorId(processor_id);
                }
                String status = rs.getString(14);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "status--->" + status);
                if(status != null && status.trim().length() > 0) {
                    respObj.setBillPaymentStatus(status);
                }
            } else{
                respObj.setRespCode("12");
                respObj.setRespCode("Invalid Transaction ID or ARN provided");
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage());
            ;
            throw ex;
        } finally {
            try {
                if(rs != null)
                    rs.close();
                if(pstmt != null)
                    pstmt.close();
            } catch(Exception ex) {
            } // end try, end
        }
        return respObj;
    }




    public String getPaymentResponseDescription(String paymentRespCode,
            String processorId) throws Exception {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String respDesc = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    " Method for getting description for payment response code--->"
                            + paymentRespCode + "<---Processor ID--->"
                            + processorId);
            query
                    .append("select resp_code_desc from bp_resp_codes where resp_code = ? and processor_id = ? ");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    " Query for getting description for payment response code --->"
                            + query);
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, paymentRespCode);
            pstmt.setString(2, processorId);

            rs = pstmt.executeQuery();

            if(rs.next()) {
                respDesc = rs.getString(1);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_FINEST),
                        " Description got --->" + respDesc);
            }
        } catch(Exception e) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in Getting Response Description --->" + e);
            throw e;
        } finally {
            try {
                if(rs != null)
                    rs.close();
                if(pstmt != null)
                    pstmt.close();
            } catch(Exception ex) {
            } // end try, end
        }
        return respDesc;
    }




    public String getPaymentDetailResponseDescription(String detailRespCode,
            String processorId) throws Exception {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String respDesc = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    " Method for getting description for payment detail response code--->"
                            + detailRespCode + "<---Processor ID--->"
                            + processorId);
            query
                    .append("select resp_detail_desc from bp_resp_details where resp_detail_id = ? and processor_id = ? ");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    " Query for getting description for payment detail response code --->"
                            + query);
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, detailRespCode);
            pstmt.setString(2, processorId);

            rs = pstmt.executeQuery();

            if(rs.next()) {
                respDesc = rs.getString(1);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_FINEST),
                        " Description got --->" + respDesc);
            }
        } catch(Exception e) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in Getting Response Description --->" + e);
            throw e;
        } finally {
            try {
                if(rs != null)
                    rs.close();
                if(pstmt != null)
                    pstmt.close();
            } catch(Exception ex) {
            } // end try, end
        }
        return respDesc;
    }




    public boolean performCardHolderVerification(String cardPrgId) {
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String aplyOfac = null;
        String applyAvs = null;

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for checking whether to perform cardholder verification--->"
                            + cardPrgId);
            query
                    .append("select calc_avs_flag,apply_ofac from card_programs where card_prg_id = ?");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query for checking whether to perform cardholder verification--->"
                            + query);
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, cardPrgId);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                applyAvs = rs.getString(1);
                aplyOfac = rs.getString(2);
                if(applyAvs == null || applyAvs.trim().length() == 0) {
                    applyAvs = "0";
                }
                if(aplyOfac == null || aplyOfac.trim().length() == 0) {
                    aplyOfac = "0";
                }
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for checking whether to perform cardholder verification- --- applyAvs got--->"
                                        + applyAvs
                                        + "<---aplyOfac ID got--->"
                                        + aplyOfac);
                if(applyAvs.equalsIgnoreCase("0")
                        && aplyOfac.equalsIgnoreCase("0")) {
                    return false;
                } else {
                    return true;
                }
            } else {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for checking whether to perform cardholder verification --- No value got for Apply OFAC & Apply AVS flags...");
                return false;
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in checking whether to perform cardholder verification --->"
                            + ex);
            return false;
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                    rs = null;
                }
                if(pstmt != null) {
                    pstmt.close();
                    pstmt = null;
                }
            } catch(Exception ex1) {
            }
        }
    }




    public String getBillPaymentAmount(String bpSerial, String cardNo)
            throws Exception {
        String amount = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer query = new StringBuffer();
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting bill payment amount for BP Serial-->"
                            + bpSerial + "<---Card No--->" + cardNo);
            query
                    .append("select amount from bp_requests where trans_id = ? and card_no = ?");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query for getting bill amount-->" + query);
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, bpSerial);
            pstmt.setString(2, cardNo);

            rs = pstmt.executeQuery();

            if(rs.next()) {
                if(rs.getString(1) != null
                        && rs.getString(1).trim().length() > 0) {
                    amount = rs.getString(1).trim();
                }
            }
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "BP Amount got--->" + amount);
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception in getting BP Amount--->" + ex);
            throw new Exception(
                    "Unable to get bill payment amount for provided transaction serial and card number--->"
                            + ex);
        } finally {
            if(rs != null) {
                rs.close();
                rs = null;
            }
            if(pstmt != null) {
                pstmt.close();
                pstmt = null;
            }
        }
        return amount;
    }




    public ServicesResponseObj generateEntitlements(ServicesRequestObj reqObj)
            throws Exception {
        StringBuffer query = new StringBuffer();
        CallableStatement cstmt = null;
        ResultSet rs = null;
        ServicesResponseObj respObj = new ServicesResponseObj();

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for genenrating entitlements ---- Load Serial No-->"
                            + reqObj.getEntitlementLoadSerial()
                            + "<---Merchant Id--->"
                            + reqObj.getCardAcceptorId() + "<---MCC--->"
                            + reqObj.getMcc() + "<---Device Id--->"
                            + reqObj.getDeviceId() + "<---Acquirer Id--->"
                            + reqObj.getAcquirerId());

            query
                    .append("execute procedure generate_entitlement (pload_sr_no = ?,pmerchant_id = ?,pmcc = ?,pdevice_id = ?,pacquirer_id = ?)");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query -- > " + query);

            cstmt = con.prepareCall(query.toString());

            cstmt.setString(1, reqObj.getEntitlementLoadSerial());
            cstmt.setString(2, reqObj.getCardAcceptorId());
            cstmt.setString(3, reqObj.getMcc());
            cstmt.setString(4, reqObj.getDeviceId());
            cstmt.setString(5, reqObj.getAcquirerId());

            rs = cstmt.executeQuery();

            if(rs.next()) {
                // get the response code and description
                String respCode = rs.getString(1);
                String desc = rs.getString(2);
                String totalCards = rs.getString(3);
                String entlCards = rs.getString(4);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Method for genenrating entitlements ---- Response Got---- Code--->"
                                + respCode + "<----Description--->" + desc
                                + "<----Total Entitled Cards--->" + entlCards
                                + "<----Total Cards--->" + totalCards);
                respObj.setRespCode(respCode);
                respObj.setRespDesc(desc);
                respObj.setTotalEntitledCards(entlCards);
                respObj.setTotalCards(totalCards);
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception genenrating entitlements --->" + ex);
            throw new Exception("Unable to generate entitlements ---->" + ex);
        } finally {
            try {
                if(rs != null)
                    rs.close();
                if(cstmt != null)
                    cstmt.close();
            } catch(Exception ex) {
            } // end try, end catch
        }
        return respObj;
    }




    public ServicesResponseObj redeemEntitlements(ServicesRequestObj reqObj)
            throws Exception {
        StringBuffer query = new StringBuffer();
        CallableStatement cstmt = null;
        ResultSet rs = null;
        ServicesResponseObj respObj = new ServicesResponseObj();

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for redeeming entitlements ---- Entitlement ID-->"
                            + reqObj.getEntitlementSerialNo()
                            + "<---Card No--->" + reqObj.getCardNo()
                            + "<---Entitlement Qunatity--->"
                            + reqObj.getEntitlementRedeemQuantity()
                            + "<---Merchant Id--->"
                            + reqObj.getCardAcceptorId() + "<---MCC--->"
                            + reqObj.getMcc() + "<---Device Id--->"
                            + reqObj.getDeviceId() + "<---Acquirer Id--->"
                            + reqObj.getAcquirerId());

            query
                    .append("execute procedure redeem_entitlement (pcard_no = ?,pent_id = ?,pent_rdem_quantity = ?,pmerchant_id = ?,pmcc = ?,pdevice_id = ?,pacquirer_id = ?)");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Query -- > " + query);

            cstmt = con.prepareCall(query.toString());

            cstmt.setString(1, reqObj.getCardNo());
            cstmt.setString(2, reqObj.getEntitlementSerialNo());
            cstmt.setString(3, reqObj.getEntitlementRedeemQuantity());
            cstmt.setString(4, reqObj.getCardAcceptorId());
            cstmt.setString(5, reqObj.getMcc());
            cstmt.setString(6, reqObj.getDeviceId());
            cstmt.setString(7, reqObj.getAcquirerId());

            rs = cstmt.executeQuery();

            if(rs.next()) {
                // get the response code and description
                String respCode = rs.getString(1);
                String desc = rs.getString(2);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Method for redeeming entitlements ---- Response Got---- Code--->"
                                + respCode + "<----Description--->" + desc);
                respObj.setRespCode(respCode);
                respObj.setRespDesc(desc);
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_WARNING),
                    "Exception redeeming entitlements --->" + ex);
            throw new Exception("Unable to redeem entitlements ---->" + ex);
        } finally {
            try {
                if(rs != null)
                    rs.close();
                if(cstmt != null)
                    cstmt.close();
            } catch(Exception ex) {
            } // end try, end catch
        }
        return respObj;
    }

    /**
     * This method builds the query for updating the card holder attributes such as
     * his first name, last name and other information.
     * @param reqObj ServicesRequestObj
     * @return String
     */
    public ServicesResponseObj updateCHProfile(ServicesRequestObj reqObj) throws Exception{
        PreparedStatement pstmt = null;
        Properties data = new Properties();
        ServicesResponseObj respObj = new ServicesResponseObj();
        try {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Method for building update profile statement<---getFirstName---> " +
                                            reqObj.getFirstName()
                                            + "<----getMiddleName---> " +
                                            reqObj.getMiddleName()
                                            + "<----getLastName----> " +
                                            reqObj.getLastName()
                                            + "<---getDob---> " +
                                            reqObj.getDob()
                                            + "<---getAddressStreet1---> " +
                                            reqObj.getAddressStreet1()
                                            + "<---getAddressStreet2---> " +
                                            reqObj.getAddressStreet2()
                                            + "<---getCity---> " +
                                            reqObj.getCity()
                                            + "<---getStateCode---> " +
                                            reqObj.getStateCode()
                                            + "<---getZipCode---> " +
                                            reqObj.getZipCode()
                                            + "<---getHomePhone---> " +
                                            reqObj.getHomePhone()
                                            + "<---getWorkPhone---> " +
                                            reqObj.getWorkPhone()
                                            + "<---getEmail---> " +
                                            reqObj.getEmail()
                                            + "<---getGender---> " +
                                            reqObj.getGender()
                                            + "<---getMotherMaidenName---> " +
                                            reqObj.getMotherMaidenName()
                                            + "<---getSsn---> " +
                                            reqObj.getSsn()
                                            + "<---getCountry---> " +
                                            reqObj.getCountry()
                                            + "<---getBillingAddress1---> " +
                                            reqObj.getBillingAddress1()
                                            + "<---getBillingAddress2---> " +
                                            reqObj.getBillingAddress2()
                                            + "<---getBillingCity---> " +
                                            reqObj.getBillingCity()
                                            + "<---getBillingCountrycode---> " +
                                            reqObj.getBillingCountrycode()
                                            + "<---getBillingState---> " +
                                            reqObj.getBillingState()
                                            + "<---getBillingZipCode---> " +
                                            reqObj.getBillingZipCode()
                                            + "<---getForeignId---> " +
                                            reqObj.getForeignId()
                                            + "<---getForeignIdType---> " +
                                            reqObj.getForeignIdType()
                                            + "<---getForeignCountryCode---> " +
                                            reqObj.getForeignCountryCode()
                                            + "<---getDrivingLicesneNo---> " +
                                            reqObj.getDrivingLicesneNo()
                                            + "<---getDrivingLicesneState---> " +
                                            reqObj.getDrivingLicesneState());

            if (reqObj.getFirstName() != null &&
                !reqObj.getFirstName().trim().equals("")) {
                data.setProperty("first_name1", reqObj.getFirstName());
            }
            if (reqObj.getMiddleName() != null &&
                !reqObj.getMiddleName().trim().equals("")) {
                data.setProperty("middle_name1", reqObj.getMiddleName());
            }
            if (reqObj.getLastName() != null &&
                !reqObj.getLastName().trim().equals("")) {
                data.setProperty("last_name1", reqObj.getLastName());
            }
            if (reqObj.getDob() != null && !reqObj.getDob().trim().equals("")) {
                try {
                    String dob = CommonUtilities.convertDateFormat(Constants.
                            DATE_FORMAT,
                            Constants.WEB_DATE_FORMAT, reqObj.getDob());
                    data.setProperty("date_of_birth", dob);
                } catch (Exception exp) {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_FINEST),
                            "Exception in converting date format -- > " +
                            exp.getMessage());
                }
            } //end if
            if (reqObj.getAddressStreet1() != null &&
                !reqObj.getAddressStreet1().trim().equals("")) {
                data.setProperty("address1", reqObj.getAddressStreet1());
            }
            if (reqObj.getAddressStreet2() != null &&
                !reqObj.getAddressStreet2().trim().equals("")) {
                data.setProperty("address2", reqObj.getAddressStreet2());
            }
            if (reqObj.getCity() != null && !reqObj.getCity().trim().equals("")) {
                data.setProperty("city", reqObj.getCity());
            }
            if (reqObj.getStateCode() != null &&
                !reqObj.getStateCode().trim().equals("")) {
                data.setProperty("state_code", reqObj.getStateCode());
            }
            if (reqObj.getZipCode() != null &&
                !reqObj.getZipCode().trim().equals("")) {
                data.setProperty("zip_postal_code", reqObj.getZipCode());
            }
            if (reqObj.getHomePhone() != null &&
                !reqObj.getHomePhone().trim().equals("")) {
                data.setProperty("home_phone_no", reqObj.getHomePhone());
            }
            if (reqObj.getWorkPhone() != null &&
                !reqObj.getWorkPhone().trim().equals("")) {
                data.setProperty("work_phone_no", reqObj.getWorkPhone());
            }
            if (reqObj.getEmail() != null &&
                !reqObj.getEmail().trim().equals("")) {
                data.setProperty("email", reqObj.getEmail());
            }
            if (reqObj.getGender() != null &&
                !reqObj.getGender().trim().equals("")) {
                data.setProperty("gender", reqObj.getGender());
            }
            if (reqObj.getMotherMaidenName() != null &&
                !reqObj.getMotherMaidenName().trim().equals("")) {
                data.setProperty("mother_maiden_nam",
                                 reqObj.getMotherMaidenName());
            }
            if (reqObj.getSsn() != null && !reqObj.getSsn().trim().equals("")) {
                data.setProperty("ssn_nid_no", reqObj.getSsn());
            }
            if (reqObj.getCountry() != null &&
                !reqObj.getCountry().trim().equals("")) {
                data.setProperty("country_code", reqObj.getCountry());
            }
            if (reqObj.getBillingAddress1() != null
                && !reqObj.getBillingAddress1().trim().equals("")) {
                data.setProperty("bill_address1", reqObj.getBillingAddress1());
            }
            if (reqObj.getBillingAddress2() != null
                && !reqObj.getBillingAddress2().trim().equals("")) {
                data.setProperty("bill_address2", reqObj.getBillingAddress2());
            }
            if (reqObj.getBillingCity() != null
                && !reqObj.getBillingCity().trim().equals("")) {
                data.setProperty("bill_city", reqObj.getBillingCity());
            }
            if (reqObj.getBillingCountrycode() != null
                && !reqObj.getBillingCountrycode().trim().equals("")) {
                data.setProperty("bill_country_code",
                                 reqObj.getBillingCountrycode());
            }
            if (reqObj.getBillingState() != null
                && !reqObj.getBillingState().trim().equals("")) {
                data.setProperty("bill_state_code", reqObj.getBillingState());
            }
            if (reqObj.getBillingZipCode() != null
                && !reqObj.getBillingZipCode().trim().equals("")) {
                data.setProperty("bill_zip_code", reqObj.getBillingZipCode());
            }
            if (reqObj.getForeignId() != null
                && !reqObj.getForeignId().trim().equals("")) {
                data.setProperty("foreign_id", reqObj.getForeignId());
            }
            if (reqObj.getForeignIdType() != null
                && !reqObj.getForeignIdType().trim().equals("")) {
                data.setProperty("foreign_id_type", reqObj.getForeignIdType());
            }
            if (reqObj.getForeignCountryCode() != null
                && !reqObj.getForeignCountryCode().trim().equals("")) {
                data.setProperty("f_country_code", reqObj.getForeignCountryCode());
            }
            if (reqObj.getDrivingLicesneNo() != null
                && !reqObj.getDrivingLicesneNo().trim().equals("")) {
                data.setProperty("driving_license_no",
                                 reqObj.getDrivingLicesneNo());
            }
            if (reqObj.getDrivingLicesneState() != null
                && !reqObj.getDrivingLicesneState().trim().equals("")) {
                data.setProperty("driving_license_st",
                                 reqObj.getDrivingLicesneState());
            }
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            " Method for updating profile, Hastable size--->" +
                                            data.size());
            if (data != null && data.size() > 0) {
                StringBuffer query = new StringBuffer();
                query.append(" update cards set ");
                Enumeration columns = data.propertyNames();
                while (columns.hasMoreElements()) {
                    query.append(columns.nextElement());
                    query.append(" = ?,");
                }
                query.deleteCharAt(query.length() - 1);
                query.append(" where card_no= ? ");

                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                " Method for updating profile, Query build--->" +
                                                query);
                pstmt = con.prepareStatement(query.toString());
                columns = data.propertyNames();
                int index = 1;
                while (columns.hasMoreElements()) {
                    String columnName = (String) columns.nextElement();
                    pstmt.setString(index++,data.getProperty(columnName));
                }
                pstmt.setString(index++,reqObj.getCardNo());

                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                " Method for updating profile, Executing query...");
                pstmt.executeUpdate();
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                " Method for updating profile, query executed successfully...");
            } else{
                respObj.setEmptyProfile(true);
            }
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
        } catch (Exception ex) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_WARNING),
                                                " Exception in updating profile--->" +
                                                ex);
            throw ex;
        }finally{
            if(pstmt != null){
                pstmt.close();
                pstmt = null;
            }
        }
        return respObj;
    }

   public String getCurrentExpiryDate(String cardNumber) {
        String expiryDate = null;
        StringBuffer query = new StringBuffer();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Method for getting current exipiry date for card --->" +
                                            cardNumber);
            query.append(
                    "select expiry_on from cards where card_no = ");
            CommonUtilities.buildQueryInfo(query, cardNumber, true);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Query for getting current exipiry date for card --->" +
                                            query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query.toString());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "ResultSet getting current exipiry date for card --->" +
                                            rs);
            if (rs.next()) {
                if (rs.getString(1) != null &&
                    rs.getString(1).trim().length() > 0) {
                    expiryDate = rs.getString(1).trim();
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Expiry Date Found--->" +
                            expiryDate);
                }
            }
        } catch (Exception ex) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_WARNING),
                                            "Exception in getting current exipiry date for card --->" +
                                            ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
            } catch (SQLException ex1) {
            }
        }
        return expiryDate;
    }

    public CardInfoObj getCardInfo(String cardNumber) throws Exception {
        CardInfoObj cardInfo = new CardInfoObj();
        StringBuffer query = new StringBuffer();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_FINEST),
                                            "Method for getting Card Info for provided Card Number--->" +
                                            cardNumber);
            query.append(
                    "select card_prg_id,card_status_atm,card_status_pos,ofac_status,avs_status,expiry_on,card_batch_no,card_gen_mode,src_card_no from cards where card_no = ?");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Query for getting Card Info for provided Card Number--->" +
                                            query);
            stmt = con.prepareStatement(query.toString());
            stmt.setString(1,cardNumber);
            rs = stmt.executeQuery();
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "ResultSet for getting Card Info for provided Card Number--->" +
                                            rs);
            if (rs.next()) {
                String cardPrgId = rs.getString(1);
                if (cardPrgId != null && cardPrgId.trim().length() > 0) {
                    cardInfo.setCardPrgId(cardPrgId);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Card Program ID--->" +
                            cardInfo.getCardPrgId());
                }
                String atmStatus = rs.getString(2);
                if (atmStatus != null && atmStatus.trim().length() > 0) {
                    cardInfo.setCardStatusAtm(atmStatus);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Card Status ATM--->" +
                            cardInfo.getCardStatusAtm());
                }
                String posStatus = rs.getString(3);
                if (posStatus != null && posStatus.trim().length() > 0) {
                    cardInfo.setCardStatusPos(posStatus);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Card POS Status--->" +
                            cardInfo.getCardStatusPos());
                }
                String ofacStatus = rs.getString(4);
                if (ofacStatus != null && ofacStatus.trim().length() > 0) {
                    cardInfo.setOfacStatus(ofacStatus);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "OFAC Status--->" +
                            cardInfo.getOfacStatus());
                }
                String avsStatus = rs.getString(5);
                if (avsStatus != null && avsStatus.trim().length() > 0) {
                    cardInfo.setAvsStatus(avsStatus);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "AVS Status--->" +
                            cardInfo.getAvsStatus());
                }
                String expiryOn = rs.getString(6);
                if (expiryOn != null && expiryOn.trim().length() > 0) {
                    cardInfo.setExpiryOn(expiryOn);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Expiry On--->" +
                            cardInfo.getExpiryOn());
                }
                String batchNo = rs.getString(7);
                if (batchNo != null && batchNo.trim().length() > 0) {
                    cardInfo.setCardBatchNo(batchNo);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Card Batch No--->" +
                            cardInfo.getCardBatchNo());
                }
                String cardGenMode = rs.getString(8);
                if (cardGenMode != null && cardGenMode.trim().length() > 0) {
                    cardInfo.setCardGenerationMode(cardGenMode);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Card Generation Mode--->" +
                            cardInfo.getCardGenerationMode());
                }
                String srcCard = rs.getString(9);
                if (srcCard != null && srcCard.trim().length() > 0) {
                    cardInfo.setExistingCardNo(srcCard);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Existing Card No--->" +
                            cardInfo.getExistingCardNo());
                }
            }
        } catch (Exception ex) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_WARNING),
                                            "Exception in getting Card Info for provided Card Number--->" +
                                            ex);
            throw new Exception(
                    "Unable to get card information for provided card--->" + ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
            } catch (SQLException ex1) {
            }
        }
        return cardInfo;
    }

    public String getInitialBatchLoadAmount(String batchNo) {
        String initAmt = null;
        StringBuffer query = new StringBuffer();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Method for getting initial load amount for card batch number with intial batch Flag 'A'--->" +
                                            batchNo);
            query.append(
                    "select init_load_amount from card_batches where card_batch_no = ? and init_load_flag = ?");
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Query for getting initial load amount for card batch number --->" +
                                            query);
            stmt = con.prepareStatement(query.toString());
            stmt.setString(1,batchNo);
            stmt.setString(2,Constants.ACT_INIT_LOAD);
            rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getString(1) != null &&
                    rs.getString(1).trim().length() > 0) {
                    initAmt = rs.getString(1).trim();
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Amount got--->" +
                            initAmt);
                }
            }
        } catch (Exception ex) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_WARNING),
                                            "Exception in getting initial load amount for card batch number --->" +
                                            ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
            } catch (SQLException ex1) {
            }
        }
        return initAmt;
    }

    public ServicesRequestObj getExistingProfile(String cardNo) throws Exception {
        ServicesRequestObj existProfile = new ServicesRequestObj();
        StringBuffer query = new StringBuffer();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Method for getting existing profile for card no--->" +
                                            cardNo);
            query.append("SELECT first_name1,middle_name1,last_name1,address1,address2,state_code,city,work_phone_no,");
            query.append("zip_postal_code,home_phone_no,email,gender, mother_maiden_nam, date_of_birth, ssn_nid_no,");
            query.append("f_country_code,foreign_id_type,foreign_id, question_id,question_answer, bill_address1,");
            query.append("bill_address2, bill_city, bill_country_code, bill_state_code, bill_zip_code, country_code, driving_license_no, driving_license_st ");
            query.append("FROM cards WHERE card_no= ?");

            pstmt = con.prepareStatement(query.toString());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Query for getting existing profile for card no--->" +
                                            query);

            pstmt.setString(1, cardNo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String fName = rs.getString(1);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "FirstName got--->" + fName);

                if (fName != null && fName.trim().length() > 0) {
                    existProfile.setFirstName(fName);
                }
                String mName = rs.getString(2);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "MiddleName got--->" + mName);

                if (mName != null && mName.trim().length() > 0) {
                    existProfile.setMiddleName(mName);
                }
                String lName = rs.getString(3);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "LastName got--->" + lName);

                if (lName != null && lName.trim().length() > 0) {
                    existProfile.setLastName(lName);
                }
                String add1 = rs.getString(4);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "AddressStreet1 got--->" + add1);

                if (add1 != null && add1.trim().length() > 0) {
                    existProfile.setAddressStreet1(add1);
                }
                String add2 = rs.getString(5);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "AddressStreet2 got--->" + add2);

                if (add2 != null && add2.trim().length() > 0) {
                    existProfile.setAddressStreet2(add2);
                }

                String state = rs.getString(6);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "StateCode got--->" + state);

                if (state != null && state.trim().length() > 0) {
                    existProfile.setStateCode(state);
                }

                String city = rs.getString(7);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "City got--->" + city);

                if (city != null && city.trim().length() > 0) {
                    existProfile.setCity(city);
                }

                String wPh = rs.getString(8);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "WorkPhone got--->" + wPh);

                if (wPh != null && wPh.trim().length() > 0) {
                    existProfile.setWorkPhone(wPh);
                }

                String zip = rs.getString(9);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "ZipCode got--->" + zip);

                if (zip != null && zip.trim().length() > 0) {
                    existProfile.setZipCode(zip);
                }

                String hPh = rs.getString(10);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "HomePhone got--->" + hPh);

                if (hPh != null && hPh.trim().length() > 0) {
                    existProfile.setHomePhone(hPh);
                }

                String email = rs.getString(11);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "Email got--->" + email);

                if (email != null && email.trim().length() > 0) {
                    existProfile.setEmail(email);
                }

                String gender = rs.getString(12);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "Gender got--->" + gender);

                if (gender != null && gender.trim().length() > 0) {
                    existProfile.setGender(gender);
                }

                String mmn = rs.getString(13);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "MotherMaidenName got--->" +
                                                mmn);

                if (mmn != null && mmn.trim().length() > 0) {
                    existProfile.setMotherMaidenName(mmn);
                }

                String dob = rs.getString(14);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "Dob got--->" + dob);

                if (dob != null && dob.trim().length() > 0) {
                    existProfile.setDob(CommonUtilities.convertDateFormat(Constants.WEB_DATE_FORMAT,Constants.DATE_FORMAT,dob));
                }

                String ssn = rs.getString(15);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "Ssn got--->" + ssn);

                if (ssn != null && ssn.trim().length() > 0) {
                    existProfile.setSsn(ssn);
                }

                String frCountry = rs.getString(16);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "ForeignCountryCode got--->" +
                                                frCountry);

                if (frCountry != null && frCountry.trim().length() > 0) {
                    existProfile.setForeignCountryCode(frCountry);
                }

                String frId = rs.getString(17);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "ForeignId got--->" + frId);

                if (frId != null && frId.trim().length() > 0) {
                    existProfile.setForeignId(frId);
                }

                String frIdType = rs.getString(18);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "ForeignIdType got--->" +
                                                frIdType);

                if (frIdType != null && frIdType.trim().length() > 0) {
                    existProfile.setForeignIdType(frIdType);
                }

                String qId = rs.getString(19);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "QuestionId got--->" + qId);

                if (qId != null && qId.trim().length() > 0) {
                    existProfile.setQuestionId(qId);
                }

                String qAns = rs.getString(20);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "QuestionAnswer got--->" + qAns);

                if (qAns != null && qAns.trim().length() > 0) {
                    existProfile.setQuestionAnswer(qAns);
                }

                String billAdd1 = rs.getString(21);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "BillingAddress1 got--->" +
                                                billAdd1);

                if (billAdd1 != null && billAdd1.trim().length() > 0) {
                    existProfile.setBillingAddress1(billAdd1);
                }

                String billAdd2 = rs.getString(22);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "BillingAddress2 got--->" +
                                                billAdd2);

                if (billAdd2 != null && billAdd2.trim().length() > 0) {
                    existProfile.setBillingAddress2(billAdd2);
                }

                String billCity = rs.getString(23);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "BillingCity got--->" +
                                                billCity);

                if (billCity != null && billCity.trim().length() > 0) {
                    existProfile.setBillingCity(billCity);
                }

                String billCountry = rs.getString(24);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "BillingCountrycode got--->" +
                                                billCountry);

                if (billCountry != null && billCountry.trim().length() > 0) {
                    existProfile.setBillingCountrycode(billCountry);
                }

                String billState = rs.getString(25);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "BillingState got--->" +
                                                billState);

                if (billState != null && billState.trim().length() > 0) {
                    existProfile.setBillingState(billState);
                }

                String billZip = rs.getString(26);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "BillingZipCode got--->" +
                                                billZip);

                if (billZip != null && billZip.trim().length() > 0) {
                    existProfile.setBillingZipCode(billZip);
                }

                String country = rs.getString(27);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "Country got--->" + country);

                if (country != null && country.trim().length() > 0) {
                    existProfile.setCountry(country);
                }

                String drLsNo = rs.getString(28);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "DrivingLicesneNo got--->" +
                                                drLsNo);

                if (drLsNo != null && drLsNo.trim().length() > 0) {
                    existProfile.setDrivingLicesneNo(drLsNo);
                }

                String drLsstate = rs.getString(29);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                        LOG_CONFIG),
                                                "DrivingLicesneState got--->" +
                                                drLsstate);

                if (drLsstate != null && drLsstate.trim().length() > 0) {
                    existProfile.setDrivingLicesneState(drLsstate);
                }
            }
        } catch (Exception ex) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Exception in getting existing profile for card no--->" +
                                            ex);
            throw new Exception(
                    "Unable to get existing profile for provided card--->" + ex);
        } finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (pstmt != null) {
                pstmt.close();
                pstmt = null;
            }
        }
        return existProfile;
    }

    public ServicesRequestObj getUpdatedProfileFields(ServicesRequestObj
            newProfile,
            ServicesRequestObj existingProfile) throws Exception {
        ServicesRequestObj updProfile = new ServicesRequestObj();
        try {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Method for getting updated profile fields by comparing new & existing profiles...");

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New FirstName --->" +
                                            newProfile.getFirstName() +
                                            "<---Old FirstName --->" +
                                            existingProfile.getFirstName());

            if (newProfile.getFirstName() != null &&
                newProfile.getFirstName().trim().length() > 0) {
                if (existingProfile.getFirstName() == null ||
                    !newProfile.
                    getFirstName().trim().equalsIgnoreCase(existingProfile.
                        getFirstName().trim())) {
                    updProfile.setFirstName(newProfile.getFirstName().trim());
                }
            }
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New MiddleName --->" +
                                            newProfile.getMiddleName() +
                                            "<---Old MiddleName --->" +
                                            existingProfile.getMiddleName());

            if (newProfile.getMiddleName() != null &&
                newProfile.getMiddleName().trim().length() > 0) {
                if (existingProfile.getMiddleName() == null ||
                    !newProfile.
                    getMiddleName().trim().equalsIgnoreCase(existingProfile.
                        getMiddleName().trim())) {
                    updProfile.setMiddleName(newProfile.getMiddleName().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New LastName --->" +
                                            newProfile.getLastName() +
                                            "<---Old LastName --->" +
                                            existingProfile.getLastName());

            if (newProfile.getLastName() != null &&
                newProfile.getLastName().trim().length() > 0) {
                if (existingProfile.getLastName() == null ||
                    !newProfile.
                    getLastName().trim().equalsIgnoreCase(existingProfile.
                        getLastName().trim())) {
                    updProfile.setLastName(newProfile.getLastName().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New AddressStreet1 --->" +
                                            newProfile.getAddressStreet1() +
                                            "<---Old AddressStreet1 --->" +
                                            existingProfile.getAddressStreet1());

            if (newProfile.getAddressStreet1() != null &&
                newProfile.getAddressStreet1().trim().length() > 0) {
                if (existingProfile.getAddressStreet1() == null ||
                    !newProfile.
                    getAddressStreet1().trim().equalsIgnoreCase(existingProfile.
                        getAddressStreet1().trim())) {
                    updProfile.setAddressStreet1(newProfile.getAddressStreet1().
                                                 trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New AddressStreet2 --->" +
                                            newProfile.getAddressStreet2() +
                                            "<---Old AddressStreet2 --->" +
                                            existingProfile.getAddressStreet2());

            if (newProfile.getAddressStreet2() != null &&
                newProfile.getAddressStreet2().trim().length() > 0) {
                if (existingProfile.getAddressStreet2() == null ||
                    !newProfile.
                    getAddressStreet2().trim().equalsIgnoreCase(existingProfile.
                        getAddressStreet2().trim())) {
                    updProfile.setAddressStreet2(newProfile.getAddressStreet2().
                                                 trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New StateCode --->" +
                                            newProfile.getStateCode() +
                                            "<---Old StateCode --->" +
                                            existingProfile.getStateCode());

            if (newProfile.getStateCode() != null &&
                newProfile.getStateCode().trim().length() > 0) {
                if (existingProfile.getStateCode() == null ||
                    !newProfile.
                    getStateCode().trim().equalsIgnoreCase(existingProfile.
                        getStateCode().trim())) {
                    updProfile.setStateCode(newProfile.getStateCode().trim());
                }
            }
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New City --->" +
                                            newProfile.getCity() +
                                            "<---Old City --->" +
                                            existingProfile.getCity());

            if (newProfile.getCity() != null &&
                newProfile.getCity().trim().length() > 0) {
                if (existingProfile.getCity() == null ||
                    !newProfile.
                    getCity().trim().equalsIgnoreCase(existingProfile.
                        getCity().trim())) {
                    updProfile.setCity(newProfile.getCity().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New WorkPhone --->" +
                                            newProfile.getWorkPhone() +
                                            "<---Old WorkPhone --->" +
                                            existingProfile.getWorkPhone());

            if (newProfile.getWorkPhone() != null &&
                newProfile.getWorkPhone().trim().length() > 0) {
                if (existingProfile.getWorkPhone() == null ||
                    !newProfile.
                    getWorkPhone().trim().equalsIgnoreCase(existingProfile.
                        getWorkPhone().trim())) {
                    updProfile.setWorkPhone(newProfile.getWorkPhone().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New ZipCode --->" +
                                            newProfile.getZipCode() +
                                            "<---Old ZipCode --->" +
                                            existingProfile.getZipCode());

            if (newProfile.getZipCode() != null &&
                newProfile.getZipCode().trim().length() > 0) {
                if (existingProfile.getZipCode() == null ||
                    !newProfile.
                    getZipCode().trim().equalsIgnoreCase(existingProfile.
                        getZipCode().trim())) {
                    updProfile.setZipCode(newProfile.getZipCode().trim());
                }
            }
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New Email --->" +
                                            newProfile.getEmail() +
                                            "<---Old Email --->" +
                                            existingProfile.getEmail());

            if (newProfile.getEmail() != null &&
                newProfile.getEmail().trim().length() > 0) {
                if (existingProfile.getEmail() == null ||
                    !newProfile.
                    getEmail().trim().equalsIgnoreCase(existingProfile.
                        getEmail().trim())) {
                    updProfile.setEmail(newProfile.getEmail().trim());
                }
            }
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New HomePhone --->" +
                                            newProfile.getHomePhone() +
                                            "<---Old HomePhone --->" +
                                            existingProfile.getHomePhone());

            if (newProfile.getHomePhone() != null &&
                newProfile.getHomePhone().trim().length() > 0) {
                if (existingProfile.getHomePhone() == null ||
                    !newProfile.
                    getHomePhone().trim().equalsIgnoreCase(existingProfile.
                        getHomePhone().trim())) {
                    updProfile.setHomePhone(newProfile.getHomePhone().trim());
                }
            }
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New Gender --->" +
                                            newProfile.getGender() +
                                            "<---Old Gender --->" +
                                            existingProfile.getGender());

            if (newProfile.getGender() != null &&
                newProfile.getGender().trim().length() > 0) {
                if (existingProfile.getGender() == null ||
                    !newProfile.
                    getGender().trim().equalsIgnoreCase(existingProfile.
                        getGender().trim())) {
                    updProfile.setGender(newProfile.getGender().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New MotherMaidenName --->" +
                                            newProfile.getMotherMaidenName() +
                                            "<---Old MotherMaidenName --->" +
                                            existingProfile.getMotherMaidenName());

            if (newProfile.getMotherMaidenName() != null &&
                newProfile.getMotherMaidenName().trim().length() > 0) {
                if (existingProfile.getMotherMaidenName() == null ||
                    !newProfile.
                    getMotherMaidenName().trim().equalsIgnoreCase(
                        existingProfile.
                        getMotherMaidenName().trim())) {
                    updProfile.setMotherMaidenName(newProfile.
                            getMotherMaidenName().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New Dob --->" + newProfile.getDob() +
                                            "<---Old Dob --->" +
                                            existingProfile.getDob());

            if (newProfile.getDob() != null &&
                newProfile.getDob().trim().length() > 0) {
                if (existingProfile.getDob() == null ||
                    !newProfile.
                    getDob().trim().equalsIgnoreCase(existingProfile.
                        getDob().trim())) {
                    updProfile.setDob(newProfile.getDob().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New Ssn --->" + newProfile.getSsn() +
                                            "<---Old Ssn --->" +
                                            existingProfile.getSsn());

            if (newProfile.getSsn() != null &&
                newProfile.getSsn().trim().length() > 0) {
                if (existingProfile.getSsn() == null ||
                    !newProfile.
                    getSsn().trim().equalsIgnoreCase(existingProfile.
                        getSsn().trim())) {
                    updProfile.setSsn(newProfile.getSsn().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New ForeignCountryCode --->" +
                                            newProfile.getForeignCountryCode() +
                                            "<---Old ForeignCountryCode --->" +
                                            existingProfile.
                                            getForeignCountryCode());

            if (newProfile.getForeignCountryCode() != null &&
                newProfile.getForeignCountryCode().trim().length() > 0) {
                if (existingProfile.getForeignCountryCode() == null ||
                    !newProfile.
                    getForeignCountryCode().trim().equalsIgnoreCase(
                        existingProfile.
                        getForeignCountryCode().trim())) {
                    updProfile.setForeignCountryCode(newProfile.
                            getForeignCountryCode().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New ForeignId --->" +
                                            newProfile.getForeignId() +
                                            "<---Old ForeignId --->" +
                                            existingProfile.getForeignId());

            if (newProfile.getForeignId() != null &&
                newProfile.getForeignId().trim().length() > 0) {
                if (existingProfile.getForeignId() == null ||
                    !newProfile.
                    getForeignId().trim().equalsIgnoreCase(existingProfile.
                        getForeignId().trim())) {
                    updProfile.setForeignId(newProfile.getForeignId().
                                                trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New ForeignIdType --->" +
                                            newProfile.getForeignIdType() +
                                            "<---Old ForeignIdType --->" +
                                            existingProfile.getForeignIdType());

            if (newProfile.getForeignIdType() != null &&
                newProfile.getForeignIdType().trim().length() > 0) {
                if (existingProfile.getForeignIdType() == null ||
                    !newProfile.
                    getForeignIdType().trim().equalsIgnoreCase(existingProfile.
                        getForeignIdType().trim())) {
                    updProfile.setForeignIdType(newProfile.getForeignIdType().
                                                trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New QuestionId --->" +
                                            newProfile.getQuestionId() +
                                            "<---Old QuestionId --->" +
                                            existingProfile.getQuestionId());

            if (newProfile.getQuestionId() != null &&
                newProfile.getQuestionId().trim().length() > 0) {
                if (existingProfile.getQuestionId() == null ||
                    !newProfile.
                    getQuestionId().trim().equalsIgnoreCase(existingProfile.
                        getQuestionId().trim())) {
                    updProfile.setQuestionId(newProfile.getQuestionId().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New QuestionAnswer --->" +
                                            newProfile.getQuestionAnswer() +
                                            "<---Old QuestionAnswer --->" +
                                            existingProfile.getQuestionAnswer());

            if (newProfile.getQuestionAnswer() != null &&
                newProfile.getQuestionAnswer().trim().length() > 0) {
                if (existingProfile.getQuestionAnswer() == null ||
                    !newProfile.
                    getQuestionAnswer().trim().equalsIgnoreCase(existingProfile.
                        getQuestionAnswer().trim())) {
                    updProfile.setQuestionAnswer(newProfile.getQuestionAnswer().
                                                 trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New BillingAddress1 --->" +
                                            newProfile.getBillingAddress1() +
                                            "<---Old BillingAddress1 --->" +
                                            existingProfile.getBillingAddress1());

            if (newProfile.getBillingAddress1() != null &&
                newProfile.getBillingAddress1().trim().length() > 0) {
                if (existingProfile.getBillingAddress1() == null ||
                    !newProfile.
                    getBillingAddress1().trim().equalsIgnoreCase(
                        existingProfile.
                        getBillingAddress1().trim())) {
                    updProfile.setBillingAddress1(newProfile.getBillingAddress1().
                                                  trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New BillingAddress2 --->" +
                                            newProfile.getBillingAddress2() +
                                            "<---Old BillingAddress2 --->" +
                                            existingProfile.getBillingAddress2());

            if (newProfile.getBillingAddress2() != null &&
                newProfile.getBillingAddress2().trim().length() > 0) {
                if (existingProfile.getBillingAddress2() == null ||
                    !newProfile.
                    getBillingAddress2().trim().equalsIgnoreCase(
                        existingProfile.
                        getBillingAddress2().trim())) {
                    updProfile.setBillingAddress2(newProfile.getBillingAddress2().
                                                  trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New BillingCity --->" +
                                            newProfile.getBillingCity() +
                                            "<---Old BillingCity --->" +
                                            existingProfile.getBillingCity());

            if (newProfile.getBillingCity() != null &&
                newProfile.getBillingCity().trim().length() > 0) {
                if (existingProfile.getBillingCity() == null ||
                    !newProfile.
                    getBillingCity().trim().equalsIgnoreCase(existingProfile.
                        getBillingCity().trim())) {
                    updProfile.setBillingCity(newProfile.getBillingCity().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New BillingCountry --->" +
                                            newProfile.getBillingCountrycode() +
                                            "<---Old BillingCountry --->" +
                                            existingProfile.
                                            getBillingCountrycode());

            if (newProfile.getBillingCountrycode() != null &&
                newProfile.getBillingCountrycode().trim().length() > 0) {
                if (existingProfile.getBillingCountrycode() == null ||
                    !newProfile.
                    getBillingCountrycode().trim().equalsIgnoreCase(
                        existingProfile.
                        getBillingCountrycode().trim())) {
                    updProfile.setBillingCountrycode(newProfile.
                            getBillingCountrycode().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New BillingState --->" +
                                            newProfile.getBillingState() +
                                            "<---Old BillingState --->" +
                                            existingProfile.getBillingState());

            if (newProfile.getBillingState() != null &&
                newProfile.getBillingState().trim().length() > 0) {
                if (existingProfile.getBillingState() == null ||
                    !newProfile.
                    getBillingState().trim().equalsIgnoreCase(existingProfile.
                        getBillingState().trim())) {
                    updProfile.setBillingState(newProfile.getBillingState().
                                               trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New BillingZipCode --->" +
                                            newProfile.getBillingZipCode() +
                                            "<---Old BillingZipCode --->" +
                                            existingProfile.getBillingZipCode());

            if (newProfile.getBillingZipCode() != null &&
                newProfile.getBillingZipCode().trim().length() > 0) {
                if (existingProfile.getBillingZipCode() == null ||
                    !newProfile.
                    getBillingZipCode().trim().equalsIgnoreCase(existingProfile.
                        getBillingZipCode().trim())) {
                    updProfile.setBillingZipCode(newProfile.getBillingZipCode().
                                                 trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New Country --->" +
                                            newProfile.getCountry() +
                                            "<---Old Country --->" +
                                            existingProfile.getCountry());

            if (newProfile.getCountry() != null &&
                newProfile.getCountry().trim().length() > 0) {
                if (existingProfile.getCountry() == null ||
                    !newProfile.
                    getCountry().trim().equalsIgnoreCase(existingProfile.
                        getCountry().trim())) {
                    updProfile.setCountry(newProfile.getCountry().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New DrivingLicesneNo --->" +
                                            newProfile.getDrivingLicesneNo() +
                                            "<---Old DrivingLicesneNo --->" +
                                            existingProfile.getDrivingLicesneNo());

            if (newProfile.getDrivingLicesneNo() != null &&
                newProfile.getDrivingLicesneNo().trim().length() > 0) {
                if (existingProfile.getDrivingLicesneNo() == null ||
                    !newProfile.
                    getDrivingLicesneNo().trim().equalsIgnoreCase(
                        existingProfile.
                        getDrivingLicesneNo().trim())) {
                    updProfile.setDrivingLicesneNo(newProfile.
                            getDrivingLicesneNo().trim());
                }
            }

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "New DrivingLicesneState --->" +
                                            newProfile.getDrivingLicesneState() +
                                            "<---Old DrivingLicesneState --->" +
                                            existingProfile.
                                            getDrivingLicesneState());

            if (newProfile.getDrivingLicesneState() != null &&
                newProfile.getDrivingLicesneState().trim().length() > 0) {
                if (existingProfile.getDrivingLicesneState() == null ||
                    !newProfile.
                    getDrivingLicesneState().trim().equalsIgnoreCase(
                        existingProfile.
                        getDrivingLicesneState().trim())) {
                    updProfile.setDrivingLicesneState(newProfile.
                            getDrivingLicesneState().trim());
                }
            }

        } catch (Exception ex) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Exception in getting updated profile fields by comparing new & existing profiles--->" +
                                            ex);
            throw new Exception(
                    "Unable to get updated profile fields by comparing new & existing profiles--->" +
                    ex);
        }
        return updProfile;
    }

    public ServicesResponseObj transferAmount(ServicesRequestObj requestObj) throws Exception{
    ServicesResponseObj respObj = new ServicesResponseObj();
    StringBuffer query = new StringBuffer();
    CallableStatement cs = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Transfering Amount --- New Card Number--->" +
                                      requestObj.getCardNo()
                                      + "<---Existing Card Number--->" +
                                      requestObj.getExistingCardNumber()
                                      + "<---Transfer Amount--->" +
                                      requestObj.getAmount()
                                      + "<---Service ID--->" +
                                      requestObj.getServiceId()
                                      + "<---Apply Fee--->" +
                                      requestObj.getApplyFee()
                                      + "<---getDeviceType--->" +
                                      requestObj.getDeviceType()
                                      + "<---isDoAllCashOut--->" +
                                      requestObj.isDoAllCashOut()
                                      + "<---getAcquirerId--->" +
                                      requestObj.getAcquirerId()
                                      + "<---getAcquirerData1--->" +
                                      requestObj.getAcqData1()
                                      + "<---getAcquirerData2--->" +
                                      requestObj.getAcqData2()
                                      + "<---getAcquirerData3--->" +
                                      requestObj.getAcqData3()
                                      + "<---getAcquirerUserId--->" +
                                      requestObj.getAcqUsrId()
                                      + "<---getCrdAceptorCode--->" +
                                      requestObj.getCardAcceptorId()
                                      + "<---getCrdAceptorName--->" +
                                      requestObj.getCardAcceptNameAndLoc()
                                      + "<---getMerchantCatCd--->" +
                                      requestObj.getMcc()
                                      + "<---getDeviceId--->" +
                                      requestObj.getDeviceId()
                                      + "<---getRetRefNumber--->" +
                                      requestObj.getRetreivalRefNum()
                                      + "<---getActivateToCard--->" +
                                      requestObj.isActivateUpgradedCard());
      query.append("execute procedure apply_card_upgrade(pfrom_card = ?,pto_card = ?,pservice_id = ?,papply_fee	 = ?,pdevice_type = ?,pis_all_cash_out = ?,pamount = ?,pacq_id = ?,psub_srv = ?,pdata_2 = ?,pdata_3 = ?,pacq_userid = ?,pcard_acptr_code = ?,pcard_acptr_name = ?,pmcc = ?,pdev_id = ?,pretrieval_ref = ?, pactivate_to_card = ?)");
/*
      query.append("Execute procedure apply_card_upgrade(");
      if (requestObj.getCardNo() != null) {
        query.append(" pfrom_card = '" + requestObj.getCardNo() + "',");
      }
      else {
        query.append(" pfrom_card = " + null +",");
      }
      if (requestObj.getToCardNumber() != null) {
        query.append(" pto_card = '" + requestObj.getToCardNumber() + "',");
      }
      else {
        query.append(" pto_card = " + null +",");
      }
      if (requestObj.getServiceId() != null) {
        query.append(" pservice_id = '" + requestObj.getServiceId() + "',");
      }
      else {
        query.append(" pservice_id = " + null +",");
      }
      if (requestObj.getApplyFee() != null &&
          requestObj.getApplyFee().equals(Constants.YES_OPTION)) {
        query.append(" papply_fee = 'Y',");
      }
      else {
        query.append(" papply_fee= 'N',");
      }
      if (requestObj.getDeviceType() != null) {
        query.append(" pdevice_type= '" + requestObj.getDeviceType() + "',");
      }
      else {
        query.append(" pdevice_type= " + null +",");
      }
      if (requestObj.isIsAllCashOut()) {
        query.append(" pis_all_cash_out= 'Y',");
      }
      else {
        query.append(" pis_all_cash_out= 'N',");
      }
      if (requestObj.getAmount() != null) {
        query.append(" pamount= '" + requestObj.getAmount() + "')");
      }
      else {
        query.append(" pamount= " + null +")");
      }
*/
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for Transfering Amount--->" +
                                      query);

      cs = con.prepareCall(query.toString());
      cs.setString(1,requestObj.getExistingCardNumber());
      cs.setString(2,requestObj.getCardNo());
      cs.setString(3,requestObj.getServiceId());
      cs.setString(4,requestObj.getApplyFee());
      cs.setString(5,requestObj.getDeviceType());
      cs.setString(6,(requestObj.isDoAllCashOut() ? "Y" : "N"));
      cs.setString(7,requestObj.getAmount());
      cs.setString(8,requestObj.getAcquirerId());
      cs.setString(9,requestObj.getAcqData1());
      cs.setString(10,requestObj.getAcqData2());
      cs.setString(11,requestObj.getAcqData3());
      cs.setString(12,requestObj.getAcqUsrId());
      cs.setString(13,requestObj.getCardAcceptorId());
      cs.setString(14,requestObj.getCardAcceptNameAndLoc());
      cs.setString(15,requestObj.getMcc());
      cs.setString(16,requestObj.getDeviceId());
      cs.setString(17,requestObj.getRetreivalRefNum());
      cs.setString(18,(requestObj.isActivateUpgradedCard() ? "Y" : "N"));

      rs = cs.executeQuery();

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          respObj.setRespCode(rs.getString(1).trim());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          respObj.setRespDesc(rs.getString(2).trim());
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          respObj.setTanExistingCard(rs.getString(3).trim());
        }
        if (rs.getString(4) != null && rs.getString(4).trim().length() > 0) {
          respObj.setTanUpgradedCard(rs.getString(4).trim());
        }
        if (rs.getString(5) != null && rs.getString(5).trim().length() > 0) {
          respObj.setCardBalance(rs.getString(5).trim());
        }
        if (rs.getString(6) != null && rs.getString(6).trim().length() > 0) {
          respObj.setUpdCardBalance(rs.getString(6).trim());
        }
        if (rs.getString(7) != null && rs.getString(7).trim().length() > 0) {
          respObj.setFeeAmount(rs.getString(7).trim());
        }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Received ---- Response Code--->" +
                                        respObj.getRespCode()
                                        + "<---Response Description--->" +
                                        respObj.getRespDesc()
                                        + "<---Existing card Trace Audit No--->" +
                                        respObj.getTanExistingCard()
                                        + "<---Trace Audit No Upgraded Card--->" +
                                        respObj.getTanUpgradedCard()
                                        + "<---Fee Amount--->" +
                                        respObj.getFeeAmount()
                                        + "<---Existing Card Balance--->" +
                                        respObj.getCardBalance()
                                        + "<---Upgarded Card Balance--->" +
                                        respObj.getUpdCardBalance()
                                        );
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Transferring Amount--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (cs != null) {
          cs.close();
          cs = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return respObj;
  }

} // end CardsServiceHome
