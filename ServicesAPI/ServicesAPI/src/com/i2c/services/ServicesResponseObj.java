package com.i2c.services;

import java.util.Vector;

/**
 * <p>Title: ServicesResponseObj: A bean which holds the response attributes</p>
 * <p>Description: This class holds the response information such as response code and description.</p>
 * <p>Copyright: Copyright (c) 2006 Innvoavative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class ServicesResponseObj {
  private String respCode;
  private String respDesc;
  private String sysDesc;
  private String feeAmount = "0.00";
  private String feeSerialNo = null;
  private String feeTraceAuditNo = null;
  private String transId;
  private String preAuthId;
  private String cardNo;
  private String cardBalance;
  private String cardExpDate;
  private String cardStatusCode;
  private String cardStatusDesc;
  private String pin;
  private String pinOffset;
  private String cvv2;
  private String addressStreet1;
  private String addressStreet2;
  private String city;
  private String stateCode;
  private String country;
  private String zipCode;
  private String cashOutAmount;
  private java.util.Vector transactionList = new java.util.Vector();
  private java.util.Vector cardHolderPayees = new java.util.Vector();
  private String toCardNo;
  private String toCardBalance;
  private String AAC;
  private String achAccountNo;
  private String achAccountStatus;
  private String toCardAccountNo;
  private String toCardExpDate;
  private Vector cardAccounts = new Vector();
  private Vector linkedCards = new Vector();
  private Vector vasAccountList = new Vector();
  private Vector bpTransactionList = new Vector();
  private String preAuthTransId;
  private String accountNo;
  private String description;
  private String transactionId;
  private String cashOutAmt;
  private String achType;
  private String bankRoutingNo;
  private String bankAccountType;
  private String bankAccountTitle;
  private String bankName;
  private String switchTransId;
  private String switchAuditNo;
  private String switchAcctNo;
  private String switchRoutingNo;
  private String transferId;
  private String bankAddress;
  private String businessDate;
  private String achAccountStatusDesc;
  private String transCat="F";
  private String excepMsg = null;
  private String stkTrace = null;
  private String logFilePath = null;

  private String retRefNum = null;
  private String timeStamp = null;
  private String cardRef = null;
  private String firstName = null;
  private String lastName = null;
  private String portFolio = null;

  private String encryptedData = null;
  private String decryptedData = null;
  private boolean isCardAssigned = false;

  private String ofacRespCode = null;
  private String avsRespCode = null;
  private String ofacRespDesc = null;
  private String avsRespDesc = null;
  private String billPaymentTransId = null;
  private String billPaymentRespDate = null;
  private String billPaymentRespId = null;
  private String billPaymentRespValidityCode = null;
  private String billPaymentPayeeIdActual = null;
  private String billPaymentPayeeIdDirSugg = null;
  private String billPaymentRespCode = null;
  private String billPaymentRespDesc = null;
  private String billPaymentRespDetailId = null;
  private String billPaymentRespDetailDesc = null;
  private String billPaymentAdjustId = null;
  private String billPaymentAdjustRetCode = null;
  private String billPaymentScanLinePayeeId = null;
  private String billPaymentScanLinePayeeIdDir = null;
  private String billPaymentScanLinePayeeName = null;
  private String billPaymentProcessorId = null;
  private String billPaymentStatus = null;
  private String billPaymentProcessingDays = null;

  private String totalEntitledCards = null;
  private String totalCards = null;
  private boolean emptyProfile = false;

  private String tanExistingCard = null;
  private String tanUpgradedCard = null;
  private String updCardBalance = null;



  /**
   * This method returns the response code
   * @return String
   */
  public String getRespCode() {
    return respCode;
  }

  /**
   * This method sets the response code
   * @param respCode String
   */
  public void setRespCode(String respCode) {
    this.respCode = respCode;
  }

  /**
   * This method returns the response description
   * @return String
   */
  public String getRespDesc() {
    return respDesc;
  }
  /**
   * This method sets the response description
   * @param respDesc String
   */
  public void setRespDesc(String respDesc) {
    this.respDesc = respDesc;
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

  /**
   * This method return the trasaction id
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
   * This method returns the card no
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
   * This methods returns the card balance
   * @return String
   */
  public String getCardBalance() {
    return cardBalance;
  }

  /**
   * This method sets the card balance
   * @param cardBalance String
   */
  public void setCardBalance(String cardBalance) {
    this.cardBalance = cardBalance;
  }

  /**
   * This method returns the expiry date of the card
   * @return String
   */
  public String getCardExpDate() {
    return cardExpDate;
  }

  /**
   * This method sets the expiry date of the card
   * @param cardExpDate String
   */
  public void setCardExpDate(String cardExpDate) {
    this.cardExpDate = cardExpDate;
  }

  /**
   * This method returns the status code of the card
   * @return String
   */
  public String getCardStatusCode() {
    return cardStatusCode;
  }

  /**
   * This method sets the status code of the card
   * @param cardStatusCode String
   */
  public void setCardStatusCode(String cardStatusCode) {
    this.cardStatusCode = cardStatusCode;
  }

  /**
   * This method gets the card status description of the card
   * @return String
   */
  public String getCardStatusDesc() {
    return cardStatusDesc;
  }

  /**
   * This method sets the card status description
   * @param cardStatusDesc String
   */
  public void setCardStatusDesc(String cardStatusDesc) {
    this.cardStatusDesc = cardStatusDesc;
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
   * This method returns the street address 1 of the card holder
   * @return String
   */
  public String getAddressStreet1() {
    return addressStreet1;
  }

  /**
   * This method sets the street address 1 of the card holder
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
   * This method sets the street address 2 of the card holder
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
   * This method sets teh state code of the card holder
   * @param stateCode String
   */
  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }

  /**
   * This method returns the country code of the card holder
   * @return String
   */
  public String getCountry() {
    return country;
  }

  /**
   * This method sets the country code of the card holder
   * @param country String
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * This method returns the zip code of the card holder
   * @return String
   */
  public String getZipCode() {
    return zipCode;
  }

  /**
   * This method sets the ZIP code of the card holder
   * @param zipCode String
   */
  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  /**
   * This method returns the cash out amount
   * @return String
   */
  public String getCashOutAmount() {
    return cashOutAmount;
  }

  /**
   * This method sets the cash out amount
   * @param cashOutAmount String
   */
  public void setCashOutAmount(String cashOutAmount) {
    this.cashOutAmount = cashOutAmount;
  }

  /**
   * This method returns the transaction list
   * @return Vector
   */
  public java.util.Vector getTransactionList() {
    return transactionList;
  }

  /**
   * This method returns the transaction list
   * @param transactionList Vector
   */
  public void setTransactionList(java.util.Vector transactionList) {
    this.transactionList = transactionList;
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
   * This method returns the to card no.
   * @return String
   */
  public String getToCardNo() {
    return toCardNo;
  }

  /**
   * This method sets the to card no.
   * @param toCardNo String
   */
  public void setToCardNo(String toCardNo) {
    this.toCardNo = toCardNo;
  }

  /**
   * This method returns the TO card no. balance
   * @return String
   */
  public String getToCardBalance() {
    return toCardBalance;
  }

  /**
   * This method sets the TO card  no. balance
   * @param toCardBalance String
   */
  public void setToCardBalance(String toCardBalance) {
    this.toCardBalance = toCardBalance;
  }

  /**
   * This method returns the account access code
   * @return String
   */
  public String getAAC() {
    return AAC;
  }

  /**
   * This method sets the account access code
   * @param AAC String
   */
  public void setAAC(String AAC) {
    this.AAC = AAC;
  }

  /**
   * This method returns the ACH account status
   * @return String
   */
  public String getAchAccountStatus() {
    return achAccountStatus;
  }

  /**
   * This method sets the ACH account status
   * @param achAccountStatus String
   */
  public void setAchAccountStatus(String achAccountStatus) {
    this.achAccountStatus = achAccountStatus;
  }

  /**
   * This method returns the account no of TO card no.
   * @return String
   */
  public String getToCardAccountNo() {
    return toCardAccountNo;
  }

  /**
   * This method sets the To card account no.
   * @param toCardAccountNo String
   */
  public void setToCardAccountNo(String toCardAccountNo) {
    this.toCardAccountNo = toCardAccountNo;
  }

  /**
   * This method returns the card expiry date
   * @return String
   */
  public String getToCardExpDate() {
    return toCardExpDate;
  }

  /**
   * This method sets the card expiry date to
   * @param toCardExpDate String
   */
  public void setToCardExpDate(String toCardExpDate) {
    this.toCardExpDate = toCardExpDate;
  }

  /**
   * This method returns the card account
   * @return Vector
   */
  public java.util.Vector getCardAccounts() {
    return cardAccounts;
  }

  /**
   * This method sets the card account
   * @param cardAccounts Vector
   */
  public void setCardAccounts(java.util.Vector cardAccounts) {
    this.cardAccounts = cardAccounts;
  }

  /**
   * This method returns the pre-authorized transaction id
   * @return String
   */
  public String getPreAuthTransId() {
    return preAuthTransId;
  }

  /**
   * This method sets the pre auth transaciton id
   * @param preAuthTransId String
   */
  public void setPreAuthTransId(String preAuthTransId) {
    this.preAuthTransId = preAuthTransId;
  }

  /**
   * This method returns the account no.
   * @return String
   */
  public String getAccountNo() {
    return accountNo;
  }

  /**
   * This method sets the account no.
   * @param accountNo String
   */
  public void setAccountNo(String accountNo) {
    this.accountNo = accountNo;
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
   * This method returns the transaction id
   * @return String
   */
  public String getTransactionId() {
    return transactionId;
  }

  /**
   * This method sets the trasaction id
   * @param transactionId String
   */
  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  /**
   * This method returns the cash out amount
   * @return String
   */
  public String getCashOutAmt() {
    return cashOutAmt;
  }

  /**
   * This method sets the cash out amount
   * @param cashOutAmt String
   */
  public void setCashOutAmt(String cashOutAmt) {
    this.cashOutAmt = cashOutAmt;
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
   * This method returns the bank routing no.
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
   * This method returns the bank account type
   * @return String
   */
  public String getBankAccountType() {
    return bankAccountType;
  }

  /**
   * This method sets the bank account type
   * @param bankAccountType String
   */
  public void setBankAccountType(String bankAccountType) {
    this.bankAccountType = bankAccountType;
  }

  /**
   * This method returns the bank account title
   * @return String
   */
  public String getBankAccountTitle() {
    return bankAccountTitle;
  }

  /**
   * This method sets the bank account title
   * @param bankAccountTitle String
   */
  public void setBankAccountTitle(String bankAccountTitle) {
    this.bankAccountTitle = bankAccountTitle;
  }

  /**
   * This method returns the bank name
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
   * This method returns the switch transaction id
   * @return String
   */
  public String getSwitchTransId() {
    return switchTransId;
  }

  /**
   * This method sets the switch transaction id
   * @param switchTransId String
   */
  public void setSwitchTransId(String switchTransId) {
    this.switchTransId = switchTransId;
  }

  /**
   * This method returns the switch audit no
   * @return String
   */
  public String getSwitchAuditNo() {
    return switchAuditNo;
  }

  /**
   * This method sets the switch audit no
   * @param switchAuditNo String
   */
  public void setSwitchAuditNo(String switchAuditNo) {
    this.switchAuditNo = switchAuditNo;
  }

  /**
   * This method returns the switch account no
   * @return String
   */
  public String getSwitchAcctNo() {
    return switchAcctNo;
  }

  /**
   * This method sets the switch account no
   * @param switchAcctNo String
   */
  public void setSwitchAcctNo(String switchAcctNo) {
    this.switchAcctNo = switchAcctNo;
  }

  /**
   * This method returns the switch routing no.
   * @return String
   */
  public String getSwitchRoutingNo() {
    return switchRoutingNo;
  }

  /**
   * This method sets the switch routing no.
   * @param switchRoutingNo String
   */
  public void setSwitchRoutingNo(String switchRoutingNo) {
    this.switchRoutingNo = switchRoutingNo;
  }

  /**
   * This method returns the transfer id
   * @return String
   */
  public String getTransferId() {
    return transferId;
  }

  /**
   * This method sets the transfer id
   * @param transferId String
   */
  public void setTransferId(String transferId) {
    this.transferId = transferId;
  }

  /**
   * This method returns the bank address
   * @return String
   */
  public String getBankAddress() {
    return bankAddress;
  }

  /**
   * This method sets the bank address
   * @param bankAddress String
   */
  public void setBankAddress(String bankAddress) {
    this.bankAddress = bankAddress;
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
   * This method returns the ACH Account status description
   * @return String
   */
  public String getAchAccountStatusDesc() {
    return achAccountStatusDesc;
  }

  /**
   * This method sets teh ACH account status description
   * @param achAccountStatusDesc String
   */
  public void setAchAccountStatusDesc(String achAccountStatusDesc) {
    this.achAccountStatusDesc = achAccountStatusDesc;
  }

  /**
   * This method sets the transaction category
   * @return String
   */
  public String getTransCat() {
    return transCat;
  }

  /**
   * This method returns the linked cards
   * @return Vector
   */

  public Vector getLinkedCards() {
    return linkedCards;
  }

  /**
   * This method sets the stack trace contents
   * @return String
   */
  public String getStkTrace() {
    return stkTrace;
  }

  /**
   * This method returns the exception message
   * @return String
   */
  public String getExcepMsg() {
    return excepMsg;
  }

  /**
   * This method returns the log file path
   * @return String
   */
  public String getLogFilePath() {
    return logFilePath;
  }

  /**
   * This method returns the last name of the card holder
   * @return String
   */
  public String getLastName() {
    return lastName;
  }
  /**
   * This method returns the card reference
   * @return String
   */

  public String getCardRef() {
    return cardRef;
  }

  /**
   * This method return the time stamp
   * @return String
   */
  public String getTimeStamp() {
    return timeStamp;
  }

  /**
   * This method returns the prot folio
   * @return String
   */
  public String getPortFolio() {
    return portFolio;
  }

  /**
   * This method returns the first name of the card holder
   * @return String
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * This method returns the retrieval reference no.
   * @return String
   */
  public String getRetRefNum() {
    return retRefNum;
  }

  public String getPinOffset() {
    return pinOffset;
  }

  public String getFeeSerialNo() {
    return feeSerialNo;
  }

  public String getFeeTraceAuditNo() {
    return feeTraceAuditNo;
  }

  public String getCvv2() {
    return cvv2;
  }

  public String getEncryptedData() {
    return encryptedData;
  }

  public String getDecryptedData() {
    return decryptedData;
  }

  public Vector getVasAccountList() {
    return vasAccountList;
  }

  public String getSysDesc() {
    return sysDesc;
  }

  public boolean isIsCardAssigned() {
    return isCardAssigned;
  }

  /**
   * This method sets the trasaction category
   * @param transCat String
   */
  public void setTransCat(String transCat) {
    this.transCat = transCat;
  }

  /**
   * This method sets the linked cards
   * @param linkedCards Vector
   */
  public void setLinkedCards(Vector linkedCards) {
    this.linkedCards = linkedCards;
  }

  /**
   * This method sets the contents of the stack trace
   * @param stkTrace String
   */
  public void setStkTrace(String stkTrace) {
    this.stkTrace = stkTrace;
  }

  /**
   * This method sets the exception message
   * @param excepMsg String
   */
  public void setExcepMsg(String excepMsg) {
    this.excepMsg = excepMsg;
  }

  /**
   * This method sets the log file path
   * @param logFilePath String
   */
  public void setLogFilePath(String logFilePath) {
    this.logFilePath = logFilePath;
  }

  /**
   * This method sets the last name of the card holder
   * @param lastName String
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * This method sets the card reference
   * @param cardRef String
   */
  public void setCardRef(String cardRef) {
    this.cardRef = cardRef;
  }

  /**
   * This method sets the time stamp
   * @param timeStamp String
   */
  public void setTimeStamp(String timeStamp) {
    this.timeStamp = timeStamp;
  }

  /**
   * This method sets the port folio
   * @param portFolio String
   */

  public void setPortFolio(String portFolio) {
    this.portFolio = portFolio;
  }

  /**
   * This method sets the first name of the card holder
   * @param firstName String
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * This method sets the retrieval reference no.
   * @param retRefNum String
   */
  public void setRetRefNum(String retRefNum) {
    this.retRefNum = retRefNum;
  }

  public void setPinOffset(String pinOffset) {
    this.pinOffset = pinOffset;
  }

  public void setFeeSerialNo(String feeSerialNo) {
    this.feeSerialNo = feeSerialNo;
  }

  public void setFeeTraceAuditNo(String feeTraceAuditNo) {
    this.feeTraceAuditNo = feeTraceAuditNo;
  }

  public void setCvv2(String cvv2) {
    this.cvv2 = cvv2;
  }

  public void setEncryptedData(String encryptedData) {
    this.encryptedData = encryptedData;
  }

  public void setDecryptedData(String decryptedData) {
    this.decryptedData = decryptedData;
  }

  public void setVasAccountList(Vector vasAccountList) {
    this.vasAccountList = vasAccountList;
  }

  public void setSysDesc(String sysDesc) {
    this.sysDesc = sysDesc;
  }

  public void setIsCardAssigned(boolean isCardAssigned) {
    this.isCardAssigned = isCardAssigned;
  }

public String getAvsRespCode() {
	return avsRespCode;
}

public void setAvsRespCode(String avsRespCode) {
	this.avsRespCode = avsRespCode;
}

public String getAvsRespDesc() {
	return avsRespDesc;
}

public void setAvsRespDesc(String avsRespDesc) {
	this.avsRespDesc = avsRespDesc;
}

public String getOfacRespCode() {
	return ofacRespCode;
}

public void setOfacRespCode(String ofacRespCode) {
	this.ofacRespCode = ofacRespCode;
}

public String getOfacRespDesc() {
	return ofacRespDesc;
}

    public Vector getCardHolderPayees() {
        return cardHolderPayees;
    }

    public String getBillPaymentTransId() {
        return billPaymentTransId;
    }

    public String getBillPaymentScanLinePayeeName() {
        return billPaymentScanLinePayeeName;
    }

    public String getBillPaymentScanLinePayeeIdDir() {
        return billPaymentScanLinePayeeIdDir;
    }

    public String getBillPaymentScanLinePayeeId() {
        return billPaymentScanLinePayeeId;
    }

    public String getBillPaymentRespValidityCode() {
        return billPaymentRespValidityCode;
    }

    public String getBillPaymentRespId() {
        return billPaymentRespId;
    }

    public String getBillPaymentRespDetailId() {
        return billPaymentRespDetailId;
    }

    public String getBillPaymentRespDetailDesc() {
        return billPaymentRespDetailDesc;
    }

    public String getBillPaymentRespDesc() {
        return billPaymentRespDesc;
    }

    public String getBillPaymentRespDate() {
        return billPaymentRespDate;
    }

    public String getBillPaymentRespCode() {
        return billPaymentRespCode;
    }

    public String getBillPaymentPayeeIdDirSugg() {
        return billPaymentPayeeIdDirSugg;
    }

    public String getBillPaymentPayeeIdActual() {
        return billPaymentPayeeIdActual;
    }

    public String getBillPaymentAdjustRetCode() {
        return billPaymentAdjustRetCode;
    }

    public String getBillPaymentAdjustId() {
        return billPaymentAdjustId;
    }

    public String getBillPaymentProcessorId() {
        return billPaymentProcessorId;
    }

    public Vector getBpTransactionList() {
        return bpTransactionList;
    }

    public String getTotalEntitledCards() {
        return totalEntitledCards;
    }

    public String getTotalCards() {
        return totalCards;
    }

    public String getBillPaymentStatus() {
        return billPaymentStatus;
    }

    public String getBillPaymentProcessingDays() {
        return billPaymentProcessingDays;
    }

    public String getPreAuthId() {
        return preAuthId;
    }

    public boolean isEmptyProfile() {
        return emptyProfile;
    }

    public String getUpdCardBalance() {
        return updCardBalance;
    }

    public String getTanUpgradedCard() {
        return tanUpgradedCard;
    }

    public String getTanExistingCard() {
        return tanExistingCard;
    }

    public void setOfacRespDesc(String ofacRespDesc) {
	this.ofacRespDesc = ofacRespDesc;
}

    public void setCardHolderPayees(Vector cardHolderPayees) {
        this.cardHolderPayees = cardHolderPayees;
    }

    public void setBillPaymentTransId(String billPaymentTransId) {
        this.billPaymentTransId = billPaymentTransId;
    }

    public void setBillPaymentScanLinePayeeName(String
                                                billPaymentScanLinePayeeName) {
        this.billPaymentScanLinePayeeName = billPaymentScanLinePayeeName;
    }

    public void setBillPaymentScanLinePayeeIdDir(String
                                                 billPaymentScanLinePayeeIdDir) {
        this.billPaymentScanLinePayeeIdDir = billPaymentScanLinePayeeIdDir;
    }

    public void setBillPaymentScanLinePayeeId(String billPaymentScanLinePayeeId) {
        this.billPaymentScanLinePayeeId = billPaymentScanLinePayeeId;
    }

    public void setBillPaymentRespValidityCode(String
                                               billPaymentRespValidityCode) {
        this.billPaymentRespValidityCode = billPaymentRespValidityCode;
    }

    public void setBillPaymentRespId(String billPaymentRespId) {
        this.billPaymentRespId = billPaymentRespId;
    }

    public void setBillPaymentRespDetailId(String billPaymentRespDetailId) {
        this.billPaymentRespDetailId = billPaymentRespDetailId;
    }

    public void setBillPaymentRespDetailDesc(String billPaymentRespDetailDesc) {
        this.billPaymentRespDetailDesc = billPaymentRespDetailDesc;
    }

    public void setBillPaymentRespDesc(String billPaymentRespDesc) {
        this.billPaymentRespDesc = billPaymentRespDesc;
    }

    public void setBillPaymentRespDate(String billPaymentRespDate) {
        this.billPaymentRespDate = billPaymentRespDate;
    }

    public void setBillPaymentRespCode(String billPaymentRespCode) {
        this.billPaymentRespCode = billPaymentRespCode;
    }

    public void setBillPaymentPayeeIdDirSugg(String billPaymentPayeeIdDirSugg) {
        this.billPaymentPayeeIdDirSugg = billPaymentPayeeIdDirSugg;
    }

    public void setBillPaymentPayeeIdActual(String billPaymentPayeeIdActual) {
        this.billPaymentPayeeIdActual = billPaymentPayeeIdActual;
    }

    public void setBillPaymentAdjustRetCode(String billPaymentAdjustRetCode) {
        this.billPaymentAdjustRetCode = billPaymentAdjustRetCode;
    }

    public void setBillPaymentAdjustId(String billPaymentAdjustId) {
        this.billPaymentAdjustId = billPaymentAdjustId;
    }

    public void setBillPaymentProcessorId(String billPaymentProcessorId) {
        this.billPaymentProcessorId = billPaymentProcessorId;
    }

    public void setBpTransactionList(Vector bpTransactionList) {
        this.bpTransactionList = bpTransactionList;
    }

    public void setTotalEntitledCards(String totalEntitledCards) {
        this.totalEntitledCards = totalEntitledCards;
    }

    public void setTotalCards(String totalCards) {
        this.totalCards = totalCards;
    }

    public void setBillPaymentStatus(String billPaymentStatus) {
        this.billPaymentStatus = billPaymentStatus;
    }

    public void setBillPaymentProcessingDays(String billPaymentProcessingDays) {
        this.billPaymentProcessingDays = billPaymentProcessingDays;
    }

    public void setPreAuthId(String preAuthId) {
        this.preAuthId = preAuthId;
    }

    public void setEmptyProfile(boolean emptyProfile) {
        this.emptyProfile = emptyProfile;
    }

    public void setUpdCardBalance(String updCardBalance) {
        this.updCardBalance = updCardBalance;
    }

    public void setTanUpgradedCard(String tanUpgradedCard) {
        this.tanUpgradedCard = tanUpgradedCard;
    }

    public void setTanExistingCard(String tanExistingCard) {
        this.tanExistingCard = tanExistingCard;
    }

} //end ServicesResponseObj
