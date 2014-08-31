package edu.syr.mobileos.encryptednotepad;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by scottconstable on 8/31/14.
 */
public class Crypto {

    public static byte[] sha256(String s) {
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (digest != null) {
            digest.reset();
            return digest.digest(s.getBytes());
        }

        return null;
    }

    // useful for debugging
    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
    }
}
