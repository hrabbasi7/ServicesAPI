package com.i2c.services.registration.cardupgrade;

import com.i2c.services.registration.base.InformationValidator;
import com.i2c.services.registration.base.TransactionRequestInfoObj;
import com.i2c.services.util.*;

/**
 * <p>Title: CardUpgradeInformationValidator: A class which validates the mandatory attributes of the card </p>
 * <p>Description: Validates mandatory attributes of card contained in the request object. For
 * example whether the given card program is a valid card program and it exists in the database</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */


public class CardUpgradeInformationValidator extends InformationValidator{

  private TransactionRequestInfoObj requestInfoObj = null; // hold information which is being validated.

  /**
   * Constructor of the class. Calls the super class constructor and sets request information object attribute.
   * @param requestInfoObj TransactionRequestInfoObj
   */
  public CardUpgradeInformationValidator(TransactionRequestInfoObj requestInfoObj) {
    super(requestInfoObj);
    this.requestInfoObj = requestInfoObj;
  }

  /**
   * A method which validates the mandatory attributes of the card such as card no. If the card no
   * was found invalid then this method throws exception which describes the reason of the error
   * in detail. In case of successful validation it returns true which means that the mandatory
   * attributes have the valid values and processing can be continued.
   * @throws Exception
   * @return boolean
   */

  protected boolean validateMandatory() throws Exception{
    try {
      super.validateMandatory();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Validating Mandatory Attrubtes" );
      if(requestInfoObj.getExistingCard() == null || requestInfoObj.getExistingCard().trim().length() == 0){
        throw new Exception("Mandatory Field Missing---Existing Card");
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Validating Mandatory Attrubtes--->" + ex);
      throw ex;
    }
    return true;
  }

  /**
   * This method matches the old card program with the new card program, if they were found diffenet then it
   * returns true else it returns false. In case of any error during processing it returns false which
   * means either the card programs are not valid or there was an error while validating the
   * card programs.
   * @param existingCardPrgID String
   * @param newCardPrgID String
   * @return boolean
   */
  boolean validateCardPrograms(String existingCardPrgID, String newCardPrgID){
    boolean areDifferent = false;
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                      "Method for Validating Card Programs --- Existing Card Program--->" + existingCardPrgID + "<---newCardPrgID-->" + newCardPrgID);
    try {
      if (!existingCardPrgID.equalsIgnoreCase(newCardPrgID)) {
        return true;
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Validating Card Programs--->" + ex);
    }
    return areDifferent;
  }

  /**
   * This method compares the old switch information with the new switch. If both i.e. the old and new
   * switch have the same identity then this method returns true else it retuns flase.
   * When this method returns true then this means that switches have been validated successfully
   * In other case it returns false which means that the two switches are not identical.
   * @param oldCardPrgSwitch String
   * @param newCardPrgSwitch String
   * @return boolean
   */

  boolean validateSwitches(String oldCardPrgSwitch, String newCardPrgSwitch){
    boolean areSame = false;
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Method for Validating Switches for both Card programs  --- Old Card Program Switch--->" + oldCardPrgSwitch + "<---New Card Prg Switch-->" + newCardPrgSwitch);
    try {
      if (oldCardPrgSwitch.equalsIgnoreCase(newCardPrgSwitch)) {
        return true;
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Validating Switches for both Card programs--->" + ex);
    }
    return areSame;
  }

}
