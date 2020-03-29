package com.i2c.payeeinfoservice.vo;

/**
 * <p>Title: Payee Information Service</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Innovative Pvt. Ltd.</p>
 *
 * @author Haroon ur Rashid Abbasi
 * @version 1.0
 */
public class PayeeAddressVO {
    private String addressChildId = "";
    private String address1 = "";
    private String address2 = "";
    private String addressCity = "";
    private String addressState = "";
    private String addressZIP = "";
    private String addressCountryCode = "US";
    private String payeeSNo = "";
    private String addressSNo = "";
    public PayeeAddressVO() {
    }

    public void setAddressChildId(String addressChildId) {
        this.addressChildId = addressChildId;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setAddressCity(String addressCity) {

        this.addressCity = addressCity;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public void setAddressZIP(String addressZIP) {
        this.addressZIP = addressZIP;
    }

    public void setAddressCountryCode(String addressCountryCode) {
        this.addressCountryCode = addressCountryCode;
    }

    public void setPayeeSNo(String payeeSNo) {
        this.payeeSNo = payeeSNo;
    }

    public void setAddressSNo(String addressSNo) {
        this.addressSNo = addressSNo;
    }

    public String getAddressChildId() {
        return addressChildId;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddressCity() {

        return addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public String getAddressZIP() {
        return addressZIP;
    }

    public String getAddressCountryCode() {
        return addressCountryCode;
    }

    public String getPayeeSNo() {
        return payeeSNo;
    }

    public String getAddressSNo() {
        return addressSNo;
    }
}
