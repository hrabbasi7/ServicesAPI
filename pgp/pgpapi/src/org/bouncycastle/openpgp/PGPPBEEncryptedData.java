package org.bouncycastle.openpgp;

import java.io.EOFException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.bcpg.BCPGInputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.InputStreamPacket;
import org.bouncycastle.bcpg.SymmetricEncIntegrityPacket;
import org.bouncycastle.bcpg.SymmetricKeyEncSessionPacket;
import org.bouncycastle.openpgp.logging.*;

/**
 * A password based encryption object.
 */
public class PGPPBEEncryptedData
    extends PGPEncryptedData
{
    SymmetricKeyEncSessionPacket    keyData;

   public PGPPBEEncryptedData(
        SymmetricKeyEncSessionPacket    keyData,
        InputStreamPacket               encData)
    {
        super(encData);

        this.keyData = keyData;
    }

    /**
     * Return the raw input stream for the data stream.
     *
     * @return InputStream
     */
    public InputStream getInputStream()
    {
        return encData.getInputStream();
    }

    /**
     * Return the decrypted input stream, using the passed in passPhrase.
     *
     * @param passPhrase
     * @param provider
     * @return InputStream
     * @throws PGPException
     * @throws NoSuchProviderException
     */
    public InputStream getDataStream(
        char[]                passPhrase,
        String                provider)
        throws PGPException, NoSuchProviderException
    {
        Cipher c;

        try
        {
            if (encData instanceof SymmetricEncIntegrityPacket)
            {
                c =
                    Cipher.getInstance(
                        PGPUtil.getSymmetricCipherName(keyData.getEncAlgorithm()) + "/CFB/NoPadding",
                        provider);
            }
            else
            {
                c =
                    Cipher.getInstance(
                        PGPUtil.getSymmetricCipherName(keyData.getEncAlgorithm()) + "/OpenPGPCFB/NoPadding",
                        provider);
            }
        }
        catch (NoSuchProviderException e)
        {
           throw e;
        }
        catch (PGPException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new PGPException("exception creating cipher", e);
        }

        if (c != null)
        {
            try
            {
                SecretKey    key = PGPUtil.makeKeyFromPassPhrase(keyData.getEncAlgorithm(), keyData.getS2K(), passPhrase, provider);

                int blockSize = c.getBlockSize();
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPPBEEncryptedData --- getDataStream --- Cipher Block Size --->" + blockSize);
                byte[]       iv = new byte[blockSize];

                c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

                encStream = new BCPGInputStream(new CipherInputStream(encData.getInputStream(), c));

                if (encData instanceof SymmetricEncIntegrityPacket)
                {
                    truncStream = new TruncatedStream(encStream);
                    encStream = new DigestInputStream(truncStream, MessageDigest.getInstance(PGPUtil.getDigestName(HashAlgorithmTags.SHA1), provider));
                }

                for (int i = 0; i != iv.length; i++)
                {
                    int    ch = encStream.read();
                    CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPPBEEncryptedData --- getDataStream --- IV COUNT---> " + i + " ----IV Value --->" + ch);
                    if (ch < 0)
                    {
                        throw new EOFException("unexpected end of stream.");
                    }

                    iv[i] = (byte)ch;
                }

                int    v1 = encStream.read(); // reading 9th byte
                int    v2 = encStream.read(); // reading 10th byte

                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"PGPPBEEncryptedData --- getDataStream --- V1---> " + v1 + " ----V2 --->" + v2);

                // Performaing conventional session key check
                if (v1 < 0 || v2 < 0)
                {
                    throw new EOFException("unexpected end of stream.");
                }

                //
                // some versions of PGP appear to produce 0 for the extra
                // bytes rather than repeating the two previous bytes
                //
                if (iv[iv.length - 2] != (byte)v1 && v1 != 0)// comparing 7th and 9th byte
                {
                    throw new PGPDataValidationException("data check failed.");
                }

                if (iv[iv.length - 1] != (byte)v2 && v2 != 0)// comparing 8th and 10th byte
                {
                    throw new PGPDataValidationException("data check failed.");
                }

                return encStream;
            }
            catch (PGPException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new PGPException("Exception creating cipher", e);
            }
        }
        else
        {
            return encData.getInputStream();
        }
    }
}
