package com.i2c.campaigngenservice.vo;

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
public class CampaignInstDetailsVO {
    private int campaingInstId;
    private int recipientId;
    private String channelId;
    public CampaignInstDetailsVO() {
    }

    public void setCampaingInstId(int campaingInstId) {
        this.campaingInstId = campaingInstId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getCampaingInstId() {
        return campaingInstId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public String getChannelId() {
        return channelId;
    }
}
