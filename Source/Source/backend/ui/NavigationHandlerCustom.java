/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.backend.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import com.i2c.cards.*;
import com.i2c.cards.util.*;

import com.i2c.cards.*;
import com.i2c.component.base.*;
import com.i2c.component.util.*;
import com.i2c.cards.security.PermissionInfoObj;

/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class NavigationHandlerCustom extends NavigationBase {

	/**
	 *
	 */

	public NavigationHandlerCustom() {
		super();
		// TODO Auto-generated constructor stub
	}
	public NavigationHandlerCustom(HttpServletRequest request) {
		super();
		setSession(request.getSession(false));
		// TODO Auto-generated constructor stub
	}

	public int executeAction(
			BaseForm form,
			Connection conn,
			BaseHome query,
			HttpServletRequest request)
		{
		this.query = query;
		this.request=request;
		this.form=form;
		this.conn=conn;


		// Added by ILYAS [07/12/2005]
		form.setMaintabname(null);


		//setting the previous action in session for security module


		this.setLastActionInSession(request.getSession(false), form);

		//The following statement will do one of the following things:
		//1- When executing for the first time, it will set the attribute "ComponentConstants.CURRENT_SCREEN" into session
		//2-
		String isMenu = request.getParameter("isMenu");
		if(isMenu == null){
			isMenu = "Y";
		}
		if(isMenu.trim().equalsIgnoreCase("Y")){
			removeUnrelatedContainerInfo(request.getSession(false));
		}
		// 1) validateAction(Task) based on the role.
		// 2) Enable/disable the Navigation bar based on the role.
		//this.setUniqueIdStr(this.screenName+Constants._ID);

		//Getting the 'Task Permission Object' from session to implement enabling/disabling of Navigation Bar buttons.
		PermissionInfoObj permissionInfoObj = null;

		if(this.request.getSession().getAttribute(CardsConstants.TASK_PERMISSION_INFO_OBJ)!=null)
		{
			permissionInfoObj = (PermissionInfoObj) this.request.getSession().getAttribute(CardsConstants.TASK_PERMISSION_INFO_OBJ);
		}

		//cancel = copy = commit = delete = update = gto = browse= first = next = previous = last = Constants.NO;
		// Edited by ILYAS 03/08
		navBar.setCopy(false);
		navBar.setDelete(false);
		navBar.setUpdate(false);
		navBar.setFirst(false);
		navBar.setNext(false);
		navBar.setPrevious(false);
		navBar.setLast(false);
		navBar.setGoTo(false);
		navBar.setBrowse(false);

		//search = insert =Constants.YES;
		// Edited by ILYAS 03/08
		navBar.setSearch(true);
		navBar.setInsert(true);

		//This will not run for the first time.
		if (query != null) {
			query.setNVHandler(this);
		}


		try{
			if(!ComponentsUtil.applyDoublePassword(conn)){
				request.setAttribute("ShowUnlock","NO");
			}
		}catch(Exception e){
			e.printStackTrace();
		}


		this.session = request.getSession(false);

		//Ignoring tab code
		if(form.getTabUse()!=null && (form.getTabUse().toUpperCase()).indexOf("1")!=-1) {

			this.session.removeAttribute((String)this.session.getAttribute(this.screenName+"MTAB"));
			this.session.removeAttribute(this.screenName+"MTAB");
		}
		//Ignoring tab code
		else if(form.getTabname()!=null  && form.getTabname().length()>0){
				this.session.setAttribute(this.screenName+"MTAB", form.getTabname());
				//this.session.setAttribute(form.getMaintabname(), form.getTabname());
		}

	//	 moved the code to another method Maqsood Shahzad (22-09-05)
		this.enableDisableControls(form);
		// Enable/disable the Navigation bar based on the role.
	//	  moved the code to another method  Maqsood Shahzad (22-09-05)
		 if(null!=screenName)
		 {
			 roleBasedEnableDisable();
		 }
		this.setList();

		if (this.strutsAction!=null && this.strutsAction.returnSelectionList() != null) {
			enableSelectionList(this.strutsAction.returnSelectionList());
		}
		//This will execute when user has presssed New button and NOT the Copy button
		if(this.table==null)
		{
			table = new Hashtable();
			if(this.getSession()!=null  )
			this.getSession().setAttribute(this.screenName,this.table);

		}
		return Constants.ACCESS_GRANTED;
	}

/**
 *
 * @param form
 * @author mshahzad
 */

private void enableDisableControls(BaseForm form)
{
	String rec = form.getRec();
		if (rec != null) {

			session.setAttribute(
				this.screenName + Constants.CURRENT_ACTION,
				rec);
			//If Find is pressed, enable OK and Cancel button and disable all other buttons.
			if (rec.trim().equals(Constants.SEARCH)) {

				session.setAttribute(this.screenName + Constants.CMTACTION,	Constants.SEARCH);
			}
			//If New is pressed, enable OK and Cancel button and disable all other buttons.
			else if (rec.trim().equals(Constants.INSERT)) {
				session.setAttribute(this.screenName + Constants.CMTACTION, Constants.INSERT);
			}
			//If Copy is pressed, enable OK and Cancel button and disable all other buttons.
			else if (rec.trim().equals(Constants.COPY)) {
				table =	(Hashtable) session.getAttribute(this.screenName + Constants.CROW);
				session.setAttribute(this.screenName + Constants.CMTACTION,	Constants.INSERT);
				if (table != null) {
					this.idTable =	(Hashtable) table.get(ComponentConstants.UNIQUEIDLIST);
					table = this.removeUniqueIds(table);
					session.setAttribute(this.screenName, table);
				}
			}

			//cancel = Constants.YES;
			//search = Constants.NO;
			//commit = Constants.YES;
			//insert = Constants.NO;

			// Edited by ILYAS 03/08
			navBar.setCancel(true);
			navBar.setSearch(false);
			navBar.setCommit(true);
			navBar.setInsert(false);

		}


}

/**
 *
 * @param permissionInfoObj
 * @author mshahzad
 */

private void roleBasedEnableDisable()
{
	setNavigationInSession();
	/*
	session.setAttribute(screenName + Constants.COMMIT_ENABLED, commit);
	session.setAttribute(screenName + Constants.CANCEL_ENABLED, cancel);
	session.setAttribute(screenName + Constants.DELETE_ENABLED, delete);
	session.setAttribute(screenName + Constants.UPDATE_ENABLED, update);
	session.setAttribute(screenName + Constants.SEARCH_ENABLED, search);
	session.setAttribute(screenName + Constants.FIRST_ENABLED, first);
	session.setAttribute(screenName + Constants.NEXT_ENABLED, next);
	session.setAttribute(screenName + Constants.GOTO_ENABLED, gto);
	session.setAttribute(screenName + Constants.BROWSE_ENABLED, browse);
	session.setAttribute(screenName + Constants.PREVIOUS_ENABLED,previous);
	session.setAttribute(screenName + Constants.LAST_ENABLED, last);
	session.setAttribute(screenName + Constants.COPY_ENABLED, copy);
	session.setAttribute(screenName + Constants.INSERT_ENABLED, insert);
	session.setAttribute(screenName + Constants.NVENABLE, Constants.NO);
	*/
}
}