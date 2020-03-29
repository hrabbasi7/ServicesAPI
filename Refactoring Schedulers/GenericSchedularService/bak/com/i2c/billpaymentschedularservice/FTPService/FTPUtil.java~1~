package com.i2c.service.billpaymentschedularservice.FTPService;

import com.i2c.service.util.CommonUtilities;
import com.i2c.service.util.LogLevel;
import com.i2c.service.util.Constants;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FTPUtil {

  synchronized public static boolean isFilePresent(I2cFTP ftpServer,
      FTPInfoVO ftpVo) throws Exception {

    boolean isExist = false;
    String paymentFileName = null;

    paymentFileName = ftpVo.getPaymentFilePrefix() +
        CommonUtilities.getCurrentFormatDate("MMddyyyy");

    isExist = ftpServer.listFiles(paymentFileName);
    return isExist;
  }

}
