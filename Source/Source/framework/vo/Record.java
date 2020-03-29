/*
 * Created on Oct 3, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.framework.vo;

import java.util.Hashtable;

import com.i2c.component.framework.vo.uicontrols.UIControl;

/**
 * @author milyas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Record {

	private Hashtable controls;
	private int count;

	/**
	 * intialize
	 * Initializes the number of UIControl 's in a Record
	 * @param num
	 */
	public void intialize(int num) {
		controls = new Hashtable(num);
		count = 0;
	} //end initialize    

	/**
	 * addUIControl
	 * @param record
	 */
	public void addUIControl(String name, UIControl control) {
		controls.put(name, control);
		 
	} //end addUIControl

	/**
	 * getUIControlsCount
	 * Returns Number of Filled Locations in controls array
	 * @return
	 */
	public int getUIControlsCount() {
		return controls.size();
	} //end getUIControlsCount

	/**
	 * getUIControl
	 * Return UIControl from a specified location of controls array 
	 * @param loc
	 * @return
	 */
	public UIControl getUIControl(String name) {
			return (UIControl)controls.get(name);
	} //end getUIControl

}
