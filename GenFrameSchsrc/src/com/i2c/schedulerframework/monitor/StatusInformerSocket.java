package com.i2c.schedulerframework.monitor;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Date;
import com.i2c.utils.logging.I2cLogger;
import java.util.logging.Logger;


public class StatusInformerSocket extends Thread
{

  private ServerSocket server = null;
  private Socket client = null;
  private Logger socketlLogger = null;

//  public StatusInformerSocket(ServerSocket _server)
//  {
//    this.server = _server;
//  }

  public StatusInformerSocket(ServerSocket _server, Logger lgr)
  {
    this.server = _server;
    this.socketlLogger = lgr;
  }

  public void run()
  {
    socketlLogger.log(I2cLogger.FINEST,"[StatusInformerSocket].[run] Started");
    OutputStream out = null;
    BufferedWriter bw = null;
    Date today = null;
    char[] buffer = new char[512];
    String text = null;
    try
    {
      while (true)
      {
        socketlLogger.log(I2cLogger.FINEST,"[StatusInformerSocket].[run] <----Server is waiting for client request---->");
        client = server.accept();
        try
        {
          out = client.getOutputStream();
          socketlLogger.log(I2cLogger.FINEST,"[StatusInformerSocket].[run] Server Response --- Client & Server Attributes ----  (SERVER IP AND PORT)--->" + client.getLocalSocketAddress()+  " (Client IP AND PORT)--->" + client.getRemoteSocketAddress());
          bw = new BufferedWriter(new OutputStreamWriter(out));
          today = new Date();
          text = today.toString();
          socketlLogger.log(I2cLogger.FINEST,"[StatusInformerSocket].[run] Server Response --- Current Server Date Time--->" + text);
          text.getChars(0,text.length(),buffer,0);
          bw.write(buffer);
        }// end inner try
        catch (Exception ex)
        {
          socketlLogger.log(I2cLogger.SEVERE,"[StatusInformerSocket].[run] Exception in creating connection with client---> " + ex);
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
              socketlLogger.log(I2cLogger.WARNING,"[StatusInformerSocket].[run] Client already closed---> " + ex2);
            }
        }// end finally
      }// end while
    }// end outer try
    catch (Exception ex1)
    {
      socketlLogger.log(I2cLogger.SEVERE,"[StatusInformerSocket].[run] Exception in creating listening to vlient requests---> " + ex1);
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
        socketlLogger.log(I2cLogger.WARNING,"[StatusInformerSocket].[run] Exception in closing server---> " + ex3);
      }

    }// end finally
  }// end method run
}// end class

