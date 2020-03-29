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
import com.i2c.util.*;

/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GridControl extends BaseBodyControlTag {

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

	/**
	 * 
	 * <a href="#" onClick="submitRQ(URL,values)" >
				<tr onMouseOver='high(this);' onMouseOut='low(this);' class='even'><td>1-861005-06-7</td><td>COL1</td><td>COL1</td></tr>
				</a>
	 */
	public int doStartTag() {
		JspWriter out = pageContext.getOut();
		if (defaultValueContainerName == null) {
			try {
				ValueContainerControl VC =
					(ValueContainerControl) this.getParent();
				//System.out.println("PARENT: " + VC.getValueContanier());
				if (VC != null) {
					defaultValueContainerName =
						VC.getValueContanier();
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		if( this.action!=null){
			recvalue = this.action;
			recvalue = ComponentConstants.getAction(recvalue);
		}
		StringBuffer sBuf = new StringBuffer();
		try {

			Object obj = null;
			ArrayList list = null;
			ArrayList dataList = null;
			Hashtable uniqueIdList=null;
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

					/*					
					uniqueIdTable.put(Constants.UNIQUEID_COLUMN, uniqueIdList_column);
					uniqueIdTable.put(Constants.UNIQUEID_VALUE, uniqueIdList_value);

					if (uniqueIdList != null) {
						dataTable.put(Constants.UNIQUEIDLIST, uniqueIdTable);
					}
					*/
					
					uniqueIdList =
						(Hashtable) ((Hashtable) obj).get(Constants.UNIQUEIDLIST);
					uniqueIdList_column =
						(ArrayList) ((Hashtable) uniqueIdList).get(Constants.UNIQUEID_COLUMN);
					uniqueIdList_value =
						(ArrayList) ((Hashtable) uniqueIdList).get(Constants.UNIQUEID_VALUE);
					obj = ((Hashtable) obj).get(Constants.GRID);
				}

				if (obj instanceof ArrayList) {
					list = (ArrayList) obj;
					int size = list.size();
					if (!(uniqueIdList_value != null && size==uniqueIdList_value.size())) {
						System.out.println("UniqueId 1 is null");
						uniqueIdList_value =null;
					}

					for (int i = 0; i < size; i++) {

						if (this.onclick != null) {
							sBuf.append(
								" <A  href='#' onClick=' return submitAction(");
							sBuf.append(onclick + ",\"");
							sBuf.append(href);
							sBuf.append("\",\"");
							sBuf.append(recvalue);
							sBuf.append("\",\"");
							if (uniqueIdList_value != null) {
								sBuf.append(uniqueIdList_value.get(i));
							}else {
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
						dataList = (ArrayList) list.get(i);
						if (dataList.size() > 0) {
							int datasize = dataList.size();
							for (int k = 0; k < datasize; k++) {
								sBuf.append("<TD>");
								// Align according to the dates , 
								// numbers etc and it should be configureable.
								// Develop its 
								/**
								 * 1) Define the Main categories like number, dates , times   and there alingment globally
								 * 2) Define the formatting for those numbers.*   
								 */
								sBuf.append(dataList.get(k));
								sBuf.append("</TD>");
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
				return EVAL_BODY_INCLUDE;
			} catch (Exception e) {
			e.printStackTrace();
			return SKIP_BODY;
		}
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
	public void setAction(String string){
		this.action=string;
	}
		
}
