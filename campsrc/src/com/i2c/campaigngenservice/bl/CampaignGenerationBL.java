package com.i2c.campaigngenservice.bl;

import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.Statement;

import com.i2c.campaigngenservice.dao.CampaignDAO;
import com.i2c.campaigngenservice.vo.*;
import com.i2c.campaigngenservice.util.Constants;
import java.io.File;
import com.i2c.utils.logging.I2cLogger;
import com.i2c.campaigngenservice.util.WildCardQuery;
import java.util.ArrayList;

/**
 * <p>Title: Campaign Generation API</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Innovative Pvt. Ltd.</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CampaignGenerationBL {

    private Connection dbConn = null;
    private String instanceID = null;
    private Logger campMainLgr;


    public CampaignGenerationBL(String instanceID, Connection dbConn) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
        this.campMainLgr = I2cLogger.getInstance(Constants.LOG_FILE_PATH +
                              File.separator + this.instanceID + File.separator +
                              Constants.LOG_FILE_NAME + "-%g.log",
                              Constants.LOG_FILE_SIZE,
                              Constants.LOG_FILE_NO,
                              Constants.LOG_CONTEXT_NAME);

    }





    public CampaignGenerationBL(String instanceID, Connection dbConn, Logger campMainLgr) {
        this.instanceID = instanceID;
        this.dbConn = dbConn;
        this.campMainLgr = campMainLgr;
    }





    public void copyCampaignToInsts(int campaignId){
        campMainLgr.log(I2cLogger.FINE, "<---------START copyCampaignToInsts--------->");

        CampaignsVO campVo = null;
        CampaignCardFiltersVO campCardFilterVo = null;
        CampaignTransFiltersVO campTransFilterVo = null;

        CampaignDAO campaignDao = new CampaignDAO(instanceID, dbConn, campMainLgr);

        campMainLgr.log(I2cLogger.FINE, "<--------Get Campaign, Card and Trans Filters-------->");

        campVo = campaignDao.getCampaigns(campaignId);

        campMainLgr.log(I2cLogger.FINE, "<--------Get Card and Trans Filters-------->");
        campCardFilterVo = campaignDao.getCampCardFilters(campVo.getCampaignId());
        campTransFilterVo = campaignDao.getCampTransFilters(campVo.getCampaignId());

        campMainLgr.log(I2cLogger.FINE, "<--------Insert Campaign_Insts-------->");
        campaignDao.insertCampaignInsts(campVo);

        campMainLgr.log(I2cLogger.FINE, "<-----get CampInstsId from Campaign_Insts------->");
        int campInstsId = campaignDao.getCampaingInstId(campVo.getCampaignId());

        campMainLgr.log(I2cLogger.FINE, "<--------- insert campInstCardFilter ------->");
        campCardFilterVo.setCcfCampaignId(campInstsId);
        campaignDao.insertCampInstCardFilters(campCardFilterVo);

        campMainLgr.log(I2cLogger.FINE, "<--------- insert campInstTransFilter ------->");
        campTransFilterVo.setCtfCampaignId(campInstsId);
        campaignDao.insertCampInstTransFilters(campTransFilterVo);

        campMainLgr.log(I2cLogger.FINE, "<--------- END copyCampaignToInsts ------->");
    }


    public boolean updateModified(boolean modified, int camp_id) throws Exception {
        String qry = null;
        if (modified){
            qry = "update campaigns set is_modified = 'Y', schd_next_exe_dtime = null where campaign_id = " + camp_id;
        } else {
            qry = "update campaigns set is_modified = 'N', schd_next_exe_dtime = null where campaign_id = " + camp_id;
        }

        Statement stmt = dbConn.createStatement();
        if (stmt.executeUpdate(qry) > 0) {
            return true;
        } else {
            return false;
        }
    }





    public int populateCampInstDetails(int campId){

        CampaignsVO campInstsVo = null;
        CampaignInstDetailsVO campInstDetailVo = null;

        CampaignDAO campaignDao = new CampaignDAO(instanceID, dbConn, campMainLgr);

        campInstsVo = campaignDao.getCampaignInsts(campId);

        // get recipient Query to execute STEP 2
        String recipientQuery  = recipientCardQueryBuilder(campId);

        campMainLgr.log(I2cLogger.INFO, "QUERY TO GET RECEIPIENT for Campaign_ID--(" +
                        campId + ")-------->" + recipientQuery);
        // Get Recipient STEP 3
        ArrayList recipientIds = campaignDao.getRecipientIds(recipientQuery);

        for (Object recipObj : recipientIds) {
            Integer recipientId = (Integer) recipObj;
            campInstDetailVo = new CampaignInstDetailsVO();
            campInstDetailVo.setCampaingInstId(campInstsVo.getCampaignInstId());
            campInstDetailVo.setRecipientId(recipientId);
            if (campInstsVo.getCampIsForEmail().equalsIgnoreCase("Y")){
                campInstDetailVo.setChannelId("Email");
                campaignDao.insertCampInstDetails(campInstDetailVo);
            }
            if (campInstsVo.getCampIsForFax().equalsIgnoreCase("Y")){
                campInstDetailVo.setChannelId("FAX");
                campaignDao.insertCampInstDetails(campInstDetailVo);
            }
            if (campInstsVo.getCampIsForWebsite().equalsIgnoreCase("Y")){
                campInstDetailVo.setChannelId("WEB");
                campaignDao.insertCampInstDetails(campInstDetailVo);
            }
            if (campInstsVo.getCampIsForSms().equalsIgnoreCase("Y")){
                campInstDetailVo.setChannelId("SMS");
                campaignDao.insertCampInstDetails(campInstDetailVo);
            }
            if (campInstsVo.getCampIsForIvr().equalsIgnoreCase("Y")){
                campInstDetailVo.setChannelId("IVR");
                campaignDao.insertCampInstDetails(campInstDetailVo);
            }
        }
        return campInstsVo.getCampaignInstId();
    }




    public String recipientCardQueryBuilder(int campaignId){

      	StringBuffer recipientQuery = new StringBuffer();
        CampaignsVO campInstsVo = null;
        CampaignCardFiltersVO campInstCardFilterVo = null;
        CampaignTransFiltersVO campInstTransFilterVo = null;

//        int andCount = 0;
        int whereAndCount = 0;

        CampaignDAO campaignDao = new CampaignDAO(instanceID, dbConn, campMainLgr);

        campInstsVo = campaignDao.getCampaignInsts(campaignId);
        campInstCardFilterVo = campaignDao.getCampInstCardFilters(campInstsVo.getCampaignInstId());
        campInstTransFilterVo = campaignDao.getCampInstTransFilters(campInstsVo.getCampaignInstId());


        if (campInstsVo.getCampRecipientFlag().equalsIgnoreCase("C")){
            recipientQuery.append(" SELECT DISTINCT c.card_srno recipient_id ");
            recipientQuery.append(" FROM cards c, trans_requests tr "); //camp_inst_card_filters ccf,

        } else if (campInstsVo.getCampRecipientFlag().equalsIgnoreCase("E")){
            recipientQuery.append(" SELECT DISTINCT e.employer_id_srno recipient_id ");
            recipientQuery.append(" FROM  cards c, employer_ids e, trans_requests tr ");

        } else if (campInstsVo.getCampRecipientFlag().equalsIgnoreCase("S")){
            recipientQuery.append(" SELECT DISTINCT s.stake_holder_sno recipient_id ");
            recipientQuery.append(" FROM cards c, stake_holders s, trans_requests tr ");

        }
        // Add Tables Dynamically
        if (campInstCardFilterVo.getCcfAvailBalance() != null || campInstCardFilterVo.getCcfLedgerBalance() != null){
            recipientQuery.append(" , card_funds cf ");
        }
        if (campInstTransFilterVo.getCtfMessageType() != null){
            recipientQuery.append(" , message_types mt ");
        }
        if (campInstTransFilterVo.getCtfNetworkId() != null){
            recipientQuery.append(" , networks n ");
        }
        // Adding where caluse dynamically
        recipientQuery.append(" WHERE ");

        // Add Joins into Where Caluse
        if (campInstCardFilterVo.getCcfAvailBalance() != null || campInstCardFilterVo.getCcfLedgerBalance() != null){
            if (whereAndCount > 0){
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }
            recipientQuery.append(" c.card_no = cf.card_no ");
        }
        if (campInstTransFilterVo.getCtfMessageType() != null){
            if (whereAndCount > 0){
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }
            recipientQuery.append(" tr.iso_message_type = mt.msg_type ");
        }
        if (campInstTransFilterVo.getCtfNetworkId() != null){
            if (whereAndCount > 0){
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }
            recipientQuery.append(" tr.efunddataacqnetid = n.network_id ");
        }
        if (campInstsVo.getCampRecipientFlag().equalsIgnoreCase("E")){
            if (whereAndCount > 0){
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }
            recipientQuery.append(" c.employer_id = e.employer_id ");
        }
        if (campInstsVo.getCampRecipientFlag().equalsIgnoreCase("S")){
            if (whereAndCount > 0){
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }
            recipientQuery.append(" c.stake_holder_id = s.stake_holder_id ");
        }

        if (whereAndCount > 0) {
            recipientQuery.append(" AND ");
        } else {
            whereAndCount++;
        }
        recipientQuery.append(" c.card_no = tr.card_no ");

        //----------------- Rremaing Where Caluse Elements to put--------------
        if (campInstCardFilterVo.getCcfCardProgId() != null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }
          recipientQuery.append("("+WildCardQuery.getSQL("c.card_prg_id", campInstCardFilterVo.getCcfCardProgId())+")");
        }
        if (campInstCardFilterVo.getCcfCardStatus()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }
            recipientQuery.append("("+WildCardQuery.getSQL("c.card_status_atm", campInstCardFilterVo.getCcfCardStatus())+")");
        }
        if (campInstCardFilterVo.getCcfCardNo() != null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }
            recipientQuery.append("("+WildCardQuery.getSQL("c.card_no", campInstCardFilterVo.getCcfCardNo())+")");
        }
        if (campInstCardFilterVo.getCcfChBirthdayMmDd()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }
            recipientQuery.append("("+WildCardQuery.getSQL("to_char(c.date_of_birth, '%m-%d')", campInstCardFilterVo.getCcfChBirthdayMmDd())+")");
        }
        if (campInstCardFilterVo.getCcfActDate()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("c.first_issue_on", campInstCardFilterVo.getCcfActDate())+")");
        }
        if (campInstCardFilterVo.getCcfExpirationDate()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("c.expiry_on", campInstCardFilterVo.getCcfExpirationDate())+")");
        }
        if (campInstCardFilterVo.getCcfAvailBalance()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("cf.card_balance", campInstCardFilterVo.getCcfAvailBalance())+")");
        }
        if (campInstCardFilterVo.getCcfLedgerBalance()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("cf.ledger_balance", campInstCardFilterVo.getCcfLedgerBalance())+")");
        }
        if (campInstCardFilterVo.getCcfCity()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("c.city", campInstCardFilterVo.getCcfCity())+")");
        }
        if (campInstCardFilterVo.getCcfState()!= null){
            if (whereAndCount > 0)
           {
               recipientQuery.append(" AND ");
           } else {
                whereAndCount++;
            }

           recipientQuery.append("("+WildCardQuery.getSQL("c.state_code", campInstCardFilterVo.getCcfState())+")");
        }
        if (campInstCardFilterVo.getCcfZipPostalCode()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("c.zip_postal_code", campInstCardFilterVo.getCcfZipPostalCode())+")");
        }
        if (campInstCardFilterVo.getCcfCountry()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("c.country_code", campInstCardFilterVo.getCcfCountry())+")");
        }
        if (campInstCardFilterVo.getCcfStakeHolderId()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("c.stake_holder_id", campInstCardFilterVo.getCcfStakeHolderId())+")");
        }
        /********************Tans Filter***********************************/
        if (campInstTransFilterVo.getCtfServiceId() != null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("tr.service_id", campInstTransFilterVo.getCtfServiceId())+")");
        }
        if (campInstTransFilterVo.getCtfTransDate()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("tr.trans_date", campInstTransFilterVo.getCtfTransDate())+")");
        }
        if (campInstTransFilterVo.getCtfTransTime() != null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("tr.trans_time", campInstTransFilterVo.getCtfTransTime())+")");
        }
        if (campInstTransFilterVo.getCtfTransType()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("tr.trans_type", campInstTransFilterVo.getCtfTransType())+")");
        }
        if (campInstTransFilterVo.getCtfAmountProccessed() != null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("tr.amount_proccessed", campInstTransFilterVo.getCtfAmountProccessed())+")");
        }
        // Trans Stk holder Id
