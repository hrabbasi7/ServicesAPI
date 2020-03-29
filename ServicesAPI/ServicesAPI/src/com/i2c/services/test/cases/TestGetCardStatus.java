package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.handlers.CardsServiceHandler;
import com.i2c.services.home.CardsServiceHome;
import com.i2c.services.util.Constants;

public class TestGetCardStatus extends BaseTestCase {

    private CardsServiceHome home = null;
    private ServicesResponseObj respObj = null;



    public void setUp() {
        super.setUp();
        home = CardsServiceHome.getInstance(connection);
    }




    public void testGetCardStatus() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }




    private void performTest(ServicesRequestObj requestObj) {

        try {
            
            respObj = ServicesHandler.getInstance(connection, reader.getLogPath()).getCardStatus(requestObj);
            
            String cardPrgId = home.getCardProgramID(requestObj.getCardNo());

            assertNotNull("TestGetCardStatus::Card Program Id is null against Card No("
                    + requestObj.getCardNo() + ")", cardPrgId);

            String serviceId = home.huntForServiceId(
                    requestObj.getDeviceType(), "S1", "00", cardPrgId);

            if(serviceId != null)
                assertEquals("TestGetCardStatus::Invalid Service Id Applied", serviceId, requestObj.getServiceId());
            else
                assertEquals("TestGetCardStatus::Invalid Service Id Applied", Constants.GET_CARD_STATUS_SERVICE, requestObj.getServiceId());
            
            ServicesResponseObj vresp = CardsServiceHandler.getInstance(
                    connection).validateCard(requestObj.getCardNo(),
                    requestObj.getServiceId(), requestObj.getApplyFee(),
                    "FGHIRBACDE", requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());
            
            
            if (vresp.getRespCode() != null && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals("TestGetCardStatus::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj.getRespCode());
                return;
            }
            
            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
            
            super.validateSwitch(switchInfo, requestObj, respObj, false);
            
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) 
                return;
            
            
            vresp= home.isCardInfoValid(requestObj.getCardNo(),
                    requestObj.getAAC(), requestObj.getExpiryDate(), requestObj
                            .getAccountNo(), requestObj.getPin(), requestObj
                            .getServiceId(), requestObj.getDeviceType(),
                    requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAcquirerId());
            
            
            
            if (vresp.getRespCode() != null && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals("TestGetCardStatus::Response code does not match. It should be " + vresp.getRespCode(), vresp.getRespCode(), respObj.getRespCode());
                
                return;
            }
            
            
            requestObj.setAmount("0.0");
            requestObj.setRetreivalRefNum(null);
            
            super.validateTransaction(requestObj, respObj, "0200");
            
            String cardStatus = home.getValue("select card_status_pos from cards where card_no='" + requestObj.getCardNo() + "'");
            //assert
            assertEquals("TestGetCardStatus::Invalid Card Status POS returned in response", cardStatus, respObj.getCardStatusCode());
            
            String cardStatusDesc = home.getValue("select card_status_desc from card_statuses where card_status='" + cardStatus + "'");
            //assert
            assertEquals("TestGetCardStatus::Invalid card status description returned in response", cardStatusDesc, respObj.getCardStatusDesc());
            
            super.validateCardBalance(requestObj, respObj);
            
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
