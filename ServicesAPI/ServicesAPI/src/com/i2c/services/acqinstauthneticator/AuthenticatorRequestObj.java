package com.i2c.services.acqinstauthneticator;

public class AuthenticatorRequestObj {
  private String cardBin = null;
  private String instanceId = null;
  private String acquirerId = null;
  private String serviceId = null;
  private String transTypeId = null;
  private String deviceType = null;

  public String getTransTypeId() {
    return transTypeId;
  }

  public String getAcquirerId() {
    return acquirerId;
  }

  public String getCardBin() {
    return cardBin;
  }

  public String getServiceId() {
    return serviceId;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public void setTransTypeId(String transTypeId) {
    this.transTypeId = transTypeId;
  }

  public void setAcquirerId(String acquirerId) {
    this.acquirerId = acquirerId;
  }

  public void setCardBin(String cardBin) {
    this.cardBin = cardBin;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }
}
