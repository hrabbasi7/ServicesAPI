package com.i2c.payeeinfoservice.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

import com.i2c.payeeinfoservice.excep.*;
import com.i2c.payeeinfoservice.util.*;
import com.i2c.payeeinfoservice.vo.*;
import com.i2c.utils.logging.I2cLogger;


/**
 * <p>Title: Payee Information Service</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Innovative Pvt. Ltd.</p>
 *
 * @author Haroon ur Rashid Abbasi
 * @version 1.0
 */
public class PayeesDAO extends BaseDAO {
    private String instanceID = null;
    private Connection dbConn = null;
    private Logger lgr = null;


    public PayeesDAO(String instanceID, Connection dbConn, Logger lgrPayeesDAO) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
        this.lgr = lgrPayeesDAO;
    }

    public PayeesDAO(String instanceID, Connection dbConn) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
    }

//    public PayeesDAO(String instanceID) {
//        this.instanceID = instanceID;
//        try {
//            dbConn = DatabaseHandler.getConnection("PayeesDAO", instanceID);
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//    }

    public Hashtable selectPayees() {
        StringBuffer query = new StringBuffer();
        Hashtable payeeTable = new Hashtable();
        Statement smt = null;
        ResultSet rs = null;
        PayeeVO payeeVo = null;
        AddressDAO addressDao = null;
        PrefixDAO prefixDao = null;
        CodeDAO codeDao = null;
        CatagoryDAO catagoryDao = null;
        try {
            query.append(" SELECT  payee_sno, payee_id, payee_name, regional_flag, ");
            query.append(" state_list, is_active , address_flag ");
            query.append(" FROM bp_payees");
            query.append(" ORDER BY payee_sno");

            smt = dbConn.createStatement();
            rs = smt.executeQuery(query.toString());
            while (rs.next()) {
                         payeeVo = new PayeeVO();
                         if (rs.getString(1) != null)
                             payeeVo.setPayeeSNo(rs.getString(1));

                         if (rs.getString(2) != null)
                             payeeVo.setPayeeId(rs.getString(2));

                         if (rs.getString(3) != null)
                             payeeVo.setPayeeName(rs.getString(3));

                         if (rs.getString(4) != null)
                             payeeVo.setPayeeRegion(rs.getString(4));

                         if (rs.getString(5) != null)
                             payeeVo.setPayeeState(rs.getString(5));

                         if (rs.getString(6) != null)
                             payeeVo.setPayeeStatus(rs.getString(6));

                         if (rs.getString(7) != null)
                             payeeVo.setPayeeAddressFlag(rs.getString(7));

                         if (payeeVo.getPayeeAddressFlag().equals("Y")){
                             addressDao = new AddressDAO(instanceID, dbConn);
                             payeeVo.setPayeeAddress(addressDao.selectAddresses(payeeVo.getPayeeSNo()));
//                             if (payeeVo.getPayeeAddress() != null)
//                             lgr.log(I2cLogger.FINE, "payee Address list size --> " + payeeVo.getPayeeAddress().size());
                         }
                         prefixDao = new PrefixDAO(instanceID, dbConn);
                         payeeVo.setPayeePrefix(prefixDao.selectPrefixs(payeeVo.getPayeeSNo()));
//                         if (payeeVo.getPayeePrefix() != null)
//                         lgr.log(I2cLogger.FINE, "Prefix list ---> " + payeeVo.getPayeePrefix().size());

                         codeDao = new CodeDAO(instanceID, dbConn);
                         payeeVo.setPayeeCode(codeDao.selectCodes(payeeVo.getPayeeSNo()));
//                         if (payeeVo.getPayeeCode() != null)
//                         lgr.log(I2cLogger.FINE, "Code list ---> " + payeeVo.getPayeeCode().size());

                         catagoryDao = new CatagoryDAO(instanceID, dbConn);
                         payeeVo.setPayeeCatagory(catagoryDao.selectCatagories(payeeVo.getPayeeSNo()));
//                         if (payeeVo.getPayeeCatagory() != null)
//                         lgr.log(I2cLogger.FINE, "Catagory list ---> " + payeeVo.getPayeeCatagory().size());

                         lgr.log(I2cLogger.FINE, "DB retrived Payee ID ---> " + payeeVo.getPayeeId());

                         //Add the elements in array
                         payeeTable.put(payeeVo.getPayeeId(), payeeVo);
                     } //end while

                 } catch (Exception ex) {
                     ex.printStackTrace();
                 } finally {
                     try {
                         if (rs != null) {
                             rs.close();
                         }
                         if (smt != null) {
                             smt.close();
                         }
                     } catch (Exception ex) {}
                 } //end finally
                 return payeeTable;
             }

    public long insertPayee(PayeeVO payee) {
        long serialNo = -1;
        StringBuffer query = new StringBuffer();
        try {

            if (payee != null) {
                query.append(
                        "INSERT INTO bp_payees (processor_id, payee_id, payee_name,");
                query.append(
                        "regional_flag, state_list, is_active, address_flag ) VALUES (");
                query.append("'1',");
                query.append(CommonUtilities.convertValidValue(payee.getPayeeId()) +
                             ",");
                query.append(CommonUtilities.convertValidValue(payee.
                        getPayeeName()) + ",");
                query.append(CommonUtilities.convertValidValue(payee.
                        getPayeeRegion()) + ",");
                query.append(
                        ((CommonUtilities.convertValidValue(payee.getPayeeState()) == null)
                         ? "''," :
                         CommonUtilities.convertValidValue(payee.getPayeeState())
                         + ","));

                query.append(CommonUtilities.convertValidValue(payee.
                        getPayeeStatus()) + ",");
                query.append(CommonUtilities.convertValidValue(payee.
                        getPayeeAddressFlag()));
                query.append(")");
                lgr.log(I2cLogger.FINE, "insertPayee ---> " + query.toString());
                //Save the status
                serialNo = this.insertValues(query.toString(), dbConn);
            }
        } catch (Exception ex) { //end if
            ex.printStackTrace();
        }
        return serialNo;
    }

    public void updatePayee(PayeeVO payee) {
        StringBuffer query = new StringBuffer();
        Statement stmt = null;

        try {
            query.append("UPDATE bp_payees SET ");
            query.append("payee_id = ");
            CommonUtilities.buildQueryInfo(query, payee.getPayeeId(), false);
            query.append("payee_name = ");
            CommonUtilities.buildQueryInfo(query, payee.getPayeeName(), false);
            query.append("regional_flag = ");
            CommonUtilities.buildQueryInfo(query, payee.getPayeeRegion(), false);
            query.append("state_list = ");
            CommonUtilities.buildQueryInfo(query, (payee.getPayeeState()
                        == null) ? "''" : payee.getPayeeState(), false);
            query.append("is_active = ");
            CommonUtilities.buildQueryInfo(query, payee.getPayeeStatus(), false);
            query.append("address_flag = ");
            CommonUtilities.buildQueryInfo(query, payee.getPayeeAddressFlag(), true);
            query.append(" WHERE ");
            query.append("payee_id = ");
            CommonUtilities.buildQueryInfo(query, payee.getPayeeId(), true);
            query.append(" AND ");
            query.append("payee_sno = ");
            CommonUtilities.buildQueryInfo(query, payee.getPayeeSNo(), true);

            lgr.log(I2cLogger.FINE, "updatePayee ----> " + query.toString());
            stmt = dbConn.createStatement();
            stmt.executeUpdate(query.toString());
        } catch (Exception ex) {
            lgr.log(I2cLogger.SEVERE, "Exception in updatePayee ----> " + query.toString() +
                    "Exception is " + ex.toString());
            ex.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
    }

    public boolean isPayeeAlreadyExist(PayeeVO payeeVo) {
        StringBuffer query = new StringBuffer();
        boolean payeeExist = false;
        try {
            query.append(" SELECT payee_sno FROM bp_payees ");
            query.append(" WHERE payee_id = '" + payeeVo.getPayeeId() + "'");
//            query.append(" AND payee_name = '" + payeeVo.getPayeeName() + "'");
            lgr.log(I2cLogger.FINE, "isPayeeAlreadyExist --->" + query.toString());
            if (checkValueExist(dbConn, query.toString())){
                payeeExist = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            lgr.log(I2cLogger.SEVERE, "Exception in isPayeeAlreadyExist----- " + ex.toString());
        }  //end finally
        return payeeExist;
    }

    public long getPayeeSerialNo(PayeeVO payeeVo){
        StringBuffer query = new StringBuffer();
        Statement stmt = null;
        ResultSet rs = null;
        long payeeSerialNo = -1;
        query.append("SELECT payee_sno FROM bp_payees");
        query.append(" WHERE payee_id = '" + payeeVo.getPayeeId() + "'");
        try {
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(query.toString());
        while (rs.next()) {
            if (rs.getLong(1) != -1)
                payeeSerialNo = rs.getLong(1);
            lgr.log(I2cLogger.FINE, "Payee SNO: " + payeeSerialNo);
        }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
        return payeeSerialNo;
    }

    public long getMaxPayee() {
        StringBuffer query = new StringBuffer();
        long countPayeeSerialNo = -1;
        query.append("SELECT payee_sno FROM bp_payees");
        try {
            countPayeeSerialNo = getMaxValue(query.toString(), dbConn);
        } catch (GetMaxValueExcep ex) {
            ex.printStackTrace();
        }
        return countPayeeSerialNo;
    }
}
