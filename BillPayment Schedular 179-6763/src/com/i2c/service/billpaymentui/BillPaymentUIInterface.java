package com.i2c.service.billpaymentui;

import java.sql.*;
import com.i2c.service.util.DatabaseHandler;
import com.i2c.service.billpaymentschedularservice.PayeeService.PayeeServiceDAO;
import com.i2c.service.util.Constants;
import java.util.logging.Logger;
import com.i2c.service.billpaymentschedularservice.PayeeService.PayeeServiceThread;
import java.io.File;
import com.i2c.service.billpaymentschedularservice.ServiceMain;
import java.io.IOException;
import com.i2c.utils.logging.I2cLogger;

/**
 * <p>Title: BillPayment Scheduler Service</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright i2cinc(c) 2007</p>
 *
 * <p>Company: I2cinc</p>
 *
 * @author Haroon ur Rashid Abbasi
 * @version 1.0
 */



public class BillPaymentUIInterface {
  private Logger lgr = null;
  public BillPaymentUIInterface(Logger lgr) throws IOException {
    this.lgr = lgr;

    ServiceMain.loadInitSetting();
  }

  public String processInstancePayee(Connection dbConn, String instanceName) throws Exception {
    PayeeServiceDAO  payeeServiceDAO = new PayeeServiceDAO(instanceName, dbConn, lgr);

//    ftpVo = payeeDao.getPayeeFtpInfo();
//
//    if (((Constants.PAYEE_OUTPUT_PATH != null) &&
//         (Constants.PAYEE_OUTPUT_PATH.trim().length() > 0)) &&
//        ((!ftpVo.getFtpLocalPath().equals("")) &&
//         (ftpVo.getFtpLocalPath() != null))) {
//
//        ouputPath.append(ftpVo.getFtpLocalPath() + File.separator +
//                         Constants.PAYEE_OUTPUT_PATH + File.separator +
//                         instanceName + File.separator +
//                         Constants.DOWNLOAD_FOLDER);
//
//        lgr.log(I2cLogger.INFO,
//                   "PayeeServiceThread --- invokePayeeProcessing --- Output Path to be passed--->" +
//                   ouputPath.toString());
//
//        //        Constants.PAYEE_FTP_DOWNLOAD_FOLDER = ouputPath.toString();
//        if (!new File(ouputPath.toString()).exists()) {
//            lgr.log(I2cLogger.CONFIG,
//                      "PayeeServiceThread --- invokePayeeProcessing --- Creating OutPut Path Folder(s)--->" +
//                      new File(ouputPath.toString()).mkdirs());
//            }
//          }
//          else {
//            lgr.log(I2cLogger.CONFIG,
//                    "PayeeServiceThread --- invokePayeeProcessing --- PayeeServiceThread Output Path value does not exist in DB LocalPath and Configuration file-->" +
//                    Constants.PAYEE_OUTPUT_PATH);
//
//            throw new Exception(
//                "PayeeServiceThread Path value does not exist in configuration file--->" +
//                Constants.PAYEE_OUTPUT_PATH);
//      }
//
//      String movePath = ftpVo.getFtpLocalPath() + File.separator +
//                        Constants.PAYEE_OUTPUT_PATH + File.separator +
//                        instanceName + File.separator +
//                        Constants.PROCESSED_FOLDER;
//
//      // if Payee File exist in Download and Processed
//      boolean fileInDownload = new File(ouputPath.toString()).exists();
//      boolean fileInProcessed = new File(movePath + File.separator + payeeFileName).exists();
//
//
//      String payeeFileName = new String(ftpVo.getFilePreFix() +
//                                         ftpVo.getFilePostFix());
//       ouputPath.append(File.separator + payeeFileName);
//
//      PayeePopulator(dbConn, logPath, ouputPath);

    return "";
  }

  public String processAllPayee(String instanceName)throws Exception {

      Connection dbConn = DatabaseHandler.getMasterConnection("BillPaymentUIInterface",
          instanceName,
          Constants.CARD_INSTANCE_DB_CONN);

      PayeeServiceDAO payeeServiceDAO = new PayeeServiceDAO(instanceName, dbConn, lgr);

      PayeeServiceThread payeeService = new PayeeServiceThread(instanceName, lgr, lgr);
      StringBuffer payeeTransStatus = payeeService.invokePayeeProcessing(dbConn, payeeServiceDAO);

    return payeeTransStatus.toString();
  }

  public String paymentFileGeneration(Connection dbConn, String instanceId, String fileDate) throws Exception{
    return "";
  }

  public String paymentFileUpload(Connection dbConn, String instanceId, String fileDate) throws Exception {
    return "";
  }

  public String processFile(Connection dbConn, String instanceId, String fileDate, String fileType) throws Exception {
    if (fileType.equalsIgnoreCase("P")){

    } else if (fileType.equalsIgnoreCase("R")){

    } else if (fileType.equalsIgnoreCase("A")){

    }

    return "";
  }
  // download payee, response and adjustment
  public String downloadFile(Connection dbConn, String instanceId, String fileDate, String fileType)throws Exception {
    if (fileType.equalsIgnoreCase("P")){

    } else if (fileType.equalsIgnoreCase("R")){

    } else if (fileType.equalsIgnoreCase("A")){

    }

    return "";
  }

  public boolean checkFile(String instanceId, String fileDate, String onLocation, String fileType)throws Exception {
    if (fileType.equalsIgnoreCase("P")) {

    } else if (fileType.equalsIgnoreCase("R")) {

    } else if (fileType.equalsIgnoreCase("A")) {

    }

    return true;
  }



}
