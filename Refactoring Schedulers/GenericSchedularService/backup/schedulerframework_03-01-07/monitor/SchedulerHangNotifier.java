package com.i2c.schedulerframework.monitor;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import com.i2c.schedulerframework.util.BaseConstants;
import com.i2c.schedulerframework.util.CommonUtilities;
import com.i2c.schedulerframework.mailer.AdminMailService;
import com.i2c.utils.logging.I2cLogger;

public class SchedulerHangNotifier
    extends Thread {

  private String instanceName = null;
  private String serviceName = null;
  private Date monitorDateTime = null;
  private Logger lgr = null;

  public SchedulerHangNotifier(String instanceName,
                             String serviceName,
                             Logger lgr) {
    this.instanceName = instanceName;
    this.serviceName = serviceName;
    this.monitorDateTime = null;
    this.lgr = lgr;
  }

  public void run() {
    String alertMessage = null;

    lgr.log(I2cLogger.FINEST,
            " Monitor Service Thread is Started --- Instance Name-->" +
            instanceName + "<---Service Name--->" + serviceName);
    while (true) {
      try {
        alertMessage = checkMonitorDate();
        if (alertMessage != null) {

          lgr.log(I2cLogger.FINEST,
                  " Monitor Service Thread detected problem in the execution of Thread for instance --->" +
                  instanceName +
                  "<---Sending Email Alert to Administrator");
          sendEmailAlert(alertMessage);
          this.sleep(BaseConstants.MONITOR_SERVICE_SLEEP_TIME * 60 * 1000);
        }
        else {

          lgr.log(I2cLogger.FINEST,
                  " Monitor Service Thread detected no problem in the execution of Thread for instance --->" +
                  instanceName +
                  "<---Sleeping for Default Alert Time");
          this.sleep(BaseConstants.MONITOR_SERVICE_SLEEP_TIME * 60 * 1000);
        }
      }
      catch (Exception ex) {

        lgr.log(I2cLogger.WARNING,
                " Exception in Monitor Service Thread --->" + ex);
      }
      catch (Throwable th) {
        System.out.println(
            "Severe Exception in Payee Servicen Monitor Service Thread --->" +
            th.getStackTrace());
      }
    }
  }





  private String checkMonitorDate() {
    String alertMessage = null;

    GregorianCalendar currentTime = new GregorianCalendar();
    GregorianCalendar serviceTime = new GregorianCalendar();
    Date currentDateTime = null;
    Date serviceDateTime = null;
    long currentTimeInMillis = 0;
    long monitorTimeInMillis = 0;
    long timeDifference = 0;
    long divisor = 1000 * 60;
    try {
      currentDateTime = new Date();
      serviceDateTime = getMonitorDateTime();
      if (serviceDateTime != null) {
        currentTime.setTime(currentDateTime);
        serviceTime.setTime(serviceDateTime);

        currentTimeInMillis = currentTime.getTimeInMillis();
        monitorTimeInMillis = serviceTime.getTimeInMillis();

        timeDifference = currentTimeInMillis - monitorTimeInMillis;
        //get time difference in Minutes
        timeDifference = timeDifference / divisor;

        lgr.log(I2cLogger.FINEST,
                " method for checking Service Date Time against Current Date time --- Instance Name--->" +
                instanceName
                + "<---Service Name--->" + serviceName
                + "<---Current Date Time--->" + currentDateTime
                + "<---Monitor Date Time--->" + this.monitorDateTime
                + "<---currentTimeInMillis--->" + currentTimeInMillis
                + "<---monitorTimeInMillis--->" + monitorTimeInMillis
                + "<---timeDifference (in Minutes)--->" + timeDifference
                + "<---DEFAULT ALERT TIME (in Minutes)--->" +
                BaseConstants.DEFAULT_ALERT_TIME);

        if (timeDifference > BaseConstants.DEFAULT_ALERT_TIME) {
          alertMessage = prepareAlertMessage(serviceDateTime, currentDateTime,
                                             timeDifference);
        }
      }
    }
    catch (Exception ex) {

      lgr.log(I2cLogger.SEVERE,
              " Exception in checking Service Date Time against Current Date time ---> " +
              ex);
    }
    return alertMessage;
  }





  private String prepareAlertMessage(Date serviceDateTime, Date currentDateTime,
                                     long timeDifference) {
    StringBuffer alertMsg = new StringBuffer();
    String[] msgList = null;

    try {
      alertMsg.append(BaseConstants.ALERT_EMAIL_HDR);

      msgList = CommonUtilities.convertStringArray(BaseConstants.ALERT_EMAIL_MSG,
          BaseConstants.MAIL_MSG_DELIMETER_STRING);
      alertMsg.append("\n\n" + serviceName + msgList[0] + serviceName +
                      msgList[1] + instanceName + msgList[2] + serviceName +
                      msgList[3] + timeDifference + msgList[4]);
      alertMsg.append("\n\n Current Date Time: " + currentDateTime);
      alertMsg.append("\n\n Last Modified Date Time by " + serviceName + ": " +
                      serviceDateTime);
      alertMsg.append("\n\nTime Difference(In Minutes): " + timeDifference);
      alertMsg.append("\n\nDefault Alert Time(In Minutes): " +
                      BaseConstants.DEFAULT_ALERT_TIME);
      return alertMsg.toString();
    }
    catch (Exception ex) {

      lgr.log(I2cLogger.SEVERE,
              " Exception in Preparing Alert Message ---> " + ex);
      return null;
    }
  }





  private void sendEmailAlert(String message) {
    AdminMailService mailThrd = new AdminMailService(message,
        BaseConstants.ALERT_EMAIL_SUBJECT, BaseConstants.MAIL_REPORT_ADMIN,
        BaseConstants.MAIL_REPORT_FROM);
    mailThrd.start();
  }







  public void setMonitorDateTime(Date currentDateTime) {
    this.monitorDateTime = currentDateTime;
  }

  public synchronized Date getMonitorDateTime() {
    return this.monitorDateTime;
  }

}
