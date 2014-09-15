package edu.syr.mobileos.encryptednotepad;

import android.util.Log;

import junit.framework.Assert;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Crypto library to support SHA256, HMAC_SHA256, and AES256
 */
public class Crypto {

    /**
     * offer a 16-byte IV for each note, since the block size in AES is 16 bytes
     */
    public static byte[] getIV() {
        int length = 16;
        byte[] buf = new byte[length];
        new Random().nextBytes(buf);
        return buf;
    }

    /**
     * Performs SHA256 on a string
     *
     * @param s The string to hash
     * @return A byte array containing the resulting 32-byte hash
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
     *
     * @param key  the 32-byte key
     * @param data the string to encrypt
     * @return the encrypted string
     */
    public static String aes256_enc(byte[] key, String data, byte[] pIV) {

        Assert.assertEquals(32, key.length); // 32 bytes = 256-bit key

        // Create Cipher using "AES" provider
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CFB/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            Log.d("debug", e.toString());
        } catch (NoSuchPaddingException e) {
            Log.d("debug", e.toString());
        }

        if (cipher != null) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(pIV));//.getBytes(ENCODER)));
                byte[] encrypted_data = cipher.doFinal(data.getBytes(StringUtility.ENCODER));
                return StringUtility.bin2String(encrypted_data);
            } catch (Exception e) {
                Log.d("debug", e.toString());
            }
        }

        return null;
    }

    /**
     * Decrypt a string using AES256
     *
     * @param key  the 32-byte key
     * @param data the string to decrypt
     * @return the decrypted string
     */
    public static String aes256_dec(byte[] key, String data, byte[] pIV) {

        Assert.assertEquals(32, key.length); // 32 bytes = 256-bit key

        // Create Cipher using "AES" provider
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CFB/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            Log.d("debug", e.toString());
        } catch (NoSuchPaddingException e) {
            Log.d("debug", e.toString());
        }

        if (cipher != null) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(pIV));//.getBytes(ENCODER)));
                byte[] decrypted_data = cipher.doFinal(data.getBytes(StringUtility.ENCODER));
                return StringUtility.bin2String(decrypted_data);
            } catch (Exception e) {
                Log.d("debug", e.toString());
            }
        }

        return null;
    }


    /**
     * HMAC-SHA256 encoding
     *
     * @param key  the 32-byte key to be HMAC
     * @param data the string to HMAC the key
     * @return the hmac string
     */
    public static String hmac_sha256(byte[] key, String data) {
        Assert.assertEquals(32, key.length); // 32 bytes = 256-bit key
        Mac mac = null;
        String digestString = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            Log.d("debug", e.toString());
        }
        if (mac != null) {
            try {
                mac.init(new SecretKeySpec(key, "HmacSHA256"));
                byte[] digest = mac.doFinal(data.getBytes(StringUtility.ENCODER));
                digestString = StringUtility.bin2String(digest);

            } catch (Exception e) {
                Log.d("debug", e.toString());
            }
        }
        return digestString;
    }
}


