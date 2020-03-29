/*
 * Created on Dec 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.framework.ui;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.html.FormTag;


import com.i2c.cards.util.*;
import com.i2c.component.framework.taglib.controls.BaseBodyControlTag;
import java.util.*;
import javax.servlet.jsp.JspWriter;
import com.i2c.util.*;

import com.i2c.component.util.*;

/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ValueContainerControl extends  BaseBodyControlTag  {

	/**
	 *
	 */
	private String valueContainer;
	private ArrayList enableList;
	private ArrayList disableList;
	private ArrayList readAlow;
	private ArrayList readDisallow;
	private String enableAll;
	private String readOnly;
	private String rm;
	private Hashtable typeTable = new Hashtable();
	private String formName;
	private String actionName;
	private String actionMode;
	private String imgLocation;
	private StringBuffer formatBuf = null;

	public int doStartTag()
	{
		formatBuf = new StringBuffer("");

		//Added by Edwin to access Validation Properties hashmap in CardsBaseForm's validate() method.
		this.pageContext.getSession().setAttribute(ComponentConstants.VALUE_CONTAINER_NAME, this.valueContainer);

		if( this.pageContext.getSession().getAttribute(valueContainer + Constants.UPDATEERR)!=null )
		{
			if( ((String)this.pageContext.getSession().getAttribute(valueContainer + Constants.UPDATEERR)).equals(Constants.YES)){
				try{
					pageContext.getOut().print(" <script Language=\"JavaScript\"> ");
					pageContext.getOut().print(" updMode=\"YES\"; ");
					pageContext.getOut().print(" </script>");
					this.pageContext.getSession().removeAttribute(valueContainer + Constants.UPDATEERR);
				}catch(Exception exp){
					exp.printStackTrace();
				}
			}
		}

		if( this.pageContext.getSession().getAttribute(valueContainer  + Constants.UPDATEACTIONNAME )!=null)
		{
			try{
				pageContext.getOut().print(" <script Language=\"JavaScript\"> ");
				pageContext.getOut().print(" actionName=\""+this.pageContext.getSession().getAttribute(valueContainer  + Constants.UPDATEACTIONNAME )+"\" ");
				pageContext.getOut().print(" </script>");

			}catch(Exception exp){
				exp.printStackTrace();
			}

			this.pageContext.getSession().removeAttribute(valueContainer + Constants.UPDATEACTIONNAME);
		}

		if( this.pageContext.getSession().getAttribute(valueContainer  + Constants.UPDATEFORMNAME)!=null)
		{
			try{
				pageContext.getOut().print(" <script Language=\"JavaScript\"> ");
				//out.print("  <input type=hidden name= \"formname\" >");
				//pageContext.getOut().print(" document.forms[0].name= \""+this.pageContext.getSession().getAttribute(valueContainer  + Constants.UPDATEFORMNAME )+"\" ");
				//pageContext.getOut().print(" formName=\""+this.pageContext.getSession().getAttribute(valueContainer  + Constants.UPDATEFORMNAME )+"\" ");
				pageContext.getOut().print(" for(i=0; i<document.forms.length; i++) { ");
				pageContext.getOut().print(" if( document.forms[i].name ==\""+this.pageContext.getSession().getAttribute(valueContainer  + Constants.UPDATEFORMNAME )+"\" ){ ");
					pageContext.getOut().print(" formName=document.forms[i]; " );
					pageContext.getOut().print(" } ");
					pageContext.getOut().print(" } ");
				//
				pageContext.getOut().print(" </script>");

			}catch(Exception exp){
				exp.printStackTrace();
			}

			this.pageContext.getSession().removeAttribute(valueContainer + Constants.UPDATEFORMNAME);
		}

		if( this.pageContext.getSession().getAttribute(valueContainer  + Constants.UPDATEMODE)!=null)
		{
			try{
				pageContext.getOut().print(" <script Language=\"JavaScript\"> ");
				pageContext.getOut().print(" mode=\""+this.pageContext.getSession().getAttribute(valueContainer  + Constants.UPDATEMODE )+"\" ");
				//pageContext.getOut().print(" =\""+this.pageContext.getSession().getAttribute(valueContainer  + Constants.UPDATEMODE )+"\" ");
				pageContext.getOut().print(" </script>");

			}catch(Exception exp){
				exp.printStackTrace();
			}
			this.pageContext.getSession().removeAttribute(valueContainer + Constants.UPDATEMODE);
		}
			if( this.pageContext.getSession().getAttribute(this.valueContainer + Constants.CANCELCOUNTER)!=null){
				try{
					pageContext.getOut().print(" <script Language=\"JavaScript\"> ");
					pageContext.getOut().print(" cancelCounter=\""+this.pageContext.getSession().getAttribute(valueContainer+ Constants.CANCELCOUNTER )+"\" ");
					//pageContext.getOut().print(" =\""+this.pageContext.getSession().getAttribute(valueContainer  + Constants.UPDATEMODE )+"\" ");
					pageContext.getOut().print(" </script>");

				}catch(Exception exp){
					exp.printStackTrace();
				}



		}else{
			try{
				pageContext.getOut().print(" <script Language=\"JavaScript\"> ");
				pageContext.getOut().print(" cancelCounter=1");
				//pageContext.getOut().print(" =\""+this.pageContext.getSession().getAttribute(valueContainer  + Constants.UPDATEMODE )+"\" ");
				pageContext.getOut().print(" </script>");

			}catch(Exception exp){
				exp.printStackTrace();
			}

		}


		if(valueContainer!=null) {
			enableList = (ArrayList)this.pageContext.getSession().getAttribute(valueContainer+Constants.ENABLEFIELDS);
			disableList = (ArrayList)this.pageContext.getSession().getAttribute(valueContainer+Constants.DISABLEFIELDS);
			enableAll = (String)this.pageContext.getSession().getAttribute(valueContainer+Constants.ENABLEALL);
			readAlow = (ArrayList)this.pageContext.getSession().getAttribute(valueContainer+Constants.READALLOWED);
			readDisallow =(ArrayList)this.pageContext.getSession().getAttribute(valueContainer+Constants.READDISALLOWED);
			readOnly =(String)this.pageContext.getSession().getAttribute(valueContainer+Constants.READONLY);

//			System.out.println("enableList.hashCode()" +enableList.hashCode());
//			System.out.println("disableList.hashCode()" +disableList.hashCode());
//			System.out.println("readAlow.hashCode()" +readAlow.hashCode());
//			System.out.println("readDisallow.hashCode()" +readDisallow.hashCode());

			//System.out.println("Screen info in dv.."+valueContainer+Constants.DISABLEFIELDS);
			//System.out.println("disableList :"+disableList.getClass());
			//System.out.println("disableList :"+disableList.getClass().getName());
		}
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
	    if(this.valueContainer!=null){
	    	System.out.println("Removing Container :"+ valueContainer);
			if(this.rm!=null && rm.trim().equalsIgnoreCase("true")){
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.ENABLEFIELDS);
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.DISABLEFIELDS);
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.ENABLEALL);
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.READALLOWED);
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.READDISALLOWED);
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.READONLY);

				this.pageContext.getSession().removeAttribute(this.valueContainer);
			}
			else if (this.rm==null){
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.ENABLEFIELDS);
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.DISABLEFIELDS);
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.ENABLEALL);
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.READALLOWED);
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.READDISALLOWED);
				this.pageContext.getSession().removeAttribute(valueContainer+Constants.READONLY);
				this.pageContext.getSession().removeAttribute(this.valueContainer);
			}

			this.pageContext.getSession().removeAttribute(valueContainer+Constants.ININFO);
			this.pageContext.getSession().removeAttribute(valueContainer+Constants.ERRORVALUE);

	    }
		JspWriter out = pageContext.getOut();
	    try {

	    if(this.formatBuf!=null && this.formatBuf.length()>0){
	    	 out.print("  <input type=hidden name= \"insformat\" value="+ this.formatBuf.toString()+">  ");
	    }

		out.print("  <input type=hidden name= \"formname\" >");

		out.print("  <input type=hidden name= \"requrl\" >");
		String str1 = (String) this.pageContext.getSession().getAttribute(valueContainer + Constants.CANCELCOUNTER);
		if(str1==null){
			str1="0";
			//this.pageContext.getSession().getAttribute(valueContainer + Constants.CANCELCOUNTER):"0");
		}
		out.print("  <input type=hidden name= \"cancleCounter\" value="+ str1 +">");
		this.pageContext.getSession().removeAttribute(valueContainer + Constants.CANCELCOUNTER);

		//out.print("  <input type=hidden name= \"rec\" >");
	    Enumeration enumeration = this.typeTable.keys();

	    String fieldName=null;
	    boolean isStart=true;
	    boolean isEnd=false;
		out.print("<script  language=\"javascript\"> ");
		out.print(" function validateType(form) { ");
		String strType=null;
		String strRequired=null;
		String strTypePrompt=null;
		Hashtable tTable=null;
	    while(enumeration.hasMoreElements()){

	    	if(isStart){
	    		isEnd=true;
	    		isStart=false;
	    	}
	    	fieldName = (String)enumeration.nextElement();
			tTable = (Hashtable)(typeTable.get(fieldName));
	    	if(tTable!=null) {

				strType = (String)(tTable.get("T"));
				strRequired = tTable.get("R")!=null? (String)(tTable.get("R")):"";
				strTypePrompt = tTable.get("P")!=null? (String)(tTable.get("P")):"\"\"";

			if(strType!=null) {

				if( strType.equalsIgnoreCase(Constants.CHAR_FIELD) ){


					if(strRequired.equalsIgnoreCase("Y") ) {
					out.println(" if(!validChar(form."+fieldName+","+strTypePrompt+",true) ) { " );
					}else{
						out.println(" if(!validChar(form."+fieldName+","+strTypePrompt+",false) ) { " );
					}
					out.println(" return false;");
					out.println("}");
					//------------------------------|
					//------------------------------|
					// Apply Character Field Related Checks Here.
					//------------------------------|
					//------------------------------|
			//	break;


				}else if(strType.equalsIgnoreCase(Constants.NUMBER_FIELD) ){
					if(strRequired.equalsIgnoreCase("Y") ) {
					out.println(" if(!validNum(form."+fieldName+","+strTypePrompt+",true) ) { " );
					}else{
						out.println(" if(!validNum(form."+fieldName+","+strTypePrompt+",false) ) { " );
					}
					out.println(" return false;");
					out.println("}");

					// Apply Number Field Related Checks here.
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|
				}else if(strType.equalsIgnoreCase(Constants.DATE_FIELD) ){
					if(strRequired.equalsIgnoreCase("Y") ) {
					out.println(" if(!validDate(form."+fieldName+","+strTypePrompt+",true,false) ) { " );
					}else{
						out.println(" if(!validDate(form."+fieldName+","+strTypePrompt+",false,false) ) { " );
					}
					out.println(" return false;");
					out.println("}");

					// Apply Date Field Related Checks here|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|

				}else if(strType.equalsIgnoreCase(Constants.EMAIL_FIELD) ){
					if(strRequired.equalsIgnoreCase("Y") ) {
					out.println(" if(!validEmail(form."+fieldName+","+strTypePrompt+",true) ) { " );
					}else{
						out.println(" if(!validEmail(form."+fieldName+","+strTypePrompt+",false) ) { " );
					}
					out.println(" return false;");
					out.println("}");

					//Apply Email Field Related Checks here|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|

				}
				else if(strType.equalsIgnoreCase(Constants.DECIMAL_FIELD) ){
									if(strRequired.equalsIgnoreCase("Y") ) {
									out.println(" if(!validDecimal(form."+fieldName+","+strTypePrompt+",true) ) { " );
									}else{
										out.println(" if(!validDecimal(form."+fieldName+","+strTypePrompt+",false) ) { " );
									}
									out.println(" return false;");
									out.println("}");

									// Apply Decimal Field Related Checks here|
									//-------------------------------------|
									//-------------------------------------|
									//-------------------------------------|
									//-------------------------------------|

				}
				else if(strType.equalsIgnoreCase(Constants.ALPHANUMERIC_FIELD) ){
					if(strRequired.equalsIgnoreCase("Y") ) {
					out.println(" if(!validAlphaNumeric(form."+fieldName+","+strTypePrompt+",true) ) { " );
					}else{
						out.println(" if(!validAlphaNumeric(form."+fieldName+","+strTypePrompt+",false) ) { " );
					}
					out.println(" return false;");
					out.println("}");

					// Apply Alphanumeric Field Related Checks here|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|

				}
				else if(strType.equalsIgnoreCase(Constants.ALPHANUMERICUPPER_FIELD) ){
					if(strRequired.equalsIgnoreCase("Y") ) {
					out.println(" if(!validAlphaNumericUpper(form."+fieldName+","+strTypePrompt+",true) ) { " );
					}else{
						out.println(" if(!validAlphaNumeric(form."+fieldName+","+strTypePrompt+",false) ) { " );
					}
					out.println(" return false;");
					out.println("}");

					// Apply Alphanumeric Field Related Checks here|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|

				}
				else if(strType.equalsIgnoreCase(Constants.ALPHANUMERICLOWER_FIELD) ){
					if(strRequired.equalsIgnoreCase("Y") ) {
					out.println(" if(!validAlphaNumericLower(form."+fieldName+","+strTypePrompt+",true) ) { " );
					}else{
						out.println(" if(!validAlphaNumericLower(form."+fieldName+","+strTypePrompt+",false) ) { " );
					}
					out.println(" return false;");
					out.println("}");

					// Apply Alphanumeric Field Related Checks here|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|

				}

				else if(strType.equalsIgnoreCase(Constants.ALPHABET_FIELD) ){
									if(strRequired.equalsIgnoreCase("Y") ) {
									out.println(" if(!validAlphaBet(form."+fieldName+","+strTypePrompt+",true) ) { " );
									}else{
										out.println(" if(!validAlphaBet(form."+fieldName+","+strTypePrompt+",false) ) { " );
									}
									out.println(" return false;");
									out.println("}");

									// Apply Alphanumeric Field Related Checks here|
									//-------------------------------------|
									//-------------------------------------|
									//-------------------------------------|
									//-------------------------------------|

				}
				else if(strType.equalsIgnoreCase(Constants.DATETIME_FIELD) ){
					if(strRequired.equalsIgnoreCase("Y") ) {
					out.println(" if(!validDateTime(form."+fieldName+","+strTypePrompt+",true) ) { " );
					}else{
						out.println(" if(!validDateTime(form."+fieldName+","+strTypePrompt+",false) ) { " );
					}
					out.println(" return false;");
					out.println("}");

					// Apply Date & Time Field Related Checks here|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|

				}
				else if(strType.equalsIgnoreCase(Constants.TIME_FIELD) ){
					if(strRequired.equalsIgnoreCase("Y") ) {
					out.println(" if(!validTime(form."+fieldName+","+strTypePrompt+",true) ) { " );
					}else{
						out.println(" if(!validTime(form."+fieldName+","+strTypePrompt+",false) ) { " );
					}
					out.println(" return false;");
					out.println("}");

					// Apply Time Field Related Checks here|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|

				}

				else if(strRequired!=null&& strRequired.equalsIgnoreCase(Constants.REQUIRED_FIELD) ){
					out.println(" if(!validRequired(form."+fieldName+","+strTypePrompt+") ) { " );
					out.println(" return false;");
					out.println("}");

					// Apply Date Field Related Checks here|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|
					//-------------------------------------|
				}
			}
	      }
	    }
	    if(isEnd){
	    	// End of Function here
			out.println(" return true; ");
	    } else{
			out.println(" return true; ");
	    }

		out.println(" }" );
		out.println(" </script> ");

	    }catch(Exception exp){
	    	exp.printStackTrace();
			return SKIP_BODY;
	    }

		return EVAL_PAGE;
	}


	public void setValueContainer(String value){
		valueContainer=value;
	}
	public String getValueContanier(){
		return this.valueContainer;
	}
	public boolean checkEnable(Object field){
		if(field!=null && enableList!=null)
		return this.enableList.contains(field);
		return false;
	}
	public boolean checkDisable(Object field){
		if(field!=null && disableList!=null){
			//System.out.println(" DV DATA LIST :"+ disableList );
			return this.disableList.contains(field);
		}
		return false;
	}
	public boolean enableAll() {
		if(enableAll!=null){
			 if(enableAll.equals(Constants.YES) ) {
				return true;
			 }
		}
		return false;
	}
	public boolean disableAll(){
		if(enableAll!=null){
			if (enableAll.equals(Constants.NO) ) {
				return true;
			}
		}
		return false;
	}
	public boolean checkReadAllowed(Object key){
		if(key!=null && readAlow!=null)
		return this.readAlow.contains(key);
		return false;
	}
	public boolean checkReadDisAllowed(Object key) {
		if(key!=null && readDisallow!=null)
		return this.readDisallow.contains(key);
		return false;
	}
	public boolean readOnlyAll(){
		if( this.readOnly!=null && readOnly.equals(Constants.YES) ) {
				return true;
		}
		return false;
	}
	/**
	 * @return
	 */
	public String getRm() {
		return rm;
	}

	/**
	 * @param string
	 */
	public void setRm(String string) {
		rm = string;
	}

	/**
	 * @return
	 */
	public String getActionMode() {
		return actionMode;
	}

	/**
	 * @return
	 */
	public String getActionName() {
		return actionName;
	}

	/**
	 * @return
	 */
	public String getFormName() {
		return formName;
	}

	/**
	 * @return
	 */
	public String getImgLocation() {
		return imgLocation;
	}

	/**
	 * @param string
	 */
	public void setActionMode(String string) {
		actionMode = string;
	}

	/**
	 * @param string
	 */
	public void setActionName(String string) {
		actionName = string;
	}

	/**
	 * @param string
	 */
	public void setFormName(String string) {
		formName = string;
	}

	/**
	 * @param string
	 */
	public void setImgLocation(String string) {
		imgLocation = string;
	}

	/**
	 * @return
	 */
	public Hashtable getTypeTable() {
		return typeTable;
	}

	/**
	 * @param hashtable
	 */
	public void putType(String field, Object type) {
		typeTable.put(field,type);
	}

	/**
	 * @return
	 */
	public StringBuffer getFormatBuf() {
		return formatBuf;
	}

	/**
	 * @param buffer
	 */
	public void setFormatBuf(String buffer) {
		formatBuf.append(buffer);
	}

}
