package com.i2c.payeeinfoservice.dao;

import java.util.*;
import com.i2c.payeeinfoservice.vo.PayeeAddressVO;
import java.sql.*;
import com.i2c.payeeinfoservice.util.*;
import java.util.logging.Logger;
import com.i2c.payeeinfoservice.vo.PayeeAddressVO;
import com.i2c.payeeinfoservice.mapper.PayeeMapper;
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
public class AddressDAO extends BaseDAO {

    private String instanceID = null;
    private Connection dbConn = null;
    private Logger lgrPayeesDAO = null;


    public AddressDAO(String instanceID) {
        this.instanceID = instanceID;
        try {
            dbConn = DatabaseHandler.getConnection("AddressDAO", instanceID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    AddressDAO(String instanceID, Connection dbConn, Logger lgrPayeesDAO) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
        this.lgrPayeesDAO = lgrPayeesDAO;
    }

    public AddressDAO(String instanceID, Connection dbConn) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
    }

    public ArrayList selectAddresses(String payeeSno) {
        ArrayList payeeAddressList = new ArrayList();
        StringBuffer query = new StringBuffer();
        Statement smt = null;
        ResultSet rs = null;
        PayeeAddressVO payeeAddressVo = null;
        try {
            query.append(" SELECT payee_sno, addr_sno, payee_cid, street1, street2,");
            query.append(" city, state, zip_postal_code, country_code");
            query.append(" FROM bp_payee_addrs ");
            query.append(" WHERE payee_sno = '" + payeeSno + "'");
            query.append(" ORDER BY payee_sno");

            smt = dbConn.createStatement();
            rs = smt.executeQuery(query.toString());
            while (rs.next()) {
                payeeAddressVo = new PayeeAddressVO();
                if (rs.getString(1) != null) {
                    payeeAddressVo.setPayeeSNo(rs.getString(1));
                }

                if (rs.getString(2) != null) {
                    payeeAddressVo.setAddressSNo(rs.getString(2));
                }

                if (rs.getString(3) != null) {
                    payeeAddressVo.setAddressChildId(rs.getString(3));
                }

                if (rs.getString(4) != null) {
                    payeeAddressVo.setAddress1(rs.getString(4));
                }

                if (rs.getString(5) != null) {
                    payeeAddressVo.setAddress2(rs.getString(5));
                }

                if (rs.getString(6) != null) {
                    payeeAddressVo.setAddressCity(rs.getString(6));
                }

                if (rs.getString(7) != null) {
                    payeeAddressVo.setAddressState(rs.getString(7));
                }

                if (rs.getString(8) != null) {
                    payeeAddressVo.setAddressZIP(rs.getString(8));
                }

                if (rs.getString(9) != null) {
                    payeeAddressVo.setAddressCountryCode(rs.getString(9));
                }


                //Add the elements in array
                payeeAddressList.add(payeeAddressVo);
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
        return payeeAddressList;
    }

    public boolean insertAddress(PayeeAddressVO address, long payeeSNo) {
        boolean flag = true;
        StringBuffer query = new StringBuffer();
        try {
            if (address != null) {
                query.append(
                        "INSERT INTO bp_payee_addrs (payee_sno, payee_cid, street1,");
                query.append(
                        "street2, city, state, zip_postal_code, country_code ) VALUES (");

                query.append(payeeSNo + ",");
                query.append(CommonUtilities.convertValidValue(address.
                        getAddressChildId()) + ",");
                query.append(CommonUtilities.convertValidValue(address.
                        getAddress1()) + ",");
                query.append(
                        ((CommonUtilities.convertValidValue(address.getAddress2()) == null)
                         ? "''," :
                         CommonUtilities.convertValidValue(address.getAddress2())
                         + ","));
                query.append(CommonUtilities.convertValidValue(address.
                        getAddressCity()) + ",");
                query.append(CommonUtilities.convertValidValue(address.
                        getAddressState()) + ",");
                query.append(CommonUtilities.convertValidValue(address.
                        getAddressZIP()) + ",");
                query.append(CommonUtilities.convertValidValue("US") + ")");
                //Save the status
                this.insertValues(query.toString(), dbConn);
            }
        } catch (Exception ex) { //end if
            flag = false;
        }
        return flag;
    }

    public int updateAddress(PayeeAddressVO address, long payeeSNo) {
        StringBuffer query = new StringBuffer();
        int updateResult = -1;
        Statement stmt = null;

        try {
            query.append(" UPDATE bp_payee_addrs SET ");
            query.append("payee_cid = ");
            CommonUtilities.buildQueryInfo(query, address.getAddressChildId(), false);
            query.append("street1 = ");
            CommonUtilities.buildQueryInfo(query, address.getAddress1(), false);
            query.append("street2 = ");
            CommonUtilities.buildQueryInfo(query, address.getAddress2(), false);
            query.append("city = ");
            CommonUtilities.buildQueryInfo(query, address.getAddressCity(), false);
            query.append("state = ");
            CommonUtilities.buildQueryInfo(query, address.getAddressState(), false);
            query.append("zip_postal_code = ");
            CommonUtilities.buildQueryInfo(query, address.getAddressZIP(), false);
            query.append("country_code = ");
            CommonUtilities.buildQueryInfo(query, address.getAddressCountryCode(), true);
            query.append(" WHERE payee_sno = " + payeeSNo);
            query.append(" AND street1 = ");
            CommonUtilities.buildQueryInfo(query, address.getAddress1(), true);
            query.append(" AND city = ");
            CommonUtilities.buildQueryInfo(query, address.getAddressCity(), true);
            query.append(" AND zip_postal_code = ");
            CommonUtilities.buildQueryInfo(query, address.getAddressZIP(), true);
            query.append(" AND state = ");
            CommonUtilities.buildQueryInfo(query, address.getAddressState(), true);

//            PayeeMapper.lgr.log(I2cLogger.FINE,"updateAddress ---> " + query.toString());

            stmt = dbConn.createStatement();
            updateResult = stmt.executeUpdate(query.toString());
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
        return updateResult;
    }

//        public PayeeVO selectAddress() {
//            PayeeVO payee = null;
//            return payee;
//        }
}
