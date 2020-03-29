package com.i2c.schedulerframework.model;

import java.util.ArrayList;
import java.io.Serializable;

/**
 *
 * <p>Title: Generic Scheduler Framework</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: i2cinc</p>
 *
 * @author hrabbasi
 * @version 1.0
 */

public class InstanceDbConfigBean
    implements Serializable {

  private String interfaceId;
  private String interfaceName;
  private String driverName;
  private String connString;
  private String userId;
  private String password;
  private String isActive;
  private ArrayList extBatchList;

  public void reset(){
    interfaceId = "";
    interfaceName = "";
    driverName = "";
    connString = "";
    userId = "";
    password = "";
    isActive = "";
    extBatchList = null;
  }

  public InstanceDbConfigBean() {
    reset();
  }

  public void setInterfaceId(String interfaceId) {
    this.interfaceId = interfaceId;
  }

  public void setInterfaceName(String interfaceName) {
    this.interfaceName = interfaceName;
  }

  public void setDriverName(String driverName) {
    this.driverName = driverName;
  }

  public void setConnString(String connString) {
    this.connString = connString;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setIsActive(String isActive) {
    this.isActive = isActive;
  }

  public void setExtBatchList(ArrayList extBatchList) {
    this.extBatchList = extBatchList;
  }

  public String getInterfaceId() {
    return interfaceId;
  }

  public String getInterfaceName() {
    return interfaceName;
  }

  public String getDriverName() {
    return driverName;
  }

  public String getConnString() {
    return connString;
  }

  public String getUserId() {
    return userId;
  }

  public String getPassword() {
    return password;
  }

  public String getIsActive() {
    return isActive;
  }

  public ArrayList getExtBatchList() {
    return extBatchList;
  }
}
