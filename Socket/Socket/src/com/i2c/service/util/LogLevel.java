/*
 * Created on Sep 8, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.i2c.service.util;

import java.util.logging.*;

/**
 * @author srashid
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LogLevel {
	public static Level getLevel(String level) {
		int lvl = Integer.parseInt(level);
		/*
		 * 	SEVERE (highest value)
			WARNING
			INFO
			CONFIG
			FINE
			FINER
			FINEST (lowest value)
		 */
		switch(lvl){
			case 1:
				return Level.SEVERE;
			case 2:
				return Level.WARNING;
			case 3:
				return Level.INFO;
			case 4:
				return Level.CONFIG;
			case 5:
				return Level.FINE;
			case 6:
				return Level.FINER;
			case 7:
				return Level.FINEST;
			default:
				return Level.FINE;
		}
	}

	public static Level getLevel(int level){
		return getLevel(String.valueOf(level));
	}

}
