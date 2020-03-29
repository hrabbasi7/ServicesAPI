package com.i2c.service.solsparkservice;

import com.i2c.service.base.*;
import com.i2c.service.util.*;
import com.i2c.service.excep.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import com.i2c.services.*;
import com.i2c.solspark.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ServiceHome extends BaseHome {
  /**
   * Method for updating the Card Holder information in the cards table
   * @param infoObj: information object contains the values to be updated
   * @param dbConn: connection object for database operation
   * @throws StoreValuesExcep
   */
  public void updatePersonInformation(RequestInfoObj infoObj,Connection dbConn )
      throws StoreValuesExcep{
    StringBuffer query = new StringBuffer();
    StringBuffer mainquery = new StringBuffer(" update cards set ");
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"  Update Person Information Method Card Num -->"+infoObj.getCardNum());

      //Setting the Access Code
      if (infoObj.getNewACC() != null ) {
        query.append(" card_access_code = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getNewACC(), false);
      }//end if

      //Setting the First Name
      if (infoObj.getFirstName() != null ) {
        query.append(" first_name1 = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getFirstName(), false);
      }//end if

      //Setting the Last Name
      if (infoObj.getLastName() != null ) {
        query.append(" last_name1 = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getLastName(), false);
      }//end if

      //Setting the Gender
      if (infoObj.getSex() != null ) {
        query.append(" gender = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getSex(), false);
      }//end if

      //Setting the DOB
      if (infoObj.getDob() != null ) {
        query.append(" date_of_birth = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getDob(), false);
      }//end if

      //Setting the SSN
      if (infoObj.getSsn() != null ) {
        query.append(" ssn_nid_no = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getSsn(), false);
      }//end if

      //Setting the address1
      if (infoObj.getStreet1() != null ) {
        query.append(" address1 = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getStreet1(), false);
      }//end if

      //Setting the address2
      if (infoObj.getStreet2() != null ) {
        query.append(" address2 = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getStreet2(), false);
      }//end if

      //Setting the city
      if (infoObj.getCity() != null ) {
        query.append(" city = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getCity(), false);
      }//end if

      //Setting the state_code
      if (infoObj.getState() != null ) {
        query.append(" state_code = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getState(), false);
      }//end if

      //Setting the zip_postal_code
      if (infoObj.getPostalCode() != null ) {
        query.append(" zip_postal_code = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getPostalCode(), false);
      }//end if

      //Setting the home_phone_no
      if (infoObj.getHomePhone() != null ) {
        query.append(" home_phone_no = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getHomePhone(), false);
      }//end if

      //Setting the work_phone_no
      if (infoObj.getWorkPhone() != null ) {
        query.append(" work_phone_no = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getWorkPhone(), false);
      }//end if


      //Setting the card_access_code
      if (infoObj.getNewACC() != null ) {
        query.append(" card_access_code = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getNewACC(), false);
      }//end if

      //Setting the pin_offset
      if (infoObj.getNewPIN() != null ) {
        query.append(" pin_offset = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getNewPIN(), false);
      }//end if

      //Setting the email
      if (infoObj.getEmail() != null ) {
        query.append(" email = ");
        CommonUtilities.buildQueryInfo(query, infoObj.getEmail(), false);
      }//end if

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Values Query -->"+query);
      if (query.length() > 0 ) {
        if (query.charAt(query.length() -1) == ',')
          query.deleteCharAt(query.length() -1);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," After Deleting Values Query -->"+query);

        //Main Query
        mainquery.append(query);
        mainquery.append(" where card_no = ");
        CommonUtilities.buildQueryInfo(mainquery, infoObj.getCardNum(), true);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Final Query --->"+mainquery);
        //Save the Card Information
        this.storeValues(mainquery.toString(),dbConn);
      } else  {
        //throw new StoreValuesExcep(-1, "Unable to Store Card Information Values Query -->"+query);
      }//end else
    } catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Exception in Update Person Information -->"+ex);
      throw new StoreValuesExcep(-1, "Unable to Store Card Information Values Query -->"+ex);
    }//end catch
  }//end method

    /**
    * Method for validating the Card Holder information in the cards table
    * @param infoObj: information object contains the values to be updated
    * @param dbConn: connection object for database operation
    * @throws StoreValuesExcep
    */
   public ResponseInfoObj validateCardInformation(RequestInfoObj infoObj,Connection dbConn ) throws StoreValuesExcep
   {
     ResponseInfoObj responseObj = null;
     ResponseInfoObj statusResponse = null;
     try {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"  Validate Card Information Method Card Num -->"+infoObj.getCardNum());
         statusResponse = checkValidCardStatus(dbConn, infoObj.getCardNum());
         if(statusResponse != null && statusResponse.getRespCode().trim().length() > 0 && (statusResponse.getRespCode().equalsIgnoreCase(Constants.SUCCESS_FUNCTION_ID) || statusResponse.getRespCode().equalsIgnoreCase("PA") || statusResponse.getRespCode().equalsIgnoreCase("62"))){
           responseObj = loadCardsInforamtion(infoObj,dbConn,statusResponse.getRespCode());
         }else if(statusResponse != null && statusResponse.getRespCode().trim().length() > 0 && !statusResponse.getRespCode().equalsIgnoreCase(Constants.SUCCESS_FUNCTION_ID)){
           responseObj = new ResponseInfoObj();
           responseObj.setRespCode(statusResponse.getRespCode());
           responseObj.setSwitchResponseCode(statusResponse.getRespCode());
         }else{
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"  Unable to verfiy card status POS in allowed statuses-->");
           throw new Exception("Unable to verfiy card status POS in allowed statuses");
         }
         if(responseObj != null){
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," After Getting Success Response Object -->" +responseObj.getRespCode() +"<--- Response Audit -->" + responseObj.getAuditNo());
         }
     } catch (Exception ex) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Exception in Validate Card Information -->"+ex);
        throw new StoreValuesExcep(-1, "Unable to Validate Card Information -->"+ex);
     }//end catch
//      //If the User Information is not valid prepare the error response message
//      if (responseObj == null)
//         responseObj = prepareErrorResponse();
      return responseObj;
    }//end method


    /**
     * Prepare Error Response Message
     * @return: object containing the error response message
     */
    public ResponseInfoObj prepareErrorResponse() {
      ResponseInfoObj responceObj = new ResponseInfoObj();
      responceObj.setSwitchResponseCode("55");
      return responceObj;
    }//end method

  /**
   * Method for checking the User is valid or not
   * @param loginobj: object contains the user and device information
   * @return boolean: wheather the function successfully performed or not
   */
  public int checkValidInfo(RequestInfoObj infoObj,Connection conn)
      throws SQLException {
      int validFlag = Constants.DEFAULT_SECURITY;
      StringBuffer query = new StringBuffer();
      try {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINER),"<-------- Method for checking the Valid User Info --->"+infoObj.getCardNum()+"<--- Acccess Code --->"+infoObj.getAac());
           //Checking the User is valid
           query.append(" select user_password from users ");
           query.append(" where user_id = ");
           CommonUtilities.buildQueryInfo(query, infoObj.getCardNum(), true);
           query.append(" and is_login_ok ='Y' and is_disable ='N' ");

            //Getting the user password information
            Vector userInfo = this.getKeyValues(query.toString(),conn);

            if (userInfo != null && userInfo.size() > 0 && userInfo.elementAt(0).toString().equals(infoObj.getAac()))
              validFlag = Constants.VALID_USER;
            else
              validFlag = Constants.INVALID_USER;
      } catch(Exception e){
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Validating User Info ->"+e);
        throw new SQLException();
      } //end catch
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINER),"<--- User Validation ---->"+validFlag);
      return validFlag;
  }//end checking user exist

  /**
   * Method for checking the User is valid or not
   * @param loginobj: object contains the user and device information
   * @return boolean: wheather the function successfully performed or not
   */
  public ResponseInfoObj loadCardsInforamtion(RequestInfoObj infoObj,Connection conn, String responseCode)
      throws Exception {
      ResponseInfoObj responseObj = new ResponseInfoObj();
      StringBuffer query = new StringBuffer();
      StringBuffer responseString = new StringBuffer();
      Statement smt = null;
      ResultSet rs = null;
      try {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINER),"<-------- Method for loading the Card Information Info --->"+infoObj.getCardNum()+"<--- Acccess Code --->"+infoObj.getAac() + "<---Response Code to be returned-->"+ responseCode);
           query.append(" select card_access_code,first_name1,middle_name1,last_name1,date_of_birth,gender,ssn_nid_no ");
           query.append(" from cards ");
           query.append(" where card_no = ");
           CommonUtilities.buildQueryInfo(query, infoObj.getCardNum(), true);
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),"<-------- Getting User Info Query---->"+query);
           smt = conn.createStatement();
           rs = smt.executeQuery(query.toString());
            //Populating the User Information
           if (rs.next()) {

             responseObj.setSwitchResponseCode(responseCode);
             if (rs.getString(1) != null)
               responseString.append(rs.getString(1).trim());

              responseString.append(Constants.MESSAGE_DELIMETER_VALUE);

              if (rs.getString(2) != null)
                responseString.append(rs.getString(2).trim());

               responseString.append(Constants.MESSAGE_DELIMETER_VALUE);

               if (rs.getString(3) != null)
                 responseString.append(rs.getString(3).trim());

               responseString.append(Constants.MESSAGE_DELIMETER_VALUE);

               if (rs.getString(4) != null)
                 responseString.append(rs.getString(4).trim());

               responseString.append(Constants.MESSAGE_DELIMETER_VALUE);

               if (rs.getString(5) != null)
                 responseString.append(rs.getString(5).trim());

               responseString.append(Constants.MESSAGE_DELIMETER_VALUE);

               if (rs.getString(6) != null)
                 responseString.append(rs.getString(6).trim());

               responseString.append(Constants.MESSAGE_DELIMETER_VALUE);

               if (rs.getString(7) != null)
                 responseString.append(rs.getString(7).trim());

                 //Setting in the Response String of user information
                 responseObj.setAuditNo(responseString.toString());
            } else {//If No Card Record found
             CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"User information does not exist, empty result set returned; Setting resp code to 14--->");
             responseObj.setSwitchResponseCode(Constants.INVALID_CARD_NUMBER_RESPONSE);
            }//end else
          } catch(Exception e){
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in loading User Valid information--->"+e);
            throw new Exception("Unable to load User information during card validation--> " + e);
          } finally {
            try {
              if (rs != null)
                rs.close();
              if (smt != null)
                smt.close();
            } catch (Exception e) {}//end catch
          }//end finally
      return responseObj;
  }//end checking user exist

  /**
   * Method for getting the Response Code and Description against the Switch Response Code
   * @param responseObj: object containing the switch response information
   * @param dbConn: connection object for database operationss
   * @return: the popualted response object with response information
   */
  public ResponseInfoObj getResponseCodeDesc(ResponseInfoObj responseObj,Connection dbConn){
    StringBuffer query = new StringBuffer();
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Getting the Response Code Description Code -->" + responseObj.getSwitchResponseCode());
      query.append(" select resp_desc");
      query.append(" from iso_resp_codes ");
      query.append(" where resp_code = ");
      CommonUtilities.buildQueryInfo(query,responseObj.getSwitchResponseCode(),true);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Query for getting the Response Description --->"+query);
      //Load the information
      Vector responseInfoVec = this.getKeyValues(query.toString(),dbConn);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Response Decription inforrmation object --->"+responseInfoVec.size());
      responseObj.setRespCode(responseObj.getSwitchResponseCode());
      if (responseInfoVec.size() > 0)
      {
        String response = (String)responseInfoVec.elementAt(0);
        if (response != null && response.trim().length() >0)
        {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Response Decription  --->"+response);
          responseObj.setRespDesc(response.trim());
        }
      }
      else
      {
        //Call method to Notify the Administrator regarding the new Response Codes
        notifyAdminNewResponseCode(responseObj.getSwitchResponseCode());
        responseObj.setRespCode(responseObj.getSwitchResponseCode());
      }//end else

    } catch(Exception e){
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Getting Response Description --->"+e);
    }//end catch
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Response Description Found -->"+responseObj.getRespDesc()+"<--- Response Code -->"+responseObj.getRespCode());
    return responseObj;
  }//end method

  /**
   * Method for sending the notfication to the administaror regarding the New Response Code found
   * @param responseCode: New Response Code found
   */
  public void notifyAdminNewResponseCode(String responseCode ){
    try {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Noty Admin regarding the New Response Code  -->"+responseCode);
      if (responseCode != null && responseCode.trim().length() > 0) {
        //Send the Email
        if(!sendAdminEmail(Constants.MAIL_ADMIN_NOTIFY_MESSAGE+"\n\nResponse Code: "+responseCode,Constants.MAIL_ADMIN_NOTIFY_SUBJECT))
        {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Unable to notify Admin Regarding the New Response Code --->");
        }
      }//end if
    } catch(Throwable e){
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in Notify Admin Regarding the New Response Code --->"+e);
    }//end catch

  }//end method
  /**
    * Method for sending an email to admin
    * @return: true of false;
    */
   public boolean sendAdminEmail(String message,String subject){
     boolean flag = true;
     ServiceMail mailObj = null;
     try {
       mailObj = new ServiceMail();
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Send Admin Email Method-->"+message+"<-- Subject -->"+subject);

       //Setting the From Email Address
       mailObj.setFrom(Constants.MAIL_REPORT_FROM);
       mailObj.setMessage(message);
       mailObj.setRecipientsTO(CommonUtilities.convertStringArray(Constants.MAIL_REPORT_ADMIN,Constants.DEFAULT_DELIMETER_STRING));
       mailObj.setSMTPServer(Constants.MAIL_SMTP);
       mailObj.setSubject(subject);
       //Send an Email
       mailObj.postMail();
     } catch (Exception ex) {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING)," Exception in Send Admin Email -->"+ex);
       flag = false;
     }//end catch
     return flag;
   }//end send admin email

 /**
  * Method for generating Secret Information Like PIN and Access Code
  * based on the generate method
  * @param infoObj: cards information object
  * @param cardProgObj: card program information object
  */
 private String generateSecretInfo(String generateMethod,CardInfoObj infoObj) throws ProcessValuesExcep{
         String information = null;
         try {
           CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST)," Method for Generating secret Information generateMethod -->"+generateMethod);
           //Filter Condition
           if (generateMethod != null){
               //Calculating Expiry Date
               if (generateMethod.equalsIgnoreCase(Constants.GENERETAE_METHOD_EXPIRT_DATE) && infoObj.getExpDate() != null)
                 information = CommonUtilities.convertDateFormat("MMyy",Constants.SOLSPARK_DATE_TIME_FORMAT,infoObj.getExpDate());

               //Calculating Last 4 Card Number
               else if (generateMethod.equalsIgnoreCase(Constants.GENERETAE_METHOD_LAST_4_CARD_NUMBER) && infoObj.getPan() != null)
                 information =  infoObj.getPan().substring(infoObj.getPan().length() - 4,infoObj.getPan().length());

               //Calculating Last 4 SSN
               else if (generateMethod.equalsIgnoreCase(Constants.GENERETAE_METHOD_LAST_4_SSN) && infoObj.getSsn() != null)
                 information =  infoObj.getSsn().substring(infoObj.getSsn().length() - 4,infoObj.getSsn().length());

               //Calculating Last 4 Zip/Postal Code
               else if (generateMethod.equalsIgnoreCase(Constants.GENERETAE_METHOD_LAST_4_ZIP_POSTAL) && infoObj.getBillingZip() != null)
                 information =  infoObj.getBillingZip().substring(infoObj.getBillingZip().length() - 4,infoObj.getBillingZip().length());

               //Calculating Last 4 0
               else if (generateMethod.equalsIgnoreCase(Constants.GENERETAE_METHOD_ZERO))
                 information = "0000";
             }//end if
             CommonUtilities.getLogger().log(Level.FINEST," Final Code -->"+information);
         } catch (Exception e) {
           CommonUtilities.getLogger().log(Level.WARNING,"Exception in Generating secret Information --->"+e);
           throw new ProcessValuesExcep(-1,"Exception in Generating secret Information --->"+e);
         }//end catch
         return information;
 }//end method

 /**
  * Method for generating Secret Information Like PIN and Access Code
  * @param infoObj: cards information object
  * @param cardProgObj: card program information object
  */
 private void generatePINAccessCodeInfo(String genAcccessCodeMethod,String genPINCodeMethod,CardInfoObj infoObj,Connection dbConn) throws ProcessValuesExcep {
         String accessCode = null;
         String pinCode = null;
         StringBuffer query = new StringBuffer();
         try {
           CommonUtilities.getLogger().log(Level.FINEST,"ServiceHome --- generatePINAccessCodeInfo --- Method for Generating secret Information  Accces Code Method -->"+genAcccessCodeMethod+"<--- PIN Code -->"+genPINCodeMethod+"<--- Card Number --->"+infoObj.getPan());
             //Filter Condition
             //Generate the Access Code
             if (genAcccessCodeMethod != null && genAcccessCodeMethod.trim().length() > 0
                     && !genAcccessCodeMethod.trim().equalsIgnoreCase(Constants.GENERETAE_METHOD_NATURAL)) {
                     //Call the Method for generating Access Code
                     accessCode = generateSecretInfo(genAcccessCodeMethod,infoObj);
                     CommonUtilities.getLogger().log(Level.FINEST,"ServiceHome --- generatePINAccessCodeInfo --- Access Code To be Updated -->"+accessCode);
                     //Update Access Code information in database
                     if (accessCode != null && accessCode.trim().length() > 0) {
                         try {
                             query.append( " update cards set card_access_code  = ");
                             CommonUtilities.buildQueryInfo(query,accessCode,true);
                             query.append( " where card_no = " );
                             CommonUtilities.buildQueryInfo(query,infoObj.getPan(),true);
                             //Save the Acccess Code in the DB
                             this.storeValues(query.toString(),dbConn);
                         } catch (Exception e ){
                           CommonUtilities.getLogger().log(Level.FINEST,"ServiceHome --- generatePINAccessCodeInfo --- Exception in updating access code -->"+e);
                           throw new ProcessValuesExcep(-1,"ServiceHome --- generatePINAccessCodeInfo --- Exception in updating access code -->"+e);
                         }//end catch
                     }//end if
             }//end Access Code IF

             //Generate the Access Code
             if (genPINCodeMethod != null && genPINCodeMethod.trim().length() > 0
                     && !genPINCodeMethod.trim().equalsIgnoreCase(Constants.GENERETAE_METHOD_NATURAL)) {
                 //If same access code like PIN
                 if (genPINCodeMethod.trim().equalsIgnoreCase(Constants.GENERETAE_METHOD_SAME_AS_ACCESS_CODE))
                         pinCode = accessCode;
                 else
                   //Call the Method for generating PIN Code
                   pinCode = generateSecretInfo(genPINCodeMethod,infoObj);

                 CommonUtilities.getLogger().log(Level.FINEST,"ServiceHome --- generatePINAccessCodeInfo --- PIN Code To be Updated -->"+pinCode);
                 //Update PIN Code information in database
                 if (pinCode != null && pinCode.trim().length() > 0) {
                     try {
                         //Clear the buffer
                         query.delete(0,query.length());
                         query.append( " update cards set pin_offset  = ");
                         CommonUtilities.buildQueryInfo(query,pinCode,true);
                         query.append( " where card_no = " );
                         CommonUtilities.buildQueryInfo(query,infoObj.getPan(),true);
                         //Save the Acccess Code in the DB
                         this.storeValues(query.toString(),dbConn);
                     } catch (Exception e ){
                             CommonUtilities.getLogger().log(Level.FINEST,"ServiceHome --- generatePINAccessCodeInfo --- Exception in updating pinCode code -->"+e);
                             throw new ProcessValuesExcep(-1,"ServiceHome --- generatePINAccessCodeInfo --- Exception in updating pinCode -->"+e);
                     }//end catch
                  }//end if
                }//end PIN Code IF
         } catch (Exception e) {
           CommonUtilities.getLogger().log(Level.WARNING,"ServiceHome --- generatePINAccessCodeInfo --- Exception in Generating secret Information --->"+e);
           throw new ProcessValuesExcep(-1,"ServiceHome --- generatePINAccessCodeInfo --- Exception in Generating secret Information --->"+e);
         }//end catch
 }//end method
 public boolean validateCards(RequestInfoObj requestInfo, Connection dbConn, ServiceThread thread) throws InvalidFieldValueExcep,ProcessValuesExcep{

   boolean flag = false;
   boolean doBreak = false;
   String[] cardList = new String[3];
   int count = 0;
   RequestInfoObj cardInfo = new RequestInfoObj();

   try{
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- validateCards --- Checking the validity of Provided card numbers --- Card Number -->"+requestInfo.getCardNum()+"<--- Card Number To -->"+requestInfo.getCardNumTo()+"<--- New Card Number -->"+requestInfo.getNewCardNum());

      cardInfo.setSwitchInfo(requestInfo.getSwitchInfo());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- validateCards --- Switch Info --->" + cardInfo.getSwitchInfo());

      // Adding card number
      if (requestInfo.getCardNum() != null && requestInfo.getCardNum().trim().length() > 0){
        cardList[count ++] = requestInfo.getCardNum().trim();
      }//end if
      //Adding the TO Card Number
      if (requestInfo.getCardNumTo() != null && requestInfo.getCardNumTo().trim().length() > 0){
        cardList[count ++] = requestInfo.getCardNumTo().trim();
      }//end if
      //Adding the New Card Number
      if (requestInfo.getNewCardNum() != null && requestInfo.getNewCardNum().trim().length() > 0){
        cardList[count ++] = requestInfo.getNewCardNum().trim();
      }//end if

      if(count > 0){
        for(int i=0; i<count; i++){
          cardInfo.setCardNum(cardList[i]);
          if(checkIsCardNumbersInValid(cardInfo,dbConn)){
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- validateCards ---  Provided Card Number does not exist in local DB -->" + cardInfo.getCardNum());
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- validateCards ---  Checking the card number at switch --- Calling fetchCardAttributes method");
            Vector cardAttrList = fetchCardAttributes(cardInfo,dbConn);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- validateCards ---  Checking the card number at switch --- Card Attribute Vector Size ---> " + cardAttrList.size());

            // checking switch id
            if(cardAttrList.size() > 0){
              if(cardAttrList.elementAt(3).toString() != null && cardAttrList.elementAt(3).toString().trim().length() > 0){
                String switchID = cardAttrList.elementAt(3).toString().trim();
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- validateCards ---  Checking the card number at switch --- Switch ID found ---> " + switchID);
                if(checkBatchAllowed(switchID,dbConn)){
                  ResponseInfoObj responseObj = checkCardInformationAtSwitch(requestInfo,thread,dbConn);
                  if (responseObj == null || responseObj.getRespCode() == null || !responseObj.getRespCode().trim().equalsIgnoreCase(Constants.SUCCESS_FUNCTION_ID)){
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"ServiceHome --- validateCards ---  Unable to find the Card Infomation at Solspark -->" + requestInfo.getCardNum());
                    throw new InvalidFieldValueExcep( -1, "ServiceHome --- validateCards ---  Unable to find the Card Infomation at Solspark");
                  }// end if checking response code value
                  else{// card exist at Switch, insert information in local db
                    insertCardInformation(cardInfo,cardAttrList,responseObj,dbConn);
                  }
                }//end if checking switch allowed flag
                else{
                  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- validateCards ---  Batch Transaition not Allowed");
                  doBreak = true;
                  break;
                }
              }// end if checking switch id not null
              else{
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- validateCards ---  Switch ID not found --- Card is invalid");
                doBreak = true;
                break;
              }
            }// end if checking card attr vector size > 0
            else{
             CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- validateCards ---  No Card inforamtion found in local DB --- Card is invalid");
             doBreak = true;
             break;
            }
          }// end if checking card in local db
        }//end for
        // the loop has completed normally and card is valid
        if(!doBreak)
          flag = true;
      }// end if checking total cards count
   }// end try block
   catch(InvalidFieldValueExcep ifvex)
   {
     throw ifvex;
   }
   catch(Exception ex)
   {
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"ServiceHome --- validateCards ---  Exception in validating Cards -->" + ex);
     throw new ProcessValuesExcep(-1,"ServiceHome --- validateCards --- Exception in validating Cards -->" + ex);
   }
   return flag;
 }// end method

 /**
 * Method for checking the provided Card Numbers are valid or not
 * @param requestInfo: object containing the request Card numbers
 * @param dbConn: connection object for database operations
 * @return: cards are invalid or not
 */

 private boolean checkIsCardNumbersInValid(RequestInfoObj requestInfo,Connection dbConn) throws ProcessValuesExcep{
      // invalid = true --> (card is invalid)
      // invlaid = false --> (card is valid)
      boolean invalid = false;
      StringBuffer query = new StringBuffer();
      int rowCount = 0; //indicating number of cards present in local db
      try
      {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkIsCardNumbersInValid --- Checking the Provided card number is valid ---> " + requestInfo.getCardNum());
        query.append(" select count(*) from cards");
        query.append(" where card_no = ");

        CommonUtilities.buildQueryInfo(query,requestInfo.getCardNum(),true);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ServiceHome --- checkIsCardNumbersInValid ---  Query For Checking the Validity Of Card Number -->" + query);
        Vector countList = this.getKeyValues(query.toString(), dbConn);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ServiceHome --- checkIsCardNumbersInValid ---  Count List Size -->" + countList.size());
        if (countList.size() > 0) {
          rowCount = Integer.parseInt(countList.elementAt(0).toString());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ServiceHome --- checkIsCardNumbersInValid ---  Row count of card in table -->" + rowCount);
          if (rowCount > 0) {
            return invalid; // card number is valid, returning false
          }
          else {
            return true; // card is invalid
          }
        }// end result vector size
        else{
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING), " Query ResultSet Size is Empty, Cannot validate provided card number");
            invalid = true;// card is invalid
        }
      }// outer try
      catch(Exception e){
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"ServiceHome --- checkIsCardNumbersInValid --- Exception in Checking Is Card Number InValid -->"+e);
        throw new ProcessValuesExcep(-1,"ServiceHome --- checkIsCardNumbersInValid --- Exception in Checking Is Card Number InValid -->"+e);
      }//end catch
      return invalid;
    }//end method

 /**
  * This method fetch card attributes from local db based on card bin and return these in the form of vector
  * @param requestInfo RequestInfoObj
  * @param dbConn Connection
  * @return Vector
  */
 private Vector fetchCardAttributes(RequestInfoObj requestInfo, Connection dbConn) throws LoadKeyValuesExcep{
   Vector cardAttributeList = new Vector();
   StringBuffer query = new StringBuffer();
//   String cardBin = null;
   Statement smt = null;
   ResultSet rs = null;

  try {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "ServiceHome --- fetchCardAttributes --- fetching attributes of --->" +
                                    requestInfo.getCardNum());
    //Checking the Card Number Validity
//    cardBin = requestInfo.getCardNum().substring(0, 6);
//    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
//                                    "ServiceHome --- fetchCardAttributes ---  Card Bin Number Evalulated -->" +
//                                    cardBin);

    //Getting the Card Program Information
    //select first 1 length(card_start_nos),card_prg_id,gen_access_code,gen_pin,switch_id from card_programs where length(card_start_nos) > 0 and substr('6039491000004765', 1, length(card_start_nos)) = card_start_nos order by 1 desc

    query.append("select first 1 length(card_start_nos),card_prg_id,gen_access_code,gen_pin,switch_id from card_programs where length(card_start_nos) > 0 and substr(");
    CommonUtilities.buildQueryInfo(query, requestInfo.getCardNum(), true);
    query.append(", 1, length(card_start_nos)) = card_start_nos order by 1 desc");
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "ServiceHome --- fetchCardAttributes --- Query for getting the Card Program information -->" +
                                    query.toString());

    smt = dbConn.createStatement();
    rs = smt.executeQuery(query.toString());

    //Populating the User Information
    if (rs.next()) {
      if (rs.getString(2) != null && rs.getString(2).trim().length() > 0) {
        cardAttributeList.insertElementAt(rs.getString(2).trim(), 0); // card prg id
      }
      else{
        cardAttributeList.insertElementAt("", 0);
      }
      if (rs.getString(3) != null && rs.getString(3).trim().length() > 0) {
        cardAttributeList.insertElementAt(rs.getString(3).trim(), 1); // access code method
      }
      else{
        cardAttributeList.insertElementAt("", 1);
      }
      if (rs.getString(4) != null && rs.getString(4).trim().length() > 0) {
        cardAttributeList.insertElementAt(rs.getString(4).trim(), 2); //pin method
      }
      else{
        cardAttributeList.insertElementAt("", 2);
      }
      if (rs.getString(5) != null && rs.getString(5).trim().length() > 0) {
        cardAttributeList.insertElementAt(rs.getString(5).trim(), 3); // switch id
      }
      else{
        cardAttributeList.insertElementAt("", 3);
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "ServiceHome --- fetchCardAttributes ---  Card information got --- CardProgramID---> " +
                                    cardAttributeList.elementAt(0) +
                                    "<--- AccessCodeMethod---> " +
                                    cardAttributeList.elementAt(1) +
                                    "<--- PinMethod---> " +
                                    cardAttributeList.elementAt(2) +
                                    "<--- SwitchID---> " +
                                    cardAttributeList.elementAt(3));

    } //end resultset if
  }
  catch (Exception ex){
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ServiceHome --- fetchCardAttributes --- Exception in fetching card attributes--->" + ex);
    throw new LoadKeyValuesExcep(-1,"ServiceHome --- fetchCardAttributes --- Exception in fetching card attributes--->" + ex);
  }
  finally{
    try{
      if (rs != null)
        rs.close();
      if (smt != null)
        smt.close();
    }
    catch (Exception e) {}//end catch
  }
   return cardAttributeList;
 }

 private boolean checkBatchAllowed(String switchID, Connection dbConn) throws ProcessValuesExcep {
   boolean isAllowed = false;
   StringBuffer query = new StringBuffer();

   try {
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
         "ServiceHome --- checkBatchAllowed --- Switch ID received -->" +
                                     switchID);
     query.append(
         "select bat_trans_allowed from iso_switches where switch_id = ");
     CommonUtilities.buildQueryInfo(query, switchID, true);
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "ServiceHome --- checkBatchAllowed --- Query for checking Batch Trans Allowed flag -->" +
                                     query.toString());
     Vector result = getKeyValues(query.toString(), dbConn);
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
                                     "ServiceHome --- checkBatchAllowed --- Query Result for checking Batch Trans Allowed flag -->" +
                                     result.size());
     if (result.size() > 0) {
       String batchFlag = result.elementAt(0).toString().trim();
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG),
           "ServiceHome --- checkBatchAllowed --- Batch Trans Allowed flag Value -->" +
                                       batchFlag);
       if (batchFlag.trim().equalsIgnoreCase("Y")) {
         isAllowed = true;
       }
     }
   } // end try
   catch (Exception ex) {
     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                     "ServiceHome --- checkBatchAllowed --- Exception in checking Batch Trans Allowed flag Value -->" + ex);
     throw new ProcessValuesExcep(-1, "ServiceHome --- checkBatchAllowed --- Exception in checking Batch Trans Allowed flag Value -->" + ex);
   }
   return isAllowed;
 } //end method

 private ResponseInfoObj checkCardInformationAtSwitch(RequestInfoObj requestInfo, ServiceThread serviceThread, Connection dbConn) throws ProcessValuesExcep{

   SolsparkHandler handler=null;
   SolsparkRequestObj solsparkRequestObj=null;
   SolsparkResponseObj solsparkResponseObj=null;
   ResponseInfoObj responseObj=null;

    try {
      handler=new SolsparkHandler(dbConn);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_CONFIG), "ServiceHome --- checkCardInformationAtSwitch --- SolsparkHandler Object -->" + handler);
      solsparkRequestObj = serviceThread.mapSolsparkRequestObject(requestInfo);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkCardInformationAtSwitch ---  Calling Soslpark API updateCardHolderInfo function for checking card Existence");
      solsparkResponseObj = handler.updateCardHolderInfo(solsparkRequestObj);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkCardInformationAtSwitch ---  Returned from Soslpark API updateCardHolderInfo function for checking card Existence");
      responseObj = serviceThread.mapSolsparkResponseObject(solsparkResponseObj);
    }
    catch(Exception e) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"ServiceHome --- checkCardInformationAtSwitch --- Exception in Solspark API in checking card existence -->"+e);
      throw new ProcessValuesExcep(-1,"ServiceHome --- checkCardInformationAtSwitch --- Exception in Solspark API in checking card existence-->"+e);
    }
    return responseObj;
 }

 private boolean insertCardInformation(RequestInfoObj requestInfo, Vector cardAttrList, ResponseInfoObj responseObj, Connection dbConn) throws ProcessValuesExcep,InvalidFieldValueExcep{

   boolean flag = false;
   StringBuffer query = new StringBuffer();

  try {

    if(cardAttrList.elementAt(0).toString() != null && cardAttrList.elementAt(0).toString().trim().length() > 0){
      String cardPrgID = cardAttrList.elementAt(0).toString().trim();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ServiceHome --- insertCardInformation --- Card Number--->" + requestInfo.getCardNum() + "<---Card Program ID GOT -->" + cardPrgID + "<----ATM Status Flag---> "+ Constants.ATM_STATUS_FLAG +"<----POS Status Flag---> "+ Constants.POS_STATUS_FLAG);
      //Query for inserting the card information
      query.delete(0, query.length());
      query.append(" insert into cards(card_no,card_prg_id,card_status_atm,card_status_pos) values( ");
      CommonUtilities.buildQueryInfo(query, requestInfo.getCardNum(), false);
      CommonUtilities.buildQueryInfo(query, cardPrgID, false);
      CommonUtilities.buildQueryInfo(query, Constants.ATM_STATUS_FLAG, false);
      CommonUtilities.buildQueryInfo(query, Constants.POS_STATUS_FLAG, true);
      query.append(" )");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "ServiceHome --- insertCardInformation ---  Query for inserting the Card Information -->" +
                                      query.toString());
      //Store the Card Information
      this.storeValues(query.toString(), dbConn);

      //Saving the Account information
      String accountNum = requestInfo.getCardNum();
      if (responseObj.getAccountNum() != null && responseObj.getAccountNum().length() > 0){
        accountNum = responseObj.getAccountNum();
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ServiceHome --- insertCardInformation ---  Account Number of new Card Information -->" + accountNum);

      //Query for inserting the account information
      query.delete(0, query.length());
      query.append(" insert into card_accounts (account_number,card_acc_ser_no,card_no) values (");
      CommonUtilities.buildQueryInfo(query, accountNum, false);
      CommonUtilities.buildQueryInfo(query, "1", false);
      CommonUtilities.buildQueryInfo(query, requestInfo.getCardNum(), true);
      query.append(" )");
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants. LOG_CONFIG), "ServiceHome --- insertCardInformation --- Query for Inserting Card Account Information -->" + query);
      //Save Information
      this.storeValues(query.toString(), dbConn);

      //Calling method for generating Acccess Code and PIN
      CardInfoObj infoObj = new CardInfoObj();
      infoObj.setPan(requestInfo.getCardNum());

      String genAccessCodeMethod = cardAttrList.elementAt(1).toString();
      String genPINCodeMethod = cardAttrList.elementAt(2).toString();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ServiceHome --- insertCardInformation ---  AAC Method--->" + genAccessCodeMethod + " PIN Method -->" + genPINCodeMethod);
      //Calling the Access Code method
      generatePINAccessCodeInfo(genAccessCodeMethod, genPINCodeMethod,infoObj, dbConn);
    }
    else{
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ServiceHome --- insertCardInformation ---  Unable to find card program id for the new card");
      throw new InvalidFieldValueExcep(-1,"ServiceHome --- insertCardInformation ---  Unable to find card program id for the new card");
    }
  }// end try
  catch(InvalidFieldValueExcep ifvex){
    throw ifvex;
  }
  catch (Exception ex) {
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST), "ServiceHome --- insertCardInformation ---  Exception in inserting new Card Information in local DB-->" + ex);
    throw new ProcessValuesExcep(-1,"ServiceHome --- insertCardInformation ---  Exception in inserting new Card Information in local DB-->" + ex);
  }
   return flag;
 }// end method

 public Vector getCardPrgInfo(String cardNum, Connection dbConn){

 Vector result = null;
  StringBuffer query = new StringBuffer();
 try
 {
  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- getCardPrgName --- Card Number -->" + cardNum + "<---Connection--->" + dbConn);
  query.append("select card_prg_id, card_prg_name from card_programs where card_prg_id = (select card_prg_id from cards where card_no = ");
  CommonUtilities.buildQueryInfo(query,cardNum,true);
  query.append(")");
  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- getCardPrgName --- Query for getting card program name--->" + query.toString());
  result = this.getKeyValuePairs(query.toString(), dbConn);
  CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- getCardPrgName --- Result for getting card program name--->" + result.size());
 }
 catch (Exception ex)
 {
   CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- getCardPrgName --- Exception in getting card program name--->" + ex);
 }
  return result;
}
public String checkCardUserID(Connection dbConn, RequestInfoObj requestObj) throws ProcessValuesExcep{
  String serviceID = null;
  StringBuffer query = new StringBuffer();

  String fromOwner = null;
  String toOwner = null;

  try{
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkCardUserID --- Method for checking User ID of Card for Card to Card Transfer function's Service ID determination --- From Card-->" + requestObj.getCardNum() + "<---TO Card-->" + requestObj.getCardNumTo());
    query.append("select user_id from cards where card_no = ");
    CommonUtilities.buildQueryInfo(query,requestObj.getCardNum(),true);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkCardUserID --- Query for checking Form card userID-->" + query);
    Vector result = this.getKeyValues(query.toString(),dbConn);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkCardUserID --- Result size for checking Form card userID-->" + result.size());
    if(result.size() > 0){
      fromOwner = result.elementAt(0).toString();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkCardUserID --- Form card userID-->" + fromOwner);
    }
    query.delete(0,query.length());

    query.append("select user_id from cards where card_no = ");
    CommonUtilities.buildQueryInfo(query,requestObj.getCardNumTo(),true);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkCardUserID --- Query for checking TO card userID-->" + query);
    result = this.getKeyValues(query.toString(),dbConn);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkCardUserID --- Result size for checking TO card userID-->" + result.size());
    if(result.size() > 0){
      toOwner = result.elementAt(0).toString();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkCardUserID --- To card userID-->" + toOwner);
    }

    if(fromOwner != null && fromOwner.trim().length() > 0 && fromOwner.equals(toOwner)){
      serviceID = Constants.CARD_TO_CARD_SELF_TRNSF;
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkCardUserID --- From & To card userID same returning-->" + serviceID);
    }else{
      serviceID = Constants.CARD_TO_CARD_SHARE_FUNDS;
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkCardUserID --- From & To card userID different returning-->" + serviceID);
    }

  }catch(Exception ex){
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"ServiceHome --- checkCardUserID --- Exception in checking Card to Card User ID--->" + ex);
    throw new ProcessValuesExcep(-1,"Unable to check user ID for Card to Card Transfer---->" + ex);
  }
  return serviceID;
}

