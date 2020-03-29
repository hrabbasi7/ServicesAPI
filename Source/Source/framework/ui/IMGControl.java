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
public class IMGControl extends BaseBodyControlTag {
	/**
	 * 
	 */
	private String dataFieldName;
	private String passwordField;
	private String size;
	private String label;
	private String listName;
	private String defaultValue;
	private String defaultValueContainerName;
	private String colspan;
	private String color;
	private String image;
	private String src;
	public int doStartTag(){
			JspWriter out = pageContext.getOut();		
			try{
				StringBuffer sBuf = new StringBuffer();
				if(this.name!=null){
					sBuf.append(" <img name="+name);
					if( src!=null){
						sBuf.append(" src="+src);
					}
					if(this.width!=null){
						sBuf.append(" width="+width);
					}
					if(this.height!=null){
						sBuf.append(" height="+height);						
					}
					if(this.border!=null){
						sBuf.append(" border="+border);						
					}
					sBuf.append(">");
				}
				out.print(sBuf.toString());
				return EVAL_BODY_INCLUDE;
			}catch(Exception e){
				e.printStackTrace();
				return SKIP_BODY;
			}
		}
	   public int doEndTag() throws JspException{
		try {
    		  pageContext.getOut().print(" </img>");	
	    	  return EVAL_PAGE;
		} catch (java.io.IOException ex) {
		  throw new JspException(ex.toString());
		}
	   }
	   public void setSize(String value){
		this.size=value;
	   }
	   public void setLabel(String value){
		this.label=value;
	   }
/**
 * Is used to poplate the list box
 * @param value
 */
		public void setListName(String value){
			listName=value;
		}
		/**
		 * Is used to set the defalut value
		 * @param value
		 */
		public void putValue(String value){
			defaultValue = value;
		}
		public void setValueContainer(String value){
			defaultValueContainerName=value;
		}
		public void setColsPan(String value){
			this.colspan =value;
		}
		public void setColor(String value){
			this.color = value;
		}
		public void setImage(String value){
			this.image=value;
		}
		public void setSrc(String value){
			this.src=value;	
		}
}	
