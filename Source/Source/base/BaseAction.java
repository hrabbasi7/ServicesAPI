 /*
 * Created on Feb 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */


package com.i2c.component.base;



import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;

import com.i2c.component.backend.ui.*;
import com.i2c.component.util.ComponentConstants;
import com.i2c.util.DatabaseHandler;
import com.i2c.util.*;

import java.sql.*;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class BaseAction extends org.apache.struts.action.Action {

	protected  String success="";
	protected String error="";


	abstract protected boolean processAction(ActionForm form,java.sql.Connection conn,HttpServletRequest request ) throws Exception;
	protected boolean processAction(ActionForm form,java.sql.Connection conn,HttpServletRequest request,int nvnum ) throws Exception{return false;};
	abstract public String toString();
	protected String processAction(java.sql.Connection conn, ActionForm form,HttpServletRequest request ) throws Exception{return null;};

	public ActionForward executeBaseAction(BaseAction act,ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		java.sql.Connection conn =null;
		System.out.println("Start Processing Request for [ "+act.toString()+" ]>");
		try
		{
			conn = DatabaseHandler.getConnection(act.toString(),request);
			if(act.processAction(form,conn,request))
			{
				System.out.println("Status Calling [ "+act.success+" ]");
				return mapping.findForward(act.success);
			}
			else
			{
				System.out.println("Status Calling [ "+act.error+" ]");
				return mapping.findForward(act.error);
			}
   		}
		catch(Throwable the)
		{
			the.printStackTrace();
			System.out.println("Status Calling [ "+act.error+" ]");
			return mapping.findForward(act.error);
		}
		finally
		{
			DatabaseHandler.returnConnection(conn,act.toString());
			System.out.println("End Porocessing Request for [ "+act.toString()+" ] <");
		}
	}

	public ActionForward executeBaseAction(BaseAction act,ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,int nvnum) throws Exception
	{
		System.out.println("Start Processing Request for [ "+act.toString()+" ]>");
		java.sql.Connection conn =null;
		try
		{
			conn = DatabaseHandler.getConnection(act.toString(),request);
			//Do all the Server side validations here, as it cannot be done in CardsBaseForm because, incase of errors, the struts framework forwards control to input JSP
			//and any refernce data controls present on that page are not filled since in MCP Admin site, we have not mechanism to populate reference data controls on JSP
			//and that mechanism is written inside Action classes. So, i have to violate Strut's approach of returning to error page incase ActionErrors object is returned
			//from validate() method.
			/*
			 	1- Do validation just like i did initially in the CardsBaseForm class.
			 	2- That should do the job.
			 */
			ActionErrors actionErrors = validate(mapping,request,(BaseForm)form);

			if (act.processAction(form, conn, request)) {
				if(act.processAction(form,conn,request,nvnum))
				{
					System.out.println("Status Calling [ "+act.success+" ]");
					return mapping.findForward(act.success);
				}
			}

			System.out.println("Status Calling [ "+act.error+" ]");
			return mapping.findForward(act.error);

		}
		catch(Throwable the)
		{
			the.printStackTrace();
			System.out.println("Status Calling [ "+act.error+" ]");
			return mapping.findForward(act.error);
		}
		finally
		{
			DatabaseHandler.returnConnection(conn,act.toString());
			System.out.println("End Porocessing Request for [ "+act.toString()+" ] <");

		}
	}

	/**
	 * IT SUPPORTS TO DEFINE OWN FORWARDS
	 * BECAUSE IT CALLS protected String processAction(java.sql.Connection conn, ActionForm form,HttpServletRequest request,int nvnum ) throws Exception{return null;};
	 * AND IT FORWARDS TO THE RETURN STRING THAT WILL BE DEFINED AND RETURNED BY DEVELOPER.
	 * @param act
	 * @param form
	 * @param mapping
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */

	public ActionForward executeBaseAction(BaseAction act,ActionForm form, ActionMapping mapping,  HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		System.out.println("Start Processing Request for [ "+act.toString()+" ]>");
		java.sql.Connection conn =null;
		String fwd = null;
		try
		{
			conn = DatabaseHandler.getConnection(act.toString(),request);
			//Do all the Server side validations here, as it cannot be done in CardsBaseForm because, incase of errors, the struts framework forwards control to input JSP
			//and any refernce data controls present on that page are not filled since in MCP Admin site, we have not mechanism to populate reference data controls on JSP
			//and that mechanism is written inside Action classes. So, i have to violate Strut's approach of returning to error page incase ActionErrors object is returned
			//from validate() method.
			/*
			 	1- Do validation just like i did initially in the CardsBaseForm class.
			 	2- That should do the job.
			 */
			ActionErrors actionErrors = validate(mapping,request,(BaseForm)form);
			if (act.processAction(form, conn, request)) {
				fwd = act.processAction(conn,form,request);
				if(fwd != null)
				{
					System.out.println("Status Calling [ "+fwd+" ]");
					return mapping.findForward(fwd);
				}
			}
		 return null;
		}
		catch(Throwable the)
		{
			the.printStackTrace();
			System.out.println("Status Calling [ "+fwd+" ]");
			return mapping.findForward(act.error);
		}
		finally
		{
			DatabaseHandler.returnConnection(conn,act.toString());
			System.out.println("End Porocessing Request for [ "+act.toString()+" ] <");

		}
	}// end method


	public Hashtable returnSelectionList(){return null;}

	public void saveErrors(ArrayList list,HttpServletRequest request)
	{
	   super.saveErrors(request, CommonUtilities.saveErrors(list));
	}

	public void saveErrors(ArrayList list, ArrayList objectsList, HttpServletRequest request)
	{
		super.saveErrors(request, CommonUtilities.saveErrors(list,objectsList));
	}

	/**
	 * @author eyaqub
	 * @description		The following methods have been added to provide access to Struts Framework's features for
	 * 					displaying errors and messages of the server side
	 */
	public void saveErrors(HttpServletRequest arg1, ActionErrors arg2){super.saveErrors(arg1,arg2);};
	public void saveMessages(HttpServletRequest arg1, ActionErrors arg2){super.saveMessages(arg1,arg2);};

	/**
	 * @author eyaqub
	 * @description		This method will be used by the derived classes to set their 'Reference Data' at the instance
	 * 					level. The method will be overriden in Action classes and SearchAction will call it from there.
	 */
	protected void setReferenceData(Connection conn){};


	/**
	 * @author eyaqub
	 * @description 	This is the Struts Framework method. It was previously written in derived classes i.e., Action
	 * 					and SearchAction classes. Overriding this method in this class is the correct way to delegate
	 * 					control to the appropriate processAction() methods of the derived classes. Also the users now
	 * 					wont need to override this method in derived classes. It also eliminates need for executeBaseAction() method.
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		java.sql.Connection conn =null;
		this.success="success";
		this.error="error";
		System.out.println("Start Processing Request for [ "+this.toString()+" ]>");
		try
		{
			conn = DatabaseHandler.getConnection(this.toString(),request);

			//Do all the Server side validations here, as it cannot be done in CardsBaseForm because, incase of errors, the struts framework forwards control to input JSP
			//and any refernce data controls present on that page are not filled since in MCP Admin site, we have not mechanism to populate reference data controls on JSP
			//and that mechanism is written inside Action classes. So, i have to violate Strut's approach of returning to error page incase ActionErrors object is returned
			//from validate() method.
			/*
				1- Do validation just like i did initially in the CardsBaseForm class.
				2- That should do the job.
			 */
			ActionErrors actionErrors = validate(mapping,request,(BaseForm)form);

			if(this.processAction(form,conn,request))
			{
				System.out.println("Status Calling [ "+this.success+" ]");
				return mapping.findForward(this.success);
			}
			else
			{
				System.out.println("Status Calling [ "+this.error+" ]");
				return mapping.findForward(this.error);
			}
		}
		catch(Throwable the)
		{
			the.printStackTrace();
			System.out.println("Status Calling [ "+this.error+" ]");
			return mapping.findForward(this.error);
		}
		finally
		{
			DatabaseHandler.returnConnection(conn,this.toString());
			System.out.println("End Processing Request for [ "+this.toString()+" ] <");
		}
	}

	abstract public ActionErrors validate(ActionMapping mapping,HttpServletRequest request, BaseForm baseForm);
}