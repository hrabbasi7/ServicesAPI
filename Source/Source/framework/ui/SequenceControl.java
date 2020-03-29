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

import com.i2c.cards.util.Constants;
import com.i2c.component.framework.taglib.controls.BaseBodyControlTag;
import com.i2c.component.util.ComponentConstants;

import javax.servlet.jsp.JspWriter;
/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SequenceControl extends BaseBodyControlTag {

	/**
	 * 
	 */
	private String total;
	private String current;
	private String to;
	private String toin;
	private String in;
	private String scName;
	private String location;
	
	public int doStartTag() {
		JspWriter out = pageContext.getOut();
		StringBuffer sBuf = new StringBuffer();
			try {
				ValueContainerControl VC = (ValueContainerControl) this.getParent();
				//System.out.println("PARENT: " + VC.getValueContanier());
				if (VC != null) {
					scName = VC.getValueContanier();
					total = scName+total;
					current = scName+current;
					to = scName+to;
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}

		try {
			HttpSession session = this.pageContext.getSession();
			if(session!=null){
				
				String cssClass = "fieldLabel";
				if(location != null && location.trim().equals(ComponentConstants.TABS_RECORD_COUNTER)){
				cssClass = "fieldLabel_White";
				}
			
				sBuf.append("<div align=\"right\" valign = \"top\" class=\""+cssClass+"\" id = \"sequence\">\n");
				if(session.getAttribute(scName+Constants.NVENABLE)!=null &&session.getAttribute(scName+Constants.NVENABLE).equals(Constants.YES)) { 
				if (total != null && current != null) {
				if (session.getAttribute(total) != null) {
					if (!(((String) session.getAttribute(total))
						.trim()
						.equals("0"))) {
						if (session.getAttribute(current) != null) {
							sBuf.append(session.getAttribute(current));

							if (session.getAttribute(to) != null) {
								if(toin!=null){
									sBuf.append(toin);
								}
								sBuf.append(session.getAttribute(to));
							}							
							if(in!=null){
								sBuf.append(in);
							}
							if (session.getAttribute(total) != null) {
								sBuf.append(session.getAttribute(total));
							}
						}
					}
				}
			}
			}
			}
			sBuf.append("</div>");
			out.print(sBuf.toString());
			return EVAL_BODY_INCLUDE;
		} catch (Exception exp) {
			exp.printStackTrace();
			return SKIP_BODY;

		}
	}
	public void setTotal(String value) {
		total = value;
	}
	public void setCurrent(String value) {
		this.current = value;
	}
	public void setIn(String value){
		this.in= value;
	}
	/**
	 * @param string
	 */
	public void setTo(String string) {
		to = string;
	}

	/**
	 * @param string
	 */
	public void setToin(String string) {
		toin = string;
	}

	/**
	 * @param string
	 */
	public void setLocation(String string) {
		location = string;
	}

}
