package org.bouncycastle.openpgp.examples;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.bcpg.BCPGInputStream;
import org.bouncycastle.bcpg.SymmetricEncDataPacket;
import org.bouncycastle.bcpg.Packet;
import org.bouncycastle.bcpg.S2K;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyEncSessionPacket;
import org.bouncycastle.openpgp.PGPPBEEncryptedData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.examples.UnZIPProcesser;

public class OldPGPBEFileProcessor {

//-----------------Example For OLD PGP Version Decryption---------------------//

      /**
       * decrypt the passed in message stream
       */
      private static void decryptFile(InputStream in, char[] passPhrase, String out) throws Exception
      {
          in = PGPUtil.getDecoderStream(in);
//-----------------------------------------------------------------------------//
//        int data = 0;
//        int count = 0;
//
//        while(data != -1)
//        {
//          data = in.read();
//          count ++;
//          System.out.print(data + " ");
//        }
//        System.out.println("Count--->" + count);
//-----------------------------------------------------------------------------//

          BCPGInputStream bcin = new  BCPGInputStream(in);
          SymmetricEncDataPacket sedp =  null;
          Packet p = bcin.readPacket();

          if(p instanceof SymmetricEncDataPacket)
          {
            sedp = (SymmetricEncDataPacket)p;
          }

        //------Creating SymmetricKeyEncSessionPacket attributes------//

          S2K s2k = new S2K(HashAlgorithmTags.MD5); // Simple S2K
          int algorithm = SymmetricKeyAlgorithmTags.IDEA;
          byte [] secKeyDate = null;


        //------Creating PGPPBEEncryptedData attributes------//

          SymmetricKeyEncSessionPacket skesp = new SymmetricKeyEncSessionPacket(algorithm,s2k,secKeyDate);
          PGPPBEEncryptedData pbe = new PGPPBEEncryptedData(skesp,sedp);

          InputStream clear = pbe.getDataStream(passPhrase, "BC");

          PGPObjectFactory pgpFact = new PGPObjectFactory(clear);


          Object message = pgpFact.nextObject();

          if (message instanceof PGPCompressedData)
          {
             PGPCompressedData   cData = (PGPCompressedData)message;
             pgpFact = new PGPObjectFactory(cData.getDataStream());
             message = pgpFact.nextObject();
          }
          if (message instanceof PGPLiteralData)
          {
             PGPLiteralData      ld = (PGPLiteralData)message;
             String fileName = out + ld.getFileName();
             System.out.println("Decrypted FILE Path + Name --->" + fileName);
             FileOutputStream    fOut = new FileOutputStream(fileName);

             InputStream    unc = ld.getInputStream();
             int    ch;
             while ((ch = unc.read()) >= 0)
             {
                fOut.write(ch);
             }

             // Code to unzip file
             //-----??????please remember to create all neccessary directories??????----
             UnZIPProcesser processor = new UnZIPProcesser(fileName);
             processor.unZip("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\UnZIP\\");

          }
          else if (message instanceof PGPOnePassSignatureList)
          {
              throw new PGPException("encrypted message contains a signed message - not literal data.");
          }
          else
          {
              throw new PGPException("message is not a simple encrypted file - type unknown.");
          }

          if (pbe.isIntegrityProtected())
          {
              if (!pbe.verify())
              {
                  System.err.println("message failed integrity check");
              }
              else
              {
                  System.err.println("message integrity check passed");
              }
          }
          else
          {
              System.err.println("no message integrity check");
          }
      }

      public static void main(String[] args) throws Exception
      {
              Security.addProvider(new BouncyCastleProvider());
              FileInputStream in = null;
              String out = "D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\Decrypted\\";

//              in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\Transaction\\TransactionExtract04012005.zip.pgp");
//              decryptFile(in, "D@ffo3iL".toCharArray(),out);


         // -----------------------Card Holder ---------------------------//

                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract04012005.zip.pgp");
                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract04042005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03082005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03092005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03102005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(), out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03112005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(), out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03122005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(), out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03132005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(), out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03142005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03152005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03162005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03172005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03182005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03192005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03202005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03212005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03222005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03232005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03242005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03252005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03262005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03272005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03282005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03292005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03302005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);
//                in = new FileInputStream("D:\\Backup\\BouncyCastle\\BouncyCastle\\Final\\CardHolder\\CardHolderExtract03312005.zip.pgp");
//                decryptFile(in, "D@ffo3iL".toCharArray(),out);

      }
}
