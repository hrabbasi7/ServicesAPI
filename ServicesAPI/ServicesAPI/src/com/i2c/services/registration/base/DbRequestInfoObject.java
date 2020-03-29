package com.i2c.services.registration.base;

/**
 * <p>Title: DbRequestInfoObject: A bean which holds the properties of database request. </p>
 * <p>Description: This class is a bean which holds the request information about database</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

///
public class DbRequestInfoObject {
  private String cardNo = null;
  private String cardProgramId = null;
  private String transId = null;
  private String serviceId = null;
  private String amount = "0.00";
  private String switchBalance = null;
  private String orgTraceAudit = null;
  private String deviceType = null;
  private String deviceId = null;
  private String crdAceptorCode = null;
  private String crdAceptorName = null;
  private String merchantCatCd = null;
  private String feeAmount = null;
  private String feeDesc = null;
  private String applyFee = null;
  private String toCardNumber = null;
  private String retRefNumber = null;
  private String msgType = null;
  private String remainingBalance = null;
  private String currentBalance = null;
  private String amountProcessed = null;
  private String description = null;
  private String accountNumber = null;
  private String insertMode = null;
  private String responseCode = null;
  private String acquirerId = null;
  private String skipStatus = null;
  private boolean isAllCashOut = false;
  private String isOKNegBal = null;
  private String chkTransAmt = null;
  private String acquirerUserId = null;
  private String acquirerData1 = null;
  private String acquirerData2 = null;
  private String acquirerData3 = null;
  private String activateToCard = "N";

  /**
   * This method returns trace audit no.
   * @return String
   */
  public String getOrgTraceAudit() {
  return orgTraceAudit;
}

