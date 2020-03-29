package com.i2c.services.registration.cardupgrade;

import com.i2c.services.registration.base.InformationValidator;
import com.i2c.services.registration.base.TransactionRequestInfoObj;
import com.i2c.services.util.*;

/**
 * <p>Title: CardUpgradeWithAmountTransferInformationValidtor. A class which validates the attribute of card </p>
 * <p>Description: This class validates the attributes of the card such as validating the transfer amount,
 * card program and the switch information.</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class CardUpgradeWithAmountTransferInformationValidator extends InformationValidator {

  private TransactionRequestInfoObj requestInfoObj = null;

  /**
   * Constructor for the class
   * @param requestInfoObj TransactionRequestInfoObj
   */
  public CardUpgradeWithAmountTransferInformationValidator(TransactionRequestInfoObj requestInfoObj) {
    super(requestInfoObj);
    this.requestInfoObj = requestInfoObj;
  }



  /**
   * A method which validates the mandatory attributes of the card such as card no and transfer amount.
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
      if(requestInfoObj.getTransferAmount() == null || requestInfoObj.getTransferAmount().trim().length() == 0){
        throw new Exception("Mandatory Field Missing---Transfer Amount");
      }
      if(Double.parseDouble(requestInfoObj.getTransferAmount()) < 0){
        throw new Exception("Invalid Field --- Got Negative Transfer Amount");
      }
    }catch(NumberFormatException nfex){
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Invalid Field--->" + nfex);
      throw new Exception("Invalid Field--->" + nfex.getMessage());
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Validating Mandatory Attrubtes--->" + ex);
      throw ex;
    }
    return true;
  }

  /**
   * This method matches the old card program with the new card program, if they were found diffenet then
   * it return true else it returns false.
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
   * This method matches the new card program switch with the existing (old) card program switch, if
   * they were found same then it  return true else it returns false.
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


