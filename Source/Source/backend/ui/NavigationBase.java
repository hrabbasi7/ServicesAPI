/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.backend.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import com.i2c.cards.*;
import com.i2c.cards.util.*;
import com.i2c.util.*;
import com.i2c.component.base.*;
import com.i2c.component.util.*;
import org.apache.struts.action.*;
import com.i2c.component.base.*;
import com.i2c.cards.security.PermissionInfoObj;

/**
 * @author iahmad
 *
 * @DESCRIPTION
 * The purpose of this class is to handle the navigation.
 * This clause provides the background logic for the tags which has been developed to handle the interface
 * related conserns.
 * This class should be used by the action and gets the required functionaly
 * Here is the detail of the functionality this class provides
 * 1) Deals with the navigation bar
 * 2) Deals with the  sequence
 * 3) Populates the container for the data fields
 *
 *
 */

public abstract class NavigationBase implements Navigation {

	/**
	 *
	 */
	/*protected String cancel = Constants.NO,
		copy = Constants.YES,
		commit = Constants.NO,
		delete = Constants.YES,
		update = Constants.YES,
		search = Constants.YES,
		insert = Constants.YES,
		first = Constants.YES,
		next = Constants.YES,
		previous = Constants.YES,
		last = Constants.YES,
		gto = Constants.YES,
		browse = Constants.YES;*/

	// Edited by ILYAS 03/08
	protected NavigationBar navBar = new NavigationBar();
	protected NavigationBar navBar_Prev = null;

	protected int navigationnum = 1;
	protected String navigationnumstr;
	protected String rec;

	// [Key Column Name] to store the grid hidden elements
	protected String keyColName;


	protected String nvNumstr; // set value in Session
	protected String whereClauseStr; // set value; in Session
	protected String totalRecStr; // set value; in Session
	protected String cmtActionStr; // set value; in Session
	protected String uniqueIdStr;
	// set value; used for update , delete etc in Session
	protected String cRowStr; // set value;
	protected String dataContainer;
	protected String currentRecValue;
	protected String screenName;

	protected Hashtable table = null;
	protected HttpSession session;
	protected int rows;
	protected int tRows;
	protected String userMode;
	protected ArrayList uniqueIdList = new ArrayList(3);
	private ArrayList enableFildsList = new ArrayList(3);
	private ArrayList disableFildsList = new ArrayList(3);
	private ArrayList readAllowedList = new ArrayList(3);
	private ArrayList readDisAllowedList = new ArrayList(3);

	protected Hashtable idTable = null;
	// Now we are using NavigationBar Object
	//protected Hashtable nvStateTable = new Hashtable();
	protected ActionMapping mapping;
	protected HttpServletResponse response;
	protected BaseAction strutsAction;
	protected BaseForm form;
	protected Connection conn;
	protected BaseHome query;
	protected HttpServletRequest request;
	protected Hashtable columnMappingTable = new Hashtable();
	protected Hashtable formatMappingTable = new Hashtable();

	private Hashtable editValuesTable = new Hashtable();
	protected Hashtable insFormatTable  = new Hashtable();
	//The following permission info object added by Edwin
	private PermissionInfoObj permissionInfoObj;

//	for lock button
	private boolean isLocked = false;
//	End for lock button


	public abstract int executeAction(
		BaseForm form,
		Connection conn,
		BaseHome query,
		HttpServletRequest request);


	public void setNvNumstr(String value) {
		nvNumstr = value;
	}
	/**
	 * this Method is used for setting the unique ids.
	 * value is the name of the db table primary key field and
	 * dtype is the name of type like Constants.CHAR_FIELD.
	 */
	public void setUniqueIdStr(String value, String dType) {
		String s[] = null;
		if (value != null && dType != null) {
			s = new String[2];
			s[0] = value;
			s[1] = dType;
			uniqueIdList.add(s);
		}
		//uniqueIdStr = value;
	}

	/**
	 * Note Currently this method has wrong results.
	 * this Method is used for setting the unique ids.
	 * tableName is the name of the table.
	 * value is the name of the db table primary key field and
	 * dtype is the name of type like Constants.CHAR_FIELD.
	 *
	 */

	public void setUniqueIdStr(
		String value,
		String formFieldName,
		String dType) {
		String s[] = null;
		if (value != null && dType != null && formFieldName != null) {
			s = new String[3];
			s[0] = value;
			s[1] = dType;
			s[2] = formFieldName;
			uniqueIdList.add(s);
		}
		//uniqueIdStr = value;
	}

	/**
	 * Note Currently this method has wrong results.
	 * this Method is used for setting the unique ids.
	 * uId is the name of the table.
	 * uDisplay is the name of the display required for this particular id.
	 * dtype is the name of type like Constants.CHAR_FIELD.
	 *
	 */

	public void setUniqueIdStrBrowse(
		String uId,
		String uDisplay,
		String dType) {
		String s[] = null;
		if (uId != null && dType != null) {
			if (uDisplay != null) {
				s = new String[3];
				s[2] = uDisplay;
			} else {
				s = new String[2];
			}
			s[0] = uId;
			s[1] = dType;

			uniqueIdList.add(s);
		}
		//uniqueIdStr = value;
	}

	public void setTotalRecStr(String value) {
		totalRecStr = value;
	}
	public void setWhereClauseStr(String value) {
		whereClauseStr = value;
	}
	public void setCmtActionStr(String value) {
		cmtActionStr = value;
	}
	public void setCRowStr(String value) {
		cRowStr = value;
	}
	public void setContainer(String value) {
		dataContainer = value;
	}
	/**
	 * The Name of the screens for which this screen will be executed
	 * Its given name must match the name at jsp side.
	 */
	public void setScreenName(String value) {
		screenName = value;
	}
	public void setOptionValue(
		String optNameASKey,
		String optContainingValue) {
		if (optNameASKey != null && optContainingValue != null) {
			if (getSession() != null) {
				getSession().setAttribute(optNameASKey, optContainingValue);
			}
		}
	}
	/**
	 * Is used to select the particular field like in Combobox defautl selection
	 */
	public void setOPtionSelected(String key, String value) {
		//System.out.println("OPTION SELECTED "+table);
//		Hashtable tbl=null;
//		if( getSession().getAttribute(this.screenName+Constants.ERRORVALUE)!=null && getSession().getAttribute(this.screenName+Constants.ERRORVALUE).equals(Constants.YES) ){
//			//defaultValueContainerName = secName+Constants.ININFO;
//
//			tbl = (Hashtable)getSession().getAttribute(this.screenName+Constants.ININFO);
//		}
//
//
//		if(tbl!=null){
//			table=tbl;
//		}
		if (table != null) {
			if (getSession() != null) {
				if (key != null && value != null)
					getSession().setAttribute(
						key + Constants.SELECTED,
						table.get(value));
			}
		}
	}
	/**
	 * How many rows at one attempt required from the database.
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}
	/**
	 * if the change in current REC Value is required.(Absolete)
	 */
	public void setCurrentRecValue(String currentRecValue) {
		this.currentRecValue = currentRecValue;
	}
	//(Absolete)
	public String getCurrentRecValue() {
		return currentRecValue;
	}
	/**
	 * For setting the session Externally.
	 */

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public HttpSession getSession() {
		if (session != null)
			return session;
		return null;
	}
	/**
	 * setting the values manually for the particular fields.
	 */
	public Object getDBValue(String colName) {
		if (this.table != null && colName != null) {
			return table.get(colName);
		}
		return null;
	}
	public void setSelectedField(
		String optNameASKey,
		String optContainingValue) {
		if (optNameASKey != null && optContainingValue != null) {
			if (getSession() != null) {
				getSession().setAttribute(
					optNameASKey + Constants.SELECTED,
					optContainingValue);
			}
		}
	}