private ResponseInfoObj checkValidCardStatus(Connection dbConn, String cardNumber) throws Exception{
  ResponseInfoObj response = new ResponseInfoObj();
  StringBuffer query = new StringBuffer();
  String cardStatus = null;
  try{
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkValidCardStatus --- Method for checking card status  --- cardNumber-->" + cardNumber);
    query.append("select card_status_pos from cards where card_no = ");
    CommonUtilities.buildQueryInfo(query,cardNumber,true);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkValidCardStatus --- Method for checking card status  --- Query for getting 'card_status_pos' --->" + query);
    Vector result = this.getKeyValues(query.toString(),dbConn);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkValidCardStatus --- Method for checking card status  --- Result size of Query for getting 'card_status_pos' --->" + result.size());
    if(result.size() > 0){//handle 'empty result set'
      cardStatus = result.elementAt(0).toString();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"ServiceHome --- checkValidCardStatus --- Method for checking card status  --- Found 'card_status_pos' value --->" + cardStatus);
      if(cardStatus != null && cardStatus.trim().length() > 0){//handle 'null'
        if(cardStatus.trim().equalsIgnoreCase(Constants.PRE_ACTIVE_CARD_STATUS)){//-->A
          response.setRespCode("PA");
        }else if(cardStatus.trim().equalsIgnoreCase(Constants.ACTIVE_CARD_STATUS)){//-->B
          response.setRespCode("00");
        }else if(cardStatus.trim().equalsIgnoreCase(Constants.LOST_CARD_STATUS)){//-->C
          response.setRespCode("41");
        }else if(cardStatus.trim().equalsIgnoreCase(Constants.STOLEN_CARD_STATUS)){//-->D
          response.setRespCode("43");
        }else if(cardStatus.trim().equalsIgnoreCase(Constants.RESTRICTED_CARD_STATUS)){//-->E
          response.setRespCode("62");
        }else if(cardStatus.trim().equalsIgnoreCase(Constants.CLOSED_CARD_STATUS)){//-->F
          response.setRespCode("SD");
        }else if(cardStatus.trim().equalsIgnoreCase(Constants.LOST_NOT_CAPT_CARD_STATUS)){//-->G
          response.setRespCode("41");
        }else if(cardStatus.trim().equalsIgnoreCase(Constants.STOLEN_NOT_CAPT_CARD_STATUS)){//-->H
          response.setRespCode("43");
        }else if(cardStatus.trim().equalsIgnoreCase(Constants.INACTIVE_CARD_STATUS)){//-->I
          response.setRespCode("SA");
        }else if(cardStatus.trim().equalsIgnoreCase(Constants.REISSUE_CARD_STATUS)){//-->R
          response.setRespCode("SD");
        }
      }else{
        response.setRespCode("01");
      }
    }else{
      response.setRespCode("14");
    }
  }catch(Exception ex){
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"ServiceHome --- checkValidCardStatus --- Method for checking card status --- Exception in checking card status --->"  + ex);
    throw new Exception("Unable to check card status--->" + ex);
  }
  return response;
}

