/*
 * Created on Dec 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.framework.ui;

import java.util.HashMap;
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
import com.i2c.component.util.*;
import com.i2c.cards.util.*;
import com.i2c.component.framework.taglib.controls.BaseBodyControlTag;
import javax.servlet.jsp.JspWriter;
import com.i2c.component.framework.taglib.controls.*;
import com.i2c.util.*;
import java.util.regex.*;

/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DataControl extends BaseBodyControlTag {

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
	private String maxlength;
	private String value;
	private String selectedIndex;
	private String selected;
	private String title;
	private ValueContainerControl VC=null;
	private String selectedvalue;
	private String multiple;
	private final String setColor = "setColor(this.name,'black', '"+Constants.CONTROLS_BACKGROUND_COLOR +"',this.form);";
	private String onFocus;
	/**
	* Type can be C (character value) D (Date value) N(Numeric value) and E ( EMAIL ) see Doc for detail
	*/
	private String type;// Two decesion Formatting and frontend validation
	private String typeprompt;
	private String required; //Y for
	private String tabIndex;

	private String applydisplayformat = null;
	private String validateReq = null;
	private String validateType = null;
	private String isNullable = null;
	private String promptLabel = null;
	//private String scale = null;
	//private String precision = null;
	private String leftDigits = null;
	private String rightDigits = null;
	//Saqib Bukhari -- Monday, 06 th Feb. 2006
	private String textCase;

	private String valign;

	public DataControl() {
		super();

	}
	public int doStartTag() {

		boolean chk=false;
		boolean sAction=false;
		boolean  en = false;
		boolean  ds = false;
		boolean rdOnly = false;
		boolean dsRead = false;
		boolean enRead = false;
		boolean inAction=false;

		JspWriter out = pageContext.getOut();
		String secName=null;
//		type = "N";
//		this.required="Y";
		String str_value = null;
			try {
				VC = (ValueContainerControl) this.getParent();
				if(VC!=null){
					if(		this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.CURRENT_ACTION)!=null
					    && 	this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.CURRENT_ACTION).equals(Constants.SEARCH)
					  ){
						//this.maxlength=null;
						//sAction=true;
					}
					secName = VC.getValueContanier();

				//}
				//if(VC!=null){
				if(		this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.CURRENT_ACTION)!=null
					&& 	this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.CURRENT_ACTION).equals(Constants.INSERT)
					|| 	this.pageContext.getSession().getAttribute(VC.getValueContanier()+ComponentConstants.PRVIOUS_ACTION)!=null
					&& 	this.pageContext.getSession().getAttribute(VC.getValueContanier()+ComponentConstants.PRVIOUS_ACTION).equals(Constants.INSERT)
					|| 	this.pageContext.getSession().getAttribute(VC.getValueContanier()+ComponentConstants.PRVIOUS_ACTION)!=null
					&& 	this.pageContext.getSession().getAttribute(VC.getValueContanier()+ComponentConstants.PRVIOUS_ACTION).equals(Constants.COPY)
					|| 	this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.CURRENT_ACTION)!=null
					&& 	this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.CURRENT_ACTION).equals(Constants.COPY)
				  ){

						str_value = (String) this.pageContext.getSession().getAttribute(dataFieldName + "_SELVALUE");
						sAction=true;
						inAction=true;
					}

					/*
					 if(!inAction) {
						if( this.pageContext.getSession().getAttribute(VC.getValueContanier()+ComponentConstants.PRVIOUS_ACTION)!=null
						&& this.pageContext.getSession().getAttribute(VC.getValueContanier()+ComponentConstants.PRVIOUS_ACTION).equals(Constants.INSERT)
						|| this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.CURRENT_ACTION)!=null
						&& this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.CURRENT_ACTION).equals(Constants.COMMIT) ){
							sAction=false;
						}
					}
					*/

					if( this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.CURRENT_ACTION)==null
					//||  this.pageContext.getSession().getAttribute(VC.getValueContanier()+ComponentConstants.PRVIOUS_ACTION)==null
					){
							ds=true;
					}
				}
				//System.out.println("PARENT: " + this.getParent());
				//System.out.println("PARENT: " + VC.getValueContanier());
				if( 	this.pageContext.getSession().getAttribute(secName+Constants.ERRORVALUE)!=null
					&& 	this.pageContext.getSession().getAttribute(secName+Constants.ERRORVALUE).equals(Constants.YES)
				  ){

					if( dataField!=null && (! dataField.equalsIgnoreCase("OPTION")) ){

					defaultValueContainerName = secName+Constants.ININFO;
					defaultValue = this.dataFieldName;
					}
					/*
					else if( dataField!=null && ( dataField.equalsIgnoreCase("OPTION")) )
					{

					}
					*/
				}
				else if( dataField!=null && (! dataField.equalsIgnoreCase("OPTION")) )
				{
					defaultValueContainerName = null;
				}


				if(defaultValueContainerName==null) {
					if (VC != null) {
						defaultValueContainerName = VC.getValueContanier();
					}
				}

