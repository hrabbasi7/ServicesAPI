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
import java.text.SimpleDateFormat;
import java.util.*;
import com.i2c.component.util.ComponentConstants;
import com.i2c.cards.*;
import com.i2c.cards.util.*;
import com.i2c.util.*;
import com.i2c.component.base.*;
import com.i2c.component.util.ComponentsUtil;
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

public class NavigationHandler4Edit extends NavigationBase{

	/**
	 * 
	 */
	
	public NavigationHandler4Edit() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public NavigationHandler4Edit(HttpServletRequest request ) {
		super();
		setSession(request.getSession(false));
		// TODO Auto-generated constructor stub
	}

	public int executeAction(BaseForm form ,java.sql.Connection conn,BaseHome query, HttpServletRequest request )
	{
		this.query = query;
		this.request=request; 
		this.form=form; 
		this.conn=conn;
		
		//setting the previous action in session for security module
		this.setLastActionInSession(request.getSession(false), form);
		
		//Getting the 'Task Permission Object' from session to implement enabling/disabling of Navigation Bar buttons. 
		PermissionInfoObj permissionInfoObj = null;
		
		if(this.request.getSession().getAttribute(CardsConstants.TASK_PERMISSION_INFO_OBJ)!=null)
		{
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
		
		try{
			String rec = form.getRec();//request.getParameter(Constants.NVREC);
			
				
			this.setNvNumstr(this.screenName+Constants.NAVIGATIONROWNUM);
			this.setCmtActionStr(this.screenName+Constants.CMTACTION);
			this.setCRowStr(this.screenName+Constants.CROW);
			this.setWhereClauseStr(this.screenName+Constants.WHERECLASE);
			this.setTotalRecStr(this.screenName+Constants.TOTALREC);
			this.setContainer(this.screenName);

		// 1) validateAction(Task) based on the role. Whether the requested task is allowed for this particular User.
		
		// 2) Enable/disable the Navigation bar based on the role.
		
		// 3) 
		    removeUnrelatedContainerInfo(request.getSession(false));		

			setSession(request.getSession(false));
		
			if(form.getTabUse()!=null && (form.getTabUse().toUpperCase()).indexOf("1")!=-1) {
				
				this.session.removeAttribute((String)this.session.getAttribute(this.screenName+"MTAB"));
				this.session.removeAttribute(this.screenName+"MTAB");
			}
			else if(form.getTabname()!=null  && form.getTabname().length()>0){
				this.session.setAttribute(this.screenName+"MTAB", form.getTabname());
					//this.session.setAttribute(form.getMaintabname(), form.getTabname());	
			}
			
			String qry=" ";
			String navigationnumstr = (String)getSession().getAttribute(nvNumstr);
			
	
			if( navigationnumstr ==null){
				navigationnumstr="1";
				navBar.setFirst(false);
				navBar.setPrevious(false);
//				first=Constants.NO;
//				previous=Constants.NO;
			}
			if(form.getRecno()!=null){
				navigationnum = Integer.parseInt(form.getRecno());
			}else{
				navigationnum = Integer.parseInt(navigationnumstr);
			}
			if(navigationnum<=0){
				navigationnum=1;
				navBar.setFirst(false);
				navBar.setPrevious(false);
//				first=Constants.NO;
//				previous=Constants.NO;
			}
			if(rec!=null){
				String id = (String)getSession().getAttribute(uniqueIdStr); // unqueId
				//System.out.println(rec);
				System.out.println(this.screenName+" : " +id);
				if(!( rec.equals(Constants.BROWSE) || rec.equals(Constants.GOTO) ||rec.equals(Constants.FIRST) || rec.equals(Constants.PREVIOUS) || rec.equals(Constants.NEXT) || rec.equals(Constants.LAST)) ) { 
					String rec1=(String)getSession().getAttribute(cmtActionStr); // cmtAction;
					if(rec1!=null){
						rec=rec1;
						System.out.println("REC1: " +rec);
						this.setPreviousAction(screenName,rec);
						System.out.println("Previous Action :"+ this.getPreviousAction());
						getSession().removeAttribute(cmtActionStr);
					}
				}
				if( id!=null) {
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
				}
				else if(rec.equals(Constants.DELETE)){
					//query = new AgentQuery();
					query.delete(id,conn);
					rec=Constants.FIRST;
					//session.removeAttribute("agentwhereClause");
				}
				}
				
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
				}
				if(rec.equals(Constants.SEARCH) || rec.equals(Constants.COMMIT)){
					getSession().removeAttribute(whereClauseStr);
					String whereClause = form.getWhereClause();
					System.out.println("Where Cluase : "+ whereClause);
				if(whereClause!=null && (whereClause.trim().length())>0)	
					getSession().setAttribute(whereClauseStr,whereClause);
					rec=Constants.FIRST;
				}
			}
			qry = (String) getSession().getAttribute(whereClauseStr);
			if(qry==null){
				qry= " ";
			} 
			if(query!=null && !( query.getSqlStart().indexOf("WHERE")!=-1))
			qry=" WHERE ";
			if( form.getId()!=null){
				System.out.println("[Form.getId()]" + form.getId());
				StringTokenizer token= new StringTokenizer(form.getId(),"}");
				StringBuffer buf=new StringBuffer();
				if(token.hasMoreTokens()){
					String s1=token.nextToken();
					String s2= ComponentsUtil.replaceStr(s1,"{","" );
					s2 = ComponentsUtil.replaceStr(s2,"(","'" );  
					s2 = ComponentsUtil.replaceStr(s2,")","'" );
					buf.append(s2);
					while(token.hasMoreTokens()){
						s1=token.nextToken();
						s2= ComponentsUtil.replaceStr(s1,"{"," AND " );
						s2 = ComponentsUtil.replaceStr(s2,"(","'" );  
						s2 = ComponentsUtil.replaceStr(s2,")","'" );
						buf.append(s2);
					}
					qry =  qry +" " + buf.toString();
					System.out.println("Uniqid in where clause"+ qry);
				}
				
			} 
			//System.out.println("Where Clause"+qry);
			System.out.println(query.getSqlStart()+ qry + query.getSqlEnd());
			//System.out.println(" rows "+ rows );
			//System.out.println(" navigationnum "+ navigationnum );
//			String s1 = ""; 
//			String s2 = ""; 
//			String s3 = "";
//			s1 = query.getSqlStart()+ qry + query.getSqlEnd();
//			if(s1!=null && (s1.toUpperCase()).indexOf("FROM")!=-1) {
//				s2 = (s1.substring((s1.toUpperCase()).indexOf("FROM")));
//			}
//			if( s2!=null && (s2.toUpperCase()).indexOf("ORDER")!=-1 ){
//				s3 =  s2.substring(0,(s2.toUpperCase()).indexOf("ORDER"));
//			}
//			if(s3!=null && s3.length()>0)
//				s1 = " SELECT COUNT(*) " + s3;
//			else if( s2!=null && s2.length()>0 ){
//				s1 = " SELECT COUNT(*) " + s2;
//			}
			//table = query.getData( rows,navigationnum,conn, query.getSqlStart()+ qry + query.getSqlEnd(),uniqueIdList);
			String totalRec = null;
			totalrows = query.getSqlCount(getCountQry(query.getSqlStart()+ qry + query.getSqlEnd()),conn); //query.getTotalRows();
			//totalrows = query.getSqlCount(s1,conn); //query.getTotalRows();
			tRows = totalrows;
			totalRec = String.valueOf(totalrows);
			
			//System.out.println("Total Rec : " + totalRec);		
			getSession().setAttribute(totalRecStr,totalRec); // total record
			
			getSession().setAttribute(this.screenName+Constants.CURRENT_ACTION,rec);
			if(rec!=null){
					if(rec.equals(Constants.GOTO)){
						int intgoto=this.getRecGoto(form.getRecgoto());
						System.out.println("RECGOTO : "+intgoto);
						if ( intgoto>0 ) {
								if ( navigationnum+intgoto>totalrows ){
									  // Can't exceed the maximum limit of [totalrows]. 
									  // OR The value you entered  navigationnum+intgoto is greater than the maximum limit.
									 this.getSession().setAttribute(this.screenName+Constants.ACTIVE_MSG,Constants.MAX_LIMIT_MSG+" ["+totalrows+"]");
								}else{
									navigationnum = navigationnum+intgoto;
								}
							}else if (intgoto<0 ){
								if( navigationnum+intgoto<0 ) {
									this.getSession().setAttribute(this.screenName+Constants.ACTIVE_MSG,Constants.MIN_LIMIT_MSG);
								}else{
									navigationnum = navigationnum+intgoto;
								}
							} 
						if(navigationnum==1){
							navBar.setFirst(false);
							navBar.setPrevious(false);
//							first=Constants.NO;
//							previous=Constants.NO;
						}
					}
					else if(rec.equals(Constants.FIRST)){
					navigationnum = 1;
					navBar.setFirst(false);
					navBar.setPrevious(false);
//					first=Constants.NO;
//					previous=Constants.NO;
					}else if (rec.equals(Constants.NEXT)){
						navigationnum = ((navigationnum+rows)< Integer.parseInt(totalRec) ) ? (navigationnum+rows) : (Integer.parseInt(totalRec) );
						if(navigationnum>=(Integer.parseInt(totalRec))){
							navBar.setNext(false);
							navBar.setLast(false);
//							next=Constants.NO;
//							last=Constants.NO;
						}
					}
					else if (rec.equals(Constants.PREVIOUS)){
						navigationnum = ( (navigationnum-rows)>1 ) ? (navigationnum-rows) :1;
						if(navigationnum<=1){
							navBar.setFirst(false);
							navBar.setPrevious(false);
//							first=Constants.NO;
//							previous=Constants.NO;
						}
					}
					else if (rec.equals(Constants.LAST)){
						navigationnum = Integer.parseInt(totalRec);
						if(rows>1) {
							navigationnum = (navigationnum) - rows;
						}
						navBar.setNext(false);
						navBar.setLast(false);
//						next=Constants.NO;
//						last=Constants.NO;
					}
				}else{
					navigationnum=1;
					navBar.setFirst(false);
					navBar.setPrevious(false);
//					first=Constants.NO;
//					previous=Constants.NO;
				}
				if(totalrows<=1){
					navBar.setFirst(false);
					navBar.setPrevious(false);
					navBar.setNext(false);
					navBar.setLast(false);
					navBar.setGoTo(false);
					
//					first=Constants.NO;
//					next=Constants.NO;
//					previous=Constants.NO;
//					last=Constants.NO;
//					gto=Constants.NO;
				}else
				if(totalrows<=rows){
					navBar.setFirst(false);
					navBar.setPrevious(false);
					navBar.setNext(false);
					navBar.setLast(false);
					navBar.setGoTo(false);

//					first=Constants.NO;
//					next=Constants.NO;
//					previous=Constants.NO;
//					last=Constants.NO;
//					gto=Constants.NO;
				}else
				if(rows>1) {
					if(totalrows==(navigationnum+rows) ){
						navBar.setFirst(true);
						navBar.setPrevious(true);
						navBar.setNext(false);
						navBar.setLast(false);

//						first=Constants.YES;
//						next=Constants.NO;
//						previous=Constants.YES;
//						last=Constants.NO;
					}
					else if((totalrows+1)==(navigationnum+rows) ){

						navBar.setFirst(true);
						navBar.setPrevious(true);
						navBar.setNext(false);
						navBar.setLast(false);

//						first=Constants.YES;
//						next=Constants.NO;
//						previous=Constants.YES;
//						last=Constants.NO;
					}
					else if((totalrows)<(navigationnum+rows) ){

						navBar.setFirst(true);
						navBar.setPrevious(true);
						navBar.setNext(false);
						navBar.setLast(false);

//					first=Constants.YES;
//					next=Constants.NO;
//					previous=Constants.YES;
//					last=Constants.NO;
 				  }
				}
				
			if(query!=null){
				System.out.println(" rows "+ rows );
				System.out.println(" navigationnum "+ navigationnum );
				table = query.getData( rows,navigationnum,conn, query.getSqlStart()+ qry + query.getSqlEnd(),uniqueIdList,super.getRequest()  );
			}
			System.out.println("TABLE DATA [" +table +" ]");
			super.setPermissionInfoObj(permissionInfoObj);
			setNavigation();
						
		}catch(Exception exp){
			exp.printStackTrace();
			return Constants.ACCESS_DENIED;
		}
		return Constants.ACCESS_GRANTED ;
	}
}