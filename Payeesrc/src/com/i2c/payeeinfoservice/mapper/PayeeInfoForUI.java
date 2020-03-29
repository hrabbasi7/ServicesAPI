package com.i2c.payeeinfoservice.mapper;

import java.util.logging.Logger;
import java.sql.Connection;

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
public class PayeeInfoForUI {
    /****** Interface for UI for Manual Insert and Update of Payee*******/
    public PayeeInfoForUI(String instanceID,
                          Connection dbConn,
                          String logPath,
                          String payeeFileName,
                          Logger logger) throws Exception {

        PayeeMapper payeeInterfaceForUI =
                new PayeeMapper(instanceID,
                                dbConn,
                                payeeFileName,
                                logPath,
                                logger);

        payeeInterfaceForUI.payeePopulater();
    }
   /********************************************************************/
}
