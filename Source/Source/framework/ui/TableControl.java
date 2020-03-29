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
import com.i2c.component.framework.taglib.controls.BaseBodyControlTag;
import javax.servlet.jsp.JspWriter;
/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TableControl extends  BaseBodyControlTag  {

	/**
	 * 
	 */
	private String cellspacing;
	private String cellpadding;
	private String valueContainer;
	private String hidden=null;
	public TableControl() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int doStartTag(){
		JspWriter out = pageContext.getOut();		
		try{
			StringBuffer sBuf = new StringBuffer();
			if(hidden!=null && hidden.equals("FALSE")) {
				sBuf.append("<table");
				if(style!=null)
				sBuf.append(" class = '"+style+"'");  
				if(width !=null)
				sBuf.append(" width=\"" + width + "\"");
				if(this.border!=null)
				sBuf.append(" border=\"" + border + "\"");
				if( cellspacing!=null ){
					sBuf.append(" cellspacing=\"" + cellspacing + "\"");
				}
				if( cellpadding!=null ){
					sBuf.append(" cellpadding=\"" + cellpadding + "\"");				
				}						
				sBuf.append(">");
				out.print(sBuf.toString());
			}
			return EVAL_BODY_INCLUDE;
		}catch(Exception e){
			e.printStackTrace();
			return SKIP_BODY;
		}
	}
	public int doEndTag() throws JspException{
	 try {
		 JspWriter out = pageContext.getOut();
		    if(this.valueContainer!=null){
				this.pageContext.getSession().removeAttribute(this.valueContainer);		    	
		    }
		if(hidden!=null && hidden.equals("FALSE")) {
			 out.println("</Table>");
		}
		 return EVAL_PAGE;
	 } catch (java.io.IOException ex) {
	   throw new JspException(ex.toString());
	 }
	}
/*
	public int doAfterBody() throws JspException {
		try {
			bodyContent.writeOut(bodyContent.getEnclosingWriter());
		    return SKIP_BODY;
		} catch (java.io.IOException ex) {
		  throw new JspTagException(ex.toString());
		}
	 }
*/
	public void setCellpadding(String value){
		this.cellpadding = value;
	}
	public void setCellspacing(String value){
			this.cellspacing = value;
	}	
	public void setValueContainer(String value){
		valueContainer=value;
	}
	public void setHidden(String value){
		this.hidden=value;
	}
}
