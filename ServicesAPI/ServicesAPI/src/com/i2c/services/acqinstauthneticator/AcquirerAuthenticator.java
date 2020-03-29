package com.i2c.services.acqinstauthneticator;

import java.sql.Connection;
import com.i2c.services.util.*;

public class AcquirerAuthenticator {

  private AuthenticatorRequestObj requestObj = null;
  private Connection dbConn = null;
  public AcquirerAuthenticator(AuthenticatorRequestObj requestObj,
                               Connection dbConn) {
    this.requestObj = requestObj;
    this.dbConn = dbConn;
  }

  public AuthenticatorResponseObj authenticate() {

    AuthenticatorDatabaseHandler dbHndlr = null;
    AuthenticatorResponseObj respObj = new AuthenticatorResponseObj();
    ErrorInfoObj errObj = null;

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "AcquirerAuthenticator --- authenticate --- Method for authenticating acquirer information --- Acq Id--->" +
                                    requestObj.getAcquirerId() +
                                    "<---Card Bin--->" +
                                    requestObj.getCardBin() +
                                    "<---Service ID--->" +
                                    requestObj.getServiceId()
                                    + "<---Trans Type--->" +
                                    requestObj.getTransTypeId());
    try {

      dbHndlr = new AuthenticatorDatabaseHandler(this.dbConn);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AcquirerAuthenticator --- authenticate --- Method for authenticating acquirer information --- getting instance information against provided BIN--->" +
                                      requestObj.getCardBin());
      if (requestObj == null) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for authenticating acquirer information ---  Mandatory Field missing, Request Object is NULL...");
        respObj.setResponseCode("30");
        respObj.setResponseDesc(
            "Mandatory Field missing, Request Object is NULL");

