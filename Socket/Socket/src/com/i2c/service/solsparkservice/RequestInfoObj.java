package com.i2c.service.solsparkservice;
//import com.i2c.solspark.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class RequestInfoObj{

  private String function = null;
  private String switchInfo = null;
  private String instance = null;
  private String dateTime = null;
  private String securityCode = null;
  private String cardNumTo = null;
  private String accountNumTo = null;
  private String achType = null;
  private String accountTitle = null;
  private String accountType = null;
  private String bankName = null;
  private String bankAccountNum = null;
  private String routingNum = null;

  //*************************SolsPark Object variables*************************
  private String userID = null;
  private String password = null;
  private String isProd = null;
  private String key1 = null;
  private String key2 = null;
  private String key3 = null;
  private String caic = null;
  private String cardNum = null;
  private String functionID = null; //is it redundant?
  private String expDate = null;
  private String aac = null;
  private String amount = null;
  private String accountNum = null;
  private String firstName = null;
  private String lastName = null;
  private String sex = null;
  private String dob = null;
  private String email = null;
  private String ssn = null;
  private String street1 = null;
  private String street2 = null;
  private String city = null;
  private String state = null;
  private String postalCode = null;
  private String homePhone = null;
  private String workPhone = null;
  private String newACC = null;
  private String newPIN = null;
  private String newCardNum = null;
  private String maxRecords = null;
  private String pageNum = null;

//************************-----New Attributes-------***********************
   private String pin = null;
   private String applyFee = "Y";
   private String description = null;
   private String middleName = null;
   private String country = null;
   private String motherMaidenName = null;
   private String nickName = null;
   private String bankAddress = null;
   private String transferDate = null;
   private String retryOnFail = null;
   private String maxTries = null;
   private String deviceType = "I";
   private String serviceID = null;
/**
 * @return
 */
public String getSwitchInfo() {
        return switchInfo;
}

/**
 * @return
 */
