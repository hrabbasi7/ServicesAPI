package com.i2c.payeeinfoservice.dao;

import java.util.logging.Logger;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.sql.*;

import com.i2c.payeeinfoservice.util.*;
import com.i2c.payeeinfoservice.vo.*;
import com.i2c.payeeinfoservice.excep.*;
import com.i2c.payeeinfoservice.util.*;
import com.i2c.payeeinfoservice.mapper.PayeeMapper;
import com.i2c.utils.logging.*;

public class CatagoryDAO extends BaseDAO {
    private String instanceID = null;
    private Connection dbConn = null;
    private Logger lgrPayeesDAO = null;

    CatagoryDAO(String instanceID, Connection dbConn, Logger lgrPayeesDAO) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
        this.lgrPayeesDAO = lgrPayeesDAO;
    }

    public CatagoryDAO(String instanceID, Connection dbConn) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
    }

    public CatagoryDAO(String instanceID) {
        this.instanceID = instanceID;
        try {
            dbConn = DatabaseHandler.getConnection("CatagoryDAO", instanceID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean insertCatagories(long payeeSNo, ArrayList payeeCatagoryList) {
        boolean flag = true;
        PayeeCatagoryVO payeeCatagoryVo = null;
        try {
            for (int i = 0; i < payeeCatagoryList.size(); i++) {
                payeeCatagoryVo = (PayeeCatagoryVO) payeeCatagoryList.get(i);
                insertCatagoryName(payeeCatagoryVo);
                insertCatagoryDescription(payeeCatagoryVo);
                insertPayeeCatagory(payeeSNo, payeeCatagoryVo);
            }
        } catch (Exception ex) { //end if
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    public boolean updateCatagories(long payeeSNo, ArrayList payeeCatagoryList) {
        boolean flag = true;
        PayeeCatagoryVO payeeCatagoryVo = null;
        try {
            for (int i = 0; i < payeeCatagoryList.size(); i++) {
                payeeCatagoryVo = (PayeeCatagoryVO) payeeCatagoryList.get(i);
                if (updateCatagoryName(payeeCatagoryVo) == 0){
                    insertCatagoryName(payeeCatagoryVo);
                }
                if (updateCatagoryDescription(payeeCatagoryVo) == 0){
                    insertCatagoryDescription(payeeCatagoryVo);
                }
                if (updatePayeeCatagory(payeeSNo, payeeCatagoryVo) == 0){
                    insertPayeeCatagory(payeeSNo, payeeCatagoryVo);
                }
            }
        } catch (Exception ex) { //end if
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    public ArrayList selectCatagories(String payeeSno) {
        ArrayList payeeCatagoryList = new ArrayList();
        StringBuffer query = new StringBuffer();
        Statement smt = null;
        ResultSet rs = null;
        PayeeCatagoryVO payeeCatagoryVo = null;
        try {
            query.append(" SELECT pids.cat_group_id, pcats.payee_cat_id ");
            query.append("FROM bp_payee_cats pcats, bp_payee_cat_ids pids ");
            query.append("WHERE payee_sno = '" + payeeSno + "'");
            query.append("AND pcats.payee_cat_id = pids.payee_cat_id");

            smt = dbConn.createStatement();
            rs = smt.executeQuery(query.toString());
            while (rs.next()) {
                payeeCatagoryVo = new PayeeCatagoryVO();
                if (rs.getString(1) != null) {
                    payeeCatagoryVo.setCatagoryName(rs.getString(1));
                }

                if (rs.getString(2) != null) {
                    payeeCatagoryVo.setCatagoryDescription(rs.getString(2));
                }

                //Add the elements in array
                payeeCatagoryList.add(payeeCatagoryVo);
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

        return payeeCatagoryList;
    }

    boolean insertPayeeCatagory(long payeeId,
                                PayeeCatagoryVO payeeCatagory) {
        boolean flag = true;
        StringBuffer query = new StringBuffer();
        try {
            if (payeeCatagory != null) {
                query.append("INSERT INTO bp_payee_cats (payee_sno,");
                query.append(" payee_cat_id) VALUES (");
                query.append(payeeId + ",");
                query.append(CommonUtilities.convertValidValue(payeeCatagory.
                        getCatagoryDescription()));
                query.append(")");
                PayeeMapper.lgr.log(I2cLogger.FINE, "insertPayeeCatagory-->" + query.toString());
                //Save the status
                this.insertValues(query.toString(), dbConn);
            }
        } catch (Exception ex) { //end if
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    private int updatePayeeCatagory(long payeeSNo,
                                PayeeCatagoryVO payeeCatagory) {
        int updateFlag = -1;
        Statement stmt = null;
        StringBuffer query = new StringBuffer();
        try {
            if (payeeCatagory != null &&
                !isCatagoryName(payeeCatagory.getCatagoryDescription())) {
                query.append("UPDATE bp_payee_cats SET");
                query.append(" payee_cat_id = ");
                CommonUtilities.buildQueryInfo(query, payeeCatagory.getCatagoryDescription(), true);
                query.append(" WHERE payee_sno = " + payeeSNo);
                query.append(" AND payee_cat_id = ");
                CommonUtilities.buildQueryInfo(query, payeeCatagory.getCatagoryDescription(), true);

                PayeeMapper.lgr.log(I2cLogger.FINE, "updatePayeeCatagory -->" +
                                   query.toString());
                stmt = dbConn.createStatement();
                updateFlag = stmt.executeUpdate(query.toString());
            }
        } catch (Exception ex) {
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
        return updateFlag;
    }


    boolean insertCatagoryName(PayeeCatagoryVO payeeCatagory) {
        boolean flag = true;
        StringBuffer query = new StringBuffer();
        try {
            if (payeeCatagory != null &&
                !isCatagoryName(payeeCatagory.getCatagoryName())) {
                query.append("INSERT INTO bp_payee_cat_grps (cat_group_id,");
                query.append(" cat_group_desc, cat_group_abrv) VALUES (");
                query.append(CommonUtilities.convertValidValue(payeeCatagory.
                        getCatagoryName()) + ",");
                query.append(CommonUtilities.convertValidValue("") + ",");
                query.append(CommonUtilities.convertValidValue(""));
                query.append(")");
                PayeeMapper.lgr.log(I2cLogger.FINE, "insertCatagoryName -->" + query.toString());
                //Save the status
                this.insertValues(query.toString(), dbConn);
            }
        } catch (Exception ex) { //end if
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    private int updateCatagoryName(PayeeCatagoryVO payeeCatagory) {
        int updateFlag = -1;
        Statement stmt = null;
        StringBuffer query = new StringBuffer();
        try {
            if (payeeCatagory != null &&
                !isCatagoryName(payeeCatagory.getCatagoryName())) {
                query.append("UPDATE bp_payee_cat_grps SET");
                query.append(" cat_group_id = ");
                CommonUtilities.buildQueryInfo(query, payeeCatagory.getCatagoryName(), true);
//                query.append(",cat_group_desc = ''");
//                query.append(",cat_group_abrv = ''");
                query.append(" WHERE cat_group_id = ");
                CommonUtilities.buildQueryInfo(query, payeeCatagory.getCatagoryName(), true);

                PayeeMapper.lgr.log(I2cLogger.FINE, "updateCatagoryName -->" + query.toString());
                stmt = dbConn.createStatement();
                updateFlag = stmt.executeUpdate(query.toString());
            }
        } catch (Exception ex) {
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
        return updateFlag;
    }


    boolean insertCatagoryDescription(PayeeCatagoryVO payeeCatagory) {
        boolean flag = true;
        StringBuffer query = new StringBuffer();
        try {
            if (payeeCatagory != null &&
                !isCatagoryDescription(payeeCatagory.getCatagoryDescription())) {
                query.append("INSERT INTO bp_payee_cat_ids (payee_cat_id,");
                query.append(" cat_group_id , payee_cat_desc, payee_cat_abrv)");
                query.append(" VALUES (");
                query.append(CommonUtilities.convertValidValue(payeeCatagory.
                        getCatagoryDescription()) + ",");
                query.append(CommonUtilities.convertValidValue(payeeCatagory.
                        getCatagoryName()) + ",");
                query.append(CommonUtilities.convertValidValue("") + ",");
                query.append(CommonUtilities.convertValidValue(""));
                query.append(")");
                PayeeMapper.lgr.log(I2cLogger.FINE, "insertCatagoryDescription-->" +
                                   query.toString());
                //Save the status
                this.insertValues(query.toString(), dbConn);
            }
        } catch (Exception ex) { //end if
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    private int updateCatagoryDescription(PayeeCatagoryVO payeeCatagory) {
        int updateFlag = -1;
        Statement stmt = null;
        StringBuffer query = new StringBuffer();
        try {
            if (payeeCatagory != null &&
                !isCatagoryName(payeeCatagory.getCatagoryName())) {
                query.append("UPDATE bp_payee_cat_ids SET");
                query.append(" payee_cat_id = ");
                CommonUtilities.buildQueryInfo(query, payeeCatagory.getCatagoryDescription(), false);
//                query.append(",payee_cat_desc = ''");
//                query.append(",payee_cat_abrv = ''");
                query.append("cat_group_id = ");
                CommonUtilities.buildQueryInfo(query, payeeCatagory.getCatagoryName(), true);
                query.append(" WHERE payee_cat_id = ");
                CommonUtilities.buildQueryInfo(query, payeeCatagory.getCatagoryDescription(), true);

                PayeeMapper.lgr.log(I2cLogger.FINE, "updateCatagoryName -->" + query.toString());
                stmt = dbConn.createStatement();
                updateFlag = stmt.executeUpdate(query.toString());
            }
        } catch (Exception ex) {
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
        return updateFlag;

    }


    public boolean isCatagoryName(String catagoryName) {
        StringBuffer query = new StringBuffer();
        boolean payeeCatagoryNameExist = false;
        query.append(
                "SELECT cat_group_id FROM bp_payee_cat_grps WHERE cat_group_id = ");
        query.append("'" + catagoryName + "'");
        PayeeMapper.lgr.log(I2cLogger.FINE, "isCatagoryName ---> " + query.toString());
        try {
            Vector payeeCatagoryId = getKeyValues(query.toString(), dbConn);
            if (payeeCatagoryId.size() > 0) {
                payeeCatagoryNameExist = true;
            }
        } catch (LoadKeyValuesExcep ex) {
            payeeCatagoryNameExist = true;
            ex.printStackTrace();
        }
        return payeeCatagoryNameExist;
    }

    public boolean isCatagoryDescription(String catagoryDescription) {
        StringBuffer query = new StringBuffer();
        boolean payeeCatagoryDescExist = false;
        query.append(
                "SELECT payee_cat_id FROM bp_payee_cat_ids WHERE payee_cat_id = ");
        query.append("'" + catagoryDescription + "'");
        PayeeMapper.lgr.log(I2cLogger.FINE, "isCatagoryDescription ---> " + query.toString());
        try {
            Vector payeeCatagoryId = getKeyValues(query.toString(), dbConn);
            if (payeeCatagoryId.size() > 0) {
                payeeCatagoryDescExist = true;
            }
        } catch (LoadKeyValuesExcep ex) {
            payeeCatagoryDescExist = true;
            ex.printStackTrace();
        }
        return payeeCatagoryDescExist;
    }


}
