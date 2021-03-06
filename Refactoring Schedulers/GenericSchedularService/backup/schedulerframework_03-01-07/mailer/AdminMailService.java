package com.i2c.schedulerframework.mailer;
import java.io.*;
import java.net.*;
import java.util.*;
import com.i2c.schedulerframework.util.*;
import com.i2c.utils.logging.I2cLogger;
import com.i2c.schedulerframework.mailer.ServiceMail;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class AdminMailService extends Thread {
  String message = null;
  String subject = null;
  String toEmails = BaseConstants.MAIL_REPORT_ADMIN;
  String fromEmail = BaseConstants.MAIL_REPORT_ADMIN;
  //Main Constructor
  public AdminMailService(String _message,String _subject) {
    message = _message;
    subject = _subject;
//    ServiceMain.lgr.log(I2cLogger.FINEST," Admin Mail Send Constructor Message -->"+message+"<--- Subject -->"+subject);
  }//end constrcutor

//Main Constructor
  public AdminMailService(String _message,String _subject,String toEmailAddresses, String fromAddress) {
    message = _message;
    subject = _subject;
    toEmails = toEmailAddresses;
    fromEmail = fromAddress;
//    ServiceMain.lgr.log(I2cLogger.FINEST," Admin Mail Send Constructor Message -->"+message+"<--- Subject -->"+subject+"<--- toEmails -->"+toEmails+"<--- fromEmail -->"+fromEmail);
  }//end constrcutor


  public void run() {
//    ServiceMain.lgr.log(I2cLogger.FINEST," Admin Mail Service Thread is Started Report -->"+message);
    try {
        for (int i = 0; i < BaseConstants.MAX_EMAIL_RETRY; ++i) {
//          ServiceMain.lgr.log(I2cLogger.FINEST,"<--- Maximum  Email Retry -->"+i);
          if (sendAdminReportFailedEmail()) {
//            ServiceMain.lgr.log(I2cLogger.FINEST,"<--- Success Email Send Clear Things -->");
            break;
          }//end if
        }//end for
    } catch (Exception ex) {
//      ServiceMain.lgr.log(I2cLogger.WARNING," Exception in Admin Serive Thread -->"+ex);
    }//end catch
  }//end run

  /**
   * Method for sending an email to admin
   * @return: true of false;
   */
  private boolean sendAdminReportFailedEmail(){
    boolean flag = true;
    ServiceMail mailObj = null;
    try {
      mailObj = new ServiceMail();
//      ServiceMain.lgr.log(I2cLogger.WARNING," Send Admin Email Method-->"+message+"<-- Subject -->"+subject+"<--- toEmails -->"+toEmails);

      //Setting the From Email Address
      mailObj.setFrom(fromEmail);
      mailObj.setMessage(message);
      mailObj.setRecipientsTO(CommonUtilities.convertStringArray(toEmails,BaseConstants.DEFAULT_DELIMETER_STRING));
      mailObj.setSMTPServer(BaseConstants.MAIL_SMTP);
      mailObj.setSubject(subject);
      //Send an Email
      mailObj.postMail();
    } catch (Exception ex) {
//      ServiceMain.lgr.log(I2cLogger.WARNING," Exception in Send Admin Email -->"+ex);
      flag = false;
    }//end catch
    return flag;
  }//end send admin email
}//end class
