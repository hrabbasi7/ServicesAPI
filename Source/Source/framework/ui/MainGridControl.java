/*
 * Created on Feb 11, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
import com.i2c.component.util.ComponentsUtil;
import com.i2c.util.*;

/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MainGridControl extends BaseBodyControlTag {

	/**
	 *
	 */

	private String evenRow;
	private String oddRow;
	private String selectedRow; // putvalue
	private String rowspan;
	private String colspan;
	private String href;
	private String recvalue;
	private String defaultValueContainerName; // valuecontainer
	private Hashtable styleRelated = null;
	private ArrayList dataTypesList =null;
	private Hashtable comboList=null;
	private Hashtable serailTB =null;
	private String conditionalcolnum=null;
	private String condition4colnum=null;
	private String conditionvalue=null;
	private ValueContainerControl VC;
	private String groupby;
	private String groupbyAlign;
	private String groupbyStyle;

	//For Hidding Grid Elements
	private Hashtable activeComponents;


	/**
	 *
	 * <a href="#" onClick="submitRQ(URL,values)" >
				<tr onMouseOver='high(this);' onMouseOut='low(this);' class='even'><td>1-861005-06-7</td><td>COL1</td><td>COL1</td></tr>
				</a>
	 */
	public int doStartTag() {

		dataTypesList  = new ArrayList();
		styleRelated = new Hashtable();
		comboList = new Hashtable();
		serailTB = new Hashtable();

		//System.out.println(sBuf.toString());
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		String groupByVlaue = "";
		String previousGroupByValue = "";
		int ccol=0;
		int c4col =0;
		int catColspan = 0;
		String cValue=null;
		try{
			ccol = Integer.parseInt(conditionalcolnum);
		}catch(Exception exp){
			ccol=-1;
		}
		//--------------------------------------
		try{
			c4col = Integer.parseInt(condition4colnum);
		}catch(Exception exp){
			c4col=-1;
		}


		JspWriter out = pageContext.getOut();
		if (defaultValueContainerName == null) {
			try {
				 VC =
					(ValueContainerControl) this.getParent();
				//System.out.println("PARENT: " + VC.getValueContanier());
				if (VC != null) {
					defaultValueContainerName = VC.getValueContanier();
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		if (this.action != null) {
			recvalue = this.action;
			recvalue = ComponentConstants.getAction(recvalue);
		}
		StringBuffer sBuf = new StringBuffer();

		if(this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.RECNO)!=null){


			   sBuf.append(" <input type='HIDDEN' ");
			   sBuf.append(" name='" +Constants.RECNO+ "'");
			   sBuf.append(" value='" + this.pageContext.getSession().getAttribute(VC.getValueContanier()+Constants.RECNO) +"' >");
			   this.pageContext.getSession().removeAttribute(VC.getValueContanier()+Constants.RECNO);

		}

		try {

			Object obj = null;
			ArrayList list = null;
			ArrayList dataList = null;
			List serial = null;
			Hashtable uniqueIdList = null;
			ArrayList uniqueIdList_column = null;
			ArrayList uniqueIdList_value = null;
			ArrayList uniqueIdList3 = null;

			obj =
				this.pageContext.getSession().getAttribute(
					defaultValueContainerName);
			this.pageContext.getSession().removeAttribute(
				defaultValueContainerName);
			if (obj instanceof Hashtable) {
				if (obj != null) {

					Hashtable table = (Hashtable) obj;
					serial =(ArrayList) (table.get(Constants.SERIAL));

					/*
					uniqueIdTable.put(Constants.UNIQUEID_COLUMN, uniqueIdList_column);
					uniqueIdTable.put(Constants.UNIQUEID_VALUE, uniqueIdList_value);

					if (uniqueIdList != null) {
						dataTable.put(Constants.UNIQUEIDLIST, uniqueIdTable);
					}
					*/

					serial =(ArrayList)(((Hashtable) obj).get(Constants.SERIAL));
					uniqueIdList =
						(Hashtable) ((Hashtable) obj).get(
							Constants.UNIQUEIDLIST);
					uniqueIdList_column =
						(ArrayList) ((Hashtable) uniqueIdList).get(
							Constants.UNIQUEID_COLUMN);
					uniqueIdList_value =
						(ArrayList) ((Hashtable) uniqueIdList).get(
							Constants.UNIQUEID_VALUE);

					obj = ((Hashtable) obj).get(Constants.GRID);




				}
				if (obj instanceof ArrayList) {
					list = (ArrayList) obj;
					int size = list.size();
					if (!(uniqueIdList_value != null
						&& size == uniqueIdList_value.size())) {
						System.out.println("UniqueId 1 is null");
						uniqueIdList_value = null;
					}
					boolean serialNum=false ,serialDispaly=false;
					int sIndex=0;

					if( serailTB.get("SERIAL")!=null ) {
						serialNum = true;
						sIndex =Integer.parseInt((String)serailTB.get("SERIAL")) ;
					}
					for (int i = 0; i < size; i++) {
						//sbukhari [code for Categorization] 28_11_05
  						dataList = (ArrayList) list.get(i);
  						if(!CommonUtilities.isNullOrEmptyString(groupby)){
  							if(dataTypesList!=null && dataTypesList.size()>0 ) {
								for (int ind = 0; ind < dataList.size(); ind++) {
									String [] colList = (String []) dataTypesList.get(ind);
									catColspan = dataList.size();
									/*System.out.println("Length of the array:="+colList.length);
									System.out.println("Length of the dataTypesList:="+dataTypesList.size());
									System.out.println("Length of the DataList:="+dataList.size());*/
									if(colList[2].equals(groupby)){
										groupByVlaue = (String) dataList.get(ind);
										 break;
									}
								}
							}
	  						if(!groupByVlaue.equals(previousGroupByValue)){
								previousGroupByValue = groupByVlaue;
	  							sBuf.append("<TR><td colspan='");
								sBuf.append(""+catColspan);
	  							sBuf.append("' align='");
	  							System.out.println(CommonUtilities.fixNull(groupbyAlign));
	  							sBuf.append(CommonUtilities.fixNull(groupbyAlign));
	  							sBuf.append("' class='");
	  							sBuf.append(CommonUtilities.fixNull(groupbyStyle));
	  							sBuf.append("')>");
	  							sBuf.append(groupByVlaue);
	  							sBuf.append("</td></TR>");
	  						}
						}
						//sbukhari code End
						if (this.onclick != null) {
							sBuf.append(
								" <A  href='#' onClick=' return submitAction(");
							sBuf.append(onclick + ",\"");
							sBuf.append(href);
							sBuf.append("\",\"");

							if(null == recvalue)
								sBuf.append(Constants.BROWSE_EDIT);
							else
								sBuf.append(recvalue);

							sBuf.append("\",\"");
							if (uniqueIdList_value != null) {
								sBuf.append(uniqueIdList_value.get(i));
							} else {
								sBuf.append(" ");
							}
							sBuf.append("\",\"");
							sBuf.append(" ");
							sBuf.append("\",\"");
							sBuf.append(" ");
							sBuf.append("\");'");
							sBuf.append(" >");
						}
						sBuf.append("<TR ");
						if (evenRow != null
							&& oddRow != null
							&& selectedRow != null) {
							if (i == 0)
								style = selectedRow;
							style = (i % 2 == 0) ? evenRow : oddRow;
							sBuf.append(" class = '" + style + "'");
						}
						if (this.onmouseover != null) {
							sBuf.append(
								" onMouseOver =\"" + onmouseover + "\"");
						}
						if (this.onmouseout != null) {
							sBuf.append(" onMouseOut =\"" + onmouseout + "\"");
						}
						if (width != null)
							sBuf.append(" width=\"" + width + "\"");
						if (height != null)
							sBuf.append(" height=\"" + height + "\"");
						if (name != null) {
							sBuf.append(" name=\"" + name + "\"");
						}
						if (id != null) {
							sBuf.append(" id=\"" + id + "\"");
						}
						if (rowspan != null) {
							sBuf.append(" rowspan=\"" + rowspan + "\"");
						}
						if (colspan != null) {
							sBuf.append(" colspan=\"" + colspan + "\"");
						}

						// Style sheet
						// Mous Overs COLOURS
						// EVEN ODD ROW COLORS
						// MOUSE CLICKS
						// NEED TO CHANGE THE CURSOR for ROWS to hands
						sBuf.append(" >");
						//===========
						//dataList = (ArrayList) list.get(i);
						//===========
						if (dataList.size() > 0) {

							if(ccol!=-1){
								cValue=(String)dataList.get(ccol);
								if(cValue!=null){
									//if(conditionvalue!=null && conditionvalue.toUpperCase().equals("NULL"));	conditionvalue="";
									if(conditionvalue!=null && conditionvalue.toUpperCase().indexOf(cValue.toUpperCase())!=-1){
										dataList.set(c4col,"HIDETHIS");
									}
								}
								dataList.remove(ccol);
							}

							int datasize =dataList.size();
							//System.out.println( "Data Size : " +datasize);
//							if( colList[0].equals("SERIAL") ){
//								if(serial!=null)
//								sBuf.append(  serial.get(i) );
//							}
							String value = null;
							if(dataTypesList!=null && dataTypesList.size()>0 ) {

							for (int k = 0; k < datasize; k++) {


								// Align according to the dates ,
								// numbers etc and it should be configureable.
								// Develop its
								/**
								 * 1) Define the Main categories like number, dates , times   and there alingment globally
								 * 2) Define the formatting for those numbers.*
								 */

								String [] colList_Original = (String []) dataTypesList.get(k);

								// Code By ILYAS [31/03/2006] To show specific elements in Grid
								// Updating colList according to the User Requirement
								// To Hide Specific Elements
								String[] colList = new String[colList_Original.length];
								int start = 0;
								while(start < colList_Original.length){
									colList[start] = colList_Original[start++];
								}
								if(colList != null && colList.length == 6){


									String attrName = colList[5];
									String key = (hiddenRowKey != null)?(String)dataList.get(Integer.parseInt(hiddenRowKey)):null;
									if(isHiddenElement(attrName, key)){

										if(	colList[0].equals("TEXT") ||colList[0].equals("OPTION") ||
											colList[0].equals("CHECKBOX")||colList[0].equals("OPTION")){
											colList[0] = "LABEL";
											colList[1] = "";
											colList[3] = "";
											colList[4] = "";
											colList[5] = "";
										}

									}
								}

								// End Code by ILYAS


								if( serialNum==true && sIndex==k ){
									serialDispaly=true;
									if(serial!=null)
										if(serial.get(i)!=null) {
											if(serailTB.get("SERIALSTYLEREL") !=null){
												sBuf.append ("<TD ");
												sBuf.append (serailTB.get("SERIALSTYLEREL"));
												sBuf.append (" >");
											}else{
												sBuf.append("<TD>");
											}
											sBuf.append(  serial.get(i) );
											sBuf.append("</TD>");
										}
								}
//								if( colList[0].equals("SERIAL") ) {
//									continue;
//								}
								//System.out.println( "Childs Size : " +dataTypesList);
								if(k<dataList.size()) {
									value = (String) dataList.get(k);
									if(value!=null)
									{
										value = super.handleSpecialCharacters(value);
									}
								}else
								{
									value="";
								}
								if(colList!=null && colList.length == 6){
									if( !colList[0].equals("HIDDEN") )
									if(this.styleRelated.get(String.valueOf(k)) !=null){
										sBuf.append ("<TD ");
										sBuf.append (styleRelated.get(String.valueOf(k)));
										sBuf.append (" >");
									}else{
										sBuf.append("<TD>");
									}
									if( colList[0].equals("OPTION") ) {
										sBuf.append( colList[1] );
										sBuf.append(" name = '" +(colList[2]+"_"+ i)+"'");
										if(!colList[0].equals("LABEL") &&
											ComponentsUtil.fixNull(
												(String)pageContext.getRequest().
												  getAttribute("isUnlock")).
												  	equals("NO")&&
												  	!ComponentsUtil.fixNull(
															(String)pageContext.getRequest().
															  getAttribute("ShowUnlock")).
															  	equals("NO")){
											sBuf.append(" DISABLED = 'true'");
										}
										sBuf.append( colList[3] );
										sBuf.append( this.getListData(k,value));
										sBuf.append( colList[4]);
									}else{
										if(!value.trim().equals("HIDETHIS")){
										String colName = colList[2].trim();

										//---Checking If Card Number
										if(ComponentsUtil.isCardNumber(colName,value,this.defaultValueContainerName)){
										value = ComponentsUtil.maskCardNumber(value);
										}


										if(colList[1].length()>0)
										sBuf.append( colList[1] );
										if(colList[2].length()>0 && !colList[0].equals("LABEL")&& !colList[0].equals("HREF"))
										sBuf.append(" name = '" +(colList[2]+"_"+i)+"'");
										if(colList[3].length()>0)
										sBuf.append( colList[3] );
											if( colList[0].equals("TEXTAREA") || colList[0].equals("LABEL") ) {
												//---Checking If Card Number
												if(ComponentsUtil.isCardNumber(colName,value,this.defaultValueContainerName)){
												value = ComponentsUtil.maskCardNumber(value);
												}
												sBuf.append(  value );
											}else{
												if(colList[0].equals("HREF")){
													sBuf.append(value);
												}else{
												sBuf.append( " value='"+ value +"'");
												}
												if(colList[0].equals("RADIO") || colList[0].equals("CHECKBOX") ) {
													if( value.equals(comboList.get(String.valueOf(k))) )
													sBuf.append(" CHECKED ");
												}
											}
											// Added for HREF
											if(colList[2].length()>0 && !colList[0].equals("LABEL") && colList[0].equals("HREF"))
													sBuf.append(" name = '" +(colList[2]+"_"+i)+"'");
											if(!colList[0].equals("LABEL") &&
												ComponentsUtil.fixNull(
													(String)pageContext.getRequest().
													  getAttribute("isUnlock")).
													  	equals("NO") &&
													  	!ComponentsUtil.fixNull(
															(String)pageContext.getRequest().
															  getAttribute("ShowUnlock")).
															  	equals("NO")){
												sBuf.append(" DISABLED = 'true'");
											}
											if(colList[4].length()>0)
											sBuf.append( colList[4]);
										}
									}
									if( !colList[0].equals("HIDDEN") )
										sBuf.append("</TD>");
								}
							}
							}
							if( serialNum==true && sIndex==dataTypesList.size() && serialDispaly==false ){
								if(serial!=null)
									if(serial.get(i)!=null) {
										if(serailTB.get("SERIALSTYLEREL") !=null){
											sBuf.append ("<TD ");
											sBuf.append (serailTB.get("SERIALSTYLEREL"));
											sBuf.append (" >");
										}else{
											sBuf.append("<TD>");
										}
										sBuf.append(  serial.get(i) );
										sBuf.append("</TD>");
									}
							}
						}
						sBuf.append("</TR>");
						if (this.onclick != null)
							sBuf.append("</A>");
					}
				}
			}
			//System.out.println(sBuf.toString());
			out.print(sBuf.toString());
			out.print("<input type = \"hidden\" name = \"browseFlag\" value = \"true\" >");
			return EVAL_PAGE;
		} catch (Exception e) {
			e.printStackTrace();
			return SKIP_BODY;
		}
	}

	/**
	 * This method checks that the passed element is hidden or not
	 * If hidden it will be changed to HTML Hidden Tag
	 * @param attrName
	 * @return
	 */
	private boolean isHiddenElement(String attrName, String key) {
		if(attrName == null || key == null || attrName.trim().length()<1 || key.trim().length() <1)
		return false;

		// Getting hidden element List from Session
		Hashtable activeComponents = (Hashtable)pageContext.getRequest().getAttribute(ComponentConstants.ACTIVE_GRID_COMPONENTS);
		if(activeComponents == null)
			return false;
		Vector elements = (Vector)activeComponents.get(key);
		if(elements != null && elements.size()>=1 && !elements.contains(attrName)){
			return true;
		}
		return false;
	}

	/**
	 * Is used to set the defalut value
	 * @param value
	 */
	public void setValueContainer(String value) {
		defaultValueContainerName = value;
		;
	}
	/**
	 * @return
	 */
	public String getDefaultValueContainerName() {
		return defaultValueContainerName;
	}

	/**
	 * @return
	 */
	public String getEvenRow() {
		return evenRow;
	}

	/**
	 * @return
	 */
	public String getOddRow() {
		return oddRow;
	}

	/**
	 * @return
	 */
	public String getSelectedRow() {
		return selectedRow;
	}

	/**
	 * @param string
	 */
	public void setDefaultValueContainerName(String string) {
		defaultValueContainerName = string;
	}

	/**
	 * @param string
	 */
	public void setEvenRow(String string) {
		evenRow = string;
	}

	/**
	 * @param string
	 */
	public void setOddRow(String string) {
		oddRow = string;
	}

	/**
	 * @param string
	 */
	public void setSelectedRow(String string) {
		selectedRow = string;
	}

	/**
	 * @return
	 */
	public String getColspan() {
		return colspan;
	}

	/**
	 * @return
	 */
	public String getRowspan() {
		return rowspan;
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
	public void setRowspan(String string) {
		rowspan = string;
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
	public void setRecvalue(String string) {
		recvalue = string;
	}
	public void setAction(String string) {
		this.action = string;
	}
	public void setTypeList(String[] data, Object list) {
		this.dataTypesList.add(data);
		if (list != null)
			this.comboList.put(String.valueOf(dataTypesList.size()-1), list);
	}

	public void setTypeList(String[] data, String selectedValue) {
		this.dataTypesList.add(data);
		if (selectedValue != null)
			this.comboList.put(String.valueOf(dataTypesList.size()-1), selectedValue);
	}

	public void setTypeList(String[] data) {
		this.dataTypesList.add(data);
	}
	public void setStyleRelated(String data){
		this.styleRelated.put(String.valueOf(dataTypesList.size()-1), data);
	}
	private String getListData(int k ,Object valueSelected){
		StringBuffer sBuf=new StringBuffer("");
		List list = (ArrayList) comboList.get(String.valueOf(k));
		String data[] = null;
		if(list!=null)

		for (int i = 0; i < list.size(); i++) {
			data = (String[]) list.get(i);

			//System.out.print(" valueSelected " + valueSelected + "  =  DATA[0]" + data[0] );
			//System.out.println("  DATA[1] " + data[1] );

			sBuf.append("<option value=");
			sBuf.append(seakValue(data[0]));
			if (valueSelected != null && !((String)valueSelected).trim().equals("") && !data[0].trim().equals("")) {
				if (CommonUtilities.trim(data[0]).toUpperCase().equals(CommonUtilities.trim(((String)valueSelected)).toUpperCase())) {
					sBuf.append(" selected");
				}
			}
			else {
				if (i == 0){
					sBuf.append(" selected");
				}
			}
			sBuf.append(">");
			sBuf.append(data[1]);
			sBuf.append("</option>");
		}
		return sBuf.toString();
	}
	public void setSerailInfo(String serial){
		serailTB.put("SERIAL" ,String.valueOf(dataTypesList.size()));

	}
	public void setSerailInfoStyleRelated(String data){
		serailTB.put("SERIALSTYLEREL" ,data);

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
	 * @param string
	 */
	public void setConditionvalue(String string) {
		conditionvalue = string;
	}

	/**
	 * @param string
	 */
	public void setCondition4colnum(String string) {
		condition4colnum = string;
	}

	/**
	 * @param string
	 */
	public void setConditionalcolnum(String string) {
		conditionalcolnum = string;
	}

	/**
	 * @return
	 */
	public String getGroupby() {
		return groupby;
	}

	/**
	 * @param string
	 */
	public void setGroupby(String string) {
		groupby = string;
	}

	/**
	 * @return
	 */
	public String getGroupbyAlign() {
		return groupbyAlign;
	}

	/**
	 * @return
	 */
	public String getGroupbyStyle() {
		return groupbyStyle;
	}

	/**
	 * @param string
	 */
	public void setGroupbyAlign(String string) {
		groupbyAlign = string;
	}

	/**
	 * @param string
	 */
	public void setGroupbyStyle(String string) {
		groupbyStyle = string;
	}

}
