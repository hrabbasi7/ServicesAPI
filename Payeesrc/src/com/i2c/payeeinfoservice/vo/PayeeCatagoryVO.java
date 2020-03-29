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
public class PayeeCatagoryVO {
    private String catagoryName = "";
    private String catagoryDescription = "";
    public PayeeCatagoryVO() {
    }

    public void setCatagoryName(String catagoryName) {
        this.catagoryName = catagoryName;
    }

    public void setCatagoryDescription(String catagoryDescription) {
        this.catagoryDescription = catagoryDescription;
    }

    public String getCatagoryName() {
        return catagoryName;
    }

    public String getCatagoryDescription() {
        return catagoryDescription;
    }
}
