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
public class BPTransactionObj {
    private String transactionId = null;
    private String amount = null;
    private String scheduleDate = null;
    private String responseDate = null;
    private String sentDate = null;
    private String comments = null;
    private String cardNo = null;
    private String payeeSrNo = null;
    private String payeeName = null;
    private String state = null;
    private String city = null;
    private String payeeId = null;
    private String street2 = null;
    private String street1 = null;
    private String consumerAccountNo = null;
    private String zipCode = null;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public void setConsumerAccountNo(String consumerAccountNo) {
        this.consumerAccountNo = consumerAccountNo;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getComments() {
        return comments;
    }

    public String getResponseDate() {
        return responseDate;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public String getSentDate() {
        return sentDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getStreet2() {
        return street2;
    }

    public String getStreet1() {
        return street1;
    }

    public String getState() {
        return state;
    }

    public String getPayeeSrNo() {
        return payeeSrNo;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public String getConsumerAccountNo() {
        return consumerAccountNo;
    }

    public String getCity() {
        return city;
    }
}
