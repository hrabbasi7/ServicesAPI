/*
 * Created on Dec 17, 2003
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

import com.i2c.cards.util.*;
import com.i2c.component.framework.taglib.controls.BaseBodyControlTag;
import javax.servlet.jsp.JspWriter;
import com.i2c.component.framework.taglib.controls.*;
import com.i2c.component.util.*;
import com.i2c.util.*;

/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SortingControl extends BaseBodyControlTag {

	/**
	 * 
	 */

	private String sortcol;
	private String sortname;
	private String title;
	private String sortdesc;
	private String imgwidth;
	private String imgheight;
	
	public SortingControl() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int doStartTag() {
		JspWriter out = pageContext.getOut();		
		StringBuffer sBuf=new StringBuffer("");
		MainSortingControl MSC = null;
		
		String sortimg = null;
		String sortOrder ="";
		try {
			MSC = (MainSortingControl) this.getParent();
			if (MSC != null) {
				if (sortname != null) {
					if (sortname.equals(MSC.getDefaultsortname())) {
						//MSC.getDefaultsortcol();
						if (MSC.getDefaultsortorder() != null)
							if (MSC
								.getDefaultsortorder()
								.equals(ComponentConstants.ASC)) {
								 sortimg = MSC.getCascorderimg();
							} else if (
								MSC.getDefaultsortorder().equals(
									ComponentConstants.DESC)) {
								 sortimg = MSC.getCdescorderimg();
							}
						//MSC.getDefaultsortname();
						// code for enable sorting ..

						sortOrder=MSC.getDefaultsortorder();
					}else if (sortname.equals(MSC.getPdefaultsortname())) {
						
						
						if (MSC.getPdefaultsortorder() != null)
							if (MSC
								.getPdefaultsortorder()
								.equals(ComponentConstants.ASC)) {
								sortimg = MSC.getAscorderimg();
							} else if (
								MSC.getPdefaultsortorder().equals(
									ComponentConstants.DESC)) {
								sortimg = MSC.getDescorderimg();
							}

						sortOrder=MSC.getPdefaultsortorder();
					}
				 	else  {
						if (MSC.getNoorderimg() != null) {
							sortimg = MSC.getNoorderimg();
						// code for disable sorting ..
						} else {
							sortimg = null;
						}

					}
				}
				/*
				 *  Start logic here for the sake of display and 
				 */
				 
				sBuf.append(" <A ");
					if (this.style != null) {
						sBuf.append(" class='" + this.style + "'");
					}
					sBuf.append(" href='#'");
					if (MSC.getOnclick() != null) {
						//submitSort(form,actv,sortname,sortorder)
						sBuf.append(
							"  onClick=' return submitSort("
								+ MSC.getOnclick()
								+ ",\""
								+ MSC.getHref()
								+ "\",\""
								+ ComponentConstants.SORTING
								+ "\",\""
								+ sortname
								+ "\",\""
								+ sortcol
								+ "\",\""
								+ sortOrder
								+ "\");'");

					} else {
					if (MSC.getHref() != null) {
						if (this.style != null) {
							sBuf.append(" class='" + this.style + "'");
						}
						sBuf.append(" href='" + MSC.getHref() + "'");
					} else {
						sBuf.append(" href='#'");
					}
				}

				if (title != null) {
					sBuf.append(" title='" + title + "'");
				}
				sBuf.append(">");

				if(sortdesc!=null) {
					sBuf.append(sortdesc);
				}
			//------------------------------------------
			if(sortimg!=null && sortimg.length()>0 ) {
			
				sBuf.append("<img  name=" + sortname);
				sBuf.append(" border='0'");
				if (this.imgwidth != null) {
					sBuf.append(" width=" + this.imgwidth);
				}
				if (this.imgheight != null) {
					sBuf.append(" height=" + this.imgheight);
				}
				sBuf.append(" src= "+Constants.IMGLOCATION);
				if (sortimg != null) {
					sBuf.append(sortimg);
				}
				sBuf.append(" >");
			}
				sBuf.append("</a>");
		}
		out.print(sBuf.toString());
		return EVAL_BODY_INCLUDE;
		} catch (Exception exp) {
			exp.printStackTrace();
			return SKIP_BODY;
		}
	
	}
	/**
	 * @return
	 */
	public String getSortcol() {
		return sortcol;
	}
	/**
	 * @param string
	 */
	public void setSortcol(String string) {
		sortcol = string;
	}
	/**
	 * @return
	 */
	public String getSortname() {
		return sortname;
	}

	/**
	 * @param string
	 */
	public void setSortname(String string) {
		sortname = string;
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
	 * @return
	 */
	public String getSortdesc() {
		return sortdesc;
	}

	/**
	 * @param string
	 */
	public void setSortdesc(String string) {
		sortdesc = string;
	}

	/**
	 * @return
	 */
	public String getImgheight() {
		return imgheight;
	}

	/**
	 * @return
	 */
	public String getImgwidth() {
		return imgwidth;
	}

	/**
	 * @param string
	 */
	public void setImgheight(String string) {
		imgheight = string;
	}

	/**
	 * @param string
	 */
	public void setImgwidth(String string) {
		imgwidth = string;
	}
	public void release() {
		 sortcol = null;
		 sortname = null;
		 title = null;
		 sortdesc = null;
		 imgwidth = null;
		 imgheight = null;
		
	}
}
