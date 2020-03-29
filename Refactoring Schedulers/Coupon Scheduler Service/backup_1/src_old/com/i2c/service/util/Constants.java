package com.i2c.service.util;

import com.i2c.schedulerframework.util.BaseConstants;

/**
 * <p>Title: Coupon Scheduler</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright i2cinc (c) 2003</p>
 * <p>Company: I2CINC</p>
 * @author Haroon ur Rashid Abbasi
 * @version 1.0
 */

public class Constants extends BaseConstants {

  public static String COUPON_GENERATION_SERVICE="gen_coupon";
  public static String USE_COUPON_GEN="USE_COUPON_GEN";

  public static  long INCREAMENT_EMAIL_COUNTER = 10;
  public static  long MAX_LIMIT_SEND_EMAIL_COUNTER = 3;
  public static long COUPON_GEN_SLEEP_TIME = 0;

  public static String LOCAL_CONTEXT_NAME = "com.i2c.service.coupon";

}
