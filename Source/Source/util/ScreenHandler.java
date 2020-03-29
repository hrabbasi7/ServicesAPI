///*
// * Created on Oct 6, 2005
// *
// * To change the template for this generated file go to
// * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
// */
//package com.i2c.component.util;
//import javax.servlet.http.HttpServletRequest;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import javax.xml.bind.Unmarshaller;
//
//import com.i2c.cards.util.Constants;
//import com.i2c.component.excep.BaseException;
//import com.i2c.component.excep.ScreenException;
//import com.i2c.component.framework.vo.Record;
//import com.i2c.component.framework.vo.Screen;
//import com.i2c.component.framework.vo.uicontrols.CheckBox;
//import com.i2c.component.framework.vo.uicontrols.ComboBox;
//import com.i2c.component.framework.vo.uicontrols.Option;
//import com.i2c.component.framework.vo.uicontrols.RadioButton;
//import com.i2c.component.framework.vo.uicontrols.TextArea;
//import com.i2c.component.framework.vo.uicontrols.TextField;
//import com.i2c.component.framework.vo.uicontrols.UIControl;
//import com.i2c.component.screenxmlobjects.ControlMappingsType;
//import com.i2c.component.screenxmlobjects.ControlQueryType;
//import com.i2c.component.screenxmlobjects.ControlType;
//
//import java.io.*;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.*;
//
///**
// * @author milyas
// *
// * To change the template for this generated type comment go to
// * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
// */
//public class ScreenHandler {
//
//	private static ScreenHandler obj = null;
//
//	private ScreenHandler() {
//	}
//	/**
//	 * getInstance
//	 * Returns a single instance of ScreenHandler
//	 * @return
//	 */
//	public static ScreenHandler getInstance() {
//		if (obj == null)
//			obj = new ScreenHandler();
//		return obj;
//	} //end getInstance
//
//	/**
//	* getScreen
//	* Returns the Screen Object
//	* @param rs
//	* @param rowNum
//	* @param request
//	* @return	
//	* @throws Exception
//	*/
//	private Screen getScreen(
//		String screenName,
//		int rows,
//		int navigationnum,
//		int totalRows,
//		Connection conn,
//		String qry
//		)
//		throws BaseException {
//
//		if (conn == null
//			|| screenName == null
//			|| qry == null
//			)
//			throw new ScreenException("Parameter(s) is/are Null... Not enough to create a Screen");
//		ResultSet rs = null;
//		Statement stm;
//		Screen screen = null;
//		Record record = null;
//
//		try {
//
//			stm = conn.createStatement();
//			rs = stm.executeQuery(qry);
//			
//			if(rs == null)
//			throw new ScreenException("No Record Found for Screen ["+screenName+"]");
//			
//
//			// Settings 
//			if (rows > 1) {
//				rows =	(navigationnum + rows) <= totalRows	? (navigationnum + rows): totalRows;
//			} else {
//				navigationnum = 0;
//			}
//
//			//		Intialize Screen
//			screen = new Screen();
//
//			//      Screen Population 
//			for (; navigationnum < rows; navigationnum++) {
//
//				record = getRecord(rs, navigationnum, screenName, conn);
//				screen.addRecord(record);
//			}
//
//		} catch (SQLException e) {
//			throw new ScreenException("Could not create a Screen "+e);
//		}
//
//		return screen;
//	} //end getScreen
//
//	/**
//	 * getRecord
//	 * Return a record object
//	 * @param rs
//	 * @param navigationnum
//	 * @return
//	 */
//	private Record getRecord(
//		ResultSet rs,
//		int navigationnum,
//		String screenName,
//		Connection conn)
//		throws SQLException, BaseException {
//
//		Record record = null;
//		UIControl uiControl = null;
//		com.i2c.component.screenxmlobjects.Screen screenXMLObj = getScreenXMLObj(screenName);
//		ResultSetMetaData rmdata = rs.getMetaData();
//		int colc = rmdata.getColumnCount();
//		
//		if(navigationnum == 0 ){
//		rs.next();
//		}else{
//		rs.absolute(navigationnum);
//		}
//		
//		for (int col = 1; col <= colc; col++) {
//			 uiControl = getUIControl(rmdata.getColumnName(col),screenXMLObj, rs, conn);
//		     record.addUIControl(rmdata.getColumnName(col),uiControl);
//		}
//
//		return record;
//	}//end getRecord()
//	
//	
//	/**
//	 * Returns UIControl 
//	 * Defined against column name in Configuration File
//	 * @param screen
//	 * @return
//	 */
//	private UIControl getUIControl(
//	    String name, 
//		com.i2c.component.screenxmlobjects.Screen screen, ResultSet rs, Connection conn) throws SQLException {
//	
//		UIControl uiControl = null;
//		List controls = screen.getControlMappings().getControl();
//		ControlType controlType = getControlType(controls, name);
//		
//		if(controlType.getType().trim().equals("TextField")){
//			uiControl = new TextField();
//		}else if(controlType.getType().trim().equals("TextArea")){
//			uiControl = new TextArea();
//		}else if(controlType.getType().trim().equals("RadioButton")){
//			uiControl = new RadioButton();
//		}else if(controlType.getType().trim().equals("CheckBox")){
//			uiControl = new CheckBox();
//		}else if(controlType.getType().trim().equals("ComboBox")){
//			uiControl = new ComboBox();
//			addComboOptions(uiControl,controlType.getControlQuery(), conn);
//		}
//
//		// Setting Common Values
//		uiControl.setName(name);
//		uiControl.setValue(rs.getString(name));		
//
//		return uiControl;
//	}//end getUIControl
//
//	/**
//	 * addComboOptions
//	 * @param uiControl
//	 * @param string
//	 */
//	private void addComboOptions(UIControl uiControl, ControlQueryType controlQuery, Connection conn) throws SQLException{
//	
//		PreparedStatement pstm = conn.prepareStatement(controlQuery.getQuery());
//		ResultSet rs = pstm.executeQuery();
//		Option option = null;
//		
//		while(rs.next()){
//			option = new Option();
//			option.setValue(rs.getString(controlQuery.getColumn1()));
//			option.setDescription(rs.getString(controlQuery.getColumn2()));
//			uiControl.addOption(option);
//		}
//		
//	}//end addComboOptions
//	
//	/**
//	 * @param controls
//	 * @param name
//	 * @return
//	 */
//	private ControlType getControlType(List controls, String name) {
//		
//		int count = 0;
//		ControlType controlType = null;
//		while(count < controls.size()){	
//		controlType = (ControlType)controls.get(count);
//		if(controlType.getName().trim().equals(name)){
//		break;
//		}
//		}//end while
//		
//		return controlType;
//	}//end getControlType()
//	
//	
//	/**
//	 * getScreenXMLObj
//	 * Returns the Screen XML Object by getting the Screen
//	 * XML Config File agaist Screen Name
//	 * @param screenName
//	 * @return
//	 */
//	private com.i2c.component.screenxmlobjects.Screen getScreenXMLObj(
//		String screenName)
//		throws BaseException {
//
//		FileInputStream fis = null;
//		JAXBContext jc = null;
//		Unmarshaller uMarshaller = null;
//		com.i2c.component.screenxmlobjects.Screen screen = null;
//		
//
//		String screenXmlConfigFilePath =
//			Constants.CONTEXTREALPATH + "/screens/" + screenName + ".xml";
//		File file = new File(screenXmlConfigFilePath);
//
//		if (!file.exists()) {
//			throw new ScreenException(
//				"No Configuration File for the Screen Name ["
//					+ screenName
//					+ "]");
//		}
//		
//		try{
//		// create a JAXBContext capable of handling classes generated into
//		// the primer.po package
//		jc = JAXBContext.newInstance("com.i2c.component.screenxmlobjects");
//
//		// create an Unmarshaller
//		uMarshaller = jc.createUnmarshaller();
//		uMarshaller.setValidating(true);
//
//		fis = new FileInputStream(file);
//		screen =	(com.i2c.component.screenxmlobjects.Screen) uMarshaller.unmarshal(fis);
//
//		}catch(JAXBException jaxe){
//		throw new ScreenException("Unable to create XML Screen Object of Screen ["+screenName+"]"+jaxe);
//		}catch(IOException ioe){
//			throw new ScreenException("Unable to load XML Screen Conf File of Screen ["+screenName+"]"+ioe);
//		}
//
//		return screen;
//	} //end getScreenXMLObj
//
//}
