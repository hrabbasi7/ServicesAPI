package com.i2c.service.excep;

/**
 * Title:        MCP
 * Description:  This is a top level Base Exception Class
 * Copyright:    Copyright (c) 2004
 * Company:      i2c Inc.
 * @author barshad
 * @version 1.0
 */

public abstract class BaseExcep extends Exception {

    private static final String rootCause = " ROOT CAUSE->";
    protected static int errorCode = -1;

    private Throwable rootException = null;
    private String errorMessage = null;
	private String methodName = null;
	private String className = null;

    public BaseExcep() { super(); }
    public Throwable getRootException() { return rootException; }
    public int getErrorCode() { return errorCode; }
    public String getFullErrorMessage() { return errorMessage + rootCause + rootException==null ? "null" : rootException.toString(); }
	public String getErrorMessage() { return errorMessage; }
	public String getMethodName() { return methodName; }
	public String getClassName() { return className; }

	public BaseExcep(int errorcode) {
		errorCode = errorcode;
	}//end store

	public BaseExcep(int errorcode, String message) {
		super(message);
		errorCode = errorcode;
		errorMessage = message;
	}//end store value method

	public BaseExcep(Throwable excep, String message) {
		super( message + rootCause + excep.toString() );
		rootException = excep;
		errorMessage = message;
	}//end store value method


	public BaseExcep(Throwable excep) {
		super( excep.getMessage() + rootCause + excep.toString() );
		rootException = excep;
		errorMessage = excep.getMessage();
	}//end store value method


	public BaseExcep(int errorcode,Throwable excep,String message) {
		super(message + rootCause + excep.toString() );
		errorCode = errorcode;
		rootException = excep;
		errorMessage = message;
	}//end store value method

	public BaseExcep(int errorcode,Throwable excep) {
		super( excep.getMessage() + rootCause + excep.toString() );
		errorCode = errorcode;
		rootException = excep;
		errorMessage = excep.getMessage();
	}//end store value method


	public BaseExcep(Throwable excep,String message,String method) {
		super( message + rootCause + excep.toString() );
		rootException = excep;
		errorMessage = message;
		methodName = method;
	}//end store value method

	public BaseExcep(int errorcode,Throwable excep,String message,String method) {
		super( message + rootCause + excep.toString() );
		errorCode = errorcode;
		rootException = excep;
		errorMessage = message;
		methodName = method;
	}//end store value method


	public BaseExcep(Throwable excep,String message,String method,String classname) {
		super( message + rootCause + excep.toString() );
		rootException = excep;
		errorMessage = message;
		methodName = method;
		className = classname;
	}//end store value method

	public BaseExcep(int errorcode,Throwable excep,String message,String method,String classname) {
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
