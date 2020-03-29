package com.i2c.services;

public class VASAccountsInfoObj {
  private String vasAccountNumber = null;
  private String vasAccountType = null;
  private String vasVendor = null;
  public String getVasAccountNumber() {
    return vasAccountNumber;
  }

  public String getVasVendor() {
    return vasVendor;
  }

  public String getVasAccountType() {
    return vasAccountType;
  }

  public VASAccountsInfoObj(String vasAcctNum,String vasAcctType,String vasVendor) {
    this.vasAccountNumber = vasAcctNum;
    this.vasAccountType = vasAcctType;
    this.vasVendor = vasVendor;
  }
}
