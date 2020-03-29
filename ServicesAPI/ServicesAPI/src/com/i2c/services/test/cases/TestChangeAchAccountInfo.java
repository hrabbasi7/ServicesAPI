package com.i2c.services.test.cases;

import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;
import com.i2c.services.SwitchInfoObj;
import com.i2c.services.handlers.CardsServiceHandler;
import com.i2c.services.home.ACHServiceHome;
import com.i2c.services.home.CardsServiceHome;
import com.i2c.services.util.CommonUtilities;
import com.i2c.services.util.Constants;
import com.i2c.services.util.LogLevel;

public class TestChangeAchAccountInfo extends BaseTestCase{
    
    ACHServiceHome home = null;
    ServicesResponseObj respObj = null;
    
    public void setUp() {
        super.setUp();
        home = ACHServiceHome.getInstance(connection);
    }
    
    
    public void testChangeAchAccountInfo() {
        
        for (ServicesRequestObj requestObj: requestObjList) {
            
        }
        
    }
    
    public void performTest(ServicesRequestObj requestObj) {
        try {
            
            CardsServiceHome cardsServiceHome = CardsServiceHome.getInstance(connection);
            
            respObj = ServicesHandler.getInstance(connection, reader.getLogPath()).changeACHAccountInfo(requestObj);
            
            String cardPrgId = home.getCardProgramID(requestObj.getCardNo()); // This line added by Imtiaz

            //String serviceId = CardsServiceHome.getInstance(con).huntForServiceId(requestObj.getDeviceType(), "AC");
            String serviceId = cardsServiceHome.huntForServiceId(
                requestObj.getDeviceType(), "AC", "00", cardPrgId);
            
            if(serviceId != null)
                assertEquals("TestGetCardStatus::Invalid Service Id Applied", serviceId, requestObj.getServiceId());
            else
                assertEquals("TestGetCardStatus::Invalid Service Id Applied", Constants.GET_CARD_STATUS_SERVICE, requestObj.getServiceId());
            
            ServicesResponseObj vresp = cardsServiceHome.isCardInfoValid(requestObj.getCardNo(),requestObj.getAAC(),requestObj.getExpiryDate(),requestObj.getAccountNo(),requestObj.getPin(), requestObj.getServiceId(), requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAcquirerId());
            
            if(!vresp.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
                assertEquals("TestGetCardStatus::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj);
                return;
            }
            
            //get the card switch info
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
            
            super.validateSwitch(switchInfo, requestObj, respObj, false);
            
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) 
                return;
            
            vresp = CardsServiceHandler.getInstance(connection).validateCard(requestObj.getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(), null,requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

            //validate the response
            if(!vresp.getRespCode().equalsIgnoreCase(Constants.SUCCESS_CODE)) {
                assertEquals("TestChangeAchAccountInfo::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj);
                return;
            }
            
            String acctQry ="select ach_acct_sr_no from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "";
            //execute the query and get the result
            String acctNo = home.getValue(acctQry);

            //validate the acctNo
            if (acctNo == null) {

                assertEquals("TestChangeAchAccountInfo::Account Serial No not found in ach_accounts, response should be 06", "06", respObj);

                requestObj.setRetreivalRefNum(null);
                requestObj.setAmount("0");
                super.validateTransaction(requestObj, respObj, "0200");

                return;
            } //end if
            

            //verify whether the supplied ach account no is for load or withdraw?
            String acctTypeQry = "select ach_type from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo();
            //execute the query and get the achtype
            String achType = home.getValue(acctTypeQry);

            if (achType != null && !achType.trim().equals("2")) {
                
                assertEquals("TestChangeAchAccountInfo::Supplied account is not withdraw only account, response should be 06", "06", respObj);
                return;
            } //end if
            
            requestObj.setRetreivalRefNum(null);
            requestObj.setAmount("0.0");
            super.validateTransaction(requestObj, respObj, "0200");
            
            super.validateCardBalance(requestObj, respObj);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
