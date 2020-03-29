package com.i2c.services;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BPChildPayeeObj {
    private String addressSrNo = null;
    private String street1 = null;
    private String street2 = null;
    private String street3 = null;
    private String city = null;
    private String street4 = null;
    private String state = null;
    private String zipCode = null;
    private String countryCode = null;
    private String payeeSrNo = null;
    private String payeeCId = null;
    public String getAddressSrNo() {
        return addressSrNo;
    }

    public void setAddressSrNo(String addressSrNo) {
        this.addressSrNo = addressSrNo;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setStreet4(String street4) {
        this.street4 = street4;
    }

    public void setStreet3(String street3) {
        this.street3 = street3;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPayeeSrNo(String payeeSrNo) {
        this.payeeSrNo = payeeSrNo;
    }

    public void setPayeeCId(String payeeCId) {
        this.payeeCId = payeeCId;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getPayeeCId() {
        return payeeCId;
    }

    public String getPayeeSrNo() {
        return payeeSrNo;
    }

    public String getState() {
        return state;
    }

    public String getStreet1() {
        return street1;
    }

    public String getStreet2() {
        return street2;
    }

    public String getStreet3() {
        return street3;
    }

    public String getStreet4() {
        return street4;
    }

    public String getZipCode() {
        return zipCode;
    }

}
