package com.i2c.service.billpaymentschedularservice.ResponseService;

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
public class ResponseInfoVO {

  private String currentDateTime = null;
  private String scheduledDateTime = null;
  private long timeDifference;

  public ResponseInfoVO() {
    timeDifference = -1;
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
