package com.i2c.schedulerframework.model;

import java.io.Serializable;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author habbasi
 * @version 1.0
 */
public class EmailConfigBean
    implements Serializable {

  private String smtpServer = "";
  private String from = "";
  private String[] recipientsTO;
  private String[] recipientsCC;
  private String[] recipientsBCC;
  private String subject = "";
  private String message = "";
  private String titleFile = null;
  private String[] fileName;

  public void reset() {
    smtpServer = "";
    subject = "";
    message = "";
    from = "";
    titleFile = null;
  }

  public EmailConfigBean() {
    reset();
  }

  public void setFileTitle(String s) {
    titleFile = s;
  }

  public void setSMTPServer(String s) {
    smtpServer = s;
  }

  public void setRecipientsTO(String[] s) {
    recipientsTO = s;
  }

  public void setRecipientsCC(String[] s) {
    recipientsCC = s;
  }

  public void setRecipientsBCC(String[] s) {
    recipientsBCC = s;
  }

  public void setSubject(String s) {
    subject = s;
  }

  public void setFrom(String s) {
    from = s;
  }

  public void setMessage(String s) {
    message = s;
  }

  public void setFileName(String[] s) {
    fileName = s;
  }

}
