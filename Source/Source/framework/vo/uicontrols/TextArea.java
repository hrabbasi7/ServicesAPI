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
public class TextArea extends UIControl implements Serializable{

	private String label;
	private String name;
	private String value;
	private int rows;
	private int cols;
	private String styleClass;
	

	/* 
	 * paint
	 * paints the TextArea HTML Code 
	 */
	public void paint(JspWriter out) throws IOException{

		StringBuffer sb = new StringBuffer();
		sb.append("<TextArea ");
		sb.append(" rows=\""+this.rows+"\"");
		sb.append(" cols=\""+this.cols+"\"");
		if(getStyleClass() != null){
			sb.append(" class=\""+this.styleClass+"\"");
		}
		sb.append(">");
        sb.append(this.value);
		sb.append("</TextArea>");
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
	public int getCols() {
		return cols;
	}

	/**
	 * @return
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param i
	 */
	public void setCols(int i) {
		cols = i;
	}

	/**
	 * @param i
	 */
	public void setRows(int i) {
		rows = i;
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
