package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.handlers.CardsServiceHandler;
import com.i2c.services.home.CardsServiceHome;
import com.i2c.services.home.FinancialServiceHome;
import com.i2c.services.util.Constants;

public class TestGetCardMiniStatement extends BaseTestCase{

    private FinancialServiceHome home = null;
    private ServicesResponseObj respObj = null;



    public void setUp() {
        super.setUp();
        home = FinancialServiceHome.getInstance(connection);
    }




    public void testGetCardMiniStatement() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }
    
    
    public void performTest(ServicesRequestObj requestObj) {
        
        try {
            
            respObj = ServicesHandler.getInstance(connection, reader.getLogPath()).getCardMiniStatement(requestObj);
            
            assertEquals("TestCardMiniStatement::Transaction Type should be Successful only", Constants.SUCCESSFUL_TRANS_ONLY, requestObj.getTypeOfTrans());
            assertEquals("TestCardMiniStatement::No of Trans should be " + Constants.NO_OF_TRANS, Constants.NO_OF_TRANS, requestObj.getNoOfTrans());            
            assertEquals("TestCardMiniStatement::Transactions should be of amount > 0.0", true, requestObj.isChkAmount());
            
            String cardPrgId = home.getCardProgramID(requestObj.getCardNo());

            assertNotNull("TestCardMiniStatement::Card Program Id is null against Card No("
                    + requestObj.getCardNo() + ")", cardPrgId);

            String serviceId = home.huntForServiceId(requestObj.getDeviceType(), "M1", "00", cardPrgId);
            
            if(serviceId != null)
                assertEquals("TestCardMiniStatement::Invalid Service Id Applied", serviceId, requestObj.getServiceId());
            else
                assertEquals("TestCardMiniStatement::Invalid Service Id Applied", Constants.MINI_STMT_SERVICE, requestObj.getServiceId());
            
            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
            
            super.validateSwitch(switchInfo, requestObj, respObj, true);
            
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) 
                return;
            
            
            //validate the card information
            ServicesResponseObj vresp = CardsServiceHome.getInstance(connection).isCardInfoValid(requestObj.
                getCardNo(), requestObj.getAAC(), requestObj.getExpiryDate(),
                requestObj.getAccountNo(), requestObj.getPin(),
                requestObj.getServiceId(), requestObj.getDeviceType(),
                requestObj.getDeviceId(), requestObj.getCardAcceptorId(),
                requestObj.getCardAcceptNameAndLoc(), requestObj.getMcc(),
                requestObj.getAcquirerId());
            
            if (vresp.getRespCode() != null && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals("TestCardMiniStatement::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj.getRespCode());
                return;
            }
            
            requestObj.setAmount("0.0");
            requestObj.setRetreivalRefNum(null);
            
            super.validateTransaction(requestObj, respObj, "0200");

            super.validateCardBalance(requestObj, respObj);

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    
}
