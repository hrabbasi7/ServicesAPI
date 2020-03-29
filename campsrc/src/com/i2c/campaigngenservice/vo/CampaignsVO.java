package com.i2c.campaigngenservice.vo;

import java.sql.*;

/**
 * <p>Title: Campaign Generation API</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Innovative Pvt. Ltd.</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CampaignsVO {
    private int campaignId;
    private String campaignDesc;
    private Date campCreationDate;
    private Date campCreationTime;
    private String campCreatorUserId;
    private String campStatusId;
    private String campRecipientFlag;
    private String campBusinessDaysFlag;
    private String campSubject;
    private String campFreqType;
    private String campFreqMap;
    private Date campSchdStartDate;
    private String campSchdTimeSlot;
    private int campReminders;
    private String campReminderIntervalUnit;
    private String campReminderIntervalNunits;
    private String campTerminiationBasis;
    private Date campTerminiationDate;
    private int campTerminiationFreq;
    private String campBusinessDays;
    private String campIsForEmail;
    private String campIsForFax;
    private String campIsForWebsite;
    private String campIsForSms;
    private String campIsForIvr;
    private String campMessageBody;
    private String campSmsMessage;
    private int campAttachFileId;
    private int campAttachFileIvrId;
    private String campCouponProgId;
    private String campIsModified;
    private Date campSchdNextDtime;
    private int campRemindersDone;
    private int campaignInstId;
    private String campaignName;
    private String campFromEmail;
    public CampaignsVO() {
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public void setCampaignDesc(String campaignDesc) {
        this.campaignDesc = campaignDesc;
    }

    public void setCampCreationDate(Date campCreationDate) {
        this.campCreationDate = campCreationDate;
    }

    public void setCampCreationTime(Date campCreationTime) {
        this.campCreationTime = campCreationTime;
    }

    public void setCampCreatorUserId(String campCreatorUserId) {
        this.campCreatorUserId = campCreatorUserId;
    }

    public void setCampStatusId(String campStatusId) {
        this.campStatusId = campStatusId;
    }

    public void setCampRecipientFlag(String campRecipientFlag) {
        this.campRecipientFlag = campRecipientFlag;
    }

    public void setCampBusinessDaysFlag(String campBusinessDaysFlag) {
        this.campBusinessDaysFlag = campBusinessDaysFlag;
    }

    public void setCampSubject(String campSubject) {
        this.campSubject = campSubject;
    }

    public void setCampFreqType(String campFreqType) {
        this.campFreqType = campFreqType;
    }

    public void setCampFreqMap(String campFreqMap) {
        this.campFreqMap = campFreqMap;
    }

    public void setCampSchdStartDate(Date campSchdStartDate) {
        this.campSchdStartDate = campSchdStartDate;
    }

    public void setCampSchdTimeSlot(String campSchdTimeSlot) {
        this.campSchdTimeSlot = campSchdTimeSlot;
    }

    public void setCampReminders(int campReminders) {
        this.campReminders = campReminders;
    }

    public void setCampReminderIntervalUnit(String campReminderIntervalUnit) {
        this.campReminderIntervalUnit = campReminderIntervalUnit;
    }

    public void setCampReminderIntervalNunits(String campReminderIntervalNunits) {
        this.campReminderIntervalNunits = campReminderIntervalNunits;
    }

    public void setCampTerminiationBasis(String campTerminiationBasis) {
        this.campTerminiationBasis = campTerminiationBasis;
    }

    public void setCampTerminiationDate(Date campTerminiationDate) {
        this.campTerminiationDate = campTerminiationDate;
    }

    public void setCampTerminiationFreq(int campTerminiationFreq) {
        this.campTerminiationFreq = campTerminiationFreq;
    }

    public void setCampBusinessDays(String campBusinessDays) {
        this.campBusinessDays = campBusinessDays;
    }

    public void setCampIsForEmail(String campIsForEmail) {
        this.campIsForEmail = campIsForEmail;
    }

    public void setCampIsForFax(String campIsForFax) {
        this.campIsForFax = campIsForFax;
    }

    public void setCampIsForWebsite(String campIsForWebsite) {
        this.campIsForWebsite = campIsForWebsite;
    }

    public void setCampIsForSms(String campIsForSms) {
        this.campIsForSms = campIsForSms;
    }

    public void setCampIsForIvr(String campIsForIvr) {
        this.campIsForIvr = campIsForIvr;
    }

    public void setCampMessageBody(String campMessageBody) {

        this.campMessageBody = campMessageBody;
    }

    public void setCampSmsMessage(String campSmsMessage) {
        this.campSmsMessage = campSmsMessage;
    }

    public void setCampAttachFileId(int campAttachFileId) {

        this.campAttachFileId = campAttachFileId;
    }

    public void setCampAttachFileIvrId(int campAttachFileIvrId) {

        this.campAttachFileIvrId = campAttachFileIvrId;
    }

    public void setCampCouponProgId(String campCouponProgId) {
        this.campCouponProgId = campCouponProgId;
    }

    public void setCampIsModified(String campIsModified) {
        this.campIsModified = campIsModified;
    }

    public void setCampSchdNextDtime(Date campSchdNextDtime) {
        this.campSchdNextDtime = campSchdNextDtime;
    }

    public void setCampRemindersDone(int campRemindersDone) {
        this.campRemindersDone = campRemindersDone;
    }

    public void setCampaignInstId(int campaignInstId) {
        this.campaignInstId = campaignInstId;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public void setCampFromEmail(String campFromEmail) {
        this.campFromEmail = campFromEmail;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public String getCampaignDesc() {
        return campaignDesc;
    }

    public Date getCampCreationDate() {
        return campCreationDate;
    }

    public Date getCampCreationTime() {
        return campCreationTime;
    }

    public String getCampCreatorUserId() {
        return campCreatorUserId;
    }

    public String getCampStatusId() {
        return campStatusId;
    }

    public String getCampRecipientFlag() {
        return campRecipientFlag;
    }

    public String getCampBusinessDaysFlag() {
        return campBusinessDaysFlag;
    }

    public String getCampSubject() {
        return campSubject;
    }

    public String getCampFreqType() {
        return campFreqType;
    }

    public String getCampFreqMap() {
        return campFreqMap;
    }

    public Date getCampSchdStartDate() {
        return campSchdStartDate;
    }

    public String getCampSchdTimeSlot() {
        return campSchdTimeSlot;
    }

    public int getCampReminders() {
        return campReminders;
    }

    public String getCampReminderIntervalUnit() {
        return campReminderIntervalUnit;
    }

    public String getCampReminderIntervalNunits() {
        return campReminderIntervalNunits;
    }

    public String getCampTerminiationBasis() {
        return campTerminiationBasis;
    }

    public Date getCampTerminiationDate() {
        return campTerminiationDate;
    }

    public int getCampTerminiationFreq() {
        return campTerminiationFreq;
    }

    public String getCampBusinessDays() {
        return campBusinessDays;
    }

    public String getCampIsForEmail() {
        return campIsForEmail;
    }

    public String getCampIsForFax() {
        return campIsForFax;
    }

    public String getCampIsForWebsite() {
        return campIsForWebsite;
    }

    public String getCampIsForSms() {
        return campIsForSms;
    }

    public String getCampIsForIvr() {
        return campIsForIvr;
    }

    public String getCampMessageBody() {

        return campMessageBody;
    }

    public String getCampSmsMessage() {
        return campSmsMessage;
    }

    public int getCampAttachFileId() {

        return campAttachFileId;
    }

    public int getCampAttachFileIvrId() {

        return campAttachFileIvrId;
    }

    public String getCampCouponProgId() {
        return campCouponProgId;
    }

    public String getCampIsModified() {
        return campIsModified;
    }

    public Date getCampSchdNextDtime() {
        return campSchdNextDtime;
    }

    public int getCampRemindersDone() {
        return campRemindersDone;
    }

    public int getCampaignInstId() {
        return campaignInstId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public String getCampFromEmail() {
        return campFromEmail;
    }
}
