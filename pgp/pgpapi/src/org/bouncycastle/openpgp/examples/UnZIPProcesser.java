package org.bouncycastle.openpgp.examples;

import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.*;
import java.util.Enumeration;


public class UnZIPProcesser
{

  private String zipFileName = null;
  private static final int buffer_size = 2048;

  UnZIPProcesser(String _fileName)
  {
    zipFileName = _fileName;
  }

  public void unZip(String destinationPath)
  {

    byte[] buffer = new byte[buffer_size];

    ZipFile zipFile = null;
    Enumeration fileList = null;
    ZipEntry textFile = null;

    BufferedInputStream bIn = null;
    BufferedOutputStream bOut = null;
    FileOutputStream fOut = null;
    int count = 0;

    System.out.println("ZIP File name---> " + zipFileName);
    System.out.println("Dest Path---> " + destinationPath);
    try
    {
      zipFile = new ZipFile(zipFileName);
      fileList = zipFile.entries();

      while(fileList.hasMoreElements())
      {
        ZipEntry entry = (ZipEntry) fileList.nextElement();
        System.out.println("ZIP File Entry Name---> " + entry.getName());
        System.out.println("ZIP File Entry Method Name---> " + entry.getMethod());
        System.out.println("ZIP File Entry getCompressedSize---> " + entry.getCompressedSize());
        System.out.println("ZIP File Entry Size---> " + entry.getSize());

        if(entry.getName().endsWith("txt"))
        {
          System.out.println("Found a text file ---> " + entry.getName());
          textFile = entry;
          break;
        }
      }// end while
      if(textFile != null)
      {
        bIn = new BufferedInputStream(zipFile.getInputStream(textFile));

        fOut = new FileOutputStream(destinationPath + textFile.getName());
        bOut = new BufferedOutputStream(fOut,buffer_size);

        while((count = bIn.read(buffer,0,buffer_size)) != -1)
        {
          bOut.write(buffer,0,count);
        }
        bOut.flush();
      }
      else
      {
          System.out.println("Could not find a text file in the provided zip file");
      }
    }
    catch (Exception ex)
    {
      System.out.println("Exception in unzipping file---> " + ex);
    }
    finally
    {
        try
        {
          if (bOut != null)
          {
            bOut.close();
          }
          if (bIn != null)
          {
            bIn.close();
          }
        }
        catch (IOException ex1)
        {

        }
    }
  }
}
