package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.handlers.CardsServiceHandler;
import com.i2c.services.home.CardsServiceHome;
import com.i2c.services.util.CommonUtilities;
import com.i2c.services.util.Constants;
import com.i2c.services.util.LogLevel;

public class TestAllCashOut extends BaseTestCase {

    private CardsServiceHome home = null;
    private ServicesResponseObj respObj = null;




    public void setUp() {
        super.setUp();
        home = CardsServiceHome.getInstance(connection);
    }




    public void testAllCashOut() {

        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }

    }




    public void performTest(ServicesRequestObj requestObj) {

        try {
            ServicesHandler handler = ServicesHandler.getInstance(connection,
                    reader.getLogPath());
            
            String cashOutAmount = "";
            String cardBalance = home.getValue("select card_balance from card_funds where card_no='" +requestObj.getCardNo() + "'");
            
            //calculate the service fee
            ServicesResponseObj feeResp = home.getServiceFee(
                requestObj.getCardNo(), "0.0", requestObj.getServiceId(),
                requestObj.getDeviceType(), requestObj.getDeviceId(),
                requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                requestObj.getMcc(), requestObj.getAccountNo(),
                requestObj.getAcquirerId());
            
            
            if (feeResp.getRespCode() != null && feeResp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                
                double requestedAmount = Double.parseDouble(cardBalance);
                double feeAmount = Double.parseDouble(feeResp.getFeeAmount());
                //get the cashout amount
                cashOutAmount = (requestedAmount - feeAmount) + "";
            }
            
            respObj = handler.allCashOut(requestObj);

            String cardPrgId = home.getCardProgramID(requestObj.getCardNo());

            String serviceId = home.huntForServiceId(
                    requestObj.getDeviceType(), "C1", "00", cardPrgId);

            if(serviceId != null && !serviceId.equals("")
                    && serviceId.trim().length() > 0) {

                // assert
                assertEquals("TestAllCashOut::Invalid Service Id applied", serviceId,
                        requestObj.getServiceId());
            } else {
                // assert
                assertEquals("TestAllCashOut::Invalid Service Id applied",
                        Constants.ALL_CASH_OUT_SERVICE, requestObj
                                .getServiceId());
            }

            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj
                    .getCardNo());
            // validate switch information
            super.validateSwitch(switchInfo, requestObj, respObj, true);

            if(switchInfo.getSwitchId() != null
                    && !switchInfo.getSwitchId().trim().equals("")
                    && switchInfo.isBatchTransAllowed()) {
                return;
            }

            // validate card information
            ServicesResponseObj vresp = home.isCardInfoValid(requestObj
                    .getCardNo(), requestObj.getAAC(), requestObj
                    .getExpiryDate(), requestObj.getAccountNo(), requestObj
                    .getPin(), requestObj.getServiceId(), requestObj
                    .getDeviceType(), requestObj.getDeviceId(), requestObj
                    .getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                    requestObj.getMcc(), requestObj.getAcquirerId());

            CommonUtilities.getLogger().log(
                    LogLevel.getLevel(Constants.LOG_CONFIG),
                    "After Validation Response Got :: Resp Code -- > "
                            + respObj.getRespCode() + " && Desc -- > "
                            + respObj.getRespDesc());

            if(!vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals(
                        "TestAllCashOut::Response Code does not match. It should be "
                                + vresp.getRespCode(), vresp.getRespCode(),
                        respObj);
                return;
            }
            
            
            //check for the card balance
            vresp = CardsServiceHandler.getInstance(connection).validateCard(requestObj.
                getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(),
                "GHIRBACDE", requestObj.getDeviceType(), requestObj.getDeviceId(),
                requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                requestObj.getMcc(), requestObj.getAccountNo(),
                requestObj.getAcquirerId());

            if(!vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals(
                        "TestAllCashOut::Response Code does not match. It should be "
                                + vresp.getRespCode(), vresp.getRespCode(),
                        respObj);
                return;
            }
            
            if (feeResp.getRespCode() != null &&
                    !feeResp.getRespCode().trim().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
                
                
                assertEquals(
                        "TestAllCashOut::Response Code does not match. It should be "
                                + feeResp.getRespCode(), feeResp.getRespCode(),
                        respObj);
                requestObj.setRetreivalRefNum(null);
                requestObj.setAmount("0");
                
                super.validateTransaction(requestObj, respObj, "0200");
                
                return; 
                } //end if
            
            
            assertEquals("TestAllCashOut::All Cash out Amount is invalid", cashOutAmount, respObj.getCashOutAmount());
            
            requestObj.setRetreivalRefNum(null);
            requestObj.setAmount("-" + cashOutAmount);
            super.validateTransaction(requestObj, respObj, "0200");
            
            assertEquals("TestAllCashOut::Response Code does not match. It should be 00", "00", respObj.getRespCode());
            
            super.validateCardBalance(requestObj, respObj);
            
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
