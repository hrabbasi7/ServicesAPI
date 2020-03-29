package com.i2c.service.couponschedularservice.bl;

import com.i2c.service.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import com.i2c.schedulerframework.monitor.SchedulerHangNotifier;
import com.i2c.schedulerframework.handler.BaseHandler;
import java.util.logging.Logger;
import com.i2c.service.util.Constants;
import com.i2c.service.couponschedularservice.dao.CouponGenerationServiceDAO;
import com.i2c.utils.logging.I2cLogger;
import java.io.File;
import com.i2c.schedulerframework.util.DatabaseConnectionUtil;


public class CouponGenerationServiceThread extends BaseHandler {

  private String instanceName = null;
  private SchedulerHangNotifier couponMonitor = null;
  private Logger couponLogger = null;

  public CouponGenerationServiceThread(String instanceName,
                                       SchedulerHangNotifier couponMonitor) {
    this.instanceName = instanceName;
    this.couponMonitor = couponMonitor;

    this.couponLogger = I2cLogger.getInstance(
      Constants.LOG_FILE_PATH +
      Constants.COUPON_GENERATION_SERVICE + File.separator +
      Constants.COUPON_GENERATION_SERVICE + "-log-%g.log",
      Constants.LOG_FILE_SIZE,
      Constants.LOG_FILE_NO,
      Constants.COUPON_GENERATION_SERVICE);

  }




  public CouponGenerationServiceThread(String instanceName,
                                       SchedulerHangNotifier couponMonitor,
                                       Logger lgr) {
    this.instanceName = instanceName;
    this.couponMonitor = couponMonitor;
    this.couponLogger = lgr;

  }





//  public void run() {
  public void mainBusinessLogic() {
    Connection dbConn = null;
    CouponGenerationServiceDAO serviceHome = null;
    try {
      couponLogger.log(I2cLogger.INFO,
                       "[CouponGenerationServiceThread].[run-mainBusinessLogic] Invoking thread for generating coupons for instance name---> " +
                       instanceName);
      couponLogger.log(I2cLogger.FINEST,
                       "[CouponGenerationServiceThread].[run-mainBusinessLogic] Getting Database Connection for instance --->" +
                       instanceName);
      dbConn = DatabaseConnectionUtil.getConnection("CouponGenerationServiceThread", instanceName, couponLogger);
      serviceHome = new CouponGenerationServiceDAO(instanceName,dbConn, couponLogger);
      while (true) {
        try {
          try {
            couponLogger.log(I2cLogger.CONFIG,"[CouponGenerationServiceThread].[run-mainBusinessLogic] Calling method for testing database connection");
            testConnection(dbConn);
          }
          catch (SQLException tstSqConEx) {
            couponLogger.log(I2cLogger.WARNING,"[CouponGenerationServiceThread].[run-mainBusinessLogic] SQL Exception in testing database connection -- Creating new Connection--->" + tstSqConEx);
            try{
              dbConn = DatabaseConnectionUtil.getConnection("CouponGenerationServiceThread",instanceName, couponLogger);
              serviceHome = new CouponGenerationServiceDAO(instanceName, dbConn, couponLogger);
            }catch(Exception ex){
              couponLogger.log(I2cLogger.WARNING,"[CouponGenerationServiceThread].[run-mainBusinessLogic] Exception in creating new Connection");
            }
          }catch (Exception tstConEx) {
            couponLogger.log(I2cLogger.WARNING,"[CouponGenerationServiceThread].[run-mainBusinessLogic] Exception in testing database connection -- Creating new Connection--->" + tstConEx);
            try{
              dbConn = DatabaseConnectionUtil.getConnection("CouponGenerationServiceThread",instanceName, couponLogger);
              serviceHome = new CouponGenerationServiceDAO(instanceName, dbConn, couponLogger);
            }catch(Exception ex){
              couponLogger.log(I2cLogger.WARNING,"[CouponGenerationServiceThread].[run-mainBusinessLogic] Exception in creating new Connection");
            }

          }catch (Throwable tstConTh) {
            couponLogger.log(I2cLogger.WARNING,"[CouponGenerationServiceThread].[run-mainBusinessLogic] Severe Exception in testing database connection -- Creating new Connection--->" + tstConTh);
            try{
              dbConn = DatabaseConnectionUtil.getConnection("CouponGenerationServiceThread",instanceName, couponLogger);
              serviceHome = new CouponGenerationServiceDAO(instanceName, dbConn, couponLogger);
            }catch(Exception ex){
              couponLogger.log(I2cLogger.WARNING,"[CouponGenerationServiceThread].[run-mainBusinessLogic] Exception in creating new Connection");
            }
          }

          //Update Monitor thread
          couponLogger.log(I2cLogger.FINEST,
                           "[CouponGenerationServiceThread].[run-mainBusinessLogic]\n\nUpdating Coupon Generation Monitoring Date Time ");
          Date currDate = getCurrentDate();
          couponLogger.log(I2cLogger.INFO,
                           "[CouponGenerationServiceThread].[run-mainBusinessLogic] \n\nUpdating Coupon Generation Monitoring Date Time --- Current Date Time Got--->" +
                           currDate);
          couponMonitor.setMonitorDateTime(currDate);

          couponLogger.log(I2cLogger.CONFIG,"[CouponGenerationServiceThread].[run-mainBusinessLogic]<--Checking if service is allowed for this instance-->");

          if (serviceHome.processScheduler(Constants.USE_COUPON_GEN, dbConn)) {
            couponLogger.log(I2cLogger.INFO,"[CouponGenerationServiceThread].[run-mainBusinessLogic]<---invoking coupon generation service for this instance--->");
            invokeCouponGeneration(serviceHome);
            couponLogger.log(I2cLogger.INFO,"[CouponGenerationServiceThread].[run-mainBusinessLogic]<---coupon generation service invoked successfully, sleeping for --->" + Constants.COUPON_GEN_SLEEP_TIME + " millisecs");
            Thread.sleep(Constants.COUPON_GEN_SLEEP_TIME);
          }else{
            couponLogger.log(I2cLogger.INFO,
                             "[CouponGenerationServiceThread].[run-mainBusinessLogic] Coupon generation service is not allowed for this instance-->" + instanceName);
            Thread.sleep(Constants.DEFAULT_SLEEP_TIME);
          }
        }catch (Exception ex) {
          couponLogger.log(I2cLogger.WARNING,
                           "[CouponGenerationServiceThread].[run-mainBusinessLogic] Exception in thread for generating coupons ---> " +
                           ex);
        }catch (Throwable th) {
          couponLogger.log(I2cLogger.WARNING,
                           "[CouponGenerationServiceThread].[run-mainBusinessLogic] Severe Exception in thread for generating coupons ---> " +
                           th);
        }
      }
    }catch (Exception outerEx) {
      couponLogger.log(I2cLogger.WARNING,
                       "[CouponGenerationServiceThread].[run-mainBusinessLogic] Exception in thread for generating coupons(Outer Catch) ---> " +
                       outerEx);
      outerEx.printStackTrace();
    }catch (Throwable outerTh) {
      couponLogger.log(I2cLogger.WARNING,
                       "[CouponGenerationServiceThread].[run-mainBusinessLogic] Severe Exception in thread for generating coupons(Outer Catch) ---> " +
                       outerTh);
      outerTh.printStackTrace();
    }
  }

