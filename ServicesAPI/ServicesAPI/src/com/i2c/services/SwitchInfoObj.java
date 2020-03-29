package com.i2c.services;

/**
 * <p>Title: SwitchInfoObj: A bean which holds information about switch </p>
 * <p>Description: This classes holds the switch information</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class SwitchInfoObj {
  private String switchId;
  private boolean switchActive;
  private boolean batchTransAllowed;
  private String localconnIP;
  private int localConnPort;

  /**
   * This method returns the switch id
   * @return String
   */
  public String getSwitchId() {
    return switchId;
  }

  /**
   * This method sets the switch id
   * @param switchId String
   */
  public void setSwitchId(String switchId) {
    this.switchId = switchId;
  }

  /**
   * This method returns true or flase which indicate whether switch is active or not
   * @return boolean
   */
  public boolean isSwitchActive() {
    return switchActive;
  }

  /**
   * This method sets the switch active or inactive
   * @param switchActive boolean
   */
  public void setSwitchActive(boolean switchActive) {
    this.switchActive = switchActive;
  }

  /**
   * This method returns true or false which indicates whether batch transaction is allowed on switch or not
   * @return boolean
   */
  public boolean isBatchTransAllowed() {
    return batchTransAllowed;
  }

  /**
   * This method sets whether the batch transaction is allowed or not on the switch
   * @param batchTransAllowed boolean
   */
  public void setBatchTransAllowed(boolean batchTransAllowed) {
    this.batchTransAllowed = batchTransAllowed;
  }

  /**
   * This method sets the local connection ip address
   * @return String
   */
  public String getLocalconnIP() {
    return localconnIP;
  }

  /**
   * This method sets the local connection IP address
   * @param localconnIP String
   */
  public void setLocalconnIP(String localconnIP) {
    this.localconnIP = localconnIP;
  }

  /**
   * This method returns the local connection port
   * @return int
   */
  public int getLocalConnPort() {
    return localConnPort;
  }

  /**
   * This method sets the local connection port
   * @param localConnPort int
   */
  public void setLocalConnPort(int localConnPort) {
    this.localConnPort = localConnPort;
  }
} //end SwitchInfoObj
