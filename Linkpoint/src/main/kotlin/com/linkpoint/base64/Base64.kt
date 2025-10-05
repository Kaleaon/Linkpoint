/*
 * Clean Base64 implementation for Linkpoint
 * Replaces problematic decompiled code with working implementation
 */
package com.linkpoint.base64

/**
 * Base64 encoding/decoding utility using Android's built-in Base64 implementation
 */
object Base64 {
    
    /**
     * Decode a Base64 encoded string to bytes
     */
    @JvmStatic
    fun decode(input: String?): ByteArray {
        if (input == null) {
            return ByteArray(0)
        }
        return android.util.Base64.decode(input, android.util.Base64.DEFAULT)
    }
    
    /**
     * Encode bytes to a Base64 string
     */
    @JvmStatic
    fun encode(input: ByteArray?): String {
        if (input == null) {
            return ""
        }
        return android.util.Base64.encodeToString(input, android.util.Base64.DEFAULT)
    }
}