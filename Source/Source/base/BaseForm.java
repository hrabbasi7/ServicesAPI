/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package com.i2c.component.base;

/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import java.util.*;


public class BaseForm extends ActionForm {

	protected String rec;
	protected Hashtable userRowTable = new Hashtable();
	private String screenId;
	private String id;
	private String tabname;
	private String maintabname;
	private String tabUse;
	private String action; // tabclick or sorting etc.
	private String recgoto;
	private String sortname;
	private String sortcol;
	private String sortorder;
	private String recno;
	private String insformat;
	private String formname;
	private String requrl;
	private int cancleCounter;
	
	private String dbRecNo;
	private String dbTableName;
	

	private String fullClassName;


	private HttpServletResponse response;


	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	/**
	 *
	 */
	public void setRec(String value) {
		this.rec = value;
	}
	public String getRec() {
		return rec;
	}

	public String getWhereClause() {
		return "";
	};

	/**
	 * @return
	 */
	public String getScreenId() {
		return screenId;
	}

	/**
	 * @return
	 */

	/**
	 * @param string
	 */
	public void setScreenId(String string) {
		screenId = string;
	}

	/**
	 * @return
	 */
	public Hashtable getUserRowTable() {
		return userRowTable;
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param string
	 */
	public void setId(String string) {
		id = string;
	}
	public void reset(
		ActionMapping actionMapping,
		HttpServletRequest request) {
		rec = null;
		userRowTable = null;
		screenId = null;
		id = null;
		tabname = null;
		maintabname = null;
		action = null; // tabclick or sorting etc.
		recgoto = null;
		sortname = null;
		sortcol = null;
		sortorder = null;
		recno = null;
		cancleCounter = 0;
	}

	/**
	 * @return
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @return
	 */
	public String getTabname() {
		return tabname;
	}

	/**
	 * @param string
	 */
	public void setAction(String string) {
		action = string;
	}

	/**
	 * @param string
	 */
	public void setTabname(String string) {
		tabname = string;
	}

	/**
	 * @return
	 */
	public String getMaintabname() {
		return maintabname;
	}

	/**
	 * @param string
	 */
	public void setMaintabname(String string) {
		maintabname = string;
	}

	/**
	 * @return
	 */
	public String getRecgoto() {
		return recgoto;
	}

	/**
	 * @param string
	 */
	public void setRecgoto(String string) {
		recgoto = string;
	}

	/**
	 * @return
	 */
	public String getSortorder() {
		return sortorder;
	}

	/**
	 * @param string
	 */
	public void setSortorder(String string) {
		sortorder = string;
	}

	/**
	 * @return
	 */
	public String getSortcol() {
		return sortcol;
	}

	/**
	 * @return
	 */
	public String getSortname() {
		return sortname;
	}

	/**
	 * @param string
	 */
	public void setSortcol(String string) {
		sortcol = string;
	}

	/**
	 * @param string
	 */
	public void setSortname(String string) {
		sortname = string;
	}

	/**
	 * @return
	 */
	public String getTabUse() {
		return tabUse;
	}

	/**
	 * @param string
	 */
	public void setTabUse(String string) {
		tabUse = string;
	}

	/**
	 * @return
	 */
	public String getRecno() {
		return recno;
	}

	/**
	 * @param string
	 */
	public void setRecno(String string) {
		recno = string;
	}

	/**
	 * @return
	 */
	public String getInsformat() {
		return insformat;
	}

	/**
	 * @param string
	 */
	public void setInsformat(String string) {
		insformat = string;
	}

	/**
	 * @return
	 */
	public String getFormname() {
		return formname;
	}

	/**
	 * @param string
	 */
	public void setFormname(String string) {
		formname = string;
	}

	/**
	 * @return
	 */
	public String getRequrl() {
		return requrl;
	}

	/**
	 * @param string
	 */
	public void setRequrl(String string) {
		requrl = string;
	}

	/**
	 * @return
	 */
	public int getCancleCounter() {
		return cancleCounter;
	}

	/**
	 * @param i
	 */
	public void setCancleCounter(int i) {
		cancleCounter = i;
	}
	public String getFullClassName() {
		return fullClassName;
	}
	public void setFullClassName(String fullClassName) {
		this.fullClassName = fullClassName;
	}

	/**
	 * @return
	 */
	public String getDbRecNo() {
		return dbRecNo;
	}

	/**
	 * @return
	 */
	public String getDbTableName() {
		return dbTableName;
	}

	/**
	 * @param string
	 */
	public void setDbRecNo(String string) {
		dbRecNo = string;
	}

	/**
	 * @param string
	 */
	public void setDbTableName(String string) {
		dbTableName = string;
	}

}
