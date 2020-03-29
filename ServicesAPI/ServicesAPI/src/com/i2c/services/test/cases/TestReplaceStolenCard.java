package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.home.CardsServiceHome;

public class TestReplaceStolenCard extends BaseTestCase{
    private CardsServiceHome home = null;
    private ServicesResponseObj respObj = null;



    public void setUp() {
        super.setUp();
        home = CardsServiceHome.getInstance(connection);
    }




    public void testReplaceStolenCard() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }
    
    public void performTest(ServicesRequestObj requestObj) {
        try {
            respObj = ServicesHandler.getInstance(connection, reader.getLogPath()).replaceStolenCard(requestObj);

            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
            
            super.validateSwitch(switchInfo, requestObj, respObj, false);
            
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) 
                return;
            
            
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
