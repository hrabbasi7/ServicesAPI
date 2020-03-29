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

public class TestForcePostTransaction extends BaseTestCase{

    private FinancialServiceHome home = null;
    private ServicesResponseObj respObj = null;



    public void setUp() {
        super.setUp();
        home = FinancialServiceHome.getInstance(connection);
    }




    public void testGetCardStatus() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }
    
    public void performTest(ServicesRequestObj requestObj) {
        
        try {
            respObj = ServicesHandler.getInstance(connection, reader.getLogPath()).forcePostTransaction(requestObj);
            
            if (requestObj.getAmount() != null &&
                    requestObj.getAmount().trim().length() > 0) {

                try {
                    double amt = Double.parseDouble(requestObj.getAmount().trim());
                    if (amt < 0) {
                        
                        assertEquals("TestForcePostTransaction::Response Code does not match. It should be 13", "13", respObj.getRespCode());
                        return;
                    }
                  }
                  catch (NumberFormatException ex1) {
                      assertEquals("TestForcePostTransaction::Response Code does not match. It should be 13", "13", respObj.getRespCode());
                      return;
                  }
                } //Validating Amount
            
            String cardPrgId = home.getCardProgramID(requestObj.getCardNo());

            assertNotNull("TestForcePostTransaction::Card Program Id is null against Card No("
                    + requestObj.getCardNo() + ")", cardPrgId);

            String serviceId = home.huntForServiceId(
                    requestObj.getDeviceType(), "00", "00", cardPrgId);

            if(serviceId != null)
                assertEquals("TestForcePostTransaction::Invalid Service Id Applied", serviceId, requestObj.getServiceId());
            else
                assertEquals("TestForcePostTransaction::Invalid Service Id Applied", Constants.FORCE_POST_AUTHORIZATION, requestObj.getServiceId());
            
            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
            
            super.validateSwitch(switchInfo, requestObj, respObj, false);
            
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) 
                return;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
