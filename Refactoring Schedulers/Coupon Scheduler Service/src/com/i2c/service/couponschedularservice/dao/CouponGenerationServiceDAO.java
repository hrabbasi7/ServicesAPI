package com.i2c.service.couponschedularservice.dao;

import java.sql.*;
import com.i2c.schedulerframework.dao.BaseDAO;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.i2c.utils.logging.I2cLogger;

public class CouponGenerationServiceDAO extends BaseDAO {

  private Connection dbConn = null;
  private String instanceName = null;
  private Logger daoLogger = null;

  public CouponGenerationServiceDAO(String instanceName,Connection dbConn, Logger lgr) {
    super(lgr);
    this.instanceName = instanceName;
    this.dbConn = dbConn;
    this.daoLogger = lgr;
  }


  public boolean executeUpdate(String query) throws Exception {
    return true;
  }

  public boolean executeInsert(String query) throws Exception {
   return true;
 }

 public ArrayList executeSelect(String query) throws Exception {
   ArrayList arrayList = null;
   return arrayList;
 }


  public void callGenerateCoupons() {
    CallableStatement cs = null;

    daoLogger.log(I2cLogger.FINE,
            "[CouponGenerationServiceDAO].[callGenerateCoupons] start of callGenerateCoupons on " + instanceName);
    try {
      cs = dbConn.prepareCall("{call generate_coupons()}");
      daoLogger.log(I2cLogger.FINE,
          "[CouponGenerationServiceDAO].[callGenerateCoupons] Got Callable Statement for generate_coupons procedure--->" +
          cs);
      cs.execute();
      daoLogger.log(I2cLogger.INFO,
      "[CouponGenerationServiceDAO].[callGenerateCoupons] generate_coupons procedure executed successfully--->" + cs);
    }catch (Exception ex) {
      daoLogger.log(I2cLogger.WARNING,
      "[CouponGenerationServiceDAO].[callGenerateCoupons] Exception in processing callGenerateCoupons--->" + ex);
      ex.printStackTrace();
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




  public void callGenerateChargeTrans () {
   CallableStatement cStmt = null;

      daoLogger.log(I2cLogger.FINE,
              "[CouponGenerationServiceDAO].[callGenerateChargeTrans] start of <callGenerateChargeTrans> on " + instanceName);
      try {
        cStmt = dbConn.prepareCall("{call generate_charge_trans()}");
        daoLogger.log(I2cLogger.FINE,
            "[CouponGenerationServiceDAO].[callGenerateChargeTrans] Got Callable Statement for generate_charge_trans procedure--->" +
            cStmt);
        cStmt.execute();
        daoLogger.log(I2cLogger.INFO,
        "[CouponGenerationServiceDAO].[callGenerateChargeTrans] generate_charge_trans procedure executed successfully--->" + cStmt);
      }catch (Exception ex) {
        daoLogger.log(I2cLogger.WARNING,
        "[CouponGenerationServiceDAO].[callGenerateChargeTrans] Exception in processing callGenerateChargeTrans--->" + ex);
        ex.printStackTrace();
      }
      finally {
        try {
          if (cStmt != null) {
            cStmt.close();
          }
        }
        catch (Exception ex1) {}
   }
 }

}

