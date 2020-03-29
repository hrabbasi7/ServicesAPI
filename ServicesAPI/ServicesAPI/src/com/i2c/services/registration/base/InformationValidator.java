package com.i2c.services.registration.base;

import com.i2c.services.util.*;

/**
  * <p>Title: Information Validator: This class is used to validate the card information </p>
 * <p>Description: The class provides the services for validation of the card attributes such
 * as to check the card no whether it empty or null</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */

 public class InformationValidator {

   private TransactionRequestInfoObj requestInfoObj = null;

  protected InformationValidator(TransactionRequestInfoObj requestInfoObj) {
    this.requestInfoObj = requestInfoObj;
  }

  /**
   * This method validates the card program id and device type if these are empty or null then it throws
   * exception. If the information is valid then it returns true else false.
   * @throws Exception
   * @return boolean
   */
  protected boolean validateMandatory() throws Exception{

    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
          "Method for Validating Mandatory Attrubtes" );
      if (requestInfoObj.getCardPrgId() == null ||
          requestInfoObj.getCardPrgId().trim().length() == 0) {
        throw new Exception("Mandatory Field Missig---Card Program ID");
      }
      if (requestInfoObj.getDeviceType() == null ||
          requestInfoObj.getDeviceType().trim().length() == 0) {
        throw new Exception("Mandatory Field Missig---Device Type");
      }
      return true;
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
          "Exception in Validating Mandatory Attrubtes" );
      throw ex;
    }
  }
}
