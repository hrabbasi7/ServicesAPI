package com.i2c.services.acqinstauthneticator;

public class AcquirerInfoObj {
  private String acqID = null;
  private String acqUserID = null;
  private String acqUserPassword = null;
  private String securityKey1 = null;
  private String securityKey2 = null;
  private String securityKey3 = null;
  private String algoCode = "DES";

  /**
   * @return

   */

  public String getAcqID() {

    return acqID;

  }

  /**
   * @return

   */

  public String getAcqUserID() {

    return acqUserID;

  }

  /**
   * @return

   */

  public String getAcqUserPassword() {

    return acqUserPassword;

  }

  /**
   * @param string

   */

  public void setAcqID(String string) {

    acqID = string;

  }

  /**
   * @param string

   */

  public void setAcqUserID(String string) {

    acqUserID = string;

  }

  /**
   * @param string

   */

  public void setAcqUserPassword(String string) {

    acqUserPassword = string;

  }

  /**
   * @return

   */

  public String getSecurityKey1() {

    return securityKey1;

  }

  /**
   * @return

   */

  public String getSecurityKey2() {

    return securityKey2;

  }

  /**
   * @return

   */

  public String getSecurityKey3() {

    return securityKey3;

  }

  /**
   * @param string

   */

  public void setSecurityKey1(String string) {

    securityKey1 = string;

  }

  /**
   * @param string

   */

  public void setSecurityKey2(String string) {

    securityKey2 = string;

  }

  /**
   * @param string

   */

  public void setSecurityKey3(String string) {

    securityKey3 = string;

  }

  /**
   * @return

   */

  public String getAlgoCode() {

    return algoCode;

  }

  /**
   * @param string

   */

  public void setAlgoCode(String string) {

    algoCode = string;

  }

}
