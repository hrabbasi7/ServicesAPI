/*
 * Created on Mar 10, 2006
 * Author: Muhammad ILYAS
 * Company: IPL
 * Project|Module: I2C -
 * Description: This class deals with the external communication
 * of components with the modules. 
 */
package com.i2c.component.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.i2c.component.backend.ui.NavigationBar;

public class ComponentExtUtil {

	/**
	 * This method is used to enable or disable Navigation Bar Button
	 * from any module.
	 * @param componentName
	 */
	public void enableDisableNavBarComponent(
		HttpServletRequest request,
		String componentName,
		boolean en_dis) {

		if (request == null || componentName == null) {
			return;
		}
		
		// Getting Session Object
		HttpSession session = request.getSession();

		// Getting NavigationBar Object from Session
		NavigationBar navBar =
			(NavigationBar) session.getAttribute(
				ComponentConstants.NAVIGATIONBARKEY);

		if (navBar != null) {

			if (componentName.equalsIgnoreCase(ComponentConstants.COMMIT)) {
				navBar.setCommit(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.INSERT)) {
				navBar.setInsert(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.DELETE)) {
				navBar.setDelete(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.COPY)) {
				navBar.setCopy(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.UPDATE)) {
				navBar.setUpdate(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.SEARCH)) {
				navBar.setSearch(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.FIRST)) {
				navBar.setFirst(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.LAST)) {
				navBar.setLast(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.PREVIOUS)) {
				navBar.setPrevious(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.GOTO)) {
				navBar.setGoTo(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.BROWSE)) {
				navBar.setBrowse(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.CANCEL)) {
				navBar.setCancel(en_dis);
			} else if (componentName.equalsIgnoreCase(ComponentConstants.NEXT)) {
				navBar.setNext(en_dis);
			}
		}

	} // end method	

}
