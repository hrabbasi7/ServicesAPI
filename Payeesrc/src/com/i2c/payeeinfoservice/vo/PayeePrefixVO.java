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
public class PayeePrefixVO {
    private String prefixChildId = "";
    private String prefixMaskFrom = "";
    private String prefixLength = "";
    private String prefixMaskTo = "";
    public PayeePrefixVO() {
    }

    public void setPrefixChildId(String prefixChildId) {
        this.prefixChildId = prefixChildId;
    }

    public void setPrefixMaskFrom(String prefixMaskFrom) {

        this.prefixMaskFrom = prefixMaskFrom;
    }

    public void setPrefixLength(String prefixLength) {
        this.prefixLength = prefixLength;
    }

    public void setPrefixMaskTo(String prefixMaskTo) {
        this.prefixMaskTo = prefixMaskTo;
    }

    public String getPrefixChildId() {
        return prefixChildId;
    }

    public String getPrefixMaskFrom() {

        return prefixMaskFrom;
    }

    public String getPrefixLength() {
        return prefixLength;
    }

    public String getPrefixMaskTo() {
        return prefixMaskTo;
    }
}
