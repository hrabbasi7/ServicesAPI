package com.i2c.services.test;

import com.i2c.services.*;
import com.i2c.services.registration.base.*;
import com.i2c.services.acqinstauthneticator.*;
import java.sql.*;
//import com.i2c.wrapper.hsm.*;

public class TestServices {
//  public static void main(String[] args) {
//    try {
//
//      ServicesRequestObj req = new ServicesRequestObj();
//      req.setEncryptedData("CF95BE9C3789B5E3E9D2CB1FFEBD7204");
//      req.setDecryptedData("i2cmcp");
//      ServicesResponseObj respObj = ServicesHandler.encryptData(req);
//      System.out.println("getAcqID -- > " + respObj.getEncryptedData());
//    }
//    catch (Exception ex) {
//    }
//  }


/*
  public static void main(String[] args) {
    try {
      Connection con = getConnection("226-m", "mcp", "i2cmcp");

      AuthenticatorResponseObj respObj = null;
      AuthenticatorRequestObj requestObj = new AuthenticatorRequestObj();
      requestObj.setAcquirerId("mcptest");
//      requestObj.setCardBin("122222");
      requestObj.setInstanceId("mcp");
//      requestObj.setServiceId("IVR_BI");
      requestObj.setDeviceType("I");
      requestObj.setTransTypeId("31");

      respObj = ServicesHandler.getInstance(con,"c:\\mcp\\services").authenticateAcquirer(requestObj);


      System.out.println("getResponseCode -- > " + respObj.getResponseCode());
      System.out.println("getResponseDesc -- > " + respObj.getResponseDesc());
      System.out.println("getErrSerialNo-- > " + respObj.getErrSerialNo());
      System.out.println("getAcqObj -- > " + respObj.getAcqObj());
      System.out.println("getInstObj -- > " + respObj.getInstObj());

     AcquirerInfoObj acq = respObj.getAcqObj();
     if(acq != null){
       System.out.println("getAcqID -- > " + acq.getAcqID());
       System.out.println("getAcqUserID -- > " + acq.getAcqUserID());
       System.out.println("getAcqUserPassword-- > " + acq.getAcqUserPassword());
       System.out.println("getAlgoCode -- > " + acq.getAlgoCode());
       System.out.println("getSecurityKey1 -- > " + acq.getSecurityKey1());
       System.out.println("getSecurityKey2 -- > " + acq.getSecurityKey2());
       System.out.println("getSecurityKey3 -- > " + acq.getSecurityKey3());
     }

     InstanceInfoObj ins = respObj.getInstObj();
     if(ins != null){
       System.out.println("getAcqID -- > " + ins.getInstanceId());
       System.out.println("getAcqUserID -- > " + ins.getConnStr());
       System.out.println("getAcqUserPassword-- > " + ins.getConnUsr());
       System.out.println("getAlgoCode -- > " + ins.getConnPwd());
     }
    }
    catch (Exception ex) {
    }
  }
*/

