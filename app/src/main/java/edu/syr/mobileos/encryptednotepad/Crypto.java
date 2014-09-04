package edu.syr.mobileos.encryptednotepad;

import android.util.Log;

import junit.framework.Assert;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Crypto library to support SHA256, HMAC_SHA256, and AES256
 */
public class Crypto {

    private static final String sIV = "0000000000000000";

    private static final String ENCODER = "ISO-8859-1";

    /**
     * Performs SHA256 on a string
     * @param s     The string to hash
     * @return      A byte array containing the resulting 32-byte hash
     */
    public static byte[] sha256(String s) {
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }

        if (digest != null) {
            digest.reset();
            return digest.digest(s.getBytes());
        }

        return null;
    }

    /**
     * Encrypt a string using AES256
     * @param key   the 32-byte key
     * @param data  the string to encrypt
     * @return      the encrypted string
     */
    public static String aes256_enc(byte[] key, String data) {

        Assert.assertEquals(32, key.length); // 32 bytes = 256-bit key

        // Create Cipher using "AES" provider
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/OFB/NoPadding");
        }
        catch (NoSuchAlgorithmException e) {
            Log.d("debug", e.toString());
        }
        catch (NoSuchPaddingException e) {
            Log.d("debug", e.toString());
        }

        if (cipher != null) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(sIV.getBytes(ENCODER)));
                byte[] encrypted_data = cipher.doFinal(data.getBytes(ENCODER));
                return bin2String(encrypted_data);
            }
            catch (Exception e) {
                Log.d("debug", e.toString());
            }
        }

        return null;
    }

    /**
     * Decrypt a string using AES256
     * @param key   the 32-byte key
     * @param data  the string to decrypt
     * @return      the decrypted string
     */
    public static String aes256_dec(byte[] key, String data) {

        Assert.assertEquals(32, key.length); // 32 bytes = 256-bit key

        // Create Cipher using "AES" provider
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/OFB/NoPadding");
        }
        catch (NoSuchAlgorithmException e) {
            Log.d("debug", e.toString());
        }
        catch (NoSuchPaddingException e) {
            Log.d("debug", e.toString());
        }

        if (cipher != null) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(sIV.getBytes(ENCODER)));
                byte [] decrypted_data = cipher.doFinal(data.getBytes(ENCODER));
                return bin2String(decrypted_data);
            }
            catch (Exception e) {
                Log.d("debug", e.toString());
            }
        }

        return null;
    }

    // useful for debugging
    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
    }

    public static String bin2String(byte[] data) {
        String encoded_data = null;

        try {
            encoded_data = new String(data, ENCODER);
        }
        catch (UnsupportedEncodingException e) {
            Log.d("debug", e.toString());
        }

        return encoded_data;
    }

    public static byte[] string2Bin(String s) {
        byte[] encoded_data = null;

        try {
            encoded_data = s.getBytes(ENCODER);
        }
        catch (UnsupportedEncodingException e) {
            Log.d("debug", e.toString());
        }

        return encoded_data;
    }
}