	/**
	 * @param string
	 */
	public void setBrowseActionID(String screen, String value) {

		if (this.table != null && value != null) {
			if (table.get(value) != null) {
				if (getSession() != null && screen != null) {
					getSession().setAttribute(
						screen + ComponentConstants.BROWSEID,
						table.get(value));
				}
			}
		}
	}
	public void setBrowseActionScreen(String screen) {
		if (getSession() != null && screen != null) {
			getSession().setAttribute(
				screen + ComponentConstants.BROWSEACTIONSCREEN,
				screen);
		}
	}
	/**
	 * Is used to set The Attributes for the purposes of where the normal methods are not fullfilling the requirements.
	 */
	public void setAttribute(String key, Object value) {
		if (getSession() != null && key != null && value != null) {
			getSession().setAttribute(key, value);
		}
	}
	/**
	 * Another version of setting the user values manully for FRONT ENDs.
	 */
	public void setDBValue(String key, Object value) {
		if (this.table != null && key != null && value != null) {
			table.put(key, value);
		}
	}
	/**
	 * Is used to enable the Particular Tab.
	 */
	public void enableTab(String tab) {
		if (getSession() != null && tab != null) {
			getSession().setAttribute(tab, ComponentConstants.ENABLE);
		}
	}
	/**
	 * Is used to disable the Particular Tab.
	 */

	public void disableTab(String tab) {
		if (getSession() != null && tab != null) {
			getSession().setAttribute(tab, ComponentConstants.DISABLE);
		}
	}
	/**
	 * Is used to show as selected for the Particular Tab.
	 */

	public void setSelectedTab(String tab) {
		if (getSession() != null && tab != null) {
			getSession().setAttribute(tab, ComponentConstants.SELECT);
		}
	}
	/**
	 * To get the Pervious Action Manually for the particular screen.
	 */
	public String getPreviousAction(String screen) {
		if (getSession() != null && screen != null) {
			return (String) getSession().getAttribute(
				screen + ComponentConstants.PRVIOUS_ACTION);
		}
		return null;
	}
	/**
	 * To get the Pervious Action Manually.
	 */
	public String getPreviousAction() {
		if (getSession() != null) {
			return (String) getSession().getAttribute(
				this.screenName + ComponentConstants.PRVIOUS_ACTION);
		}
		return null;
	}
	/**
	 * Is used to get the Curren Action for the particular given screen.
	 */
	public String getCurrentAction(String screen) {
		if (getSession() != null && screen != null) {
			return (String) getSession().getAttribute(
				screen + Constants.CURRENT_ACTION);
		}
		return null;
	}
	/**
	 * Is used to get the Current Action of the crrent screen.
	 */

	public String getCurrentAction() {
		if (getSession() != null) {
			return (String) getSession().getAttribute(
				this.screenName + Constants.CURRENT_ACTION);
		}
		return null;
	}
	/**
	 * TO override the previous Action of the screen.
	 */
	public void setPreviousAction(String screen, String action) {
		if (getSession() != null && screen != null) {
			getSession().setAttribute(
				screen + ComponentConstants.PRVIOUS_ACTION,
				action);
		}
	}
	/**
	 * To Override the previous Action of the Current Screen.
	 */
	public void setPreviousAction(String action) {
		if (getSession() != null) {
			getSession().setAttribute(
				this.screenName + ComponentConstants.PRVIOUS_ACTION,
				action);
		}
	}
	/**
	 * To Override the Current Action of the particular Screen.
	 */

	public void setCurrentAction(String screen, String action) {
		if (getSession() != null && screen != null) {
			getSession().setAttribute(
				screen + Constants.CURRENT_ACTION,
				action);
		}
	}
	/**
	 * To Override the current Action of the Current Screen.
	 */

	public void setCurrentAction(String action) {
		this.resetCurrentMode(action);
	}
	public void setSession(HttpServletRequest request) {
		this.setSession(request.getSession(false));
	}

	/**
	 * To set the ValueContainer Manually.
	 */
	public void setCurrentTable(Hashtable table) {
		this.table = table;
	}

	/**
	 * @return
	 */
	public String getCmtActionStr() {
		return cmtActionStr;
	}


	/**
	 * @return
	 */
	public String getCRowStr() {
		return cRowStr;
	}

	/**
	 * @return
	 */
	public String getDataContainer() {
		return dataContainer;
	}


	/**
	 * @return
	 */
	public int getNavigationnum() {
		return navigationnum;
	}

	/**
	 * @return
	 */
	public String getNavigationnumstr() {
		return navigationnumstr;
	}


	/**
	 * @return
	 */
	public String getNvNumstr() {
		return nvNumstr;
	}


	/**
	 * @return
	 */
	public String getRec() {
		return rec;
	}

	/**
	 * @return
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @return the current screen Name.
	 */
	public String getScreenName() {
		return screenName;
	}


	/**
	 * @return
	 */
	public Hashtable getTable() {
		return table;
	}

	/**
	 * @return
	 */
	public String getTotalRecStr() {
		return totalRecStr;
	}
	/**
	 * @return
	 */
	public ArrayList getUniqueIdList() {
		return uniqueIdList;
	}

	/**
	 * @return
	 */
	public String getUniqueIdStr() {
		return uniqueIdStr;
	}


	/**
	 * @return
	 */
	public String getUserMode() {
		return userMode;
	}

	/**
	 * @return
	 */
	public String getWhereClauseStr() {
		return whereClauseStr;
	}


	/**
	 * @param string
	 */
	public void setDataContainer(String string) {
		dataContainer = string;
	}


	/**
	 * @param i
	 */
	public void setNavigationnum(int i) {
		navigationnum = i;
	}

	/**
	 * @param string
	 */
	public void setNavigationnumstr(String string) {
		navigationnumstr = string;
	}


	/**
	 * @param string
	 */
	public void setRec(String string) {
		rec = string;
	}


	/**
	 * @param hashtable
	 */
	public void setTable(Hashtable hashtable) {
		table = hashtable;
	}

	/**
	 * @param list
	 */
	public void setUniqueIdList(ArrayList list) {
		uniqueIdList = list;
	}


	/**
	 * Not Used Yet
	 * @param string
	 */
	public void setUserMode(String string) {
		userMode = string;
	}
	/**
	 * If your are willing to add your own parameteres in where clause.
	 */
	public void addWhereClause(String additionalWhereClasue) {
		String extWhereClause =
			(String) this.session.getAttribute(this.whereClauseStr);
		System.out.println("Attention : Existing where Clause Has been Altered ");
		System.out.println("Existing where Clause :" + extWhereClause);
		System.out.println("New  where Clause :" + additionalWhereClasue);
		extWhereClause = extWhereClause + " " + additionalWhereClasue;
		System.out.println("Final where Clause after change :" + extWhereClause);
		session.setAttribute(
			this.screenName + this.whereClauseStr,
			extWhereClause);

	}

	/* If your are willing to add your own parameteres in where clause.
	 * Should be used with care and should be comunicated with the senior member befor its use.
	 */
	public void resetWhereClause(String newWhereClause) {
		System.out.println(
			"Attention : Existing where Clause Has been over-Rulled");
		String extWhereClause =
			(String) this.session.getAttribute(this.whereClauseStr);
		System.out.println("Existing where Clause :" + extWhereClause);
		System.out.println("New  where Clause :" + newWhereClause);
		System.out.println("New  where Clause STR :" + this.whereClauseStr);
		session.setAttribute(this.whereClauseStr, newWhereClause);
	}
	/**
	 * To Set the valueContainer.
	 */
	public void setValueContiner(String containerName, Object container) {
		session.setAttribute(containerName, container);
	}
	/**
		* To Set the ComboBox
		*
		*/
	public void setOption(String optNameASKey, Object optContainingValue) {
		if (getSession() != null
			&& optNameASKey != null
			&& optContainingValue != null) {
			getSession().setAttribute(optNameASKey, optContainingValue);
		}
	}
	/**
	 * To Set the ComboBox and with Value for By Default Selection
	 */
	public void setOption(
		String optNameASKey,
		Object optContainingValue,
		String valueSelected) {
		if (getSession() != null
			&& optNameASKey != null
			&& optContainingValue != null) {
			getSession().setAttribute(optNameASKey, optContainingValue);
			if (valueSelected != null)
				getSession().setAttribute(
					optNameASKey + "_SELVALUE",
					valueSelected);
		}

	}
	/**
	 * To Set the ComboBox and with Index for By Default Selection
	 */

