package com.i2c.component.backend.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;


import com.i2c.cards.*;
import com.i2c.cards.util.*;
import com.i2c.util.*;
import com.i2c.component.base.*;
import com.i2c.component.util.ComponentConstants;
import com.i2c.component.util.ComponentsUtil;
import com.i2c.cards.security.PermissionInfoObj;

/**
 * @author Muhammad ILYAS
 *
 * @DESCRIPTION
 * The purpose of this class is to handle the navigation.
 * This class deals with the browse views. The developers uses this class
 * in their Actions that are developed for browse views.
 * Here is the detail of the functionality this class provides
 * 1) Deals with the navigation bar
 * 2) Deals with the  sequence
 * 3) Populates the container for the data fields
 *
 *
 */

public class NavigationHandler4Browse extends NavigationBase{

	protected int incGoto = 0;

	public NavigationHandler4Browse() {
		super();
	}

	public NavigationHandler4Browse(HttpServletRequest request ) {
		super();
		setSession(request.getSession(false));
	}

	public int executeAction(BaseForm form ,java.sql.Connection conn,BaseHome query, HttpServletRequest request )
	{

		//--- Getting Starting Row and Total Rows Of the Screen
		//--- This is because after further operations session is disturbed
		HttpSession session = request.getSession();
		String toRows =(String)request.getSession().getAttribute(screenName + "to");
		String fRows =(String)request.getSession().getAttribute(this.screenName + Constants.NAVIGATIONROWNUM);

		String sortingRule=null;
		this.query = query;
		this.request = request;
		this.form=form;
		this.conn=conn;

		//setting the previous action in session for security module
		this.setLastActionInSession(request.getSession(false), form);

		//Getting the 'Task Permission Object' from session to implement enabling/disabling of Navigation Bar buttons.
		PermissionInfoObj permissionInfoObj = null;
		if(this.request.getSession().getAttribute(CardsConstants.TASK_PERMISSION_INFO_OBJ)!=null){
			permissionInfoObj = (PermissionInfoObj) this.request.getSession().getAttribute(CardsConstants.TASK_PERMISSION_INFO_OBJ);
		}

		if(query!=null){
			if(columnMappingTable.size()>0 )
			query.setMappingTable(this.columnMappingTable);
			if(formatMappingTable.size()>0)
			query.setFormatMappingTable(formatMappingTable);
			query.setNVHandler(this);
		}
		if(rows<=0){ rows=1; }
		int totalrows=0;
		//System.out.println( dataSource );
		try{
			String rec = form.getRec();//request.getParameter(Constants.NVREC);

			if(!ComponentsUtil.isNullOrEmptyString(rec)){
				if(rec.trim().equals(""))
					rec = "browse";
			}
			rec = ComponentsUtil.fixNull(rec);

			this.setNvNumstr(this.screenName+Constants.NAVIGATIONROWNUM);
			this.setCmtActionStr(this.screenName+Constants.CMTACTION);
			this.setCRowStr(this.screenName+Constants.CROW);
			this.setWhereClauseStr(this.screenName+Constants.WHERECLASE);
			this.setTotalRecStr(this.screenName+Constants.TOTALREC);
			this.setContainer(this.screenName);
			setSession(request.getSession(false));

			String qry_test = (String) getSession().getAttribute(whereClauseStr);

			//----Removing Unrelated Container Info
			/* Other than POP Up window without Menu */
			String isMenu = request.getParameter("isMenu");
			if(isMenu == null){
				isMenu = "Y";
			}
			if(isMenu.trim().equalsIgnoreCase("Y")){
				removeUnrelatedContainerInfo(request.getSession(false));
			}
//			for Lock / Unlock ====================
			System.out.println("LOCKING-->navigationhandler4browse:::=rec="+rec);
			if(rec.equals("lock")){
				session.setAttribute(this.screenName+"unlock","N");
			}
			System.out.println("LOCKING-->navigationhandler4browse:::=screen name unlock="+session.getAttribute(this.screenName+"unlock"));
			System.out.println("LOCKING-->navigationhandler4browse:::=Session id in browse="+session.getId());
			System.out.println("LOCKING-->navigationhandler4browse:::=applyDoublePassword="+ComponentsUtil.applyDoublePassword(conn));
			if(ComponentsUtil.applyDoublePassword(conn)){
				System.out.println("LOCKING-->navigationhandler4browse:::=attribute set from unlock servlet="+session.getAttribute(this.screenName+ComponentConstants.UNLOCK));
				if(!CommonUtilities.isNullOrEmptyString((String)session.getAttribute(this.screenName+ComponentConstants.UNLOCK))){
					System.out.println("LOCKING-->navigationhandler4browse:::=attribute set from unlock servlet="+session.getAttribute(this.screenName+ComponentConstants.UNLOCK));
					if(this.isLocked() && session.getAttribute(this.screenName+ComponentConstants.UNLOCK).equals(ComponentConstants.NO)){
						System.out.println("LOCKING-->navigationhandler4browse:::=in setting unlock button true=");
						navBar.setUnlock(true);
						navBar.setInsert(false);
						navBar.setDelete(false);
						navBar.setCopy(false);
						navBar.setUpdate(false);
						request.setAttribute("isUnlock","NO");
					}else{
						/*if(ses.getAttribute("preserveWhereClause")!=null){
							ses.removeAttribute("preserveWhereClause");
						}*/
						System.out.println("LOCKING-->navigationhandler4browse:::=in setting unlock button false=");
						request.setAttribute("isUnlock","YES");
						navBar.setLock(true);
						navBar.setUnlock(false);
					}
				}else{
					System.out.println("LOCKING-->navigationhandler4browse:::=isLocked 2="+this.isLocked());
					if(this.isLocked()){
						System.out.println("LOCKING-->navigationhandler4browse:::=in setting unlock button true 2=");
						navBar.setUnlock(true);
						navBar.setInsert(false);
						navBar.setDelete(false);
						navBar.setCopy(false);
						navBar.setUpdate(false);
						//session.setAttribute(this.screenName+"preserveWhereClause","Y");
						request.setAttribute("isUnlock","NO");
					}else{
						System.out.println("LOCKING-->navigationhandler4browse:::=in setting unlock button false 2=");
						request.setAttribute("isUnlock","YES");
						navBar.setLock(true);
						navBar.setUnlock(false);
					}
				}
			}else{
				System.out.println("LOCKING-->navigationhandler4browse:::=in else double password not implement=");
				navBar.setUnlock(false);
				navBar.setLock(false);
				request.setAttribute("isUnlock","NO");
				request.setAttribute("ShowUnlock","NO");
			}
			System.out.println("LOCKING-->navigationhandler4browse:::=isUnlock : request="+request.getAttribute("isUnlock"));
			System.out.println("LOCKING-->navigationhandler4browse:::=ShowUnlock : request="+request.getAttribute("ShowUnlock"));
			//End Lock / Unlock ====================

			//------TAB Related
			if(form.getTabUse()!=null && (form.getTabUse().toUpperCase()).indexOf("1")!=-1) {
				this.session.removeAttribute((String)this.session.getAttribute(this.screenName+"MTAB"));
				this.session.removeAttribute(this.screenName+"MTAB");
			}
			else if(form.getTabname()!=null  && form.getTabname().length()>0){
				this.session.setAttribute(this.screenName+"MTAB", form.getTabname());
					//this.session.setAttribute(form.getMaintabname(), form.getTabname());
			}

			//----Handle Sort Control If Specified
			sortingRule = handleSort(form, sortingRule);


			//----Setting Navigation Number
			//----Which will effect on Navigation Buttons Also
			String navigationnumstr = (String)getSession().getAttribute(nvNumstr);
			if( navigationnumstr ==null){
				navigationnumstr="1";
			}
			if(form.getRecno()!=null){
				navigationnum = Integer.parseInt(form.getRecno());
			}else{
				navigationnum = Integer.parseInt(navigationnumstr);
			}
			if(navigationnum<=0){
				navigationnum=1;
				// Edited by ILYAS 03/08
				navBar.setFirst(false);//first=Constants.NO;
				navBar.setPrevious(false);//previous=Constants.NO;
			}
			//-----End Setting Navigation Number

			//----Set Sorting Rule
			sortingRule =(String)getSession().getAttribute(screenName+ComponentConstants.SORTRULE);
			//----If Sorting Rule is already specified
			if(getSession().getAttribute(screenName+ComponentConstants.PREVIOUSSORTING)!=null ) {
				if(sortingRule != null)
				sortingRule = sortingRule + " , " +getSession().getAttribute(screenName+ComponentConstants.PREVIOUSSORTING);
			}


			if(rec!=null){
				//---Processing Requested Function
				//process(rec);
			}//end if(rec != null)
			if(rec != null){
				if(rec.trim().equals("")){
					rec = "ser";
				}
			}
			String qry = (String) getSession().getAttribute(whereClauseStr);

			// Updated to get Where Clause for Grid too
			//if(qry == null)
			//qry = form.getWhereClause();

			if(qry==null){
				qry=" ";
			}
			//System.out.println("Where Clause"+qry);
			System.out.println(query.getSqlStart()+ qry + query.getSqlEnd());
			//-----MUpdate
			if(rec != null){
				if (rec.equalsIgnoreCase(Constants.MUPDATE)) {
				  NavigationUtil.processMUpdate(this,toRows,fRows);
				  //this.restorePreviousNVState((Hashtable) getSession().getAttribute(screenName + ComponentConstants.NVSTATE));
				  }
				if (rec.equalsIgnoreCase(Constants.DELETE)) {
					query.delete("",conn);
				  //this.restorePreviousNVState((Hashtable) getSession().getAttribute(screenName + ComponentConstants.NVSTATE));
				  }
				if (rec.equalsIgnoreCase(Constants.INSERT)) {
					query.insert(form,conn);
				  //this.restorePreviousNVState((Hashtable) getSession().getAttribute(screenName + ComponentConstants.NVSTATE));
				  }
				if (rec.equalsIgnoreCase(Constants.UPDATE)) {
					query.update("",form,conn);
				  //this.restorePreviousNVState((Hashtable) getSession().getAttribute(screenName + ComponentConstants.NVSTATE));
				  }
				  //-----End MUpdate
			}
			String totalRec=null;
			if(CommonUtilities.trim(CommonUtilities.fixNull(query.getCountQuery())).equals("")){
				System.out.println("CCCCOUNT QUERY IN BROSER:="+query.getSqlStart()+ qry + query.getSqlEnd());
				totalrows = query.getSqlCount(getCountQry(query.getSqlStart()+ qry + query.getSqlEnd()),conn); //query.getTotalRows();
			}
			else{
				System.out.println("Count query from module:="+query.getCountQuery());
				totalrows = query.getSqlCount(query.getCountQuery(),conn); //query.getTotalRows();
			}
			//totalrows = query.getSqlCount(s1,conn); //query.getTotalRows();
			tRows = totalrows;
			totalRec = String.valueOf(totalrows);
			//System.out.println("Total Rec : " + totalRec);
			getSession().setAttribute(totalRecStr,totalRec); // total record



			//------------Processing Search (Specified)| Update Operation
			if(rec!=null){
				processRequest(rec,toRows,fRows,totalRec);
				}else{
					navigationnum=1;
					//first=Constants.NO;
					//previous=Constants.NO;
				}
				//----------End Processing Search (Specified) Operation

				//----Enable/Disable Navigation Button
				NavigationUtil.enableDisableNavButtoons(this,totalrows);
			if(navigationnum>totalrows){
					navigationnum = totalrows - rows + 1;
				}

			if(query!=null){
					//---Populating Data Table
					populateDataTable(sortingRule,qry);
				}


			//--- Enable/disable the Navigation bar based on the role.
			super.setPermissionInfoObj(permissionInfoObj);
			setNavigation();

			if(nvNumstr!=null){
				//---Setting Navigations Rows etc...
				setNavigationRows(totalrows);
			}

		}catch(Exception exp){
			exp.printStackTrace();
			return Constants.ACCESS_DENIED;
		}
		return Constants.ACCESS_GRANTED ;

	}




