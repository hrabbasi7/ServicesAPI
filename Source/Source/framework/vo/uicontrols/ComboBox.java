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
import java.util.Hashtable;

import javax.servlet.jsp.JspWriter;

/**
 * @author milyas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ComboBox extends UIControl implements Serializable{

	private String label;
	private String name;
	private String styleClass;
	private Option[] options;
	private int count;
	
	
	/**
	 * initialize
	 * initializes the Options of ComboBox
	 * @param num
	 */
	public void initialize(int num){
	
	options = new Option[num];
	count = 0;
	}//end initialize
	
	/**
	 * addOption
	 * @param option
	 */
	public void addOption(Option option){
		options[count++] = option;
	}//end addOption


	/*
	 * paint
	 * paints the ComboBox HTML Code
	 */
	public void paint(JspWriter out) throws IOException{
      
		StringBuffer sb = new StringBuffer();
		sb.append("<select");
		sb.append(" name=\""+this.name+"\"");
		if(getStyleClass() != null){
			sb.append(" class=\""+this.styleClass+"\"");
		}
		sb.append(">");
		

		//---Adding Options
		int count = 0;
		Option option = null;
		while(count < options.length){
		option = options[count];
		sb.append("<option value=\""+option.getValue()+"\">");
		sb.append(option.getDescription());
		sb.append("</option>");
		count++;
		}//end while
				
		sb.append("</select>");
		
		
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
