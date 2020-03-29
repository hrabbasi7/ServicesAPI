package com.i2c.services.registration.base;

import com.i2c.services.util.*;
import java.sql.*;

/**
 * <p>Title: TransactionDataBaseHanlder: Database handling class which performs operation related to database </p>
 * <p>Description: This class provides database related transaction operations such as validating
 * the card program and card no for their existance in the database.</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class TransactionDataBaseHandler {

  private Connection dbConn = null;

  /**
   * Constructor
   * @param dbConn Connection
   */
  protected TransactionDataBaseHandler(Connection dbConn) {
    this.dbConn = dbConn;
  }

  /**
   * Checks whether the status of the OFAC and AVS card is valid or not. The class gets the ofac status and
   * the avs status from the database table cards and returns true or false depending on the status of the
   * card. If both ofac and avs status of the card are "OK" then this method returns true else it return false.
   * @param cardNumber String
   * @return boolean
   */

  int isExistingOFAC_AVSValid(String cardNumber) throws Exception{
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;
    String ofacStatus = null;
    String avsStatus = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for checking Existing OFAC_AVS for card Number--->" +
                                      cardNumber);
      query.append(
          "select ofac_status , avs_status from cards where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for checking Existing OFAC_AVS for card Number--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ResultSet for checking Existing OFAC_AVS for card Number--->" +
                                      rs);
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          ofacStatus = rs.getString(1).trim();
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          avsStatus = rs.getString(2).trim();
        }
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Found Values  ofac_status--->" +
                                      ofacStatus + "<---avs_status--->" +
                                      avsStatus);
      if (ofacStatus != null &&
          ofacStatus.equalsIgnoreCase(Constants.OFAC_AVS_OK)) {
          if( avsStatus != null &&
          avsStatus.equalsIgnoreCase(Constants.OFAC_AVS_OK)) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "OFAC AVS for exisiting Card OK returning TRUE");
              return Constants.OFAC_AVS_GOOD;
          }else{
              return Constants.AVS_FAILED;
          }
      }else{
          return Constants.OFAC_FAILED;
      }
    } catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in checking OFAC/AVS of exisitng card--->" +
                                      ex);
      throw new Exception("Unable to check exisiting OFAC/AVS status--->" + ex);
    } finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      } catch (SQLException ex1) {
      }
    }
  }

  /**
   * This method verifes wthether the given card program id is a valid card program id.
   * The Method uses the database table card_programs to verfity the card program existance. It return true
   * if the given card program exists in the database else it returns false.
   * @param cardProgramID String
   * @return boolean
   */

  public boolean isCardProgramValid(String cardProgramID) {
    boolean isValid = false;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for checking provided Card Program ID valid--->" +
                                      cardProgramID);
      query.append(
          "select card_prg_id from card_programs where card_prg_id = ");
      CommonUtilities.buildQueryInfo(query, cardProgramID, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for checking provided Card Program ID valid--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ResultSet for checking provided Card Program ID valid--->" +
                                      rs);
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Card Program ID valid--->" +
                                          rs.getString(1).trim());
          return true;
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in checking provided Card Program ID valid--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return isValid;
  }

  /**
   * The method is used to check whether the given card number exists in the database. It returns true if
   * the given card exists in the database else it returns false.
   * @param cardNumber String
   * @return boolean
   */

  public boolean isCardNumberValid(String cardNumber) {
    boolean isValid = false;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for checking provided Card Number valid--->" +
                                      cardNumber);
      query.append(
          "select card_no from cards where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for checking provided Card Number valid--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ResultSet for checking provided Card Number valid--->" +
                                      rs);
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Card Number valid--->" +
                                          rs.getString(1).trim());
          return true;
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in checking provided Card Number valid--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return isValid;
  }

  /**
   * The method is used verfiy whehter the card number is active or not. This method checks the given
   * parameter if both i.e. ATM staus & POS status are "OK" then it returns true else returns false.
   * @param cardStatusATM String
   * @param cardStatusPOS String
   * @return boolean
   */

  public boolean isExistingCardNumberActive(String cardStatusATM,
                                            String cardStatusPOS) {
    boolean isActive = false;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for checking provided Card Number Active--->" +
                                      "<---CardStatusATM--->" + cardStatusATM +
                                      "<---CardStatusPOS--->" + cardStatusPOS);

      if (cardStatusATM != null &&
          cardStatusATM.equalsIgnoreCase(Constants.ACTIVE_CARD) &&
          cardStatusPOS != null &&
          cardStatusPOS.equalsIgnoreCase(Constants.ACTIVE_CARD)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Card Number is ACTIVE");
        return true;
      }

    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in checking provided Card Number valid--->" +
                                      ex);
    }
    return isActive;
  }

  /**
   * The method is used to check whether the card is pre-active or not base on the given parameter.
   * It check the card's ATM & POS status to determine whether the card is pre-active or not.
   * @param cardStatusATM String
   * @param cardStatusPOS String
   * @return boolean
   */

  public boolean isExistingCardNumberPreActive(String cardStatusATM,
                                               String cardStatusPOS) {
    boolean isActive = false;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for checking provided Card Number PreActive--->" +
                                      "<---CardStatusATM--->" + cardStatusATM +
                                      "<---CardStatusPOS--->" + cardStatusPOS);

      if (cardStatusATM != null &&
          cardStatusATM.equalsIgnoreCase(Constants.PRE_ACTIVE_CARD) &&
          cardStatusPOS != null &&
          cardStatusPOS.equalsIgnoreCase(Constants.PRE_ACTIVE_CARD)) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Card Number is Pre-ACTIVE");
        return true;
      }

    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in checking provided Card Number Pre-Active--->" +
                                      ex);
    }
    return isActive;
  }

  /**
   * The method return the card program id associated with the given card no. It queries the database to
   * find to find the card-program id for the given card no.
   * @param cardNumber String
   * @return String
   */

  public String getCardProgramID(String cardNumber) {
    String cardPrgID = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting Card Program ID for provided Card Number--->" +
                                      cardNumber);
      query.append(
          "select card_prg_id from cards where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting Card Program ID for provided Card Number--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ResultSet for getting Card Program ID for provided Card Number--->" +
                                      rs);
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          cardPrgID = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Card Program ID Got--->" +
                                          cardPrgID);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting Card Program ID for provided Card Number--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return cardPrgID;
  }

  public String getCardProgramIDByCardStartNos(String cardStartNos) {
      String cardPrgID = null;
      StringBuffer query = new StringBuffer();
      Statement stmt = null;
      ResultSet rs = null;

      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                          "Method for getting Card Program ID for provided Card Start Number--->" +
                                          cardStartNos);
          query.append(
                  "select card_prg_id from card_programs where card_start_nos = ");
          CommonUtilities.buildQueryInfo(query, cardStartNos, true);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Query for getting Card Program ID for provided Card Start Number--->" +
                                          query);
          stmt = dbConn.createStatement();
          rs = stmt.executeQuery(query.toString());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "ResultSet for getting Card Program ID for provided Card Start Number--->" +
                                          rs);
          if (rs.next()) {
              if (rs.getString(1) != null &&
                  rs.getString(1).trim().length() > 0) {
                  cardPrgID = rs.getString(1).trim();
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                          LOG_CONFIG),
                          "Card Program ID Got--->" +
                          cardPrgID);
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_WARNING),
                                          "Exception in getting Card Program ID for provided Card Start Number--->" +
                                          ex);
      } finally {
          try {
              if (rs != null) {
                  rs.close();
                  rs = null;
              }
              if (stmt != null) {
                  stmt.close();
                  stmt = null;
              }
          } catch (SQLException ex1) {
          }
      }
      return cardPrgID;
  }


  /**
   * The method checks whether the given service is allowed for the given card no. It uses the database
   * stored procedure "IS_SERVICE_ALLOWED" and returns true if the service is allowed else it returns false.
   * @param serviceID String
   * @param cardPrgID String
   * @return boolean
   */

  public boolean isServiceAllowed(String serviceID, String cardPrgID) {
    boolean isAllowed = false;
    CallableStatement cs = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Checking Service is Allowed for Card program ID--->" +
                                      cardPrgID + "<---Service ID--->" +
                                      serviceID);

      cs = dbConn.prepareCall("{call is_service_allowed(?,?)}");
      cs.setString(1, cardPrgID);
      cs.setString(2, serviceID);
      rs = cs.executeQuery();
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          String serviceAllowed = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Service is Allowed for Card program ID--->" +
                                          cardPrgID + "--->" + serviceAllowed);
          if (serviceAllowed.equalsIgnoreCase(Constants.YES_OPTION)) {
            return true;
          }
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Checking Service is Allowed for Card program ID--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (cs != null) {
          cs.close();
          cs = null;
        }
      }
      catch (Exception ex1) {}
    }
    return isAllowed;
  }

  /**
   * /**
    * The method checks whether the given parameter is allowed for the given card program. It uses the database
    * stored procedure "IS_PARAM_ALLOWED" to determine whether the parameter and return true if the parameter is
    *  allowed else it returns false.
    * @param paramCode String
    * @param cardPrgID String
    * @param paramValue String
    * @return boolean
    */
   public boolean isParameterAllowed(String paramCode, String cardPrgID,
                                     String paramValue) {
     boolean isOk = true;
     CallableStatement cs = null;
     ResultSet rs = null;

     try {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                       "Method for Checking Service is Allowed for Card program ID--->" +
                                       cardPrgID + "<---Parameter Code--->" +
                                       paramCode + "<---Parameter Value--->" +
                                       paramValue);

       cs = dbConn.prepareCall("{call is_param_allowed(?,?,?)}");
       cs.setString(1, cardPrgID);
       cs.setString(2, paramCode);
       cs.setString(3, paramValue);
       rs = cs.executeQuery();
       if (rs.next()) {
         if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
           String paramAllowed = rs.getString(1).trim();
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
               LOG_CONFIG),
                                           "Paramter is Allowed for Card program ID--->" +
                                           cardPrgID + "--->" + paramAllowed);
           if (paramAllowed.equalsIgnoreCase(Constants.NO_OPTION)) {
             return false;
           }
         }
       }
     }
     catch (Exception ex) {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                       "Exception in Checking Parameter is Allowed for Card program ID--->" +
                                       ex);
     }
     finally {
       try {
         if (rs != null) {
           rs.close();
           rs = null;
         }
         if (cs != null) {
           cs.close();
           cs = null;
         }
       }
       catch (Exception ex1) {}
     }
     return isOk;
   }

  /**
   * The method checks whether the given parameter is allowed for the given card program. It use the database
   *  stored procedure "IS_PARAM_ALLOWED" to determine whether the parameter and return true if the parameter
   *  is not allowed else it returns false.
   * @param paramCode String
   * @param cardPrgID String
   * @param paramValue String
   * @return boolean
   */

  public boolean isParameterNotAllowed(String paramCode, String cardPrgID,
                                       String paramValue) {
    boolean isNotOk = false;
    CallableStatement cs = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Checking Parameter is Not Allowed for Card program ID--->" +
                                      cardPrgID + "<---Parameter Code--->" +
                                      paramCode + "<---Parameter Value--->" +
                                      paramValue);

      cs = dbConn.prepareCall("{call is_param_allowed(?,?,?)}");
      cs.setString(1, cardPrgID);
      cs.setString(2, paramCode);
      cs.setString(3, paramValue);
      rs = cs.executeQuery();
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          String paramAllowed = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Paramter is Not Allowed for Card program ID--->" +
                                          cardPrgID + "--->" + paramAllowed);
          if (paramAllowed.equalsIgnoreCase(Constants.YES_OPTION)) {
            isNotOk = true;
          }
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Checking Parameter is Allowed for Card program ID--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (cs != null) {
          cs.close();
          cs = null;
        }
      }
      catch (Exception ex1) {}
    }
    return isNotOk;
  }

  /**
   * The method calculate the service fee of the given service for the given card program. It uses the database
   * stored procedure "CAL_SERVICE_FEE" to calculate the service fee for the given card porgram and service.
   * The method return a String array whose first element is the caluculated service fee second one is response
   * code and the last valuse is the response description.
   * @param serviceID String
   * @param cardPrgID String
   * @param amount String
   * @return String[]
   */

  public String[] getServiceFee(String serviceID, String cardPrgID,
                                String amount) {

    CallableStatement cs = null;
    ResultSet rs = null;
    StringBuffer query = new StringBuffer();

    String[] serviceFeeList = new String[3];
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for getting Service FEE --- Card program ID--->" +
                                      cardPrgID + "<---Service ID--->" +
                                      serviceID + "<---Amount--->" + amount);
      query.append("Execute procedure cal_service_fee( " +
                   "pcard_no= " + null +"," +
                   "pamount= " + (amount != null ? "'" + amount + "'" : null) +
                   "," +
                   "pservice_id= " + (serviceID != null ? "'" + serviceID + "'" : null) +
                   "," +
                   "pcard_prg_id= " +
                   (cardPrgID != null ? "'" + cardPrgID + "'" : null) + ")");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting Service FEE --->" +
                                      query);
      cs = dbConn.prepareCall(query.toString());
      rs = cs.executeQuery();
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          serviceFeeList[0] = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Service Fee Got--->" +
                                          serviceFeeList[0]);

        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          serviceFeeList[1] = rs.getString(2).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Return Code Got--->" +
                                          serviceFeeList[1]);
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          serviceFeeList[2] = rs.getString(3).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Description--->" + serviceFeeList[2]);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting Service Fee--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (cs != null) {
          cs.close();
          cs = null;
        }
      }
      catch (Exception ex1) {}
    }
    return serviceFeeList;
  }

  /**
   * The method is used to update the card holder profile for the given card no. The method update the
   * card holder infromation in the database table cards for the given card no.
   * @param requestObj TransactionRequestInfoObj
   * @param cardNumber String
   */

  public void updateCardHolderProfile(TransactionRequestInfoObj requestObj,
                               String cardNumber) throws Exception{

    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for Updating Card Holder Profile for card Number--->" +
                                      cardNumber);
      query.append("update cards set shipping_method = ?, first_name1 = ?, middle_name1 = ? ");
      query.append(", last_name1 = ?,date_of_birth = ?,email = ?,ssn_nid_no = ?,address1 = ?, ");
      query.append("address2 = ?,state_code = ?,city = ?,zip_postal_code = ?,user_id = ?, ");
      query.append("assignment_no = ?,stake_holder_id = ?,mother_maiden_nam = ?,country_code = ?, ");
      query.append("home_phone_no = ?,work_phone_no = ?,gender = ?,employer_id = ?, ");
      query.append("sales_node_no = ?,ofac_status = ?,avs_status = ?,foreign_id = ?,foreign_id_type = ?, ");
      query.append("bill_address1 = ?,bill_address2 = ?,bill_city = ?,bill_country_code = ?,");
      query.append("bill_state_code = ?,bill_zip_code = ?,ch_id = ?,");
      query.append("card_link_type = ?,member_id = ?,card_nickname = ?,host_id = ?,");
      query.append("driving_license_st = ?,is_main_card = ?,primary_card_no = ?,");
      query.append("question_id = ?,question_answer = ?,f_country_code = ?,driving_license_no = ?");
      query.append(" where card_no = ? ");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for Updating Card Holder Profile for card Number--->" +
                                      query);

      pstmt = dbConn.prepareStatement(query.toString());
      pstmt.setString(1, requestObj.getShippingMethod());
      pstmt.setString(2, requestObj.getFirstName());
      pstmt.setString(3, requestObj.getMiddleName());
      pstmt.setString(4, requestObj.getLastName());
      pstmt.setString(5, requestObj.getDob());
      pstmt.setString(6, requestObj.getEmailAddress());
      pstmt.setString(7, requestObj.getSsn());
      pstmt.setString(8, requestObj.getAddress1());
      pstmt.setString(9, requestObj.getAddress2());
      pstmt.setString(10, requestObj.getState());
      pstmt.setString(11, requestObj.getCity());
      pstmt.setString(12, requestObj.getZip());
      pstmt.setString(13, requestObj.getUserId());
      pstmt.setString(14, requestObj.getAssignmentNo());
      pstmt.setString(15, requestObj.getStakeHolderId());
      pstmt.setString(16, requestObj.getMotherMaidenName());
      pstmt.setString(17, requestObj.getCountryCode());
      pstmt.setString(18, requestObj.getHomePhone());
      pstmt.setString(19, requestObj.getWorkPhone());
      pstmt.setString(20, requestObj.getGender());
      pstmt.setString(21, requestObj.getEmployerId());
      pstmt.setString(22, requestObj.getSalesNodeNo());
      pstmt.setString(23, requestObj.getOfacStatus());
      pstmt.setString(24, requestObj.getAvsStatus());
      pstmt.setString(25, requestObj.getForeignId());
      pstmt.setString(26, requestObj.getForeignIdType());
      pstmt.setString(27, requestObj.getBillingAddress1());
      pstmt.setString(28, requestObj.getBillingAddress2());
      pstmt.setString(29, requestObj.getBillingCity());
      pstmt.setString(30, requestObj.getBillingCountrycode());
      pstmt.setString(31, requestObj.getBillingState());
      pstmt.setString(32, requestObj.getBillingZipCode());
      pstmt.setString(33, requestObj.getChId());
      pstmt.setString(34, requestObj.getCardLinkType());
      pstmt.setString(35, requestObj.getMemberId());
      pstmt.setString(36, requestObj.getCardNickName());
      pstmt.setString(37, requestObj.getHostId());
      pstmt.setString(38, requestObj.getDrivingLisenseState());
      pstmt.setString(39, requestObj.getIsMainCard());
      pstmt.setString(40, requestObj.getPrimaryCardNo());
      pstmt.setString(41, requestObj.getQuestionId());
      pstmt.setString(42, requestObj.getQuestionAnswer());
      pstmt.setString(43, requestObj.getForeignCountryCode());
      pstmt.setString(44, requestObj.getDrivingLisenseNumber());
      pstmt.setString(45, cardNumber);

      pstmt.executeUpdate();
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Updating Card Holder Profile for card Number--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (pstmt != null) {
          pstmt.close();
          pstmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
  }

  public void updateCardLimits(TransactionRequestInfoObj requestObj,
                                  String cardNumber) throws Exception {

      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;

      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                          "Method for Updating Card Limits for card Number--->" +
                                          cardNumber);
          query.append(
                  "update cards set atm_ol_withd_limit = ?,atm_of_withd_limit = ?,");
          query.append(
                  "pos_ol_withd_limit = ?,pos_of_withd_limit = ?,cr_ol_withd_limit = ?,");
          query.append(
                  "cr_of_withd_limit = ?,max_deposit_limit = ?,");
          query.append(
                  "day_atm_dpst_max = ?,fraud_info_flag = ?,life_high_atm_amt = ?,");
          query.append("life_high_pos_amt = ?,life_high_avl_bal = ?,");
          query.append(
                  "min_load_amount = ?,max_load_amount = ?,min_trans_amount = ?,");
          query.append(
                  "max_trans_amount = ?,earned_points = ?,since_0_balance = ?,");
          query.append(
                  "nlost_stolens = ?,nfree_atm_withd = ?,");
          query.append(
                  "nlast_declines = ?,tot_trans_amount = ?,tot_coms_amount = ?,");
          query.append(
                  "nfee_reverals = ?,last_redeemed_dt = ?,confirmed_points = ?,");
          query.append("forfeited_points = ?,redeemed_points = ?, available_points = ?,");
          query.append("pending_points = ?");
          query.append(" where card_no = ?");

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Query for Updating Card Limits for card Number--->" +
                                          query);

          pstmt = dbConn.prepareStatement(query.toString());
          pstmt.setString(1, requestObj.getAtmOnlineWithdLimit());
          pstmt.setString(2, requestObj.getAtmOflineWithdLimit());
          pstmt.setString(3, requestObj.getPosOnlineWithdLimit());
          pstmt.setString(4, requestObj.getPosOflineWithdLimit());
          pstmt.setString(5, requestObj.getCreditOnlineWithdLimit());
          pstmt.setString(6, requestObj.getCreditOflineWithdLimit());
          pstmt.setString(7, requestObj.getMaxDepositLimit());
          pstmt.setString(8, requestObj.getDailyAtmDepositMax());
          pstmt.setString(9, requestObj.getFraudInfoFlag());
          pstmt.setString(10, requestObj.getLifeHighAtmAmount());
          pstmt.setString(11, requestObj.getLifeHighPosAmount());
          pstmt.setString(12, requestObj.getLifeHighAvailableBalance());
          pstmt.setString(13, requestObj.getMinLoadAmount());
          pstmt.setString(14, requestObj.getMaxLoadAmount());
          pstmt.setString(15, requestObj.getMinTransAmount());
          pstmt.setString(16, requestObj.getMaxTransAmount());
          pstmt.setString(17, requestObj.getEarnedPoints());
          pstmt.setString(18, requestObj.getSinceZeroBalance());
          pstmt.setString(19, requestObj.getTotalLostStolen());
          pstmt.setString(20, requestObj.getTotalFreeAtmWithds());
          pstmt.setString(21, requestObj.getTotalLastDeclines());
          pstmt.setString(22, requestObj.getTotalTransAmount());
          pstmt.setString(23, requestObj.getTotalCommAmount());
          pstmt.setString(24, requestObj.getTotalFeeReversals());
          if(requestObj.getLastRedeemedDate() == null){
              pstmt.setString(25, requestObj.getLastRedeemedDate());
          } else {
              pstmt.setString(25, CommonUtilities.convertDateFormat(Constants.DATE_TIME_FORMAT, Constants.DATE_TIME_FORMAT,requestObj.getLastRedeemedDate()));
          }
          pstmt.setString(26, requestObj.getConfirmedPoints());
          pstmt.setString(27, requestObj.getFortifiedPoints());
          pstmt.setString(28, requestObj.getRedeemedPoints());
          pstmt.setString(29, requestObj.getAvailablePoints());
          pstmt.setString(30, requestObj.getPendingPoints());
          pstmt.setString(31, cardNumber);

          pstmt.executeUpdate();
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_WARNING),
                                          "Exception in Updating Card Limits for card Number--->" +
                                          ex);
          throw ex;
      } finally {
          try {
              if (pstmt != null) {
                  pstmt.close();
                  pstmt = null;
              }
          } catch (SQLException ex1) {
          }
      }

  }


  public void processCardAssignment(TransactionRequestInfoObj requestObj,
                               String cardNumber,String description){
      String salesNodeNum = null;
      String stkHolderId = null;
      String assignNo = null;
      String[] existCardAssignInfo = null;
      try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Method for prcoessing card assignment --- card Number--->" +
                                        cardNumber + "<---Stake Holder ID--->" + requestObj.getCrdAceptorCode()
                                        + "<---Existing Card No--->" + requestObj.getExistingCard()
                                        + "<---Card Program ID--->" + requestObj.getCardPrgId() + "<--description-->" + description);
        if(requestObj.getCrdAceptorCode() != null && requestObj.getCrdAceptorCode().trim().length() > 0){
          assignCard(requestObj.getCrdAceptorCode(),cardNumber,requestObj.getCardPrgId(),description);
//          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
//                                          "Method for prcoessing card assignment --- Getting sales node number against the given stake holder id and card program...");
//          salesNodeNum = getSalesNodeNumber(requestObj.getCrdAceptorCode(),requestObj.getCardPrgId());
//          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
//                                          "Method for prcoessing card assignment --- Got sales node number against the given stake holder id and card program--->" + salesNodeNum);
//          if(salesNodeNum != null && salesNodeNum.trim().length() > 0){
//            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//                LOG_CONFIG),
//                                            "Method for prcoessing card assignment --- Getting assignment no by inserting new card assignment record...");
//            assignNo = insertSalesRecord(requestObj.getCrdAceptorCode(),
//                                         cardNumber, salesNodeNum,
//                                         requestObj.getCardPrgId());
//            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//                LOG_CONFIG),
//                                            "Method for prcoessing card assignment --- Got assignment no by inserting new card assignment record--->" +
//                                            assignNo);
//            if(assignNo != null && assignNo.trim().length() > 0){
//              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//                  LOG_CONFIG),
//                                              "Method for prcoessing card assignment --- inserting detail record...");
//              insertDetailRecord(assignNo, cardNumber);
//              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//                  LOG_CONFIG),
//                                              "Method for prcoessing card assignment --- Assigning the card to stakeholder...");
//              updateCardRecord(cardNumber,assignNo,requestObj.getCrdAceptorCode(),salesNodeNum);
//            }else{
//              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//                  LOG_CONFIG),
//                                              "Method for prcoessing card assignment --- No Assignment number got");
//            }
//          }else{
//            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
//                LOG_CONFIG),
//                                            "Method for prcoessing card assignment --- No Sales Node number found");
//
//          }
        }else if(requestObj.getExistingCard() != null && requestObj.getExistingCard().trim().length() > 0){
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Method for prcoessing card assignment --- getting stake holder id and sales node number of existing card...");
            existCardAssignInfo = getExistingCardAssignmentInfo(requestObj.getExistingCard());
            if(existCardAssignInfo != null && existCardAssignInfo.length > 0){
              salesNodeNum = existCardAssignInfo[0];
              stkHolderId = existCardAssignInfo[1];
              if(stkHolderId != null && stkHolderId.trim().length() > 0
                 && salesNodeNum != null && salesNodeNum.trim().length() > 0){
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                                "Method for prcoessing card assignment --- Getting assignment no by inserting new card assignment record...");
                assignNo = insertSalesRecord(stkHolderId,
                                             cardNumber, salesNodeNum,
                                             requestObj.getCardPrgId(),description);
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                                "Method for prcoessing card assignment --- Got assignment no by inserting new card assignment record--->" +
                                                assignNo);
                if(assignNo != null && assignNo.trim().length() > 0){
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                                  "Method for prcoessing card assignment --- inserting detail record...");
                  insertDetailRecord(assignNo, cardNumber);
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                                  "Method for prcoessing card assignment --- Assigning the card to stakeholder...");
                  updateCardRecord(cardNumber,assignNo,stkHolderId,salesNodeNum);
                }else{
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                                  "Method for prcoessing card assignment --- No Assignment number got");
                }
              }else{
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                                "Method for prcoessing card assignment --- Existing card is not assigned");
              }
            }else{
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                              "Method for prcoessing card assignment --- No Assignment info got");
            }
        }else{
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Method for prcoessing card assignment --- Cannot assign card, required info missing");

        }
      }catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_WARNING),
                                        "Exception in prcoessing card assignment --->" + ex);
      }
  }

  protected String getSalesNodeNumber(String stakeHolderId,
                               String cardProgId){

      String salesNodeNum = null;
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Method for getting sales node number --- StakeHolderID--->" +
                                        stakeHolderId + "<---Card Program ID--->" + cardProgId);
        if(stakeHolderId != null && stakeHolderId.trim().length() > 0){
          query.append("select sales_node_no from sale_chanels where card_prg_id = ? and stake_holder_id = ? order by sales_node_no");
          pstmt = dbConn.prepareStatement(query.toString());
          pstmt.setString(1,cardProgId);
          pstmt.setString(2,stakeHolderId);

          rs = pstmt.executeQuery();

          if(rs.next()){
            salesNodeNum = rs.getString(1);
            if(salesNodeNum != null && salesNodeNum.trim().length() > 0){
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                              "Method for getting sales node number --- SalesNodeNumber got--->" + salesNodeNum);
              return salesNodeNum.trim();
            }else{
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                              "Method for getting sales node number --- No SalesNodeNumber found for provided card program and Stake holder");
              return null;
            }
          }else{
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                            "Method for getting sales node number --- No SalesNodeNumber found for provided card program and Stake holder");
            return null;
          }
        }else{
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                          "Method for getting sales node number --- No StakeHolder provided");
          return null;
        }
      }catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Exception in getting sales node number --->" + ex);
        return null;
      }finally{
        try {
          if (rs != null) {
            rs.close();
            rs = null;
          }
          if (pstmt != null) {
            pstmt.close();
            pstmt = null;
          }
        }catch (Exception ex1) {}
      }
    }

    protected String[] getExistingCardAssignmentInfo(String existCardNo){

      String [] assignInfo = new String[2];
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      String salesNodeNo = null;
      String stkHolderId = null;

      try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Method for getting assignment info of existing card --- Card No--->" + existCardNo);
          query.append("select sales_node_no,stake_holder_id from cards where card_no = ? and assignment_no is not null");
          pstmt = dbConn.prepareStatement(query.toString());
          pstmt.setString(1,existCardNo);

          rs = pstmt.executeQuery();

          if(rs.next()){
            salesNodeNo = rs.getString(1);
            if(salesNodeNo != null && salesNodeNo.trim().length() > 0){
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                              "Method for getting assignment info of existing card --- SalesNodeNumber got--->" + salesNodeNo);
              assignInfo[0] = salesNodeNo.trim();
            }
            stkHolderId = rs.getString(2);
            if(stkHolderId != null && stkHolderId.trim().length() > 0){
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                              "Method for getting assignment info of existing card --- Stake HolderId got--->" + stkHolderId);
              assignInfo[1] = stkHolderId.trim();
            }
          }else{
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                            "Method for getting assignment info of existing card --- No SalesNodeNumber found for provided card program and Stake holder");
            return null;
          }
      }catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Exception in getting assignment info of existing card  --->" + ex);
        return null;
      }finally{
        try {
          if (rs != null) {
            rs.close();
            rs = null;
          }
          if (pstmt != null) {
            pstmt.close();
            pstmt = null;
          }
        }catch (Exception ex1) {}
      }
      return assignInfo;
    }


    protected String insertSalesRecord(String stakeHolderId,
                           String cardNumber,
                           String salesNodeNumber,
                           String cardProgId,
                           String description){

    String assignNo = null;
    StringBuffer query = new StringBuffer();
    StringBuffer serialQuery = new StringBuffer();
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;
    String assignDate = null;
    String assignTime = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for getting sales node number --- StakeHolderID--->" +
                                      stakeHolderId + "<---Card Program ID--->" + cardProgId + "<---Description--->" + description);
      assignDate = CommonUtilities.getCurrentFormatDate(Constants.INFORMIX_DATE_FORMAT);
      assignTime = CommonUtilities.getCurrentFormatDate(Constants.TIME_FORMAT);

      query.append("insert into card_assignments(stake_holder_id,assign_date,assign_time,remarks,ncards,card_no_start,card_no_to,sales_node_no,card_prg_id,current_ncards) values(?,?,?,?,?,?,?,?,?,?)");
      pstmt = dbConn.prepareStatement(query.toString());
      pstmt.setString(1,stakeHolderId);
      pstmt.setString(2,assignDate);
      pstmt.setString(3,assignTime);
      pstmt.setString(4,description);
      pstmt.setString(5,"1");
      pstmt.setString(6,cardNumber);
      pstmt.setString(7,cardNumber);
      pstmt.setString(8,salesNodeNumber);
      pstmt.setString(9,cardProgId);
      pstmt.setString(10,"1");

      pstmt.executeUpdate();

      if (query.toString().toLowerCase().indexOf(Constants.INSERT_QUERY_INTO_VALUE.toLowerCase()) > -1) {
        serialQuery.append(Constants.SERIAL_QUERY + " " + query.toString().substring(query.toString().toLowerCase().indexOf(Constants.INSERT_QUERY_INTO_VALUE.toLowerCase()) + Constants.INSERT_QUERY_INTO_VALUE.length(), query.toString().toLowerCase().indexOf(Constants.INSERT_QUERY_COLUMN_START_VALUE.toLowerCase())));
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Serail Number Query --> " + serialQuery);
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(serialQuery.toString());

        if (rs.next()) {
          if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
            assignNo = rs.getString(1).trim();
          }
        }
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in getting sales node number --->" + ex);
      return null;
    }finally{
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
        if (pstmt != null) {
          pstmt.close();
          pstmt = null;
        }
      }catch (Exception ex1) {}
    }
    return assignNo;
  }

  protected void updateCardRecord(String cardNo, String assignNo, String stkHolderID,
                  String salesNodeNo) {

    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for assinging Card to stakeholder --- Card No--->" +
                                      cardNo
                                      + "<---Assign No--->" + assignNo
                                      + "<---Stake Holder ID--->" + stkHolderID
                                      + "<---Sales Node No--->" + salesNodeNo);
      query.append("update cards set assignment_no = ?,stake_holder_id = ?,sales_node_no = ? where card_no = ?");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for assinging Card to stakeholder --->" +
                                      query);

      pstmt = dbConn.prepareStatement(query.toString());
      pstmt.setString(1, assignNo);
      pstmt.setString(2, stkHolderID);
      pstmt.setString(3, salesNodeNo);
      pstmt.setString(4, cardNo);

      pstmt.executeUpdate();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Card assigned to stakeholder successfully....");
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in assigning card to stakeholder--->" +
                                      ex);
    }finally {
      try {
        if (pstmt != null) {
          pstmt.close();
          pstmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
  }


  /**
   * The method returns the TransactionRequestInfoObj which contains the card holder information for the given
   * card no.
   * @param cardNumber String
   * @return TransactionRequestInfoObj
   */

  public TransactionRequestInfoObj getCardHolderProfile(String cardNumber) throws Exception{

    TransactionRequestInfoObj profileObj = new TransactionRequestInfoObj();
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "Method for getting Card Holder Profile for card Number--->" +
                                        cardNumber);
        if (cardNumber != null) {
            query.append(
                    "select first_name1,middle_name1,last_name1,date_of_birth");
            query.append(
                    ",email,ssn_nid_no,address1,address2,state_code,city,zip_postal_code,");
            query.append(
                    "user_id,mother_maiden_nam,country_code,home_phone_no,");
            query.append(
                    "work_phone_no,gender,ofac_status,avs_status,foreign_id,");
            query.append(
                    "foreign_id_type,employer_id,bill_address1,bill_address2,");
            query.append(
                    "bill_city,bill_country_code,bill_state_code,bill_zip_code,");
            query.append(
                    "atm_ol_withd_limit,atm_of_withd_limit,pos_ol_withd_limit,");
            query.append(
                    "pos_of_withd_limit,cr_ol_withd_limit,cr_of_withd_limit,");
            query.append(
                    "max_deposit_limit,is_main_card,day_atm_dpst_max,fraud_info_flag,");
            query.append(
                    "life_high_atm_amt,life_high_pos_amt,life_high_avl_bal,");
            query.append(
                    "min_load_amount,max_load_amount,min_trans_amount,max_trans_amount,");
            query.append(
                    "earned_points,since_0_balance,primary_card_no,nlost_stolens,");
            query.append(
                    "nfree_atm_withd,nlast_declines,tot_trans_amount,tot_coms_amount,");
            query.append(
                    "nfee_reverals,last_redeemed_dt,confirmed_points,forfeited_points,");
            query.append(
                    "redeemed_points,available_points,pending_points,question_id,");
            query.append(
                    "question_answer,assignment_no,stake_holder_id,");
            query.append("sales_node_no,ch_id,card_link_type,member_id,card_nickname,");
            query.append("host_id,driving_license_no,driving_license_st,f_country_code ");
            query.append("from cards where card_no = ");
            CommonUtilities.buildQueryInfo(query, cardNumber, true);

            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                            "Query for getting Card Holder Profile for card Number--->" +
                                            query);
            stmt = dbConn.createStatement();
            rs = stmt.executeQuery(query.toString());

            if (rs.next()) {
                if (rs.getString(1) != null &&
                    rs.getString(1).trim().length() > 0) {
                    profileObj.setFirstName(rs.getString(1).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "FirstName--->" +
                            profileObj.getFirstName());
                }
                if (rs.getString(2) != null &&
                    rs.getString(2).trim().length() > 0) {
                    profileObj.setMiddleName(rs.getString(2).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "MiddleName--->" +
                            profileObj.getMiddleName());

                }
                if (rs.getString(3) != null &&
                    rs.getString(3).trim().length() > 0) {
                    profileObj.setLastName(rs.getString(3).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "LastName--->" +
                            profileObj.getLastName());

                }
                if (rs.getString(4) != null &&
                    rs.getString(4).trim().length() > 0) {
                    profileObj.setDob(rs.getString(4).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Dob--->" +
                            profileObj.getDob());

                }
                if (rs.getString(5) != null &&
                    rs.getString(5).trim().length() > 0) {
                    profileObj.setEmailAddress(rs.getString(5).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Email--->" +
                            profileObj.getEmailAddress());

                }
                if (rs.getString(6) != null &&
                    rs.getString(6).trim().length() > 0) {
                    profileObj.setSsn(rs.getString(6).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "SSN--->" +
                            profileObj.getSsn());

                }
                if (rs.getString(7) != null &&
                    rs.getString(7).trim().length() > 0) {
                    profileObj.setAddress1(rs.getString(7).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Address1--->" +
                            profileObj.getAddress1());

                }
                if (rs.getString(8) != null &&
                    rs.getString(8).trim().length() > 0) {
                    profileObj.setAddress2(rs.getString(8).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Address2--->" +
                            profileObj.getAddress2());

                }
                if (rs.getString(9) != null &&
                    rs.getString(9).trim().length() > 0) {
                    profileObj.setState(rs.getString(9).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "State--->" +
                            profileObj.getState());

                }
                if (rs.getString(10) != null &&
                    rs.getString(10).trim().length() > 0) {
                    profileObj.setCity(rs.getString(10).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "City--->" +
                            profileObj.getCity());

                }
                if (rs.getString(11) != null &&
                    rs.getString(11).trim().length() > 0) {
                    profileObj.setZip(rs.getString(11).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "Zip--->" +
                            profileObj.getZip());
                }
                if (rs.getString(12) != null &&
                    rs.getString(12).trim().length() > 0) {
                    profileObj.setUserId(rs.getString(12).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "UserId--->" +
                            profileObj.getUserId());

                }
                if (rs.getString(13) != null &&
                    rs.getString(13).trim().length() > 0) {
                    profileObj.setMotherMaidenName(rs.getString(13).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "setMotherMaidenName--->" +
                            profileObj.getMotherMaidenName());

                }
                if (rs.getString(14) != null &&
                    rs.getString(14).trim().length() > 0) {
                    profileObj.setCountryCode(rs.getString(14).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getCountryCode--->" +
                            profileObj.getCountryCode());

                }
                if (rs.getString(15) != null &&
                    rs.getString(15).trim().length() > 0) {
                    profileObj.setHomePhone(rs.getString(15).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getHomePhone--->" +
                            profileObj.getHomePhone());

                }
                if (rs.getString(16) != null &&
                    rs.getString(16).trim().length() > 0) {
                    profileObj.setWorkPhone(rs.getString(16).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getWorkPhone--->" +
                            profileObj.getWorkPhone());

                }
                if (rs.getString(17) != null &&
                    rs.getString(17).trim().length() > 0) {
                    profileObj.setGender(rs.getString(17).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getGender--->" +
                            profileObj.getGender());

                }
                if (rs.getString(18) != null &&
                    rs.getString(18).trim().length() > 0) {
                    profileObj.setOfacStatus(rs.getString(18).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getOfacStatus--->" +
                            profileObj.getOfacStatus());
                }
                if (rs.getString(19) != null &&
                    rs.getString(19).trim().length() > 0) {
                    profileObj.setAvsStatus(rs.getString(19).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getAvsStatus--->" +
                            profileObj.getAvsStatus());
                }
                if (rs.getString(20) != null &&
                    rs.getString(20).trim().length() > 0) {
                    profileObj.setForeignId(rs.getString(20).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getForeignId--->" +
                            profileObj.getForeignId());
                }
                if (rs.getString(21) != null &&
                    rs.getString(21).trim().length() > 0) {
                    profileObj.setForeignIdType(rs.getString(21).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getForeignIdType--->" +
                            profileObj.getForeignIdType());
                }
                if (rs.getString(22) != null &&
                    rs.getString(22).trim().length() > 0) {
                    profileObj.setEmployerId(rs.getString(22).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getEmployerId--->" +
                            profileObj.getEmployerId());
                }
                if (rs.getString(23) != null &&
                    rs.getString(23).trim().length() > 0) {
                    profileObj.setBillingAddress1(rs.getString(23).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getBillingAddress1--->" +
                            profileObj.getBillingAddress1());
                }
                if (rs.getString(24) != null &&
                    rs.getString(24).trim().length() > 0) {
                    profileObj.setBillingAddress2(rs.getString(24).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getBillingAddress2--->" +
                            profileObj.getBillingAddress2());
                }
                if (rs.getString(25) != null &&
                    rs.getString(25).trim().length() > 0) {
                    profileObj.setBillingCity(rs.getString(25).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getBillingCity--->" +
                            profileObj.getBillingCity());
                }
                if (rs.getString(26) != null &&
                    rs.getString(26).trim().length() > 0) {
                    profileObj.setBillingCountrycode(rs.getString(26).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getBillingCountrycode--->" +
                            profileObj.getBillingCountrycode());
                }
                if (rs.getString(27) != null &&
                    rs.getString(27).trim().length() > 0) {
                    profileObj.setBillingState(rs.getString(27).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getBillingState--->" +
                            profileObj.getBillingState());
                }
                if (rs.getString(28) != null &&
                    rs.getString(28).trim().length() > 0) {
                    profileObj.setBillingZipCode(rs.getString(28).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getBillingZipCode--->" +
                            profileObj.getBillingZipCode());
                }
                if (rs.getString(29) != null &&
                    rs.getString(29).trim().length() > 0) {
                    profileObj.setAtmOnlineWithdLimit(rs.getString(29).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getAtmOflineWithdLimit--->" +
                            profileObj.getAtmOflineWithdLimit());
                }
                if (rs.getString(30) != null &&
                    rs.getString(30).trim().length() > 0) {
                    profileObj.setAtmOflineWithdLimit(rs.getString(30).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getAtmOnlineWithdLimit--->" +
                            profileObj.getAtmOnlineWithdLimit());
                }
                if (rs.getString(31) != null &&
                    rs.getString(31).trim().length() > 0) {
                    profileObj.setPosOnlineWithdLimit(rs.getString(31).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getPosOnlineWithdLimit--->" +
                            profileObj.getPosOnlineWithdLimit());
                }
                if (rs.getString(32) != null &&
                    rs.getString(32).trim().length() > 0) {
                    profileObj.setPosOflineWithdLimit(rs.getString(32).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getPosOflineWithdLimit--->" +
                            profileObj.getPosOflineWithdLimit());
                }
                if (rs.getString(33) != null &&
                    rs.getString(33).trim().length() > 0) {
                    profileObj.setCreditOnlineWithdLimit(rs.getString(33).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getCreditOnlineWithdLimit--->" +
                            profileObj.getCreditOnlineWithdLimit());
                }
                if (rs.getString(34) != null &&
                    rs.getString(34).trim().length() > 0) {
                    profileObj.setCreditOflineWithdLimit(rs.getString(34).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getCreditOflineWithdLimit--->" +
                            profileObj.getCreditOflineWithdLimit());
                }
                if (rs.getString(35) != null &&
                    rs.getString(35).trim().length() > 0) {
                    profileObj.setMaxDepositLimit(rs.getString(35).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getMaxDepositLimit--->" +
                            profileObj.getMaxDepositLimit());
                }
                if (rs.getString(36) != null &&
                    rs.getString(36).trim().length() > 0) {
                    profileObj.setIsMainCard(rs.getString(36).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getIsMainCard--->" +
                            profileObj.getIsMainCard());
                }
                if (rs.getString(37) != null &&
                    rs.getString(37).trim().length() > 0) {
                    profileObj.setDailyAtmDepositMax(rs.getString(37).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getDailyAtmDepositMax--->" +
                            profileObj.getDailyAtmDepositMax());
                }
                if (rs.getString(38) != null &&
                    rs.getString(38).trim().length() > 0) {
                    profileObj.setFraudInfoFlag(rs.getString(38).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getFraudInfoFlag--->" +
                            profileObj.getFraudInfoFlag());
                }
                if (rs.getString(39) != null &&
                    rs.getString(39).trim().length() > 0) {
                    profileObj.setLifeHighAtmAmount(rs.getString(39).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getLifeHighAtmAmount--->" +
                            profileObj.getLifeHighAtmAmount());
                }
                if (rs.getString(40) != null &&
                    rs.getString(40).trim().length() > 0) {
                    profileObj.setLifeHighPosAmount(rs.getString(40).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getLifeHighPosAmount--->" +
                            profileObj.getLifeHighPosAmount());
                }
                if (rs.getString(41) != null &&
                    rs.getString(41).trim().length() > 0) {
                    profileObj.setLifeHighAvailableBalance(rs.getString(41).
                            trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getLifeHighAvailableBalance--->" +
                            profileObj.getLifeHighAvailableBalance());
                }
                if (rs.getString(42) != null &&
                    rs.getString(42).trim().length() > 0) {
                    profileObj.setMinLoadAmount(rs.getString(42).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getMinLoadAmount--->" +
                            profileObj.getMinLoadAmount());
                }
                if (rs.getString(43) != null &&
                    rs.getString(43).trim().length() > 0) {
                    profileObj.setMaxLoadAmount(rs.getString(43).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getMaxLoadAmount--->" +
                            profileObj.getMaxLoadAmount());
                }
                if (rs.getString(44) != null &&
                    rs.getString(44).trim().length() > 0) {
                    profileObj.setMinTransAmount(rs.getString(44).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getMinTransAmount--->" +
                            profileObj.getMinTransAmount());
                }
                if (rs.getString(45) != null &&
                    rs.getString(45).trim().length() > 0) {
                    profileObj.setMaxTransAmount(rs.getString(45).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getMaxTransAmount--->" +
                            profileObj.getMaxTransAmount());
                }
                if (rs.getString(46) != null &&
                    rs.getString(46).trim().length() > 0) {
                    profileObj.setEarnedPoints(rs.getString(46).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getEarnedPoints--->" +
                            profileObj.getEarnedPoints());
                }
                if (rs.getString(47) != null &&
                    rs.getString(47).trim().length() > 0) {
                    profileObj.setSinceZeroBalance(rs.getString(47).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getSinceZeroBalance--->" +
                            profileObj.getSinceZeroBalance());
                }
                if (rs.getString(48) != null &&
                    rs.getString(48).trim().length() > 0) {
                    profileObj.setPrimaryCardNo(rs.getString(48).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getPrimaryCardNo--->" +
                            profileObj.getPrimaryCardNo());
                }
                if (rs.getString(49) != null &&
                    rs.getString(49).trim().length() > 0) {
                    profileObj.setTotalLostStolen(rs.getString(49).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getTotalLostStolen--->" +
                            profileObj.getTotalLostStolen());
                }
                if (rs.getString(50) != null &&
                    rs.getString(50).trim().length() > 0) {
                    profileObj.setTotalFreeAtmWithds(rs.getString(50).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getTotalFreeAtmWithds--->" +
                            profileObj.getTotalFreeAtmWithds());
                }
                if (rs.getString(51) != null &&
                    rs.getString(51).trim().length() > 0) {
                    profileObj.setTotalLastDeclines(rs.getString(51).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getTotalLastDeclines--->" +
                            profileObj.getTotalLastDeclines());
                }
                if (rs.getString(52) != null &&
                    rs.getString(52).trim().length() > 0) {
                    profileObj.setTotalTransAmount(rs.getString(52).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getTotalTransAmount--->" +
                            profileObj.getTotalTransAmount());
                }
                if (rs.getString(53) != null &&
                    rs.getString(53).trim().length() > 0) {
                    profileObj.setTotalCommAmount(rs.getString(53).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getTotalCommAmount--->" +
                            profileObj.getTotalCommAmount());
                }
                if (rs.getString(54) != null &&
                    rs.getString(54).trim().length() > 0) {
                    profileObj.setTotalFeeReversals(rs.getString(54).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getTotalFeeReversals--->" +
                            profileObj.getTotalFeeReversals());
                }
                if (rs.getString(55) != null &&
                    rs.getString(55).trim().length() > 0) {
                    profileObj.setLastRedeemedDate(rs.getString(55).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getLastRedeemedDate--->" +
                            profileObj.getLastRedeemedDate());
                }
                if (rs.getString(56) != null &&
                    rs.getString(56).trim().length() > 0) {
                    profileObj.setConfirmedPoints(rs.getString(56).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getConfirmedPoints--->" +
                            profileObj.getConfirmedPoints());
                }
                if (rs.getString(57) != null &&
                    rs.getString(57).trim().length() > 0) {
                    profileObj.setFortifiedPoints(rs.getString(57).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getFortifiedPoints--->" +
                            profileObj.getFortifiedPoints());
                }
                if (rs.getString(58) != null &&
                    rs.getString(58).trim().length() > 0) {
                    profileObj.setRedeemedPoints(rs.getString(58).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getRedeemedPoints--->" +
                            profileObj.getRedeemedPoints());
                }
                if (rs.getString(59) != null &&
                    rs.getString(59).trim().length() > 0) {
                    profileObj.setAvailablePoints(rs.getString(59).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getAvailablePoints--->" +
                            profileObj.getAvailablePoints());
                }
                if (rs.getString(60) != null &&
                    rs.getString(60).trim().length() > 0) {
                    profileObj.setPendingPoints(rs.getString(60).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getPendingPoints--->" +
                            profileObj.getPendingPoints());
                }
                if (rs.getString(61) != null &&
                    rs.getString(61).trim().length() > 0) {
                    profileObj.setQuestionId(rs.getString(61).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getQuestionId--->" +
                            profileObj.getQuestionId());
                }
                if (rs.getString(62) != null &&
                    rs.getString(62).trim().length() > 0) {
                    profileObj.setQuestionAnswer(rs.getString(62).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getQuestionAnswer--->" +
                            profileObj.getQuestionAnswer());
                }
                if (rs.getString(63) != null &&
                    rs.getString(63).trim().length() > 0) {
                    profileObj.setAssignmentNo(rs.getString(63).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getAssignmentNo--->" +
                            profileObj.getAssignmentNo());
                }
                if (rs.getString(64) != null &&
                    rs.getString(64).trim().length() > 0) {
                    profileObj.setStakeHolderId(rs.getString(64).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getStakeHolderId--->" +
                            profileObj.getStakeHolderId());
                }
                if (rs.getString(65) != null &&
                    rs.getString(65).trim().length() > 0) {
                    profileObj.setSalesNodeNo(rs.getString(65).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getSalesNodeNo--->" +
                            profileObj.getSalesNodeNo());

                }
                if (rs.getString(66) != null && rs.getString(66).trim().length() > 0) {
                    profileObj.setChId(rs.getString(66).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                                                    "getChId--->" +
                                                    profileObj.getChId());
                }

                if (rs.getString(67) != null &&
                    rs.getString(67).trim().length() > 0) {
                    profileObj.setCardLinkType(rs.getString(67).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getCardLinkType--->" +
                            profileObj.getCardLinkType());
                }
                if (rs.getString(68) != null &&
                    rs.getString(68).trim().length() > 0) {
                    profileObj.setMemberId(rs.getString(68).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getMemberId--->" +
                            profileObj.getMemberId());
                }
                if (rs.getString(69) != null &&
                    rs.getString(69).trim().length() > 0) {
                    profileObj.setCardNickName(rs.getString(69).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getCardNickName--->" +
                            profileObj.getCardNickName());
                }
                if (rs.getString(70) != null &&
                    rs.getString(70).trim().length() > 0) {
                    profileObj.setHostId(rs.getString(70).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getHostId--->" +
                            profileObj.getHostId());
                }
                if (rs.getString(71) != null &&
                    rs.getString(71).trim().length() > 0) {
                    profileObj.setDrivingLisenseNumber(rs.getString(71).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                            "getDrivingLisenseNumber--->" +
                            profileObj.getDrivingLisenseNumber());

                }
                if (rs.getString(72) != null && rs.getString(72).trim().length() > 0) {
                    profileObj.setDrivingLisenseState(rs.getString(72).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                                                    "getDrivingLisenseState--->" +
                                                    profileObj.getDrivingLisenseState());
                }
                if (rs.getString(73) != null && rs.getString(73).trim().length() > 0) {
                    profileObj.setForeignCountryCode(rs.getString(73).trim());
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                            LOG_CONFIG),
                                                    "getForeignCountryCode--->" +
                                                    profileObj.getForeignCountryCode());
                }
            }
        }
    } catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                        "Exception in getting Card Holder Profile for card Number--->" +
                                        ex);
        throw ex;
    } finally {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException ex1) {
        }
    }
    return profileObj;
  }

  /**
   * The method returns the switch id for the given card program. It queies the database table iso_switches
   * to find the switch for the given card program. It retrurns null if no such card program was found.
   * @param cardPrgID String
   * @return String
   */

  public String getSwitchID(String cardPrgID) {
    String switchID = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting Switch for Provided Card Program--->" +
                                      cardPrgID);
      query.append("select switch_id from card_programs where card_prg_id = ");
      CommonUtilities.buildQueryInfo(query, cardPrgID, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting Switch for Provided Card Program--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          switchID = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Switch for Provided Card Program--->" +
                                          switchID);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in getting Switch for Provided Card Program--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }

    return switchID;
  }

  /**
   * The method checks whether batch transaction is allowed for the given switch id. It returns true if the
   * batch transaction is allowed on given switch id else it returns false.
   * @param switchID String
   * @return boolean
   */
  public boolean isBatchAllowed(String switchID) {
    boolean isBatchMode = false;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for checking batch status for given Switch--->" +
                                      switchID);
      query.append(
          "select bat_trans_allowed from iso_switches where switch_id = ");
      CommonUtilities.buildQueryInfo(query, switchID, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for checking batch status for given Switch--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          isBatchMode = (rs.getString(1).trim().equalsIgnoreCase(Constants.
              YES_OPTION) ? true : false);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Batch Status for provided switch--->" +
                                          isBatchMode);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in checking batch status for given Switch--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return isBatchMode;
  }

  /**
   * The method checks whether the given switch is active or not. It returns true if the given switch is
   * active else it returns false.
   * @param switchID String
   * @return boolean
   */

  public boolean isSwitchActive(String switchID) {
    boolean isActive = false;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for checking active status for given Switch--->" +
                                      switchID);
      query.append("select switch_active from iso_switches where switch_id = ");
      CommonUtilities.buildQueryInfo(query, switchID, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for checking active status for given Switch--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          isActive = (rs.getString(1).trim().equalsIgnoreCase(Constants.
              YES_OPTION) ? true : false);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Active Status for provided switch--->" +
                                          isActive);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in checking active status for given Switch--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return isActive;
  }

  /**
   * The method returns the blanace of the given card no. It uses the database table "card_funds" to find
   * the balance of the given card no.
   * @param cardNumber String
   * @return String
   */

  public String getCardBalance(String cardNumber) throws Exception{
    String cardBalance = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for Getting card balance for card--->" +
                                      cardNumber);
      query.append("select card_balance from card_funds where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for Getting card balance for card--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          cardBalance = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Balance Got--->" +
                                          cardBalance);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting card balance--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return cardBalance;
  }

  /**
   * The method returns the ledger balance of the given card no. It uses the database table "card_funds"
   * to find the balance of the given card no.
   * @param cardNumber String
   * @return String
   */

  public String getCardLedgerBalance(String cardNumber) {
    String cardBalance = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for Getting ledger_balance for card--->" +
                                      cardNumber);
      query.append("select ledger_balance from card_funds where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for Getting ledger_balance for card--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          cardBalance = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Balance Got--->" +
                                          cardBalance);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting ledger_balance--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return cardBalance;
  }

  /**
   * This method find the account number for the given card no. It uses the database table "card_accounts" to
   * find the account number for the given card number.
   * @param cardNumber String
   * @return String
   */
  public String getCardAccountNumber(String cardNumber) {
    String accountNumber = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for Getting account number for card--->" +
                                      cardNumber);
      query.append("select account_number from card_accounts where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for Getting account number for card--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          accountNumber = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Account Number got--->" +
                                          accountNumber);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting account number--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return accountNumber;
  }

  /**
   * The method finds and returns the card programs name for the given card program. It uses the database
   * table "card_programs" to find the card program name for the given card program id.
   * @param cardPrgID String
   * @return String
   */
  public String getCardProgramName(String cardPrgID) throws Exception{
    String cardPrgName = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for Getting Card Program Name for card prg id--->" +
                                      cardPrgID);
      query.append(
          "select card_prg_name from card_programs where card_prg_id = ");
      CommonUtilities.buildQueryInfo(query, cardPrgID, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for Getting Card Program Name for card prg id--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          cardPrgName = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Card Prg Name got--->" +
                                          cardPrgName);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Getting Card Program Name--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return cardPrgName;
  }

  /**
   * The method verifies whether negative balance is OK for the given card program and service.
   * It returns true if the negative balance is OK else false.
   * @param cardProgramID String
   * @param serviceID String
   * @return boolean
   */

  public boolean checkIsOkOnNegativeBalance(String cardProgramID,
                                            String serviceID) {
    boolean isOkOnNegativeBalance = false;

    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for checking is_ok_on_negative_balance for Card Program ID--->" +
                                      cardProgramID + "<---service ID--->" +
                                      serviceID);
      query.append(
          "select is_ok_on_neg_bal from card_program1s where card_prg_id = ");
      CommonUtilities.buildQueryInfo(query, cardProgramID, true);
      query.append(" and service_id = ");
      CommonUtilities.buildQueryInfo(query, serviceID, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for checking is_ok_on_negative_balance--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          String isOkFlag = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "is_ok_on_negative_balance--->" +
                                          isOkFlag);
          if (isOkFlag != null &&
              isOkFlag.trim().equalsIgnoreCase(Constants.YES_OPTION)) {
            return true;
          }
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting card balance--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return isOkOnNegativeBalance;
  }

  /**
   * The method debits the funds from the given card no. It calls the database procedure "CONFIRM_DEBIT"
   * to debit the funds from the given card no. At the end of the processing it returns response which
   * describes the status of the processing in detail.
   * @param feeObj DbRequestInfoObject
   * @return DbResponseInfoObject
   */
  public DbResponseInfoObject debitServiceFee(DbRequestInfoObject feeObj) throws Exception{
    DbResponseInfoObject respObj = new DbResponseInfoObject();
    StringBuffer query = new StringBuffer();
    CallableStatement cs = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Debiting Service Fee");
      query.append("execute procedure confirm_debit(p_str_cardno = ?, p_str_tranid = ?,p_str_serviceid = ?,p_d_amount = ?,p_d_switch_balance = ?,p_org_trace_audit = ?,p_device_type = ?,p_device_id = ?,p_crd_aceptor_code = ?,p_crd_aceptor_name = ?,p_merchant_cat_cd = ?,p_fee_amount = ?,p_fee_desc = ?,pacq_id = ?,psub_srv = ?,pdata_2 = ?,pdata_3 = ?,pacq_userid = ?)");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for Debiting Service Fee--->" +
                                      query);

      cs = dbConn.prepareCall(query.toString());
      cs.setString(1, feeObj.getCardNo());
      cs.setString(2, feeObj.getTransId());
      cs.setString(3, feeObj.getServiceId());
      cs.setString(4, feeObj.getAmount());
      cs.setString(5, feeObj.getSwitchBalance());
      cs.setString(6, feeObj.getOrgTraceAudit());
      cs.setString(7, feeObj.getDeviceType());
      cs.setString(8, feeObj.getDeviceId());
      cs.setString(9, feeObj.getCrdAceptorCode());
      cs.setString(10, feeObj.getCrdAceptorName());
      cs.setString(11, feeObj.getMerchantCatCd());
      cs.setString(12, feeObj.getFeeAmount());
      cs.setString(13, feeObj.getFeeDesc());
      cs.setString(14, feeObj.getAcquirerId());
      cs.setString(15, feeObj.getAcquirerData1());
      cs.setString(16, feeObj.getAcquirerData2());
      cs.setString(17, feeObj.getAcquirerData3());
      cs.setString(18, feeObj.getAcquirerUserId());

      rs = cs.executeQuery();

      if (rs.next()) {

        String feeAmt = null;

        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          respObj.setResponseCode(rs.getString(1).trim());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          respObj.setIsoSerialNo(rs.getString(2).trim());
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          respObj.setTraceAuditNo(rs.getString(3).trim());
        }
        if (rs.getString(4) != null && rs.getString(4).trim().length() > 0) {
          respObj.setRemainingBal(rs.getString(4).trim());
        }
        if (rs.getString(5) != null && rs.getString(5).trim().length() > 0) {
          respObj.setResponseDesc(rs.getString(5).trim());
        }
        if (rs.getString(6) != null && rs.getString(6).trim().length() > 0) {
          feeAmt = rs.getString(6).trim();
        }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Received ---- Response Code--->" +
                                        respObj.getResponseCode()
                                        + "<---Response Description--->" +
                                        respObj.getResponseDesc()
                                        + "<---Trace Audtit No--->" +
                                        respObj.getTraceAuditNo()
                                        + "<---ISO Serial No--->" +
                                        respObj.getIsoSerialNo()
                                        + "<---Remaining Balance--->" +
                                        respObj.getRemainingBal()
                                        + "<---Fee Amount--->" +
                                        feeAmt);
      }

    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Debiting Service Fee--->" +
                                      ex);
      throw ex;
    }finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (cs != null) {
          cs.close();
          cs = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return respObj;
  }

  /**
   * This method checks whether a card has enough balance for fee deduction or not. Following checks are
   * performed in this procedure:<br>
   * 1. card number is valid or not (error code: 14)<br>
   * 2. card number is expired or not (error code: 54)<br>
   * 3. specified service is available for the card or not (error code: 57)<br>
   * 4. card has valid status or not. default valid status is B (active).<br>
   * 5. card balance is greater than service fee or not (error code: 51)<br>
   * If the card passes through these checks than service fee and successfull response code (00)
   * is returned with proper description of the processing.
   * @param requestObj DbRequestInfoObject
   * @return DbResponseInfoObject
   */

  public DbResponseInfoObject checkDebitAllowed(DbRequestInfoObject requestObj) {
    DbResponseInfoObject respObj = new DbResponseInfoObject();
    StringBuffer query = new StringBuffer();
    CallableStatement cs = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for checking Debit Allowed");
      query.append("Execute procedure check_service_fee(");
      if (requestObj.getCardNo() != null) {
        query.append(" pcard_no = '" + requestObj.getCardNo() + "',");
      }
      else {
        query.append(" pcard_no = " + null +",");
      }
      if (requestObj.getServiceId() != null) {
        query.append(" pservice_id = '" + requestObj.getServiceId() + "',");
      }
      else {
        query.append(" pservice_id = " + null +",");
      }
      if (requestObj.getAmount() != null) {
        query.append(" ptrans_amount  = " +
                     Double.parseDouble(requestObj.getAmount()) +
                     ",");
      }
      else {
        query.append(" ptrans_amount = " + null +",");
      }
      if (requestObj.getSkipStatus() != null) {
        query.append(" pskip_status  = '" + requestObj.getSkipStatus() +
                     "',");
      }
      else {
        query.append(" pskip_status = " + null +",");
      }
      if (requestObj.getIsOKNegBal() != null) {
        query.append(" pchk_ok_on_neg_bal   = '" + requestObj.getIsOKNegBal() +
                     "',");
      }
      else {
        query.append(" pchk_ok_on_neg_bal  = " + null +",");
      }
      if (requestObj.getChkTransAmt() != null) {
        query.append(" pchk_trans_amt = '" + requestObj.getChkTransAmt() +
                     "')");
      }
      else {
        query.append(" pchk_trans_amt = " + null +")");
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for checking Debit Allowed--->" +
                                      query);

      cs = dbConn.prepareCall(query.toString());
      rs = cs.executeQuery();

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          respObj.setResponseCode(rs.getString(1).trim());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          respObj.setResponseDesc(rs.getString(2).trim());
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          respObj.setFeeAmount(rs.getString(3).trim());
        }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Received ---- Response Code--->" +
                                        respObj.getResponseCode()
                                        + "<---Response Description--->" +
                                        respObj.getResponseDesc()
                                        + "<---Debit Fee--->" +
                                        respObj.getFeeAmount()
                                        );
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in checking Debit Allowed--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (cs != null) {
          cs.close();
          cs = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return respObj;
  }

  /**
   * This method is developed to check whether funds can be added to the given card account:<br>
   * 1. card number is valid or not (error code: 14)<br>
   * 2. card number is expired or not (error code: 54)<br>
   * 3. specified service is available for the card or not (error code: 57)<br>
   * 4. card has valid status or not. default valid status is B (active).<br>
   * 5. card balance is greater than service fee or not (error code: 51)<br>
   * if the card passes through these checks than service fee and successfull response code (00) is returned
   * @param requestObj DbRequestInfoObject
   * @return DbResponseInfoObject
   */

  public DbResponseInfoObject checkCard(DbRequestInfoObject requestObj) throws Exception{
    DbResponseInfoObject respObj = new DbResponseInfoObject();
    StringBuffer query = new StringBuffer();
    CallableStatement cs = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Checking Credit Allowed");
      query.append("Execute procedure check_card(");
      if (requestObj.getCardNo() != null) {
        query.append(" cardno = '" + requestObj.getCardNo() + "',");
      }
      else {
        query.append(" cardno = " + null +",");
      }
      query.append(" init_string= " + null +",");
      if (requestObj.getAmount() != null) {
        query.append(" amount = " + Double.parseDouble(requestObj.getAmount()) +
                     ",");
      }
      else {
        query.append(" amount  = " + 0.0 + ",");
      }
      query.append(" process_code= " + null +",");
      if (requestObj.getServiceId() != null) {
        query.append(" pservice_id = '" + requestObj.getServiceId() + "',");
      }
      else {
        query.append(" pservice_id = " + null +",");
      }

      query.append(" check_balance = " + 0 + ",");
      if (requestObj.getSkipStatus() != null) {
        query.append(" pskip_status  = '" + requestObj.getSkipStatus() +
                     "',");
      }
      else {
        query.append(" pskip_status = " + null +",");
      }
      query.append(" papply_fee= " + 0 + ")");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for Checking Credit Allowed--->" +
                                      query);

      cs = dbConn.prepareCall(query.toString());
      rs = cs.executeQuery();

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          respObj.setResponseCode(rs.getString(1).trim());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          respObj.setResponseDesc(rs.getString(2).trim());
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          respObj.setFeeAmount(rs.getString(3).trim());
        }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Received ---- Response Code--->" +
                                        respObj.getResponseCode()
                                        + "<---Response Description--->" +
                                        respObj.getResponseDesc()
                                        + "<---Credit Fee--->" +
                                        respObj.getFeeAmount()
                                        );
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in  Checking Credit Allowed--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (cs != null) {
          cs.close();
          cs = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return respObj;
  }

  /**
   * This method transfer the fund from one card account to another card account. It calls the database
   * procedure "APPLY_CARD_UPGRADE" to perfrom the processing. At the end of the processing it returns
   * response with proper response code and description.
   * @param requestObj DbRequestInfoObject
   * @return DbResponseInfoObject
   */

  public DbResponseInfoObject transferAmount(DbRequestInfoObject requestObj) throws Exception{
    DbResponseInfoObject respObj = new DbResponseInfoObject();
    StringBuffer query = new StringBuffer();
    CallableStatement cs = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Transfering Amount --- From Card Number--->" +
                                      requestObj.getCardNo()
                                      + "<---To Card Number--->" +
                                      requestObj.getToCardNumber()
                                      + "<---Transfer Amount--->" +
                                      requestObj.getAmount()
                                      + "<---Service ID--->" +
                                      requestObj.getServiceId()
                                      + "<---Service Fee--->" +
                                      requestObj.getFeeAmount()
                                      + "<---Apply Fee--->" +
                                      requestObj.getApplyFee()
                                      + "<---getDeviceType--->" +
                                      requestObj.getDeviceType()
                                      + "<---isIsAllCashOut--->" +
                                      requestObj.isIsAllCashOut()
                                      + "<---getAcquirerId--->" +
                                      requestObj.getAcquirerId()
                                      + "<---getAcquirerData1--->" +
                                      requestObj.getAcquirerData1()
                                      + "<---getAcquirerData2--->" +
                                      requestObj.getAcquirerData2()
                                      + "<---getAcquirerData3--->" +
                                      requestObj.getAcquirerData3()
                                      + "<---getAcquirerUserId--->" +
                                      requestObj.getAcquirerUserId()
                                      + "<---getCrdAceptorCode--->" +
                                      requestObj.getCrdAceptorCode()
                                      + "<---getCrdAceptorName--->" +
                                      requestObj.getCrdAceptorName()
                                      + "<---getMerchantCatCd--->" +
                                      requestObj.getMerchantCatCd()
                                      + "<---getDeviceId--->" +
                                      requestObj.getDeviceId()
                                      + "<---getRetRefNumber--->" +
                                      requestObj.getRetRefNumber()
                                      + "<---getActivateToCard--->" +
                                      requestObj.getActivateToCard());
      query.append("execute procedure apply_card_upgrade(pfrom_card = ?,pto_card = ?,pservice_id = ?,papply_fee	 = ?,pdevice_type = ?,pis_all_cash_out = ?,pamount = ?,pacq_id = ?,psub_srv = ?,pdata_2 = ?,pdata_3 = ?,pacq_userid = ?,pcard_acptr_code = ?,pcard_acptr_name = ?,pmcc = ?,pdev_id = ?,pretrieval_ref = ?,	pactivate_to_card = ?)");
