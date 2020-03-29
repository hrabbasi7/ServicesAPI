package com.i2c.campaigngenservice.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

import com.i2c.campaigngenservice.excep.*;
//import com.i2c.campaigngenservice.util.*;
import com.i2c.campaigngenservice.util.CommonUtilities;
import com.i2c.campaigngenservice.util.DatabaseHandler;
import com.i2c.campaigngenservice.vo.*;
import com.i2c.utils.logging.I2cLogger;


/**
 * <p>Title: Campaign Generation API</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Innovative Pvt. Ltd.</p>
 *
 * @author Haroon ur Rashid Abbasi
 * @version 1.0
 */
public class CampaignDAO extends BaseDAO {
    private String instanceID = null;
    private Connection dbConn = null;
//    private Logger lgr = null;


    public CampaignDAO(String instanceID, Connection dbConn, Logger lgrPayeesDAO) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
        lgr = lgrPayeesDAO;
    }

    public CampaignDAO(String instanceID, Connection dbConn) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
    }

    public CampaignDAO(String instanceID) {
        this.instanceID = instanceID;
        try {
            dbConn = DatabaseHandler.getConnection("CampaignDAO", instanceID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public CampaignsVO getCampaigns(int campaignId)  {
        lgr.log(I2cLogger.FINE, "<---- START method getCampaings(campaignId) ---->");

        CampaignsVO campaignVo = new CampaignsVO();
        StringBuffer CAMPAIGN_SELECT_QUERY = new StringBuffer();

        CAMPAIGN_SELECT_QUERY.append("SELECT campaign_id, campaign_name, campaign_desc, creation_date, creation_time, creator_user_id, status_id,");
        CAMPAIGN_SELECT_QUERY.append(" recipient_flag, business_days_flag, subject, freq_type, freq_map, schd_start_date, schd_time_slot_id, reminders,");
        CAMPAIGN_SELECT_QUERY.append(" reminder_interval_unit, reminder_interval_nunits, termination_basis, termination_date, termination_freq, is_for_email,");
        CAMPAIGN_SELECT_QUERY.append(" is_for_sms, is_for_fax, is_for_ivr, is_for_website, message_body, sms_message, attached_file_id,");
        CAMPAIGN_SELECT_QUERY.append(" ivr_file_id, coupon_program_id, is_modified, schd_next_exe_dtime ");
        CAMPAIGN_SELECT_QUERY.append(" FROM campaigns WHERE campaign_id = ? ");


        PreparedStatement stmt = null ;
        ResultSet rs = null ;
        try {
            stmt = dbConn.prepareStatement(CAMPAIGN_SELECT_QUERY.toString());
            // set the Campaign ID value in the query
            stmt.setInt(1, campaignId);

            lgr.log(I2cLogger.FINEST, "Campaign Id-- > " + campaignId);

            rs = stmt.executeQuery();// execute the query
            // get the values from result set and set in Value Object
            while (rs.next()){
                campaignVo.setCampaignId(rs.getInt("campaign_id"));
                campaignVo.setCampaignName(rs.getString("campaign_name"));
                campaignVo.setCampaignDesc(rs.getString("campaign_desc"));
                campaignVo.setCampCreationDate(rs.getDate("creation_date"));
                campaignVo.setCampCreationTime(rs.getDate("creation_time"));
                campaignVo.setCampCreatorUserId(rs.getString("creator_user_id"));
                campaignVo.setCampStatusId(rs.getString("status_id"));
                campaignVo.setCampRecipientFlag(rs.getString("recipient_flag"));
                campaignVo.setCampBusinessDaysFlag(rs.getString("business_days_flag"));
                campaignVo.setCampSubject(rs.getString("subject"));
                campaignVo.setCampFreqType(rs.getString("freq_type"));
                campaignVo.setCampFreqMap(rs.getString("freq_map"));
                campaignVo.setCampSchdStartDate(rs.getDate("schd_start_date"));
                campaignVo.setCampSchdTimeSlot(rs.getString("schd_time_slot_id"));
                campaignVo.setCampReminders(rs.getInt("reminders"));
                campaignVo.setCampReminderIntervalUnit(rs.getString("reminder_interval_unit"));
                campaignVo.setCampReminderIntervalNunits(rs.getString("reminder_interval_nunits"));
                campaignVo.setCampTerminiationBasis(rs.getString("termination_basis"));
                campaignVo.setCampTerminiationDate(rs.getDate("termination_date"));
                campaignVo.setCampTerminiationFreq(rs.getInt("termination_freq"));
                campaignVo.setCampIsForEmail(rs.getString("is_for_email"));
                campaignVo.setCampIsForSms(rs.getString("is_for_sms"));
                campaignVo.setCampIsForFax(rs.getString("is_for_fax"));
                campaignVo.setCampIsForIvr(rs.getString("is_for_ivr"));
                campaignVo.setCampIsForWebsite(rs.getString("is_for_website"));
                campaignVo.setCampMessageBody(rs.getString("message_body"));
                campaignVo.setCampSmsMessage(rs.getString("sms_message"));
                campaignVo.setCampAttachFileId(rs.getInt("attached_file_id"));
                campaignVo.setCampAttachFileIvrId(rs.getInt("ivr_file_id"));
//                campaignVo.setCampAttachDataFileName(rs.getString("attached_data_file_name"));
//                campaignVo.setCampAttachFileData(rs.getBytes("attached_file_data"));
//                campaignVo.setCampAttachFileIvr(rs.getBytes("attached_file_ivr"));
//                campaignVo.setCampAttachIvrFileName(rs.getString("attached_ivr_file_name"));
                campaignVo.setCampCouponProgId(rs.getString("coupon_program_id"));
                campaignVo.setCampIsModified(rs.getString("is_modified"));
                campaignVo.setCampSchdNextDtime(rs.getDate("schd_next_exe_dtime"));
            }

        } catch (SQLException ex) {
            lgr.log(I2cLogger.WARNING, "Exception in method getCampaigns-- > " + ex.getMessage());
            lgr.log(I2cLogger.WARNING, "Stack Tracke of getCampaings --> " +
                    CommonUtilities.getStackTrace(ex));
        }finally{
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {

            }
        }
        return campaignVo;
    }





    public CampaignsVO getCampaignInsts (int campaignId)  {
            lgr.log(I2cLogger.FINE, "<---- START method getCampaignInsts(campaignId) ---->");

            CampaignsVO campaignVo = new CampaignsVO();
            StringBuffer CAMPAIGN_SELECT_QUERY = new StringBuffer();

            CAMPAIGN_SELECT_QUERY.append("SELECT campaign_id, campaign_inst_id, campaign_name, campaign_desc, creation_date, creation_time, creator_user_id, status_id,");
            CAMPAIGN_SELECT_QUERY.append(" recipient_flag, business_days_flag, subject, freq_type, freq_map, schd_start_date, schd_time_slot_id, reminders,");
            CAMPAIGN_SELECT_QUERY.append(" reminder_interval_unit, reminder_interval_nunits, termination_basis, termination_date, termination_freq, is_for_email,");
            CAMPAIGN_SELECT_QUERY.append(" is_for_sms, is_for_fax, is_for_ivr, is_for_website, message_body, sms_message, attached_file_id,");
            CAMPAIGN_SELECT_QUERY.append(" ivr_file_id, coupon_program_id, is_modified, schd_next_exe_dtime, reminders_done ");
            CAMPAIGN_SELECT_QUERY.append(" FROM campaign_insts WHERE campaign_id = ? ");


            PreparedStatement stmt = null ;
            ResultSet rs = null ;
            try {
                stmt = dbConn.prepareStatement(CAMPAIGN_SELECT_QUERY.toString());
                // set the Campaign ID value in the query
                stmt.setInt(1, campaignId);

                lgr.log(I2cLogger.FINEST, "Campaign Id-- > " + campaignId);

                rs = stmt.executeQuery();// execute the query
                // get the values from result set and set in Value Object
                while (rs.next()){
                    campaignVo.setCampaignId(rs.getInt("campaign_id"));
                    campaignVo.setCampaignInstId(rs.getInt("campaign_inst_id"));
                    campaignVo.setCampaignName(rs.getString("campaign_name"));
                    campaignVo.setCampaignDesc(rs.getString("campaign_desc"));
                    campaignVo.setCampCreationDate(rs.getDate("creation_date"));
                    campaignVo.setCampCreationTime(rs.getDate("creation_time"));
                    campaignVo.setCampCreatorUserId(rs.getString("creator_user_id"));
                    campaignVo.setCampStatusId(rs.getString("status_id"));
                    campaignVo.setCampRecipientFlag(rs.getString("recipient_flag"));
                    campaignVo.setCampBusinessDaysFlag(rs.getString("business_days_flag"));
                    campaignVo.setCampSubject(rs.getString("subject"));
                    campaignVo.setCampFreqType(rs.getString("freq_type"));
                    campaignVo.setCampFreqMap(rs.getString("freq_map"));
                    campaignVo.setCampSchdStartDate(rs.getDate("schd_start_date"));
                    campaignVo.setCampSchdTimeSlot(rs.getString("schd_time_slot_id"));
                    campaignVo.setCampReminders(rs.getInt("reminders"));
                    campaignVo.setCampReminderIntervalUnit(rs.getString("reminder_interval_unit"));
                    campaignVo.setCampReminderIntervalNunits(rs.getString("reminder_interval_nunits"));
                    campaignVo.setCampTerminiationBasis(rs.getString("termination_basis"));
                    campaignVo.setCampTerminiationDate(rs.getDate("termination_date"));
                    campaignVo.setCampTerminiationFreq(rs.getInt("termination_freq"));
                    campaignVo.setCampIsForEmail(rs.getString("is_for_email"));
                    campaignVo.setCampIsForSms(rs.getString("is_for_sms"));
                    campaignVo.setCampIsForFax(rs.getString("is_for_fax"));
                    campaignVo.setCampIsForIvr(rs.getString("is_for_ivr"));
                    campaignVo.setCampIsForWebsite(rs.getString("is_for_website"));
                    campaignVo.setCampMessageBody(rs.getString("message_body"));
                    campaignVo.setCampSmsMessage(rs.getString("sms_message"));
//                    campaignVo.setCampAttachDataFileName(rs.getString("attached_data_file_name"));
                    campaignVo.setCampAttachFileId(rs.getInt("attached_file_id"));
                    campaignVo.setCampAttachFileIvrId(rs.getInt("ivr_file_id"));
//                    campaignVo.setCampAttachIvrFileName(rs.getString("attached_ivr_file_name"));
                    campaignVo.setCampCouponProgId(rs.getString("coupon_program_id"));
                    campaignVo.setCampIsModified(rs.getString("is_modified"));
                    campaignVo.setCampSchdNextDtime(rs.getDate("schd_next_exe_dtime"));
                    campaignVo.setCampRemindersDone(rs.getInt("reminders_done"));
                }

            } catch (SQLException e) {
                lgr.log(I2cLogger.WARNING, "Exception in method getCampaignInsts-- > " +
                       e.getMessage());
                lgr.log(I2cLogger.WARNING, "Stack Tracke of getCampaignInsts --> " +
                        CommonUtilities.getStackTrace(e));
            }finally{
                try {
                    if (rs != null)
                        rs.close();
                    if (stmt != null)
                        stmt.close();
                } catch (SQLException e) {

                }
            }
            return campaignVo;
    }





    public void insertCampaignInsts (CampaignsVO campaignVoObj) {
        lgr.log(I2cLogger.FINE, "<----START method insertCampaignInsts-- > ");

        StringBuffer INSERT_CAMPAIGN_INSTS = new StringBuffer();
        INSERT_CAMPAIGN_INSTS.append("INSERT INTO campaign_insts (campaign_id, campaign_name, campaign_desc, creation_date, ");
        INSERT_CAMPAIGN_INSTS.append("creation_time, creator_user_id, status_id, recipient_flag, ");
        INSERT_CAMPAIGN_INSTS.append("business_days_flag, subject, freq_type, freq_map, ");
        INSERT_CAMPAIGN_INSTS.append("schd_start_date, schd_time_slot_id, reminders, reminder_interval_unit, ");
        INSERT_CAMPAIGN_INSTS.append("reminder_interval_nunits, termination_basis, termination_date, termination_freq, ");
        INSERT_CAMPAIGN_INSTS.append("is_for_email, is_for_sms, is_for_fax, is_for_ivr, ");
        INSERT_CAMPAIGN_INSTS.append("is_for_website, message_body, sms_message, attached_file_id, ");
        INSERT_CAMPAIGN_INSTS.append("ivr_file_id, coupon_program_id, is_modified, schd_next_exe_dtime, from_email)");
        INSERT_CAMPAIGN_INSTS.append("VALUES(  ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?,?, ?,?, ?, ?, ?, ?,?, ?, ?)");

        lgr.log(I2cLogger.INFO, "Insert into CampaignInst --> " +
                INSERT_CAMPAIGN_INSTS.toString());

        PreparedStatement stmt = null ;

        try {
            // prepare the statement
            stmt = dbConn.prepareStatement(INSERT_CAMPAIGN_INSTS.toString());

            stmt.setInt(1,campaignVoObj.getCampaignId());
            stmt.setString(2, campaignVoObj.getCampaignName());
            stmt.setString(3, campaignVoObj.getCampaignDesc());
            stmt.setDate(4, campaignVoObj.getCampCreationDate());
            stmt.setDate(5, campaignVoObj.getCampCreationTime());
            stmt.setString(6, campaignVoObj.getCampCreatorUserId());
            stmt.setString(7, campaignVoObj.getCampStatusId());
            stmt.setString(8, campaignVoObj.getCampRecipientFlag());
            stmt.setString(9, campaignVoObj.getCampBusinessDaysFlag());
            stmt.setString(10, campaignVoObj.getCampSubject());
            stmt.setString(11, campaignVoObj.getCampFreqType());
            stmt.setString(12, campaignVoObj.getCampFreqMap());
            stmt.setDate(13, campaignVoObj.getCampSchdStartDate());
            stmt.setString(14, campaignVoObj.getCampSchdTimeSlot());
            stmt.setInt(15, campaignVoObj.getCampReminders());
            stmt.setString(16, campaignVoObj.getCampReminderIntervalUnit());
            stmt.setString(17, campaignVoObj.getCampReminderIntervalNunits());
            stmt.setString(18, campaignVoObj.getCampTerminiationBasis());
            stmt.setDate(19, campaignVoObj.getCampTerminiationDate());
            stmt.setInt(20, campaignVoObj.getCampTerminiationFreq());
            stmt.setString(21, campaignVoObj.getCampIsForEmail());
            stmt.setString(22, campaignVoObj.getCampIsForSms());
            stmt.setString(23, campaignVoObj.getCampIsForFax());
            stmt.setString(24, campaignVoObj.getCampIsForIvr());
            stmt.setString(25, campaignVoObj.getCampIsForWebsite());
            stmt.setString(26, campaignVoObj.getCampMessageBody());
            stmt.setString(27, campaignVoObj.getCampSmsMessage());
            stmt.setInt(28, campaignVoObj.getCampAttachFileId());
            stmt.setInt(29, campaignVoObj.getCampAttachFileIvrId());
            stmt.setString(30, campaignVoObj.getCampCouponProgId());
            stmt.setString(31, campaignVoObj.getCampIsModified());
            stmt.setDate(32, campaignVoObj.getCampSchdNextDtime());
            stmt.setString(33, campaignVoObj.getCampFromEmail());

//            stmt.setString(4, CommonUtilities.convertDateFormat(Constants.DATE_FORMAT, Constants.DATA_FORMAT_PROCESS, campaignVoObj.getCampCreationDate().toString()));
//            stmt.setString(28, campaignVoObj.getCampAttachDataFileName());
//            stmt.setBytes(29, campaignVoObj.getCampAttachFileData());
//            stmt.setBytes(30, campaignVoObj.getCampAttachFileIvr());
//            stmt.setString(31, campaignVoObj.getCampAttachIvrFileName());


            int var = -1;
            if (dbConn != null){
                var = stmt.executeUpdate();
            }
//            System.out.print(var);
//            dbConn.commit();
        } catch (SQLException e) {
            lgr.log(I2cLogger.WARNING, "<------ Exception in insertCampaignInsts ------>" +
                    e.getMessage());
            lgr.log(I2cLogger.WARNING, "Stack Trace----->" + CommonUtilities.getStackTrace(e));
        }finally{

            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {

            }
        }

        lgr.log(I2cLogger.FINEST, "<----END method insertCampaignInsts-- > ");

    }




    public int getCampaingInstId(int campaignId) {
        lgr.log(I2cLogger.FINEST, "<----START method insertCampaignInsts-- > ");

        StringBuffer CAMPAIGN_INST_ID_QUERY = new StringBuffer();
        CAMPAIGN_INST_ID_QUERY.append("SELECT campaign_inst_id FROM campaign_insts WHERE campaign_id = ?");

        int campaignInstId = 0;
        PreparedStatement stmt = null ;
        ResultSet rs = null ;
        try {
            if (dbConn != null)
            stmt = dbConn.prepareStatement(CAMPAIGN_INST_ID_QUERY.toString());
            // set the Campaign ID value in the query
            stmt.setInt(1, campaignId);

            lgr.log(I2cLogger.INFO, " campInstSelect Query ----> " + CAMPAIGN_INST_ID_QUERY.toString());
            lgr.log(I2cLogger.INFO, "Campaign_Insts_Id-- > " + campaignId);

            rs = stmt.executeQuery();// execute the query
            // get the values from result set and set in Value Object
            if (rs.next()){
                campaignInstId = rs.getInt(1);
            }
        } catch (SQLException e) {
            lgr.log(I2cLogger.WARNING, "Exception in method getCampaigns-- > " + e.getMessage());
            lgr.log(I2cLogger.WARNING, "Stack Tracke of getCampaings --> " +
                    CommonUtilities.getStackTrace(e));
        }finally{
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                System.err.print(e);
            }
        }
        return campaignInstId;
    }




    public CampaignCardFiltersVO getCampCardFilters(int campaignId)  {
       lgr.log(I2cLogger.FINE, "<---- START method getCampCardFilters(campaignId) ---->");

       CampaignCardFiltersVO campaignCardFilterVo = new CampaignCardFiltersVO();
       StringBuffer CAMP_CARD_FILTER_QUERY = new StringBuffer();

       CAMP_CARD_FILTER_QUERY.append("SELECT campaign_id, card_prg_id, card_status, ch_ep_loc_group, card_no, ch_birthday_mmdd,");
       CAMP_CARD_FILTER_QUERY.append(" zip_postal_code, ch_age_dob, ch_age, act_date, expiration_date, avail_balance, ledger_balance, city, state,");
       CAMP_CARD_FILTER_QUERY.append(" country, stake_holder_id FROM camp_card_filters WHERE campaign_id = ?");

       PreparedStatement stmt = null;
       ResultSet rs = null;
       try {
           stmt = dbConn.prepareStatement(CAMP_CARD_FILTER_QUERY.toString());
           // set the Campaign ID value in the query
           stmt.setInt(1, campaignId);

           lgr.log(I2cLogger.FINEST, "CampaignInstId-- > " + campaignId);

           rs = stmt.executeQuery();// execute the query
           // get the values from result set and set in Value Object
           while (rs.next()){
               campaignCardFilterVo.setCcfCampaignId(rs.getInt("campaign_id"));
               campaignCardFilterVo.setCcfCardProgId(rs.getString("card_prg_id"));
               campaignCardFilterVo.setCcfCardStatus(rs.getString("card_status"));
               campaignCardFilterVo.setCcfChEpLocGroup(rs.getString("ch_ep_loc_group"));
               campaignCardFilterVo.setCcfCardNo(rs.getString("card_no"));
               campaignCardFilterVo.setCcfChBirthdayMmDd(rs.getString("ch_birthday_mmdd"));
               campaignCardFilterVo.setCcfChAgeDOB(rs.getString("ch_age_dob"));
               campaignCardFilterVo.setCcfChAge(rs.getString("ch_age"));
               campaignCardFilterVo.setCcfActDate(rs.getString("act_date"));
               campaignCardFilterVo.setCcfExpirationDate(rs.getString("expiration_date"));
               campaignCardFilterVo.setCcfAvailBalance(rs.getString("avail_balance"));
               campaignCardFilterVo.setCcfLedgerBalance(rs.getString("ledger_balance"));
               campaignCardFilterVo.setCcfCity(rs.getString("city"));
               campaignCardFilterVo.setCcfState(rs.getString("state"));
               campaignCardFilterVo.setCcfZipPostalCode(rs.getString("zip_postal_code"));
               campaignCardFilterVo.setCcfCountry(rs.getString("country"));
               campaignCardFilterVo.setCcfStakeHolderId(rs.getString("stake_holder_id"));
           }

       } catch (SQLException e) {
           lgr.log(I2cLogger.WARNING, "Exception in method getCampCardFilters-- > " + e.getMessage());
           lgr.log(I2cLogger.WARNING, "Stack Tracke of getCampCardFilters --> " +
                   CommonUtilities.getStackTrace(e));
       }finally{
           try {
               if (rs != null)
                   rs.close();
               if (stmt != null)
                   stmt.close();
           } catch (SQLException e) {

           }
       }
       return campaignCardFilterVo;
   }





   public CampaignCardFiltersVO getCampInstCardFilters(int campaignId)  {
          lgr.log(I2cLogger.FINE, "<---- START method getCampInstCardFilters(campaignId) ---->");

          CampaignCardFiltersVO campaignCardFilterVo = new CampaignCardFiltersVO();
          StringBuffer CAMP_CARD_FILTER_QUERY = new StringBuffer();

          CAMP_CARD_FILTER_QUERY.append("SELECT campaign_inst_id, card_prg_id, card_status, ch_ep_loc_group, card_no, ch_birthday_mmdd,");
          CAMP_CARD_FILTER_QUERY.append(" zip_postal_code, ch_age_dob, ch_age, act_date, expiration_date, avail_balance, ledger_balance, city, state,");
          CAMP_CARD_FILTER_QUERY.append(" country, stake_holder_id FROM camp_inst_card_filters WHERE campaign_inst_id = ?");

          PreparedStatement stmt = null;
          ResultSet rs = null;
          try {
              stmt = dbConn.prepareStatement(CAMP_CARD_FILTER_QUERY.toString());
              // set the Campaign ID value in the query
              stmt.setInt(1, campaignId);

              lgr.log(I2cLogger.FINEST, "CampaignInstId-- > " + campaignId);

              rs = stmt.executeQuery();// execute the query
              // get the values from result set and set in Value Object
              while (rs.next()){
                  campaignCardFilterVo.setCcfCampaignId(rs.getInt("campaign_inst_id"));
                  campaignCardFilterVo.setCcfCardProgId(rs.getString("card_prg_id"));
                  campaignCardFilterVo.setCcfCardStatus(rs.getString("card_status"));
                  campaignCardFilterVo.setCcfChEpLocGroup(rs.getString("ch_ep_loc_group"));
                  campaignCardFilterVo.setCcfCardNo(rs.getString("card_no"));
                  campaignCardFilterVo.setCcfChBirthdayMmDd(rs.getString("ch_birthday_mmdd"));
                  campaignCardFilterVo.setCcfChAgeDOB(rs.getString("ch_age_dob"));
                  campaignCardFilterVo.setCcfChAge(rs.getString("ch_age"));
                  campaignCardFilterVo.setCcfActDate(rs.getString("act_date"));
                  campaignCardFilterVo.setCcfExpirationDate(rs.getString("expiration_date"));
                  campaignCardFilterVo.setCcfAvailBalance(rs.getString("avail_balance"));
                  campaignCardFilterVo.setCcfLedgerBalance(rs.getString("ledger_balance"));
                  campaignCardFilterVo.setCcfCity(rs.getString("city"));
                  campaignCardFilterVo.setCcfState(rs.getString("state"));
                  campaignCardFilterVo.setCcfZipPostalCode(rs.getString("zip_postal_code"));
                  campaignCardFilterVo.setCcfCountry(rs.getString("country"));
                  campaignCardFilterVo.setCcfStakeHolderId(rs.getString("stake_holder_id"));
              }

          } catch (SQLException e) {
              lgr.log(I2cLogger.WARNING, "Exception in method getCampInstCardFilters-- > " + e.getMessage());
              lgr.log(I2cLogger.WARNING, "Stack Tracke of getCampInstCardFilters --> " +
                      CommonUtilities.getStackTrace(e));
          }finally{
              try {
                  if (rs != null)
                      rs.close();
                  if (stmt != null)
                      stmt.close();
              } catch (SQLException e) {

              }
          }
          return campaignCardFilterVo;
   }





   public CampaignTransFiltersVO getCampTransFilters(int campaignId)  {
       lgr.log(I2cLogger.FINE, "<---- START method getCampTransFilters(campaignId) ---->");

       CampaignTransFiltersVO campaignTransFilterVo = new CampaignTransFiltersVO();
       StringBuffer CAMP_TRANS_FILTER_QUERY = new StringBuffer();

       CAMP_TRANS_FILTER_QUERY.append("SELECT campaign_id, service_id, trans_date, trans_time, trans_type, amount_proccessed,");
       CAMP_TRANS_FILTER_QUERY.append(" trans_ep_loc_group, trans_stk_holder_id, trans_merchant_id, response_code, message_type,");
       CAMP_TRANS_FILTER_QUERY.append(" acquirer_id, network_id, merchant_city, merchant_state, merchant_zip_p_code, merchant_country ");
       CAMP_TRANS_FILTER_QUERY.append(" FROM camp_trans_filters WHERE campaign_id = ?");


       PreparedStatement stmt = null ;
       ResultSet rs = null ;
       try {
           stmt = dbConn.prepareStatement(CAMP_TRANS_FILTER_QUERY.toString());
           // set the Campaign ID value in the query
           stmt.setInt(1, campaignId);

           lgr.log(I2cLogger.FINEST, "CampaignInstId-- > " + campaignId);

           rs = stmt.executeQuery();// execute the query
           // get the values from result set and set in Value Object
           while (rs.next()){
               campaignTransFilterVo.setCtfCampaignId(rs.getInt("campaign_id"));
               campaignTransFilterVo.setCtfServiceId(rs.getString("service_id"));
               campaignTransFilterVo.setCtfTransDate(rs.getString("trans_date"));
               campaignTransFilterVo.setCtfTransTime(rs.getString("trans_time"));
               campaignTransFilterVo.setCtfTransType(rs.getString("trans_type"));
               campaignTransFilterVo.setCtfAmountProccessed(rs.getString("amount_proccessed"));
               campaignTransFilterVo.setCtfTransEpLocGroup(rs.getString("trans_ep_loc_group"));
               campaignTransFilterVo.setCtfTransStkHolderId(rs.getString("trans_stk_holder_id"));
               campaignTransFilterVo.setCtfTransMerchantId(rs.getString("trans_merchant_id"));
               campaignTransFilterVo.setCtfResponseCode(rs.getString("response_code"));
               campaignTransFilterVo.setCtfMessageType(rs.getString("message_type"));
               campaignTransFilterVo.setCtfAcquirerId(rs.getString("acquirer_id"));
               campaignTransFilterVo.setCtfNetworkId(rs.getString("network_id"));
               campaignTransFilterVo.setCtfMerchentCity(rs.getString("merchant_city"));
               campaignTransFilterVo.setCtfMerchentState(rs.getString("merchant_state"));
               campaignTransFilterVo.setCtfMerchentZipPCode(rs.getString("merchant_zip_p_code"));
               campaignTransFilterVo.setCtfMerchentCountry(rs.getString("merchant_country"));
           }

       } catch (SQLException e) {
           lgr.log(I2cLogger.WARNING, "Exception in method getCampTransFilters-- > " + e.getMessage());
           lgr.log(I2cLogger.WARNING, "Stack Tracke of getCampTransFilters --> " +
                   CommonUtilities.getStackTrace(e));
       }finally{
           try {
               if (rs != null)
                   rs.close();
               if (stmt != null)
                   stmt.close();
           } catch (SQLException e) {

           }
       }
       return campaignTransFilterVo;
   }





   public CampaignTransFiltersVO getCampInstTransFilters(int campaignId)  {
          lgr.log(I2cLogger.FINE, "<---- START method getCampInstTransFilters(campaignId) ---->");

          CampaignTransFiltersVO campaignTransFilterVo = new CampaignTransFiltersVO();
          StringBuffer CAMP_TRANS_FILTER_QUERY = new StringBuffer();

          CAMP_TRANS_FILTER_QUERY.append("SELECT campaign_inst_id, service_id, trans_date, trans_time, trans_type, amount_proccessed,");
          CAMP_TRANS_FILTER_QUERY.append(" trans_ep_loc_group, trans_stk_holder_id, trans_merchant_id, response_code, message_type,");
          CAMP_TRANS_FILTER_QUERY.append(" acquirer_id, network_id, merchant_city, merchant_state, merchant_zip_p_code, merchant_country ");
          CAMP_TRANS_FILTER_QUERY.append(" FROM camp_inst_trans_filters WHERE campaign_inst_id = ?");


          PreparedStatement stmt = null ;
          ResultSet rs = null ;
          try {
              stmt = dbConn.prepareStatement(CAMP_TRANS_FILTER_QUERY.toString());
              // set the Campaign ID value in the query
              stmt.setInt(1, campaignId);

              lgr.log(I2cLogger.FINEST, "CampaignInstId-- > " + campaignId);

              rs = stmt.executeQuery();// execute the query
              // get the values from result set and set in Value Object
              while (rs.next()){
                  campaignTransFilterVo.setCtfCampaignId(rs.getInt("campaign_inst_id"));
                  campaignTransFilterVo.setCtfServiceId(rs.getString("service_id"));
                  campaignTransFilterVo.setCtfTransDate(rs.getString("trans_date"));
                  campaignTransFilterVo.setCtfTransTime(rs.getString("trans_time"));
                  campaignTransFilterVo.setCtfTransType(rs.getString("trans_type"));
                  campaignTransFilterVo.setCtfAmountProccessed(rs.getString("amount_proccessed"));
                  campaignTransFilterVo.setCtfTransEpLocGroup(rs.getString("trans_ep_loc_group"));
                  campaignTransFilterVo.setCtfTransStkHolderId(rs.getString("trans_stk_holder_id"));
                  campaignTransFilterVo.setCtfTransMerchantId(rs.getString("trans_merchant_id"));
                  campaignTransFilterVo.setCtfResponseCode(rs.getString("response_code"));
                  campaignTransFilterVo.setCtfMessageType(rs.getString("message_type"));
                  campaignTransFilterVo.setCtfAcquirerId(rs.getString("acquirer_id"));
                  campaignTransFilterVo.setCtfNetworkId(rs.getString("network_id"));
                  campaignTransFilterVo.setCtfMerchentCity(rs.getString("merchant_city"));
                  campaignTransFilterVo.setCtfMerchentState(rs.getString("merchant_state"));
                  campaignTransFilterVo.setCtfMerchentZipPCode(rs.getString("merchant_zip_p_code"));
                  campaignTransFilterVo.setCtfMerchentCountry(rs.getString("merchant_country"));
              }

          } catch (SQLException e) {
              lgr.log(I2cLogger.WARNING, "Exception in method getCampInstTransFilters-- > " + e.getMessage());
              lgr.log(I2cLogger.WARNING, "Stack Tracke of getCampInstTransFilters --> " +
                      CommonUtilities.getStackTrace(e));
          }finally{
              try {
                  if (rs != null)
                      rs.close();
                  if (stmt != null)
                      stmt.close();
              } catch (SQLException e) {

              }
          }
          return campaignTransFilterVo;
   }





   public void insertCampInstCardFilters (CampaignCardFiltersVO campCardFilterVo) {
       lgr.log(I2cLogger.FINEST, "<----START method insertCampInstCardFilters-- > ");

       StringBuffer INSERT_CAMP_INST_CARD_FILTER = new StringBuffer();
       INSERT_CAMP_INST_CARD_FILTER.append("INSERT INTO camp_inst_card_filters (campaign_inst_id, card_prg_id, card_status, ch_ep_loc_group, card_no, ch_birthday_mmdd,");
       INSERT_CAMP_INST_CARD_FILTER.append(" ch_age_dob, ch_age, act_date, expiration_date, avail_balance, ledger_balance, city, state, zip_postal_code, country, stake_holder_id)");
       INSERT_CAMP_INST_CARD_FILTER.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");

       PreparedStatement stmt = null ;

       try {
           // prepare the statement
           stmt = dbConn.prepareStatement(INSERT_CAMP_INST_CARD_FILTER.toString());

           stmt.setInt(1,campCardFilterVo.getCcfCampaignId());
           stmt.setString(2, campCardFilterVo.getCcfCardProgId());
           stmt.setString(3, campCardFilterVo.getCcfCardStatus());
           stmt.setString(4, campCardFilterVo.getCcfChEpLocGroup());
           stmt.setString(5, campCardFilterVo.getCcfCardNo());
           stmt.setString(6, campCardFilterVo.getCcfChBirthdayMmDd());
           stmt.setString(7, campCardFilterVo.getCcfChAgeDOB());
           stmt.setString(8, campCardFilterVo.getCcfChAge());
           stmt.setString(9, campCardFilterVo.getCcfActDate());
           stmt.setString(10, campCardFilterVo.getCcfExpirationDate());
           stmt.setString(11, campCardFilterVo.getCcfAvailBalance());
           stmt.setString(12, campCardFilterVo.getCcfLedgerBalance());
           stmt.setString(13, campCardFilterVo.getCcfCity());
           stmt.setString(14, campCardFilterVo.getCcfState());
           stmt.setString(15, campCardFilterVo.getCcfZipPostalCode());
           stmt.setString(16, campCardFilterVo.getCcfCountry());
           stmt.setString(17, campCardFilterVo.getCcfStakeHolderId());

           stmt.executeUpdate();
//           dbConn.commit();
       } catch (SQLException e) {
           lgr.log(I2cLogger.WARNING, "<------ Exception in insertCampInstCardFilters ------>" +
                   e.getMessage());
           lgr.log(I2cLogger.WARNING, "Stack Trace----->" + CommonUtilities.getStackTrace(e));
       }finally{

           try {
               if (stmt != null)
                   stmt.close();
           } catch (SQLException e) {

           }
       }

       lgr.log(I2cLogger.FINEST, "<----END method insertCampInstCardFilters-- > ");

   }





    public void insertCampInstTransFilters (CampaignTransFiltersVO campTransFilterVo) {
        lgr.log(I2cLogger.FINEST, "<----START method insertCampInstTransFilters-- > ");

        StringBuffer INSERT_CAMP_INST_TRANS_FILTER = new StringBuffer();
        INSERT_CAMP_INST_TRANS_FILTER.append("INSERT INTO camp_inst_trans_filters (campaign_inst_id, service_id, trans_date, trans_time, trans_type,");
        INSERT_CAMP_INST_TRANS_FILTER.append(" amount_proccessed,  trans_ep_loc_group, trans_stk_holder_id, trans_merchant_id, response_code,");
        INSERT_CAMP_INST_TRANS_FILTER.append(" message_type, acquirer_id, network_id, merchant_city, merchant_state, merchant_zip_p_code, merchant_country )");
        INSERT_CAMP_INST_TRANS_FILTER.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");

        PreparedStatement stmt = null ;

        try {
            // prepare the statement
            stmt = dbConn.prepareStatement(INSERT_CAMP_INST_TRANS_FILTER.toString());

            stmt.setInt(1,campTransFilterVo.getCtfCampaignId());
            stmt.setString(2, campTransFilterVo.getCtfServiceId());
            stmt.setString(3, campTransFilterVo.getCtfTransDate());
            stmt.setString(4, campTransFilterVo.getCtfTransTime());
            stmt.setString(5, campTransFilterVo.getCtfTransType());
            stmt.setString(6, campTransFilterVo.getCtfAmountProccessed());
            stmt.setString(7, campTransFilterVo.getCtfTransEpLocGroup());
            stmt.setString(8, campTransFilterVo.getCtfTransStkHolderId());
            stmt.setString(9, campTransFilterVo.getCtfTransMerchantId());
            stmt.setString(10, campTransFilterVo.getCtfResponseCode());
            stmt.setString(11, campTransFilterVo.getCtfMessageType());
            stmt.setString(12, campTransFilterVo.getCtfAcquirerId());
            stmt.setString(13, campTransFilterVo.getCtfNetworkId());
            stmt.setString(14, campTransFilterVo.getCtfMerchentCity());
            stmt.setString(15, campTransFilterVo.getCtfMerchentState());
            stmt.setString(16, campTransFilterVo.getCtfMerchentZipPCode());
            stmt.setString(17, campTransFilterVo.getCtfMerchentCountry());

            stmt.executeUpdate();

//            dbConn.commit();
        } catch (SQLException e) {
            lgr.log(I2cLogger.WARNING, "<------ Exception in insertCampInstTransFilters ------>" +
                    e.getMessage());
            lgr.log(I2cLogger.WARNING, "Stack Trace----->" + CommonUtilities.getStackTrace(e));
        }finally{

            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {

            }
        }

        lgr.log(I2cLogger.FINEST, "<----END method insertCampInstTransFilters-- > ");

    }





    public void insertCampInstDetails (CampaignInstDetailsVO campInstDetailsVo) {
            lgr.log(I2cLogger.FINEST, "<----START method insertCampInstDetails-- > ");

            StringBuffer INSERT_CAMP_INST_DETAILS = new StringBuffer();
            INSERT_CAMP_INST_DETAILS.append("INSERT INTO camp_inst_details (recipient_id, campaign_inst_id, channel_id) VALUES ( ?, ?, ?)");

            PreparedStatement stmt = null ;

            try {
                // prepare the statement
                stmt = dbConn.prepareStatement(INSERT_CAMP_INST_DETAILS.toString());

                stmt.setInt(1, campInstDetailsVo.getRecipientId());
                stmt.setInt(2, campInstDetailsVo.getCampaingInstId());
                stmt.setString(3, campInstDetailsVo.getChannelId());

                stmt.executeUpdate();
            } catch (SQLException e) {
                lgr.log(I2cLogger.WARNING, "<------ Exception in insertCampInstDetails ------>" +
                        e.getMessage());
                lgr.log(I2cLogger.WARNING, "Stack Trace----->" + CommonUtilities.getStackTrace(e));
            }finally{

                try {
                    if (stmt != null)
                        stmt.close();
                } catch (SQLException e) {

                }
            }

            lgr.log(I2cLogger.FINEST, "<----END method insertCampInstTransFilters-- > ");

    }





    public ArrayList getRecipientIds (String recipientQuery)  {
           lgr.log(I2cLogger.FINE, "<---- START method getRecipientId(recipientQuery) ---->");

           ArrayList recipientIdAryList = new ArrayList();

           PreparedStatement stmt = null ;
           ResultSet rs = null ;
           try {
             lgr.info("RecipientQuery ---- > " + recipientQuery);

             stmt = dbConn.prepareStatement(recipientQuery);


               rs = stmt.executeQuery();// execute the query
               // get the values from result set and set in Value Object
               while (rs.next()){
                   recipientIdAryList.add(new Integer(rs.getInt(1)));
               }

           } catch (SQLException e) {
               lgr.log(I2cLogger.WARNING, "Exception in method getRecipientId-- > " + e.getMessage());
               lgr.log(I2cLogger.WARNING, "Stack Tracke of getRecipientId --> " +
                       CommonUtilities.getStackTrace(e));
           }finally{
               try {
                   if (rs != null)
                       rs.close();
                   if (stmt != null)
                       stmt.close();
               } catch (SQLException e) {

               }
           }
           return recipientIdAryList;
   }





//   public CampaignsVO getNextCampIds()  {
//       lgr.log(I2cLogger.FINE, "<---- START method getCampaings(campaignId) ---->");
//
//       CampaignsVO campaignVo = new CampaignsVO();
//       StringBuffer CAMPAIGN_SELECT_QUERY = new StringBuffer();
////       int[] campId = 0;
//
//       CAMPAIGN_SELECT_QUERY.append("SELECT campaign_id, schd_next_exe_dtime FROM campaigns WHERE schd_next_exe_dtime <= today");
//
//       PreparedStatement stmt = null ;
//       ResultSet rs = null ;
//       try {
//           stmt = dbConn.prepareStatement(CAMPAIGN_SELECT_QUERY.toString());
//           // set the Campaign ID value in the query
////           stmt.setInt(1, campaignId);
//
////           lgr.log(I2cLogger.FINEST, "Campaign Id-- > " + campaignId);
//
//           rs = stmt.executeQuery();// execute the query
//           // get the values from result set and set in Value Object
//           while (rs.next()){
//
//           }
//
//           } catch (SQLException e) {
//               lgr.log(I2cLogger.WARNING, "Exception in method getCampaigns-- > " + e.getMessage());
//               lgr.log(I2cLogger.WARNING, "Stack Tracke of getCampaings --> " +
//                       CommonUtilities.getStackTrace(e));
//           }finally{
//               try {
//                   if (rs != null)
//                       rs.close();
//                   if (stmt != null)
//                       stmt.close();
//               } catch (SQLException e) {
//
//               }
//           }
//           return campaignVo;
//    }

}
