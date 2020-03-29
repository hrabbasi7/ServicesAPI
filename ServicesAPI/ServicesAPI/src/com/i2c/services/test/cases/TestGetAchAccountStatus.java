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

public class TestGetAchAccountStatus extends BaseTestCase{

    private ACHServiceHome home = null;
    private ServicesResponseObj respObj = null;



    public void setUp() {
        super.setUp();
        home = ACHServiceHome.getInstance(connection);
    }




    public void testGetCardStatus() {
        for(ServicesRequestObj requestObj : requestObjList) {
            performTest(requestObj);
        }
    }
    
    public void performTest(ServicesRequestObj requestObj) {
        
        try {
            respObj = ServicesHandler.getInstance(connection, reader.getLogPath()).getCardStatus(requestObj);
            
            String cardPrgId = home.getCardProgramID(requestObj.getCardNo());

            assertNotNull("TestGetCardStatus::Card Program Id is null against Card No("
                    + requestObj.getCardNo() + ")", cardPrgId);

            String serviceId = home.huntForServiceId(
                    requestObj.getDeviceType(), "AA", "00", cardPrgId);

            if(serviceId != null)
                assertEquals("TestGetCardStatus::Invalid Service Id Applied", serviceId, requestObj.getServiceId());
            else
                assertEquals("TestGetCardStatus::Invalid Service Id Applied", Constants.ACH_ACCT_STATUS_SERVICE, requestObj.getServiceId());
            
            ServicesResponseObj vresp = CardsServiceHome.getInstance(connection).isCardInfoValid(requestObj.getCardNo(),requestObj.getAAC(),requestObj.getExpiryDate(),requestObj.getAccountNo(),requestObj.getPin(), requestObj.getServiceId(), requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAcquirerId());

            if (vresp.getRespCode() != null && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals("TestGetCardStatus::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj.getRespCode());
                return;
            }
            
            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
            
            super.validateSwitch(switchInfo, requestObj, respObj, false);
            
            if(switchInfo.getSwitchId() != null && !switchInfo.isSwitchActive()) 
                return;
            
            vresp = CardsServiceHandler.getInstance(connection).validateCard(requestObj.getCardNo(), requestObj.getServiceId(), requestObj.getApplyFee(), null,requestObj.getDeviceType(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());
            
            if (vresp.getRespCode() != null && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals("TestGetCardStatus::Response code does not match. It should be " + vresp.getRespCode(), vresp.getRespCode(), respObj.getRespCode());
                
                return;
            }
            
            String acctQry =
                "select ach_acct_sr_no from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo() + "";
            //execute the query and get the result
            String acctNo = home.getValue(acctQry);
            
            //validate the acctNo
            if (acctNo == null) {
                                
                assertEquals("TestGetCardStatus::Response code does not match. It should be 06", "06", respObj.getRespCode());                
                
                requestObj.setRetreivalRefNum(null);
                requestObj.setAmount("0");
              
                super.validateTransaction(requestObj, respObj, "0200");
                
                return;
           } //end if
            
            //verify whether the supplied ach account no is for load or withdraw?
            String acctTypeQry = "select ach_type from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo();
            //execute the query and get the achtype
            String achType = home.getValue(acctTypeQry);

            if (achType != null && !achType.trim().equals("1")) {
                
                assertEquals("TestGetCardStatus::Response code does not match. It should be 06", "06", respObj.getRespCode());                

              //log the transaction
              String[] transIds = home.logTransaction(requestObj.getCardNo(),requestObj.getServiceId(),requestObj.getDeviceType(),null,"0200","0",respObj.getRespDesc(),"0", respObj.getRespCode(),requestObj.getDeviceId(),requestObj.getCardAcceptorId(),requestObj.getCardAcceptNameAndLoc(),requestObj.getMcc(),requestObj.getAccountNo(),requestObj.getAcquirerId());

              requestObj.setRetreivalRefNum(null);
              requestObj.setAmount("0");
            
              super.validateTransaction(requestObj, respObj, "0200");
              
              return;
            } //end if
            
            requestObj.setAmount("0.0");
            requestObj.setRetreivalRefNum(null);
            
            super.validateTransaction(requestObj, respObj, "0200");
            
            super.validateCardBalance(requestObj, respObj);
            
            acctQry = "select verify_status from ach_accounts where ach_acct_sr_no=" + requestObj.getAchAccountNo();
            //execute the query and get the status
            String acctStatus = home.getValue(acctQry);
            
            assertEquals("TestGetAchAccountStatus:: Returned Ach Account Status is invalid", acctStatus, respObj.getAchAccountStatus());
            
            if(acctStatus != null && acctStatus.trim().equalsIgnoreCase("L"))
                assertEquals("TestGetAchAccountStatus:: Returned Ach Account Status Desc is invalid", "Logged" , respObj.getAchAccountStatusDesc());
              else if(acctStatus != null && acctStatus.trim().equalsIgnoreCase("I"))
                  assertEquals("TestGetAchAccountStatus:: Returned Ach Account Status Desc is invalid", "In Process" , respObj.getAchAccountStatusDesc());
              else if(acctStatus != null && acctStatus.trim().equalsIgnoreCase("V"))
                  assertEquals("TestGetAchAccountStatus:: Returned Ach Account Status Desc is invalid", "Verified" , respObj.getAchAccountStatusDesc());
              else if(acctStatus != null && acctStatus.trim().equalsIgnoreCase("F"))
                  assertEquals("TestGetAchAccountStatus:: Returned Ach Account Status Desc is invalid", "Failed" , respObj.getAchAccountStatusDesc());
            
            
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
