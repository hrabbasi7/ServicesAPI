package com.i2c.services.registration.purchaseorder;

import com.i2c.services.registration.base.InformationValidator;
import com.i2c.services.registration.base.TransactionRequestInfoObj;
import com.i2c.services.util.*;

/**
 * <p>Title: PurchaseOrderInformationValidator: A class which validates the mandatory attribute of the
 * purchase order </p>
 * <p>Description: This class validates the mandatory attributes of the card holder or purchase such as
 * validation of the first name and last name whether they are empty or null</p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
class PurchaseOrderInformationValidator
    extends InformationValidator {

  private TransactionRequestInfoObj requestInfoObj = null;

  /**
   * Constructor which initlize the request which is being processed.
   * @param requestInfoObj TransactionRequestInfoObj
   */
  PurchaseOrderInformationValidator(TransactionRequestInfoObj requestInfoObj) {
    super(requestInfoObj);
    this.requestInfoObj = requestInfoObj;
  }

  /**
   * This method validates the mandatory attributes of the card holder. It throws
   * exception if a mandatory attribute was found null or empty. The mandatory attributes are <br>
   * 1. First Name of the card holder <br>
   * 2. Last Name of the card holder  <br>
   * 3. Social Security Number of the card holder <br>
   * 4. Address of the card holder <br>
   * 5. City of the purchaser <br>
   * 6. State of the purchaser <br>
   * 7. Zip of the purchaser <br>
   * @throws Exception
   * @return boolean
   */
  protected boolean validateMandatory() throws Exception {
    try {
      super.validateMandatory();
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Validating Mandatory Attrubtes--->" +
                                      ex);
      throw ex;
    }
    return true;
  }

  boolean checkOFACAVSMandatoryFields() throws Exception {
    try {
      if(checkAllFieldsPresent()){//Perform OFAC & AVS
        return true;
      }else if(checkAllFieldsNotPresent()){//Do not perform OFAC/AVS
        return false;
      }else{
        throw new Exception("Mandatory Fields missing for performing OFAC/AVS");
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in Validating Mandatory Attrubtes--->" +
                                      ex);
      throw ex;
    }
  }

  private boolean checkAllFieldsNotPresent() {
    boolean allNotPresent = false;
    try {
      if ((requestInfoObj.getFirstName() == null || requestInfoObj.getFirstName().trim().length() == 0)
          && (requestInfoObj.getLastName() == null || requestInfoObj.getLastName().trim().length() == 0)
          && (requestInfoObj.getAddress1() == null || requestInfoObj.getAddress1().trim().length() == 0)
          && (requestInfoObj.getCity() == null || requestInfoObj.getCity().trim().length() == 0)
          && (requestInfoObj.getState() == null || requestInfoObj.getState().trim().length() == 0)
          && (requestInfoObj.getZip() == null || requestInfoObj.getZip().trim().length() == 0)) {
        allNotPresent = true;
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in checking if all mandatory fields required for OFAC&AVS are not present--->" + ex);
    }
    return allNotPresent;
  }


  private boolean checkAllFieldsPresent() {
    boolean allPresent = false;
    try {
      if (requestInfoObj.getFirstName() != null && requestInfoObj.getFirstName().trim().length() > 0
          && requestInfoObj.getLastName() != null && requestInfoObj.getLastName().trim().length() > 0
          && requestInfoObj.getAddress1() != null && requestInfoObj.getAddress1().trim().length() > 0
          && requestInfoObj.getCity() != null && requestInfoObj.getCity().trim().length() > 0
          && requestInfoObj.getState() != null && requestInfoObj.getState().trim().length() > 0
          && requestInfoObj.getZip() != null && requestInfoObj.getZip().trim().length() > 0) {
        allPresent = true;
      }
    }catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "Exception in checking if all mandatory fields required for OFAC&AVS are present--->" + ex);
    }
    return allPresent;
  }

//  private int checkConditionalFields(){
//    if(requestInfoObj.getSsn() != null && requestInfoObj.getSsn().trim().length() > 0){
//      return 1;
//    }else if(requestInfoObj.getDrivingLisenseNumber() != null  && requestInfoObj.getDrivingLisenseNumber().trim().length() > 0
//             && requestInfoObj.getDrivingLisenseState() != null  && requestInfoObj.getDrivingLisenseState().trim().length() > 0){
//      return 2;
//    }else{
//      return 0;
//    }
//  }

  int checkConditionalFields(){
    int condID = -1;
    if(requestInfoObj.getSsn() != null && requestInfoObj.getSsn().trim().length() > 0){
      return 1;
    }else if(requestInfoObj.getDrivingLisenseNumber() != null  && requestInfoObj.getDrivingLisenseNumber().trim().length() > 0
             && requestInfoObj.getDrivingLisenseState() != null  && requestInfoObj.getDrivingLisenseState().trim().length() > 0){
      return 2;
    }
    else if(checkUSForeignID()){
      return 3;
    }
    return condID;
  }

  private boolean checkUSForeignID(){
    boolean isUS = false;
    if(requestInfoObj.getCountryCode() != null && (requestInfoObj.getCountryCode().equalsIgnoreCase("US") || requestInfoObj.getCountryCode().equalsIgnoreCase("USA"))){
      if(requestInfoObj.getForeignIdType() != null && requestInfoObj.getForeignIdType().equals("1") && requestInfoObj.getForeignId() != null){//Driving Lisence Number
        return true;
      }
    }
    return isUS;
  }

}
