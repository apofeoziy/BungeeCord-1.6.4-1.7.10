package net.md_5.bungee;

import lombok.Getter;
import net.md_5.bungee.protocol.packet.PacketFCEncryptionResponse;
import net.md_5.bungee.protocol.packet.PacketFDEncryptionRequest;
import net.md_5.bungee.protocol.packet.protocolhack.PacketEncryptionRequest;
import net.md_5.bungee.protocol.packet.protocolhack.PacketEncryptionResponse;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

/**
 * Class containing all encryption related methods for the proxy.
 */
public class EncryptionUtil
{

    private static final Random random = new Random();
    public static KeyPair keys;
    @Getter
    private static SecretKey secret = new SecretKeySpec( new byte[ 16 ], "AES" );

    static
    {
        try
        {
            keys = KeyPairGenerator.getInstance( "RSA" ).generateKeyPair();
        } catch ( NoSuchAlgorithmException ex )
        {
            throw new ExceptionInInitializerError( ex );
        }
    }

    public static PacketFDEncryptionRequest encryptRequest164(boolean onlinemode)
    {
        String hash = ( onlinemode ) ? Long.toString( random.nextLong(), 16 ) : "-";
        byte[] pubKey = keys.getPublic().getEncoded();
        byte[] verify = new byte[ 4 ];
        random.nextBytes(verify);
        return new PacketFDEncryptionRequest( hash, pubKey, verify );
    }

    public static PacketEncryptionRequest encryptRequest172(boolean onlinemode)
    {
        String hash = ( onlinemode ) ? Long.toString( random.nextLong(), 16 ) : "-";
        byte[] pubKey = keys.getPublic().getEncoded();
        byte[] verify = new byte[ 4 ];
        random.nextBytes(verify);
        return new PacketEncryptionRequest( hash, pubKey, verify );
    }

    public static SecretKey getSecret(PacketFCEncryptionResponse resp, PacketFDEncryptionRequest request) throws GeneralSecurityException
    {
        Cipher cipher = Cipher.getInstance( "RSA" );
        cipher.init( Cipher.DECRYPT_MODE, keys.getPrivate() );
        byte[] decrypted = cipher.doFinal( resp.getVerifyToken() );

        if ( !Arrays.equals( request.getVerifyToken(), decrypted ) )
        {
            throw new IllegalStateException( "Key pairs do not match!" );
        }

        cipher.init( Cipher.DECRYPT_MODE, keys.getPrivate() );
        return new SecretKeySpec( cipher.doFinal( resp.getSharedSecret() ), "AES" );
    }
    public static SecretKey getSecret(PacketEncryptionResponse resp, PacketEncryptionRequest request) throws GeneralSecurityException
    {
        Cipher cipher = Cipher.getInstance( "RSA" );
        cipher.init( Cipher.DECRYPT_MODE, keys.getPrivate() );
        byte[] decrypted = cipher.doFinal( resp.getVerifyToken() );

        if ( !Arrays.equals( request.getVerifyToken(), decrypted ) )
        {
            throw new IllegalStateException( "Key pairs do not match!" );
        }

        cipher.init( Cipher.DECRYPT_MODE, keys.getPrivate() );
        return new SecretKeySpec( cipher.doFinal( resp.getSharedSecret() ), "AES" );
    }

    public static Cipher getCipher(int opMode, Key shared) throws GeneralSecurityException
    {
        Cipher cip = Cipher.getInstance( "AES/CFB8/NoPadding" );
        cip.init( opMode, shared, new IvParameterSpec( shared.getEncoded() ) );
        return cip;
    }

    public static PublicKey getPubkey(PacketFDEncryptionRequest request) throws GeneralSecurityException
    {
        return KeyFactory.getInstance( "RSA" ).generatePublic( new X509EncodedKeySpec( request.getPublicKey() ) );
    }

    public static PublicKey getPubkey(PacketEncryptionRequest request) throws GeneralSecurityException
    {
        return KeyFactory.getInstance( "RSA" ).generatePublic( new X509EncodedKeySpec( request.getPublicKey() ) );
    }


    public static byte[] encrypt(Key key, byte[] b) throws GeneralSecurityException
    {
        Cipher hasher = Cipher.getInstance( "RSA" );
        hasher.init( Cipher.ENCRYPT_MODE, key );
        return hasher.doFinal( b );
    }

}
