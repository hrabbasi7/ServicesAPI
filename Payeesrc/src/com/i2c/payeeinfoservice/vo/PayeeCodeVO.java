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
public class PayeeCodeVO {
    private String codeChildId = "";
    private String codeMask = "";
    private String codeFieldLength = "";
    public PayeeCodeVO() {
    }

    public void setCodeChildId(String codeChildId) {

        this.codeChildId = codeChildId;
    }

    public void setCodeMask(String codeMask) {
        this.codeMask = codeMask;
    }

    public void setCodeFieldLength(String codeFieldLength) {

        this.codeFieldLength = codeFieldLength;
    }

    public String getCodeChildId() {

        return codeChildId;
    }

    public String getCodeMask() {
        return codeMask;
    }

    public String getCodeFieldLength() {

        return codeFieldLength;
    }
}
