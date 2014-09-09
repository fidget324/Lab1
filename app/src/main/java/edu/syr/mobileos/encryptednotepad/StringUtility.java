package edu.syr.mobileos.encryptednotepad;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * Created by scottconstable on 9/9/14.
 */
public class StringUtility {

    public static final String ENCODER = "ISO-8859-1";

    // useful for debugging
    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    public static String bin2String(byte[] data) {
        String encoded_data = null;

        try {
            encoded_data = new String(data, ENCODER);
        } catch (UnsupportedEncodingException e) {
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
