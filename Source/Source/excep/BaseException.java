package com.i2c.component.excep;

/**
 * Title:        Base Exception
 * Description:  This is a top level Base Exception Class
 * Copyright:    Copyright (c) 2004
 * Company:      i2c Inc.
 * @author ILYAS
 * @version 1.0
 */

public abstract class BaseException extends Exception {

    private static final String rootCause = " ROOT CAUSE->";
    protected static String errorCode = "-1";

    private Throwable rootException = null;
    private String errorMessage = null;
	private String methodName = null;
	private String className = null;

    public BaseException() { super(); }
    public Throwable getRootException() { return rootException; }
    public String getErrorCode() { return errorCode; }
    public String getFullErrorMessage() {

      return errorMessage + rootCause + rootException==null ? "null" : rootException.toString() +"in Method ---"+className+"."+methodName ;

    }
	public String getErrorMessage() { return errorMessage; }
	public String getMethodName() { return methodName; }
	public String getClassName() { return className; }

	public BaseException(String errorcode) {
		errorCode = errorcode;
	}//end store

	public BaseException(String errorcode, String message) {
		super(message);
		errorCode = errorcode;
		errorMessage = message;
	}//end store value method

	public BaseException(Throwable excep, String message) {
		super( message + rootCause + excep.toString() );
		rootException = excep;
		errorMessage = message;
	}//end store value method


	public BaseException(Throwable excep) {
		super( excep.getMessage() + rootCause + excep.toString() );
		rootException = excep;
		errorMessage = excep.getMessage();
	}//end store value method


	public BaseException(String errorcode,Throwable excep,String message) {
		super(message + rootCause + excep.toString() );
		errorCode = errorcode;
		rootException = excep;
		errorMessage = message;
	}//end store value method

	public BaseException(String errorcode,Throwable excep) {
		super( excep.getMessage() + rootCause + excep.toString() );
		errorCode = errorcode;
		rootException = excep;
		errorMessage = excep.getMessage();
	}//end store value method


	public BaseException(Throwable excep,String message,String method) {
		super( message + rootCause + excep.toString() );
		rootException = excep;
		errorMessage = message;
		methodName = method;
	}//end store value method

	public BaseException(String errorcode,Throwable excep,String message,String method) {
		super( message + rootCause + excep.toString() );
		errorCode = errorcode;
		rootException = excep;
		errorMessage = message;
		methodName = method;
	}//end store value method


	public BaseException(Throwable excep,String message,String method,String classname) {
		super( message + rootCause + excep.toString() );
		rootException = excep;
		errorMessage = message;
		methodName = method;
		className = classname;
	}//end store value method

	public BaseException(String errorcode,Throwable excep,String message,String method,String classname) {
		super( message + rootCause + excep.toString() );
		errorCode = errorcode;
		rootException = excep;
		errorMessage = message;
		methodName = method;
		className = classname;
	}//end store value method


	public void printStackTrace() {
	  super.printStackTrace();
	  if (rootException != null) {
		System.err.println("Caused by:");
		rootException.printStackTrace();
	  }
	}

	public void printStackTrace(java.io.PrintStream ps) {
	  super.printStackTrace(ps);
	  if (rootException != null) {
		ps.println("Caused by:");
		rootException.printStackTrace(ps);
	  }
	}

	public void printStackTrace(java.io.PrintWriter pw) {
	  super.printStackTrace(pw);
	  if (rootException != null) {
		pw.println("Caused by:");
		rootException.printStackTrace(pw);
	  }
	}
}
