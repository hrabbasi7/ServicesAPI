package com.i2c.billpaymentschedularservice.model;

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
 * @author not attributable
 * @version 1.0
 */
public class PaymentInfoVO implements Serializable{

  private String currentDateTime;
  private String scheduledDateTime;
  private long timeDifference;

  public void reset(){
    currentDateTime = null;
    scheduledDateTime = null;
    timeDifference = -1;
  }

  public PaymentInfoVO() {
    reset();
  }

  public String getCurrentDateTime() {
    return currentDateTime;
  }

  public String getScheduledDateTime() {
    return scheduledDateTime;
  }

  public long getTimeDifference() {
    return timeDifference;
  }

  public void setCurrentDateTime(String _currentDateTime) {
    currentDateTime = _currentDateTime;
  }

  public void setScheduledDateTime(String _scheduledDateTime) {
    scheduledDateTime = _scheduledDateTime;
  }

  public void setTimeDifference(long _timeDifference) {
    timeDifference = _timeDifference;
  }
}
