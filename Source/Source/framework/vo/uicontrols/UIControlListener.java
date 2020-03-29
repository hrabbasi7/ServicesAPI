/*
 * Created on Oct 3, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.framework.vo.uicontrols;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.jsp.JspWriter;

/**
 * @author milyas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface UIControlListener {

	public void paint(JspWriter out) throws IOException;
	public void setChecked(boolean checked);
	public boolean isChecked();
	public void setLabel(String label);
	public String getLabel();
	public void setName(String name);
	public String getName();
	public void setStyleClass(String styleClass);
	public String getStyleClass();
	public void setValue(String value);
	public String getValue();
	public void addOption(Option option);

}