//				if( this.pageContext.getSession().getAttribute(secName+Constants.ERRORVALUE)!=null && this.pageContext.getSession().getAttribute(secName+Constants.ERRORVALUE).equals(Constants.YES) ){
//					defaultValueContainerName = secName+Constants.ININFO;
//					defaultValue = this.dataFieldName;
//				}

				//if(inAction) {
				if(! (dataField!=null && dataField.equalsIgnoreCase("HIDDEN")) ) {
					//System.out.println("\n*********************1************************************\n");
				Hashtable typeTable = new Hashtable(3);
				if(this.required!=null){
					typeTable.put("R",this.required);
				}
				if(this.type!=null) {
					typeTable.put("T",this.type);
					if(this.typeprompt !=null){
						typeTable.put("P","\""+this.typeprompt+"\"");
					}

				}
				if(typeTable.size()>0)
				VC.putType(this.dataFieldName,typeTable);
				}
				//}
				/*
				else{
					VC.getTypeTable().remove(this.dataFieldName);
				}
				*/
			} catch (Exception exp) {
				exp.printStackTrace();
			}


		try
		{
			StringBuffer sBuf = new StringBuffer();
			StringBuffer endsBuf = new StringBuffer("");
			StringBuffer functionsBuf = new StringBuffer("");

			if(this.onclick!=null )
			functionsBuf.append(" onClick ="+onclick);

			if(secName!=null && this.pageContext.getSession().getAttribute(secName+Constants.TOTALREC)!=null ){
				if(Integer.parseInt((String)this.pageContext.getSession().getAttribute(secName+Constants.TOTALREC))>0) {
					if(!sAction){
						// For On Key Chnage
						if( this.onchange == null ){
							this.onchange = "'setUpdateMode(\""+secName+"\","+ VC.getFormName()+",\""+ VC.getActionName()+"\",\"upd\");'  ";
						}else{
							if(onchange.endsWith(";") ) {
							    	this.onchange ="'"+ onchange + "  setUpdateMode(\""+secName+"\","+ VC.getFormName()+",\""+ VC.getActionName()+"\",\"upd\"); ' ";
							    }
						   	    else{
						   	    	this.onchange ="'"+ onchange + "  ; setUpdateMode(\""+secName+"\","+ VC.getFormName()+",\""+ VC.getActionName()+"\",\"upd\"); ' ";
						   	    }
						}
					}
				}else{
					if(!sAction)
					ds=true;
			}
			}

			// Handling onkeyup for max. lengtrh attribute
			// At components level
			addOnKeyUpAttribute();

			if( this.valign!=null){
				functionsBuf.append(" valign="+valign);
			}
			if( this.onchange!=null){
				functionsBuf.append(" onChange="+onchange);
			}
			onchange=null;
			if( this.onfoucsout!=null){
				functionsBuf.append(" onfocucsout="+onfoucsout);
			}
			if( this.onkeydown!=null){
				functionsBuf.append(" onkeydown="+onkeydown);
			}
			if(this.onkeypress!=null){
				functionsBuf.append(" onkeypress="+onkeypress);
			}
			if(this.onkeyup!=null){
				functionsBuf.append(" onkeyup="+onkeyup);
			}
			onkeyup = null;
			//Saqib Bukhari -- Monday, 06 th Feb. 2006
			if(this.onblur!=null){
				String tempBlur = null;
				if(onblur.substring(0,0).equals("\"") || onblur.substring(0,0).equals("'"))
					onblur = onblur.substring(1);
				if(onblur.substring(onblur.length()).equals("\"") || onblur.substring(onblur.length()).equals("'"))
					tempBlur = onblur.substring(0,onblur.length());
				else
					tempBlur = onblur;
				if(textCase!=null){
					if(textCase.equalsIgnoreCase(CardsConstants.UPPER_CASE)){
						functionsBuf.append(" onblur=\"changePKCase('"+CardsConstants.UPPER_CASE+"',this);"+tempBlur+"\"");
					}else if(textCase.equalsIgnoreCase(CardsConstants.LOWER_CASE)){
						functionsBuf.append(" onblur=\"changePKCase('"+CardsConstants.LOWER_CASE+"',this);"+tempBlur+"\"");
					}
				}else{
					functionsBuf.append(" onblur=\""+tempBlur+"\"");
				}
			}else{
				if(textCase!=null){
					if(textCase.equalsIgnoreCase(CardsConstants.UPPER_CASE)){
						functionsBuf.append(" onblur=\"changePKCase('"+CardsConstants.UPPER_CASE+"',this);\"");
					}else if(textCase.equalsIgnoreCase(CardsConstants.LOWER_CASE)){
						functionsBuf.append(" onblur=\"changePKCase('"+CardsConstants.LOWER_CASE+"',this);\"");
					}
				}
			}
			if(this.onselect!=null){
				functionsBuf.append(" onselect="+onselect);
			}
			//setting the onFocus event for changing the background of fields
			if(this.onFocus != null) {
				functionsBuf.append(" onFocus=\""+setColor+this.onFocus+"\" ");
			}
			else {
				functionsBuf.append(" onFocus=\""+setColor+"\" ");
			}
			//--String rd =null ;
			//if( this.defaultValue!=null && this.defaultValue.equals("bank_id")){
			//	System.out.println("bank_id");
			//}
			if(VC!=null){
				if(VC.enableAll() ){
					en = true;
				}else if(VC.disableAll()){
					ds=true;
				}
				if(VC.checkDisable(dataFieldName)){
					ds=true;
				}else if(VC.checkEnable(dataFieldName)){
					en=true;
				}

//          ReadOnly does not work properly	canged with !(en) @ Date 11-OCT-2004.
//				if( VC.readOnlyAll()){
//					rdOnly=true;
//				}

				if( !(en) && VC.readOnlyAll()){
					rdOnly=true;
				}

				if (VC.checkReadDisAllowed(dataFieldName)) {
					dsRead=true;
				}
//				if(dataFieldName.equals("city")){
//					System.out.println("City");
//				}
				if (VC.checkReadAllowed(dataFieldName)){
					//System.out.println("ReadAllowed");
					rdOnly=false;
					enRead=true;
					dsRead=false;
				}
			}

				if(ds==true && !en) {
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

				if( this.title !=null ){
					endsBuf.append( "title ='" +title+"'" );
				}
				if(this.tabIndex!=null){
					endsBuf.append(" tabIndex='"+tabIndex+"' ");
				}
			 if(this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.RECNO)!=null){


					sBuf.append(" <input type='HIDDEN' ");
					sBuf.append(" name='" +Constants.RECNO+ "'");
					sBuf.append(" value='" + this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.RECNO) +"' >");
					this.pageContext.getSession().removeAttribute(VC.getValueContanier()+Constants.RECNO);

			 }

			if(this.type!=null && applydisplayformat!=null)  {
				if( (this.type.equalsIgnoreCase(Constants.DATE_FIELD)
				|| this.type.equalsIgnoreCase(Constants.DATETIME_FIELD)
				|| this.type.equalsIgnoreCase(Constants.NUMBER_FIELD)
				|| this.type.equalsIgnoreCase(Constants.TIME_FIELD)
				|| this.type.equalsIgnoreCase(Constants.DECIMAL_FIELD)
				) && applydisplayformat.equalsIgnoreCase(Constants.YES) ) {
					setFomatedValue(defaultValueContainerName ,defaultValue,this.type);

					VC.setFormatBuf("{("+this.dataFieldName+"="+this.type+")}");

				}

			}

			if (dataField != null && dataField.equalsIgnoreCase("LABEL"))
			{
				if (defaultValueContainerName != null
					&& this.defaultValue != null) {
					sBuf.append(this.getOBJValue(
								defaultValueContainerName,
								defaultValue));
				}
			}

			else if (dataField != null && dataField.equalsIgnoreCase("RADIO"))
			{
				if(this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)!=null
					&& ((String)this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)).trim().equalsIgnoreCase(ComponentConstants.HIGHLIGHT_CONTROL) )
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (REQUIRED) *******************\n");
					sBuf.append("<table border='0' cellspacing='0' cellpadding='0'><tr><td>");
					//"<input type='text'>");
					this.generateRadioButtonCode(sBuf,endsBuf, functionsBuf, chk);
					String errorCount = (String) this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL_COUNTER);
					sBuf.append("</td><td><img src='"+Constants.IMGLOCATION+Constants.WARNING_IMAGE_SMALL+"'></td><td>&nbsp;"+ errorCount +"</td></tr></table>");
				}
				else
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (NOT REQUIRED) *******************\n");
					this.generateRadioButtonCode(sBuf,endsBuf, functionsBuf, chk);
				}
			}

			else if (dataField != null && dataField.equalsIgnoreCase("CHECKBOX"))
			{
				if(this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)!=null
					&& ((String)this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)).trim().equalsIgnoreCase(ComponentConstants.HIGHLIGHT_CONTROL) )
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (REQUIRED) *******************\n");
					sBuf.append("<table border='0' cellspacing='0' cellpadding='0'><tr><td>");
					//"<input type='text'>");
					this.generateCheckBoxCode(sBuf,endsBuf, functionsBuf, chk);
					String errorCount = (String) this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL_COUNTER);
					sBuf.append("</td><td><img src='"+Constants.IMGLOCATION+Constants.WARNING_IMAGE_SMALL+"'></td><td>&nbsp;"+ errorCount +"</td></tr></table>");
				}
				else
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (NOT REQUIRED) *******************\n");
					this.generateCheckBoxCode(sBuf,endsBuf, functionsBuf, chk);
				}
			}

			else  if (dataField != null && dataField.equalsIgnoreCase("TEXT"))
			{
				if(this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)!=null
					&& ((String)this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)).trim().equalsIgnoreCase(ComponentConstants.HIGHLIGHT_CONTROL) )
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (REQUIRED) *******************\n");
					sBuf.append("<table border='0' cellspacing='0' cellpadding='0'><tr><td>");
					//"<input type='text'>");
					this.generateTextFieldCode(sBuf,endsBuf, functionsBuf);
					String errorCount = (String) this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL_COUNTER);
					sBuf.append("</td><td><img src='"+Constants.IMGLOCATION+Constants.WARNING_IMAGE_SMALL+"'></td><td>&nbsp;"+ errorCount +"</td></tr></table>");
				}
				else
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (NOT REQUIRED) *******************\n");
					this.generateTextFieldCode(sBuf,endsBuf, functionsBuf);
				}
			}

			else  if (dataField != null && dataField.equalsIgnoreCase("PASSWORD"))
			{
				if(this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)!=null
					&& ((String)this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)).trim().equalsIgnoreCase(ComponentConstants.HIGHLIGHT_CONTROL) )
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (REQUIRED) *******************\n");
					sBuf.append("<table border='0' cellspacing='0' cellpadding='0'><tr><td>");
					//"<input type='password'>");
					this.generatePasswordFieldCode(sBuf, endsBuf, functionsBuf);
					String errorCount = (String) this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL_COUNTER);
					sBuf.append("</td><td><img src='"+Constants.IMGLOCATION+Constants.WARNING_IMAGE_SMALL+"'></td><td>&nbsp;"+ errorCount +"</td></tr></table>");
				}
				else
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (NOT REQUIRED) *******************\n");
					this.generatePasswordFieldCode(sBuf, endsBuf, functionsBuf);
				}
			}

			else  if (dataField != null && dataField.equalsIgnoreCase("BUTTON"))
			{
				//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (NOT REQUIRED) *******************\n");
				this.generateButtonCode(sBuf, endsBuf, functionsBuf);
			}
			else  if (dataField != null && dataField.equalsIgnoreCase("RESET"))
			{
				this.generateResetCode(sBuf, endsBuf, functionsBuf);
			}
			else  if (dataField != null && dataField.equalsIgnoreCase("PASSWORD"))
			{
				this.generateSubmitCode(sBuf, endsBuf, functionsBuf);
			}

			else  if (dataField != null && dataField.equalsIgnoreCase("HIDDEN")) {

				sBuf.append(" <input type='HIDDEN' ");
				if (dataFieldName != null) {
					sBuf.append(" name='" + this.dataFieldName + "'");
				}
				if (this.size != null) {
					sBuf.append(" size='" + this.size + "'");
				}
				if (defaultValueContainerName != null
					&& this.defaultValue != null) {
					sBuf.append(
						" value=\""
							+ this.getOBJValue(
								defaultValueContainerName,
								defaultValue)+"\"");

				}
				else if (value!=null){
					sBuf.append(" value=\""+ super.handleSpecialCharacters(value) +"\"");
				}
				sBuf.append(endsBuf.toString());
				sBuf.append(" >");
			}

			//<TEXTAREA NAME="others" COLS=48 ROWS=4></TEXTAREA>

			else  if (dataField != null && dataField.equalsIgnoreCase("TEXTAREA"))
			{
				if(this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)!=null
					&& ((String)this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)).trim().equalsIgnoreCase(ComponentConstants.HIGHLIGHT_CONTROL) )
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (REQUIRED) *******************\n");
					sBuf.append("<table border='0' cellspacing='0' cellpadding='0'><tr><td>");
					//"<input type='password'>");
					this.generateTextAreaCode(sBuf, endsBuf, functionsBuf);
					String errorCount = (String) this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL_COUNTER);
					sBuf.append("</td><td><img src='"+Constants.IMGLOCATION+Constants.WARNING_IMAGE_SMALL+"'></td><td>&nbsp;"+ errorCount +"</td></tr></table>");
				}
				else
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (NOT REQUIRED) *******************\n");
					this.generateTextAreaCode(sBuf, endsBuf, functionsBuf);
				}
			}

			else if (dataField != null && dataField.equalsIgnoreCase("OPTION"))
			{
				if(this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)!=null
					&& ((String)this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL)).trim().equalsIgnoreCase(ComponentConstants.HIGHLIGHT_CONTROL) )
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (REQUIRED) *******************\n");
					sBuf.append("<table border='0' cellspacing='0' cellpadding='0'><tr><td>");
					//"<input type='password'>");
					this.generateSelectCode(sBuf, endsBuf, functionsBuf, secName, str_value);
					String errorCount = (String) this.pageContext.getRequest().getAttribute(this.dataFieldName+ComponentConstants.HIGHLIGHT_CONTROL_COUNTER);
					sBuf.append("</td><td><img src='"+Constants.IMGLOCATION+Constants.WARNING_IMAGE_SMALL+"'></td><td>&nbsp;"+ errorCount +"</td></tr></table>");
				}
				else
				{
					//System.out.println("\n****************** ComponentConstants.HIGHLIGHT_CONTROL (NOT REQUIRED) *******************\n");
					this.generateSelectCode(sBuf, endsBuf, functionsBuf, secName, str_value);
				}
			}


			//System.out.println("\n\n *****************  this.validateType = " +this.validateType + "\n\n");
			String screenName = (String) this.pageContext.getSession().getAttribute(ComponentConstants.VALUE_CONTAINER_NAME);
			//Code added by Edwin for implementing Server side validations.
			if(this.pageContext.getSession().getAttribute(screenName+ComponentConstants.VALIDATION_PROPERTIES)!=null)
			{
				HashMap fieldsValidationHashMap = (HashMap) this.pageContext.getSession().getAttribute(screenName+ComponentConstants.VALIDATION_PROPERTIES);
				if(fieldsValidationHashMap!=null
					//&& this.validateReq!=null && this.validateReq.trim().equalsIgnoreCase("Y")
					&& this.validateType !=null && this.validateType.trim().length()>0 &&
					this.promptLabel !=null && this.promptLabel.trim().length()>0
					&& !fieldsValidationHashMap.containsKey(this.dataFieldName))
				{
					//Entry for current dataFieldName does NOT exist in fieldsValidationHashMap. Enter it here.
					HashMap validationPropertiesHashMap = new HashMap();

					/*
					System.out.println("\n\nDV validateRequired = "+this.validateReq+" validateType = "+this.validateType);
					System.out.println(" isNullable  = "+this.isNullable +" promptLabel = "+this.promptLabel);
					System.out.println(" applydisplayformat = "+this.applydisplayformat+" leftDigits = "+this.leftDigits);
					System.out.println(" rightDigits = "+this.rightDigits+" maxlength = "+this.maxlength);
					*/

					validationPropertiesHashMap.put(ComponentConstants.VALIDATE_REQUIRED, this.validateReq);
					validationPropertiesHashMap.put(ComponentConstants.VALIDATE_TYPE, this.validateType);
					validationPropertiesHashMap.put(ComponentConstants.IS_NULLABLE, this.isNullable);
					validationPropertiesHashMap.put(ComponentConstants.PROMPT_LABEL, this.promptLabel);
					validationPropertiesHashMap.put(ComponentConstants.DISPLAY_FORMAT, this.applydisplayformat);
					validationPropertiesHashMap.put(ComponentConstants.LEFT_DIGITS, this.leftDigits);
					validationPropertiesHashMap.put(ComponentConstants.RIGHT_DIGITS, this.rightDigits);
					validationPropertiesHashMap.put(ComponentConstants.MAX_LENGTH, this.maxlength);

					fieldsValidationHashMap.put(this.dataFieldName, validationPropertiesHashMap);

					//System.out.println("\n DV - ANOTHER COLUMN ENTERED *******************************\n");
				}
			}
			else
			{
				//if(this.validateReq!=null && this.validateReq.trim().equalsIgnoreCase("Y"))
				if( this.validateType !=null && this.validateType.trim().length()>0 &&
					this.promptLabel !=null && this.promptLabel.trim().length()>0 )
				{
					//Put fieldsValidationHashMap in Session against key ComponentConstants.VALUE_CONTAINER_NAME+ComponentConstants.VALIDATION_PROPERTIES after inserting one key-value pair (dataFieldName-ValidationPropertiesHashMap) of current field in it.
					HashMap fieldsValidationHashMap = new HashMap();
					HashMap validationPropertiesHashMap = new HashMap();

					/*
					System.out.println("\n\nDV validateRequired = "+this.validateReq+" validateType = "+this.validateType);
					System.out.println(" isNullable  = "+this.isNullable +" promptLabel = "+this.promptLabel);
					System.out.println(" applydisplayformat = "+this.applydisplayformat+" leftDigits = "+this.leftDigits);
					System.out.println(" rightDigits = "+this.rightDigits+" maxlength = "+this.maxlength);
					*/

					validationPropertiesHashMap.put(ComponentConstants.VALIDATE_REQUIRED, this.validateReq);
					validationPropertiesHashMap.put(ComponentConstants.VALIDATE_TYPE, this.validateType);
					validationPropertiesHashMap.put(ComponentConstants.IS_NULLABLE, this.isNullable);
					validationPropertiesHashMap.put(ComponentConstants.PROMPT_LABEL, this.promptLabel);
					validationPropertiesHashMap.put(ComponentConstants.DISPLAY_FORMAT, this.applydisplayformat);
					validationPropertiesHashMap.put(ComponentConstants.LEFT_DIGITS, this.leftDigits);
					validationPropertiesHashMap.put(ComponentConstants.RIGHT_DIGITS, this.rightDigits);
					validationPropertiesHashMap.put(ComponentConstants.MAX_LENGTH, this.maxlength);

					fieldsValidationHashMap.put(this.dataFieldName, validationPropertiesHashMap);
					//screenName = (String) this.pageContext.getSession().getAttribute(ComponentConstants.VALUE_CONTAINER_NAME);
					this.pageContext.getSession().setAttribute(screenName+ComponentConstants.VALIDATION_PROPERTIES,fieldsValidationHashMap);

					//System.out.println("\n DV - FIRST COLUMN ENTERED *******************************\n");
				}
			}//Code by Edwin ends.


			out.print(sBuf.toString());
			return EVAL_BODY_INCLUDE;
		} catch (Exception e) {
			e.printStackTrace();
			return SKIP_BODY;
		}
	}

	/**
	 * Adds onkeyup attribute in Text..Passwprd...Textarea fields
	 */
	private void addOnKeyUpAttribute() {

		if (dataField != null &&
			(dataField.equalsIgnoreCase("TEXT")) || (dataField.equalsIgnoreCase("PASSWORD"))
			|| (dataField.equalsIgnoreCase("TEXTAREA"))
			){
				// For On Key Up
				if( this.onkeyup == null ){
					this.onkeyup = "'validateLength(this);'";
				}else{
					if(!onkeyup.endsWith(";") ){
						onkeyup = onkeyup+";";
					}
					this.onkeyup ="'"+ onkeyup + "  validateLength(this); ' ";
				}
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
	public String getTitle() {
		return title;
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
	public void setSelectedvalue(String string) {
		selectedvalue = string;
	}
	private boolean isNull(String value){
		if(value==null ){
			return true;
		}
		if(value.trim().toUpperCase().indexOf("null")>0){
			return true;
		}
		return false;
	}
	private boolean isEmpty(String value){
		if(value.trim().equals("")){
			return true;
		}
		return false;

	}
	private String seakValue(String value) {
		//System.out.print("  "+value);
		if (value == null)
			value = "";
		if(value.toUpperCase().indexOf("NULL")!=-1) {
			value = "";
		}
		if(value.trim().toUpperCase().indexOf("null")>0){
			value = "";
		}
		if(value.trim().equals("")){
			return (value = "''");
		}
		return ("'"+value.trim()+"'");
	}

	/**
	 * @return
	 */
	public String getTypeprompt() {
		return typeprompt;
	}

	/**
	 * @param string
	 */
	public void setTypeprompt(String string) {
		typeprompt = string;
	}

	/**
	 * @return
	 */
	public String getRequired() {
		return required;
	}

	/**
	 * @param string
	 */
	public void setRequired(String string) {
		required = string;
	}

	/**
	 * @return
	 */

	/**
	 * @return
	 */
	public String getMultiple() {
		return multiple;
	}

	/**
	 * @param string
	 */
	public void setMultiple(String string) {
		multiple = string;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param string
	 */
	public void setType(String string) {
		type = string;
	}
	private void setFomatedValue(String valueContainerName , String value,String type) throws Exception{
		Object obj ="";
		if(valueContainerName!=null && value!=null){
			try{
				obj= this.pageContext.getSession().getAttribute(valueContainerName);
				if(obj!=null){
					if(obj instanceof Hashtable){
						Hashtable htable= (Hashtable)obj;
							obj=htable.get(value);
							if(obj!=null && type!=null){
								if(type.equalsIgnoreCase(Constants.DATE_FIELD)){
									htable.put(value,Constants.getFormatedDateValue((String)obj));
								}
								else if(type.equalsIgnoreCase(Constants.DATETIME_FIELD)){
									htable.put(value,Constants.getFormatedDateTimeValue((String)obj));
								}
								else if(type.equalsIgnoreCase(Constants.NUMBER_FIELD)){
									htable.put(value,Constants.getFormatedDecimalValue((String)obj));
								}
								else if(type.equalsIgnoreCase(Constants.DECIMAL_FIELD)){
									htable.put(value,Constants.getFormatedDecimalValue((String)obj));
								}
								else if(type.equalsIgnoreCase(Constants.TIME_FIELD)){
									htable.put(value,Constants.getFormatedTimeValue((String)obj));
								}
							}
						}
					}
				}
				catch(Exception exp)
				{
					exp.printStackTrace();
				}
	     }
	}
	/**
	 * @return
	 */
	public String getApplydisplayformat() {
		return applydisplayformat;
	}

	/**
	 * @param string
	 */
	public void setApplydisplayformat(String string) {
		applydisplayformat = string;
	}

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

	/**
	 * @return
	 */
	public String getIsNullable() {
		return isNullable;
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
	public void setValidateReq(String string) {
		validateReq = string;
	}

	/**
	 * @param string
	 */
	public void setValidateType(String string) {
		validateType = string;
	}

	void setFieldValidationAttr(boolean bolIfCombo, StringBuffer strbAttr){
		/*Changes made by Danish for Field Validation*/
		if (!bolIfCombo){
			if (this.validateReq != null){
				strbAttr.append(" validateReq=\""+this.validateReq+"\" ");
			}
			//Setting default value - commented by Edwin
			else {
				this.validateReq = "N";
				strbAttr.append(" validateReq=\""+this.validateReq+"\" ");
			}

			if (this.validateType != null){
				strbAttr.append(" validateType=\""+this.validateType+"\" ");
			}
			//Setting default value - commented by Edwin
			else {
				this.validateType = Constants.ALPHA_NUMERIC_VALIDATION;// "Alphanumeric";
				strbAttr.append(" validateType=\""+this.validateType+"\" ");
			}
		}

		if (this.promptLabel != null){
			strbAttr.append(" promptLabel=\""+this.promptLabel+"\" ");
		}
		//Setting default value - commented by Edwin
		else {
			this.promptLabel = "";
			strbAttr.append(" promptLabel=\""+this.promptLabel+"\" ");
		}

		if (this.isNullable != null){
			strbAttr.append(" isNullable=\""+this.isNullable+"\" ");
		}
		//Setting default value - commented by Edwin
		else {
			this.isNullable = "Y";
			strbAttr.append(" isNullable=\""+this.isNullable+"\" ");
		}


		//Code added by Edwin for setting default values for maxlength, leftDigits and rightDigits attributes.
		//System.out.println("\n\n^^^^^^^^^^^^^^ dataFieldName = "+this.dataFieldName+" this.maxlength = "+this.maxlength);

		if (this.validateType != null &&
			(this.validateType.trim().equalsIgnoreCase(Constants.ALPHA_NUMERIC_VALIDATION) ||
			 this.validateType.trim().equalsIgnoreCase(Constants.INT_VALIDATION) ||
		   	 this.validateType.trim().equalsIgnoreCase(Constants.BIG_INTEGER_VALIDATION) ||
			 this.validateType.trim().equalsIgnoreCase(Constants.LONG_VALIDATION)
			)
			&& this.maxlength == null)
		{
			//System.out.println("DV - Setting default value for maxlength = 0 \n\n");
			this.maxlength = "0";
		}
		else
		{
			//System.out.println("DV - ELSE - this.maxlength \n\n");
		}

		//System.out.println("\n\n^^^^^^^^^^^^^^ dataFieldName = "+this.dataFieldName+"this.leftDigits = "+this.leftDigits);
		if (this.validateType != null &&
			(this.validateType.trim().equalsIgnoreCase(Constants.DECIMAL_VALIDATION)||
			 this.validateType.trim().equalsIgnoreCase(Constants.DOUBLE_VALIDATION)||
			 this.validateType.trim().equalsIgnoreCase(Constants.FLOAT_VALIDATION)||
			 this.validateType.trim().equalsIgnoreCase(Constants.BIG_DECIMAL_VALIDATION)
			 )
			&& this.leftDigits == null)
		{
			//System.out.println("DV - Setting default value for leftDigits = 0 \n\n");
			this.leftDigits = "10";
		}
		else
		{
			//System.out.println("DV - ELSE - this.leftDigits \n\n");
		}

		//System.out.println("\n\n^^^^^^^^^^^^^^ dataFieldName = "+this.dataFieldName+"this.rightDigits= "+this.rightDigits);
		if (this.validateType != null &&
			(this.validateType.trim().equalsIgnoreCase(Constants.DECIMAL_VALIDATION)||
			 this.validateType.trim().equalsIgnoreCase(Constants.DOUBLE_VALIDATION)||
			 this.validateType.trim().equalsIgnoreCase(Constants.FLOAT_VALIDATION)||
			 this.validateType.trim().equalsIgnoreCase(Constants.BIG_DECIMAL_VALIDATION)
			 )
			&& this.rightDigits == null)
		{
			//System.out.println("DV - Setting default value for rightDigits = 0 \n\n");
			this.rightDigits = "2";
		}
		else
		{
			//System.out.println("DV - ELSE - this.rightDigits \n\n");
		}

		/*Changes made by Danish for Field Validation*/
	}
	/**
	 * @return
	 */
	public String getPromptLabel() {
		return promptLabel;
	}

	/**
	 * @param string
	 */
	public void setPromptLabel(String string) {
		promptLabel = string;
	}

	/**
	 * @return
	 */
	public String getTabIndex() {
		return tabIndex;
	}

	/**
	 * @param string
	 */
	public void setTabIndex(String string) {
		tabIndex = string;
	}

	/*private void generateTextFieldCode(StringBuffer sBuf, StringBuffer endsBuf, StringBuffer functionsBuf) throws Exception
	{
		sBuf.append(" <input type='text' ");
		if (dataFieldName != null) {
			sBuf.append(" name='" + this.dataFieldName + "'");
		}
		if (this.style != null) {
			sBuf.append(" class='" + this.style + "'");
		}
		if (this.size != null) {
			sBuf.append(" size='" + this.size + "'");
		}
		if (this.maxlength!= null) {
			sBuf.append(" maxlength='" + this.maxlength + "'");
		}


		setFieldValidationAttr(false,functionsBuf);
		sBuf.append(functionsBuf.toString());
		if (defaultValueContainerName != null
			&& this.defaultValue != null) {

				Object specialCharacterHandledString = this.getOBJValue(defaultValueContainerName,defaultValue);

				if(! specialCharacterHandledString.equals("")){
			sBuf.append(
				" value=\""
					+ specialCharacterHandledString +"\"");

			}	else if (value!=null){
				sBuf.append(" value=\""+ super.handleSpecialCharacters(value) +"\"");

			}
		}
		sBuf.append(endsBuf.toString());
		sBuf.append(" >");
	}*/

	private void generateTextFieldCode(StringBuffer sBuf, StringBuffer endsBuf, StringBuffer functionsBuf) throws Exception
	{

		if(defaultValue != null && !defaultValueContainerName.trim().equalsIgnoreCase("cardsassignment") &&
		((this.defaultValue.trim().indexOf("card_no") != -1) ||
		this.defaultValue.trim().indexOf("account_no") != -1 ||
		this.defaultValue.trim().equalsIgnoreCase("i_002pan") ||
		this.defaultValue.trim().equalsIgnoreCase("category_ref")||
		this.defaultValue.trim().equalsIgnoreCase("i_102account_id1")
		)){

			Object specialCharacterHandledString = this.getOBJValue(defaultValueContainerName,defaultValue);

			if(specialCharacterHandledString == null || ((String)specialCharacterHandledString).trim().length() <=1){
			//---Generate Previous Tag As Implemented
			generateTextField(this.dataFieldName,sBuf,functionsBuf,endsBuf,"text");

			}else{

			String temp = ((String)specialCharacterHandledString).trim();
			temp = ComponentsUtil.removeSpaces(temp);
			if(temp.length() >=16 && temp.length()<=19){
			//--Step 1
			//Generate Hidden Field As Above In Find Case [with Value]
			//generateTextField(this.dataFieldName,sBuf,functionsBuf,endsBuf,"hidden");

			//sBuf.append("<br/>");

			//--Step 2
			//Generate ReadOnly Field To show Masked Value
			//generateTextField("cardNoTemp",sBuf,functionsBuf,endsBuf,"text");
            generateTextField(this.dataFieldName+"Temp",sBuf,functionsBuf,endsBuf,"text");
			}else{
			generateTextField(this.dataFieldName,sBuf,functionsBuf,endsBuf,"text");
			}
			}
		    }else{
		   //----Previous Implementation
		   generateTextField(this.dataFieldName,sBuf,functionsBuf,endsBuf,"text");
		}
	}

	/**
	 * @param string
	 * @param sBuf
	 * @param string2
	 */
	private void generateTextField(String name, StringBuffer sBuf,StringBuffer functionsBuf, StringBuffer endsBuf, String type) throws Exception{

				sBuf.append(" <input type='"+type+"' ");
				if (dataFieldName != null) {
					  sBuf.append(" name='" + name + "'");
				}
				if (this.style != null) {
					  sBuf.append(" class='" + this.style + "'");
				}
				if (this.size != null) {
					  sBuf.append(" size='" + this.size + "'");
				}
				if (this.maxlength!= null) {
					  //sBuf.append(" maxlength='" + this.maxlength + "'");
					  // Changed by Milyas to use components maxl attribute checking of max. length
						sBuf.append(" maxl='" + this.maxlength + "'");
				}
				setFieldValidationAttr(false,functionsBuf);
				sBuf.append(functionsBuf.toString());
				if (defaultValueContainerName != null
					  && this.defaultValue != null) {

							Object specialCharacterHandledString = this.getOBJValue(defaultValueContainerName,defaultValue);

							if(! specialCharacterHandledString.equals("")){
							if(name.equals(this.dataFieldName+"Temp")){
							String temp = (String)specialCharacterHandledString;
//							temp =  temp.trim();
//							temp = ComponentsUtil.removeSpaces(temp);
//							String firstSix = temp.substring(0,6);
//							String lastFour = temp.substring(temp.length()-4,temp.length());
//							int charsMissed = temp.length()-10;
//							temp = firstSix;
//							for(int i=0; i<charsMissed;i++){
//								  temp = temp+"*";
//							}
//							temp = temp+ lastFour;
							specialCharacterHandledString = ComponentsUtil.maskCardNumber(temp);
							}
							 sBuf.append(" value=\""      + specialCharacterHandledString +"\"");
					  }     else if (value!=null){
							sBuf.append(" value=\""+ super.handleSpecialCharacters(value) +"\"");
					  }
				}
				sBuf.append(endsBuf.toString());
				sBuf.append(" >");
	  }
	private void generatePasswordFieldCode(StringBuffer sBuf, StringBuffer endsBuf, StringBuffer functionsBuf) throws Exception
	{
		sBuf.append(" <input type='password' ");
		if (dataFieldName != null) {
			sBuf.append(" name='" + this.dataFieldName + "'");
		}
		if (this.style != null) {
			sBuf.append(" class='" + this.style + "'");
		}
		if (this.size != null) {
			sBuf.append(" size='" + size + "'");
		}
		if(this.maxlength != null){
			//sBuf.append(" maxlength='" + this.maxlength + "'");
			// Changed by Milyas to use components maxl attribute checking of max. length
			  sBuf.append(" maxl='" + this.maxlength + "'");
		}

		setFieldValidationAttr(false,functionsBuf);
		sBuf.append(functionsBuf.toString());
		if (defaultValueContainerName != null
			&& this.defaultValue != null) {
			sBuf.append(
				" value=\""
					+ this.getOBJValue(
						defaultValueContainerName,
						defaultValue)+"\"");

		}
		sBuf.append(endsBuf.toString());
		sBuf.append(" >");
	}
	/*
	* code for button functionality in components
	*  saqib bukhari ----- 1st March 2006
	*/
	private void generateButtonCode(StringBuffer sBuf, StringBuffer endsBuf, StringBuffer functionsBuf) throws Exception
	{
		sBuf.append(" <input type='button' ");
		if (dataFieldName != null) {
			sBuf.append(" name='" + this.dataFieldName + "'");
		}
		if (this.style != null) {
			sBuf.append(" class='" + this.style + "'");
		}
		sBuf.append(functionsBuf.toString());
		if (defaultValueContainerName != null
			&& this.value != null) {
			sBuf.append(
				" value=\""+this.value+"\"");
		}
		sBuf.append(endsBuf.toString());
		sBuf.append(" >");
	}

	private void generateSubmitCode(StringBuffer sBuf, StringBuffer endsBuf, StringBuffer functionsBuf) throws Exception
	{
		sBuf.append(" <input type='submit' ");
		if (dataFieldName != null) {
			sBuf.append(" name='" + this.dataFieldName + "'");
		}
		if (this.style != null) {
			sBuf.append(" class='" + this.style + "'");
		}
		sBuf.append(functionsBuf.toString());
		if (defaultValueContainerName != null
			&& this.value != null) {
			sBuf.append(
				" value=\""+this.value+"\"");
		}
		sBuf.append(endsBuf.toString());
		sBuf.append(" >");
	}

	private void generateResetCode(StringBuffer sBuf, StringBuffer endsBuf, StringBuffer functionsBuf) throws Exception
	{
		sBuf.append(" <input type='reset' ");
		if (dataFieldName != null) {
			sBuf.append(" name='" + this.dataFieldName + "'");
		}
		if (this.style != null) {
			sBuf.append(" class='" + this.style + "'");
		}
		sBuf.append(functionsBuf.toString());
		if (defaultValueContainerName != null
			&& this.value != null) {
			sBuf.append(
				" value=\""+this.value+"\"");
		}
		sBuf.append(endsBuf.toString());
		sBuf.append(" >");

	}
	/*
	 * End Code Saqib Bukhari ----- 1st March 2006
	 */


	private void generateSelectCode(StringBuffer sBuf, StringBuffer endsBuf, StringBuffer functionsBuf, String secName, String str_value) throws Exception
	{
		String selValues[] = null;
		sBuf.append(" <select ");
		if (dataFieldName != null) {
			sBuf.append(" name='" + this.dataFieldName + "'");
		}
        if(multiple != null && multiple.equalsIgnoreCase("true"))
        {
            sBuf.append(" multiple ");
            if(size != null)
                sBuf.append(" size=\"" + size + "\" ");
            else
                sBuf.append(" size= \"1\"");
            selValues = (String[])pageContext.getRequest().getAttribute(secName + dataFieldName + "SEL_VALUES");
        }
		sBuf.append(endsBuf.toString());
		if (this.style != null) {
			sBuf.append(" class='" + this.style + "' ");
		}


		setFieldValidationAttr(true,functionsBuf);
		sBuf.append(functionsBuf.toString());
		sBuf.append(" > ");
		String str_selected =(String) this.pageContext.getSession().getAttribute(dataFieldName + "_selected");
			if(str_selected!=null){
				this.pageContext.getSession().removeAttribute(dataFieldName + "_selected");
			}
		String str_index = (String) this.pageContext.getSession().getAttribute(
		dataFieldName + "_SELINDEX");
		if(str_index!=null){
			this.pageContext.getSession().removeAttribute(dataFieldName + "_SELINDEX");
		}
		if(str_selected==null) {

			if( this.pageContext.getSession().getAttribute(secName+Constants.ERRORVALUE)!=null && this.pageContext.getSession().getAttribute(secName+Constants.ERRORVALUE).equals(Constants.YES) ){
				//defaultValueContainerName = secName+Constants.ININFO;
				defaultValue = this.dataFieldName;
				if(this.defaultValue!=null)
				str_selected = Constants.getContainerValue(this.pageContext.getSession().getAttribute(secName+Constants.ININFO),defaultValue);
			}
			else
			if(secName!=null && defaultValue!=null ) {
				str_selected = Constants.getContainerValue(this.pageContext.getSession().getAttribute(secName),defaultValue);
			}
			if(str_selected==null && str_value!=null){
				str_selected = str_value;
				//----
				this.pageContext.getSession().removeAttribute(dataFieldName + "_SELVALUE");
			}
		}
		if (this.defaultValueContainerName != null) {
			List list = this.getList(defaultValueContainerName);
			String data[] = null;
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					data = (String[]) list.get(i);
					sBuf.append("<option value=");

					sBuf.append(this.seakValue(data[0]));
                    if(multiple != null && multiple.equalsIgnoreCase("true"))
                    {
                        String value = data[0];
                        int j = 0;
                        boolean dec = false;
                        if(selValues != null && value != null)
                        {
                            while(j < selValues.length)
                                if(selValues[j++].trim().equals(value.trim()))
                                {
                                    dec = true;
                                    break;
                                }
                            if(dec)
                                sBuf.append(" selected");
                        }
                        sBuf.append(">");
                        sBuf.append(data[1]);
                        sBuf.append("</option>");
                    } else{

					if (str_selected != null && !((String)str_selected).trim().equals("") && !data[0].trim().equals(""))
					{


//						Pattern pFrom = Pattern.compile("\\sfrom\\s",Pattern.CASE_INSENSITIVE);
//						Pattern pOrder = Pattern.compile("\\sorder\\s",Pattern.CASE_INSENSITIVE);
//						java.util.regex.Matcher m = pFrom.matcher(sql);
//
//						if (m.find()) {

						if (data[0].trim().equalsIgnoreCase((str_selected.trim()))) {
							this
								.pageContext
								.getSession()
								.removeAttribute(
								dataFieldName + "_selected");
							if(sBuf.indexOf("selected")!=-1){
								sBuf.replace(sBuf.indexOf("selected"),sBuf.indexOf("selected")+8,"");
							}
							sBuf.append(" selected");
							str_selected=null;
						}
					}else if (str_index !=null ){
						this.pageContext.getSession().removeAttribute(dataFieldName + "_SELINDEX");
						if(str_index.equals(String.valueOf(i)))	 {
							if(sBuf.indexOf("selected")!=-1){
								sBuf.replace(sBuf.indexOf("selected"),sBuf.indexOf("selected")+8,"");
							}
							sBuf.append(" selected ");
							str_index=null;
						}
					}else if (selectedIndex !=null ){
						if(selectedIndex.equals(String.valueOf(i))){
							if(sBuf.indexOf("selected")!=-1){
								sBuf.replace(sBuf.indexOf("selected"),sBuf.indexOf("selected")+8,"");
							}

							sBuf.append(" selected ");
							selectedIndex=null;
						}
					}
					else {
						if (i == 0){
							if(sBuf.indexOf("selected")!=-1){
								sBuf.replace(sBuf.indexOf("selected"),sBuf.indexOf("selected")+8,"");
							}
							sBuf.append(" selected");
						}
					}
					sBuf.append(">");
					sBuf.append(data[1]);
					sBuf.append("</option>");
				}
				}
			}
		}
		sBuf.append(" </select> ");
	}

	private void generateTextAreaCode(StringBuffer sBuf, StringBuffer endsBuf, StringBuffer functionsBuf) throws Exception
	{
		sBuf.append(" <TEXTAREA ");
		if (dataFieldName != null) {
			sBuf.append(" name='" + this.dataFieldName + "'");
		}
		if (this.style != null) {
			sBuf.append(" class='" + this.style + "'");
		}
		if (this.rows != null) {
			sBuf.append(" cols='" + this.cols + "'");
		}
		if (this.rows != null) {
			sBuf.append(" rows='" + this.rows + "'");
		}


		setFieldValidationAttr(false,functionsBuf);
		sBuf.append(functionsBuf.toString());
		sBuf.append(endsBuf.toString());
		sBuf.append(" >");
		if (defaultValueContainerName != null
			&& this.defaultValue != null) {
			sBuf.append(this.getOBJValue(
						defaultValueContainerName,
						defaultValue));
		}
		sBuf.append("</TEXTAREA>");
	}

	private void generateCheckBoxCode(StringBuffer sBuf, StringBuffer endsBuf, StringBuffer functionsBuf, boolean chk) throws Exception
	{
		sBuf.append(" <input type='CHECKBOX' ");
		if (dataFieldName != null) {
			sBuf.append(" name='" + this.dataFieldName + "' ");
		}


		sBuf.append(functionsBuf.toString());

		Object specialCharacterHandledString = this.getOBJValue(defaultValueContainerName,defaultValue);

		String str_selected =
							(String) this.pageContext.getSession().getAttribute(
							dataFieldName +specialCharacterHandledString+ "_selected");

		if(selectedvalue!=null){

			Object specialCharacterHandledString2 = this.getOBJValue(defaultValueContainerName,defaultValue);

			if( specialCharacterHandledString2!=null ){
				if(selectedvalue.equalsIgnoreCase((String)specialCharacterHandledString2)){
					sBuf.append(" CHECKED ");
					chk=true;
			}
		  }
		}
		if (str_selected != null) {
			sBuf.append(" CHECKED ");
			chk=true;
		}
		if(chk==true){
			if (defaultValueContainerName != null
				&& this.defaultValue != null) {
			  if(! defaultValue.trim().equals("")) {

				Object specialCharacterHandledString3 = this.getOBJValue(defaultValueContainerName,defaultValue);

				if(! specialCharacterHandledString3.equals("")){
					sBuf.append(
					"  value='"
						+ specialCharacterHandledString3 +"'");
				}//else{ sBuf.append(" value='NO'"); }
			}//else{ sBuf.append(" value='NO'"); }
			}
			else if (value!=null){
				sBuf.append(" value='"+ super.handleSpecialCharacters(value) +"' ");
			}

		}

		sBuf.append(endsBuf.toString());
		sBuf.append(" >");
	}

	private void generateRadioButtonCode(StringBuffer sBuf, StringBuffer endsBuf, StringBuffer functionsBuf, boolean chk) throws Exception
	{
		sBuf.append(" <input type='RADIO' ");
		if (dataFieldName != null) {
			sBuf.append(" name='" + this.dataFieldName + "' ");
		}


		sBuf.append(functionsBuf.toString());

		Object specialCharacterHandledString = this.getOBJValue(defaultValueContainerName,defaultValue);

		// else{ sBuf.append(" value='NO'"); }
		String str_selected =
							(String) this.pageContext.getSession().getAttribute(
								dataFieldName +specialCharacterHandledString+ "_selected");
		if(selectedvalue!=null){
			if( specialCharacterHandledString!=null ){
				if(selectedvalue.equalsIgnoreCase((String)specialCharacterHandledString)){
					sBuf.append(" CHECKED ");
					chk=true;
			}
			}
		}
		if (str_selected != null) {
			sBuf.append(" CHECKED ");
			chk=true;
		}else if(this.selected !=null && this.selected.equalsIgnoreCase("TRUE")){
			sBuf.append(" CHECKED ");
			chk=true;
		}
		if(chk==true){
			if (defaultValueContainerName != null
				&& this.defaultValue != null) {
				if(! defaultValue.trim().equals("")) {

					Object specialCharacterHandledString2 = this.getOBJValue(defaultValueContainerName,defaultValue);

					if(! specialCharacterHandledString2.equals("")){
				sBuf.append(
					"  value='"
						+ specialCharacterHandledString2+"'");
					} //else{ sBuf.append(" value='NO'"); }
				} //else{ sBuf.append(" value='NO'"); }
			}else if (value!=null){
				sBuf.append(" value='"+ super.handleSpecialCharacters(value) +"' ");
			}

		}
		sBuf.append(endsBuf.toString());
		sBuf.append(" >");
	}
	/**
	 * @return
	 */
	/*
	public String getPrecision() {
		return precision;
	}
	*/

	/**
	 * @return
	 */
	/*
	public String getScale() {
		return scale;
	}
	*/

	/**
	 * @param string
	 */
	/*
	public void setPrecision(String string) {
		precision = string;
	}
	*/

	/**
	 * @param string
	 */
	/*
	public void setScale(String string) {
		scale = string;
	}
	*/

	/**
	 * @return
	 */
	public String getLeftDigits() {
		return leftDigits;
	}

	/**
	 * @param string
	 */
	public void setLeftDigits(String string) {
		leftDigits = string;
	}

	/**
	 * @return
	 */
	public String getRightDigits() {
		return rightDigits;
	}

	/**
	 * @param string
	 */
	public void setRightDigits(String string) {
		rightDigits = string;
	}

	/**
	 * @return
	 */
	public String gettextCase() {
		return textCase;
	}

	/**
	 * @param string
	 */
	public void settextCase(String string) {
		textCase = string;
	}

	/**
	 * @return
	 */
	public String getValign() {
		return valign;
	}

	/**
	 * @param string
	 */
	public void setValign(String string) {
		valign = string;
	}

}