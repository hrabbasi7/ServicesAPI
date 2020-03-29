/*
 * Created on Feb 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.util;

import java.util.*;
/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ComponentConstants {
	public static String ACTIVE_GRID_COMPONENTS = "HActiveComponents";
	//public static String HIDDEN_GRID_KEY_COLUMN = "HGridKeyColumn";

	// New Constants for Nav Bar Object
	public static String NAVIGATIONBARKEY = "navBarObj";
	// End
	public static final String BROWSEID="BRID";
	public static final String BROWSEACTIONSCREEN="BRACT";
	public static final String DISABLE = "DIS";
	public static final String ENABLE = "ENABLE";
	public static final String ALLOWED = "ALLOWED";
	public static final String DISALLOWED = "DISALLOWED";
	public static final String SELECT = "SELECT";
	public static final String HIDE = "HIDE";
	public static final String PRVIOUS_ACTION="PREVACT";
	public static final String UNIQUEIDLIST="UNIQUEIDLIST";
	public static final String UNIQUEID="UNIQUEID";
	public static final String NVSTATE="NVSTATE";
	public static final String FIRST="fst";
	public static final String PREVIOUS="prv";
	public static final String NEXT="nxt";
	public static final String LAST="lst";
	public static final String SEARCH="ser";
	public static final String HISTORY="his";
	public static final String COMMIT="cmt";
	public static final String UPDATE="upd";
	public static final String MUPDATE="mupd";
	public static final String DELETE="del";
	public static final String COPY="cpy";
	public static final String CANCEL="cncl";
	public static final String INSERT="ins";
	public static final String GOTO="goto";
	public static final String BROWSE="browse";
	public static final String ASC="ASC";
	public static final String DESC="DESC";
	public static final String CURRENT_SCREEN="CSREEN";
	public static final String PREVIOUS_SCREEN="PSREEN";
	public static final String TABS_RECORD_COUNTER="tabscounter";

/////////////////////////////////////////////

	public static final String SORTCOLUMN="SORTCOLUMN";
	public static final String SORTORDER="SORTORDER";
	public static final String SORTNAME="SORTNAME";

	public static final String PSORTCOLUMN="PSORTCOLUMN";
	public static final String PSORTORDER="PSORTORDER";
	public static final String PSORTNAME="PSORTNAME";

	public static final String SORTRULE="SORTRULE";
	public static final String SORTING="SORTING";

	public static final String PREVIOUSSORTING="PREVIOUSSORTING";

	//Fields added by Edwin for preservation of original search query made on the first tab.
	public static final String ORIGINAL_SEARCH_QUERY_MASTERTAB = "originalquery";
	public static final String CURRENT_RECORD = "currentrecord";

	private final static Hashtable actionTable=new Hashtable(13);
	private final static Hashtable actionTitleTable=new Hashtable(13);

	public static final String singleQuotHtmlCode = "&#39;";
	public static final String doubleQuotHtmlCode = "&quot;";
	public static final String ampersandHtmlCode = "&amp;";
	public static final String lessThanHtmlCode = "&lt;";
	public static final String greaterThanHtmlCode = "&gt;";

	//Fields added by Edwin for implementing implicit Server Side Validations.
	public static final String HIGHLIGHT_CONTROL = "HIGHLIGHT_CONTROL";//Marker used for each DV control as a key in Request - used to display error sign along DV controls.
	public static final String HIGHLIGHT_CONTROL_COUNTER = "counter";//Counter to be appended aside the error sign.

	public static final String VALUE_CONTAINER_NAME = "VALUE_CONTAINER_NAME";//Key in Session used to store screen name.

	public static final String VALIDATION_PROPERTIES = "CONTROLS_VALIDATION_PROPERTIES"; //Key for a HashMap containing:
	public static final String VALIDATE_REQUIRED= "VALIDATE_REQUIRED";//This will be in the above mentioned HashMap.
	public static final String VALIDATE_TYPE = "VALIDATE_TYPE";//This will be in the above mentioned HashMap.
	public static final String IS_NULLABLE = "IS_NULLABLE";//This will be in the above mentioned HashMap.
	public static final String PROMPT_LABEL = "PROMPT_LABEL";//This will be in the above mentioned HashMap.
	public static final String DISPLAY_FORMAT = "DISPLAY_FORMAT";//This will be in the above mentioned HashMap.
	public static final String LEFT_DIGITS = "LEFT_DIGITS";//This will be in the above mentioned HashMap.
	public static final String RIGHT_DIGITS = "RIGHT_DIGITS";//This will be in the above mentioned HashMap.
	public static final String MAX_LENGTH = "MAX_LENGTH";//This will be in the above mentioned HashMap.

	//for lock button
	public static final String UNLOCK = "unlock";
	public static final String LOCK = "lock";
	public static final String NO = "N";
	public static final String YES = "Y";
	//End for lock button

	public static final String PREV_VALID_STATE_HASHTABLE = "PREV_VALID_STATE_HASHTABLE";//This is used as a key to hold the previous valid states hashtable (incase multiple errors come consecutively).

	static{
		actionTable.put("FIRST",FIRST);
		actionTable.put("PREVIOUS",PREVIOUS);
		actionTable.put("NEXT",NEXT);
		actionTable.put("LAST",LAST);
		actionTable.put("SEARCH",SEARCH);
		actionTable.put("COMMIT",COMMIT);
		actionTable.put("UPDATE",UPDATE);
		actionTable.put("MUPDATE",MUPDATE);
		actionTable.put("DELETE",DELETE);
		actionTable.put("COPY",COPY);
		actionTable.put("CANCEL",CANCEL);
		actionTable.put("INSERT",INSERT);
		actionTable.put("GOTO",GOTO);
		actionTable.put("BROWSE",BROWSE);
		actionTable.put("HISTORY",HISTORY);
		actionTable.put("UNLOCK",UNLOCK);
		actionTable.put("LOCK",LOCK);
		// Titles....


		actionTitleTable.put("FIRST"," First Record ");
		actionTitleTable.put("PREVIOUS"," Previous Record  ");
		actionTitleTable.put("NEXT"," Next Record  ");
		actionTitleTable.put("LAST"," Last Record  ");
		actionTitleTable.put("SEARCH"," Search Record ");
		actionTitleTable.put("HISTORY"," History ");
		actionTitleTable.put("UNLOCK"," Unlock ");
		actionTitleTable.put("LOCK"," Lock ");
		actionTitleTable.put("COMMIT"," Commit Record ");
		actionTitleTable.put("UPDATE"," Update Record ");
		actionTitleTable.put("MUPDATE"," Update Records ");
		actionTitleTable.put("DELETE"," Delete Record ");
		actionTitleTable.put("COPY"," Copy Record ");
		actionTitleTable.put("CANCEL"," Cancel ");
		actionTitleTable.put("INSERT"," Insert Record ");
		actionTitleTable.put("BROWSE"," Browse Records ");
		actionTitleTable.put("GOTO"," Go To Record +/- ");
	}
	public static String getAction(Object key){
		if(key==null)
		key="";
		key= ((String)key).toUpperCase();
		return    actionTable.get(key)!=null ? (String)actionTable.get(key) :"";
	}
	public static Object getActionTitle(Object key){
		if(key==null)
		key="";
		key= ((String)key).toUpperCase();
		return actionTitleTable.get(key)!=null ? actionTitleTable.get(key) :"";
	}
}
