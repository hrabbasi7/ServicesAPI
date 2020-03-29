package com.i2c.service.couponschedularservice;

import com.i2c.service.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import com.i2c.service.couponschedularservice.monitor.CouponGenerationMonitorThread;

public class CouponGenerationServiceThread
    extends Thread {

  private String instanceName = null;
  private CouponGenerationMonitorThread couponMonitor = null;

  public CouponGenerationServiceThread(String instanceName,
                                       CouponGenerationMonitorThread couponMonitor) {
    this.instanceName = instanceName;
    this.couponMonitor = couponMonitor;
    CommonUtilities.getLogger(instanceName,
                              Constants.COUPON_GENERATION_SERVICE).log(LogLevel.
        getLevel(
            Constants.LOG_FINEST),
        " CouponGenerationServiceThread --- Setting the Instance Name -->" +
        instanceName + "<---Monitor Thread-->" +
        couponMonitor);

  }

  public void run() {
    Connection dbConn = null;
    CouponGenerationServiceHome serviceHome = null;
    try {
      CommonUtilities.getLogger(instanceName,
                                Constants.COUPON_GENERATION_SERVICE).
          log(LogLevel.getLevel(Constants.LOG_CONFIG),
              "<---CouponGenerationServiceThread -- run -- --- Invoking thread for generating coupons for instance name---> " +
              instanceName);
      CommonUtilities.getLogger(instanceName,
                                Constants.COUPON_GENERATION_SERVICE).log(LogLevel.getLevel(
          Constants.LOG_FINEST),
          "CouponGenerationServiceThread -- Method Name: run --- Getting Database Connection for instance --->" +
          instanceName);
      dbConn = DatabaseHandler.getConnection("CouponGenerationServiceThread",
                                             instanceName);
      serviceHome = new CouponGenerationServiceHome(instanceName,dbConn);
      while (true) {
        try {
          try {
            CommonUtilities.getLogger(instanceName,
                                      Constants.COUPON_GENERATION_SERVICE).log(LogLevel.getLevel(Constants.LOG_CONFIG),"CouponGenerationServiceThread -- Method Name: run --- Calling method for testing database connection");
            testConnection(dbConn);
          }
          catch (SQLException tstSqConEx) {
            CommonUtilities.getLogger(instanceName,
                                      Constants.COUPON_GENERATION_SERVICE).log(LogLevel.getLevel(Constants.LOG_WARNING),"CouponGenerationServiceThread -- Method Name: run --- SQL Exception in testing database connection -- Creating new Connection--->" + tstSqConEx);
            try{
              dbConn = DatabaseHandler.getConnection("CouponGenerationServiceThread",instanceName);
              serviceHome = new CouponGenerationServiceHome(instanceName,dbConn);
            }catch(Exception ex){
              CommonUtilities.getLogger(instanceName,
                                        Constants.COUPON_GENERATION_SERVICE).log(LogLevel.getLevel(Constants.LOG_WARNING),"CouponGenerationServiceThread -- Method Name: run --- Exception in creating new Connection");
            }
          }catch (Exception tstConEx) {
            CommonUtilities.getLogger(instanceName,
                                      Constants.COUPON_GENERATION_SERVICE).log(LogLevel.getLevel(Constants.LOG_WARNING),"CouponGenerationServiceThread -- Method Name: run --- Exception in testing database connection -- Creating new Connection--->" + tstConEx);
            try{
              dbConn = DatabaseHandler.getConnection("CouponGenerationServiceThread",instanceName);
              serviceHome = new CouponGenerationServiceHome(instanceName,dbConn);
            }catch(Exception ex){
              CommonUtilities.getLogger(instanceName,
                                        Constants.COUPON_GENERATION_SERVICE).log(LogLevel.getLevel(Constants.LOG_WARNING),"CouponGenerationServiceThread -- Method Name: run --- Exception in creating new Connection");
            }

          }catch (Throwable tstConTh) {
            CommonUtilities.getLogger(instanceName,
                                      Constants.COUPON_GENERATION_SERVICE).log(LogLevel.getLevel(Constants.LOG_WARNING),"CouponGenerationServiceThread -- Method Name: run --- Severe Exception in testing database connection -- Creating new Connection--->" + tstConTh);
            try{
              dbConn = DatabaseHandler.getConnection("CouponGenerationServiceThread",instanceName);
              serviceHome = new CouponGenerationServiceHome(instanceName,dbConn);
            }catch(Exception ex){
              CommonUtilities.getLogger(instanceName,
                                        Constants.COUPON_GENERATION_SERVICE).log(LogLevel.getLevel(Constants.LOG_WARNING),"CouponGenerationServiceThread -- Method Name: run --- Exception in creating new Connection");
            }
          }

          //Update Monitor thread
          CommonUtilities.getLogger(instanceName,
                                    Constants.COUPON_GENERATION_SERVICE).log(LogLevel.
              getLevel(Constants.LOG_FINEST),
              " \n\nUpdating Coupon Generation Monitoring Date Time ");
          Date currDate = getCurrentDate();
          CommonUtilities.getLogger(instanceName,
                                    Constants.COUPON_GENERATION_SERVICE).log(LogLevel.
              getLevel(Constants.LOG_FINEST),
              " \n\nUpdating Coupon Generation Monitoring Date Time --- Current Date Time Got--->" +
              currDate);
          couponMonitor.setMonitorDateTime(currDate);

          CommonUtilities.getLogger(instanceName,Constants.COUPON_GENERATION_SERVICE).log(LogLevel.getLevel(Constants.LOG_CONFIG),"<---Checking if service is allowed for this instance--->");

          if (serviceHome.processScheduler(dbConn,Constants.USE_COUPON_GEN)) {
            CommonUtilities.getLogger(instanceName,Constants.COUPON_GENERATION_SERVICE).log(LogLevel.getLevel(Constants.LOG_CONFIG),"<---invoking coupon generation service for this instance--->");
            invokeCouponGeneration(serviceHome);
            CommonUtilities.getLogger(instanceName,Constants.COUPON_GENERATION_SERVICE).log(LogLevel.getLevel(Constants.LOG_CONFIG),"<---coupon generation service invoked successfully, sleeping for --->" + Constants.COUPON_GEN_SLEEP_TIME + " millisecs");
            sleep(Constants.COUPON_GEN_SLEEP_TIME);
          }else{
            CommonUtilities.getLogger(instanceName, Constants.COUPON_GENERATION_SERVICE).
                log(LogLevel.getLevel(Constants.LOG_CONFIG),
                    "CouponGenerationServiceThread -- Coupon generation service is not allowed for this instance-->" + instanceName);
            sleep(Constants.DEFAULT_SLEEP_TIME);
          }
        }catch (Exception ex) {
          CommonUtilities.getLogger(instanceName,
                                    Constants.COUPON_GENERATION_SERVICE).log(
              LogLevel.getLevel(Constants.LOG_WARNING),
              "<---CouponGenerationServiceThread -- run -- --- Exception in thread for generating coupons ---> " +
              ex);
        }catch (Throwable th) {
          CommonUtilities.getLogger(instanceName,
                                    Constants.COUPON_GENERATION_SERVICE).log(
              LogLevel.getLevel(Constants.LOG_WARNING),
              "<---CouponGenerationServiceThread -- run -- --- Severe Exception in thread for generating coupons ---> " +
              th);
        }
      }
    }catch (Exception outerEx) {
      CommonUtilities.getLogger(instanceName,
                                Constants.COUPON_GENERATION_SERVICE).log(
      LogLevel.getLevel(Constants.LOG_WARNING),
      "<---CouponGenerationServiceThread -- run -- --- Exception in thread for generating coupons(Outer Catch) ---> " +
      outerEx);
    }catch (Throwable outerTh) {
      CommonUtilities.getLogger(instanceName,
                                Constants.COUPON_GENERATION_SERVICE).log(
      LogLevel.getLevel(Constants.LOG_WARNING),
      "<---CouponGenerationServiceThread -- run -- --- Severe Exception in thread for generating coupons(Outer Catch) ---> " +
      outerTh);
    }
  }

  private void invokeCouponGeneration(CouponGenerationServiceHome serviceHome) {
    try {
      CommonUtilities.getLogger(instanceName,
                                Constants.COUPON_GENERATION_SERVICE).log(
      LogLevel.
      getLevel(Constants.LOG_CONFIG),
      " Method for invoking coupon generation DB API ");

      serviceHome.callGenerateCoupons();

      CommonUtilities.getLogger(instanceName,
                          Constants.COUPON_GENERATION_SERVICE).log(
      LogLevel.
      getLevel(Constants.LOG_CONFIG),
      " Method for invoking coupon generation DB API completed successfully");

    }catch (Exception ex) {
      CommonUtilities.getLogger(instanceName,
                                Constants.COUPON_GENERATION_SERVICE).log(
      LogLevel.
      getLevel(Constants.LOG_CONFIG),
      " Exception in method for invoking coupon generation DB API--->" + ex);
    }
  }

  private Date getCurrentDate() {
      Date currDate = null;
      try {
        CommonUtilities.getLogger(instanceName,
                                  Constants.COUPON_GENERATION_SERVICE).log(LogLevel.
            getLevel(Constants.LOG_FINEST),
            " Getting current Date ");
        currDate = new Date();
      }
      catch (Exception ex) {
        CommonUtilities.getLogger(instanceName,
                                  Constants.COUPON_GENERATION_SERVICE).log(
                                      LogLevel.getLevel(Constants.LOG_WARNING),
                                      " Exception in Getting current Date --->" +
                                      ex);
      }
      return currDate;
  }

  private void testConnection(Connection dbConn) throws SQLException {

    StringBuffer query = new StringBuffer();
    Statement stmt = null;

    try {
      CommonUtilities.getLogger(instanceName,
                                 Constants.COUPON_GENERATION_SERVICE).log(LogLevel.
           getLevel(Constants.LOG_FINEST),
           " Method for testing database conenction ");

      query.append("select business_date from system_variables");
      stmt = dbConn.createStatement();
      stmt.executeQuery(query.toString());
      stmt.close();
    } catch (SQLException ex) {
      CommonUtilities.getLogger(instanceName,
                                Constants.COUPON_GENERATION_SERVICE).log(LogLevel.
          getLevel(Constants.LOG_FINEST),
          " Exception in testing database conenction --->" + ex + "<---Error Code--->" + ex.getErrorCode());
      if(dbConn != null){
        dbConn.close();
      }
      throw new SQLException("Exception in testing database connection...");
    }finally{
      if(stmt != null){
        stmt.close();
        stmt = null;
      }
    }
  }
}
