package com.i2c.services.exceptions;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class InvalidConfigException extends Exception {

    public InvalidConfigException(String msg) {
        super(msg);
    }




    public InvalidConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }




    public InvalidConfigException(Throwable cause) {
        super(cause);
    }

}// end InvalidConfigException
