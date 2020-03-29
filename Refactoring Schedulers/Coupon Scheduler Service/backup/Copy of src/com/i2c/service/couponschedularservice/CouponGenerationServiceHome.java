package com.i2c.service.couponschedularservice;

import com.i2c.service.util.*;
import com.i2c.service.base.BaseHome;
import java.sql.*;

public class CouponGenerationServiceHome
    extends BaseHome {

  private Connection dbConn = null;
  private String instanceName = null;

  public CouponGenerationServiceHome(String instanceName,Connection dbConn) {
    this.instanceName = instanceName;
    this.dbConn = dbConn;
  }

  void callGenerateCoupons() {
    CallableStatement cs = null;

    CommonUtilities.getLogger(instanceName, Constants.COUPON_GENERATION_SERVICE).
        log(LogLevel.getLevel(Constants.LOG_CONFIG),
            " CouponGenerationServiceThread --- callGenerateCoupons ---");
    try {
      cs = dbConn.prepareCall("{call generate_coupons()}");
      CommonUtilities.getLogger(instanceName,
                                Constants.COUPON_GENERATION_SERVICE).log(
          LogLevel.getLevel(Constants.LOG_CONFIG),
          " CouponGenerationServiceThread --- callGenerateCoupons --- Got Callable Statement for generate_coupons procedure--->" +
          cs);
      cs.execute();
      CommonUtilities.getLogger(instanceName,
                                Constants.COUPON_GENERATION_SERVICE).log(
      LogLevel.getLevel(Constants.LOG_CONFIG),
      " CouponGenerationServiceThread --- callGenerateCoupons --- generate_coupons procedure executed successfully--->" + cs);
    }catch (Exception ex) {
      CommonUtilities.getLogger(instanceName,
                                Constants.COUPON_GENERATION_SERVICE).log(
      LogLevel.getLevel(Constants.LOG_WARNING),
      " CouponGenerationServiceThread --- callGenerateCoupons --- Exception in processing callGenerateCoupons--->" +
      ex);
    }
    finally {
      try {
        if (cs != null) {
          cs.close();
        }
      }
      catch (Exception ex1) {}
    }
  }
}
