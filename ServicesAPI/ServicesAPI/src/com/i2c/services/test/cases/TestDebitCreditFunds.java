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

public class TestDebitCreditFunds extends BaseTestCase {
    
    private FinancialServiceHome home = null;
    private ServicesResponseObj respObj = null;
    
    public void setUp() {
        super.setUp();
        home = FinancialServiceHome.getInstance(connection);
    }
    
    public void testDebitCreditFunds() {
        for (ServicesRequestObj requestObj: requestObjList) {
            performTest(requestObj);
        }
    }
    
    public void performTest(ServicesRequestObj requestObj) {
        try {
            
            respObj = ServicesHandler.getInstance(connection, reader.getLogPath()).creditFunds(requestObj);
            
            if (requestObj.getAmount() != null &&
                    requestObj.getAmount().trim().length() > 0) {
                
                  try {
                    double amt = Double.parseDouble(requestObj.getAmount().trim());
                    if (amt < 0) {
                        //assert
                        assertEquals("TestCreditFunds:: Invalid Amount Supplied. Response code should be 13", "13" , respObj.getRespCode());
                        return;
                    }
                  }
                  catch (NumberFormatException ex1) {
                      //assert
                      assertEquals("TestCreditFunds:: Invalid Amount Supplied. Response code should be 13", "13" , respObj.getRespCode());
                      return;
                  }
                }else{
                    //assert
                    assertEquals("TestCreditFunds:: Invalid Amount Supplied. Response code should be 13", "13" , respObj.getRespCode());
                    return;
                }
            

            if (requestObj.getServiceId() == null ||
                    requestObj.getServiceId().trim().length() == 0) {
                
                  String cardPrgId = home.getCardProgramID(requestObj.getCardNo());

                  String serviceId = home.huntForServiceId(requestObj.getDeviceType(), "21", "00", cardPrgId);

                  if (serviceId != null && serviceId.trim().length() > 0 &&!serviceId.equals(""))
                      assertEquals("TestCreditFunds::Invalid Service Id Applied", serviceId, requestObj.getServiceId());
                  else
                      assertEquals("TestCreditFunds::Invalid Service Id Applied", "SW_DEPOSIT", requestObj.getServiceId());
                }
            
            // get the switch information of the card
            SwitchInfoObj switchInfo = home.getCardSwitchInfo(requestObj.getCardNo());
            
            super.validateSwitch(switchInfo, requestObj, respObj, true);

            double requestedAmt = Double.parseDouble(requestObj.getAmount());
            boolean batchMode = switchInfo.isBatchTransAllowed();

            if (requestObj.getAmount().indexOf("-") != -1) {
                
                //call check debit
                ServicesResponseObj vresp = home.checkDebit(requestObj.getCardNo(),
                                                          requestObj.getAmount(),
                                                          requestObj.getServiceId(),
                                                          requestObj.getApplyFee(), batchMode,requestObj.getRetreivalRefNum(),requestObj.getCardAcceptorId(),"0200",requestObj.getAcquirerId());

                //assert
                assertEquals("TestCreditFunds::Requested Amount is invalid", requestedAmt+"" , requestObj.getAmount());

                if (vresp.getRespCode() != null && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                    assertEquals("TestCreditFunds::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj);
                    
                    requestObj.setRetreivalRefNum(null);
                    requestObj.setAmount("0");
                    super.validateTransaction(requestObj, respObj, "0200");
                    return;
                }
              } //end if
              else {
                //call check credit
                ServicesResponseObj vresp = home.checkCredit(requestObj.getCardNo(),
                    requestObj.getAmount(), requestObj.getServiceId(),
                    requestObj.getApplyFee(), batchMode,requestObj.getRetreivalRefNum(),requestObj.getCardAcceptorId(),"0200",requestObj.getAcquirerId());

                if (vresp.getRespCode() != null && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                    assertEquals("TestCreditFunds::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj);
                    
                    requestObj.setRetreivalRefNum(null);
                    requestObj.setAmount("0");
                    super.validateTransaction(requestObj, respObj, "0200");
                    return;
                }
              } //end else
            
            //validate card information
            ServicesResponseObj vresp = CardsServiceHome.getInstance(connection).isCardInfoValid(requestObj.getCardNo(), requestObj.getAAC(),
                                                requestObj.getExpiryDate(), requestObj.getAccountNo(),
                                                requestObj.getPin(), requestObj.getServiceId(),
                                                requestObj.getDeviceType(), requestObj.getDeviceId(),
                                                requestObj.getCardAcceptorId(),
                                                requestObj.getCardAcceptNameAndLoc(),
                                                requestObj.getMcc(), requestObj.getAcquirerId());
            
            if (vresp.getRespCode() != null && !vresp.getRespCode().equals(Constants.SUCCESS_CODE)) {
                assertEquals("TestCreditFunds::Response Code does not match. It should be "+ vresp.getRespCode(), vresp.getRespCode(), respObj);
                return;
            }
            
            super.validateCardBalance(requestObj, respObj);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