  private void invokeCouponGeneration(CouponGenerationServiceDAO serviceHome) {
    try {
      couponLogger.log(I2cLogger.INFO,
                       "[CouponGenerationServiceThread].[invokeCouponGeneration] Method for invoking coupon generation DB API ");

      serviceHome.callGenerateCoupons();

      couponLogger.log(I2cLogger.INFO,
                       "[CouponGenerationServiceThread].[invokeCouponGeneration] Method for invoking coupon generation DB API completed successfully");

    }catch (Exception ex) {
      couponLogger.log(I2cLogger.WARNING,
                       "[CouponGenerationServiceThread].[invokeCouponGeneration] Exception in method for invoking coupon generation DB API--->" + ex);
      ex.printStackTrace();
    }
  }

  private Date getCurrentDate() {
      Date currDate = null;
      try {
        couponLogger.log(I2cLogger.FINEST, "[CouponGenerationServiceThread].[getCurrentDate] Getting current Date ");
        currDate = new Date();
      }
      catch (Exception ex) {
        couponLogger.log(I2cLogger.WARNING, "[CouponGenerationServiceThread].[getCurrentDate] Exception in Getting current Date --->" + ex);
      }
      return currDate;
  }

  private void testConnection(Connection dbConn) throws SQLException {

    StringBuffer query = new StringBuffer();
    Statement stmt = null;

    try {
      couponLogger.log(I2cLogger.FINEST,
                       "[CouponGenerationServiceThread].[testConnection] Method for testing database conenction ");

      query.append("select business_date from system_variables");
      stmt = dbConn.createStatement();
      stmt.executeQuery(query.toString());
      stmt.close();
    } catch (SQLException ex) {
      couponLogger.log(I2cLogger.WARNING,
                       "[CouponGenerationServiceThread].[testConnection] Exception in testing database conenction --->" + ex + "<---Error Code--->" + ex.getErrorCode());
      if(dbConn != null){
        dbConn.close();
      }
      throw new SQLException("[CouponGenerationServiceThread].[testConnection] Exception in testing database connection...");
    }finally{
      if(stmt != null){
        stmt.close();
        stmt = null;
      }
    }
  }
}
