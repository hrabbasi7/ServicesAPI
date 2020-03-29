package org.bouncycastle.bcpg;

import java.io.*;
import org.bouncycastle.openpgp.logging.*;
/**
 * reader for PGP objects
 */
public class BCPGInputStream
    extends InputStream implements PacketTags
{
    InputStream    in;
    boolean        next = false;
    int            nextB;

    public BCPGInputStream(
        InputStream    in)
    {
        this.in = in;
    }

    public int available()
        throws IOException
    {
        return in.available();
    }

    public int read()
        throws IOException
    {
        if (next)
        {
            next = false;
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Return Value --- read --->" + nextB);
            return nextB;
        }
        else
        {
            int value = in.read();
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Read Value --- read --->" + value);
            return value;
        }
    }

    public void readFully(
        byte[]    buf,
        int       off,
        int       len)
        throws IOException
    {
        if (len > 0)
        {
            int    b = this.read();
            if (b < 0)
            {
                throw new EOFException();
            }

            buf[off] = (byte)b;
            off++;
            len--;
        }

        while (len > 0)
        {
            int    l = in.read(buf, off, len);
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Read Value --- readFully --- Buffer value--->" + new String(buf));
            if (l < 0)
            {
                throw new EOFException();
            }

            off += l;
            len -= l;
        }
    }

    public void readFully(
        byte[]    buf)
        throws IOException
    {
        readFully(buf, 0, buf.length);
    }

    /**
     * returns the nest packet tag in the stream.
     *
     * @return the tag number.
     *
     * @throws IOException
     */
    public int nextPacketTag()
        throws IOException
    {
        if (!next)
        {
            try
            {
                nextB = in.read();
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Read Value --- nextPacketTag --->" + nextB);
            }
            catch (EOFException e)
            {
                nextB = -1;
            }
        }

        next = true;

        if (nextB >= 0)
        {
            if ((nextB & 0x40) != 0)    // new
            {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Return Value --- nextPacketTag --->" + (nextB & 0x3f));
              return (nextB & 0x3f);
            }
            else    // old
            {
              CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Return Value --- nextPacketTag --->" + ((nextB & 0x3f) >> 2));
              return ((nextB & 0x3f) >> 2);
            }
        }
        return nextB;
    }

    public Packet readPacket()
        throws IOException
    {
        int    hdr = this.read();
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Header Value --- readPacket --->" + hdr);
        if (hdr < 0)
        {
            return null;
        }

        if ((hdr & 0x80) == 0)
        {
          CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_WARNING),"Invalid Header Value --- readPacket--->" + hdr);
          throw new IOException("invalid header encountered");
        }

        boolean    newPacket = (hdr & 0x40) != 0;
        CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Is New Packet Type --- readPacket --->" + newPacket);
        int        tag = 0;
        int        bodyLen = 0;
        boolean    partial = false;

        if (newPacket)
        {
            tag = hdr & 0x3f;
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Tag Type --- readPacket --->" + tag);

            int    l = this.read();
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"length Type --- readPacket --->" + l);
            if (l < 192)
            {
                bodyLen = l;
            }
            else if (l <= 223)
            {
                int b = in.read();
                CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Read value --- readPacket --->" + b);
                bodyLen = ((l - 192) << 8) + (b) + 192;
            }
            else if (l == 255)
            {
                bodyLen = (in.read() << 24) | (in.read() << 16) |  (in.read() << 8)  | in.read();
            }
            else
            {
                partial = true;
                bodyLen = 1 << (l & 0x1f);
            }
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Total Body Length [New] --->" + bodyLen);
        }
        else
        {
            int lengthType = hdr & 0x3;
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"length Type --->" + lengthType);
            tag = (hdr & 0x3f) >> 2;
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Tag Type --->" + tag);
            switch (lengthType)
            {
            case 0:
                bodyLen = this.read();
                break;
            case 1:
                bodyLen = (this.read() << 8) | this.read();
                break;
            case 2:
                bodyLen = (this.read() << 24) | (this.read() << 16) | (this.read() << 8) | this.read();
                break;
            case 3:
                partial = true;
                break;
            default:
                throw new IOException("unknown length type encountered");
            }
            CommonUtilities.getLogger().log(LogLevel.getLevel(Constants.LOG_FINEST),"Total Body Length [Old] --->" + bodyLen);
        }

        ContainedPacket    packet = null;
        BCPGInputStream    objStream;

        if (bodyLen == 0 && partial)
        {
            objStream = this;
        }
        else
        {
            objStream = new BCPGInputStream(new PartialInputStream(this, partial, bodyLen));
        }

        switch (tag)
        {
        case RESERVED:
            return new InputStreamPacket(objStream);
        case PUBLIC_KEY_ENC_SESSION:
            return new PublicKeyEncSessionPacket(objStream);
        case SIGNATURE:
            return new SignaturePacket(objStream);
        case SYMMETRIC_KEY_ENC_SESSION:
            return new SymmetricKeyEncSessionPacket(objStream);
        case ONE_PASS_SIGNATURE:
            return new OnePassSignaturePacket(objStream);
        case SECRET_KEY:
            return new SecretKeyPacket(objStream);
        case PUBLIC_KEY:
            return new PublicKeyPacket(objStream);
        case SECRET_SUBKEY:
            return new SecretSubkeyPacket(objStream);
        case COMPRESSED_DATA:
            return new CompressedDataPacket(objStream);
        case SYMMETRIC_KEY_ENC:
            return new SymmetricEncDataPacket(objStream);
        case MARKER:
            return new MarkerPacket(objStream);
        case LITERAL_DATA:
            return new LiteralDataPacket(objStream);
        case TRUST:
            return new TrustPacket(objStream);
        case USER_ID:
            return new UserIDPacket(objStream);
        case USER_ATTRIBUTE:
            return new UserAttributePacket(objStream);
        case PUBLIC_SUBKEY:
            return new PublicSubkeyPacket(objStream);
        case SYM_ENC_INTEGRITY_PRO:
            return new SymmetricEncIntegrityPacket(objStream);
        case MOD_DETECTION_CODE:
            return new ModDetectionCodePacket(objStream);
        case EXPERIMENTAL_1:
        case EXPERIMENTAL_2:
        case EXPERIMENTAL_3:
        case EXPERIMENTAL_4:
            return new ExperimentalPacket(tag, objStream);
        default:
            throw new IOException("unknown packet type encountered: " + tag);
        }
    }

    /**
     * a stream that overlays our input stream, allowing the user to only read a segment of it.
     */
    private static class PartialInputStream
        extends InputStream
    {
        private BCPGInputStream     in;
        private boolean             partial;
        private int                 dataLength;

        PartialInputStream(
            BCPGInputStream  in,
            boolean          partial,
            int              dataLength)
        {
            this.in = in;
            this.partial = partial;
            this.dataLength = dataLength;
        }

        public int available()
            throws IOException
        {
            int avail = in.available();

            if (avail <= dataLength)
            {
                return avail;
            }
            else
            {
                if (partial && dataLength == 0)
                {
                    return 1;
                }
                return dataLength;
            }
        }

        public int read()
            throws IOException
        {
            if (dataLength > 0)
            {
                dataLength--;
                return in.read();
            }
            else if (partial)
            {
                int            l = in.read();

                if (l < 0)
                {
                    return -1;
                }

                partial = false;
                if (l < 192)
                {
                    dataLength = l;
                }
                else if (l < 223)
                {
                    dataLength = ((l - 192) << 8) + (in.read()) + 192;
                }
                else if (l == 255)
                {
                    dataLength = (in.read() << 24) | (in.read() << 16) |  (in.read() << 8)  | in.read();
                }
                else
                {
                    partial = true;
                    dataLength = 1 << (l & 0x1f);
                }

                return this.read();
            }

            return -1;
        }
    }
}