	public void setOption(
		String optNameASKey,
		Object optContainingValue,
		int indexSelected) {
		if (getSession() != null
			&& optNameASKey != null
			&& optContainingValue != null) {
			getSession().setAttribute(optNameASKey, optContainingValue);
			if (indexSelected != 0)
				getSession().setAttribute(
					optNameASKey + "_SELINDEX",
					String.valueOf(indexSelected));
		}

	}
	protected void setNavigation() {
		setNavigationInSession();
		if (screenName != null && table != null && table.size() > 0)
		{
		    //setNavigationInSession();
			setNVState();
			//getSession().setAttribute(screenName + ComponentConstants.NVSTATE,this.nvStateTable);
			getSession().setAttribute(screenName + ComponentConstants.NVSTATE,this.navBar_Prev);

			//System.out.println(" Storing NVSTATETABLE "+ nvStateTable );
			if (this.strutsAction != null
				&& this.strutsAction.returnSelectionList() != null)
			{
				enableSelectionList(this.strutsAction.returnSelectionList());
			}
			this.setList();
		}
		if (nvNumstr != null) {
			getSession().setAttribute(nvNumstr, String.valueOf(navigationnum));
			getSession().setAttribute(this.screenName+Constants.RECNO, String.valueOf(navigationnum));

		}
		if (table != null) {

			if (table.get(ComponentConstants.UNIQUEIDLIST) != null) {
				getSession().setAttribute(
					screenName + ComponentConstants.UNIQUEIDLIST,
					table.get(ComponentConstants.UNIQUEIDLIST));
			}
			getSession().setAttribute(dataContainer, table);
			//System.out.println(dataContainer);
			if (rows == 1)
				getSession().setAttribute(cRowStr, table);
		}
		if (this.query != null) {
			if (query.getErrors().size() > 0
				&& query.getObjectErrorKeys().size() > 0) {
				if (this.strutsAction != null)
					this.strutsAction.saveErrors(
						query.getErrors(),
						query.getObjectErrorKeys(),
						request);
				//this.saveErrors(query.getErrors(), query.getObjectErrorKeys(), request);
			}
			if (query.getErrorKeys().size() > 0) {
				//this.saveErrors(query.getErrorKeys(),request);
				if (this.strutsAction != null)
					this.strutsAction.saveErrors(query.getErrorKeys(), request);

			}
			if (query.getMessages().size() > 0
				&& query.getObjectMessageKeys().size() > 0) {
				this.saveMessages(
					query.getMessages(),
					query.getObjectMessageKeys(),
					request);
			}
			if (query.getMessageKeys().size() > 0) {
				this.saveMessages(query.getMessageKeys(), request);
			}
		}
		if(this.tRows<=0) {
			this.getSession().setAttribute(this.screenName+Constants.ACTIVE_MSG,Constants.NO_REC_FND);
		}

	}

	protected void setErrNavigation() {
		if (screenName !=null )
		{
			this.setNavigationInSession();
			/*
			getSession().setAttribute(screenName + Constants.COMMIT_ENABLED,commit);
			getSession().setAttribute(screenName + Constants.CANCEL_ENABLED,cancel);
			getSession().setAttribute(screenName + Constants.COPY_ENABLED, copy);
			getSession().setAttribute(screenName + Constants.DELETE_ENABLED,delete);
			getSession().setAttribute(screenName + Constants.UPDATE_ENABLED,update);
			getSession().setAttribute(screenName + Constants.SEARCH_ENABLED,search);
			getSession().setAttribute(screenName + Constants.INSERT_ENABLED,insert);
			getSession().setAttribute(screenName + Constants.FIRST_ENABLED, first);
			getSession().setAttribute(screenName + Constants.NEXT_ENABLED, next);
			getSession().setAttribute(screenName + Constants.PREVIOUS_ENABLED,previous);
			getSession().setAttribute(screenName + Constants.LAST_ENABLED, last);
			getSession().setAttribute(screenName + Constants.NVENABLE,Constants.YES);
			getSession().setAttribute(screenName + Constants.GOTO_ENABLED, gto);
			getSession().setAttribute(screenName + ComponentConstants.NVSTATE,this.nvStateTable);
			*/
			if (this.query != null) {
				if (query.getErrors().size() > 0
					&& query.getObjectErrorKeys().size() > 0) {
					if (this.strutsAction != null)
						this.strutsAction.saveErrors(
							query.getErrors(),
							query.getObjectErrorKeys(),
							request);
					//this.saveErrors(query.getErrors(), query.getObjectErrorKeys(), request);
				}
				if (query.getErrorKeys().size() > 0) {
					//this.saveErrors(query.getErrorKeys(),request);
					if (this.strutsAction != null)
						this.strutsAction.saveErrors(query.getErrorKeys(), request);

				}
				if (query.getMessages().size() > 0
					&& query.getObjectMessageKeys().size() > 0) {
					this.saveMessages(
						query.getMessages(),
						query.getObjectMessageKeys(),
						request);
				}
				if (query.getMessageKeys().size() > 0) {
					this.saveMessages(query.getMessageKeys(), request);
				}
			}

			setNVState();
			//			System.out.println(" Storing NVSTATETABLE "+ nvStateTable );
		}
	}

	/**
	 * @return
	 */
	public Connection getConn() {
		return conn;
	}

	/**
	 * @return
	 */
	public BaseForm getForm() {
		return form;
	}

	/**
	 * @return
	 */
	public ActionMapping getMapping() {
		return mapping;
	}

	/**
	 * @return
	 */
	public BaseHome getQuery() {
		return query;
	}

	/**
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * @return
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * @return
	 */
	public BaseAction getStrutsAction() {
		return strutsAction;
	}

	/**
	 * @param connection
	 */
	public void setConn(Connection connection) {
		conn = connection;
	}

	/**
	 * @param form
	 */
	public void setForm(BaseForm form) {
		this.form = form;
	}

	/**
	 * @param mapping
	 */
	public void setMapping(ActionMapping mapping) {
		this.mapping = mapping;
	}

	/**
	 * @param home
	 */
	public void setQuery(BaseHome home) {
		query = home;
	}

	/**
	 * @param request
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @param response
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * @param action
	 * Every Action should use this method for the perpose of Lines
	 */
	public void setStrutsAction(BaseAction action) {
		strutsAction = action;
	}
	/**
	 * Is use to Hide the particular Tab.
	 */
	public void hideTab(String tab) {
		if (getSession() != null && tab != null) {
			getSession().setAttribute(tab, ComponentConstants.HIDE);
		}
	}
	/**
	 * Is use to enable the Navigation Item.
	 */
	public void enableNV(String nvName) {
		if (getSession() != null && nvName != null) {
			getSession().setAttribute(
				this.screenName + nvName,
				ComponentConstants.ENABLE);
		}
	}
	/**
	 * Is use to disnable the Navigation Item.
	 */

	public void disableNV(String nvName) {
		if (getSession() != null && nvName != null) {
			getSession().setAttribute(
				this.screenName + nvName,
				ComponentConstants.DISABLE);
		}
	}
	/**
	 * Is use to hide the Navigation Item.
	 */

	public void hideNV(String nvName) {
		if (getSession() != null && nvName != null) {
			getSession().setAttribute(
				this.screenName + nvName,
				ComponentConstants.HIDE);
		}
	}

	//This method is returning a string made up of this structure: column1 = value1 AND column2 = value2
	//It is called from the executeAction() method
	public String getUniqueIds() {

		StringBuffer buf = new StringBuffer("");
		if (idTable != null) {
			Enumeration enumeration = this.idTable.keys();
			//System.out.println(idTable);
			String key = null;
			if (enumeration.hasMoreElements()) {
				key = (String) enumeration.nextElement();
				buf.append(" ");
				buf.append(key);
				buf.append(" = ");
				buf.append(idTable.get(key));
			}
			while (enumeration.hasMoreElements()) {
				key = (String) enumeration.nextElement();
				buf.append(" AND ");
				buf.append(key);
				buf.append(" = ");
				buf.append(idTable.get(key));
			}
		}
		//System.out.println("Unique IDS  : " + buf.toString());
		return buf.toString();
	}
	public Hashtable removeUniqueIds(Hashtable table) {
		if (idTable != null) {
			Enumeration enumeration = this.idTable.keys();
			StringBuffer buf = new StringBuffer("");
			String key = null;
			while (enumeration.hasMoreElements()) {
				key = (String) enumeration.nextElement();
				table.remove(key);
			}
			//System.out.println(" removeUniqueIds" + table);
		}
		return table;
	}

