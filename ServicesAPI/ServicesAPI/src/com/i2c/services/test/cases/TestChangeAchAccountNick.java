package com.i2c.services.test.cases;

import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.handlers.CardsServiceHandler;
import com.i2c.services.home.ACHServiceHome;
import com.i2c.services.home.CardsServiceHome;
import com.i2c.services.util.Constants;

public class TestChangeAchAccountNick extends BaseTestCase{
    
    ACHServiceHome home = null;
    ServicesResponseObj respObj = null;
    
    public void setUp() {
        super.setUp();
        home = ACHServiceHome.getInstance(connection);
    }
    
    public void testChangeAchAccountNick() {
        
        for (ServicesRequestObj requestObj: requestObjList) {
            
        }
    }
    
    public void performTest(ServicesRequestObj requestObj) {
        
        try {
            String cardPrgId = home.getCardProgramID(requestObj.getCardNo());
            String serviceId = home.huntForServiceId(requestObj.getDeviceType(), "AB", "00", cardPrgId);
            
            if(serviceId != null)
                assertEquals("TestChangeAchAccountNick::Invalid Service Id Applied", serviceId, requestObj.getServiceId());
            else
                assertEquals("TestChangeAchAccountNick::Invalid Service Id Applied", Constants.GET_CARD_STATUS_SERVICE, requestObj.getServiceId());
            
            ServicesResponseObj vresp = CardsServiceHome.getInstance(connection).isCardInfoValid(requestObj.getCardNo(),requestObj.getAAC(),requestObj.getExpiryDate(),requestObj.getAccountNo(),requestObj.getPin(), requestObj.getServiceId(), requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAcquirerId());

            if (vresp.getRespCode() != null && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals("TestChangeAchAccountNick::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj);
                return;
            }
            
            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
            
            super.validateSwitch(switchInfo, requestObj, respObj, false);
            
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) 
                return;

            vresp = CardsServiceHandler.getInstance(connection).validateCard(requestObj.getCardNo(),requestObj.getServiceId(), requestObj.getApplyFee(), null,requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
            
            if (vresp.getRespCode() != null && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals("TestChangeAchAccountNick::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj);
                return;
            }
            
            String acctQry ="select ach_acct_sr_no from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "";
            //execute the query and get the result
            String acctNo = home.getValue(acctQry);
            
            //validate the acctNo
            if (acctNo == null) {

                assertEquals("TestChangeAchAccountInfo::Account Serial No not found in ach_accounts, response should be 12", "12", respObj);
                
                requestObj.setRetreivalRefNum(null);
                requestObj.setAmount("0");
                super.validateTransaction(requestObj, respObj, "0200");
                
                return;
            } //end if
            
            requestObj.setRetreivalRefNum(null);
            requestObj.setAmount("0.0");
            super.validateTransaction(requestObj, respObj, "0200");
            
            super.validateCardBalance(requestObj, respObj);

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
