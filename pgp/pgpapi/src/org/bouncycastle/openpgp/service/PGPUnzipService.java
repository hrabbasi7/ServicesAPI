package org.bouncycastle.openpgp.service;

import org.bouncycastle.openpgp.logging.*;

import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.util.Vector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

public class PGPUnzipService {
  private final int buffer_size = 2048;

  public PGPUnzipService(String logFilePath) {
    if (logFilePath != null && logFilePath.trim().length() > 0) {
      Constants.LOG_FILE_PATH = logFilePath;
      if (!new File(Constants.LOG_FILE_PATH).exists()) {
        new File(Constants.LOG_FILE_PATH).mkdir();
      }

      Constants.LOG_FILE_NAME = "service-log";
      Constants.LOG_FILE_SIZE = 1000000;
      Constants.LOG_DEBUG_LEVEL = 7;
      Constants.LOG_FILE_NO = 1000;

      try {
        LogAttributes.execute();
      }
      catch (Exception e) {
        System.out.println("Exception in creating log attributes " + e);
      }

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "PGPUnzipService ---Constructor--- ");
    }
  }

  public PGPUnzipService(String logFilePath, String logFileName,
                         int logFileSize, int logDebugLevel, int noOfLogFiles) {
    if (logFilePath != null && logFilePath.trim().length() > 0) {
      Constants.LOG_FILE_PATH = logFilePath;
      if (!new File(Constants.LOG_FILE_PATH).exists()) {
        new File(Constants.LOG_FILE_PATH).mkdir();
      }
      if (logFileName != null && logFileName.trim().length() > 0) {
        Constants.LOG_FILE_NAME = logFileName;
      }
      if (logFileSize > 0) {
        Constants.LOG_FILE_SIZE = logFileSize;
      }
      if (logDebugLevel > 0) {
        Constants.LOG_DEBUG_LEVEL = logDebugLevel;
      }
      if (noOfLogFiles > 0) {
        Constants.LOG_FILE_NO = noOfLogFiles;
      }
      try {
        LogAttributes.execute();
      }
      catch (Exception e) {
        System.out.println("Exception in creating log attributes " + e);
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                      "PGPUnzipService ---Constructor--- ");
    }
  }

  /**
   * Unzip All files of specific extension in the provided zip file
   * @param zipFileName String
   * @param extension String
   * @param destinationPath String
   * @return String[]
   */
  public Vector unzipSpecificFiles(String zipFileName, String extension,
                                   String destinationPath) throws Exception {
    Vector returnFileNames = new Vector();
    byte[] buffer = new byte[buffer_size];

    ZipFile zipFile = null;
    Enumeration fileList = null;
    ZipEntry reqdFile = null;

    BufferedInputStream bIn = null;
    BufferedOutputStream bOut = null;
    FileOutputStream fOut = null;
    int fileCount = 0;
    int count = 0;

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
        "PGPUnzipService --- unzipSpecificFile --- ZIP File name---> " +
                                    zipFileName);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
        "PGPUnzipService --- unzipSpecificFile ---Required File with Extension---> " +
                                    extension);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
        "PGPUnzipService --- unzipSpecificFile ---Dest Path---> " +
                                    destinationPath);
    try {
      zipFile = new ZipFile(zipFileName);
      fileList = zipFile.entries();

      while (fileList.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) fileList.nextElement();
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            "PGPUnzipService --- unzipSpecificFile ---ZIP File Entry Name---> " +
                                        entry.getName());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            "PGPUnzipService --- unzipSpecificFile ---ZIP File Entry Method Name---> " +
                                        entry.getMethod());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            "PGPUnzipService --- unzipSpecificFile ---ZIP File Entry getCompressedSize---> " +
                                        entry.getCompressedSize());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            "PGPUnzipService --- unzipSpecificFile ---ZIP File Entry Size---> " +
                                        entry.getSize());

        if (entry.getName().endsWith(extension)) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              "PGPUnzipService --- unzipSpecificFile ---Found the specified file ---> " +
                             entry.getName());
          reqdFile = entry;

          bIn = new BufferedInputStream(zipFile.getInputStream(reqdFile));
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              "PGPUnzipService --- unzipSpecificFile ---Full Destination path---> " +
                             destinationPath + File.separator +
                             reqdFile.getName());
          fOut = new FileOutputStream(destinationPath + File.separator +
                                      reqdFile.getName());
          bOut = new BufferedOutputStream(fOut, buffer_size);

          while ( (count = bIn.read(buffer, 0, buffer_size)) != -1) {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
                LOG_FINEST),
                "PGPUnzipService --- unzipSpecificFile ---Total Bytes Read---> " +
                               count);