	public String convertUniqueIds(String value) {
		Enumeration enumeration = this.idTable.keys();
		StringBuffer buf = new StringBuffer("");
		String key = null;
		if (enumeration.hasMoreElements()) {
			key = (String) enumeration.nextElement();
			buf.append(" ");
			buf.append(key);
			buf.append(" = ");
			buf.append(idTable.get(key));
		}
		while (enumeration.hasMoreElements()) {
			key = (String) enumeration.nextElement();
			buf.append(" AND ");
			buf.append(key);
			buf.append(" = ");
			buf.append(idTable.get(key));
		}
		//System.out.println(buf.toString());
		return buf.toString();
	}
	protected void setList() {

		//		System.out.println("setList >");
		//
		//		System.out.println("enableFildsList.hashCode()" +enableFildsList.hashCode());
		//		System.out.println("disableFildsList.hashCode()" +disableFildsList.hashCode());
		//		System.out.println("readAllowedList.hashCode()" +readAllowedList.hashCode());
		//
		//		System.out.println("readDisAllowedList.hashCode()" +readDisAllowedList.hashCode());

		getSession().setAttribute(screenName + Constants.ENABLEFIELDS,enableFildsList);
		getSession().setAttribute(screenName + Constants.DISABLEFIELDS,disableFildsList);
		getSession().setAttribute(screenName + Constants.READALLOWED,this.readAllowedList);
		getSession().setAttribute(screenName + Constants.READDISALLOWED,this.readDisAllowedList);
		//System.out.println("setList <");
	}

	public void setNVState()
	{
		// Setting Just One Object [NavigationBar]
		navBar_Prev = (NavigationBar)navBar.clone();
		// Setting Permission Dependies in the NavigaionBarObject
		if(navBar_Prev != null)
		navBar_Prev.setPermissionDependencies(this.permissionInfoObj);

		/*

		// Edited by ILYAS 03/08
		String cancel, commit, delete, update, last, next, first, previous, gto, copy, search , browse, insert;

		cancel  	= navBar.isCancel()?"Y":"N";
		commit  	= navBar.isCommit()?"Y":"N";
		delete  	= navBar.isDelete()?"Y":"N";
		update  	= navBar.isUpdate()?"Y":"N";
		last    	= navBar.isLast()?"Y":"N";
		first		= navBar.isFirst()?"Y":"N";
		next		= navBar.isNext()?"Y":"N";
		previous	= navBar.isPrevious()?"Y":"N";
		gto 		= navBar.isGoTo()?"Y":"N";
		copy 		= navBar.isCopy()?"Y":"N";
		search 		= navBar.isSearch()?"Y":"N";
		browse 		= navBar.isBrowse()?"Y":"N";
		insert 		= navBar.isInsert()?"Y":"N";


		if(this.permissionInfoObj!=null) //further filtering not applicable
		{
			nvStateTable.put(Constants.CANCEL, cancel);
			nvStateTable.put(Constants.COMMIT, commit);
		}
		//For projects other than MCP that do not use PermissionInfoObj security class.
		else
		{
			nvStateTable.put(Constants.CANCEL, cancel);
			nvStateTable.put(Constants.COMMIT, commit);
		}
		if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsDeleteAllowed())
		{
			nvStateTable.put(Constants.DELETE, delete);
		}
		//For projects other than MCP that do not use PermissionInfoObj security class.
		else
		{
			nvStateTable.put(Constants.DELETE, delete);
		}

		if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsEditAllowed())
		{
			nvStateTable.put(Constants.UPDATE, update);
		}
		//For projects other than MCP that do not use PermissionInfoObj security class.
		else
		{
			nvStateTable.put(Constants.UPDATE, update);
		}

		if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsInsertAllowed())
		{
			nvStateTable.put(Constants.INSERT, insert);
			nvStateTable.put(Constants.COPY, copy);
		}
		//For projects other than MCP that do not use PermissionInfoObj security class.
		else
		{
			nvStateTable.put(Constants.INSERT, insert);
			nvStateTable.put(Constants.COPY, copy);
		}

		if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsViewAllowed())
		{
			nvStateTable.put(Constants.SEARCH, search);
			nvStateTable.put(Constants.FIRST, first);
			nvStateTable.put(Constants.NEXT, next);
			nvStateTable.put(Constants.PREVIOUS, previous);
			nvStateTable.put(Constants.LAST, last);
			nvStateTable.put(Constants.GOTO, gto);
			nvStateTable.put(Constants.BROWSE,browse);
		}
		//For projects other than MCP that do not use PermissionInfoObj security class.
		else
		{
			nvStateTable.put(Constants.SEARCH, search);
			nvStateTable.put(Constants.FIRST, first);
			nvStateTable.put(Constants.NEXT, next);
			nvStateTable.put(Constants.PREVIOUS, previous);
			nvStateTable.put(Constants.LAST, last);
			nvStateTable.put(Constants.GOTO, gto);
			nvStateTable.put(Constants.BROWSE,browse);
		}

		//getSession().setAttribute(screenName + Constants.BROWSE_ENABLED, browse);

		 */
	}

	/**
	 * TO Restore the pervious Navigation State.
	 * Deprected Method Using Navigation Bar
	 * @param nvStateTable
	 */
	protected void restorePreviousNVState(NavigationBar navBar_Prev)
	{
		navBar.copy(navBar_Prev);
		this.navBar_Prev = navBar_Prev;
	}

	/**
	 * TO Restore the pervious Navigation State.
	 * @param nvStateTable
	 */
	protected void restorePreviousNVState(Hashtable nvStateTable)
	{
		/*
		String cancel, commit, delete, update, last, next, first, previous, gto, copy, search , browse, insert;
		if (nvStateTable != null)
		{
			cancel = (String) nvStateTable.get(Constants.CANCEL);
			copy = (String) nvStateTable.get(Constants.COPY);
			commit = (String) nvStateTable.get(Constants.COMMIT);
			delete = (String) nvStateTable.get(Constants.DELETE);
			update = (String) nvStateTable.get(Constants.UPDATE);
			search = (String) nvStateTable.get(Constants.SEARCH);
			insert = (String) nvStateTable.get(Constants.INSERT);
			first = (String) nvStateTable.get(Constants.FIRST);
			next = (String) nvStateTable.get(Constants.NEXT);
			previous = (String) nvStateTable.get(Constants.PREVIOUS);
			last = (String) nvStateTable.get(Constants.LAST);
			gto = (String) nvStateTable.get(Constants.GOTO);
			browse = (String) nvStateTable.get(Constants.BROWSE);

			// Edited by ILYAS 03/08
			// Setting Navigation Bar
			navBar.setCancel((cancel != null && cancel.equals("Y"))?true:false);
			navBar.setCopy((copy != null && copy.equals("Y"))?true:false);
			navBar.setCommit((commit != null && commit.equals("Y"))?true:false);
			navBar.setDelete((delete != null && delete.equals("Y"))?true:false);
			navBar.setUpdate((update != null && update.equals("Y"))?true:false);
			navBar.setSearch((search != null && search.equals("Y"))?true:false);
			navBar.setInsert((insert != null && insert.equals("Y"))?true:false);
			navBar.setFirst((first != null && first.equals("Y"))?true:false);
			navBar.setNext((next != null && next.equals("Y"))?true:false);
			navBar.setPrevious((previous != null && previous.equals("Y"))?true:false);
			navBar.setLast((last != null && last.equals("Y"))?true:false);
			navBar.setGoTo((gto != null && gto.equals("Y"))?true:false);
			navBar.setBrowse((browse != null && browse.equals("Y"))?true:false);

		}
		*/
	}
	public void restorePreviousState() {
		restorePreviousState(this.screenName);
	}
	public void restorePreviousState(String Screen)
	{
		//restorePreviousNVState((Hashtable) getSession().getAttribute(Screen + ComponentConstants.NVSTATE));
		restorePreviousNVState((NavigationBar) getSession().getAttribute(Screen + ComponentConstants.NVSTATE));
		this.setNavigationInSession();
		/*
		getSession().setAttribute(screenName + Constants.COMMIT_ENABLED,commit);
		getSession().setAttribute(screenName + Constants.CANCEL_ENABLED,cancel);
		getSession().setAttribute(screenName + Constants.COPY_ENABLED, copy);
		getSession().setAttribute(screenName + Constants.DELETE_ENABLED,delete);
		getSession().setAttribute(screenName + Constants.UPDATE_ENABLED,update);
		getSession().setAttribute(screenName + Constants.SEARCH_ENABLED,search);
		getSession().setAttribute(screenName + Constants.INSERT_ENABLED,insert);
		getSession().setAttribute(screenName + Constants.FIRST_ENABLED, first);
		getSession().setAttribute(screenName + Constants.NEXT_ENABLED, next);
		getSession().setAttribute(screenName + Constants.PREVIOUS_ENABLED,previous);
		getSession().setAttribute(screenName + Constants.LAST_ENABLED, last);
		getSession().setAttribute(screenName + Constants.GOTO_ENABLED, gto);
		getSession().setAttribute(screenName + Constants.NVENABLE,Constants.YES);
		getSession().setAttribute(screenName + ComponentConstants.NVSTATE,this.nvStateTable);
		*/
		setNVState();
	}

