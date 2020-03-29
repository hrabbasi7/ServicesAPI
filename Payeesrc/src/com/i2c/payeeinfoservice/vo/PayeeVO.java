package com.i2c.payeeinfoservice.vo;

import java.util.*;

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
public class PayeeVO {
    private String payeeId = "";
    private String payeeName = "";
    private String payeeStatus = "";
    private String payeeRegion = "";
    private String payeeState = "";
    private ArrayList payeeCatagory;
    private ArrayList payeeCode;
    private ArrayList payeePrefix;
    private ArrayList payeeAddress;
    private String payeeAddressFlag = "";
    private String payeeSNo = "";
    private boolean updateFlag = false;

    public PayeeVO() {
    }

    public PayeeVO(String payeeId,
                   String payeeName,
                   String payeeStatus,
                   String payeeRegion,
                   String payeeState,
                   ArrayList payeeCatagory,
                   ArrayList payeeCode,
                   ArrayList payeePrefix,
                   ArrayList payeeAddress,
                   String payeeAddressFlag) {
        this.payeeId = payeeId;
        this.payeeName = payeeName;
        this.payeeStatus = payeeStatus;
        this.payeeRegion = payeeRegion;
        this.payeeState = payeeState;
        this.payeeCatagory = payeeCatagory;
        this.payeeCode = payeeCode;
        this.payeePrefix = payeePrefix;
        this.payeeAddress = payeeAddress;
        this.payeeAddressFlag = payeeAddressFlag;
    }

    public void setPayeeId(String payeeId) {

        this.payeeId = payeeId;
    }

    public void setPayeeName(String payeeName) {

        this.payeeName = payeeName;
    }

    public void setPayeeStatus(String payeeStatus) {

        this.payeeStatus = payeeStatus;
    }

    public void setPayeeRegion(String payeeRegion) {

        this.payeeRegion = payeeRegion;
    }

    public String getPayeeId() {

        return payeeId;
    }

    public String getPayeeName() {

        return payeeName;
    }

    public String getPayeeStatus() {

        return payeeStatus;
    }

    public String getPayeeRegion() {

        return payeeRegion;
    }

    public void setPayeeState(String payeeState) {

        this.payeeState = payeeState;
    }

    public void setPayeeCatagory(ArrayList payeeCatagory) {

        this.payeeCatagory = payeeCatagory;
    }

    public void setPayeeCode(ArrayList payeeCode) {

        this.payeeCode = payeeCode;
    }

    public void setPayeePrefix(ArrayList payeePrefix) {

        this.payeePrefix = payeePrefix;
    }

    public void setPayeeAddress(ArrayList payeeAddress) {

        this.payeeAddress = payeeAddress;
    }

    public void setPayeeAddressFlag(String payeeAddressFlag) {

        this.payeeAddressFlag = payeeAddressFlag;
    }

    public void setPayeeSNo(String payeeSNo) {
        this.payeeSNo = payeeSNo;
    }

    public void setUpdateFlag(boolean updateFlag) {
        this.updateFlag = updateFlag;
    }

    public String getPayeeState() {

        return payeeState;
    }

    public ArrayList getPayeeCatagory() {

        return payeeCatagory;
    }

    public ArrayList getPayeeCode() {

        return payeeCode;
    }

    public ArrayList getPayeePrefix() {

        return payeePrefix;
    }

    public ArrayList getPayeeAddress() {

        return payeeAddress;
    }

    public String getPayeeAddressFlag() {

        return payeeAddressFlag;
    }

    public String getPayeeSNo() {
        return payeeSNo;
    }

    public boolean isUpdateFlag() {
        return updateFlag;
    }

}
