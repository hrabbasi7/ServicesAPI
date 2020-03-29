package com.i2c.campaigngenservice.util;

import java.util.logging.*;
import com.i2c.campaigngenservice.util.*;
/**
 * Form bean for a Struts application.
 * Users may access 6 fields on this form:
 * <ul>
 * <li>fileSize - [your comment here]
 * <li>appName - [your comment here]
 * <li>instanceName - [your comment here]
 * <li>logger - [your comment here]
 * <li><no name> - [your comment here]
 * <li>fileName - [your comment here]
 * </ul>
 * @version 	1.0
 * @author
 */
public class InstanceLoggerInfoObj {

	private String appName = null;
	private String fileSize = null;
	private String instanceName = null;
	private Logger logger = null;
	private String fileName = null;
	FileHandler fileHandler = null;
	/**
	 * Get appName
	 * @return String
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * Set appName
	 * @param <code>String</code>
	 */
	public void setAppName(String a) {
		this.appName = a;
	}

	/**
	 * Get fileSize
	 * @return String
	 */
	public String getFileSize() {
		return fileSize;
	}

	/**
	 * Set fileSize
	 * @param <code>String</code>
	 */
	public void setFileSize(String f) {
		this.fileSize = f;
	}

	/**
	 * Get instanceName
	 * @return String
	 */
	public String getInstanceName() {
		return instanceName;
	}

	/**
	 * Set instanceName
	 * @param <code>String</code>
	 */
	public void setInstanceName(String i) {
		this.instanceName = i;
	}

	/**
	 * Get logger
	 * @return Logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Set logger
	 * @param <code>Logger</code>
	 */
	public void setLogger(Logger l) {
		this.logger = l;
	}

	/**
	 * Get fileName
	 * @return String
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Set fileName
	 * @param <code>String</code>
	 */
	public void setFileName(String f) {
		this.fileName = f;
	}

	/**
	 * @return
	 */
	public FileHandler getFileHandler() {
		return fileHandler;
	}

	/**
	 * @param handler
	 */
	public void setFileHandler(FileHandler handler) {
		fileHandler = handler;
	}

}
