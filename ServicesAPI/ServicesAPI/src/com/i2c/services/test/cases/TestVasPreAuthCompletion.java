package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.home.VasServiceHome;
import com.i2c.services.util.CommonUtilities;
import com.i2c.services.util.Constants;
import com.i2c.services.util.LogLevel;

public class TestVasPreAuthCompletion extends BaseTestCase{
    
    private VasServiceHome home = null;
    private ServicesResponseObj respObj = null;



    public void setUp() {
        super.setUp();
        home = VasServiceHome.getInstance(connection);
    }




    public void testVasPreAuthCompletion() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }
    
    public void performTest(ServicesRequestObj requestObj) {
        try {
            respObj = ServicesHandler.getInstance(connection, reader.getLogPath()).vasPreAuthCompletion(requestObj);
            
            if (requestObj.getAmount() != null &&
                    requestObj.getAmount().trim().length() > 0) {
                
                  try {
                    double amt = Double.parseDouble(requestObj.getAmount().trim());
                    if (amt < 0) {
                        assertEquals("TestVasPreAuthCompletion::Amount is invalid. Response should be 13", "13", respObj.getRespCode());
                        return;
                    }
                  }
                  catch (NumberFormatException ex1) {
                      assertEquals("TestVasPreAuthCompletion::Amount is invalid. Response should be 13", "13", respObj.getRespCode());
                      return;
                  }
                } //Validating Amount
            
            assertEquals("TestVasPreAuthCompletion::Invalid Service Id Applied", Constants.VAS_DEBIT, requestObj.getServiceId());
            
            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
            
            super.validateSwitch(switchInfo, requestObj, respObj, false);
            
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) 
                return;
            
            if (requestObj.getVasAccountType() != null &&
                    requestObj.getVasAccountType().trim().length() > 0) {
                }
                else {
                    assertEquals("TestVasPreAuthCompletion::Amount is invalid. Response should be 30", "30", respObj.getRespCode());
                    return;
                }
            
            requestObj.setRetreivalRefNum(null);
            
            super.validateTransaction(requestObj, respObj, "0200");
            
            super.validateCardBalance(requestObj, respObj);
            
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
