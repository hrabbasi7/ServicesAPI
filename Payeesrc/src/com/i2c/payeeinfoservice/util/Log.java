/*
 * Created on Sep 8, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.i2c.payeeinfoservice.util;

import java.util.logging.*;
import java.io.*;
import java.util.*;


/**
 * @author srashid
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Log {

	private static Vector loggerList = new Vector();

	static {
		System.out.println(" Logging Static Block ");
		try {
			//Loading the log parameters
			try {
				LogAttributes.execute();
			} catch(Exception e){}

			//Calling method for preparing common log information
			createCommonLogInformation();
			//Call method for preparing instance and application specific logging
			populateLoggingInformation();
		} catch(Exception e) {
			System.out.println(" Exception in Static Block -->"+e);
		}//end catch
	}//end static block

	/**
	 * Default Constructor
	 * @throws IOException
	 */
	private Log() throws IOException{
		//Calling method for preparing common log information
		createCommonLogInformation();
		//Call method for preparing instance and application specific logging
		populateLoggingInformation();
	}//end constrcutor
	  /**
	   * Method for populating the common logging information
	   *
	   */
	  private static void createCommonLogInformation(){
		Logger logger = null;
		FileHandler fh = null;
		File logFilePath = null;
		InstanceLoggerInfoObj instanceAppLogInfo = null;
		try {
			System.out.println(" Common Logging Information Method -->LogAttributes.getLogFilePath() "+LogAttributes.getLogFilePath()+"<----Constants.COMMON_LOG_INFO -->"+Constants.COMMON_LOG_INFO);
			//Create the Rooot Logging Directory
			String path = LogAttributes.getLogFilePath() + File.separator + Constants.COMMON_LOG_INFO;
			System.out.println(" Common Logging Information Method -->Log Path Construcrted---->  " + path);
                        logFilePath =  new File(path);
			if (!logFilePath.exists())
				System.out.println(" Creating Common Logging Directory "+path+"-->"+logFilePath.mkdir());
			//Create Log File
			String logFile = path + File.separator + "cards-%g.log" ;
			System.out.println("Using log file = " + logFile);
			logger = Logger.getLogger(Constants.LOG_CONTEXT_NAME);
			fh = new FileHandler(logFile,Constants.LOG_FILE_SIZE,Constants.LOG_FILE_NO);
			SimpleFormatter simpleFormatter = new SimpleFormatter();
			fh.setFormatter(simpleFormatter);
			logger.addHandler(fh);

			// Request that every detail gets logged.
			logger.setLevel(LogLevel.getLevel(LogAttributes.getDebugLevel()));
			//Creating an instance for logging the information
			instanceAppLogInfo = new InstanceLoggerInfoObj();
			//Saving the information
			instanceAppLogInfo.setFileHandler(fh);
			instanceAppLogInfo.setAppName(Constants.COMMON_LOG_INFO);
			instanceAppLogInfo.setInstanceName(Constants.COMMON_LOG_INFO);
			instanceAppLogInfo.setLogger(logger);
			instanceAppLogInfo.setFileName(logFile);
			//Add the logger information into the List
			loggerList.addElement(instanceAppLogInfo);
			System.out.println(" The Loggeer List Size is --->"+loggerList.size());
		} catch(Exception e) {
			System.out.println("Exception in Common Logging Information -->"+e);
		}//end catch

	  }//end method

	  /**
	   * Method for loading the Application and Instance Logging Information
	   */
	  private static void populateLoggingInformation() {
		Logger logger = null;
		FileHandler fh = null;
		File logFilePath = null;
		InstanceLoggerInfoObj instanceAppLogInfo = null;
	  	try {
			System.out.println(" Pouplate Logging Information Method --> Application Array Size "+Constants.APPLICATIONS_NAME_ARRAY.length+"<--LoadProperties.instanceConnectionsTable.size()-->"+LoadProperties.instanceConnectionsTable.size());
                        Enumeration instanceList = LoadProperties.instanceConnectionsTable.keys();
			while (instanceList.hasMoreElements()) {
                          String instanceName = instanceList.nextElement().toString();
				System.out.println(" Create Logging Mechanism for Instance -->"+instanceName);
				System.out.println(" Log File Directory Path -->"+LogAttributes.getLogFilePath());
				for (int i =0 ; i < Constants.APPLICATIONS_NAME_ARRAY.length ; ++i) {
					//Creating an instance for logging the information
					instanceAppLogInfo = new InstanceLoggerInfoObj();
					String applicationName = Constants.APPLICATIONS_NAME_ARRAY[i];
					System.out.println(" Create Logging Mechanism for Application -->"+applicationName);
					//Create the Rooot Logging Directory
					logFilePath =  new File(LogAttributes.getLogFilePath());
					if (!logFilePath.exists())
						System.out.println(" Creating Directory "+LogAttributes.getLogFilePath()+"-->"+logFilePath.mkdir());
					//Create the Instance Logging Directory
					logFilePath =  new File(LogAttributes.getLogFilePath() + File.separator + instanceName);
					if (!logFilePath.exists())
						System.out.println(" Creating Directory "+LogAttributes.getLogFilePath() + File.separator +  instanceName +"-->"+logFilePath.mkdir());
					String path = LogAttributes.getLogFilePath() + File.separator + instanceName + File.separator + applicationName ;
					System.out.println(" Full Log File Directory Path --->"+path);
					//Create the Log File Directory Path If Already exist
					logFilePath =  new File(path);
					if (!logFilePath.exists())
						System.out.println(" Creating Directory -->"+logFilePath.mkdir());
					//Create Log File
					String logFile = path + File.separator + applicationName +"-%g.log" ;
					System.out.println("Using log file = " + logFile);
					logger = Logger.getLogger(Constants.LOG_CONTEXT_NAME + instanceName + applicationName);
					fh = new FileHandler(logFile,Constants.LOG_FILE_SIZE,Constants.LOG_FILE_NO);
					SimpleFormatter simpleFormatter = new SimpleFormatter();
					fh.setFormatter(simpleFormatter);
					logger.addHandler(fh);
					// Request that every detail gets logged.
					logger.setLevel(LogLevel.getLevel(LogAttributes.getDebugLevel()));
					//Saving the information
					instanceAppLogInfo.setFileHandler(fh);
					instanceAppLogInfo.setAppName(applicationName);
					instanceAppLogInfo.setInstanceName(instanceName);
					instanceAppLogInfo.setLogger(logger);
					instanceAppLogInfo.setFileName(logFile);
					//Add the logger information into the List
					loggerList.addElement(instanceAppLogInfo);
				}//end inner for
			}//end for
			System.out.println(" The Loggeer List Size is --->"+loggerList.size());
	  	} catch(Exception e) {
	  		System.out.println("Exception in Populating Logging Information -->"+e);
	  	}//end catch
	  }//end method

	/**
	 * Method for getting the logger information
	 * @param application: application whose logger to be obtained
	 * @param instance: instance whose logger to be obtained
	 * @return
	 */
	public static Logger getLogger(String application,String instance) {
		InstanceLoggerInfoObj instanceAppLogInfo = null;
		Logger logger = null;
		try {
			//System.out.println("   getLogObj Method  application-->"+application+"<---instance--->"+instance);
			//Getting the Appropate Logger
			for (int i = 0; i < loggerList.size() ; ++i) {
				instanceAppLogInfo = (InstanceLoggerInfoObj)loggerList.elementAt(i);
				//System.out.println(" Instance Name -->"+instanceAppLogInfo.getInstanceName()+"<---- Appplication Name -->"+instanceAppLogInfo.getAppName());
				//Checkign the Instance and the Application
				if (instanceAppLogInfo.getInstanceName().trim().equalsIgnoreCase(instance.trim()) &&
					instanceAppLogInfo.getAppName().trim().equalsIgnoreCase(application.trim())) {
					//System.out.println(" Found Instance and Application Logger ");
					logger = instanceAppLogInfo.getLogger();
					break;
				}//end if
			}//end for
		} catch (Exception e) {
			System.out.println("Exception in getLogObj Method -->"+e);
		}//end catch
		//System.out.println(" Returning Logger -->"+logger);
		return logger;
	 }//end get log object method

	  /**
	   * Method for closing the logger information
	   */
	  public void close() {
		InstanceLoggerInfoObj instanceAppLogInfo = null;
	  	try {
			System.out.println(" Close Logger Information -->"+loggerList.size());
			for (int i = 0; i < loggerList.size() ; ++i) {
				try {
					instanceAppLogInfo = (InstanceLoggerInfoObj)loggerList.elementAt(i);
					System.out.println(" Instance Name -->"+instanceAppLogInfo.getInstanceName()+"<---- Appplication Name -->"+instanceAppLogInfo.getAppName());
					if (instanceAppLogInfo.getFileHandler() != null)
						instanceAppLogInfo.getFileHandler().close();
					instanceAppLogInfo.setFileHandler(null);
					instanceAppLogInfo.setLogger(null);
					instanceAppLogInfo = null;
				} catch (Exception ex){}
			}//end for
	  	} catch (Exception e) {
			System.out.println(" Exception in Clsoing Logger -->"+e);
	  	}//end catch
	  }//end close
}//end class
