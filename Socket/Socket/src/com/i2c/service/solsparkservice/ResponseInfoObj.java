package com.i2c.service.solsparkservice;

//import com.i2c.solspark.*;
import java.util.Vector;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ResponseInfoObj {

  private String achType = null;
  private String accountTitle = null;
  private String accountType = null;
  private String bankName = null;
  private String switchResponseCode = null;

  //********************************************************************
   private Vector transactionList = null;
  private String respCode = null;
  private String balance = null;
  private String balanceTo = null;
  private String feeAmount = null;
  private String auditNo = null;
  private String respDesc = null;
  private String accountNum = null;
  private String rountingNum = null;
  private String transID = null;
  private String firstName = null;
  private String lastName = null;
  private String totalPurchases = null;
  private String totalCredits = null;
  private boolean isError;

//*********************---------New Attributes---------------******************//
  private String transTypeId;
  private String transDate;
  private String acceptorNameAndLoc;
  private String transAmount;
  private String description;
  private String achAccountNo;
  private String businessDate;
  private String cardNo;
  private String cardExpDate;
  private String switchTransID;
  private String switchRoutingNum;
  private String switchAccountNum;
  private String servAPIExcepMsg = null;
  private String servAPIStkTrace = null;
  private String servAPILogFilePath = null;



  /**
   * @return
   */
  public String getAuditNo() {
    return auditNo;
  }

  /**
   * @return
   */
  public String getBalance() {
    return balance;
  }

  /**
   * @return
   */
  public String getFeeAmount() {
    return feeAmount;
  }

  /**
   * @return
   */
  public String getRespCode() {
    return respCode;
  }

  /**
   * @param string
   */
  public void setAuditNo(String string) {
    auditNo = string;
  }

  /**
   * @param string
   */
  public void setBalance(String string) {
    balance = string;
  }

  /**
   * @param string
   */
  public void setFeeAmount(String string) {
    feeAmount = string;
  }

  /**
   * @param string
   */
  public void setRespCode(String string) {
    respCode = string;
  }

  /**
   * @return
   */
  public boolean isError() {
    return isError;
  }

  /**
   * @return
   */
  public String getRespDesc() {
    return respDesc;
  }

  /**
   * @param b
   */
  public void setError(boolean b) {
    isError = b;
  }

  /**
   * @param string
   */
  public void setRespDesc(String string) {
    respDesc = string;
  }

  /**
   * @return
   */
  public String getAccountNum() {
    return accountNum;
  }

  /**
   * @return
   */
  public String getRountingNum() {
    return rountingNum;
  }

  /**
   * @return
   */
  public String getTransID() {
    return transID;
  }

  /**
   * @param string
   */
  public void setAccountNum(String string) {
    accountNum = string;
  }

  /**
   * @param string
   */
  public void setRountingNum(String string) {
    rountingNum = string;
  }

  /**
   * @param string
   */
  public void setTransID(String string) {
    transID = string;
  }

  public String getBalanceTo() {
    return balanceTo;
  }

  public void setBalanceTo(String balanceTo) {
    this.balanceTo = balanceTo;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public boolean isIsError() {
    return isError;
  }

  public void setIsError(boolean isError) {
    this.isError = isError;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getTotalCredits() {
    return totalCredits;
  }

  public void setTotalCredits(String totalCredits) {
    this.totalCredits = totalCredits;
  }

  public String getTotalPurchases() {
    return totalPurchases;
  }

  public void setTotalPurchases(String totalPurchases) {
    this.totalPurchases = totalPurchases;
  }

  public Vector getTransactionList() {
    return transactionList;
  }

  public void setTransactionList(Vector transactionList) {
    this.transactionList = transactionList;
  }

  //********************************************************************
   public String getAccountTitle() {
     return accountTitle;
   }

  public String getAccountType() {
    return accountType;
  }

  public String getAchType() {
    return achType;
  }

  public String getBankName() {
    return bankName;
  }

  public void setAccountTitle(String accountTitle) {
    this.accountTitle = accountTitle;
  }

  public void setAccountType(String accountType) {
    this.accountType = accountType;
  }

  public void setAchType(String achType) {
    this.achType = achType;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public String getSwitchResponseCode() {
    return switchResponseCode;
  }

  public void setSwitchResponseCode(String switchResponseCode) {
    this.switchResponseCode = switchResponseCode;
  }

  public String getBusinessDate() {
    return businessDate;
  }

  public void setBusinessDate(String businessDate) {
    this.businessDate = businessDate;
  }

  public String getTransTypeId() {
    return transTypeId;
  }

  public void setTransTypeId(String transTypeId) {
    this.transTypeId = transTypeId;
  }

  public String getTransDate() {
    return transDate;
  }

  public void setTransDate(String transDate) {
    this.transDate = transDate;
  }

  public String getAcceptorNameAndLoc() {
    return acceptorNameAndLoc;
  }

  public void setAcceptorNameAndLoc(String acceptorNameAndLoc) {
    this.acceptorNameAndLoc = acceptorNameAndLoc;
  }

  public String getTransAmount() {
    return transAmount;
  }

  public void setTransAmount(String transAmount) {
    this.transAmount = transAmount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAchAccountNo() {
    return achAccountNo;
  }

  public void setAchAccountNo(String achAccountNo) {
    this.achAccountNo = achAccountNo;
  }

  public String getCardNo() {
    return cardNo;
  }

  public void setCardNo(String cardNo) {
    this.cardNo = cardNo;
  }

  public String getCardExpDate() {
    return cardExpDate;
  }

  public void setCardExpDate(String cardExpDate) {
    this.cardExpDate = cardExpDate;
  }

  public String getSwitchTransID() {
    return switchTransID;
  }

  public void setSwitchTransID(String switchTransID) {
    this.switchTransID = switchTransID;
  }

  public String getSwitchRoutingNum() {
    return switchRoutingNum;
  }

  public void setSwitchRoutingNum(String switchRoutingNum) {
    this.switchRoutingNum = switchRoutingNum;
  }

  public String getSwitchAccountNum() {
    return switchAccountNum;
  }

  public void setSwitchAccountNum(String switchAccountNum) {
    this.switchAccountNum = switchAccountNum;
  }

  public void setServAPILogFilePath(String servAPILogFilePath) {
    this.servAPILogFilePath = servAPILogFilePath;
  }

  public void setServAPIExcepMsg(String servAPIExcepMsg) {
    this.servAPIExcepMsg = servAPIExcepMsg;
  }

  public void setServAPIStkTrace(String servAPIStkTrace) {
    this.servAPIStkTrace = servAPIStkTrace;
  }

  public String getServAPILogFilePath() {
    return servAPILogFilePath;
  }

  public String getServAPIExcepMsg() {
    return servAPIExcepMsg;
  }

  public String getServAPIStkTrace() {
    return servAPIStkTrace;
  }

}