  public static void main(String[] args) {
    try {

//      HSMConnectionPoolWrapper hc= new HSMConnectionPoolWrapper("c:\\mcp\\services","D:\\ShahzadAliDATA\\SVN\\ServicesAPI-SVN\\wrapper.ini");

//      System.out.println("Result " + Math.pow(2,5));
      //'6039491000004765', '6039491000004666'
//      Connection con = getConnection("116", "admin", "scmadmin");
      Connection con = getConnection("226", "mcp", "i2cmcp");
//      Connection con = getConnection("226", "shali", "temp_shali");
//      HSMHandler h  = new HSMHandler("c:\\mcp\\services","192.168.0.226",9005,1);
//      System.out.print(h.doEncrypt("tueday"));
//      Connection con = getConnection("stage", "root", "i2c_xo");
//      Connection con = getConnection("226-b", "mcp", "temp_db");

//     requestObj.setCardNo("4784400020016131");4564564556009970;4444440000000000005
//Activate Card,Credit Funds,Debit Funds
//      requestObj.setCardNo("");
      ServicesRequestObj requestObj = new ServicesRequestObj();
//      requestObj.setTransId("202836");
//      requestObj.setBirthYear("1952");
      requestObj.setCardNo("1112220000000049");
//      requestObj.setDescription("Assignment during card generation");
      requestObj.setCardAcceptorId("SSSH01");
//      requestObj.setToCardNo("3030300000000150");
//      requestObj.setLinkType("L");
//      requestObj.setPreAuthTransId("16413");
//      requestObj.setVasAccountType("1");
      requestObj.setAmount("50");
//      requestObj.setPreAuthTransId("16407");
//      requestObj.setChargeBackCaseId("1172");
//      requestObj.setCardStatus("F");
//      requestObj.setVasAccountType("01");
//      requestObj.setVasVendorId("01");
//      requestObj.setBankAcctNo("3456956");
//      requestObj.setBankAcctType("1");
//      requestObj.setBankRoutingNo("0");
//      requestObj.setNickName("abcdefg");
//      requestObj.setAmount("100");
//      requestObj.setVasAccountType("2");
//      requestObj.setNoOfTrans("10");
//      requestObj.setAcquirerFeeAmt("11");
//      requestObj.setAcquirerFeeDesc("Card Calls");
      requestObj.setDeviceType("W");
//      requestObj.setRetreivalRefNum("998655");
//      requestObj.setAcquirerId("mcp002");
//      requestObj.setAcqUsrId("gdtest");
//      requestObj.setAcqData1("data1");
//      requestObj.setAcqData2("data2");
//      requestObj.setAcqData3("data3");
//      requestObj.setCardAcceptNameAndLoc("testmerchant");
//      requestObj.setDeviceId("tstdev1234");
//      requestObj.setMcc("testmcc1234");
//      requestObj.setDescription("Testing Get VAS Accounts Info");
//      requestObj.setIsAdminTrans("Y");
//      requestObj.setVirtualAccount("testvrtacc");
//      requestObj.setReceiverName("testrecvr");
//      requestObj.setLocalDateTime("2006-01-01 16:51:00");
//      requestObj.setReverseTransFee("Y");
      requestObj.setApplyFee("Y");

      ServicesResponseObj respObj = null;
      respObj = ServicesHandler.getInstance(con,"c:\\mcp\\services").activateCard(requestObj);

      System.out.println( "Response Code---> " + respObj.getRespCode()
                    + "<---Resp Desc--->" + respObj.getRespDesc()
                    + "<---Is Card Assigned--->" + respObj.isIsCardAssigned()
                    + "<---Trans Id--->" + respObj.getTransId()
                    + "<---Fee Amount--->" + respObj.getFeeAmount()
                    + "<---Ret Ref Num--->" + respObj.getRetRefNum()
                    + "<---First Name--->" + respObj.getFirstName()
                    + "<---Last Name--->" + respObj.getLastName()
                    + "<---Card Ref--->" + respObj.getCardRef()
                    + "<---Portfolio--->" + respObj.getPortFolio()
                    + "<---Zip Code--->" + respObj.getZipCode()
                    + "<---Time Stamp--->" + respObj.getTimeStamp()
                    + "<---Exp Date--->" + respObj.getCardExpDate()
                    + "<---CVV2--->" + respObj.getCvv2()
                    + "<---Card No--->" + respObj.getCardNo());

//Reversal
//      requestObj.setCardNo("3030300000000291");
//      requestObj.setTransId("191676");
//      requestObj.setDeviceType("W");
//      requestObj.setRetreivalRefNum("123456");
//      requestObj.setAcquirerId("gd001");
//      requestObj.setAcqUsrId("gdtest");
//      requestObj.setAcqData1("data1");
//      requestObj.setAcqData2("data2");
//      requestObj.setAcqData3("data3");
//      requestObj.setCardAcceptorId("abc1234");
//      requestObj.setCardAcceptNameAndLoc("testmerchant");
//      requestObj.setDeviceId("tstdev1234");
//      requestObj.setMcc("testmcc1234");
//      requestObj.setVirtualAccount("testvrtacc");
//      requestObj.setReceiverName("testrecvr");
//
//      ServicesResponseObj respObj = null;
//
//      respObj = ServicesHandler.getInstance(con, "c:\\mcp\\services").reverseAcquirerFee(requestObj);
//      System.out.println("Response Code---> " + respObj.getRespCode()
//                         + "<---Resp Desc--->" + respObj.getRespDesc()
//                         + "<---Trans Id--->" + respObj.getTransId()
//                         + "<---Ret Ref Num--->" + respObj.getRetRefNum()
//                         + "<---First Name--->" + respObj.getFirstName()
//                         + "<---Last Name--->" + respObj.getLastName()
//                         + "<---Card Ref--->" + respObj.getCardRef()
//                         + "<---Portfolio--->" + respObj.getPortFolio()
//                         + "<---Zip Code--->" + respObj.getZipCode()
//                         + "<---Time Stamp--->" + respObj.getTimeStamp());

//      requestObj.setCardNo("3030300000000291");
//      requestObj.setServiceId("IVR_BI");
//      requestObj.setDeviceType("W");
//      requestObj.setRetreivalRefNum("123456");
//      requestObj.setAcquirerId("gd001");
//      requestObj.setAcqUsrId("gdtest");
//      requestObj.setAcqData1("data1");
//      requestObj.setAcqData2("data2");
//      requestObj.setAcqData3("data3");
//      requestObj.setCardAcceptorId("abc1234");
//      requestObj.setCardAcceptNameAndLoc("testmerchant");
//      requestObj.setDeviceId("tstdev1234");
//      requestObj.setMcc("testmcc1234");
//      requestObj.setVirtualAccount("testvrtacc");
//      requestObj.setReceiverName("testrecvr");
//      requestObj.setApplyFee("N");
//
//      ServicesResponseObj respObj = null;
//
//      respObj = ServicesHandler.getInstance(con,"c:\\mcp\\services").allCashOut(requestObj);
//      System.out.println( "Response Code---> " + respObj.getRespCode()
//                    + "<---Resp Desc--->" + respObj.getRespDesc()
//                    + "<---Trans Id--->" + respObj.getTransId()
//                    + "<---Ret Ref Num--->" + respObj.getRetRefNum()
//                    + "<---First Name--->" + respObj.getFirstName()
//                    + "<---Last Name--->" + respObj.getLastName()
//                    + "<---Card Ref--->" + respObj.getCardRef()
//                    + "<---Portfolio--->" + respObj.getPortFolio()
//                    + "<---Zip Code--->" + respObj.getZipCode()
//                    + "<---Time Stamp--->" + respObj.getTimeStamp());



//ServicesResponseObj respObj = ServicesHandler.getInstance(con,"c:\\mcp\\services").getVASMiniStatement(requestObj);
//      ServicesResponseObj respObj = ServicesHandler.getInstance(con,"c:\\mcp\\services").getACHMiniStatement(requestObj);
//

//      System.out.println("resp code -- > " + respObj.getRespCode());
//      System.out.println("resp Desc -- > " + respObj.getRespDesc());
//      System.out.println("Trans Id -- > " + respObj.getTransId());
//      System.out.println("Fee -- > " + respObj.getFeeAmount());
//      System.out.println("From Card Balance -- > " + respObj.getCardBalance());
//      System.out.println("Business Date --> " + respObj.getBusinessDate());
//      System.out.println("To Card Balance -- > " + respObj.getToCardBalance());
//      System.out.println("Pre Auth Trans Id -- > " + respObj.getPreAuthTransId());
//      System.out.println("Card Status -- > " + respObj.getCardStatusCode());
//      System.out.println("Card Status Desc -- > " + respObj.getCardStatusDesc());
//      System.out.println("AAC -- > " + respObj.getAAC());
//      System.out.println("ACH Account No -- > " + respObj.getAchAccountNo());
//      System.out.println("Transaction id -- > " + respObj.getTransactionId());
//      System.out.println("New Card No -- > " + respObj.getToCardNo());
//      System.out.println("New Card Account -- > " + respObj.getToCardAccountNo());
//      System.out.println("New Card Balance -- > " + respObj.getToCardBalance());
//      System.out.println("Expiry Date -- > " + respObj.getToCardExpDate());
//      System.out.println("Bank Name --> " + respObj.getBankName());
//      System.out.println("Bank Account Number --> " + respObj.getAchAccountNo());
//      System.out.println("Bank Account Title --> " + respObj.getBankAccountTitle());
//      System.out.println("Bank Account Type --> " + respObj.getBankAccountType());
//      System.out.println("Bank Routing No --> " + respObj.getBankRoutingNo());
//      System.out.println("Cashout Amount --> " + respObj.getCashOutAmount());
//      System.out.println("Exception Message --> " + respObj.getExcepMsg());
//      System.out.println("Stack Trace --> " + respObj.getStkTrace());
//      System.out.println("Log Path --> " + respObj.getLogFilePath());


//
      if(respObj.getTransactionList().size() > 0)
      {
        for (int i = 0; i < respObj.getTransactionList().size(); i++) {
          System.out.println("--------------------------------------------------------");
          TransactionObj transObj = (TransactionObj) respObj.getTransactionList().get(i);
          System.out.println("Trans Id -- > " + transObj.getTransId());
          System.out.println("Account No -- > " + transObj.getAccountNo());
          System.out.println("Trans Type Id -- > " + transObj.getTransTypeId());
          System.out.println("Trans Date -- > " + transObj.getTransDate());
          System.out.println("Business Date -- > " + transObj.getBusinessDate());
          System.out.println("Acceptor Name & LOC -- > " + transObj.getAccpNameAndLoc());
          System.out.println("Amount -- > " + transObj.getAmount());
          System.out.println("Desc -- > " + transObj.getDescription());
          System.out.println("-----------------------------------------------------------");
        }
      }
//      if(respObj.getCardAccounts().size() > 0)
//      {
//        for (int i = 0; i < respObj.getCardAccounts().size(); i++) {
//          System.out.println("--------------------------------------------------------");
//          CardAccountObj transObj = (CardAccountObj) respObj.getCardAccounts().get(i);
//          System.out.println("Account No -- > " + transObj.getCardAccountNo());
//          System.out.println("Account Status -- > " + transObj.getCardAccountStatus());
//          System.out.println("Account Type -- > " + transObj.getCardAccountType());
//          System.out.println("-----------------------------------------------------------");
//        }
//      }
//
//      if(respObj.getLinkedCards().size() > 0)
//      {
//        for (int i = 0; i < respObj.getLinkedCards().size(); i++) {
//          System.out.println("--------------------------------------------------------");
//          String cardNo = (String) respObj.getLinkedCards().get(i);
//          System.out.println("Card No -- > " + cardNo);
//          System.out.println("-----------------------------------------------------------");
//        }
//      }

      if(respObj.getVasAccountList().size() > 0)
      {
        for (int i = 0; i < respObj.getVasAccountList().size(); i++) {
          System.out.println("--------------------------------------------------------");
          VASAccountsInfoObj vasInfo = (VASAccountsInfoObj) respObj.getVasAccountList().get(i);
          System.out.println("VAS No -- > " + vasInfo.getVasAccountNumber());
          System.out.println("VAS Type -- > " + vasInfo.getVasAccountType());
          System.out.println("VAS Vendor -- > " + vasInfo.getVasVendor());
          System.out.println("-----------------------------------------------------------");
        }
      }


    }
    catch (Exception exp) {
      System.out.println("Exception -- > " + exp.getMessage());
    } //end catch
  } //end main

/*
public static void main(String[] args) {
  try {
    Connection con = getConnection("226", "shali", "temp_shali");

    TransactionRequestInfoObj reqObj = new TransactionRequestInfoObj();
//    reqObj.setExistingCard("1112220000002144");
    reqObj.setCardPrgId("TestCardProgram123");
    reqObj.setInitialAmount("10");
//    reqObj.setTransferAmount("10");
//    reqObj.setCloseExistingCard(true);
//    reqObj.setReissueType("3");
    reqObj.setDeviceType("W");
    reqObj.setApplyFee("Y");

    reqObj.setRetRefNumber("123456");
    reqObj.setAcquirerId("gd001");
    reqObj.setAcquirerUserId("gdtest");
    reqObj.setAcquirerData1("data1");
    reqObj.setAcquirerData2("data2");
    reqObj.setAcquirerData3("data3");
    reqObj.setCrdAceptorCode("SSSH5");
    reqObj.setCrdAceptorName("testmerchant");
    reqObj.setDeviceId("tstdev1234");
    reqObj.setMerchantCatCd("testmcc1234");

    reqObj.setFirstName("Timothy");
    reqObj.setLastName("Kennedy");
    reqObj.setAddress1("828 North East 15th avenue unit 4");
    reqObj.setState("FL");
    reqObj.setZip("33304");
    reqObj.setCity("Fort Lauderdale");
    reqObj.setSsn("123456789");
    reqObj.setDrivingLisenseNumber("987654321");
    reqObj.setDrivingLisenseState("FL");
    reqObj.setAddress2("Address 2");
    reqObj.setEmailAddress("shali@i2inc.com");
    reqObj.setMiddleName("Middle");
    reqObj.setMotherMaidenName("MMN");
    reqObj.setCountryCode("US");
    reqObj.setHomePhone("123456");
    reqObj.setWorkPhone("67890");
    reqObj.setGender("M");
    reqObj.setForeignId("14646717");
    reqObj.setDob("1981-10-24");
    reqObj.setForeignIdType("2");

    TransactionResponseInfoObj resObj = new TransactionResponseInfoObj();

    resObj = ServicesHandler.getInstance(con, "c:\\mcp\\services").purchaseOrder(reqObj);

    System.out.println("resp code -- > " + resObj.getResposneCode());
    System.out.println("resp Desc -- > " + resObj.getResposneDescription());
    System.out.println("Trans Id -- > " + resObj.getTraceAuditNumber());
    System.out.println("ISO Serial No-- > " + resObj.getIsoSerialNumber());
    System.out.println("Fee -- > " + resObj.getFeeAmount());
    System.out.println("New Card Numebr -- > " + resObj.getNewCardNumber());
    System.out.println("getCardBalance -- > " + resObj.getCardBalance());
    System.out.println("getRefernceId -- > " + resObj.getRefernceId());
//    System.out.println("getCardHolderName-- > " + resObj.getCardHolderName());
//    System.out.println("getCardPrgName -- > " + resObj.getCardPrgName());
//    System.out.println("getCardStatus-- > " + resObj.getCardStatus());
//    System.out.println("getCurrentBalance -- > " + resObj.getCurrentBalance());
//    System.out.println("getExpiryDate -- > " + resObj.getExpiryDate());
//    System.out.println("getCardBatchNo-- > " + resObj.getCardBatchNo());
//    System.out.println("getInstitutionName -- > " + resObj.getInstitutionName());
//    System.out.println("getLedgerBalance-- > " + resObj.getLedgerBalance());
//    System.out.println("getUserID-- > " + resObj.getUserID());
  }
  catch (Exception exp) {
    System.out.println("Exception -- > " + exp.getMessage());
  } //end catch
}
*/

