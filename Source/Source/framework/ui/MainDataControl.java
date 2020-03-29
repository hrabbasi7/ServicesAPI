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
import com.i2c.util.*;


/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MainDataControl extends BaseBodyControlTag {

	/**
	 * 
	 */
	private String dataField;
	private String dataFieldName;
	private String defaultValue; // putvalue
	private String defaultValueContainerName; // valuecontainer
	private String size;
	private String rows;
	private String cols;
	private String readonly;
	private String disabled;
	private String maxlength;
	private String value;
	private String selectedIndex;
	private String selected;
	private String selectedvalue;
	private MainGridControl MGC=null;
	private String styleRelated;
	private String onFocus;
	private final String setColor = "setColor(this.name,'black', '"+Constants.CONTROLS_BACKGROUND_COLOR +"',this.form);";
	private String align,background,bgcolor,bordercolor,colspan,height,nowrap,valign,width,rowspan,title;

	private String validateReq;
	private String validateType;
	private String isNullable;
	private String promptLabel;

	public MainDataControl() {
		super();
		
	}
	public int doStartTag() {
				
			try {
				MGC = (MainGridControl) this.getParent();
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		try {
			StringBuffer styleRelated = new StringBuffer("");
			if(this.styleRelated !=null ) {
				styleRelated.append(styleRelated);
			}
			if(align!=null){
				styleRelated.append(" align ='"+align+"'");
			}else{
				styleRelated.append(" align = 'center'");
			}
			if(background!=null){
				styleRelated.append(" background ='"+background+"'");
			}
			if(bgcolor!=null){
				styleRelated.append(" bgcolor ='"+bgcolor+"'");
			}
			if(bordercolor!=null){
				styleRelated.append(" bordercolor ='"+bordercolor+"'");
			}
			if(colspan!=null){
				styleRelated.append(" colspan ='"+colspan+"'");
			}
			if(height!=null){
				styleRelated.append(" height ='"+height+"'");
			}
			if(nowrap!=null){
				styleRelated.append(" "+nowrap);
			}
			if(valign!=null){
				styleRelated.append(" valign ='"+valign+"'");
			}
//			else{
//				styleRelated.append(" valign='middle'");
//			}
			if(width!=null){
				styleRelated.append(" width ='"+width+"'");
			}
			if(rowspan!=null){
				styleRelated.append(" rowspan ='"+rowspan+"'");
			}
			if(title!=null){
				styleRelated.append(" title ='"+title+"'");
			}
			
			StringBuffer endsBuf = new StringBuffer("");
			StringBuffer functionsBuf = new StringBuffer("");
			List list = null;
			if(this.onclick!=null )
			functionsBuf.append(" onClick ="+onclick);				
			if( this.onchange!=null){
				functionsBuf.append(" onChange="+onchange);	
			}
			if( this.onfoucsout!=null){
				functionsBuf.append(" onfoucsout="+onfoucsout);	
			}
			if( this.onkeydown!=null){
				functionsBuf.append(" onkeydown="+onkeydown);
			}
			if(this.onkeypress!=null){
				functionsBuf.append(" onkeypress="+onkeypress);
			}
			if(this.onkeyup!=null){
				functionsBuf.append(" onkeyup="+onkeyup);
			}if(this.onblur!=null){
				functionsBuf.append(" onblur="+onblur);
			}
			if(this.onselect!=null){
					functionsBuf.append(" onselect="+onselect);
			}
			//setting the onFocus event for changing the background of fields
		  	if(this.onFocus != null) {
				if (!this.onFocus.equals("")){	
			  		functionsBuf.append(" onFocus=\""+setColor+this.onFocus+"\" ");
				}
				else {
					functionsBuf.append(" onFocus=''");
				}
		  	}
		  	else {
			  	functionsBuf.append(" onFocus=\""+setColor+"\" ");
		  	}
			
			boolean  en = false;
			boolean  ds = false;
			boolean rdOnly = false;
			boolean dsRead = false;
			boolean enRead = false;
			//--String rd =null ;
			//if( this.defaultValue!=null && this.defaultValue.equals("bank_id")){
			//	System.out.println("bank_id");
			//}
			
				if(ds==true) {
					endsBuf.append(" disabled ");
				}
				else if(this.disabled!=null && disabled.equalsIgnoreCase("true")) {
					endsBuf.append(" disabled ");
				}
				if ((!enRead) && ( rdOnly == true || dsRead== true)){
					endsBuf.append(" READONLY ");
				}
				else if(readonly!=null && readonly.equalsIgnoreCase("true"))
					endsBuf.append(" READONLY ");
			//String [] dataList = new String[5];
			// To Include Put Value Attribute in the List			
			String [] dataList = new String[6];
			dataList[0] = "";
			dataList[1] = "";
			dataList[2] = "";
			dataList[3] = "";
			dataList[4] = "";
			dataList[5] = "";

			

			if( dataField !=null)
				dataField = dataField.toUpperCase();
				
			if (dataField != null && dataField.equalsIgnoreCase("LABEL")) {
				dataList[0] = dataField;
				dataList[1] = "";
				dataList[2] = (this.dataFieldName == null)?"":this.dataFieldName;
				dataList[3] = "";
				dataList[4] = "";
				dataList[5] = (this.defaultValue == null)?"":this.defaultValue;
			}
			else if (dataField != null && dataField.equalsIgnoreCase("RADIO")) {
				
				dataList[0] = dataField;
				
				dataList[1] = (" <input type='RADIO' ");
				if (dataFieldName != null) {
					dataList[2] = this.dataFieldName ;
				}
				dataList[3] = (functionsBuf.toString());
				
				/*
				if (str_selected != null) {
					sBuf.append(" CHECKED "); 
				}else if(this.selected !=null && this.selected.equalsIgnoreCase("TRUE")){
					sBuf.append(" CHECKED "); 
				}
				*/
				
				dataList[3] = dataList[3] + (endsBuf.toString());				
				dataList[4] = (" >");
				dataList[5] = (this.defaultValue == null)?"":this.defaultValue;
			} 

			else if (dataField != null && dataField.equalsIgnoreCase("CHECKBOX")) {
				dataList[0] = dataField;
				dataList[1] = (" <input type='CHECKBOX' ");
				if (dataFieldName != null) {
					dataList[2] = this.dataFieldName ;
				}
				 
				
				dataList[3] = dataList[3] + (functionsBuf.toString());
				/*
				if (defaultValueContainerName != null
					&& this.defaultValue != null) {
				  if(! defaultValue.trim().equals("")) {
					if(! this.getOBJValue(defaultValueContainerName,defaultValue).equals("")){
						sBuf.append(
						"  value='"
							+ this.getOBJValue(
								defaultValueContainerName,
								defaultValue)+"'");
					}else{ sBuf.append(" value='NO'"); }			
				}else{ sBuf.append(" value='NO'"); }
				}
				else if (value!=null){
					sBuf.append(" value='"+value+"'");
				}

				String str_selected =
									(String) this.pageContext.getSession().getAttribute(
										dataFieldName +this.getOBJValue(
				defaultValueContainerName,
				defaultValue)+ "_selected");
				
				if (str_selected != null) {
					sBuf.append(" CHECKED "); 
				}
				*/
				dataList[3] = dataList[3] +(endsBuf.toString());
				dataList[4] = (" >");
				dataList[5] = (this.defaultValue == null)?"":this.defaultValue;
			} 
			else  if (dataField != null && dataField.equalsIgnoreCase("TEXT")) {
				dataList[0] = dataField;
				dataList[1] = (" <input type='text' ");
				if (dataFieldName != null) {
					dataList[2] = this.dataFieldName ;
				}
				if (this.style != null) {
					dataList[3] = dataList[3] +(" class='" + this.style + "'");
				}
				if (this.size != null) {
					dataList[3] = dataList[3] +(" size='" + this.size + "'");
				}
				if (this.maxlength!= null) {
					dataList[3] = dataList[3] +(" maxlength='" + this.maxlength + "'");
				}
				setFieldValidationAttr(false,functionsBuf);
				dataList[3] = dataList[3] +(functionsBuf.toString());
				dataList[3] = dataList[3] +(endsBuf.toString());
				dataList[4] = (" >");
				dataList[5] = (this.defaultValue == null)?"":this.defaultValue;
			}
			else if(dataField != null && dataField.equalsIgnoreCase("HREF")) 
			{
				dataList[0] = dataField;
				dataList[1] = (" <A href="+this.defaultValue); 
//				dataList[2] = "";
				if (dataFieldName != null) {
					dataList[2] = this.dataFieldName ;
				}
				if (this.style != null) {
					dataList[3] = dataList[3] +(" class='" + this.style + "'");
				}
				//dataList[3] = dataList[3] +(functionsBuf.toString());
				dataList[3] = dataList[3] +(endsBuf.toString());
				dataList[4] = (" " + functionsBuf.toString() +" >"+ value +"</a>");
				//System.out.println("\n ********* dataList[3] = " + dataList[3].toString() + "\n");
				dataList[5] = (this.defaultValue == null)?"":this.defaultValue;
			}
			else if (dataField != null && dataField.equalsIgnoreCase("SERIAL")) {
				MGC.setSerailInfo(dataField);
				MGC.setSerailInfoStyleRelated(styleRelated.toString());	
				return EVAL_BODY_INCLUDE;				
			}
			else  if (dataField != null && dataField.equalsIgnoreCase("PASSWORD")) {
				dataList[0] = dataField;
				dataList[1] = (" <input type='password' ");
				if (dataFieldName != null) {
					dataList[2] = this.dataFieldName ;
				}
				if (this.style != null) {
					dataList[3] = dataList[3]  +(" class='" + this.style + "'");
				}
				if (this.size != null) {
					dataList[3] = dataList[3] + (" size='" + size + "'");
				}
				setFieldValidationAttr(false,functionsBuf);
				dataList[3] = dataList[3] + (functionsBuf.toString());
				dataList[3] = dataList[3] + (endsBuf.toString());
				dataList[4] = (" >");
				dataList[5] = (this.defaultValue == null)?"":this.defaultValue;
			}
			
			else  if (dataField != null && dataField.equalsIgnoreCase("HIDDEN")) {
				dataList[0] = dataField; 
				dataList[1] = (" <input type='HIDDEN' ");
				if (dataFieldName != null) {
					dataList[2] = this.dataFieldName ;
				}
				if (this.size != null) {
					dataList[3] = dataList[3] +(" size='" + this.size + "'");
				}
				dataList[3] = dataList[3] +(endsBuf.toString());
				dataList[4] = (" >");
				dataList[5] = (this.defaultValue == null)?"":this.defaultValue;
			}
			
			//<TEXTAREA NAME="others" COLS=48 ROWS=4></TEXTAREA>

			else  if (dataField != null && dataField.equalsIgnoreCase("TEXTAREA")) {
				dataList[0] = dataField;
				dataList[1] = (" <TEXTAREA ");
				if (dataFieldName != null) {
					dataList[2] = this.dataFieldName ;
				}
				if (this.style != null) {
					dataList[3] = dataList[3] +(" class='" + this.style + "'");
				}
				if (this.rows != null) {
					dataList[3] = dataList[3] +(" cols='" + this.cols + "'");
				}
				if (this.rows != null) {
					dataList[3] = dataList[3] +(" rows='" + this.rows + "'");
				}
				setFieldValidationAttr(false,functionsBuf);
				dataList[3] = dataList[3] +(functionsBuf.toString());
				dataList[3] = dataList[3] +(endsBuf.toString());
				dataList[3] = dataList[3] + (" >");
				dataList[4] = ("</TEXTAREA>");
				dataList[5] = (this.defaultValue == null)?"":this.defaultValue;
			}
			 else if (
				dataField != null && dataField.equalsIgnoreCase("OPTION")) {
				dataList[0] = dataField;					
				dataList[1] = (" <select ");
				if (dataFieldName != null) {
					dataList[2] = this.dataFieldName ;
				}

				dataList[3] =dataList[3] +(endsBuf.toString());					
				if (this.style != null) {
					dataList[3] =dataList[3] +(" class='" + this.style + "' ");
				}
				setFieldValidationAttr(true,functionsBuf);
				dataList[3] =dataList[3] +(functionsBuf.toString());
				dataList[3] =dataList[3] +(" > ");
				
				if (this.defaultValueContainerName != null) {
					 list = this.getList(defaultValueContainerName);
				}
				dataList[4] =(" </select> ");
				dataList[5] = (this.defaultValue == null)?"":this.defaultValue;
				
				
			}
			
			if(list!=null || this.selectedvalue!=null) {
				if(list!=null)
					MGC.setTypeList(dataList, list);
				if( this.selectedvalue!=null) {
					//System.out.println("SELECTED VALUE in the combo :" +selectedvalue);					
					MGC.setTypeList(dataList, selectedvalue);
				}
			}
			else {
				MGC.setTypeList(dataList);
			}
			if(styleRelated.length()>0){
				// -- //
				MGC.setStyleRelated(styleRelated.toString()); 
			}
			//System.out.println(sBuf.toString());
			//
			return EVAL_BODY_INCLUDE;
		} catch (Exception e) {
			e.printStackTrace();
			return SKIP_BODY;
		}
	}

	public void setdataFieldName(String value) {
		this.dataFieldName = value;
	}

	/**
	 * Is used to populate the list box
	 * @param value
	 */
	public void setdataField(String value) {
		dataField = value;
	}
	/**
	 * Is used to set the defalut value
	 * @param value
	 */
	public void setPutValue(String value) {
		defaultValue = value;
	}
	public void setValueContainer(String value) {
		defaultValueContainerName = value;
	}
	public void setSize(String value) {
		this.size = value;
	}
	/**
	 * @param string
	 */
	public void setCols(String string) {
		cols = string;
	}

	/**
	 * @param string
	 */
	public void setRows(String string) {
		rows = string;
	}
	/**
	 * @param string
	 */
	public void setValue(String string) {
		value = string;
	}

	/**
	 * @param string
	 */
	public void setSelectedIndex(String string) {
		selectedIndex = string;
	}

	/**
	 * @param string
	 */
	public void setSelected(String string) {
		selected = string;
		
	}
	/**
	 * @param string
	 */
	public void setReadonly(String string) {
		readonly = string;
	}

	/**
	 * @param string
	 */
	public void setMaxlength(String string) {
		maxlength = string;
	}

	/**
	 * @return
	 */
	public String getSelectedvalue() {
		return selectedvalue;
	}

	/**
	 * @param string
	 */
	public void setSelectedvalue(String string) {
		//System.out.println("SELECTED VALUE :" +string);
		selectedvalue = string;
	}

	/**
	 * @return
	 */
	public String getStyleRelated() {
		return styleRelated;
	}

	/**
	 * @param string
	 */
	public void setStyleRelated(String string) {
		styleRelated = string;
	}

	/**
	 * @return
	 */
	public String setColspan() {
		return colspan;
	}

	/**
	 * @return
	 */
	public String setSize() {
		return size;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param string
	 */
	public void setAlign(String string) {
		align = string;
	}

	/**
	 * @param string
	 */
	public void setBackground(String string) {
		background = string;
	}

	/**
	 * @param string
	 */
	public void setBgcolor(String string) {
		bgcolor = string;
	}

	/**
	 * @param string
	 */
	public void setBordercolor(String string) {
		bordercolor = string;
	}

	/**
	 * @param string
	 */
	public void setColspan(String string) {
		colspan = string;
	}

	/**
	 * @param string
	 */

	/**
	 * @param string
	 */
	public void setNowrap(String string) {
		nowrap = string;
	}

	/**
	 * @param string
	 */
	public void setRowspan(String string) {
		rowspan = string;
	}

	/**
	 * @param string
	 */
	public void setTitle(String string) {
		title = string;
	}

	/**
	 * @param string
	 */
	public void setValign(String string) {
		valign = string;
	}

	/**
	 * @param string
	 */

	/**
	 * @return
	 */
	public String getOnFocus() {
		return onFocus;
	}

	/**
	 * @param string
	 */
	public void setOnFocus(String string) {
		onFocus = string;
	}

	void setFieldValidationAttr(boolean bolIfCombo, StringBuffer strbAttr){
		/*Changes made by Danish for Field Validation*/
		if (!bolIfCombo){
			if (this.validateReq != null){
				strbAttr.append(" validateReq=\""+validateReq+"\" ");
			}
			else {
				strbAttr.append(" validateReq='Y'");
			}
			if (this.validateType != null){
				strbAttr.append(" validateType=\""+validateType+"\" ");
			}
			else {
				strbAttr.append(" validateType='Alphanumeric'");
			}
		}	
		if (this.promptLabel != null){
			strbAttr.append(" promptLabel=\""+promptLabel+"\" ");
		}
		else {
			strbAttr.append(" promptLabel=''");
		}
		if (this.isNullable != null){
			strbAttr.append(" isNullable=\""+isNullable+"\" ");
		}
		else {
			strbAttr.append(" isNullable='Y'");
		}

		/*Changes made by Danish for Field Validation*/			
	}
	/**
	 * @return
	 */
	public String getIsNullable() {
		return isNullable;
	}

	/**
	 * @return
	 */
	public String getPromptLabel() {
		return promptLabel;
	}

	/**
	 * @return
	 */
	public String getValidateReq() {
		return validateReq;
	}

	/**
	 * @return
	 */
	public String getValidateType() {
		return validateType;
	}

	/**
	 * @param string
	 */
	public void setIsNullable(String string) {
		isNullable = string;
	}

	/**
	 * @param string
	 */
	public void setPromptLabel(String string) {
		promptLabel = string;
	}

	/**
	 * @param string
	 */
	public void setValidateReq(String string) {
		validateReq = string;
	}

	/**
	 * @param string
	 */
	public void setValidateType(String string) {
		validateType = string;
	}

}