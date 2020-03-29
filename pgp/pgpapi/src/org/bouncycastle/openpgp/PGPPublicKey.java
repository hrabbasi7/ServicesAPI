package org.bouncycastle.openpgp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.bcpg.*;

import org.bouncycastle.jce.interfaces.ElGamalPublicKey;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.jce.spec.ElGamalPublicKeySpec;

/**
 * general class to handle a PGP public key object.
 */
public class PGPPublicKey
    implements PublicKeyAlgorithmTags
{
    private long    keyID;
    private byte[]  fingerprint;
    private int     keyStrength;
    
    PublicKeyPacket publicPk;
    TrustPacket     trustPk;
    ArrayList       keySigs = new ArrayList();
    ArrayList       ids = new ArrayList();
    ArrayList       idTrusts = new ArrayList();
    ArrayList       idSigs = new ArrayList();
    
    ArrayList       subSigs = null;

    private void init()
        throws IOException
    {
        BCPGKey                key = publicPk.getKey();
        
        if (publicPk.getVersion() <= 3)
        {
            RSAPublicBCPGKey    rK = (RSAPublicBCPGKey)key;
            
            this.keyID = rK.getModulus().longValue();
            
            try
            {
                MessageDigest   digest = MessageDigest.getInstance("MD5");
            
                byte[]  bytes = new MPInteger(rK.getModulus()).getEncoded();
                digest.update(bytes, 2, bytes.length - 2);
            
                bytes = new MPInteger(rK.getPublicExponent()).getEncoded();
                digest.update(bytes, 2, bytes.length - 2);
            
                this.fingerprint = digest.digest();
            }
            catch (NoSuchAlgorithmException e)
            {
                throw new IOException("can't find MD5");
            }

            this.keyStrength = rK.getModulus().bitLength();
        }
        else
        {
            byte[]             kBytes = publicPk.getEncodedContents();

            try
            {
                MessageDigest   digest = MessageDigest.getInstance("SHA1");
            
                digest.update((byte)0x99);
                digest.update((byte)(kBytes.length >> 8));
                digest.update((byte)kBytes.length);
                digest.update(kBytes);
                
                this.fingerprint = digest.digest();
            }
            catch (NoSuchAlgorithmException e)
            {
                throw new IOException("can't find SHA1");
            }
            
            this.keyID = ((long)(fingerprint[fingerprint.length - 8] & 0xff) << 56)
                            | ((long)(fingerprint[fingerprint.length - 7] & 0xff) << 48)
                            | ((long)(fingerprint[fingerprint.length - 6] & 0xff) << 40)
                            | ((long)(fingerprint[fingerprint.length - 5] & 0xff) << 32)
                            | ((long)(fingerprint[fingerprint.length - 4] & 0xff) << 24)
                            | ((long)(fingerprint[fingerprint.length - 3] & 0xff) << 16)
                            | ((long)(fingerprint[fingerprint.length - 2] & 0xff) << 8)
                            | ((fingerprint[fingerprint.length - 1] & 0xff));
            
            if (key instanceof RSAPublicBCPGKey)
            {
                this.keyStrength = ((RSAPublicBCPGKey)key).getModulus().bitLength();
            }
            else if (key instanceof DSAPublicBCPGKey)
            {
                this.keyStrength = ((DSAPublicBCPGKey)key).getP().bitLength();
            }
            else if (key instanceof ElGamalPublicBCPGKey)
            {
                this.keyStrength = ((ElGamalPublicBCPGKey)key).getP().bitLength();
            }
        }
    }
    
    /**
     * Create a PGPPublicKey from the passed in JCA one.
     * <p>
     * Note: the time passed in affects the value of the key's keyID, so you probably only want
     * to do this once for a JCA key, or make sure you keep track of the time you used.
     * 
     * @param algorithm
     * @param pubKey
     * @param time
     * @param provider
     * @throws PGPException
     * @throws NoSuchProviderException
     */
    public PGPPublicKey(        
        int            algorithm,
        PublicKey      pubKey,
        Date           time,
        String         provider) 
        throws PGPException, NoSuchProviderException
    {
        PublicKeyPacket  pubPk;

        if (pubKey instanceof RSAPublicKey)
        {
            RSAPublicKey    rK = (RSAPublicKey)pubKey;
            
            pubPk = new PublicKeyPacket(algorithm, time, new RSAPublicBCPGKey(rK.getModulus(), rK.getPublicExponent()));
        }
        else if (pubKey instanceof DSAPublicKey)
        {
            DSAPublicKey    dK = (DSAPublicKey)pubKey;
            DSAParams       dP = dK.getParams();
            
            pubPk = new PublicKeyPacket(algorithm, time, new DSAPublicBCPGKey(dP.getP(), dP.getQ(), dP.getG(),  dK.getY()));
        }
        else if (pubKey instanceof ElGamalPublicKey)
        {
            ElGamalPublicKey        eK = (ElGamalPublicKey)pubKey;
            ElGamalParameterSpec    eS = eK.getParameters();
            
            pubPk = new PublicKeyPacket(algorithm, time, new ElGamalPublicBCPGKey(eS.getP(), eS.getG(), eK.getY()));
        }
        else
        {
            throw new PGPException("unknown key class");
        }
        
        this.publicPk = pubPk;
        this.ids = new ArrayList();
        this.idSigs = new ArrayList();
        
        try
        {
            init();
        }
        catch (IOException e)
        {
            throw new PGPException("exception calculating keyID", e);
        }
    }
    
    /**
      * Constructor for a sub-key.
      * 
      * @param pk
      * @param sha
      * @param sig
      */
    PGPPublicKey(
        PublicKeyPacket publicPk, 
        TrustPacket     trustPk, 
        ArrayList       sigs)
        throws IOException
     {
        this.publicPk = publicPk;
        this.trustPk = trustPk;
        this.subSigs = sigs;
        
        init();
     }
     
    /**
     * @param key
     * @param trust
     * @param subSigs
     */
    PGPPublicKey(
        PGPPublicKey key,
        TrustPacket trust, 
        ArrayList subSigs)
    {
        this.publicPk = key.publicPk;
        this.trustPk = trust;
        this.subSigs = subSigs;
                
        this.fingerprint = key.fingerprint;
        this.keyID = key.keyID;
        this.keyStrength = key.keyStrength;
    }
    
    /**
     * Copy constructor.
     * @param pubKey
     */
    PGPPublicKey(
        PGPPublicKey    pubKey)
     {
        this.publicPk = pubKey.publicPk;
        
        this.keySigs = new ArrayList(pubKey.keySigs);
        this.ids = new ArrayList(pubKey.ids);
        this.idTrusts = new ArrayList(pubKey.idTrusts);
        this.idSigs = new ArrayList(pubKey.idSigs.size());
        for (int i = 0; i != pubKey.idSigs.size(); i++)
        {
            this.idSigs.add(new ArrayList((ArrayList)pubKey.idSigs.get(i)));
        }
       
        if (pubKey.subSigs != null)
        {
            this.subSigs = new ArrayList(pubKey.subSigs.size());
            for (int i = 0; i != pubKey.subSigs.size(); i++)
            {
                this.subSigs.add(pubKey.subSigs.get(i));
            }
        }
        
        this.fingerprint = pubKey.fingerprint;
        this.keyID = pubKey.keyID;
        this.keyStrength = pubKey.keyStrength;
     }

    PGPPublicKey(
        PublicKeyPacket publicPk,
        TrustPacket     trustPk,
        ArrayList       keySigs,
        ArrayList       ids,
        ArrayList       idTrusts,
        ArrayList       idSigs)
        throws IOException
    {
        this.publicPk = publicPk;
        this.trustPk = trustPk;
        this.keySigs = keySigs;
        this.ids = ids;
        this.idTrusts = idTrusts;
        this.idSigs = idSigs;
    
        init();
    }
    
    PGPPublicKey(
        PublicKeyPacket  publicPk,
        ArrayList        ids,
        ArrayList        idSigs)
        throws IOException
    {
        this.publicPk = publicPk;
        this.ids = ids;
        this.idSigs = idSigs;

        init();
    }
    
    /**
     * @return the version of this key.
     */
    public int getVersion()
    {
        return publicPk.getVersion();
    }
    
    /**
     * @return creation time of key.
     */
    public Date getCreationTime()
    {
        return publicPk.getTime();
    }
    
    /**
     * @return number of valid days from creation time - zero means no
     * expiry.
     */
    public int getValidDays()
    {
        return publicPk.getValidDays();
    }
    
    /**
     * Return the keyID associated with the public key.
     * 
     * @return long
     */
    public long getKeyID()
    {
        return keyID;
    }
    
    /**
     * Return the fingerprint of the key.
     * 
     * @return key fingerprint.
     */
    public byte[] getFingerprint()
    {
        byte[]    tmp = new byte[fingerprint.length];
        
        System.arraycopy(fingerprint, 0, tmp, 0, tmp.length);
        
        return tmp;
    }
    
    /**
     * return true if this key is marked as suitable for encryption.
     */
    public boolean isEncryptionKey()
    {
        int algorithm = publicPk.getAlgorithm();

        return ((algorithm == RSA_GENERAL) || (algorithm == RSA_ENCRYPT)
                || (algorithm == ELGAMAL_ENCRYPT) || (algorithm == ELGAMAL_GENERAL));
    }

    /**
     * Return true if this is a master key.
     * @return true if a master key.
     */
    public boolean isMasterKey()
    {
        return (subSigs == null);
    }
    
    /**
     * Return the algorithm code associated with the public key.
     * 
     * @return int
     */
    public int getAlgorithm()
    {
        return publicPk.getAlgorithm();
    }
    
    /**
     * Return the strength of the key in bits.
     * 
     * @return bit strenght of key.
     */
    public int getBitStrength()
    {
        return keyStrength;
    }
    
    /**
     * Return the public key contained in the object.
     * 
     * @param provider provider to construct the key for.
     * 
     * @return PublicKey
     * @throws PGPException
     * @throws NoSuchProviderException
     */
    public PublicKey getKey(
        String                provider)
        throws PGPException, NoSuchProviderException
    {
        KeyFactory                        fact;
        
        try
        {
            switch (publicPk.getAlgorithm())
            {
            case RSA_ENCRYPT:
            case RSA_GENERAL:
            case RSA_SIGN:
                RSAPublicBCPGKey    rsaK = (RSAPublicBCPGKey)publicPk.getKey();
                RSAPublicKeySpec    rsaSpec = new RSAPublicKeySpec(rsaK.getModulus(), rsaK.getPublicExponent());
    
                fact = KeyFactory.getInstance("RSA", provider);
                
                return fact.generatePublic(rsaSpec);
            case DSA:
                DSAPublicBCPGKey    dsaK = (DSAPublicBCPGKey)publicPk.getKey();
                DSAPublicKeySpec    dsaSpec = new DSAPublicKeySpec(dsaK.getY(), dsaK.getP(), dsaK.getQ(), dsaK.getG());
            
                fact = KeyFactory.getInstance("DSA", provider);
                
                return fact.generatePublic(dsaSpec);
            case ELGAMAL_ENCRYPT:
            case ELGAMAL_GENERAL:
                ElGamalPublicBCPGKey    elK = (ElGamalPublicBCPGKey)publicPk.getKey();
                ElGamalPublicKeySpec    elSpec = new ElGamalPublicKeySpec(elK.getY(), new ElGamalParameterSpec(elK.getP(), elK.getG()));
                
                fact = KeyFactory.getInstance("ElGamal", provider);
                
                return fact.generatePublic(elSpec);
            default:
                throw new PGPException("unknown public key algorithm encountered");
            }
        }
        catch (PGPException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new PGPException("exception constructing public key", e);
        }
    }
    
    /**
     * Return any userIDs associated with the key.
     * 
     * @return an iterator of Strings.
     */
    public Iterator getUserIDs()
    {
        ArrayList    temp = new ArrayList();
        
        for (int i = 0; i != ids.size(); i++)
        {
            if (ids.get(i) instanceof String)
            {
                temp.add(ids.get(i));
            }
        }
        
        return temp.iterator();
    }
    
    /**
     * Return any user attribute vectors associated with the key.
     * 
     * @return an iterator of PGPUserAttributeSubpacketVector objects.
     */
    public Iterator getUserAttributes()
    {
        ArrayList    temp = new ArrayList();
        
        for (int i = 0; i != ids.size(); i++)
        {
            if (ids.get(i) instanceof PGPUserAttributeSubpacketVector)
            {
                temp.add(ids.get(i));
            }
        }
        
        return temp.iterator();
    }
    
    /**
     * Return any signatures associated with the passed in id.
     * 
     * @param id the id to be matched.
     * @return an iterator of PGPSignature objects.
     */
    public Iterator getSignaturesForID(
        String   id)
    {
        for (int i = 0; i != ids.size(); i++)
        {
            if (id.equals(ids.get(i)))
            {
                return ((ArrayList)idSigs.get(i)).iterator();
            }
        }
        
        return null;
    }
    
    /**
     * Return an iterator of signatures associated with the passed in user attributes.
     * 
     * @param userAttributes the vector of user attributes to be matched.
     * @return an iterator of PGPSignature objects.
     */
    public Iterator getSignaturesForUserAttribute(
        PGPUserAttributeSubpacketVector    userAttributes)
    {
        for (int i = 0; i != ids.size(); i++)
        {
            if (userAttributes.equals(ids.get(i)))
            {
                return ((ArrayList)idSigs.get(i)).iterator();
            }
        }
        
        return null;
    }
    
    /**
     * Return signatures of the passed in type that are on this key.
     * 
     * @param signatureType the type of the signature to be returned.
     * @return an iterator (possibly empty) of signatures of the given type.
     */
    public Iterator getSignaturesOfType(
        int signatureType)
    {
        List        l = new ArrayList();
        Iterator    it = this.getSignatures();
        
        while (it.hasNext())
        {
            PGPSignature    sig = (PGPSignature)it.next();
            
            if (sig.getSignatureType() == signatureType)
            {
                l.add(sig);
            }
        }
        
        return l.iterator();
    }
    
    /**
     * Return all signatures/certifications associated with this key.
     * 
     * @return an iterator (possibly empty) with all signatures/certifications.
     */
    public Iterator getSignatures()
    {
        if (subSigs == null)
        {
            ArrayList sigs = new ArrayList();

            sigs.addAll(keySigs);

            for (int i = 0; i != idSigs.size(); i++)
            {
                sigs.addAll((Collection)idSigs.get(i));
            }
            
            return sigs.iterator();
        }
        else
        {
            return subSigs.iterator();
        }
    }
    
    public byte[] getEncoded() 
        throws IOException
    {
        ByteArrayOutputStream    bOut = new ByteArrayOutputStream();
        
        this.encode(bOut);
        
        return bOut.toByteArray();
    }
    
    public void encode(
        OutputStream    outStream) 
        throws IOException
    {
        BCPGOutputStream    out;
        
        if (outStream instanceof BCPGOutputStream)
        {
            out = (BCPGOutputStream)outStream;
        }
        else
        {
            out = new BCPGOutputStream(outStream);
        }
        
        out.writePacket(publicPk);
        if (trustPk != null)
        {
            out.writePacket(trustPk);
        }
        
        if (subSigs == null)    // not a sub-key
        {
            for (int i = 0; i != keySigs.size(); i++)
            {
                ((PGPSignature)keySigs.get(i)).encode(out);
            }
            
            for (int i = 0; i != ids.size(); i++)
            {
                if (ids.get(i) instanceof String)
                {
                    String    id = (String)ids.get(i);
                    
                    out.writePacket(new UserIDPacket(id));
                }
                else
                {
                    PGPUserAttributeSubpacketVector    v = (PGPUserAttributeSubpacketVector)ids.get(i);

                    out.writePacket(new UserAttributePacket(v.toSubpacketArray()));
                }
                
                if (idTrusts.get(i) != null)
                {
                    out.writePacket((ContainedPacket)idTrusts.get(i));
                }
                
                ArrayList    sigs = (ArrayList)idSigs.get(i);
                for (int j = 0; j != sigs.size(); j++)
                {
                    ((PGPSignature)sigs.get(j)).encode(out);
                }
            }
        }
        else
        {
            for (int j = 0; j != subSigs.size(); j++)
            {
                ((PGPSignature)subSigs.get(j)).encode(out);
            }
        }
    }
    
    /**
     * Add a certification to the given public key.
     * 
     * @param key the key the certification is to be added to.
     * @param id the id the certification is associated with.
     * @param certification the new certification.
     * @return the re-certified key.
     */
    public static PGPPublicKey addCertification(
        PGPPublicKey    key,
        String          id,
        PGPSignature    certification)
    {
        PGPPublicKey    returnKey = new PGPPublicKey(key);
        
        Iterator    idIt = returnKey.getUserIDs();
        ArrayList    sigList = null;
        
        for (int i = 0; i != returnKey.ids.size(); i++)
        {
            if (id.equals(returnKey.ids.get(i)))
            {
                sigList = (ArrayList)returnKey.idSigs.get(i);
            }
        }
        
        if (sigList != null)
        {
            sigList.add(certification);
        }
        else
        {
            sigList = new ArrayList();
            
            sigList.add(certification);
            returnKey.ids.add(id);
            returnKey.idTrusts.add(null);
            returnKey.idSigs.add(sigList);
        }
        
        return returnKey;
    }
    
    /**
     * Remove any certifications associated with a given id on a key.
     * 
     * @param key the key the certifications are to be removed from.
     * @param id the id that is to be removed.
     * @return the re-certified key, null if the id was not found on the key.
     */
    public static PGPPublicKey removeCertification(
        PGPPublicKey    key,
        String          id)
    {
        PGPPublicKey    returnKey = new PGPPublicKey(key);
        Iterator        idIt = returnKey.getUserIDs();
        ArrayList       sigList = null;
        boolean         found = false;
        
        for (int i = 0; i < returnKey.ids.size(); i++)
        {
            if (id.equals(returnKey.ids.get(i)))
            {
                found = true;
                returnKey.ids.remove(i);
                returnKey.idTrusts.remove(i);
                returnKey.idSigs.remove(i);
            }
        }
        
        if (!found)
        {
            return null;
        }
        
        return returnKey;
    }
    
    /**
     * Remove any certifications associated with a given id on a key.
     * 
     * @param key the key the certifications are to be removed from.
     * @param id the id that the certfication is to be removed from.
     * @param certification the certfication to be removed.
     * @return the re-certified key, null if the certification was not found.
     */
    public static PGPPublicKey removeCertification(
        PGPPublicKey    key,
        String          id,
        PGPSignature    certification)
    {
        PGPPublicKey    returnKey = new PGPPublicKey(key);
        
        Iterator    idIt = returnKey.getUserIDs();
        ArrayList   sigList = null;
        boolean     found = false;
        
        for (int i = 0; i < returnKey.ids.size(); i++)
        {
            if (id.equals(returnKey.ids.get(i)))
            {
                found = ((ArrayList)returnKey.idSigs.get(i)).remove(certification);
            }
        }
        
        if (!found)
        {
            return null;
        }
        
        return returnKey;
    }
    
    /**
     * Add a revocation or some other key certification to a key.
     * 
     * @param key the key the revocation is to be added to.
     * @param certification the key signature to be added.
     * @return the new changed public key object.
     */
    public static PGPPublicKey addCertification(
        PGPPublicKey    key,
        PGPSignature    certification)
    {
        if (key.isMasterKey())
        {
            if (certification.getSignatureType() == PGPSignature.SUBKEY_REVOCATION)
            {
                throw new IllegalArgumentException("signature type incorrect for master key revocation.");
            }
        }
        else
        {
            if (certification.getSignatureType() == PGPSignature.KEY_REVOCATION)
            {
                throw new IllegalArgumentException("signature type incorrect for sub-key revocation.");
            }
        }

        PGPPublicKey    returnKey = new PGPPublicKey(key);
        
        if (returnKey.subSigs != null)
        {
            returnKey.subSigs.add(certification);
        }
        else
        {
            returnKey.keySigs.add(certification);
        }
        
        return returnKey;
    }
}