public ResponseInfoObj getC2CAmountParamterValue(Connection dbConn, String cardNumber) throws Exception{
  ResponseInfoObj response = new ResponseInfoObj();
  StringBuffer query = new StringBuffer();
  CallableStatement cs = null;
  ResultSet rs = null;
  try{
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "ServiceHome --- getC2CAmountParamterValue --- Method for getting C2C Amount Paramter Value  --- cardNumber-->" + cardNumber);
    cs = dbConn.prepareCall("{call c2c_transfer_limit(?)}");
    cs.setString(1,cardNumber);
    rs = cs.executeQuery();
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "ServiceHome --- getC2CAmountParamterValue --- Calling DB API c2c_transfer_limits passing cardNumber-->" + cardNumber);
    if(rs.next()){
      response.setRespCode(rs.getString(1));
      response.setSwitchResponseCode(rs.getString(1));
      response.setAuditNo(rs.getString(2).trim());
      response.setFeeAmount(rs.getString(3).trim());
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                "ServiceHome --- getC2CAmountParamterValue --- Method for getting C2C Amount Paramter Value  --- Response --->" +
                                "<--Resp Code-->" + response.getRespCode() +
                                "<--Min Val-->" + response.getAuditNo()+
                                "<--Max Val-->" + response.getFeeAmount());
    }else{
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                "ServiceHome --- getC2CAmountParamterValue --- No response received from DB API for C2C parameters -- returning 06 --->" );
      response.setRespCode("06");
      response.setSwitchResponseCode("06");
    }

  }catch(Exception ex){
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),
                                      "ServiceHome --- getC2CAmountParamterValue --- Exception in getting C2C paramter values---> "  +ex);
      throw new Exception("Unable to get C2C parameter values--->" + ex);
  }finally{
    try {
          if (rs != null) {
            rs.close();
          }
          if (cs != null) {
            cs.close();
          }
        }
        catch (Exception ex1) {
        }
  }
  return response;
}//end method

}// end class

