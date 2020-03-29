package com.i2c.service.util;

public class MailInfoObj {
  private String emailFrom = null;
  private String emailTo = null;
  private String emailCC = null;
  private String emailSubject = null;
  private String emailBody = null;
  private String emailHeader = null;
  private String emailFooter = null;
  public String getEmailTo() {
    return emailTo;
  }

  public String getEmailCC() {
    return emailCC;
  }

  public String getEmailBody() {
    return emailBody;
  }

  public String getEmailSubject() {
    return emailSubject;
  }

  public String getEmailFooter() {
    return emailFooter;
  }

  public String getEmailFrom() {
    return emailFrom;
  }

  public void setEmailHeader(String emailHeader) {
    this.emailHeader = emailHeader;
  }

  public void setEmailTo(String emailTo) {
    this.emailTo = emailTo;
  }

  public void setEmailCC(String emailCC) {
    this.emailCC = emailCC;
  }

  public void setEmailBody(String emailBody) {
    this.emailBody = emailBody;
  }

  public void setEmailSubject(String emailSubject) {
    this.emailSubject = emailSubject;
  }

  public void setEmailFooter(String emailFooter) {
    this.emailFooter = emailFooter;
  }

  public void setEmailFrom(String emailFrom) {
    this.emailFrom = emailFrom;
  }

  public String getEmailHeader() {
    return emailHeader;
  }
}