/*
      query.append("Execute procedure apply_card_upgrade(");
      if (requestObj.getCardNo() != null) {
        query.append(" pfrom_card = '" + requestObj.getCardNo() + "',");
      }
      else {
        query.append(" pfrom_card = " + null +",");
      }
      if (requestObj.getToCardNumber() != null) {
        query.append(" pto_card = '" + requestObj.getToCardNumber() + "',");
      }
      else {
        query.append(" pto_card = " + null +",");
      }
      if (requestObj.getServiceId() != null) {
        query.append(" pservice_id = '" + requestObj.getServiceId() + "',");
      }
      else {
        query.append(" pservice_id = " + null +",");
      }
      if (requestObj.getApplyFee() != null &&
          requestObj.getApplyFee().equals(Constants.YES_OPTION)) {
        query.append(" papply_fee = 'Y',");
      }
      else {
        query.append(" papply_fee= 'N',");
      }
      if (requestObj.getDeviceType() != null) {
        query.append(" pdevice_type= '" + requestObj.getDeviceType() + "',");
      }
      else {
        query.append(" pdevice_type= " + null +",");
      }
      if (requestObj.isIsAllCashOut()) {
        query.append(" pis_all_cash_out= 'Y',");
      }
      else {
        query.append(" pis_all_cash_out= 'N',");
      }
      if (requestObj.getAmount() != null) {
        query.append(" pamount= '" + requestObj.getAmount() + "')");
      }
      else {
        query.append(" pamount= " + null +")");
      }
*/
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for Transfering Amount--->" +
                                      query);

      cs = dbConn.prepareCall(query.toString());
      cs.setString(1,requestObj.getCardNo());
      cs.setString(2,requestObj.getToCardNumber());
      cs.setString(3,requestObj.getServiceId());
      cs.setString(4,requestObj.getApplyFee());
      cs.setString(5,requestObj.getDeviceType());
      cs.setString(6,(requestObj.isIsAllCashOut() ? "Y" : "N"));
      cs.setString(7,requestObj.getAmount());
      cs.setString(8,requestObj.getAcquirerId());
      cs.setString(9,requestObj.getAcquirerData1());
      cs.setString(10,requestObj.getAcquirerData2());
      cs.setString(11,requestObj.getAcquirerData3());
      cs.setString(12,requestObj.getAcquirerUserId());
      cs.setString(13,requestObj.getCrdAceptorCode());
      cs.setString(14,requestObj.getCrdAceptorName());
      cs.setString(15,requestObj.getMerchantCatCd());
      cs.setString(16,requestObj.getDeviceId());
      cs.setString(17,requestObj.getRetRefNumber());
      cs.setString(18,requestObj.getActivateToCard());

      rs = cs.executeQuery();

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          respObj.setResponseCode(rs.getString(1).trim());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          respObj.setResponseDesc(rs.getString(2).trim());
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          respObj.setTraceAuditNo(rs.getString(3).trim());
        }
        if (rs.getString(4) != null && rs.getString(4).trim().length() > 0) {
          respObj.setTraceAuditNoToCard(rs.getString(4).trim());
        }
        if (rs.getString(5) != null && rs.getString(5).trim().length() > 0) {
          respObj.setCardBalance(rs.getString(5).trim());
        }
        if (rs.getString(6) != null && rs.getString(6).trim().length() > 0) {
          respObj.setNewCardBalance(rs.getString(6).trim());
        }
        if (rs.getString(7) != null && rs.getString(7).trim().length() > 0) {
          respObj.setFeeAmount(rs.getString(7).trim());
        }

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Received ---- Response Code--->" +
                                        respObj.getResponseCode()
                                        + "<---Response Description--->" +
                                        respObj.getResponseDesc()
                                        + "<---Trace Audit No--->" +
                                        respObj.getTraceAuditNo()
                                        + "<---Trace Audit No To Card--->" +
                                        respObj.getTraceAuditNoToCard()
                                        + "<---Fee Amount--->" +
                                        respObj.getFeeAmount()
                                        + "<---Card Balance--->" +
                                        respObj.getCardBalance()
                                        + "<---New Card Balance--->" +
                                        respObj.getNewCardBalance()
                                        );
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Transferring Amount--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (cs != null) {
          cs.close();
          cs = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return respObj;
  }

  /**
   * The method is used to log the transaction into the database. Its basic purpose is to keep the
   * record of the transaction that are processed. It stores different information into the database
   * such as card no, device-id etc.
   * @param logInfo DbRequestInfoObject
   * @return DbResponseInfoObject
   */
  public DbResponseInfoObject logTransaction(DbRequestInfoObject logInfo) {

    StringBuffer query = new StringBuffer();
    DbResponseInfoObject transactionInfo = new DbResponseInfoObject();
    CallableStatement cs = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Logging transaction in database  --- CardNo--->" +
                                      logInfo.getCardNo()
                                      + "<---ServiceID--->" +
                                      logInfo.getServiceId()
                                      + "<---RetRefNumber--->" +
                                      logInfo.getRetRefNumber()
                                      + "<---MsgType--->" + logInfo.getMsgType()
                                      + "<---RemainingBalance--->" +
                                      logInfo.getRemainingBalance()
                                      + "<---Amount--->" + logInfo.getAmount()
                                      + "<---Description--->" +
                                      logInfo.getDescription()
                                      + "<---AccountNumber--->" +
                                      logInfo.getAccountNumber()
                                      + "<---InsertMode--->" +
                                      logInfo.getInsertMode()
                                      + "<---DeviceType--->" +
                                      logInfo.getDeviceType()
                                      + "<---ResponseCode--->" +
                                      logInfo.getResponseCode()
                                      + "<---DeviceId--->" +
                                      logInfo.getDeviceId()
                                      + "<---CrdAceptorCode--->" +
                                      logInfo.getCrdAceptorCode()
                                      + "<---CrdAceptorName--->" +
                                      logInfo.getCrdAceptorName()
                                      + "<---MerchantCatCd--->" +
                                      logInfo.getMerchantCatCd()
                                      + "<---AcquirerId--->" +
                                      logInfo.getAcquirerId()
                                      );
      query.append("Execute procedure LOG_TRANSACTION(" +
                   "pcard_no=" +
                   (logInfo.getCardNo() != null ?
                    "'" + logInfo.getCardNo() + "'" : null) +
                   "," +
                   "pservice_id='" + logInfo.getServiceId() + "'," +
                   "pret_ref=" + logInfo.getRetRefNumber() + "," +
                   "pmsg_type='" + logInfo.getMsgType() + "'," +
                   "premaining_bal=" + logInfo.getRemainingBalance() + "," +
                   "pamt_processed=" +
                   (logInfo.getAmount() == null ? "0.0" : logInfo.getAmount()) +
                   "," +
                   "pdescription=" +
                   (logInfo.getDescription() != null ?
                    "'" + logInfo.getDescription() + "'" : null) + "," +
                   "paccount_id=" +
                   (logInfo.getAccountNumber() != null ?
                    "'" + logInfo.getAccountNumber() + "'" : null) +
                   "," +
                   "pinsert_mode='" + logInfo.getInsertMode() + "'," +
                   "pdevice_type=" +
                   (logInfo.getDeviceType() != null ?
                    "'" + logInfo.getDeviceType() + "'" : null) + "," +
                   "presp_code='" + logInfo.getResponseCode() + "'," +
                   "pdevice_id=" +
                   (logInfo.getDeviceId() != null ?
                    "'" + logInfo.getDeviceId() + "'" : null) +
                   "," +
                   "pcard_aceptor_code=" +
                   (logInfo.getCrdAceptorCode() != null ?
                    "'" + logInfo.getCrdAceptorCode() + "'" : null) +
                   "," +
                   "pcard_aceptor_name=" +
                   (logInfo.getCrdAceptorName() != null ?
                    "'" + logInfo.getCrdAceptorName() + "'" : null) + "," +
                   "pmerchant_cat_code=" +
                   (logInfo.getMerchantCatCd() != null ?
                    "'" + logInfo.getMerchantCatCd() + "'" : null) +
                   "," +
                   "p_acq_id=" +
                   (logInfo.getAcquirerId() != null ?
                    "'" + logInfo.getAcquirerId() + "'" : null) +
                   ")"
                   );
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for Logging Transaction--->" +
                                      query);

      cs = dbConn.prepareCall(query.toString());
      rs = cs.executeQuery();

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          transactionInfo.setIsoSerialNo(rs.getString(1).trim());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "ISO Serial Number of Transaction--->" +
                                          transactionInfo.getIsoSerialNo());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          transactionInfo.setTraceAuditNo(rs.getString(2).trim());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Trace Audit Number of Transaction--->" +
                                          transactionInfo.getTraceAuditNo());
        }
      }

    }
    catch (Exception ex) {

    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (cs != null) {
          cs.close();
          cs = null;
        }
      }
      catch (SQLException ex1) {
      }
    }

    return transactionInfo;
  }

  /**
   * This method mask the card no and return it. It calls the database procedure "GET_CARDNO" to mask
   * the given card no.
   * @param cardNo String
   * @throws Exception
   * @return String
   */

  public String maskCardNo(String cardNo) throws Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Method for masking provided Card No -- > " +
                                    cardNo);
    CallableStatement cstmt = null;
    ResultSet rs = null;
    try {
      String qry = "Execute procedure get_cardno(pcard_no='" + cardNo + "')";
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query -- > " + qry);

      cstmt = dbConn.prepareCall(qry);
      rs = cstmt.executeQuery();
      if (rs.next()) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_FINEST), "Masked Card No Got -- > " + rs.getString(1));
        return rs.getString(1);
      } //end if
      return null;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in masking card no-- > " +
                                      ex.getMessage());
      throw ex;
    } //end catch
  } //end maskCardNo

  /**
   * The mehod fetches the success response codes for the for the given card no. It gets different
   * attributes of  the user associated with the given card no.
   * @param cardNumber String
   * @param clientResponse TransactionResponseInfoObj
   */

  public void getSuccessResponseAttributes(String cardNumber,
                                           TransactionResponseInfoObj
                                           clientResponse) throws Exception {
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for getting attributes to be send to client in case of successful resposne --- Card Number--->" +
                                      cardNumber);
      query.append("select c.user_id,c.card_status_pos,c.card_access_code,c.card_batch_no,c.expiry_on,c.first_name1,c.middle_name1,c.last_name1,f.ledger_balance from cards c, card_funds f where c.card_no = f.card_no and c.card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting attributes to be send to client in case of successful resposne --->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if (rs.next()) {

        String fName = null;
        String mName = null;
        String lName = null;
        StringBuffer name = new StringBuffer();

        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          clientResponse.setUserID(rs.getString(1).trim());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          clientResponse.setCardStatus(rs.getString(2).trim());
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          clientResponse.setAac(rs.getString(3).trim());
        }
        if (rs.getString(4) != null && rs.getString(4).trim().length() > 0) {
          clientResponse.setCardBatchNo(rs.getString(4).trim());
        }
        if (rs.getString(5) != null && rs.getString(5).trim().length() > 0) {
          clientResponse.setExpiryDate(rs.getString(5).trim());
        }
        if (rs.getString(6) != null && rs.getString(6).trim().length() > 0) {
          fName = rs.getString(6).trim();
        }
        if (rs.getString(7) != null && rs.getString(7).trim().length() > 0) {
          mName = rs.getString(7).trim();
        }
        if (rs.getString(8) != null && rs.getString(8).trim().length() > 0) {
          lName = rs.getString(8).trim();
        }
        if (rs.getString(9) != null && rs.getString(9).trim().length() > 0) {
          clientResponse.setLedgerBalance(rs.getString(9).trim());
        }

        if (fName != null && fName.trim().length() > 0) {
          name.append(fName.trim() + " ");
        }
        if (mName != null && mName.trim().length() > 0) {
          name.append(mName.trim() + " ");
        }
        if (lName != null && lName.trim().length() > 0) {
          name.append(lName.trim());
        }
        if (name != null) {
          clientResponse.setCardHolderName(name.toString());
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting attributes to be send to client in case of successful resposne--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
  }

  /**
   * The mehod fetches the success response codes for the for the given card no. It gets different
   * attributes of the user associated with the given card no.
   * @param cardNumber String
   * @param clientResponse TransactionResponseInfoObj
   * @param simpleUpgrade boolean
   */

  public void getSuccessResponseAttributes(String cardNumber,
                                           TransactionResponseInfoObj
                                           clientResponse,
                                           boolean simpleUpgrade) throws Exception{
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for getting attributes to be send to client in case of successful resposne --- Card Number--->" +
                                      cardNumber);
      query.append("select c.user_id,c.card_status_pos,c.card_access_code,c.card_batch_no,c.expiry_on,c.first_name1,c.middle_name1,c.last_name1,f.ledger_balance,f.card_balance from cards c, card_funds f where c.card_no = f.card_no and c.card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting attributes to be send to client in case of successful resposne --->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if (rs.next()) {

        String fName = null;
        String mName = null;
        String lName = null;
        StringBuffer name = new StringBuffer();

        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          clientResponse.setUserID(rs.getString(1).trim());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          clientResponse.setCardStatus(rs.getString(2).trim());
        }
        if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
          clientResponse.setAac(rs.getString(3).trim());
        }
        if (rs.getString(4) != null && rs.getString(4).trim().length() > 0) {
          clientResponse.setCardBatchNo(rs.getString(4).trim());
        }
        if (rs.getString(5) != null && rs.getString(5).trim().length() > 0) {
          clientResponse.setExpiryDate(rs.getString(5).trim());
        }
        if (rs.getString(6) != null && rs.getString(6).trim().length() > 0) {
          fName = rs.getString(6).trim();
        }
        if (rs.getString(7) != null && rs.getString(7).trim().length() > 0) {
          mName = rs.getString(7).trim();
        }
        if (rs.getString(8) != null && rs.getString(8).trim().length() > 0) {
          lName = rs.getString(8).trim();
        }
        if (rs.getString(9) != null && rs.getString(9).trim().length() > 0) {
          clientResponse.setLedgerBalance(rs.getString(9).trim());
        }
        if (rs.getString(10) != null && rs.getString(10).trim().length() > 0) {
          clientResponse.setCurrentBalance(rs.getString(10).trim());
        }

        if (fName != null && fName.trim().length() > 0) {
          name.append(fName.trim() + " ");
        }
        if (mName != null && mName.trim().length() > 0) {
          name.append(mName.trim() + " ");
        }
        if (lName != null && lName.trim().length() > 0) {
          name.append(lName.trim());
        }
        if (name != null) {
          clientResponse.setCardHolderName(name.toString());
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting attributes to be send to client in case of successful resposne--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
  }

  /**
   * The method gets the financial institution name for the given card prograram. Its basic purpose is to
   * find the institution name associated with the given card program.
   * @param cardPrgId String
   * @return String
   */

  public String getFinanacialInstitutionName(String cardPrgId) throws Exception{
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;
    String bankName = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for getting Finanacial Institution Name for Card Program ID--->" +
                                      cardPrgId);
      query.append("select b.bank_name from banks b,card_programs c where b.bank_id = c.bank_id and c.card_prg_id = ");
      CommonUtilities.buildQueryInfo(query, cardPrgId, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting Finanacial Institution Name for Card Program ID--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          bankName = rs.getString(1).trim();
        }
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Finanacial Institution Name got for Card Program ID--->" +
                                      bankName);
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting Finanacial Institution Name for Card Program ID--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return bankName;
  }

  /**
   * The method is used to fetch the card status at ATM & POS from the database.
   * @param requestObj TransactionRequestInfoObj
   */

  public void getCardStatuses(TransactionRequestInfoObj requestObj) {
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting provided Card Number statuses--->" +
                                      requestObj.getExistingCard());
      query.append(
          "select card_status_atm, card_status_pos from cards where card_no = ");
      CommonUtilities.buildQueryInfo(query, requestObj.getExistingCard(), true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting provided Card Number statuses--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ResultSet for getting provided Card Number statuses--->" +
                                      rs);
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          requestObj.setAtmStatus(rs.getString(1).trim());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Card Status ATM--->" +
                                          requestObj.getAtmStatus());
        }
        if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
          requestObj.setPosStatus(rs.getString(2).trim());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Card Status POS--->" +
                                          requestObj.getPosStatus());
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting provided Card Number statuses--->" +
                                      ex);
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
  }

  public String getTraceAuditNo(String isoSerialNo) throws Exception{
    String traceAudit = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting TraceAuditNO for provided ISO Serial No --->" +
                                      isoSerialNo);
      if (isoSerialNo == null || isoSerialNo.trim().length() == 0) {
        return null;
      }

      query.append(
          "select trace_audit_no from trans_requests where iso_serial_no = ");
      CommonUtilities.buildQueryInfo(query, isoSerialNo, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting TraceAuditNO for provided ISO Serial No--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ResultSet getting TraceAuditNO for provided ISO Serial No--->" +
                                      rs);
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          traceAudit = rs.getString(1).trim();
        }
        else {
          return null;
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting TraceAuditNO for provided ISO Serial No--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return traceAudit;
  }

  public DbResponseInfoObject creditFundsAtOltp(DbRequestInfoObject reqObj) throws
      Exception {
    DbResponseInfoObject respObj = new DbResponseInfoObject();
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_CONFIG), "Going to load funds...");

    query.append("execute procedure LOAD_FUNDS(pcard_no = ?, pamount = ?,pdevice_type = ?,pswitch_bal = ?,pret_ref = ?,presp_code = ?,pdesc = ?,papply_fee = ?,pservice_id = ?, pacq_id = ?, psub_srv = ?, pdata_2 = ?, pdata_3 = ?, pacq_userid = ?, pcard_acptr_code = ?, pcard_acptr_name = ?, pmcc = ?, pdev_id = ?)");

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_CONFIG), "Executing Query -- > " + query);

    try {
      //prepare the call
      cstmt = dbConn.prepareCall(query.toString());

      cstmt.setString(1, reqObj.getCardNo());
      cstmt.setString(2, reqObj.getAmount());
      cstmt.setString(3, reqObj.getDeviceType());
      cstmt.setString(4, reqObj.getSwitchBalance());
      cstmt.setString(5, reqObj.getRetRefNumber());
      cstmt.setString(6, reqObj.getResponseCode());
      cstmt.setString(7, reqObj.getDescription());
      cstmt.setString(8, (reqObj.getApplyFee().trim().equals("Y") ? "1" : "0"));
      cstmt.setString(9, reqObj.getServiceId());
      cstmt.setString(10, reqObj.getAcquirerId());
      cstmt.setString(11, reqObj.getAcquirerData1());
      cstmt.setString(12, reqObj.getAcquirerData2());
      cstmt.setString(13, reqObj.getAcquirerData3());
      cstmt.setString(14, reqObj.getAcquirerUserId());
      cstmt.setString(15, reqObj.getCrdAceptorCode());
      cstmt.setString(16, reqObj.getCrdAceptorName());
      cstmt.setString(17, reqObj.getMerchantCatCd());
      cstmt.setString(18, reqObj.getDeviceId());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "Executing Query -- > " + query);

      //execute the procedure
      rs = cstmt.executeQuery();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "Query Executed....Looking for values...");
      if (rs.next()) {
        String response = rs.getString(1);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Response Code -- > " + response);
        //set in response object
        respObj.setResponseCode(response);

        String desc = rs.getString(2);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Response Desc -- > " + desc);
        //set in response obj
        respObj.setResponseDesc(desc);

        String cardBalance = rs.getString(3);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Card Balance -- > " + cardBalance);
        //set it in response
        respObj.setCardBalance(cardBalance);

        String feeAmount = rs.getString(4);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Fee Amount -- > " + feeAmount);
        //set it in response
        respObj.setFeeAmount(feeAmount);

        String isoSerialNo = rs.getString(5);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "ISO Serial No -- > " + isoSerialNo);
        //set it in response
        respObj.setIsoSerialNo(isoSerialNo);
      } //end if

      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null)
          rs.close();
        if (cstmt != null)
          cstmt.close();
      } //end try
      catch (Exception ex) {} //end
    } //end finally
  } //end if

  public DbResponseInfoObject activateCardInOltp(DbRequestInfoObject reqObj) throws
      Exception {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "activateCardInOltp --- Arguments Received :: Card No -- > " + reqObj.getCardNo()
                                    + "<--ApplyFee -- > " + reqObj.getApplyFee() +
                                    "<--ServiceId -- > " + reqObj.getServiceId() +
                                    "<--DeviceType -- > " + reqObj.getDeviceType() +
                                    "<--Amount -- > " + reqObj.getAmount() +
                                    "<--AcquirerId -- > " + reqObj.getAcquirerId() +
                                    "<--AcqData1 -- > " + reqObj.getAcquirerData1() +
                                    "<--AcqData1 -- > " + reqObj.getAcquirerData2() +
                                    "<--AcqData1 -- > " + reqObj.getAcquirerData3() +
                                    "<--AcqUsrId -- > " + reqObj.getAcquirerUserId() +
                                    "<--CardAcceptorId -- > " + reqObj.getCrdAceptorCode() +
                                    "<--CardAcceptNameAndLoc -- > " + reqObj.getCrdAceptorName() +
                                    "<--Mcc -- > " + reqObj.getMerchantCatCd() +
                                    "<--DeviceId -- > " + reqObj.getDeviceId() +
                                    "<--RetreivalRefNum -- > " + reqObj.getRetRefNumber());

    //prepare the query
    StringBuffer query = new StringBuffer();

    CallableStatement cstmt = null;
    ResultSet rs = null;
    DbResponseInfoObject respObj = new DbResponseInfoObject();
    try {

      String tempApplyFee = reqObj.getApplyFee();
      String applyFee = null;
      if (reqObj.getApplyFee() != null && reqObj.getApplyFee().trim().equalsIgnoreCase("Y")) {
        applyFee = "1";
      }
      else if (reqObj.getApplyFee() != null && reqObj.getApplyFee().trim().equalsIgnoreCase("N")) {
        applyFee = "0";
      }
      else {
        applyFee = "1"; // By default
      }

      query.append("execute procedure activate_card(pcardno= ?,pservice_id= ?,papply_fee= ?,pdevice_type= ?,pamount= ?, pacq_id= ?,psub_srv= ?, pdata_2= ? , pdata_3= ? , pacq_userid= ? , pcard_acptr_code= ? , pcard_acptr_name= ? , pmcc= ? , pdev_id= ? , pretrieval_ref= ? )");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query -- > " + query);

      cstmt = dbConn.prepareCall(query.toString());

      cstmt.setString(1, reqObj.getCardNo());
      cstmt.setString(2, reqObj.getServiceId());
      cstmt.setString(3, applyFee);
      cstmt.setString(4, reqObj.getDeviceType());
      cstmt.setString(5, (reqObj.getAmount() != null ? reqObj.getAmount() : "0"));
      cstmt.setString(6, reqObj.getAcquirerId());
      cstmt.setString(7, reqObj.getAcquirerData1());
      cstmt.setString(8, reqObj.getAcquirerData2());
      cstmt.setString(9, reqObj.getAcquirerData3());
      cstmt.setString(10, reqObj.getAcquirerUserId());
      cstmt.setString(11, reqObj.getCrdAceptorCode());
      cstmt.setString(12, reqObj.getCrdAceptorName());
      cstmt.setString(13, reqObj.getMerchantCatCd());
      cstmt.setString(14, reqObj.getDeviceId());
      cstmt.setString(15, reqObj.getRetRefNumber());

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Going to execute the query...");

      rs = cstmt.executeQuery();

      if (rs.next()) {
        //get the response code and description
        String respCode = rs.getString(1);
        String transId = rs.getString(2);
        String cardBal = rs.getString(3);
        String feeAmount = rs.getString(4);
        String respDesc = rs.getString(5);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Response Code ---> " + respCode
                                        +"<---Resp Desc---> " + respDesc
                                        +"<---ISO Serial---> " + transId
                                        +"<---Card Balance---> " + cardBal
                                        +"<---Fee---> " + feeAmount);

        respObj.setResponseCode(respCode);
        respObj.setResponseDesc(respDesc);
        respObj.setIsoSerialNo(transId);
        respObj.setCardBalance(cardBal);
        respObj.setFeeAmount(feeAmount);
      } //end if
      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null) rs.close();
        if (cstmt != null) cstmt.close();
      } //end try
      catch (Exception ex) {} //end catch
    } //end finally
  } //end activateCardInOltp

  public DbResponseInfoObject checkCardProgramFee(DbRequestInfoObject reqObj) throws
      Exception {
    DbResponseInfoObject respObj = new DbResponseInfoObject();
    StringBuffer query = new StringBuffer();
    CallableStatement cstmt = null;
    ResultSet rs = null;

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_CONFIG),
        "Method for checking card program Fee --- Card Prg Id--->" +
                       reqObj.getCardProgramId()
                       + "<---getServiceId--->" + reqObj.getServiceId()
                       + "<---getApplyFee--->" + reqObj.getApplyFee()
                       + "<---getAmount--->" + reqObj.getAmount());

    query.append("execute procedure check_card_prg_fee(pcard_prg_id = ?,pservice_id = ?,papply_fee = ?,pamount = ?)");

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
        LOG_CONFIG), "Executing Query -- > " + query);

    try {
      //prepare the call
      cstmt = dbConn.prepareCall(query.toString());

      cstmt.setString(1, reqObj.getCardProgramId());
      cstmt.setString(2, reqObj.getServiceId());
      cstmt.setString(3, reqObj.getApplyFee());
      cstmt.setString(4, reqObj.getAmount());

      //execute the procedure
      rs = cstmt.executeQuery();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG), "Query Executed....Looking for values...");
      if (rs.next()) {
        String response = rs.getString(1);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Response Code -- > " + response);
        //set in response object
        respObj.setResponseCode(response);

        String desc = rs.getString(2);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Response Desc -- > " + desc);
        //set in response object
        respObj.setResponseDesc(desc);

        String feeAmt = rs.getString(3);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG), "Service Fee -- > " + feeAmt);
        //set in response object
        respObj.setFeeAmount(feeAmt);
      } //end if
      return respObj;
    } //end try
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_SEVERE), "Exception -- > " + ex.getMessage());
      throw ex;
    } //end catch
    finally {
      try {
        if (rs != null)
          rs.close();
        if (cstmt != null)
          cstmt.close();
      } //end try
      catch (Exception ex) {} //end
    } //end finally
  } //end if

  protected String insertDetailRecord(String assigNo,
                           String cardNumber) {

    String detailAssignNo = null;
    StringBuffer query = new StringBuffer();
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for getting detail sales node number --- AssigNo--->" +
                                      assigNo + "<---Card No--->" +
                                      cardNumber);

      query.append("insert into card_assignment1s(assignment_no,card_no_from,card_no_to,ncards) values(?,?,?,?)");
      pstmt = dbConn.prepareStatement(query.toString());
      pstmt.setString(1, assigNo);
      pstmt.setString(2, cardNumber);
      pstmt.setString(3, cardNumber);
      pstmt.setString(4, "1");
      pstmt.executeUpdate();
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in getting detail sales node number --->" +
                                      ex);
      return null;
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
        if (pstmt != null) {
          pstmt.close();
          pstmt = null;
        }
      }
      catch (Exception ex1) {}
    }
    return detailAssignNo;
  }

  protected boolean assignCard(String stkHolderId, String cardNumber,
                               String cardPrgId,String description) {
    String salesNode = null;
    String assignNo = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for prcoessing card assignment --- Getting sales node number against the given stake holder id and card program...");
      salesNode = getSalesNodeNumber(stkHolderId, cardPrgId);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Method for prcoessing card assignment --- Got sales node number against the given stake holder id and card program--->" +
                                      salesNode);
      if (salesNode != null && salesNode.trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Method for prcoessing card assignment --- Getting assignment no by inserting new card assignment record...");
        assignNo = insertSalesRecord(stkHolderId, cardNumber, salesNode,
                                     cardPrgId,description);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Method for prcoessing card assignment --- Got assignment no by inserting new card assignment record--->" +
                                        assignNo);
        if (assignNo != null && assignNo.trim().length() > 0) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Method for prcoessing card assignment --- inserting detail record...");
          insertDetailRecord(assignNo, cardNumber);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Method for prcoessing card assignment --- Assigning the card to stakeholder...");
          updateCardRecord(cardNumber, assignNo, stkHolderId, salesNode);
          return true;
        }else {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Method for prcoessing card assignment --- No Assignment number got");
          return false;
        }
      }else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Method for prcoessing card assignment --- No Sales Node number found");
        return false;
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_CONFIG),
                                      "Exception during card assignment--->" +
                                      ex);
      return false;
    }
  }

  protected String getCardEmployerID(String cardNumber) {
    String empID = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting Card Employer ID for provided Card Number--->" +
                                      cardNumber);
      query.append("select employer_id from cards where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting Card Employer ID for provided Card Number--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ResultSet for getting Card Employer ID for provided Card Number--->" +
                                      rs);
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          empID = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Card Employer ID Got--->" +
                                          empID);
        }
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting Card Employer ID for provided Card Number--->" +
                                      ex);
    }finally {
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }
    return empID;
  }

  public String buildRefernceId(String isoSerial){
    StringBuffer refId = new StringBuffer();
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;
    String instId = null;

    int refIdLen = -1;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for building 12 digit refernce id using provided iso serial --->" +
                                      isoSerial);
      query.append("select institution_id from system_variables");

      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());

      if(rs.next()){
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0){
          instId = rs.getString(1).trim();
        }
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Instance id got--->" + instId);
      if(instId != null){
        refId.append(instId);
        refIdLen = 12 - instId.length();
      }else{
        refIdLen = 12;
      }
      if(isoSerial != null){
        refIdLen = refIdLen - isoSerial.length();
      }

      for(int i=0; i<refIdLen; i++){
        refId.append("0");
      }
      refId.append(isoSerial);
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Exception in building 12 digit refernce id using provided iso serial --->" +
                                      ex);
    }finally{
      try {
        if (rs != null) {
          rs.close();
        }
        if (stmt != null) {
          stmt.close();
        }
      }
      catch (SQLException ex1) {
      }
    }
    return refId.toString();
  }

  public String getISOSerialNo(String traceAuditNo) throws Exception {
      String isoSerial = null;
      StringBuffer query = new StringBuffer();
      Statement stmt = null;
      ResultSet rs = null;

      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                          "Method for getting ISOSerialNo for provided ISO Serial No --->" +
                                          traceAuditNo);
          if (traceAuditNo == null || traceAuditNo.trim().length() == 0) {
              return null;
          }

          query.append(
                  "select iso_serial_no from trans_requests where trace_audit_no = ");
          CommonUtilities.buildQueryInfo(query, traceAuditNo, true);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Query for getting ISOSerialNo for provided TraceAuditNO--->" +
                                          query);
          stmt = dbConn.createStatement();
          rs = stmt.executeQuery(query.toString());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "ResultSet getting ISOSerialNo for provided TraceAuditNO--->" +
                                          rs);
          if (rs.next()) {
              if (rs.getString(1) != null &&
                  rs.getString(1).trim().length() > 0) {
                  isoSerial = rs.getString(1).trim();
              } else {
                  return null;
              }
          }
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_WARNING),
                                          "Exception in getting ISOSerialNo for provided TraceAuditNO-->" +
                                          ex);
          throw ex;
      } finally {
          try {
              if (rs != null) {
                  rs.close();
                  rs = null;
              }
              if (stmt != null) {
                  stmt.close();
                  stmt = null;
              }
          } catch (SQLException ex1) {
          }
      }
      return isoSerial;
  }

  public void transferCardAlerts(String oldCardNo,String newCardNo){
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
    try {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        "Method for transferring Alerts associated with Old Card to New Card --- Old Card No--->" +
                                        oldCardNo + "<---New Card No--->" +
                                        newCardNo);
        query.append("update alert_user_params set card_no = ? where card_no = ?");
        pstmt = dbConn.prepareStatement(query.toString());
        pstmt.setString(1,newCardNo);
        pstmt.setString(2,oldCardNo);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        "Query for transferring Alerts associated with Old Card to New Card --->" + query);

        pstmt.executeUpdate();
    } catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                        "Exception in transferring Alerts associated with Old Card to New Card --->" + ex);
    }finally{
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException ex1) {
        }
    }
  }

  public void transferCardCoupons(String oldCardNo, String newCardNo) {
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for transferring Available Coupons associated with Old Card to New Card --- Old Card No--->" +
                                          oldCardNo + "<---New Card No--->" +
                                          newCardNo);
          query.append(
                  "update card_coupons set card_no = ? where card_no = ? and coupon_status = ?");
          pstmt = dbConn.prepareStatement(query.toString());
          pstmt.setString(1, newCardNo);
          pstmt.setString(2, oldCardNo);
          pstmt.setString(3, Constants.COUPONS_STATUS_AVAIL);

          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Query for transferring Available Coupons associated with Old Card to New Card --->" +
                                          query);
          pstmt.executeUpdate();
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Exception in transferring Available Coupons associated with Old Card to New Card --->" +
                                          ex);
      } finally {
          try {
              if (pstmt != null) {
                  pstmt.close();
              }
          } catch (SQLException ex1) {
          }
      }
  }

  public void updateDefaultCard(String userId, String oldDefaultCard,
                                 String newDefaultCard) {
      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;
      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Method for updating Default card no associated with user --- User Id--->" +
                                          userId + "<---Old Card No--->" +
                                          oldDefaultCard +
                                          "<---New Card No--->" +
                                          newDefaultCard);
          query.append(
                  "update users set category_ref = ? where user_id = ? and category_ref = ? ");
          pstmt = dbConn.prepareStatement(query.toString());
          pstmt.setString(1, newDefaultCard);
          pstmt.setString(2, userId);
          pstmt.setString(3, oldDefaultCard);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Query for updating Default card no associated with user--->" +
                                          query);

          pstmt.executeUpdate();
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                          "Exception in updating Default card no associated with user --->" +
                                          ex);
      } finally {
          try {
              if (pstmt != null) {
                  pstmt.close();
              }
          } catch (SQLException ex1) {
          }
      }
  }
}

