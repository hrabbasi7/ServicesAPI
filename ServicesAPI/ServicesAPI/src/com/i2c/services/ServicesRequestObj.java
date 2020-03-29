package com.i2c.services;

import com.i2c.services.util.*;

/**
 * <p>Title: ServicesRequestObj: A bean which holds the service request attributes</p>
 * <p>Description: This class holds all the request information</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class ServicesRequestObj {
  private String cardNo;
  private String cardUserId = null;
  private String cardPrgId = null;
  private String expiryDate;
  private String pin;
  private String applyFee = "Y";
  private String toCardNo;
  private String cardStatus;
  private String newPin;
  private String addressStreet1;
  private String addressStreet2;
  private String city;
  private String stateCode;
  private String zipCode;
  private String country;
  private String AAC;
  private String newCardNo;
  private String firstName;
  private String middleName;
  private String lastName;
  private String dob;
  private String homePhone;
  private String workPhone;
  private String email;
  private String gender;
  private String motherMaidenName;
  private String ssn;
  private String birthYear;
  private String amount;
  private String description;
  private String transId;
  private String transDate;
  private String reverseTransFee = "Y";
  private String noOfTrans;
  private String dateFrom = DateUtil.getCurrentDate(Constants.WEB_DATE_FORMAT);
  private String dateTo = DateUtil.getCurrentDate(Constants.WEB_DATE_FORMAT);
  private String nickName;
  private String bankName;
  private String bankAddress;
  private String bankAcctNo;
  private String bankAcctTitle;
  private String bankAcctType;
  private String bankRoutingNo;
  private String achAccountNo;
  private String verifyAmount1;
  private String verifyAmount2;
  private String accountNo;
  private String typeOfTrans = Constants.ALL_TRANS;
  private String retryOnFail = "N";
  private String maxTries;
  private String achType;
  private String newAAC;
  private String acquirerFeeAmt;
  private String acquirerFeeDesc;
  private String preAuthTransId;
  private String deviceType;
  private String toCardAccountNo;
  private String transferDate;
  private boolean chkAmount = false;
  private String serviceId; //whether to check the amount while getting transaction list of card
  private String isInternational = null;
  private String batchPin = "N";
  private String cardAcceptorId;
  private String cardAcceptNameAndLoc;
  private String mcc = "0";
  private String deviceId;
  private String localDateTime;
  private String transCat;
  private String acquirerId;
  private String vasAccountType;
  private String vasVendorId;
  private byte audioFile[] = null;
  private String secCardNo = null;
  private String linkType = "L";
  private String retreivalRefNum = null;
  private String localTransDate = null;
  private String localTransDateTime = null;
  private String localTransTime = null;

  private String acqData1 = null;
  private String acqData2 = null;
  private String acqData3 = null;
  private String acqUsrId = null;
  private String virtualAccount = null;
  private String receiverName = null;

  private String encryptedData = null;
  private String decryptedData = null;
  private String chargeBackCaseId = null;
  private String chargeBackApprovedAmount = null;
  private String chargeBackStatus = null;
  private String chargeBackRemarks = null;
  private String securityCode = null;
  private String isAdminTrans = "N";

  private String drivingLicesneNo = null;
  private String drivingLicesneState = null;

  private String billingAddress1 = null;
  private String billingAddress2 = null;
  private String billingCity = null;
  private String billingState = null;
  private String billingCountrycode = null;
  private String billingZipCode = null;
  private String foreignId = null;
  private String foreignIdType = null;
  private String foreignCountryCode = null;
  private String actionType = "03";
  private String traceAuditNo = null;


  private String billPaymentReversalType = null;
  private String billPaymentResponseCode = null;
  private String billPaymentResponseDesc = null;
  private String billPaymentTransactionSerial = null;
  private String billPaymentPayeeSerialNo = null;
  private String billPaymentPayeeID = null;
  private String billPaymentConsumerAccountNo = null;
  private String billPaymentAmount = null;
  private String billPaymentDate = null;
  private String billPaymentPayeeName = null;
  private String billPaymentPayeeCID = null;
  private String billPaymentPayeeStreet1 = null;
  private String billPaymentPayeeStreet2 = null;
  private String billPaymentPayeeStreet3 = null;
  private String billPaymentPayeeStreet4 = null;
  private String billPaymentPayeeCity = null;
  private String billPaymentPayeeState = null;
  private String billPaymentPayeeZIP = null;
  private String billPaymentPayeeCountry = null;
  private String billPaymentPayerName = null;
  private String billPaymentPayerNo = null;
  private String billPaymentPayerAddress1 = null;
  private String billPaymentPayerAddress2 = null;
  private String billPaymentPayerCity = null;
  private String billPaymentPayerState = null;
  private String billPaymentPayerZIP = null;
  private String billPaymentPayerCountry = null;
  private String billPaymentPayeeUserData1 = null;
  private String billPaymentPayeeUserData2 = null;
  private String billPaymentPayeeUserData3 = null;
  private String billPaymentPayeeUserData4 = null;
  private String billPaymentPayeeUserData5 = null;
  private String billPaymentPayeeUserData6 = null;
  private String billPaymentProcessorId = null;
  private String billPaymentBillType = null;
  private String billPaymentAlertType = null;
  private String billPaymentAlertUserNo = null;
  private String billPaymentStatementType = null;
  private String cardHolderId = null;
  private boolean getChPayeesIVROnly = false;

  private String alertChannelId = null;
  private String alertProviderId = null;
  private String alertChannelAddress = null;

  private String entitlementLoadSerial = null;
  private String entitlementSerialNo = null;
  private String entitlementRedeemQuantity = null;

  private String questionId = null;
  private String questionAnswer = null;

  private String existingCardNumber = null;
  private boolean doAllCashOut = false;
  private boolean activateUpgradedCard = false;

  /**
   * This method returns the card no.
   * @return String
   */
  public String getCardNo() {
    return cardNo;
  }

  /**
   * This method sets the card no
   * @param cardNo String
   */
  public void setCardNo(String cardNo) {
    this.cardNo = cardNo;
  }

  /**
   * This method returns the card expiry date
   * @return String
   */
  public String getExpiryDate() {
    return expiryDate;
  }

  /**
   * This method sets the expiry date
   * @param expiryDate String
   */
  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  /**
   * This method returns the PIN of the card
   * @return String
   */
  public String getPin() {
    return pin;
  }

  /**
   * This method sets the PIN of the card
   * @param pin String
   */
  public void setPin(String pin) {
    this.pin = pin;
  }

  /**
   * This method returns the apply fee
   * @return String
   */
  public String getApplyFee() {
    return applyFee;
  }

  /**
   * This method sets the apply fee
   * @param applyFee String
   */
  public void setApplyFee(String applyFee) {
    this.applyFee = applyFee;
  }

  /**
   * This method returns the card no
   * @return String
   */
  public String getToCardNo() {
    return toCardNo;
  }
  /**
   * This method sets the card no to
   * @param toCardNo String
   */

  public void setToCardNo(String toCardNo) {
    this.toCardNo = toCardNo;
  }

  /**
   * This method returns the card status
   * @return String
   */
  public String getCardStatus() {
    return cardStatus;
  }

  /**
   * This method sets the card status
   * @param cardStatus String
   */
  public void setCardStatus(String cardStatus) {
    this.cardStatus = cardStatus;
  }

  /**
   * This method returns the new PIN of the card
   * @return String
   */
  public String getNewPin() {
    return newPin;
  }

  /**
   * This method sets the new PIN
   * @param newPin String
   */
  public void setNewPin(String newPin) {
    this.newPin = newPin;
  }

  /**
   * This method returns the street address of the card holder
   * @return String
   */
  public String getAddressStreet1() {
    return addressStreet1;
  }

  /**
   * This method sets the street address of the card holder
   * @param addressStreet1 String
   */
  public void setAddressStreet1(String addressStreet1) {
    this.addressStreet1 = addressStreet1;
  }

  /**
   * This method returns the street address 2 of the card holder
   * @return String
   */
  public String getAddressStreet2() {
    return addressStreet2;
  }

  /**
   * This method sets the the street address 2 of the card holder
   * @param addressStreet2 String
   */
  public void setAddressStreet2(String addressStreet2) {
    this.addressStreet2 = addressStreet2;
  }

  /**
   * This method returns the city of the card holder
   * @return String
   */
  public String getCity() {
    return city;
  }

  /**
   * This method sets the city of the card holder
   * @param city String
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * This method returns the state code of the card holder
   * @return String
   */
  public String getStateCode() {
    return stateCode;
  }

  /**
   * This method sets the state code of the card holder
   * @param stateCode String
   */
  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }

  /**
   * This method returns the zip code of the card holder
   * @return String
   */
  public String getZipCode() {
    return zipCode;
  }

  /**
   * This method sets the zip code of the card holder
   * @param zipCode String
   */
  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  /**
   * This method returns the country of the card holder
   * @return String
   */
  public String getCountry() {
    return country;
  }

  /**
   * This method sets the country of the card holder
   * @param country String
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * This method returns the account access code of the card holder
   * @return String
   */
  public String getAAC() {
    return AAC;
  }

  /**
   * This method sets the account access code of the card holder
   * @param AAC String
   */
  public void setAAC(String AAC) {
    this.AAC = AAC;
  }

  /**
   * This method returns the new card no
   * @return String
   */
  public String getNewCardNo() {
    return newCardNo;
  }

  /**
   * This method sets the new card no
   * @param newCardNo String
   */
  public void setNewCardNo(String newCardNo) {
    this.newCardNo = newCardNo;
  }

  /**
   * This method returns the first name of the card holder
   * @return String
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * This method sets the first name of the card holder
   * @param firstName String
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * This method returns the middle name of the card holder
   * @return String
   */
  public String getMiddleName() {
    return middleName;
  }

  /**
   * This method sets the middle name of the card holder
   * @param middleName String
   */
  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  /**
   * This method returns the last name of the card holder
   * @return String
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * This method sets the last name of the card holder
   * @param lastName String
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * This method returns the date of birth of the card holder
   * @return String
   */
  public String getDob() {
    return dob;
  }

  /**
   * This method sets the date of birth of the card holder
   * @param dob String
   */
  public void setDob(String dob) {
    this.dob = dob;
  }

  /**
   * This method returns the home phone of the card holder
   * @return String
   */
  public String getHomePhone() {
    return homePhone;
  }

  /**
   * This method sets the home phone of the card holder
   * @param homePhone String
   */
  public void setHomePhone(String homePhone) {
    this.homePhone = homePhone;
  }

  /**
   * This method returns the work phone of the card holder
   * @return String
   */
  public String getWorkPhone() {
    return workPhone;
  }

  /**
   * This method sets the work phone of the card holder
   * @param workPhone String
   */
  public void setWorkPhone(String workPhone) {
    this.workPhone = workPhone;
  }

  /**
   * This method returns the e-mail address of the card holder
   * @return String
   */
  public String getEmail() {
    return email;
  }

  /**
   * This method sets the e-mail address of the card holder
   * @param email String
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * This method returns the gender of the card holder
   * @return String
   */
  public String getGender() {
    return gender;
  }

  /**
   * This method sets the gender of the card holder
   * @param gender String
   */
  public void setGender(String gender) {
    this.gender = gender;
  }

  /**
   * This method returns the mother maiden name of the card holder
   * @return String
   */
  public String getMotherMaidenName() {
    return motherMaidenName;
  }

  /**
   * This method sets the mother maiden name of the card holder
   * @param motherMaidenName String
   */
  public void setMotherMaidenName(String motherMaidenName) {
    this.motherMaidenName = motherMaidenName;
  }

  /**
   * This method returns the social security number of the card holder
   * @return String
   */
  public String getSsn() {
    return ssn;
  }

  /**
   * This method sets the social security number of the card holder
   * @param ssn String
   */
  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

  /**
   * This method returns the amount of the card holder
   * @return String
   */
  public String getAmount() {
    return amount;
  }

  /**
   * This method sets the amount of the card holder
   * @param amount String
   */
  public void setAmount(String amount) {
    this.amount = amount;
  }

  /**
   * This method returns the description of the card holder
   * @return String
   */
  public String getDescription() {
    return description;
  }

  /**
   * This method sets the description of the card holder
   * @param description String
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * This method returns the transaction id
   * @return String
   */
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
   * This method returns the reverse transaction fee.
   * @return String
   */
  public String getReverseTransFee() {
    return reverseTransFee;
  }

  /**
   * This method sets the reverse transaction fee
   * @param reverseTransFee String
   */
  public void setReverseTransFee(String reverseTransFee) {
    this.reverseTransFee = reverseTransFee;
  }

  /**
   * This method returns the quantity of transactions
   * @return String
   */
  public String getNoOfTrans() {
    return noOfTrans;
  }

  /**
   * This method sets the quantity of transactions
   * @param noOfTrans String
   */
  public void setNoOfTrans(String noOfTrans) {
    this.noOfTrans = noOfTrans;
  }

  /**
   * This method returns the date from
   * @return String
   */
  public String getDateFrom() {
    return dateFrom;
  }

  /**
   * This method sets the date from
   * @param dateFrom String
   */
  public void setDateFrom(String dateFrom) {
    this.dateFrom = dateFrom;
  }

  /**
   * This method returns the date to
   * @return String
   */
  public String getDateTo() {
    return dateTo;
  }

  /**
   * This method sets the date to
   * @param dateTo String
   */
  public void setDateTo(String dateTo) {
    this.dateTo = dateTo;
  }

  /**
   * This method returns the nick name
   * @return String
   */
  public String getNickName() {
    return nickName;
  }

  /**
   * This method sets the nick name
   * @param nickName String
   */
  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  /**
   * This method returns the bank name of the card holder
   * @return String
   */
  public String getBankName() {
    return bankName;
  }

  /**
   * This method sets the bank name
   * @param bankName String
   */
  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  /**
   * This method returns the bank address
   * @return String
   */
  public String getBankAddress() {
    return bankAddress;
  }

  /**
   * This method sets the bank
   * @param bankAddress String
   */
  public void setBankAddress(String bankAddress) {
    this.bankAddress = bankAddress;
  }
  /**
   * This method returns the bank account no
   * @return String
   */

  public String getBankAcctNo() {
    return bankAcctNo;
  }

  /**
   * This method sets the bank account no
   * @param bankAcctNo String
   */
  public void setBankAcctNo(String bankAcctNo) {
    this.bankAcctNo = bankAcctNo;
  }

  /**
   * This method returns the bank account title
   * @return String
   */
  public String getBankAcctTitle() {
    return bankAcctTitle;
  }

  /**
   * This method sets th bank account title
   * @param bankAcctTitle String
   */
  public void setBankAcctTitle(String bankAcctTitle) {
    this.bankAcctTitle = bankAcctTitle;
  }

  /**
   * This method returns the bank account type
   * @return String
   */
  public String getBankAcctType() {
    return bankAcctType;
  }

  /**
   * This method sets the bank account type
   * @param bankAcctType String
   */
  public void setBankAcctType(String bankAcctType) {
    this.bankAcctType = bankAcctType;
  }

  /**
   * This method returns the bank routing no
   * @return String
   */
  public String getBankRoutingNo() {
    return bankRoutingNo;
  }

  /**
   * This method sets the bank routing no
   * @param bankRoutingNo String
   */
  public void setBankRoutingNo(String bankRoutingNo) {
    this.bankRoutingNo = bankRoutingNo;
  }

  /**
   * This method returns the ACH account no
   * @return String
   */
  public String getAchAccountNo() {
    return achAccountNo;
  }

  /**
   * This method sets the ACH account no
   * @param achAccountNo String
   */
  public void setAchAccountNo(String achAccountNo) {
    this.achAccountNo = achAccountNo;
  }

  /**
   * This method returns the verify amount 1
   * @return String
   */
  public String getVerifyAmount1() {
    return verifyAmount1;
  }

  /**
   * This method sets the verify amount 1
   * @param verifyAmount1 String
   */
  public void setVerifyAmount1(String verifyAmount1) {
    this.verifyAmount1 = verifyAmount1;
  }

  /**
   * This method returns the verify amount 2
   * @return String
   */
  public String getVerifyAmount2() {
    return verifyAmount2;
  }

  /**
   * This method sets the verify amount 2
   * @param verifyAmount2 String
   */
  public void setVerifyAmount2(String verifyAmount2) {
    this.verifyAmount2 = verifyAmount2;
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
   * This method returns the type of transaction
   * @return String
   */
  public String getTypeOfTrans() {
    return typeOfTrans;
  }

  /**
   * This method sets the type of tranasaction
   * @param typeOfTrans String
   */
  public void setTypeOfTrans(String typeOfTrans) {
    this.typeOfTrans = typeOfTrans;
  }

  /**
   * This method returns the retry on fail
   * @return String
   */
  public String getRetryOnFail() {
    return retryOnFail;
  }

  /**
   * This method sets the retry on fail
   * @param retryOnFail String
   */
  public void setRetryOnFail(String retryOnFail) {
    this.retryOnFail = retryOnFail;
  }

  /**
   * This method returns the maximaum tries
   * @return String
   */
  public String getMaxTries() {
    return maxTries;
  }

  /**
   * This method sets the maximum tries
   * @param maxTries String
   */
  public void setMaxTries(String maxTries) {
    this.maxTries = maxTries;
  }

  /**
   * This method returns the ACH type
   * @return String
   */
  public String getAchType() {
    return achType;
  }

  /**
   * This method sets the ACH type
   * @param achType String
   */
  public void setAchType(String achType) {
    this.achType = achType;
  }

  /**
   * This method returns the new account access code
   * @return String
   */
  public String getNewAAC() {
    return newAAC;
  }

  /**
   * This method sets the new account access code
   * @param newAAC String
   */
  public void setNewAAC(String newAAC) {
    this.newAAC = newAAC;
  }

  /**
   * This method returns the acquirer fee amount
   * @return String
   */
  public String getAcquirerFeeAmt() {
    return acquirerFeeAmt;
  }

  /**
   * This method sets the acquirer fee amount
   * @param acquirerFeeAmt String
   */
  public void setAcquirerFeeAmt(String acquirerFeeAmt) {
    this.acquirerFeeAmt = acquirerFeeAmt;
  }

  /**
   * This method returns the acquirer fee description
   * @return String
   */
  public String getAcquirerFeeDesc() {
    return acquirerFeeDesc;
  }

  /**
   * This method sets the acquirer fee description
   * @param acquirerFeeDesc String
   */
  public void setAcquirerFeeDesc(String acquirerFeeDesc) {
    this.acquirerFeeDesc = acquirerFeeDesc;
  }

  /**
   * This method returns the pre-authorized transaction id
   * @return String
   */
  public String getPreAuthTransId() {
    return preAuthTransId;
  }

  /**
   * This method sets the pre-authorization transaction id
   * @param preAuthTransId String
   */
  public void setPreAuthTransId(String preAuthTransId) {
    this.preAuthTransId = preAuthTransId;
  }

  /**
   * This method returns the device type
   * @return String
   */
  public String getDeviceType() {
    return deviceType;
  }

  /**
   * This method sets the device type
   * @param deviceType String
   */
  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }
  /**
   * This method returns the to card account no
   * @return String
   */

  public String getToCardAccountNo() {
    return toCardAccountNo;
  }

  /**
   * This method sets the to card account no
   * @param toCardAccountNo String
   */
  public void setToCardAccountNo(String toCardAccountNo) {
    this.toCardAccountNo = toCardAccountNo;
  }

  /**
   * This method returns the transfer date
   * @return String
   */
  public String getTransferDate() {
    return transferDate;
  }

  /**
   * This method sets the transfer date
   * @param transferDate String
   */
  public void setTransferDate(String transferDate) {
    this.transferDate = transferDate;
  }

  /**
   * This method returns the check amount
   * @return boolean
   */
  public boolean isChkAmount() {
    return chkAmount;
  }

  /**
   * This method sets teh check amount
   * @param chkAmount boolean
   */
  public void setChkAmount(boolean chkAmount) {
    this.chkAmount = chkAmount;
  }

  /**
   * This method returns the service id
   * @return String
   */
  public String getServiceId() {
    return serviceId;
  }

  /**
   * This method sets the service id
   * @param serviceId String
   */
  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  /**
   * This method returns the batch PIN
   * @return String
   */
  public String getBatchPin() {
    return batchPin;
  }

  /**
   * This method sets the batch PIN
   * @param batchPin String
   */
  public void setBatchPin(String batchPin) {
    this.batchPin = batchPin;
  }

  /**
   * This method returns the card acquirer id
   * @return String
   */
  public String getCardAcceptorId() {
    return cardAcceptorId;
  }

  /**
   * This method sets the card acceptor id
   * @param cardAcceptorId String
   */
  public void setCardAcceptorId(String cardAcceptorId) {
    this.cardAcceptorId = cardAcceptorId;
  }

  /**
   * This method return the card acceptor name & location
   * @return String
   */
  public String getCardAcceptNameAndLoc() {
    return cardAcceptNameAndLoc;
  }

  /**
   * This method sets teh card acceptor name and location
   * @param cardAcceptNameAndLoc String
   */
  public void setCardAcceptNameAndLoc(String cardAcceptNameAndLoc) {
    this.cardAcceptNameAndLoc = cardAcceptNameAndLoc;
  }

  /**
   * This method returns the merchant category code
   * @return String
   */
  public String getMcc() {
    return mcc;
  }

  /**
   * This method sets the merchant category code
   * @param mcc String
   */
  public void setMcc(String mcc) {
    this.mcc = mcc;
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
   * This method returns the local date & time
   * @return String
   */
  public String getLocalDateTime() {
    return localDateTime;
  }

  /**
   * This method sets the local date & time
   * @param localDateTime String
   */
  public void setLocalDateTime(String localDateTime) {
    this.localDateTime = localDateTime;
  }

  /**
   * This method return the transaction category
   * @return String
   */
  public String getTransCat() {
    return transCat;
  }

  /**
   * This method sets teh transaction category
   * @param transCat String
   */
  public void setTransCat(String transCat) {
    this.transCat = transCat;
  }

  /**
   * This method returns the acquirer id
   * @return String
   */
  public String getAcquirerId() {
    return acquirerId;
  }

  /**
   * This method sets the acquirer id
   * @param acquirerId String
   */
  public void setAcquirerId(String acquirerId) {
    this.acquirerId = acquirerId;
  }

  /**
   * This method return the VAS account type
   * @return String
   */
  public String getVasAccountType() {
    return vasAccountType;
  }

  /**
   * This method returns the audio file
   * @return byte[]
   */
  public byte[] getAudioFile() {
    return audioFile;
  }

  /**
   * This method sets the VAS account type
   * @param vasAccountType String
   */
  public void setVasAccountType(String vasAccountType) {
    this.vasAccountType = vasAccountType;
  }

  /**
   * This method sets the audo file
   * @param audioFile byte[]
   */
  public void setAudioFile(byte[] audioFile) {
    this.audioFile = audioFile;
  }

  /**
   * This method returns the secondary card no
   * @return String
   */
  public String getSecCardNo() {
    return secCardNo;
  }

  /**
   * This method sets the secondary card no
   * @param secCardNo String
   */
  public void setSecCardNo(String secCardNo) {
    this.secCardNo = secCardNo;
  }

  /**
   * This method returns the link type
   * @return String
   */
  public String getLinkType() {
    return linkType;
  }

  /**
   * This method returns the retrieval refrence no.
   * @return String
   */
  public String getRetreivalRefNum() {
    return retreivalRefNum;
  }

  /**
   * This method returns the local time at which transaction is perfromed
   * @return String
   */
  public String getLocalTransTime() {
    return localTransTime;
  }

  /**
   * This method returns the local date and time at which transaction is perfromed
   * @return String
   */

  public String getLocalTransDateTime() {
    return localTransDateTime;
  }

  /**
   * This method returns the local date  at which transaction is perfromed
   * @return String
   */
  public String getLocalTransDate() {
    return localTransDate;
  }

  public String getAcqData2() {
    return acqData2;
  }

  public String getAcqData1() {
    return acqData1;
  }

  public String getAcqData3() {
    return acqData3;
  }

  public String getAcqUsrId() {
    return acqUsrId;
  }

  public String getReceiverName() {
    return receiverName;
  }

  public String getVirtualAccount() {
    return virtualAccount;
  }

  public String getBirthYear() {
    return birthYear;
  }

  public String getEncryptedData() {
    return encryptedData;
  }

  public String getDecryptedData() {
    return decryptedData;
  }

  public String getVasVendorId() {
    return vasVendorId;
  }

  public String getIsAdminTrans() {
    return isAdminTrans;
  }

  public String getChargeBackCaseId() {
    return chargeBackCaseId;
  }

  public String getChargeBackRemarks() {
    return chargeBackRemarks;
  }

  public String getChargeBackStatus() {
    return chargeBackStatus;
  }

  public String getChargeBackApprovedAmount() {
    return chargeBackApprovedAmount;
  }

  public String getSecurityCode() {
    return securityCode;
  }

    public String getForeignIdType() {
        return foreignIdType;
    }

    public String getForeignId() {
        return foreignId;
    }

    public String getBillingZipCode() {
        return billingZipCode;
    }

    public String getBillingState() {
        return billingState;
    }

    public String getBillingCountrycode() {
        return billingCountrycode;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public String getBillingAddress2() {
        return billingAddress2;
    }

    public String getBillingAddress1() {
        return billingAddress1;
    }

    public String getActionType() {
        return actionType;
    }

    /**
   * This method sets the link type
   * @param linkType String
   */
  public void setLinkType(String linkType) {
    this.linkType = linkType;
  }

  /**
   * This method set the retrieval refernce no.
   * @param retreivalRefNum String
   */
  public void setRetreivalRefNum(String retreivalRefNum) {
    this.retreivalRefNum = retreivalRefNum;
  }

  /**
   * This method sets the local time at which transaction is perfromed
   * @param localTransTime String
   */
   public void setLocalTransTime(String localTransTime) {
    this.localTransTime = localTransTime;
  }

  /**
   * This method sets the local date and time at which transaction is perfromed
   * @param localTransDateTime String
   */
  public void setLocalTransDateTime(String localTransDateTime) {
    this.localTransDateTime = localTransDateTime;
  }

  /**
   * This method sets the local date at which transaction is perfromed
   * @param localTransDate String
   */
  public void setLocalTransDate(String localTransDate) {
    this.localTransDate = localTransDate;
  }

  public void setAcqData2(String acqData2) {
    this.acqData2 = acqData2;
  }

  public void setAcqData1(String acqData1) {
    this.acqData1 = acqData1;
  }

  public void setAcqData3(String acqData3) {
    this.acqData3 = acqData3;
  }

  public void setAcqUsrId(String acqUsrId) {
    this.acqUsrId = acqUsrId;
  }

  public void setReceiverName(String receiverName) {
    this.receiverName = receiverName;
  }

  public void setVirtualAccount(String virtualAccount) {
    this.virtualAccount = virtualAccount;
  }

  public void setBirthYear(String birthYear) {
    this.birthYear = birthYear;
  }

  public void setEncryptedData(String encryptedData) {
    this.encryptedData = encryptedData;
  }

  public void setDecryptedData(String decryptedData) {
    this.decryptedData = decryptedData;
  }

  public void setVasVendorId(String vasVendorId) {
    this.vasVendorId = vasVendorId;
  }

  public void setIsAdminTrans(String isAdminTrans) {
    this.isAdminTrans = isAdminTrans;
  }

  public void setChargeBackCaseId(String chargeBackCaseId) {
    this.chargeBackCaseId = chargeBackCaseId;
  }

  public void setChargeBackRemarks(String chargeBackRemarks) {
    this.chargeBackRemarks = chargeBackRemarks;
  }

  public void setChargeBackStatus(String chargeBackStatus) {
    this.chargeBackStatus = chargeBackStatus;
  }

  public void setChargeBackApprovedAmount(String chargeBackApprovedAmount) {
    this.chargeBackApprovedAmount = chargeBackApprovedAmount;
  }

  public void setSecurityCode(String securityCode) {
    this.securityCode = securityCode;
  }

    public void setForeignIdType(String foreignIdType) {
        this.foreignIdType = foreignIdType;
    }

    public void setForeignId(String foreignId) {
        this.foreignId = foreignId;
    }

    public void setBillingZipCode(String billingZipCode) {
        this.billingZipCode = billingZipCode;
    }

    public void setBillingState(String billingState) {
        this.billingState = billingState;
    }

    public void setBillingCountrycode(String billingCountrycode) {
        this.billingCountrycode = billingCountrycode;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public void setBillingAddress2(String billingAddress2) {
        this.billingAddress2 = billingAddress2;
    }

    public void setBillingAddress1(String billingAddress1) {
        this.billingAddress1 = billingAddress1;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

	public String getDrivingLicesneNo() {
		return drivingLicesneNo;
	}

	public void setDrivingLicesneNo(String drivingLicesneNo) {
		this.drivingLicesneNo = drivingLicesneNo;
	}

	public String getDrivingLicesneState() {
		return drivingLicesneState;
	}

	public void setDrivingLicesneState(String drivingLicesneState) {
		this.drivingLicesneState = drivingLicesneState;
	}

	public String getForeignCountryCode() {
		return foreignCountryCode;
	}

	public void setForeignCountryCode(String foreignCountryCode) {
		this.foreignCountryCode = foreignCountryCode;
	}

	public String getCardPrgId() {
		return cardPrgId;
	}

    public String getTraceAuditNo() {
        return traceAuditNo;
    }

    public String getBillPaymentReversalType() {
        return billPaymentReversalType;
    }

    public String getBillPaymentResponseDesc() {
        return billPaymentResponseDesc;
    }

    public String getBillPaymentResponseCode() {
        return billPaymentResponseCode;
    }

    public String getBillPaymentTransactionSerial() {
        return billPaymentTransactionSerial;
    }

    public String getBillPaymentPayerZIP() {
        return billPaymentPayerZIP;
    }

    public String getBillPaymentPayerState() {
        return billPaymentPayerState;
    }

    public String getBillPaymentPayerNo() {
        return billPaymentPayerNo;
    }

    public String getBillPaymentPayerName() {
        return billPaymentPayerName;
    }

    public String getBillPaymentPayerCountry() {
        return billPaymentPayerCountry;
    }

    public String getBillPaymentPayerCity() {
        return billPaymentPayerCity;
    }

    public String getBillPaymentPayerAddress2() {
        return billPaymentPayerAddress2;
    }

    public String getBillPaymentPayerAddress1() {
        return billPaymentPayerAddress1;
    }

    public String getBillPaymentPayeeZIP() {
        return billPaymentPayeeZIP;
    }

    public String getBillPaymentPayeeUserData6() {
        return billPaymentPayeeUserData6;
    }

    public String getBillPaymentPayeeUserData5() {
        return billPaymentPayeeUserData5;
    }

    public String getBillPaymentPayeeUserData4() {
        return billPaymentPayeeUserData4;
    }

    public String getBillPaymentPayeeUserData3() {
        return billPaymentPayeeUserData3;
    }

    public String getBillPaymentPayeeUserData2() {
        return billPaymentPayeeUserData2;
    }

    public String getBillPaymentPayeeUserData1() {
        return billPaymentPayeeUserData1;
    }

    public String getBillPaymentPayeeStreet3() {
        return billPaymentPayeeStreet3;
    }

    public String getBillPaymentPayeeStreet2() {
        return billPaymentPayeeStreet2;
    }

    public String getBillPaymentPayeeStreet1() {
        return billPaymentPayeeStreet1;
    }

    public String getBillPaymentPayeeState() {
        return billPaymentPayeeState;
    }

    public String getBillPaymentPayeeSerialNo() {
        return billPaymentPayeeSerialNo;
    }

    public String getBillPaymentPayeeName() {
        return billPaymentPayeeName;
    }

    public String getBillPaymentPayeeID() {
        return billPaymentPayeeID;
    }

    public String getBillPaymentPayeeCountry() {
        return billPaymentPayeeCountry;
    }

    public String getBillPaymentPayeeCity() {
        return billPaymentPayeeCity;
    }

    public String getBillPaymentPayeeCID() {
        return billPaymentPayeeCID;
    }

    public String getBillPaymentDate() {
        return billPaymentDate;
    }

    public String getBillPaymentConsumerAccountNo() {
        return billPaymentConsumerAccountNo;
    }

    public String getBillPaymentAmount() {
        return billPaymentAmount;
    }

    public String getBillPaymentPayeeStreet4() {
        return billPaymentPayeeStreet4;
    }

    public String getCardHolderId() {
        return cardHolderId;
    }

    public String getBillPaymentProcessorId() {
        return billPaymentProcessorId;
    }

    public String getBillPaymentBillType() {
        return billPaymentBillType;
    }

    public String getBillPaymentAlertType() {
        return billPaymentAlertType;
    }

    public String getBillPaymentAlertUserNo() {
        return billPaymentAlertUserNo;
    }

    public boolean isGetChPayeesIVROnly() {
        return getChPayeesIVROnly;
    }

    public String getBillPaymentStatementType() {
        return billPaymentStatementType;
    }

    public String getAlertProviderId() {
        return alertProviderId;
    }

    public String getAlertChannelId() {
        return alertChannelId;
    }

    public String getAlertChannelAddress() {
        return alertChannelAddress;
    }

    public String getCardUserId() {
        return cardUserId;
    }

    public String getEntitlementLoadSerial() {
        return entitlementLoadSerial;
    }

    public String getEntitlementSerialNo() {
        return entitlementSerialNo;
    }

    public String getEntitlementRedeemQuantity() {
        return entitlementRedeemQuantity;
    }

    public String getIsInternational() {
        return isInternational;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public String getExistingCardNumber() {
        return existingCardNumber;
    }

    public boolean isDoAllCashOut() {
        return doAllCashOut;
    }

    public boolean isActivateUpgradedCard() {
        return activateUpgradedCard;
    }

    public void setCardPrgId(String cardPrgid) {
		this.cardPrgId = cardPrgid;
	}

    public void setTraceAuditNo(String traceAuditNo) {
        this.traceAuditNo = traceAuditNo;
    }

    public void setBillPaymentReversalType(String billPaymentReversalType) {
        this.billPaymentReversalType = billPaymentReversalType;
    }

    public void setBillPaymentResponseDesc(String billPaymentResponseDesc) {
        this.billPaymentResponseDesc = billPaymentResponseDesc;
    }

    public void setBillPaymentResponseCode(String billPaymentResponseCode) {
        this.billPaymentResponseCode = billPaymentResponseCode;
    }

    public void setBillPaymentTransactionSerial(String
                                                billPaymentTransactionSerial) {
        this.billPaymentTransactionSerial = billPaymentTransactionSerial;
    }

    public void setBillPaymentPayerZIP(String billPaymentPayerZIP) {
        this.billPaymentPayerZIP = billPaymentPayerZIP;
    }

    public void setBillPaymentPayerState(String billPaymentPayerState) {
        this.billPaymentPayerState = billPaymentPayerState;
    }

    public void setBillPaymentPayerNo(String billPaymentPayerNo) {
        this.billPaymentPayerNo = billPaymentPayerNo;
    }

    public void setBillPaymentPayerName(String billPaymentPayerName) {
        this.billPaymentPayerName = billPaymentPayerName;
    }

    public void setBillPaymentPayerCountry(String billPaymentPayerCountry) {
        this.billPaymentPayerCountry = billPaymentPayerCountry;
    }

    public void setBillPaymentPayerCity(String billPaymentPayerCity) {
        this.billPaymentPayerCity = billPaymentPayerCity;
    }

    public void setBillPaymentPayerAddress2(String billPaymentPayerAddress2) {
        this.billPaymentPayerAddress2 = billPaymentPayerAddress2;
    }

    public void setBillPaymentPayerAddress1(String billPaymentPayerAddress1) {
        this.billPaymentPayerAddress1 = billPaymentPayerAddress1;
    }

    public void setBillPaymentPayeeZIP(String billPaymentPayeeZIP) {
        this.billPaymentPayeeZIP = billPaymentPayeeZIP;
    }

    public void setBillPaymentPayeeUserData6(String billPaymentPayeeUserData6) {
        this.billPaymentPayeeUserData6 = billPaymentPayeeUserData6;
    }

    public void setBillPaymentPayeeUserData5(String billPaymentPayeeUserData5) {
        this.billPaymentPayeeUserData5 = billPaymentPayeeUserData5;
    }

    public void setBillPaymentPayeeUserData4(String billPaymentPayeeUserData4) {
        this.billPaymentPayeeUserData4 = billPaymentPayeeUserData4;
    }

    public void setBillPaymentPayeeUserData3(String billPaymentPayeeUserData3) {
        this.billPaymentPayeeUserData3 = billPaymentPayeeUserData3;
    }

    public void setBillPaymentPayeeUserData2(String billPaymentPayeeUserData2) {
        this.billPaymentPayeeUserData2 = billPaymentPayeeUserData2;
    }

    public void setBillPaymentPayeeUserData1(String billPaymentPayeeUserData1) {
        this.billPaymentPayeeUserData1 = billPaymentPayeeUserData1;
    }

    public void setBillPaymentPayeeStreet3(String billPaymentPayeeStreet3) {
        this.billPaymentPayeeStreet3 = billPaymentPayeeStreet3;
    }

    public void setBillPaymentPayeeStreet2(String billPaymentPayeeStreet2) {
        this.billPaymentPayeeStreet2 = billPaymentPayeeStreet2;
    }

    public void setBillPaymentPayeeStreet1(String billPaymentPayeeStreet1) {
        this.billPaymentPayeeStreet1 = billPaymentPayeeStreet1;
    }

    public void setBillPaymentPayeeState(String billPaymentPayeeState) {
        this.billPaymentPayeeState = billPaymentPayeeState;
    }

    public void setBillPaymentPayeeSerialNo(String billPaymentPayeeSerialNo) {
        this.billPaymentPayeeSerialNo = billPaymentPayeeSerialNo;
    }

    public void setBillPaymentPayeeName(String billPaymentPayeeName) {
        this.billPaymentPayeeName = billPaymentPayeeName;
    }

    public void setBillPaymentPayeeID(String billPaymentPayeeID) {
        this.billPaymentPayeeID = billPaymentPayeeID;
    }

    public void setBillPaymentPayeeCountry(String billPaymentPayeeCountry) {
        this.billPaymentPayeeCountry = billPaymentPayeeCountry;
    }

    public void setBillPaymentPayeeCity(String billPaymentPayeeCity) {
        this.billPaymentPayeeCity = billPaymentPayeeCity;
    }

    public void setBillPaymentPayeeCID(String billPaymentPayeeCID) {
        this.billPaymentPayeeCID = billPaymentPayeeCID;
    }

    public void setBillPaymentDate(String billPaymentDate) {
        this.billPaymentDate = billPaymentDate;
    }

    public void setBillPaymentConsumerAccountNo(String
                                                billPaymentConsumerAccountNo) {
        this.billPaymentConsumerAccountNo = billPaymentConsumerAccountNo;
    }

    public void setBillPaymentAmount(String billPaymentAmount) {
        this.billPaymentAmount = billPaymentAmount;
    }

    public void setBillPaymentPayeeStreet4(String billPaymentPayeeStreet4) {
        this.billPaymentPayeeStreet4 = billPaymentPayeeStreet4;
    }

    public void setCardHolderId(String cardHolderId) {
        this.cardHolderId = cardHolderId;
    }

    public void setBillPaymentProcessorId(String billPaymentProcessorId) {
        this.billPaymentProcessorId = billPaymentProcessorId;
    }

    public void setBillPaymentBillType(String billPaymentBillType) {
        this.billPaymentBillType = billPaymentBillType;
    }

    public void setBillPaymentAlertType(String billPaymentAlertType) {
        this.billPaymentAlertType = billPaymentAlertType;
    }

    public void setBillPaymentAlertUserNo(String billPaymentAlertUserNo) {
        this.billPaymentAlertUserNo = billPaymentAlertUserNo;
    }

    public void setGetChPayeesIVROnly(boolean getChPayeesIVROnly) {
        this.getChPayeesIVROnly = getChPayeesIVROnly;
    }

    public void setBillPaymentStatementType(String billPaymentStatementType) {
        this.billPaymentStatementType = billPaymentStatementType;
    }

    public void setAlertProviderId(String alertProviderId) {
        this.alertProviderId = alertProviderId;
    }

    public void setAlertChannelId(String alertChannelId) {
        this.alertChannelId = alertChannelId;
    }

    public void setAlertChannelAddress(String alertChannelAddress) {
        this.alertChannelAddress = alertChannelAddress;
    }

    public void setCardUserId(String cardUserId) {
        this.cardUserId = cardUserId;
    }

    public void setEntitlementLoadSerial(String entitlementLoadSerial) {
        this.entitlementLoadSerial = entitlementLoadSerial;
    }

    public void setEntitlementSerialNo(String entitlementSerialNo) {
        this.entitlementSerialNo = entitlementSerialNo;
    }

    public void setEntitlementRedeemQuantity(String entitlementRedeemQuantity) {
        this.entitlementRedeemQuantity = entitlementRedeemQuantity;
    }

    public void setIsInternational(String isInternational) {
        this.isInternational = isInternational;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public void setExistingCardNumber(String existingCardNumber) {
        this.existingCardNumber = existingCardNumber;
    }

    public void setDoAllCashOut(boolean doAllCashOut) {
        this.doAllCashOut = doAllCashOut;
    }

    public void setActivateUpgradedCard(boolean activateUpgradedCard) {
        this.activateUpgradedCard = activateUpgradedCard;
    }

} //end ServicesRequestObj