/**
   * This method returns Card Acceptor Code
   * @return String
   */

  public String getCrdAceptorCode() {
    return crdAceptorCode;
  }

  /**
     * This method returns Card Acceptor Name
     * @return String
     */

  public String getCrdAceptorName() {
    return crdAceptorName;
  }

  /**
     * This method returns device type
     * @return String
     */

  public String getDeviceType() {
    return deviceType;
  }

  /**
   * This method returns transaction id.
   * @return String
   */

  public String getTransId() {
    return transId;
  }

  /**
   * This method returns trace fee amount.
   * @return String
   */


  public String getFeeAmount() {
    return feeAmount;
  }


  /**
   * This method returns fund amount.
   * @return String
   */
  public String getAmount() {
    return amount;
  }

  /**
 * This method returns switch balance
 * @return String
 */

  public String getSwitchBalance() {
    return switchBalance;
  }


  /**
   * This method returns card no.
   * @return String
   */

  public String getCardNo() {
    return cardNo;
  }

  /**
   * This method returns Merchant Category Code.
   * @return String
   */


  public String getMerchantCatCd() {
    return merchantCatCd;
  }

  /**
   * This method returns device Id.
   * @return String
   */

  public String getDeviceId() {
    return deviceId;
  }

  /**
 * This method returns service Id.
 * @return String
 */


  public String getServiceId() {
    return serviceId;
  }


  /**
   * This method sets the fee description.
   * @param feeDesc String
   */
  public void setFeeDesc(String feeDesc) {
    this.feeDesc = feeDesc;
  }
  /**
   * This method sets Organization Trace Audit.
   * @param orgTraceAudit String
   */

  public void setOrgTraceAudit(String orgTraceAudit) {
    this.orgTraceAudit = orgTraceAudit;
  }


  /**
   * This method sets the Card Acceptor Code.
   * @param crdAceptorCode String
   */
  public void setCrdAceptorCode(String crdAceptorCode) {
    this.crdAceptorCode = crdAceptorCode;
  }

  /**
   * This mehtod sets the Card Acceptor Name.
   * @param crdAceptorName String
   */

  public void setCrdAceptorName(String crdAceptorName) {
    this.crdAceptorName = crdAceptorName;
  }

  /**
   * This method sets the Device Type.
   * @param deviceType String
   */
  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  /**
   * This method sets the Transaction id.
   * @param transId String
   */
  public void setTransId(String transId) {
    this.transId = transId;
  }

  /**
   * This method sets the fee amount.
   * @param feeAmount String
   */
  public void setFeeAmount(String feeAmount) {
    this.feeAmount = feeAmount;
  }

  /**
   * This method sets the funds amount.
   * @param amount String
   */
  public void setAmount(String amount) {
    this.amount = amount;
  }

  /**
   * This method sets the switch balance.
   * @param switchBalance String
   */
  public void setSwitchBalance(String switchBalance) {
    this.switchBalance = switchBalance;
  }

  /**
   * This method sets the card no.
   * @param cardNo String
   */
  public void setCardNo(String cardNo) {
    this.cardNo = cardNo;
  }

  /**
   * This method sets the merchant category code.
   * @param merchantCatCd String
   */
  public void setMerchantCatCd(String merchantCatCd) {
    this.merchantCatCd = merchantCatCd;
  }


  /**
   * This method sets the device Id
   * @param deviceId String
   */
  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }


  /**
   * This method sets the service id.
   * @param serviceId String
   */
  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  /**
   * This method sets the apply fee.
   * @param applyFee String
   */
  public void setApplyFee(String applyFee) {
    this.applyFee = applyFee;
  }

  /**
   * This method sets the Card no To.
   * @param toCardNumber String
   */
  public void setToCardNumber(String toCardNumber) {
    this.toCardNumber = toCardNumber;
  }

  /**
   * This method sets the response code.
   * @param responseCode String
   */
  public void setResponseCode(String responseCode) {
    this.responseCode = responseCode;
  }

  /**
   * This method sets the message type.
   * @param msgType String
   */
  public void setMsgType(String msgType) {
    this.msgType = msgType;
  }

  /**
   * This method sets tht insert mode.
   * @param insertMode String
   */
  public void setInsertMode(String insertMode) {
    this.insertMode = insertMode;
  }

  /**
   * This method sets the acquirer id.
   * @param acquirerId String
   */
  public void setAcquirerId(String acquirerId) {
    this.acquirerId = acquirerId;
  }

  /**
   * The method sets the Return Reference Number.
   * @param retRefNumber String
   */
  public void setRetRefNumber(String retRefNumber) {
    this.retRefNumber = retRefNumber;
  }

  /**
   * the method sets the description.
   * @param description String
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * The method sets the remaining balance.
   * @param remainingBalance String
   */
  public void setRemainingBalance(String remainingBalance) {
    this.remainingBalance = remainingBalance;
  }

  /**
   * The method sets the account nubmer.
   * @param accountNumber String
   */
  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  /**
   * The methos sets the amount processed.
   * @param amountProcessed String
   */
  public void setAmountProcessed(String amountProcessed) {
    this.amountProcessed = amountProcessed;
  }

  /**
   * The method sets the current balance.
   * @param currentBalance String
   */
  public void setCurrentBalance(String currentBalance) {
    this.currentBalance = currentBalance;
  }

  /**
   * The method sets is all cash out.
   * @param isAllCashOut boolean
   */
  public void setIsAllCashOut(boolean isAllCashOut) {
    this.isAllCashOut = isAllCashOut;
  }

  /**
   * The method sets the skip status.
   * @param skipStatus String
   */
  public void setSkipStatus(String skipStatus) {
    this.skipStatus = skipStatus;
  }

  /**
   * The method sets whether is negative balance OK.
   * @param isOKNegBal String
   */
  public void setIsOKNegBal(String isOKNegBal) {
    this.isOKNegBal = isOKNegBal;
  }

  /**
   * The method sets the Check Transaction Amount.
   * @param chkTransAmt String
   */

  public void setChkTransAmt(String chkTransAmt) {
    this.chkTransAmt = chkTransAmt;
  }

  public void setAcquirerUserId(String acquirerUserId) {
    this.acquirerUserId = acquirerUserId;
  }

  public void setAcquirerData3(String acquirerData3) {
    this.acquirerData3 = acquirerData3;
  }

  public void setAcquirerData1(String acquirerData1) {
    this.acquirerData1 = acquirerData1;
  }

  public void setAcquirerData2(String acquirerData2) {
    this.acquirerData2 = acquirerData2;
  }

  public void setCardProgramId(String cardProgramId) {
    this.cardProgramId = cardProgramId;
  }

    public void setActivateToCard(String activateToCard) {
        this.activateToCard = activateToCard;
    }

    /**
   * The method returns the fee description.
   * @return String
   */
  public String getFeeDesc() {
    return feeDesc;
  }

  /**
   * The method gets the apply fee.
   * @return String
   */
  public String getApplyFee() {
    return applyFee;
  }

  /**
   * The method gets the to card no.
   * @return String
   */
  public String getToCardNumber() {
    return toCardNumber;
  }

  /**
   * The method gets the response code.
   * @return String
   */
  public String getResponseCode() {
    return responseCode;
  }

  /**
   * The method gets the message type.
   * @return String
   */
  public String getMsgType() {
    return msgType;
  }

  /**
   * The method gets the insert mode.
   * @return String
   */
  public String getInsertMode() {
    return insertMode;
  }

  /**
   * The method gets the acquirer id.
   * @return String
   */
  public String getAcquirerId() {
    return acquirerId;
  }

  /**
   * The method gets the Return Reference no.
   * @return String
   */
  public String getRetRefNumber() {
    return retRefNumber;
  }

  /**
   * The mehtod returns the description.
   * @return String
   */
  public String getDescription() {
    return description;
  }

  /**
   * The method returns the remaining balance.
   * @return String
   */
  public String getRemainingBalance() {
    return remainingBalance;
  }

  /**
   * The method gets the account number.
   * @return String
   */

  public String getAccountNumber() {
    return accountNumber;
  }

  /**
   * The method gets the amount processsed.
   * @return String
   */
  public String getAmountProcessed() {
    return amountProcessed;
  }

  /**
   * The method gets the current balance.
   * @return String
   */
  public String getCurrentBalance() {
    return currentBalance;
  }

  /**
   * The method returns the is all cash out.
   * @return boolean
   */
  public boolean isIsAllCashOut() {
    return isAllCashOut;
  }

  /**
   * The method gets the skip status.
   * @return String
   */
  public String getSkipStatus() {
    return skipStatus;
  }

  /**
   * The method gets is OK on Negative Balance.
   * @return String
   */
  public String getIsOKNegBal() {
    return isOKNegBal;
  }

  /**
   * The method gets the check transaction amount.
   * @return String
   */

  public String getChkTransAmt() {
    return chkTransAmt;
  }

  public String getAcquirerUserId() {
    return acquirerUserId;
  }

  public String getAcquirerData3() {
    return acquirerData3;
  }

  public String getAcquirerData1() {
    return acquirerData1;
  }

  public String getAcquirerData2() {
    return acquirerData2;
  }

  public String getCardProgramId() {
    return cardProgramId;
  }

    public String getActivateToCard() {
        return activateToCard;
    }
}
