/*
 * Created on Dec 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.framework.ui;

import java.util.List;
import java.util.Vector;

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
public class TDControl extends BaseBodyControlTag  {

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
	private String colspan,rowspan;
	private String color;
	private String src;
	public int doStartTag(){
			JspWriter out = pageContext.getOut();		
			try{
				StringBuffer sBuf = new StringBuffer();
				sBuf.append("<td");
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
					sBuf.append(" id=\"" +id+ "\"");
				}
				if(rowspan!=null ){
					sBuf.append(" rowspan=\"" +rowspan+ "\"");
				}			
				if(colspan !=null){
					sBuf.append(" colspan=\"" +colspan + "\"");
				}
				
				sBuf.append(">");
				
				if(dataFieldName!=null) {
					sBuf.append(" <input type='text' name='"+dataFieldName+"'");
					if(size!=null)
					sBuf.append(" size='"+size+"'");
					if(this.defaultValue!=null){
						sBuf.append(" value="+this.getOBJValue(defaultValueContainerName,defaultValue));
					}
					sBuf.append(">");	
				}
				if(listName!=null) {
					//System.out.println(listName);
					sBuf.append(" <Select name='"+listName+"'>");
					
					//sBuf.append("<option value='admin' selected>Administrator</option>");
					//sBuf.append("<option value='guest'>Guest</option>");
					//sBuf.append("<option value='manager'>Manager</option>");
					//sBuf.append("<option value='controller'>Controller</option>");
					String str_selected =(String)this.pageContext.getSession().getAttribute(listName+"_selected");
					if(this.defaultValueContainerName!=null){
						List list=this.getList(defaultValueContainerName);
						//System.out.println(list);
						String data[]=null;
						if(list!=null){
							for (int i=0;i<list.size(); i++) {
								data = (String[])list.get(i);
								sBuf.append("<option value=");
								sBuf.append(data[0]);
								if(str_selected!=null){
									if(data[0].indexOf(str_selected)!=-1){
										this.pageContext.getSession().removeAttribute(listName+"_selected");									
										sBuf.append(" selected");									
									}
									
								}else{
									//System.out.print("  "+data[0] +"=="+str_selected);
									if(i==0) sBuf.append("\"\" selected");
								}
								
								sBuf.append(">");
								sBuf.append(data[1]);
								sBuf.append("</option>");
							}
						}
					}
					sBuf.append("</select>");	
				}
				
				if(passwordField!=null) {
					sBuf.append(" <input type='password' name='"+passwordField+"' value=\"\"");
					if(size!=null)
					sBuf.append(" size='"+size+"'");
					sBuf.append(">");	
				}
				if(label!=null){
					if(color!=null){
						sBuf.append("<font color="+color+">");
						sBuf.append(label);
						sBuf.append("</font>");
					}
					else {
						sBuf.append(label);
					}
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
			JspWriter out = pageContext.getOut();	
			out.println("</td>");
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
		public void setPutValue(String value){
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
		public void setColor(String value){
			this.color = value;
		}
		public void setSrc(String value){
			this.src=value;	
		}
		
}	