  public static Connection getConnection(String serverId, String userId,
                                         String password) throws Exception {
    try {
      Class.forName("com.informix.jdbc.IfxDriver");

      String connectionString = null;

      if (serverId.trim().equalsIgnoreCase("226")) {
        //jdbc:informix-sqli://192.168.0.226:8002/cards:informixserver=ids_mcp
        connectionString =
//              "jdbc:informix-sqli://192.168.0.41:8019/cards:informixserver=ids_net_mcp01";
            "jdbc:informix-sqli://192.168.0.226:9011/tcp:informixserver=ids_net_mcp10;database=cards;";
      } //end if
      else if (serverId.trim().equalsIgnoreCase("226-m")) {
        //jdbc:informix-sqli://192.168.0.226:8002/cards:informixserver=ids_mcp
        connectionString =
//              "jdbc:informix-sqli://192.168.0.41:8019/cards:informixserver=ids_net_mcp01";
            "jdbc:informix-sqli://192.168.0.226:9001/card_instances:informixserver=ids_net_mcp10";
      }
      else if (serverId.trim().equalsIgnoreCase("226-b")) {
        //jdbc:informix-sqli://192.168.0.226:8002/cards:informixserver=ids_mcp
        connectionString =
//              "jdbc:informix-sqli://192.168.0.41:8019/cards:informixserver=ids_net_mcp01";
            "jdbc:informix-sqli://192.168.0.116:8006/cards:informixserver=ids_mcpqa2";
      }

      else if (serverId.trim().equalsIgnoreCase("pps")) {
        connectionString =
            "jdbc:informix-sqli://192.168.0.41:8019/cards:informixserver=ids_net_mcp01";
      }
      else if (serverId.trim().equalsIgnoreCase("trin")) {
        connectionString =
            "jdbc:informix-sqli://192.168.0.226:9001/cards:informixserver=ids_mcpqa2";
      }
      else if (serverId.trim().equalsIgnoreCase("116")) {
        connectionString = "jdbc:informix-sqli://192.168.0.116:8004/cards:informixserver=ids_net_mcpqa;database=cards;";
      } //end else if
      else if (serverId.trim().equalsIgnoreCase("stage")) {
        connectionString = "jdbc:informix-sqli://192.168.0.43:8001/cards:informixserver=ids_net_mcpstage;";
      }

      else if (serverId.trim().equalsIgnoreCase("199")) {
        connectionString = "jdbc:informix-sqli://206.111.151.199:8004:informixserver=ids_net_mcp01;database=cards;";
      } //end else if

      System.out.println("Before making the connection.......");
      System.out.println("Connection String -- > " + connectionString);

      DriverManager.setLoginTimeout(5);

      Connection testCon = DriverManager.getConnection(
          connectionString, userId, password);

      System.out.println("Connection established with server......");
      return testCon;
    }
    catch (Exception ex) {
      System.out.println("Exception --  > " + ex.getMessage());
      throw ex;
    } //end catch
  } //end getConnection

} //end TestServices
