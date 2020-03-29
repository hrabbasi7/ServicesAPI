package org.bouncycastle.openpgp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.bcpg.S2K;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.util.encoders.Base64;


/**
 * Basic utility class
 */
public class PGPUtil
    implements HashAlgorithmTags
{
    private    static String    defProvider = "BC";

    /**
     * Return the provider that will be used by factory classes in situations
     * where a provider must be determined on the fly.
     *
     * @return String
     */
    public static String getDefaultProvider()
    {
        return defProvider;
    }

    /**
     * Set the provider to be used by the package when it is necessary to
     * find one on the fly.
     *
     * @param provider
     */
    public static void setDefaultProvider(
        String    provider)
    {
        defProvider = provider;
    }

    static String getDigestName(
        int        hashAlgorithm)
        throws PGPException
    {
        switch (hashAlgorithm)
        {
        case HashAlgorithmTags.SHA1:
            return "SHA1";
        case HashAlgorithmTags.MD2:
            return "MD2";
        case HashAlgorithmTags.MD5:
            return "MD5";
        case HashAlgorithmTags.RIPEMD160:
            return "RIPEMD160";
        default:
            throw new PGPException("unknown hash algorithm tag in getDigestName: " + hashAlgorithm);
        }
    }

    static String getSignatureName(
        int        keyAlgorithm,
        int        hashAlgorithm)
        throws PGPException
    {
        String     encAlg;

        switch (keyAlgorithm)
        {
        case PublicKeyAlgorithmTags.RSA_GENERAL:
        case PublicKeyAlgorithmTags.RSA_SIGN:
            encAlg = "RSA";
            break;
        case PublicKeyAlgorithmTags.DSA:
            encAlg = "DSA";
            break;
        case PublicKeyAlgorithmTags.ELGAMAL_ENCRYPT: // in some malformed cases.
        case PublicKeyAlgorithmTags.ELGAMAL_GENERAL:
            encAlg = "ElGamal";
            break;
        default:
            throw new PGPException("unknown algorithm tag in signature:" + keyAlgorithm);
        }

        return getDigestName(hashAlgorithm) + "with" + encAlg;
    }

    static String getSymmetricCipherName(
        int    algorithm)
        throws PGPException
    {
        String    algName;

        switch (algorithm)
        {
        case SymmetricKeyAlgorithmTags.NULL:
            return null;
        case SymmetricKeyAlgorithmTags.TRIPLE_DES:
            return "DESEDE";
        case SymmetricKeyAlgorithmTags.IDEA:
            return "IDEA";
        case SymmetricKeyAlgorithmTags.CAST5:
            return "CAST5";
        case SymmetricKeyAlgorithmTags.BLOWFISH:
            return "Blowfish";
        case SymmetricKeyAlgorithmTags.SAFER:
            return "SAFER";
        case SymmetricKeyAlgorithmTags.DES:
            return "DES";
        case SymmetricKeyAlgorithmTags.AES_128:
            return "AES";
        case SymmetricKeyAlgorithmTags.AES_192:
            return "AES";
        case SymmetricKeyAlgorithmTags.AES_256:
            return "AES";
        case SymmetricKeyAlgorithmTags.TWOFISH:
            return "Twofish";
        default:
            throw new PGPException("unknown symmetric algorithm: " + algorithm);
        }
    }

    public static SecretKey makeRandomKey(
        int             algorithm,
        SecureRandom    random)
        throws PGPException
    {
        String    algName = null;
        int        keySize = 0;

        switch (algorithm)
        {
        case SymmetricKeyAlgorithmTags.TRIPLE_DES:
            keySize = 192;
            algName = "DES_EDE";
            break;
        case SymmetricKeyAlgorithmTags.IDEA:
            keySize = 128;
            algName = "IDEA";
            break;
        case SymmetricKeyAlgorithmTags.CAST5:
            keySize = 128;
            algName = "CAST5";
            break;
        case SymmetricKeyAlgorithmTags.BLOWFISH:
            keySize = 128;
            algName = "Blowfish";
            break;
        case SymmetricKeyAlgorithmTags.SAFER:
            keySize = 128;
            algName = "SAFER";
            break;
        case SymmetricKeyAlgorithmTags.DES:
            keySize = 64;
            algName = "DES";
            break;
        case SymmetricKeyAlgorithmTags.AES_128:
            keySize = 128;
            algName = "AES";
            break;
        case SymmetricKeyAlgorithmTags.AES_192:
            keySize = 192;
            algName = "AES";
            break;
        case SymmetricKeyAlgorithmTags.AES_256:
            keySize = 256;
            algName = "AES";
            break;
        case SymmetricKeyAlgorithmTags.TWOFISH:
            keySize = 256;
            algName = "Twofish";
            break;
        default:
            throw new PGPException("unknown symmetric algorithm: " + algorithm);
        }

        byte[]    keyBytes = new byte[(keySize + 7) / 8];

        random.nextBytes(keyBytes);

        return new SecretKeySpec(keyBytes, algName);
    }

    public static SecretKey makeKeyFromPassPhrase(
        int       algorithm,
        char[]    passPhrase,
        String    provider)
        throws NoSuchProviderException, PGPException
    {
        return makeKeyFromPassPhrase(algorithm, null, passPhrase, provider);
    }

    public static SecretKey makeKeyFromPassPhrase(
        int     algorithm,
        S2K     s2k,
        char[]  passPhrase,
        String  provider)
        throws PGPException, NoSuchProviderException
    {
        String    algName = null;
        int        keySize = 0;

        switch (algorithm)
        {
        case SymmetricKeyAlgorithmTags.TRIPLE_DES:
            keySize = 192;
            algName = "DES_EDE";
            break;
        case SymmetricKeyAlgorithmTags.IDEA:
            keySize = 128;
            algName = "IDEA";
            break;
        case SymmetricKeyAlgorithmTags.CAST5:
            keySize = 128;
            algName = "CAST5";
            break;
        case SymmetricKeyAlgorithmTags.BLOWFISH:
            keySize = 128;
            algName = "Blowfish";
            break;
        case SymmetricKeyAlgorithmTags.SAFER:
            keySize = 128;
            algName = "SAFER";
            break;
        case SymmetricKeyAlgorithmTags.DES:
            keySize = 64;
            algName = "DES";
            break;
        case SymmetricKeyAlgorithmTags.AES_128:
            keySize = 128;
            algName = "AES";
            break;
        case SymmetricKeyAlgorithmTags.AES_192:
            keySize = 192;
            algName = "AES";
            break;
        case SymmetricKeyAlgorithmTags.AES_256:
            keySize = 256;
            algName = "AES";
            break;
        case SymmetricKeyAlgorithmTags.TWOFISH:
            keySize = 256;
            algName = "Twofish";
            break;
        default:
            throw new PGPException("unknown symmetric algorithm: " + algorithm);
        }

        byte[]           pBytes = new byte[passPhrase.length];
        MessageDigest    digest;

        for (int i = 0; i != passPhrase.length; i++)
        {
            pBytes[i] = (byte)passPhrase[i];
        }

        byte[]    keyBytes = new byte[(keySize + 7) / 8];

        int    generatedBytes = 0;
        int    loopCount = 0;

        while (generatedBytes < keyBytes.length)
        {
            if (s2k != null)
            {
                try
                {
                    switch (s2k.getHashAlgorithm())
                    {
                    case HashAlgorithmTags.SHA1:
                        digest = MessageDigest.getInstance("SHA1", provider);
                        break;
                    case HashAlgorithmTags.MD5:
                        digest = MessageDigest.getInstance("MD5", provider);
                        break;
                    default:
                        throw new PGPException("unknown hash algorithm: " + s2k.getHashAlgorithm());
                    }
                }
                catch (NoSuchAlgorithmException e)
                {
                    throw new PGPException("can't find S2K digest", e);
                }

                for (int i = 0; i != loopCount; i++)
                {
                    digest.update((byte)0);
                }

                byte[]    iv = s2k.getIV();

                switch (s2k.getType())
                {
                case S2K.SIMPLE:
                    digest.update(pBytes);
                    break;
                case S2K.SALTED:
                    digest.update(iv);
                    digest.update(pBytes);
                    break;
                case S2K.SALTED_AND_ITERATED:
                    long    count = s2k.getIterationCount();
                    digest.update(iv);
                    digest.update(pBytes);

                    count -= iv.length + pBytes.length;

                    while (count > 0)
                    {
                        if (count < iv.length)
                        {
                            digest.update(iv, 0, (int)count);
                            break;
                        }
                        else
                        {
                            digest.update(iv);
                            count -= iv.length;
                        }

                        if (count < pBytes.length)
                        {
                            digest.update(pBytes, 0, (int)count);
                            count = 0;
                        }
                        else
                        {
                            digest.update(pBytes);
                            count -= pBytes.length;
                        }
                    }
                    break;
                default:
                    throw new PGPException("unknown S2K type: " + s2k.getType());
                }
            }
            else
            {
                try
                {
                    digest = MessageDigest.getInstance("MD5", provider);

                    for (int i = 0; i != loopCount; i++)
                    {
                        digest.update((byte)0);
                    }

                    digest.update(pBytes);
                }
                catch (NoSuchAlgorithmException e)
                {
                    throw new PGPException("can't find MD5 digest", e);
                }
            }

            byte[]    dig = digest.digest();

            if (dig.length > (keyBytes.length - generatedBytes))
            {
                System.arraycopy(dig, 0, keyBytes, generatedBytes, keyBytes.length - generatedBytes);
            }
            else
            {
                System.arraycopy(dig, 0, keyBytes, generatedBytes, dig.length);
            }

            generatedBytes += dig.length;

            loopCount++;
        }

        for (int i = 0; i != pBytes.length; i++)
        {
            pBytes[i] = 0;
        }

        return new SecretKeySpec(keyBytes, algName);
    }

    /**
     * write out the passed in file as a literal data packet.
     *
     * @param file
     * @param fileType the LiteralData type for the file.
     * @param out
     * @throws IOException
     */
    public static void writeFileToLiteralData(
        OutputStream    out,
        char            fileType,
        File            file)
        throws IOException
    {
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream            pOut = lData.open(out, fileType, file.getName(), file.length(), new Date(file.lastModified()));
        FileInputStream         in = new FileInputStream(file);
        byte[]                  buf = new byte[4096];
        int                     len;

        while ((len = in.read(buf)) > 0)
        {
            pOut.write(buf, 0, len);
        }

        lData.close();
    }

    /**
     * write out the passed in file as a literal data packet in partial packet format.
     *
     * @param file
     * @param fileType the LiteralData type for the file.
     * @param out
     * @param buffer buffer to be used to chunk the file into partial packets.
     * @throws IOException
     */
    public static void writeFileToLiteralData(
        OutputStream    out,
        char            fileType,
        File            file,
        byte[]          buffer)
        throws IOException
    {
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream            pOut = lData.open(out, fileType, file.getName(), new Date(file.lastModified()), buffer);
        FileInputStream         in = new FileInputStream(file);
        byte[]                  buf = new byte[buffer.length];
        int                     len;

        while ((len = in.read(buf)) > 0)
        {
            pOut.write(buf, 0, len);
        }

        lData.close();
    }

    private static final int READ_AHEAD = 60;

    private static boolean isPossiblyBase64(
        int    ch)
    {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')
                || (ch >= '0' && ch <= '9') || (ch == '+') || (ch == '/')
                || (ch == '\r') || (ch == '\n');
    }

    /**
     * Return either an ArmoredInputStream or a BCPGInputStream based on
     * whether the initial characters of the stream are binary PGP encodings or not.
     *
     * @param in the stream to be wrapped
     * @return a BCPGInputStream
     * @throws IOException
     */
    public static InputStream getDecoderStream(
        InputStream    in)
        throws IOException
    {
        if (!in.markSupported())
        {
            in = new BufferedInputStream(in);
        }

        in.mark(READ_AHEAD);

        int    ch = in.read();
        if ((ch & 0x80) != 0)
        {
            in.reset();
            return in;
        }
        else
        {
            if (!isPossiblyBase64(ch))
            {
                in.reset();

                return new ArmoredInputStream(in);
            }

            byte[]  buf = new byte[READ_AHEAD];
            int     count = 0;
            int     index = 1;

            buf[0] = (byte)ch;
            while (count != READ_AHEAD && (ch = in.read()) >= 0)
            {
                if (!isPossiblyBase64(ch))
                {
                    in.reset();

                    return new ArmoredInputStream(in);
                }

                if (ch != '\n' && ch != '\r')
                {
                    buf[index++] = (byte)ch;
                }

                count++;
            }

            in.reset();

            //
            // nothing but new lines, little else, assume regular armoring
            //
            if (count < 4)
            {
                return new ArmoredInputStream(in);
            }

            //
            // test our non-blank data
            //
            byte[]    firstBlock = new byte[8];

            System.arraycopy(buf, 0, firstBlock, 0, firstBlock.length);

            byte[]    decoded = Base64.decode(firstBlock);

            //
            // it's a base64 PGP block.
            //
            if ((decoded[0] & 0x80) != 0)
            {
                return new ArmoredInputStream(in, false);
            }

            return new ArmoredInputStream(in);
        }
    }
}
