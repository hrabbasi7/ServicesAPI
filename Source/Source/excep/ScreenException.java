package com.i2c.component.excep;

/**
 * <p>Title: Connection Not Build Exception</p>
 * <p>Description: Deals with Client's Connection Related Issues. </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: IPL</p>
 * @author Muhammad ILYAS
 * @version 1.0
 */

public class ScreenException extends BaseException {
  public ScreenException() {
  }
  public ScreenException(String errorcode) {
           super(errorcode);
   }//end store

   public ScreenException(String errorcode, String message) {
           super(errorcode,message);
   }//end store value method

   public ScreenException(Throwable excep, String message) {
           super(excep,message);
   }//end store value method

   public ScreenException(String errorcode, Throwable excep) {
           super(errorcode,excep);
   }//end store value method

   public ScreenException(Throwable excep) {
           super(excep);
   }//end store value method

   public ScreenException(String errorcode,Throwable excep,String message) {
           super(errorcode,excep,message);
   }//end store value method


   public ScreenException(Throwable excep,String message,String method) {
           super( excep,message,method );
   }//end store value method

   public ScreenException(String errorcode,Throwable excep,String message,String method) {
           super(errorcode,excep,message,method );
   }//end store value method


   public ScreenException(Throwable excep,String message,String method,String classname) {
           super(excep,message,method,classname );
   }//end store value method

   public ScreenException(String errorcode,Throwable excep,String message,String method,String classname) {
           super(errorcode,excep,message,method,classname );
   }//end store value method




}
