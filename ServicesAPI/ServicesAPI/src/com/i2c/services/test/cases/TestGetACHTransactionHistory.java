package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.handlers.CardsServiceHandler;
import com.i2c.services.home.CardsServiceHome;
import com.i2c.services.home.FinancialServiceHome;
import com.i2c.services.util.Constants;
import com.i2c.services.util.DateUtil;

public class TestGetACHTransactionHistory extends BaseTestCase{

    private FinancialServiceHome home = null;
    private ServicesResponseObj respObj = null;



    public void setUp() {
        super.setUp();
        home = FinancialServiceHome.getInstance(connection);
    }




    public void testGetACHTransactionHistory() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }
    
    
    public void performTest(ServicesRequestObj requestObj) {
        try {
            respObj = ServicesHandler.getInstance(connection, reader.getLogPath()).getACHTransactionHistory(requestObj);
            
            assertEquals("TestGetACHTransactionHistory::Transaction Type should be Successful only", Constants.SUCCESSFUL_TRANS_ONLY, requestObj.getTypeOfTrans());
            
            if (requestObj.getDateFrom() == null)
                assertEquals("Invalid Start Date", DateUtil.getCurrentDate(Constants.WEB_DATE_FORMAT), requestObj.getDateFrom());
              if (requestObj.getDateTo() == null)
                  assertEquals("Invalid End Date", DateUtil.getCurrentDate(Constants.WEB_DATE_FORMAT), requestObj.getDateTo());
              
              String cardPrgId = home.getCardProgramID(requestObj.getCardNo());

              assertNotNull("TestGetACHTransactionHistory::Card Program Id is null against Card No("
                      + requestObj.getCardNo() + ")", cardPrgId);

              String serviceId = home.huntForServiceId(requestObj.getDeviceType(), "AH", "00", cardPrgId);
              
              if(serviceId != null)
                  assertEquals("TestGetACHTransactionHistory::Invalid Service Id Applied", serviceId, requestObj.getServiceId());
              else
                  assertEquals("TestGetACHTransactionHistory::Invalid Service Id Applied", Constants.ACH_TRANS_HISTORY_SERVICE, requestObj.getServiceId());
              
              // get the switch information of the card
              SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
              
              super.validateSwitch(switchInfo, requestObj, respObj, false);
              
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
                  assertEquals("TestGetACHTransactionHistory::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj.getRespCode());
                  return;
              }
              
              //validate the information
              vresp = CardsServiceHandler.getInstance(connection).validateCard(requestObj.
                  getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(), null,
                  requestObj.getDeviceType(), requestObj.getDeviceId(),
                  requestObj.getCardAcceptorId(), requestObj.getCardAcceptNameAndLoc(),
                  requestObj.getMcc(), requestObj.getAccountNo(),
                  requestObj.getAcquirerId());
              
              if (vresp.getRespCode() != null && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                  assertEquals("TestGetACHTransactionHistory::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj.getRespCode());
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
