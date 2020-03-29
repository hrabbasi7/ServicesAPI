package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.handlers.CardsServiceHandler;
import com.i2c.services.home.CardsServiceHome;
import com.i2c.services.util.Constants;

public class TestGetCardHolderAccounts extends BaseTestCase {

    private CardsServiceHome home = null;
    private ServicesResponseObj respObj = null;




    public void setUp() {
        super.setUp();
        home = CardsServiceHome.getInstance(connection);
    }




    public void testGetCardHolderAccounts() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }




    public void performTest(ServicesRequestObj requestObj) {

        try {
            respObj = ServicesHandler.getInstance(connection,
                    reader.getLogPath()).getCardHolderAccounts(requestObj);

            String cardPrgId = home.getCardProgramID(requestObj.getCardNo());

            assertNotNull(
                    "TestGetCardHolderAccounts::Card Program Id is null against Card No("
                            + requestObj.getCardNo() + ")", cardPrgId);

            String serviceId = home.huntForServiceId(
                    requestObj.getDeviceType(), "A4", "00", cardPrgId);

            if(serviceId != null)
                assertEquals("TestGetCardHolderAccounts::Invalid Service Id Applied",
                        serviceId, requestObj.getServiceId());
            else
                assertEquals("TestGetCardHolderAccounts::Invalid Service Id Applied",
                        Constants.GET_CH_ACCOUNTS_SERVICE, requestObj
                                .getServiceId());

            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj
                    .getCardNo());

            super.validateSwitch(switchInfo, requestObj, respObj, false);

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())
                return;

            // validate the card information
            ServicesResponseObj vresp = home.isCardInfoValid(requestObj
                    .getCardNo(), requestObj.getAAC(), requestObj
                    .getExpiryDate(), requestObj.getAccountNo(), requestObj
                    .getPin(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAcquirerId());

            if(vresp.getRespCode() != null
                    && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals(
                        "TestGetCardHolderAccounts::Response code does not match. It should be "
                                + vresp.getRespCode(), vresp.getRespCode(),
                        respObj.getRespCode());

                return;
            }

            // validate the card
            vresp = CardsServiceHandler.getInstance(connection).validateCard(
                    requestObj.getCardNo(), requestObj.getServiceId(),
                    requestObj.getApplyFee(), null, requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());

            if(vresp.getRespCode() != null
                    && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals(
                        "TestGetCardHolderAccounts::Response code does not match. It should be "
                                + vresp.getRespCode(), vresp.getRespCode(),
                        respObj.getRespCode());

                return;
            }

            requestObj.setAmount("0.0");
            requestObj.setRetreivalRefNum(null);

            super.validateTransaction(requestObj, respObj, "0200");

            super.validateCardBalance(requestObj, respObj);

        } catch(Exception exp) {
            exp.printStackTrace();
        }
    }
}
