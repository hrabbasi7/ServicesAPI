package com.i2c.services.registration.base;

/**
 * <p>Title: DbResponseInfoObject: This class holds the response attributes </p>
 * <p>Description: This class is used to hold the response attributes such as response code</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class DbResponseInfoObject {
  private String responseCode = null;
  private String responseDesc = null;
  private String traceAuditNo = null;
  private String isoSerialNo = null;
  private String traceAuditNoToCard = null;
  private String remainingBal = null;
  private String feeAmount = null;
  private String cardBalance = null;
  private String newCardBalance = null;

  /**
   * This method returns the response code
   * @return String
   */
  public String getResponseCode() {
    return responseCode;
  }

  /**
   * This method returns the remaining balance
   * @return String
   */

  public String getRemainingBal() {
    return remainingBal;
  }

  /**
   * This method returns the trace audit no
   * @return String
   */

  public String getTraceAuditNo() {
    return traceAuditNo;
  }

  /**
   * This method returns the response description
   * @return String
   */

  public String getResponseDesc() {
    return responseDesc;
  }

  /**
   * This method sets the ISO serial no.
   * @param isoSerialNo String
   */
  public void setIsoSerialNo(String isoSerialNo) {
    this.isoSerialNo = isoSerialNo;
  }

  /**
     * This method sets the response code.
     * @param isoSerialNo String
     */

  public void setResponseCode(String responseCode) {
    this.responseCode = responseCode;
  }

  /**
   * This method sets the remaining balance of the card
   * @param remainingBal String
   */
  public void setRemainingBal(String remainingBal) {
    this.remainingBal = remainingBal;
  }

  /**
   * This method sets the trace audit no.
   * @param traceAuditNo String
   */
  public void setTraceAuditNo(String traceAuditNo) {
    this.traceAuditNo = traceAuditNo;
  }

  /**
   * This method sets the response description
   * @param responseDesc String
   */
  public void setResponseDesc(String responseDesc) {
    this.responseDesc = responseDesc;
  }

  /**
   * This method sets the fee amount
   * @param feeAmount String
   */
  public void setFeeAmount(String feeAmount) {
    this.feeAmount = feeAmount;
  }

  /**
   * This method set the card balance
   * @param cardBalance String
   */
  public void setCardBalance(String cardBalance) {
    this.cardBalance = cardBalance;
  }

  /**
   * This method sets the new card blance after execution.
   * @param newCardBalance String
   */
  public void setNewCardBalance(String newCardBalance) {
    this.newCardBalance = newCardBalance;
  }

  /**
   * This method set the trace audit no to card.
   * @param traceAuditNoToCard String
   */
  public void setTraceAuditNoToCard(String traceAuditNoToCard) {
    this.traceAuditNoToCard = traceAuditNoToCard;
  }

  /**
   * This method returns the ISO serial no.
   * @return String
   */
  public String getIsoSerialNo() {
    return isoSerialNo;
  }

  /**
   * This methods return the fee amount
   * @return String
   */
  public String getFeeAmount() {
    return feeAmount;
  }

  /**
   * This methods returns the card balance
   * @return String
   */
  public String getCardBalance() {
    return cardBalance;
  }

  /**
   * This method returns the new card balance after processing
   * @return String
   */
  public String getNewCardBalance() {
    return newCardBalance;
  }

  /**
   * This method returns the trace audit no to card.
   * @return String
   */
  public String getTraceAuditNoToCard() {
    return traceAuditNoToCard;
  }
}
