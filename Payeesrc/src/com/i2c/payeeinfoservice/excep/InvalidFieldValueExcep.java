/*
 * Created on Jan 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.payeeinfoservice.excep;

/**
 * @author barshad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InvalidFieldValueExcep extends DatabaseExcep {

	public InvalidFieldValueExcep(int errorcode) {
		super(errorcode);
	}//end store

	public InvalidFieldValueExcep(int errorcode, String message) {
		super(errorcode,message);
	}//end store value method

	public InvalidFieldValueExcep(Throwable excep, String message) {
		super(excep,message);
	}//end store value method

	public InvalidFieldValueExcep(int errorcode, Throwable excep) {
		super(errorcode,excep);
	}//end store value method

	public InvalidFieldValueExcep(Throwable excep) {
		super(excep);
	}//end store value method

	public InvalidFieldValueExcep(int errorcode,Throwable excep,String message) {
		super(errorcode,excep,message);
	}//end store value method

	public InvalidFieldValueExcep(Throwable excep,String message,String method) {
		super( excep,message,method );
	}//end store value method

	public InvalidFieldValueExcep(int errorcode,Throwable excep,String message,String method) {
		super(errorcode,excep,message,method );
	}//end store value method


	public InvalidFieldValueExcep(Throwable excep,String message,String method,String classname) {
		super(excep,message,method,classname );
	}//end store value method

	public InvalidFieldValueExcep(int errorcode,Throwable excep,String message,String method,String classname) {
		super(errorcode,excep,message,method,classname );
	}//end store value method



}
