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
public class TextField extends UIControl implements Serializable{
    
    private String label;
    private String name;
    private String value;
    private String styleClass;


	/**
	 * paint
	 * paints the TextField HTML Code
	 */
	public void paint(JspWriter out) throws IOException{
	 
	 StringBuffer sb = new StringBuffer();
	 sb.append("<input type=\"text\"");
	 sb.append(" name=\""+this.name+"\"");
	 sb.append(" value=\""+this.value+"\"");
	 if(getStyleClass() != null){
		 sb.append(" class=\""+this.styleClass+"\"");
	 }
	 sb.append("></input>");
	 out.print(sb.toString());
	 
	}

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
