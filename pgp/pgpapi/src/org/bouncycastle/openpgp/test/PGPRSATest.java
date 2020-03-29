package org.bouncycastle.openpgp.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.PGPV3SignatureGenerator;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.test.SimpleTestResult;
import org.bouncycastle.util.test.Test;
import org.bouncycastle.util.test.TestResult;

public class PGPRSATest
    implements Test
{
    byte[] testPubKey = Base64.decode(
        "mIsEPz2nJAEEAOTVqWMvqYE693qTgzKv/TJpIj3hI8LlYPC6m1dk0z3bDLwVVk9F"
      + "FAB+CWS8RdFOWt/FG3tEv2nzcoNdRvjv9WALyIGNawtae4Ml6oAT06/511yUzXHO"
      + "k+9xK3wkXN5jdzUhf4cA2oGpLSV/pZlocsIDL+jCUQtumUPwFodmSHhzAAYptC9F"
      + "cmljIEVjaGlkbmEgKHRlc3Qga2V5KSA8ZXJpY0Bib3VuY3ljYXN0bGUub3JnPoi4"
      + "BBMBAgAiBQI/PackAhsDBQkAg9YABAsHAwIDFQIDAxYCAQIeAQIXgAAKCRA1WGFG"
      + "/fPzc8WMA/9BbjuB8E48QAlxoiVf9U8SfNelrz/ONJA/bMvWr/JnOGA9PPmFD5Uc"
      + "+kV/q+i94dEMjsC5CQ1moUHWSP2xlQhbOzBP2+oPXw3z2fBs9XJgnTH6QWMAAvLs"
      + "3ug9po0loNHLobT/D/XdXvcrb3wvwvPT2FptZqrtonH/OdzT9JdfrA==");

    byte[] testPrivKey = Base64.decode(
        "lQH8BD89pyQBBADk1aljL6mBOvd6k4Myr/0yaSI94SPC5WDwuptXZNM92wy8FVZP"
      + "RRQAfglkvEXRTlrfxRt7RL9p83KDXUb47/VgC8iBjWsLWnuDJeqAE9Ov+ddclM1x"
      + "zpPvcSt8JFzeY3c1IX+HANqBqS0lf6WZaHLCAy/owlELbplD8BaHZkh4cwAGKf4D"
      + "AwKbLeIOVYTEdWD5v/YgW8ERs0pDsSIfBTvsJp2qA798KeFuED6jGsHUzdi1M990"
      + "6PRtplQgnoYmYQrzEc6DXAiAtBR4Kuxi4XHx0ZR2wpVlVxm2Ypgz7pbBNWcWqzvw"
      + "33inl7tR4IDsRdJOY8cFlN+1tSCf16sDidtKXUVjRjZNYJytH18VfSPlGXMeYgtw"
      + "3cSGNTERwKaq5E/SozT2MKTiORO0g0Mtyz+9MEB6XVXFavMun/mXURqbZN/k9BFb"
      + "z+TadpkihrLD1xw3Hp+tpe4CwPQ2GdWKI9KNo5gEnbkJgLrSMGgWalPhknlNHRyY"
      + "bSq6lbIMJEE3LoOwvYWwweR1+GrV9farJESdunl1mDr5/d6rKru+FFDwZM3na1IF"
      + "4Ei4FpqhivZ4zG6pN5XqLy+AK85EiW4XH0yAKX1O4YlbmDU4BjxhiwTdwuVMCjLO"
      + "5++jkz5BBQWdFX8CCMA4FJl36G70IbGzuFfOj07ly7QvRXJpYyBFY2hpZG5hICh0"
      + "ZXN0IGtleSkgPGVyaWNAYm91bmN5Y2FzdGxlLm9yZz6IuAQTAQIAIgUCPz2nJAIb"
      + "AwUJAIPWAAQLBwMCAxUCAwMWAgECHgECF4AACgkQNVhhRv3z83PFjAP/QW47gfBO"
      + "PEAJcaIlX/VPEnzXpa8/zjSQP2zL1q/yZzhgPTz5hQ+VHPpFf6voveHRDI7AuQkN"
      + "ZqFB1kj9sZUIWzswT9vqD18N89nwbPVyYJ0x+kFjAALy7N7oPaaNJaDRy6G0/w/1"
      + "3V73K298L8Lz09habWaq7aJx/znc0/SXX6w=");

    byte[] testPubKeyV3 = Base64.decode(
       "mQCNAz+zvlEAAAEEAMS22jgXbOZ/D3xWgM2kauSdzrwlU7Ms5hDW05ObqQyO"
      + "FfQoKKMhfupyoa7J3x04VVBKu6Eomvr1es+VImH0esoeWFFahNOYq/I+jRRB"
      + "woOhAGZ5UB2/hRd7rFmxqp6sCXi8wmLO2tAorlTzAiNNvl7xF4cQZpc0z56F"
      + "wdi2fBUJAAURtApGSVhDSVRZX1FBiQCVAwUQP7O+UZ6Fwdi2fBUJAQFMwwQA"
      + "qRnFsdg4xQnB8Y5d4cOpXkIn9AZgYS3cxtuSJB84vG2CgC39nfv4c+nlLkWP"
      + "4puG+mZuJNgVoE84cuAF4I//1anKjlU7q1M6rFQnt5S4uxPyG3dFXmgyU1b4"
      + "PBOnA0tIxjPzlIhJAMsPCGGA5+5M2JP0ad6RnzqzE3EENMX+GqY=");

    byte[] testPrivKeyV3 = Base64.decode(
        "lQHfAz+zvlEAAAEEAMS22jgXbOZ/D3xWgM2kauSdzrwlU7Ms5hDW05ObqQyO"
      + "FfQoKKMhfupyoa7J3x04VVBKu6Eomvr1es+VImH0esoeWFFahNOYq/I+jRRB"
      + "woOhAGZ5UB2/hRd7rFmxqp6sCXi8wmLO2tAorlTzAiNNvl7xF4cQZpc0z56F"
      + "wdi2fBUJAAURAXWwRBZQHNikA/f0ScLLjrXi4s0hgQecg+dkpDow94eu5+AR"
      + "0DzZnfurpgfUJCNiDi5W/5c3Zj/xyrfMAgkbCgJ1m6FZqAQh7Mq73l7Kfu4/"
      + "XIkyDF3tDgRuZNezB+JuElX10tV03xumHepp6M6CfhXqNJ15F33F99TA5hXY"
      + "CPYD7SiSOpIhQkCOAgDAA63imxbpuKE2W7Y4I1BUHB7WQi8ZdkZd04njNTv+"
      + "rFUuOPapQVfbWG0Vq8ld3YmJB4QWsa2mmqn+qToXbwufAgBpXkjvqK5yPiHF"
      + "Px2QbFc1VqoCJB6PO5JRIqEiUZBFGdDlLxt3VSyqz7IZ/zEnxZq+tPCGGGSm"
      + "/sAGiMvENcHVAfy0kTXU42TxEAYJyyNyqjXOobDJpEV1mKhFskRXt7tbMfOS"
      + "Yf91oX8f6xw6O2Nal+hU8dS0Bmfmk5/enHmvRLHQocO0CkZJWENJVFlfUUE=");

    byte[] sig1 = Base64.decode(
        "owGbwMvMwMRoGpHo9vfz52LGNTJJnBmpOTn5eiUVJfb23JvAHIXy/KKcFEWuToap"
      + "zKwMIGG4Bqav0SwMy3yParsEKi2LMGI9xhh65sBxb05n5++ZLcWNJ/eLFKdWbm95"
      + "tHbDV7GMwj/tUctUpFUXWPYFCLdNsDiVNuXbQvZtdXV/5xzY+9w1nCnijH9JoNiJ"
      + "22n2jo0zo30/TZLo+jDl2vTzIvPeLEsPM3ZUE/1Ytqs4SG2TxIQbH7xf3uzcYXq2"
      + "5Fw9AA==");
      
    byte[] sig1crc = Base64.decode("+3i0");

    byte[] subKey = Base64.decode(
        "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"
      +    "CphiUYWnsC0mQ+J15B4=");
    
    byte[] enc1 = Base64.decode(
        "hIwDKwfQexPJboABA/4/7prhYYMORTiQ5avQKx0XYpCLujzGefYjnyuWZnx3Iev8"
        +    "Pmsguumm+OLLvtXhhkXQmkJRXbIg6Otj2ubPYWflRPgpJSgOrNOreOl5jeABOrtw"
        +    "bV6TJb9OTtZuB7cTQSCq2gmYiSZkluIiDjNs3R3mEanILbYzOQ3zKSggKpzlv9JQ"
        +    "AZUqTyDyJ6/OUbJF5fI5uiv76DCsw1zyMWotUIu5/X01q+AVP5Ly3STzI7xkWg/J"
        +    "APz4zUHism7kSYz2viAQaJx9/bNnH3AM6qm1Fuyikl4=");
        

    byte[] enc1crc = Base64.decode("lv4o");
    
    byte[] enc2 = Base64.decode(
         "hIwDKwfQexPJboABBAC62jcJH8xKnKb1neDVmiovYON04+7VQ2v4BmeHwJrdag1g"
        + "Ya++6PeBlQ2Q9lSGBwLobVuJmQ7cOnPUJP727JeSGWlMyFtMbBSHekOaTenT5lj7"
        + "Zk7oRHxMp/hByzlMacIDzOn8LPSh515RHM57eDLCOwqnAxGQwk67GRl8f5dFH9JQ"
        + "Aa7xx8rjCqPbiIQW6t5LqCNvPZOiSCmftll6+se1XJhFEuq8WS4nXtPfTiJ3vib4"
        + "3soJdHzGB6AOs+BQ6aKmmNTVAxa5owhtSt1Z/6dfSSk=");

     byte[]    subPubKey = Base64.decode(
         "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");
         
    byte[]    subPubCrc = Base64.decode("=rikt");

    byte[]    pgp8Key = Base64.decode(
          "lQIEBEBXUNMBBADScQczBibewnbCzCswc/9ut8R0fwlltBRxMW0NMdKJY2LF"
        + "7k2COeLOCIU95loJGV6ulbpDCXEO2Jyq8/qGw1qD3SCZNXxKs3GS8Iyh9Uwd"
        + "VL07nMMYl5NiQRsFB7wOb86+94tYWgvikVA5BRP5y3+O3GItnXnpWSJyREUy"
        + "6WI2QQAGKf4JAwIVmnRs4jtTX2DD05zy2mepEQ8bsqVAKIx7lEwvMVNcvg4Y"
        + "8vFLh9Mf/uNciwL4Se/ehfKQ/AT0JmBZduYMqRU2zhiBmxj4cXUQ0s36ysj7"
        + "fyDngGocDnM3cwPxaTF1ZRBQHSLewP7dqE7M73usFSz8vwD/0xNOHFRLKbsO"
        + "RqDlLA1Cg2Yd0wWPS0o7+qqk9ndqrjjSwMM8ftnzFGjShAdg4Ca7fFkcNePP"
        + "/rrwIH472FuRb7RbWzwXA4+4ZBdl8D4An0dwtfvAO+jCZSrLjmSpxEOveJxY"
        + "GduyR4IA4lemvAG51YHTHd4NXheuEqsIkn1yarwaaj47lFPnxNOElOREMdZb"
        + "nkWQb1jfgqO24imEZgrLMkK9bJfoDnlF4k6r6hZOp5FSFvc5kJB4cVo1QJl4"
        + "pwCSdoU6luwCggrlZhDnkGCSuQUUW45NE7Br22NGqn4/gHs0KCsWbAezApGj"
        + "qYUCfX1bcpPzUMzUlBaD5rz2vPeO58CDtBJ0ZXN0ZXIgPHRlc3RAdGVzdD6I"
        + "sgQTAQIAHAUCQFdQ0wIbAwQLBwMCAxUCAwMWAgECHgECF4AACgkQs8JyyQfH"
        + "97I1QgP8Cd+35maM2cbWV9iVRO+c5456KDi3oIUSNdPf1NQrCAtJqEUhmMSt"
        + "QbdiaFEkPrORISI/2htXruYn0aIpkCfbUheHOu0sef7s6pHmI2kOQPzR+C/j"
        + "8D9QvWsPOOso81KU2axUY8zIer64Uzqc4szMIlLw06c8vea27RfgjBpSCryw"
        + "AgAA");

    char[]    pgp8Pass = "2002 Buffalo Sabres".toCharArray();

    char[]    pass = { 'h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd' };

    byte[]  fingerprintKey = Base64.decode(
            "mQEPA0CiJdUAAAEIAMI+znDlPd2kQoEcnxqxLcRz56Z7ttFKHpnYp0UkljZdquVc"
          + "By1jMfXGVV64xN1IvMcyenLXUE0IUeUBCQs6tHunFRAPSeCxJ3FdFe1B5MpqQG8A"
          + "BnEpAds/hAUfRDZD5y/lolk1hjvFMrRh6WXckaA/QQ2t00NmTrJ1pYUpkw9tnVQb"
          + "LUjWJhfZDBBcN0ADtATzgkugxMtcDxR6I5x8Ndn+IilqIm23kxGIcmMd/BHOec4c"
          + "jRwJXXDb7u8tl+2knAf9cwhPHp3+Zy4uGSQPdzQnXOhBlA+4WDa0RROOevWgq8uq"
          + "8/9Xp/OlTVL+OoIzjsI6mJP1Joa4qmqAnaHAmXcAEQEAAbQoQk9BM1JTS1kgPEJP"
          + "QSBNb25pdG9yaW5nIEAgODg4LTI2OS01MjY2PokBFQMFEECiJdWqaoCdocCZdwEB"
          + "0RsH/3HPxoUZ3G3K7T3jgOnJUckTSHWU3XspHzMVgqOxjTrcexi5IsAM5M+BulfW"
          + "T2aO+Kqf5w8cKTKgW02DNpHUiPjHx0nzDE+Do95zbIErGeK+Twkc4O/aVsvU9GGO"
          + "81VFI6WMvDQ4CUAUnAdk03MRrzI2nAuhn4NJ5LQS+uJrnqUJ4HmFAz6CQZQKd/kS"
          + "Xgq+A6i7aI1LG80YxWa9ooQgaCrb9dwY/kPQ+yC22zQ3FExtv+Fv3VtAKTilO3vn"
          + "BA4Y9uTHuObHfI+1yxUS2PrlRUX0m48ZjpIX+cEN3QblGBJudI/A1QSd6P0LZeBr"
          + "7F1Z1aF7ZDo0KzgiAIBvgXkeTpw=");
    
    byte[] fingerprintCheck = Base64.decode("=CTv2");
    
    private boolean notEqual(
        byte[]    b1,
        byte[]    b2)
    {
        if (b1.length != b2.length)
        {
            return true;
        }
        
        for (int i = 0; i != b2.length; i++)
        {
            if (b1[i] != b2[i])
            {
                return true;
            }
        }
        
        return false;
    }
    
    private TestResult fingerPrintTest()
        throws Exception
    {
        //
        // version 3
        //
        PGPPublicKeyRing        pgpPub = new PGPPublicKeyRing(fingerprintKey);

        PGPPublicKey            pubKey = pgpPub.getPublicKey();

        if (notEqual(pubKey.getFingerprint(), Hex.decode("4FFB9F0884266C715D1CEAC804A3BBFA")))
        {
            return new SimpleTestResult(false, getName() + ": version 3 fingerprint test failed");
        }
        
        //
        // version 4
        //
        pgpPub = new PGPPublicKeyRing(testPubKey);

        pubKey = pgpPub.getPublicKey();

        if (notEqual(pubKey.getFingerprint(), Hex.decode("3062363c1046a01a751946bb35586146fdf3f373")))
        {
            return new SimpleTestResult(false, getName() + ": version 4 fingerprint test failed");
        }
        return new SimpleTestResult(true, getName() + ": Okay");
    }
    
    public TestResult perform()
    {
        try
        {
            KeyFactory     fact = KeyFactory.getInstance("RSA", "BC");
            PublicKey      pubKey = null;
            PrivateKey     privKey = null;

            //
            // Read the public key
            //
            PGPPublicKeyRing        pgpPub = new PGPPublicKeyRing(testPubKey);

            pubKey = pgpPub.getPublicKey().getKey("BC");

            Iterator    it = pgpPub.getPublicKey().getUserIDs();
            
            String    uid = (String)it.next();

            it = pgpPub.getPublicKey().getSignaturesForID(uid);
            
            PGPSignature    sig = (PGPSignature)it.next();
            
            sig.initVerify(pgpPub.getPublicKey(), "BC");
            
            if (!sig.verifyCertification(uid, pgpPub.getPublicKey()))
            {
                return new SimpleTestResult(false, getName() + ": failed to verify certification");
            }
            
            //
            // write a public key
            //
            ByteArrayOutputStream    bOut = new ByteArrayOutputStream();
            BCPGOutputStream         pOut = new BCPGOutputStream(bOut);
            
            pgpPub.encode(pOut);

            if (notEqual(bOut.toByteArray(), testPubKey))    
            {
                return new SimpleTestResult(false, getName() + ": public key rewrite failed");
            }
            
            //
            // Read the public key
            //
            PGPPublicKeyRing     pgpPubV3 = new PGPPublicKeyRing(testPubKeyV3);
            PublicKey            pubKeyV3 = pgpPub.getPublicKey().getKey("BC");

            //
            // write a V3 public key
            //
            bOut = new ByteArrayOutputStream();
            pOut = new BCPGOutputStream(bOut);
            
            pgpPubV3.encode(pOut);

            //
            // Read a v3 private key
            //
            char[]                  passP = "FIXCITY_QA".toCharArray();

            PGPSecretKeyRing        pgpPriv = new PGPSecretKeyRing(testPrivKeyV3);

            PGPPrivateKey            pgpPrivKey = pgpPriv.getSecretKey().extractPrivateKey(passP, "BC");

            //
            // write a v3 private key
            //
            bOut = new ByteArrayOutputStream();
            pOut = new BCPGOutputStream(bOut);
            
            pgpPriv.encode(pOut);

            if (notEqual(bOut.toByteArray(), testPrivKeyV3))    
            {
                return new SimpleTestResult(false, getName() + ": private key V3 rewrite failed");
            }

            //
            // Read the private key
            //
            pgpPriv = new PGPSecretKeyRing(testPrivKey);

            pgpPrivKey = pgpPriv.getSecretKey().extractPrivateKey(pass, "BC");
            
            //
            // write a private key
            //
            bOut = new ByteArrayOutputStream();
            pOut = new BCPGOutputStream(bOut);
            
            pgpPriv.encode(pOut);

            if (notEqual(bOut.toByteArray(), testPrivKey))    
            {
                return new SimpleTestResult(false, getName() + ": private key rewrite failed");
            }
            

            //
            // test encryption
            //
            Cipher c = Cipher.getInstance("RSA", "BC");

            c.init(Cipher.ENCRYPT_MODE, pubKey);
            
            byte[]  in = "hello world".getBytes();

            byte[]  out = c.doFinal(in);
            
            c.init(Cipher.DECRYPT_MODE, pgpPrivKey.getKey());
            
            out = c.doFinal(out);
            
            if (notEqual(in, out))
            {
                return new SimpleTestResult(false, getName() + ": decryption failed.");
            }

            //
            // test signature message
            //
            PGPObjectFactory                pgpFact = new PGPObjectFactory(sig1);

            PGPCompressedData             c1 = (PGPCompressedData)pgpFact.nextObject();

            pgpFact = new PGPObjectFactory(c1.getDataStream());
            
            PGPOnePassSignatureList     p1 = (PGPOnePassSignatureList)pgpFact.nextObject();
            
            PGPOnePassSignature            ops = p1.get(0);
            
            PGPLiteralData                    p2 = (PGPLiteralData)pgpFact.nextObject();

            InputStream                         dIn = p2.getInputStream();
            int                                     ch;

            ops.initVerify(pgpPub.getPublicKey(ops.getKeyID()), "BC");
            
            while ((ch = dIn.read()) >= 0)
            {
                ops.update((byte)ch);
            }

            PGPSignatureList                        p3 = (PGPSignatureList)pgpFact.nextObject();

            if (!ops.verify(p3.get(0)))
            {
                return new SimpleTestResult(false, getName() + ": Failed signature check");
            }
            
            //
            // encrypted message - read subkey
            //
            pgpPriv = new PGPSecretKeyRing(subKey);

            //
            // encrypted message
            //
            byte[]    text = { (byte)'h', (byte)'e', (byte)'l', (byte)'l', (byte)'o', (byte)' ', (byte)'w', (byte)'o', (byte)'r', (byte)'l', (byte)'d', (byte)'!', (byte)'\n' };
            
            PGPObjectFactory pgpF = new PGPObjectFactory(enc1);

            PGPEncryptedDataList            encList = (PGPEncryptedDataList)pgpF.nextObject();
        
            PGPPublicKeyEncryptedData    encP = (PGPPublicKeyEncryptedData)encList.get(0);
            
            pgpPrivKey = pgpPriv.getSecretKey(encP.getKeyID()).extractPrivateKey(pass, "BC");

            InputStream clear = encP.getDataStream(pgpPrivKey, "BC");
                     
            pgpFact = new PGPObjectFactory(clear);

            c1 = (PGPCompressedData)pgpFact.nextObject();

            pgpFact = new PGPObjectFactory(c1.getDataStream());
            
            PGPLiteralData    ld = (PGPLiteralData)pgpFact.nextObject();
        
            bOut = new ByteArrayOutputStream();
            
            if (!ld.getFileName().equals("test.txt"))
            {
                throw new RuntimeException("wrong filename in packet");
            }

            InputStream    inLd = ld.getDataStream();
            
            while ((ch = inLd.read()) >= 0)
            {
                bOut.write(ch);
            }

            if (notEqual(bOut.toByteArray(), text))
            {
                return new SimpleTestResult(false, getName() + ": wrong plain text in decrypted packet");
            }
            
            //
            // encrypt - short message
            //
            byte[]    shortText = { (byte)'h', (byte)'e', (byte)'l', (byte)'l', (byte)'o' };
        
            ByteArrayOutputStream        cbOut = new ByteArrayOutputStream();
            PGPEncryptedDataGenerator    cPk = new PGPEncryptedDataGenerator(SymmetricKeyAlgorithmTags.CAST5, new SecureRandom(), "BC");            
            PGPPublicKey                        puK = pgpPriv.getSecretKey(encP.getKeyID()).getPublicKey();
            
            cPk.addMethod(puK);
            
            OutputStream    cOut = cPk.open(cbOut, shortText.length);

            cOut.write(shortText);

            cOut.close();

            pgpF = new PGPObjectFactory(cbOut.toByteArray());

            encList = (PGPEncryptedDataList)pgpF.nextObject();
        
            encP = (PGPPublicKeyEncryptedData)encList.get(0);
            
            pgpPrivKey = pgpPriv.getSecretKey(encP.getKeyID()).extractPrivateKey(pass, "BC");

            clear = encP.getDataStream(pgpPrivKey, "BC");
            
            bOut.reset();
            
            while ((ch = clear.read()) >= 0)
            {
                bOut.write(ch);
            }

            out = bOut.toByteArray();

            if (notEqual(out, shortText))
            {
                return new SimpleTestResult(false, getName() + ": wrong plain text in generated short text packet");
            }
            
            //
            // encrypt
            //
            cbOut = new ByteArrayOutputStream();
            cPk = new PGPEncryptedDataGenerator(SymmetricKeyAlgorithmTags.CAST5, new SecureRandom(), "BC");            
            puK = pgpPriv.getSecretKey(encP.getKeyID()).getPublicKey();
            
            cPk.addMethod(puK);
            
            cOut = cPk.open(cbOut, text.length);

            cOut.write(text);

            cOut.close();

            pgpF = new PGPObjectFactory(cbOut.toByteArray());

            encList = (PGPEncryptedDataList)pgpF.nextObject();
        
            encP = (PGPPublicKeyEncryptedData)encList.get(0);
            
            pgpPrivKey = pgpPriv.getSecretKey(encP.getKeyID()).extractPrivateKey(pass, "BC");

            clear = encP.getDataStream(pgpPrivKey, "BC");
            
            bOut.reset();
            
            while ((ch = clear.read()) >= 0)
            {
                bOut.write(ch);
            }

            out = bOut.toByteArray();

            if (notEqual(out, text))
            {
                return new SimpleTestResult(false, getName() + ": wrong plain text in generated packet");
            }
            
            //
            // read public key with sub key.
            //
            pgpF = new PGPObjectFactory(subPubKey);
            Object    o;
            
            while ((o = pgpFact.nextObject()) != null)
            {
                // System.out.println(o);
            }

            //
            // key pair generation - CAST5 encryption
            //
            char[]                    passPhrase = "hello".toCharArray();
            
            KeyPairGenerator    kpg = KeyPairGenerator.getInstance("RSA", "BC");
        
            kpg.initialize(1024);
        
            KeyPair                    kp = kpg.generateKeyPair();

            PGPSecretKey    secretKey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, PublicKeyAlgorithmTags.RSA_GENERAL, kp.getPublic(), kp.getPrivate(), new Date(), "fred", SymmetricKeyAlgorithmTags.CAST5, passPhrase, null, null, new SecureRandom(), "BC");
        
            PGPPublicKey    key = secretKey.getPublicKey();

            it = key.getUserIDs();

            uid = (String)it.next();

            it = key.getSignaturesForID(uid);

            sig = (PGPSignature)it.next();

            sig.initVerify(key, "BC");

            if (!sig.verifyCertification(uid, key))
            {
                return new SimpleTestResult(false, getName() +": failed to verify certification");
            }
    
            pgpPrivKey = secretKey.extractPrivateKey(passPhrase, "BC");
            
            key = PGPPublicKey.removeCertification(key, uid, sig);
            
            if (key == null)
            {
                return new SimpleTestResult(false, getName() +": failed certification removal");
            }
            
            byte[]    keyEnc = key.getEncoded();
            
            key = PGPPublicKey.addCertification(key, uid, sig);
            
            keyEnc = key.getEncoded();

            PGPSignatureGenerator sGen = new PGPSignatureGenerator(PublicKeyAlgorithmTags.RSA_GENERAL, HashAlgorithmTags.SHA1, "BC");
            
            sGen.initSign(PGPSignature.KEY_REVOCATION, secretKey.extractPrivateKey(passPhrase, "BC"));

            sig = sGen.generateCertification(key);

            key = PGPPublicKey.addCertification(key, sig);

            keyEnc = key.getEncoded();

            PGPPublicKeyRing    tmpRing = new PGPPublicKeyRing(keyEnc);

            key = tmpRing.getPublicKey();

            Iterator            sgIt = key.getSignaturesOfType(PGPSignature.KEY_REVOCATION);

            sig = (PGPSignature)sgIt.next();

            sig.initVerify(key, "BC");

            if (!sig.verifyCertification(key))
            {
                return new SimpleTestResult(false, getName() +": failed to verify revocation certification");
            }

            //
            // use of PGPKeyPair
            //
            PGPKeyPair    pgpKp = new PGPKeyPair(PGPPublicKey.RSA_GENERAL , kp.getPublic(), kp.getPrivate(), new Date(), "BC");
            
            PGPPublicKey k1 = pgpKp.getPublicKey();
            
            PGPPrivateKey k2 = pgpKp.getPrivateKey();
            
            k1.getEncoded();
            
            //
            // key pair generation - AES_256 encryption.
            //
            kp = kpg.generateKeyPair();

            secretKey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, PublicKeyAlgorithmTags.RSA_GENERAL, kp.getPublic(), kp.getPrivate(), new Date(), "fred", SymmetricKeyAlgorithmTags.AES_256, passPhrase, null, null, new SecureRandom(), "BC");
        
            secretKey.extractPrivateKey(passPhrase, "BC");
            
            secretKey.encode(new ByteArrayOutputStream());
            
            //
            // secret key password changing.
            //
            String  newPass = "newPass";
            
            secretKey = PGPSecretKey.copyWithNewPassword(secretKey, passPhrase, newPass.toCharArray(), secretKey.getKeyEncryptionAlgorithm(), new SecureRandom(), "BC");
            
            secretKey.extractPrivateKey(newPass.toCharArray(), "BC");
            
            secretKey.encode(new ByteArrayOutputStream());
            
            key = secretKey.getPublicKey();

            key.encode(new ByteArrayOutputStream());
            
            it = key.getUserIDs();

            uid = (String)it.next();

            it = key.getSignaturesForID(uid);

            sig = (PGPSignature)it.next();

            sig.initVerify(key, "BC");

            if (!sig.verifyCertification(uid, key))
            {
                return new SimpleTestResult(false, getName() +": failed to verify certification");
            }
    
            pgpPrivKey = secretKey.extractPrivateKey(newPass.toCharArray(), "BC");
            
            //
            // signature generation
            //
            String                                data = "hello world!";
            
            bOut = new ByteArrayOutputStream();
            
            ByteArrayInputStream        testIn = new ByteArrayInputStream(data.getBytes());
            
            sGen = new PGPSignatureGenerator(PublicKeyAlgorithmTags.RSA_GENERAL, HashAlgorithmTags.SHA1, "BC");
        
            sGen.initSign(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

            PGPCompressedDataGenerator cGen = new PGPCompressedDataGenerator(
                                                        PGPCompressedData.ZIP);

            BCPGOutputStream            bcOut = new BCPGOutputStream(cGen.open(bOut));

            sGen.generateOnePassVersion(false).encode(bcOut);

            PGPLiteralDataGenerator    lGen = new PGPLiteralDataGenerator();
            OutputStream                    lOut = lGen.open(bcOut, PGPLiteralData.BINARY, "_CONSOLE", data.getBytes().length, new Date());
            
            while ((ch = testIn.read()) >= 0)
            {
                lOut.write(ch);
                sGen.update((byte)ch);
            }

            lGen.close();

            sGen.generate().encode(bcOut);

            cGen.close();

            //
            // verify generated signature
            //
            pgpFact = new PGPObjectFactory(bOut.toByteArray());

            c1 = (PGPCompressedData)pgpFact.nextObject();

            pgpFact = new PGPObjectFactory(c1.getDataStream());
            
            p1 = (PGPOnePassSignatureList)pgpFact.nextObject();
            
            ops = p1.get(0);
            
            p2 = (PGPLiteralData)pgpFact.nextObject();

            dIn = p2.getInputStream();

            ops.initVerify(secretKey.getPublicKey(), "BC");
            
            while ((ch = dIn.read()) >= 0)
            {
                ops.update((byte)ch);
            }

            p3 = (PGPSignatureList)pgpFact.nextObject();

            if (!ops.verify(p3.get(0)))
            {
                return new SimpleTestResult(false, getName() + ": Failed generated signature check");
            }
            
            //
            // signature generation - version 3
            //
            bOut = new ByteArrayOutputStream();
            
            testIn = new ByteArrayInputStream(data.getBytes());
            PGPV3SignatureGenerator    sGenV3 = new PGPV3SignatureGenerator(PGPPublicKey.RSA_GENERAL, PGPUtil.SHA1, "BC");
        
            sGen.initSign(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

            cGen = new PGPCompressedDataGenerator(
                                                                    PGPCompressedData.ZIP);

            bcOut = new BCPGOutputStream(cGen.open(bOut));

            sGen.generateOnePassVersion(false).encode(bcOut);

            lGen = new PGPLiteralDataGenerator();
            lOut = lGen.open(bcOut, PGPLiteralData.BINARY, "_CONSOLE", data.getBytes().length, new Date());
            
            while ((ch = testIn.read()) >= 0)
            {
                lOut.write(ch);
                sGen.update((byte)ch);
            }

            sGen.generate().encode(bcOut);

            lGen.close();

            cGen.close();

            //
            // verify generated signature
            //
            pgpFact = new PGPObjectFactory(bOut.toByteArray());

            c1 = (PGPCompressedData)pgpFact.nextObject();

            pgpFact = new PGPObjectFactory(c1.getDataStream());
            
            p1 = (PGPOnePassSignatureList)pgpFact.nextObject();
            
            ops = p1.get(0);
            
            p2 = (PGPLiteralData)pgpFact.nextObject();

            dIn = p2.getInputStream();

            ops.initVerify(secretKey.getPublicKey(), "BC");
            
            while ((ch = dIn.read()) >= 0)
            {
                ops.update((byte)ch);
            }

            p3 = (PGPSignatureList)pgpFact.nextObject();

            if (!ops.verify(p3.get(0)))
            {
                return new SimpleTestResult(false, getName() + ": Failed v3 generated signature check");
            }
            
            //
            // extract PGP 8 private key
            //
            pgpPriv = new PGPSecretKeyRing(pgp8Key);
            
            secretKey = pgpPriv.getSecretKey();
            
            pgpPrivKey = secretKey.extractPrivateKey(pgp8Pass, "BC");

            return fingerPrintTest();
        }
        catch (Exception e)
        {    
            e.printStackTrace();
            return new SimpleTestResult(false, getName() + ": exception - " + e.toString());
        }
    }

    public String getName()
    {
        return "PGPRSATest";
    }

    public static void main(
        String[]    args)
    {
        Security.addProvider(new BouncyCastleProvider());

        Test            test = new PGPRSATest();
        TestResult      result = test.perform();

        System.out.println(result.toString());
    }
}