        errObj = new ErrorInfoObj();
        errObj.setRespCode(respObj.getResponseCode());
        errObj.setRespDesc(respObj.getResponseDesc());
        errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
            INFORMIX_DATE_FORMAT));
        errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
            TIME_FORMAT));

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for authenticating acquirer information ---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request --- Logging error transaction ...");
        dbHndlr.logErrorTransaction(errObj);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for authenticating acquirer information ---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request --- Transaction Logged--->" +
                                        errObj.getSerialNo());

        respObj.setErrSerialNo(errObj.getSerialNo());
        return respObj;
      }
      if (requestObj.getCardBin() != null &&
          requestObj.getCardBin().trim().length() > 0) {
        respObj = performBinBasedProcessing();
        if(respObj == null || respObj.getResponseCode() == null){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for authenticating acquirer information ---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request --- Transaction Logged--->" +
                                        errObj.getSerialNo());
          throw new Exception("Invalid response received...");
        }
        return respObj;
      }else if(requestObj.getInstanceId() != null &&
               requestObj.getInstanceId().trim().length() > 0) {
        respObj = performInstanceBasedProcessing();
        if(respObj == null || respObj.getResponseCode() == null){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                          "AcquirerAuthenticator --- authenticate --- Method for authenticating acquirer information ---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request --- Transaction Logged--->" +
                                          errObj.getSerialNo());
          throw new Exception("Invalid response received...");
        }
        return respObj;
      }else{
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for authenticating acquirer information ---  Mandatory field missing, card bin ...");
        respObj.setResponseCode("30");
        respObj.setResponseDesc("Mandatory field missing, Card Bin & Instance ID missing");

        errObj = new ErrorInfoObj();
        errObj.setRespCode(respObj.getResponseCode());
        errObj.setRespDesc(respObj.getResponseDesc());
        errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
            INFORMIX_DATE_FORMAT));
        errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
            TIME_FORMAT));
        errObj.setBin(requestObj.getCardBin());
        errObj.setAcquirerId(requestObj.getAcquirerId());
        errObj.setServId(requestObj.getServiceId());
        errObj.setTransType(requestObj.getTransTypeId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for authenticating acquirer information ---  Mandatory field missing, card bin --- Logging error transaction ...");
        dbHndlr.logErrorTransaction(errObj);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for authenticating acquirer information ---  Mandatory field missing, card bin --- Transaction Logged--->" +
                                        errObj.getSerialNo());

        respObj.setErrSerialNo(errObj.getSerialNo());
        return respObj;
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AcquirerAuthenticator --- authenticate --- Exception in authenticating acquirer information --->" +
                                      ex);
      respObj.setResponseCode("96");
      respObj.setResponseDesc("System Error--->" + ex.getMessage());

      errObj = new ErrorInfoObj();
      errObj.setRespCode(respObj.getResponseCode());
      errObj.setRespDesc(respObj.getResponseDesc());
      errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
          INFORMIX_DATE_FORMAT));
      errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
          TIME_FORMAT));
      errObj.setBin(requestObj.getCardBin());
      errObj.setAcquirerId(requestObj.getAcquirerId());
      errObj.setServId(requestObj.getServiceId());
      errObj.setTransType(requestObj.getTransTypeId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AcquirerAuthenticator --- authenticate --- Exception in authenticating acquirer information --- Logging error transaction ...");
      dbHndlr.logErrorTransaction(errObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AcquirerAuthenticator --- authenticate --- Exception in authenticating acquirer information --- Transaction Logged--->" +
                                      errObj.getSerialNo());

      respObj.setErrSerialNo(errObj.getSerialNo());
      return respObj;
    }
  }

  private AuthenticatorResponseObj performBinBasedProcessing() throws Exception{
    AuthenticatorDatabaseHandler dbHndlr = null;
    InstanceInfoObj instInfo = null;
    AcquirerInfoObj acqInfo = null;
    AuthenticatorResponseObj respObj = new AuthenticatorResponseObj();
    ErrorInfoObj errObj = null;

    try {
      dbHndlr = new AuthenticatorDatabaseHandler(this.dbConn);
      instInfo = dbHndlr.getBINInstanceInfo(requestObj.getCardBin());
      if (instInfo != null) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for authenticating acquirer information ---Instance info got --- Instance Id--->" +
                                        instInfo.getInstanceId()
                                        + "<---Conn Str--->" +
                                        instInfo.getConnStr()
                                        + "<---Conn Usr--->" +
                                        instInfo.getConnUsr()
                                        + "<---Conn Pwd--->" +
                                        instInfo.getConnPwd());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for authenticating acquirer information ---Getting Acquirer Info for provided acquirer ID --->" +
                                        requestObj.getAcquirerId());
        if (requestObj.getAcquirerId() != null &&
            requestObj.getAcquirerId().trim().length() > 0) {

          acqInfo = dbHndlr.getAcquirerInfo(requestObj.getAcquirerId());
          if (acqInfo != null) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "AcquirerAuthenticator --- performBinBasedProcessing --- Method for getting acquirer information ---Acquirer info got --- Acquirer Id--->" +
                                            acqInfo.getAcqID()
                                            + "<---Acquirer User Id--->" +
                                            acqInfo.getAcqUserID()
                                            + "<---Acquirer User Pwd--->" +
                                            acqInfo.getAcqUserPassword()
                                            + "<---SecurityKey1--->" +
                                            acqInfo.getSecurityKey1()
                                            + "<---SecurityKey2--->" +
                                            acqInfo.getSecurityKey2()
                                            + "<---SecurityKey3--->" +
                                            acqInfo.getSecurityKey3()
                                            + "<---Algo Code--->" +
                                            acqInfo.getAlgoCode());

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "AcquirerAuthenticator --- performBinBasedProcessing --- Method for authenticating acquirer information --- Checking if Instance fetched is allowed to provided Acquirer ID --- Instance ID--->" +
                                            instInfo.getInstanceId() +
                                            "<---Acquirer ID--->" +
                                            requestObj.getAcquirerId());
            if (instInfo.getInstanceId() != null &&
                instInfo.getInstanceId().trim().length() > 0) {
              if (dbHndlr.checkInstanceAllowed(requestObj.getAcquirerId(),
                                               instInfo.getInstanceId())) {
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_FINEST),
                                                "AcquirerAuthenticator --- performBinBasedProcessing --- Method for authenticating acquirer information ---  Instance fetched is allowed to provided Acquirer ID ...");
                if (requestObj.getServiceId() != null &&
                    requestObj.getServiceId().trim().length() > 0) {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performBinBasedProcessing --- Method for authenticating acquirer information ---  Checking if provided service is allowed to provided Acquirer ID ...");
                  if (dbHndlr.checkServiceAllowed(requestObj.getAcquirerId(),
                                                  requestObj.getServiceId())) {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for authenticating acquirer information ---  Provided service is allowed to provided Acquirer ID ...");
                    respObj.setResponseCode("00");
                    respObj.setResponseDesc("Successfully Authenticated");
                    respObj.setAcqObj(acqInfo);
                    respObj.setInstObj(instInfo);
                    return respObj;
                  }else {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for authenticating acquirer information ---  Provided service is not allowed to provided Acquirer ID ...");
                    respObj.setResponseCode("01");
                    respObj.setResponseDesc("Service (" +
                                            requestObj.getServiceId() +
                                            ") not allowed to acquirer---> " +
                                            requestObj.getAcquirerId());

                    errObj = new ErrorInfoObj();
                    errObj.setRespCode(respObj.getResponseCode());
                    errObj.setRespDesc(respObj.getResponseDesc());
                    errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                        Constants.INFORMIX_DATE_FORMAT));
                    errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                        Constants.TIME_FORMAT));
                    errObj.setBin(requestObj.getCardBin());
                    errObj.setAcquirerId(requestObj.getAcquirerId());
                    errObj.setServId(requestObj.getServiceId());
                    errObj.setTransType(requestObj.getTransTypeId());

                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing---  Provided service is not allowed to provided Acquirer ID --- Logging error transaction ...");
                    dbHndlr.logErrorTransaction(errObj);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Provided service is not allowed to provided Acquirer ID --- Transaction Logged--->" +
                        errObj.getSerialNo());

                    respObj.setErrSerialNo(errObj.getSerialNo());
                    return respObj;
                  }
                }else if (requestObj.getTransTypeId() != null &&
                         requestObj.getTransTypeId().trim().length() > 0) {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  No service id is provided in request, getting service id from provided trans type --->" +
                                                  requestObj.getTransTypeId());
                  String serviceId = dbHndlr.getServiceId(requestObj.
                      getTransTypeId(), requestObj.getDeviceType(), "00");

                  if (serviceId == null) {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing---  No service fetched for provided transaction type...");
                    respObj.setResponseCode("ST");
                    respObj.setResponseDesc(
                        "No service fetched for provided transaction type---> " +
                        requestObj.getTransTypeId());

                    errObj = new ErrorInfoObj();
                    errObj.setRespCode(respObj.getResponseCode());
                    errObj.setRespDesc(respObj.getResponseDesc());
                    errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                        Constants.INFORMIX_DATE_FORMAT));
                    errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                        Constants.TIME_FORMAT));
                    errObj.setBin(requestObj.getCardBin());
                    errObj.setAcquirerId(requestObj.getAcquirerId());
                    errObj.setServId(requestObj.getServiceId());
                    errObj.setTransType(requestObj.getTransTypeId());

                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  No service fetched for provided transaction type --- Logging error transaction ...");
                    dbHndlr.logErrorTransaction(errObj);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  No service fetched for provided transaction type --- Transaction Logged--->" +
                        errObj.getSerialNo());

                    respObj.setErrSerialNo(errObj.getSerialNo());
                    return respObj;
                  }

                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Service Id found --->" +
                                                  serviceId);
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing---  Checking if fetched service is allowed to provided Acquirer ID ...");
                  if (dbHndlr.checkServiceAllowed(requestObj.getAcquirerId(),
                                                  serviceId)) {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Fetched service is allowed to provided Acquirer ID ...");
                    respObj.setResponseCode("00");
                    respObj.setResponseDesc("Successfully Authenticated");
                    respObj.setAcqObj(acqInfo);
                    respObj.setServiceId(serviceId);
                    respObj.setInstObj(instInfo);
                    return respObj;
                  }else {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Fetched service is not allowed to provided Acquirer ID ...");
                    respObj.setResponseCode("01");
                    respObj.setResponseDesc("Service (" + serviceId +
                                            ") not allowed to acquirer---> " +
                                            requestObj.getAcquirerId());

                    errObj = new ErrorInfoObj();
                    errObj.setRespCode(respObj.getResponseCode());
                    errObj.setRespDesc(respObj.getResponseDesc());
                    errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                        Constants.INFORMIX_DATE_FORMAT));
                    errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                        Constants.TIME_FORMAT));
                    errObj.setBin(requestObj.getCardBin());
                    errObj.setAcquirerId(requestObj.getAcquirerId());
                    errObj.setServId(requestObj.getServiceId());
                    errObj.setTransType(requestObj.getTransTypeId());

                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Fetched service is not allowed to provided Acquirer ID --- Logging error transaction ...");
                    dbHndlr.logErrorTransaction(errObj);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Fetched service is not allowed to provided Acquirer ID --- Transaction Logged--->" +
                        errObj.getSerialNo());

                    respObj.setErrSerialNo(errObj.getSerialNo());
                    return respObj;
                  }
                }else {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request...");
                  respObj.setResponseCode("30");
                  respObj.setResponseDesc("Mandatory Field missing, Either transaction type id or Service ID must be provided in request");

                  errObj = new ErrorInfoObj();
                  errObj.setRespCode(respObj.getResponseCode());
                  errObj.setRespDesc(respObj.getResponseDesc());
                  errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                      Constants.INFORMIX_DATE_FORMAT));
                  errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                      Constants.TIME_FORMAT));
                  errObj.setBin(requestObj.getCardBin());
                  errObj.setAcquirerId(requestObj.getAcquirerId());
                  errObj.setServId(requestObj.getServiceId());
                  errObj.setTransType(requestObj.getTransTypeId());

                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request --- Logging error transaction ...");
                  dbHndlr.logErrorTransaction(errObj);
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request --- Transaction Logged--->" +
                                                  errObj.getSerialNo());

                  respObj.setErrSerialNo(errObj.getSerialNo());
                  return respObj;
                }
              }else {
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_FINEST),
                                                "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Instance fetched is not allowed to provided Acquirer ID ...");
                respObj.setResponseCode("ID");
                respObj.setResponseDesc("Instance (" + instInfo.getInstanceId() +
                                        ") not allowed to acquirer---> " +
                                        requestObj.getAcquirerId());

                errObj = new ErrorInfoObj();
                errObj.setRespCode(respObj.getResponseCode());
                errObj.setRespDesc(respObj.getResponseDesc());
                errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                    Constants.INFORMIX_DATE_FORMAT));
                errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                    Constants.TIME_FORMAT));
                errObj.setBin(requestObj.getCardBin());
                errObj.setAcquirerId(requestObj.getAcquirerId());
                errObj.setServId(requestObj.getServiceId());
                errObj.setTransType(requestObj.getTransTypeId());

                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_FINEST),
                                                "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing---  Instance fetched is not allowed to provided Acquirer ID --- Logging error transaction ...");
                dbHndlr.logErrorTransaction(errObj);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_FINEST),
                                                "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Instance fetched is not allowed to provided Acquirer ID --- Transaction Logged--->" +
                                                errObj.getSerialNo());

                respObj.setErrSerialNo(errObj.getSerialNo());
                return respObj;
              }
            }else {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                              "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Invalid Instance ID got from database for provided BIN ...");
              respObj.setResponseCode("06");
              respObj.setResponseDesc(
                  "Invalid Instance ID got from database for provided BIN --->" +
                  instInfo.getInstanceId());

              errObj = new ErrorInfoObj();
              errObj.setRespCode(respObj.getResponseCode());
              errObj.setRespDesc(respObj.getResponseDesc());
              errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                  Constants.INFORMIX_DATE_FORMAT));
              errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                  Constants.TIME_FORMAT));
              errObj.setBin(requestObj.getCardBin());
              errObj.setAcquirerId(requestObj.getAcquirerId());
              errObj.setServId(requestObj.getServiceId());
              errObj.setTransType(requestObj.getTransTypeId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                              "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing---  Invalid Instance ID got from database for provided BIN --- Logging error transaction ...");
              dbHndlr.logErrorTransaction(errObj);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                              "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing---  Invalid Instance ID got from database for provided BIN --- Transaction Logged--->" +
                                              errObj.getSerialNo());

              respObj.setErrSerialNo(errObj.getSerialNo());
              return respObj;
            }
          }else {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing---  No Acquirer information found for provided acquirer ID ...");
            respObj.setResponseCode("E9");
            respObj.setResponseDesc("Invalid acquirer ID provided");

            errObj = new ErrorInfoObj();
            errObj.setRespCode(respObj.getResponseCode());
            errObj.setRespDesc(respObj.getResponseDesc());
            errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
                INFORMIX_DATE_FORMAT));
            errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
                TIME_FORMAT));
            errObj.setBin(requestObj.getCardBin());
            errObj.setAcquirerId(requestObj.getAcquirerId());
            errObj.setServId(requestObj.getServiceId());
            errObj.setTransType(requestObj.getTransTypeId());

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing---  No Acquirer information found for provided acquirer ID --- Logging error transaction ...");
            dbHndlr.logErrorTransaction(errObj);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing---  No Acquirer information found for provided acquirer ID --- Transaction Logged--->" +
                                            errObj.getSerialNo());

            respObj.setErrSerialNo(errObj.getSerialNo());
            return respObj;
          }
        }else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
                                          "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing---  Mandatory field missing, Acquirer ID...");
          respObj.setResponseCode("30");
          respObj.setResponseDesc("Mandatory field missing, Acquirer ID");

          errObj = new ErrorInfoObj();
          errObj.setRespCode(respObj.getResponseCode());
          errObj.setRespDesc(respObj.getResponseDesc());
          errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
              INFORMIX_DATE_FORMAT));
          errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
              TIME_FORMAT));
          errObj.setBin(requestObj.getCardBin());
          errObj.setAcquirerId(requestObj.getAcquirerId());
          errObj.setServId(requestObj.getServiceId());
          errObj.setTransType(requestObj.getTransTypeId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
                                          "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Mandatory field missing, Acquirer ID--- Logging error transaction ...");
          dbHndlr.logErrorTransaction(errObj);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
                                          "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  Mandatory field missing, Acquirer ID--- Transaction Logged--->" +
                                          errObj.getSerialNo());

          respObj.setErrSerialNo(errObj.getSerialNo());
          return respObj;
        }
      }else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  No Instance information found for provided card bin ...");
        respObj.setResponseCode("IB");
        respObj.setResponseDesc(
            "No Instance information found for provided card BIN");

        errObj = new ErrorInfoObj();
        errObj.setRespCode(respObj.getResponseCode());
        errObj.setRespDesc(respObj.getResponseDesc());
        errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
            INFORMIX_DATE_FORMAT));
        errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
            TIME_FORMAT));
        errObj.setBin(requestObj.getCardBin());
        errObj.setAcquirerId(requestObj.getAcquirerId());
        errObj.setServId(requestObj.getServiceId());
        errObj.setTransType(requestObj.getTransTypeId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  No Instance information found for provided card bin --- Logging error transaction ...");
        dbHndlr.logErrorTransaction(errObj);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- performBinBasedProcessing --- Method for performing BIN based processing ---  No Instance information found for provided card bin --- Transaction Logged--->" +
                                        errObj.getSerialNo());

        respObj.setErrSerialNo(errObj.getSerialNo());
        return respObj;
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_WARNING),
                                      "AcquirerAuthenticator --- performBinBasedProcessing --- Exception in performing BIN based processing--->" +
                                      ex);
      throw ex;
    }
  }

  private AuthenticatorResponseObj performInstanceBasedProcessing() throws Exception{
    AuthenticatorDatabaseHandler dbHndlr = null;
    InstanceInfoObj instInfo = null;
    AcquirerInfoObj acqInfo = null;
    AuthenticatorResponseObj respObj = new AuthenticatorResponseObj();
    ErrorInfoObj errObj = null;
    try {
      dbHndlr = new AuthenticatorDatabaseHandler(this.dbConn);
      instInfo = dbHndlr.getInstanceInfo(requestObj.getInstanceId());
      if (instInfo != null) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---Instance info got --- Instance Id--->" +
                                        instInfo.getInstanceId()
                                        + "<---Conn Str--->" +
                                        instInfo.getConnStr()
                                        + "<---Conn Usr--->" +
                                        instInfo.getConnUsr()
                                        + "<---Conn Pwd--->" +
                                        instInfo.getConnPwd());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---Getting Acquirer Info for provided acquirer ID --->" +
                                        requestObj.getAcquirerId());
        if (requestObj.getAcquirerId() != null &&
            requestObj.getAcquirerId().trim().length() > 0) {

          acqInfo = dbHndlr.getAcquirerInfo(requestObj.getAcquirerId());
          if (acqInfo != null) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---Acquirer info got --- Acquirer Id--->" +
                                            acqInfo.getAcqID()
                                            + "<---Acquirer User Id--->" +
                                            acqInfo.getAcqUserID()
                                            + "<---Acquirer User Pwd--->" +
                                            acqInfo.getAcqUserPassword()
                                            + "<---SecurityKey1--->" +
                                            acqInfo.getSecurityKey1()
                                            + "<---SecurityKey2--->" +
                                            acqInfo.getSecurityKey2()
                                            + "<---SecurityKey3--->" +
                                            acqInfo.getSecurityKey3()
                                            + "<---Algo Code--->" +
                                            acqInfo.getAlgoCode());

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing --- Checking if Instance fetched is allowed to provided Acquirer ID --- Instance ID--->" +
                                            instInfo.getInstanceId() +
                                            "<---Acquirer ID--->" +
                                            requestObj.getAcquirerId());
            if (instInfo.getInstanceId() != null &&
                instInfo.getInstanceId().trim().length() > 0) {
              if (dbHndlr.checkInstanceAllowed(requestObj.getAcquirerId(),
                                               instInfo.getInstanceId())) {
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_FINEST),
                                                "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Instance fetched is allowed to provided Acquirer ID ...");
                if (requestObj.getServiceId() != null &&
                    requestObj.getServiceId().trim().length() > 0) {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  Checking if provided service is allowed to provided Acquirer ID ...");
                  if (dbHndlr.checkServiceAllowed(requestObj.getAcquirerId(),
                                                  requestObj.getServiceId())) {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Provided service is allowed to provided Acquirer ID ...");
                    respObj.setResponseCode("00");
                    respObj.setResponseDesc("Successfully Authenticated");
                    respObj.setAcqObj(acqInfo);
                    respObj.setInstObj(instInfo);
                    return respObj;
                  }else {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Provided service is not allowed to provided Acquirer ID ...");
                    respObj.setResponseCode("01");
                    respObj.setResponseDesc("Service (" +
                                            requestObj.getServiceId() +
                                            ") not allowed to acquirer---> " +
                                            requestObj.getAcquirerId());

                    errObj = new ErrorInfoObj();
                    errObj.setRespCode(respObj.getResponseCode());
                    errObj.setRespDesc(respObj.getResponseDesc());
                    errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                        Constants.INFORMIX_DATE_FORMAT));
                    errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                        Constants.TIME_FORMAT));
                    errObj.setBin(requestObj.getCardBin());
                    errObj.setAcquirerId(requestObj.getAcquirerId());
                    errObj.setServId(requestObj.getServiceId());
                    errObj.setTransType(requestObj.getTransTypeId());

                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  Provided service is not allowed to provided Acquirer ID --- Logging error transaction ...");
                    dbHndlr.logErrorTransaction(errObj);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  Provided service is not allowed to provided Acquirer ID --- Transaction Logged--->" +
                        errObj.getSerialNo());

                    respObj.setErrSerialNo(errObj.getSerialNo());
                    return respObj;
                  }
                }else if (requestObj.getTransTypeId() != null &&
                         requestObj.getTransTypeId().trim().length() > 0) {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  No service id is provided in request, getting service id from provided trans type --->" +
                                                  requestObj.getTransTypeId());
                  String serviceId = dbHndlr.getServiceId(requestObj.
                      getTransTypeId(), requestObj.getDeviceType(), "00");

                  if (serviceId == null) {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  No service fetched for provided transaction type...");
                    respObj.setResponseCode("ST");
                    respObj.setResponseDesc(
                        "No service fetched for provided transaction type---> " +
                        requestObj.getTransTypeId());

                    errObj = new ErrorInfoObj();
                    errObj.setRespCode(respObj.getResponseCode());
                    errObj.setRespDesc(respObj.getResponseDesc());
                    errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                        Constants.INFORMIX_DATE_FORMAT));
                    errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                        Constants.TIME_FORMAT));
                    errObj.setBin(requestObj.getCardBin());
                    errObj.setAcquirerId(requestObj.getAcquirerId());
                    errObj.setServId(requestObj.getServiceId());
                    errObj.setTransType(requestObj.getTransTypeId());

                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  No service fetched for provided transaction type --- Logging error transaction ...");
                    dbHndlr.logErrorTransaction(errObj);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  No service fetched for provided transaction type --- Transaction Logged--->" +
                        errObj.getSerialNo());

                    respObj.setErrSerialNo(errObj.getSerialNo());
                    return respObj;
                  }
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  Service Id found --->" +
                                                  serviceId);
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  Checking if fetched service is allowed to provided Acquirer ID ...");
                  if (dbHndlr.checkServiceAllowed(requestObj.getAcquirerId(),
                                                  serviceId)) {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Fetched service is allowed to provided Acquirer ID ...");
                    respObj.setResponseCode("00");
                    respObj.setResponseDesc("Successfully Authenticated");
                    respObj.setAcqObj(acqInfo);
                    respObj.setServiceId(serviceId);
                    respObj.setInstObj(instInfo);
                    return respObj;
                  }else {
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  Fetched service is not allowed to provided Acquirer ID ...");
                    respObj.setResponseCode("01");
                    respObj.setResponseDesc("Service (" + serviceId +
                                            ") not allowed to acquirer---> " +
                                            requestObj.getAcquirerId());

                    errObj = new ErrorInfoObj();
                    errObj.setRespCode(respObj.getResponseCode());
                    errObj.setRespDesc(respObj.getResponseDesc());
                    errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                        Constants.INFORMIX_DATE_FORMAT));
                    errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                        Constants.TIME_FORMAT));
                    errObj.setBin(requestObj.getCardBin());
                    errObj.setAcquirerId(requestObj.getAcquirerId());
                    errObj.setServId(requestObj.getServiceId());
                    errObj.setTransType(requestObj.getTransTypeId());

                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Fetched service is not allowed to provided Acquirer ID --- Logging error transaction ...");
                    dbHndlr.logErrorTransaction(errObj);
                    CommonUtilities.getLogger().log(LogLevel.getLevel(
                        Constants.LOG_FINEST),
                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Fetched service is not allowed to provided Acquirer ID --- Transaction Logged--->" +
                        errObj.getSerialNo());

                    respObj.setErrSerialNo(errObj.getSerialNo());
                    return respObj;
                  }
                }
                else {
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request...");
                  respObj.setResponseCode("30");
                  respObj.setResponseDesc("Mandatory Field missing, Either transaction type id or Service ID must be provided in request");

                  errObj = new ErrorInfoObj();
                  errObj.setRespCode(respObj.getResponseCode());
                  errObj.setRespDesc(respObj.getResponseDesc());
                  errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                      Constants.INFORMIX_DATE_FORMAT));
                  errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                      Constants.TIME_FORMAT));
                  errObj.setBin(requestObj.getCardBin());
                  errObj.setAcquirerId(requestObj.getAcquirerId());
                  errObj.setServId(requestObj.getServiceId());
                  errObj.setTransType(requestObj.getTransTypeId());

                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request --- Logging error transaction ...");
                  dbHndlr.logErrorTransaction(errObj);
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_FINEST),
                                                  "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request --- Transaction Logged--->" +
                                                  errObj.getSerialNo());

                  respObj.setErrSerialNo(errObj.getSerialNo());
                  return respObj;
                }
              }else {
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_FINEST),
                                                "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Instance fetched is not allowed to provided Acquirer ID ...");
                respObj.setResponseCode("ID");
                respObj.setResponseDesc("Instance (" + instInfo.getInstanceId() +
                                        ") not allowed to acquirer---> " +
                                        requestObj.getAcquirerId());

                errObj = new ErrorInfoObj();
                errObj.setRespCode(respObj.getResponseCode());
                errObj.setRespDesc(respObj.getResponseDesc());
                errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                    Constants.INFORMIX_DATE_FORMAT));
                errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                    Constants.TIME_FORMAT));
                errObj.setBin(requestObj.getCardBin());
                errObj.setAcquirerId(requestObj.getAcquirerId());
                errObj.setServId(requestObj.getServiceId());
                errObj.setTransType(requestObj.getTransTypeId());

                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_FINEST),
                                                "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Instance fetched is not allowed to provided Acquirer ID --- Logging error transaction ...");
                dbHndlr.logErrorTransaction(errObj);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_FINEST),
                                                "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Instance fetched is not allowed to provided Acquirer ID --- Transaction Logged--->" +
                                                errObj.getSerialNo());

                respObj.setErrSerialNo(errObj.getSerialNo());
                return respObj;
              }
            }else {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                              "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Invalid Instance ID got from database for provided BIN ...");
              respObj.setResponseCode("06");
              respObj.setResponseDesc(
                  "Invalid Instance ID got from database for provided BIN --->" +
                  instInfo.getInstanceId());

              errObj = new ErrorInfoObj();
              errObj.setRespCode(respObj.getResponseCode());
              errObj.setRespDesc(respObj.getResponseDesc());
              errObj.setLogDate(CommonUtilities.getCurrentFormatDate(
                  Constants.INFORMIX_DATE_FORMAT));
              errObj.setLogTime(CommonUtilities.getCurrentFormatDate(
                  Constants.TIME_FORMAT));
              errObj.setBin(requestObj.getCardBin());
              errObj.setAcquirerId(requestObj.getAcquirerId());
              errObj.setServId(requestObj.getServiceId());
              errObj.setTransType(requestObj.getTransTypeId());

              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                              "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  Invalid Instance ID got from database for provided BIN --- Logging error transaction ...");
              dbHndlr.logErrorTransaction(errObj);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                              "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  Invalid Instance ID got from database for provided BIN --- Transaction Logged--->" +
                                              errObj.getSerialNo());

              respObj.setErrSerialNo(errObj.getSerialNo());
              return respObj;
            }
          }else {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  No Acquirer information found for provided acquirer ID ...");
            respObj.setResponseCode("E9");
            respObj.setResponseDesc("Invalid acquirer ID provided");

            errObj = new ErrorInfoObj();
            errObj.setRespCode(respObj.getResponseCode());
            errObj.setRespDesc(respObj.getResponseDesc());
            errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
                INFORMIX_DATE_FORMAT));
            errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
                TIME_FORMAT));
            errObj.setBin(requestObj.getCardBin());
            errObj.setAcquirerId(requestObj.getAcquirerId());
            errObj.setServId(requestObj.getServiceId());
            errObj.setTransType(requestObj.getTransTypeId());

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  No Acquirer information found for provided acquirer ID --- Logging error transaction ...");
            dbHndlr.logErrorTransaction(errObj);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                                            "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  No Acquirer information found for provided acquirer ID --- Transaction Logged--->" +
                                            errObj.getSerialNo());

            respObj.setErrSerialNo(errObj.getSerialNo());
            return respObj;
          }

        }else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
                                          "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Mandatory field missing, Acquirer ID...");
          respObj.setResponseCode("30");
          respObj.setResponseDesc("Mandatory field missing, Acquirer ID");

          errObj = new ErrorInfoObj();
          errObj.setRespCode(respObj.getResponseCode());
          errObj.setRespDesc(respObj.getResponseDesc());
          errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
              INFORMIX_DATE_FORMAT));
          errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
              TIME_FORMAT));
          errObj.setBin(requestObj.getCardBin());
          errObj.setAcquirerId(requestObj.getAcquirerId());
          errObj.setServId(requestObj.getServiceId());
          errObj.setTransType(requestObj.getTransTypeId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
                                          "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Mandatory field missing, Acquirer ID--- Logging error transaction ...");
          dbHndlr.logErrorTransaction(errObj);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
                                          "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  Mandatory field missing, Acquirer ID--- Transaction Logged--->" +
                                          errObj.getSerialNo());

          respObj.setErrSerialNo(errObj.getSerialNo());
          return respObj;
        }
      }else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing ---  No Instance information found for provided ID ...");
        respObj.setResponseCode("IB");
        respObj.setResponseDesc(
            "No Instance information found for provided ID");

        errObj = new ErrorInfoObj();
        errObj.setRespCode(respObj.getResponseCode());
        errObj.setRespDesc(respObj.getResponseDesc());
        errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
            INFORMIX_DATE_FORMAT));
        errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
            TIME_FORMAT));
        errObj.setBin(requestObj.getCardBin());
        errObj.setAcquirerId(requestObj.getAcquirerId());
        errObj.setServId(requestObj.getServiceId());
        errObj.setTransType(requestObj.getTransTypeId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  No Instance information found for provided ID --- Logging error transaction ...");
        dbHndlr.logErrorTransaction(errObj);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- performInstanceBasedProcessing --- Method for performing instance based processing---  No Instance information found for provided ID --- Transaction Logged--->" +
                                        errObj.getSerialNo());

        respObj.setErrSerialNo(errObj.getSerialNo());
        return respObj;
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_FINEST),
                                      "AcquirerAuthenticator --- performInstanceBasedProcessing --- Exception in performing instance based processing --->" +
                                      ex);
      throw ex;
    }
  }

  public AuthenticatorResponseObj getAcquirerInfo() {
    AuthenticatorDatabaseHandler dbHndlr = null;
    InstanceInfoObj instInfo = null;
    AcquirerInfoObj acqInfo = null;
    AuthenticatorResponseObj respObj = new AuthenticatorResponseObj();
    ErrorInfoObj errObj = null;

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "AcquirerAuthenticator --- authenticate --- Method for getting acquirer information --- Acq Id--->" +
                                    requestObj.getAcquirerId());
    try {

      dbHndlr = new AuthenticatorDatabaseHandler(this.dbConn);

      if (requestObj == null) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for getting acquirer information ---  Mandatory Field missing, Request Object is NULL...");
        respObj.setResponseCode("30");
        respObj.setResponseDesc(
            "Mandatory Field missing, Request Object is NULL");

        errObj = new ErrorInfoObj();
        errObj.setRespCode(respObj.getResponseCode());
        errObj.setRespDesc(respObj.getResponseDesc());
        errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
            INFORMIX_DATE_FORMAT));
        errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
            TIME_FORMAT));
