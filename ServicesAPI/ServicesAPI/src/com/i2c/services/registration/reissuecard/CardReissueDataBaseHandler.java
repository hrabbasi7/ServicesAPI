package com.i2c.services.registration.reissuecard;

import com.i2c.services.registration.base.TransactionDataBaseHandler;
import com.i2c.services.util.*;
import java.sql.*;
import com.i2c.services.registration.base.TransactionRequestInfoObj;
/**
 * <p>Title: CardReissueDataBaseHandler: A class which provides the functions for reissue of card </p>
 * <p>Description: This class performs database related operation for reissuance of card such as adding
 * new card information into the database</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class CardReissueDataBaseHandler  extends TransactionDataBaseHandler {

  private Connection dbConn = null;

  /**
   * Constructor which initlized the database connection for future use.
   * @param dbConn Connection
   */
  public CardReissueDataBaseHandler(Connection dbConn) {
    super(dbConn);
    this.dbConn = dbConn;
  }

  /**
   * This method updates the last reissue date of the given card no.
   * @param cardNumber String
   */

  void updateLastReissueDate(String cardNumber) {
    StringBuffer query = new StringBuffer();
    Statement stmt = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for updateing last reissue on date for card--->" +
                                      cardNumber);
      query.append("update cards set last_reissue_on = ");
      CommonUtilities.buildQueryInfo(query,
                                     CommonUtilities.
                                     getCurrentFormatDate(Constants.DATE_FORMAT), true);
      query.append(" where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for updating last reissue on date for card--->" +
                                      query);
      stmt = dbConn.createStatement();
      stmt.executeUpdate(query.toString());
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in updating last reissue on date for card--->" +
                                      ex);
    }
    finally {
      try {
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
   * This method is used to update the status of the given card no.
   * @param cardNumber String
   */
  void updateCardStatus(String cardNumber) {
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for updating card status to 'A' for card--->" +
                                      cardNumber);
      query.append("update cards set card_status = ");
      CommonUtilities.buildQueryInfo(query, Constants.PRE_ACTIVE_CARD, true);
      query.append(" where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for updating card status to 'A' for card--->" +
                                      query);
      stmt = dbConn.createStatement();
      stmt.executeUpdate(query.toString());
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in updating card status to 'A' for card--->" +
                                      ex);
    }
    finally {
      try {
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
   * This method is used to update the ATM and POS status of the given card to pre-active.
   * @param cardNumber String
   */

  void updateCardAtmPosStatus(String cardNumber) {
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for updating card status atm, pos to PRE-ACTIVE for card--->" +
                                      cardNumber);
      query.append("update cards set card_status_atm = ");
      CommonUtilities.buildQueryInfo(query, Constants.PRE_ACTIVE_CARD, false);
      query.append(" card_status_pos = ");
      CommonUtilities.buildQueryInfo(query, Constants.PRE_ACTIVE_CARD, true);
      query.append(" where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for updating card status atm, pos to PRE-ACTIVE for card--->" +
                                      query);
      stmt = dbConn.createStatement();
      stmt.executeUpdate(query.toString());
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in updating card status atm, pos to PRE-ACTIVE for card--->" +
                                      ex);
    }
    finally {
      try {
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
   * This method is used to get the expiration months for the given card program such as number of
   * months of expiration of card program ABC.
   * @param cardPrgID String
   * @return String
   */

  String getCardExpirationMonths(String cardPrgID) {
    String expiryMonths = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for getting exipiration months for card program--->" +
                                      cardPrgID);
      query.append(
          "select expiration_months from card_programs where card_prg_id = ");
      CommonUtilities.buildQueryInfo(query, cardPrgID, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting exipiration months for card program--->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ResultSet getting exipiration months for card program--->" +
                                      rs);
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          expiryMonths = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Expiry Months Found--->" +
                                          expiryMonths);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting exipiration months for card program--->" +
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
    return expiryMonths;
  }


  /**
   * This method returns the expiry date of the given card no. It uses the database table cards
   * to find the expiry date of the given card no.
   * @param cardNumber String
   * @return String
   */
  String getCurrentExpiruDate(String cardNumber) {
    String expiryDate = null;
    StringBuffer query = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for getting current exipiry date for card --->" +
                                      cardNumber);
      query.append(
          "select expiry_on from cards where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for getting current exipiry date for card --->" +
                                      query);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query.toString());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "ResultSet getting current exipiry date for card --->" +
                                      rs);
      if (rs.next()) {
        if (rs.getString(1) != null && rs.getString(1).trim().length() > 0) {
          expiryDate = rs.getString(1).trim();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_CONFIG),
                                          "Expiry Date Found--->" +
                                          expiryDate);
        }
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in getting current exipiry date for card --->" +
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
    return expiryDate;
  }


  /**
   * This method updates (sets new date) the expiry date of the given card no.
   * @param cardNumber String
   * @param expiryDate String
   */

  void updateCardExpiryDate(String cardNumber, String expiryDate) {

    StringBuffer query = new StringBuffer();
    Statement stmt = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for updating Expiry Date for card--->" +
                                      cardNumber);
      query.append("update cards set expiry_on = ");
      CommonUtilities.buildQueryInfo(query, expiryDate, true);
      query.append(" where card_no = ");
      CommonUtilities.buildQueryInfo(query, cardNumber, true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for updating Expiry Date for card--->" +
                                      query);
      stmt = dbConn.createStatement();
      stmt.executeUpdate(query.toString());
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in updating Expiry Date for card--->" +
                                      ex);
    }
    finally {
      try {
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
   * This method updates the card staus and last reissue date of the given card no. It uses the
   * database table card to update the card information.
   * @param cardNumber String
   */
  void updateCardAttrForReissueType1(String cardNumber) throws Exception{
    StringBuffer query = new StringBuffer();
    PreparedStatement stmt = null;
    String newReissueDate = null;
    try {

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for updating card attributes for Reissue Type-1 --- Getting new Reissue Date");
      newReissueDate = CommonUtilities.getCurrentFormatDate(Constants.
          DATE_FORMAT);

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for updating card attributes for Reissue Type-1 ---  Card Status ---> " +
                                      Constants.PRE_ACTIVE_CARD +
                                      "<---Last Reissue Date--->" +
                                      newReissueDate +
                                      "<---For Card Number--->" +
                                      cardNumber);
      query.append("update cards set card_status = ?,");
      query.append(" last_reissue_on = ?, track_batch_no = null,card_gen_mode = ?");
      query.append(" where card_no = ?");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for updating card attributes for Reissue Type-1--->" +
                                      query);
      stmt = dbConn.prepareStatement(query.toString());
      stmt.setString(1, Constants.PRE_ACTIVE_CARD);
      stmt.setString(2, newReissueDate);
      stmt.setString(3, Constants.NEW_CARDGEN_REISSUE);
      stmt.setString(4, cardNumber);
      stmt.executeUpdate();
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in updating card attributes for Reissue Type-1--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
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
   * This method updates the card status and the expiry date of the given card no.
   * @param cardNumber String
   * @param newExpiryDate String
   */

  void updateCardAttrForReissueType2(String cardNumber,String newExpiryDate) throws Exception{
    StringBuffer query = new StringBuffer();
    PreparedStatement stmt = null;
    try {


      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for updating card attributes for Reissue Type-2 --- Card Status ---> " +
                                      Constants.PRE_ACTIVE_CARD +
                                      "<---Expiry Date--->" +
                                      newExpiryDate +
                                      "<---For Card Number--->" +
                                      cardNumber);
      query.append("update cards set card_status = ?,");
      query.append(" expiry_on = ?, track_batch_no = null,card_gen_mode = ?");
      query.append(" where card_no = ?");

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Query for updating card attributes for Reissue Type-2--->" +
                                      query);
      stmt = dbConn.prepareStatement(query.toString());
      stmt.setString(1, Constants.PRE_ACTIVE_CARD);
      stmt.setString(2, newExpiryDate);
      stmt.setString(3, Constants.NEW_CARDGEN_REISSUE);
      stmt.setString(4, cardNumber);
      stmt.executeUpdate();
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in updating card attributes for Reissue Type-2--->" +
                                      ex);
      throw ex;
    }
    finally {
      try {
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException ex1) {
      }
    }

  }

  public void processCardAssignment(TransactionRequestInfoObj requestObj,
                                    String cardNumber,String description) {
    String existSalesNode = null;
    String assignNo = null;

    String stkHolderId = null;
    String[] existCardAssignInfo = null;
    String employerId = null;

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for prcoessing card assignment --- card Number--->" +
                                      cardNumber + "<---Existing Card No--->" +
                                      requestObj.getExistingCard()
                                      + "<---Card Program ID--->" +
                                      requestObj.getCardPrgId() + "<--Description-->" + description);
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
            return;
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