	protected void enableSelectionList(Hashtable selTable) {
		if (this.getSession() != null) {
			//System.out.println("SELECTION TABLE " + selTable);
			Enumeration enumeration = selTable.keys();
			String key = null;
			while (enumeration.hasMoreElements()) {
				key = (String) enumeration.nextElement();
				//System.out.println("Key :" + key);
				//System.out.println("value :" + selTable.get(key));

				//this.getSession().setAttribute(key,);
				if (table != null) {
					//System.out.println("value for selection :" + table.get(selTable.get(key)));
					this.getSession().setAttribute(
						key + Constants.SELECTED,
						table.get(selTable.get(key)));
				}
			}

		}
	}
	/**
	 * To Enable all DV FIELDS.
	 */
	public void enableAllDVFields() {
		this.getSession().setAttribute(
			this.screenName + Constants.ENABLEALL,
			Constants.YES);
	}
	/**
	 * To disable all DV FIELDS.
	 */

	public void disableAllDVFields() {
		this.getSession().setAttribute(
			this.screenName + Constants.ENABLEALL,
			Constants.NO);
	}
	/**
	 * To enable a particular DV FIELDS.
	 * * where dvName is the name of the html form Field
	 */
	public void enableDVField(String dvName) {

		enableFildsList.add(dvName);
	}
	/**
	 * To disable a particular DV FIELDS.
	 * * where dvName is the name of the html form Field
	 */
	public void disableDVField(String dvName) {
		//System.out.println("LIST : "+disableFildsList);
		disableFildsList.add(dvName);
		//		System.out.println("LIST AFTER"+disableFildsList);
		//		System.out.println(" screen info ...." +screenName+Constants.DISABLEFIELDS);
		//		ArrayList list = ((ArrayList)(getSession().getAttribute(screenName+Constants.DISABLEFIELDS)));
		//		System.out.println(" FROM SESSION: " +list.getClass());
		//		System.out.println(" FROM SESSION: " +list.getClass().getName());
	}
	/**
	 * If we want to allow a field for Read OR to Vanish the Readonly Mode from the particular Field
	 * where dvName is the name of the html form Field
	 */
	public void readAllowedDVField(String dvName) {
		readAllowedList.add(dvName);
	}
	/**
	 * If we want set a field of this current screen in ReadOnly Mode.
	 * * where dvName is the name of the html form Field
	 */
	public void readDisAllowedDVField(String dvName) {
		readDisAllowedList.add(dvName);
	}
	/**
	 * If we want to set the readonly Mode for all the fields
	 */
	public void setAllReadOnlyFields() {
		this.getSession().setAttribute(
			this.screenName + Constants.READONLY,
			Constants.YES);
	}
	// Error/Messages Related.
	private void saveErrors(ArrayList list, HttpServletRequest request) {
		this.storeMessages(request, CommonUtilities.saveErrors(list), "error");
	}
	private void saveMessages(ArrayList list, HttpServletRequest request) {
		this.storeMessages(
			request,
			CommonUtilities.saveMessages(list),
			"message");
	}
	private void saveErrors(
		ArrayList list,
		ArrayList objectsList,
		HttpServletRequest request) {
		this.storeMessages(
			request,
			CommonUtilities.saveErrors(list, objectsList),
			"errors");
	}
	private void saveMessages(
		ArrayList list,
		ArrayList objectsList,
		HttpServletRequest request) {
		this.storeMessages(
			request,
			CommonUtilities.saveMessages(list, objectsList),
			"messages");
	}
	private void storeMessages(
		HttpServletRequest request,
		org.apache.struts.action.ActionMessages messages,
		String key) {
		request.setAttribute(key, messages);
	}
	private void storeErrors(
		HttpServletRequest request,
		org.apache.struts.action.ActionErrors errors,
		String key) {
		request.setAttribute(key, errors);
	}
	public int getRecGoto(String recNum) {
		String recgoto = null;
		int intgoto = 0;
		if (recNum != null) {
			recgoto = recNum;
			if (recgoto.startsWith("+")) {
				recgoto = recgoto.substring(1);
				try {
					intgoto = Integer.parseInt(recgoto);
				} catch (Exception e) {
					intgoto = 0;
				}
			} else if (recgoto.startsWith("-")) {
				recgoto = recgoto.substring(1);
				try {
					intgoto = Integer.parseInt(recgoto);
					intgoto = -intgoto;
				} catch (Exception e) {
					intgoto = 0;
				}
			} else {
				try {
					intgoto = Integer.parseInt(recgoto);
				} catch (Exception e) {
					intgoto = 0;
				}
			}
		}
		return intgoto;
	}
	public int getTotalRows(){
	   return this.tRows;
	}
	private void resetCurrentMode(String rec)
	{
		// Edited by ILYAS 03/08
		if (rec != null)
		{
			session.setAttribute(
				this.screenName + Constants.CURRENT_ACTION,
				rec);
			if (rec.trim().equals(Constants.SEARCH)) {
				navBar.setCancel(true);//cancel = Constants.YES;
				session.setAttribute(this.screenName + Constants.CMTACTION,	Constants.SEARCH);
				navBar.setSearch(false);//search = Constants.NO;
				navBar.setCommit(true);//commit = Constants.YES;
				navBar.setInsert(false);//insert = Constants.NO;
			} else if (rec.trim().equals(Constants.INSERT)) {
				navBar.setCancel(true);//cancel = Constants.YES;
				session.setAttribute(this.screenName + Constants.CMTACTION,	Constants.INSERT);
				navBar.setSearch(false);//search = Constants.NO;
				navBar.setCommit(true);//commit = Constants.YES;
				navBar.setInsert(false);//insert = Constants.NO;
			} else if (rec.trim().equals(Constants.COPY)) {
				navBar.setCancel(true);//cancel = Constants.YES;
				table =	(Hashtable) session.getAttribute(this.screenName + Constants.CROW);
				session.setAttribute(this.screenName + Constants.CMTACTION,	Constants.INSERT);
				if (table != null) {
					session.setAttribute(this.screenName + Constants.CMTACTION,	Constants.INSERT);
					navBar.setSearch(false);//search = Constants.NO;
					navBar.setCommit(true);//commit = Constants.YES;
					navBar.setInsert(false);//insert = Constants.NO;
					this.idTable = (Hashtable) table.get(ComponentConstants.UNIQUEIDLIST);
					table = this.removeUniqueIds(table);
					session.setAttribute(this.screenName, table);
				}
			}
		}
		//2) Enable/disable the Navigation bar based on the role.
		if (screenName != null)
		{

			// Setting the Navigation Bar in Session
			if(navBar != null)
			navBar.setPermissionDependencies(this.permissionInfoObj);
			getSession().setAttribute(screenName + ComponentConstants.NAVIGATIONBARKEY,navBar);

			/*
			// Edited by ILYAS 03/08
			String cancel, commit, delete, update, last, next, first, previous, gto, copy, search , browse, insert;

			cancel  	= navBar.isCancel()?"Y":"N";
			commit  	= navBar.isCommit()?"Y":"N";
			delete  	= navBar.isDelete()?"Y":"N";
			update  	= navBar.isUpdate()?"Y":"N";
			last    	= navBar.isLast()?"Y":"N";
			first		= navBar.isFirst()?"Y":"N";
			next		= navBar.isNext()?"Y":"N";
			previous	= navBar.isPrevious()?"Y":"N";
			gto 		= navBar.isGoTo()?"Y":"N";
			copy 		= navBar.isCopy()?"Y":"N";
			search 		= navBar.isSearch()?"Y":"N";
			browse 		= navBar.isBrowse()?"Y":"N";
			insert 		= navBar.isInsert()?"Y":"N";



			if(this.permissionInfoObj!=null) //further filtering not applicable
			{
				session.setAttribute(screenName + Constants.COMMIT_ENABLED, commit);
				session.setAttribute(screenName + Constants.CANCEL_ENABLED, cancel);
			}
			//For projects other than MCP that do not use PermissionInfoObj security class.
			else
			{
				session.setAttribute(screenName + Constants.COMMIT_ENABLED, commit);
				session.setAttribute(screenName + Constants.CANCEL_ENABLED, cancel);
			}

			if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsDeleteAllowed())
			{
				session.setAttribute(screenName + Constants.DELETE_ENABLED, delete);
			}
			//For projects other than MCP that do not use PermissionInfoObj security class.
			else
			{
				session.setAttribute(screenName + Constants.DELETE_ENABLED, delete);
			}

			if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsEditAllowed())
			{
				session.setAttribute(screenName + Constants.UPDATE_ENABLED, update);
			}
			//For projects other than MCP that do not use PermissionInfoObj security class.
			else
			{
				session.setAttribute(screenName + Constants.UPDATE_ENABLED, update);
			}

			if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsInsertAllowed())
			{
				session.setAttribute(screenName + Constants.INSERT_ENABLED, insert);
				session.setAttribute(screenName + Constants.COPY_ENABLED, copy);
			}
			//For projects other than MCP that do not use PermissionInfoObj security class.
			else
			{
				session.setAttribute(screenName + Constants.INSERT_ENABLED, insert);
				session.setAttribute(screenName + Constants.COPY_ENABLED, copy);
			}

			if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsViewAllowed())
			{
				session.setAttribute(screenName + Constants.SEARCH_ENABLED, search);
				session.setAttribute(screenName + Constants.FIRST_ENABLED, first);
				session.setAttribute(screenName + Constants.NEXT_ENABLED, next);
				session.setAttribute(screenName + Constants.PREVIOUS_ENABLED,previous);
				session.setAttribute(screenName + Constants.LAST_ENABLED, last);
				session.setAttribute(screenName + Constants.GOTO_ENABLED, gto);
				session.setAttribute(screenName + Constants.BROWSE_ENABLED, browse);
			}
			//For projects other than MCP that do not use PermissionInfoObj security class.
			else
			{
				session.setAttribute(screenName + Constants.SEARCH_ENABLED, search);
				session.setAttribute(screenName + Constants.FIRST_ENABLED, first);
				session.setAttribute(screenName + Constants.NEXT_ENABLED, next);
				session.setAttribute(screenName + Constants.PREVIOUS_ENABLED,previous);
				session.setAttribute(screenName + Constants.LAST_ENABLED, last);
				session.setAttribute(screenName + Constants.GOTO_ENABLED, gto);
				session.setAttribute(screenName + Constants.BROWSE_ENABLED, browse);
			}
			*/
			if(this.permissionInfoObj!=null) //further filtering not applicable
			{
				session.setAttribute(screenName + Constants.NVENABLE, Constants.NO);
			}
			//For projects other than MCP that do not use PermissionInfoObj security class.
			else
			{
				session.setAttribute(screenName + Constants.NVENABLE, Constants.NO);
			}
		}
	}
	/**
	 * User can change the active message for the particular secreen.
	 * @param msg
	 */
	public void setActiveMSG(String msg){
		this.getSession().setAttribute(this.screenName+Constants.ACTIVE_MSG,msg);
	}
	/**
	 *  Use applySecurity (screenName + Constants.COMMIT_ENABLED,Constants.COMMIT,commit);
	 *  instead of session.setAttribute(screenName + Constants.COMMIT_ENABLED, commit);
	 *  for All the entries of NV...
	 * @param actionObj
	 * @param action
	 * @param rule
	 */
	protected void applySecurity(Object actionObj , String action,Object rule){
		if( this.getSecRule(action) != null ) {
		  if(this.getSecRule(action).equals(ComponentConstants.ALLOWED)){
			this.getSession().setAttribute((String)actionObj,rule);
		  }
		  else if(this.getSecRule(action).equals(ComponentConstants.HIDE)){
			this.getSession().setAttribute((String)actionObj,ComponentConstants.HIDE);
		  }
		  else if(this.getSecRule(action).equals(ComponentConstants.DISALLOWED)){
			this.getSession().setAttribute((String)actionObj,Constants.NO);
		  }

		}
		//session.setAttribute(screenName + Constants.COMMIT_ENABLED, commit);
	}
	protected Object getSecRule(String action){
		Hashtable sectbl = (Hashtable) this.getSession().getAttribute(Constants.SECTABLE);
		if(sectbl!=null){
			return sectbl.get(action);
		}
		return null;
	}
	/**
	 * Default sequence Resetting is 0 for the current screen.
	 *
	 */
	public void reSetSequence(){
		getSession().setAttribute(this.screenName+Constants.NAVIGATIONROWNUM, String.valueOf(0));
	}
	/**
	 * User defined sequece Resetting for the current screen.
	 *
	 */