	/**
	 * ********************************************************************
	 *   BREAK DOWN METHODS
	 * ********************************************************************
	 */


	/**
	 * processRequest
	 * Processes the Required Button Pressed Function.
	 * @param rec
	 */
	private void processRequest(String rec, String toRows, String fRows, String totalRec) {


			if(rec.equals(Constants.GOTO)){
				//---Process Go To
				incGoto = NavigationUtil.processGoTo(this,form);
			}
			else if(rec.equals(Constants.FIRST)){
				//---Process First
				NavigationUtil.processFirst(this);
			}else if (rec.equals(Constants.NEXT)){
				//---Process Next
				NavigationUtil.processNext(this,totalRec);
			}
			else if (rec.equals(Constants.PREVIOUS)){
				//---Process Previous
				NavigationUtil.processPrevious(this);
			}
			else if (rec.equals(Constants.LAST)){
				//---Process Last
				NavigationUtil.processLast(this,totalRec);
			}
			else if(rec.equalsIgnoreCase(ComponentConstants.SORTING)){
				this.restorePreviousState();
			}

	}//end processRequest



	/**
	 * setNavigationRows
	 * This Methods set the Session Values of From and To Rows in Session on a  Grid View
	 */
	private void setNavigationRows(int totalrows) {

		int rowsToStoreInSession = 0;
		if( incGoto>0 ) {
			getSession().setAttribute(nvNumstr,String.valueOf(incGoto));
		}else{
			getSession().setAttribute(nvNumstr,String.valueOf(navigationnum));
		}
		if(rec==null){
			rec=Constants.FIRST;
		}
		// Setting the Destination [To] Rows Number
		// If Last Button Pressed
		if( rec.equals(Constants.LAST)){
			rowsToStoreInSession = totalrows;
		}
		else
		// If Next Button Pressed
		if(rec.equals(Constants.NEXT)){
			if(navigationnum+rows>totalrows){
				rowsToStoreInSession = totalrows;
			}else {
				rowsToStoreInSession = (navigationnum-1)+rows;
			}
		}
		else{
			// For All Other Cases [Go to Button]
			// For Positive Go to Number
			if( incGoto>0 ) {
				if((totalrows)<(incGoto+rows)-1 ){
					rowsToStoreInSession = totalrows;
				}
				else {
					rowsToStoreInSession = (incGoto-1)+rows;
				}

			}// end if
			//	For Negative Go to Number
			else{
				if((totalrows)<(navigationnum+rows)-1 ){
					rowsToStoreInSession = totalrows;
				}
				else {
					rowsToStoreInSession = (navigationnum-1)+rows;
				}
			}// end else
		}
		// Setting in Session
		getSession().setAttribute(screenName+"to",String.valueOf( rowsToStoreInSession));

	}//end setNavigationRows

