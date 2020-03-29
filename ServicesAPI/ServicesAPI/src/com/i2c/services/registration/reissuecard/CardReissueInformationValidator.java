package com.i2c.services.registration.reissuecard;

import com.i2c.services.registration.base.InformationValidator;
import com.i2c.services.registration.base.TransactionRequestInfoObj;
import com.i2c.services.util.*;

/**
 * <p>Title: This class validates the card infomration. </p>
 * <p>Description: This class performs validation of the given card attributes such as device type
 * and reissue type validation.</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class CardReissueInformationValidator extends InformationValidator {

  private TransactionRequestInfoObj requestInfoObj = null;

  public CardReissueInformationValidator(TransactionRequestInfoObj
                                         requestInfoObj) {
    super(requestInfoObj);
    this.requestInfoObj = requestInfoObj;
  }

  /**
   * This method performs the validation of the device type and card re-issue type. It throws exception
   * in case if these attributes were found null or empty or invalid reissue.
   * @throws Exception
   * @return boolean
   */

  protected boolean validateMandatory() throws Exception {
    try {
      if (requestInfoObj.getDeviceType() == null ||
          requestInfoObj.getDeviceType().trim().length() == 0) {
        throw new Exception("Mandatory Field Missig---Device Type");
      }
      if(requestInfoObj.getExistingCard() == null || requestInfoObj.getExistingCard().trim().length() == 0){
        throw new Exception("Mandatory Field Missing---Existing Card");
      }
      if(requestInfoObj.getReissueType() == null || requestInfoObj.getReissueType().trim().length() == 0){
        throw new Exception("Mandatory Field Missing---Reissue Type");
      }
      if(!requestInfoObj.getReissueType().equals(Constants.REISSUE_SAME_CARD_SAME_INFO)
         && !requestInfoObj.getReissueType().equals(Constants.REISSUE_NEW_CARD_SAME_INFO)
         && !requestInfoObj.getReissueType().equals(Constants.REISSUE_SAME_CARD_UPDATE_EXPIRY)){
        throw new Exception("Invalid Field --- Reissue Type");
      }
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Validating Mandatory Attrubtes--->" +
                                      ex);
      throw ex;
    }
    return true;
  }

}
