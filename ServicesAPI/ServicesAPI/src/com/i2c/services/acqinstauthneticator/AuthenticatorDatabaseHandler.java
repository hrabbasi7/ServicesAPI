package com.i2c.services.acqinstauthneticator;

import java.sql.*;

import com.i2c.services.util.*;

public class AuthenticatorDatabaseHandler {

  private Connection dbConn = null;

  public AuthenticatorDatabaseHandler(Connection dbConn) {
    this.dbConn = dbConn;
  }

  AcquirerInfoObj getAcquirerInfo(String acqId) {
    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    AcquirerInfoObj acqInfo = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getAcquirerInfo --- Method for getting acquirer info --- Acq Id--->" +
                                      acqId);
      query.append("select acquirer_user_id, acquirer_user_pass, secret_key1, secret_key2, secret_key3, algo_code from ws_acquirers where acquirer_id = ? ");
      pstmt = dbConn.prepareStatement(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getAcquirerInfo --- Query for getting acquirer info --->" +
                                      query);
      pstmt.setString(1, acqId);
      rs = pstmt.executeQuery();

      if (rs.next()) {

        acqInfo = new AcquirerInfoObj();
        acqInfo.setAcqID(acqId);
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          acqInfo.setAcqUserID(rs.getString(1).trim());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          acqInfo.setAcqUserPassword(rs.getString(2).trim());
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          acqInfo.setSecurityKey1(rs.getString(3).trim());
        }
        if (rs.getString(4) != null && rs.getString(4).trim().length() > 0) {
          acqInfo.setSecurityKey2(rs.getString(4).trim());
        }
        if (rs.getString(5) != null && rs.getString(5).trim().length() > 0) {
          acqInfo.setSecurityKey3(rs.getString(5).trim());
        }
        if (rs.getString(6) != null && rs.getString(6).trim().length() > 0) {
          acqInfo.setAlgoCode(rs.getString(6).trim());
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AuthenticatorDatabaseHandler --- getAcquirerInfo --- No Info feteched for provided acquirer --->" +
                                        acqId);
        return null;
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getAcquirerInfo --- Exception in getting acquirer info --->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (SQLException ex1) {

      }
    }
    return acqInfo;
  }

  InstanceInfoObj getBINInstanceInfo(String cardBin) {
    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    InstanceInfoObj instInfo = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getInstanceInfo --- Method for getting instance info belonging to provided bin--- Card BIN--->" +
                                      cardBin);
      query.append("select i.instance_id,i.db_conn_string,i.db_user_id,i.db_user_passwd from instances i,card_bins c where c.card_bin = ? and c.instance_id = i.instance_id");
      pstmt = dbConn.prepareStatement(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getInstanceInfo --- Query for getting instance info belonging to provided bin --->" +
                                      query);
      pstmt.setString(1, cardBin);
      rs = pstmt.executeQuery();

      if (rs.next()) {

        instInfo = new InstanceInfoObj();
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          instInfo.setInstanceId(rs.getString(1).trim());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          instInfo.setConnStr(rs.getString(2).trim());
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          instInfo.setConnUsr(rs.getString(3).trim());
        }
        if (rs.getString(4) != null && rs.getString(4).trim().length() > 0) {
          instInfo.setConnPwd(rs.getString(4).trim());
        }
      }else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AuthenticatorDatabaseHandler --- getInstanceInfo --- No Instance Info feteched for provided card bin --->" +
                                        cardBin);
        return null;
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getInstanceInfo --- Exception in getting info belonging to provided bin --->" +
                                      ex);
    }finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (SQLException ex1) {

      }
    }
    return instInfo;
  }

  InstanceInfoObj getInstanceInfo(String instanceId) {
    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    InstanceInfoObj instInfo = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getInstanceInfo --- Method for getting instance info for provided instance id--->" +
                                      instanceId);
      query.append("select instance_id,db_conn_string,db_user_id,db_user_passwd from instances where instance_id = ? ");
      pstmt = dbConn.prepareStatement(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getInstanceInfo --- Query for getting instance info for provided instance id --->" +
                                      query);
      pstmt.setString(1, instanceId);
      rs = pstmt.executeQuery();

      if (rs.next()) {
        instInfo = new InstanceInfoObj();
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          instInfo.setInstanceId(rs.getString(1).trim());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          instInfo.setConnStr(rs.getString(2).trim());
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          instInfo.setConnUsr(rs.getString(3).trim());
        }
        if (rs.getString(4) != null && rs.getString(4).trim().length() > 0) {
          instInfo.setConnPwd(rs.getString(4).trim());
        }
      }else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AuthenticatorDatabaseHandler --- getInstanceInfo --- No Instance Info feteched for provided instanceId --->" +
                                        instanceId);
        return null;
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getInstanceInfo --- Exception in getting instance info for provided instance id --->" +
                                      ex);
    }finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return instInfo;
  }


  boolean checkInstanceAllowed(String acqId, String instId) {
    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- checkInstanceAllowed --- Method for cheking instance allowed to provided acquirer --- Acquirer Id--->" +
                                      acqId + "<---Inst Id--->" + instId);
      query.append("select is_allowed from acq_instances where acquirer_id = ? and instance_id = ? ");
      pstmt = dbConn.prepareStatement(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- checkInstanceAllowed --- Query for cheking instance allowed to provided acquirer --->" +
                                      query);
      pstmt.setString(1, acqId);
      pstmt.setString(2, instId);
      rs = pstmt.executeQuery();

      if (rs.next()) {
        String isAllowed = null;
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          isAllowed = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                          "AuthenticatorDatabaseHandler --- checkInstanceAllowed --- Query for cheking instance allowed to provided acquirer --- Is Allowed--->" +
                                          isAllowed);
          if(isAllowed.equals(Constants.YES_OPTION)){
            return true;
          }else{
            return false;
          }
        }else{
          return false;
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AuthenticatorDatabaseHandler --- checkInstanceAllowed --- No relationship between provided Instance & Acquirer IDs --- Acquirer Id--->" +
                                        acqId + "<---Inst Id--->" + instId);
        return false;
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- checkInstanceAllowed --- Exception in cheking instance allowed to provided acquirer  --->" +
                                      ex);
      return false;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (SQLException ex1) {

      }
    }
  }

  boolean checkServiceAllowed(String acqId, String srvId) {
    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- checkServiceAllowed --- Method for cheking service allowed to provided acquirer --- Acquirer Id--->" +
                                      acqId + "<---Service Id--->" + srvId);
      query.append("select is_allowed from acq_services where acquirer_id = ? and service_id = ? ");
      pstmt = dbConn.prepareStatement(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- checkServiceAllowed --- Query for cheking service allowed to provided acquirer --->" +
                                      query);
      pstmt.setString(1, acqId);
      pstmt.setString(2, srvId);
      rs = pstmt.executeQuery();

      if (rs.next()) {
        String isAllowed = null;
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          isAllowed = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                          "AuthenticatorDatabaseHandler --- checkServiceAllowed --- Query for checking service allowed to provided acquirer --- Is Allowed--->" +
                                          isAllowed);
          if(isAllowed.equals(Constants.YES_OPTION)){
            return true;
          }else{
            return false;
          }
        }else{
          return false;
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AuthenticatorDatabaseHandler --- checkServiceAllowed --- No relationship between provided Service & Acquirer IDs --- Acquirer Id--->" +
                                        acqId + "<---Service Id--->" + srvId);
        return false;
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- checkServiceAllowed --- Exception in cheking service allowed to provided acquirer  --->" +
                                      ex);
      return false;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (SQLException ex1) {

      }
    }
  }

  String getServiceId(String transType, String deviceType,String responseCode) {
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getServiceId --- Method for getting service id belonging to provided transaction type id --- Trans Type Id--->" +
                                      transType);
      query.append("Execute procedure get_service_id(f_device_type = ");
      if(deviceType != null && deviceType.trim().length() > 0){
        query.append("'" + deviceType + "',");
      }else{
        query.append(null + ",");
      }
      query.append(" process_code = ");
      if(transType != null && transType.trim().length() > 0){
        query.append("'" + transType + "',");
      }else{
        query.append(null + ",");
      }
      query.append("response_flag = ");
      if(responseCode != null && responseCode.trim().length() > 0){
        query.append("'" + responseCode + "')");
      }else{
        query.append(null + ")");
      }

      cstmt = dbConn.prepareCall(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getServiceId --- Query for getting service id belonging to provided transaction type id --->" +
                                      query);
      rs = cstmt.executeQuery();

      if (rs.next()) {
        String serviceId = null;
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          serviceId = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                          "AuthenticatorDatabaseHandler --- getServiceId --- Query for getting service id belonging to provided transaction type id  --- Service Id--->" +
                                          serviceId);
          return serviceId;
        }else{
          return null;
        }
      }
      else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "AuthenticatorDatabaseHandler --- getServiceId --- No serviceId found for provided Trans Type ID --- Trans Type Id--->" +
                                        transType);
        return null;
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getServiceId --- Exception in getting service id belonging to provided transaction type id  --->" +
                                      ex);
      return null;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (cstmt != null) {
          cstmt.close();
        }
      }
      catch (SQLException ex1) {

      }
    }
  }

  void logErrorTransaction(ErrorInfoObj errObj){
    StringBuffer query = new StringBuffer();
    StringBuffer serialQuery = new StringBuffer();
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;
    int serialNo = -1;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- logErrorTransaction --- Method for logging error transaction --- Log Date--->" + errObj.getLogDate()
                                      + "<---Log Time--->" + errObj.getLogTime()
                                      + "<---Trans Type--->" + errObj.getTransType()
                                      + "<---Serv Id--->" + errObj.getServId()
                                      + "<---Acquirer Id--->" + errObj.getAcquirerId()
                                      + "<---Bin--->" + errObj.getBin()
                                      + "<---Resp Code--->" + errObj.getRespCode()
                                      + "<---Resp Desc--->" + errObj.getRespDesc());
      query.append(" insert into excep_logs (log_date,log_time,trans_type,service_id,acquirer_id,bin,response_code,description) values(?,?,?,?,?,?,?,?)");
      pstmt = dbConn.prepareStatement(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getServiceId --- Query for logging error transaction --->" +
                                      query);
      pstmt.setString(1, errObj.getLogDate());
      pstmt.setString(2, errObj.getLogTime());
      pstmt.setString(3, errObj.getTransType());
      pstmt.setString(4, (errObj.getServId() != null && errObj.getServId().trim().length() > 0 ? errObj.getServId() : null));
      pstmt.setString(5, errObj.getAcquirerId());
      pstmt.setString(6, errObj.getBin());
      pstmt.setString(7, errObj.getRespCode());
      pstmt.setString(8, errObj.getRespDesc());

      pstmt.executeUpdate();

      if (query.toString().toLowerCase().indexOf(Constants.
                                                 INSERT_QUERY_INTO_VALUE.
                                                 toLowerCase()) > -1) {
        serialQuery.append(Constants.SERIAL_QUERY + " " +
                           query.toString().
                           substring(query.toString().
                                     toLowerCase().indexOf(Constants.
            INSERT_QUERY_INTO_VALUE.toLowerCase()) +
                                     Constants.INSERT_QUERY_INTO_VALUE.length(),
                                     query.toString().toLowerCase().indexOf(
            Constants.INSERT_QUERY_COLUMN_START_VALUE.toLowerCase())));
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        " Serail Number Query --> " +
                                        serialQuery);
        stmt = dbConn.createStatement();

        rs = stmt.executeQuery(serialQuery.toString());

        if (rs.next()) {
          if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
            serialNo = Integer.parseInt(rs.getString(1).trim());
            errObj.setSerialNo(Integer.toString(serialNo));
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                " Error Transaction ID --> " +
                                errObj.getSerialNo());

          }
        }
      } //end serial number if
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "AuthenticatorDatabaseHandler --- getServiceId --- Exception in logging error transaction  --->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
        if (pstmt != null) {
          pstmt.close();
        }
        if (stmt != null) {
          stmt.close();
        }
      }
      catch (SQLException ex1) {

      }
    }
  }
}
