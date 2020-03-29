/*
 * Created on Mar 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.backend.ui;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.i2c.component.base.BaseForm;
import com.i2c.component.base.BaseHome;
import com.i2c.component.base.BaseAction;
import java.util.*;
/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface Navigation {
	public abstract int executeAction(
		BaseForm form,
		Connection conn,
		BaseHome query,
		HttpServletRequest request);
	public abstract void setNvNumstr(String value);
	public abstract void setUniqueIdStr(String value,String dType);	
	public abstract void setUniqueIdStr(String value,String formFieldName,String dType);
	//public abstract void setUniqueIdStr(String tableName,String value,String dType);
	public abstract void setUniqueIdStrBrowse(String uId,String uDisplay, String dType);	
	//public void setUniqueIdStr(String uId,String uDisplay, String dType);
	
	public abstract void setTotalRecStr(String value);
	public abstract void setWhereClauseStr(String value);
	public abstract void setCmtActionStr(String value);
	public abstract void setCRowStr(String value);
	public abstract void setContainer(String value);
	public abstract void setScreenName(String value);
	
	public abstract void setOption(
		String optNameASKey,
	Object optContainingValue);
	public abstract void setOption(
		String optNameASKey,
	    Object optContainingValue,String valueSelected);
	public abstract void setOption(
		String optNameASKey,
		Object optContainingValue, int indexSelected );
	public abstract void setOptionValue(
		String optNameASKey,
		String optContainingValue);
	public abstract void setOPtionSelected(String key, String value);
	public abstract void setRows(int rows);
	public abstract void setCurrentRecValue(String currentRecValue);
	public abstract String getCurrentRecValue();
	public abstract void setSession(HttpSession session);
	public abstract void setSession(HttpServletRequest request);
	public abstract HttpSession getSession();
	public abstract Object getDBValue(String colName);
	public abstract void setSelectedField(
		String optNameASKey,
		String optContainingValue);
	
	public abstract void setBrowseActionID(String screen, String value);
	public abstract void setBrowseActionScreen(String screen);
	public abstract void setAttribute(String key , Object value);
	public abstract void setDBValue(String key, Object value);
	public abstract void enableTab(String tab);
	public abstract void disableTab(String tab);
	public abstract void setSelectedTab(String tab);
	public abstract void hideTab(String tab);
	public abstract void enableNV(String nvName);
	public abstract void disableNV(String nvName);
	public abstract void hideNV(String nvName);
	public abstract String getPreviousAction(String screen);
	public abstract String getPreviousAction();
	public abstract String getCurrentAction(String screen);
	public abstract String getCurrentAction();
	public abstract void setPreviousAction(String screen,String action);
	public abstract void setPreviousAction(String action);
	public abstract void setCurrentAction(String screen,String action);
	public abstract void setCurrentAction(String action);
	public abstract void setCurrentTable(Hashtable table);
	public abstract void addWhereClause(String additionalWhereClasue);
	public abstract void resetWhereClause(String newWhereClause); // Should be used with care
	public abstract void setValueContiner(String containerName, Object container);
	public abstract void enableAllDVFields();
	public abstract void disableAllDVFields();
	public abstract void enableDVField(String dvName);
	public abstract void disableDVField(String dvName) ;
	public abstract void readAllowedDVField(String dvName);
	public abstract void readDisAllowedDVField(String dvName);
	public abstract void setAllReadOnlyFields();
	public BaseAction getStrutsAction();
	public HttpServletRequest getRequest();
	
//	public abstract String encryptValue(Connection con, String encryptValue) throws Exception;
//	public abstract Object decryptValue(Connection con, String key) throws Exception;
//	public abstract String encryptValue(String encryptValue) throws Exception;
//	public abstract Object decryptValue(String key) throws Exception;
}