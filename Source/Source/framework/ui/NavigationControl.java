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

//import com.i2c.ach.util.Constants;
import com.i2c.cards.util.*;
import com.i2c.component.backend.ui.NavigationBar;
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
public class NavigationControl extends BaseBodyControlTag {

	/**
	 *
	 */
	public NavigationControl() {
		super();
		// TODO Auto-generated constructor stub
	}

	private String href;
	private String title;
	private String imgname ;
	private String imgheight;
	private String imgborder;
	private String imglocation;
	private String imgsrcname;
	private String imgwidth;
	private String nvname;
	private String cancel;
	private String recvalue;
	private String rm;
	private String size;
	private String maxlength;
	private String centerstr;
	public int doStartTag() {
		StringBuffer unlockHiddenFields = new StringBuffer();
		JspWriter out = pageContext.getOut();
		boolean unlock = false;
		String scName=null;
		String gtBtn=null;
		ValueContainerControl	VC=null;
		boolean displayButton = true;
		try {
			if(nvname.toUpperCase().indexOf("UNLOCK") >=0){
				System.out.println("LOCKING-->navigationcontrol:::=ShowUnlock : request="+pageContext.getRequest().
						  getAttribute("ShowUnlock"));
				if(ComponentsUtil.fixNull((String)pageContext.getRequest().
						  getAttribute("ShowUnlock")).equals("NO")){
					displayButton = false;
				}else{
					System.out.println("LOCKING-->navigationcontrol:::=isUnlock : request="+pageContext.getRequest().
							  getAttribute("isUnlock"));
					if(ComponentsUtil.fixNull((String)pageContext.getRequest().
										  getAttribute("isUnlock")).equals("YES")){
						nvname = "lock";
					}
				}
			}
			if(nvname.toUpperCase().indexOf("UNLOCK") >=0){
				unlock = true;
			}
			// Code added By Maqsood Shahzad
			if(nvname.toUpperCase().indexOf("COMMIT") >=0){
				//pageContext.getRequest().setAttribute("OKONCLICK",href);
				out.print("<input type = 'hidden' name = 'okclick' value = '"+href+"' >");
			}
			if(nvname.toUpperCase().indexOf("NEXT") >=0)
			{
				out.println("<input type = 'hidden' name = 'imageslocation' value = '"+Constants.NVIMGLOCATION+"' >");
				out.print("<input type = 'hidden' name = '"+Constants.PREVIOUS_ACTION_VAL+"'>");
			}
			// End of Code added by Maqsood Shahzad
			VC = (ValueContainerControl) this.getParent();
				if(VC!=null){
					scName= VC.getValueContanier();
				}
		}catch(Exception exp){
			exp.toString();
		}
		try {

			StringBuffer sBuf = new StringBuffer();
			StringBuffer functionsBuf = new StringBuffer("");

			if(this.onclick!=null )
			functionsBuf.append(" onClick ="+onclick);
			if( this.onchange!=null){
				functionsBuf.append(" onChange="+onchange);
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

			if(nvname != null && nvname.trim().toLowerCase().indexOf("update")==-1){

			if (nvname != null) {
				gtBtn = nvname;
				recvalue=(String)ComponentConstants.getAction(nvname.toUpperCase());
				title=	(String)ComponentConstants.getActionTitle(nvname.toUpperCase());
				if(nvname.equalsIgnoreCase("COMMIT")){
					VC.setFormName(this.onclick);
					VC.setActionName(this.href);
					VC.setActionMode(this.recvalue);
				}
			}
			if(scName!=null) {
				//nvname = scName+nvname;
			}
			if (nvname != null) {
				//String nvEnable =	(String) this.pageContext.getSession().getAttribute(nvname + "_ENABLED");
				// Changed 08/03/2006
				// To have  asingle Object of Navigation Bar in Session
				String nvEnable = null;
				NavigationBar nvBar =	(NavigationBar) this.pageContext.getSession().getAttribute(scName+ComponentConstants.NAVIGATIONBARKEY);
				if(nvBar != null){
					nvEnable = (nvBar.isEnabled(nvname))?"Y":"N";
				}


				String en_dis="";
				//System.out.println(nvname +" =  " + nvEnable);
				//---------------------------------
				if (nvEnable != null && nvEnable.equals("Y")) {
					if (this.rm != null && rm.equals("TRUE")) {
						this.pageContext.getSession().removeAttribute(nvname + "_ENABLED");
					}
					imgname= "ENABLED";
					//System.out.println(nvname);
					if(nvBar.isEnabled(nvname)== true)
						en_dis=ComponentConstants.ENABLE;
					else
						en_dis=ComponentConstants.DISABLE;
					//en_dis=(String)this.pageContext.getSession().getAttribute(nvname);
					if( en_dis==null){
						 en_dis=ComponentConstants.ENABLE;
					}else{
						this.pageContext.getSession().removeAttribute(nvname);
					}

					if(gtBtn!=null && gtBtn.trim().equalsIgnoreCase(ComponentConstants.GOTO)) {
						sBuf.append("<input type='text' ");
							sBuf.append(" name='recgoto'");
						if (this.style != null) {
							sBuf.append(" class='" + this.style + "'");
						}
						if (this.size != null) {
							sBuf.append(" size='" + this.size + "'");
						}
						if (this.maxlength!= null) {
							sBuf.append(" maxlength='" + this.maxlength + "'");
						}
							sBuf.append(functionsBuf.toString());
						if(!en_dis.equals(ComponentConstants.ENABLE) )
							sBuf.append(" disabled ");
							sBuf.append(" >");
							if( centerstr!=null ) {
								sBuf.append(centerstr);
							}
					}
					} else {
						imgname = "DISABLED";
				}
				if(gtBtn!=null && gtBtn.trim().equalsIgnoreCase(ComponentConstants.GOTO)) {
					if(!en_dis.equals(ComponentConstants.ENABLE) ) {

					sBuf.append("  <input type='text' ");
						sBuf.append(" name='" +gtBtn+ "'");
					if (this.style != null) {
						sBuf.append(" class='" + this.style + "'");
					}
					if (this.size != null) {
						sBuf.append(" size='" + this.size + "'");
					}
					if (this.maxlength!= null) {
						sBuf.append(" maxlength='" + this.maxlength + "'");
					}
						sBuf.append(functionsBuf.toString());
						sBuf.append(" disabled ");
						sBuf.append(" > ");
						if( centerstr!=null ) {
							sBuf.append(centerstr);
						}
					}
				}
				//------------------------------------------
				if( nvEnable != null  || !en_dis.equals(ComponentConstants.DISABLE)){
				if (this.nvname != null) {
					sBuf.append("<img  name=" + scName+nvname);
					sBuf.append(" border='0'");
					if (this.imgwidth != null) {
						sBuf.append(" width=" + this.imgwidth);


					}else {
						sBuf.append(" width=" + Constants.getActionWidth(this.recvalue));
					}
					if (this.imgheight != null) {
						sBuf.append(" height=" + this.imgheight);
					}else{
						sBuf.append(" height=" + Constants.getActionHeight(this.recvalue));
					}
					if (nvEnable != null && nvEnable.equals("Y") || en_dis.equals(ComponentConstants.ENABLE)) {
						if ((this.cancel != null && cancel.equals("TRUE"))) {
							String str1 =(String)this.pageContext.getSession().getAttribute(scName+ Constants.CANCELCOUNTER);
							if(str1==null){
								str1="-1";
							}else{

								str1 = "-"+str1;
							}
							sBuf.append(" href='#' onClick='javascript:history.go("+str1+");' ");
							sBuf.append(" onMouseOut=\"MM_swapImgRestore()\" ");
							sBuf.append(" onMouseOver=\"MM_swapImage('"+nvname+"','','"+Constants.NVIMGLOCATION+Constants.getImg(recvalue+"_OVER") +"',1)\"");


						} else if (this.onclick != null) {
							if (this.style != null) {
								sBuf.append(" class='" + this.style + "'");
							}
							sBuf.append(" onMouseOut=\"MM_swapImgRestore()\" ");
							sBuf.append(" onMouseOver=\"MM_swapImage('"+nvname+"','','"+Constants.NVIMGLOCATION+Constants.getImg(recvalue+"_OVER") +"',1)\"");

							if (this.recvalue != null) {
								if(this.recvalue.equalsIgnoreCase("del")){
									sBuf.append(
										"  onClick=' return validateDel("
											+ onclick
											+ ",\""
											+ href
											+ "\",\""
											+ recvalue
											+ "\");'");
								}
								else{
									if(unlock){
										unlockHiddenFields.append("<input type='hidden' " +
												"name='unlockFormName' value='"+onclick+"'>");
										unlockHiddenFields.append("<input type='hidden' " +
												"name='unlockAction' value='"+href+"'>");
										sBuf.append(/*showUnlockDiv('unlockDiv','unlockId')*/
												"  onClick=' showUnlockDiv("
													+"\"unlockDiv\""
													+ ",\"unlockId"
													+ "\");'");
										sBuf.append("id='unlockId'");
									}else{
										sBuf.append(
											"  onClick=' return submitForm("
												+ onclick
												+ ",\""
												+ href
												+ "\",\""
												+ recvalue
												+ "\");'");
									}
								}
							} else {
								sBuf.append(
									"  onClick=' return submitForm("
										+ onclick
										+ ",\""
										+ href
										+ "\",\"  \");'");
							}

						}
						if (title != null) {
							sBuf.append(" title='" + title + "'");
						}
						sBuf.append(" style=\"cursor:pointer\"");
					}
					sBuf.append(" src= "+Constants.NVIMGLOCATION);

					if (this.recvalue != null) {
//						if(recvalue.equals("unlock")&&ComponentsUtil.fixNull((String)pageContext.getRequest().getAttribute("isUnlock")).equals("YES")){
//							recvalue = "lock";
//							imgname = "ENABLED";
//						}
						sBuf.append(
							Constants.getImg(this.recvalue + "_" + imgname));
					}
					sBuf.append(" >");
				}
				}
			}
			if(displayButton)
				out.print(sBuf.toString()+unlockHiddenFields.toString());
			}
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
	public void setNvname(String nvname) {
		this.nvname = nvname;
	}
	/*
	public void setOnclick(String onclick) {
	  this.onclick = onclick;
	}
	*/
	public void setTitle(String title) {
		this.title = title;
	}
	public void setCancel(String value) {
		cancel = value;
	}
	public void setRecvalue(String value) {
		this.recvalue = value;
	}
	public void setRm(String value) {
		this.rm = value;
	}
	public void release() {
		href = null;
		title = null;
		imgheight = null;
		imgborder = null;
		imglocation = null;
		imgsrcname = null;
		imgwidth = null;
		nvname = null;
		cancel = null;
		recvalue = null;
		imgname=null;
	}
	/**
	 * @param string
	 */
	public void setMaxlength(String string) {
		maxlength = string;
	}

	/**
	 * @param string
	 */
	public void setSize(String string) {
		size = string;
	}

	/**
	 * @param string
	 */
	public void setCenterstr(String string) {
		centerstr = string;
	}

}
