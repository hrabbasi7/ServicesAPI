package org.bouncycastle.openpgp.service;

import org.bouncycastle.openpgp.logging.*;

import java.io.*;

public class PGPLogService
{
  private static long lastUpdateTime = -1;

  PGPLogService()
  {
    try
      {
        loadInitSetting();
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Welcome to PGP Service");
      }
      catch (Exception ex)
      {
        System.out.println("Exception in loading initial Settings--->" + ex);
      }
  }
  /**
   * method for loading configuration settings
   * @throws IOException
   * @return boolean
   */
  public static boolean loadInitSetting() throws IOException
  {
    try
    {
      System.out.println("-----Loading Application Settings----");
      String filePath = System.getProperty("user.dir") + File.separator + Constants.CONFIGURATION_FILE;
      System.out.println("The Config File Path is -->" + filePath);
      if (isUpdated(filePath))
      {
        System.out.println(" Enter in Loading Values from Config File ");
        LoadProperties.loadInfo(filePath);

        System.out.println(" Constants.LOG_FILE_SIZE-" + Constants.LOG_FILE_SIZE + "_");
        System.out.println(" Constants.LOG_FILE_NO-" + Constants.LOG_FILE_NO + "_");
        System.out.println(" Constants.LOG_FILE_PATH-" + Constants.LOG_FILE_PATH + "_");
      }
      else
      {
        System.out.println("Config File is not Updated. No Need for Loading again ");
      } //end else
    }
    catch (IOException e)
    {
      System.out.println("Unable to Load the Settings. Server is unable to start/continue -->" + e);
      throw e;
    } //end catch
    return true;
  }//end load method

  /**
   * Checking the configuration file is update or not
   * @param fileName: configuration file
   * @return: changed or not
   */
  public static boolean isUpdated(String fileName)
  {
    try
    {
      File configFile = new File(fileName);
      System.out.println("Checking Config File Updation lastUpdateTime -->" + lastUpdateTime + " New Time -->" + configFile.lastModified());
      if (!configFile.exists())
        return true;

      if (lastUpdateTime != configFile.lastModified())
      {
        lastUpdateTime = configFile.lastModified();
        return true;
      } //end if
    }
    catch (Exception ex)
    {
      System.out.println("Exception in checking updation of Config file -->" + ex);
      return false;
    }
    return false;
  } //end method
}
