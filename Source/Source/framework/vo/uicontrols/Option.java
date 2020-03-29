/*
 * Created on Oct 3, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.framework.vo.uicontrols;

import java.io.Serializable;

/**
 * @author milyas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Option implements Serializable{

  private String value;
  private String description;

/**
 * @return
 */
public String getDescription() {
	return description;
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
public void setDescription(String string) {
	description = string;
}

/**
 * @param string
 */
public void setValue(String string) {
	value = string;
}

}
