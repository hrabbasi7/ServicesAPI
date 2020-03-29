package com.i2c.services;

/**
 * <p>Title:TransactionObj: A bean which holds the attribute of the transaction </p>
 * <p>Description: This class holds the properties of the transaction </p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class TransactionObj {
  private String transId;
  private String accountNo;
  private String transTypeId;
  private String transDate;
  private String businessDate;
  private String accpNameAndLoc;
  private String amount;
  private String description;
  private String additionalData1;
  private String additionalData2;
  private String additionalData3;
  private String deviceId;
  private String feeAmount;
  public String getTransId() {
    return transId;
  }

  /**
   * This method sets the transaction id
   * @param transId String
   */
  public void setTransId(String transId) {
    this.transId = transId;
  }

  /**
   * This method returns the account no
   * @return String
   */
  public String getAccountNo() {
    return accountNo;
  }

  /**
   * This method sets the account no
   * @param accountNo String
   */
  public void setAccountNo(String accountNo) {
    this.accountNo = accountNo;
  }

  /**
   * This method returns the transaction type id
   * @return String
   */
  public String getTransTypeId() {
    return transTypeId;
  }

  /**
   * This method sets the transaction type id
   * @param transTypeId String
   */
  public void setTransTypeId(String transTypeId) {
    this.transTypeId = transTypeId;
  }

  /**
   * This method returns the transaction date
   * @return String
   */
  public String getTransDate() {
    return transDate;
  }

  /**
   * This method sets the transaction date
   * @param transDate String
   */
  public void setTransDate(String transDate) {
    this.transDate = transDate;
  }

  /**
   * This method returns the business date
   * @return String
   */
  public String getBusinessDate() {
    return businessDate;
  }

  /**
   * This method sets the business date
   * @param businessDate String
   */
  public void setBusinessDate(String businessDate) {
    this.businessDate = businessDate;
  }

  /**
   * This method returns the acceptor name & location
   * @return String
   */
  public String getAccpNameAndLoc() {
    return accpNameAndLoc;
  }

  /**
   * This method sets the acceptor name & location
   * @param accpNameAndLoc String
   */
  public void setAccpNameAndLoc(String accpNameAndLoc) {
    this.accpNameAndLoc = accpNameAndLoc;
  }

  /**
   * This method returns the amount
   * @return String
   */
  public String getAmount() {
    return amount;
  }

  /**
   * This method sets the amount
   * @param amount String
   */
  public void setAmount(String amount) {
    this.amount = amount;
  }

  /**
   * This method returns the description
   * @return String
   */
  public String getDescription() {
    return description;
  }

  /**
   * This method sets the description
   * @param description String
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * This method returns the additional data 1
   * @return String
   */
  public String getAdditionalData1() {
    return additionalData1;
  }

  /**
   * This method sets the additional data 1
   * @param additionalData1 String
   */
  public void setAdditionalData1(String additionalData1) {
    this.additionalData1 = additionalData1;
  }

  /**
   * This method returns the additional data 2
   * @return String
   */
  public String getAdditionalData2() {
    return additionalData2;
  }

  /**
   * This method sets the additional data 2
   * @param additionalData2 String
   */
  public void setAdditionalData2(String additionalData2) {
    this.additionalData2 = additionalData2;
  }

  /**
   * This method returns the additional data 3
   * @return String
   */
  public String getAdditionalData3() {
    return additionalData3;
  }

  /**
   * This method sets the additional data 3
   * @param additionalData3 String
   */
  public void setAdditionalData3(String additionalData3) {
    this.additionalData3 = additionalData3;
  }

  /**
   * This method returns the device id
   * @return String
   */
  public String getDeviceId() {
    return deviceId;
  }

  /**
   * This method sets the device id
   * @param deviceId String
   */
  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  /**
   * This method returns the fee amount
   * @return String
   */
  public String getFeeAmount() {
    return feeAmount;
  }

  /**
   * This method sets the fee amount
   * @param feeAmount String
   */
  public void setFeeAmount(String feeAmount) {
    this.feeAmount = feeAmount;
  }
} //end TransInfoObj