public void setSwitchInfo(String value) {
        switchInfo = value;
}


  /**
   * @return
   */
  public String getAac() {
          return aac;
  }

  /**
   * @return
   */
  public String getAmount() {
          return amount;
  }

  /**
   * @return
   */
  public String getCaic() {
          return caic;
  }

  /**
   * @return
   */
  public String getCardNum() {
          return cardNum;
  }

  /**
   * @return
   */
  public String getExpDate() {
          return expDate;
  }

  /**
   * @return
   */
  public String getFunctionID() {
          return functionID;
  }

  /**
   * @return
   */
  public String getIsProd() {
          return isProd;
  }

  /**
   * @return
   */
  public String getKey1() {
          return key1;
  }

  /**
   * @return
   */
  public String getKey2() {
          return key2;
  }

  /**
   * @return
   */
  public String getKey3() {
          return key3;
  }

  /**
   * @return
   */
  public String getPassword() {
          return password;
  }

  /**
   * @return
   */
  public String getUserID() {
          return userID;
  }

  /**
   * @param string
   */
  public void setAac(String string) {
          aac = string;
  }

  /**
   * @param string
   */
  public void setAmount(String string) {
          amount = string;
  }

  /**
   * @param string
   */
  public void setCaic(String string) {
          caic = string;
  }

  /**
   * @param string
   */
  public void setCardNum(String string) {
          cardNum = string;
  }

  /**
   * @param string
   */
  public void setExpDate(String string) {
          expDate = string;
  }

  /**
   * @param string
   */
  public void setFunctionID(String string) {
          functionID = string;
  }

  /**
   * @param string
   */
  public void setIsProd(String string) {
          isProd = string;
  }

  /**
   * @param string
   */
  public void setKey1(String string) {
          key1 = string;
  }

  /**
   * @param string
   */
  public void setKey2(String string) {
          key2 = string;
  }

  /**
   * @param string
   */
  public void setKey3(String string) {
          key3 = string;
  }

  /**
   * @param string
   */
  public void setPassword(String string) {
          password = string;
  }

  /**
   * @param string
   */
  public void setUserID(String string) {
          userID = string;
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
  public String getCity() {
          return city;
  }

  /**
   * @return
   */
  public String getDob() {
          return dob;
  }

  /**
   * @return
   */
  public String getEmail() {
          return email;
  }

  /**
   * @return
   */
  public String getFirstName() {
          return firstName;
  }

  /**
   * @return
   */
  public String getHomePhone() {
          return homePhone;
  }

  /**
   * @return
   */
  public String getLastName() {
          return lastName;
  }

  /**
   * @return
   */
  public String getNewACC() {
          return newACC;
  }

  /**
   * @return
   */
  public String getNewPIN() {
          return newPIN;
  }

  /**
   * @return
   */
  public String getPostalCode() {
          return postalCode;
  }

  /**
   * @return
   */
  public String getSex() {
          return sex;
  }

  /**
   * @return
   */
  public String getSsn() {
          return ssn;
  }

  /**
   * @return
   */
  public String getState() {
          return state;
  }

  /**
   * @return
   */
  public String getStreet1() {
          return street1;
  }

  /**
   * @return
   */
  public String getStreet2() {
          return street2;
  }

  /**
   * @return
   */
  public String getWorkPhone() {
          return workPhone;
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
  public void setCity(String string) {
          city = string;
  }

  /**
   * @param string
   */
  public void setDob(String string) {
          dob = string;
  }

  /**
   * @param string
   */
  public void setEmail(String string) {
          email = string;
  }

  /**
   * @param string
   */
  public void setFirstName(String string) {
          firstName = string;
  }

  /**
   * @param string
   */
  public void setHomePhone(String string) {
          homePhone = string;
  }

  /**
   * @param string
   */
  public void setLastName(String string) {
          lastName = string;
  }

  /**
   * @param string
   */
  public void setNewACC(String string) {
          newACC = string;
  }

  /**
   * @param string
   */
  public void setNewPIN(String string) {
          newPIN = string;
  }

  /**
   * @param string
   */
  public void setPostalCode(String string) {
          postalCode = string;
  }

  /**
   * @param string
   */
  public void setSex(String string) {
          sex = string;
  }

  /**
   * @param string
   */
  public void setSsn(String string) {
          ssn = string;
  }

  /**
   * @param string
   */
  public void setState(String string) {
          state = string;
  }

  /**
   * @param string
   */
  public void setStreet1(String string) {
          street1 = string;
  }

  /**
   * @param string
   */
  public void setStreet2(String string) {
          street2 = string;
  }

  /**
   * @param string
   */
  public void setWorkPhone(String string) {
          workPhone = string;
  }

  public String getNewCardNum() {
    return newCardNum;
  }

  public void setNewCardNum(String newCardNum) {
    this.newCardNum = newCardNum;
  }

  public String getMaxRecords() {
    return maxRecords;
  }

  public void setMaxRecords(String maxRecords) {
    this.maxRecords = maxRecords;
  }

  public String getPageNum() {
    return pageNum;
  }

  public void setPageNum(String pageNum) {
    this.pageNum = pageNum;
  }

  //***************************************************************************
  public String getDateTime() {
    return dateTime;
  }

  public String getFunction() {
    return function;
  }

  public String getInstance() {
    return instance;
  }

  public String getSecurityCode() {
    return securityCode;
  }

  public void setDateTime(String datTime) {
    this.dateTime = datTime;
  }

  public void setFunction(String _function) {
    this.function = _function;
  }

  public void setInstance(String _instance) {
    this.instance = _instance;
  }

  public void setSecurityCode(String _securityCode) {
    this.securityCode = _securityCode;
  }
  public String getCardNumTo() {
    return cardNumTo;
  }
  public void setCardNumTo(String _cardnumTo) {
    this.cardNumTo = _cardnumTo;
  }
  public String getAccountNumTo() {
    return accountNumTo;
  }
  public void setAccountNumTo(String _accountNumTo) {
    this.accountNumTo = _accountNumTo;
  }
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
  public void setAccountTitle(String _accountTitle) {
    this.accountTitle = _accountTitle;
  }
  public void setAccountType(String _accountType) {
    this.accountType = _accountType;
  }
  public void setAchType(String _achType) {
    this.achType = _achType;
  }
  public void setBankName(String _bankName) {
    this.bankName = _bankName;
  }
  public String getRoutingNum() {
    return routingNum;
  }
  public void setRoutingNum(String routingNum) {
    this.routingNum = routingNum;
  }
  public String getBankAccountNum() {
    return bankAccountNum;
  }
  public void setBankAccountNum(String bankAccountNum) {
    this.bankAccountNum = bankAccountNum;
  }

  public String getPin() {
    return pin;
  }
  public void setPin(String pin) {
    this.pin = pin;
  }

  public String getApplyFee() {
      return applyFee;
  }
  public void setApplyFee(String applyFee) {
     this.applyFee = applyFee;
  }
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getMotherMaidenName() {
    return motherMaidenName;
  }

  public void setMotherMaidenName(String motherMaidenName) {
    this.motherMaidenName = motherMaidenName;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public String getBankAddress() {
    return bankAddress;
  }

  public void setBankAddress(String bankAddress) {
    this.bankAddress = bankAddress;
  }

  public String getTransferDate() {
    return transferDate;
  }

  public void setTransferDate(String transferDate) {
    this.transferDate = transferDate;
  }

  public String getRetryOnFail() {
    return retryOnFail;
  }

  public void setRetryOnFail(String retryOnFail) {
    this.retryOnFail = retryOnFail;
  }
  public String getMaxTries() {
  return maxTries;
  }
  public void setMaxTries(String maxTries) {
    this.maxTries = maxTries;
  }

  public String getDeviceType() {
  return deviceType;
  }
  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public String getServiceID() {
    return serviceID;
  }
  public void setServiceID(String _serviceID) {
    this.serviceID = _serviceID;
  }

}//end class
