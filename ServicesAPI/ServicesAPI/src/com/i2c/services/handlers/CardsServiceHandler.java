package com.i2c.services.handlers;

import com.i2c.services.*;
import com.i2c.services.home.*;
import com.i2c.services.util.*;
import com.i2c.solspark.*;
import com.i2c.communication.manager.STrack2Manager;
import com.i2c.api.hsm.HSMResponse;
import com.i2c.service.hsm.HSMService;
import java.util.*;
import java.sql.*;
import java.io.*;
import com.i2c.communication.object.*;
import com.i2c.chauth.vo.RecordAuthInfoObj;

/**
 * <p>
 * Title: CardsServiceHandler: This class provides card related services
 * </p>
 * <p>
 * Description: This class provides services for Cards related Information. For
 * example if we want to activate a card whose status is inactive or to update
 * the card holder profile.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore
 * </p>
 * <p>
 * Company: I2c Inc
 * </p>
 *
 * @author MCP BackEnd Team
 * @version 1.0
 */

public class CardsServiceHandler {

    private Connection con = null;




    /**
     * Default Constructor for CardsServiceHandler class
     */
    private CardsServiceHandler() {
    }




    /**
     * This static method creates and returns the instance of the
     * CardsServiceHandler class.
     *
     * @param _con
     *        Connection
     * @return CardsServiceHandler
     */

    public static CardsServiceHandler getInstance(Connection _con) {
        CardsServiceHandler handler = new CardsServiceHandler();
        handler.con = _con;
        return handler;
    } // end getInstance()




    /**
     * This method validates the information related to the card such as
     * card-no, card-status etc. and returns the "ServiceResponseObj" which
     * describes validation results. If any information found false related to
     * the card then ServiceResponseObj contains the appropiate error response
     * code with the description. In case of successfull validation
     * ServiceResponseObj contains the response code "00" whcih means processing
     * was completed successfully.
     *
     * @param cardNo
     *        String
     * @param serviceId
     *        String
     * @param applyFee
     *        String
     * @param skipStatuses
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
     * @param accountNo
     *        String
     * @param acquirerId
     *        String
     * @throws Exception
     * @return ServicesResponseObj
     */

    public ServicesResponseObj validateCard(String cardNo, String serviceId,
            String applyFee, String skipStatuses, String deviceType,
            String deviceId, String cardAcceptorId,
            String cardAcceptorNameAndLoc, String mcc, String accountNo,
            String acquirerId) throws Exception {
        return validateCard(cardNo, serviceId, applyFee, null, skipStatuses,
                deviceType, deviceId, cardAcceptorId, cardAcceptorNameAndLoc,
                mcc, accountNo, acquirerId);
    } // end validate card




    /**
     * This method validates the information related to the card such as
     * card-no, card-status etc. and returns the ServiceResponseObj which
     * describes validation results. If any infromation found false realted to
     * the card then ServiceResponseObj contains the appropiate error response
     * code and error description. In case of successfull validation
     * ServiceResponseObj contains the dresponse code "00" whcih means that
     * every thing is OK.
     *
     * @param cardNo
     *        String -- Card No to validate
     * @param serviceId
     *        String -- Service ID to apply the Fee
     * @param applyFee
     *        String -- Apply Fee or Not
     * @param amount
     *        String -- Amount
     * @param skipStatuses
     *        String -- Card Statuses to skip
     * @throws Exception
     * @return ServicesResponseObj
     */

