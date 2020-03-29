/*
 * Created on Oct 24, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
 * @author milyas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationUtil {

	
	/**
	 * @param totalrows
	 */
	public static void enableDisableNavButtoons(NavigationBase nvBase,int totalrows) {

		//---Added to handle
		//---First Click on Browse or Tab
		if(nvBase.navigationnum ==1){
			nvBase.navBar.setNavigationState("2222222002222");
//			nvBase.first=Constants.NO;
//			nvBase.previous=Constants.NO;
		}
	   if(totalrows<=1 || totalrows<=nvBase.rows){
		nvBase.navBar.setNavigationState("2222222000002");
//		nvBase.first=Constants.NO;
//		nvBase.next=Constants.NO;
//		nvBase.previous=Constants.NO;
//		nvBase.last=Constants.NO;
//		nvBase.gto=Constants.NO;
		}else
		if(nvBase.rows>1) {
			if(totalrows <=(nvBase.navigationnum+nvBase.rows)-1 ){
				nvBase.navBar.setNavigationState("2222222110022");
//				nvBase.first=Constants.YES;
//				nvBase.next=Constants.NO;
//				nvBase.previous=Constants.YES;
//				nvBase.last=Constants.NO;
			}
		}

		//---Extra for Navigation
		if(nvBase.tRows>0){
			nvBase.navBar.setBrowse(true);
			//nvBase.browse=Constants.YES;
		}else{
			nvBase.navBar.setBrowse(false);
			//nvBase.browse=Constants.NO;
		}



	}//end enableDisbleButtons


	/**
	 * processMUpdate
	 */
	public static void processMUpdate(NavigationBase nvBase,String toRows, String fRows) {
		int tt = 0;
		int ttf = 0;
		try {
			//--- This was first scenario but here session becomes unavaiable
			//--- So now these are passed as parameters in this function getting in start
			//--- of request.
			//--- String toRows =(String) getSession().getAttribute(screenName + "to");
			//--- String fRows =(String) getSession().getAttribute(this.screenName + Constants.NAVIGATIONROWNUM);
			if (fRows != null && toRows != null) {
				if (Integer.parseInt(fRows)
					< Integer.parseInt(toRows)) {
					ttf = Integer.parseInt(fRows);
					tt = Integer.parseInt(toRows);
					tt = tt - (ttf - 1);
				} else if (Integer.parseInt(toRows) == 1) {
					tt = 1;
				}else if(Integer.parseInt(fRows)== Integer.parseInt(toRows))  {
					tt = 1;
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		nvBase.query.mUpdate(nvBase.conn,ComponentsUtil.getRequestParametersTable(nvBase.request),tt);
	}//end processMUpdate



	/**
	 * processFirst
	 * is invoked when User Press First Button to view First Record
	 * @param form
	 */
	public static void processFirst(NavigationBase nvBase) {
		nvBase.navigationnum = 1;
		nvBase.navBar.setNavigationState("2222222002222");
//		nvBase.first = Constants.NO;
//		nvBase.previous = Constants.NO;

		// Added by ILYAS [06/12/2005]
		String tabname = nvBase.request.getParameter("tabname");
		if(tabname != null){
			nvBase.getSession().setAttribute(tabname+"_NavNumber",nvBase.navigationnum+"");
		}



	}//end processFirst


	/**
	 * processLast
	 * is invoked when User Press Last Button to view Last Record
	 */
	public static void processLast(NavigationBase nvBase,String totalRec) {
		int totalrows = Integer.parseInt(totalRec);
		nvBase.navigationnum = Integer.parseInt(totalRec);
		if(nvBase.rows>1 && nvBase.navigationnum > nvBase.rows ) {
			nvBase.navigationnum= (nvBase.navigationnum-(nvBase.navigationnum%nvBase.rows))+1;
		}
		nvBase.navBar.setNavigationState("2222222220022");
//		nvBase.next = Constants.NO;
//		nvBase.last = Constants.NO;

		// Added by ILYAS [06/12/2005]
		String tabname = nvBase.request.getParameter("tabname");
		if(tabname != null){
			nvBase.getSession().setAttribute(tabname+"_NavNumber",nvBase.navigationnum+"");
		}


	}//end processLast

	/**
	 * processPrevious
	 * is invoked when User Press Previous Button to view Previous Record
	 * @param form
	 */
	public static void processPrevious(NavigationBase nvBase) {
		if((nvBase.navigationnum-nvBase.rows)>1){
			nvBase.navigationnum = nvBase.navigationnum-nvBase.rows;
		}else{
			nvBase.navigationnum = 1;
		}
		if (nvBase.navigationnum <= 1) {
			nvBase.navBar.setNavigationState("2222222002222");
//			nvBase.first = Constants.NO;
//			nvBase.previous = Constants.NO;
		}
		// Added by ILYAS [06/12/2005]
		String tabname = nvBase.request.getParameter("tabname");
		if(tabname != null){
			nvBase.getSession().setAttribute(tabname+"_NavNumber",nvBase.navigationnum+"");
		}

	}//end processPrevious


	/**
	 * processNext
	 * is invoked when User Press Next Button to viw Next Record
	 * @param form
	 * @param totalRec
	 */
	public static void processNext(NavigationBase nvBase,String totalRec) {
		if((nvBase.navigationnum+nvBase.rows)< Integer.parseInt(totalRec)){
			nvBase.navigationnum = nvBase.navigationnum+nvBase.rows;
		}else{
			nvBase.navigationnum = Integer.parseInt(totalRec);
		}
		if (nvBase.navigationnum
				>= (Integer.parseInt(totalRec))) {
				nvBase.navBar.setNavigationState("2222222220022");
//				nvBase.next = Constants.NO;
//				nvBase.last = Constants.NO;
		}
		// Added by ILYAS [06/12/2005]
		String tabname = nvBase.request.getParameter("tabname");
		if(tabname != null){
			nvBase.getSession().setAttribute(tabname+"_NavNumber",nvBase.navigationnum+"");
		}

	}//end processNext


	/**
	 * processGoTo
	 * is invoked when user presses Go Button by providing Record No
	 * This Function has to take care of MAX and MIN Limits of the
	 * records available to Navigate.
	 * @param form
	 */
	public static int processGoTo(NavigationBase nvBase, BaseForm form) {

		int intgoto=nvBase.getRecGoto(form.getRecgoto());
		int incGoto = 0;
		//----If Input is Just a Number No +|- Sign Included
		if(form.getRecgoto()!=null &&  ! form.getRecgoto().trim().equals("") && !(form.getRecgoto().startsWith("+") || form.getRecgoto().startsWith("-"))){
			
			// For '0' Record should start from 1
			if(intgoto == 0) {
				intgoto = 1;
				nvBase.getSession().setAttribute(nvBase.screenName+Constants.ACTIVE_MSG,Constants.MIN_LIMIT_MSG);
			}
			
			//----Checking MAX Limit
			if ( intgoto>nvBase.tRows   ){
				nvBase.getSession().setAttribute(nvBase.screenName+Constants.ACTIVE_MSG, Constants.MAX_LIMIT_MSG +"["+nvBase.tRows+"]");
				incGoto = nvBase.navigationnum;
			}else
			if(intgoto != 0){
				//nvBase.navigationnum = intgoto;
				incGoto = intgoto;
			}

		}//end if (Input is Just a Number No +|- Sign Included)
		else{
			//----If + Sign
			if ( intgoto>0 ) {
				//----Checking MAX Limit
				if ( (nvBase.navigationnum+(nvBase.rows-1))+intgoto>nvBase.tRows   ){
					nvBase.getSession().setAttribute(nvBase.screenName+Constants.ACTIVE_MSG, Constants.MAX_LIMIT_MSG +"["+nvBase.tRows+"]");
					incGoto = nvBase.navigationnum;
				}else{
					incGoto = nvBase.navigationnum+intgoto;
				}
			}//end if (+ Sign)
			else{
					//----Checking MIN Limit
					if( (nvBase.navigationnum)+intgoto<=0 ) {
						nvBase.getSession().setAttribute(nvBase.screenName+Constants.ACTIVE_MSG,Constants.MIN_LIMIT_MSG);
						incGoto = nvBase.navigationnum;
					}else{
						incGoto = nvBase.navigationnum+intgoto;
					}
			}//end else [- Sign]
		}//end else


		// Added by ILYAS [06/12/2005]
		String tabname = nvBase.request.getParameter("tabname");
		if(tabname != null){
			nvBase.getSession().setAttribute(tabname+"_NavNumber",nvBase.navigationnum+"");
		}

		nvBase.navigationnum = incGoto;
		return incGoto;
	}//end processGoTo




	/***
	 * Old Methods
	 * ************************************************************************************************
	 * @param form
	 * @param totalRec
	 */




	/**
	 * processNext
	 * @param form
	 */
	private void processNext_Old(BaseForm form, String totalRec) {
		/*navigationnum = ((navigationnum+rows)< Integer.parseInt(totalRec) ) ? (navigationnum+rows) : (Integer.parseInt(totalRec) );
		if(navigationnum>=(Integer.parseInt(totalRec))){
			next=Constants.NO;
			last=Constants.NO;
		}*/
	}//end processNext

	/**
	 * processLast
	 * @param form
	 */
	private void processLast_Old(BaseForm form, String totalRec) {
		/*navigationnum = Integer.parseInt(totalRec);

		if(rows>1) {
			if( navigationnum>rows) {
				navigationnum= (navigationnum-(navigationnum%rows))+1;
			}
			if(navigationnum>Integer.parseInt(totalRec)) {
				navigationnum =  (navigationnum-rows);
			}
		}
		next=Constants.NO;
		last=Constants.NO;*/
	}//end processLast

	/**
	 * processFirst
	 * @param form
	 */
	private void processFirst_Old(BaseForm form) {
		/*navigationnum = 1;
		first=Constants.NO;
		previous=Constants.NO;*/
	}//end processFirst

	/**
	 * @param totalrows
	 */
	private void enableDisableNavButtoons_Old(int totalrows) {

		/*
		//---Added to handle
		//---First Click on Browse or Tab
		if(navigationnum ==1){
			first=Constants.NO;
			previous=Constants.NO;
		}
	   if(totalrows<=1 || totalrows<=rows){
			first=Constants.NO;
			next=Constants.NO;
			previous=Constants.NO;
			last=Constants.NO;
			gto=Constants.NO;
		}//else
		//if(totalrows<=rows){
		//	first=Constants.NO;
		//	next=Constants.NO;
		//	previous=Constants.NO;
		//	last=Constants.NO;
		//	gto=Constants.NO;
		//}
		else
		if(rows>1) {
			if(totalrows <=(navigationnum+rows)-1 ){
				first=Constants.YES;
				next=Constants.NO;
				previous=Constants.YES;
				last=Constants.NO;
			}
			//else if((totalrows)<(navigationnum+rows)-1 ){
			//first=Constants.YES;
			//next=Constants.NO;
			//previous=Constants.YES;
			//last=Constants.NO;
		  //}
		}*/

	}//end enableDisbleButtons



	/**
	 * processGoTo
	 * @param form
	 */
	private void processGoTo_Old(BaseForm form) {

		/*
		int intgoto=this.getRecGoto(form.getRecgoto());
		if(form.getRecgoto()!=null &&  ! form.getRecgoto().trim().equals("") && !(form.getRecgoto().startsWith("+") || form.getRecgoto().startsWith("-"))){
			if ( intgoto>tRows   ){
				  // Can't exceed the maximum limit of [totalrows].
				  // OR The value you entered  navigationnum+intgoto is greater than the maximum limit.
				this.getSession().setAttribute(this.screenName+Constants.ACTIVE_MSG, Constants.MAX_LIMIT_MSG +"["+tRows+"]");
			}else if( intgoto > 0){
			navigationnum = intgoto;
			}

			//else if( intgoto==tRows){
			//	navigationnum = intgoto;
			//}
			//else if(intgoto >0){
			//	navigationnum = intgoto;
			//}
		}
		else{
		if ( intgoto>0 ) {
				if ( (navigationnum+(rows-1))+intgoto>tRows   ){
					  // Can't exceed the maximum limit of [totalrows].
					  // OR The value you entered  navigationnum+intgoto is greater than the maximum limit.
					this.getSession().setAttribute(this.screenName+Constants.ACTIVE_MSG, Constants.MAX_LIMIT_MSG +"["+tRows+"]");
				}else{
					incGoto = navigationnum+intgoto;
				}

				//else if( (navigationnum+(rows-1))+intgoto==tRows){
				//	incGoto = navigationnum+intgoto;
			//	}
			//	else{
			//		incGoto = navigationnum+intgoto;
			//	}
			}else if (intgoto<0 ){
				if( (navigationnum)+intgoto<=0 ) {
					this.getSession().setAttribute(this.screenName+Constants.ACTIVE_MSG,Constants.MIN_LIMIT_MSG);
				}else{
					incGoto = navigationnum+intgoto;
				}
			}
		}
		//if(navigationnum==1){
		//	first=Constants.NO;
		//	previous=Constants.NO;
		//}
		 */
	}//end processGoTo
	/**
	 * process
	 * does appropriate operations depending upon the value of REC
	 * @param rec
	 */
	private void process(String rec) throws SQLException {

	/*	//System.out.println(rec);
		//---If Rec Value is Browse|GOTO|FIRST|NEXT|PREV|LAST
		if(!( rec.equals(Constants.BROWSE) || rec.equals(Constants.GOTO) ||rec.equals(Constants.FIRST) || rec.equals(Constants.PREVIOUS) || rec.equals(Constants.NEXT) || rec.equals(Constants.LAST)) ) {
			String rec1=(String)getSession().getAttribute(cmtActionStr); // cmtAction;
			if(rec1!=null){
				rec=rec1;
				System.out.println("REC1: " +rec);
				this.setPreviousAction(screenName,rec);
				System.out.println("Previous Action :"+ this.getPreviousAction());
				getSession().removeAttribute(cmtActionStr);
			}
		}//end if (REC Browse|GOTO|FIRST|NEXT|PREV|LAST)

		String id = (String)getSession().getAttribute(uniqueIdStr); // unqueId
		System.out.println(this.screenName+" : " +id);
		if( id!=null) {

		//---If Rec Value is UPDATE
		if(rec.equals(Constants.UPDATE)){
			query= query.getInstance((java.util.Hashtable)getSession().getAttribute(cRowStr)); // current row.
			query.update(id,form,conn);
			//rec="fst";
			getSession().removeAttribute(whereClauseStr);
			String whereClause = form.getWhereClause();
			if(whereClause!=null && (whereClause.trim().length())>0){
				getSession().setAttribute(whereClauseStr,whereClause);
			}
			rec=Constants.FIRST;
		}//end if (REC UPDATE)

		//----If Rec Value is Delete
		else if(rec.equals(Constants.DELETE)){
			//query = new AgentQuery();
			query.delete(id,conn);
			rec=Constants.FIRST;
			//session.removeAttribute("agentwhereClause");
		}//end if (REC DELETE)
		}///end if (id != null)

		//----If Rec Value is INSERT
		if(rec.equals(Constants.INSERT)){
			//query = new AgentQuery();
			query.insert(form,conn);
			//Move to saved record.
			getSession().removeAttribute(whereClauseStr);
			String whereClause = form.getWhereClause();
			if(whereClause!=null && (whereClause.trim().length())>0){
				getSession().setAttribute(whereClauseStr,whereClause);
			}
			rec=Constants.FIRST;
		}//end if(REC INSERT)

		//----If Rec Value is SEARCH|COMMIT
		if(rec.equals(Constants.SEARCH) || rec.equals(Constants.COMMIT)){
			getSession().removeAttribute(whereClauseStr);
			String whereClause = form.getWhereClause();
			System.out.println("Where Cluase : "+ whereClause);
		if(whereClause!=null && (whereClause.trim().length())>0)
			getSession().setAttribute(whereClauseStr,whereClause);
			rec=Constants.FIRST;
		}//end if (REC SEARCH|COMMIT)*/

	}//end process


}