//        if (campInstTransFilterVo.getCtfTransStkHolderId()!= null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            } else {
//                        whereAndCount++;
//            }

//            recipientQuery.append(WildCardQuery.getSQL("tr.trans_stk_holder_id", campInstTransFilterVo.getCtfTransStkHolderId()));
//        }
        // trans merchant id
        if (campInstTransFilterVo.getCtfTransMerchantId()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            }
            recipientQuery.append("("+WildCardQuery.getSQL("tr.card_aceptor_code", campInstTransFilterVo.getCtfTransMerchantId())+")");
        }
        if (campInstTransFilterVo.getCtfResponseCode()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("tr.response_code", campInstTransFilterVo.getCtfResponseCode())+")");
        }
        if (campInstTransFilterVo.getCtfMessageType()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

//            recipientQuery.append("tr.iso_message_type = mt.msg_type AND ");
            recipientQuery.append("("+WildCardQuery.getSQL("mt.msg_type", campInstTransFilterVo.getCtfMessageType())+")");
        }
        if (campInstTransFilterVo.getCtfAcquirerId()!= null){
            if (whereAndCount > 0)
           {
               recipientQuery.append(" AND ");
           } else {
                whereAndCount++;
            }

           recipientQuery.append("("+WildCardQuery.getSQL("tr.acq_inst_code", campInstTransFilterVo.getCtfAcquirerId())+")");
        }
        if (campInstTransFilterVo.getCtfNetworkId()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

//            recipientQuery.append("tr.efunddataacqnetid = n.network_id AND ");
            recipientQuery.append("("+WildCardQuery.getSQL("tr.efunddataacqnetid", campInstTransFilterVo.getCtfNetworkId())+")");
        }
        if (campInstTransFilterVo.getCtfMerchentCity()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("tr.card_aceptor_name[24,36]", campInstTransFilterVo.getCtfMerchentCity())+")");
        }
        if (campInstTransFilterVo.getCtfMerchentState()!= null){
            if (whereAndCount > 0)
            {
                recipientQuery.append(" AND ");
            } else {
                whereAndCount++;
            }

            recipientQuery.append("("+WildCardQuery.getSQL("tr.card_aceptor_name[37,38]", campInstTransFilterVo.getCtfMerchentState())+")");
        }
        if (campInstTransFilterVo.getCtfMerchentZipPCode()!= null){
           if (whereAndCount > 0)
           {
               recipientQuery.append(" AND ");
           } else {
                whereAndCount++;
            }

           recipientQuery.append("("+WildCardQuery.getSQL("tr.trans_geo_data[6,14]", campInstTransFilterVo.getCtfMerchentZipPCode())+")");
       }
       if (campInstTransFilterVo.getCtfMerchentCountry()!= null){
           if (whereAndCount > 0)
           {
               recipientQuery.append(" AND ");
           } else {
                whereAndCount++;
            }

           recipientQuery.append("("+WildCardQuery.getSQL("tr.card_aceptor_name[39,40]", campInstTransFilterVo.getCtfMerchentCountry())+")");
        }
