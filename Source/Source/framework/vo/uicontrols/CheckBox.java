/*
 * Created on Oct 3, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.framework.vo.uicontrols;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.servlet.jsp.JspWriter;

/**
 * @author milyas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CheckBox extends UIControl implements Serializable{

	private String label;
	private String name;
	private String value;
	private String styleClass;
    private boolean checked;

	
	/* 
	 * paint
	 * paints the CheckBox HTML Code 
	 */
	public void paint(JspWriter out) throws IOException{

		StringBuffer sb = new StringBuffer();
		sb.append("<input type=\"checkbox\"");
		sb.append(" name=\""+this.name+"\"");
		sb.append(" value=\""+this.value+"\"");
		if(getStyleClass() != null){
			sb.append(" class=\""+this.styleClass+"\"");
		}
		if(isChecked()){
		sb.append(" checked");
		}
		sb.append("></input>");
		out.print(sb.toString());
		
	}//end paint

	/**
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
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
	public void setLabel(String string) {
		label = string;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param string
	 */
	public void setValue(String string) {
		value = string;
	}

	/**
	 * @return
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param b
	 */
	public void setChecked(boolean b) {
		checked = b;
	}

	/**
	 * @return
	 */
	public String getStyleClass() {
		return styleClass;
	}

	/**
	 * @param string
	 */
	public void setStyleClass(String string) {
		styleClass = string;
	}

}
