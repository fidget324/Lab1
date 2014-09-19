package edu.syr.mobileos.encryptednotepad;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * Utility for encoding/decoding data using Strings
 */
public class StringUtility {

    // This encoder defines a bijection between bytes and characters
    public static final String ENCODER = "ISO-8859-1";

    /**
     * Converts a byte array to its hexadecimal representation in String form. Useful for debugging
     * @param data      byte[] of data to convert
     * @return          hex of input
     */
    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    /**
     * Converts a byte array to a String
     * @param data      byte[] of data to encode
     * @return          encoded data
     */
    public static String bin2String(byte[] data) {
        String encoded_data = null;

        try {
            encoded_data = new String(data, ENCODER);
        } catch (UnsupportedEncodingException e) {
            Log.d("debug", e.toString());
        }

        return encoded_data;
    }

    /**
     * Decode a String into a byte array
     * @param s     String to decode
     * @return      Decoded byte array
     */
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
