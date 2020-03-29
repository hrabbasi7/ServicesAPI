package com.i2c.services.util;

import java.util.logging.*;

/**
 * <p>Title: LogLevel: This class hold the log level information </p>
 * <p>Description: A class which defines the log level </p>
 * <p>Copyright: Copyright (c) 2006 Innovative Pvt. Ltd. Lahore</p>
 * <p>Company: I2c Inc</p>
 * @author MCP Backend Team
 * @version 1.0
 */
public class LogLevel {

  /**
   * This method returns the Level of logger againts th given log level
   * @param level String
   * @return Level
   */
  public static Level getLevel(String level) {
    int lvl = Integer.parseInt(level);
    switch (lvl) {
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

  /**
   * This method returns the log level for the given integer value.
   * @param level int
   * @return Level
   */

  public static Level getLevel(int level) {
    return getLevel(String.valueOf(level));
  }

}
