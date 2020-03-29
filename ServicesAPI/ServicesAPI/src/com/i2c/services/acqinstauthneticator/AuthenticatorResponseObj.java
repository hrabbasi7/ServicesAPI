package com.i2c.services.acqinstauthneticator;

public class AuthenticatorResponseObj {

    private String responseCode = null;
    private String responseDesc = null;
    private String errSerialNo = null;
    private String serviceId = null;
    private InstanceInfoObj instObj = null;
    private AcquirerInfoObj acqObj = null;



  public String getErrSerialNo() {
    return errSerialNo;
  }

  public String getResponseDesc() {
    return responseDesc;
  }

  public String getResponseCode() {
    return responseCode;
  }

  public AcquirerInfoObj getAcqObj() {
    return acqObj;
  }

  public InstanceInfoObj getInstObj() {
    return instObj;
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setErrSerialNo(String errSerialNo) {
    this.errSerialNo = errSerialNo;
  }

  public void setResponseDesc(String responseDesc) {
    this.responseDesc = responseDesc;
  }

  public void setResponseCode(String responseCode) {
    this.responseCode = responseCode;
  }

  public void setAcqObj(AcquirerInfoObj acqObj) {
    this.acqObj = acqObj;
  }

  public void setInstObj(InstanceInfoObj instObj) {
    this.instObj = instObj;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

}
