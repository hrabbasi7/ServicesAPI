package com.i2c.services.registration.base;

/**
 * <p>Title: TransactionResponseInfoOb: This class holds the response attributes created during transaction
 * processing</p>
 * <p>Description: Holds the information about the transaction response such as response code and
 * the description of the response</p>
 * <p>Copyright: Copyright (c) 2006 Innvovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class TransactionResponseInfoObj {

  private String resposneCode = null;
  private String resposneDescription = null;
  private String isoSerialNumber = null;
  private String traceAuditNumber = null;
  private String traceAuditNumberToCard = null;
  private String feeAmount = null;
  private String cardBalance = null;
  private String newCardNumber = null;
  private String newExpiry = null;
  private String cardHolderName = null;
  private String institutionName = null;
  private String cardPrgName = null;
  private String cardBatchNo = null;
  private String expiryDate = null;
  private String userID = null;
  private String cardStatus = null;
  private String aac = null;
  private String ledgerBalance = null;
  private String currentBalance = null;
  private String excepMsg = null;
  private String stkTrace = null;
  private String logFilePath = null;
  private String refernceId = null;


  /**
   * This method sets the serial no of the transaction
   * @return String
   */
  public String getIsoSerialNumber() {
    return isoSerialNumber;
  }

  /**
   * This method sets the response description
   * @return String
   */
  public String getResposneDescription() {
    return resposneDescription;
  }

  /**
   * This method sets the response code
   * @param resposneCode String
   */
  public void setResposneCode(String resposneCode) {
    this.resposneCode = resposneCode;
  }

  /**
   * This method sets the ISO serial number
   * @param isoSerialNumber String
   */
  public void setIsoSerialNumber(String isoSerialNumber) {
    this.isoSerialNumber = isoSerialNumber;
  }

  /**
   * This method sets the response description
   * @param resposneDescription String
   */
  public void setResposneDescription(String resposneDescription) {
    this.resposneDescription = resposneDescription;
  }

  /**
   * This method sets the fee amount
   * @param feeAmount String
   */
  public void setFeeAmount(String feeAmount) {
    this.feeAmount = feeAmount;
  }

  /**
   * This method sets the trace audit number
   * @param traceAuditNumber String
   */
  public void setTraceAuditNumber(String traceAuditNumber) {
    this.traceAuditNumber = traceAuditNumber;
  }

  /**
   * This method sets the card balance of the card
   * @param cardBalance String
   */

  public void setCardBalance(String cardBalance) {
    this.cardBalance = cardBalance;
  }

  /**
   * This method sets the new card number.
   * @param newCardNumber String
   */
  public void setNewCardNumber(String newCardNumber) {
    this.newCardNumber = newCardNumber;
  }

  /**
   * This method sets the expiry date of the card
   * @param newExpiry String
   */
  public void setNewExpiry(String newExpiry) {
    this.newExpiry = newExpiry;
  }

  /**
   * This method sets the card batch no.
   * @param cardBatchNo String
   */
  public void setCardBatchNo(String cardBatchNo) {
    this.cardBatchNo = cardBatchNo;
  }

  /**
   * This method sets the card status of the card
   * @param cardStatus String
   */
  public void setCardStatus(String cardStatus) {
    this.cardStatus = cardStatus;
  }

  /**
   * This method sets the card program name
   * @param cardPrgName String
   */
  public void setCardPrgName(String cardPrgName) {
    this.cardPrgName = cardPrgName;
  }
  /**
   * This method sets the account access code for the card
   * @param aac String
   */
  public void setAac(String aac) {
    this.aac = aac;
  }

  /**
   * This method sets the card holder name
   * @param cardHolderName String
   */
  public void setCardHolderName(String cardHolderName) {
    this.cardHolderName = cardHolderName;
  }

  /**
   * This method returns the current balance of the card
   * @param currentBalance String
   */
  public void setCurrentBalance(String currentBalance) {
    this.currentBalance = currentBalance;
  }

  /**
   * This method sets the expiry date of the card
   * @param expiryDate String
   */
  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  /**
   * This method sets the ledger balance of the card
   * @param ledgerBalance String
   */
  public void setLedgerBalance(String ledgerBalance) {
    this.ledgerBalance = ledgerBalance;
  }
  /**
   * This method sets the instiution name
   * @param institutionName String
   */
  public void setInstitutionName(String institutionName) {
    this.institutionName = institutionName;
  }

  /**
   * This method sets the user id of the card holder
   * @param userID String
   */
  public void setUserID(String userID) {
    this.userID = userID;
  }

  /**
   * This method sets the trace audit number to card
   * @param traceAuditNumberToCard String
   */
  public void setTraceAuditNumberToCard(String traceAuditNumberToCard) {
    this.traceAuditNumberToCard = traceAuditNumberToCard;
  }

  /**
   * This method sets the log file path
   * @param logFilePath String
   */
  public void setLogFilePath(String logFilePath) {
    this.logFilePath = logFilePath;
  }

  /**
   * This method sets the stack trace contents
   * @param stkTrace String
   */
  public void setStkTrace(String stkTrace) {
    this.stkTrace = stkTrace;
  }

  /**
   * This method sets the message of the exception.
   * @param excepMsg String
   */
  public void setExcepMsg(String excepMsg) {
    this.excepMsg = excepMsg;
  }

  public void setRefernceId(String refernceId) {
    this.refernceId = refernceId;
  }

  /**
   * This method returns the response code
   * @return String
   */
  public String getResposneCode() {
    return resposneCode;
  }

  /**
   * This method returns the fee amount
   * @return String
   */
  public String getFeeAmount() {
    return feeAmount;
  }

  /**
   * This method returns the trace audit number
   * @return String
   */
  public String getTraceAuditNumber() {
    return traceAuditNumber;
  }

  /**
   * This method returns the card balance
   * @return String
   */
  public String getCardBalance() {
    return cardBalance;
  }

  /**
   * This method returns the new card number
   * @return String
   */
  public String getNewCardNumber() {
    return newCardNumber;
  }

  /**
   * This method returns the new expiry date of the card
   * @return String
   */
  public String getNewExpiry() {
    return newExpiry;
  }

  /**
   * This method returns the card batch no
   * @return String
   */
  public String getCardBatchNo() {
    return cardBatchNo;
  }

  /**
   * This method returns the card status of the card
   * @return String
   */
  public String getCardStatus() {
    return cardStatus;
  }

  /**
   * This method returns the card program name
   * @return String
   */
  public String getCardPrgName() {
    return cardPrgName;
  }

  /**
   * This method returns the acccount access code
   * @return String
   */
  public String getAac() {
    return aac;
  }

  /**
   * This method returns the card holder name
   * @return String
   */
  public String getCardHolderName() {
    return cardHolderName;
  }

  /**
   * This method returns the current blance
   * @return String
   */
  public String getCurrentBalance() {
    return currentBalance;
  }

  /**
   * This method returns the expiry date
   * @return String
   */
  public String getExpiryDate() {
    return expiryDate;
  }

  /**
   * This method returns the ledger balance of the card
   * @return String
   */
  public String getLedgerBalance() {
    return ledgerBalance;
  }

  /**
   * This method returns the institution name
   * @return String
   */
  public String getInstitutionName() {
    return institutionName;
  }

  /**
   * This method returns the user id  of the card holder
   * @return String
   */
  public String getUserID() {
    return userID;
  }

  /**
   * This method returns the trace audit number to card
   * @return String
   */
  public String getTraceAuditNumberToCard() {
    return traceAuditNumberToCard;
  }


  /**
   * This method returns the log file path.
   * @return String
   */
  public String getLogFilePath() {
    return logFilePath;
  }

  /**
   * This method returns the stack trace contents.
   * @return String
   */
  public String getStkTrace() {
    return stkTrace;
  }

  /**
   * This method returns the exception's message.
   * @return String
   */
  public String getExcepMsg() {
    return excepMsg;
  }

  public String getRefernceId() {
    return refernceId;
  }

}
