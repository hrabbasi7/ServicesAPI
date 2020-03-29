package com.i2c.campaigngenservice.util;
/*
 * Created on Jul 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */


import java.util.Hashtable;

/**
 * @author barshad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class DbConnectionInfoObj {

	private String connectionString = null;
	private String userID = null;
	private String passwod = null;
        private String fcConnectionString = null;
        private String fcUserID = null;
        private String fcPasswod = null;
	private String connectionName = null;
        private String serviceUserID = null;
        private String achOutputPath = null;
	private Hashtable connectionPool = new Hashtable();

	/**
	 * @return
	 */
	public String getConnectionName() {
		return connectionName;
	}

	/**
	 * @return
	 */
	public String getConnectionString() {
		return connectionString;
	}

	/**
	 * @return
	 */
	public String getPasswod() {
		return passwod;
	}

	/**
	 * @return
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param string
	 */
	public void setConnectionName(String string) {
		connectionName = string;
	}

	/**
	 * @param string
	 */
	public void setConnectionString(String string) {
		connectionString = string;
	}

	/**
	 * @param string
	 */
	public void setPasswod(String string) {
		passwod = string;
	}

	/**
	 * @param string
	 */
	public void setUserID(String string) {
		userID = string;
	}

	/**
	 * @return
	 */
	public Hashtable getConnectionPool() {
		return connectionPool;
	}

	/**
	 * @param hashtable
	 */
	public void setConnectionPool(Hashtable hashtable) {
		connectionPool = hashtable;
	}

        public String getServiceUserID() {
          return serviceUserID;
        }

        public void setServiceUserID(String serviceUserID) {
          this.serviceUserID = serviceUserID;
        }
  public String getAchOutputPath() {
    return achOutputPath;
  }
  public void setAchOutputPath(String achOutputPath) {
    this.achOutputPath = achOutputPath;
  }
  public String getFcConnectionString() {
    return fcConnectionString;
  }
  public void setFcConnectionString(String fcConnectionString) {
    this.fcConnectionString = fcConnectionString;
  }
  public String getFcPasswod() {
    return fcPasswod;
  }
  public String getFcUserID() {
    return fcUserID;
  }
  public void setFcPasswod(String fcPasswod) {
    this.fcPasswod = fcPasswod;
  }
  public void setFcUserID(String fcUserID) {
    this.fcUserID = fcUserID;
  }

}
