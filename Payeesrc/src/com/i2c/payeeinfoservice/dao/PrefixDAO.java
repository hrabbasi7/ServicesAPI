package com.i2c.payeeinfoservice.dao;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.sql.Connection;

import java.util.logging.Logger;
import com.i2c.payeeinfoservice.vo.*;
import com.i2c.payeeinfoservice.util.*;
import com.i2c.payeeinfoservice.mapper.PayeeMapper;
import com.i2c.utils.logging.*;
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
public class PrefixDAO extends BaseDAO {

    private String instanceID = null;
    private Connection dbConn = null;
    private Logger lgrPayeesDAO = null;

    public PrefixDAO() {
    }

    PrefixDAO(String instanceID, Connection dbConn, Logger lgrPayeesDAO) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
        this.lgrPayeesDAO = lgrPayeesDAO;
    }

    public PrefixDAO(String instanceID, Connection dbConn) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
    }

    public PrefixDAO(String instanceID) {
        this.instanceID = instanceID;
        try {
            dbConn = DatabaseHandler.getConnection("CatagoryDAO", instanceID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList selectPrefixs(String payeeSno) {
        ArrayList payeePrefixList = new ArrayList();
        StringBuffer query = new StringBuffer();
        Statement smt = null;
        ResultSet rs = null;
        PayeePrefixVO payeePrefixVo = null;
        try {
            query.append(" SELECT payee_cid, prefix_from,");
            query.append(" prefix_to, consumer_acct_len ");
            query.append(" FROM bp_payee_prefixs ");
            query.append(" WHERE payee_sno = '" + payeeSno + "'" );
            query.append(" ORDER BY payee_sno");

            smt = dbConn.createStatement();
            rs = smt.executeQuery(query.toString());
            while (rs.next()) {
                         payeePrefixVo = new PayeePrefixVO();
                         if (rs.getString(1) != null)
                             payeePrefixVo.setPrefixChildId(rs.getString(1));

                         if (rs.getString(2) != null)
                             payeePrefixVo.setPrefixMaskFrom(rs.getString(2));

                         if (rs.getString(3) != null)
                             payeePrefixVo.setPrefixMaskTo(rs.getString(3));

                         if (rs.getString(4) != null)
                             payeePrefixVo.setPrefixLength(rs.getString(4));

                         //Add the elements in array
                         payeePrefixList.add(payeePrefixVo);
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

        return payeePrefixList;
    }

    public boolean insertPrefixes(long payeeId,
                                  ArrayList payeePrefixList) {
        boolean flag = true;
        PayeePrefixVO payeePrefixVo = null;
        try {
            for (int i = 0; i < payeePrefixList.size(); i++) {
                payeePrefixVo = (PayeePrefixVO) payeePrefixList.get(i);
                insertPayeePrefix(payeeId, payeePrefixVo);
            }
        } catch (Exception ex) { //end if
            flag = false;
        }
        return flag;
    }

    public boolean updatePrefixes(long payeeId,
                                  ArrayList payeePrefixList) {
        int returnValue = -1;
        boolean flag = true;
        PayeePrefixVO payeePrefixVo = null;
        try {
            for (int i = 0; i < payeePrefixList.size(); i++) {
                payeePrefixVo = (PayeePrefixVO) payeePrefixList.get(i);
                returnValue = updatePayeePrefix(payeeId, payeePrefixVo);
                if (returnValue == 0)
                    insertPayeePrefix(payeeId, payeePrefixVo);
            }
        } catch (Exception ex) { //end if
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }



    boolean insertPayeePrefix(long payeeId,
                              PayeePrefixVO payeePrefix) {
        boolean flag = true;
        StringBuffer query = new StringBuffer();
        try {
            if (payeePrefix != null) {
                query.append("INSERT INTO bp_payee_prefixs (payee_sno, payee_cid,");
                query.append(" prefix_from, prefix_to, consumer_acct_len) VALUES (");
                query.append(payeeId + ",");
                query.append(CommonUtilities.convertValidValue(
                        payeePrefix.getPrefixChildId()) + ",");
                query.append(CommonUtilities.convertValidValue(
                        payeePrefix.getPrefixMaskFrom()) + ",");
                query.append(CommonUtilities.convertValidValue(
                        payeePrefix.getPrefixMaskTo()) + ",");
                query.append(CommonUtilities.convertValidValue(
                        payeePrefix.getPrefixLength()));
                query.append(")");
                PayeeMapper.lgr.log(I2cLogger.FINE, query.toString());
                //Save the status
                this.insertValues(query.toString(), dbConn);
            }
        } catch (Exception ex) { //end if
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }


    public int updatePayeePrefix(long payeeSNo,
                              PayeePrefixVO payeePrefix) {
        StringBuffer query = new StringBuffer();
        int updateReturn = -1;
        boolean flag = true;
        Statement stmt = null;

        try {
            query.append("UPDATE bp_payee_prefixs SET");
            query.append(" payee_cid = ");
            CommonUtilities.buildQueryInfo(query, payeePrefix.getPrefixChildId(), false);
            query.append("prefix_from = ");
            CommonUtilities.buildQueryInfo(query, payeePrefix.getPrefixMaskFrom(), false);
            query.append("prefix_to = ");
            CommonUtilities.buildQueryInfo(query, payeePrefix.getPrefixMaskTo(), false);
            query.append("consumer_acct_len = ");
            CommonUtilities.buildQueryInfo(query, payeePrefix.getPrefixLength(), true);
            query.append(" WHERE payee_sno = " + payeeSNo);
            query.append(" AND payee_cid = ");
            CommonUtilities.buildQueryInfo(query, payeePrefix.getPrefixChildId(), true);
            query.append(" AND prefix_from = ");
            CommonUtilities.buildQueryInfo(query, payeePrefix.getPrefixMaskFrom(), true);
            query.append(" AND prefix_to = ");
            CommonUtilities.buildQueryInfo(query, payeePrefix.getPrefixMaskTo(), true);

            PayeeMapper.lgr.log(I2cLogger.FINE, "updatePayeePrefix ---> " + query.toString());

            stmt = dbConn.createStatement();
            updateReturn = stmt.executeUpdate(query.toString());
        } catch (Exception ex) {
            flag = false;
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
        return updateReturn;
    }


}
