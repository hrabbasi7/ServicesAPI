package com.i2c.services.acqinstauthneticator;

public class ErrorInfoObj {
  private String serialNo = null;
  private String logDate = null;
  private String logTime = null;
  private String transType = null;
  private String servId = null;
  private String acquirerId = null;
  private String bin = null;
  private String respCode = null;
  private String respDesc = null;
  public String getLogDate() {
    return logDate;
  }

  public String getAcquirerId() {
    return acquirerId;
  }

  public String getRespDesc() {
    return respDesc;
  }

  public String getTransType() {
    return transType;
  }

  public String getSerialNo() {
    return serialNo;
  }

  public String getLogTime() {
    return logTime;
  }

  public String getBin() {
    return bin;
  }

  public String getRespCode() {
    return respCode;
  }

  public void setServId(String servId) {
    this.servId = servId;
  }

  public void setLogDate(String logDate) {
    this.logDate = logDate;
  }

  public void setAcquirerId(String acquirerId) {
    this.acquirerId = acquirerId;
  }

  public void setRespDesc(String respDesc) {
    this.respDesc = respDesc;
  }

  public void setTransType(String transType) {
    this.transType = transType;
  }

  public void setSerialNo(String serialNo) {
    this.serialNo = serialNo;
  }

  public void setLogTime(String logTime) {
    this.logTime = logTime;
  }

  public void setBin(String bin) {
    this.bin = bin;
  }

  public void setRespCode(String respCode) {
    this.respCode = respCode;
  }

  public String getServId() {
    return servId;
  }

}
