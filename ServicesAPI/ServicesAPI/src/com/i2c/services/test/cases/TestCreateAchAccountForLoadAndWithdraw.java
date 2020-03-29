package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.home.ACHServiceHome;
import com.i2c.services.home.CardsServiceHome;

public class TestCreateAchAccountForLoadAndWithdraw extends BaseTestCase{
    
    private ACHServiceHome home = null;
    private ServicesResponseObj respObj;




    public void setUp() {
        super.setUp();
        home = ACHServiceHome.getInstance(connection);
    }




    public void testCreateAchAccountForLoad() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }




    public void performTest(ServicesRequestObj requestObj) {
        try {
            
            respObj = ServicesHandler.getInstance(connection,
                    reader.getLogPath()).createACHAccountForLoadAndWithdraw(requestObj);
            // assert
            assertEquals("TestCreateAchAccountForLoad::Ach Type is not 1", "1",
                    requestObj.getAchType());

            String cardPrgId = home.getCardProgramID(requestObj.getCardNo());

            String serviceId = CardsServiceHome.getInstance(connection)
                    .huntForServiceId(requestObj.getDeviceType(), "43", "00",
                            cardPrgId);

            if(serviceId != null)
                assertEquals("TestGetCardStatus::Invalid Service Id Applied",
                        serviceId, requestObj.getServiceId());
            else
                assertEquals("TestGetCardStatus::Invalid Service Id Applied",
                        "ACH_REG", requestObj.getServiceId());
            
            super.validateCreateAchAccount(requestObj, respObj);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
