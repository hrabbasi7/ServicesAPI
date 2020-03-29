package com.i2c.services;

/**
 * <p>Title: CardAccountObj: A bean which holds the attributes of the card account</p>
 * <p>Description: This class contains the information of the card account of card holder such as account no.
 * and account type</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

public class CardAccountObj {
  private String cardAccountNo;
  private String cardAccountType;
  private String cardAccountStatus;
  public String getCardAccountNo() {
    return cardAccountNo;
  }

  /**
   * This method sets the card account no.
   * @param cardAccountNo String
   */

  public void setCardAccountNo(String cardAccountNo) {
    this.cardAccountNo = cardAccountNo;
  }

  /**
   * This method return the card acccount type
   * @return String
   */
  public String getCardAccountType() {
    return cardAccountType;
  }

  /**
   * This method sets the card account type.
   * @param cardAccountType String
   */
  public void setCardAccountType(String cardAccountType) {
    this.cardAccountType = cardAccountType;
  }

  /**
   * This method gets the card account status
   * @return String
   */

  public String getCardAccountStatus() {
    return cardAccountStatus;
  }

  /**
   * This method sets the status of the card account
   * @param cardAccountStatus String
   */
  public void setCardAccountStatus(String cardAccountStatus) {
    this.cardAccountStatus = cardAccountStatus;
  }
} //end CardAccountObj
