package com.i2c.services.test.cases;

import com.i2c.services.CardInfoObj;
import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.handlers.CardsServiceHandler;
import com.i2c.services.home.CardsServiceHome;
import com.i2c.services.util.CommonUtilities;
import com.i2c.services.util.Constants;
import com.i2c.services.util.LogLevel;

public class TestActivateCard extends BaseTestCase {
    private CardsServiceHome home = null;




    public void setUp() {

        super.setUp();
        home = CardsServiceHome.getInstance(connection);

    }




    public void testActivateCard() {

        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }




    public void performTest(ServicesRequestObj requestObj) {

        try {

            ServicesResponseObj respObj = ServicesHandler.getInstance(
                    connection, reader.getLogPath()).activateCard(requestObj);

            if(requestObj.getAmount() != null
                    && requestObj.getAmount().length() > 0) {
                try {

                    double amount = Double.parseDouble(requestObj.getAmount());

                    if(amount < 0) {
                        // assert
                        assertEquals("Response code is not 13 but Amount("
                                + amount + ") is invalid", respObj
                                .getRespCode(), "13");
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    // assert
                    assertEquals("Response code is not 13 but Amount("
                            + requestObj.getAmount() + ") is invalid", respObj
                            .getRespCode(), "13");
                }
            }// end if

            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj
                    .getCardNo());

            super.validateSwitch(switchInfo, requestObj, respObj, true);

            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive())
                return;

            ServicesResponseObj vresp = home.isCardInfoValid(requestObj
                    .getCardNo(), requestObj.getAAC(), requestObj
                    .getExpiryDate(), requestObj.getAccountNo(), requestObj
                    .getPin(), true, requestObj.getServiceId(), requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAcquirerId(), requestObj
                            .getSsn(), requestObj.getHomePhone(), requestObj
                            .getDob(), requestObj.getZipCode(), requestObj
                            .getSecurityCode(), requestObj
                            .getDrivingLicesneNo(), requestObj
                            .getDrivingLicesneState());

            if(vresp.getRespCode() != null
                    && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals(
                        "TestActivateCard::Response code does not match. It should be "
                                + vresp.getRespCode(), vresp.getRespCode(),
                        respObj.getRespCode());

                return;
            }

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                return;
            }
            
            String validCardStatus = null;

            if(requestObj.getDeviceType() != null
                    && (requestObj.getDeviceType().equalsIgnoreCase(
                            Constants.DEVICE_TYPE_CS) || requestObj
                            .getDeviceType().equalsIgnoreCase(
                                    Constants.DEVICE_TYPE_WS))) {
                validCardStatus = "AI";
            } else {
                validCardStatus = "A";
            }

            vresp = CardsServiceHandler.getInstance(connection).validateCard(
                    requestObj.getCardNo(), requestObj.getServiceId(),
                    requestObj.getApplyFee(), validCardStatus,
                    requestObj.getDeviceType(), requestObj.getDeviceId(),
                    requestObj.getCardAcceptorId(),
                    requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                    requestObj.getAccountNo(), requestObj.getAcquirerId());
            
            if(vresp.getRespCode() != null
                    && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals(
                        "TestActivateCard::Response code does not match. It should be "
                                + vresp.getRespCode(), vresp.getRespCode(),
                        respObj.getRespCode());

                return;
            }
            

            CardInfoObj cardInfo = home.getCardInfo(requestObj.getCardNo());

            boolean[] cardPrgRegValues = home.checkIsBrandedCardProgram(cardInfo.getCardPrgId());
            int cardOfacAvsStatus = home.isExistingOFAC_AVSValid(cardInfo.getOfacStatus(),cardInfo.getAvsStatus());
            
            if(cardPrgRegValues != null && cardPrgRegValues.length > 0) {
                if(cardPrgRegValues[0] && cardOfacAvsStatus != Constants.OFAC_AVS_GOOD) {
                    if(cardOfacAvsStatus == Constants.OFAC_FAILED){
                        
                        assertEquals(
                                "TestActivateCard::Response code does not match. It should be OF", "OF", respObj.getRespCode());
                    }else if(cardOfacAvsStatus == Constants.AVS_FAILED){
                        assertEquals(
                                "TestActivateCard::Response code does not match. It should be AV", "AV", respObj.getRespCode());
                    }
                    
                    requestObj.setAmount("0");
                    requestObj.setRetreivalRefNum(null);
                    super.validateTransaction(requestObj, respObj, "0200");
                    return;
                    
                } else if(!cardPrgRegValues[0] && !cardPrgRegValues[1]
                        && cardOfacAvsStatus != Constants.OFAC_AVS_GOOD) {
                    if(cardOfacAvsStatus == Constants.OFAC_FAILED){
                        assertEquals(
                                "TestActivateCard::Response code does not match. It should be OF", "OF", respObj.getRespCode());
                    }else if(cardOfacAvsStatus == Constants.AVS_FAILED){
                        assertEquals(
                                "TestActivateCard::Response code does not match. It should be AV", "AV", respObj.getRespCode());
                    }
                    requestObj.setAmount("0");
                    requestObj.setRetreivalRefNum(null);
                    super.validateTransaction(requestObj, respObj, "0200");
                    return;
                }
                
                String currDate = CommonUtilities.getCurrentFormatDate(Constants.DATE_FORMAT);
                long diffInMonths = CommonUtilities.getDiffInDates(currDate,cardInfo.getExpiryOn(),Constants.DATE_FORMAT,Constants.DIFF_IN_MONTHS);
                boolean paramAllowed = home.isParameterAllowed(Constants.REM_EXP_MONTHS_BFR_ACT_PARAM,cardInfo.getCardPrgId(),Long.toString(diffInMonths));
                if(paramAllowed == false){
                    assertEquals(
                            "TestActivateCard::Response code does not match. It should be AD", "AD", respObj.getRespCode());
                    
                    requestObj.setAmount("0");
                    requestObj.setRetreivalRefNum(null);
                    super.validateTransaction(requestObj, respObj, "0200");
                    return;
                }
                
                if (cardInfo.getCardStatusAtm().equals(Constants.PRE_ACTIVE_CARD) &&
                        cardInfo.getCardStatusPos().equals(Constants.PRE_ACTIVE_CARD)) {
                        String initAmt = home.getInitialBatchLoadAmount(
                                cardInfo.getCardBatchNo());
                        if (initAmt != null && initAmt.trim().length() > 0) {
                            double initAmount = 0;
                            try {
                                initAmount = Double.parseDouble(initAmt);
                                if (requestObj.getAmount() != null &&
                                    requestObj.getAmount().trim().length() > 0) {
                                    assertEquals(
                                            "TestActivateCard::Response code does not match. It should be FI", "FI", respObj.getRespCode());
                                    
                                    requestObj.setAmount("0");
                                    requestObj.setRetreivalRefNum(null);
                                    super.validateTransaction(requestObj, respObj, "0200");
                                    return;
                                }
                                
                                assertEquals("TestActivateCard:: Amount does not match", Double.toString(initAmount), requestObj.getAmount());
                                assertEquals("TestActivateCard::Service Id does not match", Constants.ACTIVE_AND_LOAD_CARD_SERVICE, requestObj.getServiceId());
                            } catch (NumberFormatException amtEx) {
                                amtEx.printStackTrace();
                            }
                            
                            }
                        }
                    }
            
            super.validateCardBalance(requestObj, respObj);
        } catch(Exception exp) {
            exp.printStackTrace();
        }
    }

}
