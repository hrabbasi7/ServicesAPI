    package com.i2c.services;
    
    import java.sql.Connection;
    
    import com.i2c.services.handlers.CardsServiceHandler;
    import com.i2c.services.handlers.FinancialServiceHandler;
    import com.i2c.services.home.CardsServiceHome;
    import com.i2c.services.util.CommonUtilities;
    import com.i2c.services.util.Constants;
    import com.i2c.services.util.LogLevel;
    
    public class ServicesManager {
        private Connection con = null;   
        private boolean transactionExternallyManaged = false;
        
    
    
        private ServicesManager(String servicesLogPath) {
            Constants.LOG_FILE_PATH = servicesLogPath;
            Constants.HSM_LOG_FILE_PATH = servicesLogPath;
            Constants.FUR_LOG_FILE_PATH = servicesLogPath;
            Constants.TRANSFERAPI_LOG_PATH = servicesLogPath
                    + java.io.File.separator + "transferapi";
        } // end Constructor ServicesHandler
    
    
    
    
        public static ServicesManager getInstance(String servicesLogPath,Connection _con
                ,boolean isTransactionExternallyManaged) {
            ServicesManager manager = new ServicesManager(servicesLogPath);
    
            try {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_FINEST),
                        "Connection -- > " + _con + " && My Conn -- > "
                                + manager.con);
                manager.con = _con;
                manager.setTransactionExternallyManaged(isTransactionExternallyManaged);
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Connection -- > " + _con + " && My Conn -- > "
                                + manager.con + "&& Is Transaction Externally Managed--->" + isTransactionExternallyManaged);
            } catch(Exception exp) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_SEVERE),
                        "Exception while changing the connection's autocommit...."
                                + exp.getMessage());
            }
            return manager;
        }
    
    
    
    
        public ServicesResponseObj creditFunds(ServicesRequestObj requestObj)
                throws Exception {
            // make the response object
            ServicesResponseObj respObj = new ServicesResponseObj();
            CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);
    
            try {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Credit Funds --- Current Auto Commit Status of passed database connection --->"
                                        + con.getAutoCommit() + "<---Transaction Extrnally Managed Flag--->" + isTransactionExternallyManaged());
    
                if(con.getAutoCommit()) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Credit Funds --- Beginning Transaction...");
                    con.setAutoCommit(false);                
                }
    
                if(requestObj.getAmount() != null
                        && requestObj.getAmount().trim().length() > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Credit Funds --- Validating provided Amount --->"
                                    + requestObj.getAmount());
                    try {
                        double amt = Double.parseDouble(requestObj.getAmount()
                                .trim());
                        if(amt < 0) {
                            CommonUtilities.getLogger().log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Credit Funds --- Invalid Amount Received--->"
                                            + requestObj.getAmount());
                            respObj.setRespCode("13");
                            respObj.setRespDesc("Invalid Amount");
                            return respObj;
                        }
                    } catch(NumberFormatException ex1) {
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Credit Funds--- Invalid Amount Received--->"
                                        + requestObj.getAmount());
                        respObj.setRespCode("13");
                        respObj.setRespDesc("Invalid Amount");
                        return respObj;
                    }
                } else {
                    respObj.setRespCode("13");
                    respObj.setRespDesc("Invalid Amount");
                    return respObj;
                }
    
                if(requestObj.getServiceId() == null
                        || requestObj.getServiceId().trim().length() == 0) {
                    String cardPrgId = serviceHome.getCardProgramID(requestObj
                            .getCardNo());
    
                    String serviceId = serviceHome.huntForServiceId(requestObj
                            .getDeviceType(), "21", "00", cardPrgId);
    
                    if(serviceId != null && serviceId.trim().length() > 0
                            && !serviceId.equals(""))
                        requestObj.setServiceId(serviceId);
                    else
                        requestObj.setServiceId("SW_DEPOSIT");
                }
    
                respObj = FinancialServiceHandler.getInstance(con).addFunds(
                        requestObj);
                // commit all the work
                if(isTransactionExternallyManaged() == false && con.getAutoCommit() == false) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Credit Funds --- Committing Work...");
                    con.commit();
                }
                // return the result
                return respObj;
            } // end try
            catch(Exception exp) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_SEVERE),
                        "Exception  -- > " + exp.getMessage());
                try {
                    if(con != null)
                        con.rollback();
                } // end try
                catch(Exception ex) {
                } // end
    
                respObj.setRespCode("96");
                respObj.setRespDesc("System Error");
                respObj.setExcepMsg(exp.getMessage());
                respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
                respObj.setLogFilePath("More information can be found at--->"
                        + Constants.EXACT_LOG_PATH);
                return respObj;
            } // end catch
        } // end creditFunds
    
    
    
    
        public ServicesResponseObj debitFunds(ServicesRequestObj requestObj)
                throws Exception {
            // make the response object
            ServicesResponseObj respObj = new ServicesResponseObj();
            CardsServiceHome serviceHome = CardsServiceHome.getInstance(con);
            try {
    
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Debit Funds --- Current Auto Commit Status of passed database connection --->"
                                + con.getAutoCommit() + "<---Transaction Extrnally Managed Flag--->" + isTransactionExternallyManaged());
    
                if(con.getAutoCommit()) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Debit Funds --- Beginning Transaction...");
                    con.setAutoCommit(false);
                    
                }
    
                if(requestObj.getAmount() != null
                        && requestObj.getAmount().trim().length() > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Debit Funds --- Validating provided Amount --->"
                                    + requestObj.getAmount());
                    try {
                        double amt = Double.parseDouble(requestObj.getAmount()
                                .trim());
                        if(amt < 0) {
                            CommonUtilities.getLogger().log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Debit Funds --- Invalid Amount Received--->"
                                            + requestObj.getAmount());
                            respObj.setRespCode("13");
                            respObj.setRespDesc("Invalid Amount");
                            return respObj;
                        }
                    } catch(NumberFormatException ex1) {
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Debit Funds--- Invalid Amount Received--->"
                                        + requestObj.getAmount());
                        respObj.setRespCode("13");
                        respObj.setRespDesc("Invalid Amount");
                        return respObj;
                    }
                } else {
                    respObj.setRespCode("13");
                    respObj.setRespDesc("Invalid Amount");
                    return respObj;
                }
    
                // make the amount to negative
                double nBal = Double.parseDouble(requestObj.getAmount());
    
                if(nBal > 0) {
                    nBal *= -1;
                }
                requestObj.setAmount(nBal + "");
    
                if(requestObj.getServiceId() == null
                        || requestObj.getServiceId().trim().length() == 0) {
    
                    String cardPrgId = serviceHome.getCardProgramID(requestObj
                            .getCardNo());
    
                    String serviceId = serviceHome.huntForServiceId(requestObj
                            .getDeviceType(), "01", "00", cardPrgId);
    
                    if(serviceId != null && !serviceId.equals("")
                            && serviceId.trim().length() > 0)
                        requestObj.setServiceId(serviceId);
                    else
                        requestObj.setServiceId("WS_WITHD");
    
                }
    
                respObj = FinancialServiceHandler.getInstance(con).addFunds(
                        requestObj);
                if(isTransactionExternallyManaged() == false && con.getAutoCommit() == false) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Credit Funds --- Committing Work...");
                    con.commit();
                }
                // return the result
                return respObj;
            } // end try
            catch(Exception exp) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_SEVERE),
                        "Exception  -- > " + exp.getMessage());
                try {
                    if(con != null)
                        con.rollback();
                } // end try
                catch(Exception ex) {
                } // end
    
                respObj.setRespCode("96");
                respObj.setRespDesc("System Error");
                respObj.setExcepMsg(exp.getMessage());
                respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
                respObj.setLogFilePath("More information can be found at--->"
                        + Constants.EXACT_LOG_PATH);
                return respObj;
            } // end catch
        } // debitFunds
    
    
    
    
        public ServicesResponseObj activateCard(ServicesRequestObj requestObj)
                throws Exception {
            // make the response object
            ServicesResponseObj respObj = new ServicesResponseObj();
    
            try {
                CommonUtilities
                        .getLogger()
                        .log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Activate Card --- Current Auto Commit Status of passed database connection --->"
                                        + con.getAutoCommit() + "<---Transaction Extrnally Managed Flag--->" + isTransactionExternallyManaged());
    
                if(con.getAutoCommit()) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Activate Card --- Beginning Transaction...");
                    con.setAutoCommit(false);
                    
                }
    
                if(requestObj.getAmount() != null
                        && requestObj.getAmount().trim().length() > 0) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Activate Card --- Validating provided Amount --->"
                                    + requestObj.getAmount());
                    try {
                        double amt = Double.parseDouble(requestObj.getAmount()
                                .trim());
                        if(amt < 0) {
                            CommonUtilities.getLogger().log(
                                    LogLevel.getLevel(Constants.LOG_CONFIG),
                                    "Activate Card --- Invalid Amount Received--->"
                                            + requestObj.getAmount());
                            respObj.setRespCode("13");
                            respObj.setRespDesc("Invalid Amount");
                            return respObj;
                        }
                    } catch(NumberFormatException ex1) {
                        CommonUtilities.getLogger().log(
                                LogLevel.getLevel(Constants.LOG_CONFIG),
                                "Activate Card --- Invalid Amount Received--->"
                                        + requestObj.getAmount());
                        respObj.setRespCode("13");
                        respObj.setRespDesc("Invalid Amount");
                        return respObj;
                    }
                } // Validating Amount
    
                respObj = CardsServiceHandler.getInstance(con).activateCard(
                        requestObj);
                if(isTransactionExternallyManaged() == false && con.getAutoCommit() == false) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Credit Funds --- Committing Work...");
                    con.commit();
                }
                // return the result
                return respObj;
            } // end try
            catch(Exception exp) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_SEVERE),
                        "Exception  -- > " + exp.getMessage());
                try {
                    if(con != null)
                        con.rollback();
                } // end try
                catch(Exception ex) {
                } // end
    
                respObj.setRespCode("96");
                respObj.setRespDesc("System Error");
                respObj.setExcepMsg(exp.getMessage());
                respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
                respObj.setLogFilePath("More information can be found at--->"
                        + Constants.EXACT_LOG_PATH);
    
                return respObj;
            }
        } // end activateCard
    
    
    
    
        public ServicesResponseObj assignCard(ServicesRequestObj requestObj)
                throws Exception {
            ServicesResponseObj respObj = new ServicesResponseObj();
            try {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Assign Card --- Current Auto Commit Status of passed database connection --->"
                                + con.getAutoCommit() + "<---Transaction Extrnally Managed Flag--->" + isTransactionExternallyManaged());
    
                if(con.getAutoCommit()) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Assign Card --- Beginning Transaction...");
                    con.setAutoCommit(false);
                    
                }
    
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_CONFIG),
                        "Method for assigning card...");
                respObj = CardsServiceHandler.getInstance(con).assignCard(
                        requestObj);
                if(isTransactionExternallyManaged() == false && con.getAutoCommit() == false) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_CONFIG),
                            "Credit Funds --- Committing Work...");
                    con.commit();
                }
                return respObj;
            } catch(Exception exp) {
                CommonUtilities.getLogger().log(
                        LogLevel.getLevel(Constants.LOG_SEVERE),
                        "Exception  -- > " + exp);
                try {
                    if(con != null)
                        con.rollback();
                } catch(Exception ex) {
                    CommonUtilities.getLogger().log(
                            LogLevel.getLevel(Constants.LOG_SEVERE),
                            "Exception in rolling back -- > " + ex);
                }
                respObj.setRespCode("00");
                respObj.setRespDesc("OK");
                respObj.setIsCardAssigned(false);
                respObj.setExcepMsg(exp.getMessage());
                respObj.setStkTrace(CommonUtilities.getStackTrace(exp));
                respObj.setLogFilePath("More information can be found at--->"
                        + Constants.EXACT_LOG_PATH);
                return respObj;
            }
        }
    
    
    
        
        private boolean isTransactionExternallyManaged() {
            return transactionExternallyManaged;
        }
    
    
    
    
        private void setTransactionExternallyManaged(boolean transactionExternallyManaged) {
            this.transactionExternallyManaged = transactionExternallyManaged;
        }
    }
