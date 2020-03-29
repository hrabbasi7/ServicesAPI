package com.i2c.billpaymentschedularservice.model;
/*
 * Created on Jul 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */


import java.util.Hashtable;

/**
 * @author hrabbasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class DbConnectionInfoVO {

	private String connectionString;
	private String userID;
	private String passwod;
        private String fcConnectionString;
        private String fcUserID;
        private String fcPasswod;
	private String connectionName;
        private String serviceUserID;
        private String achOutputPath;
	private Hashtable connectionPool;

        public void reset() {
          connectionString = null;
          userID = null;
          passwod = null;
          fcConnectionString = null;
          fcUserID = null;
          fcPasswod = null;
          connectionName = null;
          serviceUserID = null;
          achOutputPath = null;
          connectionPool = new Hashtable();
        }

        public DbConnectionInfoVO(){
          reset();
        }

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
