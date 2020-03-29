package com.i2c.services.test.cases;

import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.home.VasServiceHome;
import com.i2c.services.util.Constants;

public class TestVasPreAuth extends BaseTestCase{
    
    private VasServiceHome home = null;
    private ServicesResponseObj respObj = null;



    public void setUp() {
        super.setUp();
        home = VasServiceHome.getInstance(connection);
    }




    public void testGetCardStatus() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }
    
    
    public void performTest(ServicesRequestObj requestObj) {
        try {
            if (requestObj.getAmount() != null &&
                    requestObj.getAmount().trim().length() > 0) {
                  try {
                    double amt = Double.parseDouble(requestObj.getAmount().trim());
                    if (amt < 0) {
                        assertEquals("TestVasPreAuth::Invalid Amount Supplied. Response should be 13", "13", respObj.getRespCode());
                        assertEquals("TestVasPreAuth::Invalid Amount Supplied. Response Desc should be Invalid Amount", "Invalid Amount", respObj.getRespDesc());
                        return;
                    }
                  }
                  catch (NumberFormatException ex1) {
                      assertEquals("TestVasPreAuth::Invalid Amount Supplied. Response should be 13", "13", respObj.getRespCode());
                      assertEquals("TestVasPreAuth::Invalid Amount Supplied. Response Desc should be Invalid Amount", "Invalid Amount", respObj.getRespDesc());
                      return;
                  }
            }
            
            assertEquals("TestVasPreAuth::Invalid Service Id Applied", Constants.VAS_DEBIT, requestObj.getServiceId());
            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
            
            super.validateSwitch(switchInfo, requestObj, respObj, false);
            
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) 
                return;
            
            if (requestObj.getVasAccountType() != null &&
                    requestObj.getVasAccountType().trim().length() > 0) {
                
            } else {
                assertEquals("TestVasPreAuth::Vas Account Type value missing. Response should be 30", "30", respObj.getRespCode());
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
