package org.bouncycastle.openpgp.service;

import org.bouncycastle.openpgp.logging.*;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

import java.security.Security;
import java.security.SecureRandom;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.bcpg.BCPGInputStream;
import org.bouncycastle.bcpg.SymmetricEncDataPacket;
import org.bouncycastle.bcpg.Packet;
import org.bouncycastle.bcpg.S2K;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyEncSessionPacket;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPPBEEncryptedData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import java.io.*;

//import java.util.Enumeration;

public class PGPCryptographicService
{

  public PGPCryptographicService(String logFilePath)
  {
    Security.addProvider(new BouncyCastleProvider());
    if(logFilePath != null && logFilePath.trim().length() > 0)
    {
      Constants.LOG_FILE_PATH = logFilePath;
      if (!new File(Constants.LOG_FILE_PATH).exists())
      {
        new File(Constants.LOG_FILE_PATH).mkdir();
      }

      Constants.LOG_FILE_NAME = "service-log";
      Constants.LOG_FILE_SIZE = 1000000;
      Constants.LOG_DEBUG_LEVEL = 7;
      Constants.LOG_FILE_NO = 1000;

      try
      {
        LogAttributes.execute();
      }
      catch(Exception e)
      {
        System.out.println("Exception in creating log attributes " + e);
      }


      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService ---Constructor--- ");
    }
  }

  public PGPCryptographicService(String logFilePath, String logFileName, int logFileSize, int logDebugLevel, int noOfLogFiles)
  {
    Security.addProvider(new BouncyCastleProvider());
    if(logFilePath != null && logFilePath.trim().length() > 0)
    {
      Constants.LOG_FILE_PATH = logFilePath;
      if (!new File(Constants.LOG_FILE_PATH).exists())
      {
        new File(Constants.LOG_FILE_PATH).mkdir();
      }
      if(logFileName != null && logFileName.trim().length() > 0)
      {
        Constants.LOG_FILE_NAME = logFileName;
      }
      if(logFileSize > 0)
      {
        Constants.LOG_FILE_SIZE = logFileSize;
      }
      if(logDebugLevel > 0)
      {
        Constants.LOG_DEBUG_LEVEL = logDebugLevel;
      }
      if(noOfLogFiles > 0)
      {
        Constants.LOG_FILE_NO = noOfLogFiles;
      }
      try
      {
        LogAttributes.execute();
      }
      catch(Exception e)
      {
        System.out.println("Exception in creating log attributes " + e);
      }
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService ---Constructor--- ");
    }
  }

  /**
   *
   * This method will encrypt the provided plainText File using the passphrase.
   * On successful encryption the encrypted file will be placed at the provided
   * output path
   *
   * @param plainTextFile String
   * @param passphrase String
   * @param outputPath String
   * @return boolean
   */

  public boolean encryptPGPFile(String plainTextFile, String passphrase, String outputPath)
  {
    boolean flag = true;

    boolean armor = false;
    boolean withIntegrityCheck = false;

    char[] passPhrase = passphrase.toCharArray();
    OutputStream out = null;
    PGPEncryptedDataGenerator cPk = null;
    ByteArrayOutputStream bOut= null;
    File inputFile = null;
    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService ---encryptPGPFile--- Parameters --- plainTextFile ---> "+ plainTextFile + " passphrase ---> " + passphrase +" outputPath --->" + outputPath +"  armor --->" + armor +"  withIntegrityCheck --->" + withIntegrityCheck);
        try
        {
          inputFile = new File(plainTextFile);
          outputPath = outputPath + File.separator + inputFile.getName();
          if(armor)
          {
            outputPath = outputPath + ".asc";
          }
          else
          {
            outputPath = outputPath + ".pgp";
          }
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService ---encryptPGPFile--- Parameters ---  outputPath --->" + outputPath );
          out = new FileOutputStream(outputPath);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService ---encryptPGPFile--- FileOutputStream created--->" + out);
          if (armor)
          {
            out = new ArmoredOutputStream(out);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService ---encryptPGPFile--- ArmoredOutputStream created--->" + out);
          }

          bOut = new ByteArrayOutputStream();

          PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);

          PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, new File(plainTextFile));
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService ---encryptPGPFile--- File is Compressed --->" + comData);
          comData.close();

