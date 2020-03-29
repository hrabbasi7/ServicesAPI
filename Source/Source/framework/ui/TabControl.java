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
public class TabControl extends BaseBodyControlTag {

	/**
	 * 
	 */
	public TabControl() {
		super();
		// TODO Auto-generated constructor stub
	}
	private String tabname;
	private String href;
	private String title;
//--------------------------	
	private String imgSelR;
	private String imgSelL;
	private String imgDisR;
	private String imgDisL;
	private String imgEnR;
	private String imgEnL;
//--------------------------	
	private String imgname;
	private String imgDisable;
	private String imgEnable;
	private String imgSelect;
	private String imgheight;
	private String imgborder;
	private String imglocation;
	private String imgsrcname;
	private String defalutEnable;
	private String defaultSelect;
	private String imgwidth;
	//private String action;
	private String autoImage;
	private String displayText;
	private String displayTextWidth;
	
	// Added By ILYAS [06/12/2005]
	private String storeCriteriaInSession;
	
	public int doStartTag() {
		JspWriter out = pageContext.getOut();
		String pTab=null;
		
		
				
		try {
			StringBuffer sBuf = new StringBuffer();
			MainTabControl mTab =null;
			String tabStatus=null;
			try{
				mTab = (MainTabControl)this.getParent();
				pTab = mTab.getTabname();

			}catch(Exception exp ) {
				exp.printStackTrace();
			}
			if( mTab!=null && mTab.getSeletedTab() !=null && mTab.getSeletedTab().equalsIgnoreCase(tabname)) {
				tabStatus = ComponentConstants.SELECT;
			}else if (this.tabname != null) {
				tabStatus =
					(String) this.pageContext.getSession().getAttribute(
						tabname);
			}
				{
				if (tabStatus!=null&&tabStatus.equals(ComponentConstants.SELECT)) {
					imgname = this.imgSelect;
					//if(mTab!=null){
					//	mTab.setSeletedTab(this.tabname);
					//}
				}
				else if (tabStatus!=null&&tabStatus.equals(ComponentConstants.ENABLE)) {
					imgname = this.imgEnable;
				}
				else if (tabStatus!=null&&tabStatus.equals(ComponentConstants.DISABLE)) {
						imgname = this.imgDisable;
				}else 	if (this.defaultSelect != null
					&& defaultSelect.equals(Constants.YES)) {
					tabStatus = ComponentConstants.SELECT;
					imgname = this.imgSelect;
				}
				else if (defalutEnable != null
					&& defalutEnable.equals(Constants.YES)) {
					tabStatus = ComponentConstants.ENABLE;
					imgname = this.imgEnable;
				}
				if (tabStatus == null){
					tabStatus = ComponentConstants.DISABLE;
					imgname = this.imgDisable;
				}
				
			if (!tabStatus.equals(ComponentConstants.DISABLE) && (this.autoImage==null)) 
			{				
					sBuf.append(" <A ");
				if (this.onclick != null) {
					if (this.style != null) {
						sBuf.append(" class='" + this.style + "'");
					}

					sBuf.append(" href='#'");
					 
//					if(mTab.getScreenName()!= null){
//						if((String)this.pageContext.getSession().getAttribute(mTab.getScreenName()+Constants.CURRENT_ACTION) != null) {
//							this.action = (String)this.pageContext.getSession().getAttribute(mTab.getScreenName()+Constants.CURRENT_ACTION);
//						}
//					}else { if(this.action != null)  action = ComponentConstants.getAction(action); } 
					if (this.action != null) {
						action = ComponentConstants.getAction(action);						
							
						if(pTab!=null) 
						sBuf.append(
							"  onClick=' return submitTab("
								+ onclick
								+ ",\""
								+ href
								+ "\",\""
								+ action
								+ "\",\""
								+ tabname
								+ "\",\""
								+ pTab 
								+ "\",\""
								+ storeCriteriaInSession
								+ "\");'");
					} else {
						if(pTab!=null)
						sBuf.append(
							"  onClick=' return submitTab("
								+ onclick
								+ ",\""
								+ href
								+ "\",\" "   
								+ "\",\""
								+ tabname
								+ "\",\""
								+ pTab
								+ "\",\""
								+ storeCriteriaInSession
								+ "\");'");
					}

				} else {
					if (href != null) {
						if (this.style != null) {
							sBuf.append(" class='" + this.style + "'");
						}
						sBuf.append(" href='" + href + "'");
					} else {
						sBuf.append(" href='#'");
					}
				}

				if (title != null) {
					sBuf.append(" title='" + title + "'");
				}
				sBuf.append(">");
			}
			//------------------------------------------
			if (this.imgname != null && this.autoImage==null) {
				sBuf.append("<img  name=" + tabname);
				if(this.border!=null) {
					sBuf.append(" border='"+border+"'");
				}
				else {
					sBuf.append(" border='0'");
				}
				if (this.width != null) {
					sBuf.append(" width=" + this.width);
				}
				if (this.height != null) {
					sBuf.append(" height=" + this.height);
				}
				sBuf.append(" src= "+ Constants.IMGLOCATION+imgname);
				sBuf.append(" >");
			}
			
			//My work starts here.
			if(this.autoImage!=null && this.href!=null && this.title!=null)
			{
				StringBuffer stringBuffer = new StringBuffer("");
				if (this.action != null) {
					action = ComponentConstants.getAction(action);						
							
					if(pTab!=null) 
					stringBuffer.append(
						"  onClick=' return submitTab("
							+ onclick
							+ ",\""
							+ href
							+ "\",\""
							+ action
							+ "\",\""
							+ tabname
							+ "\",\""
							+ pTab 
							+ "\",\""
							+ storeCriteriaInSession
							+ "\");'");
				} else {
					if(pTab!=null)
					stringBuffer.append(
						"  onClick=' return submitTab("
							+ onclick
							+ ",\""
							+ href
							+ "\",\" "   
							+ "\",\""
							+ tabname
							+ "\",\""
							+ pTab
							+ "\",\""
							+ storeCriteriaInSession
							+ "\");'");
				}
				
				System.out.println(" **************** tabStatus = "+tabStatus);
					sBuf.append("<td>" +
									"<table cellpadding='0' cellspacing='0' border='0' >" +
										"<tr><td>");
									//Making the first image.
									if(tabStatus!=null && tabStatus.trim().equalsIgnoreCase(ComponentConstants.ENABLE))
									{
										sBuf.append("<A href='#' "+ stringBuffer + " style='text-decoration:none' class='tabTableHead' title='"+this.title+"'><img border='0' src=" + Constants.IMGLOCATION + "tabs/general/bL2.jpg></A></td>");
									}
									else if(tabStatus!=null && tabStatus.trim().equalsIgnoreCase(ComponentConstants.DISABLE))
									{
										sBuf.append("<img border='0' src=" + Constants.IMGLOCATION + "tabs/general/bL2.jpg></img></td>");
									}
									else if(tabStatus!=null && tabStatus.trim().equalsIgnoreCase(ComponentConstants.SELECT))
									{
										sBuf.append("<A href='#' "+ stringBuffer + " style='text-decoration:none' class='tabTableHead' title='"+this.title+"'><img border='0' src=" + Constants.IMGLOCATION + "tabs/general/bL1.jpg></A></td>");
									}
									
									//Making the text.
									if(tabStatus!=null && tabStatus.trim().equalsIgnoreCase(ComponentConstants.SELECT))
									{
										sBuf.append("<td class='tabTableHead'><A href='#' "+ stringBuffer +" style='text-decoration:none' title='"+ this.title +"' ><FONT color='white'> <b>"+this.displayText+"</b></FONT></A></td>");
									}
									else if(tabStatus!=null && tabStatus.trim().equalsIgnoreCase(ComponentConstants.ENABLE))
									{
										sBuf.append("<td bgcolor='#FEE39E'><A href='#' "+ stringBuffer +" style='text-decoration:none' title='"+ this.title +"' ><FONT color='black'> <b>"+this.displayText+"</b></FONT></A></td>");
									}
									else if(tabStatus!=null && tabStatus.trim().equalsIgnoreCase(ComponentConstants.DISABLE))
									{
										sBuf.append("<td bgcolor='#FEE39E'><FONT color='gray'> <b>"+this.displayText+"</b></FONT></td>");
									}									
											
									sBuf.append("<td>");									
									
									//Making the second image.
									if(tabStatus!=null && tabStatus.trim().equalsIgnoreCase(ComponentConstants.ENABLE))
									{
										sBuf.append("<A href='#' "+ stringBuffer +" style='text-decoration:none' class='tabTableHead' title='"+this.title+"'><img border='0' src=" + Constants.IMGLOCATION + "tabs/general/bR2.jpg></A></td>");
									}
									else if(tabStatus!=null && tabStatus.trim().equalsIgnoreCase(ComponentConstants.DISABLE))
									{
										sBuf.append("<img border='0' src=" + Constants.IMGLOCATION + "tabs/general/bR2.jpg></img></td>");
									}
									else if(tabStatus!=null && tabStatus.trim().equalsIgnoreCase(ComponentConstants.SELECT))
									{
										sBuf.append("<A href='#' "+ stringBuffer +" style='text-decoration:none' class='tabTableHead' title='"+this.title+"'><img border='0' src=" + Constants.IMGLOCATION + "tabs/general/bR1.jpg></A></td>");
									}
									

									sBuf.append("</tr>" +
									"</table>"+
								"</td>");
			}
			//My work ends here.
			
			if (!tabStatus.equals(ComponentConstants.DISABLE) && (this.autoImage == null) ) {
				sBuf.append("</a>");
			}
		}

		out.print(sBuf.toString());
		return EVAL_BODY_INCLUDE;
	} catch (Exception e) {
		e.printStackTrace();
		return SKIP_BODY;
	}
}

public void setHref(String href) {
	this.href = href;
}
public void setImgborder(String imgborder) {
	this.imgborder = imgborder;
}
public void setImgheight(String imgheight) {
	this.imgheight = imgheight;
}
public void setImglocation(String imgsrc) {
	this.imglocation = imgsrc;
}
public void setImgsrcname(String imgsrcname) {
	this.imgsrcname = imgsrcname;
}
public void setImgwidth(String imgwidth) {
	this.imgwidth = imgwidth;
}
/*
public void setOnclick(String onclick) {
  this.onclick = onclick;
}
*/
public void setTitle(String title) {
	this.title = title;
}
public void release() {
	href = null;
	title = null;
	imgheight = null;
	imgborder = null;
	imglocation = null;
	imgsrcname = null;
	imgwidth = null;
	action = null;
	imgname = null;
}

/**
 * @param string
 */
public void setImgDisable(String string) {
	imgDisable = string;
}

/**
 * @param string
 */
public void setImgEnable(String string) {
	imgEnable = string;
}

/**
 * @param string
 */
public void setImgSelect(String string) {
	imgSelect = string;
}

/**
 * @param string
 */
public void setTabname(String string) {
	tabname = string;
}

/**
 * @param string
 */
public void setDefalutEnable(String string) {
	defalutEnable = string;
}
public void setAction(String string){
	this.action = string;
}
	/**
	 * @param string
	 */
	public void setDefaultSelect(String string) {
		defaultSelect = string;
	}

	/**
	 * @return
	 */
	public String getAutoImage() {
		return autoImage;
	}

	/**
	 * @param string
	 */
	public void setAutoImage(String string) {
		autoImage = string;
	}

	/**
	 * @return
	 */
	public String getDisplayText() {
		return displayText;
	}

	/**
	 * @param string
	 */
	public void setDisplayText(String string) {
		displayText = string;
	}

	/**
	 * @return
	 */
	public String getDisplayTextWidth() {
		return displayTextWidth;
	}

	/**
	 * @param string
	 */
	public void setDisplayTextWidth(String string) {
		displayTextWidth = string;
	}

	/**
	 * @return
	 */
	public String getStoreCriteriaInSession() {
		return storeCriteriaInSession;
	}

	/**
	 * @param string
	 */
	public void setStoreCriteriaInSession(String string) {
		storeCriteriaInSession = string;
	}

}
