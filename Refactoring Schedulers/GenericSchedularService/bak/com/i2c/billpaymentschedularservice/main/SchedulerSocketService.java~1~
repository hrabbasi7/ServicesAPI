package com.i2c.service.billpaymentschedularservice;

import com.i2c.service.util.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Date;


public class SchedulerSocketService extends Thread
{

  private ServerSocket server = null;
  private Socket client = null;

  public SchedulerSocketService(ServerSocket _server)
  {
    server = _server;
  }

  public void run()
  {
    ServiceMain.lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," SchedulerSocketService ---- run---- ");
    OutputStream out = null;
    BufferedWriter bw = null;
    Date today = null;
    char[] buffer = new char[512];
    String text = null;
    try
    {
      while (true)
      {
        ServiceMain.lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," SchedulerSocketService ---- run---- <----Server is waiting for client request---->");
        client = server.accept();
        try
        {
          out = client.getOutputStream();
          ServiceMain.lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," SchedulerSocketService ---- run---- Server Response --- Client & Server Attributes ----  (SERVER IP AND PORT)--->" + client.getLocalSocketAddress()+  " (Client IP AND PORT)--->" + client.getRemoteSocketAddress());
          bw = new BufferedWriter(new OutputStreamWriter(out));
          today = new Date();
          text = today.toString();
          ServiceMain.lgr.log(LogLevel.getLevel(Constants.LOG_FINEST)," SchedulerSocketService ---- run---- Server Response --- Current Server Date Time--->" + text);
          text.getChars(0,text.length(),buffer,0);
          bw.write(buffer);
        }// end inner try
        catch (Exception ex)
        {
          ServiceMain.lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in creating connection with client---> " + ex);
        }
        finally
        {
            try
            {
              if(bw != null)
              {
                bw.close();
              }
              if(client != null)
              {
                client.close();
              }
            }
            catch (Exception ex2)
            {
              ServiceMain.lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),"Client already closed---> " + ex2);
            }
        }// end finally
      }// end while
    }// end outer try
    catch (Exception ex1)
    {
      ServiceMain.lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in creating listening to vlient requests---> " + ex1);
    }
    finally
    {
      try
      {
        if (server != null) {
          server.close();
        }
        if (out != null) {
          out.close();
          out = null;
        }
      }
      catch (Exception ex3)
      {
        ServiceMain.lgr.log(LogLevel.getLevel(Constants.LOG_WARNING),"Exception in closing server---> " + ex3);
      }

    }// end finally
  }// end method run
}// end class