	/**
	 * populateDataTable
	 * Populates Data Table to be shown on Grid View
	 * @param sortingRule
	 * @param qry
	 * @return
	 */
	private void populateDataTable(String sortingRule, String qry) throws Exception {

		//System.out.println(" rows "+ rows );
		//System.out.println(" navigationnum "+ navigationnum );


	if( sortingRule!=null ) {
		if(query.getSqlEnd().toUpperCase().indexOf("ORDER")!=-1) {
			sortingRule = " "+sortingRule+" ";
		}else{
			sortingRule = " ORDER BY "+sortingRule+" ";
		}
		//System.out.println(" query  : "+ query.getSqlStart()+ qry + query.getSqlEnd()+sortingRule );
		table = query.getData( (rows-1),(navigationnum),conn, query.getSqlStart()+ qry + query.getSqlEnd()+sortingRule,uniqueIdList,request);


	}else{
		//System.out.println(" query  : "+ query.getSqlStart()+ qry + query.getSqlEnd() );
		if( incGoto>0 ) {
			table = query.getData( (rows-1),(incGoto),conn, query.getSqlStart()+ qry + query.getSqlEnd(),uniqueIdList,request);
		}else{
			table = query.getData( (rows-1),(navigationnum),conn, query.getSqlStart()+ qry + query.getSqlEnd(),uniqueIdList,request);
		}
		}

		// Getting Active Components Of This Grid
		Hashtable activeComponents = query.getActiveComponents(getKeyColName(),(rows-1),(navigationnum), conn);
		request.setAttribute(ComponentConstants.ACTIVE_GRID_COMPONENTS, activeComponents);
		//request.setAttribute(ComponentConstants.HIDDEN_GRID_KEY_COLUMN, getKeyColName());

	}//end populateDataTable

