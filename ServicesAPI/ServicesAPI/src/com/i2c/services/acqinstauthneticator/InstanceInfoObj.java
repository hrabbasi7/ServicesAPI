package com.i2c.services.acqinstauthneticator;

public class InstanceInfoObj {
  private String instanceId = null;
  private String connStr = null;
  private String connUsr = null;
  private String connPwd = null;

  public String getInstanceId() {
    return instanceId;
  }

  public String getConnPwd() {
    return connPwd;
  }

  public String getConnStr() {
    return connStr;
  }

  public void setConnUsr(String connUsr) {
    this.connUsr = connUsr;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public void setConnPwd(String connPwd) {
    this.connPwd = connPwd;
  }

  public void setConnStr(String connStr) {
    this.connStr = connStr;
  }

  public String getConnUsr() {
    return connUsr;
  }

}
