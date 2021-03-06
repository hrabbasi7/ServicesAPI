package org.bouncycastle.openpgp;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.bouncycastle.bcpg.InputStreamPacket;
import org.bouncycastle.bcpg.SymmetricEncIntegrityPacket;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;

public abstract class PGPEncryptedData
    implements SymmetricKeyAlgorithmTags
{
    protected class TruncatedStream extends InputStream
    {
        int[]         lookAhead = new int[22];
        int           bufPtr;
        InputStream   in;
        
        TruncatedStream(
            InputStream    in) 
            throws IOException
        {
            for (int i = 0; i != lookAhead.length; i++)
            {
                lookAhead[i] = in.read();
            }
            
            bufPtr = 0;
            this.in = in;
        }

        public int read() 
            throws IOException
        {
            int    ch = in.read();
            
            if (ch >= 0)
            {
                int    c = lookAhead[bufPtr];
                
                lookAhead[bufPtr] = ch;
                bufPtr = (bufPtr + 1) % lookAhead.length;
                
                return c;
            }
            
            return -1;
        }
        
        int[] getLookAhead()
        {
            int[]    tmp = new int[lookAhead.length];
            int    count = 0;
            
            for (int i = bufPtr; i != lookAhead.length; i++)
            {
                tmp[count++] = lookAhead[i];
            }
            for (int i = 0; i != bufPtr; i++)
            {
                tmp[count++] = lookAhead[i];
            }
            
            return tmp;
        }
    }
    
    InputStreamPacket        encData;
    InputStream              encStream;
    TruncatedStream          truncStream;
    
    PGPEncryptedData(
        InputStreamPacket    encData)
    {
        this.encData = encData;
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
     * Return true if the message is integrity protected.
     * @return true if there is a modification detection code package associated with this stream
     */
    public boolean isIntegrityProtected()
    {
        return (encData instanceof SymmetricEncIntegrityPacket);
    }
    
    /**
     * Note: This can only be called after the message has been read.
     * 
     * @return true if the message verifies, false otherwise.
     * @throws PGPException if the message is not integrity protected.
     */
    public boolean verify()
        throws PGPException, IOException
    {
        if (!this.isIntegrityProtected())
        {
            throw new PGPException("data not integrity protected.");
        }
        
        DigestInputStream    dIn = (DigestInputStream)encStream;
        MessageDigest        hash = dIn.getMessageDigest();
        
        //
        // process the MDC packet
        //
        int[]    lookAhead = truncStream.getLookAhead();

        hash.update((byte)lookAhead[0]);
        hash.update((byte)lookAhead[1]);

        byte[]    digest = hash.digest();
        byte[]  streamDigest = new byte[digest.length];
        
        for (int i = 0; i != streamDigest.length; i++)
        {
            streamDigest[i] = (byte)lookAhead[i + 2];
        }

        return MessageDigest.isEqual(digest, streamDigest);
    }
}
