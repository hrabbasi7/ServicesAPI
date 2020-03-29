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
 * @author sbukhari
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GridUpdateControl extends BaseBodyControlTag {

	public GridUpdateControl() {
		super();
	}
	private String title;
	private String value;
	private String type;

	public int doStartTag() {
		JspWriter out = pageContext.getOut();
		StringBuilder sbuilder = new StringBuilder();
		try {
			sbuilder.append("<input ");

			if(this.type != null){
				sbuilder.append(" type=\""+this.type+"\"");
			}

			if(this.name != null){
				sbuilder.append(" name=\""+this.name+"\"");
			}
			if(this.value != null){
				sbuilder.append(" value=\""+this.value+"\"");
			}

			if(this.onclick != null){
				sbuilder.append(" onclick=\""+this.onclick+"\"");
			}
			System.out.println("LOCKING-->GridUpdateControl:::=isUnlock="+pageContext.getRequest().
					  getAttribute("isUnlock"));
			System.out.println("LOCKING-->GridUpdateControl:::=ShowUnlock="+pageContext.getRequest().
					  getAttribute("ShowUnlock"));
			if(ComponentsUtil.fixNull((String)pageContext.getRequest().
					  getAttribute("isUnlock")).equals("NO") &&
					  !ComponentsUtil.fixNull(
							(String)pageContext.getRequest().
							  getAttribute("ShowUnlock")).
							  	equals("NO")){
				sbuilder.append(" DISABLED = true ");
			}
			sbuilder.append("/>");
			out.print(sbuilder.toString());
			return EVAL_BODY_INCLUDE;
		} catch (Exception e) {
			e.printStackTrace();
			return SKIP_BODY;
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
