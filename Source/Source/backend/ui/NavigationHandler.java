/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package com.i2c.component.backend.ui;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.i2c.cards.security.PermissionInfoObj;
import com.i2c.cards.util.CardsConstants;
import com.i2c.cards.util.Constants;
import com.i2c.component.base.BaseForm;
import com.i2c.component.base.BaseHome;
import com.i2c.component.util.ComponentConstants;
import com.i2c.component.util.ComponentsUtil;
import com.i2c.util.CommonUtilities;

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

public class NavigationHandler extends NavigationBase {

	/**
	 *
	 */
	boolean searchPerformed = false;
	public NavigationHandler() {
		super();
		// TODO Auto-generated constructor stub
	}
	public NavigationHandler(HttpServletRequest request) {
		super();
		setSession(request.getSession(false));
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.i2c.component.backend.ui.Navigation#executeAction(com.i2c.component.base.BaseForm, java.sql.Connection, com.i2c.component.base.BaseHome, javax.servlet.http.HttpServletRequest)
	 */
	public int executeAction(
		BaseForm form,
		Connection conn,
		BaseHome query,
		HttpServletRequest request) {
		this.query = query;
		this.request = request;
		this.form = form;
		this.conn = conn;
		HttpSession ses = request.getSession(false);
		boolean err = false;
		boolean recordDeleted = false;

		//boolean searchPerformed = false;

		//setting the previous action in session for security module
		this.setConstants();
		if(null != request.getParameter("PREVIOUS_ACTION_VAL") && request.getParameter("PREVIOUS_ACTION_VAL").toString().equalsIgnoreCase(Constants.SEARCH))
		{
			ses.setAttribute(Constants.PREVIOUS_ACTION_VAL, "ser");
			ses.setAttribute(cmtActionStr,Constants.SEARCH);
			searchPerformed = true;

		}
		else
		{



		this.setLastActionInSession(
									request.getSession(false),
									form
									);
		}


//		if(null!= ses.getAttribute(Constants.PREVIOUS_ACTION_VAL) && ses.getAttribute(Constants.PREVIOUS_ACTION_VAL).toString().equalsIgnoreCase(Constants.SEARCH))
//				{
//					ses.setAttribute(
//										screenName + Constants.CMTACTION,
//										Constants.SEARCH);
//
//
//				}
//
//
//		this.setLastActionInSession(request.getSession(false), form);

		if(ses.getAttribute(CardsConstants.TASK_PERMISSION_INFO_OBJ)!=null)
			super.setPermissionInfoObj((PermissionInfoObj) ses.getAttribute(CardsConstants.TASK_PERMISSION_INFO_OBJ)); //Danish Lodhi 15-05-05

		/*if (query != null){
			if (columnMappingTable.size() > 0)
				query.setMappingTable(this.columnMappingTable);
			if (formatMappingTable.size() > 0)
				query.setFormatMappingTable(formatMappingTable);
			query.setNVHandler(this);
		}*/

		if (rows <= 0)
			rows = 1;
		int totalrows = 0;
		try {
			String rec = form.getRec();

			//setting Constants [Names of Session Variables]
			this.setConstants();
			setSession(request.getSession(false));
			if(null!=ses.getAttribute(cmtActionStr) && searchPerformed){

				//session.removeAttribute(cmtActionStr);
				ses.setAttribute(cmtActionStr,Constants.SEARCH);
				ses.setAttribute(this.screenName + ComponentConstants.PRVIOUS_ACTION,Constants.SEARCH);

			}
			//setting the tab properties in session
			this.setTabOption(ses,form);

			//setting the Navigation Number in session
			this.setNavigationNumber(ses);
			//Calling Appropriate Method
			//System.out.println("Previous Action:="+ses.getAttribute(this.screenName + ComponentConstants.PRVIOUS_ACTION));
			//for lock button
			/*System.out.println("Is Locked:="+this.isLocked());
			System.out.println("session :="+ses.getAttribute(this.screenName+ComponentConstants.UNLOCK));*/
			/*if(this.isLocked()){
				if(!CommonUtilities.isNullOrEmptyString((String)ses.getAttribute(this.screenName+ComponentConstants.UNLOCK))){
					if(ses.getAttribute(this.screenName+ComponentConstants.UNLOCK).equals(ComponentConstants.NO)){
						if(ComponentsUtil.isValidUser(request,conn)){
							session.setAttribute(this.screenName+ComponentConstants.UNLOCK,ComponentConstants.YES);
						}
					}
				}else{
					if(ComponentsUtil.isValidUser(request,conn)){
						session.setAttribute(this.screenName+ComponentConstants.UNLOCK,ComponentConstants.YES);
					}
				}
			}*/


//			Double Password security
			System.out.println("LOCKING-->navigationhandler:::=rec="+rec);
			if(rec.equals("lock")){
				session.setAttribute(this.screenName+"unlock","N");
				rec = "";
			}
			System.out.println("LOCKING-->navigationhandler:::=isLocked="+this.isLocked());
			if(this.isLocked()){
				ses.setAttribute(this.screenName+"preserveWhereClause","Y");
			}else{
				if(ses.getAttribute(this.screenName+"preserveWhereClause")!=null){
					ses.removeAttribute(this.screenName+"preserveWhereClause");
				}
			}
			System.out.println("LOCKING-->navigationhandler:::=preserverWhereClause="+ses.getAttribute(this.screenName+"preserveWhereClause"));
			System.out.println("LOCKING-->navigationhandler:::=applyDoublePassword="+ComponentsUtil.applyDoublePassword(conn));
			if(ComponentsUtil.applyDoublePassword(conn)){
				System.out.println("LOCKING-->navigationhandler:::=attribute set from unlock servlet="+ses.getAttribute(this.screenName+ComponentConstants.UNLOCK));
				if(!CommonUtilities.isNullOrEmptyString((String)ses.getAttribute(this.screenName+ComponentConstants.UNLOCK))){
					System.out.println("LOCKING-->navigationhandler:::=attribute set from unlock servlet="+ses.getAttribute(this.screenName+ComponentConstants.UNLOCK));
					if(this.isLocked() && ses.getAttribute(this.screenName+ComponentConstants.UNLOCK).equals(ComponentConstants.NO)){
						System.out.println("LOCKING-->navigationhandler:::=in setting unlock button true=");
						this.disableAllDVFields();
						navBar.setUnlock(true);
						navBar.setInsert(false);
						navBar.setDelete(false);
						navBar.setCopy(false);
						navBar.setUpdate(false);
					}else{
						/*if(ses.getAttribute("preserveWhereClause")!=null){
							ses.removeAttribute("preserveWhereClause");
						}*/
						System.out.println("LOCKING-->navigationhandler:::=in setting unlock button false=");
						request.setAttribute("isUnlock","YES");
						navBar.setLock(true);
						navBar.setUnlock(false);
					}
				}else{
					System.out.println("LOCKING-->navigationhandler:::=isLocked 2="+this.isLocked());
					if(this.isLocked()){
						System.out.println("LOCKING-->navigationhandler:::=in setting unlock button true 2=");
						navBar.setUnlock(true);
						navBar.setInsert(false);
						navBar.setDelete(false);
						navBar.setCopy(false);
						navBar.setUpdate(false);
						this.disableAllDVFields();
						ses.setAttribute(this.screenName+"preserveWhereClause","Y");
					}else{
						System.out.println("LOCKING-->navigationhandler:::=in setting unlock button false 2=");
						request.setAttribute("isUnlock","YES");
						navBar.setLock(true);
						navBar.setUnlock(false);
					}
				}
			}else{
				System.out.println("LOCKING-->navigationhandler:::=in else double password not implement=");
				request.setAttribute("ShowUnlock","NO");
				navBar.setUnlock(false);
				navBar.setLock(false);
			}
			System.out.println("LOCKING-->navigationhandler:::=isUnlock : request="+request.getAttribute("isUnlock"));
			System.out.println("LOCKING-->navigationhandler:::=ShowUnlock : request="+request.getAttribute("ShowUnlock"));
			//end Double Password Security
			//End for locl button
			if(rec.equals(Constants.COMMIT)){
				if(((String)ses.getAttribute(cmtActionStr)).equals(Constants.INSERT)){
					if(!this.insertNewRecord(form,conn,query,request))
						return Constants.ACCESS_DENIED;
				}else{
					if(((String)ses.getAttribute(cmtActionStr)).equals(Constants.SEARCH)){
						if(ses.getAttribute(this.screenName+"preserveWhereClause")!=null){
							ses.removeAttribute(this.screenName+"preserveWhereClause");
						}
						if(ses.getAttribute(this.screenName+"preserveWhereClause")==null
								&& ses.getAttribute(this.screenName+"lockWhereClause") != null){
							ses.removeAttribute(this.screenName+"lockWhereClause");
							if(ses.getAttribute(this.screenName+"locknavigationnum") != null){
								ses.removeAttribute(this.screenName+"locknavigationnum");
							}
						}
						if(!this.findRecords(form,conn,query,request))
							return Constants.ACCESS_DENIED;
					}
				}
			}else if (rec.equals(Constants.DELETE)){
				if(!this.deleteRecord(form,conn,query,request))
					return Constants.ACCESS_DENIED;
			}else if (rec.equals(Constants.UPDATE)){
				if(!this.updateRecord(form,conn,query,request))
					return Constants.ACCESS_DENIED;
			}else if (rec.equals(Constants.GOTO) || rec.equals(Constants.NEXT)||
				rec.equals(Constants.FIRST) || rec.equals(Constants.PREVIOUS) ||
				rec.equals(Constants.LAST)){
					if(!this.getRecord(form,conn,query,request))
						return Constants.ACCESS_DENIED;
			}else if(rec.equals(Constants.SEARCH) || rec.equals("")){
				if(!this.findRecords(form,conn,query,request))
					return Constants.ACCESS_DENIED;
			}else
				return Constants.ACCESS_DENIED;

			//Double Password security
			if(ses.getAttribute(this.screenName+"preserveWhereClause")==null
					&& ses.getAttribute(this.screenName+"lockWhereClause") != null){
				ses.removeAttribute(this.screenName+"lockWhereClause");
				if(ses.getAttribute(this.screenName+"locknavigationnum") != null){
					ses.removeAttribute(this.screenName+"locknavigationnum");
				}
			}

//			if(ComponentsUtil.applyDoublePassword(conn)){
//				if(!CommonUtilities.isNullOrEmptyString((String)ses.getAttribute(this.screenName+ComponentConstants.UNLOCK))){
//					if(this.isLocked() && ses.getAttribute(this.screenName+ComponentConstants.UNLOCK).equals(ComponentConstants.NO)){
//						this.disableAllDVFields();
//						navBar.setUnlock(true);
//						navBar.setInsert(false);
//						navBar.setDelete(false);
//						navBar.setCopy(false);
//						navBar.setUpdate(false);
//					}else{
//						navBar.setUnlock(false);
//					}
//				}else{
//					if(this.isLocked()){
//						navBar.setUnlock(true);
//						navBar.setInsert(false);
//						navBar.setDelete(false);
//						navBar.setCopy(false);
//						navBar.setUpdate(false);
//						this.disableAllDVFields();
//					}else{
//						navBar.setUnlock(false);
//					}
//				}
//			}else{
//				navBar.setUnlock(false);
//			}


		} catch (Exception exp) {
			exp.printStackTrace();
			return Constants.ACCESS_DENIED;
		}
		return Constants.ACCESS_GRANTED;
	}
	/**
	 * @param obj
	 * @return
	 */
	private BaseForm reSetFieldFormate(Object obj) {
		Vector v = this.getINSFormatKeys();

		// Need to invoke the setter method.

		Method methods[] = obj.getClass().getDeclaredMethods();
		if (v.size() > 0) {
			List methods_Value_lst = new ArrayList();
			String nvPair[] = null;

			for (int i = 0; i < methods.length; i++) {
				for (int k = 0; k < v.size(); k++) {
					if (methods[i]
						.getName()
						.toUpperCase()
						.equalsIgnoreCase("get" + (String) v.get(k))) {
						nvPair = new String[2];
						nvPair[0] = (String) v.get(k);
						try {
							String str = (String) methods[i].invoke(obj, null);
							String formatedStr = null;
							if (str != null) {
								if (this.insFormatTable.get(v.get(k))
									!= null) {
									if (((String) this
										.insFormatTable
										.get(v.get(k)))
										.equalsIgnoreCase(
											Constants.DATE_FIELD)) {
										formatedStr =
											Constants.getFormatedDBDateValue(
												str);
									} else if (
										(
											(String) this.insFormatTable.get(
												v.get(k))).equalsIgnoreCase(
											Constants.TIME_FIELD)) {
										formatedStr =
											Constants.getFormatedDBTimeValue(
												str);
									} else if (
										(
											(String) this.insFormatTable.get(
												v.get(k))).equalsIgnoreCase(
											Constants.DATETIME_FIELD)) {
										formatedStr =
											Constants
												.getFormatedDBDateTimeValue(
												str);
									} else if (
										(
											(String) this.insFormatTable.get(
												v.get(k))).equalsIgnoreCase(
											Constants.DECIMAL_FIELD)) {
										formatedStr =
											Constants
												.getFormatedDBDecimalValue(
												str);
									}

									//----
									nvPair[1] = formatedStr;

								}
							}
						} catch (Exception exp) {
							exp.printStackTrace();
						}
						methods_Value_lst.add(nvPair);
					}
				}

			}

			// do set values by invoking setter methods.
			if (methods_Value_lst != null) {

				if (methods_Value_lst.size() > 0) {
					//-----------

					for (int i = 0; i < methods_Value_lst.size(); i++) {
						nvPair = (String[]) methods_Value_lst.get(i);

						for (int k = 0; k < methods.length; k++) {
							if (methods[k]
								.getName()
								.toUpperCase()
								.equalsIgnoreCase("set" + nvPair[0])) {
								try {
									methods[k].invoke(
										obj,
										new Object[] { nvPair[1] });
								} catch (Exception exp) {
									exp.printStackTrace();
								}

							}
						}
					}
				}
			}

			//		try{
			//
			//		methods[2].invoke(obj,new Object[]{"###,###.###", new Double("123456.789"), new Locale("en", "US")});
			//		methods[3].invoke(xyz,new Object[]{"HELLO WORLD"});
			//		}catch(Exception exp){
			//			exp.printStackTrace();
			//		}
			//System.out.println("Methods : ");

		}
		return ((BaseForm) obj);
	}
	/**
	 * @param value
	 * @return
	 */
	private int updateCnclC(int value){
		if(value==0){
			return 1;
		}
		else{

			return (value)+1;
		}
	}

	/**
	 * @param err
	 */
	private void setStatesIfInsertionFails(boolean err)
	{
		err = true;
		//	query.addErrorKey("component.error.record.insert.unsuccessful");
		getSession().setAttribute(
			screenName + Constants.ERRORVALUE,
			Constants.YES);
		getSession().setAttribute(
			screenName + Constants.ININFO,
			ComponentsUtil.getRequestParametersTable(
				request));
		//this.table = ComponentsUtil.getRequestParametersTable(request);
		int cc=this.updateCnclC(form.getCancleCounter());

		session.setAttribute(this.screenName + Constants.CANCELCOUNTER,String.valueOf(cc));

		session.setAttribute(
			this.screenName + Constants.CMTACTION,
			Constants.INSERT);
		navBar.setNavigationState("1100000000000");
//		cancel = Constants.YES;
//		search = Constants.NO;
//		commit = Constants.YES;
//		insert = Constants.NO;
//
//		copy =
//			delete =
//				update =
//					first =
//						next =
//							previous =
//								last =
//									gto =
//										browse =
//											Constants.NO;
		getSession().removeAttribute(nvNumstr);
		getSession().removeAttribute(this.totalRecStr);
		setErrNavigation();
	}

	/**
	 * @param form
	 * @param conn
	 * @param query
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public boolean insertNewRecord(
		BaseForm form,
		Connection conn,
		BaseHome query,
		HttpServletRequest request)
		throws Exception{

		HttpSession ses = request.getSession(false);
		String rec = form.getRec();
		boolean isOk = false, err = false, inserting = false;
		try{
			String qry = " ";
			String strWhere2 = null;

			if (rec != null) {
				//getting Unique Id
				this.idTable = (Hashtable) ses.getAttribute(screenName + ComponentConstants.UNIQUEIDLIST);

				String id = super.getUniqueIds();
				String rec1 = (String) ses.getAttribute(cmtActionStr);
				if (rec1 != null) {
					rec = rec1;
					this.setPreviousAction(screenName, rec);
					ses.removeAttribute(cmtActionStr);
				}
				if (id != null) {
					//inserting Record
					if (rec.equals(Constants.INSERT)) {
						ses.removeAttribute(whereClauseStr);
						try{
							if(ses.getAttribute(Constants.VALIDATION_WARNINGS_EXIST)==null){
								if (query.insert(form, conn) <= 0){
									err = true;
								}else{
									session.removeAttribute(this.screenName + Constants.CANCELCOUNTER);
								}
							}else{
								err = true;
							}
						}catch (Exception exp){
							err = true;
						}
						if (!err) {
							String whereClause = form.getWhereClause();
							if (null != whereClause && (whereClause.trim().length()) > 0) {
								ses.setAttribute(whereClauseStr,whereClause);
							}
							rec = Constants.FIRST;
						}else{
							ses.setAttribute(screenName + Constants.ERRORVALUE,	Constants.YES);
							ses.setAttribute(screenName + Constants.ININFO,
										ComponentsUtil.getRequestParametersTable(request));
							int cc=this.updateCnclC(form.getCancleCounter());
							session.setAttribute(this.screenName + Constants.CANCELCOUNTER,String.valueOf(cc));
							session.setAttribute(this.screenName + Constants.CMTACTION,Constants.INSERT);
							navBar.setNavigationState("1100000000000");
//							cancel = Constants.YES;
//							search = insert = Constants.NO;
//							commit = Constants.YES;
//							copy = delete =	update = first = next = previous = last = gto = browse = Constants.NO;
							ses.removeAttribute(nvNumstr);
							ses.removeAttribute(this.totalRecStr);
							setErrNavigation();
						}
					}

					//-- Updated by ILYAS [06/12/2005]
					qry = this.getWhereClause(ses,form);
					rec = form.getRec();
					//----------------------------------

					rec = form.getRec();
					if (qry == null) {
						qry = " ";
					}
					if (!err) {
						//setting total Records [count]
						this.setTotalRecords(ses,qry,rec);
						if (query != null) {
							//getting data
							table =	query.getData(
									rows,navigationnum,	conn,
									query.getSqlStart() + qry + query.getSqlEnd(),
									uniqueIdList,request,form);
						}
						this.checkingRecords(this.tRows,searchPerformed,rec);
						//Setting Browse Option
//						if (this.tRows > 0) {
//							browse = Constants.YES;
//						} else {
//							browse = Constants.NO;
//						}
						setNavigation();
						ses.removeAttribute(screenName + "to");
					}else {
						table =	query.getData(
								rows,navigationnum,conn,
								query.getSqlStart() + qry + query.getSqlEnd(),
								uniqueIdList,request,form);
					}
				}
			}
			isOk = true;
		}catch(Exception e){
			System.out.println("Exception in Insert New Record:");
			e.printStackTrace();
		}
		return isOk;
	}//End Method

	/**
	 * @param form
	 * @param conn
	 * @param query
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public boolean findRecords(
		BaseForm form,
		Connection conn,
		BaseHome query,
		HttpServletRequest request)
		throws Exception{

		HttpSession ses = request.getSession(false);
		String rec = form.getRec();
		boolean isFind = false, err = false, inserting = false;
		try{
			int totalrows = 0;
			String qry = " ";
			String strWhere2 = null;

			if (rec != null) {
				//getting the unique ids
				this.idTable =(Hashtable) ses.getAttribute(
									screenName + ComponentConstants.UNIQUEIDLIST);
				String id = super.getUniqueIds();

				//Checking if any navigation bar buttons were pressed.
				String rec1 =
					(String) ses.getAttribute(cmtActionStr);
				if (rec1 != null) {
					rec = rec1;
					this.setPreviousAction(screenName, rec);
					ses.removeAttribute(cmtActionStr);
				}
				if (!err) {
					if (rec.equals(Constants.SEARCH)|| rec.equals(Constants.COMMIT)) {
						String whereClause = null;
						ses.removeAttribute(whereClauseStr);
						whereClause = form.getWhereClause();
						if (whereClause != null	&& (whereClause.trim().length()) > 0)
							ses.setAttribute(whereClauseStr,whereClause);
						rec = Constants.FIRST;
					}
				}
			}
			//-- Updated by ILYAS [06/12/2005]
			qry = this.getWhereClause(ses,form);
			rec = form.getRec();
			//----------------------------------
			//--- for unlock
			if(!ComponentsUtil.isNullOrEmptyString((String)ses.getAttribute(this.screenName+"preserveWhereClause"))
					&&((String)ses.getAttribute(this.screenName+"preserveWhereClause")).equals("Y")){
				ses.setAttribute(this.screenName+"lockWhereClause",qry);
				ses.setAttribute(this.screenName+"locknavigationnum",navigationnum+"");
			}
			if(!ComponentsUtil.isNullOrEmptyString((String)ses.getAttribute(this.screenName+"lockWhereClause"))
					&& form.getWhereClause() != null){
				qry = (String)ses.getAttribute(this.screenName+"lockWhereClause");
				ses.setAttribute(whereClauseStr,qry);
				navigationnum = Integer.parseInt((String)ses.getAttribute(this.screenName+"locknavigationnum"));
			}
			//End code for unlock where
			if (qry == null)
				qry = " ";
			if (!err) {
				//setting total Records [count]
				this.setTotalRecords(ses,qry,rec);
				if (query != null) {
					table =query.getData(
							rows,navigationnum,conn,
							query.getSqlStart() + qry + query.getSqlEnd(),
							uniqueIdList,request,form);
				}
				//Setting Browse Option
				this.checkingRecords(this.tRows,searchPerformed,rec);
//				if (this.tRows > 0) {
//					browse = Constants.YES;
//				} else {
//					browse = Constants.NO;
//				}
				setNavigation();
				ses.removeAttribute(screenName + "to");
			}else {
				table = query.getData(
						rows,navigationnum,conn,
						query.getSqlStart() + qry + query.getSqlEnd(),
						uniqueIdList,request,form);
			}
			isFind = true;
		}catch(Exception e){
			System.out.println("Exception in finding record:="+e.getMessage());
			e.printStackTrace();
		}
		return isFind;
	}//End Method

	/**
	 * @param form
	 * @param conn
	 * @param query
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public boolean getRecord(
		BaseForm form,
		Connection conn,
		BaseHome query,
		HttpServletRequest request)
		throws Exception{

		HttpSession ses = request.getSession(false);
		String rec = form.getRec();
		boolean isRecordGet = false, err = false, inserting = false;
		try{
			int totalrows = 0;
			String qry = " ";
			String strWhere2 = null;

			if (rec != null) {
				this.idTable = (Hashtable) ses.getAttribute(
									screenName + ComponentConstants.UNIQUEIDLIST);
				String id = super.getUniqueIds();

				//-- Updated by ILYAS [06/12/2005]
				qry = this.getWhereClause(ses,form);
				rec = form.getRec();
				//----------------------------------

				if (qry == null) {
					qry = " ";
				}
				if (!err) {
					//setting total Records [count]
					this.setTotalRecords(ses,qry,rec);
					if (query != null) {
						table =	query.getData(
								rows,navigationnum,conn,
								query.getSqlStart() + qry + query.getSqlEnd(),
								uniqueIdList,request,form);
					}
					//Setting Browse Option
					this.checkingRecords(this.tRows,searchPerformed,rec);
//					if (this.tRows > 0) {
//						browse = Constants.YES;
//					} else {
//						browse = Constants.NO;
//					}
					setNavigation();
					ses.removeAttribute(screenName + "to");
				}else {
					table = query.getData(
							rows, navigationnum, conn,
							query.getSqlStart() + qry + query.getSqlEnd(),
							uniqueIdList,request,form);
				}
			}
//			--- for unlock
			if(!ComponentsUtil.isNullOrEmptyString((String)ses.getAttribute(this.screenName+"preserveWhereClause"))&&((String)ses.getAttribute(this.screenName+"preserveWhereClause")).equals("Y")){
				ses.setAttribute(this.screenName+"lockWhereClause",qry);
				ses.setAttribute(this.screenName+"locknavigationnum",navigationnum+"");
			}
			if(!ComponentsUtil.isNullOrEmptyString((String)ses.getAttribute(this.screenName+"lockWhereClause"))){
				qry = (String)ses.getAttribute(this.screenName+"lockWhereClause");
				ses.setAttribute(whereClauseStr,qry);
				navigationnum = Integer.parseInt((String)ses.getAttribute(this.screenName+"locknavigationnum"));
			}
			//End Unlock
			isRecordGet = true;
		}catch(Exception e){
			System.out.println("Exception in getting record:="+e.getMessage());
			e.printStackTrace();
		}
		return isRecordGet;

	}//End Method

	/**
	 * @param form
	 * @param conn
	 * @param query
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public boolean deleteRecord(
		BaseForm form,
		Connection conn,
		BaseHome query,
		HttpServletRequest request)
		throws Exception{

		boolean isDelete = false;
		int totalrows = 0;
		boolean err = false;
		boolean inserting = false;
		HttpSession ses = request.getSession(false);
		String rec = form.getRec();
		String qry = " ";
		try{
			if (rec != null) {
				this.idTable = (Hashtable) ses.getAttribute(
									screenName + ComponentConstants.UNIQUEIDLIST);
				String id = super.getUniqueIds();
				if(null != id){
					if (rec.equals(Constants.DELETE)){
						try {
							if (query.delete(id, conn) <= 0) {
								System.out.println("[ ERROR IN DELETE " + screenName + " ]");
								this.restorePreviousNVState((Hashtable) ses.getAttribute(screenName
																					+ ComponentConstants.NVSTATE));
							} else {
								if (navigationnum == totalrows) {
									navigationnum = navigationnum - 1;
								} else {
									if (navigationnum != 1){
										navigationnum = navigationnum - 1;
										if(navigationnum == 1)
											rec = Constants.FIRST;
									}
									else if (navigationnum <= 0	&& totalrows > navigationnum) {
										navigationnum = 1;
									} else if (navigationnum == 1){
										rec = Constants.FIRST;
									}
								}
							}
						} catch (Exception exp) {
							System.out.println("[ EXCEPTION IN DELETE " + screenName + " ]");
							this.restorePreviousNVState(
								(Hashtable) ses.getAttribute(
									screenName + ComponentConstants.NVSTATE));
						}
					}
				}

				//-- Updated by ILYAS [06/12/2005]
				qry = this.getWhereClause(ses,form);
				rec = form.getRec();
				//----------------------------------

				if (qry == null) {
					qry = " ";
				}
				if (!err) {
					//setting total Records [count]
					this.setTotalRecords(ses,qry,rec);
					if (query != null) {
						table =	query.getData(
								rows, navigationnum, conn,
								query.getSqlStart() + qry + query.getSqlEnd(),
								uniqueIdList,request,form);
					}
					System.out.println("Total rows:="+tRows);
					System.out.println("Navigation number:="+navigationnum);
					if(tRows == navigationnum){
						if (rows > 1) {
							navigationnum = (navigationnum) - rows;
						}
						//next = Constants.NO;
						//last = Constants.NO;
						navBar.setNext(false);
						navBar.setLast(false);
					}
					//Setting Browse Option
					if (this.tRows > 0) {
						//browse = Constants.YES;
						navBar.setBrowse(true);
					} else {
						//browse = Constants.NO;
						navBar.setBrowse(false);
					}
					setNavigation();
					ses.removeAttribute(screenName + "to");
				}else {
					table =	query.getData(
							rows, navigationnum, conn,
							query.getSqlStart() + qry + query.getSqlEnd(),
							uniqueIdList,request,form);
				}
			}
		isDelete = true;
		}catch(Exception e){
			System.out.println("Exception in deleting record:="+e.getMessage());
			e.printStackTrace();
		}
		return isDelete;
	}//End Method


	/**
	 * @param form
	 * @param conn
	 * @param query
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public boolean updateRecord(
		BaseForm form,
		Connection conn,
		BaseHome query,
		HttpServletRequest request)
		throws Exception{

		boolean isUpdate = false;
		int totalrows = 0;
		boolean err = false;
		boolean inserting = false;
		HttpSession ses = request.getSession(false);
		String rec = form.getRec();
		String qry = " ";
		try{
			String strWhere2 = null;
			if (rec != null) {
				this.idTable = (Hashtable) ses.getAttribute(
									screenName + ComponentConstants.UNIQUEIDLIST);
				// unqueId
				String id = super.getUniqueIds();
				if (id != null) {
					if (rec.equals(Constants.UPDATE)) {
						query =	query.getInstance((java.util.Hashtable) ses.getAttribute(cRowStr));
						try{
							if(ses.getAttribute(Constants.VALIDATION_WARNINGS_EXIST)==null){
								if (query.update(id, form, conn) <= 0){
									err = true;
								}else{
									if(ses.getAttribute(screenName+ComponentConstants.PREV_VALID_STATE_HASHTABLE)==null){
										this.restorePreviousNVState((Hashtable) ses.getAttribute(screenName+ComponentConstants.NVSTATE));
									}else{
										Hashtable previousValidStateHashtable = (Hashtable) ses.getAttribute(screenName+ComponentConstants.PREV_VALID_STATE_HASHTABLE);
										this.restorePreviousNVState(previousValidStateHashtable);
										ses.removeAttribute(screenName+ComponentConstants.PREV_VALID_STATE_HASHTABLE);
									}
									session.removeAttribute(this.screenName + Constants.CANCELCOUNTER);
								}
							}else{
								if(ses.getAttribute(screenName+ComponentConstants.PREV_VALID_STATE_HASHTABLE)==null){
									Hashtable previousValidStateHashtable = (Hashtable) ses.getAttribute(screenName+ComponentConstants.NVSTATE);
									ses.setAttribute(screenName+ComponentConstants.PREV_VALID_STATE_HASHTABLE,previousValidStateHashtable);
								}
								err = true;
							}
							if(err){
								ses.setAttribute(screenName + Constants.ERRORVALUE,Constants.YES);
								ses.setAttribute(screenName + Constants.ININFO,
									ComponentsUtil.getRequestParametersTable(request));



								session.setAttribute(this.screenName + Constants.CMTACTION,Constants.UPDATE);
								session.setAttribute(this.screenName + Constants.UPDATEERR,Constants.YES);
								session.setAttribute(this.screenName + Constants.UPDATEACTIONNAME,form.getRequrl());
								session.setAttribute(this.screenName + Constants.UPDATEFORMNAME, form.getFormname());
								session.setAttribute(this.screenName + Constants.UPDATEMODE, form.getRec());

								int cc=this.updateCnclC(form.getCancleCounter());

								session.setAttribute(this.screenName + Constants.CANCELCOUNTER,String.valueOf(cc));
								session.setAttribute(this.screenName + Constants.CURRENT_ACTION,Constants.UPDATE);

								navBar.setNavigationState("1100000000000");

//								cancel = Constants.YES;
//								search = insert = Constants.NO;
//								commit = Constants.YES;
//								copy = delete = update = first = next = previous = last = gto = browse = Constants.NO;
								setErrNavigation();
							}
						}
						catch (Exception exp){
							err = true;
						}
					}
				}

				//-- Updated by ILYAS [06/12/2005]
				qry = this.getWhereClause(ses,form);
				rec = form.getRec();
				//----------------------------------

				if (qry == null)
					qry = " ";
				if (!err) {
					//setting total Records [count]
					this.setTotalRecords(ses,qry,rec);
					if (query != null) {
						table = query.getData(
								rows, navigationnum, conn,
								query.getSqlStart() + qry + query.getSqlEnd(),
								uniqueIdList,request,form);
					}
					//Setting Browse Option
					this.checkingRecords(this.tRows,searchPerformed,rec);
//					if (this.tRows > 0)
//						browse = Constants.YES;
//					else
//						browse = Constants.NO;
					setNavigation();
					ses.removeAttribute(screenName + "to");
				}else {
					table =	query.getData(
							rows, navigationnum, conn,
							query.getSqlStart() + qry + query.getSqlEnd(),
							uniqueIdList,request,form);
				}
			}
			isUpdate = true;
		}catch(Exception e){
			System.out.println("Exception in Updating Record:= "+e.getMessage());
			e.printStackTrace();
		}
		return isUpdate;
	}//End Method

	/**
	 * @param ses
	 * @param form
	 * @param totalRec
	 * @param totalrows
	 */
	public void goToOption(
		HttpSession ses,
		BaseForm form,
		String totalRec,
		int totalrows){

			int intgoto = this.getRecGoto(form.getRecgoto());
			if (form.getRecgoto() != null && !form.getRecgoto().trim().equals("")
				&& !(form.getRecgoto().startsWith("+") || form.getRecgoto().startsWith("-"))) {
				if (intgoto > totalrows) {
					// Can't exceed the maximum limit of [totalrows].
					// OR The value you entered  navigationnum+intgoto is greater than the maximum limit.
					ses.setAttribute(this.screenName + Constants.ACTIVE_MSG,
						Constants.MAX_LIMIT_MSG+ "[" + totalrows + "]");
				} else if (intgoto == totalrows) {
					navigationnum = intgoto;
				} else if (intgoto > 0) {
					navigationnum = intgoto;
				}
			} else {
				if (intgoto > 0) {
					if (navigationnum + intgoto > totalrows) {
						// Can't exceed the maximum limit of [totalrows].
						// OR The value you entered  navigationnum+intgoto is greater than the maximum limit.
						ses.setAttribute(this.screenName+ Constants.ACTIVE_MSG,
							Constants.MAX_LIMIT_MSG	+ "[" + totalrows + "]");
					} else {
						navigationnum = navigationnum + intgoto;
					}
				} else if (intgoto < 0) {
					if (navigationnum + intgoto <= 0) {
						ses.setAttribute(this.screenName + Constants.ACTIVE_MSG, Constants.MIN_LIMIT_MSG);
					} else {
						navigationnum = navigationnum + intgoto;
					}
				}
			}
			if (navigationnum == 1) {
				navBar.setNavigationState("2222222002222");
//				first = Constants.NO;
//				previous = Constants.NO;
			//} else if (totalrows <= (navigationnum + rows)) {
			} else if (totalrows <= (navigationnum)) {
				navBar.setNavigationState("2222222110022");
//				first = Constants.YES;
//				next = Constants.NO;
//				previous = Constants.YES;
//				last = Constants.NO;
			}


			// Added by ILYAS [06/12/2005]
			String tabname = this.request.getParameter("tabname");
			if(tabname != null){
				this.getSession().setAttribute(tabname+"_NavNumber",this.navigationnum+"");
			}


	}//End Method

	/**
	 * @param ses
	 * @param form
	 * @param totalRec
	 * @param totalrows
	 */
//	public void firstOption_old(
//		HttpSession ses,
//		BaseForm form,
//		String totalRec,
//		int totalrows){
//
//		navigationnum = 1;
//		first = Constants.NO;
//		previous = Constants.NO;
//
//	}//End Method

	/**
	 * @param ses
	 * @param form
	 * @param totalRec
	 * @param totalrows
	 */
//	public void nextOption_old(
//		HttpSession ses,
//		BaseForm form,
//		String totalRec,
//		int totalrows){
//
//		navigationnum =
//			((navigationnum + rows)
//				< Integer.parseInt(totalRec))
//				? (navigationnum + rows)
//				: (Integer.parseInt(totalRec));
//		if (navigationnum
//			>= (Integer.parseInt(totalRec))) {
//			next = Constants.NO;
//			last = Constants.NO;
//		}
//
//	}//End Method

	/**
	 * @param ses
	 * @param form
	 * @param totalRec
	 * @param totalrows
	 */
//	public void previousOption_old(
//		HttpSession ses,
//		BaseForm form,
//		String totalRec,
//		int totalrows){
//
//		navigationnum =
//			((navigationnum - rows) > 1)
//				? (navigationnum - rows)
//				: 1;
//		if (navigationnum <= 1) {
//			first = Constants.NO;
//			previous = Constants.NO;
//		}
//
//
//	}//End Method

	/**
	 * @param ses
	 * @param form
	 * @param totalRec
	 * @param totalrows
	 */
//	public void lastOption_old(
//		HttpSession ses,
//		BaseForm form,
//		String totalRec,
//		int totalrows){
//
//		navigationnum = Integer.parseInt(totalRec);
//		if (rows > 1) {
//			navigationnum = (navigationnum) - rows;
//		}
//		next = Constants.NO;
//		last = Constants.NO;
//
//	}//End Method


	/**
	 * @param ses
	 * @param rec
	 * @param form
	 * @param totalRec
	 * @param totalrows
	 */
	public void setNavigationOption(
		HttpSession ses,
		String rec,
		BaseForm form,
		String totalRec,
		int totalrows){

		if (rec != null) {
			if (rec.equals(Constants.GOTO)) {
				this.goToOption(ses,form,totalRec,totalrows);
				//NavigationUtil.processGoTo(this,form);
			} else if (rec.equals(Constants.FIRST)) {
				//this.firstOption(ses,form,totalRec,totalrows);  ----checking
				NavigationUtil.processFirst(this);
			} else if (rec.equals(Constants.NEXT)) {
				//this.nextOption(ses,form,totalRec,totalrows);   ----checking
				NavigationUtil.processNext(this,totalRec);
			} else if (rec.equals(Constants.PREVIOUS)) {
				//this.previousOption(ses,form,totalRec,totalrows);    ----checking
				NavigationUtil.processPrevious(this);
			} else if (rec.equals(Constants.LAST)) {
				//this.lastOption(ses,form,totalRec,totalrows);  ---checking
				NavigationUtil.processLast(this,totalRec);
			}
		} else {
			navigationnum = 1;
			navBar.setNavigationState("2222222002222");
//			first = Constants.NO;
//			previous = Constants.NO;
		}
	}//End Method

	/**
	 * @param ses
	 * @param totalrows
	 */
	public void setNavigationBarOptions(
		HttpSession ses,
		int totalrows){

		setSession(ses);
		if (totalrows <= 1) {
			navBar.setNavigationState("2212212000002");
//			first = Constants.NO;
//			next = Constants.NO;
//			previous = Constants.NO;
//			last = Constants.NO;
//			gto = Constants.NO;
//			search = Constants.YES;
//			insert = Constants.YES;
		} else if (totalrows <= rows) {
			navBar.setNavigationState("2222222000002");
//			first = Constants.NO;
//			next = Constants.NO;
//			previous = Constants.NO;
//			last = Constants.NO;
//			gto = Constants.NO;
		} else if (rows > 1) {
			if (totalrows == (navigationnum + rows)) {
				navBar.setNavigationState("2222222110022");
//				first = Constants.YES;
//				next = Constants.NO;
//				previous = Constants.YES;
//				last = Constants.NO;
			} else if ((totalrows + 1) == (navigationnum + rows)) {
				navBar.setNavigationState("2222222110022");
//				first = Constants.YES;
//				next = Constants.NO;
//				previous = Constants.YES;
//				last = Constants.NO;
			} else if ((totalrows) < (navigationnum + rows)) {
				navBar.setNavigationState("2222222110022");
//				first = Constants.YES;
//				next = Constants.NO;
//				previous = Constants.YES;
//				last = Constants.NO;
			}
		}

		if (tRows <= 0) {
			navBar.setNavigationState("0010010000000");
//			cancel =
//				copy =
//					commit =
//						delete =
//							update =
//								gto =
//									browse =
//										first =
//											next =
//												previous =
//													last =
//														Constants
//															.NO;
//			search = insert = Constants.YES;
		}
	}//End Method

	/**
	 *
	 */
	public void setConstants(){
		this.setNvNumstr(this.screenName + Constants.NAVIGATIONROWNUM);
		this.setCmtActionStr(this.screenName + Constants.CMTACTION);
		this.setCRowStr(this.screenName + Constants.CROW);
		this.setWhereClauseStr(this.screenName + Constants.WHERECLASE);
		this.setTotalRecStr(this.screenName + Constants.TOTALREC);
		this.setContainer(this.screenName);
	}//End Method

	/**
	 * @param ses
	 * @param form
	 */
	public void setTabOption(
		HttpSession ses,
		BaseForm form){

			if (form.getTabUse() != null
				&& (form.getTabUse().toUpperCase()).indexOf("1") != -1) {

				ses.removeAttribute(
					(String) ses.getAttribute(
						this.screenName + "MTAB"));
				ses.removeAttribute(this.screenName + "MTAB");
			} else if (
				form.getTabname() != null && form.getTabname().length() > 0) {
				ses.setAttribute(
					this.screenName + "MTAB",
					form.getTabname());
			}
	}//End Method

	/**
	 * @param ses
	 * @return
	 */
	public String getWhereClause(HttpSession ses, BaseForm form){
		String qry="";
		if(super.getPreviousAction()!=null && super.getPreviousAction().equalsIgnoreCase(ComponentConstants.SEARCH)
		   && super.getCurrentAction()!=null && super.getCurrentAction().equalsIgnoreCase(ComponentConstants.COMMIT)
		   && form.getMaintabname()!=null){
			if(ses.getAttribute(ComponentConstants.ORIGINAL_SEARCH_QUERY_MASTERTAB)==null){
				qry = (String) ses.getAttribute(whereClauseStr);
				ses.setAttribute(ComponentConstants.ORIGINAL_SEARCH_QUERY_MASTERTAB, qry);
			}else{
				qry = (String) ses.getAttribute(whereClauseStr);
				ses.setAttribute(ComponentConstants.ORIGINAL_SEARCH_QUERY_MASTERTAB, qry);
			}
		}
		if(ses.getAttribute(ComponentConstants.ORIGINAL_SEARCH_QUERY_MASTERTAB)!=null && form.getMaintabname()!=null){
			qry = (String)ses.getAttribute(ComponentConstants.ORIGINAL_SEARCH_QUERY_MASTERTAB);
		}else{
			qry = (String) ses.getAttribute(whereClauseStr);
		}
		if(!ComponentsUtil.isNullOrEmptyString((String)ses.getAttribute(this.screenName+"lockWhereClause"))){
			qry = (String)ses.getAttribute(this.screenName+"lockWhereClause");
			navigationnum = Integer.parseInt((String)ses.getAttribute(this.screenName+"locknavigationnum"));
		}

		// Added By ILYAS [06/12/2005]
		qry = handleSearchCriteria(ses,qry, form);
		if(!ComponentsUtil.isNullOrEmptyString((String)ses.getAttribute(this.screenName+"lockWhereClause"))){
			qry = (String)ses.getAttribute(this.screenName+"lockWhereClause");
			navigationnum = Integer.parseInt((String)ses.getAttribute(this.screenName+"locknavigationnum"));
			if(this.request.getParameter("tabname") != null)
			ses.setAttribute(this.request.getParameter("tabname")+"_NavNumber",navigationnum+"");
		}
		return qry;
	}//End Method

	/**
	 * handleSearchCriteria
	 * handles Search Criteria in Session
	 * Primarily for Parent Tab [Catalog Screen]
	 */
	private String handleSearchCriteria(HttpSession ses, String qry, BaseForm form) {

		String scinsession = this.request.getParameter("storeCriteriaInSession");
		String tabname = null;
		String wc = null;
		String navNumber = null;
		String recValue = this.request.getParameter("rec");

		if(scinsession != null && scinsession.trim().equals("Y")){

			tabname = this.request.getParameter("tabname");
			wc = (String)ses.getAttribute(tabname+"_WhereClauseStr");
			navNumber = (String)ses.getAttribute(tabname+"_NavNumber");

			if(recValue != null){
			if(recValue.equals(Constants.COMMIT)){
				wc = null;
				navNumber = null;
				navigationnum = 1;
				ses.setAttribute(tabname+"_NavNumber",navigationnum+"");
			}
			}
			if(wc != null){
				qry =wc;
				if(navNumber != null && navNumber.trim().length()>=1)
				navigationnum = Integer.parseInt(navNumber);
				form.setRec(recValue);

				/* Checking If Screen Level Where Clause Lost
				   Then Srtting Tab Where Clause as Screen Level
				   So that Clicking Browse can get that easily.*/
				String screenWhereClause = (String)ses.getAttribute(whereClauseStr);
				if(screenWhereClause == null
						|| screenWhereClause.trim().length() < 1){
					ses.setAttribute(whereClauseStr,qry);
				}


			}
			else{
				// To Save the Simple Find OK Operation
				if(qry == null)qry = " ";
				ses.setAttribute(tabname+"_WhereClauseStr", qry);
				}
			}


		 // Clearing Session
		 if(recValue != null && recValue.equals(Constants.COMMIT)){
				// Clearing Session Values + Any Other That is Necessary
				navigationnum = 1;
				ses.setAttribute(nvNumstr, null);
				//ses.setAttribute(tabname+"_NavNumber",navigationnum+"");
			}
		// End Clearing Session

		System.out.println("::::::: Query ----------------"+qry);
		System.out.println("::::::: Navigation Number ----------------"+navigationnum);


		return qry;

	}
	/**
	 * @param ses
	 * @param qry
	 * @param rec
	 */
	public void setTotalRecords(
		HttpSession ses,
		String qry,
		String rec){
		String totalRec = null;
		int totalrows = 0;

		if(ses.getAttribute(ComponentConstants.ORIGINAL_SEARCH_QUERY_MASTERTAB)!=null && qry!=null && qry.trim().length() >= 0){
			if(CommonUtilities.trim(CommonUtilities.fixNull(query.getCountQuery())).equals(""))
				totalrows = query.getSqlCount(getCountQry(query.getSqlStart() + qry + query.getSqlEnd()),conn);
			else
				totalrows = query.getSqlCount(query.getCountQuery(),conn);
		}else if(ses.getAttribute(ComponentConstants.ORIGINAL_SEARCH_QUERY_MASTERTAB)==null){
			if(CommonUtilities.trim(CommonUtilities.fixNull(query.getCountQuery())).equals(""))
				totalrows = query.getSqlCount(getCountQry(query.getSqlStart() +	qry +query.getSqlEnd()),conn);
			else
				totalrows = query.getSqlCount(query.getCountQuery(),conn);
		}
		tRows = totalrows;
		totalRec = String.valueOf(totalrows);
		ses.setAttribute(totalRecStr, totalRec);
		ses.setAttribute(
		  this.screenName + Constants.CURRENT_ACTION,
		  rec);
		if(!rec.equals("del") && !rec.equals("upd"))
			this.setNavigationOption(ses,rec,form,totalRec,totalrows);
		this.setNavigationBarOptions(ses,totalrows);

	}//End Method

	/**
	 * @param ses
	 */
	public void setNavigationNumber(HttpSession ses){

		String navigationnumstr = (String) ses.getAttribute(nvNumstr);
		if(navigationnumstr==null && ses.getAttribute(ComponentConstants.CURRENT_RECORD)!=null){
			navigationnumstr = (String) ses.getAttribute(ComponentConstants.CURRENT_RECORD);
		}
		if (navigationnumstr == null){
			navigationnumstr = "1";
			navBar.setNavigationState("2222222002222");
//			first = Constants.NO;
//			previous = Constants.NO;
		}
		//1- Put navigationnumstr in Session.
		ses.setAttribute(ComponentConstants.CURRENT_RECORD,navigationnumstr);

		//2- If the current action is 'nxt' then dont get navigationnumstr from session otherwise get it from there.
		if( ses.getAttribute(ComponentConstants.CURRENT_RECORD)!=null && super.getCurrentAction()!=null
			&& !super.getCurrentAction().equalsIgnoreCase(ComponentConstants.FIRST)
			&& !super.getCurrentAction().equalsIgnoreCase(ComponentConstants.PREVIOUS)
			&& !super.getCurrentAction().equalsIgnoreCase(ComponentConstants.NEXT)
			&& !super.getCurrentAction().equalsIgnoreCase(ComponentConstants.LAST)){
			navigationnumstr = (String)ses.getAttribute(ComponentConstants.CURRENT_RECORD);
		}
		//The following code provides navigation row number count and record fetching accordingly for navigation
		//within a window.
			navigationnum = Integer.parseInt(navigationnumstr);
		//Resetting session rownum
		ses.setAttribute(this.screenName + Constants.NAVIGATIONROWNUM, navigationnumstr);
		if (navigationnum <= 0){
			navigationnum = 1;
			navBar.setNavigationState("2222222002222");
//			first = Constants.NO;
//			previous = Constants.NO;
		}
	}//End Method

	public void checkingRecords(
		int tRows,
		boolean searchPerformed,
		String rec){
		if(!screenName.equals("fundsinfoloader")){
			System.out.println("-))))total no of rows are "+tRows);
			if (this.tRows > 0) {
				//Following if statement added by Maqsood Shahzad

				if(session.getAttribute(Constants.PREVIOUS_ACTION_VAL)!=null && session.getAttribute(Constants.PREVIOUS_ACTION_VAL).toString().trim().equalsIgnoreCase("ser")
					&& searchPerformed){

					Object[] temp = new Object[2];
					temp[0] = tRows+"";
					temp[1] = navigationnum+"";
					query.addMessageKey("message.catalog.searchsuccessful",temp);
				}
				//browse = Constants.YES;
				navBar.setBrowse(true);
				navBar.setHistory(true);
			}else {
				//Following if statement added by Maqsood Shahzad
				if(session.getAttribute(Constants.PREVIOUS_ACTION_VAL)!=null && session.getAttribute(Constants.PREVIOUS_ACTION_VAL).toString().trim().equalsIgnoreCase(ComponentConstants.SEARCH)
					   && rec.equalsIgnoreCase(ComponentConstants.FIRST)){

				query.addErrorKey("error.catalog.norecordfound");
				//browse = Constants.NO;
				navBar.setBrowse(false);
				}
			}
		}else{
			if (this.tRows > 0) {
				//browse = Constants.YES;
				navBar.setBrowse(true);
			} else {
				//browse = Constants.NO;
				navBar.setBrowse(false);
			}
		}

	}//End Method

}//End Class