//        errObj.setBin(requestObj.getCardBin());
//        errObj.setAcquirerId(requestObj.getAcquirerId());
//        errObj.setServId(requestObj.getServiceId());
//        errObj.setTransType(requestObj.getTransTypeId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for getting acquirer information ---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request --- Logging error transaction ...");
        dbHndlr.logErrorTransaction(errObj);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for getting acquirer information ---  Mandatory Field missing, Either transaction type id or Service ID must be provided in request --- Transaction Logged--->" +
                                        errObj.getSerialNo());

        respObj.setErrSerialNo(errObj.getSerialNo());
        return respObj;
      }
      if (requestObj.getAcquirerId() != null &&
          requestObj.getAcquirerId().trim().length() > 0) {
        acqInfo = dbHndlr.getAcquirerInfo(requestObj.getAcquirerId());
        if (acqInfo != null) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
                                          "AcquirerAuthenticator --- authenticate --- Method for getting acquirer information ---Acquirer info got --- Acquirer Id--->" +
                                          acqInfo.getAcqID()
                                          + "<---Acquirer User Id--->" +
                                          acqInfo.getAcqUserID()
                                          + "<---Acquirer User Pwd--->" +
                                          acqInfo.getAcqUserPassword()
                                          + "<---SecurityKey1--->" +
                                          acqInfo.getSecurityKey1()
                                          + "<---SecurityKey2--->" +
                                          acqInfo.getSecurityKey2()
                                          + "<---SecurityKey3--->" +
                                          acqInfo.getSecurityKey3()
                                          + "<---Algo Code--->" +
                                          acqInfo.getAlgoCode());
          respObj.setResponseCode("00");
          respObj.setResponseDesc("Aquirer information successfully fetched");
          respObj.setAcqObj(acqInfo);
          return respObj;
        }
        else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
                                          "AcquirerAuthenticator --- authenticate --- Method for getting acquirer information ---  No Acquirer information found for provided acquirer ID ...");
          respObj.setResponseCode("E9");
          respObj.setResponseDesc("Invalid acquirer ID provided");

          errObj = new ErrorInfoObj();
          errObj.setRespCode(respObj.getResponseCode());
          errObj.setRespDesc(respObj.getResponseDesc());
          errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
              INFORMIX_DATE_FORMAT));
          errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
              TIME_FORMAT));
          errObj.setBin(requestObj.getCardBin());
          errObj.setAcquirerId(requestObj.getAcquirerId());
          errObj.setServId(requestObj.getServiceId());
          errObj.setTransType(requestObj.getTransTypeId());

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
                                          "AcquirerAuthenticator --- authenticate --- Method for getting acquirer information ---  No Acquirer information found for provided acquirer ID --- Logging error transaction ...");
          dbHndlr.logErrorTransaction(errObj);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
                                          "AcquirerAuthenticator --- authenticate --- Method for getting acquirer information ---  No Acquirer information found for provided acquirer ID --- Transaction Logged--->" +
                                          errObj.getSerialNo());

          respObj.setErrSerialNo(errObj.getSerialNo());
          return respObj;
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for getting acquirer information ---  Mandatory field missing, Acquirer ID...");
        respObj.setResponseCode("30");
        respObj.setResponseDesc("Mandatory field missing, Acquirer ID");

        errObj = new ErrorInfoObj();
        errObj.setRespCode(respObj.getResponseCode());
        errObj.setRespDesc(respObj.getResponseDesc());
        errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
            INFORMIX_DATE_FORMAT));
        errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
            TIME_FORMAT));
        errObj.setBin(requestObj.getCardBin());
        errObj.setAcquirerId(requestObj.getAcquirerId());
        errObj.setServId(requestObj.getServiceId());
        errObj.setTransType(requestObj.getTransTypeId());

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for getting acquirer information ---  Mandatory field missing, Acquirer ID--- Logging error transaction ...");
        dbHndlr.logErrorTransaction(errObj);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST),
                                        "AcquirerAuthenticator --- authenticate --- Method for getting acquirer information ---  Mandatory field missing, Acquirer ID--- Transaction Logged--->" +
                                        errObj.getSerialNo());

        respObj.setErrSerialNo(errObj.getSerialNo());
        return respObj;
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AcquirerAuthenticator --- authenticate --- Exception in getting acquirer information --->" +
                                      ex);
      respObj.setResponseCode("96");
      respObj.setResponseDesc("System Error--->" + ex.getMessage());

      errObj = new ErrorInfoObj();
      errObj.setRespCode(respObj.getResponseCode());
      errObj.setRespDesc(respObj.getResponseDesc());
      errObj.setLogDate(CommonUtilities.getCurrentFormatDate(Constants.
          INFORMIX_DATE_FORMAT));
      errObj.setLogTime(CommonUtilities.getCurrentFormatDate(Constants.
          TIME_FORMAT));
      errObj.setBin(requestObj.getCardBin());
      errObj.setAcquirerId(requestObj.getAcquirerId());
      errObj.setServId(requestObj.getServiceId());
      errObj.setTransType(requestObj.getTransTypeId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AcquirerAuthenticator --- authenticate --- Exception in getting acquirer information --- Logging error transaction ...");
      dbHndlr.logErrorTransaction(errObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AcquirerAuthenticator --- authenticate --- Exception in getting acquirer information --- Transaction Logged--->" +
                                      errObj.getSerialNo());

      respObj.setErrSerialNo(errObj.getSerialNo());
      return respObj;
    }
  }

}
