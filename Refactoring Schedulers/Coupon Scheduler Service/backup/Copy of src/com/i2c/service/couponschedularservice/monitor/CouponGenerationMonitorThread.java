package com.i2c.service.couponschedularservice.monitor;

import java.util.Date;
import java.util.GregorianCalendar;
import com.i2c.service.util.Constants;
import com.i2c.service.util.CommonUtilities;
import com.i2c.service.util.LogLevel;
import com.i2c.service.couponschedularservice.main.AdminMailService;


public class CouponGenerationMonitorThread
    extends Thread{

  private String instanceName = null;
  private String serviceName = null;
  private Date monitorDateTime = null;

  public CouponGenerationMonitorThread(String instanceName, String serviceName) {
    this.instanceName = instanceName;
    this.serviceName = serviceName;
    this.monitorDateTime = null;
  }

  public void run(){

    String alertMessage = null;

    CommonUtilities.getLogger(instanceName,Constants.COUPON_GENERATION_MONITOR_SERVICE).log(LogLevel.getLevel(Constants.LOG_FINEST)," Alerts Execution Monitor Service Thread is Started --- Instance Name-->"+instanceName + "<---Service Name--->" + serviceName);
    while(true){
      try {
          alertMessage = checkMonitorDate();
        if(alertMessage != null){
          CommonUtilities.getLogger(instanceName,Constants.COUPON_GENERATION_MONITOR_SERVICE).log(LogLevel.getLevel(Constants.LOG_FINEST)," Alerts Execution Monitor Service Thread detected problem in the execution of Alerts Service Thread for instance --->"+instanceName + "<---Sending Email Alert to Administrator");
          sendEmailAlert(alertMessage);
          this.sleep(Constants.MONITOR_SERVICE_SLEEP_TIME * 60 * 1000);
        }
        else {
          CommonUtilities.getLogger(instanceName,Constants.COUPON_GENERATION_MONITOR_SERVICE).log(LogLevel.getLevel(Constants.LOG_FINEST)," Alerts Execution Monitor Service Thread detected no problem in the execution of Alerts Service Thread for instance --->"+instanceName + "<---Sleeping for Default Alert Time");
          this.sleep(Constants.MONITOR_SERVICE_SLEEP_TIME * 60 * 1000);
        }
      }
      catch(Exception ex) {
        CommonUtilities.getLogger(instanceName,Constants.COUPON_GENERATION_MONITOR_SERVICE).log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in Alerts Execution Monitor Service Thread --->" + ex);
      }catch(Throwable th){
        System.out.println("Severe Exception in Alerts Execution Monitor Service Thread --->" + th.getStackTrace());
      }
    }
  }

  private String checkMonitorDate(){
    String alertMessage = null;

    GregorianCalendar currentTime = new GregorianCalendar();
    GregorianCalendar serviceTime = new GregorianCalendar();
    Date currentDateTime = null;
    Date serviceDateTime = null;
    long currentTimeInMillis = 0;
    long monitorTimeInMillis = 0;
    long timeDifference = 0;
    long divisor = 1000*60;
    try{
      currentDateTime = new Date();
      serviceDateTime = getMonitorDateTime();
      if(serviceDateTime != null){
        currentTime.setTime(currentDateTime);
        serviceTime.setTime(serviceDateTime);

        currentTimeInMillis = currentTime.getTimeInMillis();
        monitorTimeInMillis = serviceTime.getTimeInMillis();

        timeDifference = currentTimeInMillis - monitorTimeInMillis;
      //get time difference in Minutes
        timeDifference = timeDifference / divisor;
        CommonUtilities.getLogger(instanceName,
                                  Constants.COUPON_GENERATION_MONITOR_SERVICE).log(
            LogLevel.getLevel(Constants.LOG_FINEST),
            " method for checking Service Date Time against Current Date time --- Instance Name--->" + instanceName
            + "<---Service Name--->" + serviceName
            + "<---Current Date Time--->" + currentDateTime
            + "<---Monitor Date Time--->" + serviceDateTime
            + "<---currentTimeInMillis--->" + currentTimeInMillis
            + "<---monitorTimeInMillis--->" + monitorTimeInMillis
            + "<---timeDifference (in Minutes)--->" + timeDifference
            + "<---DEFAULT ALERT TIME (in Minutes)--->" +
            Constants.DEFAULT_ALERT_TIME);

        if (timeDifference > Constants.DEFAULT_ALERT_TIME) {
          alertMessage = prepareAlertMessage(serviceDateTime, currentDateTime,
                                             timeDifference);
        }
      }//end if checking service date time not null
    }catch(Exception ex){
      CommonUtilities.getLogger(instanceName,Constants.COUPON_GENERATION_MONITOR_SERVICE).log(LogLevel.getLevel(Constants.LOG_FINEST)," Exception in checking Service Date Time against Current Date time ---> " + ex);
    }
    return alertMessage;
  }

  private void sendEmailAlert(String message){
    AdminMailService mailThrd = new AdminMailService(message,Constants.ALERT_EMAIL_SUBJECT,Constants.MAIL_REPORT_ADMIN,Constants.MAIL_REPORT_FROM);
    mailThrd.start();
  }

  private String prepareAlertMessage(Date serviceDateTime,Date currentDateTime, long timeDifference){
    StringBuffer alertMsg = new StringBuffer();
    String [] msgList = null;

    try {
      alertMsg.append(Constants.ALERT_EMAIL_HDR);

      msgList = CommonUtilities.convertStringArray(Constants.ALERT_EMAIL_MSG,
          Constants.MAIL_MSG_DELIMETER_STRING);
      alertMsg.append("\n\n" + serviceName + msgList[0] + serviceName + msgList[1] + instanceName + msgList[2] + serviceName + msgList[3] + timeDifference + msgList[4]);
      alertMsg.append("\n\n Current Date Time: " + currentDateTime);
      alertMsg.append("\n\n Last Modified Date Time by " + serviceName + ": " +
                      serviceDateTime);
      alertMsg.append("\n\nTime Difference(In Minutes): " + timeDifference);
      alertMsg.append("\n\nDefault Alert Time(In Minutes): " + Constants.DEFAULT_ALERT_TIME);
      return alertMsg.toString();
    }
    catch (Exception ex) {
      CommonUtilities.getLogger(instanceName,Constants.COUPON_GENERATION_MONITOR_SERVICE).log(LogLevel.getLevel(Constants.LOG_FINEST)," Exception in Preparing Alert Message ---> " + ex);
      return null;
    }
  }

  public synchronized void setMonitorDateTime(Date currentDateTime){
    this.monitorDateTime = currentDateTime;
  }

  public synchronized Date getMonitorDateTime(){
    return this.monitorDateTime;
  }
 }
