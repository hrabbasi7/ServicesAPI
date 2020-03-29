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
public class TRControl extends BaseBodyControlTag  {

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
	private String rowspan;
	private String colspan;
	public int doStartTag(){
			JspWriter out = pageContext.getOut();		
			try{
				StringBuffer sBuf = new StringBuffer();
				sBuf.append(" <tr");
				if(style!=null)
				sBuf.append(" class = '"+style+"'");  
				if(width !=null)
				sBuf.append(" width=\"" + width + "\"");
				if(height!=null)
				sBuf.append(" height=\"" +height + "\"");
				if(name !=null){
					sBuf.append(" name=\"" +name + "\"");
				}
				if(id !=null){
					sBuf.append(" id=\"" +id + "\"");				
				}
				if(rowspan!=null ){
					sBuf.append(" rowspan=\"" +rowspan+ "\"");
				}			
				if(colspan !=null){
					sBuf.append(" colspan=\"" +colspan + "\"");
				}
				
				if(onmouseover!=null){
					sBuf.append(" onMouseOver='high(this);'");	
				}
				if(onmouseout!=null){
					sBuf.append(" onMouseOut='low(this);'");	
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
			 JspWriter out = pageContext.getOut();	
			 out.println("</tr>");
			 return EVAL_PAGE;
		 } catch (java.io.IOException ex) {
		   throw new JspException(ex.toString());
		 }
		}
		 
	   public void setdataFieldName(String value){
	   		this.dataFieldName = value;
	   }
	   
	   public void setPasswordFieldName(String value){
		 this.passwordField = value;
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
		public void setRowspan(String value){
			rowspan=value;				
		}
		public void setColspan(String value){
			this.colspan =value;
		}
		
}	
	
	