	public void reSetSequence(int nSeq){
		getSession().setAttribute(this.screenName+Constants.NAVIGATIONROWNUM, String.valueOf(nSeq));
	}
	/**
	 * User defined sequece Resetting for the for the current screen.
	 *
	 */

	public void reSetSequence(String sName, int nSeq){
		getSession().setAttribute(sName+Constants.NAVIGATIONROWNUM, String.valueOf(nSeq));
	}

	/**
	 * defalut sequece Resetting for the for the particular screen.
	 *
	 */

	public void reSetSequence(String sName){
		getSession().setAttribute(sName+Constants.NAVIGATIONROWNUM, String.valueOf(0));
	}
	/**
	 * Here is the way by using which you can have your required mapping for the front-end. For example the database table contain a filed with value “A” and on front end you want to display “ALL” against this value, So please have a look below.
			List maping = new ArrayList();
               String p[]  = new String [2];
                      p[0] = "I";   // Value in database table
                      p[1] = "IBB"; // Display this value against db value
               	maping.add(p);
                  p  = new String [2];
                        p[0] = "C";    // Value in database table
                        p[1] = "CALL"; // Display this value against db value
                  maping.add(p);

         nvHandler.resetDBFieldDataMapping("call_status",maping); // Where “call_status” is the name of the particular table column for which we need mapping.

	 * @param cName
	 * @param mappingList
	 */
	public void resetDBFieldDataMapping(String cName, List mappingList){
		this.columnMappingTable.put(cName,mappingList);
	}

