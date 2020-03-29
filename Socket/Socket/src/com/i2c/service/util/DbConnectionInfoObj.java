/*
 * Created on Jul 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.service.util;

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
	private String connectionName = null;
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

}
