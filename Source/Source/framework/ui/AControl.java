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
import com.i2c.component.framework.taglib.controls.*;

/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AControl extends BaseBodyControlTag {
	/**
	 * 
	 */
	private String href;
	private String title;
	public int doStartTag(){
			JspWriter out = pageContext.getOut();		
			try{
				StringBuffer sBuf = new StringBuffer();

					if( href!=null){
						sBuf.append(" <A ");						
						sBuf.append(" href="+href);
					}
					if(this.onclick!=null){
						sBuf.append(" onClick="+onclick);
					}
					if( title!=null){
						sBuf.append(" title="+title);
					}
					sBuf.append(">");
				out.print(sBuf.toString());
				return EVAL_BODY_INCLUDE;
			}catch(Exception e){
				e.printStackTrace();
				return SKIP_BODY;
			}
		}
	   public int doEndTag() throws JspException{
		try {
    		  pageContext.getOut().print("</A>");	
	    	  return EVAL_PAGE;
		} catch (java.io.IOException ex) {
		  throw new JspException(ex.toString());
		}
	   }
	   public void setTitle(String value){
		this.title=value;
	   }
/**
 * Is used to poplate the list box
 * @param value
 */
		public void setHref(String value){
			this.href=value;	
		}
}	