	public void setSortingColumn(String sortName, String col, String srtOrder){
		if( sortName!=null )
			this.getSession().setAttribute(this.getScreenName()+ComponentConstants.SORTNAME,sortName);
		if( col!=null )
			this.getSession().setAttribute(this.getScreenName()+ComponentConstants.SORTCOLUMN,col);
		if(srtOrder!=null)
			this.getSession().setAttribute(this.getScreenName()+ComponentConstants.SORTORDER,srtOrder);
	}


	/**
	 * handleSort
	 * Handles the Sorting if specified by the User [Developer]
	 * @param form
	 */
	private String handleSort(BaseForm form, String sortingRule) {

		if(form.getSortcol()!=null ){
			if( !form.getSortcol().trim().equalsIgnoreCase("Y") ) {
				if(form.getSortname()!=null) {
						sortingRule = form.getSortcol();
						String sortOrder=null;
					if( form.getSortorder()!=null && !form.getSortorder().trim().equals("")){
						if(form.getSortorder().equals(ComponentConstants.ASC)) {
							sortOrder = ComponentConstants.DESC;
						}else if(form.getSortorder().equals(ComponentConstants.DESC)) {
							sortOrder = ComponentConstants.ASC;
						}
					}else{
						sortOrder = ComponentConstants.ASC;
					}
					sortingRule = sortingRule +" "+ sortOrder;
					if(getSession().getAttribute(this.screenName+ComponentConstants.SORTCOLUMN)!=null && !form.getSortcol().equalsIgnoreCase((String)getSession().getAttribute(this.screenName+ComponentConstants.SORTCOLUMN) )){
							if( getSession().getAttribute(screenName+ComponentConstants.SORTRULE) !=null )
							getSession().setAttribute( screenName+ComponentConstants.PREVIOUSSORTING,  getSession().getAttribute(screenName+ComponentConstants.SORTRULE) );
							getSession().setAttribute(this.screenName+ComponentConstants.PSORTCOLUMN,getSession().getAttribute(this.screenName+ComponentConstants.SORTCOLUMN));
							getSession().setAttribute(screenName+ComponentConstants.PSORTNAME,getSession().getAttribute(this.screenName+ComponentConstants.SORTNAME));
							getSession().setAttribute(screenName+ComponentConstants.PSORTORDER,getSession().getAttribute(this.screenName+ComponentConstants.SORTORDER));

					}
					getSession().setAttribute(this.screenName+ComponentConstants.SORTCOLUMN,form.getSortcol());
					getSession().setAttribute(screenName+ComponentConstants.SORTNAME,form.getSortname());
					getSession().setAttribute(screenName+ComponentConstants.SORTORDER,sortOrder);
					getSession().setAttribute(screenName+ComponentConstants.SORTRULE,sortingRule);

				}
			}

		}else{
			getSession().removeAttribute(screenName+ComponentConstants.SORTRULE);
			getSession().removeAttribute(this.screenName+ComponentConstants.SORTCOLUMN);
			getSession().removeAttribute(screenName+ComponentConstants.SORTNAME);
			getSession().removeAttribute(screenName+ComponentConstants.SORTORDER);
			getSession().removeAttribute(screenName+ComponentConstants.PREVIOUSSORTING);
			getSession().removeAttribute(this.screenName+ComponentConstants.PSORTCOLUMN);
			getSession().removeAttribute(screenName+ComponentConstants.PSORTNAME);
			getSession().removeAttribute(screenName+ComponentConstants.PSORTORDER);
			}

	return sortingRule;
	}//end handleSort
}
