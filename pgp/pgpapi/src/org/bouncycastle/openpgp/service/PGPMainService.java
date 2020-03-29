package org.bouncycastle.openpgp.service;

import java.util.Vector;

public class PGPMainService {
  public PGPMainService()
  {

  }

  public static void main(String[] args)
  {
//    PGPLogService pls = new PGPLogService();
    PGPCryptographicService cs = new PGPCryptographicService("D:\\ShahzadAliDATA\\Backup\\BouncyCastle\\BouncyCastle\\ServiceLogs");
//    String file = "D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\Transaction\\testfile-AES(unsuccessful).txt.pgp";
//    String pass = "abcd1234";
//    String path = "D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\Transaction\\Decrypted";

    // Encryption Test
//    String file = "D:\\Backup\\BouncyCastle\\BouncyCastle\\Decrypted\\TransactionExtract07312004.zip";
//    String pass = "D@ffo3iL";
//    String path = "D:\\Backup\\BouncyCastle\\BouncyCastle\\Encrypted\\Test";
//
//    if(cs.encryptPGPFile(file,pass,path,false,false))
//    {
//      System.out.println("File decrypted successfully");
//    }


    // Decryption Test
    String file = "D:\\cardholder\\Encrypted\\CardHolderExtract06222005.zip.pgp";
    String pass = "D@ffo3iL";
    String path = "D:\\cardholder\\Decrypted";

    try {
      String fileName = cs.decryptPGPFile(file, pass, path);
      System.out.println("File decrypted successfully--->" + fileName);

//    PGPUnzipService us = new PGPUnzipService();
    }
    catch (Exception ex) {
    }
//    String zipFile = "D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\Transaction\\Decrypted\\TransactionExtract04032005.zip";
//    String extension = "xml";
//    String destPath = "D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\Transaction\\Decrypted\\Unzip";
//
//    Vector unzipFileNameList = us.unzipSpecificFiles(zipFile,extension,destPath);
//    Vector unzipFileNameList = us.unzipAllFiles(zipFile,destPath);
//
//    for(int i=0;i<unzipFileNameList.size();i++)
//    {
//      System.out.println(unzipFileNameList.get(i).toString());
//    }
  }

}
