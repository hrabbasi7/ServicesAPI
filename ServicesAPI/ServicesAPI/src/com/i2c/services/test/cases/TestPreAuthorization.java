package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.home.CardsServiceHome;
import com.i2c.services.home.FinancialServiceHome;
import com.i2c.services.util.CommonUtilities;
import com.i2c.services.util.Constants;
import com.i2c.services.util.LogLevel;

public class TestPreAuthorization extends BaseTestCase{

    private FinancialServiceHome home = null;
    private ServicesResponseObj respObj = null;



    public void setUp() {
        super.setUp();
        home = FinancialServiceHome.getInstance(connection);
    }




    public void testPreAuthorization() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }
    
    
    public void performTest(ServicesRequestObj requestObj) {
        try {
            
            respObj = ServicesHandler.getInstance(connection, reader.getLogPath()).preAuthorization(requestObj);
            
            if (requestObj.getAmount() != null &&
                    requestObj.getAmount().trim().length() > 0) {
                
                  try {
                    double amt = Double.parseDouble(requestObj.getAmount().trim());
                    if (amt < 0) {
                        assertEquals("TestPreAuthorization::Invalid Amount Supplied. Response should be 13", "13", respObj.getRespCode());
                        assertEquals("TestPreAuthorization::Invalid Amount Supplied. Response Desc should be Invalid Amount", "Invalid Amount", respObj.getRespDesc());
                        return;
                    }
                  }
                  catch (NumberFormatException ex1) {
                      assertEquals("TestPreAuthorization::Invalid Amount Supplied. Response should be 13", "13", respObj.getRespCode());
                      assertEquals("TestPreAuthorization::Invalid Amount Supplied. Response Desc should be Invalid Amount", "Invalid Amount", respObj.getRespDesc());
                      return;
                  }
                } //Validating Amount
            
            String cardPrgId = home.getCardProgramID(requestObj.getCardNo());
            
            String serviceId = null;
            if (requestObj.getDeviceType().equalsIgnoreCase(Constants.DEVICE_TYPE_CS)) {
                serviceId = home.huntForServiceId(requestObj.
                                                         getDeviceType(),
                                                         "01", "00", cardPrgId,
                                                         requestObj.getIsInternational());
            } else {
                serviceId = home.huntForServiceId(requestObj.
                                                         getDeviceType(),
                                                         "00", "00", cardPrgId,
                                                         requestObj.getIsInternational());
            }
            
            if (serviceId != null && !serviceId.equals("") &&
                    serviceId.trim().length() > 0) {
                assertEquals("TestPreAuthorization::Invalid Service Id Applied", serviceId, requestObj.getServiceId());
                
                } else {
                    assertEquals("TestPreAuthorization::Invalid Service Id Applied", Constants.PRE_AUTHORIZATION, requestObj.getServiceId());
                }
            
            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
            
            super.validateSwitch(switchInfo, requestObj, respObj, false);
            
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) 
                return;
            
            requestObj.setRetreivalRefNum(null);
            super.validateTransaction(requestObj, respObj, "0200");
            
            super.validateCardBalance(requestObj, respObj);
            
        } catch (Exception exp) { 
            exp.printStackTrace();
        }
        
    }
}
