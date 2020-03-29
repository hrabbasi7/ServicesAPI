package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.home.FinancialServiceHome;
import com.i2c.services.util.CommonUtilities;
import com.i2c.services.util.Constants;
import com.i2c.services.util.LogLevel;

public class TestGetTransactionInfo  extends BaseTestCase{

    private FinancialServiceHome home = null;
    private ServicesResponseObj respObj = null;



    public void setUp() {
        super.setUp();
        home = FinancialServiceHome.getInstance(connection);
    }




    public void testGetTransactionInfo() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }
    
    public void performTest(ServicesRequestObj requestObj) {
        try {
            respObj = ServicesHandler.getInstance(connection, reader.getLogPath()).getTransactionInfo(requestObj);

            String cardPrgId = home.getCardProgramID(requestObj.getCardNo());

            assertNotNull("TestGetCardStatus::Card Program Id is null against Card No("
                    + requestObj.getCardNo() + ")", cardPrgId);

            String serviceId = home.huntForServiceId(
                    requestObj.getDeviceType(), "T1", "00", cardPrgId);

            if(serviceId != null)
                assertEquals("TestGetCardStatus::Invalid Service Id Applied", serviceId, requestObj.getServiceId());
            else
                assertEquals("TestGetCardStatus::Invalid Service Id Applied", Constants.TRANS_INFO_SERVICE, requestObj.getServiceId());
            
            if (requestObj.getTransCat() != null &&
                    !requestObj.getTransCat().trim().equalsIgnoreCase("F")) {
                
                assertEquals("TestGetTransactionInfo::Invalid Transaction Id, Response should be 12", "12", respObj.getRespCode());
                
                requestObj.setAmount("0");
                requestObj.setRetreivalRefNum(null);
                
                super.validateTransaction(requestObj, respObj, "0200");
                
                return;
                } //end if
            
            
            //get the card no
            String cardNo = home.getValue("select card_no from trans_requests where iso_serial_no=" +requestObj.getTransId());
            
            if (cardNo == null) {
                
                assertEquals("TestGetTransactionInfo::Invalid Transaction Id, Response should be 12", "12", respObj.getRespCode());
                
                requestObj.setAmount("0");
                requestObj.setRetreivalRefNum(null);
                
                super.validateTransaction(requestObj, respObj, "0200");
                
                return;
            } //end if
            
            if (requestObj.getCardNo() != null &&
                    !requestObj.getCardNo().trim().equals("")) {
                
                  //match card no
                  if (!cardNo.trim().equalsIgnoreCase(requestObj.getCardNo().trim())) {
                      
                      assertEquals("TestGetTransactionInfo::Invalid Transaction Id, Response should be 12", "12", respObj.getRespCode());
                      
                      requestObj.setAmount("0");
                      requestObj.setRetreivalRefNum(null);
                      
                      super.validateTransaction(requestObj, respObj, "0200");
                      
                      return;
                  } //end if
                } //end if
            else {
                assertEquals("TestGetTransactionInfo::Invalid Card No in Request Object", cardNo, requestObj.getCardNo());
            }
            
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
