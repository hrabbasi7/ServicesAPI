/*
 * Created on Dec 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.framework.ui;

import javax.servlet.http.HttpSession;
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
import javax.servlet.jsp.JspWriter;
import com.i2c.util.*;


/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ActionMessageControl extends BaseBodyControlTag {

	/**
	 * 
	 */
	public int doStartTag() {
		JspWriter out = pageContext.getOut();
		String secName = null;
		StringBuffer sBuf = new StringBuffer();
		try {
			HttpSession session = this.pageContext.getSession();
			try {
				 ValueContainerControl VC = (ValueContainerControl) this.getParent();
				System.out.println("PARENT: " + VC.getValueContanier());
				if (VC != null) {
					secName = VC.getValueContanier();
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
			if(session!=null){
				 if ( secName!=null ){
				 	out.println("<div align = \"center\" id = \"actionmessage\"> ");
					out.println(Constants.getMessage(session.getAttribute(secName+Constants.CURRENT_ACTION)));
					out.println("</div>");
				}
			}
			out.print(sBuf.toString());
			out.print("<script language = \"javascript\"> removeSpace();</script>");
			return EVAL_BODY_INCLUDE;
		} catch (Exception exp) {
			exp.printStackTrace();
			return SKIP_BODY;
		}
	}
}