	public void applyFormat(String cName, String oldFormat,String  newFormat){
		formatMappingTable.put(cName,new String[]{oldFormat,newFormat});
	}
	protected void removeUnrelatedContainerInfo(HttpSession ses ){

		if(ses.getAttribute(ComponentConstants.CURRENT_SCREEN)!=null){
			if(ses.getAttribute(ComponentConstants.PREVIOUS_SCREEN)!=null){
				// Remove the previous Screen values from the Memory...
				if( !ses.getAttribute(ComponentConstants.CURRENT_SCREEN).equals(ses.getAttribute(ComponentConstants.PREVIOUS_SCREEN))){
					Enumeration enumeration = ses.getAttributeNames();
					System.out.println("================= ()> Removing Unrelated Container Data ()> ===================");
					// for lock button
					//this.setLocked(false);
					/*try{
						session.removeAttribute(this.screenName+ComponentConstants.UNLOCK);
					}catch(Exception e){
					}*/
					// End for lock button
					String unrelSC= (String) ses.getAttribute(ComponentConstants.PREVIOUS_SCREEN);
					String token=null;
					Vector v= new Vector();
					while(enumeration.hasMoreElements()){
							token =(String)enumeration.nextElement();
							//System.out.println(" TOKEN "+token);
						if(token.startsWith(unrelSC)){
							v.add(token);
							System.out.println(" REMOVING : "+token);
						}
					}
					for ( int i=0; i<v.size();i++){
						// EMPTY TABLE and then remove it from the session ....
						if(v.get(i)!=null && ((String)v.get(i)).endsWith(Constants.CROW)){
							//System.out.println("Hastable: "+(Hashtable)ses.getAttribute((String)v.get(i)));

							Hashtable table=(Hashtable)ses.getAttribute((String)v.get(i));
							Enumeration enumt = table.elements();
							Object tokent=null;
							Vector vt= new Vector();
							while(enumt.hasMoreElements()){
									tokent =enumt.nextElement();
									vt.add(tokent);
									System.out.println(" REMOVING CROW -> : "+tokent);

								}
							for(int k=0;k<vt.size(); k++){
								table.remove(vt.get(k));
							}
							vt.removeAllElements();
							vt.setSize(0);
						}
						ses.removeAttribute((String)v.get(i));
					}
					v.removeAllElements();
					v.setSize(0);

					System.out.println("================== ()< Removing Unrelated Container Data ()< ==================");
					ses.setAttribute(ComponentConstants.PREVIOUS_SCREEN, ses.getAttribute(ComponentConstants.CURRENT_SCREEN));
			   }
			}else{
				ses.setAttribute(ComponentConstants.PREVIOUS_SCREEN, ses.getAttribute(ComponentConstants.CURRENT_SCREEN));
			}
				ses.setAttribute(ComponentConstants.CURRENT_SCREEN ,this.getScreenName());
		}else{
				ses.setAttribute(ComponentConstants.CURRENT_SCREEN ,this.getScreenName());
		}
	}
	/**
	 * This method return the proper sql to get the total record count for this specfic query.
	 * @param sql
	 * @return
	 */
	public String getCountQry(String sql){

		Pattern pFrom = Pattern.compile("\\sfrom\\s",Pattern.CASE_INSENSITIVE);
		Pattern pOrder = Pattern.compile("\\sorder\\s",Pattern.CASE_INSENSITIVE);
		Pattern pUnion = Pattern.compile("\\sunion\\s",Pattern.CASE_INSENSITIVE);

		// Added by ILYAS [09/12/2005]
		Pattern pGroupBy = Pattern.compile("\\sgroup by\\s",Pattern.CASE_INSENSITIVE);

		java.util.regex.Matcher m = pFrom.matcher(sql);

		if (m.find()) {
			// m.appendReplacement(sb, "dog");
			sql = "Select COUNT(*) "+ sql.substring(m.start());
		 }
		 m = pOrder.matcher(sql);
		if (m.find()) {
			// m.appendReplacement(sb, "dog");
			//System.out.println("Select * "+ s1.substring(m.start()));
			sql = sql.substring(0,m.start());
		 }
		m = pUnion.matcher(sql);
		if (m.find()) {
			String s1="";
			String s2="";
			// m.appendReplacement(sb, "dog");
			//System.out.println("Select * "+ s1.substring(m.start()));
			s1 = sql.substring(0,m.start());
			s2 = sql.substring(m.start()+5);
			sql = s1+" UNION "+ getCountQry(s2);
		 }

		// Added by ILYAS [09/12/2005]
		m = pGroupBy.matcher(sql);
		if (m.find()) {
			sql = sql.substring(0,m.start());
		 }


		 System.out.println("SQL FOR COUNTER : [ "+ sql+" ]");
	return sql;
	}
	/**
	 * This function pupolate the table of uniqe keys avaiable through browse action in each row. This function is for internal use of components.
	 * Please call this  before calling  getEditValueKey(String key);
	 * @param form
	 */
	public void populateEditValueKeys(BaseForm form){
		String key=null;
		String value=null;
		if( form.getId()!=null){
			System.out.println("[Form.getId()]" + form.getId());
			StringTokenizer token= new StringTokenizer(form.getId(),"}");
			//StringBuffer buf=new StringBuffer();
			if(token.hasMoreTokens()){
				String s1=token.nextToken();
				String s2= ComponentsUtil.replaceStr(s1,"{","" );
				s2 = ComponentsUtil.replaceStr(s2,"(","" );
				s2 = ComponentsUtil.replaceStr(s2,")","" );

				key = s2.substring(0,s2.indexOf("="));
				value = s2.substring(s2.indexOf("=")+1);
				//buf.append(s2);
				editValuesTable.put(key,value);
				while(token.hasMoreTokens()){
					s1=token.nextToken();
					s2= ComponentsUtil.replaceStr(s1,"{","" );
					s2 = ComponentsUtil.replaceStr(s2,"(","" );
					s2 = ComponentsUtil.replaceStr(s2,")","" );

					key = s2.substring(0,s2.indexOf("="));
					value = s2.substring(s2.indexOf("=")+1);
					//buf.append(s2);
					editValuesTable.put(key,value);
				}
			}
		}
		System.out.println("VALUES TABLE "+ this.editValuesTable );
	}
	/**
	 * This function is used to get values of uniqe keys avaiable through browse action in each row. This function is for internal use of components.
	 * Please call this  after calling  populateEditValueKeys();
	 *
	 * @param value
	 * @return
	 */
	public String getEditValueKey(String  key){
		return (String) editValuesTable.get(key);
	}

	public String getINSFormat(String key){
		return (String) insFormatTable.get(key);
	}
	// Format conversion  that is acceptable for DB e.g., Date, Time Decimal etc. while inserting....
	public void populateINSFormatKeys(BaseForm form){

		String key=null;
		String value=null;
		if( form.getInsformat()!=null){
			System.out.println("[Form.getId()]" + form.getInsformat());
			StringTokenizer token= new StringTokenizer(form.getInsformat(),"}");
			//StringBuffer buf=new StringBuffer();
			if(token.hasMoreTokens()){
				String s1=token.nextToken();
				String s2= ComponentsUtil.replaceStr(s1,"{","" );
				s2 = ComponentsUtil.replaceStr(s2,"(","" );
				s2 = ComponentsUtil.replaceStr(s2,")","" );

				key = s2.substring(0,s2.indexOf("="));
				value = s2.substring(s2.indexOf("=")+1);
				//buf.append(s2);
				//editValuesTable.put(key,value);
				insFormatTable.put(key,value);
				while(token.hasMoreTokens()){
					s1=token.nextToken();
					s2= ComponentsUtil.replaceStr(s1,"{","" );
					s2 = ComponentsUtil.replaceStr(s2,"(","" );
					s2 = ComponentsUtil.replaceStr(s2,")","" );

					key = s2.substring(0,s2.indexOf("="));
					value = s2.substring(s2.indexOf("=")+1);
					//buf.append(s2);
					//editValuesTable.put(key,value);
					insFormatTable.put(key,value);

				}
			}
		}
		System.out.println("VALUES TABLE "+ this.editValuesTable );
	}
	public Vector getINSFormatKeys(){
		Enumeration enumeration = insFormatTable.keys();
		Vector v = new Vector();
		while(enumeration.hasMoreElements()){
		 v.add(enumeration.nextElement());
		}
		return v;
	}
	public void ignoreINSErrAction(){

		// Edited by ILYAS 03/08
		getSession().removeAttribute(screenName + Constants.ERRORVALUE);
		getSession().removeAttribute(screenName + Constants.ININFO);
		//this.table = ComponentsUtil.getRequestParametersTable(request);
		navBar.setCancel(false);//cancel = Constants.NO;
		session.removeAttribute(this.screenName + Constants.CMTACTION);
		navBar.setSearch(false);//search = Constants.NO;
		navBar.setCommit(false);//commit = Constants.NO;
		navBar.setInsert(true);//insert = Constants.YES;

		//copy = delete =	update = first = next =	previous = last = gto = browse = Constants.NO;
		navBar.setCopy(false);
		navBar.setDelete(false);
		navBar.setUpdate(false);
		navBar.setFirst(false);
		navBar.setNext(false);
		navBar.setPrevious(false);
		navBar.setLast(false);
		navBar.setGoTo(false);
		navBar.setBrowse(false);

		setNavigation();
	}

	/*
	 * @author		Edwin Yaqub
	 * @description	A simple setter method for this.permissionInfoObj
	 * @param		permissionInfoObj - an object of type PermissionInfoObj
	 * @return		void
	 */

	public void setPermissionInfoObj(PermissionInfoObj permissionInfoObj)
	{
		this.permissionInfoObj = permissionInfoObj;
	}

	/*
	 * @author		Edwin Yaqub
	 * @description	A simple getter method for this.permissionInfoObj
	 * @param		None
	 * @return		Instance level variable 'permissionInfoObj'
	 */
	public PermissionInfoObj getPermissionInfoObj()
	{
		return this.permissionInfoObj;
	}

