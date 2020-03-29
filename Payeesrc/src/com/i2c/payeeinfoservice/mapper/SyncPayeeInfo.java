package com.i2c.payeeinfoservice.mapper;

import com.i2c.payeeinfoservice.vo.PayeeVO;
import com.i2c.payeeinfoservice.dao.PayeesDAO;
import java.util.logging.Logger;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>Title: Payee Information Service</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Innovative Pvt. Ltd.</p>
 *
 * @author Haroon ur Rashid Abbasi
 * @version 1.0
 */

public class SyncPayeeInfo implements PayeeInfoInterface {
     private Logger lgr;
     private Connection dbConn = null;
     private String instanceID = null;

     public SyncPayeeInfo(){}

     public SyncPayeeInfo(String instanceID,
                               Connection dbConn,
                               String logPath,
                               Logger logger){
         this.lgr = logger;
         this.dbConn = dbConn;
         this.instanceID = instanceID;
     }

     public void updatePayeeData(PayeeVO payeeVo) throws Exception{
         PayeesDAO payeeDao = new PayeesDAO(instanceID,dbConn, lgr);
         long payeeSerialNo = -1;
         PayeeMapper payeeMapper = new PayeeMapper(instanceID,dbConn,"",lgr);
         payeeSerialNo = payeeDao.getPayeeSerialNo(payeeVo);
         if (payeeSerialNo != -1){
             payeeMapper.updatePayeeInDbFromPAR(payeeVo, payeeDao, payeeSerialNo);
         }else{
             throw new Exception("Payee Serial No does not ex  it or Payee Vo Null");
         }
     }

    public void updatePayeeData(ArrayList payeeVoArray) throws Exception {
        PayeesDAO payeeDao = new PayeesDAO(instanceID,dbConn, lgr);
        long payeeSerialNo = -1;
         PayeeMapper payeeMapper = new PayeeMapper(instanceID,dbConn,"",lgr);
         for (int i = 0; i < payeeVoArray.size(); i++){
             PayeeVO payeeVo = (PayeeVO) payeeVoArray.get(i);
             payeeSerialNo = payeeDao.getPayeeSerialNo(payeeVo);
             if (payeeSerialNo != -1){
                 payeeMapper.updatePayeeInDbFromPAR(payeeVo, payeeDao, payeeSerialNo);
             }else{
                 throw new Exception("Payee Serial No does not exsit in ArrayList or Payee Vo Null");
             }
         }
     }

    public void updatePayeeData(PayeeVO[] payeeVoArray, int arraySize) throws Exception {
        PayeesDAO payeeDao = new PayeesDAO(instanceID,dbConn, lgr);
        long payeeSerialNo = -1;
         PayeeMapper payeeMapper = new PayeeMapper(instanceID,dbConn,"",lgr);
         for (int i = 0; i < arraySize; i++){
             payeeSerialNo = payeeDao.getPayeeSerialNo(payeeVoArray[i]);
             if (payeeSerialNo != -1){
                 payeeMapper.updatePayeeInDbFromPAR(payeeVoArray[i], payeeDao, payeeSerialNo);
             }else{
                 throw new Exception("Payee Serial No does not exsit in ArrayList or Payee Vo Null");
             }

         }
    }
}