          cPk = new PGPEncryptedDataGenerator(PGPEncryptedData.CAST5, withIntegrityCheck, new SecureRandom(),"BC");

          cPk.addMethod(passPhrase);

          byte[] bytes = bOut.toByteArray();

          OutputStream cOut = cPk.open(out, bytes.length);

          cOut.write(bytes);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService ---encryptPGPFile--- Encrypted File Created");
        }// end try
        catch (Exception ex)
        {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"PGPCryptographicService --- Exception in encrypting file--->" + ex);
          flag = false;
        }
        finally
        {
          try
          {
            if(cPk != null)
            {
              cPk.close();
            }
            if(out != null)
            {
              out.close();
            }
          }
          catch (IOException ex1)
          {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"PGPCryptographicService --- Exception in closing file streams" + ex1);
          }
        }
    return flag;
  }

  /**
   *
   * This method will decrypt the provided encrypted File using the passphrase.
   * On successful decryption the plaintext file will be placed at the provided
   * output path.
   *
   * @param plainTextFile String
   * @param passphrase String
   * @param outputPath String
   * @return boolean
   */

  public String decryptPGPFile(String encryptedFile, String passPhrase, String outputPath) throws Exception
  {
    FileInputStream inputFile = null;
    String fileName = null;

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptPGPFile --- Encrypted file--->" + encryptedFile + " Passphrase--->" +passPhrase + " OuputPath--->" + outputPath);
    try
    {
      inputFile = new FileInputStream(encryptedFile);
      if(checkNewVersion(inputFile))// New version PGP File
      {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptPGPFile --- checkNewVersion--->true");
        fileName = decryptNewVersionPGPFile(new FileInputStream(encryptedFile), passPhrase.toCharArray(), outputPath);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptPGPFile --- New version file is decrypted successfully");
      }
      else// Old version PGP file
      {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptPGPFile --- checkNewVersion--->false");
        fileName = decryptOldVersionPGPFile(new FileInputStream(encryptedFile), passPhrase.toCharArray(), outputPath);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptPGPFile --- Old version file is decrypted successfully");
      }
    }
    catch (Exception ex)
    {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"PGPCryptographicService --- decryptPGPFile --- Exception in decrypting file --->" + ex);
      throw ex;
    }
    return fileName;
  }

  /**
   * Method for decrypting old version files
   * @param in InputStream
   * @param passphrase char[]
   * @param outputPath String
   * @return boolean
   */

  private String decryptOldVersionPGPFile(InputStream in, char[] passphrase, String outputPath) throws Exception
  {

    String fileName = null;
    BCPGInputStream bcin = null;
    SymmetricEncDataPacket sedp = null;
    Packet p = null;
    PGPPBEEncryptedData pbe = null;
    String provider = "BC";
    FileOutputStream fOut = null;
    InputStream unc = null;
    InputStream clear = null;

    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptOldVersionPGPFile --- Encrypted file--->" + in + " Passphrase--->" + new String(passphrase) + " OuputPath--->" + outputPath);
    try
    {
      in = PGPUtil.getDecoderStream(in);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptOldVersionPGPFile --- Got Decoder Stream " + in);

      bcin = new BCPGInputStream(in);
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptOldVersionPGPFile --- Creating BCPGInputStream " + bcin);

      p = bcin.readPacket();
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptOldVersionPGPFile --- Retrieving First Packet---> " + p);

      //------We Can Only Process SymmetricEncDataPacket in this section------//
      if (p instanceof SymmetricEncDataPacket)
      {
        sedp = (SymmetricEncDataPacket) p;

      //------Creating SymmetricKeyEncSessionPacket attributes------//

        S2K s2k = new S2K(HashAlgorithmTags.MD5); // Simple S2K
        int algorithm = SymmetricKeyAlgorithmTags.IDEA;
        byte[] secKeyDate = null;
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptOldVersionPGPFile --- Simple S2K Attributes--- Hash Algo---> " + HashAlgorithmTags.MD5);

      //------Creating PGPPBEEncryptedData attributes------//

        SymmetricKeyEncSessionPacket skesp = new SymmetricKeyEncSessionPacket(algorithm, s2k, secKeyDate);
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptOldVersionPGPFile --- Simple S2K Attributes--- Symmetric Algo---> " + algorithm + " S2K Type--->" + s2k.getType() + " Secret Key Data--->" + secKeyDate);

        pbe = new PGPPBEEncryptedData(skesp, sedp);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptOldVersionPGPFile --- Calling getDataStream--- Passphrase---> " + new String(passphrase) + " Provider--->  " + provider);
        clear = pbe.getDataStream(passphrase, provider);

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptOldVersionPGPFile --- Returned from getDataStream--- Decrpyted Input Stream---> " + clear);

        PGPObjectFactory pgpFact = new PGPObjectFactory(clear);

        Object message = pgpFact.nextObject();

        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptOldVersionPGPFile --- getting nextObject --->" + message);

        if (message instanceof PGPCompressedData)
        {
          PGPCompressedData cData = (PGPCompressedData) message;
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptOldVersionPGPFile --- extracting literal data ");
          pgpFact = new PGPObjectFactory(cData.getDataStream());
          message = pgpFact.nextObject();
        }
        if (message instanceof PGPLiteralData)
        {
          PGPLiteralData ld = (PGPLiteralData) message;
          fileName = outputPath + File.separator + ld.getFileName();
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptOldVersionPGPFile --- decrypted file path\name " + fileName);
          fOut = new FileOutputStream(fileName);

          unc = ld.getInputStream();
          int ch;
          while ( (ch = unc.read()) >= 0)
          {
            fOut.write(ch);
          }
        }// end if checking PGPLiteralData
      }// end if checking SymmetricEncDataPacket
      else
      {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"PGPCryptographicService --- decryptOldVersionPGPFile --- Cannot decrypt FILE, it is not an insatnce of  SymmetricEncDataPacket");
          throw new Exception("Unable to decrypt FILE, it is not an insatnce of  SymmetricEncDataPacket");
      }
    }// end try
    catch (Exception ex)
    {
      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"PGPCryptographicService --- Exception in decrypting file" + ex);
      throw new Exception("Unable to Decrypt Old version File--->" + ex);
    }
    finally
    {
        try
        {
          if (bcin != null)
          {
            bcin.close();
            bcin = null;
          }
          if (fOut != null)
          {
              fOut.close();
              fOut = null;
          }
          if(unc != null)
          {
              unc.close();
              unc = null;
          }
          if (clear != null){
              clear.close();
              clear = null;
          }
          if (in != null){
              in.close();
              in = null;
          }
      }
      catch (IOException ex1)
        {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"PGPCryptographicService --- Exception in closing streams" + ex1);
        }
    }
    return fileName;
  }

  /**
   * Method for decrypting new version files
   * @param in InputStream
   * @param passPhrase char[]
   * @param outputPath String
   * @return boolean
   */
  private String decryptNewVersionPGPFile(InputStream in, char[] passPhrase, String outputPath) throws Exception
   {
      String fileName = null;
      String provider = "BC";
      InputStream unc = null;
      FileOutputStream fOut = null;
      PGPObjectFactory pgpF = null;
      PGPEncryptedDataList enc = null;
      Object o = null;
      PGPPBEEncryptedData pbe = null;
      InputStream clear = null;

      CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptNewVersionPGPFile --- Encrypted file--->" + in + " Passphrase--->" + new String(passPhrase) + " OuputPath--->" + outputPath);
      try
      {
        in = PGPUtil.getDecoderStream(in);

//        int ch1 = 0;
//        while((ch1 = in.read()) != -1)
//        {
//          System.out.print(ch1 + " ");
//        }
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptNewVersionPGPFile --- Got Decoder Stream---> " + in);
        pgpF = new PGPObjectFactory(in);
        o = pgpF.nextObject();
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptNewVersionPGPFile --- Got Next Object---> " + o);
        //
        // the first object might be a PGP marker packet.
        //
        if (o instanceof PGPEncryptedDataList)
        {
          enc = (PGPEncryptedDataList) o;
        }
        else
        {
            enc = (PGPEncryptedDataList) pgpF.nextObject();
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptNewVersionPGPFile --- After Marker TAG Got Next Object---> " + o);
        }

        o = enc.get(0);
        //This section can only decrypt password based files
        if(o instanceof PGPPBEEncryptedData)
        {
          pbe = (PGPPBEEncryptedData) o;

          clear = pbe.getDataStream(passPhrase, provider);
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptNewVersionPGPFile --- Got decrypted stream---> " + clear);

          PGPObjectFactory pgpFact = new PGPObjectFactory(clear);

          Object message = pgpFact.nextObject();

          if (message instanceof PGPCompressedData)
          {
            PGPCompressedData cData = (PGPCompressedData) message;
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptNewVersionPGPFile --- extracting literal data ");
            pgpFact = new PGPObjectFactory(cData.getDataStream());
            message = pgpFact.nextObject();
          }
          if (message instanceof PGPLiteralData)
          {
            PGPLiteralData ld = (PGPLiteralData) message;
            fileName = outputPath + File.separator + ld.getFileName();
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- decryptNewVersionPGPFile --- decrypted file path\name " + fileName);
            fOut = new FileOutputStream(fileName);

            unc = ld.getInputStream();

            int ch = -1;

            while ( (ch = unc.read()) >= 0)
            {
              fOut.write(ch);
            }
          }// end if checking PGPLiteralData
          if (pbe.isIntegrityProtected())
          {
            if (!pbe.verify())
            {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"message failed integrity check");
            }
            else
            {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"message integrity check passed");
            }
          }
          else
          {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"no message integrity check");
          }
        }//end if checking PGPPBEEncryptedData
        else
        {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"PGPCryptographicService --- decryptOldVersionPGPFile --- Cannot decrypt FILE, it is not an insatnce of PGPPBEEncryptedData");
            throw new Exception("Unable to decrypt FILE, it is not an insatnce of PGPPBEEncryptedData");
        }
      }//end try
      catch (Exception ex)
      {
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"PGPCryptographicService --- Exception in decrypting file" + ex);
        throw new Exception("Unable to Decrypt New version File--->" + ex);
      }
      finally
      {
          try
          {
            if (fOut != null)
            {
              fOut.close();
              fOut = null;
            }
            if(unc != null)
            {
              unc.close();
              unc = null;
            }
            if(in != null)
            {
              in.close();
              in = null;
            }
            if(clear != null)
            {
              clear.close();
              clear = null;
            }
          }
          catch (Exception ex) {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"PGPCryptographicService --- Exception in Closing files" + ex);
          }

      }
     return fileName;
   }//end method

   /**
    * method to check the version of PGP file header
    * @param in InputStream
    * @return boolean --> false-- if old, true-- otherwise
    */
   private boolean checkNewVersion(InputStream in) throws Exception
   {
     boolean flag = true;
     int firstByte = -1;

     CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- checkNewVersion --- Input Stream --->" + in);

     try
     {
       firstByte = in.read();
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- checkNewVersion --- First Byte --->" + firstByte);
       int tag = -1;
       boolean newPacket = (firstByte & 0x40) != 0;
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- checkNewVersion --- New Packet --->" + newPacket);
       if(newPacket)
       {
         tag = firstByte & 0x3f;
       }
       else
       {
           tag = (firstByte & 0x3f) >> 2;
       }
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPCryptographicService --- checkNewVersion --- Tag Number --->" + tag);
       if(tag == 9)
       {
         return false;//old version
       }
     }
     catch (Exception ex)
     {
       CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"PGPCryptographicService --- checkNewVersion --- Exception in checking new version --->" + ex);
       throw new Exception("Unable to check the veriosn of file");
     }
     finally
     {
         if(in != null)
         {
          try
          {
            in.close();
          }
          catch (IOException ex1)
          {
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"PGPCryptographicService --- checkNewVersion --- Exception in clsing file stream --->" + ex1);
          }
         }
     }
     return flag;
   }// end method
}// end class
