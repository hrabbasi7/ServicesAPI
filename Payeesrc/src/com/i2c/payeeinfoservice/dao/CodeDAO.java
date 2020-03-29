package com.i2c.payeeinfoservice.dao;

import java.util.logging.Logger;
import java.util.Vector;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import com.i2c.payeeinfoservice.vo.*;
import com.i2c.payeeinfoservice.excep.*;
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
public class CodeDAO extends BaseDAO {

    private String instanceID = null;
    private Connection dbConn = null;
    private Logger lgrPayeesDAO = null;

    public CodeDAO() {
    }

    CodeDAO(String instanceID, Connection dbConn, Logger lgrPayeesDAO) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
        this.lgrPayeesDAO = lgrPayeesDAO;
    }

    public CodeDAO(String instanceID, Connection dbConn) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
    }

    public CodeDAO(String instanceID) {
        this.instanceID = instanceID;
        try {
            dbConn = DatabaseHandler.getConnection("CatagoryDAO", instanceID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList selectCodes(String payeeSno) {
        ArrayList payeeCodeList = new ArrayList();
        StringBuffer query = new StringBuffer();
        Statement smt = null;
        ResultSet rs = null;
        PayeeCodeVO payeeCodeVo = null;
        try {
            query.append(" SELECT payee_cid, mask, consumer_acct_len ");
            query.append(" FROM bp_payee_masks ");
            query.append(" WHERE payee_sno = '" + payeeSno + "'");
            query.append(" ORDER BY payee_sno");

            smt = dbConn.createStatement();
            rs = smt.executeQuery(query.toString());
            while (rs.next()) {
                payeeCodeVo = new PayeeCodeVO();
                if (rs.getString(1) != null) {
                    payeeCodeVo.setCodeChildId(rs.getString(1));
                }

                if (rs.getString(2) != null) {
                    payeeCodeVo.setCodeMask(rs.getString(2));
                }

                if (rs.getString(3) != null) {
                    payeeCodeVo.setCodeFieldLength(rs.getString(3));
                }

                //Add the elements in array
                payeeCodeList.add(payeeCodeVo);
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

        return payeeCodeList;
    }

    public boolean insertCodes(long payeeId,
                               ArrayList payeeCodeList) {
        boolean flag = true;
        PayeeCodeVO payeeCodeVo = null;
        try {
            for (int i = 0; i < payeeCodeList.size(); i++) {
                payeeCodeVo = (PayeeCodeVO) payeeCodeList.get(i);
                insertPayeeCode(payeeId, payeeCodeVo);
            }
        } catch (Exception ex) { //end if
            flag = false;
        }
        return flag;
    }

    public boolean updateCodes(long payeeSNo,
                               ArrayList payeeCodeList) {
        boolean flag = true;
        int updateSucessful = -1;
        PayeeCodeVO payeeCodeVo = null;
        try {
            for (int i = 0; i < payeeCodeList.size(); i++) {
                payeeCodeVo = (PayeeCodeVO) payeeCodeList.get(i);
                updateSucessful = updatePayeeCode(payeeSNo, payeeCodeVo);
                if (updateSucessful == 0){
                    insertPayeeCode(payeeSNo, payeeCodeVo);
                }
            }
        } catch (Exception ex) { //end if
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    boolean insertPayeeCode(long payeeSNo,
                            PayeeCodeVO payeeCode) {
        boolean flag = true;
        StringBuffer query = new StringBuffer();
        try {
            if (payeeCode != null) {
                query.append("INSERT INTO bp_payee_masks (payee_sno,");
                query.append(" mask, payee_cid, consumer_acct_len) VALUES (");
                query.append(payeeSNo + ",");
                query.append(CommonUtilities.convertValidValue(payeeCode.
                        getCodeMask()) + ",");
                query.append(CommonUtilities.convertValidValue(payeeCode.
                        getCodeChildId()) + ",");
                query.append(Integer.parseInt(payeeCode.getCodeFieldLength()));
                query.append(")");
                PayeeMapper.lgr.log(I2cLogger.FINE, "insertPayeeCode--> " + query.toString());
                //Save the status
                this.insertValues(query.toString(), dbConn);
            }
        } catch (Exception ex) { //end if
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

   int updatePayeeCode(long payeeSNo,
                            PayeeCodeVO payeeCode) {
        StringBuffer query = new StringBuffer();
        boolean flag = true;
        int updateResult = -1;
        Statement stmt = null;

        try {
            query.append(" UPDATE bp_payee_masks SET ");
            query.append("mask = ");
            CommonUtilities.buildQueryInfo(query, payeeCode.getCodeMask(), false);
            query.append("payee_cid = ");
            CommonUtilities.buildQueryInfo(query, payeeCode.getCodeChildId(), false);
            query.append("consumer_acct_len = ");
            CommonUtilities.buildQueryInfo(query, payeeCode.getCodeFieldLength(), true);
            query.append(" WHERE payee_sno = " + payeeSNo);
            query.append(" AND mask = ");
            CommonUtilities.buildQueryInfo(query, payeeCode.getCodeMask(), true);
            query.append(" AND payee_cid = ");
            CommonUtilities.buildQueryInfo(query, payeeCode.getCodeChildId(), true);

            PayeeMapper.lgr.log(I2cLogger.FINE, "updatePayeeCode ---> " + query.toString());

            stmt = dbConn.createStatement();
            updateResult = stmt.executeUpdate(query.toString());
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
        return updateResult;
    }


}
