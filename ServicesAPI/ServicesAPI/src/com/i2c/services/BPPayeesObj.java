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
public class BPPayeesObj {
   private String payeeSrNo = null;
   private String consumerAccountNumber = null;
   private String orderNumber = null;
   private String payeeNick = null;
   private byte [] payeeVoiceNick = null;
   private String payeeId = null;
   private String payeeName = null;
   private String chId = null;
   private String isActive = null;
   private String processorId = null;
   private BPChildPayeeObj addressInfo = null;

    public String getChId() {
        return chId;
    }

    public void setChId(String chId) {
        this.chId = chId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
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

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public void setPayeeVoiceNick(byte[] payeeVoiceNick) {
        this.payeeVoiceNick = payeeVoiceNick;
    }

    public void setPayeeNick(String payeeNick) {
        this.payeeNick = payeeNick;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setConsumerAccountNumber(String consumerAccountNumber) {
        this.consumerAccountNumber = consumerAccountNumber;
    }

    public void setAddressInfo(BPChildPayeeObj addressInfo) {
        this.addressInfo = addressInfo;
    }

    public String getIsActive() {
        return isActive;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public String getPayeeSrNo() {
        return payeeSrNo;
    }

    public String getProcessorId() {
        return processorId;
    }

    public byte[] getPayeeVoiceNick() {
        return payeeVoiceNick;
    }

    public String getPayeeNick() {
        return payeeNick;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getConsumerAccountNumber() {
        return consumerAccountNumber;
    }

    public BPChildPayeeObj getAddressInfo() {
        return addressInfo;
    }

}
