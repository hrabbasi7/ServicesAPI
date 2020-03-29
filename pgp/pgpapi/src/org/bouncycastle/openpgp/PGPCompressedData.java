package org.bouncycastle.openpgp;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.bouncycastle.bcpg.BCPGInputStream;
import org.bouncycastle.bcpg.CompressedDataPacket;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;

/**
 * Compressed data objects.
 */
public class PGPCompressedData 
    implements CompressionAlgorithmTags
{
    CompressedDataPacket    data;
    
    public PGPCompressedData(
        BCPGInputStream    pIn)
        throws IOException
    {
        data = (CompressedDataPacket)pIn.readPacket();
    }
    
    /**
     * Return the algorithm used for compression
     * 
     * @return algorithm code
     */
    public int getAlgorithm()
    {
        return data.getAlgorithm();
    }
    
    /**
     * Return the raw input stream contained in the object.
     * 
     * @return InputStream
     */
    public InputStream getInputStream()
    {
        return data.getInputStream();
    }
    
    /**
     * Return an uncompressed input stream which allows reading of the 
     * compressed data.
     * 
     * @return InputStream
     * @throws PGPException
     */
    public InputStream getDataStream()
        throws PGPException
    {
        if (this.getAlgorithm() == ZIP)
        {
            return new InflaterInputStream(this.getInputStream(), new Inflater(true));
        }
        else if (this.getAlgorithm() == ZLIB)
        {
            return new InflaterInputStream(this.getInputStream());
        }
        
        throw new PGPException("can't recognise compression algorithm: " + this.getAlgorithm());
    }
}