	/*
	 * @author 			Edwin Yaqub
	 * @description		This method is called from setErrNavigation() and restorePreviousState() as well as from
	 * 					derived classes to set values of critical keys in session. These keys control behaviour of
	 * 					Navigation Bar by enabling or disabling Navigation buttons based on privileges of user.
	 * @param			None
	 * @return			void
	 */
	public void setNavigationInSession()
	{
/*		NavigationBar navBar = new NavigationBar();

		if(this.permissionInfoObj!=null) //further filtering not applicable.
		{
			//getSession().setAttribute(screenName + Constants.COMMIT_ENABLED,commit);
			//getSession().setAttribute(screenName + Constants.CANCEL_ENABLED,cancel);
			if(commit != null && commit.equalsIgnoreCase("Y"))
			navBar.setCommit(true);
			if(cancel != null && cancel.equalsIgnoreCase("Y"))
			navBar.setCancel(true);
		}
		//For projects other than MCP that do not use PermissionInfoObj security class.
		else
		{
			//getSession().setAttribute(screenName + Constants.COMMIT_ENABLED,commit);
			//getSession().setAttribute(screenName + Constants.CANCEL_ENABLED,cancel);
			if(commit != null && commit.equalsIgnoreCase("Y"))
			navBar.setCommit(true);
			if(cancel != null && cancel.equalsIgnoreCase("Y"))
			navBar.setCancel(true);
		}

		if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsDeleteAllowed())
		{
			//getSession().setAttribute(screenName + Constants.DELETE_ENABLED,delete);
			if(delete != null && delete.equalsIgnoreCase("Y"))
			navBar.setDelete(true);

		}
		//For projects other than MCP that do not use PermissionInfoObj security class.
		else
		{
			//getSession().setAttribute(screenName + Constants.DELETE_ENABLED,delete);
			if(delete != null && delete.equalsIgnoreCase("Y"))
			navBar.setDelete(true);
		}

		if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsEditAllowed())
		{
			//getSession().setAttribute(screenName + Constants.UPDATE_ENABLED,update);
			if(update != null && update.equalsIgnoreCase("Y"))
			navBar.setUpdate(true);

		}
		//For projects other than MCP that do not use PermissionInfoObj security class.
		else
		{
			//getSession().setAttribute(screenName + Constants.UPDATE_ENABLED,update);
			if(update != null && update.equalsIgnoreCase("Y"))
			navBar.setUpdate(true);
		}

		if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsInsertAllowed())
		{
			//getSession().setAttribute(screenName + Constants.INSERT_ENABLED,insert);
			//getSession().setAttribute(screenName + Constants.COPY_ENABLED,copy);
			if(insert != null && insert.equalsIgnoreCase("Y"))
			navBar.setInsert(true);
			if(copy != null && copy.equalsIgnoreCase("Y"))
			navBar.setCopy(true);
		}
		//For projects other than MCP that do not use PermissionInfoObj security class.
		else
		{
			//getSession().setAttribute(screenName + Constants.INSERT_ENABLED,insert);
			//getSession().setAttribute(screenName + Constants.COPY_ENABLED,copy);
			if(insert != null && insert.equalsIgnoreCase("Y"))
			navBar.setInsert(true);
			if(copy != null && copy.equalsIgnoreCase("Y"))
			navBar.setCopy(true);
		}

		if(this.permissionInfoObj!=null && this.permissionInfoObj.getIsViewAllowed())
		{
//			getSession().setAttribute(screenName + Constants.SEARCH_ENABLED,search);
//			getSession().setAttribute(screenName + Constants.FIRST_ENABLED,first);
//			getSession().setAttribute(screenName + Constants.NEXT_ENABLED,next);
//			getSession().setAttribute(screenName + Constants.PREVIOUS_ENABLED,previous);
//			getSession().setAttribute(screenName + Constants.LAST_ENABLED,last);
//			getSession().setAttribute(screenName + Constants.GOTO_ENABLED, gto);
//			getSession().setAttribute(screenName + Constants.BROWSE_ENABLED, browse);

			if(search != null && search.equalsIgnoreCase("Y"))
			navBar.setSearch(true);
			if(first != null && first.equalsIgnoreCase("Y"))
			navBar.setFirst(true);
			if(next != null && next.equalsIgnoreCase("Y"))
			navBar.setNext(true);
			if(previous != null && previous.equalsIgnoreCase("Y"))
			navBar.setPrevious(true);
			if(last != null && last.equalsIgnoreCase("Y"))
			navBar.setLast(true);
			if(gto != null && gto.equalsIgnoreCase("Y"))
			navBar.setGoTo(true);
			if(browse != null && browse.equalsIgnoreCase("Y"))
			navBar.setBrowse(true);


		}
		//For projects other than MCP that do not use PermissionInfoObj security class.
		else
		{
//			getSession().setAttribute(screenName + Constants.SEARCH_ENABLED,search);
//			getSession().setAttribute(screenName + Constants.FIRST_ENABLED,first);
//			getSession().setAttribute(screenName + Constants.NEXT_ENABLED,next);
//			getSession().setAttribute(screenName + Constants.PREVIOUS_ENABLED,previous);
//			getSession().setAttribute(screenName + Constants.LAST_ENABLED,last);
//			getSession().setAttribute(screenName + Constants.GOTO_ENABLED, gto);
//			getSession().setAttribute(screenName + Constants.BROWSE_ENABLED, browse);
			if(search != null && search.equalsIgnoreCase("Y"))
			navBar.setSearch(true);
			if(first != null && first.equalsIgnoreCase("Y"))
			navBar.setFirst(true);
			if(next != null && next.equalsIgnoreCase("Y"))
			navBar.setNext(true);
			if(previous != null && previous.equalsIgnoreCase("Y"))
			navBar.setPrevious(true);
			if(last != null && last.equalsIgnoreCase("Y"))
			navBar.setLast(true);
			if(gto != null && gto.equalsIgnoreCase("Y"))
			navBar.setGoTo(true);
			if(browse != null && browse.equalsIgnoreCase("Y"))
			navBar.setBrowse(true);

		}
		*/

		/*
		if(this.permissionInfoObj!=null) //further filtering not applicable.
		{
			getSession().setAttribute(screenName + Constants.NVENABLE,Constants.YES);
			getSession().setAttribute(screenName + ComponentConstants.NVSTATE,this.nvStateTable);
		}
		//For projects other than MCP that do not use PermissionInfoObj security class.
		else
		{
			getSession().setAttribute(screenName + Constants.NVENABLE,Constants.YES);
			getSession().setAttribute(screenName + ComponentConstants.NVSTATE,this.nvStateTable);
		}
		*/

		// Setting Navigation State
		getSession().setAttribute(screenName + Constants.NVENABLE,Constants.YES);
		if(navBar_Prev != null)
		navBar_Prev.setPermissionDependencies(this.permissionInfoObj);
		getSession().setAttribute(screenName + ComponentConstants.NVSTATE,this.navBar_Prev);

		// Now setting the Navigation Bar in Session
		if(navBar != null)
		navBar.setPermissionDependencies(this.permissionInfoObj);
		getSession().setAttribute(screenName + ComponentConstants.NAVIGATIONBARKEY,navBar);

	}

	protected void setLastActionInSession(HttpSession session, BaseForm form) {
		if(Constants.SEARCH.equals(form.getRec())
			|| Constants.INSERT.equals(form.getRec())
			|| Constants.BROWSE.equals(form.getRec())
			|| Constants.COPY.equals(form.getRec())
			|| Constants.UPDATE.equals(form.getRec())
			|| Constants.MUPDATE.equals(form.getRec())
			|| Constants.BROWSE_EDIT.equals(form.getRec())
			|| Constants.DELETE.equals(form.getRec())) {
			System.out.println("Setting previous value in session to.......="+form.getRec());
			session.setAttribute(Constants.PREVIOUS_ACTION_VAL, form.getRec());
		}

		if("his".equalsIgnoreCase(form.getRec())){
			session.setAttribute("PARENT_PREV_ACTION_VAL",
						session.getAttribute(Constants.PREVIOUS_ACTION_VAL));
			session.setAttribute("PARENT_TASKS_VAL",
						session.getAttribute(CardsConstants.TASK_REQUEST_PARAM_NAME));
		}

	}
	/**
	 * @return
	 */
	public String getKeyColName() {
		return keyColName;
	}

	/**
	 * @param string
	 */
	public void setKeyColName(String string) {
		keyColName = string;
	}
//	for lock button
/**
 * @return
 */
public boolean isLocked() {
	return isLocked;
}

/**
 * @param b
 */
public void setLocked(boolean b) {
	isLocked = b;
}
//End for lock button
}