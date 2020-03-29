package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.handlers.CardsServiceHandler;
import com.i2c.services.home.CardsServiceHome;
import com.i2c.services.test.helper.DBConnectionManager;
import com.i2c.services.test.helper.InputReader;
import com.i2c.services.util.Constants;

import java.sql.Connection;
import java.util.ArrayList;

import junit.framework.TestCase;

public class BaseTestCase extends TestCase {

    protected Connection connection = null;
    protected ServicesHandler handler = null;
    protected InputReader reader = null;
    protected ArrayList<ServicesRequestObj> requestObjList = null;




    protected void setUp() {

        try {
            reader = new InputReader("/config.properties");
            connection = DBConnectionManager.getConnection(reader
                    .getDbInformation());

            handler = ServicesHandler.getInstance(connection, reader
                    .getLogPath());
            requestObjList = reader.getServicesRequestObjList();

        } catch(Exception exp) {
            exp.printStackTrace();
        }
    }




    public void validateSwitch(SwitchInfoObj switchInfo,
            ServicesRequestObj requestObj, ServicesResponseObj respObj,
            boolean batchModeAllowed) {

        if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) {
            assertEquals(
                    "BaseTestCase::Switch is inactive Response Code should be 91",
                    respObj.getRespCode(), "91");
            assertNotNull(
                    "BaseTestCase::Switch is inactive, Transaction should be logged",
                    respObj.getTransId());

            requestObj.setAmount("0");
            requestObj.setRetreivalRefNum(null);

            validateTransaction(requestObj, respObj, "0200");
        } else if(switchInfo.getSwitchId() != null
                && !switchInfo.getSwitchId().trim().equals("")
                && switchInfo.isBatchTransAllowed()) {

            if(!batchModeAllowed) {
                assertEquals(
                        "BaseTestCase::Switch is batch mode Response Code should be 40",
                        respObj.getRespCode(), "40");
            }
        }

    }




    public void validateCardBalance(ServicesRequestObj requestObj,
            ServicesResponseObj respObj) {

        try {
            String cardBalance = CardsServiceHome.getInstance(connection)
                    .getValue(
                            "select card_balance from card_funds where card_no='"
                                    + requestObj.getCardNo() + "'");
            // assert
            assertEquals(
                    "TestGetCardStatus::Invalid card balance returned in response",
                    cardBalance, respObj.getCardBalance());

        } catch(Exception exp) {
            exp.printStackTrace();
        }
    }




    /**
     * @param requestObj
     * @param respObj
     * @param msgType
     */
    protected void validateTransaction(ServicesRequestObj requestObj,
            ServicesResponseObj respObj, String msgType) {

        try {
            CardsServiceHome home = CardsServiceHome.getInstance(connection);

            String traceAuditNo = home
                    .getValue("select trace_audit_no from trans_requests where iso_serial_no="
                            + respObj.getTransId());
            // assert
            assertNotNull("Transaction is not logged in trans_requests",
                    traceAuditNo);

            String cardNo = home
                    .getValue("select card_no from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Card No does not match in trans_requests", cardNo,
                    requestObj.getCardNo());

            String serviceId = home
                    .getValue("select service_id from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Service Id does not match in trans_requests",
                    serviceId, requestObj.getServiceId());

            String deviceType = home
                    .getValue("select device_type from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Device Type does not match in trans_requests",
                    deviceType, requestObj.getDeviceType());

            String retRefNo = home
                    .getValue("select ret_ref_no from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Ret Ref No does not match in trans_requests",
                    retRefNo, requestObj.getRetreivalRefNum());

            String mType = home
                    .getValue("select msg_type from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Message Type does not match in trans_requests",
                    mType, msgType);

            String description = home
                    .getValue("select trans_desc from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Trans Desc does not match in trans_requests",
                    description, requestObj.getDescription());

            String respCode = home
                    .getValue("select resp_code from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Response Code does not match in trans_requests",
                    respCode, respObj.getRespCode());

            String deviceId = home
                    .getValue("select device_id from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Device Id does not match in trans_requests",
                    deviceId, requestObj.getDeviceId());

            String cardAcceptorCd = home
                    .getValue("select card_acceptor_cd from trans_requets where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Card Acceptor code does not match in trans_requests",
                    cardAcceptorCd, requestObj.getCardAcceptorId());

            String cardAccNameAndLoc = home
                    .getValue("select card_acc_Name_loc from trans_requests where  trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals(
                    "Card Acceptor Name and Location does not match in trans_requests",
                    cardAccNameAndLoc, requestObj.getCardAcceptNameAndLoc());

            String mcc = home
                    .getValue("select mcc from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("MCC does not match in trans_requests", mcc,
                    requestObj.getMcc());

            String accountNo = home
                    .getValue("select account_no from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Account No does not match in trans_requests",
                    accountNo, requestObj.getAccountNo());

            String acquirerId = home
                    .getValue("select acquirer_id from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Acquirer Id does not match in trans_requests",
                    acquirerId, requestObj.getAcquirerId());

            if(requestObj.getApplyFee() != null
                    && requestObj.getApplyFee().equalsIgnoreCase("Y")) {

                String feeTraceAudit = home
                        .getValue("select fee_trace_audit from trans_requests where trace_audit_no="
                                + traceAuditNo);

                respObj.setTransId(feeTraceAudit);

                validateFeeTransaction(requestObj, respObj, msgType);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }




    protected void validateFeeTransaction(ServicesRequestObj requestObj,
            ServicesResponseObj respObj, String msgType) {

        try {
            CardsServiceHome home = CardsServiceHome.getInstance(connection);

            String traceAuditNo = respObj.getTransId();

            String cardNo = home
                    .getValue("select card_no from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Card No does not match in trans_requests", cardNo,
                    requestObj.getCardNo());

            String serviceId = home
                    .getValue("select service_id from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Service Id does not match in trans_requests",
                    serviceId, requestObj.getServiceId());

            String deviceType = home
                    .getValue("select device_type from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Device Type does not match in trans_requests",
                    deviceType, requestObj.getDeviceType());

            String retRefNo = home
                    .getValue("select ret_ref_no from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Ret Ref No does not match in trans_requests",
                    retRefNo, requestObj.getRetreivalRefNum());

            String mType = home
                    .getValue("select msg_type from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Message Type does not match in trans_requests",
                    mType, msgType);

            String description = home
                    .getValue("select trans_desc from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Trans Desc does not match in trans_requests",
                    description, requestObj.getDescription());

            String respCode = home
                    .getValue("select resp_code from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Response Code does not match in trans_requests",
                    respCode, respObj.getRespCode());

            String deviceId = home
                    .getValue("select device_id from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Device Id does not match in trans_requests",
                    deviceId, requestObj.getDeviceId());

            String cardAcceptorCd = home
                    .getValue("select card_acceptor_cd from trans_requets where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Card Acceptor code does not match in trans_requests",
                    cardAcceptorCd, requestObj.getCardAcceptorId());

            String cardAccNameAndLoc = home
                    .getValue("select card_acc_Name_loc from trans_requests where  trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals(
                    "Card Acceptor Name and Location does not match in trans_requests",
                    cardAccNameAndLoc, requestObj.getCardAcceptNameAndLoc());

            String mcc = home
                    .getValue("select mcc from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("MCC does not match in trans_requests", mcc,
                    requestObj.getMcc());

            String accountNo = home
                    .getValue("select account_no from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Account No does not match in trans_requests",
                    accountNo, requestObj.getAccountNo());

            String acquirerId = home
                    .getValue("select acquirer_id from trans_requests where trace_audit_no="
                            + traceAuditNo);
            // assert
            assertEquals("Acquirer Id does not match in trans_requests",
                    acquirerId, requestObj.getAcquirerId());

        } catch(Exception e) {
            e.printStackTrace();
        }
    }




    protected void validateCreateAchAccount(ServicesRequestObj requestObj,
            ServicesResponseObj respObj) {
        try {
            CardsServiceHome home = CardsServiceHome.getInstance(connection);

            ServicesResponseObj vresp = CardsServiceHome
                    .getInstance(connection).isCardInfoValid(
                            requestObj.getCardNo(), requestObj.getAAC(),
                            requestObj.getExpiryDate(),
                            requestObj.getAccountNo(), requestObj.getPin(),
                            requestObj.getServiceId(),
                            requestObj.getDeviceType(),
                            requestObj.getDeviceId(),
                            requestObj.getCardAcceptorId(),
                            requestObj.getCardAcceptNameAndLoc(),
                            requestObj.getMcc(), requestObj.getAcquirerId());

            if(vresp.getRespCode() != null
                    && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals(
                        "BaseTestCase::Response Code does not match. It should be "
                                + vresp.getRespCode(), vresp.getRespCode(),
                        respObj);
                return;
            }

            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj
                    .getCardNo());

            validateSwitch(switchInfo, requestObj, respObj, false);

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())
                return;

            vresp = CardsServiceHandler.getInstance(connection).validateCard(
                    requestObj.getCardNo(), requestObj.getServiceId(),
                    requestObj.getApplyFee(), null, requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());
            // validate the response
            if(vresp.getRespCode() != null
                    && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals(
                        "BaseTestCase::Response Code does not match. It should be "
                                + vresp.getRespCode(), vresp.getRespCode(),
                        respObj);
                return;
            }

            if(requestObj.getBankRoutingNo() != null
                    && requestObj.getBankRoutingNo().trim().length() > 0) {
                try {
                    String routingNo = requestObj.getBankRoutingNo().substring(
                            0, requestObj.getBankRoutingNo().length() - 1);
                    String checkDegit = requestObj.getBankRoutingNo()
                            .substring(
                                    requestObj.getBankRoutingNo().length() - 1,
                                    requestObj.getBankRoutingNo().length());
                    Integer retChkDegit = computeCheckDigit(routingNo);

                    if(checkDegit != null
                            && checkDegit.equals(retChkDegit.toString())) {

                    } else {

                        assertEquals(
                                "BaseTestCase:: Routing No is invalid. Response should be RI",
                                "RI", respObj.getRespCode());

                        requestObj.setRetreivalRefNum(null);
                        requestObj.setAmount("0");
                        validateTransaction(requestObj, respObj, "0200");

                        return;
                    }
                } catch(Exception ex1) {

                    assertEquals(
                            "BaseTestCase:: Routing No is invalid. Response should be RI",
                            "RI", respObj.getRespCode());

                    requestObj.setRetreivalRefNum(null);
                    requestObj.setAmount("0");
                    validateTransaction(requestObj, respObj, "0200");

                    return;
                }
            } else {
                assertEquals(
                        "BaseTestCase:: Routing No is invalid. Response should be RI",
                        "RI", respObj.getRespCode());

                requestObj.setRetreivalRefNum(null);
                requestObj.setAmount("0");
                validateTransaction(requestObj, respObj, "0200");

                return;
            }

            // get the user id
            String userId = home
                    .getValue("select user_id from cards where card_no='"
                            + requestObj.getCardNo() + "'");

            if(userId == null || userId.trim().length() == 0) {

                respObj.setRespCode("UN");
                respObj
                        .setRespDesc("Invalid User ID got while creating ACH Account");

                assertEquals(
                        "BaseTestCase:: Routing No is invalid. Response should be RI",
                        "RI", respObj.getRespCode());

                requestObj.setRetreivalRefNum(null);
                requestObj.setAmount("0");
                validateTransaction(requestObj, respObj, "0200");

                return;
            }

            requestObj.setAmount("0.0");
            requestObj.setRetreivalRefNum(null);

            validateTransaction(requestObj, respObj, "0200");

            validateCardBalance(requestObj, respObj);

        } catch(Exception exp) {
            exp.printStackTrace();
        }
    }




    private Integer computeCheckDigit(String routingno) {

        if(routingno.trim().length() != 8) {
            throw new IllegalArgumentException(
                    "Routing No length should be equal to 8 digits");
        }

        char[] array = routingno.toCharArray();
        int sum = 0;
        int weights[] = { 3, 7, 1, 3, 7, 1, 3, 7 };
        int checkDigit = 0;
        for(int i = 0; i < array.length; i++) {
            int digit = Character.getNumericValue(array[i]);
            sum += digit * weights[i];
        }

        for(int j = 10;;) {
            if(j > sum || j == sum) {
                checkDigit = j - sum;
                break;
            } else {
                j += 10;
            }
        }
        return new Integer(checkDigit);
    }




    protected void tearDown() {
        try {
            DBConnectionManager.closeConnection(connection);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
