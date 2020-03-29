package com.i2c.services.registration.cardupgrade;

import com.i2c.services.registration.base.TransactionDataBaseHandler;
import com.i2c.services.util.*;
import java.sql.*;
import com.i2c.services.registration.base.TransactionRequestInfoObj;


/**
 * <p>Title:CardUpgradeDataBaseHandler: A class which provides card related information for database interaction. </p>
 * <p>Description: This class provides different service for the card upgrade such as fetching
 * the balance of the given card from the database.</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

class CardUpgradeDataBaseHandler   extends TransactionDataBaseHandler {

  private Connection dbConn = null;

   /**
   * Constructor of the class which sets the database connection for future use.
   * @param dbConn Connection
   */
  CardUpgradeDataBaseHandler(Connection dbConn) {
    super(dbConn);
    this.dbConn = dbConn;
  }

  /**
   * This method checks whether the amount which is being transfered in the given card number
   * is a valid amount. It returns true if the amount being transfered is a valid amount else
   * returns false. The method call the "getCardBalance" method to fetch the balance of the card
   * from the database.
   * @param existingCardNumber String
   * @param amount String
   * @return boolean
   */
  boolean validateTrasferAmount(String existingCardNumber, String amount) {
    boolean isValid = false;
    double transferAmount;
    double balance;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Method for checking Transfer Amount not greater than card balance --- Transfer Amount--->" +amount + "<---Card Number--->" +existingCardNumber);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Getting existing Card Balance--->" +existingCardNumber);
      String cardBalance = getCardBalance(existingCardNumber);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Got existing Card Balance--->" +cardBalance);
      if (cardBalance != null) {
        balance = Double.parseDouble(cardBalance);
        transferAmount = Double.parseDouble(amount);
        if (transferAmount <= balance) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"Transfer Amount <= Card Balance");
          return true;
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING), "Exception in getting card balance--->" +ex);
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

  public TransactionRequestInfoObj getCardHolderProfile(String cardNumber) throws
      Exception {

    TransactionRequestInfoObj profileObj = new TransactionRequestInfoObj();
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "Method for getting Card Holder Profile for card Number--->" +
                                      cardNumber);
      if (cardNumber != null) {
        query.append("select first_name1,middle_name1,last_name1,date_of_birth,");
        query.append("email,ssn_nid_no,address1,address2,state_code,city,zip_postal_code,");
        query.append("user_id,mother_maiden_nam,country_code,home_phone_no,");
        query.append("work_phone_no,gender,ofac_status,avs_status,foreign_id,");
        query.append("foreign_id_type,employer_id,bill_address1,bill_address2,");
        query.append("bill_city,bill_country_code,bill_state_code,bill_zip_code,");
        query.append("atm_ol_withd_limit,atm_of_withd_limit,pos_ol_withd_limit,");
        query.append("pos_of_withd_limit,cr_ol_withd_limit,cr_of_withd_limit,");
        query.append("max_deposit_limit,is_main_card,day_atm_dpst_max,fraud_info_flag,");
        query.append("life_high_atm_amt,life_high_pos_amt,life_high_avl_bal,min_load_amount,");
        query.append("max_load_amount,min_trans_amount,max_trans_amount,");
        query.append("earned_points,since_0_balance,primary_card_no,");
        query.append("nlost_stolens,nfree_atm_withd,nlast_declines,tot_trans_amount,tot_coms_amount,");
        query.append("nfee_reverals,last_redeemed_dt,confirmed_points,forfeited_points,redeemed_points,");
        query.append("available_points,pending_points,question_id,question_answer,ch_id ");
        query.append(",driving_license_no,driving_license_st,f_country_code");
        query.append(" from cards where card_no = ");
        CommonUtilities.buildQueryInfo(query, cardNumber, true);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                        "Query for getting Card Holder Profile for card Number--->" +
                                        query);
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(query.toString());

        if (rs.next()) {
          if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
            profileObj.setFirstName(rs.getString(1).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "FirstName--->" +
                                            profileObj.getFirstName());
          }
          if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
            profileObj.setMiddleName(rs.getString(2).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "MiddleName--->" +
                                            profileObj.getMiddleName());

          }
          if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
            profileObj.setLastName(rs.getString(3).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "LastName--->" +
                                            profileObj.getLastName());

          }
          if (rs.getString(4) != null && rs.getString(4).trim().length() > 0) {
            profileObj.setDob(rs.getString(4).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Dob--->" +
                                            profileObj.getDob());

          }
          if (rs.getString(5) != null && rs.getString(5).trim().length() > 0) {
            profileObj.setEmailAddress(rs.getString(5).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Email--->" +
                                            profileObj.getEmailAddress());

          }
          if (rs.getString(6) != null && rs.getString(6).trim().length() > 0) {
            profileObj.setSsn(rs.getString(6).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "SSN--->" +
                                            profileObj.getSsn());

          }
          if (rs.getString(7) != null && rs.getString(7).trim().length() > 0) {
            profileObj.setAddress1(rs.getString(7).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Address1--->" +
                                            profileObj.getAddress1());

          }
          if (rs.getString(8) != null && rs.getString(8).trim().length() > 0) {
            profileObj.setAddress2(rs.getString(8).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Address2--->" +
                                            profileObj.getAddress2());

          }
          if (rs.getString(9) != null && rs.getString(9).trim().length() > 0) {
            profileObj.setState(rs.getString(9).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "State--->" +
                                            profileObj.getState());

          }
          if (rs.getString(10) != null && rs.getString(10).trim().length() > 0) {
            profileObj.setCity(rs.getString(10).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "City--->" +
                                            profileObj.getCity());

          }
          if (rs.getString(11) != null && rs.getString(11).trim().length() > 0) {
            profileObj.setZip(rs.getString(11).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Zip--->" +
                                            profileObj.getZip());
          }
          if (rs.getString(12) != null && rs.getString(12).trim().length() > 0) {
            profileObj.setUserId(rs.getString(12).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "UserId--->" +
                                            profileObj.getUserId());

          }
          if (rs.getString(13) != null && rs.getString(13).trim().length() > 0) {
            profileObj.setMotherMaidenName(rs.getString(13).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "setMotherMaidenName--->" +
                                            profileObj.getMotherMaidenName());

          }
          if (rs.getString(14) != null && rs.getString(14).trim().length() > 0) {
            profileObj.setCountryCode(rs.getString(14).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getCountryCode--->" +
                                            profileObj.getCountryCode());

          }
          if (rs.getString(15) != null && rs.getString(15).trim().length() > 0) {
            profileObj.setHomePhone(rs.getString(15).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getHomePhone--->" +
                                            profileObj.getHomePhone());

          }
          if (rs.getString(16) != null && rs.getString(16).trim().length() > 0) {
            profileObj.setWorkPhone(rs.getString(16).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getWorkPhone--->" +
                                            profileObj.getWorkPhone());

          }
          if (rs.getString(17) != null && rs.getString(17).trim().length() > 0) {
            profileObj.setGender(rs.getString(17).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getGender--->" +
                                            profileObj.getGender());

          }
          if (rs.getString(18) != null && rs.getString(18).trim().length() > 0) {
            profileObj.setOfacStatus(rs.getString(18).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getOfacStatus--->" +
                                            profileObj.getOfacStatus());
          }
          if (rs.getString(19) != null && rs.getString(19).trim().length() > 0) {
            profileObj.setAvsStatus(rs.getString(19).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getAvsStatus--->" +
                                            profileObj.getAvsStatus());
          }
          if (rs.getString(20) != null && rs.getString(20).trim().length() > 0) {
            profileObj.setForeignId(rs.getString(20).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getForeignId--->" +
                                            profileObj.getForeignId());
          }
          if (rs.getString(21) != null && rs.getString(21).trim().length() > 0) {
            profileObj.setForeignIdType(rs.getString(21).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getForeignIdType--->" +
                                            profileObj.getForeignIdType());
          }
          if (rs.getString(22) != null && rs.getString(22).trim().length() > 0) {
            profileObj.setEmployerId(rs.getString(22).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getEmployerId--->" +
                                            profileObj.getEmployerId());
          }
          if (rs.getString(23) != null && rs.getString(23).trim().length() > 0) {
            profileObj.setBillingAddress1(rs.getString(23).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getBillingAddress1--->" +
                                            profileObj.getBillingAddress1());
          }
          if (rs.getString(24) != null && rs.getString(24).trim().length() > 0) {
            profileObj.setBillingAddress2(rs.getString(24).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getBillingAddress2--->" +
                                            profileObj.getBillingAddress2());
          }
          if (rs.getString(25) != null && rs.getString(25).trim().length() > 0) {
            profileObj.setBillingCity(rs.getString(25).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getBillingCity--->" +
                                            profileObj.getBillingCity());
          }
          if (rs.getString(26) != null && rs.getString(26).trim().length() > 0) {
            profileObj.setBillingCountrycode(rs.getString(26).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getBillingCountrycode--->" +
                                            profileObj.getBillingCountrycode());
          }
          if (rs.getString(27) != null && rs.getString(27).trim().length() > 0) {
            profileObj.setBillingState(rs.getString(27).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getBillingState--->" +
                                            profileObj.getBillingState());
          }
          if (rs.getString(28) != null && rs.getString(28).trim().length() > 0) {
            profileObj.setBillingZipCode(rs.getString(28).trim());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "getBillingZipCode--->" +
                                            profileObj.getBillingZipCode());
          }
          if (rs.getString(29) != null && rs.getString(29).trim().length() > 0) {
              profileObj.setAtmOnlineWithdLimit(rs.getString(29).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getAtmOflineWithdLimit--->" +
                                              profileObj.getAtmOnlineWithdLimit());
          }
          if (rs.getString(30) != null && rs.getString(30).trim().length() > 0) {
              profileObj.setAtmOflineWithdLimit(rs.getString(30).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getAtmOnlineWithdLimit--->" +
                                              profileObj.getAtmOflineWithdLimit());
          }
          if (rs.getString(31) != null && rs.getString(31).trim().length() > 0) {
              profileObj.setPosOnlineWithdLimit(rs.getString(31).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getPosOnlineWithdLimit--->" +
                                              profileObj.getPosOnlineWithdLimit());
          }
          if (rs.getString(32) != null && rs.getString(32).trim().length() > 0) {
              profileObj.setPosOflineWithdLimit(rs.getString(32).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getPosOflineWithdLimit--->" +
                                              profileObj.getPosOflineWithdLimit());
          }
          if (rs.getString(33) != null && rs.getString(33).trim().length() > 0) {
              profileObj.setCreditOnlineWithdLimit(rs.getString(33).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getCreditOnlineWithdLimit--->" +
                                              profileObj.getCreditOnlineWithdLimit());
          }
          if (rs.getString(34) != null && rs.getString(34).trim().length() > 0) {
              profileObj.setCreditOflineWithdLimit(rs.getString(34).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getCreditOflineWithdLimit--->" +
                                              profileObj.getCreditOflineWithdLimit());
          }
          if (rs.getString(35) != null && rs.getString(35).trim().length() > 0) {
              profileObj.setMaxDepositLimit(rs.getString(35).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getMaxDepositLimit--->" +
                                              profileObj.getMaxDepositLimit());
          }
          if (rs.getString(36) != null && rs.getString(36).trim().length() > 0) {
              profileObj.setIsMainCard(rs.getString(36).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getIsMainCard--->" +
                                              profileObj.getIsMainCard());
          }
          if (rs.getString(37) != null && rs.getString(37).trim().length() > 0) {
              profileObj.setDailyAtmDepositMax(rs.getString(37).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getDailyAtmDepositMax--->" +
                                              profileObj.getDailyAtmDepositMax());
          }
          if (rs.getString(38) != null && rs.getString(38).trim().length() > 0) {
              profileObj.setFraudInfoFlag(rs.getString(38).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getFraudInfoFlag--->" +
                                              profileObj.getFraudInfoFlag());
          }
          if (rs.getString(39) != null && rs.getString(39).trim().length() > 0) {
              profileObj.setLifeHighAtmAmount(rs.getString(39).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getLifeHighAtmAmount--->" +
                                              profileObj.getLifeHighAtmAmount());
          }
          if (rs.getString(40) != null && rs.getString(40).trim().length() > 0) {
              profileObj.setLifeHighPosAmount(rs.getString(40).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getLifeHighPosAmount--->" +
                                              profileObj.getLifeHighPosAmount());
          }
          if (rs.getString(41) != null && rs.getString(41).trim().length() > 0) {
              profileObj.setLifeHighAvailableBalance(rs.getString(41).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getLifeHighAvailableBalance--->" +
                                              profileObj.getLifeHighAvailableBalance());
          }
          if (rs.getString(42) != null && rs.getString(42).trim().length() > 0) {
              profileObj.setMinLoadAmount(rs.getString(42).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getMinLoadAmount--->" +
                                              profileObj.getMinLoadAmount());
          }
          if (rs.getString(43) != null && rs.getString(43).trim().length() > 0) {
              profileObj.setMaxLoadAmount(rs.getString(43).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getMaxLoadAmount--->" +
                                              profileObj.getMaxLoadAmount());
          }
          if (rs.getString(44) != null && rs.getString(44).trim().length() > 0) {
              profileObj.setMinTransAmount(rs.getString(44).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getMinTransAmount--->" +
                                              profileObj.getMinTransAmount());
          }
          if (rs.getString(45) != null && rs.getString(45).trim().length() > 0) {
              profileObj.setMaxTransAmount(rs.getString(45).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getMaxTransAmount--->" +
                                              profileObj.getMaxTransAmount());
          }
          if (rs.getString(46) != null && rs.getString(46).trim().length() > 0) {
              profileObj.setEarnedPoints(rs.getString(46).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getEarnedPoints--->" +
                                              profileObj.getEarnedPoints());
          }
          if (rs.getString(47) != null && rs.getString(47).trim().length() > 0) {
              profileObj.setSinceZeroBalance(rs.getString(47).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getSinceZeroBalance--->" +
                                              profileObj.getSinceZeroBalance());
          }
          if (rs.getString(48) != null && rs.getString(48).trim().length() > 0) {
              profileObj.setPrimaryCardNo(rs.getString(48).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getPrimaryCardNo--->" +
                                              profileObj.getPrimaryCardNo());
          }
          if (rs.getString(49) != null && rs.getString(49).trim().length() > 0) {
              profileObj.setTotalLostStolen(rs.getString(49).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getTotalLostStolen--->" +
                                              profileObj.getTotalLostStolen());
          }
          if (rs.getString(50) != null && rs.getString(50).trim().length() > 0) {
              profileObj.setTotalFreeAtmWithds(rs.getString(50).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getTotalFreeAtmWithds--->" +
                                              profileObj.getTotalFreeAtmWithds());
          }
          if (rs.getString(51) != null && rs.getString(51).trim().length() > 0) {
              profileObj.setTotalLastDeclines(rs.getString(51).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getTotalLastDeclines--->" +
                                              profileObj.getTotalLastDeclines());
          }
          if (rs.getString(52) != null && rs.getString(52).trim().length() > 0) {
              profileObj.setTotalTransAmount(rs.getString(52).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getTotalTransAmount--->" +
                                              profileObj.getTotalTransAmount());
          }
          if (rs.getString(53) != null && rs.getString(53).trim().length() > 0) {
              profileObj.setTotalCommAmount(rs.getString(53).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getTotalCommAmount--->" +
                                              profileObj.getTotalCommAmount());
          }
          if (rs.getString(54) != null && rs.getString(54).trim().length() > 0) {
              profileObj.setTotalFeeReversals(rs.getString(54).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getTotalFeeReversals--->" +
                                              profileObj.getTotalFeeReversals());
          }
          if (rs.getString(55) != null && rs.getString(55).trim().length() > 0) {
              profileObj.setLastRedeemedDate(rs.getString(55).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getLastRedeemedDate--->" +
                                              profileObj.getLastRedeemedDate());
          }
          if (rs.getString(56) != null && rs.getString(56).trim().length() > 0) {
              profileObj.setConfirmedPoints(rs.getString(56).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getConfirmedPoints--->" +
                                              profileObj.getConfirmedPoints());
          }
          if (rs.getString(57) != null && rs.getString(57).trim().length() > 0) {
              profileObj.setFortifiedPoints(rs.getString(57).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getFortifiedPoints--->" +
                                              profileObj.getFortifiedPoints());
          }
          if (rs.getString(58) != null && rs.getString(58).trim().length() > 0) {
              profileObj.setRedeemedPoints(rs.getString(58).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getRedeemedPoints--->" +
                                              profileObj.getRedeemedPoints());
          }
          if (rs.getString(59) != null && rs.getString(59).trim().length() > 0) {
              profileObj.setAvailablePoints(rs.getString(59).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getAvailablePoints--->" +
                                              profileObj.getAvailablePoints());
          }
          if (rs.getString(60) != null && rs.getString(60).trim().length() > 0) {
              profileObj.setPendingPoints(rs.getString(60).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getPendingPoints--->" +
                                              profileObj.getPendingPoints());
          }
          if (rs.getString(61) != null && rs.getString(61).trim().length() > 0) {
              profileObj.setQuestionId(rs.getString(61).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getQuestionId--->" +
                                              profileObj.getQuestionId());
          }
          if (rs.getString(62) != null && rs.getString(62).trim().length() > 0) {
              profileObj.setQuestionAnswer(rs.getString(62).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getQuestionAnswer--->" +
                                              profileObj.getQuestionAnswer());
          }
          if (rs.getString(63) != null && rs.getString(63).trim().length() > 0) {
              profileObj.setChId(rs.getString(63).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getChId--->" +
                                              profileObj.getChId());
          }
          if (rs.getString(64) != null &&
              rs.getString(64).trim().length() > 0) {
              profileObj.setDrivingLisenseNumber(rs.getString(64).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getDrivingLisenseNumber--->" +
                                              profileObj.getDrivingLisenseNumber());

          }
          if (rs.getString(65) != null && rs.getString(65).trim().length() > 0) {
              profileObj.setDrivingLisenseState(rs.getString(65).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getDrivingLisenseState--->" +
                                              profileObj.getDrivingLisenseState());
          }
          if (rs.getString(66) != null && rs.getString(66).trim().length() > 0) {
              profileObj.setForeignCountryCode(rs.getString(66).trim());
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                      LOG_CONFIG),
                                              "getForeignCountryCode--->" +
                                              profileObj.getForeignCountryCode());
          }
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting Card Holder Profile for card Number--->" +
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
    return profileObj;
  }

  public void updateCardHolderProfile(TransactionRequestInfoObj requestObj,
                                         String cardNumber) throws Exception {

      StringBuffer query = new StringBuffer();
      PreparedStatement pstmt = null;

      try {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_FINEST),
                                          "Method for Updating Card Holder Profile for card Number--->" +
                                          cardNumber);
          query.append(
            "update cards set shipping_method = ?, first_name1 = ?, middle_name1 = ? ");
          query.append(
            ", last_name1 = ?,date_of_birth = ?,email = ?,ssn_nid_no = ?,address1 = ?, ");
          query.append(
            "address2 = ?,state_code = ?,city = ?,zip_postal_code = ?,user_id = ?, ");
          query.append(
            "mother_maiden_nam = ?,country_code = ?, ");
          query.append(
            "home_phone_no = ?,work_phone_no = ?,gender = ?,ofac_status = ?");
          query.append(",avs_status = ?,foreign_id = ?,foreign_id_type = ?, ");
          query.append(
            "bill_address1 = ?,bill_address2 = ?,bill_city = ?,bill_country_code = ?");
          query.append(" ,bill_state_code = ?,bill_zip_code = ?,employer_id = ?,ch_id = ?, ");
          query.append(" driving_license_no = ?,driving_license_st = ?,f_country_code = ?,question_id = ?,question_answer = ?");
          query.append(" where card_no = ?");
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
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
          pstmt.setString(14, requestObj.getMotherMaidenName());
          pstmt.setString(15, requestObj.getCountryCode());
          pstmt.setString(16, requestObj.getHomePhone());
          pstmt.setString(17, requestObj.getWorkPhone());
          pstmt.setString(18, requestObj.getGender());
          pstmt.setString(19, requestObj.getOfacStatus());
          pstmt.setString(20, requestObj.getAvsStatus());
          pstmt.setString(21, requestObj.getForeignId());
          pstmt.setString(22, requestObj.getForeignIdType());
          pstmt.setString(23, requestObj.getBillingAddress1());
          pstmt.setString(24, requestObj.getBillingAddress2());
          pstmt.setString(25, requestObj.getBillingCity());
          pstmt.setString(26, requestObj.getBillingCountrycode());
          pstmt.setString(27, requestObj.getBillingState());
          pstmt.setString(28, requestObj.getBillingZipCode());
          pstmt.setString(29, requestObj.getEmployerId());
          pstmt.setString(30, requestObj.getChId());
          pstmt.setString(31, requestObj.getDrivingLisenseNumber());
          pstmt.setString(32, requestObj.getDrivingLisenseState());
          pstmt.setString(33, requestObj.getForeignCountryCode());
          pstmt.setString(34, requestObj.getQuestionId());
          pstmt.setString(35, requestObj.getQuestionAnswer());
          pstmt.setString(36, cardNumber);

          pstmt.executeUpdate();
      } catch (Exception ex) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_WARNING),
                                          "Exception in Updating Card Holder Profile for card Number--->" +
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
                                    String cardNumber,String description) {
    String existSalesNode = null;
    String stkHolderId = null;
    String[] existCardAssignInfo = null;
    String employerId = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for prcoessing card assignment --- card Number--->" +
                                      cardNumber + "<---Existing Card No--->" +
                                      requestObj.getExistingCard()
                                      + "<---Card Program ID--->" +
                                      requestObj.getCardPrgId() + "<---Description--->" + description);
      if (requestObj.getExistingCard() != null &&
          requestObj.getExistingCard().trim().length() > 0) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Method for prcoessing card assignment --- getting stake holder id and sales node number of existing card...");
        existCardAssignInfo = getExistingCardAssignmentInfo(requestObj.getExistingCard());
        if(existCardAssignInfo != null && existCardAssignInfo.length > 0){
          existSalesNode = existCardAssignInfo[0];
          stkHolderId = existCardAssignInfo[1];
          if(stkHolderId != null && stkHolderId.trim().length() > 0
             && existSalesNode != null && existSalesNode.trim().length() > 0){
            if(assignCard(stkHolderId,cardNumber,requestObj.getCardPrgId(),description)){
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                              "Method for prcoessing card assignment --- New card is successfully assigned...");
            }else{
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                              "Method for prcoessing card assignment --- Assignment using card acceptor failed, attempting assignment using employer...");
              employerId = getCardEmployerID(cardNumber);
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                              "Method for prcoessing card assignment --- Employer Id-->" + employerId);
              if(assignCard(employerId,cardNumber,requestObj.getCardPrgId(),description)){
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                                "Method for prcoessing card assignment --- New card is successfully assigned...");
              }else{
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                    LOG_CONFIG),
                                                "Method for prcoessing card assignment --- Assignment using employer failed,new card is not assigned...");
              }
            }
          }else{//Existing card not assigned, attempting assignment using employer
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Method for prcoessing card assignment --- Existing card not assigned, attempting assignment using employer...");
            employerId = getCardEmployerID(cardNumber);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Method for prcoessing card assignment --- Employer Id-->" +
                                            employerId);
            if (assignCard(employerId, cardNumber, requestObj.getCardPrgId(),description)) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                              "Method for prcoessing card assignment --- New card is successfully assigned...");
            }else {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                  LOG_CONFIG),
                                              "Method for prcoessing card assignment --- Assignment using employer failed,new card is not assigned...");
            }
          }
        }else{
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Method for prcoessing card assignment --- Existing card not assigned, attempting assignment using employer...");
          employerId = getCardEmployerID(cardNumber);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Method for prcoessing card assignment --- Employer Id-->" +
                                          employerId);
          if (assignCard(employerId, cardNumber, requestObj.getCardPrgId(),description)) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Method for prcoessing card assignment --- New card is successfully assigned...");
          }else {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_CONFIG),
                                            "Method for prcoessing card assignment --- Assignment using employer failed,new card is not assigned...");
          }
        }
      }else {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
            LOG_CONFIG),
                                        "Method for prcoessing card assignment --- Cannot assign card, required info missing");
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
          LOG_WARNING),
                                      "Exception in prcoessing card assignment --->" +
                                      ex);
    }
  }
}
