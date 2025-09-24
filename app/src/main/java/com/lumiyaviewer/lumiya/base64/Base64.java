/*
 * Clean Base64 implementation for Linkpoint
 * Replaces problematic decompiled code with working implementation
 */
package com.lumiyaviewer.lumiya.base64;

/**
 * Base64 encoding/decoding utility using Android's built-in Base64 implementation
 */
public class Base64 {
    
    /**
     * Decode a Base64 encoded string to bytes
     */
    public static byte[] decode(String input) {
        if (input == null) {
            return new byte[0];
        }
        return android.util.Base64.decode(input, android.util.Base64.DEFAULT);
    }
    
    /**
     * Encode bytes to a Base64 string
     */
    public static String encode(byte[] input) {
        if (input == null) {
            return "";
        }
        return android.util.Base64.encodeToString(input, android.util.Base64.DEFAULT);
    }
}