//            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPUnzipService --- unzipSpecificFile ---Bytes Read---> " + new String(buffer));
            bOut.write(buffer, 0, count);
          }
          bOut.flush();
          returnFileNames.add(reqdFile.getName());
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              "PGPUnzipService --- unzipSpecificFile ---Adding Successfully unzipped file to return list---> " +
                             returnFileNames.get(fileCount).toString());
          fileCount++;
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              "PGPUnzipService --- unzipSpecificFile ---Total unzipped files so far---> " +
                             fileCount);
        } // end if checking provided ext
      } // end outer while
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
          "PGPUnzipService --- unzipSpecificFiles ---Exception in unzipping file---> " +
                                      ex);
      throw new Exception("Unable to unzip the file");
    }
    finally {
      try {
        if(zipFile != null){
          zipFile.close();
          zipFile = null;
        }
        if(fOut != null){
          fOut.close();
          fOut = null;
        }
        if (bOut != null) {
          bOut.close();
          bOut = null;
        }
        if (bIn != null) {
          bIn.close();
          bIn = null;
        }
      }catch (IOException ex1) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "PGPUnzipService --- unzipSpecificFiles ---Exception in closing file streams---> " + ex1);
      }
    }
    return returnFileNames;
  }

  public Vector unzipAllFiles(String zipFileName, String destinationPath) throws
      Exception {
    Vector returnFileNames = new Vector();
    byte[] buffer = new byte[buffer_size];

    ZipFile zipFile = null;
    Enumeration fileList = null;
    ZipEntry entry = null;

    BufferedInputStream bIn = null;
    BufferedOutputStream bOut = null;
    FileOutputStream fOut = null;

    int fileCount = 0;
    int count = 0;

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "PGPUnzipService --- unzipSpecificFile --- ZIP File name---> " +
                                    zipFileName);
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                    "PGPUnzipService --- unzipSpecificFile ---Dest Path---> " +
                                    destinationPath);
    try {
      zipFile = new ZipFile(zipFileName);
      fileList = zipFile.entries();

      while (fileList.hasMoreElements()) {
        entry = (ZipEntry) fileList.nextElement();

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            "PGPUnzipService --- unzipSpecificFile ---ZIP File Entry Name---> " +
                                        entry.getName());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            "PGPUnzipService --- unzipSpecificFile ---ZIP File Entry Method Name---> " +
                                        entry.getMethod());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            "PGPUnzipService --- unzipSpecificFile ---ZIP File Entry getCompressedSize---> " +
                                        entry.getCompressedSize());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            "PGPUnzipService --- unzipSpecificFile ---ZIP File Entry Size---> " +
                                        entry.getSize());

        bIn = new BufferedInputStream(zipFile.getInputStream(entry));
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            "PGPUnzipService --- unzipSpecificFile ---Full Destination path---> " +
                                        destinationPath + File.separator +
                                        entry.getName());
        fOut = new FileOutputStream(destinationPath + File.separator +
                                    entry.getName());
        bOut = new BufferedOutputStream(fOut, buffer_size);

        while ( (count = bIn.read(buffer, 0, buffer_size)) != -1) {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              "PGPUnzipService --- unzipSpecificFile ---Total Bytes Read---> " +
                             count);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.
              LOG_FINEST),
              "PGPUnzipService --- unzipSpecificFile ---Bytes Read---> " +
                             new String(buffer));
          bOut.write(buffer, 0, count);
        }
        bOut.flush();
        returnFileNames.add(entry.getName());
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "PGPUnzipService --- unzipSpecificFile ---Adding Successfully unzipped file to return list---> " +
                                        returnFileNames.get(fileCount).toString());
        fileCount++;
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
            "PGPUnzipService --- unzipSpecificFile ---Total unzipped files so far---> " +
                                        fileCount);
      } // end outer while
    }
    catch (Exception ex) {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
          "PGPUnzipService --- unzipSpecificFiles ---Exception in unzipping file---> " +
                                      ex);
      throw new Exception("Unable to unzip the file");
    }
    finally {
      try {
        if(zipFile != null){
          zipFile.close();
          zipFile = null;
        }
        if(fOut != null){
          fOut.close();
          fOut = null;
        }
        if (bOut != null) {
          bOut.close();
          bOut = null;
        }
        if (bIn != null) {
          bIn.close();
          bIn = null;
        }
      }catch (IOException ex1) {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),
                                        "PGPUnzipService --- unzipSpecificFiles ---Exception in closing file streams---> " + ex1);
      }
    } //end finally
    return returnFileNames;
  } //end method
} //end class
