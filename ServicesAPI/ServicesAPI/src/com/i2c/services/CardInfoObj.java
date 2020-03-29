package com.i2c.services;

public class CardInfoObj {
    private String cardNo = null;
    private String cardPrgId = null;
    private String cardBatchNo = null;
    private String cardStatusAtm = null;
    private String cardStatusPos = null;
    private String ofacStatus = null;
    private String expiryOn = null;
    private String avsStatus = null;
    private String cardGenerationMode = null;
    private String existingCardNo = null;

    public String getAvsStatus() {
        return avsStatus;
    }

    public void setAvsStatus(String avsStatus) {
        this.avsStatus = avsStatus;
    }

    public void setOfacStatus(String ofacStatus) {
        this.ofacStatus = ofacStatus;
    }

    public void setExpiryOn(String expiryOn) {
        this.expiryOn = expiryOn;
    }

    public void setCardStatusPos(String cardStatusPos) {
        this.cardStatusPos = cardStatusPos;
    }

    public void setCardStatusAtm(String cardStatusAtm) {
        this.cardStatusAtm = cardStatusAtm;
    }

    public void setCardPrgId(String cardPrgId) {
        this.cardPrgId = cardPrgId;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setCardBatchNo(String cardBatchNo) {
        this.cardBatchNo = cardBatchNo;
    }

    public void setExistingCardNo(String existingCardNo) {
        this.existingCardNo = existingCardNo;
    }

    public void setCardGenerationMode(String cardGenerationMode) {
        this.cardGenerationMode = cardGenerationMode;
    }

    public String getCardBatchNo() {
        return cardBatchNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getCardPrgId() {
        return cardPrgId;
    }

    public String getCardStatusAtm() {
        return cardStatusAtm;
    }

    public String getCardStatusPos() {
        return cardStatusPos;
    }

    public String getExpiryOn() {
        return expiryOn;
    }

    public String getOfacStatus() {
        return ofacStatus;
    }

    public String getExistingCardNo() {
        return existingCardNo;
    }

    public String getCardGenerationMode() {
        return cardGenerationMode;
    }

}
