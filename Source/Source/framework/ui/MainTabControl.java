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


/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MainTabControl extends  BaseBodyControlTag  {

	/**
	 * 
	 */
	
	private String tabname;
	private String seletedTab;
	private String defaultSelectedTab;
	private String secName=null;
	public int doStartTag(){
		
		try {
			ValueContainerControl VC =
				(ValueContainerControl) this.getParent();
			//System.out.println("PARENT: " + VC.getValueContanier());
			if (VC != null) {
				secName = VC.getValueContanier();
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
		seletedTab =
				(String) this.pageContext.getSession().getAttribute(secName+"MTAB");
				if(seletedTab==null){
					seletedTab = defaultSelectedTab;
				}
		return EVAL_BODY_INCLUDE;
	}
	public int doEndTag() throws JspException{
		//this.pageContext.getSession().removeAttribute(tabname);
		return EVAL_PAGE;		
	}
	/**
	 * @return
	 */
	public String getSeletedTab() {
		return seletedTab;
	}


	/**
	 * @param string
	 */
	public void setSeletedTab(String string) {
		seletedTab = string;
	}
	/**
	 * @return
	 */
	public String getDefaultSelectedTab() {
		return defaultSelectedTab;
	}

	/**
	 * @return
	 */
	public String getTabname() {
		return tabname;
	}

	/**
	 * @param string
	 */
	public void setDefaultSelectedTab(String string) {
		defaultSelectedTab = string;
	}

	/**
	 * @param string
	 */
	public void setTabname(String string) {
		tabname = string;
	}
	public String getScreenName(){
		 return secName;
	}
}
