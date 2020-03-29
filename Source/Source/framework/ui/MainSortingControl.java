package com.i2c.component.framework.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.html.FormTag;

import com.i2c.cards.util.*;
import com.i2c.component.framework.taglib.controls.BaseBodyControlTag;
import javax.servlet.jsp.JspWriter;
import com.i2c.component.framework.taglib.controls.*;
import com.i2c.component.util.ComponentConstants;
import com.i2c.util.*;

/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MainSortingControl extends BaseBodyControlTag {
	
	private static String ascorderimg;
	private static String descorderimg;
	
	private static String cascorderimg;
	private static String cdescorderimg;
	
	private static String noorderimg;
	private String href;
	private String defaultsortname;
	private String defaultsortcol;
	private String defaultsortorder="";

	private String pdefaultsortname;
	private String pdefaultsortcol;
	private String pdefaultsortorder="";


	public int doStartTag() {
		String scName=null;
		ValueContainerControl	VC=null;
		try {
				VC = (ValueContainerControl) this.getParent();	
				if(VC!=null){
					scName= VC.getValueContanier();
				}
		}catch(Exception exp){
			exp.toString();
		}
		if(VC!=null) {
			if( this.pageContext.getSession().getAttribute(scName+ComponentConstants.SORTNAME) !=null) {
				defaultsortname = (String) this.pageContext.getSession().getAttribute(scName+ComponentConstants.SORTNAME);
			}else{
				defaultsortname =null;
			}
			if( this.pageContext.getSession().getAttribute(scName+ComponentConstants.SORTCOLUMN) !=null) {
				defaultsortcol = (String) this.pageContext.getSession().getAttribute(scName+ComponentConstants.SORTCOLUMN);
			}else{
				defaultsortcol = null;
			}
			if( this.pageContext.getSession().getAttribute(scName+ComponentConstants.SORTORDER) !=null) {
				defaultsortorder = (String) this.pageContext.getSession().getAttribute(scName+ComponentConstants.SORTORDER);
			}else{
				defaultsortorder = "";
			}
			
			if( this.pageContext.getSession().getAttribute(scName+ComponentConstants.PSORTNAME) !=null) {
				pdefaultsortname = (String) this.pageContext.getSession().getAttribute(scName+ComponentConstants.PSORTNAME);
			}else{
				pdefaultsortname =null;
			}
			if( this.pageContext.getSession().getAttribute(scName+ComponentConstants.PSORTCOLUMN) !=null) {
				pdefaultsortcol = (String) this.pageContext.getSession().getAttribute(scName+ComponentConstants.PSORTCOLUMN);
			}else{
				pdefaultsortcol = null;
			}
			if( this.pageContext.getSession().getAttribute(scName+ComponentConstants.PSORTORDER) !=null) {
				pdefaultsortorder = (String) this.pageContext.getSession().getAttribute(scName+ComponentConstants.PSORTORDER);
			}else{
				pdefaultsortorder = "";
			}
			
		}
		cascorderimg=Constants.getImg("cascorderimg");
	    cdescorderimg=Constants.getImg("cdescorderimg");
	    ascorderimg=Constants.getImg("ascorderimg");
	    descorderimg=Constants.getImg("descorderimg");
	    noorderimg=Constants.getImg("noorderimg");
	    		
		return EVAL_BODY_INCLUDE;		
	}	
	/**
	 * @return
	 */
	public String getDefaultsortcol() {
		return defaultsortcol;
	}

	/**
	 * @return
	 */
	public String getDefaultsortname() {
		return defaultsortname;
	}

	/**
	 * @return
	 */
	public String getDefaultsortorder() {
		return defaultsortorder;
	}

	/**
	 * @param string
	 */
	public void setDefaultsortcol(String string) {
		defaultsortcol = string;
	}

	/**
	 * @param string
	 */
	public void setDefaultsortname(String string) {
		defaultsortname = string;
	}

	/**
	 * @param string
	 */
	public void setDefaultsortorder(String string) {
		defaultsortorder = string;
	}

	/**
	 * @return
	 */
	public String getAscorderimg() {
		return ascorderimg;
	}

	/**
	 * @return
	 */
	public String getDescorderimg() {
		return descorderimg;
	}

	/**
	 * @return
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @return
	 */
	public String getNoorderimg() {
		return noorderimg;
	}

	/**
	 * @param string
	 */
	public void setAscorderimg(String string) {
		ascorderimg = string;
	}

	/**
	 * @param string
	 */
	public void setDescorderimg(String string) {
		descorderimg = string;
	}

	/**
	 * @param string
	 */
	public void setHref(String string) {
		href = string;
	}

	/**
	 * @param string
	 */
	public void setNoorderimg(String string) {
		noorderimg = string;
	}
	public String getOnclick(){
		return this.onclick;
	}

	public void release() {
		ascorderimg = null;
		descorderimg = null;
		noorderimg = null;
		href = null;
		defaultsortname = null;
		defaultsortcol = null;
		defaultsortorder="";
	}

	/**
	 * @return
	 */
	public String getPdefaultsortcol() {
		return pdefaultsortcol;
	}

	/**
	 * @return
	 */
	public String getPdefaultsortname() {
		return pdefaultsortname;
	}

	/**
	 * @return
	 */
	public String getPdefaultsortorder() {
		return pdefaultsortorder;
	}

	/**
	 * @return
	 */
	public String getCascorderimg() {
		return cascorderimg;
	}

	/**
	 * @return
	 */
	public String getCdescorderimg() {
		return cdescorderimg;
	}

	/**
	 * @param string
	 */
	public void setCascorderimg(String string) {
		cascorderimg = string;
	}

	/**
	 * @param string
	 */
	public void setCdescorderimg(String string) {
		cdescorderimg = string;
	}

}