    public ServicesResponseObj validateCard(String cardNo, String serviceId,
            String applyFee, String amount,

            String skipStatuses, String deviceType, String deviceId,
            String cardAcceptorId, String cardAcceptorNameAndLoc, String mcc,
            String accountNo, String acquirerId) throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Card No -- > " + cardNo + " Service Id -- > " + serviceId
                        + " && Apply Fee -- > " + applyFee
                        + " && Skip Statuses -- > " + skipStatuses);

        if(cardNo == null || cardNo.trim().equals(""))
            throw new IllegalArgumentException(
                    "Null or empty Card No is not allowed");
        // apply fee flag
        boolean feeFlag = true;

        if(applyFee == null || applyFee.trim().equalsIgnoreCase("Y")) {
            feeFlag = true;
        } // end if(applyFee.trim().equalsIgnoreCase("Y"))
        else if(applyFee.trim().equalsIgnoreCase("N")) {
            feeFlag = false;
        } // end else if(applyFee.trim().equalsIgnoreCase("N"))

        CommonUtilities.getLogger().log(
                LogLevel.getLevel(Constants.LOG_CONFIG),
                "Starting Validation for Card -- > " + cardNo);

        try {
            // get the instance of Cards Service Home
            CardsServiceHome cardsServiceHome = CardsServiceHome
                    .getInstance(con);
            // check the card pre requisites
            respObj = cardsServiceHome.checkCardPreReqs(cardNo, feeFlag,
                    serviceId, skipStatuses);

            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the error transaction
                String[] transIds = cardsServiceHome.logTransaction(cardNo,
                        serviceId, deviceType, null, "0200", "0", respObj
                                .getRespDesc(), "0", respObj.getRespCode(),
                        deviceId, cardAcceptorId, cardAcceptorNameAndLoc, mcc,
                        accountNo, acquirerId);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if

            if(amount != null && !amount.trim().equalsIgnoreCase("")) {
                // parse the amount
                double amt = Double.parseDouble(amount);
                // get the balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + cardNo + "'");
                // parse the balance
                double bal = Double.parseDouble(balance);
                if(amt > bal) {
                    respObj.setRespCode("51");
                    respObj.setRespDesc("Insufficient Balance(" + balance
                            + ") in Card Number("
                            + cardsServiceHome.maskCardNo(cardNo) + ")");

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Insufficient Balance in card :: Going to log the transaction :: Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());

                    // log the errror transaction
                    String[] transIds = cardsServiceHome.logTransaction(cardNo,
                            serviceId, deviceType, null, "0200", "0", respObj
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
            } // end if
            // set the response to ok
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
    } // end validateCard




    /**
     * This method validates the card balance at "SOLSPARK" switch. It first
     * checks whether the give amount is zero. If it is then it returns the OK
     * response to the calling method. In case of non-zero amount it creates the
     * request object for the SOLSPARK switch and translates the information
     * contained in the request object to the SOLSPARK request object. Then it
     * validates the balance at the "SOLSPARK" switch and returns the appropiate
     * response with description to the client.
     *
     * @param cardNo
     *        String -- Card No
     * @param expDate
     *        String -- Expiry Date
     * @param AAC
     *        String -- Account Access Code
     * @param accountNo
     *        String -- Account No
     * @param amount
     *        double -- Amount
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    public ServicesResponseObj validateCardBalAtSolspark(String cardNo,
            String expDate, String AAC, String accountNo, double amount,
            String serviceId, String deviceType, String deviceId,
            String cardAcceptorId, String cardAcceptorNameAndLoc, String mcc,
            String acquirerId) throws Exception {
        try {
            // make the response object
            ServicesResponseObj respObj = new ServicesResponseObj();
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Arguments Received :: Card No -- > " + cardNo
                            + " && Expiry Date -- > " + expDate
                            + " && AAC -- > " + AAC + " && Account No -- > "
                            + accountNo + " && amount -- > " + amount
                            + " && Service Id --> " + serviceId
                            + " && Device Type--> " + deviceType);

            if(amount <= 0) {
                respObj.setRespCode("00");
                respObj.setRespDesc("OK");
                return respObj;
            }
            // get the instance of Cards Service Home
            CardsServiceHome cardsServiceHome = CardsServiceHome
                    .getInstance(con);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting the balance at SOLSPARK....");

            // create the solspark handler
            SolsparkHandler handler = new SolsparkHandler(con);
            SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
            // set the card no
            solsReqObj.setCardNum(cardNo);
            // set the expiry date
            solsReqObj.setExpDate(expDate);
            // set the ACC
            solsReqObj.setAac(AAC);
            // set the account no
            solsReqObj.setAccountNum(accountNo);
            // set the switch id
            solsReqObj.setSwitchInfo("SOLSPARK");
            // get the balance of card at SOLSPARK
            SolsparkResponseObj solsRespObj = handler
                    .getCardBalance(solsReqObj);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response after Get Balance :: Code -- > "
                            + solsRespObj.getRespCode() + " && Desc -- > "
                            + solsRespObj.getRespDesc() + " && Balance -- > "
                            + solsRespObj.getBalance());

            if(!solsRespObj.getRespCode().equalsIgnoreCase(
                    Constants.SUCCESS_CODE)) {
                respObj.setRespCode(cardsServiceHome.getSwitchResponseCode(
                        "SOLSPARK", solsRespObj.getRespCode()));
                // get the mapped description
                respObj.setRespDesc(cardsServiceHome.getSwitchResponseDesc(
                        "SOLSPARK", solsRespObj.getRespCode()));
                respObj.setRespDesc(respObj.getRespDesc() + " ("
                        + cardsServiceHome.maskCardNo(cardNo) + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());

                // log the error transaction
                String[] transIds = cardsServiceHome.logTransaction(cardNo,
                        serviceId, deviceType, null, "0200", "0", respObj
                                .getRespDesc(), "0", respObj.getRespCode(),
                        deviceId, cardAcceptorId, cardAcceptorNameAndLoc, mcc,
                        accountNo, acquirerId);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with ISO Serial No -- > "
                                + transIds[0] + " && Trace Audit No -- > "
                                + transIds[1]);

                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if
            // parse the balance to double
            double balance = Double
                    .parseDouble((solsRespObj.getBalance() != null
                            && !solsRespObj.getBalance().trim().equals("") ? solsRespObj
                            .getBalance().trim()
                            : "0.0"));

            if(balance < amount) {
                respObj.setRespCode("51");
                respObj.setRespDesc("Insufficient funds in Card No("
                        + cardsServiceHome.maskCardNo(cardNo) + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Insufficient Balance in card :: Going to log the transaction :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());

                // log the errror transaction
                String[] transIds = cardsServiceHome.logTransaction(cardNo,
                        serviceId, deviceType, null, "0200", "0", respObj
                                .getRespDesc(), "0", respObj.getRespCode(),
                        deviceId, cardAcceptorId, cardAcceptorNameAndLoc, mcc,
                        accountNo, acquirerId);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if

            respObj.setRespCode("00");
            respObj.setRespDesc("OK");
            return respObj;
        } // end try
        catch(Exception exp) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage());
            throw exp;
        } // end catch
    } // end validateCardBalAtSolspark




    /**
     * This method is used to activate the supplied card no. It first gets the
     * switch information from the database and validates the switch for
     * activeness and online mode. If the switch is inactive or in the batch
     * mode then it pauses the execution and returns the "ServiceResponseObj"
     * which describes the error(Response-code) and the error description
     * (Response-Description). If switch is validated successfully then it
     * validates the card and updates the status of the card both at the ATM and
     * POS to the active state. At the end it logs the transaction and returns
     * the "ServiceResponseObj" which describes the final status of the the
     * processing.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request information
     * @throws Exception
     * @return ServicesResponseObj -- Response information
     */

    public ServicesResponseObj activateCard(ServicesRequestObj requestObj)
            throws Exception {

        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the reference of Cards Service Home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        try {

            if(requestObj.getAmount() != null
                    && requestObj.getAmount().trim().length() > 0) { // Both
                                                                        // Activate
                                                                        // and
                                                                        // Load
                                                                        // request
                requestObj.setServiceId(Constants.ACTIVE_AND_LOAD_CARD_SERVICE);
            } else { // Only Activate request
                requestObj.setServiceId(Constants.ACTIVE_CARD_SERVICE);
            }

            // get the switch info
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), true,
                    requestObj.getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId(), requestObj.getSsn(), requestObj
                            .getHomePhone(), requestObj.getDob(), requestObj
                            .getZipCode(), requestObj.getSecurityCode(),
                    requestObj.getDrivingLicesneNo(), requestObj
                            .getDrivingLicesneState());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
                return respObj;
            }

            String validCardStatus = null;
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating card, Device Type got--->"
                            + requestObj.getDeviceType());

            if(requestObj.getDeviceType() != null
                    && (requestObj.getDeviceType().equalsIgnoreCase(
                            Constants.DEVICE_TYPE_CS) || requestObj
                            .getDeviceType().equalsIgnoreCase(
                                    Constants.DEVICE_TYPE_WS))) {
                validCardStatus = "AI";
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Validating card, Allowed card statuses for activation request coming from CS Device--->"
                                        + validCardStatus);
            } else {
                validCardStatus = "A";
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Validating card, Allowed card statuses for activation request coming from all devices other than CS--->"
                                        + validCardStatus);
            }

            respObj = validateCard(requestObj.getCardNo(), requestObj
                    .getServiceId(), requestObj.getApplyFee(), validCardStatus,
                    requestObj.getDeviceType(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response after card validation:: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());

            if(respObj.getRespCode() != null
                    && !respObj.getRespCode().trim().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                return respObj;

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {

                /**
                 * **********************Calculate
                 * Amount******************************************
                 */
                double validateAmt = 0.0;
                double feeAmt = 0.0;
                double loadAmt = 0.0;

                if(requestObj.getApplyFee() != null
                        && requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to calculate the Service Fee of Service -- > "
                                    + Constants.ACTIVE_CARD_SERVICE);
                    // calculate the fee amount
                    ServicesResponseObj feeResp = cardsServiceHome
                            .getServiceFee(requestObj.getCardNo(), "0.0",
                                    Constants.ACTIVE_CARD_SERVICE, requestObj
                                            .getDeviceType(), requestObj
                                            .getDeviceId(), requestObj
                                            .getCardAcceptorId(), requestObj
                                            .getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(), requestObj
                                            .getAccountNo(), requestObj
                                            .getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after Fee Calculation :: Code -- > "
                                    + feeResp.getRespCode() + " && Desc -- > "
                                    + feeResp.getRespDesc());
                    if(!feeResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return feeResp;
                    // parse the calculated fee amount
                    feeAmt = Double
                            .parseDouble((feeResp.getFeeAmount() != null
                                    && !feeResp.getFeeAmount().trim()
                                            .equals("") ? feeResp
                                    .getFeeAmount() : "0.0"));
                    // add it in total validation amount
                    validateAmt += feeAmt;
                } // end if

                if(requestObj.getAmount() != null
                        && !requestObj.getAmount().trim().equals("")) {
                    // parse the load amount
                    loadAmt = Double.parseDouble(requestObj.getAmount());
                } // end if
                /** ******************************************************************************** */
                if(validateAmt > 0 && validateAmt > loadAmt) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Validating card at SOLSPARK....");
                    // validate the card at SOLSPARK
                    respObj = validateCardBalAtSolspark(requestObj.getCardNo(),
                            requestObj.getExpiryDate(), requestObj.getAAC(),
                            requestObj.getAccountNo(), validateAmt,
                            Constants.ACTIVE_CARD_SERVICE, requestObj
                                    .getDeviceType(), requestObj.getDeviceId(),
                            requestObj.getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after SOLSPARK validation :: Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    if(!respObj.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return respObj;
                } // end if

                // create the solspark handler
                SolsparkHandler handler = new SolsparkHandler(con);
                SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
                // set the card no
                solsReqObj.setCardNum(requestObj.getCardNo());
                // set the expiry date
                solsReqObj.setExpDate(requestObj.getExpiryDate());
                // set the ACC
                solsReqObj.setAac(requestObj.getAAC());
                // set the account no
                solsReqObj.setAccountNum(requestObj.getAccountNo());
                // set the switch id
                solsReqObj.setSwitchInfo(switchInfo.getSwitchId());
                // call the handler activate card method
                SolsparkResponseObj solsRespObj = handler
                        .activateCard(solsReqObj);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Received From SOLSPARK::Response Code --> "
                                + solsRespObj.getRespCode() + "\n"
                                + "Response Desc -- > "
                                + solsRespObj.getRespDesc());

                if(!solsRespObj.getRespCode().equals(Constants.SUCCESS_CODE)) {
                    // set the response code and description received from
                    // solspark
                    respObj.setRespCode(cardsServiceHome
                            .getSwitchResponseCode(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    // set the response description
                    respObj.setRespDesc(cardsServiceHome
                            .getSwitchResponseDesc(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to log the error transaction::Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    // log the transaction
                    String[] transIds = cardsServiceHome.logTransaction(
                            requestObj.getCardNo(),
                            Constants.ACTIVE_CARD_SERVICE, requestObj
                                    .getDeviceType(), null, "0200", "0",
                            respObj.getRespDesc(), "0", respObj.getRespCode(),
                            requestObj.getDeviceId(), requestObj
                                    .getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAccountNo(),
                            requestObj.getAcquirerId());

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with iso serial no -- > "
                                    + transIds[0] + " && trace audit no -- > "
                                    + transIds[1]);

                    // return the response object
                    return respObj;
                } // end if
                // set the balance in response
                respObj.setCardBalance(solsRespObj.getBalance());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Card Activated at SolsPark ......");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Activating card at MCP Side....");
                // activate card
                cardsServiceHome
                        .executeQuery("update cards set card_status_atm='B',card_status_pos='B' where card_no='"
                                + requestObj.getCardNo() + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Card activated at MCP System....");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the transaction in MCP System...");
                // log the transaction in MCP System
                String transIds[] = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), Constants.ACTIVE_CARD_SERVICE, requestObj
                        .getDeviceType(), Constants.CARD_ACTIVATE_MSG, respObj
                        .getTransId(), requestObj.getDeviceId(), requestObj
                        .getCardAcceptorId(), requestObj
                        .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                        requestObj.getAccountNo(), requestObj.getAcquirerId());
                // get the iso serial no
                String transId = transIds[0];
                String orgTraceNo = transIds[1];
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with ISO Serial No -- > "
                                + transIds[0]);

                if(requestObj.getAmount() != null
                        && !requestObj.getAmount().trim().equals("")) {
                    CommonUtilities
                            .getLogger()
                            .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Going to add the balance in the card at SOLSPARK.....");
                    // set the amount in the request object
                    solsReqObj.setAmount(requestObj.getAmount());
                    // load the amount
                    SolsparkResponseObj solsResp = handler
                            .addCardFunds(solsReqObj);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Add Funds Response From SOLSPARK :: Resp Code -- > "
                                    + solsResp.getRespCode() + "\n"
                                    + "Resp Desc -- > "
                                    + solsResp.getRespDesc());
                    if(!solsResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE)) {
                        respObj.setRespCode(cardsServiceHome
                                .getSwitchResponseCode(
                                        switchInfo.getSwitchId(), solsResp
                                                .getRespCode()));
                        respObj.setRespDesc(cardsServiceHome
                                .getSwitchResponseDesc(
                                        switchInfo.getSwitchId(), solsResp
                                                .getRespCode()));

                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Going to log the error transaction::Code -- > "
                                        + respObj.getRespCode()
                                        + " && Desc -- > "
                                        + respObj.getRespDesc());
                        // log the transaction
                        transIds = cardsServiceHome.logTransaction(requestObj
                                .getCardNo(), Constants.ACTIVE_CARD_SERVICE,
                                requestObj.getDeviceType(), null, "0200", "0",
                                respObj.getRespDesc(), "0", respObj
                                        .getRespCode(), requestObj
                                        .getDeviceId(), requestObj
                                        .getCardAcceptorId(), requestObj
                                        .getCardAcceptNameAndLoc(), requestObj
                                        .getMcc(), requestObj.getAccountNo(),
                                requestObj.getAcquirerId());

                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Transaction logged with iso serial no -- > "
                                        + transIds[0]
                                        + " && trace audit no -- > "
                                        + transIds[1]);

                        // set the trans id in response
                        respObj.setTransId(transIds[0]);
                        return respObj;
                    } // end
                        // if(!solsResp.getRespCode().equalsIgnoreCase("00"))
                    // set the balance in response
                    respObj.setCardBalance(solsResp.getBalance());

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Funds loaded at SOLSPARK successfully......");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to log the transaction in MCP System...");
                    // log the transaction
                    transIds = cardsServiceHome.logTransaction(requestObj
                            .getCardNo(), "SW_DEPOSIT", requestObj
                            .getDeviceType(), solsResp.getTransID(), "0200",
                            requestObj.getAmount(), "Web Services Deposit",
                            "0", "00", requestObj.getDeviceId(), requestObj
                                    .getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAccountNo(),
                            requestObj.getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with iso serial no -- > "
                                    + transIds[0] + " & Trace Audit No--> "
                                    + transIds[1]);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to insert the original trace audit no...");
                    // insert the original trace audit no
                    cardsServiceHome
                            .executeQuery("update trans_requests set org_trace_audit="
                                    + orgTraceNo
                                    + " where trace_audit_no="
                                    + transIds[1]);
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Org Trace Audit No updated successfully");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Adding Funds at OLTP using update_funds API...");
                    StringBuffer updateFundsQry = new StringBuffer();
                    updateFundsQry.append("Execute procedure update_funds (");
                    updateFundsQry.append("pcard_no = '"
                            + requestObj.getCardNo() + "',");
                    updateFundsQry.append("ptrans_amount = '"
                            + requestObj.getAmount() + "',");
                    updateFundsQry.append("pservice_id = 'SW_DEPOSIT',");
                    updateFundsQry.append("pmti = '0200',");
                    updateFundsQry.append("presp_code = '00',");
                    updateFundsQry.append("pis_batch = 'N',");
                    updateFundsQry.append("do_change_status = 'N')");
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Adding Funds at OLTP using update_funds API --- Query--->"
                                    + updateFundsQry);
                    String[] results = cardsServiceHome
                            .getProcedureVal(updateFundsQry.toString());

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Got the Results from update_funds :: "
                                    + "response code -- > " + results[0]
                                    + " && response description -- > "
                                    + results[1]);

                    // make the query to add the funds in OLTP
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Updating switch balance to --->"
                                    + solsResp.getBalance());
                    String addFundsQry = "update card_funds set switch_balance="
                            + solsResp.getBalance()
                            + " where card_no='"
                            + requestObj.getCardNo() + "'";
                    // execute the query to add the funds
                    cardsServiceHome.executeQuery(addFundsQry);
                } // end if(requestObj.getAmount() != null &&
                    // !requestObj.getAmount().trim().equals(""))

                if(requestObj.getApplyFee().trim().equalsIgnoreCase("Y")
                        && feeAmt > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Applying Fee at SOLSPARK for service -- > "
                                    + Constants.ACTIVE_CARD_SERVICE);
                    // calculate the debit fee
                    double debitFee = Double.parseDouble((respObj
                            .getFeeAmount() != null ? respObj.getFeeAmount()
                            : "0.0"));
                    // applply the service fee at solspark
                    respObj = cardsServiceHome.applyServiceFeeAtSolspark(
                            requestObj.getCardNo(), "0.00",
                            Constants.ACTIVE_CARD_SERVICE, debitFee, respObj
                                    .getCardBalance());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response After applying Fee :: Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    if(!respObj.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return respObj;
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Applying fee at OLTP...");
                    // apply the service fee at OLTP
                    ServicesResponseObj feeResp = cardsServiceHome
                            .applyServiceFeeAtOltp(requestObj.getCardNo(),
                                    Constants.ACTIVE_CARD_SERVICE, solsRespObj
                                            .getBalance(),
                                    respObj.getTransId(), transIds[1],
                                    requestObj.getDeviceType(), requestObj
                                            .getDeviceId(), requestObj
                                            .getCardAcceptorId(), requestObj
                                            .getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(), requestObj
                                            .getAcquirerId(), requestObj
                                            .getAcqUsrId(), requestObj
                                            .getAcqData1(), requestObj
                                            .getAcqData2(), requestObj
                                            .getAcqData3());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response After applying Fee :: Code -- > "
                                    + feeResp.getRespCode() + " && Desc -- > "
                                    + feeResp.getRespDesc());
                    if(!feeResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return feeResp;
                } else if(feeAmt == 0) { // end if
                    respObj.setFeeAmount("0.0");
                } // end else if

                // set ret ref no in response
                respObj.setTransId(transId);
                // set the solspark information in response
                respObj.setSwitchTransId(solsRespObj.getTransID());
                respObj.setSwitchAuditNo(solsRespObj.getAuditNo());
                // return the response with success message
                respObj.setRespCode(Constants.SUCCESS_CODE);
                respObj.setRespDesc(Constants.SUCCESS_MSG);
                return respObj;
            } // end if

           CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting card info ");
           CardInfoObj cardInfo = cardsServiceHome.getCardInfo(requestObj.getCardNo());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Got card program--->" + cardInfo.getCardPrgId());
            boolean[] cardPrgRegValues = cardsServiceHome
                    .checkIsBrandedCardProgram(cardInfo.getCardPrgId());
            int cardOfacAvsStatus = cardsServiceHome
                    .isExistingOFAC_AVSValid(cardInfo.getOfacStatus(),cardInfo.getAvsStatus());
            if(cardPrgRegValues != null && cardPrgRegValues.length > 0) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Card Program Branded Status --->"
                                + cardPrgRegValues[0]
                                + "<---Activate Unregistered Value--->"
                                + cardPrgRegValues[1]
                                + "<---Card OFAC/AVS Status--->"
                                + cardOfacAvsStatus);
                if(cardPrgRegValues[0] && cardOfacAvsStatus != Constants.OFAC_AVS_GOOD) {
                    if(cardOfacAvsStatus == Constants.OFAC_FAILED){
                        respObj.setRespCode("OF");
                        respObj
                            .setRespDesc("Unable to activate card as ofac of provided card ("
                                    + cardsServiceHome.maskCardNo(requestObj
                                            .getCardNo())
                                    + ") is not Good in database, while the card program is marked branded");
                    }else if(cardOfacAvsStatus == Constants.AVS_FAILED){
                        respObj.setRespCode("AV");
                        respObj
                                .setRespDesc("Unable to activate card as avs of provided card ("
                                             + cardsServiceHome.maskCardNo(requestObj
                                .getCardNo())
                                             + ") is not Good in database, while the card program is marked branded");
                    }
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to log the error transaction::Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    // log the transaction
                    String[] transIds = cardsServiceHome.logTransaction(
                            requestObj.getCardNo(), requestObj.getServiceId(),
                            requestObj.getDeviceType(), null, "0200", "0",
                            respObj.getRespDesc(), "0", respObj.getRespCode(),
                            requestObj.getDeviceId(), requestObj
                                    .getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAccountNo(),
                            requestObj.getAcquirerId());

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with iso serial no -- > "
                                    + transIds[0] + " && trace audit no -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                } else if(!cardPrgRegValues[0] && !cardPrgRegValues[1]
                        && cardOfacAvsStatus != Constants.OFAC_AVS_GOOD) {
                    if(cardOfacAvsStatus == Constants.OFAC_FAILED){
                        respObj.setRespCode("OF");
                        respObj
                            .setRespDesc("Unable to activate card as ofac of provided card ("
                                    + cardsServiceHome.maskCardNo(requestObj
                                            .getCardNo())
                                    + ") is not Good in database, while the card program settings dooes not allow activating unregistered ATM cards");
                    }else if(cardOfacAvsStatus == Constants.AVS_FAILED){
                        respObj.setRespCode("AV");
                        respObj
                                .setRespDesc("Unable to activate card as avs of provided card ("
                                             + cardsServiceHome.maskCardNo(requestObj
                                .getCardNo())
                                             + ") is not Good in database, while the card program settings dooes not allow activating unregistered ATM cards");
                    }
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to log the error transaction::Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    // log the transaction
                    String[] transIds = cardsServiceHome.logTransaction(
                            requestObj.getCardNo(), requestObj.getServiceId(),
                            requestObj.getDeviceType(), null, "0200", "0",
                            respObj.getRespDesc(), "0", respObj.getRespCode(),
                            requestObj.getDeviceId(), requestObj
                                    .getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAccountNo(),
                            requestObj.getAcquirerId());

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with iso serial no -- > "
                                    + transIds[0] + " && trace audit no -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                }
            }

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Expiry Date got-->" + cardInfo.getExpiryOn());
            String currDate = CommonUtilities.getCurrentFormatDate(Constants.DATE_FORMAT);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Current Date--->" + currDate);
            long diffInMonths = CommonUtilities.getDiffInDates(currDate,cardInfo.getExpiryOn(),Constants.DATE_FORMAT,Constants.DIFF_IN_MONTHS);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Difference in Months got--->" + diffInMonths);
            boolean paramAllowed = cardsServiceHome.isParameterAllowed(Constants.REM_EXP_MONTHS_BFR_ACT_PARAM,cardInfo.getCardPrgId(),Long.toString(diffInMonths));
            if(paramAllowed == false){
                respObj.setRespCode("AD");
                // set the response description
                respObj.setRespDesc("Unable to activate the card as card will expire in ("+ diffInMonths + ") months");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                        + respObj.getRespCode() + " && Desc -- > "
                        + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(
                        requestObj.getCardNo(),
                        requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0",
                        respObj.getRespDesc(), "0", respObj.getRespCode(),
                        requestObj.getDeviceId(), requestObj
                        .getCardAcceptorId(), requestObj
                        .getCardAcceptNameAndLoc(), requestObj
                        .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                        + transIds[0] + " && trace audit no -- > "
                        + transIds[1]);
                // return the response object
                return respObj;
            }

            if (cardInfo.getCardStatusAtm().equals(Constants.PRE_ACTIVE_CARD) &&
                cardInfo.getCardStatusPos().equals(Constants.PRE_ACTIVE_CARD)) {
                String initAmt = cardsServiceHome.getInitialBatchLoadAmount(
                        cardInfo.getCardBatchNo());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Initial Load Amount got-->" + initAmt);
                if (initAmt != null && initAmt.trim().length() > 0) {
                    double initAmount = 0;
                    try {
                        initAmount = Double.parseDouble(initAmt);
                        if (requestObj.getAmount() != null &&
                            requestObj.getAmount().trim().length() > 0) {
                            CommonUtilities.getLogger().log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Unable to activate the card as Load Amount is provided in the request but card is Fixed initial value card-->" +
                                    requestObj.getAmount());
                            respObj.setRespCode("FI");
                            // set the response description
                            respObj.setRespDesc(
                                    "Unable to activate the card as Load Amount is provided in the request but card is Fixed initial value card-->" +
                                    requestObj.getAmount());
                            CommonUtilities.getLogger().log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Going to log the error transaction::Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                            // log the transaction
                            String[] transIds = cardsServiceHome.logTransaction(
                                    requestObj.getCardNo(),
                                    requestObj.getServiceId(), requestObj
                                    .getDeviceType(), null, "0200", "0",
                                    respObj.getRespDesc(), "0",
                                    respObj.getRespCode(),
                                    requestObj.getDeviceId(), requestObj
                                    .getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAccountNo(),
                                    requestObj.getAcquirerId());

                            CommonUtilities.getLogger().log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Transaction logged with iso serial no -- > "
                                    + transIds[0] + " && trace audit no -- > "
                                    + transIds[1]);
                            // return the response object
                            return respObj;

                        }
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Total amount to load on card on activation-->" +
                                initAmount);
                        requestObj.setAmount(Double.toString(initAmount));
                        requestObj.setServiceId(Constants.
                                                ACTIVE_AND_LOAD_CARD_SERVICE);
                    } catch (NumberFormatException amtEx) {
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Unparseable inital load amount got--->" +
                                initAmt);
                    }
                }
            }

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to activate the card at OLTP....");
            respObj = cardsServiceHome.activateCardInOltp(requestObj);

            if(respObj != null && respObj.getRespCode() != null
                    && respObj.getRespCode().equals(Constants.SUCCESS_CODE)) {
                requestObj.setDescription("Assignment during card activation");
                cardsServiceHome.assignCard(requestObj);
            }

            return respObj;
            /*
             * String traceAuditNo = null; if(requestObj.getAmount() != null &&
             * !requestObj.getAmount().trim().equals("")) {
             * CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Going
             * to add the funds in the card at OLTP...."); //Add the funds in
             * OLTP respObj =
             * cardsServiceHome.addFundsInCard(requestObj.getCardNo(),requestObj.getAmount());
             * if(respObj.getRespCode() != null &&
             * !respObj.getRespCode().trim().equals("00")) return respObj; else
             * cardsServiceHome.executeQuery("update cards set
             * card_status_atm='A',card_status_pos='A' where card_no='" +
             * requestObj.getCardNo() + "'"); //get the latest balance String
             * balance = cardsServiceHome.getValue("select card_balance from
             * card_funds where card_no='" + requestObj.getCardNo() + "'");
             * //set the latest balance in response
             * respObj.setCardBalance(balance);
             * CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Going
             * to log the load transaction..."); //log the load transaction
             * String[] transIds =
             * cardsServiceHome.logTransaction(requestObj.getCardNo(),"SW_DEPOSIT",
             * requestObj.getDeviceType(),
             * null,"0200",requestObj.getAmount(),"Web Services
             * Deposit","0","00",
             * requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
             * CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Transaction
             * logged with iso serial no -- > " + transIds[0] + " & Trace Audit
             * No--> " + transIds[1]); //set load trans trace audit no
             * traceAuditNo = transIds[1]; }//end if
             * CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
             * "Performing activate card in the MCP DB......"); //perform the
             * operation in OLTP respObj =
             * cardsServiceHome.activateCardInOltp(requestObj.getCardNo(),
             * requestObj.getApplyFee(),Constants.ACTIVE_CARD_SERVICE,
             * requestObj.getDeviceType());
             * CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
             * "Response Received -- > " + respObj.getRespCode() + " && " +
             * respObj.getRespDesc());
             * if(!respObj.getRespCode().trim().equals(Constants.SUCCESS_CODE)) {
             * con.rollback();
             * CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
             * LOG_CONFIG),"Going to log the error transaction::Code -- > " +
             * respObj.getRespCode() + " && Desc -- > " +
             * respObj.getRespDesc()); //log the transaction String[] transIds =
             * cardsServiceHome.logTransaction(requestObj.getCardNo(),Constants.ACTIVE_CARD_SERVICE,requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0",
             * respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
             * CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
             * LOG_CONFIG),"Transaction logged with iso serial no -- > " +
             * transIds[0] + " && trace audit no -- > " + transIds[1]); //set
             * the transaction id in response respObj.setTransId(transIds[0]);
             * return respObj; }//end if if(traceAuditNo != null) {
             * CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Going
             * to update the org trace audit no against load trans id --> " +
             * traceAuditNo); //get original trace number String orgTraceNo =
             * cardsServiceHome.getValue("select trace_audit_no from
             * trans_requests where iso_serial_no=" + respObj.getTransId());
             * //insert the original trace audit no
             * cardsServiceHome.executeQuery("update trans_requests set
             * org_trace_audit="+orgTraceNo + " where trace_audit_no=" +
             * traceAuditNo);
             * CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Org
             * Trace Audit No updated successfully"); }//end if //set the isoF
             * serial no in response
             * respObj.setRespCode(Constants.SUCCESS_CODE);
             * respObj.setRespDesc(Constants.SUCCESS_MSG);
             */
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end activateCard




    /**
     * This method deactivates the given card. It first checks the card for
     * de-activation, if the given card is already deactivated then it pauses
     * the execution and returns the appropiate response with response code and
     * the response description. It then fetches the switch information from the
     * datatabse. If the switch is inactive or in batch mode then it returns the
     * appropiate response code with the description. Then it applies the
     * service fee and updates the card status to inactive.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request information
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    public ServicesResponseObj deActivateCard(ServicesRequestObj requestObj)
            throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting card Status....");
            // get card status
            String status = cardsServiceHome
                    .getValue("select card_status_pos from cards where card_no='"
                            + requestObj.getCardNo() + "'");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Card Status Got -- > " + status);
            if(status.equalsIgnoreCase("I") || status.equalsIgnoreCase("A")) {
                respObj.setRespCode("SA");
                respObj.setRespDesc("Card No(" + requestObj.getCardNo()
                        + ") is already inactive");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), Constants.DEACTIVATE_CARD_SERVICE,
                        requestObj.getDeviceType(), null, "0200", "0", respObj
                                .getRespDesc(), "0", respObj.getRespCode(),
                        requestObj.getDeviceId(), requestObj
                                .getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;

            } // end if

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting the switch information....");
            // get the switch information
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), Constants.DEACTIVATE_CARD_SERVICE,
                        requestObj.getDeviceType(), null, "0200", "0", respObj
                                .getRespDesc(), "0", respObj.getRespCode(),
                        requestObj.getDeviceId(), requestObj
                                .getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Switch is active and Batch Mode is on....");
                respObj.setRespCode("40");
                respObj
                        .setRespDesc("Deactivate Card Service is not supported at switch("
                                + switchInfo.getSwitchId() + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), Constants.DEACTIVATE_CARD_SERVICE,
                        requestObj.getDeviceType(), null, "0200", "0", respObj
                                .getRespDesc(), "0", respObj.getRespCode(),
                        requestObj.getDeviceId(), requestObj
                                .getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if

            CommonUtilities
                    .getLogger()
                    .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Validating Card Information like Old Pin, Old ACC, Exp Date.....");
            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(),
                    Constants.DEACTIVATE_CARD_SERVICE, requestObj
                            .getDeviceType(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            if(requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to validate the card ....");
                // validate the card for balance
                respObj = validateCard(requestObj.getCardNo(),
                        Constants.DEACTIVATE_CARD_SERVICE, requestObj
                                .getApplyFee(), "GHIRBACDE", requestObj
                                .getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after validation :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE))
                    return respObj;
            } // end if
            else {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Checking for card Existance...");
                // get the card no
                String cardVal = cardsServiceHome
                        .getValue("select card_no from cards where card_no='"
                                + requestObj.getCardNo() + "'");
                if(cardVal == null) {
                    respObj.setRespCode("51");
                    respObj.setRespDesc("Card No(" + requestObj.getCardNo()
                            + ") does not exist");
                    return respObj;
                } // end if
            } // end else

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to deactivate the card....");
            // deactivate the card
            respObj = cardsServiceHome.deactivateCard(requestObj.getCardNo());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response after deactivating Card :: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction...");
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), Constants.DEACTIVATE_CARD_SERVICE, requestObj
                    .getDeviceType(), Constants.CARD_DEACT_MSG, null,
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());
            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial NO -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to apply the fee....");
                // apply the fee at OLTP
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), Constants.DEACTIVATE_CARD_SERVICE, null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after applying fee :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    return respObj;
                } // end if
            } // end if
            else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else
            // set the iso serial no in response
            respObj.setTransId(transIds[0]);
            // set the response to success and return
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            return respObj;
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end deActivateCard




    /**
     * This method gets the current status of the given card. It first checks
     * whether the application of the service fee is allowed or not. If the
     * service fee application is allowed then it validates the given card
     * attributes. If an error occured while validation of the card then it
     * returns the ServiceResponseObj bean object which contains the response
     * code with the appropiate response description. It then fetches the switch
     * infromation from the database and validates the switch i.e. whether it is
     * active and online. If the switch is inactive or in the batch mode then it
     * log the transaction and returns the error response code with description.
     * If validation of the switch was successfull then it fetches the status of
     * the card from the database, log the transaction, populates the response
     * object with appropiate response code and response description and returns
     * the response to the client.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request information
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    // /////////////////Changed///////////////////////////
    public ServicesResponseObj getCardStatus(ServicesRequestObj requestObj)
            throws Exception {
        if(requestObj == null)
            throw new IllegalArgumentException(
                    "Null request object is not allowed");
        // create the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        try {
            if(requestObj.getApplyFee().trim().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to check the balance of the card...");
                // check the balance of the card
                // respObj = validateCard(requestObj.getCardNo(),
                // Constants.GET_CARD_STATUS_SERVICE,requestObj.getApplyFee(),"FGHIRBACDE",
                // requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
                // ///////////*** Above Line Replaced with This By
                // Imtiaz****////////////////////
                respObj = validateCard(requestObj.getCardNo(), requestObj
                        .getServiceId(), requestObj.getApplyFee(),
                        "FGHIRBACDE", requestObj.getDeviceType(), requestObj
                                .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response Received after checking Balance :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                    return respObj;
            } // end if(requestObj.getApplyFee().trim().equalsIgnoreCase("Y"))
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to get the switch information of the card....");
            // get the switch information of the card
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                // String[] transIds =
                // cardsServiceHome.logTransaction(requestObj.getCardNo(),Constants.GET_CARD_STATUS_SERVICE,requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0",
                // respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
                // ///////////////////////// *** Above Line Replaced With This
                // Line By Imtiaz ****////////////
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
                // set the appropriate response codes
                respObj.setRespCode("40");
                respObj
                        .setRespDesc("Get Card Status Service is not supported at the switch("
                                + switchInfo.getSwitchId() + ")");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction

                // String[] transIds =
                // cardsServiceHome.logTransaction(requestObj.getCardNo(),Constants.GET_CARD_STATUS_SERVICE,requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0",
                // respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
                // ///////////////////////// *** Above Line Replaced With This
                // Line By Imtiaz ****////////////
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                // return the response object
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // switchInfo.isBatchTransAllowed())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            String cardStatusQry = "select card_status_pos from cards where card_no='"
                    + requestObj.getCardNo() + "'";

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to get the status of the card......");

            // get the card status
            String cardStatus = cardsServiceHome.getValue(cardStatusQry);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Card Status Got -- > " + cardStatus);

            cardStatusQry = "select card_status_desc from card_statuses where card_status='"
                    + cardStatus + "'";

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to get the description of the card status....");

            // get the card status description
            String cardStatusDesc = cardsServiceHome.getValue(cardStatusQry);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Card Status Description Got -- > " + cardStatusDesc);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction in MCP System....");
            // log the transaction in MCP System

            // String[] transIds =
            // cardsServiceHome.logTransaction(requestObj.getCardNo(),Constants.GET_CARD_STATUS_SERVICE,
            // requestObj.getDeviceType(),
            // Constants.GET_CARD_STATUS_MSG,null,requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
            // ///////////////////////// *** Above Line Replaced With This Line
            // By Imtiaz ****////////////

            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.GET_CARD_STATUS_MSG, null,
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to apply the fee at OLTP...");
                // apply the fee at OLTP

                // respObj =
                // cardsServiceHome.applyServiceFeeAtOltp(requestObj.getCardNo(),Constants.GET_CARD_STATUS_SERVICE,null,null,transIds[1],
                // requestObj.getDeviceType(),
                // requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc());
                // //////////////////////////*** Above Line Was Replace With
                // Following Line By Imtiaz ***///////////////////////
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());

                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    // return response object
                    return respObj;
                } // end if
            } // end if(requestObj.getApplyFee().equalsIgnoreCase("Y"))
            else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else
            // set the iso serial no in response object
            respObj.setTransId(transIds[0]);
            // set the status in response
            respObj.setCardStatusCode(cardStatus);
            respObj.setCardStatusDesc(cardStatusDesc);
            // set the response to ok
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);

            // return the response object
            return respObj;
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end getCardStatus




    /**
     * This method changes the status of the given card. It first checks whether
     * the application of the service fee is allowed, if yes then it validates
     * the card attributes contained in the request object. If validation of the
     * card is unsuccessful then it pauses the execution and returns the
     * response with proper response code and the description. It then gets the
     * switch information from the database and validate the switch. If the
     * switch is inactive or in the batch mode, it pauses the execution, logs
     * the transaction and returns the response with appropiate response code
     * and the response description. If application of service fee is allowed
     * then it charges the fee on the card account, changes the status of the
     * card and returns the response to the calling method.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request information
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    // /////////////////////////////// Changed
    // ///////////////////////////////////////////////////////
    // Constants.SET_CARD_STATUS_SERVICE were replaced with
    // requestObj.getServiceId() //
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public ServicesResponseObj setCardStatus(ServicesRequestObj requestObj)
            throws Exception {
        // create the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);

        try {

            if(requestObj.getApplyFee().trim().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to check the balance of the card...");
                // check the balance of the card
                respObj = validateCard(requestObj.getCardNo(), requestObj
                        .getServiceId(), "N", "GHIRBACDE", requestObj
                        .getDeviceType(), requestObj.getDeviceId(), requestObj
                        .getCardAcceptorId(), requestObj
                        .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                        requestObj.getAccountNo(), requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after Checking Balance :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                    return respObj;
            } // end if(requestObj.getApplyFee().trim().equalsIgnoreCase("Y"))

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to get the switch information of the card....");

            // get the switch information of the card
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
                // set the appropriate response codes
                respObj.setRespCode("40");
                respObj
                        .setRespDesc("Set Card Status Service is not supported at the switch("
                                + switchInfo.getSwitchId() + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                // return the response object
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // switchInfo.isBatchTransAllowed())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            // update the card status in MCP System
            boolean trkGenrtd = false;
            if(cardsServiceHome.checkCardTrackGenerated(requestObj.getCardNo())) {
                trkGenrtd = true;
            }

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Checking whether changing new card status is allowed...");
            if(!cardsServiceHome.checkCardStatusChangeAllowed(requestObj
                    .getCardNo(), requestObj.getCardStatus())) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Changing card status to --->"
                                + requestObj.getCardStatus()
                                + " is not allowed for card--->"
                                + requestObj.getCardNo());
                // set the appropriate response codes
                respObj.setRespCode("57");
                respObj.setRespDesc("Changing card status to ("
                        + requestObj.getCardStatus()
                        + ") is not allowed for card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                // return the response object
                return respObj;
            }

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to set the status of the card in MCP System......");

            cardsServiceHome.changeCardStatus(requestObj.getCardNo(),
                    requestObj.getCardStatus(), trkGenrtd);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Card Status updated successfully");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction in MCP System....");
            // log the transaction in MCP System
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.SET_CARD_STATUS_MSG, null,
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Applying Fee at OLTP..");
                // apply the fee
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    // return the response object
                    return respObj;
                } // end if
            } // end if(requestObj.getApplyFee().equalsIgnoreCase("Y"))
            else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else

            // set the iso serial no in response object
            respObj.setTransId(transIds[0]);
            // set the response to ok
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);

            // return the response object
            return respObj;
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end setCardStatus




    /**
     * This method returns the current PIN of the supplied card no.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request Information
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    public ServicesResponseObj getCardPin(ServicesRequestObj requestObj)
            throws Exception {
        // //make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        respObj.setRespCode("00");
        return respObj;
        //
        // try{
        // //get the switch info
        // SwitchInfoObj switchInfo =
        // cardsServiceHome.getCardSwitchInfo(requestObj.getCardNo());
        //
        // if (switchInfo.getSwitchId() != null &&
        // !switchInfo.getSwitchId().trim().equals("") &&
        // switchInfo.isBatchTransAllowed()) {
        // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
        // "Batch Trans Allowed is 'Y' and Solspark does not provide any service
        // for that...");
        // //set the appropriate response codes
        // respObj.setRespCode("40");
        // respObj.setRespDesc("Get PIN Service is not supported at the switch("
        // + switchInfo.getSwitchId() + ")");
        // //return the response object
        // return respObj;
        // } //end if (switchInfo.getSwitchId() != null &&
        // !switchInfo.getSwitchId().trim().equals("") &&
        // switchInfo.isBatchTransAllowed())
        //
        // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Validating
        // Card's Old Information....");
        // //validate the card information
        // respObj =
        // cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),requestObj.getAAC(),requestObj.getExpiryDate(),requestObj.getAccountNo(),requestObj.getPin());
        // if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        // return respObj;
        //
        // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Validating
        // the Card....");
        // //validate the information
        // respObj =
        // validateCard(requestObj.getCardNo(),Constants.GET_PIN_SERVICE,requestObj.getApplyFee(),null);
        // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Response
        // after card validation :: Code -- > " + respObj.getRespCode() + " &&
        // Desc -- > " + respObj.getRespDesc());
        //
        // if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
        // return respObj;
        //
        // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
        // "Getting the PIN for the Card from MCP DB......");
        // //get the PIN of Card from MCP DB
        // String pin = cardsServiceHome.getValue("select pin_offset from cards
        // where card_no='" + requestObj.getCardNo() + "'");
        // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Got
        // the PIN -- > " + pin);
        //
        // if(pin == null)
        // {
        // respObj.setRespCode("06");
        // respObj.setRespDesc("PIN is not available for Card No(" +
        // requestObj.getCardNo() + ")");
        // return respObj;
        // }//end if
        //
        // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Going
        // to log the transaction in MCP System....");
        // //log the transaction in MCP System
        // String[] transIds =
        // cardsServiceHome.logTransaction(requestObj.getCardNo(),Constants.GET_PIN_SERVICE,null);
        // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Transaction
        // logged with ISO Serial No -- > " + transIds[0]);
        //
        // if(requestObj.getApplyFee().equalsIgnoreCase("Y"))
        // {
        // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Going
        // to apply the fee at OLTP....");
        // //apply the fee at OLTP
        // respObj =
        // cardsServiceHome.applyServiceFeeAtOltp(requestObj.getCardNo(),Constants.GET_PIN_SERVICE,null,null,transIds[1]);
        //
        // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Resp
        // Got after applying Fee :: Code -- > " + respObj.getRespCode() + " &&
        // Desc -- > " + respObj.getRespDesc());
        // if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
        // {
        // //rollback the work
        // con.rollback();
        // //return the response object
        // return respObj;
        // }//end if
        // }//end if(requestObj.getApplyFee().equalsIgnoreCase("Y"))
        // else
        // {
        // //get the latest balance
        // String balance = cardsServiceHome.getValue("select card_balance from
        // card_funds where card_no='" + requestObj.getCardNo() + "'");
        // //set the balance in response
        // respObj.setCardBalance(balance);
        // }//end else
        // //set the iso serial no in response
        // respObj.setTransId(transIds[0]);
        // //populate the message object with success code and description
        // respObj.setRespCode(Constants.SUCCESS_CODE);
        // respObj.setRespDesc(Constants.SUCCESS_MSG);
        // //return the response
        // return respObj;
        // }//end try
        // catch(Exception exp)
        // {
        // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_SEVERE),"Exception
        // -- > " + exp.getMessage());
        // //set the response code to system error
        // respObj.setRespCode("96");
        // respObj.setRespDesc("System Error");
        // return respObj;
        // }//end catch
    } // end getCardPin




    /**
     * This method changes the PIN of the given card no. First it gets CARD
     * PROGRAM ID and the PIN length from the database. If the PIN length is
     * null then it pauses the execution by returning the response code 06 with
     * appropiate response description. It then gets the switch information and
     * validates the switch for activeness and the online mode. If the switch is
     * inactive or in the batch mode then it logs the transaction and returns
     * the response to the client with appropiate response code and the response
     * description. Then it checks whether application of the service fee is
     * allowed or not. If it is allowed then it calculates the service fee and
     * determines the card balance at the SOLSPARK switch if the amount is
     * greater then zero. It then tranlates the request object into the SOLSPARK
     * request object, changes the PIN of the card and log the transaction.
     * After logging the transaction it returns the appropiate response with
     * response code, response description, card balance and other attributes
     * set in the ServiceResponseObj to the client.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request Information
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    // //////////////////////// Changed
    // //////////////////////////////////////////////////////////////
    // Constants.SET_PIN_SERVICE Were Replaced with requestObj.getServiceId() //
    // /////////////////////////////////////////////////////////////////////////////////////////////////
    public ServicesResponseObj setCardPin(ServicesRequestObj requestObj)
            throws Exception {
        // create the services response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        String pinLength = null;
        String cardPrgId = null;
        String oldPinOffset = null;
        String isSwitchPIN = null;
        boolean rollBackOffset = false;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting isPINPreverified flag from system_variables...");
            isSwitchPIN = cardsServiceHome
                    .getValue("select is_pin_preverified from system_variables");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Value of isPINPreverified flag in system_variables--->"
                            + isSwitchPIN);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting the Old PIN offset, card prg id for provided card number--->"
                            + requestObj.getCardNo());
            String[] result = cardsServiceHome
                    .getValues("select card_prg_id,pin_offset from cards where card_no='"
                            + requestObj.getCardNo() + "'");
            if(result != null) {
                cardPrgId = result[0];
                oldPinOffset = result[1];
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Getting the PIN length defined in card program....");
                pinLength = cardsServiceHome
                        .getValue("select pin_length from card_programs where card_prg_id = '"
                                + cardPrgId + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "PIN length defined in card program -- > " + pinLength
                                + "<---Old PIN offset-->" + oldPinOffset
                                + "<---Card Prg ID--->" + cardPrgId);
            }
            if(pinLength == null) {
                respObj.setRespCode("06");
                respObj.setRespDesc("PIN Length not defined in card program ("
                        + cardPrgId + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                // return the response object
                return respObj;
            } // end if
            // parse the pin length into integer
            int plength = Integer.parseInt(pinLength);

            if(requestObj.getNewPin() == null
                    || requestObj.getNewPin().trim().length() == 0) {
                respObj.setRespCode("55");
                respObj.setRespDesc("Invalid New PIN supplied --->"
                        + requestObj.getNewPin());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                // return the response object
                return respObj;
            }

            if(requestObj.getNewPin().length() > plength) {
                respObj.setRespCode("55");
                respObj.setRespDesc("Invalid New PIN supplied for Card No("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + "). Its length should be " + plength + " digits");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                // return the response object
                return respObj;
            } // end if

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting the Switch Information of card...");
            // get the switch info
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating the Card....");
                // validate the information
                respObj = validateCard(requestObj.getCardNo(), requestObj
                        .getServiceId(), "N", "BAE",
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "After Validation Response Received :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());

                if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                    return respObj;

                /**
                 * **********************Calculate the
                 * Amount******************************
                 */
                double validateAmt = 0.0;
                double feeAmt = 0.0;

                if(requestObj.getApplyFee() != null
                        && requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Calculating the fee for service -- > "
                                    + requestObj.getServiceId());
                    // calcualte the service fee
                    ServicesResponseObj feeResp = cardsServiceHome
                            .getServiceFee(requestObj.getCardNo(), "0.0",
                                    requestObj.getServiceId(), requestObj
                                            .getDeviceType(), requestObj
                                            .getDeviceId(), requestObj
                                            .getCardAcceptorId(), requestObj
                                            .getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(), requestObj
                                            .getAccountNo(), requestObj
                                            .getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after fee  calculation :: Code -- > "
                                    + feeResp.getRespCode() + " && Desc -- > "
                                    + feeResp.getRespDesc() + " && Fee -- > "
                                    + feeResp.getFeeAmount());
                    if(!feeResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE)) {
                        // return the response object
                        return respObj;
                    } // end if
                    // parse the fee amount
                    feeAmt = Double
                            .parseDouble((feeResp.getFeeAmount() != null
                                    && !feeResp.getFeeAmount().trim()
                                            .equals("") ? feeResp
                                    .getFeeAmount() : "0.0"));
                    validateAmt += feeAmt;
                } // end if
                /** ************************************************************************ */
                if(validateAmt > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Validating Card at SOLSPARK....");
                    // validate card balance at SOLSPARK
                    respObj = validateCardBalAtSolspark(requestObj.getCardNo(),
                            requestObj.getExpiryDate(), requestObj.getAAC(),
                            requestObj.getAccountNo(), validateAmt, requestObj
                                    .getServiceId(),
                            requestObj.getDeviceType(), requestObj
                                    .getDeviceId(), requestObj
                                    .getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after validation at SOLSPARK:: Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    if(!respObj.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return respObj;
                } // end if

                // make the request object
                SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
                // populate the request object with the required info
                solsReqObj.setCardNum(requestObj.getCardNo());
                solsReqObj.setSwitchInfo(switchInfo.getSwitchId());
                solsReqObj.setNewPIN(requestObj.getNewPin());
                // make the handler
                SolsparkHandler handler = new SolsparkHandler(con);
                // update the pin
                SolsparkResponseObj solsRespObj = handler
                        .updateCardHolderInfo(solsReqObj);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response Got from SOLSPARK :: Code -- > "
                                + solsRespObj.getRespCode() + " && Desc -- > "
                                + solsRespObj.getRespDesc());

                if(!solsRespObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    respObj.setRespCode(cardsServiceHome
                            .getSwitchResponseCode(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    // get the appropriate response
                    respObj.setRespDesc(cardsServiceHome
                            .getSwitchResponseDesc(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    return respObj;
                } // end if
                // set the balance in response
                respObj.setCardBalance(solsRespObj.getBalance());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the transaction in MCP System...");
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), Constants.SET_CARD_PIN_MSG,
                        solsRespObj.getTransID(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with ISO Serial No -- > "
                                + transIds[0]);

                if(requestObj.getApplyFee().equalsIgnoreCase("Y") && feeAmt > 0) {
                    // parse the fee into double
                    double debitFee = (solsRespObj.getFeeAmount() != null
                            && !solsRespObj.getFeeAmount().trim().equals("") ? Double
                            .parseDouble(solsRespObj.getFeeAmount())
                            : 0.0);

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to apply the fee at SOSLPARK...");
                    // apply the fee at solspark
                    respObj = cardsServiceHome.applyServiceFeeAtSolspark(
                            requestObj.getCardNo(), "0.0", requestObj
                                    .getServiceId(), debitFee, respObj
                                    .getCardBalance());

                    if(!respObj.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return respObj;

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to apply fee at OLTP...");
                    // apply the fee at OLTP
                    ServicesResponseObj feeResp = cardsServiceHome
                            .applyServiceFeeAtOltp(requestObj.getCardNo(),
                                    requestObj.getServiceId(), respObj
                                            .getCardBalance(), respObj
                                            .getTransId(), transIds[1],
                                    requestObj.getDeviceType(), requestObj
                                            .getDeviceId(), requestObj
                                            .getCardAcceptorId(), requestObj
                                            .getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(), requestObj
                                            .getAcquirerId(), requestObj
                                            .getAcqUsrId(), requestObj
                                            .getAcqData1(), requestObj
                                            .getAcqData2(), requestObj
                                            .getAcqData3());
                    if(!feeResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return feeResp;
                } // end if(requestObj.getApplyFee().equalsIgnoreCase("Y"))

                // set the iso serial no in response object
                respObj.setTransId(transIds[0]);
                // set the solspark information in response
                respObj.setSwitchTransId(solsRespObj.getTransID());
                respObj.setSwitchAuditNo(solsRespObj.getAuditNo());
                // set the response to success
                respObj.setRespCode(Constants.SUCCESS_CODE);
                respObj.setRespDesc(Constants.SUCCESS_MSG);
                // return the response object
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating the Card....");
            // validate the information
            respObj = validateCard(requestObj.getCardNo(), requestObj
                    .getServiceId(), requestObj.getApplyFee(), null, requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAccountNo(), requestObj
                            .getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "After Validation Response Received :: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());

            if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Calling method for getting the pin offset from HSM...");

            respObj = getPINOffset(cardsServiceHome, requestObj);

            if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Pin offset got from HSM--->" + respObj.getPinOffset());

            String newPinOffset = respObj.getPinOffset();

            if(isSwitchPIN != null && isSwitchPIN.equals(Constants.YES_OPTION)) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "isPinPreVerified flag is 'Y', communicating Pin offset to switch--->"
                                + newPinOffset);

                respObj = commuincatePinOffset(cardsServiceHome, switchInfo,
                        requestObj, newPinOffset);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response recevided from EFUND communication API--->"
                                + respObj.getRespCode());
                if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                    return respObj;
            }

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to update the offset in mcp system....");
            // set the PIN of Card in MCP DB
            cardsServiceHome.executeQuery("update cards set pin_offset='"
                    + newPinOffset + "' where card_no='"
                    + requestObj.getCardNo() + "'");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "PIN Offset updated successfully....");

            // if(requestObj.getBatchPin() != null &&
            // requestObj.getBatchPin().trim().equalsIgnoreCase("Y")){
            // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Going
            // to update the card status to P..");
            // //update the card status to P
            // cardsServiceHome.executeQuery("update cards set card_status='P'
            // where card_no='" + requestObj.getCardNo() + "'");
            // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Card
            // status updated successfully....");
            // }//end if
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log success transaction in MCP System....");
            // log the transaction in MCP System
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.SET_CARD_PIN_MSG, null,
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());
            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to apply the fee at OLTP...");
                // apply the fee at OLTP
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Resp Got after applying Fee :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    // return the response object
                    return respObj;
                } // end if
            } // end if
            else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else

            // populate the message object with success code and description
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            // set the iso serial no in response
            respObj.setTransId(transIds[0]);

            // return the response
            return respObj;
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Stack Trace -- > " + CommonUtilities.getStackTrace(exp));
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error");
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end setCardPin




    /*
     * public ServicesResponseObj resetCardPin(ServicesRequestObj requestObj)
     * throws Exception { }//end resetCardPin
     */

    /**
     * This method gets the AAC of the given card. It first gets the switch
     * information from the database, validates the swtich information and
     * returns the response and log the transaction if validation was
     * unsuccessful. It then gets the Card Access Code (CAC). Before returning
     * response to the client it logs the transaction and sets the attributes of
     * the response object.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request information
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    // ////////////////////////////////// Changed
    // ////////////////////////////////////////////////////////
    // Constants.GET_AAC_SERVICE were replaced with requestObj.getServiceId() //
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    public ServicesResponseObj getCardAccessCode(ServicesRequestObj requestObj)
            throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        try {
            // get the switch info
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
                respObj.setRespCode("40");
                respObj
                        .setRespDesc("Get Card Access Code function is not available for switch("
                                + switchInfo.getSwitchId() + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                // return the response object
                return respObj;
            } // end if(switchInfo.getSwitchId() != null
                // &&!switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating Card....");
            // validate the information
            respObj = validateCard(requestObj.getCardNo(), requestObj
                    .getServiceId(), requestObj.getApplyFee(), null, requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAccountNo(), requestObj
                            .getAcquirerId());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response After card validation :: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());

            if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting the AAC for the Card from MCP DB......");
            // build the query to get the ACC
            String accQry = "select card_access_code from cards where card_no='"
                    + requestObj.getCardNo() + "'";
            // get the access code
            String accCode = cardsServiceHome.getValue(accQry);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Card Access Code Found -- > " + accCode);

            if(accCode == null) {
                respObj.setRespCode("06");
                respObj
                        .setRespDesc("Card Access Code is not available against Card("
                                + requestObj.getCardNo() + ")");
                // return the response
                return respObj;
            } // end if(accCode == null)

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction in MCP System...");
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.GET_AAC_MSG, null, requestObj
                    .getDeviceId(), requestObj.getCardAcceptorId(), requestObj
                    .getCardAcceptNameAndLoc(), requestObj.getMcc(), requestObj
                    .getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial NO -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Applying the fee at OLTP..");
                // apply fee at OLTP
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());

                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    return respObj;
                } // end if
            } // end if(requestObj.getApplyFee().equalsIgnoreCase("Y"))
            else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else

            // set the iso serial no in response
            respObj.setTransId(transIds[0]);
            // set the response to ok
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            respObj.setAAC(accCode);
            return respObj;
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end getCardAccessCode




    /**
     * This method sets the ACC. First it validates the card information. Then
     * it gets the switch information against the switch id which is contained
     * in the request. It then validates the switch information such as whether
     * the switch is active and in the batch mode. If the switch is in-active or
     * in the batch mode then it logs the transaction, populate the response
     * object and returns the response to the client. It then checks whether the
     * application of the service fee is allowed, if it is then it gets the
     * service fee for this transaction, apply the service fee, sets the card
     * access code and returns the response to the client.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request information
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    // //////////////////////////// Changed
    // /////////////////////////////////////////////////////////
    // Constants.SET_AAC_SERVICE were replaced with requestObj.getServiceId() //
    // //////////////////////////////////////////////////////////////////////////////////////////////
    public ServicesResponseObj setCardAccessCode(ServicesRequestObj requestObj)
            throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);

        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Old Access Code supplied -- > " + requestObj.getAAC()
                            + "\n" + "New Access Code Supplied -- > "
                            + requestObj.getNewAAC());
            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            // get the switch info
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Batch Trans Allowed is 'Y'....");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating the Card......");
                // validate the information
                respObj = validateCard(requestObj.getCardNo(), requestObj
                        .getServiceId(), "N", null, requestObj.getDeviceType(),
                        requestObj.getDeviceId(), requestObj
                                .getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response After card validation :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());

                if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                    return respObj;

                /**
                 * **********************Calculate the
                 * Amount******************************
                 */
                double validateAmt = 0.0;
                double feeAmt = 0.0;

                if(requestObj.getApplyFee() != null
                        && requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Calculating the fee for service -- > "
                                    + requestObj.getServiceId());
                    // calcualte the service fee
                    ServicesResponseObj feeResp = cardsServiceHome
                            .getServiceFee(requestObj.getCardNo(), "0.0",
                                    requestObj.getServiceId(), requestObj
                                            .getDeviceType(), requestObj
                                            .getDeviceId(), requestObj
                                            .getCardAcceptorId(), requestObj
                                            .getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(), requestObj
                                            .getAccountNo(), requestObj
                                            .getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after fee  calculation :: Code -- > "
                                    + feeResp.getRespCode() + " && Desc -- > "
                                    + feeResp.getRespDesc() + " && Fee -- > "
                                    + feeResp.getFeeAmount());
                    if(!feeResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return feeResp;
                    // parse the fee amount
                    feeAmt = Double
                            .parseDouble((feeResp.getFeeAmount() != null
                                    && !feeResp.getFeeAmount().trim()
                                            .equals("") ? feeResp
                                    .getFeeAmount() : "0.0"));
                    validateAmt += feeAmt;
                } // end if
                /** ************************************************************************ */
                if(validateAmt > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Validating Card at SOLSPARK....");
                    // validate card balance at SOLSPARK
                    respObj = validateCardBalAtSolspark(requestObj.getCardNo(),
                            requestObj.getExpiryDate(), requestObj.getAAC(),
                            requestObj.getAccountNo(), validateAmt, requestObj
                                    .getServiceId(),
                            requestObj.getDeviceType(), requestObj
                                    .getDeviceId(), requestObj
                                    .getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after validation at SOLSPARK:: Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    if(!respObj.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return respObj;
                } // end if

                // make the solspark request object
                SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
                solsReqObj.setCardNum(requestObj.getCardNo());
                solsReqObj.setNewACC(requestObj.getNewAAC());
                solsReqObj.setSwitchInfo(switchInfo.getSwitchId());
                // make the solspark handler
                SolsparkHandler handler = new SolsparkHandler(con);
                // update the information at solspark
                SolsparkResponseObj solsRespObj = handler
                        .updateCardHolderInfo(solsReqObj);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response from SOLSPARK:: Trans Id -- > "
                                + solsRespObj.getTransID() + " && Code -- > "
                                + solsRespObj.getRespCode() + " && Desc -- > "
                                + solsRespObj.getRespDesc());

                if(!solsRespObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // return the response object
                    respObj.setRespCode(cardsServiceHome
                            .getSwitchResponseCode(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    respObj.setRespDesc(cardsServiceHome
                            .getSwitchResponseDesc(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    return respObj;
                } // end if
                // set the card balance in response
                respObj.setCardBalance(solsRespObj.getBalance());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the transaction in MCP System....");
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), Constants.SET_AAC_MSG, solsRespObj
                        .getTransID(), requestObj.getDeviceId(), requestObj
                        .getCardAcceptorId(), requestObj
                        .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                        requestObj.getAccountNo(), requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with ISO Serial No -- > "
                                + transIds[0]);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to change the status at MCP Side....");
                // change the AAC at MCP side too
                cardsServiceHome
                        .executeQuery("update cards set card_access_code='"
                                + requestObj.getNewAAC() + "' where card_no='"
                                + requestObj.getCardNo() + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Card Access code changed at MCP Side....");

                if(requestObj.getApplyFee().equalsIgnoreCase("Y") && feeAmt > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Applying the Fee at SOSLPARk...");
                    // parse the debit fee in double
                    double debitFee = Double
                            .parseDouble((solsRespObj.getFeeAmount() != null
                                    && !solsRespObj.getFeeAmount().trim()
                                            .equals("") ? solsRespObj
                                    .getFeeAmount() : "0.0"));
                    // apply the fee at SOLSPARK
                    respObj = cardsServiceHome.applyServiceFeeAtSolspark(
                            requestObj.getCardNo(), "0.0", requestObj
                                    .getServiceId(), debitFee, respObj
                                    .getCardBalance());

                    if(!respObj.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return respObj;

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Applying fee at OLTP...");
                    // apply fee at OLTP
                    cardsServiceHome.applyServiceFeeAtOltp(requestObj
                            .getCardNo(), requestObj.getServiceId(), respObj
                            .getCardBalance(), respObj.getTransId(),
                            transIds[1], requestObj.getDeviceType(), requestObj
                                    .getDeviceId(), requestObj
                                    .getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAcquirerId(),
                            requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                            requestObj.getAcqData2(), requestObj.getAcqData3());

                    return respObj;
                } // end if(requestObj.getApplyFee().equalsIgnoreCase("Y"))
                else if(feeAmt == 0) {
                    respObj.setFeeAmount("0.0");
                } // end else if
                respObj.setRespCode(Constants.SUCCESS_CODE);
                respObj.setRespDesc(Constants.SUCCESS_MSG);
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // return the response object
                    return respObj;
                } // end if
                // set the ret ref no in response
                respObj.setTransId(transIds[0]);
                // set the solspark information in response
                respObj.setSwitchTransId(solsRespObj.getTransID());
                respObj.setSwitchAuditNo(solsRespObj.getAuditNo());
                // set the response to success
                respObj.setRespCode(Constants.SUCCESS_CODE);
                respObj.setRespDesc(Constants.SUCCESS_MSG);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null
                // &&!switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating the Card......");
            // validate the information
            respObj = validateCard(requestObj.getCardNo(), requestObj
                    .getServiceId(), requestObj.getApplyFee(), null, requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAccountNo(), requestObj
                            .getAcquirerId());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response After card validation :: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());

            if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Setting the new AAC for the Card in MCP DB......");
            // build the query to get the ACC
            String updQry = "update cards set card_access_code='"
                    + requestObj.getNewAAC() + "' where card_no='"
                    + requestObj.getCardNo() + "'";
            // set the access code
            cardsServiceHome.executeQuery(updQry);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Card Access Code updated successfully");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction in MCP System....");
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.SET_AAC_MSG, null, requestObj
                    .getDeviceId(), requestObj.getCardAcceptorId(), requestObj
                    .getCardAcceptNameAndLoc(), requestObj.getMcc(), requestObj
                    .getAccountNo(), requestObj.getAcquirerId());
            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Applying fee at OLTP...");
                // apply the fee at oltp
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    // return the response object
                    return respObj;
                } // end if
            } // end if(requestObj.getApplyFee().equalsIgnoreCase("Y"))
            else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else

            // set the iso serial no in response
            respObj.setTransId(transIds[0]);
            // set the response to ok
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            return respObj;
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(trc);
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end setCardAccessCode




    /**
     * This method resets the Access Code of the supplied card. It first gets
     * the switch infromation, validate and log the transaction in case of
     * switch is in-active or it is in the batch mode. It then re-generate the
     * access code, translates the request object into the SOLSPARK request
     * object, updates the card holder profile, applies the fee, logs the
     * transaction and sends the response to the client.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request information
     * @throws Exception
     * @return ServicesResponseObj -- Response information
     */

    // //////////////////////////////////// Changed
    // ////////////////////////////////////////////////////
    // Constants.RESET_AAC were Replaced with requestObj.getServiceId() //
    // /////////////////////////////////////////////////////////////////////////////////////////////////
    public ServicesResponseObj resetCardAccessCode(ServicesRequestObj requestObj)
            throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);

        try {
            // get the switch information of card
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Switch is active and in Batch mode....");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating card....");
                // validate the card
                respObj = validateCard(requestObj.getCardNo(), requestObj
                        .getServiceId(), "N", null, requestObj.getDeviceType(),
                        requestObj.getDeviceId(), requestObj
                                .getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after card validation :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE))
                    return respObj;

                /**
                 * **********************Calculate the
                 * Amount******************************
                 */
                double validateAmt = 0.0;
                double feeAmt = 0.0;

                if(requestObj.getApplyFee() != null
                        && requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Calculating the fee for service -- > "
                                    + requestObj.getServiceId());
                    // calcualte the service fee
                    ServicesResponseObj feeResp = cardsServiceHome
                            .getServiceFee(requestObj.getCardNo(), "0.0",
                                    requestObj.getServiceId(), requestObj
                                            .getDeviceType(), requestObj
                                            .getDeviceId(), requestObj
                                            .getCardAcceptorId(), requestObj
                                            .getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(), requestObj
                                            .getAccountNo(), requestObj
                                            .getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after fee  calculation :: Code -- > "
                                    + feeResp.getRespCode() + " && Desc -- > "
                                    + feeResp.getRespDesc() + " && Fee -- > "
                                    + feeResp.getFeeAmount());
                    if(!feeResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return feeResp;
                    // parse the fee amount
                    feeAmt = Double
                            .parseDouble((feeResp.getFeeAmount() != null
                                    && !feeResp.getFeeAmount().trim()
                                            .equals("") ? feeResp
                                    .getFeeAmount() : "0.0"));
                    validateAmt += feeAmt;
                } // end if
                /** ************************************************************************ */
                if(validateAmt > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Validating Card at SOLSPARK....");
                    // validate card balance at SOLSPARK
                    respObj = validateCardBalAtSolspark(requestObj.getCardNo(),
                            requestObj.getExpiryDate(), requestObj.getAAC(),
                            requestObj.getAccountNo(), validateAmt, requestObj
                                    .getServiceId(),
                            requestObj.getDeviceType(), requestObj
                                    .getDeviceId(), requestObj
                                    .getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after validation at SOLSPARK:: Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    if(!respObj.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return respObj;
                } // end if
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Generating new access code....");
                // generate the access code
                String accessCode = cardsServiceHome
                        .generateAccessCode(requestObj.getCardNo());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Access Code generated -- > " + accessCode);
                // make the solspark handler
                SolsparkHandler handler = new SolsparkHandler(con);
                // make the solspark request
                SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
                solsReqObj.setCardNum(requestObj.getCardNo());
                solsReqObj.setSwitchInfo(switchInfo.getSwitchId());
                solsReqObj.setNewACC(accessCode);
                // change the AAC at SOLSPARk
                SolsparkResponseObj solsRespObj = handler
                        .updateCardHolderInfo(solsReqObj);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response from SOLSPARK :: Code -- > "
                                + solsRespObj.getRespCode() + " && Desc -- > "
                                + solsRespObj.getRespDesc()
                                + " && Trans Id -- > "
                                + solsRespObj.getTransID());

                if(!solsRespObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    respObj.setRespCode(cardsServiceHome
                            .getSwitchResponseCode(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    respObj.setRespDesc(cardsServiceHome
                            .getSwitchResponseDesc(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    return respObj;
                } // end if
                // set the balance in response
                respObj.setCardBalance(solsRespObj.getBalance());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the transaction....");
                // log the transaction in MCP System...
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), Constants.RESET_AAC_MSG, solsRespObj
                        .getTransID(), requestObj.getDeviceId(), requestObj
                        .getCardAcceptorId(), requestObj
                        .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                        requestObj.getAccountNo(), requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0]);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Reseting access code in mcp system...");
                // reset access code in MCP System
                cardsServiceHome.resetAccessCode(requestObj.getCardNo());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "AAC Reset successfully...");

                if(requestObj.getApplyFee().equalsIgnoreCase("Y") && feeAmt > 0) {
                    // parse the fee in double
                    double debitFee = Double.parseDouble((solsRespObj
                            .getFeeAmount() != null ? solsRespObj
                            .getFeeAmount() : "0.0"));
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to apply the fee at SOLSPARk...");
                    // apply the fee at SOLSPARK
                    respObj = cardsServiceHome.applyServiceFeeAtSolspark(
                            requestObj.getCardNo(), "0.0", requestObj
                                    .getServiceId(), debitFee, respObj
                                    .getCardBalance());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after applying Fee :: Code -- > "
                                    + respObj.getRespCode() + " && Desc - > "
                                    + respObj.getRespDesc());
                    if(!respObj.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return respObj;

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Applying fee at OLTP...");
                    // apply fee at OLTP
                    ServicesResponseObj feeResp = cardsServiceHome
                            .applyServiceFeeAtOltp(requestObj.getCardNo(),
                                    requestObj.getServiceId(), respObj
                                            .getCardBalance(), respObj
                                            .getTransId(), transIds[1],
                                    requestObj.getDeviceType(), requestObj
                                            .getDeviceId(), requestObj
                                            .getCardAcceptorId(), requestObj
                                            .getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(), requestObj
                                            .getAcquirerId(), requestObj
                                            .getAcqUsrId(), requestObj
                                            .getAcqData1(), requestObj
                                            .getAcqData2(), requestObj
                                            .getAcqData3());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after applying Fee :: Code == > "
                                    + feeResp.getRespCode() + " && Desc -- > "
                                    + feeResp.getRespDesc());
                    if(!feeResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return feeResp;
                } // end if
                else if(feeAmt == 0) {
                    respObj.setFeeAmount("0.0");
                } // end else if
                // set the ret ref no in response
                respObj.setTransId(transIds[0]);
                // set the solspark information in response object
                respObj.setSwitchTransId(solsRespObj.getTransID());
                respObj.setSwitchAuditNo(solsRespObj.getAuditNo());
                // set the response to success and return
                respObj.setRespCode(Constants.SUCCESS_CODE);
                respObj.setRespDesc(Constants.SUCCESS_MSG);
                respObj.setAAC(accessCode);
                return respObj;
            } // end (switchInfo.getSwitchId() != null
                // &&!switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating card.....");
            // validate the card
            respObj = validateCard(requestObj.getCardNo(), requestObj
                    .getServiceId(), requestObj.getApplyFee(), null, requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAccountNo(), requestObj
                            .getAcquirerId());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response after card Validation :: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Reseting AAC .....");
            // reset the AAc
            String newAAC = cardsServiceHome.resetAccessCode(requestObj
                    .getCardNo());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "AAC Reset successfully...");

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction....");
            // log the transaction in MCP System
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.RESET_AAC_MSG, null, requestObj
                    .getDeviceId(), requestObj.getCardAcceptorId(), requestObj
                    .getCardAcceptNameAndLoc(), requestObj.getMcc(), requestObj
                    .getAccountNo(), requestObj.getAcquirerId());
            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Applying Fee at OLTP...");
                // apply fee at OLTP
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after applying Fee :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());

                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    return respObj;
                } // end if
            } // end if
            else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else
            // set the iso serial no in response
            respObj.setTransId(transIds[0]);
            // set the new AAC
            respObj.setAAC(newAAC);
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            return respObj;
        } // end try
        catch(Exception ex) {
            String trc = CommonUtilities.getStackTrace(ex);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception exp) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + ex);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(ex.getMessage());
            respObj.setStkTrace(trc);
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end resetCardAccessCode




    /**
     * This method resets the PIN of the supplied card. This method is used to
     * reset the PIN Code of the supplied card no. It first gets the switch
     * information, validates and log the transaction if switch is in-active or
     * it is in the batch mode. It then gets the PIN generation method
     * re-generate the pin, apply the fee, translate the request object into the
     * SOLSPARK request object, update the card holder profile, apply the fee,
     * log the transaction and returns the response to the client.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request information
     * @throws Exception
     * @return ServicesResponseObj -- Response information
     */

    // ////////////////////////// Changed
    // //////////////////////////////////////////////////////////////
    // Constants.RESET_PIN were replaced with requestObj.getServiceId(); //
    // ////////////////////////////////////////////////////////////////////////////////////////////////
    public ServicesResponseObj resetPIN(ServicesRequestObj requestObj)
            throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        String pinLength = null;
        String cardPrgId = null;
        String oldPinOffset = null;
        String isSwitchPIN = null;
        boolean rollBackOffset = false;

        try {
            // get the switch information of card

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting isPINPreverified flag from system_variables...");
            isSwitchPIN = cardsServiceHome
                    .getValue("select is_pin_preverified from system_variables");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Value of isPINPreverified flag in system_variables--->"
                            + isSwitchPIN);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting the Old PIN offset, card prg id for provided card number--->"
                            + requestObj.getCardNo());
            String[] result = cardsServiceHome
                    .getValues("select card_prg_id,pin_offset from cards where card_no='"
                            + requestObj.getCardNo() + "'");
            if(result != null) {
                cardPrgId = result[0];
                oldPinOffset = result[1];
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Getting the PIN length defined in card program....");
                pinLength = cardsServiceHome
                        .getValue("select pin_length from card_programs where card_prg_id = '"
                                + cardPrgId + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "PIN length defined in card program -- > " + pinLength
                                + "<---Old PIN offset-->" + oldPinOffset
                                + "<---Card Prg ID--->" + cardPrgId);
            }
            if(pinLength == null) {
                respObj.setRespCode("06");
                respObj.setRespDesc("PIN Length not defined in card program ("
                        + cardPrgId + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                // return the response object
                return respObj;
            } // end if

            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            // variable to hold new pin
            String newPin = null;

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Switch is active and in Batch mode....");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating card....");
                // validate the card
                respObj = validateCard(requestObj.getCardNo(), requestObj
                        .getServiceId(), "N", "BA", requestObj.getDeviceType(),
                        requestObj.getDeviceId(), requestObj
                                .getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after card validation :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE))
                    return respObj;

                /**
                 * **********************Calculate the
                 * Amount******************************
                 */
                double validateAmt = 0.0;
                double feeAmt = 0.0;

                if(requestObj.getApplyFee() != null
                        && requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Calculating the fee for service -- > "
                                    + requestObj.getServiceId());
                    // calcualte the service fee
                    ServicesResponseObj feeResp = cardsServiceHome
                            .getServiceFee(requestObj.getCardNo(), "0.0",
                                    requestObj.getServiceId(), requestObj
                                            .getDeviceType(), requestObj
                                            .getDeviceId(), requestObj
                                            .getCardAcceptorId(), requestObj
                                            .getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(), requestObj
                                            .getAccountNo(), requestObj
                                            .getAcquirerId());

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after fee  calculation :: Code -- > "
                                    + feeResp.getRespCode() + " && Desc -- > "
                                    + feeResp.getRespDesc() + " && Fee -- > "
                                    + feeResp.getFeeAmount());

                    if(!feeResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return feeResp;

                    // parse the fee amount
                    feeAmt = Double
                            .parseDouble((feeResp.getFeeAmount() != null
                                    && !feeResp.getFeeAmount().trim()
                                            .equals("") ? feeResp
                                    .getFeeAmount() : "0.0"));
                    validateAmt += feeAmt;
                } // end if
                /** ************************************************************************ */
                if(validateAmt > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Validating Card at SOLSPARK....");
                    // validate card balance at SOLSPARK
                    respObj = validateCardBalAtSolspark(requestObj.getCardNo(),
                            requestObj.getExpiryDate(), requestObj.getAAC(),
                            requestObj.getAccountNo(), validateAmt, requestObj
                                    .getServiceId(),
                            requestObj.getDeviceType(), requestObj
                                    .getDeviceId(), requestObj
                                    .getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after validation at SOLSPARK:: Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    if(!respObj.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return respObj;
                } // end if
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Getting PIN Generation Method....");
                // get pin generation method
                String pinMethod = cardsServiceHome
                        .getPINGenerationMethod(requestObj.getCardNo());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "PIN generation method -- > " + pinMethod);

                if(pinMethod.equalsIgnoreCase("N")) {
                    respObj.setRespCode("06");
                    respObj
                            .setRespDesc("Could Not Generate Natural PIN at switch("
                                    + switchInfo.getSwitchId() + ")");

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to log the error transaction::Code -- > "
                                    + respObj.getRespCode() + " && Desc -- > "
                                    + respObj.getRespDesc());
                    // log the transaction
                    String[] transIds = cardsServiceHome.logTransaction(
                            requestObj.getCardNo(), requestObj.getServiceId(),
                            requestObj.getDeviceType(), null, "0200", "0",
                            respObj.getRespDesc(), "0", respObj.getRespCode(),
                            requestObj.getDeviceId(), requestObj
                                    .getCardAcceptorId(), requestObj
                                    .getCardAcceptNameAndLoc(), requestObj
                                    .getMcc(), requestObj.getAccountNo(),
                            requestObj.getAcquirerId());

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with iso serial no -- > "
                                    + transIds[0] + " && trace audit no -- > "
                                    + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                } // end if
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Generating the PIN...");
                // generate PIN
                newPin = cardsServiceHome.generatePIN(requestObj.getCardNo(),
                        pinMethod);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "PIN Generated -- > " + newPin);
                // make the solspark handler
                SolsparkHandler handler = new SolsparkHandler(con);
                // make the solspark request
                SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
                solsReqObj.setCardNum(requestObj.getCardNo());
                solsReqObj.setSwitchInfo(switchInfo.getSwitchId());
                solsReqObj.setNewPIN(newPin);
                // change the PIN at SOLSPARk
                SolsparkResponseObj solsRespObj = handler
                        .updateCardHolderInfo(solsReqObj);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response from SOLSPARK :: Code -- > "
                                + solsRespObj.getRespCode() + " && Desc -- > "
                                + solsRespObj.getRespDesc()
                                + " && Trans Id -- > "
                                + solsRespObj.getTransID());

                if(!solsRespObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    respObj.setRespCode(cardsServiceHome
                            .getSwitchResponseCode(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    respObj.setRespDesc(cardsServiceHome
                            .getSwitchResponseDesc(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    return respObj;
                } // end if
                // set the balance in response
                respObj.setCardBalance(solsRespObj.getBalance());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the transaction....");
                // log the transaction in MCP System...
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), Constants.RESET_PIN_MSG, solsRespObj
                        .getTransID(), requestObj.getDeviceId(), requestObj
                        .getCardAcceptorId(), requestObj
                        .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                        requestObj.getAccountNo(), requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0]);

                if(requestObj.getApplyFee().equalsIgnoreCase("Y") && feeAmt > 0) {
                    // parse the fee in double
                    double debitFee = Double.parseDouble((solsRespObj
                            .getFeeAmount() != null ? solsRespObj
                            .getFeeAmount() : "0.0"));
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to apply the fee at SOLSPARk...");
                    // apply the fee at SOLSPARK
                    respObj = cardsServiceHome.applyServiceFeeAtSolspark(
                            requestObj.getCardNo(), "0.0", requestObj
                                    .getServiceId(), debitFee, respObj
                                    .getCardBalance());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after applying Fee :: Code -- > "
                                    + respObj.getRespCode() + " && Desc - > "
                                    + respObj.getRespDesc());
                    if(!respObj.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return respObj;

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Applying fee at OLTP...");
                    // apply fee at OLTP
                    ServicesResponseObj feeResp = cardsServiceHome
                            .applyServiceFeeAtOltp(requestObj.getCardNo(),
                                    requestObj.getServiceId(), respObj
                                            .getCardBalance(), respObj
                                            .getTransId(), transIds[1],
                                    requestObj.getDeviceType(), requestObj
                                            .getDeviceId(), requestObj
                                            .getCardAcceptorId(), requestObj
                                            .getCardAcceptNameAndLoc(),
                                    requestObj.getMcc(), requestObj
                                            .getAcquirerId(), requestObj
                                            .getAcqUsrId(), requestObj
                                            .getAcqData1(), requestObj
                                            .getAcqData2(), requestObj
                                            .getAcqData3());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after applying Fee :: Code == > "
                                    + feeResp.getRespCode() + " && Desc -- > "
                                    + feeResp.getRespDesc());
                    if(!feeResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return feeResp;
                } // end if
                else if(feeAmt == 0) {
                    respObj.setFeeAmount("0.0");
                } // end else if
                // set the ret ref no in response
                respObj.setTransId(transIds[0]);
                // set the solspark information in response object
                respObj.setSwitchTransId(solsRespObj.getTransID());
                respObj.setSwitchAuditNo(solsRespObj.getAuditNo());
                // set the response to success and return
                respObj.setRespCode(Constants.SUCCESS_CODE);
                respObj.setRespDesc(Constants.SUCCESS_MSG);
                respObj.setPin(newPin);
                return respObj;
            } // end (switchInfo.getSwitchId() != null
                // &&!switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());

            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating card.....");
            // validate the card
            respObj = validateCard(requestObj.getCardNo(), requestObj
                    .getServiceId(), requestObj.getApplyFee(), null, requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAccountNo(), requestObj
                            .getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response after card Validation :: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());

            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting PIN Generation Method ....");
            // get PIN generation method
            String pinMethod = cardsServiceHome
                    .getPINGenerationMethod(requestObj.getCardNo());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "PIN generation method -- > " + pinMethod);

            if(pinMethod == null) {
                respObj.setRespCode("06");
                respObj
                        .setRespDesc("PIN Generation method is NULL in card program");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if

            String newPinOffset = null;

            if(pinMethod.equalsIgnoreCase("N")) {
                // set the offset = 0000 in case of natural pin
                newPinOffset = "0000";
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "PIN generation method natural, new PIN offset --->"
                                + newPinOffset);
                // Comunicate to EFUND;
            } else {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Calculating PIN....");
                // generate pin from method
                newPin = cardsServiceHome.generatePIN(requestObj.getCardNo(),
                        pinMethod);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Calculated PIN -- > " + newPin);
                requestObj.setNewPin(newPin);
                CommonUtilities
                        .getLogger()
                        .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Calling method for getting the pin offset from HSM...");

                respObj = getPINOffset(cardsServiceHome, requestObj);

                if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                    return respObj;

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Pin offset got from HSM--->" + respObj.getPinOffset());
                newPinOffset = respObj.getPinOffset();
            } // end else

            if(isSwitchPIN != null && isSwitchPIN.equals(Constants.YES_OPTION)) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "isPinPreVerified flag is 'Y', communicating Pin offset to switch--->"
                                + newPinOffset);

                respObj = commuincatePinOffset(cardsServiceHome, switchInfo,
                        requestObj, newPinOffset);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response recevided from EFUND communication API--->"
                                + respObj.getRespCode());
                if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                    return respObj;
            }

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to update the offset in mcp system--->"
                            + newPinOffset);
            // set the PIN of Card in MCP DB
            cardsServiceHome.executeQuery("update cards set pin_offset='"
                    + newPinOffset + "' where card_no='"
                    + requestObj.getCardNo() + "'");
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "PIN Offset updated successfully....");

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction....");
            // log the transaction in MCP System
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.RESET_PIN_MSG, null, requestObj
                    .getDeviceId(), requestObj.getCardAcceptorId(), requestObj
                    .getCardAcceptNameAndLoc(), requestObj.getMcc(), requestObj
                    .getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Applying Fee at OLTP...");
                // apply fee at OLTP
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after applying Fee :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());

                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    return respObj;
                } // end if
            } else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else
            // set the iso serial no in response
            respObj.setTransId(transIds[0]);
            // set the new PIN
            respObj.setPin(newPin);
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            return respObj;
        } // end try

        catch(Exception ex) {
            String trc = CommonUtilities.getStackTrace(ex);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + ex.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception exp) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + ex);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(ex.getMessage());
            respObj.setStkTrace(trc);
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end resetPIN




    /**
     * This method updates the card holder profile. It first gets the switch
     * infromation, vlidates the card attributes. If validation of the cards is
     * unsuccessful then it pauses the execution. Then it checks whether the
     * application of the service is allowed, if it is then it calculates the
     * service fee. If the service fee is greater then zero then it validates
     * the card balance at SOLSPARK switch. If validation is not successful then
     * it pauses the execution by returning the response object which describes
     * the cause of the error. In case of the successful card balance validation
     * at SOLSPARK switch, card holder's profile is upated using the
     * SOLSPARKHandler class. After updating the card holder profile service fee
     * is debited from the card holder account. At successful execution a
     * response is returned to the client which describes the execution status
     * with appropiate response code and the response description.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request information
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    // ////////////////////////////// Changed
    // ////////////////////////////////////////////////////////////
    // Constants.CH_PROFILE_UPD_SERVICE were replaced with
    // requestObj.getServiceId() //
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    public ServicesResponseObj updateCardHolderProfile(
            ServicesRequestObj requestObj) throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Received Card Holder Profile :: First Name -- > "
                    + requestObj.getFirstName() + "\n"
                    + "Middle Name --> " + requestObj.getMiddleName()
                    + "\n" + "Last Name -- > "
                    + requestObj.getLastName() + "\n" + "DOB -- > "
                    + requestObj.getDob() + "\n" + "Address -- > "
                    + requestObj.getAddressStreet1() + "\n"
                    + "City -- > " + requestObj.getCity() + "\n"
                    + "State Code -- > " + requestObj.getStateCode()
                    + "\n" + "Zip Code -- > " + requestObj.getZipCode()
                    + "\n" + "Country -- > " + requestObj.getCountry()
                    + "\n" + "Home Phone -- > "
                    + requestObj.getHomePhone() + "\n"
                    + "Work Phone -- > " + requestObj.getWorkPhone()
                    + "\n" + "Email -- > " + requestObj.getEmail()
                    + "\n" + "Gender --> " + requestObj.getGender()
                    + "\n" + "Mother Maiden Name --> "
                    + requestObj.getMotherMaidenName() + "\n"
                    + "SSN --> " + requestObj.getSsn());

            // get the instance of cards service home

            // get the switch info
            SwitchInfoObj switchInfo = cardsServiceHome
                                       .getCardSwitchInfo(requestObj.getCardNo());

            if (switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                                    + ") is not active for Card ("
                                    +
                                    cardsServiceHome.maskCardNo(requestObj.getCardNo())
                                    + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                        + respObj.getRespCode() + " && Desc -- > "
                        + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                        .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                        + transIds[0] + " && trace audit no -- > "
                        + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
            // !switchInfo.isSwitchActive())

            if (switchInfo.getSwitchId() != null
                && !switchInfo.getSwitchId().trim().equals("")
                && switchInfo.isBatchTransAllowed()) {

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Validating the Card.....");
                // validate the information
                respObj = validateCard(requestObj.getCardNo(), requestObj
                                       .getServiceId(), "N", "ABE",
                                       requestObj.getDeviceType(),
                                       requestObj.getDeviceId(),
                                       requestObj.getCardAcceptorId(),
                                       requestObj
                                       .getCardAcceptNameAndLoc(),
                                       requestObj.getMcc(),
                                       requestObj.getAccountNo(),
                                       requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after validation :: Code -- > "
                        + respObj.getRespCode() + " Desc -- > "
                        + respObj.getRespDesc());
                // validate the response
                if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                    return respObj;

                /**
                 * **********************Calculate the
                 * Amount******************************
                 */
                double validateAmt = 0.0;
                double feeAmt = 0.0;

                if (requestObj.getApplyFee() != null
                    && requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Calculating the fee for service -- > "
                            + requestObj.getServiceId());
                    // calcualte the service fee
                    ServicesResponseObj feeResp = cardsServiceHome
                                                  .getServiceFee(requestObj.
                            getCardNo(), "0.0",
                            requestObj.getServiceId(), requestObj
                            .getDeviceType(), requestObj
                            .getDeviceId(), requestObj
                            .getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(),
                            requestObj.getMcc(), requestObj
                            .getAccountNo(), requestObj
                            .getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after fee  calculation :: Code -- > "
                            + feeResp.getRespCode() + " && Desc -- > "
                            + feeResp.getRespDesc() + " && Fee -- > "
                            + feeResp.getFeeAmount());
                    if (!feeResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return feeResp;
                    // parse the fee amount
                    feeAmt = Double
                             .parseDouble((feeResp.getFeeAmount() != null
                                           && !feeResp.getFeeAmount().trim()
                                           .equals("") ? feeResp
                                           .getFeeAmount() : "0.0"));
                    validateAmt += feeAmt;
                } // end if
                /** ************************************************************************ */
                if (validateAmt > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Validating Card at SOLSPARK....");
                    // validate card balance at SOLSPARK
                    respObj = validateCardBalAtSolspark(requestObj.getCardNo(),
                            requestObj.getExpiryDate(), requestObj.getAAC(),
                            requestObj.getAccountNo(), validateAmt, requestObj
                            .getServiceId(),
                            requestObj.getDeviceType(), requestObj
                            .getDeviceId(), requestObj
                            .getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj
                            .getMcc(), requestObj.getAcquirerId());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after validation at SOLSPARK:: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
                    if (!respObj.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return respObj;
                } // end if

                CommonUtilities
                        .getLogger()
                        .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                             "Batch Trans Allowed is 'Y' and updating the CH profile at SOLSPARK...");
                // create the solspark handler
                SolsparkHandler handler = new SolsparkHandler(con);
                // create solspark request object
                SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
                // set the card no
                solsReqObj.setCardNum(requestObj.getCardNo());
                // fill the fields in solspark request object
                CommonUtilities.fillTheCHInfoInSolsparkObj(solsReqObj,
                        requestObj);
                // set the switch
                solsReqObj.setSwitchInfo(switchInfo.getSwitchId());
                // update the cardholder information at SOLSPARK
                SolsparkResponseObj solsRespObj = handler
                                                  .updateCardHolderInfo(
                        solsReqObj);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Received from SOLSPARK :: Response Code -- > "
                        + solsRespObj.getRespCode()
                        + " &&  Resp Desc - > "
                        + solsRespObj.getRespDesc()
                        + " && Trans Id -- > "
                        + solsRespObj.getTransID());

                if (!solsRespObj.getRespCode().equals(Constants.SUCCESS_CODE)) {
                    // set the response code and description got from SOLSPARK
                    respObj.setRespCode(solsRespObj.getRespCode());
                    // set the response description
                    respObj.setRespDesc(cardsServiceHome
                                        .getSwitchResponseDesc(switchInfo.
                            getSwitchId(),
                            solsRespObj.getRespCode()));
                    // return the response object
                    return respObj;
                } // end if(!solsRespObj.equals("00"))
                // build the query to update the card holder profile
                ServicesResponseObj updResp = cardsServiceHome.updateCHProfile(
                        requestObj);
                if (updResp.isEmptyProfile()) {
                    respObj.setRespCode(Constants.SUCCESS_CODE);
                    respObj.setRespDesc("OK");
                    // set the solspark information in response
                    respObj.setSwitchTransId(solsRespObj.getTransID());
                    respObj.setSwitchAuditNo(solsRespObj.getAuditNo());
                    respObj.setSwitchAcctNo(solsRespObj.getAccountNum());
                    respObj.setSwitchRoutingNo(solsRespObj.getRountingNum());
                    return respObj;
                } // end if

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the trandaction in MCP System....");
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), Constants.UPDATE_PROFILE_MSG,
                        solsRespObj.getTransID(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                        .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with ISO Serial No -- > "
                        + transIds[0]);
                if (requestObj.getApplyFee().equalsIgnoreCase("Y") &&
                    feeAmt > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Applying the Fee at SOLSPARK....Fee -- > "
                            + solsRespObj.getFeeAmount());
                    // parse the debit fee to double
                    double debitFee = Double
                                      .parseDouble((solsRespObj.getFeeAmount() != null
                            && !solsRespObj.getFeeAmount().trim()
                            .equals("") ? solsRespObj
                            .getFeeAmount() : "0.0"));
                    // apply the fee at SOLSPARK
                    respObj = cardsServiceHome.applyServiceFeeAtSolspark(
                            requestObj.getCardNo(), "0.0", requestObj
                            .getServiceId(), debitFee, respObj
                            .getCardBalance());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response Got After Applying Fee at SOLSAPRK :: Resp Code -- > "
                            + respObj.getRespCode() + " && Desc -- >  "
                            + respObj.getRespDesc());
                    // validate the response
                    if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                        return respObj;

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Applying the Fee at OLTP...");
                    // apply the fee at OLTP
                    ServicesResponseObj feeResp = cardsServiceHome
                                                  .applyServiceFeeAtOltp(
                            requestObj.getCardNo(),
                            requestObj.getServiceId(), respObj
                            .getCardBalance(), respObj
                            .getTransId(), transIds[1],
                            requestObj.getDeviceType(), requestObj
                            .getDeviceId(), requestObj
                            .getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(),
                            requestObj.getMcc(), requestObj
                            .getAcquirerId(), requestObj
                            .getAcqUsrId(), requestObj
                            .getAcqData1(), requestObj
                            .getAcqData2(), requestObj
                            .getAcqData3());
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Response after applying Fee :: Code -- > "
                            + feeResp.getRespCode() + " && Desc -- > "
                            + feeResp.getRespDesc());
                    if (!feeResp.getRespCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE))
                        return feeResp;
                } else if (feeAmt == 0) { // end if(requestObj.getApplyFee().equalsIgnoreCase("Y"))
                    respObj.setFeeAmount("0.0");
                } // end else if

                // set the solspark information in response
                respObj.setSwitchTransId(solsRespObj.getTransID());
                respObj.setSwitchAuditNo(solsRespObj.getAuditNo());
                respObj.setSwitchAcctNo(solsRespObj.getAccountNum());
                respObj.setSwitchRoutingNo(solsRespObj.getRountingNum());
                // set the ret ref no in response
                respObj.setTransId(transIds[0]);
                respObj.setRespCode(Constants.SUCCESS_CODE);
                respObj.setRespDesc(Constants.SUCCESS_MSG);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null
            // &&!switchInfo.getSwitchId().trim().equals("") &&
            // switchInfo.isBatchTransAllowed())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                    .getAccountNo(), requestObj.getPin(), requestObj
                    .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if (!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating the Card.....");
            // validate the information
            respObj = validateCard(requestObj.getCardNo(), requestObj
                                   .getServiceId(), requestObj.getApplyFee(),
                                   "BA", requestObj
                                   .getDeviceType(), requestObj.getDeviceId(),
                                   requestObj
                                   .getCardAcceptorId(),
                                   requestObj.getCardAcceptNameAndLoc(),
                                   requestObj.getMcc(), requestObj.getAccountNo(),
                                   requestObj
                                   .getAcquirerId());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response after validation :: Code -- > "
                    + respObj.getRespCode() + " Desc -- > "
                    + respObj.getRespDesc());
            // validate the response
            if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting existing profile of provided card...");

            ServicesRequestObj existingProfile = cardsServiceHome.
                                                 getExistingProfile(requestObj.
                    getCardNo());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Comapring existing profile with provided profile information...");

            ServicesRequestObj updProfile = cardsServiceHome.
                                            getUpdatedProfileFields(requestObj,
                    existingProfile);
            updProfile.setCardNo(requestObj.getCardNo());
            updProfile.setCardPrgId(requestObj.getCardPrgId());
            updProfile.setServiceId(requestObj.getServiceId());
            updProfile.setDeviceType(requestObj.getDeviceType());
            updProfile.setDeviceId(requestObj.getDeviceId());
            updProfile.setCardAcceptorId(requestObj.getCardAcceptorId());
            updProfile.setCardAcceptNameAndLoc(requestObj.
                                               getCardAcceptNameAndLoc());
            updProfile.setMcc(requestObj.getMcc());
            updProfile.setAccountNo(requestObj.getAccountNo());
            updProfile.setAcquirerId(requestObj.getAcquirerId());

            boolean performOfacAvs = false;
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Checking whether to perform OFAC/AVS for card program--->"
                    + updProfile.getCardPrgId());
            if (!cardsServiceHome.performCardHolderVerification(updProfile.
                    getCardPrgId())) {
                performOfacAvs = false;
            } else {
                int chkResult = cardsServiceHome
                                .checkOfacAvsFieldUpdates(updProfile);
                if (chkResult == 1) {
                    performOfacAvs = true;
                    // perform OFAC/AVS
                } else if (chkResult == 2) {
//                    Vector ofacAvsInfo = cardsServiceHome
//                            .getOfacAvsExistingFields(updProfile.getCardNo());
                    if (updProfile.getFirstName() == null
                        || updProfile.getFirstName().trim().length() == 0) {
                        updProfile.setFirstName(existingProfile.getFirstName());
                    }
                    if (updProfile.getLastName() == null
                        || updProfile.getLastName().trim().length() == 0) {
                        updProfile.setLastName(existingProfile.getLastName());
                    }
                    if (updProfile.getAddressStreet1() == null
                        || updProfile.getAddressStreet1().trim()
                        .length() == 0) {
                        updProfile.setAddressStreet1(existingProfile.getAddressStreet1());
                    }
                    if (updProfile.getCity() == null
                        || updProfile.getCity().trim().length() == 0) {
                        updProfile.setCity(existingProfile.getCity());
                    }
                    if (updProfile.getStateCode() == null
                        || updProfile.getStateCode().trim().length() == 0) {
                        updProfile.setStateCode(existingProfile.getStateCode());
                    }
                    if (updProfile.getZipCode() == null
                        || updProfile.getZipCode().trim().length() == 0) {
                        updProfile
                                .setZipCode(existingProfile.getZipCode());
                    }
                    if (updProfile.getSsn() == null
                        || updProfile.getSsn().trim().length() == 0) {
                        if (updProfile.getDrivingLicesneNo() == null
                            || updProfile.getDrivingLicesneNo().trim()
                            .length() == 0
                            || updProfile.getDrivingLicesneState() == null
                            || updProfile.getDrivingLicesneState()
                            .trim().length() == 0) {
                            String existingSSN = existingProfile.getSsn();
                            String existingDL = existingProfile.getDrivingLicesneNo();
                            String existingDLState = existingProfile.getDrivingLicesneState();
                            if (existingSSN != null
                                && existingSSN.trim().length() > 0) {
                                updProfile.setSsn(existingSSN);
                            } else if (existingDL != null
                                       && existingDLState != null
                                       && existingDL.trim().length() > 0
                                       && existingDLState.trim().length() > 0) {
                                updProfile.setDrivingLicesneNo(existingDL);
                                updProfile
                                        .setDrivingLicesneState(existingDLState);
                            }
                        }
                    }
                    performOfacAvs = true;
                } else if (chkResult == 3) {
                    performOfacAvs = false;
                }
            }

            if (performOfacAvs) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Checking OFAC/AVS of provided information...");
                respObj = cardsServiceHome.checkNewOFAC_AVS(updProfile);
                if (!respObj.getRespCode().equals(Constants.SUCCESS_CODE)) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc()
                            + "<---OFAC Code--->"
                            + respObj.getOfacRespCode()
                            + "<---AVS Code--->"
                            + respObj.getAvsRespCode()
                            + "<---OFAC Desc--->"
                            + respObj.getOfacRespDesc()
                            + "<---AVS Desc--->"
                            + respObj.getAvsRespDesc());
                    // log the transaction
                    String[] transIds = cardsServiceHome.logTransaction(
                            requestObj.getCardNo(), requestObj.getServiceId(),
                            requestObj.getDeviceType(), null, "0200", "0",
                            respObj.getRespDesc(), "0", respObj.getRespCode(),
                            requestObj.getDeviceId(), requestObj
                            .getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj
                            .getMcc(), requestObj.getAccountNo(),
                            requestObj.getAcquirerId());

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with iso serial no -- > "
                            + transIds[0] + " && trace audit no -- > "
                            + transIds[1]);
                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                }
            } else {
                CommonUtilities
                        .getLogger()
                        .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                             "Update request conatins no fields which require OFAC/AVS...");
            }
            CommonUtilities
                    .getLogger()
                    .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                         "Updating new profile information for cardholder...");
            // build the query to update the card holder profile
            respObj = cardsServiceHome.updateCHProfile(updProfile);
            if (respObj.isEmptyProfile()) {
                return respObj;
            }
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction in MCP System...");
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.UPDATE_PROFILE_MSG, null,
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());
            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No -- > "
                            + transIds[0]);
            if (requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Applying the Fee at OLTP.....");
                // apply the fee at OLTP
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                        .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after applying Fee :: Code -- > "
                        + respObj.getRespCode() + " && Desc -- > "
                        + respObj.getRespDesc());
                if (!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    return respObj;
                } // end if
            } else {
                // get the latest balance
                String balance = cardsServiceHome
                                 .getValue(
                        "select card_balance from card_funds where card_no='"
                        + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else

            // set the iso serial no in response
            respObj.setTransId(transIds[0]);
            return respObj;
        } catch (Exception exp) { // end try
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                    + trc);
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception ex) { // end try
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(trc);
            respObj.setLogFilePath("More information can be found at--->"
                                   + Constants.EXACT_LOG_PATH);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                    + respObj.getRespCode() + " && Desc -- > "
                    + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                    .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                    + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end updateCardHolderProfile




    /**
     * This method is used to replace the stolen card. It first checks whether
     * the stolen card can be replaced or not. If the card can not be replaced
     * then it logs the transaction and returns the response to the client with
     * appropiate response code and response description. Then it gets the
     * switch inoformation and validate them. If the switch is active and in the
     * batch mode then it gets the switch balance of the card at the SOLSPARK
     * switch. After this it calls the SOLSPARK API to replace the old card with
     * the new one.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request Information
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    public ServicesResponseObj replaceStolenCard(ServicesRequestObj requestObj)
            throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        String switchBal = null;
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Checking for the replace stolen card .....");
            // check for the replace sotlen card
            respObj = cardsServiceHome.checkCardReplace(requestObj.getCardNo(),
                    requestObj.getNewCardNo(), Constants.REP_STOLEN_SERVICE,
                    requestObj.getApplyFee());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response Received from Check Replace :: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            if(respObj.getRespCode() != null
                    && !respObj.getRespCode().trim().equalsIgnoreCase(
                            Constants.SUCCESS_CODE)) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), Constants.REP_STOLEN_SERVICE, requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                return respObj;
            } // end if

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting the switch information...");
            // get the switch information
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), Constants.REP_STOLEN_SERVICE, requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Batch Trans is allowed against switch -- > "
                                + switchInfo.getSwitchId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Getting the balance of the card at SOLSPARK...");
                // make the solspark handler
                SolsparkHandler handler = new SolsparkHandler(con);
                // make the solspark request object
                SolsparkRequestObj solsReqObj = new SolsparkRequestObj();
                solsReqObj.setCardNum(requestObj.getCardNo());
                // set the switch information
                solsReqObj.setSwitchInfo(switchInfo.getSwitchId());
                // get the balance from SOLSPARK
                SolsparkResponseObj solsRespObj = handler
                        .getCardBalance(solsReqObj);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after getting Balance:: Code -- > "
                                + solsRespObj.getRespCode() + " && Desc -- > "
                                + solsRespObj.getRespDesc());

                if(solsRespObj.getRespCode() != null
                        && !solsRespObj.getRespCode().trim().equalsIgnoreCase(
                                Constants.SUCCESS_CODE)) {
                    respObj.setRespCode(cardsServiceHome
                            .getSwitchResponseCode(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    respObj.setRespDesc(cardsServiceHome
                            .getSwitchResponseDesc(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    return respObj;
                } // end if

                switchBal = solsRespObj.getBalance();
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Replacing Stolen Card at SOLSPARK....");
                // set the request parameters
                solsReqObj.setCardNum(requestObj.getCardNo());
                solsReqObj.setNewCardNum(requestObj.getNewCardNo());
                // deactivate the card at solspark
                solsRespObj = handler.replaceStolenCard(solsReqObj);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response Got from SOLSPARK :: Resp Code -- > "
                                + solsRespObj.getRespCode() + " && Desc -- > "
                                + solsRespObj.getRespDesc()
                                + " && Trans Id -- > "
                                + solsRespObj.getTransID());

                if(!solsRespObj.getRespCode().equals(Constants.SUCCESS_CODE)) {
                    respObj.setRespCode(cardsServiceHome
                            .getSwitchResponseCode(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    respObj.setRespDesc(cardsServiceHome
                            .getSwitchResponseDesc(switchInfo.getSwitchId(),
                                    solsRespObj.getRespCode()));
                    return respObj;
                } // end
                    // if(!solsRespObj.getRespCode().equals(Constants.SUCCESS_CODE))
                // set the balance in response
                respObj.setCardBalance(solsRespObj.getBalance());
                respObj.setSwitchTransId(solsRespObj.getTransID());
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to call the confirm replace card....");
            // call the confirm replace card
            ServicesResponseObj repRespObj = cardsServiceHome.applyReplaceCard(
                    requestObj.getCardNo(), requestObj.getNewCardNo(),
                    Constants.REP_STOLEN_SERVICE, requestObj.getApplyFee(),
                    requestObj.getDeviceType(), "20", respObj
                            .getSwitchTransId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response from confirm replace card :: Code -- > "
                            + repRespObj.getRespCode() + " && Desc -- > "
                            + repRespObj.getRespDesc());

            if(repRespObj.getRespCode() != null
                    && !repRespObj.getRespCode().trim().equalsIgnoreCase(
                            Constants.SUCCESS_CODE)) {
                return repRespObj;
            } else {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Getting card account no....");
                // get the account no
                String accountNo = cardsServiceHome
                        .getValue("select account_number from card_accounts where card_no='"
                                + requestObj.getNewCardNo() + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Account no got -- > " + accountNo);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Getting the Card Expiry date....");
                // get expiry date
                String expiryDate = cardsServiceHome
                        .getValue("select expiry_on from cards where card_no='"
                                + requestObj.getNewCardNo() + "'");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Expiry Date Got -- > " + expiryDate);

                if(expiryDate != null) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Going to change the format of expiry date...");
                    try {
                        // convert the date format
                        expiryDate = CommonUtilities.convertDateFormat(
                                Constants.EXP_DATE_FORMAT,
                                Constants.INFORMIX_DATE_FORMAT, expiryDate);
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Expiry Date after formatting -- > "
                                        + expiryDate);
                    } // end try
                    catch(Exception exp) {
                        CommonUtilities
                                .getLogger()
                                .log(LogLevel.getLevel(Constants.LOG_SEVERE),
                                        "Exception while changing expiry date format...");
                        expiryDate = null;
                    } // end catch
                } // end if

                respObj.setToCardNo(requestObj.getNewCardNo());
                respObj.setToCardBalance(repRespObj.getCardBalance());
                respObj.setToCardAccountNo(accountNo);
                respObj.setToCardExpDate(expiryDate);
                respObj.setTransId(repRespObj.getTransId());
                respObj.setRespCode(repRespObj.getRespCode());
                respObj.setRespDesc(repRespObj.getRespDesc());
                respObj.setFeeAmount(repRespObj.getFeeAmount());
            } // end else

            return respObj;
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end replaceStolenCard




    /**
     * This method validates the PIN of supplied card no. First it gets the
     * switch information from the database and checks the switch for activation
     * and online mode. If the switch is inactive or in the batch mode,
     * transaction is logged and appropiate response is returned to the client
     * with response code and description of the response. It then validates the
     * card information and gets the PIN offset from the database for the
     * validation of the PIN by the HSM Handler. After validation of the PIN
     * transaction is logged and appropiate response i.e. whether pin validation
     * was succcessful or not is returned to the user.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request information
     * @throws Exception
     * @return ServicesResponseObj -- Response information
     */

    // //////////////////////////////// Changed
    // /////////////////////////////////////////////////////////
    // Constants.VALIDATE_PIN_SERVICE were replaced with
    // requestObj.getServiceId() //
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    public ServicesResponseObj validatePIN(ServicesRequestObj requestObj)
            throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        HSMService mgr = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating Card...");
            // get the switch information of the card
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Batch Trans is allowed for the switch -- > "
                                + switchInfo.getSwitchId());
                respObj.setRespCode("40");
                respObj
                        .setRespDesc("PIN Validation Service is not available for switch("
                                + switchInfo.getSwitchId() + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), false,
                    requestObj.getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting the PIN for Card No -- > "
                            + requestObj.getCardNo());
            // validate the card
            respObj = validateCard(requestObj.getCardNo(), requestObj
                    .getServiceId(), requestObj.getApplyFee(), null, requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAccountNo(), requestObj
                            .getAcquirerId());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response After validation :: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // check for the response
            if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting the PIN offset...");
            // get pin offset
            String pinOffset = cardsServiceHome
                    .getValue("select pin_offset from cards where card_no='"
                            + requestObj.getCardNo() + "'");

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Got PIN Offset -- > " + pinOffset);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating the PIN at MCP System....");
            // create hsm manager
            mgr = new HSMService(Constants.HSM_LOG_FILE_PATH,
                    Constants.HSM_WRPR_FILE_PATH, con);

            // validate the pin from hsm
            HSMResponse hsmResp = mgr.validatePin(requestObj.getCardNo(),
                    requestObj.getPin(), requestObj.getPin().length(),
                    pinOffset, requestObj.getPin().length());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response from HSM -- > " + hsmResp.getResponseCode());

            if(hsmResp.getResponseCode() != null
                    && !hsmResp.getResponseCode().equalsIgnoreCase(
                            Constants.SUCCESS_CODE)) {
                respObj.setRespCode("55");
                respObj.setRespDesc("Invalid PIN(" + requestObj.getPin()
                        + ") supplied for Card No("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                return respObj;
            } // end if
            else if(hsmResp.getResponseCode() == null
                    || hsmResp.getResponseCode().trim().equals("")) {
                respObj.setRespCode("96");
                respObj.setRespDesc("System Malfunction");
                return respObj;
            } // end else if

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction...");
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.VALIDATE_PIN_MSG, null,
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());
            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee() != null
                    && requestObj.getApplyFee().trim().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Applying Fee for PIN Validation.....");
                // apply the fee for pin validation
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback all the work
                    con.rollback();
                    return respObj;
                } // end if
            } // end if
            else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else

            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);

            respObj.setTransId(transIds[0]);
            return respObj;
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
        finally {
            // if (mgr != null) {
            // mgr.closeHSMConnection();
            // mgr = null;
            // }
        }
    } // end validatePIN




    /**
     * This method validates the AAC of the supplied card no. First it gets the
     * switch information, determines whether the switch is active and online.
     * If the switch is inactive or in the batch mode then transaction is logged
     * and response with the appropiate response code and the response
     * description is returned to the user. After this card validation is
     * performed for validation of the card Access Code. Then it determines
     * whether the application of the service fee is allowed or not. If it is
     * then transaction fee is applied to the card and card balance is fetched
     * and returned to the client with proper description.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request Information
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    // //////////////////////// Changed
    // /////////////////////////////////////////////////////////////////
    // Constants.VALIDATE_AAC_SERVICE were replaced with
    // requestObj.getServiceId() //
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    public ServicesResponseObj validateAccessCode(ServicesRequestObj requestObj)
            throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        try {
            // get the switch information of the card
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Batch Trans is allowed for the switch -- > "
                                + switchInfo.getSwitchId());

                respObj.setRespCode("40");
                respObj
                        .setRespDesc("Account Access Code Validation Service is not available for switch("
                                + switchInfo.getSwitchId() + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating card......");
            // validate the card
            respObj = validateCard(requestObj.getCardNo(), requestObj
                    .getServiceId(), requestObj.getApplyFee(), null, requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAccountNo(), requestObj
                            .getAcquirerId());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response after card validation :: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // validate response
            if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction...");
            // log the transaction
            String transIds[] = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.VALIDATE_AAC_MSG, null,
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());
            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee() != null
                    && requestObj.getApplyFee().trim().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Applying Fee for AAC Validation.....");
                // apply the fee for pin validation
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback all the work
                    con.rollback();
                    return respObj;
                } // end if
            } // end if
            else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else
            // set the trace audit no in response
            respObj.setTransId(transIds[0]);
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            return respObj;
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end validateAccessCode




    /**
     * This method gets the accounts associated with the supplied card. First it
     * checks the switch activation and the online mode. If switch is inactive
     * or in the batch mode then transaction is logged and appropiate response
     * is returned to the user. If the switch validation was successfull then
     * validation of the card is carried out. After validation of the card has
     * been performed if an error occur then further execution is paused and the
     * response is returned to the user. After this card holder account is
     * fetched and service fee is applied on the card. After application of
     * service fee response is returned to the client.
     *
     * @param requestObj
     *        ServicesRequestObj -- Request Information
     * @throws Exception
     * @return ServicesResponseObj -- Response Information
     */

    // //////////////////////////////// Changed
    // //////////////////////////////////////////////////////
    // Constants.GET_CH_ACCOUNTS_SERVICE were Replaced with
    // requestObj.getServiceId() //////
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    public ServicesResponseObj getCardHolderAccounts(
            ServicesRequestObj requestObj) throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        try {
            // get the switch information of the card
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Batch Trans is allowed for the switch -- > "
                                + switchInfo.getSwitchId());

                respObj.setRespCode("40");
                respObj
                        .setRespDesc("Get Card Holder Accounts Service is not available for switch("
                                + switchInfo.getSwitchId() + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating card......");
            // validate the card
            respObj = validateCard(requestObj.getCardNo(), requestObj
                    .getServiceId(), requestObj.getApplyFee(), null, requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAccountNo(), requestObj
                            .getAcquirerId());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response after card validation :: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // validate response
            if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting the accounts for card....");
            // get the accounts for card
            Vector accountList = cardsServiceHome
                    .getCardHolderAccounts(requestObj.getCardNo());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "No of accounts got -- > " + accountList.size());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction....");
            // log the transaction
            String transIds[] = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0.0",
                    Constants.GET_CH_ACCOUNTS_MSG, "0", "00", requestObj
                            .getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());
            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee() != null
                    && requestObj.getApplyFee().trim().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Applying Fee.....");
                // apply the fee for pin validation
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    return respObj;
                } // end if
            } // end if
            else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else
            // set the account list in the response
            respObj.setCardAccounts(accountList);
            // set the trace audit no in response
            respObj.setTransId(transIds[0]);
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            return respObj;
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    } // end getCardHolderAccounts




    /**
     * This method is used to verfiy the given card. It first finds the switch
     * and validate the switch. If switch is inactive or in the batch mode then
     * execution is paused and response is returned to the client. If switch
     * validation was performed successfully then CardServiceHome class is used
     * to verify the given card. At the end resonse is returned to the client
     * which describes the processing status in detail.
     *
     * @param requestObj
     *        ServicesRequestObj
     * @throws Exception
     * @return ServicesResponseObj
     */
    public ServicesResponseObj verifyCard(ServicesRequestObj requestObj)
            throws Exception {
        // make the response object
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        try {
            // get the switch information of the card
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Batch Trans is allowed for the switch -- > "
                                + switchInfo.getSwitchId());

                respObj.setRespCode("40");
                respObj
                        .setRespDesc("Verify Card Service is not available for switch("
                                + switchInfo.getSwitchId() + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);

                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            // validate the card information
            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Calling method for verifying card");

            respObj = cardsServiceHome.verifyCard(requestObj);

            return respObj;
        } // end try
        catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;
        } // end catch
    }




    private ServicesResponseObj getPINOffset(CardsServiceHome cardsServiceHome,
            ServicesRequestObj requestObj) throws Exception {

        ServicesResponseObj respObj = new ServicesResponseObj();
        HSMResponse hsmResp = null;
        HSMService mgr = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting the pin offset from HSM......");

            try {
                mgr = new HSMService(Constants.HSM_LOG_FILE_PATH,
                        Constants.HSM_WRPR_FILE_PATH, con);
                // update the pin in MCP system
                hsmResp = mgr.setPin(requestObj.getCardNo(), requestObj
                        .getNewPin(), requestObj.getNewPin().length());
            } catch(IOException hsmIOEx) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "IO Exception received from HSM --->" + hsmResp);
                respObj.setRespCode("96");
                respObj.setRespDesc("Unable to change the PIN of Card No("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + "), IO Exception in HSM API--->" + hsmIOEx);
                respObj.setExcepMsg(hsmIOEx.getMessage());
                respObj.setStkTrace(CommonUtilities.getStackTrace(hsmIOEx));
                respObj.setLogFilePath("More information can be found at--->"
                        + Constants.EXACT_LOG_PATH);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
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
                        "Exception received from HSM --->" + hsmResp);
                respObj.setRespCode("96");
                respObj.setRespDesc("Unable to change the PIN of Card No("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + "), Exception in HSM API--->" + hsmEx);
                respObj.setExcepMsg(hsmEx.getMessage());
                respObj.setStkTrace(CommonUtilities.getStackTrace(hsmEx));
                respObj.setLogFilePath("More information can be found at--->"
                        + Constants.EXACT_LOG_PATH);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
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
                // mgr = null;
                // }
            }

            if(hsmResp == null) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "No response received from HSM --->" + hsmResp);
                respObj.setRespCode("96");
                respObj
                        .setRespDesc("Unable to change the PIN of Card No("
                                + cardsServiceHome.maskCardNo(requestObj
                                        .getCardNo())
                                + "), Invalid response received from HSM--->"
                                + hsmResp);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            }
            if(!hsmResp.getResponseCode().equalsIgnoreCase(
                    Constants.SUCCESS_CODE)) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Unsuccessful response received from HSM --- HSM response code--->"
                                + hsmResp.getResponseCode());
                respObj
                        .setRespCode((hsmResp.getResponseCode().equals("55") ? hsmResp
                                .getResponseCode()
                                : "96"));
                respObj.setRespDesc("Unable to change the PIN of Card No("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + "), HSM response code--->"
                        + hsmResp.getResponseCode());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "PIN Successfully changed using HSM :: Code -- > "
                            + hsmResp.getResponseCode() + " && Offset -- > "
                            + hsmResp.getOffset());
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in getting PIN offset from HSM--->" + ex);
            throw ex;
        }

        respObj.setRespCode("00");
        respObj.setRespDesc("OK");
        respObj.setPinOffset(hsmResp.getOffset());
        return respObj;
    }




    private ServicesResponseObj commuincatePinOffset(
            CardsServiceHome cardsServiceHome, SwitchInfoObj switchInfo,
            ServicesRequestObj requestObj, String pinOffset) throws Exception {

        ServicesResponseObj respObj = new ServicesResponseObj();
        FUResponse response = null;
        STrack2Manager tranckObj = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for communicating PIN offset to switch--->"
                            + pinOffset);
            // make the STrack2Manager instance
            if(!con.getAutoCommit()) {
                // con.commit();
                con.setAutoCommit(true);
            }
            try {
                tranckObj = new STrack2Manager(con, requestObj.getCardNo(),
                        Constants.FUR_LOG_FILE_PATH, switchInfo
                                .getLocalconnIP(), switchInfo
                                .getLocalConnPort());
                // call the change pin method
                response = tranckObj.getPinChangeResponse(pinOffset);
            } catch(Exception efundEx) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Exception in PIN change comunication API--->"
                                + efundEx);
                // rollback the work
                respObj.setRespCode("96");
                respObj
                        .setRespDesc("Exception in PIN change comunication API--->"
                                + efundEx);
                respObj.setExcepMsg(efundEx.getMessage());
                respObj.setStkTrace(CommonUtilities.getStackTrace(efundEx));
                respObj.setLogFilePath("More information can be found at--->"
                        + Constants.EXACT_LOG_PATH);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                // rollBackOffset = true;
                return respObj;
            } finally {
                if(con.getAutoCommit()) {
                    con.setAutoCommit(false);
                }
            }
            if(response == null) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "No Response recevide from API for comuuincating PIN change to switch--->"
                                + response);
                // rollback the work
                respObj.setRespCode("96");
                respObj
                        .setRespDesc("No Response received from API for comunicating PIN change to switch--->"
                                + response);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                // rollBackOffset = true;
                return respObj;
            } // end if

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response Received from Efund API::Code --> "
                            + response.getCode() + " && Desc --> "
                            + response.getDesc());

            if(response.getCode() != null
                    && !response.getCode().trim().equalsIgnoreCase(
                            Constants.SUCCESS_CODE)) {
                respObj.setRespCode(response.getCode());
                respObj.setRespDesc(response.getDesc());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction...");
                // log the error transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);

                // set the trans id in response
                respObj.setTransId(transIds[0]);
                // rollBackOffset = true;
                // return the repsonse
                return respObj;
            } // end if
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in commuincating PIN offset to switch--->" + ex);
            throw ex;
        }
        respObj.setRespCode("00");
        return respObj;
    }




    public ServicesResponseObj processInstantCardIssue(ServicesRequestObj reqObj)
            throws Exception {

        ServicesResponseObj respObj = new ServicesResponseObj();
        CardsServiceHome cardsServiceHome = null;
        String cvv2 = null;
        try {
            cardsServiceHome = CardsServiceHome.getInstance(this.con);
            String cardNo = null;
            reqObj.setServiceId(Constants.CARD_INSTANT_ISSUE_SERVICE);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for Processing Instant Card Issue --- ISO Serial No--->"
                            + reqObj.getTransId());
            if(reqObj.getTransId() == null
                    || reqObj.getTransId().trim().length() == 0) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Method for Processing Instant Card Issue --- Invalid ISO Serial No given--->"
                                + reqObj.getTransId());
                respObj.setRespCode("12");
                respObj.setRespDesc("Invalid Transaction ID given");
                String[] transIds = cardsServiceHome.logTransaction(reqObj
                        .getCardNo(), reqObj.getServiceId(), reqObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), reqObj
                        .getDeviceId(), reqObj.getCardAcceptorId(), reqObj
                        .getCardAcceptNameAndLoc(), reqObj.getMcc(), reqObj
                        .getAccountNo(), reqObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);

                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            }

            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for Processing Instant Card Issue --- Getting Card No from provided ISO Serial--->"
                                    + reqObj.getTransId());
            cardNo = cardsServiceHome.validateInstantIssueAttributes(reqObj
                    .getTransId());
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for Processing Instant Card Issue --- Got Card No from provided ISO Serial--->"
                                    + cardNo);
            if(cardNo == null || cardNo.trim().length() == 0) {
                respObj.setRespCode("12");
                respObj
                        .setRespDesc("Invalid Transaction ID given, No card Number found");
                String[] transIds = cardsServiceHome.logTransaction(reqObj
                        .getCardNo(), reqObj.getServiceId(), reqObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), reqObj
                        .getDeviceId(), reqObj.getCardAcceptorId(), reqObj
                        .getCardAcceptNameAndLoc(), reqObj.getMcc(), reqObj
                        .getAccountNo(), reqObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);

                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            }

            reqObj.setCardNo(cardNo);

            int status = cardsServiceHome.isExistingOFAC_AVSValid(reqObj.getCardNo());
            if(status != Constants.OFAC_AVS_GOOD) {
                if (status == Constants.OFAC_FAILED) {
                    respObj.setRespCode("OF");
                    respObj.setRespDesc("OFAC of provided card is not Good");
                } else if (status == Constants.AVS_FAILED) {
                    respObj.setRespCode("AV");
                    respObj.setRespDesc("AVS of provided card is not Good");
                }

                String[] transIds = cardsServiceHome.logTransaction(reqObj
                        .getCardNo(), reqObj.getServiceId(), reqObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), reqObj
                        .getDeviceId(), reqObj.getCardAcceptorId(), reqObj
                        .getCardAcceptNameAndLoc(), reqObj.getMcc(), reqObj
                        .getAccountNo(), reqObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);

                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            }

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating the Card....");
            // validate the information
            respObj = validateCard(reqObj.getCardNo(), reqObj.getServiceId(),
                    reqObj.getApplyFee(), "BA", reqObj.getDeviceType(), reqObj
                            .getDeviceId(), reqObj.getCardAcceptorId(), reqObj
                            .getCardAcceptNameAndLoc(), reqObj.getMcc(), reqObj
                            .getAccountNo(), reqObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "After Validation Response Received :: Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());

            if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            if(reqObj.getBirthYear() != null
                    && reqObj.getBirthYear().trim().length() > 0) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Getting DOB for card--->" + reqObj.getCardNo());
                String dob = cardsServiceHome.getCardInfo(reqObj.getCardNo(),
                        "select date_of_birth from cards where card_no = ? ");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got DOB for card--->" + dob);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Comaring Birth Years --- Birth Year in request--->"
                                + reqObj.getBirthYear() + "<---DOB--->" + dob);

                try {
                    if(compareBirthYear(reqObj.getBirthYear(), dob)) {
                        CommonUtilities
                                .getLogger()
                                .log(
                                        LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Comaring Birth Years --- Birth Year in request is mathced with Actual Birth Year");
                    }
                } catch(Exception byEx) {
                    respObj.setRespCode("SP");
                    respObj.setRespDesc("<---Unable to compare birth years-->"
                            + byEx);
                    String[] transIds = cardsServiceHome.logTransaction(reqObj
                            .getCardNo(), reqObj.getServiceId(), reqObj
                            .getDeviceType(), null, "0200", "0", respObj
                            .getRespDesc(), "0", respObj.getRespCode(), reqObj
                            .getDeviceId(), reqObj.getCardAcceptorId(), reqObj
                            .getCardAcceptNameAndLoc(), reqObj.getMcc(), reqObj
                            .getAccountNo(), reqObj.getAcquirerId());

                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with iso serial no -- > "
                                    + transIds[0] + " && trace audit no -- > "
                                    + transIds[1]);

                    // set the trans id in response
                    respObj.setTransId(transIds[0]);
                    return respObj;
                }
            }

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting Expiry Date for card--->" + reqObj.getCardNo());

            String expiryDate = cardsServiceHome.getCardInfo(
                    reqObj.getCardNo(),
                    "select expiry_on from cards where card_no = ? ");

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Got Expiry Date for card--->" + expiryDate);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting CVV2 from HSM for card--->" + reqObj.getCardNo());

            respObj = getCCV2(cardsServiceHome, reqObj);

            if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            cvv2 = respObj.getCvv2();

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log success transaction in MCP System....");
            // log the transaction in MCP System
            String[] transIds = cardsServiceHome.logTransaction(reqObj
                    .getCardNo(), reqObj.getServiceId(),
                    reqObj.getDeviceType(), Constants.CARD_INSTANT_ISSUE_MSG,
                    null, reqObj.getDeviceId(), reqObj.getCardAcceptorId(),
                    reqObj.getCardAcceptNameAndLoc(), reqObj.getMcc(), reqObj
                            .getAccountNo(), reqObj.getAcquirerId());
            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial No -- > "
                                    + transIds[0]);

            if(reqObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to apply the fee at OLTP...");
                // apply the fee at OLTP
                respObj = cardsServiceHome.applyServiceFeeAtOltp(reqObj
                        .getCardNo(), reqObj.getServiceId(), null, reqObj
                        .getRetreivalRefNum(), transIds[1], reqObj
                        .getDeviceType(), reqObj.getDeviceId(), reqObj
                        .getCardAcceptorId(), reqObj.getCardAcceptNameAndLoc(),
                        reqObj.getMcc(), reqObj.getAcquirerId(), reqObj
                                .getAcqUsrId(), reqObj.getAcqData1(), reqObj
                                .getAcqData2(), reqObj.getAcqData3());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Resp Got after applying Fee :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    // return the response object
                    return respObj;
                } // end if
            } else {
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + reqObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else

            // populate the message object with success code and description
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            respObj.setCardNo(reqObj.getCardNo());
            respObj.setCardExpDate(CommonUtilities.convertDateFormat(
                    Constants.IVR_DATE_FORMAT, Constants.INFORMIX_DATE_FORMAT,
                    expiryDate));
            respObj.setCvv2(cvv2);
            // set the iso serial no in response
            respObj.setTransId(transIds[0]);

            // return the response
            return respObj;
        } catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } // end try
            catch(Exception ex) {
            }
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(reqObj
                    .getCardNo(), reqObj.getServiceId(),
                    reqObj.getDeviceType(), null, "0200", "0", respObj
                            .getRespDesc(), "0", respObj.getRespCode(), reqObj
                            .getDeviceId(), reqObj.getCardAcceptorId(), reqObj
                            .getCardAcceptNameAndLoc(), reqObj.getMcc(), reqObj
                            .getAccountNo(), reqObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);

            return respObj;

        }
    }




    private ServicesResponseObj getCCV2(CardsServiceHome cardsServiceHome,
            ServicesRequestObj requestObj) throws Exception {

        ServicesResponseObj respObj = new ServicesResponseObj();
        HSMResponse hsmResp = null;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting CCV2 from HSM for card--->"
                            + requestObj.getCardNo());
            try {
                hsmResp = cardsServiceHome.getCVV2FromHSM(requestObj
                        .getCardNo());
            } catch(IOException hsmIOEx) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "IO Exception received from HSM --->" + hsmResp);
                respObj.setRespCode("96");
                respObj.setRespDesc("Unable to get CCV2 of Card No("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + "), IO Exception in HSM API--->" + hsmIOEx);
                respObj.setExcepMsg(hsmIOEx.getMessage());
                respObj.setStkTrace(CommonUtilities.getStackTrace(hsmIOEx));
                respObj.setLogFilePath("More information can be found at--->"
                        + Constants.EXACT_LOG_PATH);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
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
                        "Exception received from HSM --->" + hsmEx);
                respObj.setRespCode("96");
                respObj.setRespDesc("Unable to get CCV2 of Card No("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + "), Exception in HSM API--->" + hsmEx);
                respObj.setExcepMsg(hsmEx.getMessage());
                respObj.setStkTrace(CommonUtilities.getStackTrace(hsmEx));
                respObj.setLogFilePath("More information can be found at--->"
                        + Constants.EXACT_LOG_PATH);

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
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

            if(hsmResp == null) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "No response received from HSM --->" + hsmResp);
                respObj.setRespCode("96");
                respObj
                        .setRespDesc("Unable to get CCV2 of Card No("
                                + cardsServiceHome.maskCardNo(requestObj
                                        .getCardNo())
                                + "), Invalid response received from HSM--->"
                                + hsmResp);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            }
            if(!hsmResp.getResponseCode().equalsIgnoreCase(
                    Constants.SUCCESS_CODE)) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Unsuccessful response received from HSM --- HSM response code--->"
                                + hsmResp.getResponseCode());
                respObj.setRespCode("96");
                respObj.setRespDesc("Unable to get CCV2 of Card No("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + "), HSM response code--->"
                        + hsmResp.getResponseCode());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Got CCV2 using HSM :: Code -- > "
                            + hsmResp.getResponseCode() + " && CVV2 -- > "
                            + hsmResp.getCVV2());
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in getting CVV2 from HSM--->" + ex);
            throw ex;
        }

        respObj.setRespCode("00");
        respObj.setRespDesc("OK");
        respObj.setCvv2(hsmResp.getCVV2());
        return respObj;
    }




    private boolean compareBirthYear(String birthYear, String dob)
            throws Exception {
        boolean isOk = false;
        int year = -1;
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for comparing birth years --- Birth Year provided in request--->"
                            + birthYear + "<---DOB--->" + dob);
            if(birthYear.trim().length() != 4) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for comparing birth years --- Invalid Birth Year provided in request (Must be in format 'YYYY')--->"
                                        + birthYear);

                throw new Exception(
                        "Invalid Birth Year provided in request (Must be in format 'YYYY')--->"
                                + birthYear);
            }
            if(dob == null || dob.trim().length() == 0) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Method for comparing birth years --- DOB is undefined in database--->"
                                + dob);

                throw new Exception("DOB is undefined in database--->" + dob);
            }

            java.util.Date bDate = CommonUtilities.getFormatDate(
                    Constants.INFORMIX_DATE_FORMAT, dob);
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(bDate);

            year = gc.get(GregorianCalendar.YEAR);
            int bYear = Integer.parseInt(birthYear);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for comparing birth years --- Got Year from DOB--->"
                            + year + "<--Birth Year given in request-->"
                            + bYear);
            if(bYear == year) {
                return true;
            } else {
                throw new Exception(
                        "Provided Birth Year does not match with Actual Birth Year--->"
                                + birthYear + "<---Actual Birth Year--->"
                                + year);
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in comparing birth years --->" + ex);
            throw ex;
        }
    }




    public ServicesResponseObj assignCard(ServicesRequestObj requestObj)
            throws Exception {
        ServicesResponseObj respObj = new ServicesResponseObj();
        CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(con);
        try {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Calling method for assigning card");
            respObj = cardsServiceHome.assignCard(requestObj);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response of Card Assignment --- Response Code--->" + respObj.getRespCode()
                    + "<---Response Description--->" + respObj.getRespDesc()
                    + "<---Is Card Assigned--->" + respObj.isIsCardAssigned());
            return respObj;
        } catch(Exception exp) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception during card assignment failed--->" + exp);
            respObj.setRespCode("00");
            respObj.setRespDesc("OK");
            respObj.setIsCardAssigned(false);
            return respObj;
        } // end catch
    }




    public ServicesResponseObj getCardHolderPayeesList(
            ServicesRequestObj requestObj) throws Exception {
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = null;
        Vector chPayees = null;
        try {

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting cardholder payees list... ");

            cardsServiceHome = CardsServiceHome.getInstance(con);

            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            // get the switch info
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
                respObj.setRespCode("40");
                respObj
                        .setRespDesc("Get CardHolder Payee List service is not available for switch("
                                + switchInfo.getSwitchId() + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                // return the response object
                return respObj;
            } // end if(switchInfo.getSwitchId() != null
                // &&!switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating the Card.....");
            // validate the information
            respObj = validateCard(requestObj.getCardNo(), requestObj
                    .getServiceId(), requestObj.getApplyFee(), null, requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAccountNo(), requestObj
                            .getAcquirerId());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response after validation :: Code -- > "
                            + respObj.getRespCode() + " Desc -- > "
                            + respObj.getRespDesc());
            // validate the response
            if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting cardholder id for card--->"
                            + requestObj.getCardNo());
            String chId = cardsServiceHome.getCardHolderId(requestObj
                    .getCardNo());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Got cardholder id--->" + chId);
            if(chId == null || chId.trim().length() == 0) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "No cardholder id got, using provided card as CH ID--->"
                                + requestObj.getCardNo());
                chId = requestObj.getCardNo();
            }
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Fetching payees associated with cardholder id--->" + chId
                            + "<---Voice Only Payees--->"
                            + requestObj.isGetChPayeesIVROnly());
            chPayees = cardsServiceHome.getCardholderPayees(chId, requestObj
                    .isGetChPayeesIVROnly());
            if(chPayees != null && chPayees.size() > 0) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Total payees associated with cardholder id--->"
                                + chPayees.size());
            } else {
                CommonUtilities
                        .getLogger()
                        .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "No payees are currently associated with cardholder...");
            }

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction in MCP System...");
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.GET_CH_PAYEE_MSG, null,
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial NO -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Applying the Fee at OLTP.....");
                // apply the fee at OLTP
                respObj = cardsServiceHome.applyServiceFeeAtOltp(requestObj
                        .getCardNo(), requestObj.getServiceId(), null,
                        requestObj.getRetreivalRefNum(), transIds[1],
                        requestObj.getDeviceType(), requestObj.getDeviceId(),
                        requestObj.getCardAcceptorId(), requestObj
                                .getCardAcceptNameAndLoc(),
                        requestObj.getMcc(), requestObj.getAcquirerId(),
                        requestObj.getAcqUsrId(), requestObj.getAcqData1(),
                        requestObj.getAcqData2(), requestObj.getAcqData3());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after applying Fee :: Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                if(!respObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    return respObj;
                } // end if
            } else { // end if
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else

            // set the iso serial no in response
            respObj.setCardHolderPayees(chPayees);
            respObj.setTransId(transIds[0]);
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            return respObj;
            // validate the card information
        } catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } catch(Exception ex) {
            } // end try
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);
            return respObj;
        }
    }




    public ServicesResponseObj getBillPaymentStatus(
            ServicesRequestObj requestObj) throws Exception {
        ServicesResponseObj respObj = new ServicesResponseObj();
        // get the instance of cards service home
        CardsServiceHome cardsServiceHome = null;
        try {

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Method for getting bill payment status... ");

            cardsServiceHome = CardsServiceHome.getInstance(con);

            respObj = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Checking conditional fields...");
            if((requestObj.getRetreivalRefNum() == null || requestObj
                    .getRetreivalRefNum().trim().length() == 0)
                    && (requestObj.getTransId() == null || requestObj
                            .getTransId().trim().length() == 0)) {
                CommonUtilities
                        .getLogger()
                        .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Missing conditional fields required for getting bill payment status...");
                respObj.setRespCode("12");
                respObj
                        .setRespDesc("Missing conditional fields required for getting bill payment status");

                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            }

            // get the switch info
            SwitchInfoObj switchInfo = cardsServiceHome
                    .getCardSwitchInfo(requestObj.getCardNo());

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
                respObj.setRespCode("91");
                respObj.setRespDesc("Switch(" + switchInfo.getSwitchId()
                        + ") is not active for Card ("
                        + cardsServiceHome.maskCardNo(requestObj.getCardNo())
                        + ")");
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            } // end if(switchInfo.getSwitchId() != null &&
                // !switchInfo.isSwitchActive())

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Batch Trans Allowed is 'Y' and Solspark does not provide any service for that...");
                respObj.setRespCode("40");
                respObj
                        .setRespDesc("Get BillPayment Status service is not available for switch("
                                + switchInfo.getSwitchId() + ")");

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Going to log the error transaction::Code -- > "
                                + respObj.getRespCode() + " && Desc -- > "
                                + respObj.getRespDesc());
                // log the transaction
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                                .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                                + transIds[0] + " && trace audit no -- > "
                                + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                // return the response object
                return respObj;
            } // end if(switchInfo.getSwitchId() != null
                // &&!switchInfo.getSwitchId().trim().equals("") &&
                // switchInfo.isBatchTransAllowed())

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Validating the Card.....");
            // validate the information
            respObj = validateCard(requestObj.getCardNo(), requestObj
                    .getServiceId(), requestObj.getApplyFee(), null, requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAccountNo(), requestObj
                            .getAcquirerId());
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Response after validation :: Code -- > "
                            + respObj.getRespCode() + " Desc -- > "
                            + respObj.getRespDesc());
            // validate the response
            if(!respObj.getRespCode().equals(Constants.SUCCESS_CODE))
                return respObj;

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Getting bill payment status, Card No--->"
                            + requestObj.getCardNo() + "<---ISO Serial--->"
                            + requestObj.getTransId() + "<---ARN--->"
                            + requestObj.getRetreivalRefNum());
            respObj = cardsServiceHome.getBillPaymentStatus(requestObj
                    .getCardNo(), requestObj.getTransId(), requestObj
                    .getRetreivalRefNum());

            if(!respObj.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)){
                String[] transIds = cardsServiceHome.logTransaction(requestObj
                        .getCardNo(), requestObj.getServiceId(), requestObj
                        .getDeviceType(), null, "0200", "0", respObj
                        .getRespDesc(), "0", respObj.getRespCode(), requestObj
                        .getDeviceId(), requestObj.getCardAcceptorId(),
                        requestObj.getCardAcceptNameAndLoc(), requestObj
                        .getMcc(), requestObj.getAccountNo(),
                        requestObj.getAcquirerId());

                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Transaction logged with iso serial no -- > "
                        + transIds[0] + " && trace audit no -- > "
                        + transIds[1]);
                // set the trans id in response
                respObj.setTransId(transIds[0]);
                return respObj;
            }

            if(respObj.getBillPaymentRespCode() != null
                    && respObj.getBillPaymentProcessorId() != null) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Getting description for Bill Payment response code --->"
                                + respObj.getBillPaymentRespCode()
                                + "<---Processor ID--->"
                                + respObj.getBillPaymentProcessorId());
                String respDesc = cardsServiceHome
                        .getPaymentResponseDescription(respObj
                                .getBillPaymentRespCode(), respObj
                                .getBillPaymentProcessorId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got Description--->" + respDesc);
                respObj.setBillPaymentRespDesc(respDesc);
            }

            if(respObj.getBillPaymentRespDetailId() != null
                    && respObj.getBillPaymentProcessorId() != null) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Getting description for Bill Payment Detail response code --->"
                                + respObj.getBillPaymentRespDetailId()
                                + "<---Processor ID--->"
                                + respObj.getBillPaymentProcessorId());
                String respDesc = cardsServiceHome
                        .getPaymentDetailResponseDescription(respObj
                                .getBillPaymentRespDetailId(), respObj
                                .getBillPaymentProcessorId());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Got Description--->" + respDesc);
                respObj.setBillPaymentRespDetailDesc(respDesc);
            }

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Going to log the transaction in MCP System...");
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), Constants.GET_BP_STATUS_MSG, null,
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Transaction logged with ISO Serial NO -- > "
                                    + transIds[0]);

            if(requestObj.getApplyFee().equalsIgnoreCase("Y")) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Applying the Fee at OLTP.....");
                // apply the fee at OLTP
                ServicesResponseObj feeRespObj = cardsServiceHome
                        .applyServiceFeeAtOltp(requestObj.getCardNo(),
                                requestObj.getServiceId(), null, requestObj
                                        .getRetreivalRefNum(), transIds[1],
                                requestObj.getDeviceType(), requestObj
                                        .getDeviceId(), requestObj
                                        .getCardAcceptorId(), requestObj
                                        .getCardAcceptNameAndLoc(), requestObj
                                        .getMcc(), requestObj.getAcquirerId(),
                                requestObj.getAcqUsrId(), requestObj
                                        .getAcqData1(), requestObj
                                        .getAcqData2(), requestObj
                                        .getAcqData3());
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Response after applying Fee :: Code -- > "
                                + feeRespObj.getRespCode() + " && Desc -- > "
                                + feeRespObj.getRespDesc());
                if(!feeRespObj.getRespCode().equalsIgnoreCase(
                        Constants.SUCCESS_CODE)) {
                    // rollback the work
                    con.rollback();
                    return feeRespObj;
                } // end if
                respObj.setFeeAmount(feeRespObj.getFeeAmount());
                respObj.setCardBalance(feeRespObj.getCardBalance());
            } else { // end if
                // get the latest balance
                String balance = cardsServiceHome
                        .getValue("select card_balance from card_funds where card_no='"
                                + requestObj.getCardNo() + "'");
                // set the balance in response
                respObj.setCardBalance(balance);
            } // end else

            respObj.setTransId(transIds[0]);
            respObj.setRespCode(Constants.SUCCESS_CODE);
            respObj.setRespDesc(Constants.SUCCESS_MSG);
            return respObj;
            // validate the card information
        } catch(Exception exp) {
            String trc = CommonUtilities.getStackTrace(exp);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Exception -- > " + exp.getMessage() + "Stack Trace -- > "
                            + trc);
            try {
                if(con != null)
                    con.rollback();
            } catch(Exception ex) {
            } // end try
            // set the response code to system error
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            // respObj.setSysDesc("System Error, Check logs for more
            // information...");
            respObj.setExcepMsg(exp.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Going to log the error transaction::Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());
            // log the transaction
            String[] transIds = cardsServiceHome.logTransaction(requestObj
                    .getCardNo(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), null, "0200", "0", respObj.getRespDesc(),
                    "0", respObj.getRespCode(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(), requestObj
                            .getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_FINEST),
                    "Transaction logged with iso serial no -- > " + transIds[0]
                            + " && trace audit no -- > " + transIds[1]);
            // set the trans id in response
            respObj.setTransId(transIds[0]);
            return respObj;
        }
    }




    public ServicesResponseObj defineBillPaymentAlert(
            ServicesRequestObj requestObj) {
        ServicesResponseObj respObj = new ServicesResponseObj();
        CardsServiceHome cardsServiceHome = null;

        try {
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for defining alert for bill payment request--- Bill payment Transaction ID--->"
                                    + requestObj
                                            .getBillPaymentTransactionSerial()
                                    + "<---Card No--->"
                                    + requestObj.getCardNo()
                                    + "<---Alert Channel Address--->"
                                    + requestObj.getAlertChannelAddress()
                                    + "<---Alert Channel Id--->"
                                    + requestObj.getAlertChannelId()
                                    + "<---Alert Provider Id--->"
                                    + requestObj.getAlertProviderId()
                                    + "<---Alert Provider Id--->"
                                    + requestObj.getBillPaymentAlertType());
            if(requestObj.getBillPaymentTransactionSerial() == null
                    || requestObj.getBillPaymentTransactionSerial().trim()
                            .length() == 0
                    || requestObj.getAlertChannelId() == null
                    || requestObj.getAlertChannelId().trim().length() == 0
                    || requestObj.getBillPaymentAlertType() == null
                    || requestObj.getBillPaymentAlertType().trim().length() == 0) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for defining alert for bill payment request--- Mandatory fields required for alerts definition missing");
                respObj.setRespCode("12");
                respObj
                        .setRespDesc("Mandatory fields required for alerts definition missing");
                return respObj;
            }
            cardsServiceHome = CardsServiceHome.getInstance(con);
            String userId = cardsServiceHome.getCardUserID(requestObj
                    .getCardNo());
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for defining alert for bill payment request--- User ID found for card--->"
                                    + userId);
            if(userId == null || userId.trim().length() == 0) {
                requestObj.setCardUserId(requestObj.getCardNo());
            } else {
                requestObj.setCardUserId(userId);
            }
            String amount = cardsServiceHome.getBillPaymentAmount(requestObj
                    .getBillPaymentTransactionSerial(), requestObj.getCardNo());
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for defining alert for bill payment request--- Got bill payment amount for provided BP Serial and card no--->"
                                    + amount);
            if(amount == null || amount.trim().length() == 0) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for defining alert for bill payment request--- Invalid amount got for provided bill pay serial...");
                respObj.setRespCode("12");
                respObj.setRespDesc("Invalid Bill Payment amount got");
                return respObj;
            } else {
                requestObj.setBillPaymentAmount(amount);
            }
            if(requestObj.getAlertChannelId().equalsIgnoreCase(
                    Constants.ALERT_CH_EMAIL)
                    && (requestObj.getAlertChannelAddress() == null || requestObj
                            .getAlertChannelAddress().trim().length() == 0)) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for defining alert for bill payment request--- Alert channel is email, no email address provided in request, getting email against provided card...");
                String email = cardsServiceHome.getUserProfileInfo(requestObj
                        .getCardNo(), Constants.ALERT_CH_ADD_EMAIL);
                if(email == null || email.trim().length() == 0) {
                    CommonUtilities
                            .getLogger()
                            .log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Method for defining alert for bill payment request--- No email address exist for provided card number...");
                    respObj.setRespCode("12");
                    respObj
                            .setRespDesc("Unable to define alert,Alert Channel Address missing for Email channel");
                    return respObj;
                } else {
                    requestObj.setAlertChannelAddress(email);
                }
            } else if(requestObj.getAlertChannelId().equalsIgnoreCase(
                    Constants.ALERT_CH_SMS)
                    && (requestObj.getAlertChannelAddress() == null || requestObj
                            .getAlertChannelAddress().trim().length() == 0)) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for defining alert for bill payment request--- Alert channel is SMS, no cell number provided in request...");
                respObj.setRespCode("12");
                respObj
                        .setRespDesc("Mandatory fields required for alerts definition missing");
                return respObj;
            }
            long alertSerial = cardsServiceHome
                    .insertAlertUserRecord(requestObj);
            if(alertSerial > 0) {
                requestObj
                        .setBillPaymentAlertUserNo(Long.toString(alertSerial));
                cardsServiceHome.insertAlertChannelRecord(requestObj);
                cardsServiceHome.updateBPRequestAlertInfo(requestObj);
                respObj.setRespCode("00");
                respObj.setRespDesc("OK");
                return respObj;
            } else {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for defining alert for bill payment request--- Alert channel is SMS, no cell number provided in request...");
                respObj.setRespCode("12");
                respObj
                        .setRespDesc("Mandatory fields required for alerts definition missing");
                return respObj;
            }
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in defining alert for bill payment request--->"
                            + ex);
            respObj.setRespCode("96");
            respObj
                    .setRespDesc("Unable to define alert for bill payment request--->"
                            + ex);
            return respObj;
        }
    }




    public ServicesResponseObj processEntitlementGeneration(
            ServicesRequestObj reqObj) {
        ServicesResponseObj respObj = new ServicesResponseObj();
        try {
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for processing entitlement generation --- Arguments got --- Load Serial--->"
                                    + reqObj.getEntitlementLoadSerial()
                                    + "<---Merchant Id--->"
                                    + reqObj.getCardAcceptorId()
                                    + "<---MCC--->"
                                    + reqObj.getMcc()
                                    + "<---Device Id--->"
                                    + reqObj.getDeviceId()
                                    + "<---Acquirer Id--->"
                                    + reqObj.getAcquirerId());
            CommonUtilities
                    .getLogger()
                    .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for processing entitlement generation --- Checking mandatory fields...");
            if(reqObj.getEntitlementLoadSerial() == null
                    || reqObj.getEntitlementLoadSerial().trim().length() == 0) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for processing entitlement generation --- Load Serial Missing, returning error response...");
                respObj.setRespCode("12");
                respObj
                        .setRespDesc("Unable to load entitlement, mandtory field missing (Load Serial No)");
                return respObj;
            }
            // if (reqObj.getEntitlementCardNoFrom() == null ||
            // reqObj.getEntitlementCardNoFrom().trim().length() == 0) {
            // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            // LOG_CONFIG),
            // "Method for processing entitlement generation --- Card No From,
            // returning error response...");
            // respObj.setRespCode("12");
            // respObj.setRespDesc(
            // "Unable to load entitlement, mandtory field missing (From Card
            // No)");
            // return respObj;
            // }
            // if (reqObj.getEntitlementCardNoTo() == null ||
            // reqObj.getEntitlementCardNoTo().trim().length() == 0) {
            // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            // LOG_CONFIG),
            // "Method for processing entitlement generation --- Card No To,
            // returning error response...");
            // respObj.setRespCode("12");
            // respObj.setRespDesc(
            // "Unable to load entitlement, mandtory field missing (To Card
            // No)");
            // return respObj;
            // }

            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for processing entitlement generation --- Calling API for generating entitlments...");

            CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);
            respObj = serviceHome.generateEntitlements(reqObj);

            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for processing entitlement generation --- Response Got --- Response Code--->"
                                    + respObj.getRespCode()
                                    + "<---Total Cards-->"
                                    + respObj.getTotalCards()
                                    + "<---Entitlement Cards-->"
                                    + respObj.getTotalEntitledCards());
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in processing of entitlement generation --->"
                            + ex);
            String trc = CommonUtilities.getStackTrace(ex);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Stack Trace -- > " + trc);
            try {
                if(con != null) {
                    con.rollback();
                }
            } catch(Exception exRoll) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_SEVERE),
                        "Exception in rolling back transaction--->" + exRoll);
            }
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            respObj.setExcepMsg(ex.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(ex));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);
        }
        return respObj;
    }




    public ServicesResponseObj processEntitlementRedemption(
            ServicesRequestObj reqObj) {
        ServicesResponseObj respObj = new ServicesResponseObj();
        try {
            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for processing entitlement redemption --- Arguments got --- Entitlement Serial--->"
                                    + reqObj.getEntitlementSerialNo()
                                    + "<---Merchant Id--->"
                                    + reqObj.getCardAcceptorId()
                                    + "<---MCC--->"
                                    + reqObj.getMcc()
                                    + "<---Device Id--->"
                                    + reqObj.getDeviceId()
                                    + "<---Acquirer Id--->"
                                    + reqObj.getAcquirerId());
            CommonUtilities
                    .getLogger()
                    .log(LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for processing entitlement redemption --- Checking mandatory fields...");
            if(reqObj.getEntitlementSerialNo() == null
                    || reqObj.getEntitlementSerialNo().trim().length() == 0) {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Method for processing entitlement redemption --- Load Serial Missing, returning error response...");
                respObj.setRespCode("12");
                respObj
                        .setRespDesc("Unable to redeem entitlement, mandtory field missing (Entitlement Serial No)");
                return respObj;
            }
            // if (reqObj.getEntitlementCardNoFrom() == null ||
            // reqObj.getEntitlementCardNoFrom().trim().length() == 0) {
            // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            // LOG_CONFIG),
            // "Method for processing entitlement generation --- Card No From,
            // returning error response...");
            // respObj.setRespCode("12");
            // respObj.setRespDesc(
            // "Unable to load entitlement, mandtory field missing (From Card
            // No)");
            // return respObj;
            // }
            // if (reqObj.getEntitlementCardNoTo() == null ||
            // reqObj.getEntitlementCardNoTo().trim().length() == 0) {
            // CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            // LOG_CONFIG),
            // "Method for processing entitlement generation --- Card No To,
            // returning error response...");
            // respObj.setRespCode("12");
            // respObj.setRespDesc(
            // "Unable to load entitlement, mandtory field missing (To Card
            // No)");
            // return respObj;
            // }

            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for processing entitlement redemption --- Calling API for redeeming entitlments...");

            CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);
            respObj = serviceHome.redeemEntitlements(reqObj);

            CommonUtilities
                    .getLogger()
                    .log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Method for processing entitlement redemption --- Response Got --- Response Code--->"
                                    + respObj.getRespCode());
        } catch(Exception ex) {
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "Exception in processing of entitlement redemption --->"
                            + ex);
            String trc = CommonUtilities.getStackTrace(ex);
            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_SEVERE),
                    "Stack Trace -- > " + trc);
            try {
                if(con != null) {
                    con.rollback();
                }
            } catch(Exception exRoll) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_SEVERE),
                        "Exception in rolling back transaction--->" + exRoll);
            }
            respObj.setRespCode("96");
            respObj.setRespDesc("System Error-->" + trc);
            respObj.setExcepMsg(ex.getMessage());
            respObj.setStkTrace(CommonUtilities.getStackTrace(ex));
            respObj.setLogFilePath("More information can be found at--->"
                    + Constants.EXACT_LOG_PATH);
        }
        return respObj;
    }

} // end CardsServiceHandler