//        if (campInstTransFilterVo.getCtfTransEpLocGroup()!= null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            } else {
//                        whereAndCount++;
//                    }

//            recipientQuery.append(WildCardQuery.getSQL("tr.trans_ep_loc_group", campInstTransFilterVo.getCtfTransEpLocGroup()));
//        }
        String finalRecipientQuery = null;
        if (whereAndCount == 0){
            finalRecipientQuery = recipientQuery.substring(0, recipientQuery.length() - 5);
            campMainLgr.log(I2cLogger.FINE, "\n Final Receipient Query --------> "+ finalRecipientQuery + "\n\n\n");
            return finalRecipientQuery;
        }
        campMainLgr.log(I2cLogger.FINE, "\n\n\n Receipient Query --------> "+ recipientQuery.toString() + "\n\n\n");
        return recipientQuery.toString();

    }




//    public String recipientTransQueryBuilder(int campaignId){
//
//        StringBuffer recipientQuery = new StringBuffer();
//        CampaignsVO campInstsVo = null;
////        CampaignCardFiltersVO campInstCardFilterVo = null;
//        CampaignTransFiltersVO campInstTransFilterVo = null;
////        CampaignInstDetailsVO campInstDetailVo = null;
//        int whereAndCount = 0;
//
//        CampaignDAO campaignDao = new CampaignDAO(instanceID, dbConn, campMainLgr);
//
//        campInstsVo = campaignDao.getCampaignInsts(campaignId);
////        campInstCardFilterVo = campaignDao.getCampInstCardFilters(campInstsVo.getCampaignInstId());
//        campInstTransFilterVo = campaignDao.getCampInstTransFilters(campInstsVo.getCampaignInstId());
//
//
//        if (campInstsVo.getCampRecipientFlag().equalsIgnoreCase("C")){
//            recipientQuery.append("SELECT DISTINCT c.card_srno recipient_id ");
//            recipientQuery.append("FROM cards c, camp_inst_trans_filters ctf, card_funds cf WHERE ");
//
//        } else if (campInstsVo.getCampRecipientFlag().equalsIgnoreCase("E")){
//            recipientQuery.append("SELECT DISTINCT e.employer_id_srno recipient_id ");
//            recipientQuery.append("FROM  cards c, employer_ids e ");
//            recipientQuery.append("WHERE c.employer_id = e.employer_id AND ");
//
//        } else if (campInstsVo.getCampRecipientFlag().equalsIgnoreCase("S")){
//            recipientQuery.append("SELECT DISTINCT e.stake_holder_sno recipient_id ");
//            recipientQuery.append("FROM cards c, stake_holders e ");
//            recipientQuery.append("WHERE c.stake_holder_id = e.stake_holder_id AND ");
//
//        }
//
//        if (campInstTransFilterVo.getCtfServiceId() != null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            }
//            else {
//                whereAndCount++;
//            }
//            recipientQuery.append("c.card_prg_id = ccf.card_prg_id AND ");
//            recipientQuery.append(WildCardQuery.getSQL("ccf.card_prg_id", campInstTransFilterVo.getCtfServiceId()));
//        }
//        if (campInstTransFilterVo.getCcfCardStatus()!= null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            }
//            recipientQuery.append("c.card_status = ccf.card_status AND ");
//            recipientQuery.append(WildCardQuery.getSQL("ccf.card_status", campInstTransFilterVo.getCcfCardStatus()));
//        }
//        if (campInstTransFilterVo.getCcfCardNo() != null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            }
//            recipientQuery.append("c.card_no = ccf.card_no AND ");
//            recipientQuery.append("'" + WildCardQuery.getSQL("ccf.card_no", campInstTransFilterVo.getCcfCardNo()) + "'");
//        }
//        if (campInstTransFilterVo.getCcfChBirthdayMmDd()!= null){
//            if (whereAndCount > 0)
//            {
//                            recipientQuery.append(" AND ");
//            }
//            recipientQuery.append("c.date_of_birth = ccf.ch_birthday_mmdd AND ");
//            recipientQuery.append(WildCardQuery.getSQL("ccf.ch_birthday_mmdd", campInstTransFilterVo.getCcfChBirthdayMmDd()));
//        }
//        if (campInstTransFilterVo.getCcfActDate()!= null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            }
//            recipientQuery.append("c.first_act_on = ccf.act_date AND ");
//            recipientQuery.append(WildCardQuery.getSQL("ccf.act_date", campInstTransFilterVo.getCcfActDate()));
//        }
//        if (campInstTransFilterVo.getCcfExpirationDate()!= null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            }
//            recipientQuery.append("c.expiry_on = ccf.expiration_date AND ");
//            recipientQuery.append(WildCardQuery.getSQL("ccf.expiration_date", campInstTransFilterVo.getCcfExpirationDate()));
//        }
//        if (campInstTransFilterVo.getCcfAvailBalance()!= null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            }
//            recipientQuery.append("cf.card_balance = ccf.avail_balance AND ");
//            recipientQuery.append("c.card_no = cf.card_no AND ");
//            recipientQuery.append(WildCardQuery.getSQL("ccf.avail_balance", campInstTransFilterVo.getCcfAvailBalance()));
//        }
//        if (campInstTransFilterVo.getCcfLedgerBalance()!= null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            }
//            recipientQuery.append("cf.ledger_balance = ccf.ledger_balance AND ");
//            recipientQuery.append("c.card_no = cf.card_no AND ");
//            recipientQuery.append(WildCardQuery.getSQL("ccf.ledger_balance", campInstTransFilterVo.getCcfLedgerBalance()));
//        }
//        if (campInstTransFilterVo.getCcfCity()!= null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            }
//            recipientQuery.append("c.city = ccf.city AND ");
//            recipientQuery.append(WildCardQuery.getSQL("ccf.city", campInstTransFilterVo.getCcfCity()));
//        }
//        if (campInstTransFilterVo.getCcfState()!= null){
//            if (whereAndCount > 0)
//           {
//               recipientQuery.append(" AND ");
//           }
//           recipientQuery.append("c.state_code = ccf.state AND ");
//           recipientQuery.append(WildCardQuery.getSQL("ccf.state", campInstTransFilterVo.getCcfState()));
//        }
//        if (campInstTransFilterVo.getCcfZipPostalCode()!= null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            }
//            recipientQuery.append("c.zip_postal_code = ccf.zip_postal_code AND ");
//            recipientQuery.append(WildCardQuery.getSQL("ccf.zip_postal_code", campInstTransFilterVo.getCcfZipPostalCode()));
//        }
//        if (campInstTransFilterVo.getCcfCountry()!= null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            }
//            recipientQuery.append("c.country_code = ccf.country AND ");
//            recipientQuery.append(WildCardQuery.getSQL("ccf.country", campInstTransFilterVo.getCcfCountry()));
//        }
//        if (campInstTransFilterVo.getCcfStakeHolderId()!= null){
//            if (whereAndCount > 0)
//            {
//                recipientQuery.append(" AND ");
//            }
//            recipientQuery.append("c.stake_holder_id = ccf.stake_holder_id AND ");
//            recipientQuery.append(WildCardQuery.getSQL("ccf.stake_holder_id", campInstTransFilterVo.getCcfStakeHolderId()));
//        }
//        return recipientQuery.toString();
//
//        /*************************************/
////        if (campInstsVo.getCampIsForEmail().equalsIgnoreCase("Y")){
////
////        } else if (campInstsVo.getCampIsForFax().equalsIgnoreCase("Y")){
////
////        } else if (campInstsVo.getCampIsForWebsite().equalsIgnoreCase("Y")){
////
////        } else if (campInstsVo.getCampIsForSms().equalsIgnoreCase("Y")){
////
////        } else if (campInstsVo.getCampIsForIvr().equalsIgnoreCase("Y")){
////
////        }
////        return recipientQuery.toString();
//    }


}
