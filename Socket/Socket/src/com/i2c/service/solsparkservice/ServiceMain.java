package com.i2c.service.solsparkservice;
import com.i2c.service.util.*;
import java.io.*;
import java.net.*;
import com.i2c.services.ServicesHandler;
import com.i2c.services.ServicesRequestObj;
import com.i2c.services.ServicesResponseObj;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ServiceMain {

  private static long lastUpdateTime = -1;
  private static ServerSocket server = null;

  public static void main(String[] args) {
    try {
      //Load Application setting from the Config file
      loadInitSetting();
      initHSMByServiceAPI();
      //Listening on a socket
      server = new ServerSocket(Constants.PORT_NO_VALUE);
      ServiceThread requestThread = null;
      while (true) {
        try {
          //Accept the request

          Socket socket = server.accept();
        //Load configuration file settings
          loadInitSetting();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"The Requesting Client IP is -->"+socket.getInetAddress().getHostAddress());
          //Reading the Client socket
          requestThread = new ServiceThread(socket);
          //Check if the request from the Valid IP address
          if (Constants.CLIENT_IP_VLAUE.indexOf(socket.getInetAddress().getHostAddress()) > -1) {
            //Call the thread to handle the request
            requestThread.start();
//            requestThread.join();
          } else { //Incase of failure
            requestThread.sendClientResponse(Constants.ERROR_AUTHORIZATION_ID + Constants.MESSAGE_DELIMETER_VALUE + Constants.ERROR_AUTHORIZATION_MESSAGE);
            socket.close();
          }//end else
        } catch (IOException e) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"IO Exception in handling request -->" + e);
        } catch (Throwable e) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Exception in handling request -->" + e);
        } //end catch
      } //end while
    } catch (Throwable e) {
        System.out.println("Exception in handling request -->" + e);
    } finally {
      try {
        if (server != null) {
          server.close();
          server = null;
        } //end if
      }
      catch (Throwable e) {
        System.out.println("Exception in closing server -->" + e);
      } //end catch
      System.out.println("Server is down now!");
      System.exit(0); // termination by force
    }
  }//end main

  /*********************************************************************
   * This method is only to use SocketService so that HSMService can be
   * initialized in main method of socket service
   * @throws Exception
   */
  private static void initHSMByServiceAPI() throws Exception {
    System.out.println("Initializing HSM Pool by sending dummy request for data encryption");
    ServicesRequestObj reqObj = new ServicesRequestObj();
    reqObj.setDecryptedData("test");
    ServicesResponseObj respObj = ServicesHandler.encryptData(reqObj);
    System.out.println("Result-->" + respObj.getEncryptedData());
  } // end initHSMByServiceAPI()

  /**
   * Method for loading the initial settings
   * @return: true or false
   * @throws IOException
   */
  public static boolean loadInitSetting()
      throws IOException {
    boolean firstTime = false;

    try {
      System.out.println("Load Application Settings ");
      String filePath = System.getProperty("user.dir") + File.separator + Constants.CONFIGURATION_FILE;
      System.out.println("The Config File Path is -->"+filePath);

      if (isUpdated(filePath)) {
            System.out.println(" Enter in Loading Values from Config File ");
            LoadProperties.loadInfo(filePath);

            System.out.println(" Constants.DB_INSTANCE_CONNECTION_SIZE ---->"+LoadProperties.instanceConnectionsTable.size());
            System.out.println(" Constants.DB_DRIVER-"+Constants.DB_DRIVER_NAME+"_");
            System.out.println(" Constants.PORT --->"+Constants.PORT_NO_VALUE+"_");
            System.out.println(" Constants.CLIENT_IP ---->"+Constants.CLIENT_IP_VLAUE+"_");
            System.out.println(" Constants.MESSAGE_DELIMETER_VALUE ---->"+Constants.MESSAGE_DELIMETER_VALUE+"_");
            System.out.println(" Constants.LOG_FILE_SIZE-"+Constants.LOG_FILE_SIZE+"_");
            System.out.println(" Constants.LOG_FILE_NO-"+Constants.LOG_FILE_NO+"_");
            System.out.println(" Constants.LOG_FILE_PATH-"+Constants.LOG_FILE_PATH+"_");
            System.out.println(" Constants.SERVICES_LOG_FILE_PATH-"+Constants.SERVICES_LOG_FILE_PATH+"_");
            System.out.println(" Constants.MAIL_REPORT_FROM-"+Constants.MAIL_REPORT_FROM+"_");
            System.out.println(" Constants.MAIL_SMTP-"+Constants.MAIL_SMTP+"_");
        } else {
            System.out.println("Config File is not Updated. No Need for Loading again ");
        }//end else

        //--------------- Set the MACHINE IP here so it is avaiable throughout the application
        if(Constants.getMachineIP() == null )
        {
          Constants.setMachineIP();
        }

    } catch (IOException e) {
        System.out.println("Unable to Load the Settings. Server is unable to start/continue -->"+e);
        throw e;
    }//end catch
    return true;
  }//end load method

  /**
 * Checking the configuration file is update or not
 * @param fileName: configuration file
 * @return: changed or not
 */
public static boolean isUpdated (String fileName) {
  try {
      File configFile = new File(fileName);
      System.out.println("Checking Config File Updation lastUpdateTime -->"+lastUpdateTime+" New Time -->"+configFile.lastModified());

      if (!configFile.exists())
          return true;

      if (lastUpdateTime != configFile.lastModified()) {
          lastUpdateTime = configFile.lastModified();
          return true;
      }//end if
  } catch (Exception ex) {
      System.out.println("Exception in checking updation of Config file -->"+ex);
    return false;
  }
  return false;
}//end method



}
